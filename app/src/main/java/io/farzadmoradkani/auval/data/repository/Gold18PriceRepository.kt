package io.farzadmoradkani.auval.data.repository

import io.farzadmoradkani.auval.ui.settings.GoldPriceSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToLong

data class GoldPriceState(
    val pricePerGram: Long = 0L,
    val source: GoldPriceSource = GoldPriceSource.TGJU,
    val lastUpdated: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class Gold18PriceRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow(GoldPriceState())
    val state: StateFlow<GoldPriceState> = _state.asStateFlow()

    private val requestCounter = AtomicLong(0L)

    fun selectSource(source: GoldPriceSource) {
        _state.update {
            it.copy(
                source = source,
                pricePerGram = 0L,
                lastUpdated = null,
                error = null
            )
        }
        refresh()
    }

    fun refresh() {
        val source = _state.value.source
        val request = requestCounter.incrementAndGet()

        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            val result = fetchPrice(source)
            if (request != requestCounter.get()) return@launch

            result
                .onSuccess { pricePerGram ->
                    _state.update {
                        it.copy(
                            pricePerGram = pricePerGram,
                            lastUpdated = System.currentTimeMillis(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    suspend fun testSource(source: GoldPriceSource): Result<Long> = fetchPrice(source)

    suspend fun fetchPrice(source: GoldPriceSource): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            val document = fetchDocument(source.url)

            val mesghalPrice = parseMesghalPrice(document, source)
            require(mesghalPrice > 0) { "Gold price was not found" }
            mesghalToGram(mesghalPrice, source)
        }
    }

    private fun fetchDocument(url: String): Document {
        var currentUrl = url
        val cookies = mutableMapOf<String, String>()

        repeat(MAX_REDIRECTS) {
            val response = Jsoup.connect(currentUrl)
                .userAgent(USER_AGENT)
                .timeout(15_000)
                .followRedirects(false)
                .cookies(cookies)
                .execute()

            val status = response.statusCode()
            if (status in 300..399) {
                cookies.putAll(response.cookies())
                val location = response.header("Location")
                    ?: error("Redirect without Location from $currentUrl")
                currentUrl = URL(URL(currentUrl), location).toString()
                return@repeat
            }

            if (status !in 200..299) {
                error("HTTP $status from $currentUrl")
            }

            return response.parse()
        }

        error("Too many redirects for $url")
    }

    private val GoldPriceSource.url: String
        get() = when (this) {
            GoldPriceSource.IRANJIB -> "https://www.iranjib.ir/showgroup/23/realtime_price/"
            GoldPriceSource.TALASEA -> "https://talasea.ir/gold-price"
            GoldPriceSource.TGJU -> "https://www.tgju.org/"
        }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Mobile Safari/537.36"
        private const val MAX_REDIRECTS = 5
    }
}

private const val MESGHAL_TO_GRAM = 4.3

internal fun mesghalToGram(mesghalPrice: Long, source: GoldPriceSource): Long {
    // Iranjib and TGJU publish the mesghal price in Rial; Talasea publishes it in Toman.
    val priceInToman = when (source) {
        GoldPriceSource.IRANJIB, GoldPriceSource.TGJU -> mesghalPrice / 10.0
        GoldPriceSource.TALASEA -> mesghalPrice.toDouble()
    }
    return (priceInToman / MESGHAL_TO_GRAM).roundToLong()
}

internal fun parseMesghalPrice(document: Document, source: GoldPriceSource): Long =
    when (source) {
        GoldPriceSource.IRANJIB -> parseIranjib(document)
        GoldPriceSource.TALASEA -> parseTalasea(document)
        GoldPriceSource.TGJU -> parseTgju(document)
    }

private fun parseIranjib(document: Document): Long {
    val row = document.select("tr").firstOrNull { row ->
        row.selectFirst("a.tts")?.text()?.contains("مثقال") == true
    } ?: error("Iranjib mesghal row was not found")

    val priceText = row.selectFirst("span.lastprice")?.text()
        ?: row.selectFirst("td[id$=_pr]")?.text()
        ?: error("Iranjib mesghal price was not found")

    return parsePrice(priceText)
}

private fun parseTalasea(document: Document): Long {
    val row = document.select("tr").firstOrNull { row ->
        row.selectFirst("a[href*=mesghal]") != null
    } ?: error("Talasea mesghal row was not found")

    val priceText = row.select("td").getOrNull(1)?.text()
        ?: error("Talasea mesghal price was not found")

    return parsePrice(priceText)
}

private fun parseTgju(document: Document): Long {
    val row = document.selectFirst("tr[data-market-nameslug=mesghal]")
        ?: document.select("tr").firstOrNull { row ->
            row.selectFirst("th")?.text()?.contains("مثقال") == true
        }
        ?: error("TGJU mesghal row was not found")

    val priceText = row.attr("data-price")
        .ifBlank { row.selectFirst("td")?.text().orEmpty() }
        .ifBlank { error("TGJU mesghal price was not found") }

    return parsePrice(priceText)
}

private fun parsePrice(raw: String): Long {
    val normalized = normalizeDigits(raw).filter { it.isDigit() }
    return normalized.toLongOrNull() ?: error("Invalid price: $raw")
}

private fun normalizeDigits(value: String): String = buildString(value.length) {
    value.forEach { character ->
        val digit = when (character) {
            in '۰'..'۹' -> character - '۰'
            in '٠'..'٩' -> character - '٠'
            else -> null
        }
        if (digit != null) {
            append('0' + digit)
        } else {
            append(character)
        }
    }
}

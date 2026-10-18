package io.farzadmoradkani.auval.data.repository

import io.farzadmoradkani.auval.ui.settings.GoldPriceSource
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class Gold18PriceParserTest {

    @Test
    fun parsesIranjibMesghalPrice() {
        val html = """
            <table>
              <tr>
                <td><a class="tts">هر مثقال طلا در بازار تهران</a></td>
                <td id="f_84_63_pr"><span class="lastprice">۱,۱۱۹,۴۰۰,۰۰۰</span></td>
              </tr>
            </table>
        """.trimIndent()

        assertEquals(
            1_119_400_000L,
            parseMesghalPrice(Jsoup.parse(html), GoldPriceSource.IRANJIB)
        )
    }

    @Test
    fun parsesTalaseaMesghalPrice() {
        val html = """
            <table>
              <tr>
                <td><a href="https://talasea.ir/mesghal">۱ مثقال طلا</a></td>
                <td><span>112,401,546<span>تومان</span></span></td>
              </tr>
            </table>
        """.trimIndent()

        assertEquals(
            112_401_546L,
            parseMesghalPrice(Jsoup.parse(html), GoldPriceSource.TALASEA)
        )
    }

    @Test
    fun parsesTgjuMesghalPrice() {
        val html = """
            <table>
              <tr data-market-nameslug="mesghal" data-price="1,122,520,000">
                <th>مثقال طلا</th>
                <td>1,121,670,000</td>
              </tr>
            </table>
        """.trimIndent()

        assertEquals(
            1_122_520_000L,
            parseMesghalPrice(Jsoup.parse(html), GoldPriceSource.TGJU)
        )
    }

    @Test
    fun convertsIranjibRialMesghalToTomanGrams() {
        assertEquals(26_000_000L, mesghalToGram(1_118_000_000L, GoldPriceSource.IRANJIB))
    }

    @Test
    fun convertsTgjuRialMesghalToTomanGrams() {
        assertEquals(26_000_000L, mesghalToGram(1_118_000_000L, GoldPriceSource.TGJU))
    }

    @Test
    fun convertsTalaseaTomanMesghalToGrams() {
        assertEquals(26_139_535L, mesghalToGram(112_400_000L, GoldPriceSource.TALASEA))
    }
}

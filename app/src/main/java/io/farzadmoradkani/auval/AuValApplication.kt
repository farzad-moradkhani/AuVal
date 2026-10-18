package io.farzadmoradkani.auval

import android.app.Application
import io.farzadmoradkani.auval.data.local.AuValDatabase
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository

class AuValApplication : Application() {

    val database: AuValDatabase by lazy {
        AuValDatabase.create(this)
    }

    val goldEntryRepository: GoldEntryRepository by lazy {
        GoldEntryRepository(database.goldEntryDao())
    }

    val goldPriceRepository: Gold18PriceRepository by lazy {
        Gold18PriceRepository()
    }
}

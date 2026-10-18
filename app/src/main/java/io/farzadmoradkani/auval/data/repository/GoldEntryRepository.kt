package io.farzadmoradkani.auval.data.repository

import io.farzadmoradkani.auval.data.local.GoldEntryDao
import io.farzadmoradkani.auval.data.local.GoldEntryEntity
import kotlinx.coroutines.flow.Flow

class GoldEntryRepository(private val dao: GoldEntryDao) {

    val entries: Flow<List<GoldEntryEntity>> = dao.observeAll()

    suspend fun add(entry: GoldEntryEntity) {
        dao.insert(entry)
    }

    suspend fun remove(id: Long) {
        dao.deleteById(id)
    }
}

package io.farzadmoradkani.auval.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoldEntryDao {

    @Query("SELECT * FROM gold_entries ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<GoldEntryEntity>>

    @Insert
    suspend fun insert(entry: GoldEntryEntity): Long

    @Query("DELETE FROM gold_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}

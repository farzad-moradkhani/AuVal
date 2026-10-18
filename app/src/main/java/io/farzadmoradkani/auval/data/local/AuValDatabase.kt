package io.farzadmoradkani.auval.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [GoldEntryEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AuValDatabase : RoomDatabase() {

    abstract fun goldEntryDao(): GoldEntryDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `gold_entries_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `addedAt` INTEGER NOT NULL,
                        `weightGrams` REAL NOT NULL,
                        `purchaseValueToman` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `gold_entries_new` (`id`, `addedAt`, `weightGrams`, `purchaseValueToman`)
                    SELECT `id`, `addedAt`, `weightGrams`, `purchaseValueToman` FROM `gold_entries`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `gold_entries`")
                db.execSQL("ALTER TABLE `gold_entries_new` RENAME TO `gold_entries`")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `gold_entries_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `addedAt` INTEGER NOT NULL,
                        `weightGrams` REAL NOT NULL,
                        `purchasePricePerGram` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                // Previous versions stored the dialog's per-gram price in purchaseValueToman.
                db.execSQL(
                    """
                    INSERT INTO `gold_entries_new`
                        (`id`, `addedAt`, `weightGrams`, `purchasePricePerGram`)
                    SELECT
                        `id`,
                        `addedAt`,
                        `weightGrams`,
                        `purchaseValueToman`
                    FROM `gold_entries`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `gold_entries`")
                db.execSQL("ALTER TABLE `gold_entries_new` RENAME TO `gold_entries`")
            }
        }

        fun create(context: Context): AuValDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AuValDatabase::class.java,
                "auval.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
    }
}

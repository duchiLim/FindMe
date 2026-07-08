package com.greenbee.findme.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS recently_viewed_animals (
                desertionNo TEXT NOT NULL PRIMARY KEY,
                kindName TEXT NOT NULL,
                processState TEXT NOT NULL,
                shelterName TEXT NOT NULL,
                imageUrl1 TEXT,
                imageUrl2 TEXT,
                viewedAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorite_shelters (
                careRegNo TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                orgName TEXT NOT NULL,
                tel TEXT NOT NULL,
                address TEXT NOT NULL,
                savedAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}

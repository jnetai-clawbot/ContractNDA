package com.jnetai.contractnda.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jnetai.contractnda.model.Contract
import com.jnetai.contractnda.model.Clause

@Database(entities = [Contract::class, Clause::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contractDao(): ContractDao
    abstract fun clauseDao(): ClauseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contractnda.db"
                ).build().also { INSTANCE = it }
            }
    }
}
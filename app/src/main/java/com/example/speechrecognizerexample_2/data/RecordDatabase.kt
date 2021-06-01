package com.example.speechrecognizerexample_2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Record::class], version = 1)
abstract class RecordDatabase : RoomDatabase() {
    abstract val recordDao : RecordDao

    companion object{
        @Volatile
        private var INSTANCE : RecordDatabase? = null

        fun getInstance(context: Context) : RecordDatabase {
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordDatabase::class.java,
                        "speech_record_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

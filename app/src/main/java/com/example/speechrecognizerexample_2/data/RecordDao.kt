package com.example.speechrecognizerexample_2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table")
    fun getRecords() : LiveData<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record : Record)
}
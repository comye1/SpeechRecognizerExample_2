package com.example.speechrecognizerexample_2.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table")
    fun getRecords() : LiveData<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record : Record)

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM record_table")
    suspend fun clear()
}
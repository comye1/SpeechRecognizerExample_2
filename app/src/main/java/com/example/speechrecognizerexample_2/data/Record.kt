package com.example.speechrecognizerexample_2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

@Entity(tableName = "record_table")
data class Record (
    val speech: String,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0) {
    val createdDateFormatted : String
        get() = DateFormat.getDateTimeInstance().format(created)
}
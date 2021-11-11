package com.example.speechrecognizerexample_2.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechrecognizerexample_2.data.Record
import com.example.speechrecognizerexample_2.data.RecordDao
import kotlinx.coroutines.launch

class ListViewModel(dao: RecordDao) : ViewModel() {
    val recordDao = dao
    val records = recordDao.getRecords()

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            recordDao.delete(record)
        }
    }
}
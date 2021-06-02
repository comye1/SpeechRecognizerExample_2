package com.example.speechrecognizerexample_2.screens.list

import androidx.lifecycle.ViewModel
import com.example.speechrecognizerexample_2.data.RecordDao

class ListViewModel(dao: RecordDao) : ViewModel() {

    val recordDao = dao
    val records = recordDao.getRecords()
}
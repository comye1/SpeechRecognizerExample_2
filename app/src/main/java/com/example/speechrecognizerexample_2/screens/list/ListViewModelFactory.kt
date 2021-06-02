package com.example.speechrecognizerexample_2.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.speechrecognizerexample_2.data.RecordDao
import java.lang.IllegalArgumentException

class ListViewModelFactory(private val dao : RecordDao)
    : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ListViewModel::class.java)) {
                return ListViewModel(dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
package com.example.speechrecognizerexample_2.screens

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechrecognizerexample_2.data.Record
import com.example.speechrecognizerexample_2.data.RecordDao
import kotlinx.coroutines.launch

class MainViewModel(dao : RecordDao) : ViewModel() {

    val recordDao = dao

    private val _navigateToList = MutableLiveData<Boolean>()
    val navigateToList : LiveData<Boolean>
        get() = _navigateToList

    private val _onRecordSaved = MutableLiveData<Boolean>()
    val onRecordSaved : LiveData<Boolean>
        get() = _onRecordSaved

    fun doneNavigating(){
        _navigateToList.value = false
    }

    fun doneShowingSavedMessage(){
        _onRecordSaved.value = false
    }

    fun onSaveButtonClicked(record : Record){
        viewModelScope.launch {
            recordDao.insert(record)

            _onRecordSaved.value = true
        }
    }
}
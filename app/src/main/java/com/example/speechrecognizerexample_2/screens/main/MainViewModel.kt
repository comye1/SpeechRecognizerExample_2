 package com.example.speechrecognizerexample_2.screens.main

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


    private val _speechText = MutableLiveData<String>()
    val speechText : LiveData<String>
        get() = _speechText

    fun setSpeechText(text: String){
        if(speechText.value.isNullOrEmpty()) {
            _speechText.value = text
        }else{
            _speechText.value += text
        }
    }

    fun clearSpeechText(){
        _speechText.value = null
    }

    fun doneNavigating(){
        _navigateToList.value = false
    }

    fun doneShowingSavedMessage(){
        _onRecordSaved.value = false
    }

    fun onSaveButtonClicked(){
        if(!speechText.value.isNullOrEmpty()){
            val record = Record(speechText.value!!)
            createRecord(record)
        }
        clearSpeechText()
    }

    private fun createRecord(record : Record){
        viewModelScope.launch {
            recordDao.insert(record)
            _onRecordSaved.value = true
        }
    }
}
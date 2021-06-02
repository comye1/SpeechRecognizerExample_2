package com.example.speechrecognizerexample_2.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.speechrecognizerexample_2.R
import com.example.speechrecognizerexample_2.data.Record
import com.example.speechrecognizerexample_2.data.RecordDao
import com.example.speechrecognizerexample_2.data.RecordDatabase
import com.example.speechrecognizerexample_2.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding
    private var speechText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false)

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val dao = RecordDatabase.getInstance(application).recordDao
        val viewModelFactory = MainViewModelFactory(dao)
        val mainViewModel = ViewModelProvider(
            this, viewModelFactory).get(MainViewModel::class.java)


        binding.mainViewModel = mainViewModel

        if(!speechText.isNullOrEmpty()){
            binding.textView.text = speechText
        }

        binding.imageView.setOnClickListener {
            checkAudioPermission()
            startSpeechToText()
        }

        binding.buttonList.setOnClickListener {
            navigateToList()
        }

        binding.buttonSave.setOnClickListener {
            //validation
            if(!speechText.isNullOrBlank()){
                //save text
                val record = Record(speechText)
                mainViewModel.onSaveButtonClicked(record)
            }
            clearText()
        }

        binding.buttonClear.setOnClickListener {
            clearText()
        }

        mainViewModel.navigateToList.observe(viewLifecycleOwner, Observer {
            if(it){
                navigateToList()
                mainViewModel.doneNavigating()
            }
        })

        mainViewModel.onRecordSaved.observe(viewLifecycleOwner, Observer {
            if(it){
                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_LONG).show()
            }
        })
        return binding.root
    }

    private fun clearText() {
        speechText = ""
        binding.textView.text = "Output Text"
    }

    private fun navigateToList() {
        findNavController().navigate(R.id.action_mainFragment_to_listFragment)
    }

    private fun checkAudioPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
            var permission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO)
            if(permission != PackageManager.PERMISSION_GRANTED) {
                // this will open settings which asks for permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.speechrecognizerexample_2"))
                startActivity(intent)
                Toast.makeText(requireContext(), "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                binding.imageView.setImageResource(R.drawable.ic_baseline_mic_96)
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {
                binding.imageView.setImageResource(R.drawable.ic_baseline_mic_none_96)
            }
            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    // result[0] will give the output of speech
//                    if(!speechText.isNullOrEmpty()) {
//                        speechText += ".\n"+ result[0]
//                    }else{
//                        speechText = result[0]
//                    }
                    speechText += result[0] + ".\n"
                    binding.textView.text = speechText
                }
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        })
        // starts listening ...
        speechRecognizer.startListening(speechRecognizerIntent)
    }
}
package com.example.speechrecognizerexample_2.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speechrecognizerexample_2.R
import com.example.speechrecognizerexample_2.data.RecordDao
import com.example.speechrecognizerexample_2.data.RecordDatabase
import com.example.speechrecognizerexample_2.databinding.FragmentListBinding

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false)

        val application = requireNotNull(this.activity).application
        val dao = RecordDatabase.getInstance(application).recordDao
        val viewModelFactory = ListViewModelFactory(dao)

        val listViewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)
        binding.lifecycleOwner = this

        val adapter = RecordsAdapter()
        binding.recordList.adapter = adapter

        listViewModel.records.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.recordList.layoutManager = manager

        return binding.root
    }
}
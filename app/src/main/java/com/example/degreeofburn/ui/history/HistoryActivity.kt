package com.example.degreeofburn.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityHistoryBinding
import com.example.degreeofburn.ui.history.adapter.HistoryAdapter
import com.example.degreeofburn.utils.Resource

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        setupRecyclerView()
        observeData()
    }

    private fun setupUI() {
        binding.btnBackHistory.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun observeData() {
        viewModel.historyItems.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    if (resource.data?.isEmpty() == true) {
                        showEmptyState(true)
                    } else {
                        showEmptyState(false)
                        resource.data?.let { historyAdapter.setHistoryList(it) }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showEmptyState(true)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // For demo purposes, you can switch between real data and dummy data
        // Uses real API data
        viewModel.loadHistoryData()

        // OR use dummy data if you're testing without API
        // viewModel.loadDummyData()
    }



    private fun showEmptyState(isEmpty: Boolean) {
        val emptyStateLayout = findViewById<View>(R.id.emptyStateLayout)

        if (isEmpty) {
            emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }
}
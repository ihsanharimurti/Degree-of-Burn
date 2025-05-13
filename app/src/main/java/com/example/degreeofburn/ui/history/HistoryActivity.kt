package com.example.degreeofburn.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityHistoryBinding
import com.example.degreeofburn.ui.history.adapter.HistoryAdapter
import com.example.degreeofburn.ui.home.MainActivity
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
        setupSearchBar()
        observeData()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Alih-alih perilaku default, pindah ke MainActivity
        navigateToMainActivity()
    }

    private fun setupUI() {
        binding.btnBackHistory.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                historyAdapter.filter(query)
                binding.clearSearchButton.isVisible = query.isNotEmpty()
            }
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                historyAdapter.filter(query)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.text.clear()
            historyAdapter.filter("")
            binding.clearSearchButton.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)

            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Check if we need to load more items
                    if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                        // We're near the end of the list, load more items
                        historyAdapter.loadNextPage()
                    }
                }
            })
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

        // Observe loading state for pagination
        viewModel.isLoading.observe(this) { isLoading ->
            // You could show a bottom loading indicator here if needed
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
            // Show loading overlay and progress bar
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE

            // Disable all interactive elements
            binding.searchCardView.isEnabled = false

            // Add visual dimming effect to searchCardView
            binding.searchCardView.alpha = 0.2f

            // Bring the overlay and progress bar to the front
            binding.loadingOverlay.bringToFront()
            binding.progressBar.bringToFront()
        } else {
            // Hide loading overlay and progress bar
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            // Re-enable all interactive elements
            binding.searchCardView.isEnabled = true

            // Restore normal appearance
            binding.searchCardView.alpha = 1.0f
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
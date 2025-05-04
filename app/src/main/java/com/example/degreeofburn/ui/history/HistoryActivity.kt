package com.example.degreeofburn.ui.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.HistoryModel
import com.example.degreeofburn.databinding.ActivityHistoryBinding
import com.example.degreeofburn.ui.history.adapter.HistoryAdapter

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        loadDummyData()
    }

    private fun setupUI() {
        binding.btnBackHistory.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun loadDummyData() {
        val dummyData = getDummyHistoryData()

        if (dummyData.isEmpty()) {
            // Show placeholder when there's no data
            showEmptyState(true)
        } else {
            showEmptyState(false)
            historyAdapter.setHistoryList(dummyData)
        }
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

    private fun getDummyHistoryData(): List<HistoryModel> {
        // Change to true to show empty state placeholder
        val showEmptyState = true

        return if (showEmptyState) {
            emptyList()
        } else {
            // Dummy data
            listOf(
                HistoryModel(
                    id = "1",
                    patientName = "Ahmad Setiawan",
                    officerName = "dr. Budi Santoso",
                    actionDate = "25/04/2025"
                ),
                HistoryModel(
                    id = "2",
                    patientName = "Siti Rahayu",
                    officerName = "dr. Ani Wijaya",
                    actionDate = "24/04/2025"
                ),
                HistoryModel(
                    id = "3",
                    patientName = "Doni Kurniawan",
                    officerName = "dr. Linda Susanti",
                    actionDate = "23/04/2025"
                ),
                HistoryModel(
                    id = "4",
                    patientName = "Maya Putri",
                    officerName = "dr. Rini Agustina",
                    actionDate = "22/04/2025"
                ),
                HistoryModel(
                    id = "5",
                    patientName = "Rudi Hermawan",
                    officerName = "dr. Hadi Prasetyo",
                    actionDate = "21/04/2025"
                ),
                HistoryModel(
                    id = "6",
                    patientName = "Doni Kurniawan",
                    officerName = "dr. Linda Susanti",
                    actionDate = "23/04/2025"
                ),
                HistoryModel(
                    id = "7",
                    patientName = "Maya Putri",
                    officerName = "dr. Rini Agustina",
                    actionDate = "22/04/2025"
                ),
                HistoryModel(
                    id = "8",
                    patientName = "Rudi Hermawan",
                    officerName = "dr. Hadi Prasetyo",
                    actionDate = "21/04/2025"
                )

            )
        }
    }
}
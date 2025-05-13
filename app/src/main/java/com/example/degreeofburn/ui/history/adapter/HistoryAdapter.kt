package com.example.degreeofburn.ui.history.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.degreeofburn.data.model.HistoryModel
import com.example.degreeofburn.databinding.ActivityItemLoadMoreBinding
import com.example.degreeofburn.databinding.HistoryCardBinding
import com.example.degreeofburn.ui.result.ResultActivity
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var historyList = ArrayList<HistoryModel>()
    private var filteredList = ArrayList<HistoryModel>()
    private var currentPage = 1
    private val itemsPerPage = 15
    private var isLoadingMore = false
    private var hasMoreItems = true

    // Current search query
    private var currentSearchQuery: String = ""

    // View type constants
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    init {
        filteredList = historyList
    }

    fun setHistoryList(historyList: List<HistoryModel>) {
        this.historyList.clear()
        this.historyList.addAll(historyList.sortedByDescending { it.id })
        filter(currentSearchQuery) // Apply current filter
    }

    fun filter(query: String) {
        currentSearchQuery = query
        currentPage = 1

        filteredList = if (query.isEmpty()) {
            ArrayList(historyList)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            ArrayList(historyList.filter { history ->
                history.patientName.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        history.officerName.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        history.actionDate.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            })
        }

        hasMoreItems = filteredList.size > itemsPerPage
        notifyDataSetChanged()
    }

    fun loadNextPage() {
        if (isLoadingMore || !hasMoreItems) return

        isLoadingMore = true
        notifyItemInserted(getVisibleItemCount())

        // Simulate loading delay - in real app would be API call
        android.os.Handler().postDelayed({
            isLoadingMore = false
            currentPage++

            // Check if there are still more items after this page
            hasMoreItems = filteredList.size > currentPage * itemsPerPage

            notifyDataSetChanged()
        }, 1000)
    }

    private fun getVisibleItemCount(): Int {
        val totalItems = filteredList.size
        val itemsToShow = currentPage * itemsPerPage

        // Calculate how many items should be visible
        val visibleItems = if (totalItems <= itemsToShow) {
            totalItems // Show all items if we have fewer than the calculated amount
        } else {
            itemsToShow // Show calculated amount
        }

        // Add 1 for loading item if more items exist
        return if (hasMoreItems && isLoadingMore) visibleItems + 1 else visibleItems
    }

    override fun getItemViewType(position: Int): Int {
        val visibleItems = if (hasMoreItems && isLoadingMore) {
            currentPage * itemsPerPage
        } else {
            getVisibleItemCount()
        }

        return if (position == visibleItems && isLoadingMore) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HistoryViewHolder(binding)
        } else {
            val binding = ActivityItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = getVisibleItemCount()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HistoryViewHolder && position < filteredList.size) {
            holder.bind(filteredList[position])
        }
    }

    inner class HistoryViewHolder(private val binding: HistoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryModel) {
            binding.tvRecyclerPatient.text = history.patientName
            binding.tvRecyclerOfficer.text = history.officerName
            binding.tvRecyclerDate.text = history.actionDate

            // Set click listener to show toast with rekam medis ID
            itemView.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "ID Rekam Medis: ${history.id}",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(itemView.context, ResultActivity::class.java).apply {
                    putExtra("ID_REKAM_MEDIS", history.id)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    inner class LoadingViewHolder(binding: ActivityItemLoadMoreBinding) : RecyclerView.ViewHolder(binding.root)
}
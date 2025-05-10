package com.example.degreeofburn.ui.history.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.degreeofburn.data.model.HistoryModel
import com.example.degreeofburn.databinding.HistoryCardBinding
import com.example.degreeofburn.ui.result.ResultActivity

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = ArrayList<HistoryModel>()

    fun setHistoryList(historyList: List<HistoryModel>) {
        this.historyList.clear()
        this.historyList.addAll(historyList.sortedByDescending { it.id })
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
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
}
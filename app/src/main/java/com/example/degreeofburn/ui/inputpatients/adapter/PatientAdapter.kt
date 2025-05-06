package com.example.degreeofburn.ui.inputpatients.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.response.PatientResponse

class PatientAdapter(
    context: Context,
    resource: Int,
    private val patients: List<PatientResponse>
) : ArrayAdapter<PatientResponse>(context, resource, patients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_patient_dropdown, parent, false
        )

        val patient = patients[position]
        val textViewName = view.findViewById<TextView>(R.id.tvPatientName)
        val textViewDetails = view.findViewById<TextView>(R.id.tvPatientDetails)

        textViewName.text = patient.nama
        textViewDetails.text = "${patient.usia} tahun, ${patient.jenisKelamin}"

        return view
    }

    override fun getItem(position: Int): PatientResponse {
        return patients[position]
    }

    override fun getItemId(position: Int): Long {
        return patients[position].idPasien.toLong()
    }
}
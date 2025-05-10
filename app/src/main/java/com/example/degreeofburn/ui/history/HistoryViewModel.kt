package com.example.degreeofburn.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.HistoryModel
import com.example.degreeofburn.data.model.MedicalHistoryModel
import com.example.degreeofburn.data.repository.HistoryRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application)

    private val _historyItems = MutableLiveData<Resource<List<HistoryModel>>>()
    val historyItems: LiveData<Resource<List<HistoryModel>>> = _historyItems

    init {
        loadHistoryData()
    }

    fun loadHistoryData() {
        viewModelScope.launch {
            _historyItems.value = Resource.Loading()

            val result = repository.getMedicalHistory()

            // Map MedicalHistoryModel to HistoryModel for display
            _historyItems.value = when (result) {
                is Resource.Success -> {
                    val historyModels = result.data?.map { it.toHistoryModel() } ?: emptyList()
                    Resource.Success(historyModels)
                }
                is Resource.Error -> Resource.Error(result.message ?: "Unknown error", null)
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    // Extension function to convert MedicalHistoryModel to HistoryModel
    private fun MedicalHistoryModel.toHistoryModel(): HistoryModel {
        return HistoryModel(
            id = this.idRekamMedis,
            patientName = this.patientName,
            officerName = this.officerName,
            actionDate = this.actionDate
        )
    }

    // For testing without API
//    fun loadDummyData() {
//        val dummyData = listOf(
//            HistoryModel(
//                id = "1",
//                patientName = "Ahmad Setiawan",
//                officerName = "dr. Budi Santoso",
//                actionDate = "25/04/2025"
//            ),
//            HistoryModel(
//                id = "2",
//                patientName = "Siti Rahayu",
//                officerName = "dr. Ani Wijaya",
//                actionDate = "24/04/2025"
//            ),
//            HistoryModel(
//                id = "3",
//                patientName = "Doni Kurniawan",
//                officerName = "dr. Linda Susanti",
//                actionDate = "23/04/2025"
//            ),
//            HistoryModel(
//                id = "4",
//                patientName = "Maya Putri",
//                officerName = "dr. Rini Agustina",
//                actionDate = "22/04/2025"
//            ),
//            HistoryModel(
//                id = "5",
//                patientName = "Rudi Hermawan",
//                officerName = "dr. Hadi Prasetyo",
//                actionDate = "21/04/2025"
//            )
//        )

//        _historyItems.value = Resource.Success(dummyData)
//    }
}
package com.example.degreeofburn.data.model

import com.example.degreeofburn.data.model.response.MedicalRecordResponse
import com.example.degreeofburn.data.model.response.PatientResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import java.text.SimpleDateFormat
import java.util.Locale

data class ResultModel(
    val idRekamMedis: Int,
    val idPasien: Int,
    val patientName: String,
    val patientWeight: String,
    val patientHeight: Int,
    val patienAge:Int,
    val patientBlood:String,
    val officerName: String,  // This will come from UserDetailResponse
    val actionDate: String,
    val diagnosa: String,
    val gambarLuka: String,
    val luasLuka: Int,
    val derajatLuka: Int,
    val kebutuhanCairan: Int
) {
    companion object {
        fun fromApiResponse(
            medicalRecord: MedicalRecordResponse,
            patient: PatientResponse,
            userDetail: UserDetailResponse?
        ): ResultModel {
            // Format date from API (2024-07-03T00:00:00.000Z) to display format (03/07/2024)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = try {
                val parsedDate = inputFormat.parse(medicalRecord.tanggal)
                parsedDate?.let { outputFormat.format(it) } ?: "Unknown date"
            } catch (e: Exception) {
                "Unknown date"
            }

            return ResultModel(
                idRekamMedis = medicalRecord.idRekamMedis,
                idPasien = medicalRecord.idPasien,
                patientName = patient.nama,
                patientWeight=patient.beratBadan,
                patientHeight=patient.tinggiBadan,
                patienAge = patient.usia,
                patientBlood = patient.golonganDarah,
                officerName = userDetail?.nama ?: "dr. Unknown",
                actionDate = date,
                diagnosa = medicalRecord.diagnosa,
                gambarLuka = medicalRecord.gambarLuka,
                luasLuka = medicalRecord.luasLuka,
                derajatLuka = medicalRecord.derajatLuka,
                kebutuhanCairan = medicalRecord.kebutuhanCairan
            )
        }
    }

    fun toResultModel(): ResultProcessedModel {
        return ResultProcessedModel(
            idRekamMedis = idRekamMedis,
            idPasien = idPasien,
            patientName = patientName,
            patientWeight = patientWeight,
            patientHeight = patientHeight,
            patienAge = patienAge,
            patientBlood = patientBlood,
            officerName = officerName,
            actionDate = actionDate,
            diagnosa = diagnosa,
            gambarLuka = gambarLuka,
            luasLuka = luasLuka,
            derajatLuka = derajatLuka,
            kebutuhanCairan = kebutuhanCairan
        )
    }

}

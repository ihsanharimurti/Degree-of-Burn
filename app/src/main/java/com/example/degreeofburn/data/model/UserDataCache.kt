package com.example.degreeofburn.data.model

import com.example.degreeofburn.data.model.response.UserDetailResponse

object UserDataCache {
    private var userDetail: UserDetailResponse? = null
    private var patientCount: Int = 0

    // Timestamp untuk mengetahui kapan terakhir data diambil
    private var lastFetchTime: Long = 0

    // Tetapkan batas waktu cache (misal 5 menit = 300000 ms)
    private const val CACHE_TIMEOUT = 300000L

    fun setUserDetail(data: UserDetailResponse) {
        userDetail = data
        lastFetchTime = System.currentTimeMillis()
    }

    fun getUserDetail(): UserDetailResponse? {
        return userDetail
    }

    fun setPatientCount(count: Int) {
        patientCount = count
        lastFetchTime = System.currentTimeMillis()
    }

    fun getPatientCount(): Int {
        return patientCount
    }

    fun isCacheValid(): Boolean {
        return userDetail != null &&
                System.currentTimeMillis() - lastFetchTime < CACHE_TIMEOUT
    }

    fun clearCache() {
        userDetail = null
        patientCount = 0
        lastFetchTime = 0
    }

    // Flag untuk memaksa refresh data setelah update profile
    private var forceRefresh = false

    fun setForceRefresh(refresh: Boolean) {
        forceRefresh = refresh
    }

    fun shouldForceRefresh(): Boolean {
        val shouldRefresh = forceRefresh
        // Reset flag setelah digunakan
        if (forceRefresh) {
            forceRefresh = false
        }
        return shouldRefresh
    }
}
package com.example.degreeofburn.data.remote

import com.example.degreeofburn.data.model.request.ChangePasswordRequest
import com.example.degreeofburn.data.model.request.LoginRequest
import com.example.degreeofburn.data.model.response.LoginResponse
import com.example.degreeofburn.data.model.request.OtpRequest
import com.example.degreeofburn.data.model.response.OtpResponse
import com.example.degreeofburn.data.model.request.PatientRequest
import com.example.degreeofburn.data.model.response.PatientResponse
import com.example.degreeofburn.data.model.request.RegisterRequest
import com.example.degreeofburn.data.model.request.UpdateUserRequest
import com.example.degreeofburn.data.model.response.ChangePasswordResponse
import com.example.degreeofburn.data.model.response.PatientCountResponse
import com.example.degreeofburn.data.model.response.RegisterResponse
import com.example.degreeofburn.data.model.response.UpdateUserResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun registerDoctor(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<OtpResponse>

    @POST("pasien")
    suspend fun createPatient(@Body patientRequest: PatientRequest): Response<PatientResponse>


    @GET("pasien/count/user")
    suspend fun getPatientCount(@Query("count") countType: String): Response<PatientCountResponse>

    @GET("users/{uid}")
    suspend fun getUserDetail(@Path("uid") userId: String): Response<UserDetailResponse>

    @PUT("users/{uid}")
    suspend fun updateUser(
        @Path("uid") userId: String,
        @Body updateUserRequest: UpdateUserRequest
    ): Response<UpdateUserResponse>

    @PUT("auth/change-password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}



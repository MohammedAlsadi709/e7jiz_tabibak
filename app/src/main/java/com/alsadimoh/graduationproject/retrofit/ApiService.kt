package com.alsadimoh.graduationproject.retrofit

import com.alsadimoh.graduationproject.retrofit.models.NotificationModel
import com.alsadimoh.graduationproject.retrofit.models.ResponseClass
import com.alsadimoh.graduationproject.retrofit.models.ResponseClassWithoutItems
import com.alsadimoh.graduationproject.retrofit.models.auth.*
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.EditBookingTreatmentsResponseModel
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.TreatmentsAndPeriodsResponseModel
import com.alsadimoh.graduationproject.retrofit.models.userBooking.*
import com.alsadimoh.graduationproject.retrofit.models.userCaregories.SpecializationDoctorsModel
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.CommentForUserModel
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.DoctorProfileForUserRequestModel
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.DoctorProfileForUserResponse
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.RateDoctorRequestModel
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.models.userHome.HomePageModel
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel
import com.alsadimoh.graduationproject.retrofit.models.userProfile.BookmarksResponseModel
import com.alsadimoh.graduationproject.retrofit.models.userProfile.GetUserProfileModel
import com.alsadimoh.graduationproject.retrofit.models.userProfile.UpdateProfileModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("user/signup")
    suspend fun register(@Body registerModel: RegisterModel): ResponseClass<RegisterResponseModel>

    @POST("user/login")
    suspend fun login(@Body loginModel: LoginModel): ResponseClass<LoginResponseModel>

    @POST("user/logout")
    suspend fun logout(): ResponseClassWithoutItems

    @GET("user/homepage")
    suspend fun getUserHome(): ResponseClass<HomePageModel>

    @GET("user/doctors")
    suspend fun getAllRatedDoctors(@Query("page") page: String): ResponseClass<List<DoctorModelForUserHome>>


    @GET("user/search/words")
    suspend fun searchWithOnlyWords(@Query("words") words: String): ResponseClass<List<DoctorModelForUserHome>>

    @GET("user/search/filter")
    suspend fun searchWithFilter(
        @Query("specialization") specialization: String,
        @Query("gender") gender: String,
        @Query("waiting_min") waiting_min: Float,
        @Query("waiting_max") waiting_max: Float,
        @Query("price_min") price_min: Float,
        @Query("price_max") price_max: Float,
        @Query("lat") lat: Double?,
        @Query("long") long: Double?,
        @Query("sort_by") sort_by: String,
        @Query("sort") sort: String
    ): ResponseClass<List<DoctorModelForUserHome>>

    @GET("user/specialization/{id}")
    suspend fun getDoctorsOfCategory(@Path("id") id: Int): ResponseClass<SpecializationDoctorsModel>

    @GET("user/allspecializations")
    suspend fun getAllCategories(): ResponseClass<List<SpecializationModel>>

    @GET("user/profile")
    suspend fun getUserProfile(): ResponseClass<GetUserProfileModel>

    @GET("user/bookmark")
    suspend fun getUserBookmarks(): ResponseClass<List<BookmarksResponseModel>>

    @POST("user/forget/password")
    suspend fun changePasswordForForgetPassword(@Body forgetPasswordModel: ForgetPasswordModel): ResponseClassWithoutItems

    @POST("user/change/password")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel): ResponseClassWithoutItems

    @Multipart
    @POST("user/change/profile")
    suspend fun updateUserProfile(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part image: MultipartBody.Part?
    ): ResponseClass<UpdateProfileModel>

    @POST("user/doctor/profile")
    suspend fun getDoctorProfileForUser(@Body doctorProfileForUserRequestModel: DoctorProfileForUserRequestModel): ResponseClass<DoctorProfileForUserResponse>

    @FormUrlEncoded
    @POST("user/doctor/ratings")
    suspend fun getAllDoctorRatingsForUser(@Field("doctor_id") doctor_id: Int): ResponseClass<List<CommentForUserModel>>

    @FormUrlEncoded
    @POST("user/doctor/bookmark")
    suspend fun putInBookmarksForUser(@Field("doctor_id") doctor_id: Int): ResponseClassWithoutItems

    @POST("user/day/bookings")
    suspend fun getBookingsForDay(@Body getBookingsForDay: GetBookingsForDay): ResponseClass<List<AvailableTimesForAddNewBooking>>

    @POST("user/start/booking")
    suspend fun startNewBooking(@Body newBookingRequestModel: NewBookingRequestModel): ResponseClassWithoutItems

    @POST("user/booking/edit")
    suspend fun editBookingForUser(@Body editBookingRequestModel: EditBookingRequestModel): ResponseClassWithoutItems

    @POST("user/doctor/rate")
    suspend fun rateDoctor(@Body rateDoctorRequestModel: RateDoctorRequestModel): ResponseClassWithoutItems

    @GET("user/bookings/upcoming")
    suspend fun getIncomingBookingForUser(): ResponseClass<List<BookingModel>>

    @GET("user/bookings/expired")
    suspend fun getFinishedBookingForUser(): ResponseClass<List<BookingModel>>

    @GET("user/bookings/pending")
    suspend fun getPendingBookingForUser(): ResponseClass<List<BookingModel>>

    @POST("user/booking/upcoming")
    suspend fun getIncomingBookingDetailsForUser(@Body model: GetIncomingBookingDetailsRequestModel): ResponseClass<BookingModel>

    @POST("user/booking/pending")
    suspend fun getPendingBookingDetailsForUser(@Body getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel): ResponseClass<BookingModel>

    @FormUrlEncoded
    @POST("user/booking/expired")
    suspend fun getFinishedBookingDetailsForUser(@Field("booking_id") booking_id: Int): ResponseClass<BookingModel>

    @FormUrlEncoded
    @POST("user/booking/cancel")
    suspend fun cancelBookingForUser(@Field("booking_id") booking_id: Int): ResponseClassWithoutItems

    @FormUrlEncoded
    @POST("doctor/booking/upcoming")
    suspend fun getIncomingBookingsForDoctor(@Field("date") date: String): ResponseClass<List<BookingModelForDoctor>>

    @FormUrlEncoded
    @POST("doctor/booking")
    suspend fun getBookingsInfoForDoctor(@Field("booking_id") booking_id: Int): ResponseClass<BookingModelForDoctor>

    @GET("doctor/booking/expired")
    suspend fun getFinishedBookingsForDoctor(): ResponseClass<List<BookingModelForDoctor>>

    @FormUrlEncoded
    @POST("doctor/booking/cancel")
    suspend fun cancelBookingForDoctor(
        @Field("booking_id") booking_id: Int,
        @Field("reason") reason: String,
        @Field("is_attended") is_attended: String,
        @Field("status") status: String,
    ): ResponseClassWithoutItems

    @GET("doctor/booking/pending")
    suspend fun getPendingBookingsForDoctor(): ResponseClass<List<BookingModelForDoctor>>

    @FormUrlEncoded
    @POST("doctor/booking/approve")
    suspend fun approveBookingForDoctor(
        @Field("booking_id") booking_id: Int,
        @Field("status") status: String
    ): ResponseClass<List<BookingModelForDoctor>>

    @GET("doctor/ratings")
    suspend fun getAllDoctorRatingsForDoctor(): ResponseClass<List<CommentForUserModel>>

    @GET("doctor/profile")
    suspend fun getDoctorProfileForDoctor(): ResponseClass<DoctorProfileForUserResponse>

    @Multipart
    @POST("doctor/change/profile")
    suspend fun updateDoctorProfileForDoctor(
        @Part("bio") bio: RequestBody,
        @Part("patient_examination_price") patient_examination_price: RequestBody,
        @Part("average_answer_time") average_answer_time: RequestBody,
        @Part("waiting_time") waiting_time: RequestBody,
        @Part("address") address: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part image: MultipartBody.Part?
    ): ResponseClassWithoutItems


    @GET("doctor/treatment")
    suspend fun getMedicinesAndPeriods(): ResponseClass<TreatmentsAndPeriodsResponseModel>


    @FormUrlEncoded
    @POST("doctor/treatment")
    suspend fun addTreatmentForBooking(
        @Field("booking_id") booking_id: Int,
        @Field("doctor_notes") doctor_notes: String,
        @Field("data") data: String
    ): ResponseClassWithoutItems


    @FormUrlEncoded
    @POST("doctor/treatment/edit")
    suspend fun editTreatmentForBooking(
        @Field("booking_id") booking_id: Int,
        @Field("doctor_notes") doctor_notes: String,
        @Field("data") data: String
    ): ResponseClassWithoutItems

    @FormUrlEncoded
    @POST("doctor/medicines")
    suspend fun searchForMedicineForDoctor(@Field("word") word: String): ResponseClass<List<MedicineModel>>

    @FormUrlEncoded
    @POST("doctor/add/medicine")
    suspend fun addNewMedicineToList(
        @Field("name") name: String,
        @Field("description") description: String
    ): ResponseClass<MedicineModel>

    @FormUrlEncoded
    @POST("doctor/treatment/get")
    suspend fun getOldResultsToEditTreatment(@Field("booking_id") booking_id: Int): ResponseClass<EditBookingTreatmentsResponseModel>

    @GET("notifications")
    suspend fun getAllNotifications(): ResponseClass<List<NotificationModel>>


}
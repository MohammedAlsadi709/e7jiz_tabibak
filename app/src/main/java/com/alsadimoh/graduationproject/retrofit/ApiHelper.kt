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

interface ApiHelper {
    suspend fun register(@Body registerModel: RegisterModel): ResponseClass<RegisterResponseModel>
    suspend fun login(@Body loginModel: LoginModel): ResponseClass<LoginResponseModel>
    suspend fun logout(): ResponseClassWithoutItems
    suspend fun getUserHome(): ResponseClass<HomePageModel>
    suspend fun getAllRatedDoctors(@Query("page") page:String): ResponseClass<List<DoctorModelForUserHome>>
    suspend fun searchWithOnlyWords(@Query("words") words:String): ResponseClass<List<DoctorModelForUserHome>>
    suspend fun searchWithFilter(@Query("specialization") specialization:String,@Query("gender") gender:String,@Query("waiting_min") waiting_min:Float,@Query("waiting_max") waiting_max:Float,@Query("price_min") price_min:Float,@Query("price_max") price_max:Float,@Query("lat") lat:Double?,@Query("long") long:Double?,@Query("sort_by") sort_by:String,@Query("sort") sort:String): ResponseClass<List<DoctorModelForUserHome>>
    suspend fun getDoctorsOfCategory(@Path("id") id:Int) : ResponseClass<SpecializationDoctorsModel>
    suspend fun getAllCategories() : ResponseClass<List<SpecializationModel>>
    suspend fun getUserProfile() : ResponseClass<GetUserProfileModel>
    suspend fun getUserBookmarks() : ResponseClass<List<BookmarksResponseModel>>
    suspend fun changePasswordForForgetPassword(@Body forgetPasswordModel: ForgetPasswordModel) : ResponseClassWithoutItems
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel) : ResponseClassWithoutItems

    suspend fun updateUserProfile(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part image: MultipartBody.Part?
    ): ResponseClass<UpdateProfileModel>
    suspend fun getDoctorProfileForUser(@Body doctorProfileForUserRequestModel: DoctorProfileForUserRequestModel):ResponseClass<DoctorProfileForUserResponse>
    suspend fun getAllDoctorRatingsForUser(@Field("doctor_id") doctor_id:Int):ResponseClass<List<CommentForUserModel>>
    suspend fun putInBookmarksForUser(@Field("doctor_id") doctor_id:Int):ResponseClassWithoutItems
    suspend fun getBookingsForDay(@Body getBookingsForDay: GetBookingsForDay) : ResponseClass<List<AvailableTimesForAddNewBooking>>
    suspend fun startNewBooking(@Body newBookingRequestModel: NewBookingRequestModel) : ResponseClassWithoutItems
    suspend fun editBookingForUser(@Body editBookingRequestModel: EditBookingRequestModel): ResponseClassWithoutItems
    suspend fun rateDoctor(@Body rateDoctorRequestModel: RateDoctorRequestModel): ResponseClassWithoutItems
    suspend fun getIncomingBookingForUser() : ResponseClass<List<BookingModel>>
    suspend fun getFinishedBookingForUser() : ResponseClass<List<BookingModel>>
    suspend fun getPendingBookingForUser(): ResponseClass<List<BookingModel>>
    suspend fun getPendingBookingDetailsForUser(@Body getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel): ResponseClass<BookingModel>
    suspend fun getIncomingBookingDetailsForUser(@Body getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel) : ResponseClass<BookingModel>
    suspend fun getFinishedBookingDetailsForUser(@Field("booking_id") booking_id:Int):ResponseClass<BookingModel>
    suspend fun cancelBookingForUser(@Field("booking_id") booking_id:Int):ResponseClassWithoutItems
    suspend fun getIncomingBookingsForDoctor(@Field("date") date:String):ResponseClass<List<BookingModelForDoctor>>
    suspend fun getFinishedBookingsForDoctor():ResponseClass<List<BookingModelForDoctor>>
    suspend fun getBookingsInfoForDoctor(@Field("booking_id") booking_id:Int):ResponseClass<BookingModelForDoctor>
    suspend fun cancelBookingForDoctor(@Field("booking_id") booking_id:Int,@Field("reason") reason:String,@Field("is_attended") is_attended:String,@Field("status") status:String):ResponseClassWithoutItems
    suspend fun getPendingBookingsForDoctor():ResponseClass<List<BookingModelForDoctor>>
    suspend fun approveBookingForDoctor(@Field("booking_id") booking_id:Int,@Field("status") status:String):ResponseClass<List<BookingModelForDoctor>>
    suspend fun getAllDoctorRatingsForDoctor():ResponseClass<List<CommentForUserModel>>
    suspend fun getDoctorProfileForDoctor():ResponseClass<DoctorProfileForUserResponse>

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

    suspend fun getMedicinesAndPeriods():ResponseClass<TreatmentsAndPeriodsResponseModel>
    suspend fun addTreatmentForBooking(@Field("booking_id") booking_id:Int,@Field("doctor_notes") doctor_notes:String,@Field("data") data:String):ResponseClassWithoutItems
    suspend fun editTreatmentForBooking(
        @Field("booking_id") booking_id: Int,
        @Field("doctor_notes") doctor_notes: String,
        @Field("data") data: String
    ): ResponseClassWithoutItems
    suspend fun searchForMedicineForDoctor(@Field("word") word: String): ResponseClass<List<MedicineModel>>
    suspend fun addNewMedicineToList(@Field("name") name: String,@Field("description") description: String): ResponseClass<MedicineModel>
    suspend fun getOldResultsToEditTreatment(@Field("booking_id") booking_id: Int): ResponseClass<EditBookingTreatmentsResponseModel>
    suspend fun getAllNotifications(): ResponseClass<List<NotificationModel>>

}
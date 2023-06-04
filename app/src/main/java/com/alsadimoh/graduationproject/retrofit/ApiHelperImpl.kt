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

class ApiHelperImpl (private val apiService: ApiService): ApiHelper {

    override suspend fun register(registerModel: RegisterModel): ResponseClass<RegisterResponseModel> {
        return apiService.register(registerModel)
    }

    override suspend fun login(loginModel: LoginModel): ResponseClass<LoginResponseModel> {
        return apiService.login(loginModel)
    }

    override suspend fun getUserHome(): ResponseClass<HomePageModel> {
        return apiService.getUserHome()
    }

    override suspend fun getAllRatedDoctors(page: String): ResponseClass<List<DoctorModelForUserHome>> {
        return apiService.getAllRatedDoctors(page)
    }

    override suspend fun searchWithOnlyWords(words: String): ResponseClass<List<DoctorModelForUserHome>> {
        return apiService.searchWithOnlyWords(words)
    }

    override suspend fun searchWithFilter(
        specialization: String,
        gender: String,
        waiting_min: Float,
        waiting_max: Float,
        price_min: Float,
        price_max: Float,
        lat: Double?,
        long: Double?,
        sort_by: String,
        sort: String
    ): ResponseClass<List<DoctorModelForUserHome>> {
        return apiService.searchWithFilter(specialization, gender, waiting_min, waiting_max, price_min, price_max, lat, long, sort_by, sort)
    }

    override suspend fun getDoctorsOfCategory(id: Int): ResponseClass<SpecializationDoctorsModel> {
        return apiService.getDoctorsOfCategory(id)
    }

    override suspend fun getAllCategories(): ResponseClass<List<SpecializationModel>> {
        return apiService.getAllCategories()
    }

    override suspend fun getUserProfile(): ResponseClass<GetUserProfileModel> {
        return apiService.getUserProfile()
    }

    override suspend fun getUserBookmarks(): ResponseClass<List<BookmarksResponseModel>> {
        return apiService.getUserBookmarks()
    }

    override suspend fun changePasswordForForgetPassword(forgetPasswordModel: ForgetPasswordModel): ResponseClassWithoutItems {
        return apiService.changePasswordForForgetPassword(forgetPasswordModel)
    }

    override suspend fun changePassword(changePasswordModel: ChangePasswordModel): ResponseClassWithoutItems {
        return apiService.changePassword(changePasswordModel)
    }


    override suspend fun logout(): ResponseClassWithoutItems {
        return  apiService.logout()
    }


    override suspend fun updateUserProfile(
        name: RequestBody,
        gender: RequestBody,
        dateOfBirth: RequestBody,
        phone_number: RequestBody,
        image: MultipartBody.Part?
    ): ResponseClass<UpdateProfileModel> {
        return apiService.updateUserProfile(name,gender, dateOfBirth, phone_number, image)
    }

    override suspend fun getDoctorProfileForUser(doctorProfileForUserRequestModel: DoctorProfileForUserRequestModel): ResponseClass<DoctorProfileForUserResponse> {
        return apiService.getDoctorProfileForUser(doctorProfileForUserRequestModel)
    }


    override suspend fun getAllDoctorRatingsForUser(doctor_id: Int): ResponseClass<List<CommentForUserModel>> {
        return apiService.getAllDoctorRatingsForUser(doctor_id)
    }

    override suspend fun putInBookmarksForUser(doctor_id: Int): ResponseClassWithoutItems {
        return apiService.putInBookmarksForUser(doctor_id)
    }

    override suspend fun getBookingsForDay(getBookingsForDay: GetBookingsForDay): ResponseClass<List<AvailableTimesForAddNewBooking>> {
        return apiService.getBookingsForDay(getBookingsForDay)
    }

    override suspend fun startNewBooking(newBookingRequestModel: NewBookingRequestModel): ResponseClassWithoutItems {
        return apiService.startNewBooking(newBookingRequestModel)
    }

    override suspend fun editBookingForUser(editBookingRequestModel: EditBookingRequestModel): ResponseClassWithoutItems {
        return apiService.editBookingForUser(editBookingRequestModel)
    }

    override suspend fun rateDoctor(rateDoctorRequestModel: RateDoctorRequestModel): ResponseClassWithoutItems {
        return apiService.rateDoctor(rateDoctorRequestModel)
    }

    override suspend fun getIncomingBookingForUser(): ResponseClass<List<BookingModel>> {
        return apiService.getIncomingBookingForUser()
    }

    override suspend fun getFinishedBookingForUser(): ResponseClass<List<BookingModel>> {
        return  apiService.getFinishedBookingForUser()
    }

    override suspend fun getPendingBookingForUser(): ResponseClass<List<BookingModel>> {
        return apiService.getPendingBookingForUser()
    }

    override suspend fun getPendingBookingDetailsForUser(getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel): ResponseClass<BookingModel> {
        return apiService.getPendingBookingDetailsForUser(getIncomingBookingDetailsRequestModel)
    }

    override suspend fun getIncomingBookingDetailsForUser(getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel): ResponseClass<BookingModel> {
        return apiService.getIncomingBookingDetailsForUser(getIncomingBookingDetailsRequestModel)
    }

    override suspend fun getFinishedBookingDetailsForUser(booking_id: Int): ResponseClass<BookingModel> {
        return apiService.getFinishedBookingDetailsForUser(booking_id)
    }

    override suspend fun cancelBookingForUser(booking_id: Int): ResponseClassWithoutItems {
        return apiService.cancelBookingForUser(booking_id)
    }

    override suspend fun getIncomingBookingsForDoctor(date: String): ResponseClass<List<BookingModelForDoctor>> {
        return apiService.getIncomingBookingsForDoctor(date)
    }

    override suspend fun getFinishedBookingsForDoctor(): ResponseClass<List<BookingModelForDoctor>> {
        return apiService.getFinishedBookingsForDoctor()
    }

    override suspend fun getBookingsInfoForDoctor(booking_id: Int): ResponseClass<BookingModelForDoctor> {
        return apiService.getBookingsInfoForDoctor(booking_id)
    }

    override suspend fun cancelBookingForDoctor(
        booking_id: Int,
        reason: String,
        is_attended:String,
        status:String
    ): ResponseClassWithoutItems {
        return apiService.cancelBookingForDoctor(booking_id,reason,is_attended,status)
    }

    override suspend fun getPendingBookingsForDoctor(): ResponseClass<List<BookingModelForDoctor>> {
        return apiService.getPendingBookingsForDoctor()
    }

    override suspend fun approveBookingForDoctor(booking_id: Int,status:String): ResponseClass<List<BookingModelForDoctor>> {
        return apiService.approveBookingForDoctor(booking_id,status)
    }

    override suspend fun getAllDoctorRatingsForDoctor(): ResponseClass<List<CommentForUserModel>> {
        return apiService.getAllDoctorRatingsForDoctor()
    }

    override suspend fun getDoctorProfileForDoctor(): ResponseClass<DoctorProfileForUserResponse> {
        return apiService.getDoctorProfileForDoctor()
    }

    override suspend fun updateDoctorProfileForDoctor(
        bio: RequestBody,
        patient_examination_price: RequestBody,
        average_answer_time: RequestBody,
        waiting_time: RequestBody,
        address: RequestBody,
        lat: RequestBody,
        long: RequestBody,
        image: MultipartBody.Part?
    ): ResponseClassWithoutItems{
        return apiService.updateDoctorProfileForDoctor(bio, patient_examination_price, average_answer_time, waiting_time, address, lat, long, image)
    }

    override suspend fun getMedicinesAndPeriods(): ResponseClass<TreatmentsAndPeriodsResponseModel> {
        return apiService.getMedicinesAndPeriods()
    }

    override suspend fun addTreatmentForBooking(
        booking_id: Int,
        doctor_notes: String,
        data: String
    ): ResponseClassWithoutItems {
        return apiService.addTreatmentForBooking(booking_id, doctor_notes, data)
    }

    override suspend fun editTreatmentForBooking(
        booking_id: Int,
        doctor_notes: String,
        data: String
    ): ResponseClassWithoutItems {
        return apiService.editTreatmentForBooking(booking_id,doctor_notes,data)
    }

    override suspend fun searchForMedicineForDoctor(word: String): ResponseClass<List<MedicineModel>> {
        return apiService.searchForMedicineForDoctor(word)
    }

    override suspend fun addNewMedicineToList(
        name: String,
        description: String
    ): ResponseClass<MedicineModel> {
        return apiService.addNewMedicineToList(name, description)
    }

    override suspend fun getOldResultsToEditTreatment(booking_id: Int): ResponseClass<EditBookingTreatmentsResponseModel> {
        return apiService.getOldResultsToEditTreatment(booking_id)
    }

    override suspend fun getAllNotifications(): ResponseClass<List<NotificationModel>> {
        return apiService.getAllNotifications()
    }

}
package com.alsadimoh.graduationproject.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.alsadimoh.graduationproject.retrofit.util.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import java.lang.Exception

class MyViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    private val text = "MyViewModel"
    private val registerResponse = MutableLiveData<Resource<ResponseClass<RegisterResponseModel>>>()
    private val loginResponse = MutableLiveData<Resource<ResponseClass<LoginResponseModel>>>()
    private val logoutResponse = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val homeResponse = MutableLiveData<Resource<ResponseClass<HomePageModel>>>()
    private val specializationDoctorsResponse =
        MutableLiveData<Resource<ResponseClass<SpecializationDoctorsModel>>>()
    private val getAllCategoriesResponse =
        MutableLiveData<Resource<ResponseClass<List<SpecializationModel>>>>()
    private val updateProfileResponse =
        MutableLiveData<Resource<ResponseClass<UpdateProfileModel>>>()
    private val doctorProfileForUserResponse =
        MutableLiveData<Resource<ResponseClass<DoctorProfileForUserResponse>>>()
    private val getAllDoctorRatingsForUserResponse =
        MutableLiveData<Resource<ResponseClass<List<CommentForUserModel>>>>()
    private val putBookmarksResponse = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getUserProfileResponse =
        MutableLiveData<Resource<ResponseClass<GetUserProfileModel>>>()
    private val getBookmarksResponse =
        MutableLiveData<Resource<ResponseClass<List<BookmarksResponseModel>>>>()
    private val changePasswordForForgetPasswordResponse =
        MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val changePasswordResponse = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val searchWithWordsResponse =
        MutableLiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>>()
    private val searchWithFilterResponse =
        MutableLiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>>()
    private val allRatedDoctorsResponse =
        MutableLiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>>()
    private val getBookingsForDayResponse =
        MutableLiveData<Resource<ResponseClass<List<AvailableTimesForAddNewBooking>>>>()
    private val getAddNewBookingsResponse =
        MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getIncomingBookingsResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModel>>>>()
    private val getFinishedBookingsResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModel>>>>()
    private val getPendingBookingsResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModel>>>>()
    private val getIncomingBookingDetailsForUserResponse =
        MutableLiveData<Resource<ResponseClass<BookingModel>>>()
    private val getFinishedBookingDetailsForUserResponse =
        MutableLiveData<Resource<ResponseClass<BookingModel>>>()
    private val getCancelBookingForUser = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getEditBookingForUser = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val rateDoctorResponse = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getIncomingBookingsForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>>()
    private val getFinishedBookingsForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>>()
    private val getBookingsInfoForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<BookingModelForDoctor>>>()
    private val getCancelBookingForDoctor = MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getPendingBookingsForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>>()
    private val getApproveBookingForDoctor = MutableLiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>>()
    private val getAllDoctorRatingsForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<List<CommentForUserModel>>>>()
    private val doctorProfileForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<DoctorProfileForUserResponse>>>()
    private val updateDoctorProfileForDoctorResponse =
        MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getMedicineAndPeriodsResponse =
        MutableLiveData<Resource<ResponseClass<TreatmentsAndPeriodsResponseModel>>>()
    private val getAddTreatmentForBookingResponse =
        MutableLiveData<Resource<ResponseClassWithoutItems>>()
    private val getSearchForMedicineForDoctorResponse =
        MutableLiveData<Resource<ResponseClass<List<MedicineModel>>>>()
     private val getAddNewMedicineToListResponse =
        MutableLiveData<Resource<ResponseClass<MedicineModel>>>()
    private val getOldResultsToEditTreatmentResponse =
        MutableLiveData<Resource<ResponseClass<EditBookingTreatmentsResponseModel>>>()

    private val getAllNotificationsResponse =
        MutableLiveData<Resource<ResponseClass<List<NotificationModel>>>>()

    //===========================================================================//

    // register ---- Sign Up
    fun register(registerModel: RegisterModel) {
        viewModelScope.launch {
            registerResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.register(registerModel)
                if (data.status) {
                    registerResponse.postValue(Resource.success(data))
                } else {
                    registerResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "logInUser: ${ex.message}")
                registerResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getRegisterResponse(): LiveData<Resource<ResponseClass<RegisterResponseModel>>> {
        return registerResponse
    }

    //===========================================================================//

    // login ---- Sign in
    fun login(loginModel: LoginModel) {
        viewModelScope.launch {
            loginResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.login(loginModel)
                if (data.status) {
                    loginResponse.postValue(Resource.success(data))
                } else {
                    loginResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "logInUser: ${ex.message}")
                loginResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getLoginResponse(): LiveData<Resource<ResponseClass<LoginResponseModel>>> {
        return loginResponse
    }

    //===========================================================================//

    // home page user
    fun fetchUserHomepageData() {
        viewModelScope.launch {
            homeResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getUserHome()
                if (data.status) {
                    homeResponse.postValue(Resource.success(data))
                } else {
                    homeResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "fetchUserHomepageData: ${ex.message}")
                homeResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getUserHomePageResponse(): LiveData<Resource<ResponseClass<HomePageModel>>> {
        return homeResponse
    }

    //===========================================================================//

    // update profile for user
    fun updateUserProfile(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part image: MultipartBody.Part?
    ) {

        viewModelScope.launch {
            updateProfileResponse.postValue(Resource.loading(null))
            try {
                updateProfileResponse.postValue(
                    Resource.success(
                        apiHelper.updateUserProfile(
                            name,
                            gender,
                            dateOfBirth,
                            phone_number,
                            image
                        )
                    )
                )
            } catch (ex: Exception) {
                Log.e(text, "updateUserProfile: ${ex.message}")
                updateProfileResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }


    fun getUpdateUserProfileResponse(): LiveData<Resource<ResponseClass<UpdateProfileModel>>> {
        return updateProfileResponse
    }

    //===========================================================================//
    // logout
    fun logout() {
        viewModelScope.launch {
            logoutResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.logout()
                if (data.status) {
                    logoutResponse.postValue(Resource.success(data))
                } else {
                    logoutResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "logout: ${ex.message}")
                logoutResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getLogoutResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return logoutResponse
    }

    //===========================================================================//
// Specialization Doctors
    fun getSpecializationDoctors(id: Int) {
        viewModelScope.launch {
            specializationDoctorsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getDoctorsOfCategory(id)
                if (data.status) {
                    specializationDoctorsResponse.postValue(Resource.success(data))
                } else {
                    specializationDoctorsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "fetchUserHomepageData: ${ex.message}")
                specializationDoctorsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getSpecializationDoctorsResponse(): LiveData<Resource<ResponseClass<SpecializationDoctorsModel>>> {
        return specializationDoctorsResponse
    }

    //===========================================================================//


    //Specialization Doctors
    fun getAllSpecializations() {
        viewModelScope.launch {
            getAllCategoriesResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getAllCategories()
                if (data.status) {
                    getAllCategoriesResponse.postValue(Resource.success(data))
                } else {
                    getAllCategoriesResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "fetchAllSpecializations: ${ex.message}")
                getAllCategoriesResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getAllSpecializationResponse(): LiveData<Resource<ResponseClass<List<SpecializationModel>>>> {
        return getAllCategoriesResponse
    }

    //===========================================================================//

    // بروفايل الدكتور بالنسبة للمستخدم
    fun getDoctorProfileForUser(doctorProfileForUserRequestModel: DoctorProfileForUserRequestModel) {
        viewModelScope.launch {
            doctorProfileForUserResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getDoctorProfileForUser(doctorProfileForUserRequestModel)
                if (data.status) {
                    doctorProfileForUserResponse.postValue(Resource.success(data))
                } else {
                    doctorProfileForUserResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getDoctorProfileForUser: ${ex.message}")
                doctorProfileForUserResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getDoctorProfileForUserResponse(): LiveData<Resource<ResponseClass<DoctorProfileForUserResponse>>> {
        return doctorProfileForUserResponse
    }
//===========================================================================//

    // تعليقات المستخددمين على الدكتور بالنسبة للمستخدم

    fun getAllDoctorRatingsForUser(doctorId: Int) {
        viewModelScope.launch {
            getAllDoctorRatingsForUserResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getAllDoctorRatingsForUser(doctorId)
                if (data.status) {
                    getAllDoctorRatingsForUserResponse.postValue(Resource.success(data))
                } else {
                    getAllDoctorRatingsForUserResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getAllDoctorRatingsForUser: ${ex.message}")
                getAllDoctorRatingsForUserResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getAllDoctorRatingsForUserResponse(): LiveData<Resource<ResponseClass<List<CommentForUserModel>>>> {
        return getAllDoctorRatingsForUserResponse
    }
//===========================================================================//


    fun putUserInBookmarksForUser(doctorId: Int) {
        viewModelScope.launch {
            putBookmarksResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.putInBookmarksForUser(doctorId)
                if (data.status) {
                    putBookmarksResponse.postValue(Resource.success(data))
                } else {
                    putBookmarksResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "putUserInBookmarksForUser: ${ex.message}")
                putBookmarksResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun putUserInBookmarksForUserResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return putBookmarksResponse
    }

    //===========================================================================//


    fun getUserProfile() {
        viewModelScope.launch {
            getUserProfileResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getUserProfile()
                if (data.status) {
                    getUserProfileResponse.postValue(Resource.success(data))
                } else {
                    getUserProfileResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getUserProfile: ${ex.message}")
                getUserProfileResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getUserProfileResponse(): LiveData<Resource<ResponseClass<GetUserProfileModel>>> {
        return getUserProfileResponse
    }

    //===========================================================================//


    fun getBookmarks() {
        viewModelScope.launch {
            getBookmarksResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getUserBookmarks()
                if (data.status) {
                    getBookmarksResponse.postValue(Resource.success(data))
                } else {
                    getBookmarksResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getBookmarksResponse: ${ex.message}")
                getBookmarksResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getBookmarksResponse(): LiveData<Resource<ResponseClass<List<BookmarksResponseModel>>>> {
        return getBookmarksResponse
    }

    //===========================================================================//


    fun changePasswordForForgetPassword(forgetPasswordModel: ForgetPasswordModel) {
        viewModelScope.launch {
            changePasswordForForgetPasswordResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.changePasswordForForgetPassword(forgetPasswordModel)
                if (data.status) {
                    changePasswordForForgetPasswordResponse.postValue(Resource.success(data))
                } else {
                    changePasswordForForgetPasswordResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "changePasswordForForgetPassword: ${ex.message}")
                changePasswordForForgetPasswordResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun changePasswordForForgetPasswordResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return changePasswordForForgetPasswordResponse
    }

    //===========================================================================//


    fun changePassword(changePasswordModel: ChangePasswordModel) {
        viewModelScope.launch {
            changePasswordResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.changePassword(changePasswordModel)
                if (data.status) {
                    changePasswordResponse.postValue(Resource.success(data))
                } else {
                    changePasswordResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "changePasswordForForgetPassword: ${ex.message}")
                changePasswordResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun changePasswordResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return changePasswordResponse
    }

    //===========================================================================//

    fun searchWithWords(words: String) {
        viewModelScope.launch {
            searchWithWordsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.searchWithOnlyWords(words)
                if (data.status) {
                    searchWithWordsResponse.postValue(Resource.success(data))
                } else {
                    searchWithWordsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "searchWithWords: ${ex.message}")
                searchWithWordsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun searchWithWordsResponse(): LiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>> {
        return searchWithWordsResponse
    }
//===========================================================================//


    fun searchWithFilter(
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
    ) {
        viewModelScope.launch {
            searchWithFilterResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.searchWithFilter(
                    specialization,
                    gender,
                    waiting_min,
                    waiting_max,
                    price_min,
                    price_max,
                    lat,
                    long,
                    sort_by,
                    sort
                )
                if (data.status) {
                    searchWithFilterResponse.postValue(Resource.success(data))
                } else {
                    searchWithFilterResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "searchWithFilter: ${ex.message}")
                searchWithFilterResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun searchWithFilterResponse(): LiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>> {
        return searchWithFilterResponse
    }

    //===========================================================================//
    fun getAllRatedDoctors(page: String) {
        viewModelScope.launch {
            allRatedDoctorsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getAllRatedDoctors(page)
                if (data.status) {
                    allRatedDoctorsResponse.postValue(Resource.success(data))
                } else {
                    allRatedDoctorsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getAllRatedDoctors: ${ex.message}")
                allRatedDoctorsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getAllRatedDoctorsResponse(): LiveData<Resource<ResponseClass<List<DoctorModelForUserHome>>>> {
        return allRatedDoctorsResponse
    }

    //===========================================================================//
    fun getBookingsForDay(getBookingsForDay: GetBookingsForDay) {
        viewModelScope.launch {
            getBookingsForDayResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getBookingsForDay(getBookingsForDay)
                if (data.status) {
                    getBookingsForDayResponse.postValue(Resource.success(data))
                } else {
                    getBookingsForDayResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getBookingsForDay: ${ex.message}")
                getBookingsForDayResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getBookingsForDayResponse(): LiveData<Resource<ResponseClass<List<AvailableTimesForAddNewBooking>>>> {
        return getBookingsForDayResponse
    }
//===========================================================================//

    fun addNewBookings(newBookingRequestModel: NewBookingRequestModel) {
        viewModelScope.launch {
            getAddNewBookingsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.startNewBooking(newBookingRequestModel)
                if (data.status) {
                    getAddNewBookingsResponse.postValue(Resource.success(data))
                } else {
                    getAddNewBookingsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getAddNewBookings: ${ex.message}")
                getAddNewBookingsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getAddNewBookingsResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return getAddNewBookingsResponse
    }
//===========================================================================//

    fun getIncomingBookings() {
        viewModelScope.launch {
            getIncomingBookingsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getIncomingBookingForUser()
                if (data.status) {
                    getIncomingBookingsResponse.postValue(Resource.success(data))
                } else {
                    getIncomingBookingsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getIncomingBookings: ${ex.message}")
                getIncomingBookingsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getIncomingBookingsResponse(): LiveData<Resource<ResponseClass<List<BookingModel>>>> {
        return getIncomingBookingsResponse
    }
//===========================================================================//


    fun getFinishedBookings() {
        viewModelScope.launch {
            getFinishedBookingsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getFinishedBookingForUser()
                if (data.status) {
                    getFinishedBookingsResponse.postValue(Resource.success(data))
                } else {
                    getFinishedBookingsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getFinishedBookings: ${ex.message}")
                getFinishedBookingsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getFinishedBookingsResponse(): LiveData<Resource<ResponseClass<List<BookingModel>>>> {
        return getFinishedBookingsResponse
    }
//===========================================================================//

    fun getPendingBookings() {
        viewModelScope.launch {
            getPendingBookingsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getPendingBookingForUser()
                if (data.status) {
                    getPendingBookingsResponse.postValue(Resource.success(data))
                } else {
                    getPendingBookingsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getPendingBookings: ${ex.message}")
                getPendingBookingsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getPendingBookingsResponse(): LiveData<Resource<ResponseClass<List<BookingModel>>>> {
        return getPendingBookingsResponse
    }
//===========================================================================//

    fun getIncomingBookingDetailsForUser(getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel) {
        viewModelScope.launch {
            getIncomingBookingDetailsForUserResponse.postValue(Resource.loading(null))
            try {
                val data =
                    apiHelper.getIncomingBookingDetailsForUser(getIncomingBookingDetailsRequestModel)
                if (data.status) {
                    getIncomingBookingDetailsForUserResponse.postValue(Resource.success(data))
                } else {
                    getIncomingBookingDetailsForUserResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getIncomingBookingDetailsForUser: ${ex.message}")
                getIncomingBookingDetailsForUserResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getPendingBookingDetailsForUser(getIncomingBookingDetailsRequestModel: GetIncomingBookingDetailsRequestModel) {
        viewModelScope.launch {
            getIncomingBookingDetailsForUserResponse.postValue(Resource.loading(null))
            try {
                val data =
                    apiHelper.getPendingBookingDetailsForUser(getIncomingBookingDetailsRequestModel)
                if (data.status) {
                    getIncomingBookingDetailsForUserResponse.postValue(Resource.success(data))
                } else {
                    getIncomingBookingDetailsForUserResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getPendingBookingDetailsForUser: ${ex.message}")
                getIncomingBookingDetailsForUserResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getIncomingBookingDetailsForUserResponse(): LiveData<Resource<ResponseClass<BookingModel>>> {
        return getIncomingBookingDetailsForUserResponse
    }

    //===========================================================================//
    fun getFinishedBookingDetailsForUser(booking_id: Int) {
        viewModelScope.launch {
            getFinishedBookingDetailsForUserResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getFinishedBookingDetailsForUser(booking_id)
                if (data.status) {
                    getFinishedBookingDetailsForUserResponse.postValue(Resource.success(data))
                } else {
                    getFinishedBookingDetailsForUserResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getFinishedBookingDetailsForUser: ${ex.message}")
                getFinishedBookingDetailsForUserResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getFinishedBookingDetailsForUserResponse(): LiveData<Resource<ResponseClass<BookingModel>>> {
        return getFinishedBookingDetailsForUserResponse
    }
//===========================================================================//

    fun cancelBookingForUser(booking_id: Int) {
        viewModelScope.launch {
            getCancelBookingForUser.postValue(Resource.loading(null))
            try {
                val data = apiHelper.cancelBookingForUser(booking_id)
                if (data.status) {
                    getCancelBookingForUser.postValue(Resource.success(data))
                } else {
                    getCancelBookingForUser.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "cancelBookingForUser: ${ex.message}")
                getCancelBookingForUser.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getCancelBookingForUser(): LiveData<Resource<ResponseClassWithoutItems>> {
        return getCancelBookingForUser
    }
//===========================================================================//

 fun getEditBookingForUser(editBookingRequestModel: EditBookingRequestModel) {
        viewModelScope.launch {
            getEditBookingForUser.postValue(Resource.loading(null))
            try {
                val data = apiHelper.editBookingForUser(editBookingRequestModel)
                if (data.status) {
                    getEditBookingForUser.postValue(Resource.success(data))
                } else {
                    getEditBookingForUser.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getEditBookingForUser: ${ex.message}")
                getEditBookingForUser.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getEditBookingForUserResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return getEditBookingForUser
    }
//===========================================================================//

 fun rateDoctor(rateDoctorRequestModel: RateDoctorRequestModel) {
        viewModelScope.launch {
            rateDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.rateDoctor(rateDoctorRequestModel)
                if (data.status) {
                    rateDoctorResponse.postValue(Resource.success(data))
                } else {
                    rateDoctorResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "rateDoctor: ${ex.message}")
                rateDoctorResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getRateDoctorResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return rateDoctorResponse
    }

//===========================================================================//


    fun getIncomingBookingForDoctor(date: String) {
        viewModelScope.launch {
            getIncomingBookingsForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getIncomingBookingsForDoctor(date)
                if (data.status) {
                    getIncomingBookingsForDoctorResponse.postValue(Resource.success(data))
                } else {
                    getIncomingBookingsForDoctorResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getIncomingBookingForDoctor: ${ex.message}")
                getIncomingBookingsForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getIncomingBookingForDoctorResponse(): LiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>> {
        return getIncomingBookingsForDoctorResponse
    }
//===========================================================================//


    fun getFinishedBookingForDoctor() {
        viewModelScope.launch {
            getFinishedBookingsForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getFinishedBookingsForDoctor()
                if (data.status) {
                    getFinishedBookingsForDoctorResponse.postValue(Resource.success(data))
                } else {
                    getFinishedBookingsForDoctorResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getFinishedBookingsForDoctor: ${ex.message}")
                getFinishedBookingsForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getFinishedBookingsForDoctorResponse(): LiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>> {
        return getFinishedBookingsForDoctorResponse
    }
//===========================================================================//


    fun getBookingsInfoForDoctor(booking_id: Int) {
        viewModelScope.launch {
            getBookingsInfoForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getBookingsInfoForDoctor(booking_id)
                if (data.status) {
                    getBookingsInfoForDoctorResponse.postValue(Resource.success(data))
                } else {
                    getBookingsInfoForDoctorResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getBookingsInfoForDoctor: ${ex.message}")
                getBookingsInfoForDoctorResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getBookingsInfoForDoctorResponse(): LiveData<Resource<ResponseClass<BookingModelForDoctor>>> {
        return getBookingsInfoForDoctorResponse
    }
//===========================================================================//

    fun cancelBookingForDoctor(booking_id: Int, reason: String,is_attended:String,status:String) {
        viewModelScope.launch {
            getCancelBookingForDoctor.postValue(Resource.loading(null))
            try {
                val data = apiHelper.cancelBookingForDoctor(booking_id, reason,is_attended,status)
                if (data.status) {
                    getCancelBookingForDoctor.postValue(Resource.success(data))
                } else {
                    getCancelBookingForDoctor.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "cancelBookingForDoctor: ${ex.message}")
                getCancelBookingForDoctor.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getCancelBookingForDoctorResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return getCancelBookingForDoctor
    }
//===========================================================================//


    fun getPendingBookingForDoctor() {
        viewModelScope.launch {
            getPendingBookingsForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getPendingBookingsForDoctor()
                if (data.status) {
                    getPendingBookingsForDoctorResponse.postValue(Resource.success(data))
                } else {
                    getPendingBookingsForDoctorResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getFinishedBookingsForDoctor: ${ex.message}")
                getPendingBookingsForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getPendingBookingsForDoctorResponse(): LiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>> {
        return getPendingBookingsForDoctorResponse
    }
//===========================================================================//

    fun approveBookingForDoctor(booking_id: Int,status: String) {
        viewModelScope.launch {
            getApproveBookingForDoctor.postValue(Resource.loading(null))
            try {
                val data = apiHelper.approveBookingForDoctor(booking_id,status)
                if (data.status) {
                    getApproveBookingForDoctor.postValue(Resource.success(data))
                } else {
                    getApproveBookingForDoctor.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getApproveBookingForDoctor: ${ex.message}")
                getApproveBookingForDoctor.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getApproveBookingForDoctorResponse(): LiveData<Resource<ResponseClass<List<BookingModelForDoctor>>>> {
        return getApproveBookingForDoctor
    }
//===========================================================================//

    fun getAllDoctorRatingsForDoctor() {
        viewModelScope.launch {
            getAllDoctorRatingsForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getAllDoctorRatingsForDoctor()
                if (data.status) {
                    getAllDoctorRatingsForDoctorResponse.postValue(Resource.success(data))
                } else {
                    getAllDoctorRatingsForDoctorResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "getAllDoctorRatingsForDoctorResponse: ${ex.message}")
                getAllDoctorRatingsForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getAllDoctorRatingsForDoctorResponse(): LiveData<Resource<ResponseClass<List<CommentForUserModel>>>> {
        return getAllDoctorRatingsForDoctorResponse
    }
//===========================================================================//

    fun getDoctorProfileForDoctor() {
        viewModelScope.launch {
            doctorProfileForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getDoctorProfileForDoctor()
                if (data.status) {
                    doctorProfileForDoctorResponse.postValue(Resource.success(data))
                } else {
                    doctorProfileForDoctorResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "doctorProfileForDoctorResponse: ${ex.message}")
                doctorProfileForDoctorResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getDoctorProfileForDoctorResponse(): LiveData<Resource<ResponseClass<DoctorProfileForUserResponse>>> {
        return doctorProfileForDoctorResponse
    }
//===========================================================================//


    fun updateDoctorProfileForDoctor(
        @Part("bio") bio: RequestBody,
        @Part("patient_examination_price") patient_examination_price: RequestBody,
        @Part("average_answer_time") average_answer_time: RequestBody,
        @Part("waiting_time") waiting_time: RequestBody,
        @Part("address") address: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            updateDoctorProfileForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.updateDoctorProfileForDoctor(
                    bio,
                    patient_examination_price,
                    average_answer_time,
                    waiting_time,
                    address,
                    lat,
                    long,
                    image
                )
                if (data.status) {
                    updateDoctorProfileForDoctorResponse.postValue(Resource.success(data))
                } else {
                    updateDoctorProfileForDoctorResponse.postValue(
                        Resource.error(
                            data.message,
                            null
                        )
                    )
                }

            } catch (ex: Exception) {
                Log.e(text, "updateDoctorProfileForDoctor: ${ex.message}")
                updateDoctorProfileForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun updateDoctorProfileForDoctorResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return updateDoctorProfileForDoctorResponse
    }
//===========================================================================//

    fun getMedicineAndPeriods() {
        viewModelScope.launch {
            getMedicineAndPeriodsResponse.postValue(Resource.loading(null))
            try {
                val data = apiHelper.getMedicinesAndPeriods()
                if (data.status) {
                    getMedicineAndPeriodsResponse.postValue(Resource.success(data))
                } else {
                    getMedicineAndPeriodsResponse.postValue(Resource.error(data.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getMedicineAndPeriods: ${ex.message}")
                getMedicineAndPeriodsResponse.postValue(Resource.error("حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!", null))
            }
        }
    }

    fun getMedicineAndPeriodsResponse(): LiveData<Resource<ResponseClass<TreatmentsAndPeriodsResponseModel>>> {
        return getMedicineAndPeriodsResponse
    }

    //===========================================================================//
    fun addTreatmentForBooking(booking_id: Int, doctor_notes: String, data: String) {
        viewModelScope.launch {
            getAddTreatmentForBookingResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.addTreatmentForBooking(booking_id, doctor_notes, data)
                if (data2.status) {
                    getAddTreatmentForBookingResponse.postValue(Resource.success(data2))
                } else {
                    getAddTreatmentForBookingResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "addTreatmentForBooking: ${ex.message}")
                getAddTreatmentForBookingResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun editTreatmentForBooking(booking_id: Int, doctor_notes: String, data: String) {
        viewModelScope.launch {
            getAddTreatmentForBookingResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.editTreatmentForBooking(booking_id, doctor_notes, data)
                if (data2.status) {
                    getAddTreatmentForBookingResponse.postValue(Resource.success(data2))
                } else {
                    getAddTreatmentForBookingResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "addTreatmentForBooking: ${ex.message}")
                getAddTreatmentForBookingResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getAddTreatmentForBookingResponse(): LiveData<Resource<ResponseClassWithoutItems>> {
        return getAddTreatmentForBookingResponse
    }
//===========================================================================//

  fun searchForMedicineForDoctor(words: String) {
        viewModelScope.launch {
            getSearchForMedicineForDoctorResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.searchForMedicineForDoctor(words)
                if (data2.status) {
                    getSearchForMedicineForDoctorResponse.postValue(Resource.success(data2))
                } else {
                    getSearchForMedicineForDoctorResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "searchForMedicineForDoctor: ${ex.message}")
                getSearchForMedicineForDoctorResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getSearchForMedicineForDoctorResponse(): LiveData<Resource<ResponseClass<List<MedicineModel>>>> {
        return getSearchForMedicineForDoctorResponse
    }
//===========================================================================//
 fun addNewMedicineToList(name:String,description:String) {
        viewModelScope.launch {
            getAddNewMedicineToListResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.addNewMedicineToList(name, description)
                if (data2.status) {
                    getAddNewMedicineToListResponse.postValue(Resource.success(data2))
                } else {
                    getAddNewMedicineToListResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "addNewMedicineToList: ${ex.message}")
                getAddNewMedicineToListResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getAddNewMedicineToListResponse(): LiveData<Resource<ResponseClass<MedicineModel>>> {
        return getAddNewMedicineToListResponse
    }
//===========================================================================//
 fun getOldResultsToEditTreatment(booking_id:Int) {
        viewModelScope.launch {
            getOldResultsToEditTreatmentResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.getOldResultsToEditTreatment(booking_id)
                if (data2.status) {
                    getOldResultsToEditTreatmentResponse.postValue(Resource.success(data2))
                } else {
                    getOldResultsToEditTreatmentResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getOldResultsToEditTreatment: ${ex.message}")
                getOldResultsToEditTreatmentResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getOldResultsToEditTreatmentResponse(): LiveData<Resource<ResponseClass<EditBookingTreatmentsResponseModel>>> {
        return getOldResultsToEditTreatmentResponse
    }
//===========================================================================//
 fun getAllNotifications() {
        viewModelScope.launch {
            getAllNotificationsResponse.postValue(Resource.loading(null))
            try {
                val data2 = apiHelper.getAllNotifications()
                if (data2.status) {
                    getAllNotificationsResponse.postValue(Resource.success(data2))
                } else {
                    getAllNotificationsResponse.postValue(Resource.error(data2.message, null))
                }

            } catch (ex: Exception) {
                Log.e(text, "getAllNotifications: ${ex.message}")
                getAllNotificationsResponse.postValue(
                    Resource.error(
                        "حدث خطأ ما، يرجى اعادة المحاولة لاحقاً!",
                        null
                    )
                )
            }
        }
    }

    fun getAllNotificationsResponse(): LiveData<Resource<ResponseClass<List<NotificationModel>>>> {
        return getAllNotificationsResponse
    }
//===========================================================================//


}
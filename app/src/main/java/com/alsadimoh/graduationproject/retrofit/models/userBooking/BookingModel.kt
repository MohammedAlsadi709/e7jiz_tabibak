package com.alsadimoh.graduationproject.retrofit.models.userBooking

import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome

data class BookingModel(val id:Int,val user_id:Int,val doctor_id:Int,val date:String,val time:String,val price:Double,val doctor_notes:String,val status:String,val deleted_at:String?,val doctor:DoctorModelForUserHome, val treatments: List<TreatmentsModel>)
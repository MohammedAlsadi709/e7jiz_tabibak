package com.alsadimoh.graduationproject.retrofit.models.userBooking

data class NewBookingResponseModel(val id:Int, val doctor_id:Int, val user_id:Int, val price:Double, val date:String, val time:String, val status:String)

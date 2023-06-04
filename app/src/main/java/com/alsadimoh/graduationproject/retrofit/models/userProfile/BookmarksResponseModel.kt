package com.alsadimoh.graduationproject.retrofit.models.userProfile

import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome

data class BookmarksResponseModel(val doctor_id:Int,val deleted_at:String,val doctor:DoctorModelForUserHome)
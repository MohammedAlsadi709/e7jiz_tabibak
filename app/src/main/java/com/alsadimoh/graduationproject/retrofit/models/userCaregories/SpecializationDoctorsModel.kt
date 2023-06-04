package com.alsadimoh.graduationproject.retrofit.models.userCaregories

import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome

data class SpecializationDoctorsModel(val id:Int, val title:String, val image:String, val description:String, val created_at:String, val updated_at:String, val deleted_at:String,val doctors:List<DoctorModelForUserHome>)

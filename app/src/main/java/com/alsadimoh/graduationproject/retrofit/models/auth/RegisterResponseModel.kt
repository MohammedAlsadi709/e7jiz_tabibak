package com.alsadimoh.graduationproject.retrofit.models.auth

data class RegisterResponseModel(var name : String,var gender:String,var dateOfBirth:String,var phone_number:String,var username:String,var id:Int,var token:String,val is_registered:Boolean,val user_type:String)
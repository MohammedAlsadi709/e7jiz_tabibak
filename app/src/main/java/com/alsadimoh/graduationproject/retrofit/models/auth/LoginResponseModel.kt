package com.alsadimoh.graduationproject.retrofit.models.auth

data class LoginResponseModel(val id:Int, val name:String, val username:String, val dateOfBirth:String, val phone_number:String, val gender:String, val image:String, val fb_token:String,val token:String, val is_registered:Boolean,val user_type:String)

package com.alsadimoh.graduationproject.retrofit.models.userDoctors

import com.alsadimoh.graduationproject.retrofit.models.userHome.ClinicModel
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel

data class DoctorProfileForUserResponse(val id:Int, val name:String, val username:String, val phone_number:String, val gender:String, val image:String, val bio:String, val specialization_id:Int, val patient_examination_price:Double, val rate:Float, val average_answer_time:Double, val waiting_time:Double, val address:String, val lat:String, val long:String, val clinic_id:Int, val deleted_at:String?,val distance:String,val is_bookmark:Boolean, val num_ratings:Int,val spec:String,val clin:String, val worktime:List<WorkTimeModel>, val ratings:List<CommentForUserModel>, val clinic:ClinicModel,val specialization:SpecializationModel)

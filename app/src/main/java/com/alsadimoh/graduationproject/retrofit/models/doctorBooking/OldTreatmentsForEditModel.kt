package com.alsadimoh.graduationproject.retrofit.models.doctorBooking


data class OldTreatmentsForEditModel(val id:Int, val user_id:Int, val doctor_id:Int, val date:String, val time:String, val price:Float, val doctor_notes:String, val status:String, val deleted_at:String?, val treatments:List<OldTreatmentModel>)

package com.alsadimoh.graduationproject.retrofit.models.doctorBooking

import com.alsadimoh.graduationproject.retrofit.models.userBooking.MedicineModel

data class OldTreatmentModel(val id:Int, val user_id:Int, val doctor_id:Int, val medicine_id:Int, val duration:Int, val treatment_period_id:Int, val status:String, val booking_id:Int, val deleted_at:String?, val medicine : MedicineModel, val treatment_period:TreatmentPeriodModel)

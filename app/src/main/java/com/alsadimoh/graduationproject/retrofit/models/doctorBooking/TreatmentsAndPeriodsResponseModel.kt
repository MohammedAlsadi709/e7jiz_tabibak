package com.alsadimoh.graduationproject.retrofit.models.doctorBooking

import com.alsadimoh.graduationproject.retrofit.models.userBooking.MedicineModel

data class TreatmentsAndPeriodsResponseModel(val medicines : List<MedicineModel>, val treatmentPeriods:List<TreatmentPeriodModel>)

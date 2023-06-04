package com.alsadimoh.graduationproject.retrofit.models.userBooking

data class CalenderDaysAdapterModel(
    val id: Int,
    var date: String,
    val dayNumber: Int,
    var isSelected: Boolean = false
)

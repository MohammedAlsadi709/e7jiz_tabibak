package com.alsadimoh.graduationproject.retrofit.models.doctorProfile

import android.os.Parcel
import android.os.Parcelable

data class UpdateDoctorProfile(
    val bio:String,
    val waitForAnswer: Double,
    val waitingTime: Double,
    val address:String,
    val price: Double,
    val lat:String,
    val long:String,
    val image:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bio)
        parcel.writeDouble(waitForAnswer)
        parcel.writeDouble(waitingTime)
        parcel.writeString(address)
        parcel.writeDouble(price)
        parcel.writeString(lat)
        parcel.writeString(long)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateDoctorProfile> {
        override fun createFromParcel(parcel: Parcel): UpdateDoctorProfile {
            return UpdateDoctorProfile(parcel)
        }

        override fun newArray(size: Int): Array<UpdateDoctorProfile?> {
            return arrayOfNulls(size)
        }
    }
}

package com.alsadimoh.graduationproject.retrofit.models.auth

import android.os.Parcel
import android.os.Parcelable

data class RegisterModel(var name:String,var gender:String,var dateOfBirth:String,var phone_number:String,var password:String?):Parcelable {

    var id:Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeString(phone_number)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegisterModel> {
        override fun createFromParcel(parcel: Parcel): RegisterModel {
            return RegisterModel(parcel)
        }

        override fun newArray(size: Int): Array<RegisterModel?> {
            return arrayOfNulls(size)
        }
    }
}

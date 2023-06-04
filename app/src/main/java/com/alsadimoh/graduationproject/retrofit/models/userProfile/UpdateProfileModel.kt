package com.alsadimoh.graduationproject.retrofit.models.userProfile

import android.os.Parcel
import android.os.Parcelable

data class UpdateProfileModel(val name: String,
                              val gender: String,
                              val dateOfBirth: String,
                              val phone_number: String,
                              val image: String):Parcelable{
    var id:Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeString(phone_number)
        parcel.writeString(image)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateProfileModel> {
        override fun createFromParcel(parcel: Parcel): UpdateProfileModel {
            return UpdateProfileModel(parcel)
        }

        override fun newArray(size: Int): Array<UpdateProfileModel?> {
            return arrayOfNulls(size)
        }
    }
}
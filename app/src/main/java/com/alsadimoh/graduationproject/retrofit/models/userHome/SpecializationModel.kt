package com.alsadimoh.graduationproject.retrofit.models.userHome

import android.os.Parcel
import android.os.Parcelable

data class SpecializationModel(val id:Int, val title:String, val image:String, val description:String, val created_at:String, val updated_at:String, val deleted_at:String,var isSelected :Boolean=false):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeString(deleted_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpecializationModel> {
        override fun createFromParcel(parcel: Parcel): SpecializationModel {
            return SpecializationModel(parcel)
        }

        override fun newArray(size: Int): Array<SpecializationModel?> {
            return arrayOfNulls(size)
        }
    }
}

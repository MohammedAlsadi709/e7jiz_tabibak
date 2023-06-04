package com.alsadimoh.graduationproject.retrofit.models.userHome

import android.os.Parcel
import android.os.Parcelable
import com.alsadimoh.graduationproject.retrofit.models.userBooking.TreatmentsModel

data class DoctorModelForUserHome(
    val id: Int,
    val name: String,
    val username: String,
    val phone_number: String,
    val gender: String,
    val image: String,
    val bio: String,
    val specialization_id: Int,
    val patient_examination_price: Int,
    val rate: Double,
    val average_answer_time: Double,
    val waiting_time: Int,
    val address: String,
    val distance:String?,
    val lat: Double,
    val long: Double,
    val clinic_id: Int,
    val deleted_at: String,
    val spec: String,
    val clin: String,
    val specialization: SpecializationModel,
    val clinic: ClinicModel
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(SpecializationModel::class.java.classLoader)!!,
        parcel.readParcelable(ClinicModel::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(phone_number)
        parcel.writeString(gender)
        parcel.writeString(image)
        parcel.writeString(bio)
        parcel.writeInt(specialization_id)
        parcel.writeInt(patient_examination_price)
        parcel.writeDouble(rate)
        parcel.writeDouble(average_answer_time)
        parcel.writeInt(waiting_time)
        parcel.writeString(address)
        parcel.writeString(distance)
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
        parcel.writeInt(clinic_id)
        parcel.writeString(deleted_at)
        parcel.writeString(spec)
        parcel.writeString(clin)
        parcel.writeParcelable(specialization, flags)
        parcel.writeParcelable(clinic, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DoctorModelForUserHome> {
        override fun createFromParcel(parcel: Parcel): DoctorModelForUserHome {
            return DoctorModelForUserHome(parcel)
        }

        override fun newArray(size: Int): Array<DoctorModelForUserHome?> {
            return arrayOfNulls(size)
        }
    }
}
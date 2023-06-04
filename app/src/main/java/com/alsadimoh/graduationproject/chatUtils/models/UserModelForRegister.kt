package com.alsadimoh.graduationproject.chatUtils.models

import android.os.Parcel
import android.os.Parcelable

class UserModelForRegister :Parcelable{

    var id: String? = null
    var userId: String? = null
    var username: String? = null
    var email: String? = null
    var image: String? = null
    var userType: String? = null
    var lastMessageBody: String? = null
    var lastMessageTime: String? = null
    var roomId: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        userId = parcel.readString()
        username = parcel.readString()
        email = parcel.readString()
        image = parcel.readString()
        userType = parcel.readString()
        lastMessageBody = parcel.readString()
        lastMessageTime = parcel.readString()
        roomId = parcel.readString()
    }


    constructor() {}

    constructor(
        id:String?,
        userId: String?,
        username: String,
        email: String,
        image: String,
        lastMessageBody: String?,
        lastMessageTime: String?,
        roomId: String?,
        userType: String?
    ) {
        this.id = id
        this.userId = userId
        this.username = username
        this.email = email
        this.image = image
        this.userType = userType
        this.lastMessageBody = lastMessageBody
        this.lastMessageTime = lastMessageTime
        this.roomId = roomId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(userType)
        parcel.writeString(lastMessageBody)
        parcel.writeString(lastMessageTime)
        parcel.writeString(roomId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModelForRegister> {
        override fun createFromParcel(parcel: Parcel): UserModelForRegister {
            return UserModelForRegister(parcel)
        }

        override fun newArray(size: Int): Array<UserModelForRegister?> {
            return arrayOfNulls(size)
        }
    }

}
package com.alsadimoh.graduationproject.chatUtils.models

class MessageModel {
    var profilePicture: String? = null
    var message: String? = null
    var senderId: String? = null
    var time: String? = null
    var date: String? = null
    var isImage: Boolean = false
    var isSentSuccessfully: Boolean = false

    constructor(){}

    constructor(profilePicture: String?,message: String?,senderId: String?,time: String?,date: String?,isImage: Boolean){
        this.profilePicture = profilePicture
        this.message = message
        this.senderId = senderId
        this.time = time
        this.date = date
        this.isImage = isImage
    }

}
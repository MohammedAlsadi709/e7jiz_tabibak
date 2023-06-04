package com.alsadimoh.graduationproject.chatUtils.models

class ChattedUsers {

    var id: String? = null
    var userId: String? = null
    var receiverId: String? = null
    var lastMessageBody: String? = null
    var lastMessageTime: String? = null
    var roomId: String? = null


    constructor() {}

    constructor(
        id:String?,
        userId: String,
        receiverId: String,
        lastMessageBody: String?,
        lastMessageTime: String?,
        roomId: String?,
    ) {
        this.id = id
        this.userId = userId
        this.lastMessageBody = lastMessageBody
        this.roomId = roomId
        this.receiverId = receiverId
        this.lastMessageTime = lastMessageTime
    }

}
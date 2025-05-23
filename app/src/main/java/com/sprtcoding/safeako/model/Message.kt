package com.sprtcoding.safeako.model

import java.util.Date

data class Message(
    var messageId: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var receiverName: String? = null,
    var senderName: String? = null,
    var message: String? = null,
    var receivedOn: Date? = null,
    var sentOn: Date? = null
) {
    constructor() : this(null, null, null, null, null, null, null, null)
}

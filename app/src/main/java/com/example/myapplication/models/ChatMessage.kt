package com.example.myapplication.models

import java.time.chrono.ChronoLocalDateTime
import java.util.Date

data class ChatMessage(
    var senderId:String? = "",
    var receiverId:String? = "",
    var message: String? = "",
    var dateTime: String? = "",
    var dateObject: Date? = Date()
)
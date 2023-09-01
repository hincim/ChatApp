package com.example.myapplication.models

import java.time.chrono.ChronoLocalDateTime

data class ChatMessage(
    val senderId:String,
    val receiverId:String,
    val message: String,
    val dateTime: String
)
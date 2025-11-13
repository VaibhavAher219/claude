package com.sakhi.chat.model

data class Message(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "",
    val botType: BotType? = null
)

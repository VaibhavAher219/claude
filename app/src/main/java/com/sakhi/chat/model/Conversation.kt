package com.sakhi.chat.model

data class Conversation(
    val id: String = "",
    val botType: BotType,
    val title: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

package com.sakhi.chat.model

data class GroupChat(
    val id: String = "",
    val name: String,
    val description: String = "",
    val members: List<String> = emptyList(), // User IDs
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)

data class GroupMessage(
    val id: Long = System.currentTimeMillis(),
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

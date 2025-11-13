package com.sakhi.chat.model

data class UserStatus(
    val id: String = "",
    val userId: String,
    val userName: String,
    val userPhotoUrl: String = "",
    val items: List<StatusItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val viewedBy: List<String> = emptyList()
)

data class StatusItem(
    val id: String = "",
    val type: StatusType,
    val content: String, // Text or URL
    val backgroundColor: String = "#6200EE",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
)

enum class StatusType {
    TEXT,
    IMAGE,
    VIDEO
}

data class StatusView(
    val statusId: String,
    val viewerId: String,
    val viewedAt: Long = System.currentTimeMillis()
)

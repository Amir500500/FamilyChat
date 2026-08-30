package com.family.chat.models

data class User(
    val uid: String = "",
    val uniqueId: String = "",
    val displayName: String = "",
    val createdAt: Long = 0L
)

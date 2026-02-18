package com.loopers.application.user.model

data class UserChangePasswordCommand(
    val currentPassword: String,
    val newPassword: String,
)

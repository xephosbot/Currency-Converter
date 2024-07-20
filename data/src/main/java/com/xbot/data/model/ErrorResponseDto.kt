package com.xbot.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val success: Boolean,
    val error: Error
) {
    @Serializable
    data class Error(
        val code: Int,
        val type: String,
        val info: String
    )
}

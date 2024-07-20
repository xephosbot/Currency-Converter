package com.xbot.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RatesResponseDto(
    val success: Boolean,
    val timestamp: Int,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
package com.xbot.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SymbolsResponseDto(
    val success: Boolean,
    val symbols: Map<String, String>
)
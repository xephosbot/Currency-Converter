package com.xbot.data

import com.skydoves.retrofit.adapters.result.onFailureSuspend
import com.skydoves.retrofit.adapters.result.onSuccessSuspend
import com.skydoves.retrofit.adapters.serialization.deserializeHttpError
import com.xbot.data.model.ErrorResponseDto
import com.xbot.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Locale

inline fun <reified T> safeApiCall(crossinline action: suspend () -> Result<T>): Flow<Resource<T>> = flow {
    with(action()) {
        onSuccessSuspend {
            emit(Resource.Success(it))
        }
        onFailureSuspend {
            when (val e = it.deserializeHttpError<ErrorResponseDto>()) {
                is ErrorResponseDto -> {
                    emit(Resource.Error(e.error.info))
                }
                else -> {
                    emit(Resource.Error(it.message ?: "Unknown error"))
                }
            }
        }
    }
}

inline fun <T, R> Flow<Resource<T>>.map(crossinline block: (T) -> R): Flow<Resource<R>> = map { resource ->
    try {
        when (resource) {
            is Resource.Success<T> -> Resource.Success(block(resource.data))
            is Resource.Error -> Resource.Error(resource.message)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown error")
    }
}

fun Double.toDecimalNotationString(): String {
    val str = String.format(Locale.getDefault(), "%.99f", this).trimEnd('0')
    return if (str.last() == '.' || str.last() == ',') str.dropLast(1) else str
}
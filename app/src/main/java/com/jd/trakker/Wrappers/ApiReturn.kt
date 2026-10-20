package com.jd.trakker.Wrappers

enum class ApiStatus {
    OK,
    LOADING,
    NO_INTERNET,
    UNAUTHORIZED,
    GENERIC_ERROR,
    INACTIVE
}

data class ApiResult<T>(
    val status: ApiStatus,
    val data: T?
)
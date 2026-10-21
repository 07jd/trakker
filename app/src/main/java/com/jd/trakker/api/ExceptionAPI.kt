package com.jd.trakker.api

import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ExceptionAPI(val code: Int, message: String) : Exception(message) {
    class GenericError(message: String) : ExceptionAPI(0, message)
    class BadRequest                    : ExceptionAPI(400, "Bad Request")
    class NoAccess                      : ExceptionAPI(401, "No Access")
    class NotFound                      : ExceptionAPI(404, "Not Found")
    class RateLimited                   : ExceptionAPI(412, "Rate Limited")
    class ServerError                   : ExceptionAPI(500, "Server Error")

    companion object {
        fun fromCode(code: Int, message: String? = null): ExceptionAPI {
            return when (code) {
                400  -> BadRequest()
                401  -> NoAccess()
                404  -> NotFound()
                412  -> RateLimited()
                500  -> ServerError()
                else -> GenericError( message ?: "Something went wrong")
            }
        }

        fun fromException(e: Exception): ExceptionAPI {
            return when(e) {
                is UnknownHostException             -> GenericError("No internet connection")
                is SocketTimeoutException           -> GenericError("Connection Timed out")
                else                                -> GenericError("Failed to get data")
            }
        }
    }
}


package com.jd.trakker.data.remote

import com.jd.trakker.R
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("unused")
sealed class ExceptionAPI(code: Int, messageRes: Int) : Exception(messageRes.toString()) {
    class NoInternet                    : ExceptionAPI(-1, R.string.error_no_internet)
    class GenericError(message: Int)    : ExceptionAPI(0, message)
    class BadRequest                    : ExceptionAPI(400, R.string.error_bad_request)
    class NoAccess                      : ExceptionAPI(401, R.string.error_no_access)
    class NotFound                      : ExceptionAPI(404, R.string.error_not_found)
    class RateLimited                   : ExceptionAPI(412, R.string.error_rate_limited)
    class TooManyRequests               : ExceptionAPI(429, R.string.error_too_many_requests)
    class ServerError                   : ExceptionAPI(500, R.string.error_server_error)

    companion object {
        fun fromCode(code: Int, messageRes: Int? = null): ExceptionAPI {
            return when (code) {
                400  -> BadRequest()
                401  -> NoAccess()
                404  -> NotFound()
                412  -> RateLimited()
                429  -> TooManyRequests()
                500  -> ServerError()
                else -> GenericError( messageRes ?: R.string.error_generic_message)
            }
        }

        fun fromException(e: Exception): ExceptionAPI {
            return when(e) {
                is UnknownHostException             -> NoInternet()
                is SocketTimeoutException           -> GenericError(R.string.error_timed_out)
                else                                -> GenericError(R.string.error_failed_data)
            }
        }
    }
}


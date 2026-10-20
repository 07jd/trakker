package com.jd.trakker.Wrappers

import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicReference

class tmdbWrapper(
    startingToken: String
) {
    private val token = AtomicReference(startingToken)
    private val client = OkHttpClient.Builder().addInterceptor { req ->
        val request = req.request().newBuilder().addHeader(
            "Authorization", "Bearer $token"
        ).build()
        req.proceed(request)
    }.build()


    private val api =
        Retrofit.Builder().baseUrl(tmdbInterface.baseURL).client(client).addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(tmdbInterface::class.java)

    fun setApiToken(newToken: String) {
        token.set(newToken)
    }

    suspend fun checkAuth(): ApiResult<AuthResponse> {
        return try {
            val response = api.checkAuth()
            ApiResult(
                status = ApiStatus.OK,
                data = response
            )
        } catch (_: UnknownHostException) {
            ApiResult(
                status = ApiStatus.NO_INTERNET,
                data = null
            )
        } catch (e: HttpException) {
            println("API WRAPPER: Http Error when checkAuth() tmbd: ${e.message}")
            ApiResult(
                status = when (e.code()) {
                    401 -> ApiStatus.UNAUTHORIZED
                    else -> ApiStatus.GENERIC_ERROR
                },
                data = null
            )
        } catch (e: Exception) {
            println("API WRAPPER: Generic Error when checkAuth() tmbd: ${e.message}")
            ApiResult(
                status = ApiStatus.GENERIC_ERROR,
                data = null
            )
        }
    }
}
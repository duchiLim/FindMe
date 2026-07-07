package com.greenbee.findme.data.remote.interceptor

import android.util.Log
import com.greenbee.findme.data.remote.NetworkLogRedactor
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class HttpErrorLoggingInterceptor(
    private val tag: String = TAG,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                logHttpError(request.method, request.url.toString(), response)
            }
            response
        } catch (exception: IOException) {
            Log.e(tag, NetworkLogRedactor.redact("${request.method} ${request.url} — network error"), exception)
            throw exception
        }
    }

    private fun logHttpError(method: String, url: String, response: Response) {
        val redactedUrl = NetworkLogRedactor.redact(url)
        val body = response.peekBody(MAX_BODY_BYTES).string()
        Log.e(
            tag,
            buildString {
                append("$method $redactedUrl")
                append(" → HTTP ${response.code} ${response.message}")
                if (body.isNotBlank()) {
                    append("\nresponse: ")
                    append(body)
                }
            },
        )
    }

    private companion object {
        const val TAG = "FindMe-Network"
        const val MAX_BODY_BYTES = 64 * 1024L
    }
}

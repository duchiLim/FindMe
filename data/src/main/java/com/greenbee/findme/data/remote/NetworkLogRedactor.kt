package com.greenbee.findme.data.remote

internal object NetworkLogRedactor {

    private val serviceKeyPattern = Regex("serviceKey=[^&\\s]+")

    fun redact(message: String): String =
        message.replace(serviceKeyPattern, "serviceKey=***")
}

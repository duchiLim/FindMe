package com.greenbee.findme.data.di

import android.util.Log
import com.greenbee.findme.data.BuildConfig
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.interceptor.HttpErrorLoggingInterceptor
import com.greenbee.findme.data.remote.NetworkLogRedactor
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://apis.data.go.kr/1543061/abandonmentPublicService_v2/"
    private val jsonMediaType = "application/json".toMediaType()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpErrorLoggingInterceptor())
            builder.addInterceptor(
                HttpLoggingInterceptor { message ->
                    Log.d(NETWORK_LOG_TAG, NetworkLogRedactor.redact(message))
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(jsonMediaType))
        .build()

    @Provides
    @Singleton
    fun provideAbandonmentPublicApi(retrofit: Retrofit): AbandonmentPublicApi =
        retrofit.create(AbandonmentPublicApi::class.java)

    private const val TIMEOUT_SECONDS = 30L
    private const val NETWORK_LOG_TAG = "FindMe-Network"
}

package com.example.happyj.data

import android.content.Context
import com.example.happyj.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    lateinit var api: ApiService
        private set

    fun init(context: Context) {
        val log = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(log)
            .addInterceptor { chain ->
                val req = chain.request()
                val t = TokenHolder.token
                val next = if (t != null) {
                    req.newBuilder().header("Authorization", "Bearer $t").build()
                } else req
                chain.proceed(next)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }
}

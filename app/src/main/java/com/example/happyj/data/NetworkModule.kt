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
                val b = req.newBuilder()
                if (BuildConfig.API_BASE_URL.contains("ngrok", ignoreCase = true)) {
                    b.header("ngrok-skip-browser-warning", "true")
                }
                TokenHolder.token?.let { b.header("Authorization", "Bearer $it") }
                chain.proceed(b.build())
            }
            // Red local a veces lenta: más margen para leer/escribir sin cortar a la mitad del guardado.
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }
}

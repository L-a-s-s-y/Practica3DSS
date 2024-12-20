package com.example.practica3
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CookieJar
import okhttp3.HttpUrl

object ApiClient {
    private const val BASE_URL = "http://192.168.43.164:8080/"
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    private lateinit var sharedPref: android.content.SharedPreferences
    private lateinit var token: String
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun initialize(context: Context) {
        sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        token = sharedPref.getString("auth_token", "") ?: ""
    }

    val authInterceptor = Interceptor { chain ->
        //val sharedPref = sharedgetSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor) // Añadimos el interceptor de autenticación
        .build()
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
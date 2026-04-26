package com.example.thesswatair.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitInstance {
    private val  retrofit = Retrofit.Builder()
        .baseUrl("https://api.airvisual.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AirQApi by lazy{
        retrofit.create(AirQApi::class.java)
    }
}
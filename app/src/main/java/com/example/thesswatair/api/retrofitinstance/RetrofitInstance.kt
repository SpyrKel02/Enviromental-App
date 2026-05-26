package com.example.thesswatair.api.retrofitinstance

import com.example.thesswatair.api.interfaceForAPIs.interfaceForAPIs
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val  retrofit = Retrofit.Builder()
        .baseUrl("https://api.airvisual.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: interfaceForAPIs by lazy{
        retrofit.create(interfaceForAPIs::class.java)
    }
}
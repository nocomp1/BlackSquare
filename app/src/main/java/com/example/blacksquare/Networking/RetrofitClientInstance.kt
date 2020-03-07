package com.example.blacksquare.Networking

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClientInstance {
    private var retrofit : Retrofit? = null
    private const val BASE_URL = ""

    fun getRetrofitInstance() : Retrofit?{

        if (retrofit == null){

            retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        }
        return retrofit
    }
}
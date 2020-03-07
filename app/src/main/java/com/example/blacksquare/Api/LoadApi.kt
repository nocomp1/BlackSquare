package com.example.blacksquare.Api

import com.example.blacksquare.Models.Kit
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET




internal interface LoadApi {


    @GET("/kits")
    fun getAllKits(): Single<ArrayList<Kit>>
}
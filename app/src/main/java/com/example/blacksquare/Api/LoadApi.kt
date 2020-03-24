package com.example.blacksquare.Api

import com.example.blacksquare.Models.StoreKit
import io.reactivex.Single
import retrofit2.http.GET




internal interface LoadApi {


    @GET("/kits")
    fun getAllKits(): Single<ArrayList<StoreKit>>
}
package com.example.blacksquare.Repository

import com.example.blacksquare.Api.LoadApi
import com.example.blacksquare.Models.StoreKit
import com.example.blacksquare.Networking.RetrofitClientInstance
import io.reactivex.Single

class LoadRepository {
    private var loadApi: LoadApi ? = null

    init {
        RetrofitClientInstance
            .getRetrofitInstance()
            ?.create(LoadApi::class.java).let { instance ->
            loadApi = instance

        }
    }

    fun fetchKitData(): Single<ArrayList<StoreKit>> {
      return  loadApi.let {  it!!.getAllKits()}

    }
}
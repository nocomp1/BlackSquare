package com.example.blacksquare.Repository

import com.example.blacksquare.Api.LoadApi
import com.example.blacksquare.Models.Kit
import com.example.blacksquare.Networking.RetrofitClientInstance
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoadRepository {
    private var loadApi: LoadApi ? = null

    init {
        RetrofitClientInstance
            .getRetrofitInstance()
            ?.create(LoadApi::class.java).let { instance ->
            loadApi = instance

        }
    }

    fun fetchKitData(): Single<ArrayList<Kit>> {
      return  loadApi.let {  it!!.getAllKits()}

    }
}
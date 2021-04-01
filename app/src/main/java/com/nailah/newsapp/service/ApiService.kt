package com.nailah.newsapp.service

import com.nailah.newsapp.model.ResponseNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNewsData(
        @Query("country") country: String?,
        @Query("apiKey") apiKey: String?,

    ): Call<ResponseNews>
}

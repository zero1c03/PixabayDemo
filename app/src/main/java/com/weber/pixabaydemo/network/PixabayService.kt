package com.weber.pixabaydemo.network

import com.weber.pixabaydemo.data.PixabayData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayService {

    @GET("/api")
    suspend fun getPhotos(
        @Query("key") key: String,
        @Query("q") q: String?,
        @Query("page") page: Int,
    ): Response<PixabayData>
}
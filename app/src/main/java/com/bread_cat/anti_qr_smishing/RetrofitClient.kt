package com.bread_cat.anti_qr_smishing

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://safebrowsing.googleapis.com"

    fun getService(): SafeBrowsingService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SafeBrowsingService::class.java)
    }
}

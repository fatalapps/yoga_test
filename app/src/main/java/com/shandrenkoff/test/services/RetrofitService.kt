package com.shandrenkoff.test.services

import com.shandrenkoff.test.entities.Model
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitServices {
    @GET("latest")
    fun getCurrencyList(): Observable<Model.ApiResponse>
}
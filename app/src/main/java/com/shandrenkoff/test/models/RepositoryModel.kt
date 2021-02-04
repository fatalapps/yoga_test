package com.shandrenkoff.test.models

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.shandrenkoff.test.db.AppDatabase
import com.shandrenkoff.test.entities.CurrencyEntity
import com.shandrenkoff.test.services.RetrofitServices
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryModel {
    private var retrofit: Retrofit
    private val baseUrl: String = "https://open.exchangerate-api.com/v6/"
    private var retrofitService: RetrofitServices
    private lateinit var db: AppDatabase

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitServices::class.java)
    }

    @SuppressLint("CheckResult")
    fun updateList(): Flowable<CurrencyEntity> {
        return Flowable.unsafeCreate { subscriber ->
            retrofitService.getCurrencyList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe({
                    it.rates?.forEach { currency ->
                        db.currencyDao().insertAll(currency)
                        subscriber.onNext(currency)
                    }
                }, {
                    Log.e("resp error", it.toString())
                })
        }
    }

    fun getList(): Flowable<CurrencyEntity> {
        return Flowable
            .create({
                db.currencyDao().getAll().forEach { currency ->
                    it.onNext(currency)
                }
            }, BackpressureStrategy.BUFFER)
    }

    fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "yoga_db"
        ).build()
    }
}
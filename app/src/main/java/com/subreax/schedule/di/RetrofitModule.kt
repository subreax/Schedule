package com.subreax.schedule.di

import android.util.Log
import com.subreax.schedule.data.network.TsuRetrofitService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
    single {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://tulsu.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TsuRetrofitService::class.java)
    }
}

private class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val url = req.url()
        Log.d(TAG, ">> request: $url")
        val t0 = System.currentTimeMillis()
        return chain.proceed(req).also {
            val t1 = System.currentTimeMillis()
            val timeElapsed = t1 - t0
            if (it.isSuccessful) {
                Log.d(TAG, "<< response [${timeElapsed}ms]: $url")
            } else {
                Log.e(TAG, "<< response [${timeElapsed}ms] [${it.code()}]: $url")
            }
        }
    }

    companion object {
        private const val TAG = "LoggingInterceptor"
    }
}
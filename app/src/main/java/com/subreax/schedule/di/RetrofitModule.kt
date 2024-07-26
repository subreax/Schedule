package com.subreax.schedule.di

import android.util.Log
import com.subreax.schedule.data.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideRetrofitService(): RetrofitService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://tulsu.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitService::class.java)
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
}
package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.RetrofitDates
import com.subreax.schedule.data.network.model.RetrofitDictionaryItem
import com.subreax.schedule.data.network.model.RetrofitSubject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface RetrofitService {
    @FormUrlEncoded
    @POST("schedule/queries/GetSchedule.php")
    suspend fun getSubjects(
        @Field("search_value") searchValue: String,
        @Field("search_field") searchField: String
    ): List<RetrofitSubject>

    @FormUrlEncoded
    @POST("schedule/queries/GetDates.php")
    suspend fun getDates(
        @Field("search_value") searchValue: String
    ): RetrofitDates

    @FormUrlEncoded
    @POST("schedule/queries/GetDictionaries.php")
    suspend fun getDictionaries(
        @Field("search_value") searchValue: String
    ): List<RetrofitDictionaryItem>
}

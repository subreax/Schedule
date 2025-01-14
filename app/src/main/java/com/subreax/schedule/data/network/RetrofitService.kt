package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.RetrofitCalendarItem
import com.subreax.schedule.data.network.model.RetrofitDates
import com.subreax.schedule.data.network.model.RetrofitDictionaryItem
import com.subreax.schedule.data.network.model.RetrofitSubject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface RetrofitService {
    @GET("schedule/queries/GetSchedule.php")
    suspend fun getSubjects(
        @Query("search_value") searchValue: String,
        @Query("search_field") searchField: String
    ): List<RetrofitSubject>

    @FormUrlEncoded
    @POST("schedule/queries/GetDates.php")
    suspend fun getDates(
        @Field("search_value") searchValue: String
    ): RetrofitDates

    @GET("schedule/queries/GetDictionaries.php")
    suspend fun getDictionaries(
        @Query("term") scheduleId: String
    ): List<RetrofitDictionaryItem>

    @GET("schedule/queries/GetCalendar.php")
    suspend fun getCalendar(
        @Query("search_value") searchValue: String
    ): List<RetrofitCalendarItem>
}

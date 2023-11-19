package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.RetrofitSubject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface RetrofitService {
    @FormUrlEncoded
    @POST("schedule/queries/GetSchedule.php")
    suspend fun getSchedule(
        @Field("search_value") searchValue: String,
        @Field("search_field") searchField: String
    ): List<RetrofitSubject>
}

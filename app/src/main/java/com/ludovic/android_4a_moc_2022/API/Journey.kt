package com.ludovic.android_4a_moc_2022.API

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Search
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory


interface APIJourney {
    // get album par artiste
    @Headers(
        "Authorization:1572361b-25ab-4464-8f3f-d282548d31f9"
    )
    @GET("journeys")
    fun getJourneys(
        @Query("from") from: String, //2.3749036;48.8467927
        @Query("to") to: String, //2.2922926;48.8583736
//        @Query("datetime") datetime: String? = null
    ): Deferred<Search>

}

object NetworkJourney {
    val api = Retrofit.Builder()
        .baseUrl("https://api.navitia.io/v1/coverage/fr-idf/")
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder()
                .setDateFormat("yyyyMMdd'T'HHmmss")
                .create()
        ))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(APIJourney::class.java)

}
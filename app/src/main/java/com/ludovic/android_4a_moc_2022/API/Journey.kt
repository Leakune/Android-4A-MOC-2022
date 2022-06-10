package com.ludovic.android_4a_moc_2022.API

import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory

data class Durations(
    val total: Int,
    val walking: Int,
    val car: Int,
)
data class Context(
    val timezone: String,
    val current_datetime: String,
    val carDirectPath: CarDirectPath
)
data class CarDirectPath(
    val co2_emission: Co2Emission
)
data class Journey(
    val arrival_date_time: String,
    val departure_date_time: String,
    val durations: Durations,
    val sections: List<Section>,
    val co2_emission: Co2Emission,
    val nb_transfers: Int,
    val tags: List<String>,
    val type: String,
)

data class Search(
    val journeys: List<Journey>,
    val context: Context
)

interface APIJourney {
    // get album par artiste
    @Headers(
        "Authorization:3b036afe-0110-4202-b9ed-99718476c2e0"
    )
    @GET("journeys?from=2.3749036%3B48.8467927&to=2.2922926%3B48.8583736&")
    fun getJourney(): Deferred<Search>
    //fun getJourney(@Query("m")value:String): Deferred<Journey>

}

object NetworkJourney {
    val api = Retrofit.Builder()
        .baseUrl("https://api.navitia.io/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(APIJourney::class.java)

}
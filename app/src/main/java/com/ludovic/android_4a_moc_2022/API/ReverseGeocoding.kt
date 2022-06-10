package com.ludovic.android_4a_moc_2022.API

import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class Coord (
    val lat: String,
    val lon: String,
)

data class Poi(
    val name: String,
    val coord: Coord,
)
data class Stop_area(
    val name: String,
    val coord: Coord,
)

data class Place(
    val name: String,
    @SerializedName("poi", alternate=["stop_area", "address"])
    val poi: Poi,
)

data class ReverseGeocoding(
    val places: List<Place>
)


interface APIReverseGeocoding {
    // get album par artiste
    @Headers(
        "Authorization:3b036afe-0110-4202-b9ed-99718476c2e0"
    )
    @GET("places")
    fun getPlaces(@Query("q")value:String): Deferred<ReverseGeocoding>

}

object NetworkReverseGeocoding {

    val api = Retrofit.Builder()
        .baseUrl("https://api.navitia.io/v1/coverage/sandbox/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(APIReverseGeocoding::class.java)

}
package com.ludovic.android_4a_moc_2022.API

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ludovic.android_4a_moc_2022.models.Geocoding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.lang.Exception

interface APIGeocoding {
    // get album par artiste
    @Headers(
        "Authorization:1572361b-25ab-4464-8f3f-d282548d31f9"
    )
    @GET("places")
    fun getPlaces(@Query("q")query:String): Deferred<Geocoding>

}

object NetworkGeocoding {

    val api = Retrofit.Builder()
        .baseUrl("https://api.navitia.io/v1/coverage/fr-idf/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(APIGeocoding::class.java)

}



sealed class GeocodingState
object LoadingGeocodingState : GeocodingState()
data class SuccessGeocodingState(val geocoding: Geocoding) : GeocodingState()
object EmptyGeocodingState : GeocodingState()
data class ErrorGeocodingState(val ex: Exception) : GeocodingState()


object GeocodingRepository {
    suspend fun findGeocoding(address: String): Flow<GeocodingState> {
        return flow {
            emit(LoadingGeocodingState)

            try {
                val geocoding = NetworkGeocoding.api.getPlaces(address.toString()).await()
                if (geocoding.places.isNullOrEmpty()){
                    emit(EmptyGeocodingState)
                }else{
                    emit(SuccessGeocodingState(geocoding))
                }
            } catch (e: Exception) {
                emit(ErrorGeocodingState(e))
            }
        }.flowOn(Dispatchers.Default)
    }
}

class GeocodingViewModel : ViewModel() {

    private val _geocodingState = MutableLiveData<GeocodingState>()
    val geocodingState : LiveData<GeocodingState> = _geocodingState

    fun fetchGeocoding(address: String) {
        viewModelScope.launch {
            GeocodingRepository.findGeocoding(address).collect { state ->
                _geocodingState.value = state
            }
        }
    }

    fun reset() {
        _geocodingState.value = null
    }

}
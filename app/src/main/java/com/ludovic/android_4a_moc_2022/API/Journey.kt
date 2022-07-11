package com.ludovic.android_4a_moc_2022.API

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ludovic.android_4a_moc_2022.models.Search
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.QueryMap
import java.lang.Exception
import java.net.URLEncoder


interface APIJourney {
    // get album par artiste
    @Headers(
        "Authorization:1572361b-25ab-4464-8f3f-d282548d31f9"
    )
    @GET("journeys")
    fun getJourneys(
        @Query("from") from: String, //2.3749036;48.8467927
        @Query("to") to: String, //2.2922926;48.8583736
        @Query("datetime") datetime: String,
        @Query("forbidden_uris[]") forbidden_uris: List<String>,
    ): Deferred<Search>

}


object NetworkJourney {
    val apiJourney = Retrofit.Builder()
        .baseUrl("https://api.navitia.io/v1/coverage/fr-idf/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyyMMdd'T'HHmmss")
                    .create()
            )
        )
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(APIJourney::class.java)
}


sealed class JourneyState
object LoadingJourneyState : JourneyState()
data class SuccessJourneyState(val search: Search) : JourneyState()
object EmptyJourneyState : JourneyState()
data class ErrorJourneyState(val ex: Exception) : JourneyState()


object JourneyRepository {
    suspend fun findJourney(
        from: String,
        to: String,
        datetime: String,
        forbidden_uris: List<String>
    ): Flow<JourneyState> {
        return flow {
            emit(LoadingJourneyState)
            try {
                val search =
                    NetworkJourney.apiJourney.getJourneys(from, to, datetime, forbidden_uris)
                        .await()
                Log.d("mytag", search.journeys.toString())
                if (search.journeys != null) {
                    emit(SuccessJourneyState(search))
                } else {
                    emit(EmptyJourneyState)
                }
            } catch (e: Exception) {
                emit(ErrorJourneyState(e))
            }
        }.flowOn(Dispatchers.Default)
    }
}

class JourneyViewModel : ViewModel() {
    private val _journeyState = MutableLiveData<JourneyState>()
    val journeyState: LiveData<JourneyState> = _journeyState

    fun fetchJourney(from: String, to: String, datetime: String, forbidden_uris: List<String>) {
        viewModelScope.launch {
            JourneyRepository.findJourney(from, to, datetime, forbidden_uris).collect { state ->
                _journeyState.value = state
            }
        }
    }

    fun reset() {
        _journeyState.value = null
    }

}
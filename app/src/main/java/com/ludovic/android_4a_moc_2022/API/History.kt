package com.ludovic.android_4a_moc_2022.API

import androidx.lifecycle.*
import com.google.firebase.firestore.Query
import com.ludovic.android_4a_moc_2022.auth
import com.ludovic.android_4a_moc_2022.db
import com.ludovic.android_4a_moc_2022.models.History
import com.ludovic.android_4a_moc_2022.models.Journey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import java.util.HashMap

sealed class HistoryState
object LoadingHistoryState : HistoryState()
data class SuccessHistoryState(val history: History) : HistoryState()
object EmptyHistoryState : HistoryState()
data class ErrorHistoryState(val ex: Exception) : HistoryState()

class HistoryViewModel : ViewModel() {
    val fetchHistory = liveData(Dispatchers.IO) {
        emit(LoadingHistoryState)
        try {
            HistoryRepository.findHistory().collect { state ->
                emit(state)
            }

        } catch (e: Exception) {
            emit(ErrorHistoryState(e))
        }
    }
}

object HistoryRepository {
    suspend fun findHistory(): Flow<HistoryState> = callbackFlow {

        val subscription =
            db.collection("journeys").whereEqualTo("userId", auth.currentUser!!.uid)
                .orderBy("departure_date_time", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
                    // if there is an exception we want to skip.
                    if (e != null) {
                        throw Exception(e)
                    }
                    // if we are here, we did not encounter an exception
                    if (snapshot != null) {
                        val allJourneys: MutableList<Journey> = mutableListOf()
                        val documents = snapshot.documents
                        documents.forEach {
                            val journey = Journey.deserizalize(it.data as HashMap<String, Any>)
                            allJourneys.add(journey)
                        }
                        val history = History(allJourneys)
                        trySend(SuccessHistoryState(history)).isSuccess
                    } else {
                        trySend(EmptyHistoryState).isSuccess
                    }
                }
        //Finally if collect is not in use or collecting any data we cancel this channel to prevent any leak and remove the subscription listener to the database
        awaitClose { subscription.remove() }
    }
}

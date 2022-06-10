package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ludovic.android_4a_moc_2022.API.NetworkJourney
import com.ludovic.android_4a_moc_2022.API.NetworkReverseGeocoding
import com.ludovic.android_4a_moc_2022.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ItinarySearchFragment : Fragment(R.layout.itinary_search_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBtn = view.findViewById<Button>(R.id.itinary_search_submit_button);

        searchBtn.setOnClickListener {
            val action =
                ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment();
            view.findNavController().navigate(action)
        }

        GlobalScope.launch(Dispatchers.Default) {
            // val journey = NetworkJourney.api.getJourney().await()
            val Places = NetworkReverseGeocoding.api.getPlaces("eiff").await()
            withContext(Dispatchers.Main) {
                // Donner les données à la liste
                Log.d("Places", Places.toString())
            }
        }
    }


}
package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ludovic.android_4a_moc_2022.R

class ItinarySearchFragment : Fragment(R.layout.itinary_search_fragment){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBtn = view.findViewById<Button>(R.id.itinary_search_submit_button);

        searchBtn.setOnClickListener{
            val action = ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment();
            view.findNavController().navigate(action)
        }
    }



}
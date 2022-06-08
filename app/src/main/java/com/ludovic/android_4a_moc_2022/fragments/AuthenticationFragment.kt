package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ludovic.android_4a_moc_2022.R

class AuthenticationFragment : Fragment(R.layout.authentication_fragment){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectionBtn = view.findViewById<Button>(R.id.connection_submit_button);

        connectionBtn.setOnClickListener{
            val action = AuthenticationFragmentDirections.actionAuthenticationFragmentToItinarySearchFragment();
            view.findNavController().navigate(action)
        }
    }

}
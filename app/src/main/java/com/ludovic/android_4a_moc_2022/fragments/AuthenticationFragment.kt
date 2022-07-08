package com.ludovic.android_4a_moc_2022.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ludovic.android_4a_moc_2022.BuildConfig
import com.ludovic.android_4a_moc_2022.R

class AuthenticationFragment : Fragment(R.layout.authentication_fragment){
    val auth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginField = view.findViewById<EditText>(R.id.authentication_login_text_field);
        val passwordField = view.findViewById<EditText>(R.id.authentication_password_text_field);

        val submitBtn = view.findViewById<Button>(R.id.authentication_submit_button);

        submitBtn.setOnClickListener{
            val loginFieldValue: String = loginField.text.toString().trim();
            val passwordFieldValue = passwordField.text.toString().trim();

            if(loginFieldValue == "" || passwordFieldValue == ""){
                Toast.makeText(context, R.string.error_credentials_null, Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }
            auth.signInWithEmailAndPassword(loginFieldValue, passwordFieldValue).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("ESGI", auth.currentUser!!.email!!)
                    val action = AuthenticationFragmentDirections.actionAuthenticationFragmentToItinarySearchFragment();
                    view.findNavController().navigate(action)
                }
            }

        }


//
//        auth.createUserWithEmailAndPassword("ludo@live.fr", "password").addOnCompleteListener {
//            if(it.isSuccessful){
//                Log.d("ESGI", auth.currentUser!!.email!!)
//            }
//        }
//        if(auth.currentUser != null){
//            auth.signOut()
//        }

        // 1) Lancer la popup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_API_KEY)
            .requestEmail()
            .build()

        startActivityForResult(GoogleSignIn.getClient(requireActivity(), gso).signInIntent, 100)


    }
//         2) Recevoir le résultat (vous pouvez ignorer les deprecated)
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken

                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Connecté
                        } else {
                            // Il y a une erreur
                        }
                    }

            }
        }


}
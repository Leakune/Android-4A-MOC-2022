package com.ludovic.android_4a_moc_2022.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ludovic.android_4a_moc_2022.BuildConfig
import com.ludovic.android_4a_moc_2022.BuildConfig.GOOGLE_API_KEY
import com.ludovic.android_4a_moc_2022.R
import kotlinx.android.synthetic.main.authentication_fragment.*
import kotlinx.android.synthetic.main.authentication_fragment.view.*

class AuthenticationFragment : Fragment(R.layout.authentication_fragment) {
    val auth = Firebase.auth
    val action =
        AuthenticationFragmentDirections.actionAuthenticationFragmentToItinarySearchFragment()
    var isLogin = true
    lateinit var signinClient: GoogleSignInClient
    lateinit var authView: View

    private fun configureGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_API_KEY)
            .requestEmail()
            .build()
        signinClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authView = view

        configureGoogleAuth()

        val loginField = view.findViewById<EditText>(R.id.authentication_login_text_field)
        val passwordField = view.findViewById<EditText>(R.id.authentication_password_text_field)
        val confirmPasswordField =
            view.findViewById<EditText>(R.id.authentication_confirm_password_text_field)

        val confirmPasswordLayout = view.findViewById<LinearLayout>(R.id.confirm_password_layout)
        confirmPasswordLayout.visibility = View.GONE

        val submitBtn = view.findViewById<Button>(R.id.authentication_submit_button)
        val googleSigninBtn = view.findViewById<SignInButton>(R.id.sign_in_button)

        val link_text = view.findViewById<TextView>(R.id.authentication_link_text)

        submitBtn.setOnClickListener {
            val loginFieldValue: String = loginField.text.toString().trim()
            val passwordFieldValue = passwordField.text.toString().trim()
            val confirmPasswordFieldValue = confirmPasswordField.text.toString().trim()

            if (isLogin) {
                if (loginFieldValue == "" || passwordFieldValue == "") {
                    Toast.makeText(context, R.string.error_credentials_null, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                auth.signInWithEmailAndPassword(loginFieldValue, passwordFieldValue)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            authView.findNavController().navigate(action)
                        } else {
                            val mySnackbar = Snackbar.make(
                                authView.findViewById(R.id.auth_coordinatorLayout),
                                getString(R.string.authentication_error_credentials),
                                LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    }
            }

            if (!isLogin) {
                if (loginFieldValue == "" || passwordFieldValue == "" || confirmPasswordFieldValue == "") {
                    Toast.makeText(context, R.string.error_credentials_null, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (passwordFieldValue != confirmPasswordFieldValue) {
                    Toast.makeText(
                        context,
                        R.string.authentication_error_passwords,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                auth.createUserWithEmailAndPassword(loginFieldValue, confirmPasswordFieldValue)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            authView.findNavController().navigate(action)
                        } else {
                            val mySnackbar = Snackbar.make(
                                authView.findViewById(R.id.auth_coordinatorLayout),
                                it.exception?.message ?: "",
                                LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    }
            }
        }

        googleSigninBtn.setOnClickListener {
            startActivityForResult(signinClient.signInIntent, 100)
        }

        link_text.setOnClickListener {
            isLogin = !isLogin
            if (isLogin) {
                link_text.text = getString(R.string.authentication_create_account)
                confirmPasswordLayout.visibility = View.GONE
            } else {
                link_text.text = getString(R.string.authentication_signin)
                confirmPasswordLayout.visibility = View.VISIBLE
            }

        }

//        if(auth.currentUser != null){
//            auth.signOut()
//        }


    }

    // manage results of authentication's attempt with Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken

            Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        authView.findNavController().navigate(action)
                    } else {
                        val mySnackbar = Snackbar.make(
                            authView.findViewById(R.id.auth_coordinatorLayout),
                            getString(R.string.authentication_error_google),
                            LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                }

        }
    }

}
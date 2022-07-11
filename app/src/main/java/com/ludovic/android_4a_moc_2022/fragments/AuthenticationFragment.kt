package com.ludovic.android_4a_moc_2022.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
import com.ludovic.android_4a_moc_2022.BuildConfig.GOOGLE_API_KEY
import com.ludovic.android_4a_moc_2022.R

class AuthenticationFragment : Fragment(R.layout.authentication_fragment) {
    val auth = Firebase.auth
    val action =
        AuthenticationFragmentDirections.actionAuthenticationFragmentToItinarySearchFragment()
    var isLoginMode = true
    lateinit var signinClient: GoogleSignInClient
    lateinit var authView: View
    lateinit var linkText: TextView
    lateinit var confirmPasswordLayout: LinearLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authView = view

        configureGoogleAuth()

        configureSubmitBtn()

        val googleSigninBtn = authView.findViewById<SignInButton>(R.id.sign_in_button)

        linkText = authView.findViewById(R.id.authentication_link_text)


        googleSigninBtn.setOnClickListener {
            startActivityForResult(signinClient.signInIntent, 100)
        }

        linkText.setOnClickListener {
            toggleLoginMode()
        }
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

    private fun configureGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_API_KEY)
            .requestEmail()
            .build()
        signinClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun configureSubmitBtn() {
        val loginField = authView.findViewById<EditText>(R.id.authentication_login_text_field)
        val passwordField = authView.findViewById<EditText>(R.id.authentication_password_text_field)
        val confirmPasswordField =
            authView.findViewById<EditText>(R.id.authentication_confirm_password_text_field)

        confirmPasswordLayout = authView.findViewById<LinearLayout>(R.id.confirm_password_layout)
        confirmPasswordLayout.visibility = View.GONE

        val submitBtn = authView.findViewById<Button>(R.id.authentication_submit_button)


        submitBtn.setOnClickListener {
            val loginFieldValue: String = loginField.text.toString().trim()
            val passwordFieldValue = passwordField.text.toString().trim()
            val confirmPasswordFieldValue = confirmPasswordField.text.toString().trim()

            if (isLoginMode) {
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

            if (!isLoginMode) {
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
    }

    private fun toggleLoginMode() {
        isLoginMode = !isLoginMode
        if (isLoginMode) {
            linkText.text = getString(R.string.authentication_create_account)
            confirmPasswordLayout.visibility = View.GONE
        } else {
            linkText.text = getString(R.string.authentication_signin)
            confirmPasswordLayout.visibility = View.VISIBLE
        }
    }

}

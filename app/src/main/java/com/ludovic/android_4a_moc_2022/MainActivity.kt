package com.ludovic.android_4a_moc_2022

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ludovic.android_4a_moc_2022.fragments.AuthenticationFragmentDirections
import com.ludovic.android_4a_moc_2022.fragments.myContext
import com.ludovic.android_4a_moc_2022.models.Section
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.ceil


fun secToTime(secs: Int): String {
    val roundedSecs = (ceil((secs / 60).toDouble()) * 60).toInt()
    var res = ""
    val h: Int = roundedSecs / 3600
    val m: Int = (roundedSecs % 3600) / 60
    if (h > 0) {
        res += h.toString() + "h"
    }
    if (m > 0) {
        res += m.toString() + "m"
    }
    return res
}

fun transportLogo(section: Section): TextView {
    val logoText = TextView(myContext)
    val logoBackground = GradientDrawable()

    logoBackground.setColor(Color.parseColor("#${section.publicTransportDetail.color}"))
    logoText.typeface = Typeface.DEFAULT_BOLD
    logoText.gravity = Gravity.CENTER
    logoText.text = section.publicTransportDetail.code
    logoText.setTextColor(Color.parseColor("#${section.publicTransportDetail.text_color}"))

    when (section.publicTransportDetail.commercial_mode) {
        "Tram" -> {
            logoBackground.setColor(Color.parseColor("#FFFFFF"))
            logoBackground.setStroke(5, Color.parseColor("#${section.publicTransportDetail.color}"))
            logoBackground.cornerRadius = 300F
            logoText.width = 80
            logoText.height = 80
        }
        "Métro" -> {
            logoBackground.cornerRadius = 300F
            logoText.width = 80
            logoText.height = 80
        }
        "Bus" -> {
            logoText.width = 120
            logoText.height = 60
        }
        "Train Transilien", "RER" -> {
            logoBackground.cornerRadius = 15F
            logoText.width = 80
            logoText.height = 80
        }
        "TER" -> {
            logoBackground.cornerRadius = 15F
            logoText.width = 120
            logoText.height = 80
        }
        else -> {
            logoText.width = 80
            logoText.height = 80
        }
    }
    logoText.background = logoBackground
    return logoText
}

var r: Resources? = null

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    val auth = Firebase.auth

    // Check if user is signed in (non-null), then pass through the auth page
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate(AuthenticationFragmentDirections.actionAuthenticationFragmentToItinarySearchFragment())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        r = resources


        //init navController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // toolbar configuration
        toolbar.contentInsetEndWithActions
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        val toolbarLogoutBtn = findViewById<ImageButton>(R.id.logout)
        toolbarLogoutBtn.setOnClickListener {
            auth.signOut()
            finish()
        }

        //bottom nav configuration
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController((navController))

        // hide or display toolbar / bottom nav depending on the current fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.authenticationFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.itinaryResultsFragment, R.id.itinaryOneResultFragment -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                }
            }
        }

        //configureFirebaseAuthGoogle();


    }

    //configure toolbar navigation
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//
//        Log.d("mytag", "onMapReady")
//
//        googleMap.setMinZoomPreference(6.0f)
//        googleMap.setMaxZoomPreference(14.0f)
//
//        val australiaBounds = LatLngBounds(
//            LatLng((-44.0), 113.0),  // SW bounds
//            LatLng((-10.0), 154.0) // NE bounds
//        )
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
//
//
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(LatLng(0.0, 0.0))
//                .title("Marker")
//        )
//
//    }


}
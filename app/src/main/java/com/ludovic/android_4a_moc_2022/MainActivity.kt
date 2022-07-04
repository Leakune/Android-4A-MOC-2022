package com.ludovic.android_4a_moc_2022

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ludovic.android_4a_moc_2022.fragments.myContext
import com.ludovic.android_4a_moc_2022.models.Section
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.ceil

fun secToTime(secs: Int) : String {
    val roundedSecs = (ceil((secs / 60).toDouble()) * 60).toInt()
    var res = ""
    val h: Int = roundedSecs / 3600
    val m: Int = (roundedSecs % 3600) / 60
    if (h>0){
        res += h.toString() + "h"
    }
    if (m>0){
        res += m.toString() + "m"
    }
    return res;
}

fun transportLogo(section: Section): TextView {
    val logoText = TextView(myContext)
    val logoBackground = GradientDrawable()
    when (section.publicTransportDetail.commercial_mode) {
        "Métro", "RER" -> {
            logoBackground.cornerRadius = 300F;
            logoText.width = 80
            logoText.height = 80
        }
        "Bus" -> {
            logoText.width = 120
            logoText.height = 60
        }
        "Train Transilien" -> {
            logoBackground.cornerRadius = 15F;
            logoText.width = 80
            logoText.height = 80
        }
        "TER" -> {
            logoBackground.cornerRadius = 15F;
            logoText.width = 120
            logoText.height = 80
        }
        else -> {
            logoText.width = 80
            logoText.height = 80
        }
    }
    logoText.typeface = Typeface.DEFAULT_BOLD;
    logoText.gravity = Gravity.CENTER
    logoText.background = logoBackground
    logoText.text = section.publicTransportDetail.code
    logoText.setTextColor(Color.parseColor("#${section.publicTransportDetail.text_color}"))
    logoBackground.setColor(Color.parseColor("#${section.publicTransportDetail.color}"))
    return logoText
}

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init navController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // toolbar configuration
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)

        //bottom nav configuration
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController((navController))

        // hide bottom nav at authentication page
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.authenticationFragment) {
                toolbar.visibility = View.GONE
                bottomNavigationView.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
                bottomNavigationView.visibility = View.VISIBLE
            }
        }


    }

    //configure toolbar navigation
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



}
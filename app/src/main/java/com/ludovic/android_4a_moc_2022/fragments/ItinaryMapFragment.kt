package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ludovic.android_4a_moc_2022.R

class ItinaryMapFragment : Fragment(R.layout.itinary_map_fragment){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

//class AppMapFragment: SupportMapFragment(){
//
//    private val markers: MutableMap<MarkerOptions, Any> = mutableMapOf()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        getMapAsync { map ->
//            map.addMarker(MarkerOptions().position(LatLng(48.866667, 2.333333)))?.apply {
//                markers[this] = "Ma clé"
//            }
//            map.setOnMarkerClickListener { marker ->
//                val data = markers[marker]
//            }
//        }
//
//
//    }
//}
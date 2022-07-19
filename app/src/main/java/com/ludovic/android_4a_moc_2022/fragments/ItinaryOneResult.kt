package com.ludovic.android_4a_moc_2022.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ludovic.android_4a_moc_2022.*
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Section
import kotlinx.android.synthetic.main.one_result_bottom_sheet.view.*
import kotlinx.android.synthetic.main.one_result_item_cell.view.*
import java.text.SimpleDateFormat


var mainActivity: MainActivity? = null

class ItinaryOneResultFragment : Fragment(R.layout.itinary_one_result), OnMapReadyCallback {
    private val args: ItinaryOneResultFragmentArgs by navArgs()
    private lateinit var mMap: GoogleMap
    lateinit var journey: Journey
    var showSaveBtn: Boolean = false
    var mapFragment: SupportMapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = (activity as MainActivity)
        super.onViewCreated(view, savedInstanceState)
        myContext = requireContext()
        journey = args.journey
        showSaveBtn = args.showSaveButton

        view.oneResultSectionList.layoutManager = LinearLayoutManager(requireContext())

        val bottomSheet = view.findViewById<LinearLayout>(R.id.oneResultBottomsheet)

        val oneResultJourneyDate = view.findViewById<TextView>(R.id.oneResultJourneyDate)
        val oneResultStartPlace = view.findViewById<TextView>(R.id.oneResultStartPlace)
        val oneResultEndPlace = view.findViewById<TextView>(R.id.oneResultEndPlace)
        val oneResultStartTime = view.findViewById<TextView>(R.id.oneResultStartTime)
        val oneResultEndTime = view.findViewById<TextView>(R.id.oneResultEndTime)

        val oneResultDuration = view.findViewById<TextView>(R.id.itinary_oneresult_duration)
        val oneResultTransfere = view.findViewById<TextView>(R.id.itinary_oneresult_transfere)

        val OneResultChooseButton = view.findViewById<Button>(R.id.OneResultChooseButton)
        if(!showSaveBtn){
            OneResultChooseButton.visibility = View.GONE
        }
        val format = SimpleDateFormat("HH:mm")

        oneResultStartTime.text = format.format(journey.departure_date_time)
        oneResultEndTime.text = format.format(journey.arrival_date_time)
        oneResultStartPlace.text = journey.sections.first().from?.name ?: "-"
        oneResultEndPlace.text = journey.sections.last().to?.name ?: "-"

        oneResultDuration.text = secToTime(journey.durations.total)
        oneResultTransfere.text = journey.nb_transfers.toString()

        val standardBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.oneResultSectionList.adapter = SectionsAdapter(
            sections = journey.sections
        )

        Log.d("mytag", "onViewCreated")


        OneResultChooseButton.setOnClickListener {
            db.collection("journeys")
                .add(journey.serialize(auth.currentUser!!.uid))
                .addOnSuccessListener { documentReference ->
                    Log.d("Success add journey", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Error add journey", "Error adding document", e)
                }

        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("mytag", "onMapReady")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(48.85, 2.344), 9f))

        mMap = googleMap
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
        )

        for (section in journey.sections) {
            if (section.geoJson == null) {
                continue
            }

            val lineCaps = ContextCompat.getDrawable(
                myContext!!,
                R.drawable.background_round
            )?.constantState?.newDrawable()
                ?.mutate()!!

            val option = PolylineOptions()
            option.width(15.0f)
            option.startCap(RoundCap())
            option.endCap(RoundCap())
            option.geodesic(true)
            if (section.publicTransportDetail != null) {
                option.color(Color.parseColor("#${section.publicTransportDetail.color}"))
                lineCaps.setTint(Color.parseColor("#${section.publicTransportDetail.color}"))
            } else {
                option.color(ContextCompat.getColor(myContext!!, R.color.grey))
                lineCaps.setTint(ContextCompat.getColor(myContext!!, R.color.grey))
            }

            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
            canvas.setBitmap(bitmap)
            lineCaps.setBounds(0, 0, 40, 40)
            lineCaps.draw(canvas)
            val markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)

            if (section.geoJson !== null && section.geoJson!!.type == "LineString") {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                section.geoJson!!.coordinates[0][1].toDouble(),
                                section.geoJson!!.coordinates[0][0].toDouble()
                            )
                        )
                        .anchor(0.5f, 0.5f)
                        .icon(markerIcon)
                )
                for (coord in section.geoJson!!.coordinates) {
                    option.add(LatLng(coord[1].toDouble(), coord[0].toDouble()))
                }
            }
            googleMap.addPolyline(option)
        }
    }
}


class SectionsAdapter(private val sections: List<Section>) : //private val onClickListener: OnClickListener
    RecyclerView.Adapter<SectionsViewHolder>() {

    override fun getItemCount() = sections.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionsViewHolder {
        return SectionsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.one_result_item_cell, parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: SectionsViewHolder, position: Int) {
        holder.updateItem(
            section = sections[position]
        )
//        holder.itemView.setOnClickListener {
//            onClickListener.onClick(sections[position])
//        }
    }

//    class OnClickListener(val clickListener: (sections: Section) -> Unit) {
//        fun onClick(place: Section) = clickListener(place)
//    }

}

// Une cellule
class SectionsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val startTime: TextView = v.section_start_time
    val endTime: TextView = v.section_end_time
    val startPlace: TextView = v.section_start_place
    val endPlace: TextView = v.section_end_place
    val duration: TextView = v.section_duration
    val detail: LinearLayout = v.section_detail
    val lineColor: View = v.section_line_color

    lateinit var section: Section

    init {

    }

    fun updateItem(section: Section) {

        this.section = section

        val format = SimpleDateFormat("HH:mm")

        duration.text = secToTime(section.duration)

        detail.removeAllViews()

        if (section.publicTransportDetail != null) {
            startTime.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            endTime.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            startPlace.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            endPlace.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            startTime.text = format.format(section.departure_date_time)
            endTime.text = format.format(section.arrival_date_time)
            startPlace.text = section.from?.name ?: "-"
            endPlace.text = section.to?.name ?: "-"
            val background = GradientDrawable()
            background.cornerRadius = 50f
            background.setColor(Color.parseColor("#${section.publicTransportDetail.color}"))
            lineColor.background = background
            val logo = transportLogo(section)
            detail.addView(logo)
            val param = logo.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 0, 20, 0)

            val text = TextView(myContext)
            text.text =
                r!!.getString(R.string.toward) + " : " + section.publicTransportDetail.direction
            detail.addView(text)

        } else {
            startPlace.layoutParams = LinearLayout.LayoutParams(0, 0)
            endPlace.layoutParams = LinearLayout.LayoutParams(0, 0)
            startTime.layoutParams = LinearLayout.LayoutParams(0, 0)
            endTime.layoutParams = LinearLayout.LayoutParams(0, 0)
            lineColor.setBackgroundColor(ContextCompat.getColor(myContext!!, R.color.white))
            if (section.type == "waiting") {
                val logo = ImageView(myContext)
                logo.setImageResource(R.drawable.ic_baseline_timelapse_24)
                val text = TextView(myContext)
                text.text = r!!.getString(R.string.wait)
                detail.addView(logo)
                detail.addView(text)
            } else if (section.type == "street_network" || section.type == "transfer") {
                val logo = ImageView(myContext)
                logo.setImageResource(R.drawable.ic_baseline_directions_walk_24)
                val text = TextView(myContext)
                text.text = r!!.getString(R.string.walk)
                detail.addView(logo)
                detail.addView(text)
            }
        }
    }

}
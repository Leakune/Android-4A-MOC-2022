package com.ludovic.android_4a_moc_2022.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Place
import com.ludovic.android_4a_moc_2022.models.Section
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.android.synthetic.main.itinary_search_fragment.view.*
import kotlinx.android.synthetic.main.one_result_bottom_sheet.view.*
import kotlinx.android.synthetic.main.one_result_item_cell.view.*
import kotlinx.android.synthetic.main.place_item_cell.view.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter


class ItinaryOneResultFragment : Fragment(R.layout.itinary_one_result) {
    var mapView: MapView? = null
    private val args: ItinaryOneResultFragmentArgs by navArgs()
    private val mapboxMap: MapboxMap? = null
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = MapView(requireContext())


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val journey: Journey = args.journey
//        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS) { addAnnotationToMap() }

        view.oneResultSectionList.layoutManager = LinearLayoutManager(requireContext())

        val bottomSheet = view.findViewById<LinearLayout>(R.id.oneResultBottomsheet)

        val oneResultJourneyDate = view.findViewById<TextView>(R.id.oneResultJourneyDate)
        val oneResultStartPlace = view.findViewById<TextView>(R.id.oneResultStartPlace)
        val oneResultEndPlace = view.findViewById<TextView>(R.id.oneResultEndPlace)
        val oneResultStartTime = view.findViewById<TextView>(R.id.oneResultStartTime)
        val oneResultEndTime = view.findViewById<TextView>(R.id.oneResultEndTime)

        val format = SimpleDateFormat("HH:mm")

        oneResultStartTime.text = format.format(journey.departure_date_time)
        oneResultEndTime.text = format.format(journey.arrival_date_time)
        oneResultStartPlace.text = journey.sections.first().from.name
        oneResultEndPlace.text = journey.sections.last().to.name

        val standardBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.oneResultSectionList.adapter = SectionsAdapter(
            sections = journey.sections
        )


    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//////        mapView = view.findViewById(R.id.mapView)
////        val mapView = MapView(requireContext())
////        val journey: Journey = args.journey
////
////        mapView.getMapboxMap().setCamera(
////            CameraOptions.Builder().center(
////                Point.fromLngLat(
////                    48.85,
////                    2.344
////                )
////            ).zoom(10.0).build()
////        )
////
////        for (sec in journey.sections) {
////            if (sec.geoJson !=null) {
////                mapView.getMapboxMap().loadStyle(
////                    (
////                        style(styleUri = Style.MAPBOX_STREETS) {
////                            +geoJsonSource(GEOJSON_SOURCE_ID) {
////                                geometry(sec.geoJson as Geometry)
////                            }
////                            +lineLayer("linelayer", GEOJSON_SOURCE_ID) {
////                                lineCap(LineCap.ROUND)
////                                lineJoin(LineJoin.ROUND)
////                                lineOpacity(0.7)
////                                lineWidth(8.0)
////                                lineColor("#888")
////                            }
////                        }
////                    )
////                )
////            }
////        }
//    }


//    private fun addAnnotationToMap() {
//        bitmapFromDrawableRes(
//            requireContext(),
//            R.drawable.itinary_map_icon_24
//        )?.let {
//            val annotationApi = mapView?.annotations
////            mapView?.annotations?.toString()?.let { Log.d("mytag", it) }
//            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
//            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(2.344, 48.85))
//                .withIconImage(it)
//            pointAnnotationManager?.create(pointAnnotationOptions)
//
//            Log.d("mytag", it.toString())
//        }
//    }
//
//    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
//        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
//
//    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
//        if (sourceDrawable == null) {
//            return null
//        }
//        return if (sourceDrawable is BitmapDrawable) {
//            sourceDrawable.bitmap
//        } else {
//            val constantState = sourceDrawable.constantState ?: return null
//            val drawable = constantState.newDrawable().mutate()
//            val bitmap: Bitmap = Bitmap.createBitmap(
//                drawable.intrinsicWidth, drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            val canvas = Canvas(bitmap)
//            drawable.setBounds(0, 0, canvas.width, canvas.height)
//            drawable.draw(canvas)
//            bitmap
//        }
//    }
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
    val detail: TextView = v.section_detail
    val lineColor: View = v.section_line_color

    lateinit var section: Section

    init {

    }

    @SuppressLint("SetTextI18n")
    fun updateItem(section: Section) {

        this.section = section

        val format = SimpleDateFormat("HH:mm")

        startTime.text = format.format(section.departure_date_time)
        endTime.text = format.format(section.arrival_date_time)
        if (section.type != "waiting"){
            startPlace.text = section.from.name
            endPlace.text = section.to.name
        }else{
            startPlace.text = ""
            endPlace.text = ""
        }
        detail.text = section.type
        duration.text = "${section.duration}s"
        if (section.publicTransportDetail != null) {
            detail.text = section.publicTransportDetail.code
            lineColor.setBackgroundColor(Color.parseColor("#${section.publicTransportDetail.color}"))
        }
    }

}
package com.ludovic.android_4a_moc_2022.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.android.synthetic.main.itinary_one_result.view.*
import kotlinx.android.synthetic.main.itinary_results_fragment.view.*


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
        val journey: Journey = args.journey



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.getMapboxMap()?.
        loadStyleUri(
            Style.MAPBOX_STREETS
        ) { addAnnotationToMap() }
////        mapView = view.findViewById(R.id.mapView)
//        val mapView = MapView(requireContext())
//        val journey: Journey = args.journey
//
//        mapView.getMapboxMap().setCamera(
//            CameraOptions.Builder().center(
//                Point.fromLngLat(
//                    48.85,
//                    2.344
//                )
//            ).zoom(10.0).build()
//        )
//
//        for (sec in journey.sections) {
//            if (sec.geoJson !=null) {
//                mapView.getMapboxMap().loadStyle(
//                    (
//                        style(styleUri = Style.MAPBOX_STREETS) {
//                            +geoJsonSource(GEOJSON_SOURCE_ID) {
//                                geometry(sec.geoJson as Geometry)
//                            }
//                            +lineLayer("linelayer", GEOJSON_SOURCE_ID) {
//                                lineCap(LineCap.ROUND)
//                                lineJoin(LineJoin.ROUND)
//                                lineOpacity(0.7)
//                                lineWidth(8.0)
//                                lineColor("#888")
//                            }
//                        }
//                    )
//                )
//            }
//        }
    }


    private fun addAnnotationToMap() {
        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.itinary_map_icon_24
        )?.let {
            val annotationApi = mapView?.annotations
//            mapView?.annotations?.toString()?.let { Log.d("mytag", it) }
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(2.344, 48.85))
                .withIconImage(it)
            pointAnnotationManager?.create(pointAnnotationOptions)

            Log.d("mytag", it.toString())
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
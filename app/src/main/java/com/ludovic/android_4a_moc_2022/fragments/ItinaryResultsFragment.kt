package com.ludovic.android_4a_moc_2022.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Search
import kotlinx.android.synthetic.main.itinary_results_fragment.view.*
import kotlinx.android.synthetic.main.journey_item_cell.view.*

fun getDate(date: String): String {
    return "${date.substring(9, 11)}:${date.substring(11, 13)}"
}

var myContext: Context? = null
val logoMetro = arrayOf(
    R.drawable.metro_1,
    R.drawable.metro_2,
    R.drawable.metro_3,
    R.drawable.metro_4,
    R.drawable.metro_5,
    R.drawable.metro_6,
    R.drawable.metro_7,
    R.drawable.metro_8,
    R.drawable.metro_9,
    R.drawable.metro_10,
    R.drawable.metro_11,
    R.drawable.metro_12,
    R.drawable.metro_13,
    R.drawable.metro_14
)

class ItinaryResultsFragment : Fragment(R.layout.itinary_results_fragment) {

    private val args: ItinaryResultsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myContext = requireContext()
        val searchs: Search = args.search

        val itinaryResultsCount = view.findViewById<TextView>(R.id.itinary_results_count);

        itinaryResultsCount.text = searchs.journeys.size.toString() + " Results finded"
        view.journeyList.layoutManager = LinearLayoutManager(requireContext());

        view.journeyList.adapter = JourneyAdapter(
            searchs.journeys,
            JourneyAdapter.OnClickListener { journey: Journey ->
                val action =
                    ItinaryResultsFragmentDirections.actionItinaryResultsFragmentToItinaryOneResultFragment(
                        journey
                    );

                view.findNavController().navigate(action)
            }
        )
    }
}


class JourneyAdapter(
    private val journey: List<Journey>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<JourneyViewHolder>() {

    override fun getItemCount() = journey.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        return JourneyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.journey_item_cell, parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        holder.updateItem(
            journey = journey[position]
        )
        holder.itemView.setOnClickListener {
            onClickListener.onClick(journey[position])
        }
    }

    class OnClickListener(val clickListener: (journey: Journey) -> Unit) {
        fun onClick(journey: Journey) = clickListener(journey)
    }

}

// Une cellule
class JourneyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val journeyCellTimeStart: TextView = v.journeyCellTimeStart
    val journeyCellTimeEnd: TextView = v.journeyCellTimeEnd
    val journeyCellTransportsList: LinearLayout = v.journeyCellTransportsList
    val journeyCellDuration: TextView = v.journeyCellDuration

    lateinit var journey: Journey

    init {

    }

    fun updateItem(journey: Journey) {
        Log.d("journey", journey.toString())
        this.journey = journey

        val hours = journey.durations.total / 3600;
        val minutes = (journey.durations.total % 3600) / 60;
        val seconds = journey.durations.total % 60;

        val timeString = String.format("%02dh%02d", hours, minutes);

        journeyCellDuration.text = "Durée : " + timeString
        journeyCellTimeStart.text = getDate(journey.departure_date_time)
        journeyCellTimeEnd.text = getDate(journey.arrival_date_time)

        for (sec in journey.sections) {
            Log.d("mytag", sec.type)
            if (sec.type == "public_transport") {
                if (sec.publicTransportDetail.commercial_mode == "Metro") {
                    val metro = ImageView(myContext)
                    metro.layoutParams = LinearLayout.LayoutParams(80, 80)
                    metro.setImageResource(logoMetro[sec.publicTransportDetail.code.toInt() - 1])
                    journeyCellTransportsList.addView(metro)
                } else {
                    val transport = TextView(myContext)
                    transport.textSize = 15f
                    transport.text =
                        "${sec.publicTransportDetail.commercial_mode} ${sec.publicTransportDetail.code}"
                    journeyCellTransportsList.addView(transport)

                }
            } else if (sec.type == "street_network" || sec.type == "transfer") {
                val walk = ImageView(myContext)
                walk.setImageResource(R.drawable.ic_baseline_directions_walk_24)
                journeyCellTransportsList.addView(walk)
            }
//            else if (sec.type == "waiting") {
//                val waiting = ImageView(myContext)
//                waiting.setImageResource(R.drawable.ic_baseline_timelapse_24)
//                journeyCellTransportsList.addView(waiting)
//            }
            else {
                journeyCellTransportsList.removeView(
                    journeyCellTransportsList.getChildAt(
                        journeyCellTransportsList.childCount - 1
                    )
                )
            }
            val chevron = TextView(myContext)
            chevron.textSize = 15f
            chevron.text = " > "
            journeyCellTransportsList.addView(chevron)
        }
        journeyCellTransportsList.removeView(
            journeyCellTransportsList.getChildAt(
                journeyCellTransportsList.childCount - 1
            )
        )
    }

}
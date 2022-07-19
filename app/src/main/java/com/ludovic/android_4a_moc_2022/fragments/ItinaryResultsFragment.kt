package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Search
import com.ludovic.android_4a_moc_2022.myContext
import com.ludovic.android_4a_moc_2022.transportLogo
import kotlinx.android.synthetic.main.itinary_results_fragment.view.*
import kotlinx.android.synthetic.main.journey_item_cell.view.*
import java.text.SimpleDateFormat

class ItinaryResultsFragment : Fragment(R.layout.itinary_results_fragment) {

    private val args: ItinaryResultsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myContext = requireContext()
        val searchs: Search = args.search

        view.journeyList.layoutManager = LinearLayoutManager(requireContext());

        view.journeyList.adapter = JourneyAdapter(
            searchs.journeys,
            JourneyAdapter.OnClickListener { journey: Journey ->
                val action =
                    ItinaryResultsFragmentDirections.actionItinaryResultsFragmentToItinaryOneResultFragment(
                        journey,
                    true,
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
        this.journey = journey


        val hours = journey.durations.total / 3600;
        val minutes = (journey.durations.total % 3600) / 60;
        val seconds = journey.durations.total % 60;

        val timeString = String.format("%02dh%02d", hours, minutes);

        val format = SimpleDateFormat("HH:mm")

        journeyCellDuration.text = timeString
        journeyCellTimeStart.text = format.format(journey.departure_date_time)
        journeyCellTimeEnd.text = format.format(journey.arrival_date_time)

        var count = 0
        for ( sec in journey.sections) {
            if(count == 7){
                break
            }
            when (sec.type) {
                "public_transport" -> {
                    count+=1
                    val logo = transportLogo(sec)
                    journeyCellTransportsList.addView(logo)

                }
                "street_network", "transfer" -> {
                    count+=1
                    val logo = ImageView(myContext)
                    logo.setImageResource(R.drawable.ic_baseline_directions_walk_24)
                    journeyCellTransportsList.addView(logo)
                }
                else -> {
                    journeyCellTransportsList.removeView(
                        journeyCellTransportsList.getChildAt(
                            journeyCellTransportsList.childCount - 1
                        )
                    )
                }
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
        if(count == 7) {
            val logo = ImageView(myContext)
            logo.setImageResource(R.drawable.ic_baseline_more_horiz_24)
            journeyCellTransportsList.addView(logo)
        }
    }

}
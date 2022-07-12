package com.ludovic.android_4a_moc_2022.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.transportLogo
import kotlinx.android.synthetic.main.history_item_cell.view.*
import kotlinx.android.synthetic.main.itinary_results_fragment.view.*
import kotlinx.android.synthetic.main.journey_item_cell.view.*
import java.text.SimpleDateFormat

class UserTripHistoryFragment : Fragment(R.layout.user_trip_history_fragment){
    val listHistory = listOf("1", "2", "3")
    //val listHistory: List<String> = listOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_history);
        val textView = view.findViewById<TextView>(R.id.empty_history);
        val lottie = view.findViewById<LottieAnimationView>(R.id.empty_history_lottie);

        if (listHistory.isEmpty()){
            textView.visibility =  View.VISIBLE
            lottie.visibility =  View.VISIBLE
            recyclerView.visibility =  View.GONE
        }else{
            textView.visibility =  View.GONE
            lottie.visibility =  View.GONE
            recyclerView.visibility =  View.VISIBLE
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext());


        recyclerView.adapter = HistoryAdapter(
            listHistory,
            HistoryAdapter.OnClickListener { journey: String ->


            }
        )

    }

}


class HistoryAdapter(
    private val history: List<String>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    override fun getItemCount() = history.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.history_item_cell, parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.updateItem(
            journey = history[position]
        )
        holder.itemView.setOnClickListener {
            onClickListener.onClick(history[position])
        }
    }

    class OnClickListener(val clickListener: (journey: String) -> Unit) {
        fun onClick(journey: String) = clickListener(journey)
    }



}



class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val journeyCellTimeStart: TextView = v.time
    /*val journeyCellTimeEnd: TextView = v.journeyCellTimeEnd
    val journeyCellTransportsList: LinearLayout = v.journeyCellTransportsList
    val journeyCellDuration: TextView = v.journeyCellDuration*/

    lateinit var journey: String

    init {

    }

    fun updateItem(journey: String) {
        this.journey = journey

       /* val hours = journey.durations.total / 3600;
        val minutes = (journey.durations.total % 3600) / 60;
        val seconds = journey.durations.total % 60;*/

       // val timeString = String.format("%02dh%02d", hours, minutes);

        //val format = SimpleDateFormat("HH:mm")

        journeyCellTimeStart.text = journey
       // journeyCellTimeStart.text = format.format(journey.departure_date_time)
        //journeyCellTimeEnd.text = format.format(journey.arrival_date_time)

        /*var count = 0
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
        }*/
        /*journeyCellTransportsList.removeView(
            journeyCellTransportsList.getChildAt(
                journeyCellTransportsList.childCount - 1
            )
        )
        if(count == 7) {
            val logo = ImageView(myContext)
            logo.setImageResource(R.drawable.ic_baseline_more_horiz_24)
            journeyCellTransportsList.addView(logo)
        }*/
    }

}
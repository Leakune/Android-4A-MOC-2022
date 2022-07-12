package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ludovic.android_4a_moc_2022.R
import kotlinx.android.synthetic.main.history_item_cell.view.*
import kotlinx.android.synthetic.main.score_item_cell.view.*

class ScoreFragment : Fragment(R.layout.score_fragment){

    val listScore = listOf("20", "30", "40")
    //val listScore: List<String> = listOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_score);
        val textView = view.findViewById<TextView>(R.id.empty_history);
        val lottie = view.findViewById<LottieAnimationView>(R.id.empty_history_lottie);

        if (listScore.isEmpty()){
            textView.visibility =  View.VISIBLE
            lottie.visibility =  View.VISIBLE
            recyclerView.visibility =  View.GONE
        }else{
            textView.visibility =  View.GONE
            lottie.visibility =  View.GONE
            recyclerView.visibility =  View.VISIBLE
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext());


        recyclerView.adapter = ScoreAdapter(
            listScore,
            ScoreAdapter.OnClickListener { score: String ->


            }
        )
    }

}

class ScoreAdapter(
    private val score: List<String>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<ScoreViewHolder>() {

    override fun getItemCount() = score.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        return ScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.score_item_cell, parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.updateItem(
            score = score[position]
        )
        holder.itemView.setOnClickListener {
            onClickListener.onClick(score[position])
        }
    }

    class OnClickListener(val clickListener: (score: String) -> Unit) {
        fun onClick(score: String) = clickListener(score)
    }



}



class ScoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val co2Value: TextView = v.co2
    /*val journeyCellTimeEnd: TextView = v.journeyCellTimeEnd
    val journeyCellTransportsList: LinearLayout = v.journeyCellTransportsList
    val journeyCellDuration: TextView = v.journeyCellDuration*/

    lateinit var score: String

    init {

    }

    fun updateItem(score: String) {
        this.score = score

        /* val hours = journey.durations.total / 3600;
         val minutes = (journey.durations.total % 3600) / 60;
         val seconds = journey.durations.total % 60;*/

        // val timeString = String.format("%02dh%02d", hours, minutes);

        //val format = SimpleDateFormat("HH:mm")

        co2Value.text = score + "g"
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
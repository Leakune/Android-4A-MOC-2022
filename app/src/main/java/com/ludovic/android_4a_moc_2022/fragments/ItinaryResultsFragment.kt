package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Place
import com.ludovic.android_4a_moc_2022.models.Search
import kotlinx.android.synthetic.main.itinary_results_fragment.view.*
import kotlinx.android.synthetic.main.journey_item_cell.view.*


class ItinaryResultsFragment : Fragment(R.layout.itinary_results_fragment) {

    private val args: ItinaryResultsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchs: Search = args.search

        val itinaryResultsCount = view.findViewById<TextView>(R.id.itinary_results_count);

        itinaryResultsCount.text = searchs.journeys.size.toString() + " Results finded"


        view.journeyList.adapter = JourneyAdapter(
            searchs.journeys,
            JourneyAdapter.OnClickListener { journey: Journey ->
                Log.d("journey", journey.nb_transfers.toString())
            }
        )
    }
}


class JourneyAdapter(private val journey: List<Journey>, private val onClickListener: OnClickListener) :
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

    val viewTitle: TextView = v.journeyCellTitle
    val viewSubtitle: TextView = v.journeyCellSubtite

    lateinit var journey: Journey

    init {

    }

    fun updateItem(journey: Journey) {
        Log.d("journey", journey.toString())
        this.journey = journey

        viewTitle.text = journey.durations.total.toString()
        viewSubtitle.text = journey.nb_transfers.toString()
    }

}
package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ludovic.android_4a_moc_2022.API.*
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import kotlinx.android.synthetic.main.history_item_cell.view.*
import java.text.SimpleDateFormat

class UserTripHistoryFragment : Fragment(R.layout.user_trip_history_fragment) {
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_history)
        val textView = view.findViewById<TextView>(R.id.empty_history)
        val lottie = view.findViewById<LottieAnimationView>(R.id.empty_history_lottie)

        historyViewModel.fetchHistory.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingHistoryState, EmptyHistoryState, is ErrorHistoryState -> {
                    textView.visibility = View.VISIBLE
                    lottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                is SuccessHistoryState -> {
                    textView.visibility = View.GONE
                    lottie.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    recyclerView.adapter = HistoryAdapter(
                        state.history.value,
                        HistoryAdapter.OnClickListener { journey: Journey ->
                            val action =
                                UserTripHistoryFragmentDirections.actionUserTripHistoryFragmentToItinaryOneResultFragment(
                                    journey,
                                    false
                                )
                            view.findNavController().navigate(action)
                        }
                    )
                }
                else -> {}
            }
        }

    }

}


class HistoryAdapter(
    private val history: MutableList<Journey>,
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
            history = history[position]
        )
        holder.itemView.setOnClickListener {
            onClickListener.onClick(history[position])
        }
    }

    class OnClickListener(val clickListener: (history: Journey) -> Unit) {
        fun onClick(history: Journey) = clickListener(history)
    }


}


class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val journeyCellTimeStart: TextView = v.time
    private val journeyCellDate: TextView = v.date
    private val journeyDeparturePlace: TextView = v.ll_start
    private val journeyArrivalPlace: TextView = v.ll_end

    lateinit var history: Journey

    fun updateItem(history: Journey) {
        this.history = history

        val formatDate = SimpleDateFormat("EEE dd MMMM yyyy")
        journeyCellDate.text = formatDate.format(history.departure_date_time)

        val formatTime = SimpleDateFormat("HH:mm")
        val departureDate = formatTime.format(history.departure_date_time)
        val arrivalDate = formatTime.format(history.arrival_date_time)
        journeyCellTimeStart.text = "$departureDate / $arrivalDate"

        journeyDeparturePlace.text = history.sections.first().from?.name ?: "-"
        journeyArrivalPlace.text = history.sections.last().to?.name ?: "-"

    }

}
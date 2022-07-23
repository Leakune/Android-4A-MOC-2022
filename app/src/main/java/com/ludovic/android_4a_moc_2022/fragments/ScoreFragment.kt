package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ludovic.android_4a_moc_2022.API.*
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import kotlinx.android.synthetic.main.score_fragment.*
import kotlinx.android.synthetic.main.score_item_cell.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ScoreFragment : Fragment(R.layout.score_fragment) {
    private val historyViewModel: HistoryViewModel by viewModels()
    private val NUMBER_EXP_ACQUIRED_TO_LVL_UP: Float = 10000f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressLoader = view.findViewById<ProgressBar>(R.id.progress_loader_score)

        val carCo2EquivalentTotalTxt =
            view.findViewById<TextView>(R.id.score_car_co2_equivalent_total)
        val co2EmissionTotalTxt = view.findViewById<TextView>(R.id.score_co2_emission_total)

        val currentLvlTxt = view.findViewById<TextView>(R.id.current_level)
        val lvlPercentageTxt = view.findViewById<TextView>(R.id.level_percentage)
        val progressBarLvl1Txt = view.findViewById<TextView>(R.id.progress_bar_level1_label)
        val progressBarLvl2Txt = view.findViewById<TextView>(R.id.progress_bar_level2_label)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_score)
        val textView = view.findViewById<TextView>(R.id.empty_history)
        val lottie = view.findViewById<LottieAnimationView>(R.id.empty_history_lottie)

        historyViewModel.fetchHistory.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingHistoryState -> {
                    textView.visibility = View.GONE
                    lottie.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    progressLoader.visibility = View.VISIBLE
                }
                EmptyHistoryState, is ErrorHistoryState -> {
                    textView.visibility = View.VISIBLE
                    lottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    progressLoader.visibility = View.GONE
                }
                is SuccessHistoryState -> {
                    textView.visibility = View.GONE
                    lottie.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    progressLoader.visibility = View.GONE

                    var carCo2EquivalentTotal = 0.0f
                    var co2EmissionTotal = 0.0f

                    state.history.value.forEach {
                        if (it.carCo2Equivalent !== null) {
                            carCo2EquivalentTotal += it.carCo2Equivalent!!.value
                        }
                        co2EmissionTotal += it.co2_emission.value
                    }
                    val sparedCo2Emission = carCo2EquivalentTotal - co2EmissionTotal

                    val sparedCo2EmissionKg = roundOffDecimal(sparedCo2Emission / 1000)
                    val carCo2EquivalentTotalKg = roundOffDecimal(carCo2EquivalentTotal / 1000)

                    carCo2EquivalentTotalTxt.text = "+$carCo2EquivalentTotalKg"
                    co2EmissionTotalTxt.text = "-$sparedCo2EmissionKg"

                    val currentLvl = (co2EmissionTotal / NUMBER_EXP_ACQUIRED_TO_LVL_UP).toInt() + 1
                    val currentExp = co2EmissionTotal % NUMBER_EXP_ACQUIRED_TO_LVL_UP
                    val currentExpPercentage =
                        (currentExp.toInt() * 100 / NUMBER_EXP_ACQUIRED_TO_LVL_UP).toInt()

                    currentLvlTxt.text = "Niveau $currentLvl"
                    lvlPercentageTxt.text = "$currentExpPercentage%"
                    progressBarLvl1Txt.text = "Niveau $currentLvl"
                    progressBarLvl2Txt.text = "Niveau ${currentLvl + 1}"
                    progressBar.progress = currentExpPercentage


                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = ScoreAdapter(
                        state.history.value,
                    )
                }
                else -> {}
            }
        }
    }

}

class ScoreAdapter(
    private val score: MutableList<Journey>,
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
    }

}

fun roundOffDecimal(value: Float): Float {
    val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.CEILING
    return df.format(value).toFloat()
}

class ScoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val co2Value: TextView = v.co2
    private val journeyCellDate: TextView = v.date_score
    private val journeyDeparturePlace: TextView = v.ll_start_score
    private val journeyArrivalPlace: TextView = v.ll_end_score

    lateinit var score: Journey

    fun updateItem(score: Journey) {
        this.score = score


        val co2EmissionKg = roundOffDecimal(score.co2_emission.value / 1000)
        co2Value.text = co2EmissionKg.toString() + "kg"

        val formatDate = SimpleDateFormat("EEE dd MMMM yyyy")
        journeyCellDate.text = formatDate.format(score.departure_date_time)

        journeyDeparturePlace.text = score.sections.first().from?.name ?: "-"
        journeyArrivalPlace.text = score.sections.last().to?.name ?: "-"

    }

}
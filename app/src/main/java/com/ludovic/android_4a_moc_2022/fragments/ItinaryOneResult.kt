package com.ludovic.android_4a_moc_2022.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ludovic.android_4a_moc_2022.MainActivity
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Journey
import com.ludovic.android_4a_moc_2022.models.Section
import com.ludovic.android_4a_moc_2022.secToTime
import com.ludovic.android_4a_moc_2022.transportLogo
import kotlinx.android.synthetic.main.one_result_bottom_sheet.view.*
import kotlinx.android.synthetic.main.one_result_item_cell.view.*
import java.text.SimpleDateFormat
import java.util.Map.entry


var mainActivity: MainActivity? = null

class ItinaryOneResultFragment : Fragment(R.layout.itinary_one_result) {
    private val args: ItinaryOneResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = (activity as MainActivity)
        super.onViewCreated(view, savedInstanceState)
        val journey: Journey = args.journey

        view.oneResultSectionList.layoutManager = LinearLayoutManager(requireContext())

        val bottomSheet = view.findViewById<LinearLayout>(R.id.oneResultBottomsheet)

        val oneResultJourneyDate = view.findViewById<TextView>(R.id.oneResultJourneyDate)
        val oneResultStartPlace = view.findViewById<TextView>(R.id.oneResultStartPlace)
        val oneResultEndPlace = view.findViewById<TextView>(R.id.oneResultEndPlace)
        val oneResultStartTime = view.findViewById<TextView>(R.id.oneResultStartTime)
        val oneResultEndTime = view.findViewById<TextView>(R.id.oneResultEndTime)

        val oneResultDuration = view.findViewById<TextView>(R.id.itinary_oneresult_duration)
        val oneResultTransfere = view.findViewById<TextView>(R.id.itinary_oneresult_transfere)

        val format = SimpleDateFormat("HH:mm")

        oneResultStartTime.text = format.format(journey.departure_date_time)
        oneResultEndTime.text = format.format(journey.arrival_date_time)
        oneResultStartPlace.text = journey.sections.first().from.name
        oneResultEndPlace.text = journey.sections.last().to.name

        oneResultDuration.text = secToTime(journey.durations.total)
        oneResultTransfere.text = journey.nb_transfers.toString()

        val standardBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.oneResultSectionList.adapter = SectionsAdapter(
            sections = journey.sections
        )


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
            startTime.text = format.format(section.departure_date_time)
            endTime.text = format.format(section.arrival_date_time)
            startPlace.text = section.from.name
            endPlace.text = section.to.name
            val background = GradientDrawable()
            background.cornerRadius = 50f
            background.setColor(Color.parseColor("#${section.publicTransportDetail.color}"))
            lineColor.background = background
            val logo = transportLogo(section)
            detail.addView(logo)

            val text = TextView(myContext)
            text.setPadding(5,0,0,0)
            text.text = "To : " + section.publicTransportDetail.direction
            detail.addView(text)

        }else{
            (startPlace.parent as ViewManager).removeView(startPlace)
            (endPlace.parent as ViewManager).removeView(endPlace)
            (startTime.parent as ViewManager).removeView(startTime)
            (endTime.parent as ViewManager).removeView(endTime)
            if (section.type == "waiting") {
                val logo = ImageView(myContext)
                logo.setImageResource(R.drawable.ic_baseline_timelapse_24)
                val text = TextView(myContext)
                text.text = "Wait in station"
                detail.addView(logo)
                detail.addView(text)
            }else if (section.type == "street_network" || section.type == "transfer") {
                val logo = ImageView(myContext)
                logo.setImageResource(R.drawable.ic_baseline_directions_walk_24)
                val text = TextView(myContext)
                text.text = "Walk"
                detail.addView(logo)
                detail.addView(text)
            }
        }
    }

}
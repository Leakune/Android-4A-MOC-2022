package com.ludovic.android_4a_moc_2022.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.android_4a_moc_2022.API.*
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Place
import kotlinx.android.synthetic.main.itinary_search_fragment.view.*
import kotlinx.android.synthetic.main.place_item_cell.view.*
import kotlinx.coroutines.*

var currentSearchBar: String = "from"
var from: Place? = null
var to: Place? = null
var clicked = false


class ItinarySearchFragment : Fragment(R.layout.itinary_search_fragment) {

    val geocodingViewModel: GeocodingViewModel by viewModels()
    val journeyViewModel: JourneyViewModel by viewModels()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.placeList.layoutManager = LinearLayoutManager(requireContext())

        val itinarySearchRecyclerEmpty =
            view.findViewById<TextView>(R.id.itinary_search_list_empty);
        val itinarySearchRecyclerView = view.findViewById<RecyclerView>(R.id.placeList);
        val itinarySearchFrom = view.findViewById<EditText>(R.id.itinary_search_from);
        val itinarySearchTo = view.findViewById<EditText>(R.id.itinary_search_to);
        val itinarySearchSubmit = view.findViewById<Button>(R.id.itinary_search_submit);
        val itinarySearchRecyclerViewLayout =
            itinarySearchRecyclerView.layoutParams as ConstraintLayout.LayoutParams


        journeyViewModel.journeyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingJourneyState -> {
                    val spinner = ProgressBar(requireContext())
                }
                is EmptyJourneyState -> {

                }
                is SuccessJourneyState -> {
                    val action =
                        ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment(
                            state.search
                        );

                    view.findNavController().navigate(action)
                }
                is ErrorJourneyState -> {
                    Log.d("logs", "Error")
                    state.ex
                }
                else -> {}
            }
        }

        geocodingViewModel.geocodingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingGeocodingState -> {
                }
                is EmptyGeocodingState -> {
                    itinarySearchRecyclerView.visibility = View.GONE;
                    itinarySearchRecyclerEmpty.visibility = View.VISIBLE
                }
                is SuccessGeocodingState -> {
                    itinarySearchRecyclerView.visibility = View.VISIBLE;
                    itinarySearchRecyclerEmpty.visibility = View.GONE
                    view.placeList.adapter = PlacesAdapter(
                        places = state.geocoding.places,
                        PlacesAdapter.OnClickListener { place: Place ->
                            clicked = true
                            if (currentSearchBar == "from") {
                                itinarySearchFrom.setText(place.name)
                                from = place
                            } else {
                                itinarySearchTo.setText(place.name)
                                to = place
                            }
                            if (from != null && to != null) {
                                itinarySearchSubmit.isEnabled = true
                            }
                            itinarySearchRecyclerView.visibility = View.GONE;
                        }
                    )
                }
                is ErrorGeocodingState -> {
                    Log.d("logs", "Error")
                    state.ex
                }
                else -> {}
            }
        }


        itinarySearchFrom.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (clicked) {
                        clicked = false
                        return
                    }
                    currentSearchBar = "from"
                    if (s.toString().isBlank()) {
                        itinarySearchRecyclerView.visibility = View.GONE;
                        itinarySearchRecyclerEmpty.visibility = View.VISIBLE
                        return
                    }
                    geocodingViewModel.fetchGeocoding(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    itinarySearchRecyclerViewLayout.topToBottom = itinarySearchFrom.id
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )

        itinarySearchTo.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (clicked) {
                        clicked = false
                        return
                    }
                    currentSearchBar = "to"
                    if (s.toString().isBlank()) {
                        itinarySearchRecyclerView.visibility = View.GONE;
                        itinarySearchRecyclerEmpty.visibility = View.VISIBLE
                        return
                    }
                    if (from != null && to != null) {
                        itinarySearchSubmit.isEnabled = true
                    }
                    geocodingViewModel.fetchGeocoding(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    itinarySearchRecyclerViewLayout.topToBottom = itinarySearchTo.id
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )


        itinarySearchSubmit.setOnClickListener {
            if (from != null && to != null) {
                journeyViewModel.fetchJourney(
                    from = from!!.getCoords(),
                    to = to!!.getCoords()
                )
            }
        }
    }
}


class PlacesAdapter(private val places: List<Place>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<PlaceViewHolder>() {

    override fun getItemCount() = places.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.place_item_cell, parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.updateItem(
            place = places[position]
        )
        holder.itemView.setOnClickListener {
            onClickListener.onClick(places[position])
        }
    }

    class OnClickListener(val clickListener: (place: Place) -> Unit) {
        fun onClick(place: Place) = clickListener(place)
    }

}

// Une cellule
class PlaceViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val viewTitle: TextView = v.placeCellTitle
    val viewSubtitle: TextView = v.placeCellSubtite

    lateinit var place: Place

    init {

    }

    fun updateItem(place: Place) {

        this.place = place

        viewTitle.text = place.name
        viewSubtitle.text = place.getCoords()
    }

}

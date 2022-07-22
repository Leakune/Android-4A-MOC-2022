package com.ludovic.android_4a_moc_2022.fragments

import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ludovic.android_4a_moc_2022.API.*
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.carCo2Equivalent
import com.ludovic.android_4a_moc_2022.models.Address
import com.ludovic.android_4a_moc_2022.models.Coord
import com.ludovic.android_4a_moc_2022.models.Place
import kotlinx.android.synthetic.main.itinary_search_fragment.view.*
import kotlinx.android.synthetic.main.place_item_cell.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


var currentSearchBar: String = "from"
var from: Place? = null
var to: Place? = null
var clicked = false


class ItinarySearchFragment : Fragment(R.layout.itinary_search_fragment) {

    val geocodingViewModel: GeocodingViewModel by viewModels()
    val journeyViewModel: JourneyViewModel by viewModels()

    var departureDate: LocalDateTime = LocalDateTime.now()
    var forbiddenUris: MutableList<String> = mutableListOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    fun dateTimePickerSetup(view: View) {
        val itinarySearchDate = view.findViewById<Button>(R.id.itinary_search_date);
        val itinarySearchTimeSet = view.findViewById<Button>(R.id.itinary_search_time_set);
        val itinarySearchDatePopup =
            view.findViewById<LinearLayout>(R.id.itinary_search_date_popup);
        val itinarySearchDatePicker =
            view.findViewById<DatePicker>(R.id.itinary_search_date_picker);
        val itinarySearchTimePicker =
            view.findViewById<TimePicker>(R.id.itinary_search_time_picker);
        val itinarySearchSubmit = view.findViewById<Button>(R.id.itinary_search_submit);
        val itinarySearchDatetimeNow = view.findViewById<Button>(R.id.itinary_search_datetime_now);

        itinarySearchDatetimeNow.setOnClickListener {
            departureDate = LocalDateTime.now()
            itinarySearchDate.text = view.resources.getString(R.string.now)
            itinarySearchTimePicker.visibility = View.GONE
            itinarySearchDatePicker.visibility = View.VISIBLE
            itinarySearchDatePopup.visibility = View.GONE
        }

        itinarySearchTimePicker.setIs24HourView(true)

        itinarySearchDate.setOnClickListener {
            itinarySearchDatePopup.visibility = View.VISIBLE
        }
        itinarySearchTimeSet.setOnClickListener {
            if (itinarySearchTimePicker.visibility == View.GONE) {
                itinarySearchTimePicker.visibility = View.VISIBLE
                itinarySearchDatePicker.visibility = View.GONE
            } else {
                itinarySearchTimePicker.visibility = View.GONE
                itinarySearchDatePicker.visibility = View.VISIBLE
                itinarySearchDatePopup.visibility = View.GONE
                itinarySearchDate.text =
                    departureDate.format(DateTimeFormatter.ofPattern("EE dd LLL' - 'HH'h'mm"))
            }
            itinarySearchSubmit.isEnabled = true
        }
        itinarySearchDatePicker.setOnDateChangedListener { _, year, month, day ->
            departureDate =
                LocalDateTime.of(year, month, day, departureDate.hour, departureDate.minute)
        }
        itinarySearchTimePicker.setOnTimeChangedListener { _, hours, minutes ->
            departureDate = LocalDateTime.of(
                departureDate.year,
                departureDate.month,
                departureDate.dayOfMonth,
                hours,
                minutes
            )
        }
    }

    fun transportModePickerSetup(view: View) {

        forbiddenUris = mutableListOf()

        val itinarySearchTransport = view.findViewById<Button>(R.id.itinary_search_transport);
        val itinarySearchTransportClose =
            view.findViewById<Button>(R.id.itinary_search_transport_close);
        val itinarySearchTransportPopup =
            view.findViewById<LinearLayout>(R.id.itinary_search_transport_popup);
        val itinarySearchSubmit = view.findViewById<Button>(R.id.itinary_search_submit);

        itinarySearchTransport.setOnClickListener {
            itinarySearchTransportPopup.visibility = View.VISIBLE
        }

        itinarySearchTransportClose.setOnClickListener {
            itinarySearchTransportPopup.visibility = View.GONE
            itinarySearchSubmit.isEnabled = true
        }

        val itinarySearchTransportCheckbox =
            view.findViewById<LinearLayout>(R.id.itinary_search_transport_checkbox)
        itinarySearchTransportCheckbox.children.forEach {
            it as CheckBox
            it.setOnCheckedChangeListener { compoundButton, b ->
                if (!b) {
                    forbiddenUris.add(it.tag.toString())
                } else {
                    forbiddenUris.remove(it.tag.toString())
                }
                Log.d("mytag", forbiddenUris.toString())
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.placeList.layoutManager = LinearLayoutManager(requireContext())

        val itinarySearchRecyclerEmpty =
            view.findViewById<TextView>(R.id.itinary_search_list_empty);
        val itinarySearchNoResut =
            view.findViewById<TextView>(R.id.itinary_search_no_journey);
        val itinarySearchRecyclerView = view.findViewById<RecyclerView>(R.id.placeList);
        val itinarySearchFrom = view.findViewById<EditText>(R.id.itinary_search_from);
        val itinarySearchTo = view.findViewById<EditText>(R.id.itinary_search_to);
        val itinarySearchSubmit = view.findViewById<Button>(R.id.itinary_search_submit);
        val itinarySearchRecyclerViewLayout =
            itinarySearchRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        val itinarySearchCurrentLocation =
            view.findViewById<Button>(R.id.itinary_search_current_location);

        dateTimePickerSetup(view)
        transportModePickerSetup(view)


        journeyViewModel.journeyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingJourneyState -> {
                    itinarySearchSubmit.isEnabled = false
                    itinarySearchSubmit.text = "Loading"
                }
                is EmptyJourneyState -> {
                    itinarySearchRecyclerView.visibility = View.GONE;
                    itinarySearchRecyclerEmpty.visibility = View.GONE
                    itinarySearchNoResut.visibility = View.VISIBLE
                    itinarySearchSubmit.text =
                        view.resources.getString(R.string.itinary_search_submit)
                }
                is SuccessJourneyState -> {
                    carCo2Equivalent = state.search.context.car_direct_path.co2_emission
                    val action =
                        ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment(
                            state.search
                        );
                    journeyViewModel.reset()
                    geocodingViewModel.reset()
                    view.findNavController().navigate(action)
                }
                is ErrorJourneyState -> {
                    Log.d("logs", "Error")
                    state.ex
                    itinarySearchRecyclerView.visibility = View.GONE;
                    itinarySearchRecyclerEmpty.visibility = View.GONE
                    itinarySearchNoResut.visibility = View.VISIBLE
                    itinarySearchSubmit.text =
                        view.resources.getString(R.string.itinary_search_submit)
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

        itinarySearchCurrentLocation.setOnClickListener {
            Log.d("mytag", "salutt")
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    Log.d("mytag", it.toString())
                    from = Place(
                        name = "Current location",
                        Address(
                            "Current location",
                            Coord(it.latitude.toString(), it.longitude.toString())
                        )
                    )
                    itinarySearchFrom.setText("Current location")
                    // Got last known location. In some rare situations this can be null.
                }.addOnFailureListener {
                    Log.d("mytag", it.toString())
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
                    itinarySearchNoResut.visibility = View.GONE
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
                    itinarySearchNoResut.visibility = View.GONE
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )

        itinarySearchSubmit.setOnClickListener {
            if (from != null && to != null) {
                Log.d(
                    "mytag",
                    departureDate.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
                )
                journeyViewModel.fetchJourney(
                    from = from!!.getCoords(),
                    to = to!!.getCoords(),
                    datetime = departureDate.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")),
                    forbidden_uris = forbiddenUris
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

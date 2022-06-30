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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.android_4a_moc_2022.API.*
import com.ludovic.android_4a_moc_2022.R
import com.ludovic.android_4a_moc_2022.models.Place
import kotlinx.android.synthetic.main.itinary_search_fragment.view.*
import kotlinx.android.synthetic.main.place_item_cell.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

var currentSearchBar: String = "from"
var from: Place? = null
var to: Place? = null
var clicked = false


class ItinarySearchFragment : Fragment(R.layout.itinary_search_fragment) {

    val geocodingViewModel: GeocodingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.placeList.layoutManager = LinearLayoutManager(requireContext())

        val itinarySearchRecyclerEmpty =
            view.findViewById<TextView>(R.id.itinary_search_list_empty);
        val itinarySearchRecyclerView = view.findViewById<RecyclerView>(R.id.placeList);
        val itinarySearchFrom = view.findViewById<EditText>(R.id.itinary_search_from);
        val itinarySearchTo = view.findViewById<EditText>(R.id.itinary_search_to);
        val itinarySearchSubmit = view.findViewById<Button>(R.id.itinary_search_submit);
        val itinarySearchRecyclerViewLayout = itinarySearchRecyclerView.layoutParams as ConstraintLayout.LayoutParams

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
                    if(clicked) {
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
                    if(clicked) {
                        clicked = false
                        return
                    }
                    currentSearchBar = "to"
                    if (s.toString().isBlank()) {
                        itinarySearchRecyclerView.visibility = View.GONE;
                        itinarySearchRecyclerEmpty.visibility = View.VISIBLE
                        return
                    }
                    GlobalScope.launch(Dispatchers.Default) {
                        val toGeocoding = NetworkGeocoding.api.getPlaces(s.toString()).await()
                        withContext(Dispatchers.Main) {
                            if (toGeocoding.places.isNullOrEmpty()) {
                                itinarySearchRecyclerView.visibility = View.GONE;
                                itinarySearchRecyclerEmpty.visibility = View.VISIBLE
                            } else {
                                itinarySearchRecyclerView.visibility = View.VISIBLE;
                                itinarySearchRecyclerEmpty.visibility = View.GONE
                                view.placeList.adapter = PlacesAdapter(
                                    places = toGeocoding.places,
                                    PlacesAdapter.OnClickListener { place: Place ->
                                        clicked = true
                                        if (currentSearchBar == "from") {
                                            itinarySearchFrom.setText(place.name)
                                            from = place
                                        } else {
                                            itinarySearchTo.setText(place.name)
                                            to = place
                                        }
                                        itinarySearchRecyclerView.visibility = View.GONE;
                                    }
                                )
                            }
                        }
                    }
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
                GlobalScope.launch(Dispatchers.Default) {
                    val journeys = NetworkJourney.api.getJourneys(
                        from = from!!.getCoords(),
                        to = to!!.getCoords()
                    ).await()
                    withContext(Dispatchers.Main) {
                      val action =
                          ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment(journeys);

                        view.findNavController().navigate(action)
                    }
                }
            }
        }
//        val tmpBtn = view.findViewById<Button>(R.id.itinary_search_submit_button);
//        tmpBtn.setOnClickListener {
//            val action =
//                ItinarySearchFragmentDirections.actionItinarySearchFragmentToItinaryResultsFragment();
//            view.findNavController().navigate(action)
//        }
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









//
//
//sealed class ProductState
//object LoadingProductState : ProductState()
//data class SuccessProductState(val product: String) : ProductState()
//data class ErrorProductState(val ex: Exception) : ProductState()
//
//
//object ProductRepository {
//
//    suspend fun findProduct(barcode: String): Flow<ProductState> {
//        return flow {
//            emit(LoadingProductState)
//
//            // TODO Appeler la requête
//
//            try {
//                emit(SuccessProductState("OK"))
//            } catch (e: Exception) {
//                emit(ErrorProductState(e))
//            }
//        }.flowOn(Dispatchers.Default)
//    }
//
//}
//
//class ProductDetailsViewModel : ViewModel() {
//
//    private val _productState = MutableLiveData<ProductState>()
//    val productState : LiveData<ProductState> = _productState
//
//    fun fetchProduct() {
//        viewModelScope.launch {
//            ProductRepository.findProduct("1234567890").collect { state ->
//                _productState.value = state
//            }
//        }
//    }
//
//}
//
//
//
//class ProductFragment : Fragment() {
//
//    val viewModel: ProductDetailsViewModel by viewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel.fetchProduct()
//
//        viewModel.productState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is LoadingProductState -> {
//
//                }
//                is SuccessProductState -> {
//                    state.product
//                }
//                is ErrorProductState -> {
//                    state.ex
//                }
//            }
//        }
//    }
//
//}
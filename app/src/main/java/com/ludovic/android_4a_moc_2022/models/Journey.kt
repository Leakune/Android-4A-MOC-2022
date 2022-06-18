package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Journey(
    val arrival_date_time: String,
    val departure_date_time: String,
    val durations: Durations,
    val sections: List<Section>,
    val co2_emission: Co2Emission,
    val nb_transfers: Int,
    val tags: List<String>,
    val type: String,
) : Parcelable {
}
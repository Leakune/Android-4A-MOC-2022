package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Section(
    val type: String,
    val arrival_date_time: Date,
    val departure_date_time: Date,
    val from: From?,
    val to: To?,
    val duration: Int,
    val co2_emission: Co2Emission?,
    @SerializedName("display_informations")
    val publicTransportDetail: PublicTransportDetail?,
    @SerializedName("geojson")
    var geoJson: GeoJson?
) : Parcelable
package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoJson(
    val coordinates: List<List<Float>>,
    val type: String
) : Parcelable {

}
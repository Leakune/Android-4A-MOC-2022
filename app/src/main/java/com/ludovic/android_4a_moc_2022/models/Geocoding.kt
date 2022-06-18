package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Geocoding(
    val places: List<Place>
) : Parcelable{
    fun toListString(): List<String> {
        return this.places.map { it.toString() }
    }
}
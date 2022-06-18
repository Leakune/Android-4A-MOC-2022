package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Coord(
    val lat: String,
    val lon: String,
) :Parcelable {
    override fun toString(): String {
        return this.lon + ";" + this.lat
    }
}

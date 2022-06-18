package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Place(
    val name: String,
    @SerializedName("address", alternate = ["stop_area", "poi", "administrative_region"])
    val address: Address,
) : Parcelable {
    fun getCoords(): String {
        return this.address.coord.toString()
    }

    override fun toString(): String {
        return this.name
    }
}
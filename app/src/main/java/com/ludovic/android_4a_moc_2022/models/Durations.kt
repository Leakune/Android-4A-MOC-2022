package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Durations(
    val total: Int,
    val walking: Int,
    val car: Int,
) : Parcelable{
}
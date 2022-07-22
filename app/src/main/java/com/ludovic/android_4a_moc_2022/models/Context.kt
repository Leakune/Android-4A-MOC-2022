package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Context(
    val timezone: String,
    val current_datetime: String,
    val car_direct_path: CarDirectPath
) : Parcelable{
}
package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Search(
    val journeys: List<Journey>,
    val context: Context
): Parcelable {}
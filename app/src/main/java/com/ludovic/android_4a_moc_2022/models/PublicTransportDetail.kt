package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PublicTransportDetail(
    val commercial_mode: String,
    val code: String,
    val color: String,
    val text_color: String,
    val direction: String,
): Parcelable {
}
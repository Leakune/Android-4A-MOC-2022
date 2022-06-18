package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Section(
    val type: String,
    val arrival_date_time: String,
    val from: From,
    val to: To,
    val duration: Int,
    val co2_emission: Co2Emission,
    //@SerializedName("display_informations")
    //val publicTransportDetail : PublicTransportDetail
) : Parcelable {
}
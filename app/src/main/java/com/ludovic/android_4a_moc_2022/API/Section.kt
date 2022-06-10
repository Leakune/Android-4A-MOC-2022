package com.ludovic.android_4a_moc_2022.API

data class To(
    val name: String
)

data class From(
    val name: String
)

data class Co2Emission(
    val value: Float
)

data class Section(
    val type: String,
    val arrival_date_time: String,
    val from: From,
    val to: To,
    val duration: Int,
    val co2_emission: Co2Emission,
)
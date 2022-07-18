package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@Parcelize
data class Journey(
    val arrival_date_time: Date,
    val departure_date_time: Date,
    val durations: Durations,
    val sections: List<Section>,
    val co2_emission: Co2Emission,
    val nb_transfers: Int,
    val tags: List<String>,
    val type: String,
) : Parcelable {
    fun serialize(userId: String): HashMap<String, Any>{
        val sectionsObj = HashMap<String, Any>()
        sections.forEachIndexed { index, section ->
            val sectionObj: MutableMap<String, Any> = mutableMapOf(
                "type" to section.type,
                "arrival_date_time" to section.arrival_date_time,
                "departure_date_time" to section.departure_date_time,
                "from" to section.from,
                "to" to section.to,
                "duration" to section.duration,
                "publicTransportDetail" to section.publicTransportDetail,
                "geoJson" to Json.encodeToString(section.geoJson),
            )

            sectionsObj["$index"] = sectionObj
        }

        val map = HashMap<String, Any>()

        map["arrival_date_time"] = arrival_date_time
        map["departure_date_time"] = departure_date_time
        map["durations"] = durations
        map["sections"] = sectionsObj
        map["co2_emission"] = co2_emission
        map["nb_transfers"] = nb_transfers
        map["tags"] = tags
        map["userId"] = userId

        return map
    }
}
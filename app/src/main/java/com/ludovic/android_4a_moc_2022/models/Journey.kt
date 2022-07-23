package com.ludovic.android_4a_moc_2022.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.text.Typography.section

@Parcelize
data class Journey(
    val arrival_date_time: Date,
    val departure_date_time: Date,
    val durations: Durations,
    val sections: List<Section>,
    val co2_emission: Co2Emission,
    var carCo2Equivalent: Co2Emission?,
    val nb_transfers: Int,
    val tags: List<String>,
) : Parcelable {
    fun serialize(userId: String): HashMap<String, Any?> {
        val sectionsObj = HashMap<String, Any>()
        sections.forEachIndexed { index, section ->
            val sectionObj: MutableMap<String, Any?> = mutableMapOf(
                "type" to section.type,
                "arrival_date_time" to section.arrival_date_time,
                "departure_date_time" to section.departure_date_time,
                "from" to section.from,
                "to" to section.to,
                "duration" to section.duration,
                "co2_emission" to section.co2_emission,
                "publicTransportDetail" to section.publicTransportDetail,
                "geoJson" to Json.encodeToString(section.geoJson),
            )
            sectionsObj["$index"] = sectionObj
        }

        val map = HashMap<String, Any?>()
        map["arrival_date_time"] = arrival_date_time
        map["departure_date_time"] = departure_date_time
        map["durations"] = durations
        map["sections"] = sectionsObj
        map["co2_emission"] = co2_emission
        map["carCo2Equivalent"] = carCo2Equivalent
        map["nb_transfers"] = nb_transfers
        map["tags"] = tags
        map["userId"] = userId
        return map
    }

    companion object {
        fun deserizalize(map: HashMap<String, Any>): Journey {
            val sections: MutableList<Section> = mutableListOf()
            val sectionsObj: HashMap<String, Any> = map["sections"] as HashMap<String, Any>

            for ((key, value) in sectionsObj) {
                val sectionObj: HashMap<String, Any> = sectionsObj[key] as HashMap<String, Any>

                val fromObj: HashMap<String, Any>
                var from: From? = null
                if (sectionObj["from"] !== null) {
                    fromObj = sectionObj["from"] as HashMap<String, Any>
                    from = From(fromObj["name"] as String)
                }

                val toObj: HashMap<String, Any>
                var to: To? = null
                if (sectionObj["to"] !== null) {
                    toObj = sectionObj["to"] as HashMap<String, Any>
                    to = To(toObj["name"] as String)
                }

                var co2Emission: Co2Emission? = null
                val co2EmissionObj: HashMap<String, Any>
                if (sectionObj["co2_emission"] !== null) {
                    co2EmissionObj = sectionObj["co2_emission"] as HashMap<String, Any>
                    co2Emission = Co2Emission((co2EmissionObj["value"] as Double).toFloat())
                }

                var publicTransportDetail: PublicTransportDetail? = null
                var publicTransportDetailObj: HashMap<String, Any>
                if (sectionObj["publicTransportDetail"] !== null) {
                    publicTransportDetailObj =
                        sectionObj["publicTransportDetail"] as HashMap<String, Any>
                    publicTransportDetail = PublicTransportDetail(
                        publicTransportDetailObj["commercial_mode"] as String,
                        publicTransportDetailObj["code"] as String,
                        publicTransportDetailObj["color"] as String,
                        publicTransportDetailObj["text_color"] as String,
                        publicTransportDetailObj["direction"] as String
                    )
                }

                val section = Section(
                    sectionObj["type"] as String,
                    (sectionObj["arrival_date_time"] as Timestamp).toDate(),
                    (sectionObj["departure_date_time"] as Timestamp).toDate(),
                    from,
                    to,
                    (sectionObj["duration"] as Long).toInt(),
                    co2Emission,
                    publicTransportDetail,
                    Json.decodeFromString(sectionObj["geoJson"] as String),
                )
                sections.add(section)
            }
            val durations = map["durations"] as HashMap<String, Any>
            val co2EmissionObj = map["co2_emission"] as HashMap<String, Any>
            val carCo2EquivalentObj = map["carCo2Equivalent"] as HashMap<String, Any>
            return Journey(
                (map["arrival_date_time"] as Timestamp).toDate(),
                (map["departure_date_time"] as Timestamp).toDate(),
                Durations(
                    (durations["total"] as Long).toInt(),
                    (durations["walking"] as Long).toInt(),
                    (durations["car"] as Long).toInt()
                ),
                sections,
                Co2Emission((co2EmissionObj["value"] as Double).toFloat()),
                Co2Emission((carCo2EquivalentObj["value"] as Double).toFloat()),
                (map["nb_transfers"] as Long).toInt(),
                map["tags"] as List<String>,
            )
        }
    }
}
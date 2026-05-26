package com.example.thesswatair.other

import com.google.android.gms.maps.model.LatLng

object ThessalonikiAreas {
    data class Areas(
        val name: String,
        val coord: LatLng
    )
    val thessAreas = listOf<Areas>(
        Areas("Σίνδος", LatLng(40.6710, 22.8020)),
        Areas("Κορδελιό", LatLng(40.6750, 22.8950)),
        Areas("Εύοσμος", LatLng(40.6650, 22.9030)),
        Areas("Ωραιόκαστρο", LatLng(40.7280, 22.9150)),
        Areas("Κέντρο", LatLng(40.6320, 22.9410)),
        Areas("Πανόραμα", LatLng(40.5880, 23.0310)),
        Areas("Καλαμαριά", LatLng(40.5820, 22.9510)),
        Areas("Χορτιάτης", LatLng(40.6100, 23.1010)),
        Areas("Θέρμη",LatLng(40.5460, 23.0180)),
        Areas("Πολίχνη",LatLng(40.6620, 22.9450))
    )
    data class areasData(
        val aqi: Int = 0,
        val fwi: Double = 0.0,
        val rri: Double = 0.0,
        val heatIndex: Double = 0.0,
        val isLoading: Boolean = true
    )
}
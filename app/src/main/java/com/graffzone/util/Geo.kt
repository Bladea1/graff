package com.graffzone.util

import kotlin.math.*

data class LatLon(val lat: Double, val lon: Double)

fun haversineMeters(a: LatLon, b: LatLon): Double {
    val R = 6371000.0
    val dLat = Math.toRadians(b.lat - a.lat)
    val dLon = Math.toRadians(b.lon - a.lon)
    val lat1 = Math.toRadians(a.lat)
    val lat2 = Math.toRadians(b.lat)

    val h = sin(dLat/2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon/2).pow(2.0)
    val c = 2 * atan2(sqrt(h), sqrt(1-h))
    return R * c
}

package ca.danielstout.halicrime

import java.time.LocalDate

data class Crime(val date: LocalDate, val latitude: Double, val longitude: Double, val location: String,
    val type: String)
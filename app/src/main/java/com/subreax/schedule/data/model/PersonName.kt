package com.subreax.schedule.data.model

data class PersonName(
    val first: String,
    val last: String,
    val middle: String
) {
    fun full(): String {
        return "$last $first $middle".trim()
    }

    fun compact(): String {
        return if (last.isNotEmpty() && first.isNotEmpty() && middle.isNotEmpty()) {
            "$last ${first.firstOrNull() ?: ""}. ${middle.firstOrNull() ?: ""}."
        } else {
            ""
        }
    }

    companion object {
        fun parse(fullNameLFM: String): PersonName {
            val spl = fullNameLFM.split(' ')
                .filter { it.isNotEmpty() }

            val lastName = spl.getOrNull(0) ?: ""
            val firstName = spl.getOrNull(1) ?: ""
            val middleName = spl.getOrNull(2) ?: ""
            return PersonName(firstName, lastName, middleName)
        }
    }
}
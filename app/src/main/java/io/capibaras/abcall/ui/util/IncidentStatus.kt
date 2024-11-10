package io.capibaras.abcall.ui.util

enum class IncidentStatus {
    OPEN, ESCALATED, CLOSED, UPDATED;

    companion object {
        fun fromString(status: String): IncidentStatus? {
            return when (status.lowercase()) {
                "created" -> OPEN
                "escalated" -> ESCALATED
                "closed" -> CLOSED
                "updated" -> UPDATED
                else -> null
            }
        }
    }
}
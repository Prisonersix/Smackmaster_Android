package com.smackmaster.android.model

enum class Tone(val label: String) {
    NICE("Nice"),
    SMART("Smart"),
    RUDE("Rude"),
    BRUTAL("Brutal");

    companion object {
        fun fromKey(key: String): Tone? = entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
    }
}

package com.example.alphabetlauncher.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val firstLetter: Char = getFirstLetter(label)
)

private fun getFirstLetter(label: String): Char {
    val first = label.firstOrNull()?.uppercaseChar() ?: '#'
    return if (first in 'A'..'Z') first else '#'
}
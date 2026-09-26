package com.example.alphabetlauncher.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val label:String,
    val packageName:String,
    val icon: Drawable,
    val firstLetter: Char= label.firstOrNull()?.uppercaseChar() ?: '#'
)
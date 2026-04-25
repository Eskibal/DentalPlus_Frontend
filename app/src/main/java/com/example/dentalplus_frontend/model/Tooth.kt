package com.example.dentalplus_frontend.model

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

enum class ToothPart {
    MESIAL, DISTAL, LINGUAL, OCCLUSAL, CENTER
}

fun ToothPart.getDisplayName(): String {
    return when (this) {
        ToothPart.MESIAL -> "Mesial"
        ToothPart.DISTAL -> "Distal"
        ToothPart.LINGUAL -> "Lingual"
        ToothPart.OCCLUSAL -> "Oclusal"
        ToothPart.CENTER -> "Oclusal"
    }
}

enum class OdontogramType {
    CHILD, ADULT, BOTH
}

enum class Quadrant {
    TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
}

data class ToothState(
    val colors: SnapshotStateMap<ToothPart, Color> = mutableStateMapOf()
)

data class DentalCondition(
    val name: String,
    val color: Color
)


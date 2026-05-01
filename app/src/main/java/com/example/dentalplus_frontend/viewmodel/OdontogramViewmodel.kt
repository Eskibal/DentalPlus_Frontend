package com.example.dentalplus_frontend.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.dentalplus_frontend.model.ToothPart
import com.example.dentalplus_frontend.model.ToothState

class OdontogramViewModel : ViewModel() {

    val teethState = mutableStateMapOf<Int, ToothState>()

    fun getToothState(toothNumber: Int): ToothState {
        return teethState.getOrPut(toothNumber) { ToothState() }
    }

    fun updateToothState(
        toothNumber: Int,
        newColors: Map<ToothPart, Color>
    ) {
        val state = getToothState(toothNumber)

        state.colors.clear()
        state.colors.putAll(newColors)
    }
}
package com.example.dentalplus_frontend.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import com.example.dentalplus_frontend.model.BottomNavItem

object Constants {
    val BottomNavItems = listOf(
        // Home screen
        BottomNavItem(
            label = "Inici",
            icon = Icons.Filled.Home,
            route = "home"
        ),
        // Patients screen
        BottomNavItem(
            label = "Pacients",
            icon = Icons.AutoMirrored.Outlined.List,
            route = "patients"
        ),
        // Agenda screen
        BottomNavItem(
            label = "Agenda",
            icon = Icons.Outlined.DateRange,
            route = "agenda"
        ),
        // Profile screen
        BottomNavItem(
            label = "Perfil",
            icon = Icons.Outlined.Person,
            route = "profile"
        ),
    )
}
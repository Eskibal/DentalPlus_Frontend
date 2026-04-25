package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header


@Composable
fun ProfileScreen(navController: NavController) {

    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            DoctorHeaderCard()

            Spacer(modifier = Modifier.height(20.dp))

            ExpandableInfoBlock(
                expanded = expanded,
                onToggle = { expanded = !expanded }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ContactInfoBlock()

            Spacer(modifier = Modifier.height(30.dp))
        }

        BottomBar(navController)
    }
}


@Composable
fun DoctorHeaderCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color(0xFFEAEAEA)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    "Julio Martínez",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Especialitat: Odontòleg",
                    color = Color.Gray
                )

                Text(
                    "Día laboral: Dimarts",
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun ExpandableInfoBlock(
    expanded: Boolean,
    onToggle: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp
        ) {

            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle() }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Dades",
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        if (expanded)
                            Icons.Outlined.KeyboardArrowUp
                        else
                            Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                if (expanded) {

                    Divider(color = Color.Gray.copy(.3f))

                    InfoRow("Edat")
                    Divider(color = Color.Gray.copy(.3f))

                    InfoRow("Gènere")
                }
            }
        }
    }
}


@Composable
fun ContactInfoBlock() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {

        Column {

            ContactRow("E-mail")
            Divider(color = Color.Gray.copy(.3f))

            ContactRow("Nº de telèfon")
            Divider(color = Color.Gray.copy(.3f))

            ContactRow("Ciutat")
            Divider(color = Color.Gray.copy(.3f))

            ContactRow("Direcció")
        }
    }
}


@Composable
fun InfoRow(label: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .weight(1.5f)
                .height(20.dp)
                .background(
                    Color.Gray.copy(.3f),
                    RoundedCornerShape(4.dp)
                )
        )
    }
}


@Composable
fun ContactRow(label: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .weight(1.5f)
                .height(20.dp)
                .background(
                    Color.Gray.copy(.3f),
                    RoundedCornerShape(4.dp)
                )
        )
    }
}
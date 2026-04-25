package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
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
import com.example.dentalplus_frontend.navigation.Routes


data class PatientPreview(
    val name: String,
    val nextAppointment: String,
    val image: Int
)


@Composable
fun PatientListScreen(navController: NavController) {

    var search by remember { mutableStateOf("") }

    val todayPatients = listOf(
        PatientPreview(
            "Julio Martínez",
            "Pròxima cita: Avui a les 10:00 amb el Dr. Hernández",
            R.drawable.ic_launcher_foreground
        ),
        PatientPreview(
            "Andrea López",
            "Pròxima cita: Avui a les 10:00 amb el Dr. Hernández",
            R.drawable.ic_launcher_foreground
        )
    )

    val allPatients = listOf(
        PatientPreview(
            "Sara Suárez",
            "Pròxima cita: Avui a les 10:00 amb el Dr. Hernández",
            R.drawable.ic_launcher_foreground
        ),
        PatientPreview(
            "Manuel Sánchez",
            "Pròxima cita: Avui a les 10:00 amb el Dr. Hernández",
            R.drawable.ic_launcher_foreground
        )
    )


    Column(modifier = Modifier.fillMaxSize()) {

        Header()

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            label = { Text("Pacients") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        )


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {

            item {
                SectionTitle("Pacients d’avui")
            }

            items(todayPatients) {
                PatientCard(
                    patient = it,
                    onClick = {
                        navController.navigate(Routes.PATIENT_DETAIL)
                    }
                )
            }


            item {
                Spacer(modifier = Modifier.height(10.dp))
                SectionTitle("Tots els pacients")
            }

            items(allPatients) {
                PatientCard(
                    patient = it,
                    onClick = {
                        navController.navigate(Routes.PATIENT_DETAIL)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

        }

        BottomBar(navController)
    }
}


@Composable
fun SectionTitle(text: String) {

    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp)
    )

}


@Composable
fun PatientCard(
    patient: PatientPreview,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(patient.image),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    patient.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    patient.nextAppointment,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

            }

            Icon(
                Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.DentalPlus_FrontendTheme

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { Header() },
        bottomBar = { BottomBar(navController) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            WelcomeSection(doctorName = "Dr. García")

            Spacer(modifier = Modifier.height(24.dp))

            StatsRow()

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Agenda d'avui'",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentItem(
                time = "10:00 – 10:30",
                patientName = "Julio Martínez",
                reason = "Revisió de càries",
                doctor = "Dr. Martínez"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentItem(
                time = "11:00 – 11:30",
                patientName = "Pedro Gómez",
                reason = "Neteja dental",
                doctor = "Dr. Gutiérrez"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentItem(
                time = "11:00 – 11:30",
                patientName = "Laura Hernández",
                reason = "Revisió de càries",
                doctor = "Dr. Martínez"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WelcomeSection(doctorName: String, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Benvingut/da de nou, ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray)) {
                append(doctorName)
            }
        },
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
    )
}

@Composable
fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            count = "14",
            label = "Pacients\navui",
            backgroundColor = Color(0xFFD1F5FA),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = "5",
            label = "Cites\npendents",
            backgroundColor = Color(0xFFD1F7F1),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = "2",
            label = "Tractaments",
            subLabel = "+5 aquesta setmana",
            backgroundColor = Color(0xFFE2F9E1),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    count: String,
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    subLabel: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (subLabel != null) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(subLabel.take(2))
                        }
                        append(subLabel.drop(2))
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun AppointmentItem(
    time: String,
    patientName: String,
    reason: String,
    doctor: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.generic_icon_appointments_unselected),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Text(
                    text = patientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doctor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorHomeScreenPreview() {
    DentalPlus_FrontendTheme {
        HomeScreen(navController = rememberNavController())
    }
}

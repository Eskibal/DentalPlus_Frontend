package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.DentalPlus_FrontendTheme
import com.example.dentalplus_frontend.viewmodel.HomeUiState
import com.example.dentalplus_frontend.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadHome(context)
    }

    Scaffold(
        topBar = { Header() },
        bottomBar = { BottomBar(navController) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                HomeLoadingContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }

            uiState.errorMessage != null -> {
                HomeErrorContent(
                    message = uiState.errorMessage ?: "S'ha produït un error",
                    onRetry = {
                        homeViewModel.loadHome(context)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }

            else -> {
                HomeContent(
                    uiState = uiState,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        WelcomeSection(welcomeText = uiState.welcomeText)

        Spacer(modifier = Modifier.height(24.dp))

        StatsRow(
            todayPatientsCount = uiState.todayPatientsCount,
            pendingAppointmentsCount = uiState.pendingAppointmentsCount,
            completedAppointmentsCount = uiState.completedAppointmentsCount
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Agenda d'avui",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.appointments.isEmpty()) {
            EmptyAgendaMessage()
        } else {
            uiState.appointments.forEach { appointment ->
                HomeAppointmentItem(
                    appointment = appointment
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HomeLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun HomeErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Tornar-ho a provar")
        }
    }
}

@Composable
fun WelcomeSection(
    welcomeText: String,
    modifier: Modifier = Modifier
) {
    val parts = welcomeText.split(",", limit = 2)
    val greeting = parts.getOrNull(0)?.plus(",") ?: "Benvingut/da de nou,"
    val name = parts.getOrNull(1)?.trim().orEmpty()

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$greeting ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray)) {
                append(name)
            }
        },
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
    )
}

@Composable
fun StatsRow(
    todayPatientsCount: Int,
    pendingAppointmentsCount: Int,
    completedAppointmentsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            count = todayPatientsCount.toString(),
            label = "Pacients\navui",
            backgroundColor = Color(0xFFD1F5FA),
            modifier = Modifier.weight(1f)
        )

        StatCard(
            count = pendingAppointmentsCount.toString(),
            label = "Cites\npendents",
            backgroundColor = Color(0xFFD1F7F1),
            modifier = Modifier.weight(1f)
        )

        StatCard(
            count = completedAppointmentsCount.toString(),
            label = "Cites\ncompletades",
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
fun HomeAppointmentItem(
    appointment: BackendAppointmentDto,
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
                    text = formatAppointmentTime(
                        startDateTime = appointment.startDateTime,
                        endDateTime = appointment.endDateTime
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                Text(
                    text = appointment.patientName ?: "Pacient sense nom",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = appointment.notes?.takeIf { it.isNotBlank() }
                        ?: getAppointmentStatusText(appointment.status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appointment.dentistName ?: "Dentista no assignat",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyAgendaMessage(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Text(
            text = "No hi ha cites programades per avui",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun formatAppointmentTime(
    startDateTime: String?,
    endDateTime: String?
): String {
    val start = extractHourAndMinute(startDateTime)
    val end = extractHourAndMinute(endDateTime)

    return when {
        start != null && end != null -> "$start – $end"
        start != null -> start
        else -> "Hora no disponible"
    }
}

fun extractHourAndMinute(dateTime: String?): String? {
    if (dateTime.isNullOrBlank()) {
        return null
    }

    return try {
        dateTime.substringAfter("T").take(5)
    } catch (e: Exception) {
        null
    }
}

fun getAppointmentStatusText(status: String?): String {
    return when (status?.uppercase()) {
        "SCHEDULED" -> "Cita programada"
        "COMPLETED" -> "Cita completada"
        "CANCELLED" -> "Cita cancel·lada"
        else -> "Cita"
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorHomeScreenPreview() {
    DentalPlus_FrontendTheme {
        HomeScreen(navController = rememberNavController())
    }
}
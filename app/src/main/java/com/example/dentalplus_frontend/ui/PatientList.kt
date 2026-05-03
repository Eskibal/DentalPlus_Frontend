package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.model.BackendPatientDto
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.navigation.Routes
import com.example.dentalplus_frontend.viewmodel.PatientListUiState
import com.example.dentalplus_frontend.viewmodel.PatientListViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

data class PatientPreview(
    val patientId: Long,
    val name: String,
    val nextAppointment: String,
    val imageUrl: String?
)

@OptIn(FlowPreview::class)
@Composable
fun PatientListScreen(
    navController: NavController,
    patientListViewModel: PatientListViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by patientListViewModel.uiState.collectAsState()

    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        patientListViewModel.loadPatients(context)
    }

    LaunchedEffect(search) {
        snapshotFlow { search }
            .debounce(400)
            .distinctUntilChanged()
            .collect { query ->
                patientListViewModel.loadPatients(context, query)
            }
    }

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

        when {
            uiState.isLoading -> {
                PatientListLoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.errorMessage != null -> {
                PatientListErrorContent(
                    message = uiState.errorMessage ?: "S'ha produït un error",
                    onRetry = { patientListViewModel.loadPatients(context, search) },
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                PatientListContent(
                    uiState = uiState,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BottomBar(navController)
    }
}

@Composable
fun PatientListContent(
    uiState: PatientListUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp)
    ) {
        item {
            SectionTitle("Pacients d'avui")
        }

        if (uiState.todayPatients.isEmpty()) {
            item {
                EmptyPatientSectionMessage("No hi ha pacients amb cita avui")
            }
        } else {
            items(uiState.todayPatients) { patient ->
                PatientCard(
                    patient = patient.toPatientPreview(uiState.todayAppointments),
                    onClick = {
                        navController.navigate("${Routes.PATIENT_DETAIL}/${patient.patientId}")
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionTitle("Tots els pacients")
        }

        if (uiState.otherPatients.isEmpty()) {
            item {
                EmptyPatientSectionMessage("No hi ha més pacients")
            }
        } else {
            items(uiState.otherPatients) { patient ->
                PatientCard(
                    patient = patient.toPatientPreview(uiState.todayAppointments),
                    onClick = {
                        navController.navigate("${Routes.PATIENT_DETAIL}/${patient.patientId}")
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PatientListLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PatientListErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Tornar-ho a provar")
        }
    }
}

@Composable
fun EmptyPatientSectionMessage(text: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.Gray,
            modifier = Modifier.padding(14.dp)
        )
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
            PatientListImageFromUrl(
                imageUrl = patient.imageUrl,
                size = 50
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = patient.nextAppointment,
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

@Composable
fun PatientListImageFromUrl(
    imageUrl: String?,
    size: Int
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl.trim(),
            contentDescription = null,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

fun BackendPatientDto.toPatientPreview(
    appointments: List<BackendAppointmentDto>
): PatientPreview {
    val fullName = listOfNotNull(
        person?.name,
        person?.firstSurname,
        person?.secondSurname
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Pacient sense nom" }

    val nextAppointment = appointments
        .filter { it.patientId == patientId && it.active != false }
        .minByOrNull { it.startDateTime.orEmpty() }

    val appointmentText = if (nextAppointment != null) {
        val time = extractHourAndMinute(nextAppointment.startDateTime) ?: "hora no disponible"
        val dentist = nextAppointment.dentistName ?: "dentista no assignat"
        "Pròxima cita: avui a les $time amb $dentist"
    } else {
        "Sense cita programada avui"
    }

    return PatientPreview(
        patientId = patientId ?: -1L,
        name = fullName,
        nextAppointment = appointmentText,
        imageUrl = person?.profileImage?.trim()
    )
}
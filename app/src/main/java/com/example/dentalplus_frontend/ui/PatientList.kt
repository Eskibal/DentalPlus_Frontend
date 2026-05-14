package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.model.BackendPatientDto
import com.example.dentalplus_frontend.model.PersonDto
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
    val imageUrl: String?,
    val medicalAlert: String?
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
    var showPatientDialog by remember { mutableStateOf(false) }
    var editingPatient by remember { mutableStateOf<BackendPatientDto?>(null) }
    var deletingPatient by remember { mutableStateOf<BackendPatientDto?>(null) }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                onClick = {
                    editingPatient = null
                    showPatientDialog = true
                },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Afegir pacient",
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        if (!uiState.errorMessage.isNullOrBlank()) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        if (!uiState.successMessage.isNullOrBlank()) {
            Text(
                text = uiState.successMessage ?: "",
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        when {
            uiState.isLoading -> {
                PatientListLoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                PatientListContent(
                    uiState = uiState,
                    navController = navController,
                    onEditPatient = { patient ->
                        editingPatient = patient
                        showPatientDialog = true
                    },
                    onDeletePatient = { patient ->
                        deletingPatient = patient
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BottomBar(navController)
    }

    if (showPatientDialog) {
        PatientFormDialog(
            initialPatient = editingPatient,
            isSaving = uiState.isSaving,
            onDismiss = {
                showPatientDialog = false
                editingPatient = null
            },
            onSave = { patient ->
                val currentEditingPatient = editingPatient
                val patientId = currentEditingPatient?.patientId

                if (patientId == null) {
                    patientListViewModel.createPatient(
                        context = context,
                        patient = patient,
                        currentSearch = search,
                        onSuccess = {
                            showPatientDialog = false
                            editingPatient = null
                        }
                    )
                } else {
                    patientListViewModel.updatePatient(
                        context = context,
                        patientId = patientId,
                        patient = patient.copy(patientId = patientId),
                        currentSearch = search,
                        onSuccess = {
                            showPatientDialog = false
                            editingPatient = null
                        }
                    )
                }
            }
        )
    }

    deletingPatient?.let { patient ->
        ConfirmDeletePatientDialog(
            patient = patient,
            onDismiss = { deletingPatient = null },
            onConfirm = {
                patientListViewModel.deactivatePatient(
                    context = context,
                    patient = patient,
                    currentSearch = search,
                    onSuccess = { deletingPatient = null }
                )
            }
        )
    }
}

@Composable
fun PatientListContent(
    uiState: PatientListUiState,
    navController: NavController,
    onEditPatient: (BackendPatientDto) -> Unit,
    onDeletePatient: (BackendPatientDto) -> Unit,
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
                    },
                    onEdit = { onEditPatient(patient) },
                    onDelete = { onDeletePatient(patient) }
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
                    },
                    onEdit = { onEditPatient(patient) },
                    onDelete = { onDeletePatient(patient) }
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
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PatientListImageFromUrl(
                    imageUrl = patient.imageUrl,
                    size = 54
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = patient.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!patient.medicalAlert.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))

                        MedicalAlertBadge(
                            text = patient.medicalAlert
                        )
                    }
                }

                Icon(
                    Icons.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = patient.nextAppointment,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 66.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Editar",
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Eliminar",
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

@Composable
fun MedicalAlertBadge(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFFFF3CD),
        contentColor = Color(0xFFE65100)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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

@Composable
fun PatientFormDialog(
    initialPatient: BackendPatientDto?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (BackendPatientDto) -> Unit
) {
    var name by remember { mutableStateOf(initialPatient?.person?.name.orEmpty()) }
    var firstSurname by remember { mutableStateOf(initialPatient?.person?.firstSurname.orEmpty()) }
    var secondSurname by remember { mutableStateOf(initialPatient?.person?.secondSurname.orEmpty()) }
    var birthDate by remember { mutableStateOf(initialPatient?.person?.birthDate.orEmpty()) }
    var gender by remember { mutableStateOf(initialPatient?.person?.gender.orEmpty()) }
    var email by remember { mutableStateOf(initialPatient?.person?.email.orEmpty()) }
    var phonePrefix by remember { mutableStateOf(initialPatient?.person?.phonePrefix.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(initialPatient?.person?.phoneNumber.orEmpty()) }
    var address by remember { mutableStateOf(initialPatient?.person?.address.orEmpty()) }
    var city by remember { mutableStateOf(initialPatient?.person?.city.orEmpty()) }
    var medicalAlert by remember { mutableStateOf(initialPatient?.medicalAlert.orEmpty()) }
    var notes by remember { mutableStateOf(initialPatient?.notes.orEmpty()) }
    var personNotes by remember { mutableStateOf(initialPatient?.person?.notes.orEmpty()) }

    var formError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialPatient == null) "Afegir pacient" else "Editar pacient",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                PatientDialogTextField("Nom *", name) { name = it }
                PatientDialogTextField("Primer cognom", firstSurname) { firstSurname = it }
                PatientDialogTextField("Segon cognom", secondSurname) { secondSurname = it }
                PatientDialogTextField("Data naixement yyyy-MM-dd", birthDate) { birthDate = it }
                PatientDialogTextField("Gènere MALE/FEMALE/OTHER", gender) { gender = it }
                PatientDialogTextField("Email", email) { email = it }
                PatientDialogTextField("Prefix telèfon", phonePrefix) { phonePrefix = it }
                PatientDialogTextField("Telèfon", phoneNumber) { phoneNumber = it }
                PatientDialogTextField("Adreça", address) { address = it }
                PatientDialogTextField("Ciutat", city) { city = it }
                PatientDialogTextField("Al·lèrgies / infeccions", medicalAlert) { medicalAlert = it }
                PatientDialogTextField("Historial clínic", notes) { notes = it }
                PatientDialogTextField("Observacions personals", personNotes) { personNotes = it }

                if (!formError.isNullOrBlank()) {
                    Text(
                        text = formError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isSaving
                    ) {
                        Text("Cancel·lar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.trim().isBlank()) {
                                formError = "El nom és obligatori"
                                return@Button
                            }

                            formError = null

                            val person = PersonDto(
                                id = initialPatient?.person?.id,
                                name = name.trim(),
                                firstSurname = firstSurname.trim().ifBlank { null },
                                secondSurname = secondSurname.trim().ifBlank { null },
                                birthDate = birthDate.trim().ifBlank { null },
                                gender = gender.trim().ifBlank { null },
                                email = email.trim().ifBlank { null },
                                phonePrefix = phonePrefix.trim().ifBlank { null },
                                phoneNumber = phoneNumber.trim().ifBlank { null },
                                address = address.trim().ifBlank { null },
                                city = city.trim().ifBlank { null },
                                profileImage = initialPatient?.person?.profileImage,
                                notes = personNotes.trim().ifBlank { null }
                            )

                            val patient = BackendPatientDto(
                                patientId = initialPatient?.patientId,
                                userId = initialPatient?.userId,
                                clinicId = initialPatient?.clinicId,
                                clinicName = initialPatient?.clinicName,
                                registrationDate = initialPatient?.registrationDate,
                                active = true,
                                medicalAlert = medicalAlert.trim().ifBlank { null },
                                notes = notes.trim().ifBlank { null },
                                person = person,
                                documents = initialPatient?.documents
                            )

                            onSave(patient)
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientDialogTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun ConfirmDeletePatientDialog(
    patient: BackendPatientDto,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val name = patient.toPatientPreview(emptyList()).name

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Eliminar pacient",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Segur que vols eliminar $name? Aquesta acció desactivarà el pacient."
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel·lar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = onConfirm) {
                        Text("Eliminar")
                    }
                }
            }
        }
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
        imageUrl = person?.profileImage?.trim(),
        medicalAlert = medicalAlert
    )
}
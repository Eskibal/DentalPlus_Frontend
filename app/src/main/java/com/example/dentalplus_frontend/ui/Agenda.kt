package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dentalplus_frontend.model.AvailableBoxDto
import com.example.dentalplus_frontend.model.AvailableDentistDto
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.model.BackendPatientDto
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.viewmodel.AgendaUiState
import com.example.dentalplus_frontend.viewmodel.AgendaViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

val formatterDatePicker = DateTimeFormatter.ofPattern("dd MMMM", Locale("ca", "ES"))
val formatterDateDialog = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale("ca", "ES"))

@Composable
fun AgendaScreen(
    navController: NavController,
    agendaViewModel: AgendaViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by agendaViewModel.uiState.collectAsState()

    var selectedAppointment by remember { mutableStateOf<BackendAppointmentDto?>(null) }
    var editingAppointment by remember { mutableStateOf<BackendAppointmentDto?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    LaunchedEffect(selectedDate) {
        agendaViewModel.loadAppointments(context, selectedDate)
    }

    val filteredAppointments = uiState.appointments.filter { appointment ->
        val query = search.trim().lowercase()

        query.isBlank()
                || appointment.patientName.orEmpty().lowercase().contains(query)
                || appointment.dentistName.orEmpty().lowercase().contains(query)
                || appointment.notes.orEmpty().lowercase().contains(query)
                || appointment.status.orEmpty().lowercase().contains(query)
    }

    val hours = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00",
        "16:00", "17:00", "18:00", "19:00"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Header()

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color.DarkGray
                )
            },
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.DarkGray
                    )
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(
                    bottomStart = 0.dp,
                    topStart = 0.dp,
                    topEnd = 50.dp,
                    bottomEnd = 50.dp
                ),
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue40,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .offset(x = (-45).dp)
                    .padding(bottom = 10.dp)
            ) {
                Icon(Icons.Outlined.DateRange, contentDescription = null)

                Spacer(Modifier.width(5.dp))

                Text(
                    text = selectedDate.format(formatterDatePicker),
                    style = MaterialTheme.typography.headlineMedium,
                    fontStyle = FontStyle.Italic
                )
            }

            IconButton(
                onClick = {
                    editingAppointment = null
                    showCreateDialog = true
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Afegir cita",
                    tint = Color.DarkGray
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 20.dp)
        )

        if (!uiState.errorMessage.isNullOrBlank() && !uiState.isLoading) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        when {
            uiState.isLoading -> {
                AgendaLoadingContent(modifier = Modifier.weight(1f))
            }

            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(hours) { hour ->
                        HorizontalDivider(
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 20.dp)
                        )

                        HourRow(
                            hour = hour,
                            appointments = filteredAppointments,
                            onAppointmentClick = { selectedAppointment = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        BottomBar(navController)

        selectedAppointment?.let { appointment ->
            AppointmentDialog(
                appointment = appointment,
                selectedDate = selectedDate,
                onDismiss = { selectedAppointment = null },
                onEdit = {
                    editingAppointment = appointment
                    selectedAppointment = null
                    showCreateDialog = true
                },
                onDelete = {
                    appointment.id?.let { id ->
                        agendaViewModel.deleteAppointment(
                            context = context,
                            selectedDate = selectedDate,
                            appointmentId = id,
                            onSuccess = { selectedAppointment = null }
                        )
                    }
                }
            )
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showCreateDialog) {
        CreateAppointmentDialog(
            selectedDate = selectedDate,
            uiState = uiState,
            editingAppointment = editingAppointment,
            onDismiss = {
                showCreateDialog = false
                editingAppointment = null
            },
            onLoadAvailability = { start ->
                agendaViewModel.loadAvailability(
                    context = context,
                    selectedDate = selectedDate,
                    startTime = start
                )
            },
            onCreate = { patientId, dentistId, boxId, start, end, notes ->
                agendaViewModel.createAppointment(
                    context = context,
                    selectedDate = selectedDate,
                    patientId = patientId,
                    dentistId = dentistId,
                    boxId = boxId,
                    start = start,
                    end = end,
                    notes = notes,
                    onSuccess = {
                        showCreateDialog = false
                        editingAppointment = null
                    }
                )
            },
            onUpdate = { appointmentId, patientId, dentistId, boxId, start, end, notes ->
                agendaViewModel.updateAppointment(
                    context = context,
                    selectedDate = selectedDate,
                    appointmentId = appointmentId,
                    patientId = patientId,
                    dentistId = dentistId,
                    boxId = boxId,
                    start = start,
                    end = end,
                    notes = notes,
                    onSuccess = {
                        showCreateDialog = false
                        editingAppointment = null
                    }
                )
            }
        )
    }
}

@Composable
fun AgendaLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AgendaErrorContent(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant
                            .ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("Acceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel·lar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialTime: String = "08:00"
) {
    val initialHour = initialTime.split(":").getOrNull(0)?.toIntOrNull() ?: 8
    val initialMinute = initialTime.split(":").getOrNull(1)?.toIntOrNull() ?: 0
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona l'hora",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel·lar")
                    }
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            onTimeSelected("$hour:$minute")
                        }
                    ) {
                        Text("Acceptar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentDialog(
    selectedDate: LocalDate,
    uiState: AgendaUiState,
    editingAppointment: BackendAppointmentDto?,
    onDismiss: () -> Unit,
    onLoadAvailability: (start: String) -> Unit,
    onCreate: (
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        notes: String?
    ) -> Unit,
    onUpdate: (
        appointmentId: Long,
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        notes: String?
    ) -> Unit
) {
    val isEditing = editingAppointment != null

    var selectedPatient by remember(editingAppointment) {
        mutableStateOf<BackendPatientDto?>(
            uiState.patients.firstOrNull { it.patientId == editingAppointment?.patientId }
        )
    }

    var selectedDentist by remember(editingAppointment, uiState.availableDentists) {
        mutableStateOf<AvailableDentistDto?>(
            uiState.availableDentists.firstOrNull { it.id == editingAppointment?.dentistId }
                ?: editingAppointment?.dentistId?.let {
                    AvailableDentistDto(
                        id = it,
                        fullName = editingAppointment.dentistName ?: "Dentista actual",
                        speciality = null
                    )
                }
        )
    }

    var selectedBox by remember(editingAppointment, uiState.availableBoxes) {
        mutableStateOf<AvailableBoxDto?>(
            uiState.availableBoxes.firstOrNull { it.id == editingAppointment?.boxId }
                ?: editingAppointment?.boxId?.let {
                    AvailableBoxDto(
                        id = it,
                        name = editingAppointment.boxName ?: "Box actual"
                    )
                }
        )
    }

    var notes by remember(editingAppointment) {
        mutableStateOf(editingAppointment?.notes.orEmpty())
    }

    var start by remember(editingAppointment) {
        mutableStateOf(agendaExtractHourAndMinute(editingAppointment?.startDateTime).orEmpty())
    }

    var end by remember(editingAppointment) {
        mutableStateOf(agendaExtractHourAndMinute(editingAppointment?.endDateTime).orEmpty())
    }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }

    var patientExpanded by remember { mutableStateOf(false) }
    var dentistExpanded by remember { mutableStateOf(false) }
    var boxExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Editar cita" else "Nova cita",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tancar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedDate.format(formatterDateDialog),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Motiu o observacions") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = patientExpanded,
                    onExpandedChange = { patientExpanded = !patientExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPatient?.agendaPatientFullName().orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pacient") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = patientExpanded,
                        onDismissRequest = { patientExpanded = false }
                    ) {
                        uiState.patients.forEach { patient ->
                            DropdownMenuItem(
                                text = {
                                    Text(patient.agendaPatientFullName())
                                },
                                onClick = {
                                    selectedPatient = patient
                                    patientExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = start,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Inici") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showStartTimePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = end,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fi") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showEndTimePicker = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (!agendaIsValidTime(start)) {
                            error = "Introdueix una hora d'inici vàlida"
                            return@Button
                        }

                        error = null
                        onLoadAvailability(start)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoadingAvailability
                ) {
                    if (uiState.isLoadingAvailability) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Consultar disponibilitat")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = dentistExpanded,
                    onExpandedChange = {
                        if (uiState.availableDentists.isNotEmpty() || selectedDentist != null) {
                            dentistExpanded = !dentistExpanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = selectedDentist?.fullName.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dentista disponible") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dentistExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = dentistExpanded,
                        onDismissRequest = { dentistExpanded = false }
                    ) {
                        uiState.availableDentists.forEach { dentist ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = dentist.fullName
                                            ?: "Dentista #${dentist.id ?: "-"}"
                                    )
                                },
                                onClick = {
                                    selectedDentist = dentist
                                    dentistExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = boxExpanded,
                    onExpandedChange = {
                        if (uiState.availableBoxes.isNotEmpty() || selectedBox != null) {
                            boxExpanded = !boxExpanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = selectedBox?.name.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Box disponible") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = boxExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = boxExpanded,
                        onDismissRequest = { boxExpanded = false }
                    ) {
                        uiState.availableBoxes.forEach { box ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = box.name
                                            ?: "Box #${box.id ?: "-"}"
                                    )
                                },
                                onClick = {
                                    selectedBox = box
                                    boxExpanded = false
                                }
                            )
                        }
                    }
                }

                if (uiState.availableDentists.isEmpty()
                    && uiState.availableBoxes.isEmpty()
                    && !uiState.isLoadingAvailability
                    && start.isNotBlank()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Consulta la disponibilitat per carregar dentistes i boxes.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!error.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val cleanPatientId = selectedPatient?.patientId
                        val cleanDentistId = selectedDentist?.id
                        val cleanBoxId = selectedBox?.id

                        when {
                            cleanPatientId == null -> {
                                error = "Selecciona un pacient"
                            }

                            cleanDentistId == null -> {
                                error = "Selecciona un dentista"
                            }

                            cleanBoxId == null -> {
                                error = "Selecciona un box"
                            }

                            !agendaIsValidTime(start) || !agendaIsValidTime(end) -> {
                                error = "L'hora ha de tenir format HH:mm"
                            }

                            !agendaIsEndAfterStart(start, end) -> {
                                error = "L'hora de fi ha de ser posterior a l'hora d'inici"
                            }

                            isEditing && editingAppointment?.id != null -> {
                                error = null
                                onUpdate(
                                    editingAppointment.id,
                                    cleanPatientId,
                                    cleanDentistId,
                                    cleanBoxId,
                                    start,
                                    end,
                                    notes
                                )
                            }

                            else -> {
                                error = null
                                onCreate(
                                    cleanPatientId,
                                    cleanDentistId,
                                    cleanBoxId,
                                    start,
                                    end,
                                    notes
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(if (isEditing) "Guardar canvis" else "Crear cita")
                    }
                }
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerModal(
            initialTime = start.ifBlank { "08:00" },
            onTimeSelected = {
                start = it
                selectedDentist = null
                selectedBox = null
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerModal(
            initialTime = end.ifBlank { "08:30" },
            onTimeSelected = {
                end = it
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@Composable
fun HourRow(
    hour: String,
    appointments: List<BackendAppointmentDto>,
    onAppointmentClick: (BackendAppointmentDto) -> Unit
) {
    val hourAppointments = appointments.filter { appointment ->
        val start = agendaExtractHourAndMinute(appointment.startDateTime)
        start?.startsWith(hour.substring(0, 2)) == true
    }

    val rowHeight = 110.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
    ) {
        Text(
            text = hour,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            val count = hourAppointments.size
            hourAppointments.forEachIndexed { index, appointment ->
                val start = agendaExtractHourAndMinute(appointment.startDateTime) ?: "00:00"
                val minutes = start.split(":").getOrNull(1)?.toIntOrNull() ?: 0

                val durationMinutes = (agendaAppointmentDurationMinutes(
                    appointment.startDateTime,
                    appointment.endDateTime
                ) - 5).coerceAtLeast(25)

                val cardHeight = ((durationMinutes / 60f) * rowHeight.value).dp
                    .coerceAtLeast(48.dp)

                val offsetY = ((minutes / 60f) * rowHeight.value).dp

                // If multiple appointments start in the same hour, we offset them slightly and reduce width
                // to make them all somewhat visible even if they overlap.
                val widthFraction = if (count > 1) 1f - (index * 0.05f) else 1f
                val horizontalPaddingStart = (6 + (index * 12)).dp

                AppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment) },
                    modifier = Modifier
                        .offset(y = offsetY)
                        .fillMaxWidth(widthFraction)
                        .heightIn(min = 48.dp)
                        .height(cardHeight)
                        .padding(
                            start = horizontalPaddingStart,
                            end = 10.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: BackendAppointmentDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF7FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = Blue40
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${agendaExtractHourAndMinute(appointment.startDateTime) ?: "--:--"} - ${agendaExtractFormattedEndTime(appointment.endDateTime) ?: "--:--"}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F4E5F),
                    style = MaterialTheme.typography.bodySmall
                )

                if (appointment.notes?.isNotBlank() == true) {
                    Text(
                        text = appointment.notes,
                        color = Color(0xFF5E7E86),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(
                    text = appointment.patientName ?: "Pacient sense nom",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = appointment.dentistName ?: "Dentista no assignat",
                    color = Color.Gray,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Face, contentDescription = null, tint = Color.Gray)
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun AppointmentDialog(
    appointment: BackendAppointmentDto,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = appointment.notes?.takeIf { it.isNotBlank() }
                            ?: agendaAppointmentStatusText(appointment.status),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Tancar",
                            tint = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedDate.format(formatterDateDialog),
                    color = Color.Gray
                )

                Text(
                    text = "${agendaExtractHourAndMinute(appointment.startDateTime) ?: "--:--"} - ${agendaExtractFormattedEndTime(appointment.endDateTime) ?: "--:--"}",
                    color = Color.Gray
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                Row {
                    Text("Pacient:  ")
                    Text(
                        text = appointment.patientName ?: "No disponible",
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    Text("Dentista: ")
                    Text(
                        text = appointment.dentistName ?: "No disponible",
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    Text("Box:      ")
                    Text(
                        text = appointment.boxName ?: appointment.boxId?.let { "#$it" } ?: "No disponible",
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    Text("Estat:    ")
                    Text(
                        text = agendaAppointmentStatusText(appointment.status),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Outlined.Create,
                            contentDescription = "Editar",
                            tint = Color.DarkGray
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

fun BackendPatientDto.agendaPatientFullName(): String {
    return listOfNotNull(
        person?.name,
        person?.firstSurname,
        person?.secondSurname
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Pacient #${patientId ?: "-"}" }
}

fun agendaExtractHourAndMinute(dateTime: String?): String? {
    if (dateTime.isNullOrBlank()) return null

    return try {
        dateTime.substringAfter("T").take(5)
    } catch (e: Exception) {
        null
    }
}

fun agendaAppointmentStatusText(status: String?): String {
    return when (status?.uppercase()) {
        "SCHEDULED" -> "Cita programada"
        "COMPLETED" -> "Cita completada"
        "CANCELLED" -> "Cita cancel·lada"
        else -> "Cita"
    }
}

fun agendaIsValidTime(value: String): Boolean {
    return Regex("^\\d{2}:\\d{2}$").matches(value.trim())
}

fun agendaIsEndAfterStart(start: String, end: String): Boolean {
    return try {
        val startTime = LocalTime.parse(start)
        val endTime = LocalTime.parse(end)
        endTime.isAfter(startTime)
    } catch (e: DateTimeParseException) {
        false
    }
}

fun agendaAppointmentDurationMinutes(
    startDateTime: String?,
    endDateTime: String?
): Int {
    val start = agendaExtractHourAndMinute(startDateTime)
    val end = agendaExtractHourAndMinute(endDateTime)

    if (start.isNullOrBlank() || end.isNullOrBlank()) {
        return 60
    }

    return try {
        val startTime = LocalTime.parse(start)
        val endTime = LocalTime.parse(end)

        val duration = java.time.Duration.between(startTime, endTime).toMinutes().toInt()

        if (duration <= 0) 60 else duration
    } catch (e: Exception) {
        60
    }
}

/**
 * Extrae la hora y minutos de una cadena de fecha y hora, restando 5 minutos para el tiempo de cortesía/limpieza.
 */
fun agendaExtractFormattedEndTime(dateTime: String?, gapMinutes: Long = 5): String? {
    val timeStr = agendaExtractHourAndMinute(dateTime) ?: return null
    return try {
        val time = LocalTime.parse(timeStr)
        time.minusMinutes(gapMinutes).format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        timeStr
    }
}
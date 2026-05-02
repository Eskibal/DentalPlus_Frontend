package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.viewmodel.AgendaViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val formatterDatePicker = DateTimeFormatter.ofPattern("dd MMMM")
val formatterDateDialog = DateTimeFormatter.ofPattern("EEEE, dd MMMM")

@Composable
fun AgendaScreen(
    navController: NavController,
    agendaViewModel: AgendaViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by agendaViewModel.uiState.collectAsState()

    var selectedAppointment by remember { mutableStateOf<BackendAppointmentDto?>(null) }
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
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.DarkGray
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 20.dp)
        )

        when {
            uiState.isLoading -> {
                AgendaLoadingContent(modifier = Modifier.weight(1f))
            }

            uiState.errorMessage != null -> {
                AgendaErrorContent(
                    message = uiState.errorMessage ?: "S'ha produït un error",
                    onRetry = {
                        agendaViewModel.loadAppointments(context, selectedDate)
                    },
                    modifier = Modifier.weight(1f)
                )
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
                }
            }
        }

        BottomBar(navController)

        selectedAppointment?.let { appointment ->
            AppointmentDialog(
                appointment = appointment,
                selectedDate = selectedDate,
                onDismiss = { selectedAppointment = null },
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
            onDismiss = { showCreateDialog = false },
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
                    onSuccess = { showCreateDialog = false }
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

@Composable
fun CreateAppointmentDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onCreate: (
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        notes: String?
    ) -> Unit
) {
    var patientId by remember { mutableStateOf("") }
    var dentistId by remember { mutableStateOf("") }
    var boxId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

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
                        text = "Nova cita",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = null)
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

                OutlinedTextField(
                    value = patientId,
                    onValueChange = { patientId = it },
                    label = { Text("ID pacient") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dentistId,
                    onValueChange = { dentistId = it },
                    label = { Text("ID dentista") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = boxId,
                    onValueChange = { boxId = it },
                    label = { Text("ID box") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row {
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it },
                        label = { Text("Inici, ex. 10:00") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it },
                        label = { Text("Fi, ex. 10:30") },
                        modifier = Modifier.weight(1f)
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
                        val cleanPatientId = patientId.toLongOrNull()
                        val cleanDentistId = dentistId.toLongOrNull()
                        val cleanBoxId = boxId.toLongOrNull()

                        if (cleanPatientId == null || cleanDentistId == null || cleanBoxId == null) {
                            error = "Els IDs han de ser numèrics"
                            return@Button
                        }

                        if (!agendaIsValidTime(start) || !agendaIsValidTime(end)) {
                            error = "L'hora ha de tenir format HH:mm"
                            return@Button
                        }

                        onCreate(
                            cleanPatientId,
                            cleanDentistId,
                            cleanBoxId,
                            start,
                            end,
                            notes
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear cita")
                }
            }
        }
    }
}

@Composable
fun HourRow(
    hour: String,
    appointments: List<BackendAppointmentDto>,
    onAppointmentClick: (BackendAppointmentDto) -> Unit
) {
    val hourAppointments = appointments.filter {
        agendaExtractHourAndMinute(it.startDateTime)?.startsWith(hour.substring(0, 2)) == true
    }

    val baseHeight = 80.dp
    val extraPerAppointment = 40.dp

    val dynamicHeight = baseHeight + (extraPerAppointment * hourAppointments.size.toFloat())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dynamicHeight)
    ) {
        Text(
            text = hour,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            hourAppointments.forEach { appointment ->
                val start = agendaExtractHourAndMinute(appointment.startDateTime) ?: "00:00"
                val minutes = start.split(":").getOrNull(1)?.toIntOrNull() ?: 0
                val offsetY = (minutes / 60f) * dynamicHeight.value

                AppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment) },
                    modifier = Modifier.offset(y = offsetY.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "${agendaExtractHourAndMinute(appointment.startDateTime) ?: "--:--"} - ${agendaExtractHourAndMinute(appointment.endDateTime) ?: "--:--"}",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = appointment.notes?.takeIf { it.isNotBlank() }
                        ?: agendaAppointmentStatusText(appointment.status),
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(Color.LightGray)
            )

            Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(Icons.Outlined.Face, contentDescription = null, tint = Color.Gray)
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray)
            }

            Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(appointment.dentistName ?: "Dentista")
                Text(
                    text = appointment.patientName ?: "Pacient",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AppointmentDialog(
    appointment: BackendAppointmentDto,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
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
                        style = MaterialTheme.typography.headlineMedium
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
                    text = "${agendaExtractHourAndMinute(appointment.startDateTime) ?: "--:--"} - ${agendaExtractHourAndMinute(appointment.endDateTime) ?: "--:--"}",
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
                    Text("Doctor:   ")
                    Text(
                        text = appointment.dentistName ?: "No disponible",
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
                    IconButton(onClick = { }) {
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
                            tint = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
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
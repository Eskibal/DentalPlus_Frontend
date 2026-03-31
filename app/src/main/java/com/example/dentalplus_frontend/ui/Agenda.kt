package com.example.dentalplus_frontend.ui

import android.text.format.DateFormat
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.Appointment
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.ui.theme.Blue80
import com.example.dentalplus_frontend.ui.theme.BlueGrey40
import com.example.dentalplus_frontend.ui.theme.BlueGrey80
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

val formatterDatePicker = DateTimeFormatter.ofPattern("dd MMMM")
val formatterDateDialog = DateTimeFormatter.ofPattern("EEEE, dd MMMM")

val appointments = listOf(
    Appointment("10:00", "10:30", "Pacient", "Doctor", "Cita", LocalDate.now()),
    Appointment("12:00", "12:30", "Pacient", "Doctor", "Cita", LocalDate.now()),
    Appointment("12:30", "13:00", "Pacient", "Doctor", "Cita", LocalDate.now())
)


@Composable
fun AgendaScreen(navController: NavController){
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val filteredAppointments = appointments.filter {
        it.date == selectedDate
    }

    var showCreateDialog by remember { mutableStateOf(false) }

    var search by remember { mutableStateOf("") }

    val hours = listOf("10:00", "11:00", "12:00", "13:00",
                        "14:00", "15:00", "16:00", "17:00")

    Column(modifier = Modifier.fillMaxSize()) {
        Header()
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null,
                tint = Color.DarkGray) },
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null, tint = Color.DarkGray)
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
            Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Button(
                shape = RoundedCornerShape(bottomStart = 0.dp, topStart = 0.dp,
                                           topEnd = 50.dp, bottomEnd = 50.dp),
                onClick = { showDatePicker = true },
                colors = ButtonColors(
                    containerColor = Blue40, contentColor = Color.White,
                    disabledContainerColor = Color.Unspecified, disabledContentColor = Color.Unspecified),
                modifier = Modifier.fillMaxWidth(0.5f).offset(x = (-45).dp).padding(bottom = 10.dp)
            ) {
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
                Icon(Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.DarkGray)
            }
        }
        Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(hours) { hour ->
                Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp))
                HourRow(
                    hour = hour,
                    appointments = filteredAppointments,
                    onAppointmentClick = { selectedAppointment = it }
                )
            }
        }
        BottomBar(navController)
        selectedAppointment?.let { appointment ->
            AppointmentDialog(
                appointment = appointment,
                onDismiss = { selectedAppointment = null }
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
            onCreate = { newAppointment ->
                /* conectar backend aquí */
                showCreateDialog = false
            }
        )
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
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
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
    onCreate: (Appointment) -> Unit
) {
    var patient by remember { mutableStateOf("") }
    var doctor by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }

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
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tipus (Revisió, Neteja...)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = patient,
                    onValueChange = { patient = it },
                    label = { Text("Pacient") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = doctor,
                    onValueChange = { doctor = it },
                    label = { Text("Doctor") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it },
                        label = { Text("Inici (10:00)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it },
                        label = { Text("Fi (10:30)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val newAppointment = Appointment(
                            start = start,
                            end = end,
                            patient = patient,
                            doctor = doctor,
                            type = type,
                            date = selectedDate
                        )
                        onCreate(newAppointment)
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
fun HourRow(hour: String,
            appointments: List<Appointment>,
            onAppointmentClick: (Appointment) -> Unit
) {
    val hourAppointments = appointments.filter {
        it.start.startsWith(hour.substring(0,2))
    }
    val baseHeight = 80.dp
    val extraPerAppointment = 40.dp
    val dynamicHeight = baseHeight + (hourAppointments.size * extraPerAppointment)
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

                val minutes = appointment.start.split(":")[1].toInt()
                val offsetY = (minutes / 60f) * dynamicHeight.value

                AppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment) },
                    modifier = Modifier
                        .offset(y = offsetY.dp)
                )
            }

        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardColors(contentColor = Color.Unspecified,
            containerColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)) {
                Text("${appointment.start} - ${appointment.end}",
                    fontWeight = FontWeight.Bold)
                Text(appointment.type, color = Color.Gray)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                Arrangement.Center, Alignment.Start) {
                Icon(Icons.Outlined.Face, contentDescription = null, tint = Color.Gray)
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray)
            }
            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                Arrangement.Center, Alignment.End) {
                Text(appointment.doctor,)
                Text(appointment.patient, fontWeight = FontWeight.Bold)
            }
        }

    }
}

@Composable
fun AppointmentDialog(
    appointment: Appointment,
    onDismiss: () -> Unit
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
                        text = appointment.type,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tancar",
                            tint = Color.DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = appointment.date.format(formatterDateDialog),
                    color = Color.Gray
                    )
                Text("${appointment.start} - ${appointment.end}", color = Color.Gray)
                Divider(thickness = 1.dp, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp))
                Row {
                    Text("Pacient:  ")
                    Text(appointment.patient, fontWeight = FontWeight.Bold)
                }
                Row {
                    Text("Doctor:   ")
                    Text(appointment.doctor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { /* editar */ }) {
                        Icon(Icons.Outlined.Create, contentDescription = "Editar",
                            tint = Color.DarkGray)
                    }
                    IconButton(onClick = { /* eliminar */ }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Eliminar",
                            tint = Color.DarkGray)
                    }
                }
            }
        }
    }
}
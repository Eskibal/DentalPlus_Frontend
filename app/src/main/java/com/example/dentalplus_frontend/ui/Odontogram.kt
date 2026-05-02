package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.OdontogramType
import com.example.dentalplus_frontend.model.Quadrant
import com.example.dentalplus_frontend.model.ToothPart
import com.example.dentalplus_frontend.model.ToothState
import com.example.dentalplus_frontend.ui.theme.Blue80
import com.example.dentalplus_frontend.ui.theme.BlueGrey40
import com.example.dentalplus_frontend.viewmodel.OdontogramViewModel

@Composable
fun getTeethNumber(type: OdontogramType, quadrant: Quadrant): List<Int> {
    return when (type) {
        OdontogramType.CHILD -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(55, 54, 53, 52, 51)
            Quadrant.TOP_LEFT -> listOf(61, 62, 63, 64, 65)
            Quadrant.BOTTOM_RIGHT -> listOf(85, 84, 83, 82, 81)
            Quadrant.BOTTOM_LEFT -> listOf(71, 72, 73, 74, 75)
        }

        OdontogramType.ADULT -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(18, 17, 16, 15, 14, 13, 12, 11)
            Quadrant.TOP_LEFT -> listOf(21, 22, 23, 24, 25, 26, 27, 28)
            Quadrant.BOTTOM_RIGHT -> listOf(48, 47, 46, 45, 44, 43, 42, 41)
            Quadrant.BOTTOM_LEFT -> listOf(31, 32, 33, 34, 35, 36, 37, 38)
        }

        OdontogramType.BOTH -> emptyList()
    }
}

@Composable
fun OdontogramScreen(
    navController: NavController,
    patientId: Long,
    type: OdontogramType,
    viewModel: OdontogramViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(patientId, type) {
        viewModel.loadOrCreateOdontogram(
            context = context,
            patientId = patientId,
            selectedType = type
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                content = {
                    Icon(
                        Icons.Sharp.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            Text(
                text = "Selecciona una dent",
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row {
                    QuadrantView(navController, patientId, type, Quadrant.TOP_RIGHT, Modifier.weight(1f), viewModel)
                    QuadrantView(navController, patientId, type, Quadrant.TOP_LEFT, Modifier.weight(1f), viewModel)
                }

                Row {
                    QuadrantView(navController, patientId, type, Quadrant.BOTTOM_RIGHT, Modifier.weight(1f), viewModel)
                    QuadrantView(navController, patientId, type, Quadrant.BOTTOM_LEFT, Modifier.weight(1f), viewModel)
                }
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(0.8f)
                    .background(BlueGrey40)
            )

            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(0.95f)
                    .background(BlueGrey40)
            )
        }

        Image(
            painter = painterResource(R.drawable.generic_footer_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun QuadrantView(
    navController: NavController,
    patientId: Long,
    type: OdontogramType,
    quadrant: Quadrant,
    modifier: Modifier,
    viewModel: OdontogramViewModel
) {
    val adultSize = 20.dp
    val childSize = 20.dp

    val adultSpacing = 4.dp
    val childSpacing = 3.dp

    if (type == OdontogramType.BOTH) {
        Column(
            modifier = modifier
                .padding(bottom = 10.dp)
                .clickable {
                    navController.navigate("quadrant/$patientId/${quadrant.name}/${type.name}")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quadrant == Quadrant.TOP_RIGHT || quadrant == Quadrant.TOP_LEFT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        getTeethNumber(OdontogramType.ADULT, quadrant).forEach {
                            Text(it.toString(), fontSize = 10.sp, color = Blue80)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(adultSpacing)) {
                        val teeth = getTeethNumber(OdontogramType.ADULT, quadrant)

                        teeth.forEach { tooth ->
                            val state = viewModel.getToothState(tooth)
                            val isFrontal = tooth % 10 <= 3

                            if (isFrontal) {
                                FrontalToothInteractive(adultSize, state) {}
                            } else {
                                RearToothInteractive(adultSize, state) {}
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(childSpacing)) {
                        val teeth = getTeethNumber(OdontogramType.CHILD, quadrant)

                        teeth.forEach { tooth ->
                            val state = viewModel.getToothState(tooth)
                            val isFrontal = tooth % 10 <= 3

                            if (isFrontal) {
                                FrontalToothInteractive(childSize, state) {}
                            } else {
                                RearToothInteractive(childSize, state) {}
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        getTeethNumber(OdontogramType.CHILD, quadrant).forEach {
                            Text(it.toString(), fontSize = 9.sp, color = Blue80)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        getTeethNumber(OdontogramType.CHILD, quadrant).forEach {
                            Text(it.toString(), fontSize = 9.sp, color = Blue80)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(childSpacing)) {
                        val teeth = getTeethNumber(OdontogramType.CHILD, quadrant)

                        teeth.forEach { tooth ->
                            val state = viewModel.getToothState(tooth)
                            val isFrontal = tooth % 10 <= 3

                            if (isFrontal) {
                                FrontalToothInteractive(childSize, state) {}
                            } else {
                                RearToothInteractive(childSize, state) {}
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(adultSpacing)) {
                        val teeth = getTeethNumber(OdontogramType.ADULT, quadrant)

                        teeth.forEach { tooth ->
                            val state = viewModel.getToothState(tooth)
                            val isFrontal = tooth % 10 <= 3

                            if (isFrontal) {
                                FrontalToothInteractive(adultSize, state) {}
                            } else {
                                RearToothInteractive(adultSize, state) {}
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        getTeethNumber(OdontogramType.ADULT, quadrant).forEach {
                            Text(it.toString(), fontSize = 10.sp, color = Blue80)
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .padding(bottom = 10.dp)
                .clickable {
                    navController.navigate("quadrant/$patientId/${quadrant.name}/${type.name}")
                },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val teeth = getTeethNumber(type, quadrant)

            val size = if (type == OdontogramType.CHILD) 30.dp else 20.dp
            val numberSize = if (type == OdontogramType.CHILD) 15.sp else 10.sp
            val spacing = if (type == OdontogramType.CHILD) 15.dp else 12.dp

            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                teeth.forEach {
                    Text(it.toString(), fontSize = numberSize, color = Blue80)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                teeth.forEach { tooth ->
                    val state = viewModel.getToothState(tooth)
                    val isFrontal = tooth % 10 <= 3

                    if (isFrontal) {
                        FrontalToothInteractive(size, state) {}
                    } else {
                        RearToothInteractive(size, state) {}
                    }
                }
            }
        }
    }
}

fun Quadrant.getDisplayName(): String {
    return when (this) {
        Quadrant.TOP_RIGHT -> "Superior dreta"
        Quadrant.TOP_LEFT -> "Superior esquerra"
        Quadrant.BOTTOM_RIGHT -> "Inferior dreta"
        Quadrant.BOTTOM_LEFT -> "Inferior esquerra"
    }
}

@Composable
fun QuadrantZoomedScreen(
    navController: NavController,
    patientId: Long,
    quadrant: Quadrant,
    type: OdontogramType,
    viewModel: OdontogramViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                content = {
                    Icon(
                        Icons.Sharp.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            Text(
                text = quadrant.getDisplayName(),
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (type == OdontogramType.BOTH) {
                val adultTeeth = getTeethNumber(OdontogramType.ADULT, quadrant)
                val childTeeth = getTeethNumber(OdontogramType.CHILD, quadrant)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        adultTeeth.forEach { tooth ->
                            ClickableTooth(
                                patientId = patientId,
                                toothNumber = tooth,
                                navController = navController,
                                toothSize = 35.dp,
                                viewModel = viewModel
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        childTeeth.forEach { tooth ->
                            ClickableTooth(
                                patientId = patientId,
                                toothNumber = tooth,
                                navController = navController,
                                toothSize = 35.dp,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val teeth = getTeethNumber(type, quadrant)

                        teeth.forEach { tooth ->
                            ClickableTooth(
                                patientId = patientId,
                                toothNumber = tooth,
                                navController = navController,
                                toothSize = if (type == OdontogramType.CHILD) 50.dp else 35.dp,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.generic_footer_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ClickableTooth(
    patientId: Long,
    toothNumber: Int,
    navController: NavController,
    toothSize: Dp,
    viewModel: OdontogramViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            navController.navigate("tooth/$patientId/$toothNumber")
        }
    ) {
        Text(
            text = toothNumber.toString(),
            fontSize = if (toothSize > 25.dp) 12.sp else 9.sp,
            color = Blue80
        )

        Spacer(modifier = Modifier.height(2.dp))

        val isFrontal = toothNumber % 10 <= 3
        val state = viewModel.getToothState(toothNumber)

        if (isFrontal) {
            FrontalToothInteractive(
                toothSize = toothSize,
                state = state,
                onPartClick = {
                    navController.navigate("tooth/$patientId/$toothNumber")
                }
            )
        } else {
            RearToothInteractive(
                toothSize = toothSize,
                state = state,
                onPartClick = {
                    navController.navigate("tooth/$patientId/$toothNumber")
                }
            )
        }
    }
}

@Composable
fun ToothDetailScreen(
    navController: NavController,
    patientId: Long,
    toothNumber: Int,
    viewModel: OdontogramViewModel
) {
    val context = LocalContext.current

    var selectedPart by remember { mutableStateOf<ToothPart?>(null) }
    val originalState = viewModel.getToothState(toothNumber)

    val editableColors = remember(toothNumber) {
        mutableStateMapOf<ToothPart, Color>().apply {
            putAll(originalState.colors)
        }
    }

    val localState = ToothState(editableColors)
    val hasChanges = editableColors != originalState.colors
    val isFrontal = toothNumber % 10 <= 3

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Detalls de la dent",
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(Modifier.height(35.dp))

        if (isFrontal) {
            FrontalToothInteractive(150.dp, localState) { selectedPart = it }
        } else {
            RearToothInteractive(150.dp, localState) { selectedPart = it }
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Núm. de la dent: $toothNumber")
            Text("Tipus: ${if (isFrontal) "Frontal" else "Del darrere"}")

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 90.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mesial: ${editableColors[ToothPart.MESIAL]?.toDentalLabel() ?: "-"}")
                Text("Lingual: ${editableColors[ToothPart.LINGUAL]?.toDentalLabel() ?: "-"}")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 90.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Distal: ${editableColors[ToothPart.DISTAL]?.toDentalLabel() ?: "-"}")
                Text("Oclusal: ${editableColors[ToothPart.OCCLUSAL]?.toDentalLabel() ?: "-"}")
            }

            if (!isFrontal) {
                Text("Central: ${editableColors[ToothPart.CENTER]?.toDentalLabel() ?: "-"}")
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.updateToothState(
                    context = context,
                    patientId = patientId,
                    toothNumber = toothNumber,
                    colors = editableColors
                )
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            enabled = hasChanges
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Cancel·lar")
        }

        Image(
            painter = painterResource(R.drawable.generic_footer_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }

    if (selectedPart != null) {
        ColorPickerDialog(
            onColorSelected = { color ->
                selectedPart?.let {
                    if (color == Color.White) {
                        editableColors.remove(it)
                    } else {
                        editableColors[it] = color
                    }
                }
            },
            onDismiss = { selectedPart = null }
        )
    }
}

@Composable
fun ColorPickerDialog(
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        "Patologia / Lesió" to Color(0xFFDC0000),
        "Tractament ja fet" to Color(0xFF0000DC)
    )

    val colors2 = listOf(
        "Càries radiogràfiques" to Color(0xFF00DC00),
        "Segellat de foses i fissures" to Color(0xFFFFD600)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        shape = RoundedCornerShape(15.dp),
        title = { Text("Selecciona un color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        colors.forEach { (label, color) ->
                            TextButton(
                                modifier = Modifier.height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(5.dp),
                                onClick = {
                                    onColorSelected(color)
                                    onDismiss()
                                }
                            ) {
                                Text(label, textAlign = TextAlign.Center, softWrap = true)
                            }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        colors2.forEach { (label, color) ->
                            TextButton(
                                modifier = Modifier.height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(5.dp),
                                onClick = {
                                    onColorSelected(color)
                                    onDismiss()
                                }
                            ) {
                                Text(label, textAlign = TextAlign.Center, softWrap = true)
                            }
                        }
                    }
                }

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        onColorSelected(Color.Black)
                        onDismiss()
                    }
                ) {
                    Text("Absència natural", textAlign = TextAlign.Center, softWrap = true)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onColorSelected(Color.White)
                            onDismiss()
                        }
                    ) {
                        Text("Esborrar")
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun RearToothInteractive(
    toothSize: Dp,
    state: ToothState,
    onPartClick: (ToothPart) -> Unit
) {
    Canvas(
        modifier = Modifier
            .size(toothSize)
            .border(2.dp, Color.Black)
            .pointerInput(toothSize) {
                detectTapGestures { offset ->
                    val w = size.width
                    val h = size.height

                    val left = w * 0.25f
                    val right = w * 0.75f
                    val top = h * 0.25f
                    val bottom = h * 0.75f

                    val part = when {
                        offset.x in left..right && offset.y in top..bottom -> ToothPart.CENTER
                        offset.y < top && offset.x in left..right -> ToothPart.OCCLUSAL
                        offset.y > bottom && offset.x in left..right -> ToothPart.LINGUAL
                        offset.x < left && offset.y in top..bottom -> ToothPart.MESIAL
                        offset.x > right && offset.y in top..bottom -> ToothPart.DISTAL
                        else -> null
                    }

                    part?.let { onPartClick(it) }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        val left = w * 0.25f
        val right = w * 0.75f
        val top = h * 0.25f
        val bottom = h * 0.75f

        val topPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(right, top)
            lineTo(left, top)
            close()
        }

        val bottomPath = Path().apply {
            moveTo(0f, h)
            lineTo(w, h)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }

        val leftPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(left, top)
            lineTo(left, bottom)
            lineTo(0f, h)
            close()
        }

        val rightPath = Path().apply {
            moveTo(w, 0f)
            lineTo(right, top)
            lineTo(right, bottom)
            lineTo(w, h)
            close()
        }

        state.colors[ToothPart.OCCLUSAL]?.let { drawPath(topPath, it.copy(alpha = 1f)) }
        state.colors[ToothPart.LINGUAL]?.let { drawPath(bottomPath, it.copy(alpha = 1f)) }
        state.colors[ToothPart.MESIAL]?.let { drawPath(leftPath, it.copy(alpha = 1f)) }
        state.colors[ToothPart.DISTAL]?.let { drawPath(rightPath, it.copy(alpha = 1f)) }

        state.colors[ToothPart.CENTER]?.let {
            drawRect(
                it.copy(alpha = 1f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
        }

        drawRect(Color.Black, style = Stroke(3f))
        drawLine(Color.Black, Offset(0f, 0f), Offset(left, top), 3f)
        drawLine(Color.Black, Offset(w, 0f), Offset(right, top), 3f)
        drawLine(Color.Black, Offset(0f, h), Offset(left, bottom), 3f)
        drawLine(Color.Black, Offset(w, h), Offset(right, bottom), 3f)

        drawRect(
            Color.Black,
            Offset(left, top),
            Size(right - left, bottom - top),
            style = Stroke(3f)
        )
    }
}

@Composable
fun FrontalToothInteractive(
    toothSize: Dp,
    state: ToothState,
    onPartClick: (ToothPart) -> Unit
) {
    Canvas(
        modifier = Modifier
            .size(toothSize)
            .border(2.dp, Color.Black)
            .pointerInput(toothSize) {
                detectTapGestures { offset ->
                    val w = size.width
                    val h = size.height

                    val part = when {
                        offset.y < offset.x && offset.y < (h - offset.x) -> ToothPart.OCCLUSAL
                        offset.y > offset.x && offset.y > (h - offset.x) -> ToothPart.LINGUAL
                        offset.x < w / 2 -> ToothPart.MESIAL
                        else -> ToothPart.DISTAL
                    }

                    onPartClick(part)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)

        val top = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(center.x, center.y)
            close()
        }

        val bottom = Path().apply {
            moveTo(0f, h)
            lineTo(w, h)
            lineTo(center.x, center.y)
            close()
        }

        val left = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, h)
            lineTo(center.x, center.y)
            close()
        }

        val right = Path().apply {
            moveTo(w, 0f)
            lineTo(w, h)
            lineTo(center.x, center.y)
            close()
        }

        state.colors[ToothPart.OCCLUSAL]?.let { drawPath(top, it.copy(alpha = 1f)) }
        state.colors[ToothPart.LINGUAL]?.let { drawPath(bottom, it.copy(alpha = 1f)) }
        state.colors[ToothPart.MESIAL]?.let { drawPath(left, it.copy(alpha = 1f)) }
        state.colors[ToothPart.DISTAL]?.let { drawPath(right, it.copy(alpha = 1f)) }

        drawRect(Color.Black, style = Stroke(3f))
        drawLine(Color.Black, Offset(0f, 0f), Offset(w, h), 3f)
        drawLine(Color.Black, Offset(w, 0f), Offset(0f, h), 3f)
    }
}

fun Color.toDentalLabel(): String {
    return when (this) {
        Color(0xFFDC0000) -> "Patologia"
        Color(0xFF0000DC) -> "Tractament fet"
        Color(0xFF00DC00) -> "Càries RX"
        Color(0xFFFFD600) -> "Segellat"
        Color.Black -> "Absència"
        else -> "Marca"
    }
}
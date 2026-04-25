package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import com.example.dentalplus_frontend.ui.theme.Blue80
import com.example.dentalplus_frontend.ui.theme.BlueGrey40
import com.example.dentalplus_frontend.model.ToothPart
import com.example.dentalplus_frontend.model.ToothState
import com.example.dentalplus_frontend.model.getDisplayName
import com.example.dentalplus_frontend.ui.ColorPickerDialog

// ---------------- DATOS ----------------

@Composable
fun getTeethNumber(type: OdontogramType, quadrant: Quadrant): List<Int> {
    return when (type) {

        OdontogramType.CHILD -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(55,54,53,52,51)
            Quadrant.TOP_LEFT -> listOf(61,62,63,64,65)
            Quadrant.BOTTOM_RIGHT -> listOf(85,84,83,82,81)
            Quadrant.BOTTOM_LEFT -> listOf(71,72,73,74,75)
        }

        OdontogramType.ADULT -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(18,17,16,15,14,13,12,11)
            Quadrant.TOP_LEFT -> listOf(21,22,23,24,25,26,27,28)
            Quadrant.BOTTOM_RIGHT -> listOf(48,47,46,45,44,43,42,41)
            Quadrant.BOTTOM_LEFT -> listOf(31,32,33,34,35,36,37,38)
        }

        OdontogramType.BOTH -> emptyList()
    }
}
@Composable
fun getTeeth(type: OdontogramType, quadrant: Quadrant, toothSize: Dp): List<Unit> {
    return when (type) {

        OdontogramType.CHILD -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(RearTooth(toothSize), RearTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize))
            Quadrant.TOP_LEFT -> listOf(FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize))
            Quadrant.BOTTOM_RIGHT -> listOf(RearTooth(toothSize), RearTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize))
            Quadrant.BOTTOM_LEFT -> listOf(FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize))
        }

        OdontogramType.ADULT -> when (quadrant) {
            Quadrant.TOP_RIGHT -> listOf(RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize))
            Quadrant.TOP_LEFT -> listOf(FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize))
            Quadrant.BOTTOM_RIGHT -> listOf(RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize))
            Quadrant.BOTTOM_LEFT -> listOf(FrontalTooth(toothSize), FrontalTooth(toothSize), FrontalTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize), RearTooth(toothSize))

        }

        OdontogramType.BOTH -> emptyList()
    }
}

// ---------------- SCREEN PRINCIPAL ----------------

@Composable
fun OdontogramScreen(
    navController: NavController,
    type: OdontogramType
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, start = 10.dp), Arrangement.spacedBy(3.dp), Alignment.CenterVertically){
            IconButton(onClick = { navController.popBackStack() },
                content = { Icon(Icons.Sharp.ArrowBack, contentDescription = null,
                    modifier = Modifier.size(30.dp)) })
            Text(text = "Selecciona una dent", modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium)
        }
        // ODONTOGRAMA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row {
                    QuadrantView(navController, type, Quadrant.TOP_RIGHT, Modifier.weight(1f))
                    QuadrantView(navController, type, Quadrant.TOP_LEFT, Modifier.weight(1f))
                }

                Row {
                    QuadrantView(navController, type, Quadrant.BOTTOM_RIGHT, Modifier.weight(1f))
                    QuadrantView(navController, type, Quadrant.BOTTOM_LEFT, Modifier.weight(1f))
                }
            }

            // cruz
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

// ---------------- CUADRANTE ----------------

@Composable
fun QuadrantView(
    navController: NavController,
    type: OdontogramType,
    quadrant: Quadrant,
    modifier: Modifier
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
                    navController.navigate("quadrant/${quadrant.name}/${type.name}")
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
                        getTeeth(OdontogramType.ADULT, quadrant, adultSize)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(childSpacing)) {
                        getTeeth(OdontogramType.CHILD, quadrant, childSize)
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
                        getTeeth(OdontogramType.CHILD, quadrant, childSize)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(adultSpacing)) {
                        getTeeth(OdontogramType.ADULT, quadrant, adultSize)
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
    }
    else {
        Column(modifier = modifier
            .padding(bottom = 10.dp)
            .clickable {
                navController.navigate("quadrant/${quadrant.name}/${type.name}")
            }, Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            if (type == OdontogramType.CHILD)
            {
                Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    getTeethNumber(type, quadrant).forEach {
                        Text(it.toString(), fontSize = 15.sp, color = Blue80)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    getTeeth(type, quadrant, 30.dp)
                }
            }
            else
            {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    getTeethNumber(type, quadrant).forEach {
                        Text(it.toString(), fontSize = 10.sp, color = Blue80)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    getTeeth(type, quadrant, 20.dp)
                }
            }
        }
    }
}

// ---------------- DETALLE CUADRANTE ----------------
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
    quadrant: Quadrant,
    type: OdontogramType
) {

    Column(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, start = 10.dp), Arrangement.spacedBy(3.dp), Alignment.CenterVertically){
            IconButton(onClick = { navController.popBackStack() },
                content = { Icon(Icons.Sharp.ArrowBack, contentDescription = null,
                    modifier = Modifier.size(30.dp)) })
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
                                toothNumber = tooth,
                                navController = navController,
                                toothSize = 35.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        childTeeth.forEach { tooth ->
                            ClickableTooth(
                                toothNumber = tooth,
                                navController = navController,
                                toothSize = 35.dp
                            )
                        }
                    }
                }
            }
            else {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val teeth = getTeethNumber(type, quadrant)

                        if (type == OdontogramType.CHILD) {
                            teeth.forEach { tooth ->
                                ClickableTooth(toothNumber = tooth, navController = navController,
                                    toothSize = 50.dp)
                            }
                        } else {
                            teeth.forEach { tooth ->
                                ClickableTooth(toothNumber = tooth, navController = navController,
                                    toothSize = 35.dp)
                            }
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
    toothNumber: Int,
    navController: NavController,
    toothSize: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            navController.navigate("tooth/$toothNumber")
        }
    ) {
        Text(
            text = toothNumber.toString(),
            fontSize = if (toothSize > 25.dp) 12.sp else 9.sp,
            color = Blue80
        )
        Spacer(modifier = Modifier.height(2.dp))

        val isFrontal = toothNumber % 10 <= 3

        if (isFrontal) {
            FrontalTooth(toothSize)
        } else {
            RearTooth(toothSize)
        }
    }
}



@Composable
fun RearTooth(toothSize: Dp) {
    Canvas(modifier = Modifier
        .size(toothSize)
        .border(2.dp, Color(0xFF000000))
    ) {

        val w = size.width
        val h = size.height

        drawRect(color = Color(0xFF000000), topLeft = Offset(w * 0.25f, h * 0.25f), size = Size(w * 0.5f, h * 0.5f), style = Stroke(width = 3f))

        drawLine(Color(0xFF000000), Offset(0f, 0f), Offset(w * 0.25f, h * 0.25f), 3f)
        drawLine(Color(0xFF000000), Offset(w, 0f), Offset(w * 0.75f, h * 0.25f), 3f)
        drawLine(Color(0xFF000000), Offset(0f, h), Offset(w * 0.25f, h * 0.75f), 3f)
        drawLine(Color(0xFF000000), Offset(w, h), Offset(w * 0.75f, h * 0.75f), 3f)
    }
}

@Composable
fun FrontalTooth(toothSize: Dp) {
    Canvas(
        modifier = Modifier
            .size(toothSize)
            .border(2.dp, Color(0xFF000000))
    ) {

        val w = size.width
        val h = size.height

        drawLine(Color(0xFF000000), Offset(w, 0f), Offset(w*0.75f, h*0.25f), 3f)
        drawLine(Color(0xFF000000), Offset(0f, h), Offset(w*0.9f, h*.1f), 3f)
        drawLine(Color(0xFF000000), Offset(0f, 0f), Offset(w*1f, h*1f), 3f)
    }
}

// ---------------- DETALLE DIENTE ----------------
@Composable
fun ToothDetailScreen(
    navController: NavController,
    toothNumber: Int
) {
    var selectedPart by remember { mutableStateOf<ToothPart?>(null) }
    val state = remember { ToothState() }

    val isFrontal = toothNumber % 10 <= 3

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, start = 10.dp), Arrangement.spacedBy(3.dp), Alignment.CenterVertically){
            IconButton(onClick = { navController.popBackStack() },
                content = { Icon(Icons.Sharp.ArrowBack, contentDescription = null,
                    modifier = Modifier.size(30.dp)) })
            Text(
                text = "Detalls de la dent",
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(Modifier.height(35.dp))

        if (isFrontal)
        {
            FrontalToothInteractive(150.dp, state) { selectedPart = it }
        }
        else
        {
            RearToothInteractive(150.dp, state) { selectedPart = it }
        }

        Spacer(Modifier.height(20.dp))

        Column (modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Nº de la dent: $toothNumber")
            Text("Tipus: ${if (isFrontal) "Frontal" else "Del darrere"}")

            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(end = 90.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Mesial: -")
                Text("Lingual: -")
            }
            Row(modifier = Modifier.fillMaxWidth().padding(end = 90.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Distal: -")
                Text("Oclusal: -")
            }
            if (!isFrontal){
                Text("Central: -")
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {}, modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(50.dp), shape = RoundedCornerShape(10.dp)) {
            Text("Guardar")
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
                    if (color == Color.White){
                        state.colors.remove(it)
                    } else {
                        state.colors[it] = color
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
        "Patología / Lesió" to Color(0xFFDC0000),
        "Tractament ja fet" to Color(0xFF0000DC))
    val colors2 = listOf(
        "Càries radiogràfiques" to Color(0xFF00DC00),
        "Segellat de foses y fisures" to Color(0xFFFFD600)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        shape = RoundedCornerShape(15.dp),
        title = { Text("Selecciona un color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        colors.forEach { (label, color) ->
                            TextButton(modifier = Modifier.height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(5.dp), onClick = {
                                    onColorSelected(color)
                                    onDismiss()
                                },
                                content = {
                                    Text(label, textAlign = TextAlign.Center, softWrap = true)
                            })
                        }
                    }
                    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        colors2.forEach { (label, color) ->
                            TextButton(modifier = Modifier.height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(5.dp), onClick = {
                                    onColorSelected(color)
                                    onDismiss()
                                },
                                content = {
                                    Text(label, textAlign = TextAlign.Center, softWrap = true)
                                })
                        }
                    }
                }
                TextButton(modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(5.dp), onClick = {
                        onColorSelected(Color.Black)
                        onDismiss()
                    },
                    content = {
                        Text("Absència natural", textAlign = TextAlign.Center, softWrap = true)
                    })
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onColorSelected(Color.White)
                        onDismiss()
                    },) {
                        Text("Esborrar")
                        Icon(Icons.Outlined.Delete, contentDescription = null, Modifier.size(30.dp))
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
            .pointerInput(Unit) {
                detectTapGestures { offset ->

                    val w = toothSize.toPx()
                    val h = toothSize.toPx()

                    val left = w * 0.25f
                    val right = w * 0.75f
                    val top = h * 0.25f
                    val bottom = h * 0.75f

                    val part = when
                    {
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

        // PATHS CORRECTOS (trapecios)

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

        val centerRect = Rect(left, top, right, bottom)

        // PINTADO
        state.colors[ToothPart.OCCLUSAL]?.let {
            drawPath(topPath, it.copy(alpha = 1f))
        }

        state.colors[ToothPart.LINGUAL]?.let {
            drawPath(bottomPath, it.copy(alpha = 1f))
        }

        state.colors[ToothPart.MESIAL]?.let {
            drawPath(leftPath, it.copy(alpha = 1f))
        }

        state.colors[ToothPart.DISTAL]?.let {
            drawPath(rightPath, it.copy(alpha = 1f))
        }

        state.colors[ToothPart.CENTER]?.let {
            drawRect(it.copy(alpha = 1f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top))
        }

        // DIBUJO
        drawRect(Color.Black, style = Stroke(3f))

        drawLine(Color.Black, Offset(0f, 0f), Offset(left, top), 3f)
        drawLine(Color.Black, Offset(w, 0f), Offset(right, top), 3f)
        drawLine(Color.Black, Offset(0f, h), Offset(left, bottom), 3f)
        drawLine(Color.Black, Offset(w, h), Offset(right, bottom), 3f)

        drawRect(Color.Black,
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
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val w = toothSize.toPx()
                    val h = toothSize.toPx()

                    val part = when
                    {
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
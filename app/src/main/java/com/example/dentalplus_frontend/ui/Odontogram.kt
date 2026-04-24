package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.ui.theme.Blue80
import com.example.dentalplus_frontend.ui.theme.BlueGrey40

// ---------------- ENUMS ----------------

enum class OdontogramType {
    CHILD, ADULT, BOTH
}

enum class Quadrant {
    TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
}

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
            .padding(end = 20.dp, start = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically){
            IconButton(onClick = { navController.popBackStack() },
                content = { Icon(Icons.Sharp.ArrowBack, contentDescription = null,
                    modifier = Modifier.size(30.dp)) })
            Text(text = "Escull una dent", modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.displaySmall)
        }
        Divider(Modifier.padding(vertical = 10.dp, horizontal = 10.dp))
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
        Divider(Modifier.padding(vertical = 10.dp, horizontal = 10.dp))
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
            .padding(end = 20.dp, start = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically){
            IconButton(onClick = { navController.popBackStack() },
                content = { Icon(Icons.Sharp.ArrowBack, contentDescription = null,
                    modifier = Modifier.size(30.dp)) })
            Text(
                text = quadrant.getDisplayName(),
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Divider(Modifier.padding(vertical = 10.dp, horizontal = 10.dp))
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
        Divider(Modifier.padding(vertical = 10.dp, horizontal = 10.dp))
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
package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.sharp.Face
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.OdontogramType
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.navigation.Routes
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.ui.theme.Blue80
import com.example.dentalplus_frontend.ui.theme.BlueGrey40
import com.example.dentalplus_frontend.ui.theme.BlueGrey80
import com.example.dentalplus_frontend.ui.theme.DentalPlus_FrontendTheme
import com.example.dentalplus_frontend.utils.Constants

@Composable
fun PatientScreen(navController: NavController) {
    var showSelectionDialog by remember { mutableStateOf(false) }

    var selectedOdontogramType by remember { mutableStateOf<OdontogramType?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()
        Column(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())) {
            PatientHeaderCard()
            Spacer(modifier = Modifier.height(20.dp))
            InfoBlock(
                items = listOf(
                    "DNI/NIE" to "",
                    "Nº de Telèfon" to "",
                    "Domicili" to "",
                    "E-mail" to "",
                    "Població" to ""
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoBlock(
                items = listOf(
                    "Historial Clínic" to "",
                    "Al·lèrgies" to "",
                    "Observacions" to ""
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            BigActionButton("Odontograma", onClick = { showSelectionDialog = true })
            Spacer(modifier = Modifier.height(30.dp))
        }
        BottomBar(navController)
    }
    if(showSelectionDialog) {
        SelectionDialog(
            onDismiss = { showSelectionDialog = false },
            onTypeSelected = { type ->
                showSelectionDialog = false
                navController.navigate("odontogram/${type.name}")
            }
        )
    }
}

@Composable
fun PatientHeaderCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .padding(top = 20.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color(0xFFEAEAEA)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FOTO
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Nom Pacient",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text("Edat XX - Gènere X", color = Color.Gray)
                Text("D. de naix. DD/MM/YYYY", color = Color.Gray)
                Text("ID #XXXXXX", color = Color.Gray)
            }
        }
    }
}

@Composable
fun InfoBlock(title: String? = null, items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.first,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold
                        )
                        Divider(modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                            thickness = DividerDefaults.Thickness, color = Color.Gray.copy(0.4f))
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(20.dp)
                                .background(Color.Gray.copy(0.3f), RoundedCornerShape(4.dp))
                        )
                    }
                    if (index != items.lastIndex) {
                        Divider(color = Color.Gray.copy(0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun BigActionButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun SelectionDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (OdontogramType) -> Unit
)
{
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
                        text = "Selecciona una edat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    /*IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = null)
                    }*/
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 25.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            onTypeSelected(OdontogramType.CHILD)
                            onDismiss()
                        }, shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40, contentColor = Color.Unspecified,
                        modifier = Modifier.size(70.dp)
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            Arrangement.Center,
                            Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.Face, contentDescription = null)
                            Text(
                                text = "Nen",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                softWrap = true,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Surface(
                        onClick = {
                            onTypeSelected(OdontogramType.BOTH)
                            onDismiss()
                        }, shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40, contentColor = Color.Unspecified,
                        modifier = Modifier.size(70.dp)
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            Arrangement.Center,
                            Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Face, contentDescription = null)
                            Text(
                                text = "Jove",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                softWrap = true,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Surface(
                        onClick = {
                            onTypeSelected(OdontogramType.ADULT)
                            onDismiss()
                        }, shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40, contentColor = Color.Unspecified,
                        modifier = Modifier.size(70.dp)
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            Arrangement.Center,
                            Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                            Text(
                                text = "Adult",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                softWrap = true,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                }
            }
        }
    }
}
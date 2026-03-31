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
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.navigation.Routes
import com.example.dentalplus_frontend.ui.theme.DentalPlus_FrontendTheme
import com.example.dentalplus_frontend.utils.Constants

@Composable
fun PatientScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            PatientHeaderCard()
            Spacer(modifier = Modifier.height(20.dp))
            InfoBlock(
                items = listOf(
                    "DNI/NIE" to "",
                    "Telèfon" to "",
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
            BigActionButton("Odontograma")
            Spacer(modifier = Modifier.height(30.dp))
        }
        BottomBar(navController)
    }
}

@Composable
fun PatientHeaderCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp).padding(top = 20.dp),
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
                        Divider(modifier = Modifier.fillMaxHeight().width(1.dp),
                            thickness = DividerDefaults.Thickness, color = Color.Gray.copy(0.4f))
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(20.dp)
                                .background(
                                    Color.Gray.copy(0.3f),
                                    RoundedCornerShape(4.dp)
                                )
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
fun BigActionButton(text: String, onClick: () -> Unit = {}) {
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
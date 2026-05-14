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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.navigation.Routes
import com.example.dentalplus_frontend.viewmodel.ProfileUiState
import com.example.dentalplus_frontend.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()

        when {
            uiState.isLoading -> {
                ProfileLoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.errorMessage != null -> {
                ProfileErrorContent(
                    message = uiState.errorMessage ?: "S'ha produït un error",
                    onRetry = { profileViewModel.loadProfile(context) },
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                ProfileContent(
                    uiState = uiState,
                    onLogoutClick = {
                        profileViewModel.logout(context)

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BottomBar(navController)
    }
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        DoctorHeaderCard(uiState = uiState)

        Spacer(modifier = Modifier.height(20.dp))

        ExpandableInfoBlock(
            uiState = uiState,
            expanded = expanded,
            onToggle = { expanded = !expanded }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ContactInfoBlock(uiState = uiState)

        Spacer(modifier = Modifier.height(30.dp))

        LogoutButton(
            onLogoutClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun LogoutButton(
    onLogoutClick: () -> Unit
) {
    Button(
        onClick = onLogoutClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(52.dp)
    ) {
        Text(
            text = "Tancar sessió",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ProfileErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
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
fun DoctorHeaderCard(uiState: ProfileUiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color(0xFFEAEAEA)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileRemoteImage(
                imageUrl = uiState.profileImage,
                size = 70
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = uiState.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Rol: ${uiState.roleText}",
                    color = Color.Gray
                )

                Text(
                    text = "Clínica: ${uiState.clinicName}",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProfileRemoteImage(
    imageUrl: String?,
    size: Int
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
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
fun ExpandableInfoBlock(
    uiState: ProfileUiState,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle() }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dades personals",
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = if (expanded) {
                            Icons.Outlined.KeyboardArrowUp
                        } else {
                            Icons.Outlined.KeyboardArrowDown
                        },
                        contentDescription = null
                    )
                }

                if (expanded) {
                    HorizontalDivider(color = Color.Gray.copy(.3f))

                    InfoRow(
                        label = "Edat",
                        value = uiState.ageText
                    )

                    HorizontalDivider(color = Color.Gray.copy(.3f))

                    InfoRow(
                        label = "Gènere",
                        value = uiState.genderText
                    )
                }
            }
        }
    }
}

@Composable
fun ContactInfoBlock(uiState: ProfileUiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column {
            ContactRow(
                label = "E-mail",
                value = uiState.emailText
            )

            HorizontalDivider(color = Color.Gray.copy(.3f))

            ContactRow(
                label = "Núm. de telèfon",
                value = uiState.phoneText
            )

            HorizontalDivider(color = Color.Gray.copy(.3f))

            ContactRow(
                label = "Ciutat",
                value = uiState.cityText
            )

            HorizontalDivider(color = Color.Gray.copy(.3f))

            ContactRow(
                label = "Direcció",
                value = uiState.addressText
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            modifier = Modifier.weight(1.5f),
            color = Color.Gray
        )
    }
}

@Composable
fun ContactRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            modifier = Modifier.weight(1.5f),
            color = Color.Gray
        )
    }
}
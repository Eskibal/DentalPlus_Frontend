package com.example.dentalplus_frontend.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.DocumentDto
import com.example.dentalplus_frontend.model.OdontogramType
import com.example.dentalplus_frontend.navigation.BottomBar
import com.example.dentalplus_frontend.navigation.Header
import com.example.dentalplus_frontend.ui.theme.BlueGrey40
import com.example.dentalplus_frontend.viewmodel.PatientDetailUiState
import com.example.dentalplus_frontend.viewmodel.PatientDetailViewModel

@Composable
fun PatientScreen(
    navController: NavController,
    patientId: Long,
    patientDetailViewModel: PatientDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by patientDetailViewModel.uiState.collectAsState()

    LaunchedEffect(patientId) {
        patientDetailViewModel.loadPatient(context, patientId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()

        when {
            uiState.isLoading -> {
                PatientLoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.errorMessage != null && uiState.patient == null -> {
                PatientErrorContent(
                    message = uiState.errorMessage ?: "S'ha produït un error",
                    onRetry = { patientDetailViewModel.loadPatient(context, patientId) },
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                PatientContent(
                    navController = navController,
                    uiState = uiState,
                    onUploadDocuments = { uris ->
                        patientDetailViewModel.uploadDocuments(
                            context = context,
                            patientId = patientId,
                            uris = uris
                        )
                    },
                    onDownloadDocument = { document ->
                        patientDetailViewModel.downloadDocument(
                            context = context,
                            document = document
                        )
                    },
                    onDeleteDocument = { documentId ->
                        patientDetailViewModel.deleteDocument(
                            context = context,
                            patientId = patientId,
                            documentId = documentId
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BottomBar(navController)
    }
}

@Composable
fun PatientContent(
    navController: NavController,
    uiState: PatientDetailUiState,
    onUploadDocuments: (List<Uri>) -> Unit,
    onDownloadDocument: (DocumentDto) -> Unit,
    onDeleteDocument: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSelectionDialog by remember { mutableStateOf(false) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                onUploadDocuments(uris)
            }
        }
    )

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        PatientHeaderCard(uiState = uiState)

        if (uiState.hasMedicalAlert) {
            Spacer(modifier = Modifier.height(16.dp))

            PatientMedicalAlertCard(
                text = uiState.medicalAlertText
            )
        }

        if (!uiState.errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }

        if (!uiState.successMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.successMessage ?: "",
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        InfoBlock(
            items = listOf(
                "ID pacient" to uiState.patientIdText,
                "Núm. de telèfon" to uiState.phoneText,
                "Domicili" to uiState.addressText,
                "E-mail" to uiState.emailText,
                "Població" to uiState.cityText,
                "Clínica" to uiState.clinicText
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoBlock(
            items = listOf(
                "Historial clínic" to uiState.notesText,
                "Al·lèrgies" to uiState.medicalAlertText,
                "Observacions" to uiState.personNotesText
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        BigActionButton(
            text = "Odontograma",
            onClick = { showSelectionDialog = true }
        )

        Spacer(modifier = Modifier.height(30.dp))

        PatientDocumentsBlock(
            documents = uiState.documents,
            isUploading = uiState.isUploadingDocument,
            isDeleting = uiState.isDeletingDocument,
            onUploadClick = {
                documentPickerLauncher.launch(arrayOf("application/pdf"))
            },
            onDownloadDocument = onDownloadDocument,
            onDeleteDocument = onDeleteDocument
        )

        Spacer(modifier = Modifier.height(20.dp))


    }

    if (showSelectionDialog) {
        SelectionDialog(
            onDismiss = { showSelectionDialog = false },
            onTypeSelected = { type ->
                showSelectionDialog = false
                navController.navigate("odontogram/${uiState.patient?.patientId}/${type.name}")
            }
        )
    }
}

@Composable
fun PatientMedicalAlertCard(
    text: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
        color = Color(0xFFFFF3CD),
        contentColor = Color(0xFFE65100)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Alerta mèdica",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PatientDocumentsBlock(
    documents: List<DocumentDto>,
    isUploading: Boolean,
    isDeleting: Boolean,
    onUploadClick: () -> Unit,
    onDownloadDocument: (DocumentDto) -> Unit,
    onDeleteDocument: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "Documents PDF",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onUploadClick,
                    enabled = !isUploading && !isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Pujant...")
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Afegir PDFs")
                    }
                }

                if (documents.isEmpty()) {
                    HorizontalDivider(color = Color.Gray.copy(0.3f))

                    Text(
                        text = "Aquest pacient encara no té documents.",
                        color = Color.Gray,
                        modifier = Modifier.padding(14.dp)
                    )
                } else {
                    documents.forEachIndexed { index, document ->
                        HorizontalDivider(color = Color.Gray.copy(0.3f))

                        PatientDocumentRow(
                            document = document,
                            isDeleting = isDeleting,
                            onDownloadDocument = onDownloadDocument,
                            onDeleteDocument = onDeleteDocument
                        )

                        if (index != documents.lastIndex) {
                            HorizontalDivider(color = Color.Gray.copy(0.15f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientDocumentRow(
    document: DocumentDto,
    isDeleting: Boolean,
    onDownloadDocument: (DocumentDto) -> Unit,
    onDeleteDocument: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = document.name ?: "Document sense nom",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = document.documentType ?: "OTHER",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextButton(
            onClick = { onDownloadDocument(document) },
            enabled = !document.url.isNullOrBlank() && !isDeleting
        ) {
            Text("Descarregar")
        }

        IconButton(
            onClick = {
                document.id?.let { id ->
                    onDeleteDocument(id)
                }
            },
            enabled = document.id != null && !isDeleting
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Eliminar document",
                tint = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
fun PatientLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PatientErrorContent(
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
fun PatientHeaderCard(uiState: PatientDetailUiState) {
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
            PatientDetailImageFromUrl(
                imageUrl = uiState.patient?.person?.profileImage,
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
                    text = "Edat: ${uiState.ageText} - Gènere: ${uiState.genderText}",
                    color = Color.Gray
                )

                Text(
                    text = "Data de naixement: ${uiState.birthDateText}",
                    color = Color.Gray
                )

                Text(
                    text = "ID ${uiState.patientIdText}",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PatientDetailImageFromUrl(
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
fun InfoBlock(
    title: String? = null,
    items: List<Pair<String, String>>
) {
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

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            thickness = DividerDefaults.Thickness,
                            color = Color.Gray.copy(0.4f)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = item.second.ifBlank { "No disponible" },
                            modifier = Modifier.weight(1.5f),
                            color = Color.Gray
                        )
                    }

                    if (index != items.lastIndex) {
                        HorizontalDivider(color = Color.Gray.copy(0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun BigActionButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Composable
fun SelectionDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (OdontogramType) -> Unit
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
                        text = "Selecciona una edat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            onTypeSelected(OdontogramType.CHILD)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40,
                        contentColor = Color.Unspecified,
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
                        },
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40,
                        contentColor = Color.Unspecified,
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
                        },
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        color = BlueGrey40,
                        contentColor = Color.Unspecified,
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
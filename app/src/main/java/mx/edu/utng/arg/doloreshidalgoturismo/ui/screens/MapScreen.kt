package mx.edu.utng.arg.doloreshidalgoturismo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import mx.edu.utng.arg.doloreshidalgoturismo.ui.viewmodel.MapViewModel
import mx.edu.utng.arg.doloreshidalgoturismo.ui.components.PlacesList
import mx.edu.utng.arg.doloreshidalgoturismo.ui.components.PlaceDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel
) {
    val places by viewModel.places.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()

    val doloreshidalgoCenter = LatLng(21.1560, -100.9318)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(doloreshidalgoCenter, 14f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turismo Dolores Hidalgo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.showAddDialog(cameraPositionState.position.target)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar lugar")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mapa de Google
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                    compassEnabled = true
                ),
                onMapLongClick = { latLng ->
                    viewModel.showAddDialog(latLng)
                }
            ) {
                // Dibujar marcadores
                places.forEach { place ->
                    val position = LatLng(place.latitude, place.longitude)
                    Marker(
                        state = MarkerState(position = position),
                        title = place.name,
                        snippet = place.description,
                        onInfoWindowClick = {
                            viewModel.showEditDialog(place)
                        }
                    )
                }
            }

            // Lista de lugares
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                PlacesList(
                    places = places,
                    onPlaceClick = { place ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(place.latitude, place.longitude),
                            16f
                        )
                    },
                    onEditClick = { place ->
                        viewModel.showEditDialog(place)
                    },
                    onDeleteClick = { place ->
                        viewModel.deletePlace(place)
                    },
                    onFavoriteClick = { place ->
                        viewModel.toggleFavorite(place)
                    }
                )
            }
        }

        // Diálogo para agregar/editar
        if (showDialog) {
            PlaceDialog(
                place = selectedPlace,
                onDismiss = { viewModel.dismissDialog() },
                onSave = { name, description, latLng, category, color ->
                    if (selectedPlace != null) {
                        viewModel.updatePlace(
                            selectedPlace!!.copy(
                                name = name,
                                description = description,
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                category = category,
                                markerColor = color
                            )
                        )
                    } else {
                        viewModel.addPlace(name, description, latLng, category, color)
                    }
                }
            )
        }
    }
}
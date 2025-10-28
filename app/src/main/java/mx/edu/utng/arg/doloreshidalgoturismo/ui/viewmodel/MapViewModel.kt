package mx.edu.utng.arg.doloreshidalgoturismo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.arg.doloreshidalgoturismo.data.model.PlaceEntity
import mx.edu.utng.arg.doloreshidalgoturismo.data.repository.PlaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    val places: StateFlow<List<PlaceEntity>> = repository.allPlaces
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPlace = MutableStateFlow<PlaceEntity?>(null)
    val selectedPlace: StateFlow<PlaceEntity?> = _selectedPlace.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _mapCenter = MutableStateFlow(LatLng(21.1560, -100.9318))
    val mapCenter: StateFlow<LatLng> = _mapCenter.asStateFlow()

    init {
        viewModelScope.launch {
            places.first().let { placesList ->
                if (placesList.isEmpty()) {
                    repository.insertDefaultPlaces()
                }
            }
        }
    }

    fun addPlace(
        name: String,
        description: String,
        latLng: LatLng,
        category: String,
        markerColor: String
    ) {
        viewModelScope.launch {
            val newPlace = PlaceEntity(
                name = name,
                description = description,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                category = category,
                markerColor = markerColor
            )
            repository.insertPlace(newPlace)
            _showDialog.value = false
        }
    }

    fun updatePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.updatePlace(place)
            _selectedPlace.value = null
            _showDialog.value = false
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.deletePlace(place)
        }
    }

    fun toggleFavorite(place: PlaceEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(place.id, place.isFavorite)
        }
    }

    fun showAddDialog(latLng: LatLng) {
        _mapCenter.value = latLng
        _selectedPlace.value = null
        _showDialog.value = true
    }

    fun showEditDialog(place: PlaceEntity) {
        _selectedPlace.value = place
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
        _selectedPlace.value = null
    }
}
package mx.edu.utng.arg.doloreshidalgoturismo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utng.arg.doloreshidalgoturismo.data.database.AppDatabase
import mx.edu.utng.arg.doloreshidalgoturismo.data.repository.PlaceRepository
import mx.edu.utng.arg.doloreshidalgoturismo.ui.screens.MapScreen
import mx.edu.utng.arg.doloreshidalgoturismo.ui.viewmodel.MapViewModel
import mx.edu.utng.arg.doloreshidalgoturismo.ui.viewmodel.MapViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PlaceRepository(database.placeDao())

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MapViewModel = viewModel(
                        factory = MapViewModelFactory(repository)
                    )
                    MapScreen(viewModel = viewModel)
                }
            }
        }
    }
}
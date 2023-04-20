package com.example.mymapsapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mymapsapplication.ui.theme.MyMapsApplicationTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMapsApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GmapsScreen()
                }
            }
        }
    }
}

data class Pin(val location: LatLng) {
    constructor(latitude: Double, longitude: Double): this(LatLng(latitude, longitude))

    companion object {
        val pins = listOf(
            Pin(54.598, -4.321),
            Pin(54.593, -2.269),
            Pin(53.398, -2.354),
            Pin(53.470, -2.349),
            Pin(53.552, -2.128),
            Pin(53.424, -2.599),
            Pin(53.661, -2.629),
            Pin(53.772, -1.581),
            Pin(53.445, -2.735),
            Pin(53.068, -2.265),
            Pin(53.688, -1.630),
            Pin(53.412, -3.072),
            Pin(53.357, -3.201),
            Pin(53.593, -2.269),
            Pin(53.470, -2.269),
            Pin(53.552, -2.354),
            Pin(53.772, -2.354),
            Pin(53.661, -2.354),
            Pin(53.552, -2.599),
        )
    }
}

class ClusterPin(val pin: Pin): ClusterItem {
    override fun getPosition(): LatLng = pin.location
    override fun getTitle(): String = "${pin.location}"
    override fun getSnippet(): String = ""
}

data class State(
    val pins: List<Pin>,
    val selectedPin: Pin?,
)

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun GmapsScreen() {
    val scope = rememberCoroutineScope()

    val state = remember {
        mutableStateOf(
            State(
                pins = Pin.pins,
                selectedPin = null,
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(54.598, -4.321), 6f)
    }

    //setup map ui settings
    val mapUiSettings by remember { mutableStateOf(
        MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = true,
            zoomControlsEnabled = false,
            tiltGesturesEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
        )
    ) }

    //setup map properties
    val mapProperties by remember { mutableStateOf(
        MapProperties(
            isMyLocationEnabled = false,
            isTrafficEnabled = true,
        )
    ) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties,
        onMapClick = { state.value = state.value.copy(selectedPin = null, pins = Pin.pins) },
    ) {
        Clustering(
            items = state.value.pins.map { ClusterPin(it) },
            onClusterItemClick = {
                state.value = state.value.copy(selectedPin = it.pin)
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLng(
                            it.pin.location,
                        ),
                        400,
                    )
                }
                true
            },
            onClusterClick = {
                true
            },
            clusterContent = { cluster ->
                Surface(
                    modifier = Modifier.size(45.dp),
                    shape = CircleShape,
                    color = Color.Black,
                    contentColor = Color.White,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "%,d".format(cluster.size),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            clusterItemContent = {
                if (it.pin == state.value.selectedPin) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_pin_circle_24),
                        contentDescription = "",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        tint = Color(0xffff0000)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_pin_drop_24),
                        contentDescription = "",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        tint = Color(0xff0000ff)
                    )
                }
            }
        )
    }
}

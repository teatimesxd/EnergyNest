package com.example.energynest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

data class AddressResult(
    val street: String,
    val zipcode: String,
    val city: String,
    val state: String
)

@Composable
fun MapPicker(
    onAddressSelected: (AddressResult) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var addressResult by remember { mutableStateOf<AddressResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // We'll try to get current location and move the camera there
        }
    }

    // Camera state – start with a default location (e.g., Kuala Lumpur)
    val klcc = LatLng(3.1571, 101.7115)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(klcc, 12f)
    }

    // Try to get current location if permission granted
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // You could use FusedLocationProviderClient to get current location and move camera
            // For simplicity, we keep the default camera.
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // The map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            ),
            properties = MapProperties(
                isMyLocationEnabled = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ),
            onMapClick = { latLng ->
                selectedLatLng = latLng
                isLoading = true
                // Reverse geocode
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(
                        latLng.latitude, latLng.longitude, 1
                    )
                    isLoading = false
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        addressResult = AddressResult(
                            street = addr.getAddressLine(0) ?: "",
                            zipcode = addr.postalCode ?: "",
                            city = addr.locality ?: "",
                            state = addr.adminArea ?: ""
                        )
                    } else {
                        errorMessage = "No address found for this location."
                    }
                } catch (e: Exception) {
                    isLoading = false
                    errorMessage = "Geocoding error: ${e.message}"
                }
            }
        ) {
            // Show a marker at the selected location
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected location"
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom buttons: Confirm or Dismiss
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (addressResult != null) {
                Button(
                    onClick = {
                        onAddressSelected(addressResult!!)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Address")
                }
            } else {
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
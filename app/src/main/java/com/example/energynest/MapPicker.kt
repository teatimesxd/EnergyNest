@file:Suppress("DEPRECATION")

package com.example.energynest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.core.content.ContextCompat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.json.JSONArray
import org.json.JSONObject

import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


// =====================================================
// ADDRESS RESULT
// =====================================================

data class AddressResult(
    val street: String,
    val zipcode: String,
    val city: String,
    val state: String
)


// =====================================================
// LOCATION SEARCH RESULT
// =====================================================

data class LocationSearchResult(
    val latitude: Double,
    val longitude: Double,
    val displayName: String,
    val address: AddressResult
)

@Composable
fun MapPicker(onAddressSelected: (AddressResult) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var selectedAddress by remember { mutableStateOf<AddressResult?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var message by remember { mutableStateOf("") }

    // Move the map to current location
    fun moveToCurrentLocation() { message = "Getting your current location..."
        requestCurrentLocation(context) { location ->

            if (location == null) {
                message = "Unable to get your location. Please turn on GPS."
                return@requestCurrentLocation
            }

            val userLatLng = LatLng(
                location.latitude,
                location.longitude
            )

            selectedLatLng = userLatLng
            mapLibreMap?.cameraPosition = CameraPosition.Builder()
                .target(userLatLng)
                .zoom(17.0)
                .build()

            // Remove previous marker
            selectedMarker?.let { mapLibreMap?.removeMarker(it) }

            // Add marker
            selectedMarker =
                mapLibreMap?.addMarker(MarkerOptions()
                    .position(userLatLng)
                    .title("Your Current Location")
                )

            message = "Getting current address..."

            // Reverse geocode
            coroutineScope.launch {
                val result = reverseGeocodeLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                if (result != null) {
                    selectedAddress = result.address
                    // Show location in search bar
                    searchText = result.displayName
                    message = "Current location selected"
                } else {
                    selectedAddress = null
                    message = "Current location selected, but address was not found"
                }
            }
        }
    }

    // Location Permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts
        .RequestMultiplePermissions()) { permissions ->

        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            // Permission granted
            // Get the real location immediately
            moveToCurrentLocation()
        } else {
            message = "Location permission is required."
        }
    }


    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Map
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds(),

                factory = { mapContext -> MapLibre.getInstance(mapContext)
                    // UI to appear above the map

                    val options = MapLibreMapOptions
                        .createFromAttributes(mapContext)
                        .textureMode(true)

                    val mapView = MapView(mapContext, options)

                    mapView.onCreate(null)
                    mapView.getMapAsync { map -> mapLibreMap = map

                        // Map Style
                        map.setStyle("https://tiles.openfreemap.org/styles/liberty")

                        // Default Location
                        val defaultLocation = LatLng(3.1390, 101.6869)

                        map.cameraPosition = CameraPosition.Builder()
                            .target(defaultLocation)
                            .zoom(12.0)
                            .build()

                        // User taps map
                        map.addOnMapClickListener { point ->
                            selectedLatLng = point

                            // Remove previous marker
                            selectedMarker?.let { map.removeMarker(it) }

                            // Add new marker
                            selectedMarker = map.addMarker(
                                MarkerOptions()
                                    .position(point)
                                    .title(
                                        "Selected Location"
                                    )
                            )

                            message = "Getting location..."

                            // Reverse geocode selected point
                            coroutineScope.launch {
                                val result = reverseGeocodeLocation(
                                    latitude = point.latitude,
                                    longitude = point.longitude
                                )

                                if (result != null) {
                                    selectedAddress = result.address
                                    searchText = result.displayName
                                    message = "Location selected"
                                } else {
                                    selectedAddress = null
                                    message = "Unable to get address"
                                }
                            }
                            true
                        }
                    }
                    mapView
                }
            )

            // Top Controls
            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.White.copy(alpha = 0.95f))
            ) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Your Location",
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search location") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Search Button
                    Button(
                        onClick = {
                            if (searchText.isBlank()) {
                                message = "Please enter a location"
                            } else {
                                coroutineScope.launch {
                                    message = "Searching..."

                                    val result = searchLocation(searchText)

                                    if (
                                        result != null
                                    ) {
                                        val location = LatLng(result.latitude, result.longitude)

                                        // Move map
                                        mapLibreMap?.cameraPosition = CameraPosition
                                            .Builder()
                                            .target(location)
                                            .zoom(16.0)
                                            .build()

                                        selectedLatLng = location
                                        selectedAddress = result.address

                                        // Remove old marker
                                        selectedMarker?.let { mapLibreMap?.removeMarker(it) }

                                        // Add new marker
                                        selectedMarker = mapLibreMap?.addMarker(MarkerOptions().position(location).title("Selected Location"))

                                        searchText = result.displayName
                                        message = "Location selected"
                                    } else {
                                        message = "Location not found"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Search")
                    }
                }

                // Use Current Location Button
                Button(
                    onClick = {
                        val fineGranted = ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        val coarseGranted = ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        if (!fineGranted && !coarseGranted
                        ) {

                            // Ask user for permission
                            locationPermissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            )
                        } else {
                            // Permission already granted
                            moveToCurrentLocation()
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        )
                        .height(
                            48.dp
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Use My Current Location")
                }
            }

            // Confirm Button
            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 80.dp)
                .background(Color.White.copy(alpha = 0.95f))
                .padding(8.dp)
            ) {
                if (message.isNotBlank()
                ) {
                    Text(text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        color = Color.DarkGray
                    )
                }

                // Confirm Location Button
                Button(
                    onClick = {
                        val address = selectedAddress

                        if (address != null
                        ) {
                            // Send selected address back to Register page
                            onAddressSelected(address)
                            onDismiss()
                        } else {
                            message = "Please select a valid location first."
                        }
                    },

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Confirm Location")
                }
            }
        }
    }
}

// Get Current Location
fun requestCurrentLocation(
    context: Context,
    onLocationReceived: (Location?) -> Unit
) {

    val fineLocationGranted = ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    val coarseLocationGranted = ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Permission not granted
    if (!fineLocationGranted && !coarseLocationGranted
    ) {
        onLocationReceived(null)
        return
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    try {
        val provider = when {
            fineLocationGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        }

        // No provider available
        if (provider == null
        ) { onLocationReceived(null)
            return
        }

        // Actively request location
        locationManager.requestSingleUpdate(provider, object : LocationListener {

            override fun onLocationChanged(location: Location) {
                onLocationReceived(location)
            }
        },

            Looper.getMainLooper()
        )
    } catch (
        e: SecurityException
    ) {
        onLocationReceived(null
        )
    }
}


// Search Location
suspend fun searchLocation(query: String): LocationSearchResult? = withContext(Dispatchers.IO) {

    try {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = URL("https://nominatim.openstreetmap.org/search" +
                "?format=jsonv2" +
                "&addressdetails=1" +
                "&limit=1" +
                "&q=$encodedQuery"
        )

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        connection.setRequestProperty("User-Agent", "EnergyNestStudentApp/1.0")

        val response = connection
            .inputStream
            .bufferedReader()
            .use { it.readText() }

        val results = JSONArray(response)

        if (results.length() == 0
        ) { return@withContext null }


        val firstResult = results.getJSONObject(0)
        val latitude = firstResult.optDouble("lat")
        val longitude = firstResult.optDouble("lon")
        val displayName = firstResult.optString("display_name")
        val addressJson = firstResult.optJSONObject("address")
        val address = parseAddress(addressJson)

        LocationSearchResult(latitude = latitude, longitude = longitude, displayName = displayName, address = address)
    } catch (e: Exception) { null }
}


// Reverse Geocode
suspend fun reverseGeocodeLocation(latitude: Double, longitude: Double): LocationSearchResult? =
    withContext(
        Dispatchers.IO
    ) {

        try { val url = URL("https://nominatim.openstreetmap.org/reverse" +
                "?format=jsonv2" +
                "&addressdetails=1" +
                "&lat=$latitude" +
                "&lon=$longitude"
        )

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            connection.setRequestProperty("User-Agent", "EnergyNestStudentApp/1.0")

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val resultJson = JSONObject(response)
            val displayName = resultJson.optString("display_name")
            val addressJson = resultJson.optJSONObject("address")
            val address = parseAddress(addressJson)

            LocationSearchResult(latitude = latitude, longitude = longitude, displayName = displayName, address = address)
        } catch (e: Exception) {
            null
        }
    }


// Parse Address
fun parseAddress(addressJson: JSONObject?): AddressResult {

    val road = addressJson?.optString("road") ?: ""
    val houseNumber = addressJson?.optString("house_number") ?: ""
    val fullStreet = listOf(houseNumber, road).filter {
        it.isNotBlank()
    }.joinToString(" ")

    val postcode = addressJson?.optString("postcode") ?: ""
    val city = when {
        !addressJson?.optString("city").isNullOrBlank() ->
            addressJson?.optString("city") ?: ""

        !addressJson?.optString("town").isNullOrBlank() ->
            addressJson?.optString("town") ?: ""

        !addressJson?.optString("village").isNullOrBlank() ->
            addressJson?.optString("village") ?: ""

        else -> ""
    }
    val state = addressJson?.optString("state") ?: ""

    return AddressResult(
        street = fullStreet,
        zipcode = postcode,
        city = city,
        state = state
    )
}
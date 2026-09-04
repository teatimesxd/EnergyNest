package com.example.energynest.shared_ui

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView


@Composable
fun OpenStreetMapScreen() {

    AndroidView(
        modifier = Modifier.fillMaxSize(),

        factory = { context ->
            // Initialize MapLibre
            MapLibre.getInstance(context)
            // Create map
            val mapView = MapView(context)
            // Required lifecycle initialization
            mapView.onCreate(Bundle())
            // Load the map
            mapView.getMapAsync { map ->
                // OpenFreeMap style
                map.setStyle(
                    "https://tiles.openfreemap.org/styles/liberty"
                )
                // Start at Kuala Lumpur
                map.cameraPosition =
                    CameraPosition.Builder()
                        .target(
                            LatLng(
                                3.1390,
                                101.6869
                            )
                        )
                        .zoom(13.0)
                        .build()
            }
            mapView
        }
    )
}
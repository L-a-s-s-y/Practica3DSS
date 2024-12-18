package com.example.practica3

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import androidx.preference.PreferenceManager

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private val almacenes = listOf(
        GeoPoint(37.197883, -3.624462), // ETSIIT Granada
        GeoPoint(37.164199, -3.609143), // Calle Néctar
        GeoPoint(37.156979, -3.606891)  // Estadio Los Cármenes
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración global de OSMDroid antes de setContentView
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = "com.example.practica3"

        setContentView(R.layout.activity_map)

        // Configura el MapView
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK) // Tiles de OpenStreetMap
        mapView.setMultiTouchControls(true)

        // Verifica permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            initializeMap()
        }
    }

    private fun initializeMap() {
        // Añade los marcadores de almacenes
        addStoreMarkers()

        // Ajusta el zoom y el área visible para incluir todos los almacenes
        adjustZoomToStores()
    }

    private fun addStoreMarkers() {
        almacenes.forEach { location ->
            val marker = Marker(mapView)
            marker.position = location
            marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_store)
            marker.title = "Almacén"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
    }

    private fun adjustZoomToStores() {
        // Calcula un BoundingBox que incluya todos los almacenes
        val boundingBox = BoundingBox.fromGeoPointsSafe(almacenes)

        // Ajusta el mapa al BoundingBox
        mapView.zoomToBoundingBox(boundingBox, false) // false: No animado
        mapView.invalidate() // Refresca el mapa
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeMap()
        }
    }
}
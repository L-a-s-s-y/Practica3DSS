package com.example.practica3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import androidx.preference.PreferenceManager
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var buttonProducts: Button
    //private lateinit var buttonCentrar: Button
    //private lateinit var buttonAlmacenes: Button

    private val almacenes = listOf(
        GeoPoint(37.187883, -3.624462),
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

        mapView.setMultiTouchControls(true)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        initializeMap()

        buttonProducts = findViewById(R.id.buttonIrProdcuts)
        //buttonCentrar = findViewById(R.id.buttonUsuario)
        //buttonAlmacenes = findViewById(R.id.buttonAlmacenes)

        buttonProducts.setOnClickListener {
            funcTest()
        }
    }

    private fun funcTest(){
        Log.d("MAP_ACTIVITY", "Antes del finish")
        finish()
        Log.d("MAP_ACTIVITY", "Después del finish")
    }

    private fun initializeMap() {
        // Configura las propiedades básicas del mapa


        // Configura el controlador del mapa con las coordenadas iniciales
        val startPoint = GeoPoint(37.19715325305848, -3.6244863308941815)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)
        val markerU = Marker(mapView)
        markerU.position = startPoint
        markerU.icon = resources.getDrawable(R.drawable.stickman, null)
        mapView.overlays.add(markerU)

        // Añade los marcadores de almacenes
        addStoreMarkers()

        // Refresca el mapa
        //mapView.invalidate()
    }

    private fun addStoreMarkers() {
        almacenes.forEach { location ->
            val marker = Marker(mapView)
            marker.position = location
            marker.title = "Almacén"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
    }
}
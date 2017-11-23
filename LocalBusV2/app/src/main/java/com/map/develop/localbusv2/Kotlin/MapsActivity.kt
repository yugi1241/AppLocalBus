package com.map.develop.localbusv2.Kotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.map.develop.localbusv2.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var marcador: Marker? = null
    internal var lat = 0.0
    internal var lng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        myPoss()
    }

    private fun addMarker(lat: Double, lng: Double) {
        val coordenadas = LatLng(lat, lng)
        val miUbicaion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16f)

        if (marcador != null) {
            marcador!!.remove()
        }
        marcador = mMap!!.addMarker(MarkerOptions()
                .position(coordenadas)
                .title("Mi Ubicacion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
        mMap!!.animateCamera(miUbicaion)
    }

    private fun updatePoss(location: Location?) {
        if (location != null) {
            lat = location.latitude
            lng = location.longitude
            addMarker(lat, lng)
            internal var locationListener:
        }
    }
LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updatePoss(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }
    }

    private fun myPoss() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        updatePoss(location)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0f, locationListener)
    }
}

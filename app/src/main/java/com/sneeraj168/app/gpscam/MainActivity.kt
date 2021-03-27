package com.sneeraj168.app.gpscam

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

lateinit var fusedLocationProviderClient: FusedLocationProviderClient

class MainActivity : AppCompatActivity() {

    private var mImageView: ImageView? = null
    private var mLocationTextView: TextView? = null
    private var mLocationTextViewName: TextView? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var locationCallback:LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mImageView = findViewById(R.id.iv_image)
        mLocationTextView = findViewById(R.id.tv_location)
        findViewById<Button>(R.id.btnFetchLocation).setOnClickListener {
            fetchLocation()
            getsystemTime()
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()
        getsystemTime()

    }

    fun locName(location: Location) {
        var geocoder: Geocoder?
        var address: List<Address>? = null

        geocoder = Geocoder(this, Locale.getDefault())
        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            mLocationTextViewName!!.text = address.get(0).getAddressLine(0)
        } catch (e: Exception) {
        }

    }

    private fun getCityName(lat: Double, long: Double): String {
        var AddressName: String = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 1)
        AddressName = Adress.get(0).getAddressLine(0)
        return AddressName
    }

    private fun getsystemTime() {
        val textView: TextView = findViewById(R.id.timeText)
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        textView.text = currentDateAndTime
    }

    fun takeAPicture(view: View?) {
        Log.d("TAKE", "takeAPicture")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras!!["data"] as Bitmap?
            mImageView!!.setImageBitmap(bitmap)
            fetchLocation()
            getsystemTime()
        }
    }

    private fun fetchLocation() {
        // Task<Location?>
        var task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
               // locName(location = )
                mLocationTextView!!.text = "LATITUDE : ${it.latitude}  LONGITUDE:${it.longitude}" + "\n Address: " + getCityName(it.latitude, it.longitude)
                Toast.makeText(
                    applicationContext,
                    "${it.latitude} ${it.longitude} ${it.accuracy} ${it.altitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}
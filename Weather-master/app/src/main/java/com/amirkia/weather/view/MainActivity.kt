package com.amirkia.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amirkia.weather.model.DatabaseHelper
import com.bumptech.glide.Glide
import com.amirkia.weather.R
import com.amirkia.weather.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

//To log messages to the console
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel

    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    private val REQUEST_LOCATION_PERMISSION = 1

    val databaseHelper = DatabaseHelper(this)

    //=============================================================== Functions

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize GET and SET with shared preferences
        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        // get a reference to MainViewModel
        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        //Initialize text edit with Shanghai and get data from API
        var cName = GET.getString("cityName", "Shanghai")?.toLowerCase()
        edt_city_name.setText(cName)
        viewmodel.refreshData(cName!!)

        //Get data from DB
        getDataFromDB()

        // get values for calendar
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        updated_at.setText(dateTime.format(formatter))

        getLiveData()

        //---------------------------------------------------------------------
        swipe_refresh_layout.setOnRefreshListener {
            ll_data.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            var cityName = GET.getString("cityName", cName)?.toLowerCase()
            edt_city_name.setText(cityName)
            viewmodel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }
        //---------------------------------------------------------------------
        img_search_city.setOnClickListener {
            val cityName = edt_city_name.text.toString()
            val searchDate = "$year-$month-$day"

            SET.putString("cityName", cityName)
            SET.apply()
            viewmodel.refreshData(cityName)

            getLiveData()
            databaseHelper.insertCity(cityName, searchDate)
            getDataFromDB()

            Log.i(TAG, "onCreate: $cityName")
        }
        //---------------------------------------------------------------------
        clear_history.setOnClickListener{
            databaseHelper.clearTable()
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
            getDataFromDB()
        }
        //---------------------------------------------------------------------
        maps_icon.setOnClickListener {
            viewmodel.weather_data.observe(this) { weatherModel ->

                val latitude = weatherModel.coord.lat
                val longitude = weatherModel.coord.lon

                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "No maps app is installed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //---------------------------------------------------------------------
        location_button.setOnClickListener {
            // Check for the permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "This is a debug message")

                // Get the user's current location
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                // Update the cityName variable
                var cityName = ""
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses.isNotEmpty()) {
                        print(cityName)
                        cityName = addresses[0].locality.toLowerCase()
                    }
                }

                // Update the UI
                ll_data.visibility = View.GONE
                tv_error.visibility = View.GONE
                pb_loading.visibility = View.GONE

                edt_city_name.setText(cityName)
                viewmodel.refreshData(cityName)
                swipe_refresh_layout.isRefreshing = false
            }
            else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            }
        }


    }

    //===============================================================
    private fun getLiveData() {
        // show data
        viewmodel.weather_data.observe(this, Observer { data ->
            data?.let {
                ll_data.visibility = View.VISIBLE

                tv_city_code.text = data.sys.country
                tv_city_name.text = data.name
                address.text = data.name

                //The Glide library is used to load the weather icon image from the URL
                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(img_weather_pictures)

                var tempInt = data.main.temp.toInt();
                tv_degree.text = tempInt.toString() + "°C"

                tv_humidity.text = data.main.humidity.toString() + "%"
                tv_wind_speed.text = data.wind.speed.toString()
                tv_lat.text = data.coord.lat.toString()
                tv_lon.text = data.coord.lon.toString()
            }
        })
        // show error
        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    tv_error.visibility = View.VISIBLE
                    pb_loading.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    tv_error.visibility = View.GONE
                }
            }
        })
        // show progress bar
        viewmodel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    ll_data.visibility = View.GONE
                } else {
                    pb_loading.visibility = View.GONE
                }
            }
        })
    }

   // ===============================================================

    private fun getDataFromDB() {
        // Retrieve city names and dates from the database
        val cityNames = databaseHelper.getCityNames()
        val cityNamesList = ArrayList<Pair<String, String>>()

        cityNamesList.addAll(cityNames)

        // Create a CityAdapter with the city names list
        val adapter = CityAdapter(this, cityNamesList)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.edt_city_name)
        autoCompleteTextView.setAdapter(adapter)

        // Set the threshold to 0 to show the dropdown for any input
        autoCompleteTextView.threshold = 0

        // Enable the dropdown manually when the AutoCompleteTextView is clicked
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }
    }



}
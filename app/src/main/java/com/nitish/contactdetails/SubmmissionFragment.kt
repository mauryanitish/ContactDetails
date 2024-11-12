package com.nitish.contactdetails

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.nitish.contactdetails.databinding.ActivityResponseBinding
import com.nitish.contactdetails.databinding.FragmentSubmmissionBinding
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmmissionFragment : Fragment() {
    private lateinit var binding: FragmentSubmmissionBinding
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""
    private lateinit var locationManager:LocationManager
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSubmmissionBinding.inflate(inflater, container, false)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.button.setOnClickListener {
           captureGeolocationAndSubmit()
        }

        return binding.root
    }
    private fun saveAnswersAndShowResult() {
        AudioRecorderManager.stopRecording()

        Log.d("Audio File", "Recording saved at: " + AudioRecorderManager.getAudioFilePath())

        // Sample data for answers
        val answers = mapOf(
            "Q1" to arguments?.getString("gender"),
            "Q2" to arguments?.getString("age"),
//            "Q3" to "xyz.png",
            "recording" to AudioRecorderManager.getAudioFilePath(),
            "gps" to "$longitude,$latitude",
            "submit_time" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
        )

        // Convert answers to JSON format and save to file
        val json = JSONObject(answers).toString()
        saveToFile(requireContext(), "answers.json", json)

        // Start ResponseActivity and pass data
        Intent(requireContext(), ResponseActivity::class.java).apply {
            putExtra("answers", json) // Pass JSON string to ResponseActivity
            startActivity(this)
        }
    }

    private fun saveToFile(context: Context, fileName: String, data: String) {
        // Write JSON data to a file in the app's internal storage
        val file = File(context.filesDir, fileName)
        file.writeText(data)
        Toast.makeText(context, "Answers saved to $fileName", Toast.LENGTH_SHORT).show()
    }

    private fun captureGeolocationAndSubmit() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1002)
            return
        }

        // Request a single location update to capture the latitude and longitude
        locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
                Log.d("Geolocation", "Latitude: $latitude, Longitude: $longitude")
                saveAnswersAndShowResult() // Now that we have the location, save and submit the answers
            }

            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
        }, null)
    }

}
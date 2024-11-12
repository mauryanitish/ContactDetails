package com.nitish.contactdetails

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nitish.contactdetails.databinding.ActivityResponseBinding
import org.json.JSONObject

class ResponseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResponseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonData = intent.getStringExtra("answers")

        if (jsonData != null) {
            val jsonObject = JSONObject(jsonData)

            // Use getString() for all fields to handle any data type inconsistencies
            binding.textViewQ1.text = jsonObject.getString("Q1")
            binding.textViewQ2.text = jsonObject.getString("Q2")
//            binding.textViewQ3.text = jsonObject.getString("Q3")
            binding.textViewRecording.text = jsonObject.getString("recording")
            binding.textViewGPS.text = jsonObject.getString("gps")
            binding.textViewSubmitTime.text = jsonObject.getString("submit_time")
        }

    }
}
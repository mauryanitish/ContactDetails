package com.nitish.contactdetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nitish.contactdetails.databinding.FragmentGenderBinding

class GenderFragment : Fragment() {
    private lateinit var binding: FragmentGenderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGenderBinding.inflate(inflater,container,false)
        binding.spinGender.selectedItem.let{
            Log.i("FocusChange","Start focus.............")
            AudioRecorderManager.startRecording(requireContext())
            Log.i("FocusChange", "SpinGender gained focus.")
        }

        binding.btnNext.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", binding.spinGender.selectedItem.toString()) // Pass the gender data as an example
            }
            findNavController().navigate(R.id.action_genderFragment_to_ageFragment,bundle)
        }
        return binding.root
    }


}
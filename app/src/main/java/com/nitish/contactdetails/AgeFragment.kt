package com.nitish.contactdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nitish.contactdetails.databinding.FragmentAgeBinding

class AgeFragment : Fragment() {
    private lateinit var binding: FragmentAgeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgeBinding.inflate(inflater, container, false)
        binding.btnPrev.setOnClickListener {
            findNavController().navigate(R.id.action_ageFragment_to_genderFragment)
        }
        binding.btnNext.setOnClickListener {
            val bundle = Bundle().apply {
                putString("age",binding.etAge.text.toString())
                putString("gender",arguments?.getString("gender"))
            }
            findNavController().navigate(R.id.action_ageFragment_to_submmissionFragment,bundle)
        }
        return binding.root
    }
}
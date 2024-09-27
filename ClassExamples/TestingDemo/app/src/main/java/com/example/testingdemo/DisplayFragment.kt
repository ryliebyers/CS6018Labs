package com.example.testingdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.testingdemo.databinding.FragmentDisplayBinding
import com.example.testingdemo.databinding.FragmentEntryBinding


class DisplayFragment : Fragment() {
   private val viewModel: EmailViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDisplayBinding.inflate(layoutInflater, container, false)
        viewModel.firstName.observe(viewLifecycleOwner){
            binding.username.text = it
        }
        viewModel.lastName.observe(viewLifecycleOwner){
            binding.domain.text = it
        }

       binding.backButton.setOnClickListener{
           findNavController().navigate(R.id.action_displayFragment_to_entryFragment)
       }

        binding.newFragment.setOnClickListener {
            findNavController().navigate(R.id.action_displayFragment_to_blankFragment2)
        }

        return binding.root
    }
}
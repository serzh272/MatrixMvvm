package ru.serzh272.matrixmvvm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.databinding.FragmentPlotBinding

class PlotFragment : Fragment() {
    private val args by navArgs<PlotFragmentArgs>()
    private lateinit var binding:FragmentPlotBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlotBinding.inflate(layoutInflater)
        binding.gershgorinPlot.drawCircles(args.matrix)
        binding.gershgorinPlot.setOnClickListener{
            findNavController().navigate(R.id.mainFragment)
        }
        return binding.root
    }
}
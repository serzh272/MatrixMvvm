package ru.serzh272.matrixmvvm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.databinding.FragmentPreferencesBinding

class PreferencesFragment : Fragment() {
    lateinit var binding:FragmentPreferencesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreferencesBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.switchMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                if (binding.switchTypeFraction.isChecked){
                    binding.fractionPreview.mode = Fraction.FractionType.MIXED
                }else{
                    binding.fractionPreview.mode = Fraction.FractionType.COMMON
                }
            }else{
                binding.fractionPreview.mode = Fraction.FractionType.DECIMAL
                binding.tiEtPrecision.setText("${binding.fractionPreview.precision}")
            }
            binding.switchTypeFraction.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.tiLayout.visibility = if (isChecked)  View.GONE else View.VISIBLE
            binding.fractionPreview.invalidate()
        }
        binding.switchTypeFraction.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                binding.fractionPreview.mode = Fraction.FractionType.MIXED
            }else{
                binding.fractionPreview.mode = Fraction.FractionType.COMMON
            }
            binding.fractionPreview.invalidate()
        }
        binding.tiEtPrecision.addTextChangedListener {
            var pr = it.toString().toIntOrNull()
            if (pr != null){
                if (pr <= 4) {
                    binding.fractionPreview.precision = pr
                    binding.tiEtPrecision.error = null
                }else{
                    binding.tiEtPrecision.error = "Введите число от 0 до 4"
                }
            }else{
                binding.tiEtPrecision.error = "Введите число от 0 до 4"
            }
            binding.fractionPreview.invalidate()
        }
        return binding.root


    }

}
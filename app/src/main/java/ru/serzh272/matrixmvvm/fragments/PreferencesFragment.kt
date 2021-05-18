package ru.serzh272.matrixmvvm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.data.AppSettings
import ru.serzh272.matrixmvvm.databinding.FragmentPreferencesBinding
import ru.serzh272.matrixmvvm.repositories.PreferencesRepository
import ru.serzh272.matrixmvvm.viewmodels.PreferencesViewModel

class PreferencesFragment : Fragment() {
    lateinit var binding: FragmentPreferencesBinding
    lateinit var prefsViewModel: PreferencesViewModel
    lateinit var prefs: AppSettings
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreferencesBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        initViewModel()
        prefs = prefsViewModel.prefsRepo.getSettings()
        binding.switchMode.setOnCheckedChangeListener { buttonView, isChecked ->
            prefsViewModel.handleSetMode(if (isChecked) 0 else 1)
        }
        binding.switchTypeFraction.setOnCheckedChangeListener { buttonView, isChecked ->
            prefsViewModel.handleSetIsMixed(isChecked)
        }
        binding.tiEtPrecision.addTextChangedListener {
            var pr = it.toString().toIntOrNull()
            if (pr != null) {
                if (pr <= 4) {
                    prefsViewModel.handleSetPrecision(pr)
                    binding.tiEtPrecision.error = null
                } else {
                    binding.tiEtPrecision.error = "Введите число от 0 до 4"
                }
            } else {
                binding.tiEtPrecision.error = "Введите число от 0 до 4"
            }
        }
        return binding.root


    }

    private fun initViewModel() {
        prefsViewModel = ViewModelProvider(this).get(PreferencesViewModel::class.java)
        prefsViewModel.prefsLiveData.observe(viewLifecycleOwner, Observer {
            renderUi(it)
        })
    }

    fun renderUi(appSettings: AppSettings) {

        binding.switchMode.isChecked = appSettings.mode == 0
        if (appSettings.mode == 0) {
            binding.switchTypeFraction.isChecked = appSettings.isMixedFraction
            if (appSettings.isMixedFraction) {
                binding.fractionPreview.mode = Fraction.FractionType.MIXED

            } else {
                binding.fractionPreview.mode = Fraction.FractionType.COMMON
            }

        } else {
            binding.fractionPreview.mode = Fraction.FractionType.DECIMAL
            var pr = appSettings.precision
            if (pr != null) {
                if (pr <= 4) {
                    binding.fractionPreview.precision = pr
                    binding.tiEtPrecision.setText("${pr}")
                    binding.tiEtPrecision.error = null
                } else {
                    binding.tiEtPrecision.error = "Введите число от 0 до 4"
                }
            } else {
                binding.tiEtPrecision.error = "Введите число от 0 до 4"
            }
        }
        binding.switchTypeFraction.visibility =
            if (appSettings.mode == 0) View.VISIBLE else View.GONE
        binding.tiLayout.visibility = if (appSettings.mode == 0) View.GONE else View.VISIBLE
        binding.fractionPreview.invalidate()
    }

}
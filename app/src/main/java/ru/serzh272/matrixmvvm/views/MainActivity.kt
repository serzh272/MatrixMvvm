package ru.serzh272.matrixmvvm.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.databinding.ActivityMainBinding
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.viewmodels.MatrixViewModel
@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel:MatrixViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        viewModel = ViewModelProvider(this).get(MatrixViewModel::class.java)
        viewModel.matrices.observe(this, Observer { updateData(it) })

    }
    private fun updateData(it: List<Matrix>?) {
        binding.matrixView.mMatrix = it?.get(0) ?: Matrix(4, 4)
    }
}
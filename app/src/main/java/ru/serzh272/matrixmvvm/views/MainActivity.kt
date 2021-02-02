package ru.serzh272.matrixmvvm.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ru.serzh272.matrixmvvm.adapters.ViewPagerAdapter
import ru.serzh272.matrixmvvm.databinding.ActivityMainBinding
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.viewmodels.MatrixViewModel
@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel:MatrixViewModel
    lateinit var viewPager:ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        viewModel = ViewModelProvider(this).get(MatrixViewModel::class.java)
        viewModel.matrices.observe(this, Observer { updateData(it) })
        setSupportActionBar(binding.toolBar)
        viewPager = binding.viewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
    }
    private fun updateData(it: List<Matrix>?) {

    }
}
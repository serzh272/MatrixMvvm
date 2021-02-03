package ru.serzh272.matrixmvvm.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import ru.serzh272.matrixmvvm.R
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.determinant_item -> TODO("determinant action")
            R.id.summ_item -> TODO("summ action")
            R.id.diff_item -> TODO("diff action")
            R.id.multiply_item -> TODO("multiply action")
            R.id.inverse_item -> TODO("inverse action")
            R.id.frobenius_item -> TODO("frobenius action")
            R.id.upper_triangular_item -> TODO("upper action")
            R.id.lower_triangular_item -> TODO("lower action")
            R.id.transpose_item -> TODO("transpose action")
        }
        return super.onOptionsItemSelected(item)
    }
    private fun updateData(it: List<Matrix>?) {

    }
}
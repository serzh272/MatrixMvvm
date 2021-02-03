package ru.serzh272.matrixmvvm.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
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
    lateinit var viewPagerAdapter:ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        initViewModel()
        initViews()
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(this).get(MatrixViewModel::class.java)
        viewModel.matrA.observe(this, Observer { updateData(0, it) })
        viewModel.matrB.observe(this, Observer { updateData(1, it) })
        viewModel.matrRez.observe(this, Observer { updateData(2, it) })
    }

    fun initViews(){
        setSupportActionBar(binding.toolBar)
        viewPager = binding.viewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        for (n in viewPagerAdapter.matrixViews.indices){
            viewPagerAdapter.matrixViews[n].setOnDataChangedListener(object :MatrixViewGroup.OnDataChangedListener{
                override fun onDataChanged(matrix: Matrix) {
                    viewModel.setMatr(n, matrix)
                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.determinant_item -> {
                Toast.makeText(this, viewModel.getMatr(viewPager.currentItem).determinant().toString(), Toast.LENGTH_SHORT).show()
            }
            R.id.summ_item -> {
                viewModel.setMatr(2, viewModel.getMatr(0) + viewModel.getMatr(1))
                viewPager.currentItem = 2
            }
            R.id.diff_item -> {
                viewModel.setMatr(2, viewModel.getMatr(0) - viewModel.getMatr(1))
                viewPager.currentItem = 2
            }
            R.id.multiply_item -> {
                viewModel.setMatr(2, viewModel.getMatr(0) * viewModel.getMatr(1))
                viewPager.currentItem = 2
            }
            R.id.inverse_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).transformMatrix(Matrix.TransformType.INVERSE))
                viewPager.currentItem = 2
            }
            R.id.frobenius_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).getFrobeniusMatrix())
                viewPager.currentItem = 2
            }
            R.id.upper_triangular_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).transformMatrix(Matrix.TransformType.UPPER_TRIANGULAR))
                viewPager.currentItem = 2
            }
            R.id.lower_triangular_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).transformMatrix(Matrix.TransformType.LOWER_TRIANGULAR))
                viewPager.currentItem = 2
            }
            R.id.transpose_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).getTranspose())
                viewPager.currentItem = 2
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun updateData(ind: Int, matr: Matrix) {
        viewPagerAdapter.matrixViews[ind].matrix = matr
    }
}
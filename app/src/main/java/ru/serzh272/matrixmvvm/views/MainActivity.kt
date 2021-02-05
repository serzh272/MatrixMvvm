package ru.serzh272.matrixmvvm.views

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.adapters.ViewPagerAdapter
import ru.serzh272.matrixmvvm.databinding.ActivityMainBinding
import ru.serzh272.matrixmvvm.exceptions.DeterminantZeroException
import ru.serzh272.matrixmvvm.exceptions.MatrixDimensionsException
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.utils.MyToast
import ru.serzh272.matrixmvvm.viewmodels.MatrixViewModel

@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MatrixViewModel
    lateinit var viewPager: ViewPager
    lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        initViewModel()
        initViews()
    }

    fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MatrixViewModel::class.java)
        viewModel.matrA.observe(this, Observer { updateData(0, it) })
        viewModel.matrB.observe(this, Observer { updateData(1, it) })
        viewModel.matrRez.observe(this, Observer { updateData(2, it) })
    }


    fun initViews() {
        setSupportActionBar(binding.toolBar)
        viewPager = binding.viewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        for (n in viewPagerAdapter.matrixViews.indices) {
            viewPagerAdapter.matrixViews[n].setOnDataChangedListener(object :
                MatrixViewGroup.OnDataChangedListener {
                override fun onDataChanged(matrix: Matrix) {
                    viewModel.setMatr(n, matrix)
                }

            })
        }
        viewPager.setOnTouchListener { v, event ->
            //Log.d("M_MainActivity", "${event.toString()}")
            return@setOnTouchListener false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (MyToast.toast != null) {
            MyToast.toast!!.cancel()
        }
        when (item.itemId) {
            R.id.determinant_item -> {
                try {
                    MyToast.toast = Toast.makeText(
                        this,
                        viewModel.getMatr(viewPager.currentItem).determinant().toString(),
                        Toast.LENGTH_LONG
                    )
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.summ_item -> {
                try {
                    viewModel.setMatr(2, viewModel.getMatr(0) + viewModel.getMatr(1))
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.diff_item -> {
                try {
                    viewModel.setMatr(2, viewModel.getMatr(0) - viewModel.getMatr(1))
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.multiply_item -> {
                try {
                    viewModel.setMatr(2, viewModel.getMatr(0) * viewModel.getMatr(1))
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.inverse_item -> {
                try {
                    viewModel.setMatr(
                        2,
                        viewModel.getMatr(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.INVERSE)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.frobenius_item -> {
                try {
                    viewModel.setMatr(
                        2,
                        viewModel.getMatr(viewPager.currentItem).getFrobeniusMatrix()
                    )
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.upper_triangular_item -> {
                try {
                    viewModel.setMatr(
                        2,
                        viewModel.getMatr(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.UPPER_TRIANGULAR)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }

            }
            R.id.lower_triangular_item -> {
                try {
                    viewModel.setMatr(
                        2,
                        viewModel.getMatr(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.LOWER_TRIANGULAR)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(this, ex.message, Toast.LENGTH_SHORT)
                }

            }
            R.id.transpose_item -> {
                viewModel.setMatr(2, viewModel.getMatr(viewPager.currentItem).getTranspose())
                viewPager.currentItem = 2
            }
        }
        MyToast.toast?.show()
        MyToast.toast = null
        return super.onOptionsItemSelected(item)
    }

    private fun updateData(ind: Int, matr: Matrix) {
        viewPagerAdapter.matrixViews[ind].matrix = matr
    }
}
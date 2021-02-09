package ru.serzh272.matrixmvvm

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ru.serzh272.matrixmvvm.adapters.ViewPagerAdapter
import ru.serzh272.matrixmvvm.databinding.FragmentMainBinding
import ru.serzh272.matrixmvvm.exceptions.DeterminantZeroException
import ru.serzh272.matrixmvvm.exceptions.MatrixDimensionsException
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.utils.MyToast
import ru.serzh272.matrixmvvm.viewmodels.MatrixViewModel

@ExperimentalUnsignedTypes
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    lateinit var viewModel: MatrixViewModel
    lateinit var viewPager: ViewPager2
    var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        //setContentView(root)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        initViewModel()
        initViews()
        // Inflate the layout for this fragment


        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MatrixViewModel::class.java)
        viewModel.matrixLiveData1.observe(viewLifecycleOwner, Observer { updateData(0, it) })
        viewModel.matrixLiveData2.observe(viewLifecycleOwner, Observer { updateData(1, it) })
        viewModel.matrixLiveData3.observe(viewLifecycleOwner, Observer { updateData(2, it) })
        viewModel.titlesLiveData.observe(viewLifecycleOwner, Observer { updateTitles(it) })
    }

    private fun updateTitles(titles: MutableList<String>?) {
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.setText(titles?.get(i))
        }
    }


    fun initViews() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        viewPager = binding.viewPager
        val data = viewModel.loadData()
        viewPagerAdapter.updateData(data)
        Log.d("M_MainActivity", "View updated with data $data")
        viewPagerAdapter.setOnDataChangedListener(object : ViewPagerAdapter.OnDataChangedListener {
            override fun onDataChanged(pos: Int, matrix: Matrix) {
                viewModel.saveMatrix(pos, matrix)
            }

        })
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, pos ->
            tab.text = viewModel.titles[pos]
        }.attach()
        //TODO set onDataChangedListener for Matrix ViewGroup
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (MyToast.toast != null) {
            MyToast.toast!!.cancel()
        }
        when (item.itemId) {
            R.id.determinant_item -> {
                try {
                    MyToast.toast = Toast.makeText(
                        requireContext(),
                        viewModel.loadMatrix(viewPager.currentItem).determinant().toString(),
                        Toast.LENGTH_LONG
                    )
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.summ_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(0) + viewModel.loadMatrix(1)
                    )
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.diff_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(0) - viewModel.loadMatrix(1)
                    )
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.multiply_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(0) * viewModel.loadMatrix(1)
                    )
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.inverse_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.INVERSE)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.frobenius_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(viewPager.currentItem).getFrobeniusMatrix()
                    )
                    viewPager.currentItem = 2
                } catch (ex: MatrixDimensionsException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.upper_triangular_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.UPPER_TRIANGULAR)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }
            }
            R.id.lower_triangular_item -> {
                try {
                    viewPagerAdapter.updateMatrix(
                        2,
                        viewModel.loadMatrix(viewPager.currentItem)
                            .transformMatrix(Matrix.TransformType.LOWER_TRIANGULAR)
                    )
                    viewPager.currentItem = 2
                } catch (ex: DeterminantZeroException) {
                    MyToast.toast = Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT)
                }

            }
            R.id.transpose_item -> {
                viewPagerAdapter.updateMatrix(
                    2,
                    viewModel.loadMatrix(viewPager.currentItem).getTranspose()
                )
                viewPager.currentItem = 2
            }
            R.id.gershgorin_item -> {
                val act = MainFragmentDirections.actionMainFragmentToPlotFragment(viewModel.loadMatrix(viewPager.currentItem))
                findNavController().navigate(act)
            }
        }
        MyToast.toast?.show()
        MyToast.toast = null
        return super.onOptionsItemSelected(item)
    }

    private fun updateData(ind: Int, matr: Matrix) {
        Log.d("M_MainActivity", "LiveDataChanged with $matr in $ind index")

    }

}
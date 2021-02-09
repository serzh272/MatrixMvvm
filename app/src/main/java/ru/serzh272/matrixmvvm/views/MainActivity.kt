
package ru.serzh272.matrixmvvm.views
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import ru.serzh272.matrixmvvm.R

@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityMainBinding
    //var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //val root = binding.root
        setContentView(R.layout.activity_main)
    }


}
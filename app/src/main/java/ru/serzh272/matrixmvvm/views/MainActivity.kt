
package ru.serzh272.matrixmvvm.views
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.serzh272.matrixmvvm.databinding.ActivityMainBinding

@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
    }


}
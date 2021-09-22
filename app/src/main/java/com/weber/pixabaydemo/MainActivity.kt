package com.weber.pixabaydemo

import android.os.Bundle
import androidx.activity.viewModels
import com.weber.pixabaydemo.databinding.ActivityMainBinding
import com.weber.pixabaydemo.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.mtToolbar)
        setContentView(binding.root)
        mainViewModel.mLoading.observe(this@MainActivity, {
            setLoading(it)
        })
    }

    private fun setLoading(visible: Int) {
        setLoading(visible, binding.root)
    }
}
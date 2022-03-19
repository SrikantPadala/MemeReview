package com.ztute.memereview.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ztute.memereview.R
import com.ztute.memereview.common.hasInternetConnection
import com.ztute.memereview.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        observeState()
    }

    override fun onResume() {
        super.onResume()
        hasInternetConnection(application)
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.hasInternet.collect { hasInternet ->
                if (hasInternet)
                    binding.noInternet.visibility = View.GONE
                else
                    binding.noInternet.visibility = View.VISIBLE
            }
        }
    }
}
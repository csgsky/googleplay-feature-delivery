package com.csgsky.android.dog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.raywenderlich.android.shared.R
import com.raywenderlich.android.shared.databinding.ActivityCatsDogsBinding
import com.raywenderlich.android.shared.presentation.adapters.DogsCatsAdapter
import com.raywenderlich.android.shared.presentation.states.UIModel
import com.raywenderlich.android.shared.presentation.states.UIState
import com.raywenderlich.android.shared.presentation.viewmodels.CatsDogViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *  created by chenshaogang on 2022/1/18
 *  description:
 */
class DogsActivity: AppCompatActivity() {
    // 1
    private val catsDogViewModel: CatsDogViewModel by viewModel()
    private val catsDogsAdapter = DogsCatsAdapter()
    private lateinit var binding: ActivityCatsDogsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatsDogsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 2
        catsDogViewModel.getDogs()
        binding.rv.adapter = catsDogsAdapter
        observeDogs()
    }

    // 3
    private fun observeDogs() {
        lifecycleScope.launch {
            catsDogViewModel.dogs.flowWithLifecycle(lifecycle).collect { value: UIState ->
                when (value) {
                    is UIState.ShowData<*> -> {
                        binding.animationView.cancelAnimation()
                        binding.animationView.visibility = View.GONE
                        populateData(value.data as List<UIModel>)
                    }
                    is UIState.Error -> {
                        Toast.makeText(applicationContext, value.message, Toast.LENGTH_SHORT).show()
                        binding.animationView.cancelAnimation()
                        binding.animationView.visibility = View.GONE
                    }
                    UIState.Loading -> {
                        binding.animationView.apply {
                            setAnimation(R.raw.dog_animation)
                            playAnimation()
                            visibility = View.VISIBLE
                        }

                    }
                }
            }
        }
    }

    // 4
    private fun populateData(data: List<UIModel>) {
        catsDogsAdapter.submitList(data)
    }
}
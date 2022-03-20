package com.ztute.memereview.ui.meme_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.ztute.memereview.adapters.MemesAdapter
import com.ztute.memereview.common.safeNavigate
import com.ztute.memereview.databinding.MemesOverviewFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MemesOverviewFragment : Fragment() {

    val viewModel: MemesOverviewViewModel by viewModels()

    private lateinit var binding: MemesOverviewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MemesOverviewFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.memesGrid.adapter =
            MemesAdapter(MemesAdapter.OnClickListener { meme ->
                viewModel.displayMemeDetail(meme)
            })

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.CENTER
        binding.memesGrid.layoutManager = layoutManager

        observeState()
        return binding.root
    }

    private fun observeState() {
        val adapter = binding.memesGrid.adapter as MemesAdapter
        lifecycleScope.launchWhenStarted {
            viewModel.memes.collectLatest {
                adapter.submitList(it)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.hasInternet.collect { hasInternet ->
                if (!hasInternet) {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        "No Internet, Showing offline data. Turn on mobile data/wifi to fetch latest data",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressCircular.visibility = View.VISIBLE
                } else {
                    binding.progressCircular.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collect {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.navigateToSelectedMeme.collect {
                findNavController().safeNavigate(
                    MemesOverviewFragmentDirections.actionMemesOverviewFragmentToMemeDetailFragment(
                        it
                    )
                )
            }
        }
    }
}
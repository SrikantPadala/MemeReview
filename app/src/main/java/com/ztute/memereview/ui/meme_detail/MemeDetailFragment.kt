package com.ztute.memereview.ui.meme_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ztute.memereview.databinding.MemeDetailFragmentBinding

class MemeDetailFragment : Fragment() {

    private lateinit var binding: MemeDetailFragmentBinding
    private val viewModel: MemeDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MemeDetailFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val meme = MemeDetailFragmentArgs.fromBundle(
            requireArguments()
        ).meme
        viewModel.setData(meme)
    }

}
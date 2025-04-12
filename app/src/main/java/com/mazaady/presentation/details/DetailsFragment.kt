package com.mazaady.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mazaady.R
import com.mazaady.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        setupToolbar()
        setupListeners()
        observeState()
        viewModel.processIntent(DetailsIntent.LoadMovie(args.movie))
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.favoriteButton.setOnClickListener {
            viewModel.state.value.movie?.let { movie ->
                viewModel.processIntent(DetailsIntent.ToggleFavorite(movie))
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.detailsLoadingProgressBar.isVisible = state.isLoading
                binding.content.isVisible = state.movie != null

                state.movie?.let { movie ->
                    binding.apply {
                        titleText.text = movie.title
                        yearText.text = movie.year.toString()
                        ratingText.text = String.format("%.1f", movie.rating)
                        directorText.text = movie.director
                        plotText.text = movie.plot
                        
                        Glide.with(requireContext())
                            .load(movie.posterUrl)
                            .into(posterImage)

                        favoriteButton.setImageResource(
                            if (movie.isFavorite) R.drawable.ic_favorite
                            else R.drawable.ic_favorite_border
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

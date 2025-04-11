package com.mazaady.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        observeState()
        
        if (savedInstanceState == null) {
            viewModel.dispatchIntent(DetailsIntent.LoadMovie(args.movieId))
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.errorView.isVisible = state.error != null
                
                state.movie?.let { movie ->
                    binding.collapsingToolbar.title = movie.title
                    binding.titleText.text = movie.title
                    binding.releaseDateText.text = getString(R.string.release_date_format, movie.releaseDate)
                    binding.runtimeText.text = getString(R.string.runtime_format, movie.runtime)
                    binding.overviewText.text = movie.overview
                    
                    Glide.with(this@DetailsFragment)
                        .load(movie.posterUrl)
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .error(R.drawable.ic_movie_placeholder)
                        .into(binding.posterImage)

                    binding.favoriteButton.setImageResource(
                        if (movie.isFavorite) R.drawable.ic_favorite
                        else R.drawable.ic_favorite_border
                    )

                    binding.favoriteButton.setOnClickListener {
                        viewModel.dispatchIntent(DetailsIntent.ToggleFavorite(movie))
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

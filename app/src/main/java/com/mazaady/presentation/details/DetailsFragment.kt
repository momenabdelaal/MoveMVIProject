package com.mazaady.presentation.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mazaady.R
import com.mazaady.databinding.FragmentDetailsBinding
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModels()

    @Inject
    lateinit var repository: MovieRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        viewModel.setMovie(args.movie)
        setupViews()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.movie?.let { movie ->
                    binding.apply {
                        // Update favorite button
                        favoriteButton.setImageResource(
                            if (movie.isFavorite) R.drawable.ic_favorite 
                            else R.drawable.ic_favorite_border
                        )
                    }
                }
            }
        }
    }

    private fun setupViews() {
        val movie = args.movie
        
        binding.apply {
            // Setup toolbar
            toolbar.apply {
                title = movie.title
                setNavigationOnClickListener { findNavController().navigateUp() }
            }
            
            // Load movie poster
            Glide.with(requireContext())
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(posterImage)

            // Set movie details
            titleText.text = movie.title
            overviewText.text = movie.plot
            releaseDateText.text = getString(R.string.release_date_format, movie.year.toString())
            ratingText.text = getString(R.string.rating_format, movie.rating)
            
            // Setup favorite button
            favoriteButton.apply {
                setImageResource(if (movie.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                setOnClickListener {
                    viewModel.toggleFavorite()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

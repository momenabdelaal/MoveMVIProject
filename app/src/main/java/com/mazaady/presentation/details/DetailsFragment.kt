package com.mazaady.presentation.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mazaady.R
import com.mazaady.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        setupToolbar()
        displayMovieDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun displayMovieDetails() {
        val movie = args.movie
        
        binding.apply {
            toolbar.title = movie.title
            
            Glide.with(requireContext())
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(posterImage)

            titleText.text = movie.title
            overviewText.text = movie.plot
            releaseDateText.text = getString(R.string.release_date_format, movie.year.toString())
            ratingText.text = getString(R.string.rating_format, movie.rating)
            
            favoriteButton.apply {
                isSelected = movie.isFavorite
                setOnClickListener {
                    // Handle favorite toggle if needed
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

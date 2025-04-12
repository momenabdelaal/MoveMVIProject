package com.mazaady.presentation.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazaady.R
import com.mazaady.databinding.FragmentFavoritesBinding
import com.mazaady.domain.model.Movie
import com.mazaady.presentation.home.MovieAdapter
import com.mazaady.presentation.home.MovieItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val movieAdapter = MovieAdapter(
        onMovieClick = { movie -> navigateToDetails(movie) },
        onFavoriteClick = { movie -> toggleFavorite(movie) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeState()
        viewModel.processIntent(FavoritesIntent.LoadFavorites)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return 1 // Each item takes 1 span (so 2 items per row)
                    }
                }
            }
            setHasFixedSize(true)
            addItemDecoration(MovieItemDecoration(requireContext()))
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.processIntent(FavoritesIntent.LoadFavorites)
        }

        binding.toggleButton.setOnClickListener {
            viewModel.processIntent(FavoritesIntent.ToggleLayout)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.swipeRefreshLayout.isRefreshing = state.isLoading
                binding.errorText.isVisible = state.error != null
                binding.errorText.text = state.error

                // Update layout manager
                if (state.isGrid != (binding.recyclerView.layoutManager is GridLayoutManager)) {
                    binding.recyclerView.layoutManager = if (state.isGrid) {
                        GridLayoutManager(requireContext(), 2)
                    } else {
                        LinearLayoutManager(requireContext())
                    }
                }

                // Update movies
                movieAdapter.submitList(state.movies)
            }
        }
    }

    private fun navigateToDetails(movie: Movie) {
        viewModel.processIntent(FavoritesIntent.NavigateToDetails(movie))
        findNavController().navigate(
            FavoritesFragmentDirections.actionFavoritesToDetailsFragment(movie)
        )
    }

    private fun toggleFavorite(movie: Movie) {
        viewModel.processIntent(FavoritesIntent.ToggleFavorite(movie))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

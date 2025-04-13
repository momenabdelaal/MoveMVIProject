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
import com.mazaady.presentation.common.ErrorState
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
            layoutManager = GridLayoutManager(requireContext(), 2)
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
                binding.swipeRefreshLayout.isRefreshing = false
                binding.loadingIndicator.isVisible = state.isLoading && !binding.swipeRefreshLayout.isRefreshing
                handleError(state)

                // Update layout manager
                if (state.isGrid != (binding.recyclerView.layoutManager is GridLayoutManager)) {
                    binding.recyclerView.layoutManager = if (state.isGrid) {
                        GridLayoutManager(requireContext(), 2)
                    } else {
                        LinearLayoutManager(requireContext())
                    }
                }

                // Update movies
                if (!state.isLoading && state.error == null) {
                    movieAdapter.submitList(state.movies)
                }
            }
        }
    }

    private fun handleError(state: FavoritesState) {
        val errorState = when {
            state.error?.contains("internet", ignoreCase = true) == true -> ErrorState.NoInternet
            state.error != null -> ErrorState.ServerError
            state.movies.isEmpty() -> ErrorState.EmptyFavorites
            else -> null
        }

        if (errorState != null) {
            binding.errorStateView.apply {
                isVisible = true
                setErrorState(errorState) {
                    viewModel.processIntent(FavoritesIntent.LoadFavorites)
                }
            }
            binding.recyclerView.isVisible = false
        } else {
            binding.errorStateView.isVisible = false
            binding.recyclerView.isVisible = true
        }
    }

    private fun navigateToDetails(movie: Movie) {
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

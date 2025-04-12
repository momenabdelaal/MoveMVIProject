package com.mazaady.presentation.home

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
import com.mazaady.databinding.FragmentHomeBinding
import com.mazaady.domain.model.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val movieAdapter = MovieAdapter(
        onMovieClick = { movie -> navigateToDetails(movie) },
        onFavoriteClick = { movie -> toggleFavorite(movie) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setupRecyclerView()
        setupListeners()
        observeState()
        viewModel.processIntent(HomeIntent.LoadMovies)
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
            viewModel.processIntent(HomeIntent.RefreshMovies)
        }

        binding.toggleButton.setOnClickListener {
            viewModel.processIntent(HomeIntent.ToggleLayout)
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

                // Collect movies
                state.movies?.let { moviesFlow ->
                    moviesFlow.collectLatest { pagingData ->
                        movieAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun navigateToDetails(movie: Movie) {
        viewModel.processIntent(HomeIntent.NavigateToDetails(movie))
        findNavController().navigate(
            HomeFragmentDirections.actionHomeToDetailsFragment(movie)
        )
    }

    private fun toggleFavorite(movie: Movie) {
        viewModel.processIntent(HomeIntent.ToggleFavorite(movie))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.mazaady.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazaady.R
import com.mazaady.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val movieAdapter by lazy {
        MovieAdapter(
            onMovieClick = { movie ->
                viewModel.dispatchIntent(HomeIntent.NavigateToDetails(movie.id))
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToDetails(movie.id)
                )
            },
            onFavoriteClick = { movie ->
                viewModel.dispatchIntent(HomeIntent.ToggleFavorite(movie))
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()
        observeState()
        
        if (savedInstanceState == null) {
            viewModel.dispatchIntent(HomeIntent.LoadMovies)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = movieAdapter
        binding.swipeRefresh.setOnRefreshListener {
            movieAdapter.refresh()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_toggle_view -> {
                    val isGrid = binding.recyclerView.layoutManager is LinearLayoutManager
                    viewModel.dispatchIntent(HomeIntent.ToggleViewType(isGrid))
                    true
                }
                else -> false
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.errorView.isVisible = state.error != null
                binding.errorView.text = state.error

                // Update layout manager based on view type
                binding.recyclerView.layoutManager = if (state.isGrid) {
                    GridLayoutManager(requireContext(), 2)
                } else {
                    LinearLayoutManager(requireContext())
                }

                movieAdapter.submitData(state.movies)
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

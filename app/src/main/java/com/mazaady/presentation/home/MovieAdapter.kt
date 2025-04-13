package com.mazaady.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mazaady.R
import com.mazaady.databinding.ItemMovieBinding
import com.mazaady.domain.model.Movie
import timber.log.Timber

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : PagingDataAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback) {

    private var currentList: List<Movie> = emptyList()

    fun submitList(list: List<Movie>?) {
        currentList = list ?: emptyList()
        notifyDataSetChanged() // For simplicity, we can optimize this later with DiffUtil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = if (currentList.isNotEmpty()) {
            currentList[position]
        } else {
            getItem(position)
        }
        movie?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return if (currentList.isNotEmpty()) currentList.size else super.getItemCount()
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = if (currentList.isNotEmpty()) {
                        currentList[position]
                    } else {
                        getItem(position)
                    }
                    movie?.let { onMovieClick(it) }
                }
            }

            binding.favoriteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = if (currentList.isNotEmpty()) {
                        currentList[position]
                    } else {
                        getItem(position)
                    }
                    movie?.let { onFavoriteClick(it) }
                }
            }
        }

        fun bind(movie: Movie) {
            binding.apply {
                titleText.text = movie.title
                yearText.text = movie.year.toString()
                
                Timber.d("Loading poster: ${movie.posterUrl}")
                Glide.with(posterImage)
                    .load(movie.posterUrl)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .into(posterImage)

                favoriteButton.setImageResource(
                    if (movie.isFavorite) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_border
                )
            }
        }
    }

    companion object {
        object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}

package com.mazaady.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<I : MviIntent, S : MviState> : ViewModel() {
    
    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<S> = _state

    abstract fun createInitialState(): S

    abstract fun handleIntent(intent: I)

    protected fun setState(reduce: S.() -> S) {
        val newState = state.value.reduce()
        _state.value = newState
    }

    fun dispatchIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }
}

package com.mazaady.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<S : MviState, I : MviIntent> : ViewModel() {
    
    protected val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<S> = _state

    abstract fun createInitialState(): S

    fun processIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected abstract fun handleIntent(intent: I)
}

package com.mazaady.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.mazaady.databinding.LayoutErrorStateBinding

class ErrorStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutErrorStateBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setErrorState(
        errorState: ErrorState,
        onRetry: () -> Unit
    ) {
        with(binding) {
            errorTitle.setText(errorState.titleRes)
            errorMessage.setText(errorState.messageRes)
            retryButton.setOnClickListener { onRetry() }
        }
    }
}

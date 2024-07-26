package com.subreax.schedule.ui

import com.subreax.schedule.utils.UiText

sealed class UiLoadingState {
    data object Loading : UiLoadingState()
    data object Ready : UiLoadingState()
    data class Error(val message: UiText): UiLoadingState()
}

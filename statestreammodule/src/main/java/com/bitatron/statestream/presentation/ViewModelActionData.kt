package com.bitatron.statestream.presentation

data class ViewModelActionData<T>(val uiModel: T, val dataViewModelAction: DataViewModelAction) where T: UiModel
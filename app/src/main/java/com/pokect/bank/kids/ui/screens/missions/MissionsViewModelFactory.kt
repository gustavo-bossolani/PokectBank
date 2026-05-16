package com.pokect.bank.kids.ui.screens.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pokect.bank.kids.data.repository.AppRepository

class MissionsViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionsViewModel::class.java)) {
            return MissionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

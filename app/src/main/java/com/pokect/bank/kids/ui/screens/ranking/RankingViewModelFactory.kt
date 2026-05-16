package com.pokect.bank.kids.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pokect.bank.kids.data.repository.AppRepository

class RankingViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            return RankingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

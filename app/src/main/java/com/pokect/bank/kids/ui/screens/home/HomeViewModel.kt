package com.pokect.bank.kids.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokect.bank.kids.data.models.MissionStatus
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.data.repository.RepositoryEvent
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadInitialData() }
        viewModelScope.launch {
            repository.events.collect { event ->
                when (event) {
                    is RepositoryEvent.UserUpdated -> refreshUserData()
                    is RepositoryEvent.TransactionsChanged -> refreshTransactions()
                    else -> {}
                }
            }
        }
    }

    private suspend fun loadInitialData() {
        val user = repository.getUser()
        val inProgressMissions = repository.getMissions()
            .filter { it.status == MissionStatus.IN_PROGRESS }
            .take(2)
        val transactions = repository.getTransactions()

        _uiState.update {
            it.copy(
                missions = inProgressMissions,
                transactions = transactions
            ).withUser(user)
        }
    }

    private suspend fun refreshUserData() {
        val user = repository.getUser()
        val inProgressMissions = repository.getMissions()
            .filter { it.status == MissionStatus.IN_PROGRESS }
            .take(2)

        _uiState.update {
            it.copy(missions = inProgressMissions).withUser(user)
        }
    }

    private suspend fun refreshTransactions() {
        val transactions = repository.getTransactions()
        _uiState.update { it.copy(transactions = transactions) }
    }

    fun showDepositModal() {
        _uiState.update {
            it.copy(
                showDepositModal = true,
                depositAmount = 5.0,
                depositSuccess = false,
                error = null
            )
        }
    }

    fun hideDepositModal() {
        _uiState.update { it.copy(showDepositModal = false) }
    }

    fun updateDepositAmount(amount: Double) {
        if (amount < 1.0) return
        _uiState.update { it.copy(depositAmount = amount) }
    }

    fun selectQuickAmount(amount: Double) {
        if (amount < 1.0) return
        _uiState.update { it.copy(depositAmount = amount) }
    }

    fun incrementDepositAmount() {
        _uiState.update {
            it.copy(depositAmount = (it.depositAmount + 1.0).coerceAtLeast(1.0))
        }
    }

    fun decrementDepositAmount() {
        _uiState.update {
            it.copy(depositAmount = (it.depositAmount - 1.0).coerceAtLeast(1.0))
        }
    }

    fun deposit(amount: Double) {
        if (amount < 1.0) return

        viewModelScope.launch {
            val result = repository.deposit(amount)
            if (result.isSuccess) {
                // Event collector will handle refresh via UserUpdated
                _uiState.update { it.copy(showDepositModal = false, showConfetti = true) }

                delay(AnimationSpecs.CONFETTI_DURATION.toLong())
                _uiState.update { it.copy(showConfetti = false) }
            } else {
                _uiState.update {
                    it.copy(
                        error = result.exceptionOrNull()?.message ?: "Erro ao guardar moedinhas",
                        showDepositModal = false
                    )
                }
            }
        }
    }

    fun onConfettiFinished() {
        _uiState.update { it.copy(showConfetti = false) }
    }

    fun toggleHistory() {
        _uiState.update { it.copy(isHistoryExpanded = !it.isHistoryExpanded) }
    }
}

// ============================================================================
// Top-level helper functions
// ============================================================================

private fun HomeUiState.withUser(user: com.pokect.bank.kids.data.models.User): HomeUiState = copy(
    userName = user.name,
    balance = user.balance,
    goal = user.goal,
    level = user.level,
    currentXp = user.xp,
    xpToNextLevel = user.xpToNextLevel,
    totalXp = user.totalXp,
    streak = user.streak,
    coins = user.coins,
    badges = user.badges,
    selectedAvatar = user.selectedAvatar
)

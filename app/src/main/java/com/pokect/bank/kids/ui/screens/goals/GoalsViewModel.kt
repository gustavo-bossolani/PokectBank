package com.pokect.bank.kids.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokect.bank.kids.data.models.SavingsGoal
import com.pokect.bank.kids.data.models.User
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.data.repository.RepositoryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalsUiState(
    val goals: List<SavingsGoal> = emptyList(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val showAddGoalModal: Boolean = false,
    val error: String? = null
)

class GoalsViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadGoalsAndUser() }
        viewModelScope.launch {
            repository.events.collect { event ->
                when (event) {
                    is RepositoryEvent.GoalsChanged,
                    is RepositoryEvent.UserUpdated -> loadGoalsAndUser()
                    else -> {}
                }
            }
        }
    }

    private suspend fun loadGoalsAndUser() {
        val goals = repository.getGoals()
        val user = repository.getUser()
        _uiState.update { it.copy(goals = goals, user = user) }
    }

    fun showAddGoalModal() {
        _uiState.update { it.copy(showAddGoalModal = true) }
    }

    fun hideAddGoalModal() {
        _uiState.update { it.copy(showAddGoalModal = false) }
    }

    fun createGoal(name: String, icon: String, targetAmount: Double, daysLeft: Int) {
        viewModelScope.launch {
            val result = repository.createGoal(name, icon, targetAmount, daysLeft)
            if (result.isSuccess) {
                _uiState.update { it.copy(showAddGoalModal = false, error = null) }
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message ?: "Erro ao criar meta") }
            }
        }
    }

    fun allocateToGoal(goalId: String, amount: Double) {
        viewModelScope.launch {
            val result = repository.allocateToGoal(goalId, amount)
            if (result.isSuccess) {
                _uiState.update { it.copy(showAddGoalModal = false, error = null) }
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message ?: "Erro ao resgatar para meta") }
            }
        }
    }
}

package com.pokect.bank.kids.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokect.bank.kids.data.models.FamilyMember
import com.pokect.bank.kids.data.models.WeeklyChallenge
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.data.repository.RepositoryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// UI Models
// ============================================================================

data class RankingMember(
    val member: FamilyMember,
    val position: Int,
    val isCurrentUser: Boolean
)

data class RankingUiState(
    val members: List<RankingMember> = emptyList(),
    val currentUser: FamilyMember? = null,
    val weeklyChallenge: WeeklyChallenge? = null
)

// ============================================================================
// ViewModel
// ============================================================================

class RankingViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadInitialData() }
        viewModelScope.launch {
            repository.events.collect { event ->
                when (event) {
                    is RepositoryEvent.RankingChanged,
                    is RepositoryEvent.UserUpdated -> loadInitialData()
                    else -> {}
                }
            }
        }
    }

    private suspend fun loadInitialData() {
        val sortedMembers = repository.getFamilyMembers().sortedByDescending { it.xp }
        val rankingMembers = sortedMembers.mapIndexed { index, member ->
            RankingMember(
                member = member,
                position = index + 1,
                isCurrentUser = member.isCurrentUser
            )
        }

        val currentUser = sortedMembers.find { it.isCurrentUser }
        val weeklyChallenge = repository.getWeeklyChallenges().firstOrNull()

        _uiState.update {
            it.copy(
                members = rankingMembers,
                currentUser = currentUser,
                weeklyChallenge = weeklyChallenge
            )
        }
    }

    fun joinWeeklyChallenge(challengeId: String) {
        viewModelScope.launch {
            val result = repository.joinWeeklyChallenge(challengeId)
            if (result.isSuccess) {
                // Reload from repository after successful join
                loadInitialData()
            }
        }
    }
}

package com.pokect.bank.kids.ui.screens.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokect.bank.kids.data.models.Mission
import com.pokect.bank.kids.data.models.MissionCategory
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.data.repository.RepositoryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// Filter category enum — includes ALL for "Todas" filter
// ============================================================================
enum class MissionFilterCategory(val label: String) {
    ALL("Todas"),
    DAILY("Diárias"),
    WEEKLY("Semanais"),
    SPECIAL("Especiais");

    fun toMissionCategory(): MissionCategory? = when (this) {
        ALL -> null
        DAILY -> MissionCategory.DAILY
        WEEKLY -> MissionCategory.WEEKLY
        SPECIAL -> MissionCategory.SPECIAL
    }
}

// ============================================================================
// UI State
// ============================================================================
data class MissionsUiState(
    val selectedFilter: MissionFilterCategory = MissionFilterCategory.ALL,
    val allMissions: List<Mission> = emptyList(),
    val filteredMissions: List<Mission> = emptyList()
)

// ============================================================================
// ViewModel
// ============================================================================
class MissionsViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadMissions() }
        viewModelScope.launch {
            repository.events.collect { event ->
                if (event is RepositoryEvent.MissionsChanged) {
                    loadMissions()
                }
            }
        }
    }

    private suspend fun loadMissions() {
        val missions = repository.getMissions()
        _uiState.update {
            it.copy(
                allMissions = missions,
                filteredMissions = missions
            )
        }
    }

    fun selectFilter(filter: MissionFilterCategory) {
        val category = filter.toMissionCategory()
        _uiState.update { state ->
            val filtered = if (category == null) {
                state.allMissions
            } else {
                state.allMissions.filter { it.category == category }
            }
            state.copy(
                selectedFilter = filter,
                filteredMissions = filtered
            )
        }
    }
}

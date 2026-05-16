package com.pokect.bank.kids.ui.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokect.bank.kids.data.models.Reward
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.data.repository.RepositoryEvent
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// UI Models
// ============================================================================

data class RewardUi(
    val reward: Reward,
    val canAfford: Boolean,
    val progressPercent: Float
)

data class RewardsUiState(
    val coins: Int = 0,
    val rewards: List<RewardUi> = emptyList(),
    val showRedemptionModal: Boolean = false,
    val selectedReward: Reward? = null,
    val showConfetti: Boolean = false,
    val redemptionSuccessMessage: String = "",
    val error: String? = null
)

// ============================================================================
// ViewModel
// ============================================================================

class RewardsViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    // Delay before showing confetti after successful redemption — allows modal dismissal animation to complete
    private val redemptionTransitionDelay = 300L

    init {
        viewModelScope.launch { loadInitialData() }
        viewModelScope.launch {
            repository.events.collect { event ->
                when (event) {
                    is RepositoryEvent.UserUpdated -> refreshCoins()
                    is RepositoryEvent.RewardsChanged -> refreshRewards()
                    else -> {}
                }
            }
        }
    }

    private suspend fun loadInitialData() {
        val coins = repository.getUser().coins
        val rewards = repository.getRewards()
        _uiState.update {
            it.copy(
                coins = coins,
                rewards = mapToRewardUi(rewards, coins)
            )
        }
    }

    private suspend fun refreshCoins() {
        val coins = repository.getUser().coins
        _uiState.update { it.copy(coins = coins) }
    }

    private suspend fun refreshRewards() {
        val coins = repository.getUser().coins
        val rewards = repository.getRewards()
        _uiState.update {
            it.copy(
                coins = coins,
                rewards = mapToRewardUi(rewards, coins)
            )
        }
    }

    private fun mapToRewardUi(rewards: List<Reward>, coins: Int): List<RewardUi> {
        return rewards.map { reward ->
            RewardUi(
                reward = reward,
                canAfford = coins >= reward.cost,
                progressPercent = if (reward.cost > 0) {
                    (coins.toFloat() / reward.cost).coerceIn(0f, 1f)
                } else {
                    1f
                }
            )
        }
    }

    fun redeemReward(rewardId: String) {
        val reward = _uiState.value.rewards.find { it.reward.id == rewardId }?.reward ?: return
        _uiState.update {
            it.copy(
                selectedReward = reward,
                showRedemptionModal = true
            )
        }
    }

    fun confirmRedemption() {
        val selected = _uiState.value.selectedReward ?: return
        val currentCoins = _uiState.value.coins
        if (currentCoins < selected.cost) {
            _uiState.update {
                it.copy(
                    showRedemptionModal = false,
                    selectedReward = null,
                    error = "Moedas insuficientes"
                )
            }
            return
        }

        viewModelScope.launch {
            val result = repository.redeemReward(selected.id)
            if (result.isSuccess) {
                delay(redemptionTransitionDelay)
                refreshCoins()
                _uiState.update { state ->
                    state.copy(
                        showRedemptionModal = false,
                        selectedReward = null,
                        showConfetti = true,
                        redemptionSuccessMessage = "Prêmio resgatado!"
                    )
                }

                delay(AnimationSpecs.CONFETTI_DURATION.toLong())
                _uiState.update { it.copy(showConfetti = false) }
            } else {
                _uiState.update { it.copy(
                    showRedemptionModal = false,
                    selectedReward = null,
                    error = result.exceptionOrNull()?.message ?: "Erro ao resgatar prêmio"
                ) }
            }
        }
    }

    fun dismissRedemptionModal() {
        _uiState.update {
            it.copy(
                showRedemptionModal = false,
                selectedReward = null
            )
        }
    }

    fun dismissConfetti() {
        _uiState.update {
            it.copy(
                showConfetti = false,
                redemptionSuccessMessage = ""
            )
        }
    }
}

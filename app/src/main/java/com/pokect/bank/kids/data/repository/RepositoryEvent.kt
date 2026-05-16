package com.pokect.bank.kids.data.repository

sealed interface RepositoryEvent {
    data object UserUpdated : RepositoryEvent
    data object MissionsChanged : RepositoryEvent
    data object GoalsChanged : RepositoryEvent
    data object RewardsChanged : RepositoryEvent
    data object RankingChanged : RepositoryEvent
    data object TransactionsChanged : RepositoryEvent
}

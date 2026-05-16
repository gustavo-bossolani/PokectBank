package com.pokect.bank.kids.di

import com.pokect.bank.kids.data.repository.AppRepository

object AppContainer {
    val repository: AppRepository by lazy { AppRepository() }
}

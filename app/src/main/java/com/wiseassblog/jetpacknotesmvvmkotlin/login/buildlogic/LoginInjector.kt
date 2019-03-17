package com.wiseassblog.jetpacknotesmvvmkotlin.login.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseUserRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository

class LoginInjector(application: Application): AndroidViewModel(application) {

    init {
        FirebaseApp.initializeApp(application)
    }

    private fun getUserRepository(): IUserRepository {
        return FirebaseUserRepoImpl()
    }

    fun provideUserViewModelFactory(): UserViewModelFactory =
        UserViewModelFactory(
            getUserRepository()
        )

}
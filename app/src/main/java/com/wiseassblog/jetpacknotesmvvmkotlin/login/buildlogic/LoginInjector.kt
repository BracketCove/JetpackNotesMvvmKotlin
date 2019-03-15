package com.wiseassblog.jetpacknotesmvvmkotlin.login.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseAuthRepositoryImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository

object LoginInjector {

    private fun getUserRepository(context: Context): IUserRepository {
        FirebaseApp.initializeApp(context)
        return FirebaseAuthRepositoryImpl(FirebaseAuth.getInstance())
    }

    fun provideUserViewModelFactory(context: Context): UserViewModelFactory =
        UserViewModelFactory(
            getUserRepository(
                context
            )
        )

}
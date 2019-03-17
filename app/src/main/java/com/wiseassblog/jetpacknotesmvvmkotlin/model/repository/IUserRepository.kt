package com.wiseassblog.jetpacknotesmvvmkotlin.model.repository

import com.wiseassblog.jetpacknotesmvvmkotlin.common.Result
import com.wiseassblog.jetpacknotesmvvmkotlin.model.User

interface IUserRepository {
    suspend fun getCurrentUser(): Result<Exception, User?>

    suspend fun signOutCurrentUser(): Result<Exception, Unit>

    suspend fun signInGoogleUser(idToken: String): Result<Exception, Unit>
}
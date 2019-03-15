package com.wiseassblog.jetpacknotesmvvmkotlin.login

import androidx.lifecycle.MutableLiveData
import com.wiseassblog.jetpacknotesmvvmkotlin.common.*
import com.wiseassblog.jetpacknotesmvvmkotlin.model.LoginResult
import com.wiseassblog.jetpacknotesmvvmkotlin.model.User
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class UserViewModel(
    val repo: IUserRepository,
    uiContext: CoroutineContext
) : BaseViewModel<LoginEvent<LoginResult>>(uiContext) {
    //Note: I'm using MutableLiveData since my backend does not return LiveData objects.
    //View States:
    val user = MutableLiveData<User>()

    val authAttempt = MutableLiveData<Unit>()

    override fun handleEvent(event: LoginEvent<LoginResult>) {
        //Trigger loading screen
        loading.value = Unit
        when (event) {
            is LoginEvent.OnStart -> getUser()
            is LoginEvent.OnAuthButtonClick -> onAuthButtonClick()
            is LoginEvent.OnGoogleSignInResult -> onSignInResult(event.result)
        }
    }

    private fun onSignInResult(result: LoginResult) = launch {
        if (result.requestCode == RC_SIGN_IN && result.userToken != null) {

            val createGoogleUserResult = repo.signInGoogleUser(
                result.userToken
            )

            when (createGoogleUserResult) {
                is Result.Value -> getUser()
                is Result.Error -> error.value = LOGIN_ERROR
            }
        } else {
            error.value = LOGIN_ERROR
        }
    }

    private fun onAuthButtonClick() {
        if (user.value == null) authAttempt.value = Unit
        else deleteUser()
    }

    private fun deleteUser() = launch {
        val result = repo.signOutCurrentUser()

        when (result) {
            is Result.Value -> user.value = null
            is Result.Error -> error.value = LOGOUT_ERROR
        }
    }

    private fun getUser() = launch {
        val result = repo.getCurrentUser()
        when (result) {
            is Result.Value -> user.value = result.value
            is Result.Error -> error.value = LOGIN_ERROR
        }
    }
}
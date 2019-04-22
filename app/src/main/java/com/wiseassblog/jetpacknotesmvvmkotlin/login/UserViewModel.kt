package com.wiseassblog.jetpacknotesmvvmkotlin.login

import androidx.lifecycle.MutableLiveData
import com.wiseassblog.jetpacknotesmvvmkotlin.common.*
import com.wiseassblog.jetpacknotesmvvmkotlin.model.LoginResult
import com.wiseassblog.jetpacknotesmvvmkotlin.model.User
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * This approach to ViewModels reduces the complexity of the View by containing specific details about widgets and
 * controls present in the View. The benefit of doing so is to make the View in to a Humble Object; reducing or
 * eliminating the need to test the View.
 *
 * The downside of this approach, is that the ViewModel is no longer re-usable across a variety of Views. In this case,
 * since this ViewModel is only used by a single View, and the application architecture will not change any time soon,
 * losing re-usability in exchange for a simpler View is not a problem.
 */
class UserViewModel(
    val repo: IUserRepository,
    uiContext: CoroutineContext
) : BaseViewModel<LoginEvent<LoginResult>>(uiContext) {

    //The actual data model is kept private to avoid unwanted tampering
    private val userState = MutableLiveData<User>()

    //Control Logic
    internal val authAttempt = MutableLiveData<Unit>()
    internal val startAnimation = MutableLiveData<Unit>()

    //UI Binding
    internal val signInStatusText = MutableLiveData<String>()
    internal val authButtonText = MutableLiveData<String>()
    internal val satelliteDrawable = MutableLiveData<String>()

    private fun showErrorState() {
        signInStatusText.value = LOGIN_ERROR
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    private fun showLoadingState() {
        signInStatusText.value = LOADING
        satelliteDrawable.value = ANTENNA_LOOP
        startAnimation.value = Unit
    }

    private fun showSignedInState() {
        signInStatusText.value = SIGNED_IN
        authButtonText.value = SIGN_OUT
        satelliteDrawable.value = ANTENNA_FULL
    }

    private fun showSignedOutState() {
        signInStatusText.value = SIGNED_OUT
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    override fun handleEvent(event: LoginEvent<LoginResult>) {
        //Trigger loading screen first
        showLoadingState()
        when (event) {
            is LoginEvent.OnStart -> getUser()
            is LoginEvent.OnAuthButtonClick -> onAuthButtonClick()
            is LoginEvent.OnGoogleSignInResult -> onSignInResult(event.result)
        }
    }

    private fun getUser() = launch {
        val result = repo.getCurrentUser()
        when (result) {
            is Result.Value -> {
                userState.value = result.value
                if (result.value == null) showSignedOutState()
                else showSignedInState()
            }
            is Result.Error -> showErrorState()
        }
    }

    /**
     * If user is null, tell the View to begin the authAttempt. Else, attempt to sign the user out
     */
    private fun onAuthButtonClick() {
        if (userState.value == null) authAttempt.value = Unit
        else signOutUser()
    }

    private fun onSignInResult(result: LoginResult) = launch {
        if (result.requestCode == RC_SIGN_IN && result.userToken != null) {

            val createGoogleUserResult = repo.signInGoogleUser(
                result.userToken
            )

            //Result.Value means it was successful
            if (createGoogleUserResult is Result.Value) getUser()
            else showErrorState()
        } else {
            showErrorState()
        }
    }

    private fun signOutUser() = launch {
        val result = repo.signOutCurrentUser()

        when (result) {
            is Result.Value -> {
                userState.value = null
                showSignedOutState()
            }
            is Result.Error -> showErrorState()
        }
    }


}
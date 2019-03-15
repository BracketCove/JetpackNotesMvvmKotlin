package com.wiseassblog.jetpacknotesmvvmkotlin.login


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.wiseassblog.jetpacknotesmvvmkotlin.common.RC_SIGN_IN
import com.wiseassblog.jetpacknotesmvvmkotlin.common.makeToast
import com.wiseassblog.jetpacknotesmvvmkotlin.login.buildlogic.LoginInjector
import com.wiseassblog.jetpacknotesmvvmkotlin.model.LoginResult
import kotlinx.android.synthetic.main.fragment_login.*

//Note: if you want to support more than just English, you'll want to use Strings.xml instead of const val
private const val SIGN_OUT = "SIGN OUT"
private const val SIGN_IN = "SIGN IN"
private const val SIGNED_IN = "Signed In"
private const val SIGNED_OUT = "Signed Out"
private const val ERROR_NETWORK_UNAVAILABLE = "Network Unavailable"
private const val ERROR_AUTH = "An Error Has Occured"
private const val RETRY = "RETRY"
private const val ANTENNA_EMPTY = "antenna_empty"
private const val ANTENNA_FULL = "antenna_full"

class LoginView : Fragment() {

    private lateinit var viewModel: UserViewModel

    //Create and bind to ViewModel
    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(
            this,
            LoginInjector.provideUserViewModelFactory(requireContext())
        ).get(
            UserViewModel::class.java
        )

        btn_auth_attempt.setOnClickListener { viewModel.handleEvent(LoginEvent.OnAuthButtonClick) }

        observeViewModel()

        viewModel.handleEvent(LoginEvent.OnStart)
    }

    private fun observeViewModel() {
        viewModel.user.observe(
            viewLifecycleOwner,
            Observer { user ->
                if (user == null) showNullUserState()
                else showUserState()
            }
        )

        viewModel.loading.observe(
            viewLifecycleOwner,
            Observer { showLoadingState() }
        )

        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.authAttempt.observe(
            viewLifecycleOwner,
            Observer { startSignInFlow() }
        )
    }

    private fun showErrorState(errorMessage: String?) = makeToast(errorMessage!!)

    private fun showLoadingState() {
        imv_antenna_animation.setImageResource(
            resources.getIdentifier("antenna_loop_fast", "drawable", activity?.packageName)
        )

        val satelliteLoop = imv_antenna_animation.drawable as AnimationDrawable
        satelliteLoop.start()
    }

    private fun showUserState() {
        setLoginStatus(SIGNED_IN)
        setAuthButton(SIGN_OUT)
        setStatusDrawable(ANTENNA_FULL)
    }

    private fun showNullUserState() {
        setLoginStatus(SIGNED_OUT)
        setAuthButton(SIGN_IN)
        setStatusDrawable(ANTENNA_EMPTY)

    }

    fun setLoginStatus(text: String) {
        lbl_login_status_display.text = text
    }

    fun setAuthButton(text: String) {
        btn_auth_attempt.text = text
    }

    fun setStatusDrawable(imageURL: String) {
        imv_antenna_animation.setImageResource(
            resources.getIdentifier(imageURL, "drawable", activity?.packageName)
        )
    }

    private fun startSignInFlow() {
        TODO("Implement requestIdToken when firebase is set up")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //TODO: Make sure that this is called after onStart()
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        var userToken: String? = null

        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

            if (account != null) userToken = account.idToken

        } catch (exception: Exception) {
            Log.d("Login", exception.message)
        }

        viewModel.handleEvent(
            LoginEvent.OnGoogleSignInResult(
                LoginResult(
                    requestCode,
                    userToken
                )
            )
        )
    }
}

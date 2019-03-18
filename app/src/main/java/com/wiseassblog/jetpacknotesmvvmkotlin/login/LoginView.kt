package com.wiseassblog.jetpacknotesmvvmkotlin.login


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.wiseassblog.jetpacknotesmvvmkotlin.R
import com.wiseassblog.jetpacknotesmvvmkotlin.common.RC_SIGN_IN
import com.wiseassblog.jetpacknotesmvvmkotlin.common.startWithFade
import com.wiseassblog.jetpacknotesmvvmkotlin.login.buildlogic.LoginInjector
import com.wiseassblog.jetpacknotesmvvmkotlin.model.LoginResult
import com.wiseassblog.jetpacknotesmvvmkotlin.note.NoteActivity
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
private const val ANTENNA_LOOP = "antenna_loop_fast"

class LoginView : Fragment() {

    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    //Create and bind to ViewModel
    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(
            this,
            LoginInjector(requireActivity().application).provideUserViewModelFactory()
        ).get(
            UserViewModel::class.java
        )

        btn_auth_attempt.setOnClickListener { viewModel.handleEvent(LoginEvent.OnAuthButtonClick) }

        imb_toolbar_back.setOnClickListener { startListActivity() }

        requireActivity().addOnBackPressedCallback(viewLifecycleOwner, OnBackPressedCallback {
            startListActivity()
            true
        })

        //start background anim
        (root_fragment_login.background as AnimationDrawable).startWithFade()

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

    private fun showErrorState(errorMessage: String?) {
        setStatusDrawable(ANTENNA_EMPTY)
        setLoginStatus(errorMessage!!)
    }

    private fun showLoadingState() {
        setStatusDrawable(ANTENNA_LOOP)
        (imv_antenna_animation.drawable as AnimationDrawable).start()
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

    private fun startListActivity() = requireActivity().startActivity(
        Intent(
            activity,
            NoteActivity::class.java
        )
    )


    private fun startSignInFlow() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
            Log.d("Login", exception.toString())
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

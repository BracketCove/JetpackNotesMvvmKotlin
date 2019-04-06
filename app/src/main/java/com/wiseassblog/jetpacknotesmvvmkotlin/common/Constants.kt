package com.wiseassblog.jetpacknotesmvvmkotlin.common

internal const val LOGIN_ERROR = "Error retrieving user."
internal const val LOADING = "Loading..."
internal const val LOGOUT_ERROR = "Error logging out user."
internal const val GET_NOTE_ERROR = "Error retrieving note."
internal const val GET_NOTES_ERROR = "Error retrieving notes."
internal const val SIGN_OUT = "SIGN OUT"
internal const val SIGN_IN = "SIGN IN"
internal const val SIGNED_IN = "Signed In"
internal const val SIGNED_OUT = "Signed Out"
internal const val ERROR_NETWORK_UNAVAILABLE = "Network Unavailable"
internal const val ERROR_AUTH = "An Error Has Occured"
internal const val RETRY = "RETRY"
internal const val ANTENNA_EMPTY = "ic_antenna_empty"
internal const val ANTENNA_FULL = "ic_antenna_full"
internal const val ANTENNA_LOOP = "antenna_loop_fast"

/**
 * This value is just a constant to denote our sign in request; It can be any int.
 * Would have been great if that was explained in the docs, I assumed at first that it had to
 * be a specific value.
 */
internal const val RC_SIGN_IN = 1337
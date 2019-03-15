package com.wiseassblog.jetpacknotesmvvmkotlin.common

internal const val LOGIN_ERROR = "Error retrieving user."
internal const val LOGOUT_ERROR = "Error logging out user."
internal const val GET_NOTE_ERROR = "Error retrieving note."
internal const val GET_NOTES_ERROR = "Error retrieving notes."

/**
 * This value is just a constant to denote our sign in request; It can be any int.
 * Would have been great if that was explained in the docs, I assumed at first that it had to
 * be a specific value.
 */
internal const val RC_SIGN_IN = 1337
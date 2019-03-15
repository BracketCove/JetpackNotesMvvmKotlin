package com.wiseassblog.jetpacknotesmvvmkotlin.model

/**
 * Wrapper class for data recieved in LoginActivity's onActivityResult()
 * function
 */
data class LoginResult(val requestCode: Int, val userToken: String?)
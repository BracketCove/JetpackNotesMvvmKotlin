package com.wiseassblog.jetpacknotesmvvmkotlin.common

import android.graphics.drawable.AnimationDrawable
import android.widget.Toast
import androidx.fragment.app.Fragment

internal fun Fragment.makeToast(value: String) {
    Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()
}

internal fun AnimationDrawable.startWithFade(){
    this.setEnterFadeDuration(1000)
    this.setExitFadeDuration(1000)
    this.start()
}
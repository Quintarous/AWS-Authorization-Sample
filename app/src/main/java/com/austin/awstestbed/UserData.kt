package com.austin.awstestbed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserData {
    private val mloggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val loggedIn: LiveData<Boolean> get() = mloggedIn

    fun setLoggedInState(state: Boolean) {
        mloggedIn.value = state
    }
}
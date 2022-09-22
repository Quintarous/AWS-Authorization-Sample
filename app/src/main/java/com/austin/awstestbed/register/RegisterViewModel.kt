package com.austin.awstestbed.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthCategoryBehavior
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.austin.awstestbed.R
import com.austin.awstestbed.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val auth: AuthCategoryBehavior): ViewModel() {

    // defining the state flow the UI will collect it's UiState from
    private val _uiState = MutableStateFlow(
        RegisterUiState(null, null, false)
    )
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()


    /**
     * Takes the username, password, and optional email the user entered and submits it to our
     * AWS backend to register (sign up) the new user.
     *
     * It looks like callback hell but it's simpler than it looks. If sign up is successful we
     * attempt to sign in the new user we just registered. And if sign in is also successful we set
     * them as signed in locally in our app (setting [UserData.loggedIn] to true), display a toast,
     * and navigate out of the registration screen. If either sign up or sign in fails we
     * simply display a toast with the error that occurred.
     */
    fun signUp(username: String, password: String, email: String) {

        // the email needs to be added to this AuthSignUpOptions object before being passed in to
        // Cognito's signUp() method
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()

        // calling sign up
        auth.signUp(username, password, options,
            { // sign in successful
                auth.signIn(username, password,
                    {
                        viewModelScope.launch(Dispatchers.Main) {
                            // logging the user in locally
                            UserData.setLoggedInState(true)
                            // emitting a new UiState to display a toast and pop the back stack
                            _uiState.emit(RegisterUiState(R.string.sign_in_successful_toast,
                                null, true)
                            )
                        }
                    },
                    { // sign in failed
                        viewModelScope.launch {
                            _uiState.emit(
                                RegisterUiState(R.string.sign_in_failed_toast,
                                    it.localizedMessage, false)
                            )
                        }
                    }
                )
            },
            { // sign up failed
                viewModelScope.launch {
                    // if the signup failed because the username was already taken, show a unique
                    // toast. Otherwise show a generic error toast
                    when (it) {
                        is AuthException.UsernameExistsException -> {
                            _uiState.emit(
                                RegisterUiState(R.string.username_already_exists,
                                    null, false)
                            )
                        }

                        else -> {
                            _uiState.emit(
                                RegisterUiState(R.string.an_error_occured,
                                    it.localizedMessage, false)
                            )
                        }
                    }
                }
            }
        )
    }


    /**
     * Emits a new basic [RegisterUiState]. [RegisterFragment] will use this exposed method to
     * report to us that it successfully displayed a toast to the user.
     *
     * We need to do this because StateFlow observers will not trigger on subsequent emissions of
     * the same object. So for example if the user gets an error then hits the submit button again.
     * We will emit the same [RegisterUiState] object through the [_uiState] StateFlow > the
     * observer won't trigger > and the user gets no error message for their second attempt.
     *
     * To solve this we expose this method to emit a "default" [RegisterUiState] allowing any
     * subsequent error emissions to be caught by the observer and displayed. Even if they're the
     * same error.
     */
    fun toastShown() {
        viewModelScope.launch {
            _uiState.emit(RegisterUiState(null, null, false))
        }
    }


    /**
     * The ui state object for the [RegisterFragment].
     *
     * Parameters:
     * - toastMessage: The resource id of the string we want to display in a toast
     * - errorMessage: The localised error string of the error that occurred. Null if no error
     * occurred.
     * - popBackStack: true if the sign in succeeded and we want to navigate away from the
     * registration screen.-
     */
    data class RegisterUiState(
        val toastMessage: Int?,
        val errorMessage: String?,
        val popBackStack: Boolean
    )


    companion object {
        fun provideFactory(auth: AuthCategoryBehavior): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(auth) as T
                }
            }
    }
}
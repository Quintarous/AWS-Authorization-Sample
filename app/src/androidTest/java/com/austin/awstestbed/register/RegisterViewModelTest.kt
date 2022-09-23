package com.austin.awstestbed.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.austin.awstestbed.R
import com.austin.awstestbed.UserData
import com.austin.awstestbed.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class RegisterViewModelTest {

    private val fakeAuth = FakeAuth() // instantiating our fake AWS Amplify dependency

    @Rule
    @JvmField
    // this rule is needed to solve the threading issue of not being able to call
    // LiveData.observeForever() on a background thread
    val instantExecutor = InstantTaskExecutorRule()

    @After
    fun cleanup() {
        fakeAuth.behavior = FakeAuth.Behavior.SUCCESS // resetting the FakeAuth class to it's default state
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userDataUpdated_UiStateEmitted_When_SignUp_Successful() { // testing an all successful scenario
        runTest(UnconfinedTestDispatcher()) {
            val viewModel = RegisterViewModel(fakeAuth) // class under test

            viewModel.signUp("username", "password", "email") // method under test

            val loggedIn = UserData.loggedIn.getOrAwaitValue()
            val uiState = viewModel.uiState.first()
            val expectedUiState = RegisterViewModel.RegisterUiState(
                R.string.sign_in_successful_toast, null, true
            )

            assert(loggedIn) // asserting the user is logged in locally
            assert(uiState == expectedUiState) // asserting the correct ui state was emitted
        }
    }
}
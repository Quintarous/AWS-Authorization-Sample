package com.austin.awstestbed.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.austin.awstestbed.R
import com.austin.awstestbed.UserData
import com.austin.awstestbed.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
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
        fakeAuth.apply {
            behavior = FakeAuth.Behavior.SUCCESS // resetting the FakeAuth class to it's default state
            latch = null
        }
    }


    /**
     * testing an all successful scenario
     */
    @ExperimentalCoroutinesApi
    @Test
    fun userDataUpdated_UiStateEmitted_When_SignUpSuccessful() {
        runTest(UnconfinedTestDispatcher()) {
            // creating our countdown latch to wait for FakeAuth to invoke one of it's callbacks
            val latch = CountDownLatch(1)
            fakeAuth.latch = latch // injecting our latch into FakeAuth
            val viewModel = RegisterViewModel(fakeAuth) // class under test

            // testing the initial state
            val initialUiState = viewModel.uiState.value
            val expectedInitialUiState =
                RegisterViewModel.RegisterUiState(null, null, false)
            assert(initialUiState == expectedInitialUiState)

            // testing the initial logged in value
            val initialLoggedIn = UserData.loggedIn.getOrAwaitValue()
            val expectedInitialLoggedIn = false
            assert(initialLoggedIn == expectedInitialLoggedIn)

            viewModel.signUp("username", "password", "email") // method under test

            if (withContext(Dispatchers.IO) {
                latch.await(2, TimeUnit.SECONDS)
            }) { // latch counted down so we can run our assertions
                val loggedIn = UserData.loggedIn.getOrAwaitValue()
                assert(loggedIn) // asserting the user is logged in locally

                val uiState = viewModel.uiState.value
                val expectedUiState = RegisterViewModel.RegisterUiState(
                    R.string.sign_in_successful_toast, null, true
                )
                assert(uiState == expectedUiState) // asserting the correct ui state was emitted
            } else { // latch timed out so fail the test
                fail("latch timed out")
            }
        }
    }


    /**
     * if username already exists
     */
    @ExperimentalCoroutinesApi
    @Test
    fun emitsCorrectUiState_When_UsernameExistsError() {
        runTest(UnconfinedTestDispatcher()) {
            // creating our countdown latch to wait for FakeAuth to invoke one of it's callbacks
            val latch = CountDownLatch(1)
            fakeAuth.latch = latch // injecting our latch into FakeAuth
            // telling the fake dependency to simulate a sign up error
            fakeAuth.behavior = FakeAuth.Behavior.USERNAME_EXISTS
            val viewModel = RegisterViewModel(fakeAuth) // class under test

            // testing the initial state
            val initialUiState = viewModel.uiState.value
            val expectedInitialUiState =
                RegisterViewModel.RegisterUiState(null, null, false)
            assert(initialUiState == expectedInitialUiState)

            viewModel.signUp("username", "password", "email") // method under test

            // waiting for the callback and performing our final assertion
            if (withContext(Dispatchers.IO) {
                latch.await(2, TimeUnit.SECONDS)
            }) { // latch counted down so we can run our assertion
                val uiState = viewModel.uiState.value
                val expectedUiState = RegisterViewModel.RegisterUiState(R.string.username_already_exists,
                    null, false)
                assert(uiState == expectedUiState)
            } else { // latch timed out so fail the test
                fail("latch timed out")
            }
        }
    }


    /**
     * sign up has a generic error
     */
    @ExperimentalCoroutinesApi
    @Test
    fun emitsCorrectUiState_When_SignUpError() {
        runTest(UnconfinedTestDispatcher()) {
            // creating our countdown latch to wait for FakeAuth to invoke one of it's callbacks
            val latch = CountDownLatch(1)
            // telling the fake dependency to simulate a sign up error
            fakeAuth.behavior = FakeAuth.Behavior.SIGNUP_FAIL
            fakeAuth.latch = latch // injecting our latch into FakeAuth
            val viewModel = RegisterViewModel(fakeAuth) // class under test

            // testing the initial state
            val initialUiState = viewModel.uiState.value
            val expectedInitialUiState =
                RegisterViewModel.RegisterUiState(null, null, false)
            assert(initialUiState == expectedInitialUiState)

            viewModel.signUp("username", "password", "email") // method under test

            // latch doesn't function properly unless it's on the IO thread
            if (withContext(Dispatchers.IO) {
                    latch.await(2, TimeUnit.SECONDS)
            }) { // latch got counted down by FakeAuth
                val uiState = viewModel.uiState.value
                val expectedUiState = RegisterViewModel.RegisterUiState(R.string.an_error_occured,
                    TEST_ERROR_MESSAGE, false)

                assert(uiState == expectedUiState)
            } else { // latch timed out
                fail("latch timed out")
            }
        }
    }


    /**
     * sign in has a generic error
     */
    @ExperimentalCoroutinesApi
    @Test
    fun emitsCorrectUiState_When_SignInError() {
        runTest(UnconfinedTestDispatcher()) {
            // creating our countdown latch to wait for FakeAuth to invoke one of it's callbacks
            val latch = CountDownLatch(1)
            fakeAuth.latch = latch // injecting our latch into FakeAuth
            // telling the fake dependency to simulate a sign up error
            fakeAuth.behavior = FakeAuth.Behavior.SIGN_IN_FAIL
            val viewModel = RegisterViewModel(fakeAuth) // class under test

            // testing the initial state
            val initialUiState = viewModel.uiState.value
            val expectedInitialUiState =
                RegisterViewModel.RegisterUiState(null, null, false)
            assert(initialUiState == expectedInitialUiState)

            viewModel.signUp("username", "password", "email") // method under test

            if (withContext(Dispatchers.IO) {
                latch.await(2, TimeUnit.SECONDS)
            }) { // latch counted down so we can run our assertion
                val uiState = viewModel.uiState.value
                val expectedUiState = RegisterViewModel.RegisterUiState(R.string.sign_in_failed_toast,
                    TEST_ERROR_MESSAGE, false)
                assert(uiState == expectedUiState)
            } else { // latch timed out so fail the test
                fail("latch timed out")
            }
        }
    }
}
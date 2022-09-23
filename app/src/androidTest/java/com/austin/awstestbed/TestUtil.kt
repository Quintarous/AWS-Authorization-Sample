package com.austin.awstestbed

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.amplifyframework.auth.AuthCodeDeliveryDetails
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthNextSignInStep
import com.amplifyframework.auth.result.step.AuthNextSignUpStep
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.auth.result.step.AuthSignUpStep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Waits for a livedata to emit a value and returns said value. Throws a [TimeoutException] if the
 * waiting time elapses. This function is stolen from a Medium article.
 *
 * https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

// returns a AuthSignUpResult for testing
fun getAuthSignUpResult(): AuthSignUpResult {
    val authCodeDeliveryDetails = AuthCodeDeliveryDetails(
        "destination", AuthCodeDeliveryDetails.DeliveryMedium.EMAIL)

    val authNextSignUpStep = AuthNextSignUpStep(
        AuthSignUpStep.DONE,
        hashMapOf(),
        authCodeDeliveryDetails
    )

    return AuthSignUpResult(
        true,
        authNextSignUpStep,
        AuthUser("userId", "username")
    )
}

// returns a AuthSignInResult for testing
fun getAuthSignInResult(): AuthSignInResult {
    val authCodeDeliveryDetails = AuthCodeDeliveryDetails(
        "destination", AuthCodeDeliveryDetails.DeliveryMedium.EMAIL)

    val authNextSignInStep = AuthNextSignInStep(
        AuthSignInStep.DONE,
        hashMapOf(),
        authCodeDeliveryDetails
    )

    return AuthSignInResult(true, authNextSignInStep)
}
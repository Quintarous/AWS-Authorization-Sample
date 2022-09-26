package com.austin.awstestbed.register

import android.app.Activity
import android.content.Intent
import com.amplifyframework.auth.*
import com.amplifyframework.auth.options.*
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.AuthUpdateAttributeResult
import com.amplifyframework.core.Action
import com.amplifyframework.core.Consumer
import com.austin.awstestbed.getAuthSignInResult
import com.austin.awstestbed.getAuthSignUpResult
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch

const val TEST_ERROR_MESSAGE = "test_error_message"

/**
 * Faking the AWS Amplify Auth plugin code for testing purposes.
 */
class FakeAuth(): AuthCategoryBehavior {

    // this determines what scenario we're going to simulate. (success, failure, etc)
    var behavior: Behavior = Behavior.SUCCESS
    // this latch will be injected by the test code. We count it down to tell the test a callback was run
    var latch: CountDownLatch? = null


    /**
     * Calls the success or failure callback based on what behavior the test class is currently
     * setup to reproduce.
     */
    override fun signUp(
        username: String,
        password: String,
        options: AuthSignUpOptions,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
        // creating a mock result and error
        val mockResult = getAuthSignUpResult()
        val exception = AuthException(TEST_ERROR_MESSAGE, "recovery suggestion")
        val usernameExistsException =
            AuthException.UsernameExistsException(Throwable("Username already exists"))

        // if we're told to fail during signup run the onError consumer
        when (behavior) {
            Behavior.SIGNUP_FAIL -> {
                onError.accept(exception)
                latch?.countDown()
            }
            Behavior.USERNAME_EXISTS -> {
                onError.accept(usernameExistsException)
                latch?.countDown()
            }
            else -> {
                onSuccess.accept(mockResult)
                latch?.countDown()
            }
        }
    }


    /**
     * Calls the success or failure callback based on what behavior the test class is currently
     * setup to reproduce.
     */
    override fun signIn(
        username: String?,
        password: String?,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        // creating a mock result and error
        val mockResult = getAuthSignInResult()
        val exception = AuthException(TEST_ERROR_MESSAGE, "recovery suggestion")

        // if we're told to fail during sign in run the onError consumer
        if (behavior == Behavior.SIGN_IN_FAIL) {
            onError.accept(exception)
            latch?.countDown()
        } else { // otherwise run the onSuccess consumer
            onSuccess.accept(mockResult)
            latch?.countDown()
        }
    }


    enum class Behavior {
        SUCCESS, SIGNUP_FAIL, USERNAME_EXISTS, SIGN_IN_FAIL
    }


// ---------------------------------UNUSED-----------------------------------
    override fun confirmSignUp(
        username: String,
        confirmationCode: String,
        options: AuthConfirmSignUpOptions,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmSignUp(
        username: String,
        confirmationCode: String,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resendSignUpCode(
        username: String,
        options: AuthResendSignUpCodeOptions,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resendSignUpCode(
        username: String,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun signIn(
        username: String?,
        password: String?,
        options: AuthSignInOptions,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmSignIn(
        confirmationCode: String,
        options: AuthConfirmSignInOptions,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmSignIn(
        confirmationCode: String,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun signInWithSocialWebUI(
        provider: AuthProvider,
        callingActivity: Activity,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun signInWithSocialWebUI(
        provider: AuthProvider,
        callingActivity: Activity,
        options: AuthWebUISignInOptions,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun signInWithWebUI(
        callingActivity: Activity,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun signInWithWebUI(
        callingActivity: Activity,
        options: AuthWebUISignInOptions,
        onSuccess: Consumer<AuthSignInResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun handleWebUISignInResponse(intent: Intent?) {
        TODO("Not yet implemented")
    }

    override fun fetchAuthSession(
        onSuccess: Consumer<AuthSession>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun rememberDevice(onSuccess: Action, onError: Consumer<AuthException>) {
        TODO("Not yet implemented")
    }

    override fun forgetDevice(onSuccess: Action, onError: Consumer<AuthException>) {
        TODO("Not yet implemented")
    }

    override fun forgetDevice(
        device: AuthDevice,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun fetchDevices(
        onSuccess: Consumer<MutableList<AuthDevice>>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(
        username: String,
        options: AuthResetPasswordOptions,
        onSuccess: Consumer<AuthResetPasswordResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(
        username: String,
        onSuccess: Consumer<AuthResetPasswordResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmResetPassword(
        newPassword: String,
        confirmationCode: String,
        options: AuthConfirmResetPasswordOptions,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmResetPassword(
        newPassword: String,
        confirmationCode: String,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun updatePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun fetchUserAttributes(
        onSuccess: Consumer<MutableList<AuthUserAttribute>>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateUserAttribute(
        attribute: AuthUserAttribute,
        options: AuthUpdateUserAttributeOptions,
        onSuccess: Consumer<AuthUpdateAttributeResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateUserAttribute(
        attribute: AuthUserAttribute,
        onSuccess: Consumer<AuthUpdateAttributeResult>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateUserAttributes(
        attributes: MutableList<AuthUserAttribute>,
        options: AuthUpdateUserAttributesOptions,
        onSuccess: Consumer<MutableMap<AuthUserAttributeKey, AuthUpdateAttributeResult>>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateUserAttributes(
        attributes: MutableList<AuthUserAttribute>,
        onSuccess: Consumer<MutableMap<AuthUserAttributeKey, AuthUpdateAttributeResult>>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resendUserAttributeConfirmationCode(
        attributeKey: AuthUserAttributeKey,
        options: AuthResendUserAttributeConfirmationCodeOptions,
        onSuccess: Consumer<AuthCodeDeliveryDetails>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun resendUserAttributeConfirmationCode(
        attributeKey: AuthUserAttributeKey,
        onSuccess: Consumer<AuthCodeDeliveryDetails>,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun confirmUserAttribute(
        attributeKey: AuthUserAttributeKey,
        confirmationCode: String,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): AuthUser {
        TODO("Not yet implemented")
    }

    override fun signOut(onSuccess: Action, onError: Consumer<AuthException>) {
        TODO("Not yet implemented")
    }

    override fun signOut(
        options: AuthSignOutOptions,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(onSuccess: Action, onError: Consumer<AuthException>) {
        TODO("Not yet implemented")
    }
}
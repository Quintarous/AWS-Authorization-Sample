package com.austin.awstestbed.register

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import com.austin.awstestbed.R
import org.junit.Test


class RegisterFragmentTest {

    /**
     * Testing the error UI that shows up when the user enters an invalid password or email.
     * We are asserting they DO NOT show up when the edit text fields are empty.
     * They DO show up when the fields have invalid input typed into them.
     * They DO NOT show up when the fields have valid input typed into them.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun showsError_When_InvalidInput() {
        runTest(UnconfinedTestDispatcher()) {
            launchFragmentInContainer<RegisterFragment>()

            // assert all the error UI is GONE
            onView(withId(R.id.password_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.password_error_label)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.email_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.email_error_label)).check(matches(withEffectiveVisibility(Visibility.GONE)))

            // enter invalid username and password
            onView(withId(R.id.password_edittext))
                .perform(click())
                .perform(typeText("passwor"))
            onView(withId(R.id.email_edittext))
                .perform(click())
                .perform(typeText("bob"))

            // assert all the error UI is VISIBLE
            onView(withId(R.id.password_error)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.password_error_label)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.email_error)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.email_error_label)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

            // enter valid username and password
            onView(withId(R.id.password_edittext))
                .perform(click())
                .perform(clearText())
                .perform(typeText("password"))
            onView(withId(R.id.email_edittext))
                .perform(click())
                .perform(clearText())
                .perform(typeText("bob@bob.com"))

            // assert all the error UI is GONE
            onView(withId(R.id.password_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.password_error_label)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.email_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.email_error_label)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}
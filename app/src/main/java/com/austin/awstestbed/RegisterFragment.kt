package com.austin.awstestbed

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthException.UsernameExistsException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.austin.awstestbed.databinding.FragmentRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// TODO write tests for all of the registration flows!
class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerSubmitButton.setOnClickListener {
            val username = binding.usernameEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val email = binding.emailEdittext.text.toString()

            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build()

            Amplify.Auth.signUp(username, password, options,
                // signup success
                {
                    // TODO sign in user, save username, show toast, navigate to first fragment
                    // TODO research security for how to handle user password strings
                    // TODO I can access username from a signup response. Figure out how to access username from a sign in response
                    Log.i("bruh", "AuthSignUpResult: $it") // AuthSignupResult has a user field

                    // signing the user in
                    Amplify.Auth.signIn(username, password,
                        {
                            lifecycleScope.launch(Dispatchers.Main) {
                                UserData.setLoggedInState(true) // setting the user to logged in
                                // making a toast to confirm the user signed in
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.sign_in_successful_toast),
                                    Toast.LENGTH_LONG
                                ).show()

                                findNavController().popBackStack() // navigating back to the first fragment
                            }
                        },
                        { exception ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.an_error_occured, exception.localizedMessage),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                },

                // signup failure
                { authException ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        // show a toast based on the exception type
                        when (authException) {
                            is UsernameExistsException -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.username_already_exists),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.an_error_occured, authException.localizedMessage),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            )
        }

        // toggling the visibility of the password error icon and label
        binding.passwordEdittext.addTextChangedListener {
            // if the password is not long enough show an error
            if (it.toString().length in 1..7) {
                binding.passwordError.visibility = View.VISIBLE
                binding.passwordErrorLabel.visibility = View.VISIBLE
            } else { // else hide the error UI
                binding.passwordError.visibility = View.GONE
                binding.passwordErrorLabel.visibility = View.GONE
            }
        }

        // toggling the visibility of the email error icon and label
        binding.emailEdittext.addTextChangedListener {
            val email = it.toString().trim() // trimming to eliminate whitespace
            // if the email is not empty and doesn't match email format show an error
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailError.visibility = View.VISIBLE
                binding.emailErrorLabel.visibility = View.VISIBLE
            } else { // else hide the error UI
                binding.emailError.visibility = View.GONE
                binding.emailErrorLabel.visibility = View.GONE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
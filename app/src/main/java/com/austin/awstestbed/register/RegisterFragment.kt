package com.austin.awstestbed.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amplifyframework.core.Amplify
import com.austin.awstestbed.databinding.FragmentRegisterBinding
import com.austin.awstestbed.register.RegisterViewModel.RegisterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// TODO research security for how to handle user password strings
class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!

    private val viewModel: RegisterViewModel = RegisterViewModel.provideFactory(Amplify.Auth)
        .create(RegisterViewModel::class.java)


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

        /**
         * Subscribing to the [RegisterUiState] StateFlow from the ViewModel. Here we display a toast
         * if needed and pop the back stack when we're told to by the ViewModel. See
         * [RegisterViewModel.toastShown] for reasoning behind why we're reporting if a toast was
         * displayed successfully.
         */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                if (state.toastMessage != null) { // if we need to display a toast
                    // retrieve the toast string from resources
                    val toastString = if (state.errorMessage != null) { // if there's an error message
                        getString(state.toastMessage, state.errorMessage) // insert it into the string-
                    } else {
                        getString(state.toastMessage) // otherwise just get the string as normal
                    }

                    Toast.makeText(requireContext(), toastString, Toast.LENGTH_LONG).show()

                    // if a toast was shown and we're not navigating away, report it to the ViewModel
                    if (!state.popBackStack) {
                        viewModel.toastShown()
                    }
                }

                // pop the back stack when we're told to
                if (state.popBackStack) {
                    findNavController().popBackStack()
                }
            }
        }

        /**
         * Cache a snapshot of the username, password, and email entered. Then call
         * [RegisterViewModel]'s signUp() method.
         */
        binding.registerSubmitButton.setOnClickListener {
            // caching a snapshot of the entered user credentials
            val username = binding.usernameEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val email = binding.emailEdittext.text.toString()

            viewModel.signUp(username, password, email)
        }

        // toggling the visibility of the password error icon and label
        binding.passwordEdittext.addTextChangedListener {
            // if the password is not at least 8 characters show an error
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
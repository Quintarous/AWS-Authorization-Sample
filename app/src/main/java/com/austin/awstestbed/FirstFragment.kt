package com.austin.awstestbed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.austin.awstestbed.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
// TODO read from cognito if the user has already signed in and sign in automatically if they have
/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // updating the UI based on if the user is logged in or not
        UserData.loggedIn.observe(viewLifecycleOwner) {
            if (it) { // if logged in
                // set the text view to the logged in string
                binding.loggedInTextview.text = getString(R.string.logged_in)

                // set all the button to either visible or gone as appropriate
                binding.signInButton.visibility = View.GONE
                binding.registerButton.visibility = View.GONE
                binding.signOutButton.visibility = View.VISIBLE
            } else {
                // set the text view to the logged out string
                binding.loggedInTextview.text = getString(R.string.logged_out)

                // set all the button to either visible or gone as appropriate
                binding.signInButton.visibility = View.VISIBLE
                binding.registerButton.visibility = View.VISIBLE
                binding.signOutButton.visibility = View.GONE
            }
        }

        // navigate to the register fragment when the register button is clicked
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_registerFragment)
        }

        // launch the web hosted sign in when the sign in button is clicked
        binding.signInButton.setOnClickListener {
            Amplify.Auth.signInWithWebUI(requireActivity(),
                // set the logged in state in the app on a successful login
                { // on success
                    lifecycleScope.launch(Dispatchers.Main) {
                        UserData.setLoggedInState(true)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sign_in_successful_toast),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { // on failure
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sign_in_failed_toast, it.localizedMessage),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        // setting the on click listener for the sign out button
        binding.signOutButton.setOnClickListener {
            Amplify.Auth.signOut( // sign the user out
                { // on success
                    lifecycleScope.launch(Dispatchers.Main) {
                        UserData.setLoggedInState(false)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sign_out_successful_toast),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { // on error
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sign_out_error_toast, it.localizedMessage),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
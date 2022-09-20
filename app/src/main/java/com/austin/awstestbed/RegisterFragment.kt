package com.austin.awstestbed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.austin.awstestbed.databinding.FragmentRegisterBinding

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
            Log.i("bruh", "submit button pressed")

//            val options = AuthSignUpOptions.builder()
//                .userAttribute(AuthUserAttributeKey.email(), "my@email.com")
//                .build()
//            Amplify.Auth.signUp("username2", "Password123", options,
//                { Log.i("bruh", "Sign up succeeded: $it") },
//                { Log.e ("bruh", "Sign up failed", it) }
//            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
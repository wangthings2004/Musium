package com.jcxdc.musium.screen

import DatabaseViewModelFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentLoginBinding
import com.jcxdc.musium.db.UserDao
import com.jcxdc.musium.db.UserDatabase
import com.jcxdc.musium.repository.DatabaseRepository
import com.jcxdc.musium.viewmodel.DatabaseViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sử dụng DataBindingUtil để inflate layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // Initialize UserDao and DatabaseRepository
        val userDao: UserDao = UserDatabase.getInstance(requireContext()).userDao()
        val repository = DatabaseRepository(userDao)

        // Initialize ViewModel using the factory
        val viewModelFactory = DatabaseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DatabaseViewModel::class.java)

        // Set up login button click listener
        binding.btnLogin.setOnClickListener {
            if (validateLogin()) {
                val username = binding.edtUserName.text.toString().trim()
                val password = binding.edtPass.text.toString().trim()

                // Call ViewModel to login the user
                viewModel.loginUser(username, password) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                        // Navigate to the home screen
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    private fun validateLogin(): Boolean {
        val username = binding.edtUserName.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()

        return when {
            TextUtils.isEmpty(username) -> {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}

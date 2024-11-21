package com.jcxdc.musium.ui.screen

import DatabaseViewModelFactory
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentRegisterBinding
import com.jcxdc.musium.db.UserDao
import com.jcxdc.musium.db.UserDatabase
import com.jcxdc.musium.model.repository.DatabaseRepository
import com.jcxdc.musium.ui.viewmodel.DatabaseViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        val userDao: UserDao = UserDatabase.getInstance(requireContext()).userDao()
        val repository = DatabaseRepository(userDao)

        val viewModelFactory = DatabaseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DatabaseViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            if (validateUsername() && validatePassword() && validateConfirmPassword() && validateEmail()) {
                val username = binding.edtUserName.text.toString().trim()
                val password = binding.edtPass.text.toString().trim()
                val email = binding.edtMail.text.toString().trim()


                viewModel.registerUser(username, password,email) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun validateUsername(): Boolean {
        val username = binding.edtUserName.text.toString().trim()
        return if (TextUtils.isEmpty(username)) {
            binding.tvUserNameWarning.text = "Username cannot be empty"
            binding.tvUserNameWarning.visibility = View.VISIBLE
            false
        } else if (username.length < 4) {
            binding.tvUserNameWarning.text = "Username must be at least 4 characters"
            binding.tvUserNameWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvUserNameWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.edtPass.text.toString().trim()
        return if (TextUtils.isEmpty(password)) {
            binding.tvPassWarning.text = "Password cannot be empty"
            binding.tvPassWarning.visibility = View.VISIBLE
            false
        } else if (password.length < 6) {
            binding.tvPassWarning.text = "Password must be at least 6 characters"
            binding.tvPassWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvPassWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val confirmPassword = binding.edtCP.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()
        return if (confirmPassword != password) {
            binding.tvCPassWarning.text = "Passwords do not match"
            binding.tvCPassWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvCPassWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.edtMail.text.toString().trim()
        return if (TextUtils.isEmpty(email)) {
            binding.tvEmailWarning.text = "Email cannot be empty"
            binding.tvEmailWarning.visibility = View.VISIBLE
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvEmailWarning.text = "Invalid email format"
            binding.tvEmailWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvEmailWarning.visibility = View.INVISIBLE
            true
        }
    }
}

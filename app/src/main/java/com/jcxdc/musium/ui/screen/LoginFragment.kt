package com.jcxdc.musium.ui.screen

import DatabaseViewModelFactory
import android.content.Context
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
import com.jcxdc.musium.model.repository.DatabaseRepository
import com.jcxdc.musium.ui.viewmodel.DatabaseViewModel

class LoginFragment : Fragment() {
    var saveText = ""
    var savePassword = ""
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        showDataFromSharePreferences()
        val userDao: UserDao = UserDatabase.getInstance(requireContext()).userDao()
        val repository = DatabaseRepository(userDao)

        val viewModelFactory = DatabaseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DatabaseViewModel::class.java)
        binding.txtSignup.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener {
            if (validateLogin()) {
                val username = binding.edtUserName.text.toString().trim()
                val password = binding.edtPass.text.toString().trim()
                saveData()
                viewModel.loginUser(username, password) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

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
        private fun showDataFromSharePreferences() {
        var sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)?: return
        val checkSignIn = sharedPreferences?.getBoolean("LogIn",false)?:false
        val savedText = sharedPreferences?.getString("SaveText",saveText)
        val savedPassword = sharedPreferences?.getString("SavePassword",savePassword)
        val checkBox = sharedPreferences?.getBoolean("CheckBox",false)?:false
        val isChecked = checkBox && checkSignIn
        binding.chkbRemember.isChecked = isChecked
        if (isChecked) {
            binding.edtUserName.setText(savedText)
            binding.edtPass.setText(savedPassword)
        } else {
            binding.edtUserName.setText("")
            binding.edtPass.setText("")
        }
    }

    private fun saveData() {
        saveText = binding.edtUserName.text.toString()
        savePassword = binding.edtPass.text.toString()
        var sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreferences?.edit()) {
            this?.putBoolean("LogIn", true)
            if (binding.chkbRemember.isChecked) {
                this?.putString("SaveText", saveText)
                this?.putString("SavePassword", savePassword)
            }
            this?.putBoolean("CheckBox", binding.chkbRemember.isChecked)
            this?.apply()
        }
    }
}

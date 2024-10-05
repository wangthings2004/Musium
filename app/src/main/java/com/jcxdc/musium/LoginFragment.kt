package com.jcxdc.musium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jcxdc.musium.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sử dụng DataBindingUtil để inflate layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // Trả về view từ binding
        return binding.root
    }
}

package com.richard.pixlog.ui.screen.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.richard.pixlog.MainActivity
import com.richard.pixlog.data.repository.Result
import com.richard.pixlog.databinding.ActivityLoginBinding
import com.richard.pixlog.ui.screen.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginFactory = LoginViewModelFactory.getInstance(this)
        val loginViewModel: LoginViewModel by viewModels{
            loginFactory
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Setup EditTextComponent
        setupEditTextComponents()

        binding.btnLogin.setOnClickListener {
            val email = binding.textInputEmail.text.toString()
            val password = binding.textInputPassword.text.toString()
            loginViewModel.postLogin(email, password)
        }

        loginViewModel.loginResult.observe(this){
            if(it != null){
                when(it){
                    is Result.Loading -> {
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progressLoading.visibility = View.INVISIBLE
                        Toast.makeText(this, "Login Error ${it.error}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        binding.progressLoading.visibility = View.INVISIBLE
                        Toast.makeText(this, "Login Success ${it.data.message}", Toast.LENGTH_SHORT).show()
                        successLogin()
                    }
                }
            }
        }

        binding.tvQuestion2.setOnClickListener {
            val intent  = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupEditTextComponents() {
        // Setup email input
        binding.textInputEmail.apply {
            setHint("email")
            setInputType(android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        }
        
        // Setup password input
        binding.textInputPassword.apply {
            setHint("password")
            setInputType(android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)
        }
    }

    private  fun successLogin (){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
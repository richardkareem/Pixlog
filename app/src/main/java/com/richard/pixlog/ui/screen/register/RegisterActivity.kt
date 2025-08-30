package com.richard.pixlog.ui.screen.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.richard.pixlog.databinding.ActivityRegisterBinding
import com.richard.pixlog.ui.screen.login.LoginActivity
import com.richard.pixlog.data.repository.Result
class RegisterActivity : AppCompatActivity() {
    private var _binding : ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    val registerFactoru = RegisterViewModelFactory.getInstance(this)
    private val registerViewModel: RegisterViewModel by viewModels{
        registerFactoru
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val tvEmail = binding.tvEmail
        val tvPassword = binding.tvPassword
        val tvUsername = binding.tvUsername
        val btnRegister = binding.btnRegister


        btnRegister.setOnClickListener {
            if(tvEmail.text.toString().isEmpty() || tvPassword.text.toString().isEmpty() || tvUsername.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }else{
                registerViewModel.postRegister(
                    tvUsername.text.toString(),
                    tvEmail.text.toString(),
                    tvPassword.text.toString()
                )
            }
        }

        binding.tvQuestion2.setOnClickListener {
            toLoginActivity()
        }



      registerViewModel.resultRegister.observe(this) { response ->
          if(response != null){
              when(response){
                  is  Result.Loading-> {
                      binding.progressLoading.visibility = View.VISIBLE
                  }
                  is Result.Error -> {
                      binding.progressLoading.visibility = View.INVISIBLE
                      Toast.makeText(this, "Register Error ${response.error}", Toast.LENGTH_SHORT).show()
                  }
                  is Result.Success -> {
                      binding.progressLoading.visibility = View.INVISIBLE
                      Toast.makeText(this, "Register Success ${response.data.message}", Toast.LENGTH_SHORT).show()
                      toLoginActivity()
                  }
              }
          }
      }


    }

    private fun toLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
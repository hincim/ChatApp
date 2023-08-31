package com.example.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignInBinding
import com.example.myapplication.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners(){
        binding.textSignIn.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        binding.buttonSignUp.setOnClickListener {
            if (isValidSignUpDetails()){
                signUp()
            }
        }
    }

    private fun signUp(){

    }

    private fun showToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignUpDetails(): Boolean{
        if (encodedImage == null){
            showToast("Select profile image")
            return false
        }else if (binding.inputName.text.toString().trim().isEmpty()){
            showToast("Enter name")
            return false
        }else if (binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Enter email")
            return false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter valid email")
            return false
        }else if (binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter password")
            return false
        }else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()){
            showToast("Confirm your password")
            return false
        }else if (!binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString())){
            showToast("Password & confirm password must be same")
            return false
        }else{
            return true
        }
    }

    private fun loading(){

    }
}












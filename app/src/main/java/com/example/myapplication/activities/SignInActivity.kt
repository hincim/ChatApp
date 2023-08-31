package com.example.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignInBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners(){
        binding.textCreateNewAccount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        })

        binding.buttonSignIn.setOnClickListener { v -> addDataToFirestore() }
    }

    private fun addDataToFirestore(){
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "first_name" to "hinfo",
            "last_name" to "craft"
        )
        database.collection("users")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this,"Data Inserted",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }
    }
}
















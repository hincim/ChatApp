package com.example.myapplication.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        loadUserDetails()
        getToken()
        setListeners()
    }

    private fun setListeners(){
        binding.imageSignOut.setOnClickListener {
            signOut()
        }

        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(this,UsersActivity::class.java))
        }
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = android.util.Base64.decode(
            preferenceManager.getString(Constants.KEY_IMAGE),
            android.util.Base64.DEFAULT
        )
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }
    private fun showToast(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    private fun updateToken(token: String){
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)
        )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener {
                showToast("Unable to update token")
            }
    }

    private fun signOut(){
        showToast("Signing out...")
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)
        )
        val updates = mapOf(
            Constants.KEY_FCM_TOKEN to FieldValue.delete()
        )
        documentReference.update(updates)
            .addOnSuccessListener{
                preferenceManager.clear()
                startActivity(Intent(this,SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                showToast("Unable to sign out")
            }
    }
}














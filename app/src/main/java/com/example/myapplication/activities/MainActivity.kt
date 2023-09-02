package com.example.myapplication.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.adapters.RecentConversationsAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.listeners.ConversionListener
import com.example.myapplication.models.ChatMessage
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Collections
import java.util.Date

class MainActivity : BaseActivity(), ConversionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: MutableList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        init()
        loadUserDetails()
        getToken()
        setListeners()
        listenConversations()
    }

    private fun init(){
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
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

    private fun listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId = documentChange.document
                            .getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document
                            .getString(Constants.KEY_RECEIVER_ID)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                            chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        }else{
                            chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        conversations.add(chatMessage)
                    }else if (documentChange.type == DocumentChange.Type.MODIFIED){
                        for (i in 0 until conversations.size) {
                            val senderId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                            val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            if (conversations[i].senderId.equals(senderId) && conversations[i].receiverId.equals(receiverId)){
                                conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversations[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                break
                            }
                        }
                    }
                }
                Collections.sort<ChatMessage>(
                    conversations,
                    Comparator.comparing<ChatMessage, Date>(ChatMessage::dateObject)
                )
                conversationsAdapter.notifyDataSetChanged()
                binding.conversationsRecyclerView.smoothScrollToPosition(0)
                binding.conversationsRecyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
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

    override fun onConversionClicked(user: User) {
        startActivity(Intent(this,ChatActivity::class.java).putExtra(Constants.KEY_USER,user))
    }
}














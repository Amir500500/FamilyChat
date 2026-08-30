package com.family.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.family.chat.adapters.MessageAdapter
import com.family.chat.models.Message
import com.family.chat.utils.AuthHelper
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var chatId: String
    private lateinit var myUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        myUid = AuthHelper.currentUserId() ?: run { finish(); return }

        val otherUid = intent.getStringExtra("otherUid") ?: run { finish(); return }
        val otherName = intent.getStringExtra("otherName") ?: ""
        chatId = AuthHelper.buildChatId(myUid, otherUid)

        val tvChatTitle: TextView = findViewById(R.id.tvChatTitle)
        tvChatTitle.text = otherName

        recycler = findViewById(R.id.recyclerMessages)
        adapter = MessageAdapter(mutableListOf(), myUid)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val etMessage: EditText = findViewById(R.id.etMessage)
        val btnSend: Button = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.setText("")
            }
        }

        listenForMessages()
    }

    private fun sendMessage(text: String) {
        val message = Message(
            senderId = myUid,
            senderName = "",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        AuthHelper.firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
    }

    private fun listenForMessages() {
        AuthHelper.firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(Message::class.java)
                    adapter.setMessages(list)
                    if (list.isNotEmpty()) {
                        recycler.scrollToPosition(list.size - 1)
                    }
                }
            }
    }
}

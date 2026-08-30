package com.family.chat

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.family.chat.adapters.FamilyAdapter
import com.family.chat.models.User
import com.family.chat.utils.AuthHelper

class FamilyListActivity : AppCompatActivity() {

    private lateinit var adapter: FamilyAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_list)

        val recycler: RecyclerView = findViewById(R.id.recyclerFamily)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        val tvLogout: TextView = findViewById(R.id.tvLogout)

        adapter = FamilyAdapter(mutableListOf()) { user ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("otherUid", user.uid)
            intent.putExtra("otherName", user.displayName)
            startActivity(intent)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        swipeRefresh.setOnRefreshListener { loadFamilyMembers() }

        tvLogout.setOnClickListener {
            AuthHelper.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadFamilyMembers()
    }

    private fun loadFamilyMembers() {
        swipeRefresh.isRefreshing = true
        val myUid = AuthHelper.currentUserId()

        AuthHelper.firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(User::class.java).filter { it.uid != myUid }
                adapter.updateList(list)
                swipeRefresh.isRefreshing = false
            }
            .addOnFailureListener {
                swipeRefresh.isRefreshing = false
            }
    }
}

package com.family.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.family.chat.R
import com.family.chat.models.User

class FamilyAdapter(
    private val members: MutableList<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<FamilyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_family_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = members[position]
        holder.tvName.text = user.displayName
        holder.tvId.text = "@${user.uniqueId}"
        holder.tvAvatar.text = if (user.displayName.isNotEmpty()) user.displayName.first().toString() else "؟"
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = members.size

    fun updateList(newList: List<User>) {
        members.clear()
        members.addAll(newList)
        notifyDataSetChanged()
    }
}

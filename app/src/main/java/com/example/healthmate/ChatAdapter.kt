package com.example.healthmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R  // Replace with your actual R package name

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            val userView = inflater.inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(userView)
        } else {
            val botView = inflater.inflate(R.layout.item_bot_message, parent, false)
            BotViewHolder(botView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position].message
        when (holder) {
            is UserViewHolder -> holder.bind(message)
            is BotViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isBot) 1 else 0
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userMessageTextView: TextView = itemView.findViewById(R.id.userMessageTextView)

        fun bind(message: String) {
            userMessageTextView.text = message
        }
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val botMessageTextView: TextView = itemView.findViewById(R.id.botMessageTextView)

        fun bind(message: String) {
            botMessageTextView.text = message
        }
    }
}

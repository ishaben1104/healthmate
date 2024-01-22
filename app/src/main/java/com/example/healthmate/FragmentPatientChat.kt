package com.example.healthmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R

class FragmentPatientChat : Fragment() {

    private val chatMessages = mutableListOf(
        ChatMessage("Hello, I'm auto bot. Ask me any query related to the application", true)
    )

    private val randomBotResponses = listOf(
        "I'm just a dummy bot. I don't have real responses!",
        "Hope I can help you further with basic!",
        "I will forward this query further!",
        "Ask me something interesting!",
        "I'm here to help. What can I assist you with?",
        "Sorry, I'm not capable of understanding complex questions.",
        "For further query, mail us with in details on contact@healthmate.com."
    )

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_patient_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)

        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter

        // Handle send button click
        sendButton.setOnClickListener {
            val userMessage = messageEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                // Add user message to the list
                chatMessages.add(ChatMessage(userMessage, false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)

                // Simulate random bot response
                val randomBotResponse = getRandomBotResponse()
                chatMessages.add(ChatMessage(randomBotResponse, true))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)

                // Scroll to the bottom
                recyclerView.scrollToPosition(chatMessages.size - 1)

                // Clear the input field
                messageEditText.text.clear()

                // Log the updated data for debugging
                for (message in chatMessages) {
                    Log.d("ChatFragment", "Message: ${message.message}, isBot: ${message.isBot}")
                }
            }
        }
    }

    private fun getRandomBotResponse(): String {
        val randomIndex = (0 until randomBotResponses.size).random()
        return randomBotResponses[randomIndex]
    }

}
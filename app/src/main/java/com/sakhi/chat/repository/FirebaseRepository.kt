package com.sakhi.chat.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sakhi.chat.model.BotType
import com.sakhi.chat.model.Conversation
import com.sakhi.chat.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Authentication
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // Conversation Management
    suspend fun createConversation(botType: BotType): Result<String> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val conversationId = UUID.randomUUID().toString()

            val conversationData = hashMapOf(
                "id" to conversationId,
                "botType" to botType.name,
                "title" to "",
                "lastMessage" to "",
                "lastMessageTime" to System.currentTimeMillis(),
                "messageCount" to 0,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .set(conversationData)
                .await()

            Result.success(conversationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getConversationsForBot(botType: BotType): Flow<List<Conversation>> = callbackFlow {
        val userId = currentUser?.uid

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .whereEqualTo("botType", botType.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val conversations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Conversation(
                            id = doc.getString("id") ?: "",
                            botType = BotType.valueOf(doc.getString("botType") ?: ""),
                            title = doc.getString("title") ?: "",
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTime = doc.getLong("lastMessageTime") ?: 0L,
                            messageCount = doc.getLong("messageCount")?.toInt() ?: 0,
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(conversations)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun deleteConversation(conversationId: String): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            // Delete all messages in the conversation
            val messages = firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .collection("messages")
                .get()
                .await()

            messages.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            // Delete the conversation
            firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Message Management
    suspend fun saveMessage(message: Message, conversationId: String): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val messageData = hashMapOf(
                "id" to message.id,
                "text" to message.text,
                "isFromUser" to message.isFromUser,
                "timestamp" to message.timestamp,
                "conversationId" to conversationId,
                "botType" to message.botType?.name
            )

            // Save message
            firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document(message.id.toString())
                .set(messageData)
                .await()

            // Update conversation metadata
            updateConversationMetadata(conversationId, message)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateConversationMetadata(conversationId: String, lastMessage: Message) {
        try {
            val userId = currentUser?.uid ?: return
            val conversationRef = firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)

            val conversation = conversationRef.get().await()
            val currentCount = conversation.getLong("messageCount")?.toInt() ?: 0

            // Generate title from first user message if not set
            var title = conversation.getString("title") ?: ""
            if (title.isEmpty() && lastMessage.isFromUser) {
                title = lastMessage.text.take(50)
                if (lastMessage.text.length > 50) title += "..."
            }

            conversationRef.update(
                mapOf(
                    "lastMessage" to lastMessage.text.take(100),
                    "lastMessageTime" to lastMessage.timestamp,
                    "messageCount" to (currentCount + 1),
                    "title" to title
                )
            ).await()
        } catch (e: Exception) {
            // Silent fail - metadata update is not critical
            e.printStackTrace()
        }
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> = callbackFlow {
        val userId = currentUser?.uid

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Message(
                            id = doc.getLong("id") ?: 0L,
                            text = doc.getString("text") ?: "",
                            isFromUser = doc.getBoolean("isFromUser") ?: false,
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            conversationId = doc.getString("conversationId") ?: "",
                            botType = doc.getString("botType")?.let { BotType.valueOf(it) }
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun clearConversation(conversationId: String): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val messages = firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .collection("messages")
                .get()
                .await()

            messages.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            // Reset conversation metadata
            firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
                .update(
                    mapOf(
                        "lastMessage" to "",
                        "messageCount" to 0,
                        "title" to ""
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

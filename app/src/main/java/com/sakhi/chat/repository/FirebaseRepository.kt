package com.sakhi.chat.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sakhi.chat.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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

    // Firestore - Chat History
    suspend fun saveMessage(message: Message): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val messageData = hashMapOf(
                "id" to message.id,
                "text" to message.text,
                "isFromUser" to message.isFromUser,
                "timestamp" to message.timestamp
            )

            firestore.collection("users")
                .document(userId)
                .collection("messages")
                .document(message.id.toString())
                .set(messageData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMessagesFlow(): Flow<List<Message>> = callbackFlow {
        val userId = currentUser?.uid

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users")
            .document(userId)
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
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun clearAllMessages(): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val messages = firestore.collection("users")
                .document(userId)
                .collection("messages")
                .get()
                .await()

            messages.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

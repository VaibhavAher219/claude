package com.sakhi.chat.database

import android.content.Context
import androidx.room.*
import com.sakhi.chat.model.BotType
import kotlinx.coroutines.flow.Flow

// Room Entities
@Entity(tableName = "cached_messages")
data class CachedMessage(
    @PrimaryKey val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val conversationId: String,
    val botType: String,
    val isSynced: Boolean = false
)

@Entity(tableName = "cached_conversations")
data class CachedConversation(
    @PrimaryKey val id: String,
    val botType: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val messageCount: Int,
    val createdAt: Long,
    val isSynced: Boolean = false
)

// Type Converters
class Converters {
    @TypeConverter
    fun fromBotType(value: BotType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toBotType(value: String?): BotType? {
        return value?.let { BotType.valueOf(it) }
    }
}

// DAOs
@Dao
interface MessageDao {
    @Query("SELECT * FROM cached_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: String): Flow<List<CachedMessage>>

    @Query("SELECT * FROM cached_messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<CachedMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CachedMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<CachedMessage>)

    @Update
    suspend fun updateMessage(message: CachedMessage)

    @Query("UPDATE cached_messages SET isSynced = 1 WHERE id = :messageId")
    suspend fun markAsSynced(messageId: Long)

    @Query("DELETE FROM cached_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)

    @Query("SELECT COUNT(*) FROM cached_messages")
    suspend fun getMessageCount(): Int
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM cached_conversations WHERE botType = :botType ORDER BY lastMessageTime DESC")
    fun getConversationsByBot(botType: String): Flow<List<CachedConversation>>

    @Query("SELECT * FROM cached_conversations WHERE isSynced = 0")
    suspend fun getUnsyncedConversations(): List<CachedConversation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: CachedConversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<CachedConversation>)

    @Update
    suspend fun updateConversation(conversation: CachedConversation)

    @Query("UPDATE cached_conversations SET isSynced = 1 WHERE id = :conversationId")
    suspend fun markAsSynced(conversationId: String)

    @Query("DELETE FROM cached_conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("SELECT * FROM cached_conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): CachedConversation?
}

// Database
@Database(
    entities = [CachedMessage::class, CachedConversation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SakhiDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile
        private var INSTANCE: SakhiDatabase? = null

        fun getDatabase(context: Context): SakhiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SakhiDatabase::class.java,
                    "sakhi_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.graffzone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY username")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Insert
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun observeFeed(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PostEntity?

    @Query("SELECT * FROM posts WHERE tags LIKE '%' || :query || '%' OR caption LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun observeSearch(query: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE lat IS NOT NULL AND lon IS NOT NULL AND isPublic = 1 ORDER BY createdAt DESC")
    fun observeWithLocation(): Flow<List<PostEntity>>

    @Insert
    suspend fun insert(post: PostEntity): Long

    @Update
    suspend fun update(post: PostEntity)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun observeForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert
    suspend fun insert(comment: CommentEntity): Long
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE isPublic = 1 ORDER BY startTime ASC")
    fun observeEvents(): Flow<List<EventEntity>>

    @Insert
    suspend fun insert(event: EventEntity): Long
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY lastMessageAt DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Insert
    suspend fun insert(conv: ConversationEntity): Long

    @Update
    suspend fun update(conv: ConversationEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(msg: MessageEntity): Long
}

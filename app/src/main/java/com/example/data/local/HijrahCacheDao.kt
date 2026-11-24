package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HijrahCacheDao {

    // --- User Profile Cache ---
    @Query("SELECT * FROM cached_user_profiles WHERE userId = :userId LIMIT 1")
    fun observeUserProfile(userId: String): Flow<CachedUserProfileEntity?>

    @Query("SELECT * FROM cached_user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfileOnce(userId: String): CachedUserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: CachedUserProfileEntity)

    // --- Checklist Cache ---
    @Query("SELECT * FROM cached_checklist_items WHERE userId = :userId ORDER BY sortOrder ASC")
    fun observeChecklist(userId: String): Flow<List<CachedChecklistEntity>>

    @Query("SELECT * FROM cached_checklist_items WHERE userId = :userId ORDER BY sortOrder ASC")
    suspend fun getChecklistOnce(userId: String): List<CachedChecklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(items: List<CachedChecklistEntity>)

    @Query("DELETE FROM cached_checklist_items WHERE userId = :userId")
    suspend fun deleteChecklistForUser(userId: String)

    @Transaction
    suspend fun replaceChecklist(userId: String, items: List<CachedChecklistEntity>) {
        deleteChecklistForUser(userId)
        if (items.isNotEmpty()) insertChecklist(items)
    }

    // --- Document Cache ---
    @Query("SELECT * FROM cached_document_items WHERE userId = :userId")
    fun observeDocuments(userId: String): Flow<List<CachedDocumentEntity>>

    @Query("SELECT * FROM cached_document_items WHERE userId = :userId")
    suspend fun getDocumentsOnce(userId: String): List<CachedDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(items: List<CachedDocumentEntity>)

    @Query("DELETE FROM cached_document_items WHERE userId = :userId")
    suspend fun deleteDocumentsForUser(userId: String)

    @Transaction
    suspend fun replaceDocuments(userId: String, items: List<CachedDocumentEntity>) {
        deleteDocumentsForUser(userId)
        if (items.isNotEmpty()) insertDocuments(items)
    }

    // --- Budget Cache ---
    @Query("SELECT * FROM cached_budget_items WHERE userId = :userId")
    fun observeBudgetItems(userId: String): Flow<List<CachedBudgetEntity>>

    @Query("SELECT * FROM cached_budget_items WHERE userId = :userId")
    suspend fun getBudgetItemsOnce(userId: String): List<CachedBudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItems(items: List<CachedBudgetEntity>)

    @Query("DELETE FROM cached_budget_items WHERE userId = :userId")
    suspend fun deleteBudgetItemsForUser(userId: String)

    @Transaction
    suspend fun replaceBudgetItems(userId: String, items: List<CachedBudgetEntity>) {
        deleteBudgetItemsForUser(userId)
        if (items.isNotEmpty()) insertBudgetItems(items)
    }

    // --- Favorites Cache ---
    @Query("SELECT * FROM cached_favorites WHERE userId = :userId")
    fun observeFavorites(userId: String): Flow<List<CachedFavoriteEntity>>

    @Query("SELECT * FROM cached_favorites WHERE userId = :userId")
    suspend fun getFavoritesOnce(userId: String): List<CachedFavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(items: List<CachedFavoriteEntity>)

    @Query("DELETE FROM cached_favorites WHERE userId = :userId")
    suspend fun deleteFavoritesForUser(userId: String)

    @Transaction
    suspend fun replaceFavorites(userId: String, items: List<CachedFavoriteEntity>) {
        deleteFavoritesForUser(userId)
        if (items.isNotEmpty()) insertFavorites(items)
    }

    // --- Forum Discussions & Replies Cache ---
    @Query("SELECT * FROM cached_forum_posts ORDER BY createdAtMillis DESC")
    fun observeForumPosts(): Flow<List<CachedForumPostEntity>>

    @Query("SELECT * FROM cached_forum_posts ORDER BY createdAtMillis DESC")
    suspend fun getForumPostsOnce(): List<CachedForumPostEntity>

    @Query("SELECT id FROM cached_forum_posts WHERE savedForOffline = 1")
    fun observeSavedOfflinePostIds(): Flow<List<String>>

    @Query("SELECT id FROM cached_forum_posts WHERE savedForOffline = 1")
    suspend fun getSavedOfflinePostIdsOnce(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertForumPosts(posts: List<CachedForumPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertForumPost(post: CachedForumPostEntity)

    @Query("UPDATE cached_forum_posts SET savedForOffline = :saved WHERE id = :postId")
    suspend fun setForumPostSavedOffline(postId: String, saved: Boolean)

    @Query("DELETE FROM cached_forum_posts WHERE savedForOffline = 0")
    suspend fun deleteUnsavedForumPosts()

    @Transaction
    suspend fun refreshForumFeedPreservingPinned(remotePosts: List<CachedForumPostEntity>) {
        val pinnedIds = getSavedOfflinePostIdsOnce().toSet()
        deleteUnsavedForumPosts()
        val merged = remotePosts.map { post ->
            if (post.id in pinnedIds) post.copy(savedForOffline = true) else post
        }
        if (merged.isNotEmpty()) upsertForumPosts(merged)
    }

    @Query("SELECT * FROM cached_forum_replies WHERE postId = :postId ORDER BY createdAtMillis ASC")
    fun observeForumReplies(postId: String): Flow<List<CachedForumReplyEntity>>

    @Query("SELECT * FROM cached_forum_replies WHERE postId = :postId ORDER BY createdAtMillis ASC")
    suspend fun getForumRepliesOnce(postId: String): List<CachedForumReplyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumReplies(replies: List<CachedForumReplyEntity>)

    @Query("DELETE FROM cached_forum_replies WHERE postId = :postId")
    suspend fun deleteForumRepliesForPost(postId: String)

    @Transaction
    suspend fun replaceForumReplies(postId: String, replies: List<CachedForumReplyEntity>) {
        deleteForumRepliesForPost(postId)
        if (replies.isNotEmpty()) insertForumReplies(replies)
    }
}

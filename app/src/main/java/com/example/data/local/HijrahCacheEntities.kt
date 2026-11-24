package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.BudgetItem
import com.example.data.ChecklistItem
import com.example.data.CommunityPost
import com.example.data.CommunityReply
import com.example.data.DocumentItem
import com.example.data.SavedFavorite
import com.example.data.UserProfile
import com.google.firebase.Timestamp

@Entity(tableName = "cached_user_profiles")
data class CachedUserProfileEntity(
    @PrimaryKey val userId: String,
    val displayAlias: String,
    val originCountry: String,
    val currentCity: String,
    val hijrahPhase: String,
    val focusCountry: String,
    val focusCity: String,
    val onboardingCompleted: Boolean,
    val lastVisitedTitle: String,
    val lastVisitedRoute: String,
    val completedStepsCount: Long,
    val totalStepsCount: Long,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_checklist_items")
data class CachedChecklistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val phase: String,
    val title: String,
    val category: String,
    val completed: Boolean,
    val sortOrder: Long,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

@Entity(tableName = "cached_document_items")
data class CachedDocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val category: String,
    val docStatus: String,
    val note: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

@Entity(tableName = "cached_budget_items")
data class CachedBudgetEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val category: String,
    val amountEur: Long,
    val recurringMonthly: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

@Entity(tableName = "cached_favorites")
data class CachedFavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val targetType: String,
    val targetId: String,
    val title: String,
    val subtitle: String,
    val createdAtMillis: Long
)

@Entity(tableName = "cached_forum_posts")
data class CachedForumPostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorAlias: String,
    val groupTitle: String,
    val title: String,
    val body: String,
    val status: String,
    val replyCount: Long,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val savedForOffline: Boolean = false,
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_forum_replies")
data class CachedForumReplyEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val authorAlias: String,
    val body: String,
    val status: String,
    val createdAtMillis: Long
)

fun Timestamp?.toEpochMillis(): Long = this?.toDate()?.time ?: 0L

fun Long.toFirebaseTimestamp(): Timestamp? =
    if (this > 0L) Timestamp(this / 1000L, ((this % 1000L) * 1_000_000L).toInt()) else null

fun UserProfile.toCacheEntity(): CachedUserProfileEntity = CachedUserProfileEntity(
    userId = userId.ifBlank { id },
    displayAlias = displayAlias,
    originCountry = originCountry,
    currentCity = currentCity,
    hijrahPhase = hijrahPhase,
    focusCountry = focusCountry,
    focusCity = focusCity,
    onboardingCompleted = onboardingCompleted,
    lastVisitedTitle = lastVisitedTitle,
    lastVisitedRoute = lastVisitedRoute,
    completedStepsCount = completedStepsCount,
    totalStepsCount = totalStepsCount,
    createdAtMillis = createdAt.toEpochMillis(),
    updatedAtMillis = updatedAt.toEpochMillis()
)

fun CachedUserProfileEntity.toDomainModel(): UserProfile = UserProfile(
    id = userId,
    userId = userId,
    displayAlias = displayAlias,
    originCountry = originCountry,
    currentCity = currentCity,
    hijrahPhase = hijrahPhase,
    focusCountry = focusCountry,
    focusCity = focusCity,
    onboardingCompleted = onboardingCompleted,
    lastVisitedTitle = lastVisitedTitle,
    lastVisitedRoute = lastVisitedRoute,
    completedStepsCount = completedStepsCount,
    totalStepsCount = totalStepsCount,
    createdAt = createdAtMillis.toFirebaseTimestamp(),
    updatedAt = updatedAtMillis.toFirebaseTimestamp()
)

fun ChecklistItem.toCacheEntity(uid: String): CachedChecklistEntity = CachedChecklistEntity(
    id = id,
    userId = userId.ifBlank { uid },
    phase = phase,
    title = title,
    category = category,
    completed = completed,
    sortOrder = sortOrder,
    createdAtMillis = createdAt.toEpochMillis(),
    updatedAtMillis = updatedAt.toEpochMillis()
)

fun CachedChecklistEntity.toDomainModel(): ChecklistItem = ChecklistItem(
    id = id,
    userId = userId,
    phase = phase,
    title = title,
    category = category,
    completed = completed,
    sortOrder = sortOrder,
    createdAt = createdAtMillis.toFirebaseTimestamp(),
    updatedAt = updatedAtMillis.toFirebaseTimestamp()
)

fun DocumentItem.toCacheEntity(uid: String): CachedDocumentEntity = CachedDocumentEntity(
    id = id,
    userId = userId.ifBlank { uid },
    title = title,
    category = category,
    docStatus = docStatus,
    note = note,
    createdAtMillis = createdAt.toEpochMillis(),
    updatedAtMillis = updatedAt.toEpochMillis()
)

fun CachedDocumentEntity.toDomainModel(): DocumentItem = DocumentItem(
    id = id,
    userId = userId,
    title = title,
    category = category,
    docStatus = docStatus,
    note = note,
    createdAt = createdAtMillis.toFirebaseTimestamp(),
    updatedAt = updatedAtMillis.toFirebaseTimestamp()
)

fun BudgetItem.toCacheEntity(uid: String): CachedBudgetEntity = CachedBudgetEntity(
    id = id,
    userId = userId.ifBlank { uid },
    title = title,
    category = category,
    amountEur = amountEur,
    recurringMonthly = recurringMonthly,
    createdAtMillis = createdAt.toEpochMillis(),
    updatedAtMillis = updatedAt.toEpochMillis()
)

fun CachedBudgetEntity.toDomainModel(): BudgetItem = BudgetItem(
    id = id,
    userId = userId,
    title = title,
    category = category,
    amountEur = amountEur,
    recurringMonthly = recurringMonthly,
    createdAt = createdAtMillis.toFirebaseTimestamp(),
    updatedAt = updatedAtMillis.toFirebaseTimestamp()
)

fun SavedFavorite.toCacheEntity(uid: String): CachedFavoriteEntity = CachedFavoriteEntity(
    id = id,
    userId = userId.ifBlank { uid },
    targetType = targetType,
    targetId = targetId,
    title = title,
    subtitle = subtitle,
    createdAtMillis = createdAt.toEpochMillis()
)

fun CachedFavoriteEntity.toDomainModel(): SavedFavorite = SavedFavorite(
    id = id,
    userId = userId,
    targetType = targetType,
    targetId = targetId,
    title = title,
    subtitle = subtitle,
    createdAt = createdAtMillis.toFirebaseTimestamp()
)

fun CommunityPost.toCacheEntity(savedForOffline: Boolean): CachedForumPostEntity = CachedForumPostEntity(
    id = id,
    userId = userId,
    authorAlias = authorAlias,
    groupTitle = groupTitle,
    title = title,
    body = body,
    status = status,
    replyCount = replyCount,
    createdAtMillis = createdAt.toEpochMillis(),
    updatedAtMillis = updatedAt.toEpochMillis(),
    savedForOffline = savedForOffline
)

fun CachedForumPostEntity.toDomainModel(): CommunityPost = CommunityPost(
    id = id,
    userId = userId,
    authorAlias = authorAlias,
    groupTitle = groupTitle,
    title = title,
    body = body,
    status = status,
    replyCount = replyCount,
    createdAt = createdAtMillis.toFirebaseTimestamp(),
    updatedAt = updatedAtMillis.toFirebaseTimestamp()
)

fun CommunityReply.toCacheEntity(postId: String): CachedForumReplyEntity = CachedForumReplyEntity(
    id = id,
    postId = postId,
    userId = userId,
    authorAlias = authorAlias,
    body = body,
    status = status,
    createdAtMillis = createdAt.toEpochMillis()
)

fun CachedForumReplyEntity.toDomainModel(): CommunityReply = CommunityReply(
    id = id,
    userId = userId,
    authorAlias = authorAlias,
    body = body,
    status = status,
    createdAt = createdAtMillis.toFirebaseTimestamp()
)

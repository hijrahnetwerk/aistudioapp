package com.example.data

import android.content.Context
import com.example.R
import com.example.data.local.HijrahCacheDao
import com.example.data.local.HijrahLocalDatabase
import com.example.data.local.toCacheEntity
import com.example.data.local.toDomainModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HijrahRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
    private val cacheDao: HijrahCacheDao? = null
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = Firebase.auth,
        cacheDao = HijrahLocalDatabase.getInstance(context).cacheDao()
    )

    private val _isOfflineCacheMode = MutableStateFlow(false)
    val isOfflineCacheMode: StateFlow<Boolean> = _isOfflineCacheMode.asStateFlow()

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("Gebruiker moet aangemeld zijn met Google.")

    private fun isOfflineFirestoreError(error: Throwable): Boolean {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        return firestoreError?.code == FirebaseFirestoreException.Code.UNAVAILABLE ||
            firestoreError?.code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
    }

    fun observeSavedOfflinePostIds(): Flow<Set<String>> =
        cacheDao?.observeSavedOfflinePostIds()?.map { it.toSet() } ?: flowOf(emptySet())

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getUserProfileOnce(userId) }
        if (cached != null) {
            emit(cached.toDomainModel())
        }
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    val profile = if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                    if (profile != null) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.upsertUserProfile(profile.toCacheEntity())
                        }
                        if (!snapshot.metadata.isFromCache) {
                            _isOfflineCacheMode.value = false
                        }
                        profile
                    } else if (snapshot.metadata.isFromCache && cached != null) {
                        _isOfflineCacheMode.value = true
                        cached.toDomainModel()
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getUserProfileOnce(userId) }
                    if (isOfflineFirestoreError(error) && fallback != null && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeUserProfile(userId).map { it?.toDomainModel() })
                    } else {
                        logStreamFailure(error, OperationType.GET, path, userId)
                        throw error
                    }
                }
        )
    }

    fun observeChecklist(userId: String): Flow<List<ChecklistItem>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/checklist_items"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getChecklistOnce(userId).orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("users").document(userId).collection("checklist_items")
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .snapshots()
                .map { snapshot ->
                    val items = snapshot.toObjects(
                        ChecklistItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    if (!snapshot.metadata.isFromCache || items.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.replaceChecklist(userId, items.map { it.toCacheEntity(userId) })
                        }
                        if (!snapshot.metadata.isFromCache) _isOfflineCacheMode.value = false
                        items
                    } else if (cached.isNotEmpty()) {
                        _isOfflineCacheMode.value = true
                        cached.map { it.toDomainModel() }
                    } else {
                        items
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getChecklistOnce(userId).orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeChecklist(userId).map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, userId)
                        throw error
                    }
                }
        )
    }

    fun observeBudgetItems(userId: String): Flow<List<BudgetItem>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/budget_items"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getBudgetItemsOnce(userId).orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("users").document(userId).collection("budget_items")
                .snapshots()
                .map { snapshot ->
                    val items = snapshot.toObjects(
                        BudgetItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    if (!snapshot.metadata.isFromCache || items.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.replaceBudgetItems(userId, items.map { it.toCacheEntity(userId) })
                        }
                        items
                    } else if (cached.isNotEmpty()) {
                        cached.map { it.toDomainModel() }
                    } else {
                        items
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getBudgetItemsOnce(userId).orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeBudgetItems(userId).map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, userId)
                        throw error
                    }
                }
        )
    }

    fun observeDocuments(userId: String): Flow<List<DocumentItem>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/documents"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getDocumentsOnce(userId).orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("users").document(userId).collection("documents")
                .snapshots()
                .map { snapshot ->
                    val items = snapshot.toObjects(
                        DocumentItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    if (!snapshot.metadata.isFromCache || items.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.replaceDocuments(userId, items.map { it.toCacheEntity(userId) })
                        }
                        items
                    } else if (cached.isNotEmpty()) {
                        cached.map { it.toDomainModel() }
                    } else {
                        items
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getDocumentsOnce(userId).orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeDocuments(userId).map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, userId)
                        throw error
                    }
                }
        )
    }

    fun observeNotes(userId: String): Flow<List<PersonalNote>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/notes"
        emitAll(
            db.collection("users").document(userId).collection("notes")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        PersonalNote::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeFavorites(userId: String): Flow<List<SavedFavorite>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/favorites"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getFavoritesOnce(userId).orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("users").document(userId).collection("favorites")
                .snapshots()
                .map { snapshot ->
                    val items = snapshot.toObjects(
                        SavedFavorite::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    if (!snapshot.metadata.isFromCache || items.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.replaceFavorites(userId, items.map { it.toCacheEntity(userId) })
                        }
                        items
                    } else if (cached.isNotEmpty()) {
                        cached.map { it.toDomainModel() }
                    } else {
                        items
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getFavoritesOnce(userId).orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeFavorites(userId).map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, userId)
                        throw error
                    }
                }
        )
    }

    fun observeGameScores(userId: String): Flow<List<GameScoreRecord>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/game_scores"
        emitAll(
            db.collection("users").document(userId).collection("game_scores")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        GameScoreRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeNotificationPreferences(userId: String): Flow<NotificationPreferences?> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "users/$userId/notification_settings/fcm"
        emitAll(
            db.collection("users").document(userId)
                .collection("notification_settings").document("fcm")
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            NotificationPreferences::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.GET, path, userId)
                    throw error
                }
        )
    }

    fun observeApprovedExperiences(): Flow<List<ExperienceSubmission>> = flow {
        val uid = requireUserId()
        val path = "experiences"
        emitAll(
            db.collection("experiences")
                .whereEqualTo("status", "APPROVED")
                .limit(30)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ExperienceSubmission::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.LIST, path, uid)
                    throw error
                }
        )
    }

    fun observeMyExperiences(userId: String): Flow<List<ExperienceSubmission>> = flow {
        require(userId.isNotBlank()) { "userId mag niet leeg zijn" }
        val path = "experiences"
        emitAll(
            db.collection("experiences")
                .whereEqualTo("userId", userId)
                .limit(20)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ExperienceSubmission::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observePendingExperiencesForAdmin(): Flow<List<ExperienceSubmission>> = flow {
        val uid = requireUserId()
        val path = "experiences"
        emitAll(
            db.collection("experiences")
                .whereEqualTo("status", "PENDING")
                .limit(30)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ExperienceSubmission::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamFailure(error, OperationType.LIST, path, uid)
                    throw error
                }
        )
    }

    fun observeCommunityPosts(): Flow<List<CommunityPost>> = flow {
        val uid = requireUserId()
        val path = "community_posts"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getForumPostsOnce().orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("community_posts")
                .whereEqualTo("status", "PUBLISHED")
                .limit(50)
                .snapshots()
                .map { snapshot ->
                    val posts = snapshot.toObjects(
                        CommunityPost::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: Long.MAX_VALUE }
                    if (!snapshot.metadata.isFromCache || posts.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.refreshForumFeedPreservingPinned(
                                posts.map { it.toCacheEntity(savedForOffline = false) }
                            )
                        }
                        if (!snapshot.metadata.isFromCache) _isOfflineCacheMode.value = false
                        posts
                    } else if (cached.isNotEmpty()) {
                        _isOfflineCacheMode.value = true
                        cached.map { it.toDomainModel() }
                    } else {
                        posts
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getForumPostsOnce().orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeForumPosts().map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, uid)
                        throw error
                    }
                }
        )
    }

    fun observePostReplies(postId: String): Flow<List<CommunityReply>> = flow {
        val uid = requireUserId()
        require(postId.isNotBlank()) { "postId mag niet leeg zijn" }
        val path = "community_posts/$postId/replies"
        val cached = withContext(Dispatchers.IO) { cacheDao?.getForumRepliesOnce(postId).orEmpty() }
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomainModel() })
        }
        emitAll(
            db.collection("community_posts").document(postId).collection("replies")
                .whereEqualTo("status", "PUBLISHED")
                .snapshots()
                .map { snapshot ->
                    val replies = snapshot.toObjects(
                        CommunityReply::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.createdAt?.seconds ?: Long.MAX_VALUE }
                    if (!snapshot.metadata.isFromCache || replies.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            cacheDao?.replaceForumReplies(
                                postId,
                                replies.map { it.toCacheEntity(postId) }
                            )
                        }
                        replies
                    } else if (cached.isNotEmpty()) {
                        cached.map { it.toDomainModel() }
                    } else {
                        replies
                    }
                }
                .catch { error ->
                    val fallback = withContext(Dispatchers.IO) { cacheDao?.getForumRepliesOnce(postId).orEmpty() }
                    if (isOfflineFirestoreError(error) && fallback.isNotEmpty() && cacheDao != null) {
                        _isOfflineCacheMode.value = true
                        emitAll(cacheDao.observeForumReplies(postId).map { rows -> rows.map { it.toDomainModel() } })
                    } else {
                        logStreamFailure(error, OperationType.LIST, path, uid)
                        throw error
                    }
                }
        )
    }

    suspend fun toggleSaveForumPostOffline(post: CommunityPost, currentlySaved: Boolean): Result<Unit> {
        return try {
            val dao = cacheDao ?: return Result.failure(IllegalStateException("Lokale cache niet beschikbaar."))
            val nextSaved = !currentlySaved
            withContext(Dispatchers.IO) {
                dao.upsertForumPost(post.toCacheEntity(savedForOffline = nextSaved))
            }
            if (nextSaved) {
                runCatching {
                    val replySnap = db.collection("community_posts")
                        .document(post.id)
                        .collection("replies")
                        .whereEqualTo("status", "PUBLISHED")
                        .get()
                        .await()
                    val replies = replySnap.toObjects(
                        CommunityReply::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.createdAt?.seconds ?: Long.MAX_VALUE }
                    withContext(Dispatchers.IO) {
                        dao.replaceForumReplies(post.id, replies.map { it.toCacheEntity(post.id) })
                    }
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ensureProfileInitialized(defaultAlias: String): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val safeAlias = defaultAlias.trim().take(40).ifBlank { "Zuster" }.let {
                if (it.length < 2) "Zuster" else it
            }
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            val existing = userRef.get().await()
            if (!existing.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayAlias" to safeAlias,
                    "originCountry" to "",
                    "currentCity" to "",
                    "hijrahPhase" to HijrahPhase.ORIENTATIE.name,
                    "focusCountry" to "Nog aan het verkennen",
                    "focusCity" to "",
                    "onboardingCompleted" to false,
                    "lastVisitedTitle" to "Startoriëntatie: jouw traject in kaart",
                    "lastVisitedRoute" to "tools",
                    "completedStepsCount" to 0L,
                    "totalStepsCount" to 4L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userRef.set(payload).await()
                seedStarterChecklist(uid)
                seedStarterDocuments(uid)
            }
            _isOfflineCacheMode.value = false
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val uid = auth.currentUser?.uid
            val hasOfflineProfile = if (uid != null) {
                withContext(Dispatchers.IO) { cacheDao?.getUserProfileOnce(uid) != null }
            } else {
                false
            }
            if (isOfflineFirestoreError(e) && hasOfflineProfile) {
                _isOfflineCacheMode.value = true
                Result.success(Unit)
            } else {
                handleFirestoreError(e, OperationType.CREATE, docPath)
                Result.failure(e)
            }
        }
    }

    private suspend fun seedStarterChecklist(uid: String) {
        val checklistRef = db.collection("users").document(uid).collection("checklist_items")
        val existing = checklistRef.limit(1).get().await()
        if (!existing.isEmpty) return

        val starterSteps = listOf(
            Triple(HijrahPhase.ORIENTATIE.name, "Gezinsdoelen en voorwaarden voor emigratie bespreken", "Oriëntatie"),
            Triple(HijrahPhase.ONDERZOEK.name, "Twee tot drie voorkeurslanden inhoudelijk vergelijken", "Landkeuze"),
            Triple(HijrahPhase.VOORBEREIDING.name, "Geldigheid paspoorten en basisdocumenten controleren", "Documenten"),
            Triple(HijrahPhase.INTEGRATIE.name, "Contact leggen met betrouwbare zusters in de regio", "Netwerk")
        )
        val batch = db.batch()
        starterSteps.forEachIndexed { index, (phase, title, category) ->
            val docRef = checklistRef.document("starter_${index + 1}")
            batch.set(
                docRef,
                mapOf(
                    "userId" to uid,
                    "phase" to phase,
                    "title" to title,
                    "category" to category,
                    "completed" to false,
                    "sortOrder" to (index + 1).toLong(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
        batch.commit().await()
    }

    private suspend fun seedStarterDocuments(uid: String) {
        val docsRef = db.collection("users").document(uid).collection("documents")
        val existing = docsRef.limit(1).get().await()
        if (!existing.isEmpty) return

        val starterDocs = listOf(
            Triple("Paspoorten hele gezin", "Identiteit", "Controleer minimaal 6-12 maanden geldigheid"),
            Triple("Geboorteaktes + apostille", "Legalisatie", "Internationale akte aanvragen bij gemeente"),
            Triple("Huwelijksakte gelegaliseerd", "Gezin", "Benodigd voor gezinsvisum"),
            Triple("Medisch overzicht & vaccinaties", "Zorg", "Engels of Arabisch uittreksel")
        )
        val batch = db.batch()
        starterDocs.forEachIndexed { index, (title, category, note) ->
            batch.set(
                docsRef.document("doc_starter_${index + 1}"),
                mapOf(
                    "userId" to uid,
                    "title" to title,
                    "category" to category,
                    "docStatus" to DocumentStatus.NOG_REGELEN.name,
                    "note" to note,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
        batch.commit().await()
    }

    suspend fun addArrivalChecklistPreset(currentSize: Int): Result<Unit> {
        var docPath = "users/unknown/checklist_items"
        return try {
            val uid = requireUserId()
            val checklistRef = db.collection("users").document(uid).collection("checklist_items")
            docPath = checklistRef.path
            val arrivalSteps = listOf(
                "Lokaal telefoonnummer en internet regelen" to "Aankomst",
                "Tijdelijke verblijfplaats beoordelen op reistijd & scholen" to "Wonen",
                "Verblijfsregistratie of lokale aanmelding inplannen" to "Juridisch",
                "Kennismaking bij lokale moskee en zusterkring" to "Integratie"
            )
            val batch = db.batch()
            arrivalSteps.forEachIndexed { index, (title, category) ->
                val docRef = checklistRef.document()
                batch.set(
                    docRef,
                    mapOf(
                        "userId" to uid,
                        "phase" to HijrahPhase.INTEGRATIE.name,
                        "title" to title,
                        "category" to category,
                        "completed" to false,
                        "sortOrder" to (currentSize + index + 1).toLong(),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.update(
                db.collection("users").document(uid),
                mapOf(
                    "totalStepsCount" to (currentSize + arrivalSteps.size).toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateOnboardingAndPhase(
        displayAlias: String,
        originCountry: String,
        currentCity: String,
        phase: HijrahPhase,
        focusCountry: String,
        focusCity: String = ""
    ): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val cleanAlias = displayAlias.trim().take(40)
            val cleanOrigin = originCountry.trim().take(60)
            val cleanCurrentCity = currentCity.trim().take(60)
            val cleanFocusCountry = focusCountry.trim().take(60)
            val cleanFocusCity = focusCity.trim().take(60)
            require(cleanAlias.length in 2..40) { "Roepnaam moet tussen 2 en 40 tekens zijn." }
            require(cleanOrigin.length >= 2) { "Vul je land van oorsprong in (minimaal 2 tekens)." }
            require(cleanCurrentCity.length >= 2) { "Vul je huidige woonplaats in (minimaal 2 tekens)." }
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            val existing = userRef.get().await()
            if (!existing.exists()) {
                userRef.set(
                    mapOf(
                        "userId" to uid,
                        "displayAlias" to cleanAlias,
                        "originCountry" to cleanOrigin,
                        "currentCity" to cleanCurrentCity,
                        "hijrahPhase" to phase.name,
                        "focusCountry" to cleanFocusCountry,
                        "focusCity" to cleanFocusCity,
                        "onboardingCompleted" to true,
                        "lastVisitedTitle" to "Startoriëntatie: jouw traject in kaart",
                        "lastVisitedRoute" to "tools",
                        "completedStepsCount" to 0L,
                        "totalStepsCount" to 4L,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                seedStarterChecklist(uid)
                seedStarterDocuments(uid)
            } else {
                userRef.update(
                    mapOf(
                        "displayAlias" to cleanAlias,
                        "originCountry" to cleanOrigin,
                        "currentCity" to cleanCurrentCity,
                        "hijrahPhase" to phase.name,
                        "focusCountry" to cleanFocusCountry,
                        "focusCity" to cleanFocusCity,
                        "onboardingCompleted" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updatePhase(phase: HijrahPhase): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            userRef.update(
                mapOf(
                    "hijrahPhase" to phase.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun toggleChecklistItem(item: ChecklistItem, allItems: List<ChecklistItem>): Result<Unit> {
        var docPath = "users/unknown/checklist_items/${item.id}"
        return try {
            val uid = requireUserId()
            require(item.id.isNotBlank()) { "Stap-ID ontbreekt." }
            val nextCompleted = !item.completed
            val stepRef = db.collection("users").document(uid)
                .collection("checklist_items").document(item.id)
            docPath = stepRef.path

            val newCompletedCount = allItems.count {
                if (it.id == item.id) nextCompleted else it.completed
            }.toLong()

            val batch = db.batch()
            batch.update(
                stepRef,
                mapOf(
                    "completed" to nextCompleted,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                db.collection("users").document(uid),
                mapOf(
                    "completedStepsCount" to newCompletedCount,
                    "totalStepsCount" to allItems.size.toLong().coerceAtLeast(1L),
                    "lastVisitedTitle" to item.title.take(100),
                    "lastVisitedRoute" to "tools",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addChecklistItem(phase: HijrahPhase, title: String, category: String, currentSize: Int): Result<Unit> {
        var docPath = "users/unknown/checklist_items"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim().take(120)
            val cleanCategory = category.trim().take(50).ifBlank { "Persoonlijk" }
            require(cleanTitle.length >= 2) { "Titel moet minimaal 2 tekens bevatten." }

            val colRef = db.collection("users").document(uid).collection("checklist_items")
            val newDoc = colRef.document()
            docPath = newDoc.path

            val batch = db.batch()
            batch.set(
                newDoc,
                mapOf(
                    "userId" to uid,
                    "phase" to phase.name,
                    "title" to cleanTitle,
                    "category" to cleanCategory,
                    "completed" to false,
                    "sortOrder" to (currentSize + 1).toLong(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                db.collection("users").document(uid),
                mapOf(
                    "totalStepsCount" to (currentSize + 1).toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteChecklistItem(item: ChecklistItem, allItems: List<ChecklistItem>): Result<Unit> {
        var docPath = "users/unknown/checklist_items/${item.id}"
        return try {
            val uid = requireUserId()
            val stepRef = db.collection("users").document(uid)
                .collection("checklist_items").document(item.id)
            docPath = stepRef.path
            val remaining = allItems.filterNot { it.id == item.id }
            val batch = db.batch()
            batch.delete(stepRef)
            batch.update(
                db.collection("users").document(uid),
                mapOf(
                    "completedStepsCount" to remaining.count { it.completed }.toLong(),
                    "totalStepsCount" to remaining.size.toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addBudgetItem(
        title: String,
        category: String,
        amountEur: Long,
        recurringMonthly: Boolean
    ): Result<Unit> {
        var docPath = "users/unknown/budget_items"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim().take(100)
            require(cleanTitle.length >= 2) { "Postnaam moet minimaal 2 tekens bevatten." }
            require(amountEur >= 0L) { "Bedrag moet positief zijn." }
            val docRef = db.collection("users").document(uid).collection("budget_items").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "category" to category.trim().take(50).ifBlank { "Algemeen" },
                    "amountEur" to amountEur,
                    "recurringMonthly" to recurringMonthly,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteBudgetItem(itemId: String): Result<Unit> {
        var docPath = "users/unknown/budget_items/$itemId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("budget_items").document(itemId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addDocumentItem(title: String, category: String, note: String): Result<Unit> {
        var docPath = "users/unknown/documents"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim().take(120)
            require(cleanTitle.length >= 2) { "Documentnaam moet minimaal 2 tekens bevatten." }
            val docRef = db.collection("users").document(uid).collection("documents").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "category" to category.trim().take(50).ifBlank { "Documenten" },
                    "docStatus" to DocumentStatus.NOG_REGELEN.name,
                    "note" to note.trim().take(300),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cycleDocumentStatus(doc: DocumentItem): Result<Unit> {
        var docPath = "users/unknown/documents/${doc.id}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("documents").document(doc.id)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "docStatus" to doc.statusEnum().next().name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteDocumentItem(docId: String): Result<Unit> {
        var docPath = "users/unknown/documents/$docId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("documents").document(docId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun saveNote(title: String, body: String, topic: String): Result<Unit> {
        var docPath = "users/unknown/notes"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim().take(120)
            require(cleanTitle.isNotEmpty()) { "Notitietitel is verplicht." }
            val docRef = db.collection("users").document(uid).collection("notes").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "body" to body.trim().take(2000),
                    "topic" to topic.trim().take(60),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        var docPath = "users/unknown/notes/$noteId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("notes").document(noteId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(
        targetType: String,
        targetId: String,
        title: String,
        subtitle: String,
        currentlySaved: Boolean
    ): Result<Unit> {
        val safeDocId = "${targetType}_$targetId".replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(100)
        var docPath = "users/unknown/favorites/$safeDocId"
        return try {
            val uid = requireUserId()
            val favRef = db.collection("users").document(uid).collection("favorites").document(safeDocId)
            docPath = favRef.path
            if (currentlySaved) {
                favRef.delete().await()
            } else {
                favRef.set(
                    mapOf(
                        "userId" to uid,
                        "targetType" to targetType,
                        "targetId" to targetId.take(80),
                        "title" to title.trim().take(120),
                        "subtitle" to subtitle.trim().take(160),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitExperience(
        authorAlias: String,
        countryName: String,
        cityName: String,
        topic: String,
        title: String,
        story: String,
        prosAndCons: String,
        lessonLearned: String,
        practicalTip: String
    ): Result<Unit> {
        var docPath = "experiences"
        return try {
            val uid = requireUserId()
            val cleanAlias = authorAlias.trim().take(40)
            val cleanCountry = countryName.trim().take(60)
            val cleanCity = cityName.trim().take(60)
            val cleanTopic = topic.trim().take(60)
            val cleanTitle = title.trim().take(120)
            val cleanStory = story.trim().take(4000)
            val cleanProsCons = prosAndCons.trim().take(1000)
            val cleanLesson = lessonLearned.trim().take(1000)
            val cleanTip = practicalTip.trim().take(600)

            require(cleanAlias.length >= 2) { "Roepnaam moet minimaal 2 tekens bevatten." }
            require(cleanCountry.length >= 2) { "Vul een land in." }
            require(cleanTopic.length >= 2) { "Kies een onderwerp." }
            require(cleanTitle.length >= 4) { "Titel moet minimaal 4 tekens bevatten." }
            require(cleanStory.length >= 10) { "Ervaring moet minimaal 10 tekens bevatten." }

            val docRef = db.collection("experiences").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "authorAlias" to cleanAlias,
                    "countryName" to cleanCountry,
                    "cityName" to cleanCity,
                    "topic" to cleanTopic,
                    "title" to cleanTitle,
                    "story" to cleanStory,
                    "prosAndCons" to cleanProsCons,
                    "lessonLearned" to cleanLesson,
                    "practicalTip" to cleanTip,
                    "status" to "PENDING",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun moderateExperience(experienceId: String, approve: Boolean): Result<Unit> {
        var docPath = "experiences/$experienceId"
        return try {
            requireUserId()
            val docRef = db.collection("experiences").document(experienceId)
            docPath = docRef.path
            val updates = mutableMapOf<String, Any>(
                "status" to if (approve) "APPROVED" else "REJECTED",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (approve) {
                updates["verifiedAt"] = FieldValue.serverTimestamp()
            }
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun createCommunityPost(
        authorAlias: String,
        groupTitle: String,
        title: String,
        body: String
    ): Result<Unit> {
        var docPath = "community_posts"
        return try {
            val uid = requireUserId()
            val cleanAlias = authorAlias.trim().take(40)
            val cleanGroup = groupTitle.trim().take(80)
            val cleanTitle = title.trim().take(120)
            val cleanBody = body.trim().take(2000)
            require(cleanTitle.length >= 3) { "Titel moet minimaal 3 tekens bevatten." }
            require(cleanBody.length >= 5) { "Bericht moet minimaal 5 tekens bevatten." }

            val docRef = db.collection("community_posts").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "authorAlias" to cleanAlias.ifBlank { "Zuster" },
                    "groupTitle" to cleanGroup.ifBlank { "Oriëntatie & Vragen" },
                    "title" to cleanTitle,
                    "body" to cleanBody,
                    "status" to "PUBLISHED",
                    "replyCount" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addCommunityReply(
        postId: String,
        authorAlias: String,
        body: String
    ): Result<Unit> {
        var docPath = "community_posts/$postId/replies"
        return try {
            val uid = requireUserId()
            val cleanBody = body.trim().take(1200)
            require(cleanBody.length >= 2) { "Reactie moet minimaal 2 tekens bevatten." }

            val postRef = db.collection("community_posts").document(postId)
            val replyRef = postRef.collection("replies").document()
            docPath = replyRef.path

            val batch = db.batch()
            batch.set(
                replyRef,
                mapOf(
                    "userId" to uid,
                    "authorAlias" to authorAlias.trim().take(40).ifBlank { "Zuster" },
                    "body" to cleanBody,
                    "status" to "PUBLISHED",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                postRef,
                mapOf(
                    "replyCount" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateCommunityPost(
        postId: String,
        groupTitle: String,
        title: String,
        body: String
    ): Result<Unit> {
        var docPath = "community_posts/$postId"
        return try {
            requireUserId()
            val cleanGroup = groupTitle.trim().take(80).ifBlank { "Oriëntatie & Vragen" }
            val cleanTitle = title.trim().take(120)
            val cleanBody = body.trim().take(2000)
            require(cleanTitle.length >= 3) { "Titel moet minimaal 3 tekens bevatten." }
            require(cleanBody.length >= 5) { "Bericht moet minimaal 5 tekens bevatten." }

            val postRef = db.collection("community_posts").document(postId)
            docPath = postRef.path
            postRef.update(
                mapOf(
                    "groupTitle" to cleanGroup,
                    "title" to cleanTitle,
                    "body" to cleanBody,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteCommunityPost(postId: String): Result<Unit> {
        var docPath = "community_posts/$postId"
        return try {
            requireUserId()
            val postRef = db.collection("community_posts").document(postId)
            docPath = postRef.path
            postRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitModerationReport(
        targetType: String,
        targetId: String,
        reason: String
    ): Result<Unit> {
        var docPath = "reports"
        return try {
            val uid = requireUserId()
            val cleanReason = reason.trim().take(400)
            require(cleanReason.length >= 4) { "Omschrijf de reden in minimaal 4 tekens." }
            val docRef = db.collection("reports").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "reporterId" to uid,
                    "targetType" to targetType,
                    "targetId" to targetId.take(128),
                    "reason" to cleanReason,
                    "status" to "OPEN",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordLastVisited(title: String, route: String): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            userRef.update(
                mapOf(
                    "lastVisitedTitle" to title.trim().take(100),
                    "lastVisitedRoute" to route.trim().take(60),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordGameResult(gameId: String, score: Long, won: Boolean): Result<Unit> {
        var docPath = "users/unknown/game_scores/$gameId"
        return try {
            val uid = requireUserId()
            require(HijrahGameId.entries.any { it.id == gameId }) { "Ongeldig spel-ID." }
            val safeScore = score.coerceIn(0L, 1_000_000L)
            val docRef = db.collection("users").document(uid)
                .collection("game_scores").document(gameId)
            docPath = docRef.path
            val existing = docRef.get().await()
            if (!existing.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "gameId" to gameId,
                        "bestScore" to safeScore,
                        "gamesPlayed" to 1L,
                        "gamesWon" to if (won) 1L else 0L,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val prevBest = existing.getLong("bestScore") ?: 0L
                val prevPlayed = existing.getLong("gamesPlayed") ?: 0L
                val prevWon = existing.getLong("gamesWon") ?: 0L
                val nextPlayed = (prevPlayed + 1L).coerceAtMost(1_000_000L)
                val nextWon = (prevWon + if (won) 1L else 0L).coerceAtMost(nextPlayed)
                docRef.update(
                    mapOf(
                        "bestScore" to maxOf(prevBest, safeScore),
                        "gamesPlayed" to nextPlayed,
                        "gamesWon" to nextWon,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun saveNotificationPreferences(
        fcmToken: String,
        forumRepliesEnabled: Boolean,
        milestoneRemindersEnabled: Boolean,
        reminderCadence: ReminderCadence
    ): Result<Unit> {
        var docPath = "users/unknown/notification_settings/fcm"
        return try {
            val uid = requireUserId()
            val safeToken = fcmToken.trim().take(512)
            val docRef = db.collection("users").document(uid)
                .collection("notification_settings").document("fcm")
            docPath = docRef.path
            val existing = docRef.get().await()
            if (!existing.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "fcmToken" to safeToken,
                        "forumRepliesEnabled" to forumRepliesEnabled,
                        "milestoneRemindersEnabled" to milestoneRemindersEnabled,
                        "reminderCadence" to reminderCadence.name,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val updates = mutableMapOf<String, Any>(
                    "forumRepliesEnabled" to forumRepliesEnabled,
                    "milestoneRemindersEnabled" to milestoneRemindersEnabled,
                    "reminderCadence" to reminderCadence.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (safeToken.isNotBlank()) {
                    updates["fcmToken"] = safeToken
                }
                docRef.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun saveFcmToken(token: String): Result<Unit> {
        var docPath = "users/unknown/notification_settings/fcm"
        return try {
            val uid = requireUserId()
            val safeToken = token.trim().take(512)
            require(safeToken.isNotBlank()) { "Leeg FCM-token genegeerd." }
            val docRef = db.collection("users").document(uid)
                .collection("notification_settings").document("fcm")
            docPath = docRef.path
            val existing = docRef.get().await()
            if (!existing.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "fcmToken" to safeToken,
                        "forumRepliesEnabled" to true,
                        "milestoneRemindersEnabled" to true,
                        "reminderCadence" to ReminderCadence.WEEKLY.name,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.update(
                    mapOf(
                        "fcmToken" to safeToken,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    private fun logStreamFailure(
        error: Throwable,
        operationType: OperationType,
        path: String,
        uid: String
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, operationType, path)
        }
    }
}

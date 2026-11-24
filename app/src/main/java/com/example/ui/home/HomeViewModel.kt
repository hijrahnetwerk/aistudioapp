package com.example.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BudgetItem
import com.example.data.ChecklistItem
import com.example.data.CommunityPost
import com.example.data.CommunityReply
import com.example.data.DocumentItem
import com.example.data.ExperienceSubmission
import com.example.data.GameScoreRecord
import com.example.data.HijrahGameId
import com.example.data.HijrahPhase
import com.example.data.HijrahRepository
import com.example.data.NotificationPreferences
import com.example.data.PersonalNote
import com.example.data.ReminderCadence
import com.example.data.SavedFavorite
import com.example.data.UserProfile
import com.example.notifications.HijrahNotificationManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class HomeViewModel(
    private val repository: HijrahRepository,
    private val currentUserId: String,
    private val defaultAlias: String,
    val isAdminUser: Boolean
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _isSavingProfile = MutableStateFlow(false)
    val isSavingProfile: StateFlow<Boolean> = _isSavingProfile.asStateFlow()

    private val _selectedPostId = MutableStateFlow<String?>(null)

    init {
        retryInitialization()
    }

    fun retryInitialization() {
        viewModelScope.launch {
            repository.ensureProfileInitialized(defaultAlias)
                .onFailure { error ->
                    _actionMessage.value = error.localizedMessage ?: "Profiel initialiseren mislukt."
                }
        }
    }

    val profileState: StateFlow<UiState<UserProfile?>> =
        repository.observeProfile(currentUserId)
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "profiel") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val checklistState: StateFlow<UiState<List<ChecklistItem>>> =
        repository.observeChecklist(currentUserId)
            .map<List<ChecklistItem>, UiState<List<ChecklistItem>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "checklist") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val budgetState: StateFlow<UiState<List<BudgetItem>>> =
        repository.observeBudgetItems(currentUserId)
            .map<List<BudgetItem>, UiState<List<BudgetItem>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "budget") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val documentsState: StateFlow<UiState<List<DocumentItem>>> =
        repository.observeDocuments(currentUserId)
            .map<List<DocumentItem>, UiState<List<DocumentItem>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "documenten") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val notesState: StateFlow<UiState<List<PersonalNote>>> =
        repository.observeNotes(currentUserId)
            .map<List<PersonalNote>, UiState<List<PersonalNote>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "notities") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val favoritesState: StateFlow<UiState<List<SavedFavorite>>> =
        repository.observeFavorites(currentUserId)
            .map<List<SavedFavorite>, UiState<List<SavedFavorite>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "favorieten") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val gameScoresState: StateFlow<UiState<List<GameScoreRecord>>> =
        repository.observeGameScores(currentUserId)
            .map<List<GameScoreRecord>, UiState<List<GameScoreRecord>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "spelscores") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val notificationPrefsState: StateFlow<UiState<NotificationPreferences?>> =
        repository.observeNotificationPreferences(currentUserId)
            .map<NotificationPreferences?, UiState<NotificationPreferences?>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "meldingsvoorkeuren") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val approvedExperiencesState: StateFlow<UiState<List<ExperienceSubmission>>> =
        repository.observeApprovedExperiences()
            .map<List<ExperienceSubmission>, UiState<List<ExperienceSubmission>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "ervaringen") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val myExperiencesState: StateFlow<UiState<List<ExperienceSubmission>>> =
        repository.observeMyExperiences(currentUserId)
            .map<List<ExperienceSubmission>, UiState<List<ExperienceSubmission>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "mijn ervaringen") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val pendingAdminExperiencesState: StateFlow<UiState<List<ExperienceSubmission>>> =
        if (isAdminUser) {
            repository.observePendingExperiencesForAdmin()
                .map<List<ExperienceSubmission>, UiState<List<ExperienceSubmission>>> { UiState.Success(it) }
                .catch { error -> handleStreamError(error, "moderatie") { emit(it) } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)
        } else {
            MutableStateFlow(UiState.Success(emptyList()))
        }

    val communityPostsState: StateFlow<UiState<List<CommunityPost>>> =
        repository.observeCommunityPosts()
            .map<List<CommunityPost>, UiState<List<CommunityPost>>> { UiState.Success(it) }
            .catch { error -> handleStreamError(error, "community") { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPostRepliesState: StateFlow<UiState<List<CommunityReply>>> =
        _selectedPostId.flatMapLatest { postId ->
            if (postId.isNullOrBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observePostReplies(postId)
                    .map<List<CommunityReply>, UiState<List<CommunityReply>>> { UiState.Success(it) }
                    .catch { error -> handleStreamError(error, "reacties") { emit(it) } }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Success(emptyList()))

    val isOfflineCacheMode: StateFlow<Boolean> = repository.isOfflineCacheMode

    val savedOfflinePostIds: StateFlow<Set<String>> =
        repository.observeSavedOfflinePostIds()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptySet())

    fun clearMessage() {
        _actionMessage.value = null
    }

    fun selectPostForReplies(postId: String?) {
        _selectedPostId.value = postId
    }

    fun selectPhase(phase: HijrahPhase) {
        viewModelScope.launch {
            repository.updatePhase(phase)
                .onSuccess { _actionMessage.value = "Trajectfase bijgewerkt naar ${phase.titleNl}" }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Kon fase niet bijwerken." }
        }
    }

    fun saveOnboarding(
        displayAlias: String,
        originCountry: String,
        currentCity: String,
        phase: HijrahPhase,
        focusCountry: String,
        focusCity: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSavingProfile.value = true
            repository.updateOnboardingAndPhase(
                displayAlias = displayAlias,
                originCountry = originCountry,
                currentCity = currentCity,
                phase = phase,
                focusCountry = focusCountry,
                focusCity = focusCity
            )
                .onSuccess {
                    _isSavingProfile.value = false
                    _actionMessage.value = "Jouw profiel en voorkeuren zijn opgeslagen."
                    onComplete()
                }
                .onFailure {
                    _isSavingProfile.value = false
                    _actionMessage.value = it.localizedMessage ?: "Opslaan mislukt."
                }
        }
    }

    fun toggleChecklistStep(item: ChecklistItem, allItems: List<ChecklistItem>) {
        viewModelScope.launch {
            repository.toggleChecklistItem(item, allItems)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Stap kon niet worden bijgewerkt." }
        }
    }

    fun addChecklistStep(phase: HijrahPhase, title: String, category: String, currentSize: Int) {
        viewModelScope.launch {
            repository.addChecklistItem(phase, title, category, currentSize)
                .onSuccess { _actionMessage.value = "Stap toegevoegd aan HijrahTools." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Toevoegen mislukt." }
        }
    }

    fun deleteChecklistStep(item: ChecklistItem, allItems: List<ChecklistItem>) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item, allItems)
                .onSuccess { _actionMessage.value = "Stap verwijderd." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Verwijderen mislukt." }
        }
    }

    fun loadArrivalPreset(currentSize: Int) {
        viewModelScope.launch {
            repository.addArrivalChecklistPreset(currentSize)
                .onSuccess { _actionMessage.value = "Aankomst- en integratiestappen toegevoegd." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Toevoegen mislukt." }
        }
    }

    fun addBudgetItem(title: String, category: String, amountEur: Long, recurringMonthly: Boolean) {
        viewModelScope.launch {
            repository.addBudgetItem(title, category, amountEur, recurringMonthly)
                .onSuccess { _actionMessage.value = "Begrotingspost opgeslagen." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Opslaan mislukt." }
        }
    }

    fun deleteBudgetItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteBudgetItem(itemId)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Verwijderen mislukt." }
        }
    }

    fun addDocumentItem(title: String, category: String, note: String) {
        viewModelScope.launch {
            repository.addDocumentItem(title, category, note)
                .onSuccess { _actionMessage.value = "Document toegevoegd." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Toevoegen mislukt." }
        }
    }

    fun cycleDocumentStatus(doc: DocumentItem) {
        viewModelScope.launch {
            repository.cycleDocumentStatus(doc)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Status bijwerken mislukt." }
        }
    }

    fun deleteDocumentItem(docId: String) {
        viewModelScope.launch {
            repository.deleteDocumentItem(docId)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Verwijderen mislukt." }
        }
    }

    fun saveNote(title: String, body: String, topic: String) {
        viewModelScope.launch {
            repository.saveNote(title, body, topic)
                .onSuccess { _actionMessage.value = "Notitie opgeslagen." }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Opslaan mislukt." }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Verwijderen mislukt." }
        }
    }

    fun toggleFavorite(
        targetType: String,
        targetId: String,
        title: String,
        subtitle: String,
        currentlySaved: Boolean
    ) {
        viewModelScope.launch {
            repository.toggleFavorite(targetType, targetId, title, subtitle, currentlySaved)
                .onSuccess {
                    _actionMessage.value = if (currentlySaved) "Verwijderd uit favorieten" else "Opgeslagen in favorieten"
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Favoriet bijwerken mislukt." }
        }
    }

    fun submitExperience(
        authorAlias: String,
        countryName: String,
        cityName: String,
        topic: String,
        title: String,
        story: String,
        prosAndCons: String,
        lessonLearned: String,
        practicalTip: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.submitExperience(
                authorAlias = authorAlias,
                countryName = countryName,
                cityName = cityName,
                topic = topic,
                title = title,
                story = story,
                prosAndCons = prosAndCons,
                lessonLearned = lessonLearned,
                practicalTip = practicalTip
            )
                .onSuccess {
                    _actionMessage.value = "Bedankt! Jouw ervaring staat klaar ter controle door beheer."
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Insturen mislukt." }
        }
    }

    fun moderateExperience(experienceId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.moderateExperience(experienceId, approve)
                .onSuccess {
                    _actionMessage.value = if (approve) "Ervaring goedgekeurd en gepubliceerd." else "Ervaring afgewezen."
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Beoordeling mislukt." }
        }
    }

    fun createCommunityPost(
        authorAlias: String,
        groupTitle: String,
        title: String,
        body: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createCommunityPost(authorAlias, groupTitle, title, body)
                .onSuccess {
                    _actionMessage.value = "Bericht geplaatst in $groupTitle"
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Plaatsen mislukt." }
        }
    }

    fun addCommunityReply(postId: String, authorAlias: String, body: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addCommunityReply(postId, authorAlias, body)
                .onSuccess {
                    _actionMessage.value = "Reactie toegevoegd."
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Reageren mislukt." }
        }
    }

    fun updateCommunityPost(
        postId: String,
        groupTitle: String,
        title: String,
        body: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.updateCommunityPost(postId, groupTitle, title, body)
                .onSuccess {
                    _actionMessage.value = "Forumbericht bijgewerkt."
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Bijwerken mislukt." }
        }
    }

    fun deleteCommunityPost(postId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCommunityPost(postId)
                .onSuccess {
                    _actionMessage.value = "Forumbericht verwijderd."
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Verwijderen mislukt." }
        }
    }

    fun toggleSaveForumPostOffline(post: CommunityPost, currentlySaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveForumPostOffline(post, currentlySaved)
                .onSuccess {
                    _actionMessage.value = if (currentlySaved) {
                        "Verwijderd uit offline discussies."
                    } else {
                        "Discussie opgeslagen voor offline lezen."
                    }
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "Kon offline status niet bijwerken."
                }
        }
    }

    fun submitReport(targetType: String, targetId: String, reason: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.submitModerationReport(targetType, targetId, reason)
                .onSuccess {
                    _actionMessage.value = "Melding verzonden naar moderatie."
                    onComplete()
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Melding verzenden mislukt." }
        }
    }

    fun recordLastVisited(title: String, route: String) {
        viewModelScope.launch {
            repository.recordLastVisited(title, route)
        }
    }

    fun recordGameResult(gameId: HijrahGameId, score: Int, won: Boolean) {
        viewModelScope.launch {
            repository.recordGameResult(gameId.id, score.toLong(), won)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Spelscore opslaan mislukt." }
        }
    }

    fun syncNotificationPreferences(
        forumRepliesEnabled: Boolean,
        milestoneRemindersEnabled: Boolean,
        reminderCadence: ReminderCadence,
        silent: Boolean = false
    ) {
        viewModelScope.launch {
            val token = HijrahNotificationManager.fetchTokenAndSyncTopics(
                forumRepliesEnabled = forumRepliesEnabled,
                milestoneRemindersEnabled = milestoneRemindersEnabled
            )
            repository.saveNotificationPreferences(
                fcmToken = token,
                forumRepliesEnabled = forumRepliesEnabled,
                milestoneRemindersEnabled = milestoneRemindersEnabled,
                reminderCadence = reminderCadence
            )
                .onSuccess {
                    if (!silent) {
                        _actionMessage.value = "FCM-meldingsvoorkeuren opgeslagen."
                    }
                }
                .onFailure {
                    if (!silent) {
                        _actionMessage.value = it.localizedMessage ?: "Opslaan van meldingen mislukt."
                    }
                }
        }
    }

    private suspend inline fun <T> handleStreamError(
        error: Throwable,
        streamName: String,
        crossinline emitError: suspend (UiState<T>) -> Unit
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Fout bij observeren van $streamName", rootError)
        emitError(UiState.Error(rootError.localizedMessage ?: "Kon $streamName niet laden."))
    }

    private companion object {
        const val TAG = "HomeViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}

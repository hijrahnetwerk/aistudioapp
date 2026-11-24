package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.DocumentStatus
import com.example.data.EditorialHighlight
import com.example.data.HijrahGameId
import com.example.data.HijrahRepository
import com.example.data.ReminderCadence
import com.example.notifications.HijrahNotificationManager
import com.example.ui.auth.AuthWelcomeScreen
import com.example.ui.auth.RegistrationScreen
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.auth.signOutWithCleanup
import com.example.ui.components.HijrahBottomNavBar
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahSecondaryButton
import com.example.ui.components.HijrahTab
import com.example.ui.games.HijrahGamesHubScreen
import com.example.ui.home.HighlightDetailSheet
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.home.NotificationsSheet
import com.example.ui.home.OnboardingModalSheet
import com.example.ui.home.QuickSearchSheet
import com.example.ui.home.ReportModerationSheet
import com.example.ui.home.ShareExperienceModalSheet
import com.example.ui.home.UiState
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HijrahToolsScreen
import com.example.ui.screens.NavigationDirectoryScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguagePreferenceStore
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.LocalOnSelectLanguage
import com.example.ui.i18n.stringsForLanguage
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahNetwerkTheme
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

private const val ROUTE_TOOLS = "tools"
private const val ROUTE_GAMES = "games"
private const val ADMIN_EMAIL = "hijrahnetwerk@gmail.com"

class MainActivity : ComponentActivity() {
    private var pendingNotificationRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        HijrahNotificationManager.ensureChannels(this)
        pendingNotificationRoute = intent?.getStringExtra(HijrahNotificationManager.EXTRA_TARGET_ROUTE)
        setContent {
            val appContext = applicationContext
            val languageStore = remember(appContext) { LanguagePreferenceStore(appContext) }
            val currentLanguage by languageStore.languageFlow.collectAsStateWithLifecycle(
                initialValue = AppLanguage.DUTCH
            )
            val appStrings = remember(currentLanguage) { stringsForLanguage(currentLanguage) }
            val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            val scope = rememberCoroutineScope()

            CompositionLocalProvider(
                LocalAppLanguage provides currentLanguage,
                LocalAppStrings provides appStrings,
                LocalLayoutDirection provides layoutDirection,
                LocalOnSelectLanguage provides { chosen ->
                    scope.launch { languageStore.setLanguage(chosen) }
                }
            ) {
                HijrahNetwerkTheme {
                    HijrahAppRoot(
                        pendingNotificationRoute = pendingNotificationRoute,
                        onConsumeNotificationRoute = { pendingNotificationRoute = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val route = intent.getStringExtra(HijrahNotificationManager.EXTRA_TARGET_ROUTE)
        if (!route.isNullOrBlank()) {
            pendingNotificationRoute = route
        }
    }
}

@Composable
private fun HijrahAppRoot(
    pendingNotificationRoute: String?,
    onConsumeNotificationRoute: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isCheckingSession by remember { mutableStateOf(currentUser == null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                isCheckingSession = false
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            isCheckingSession = true
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    isCheckingSession = false
                    currentUser = Firebase.auth.currentUser
                },
                onUnauthenticated = {
                    isCheckingSession = false
                },
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        AuthWelcomeScreen(
            isCheckingSession = isCheckingSession,
            onAuthSuccess = {
                isCheckingSession = false
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        val uid = user.uid
        val defaultAlias = user.displayName
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.length >= 2 }
            ?: "Zuster"
        val isAdminUser = user.email.equals(ADMIN_EMAIL, ignoreCase = true) && user.isEmailVerified

        key(uid) {
            HijrahAuthenticatedShell(
                currentUserId = uid,
                userEmail = user.email,
                defaultAlias = defaultAlias,
                isAdminUser = isAdminUser,
                pendingNotificationRoute = pendingNotificationRoute,
                onConsumeNotificationRoute = onConsumeNotificationRoute,
                onSignOut = {
                    signOutWithCleanup(credentialManager, scope)
                }
            )
        }
    }
}

@Composable
private fun HijrahAuthenticatedShell(
    currentUserId: String,
    userEmail: String?,
    defaultAlias: String,
    isAdminUser: Boolean,
    pendingNotificationRoute: String?,
    onConsumeNotificationRoute: () -> Unit,
    onSignOut: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                HijrahRepository(app).let { repo ->
                    HomeViewModel(
                        repository = repo,
                        currentUserId = currentUserId,
                        defaultAlias = defaultAlias,
                        isAdminUser = isAdminUser
                    )
                }
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val checklistState by viewModel.checklistState.collectAsStateWithLifecycle()
    val budgetState by viewModel.budgetState.collectAsStateWithLifecycle()
    val documentsState by viewModel.documentsState.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val gameScoresState by viewModel.gameScoresState.collectAsStateWithLifecycle()
    val notificationPrefsState by viewModel.notificationPrefsState.collectAsStateWithLifecycle()
    val experiencesState by viewModel.approvedExperiencesState.collectAsStateWithLifecycle()
    val myExperiencesState by viewModel.myExperiencesState.collectAsStateWithLifecycle()
    val pendingAdminExperiencesState by viewModel.pendingAdminExperiencesState.collectAsStateWithLifecycle()
    val communityState by viewModel.communityPostsState.collectAsStateWithLifecycle()
    val selectedPostRepliesState by viewModel.selectedPostRepliesState.collectAsStateWithLifecycle()
    val isOfflineCacheMode by viewModel.isOfflineCacheMode.collectAsStateWithLifecycle()
    val savedOfflinePostIds by viewModel.savedOfflinePostIds.collectAsStateWithLifecycle()
    val isSavingProfile by viewModel.isSavingProfile.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: HijrahTab.HOME.route

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionMessage) {
        val msg = actionMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val strings = LocalAppStrings.current
    when (val currentProfileState = profileState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HijrahWhite),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        color = HijrahRust,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = strings.loadingProfile,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahTextSecondary
                    )
                }
            }
            return
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HijrahWhite)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.Person,
                        title = strings.profileLoadErrorTitle,
                        description = currentProfileState.message,
                        actionText = strings.retry,
                        onActionClick = viewModel::retryInitialization
                    )
                    HijrahSecondaryButton(
                        text = strings.signOut,
                        onClick = onSignOut
                    )
                }
            }
            return
        }

        is UiState.Success -> {
            val loadedProfile = currentProfileState.data
            if (loadedProfile == null || !loadedProfile.onboardingCompleted) {
                Scaffold(
                    containerColor = HijrahWhite,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = HijrahDarkBrown,
                                contentColor = HijrahWhite
                            )
                        }
                    }
                ) { regPadding ->
                    Box(modifier = Modifier.padding(regPadding)) {
                        RegistrationScreen(
                            defaultAlias = defaultAlias,
                            existingProfile = loadedProfile,
                            isSaving = isSavingProfile,
                            onSubmit = { alias, originCountry, currentCity, phase, focusCountry, focusCity ->
                                viewModel.saveOnboarding(
                                    displayAlias = alias,
                                    originCountry = originCountry,
                                    currentCity = currentCity,
                                    phase = phase,
                                    focusCountry = focusCountry,
                                    focusCity = focusCity
                                )
                            },
                            onSignOut = onSignOut
                        )
                    }
                }
                return
            }
        }
    }

    var showOnboardingSheet by remember { mutableStateOf(false) }
    var showShareExperienceSheet by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var selectedHighlight by remember { mutableStateOf<EditorialHighlight?>(null) }
    var discoverFilter by remember { mutableStateOf<String?>(null) }
    var reportTarget by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val profile = (profileState as? UiState.Success)?.data
    val checklist = (checklistState as? UiState.Success)?.data.orEmpty()
    val budgetItems = (budgetState as? UiState.Success)?.data.orEmpty()
    val documents = (documentsState as? UiState.Success)?.data.orEmpty()
    val notes = (notesState as? UiState.Success)?.data.orEmpty()
    val favorites = (favoritesState as? UiState.Success)?.data.orEmpty()
    val gameScores = (gameScoresState as? UiState.Success)?.data.orEmpty()
    val notificationPrefs = (notificationPrefsState as? UiState.Success)?.data
    val approvedExperiences = (experiencesState as? UiState.Success)?.data.orEmpty()
    val myExperiences = (myExperiencesState as? UiState.Success)?.data.orEmpty()

    LaunchedEffect(currentUserId, notificationPrefsState is UiState.Success) {
        if (notificationPrefsState is UiState.Success) {
            val prefs = notificationPrefs
            viewModel.syncNotificationPreferences(
                forumRepliesEnabled = prefs?.forumRepliesEnabled ?: true,
                milestoneRemindersEnabled = prefs?.milestoneRemindersEnabled ?: true,
                reminderCadence = prefs?.cadenceEnum() ?: ReminderCadence.WEEKLY,
                silent = true
            )
        }
    }

    val navigateToRoute: (String, String?) -> Unit = { route, label ->
        if (!label.isNullOrBlank()) {
            viewModel.recordLastVisited(label, route)
        }
        if (route == HijrahTab.DISCOVER.route && !label.isNullOrBlank()) {
            discoverFilter = label
        }
        navController.navigate(route) {
            popUpTo(HijrahTab.HOME.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(pendingNotificationRoute) {
        val target = pendingNotificationRoute
        if (!target.isNullOrBlank()) {
            navigateToRoute(target, null)
            onConsumeNotificationRoute()
        }
    }

    Scaffold(
        containerColor = HijrahWhite,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = HijrahDarkBrown,
                    contentColor = HijrahWhite
                )
            }
        },
        bottomBar = {
            if (currentRoute != ROUTE_TOOLS && currentRoute != ROUTE_GAMES) {
                HijrahBottomNavBar(
                    currentRoute = currentRoute,
                    onSelectTab = { tab ->
                        if (tab == HijrahTab.DISCOVER) discoverFilter = null
                        navigateToRoute(tab.route, null)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            NavHost(
                navController = navController,
                startDestination = HijrahTab.HOME.route
            ) {
                composable(HijrahTab.HOME.route) {
                    HomeScreen(
                        profileState = profileState,
                        checklistState = checklistState,
                        favoritesState = favoritesState,
                        experiencesState = experiencesState,
                        myExperiencesState = myExperiencesState,
                        communityState = communityState,
                        onNavigateToRoute = navigateToRoute,
                        onSelectPhase = viewModel::selectPhase,
                        onToggleStep = viewModel::toggleChecklistStep,
                        onToggleFavorite = { item, saved ->
                            viewModel.toggleFavorite(
                                targetType = "GUIDE",
                                targetId = item.id,
                                title = item.title,
                                subtitle = item.category,
                                currentlySaved = saved
                            )
                        },
                        onOpenOnboarding = { showOnboardingSheet = true },
                        onOpenShareExperience = { showShareExperienceSheet = true },
                        onOpenHighlightDetail = { selectedHighlight = it },
                        onOpenSearch = { showSearchSheet = true },
                        onOpenNotifications = { showNotificationsSheet = true },
                        onSelectTopic = { topic ->
                            discoverFilter = topic
                        }
                    )
                }

                composable(HijrahTab.DISCOVER.route) {
                    DiscoverScreen(
                        initialFilter = discoverFilter,
                        favorites = favorites,
                        onToggleFavorite = { item, saved ->
                            viewModel.toggleFavorite(
                                targetType = "GUIDE",
                                targetId = item.id,
                                title = item.title,
                                subtitle = item.category,
                                currentlySaved = saved
                            )
                        },
                        onOpenHighlight = { selectedHighlight = it },
                        onOpenTools = { navigateToRoute(ROUTE_TOOLS, "HijrahTools Planner") },
                        onOpenGames = { navigateToRoute(ROUTE_GAMES, "Hijrah Spellen") },
                        onQuizCompleted = { score, passed ->
                            viewModel.recordGameResult(HijrahGameId.QUIZ_ARENA, score, passed)
                        }
                    )
                }

                composable(HijrahTab.NAVIGATION.route) {
                    NavigationDirectoryScreen(
                        focusCountry = profile?.focusCountry.orEmpty(),
                        favorites = favorites,
                        approvedExperiences = approvedExperiences,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onOpenShareExperience = { showShareExperienceSheet = true }
                    )
                }

                composable(HijrahTab.COMMUNITY.route) {
                    CommunityScreen(
                        currentUserId = currentUserId,
                        communityState = communityState,
                        approvedExperiencesState = experiencesState,
                        pendingAdminState = pendingAdminExperiencesState,
                        repliesState = selectedPostRepliesState,
                        favorites = favorites,
                        authorAlias = profile?.displayAlias ?: defaultAlias,
                        isAdminUser = isAdminUser,
                        savedOfflinePostIds = savedOfflinePostIds,
                        isOfflineMode = isOfflineCacheMode,
                        onToggleSaveOffline = viewModel::toggleSaveForumPostOffline,
                        onCreatePost = { group, title, body, onDone ->
                            viewModel.createCommunityPost(
                                authorAlias = profile?.displayAlias ?: defaultAlias,
                                groupTitle = group,
                                title = title,
                                body = body,
                                onComplete = onDone
                            )
                        },
                        onUpdatePost = viewModel::updateCommunityPost,
                        onDeletePost = viewModel::deleteCommunityPost,
                        onSelectPostForReplies = viewModel::selectPostForReplies,
                        onAddReply = { postId, body, onDone ->
                            viewModel.addCommunityReply(
                                postId = postId,
                                authorAlias = profile?.displayAlias ?: defaultAlias,
                                body = body,
                                onComplete = onDone
                            )
                        },
                        onOpenShareExperience = { showShareExperienceSheet = true },
                        onToggleFavoriteExperience = { exp, saved ->
                            viewModel.toggleFavorite(
                                targetType = "EXPERIENCE",
                                targetId = exp.id,
                                title = exp.title,
                                subtitle = "${exp.countryName} • ${exp.topic}",
                                currentlySaved = saved
                            )
                        },
                        onModerateExperience = viewModel::moderateExperience,
                        onReportContent = { targetType, targetId, label ->
                            reportTarget = Triple(targetType, targetId, label)
                        }
                    )
                }

                composable(HijrahTab.PROFILE.route) {
                    UserProfileScreen(
                        profileState = profileState,
                        userEmail = userEmail,
                        favorites = favorites,
                        myExperiences = myExperiences,
                        checklist = checklist,
                        documents = documents,
                        budgetItems = budgetItems,
                        isOfflineMode = isOfflineCacheMode,
                        onRetryProfile = viewModel::retryInitialization,
                        onEditProfile = { showOnboardingSheet = true },
                        onToggleChecklistStep = viewModel::toggleChecklistStep,
                        onAddChecklistStep = viewModel::addChecklistStep,
                        onCycleDocumentStatus = viewModel::cycleDocumentStatus,
                        onRemoveFavorite = { fav ->
                            viewModel.toggleFavorite(
                                targetType = fav.targetType,
                                targetId = fav.targetId,
                                title = fav.title,
                                subtitle = fav.subtitle,
                                currentlySaved = true
                            )
                        },
                        onOpenTools = { navigateToRoute(ROUTE_TOOLS, "HijrahTools") },
                        onOpenNotifications = { showNotificationsSheet = true },
                        onSignOut = onSignOut
                    )
                }

                composable(ROUTE_TOOLS) {
                    HijrahToolsScreen(
                        checklist = checklist,
                        budgetItems = budgetItems,
                        documents = documents,
                        notes = notes,
                        favorites = favorites,
                        onBack = { navController.popBackStack() },
                        onToggleStep = viewModel::toggleChecklistStep,
                        onAddStep = viewModel::addChecklistStep,
                        onDeleteStep = viewModel::deleteChecklistStep,
                        onLoadArrivalPreset = viewModel::loadArrivalPreset,
                        onAddBudget = viewModel::addBudgetItem,
                        onDeleteBudget = viewModel::deleteBudgetItem,
                        onAddDocument = viewModel::addDocumentItem,
                        onCycleDocumentStatus = viewModel::cycleDocumentStatus,
                        onDeleteDocument = viewModel::deleteDocumentItem,
                        onSaveNote = viewModel::saveNote,
                        onDeleteNote = viewModel::deleteNote,
                        onQuizCompleted = { score, passed ->
                            viewModel.recordGameResult(HijrahGameId.QUIZ_ARENA, score, passed)
                        }
                    )
                }

                composable(ROUTE_GAMES) {
                    HijrahGamesHubScreen(
                        gameScores = gameScores,
                        onRecordGameResult = viewModel::recordGameResult,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    if (showOnboardingSheet) {
        OnboardingModalSheet(
            currentProfile = profile,
            onDismiss = { showOnboardingSheet = false },
            onSave = { alias, originCountry, currentCity, phase, country, city ->
                viewModel.saveOnboarding(alias, originCountry, currentCity, phase, country, city) {
                    showOnboardingSheet = false
                }
            }
        )
    }

    if (showShareExperienceSheet) {
        ShareExperienceModalSheet(
            defaultAlias = profile?.displayAlias ?: defaultAlias,
            onDismiss = { showShareExperienceSheet = false },
            onSubmit = { alias, country, city, topic, title, story, prosAndCons, lessonLearned, tip ->
                viewModel.submitExperience(
                    authorAlias = alias,
                    countryName = country,
                    cityName = city,
                    topic = topic,
                    title = title,
                    story = story,
                    prosAndCons = prosAndCons,
                    lessonLearned = lessonLearned,
                    practicalTip = tip,
                    onComplete = { showShareExperienceSheet = false }
                )
            }
        )
    }

    selectedHighlight?.let { highlight ->
        HighlightDetailSheet(
            highlight = highlight,
            onDismiss = { selectedHighlight = null },
            onOpenTargetRoute = { route ->
                selectedHighlight = null
                navigateToRoute(route, highlight.title)
            }
        )
    }

    if (showSearchSheet) {
        QuickSearchSheet(
            onDismiss = { showSearchSheet = false },
            onSelectHighlight = { highlight ->
                showSearchSheet = false
                selectedHighlight = highlight
            },
            onOpenRoute = { route ->
                showSearchSheet = false
                navigateToRoute(route, null)
            }
        )
    }

    if (showNotificationsSheet) {
        NotificationsSheet(
            myExperiences = myExperiences,
            onboardingCompleted = profile?.onboardingCompleted == true,
            openStepsCount = checklist.count { !it.completed },
            pendingDocsCount = documents.count { it.statusEnum() != DocumentStatus.GEREED },
            notificationPrefs = notificationPrefs,
            onUpdateNotificationPrefs = { forumReplies, milestoneReminders, cadence ->
                viewModel.syncNotificationPreferences(
                    forumRepliesEnabled = forumReplies,
                    milestoneRemindersEnabled = milestoneReminders,
                    reminderCadence = cadence
                )
            },
            onDismiss = { showNotificationsSheet = false },
            onOpenOnboarding = { showOnboardingSheet = true }
        )
    }

    reportTarget?.let { (targetType, targetId, label) ->
        ReportModerationSheet(
            targetLabel = label,
            onDismiss = { reportTarget = null },
            onSubmitReport = { reason ->
                viewModel.submitReport(targetType, targetId, reason) {
                    reportTarget = null
                }
            }
        )
    }
}

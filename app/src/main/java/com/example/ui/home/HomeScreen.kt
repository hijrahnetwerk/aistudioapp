package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChecklistItem
import com.example.data.CommunityPost
import com.example.data.EditorialCatalog
import com.example.data.EditorialHighlight
import com.example.data.ExperienceSubmission
import com.example.data.HijrahPhase
import com.example.data.SavedFavorite
import com.example.data.UserProfile
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahReliabilityBadge
import com.example.ui.components.HijrahSectionHeader
import com.example.ui.components.HijrahTopBar
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedSubtitle
import com.example.ui.i18n.localizedTitle
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahErrorRust
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahRustTint
import com.example.ui.theme.HijrahShadowWarm
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun HomeScreen(
    profileState: UiState<UserProfile?>,
    checklistState: UiState<List<ChecklistItem>>,
    favoritesState: UiState<List<SavedFavorite>>,
    experiencesState: UiState<List<ExperienceSubmission>>,
    myExperiencesState: UiState<List<ExperienceSubmission>>,
    communityState: UiState<List<CommunityPost>>,
    onNavigateToRoute: (String, String?) -> Unit,
    onSelectPhase: (HijrahPhase) -> Unit,
    onToggleStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onToggleFavorite: (EditorialHighlight, Boolean) -> Unit,
    onOpenOnboarding: () -> Unit,
    onOpenShareExperience: () -> Unit,
    onOpenHighlightDetail: (EditorialHighlight) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSelectTopic: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = (profileState as? UiState.Success)?.data
    val checklist = (checklistState as? UiState.Success)?.data.orEmpty()
    val favorites = (favoritesState as? UiState.Success)?.data.orEmpty()
    val myExperiences = (myExperiencesState as? UiState.Success)?.data.orEmpty()
    val pendingSubmissionsCount = myExperiences.count { it.status == "PENDING" }

    val strings = LocalAppStrings.current
    var selectedTopic by remember { mutableStateOf(EditorialCatalog.recommendedTopics.first()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HijrahWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .testTag("home_scroll_list"),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HijrahTopBar(
                    onSearchClick = onOpenSearch,
                    onNotificationsClick = onOpenNotifications,
                    hasPendingNotice = pendingSubmissionsCount > 0 || profile?.onboardingCompleted == false
                )
            }

            // 1. Welkomstgedeelte + Persoonlijk Profielbanner
            item {
                WelcomeSection(
                    profile = profile,
                    isLoading = profileState is UiState.Loading,
                    onOpenOnboarding = onOpenOnboarding
                )
            }

            // 2. Architecturale Hero Card
            item {
                EditorialHeroCard(
                    currentPhase = profile?.phaseEnum() ?: HijrahPhase.ORIENTATIE,
                    focusCountry = profile?.focusCountry.orEmpty(),
                    onContinueClick = {
                        val route = profile?.lastVisitedRoute?.ifBlank { "tools" } ?: "tools"
                        onNavigateToRoute(route, null)
                    }
                )
            }

            // 3. Persoonlijke Voortgang & Verdergaan
            item {
                PersonalJourneyCard(
                    profile = profile,
                    checklist = checklist,
                    checklistState = checklistState,
                    onSelectPhase = onSelectPhase,
                    onToggleStep = onToggleStep,
                    onOpenTools = { onNavigateToRoute("tools", "HijrahTools: Persoonlijke planner") },
                    onContinueLastVisited = {
                        val route = profile?.lastVisitedRoute?.ifBlank { "discover" } ?: "discover"
                        onNavigateToRoute(route, null)
                    }
                )
            }

            // 4. Asymmetrische Snelkoppelingen Compositie
            item {
                QuickShortcutsComposition(
                    onOpenNavigation = { onNavigateToRoute("navigation", "Hijrah Navigatie") },
                    onOpenTools = { onNavigateToRoute("tools", "HijrahTools Checklist") },
                    onOpenDiscover = { onNavigateToRoute("discover", "Kennisbank & Gidsen") },
                    onOpenShareExperience = onOpenShareExperience,
                    onOpenGames = { onNavigateToRoute("games", "Hijrah Spellen") }
                )
            }

            // 5. Uitgelichte Praktische Informatie
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HijrahSectionHeader(
                        title = strings.featuredTitle,
                        subtitle = strings.featuredSub,
                        actionLabel = strings.viewAll,
                        onActionClick = { onNavigateToRoute("discover", "Kennisbank") },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(EditorialCatalog.featuredHighlights, key = { it.id }) { highlight ->
                            val isSaved = favorites.any { it.targetId == highlight.id }
                            FeaturedHighlightCard(
                                highlight = highlight,
                                isSaved = isSaved,
                                onToggleSave = { onToggleFavorite(highlight, isSaved) },
                                onClick = { onOpenHighlightDetail(highlight) }
                            )
                        }
                    }
                }
            }

            // 6. Recente Ervaringen (Eerlijke Firestore-stream + verzorgde lege toestand)
            item {
                RecentExperiencesSection(
                    experiencesState = experiencesState,
                    pendingCount = pendingSubmissionsCount,
                    onOpenShareExperience = onOpenShareExperience,
                    onOpenCommunity = { onNavigateToRoute("community", "Ervaringen & Community") }
                )
            }

            // 7. Community-updates & Aanbevolen Onderwerpen
            item {
                CommunityAndTopicsSection(
                    communityState = communityState,
                    selectedTopic = selectedTopic,
                    onSelectTopic = { topic ->
                        selectedTopic = topic
                        onSelectTopic(topic)
                    },
                    onOpenCommunity = { onNavigateToRoute("community", "Community-groepen") },
                    onOpenDiscoverTopic = { onNavigateToRoute("discover", selectedTopic) }
                )
            }
        }
    }
}

@Composable
private fun WelcomeSection(
    profile: UserProfile?,
    isLoading: Boolean,
    onOpenOnboarding: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "اَلسَّلَامُ عَلَيْكُمْ",
                    style = HijrahArabicStyles.AmiriSubtitle,
                    color = HijrahBrown
                )
                val greetingName = profile?.displayAlias?.takeIf { it.isNotBlank() } ?: strings.sisterDefault
                Text(
                    text = if (isLoading) strings.welcomeBack else "${strings.welcomePrefix} $greetingName",
                    style = MaterialTheme.typography.displayMedium,
                    color = HijrahDarkBrown
                )
            }

            Surface(
                shape = HijrahRadii.Chip,
                color = HijrahWhite,
                border = BorderStroke(1.dp, HijrahBorderSubtle),
                modifier = Modifier
                    .clip(HijrahRadii.Chip)
                    .clickable(onClick = onOpenOnboarding)
                    .testTag("edit_orientation_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = strings.trajectoryChip,
                        tint = HijrahBrown,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.trajectoryChip,
                        style = MaterialTheme.typography.labelMedium,
                        color = HijrahDarkBrown
                    )
                }
            }
        }

        if (profile != null && !profile.onboardingCompleted) {
            Surface(
                shape = HijrahRadii.Input,
                color = HijrahGoldTint,
                border = BorderStroke(1.dp, HijrahBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(HijrahRadii.Input)
                    .clickable(onClick = onOpenOnboarding)
                    .testTag("complete_onboarding_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.personalizeBannerTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = strings.personalizeBannerSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = HijrahBrown
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorialHeroCard(
    currentPhase: HijrahPhase,
    focusCountry: String,
    onContinueClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(216.dp)
            .shadow(14.dp, HijrahRadii.HeroCard, ambientColor = HijrahShadowWarm, spotColor = HijrahShadowWarm)
            .clip(HijrahRadii.HeroCard)
            .border(1.dp, HijrahBorderGold, HijrahRadii.HeroCard)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_home_hero),
            contentDescription = strings.appName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xE84F3A23),
                            Color(0xB84F3A23),
                            Color(0x3D4F3A23)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = HijrahRadii.Chip,
                    color = Color(0x33FFFFFF),
                    border = BorderStroke(1.dp, HijrahBorderGold)
                ) {
                    Text(
                        text = currentPhase.localizedTitle(strings).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                        color = HijrahGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                if (focusCountry.isNotBlank() && focusCountry != "Nog aan het verkennen") {
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = focusCountry,
                            style = MaterialTheme.typography.labelSmall,
                            color = HijrahWhite,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = strings.heroTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = HijrahWhite,
                    modifier = Modifier.widthIn(max = 260.dp)
                )
                HijrahPrimaryButton(
                    text = strings.heroContinueCta,
                    onClick = onContinueClick,
                    leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    modifier = Modifier.testTag("hero_continue_button")
                )
            }
        }
    }
}

@Composable
private fun PersonalJourneyCard(
    profile: UserProfile?,
    checklist: List<ChecklistItem>,
    checklistState: UiState<List<ChecklistItem>>,
    onSelectPhase: (HijrahPhase) -> Unit,
    onToggleStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onOpenTools: () -> Unit,
    onContinueLastVisited: () -> Unit
) {
    val strings = LocalAppStrings.current
    val activePhase = profile?.phaseEnum() ?: HijrahPhase.ORIENTATIE
    val completedCount = checklist.count { it.completed }
    val totalCount = checklist.size.coerceAtLeast(1)
    val progressRatio = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
    val nextOpenStep = checklist.firstOrNull { !it.completed }

    HijrahElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.personalProgressTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = HijrahDarkBrown
                )
                Text(
                    text = activePhase.localizedSubtitle(strings),
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextMuted
                )
            }
            Text(
                text = "$completedCount/${checklist.size}",
                style = MaterialTheme.typography.titleLarge,
                color = HijrahRust
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive 4-phase stepper
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HijrahPhase.entries.forEach { phase ->
                val isSelected = phase == activePhase
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (isSelected) HijrahDarkBrown else HijrahSurfaceTinted,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) HijrahBorderGold else HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .clip(HijrahRadii.Chip)
                        .clickable { onSelectPhase(phase) }
                        .testTag("phase_pill_${phase.name.lowercase()}")
                ) {
                    Text(
                        text = phase.localizedTitle(strings),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = if (isSelected) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { progressRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(HijrahRadii.Pill),
            color = HijrahRust,
            trackColor = HijrahSurfaceSoftBrown,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(14.dp))

        when (checklistState) {
            is UiState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = HijrahBrown,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = strings.loadingChecklist,
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextMuted
                    )
                }
            }

            is UiState.Error -> {
                Text(
                    text = checklistState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahErrorRust
                )
            }

            is UiState.Success -> {
                if (nextOpenStep != null) {
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahSurfaceTinted,
                        border = BorderStroke(1.dp, HijrahBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(HijrahRadii.Input)
                            .clickable { onToggleStep(nextOpenStep, checklist) }
                            .testTag("home_next_step_row")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = strings.statusNotStarted,
                                tint = HijrahRust,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.nextStepInTools,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HijrahBrown
                                )
                                Text(
                                    text = nextOpenStep.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = HijrahDarkBrown
                                )
                            }
                        }
                    }
                } else if (checklist.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = HijrahGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = strings.allStepsDone,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahDarkBrown
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Verdergaan waar je gebleven bent
        Surface(
            shape = HijrahRadii.Input,
            color = HijrahWhite,
            border = BorderStroke(1.dp, HijrahBorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .clip(HijrahRadii.Input)
                .clickable(onClick = onContinueLastVisited)
                .testTag("continue_last_visited_card")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahRustTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = HijrahRust,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strings.continueWhereLeft,
                            style = MaterialTheme.typography.labelSmall,
                            color = HijrahTextMuted
                        )
                        Text(
                            text = profile?.lastVisitedTitle?.ifBlank { strings.openInitialOrientation }
                                ?: strings.openInitialOrientation,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = strings.toolsCardTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = HijrahRust,
                    modifier = Modifier
                        .clip(HijrahRadii.Chip)
                        .clickable(onClick = onOpenTools)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickShortcutsComposition(
    onOpenNavigation: () -> Unit,
    onOpenTools: () -> Unit,
    onOpenDiscover: () -> Unit,
    onOpenShareExperience: () -> Unit,
    onOpenGames: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HijrahSectionHeader(
            title = strings.shortcutsTitle,
            subtitle = strings.shortcutsSub
        )

        // Asymmetric 2-column bento composition
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left tall featured Dark Brown card: Hijrah Navigatie
            HijrahDarkCard(
                modifier = Modifier
                    .weight(1f)
                    .height(196.dp)
                    .testTag("shortcut_navigation_card"),
                onClick = onOpenNavigation,
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(HijrahRadii.Input)
                            .background(Color(0x26C9A227))
                            .border(1.dp, HijrahBorderGold, HijrahRadii.Input),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Map,
                            contentDescription = null,
                            tint = HijrahGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = strings.navCardTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = HijrahWhite
                        )
                        Text(
                            text = strings.navCardSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xCCFFFFFF)
                        )
                    }
                }
            }

            // Right stacked cards: HijrahTools + Ervaring delen
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HijrahElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .testTag("shortcut_tools_card"),
                    onClick = onOpenTools,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahRustTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Checklist,
                                contentDescription = null,
                                tint = HijrahRust,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.toolsCardTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = HijrahDarkBrown
                            )
                            Text(
                                text = strings.toolsCardSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahTextMuted
                            )
                        }
                    }
                }

                HijrahElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .testTag("shortcut_share_experience_card"),
                    onClick = onOpenShareExperience,
                    borderColor = HijrahBorderGold,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahGoldTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                tint = HijrahBrown,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.shareExpTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = HijrahDarkBrown
                            )
                            Text(
                                text = strings.shareExpSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Full-width compact Kennisbank strip
        HijrahElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shortcut_discover_card"),
            onClick = onOpenDiscover,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahSurfaceTinted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = HijrahBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strings.knowledgeStripTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = strings.knowledgeStripSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextMuted
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = HijrahBrown
                )
            }
        }

        // Full-width Hijrah Spellen strip (kinderen & volwassenen)
        HijrahElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shortcut_games_card"),
            onClick = onOpenGames,
            borderColor = HijrahBorderGold,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahRustTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SportsEsports,
                            contentDescription = null,
                            tint = HijrahRust,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strings.gamesStripTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = strings.gamesStripSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextMuted
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = HijrahRust
                )
            }
        }
    }
}

@Composable
private fun FeaturedHighlightCard(
    highlight: EditorialHighlight,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    HijrahElevatedCard(
        modifier = Modifier
            .width(292.dp)
            .testTag("highlight_card_${highlight.id}"),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (highlight.imageRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                Image(
                    painter = painterResource(id = highlight.imageRes),
                    contentDescription = highlight.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xCCFFFFFF))
                        .testTag("bookmark_highlight_${highlight.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Verwijderen uit favorieten" else "Opslaan als favoriet",
                        tint = if (isSaved) HijrahRust else HijrahDarkBrown
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HijrahReliabilityBadge(label = highlight.reliabilityLabel)
                if (highlight.imageRes == null) {
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("bookmark_highlight_${highlight.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Verwijderen uit favorieten" else "Opslaan als favoriet",
                            tint = if (isSaved) HijrahRust else HijrahBrown
                        )
                    }
                }
            }

            Text(
                text = highlight.category.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.7.sp),
                color = HijrahRust
            )
            Text(
                text = highlight.title,
                style = MaterialTheme.typography.titleMedium,
                color = HijrahDarkBrown,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = highlight.summary,
                style = MaterialTheme.typography.bodySmall,
                color = HijrahTextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = HijrahTextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = highlight.lastCheckedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahTextMuted
                )
            }
        }
    }
}

@Composable
private fun RecentExperiencesSection(
    experiencesState: UiState<List<ExperienceSubmission>>,
    pendingCount: Int,
    onOpenShareExperience: () -> Unit,
    onOpenCommunity: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HijrahSectionHeader(
            title = strings.recentExpTitle,
            subtitle = strings.recentExpSub,
            actionLabel = strings.shareExpTitle,
            onActionClick = onOpenShareExperience
        )

        if (pendingCount > 0) {
            Surface(
                shape = HijrahRadii.Input,
                color = HijrahGoldTint,
                border = BorderStroke(1.dp, HijrahBorderGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Je hebt $pendingCount ingezonden ervaring(en) in beoordeling bij beheer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahDarkBrown,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        when (experiencesState) {
            is UiState.Loading -> {
                HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = HijrahBrown,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Goedgekeurde ervaringen ophalen...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahTextSecondary
                        )
                    }
                }
            }

            is UiState.Error -> {
                HijrahEmptyStateCard(
                    icon = Icons.Rounded.Explore,
                    title = "Ervaringen tijdelijk niet beschikbaar",
                    description = experiencesState.message,
                    actionText = "Deel jouw ervaring",
                    onActionClick = onOpenShareExperience
                )
            }

            is UiState.Success -> {
                val items = experiencesState.data
                if (items.isEmpty()) {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.EditNote,
                        title = "Nog geen gepubliceerde ervaringen",
                        description = "Ervaringen worden uitsluitend na beoordeling door beheer zichtbaar, zonder verzonnen verhalen. Deel als eerste jouw praktijkervaring.",
                        actionText = "Ervaring insturen",
                        onActionClick = onOpenShareExperience
                    )
                } else {
                    items.take(3).forEach { exp ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            HijrahElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onOpenCommunity
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = listOfNotNull(
                                            exp.countryName.takeIf { it.isNotBlank() },
                                            exp.cityName.takeIf { it.isNotBlank() }
                                        ).joinToString(" • "),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = HijrahRust
                                    )
                                    HijrahReliabilityBadge(label = "Gecontroleerd")
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = exp.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HijrahDarkBrown
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = exp.story,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HijrahTextSecondary,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (exp.practicalTip.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = HijrahRadii.Chip,
                                        color = HijrahSurfaceTinted,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Praktische tip: ${exp.practicalTip}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HijrahBrown,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityAndTopicsSection(
    communityState: UiState<List<CommunityPost>>,
    selectedTopic: String,
    onSelectTopic: (String) -> Unit,
    onOpenCommunity: () -> Unit,
    onOpenDiscoverTopic: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HijrahSectionHeader(
            title = strings.communityUpdatesTitle,
            subtitle = strings.communityUpdatesSub,
            actionLabel = strings.viewAll,
            onActionClick = onOpenDiscoverTopic,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditorialCatalog.recommendedTopics.forEach { topic ->
                val selected = topic == selectedTopic
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (selected) HijrahRust else HijrahWhite,
                    border = BorderStroke(
                        1.dp,
                        if (selected) HijrahRust else HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .clip(HijrahRadii.Chip)
                        .clickable { onSelectTopic(topic) }
                        .testTag("topic_chip_${topic.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HijrahSectionHeader(
                title = strings.forumHeaderTitle,
                subtitle = strings.forumHeaderSub,
                actionLabel = strings.openForum,
                onActionClick = onOpenCommunity
            )

            when (communityState) {
                is UiState.Loading -> {
                    HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Community-updates laden...",
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextMuted
                        )
                    }
                }

                is UiState.Error -> {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.Forum,
                        title = "Community tijdelijk niet bereikbaar",
                        description = communityState.message
                    )
                }

                is UiState.Success -> {
                    val posts = communityState.data
                    if (posts.isEmpty()) {
                        HijrahEmptyStateCard(
                            icon = Icons.Rounded.Forum,
                            title = "Nog geen berichten in de groepen",
                            description = "Stel een praktische vraag over $selectedTopic in een besloten land- of onderwerpgroep.",
                            actionText = "Open Community",
                            onActionClick = onOpenCommunity
                        )
                    } else {
                        posts.take(3).forEach { post ->
                            HijrahElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onOpenCommunity
                            ) {
                                Text(
                                    text = "${post.groupTitle} • ${post.authorAlias}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HijrahBrown
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HijrahDarkBrown
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HijrahTextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

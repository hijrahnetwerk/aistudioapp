package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.BudgetItem
import com.example.data.ChecklistItem
import com.example.data.DocumentItem
import com.example.data.ExperienceSubmission
import com.example.data.HijrahPhase
import com.example.data.SavedFavorite
import com.example.data.UserProfile
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahSecondaryButton
import com.example.ui.components.HijrahSectionHeader
import com.example.ui.components.LanguageSelectorCard
import com.example.ui.components.MigrationProgressTrackerCard
import com.example.ui.home.UiState
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedSubtitle
import com.example.ui.i18n.localizedTitle
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahRustTint
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite
import java.text.SimpleDateFormat
import java.util.Locale

private data class PassportBadge(
    val title: String,
    val description: String,
    val earned: Boolean,
    val icon: ImageVector
)

@Composable
fun UserProfileScreen(
    profileState: UiState<UserProfile?>,
    userEmail: String?,
    favorites: List<SavedFavorite>,
    myExperiences: List<ExperienceSubmission>,
    checklist: List<ChecklistItem>,
    documents: List<DocumentItem>,
    budgetItems: List<BudgetItem>,
    isOfflineMode: Boolean = false,
    onRetryProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onToggleChecklistStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onAddChecklistStep: (HijrahPhase, String, String, Int) -> Unit,
    onCycleDocumentStatus: (DocumentItem) -> Unit,
    onRemoveFavorite: (SavedFavorite) -> Unit,
    onOpenTools: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedSteps = checklist.count { it.completed }
    val totalSteps = checklist.size
    val budgetItemsCount = budgetItems.size
    var showSignOutConfirm by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current

    when (profileState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(HijrahWhite)
                    .testTag("user_profile_loading"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = HijrahRust, modifier = Modifier.size(34.dp), strokeWidth = 3.dp)
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
                modifier = modifier
                    .fillMaxSize()
                    .background(HijrahWhite)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.Person,
                        title = strings.profileLoadErrorTitle,
                        description = profileState.message,
                        actionText = strings.retry,
                        onActionClick = onRetryProfile
                    )
                    HijrahSecondaryButton(
                        text = strings.signOut,
                        onClick = { showSignOutConfirm = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            return
        }

        is UiState.Success -> {
            val profile = profileState.data
            if (profile == null) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(HijrahWhite)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.Person,
                        title = strings.profileNotFilledTitle,
                        description = strings.profileNotFilledDesc,
                        actionText = strings.profileFillCta,
                        onActionClick = onEditProfile
                    )
                }
                return
            }

            val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("nl", "NL")) }
            val memberSince = profile.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: "Onlangs aangemeld"
            val badges = remember(profile, completedSteps, myExperiences, budgetItemsCount) {
                listOf(
                    PassportBadge(
                        title = "Trajectgekozen",
                        description = "Oriëntatiefase ingesteld",
                        earned = profile.onboardingCompleted,
                        icon = Icons.Rounded.Verified
                    ),
                    PassportBadge(
                        title = "Eerste Stap",
                        description = "Minstens 1 checklist-stap afgevinkt",
                        earned = completedSteps >= 1,
                        icon = Icons.Rounded.CheckCircle
                    ),
                    PassportBadge(
                        title = "Financieel Overzicht",
                        description = "Begrotingspost in HijrahTools",
                        earned = budgetItemsCount >= 1,
                        icon = Icons.Rounded.AutoAwesome
                    ),
                    PassportBadge(
                        title = "Verhaal Gedeeld",
                        description = "Ervaring ingestuurd voor zusters",
                        earned = myExperiences.isNotEmpty(),
                        icon = Icons.Rounded.EditNote
                    )
                )
            }

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
                        .testTag("user_profile_list"),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(strings.profileHeader, style = MaterialTheme.typography.displayMedium, color = HijrahDarkBrown)
                        Text(
                            strings.profileSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahTextSecondary
                        )
                    }

                    // Prominent Language Selector Card (NL | AR | EN)
                    item {
                        LanguageSelectorCard()
                    }

                    if (isOfflineMode) {
                        item {
                            Surface(
                                shape = HijrahRadii.Input,
                                color = HijrahGoldTint,
                                border = BorderStroke(1.dp, HijrahBorderGold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_offline_cache_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CloudOff,
                                        contentDescription = strings.offlineProfileBanner,
                                        tint = HijrahBrown,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = strings.offlineProfileBanner,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HijrahDarkBrown
                                    )
                                }
                            }
                        }
                    }

                    // Digitaal Trajectpaspoort
                    item {
                        HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.passportBadgeTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HijrahGold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = profile.displayAlias.ifBlank { strings.sisterDefault },
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = HijrahWhite
                                    )
                                    Text(
                                        text = "جواز الهجرة",
                                        style = HijrahArabicStyles.KufiAccent,
                                        color = HijrahGold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.Badge,
                                    contentDescription = null,
                                    tint = HijrahGold,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                shape = HijrahRadii.Input,
                                color = HijrahWhite.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, HijrahBorderGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(strings.passportPhaseLabel, style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                            Text(profile.phaseEnum().localizedTitle(strings), style = MaterialTheme.typography.titleSmall, color = HijrahWhite)
                                        }
                                        Column {
                                            Text(strings.passportFocusLabel, style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                            Text(
                                                profile.focusCountry.ifBlank { strings.passportExploring },
                                                style = MaterialTheme.typography.titleSmall,
                                                color = HijrahWhite
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(strings.passportStepsLabel, style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                            Text("$completedSteps/$totalSteps", style = MaterialTheme.typography.titleSmall, color = HijrahWhite)
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(strings.passportOriginLabel, style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                            Text(
                                                profile.originCountry.ifBlank { strings.passportToBeFilled },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = HijrahWhite
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text(strings.passportResidenceLabel, style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                            Text(
                                                profile.currentCity.ifBlank { strings.passportToBeFilled },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = HijrahWhite
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${strings.passportStampsTitle} (${badges.count { it.earned }}/${badges.size})", style = MaterialTheme.typography.labelMedium, color = HijrahGold)
                                badges.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        pair.forEach { badge ->
                                            Surface(
                                                shape = HijrahRadii.Input,
                                                color = if (badge.earned) HijrahWhite.copy(alpha = 0.14f) else HijrahWhite.copy(alpha = 0.04f),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (badge.earned) HijrahGold else HijrahWhite.copy(alpha = 0.16f)
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = badge.icon,
                                                        contentDescription = null,
                                                        tint = if (badge.earned) HijrahGold else HijrahWhite.copy(alpha = 0.4f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = badge.title,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = if (badge.earned) HijrahWhite else HijrahWhite.copy(alpha = 0.55f)
                                                        )
                                                        Text(
                                                            text = badge.description,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = HijrahWhite.copy(alpha = 0.65f)
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

                    // Visuele Migratie Voortgangstracker
                    item {
                        MigrationProgressTrackerCard(
                            currentPhase = profile.phaseEnum(),
                            checklist = checklist,
                            documents = documents,
                            budgetItems = budgetItems,
                            favorites = favorites,
                            onToggleChecklistStep = onToggleChecklistStep,
                            onAddChecklistStep = onAddChecklistStep,
                            onCycleDocumentStatus = onCycleDocumentStatus,
                            onOpenTools = onOpenTools
                        )
                    }

                    // Gestructureerd overzicht van opgeslagen Firestore-gegevens
                    item {
                        HijrahElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_profile_details_card"),
                            borderColor = HijrahBorderGold
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.profileDetailsTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = HijrahDarkBrown
                                    )
                                    Text(
                                        text = if (isOfflineMode) {
                                            strings.profileDetailsOffline
                                        } else {
                                            strings.profileDetailsLive
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HijrahTextMuted
                                    )
                                }
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = HijrahRustTint
                                ) {
                                    TextButton(
                                        onClick = onEditProfile,
                                        modifier = Modifier.testTag("edit_user_profile_button")
                                    ) {
                                        Icon(Icons.Rounded.Edit, contentDescription = null, tint = HijrahRust, modifier = Modifier.size(16.dp))
                                        Text("  ${strings.editButton}", style = MaterialTheme.typography.labelLarge, color = HijrahRust)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Person,
                                label = strings.rowAlias,
                                value = profile.displayAlias.ifBlank { strings.sisterDefault },
                                modifier = Modifier.testTag("profile_display_alias")
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Public,
                                label = strings.rowOrigin,
                                value = profile.originCountry.ifBlank { strings.passportToBeFilled },
                                modifier = Modifier.testTag("profile_origin_country")
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Home,
                                label = strings.rowResidence,
                                value = profile.currentCity.ifBlank { strings.passportToBeFilled },
                                modifier = Modifier.testTag("profile_current_residence")
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            val targetDestination = listOfNotNull(
                                profile.focusCountry.takeIf { it.isNotBlank() },
                                profile.focusCity.takeIf { it.isNotBlank() }
                            ).joinToString(" • ").ifBlank { strings.passportExploring }

                            ProfileInfoRow(
                                icon = Icons.Rounded.FlightTakeoff,
                                label = strings.rowTargetDest,
                                value = targetDestination
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Explore,
                                label = strings.rowCurrentPhase,
                                value = "${profile.phaseEnum().localizedTitle(strings)} — ${profile.phaseEnum().localizedSubtitle(strings)}"
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Email,
                                label = strings.rowGoogleAccount,
                                value = userEmail?.takeIf { it.isNotBlank() } ?: "Google"
                            )
                            HorizontalDivider(color = HijrahBorderSubtle, modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow(
                                icon = Icons.Rounded.Schedule,
                                label = strings.rowMemberSince,
                                value = memberSince
                            )
                        }
                    }

                    // Privacy & Snelle acties
                    item {
                        HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Rounded.Shield, contentDescription = null, tint = HijrahBrown)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(strings.privacyTitle, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                                    Text(
                                        strings.privacyDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HijrahTextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                HijrahSecondaryButton(
                                    text = strings.profilePhaseBtn,
                                    onClick = onEditProfile,
                                    modifier = Modifier.weight(1f)
                                )
                                HijrahSecondaryButton(
                                    text = strings.toolsBtn,
                                    onClick = onOpenTools,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HijrahSecondaryButton(
                                text = strings.notificationsBtn,
                                onClick = onOpenNotifications,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_open_notifications_button")
                            )
                        }
                    }

                    // Opgeslagen favorieten
                    item {
                        HijrahSectionHeader(
                            title = strings.savedFavoritesTitle,
                            subtitle = "${favorites.size}"
                        )
                    }

                    if (favorites.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.Bookmark,
                                title = strings.savedFavoritesEmptyTitle,
                                description = strings.savedFavoritesEmptyDesc
                            )
                        }
                    } else {
                        items(favorites, key = { it.id }) { fav ->
                            HijrahElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = HijrahRust)
                                        Column {
                                            Text(fav.title, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                                            Text(fav.subtitle, style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
                                        }
                                    }
                                    IconButton(
                                        onClick = { onRemoveFavorite(fav) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.delete, tint = HijrahTextMuted)
                                    }
                                }
                            }
                        }
                    }

                    // Mijn bijdragen
                    item {
                        HijrahSectionHeader(
                            title = strings.myExperiencesTitle,
                            subtitle = "${myExperiences.size}"
                        )
                    }

                    if (myExperiences.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.EditNote,
                                title = strings.myExperiencesEmptyTitle,
                                description = strings.myExperiencesEmptyDesc
                            )
                        }
                    } else {
                        items(myExperiences, key = { it.id }) { exp ->
                            val statusText = when (exp.status) {
                                "APPROVED" -> "Goedgekeurd / Approved"
                                "REJECTED" -> "Rejected"
                                else -> "Pending"
                            }
                            HijrahElevatedCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                                Text("${exp.countryName} • ${exp.topic}", style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                                Text(exp.title, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = HijrahGoldTint,
                                    border = BorderStroke(1.dp, HijrahBorderGold)
                                ) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HijrahDarkBrown,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Uitloggen
                    item {
                        HijrahSecondaryButton(
                            text = strings.signOutFullBtn,
                            onClick = { showSignOutConfirm = true },
                            leadingIcon = Icons.AutoMirrored.Rounded.Logout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sign_out_button")
                        )
                    }
                }
            }
        }
    }

    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            containerColor = HijrahWhite,
            title = {
                Text(strings.signOutDialogTitle, style = MaterialTheme.typography.headlineMedium, color = HijrahDarkBrown)
            },
            text = {
                Text(
                    strings.signOutDialogBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahTextSecondary
                )
            },
            confirmButton = {
                HijrahPrimaryButton(
                    text = strings.signOut,
                    onClick = {
                        showSignOutConfirm = false
                        onSignOut()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text(strings.cancel, color = HijrahDarkBrown)
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(HijrahSurfaceTinted, HijrahRadii.Chip),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = HijrahBrown, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = HijrahTextMuted)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = HijrahDarkBrown)
        }
    }
}

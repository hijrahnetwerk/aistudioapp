package com.example.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.EditorialCatalog
import com.example.data.EditorialHighlight
import com.example.data.ExperienceSubmission
import com.example.data.HijrahPhase
import com.example.data.NotificationPreferences
import com.example.data.ReminderCadence
import com.example.data.UserProfile
import com.example.notifications.HijrahNotificationManager
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahReliabilityBadge
import com.example.ui.components.HijrahSecondaryButton
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedLabel
import com.example.ui.i18n.localizedSubtitle
import com.example.ui.i18n.localizedTitle
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
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingModalSheet(
    currentProfile: UserProfile?,
    onDismiss: () -> Unit,
    onSave: (
        alias: String,
        originCountry: String,
        currentCity: String,
        phase: HijrahPhase,
        country: String,
        city: String
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var alias by remember(currentProfile) { mutableStateOf(currentProfile?.displayAlias.orEmpty().ifBlank { strings.sisterDefault }) }
    var originCountry by remember(currentProfile) { mutableStateOf(currentProfile?.originCountry.orEmpty()) }
    var currentCity by remember(currentProfile) { mutableStateOf(currentProfile?.currentCity.orEmpty()) }
    var selectedPhase by remember(currentProfile) { mutableStateOf(currentProfile?.phaseEnum() ?: HijrahPhase.ORIENTATIE) }
    var focusCountry by remember(currentProfile) {
        mutableStateOf(currentProfile?.focusCountry.orEmpty().ifBlank { "Nog aan het verkennen" })
    }
    var focusCity by remember(currentProfile) { mutableStateOf(currentProfile?.focusCity.orEmpty()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HijrahWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = strings.profilePhaseBtn,
                style = MaterialTheme.typography.headlineLarge,
                color = HijrahDarkBrown
            )
            Text(
                text = strings.regWelcomePrivacy,
                style = MaterialTheme.typography.bodySmall,
                color = HijrahTextSecondary
            )

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it.take(40) },
                label = { Text(strings.regAliasLabel) },
                singleLine = true,
                shape = HijrahRadii.Input,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HijrahRust,
                    unfocusedBorderColor = HijrahBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_alias_input")
            )

            OutlinedTextField(
                value = originCountry,
                onValueChange = { originCountry = it.take(60) },
                label = { Text(strings.regOriginTitle) },
                singleLine = true,
                shape = HijrahRadii.Input,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HijrahRust,
                    unfocusedBorderColor = HijrahBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_origin_country_input")
            )

            OutlinedTextField(
                value = currentCity,
                onValueChange = { currentCity = it.take(60) },
                label = { Text(strings.regCurrentCityLabel) },
                singleLine = true,
                shape = HijrahRadii.Input,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HijrahRust,
                    unfocusedBorderColor = HijrahBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_current_city_input")
            )

            Text(
                text = strings.regCurrentPhaseTitle,
                style = MaterialTheme.typography.titleMedium,
                color = HijrahDarkBrown
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HijrahPhase.entries.forEach { phase ->
                    val selected = phase == selectedPhase
                    Surface(
                        shape = HijrahRadii.Input,
                        color = if (selected) HijrahDarkBrown else HijrahWhite,
                        border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(HijrahRadii.Input)
                            .clickable { selectedPhase = phase }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = phase.localizedTitle(strings),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selected) HijrahWhite else HijrahDarkBrown
                                )
                                Text(
                                    text = phase.localizedSubtitle(strings),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (selected) HijrahWhite.copy(alpha = 0.8f) else HijrahTextMuted
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = HijrahGold
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = strings.regFocusCountryLabel,
                style = MaterialTheme.typography.titleMedium,
                color = HijrahDarkBrown
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditorialCatalog.focusCountrySuggestions.forEach { country ->
                    val selected = focusCountry == country
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (selected) HijrahRust else HijrahSurfaceTinted,
                        border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                        modifier = Modifier
                            .clip(HijrahRadii.Chip)
                            .clickable { focusCountry = country }
                    ) {
                        Text(
                            text = if (country == "Nog aan het verkennen") strings.passportExploring else country,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) HijrahWhite else HijrahDarkBrown,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = focusCity,
                onValueChange = { focusCity = it.take(60) },
                label = { Text(strings.regFocusCityLabel) },
                singleLine = true,
                shape = HijrahRadii.Input,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HijrahRust,
                    unfocusedBorderColor = HijrahBorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (validationError != null) {
                Text(
                    text = validationError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahErrorRust
                )
            }

            HijrahPrimaryButton(
                text = strings.save,
                onClick = {
                    val trimmed = alias.trim()
                    val cleanOrigin = originCountry.trim()
                    val cleanCurrentCity = currentCity.trim()
                    when {
                        trimmed.length < 2 ->
                            validationError = strings.regErrAlias
                        cleanOrigin.length < 2 ->
                            validationError = strings.regErrOrigin
                        cleanCurrentCity.length < 2 ->
                            validationError = strings.regErrCity
                        else -> onSave(trimmed, cleanOrigin, cleanCurrentCity, selectedPhase, focusCountry, focusCity.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_onboarding_button")
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareExperienceModalSheet(
    defaultAlias: String,
    onDismiss: () -> Unit,
    onSubmit: (
        authorAlias: String,
        countryName: String,
        cityName: String,
        topic: String,
        title: String,
        story: String,
        prosAndCons: String,
        lessonLearned: String,
        practicalTip: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var country by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf(EditorialCatalog.recommendedTopics.first()) }
    var title by remember { mutableStateOf("") }
    var story by remember { mutableStateOf("") }
    var prosAndCons by remember { mutableStateOf("") }
    var lessonLearned by remember { mutableStateOf("") }
    var tip by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HijrahWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ervaring insturen",
                style = MaterialTheme.typography.headlineLarge,
                color = HijrahDarkBrown
            )
            Text(
                text = "Jouw inzending wordt eerst gecontroleerd door het beheer voordat deze zichtbaar is voor andere zusters.",
                style = MaterialTheme.typography.bodySmall,
                color = HijrahTextSecondary
            )

            OutlinedTextField(
                value = country,
                onValueChange = { country = it.take(60) },
                label = { Text("Land *") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it.take(60) },
                label = { Text("Stad of regio (optioneel)") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Onderwerp",
                style = MaterialTheme.typography.labelMedium,
                color = HijrahDarkBrown
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditorialCatalog.recommendedTopics.forEach { item ->
                    val selected = item == topic
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                        modifier = Modifier
                            .clip(HijrahRadii.Chip)
                            .clickable { topic = item }
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) HijrahWhite else HijrahDarkBrown,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it.take(120) },
                label = { Text("Titel van jouw ervaring *") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = story,
                onValueChange = { story = it.take(4000) },
                label = { Text("Persoonlijk verhaal *") },
                minLines = 4,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = prosAndCons,
                onValueChange = { prosAndCons = it.take(1000) },
                label = { Text("Voor- en nadelen (optioneel)") },
                minLines = 2,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lessonLearned,
                onValueChange = { lessonLearned = it.take(1000) },
                label = { Text("Wat zou je achteraf anders doen? (optioneel)") },
                minLines = 2,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tip,
                onValueChange = { tip = it.take(600) },
                label = { Text("Praktische tip voor zusters (optioneel)") },
                minLines = 2,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorText != null) {
                Text(
                    text = errorText ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahErrorRust
                )
            }

            HijrahPrimaryButton(
                text = "Insturen ter controle",
                onClick = {
                    when {
                        country.trim().length < 2 -> errorText = "Vul een geldig land in."
                        title.trim().length < 4 -> errorText = "Geef een titel van minimaal 4 tekens."
                        story.trim().length < 10 -> errorText = "Beschrijf jouw ervaring in minimaal 10 tekens."
                        else -> onSubmit(
                            defaultAlias.ifBlank { "Zuster" },
                            country.trim(),
                            city.trim(),
                            topic,
                            title.trim(),
                            story.trim(),
                            prosAndCons.trim(),
                            lessonLearned.trim(),
                            tip.trim()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_experience_button")
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightDetailSheet(
    highlight: EditorialHighlight,
    onDismiss: () -> Unit,
    onOpenTargetRoute: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = HijrahWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HijrahReliabilityBadge(label = highlight.reliabilityLabel)
            Text(
                text = highlight.title,
                style = MaterialTheme.typography.headlineLarge,
                color = HijrahDarkBrown
            )
            Text(
                text = highlight.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = HijrahTextSecondary
            )
            Text(
                text = "Belangrijkste aandachtspunten",
                style = MaterialTheme.typography.titleMedium,
                color = HijrahDarkBrown
            )
            highlight.keyPoints.forEach { point ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = null,
                        tint = HijrahGold,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(16.dp)
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahDarkBrown
                    )
                }
            }
            Text(
                text = highlight.lastCheckedLabel,
                style = MaterialTheme.typography.bodySmall,
                color = HijrahTextMuted
            )
            HijrahPrimaryButton(
                text = "Verder verkennen",
                onClick = { onOpenTargetRoute(highlight.targetRoute) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSearchSheet(
    onDismiss: () -> Unit,
    onSelectHighlight: (EditorialHighlight) -> Unit,
    onOpenRoute: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    var query by remember { mutableStateOf("") }
    val matchingHighlights = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) EditorialCatalog.featuredHighlights
        else EditorialCatalog.featuredHighlights.filter {
            it.title.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.summary.lowercase().contains(q)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = HijrahWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = strings.cdSearch,
                    style = MaterialTheme.typography.headlineMedium,
                    color = HijrahDarkBrown
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.close, tint = HijrahDarkBrown)
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = HijrahBrown) },
                placeholder = { Text(strings.discoverSearchPlaceholder) },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )

            matchingHighlights.forEach { item ->
                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelectHighlight(item) }
                ) {
                    Text(item.category, style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                    Text(item.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                }
            }

            if (matchingHighlights.isEmpty()) {
                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenRoute("discover") }
                ) {
                    Text(
                        text = "Geen directe gids voor '$query' gevonden — open Ontdekken voor alle onderwerpen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    myExperiences: List<ExperienceSubmission>,
    onboardingCompleted: Boolean,
    openStepsCount: Int,
    pendingDocsCount: Int,
    notificationPrefs: NotificationPreferences?,
    onUpdateNotificationPrefs: (forumRepliesEnabled: Boolean, milestoneRemindersEnabled: Boolean, cadence: ReminderCadence) -> Unit,
    onDismiss: () -> Unit,
    onOpenOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var hasPermission by remember {
        mutableStateOf(HijrahNotificationManager.hasNotificationPermission(context))
    }
    var permissionDeniedNote by remember { mutableStateOf(false) }

    val forumRepliesEnabled = notificationPrefs?.forumRepliesEnabled ?: true
    val milestoneRemindersEnabled = notificationPrefs?.milestoneRemindersEnabled ?: true
    val cadence = notificationPrefs?.cadenceEnum() ?: ReminderCadence.WEEKLY

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDeniedNote = !granted
        if (granted) {
            HijrahNotificationManager.ensureChannels(context)
            onUpdateNotificationPrefs(forumRepliesEnabled, milestoneRemindersEnabled, cadence)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = HijrahWhite,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.notifModalTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = HijrahDarkBrown
                    )
                    Text(
                        text = strings.notifModalSub,
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextMuted
                    )
                }
                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = HijrahRust)
            }

            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HijrahRust
                ) {
                    Text(
                        text = "Meldingstoestemming inschakelen",
                        style = MaterialTheme.typography.titleSmall,
                        color = HijrahDarkBrown
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (permissionDeniedNote) {
                            "Toestemming geweigerd. Je kunt meldingen alsnog aanzetten via de systeeminstellingen van dit apparaat."
                        } else {
                            "Sta meldingen toe om updates bij nieuwe forumreacties en mijlpaal-herinneringen op tijd te ontvangen."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HijrahPrimaryButton(
                        text = "Meldingen toestaan",
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_notification_permission_button")
                    )
                }
            }

            HijrahElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fcm_settings_card"),
                borderColor = HijrahBorderGold
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pushmeldingen (FCM)",
                        style = MaterialTheme.typography.titleMedium,
                        color = HijrahDarkBrown
                    )
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (notificationPrefs?.fcmToken?.isNotBlank() == true) HijrahRustTint else HijrahGoldTint,
                        border = BorderStroke(1.dp, HijrahBorderGold)
                    ) {
                        Text(
                            text = if (notificationPrefs?.fcmToken?.isNotBlank() == true) {
                                "Apparaat gekoppeld"
                            } else {
                                "Wordt gekoppeld"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = HijrahDarkBrown,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = strings.notifForumReplies,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = strings.notifForumRepliesSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }
                    Switch(
                        checked = forumRepliesEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && !hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            onUpdateNotificationPrefs(enabled, milestoneRemindersEnabled, cadence)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HijrahWhite,
                            checkedTrackColor = HijrahRust
                        ),
                        modifier = Modifier.testTag("fcm_forum_replies_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = strings.notifMilestones,
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = strings.notifMilestonesSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }
                    Switch(
                        checked = milestoneRemindersEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && !hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            onUpdateNotificationPrefs(forumRepliesEnabled, enabled, cadence)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HijrahWhite,
                            checkedTrackColor = HijrahRust
                        ),
                        modifier = Modifier.testTag("fcm_milestone_reminders_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = strings.notifCadenceLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = HijrahBrown
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReminderCadence.entries.forEach { option ->
                        val selected = option == cadence
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Chip)
                                .clickable {
                                    onUpdateNotificationPrefs(forumRepliesEnabled, milestoneRemindersEnabled, option)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.localizedLabel(strings),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) HijrahWhite else HijrahDarkBrown
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(
                    text = strings.notifTestBtn,
                    onClick = {
                        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            val title = if (pendingDocsCount > 0 || openStepsCount > 0) {
                                "Hijrah mijlpaal-herinnering"
                            } else {
                                "Forum & traject-update actief"
                            }
                            val body = when {
                                pendingDocsCount > 0 ->
                                    "Je hebt nog $pendingDocsCount document(en) en $openStepsCount checkliststap(pen) klaarstaan."
                                openStepsCount > 0 ->
                                    "Er staan nog $openStepsCount voorbereidingsstappen open in je tracker."
                                else ->
                                    "Nieuwe forumreacties en mijlpaal-herinneringen staan ingeschakeld."
                            }
                            HijrahNotificationManager.showNotification(
                                context = context,
                                channelId = HijrahNotificationManager.CHANNEL_MILESTONE_REMINDERS,
                                title = title,
                                body = body,
                                targetRoute = "profile"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_test_fcm_notification_button")
                )
            }

            if (openStepsCount > 0 || pendingDocsCount > 0) {
                HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.Forum, contentDescription = null, tint = HijrahBrown, modifier = Modifier.size(18.dp))
                        Text("Actieve mijlpaal-herinneringen", style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$pendingDocsCount juridisch document(en) nog te regelen • $openStepsCount openstaande voorbereidingsstappen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextSecondary
                    )
                }
            }

            if (!onboardingCompleted) {
                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDismiss()
                        onOpenOnboarding()
                    }
                ) {
                    Text("Traject instellen", style = MaterialTheme.typography.titleSmall, color = HijrahRust)
                    Text(
                        "Kies jouw huidige hijrah-fase om aanbevelingen op maat te zien.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextSecondary
                    )
                }
            }
            if (myExperiences.isEmpty() && onboardingCompleted) {
                Text(
                    text = "Je bent helemaal bij. Statusupdates van ingezonden ervaringen en forumreacties verschijnen hier.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahTextSecondary
                )
            } else {
                myExperiences.forEach { exp ->
                    HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Ervaring: ${exp.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = HijrahDarkBrown
                        )
                        val statusLabel = when (exp.status) {
                            "APPROVED" -> "Goedgekeurd en gepubliceerd"
                            "REJECTED" -> "Niet gepubliceerd — bekijk richtlijnen"
                            else -> "In beoordeling door beheer"
                        }
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahBrown
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportModerationSheet(
    targetLabel: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = HijrahWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Melding indienen bij beheer",
                style = MaterialTheme.typography.headlineMedium,
                color = HijrahDarkBrown
            )
            Text(
                text = "Onderwerp: $targetLabel. Je helpt de community veilig en betrouwbaar te houden.",
                style = MaterialTheme.typography.bodySmall,
                color = HijrahTextSecondary
            )
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it.take(400) },
                label = { Text("Reden van melding *") },
                minLines = 3,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Text(error ?: "", style = MaterialTheme.typography.bodySmall, color = HijrahErrorRust)
            }
            HijrahPrimaryButton(
                text = "Melding verzenden",
                onClick = {
                    if (reason.trim().length < 4) {
                        error = "Omschrijf de reden in minimaal 4 tekens."
                    } else {
                        onSubmitReport(reason.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

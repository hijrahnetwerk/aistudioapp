package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.CommunityPost
import com.example.data.CommunityReply
import com.example.data.EditorialCatalog
import com.example.data.ExperienceSubmission
import com.example.data.SavedFavorite
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahReliabilityBadge
import com.example.ui.components.HijrahSecondaryButton
import com.example.ui.components.HijrahSectionHeader
import com.example.ui.home.UiState
import com.example.ui.i18n.HijrahStrings
import com.example.ui.i18n.LocalAppStrings
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
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

private enum class CommunityTab(val label: String) {
    FORUM("Forum & Vragen"),
    EXPERIENCES("Ervaringen"),
    MODERATION("Beheer");

    fun localizedLabel(strings: HijrahStrings): String = when (this) {
        FORUM -> strings.forumTabForum
        EXPERIENCES -> strings.forumTabExperiences
        MODERATION -> strings.forumTabModeration
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    currentUserId: String,
    communityState: UiState<List<CommunityPost>>,
    approvedExperiencesState: UiState<List<ExperienceSubmission>>,
    pendingAdminState: UiState<List<ExperienceSubmission>>,
    repliesState: UiState<List<CommunityReply>>,
    favorites: List<SavedFavorite>,
    authorAlias: String,
    isAdminUser: Boolean,
    savedOfflinePostIds: Set<String> = emptySet(),
    isOfflineMode: Boolean = false,
    onToggleSaveOffline: (CommunityPost, Boolean) -> Unit = { _, _ -> },
    onCreatePost: (groupTitle: String, title: String, body: String, onDone: () -> Unit) -> Unit,
    onUpdatePost: (postId: String, groupTitle: String, title: String, body: String, onDone: () -> Unit) -> Unit,
    onDeletePost: (postId: String, onDone: () -> Unit) -> Unit,
    onSelectPostForReplies: (String?) -> Unit,
    onAddReply: (postId: String, body: String, onDone: () -> Unit) -> Unit,
    onOpenShareExperience: () -> Unit,
    onToggleFavoriteExperience: (ExperienceSubmission, Boolean) -> Unit,
    onModerateExperience: (experienceId: String, approve: Boolean) -> Unit,
    onReportContent: (targetType: String, targetId: String, label: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var activeTab by remember { mutableStateOf(CommunityTab.FORUM) }
    var selectedGroup by remember { mutableStateOf("Alle groepen") }
    var searchQuery by remember { mutableStateOf("") }
    var onlySavedOffline by remember { mutableStateOf(false) }

    var showComposer by remember { mutableStateOf(false) }
    var editingPostId by remember { mutableStateOf<String?>(null) }
    var composeGroup by remember { mutableStateOf("Oriëntatie & Vragen") }
    var composeTitle by remember { mutableStateOf("") }
    var composeBody by remember { mutableStateOf("") }
    var composerError by remember { mutableStateOf<String?>(null) }

    var selectedPostId by remember { mutableStateOf<String?>(null) }
    var postToDelete by remember { mutableStateOf<CommunityPost?>(null) }
    var selectedExperience by remember { mutableStateOf<ExperienceSubmission?>(null) }

    val availableTabs = if (isAdminUser) CommunityTab.entries else listOf(CommunityTab.FORUM, CommunityTab.EXPERIENCES)
    val allPosts = (communityState as? UiState.Success)?.data.orEmpty()
    val liveSelectedPost = remember(allPosts, selectedPostId) {
        allPosts.firstOrNull { it.id == selectedPostId }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM • HH:mm", Locale("nl", "NL")) }
    fun formatTimestamp(ts: Timestamp?): String =
        ts?.toDate()?.let { dateFormatter.format(it) } ?: "Zojuist"

    fun openComposerForNew() {
        editingPostId = null
        composeGroup = if (selectedGroup == "Alle groepen") "Oriëntatie & Vragen" else selectedGroup
        composeTitle = ""
        composeBody = ""
        composerError = null
        showComposer = true
    }

    fun openComposerForEdit(post: CommunityPost) {
        editingPostId = post.id
        composeGroup = post.groupTitle.ifBlank { "Oriëntatie & Vragen" }
        composeTitle = post.title
        composeBody = post.body
        composerError = null
        showComposer = true
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
                .testTag("community_list"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HijrahSectionHeader(
                    title = strings.forumHeaderTitle,
                    subtitle = strings.forumHeaderSub
                )
            }

            // Forum actiekaart (Vraag stellen vs Ervaring delen)
            item {
                HijrahDarkCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = strings.forumHeroTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = HijrahWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${strings.forumHeroSubPrefix} (${authorAlias.ifBlank { strings.sisterDefault }}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahWhite.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HijrahPrimaryButton(
                            text = if (showComposer) strings.forumCloseBtn else strings.forumAskBtn,
                            leadingIcon = Icons.Rounded.Forum,
                            onClick = {
                                activeTab = CommunityTab.FORUM
                                if (showComposer) {
                                    showComposer = false
                                    editingPostId = null
                                } else {
                                    openComposerForNew()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("forum_ask_question_button")
                        )
                        HijrahSecondaryButton(
                            text = strings.forumShareBtn,
                            leadingIcon = Icons.Rounded.EditNote,
                            onClick = onOpenShareExperience,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("forum_share_experience_button")
                        )
                    }
                }
            }

            // Tabbladen
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTabs.forEach { tab ->
                        val selected = tab == activeTab
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .clip(HijrahRadii.Chip)
                                .clickable { activeTab = tab }
                                .testTag("community_tab_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = tab.localizedLabel(strings),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) HijrahWhite else HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // Zoekbalk voor forum & ervaringen
            if (activeTab != CommunityTab.MODERATION) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = HijrahBrown) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Zoekopdracht wissen", tint = HijrahTextMuted)
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                if (activeTab == CommunityTab.FORUM) strings.forumSearchPlaceholder
                                else strings.expSearchPlaceholder
                            )
                        },
                        singleLine = true,
                        shape = HijrahRadii.Input,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forum_search_input")
                    )
                }
            }

            when (activeTab) {
                CommunityTab.FORUM -> {
                    if (isOfflineMode) {
                        item {
                            Surface(
                                shape = HijrahRadii.Input,
                                color = HijrahGoldTint,
                                border = BorderStroke(1.dp, HijrahBorderGold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forum_offline_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CloudOff,
                                        contentDescription = strings.forumOfflineBadge,
                                        tint = HijrahBrown,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = strings.offlineForumBanner,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HijrahDarkBrown
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = HijrahRadii.Chip,
                                color = if (onlySavedOffline) HijrahDarkBrown else HijrahGoldTint,
                                border = BorderStroke(1.dp, if (onlySavedOffline) HijrahBorderGold else HijrahBorderGold),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .clip(HijrahRadii.Chip)
                                    .clickable { onlySavedOffline = !onlySavedOffline }
                                    .testTag("forum_offline_filter_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (onlySavedOffline) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = null,
                                        tint = if (onlySavedOffline) HijrahGold else HijrahDarkBrown,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${strings.offlineSavedFilterPrefix} (${savedOfflinePostIds.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (onlySavedOffline) HijrahWhite else HijrahDarkBrown
                                    )
                                }
                            }

                            EditorialCatalog.communityGroups.forEach { grp ->
                                val selected = grp == selectedGroup
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = if (selected) HijrahRust else HijrahWhite,
                                    border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .clip(HijrahRadii.Chip)
                                        .clickable {
                                            selectedGroup = grp
                                            if (grp != "Alle groepen") composeGroup = grp
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = if (grp == "Alle groepen") strings.allGroupsLabel else grp,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (selected) HijrahWhite else HijrahDarkBrown
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showComposer) {
                        item {
                            HijrahElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forum_composer_card"),
                                borderColor = HijrahBorderGold
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (editingPostId != null) strings.forumPostTitleEdit else strings.forumPostTitleNew,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = HijrahDarkBrown
                                    )
                                    IconButton(
                                        onClick = {
                                            showComposer = false
                                            editingPostId = null
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = strings.close, tint = HijrahTextMuted)
                                    }
                                }

                                Text(
                                    text = "Kies een rubriek of typ een land/stad",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HijrahTextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    EditorialCatalog.communityGroups.drop(1).forEach { grp ->
                                        val selected = composeGroup.equals(grp, ignoreCase = true)
                                        Surface(
                                            shape = HijrahRadii.Chip,
                                            color = if (selected) HijrahGoldTint else HijrahSurfaceTinted,
                                            border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
                                            modifier = Modifier
                                                .clip(HijrahRadii.Chip)
                                                .clickable { composeGroup = grp }
                                        ) {
                                            Text(
                                                text = grp,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = HijrahDarkBrown,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = composeGroup,
                                    onValueChange = { composeGroup = it.take(80) },
                                    label = { Text("Rubriek, land of stad") },
                                    singleLine = true,
                                    shape = HijrahRadii.Input,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = composeTitle,
                                    onValueChange = {
                                        composeTitle = it.take(120)
                                        composerError = null
                                    },
                                    label = { Text(strings.forumPostTitleLabel) },
                                    singleLine = true,
                                    shape = HijrahRadii.Input,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("forum_title_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = composeBody,
                                    onValueChange = {
                                        composeBody = it.take(2000)
                                        composerError = null
                                    },
                                    label = { Text(strings.forumPostBodyLabel) },
                                    minLines = 3,
                                    shape = HijrahRadii.Input,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("forum_body_input")
                                )
                                composerError?.let { err ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = err, style = MaterialTheme.typography.bodySmall, color = HijrahErrorRust)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                HijrahPrimaryButton(
                                    text = if (editingPostId != null) {
                                        strings.forumSaveChanges
                                    } else {
                                        "${strings.forumAskBtn} (${authorAlias.ifBlank { strings.sisterDefault }})"
                                    },
                                    onClick = {
                                        val titleClean = composeTitle.trim()
                                        val bodyClean = composeBody.trim()
                                        when {
                                            titleClean.length < 3 -> composerError = "Vul een titel in van minimaal 3 tekens."
                                            bodyClean.length < 5 -> composerError = "Schrijf een bericht van minimaal 5 tekens."
                                            else -> {
                                                val editId = editingPostId
                                                if (editId != null) {
                                                    onUpdatePost(editId, composeGroup, titleClean, bodyClean) {
                                                        composeTitle = ""
                                                        composeBody = ""
                                                        editingPostId = null
                                                        showComposer = false
                                                    }
                                                } else {
                                                    onCreatePost(composeGroup, titleClean, bodyClean) {
                                                        composeTitle = ""
                                                        composeBody = ""
                                                        showComposer = false
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("forum_submit_post_button")
                                )
                            }
                        }
                    }

                    when (communityState) {
                        is UiState.Loading -> item {
                            Text(strings.forumLoading, style = MaterialTheme.typography.bodyMedium, color = HijrahTextMuted)
                        }
                        is UiState.Error -> item {
                            HijrahEmptyStateCard(Icons.Rounded.Forum, strings.forumHeaderTitle, communityState.message)
                        }
                        is UiState.Success -> {
                            val q = searchQuery.trim().lowercase()
                            val posts = communityState.data.filter { post ->
                                val groupOk = selectedGroup == "Alle groepen" ||
                                    post.groupTitle.equals(selectedGroup, ignoreCase = true)
                                val offlineOk = !onlySavedOffline || savedOfflinePostIds.contains(post.id)
                                val queryOk = q.isEmpty() ||
                                    post.title.lowercase().contains(q) ||
                                    post.body.lowercase().contains(q) ||
                                    post.groupTitle.lowercase().contains(q) ||
                                    post.authorAlias.lowercase().contains(q)
                                groupOk && offlineOk && queryOk
                            }

                            if (posts.isEmpty()) {
                                item {
                                    HijrahEmptyStateCard(
                                        icon = Icons.Rounded.Forum,
                                        title = when {
                                            onlySavedOffline -> strings.forumNoPostsSavedTitle
                                            q.isNotEmpty() -> strings.forumNoPostsSavedTitle
                                            else -> selectedGroup.let { if (it == "Alle groepen") strings.allGroupsLabel else it }
                                        },
                                        description = if (onlySavedOffline) {
                                            strings.forumNoPostsSavedDesc
                                        } else {
                                            strings.forumHeroTitle
                                        },
                                        actionText = if (onlySavedOffline) strings.forumShowAllPosts else strings.forumAskBtn,
                                        onActionClick = {
                                            if (onlySavedOffline) {
                                                onlySavedOffline = false
                                            } else {
                                                openComposerForNew()
                                            }
                                        }
                                    )
                                }
                            } else {
                                items(posts, key = { it.id }) { post ->
                                    val isAuthor = post.userId == currentUserId
                                    val isSavedOffline = savedOfflinePostIds.contains(post.id)
                                    HijrahElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("forum_post_card_${post.id}"),
                                        onClick = {
                                            selectedPostId = post.id
                                            onSelectPostForReplies(post.id)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Surface(
                                                    shape = HijrahRadii.Pill,
                                                    color = HijrahRustTint
                                                ) {
                                                    Text(
                                                        text = post.groupTitle,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = HijrahRust,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                                if (isAuthor) {
                                                    Surface(
                                                        shape = HijrahRadii.Pill,
                                                        color = HijrahGoldTint,
                                                        border = BorderStroke(1.dp, HijrahBorderGold)
                                                    ) {
                                                        Text(
                                                            text = strings.forumYourPost,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = HijrahDarkBrown,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                                if (isSavedOffline) {
                                                    Surface(
                                                        shape = HijrahRadii.Pill,
                                                        color = HijrahSurfaceTinted,
                                                        border = BorderStroke(1.dp, HijrahBorderGold)
                                                    ) {
                                                        Text(
                                                            text = strings.forumOfflineBadge,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = HijrahBrown,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { onToggleSaveOffline(post, isSavedOffline) },
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .testTag("forum_save_offline_${post.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSavedOffline) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                                        contentDescription = if (isSavedOffline) {
                                                            strings.forumRemoveOfflineCd
                                                        } else {
                                                            strings.forumSaveOfflineCd
                                                        },
                                                        tint = if (isSavedOffline) HijrahGold else HijrahBrown,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                if (isAuthor) {
                                                    IconButton(
                                                        onClick = { openComposerForEdit(post) },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(Icons.Rounded.Edit, contentDescription = "Bericht bewerken", tint = HijrahBrown, modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(
                                                        onClick = { postToDelete = post },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Bericht verwijderen", tint = HijrahErrorRust, modifier = Modifier.size(18.dp))
                                                    }
                                                } else {
                                                    IconButton(
                                                        onClick = { onReportContent("COMMUNITY_POST", post.id, post.title) },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(Icons.Rounded.Flag, contentDescription = "Bericht melden", tint = HijrahTextMuted, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }

                                        Text(post.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = post.body,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HijrahTextSecondary,
                                            maxLines = 4,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Rounded.Schedule, contentDescription = null, tint = HijrahTextMuted, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = "${post.authorAlias} • ${formatTimestamp(post.createdAt)}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = HijrahTextMuted
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, tint = HijrahBrown, modifier = Modifier.size(15.dp))
                                                Text(
                                                    text = "${post.replyCount} ${strings.forumRepliesLabel}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = HijrahBrown
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                CommunityTab.EXPERIENCES -> {
                    when (approvedExperiencesState) {
                        is UiState.Loading -> item {
                            Text("Ervaringen laden...", style = MaterialTheme.typography.bodyMedium, color = HijrahTextMuted)
                        }
                        is UiState.Error -> item {
                            HijrahEmptyStateCard(Icons.Rounded.EditNote, "Fout bij laden ervaringen", approvedExperiencesState.message)
                        }
                        is UiState.Success -> {
                            val q = searchQuery.trim().lowercase()
                            val list = approvedExperiencesState.data.filter { exp ->
                                q.isEmpty() ||
                                    exp.title.lowercase().contains(q) ||
                                    exp.story.lowercase().contains(q) ||
                                    exp.countryName.lowercase().contains(q) ||
                                    exp.cityName.lowercase().contains(q) ||
                                    exp.topic.lowercase().contains(q)
                            }
                            if (list.isEmpty()) {
                                item {
                                    HijrahEmptyStateCard(
                                        icon = Icons.Rounded.EditNote,
                                        title = if (q.isNotEmpty()) "Geen ervaringen gevonden" else "Nog geen gepubliceerde ervaringen",
                                        description = "Publicatie gebeurt pas na goedkeuring door beheer. Deel gerust jouw verhaal en praktische tips.",
                                        actionText = "Ervaring insturen",
                                        onActionClick = onOpenShareExperience
                                    )
                                }
                            } else {
                                items(list, key = { it.id }) { exp ->
                                    val saved = favorites.any { it.targetId == exp.id }
                                    HijrahElevatedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { selectedExperience = exp }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = listOfNotNull(exp.countryName, exp.cityName.takeIf { it.isNotBlank() }).joinToString(" • "),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = HijrahRust
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { onToggleFavoriteExperience(exp, saved) },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (saved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                                        contentDescription = "Ervaring opslaan",
                                                        tint = if (saved) HijrahRust else HijrahBrown
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { onReportContent("EXPERIENCE", exp.id, exp.title) },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(Icons.Rounded.Flag, contentDescription = "Ervaring melden", tint = HijrahTextMuted, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                        HijrahReliabilityBadge(label = "Gecontroleerd • ${exp.topic}")
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(exp.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = exp.story,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HijrahTextSecondary,
                                            maxLines = 4,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "${exp.authorAlias} • ${formatTimestamp(exp.createdAt)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HijrahTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                CommunityTab.MODERATION -> {
                    item {
                        HijrahSectionHeader(
                            title = "Beheer: Inzendingen controleren",
                            subtitle = "Keur ingezonden ervaringen goed of wijs ze af"
                        )
                    }
                    when (pendingAdminState) {
                        is UiState.Loading -> item {
                            Text("Wachtrij laden...", style = MaterialTheme.typography.bodyMedium, color = HijrahTextMuted)
                        }
                        is UiState.Error -> item {
                            HijrahEmptyStateCard(Icons.Rounded.Gavel, "Beheerlijst niet beschikbaar", pendingAdminState.message)
                        }
                        is UiState.Success -> {
                            if (pendingAdminState.data.isEmpty()) {
                                item {
                                    HijrahEmptyStateCard(
                                        icon = Icons.Rounded.Gavel,
                                        title = "Geen openstaande inzendingen",
                                        description = "Alle binnengekomen ervaringen zijn beoordeeld."
                                    )
                                }
                            } else {
                                items(pendingAdminState.data, key = { it.id }) { exp ->
                                    HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                        Text("${exp.countryName} • ${exp.authorAlias} • ${exp.topic}", style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                                        Text(exp.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(exp.story, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            HijrahPrimaryButton(
                                                text = "Goedkeuren",
                                                leadingIcon = Icons.Rounded.Check,
                                                onClick = { onModerateExperience(exp.id, true) },
                                                modifier = Modifier.weight(1f)
                                            )
                                            HijrahSecondaryButton(
                                                text = "Afwijzen",
                                                leadingIcon = Icons.Rounded.Close,
                                                onClick = { onModerateExperience(exp.id, false) },
                                                modifier = Modifier.weight(1f)
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

    // Bevestigingsdialoog verwijderen eigen bericht
    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            containerColor = HijrahWhite,
            title = { Text(strings.forumDeleteTitle, style = MaterialTheme.typography.titleLarge, color = HijrahDarkBrown) },
            text = {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePost(post.id) {
                            if (selectedPostId == post.id) {
                                selectedPostId = null
                                onSelectPostForReplies(null)
                            }
                            postToDelete = null
                        }
                    }
                ) {
                    Text(strings.delete, color = HijrahErrorRust, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text(strings.cancel, color = HijrahDarkBrown)
                }
            }
        )
    }

    // Post + live reacties sheet (gekopppeld aan actuele Firestore-stream)
    liveSelectedPost?.let { post ->
        var replyText by remember(post.id) { mutableStateOf("") }
        var replyError by remember(post.id) { mutableStateOf<String?>(null) }
        val isAuthor = post.userId == currentUserId
        val isSavedOffline = savedOfflinePostIds.contains(post.id)

        ModalBottomSheet(
            onDismissRequest = {
                selectedPostId = null
                onSelectPostForReplies(null)
            },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${post.groupTitle} • ${post.authorAlias} • ${formatTimestamp(post.createdAt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = HijrahRust,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onToggleSaveOffline(post, isSavedOffline) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("forum_sheet_save_offline_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (isSavedOffline) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSavedOffline) {
                                strings.forumRemoveOfflineCd
                            } else {
                                strings.forumSaveOfflineCd
                            },
                            tint = if (isSavedOffline) HijrahGold else HijrahBrown
                        )
                    }
                    if (isAuthor) {
                        IconButton(
                            onClick = {
                                selectedPostId = null
                                onSelectPostForReplies(null)
                                openComposerForEdit(post)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = strings.editButton, tint = HijrahBrown)
                        }
                    }
                }
                Text(post.title, style = MaterialTheme.typography.headlineMedium, color = HijrahDarkBrown)
                Text(post.body, style = MaterialTheme.typography.bodyMedium, color = HijrahTextSecondary)

                Text(
                    text = "${strings.forumRepliesLabel} (${post.replyCount})",
                    style = MaterialTheme.typography.titleSmall,
                    color = HijrahDarkBrown
                )
                when (repliesState) {
                    is UiState.Loading -> Text(strings.forumLoading, style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
                    is UiState.Error -> Text(repliesState.message, style = MaterialTheme.typography.bodySmall, color = HijrahErrorRust)
                    is UiState.Success -> {
                        if (repliesState.data.isEmpty()) {
                            Text(
                                text = strings.forumNoReplies,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahTextMuted
                            )
                        } else {
                            repliesState.data.forEach { reply ->
                                Surface(
                                    shape = HijrahRadii.Input,
                                    color = HijrahSurfaceTinted,
                                    border = BorderStroke(1.dp, HijrahBorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(reply.authorAlias, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = HijrahBrown)
                                            Text(formatTimestamp(reply.createdAt), style = MaterialTheme.typography.labelSmall, color = HijrahTextMuted)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(reply.body, style = MaterialTheme.typography.bodySmall, color = HijrahDarkBrown)
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = replyText,
                    onValueChange = {
                        replyText = it.take(1200)
                        replyError = null
                    },
                    label = { Text(strings.forumReplyPlaceholder) },
                    minLines = 2,
                    shape = HijrahRadii.Input,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_reply_input")
                )
                replyError?.let { err ->
                    Text(text = err, style = MaterialTheme.typography.bodySmall, color = HijrahErrorRust)
                }
                HijrahPrimaryButton(
                    text = strings.forumSendReply,
                    leadingIcon = Icons.AutoMirrored.Rounded.Send,
                    onClick = {
                        val trimmed = replyText.trim()
                        if (trimmed.length < 2) {
                            replyError = "Typ een reactie van minimaal 2 tekens."
                        } else {
                            onAddReply(post.id, trimmed) {
                                replyText = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_submit_reply_button")
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    // Volledig ervaringenoverzicht sheet
    selectedExperience?.let { exp ->
        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("nl", "NL")) }
        val pubDate = exp.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Onlangs"
        val checkDate = exp.verifiedAt?.toDate()?.let { dateFormat.format(it) } ?: pubDate

        ModalBottomSheet(
            onDismissRequest = { selectedExperience = null },
            containerColor = HijrahWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HijrahReliabilityBadge(label = "Goedgekeurd door beheer")
                Text(
                    text = "${exp.countryName} ${if (exp.cityName.isNotBlank()) "• ${exp.cityName}" else ""} • ${exp.topic}",
                    style = MaterialTheme.typography.labelMedium,
                    color = HijrahRust
                )
                Text(exp.title, style = MaterialTheme.typography.headlineLarge, color = HijrahDarkBrown)
                Text("Gedeeld door ${exp.authorAlias}", style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
                Text(exp.story, style = MaterialTheme.typography.bodyLarge, color = HijrahDarkBrown)

                if (exp.prosAndCons.isNotBlank()) {
                    ExperienceHighlightBlock("Voor- en nadelen", exp.prosAndCons)
                }
                if (exp.lessonLearned.isNotBlank()) {
                    ExperienceHighlightBlock("Wat zou ik achteraf anders doen?", exp.lessonLearned)
                }
                if (exp.practicalTip.isNotBlank()) {
                    ExperienceHighlightBlock("Praktische tip", exp.practicalTip)
                }

                Text(
                    text = "Publicatiedatum: $pubDate • Laatst gecontroleerd: $checkDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahTextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ExperienceHighlightBlock(title: String, body: String) {
    Surface(
        shape = HijrahRadii.Input,
        color = HijrahGoldTint,
        border = BorderStroke(1.dp, HijrahBorderGold),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = HijrahBrown)
            Spacer(modifier = Modifier.height(2.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, color = HijrahDarkBrown)
        }
    }
}

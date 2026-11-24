package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.EditorialCatalog
import com.example.data.EditorialHighlight
import com.example.data.FaqEntry
import com.example.data.SavedFavorite
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahPreparationQuizComponent
import com.example.ui.components.HijrahReliabilityBadge
import com.example.ui.components.HijrahSectionHeader
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahRustTint
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun DiscoverScreen(
    initialFilter: String?,
    favorites: List<SavedFavorite>,
    onToggleFavorite: (EditorialHighlight, Boolean) -> Unit,
    onOpenHighlight: (EditorialHighlight) -> Unit,
    onOpenTools: () -> Unit,
    onOpenGames: () -> Unit,
    onQuizCompleted: (Int, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var query by remember(initialFilter) { mutableStateOf(initialFilter.orEmpty()) }
    var selectedCategory by remember { mutableStateOf("Alles") }
    var expandedFaqId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Alles",
        "Oriëntatiekader",
        "Documenten & Visum",
        "Onderwijs & Gezin",
        "Zorg & Verzekeringen",
        "Aankomst & Integratie"
    )

    val filteredGuides = remember(query, selectedCategory) {
        val q = query.trim().lowercase()
        EditorialCatalog.featuredHighlights.filter { item ->
            val catOk = selectedCategory == "Alles" || item.category == selectedCategory
            val qOk = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.summary.lowercase().contains(q)
            catOk && qOk
        }
    }

    val filteredFaq = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) EditorialCatalog.faqEntries
        else EditorialCatalog.faqEntries.filter {
            it.question.lowercase().contains(q) ||
                it.answer.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
        }
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
                .testTag("discover_list"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(strings.discoverTitle, style = MaterialTheme.typography.displayMedium, color = HijrahDarkBrown)
                Text(
                    strings.discoverSub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahTextSecondary
                )
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = HijrahBrown) },
                    placeholder = { Text(strings.discoverSearchPlaceholder) },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val selected = cat == selectedCategory
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahRust else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                            modifier = Modifier.clip(HijrahRadii.Chip).clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = if (cat == "Alles") strings.catAll else cat,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) HijrahWhite else HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                HijrahDarkCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenTools
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Rounded.Checklist, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.discoverOpenToolsTitle, style = MaterialTheme.typography.titleMedium, color = HijrahWhite)
                            Text(
                                strings.discoverOpenToolsSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                HijrahElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("discover_games_card"),
                    onClick = onOpenGames,
                    borderColor = HijrahBorderGold
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahRustTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SportsEsports,
                                contentDescription = null,
                                tint = HijrahRust,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.gamesStripTitle, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                            Text(
                                strings.gamesStripSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahTextSecondary
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                HijrahSectionHeader(
                    title = strings.quizComponentTitle,
                    subtitle = strings.quizComponentSub
                )
            }

            item {
                HijrahPreparationQuizComponent(
                    onQuizCompleted = onQuizCompleted
                )
            }

            item {
                HijrahSectionHeader(
                    title = "Kennisbank & Gidsen",
                    subtitle = "${filteredGuides.size} betrouwbare gidsen"
                )
            }

            if (filteredGuides.isEmpty()) {
                item {
                    HijrahEmptyStateCard(
                        icon = Icons.Rounded.AutoStories,
                        title = "Geen gidsen voor dit filter",
                        description = "Wis het filter om alle geverifieerde thema's en gidsen te bekijken.",
                        actionText = "Filters wissen",
                        onActionClick = {
                            query = ""
                            selectedCategory = "Alles"
                        }
                    )
                }
            } else {
                items(filteredGuides, key = { it.id }) { item ->
                    val isSaved = favorites.any { it.targetId == item.id }
                    HijrahElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenHighlight(item) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HijrahReliabilityBadge(label = item.reliabilityLabel)
                            IconButton(
                                onClick = { onToggleFavorite(item, isSaved) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Opslaan als favoriet",
                                    tint = if (isSaved) HijrahRust else HijrahBrown
                                )
                            }
                        }
                        Text(item.category, style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.summary, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.lastCheckedLabel, style = MaterialTheme.typography.labelSmall, color = HijrahTextMuted)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                HijrahSectionHeader(
                    title = "Veelgestelde vragen",
                    subtitle = "Heldere antwoorden over opzet, privacy en voorbereiding"
                )
            }

            items(filteredFaq, key = { it.id }) { faq ->
                FaqAccordionCard(
                    faq = faq,
                    expanded = expandedFaqId == faq.id,
                    onToggle = {
                        expandedFaqId = if (expandedFaqId == faq.id) null else faq.id
                    }
                )
            }
        }
    }
}

@Composable
private fun FaqAccordionCard(
    faq: FaqEntry,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    HijrahElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(faq.category, style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                Text(faq.question, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
            }
            Icon(
                imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                contentDescription = if (expanded) "Inklappen" else "Uitklappen",
                tint = HijrahBrown
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(faq.answer, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
            }
        }
    }
}

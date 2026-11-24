package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.BudgetItem
import com.example.data.ChecklistItem
import com.example.data.DocumentItem
import com.example.data.DocumentStatus
import com.example.data.HijrahPhase
import com.example.data.SavedFavorite
import com.example.ui.i18n.HijrahStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedLabel
import com.example.ui.i18n.localizedTitle
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahRustTint
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite
import kotlin.math.roundToInt

enum class MigrationMilestoneId(
    val titleNl: String,
    val subtitleNl: String,
    val icon: ImageVector,
    val quickAddPhase: HijrahPhase,
    val quickAddCategory: String,
    val quickAddTitle: String
) {
    LEGAL_DOCS(
        titleNl = "Juridische Documentatie",
        subtitleNl = "Paspoorten, apostilles, gezin & verblijfsvisum",
        icon = Icons.Rounded.Gavel,
        quickAddPhase = HijrahPhase.VOORBEREIDING,
        quickAddCategory = "Documenten",
        quickAddTitle = "Apostilles & verblijfsvergunning eisen controleren"
    ),
    HOUSING_SEARCH(
        titleNl = "Woningzoektocht & Huisvesting",
        subtitleNl = "Wijken vergelijken, tijdelijk verblijf & huurcontract",
        icon = Icons.Rounded.HomeWork,
        quickAddPhase = HijrahPhase.VOORBEREIDING,
        quickAddCategory = "Wonen",
        quickAddTitle = "Geschikte gezinswijken en eerste maand woning selecteren"
    ),
    CULTURAL_PREP(
        titleNl = "Culturele Voorbereiding",
        subtitleNl = "Taalbasis, lokale gewoonten, onderwijs & gezinsritme",
        icon = Icons.Rounded.Translate,
        quickAddPhase = HijrahPhase.ORIENTATIE,
        quickAddCategory = "Cultuur & Taal",
        quickAddTitle = "Basiswoordenschat en lokale cultuur & gewoonten verkennen"
    ),
    FINANCIAL_PLAN(
        titleNl = "Financieel & Budgetplan",
        subtitleNl = "Verhuisbuffer, vaste maandlasten & spaarplan",
        icon = Icons.Rounded.AccountBalanceWallet,
        quickAddPhase = HijrahPhase.VOORBEREIDING,
        quickAddCategory = "Financiën",
        quickAddTitle = "Spaarbuffer voor eerste 6 maanden ter plaatse berekenen"
    ),
    COMMUNITY_ARRIVAL(
        titleNl = "Aankomst & Gemeenschap",
        subtitleNl = "Lokale moskee, scholen & betrouwbare zusterkring",
        icon = Icons.Rounded.Groups,
        quickAddPhase = HijrahPhase.INTEGRATIE,
        quickAddCategory = "Integratie",
        quickAddTitle = "Kennismaking met lokale moskee en zusters inplannen"
    );

    fun localizedTitle(strings: HijrahStrings): String = when (this) {
        LEGAL_DOCS -> strings.milestoneLegalTitle
        HOUSING_SEARCH -> strings.milestoneHousingTitle
        CULTURAL_PREP -> strings.milestoneCultureTitle
        FINANCIAL_PLAN -> strings.milestoneFinanceTitle
        COMMUNITY_ARRIVAL -> strings.milestoneCommunityTitle
    }

    fun localizedSubtitle(strings: HijrahStrings): String = when (this) {
        LEGAL_DOCS -> strings.milestoneLegalSub
        HOUSING_SEARCH -> strings.milestoneHousingSub
        CULTURAL_PREP -> strings.milestoneCultureSub
        FINANCIAL_PLAN -> strings.milestoneFinanceSub
        COMMUNITY_ARRIVAL -> strings.milestoneCommunitySub
    }
}

private enum class MilestoneStatus(val labelNl: String) {
    COMPLETED("Afgerond"),
    IN_PROGRESS("Bezig"),
    NOT_STARTED("Nog starten");

    fun localizedLabel(strings: HijrahStrings): String = when (this) {
        COMPLETED -> strings.statusCompleted
        IN_PROGRESS -> strings.statusInProgress
        NOT_STARTED -> strings.statusNotStarted
    }
}

private data class MilestoneSummary(
    val id: MigrationMilestoneId,
    val progress: Float,
    val statusLabel: String,
    val status: MilestoneStatus,
    val checklistTasks: List<ChecklistItem>,
    val documentTasks: List<DocumentItem>,
    val noteHint: String? = null
)

@Composable
fun MigrationProgressTrackerCard(
    currentPhase: HijrahPhase,
    checklist: List<ChecklistItem>,
    documents: List<DocumentItem>,
    budgetItems: List<BudgetItem>,
    favorites: List<SavedFavorite>,
    onToggleChecklistStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onAddChecklistStep: (HijrahPhase, String, String, Int) -> Unit,
    onCycleDocumentStatus: (DocumentItem) -> Unit,
    onOpenTools: () -> Unit,
    modifier: Modifier = Modifier
) {
    val milestones = remember(checklist, documents, budgetItems, favorites) {
        buildMilestoneSummaries(
            checklist = checklist,
            documents = documents,
            budgetItems = budgetItems,
            favorites = favorites
        )
    }

    val overallProgress = remember(milestones) {
        if (milestones.isEmpty()) 0f
        else milestones.map { it.progress }.average().toFloat().coerceIn(0f, 1f)
    }
    val completedCount = milestones.count { it.status == MilestoneStatus.COMPLETED }
    val activeCount = milestones.count { it.status == MilestoneStatus.IN_PROGRESS }

    var expandedMilestoneId by remember {
        mutableStateOf<MigrationMilestoneId?>(MigrationMilestoneId.LEGAL_DOCS)
    }

    val animatedOverall by animateFloatAsState(
        targetValue = overallProgress,
        animationSpec = tween(durationMillis = 500),
        label = "overall_migration_progress"
    )
    val strings = LocalAppStrings.current

    HijrahElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("migration_progress_tracker"),
        borderColor = HijrahBorderGold,
        contentPadding = PaddingValues(0.dp)
    ) {
        // Donkere visuele voortgangs-header met cirkelmeter en 5-stappenlijn
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(HijrahDarkBrown, HijrahBrown)
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CircularMigrationGauge(
                    progress = animatedOverall,
                    readyLabel = strings.trackerReadyWord,
                    modifier = Modifier.size(76.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.trackerHeader,
                        style = MaterialTheme.typography.labelSmall,
                        color = HijrahGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completedCount / ${milestones.size} • ${strings.statusCompleted}",
                        style = MaterialTheme.typography.titleLarge,
                        color = HijrahWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val statusSummary = buildString {
                        append("${strings.trackerPhaseWord}: ${currentPhase.localizedTitle(strings)}")
                        if (activeCount > 0) append(" • $activeCount ${strings.trackerActiveWord}")
                    }
                    Text(
                        text = statusSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahWhite.copy(alpha = 0.82f)
                    )
                }
            }

            // 5-punts compacte routebalk
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                milestones.forEachIndexed { index, summary ->
                    val nodeColor = when (summary.status) {
                        MilestoneStatus.COMPLETED -> HijrahRust
                        MilestoneStatus.IN_PROGRESS -> HijrahGold
                        MilestoneStatus.NOT_STARTED -> HijrahWhite.copy(alpha = 0.25f)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(nodeColor)
                            .border(
                                width = 1.5.dp,
                                color = if (summary.status == MilestoneStatus.NOT_STARTED) {
                                    HijrahWhite.copy(alpha = 0.45f)
                                } else {
                                    HijrahWhite
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                expandedMilestoneId =
                                    if (expandedMilestoneId == summary.id) null else summary.id
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (summary.status == MilestoneStatus.COMPLETED) {
                                Icons.Rounded.Check
                            } else {
                                summary.id.icon
                            },
                            contentDescription = summary.id.titleNl,
                            tint = HijrahWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (index < milestones.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(
                                    if (summary.status == MilestoneStatus.COMPLETED) {
                                        HijrahGold
                                    } else {
                                        HijrahWhite.copy(alpha = 0.22f)
                                    }
                                )
                        )
                    }
                }
            }
        }

        // Gedetailleerde tijdlijn per mijlpaal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            milestones.forEachIndexed { index, summary ->
                val isExpanded = expandedMilestoneId == summary.id
                MilestoneTimelineRow(
                    summary = summary,
                    isExpanded = isExpanded,
                    isLast = index == milestones.lastIndex,
                    onToggleExpand = {
                        expandedMilestoneId = if (isExpanded) null else summary.id
                    },
                    onToggleChecklist = { item -> onToggleChecklistStep(item, checklist) },
                    onCycleDocument = onCycleDocumentStatus,
                    onQuickAddStep = {
                        onAddChecklistStep(
                            summary.id.quickAddPhase,
                            summary.id.quickAddTitle,
                            summary.id.quickAddCategory,
                            checklist.size
                        )
                    },
                    onOpenTools = onOpenTools
                )
            }
        }
    }
}

@Composable
private fun CircularMigrationGauge(
    progress: Float,
    readyLabel: String,
    modifier: Modifier = Modifier
) {
    val pct = (progress * 100f).roundToInt().coerceIn(0, 100)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 7.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = Color(0x33FFFFFF),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            if (progress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(HijrahGold, HijrahRust, HijrahGold)),
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0.03f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$pct%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = HijrahWhite
            )
            Text(
                text = readyLabel,
                style = MaterialTheme.typography.labelSmall,
                color = HijrahGold
            )
        }
    }
}

@Composable
private fun MilestoneTimelineRow(
    summary: MilestoneSummary,
    isExpanded: Boolean,
    isLast: Boolean,
    onToggleExpand: () -> Unit,
    onToggleChecklist: (ChecklistItem) -> Unit,
    onCycleDocument: (DocumentItem) -> Unit,
    onQuickAddStep: () -> Unit,
    onOpenTools: () -> Unit
) {
    val strings = LocalAppStrings.current
    val animatedProgress by animateFloatAsState(
        targetValue = summary.progress,
        animationSpec = tween(durationMillis = 360),
        label = "milestone_${summary.id.name}"
    )

    val (badgeBg, badgeBorder, badgeText) = when (summary.status) {
        MilestoneStatus.COMPLETED -> Triple(HijrahRustTint, HijrahRust, HijrahRust)
        MilestoneStatus.IN_PROGRESS -> Triple(HijrahGoldTint, HijrahBorderGold, HijrahDarkBrown)
        MilestoneStatus.NOT_STARTED -> Triple(HijrahSurfaceTinted, HijrahBorderSubtle, HijrahTextMuted)
    }

    val nodeBg = when (summary.status) {
        MilestoneStatus.COMPLETED -> HijrahRust
        MilestoneStatus.IN_PROGRESS -> HijrahGoldTint
        MilestoneStatus.NOT_STARTED -> HijrahSurfaceTinted
    }
    val nodeTint = when (summary.status) {
        MilestoneStatus.COMPLETED -> HijrahWhite
        MilestoneStatus.IN_PROGRESS -> HijrahDarkBrown
        MilestoneStatus.NOT_STARTED -> HijrahBrown
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(HijrahRadii.Input)
                .clickable(onClick = onToggleExpand)
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .testTag("milestone_row_${summary.id.name.lowercase()}"),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Linker status-icoon + verticale tijdlijnverbinding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(nodeBg)
                        .border(
                            width = 1.5.dp,
                            color = if (summary.status == MilestoneStatus.IN_PROGRESS) HijrahGold else badgeBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (summary.status == MilestoneStatus.COMPLETED) {
                            Icons.Rounded.Check
                        } else {
                            summary.id.icon
                        },
                        contentDescription = summary.id.localizedTitle(strings),
                        tint = nodeTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (!isLast && !isExpanded) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(2.dp)
                            .height(22.dp)
                            .background(
                                if (summary.status == MilestoneStatus.COMPLETED) HijrahRust.copy(alpha = 0.45f)
                                else HijrahBorderSubtle
                            )
                    )
                }
            }

            // Rechter mijlpaalinhoud
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = summary.id.localizedTitle(strings),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = HijrahDarkBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = summary.id.localizedSubtitle(strings),
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = badgeBg,
                            border = BorderStroke(1.dp, badgeBorder)
                        ) {
                            Text(
                                text = summary.status.localizedLabel(strings),
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = if (isExpanded) "Details inklappen" else "Details uitklappen",
                            tint = HijrahBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Voortgangsbalk + exacte telling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahSurfaceSoftBrown)
                    ) {
                        if (animatedProgress > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress.coerceIn(0.04f, 1f))
                                    .height(8.dp)
                                    .clip(HijrahRadii.Chip)
                                    .background(
                                        if (summary.status == MilestoneStatus.COMPLETED) HijrahRust
                                        else HijrahGold
                                    )
                            )
                        }
                    }
                    Text(
                        text = summary.statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = HijrahBrown
                    )
                }
            }
        }

        // Uitklapbare onderliggende taken & documenten
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                shape = HijrahRadii.Input,
                color = HijrahSurfaceTinted,
                border = BorderStroke(1.dp, HijrahBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, top = 4.dp, bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    summary.noteHint?.let { hint ->
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }

                    summary.documentTasks.forEach { doc ->
                        val docStatus = doc.statusEnum()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahWhite)
                                .border(1.dp, HijrahBorderSubtle, HijrahRadii.Chip)
                                .clickable { onCycleDocument(doc) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = null,
                                    tint = if (docStatus == DocumentStatus.GEREED) HijrahRust else HijrahBrown,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = doc.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HijrahDarkBrown,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                shape = HijrahRadii.Chip,
                                color = when (docStatus) {
                                    DocumentStatus.GEREED -> HijrahRustTint
                                    DocumentStatus.IN_AANVRAAG -> HijrahGoldTint
                                    DocumentStatus.NOG_REGELEN -> HijrahSurfaceTinted
                                }
                            ) {
                                Text(
                                    text = docStatus.localizedLabel(strings),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HijrahDarkBrown,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    summary.checklistTasks.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahWhite)
                                .border(1.dp, HijrahBorderSubtle, HijrahRadii.Chip)
                                .clickable { onToggleChecklist(step) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (step.completed) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription = if (step.completed) strings.statusCompleted else strings.statusNotStarted,
                                tint = if (step.completed) HijrahRust else HijrahBrown,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = if (step.completed) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (step.completed) HijrahTextMuted else HijrahDarkBrown,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (summary.documentTasks.isEmpty() && summary.checklistTasks.isEmpty()) {
                        Text(
                            text = "${summary.id.localizedTitle(strings)} — ${strings.trackerQuickAddStep}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }

                    HorizontalDivider(color = HijrahBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onQuickAddStep,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("milestone_quick_add_${summary.id.name.lowercase()}")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = HijrahRust, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.trackerQuickAddStep, style = MaterialTheme.typography.labelMedium, color = HijrahRust)
                        }
                        TextButton(
                            onClick = onOpenTools,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("milestone_open_tools_${summary.id.name.lowercase()}")
                        ) {
                            Text(strings.trackerOpenTools, style = MaterialTheme.typography.labelMedium, color = HijrahDarkBrown)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Rounded.OpenInNew, contentDescription = null, tint = HijrahDarkBrown, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun buildMilestoneSummaries(
    checklist: List<ChecklistItem>,
    documents: List<DocumentItem>,
    budgetItems: List<BudgetItem>,
    favorites: List<SavedFavorite>
): List<MilestoneSummary> {
    fun ChecklistItem.matchesKeywords(vararg keywords: String): Boolean {
        val haystack = "${category.lowercase()} ${title.lowercase()}"
        return keywords.any { haystack.contains(it) }
    }

    // 1. Juridische Documentatie
    val legalChecklist = checklist.filter {
        it.matchesKeywords("document", "juridisch", "visum", "paspoort", "legalis", "akte", "verblijf")
    }
    val legalUnitsTotal = documents.size + legalChecklist.size
    val legalUnitsDone = documents.sumOf {
        when (it.statusEnum()) {
            DocumentStatus.GEREED -> 1.0
            DocumentStatus.IN_AANVRAAG -> 0.5
            DocumentStatus.NOG_REGELEN -> 0.0
        }
    } + legalChecklist.count { it.completed }.toDouble()
    val legalProgress = if (legalUnitsTotal == 0) 0f else (legalUnitsDone / legalUnitsTotal).toFloat().coerceIn(0f, 1f)
    val legalDocsReadyCount = documents.count { it.statusEnum() == DocumentStatus.GEREED } +
        legalChecklist.count { it.completed }
    val legalLabel = if (legalUnitsTotal > 0) "$legalDocsReadyCount/$legalUnitsTotal gereed" else "0 stappen"

    // 2. Woningzoektocht & Huisvesting
    val housingChecklist = checklist.filter {
        it.matchesKeywords("wonen", "woning", "huis", "wijk", "huur", "verblijfplaats", "accommodat")
    }
    val savedPlaceFavoritesCount = favorites.count { it.targetType == "CITY" || it.targetType == "LOCATION" }
    val housingProgress = when {
        housingChecklist.isNotEmpty() -> {
            (housingChecklist.count { it.completed }.toFloat() / housingChecklist.size.toFloat()).coerceIn(0f, 1f)
        }
        savedPlaceFavoritesCount > 0 -> 0.25f
        else -> 0f
    }
    val housingLabel = when {
        housingChecklist.isNotEmpty() -> "${housingChecklist.count { it.completed }}/${housingChecklist.size} stappen"
        savedPlaceFavoritesCount > 0 -> "$savedPlaceFavoritesCount opgeslagen"
        else -> "0/1 gestart"
    }
    val housingHint = if (savedPlaceFavoritesCount > 0) {
        "Je hebt $savedPlaceFavoritesCount stad/locatie(s) bewaard voor je woningoriëntatie."
    } else null

    // 3. Culturele Voorbereiding
    val culturalChecklist = checklist.filter {
        it.matchesKeywords("cultuur", "taal", "arabisch", "oriëntatie", "orientatie", "landkeuze", "onderwijs", "gezin", "gewoonte")
    }
    val culturalProgress = if (culturalChecklist.isEmpty()) 0f
    else (culturalChecklist.count { it.completed }.toFloat() / culturalChecklist.size.toFloat()).coerceIn(0f, 1f)
    val culturalLabel = if (culturalChecklist.isNotEmpty()) {
        "${culturalChecklist.count { it.completed }}/${culturalChecklist.size} stappen"
    } else "0 stappen"

    // 4. Financieel & Budgetplan
    val financeChecklist = checklist.filter {
        it.matchesKeywords("financi", "budget", "spaar", "bank", "kosten", "inkomen")
    }
    val budgetTarget = 3
    val budgetScore = (budgetItems.size.toFloat() / budgetTarget.toFloat()).coerceIn(0f, 1f)
    val financeProgress = when {
        financeChecklist.isNotEmpty() && budgetItems.isNotEmpty() -> {
            val checkScore = financeChecklist.count { it.completed }.toFloat() / financeChecklist.size.toFloat()
            ((checkScore + budgetScore) / 2f).coerceIn(0f, 1f)
        }
        financeChecklist.isNotEmpty() -> {
            (financeChecklist.count { it.completed }.toFloat() / financeChecklist.size.toFloat()).coerceIn(0f, 1f)
        }
        else -> budgetScore
    }
    val financeLabel = if (financeChecklist.isNotEmpty()) {
        "${financeChecklist.count { it.completed }}/${financeChecklist.size} • ${budgetItems.size} posten"
    } else {
        "${budgetItems.size}/$budgetTarget posten"
    }
    val financeHint = if (budgetItems.isNotEmpty()) {
        val totalEur = budgetItems.sumOf { it.amountEur }
        "Huidige begroting: ${budgetItems.size} post(en) (totaal € $totalEur)."
    } else null

    // 5. Aankomst & Gemeenschap
    val communityChecklist = checklist.filter {
        (it.phase == HijrahPhase.INTEGRATIE.name ||
            it.matchesKeywords("netwerk", "integratie", "zuster", "moskee", "aankomst", "gemeenschap")) &&
            !housingChecklist.contains(it) &&
            !legalChecklist.contains(it)
    }
    val communityProgress = if (communityChecklist.isEmpty()) 0f
    else (communityChecklist.count { it.completed }.toFloat() / communityChecklist.size.toFloat()).coerceIn(0f, 1f)
    val communityLabel = if (communityChecklist.isNotEmpty()) {
        "${communityChecklist.count { it.completed }}/${communityChecklist.size} stappen"
    } else "0 stappen"

    fun toStatus(progress: Float): MilestoneStatus = when {
        progress >= 0.999f -> MilestoneStatus.COMPLETED
        progress > 0f -> MilestoneStatus.IN_PROGRESS
        else -> MilestoneStatus.NOT_STARTED
    }

    return listOf(
        MilestoneSummary(
            id = MigrationMilestoneId.LEGAL_DOCS,
            progress = legalProgress,
            statusLabel = legalLabel,
            status = toStatus(legalProgress),
            checklistTasks = legalChecklist,
            documentTasks = documents,
            noteHint = "Tik op een document hieronder om de status bij te werken (Nog regelen -> In aanvraag -> Gereed)."
        ),
        MilestoneSummary(
            id = MigrationMilestoneId.HOUSING_SEARCH,
            progress = housingProgress,
            statusLabel = housingLabel,
            status = toStatus(housingProgress),
            checklistTasks = housingChecklist,
            documentTasks = emptyList(),
            noteHint = housingHint
        ),
        MilestoneSummary(
            id = MigrationMilestoneId.CULTURAL_PREP,
            progress = culturalProgress,
            statusLabel = culturalLabel,
            status = toStatus(culturalProgress),
            checklistTasks = culturalChecklist,
            documentTasks = emptyList()
        ),
        MilestoneSummary(
            id = MigrationMilestoneId.FINANCIAL_PLAN,
            progress = financeProgress,
            statusLabel = financeLabel,
            status = toStatus(financeProgress),
            checklistTasks = financeChecklist,
            documentTasks = emptyList(),
            noteHint = financeHint
        ),
        MilestoneSummary(
            id = MigrationMilestoneId.COMMUNITY_ARRIVAL,
            progress = communityProgress,
            statusLabel = communityLabel,
            status = toStatus(communityProgress),
            checklistTasks = communityChecklist,
            documentTasks = emptyList()
        )
    )
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.BudgetItem
import com.example.data.ChecklistItem
import com.example.data.DocumentItem
import com.example.data.DocumentStatus
import com.example.data.EditorialCatalog
import com.example.data.HijrahPhase
import com.example.data.PersonalNote
import com.example.data.SavedFavorite
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahPreparationQuizComponent
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahSecondaryButton
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
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

private enum class ToolsSection(val label: String) {
    PLANNER("Planner"),
    BUDGET("Budget"),
    DOCUMENTS("Documenten"),
    NOTES("Notities"),
    QUIZ("Kennistest");

    fun localizedLabel(strings: HijrahStrings): String = when (this) {
        PLANNER -> strings.toolsTabPlanner
        BUDGET -> strings.toolsTabBudget
        DOCUMENTS -> strings.toolsTabDocuments
        NOTES -> strings.toolsTabNotes
        QUIZ -> strings.toolsTabQuiz
    }
}

@Composable
fun HijrahToolsScreen(
    checklist: List<ChecklistItem>,
    budgetItems: List<BudgetItem>,
    documents: List<DocumentItem>,
    notes: List<PersonalNote>,
    favorites: List<SavedFavorite>,
    onBack: () -> Unit,
    onToggleStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onAddStep: (HijrahPhase, String, String, Int) -> Unit,
    onDeleteStep: (ChecklistItem, List<ChecklistItem>) -> Unit,
    onLoadArrivalPreset: (Int) -> Unit,
    onAddBudget: (title: String, category: String, amountEur: Long, monthly: Boolean) -> Unit,
    onDeleteBudget: (String) -> Unit,
    onAddDocument: (title: String, category: String, note: String) -> Unit,
    onCycleDocumentStatus: (DocumentItem) -> Unit,
    onDeleteDocument: (String) -> Unit,
    onSaveNote: (title: String, body: String, topic: String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onQuizCompleted: (Int, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val strings = LocalAppStrings.current
    var activeSection by remember { mutableStateOf(ToolsSection.PLANNER) }

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
                .testTag("hijrah_tools_list"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.back,
                            tint = HijrahDarkBrown
                        )
                    }
                    Column {
                        Text(strings.toolsTitle, style = MaterialTheme.typography.displayMedium, color = HijrahDarkBrown)
                        Text(
                            strings.toolsSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextSecondary
                        )
                    }
                }
            }

            // Section switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToolsSection.entries.forEach { section ->
                        val selected = section == activeSection
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .clip(HijrahRadii.Chip)
                                .clickable { activeSection = section }
                                .testTag("tools_tab_${section.name.lowercase()}")
                        ) {
                            Text(
                                text = section.localizedLabel(strings),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) HijrahWhite else HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            when (activeSection) {
                ToolsSection.PLANNER -> {
                    item {
                        PlannerSummaryAndAddCard(
                            checklist = checklist,
                            onAddStep = onAddStep,
                            onLoadArrivalPreset = { onLoadArrivalPreset(checklist.size) }
                        )
                    }
                    items(checklist, key = { it.id }) { step ->
                        HijrahElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onToggleStep(step, checklist) },
                            borderColor = if (step.completed) HijrahBorderGold else HijrahBorderSubtle,
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
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (step.completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = if (step.completed) "Afgevinkt" else "Open",
                                        tint = if (step.completed) HijrahGold else HijrahRust
                                    )
                                    Column {
                                        Text(
                                            text = HijrahPhase.fromRaw(step.phase).localizedTitle(strings),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HijrahBrown
                                        )
                                        Text(
                                            text = step.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = HijrahDarkBrown
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteStep(step, checklist) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = strings.delete,
                                        tint = HijrahTextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                ToolsSection.BUDGET -> {
                    item {
                        BudgetSectionCard(
                            budgetItems = budgetItems,
                            onAddBudget = onAddBudget
                        )
                    }
                    if (budgetItems.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.AccountBalanceWallet,
                                title = "Nog geen begrotingsposten",
                                description = "Voeg eenmalige vertrekkosten (zoals vliegtickets en borg) of maandlasten toe om grip op je budget te houden."
                            )
                        }
                    } else {
                        items(budgetItems, key = { it.id }) { item ->
                            HijrahElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.category} • ${if (item.recurringMonthly) "Maandelijks" else "Eenmalig"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HijrahRust
                                        )
                                        Text(item.title, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "€${item.amountEur}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = HijrahDarkBrown
                                        )
                                        IconButton(onClick = { onDeleteBudget(item.id) }, modifier = Modifier.size(48.dp)) {
                                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Verwijderen", tint = HijrahTextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ToolsSection.DOCUMENTS -> {
                    item {
                        DocumentAddCard(onAddDocument = onAddDocument)
                    }
                    items(documents, key = { it.id }) { doc ->
                        val status = doc.statusEnum()
                        HijrahElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onCycleDocumentStatus(doc) },
                            borderColor = if (status == DocumentStatus.GEREED) HijrahBorderGold else HijrahBorderSubtle,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(doc.category, style = MaterialTheme.typography.labelSmall, color = HijrahBrown)
                                    Text(doc.title, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                                    if (doc.note.isNotBlank()) {
                                        Text(doc.note, style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = HijrahRadii.Chip,
                                        color = when (status) {
                                            DocumentStatus.GEREED -> HijrahGoldTint
                                            DocumentStatus.IN_AANVRAAG -> HijrahSurfaceSoftBrown
                                            DocumentStatus.NOG_REGELEN -> HijrahSurfaceTinted
                                        }
                                    ) {
                                        Text(
                                            text = status.localizedLabel(strings),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HijrahDarkBrown,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                    IconButton(onClick = { onDeleteDocument(doc.id) }, modifier = Modifier.size(48.dp)) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.delete, tint = HijrahTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                ToolsSection.NOTES -> {
                    item {
                        NotesAddCard(onSaveNote = onSaveNote)
                    }
                    if (notes.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.EditNote,
                                title = "Nog geen persoonlijke notities",
                                description = "Bewaar hier je eigen aantekeningen per onderwerp, schoolbezoeken of vragen voor onderweg."
                            )
                        }
                    } else {
                        items(notes, key = { it.id }) { note ->
                            HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = note.topic.ifBlank { "Persoonlijk" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HijrahRust
                                    )
                                    IconButton(onClick = { onDeleteNote(note.id) }, modifier = Modifier.size(48.dp)) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Notitie verwijderen", tint = HijrahTextMuted)
                                    }
                                }
                                Text(note.title, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                                if (note.body.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(note.body, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                                }
                            }
                        }
                    }

                    if (favorites.isNotEmpty()) {
                        item {
                            Text(
                                text = "Opgeslagen informatie (${favorites.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = HijrahDarkBrown
                            )
                        }
                        items(favorites, key = { it.id }) { fav ->
                            HijrahElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = HijrahRust)
                                    Column {
                                        Text(fav.title, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                                        Text(fav.subtitle, style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                ToolsSection.QUIZ -> {
                    item {
                        HijrahPreparationQuizComponent(
                            onQuizCompleted = onQuizCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerSummaryAndAddCard(
    checklist: List<ChecklistItem>,
    onAddStep: (HijrahPhase, String, String, Int) -> Unit,
    onLoadArrivalPreset: () -> Unit
) {
    val strings = LocalAppStrings.current
    var newTitle by remember { mutableStateOf("") }
    var selectedPhase by remember { mutableStateOf(HijrahPhase.ORIENTATIE) }
    val done = checklist.count { it.completed }
    val total = checklist.size.coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.toolsTabPlanner, style = MaterialTheme.typography.titleMedium, color = HijrahWhite)
                    Text("$done / ${checklist.size} • ${strings.statusCompleted}", style = MaterialTheme.typography.bodySmall, color = HijrahWhite.copy(alpha = 0.8f))
                }
                Icon(Icons.Rounded.Checklist, contentDescription = null, tint = HijrahGold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (done.toFloat() / total.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(HijrahRadii.Pill),
                color = HijrahRust,
                trackColor = HijrahWhite.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(12.dp))
            HijrahSecondaryButton(
                text = "Aankomst- en integratiechecklist laden",
                onClick = onLoadArrivalPreset,
                modifier = Modifier.fillMaxWidth()
            )
        }

        HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Text(strings.trackerQuickAddStep, style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it.take(120) },
                label = { Text("Omschrijving van jouw stap") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HijrahPhase.entries.forEach { phase ->
                    val selected = phase == selectedPhase
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                        modifier = Modifier.clip(HijrahRadii.Chip).clickable { selectedPhase = phase }
                    ) {
                        Text(
                            text = phase.localizedTitle(strings),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) HijrahWhite else HijrahDarkBrown,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HijrahPrimaryButton(
                text = strings.save,
                leadingIcon = Icons.Rounded.Add,
                onClick = {
                    if (newTitle.trim().length >= 2) {
                        onAddStep(selectedPhase, newTitle.trim(), selectedPhase.titleNl, checklist.size)
                        newTitle = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BudgetSectionCard(
    budgetItems: List<BudgetItem>,
    onAddBudget: (title: String, category: String, amountEur: Long, monthly: Boolean) -> Unit
) {
    val oneTimeTotal = budgetItems.filter { !it.recurringMonthly }.sumOf { it.amountEur }
    val monthlyTotal = budgetItems.filter { it.recurringMonthly }.sumOf { it.amountEur }

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Vertrek & Reis") }
    var amountText by remember { mutableStateOf("") }
    var recurringMonthly by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
            Text("FINANCIEEL OVERZICHT", style = MaterialTheme.typography.labelSmall, color = HijrahGold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Eenmalige kosten", style = MaterialTheme.typography.bodySmall, color = HijrahWhite.copy(alpha = 0.8f))
                    Text("€$oneTimeTotal", style = MaterialTheme.typography.headlineLarge, color = HijrahWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Maandelijkse lasten", style = MaterialTheme.typography.bodySmall, color = HijrahWhite.copy(alpha = 0.8f))
                    Text("€$monthlyTotal / mnd", style = MaterialTheme.typography.headlineLarge, color = HijrahGold)
                }
            }
        }

        HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Text("Begrotingspost toevoegen", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it.take(100) },
                label = { Text("Omschrijving (bijv. Borg woning, Vlucht)") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() }.take(7) },
                    label = { Text("Bedrag (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it.take(50) },
                    label = { Text("Categorie") },
                    singleLine = true,
                    shape = HijrahRadii.Input,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (!recurringMonthly) HijrahDarkBrown else HijrahSurfaceTinted,
                    modifier = Modifier.weight(1f).clip(HijrahRadii.Chip).clickable { recurringMonthly = false }
                ) {
                    Text(
                        "Eenmalig",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (!recurringMonthly) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (recurringMonthly) HijrahDarkBrown else HijrahSurfaceTinted,
                    modifier = Modifier.weight(1f).clip(HijrahRadii.Chip).clickable { recurringMonthly = true }
                ) {
                    Text(
                        "Maandelijks",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (recurringMonthly) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HijrahPrimaryButton(
                text = "Post opslaan",
                leadingIcon = Icons.Rounded.Add,
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (title.trim().length >= 2) {
                        onAddBudget(title.trim(), category.trim(), amount, recurringMonthly)
                        title = ""
                        amountText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DocumentAddCard(
    onAddDocument: (title: String, category: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Legalisatie") }
    var note by remember { mutableStateOf("") }

    HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.Description, contentDescription = null, tint = HijrahBrown)
            Text("Documentenoverzicht", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
        }
        Text(
            "Tik op een document hieronder om de status bij te werken (Nog regelen -> In aanvraag -> Gereed).",
            style = MaterialTheme.typography.bodySmall,
            color = HijrahTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it.take(120) },
            label = { Text("Documentnaam") },
            singleLine = true,
            shape = HijrahRadii.Input,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it.take(50) },
                label = { Text("Categorie") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(300) },
                label = { Text("Notitie") },
                singleLine = true,
                shape = HijrahRadii.Input,
                modifier = Modifier.weight(1.3f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        HijrahPrimaryButton(
            text = "Document toevoegen",
            leadingIcon = Icons.Rounded.Add,
            onClick = {
                if (title.trim().length >= 2) {
                    onAddDocument(title.trim(), category.trim(), note.trim())
                    title = ""
                    note = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NotesAddCard(
    onSaveNote: (title: String, body: String, topic: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf(EditorialCatalog.recommendedTopics.first()) }

    HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Text("Nieuwe notitie", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it.take(120) },
            label = { Text("Onderwerp of titel") },
            singleLine = true,
            shape = HijrahRadii.Input,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it.take(2000) },
            label = { Text("Jouw aantekeningen") },
            minLines = 3,
            shape = HijrahRadii.Input,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditorialCatalog.recommendedTopics.forEach { t ->
                val selected = t == topic
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (selected) HijrahRust else HijrahSurfaceTinted,
                    modifier = Modifier.clip(HijrahRadii.Chip).clickable { topic = t }
                ) {
                    Text(
                        text = t,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HijrahPrimaryButton(
            text = "Notitie opslaan",
            leadingIcon = Icons.Rounded.Add,
            onClick = {
                if (title.trim().isNotEmpty()) {
                    onSaveNote(title.trim(), body.trim(), topic)
                    title = ""
                    body = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

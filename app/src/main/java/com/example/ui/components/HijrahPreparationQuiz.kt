package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.HijrahGamesCatalog
import com.example.data.QuizQuestionItem
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
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun HijrahPreparationQuizComponent(
    onQuizCompleted: (scorePoints: Int, passed: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val themes = HijrahGamesCatalog.preparationQuizThemes
    var selectedTheme by remember { mutableStateOf(themes.first()) }
    var roundKey by remember { mutableIntStateOf(0) }

    val activeQuestions: List<QuizQuestionItem> = remember(selectedTheme, roundKey) {
        val pool = if (selectedTheme == themes.first()) {
            HijrahGamesCatalog.preparationQuizQuestions
        } else {
            HijrahGamesCatalog.preparationQuizQuestions.filter { it.countryTag == selectedTheme }
        }
        if (selectedTheme == themes.first()) pool.shuffled().take(6) else pool
    }

    var currentIndex by remember(selectedTheme, roundKey) { mutableIntStateOf(0) }
    var selectedOption by remember(selectedTheme, roundKey) { mutableStateOf<Int?>(null) }
    var showHint by remember(selectedTheme, roundKey) { mutableStateOf(false) }
    var correctCount by remember(selectedTheme, roundKey) { mutableIntStateOf(0) }
    var isCompleted by remember(selectedTheme, roundKey) { mutableStateOf(false) }

    fun resetQuiz() {
        roundKey++
    }

    HijrahElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("preparation_quiz_component"),
        borderColor = HijrahBorderGold
    ) {
        // Header met icoon en titel
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(HijrahRadii.Chip)
                    .background(HijrahDarkBrown),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Quiz,
                    contentDescription = null,
                    tint = HijrahGold,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.quizComponentTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = HijrahDarkBrown
                )
                Text(
                    text = strings.quizComponentSub,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Themafilter-chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            themes.forEach { theme ->
                val active = theme == selectedTheme
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (active) HijrahRust else HijrahSurfaceTinted,
                    border = BorderStroke(1.dp, if (active) HijrahRust else HijrahBorderSubtle),
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .clip(HijrahRadii.Chip)
                        .clickable {
                            if (selectedTheme != theme) {
                                selectedTheme = theme
                                roundKey++
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (theme == themes.first()) strings.catAll else theme,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) HijrahWhite else HijrahDarkBrown
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isCompleted) {
            val total = activeQuestions.size.coerceAtLeast(1)
            val pct = ((correctCount * 100f) / total).toInt()
            val passed = pct >= 60
            val readinessTitle = when {
                pct >= 80 -> "Stevig voorbereid"
                pct >= 50 -> "Goed op weg"
                else -> "Mooi startpunt om te verdiepen"
            }

            HijrahDarkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preparation_quiz_result_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Quizresultaat",
                        tint = HijrahGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(readinessTitle, style = MaterialTheme.typography.titleLarge, color = HijrahWhite)
                        Text(
                            text = "Je had $correctCount van de $total vragen goed ($pct%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahGold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (passed) {
                        "Je hebt een sterke basis op dit thema. Bekijk in de kennisbank of HijrahTools welke concrete stappen voor jouw gezin nog openstaan."
                    } else {
                        "Gebruik de gidsen in Ontdekken en de documentenplanner in HijrahTools om de belangrijkste aandachtspunten rustig door te nemen."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahWhite.copy(alpha = 0.88f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HijrahPrimaryButton(
                    text = "Opnieuw testen",
                    leadingIcon = Icons.Rounded.Refresh,
                    onClick = ::resetQuiz,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("preparation_quiz_retry_button")
                )
                if (selectedTheme != themes.first()) {
                    HijrahSecondaryButton(
                        text = strings.catAll,
                        onClick = {
                            selectedTheme = themes.first()
                            roundKey++
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else if (activeQuestions.isNotEmpty()) {
            val question = activeQuestions[currentIndex]
            val progress = (currentIndex + 1).toFloat() / activeQuestions.size.toFloat()

            // Voortgang + live score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vraag ${currentIndex + 1} van ${activeQuestions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = HijrahBrown
                )
                Surface(
                    shape = HijrahRadii.Pill,
                    color = HijrahGoldTint,
                    border = BorderStroke(1.dp, HijrahBorderGold)
                ) {
                    Text(
                        text = "Goed: $correctCount",
                        style = MaterialTheme.typography.labelMedium,
                        color = HijrahDarkBrown,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(HijrahRadii.Pill),
                color = HijrahRust,
                trackColor = HijrahSurfaceSoftBrown,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = HijrahRadii.Pill,
                color = HijrahRustTint
            ) {
                Text(
                    text = question.countryTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahRust,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                color = HijrahDarkBrown
            )

            if (selectedOption == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (showHint) HijrahGoldTint else HijrahSurfaceTinted,
                        border = BorderStroke(1.dp, if (showHint) HijrahBorderGold else HijrahBorderSubtle),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clip(HijrahRadii.Chip)
                            .clickable(enabled = !showHint) { showHint = true }
                            .testTag("preparation_quiz_hint_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (showHint) "Tip getoond" else "Tip bekijken",
                                style = MaterialTheme.typography.labelMedium,
                                color = HijrahDarkBrown
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showHint && selectedOption == null, enter = fadeIn()) {
                Surface(
                    shape = HijrahRadii.Input,
                    color = HijrahGoldTint,
                    border = BorderStroke(1.dp, HijrahBorderGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Text(
                        text = "Tip: ${question.hintText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahDarkBrown,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meerkeuze-opties
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val letters = listOf("A", "B", "C", "D")
                question.options.forEachIndexed { idx, option ->
                    val isAnswered = selectedOption != null
                    val isSelected = selectedOption == idx
                    val isCorrect = idx == question.correctIndex

                    val bgColor = when {
                        !isAnswered -> HijrahWhite
                        isCorrect -> HijrahGoldTint
                        isSelected -> HijrahRustTint
                        else -> HijrahWhite
                    }
                    val borderColor = when {
                        !isAnswered -> HijrahBorderSubtle
                        isCorrect -> HijrahGold
                        isSelected -> HijrahErrorRust
                        else -> HijrahBorderSubtle
                    }

                    Surface(
                        shape = HijrahRadii.Input,
                        color = bgColor,
                        border = BorderStroke(if (isAnswered && (isCorrect || isSelected)) 2.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clip(HijrahRadii.Input)
                            .clickable(enabled = !isAnswered) {
                                selectedOption = idx
                                if (idx == question.correctIndex) {
                                    correctCount++
                                }
                            }
                            .testTag("preparation_quiz_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isAnswered && isCorrect -> HijrahDarkBrown
                                            isAnswered && isSelected -> HijrahErrorRust
                                            else -> HijrahSurfaceTinted
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letters.getOrElse(idx) { "${idx + 1}" },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isAnswered && (isCorrect || isSelected)) HijrahWhite else HijrahDarkBrown
                                )
                            }

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = HijrahDarkBrown,
                                modifier = Modifier.weight(1f)
                            )

                            if (isAnswered && isCorrect) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Juist antwoord",
                                    tint = HijrahBrown,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isAnswered && isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Onjuist antwoord",
                                    tint = HijrahErrorRust,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Directe toelichting na antwoord + Volgende-knop
            AnimatedVisibility(visible = selectedOption != null, enter = fadeIn()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    val wasCorrect = selectedOption == question.correctIndex
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahDarkBrown,
                        border = BorderStroke(1.dp, HijrahBorderGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (wasCorrect) "Juist antwoord!" else "Goed om te weten:",
                                style = MaterialTheme.typography.titleSmall,
                                color = HijrahGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahWhite.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HijrahPrimaryButton(
                        text = if (currentIndex + 1 >= activeQuestions.size) "Bekijk eindresultaat" else "Volgende vraag",
                        leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = {
                            if (currentIndex + 1 >= activeQuestions.size) {
                                isCompleted = true
                                val scorePoints = correctCount * 35
                                val passed = correctCount * 100 >= activeQuestions.size * 60
                                onQuizCompleted(scorePoints, passed)
                            } else {
                                currentIndex++
                                selectedOption = null
                                showHint = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preparation_quiz_next_button")
                    )
                }
            }
        }
    }
}

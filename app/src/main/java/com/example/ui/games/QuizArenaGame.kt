package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.unit.dp
import com.example.data.GameAgeGroup
import com.example.data.HijrahGamesCatalog
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahSecondaryButton
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
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

private enum class QuizStage { TITLE, PLAYING, FINISHED }

@Composable
fun QuizArenaGame(
    ageGroup: GameAgeGroup,
    isMuted: Boolean,
    onGameFinished: (score: Int, won: Boolean) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(QuizStage.TITLE) }
    var roundQuestions by remember(ageGroup) {
        mutableStateOf(
            (if (ageGroup == GameAgeGroup.KINDEREN) {
                HijrahGamesCatalog.kidsQuizQuestions
            } else {
                HijrahGamesCatalog.adultQuizQuestions
            }).shuffled().take(8)
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hintUsed by remember { mutableStateOf(false) }
    var eliminatedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    fun startQuiz() {
        HijrahSoundEffects.playTap(isMuted)
        val source = if (ageGroup == GameAgeGroup.KINDEREN) {
            HijrahGamesCatalog.kidsQuizQuestions
        } else {
            HijrahGamesCatalog.adultQuizQuestions
        }
        roundQuestions = source.shuffled().take(8)
        currentIndex = 0
        score = 0
        streak = 0
        maxStreak = 0
        correctCount = 0
        selectedOption = null
        hintUsed = false
        eliminatedIndices = emptySet()
        stage = QuizStage.PLAYING
    }

    fun triggerHint() {
        val question = roundQuestions.getOrNull(currentIndex) ?: return
        if (hintUsed || selectedOption != null) return
        HijrahSoundEffects.playTap(isMuted)
        hintUsed = true
        val wrongIndices = question.options.indices.filter { it != question.correctIndex }.shuffled()
        eliminatedIndices = wrongIndices.take(2).toSet()
    }

    fun selectAnswer(index: Int) {
        if (selectedOption != null) return
        val question = roundQuestions.getOrNull(currentIndex) ?: return
        selectedOption = index
        if (index == question.correctIndex) {
            HijrahSoundEffects.playMatch(isMuted)
            correctCount++
            streak++
            maxStreak = maxOf(maxStreak, streak)
            val base = if (hintUsed) 20 else 30
            score += base + ((streak - 1) * 10)
        } else {
            HijrahSoundEffects.playWrong(isMuted)
            streak = 0
        }
    }

    fun goNextQuestion() {
        HijrahSoundEffects.playTap(isMuted)
        if (currentIndex + 1 >= roundQuestions.size) {
            stage = QuizStage.FINISHED
            HijrahSoundEffects.playVictory(isMuted)
            onGameFinished(score, correctCount >= 5)
        } else {
            currentIndex++
            selectedOption = null
            hintUsed = false
            eliminatedIndices = emptySet()
        }
    }

    when (stage) {
        QuizStage.TITLE -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "مسابقة المعرفة",
                        style = HijrahArabicStyles.KufiAccent,
                        color = HijrahGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hijrah Kennisquiz",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HijrahWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Speel 8 vragen over islamitische landen, Arabische begroetingen, documenten en wonen in niveau '${ageGroup.labelNl}'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahWhite.copy(alpha = 0.85f)
                    )
                }

                HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Spelregels", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Elk juist antwoord levert 30 basispunten op.", style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                    Text("• Reeksbonus: elke opeenvolgende goede vraag geeft +10 extra punten.", style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                    Text("• Vast? Gebruik de Hint-knop voor een tip en het wegstrepen van twee foute opties.", style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                }

                HijrahPrimaryButton(
                    text = "Quiz starten (${ageGroup.shortNl})",
                    leadingIcon = Icons.Rounded.PlayArrow,
                    onClick = ::startQuiz,
                    modifier = Modifier.fillMaxWidth().testTag("start_quiz_button")
                )
            }
        }

        QuizStage.PLAYING -> {
            val question = roundQuestions[currentIndex]
            val progress = (currentIndex + 1).toFloat() / roundQuestions.size.toFloat()

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vraag ${currentIndex + 1} van ${roundQuestions.size}",
                        style = MaterialTheme.typography.titleSmall,
                        color = HijrahBrown
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = HijrahRadii.Pill,
                            color = HijrahGoldTint,
                            border = BorderStroke(1.dp, HijrahBorderGold)
                        ) {
                            Text(
                                text = "Reeks: $streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = HijrahDarkBrown,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = HijrahRadii.Pill,
                            color = HijrahDarkBrown
                        ) {
                            Text(
                                text = "$score pt",
                                style = MaterialTheme.typography.labelMedium,
                                color = HijrahGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(HijrahRadii.Pill),
                    color = HijrahRust,
                    trackColor = HijrahSurfaceSoftBrown,
                    strokeCap = StrokeCap.Round
                )

                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HijrahBorderGold
                ) {
                    Surface(
                        shape = HijrahRadii.Pill,
                        color = HijrahRustTint
                    ) {
                        Text(
                            text = question.countryTag,
                            style = MaterialTheme.typography.labelMedium,
                            color = HijrahRust,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleLarge,
                        color = HijrahDarkBrown
                    )
                }

                // 2-tier Hint knop
                if (selectedOption == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (hintUsed) HijrahGoldTint else HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, if (hintUsed) HijrahBorderGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Chip)
                                .clickable(enabled = !hintUsed, onClick = ::triggerHint)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (hintUsed) "Hint actief (50/50)" else "Hint gebruiken (50/50)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = HijrahDarkBrown
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = hintUsed && selectedOption == null, enter = fadeIn()) {
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahGoldTint,
                        border = BorderStroke(1.dp, HijrahBorderGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tip: ${question.hintText}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahDarkBrown,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEachIndexed { idx, option ->
                        val isEliminated = idx in eliminatedIndices
                        val isSelected = selectedOption == idx
                        val isCorrect = idx == question.correctIndex
                        val bg = when {
                            selectedOption == null && isEliminated -> HijrahSurfaceTinted
                            selectedOption == null -> HijrahWhite
                            isCorrect -> HijrahGoldTint
                            isSelected -> HijrahRustTint
                            else -> HijrahWhite
                        }
                        val border = when {
                            selectedOption == null && isEliminated -> HijrahBorderSubtle
                            selectedOption != null && isCorrect -> HijrahGold
                            selectedOption != null && isSelected -> HijrahErrorRust
                            else -> HijrahBorderSubtle
                        }
                        Surface(
                            shape = HijrahRadii.Input,
                            color = bg,
                            border = BorderStroke(if (selectedOption != null && (isCorrect || isSelected)) 2.dp else 1.dp, border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .clip(HijrahRadii.Input)
                                .clickable(enabled = selectedOption == null && !isEliminated) {
                                    selectAnswer(idx)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isEliminated && selectedOption == null) HijrahTextMuted else HijrahDarkBrown,
                                    modifier = Modifier.weight(1f)
                                )
                                if (selectedOption != null && isCorrect) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Juist antwoord", tint = HijrahBrown)
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = selectedOption != null, enter = fadeIn()) {
                    HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                        val wasRight = selectedOption == question.correctIndex
                        Text(
                            text = if (wasRight) "Ma sha Allah, helemaal juist!" else "Leerzaam punt:",
                            style = MaterialTheme.typography.titleMedium,
                            color = HijrahGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahWhite.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HijrahPrimaryButton(
                            text = if (currentIndex + 1 >= roundQuestions.size) "Bekijk eindscore" else "Volgende vraag",
                            leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                            onClick = ::goNextQuestion,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        QuizStage.FINISHED -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = "Resultaat", tint = HijrahGold, modifier = Modifier.size(48.dp))
                        Text("Quiz Afgerond!", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                        Text(
                            text = "$correctCount van ${roundQuestions.size} vragen goed • Beste reeks: $maxStreak",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahWhite.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$score punten",
                            style = MaterialTheme.typography.displayMedium,
                            color = HijrahGold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HijrahPrimaryButton(text = "Nieuwe quizronde", leadingIcon = Icons.Rounded.Refresh, onClick = ::startQuiz, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(text = "Terug naar alle spellen", onClick = onExitToHub, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

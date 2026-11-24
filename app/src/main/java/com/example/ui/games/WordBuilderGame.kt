package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

private enum class WordStage { TITLE, PLAYING, FINISHED }

private data class LetterChip(
    val id: Int,
    val letter: Char,
    val used: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordBuilderGame(
    ageGroup: GameAgeGroup,
    isMuted: Boolean,
    onGameFinished: (score: Int, won: Boolean) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(WordStage.TITLE) }
    var words by remember(ageGroup) {
        mutableStateOf(
            if (ageGroup == GameAgeGroup.KINDEREN) {
                HijrahGamesCatalog.kidsWordPuzzles
            } else {
                HijrahGamesCatalog.adultWordPuzzles
            }
        )
    }

    var wordIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var solvedCount by remember { mutableIntStateOf(0) }
    var hintTier by remember { mutableIntStateOf(0) }
    var isWordSolved by remember { mutableStateOf(false) }
    var isWrongAttempt by remember { mutableStateOf(false) }

    val tray = remember { mutableStateListOf<LetterChip>() }
    val placed = remember { mutableStateListOf<LetterChip>() }

    fun loadWord(index: Int) {
        val item = words[index]
        tray.clear()
        placed.clear()
        val scrambled = item.word.toCharArray().toList().shuffled()
        scrambled.forEachIndexed { i, ch ->
            tray.add(LetterChip(i, ch, used = false))
        }
        hintTier = 0
        isWordSolved = false
        isWrongAttempt = false
    }

    fun startWordGame() {
        HijrahSoundEffects.playTap(isMuted)
        words = (if (ageGroup == GameAgeGroup.KINDEREN) {
            HijrahGamesCatalog.kidsWordPuzzles
        } else {
            HijrahGamesCatalog.adultWordPuzzles
        }).shuffled()
        wordIdx = 0
        score = 0
        solvedCount = 0
        loadWord(0)
        stage = WordStage.PLAYING
    }

    fun checkSolvedIfComplete() {
        val target = words[wordIdx].word
        if (placed.size == target.length) {
            val attempt = placed.map { it.letter }.joinToString("")
            if (attempt == target) {
                isWordSolved = true
                isWrongAttempt = false
                solvedCount++
                score += (55 - hintTier * 15).coerceAtLeast(20)
                HijrahSoundEffects.playMatch(isMuted)
            } else {
                isWrongAttempt = true
                HijrahSoundEffects.playWrong(isMuted)
            }
        } else {
            isWrongAttempt = false
        }
    }

    fun useHint() {
        if (isWordSolved || hintTier >= 2) return
        val target = words[wordIdx].word
        HijrahSoundEffects.playTap(isMuted)
        if (hintTier == 0) {
            hintTier = 1
        } else {
            hintTier = 2
            // Clear placed letters that do not match the prefix and auto-fill next letter
            val firstWrongIdx = placed.indices.firstOrNull { placed[it].letter != target[it] } ?: placed.size
            while (placed.size > firstWrongIdx) {
                val removed = placed.removeAt(placed.lastIndex)
                val trayIdx = tray.indexOfFirst { it.id == removed.id }
                if (trayIdx >= 0) tray[trayIdx] = tray[trayIdx].copy(used = false)
            }
            if (placed.size < target.length) {
                val neededChar = target[placed.size]
                val candidateIdx = tray.indexOfFirst { !it.used && it.letter == neededChar }
                if (candidateIdx >= 0) {
                    val chip = tray[candidateIdx].copy(used = true)
                    tray[candidateIdx] = chip
                    placed.add(chip)
                    checkSolvedIfComplete()
                }
            }
        }
    }

    fun goNextWord() {
        HijrahSoundEffects.playTap(isMuted)
        if (wordIdx + 1 >= words.size) {
            stage = WordStage.FINISHED
            HijrahSoundEffects.playVictory(isMuted)
            onGameFinished(score, solvedCount >= 4)
        } else {
            wordIdx++
            loadWord(wordIdx)
        }
    }

    when (stage) {
        WordStage.TITLE -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "ألغاز الكلمات", style = HijrahArabicStyles.KufiAccent, color = HijrahGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Woord & Taal Puzzel", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Zet de letters in de juiste volgorde en leer belangrijke Arabische woorden en emigratiebegrippen voor niveau '${ageGroup.labelNl}'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahWhite.copy(alpha = 0.85f)
                    )
                }

                HijrahPrimaryButton(
                    text = "Woordpuzzel starten",
                    leadingIcon = Icons.Rounded.PlayArrow,
                    onClick = ::startWordGame,
                    modifier = Modifier.fillMaxWidth().testTag("start_word_puzzle_button")
                )
            }
        }

        WordStage.PLAYING -> {
            val item = words[wordIdx]
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
                    Text("Woord ${wordIdx + 1} van ${words.size}", style = MaterialTheme.typography.titleSmall, color = HijrahBrown)
                    Surface(shape = HijrahRadii.Pill, color = HijrahDarkBrown) {
                        Text(
                            text = "$score pt",
                            style = MaterialTheme.typography.labelLarge,
                            color = HijrahGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HijrahBorderGold
                ) {
                    Surface(shape = HijrahRadii.Pill, color = HijrahRustTint) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = HijrahRust,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.meaning,
                        style = MaterialTheme.typography.titleMedium,
                        color = HijrahDarkBrown
                    )
                }

                // Antwoordvakjes
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (slotIdx in item.word.indices) {
                        val chip = placed.getOrNull(slotIdx)
                        val borderColor = when {
                            isWordSolved -> HijrahGold
                            isWrongAttempt -> HijrahErrorRust
                            chip != null -> HijrahRust
                            else -> HijrahBorderSubtle
                        }
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (isWordSolved) HijrahGoldTint else if (chip != null) HijrahWhite else HijrahSurfaceTinted,
                            border = BorderStroke(if (chip != null || isWordSolved) 2.dp else 1.dp, borderColor),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(HijrahRadii.Chip)
                                .clickable(enabled = !isWordSolved && chip != null) {
                                    HijrahSoundEffects.playTap(isMuted)
                                    val removed = placed.removeAt(slotIdx)
                                    val trayIdx = tray.indexOfFirst { it.id == removed.id }
                                    if (trayIdx >= 0) tray[trayIdx] = tray[trayIdx].copy(used = false)
                                    isWrongAttempt = false
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = chip?.letter?.toString().orEmpty(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = HijrahDarkBrown
                                )
                            }
                        }
                    }
                }

                if (isWrongAttempt && !isWordSolved) {
                    Text(
                        text = "Nog niet helemaal — tik op een letter hierboven om te corrigeren.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahErrorRust
                    )
                }

                // Letterbak
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tray.forEachIndexed { idx, chip ->
                        Surface(
                            shape = HijrahRadii.Chip,
                            color = if (chip.used) HijrahSurfaceTinted else HijrahDarkBrown,
                            border = BorderStroke(1.dp, if (chip.used) HijrahBorderSubtle else HijrahBorderGold),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(HijrahRadii.Chip)
                                .clickable(enabled = !chip.used && !isWordSolved) {
                                    HijrahSoundEffects.playFlip(isMuted)
                                    tray[idx] = chip.copy(used = true)
                                    placed.add(chip.copy(used = true))
                                    checkSolvedIfComplete()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (chip.used) "" else chip.letter.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HijrahGold
                                )
                            }
                        }
                    }
                }

                // Actiebalk: Hint + Wis laatste
                if (!isWordSolved) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = HijrahRadii.Input,
                            color = HijrahGoldTint,
                            border = BorderStroke(1.dp, HijrahBorderGold),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Input)
                                .clickable(enabled = hintTier < 2, onClick = ::useHint)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = HijrahBrown, modifier = Modifier.size(18.dp))
                                Text(
                                    text = when (hintTier) {
                                        0 -> "  Tip tonen"
                                        1 -> "  Letter invullen"
                                        else -> "  Hints gebruikt"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = HijrahDarkBrown
                                )
                            }
                        }

                        Surface(
                            shape = HijrahRadii.Input,
                            color = HijrahSurfaceTinted,
                            border = BorderStroke(1.dp, HijrahBorderSubtle),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(HijrahRadii.Input)
                                .clickable(enabled = placed.isNotEmpty()) {
                                    HijrahSoundEffects.playTap(isMuted)
                                    val removed = placed.removeAt(placed.lastIndex)
                                    val trayIdx = tray.indexOfFirst { it.id == removed.id }
                                    if (trayIdx >= 0) tray[trayIdx] = tray[trayIdx].copy(used = false)
                                    isWrongAttempt = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.Backspace, contentDescription = "Wis laatste letter", tint = HijrahDarkBrown, modifier = Modifier.size(18.dp))
                                Text("Wis", style = MaterialTheme.typography.labelLarge, color = HijrahDarkBrown)
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = hintTier >= 1 && !isWordSolved, enter = fadeIn()) {
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahGoldTint,
                        border = BorderStroke(1.dp, HijrahBorderGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aanwijzing: ${item.hint}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahDarkBrown,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = isWordSolved, enter = fadeIn()) {
                    HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = HijrahGold)
                            Text("Goed gepuzzeld: ${item.word}", style = MaterialTheme.typography.titleMedium, color = HijrahGold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.meaning, style = MaterialTheme.typography.bodyMedium, color = HijrahWhite.copy(alpha = 0.9f))
                        Spacer(modifier = Modifier.height(12.dp))
                        HijrahPrimaryButton(
                            text = if (wordIdx + 1 >= words.size) "Bekijk eindscore" else "Volgend woord",
                            leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                            onClick = ::goNextWord,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        WordStage.FINISHED -> {
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
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = "Eindscore", tint = HijrahGold, modifier = Modifier.size(48.dp))
                        Text("Woordenpuzzel Voltooid!", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                        Text("$solvedCount van ${words.size} woorden opgelost", style = MaterialTheme.typography.bodyMedium, color = HijrahWhite.copy(alpha = 0.85f))
                        Text("$score punten", style = MaterialTheme.typography.displayMedium, color = HijrahGold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HijrahPrimaryButton(text = "Opnieuw puzzelen", leadingIcon = Icons.Rounded.Refresh, onClick = ::startWordGame, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(text = "Terug naar alle spellen", onClick = onExitToHub, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

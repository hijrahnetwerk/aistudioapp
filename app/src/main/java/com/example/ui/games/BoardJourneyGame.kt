package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BoardTileKind
import com.example.data.GameAgeGroup
import com.example.data.HijrahGamesCatalog
import com.example.data.QuizQuestionItem
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahSecondaryButton
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
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class BoardPlayer(
    val id: Int,
    val name: String,
    val isBot: Boolean,
    val color: Color,
    val position: Int = 0,
    val points: Int = 0,
    val skipNextTurn: Boolean = false
)

private enum class BoardStage {
    TITLE, PLAYING, FINISHED
}

@Composable
fun BoardJourneyGame(
    ageGroup: GameAgeGroup,
    isMuted: Boolean,
    onGameFinished: (score: Int, won: Boolean) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(BoardStage.TITLE) }
    var soloVsBot by remember { mutableStateOf(true) }
    var localPlayerCount by remember { mutableIntStateOf(2) }

    val players = remember { mutableStateListOf<BoardPlayer>() }
    var activePlayerIdx by remember { mutableIntStateOf(0) }
    var lastDice by remember { mutableIntStateOf(0) }
    var eventBanner by remember { mutableStateOf("Gooi de dobbelsteen om jouw Hijrah-reis te starten!") }
    var activeQuizPrompt by remember { mutableStateOf<QuizQuestionItem?>(null) }
    var quizFeedback by remember { mutableStateOf<String?>(null) }
    var winnerPlayer by remember { mutableStateOf<BoardPlayer?>(null) }
    var isResolvingTurn by remember { mutableStateOf(false) }

    val tiles = HijrahGamesCatalog.boardTiles
    val questionPool = remember(ageGroup) {
        if (ageGroup == GameAgeGroup.KINDEREN) {
            HijrahGamesCatalog.kidsQuizQuestions
        } else {
            HijrahGamesCatalog.adultQuizQuestions
        }
    }

    fun startNewMatch() {
        HijrahSoundEffects.playTap(isMuted)
        players.clear()
        val palette = listOf(HijrahRust, HijrahBrown, HijrahGold, HijrahDarkBrown)
        if (soloVsBot) {
            players.add(BoardPlayer(0, "Jij", isBot = false, color = palette[0]))
            players.add(BoardPlayer(1, "Hijrah-Bot", isBot = true, color = palette[2]))
        } else {
            for (i in 0 until localPlayerCount) {
                players.add(BoardPlayer(i, "Speler ${i + 1}", isBot = false, color = palette[i]))
            }
        }
        activePlayerIdx = 0
        lastDice = 0
        winnerPlayer = null
        activeQuizPrompt = null
        quizFeedback = null
        isResolvingTurn = false
        eventBanner = "${players.first().name} begint! Tik op Dobbelen."
        stage = BoardStage.PLAYING
    }

    fun advanceTurn() {
        if (players.isEmpty() || stage != BoardStage.PLAYING) return
        var next = (activePlayerIdx + 1) % players.size
        if (players[next].skipNextTurn) {
            val skipped = players[next]
            players[next] = skipped.copy(skipNextTurn = false)
            eventBanner = "${skipped.name} moest even sorteren en slaat 1 beurt over."
            next = (next + 1) % players.size
        }
        activePlayerIdx = next
        isResolvingTurn = false
    }

    fun finishWithWinner(winner: BoardPlayer) {
        winnerPlayer = winner
        stage = BoardStage.FINISHED
        HijrahSoundEffects.playVictory(isMuted)
        val humanPlayer = players.firstOrNull { !it.isBot }
        val won = if (soloVsBot) winner.id == 0 else true
        val finalScore = if (soloVsBot) {
            (humanPlayer?.points ?: 0) + (if (won) 120 else 40)
        } else {
            winner.points + 100
        }
        onGameFinished(finalScore, won)
    }

    fun applyTileEffect(playerIdx: Int, landedIndex: Int) {
        val tile = tiles[landedIndex]
        val current = players[playerIdx]
        when (tile.kind) {
            BoardTileKind.FINISH -> {
                val updated = current.copy(position = 23, points = current.points + 80)
                players[playerIdx] = updated
                eventBanner = "${updated.name} heeft Integratie bereikt!"
                finishWithWinner(updated)
            }

            BoardTileKind.BLESSING -> {
                val boostedPos = (landedIndex + 2).coerceAtMost(23)
                val updated = current.copy(position = boostedPos, points = current.points + 35)
                players[playerIdx] = updated
                HijrahSoundEffects.playMatch(isMuted)
                eventBanner = "${updated.name} landt op '${tile.titleNl}': +35 pt & 2 vakjes vooruit!"
                if (boostedPos == 23) {
                    finishWithWinner(updated)
                } else {
                    advanceTurn()
                }
            }

            BoardTileKind.DELAY -> {
                val updated = current.copy(skipNextTurn = true, points = current.points + 5)
                players[playerIdx] = updated
                HijrahSoundEffects.playWrong(isMuted)
                eventBanner = "${updated.name} wacht bij '${tile.titleNl}': volgende beurt overslaan."
                advanceTurn()
            }

            BoardTileKind.QUIZ_GATE -> {
                if (current.isBot) {
                    val botCorrect = Random.nextFloat() > 0.35f
                    val bonus = if (botCorrect) 30 else 5
                    val nextPos = if (botCorrect) (landedIndex + 1).coerceAtMost(23) else landedIndex
                    val updated = current.copy(position = nextPos, points = current.points + bonus)
                    players[playerIdx] = updated
                    eventBanner = if (botCorrect) {
                        "Hijrah-Bot beantwoordt het Vraagpunt goed (+1 vakje)!"
                    } else {
                        "Hijrah-Bot twijfelt bij het Vraagpunt en blijft staan."
                    }
                    if (nextPos == 23) finishWithWinner(updated) else advanceTurn()
                } else {
                    quizFeedback = null
                    activeQuizPrompt = questionPool.random()
                    eventBanner = "Vraagpunt voor ${current.name}! Beantwoord de vraag."
                }
            }

            else -> {
                val updated = current.copy(points = current.points + 15)
                players[playerIdx] = updated
                eventBanner = "${updated.name} bereikt '${tile.titleNl}' (+15 pt)."
                advanceTurn()
            }
        }
    }

    fun rollDiceForCurrentPlayer() {
        if (stage != BoardStage.PLAYING || isResolvingTurn || activeQuizPrompt != null) return
        val current = players.getOrNull(activePlayerIdx) ?: return
        isResolvingTurn = true
        HijrahSoundEffects.playDice(isMuted)
        val roll = Random.nextInt(1, 7)
        lastDice = roll
        val newPos = (current.position + roll).coerceAtMost(23)
        players[activePlayerIdx] = current.copy(position = newPos)
        applyTileEffect(activePlayerIdx, newPos)
    }

    // Automatische beurt voor Hijrah-Bot
    LaunchedEffect(stage, activePlayerIdx, isResolvingTurn, activeQuizPrompt) {
        val active = players.getOrNull(activePlayerIdx)
        if (stage == BoardStage.PLAYING && active != null && active.isBot && !isResolvingTurn && activeQuizPrompt == null) {
            delay(850L)
            rollDiceForCurrentPlayer()
        }
    }

    when (stage) {
        BoardStage.TITLE -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "رحلة الهجرة",
                        style = HijrahArabicStyles.KufiAccent,
                        color = HijrahGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "De Hijrah-Reis Bordspel",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HijrahWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Volg de 24 stappen van Thuis tot Integratie. Pak Barakah-bonussen, vermijd vertraging en win bonusstappen op Vraagpunten.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahWhite.copy(alpha = 0.85f)
                    )
                }

                HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Kies spelvorm",
                        style = MaterialTheme.typography.titleMedium,
                        color = HijrahDarkBrown
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ModeChoiceChip(
                            label = "1 Speler vs Bot",
                            icon = Icons.Rounded.Person,
                            selected = soloVsBot,
                            onClick = {
                                HijrahSoundEffects.playTap(isMuted)
                                soloVsBot = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChoiceChip(
                            label = "Gezinsspel (2–4)",
                            icon = Icons.Rounded.Group,
                            selected = !soloVsBot,
                            onClick = {
                                HijrahSoundEffects.playTap(isMuted)
                                soloVsBot = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (!soloVsBot) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aantal spelers aan tafel",
                            style = MaterialTheme.typography.labelLarge,
                            color = HijrahBrown
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(2, 3, 4).forEach { count ->
                                val selected = localPlayerCount == count
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = if (selected) HijrahRust else HijrahSurfaceTinted,
                                    border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
                                    modifier = Modifier
                                        .size(width = 56.dp, height = 48.dp)
                                        .clip(HijrahRadii.Chip)
                                        .clickable {
                                            HijrahSoundEffects.playTap(isMuted)
                                            localPlayerCount = count
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (selected) HijrahWhite else HijrahDarkBrown
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HijrahBorderGold
                ) {
                    Text("Betekenis van de vakjes", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendRow(Icons.Rounded.AutoAwesome, HijrahGold, "Barakah-vakje: +35 punten & 2 stappen vooruit")
                    LegendRow(Icons.Rounded.Quiz, HijrahRust, "Vraagpunt (${ageGroup.shortNl}): beantwoord juist voor +1 stap")
                    LegendRow(Icons.Rounded.HourglassTop, HijrahBrown, "Vertraging: even geduld, volgende beurt overslaan")
                    LegendRow(Icons.Rounded.Flag, HijrahDarkBrown, "Finish: Integratie geslaagd (+80 bonuspunten)")
                }

                HijrahPrimaryButton(
                    text = "Spel starten",
                    leadingIcon = Icons.Rounded.Casino,
                    onClick = ::startNewMatch,
                    modifier = Modifier.fillMaxWidth().testTag("start_board_game_button")
                )
            }
        }

        BoardStage.PLAYING -> {
            val activePlayer = players.getOrNull(activePlayerIdx)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Compacte spelers-HUD bovenaan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    players.forEachIndexed { idx, player ->
                        val isTurn = idx == activePlayerIdx
                        Surface(
                            shape = HijrahRadii.Input,
                            color = if (isTurn) HijrahDarkBrown else HijrahWhite,
                            border = BorderStroke(1.dp, if (isTurn) HijrahGold else HijrahBorderSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(player.color)
                                            .border(1.dp, HijrahWhite, CircleShape)
                                    )
                                    Text(
                                        text = player.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isTurn) HijrahWhite else HijrahDarkBrown,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "Vak ${player.position + 1}/24 • ${player.points} pt",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isTurn) HijrahGold else HijrahTextMuted
                                )
                            }
                        }
                    }
                }

                // Het visuele 24-vakjes spelbord (~70-80% van het speelveld)
                HijrahElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    SerpentineBoardCanvas(
                        players = players,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Korte statusbalk + grote dobbelknop onderaan
                Surface(
                    shape = HijrahRadii.Input,
                    color = HijrahSurfaceTinted,
                    border = BorderStroke(1.dp, HijrahBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(HijrahRadii.Chip)
                                .background(HijrahDarkBrown)
                                .border(1.dp, HijrahBorderGold, HijrahRadii.Chip),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lastDice == 0) "-" else "$lastDice",
                                style = MaterialTheme.typography.titleLarge,
                                color = HijrahGold
                            )
                        }
                        Text(
                            text = eventBanner,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahDarkBrown,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HijrahPrimaryButton(
                    text = when {
                        activePlayer?.isBot == true -> "Hijrah-Bot is aan zet..."
                        activeQuizPrompt != null -> "Beantwoord eerst de vraag"
                        else -> "Dobbelen (${activePlayer?.name ?: ""})"
                    },
                    leadingIcon = Icons.Rounded.Casino,
                    enabled = activePlayer != null && !activePlayer.isBot && !isResolvingTurn && activeQuizPrompt == null,
                    onClick = ::rollDiceForCurrentPlayer,
                    modifier = Modifier.fillMaxWidth().testTag("board_roll_dice_button")
                )
            }

            // Vraagpunt dialoog
            activeQuizPrompt?.let { question ->
                AlertDialog(
                    onDismissRequest = {},
                    containerColor = HijrahWhite,
                    title = {
                        Column {
                            Text(question.countryTag, style = MaterialTheme.typography.labelMedium, color = HijrahRust)
                            Text("Vraagpunt onderweg", style = MaterialTheme.typography.titleLarge, color = HijrahDarkBrown)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(question.question, style = MaterialTheme.typography.bodyMedium, color = HijrahDarkBrown)
                            question.options.forEachIndexed { idx, opt ->
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = HijrahSurfaceTinted,
                                    border = BorderStroke(1.dp, HijrahBorderSubtle),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .clip(HijrahRadii.Chip)
                                        .clickable(enabled = quizFeedback == null) {
                                            val current = players.getOrNull(activePlayerIdx) ?: return@clickable
                                            if (idx == question.correctIndex) {
                                                HijrahSoundEffects.playMatch(isMuted)
                                                val nextPos = (current.position + 1).coerceAtMost(23)
                                                val updated = current.copy(
                                                    position = nextPos,
                                                    points = current.points + 35
                                                )
                                                players[activePlayerIdx] = updated
                                                quizFeedback = "Goed! +35 punten en 1 bonusstap vooruit."
                                            } else {
                                                HijrahSoundEffects.playWrong(isMuted)
                                                players[activePlayerIdx] = current.copy(points = current.points + 5)
                                                quizFeedback = "Helaas! Juist antwoord: ${question.options[question.correctIndex]}"
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(opt, style = MaterialTheme.typography.bodyMedium, color = HijrahDarkBrown)
                                    }
                                }
                            }
                            AnimatedVisibility(visible = quizFeedback != null, enter = fadeIn()) {
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = HijrahGoldTint,
                                    border = BorderStroke(1.dp, HijrahBorderGold),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = quizFeedback.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HijrahDarkBrown,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        if (quizFeedback != null) {
                            TextButton(
                                onClick = {
                                    val p = players.getOrNull(activePlayerIdx)
                                    activeQuizPrompt = null
                                    quizFeedback = null
                                    if (p != null && p.position >= 23) {
                                        finishWithWinner(p)
                                    } else {
                                        advanceTurn()
                                    }
                                }
                            ) {
                                Text("Verder", color = HijrahRust, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            }
        }

        BoardStage.FINISHED -> {
            val winner = winnerPlayer
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
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Winnaar",
                            tint = HijrahGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Bestemming Bereikt!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = HijrahWhite
                        )
                        Text(
                            text = "${winner?.name ?: "Speler"} rondde de Hijrah-Reis als eerste af.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahWhite.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        players.sortedByDescending { it.points }.forEachIndexed { rank, p ->
                            Surface(
                                shape = HijrahRadii.Chip,
                                color = Color(0x1FFFFFFF),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${rank + 1}. ${p.name}", color = HijrahWhite, style = MaterialTheme.typography.titleSmall)
                                    Text("${p.points} pt • Vak ${p.position + 1}", color = HijrahGold, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HijrahPrimaryButton(
                    text = "Opnieuw spelen",
                    leadingIcon = Icons.Rounded.Refresh,
                    onClick = ::startNewMatch,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(
                    text = "Terug naar alle spellen",
                    onClick = onExitToHub,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SerpentineBoardCanvas(
    players: List<BoardPlayer>,
    modifier: Modifier = Modifier
) {
    val tiles = HijrahGamesCatalog.boardTiles
    val animatedPositions = players.map { player ->
        animateFloatAsState(
            targetValue = player.position.toFloat(),
            animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
            label = "pawn_${player.id}"
        ).value
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = 4
            val rows = 6
            val padX = size.width * 0.11f
            val padY = size.height * 0.08f
            val usableW = (size.width - padX * 2f).coerceAtLeast(1f)
            val usableH = (size.height - padY * 2f).coerceAtLeast(1f)
            val stepX = usableW / (cols - 1)
            val stepY = usableH / (rows - 1)

            fun tileCenter(index: Int): Offset {
                val r = index / cols
                val rawCol = index % cols
                val c = if (r % 2 == 0) rawCol else (cols - 1 - rawCol)
                return Offset(padX + c * stepX, padY + r * stepY)
            }

            // Route-lijn tekenen
            val path = Path()
            tiles.indices.forEach { i ->
                val p = tileCenter(i)
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            drawPath(
                path = path,
                color = HijrahBorderStrongColor,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Vakjes tekenen
            tiles.forEach { tile ->
                val center = tileCenter(tile.index)
                val radius = (minOf(stepX, stepY) * 0.30f).coerceIn(14.dp.toPx(), 24.dp.toPx())
                val fill = when (tile.kind) {
                    BoardTileKind.START -> HijrahDarkBrown
                    BoardTileKind.FINISH -> HijrahDarkBrown
                    BoardTileKind.BLESSING -> HijrahGoldTintSolid
                    BoardTileKind.QUIZ_GATE -> HijrahRust
                    BoardTileKind.DELAY -> HijrahSurfaceSolid
                    BoardTileKind.NORMAL -> HijrahWhite
                }
                val ring = when (tile.kind) {
                    BoardTileKind.START, BoardTileKind.FINISH, BoardTileKind.BLESSING -> HijrahGold
                    BoardTileKind.QUIZ_GATE -> HijrahDarkBrown
                    else -> HijrahBrown
                }
                drawCircle(color = fill, radius = radius, center = center)
                drawCircle(
                    color = ring,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            // Geanimeerde pionnen tekenen
            players.forEachIndexed { idx, player ->
                val posFloat = animatedPositions.getOrElse(idx) { player.position.toFloat() }
                val baseIdx = posFloat.toInt().coerceIn(0, 23)
                val nextIdx = (baseIdx + 1).coerceIn(0, 23)
                val frac = (posFloat - baseIdx).coerceIn(0f, 1f)
                val c1 = tileCenter(baseIdx)
                val c2 = tileCenter(nextIdx)
                val interp = Offset(
                    c1.x + (c2.x - c1.x) * frac,
                    c1.y + (c2.y - c1.y) * frac
                )
                val offsetAngle = when (idx) {
                    0 -> Offset(-6.dp.toPx(), -6.dp.toPx())
                    1 -> Offset(6.dp.toPx(), -6.dp.toPx())
                    2 -> Offset(-6.dp.toPx(), 6.dp.toPx())
                    else -> Offset(6.dp.toPx(), 6.dp.toPx())
                }
                val pawnCenter = if (players.size > 1) interp + offsetAngle else interp
                drawCircle(color = HijrahWhite, radius = 9.5.dp.toPx(), center = pawnCenter)
                drawCircle(color = player.color, radius = 7.5.dp.toPx(), center = pawnCenter)
            }
        }
    }
}

private val HijrahBorderStrongColor = Color(0x3D4F3A23)
private val HijrahGoldTintSolid = Color(0xFFF7EFC9)
private val HijrahSurfaceSolid = Color(0xFFEBE4DB)

@Composable
private fun ModeChoiceChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = HijrahRadii.Input,
        color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
        border = BorderStroke(1.dp, if (selected) HijrahGold else HijrahBorderSubtle),
        modifier = modifier
            .heightIn(min = 52.dp)
            .clip(HijrahRadii.Input)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) HijrahGold else HijrahBrown, modifier = Modifier.size(18.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) HijrahWhite else HijrahDarkBrown
            )
        }
    }
}

@Composable
private fun LegendRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
    }
}

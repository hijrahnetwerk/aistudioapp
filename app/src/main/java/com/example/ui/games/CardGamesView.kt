package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DestinationDuelCard
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
import kotlinx.coroutines.delay

private enum class CardStage { TITLE, PLAYING, FINISHED }

private data class MemoryTileItem(
    val uid: String,
    val pairId: String,
    val title: String,
    val subtitle: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

@Composable
fun MemoryCardsGame(
    ageGroup: GameAgeGroup,
    isMuted: Boolean,
    onGameFinished: (score: Int, won: Boolean) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(CardStage.TITLE) }
    val cards = remember { mutableStateListOf<MemoryTileItem>() }
    var firstIdx by remember { mutableStateOf<Int?>(null) }
    var secondIdx by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var isCheckingPair by remember { mutableStateOf(false) }

    fun startMemoryGame() {
        HijrahSoundEffects.playTap(isMuted)
        val pairs = if (ageGroup == GameAgeGroup.KINDEREN) {
            HijrahGamesCatalog.kidsMemoryPairs
        } else {
            HijrahGamesCatalog.adultMemoryPairs
        }
        val deck = pairs.flatMap { pair ->
            listOf(
                MemoryTileItem("${pair.pairId}_a", pair.pairId, pair.frontLabel, pair.frontSub),
                MemoryTileItem("${pair.pairId}_b", pair.pairId, pair.matchLabel, pair.matchSub)
            )
        }.shuffled()
        cards.clear()
        cards.addAll(deck)
        firstIdx = null
        secondIdx = null
        moves = 0
        matchedPairs = 0
        isCheckingPair = false
        stage = CardStage.PLAYING
    }

    LaunchedEffect(firstIdx, secondIdx) {
        val a = firstIdx
        val b = secondIdx
        if (a != null && b != null) {
            isCheckingPair = true
            delay(780L)
            val cardA = cards[a]
            val cardB = cards[b]
            if (cardA.pairId == cardB.pairId) {
                HijrahSoundEffects.playMatch(isMuted)
                cards[a] = cardA.copy(isMatched = true)
                cards[b] = cardB.copy(isMatched = true)
                matchedPairs++
                if (matchedPairs >= 6) {
                    val finalScore = (320 - moves * 12).coerceAtLeast(80)
                    delay(300L)
                    stage = CardStage.FINISHED
                    HijrahSoundEffects.playVictory(isMuted)
                    onGameFinished(finalScore, true)
                }
            } else {
                HijrahSoundEffects.playWrong(isMuted)
                cards[a] = cardA.copy(isFaceUp = false)
                cards[b] = cardB.copy(isFaceUp = false)
            }
            firstIdx = null
            secondIdx = null
            isCheckingPair = false
        }
    }

    when (stage) {
        CardStage.TITLE -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "بطاقات الذاكرة", style = HijrahArabicStyles.KufiAccent, color = HijrahGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Steden & Cultuur Memory", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Zoek de 6 bijpassende paren bij elkaar. Niveau '${ageGroup.labelNl}' draait om woorden, steden en praktische begrippen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahWhite.copy(alpha = 0.85f)
                    )
                }
                HijrahPrimaryButton(
                    text = "Memory starten",
                    leadingIcon = Icons.Rounded.Style,
                    onClick = ::startMemoryGame,
                    modifier = Modifier.fillMaxWidth().testTag("start_memory_button")
                )
            }
        }

        CardStage.PLAYING -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = HijrahRadii.Pill,
                        color = HijrahGoldTint,
                        border = BorderStroke(1.dp, HijrahBorderGold)
                    ) {
                        Text(
                            text = "Paren: $matchedPairs / 6",
                            style = MaterialTheme.typography.labelLarge,
                            color = HijrahDarkBrown,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        shape = HijrahRadii.Pill,
                        color = HijrahSurfaceTinted,
                        border = BorderStroke(1.dp, HijrahBorderSubtle)
                    ) {
                        Text(
                            text = "Zetten: $moves",
                            style = MaterialTheme.typography.labelLarge,
                            color = HijrahBrown,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(cards, key = { _, item -> item.uid }) { idx, item ->
                        FlippingMemoryCard(
                            item = item,
                            onClick = {
                                if (isCheckingPair || item.isFaceUp || item.isMatched) return@FlippingMemoryCard
                                HijrahSoundEffects.playFlip(isMuted)
                                cards[idx] = item.copy(isFaceUp = true)
                                if (firstIdx == null) {
                                    firstIdx = idx
                                } else {
                                    secondIdx = idx
                                    moves++
                                }
                            }
                        )
                    }
                }
            }
        }

        CardStage.FINISHED -> {
            val finalScore = (320 - moves * 12).coerceAtLeast(80)
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
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = "Gewonnen", tint = HijrahGold, modifier = Modifier.size(48.dp))
                        Text("Alle 6 paren gevonden!", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                        Text("Aantal zetten: $moves", style = MaterialTheme.typography.bodyMedium, color = HijrahWhite.copy(alpha = 0.85f))
                        Text("$finalScore punten", style = MaterialTheme.typography.displayMedium, color = HijrahGold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HijrahPrimaryButton(text = "Nog een keer spelen", leadingIcon = Icons.Rounded.Refresh, onClick = ::startMemoryGame, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(text = "Terug naar alle spellen", onClick = onExitToHub, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun FlippingMemoryCard(
    item: MemoryTileItem,
    onClick: () -> Unit
) {
    val density = LocalDensity.current.density
    val rotation by animateFloatAsState(
        targetValue = if (item.isFaceUp || item.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "flip_${item.uid}"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(HijrahRadii.Input)
            .clickable(enabled = !item.isFaceUp && !item.isMatched, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Surface(
                shape = HijrahRadii.Input,
                color = HijrahDarkBrown,
                border = BorderStroke(1.5.dp, HijrahBorderGold),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "هـ",
                        style = HijrahArabicStyles.KufiDisplay.copy(fontSize = 26.sp),
                        color = HijrahGold
                    )
                }
            }
        } else {
            Surface(
                shape = HijrahRadii.Input,
                color = if (item.isMatched) HijrahGoldTint else HijrahWhite,
                border = BorderStroke(if (item.isMatched) 2.dp else 1.dp, if (item.isMatched) HijrahGold else HijrahRust),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = HijrahDarkBrown,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = HijrahTextMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

enum class DuelStatKey(val labelNl: String) {
    FAMILY("Gezin & Scholen"),
    MOSQUES("Moskeeën & Sfeer"),
    LANGUAGE("Taaltoegankelijkheid"),
    CARE("Wonen & Zorg")
}

private fun DestinationDuelCard.statValue(key: DuelStatKey): Int = when (key) {
    DuelStatKey.FAMILY -> familyAndSchools
    DuelStatKey.MOSQUES -> mosquesAndCommunity
    DuelStatKey.LANGUAGE -> languageEase
    DuelStatKey.CARE -> livingAndCare
}

@Composable
fun CountryDuelCardsGame(
    ageGroup: GameAgeGroup,
    isMuted: Boolean,
    onGameFinished: (score: Int, won: Boolean) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(CardStage.TITLE) }
    var rounds by remember { mutableStateOf<List<Pair<DestinationDuelCard, DestinationDuelCard>>>(emptyList()) }
    var roundIdx by remember { mutableIntStateOf(0) }
    var playerWins by remember { mutableIntStateOf(0) }
    var opponentWins by remember { mutableIntStateOf(0) }
    var chosenStat by remember { mutableStateOf<DuelStatKey?>(null) }

    fun startDuel() {
        HijrahSoundEffects.playTap(isMuted)
        val shuffled = HijrahGamesCatalog.destinationDuelCards.shuffled()
        val pairs = (0 until 5).map { i -> shuffled[i] to shuffled[i + 5] }
        rounds = pairs
        roundIdx = 0
        playerWins = 0
        opponentWins = 0
        chosenStat = null
        stage = CardStage.PLAYING
    }

    fun pickStat(stat: DuelStatKey) {
        if (chosenStat != null) return
        val (mine, theirs) = rounds[roundIdx]
        chosenStat = stat
        val myVal = mine.statValue(stat)
        val theirVal = theirs.statValue(stat)
        when {
            myVal > theirVal -> {
                HijrahSoundEffects.playMatch(isMuted)
                playerWins++
            }
            theirVal > myVal -> {
                HijrahSoundEffects.playWrong(isMuted)
                opponentWins++
            }
            else -> HijrahSoundEffects.playFlip(isMuted)
        }
    }

    fun nextRound() {
        HijrahSoundEffects.playTap(isMuted)
        if (roundIdx + 1 >= rounds.size) {
            stage = CardStage.FINISHED
            val won = playerWins >= opponentWins
            val totalScore = playerWins * 60 + 40
            HijrahSoundEffects.playVictory(isMuted)
            onGameFinished(totalScore, won)
        } else {
            roundIdx++
            chosenStat = null
        }
    }

    when (stage) {
        CardStage.TITLE -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "تحدي البلدان", style = HijrahArabicStyles.KufiAccent, color = HijrahGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Landen-Kwartet Duel", style = MaterialTheme.typography.headlineMedium, color = HijrahWhite)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vergelijk 5 rondes lang islamitische bestemmingen. Kies steeds de sterkste eigenschap op jouw kaart en scoor meer punten dan de verborgen kaart!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahWhite.copy(alpha = 0.85f)
                    )
                }
                HijrahElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Speelstijl (${ageGroup.shortNl})", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Elke stadskaart heeft cijfers van 1 tot 10 voor Gezin & Scholen, Moskeeën & Sfeer, Taaltoegankelijkheid en Wonen & Zorg.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextSecondary
                    )
                }
                HijrahPrimaryButton(
                    text = "Duel starten",
                    leadingIcon = Icons.Rounded.PlayArrow,
                    onClick = ::startDuel,
                    modifier = Modifier.fillMaxWidth().testTag("start_duel_button")
                )
            }
        }

        CardStage.PLAYING -> {
            val (myCard, oppCard) = rounds[roundIdx]
            val revealed = chosenStat != null
            val density = LocalDensity.current.density
            val oppRotation by animateFloatAsState(
                targetValue = if (revealed) 180f else 0f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
                label = "opp_duel_flip"
            )

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ronde ${roundIdx + 1} van 5", style = MaterialTheme.typography.titleSmall, color = HijrahBrown)
                    Surface(
                        shape = HijrahRadii.Pill,
                        color = HijrahDarkBrown
                    ) {
                        Text(
                            text = "Stand: Jij $playerWins – $opponentWins Tegenkaart",
                            style = MaterialTheme.typography.labelMedium,
                            color = HijrahGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Tegenstander kaart (dicht tot een eigenschap wordt gekozen)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            rotationY = oppRotation
                            cameraDistance = 12f * density
                        }
                ) {
                    if (oppRotation <= 90f) {
                        HijrahDarkCard(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Verborgen Bestemmingskaart", style = MaterialTheme.typography.titleMedium, color = HijrahWhite)
                                    Text("Kies hieronder een eigenschap om te vergelijken", style = MaterialTheme.typography.bodySmall, color = HijrahGold)
                                }
                                Icon(Icons.Rounded.Style, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(28.dp))
                            }
                        }
                    } else {
                        val activeStat = chosenStat ?: DuelStatKey.FAMILY
                        val theirScore = oppCard.statValue(activeStat)
                        val myScore = myCard.statValue(activeStat)
                        HijrahDarkCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { rotationY = 180f },
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(oppCard.arabicName, style = HijrahArabicStyles.KufiAccent, color = HijrahGold)
                                    Text(oppCard.cityAndCountry, style = MaterialTheme.typography.titleLarge, color = HijrahWhite)
                                }
                                Surface(
                                    shape = HijrahRadii.Chip,
                                    color = Color(0x26C9A227),
                                    border = BorderStroke(1.dp, HijrahBorderGold)
                                ) {
                                    Text(
                                        text = "${activeStat.labelNl}: $theirScore/10",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = HijrahGold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val outcome = when {
                                myScore > theirScore -> "Jij wint deze ronde ($myScore vs $theirScore)!"
                                theirScore > myScore -> "Tegenkaart scoort hoger ($theirScore vs $myScore)."
                                else -> "Gelijkspel ($myScore vs $theirScore)!"
                            }
                            Text(outcome, style = MaterialTheme.typography.labelLarge, color = HijrahWhite)
                        }
                    }
                }

                // Speler kaart met 4 klikbare eigenschappen
                HijrahElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HijrahBorderGold
                ) {
                    Text(myCard.arabicName, style = HijrahArabicStyles.KufiAccent, color = HijrahRust)
                    Text(myCard.cityAndCountry, style = MaterialTheme.typography.headlineSmall, color = HijrahDarkBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(myCard.summary, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    DuelStatKey.entries.forEach { key ->
                        val value = myCard.statValue(key)
                        val isChosen = chosenStat == key
                        Surface(
                            shape = HijrahRadii.Input,
                            color = if (isChosen) HijrahGoldTint else HijrahSurfaceTinted,
                            border = BorderStroke(if (isChosen) 2.dp else 1.dp, if (isChosen) HijrahGold else HijrahBorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .heightIn(min = 50.dp)
                                .clip(HijrahRadii.Input)
                                .clickable(enabled = !revealed) { pickStat(key) }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(key.labelNl, style = MaterialTheme.typography.labelLarge, color = HijrahDarkBrown)
                                    Text("$value / 10", style = MaterialTheme.typography.titleSmall, color = HijrahRust)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { value / 10f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(HijrahRadii.Pill),
                                    color = if (isChosen) HijrahGold else HijrahBrown,
                                    trackColor = HijrahSurfaceSoftBrown,
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = revealed, enter = fadeIn()) {
                    HijrahPrimaryButton(
                        text = if (roundIdx + 1 >= rounds.size) "Bekijk eindstand" else "Volgende ronde",
                        leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = ::nextRound,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        CardStage.FINISHED -> {
            val won = playerWins >= opponentWins
            val totalScore = playerWins * 60 + 40
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
                        Icon(
                            imageVector = if (won) Icons.Rounded.EmojiEvents else Icons.Rounded.AutoAwesome,
                            contentDescription = "Eindstand",
                            tint = HijrahGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (won) "Duel Gewonnen!" else "Spannend Duel!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = HijrahWhite
                        )
                        Text(
                            text = "Eindstand: $playerWins – $opponentWins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahWhite.copy(alpha = 0.85f)
                        )
                        Text("$totalScore punten", style = MaterialTheme.typography.displayMedium, color = HijrahGold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HijrahPrimaryButton(text = "Opnieuw duelleren", leadingIcon = Icons.Rounded.Refresh, onClick = ::startDuel, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                HijrahSecondaryButton(text = "Terug naar alle spellen", onClick = onExitToHub, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

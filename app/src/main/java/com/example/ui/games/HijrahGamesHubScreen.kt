package com.example.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.GameAgeGroup
import com.example.data.GameScoreRecord
import com.example.data.HijrahGameId
import com.example.ui.components.HijrahDarkCard
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.i18n.LocalAppStrings
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

@Composable
fun HijrahGamesHubScreen(
    gameScores: List<GameScoreRecord>,
    onRecordGameResult: (HijrahGameId, Int, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var activeGame by remember { mutableStateOf<HijrahGameId?>(null) }
    var ageGroup by remember { mutableStateOf(GameAgeGroup.KINDEREN) }
    var isMuted by remember { mutableStateOf(false) }

    BackHandler {
        if (activeGame != null) {
            HijrahSoundEffects.playTap(isMuted)
            activeGame = null
        } else {
            onBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HijrahWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            // Compacte top HUD-balk
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahWhite,
                        border = BorderStroke(1.dp, HijrahBorderSubtle)
                    ) {
                        IconButton(
                            onClick = {
                                HijrahSoundEffects.playTap(isMuted)
                                if (activeGame != null) activeGame = null else onBack()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("games_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = strings.back,
                                tint = HijrahDarkBrown
                            )
                        }
                    }
                    Column {
                        Text(
                            text = activeGame?.titleNl ?: strings.gamesHubTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = HijrahDarkBrown
                        )
                        Text(
                            text = activeGame?.genreNl ?: strings.gamesHubSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahTextMuted
                        )
                    }
                }

                // Mute / Unmute geluidsknop
                Surface(
                    shape = HijrahRadii.Input,
                    color = if (isMuted) HijrahSurfaceTinted else HijrahGoldTint,
                    border = BorderStroke(1.dp, if (isMuted) HijrahBorderSubtle else HijrahBorderGold)
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("games_mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = if (isMuted) "Geluid aanzetten" else "Geluid dempen",
                            tint = HijrahDarkBrown
                        )
                    }
                }
            }

            val current = activeGame
            if (current == null) {
                val totalPoints = gameScores.sumOf { it.bestScore }
                val totalPlayed = gameScores.sumOf { it.gamesPlayed }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("games_hub_list"),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        HijrahDarkCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ألعاب الهجرة", style = HijrahArabicStyles.KufiAccent, color = HijrahGold)
                                    Text(
                                        "Samen leren & spelen rondom Hijrah",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = HijrahWhite
                                    )
                                }
                                Surface(
                                    shape = HijrahRadii.Input,
                                    color = Color(0x26C9A227),
                                    border = BorderStroke(1.dp, HijrahBorderGold)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(18.dp))
                                        Text("$totalPoints pt", style = MaterialTheme.typography.labelLarge, color = HijrahWhite)
                                        Text("$totalPlayed potten", style = MaterialTheme.typography.labelSmall, color = HijrahGold)
                                    }
                                }
                            }
                        }
                    }

                    // Leeftijd / Niveau schakelaar
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AgeSelectorPill(
                                label = GameAgeGroup.KINDEREN.labelNl,
                                icon = Icons.Rounded.ChildCare,
                                selected = ageGroup == GameAgeGroup.KINDEREN,
                                onClick = {
                                    HijrahSoundEffects.playTap(isMuted)
                                    ageGroup = GameAgeGroup.KINDEREN
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("age_kids_chip")
                            )
                            AgeSelectorPill(
                                label = GameAgeGroup.VOLWASSENEN.labelNl,
                                icon = Icons.Rounded.FamilyRestroom,
                                selected = ageGroup == GameAgeGroup.VOLWASSENEN,
                                onClick = {
                                    HijrahSoundEffects.playTap(isMuted)
                                    ageGroup = GameAgeGroup.VOLWASSENEN
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("age_adults_chip")
                            )
                        }
                    }

                    items(HijrahGameId.entries, key = { it.id }) { game ->
                        val stats = gameScores.firstOrNull { it.gameId == game.id }
                        GameCatalogCard(
                            game = game,
                            icon = gameIcon(game),
                            stats = stats,
                            ageGroup = ageGroup,
                            onPlay = {
                                HijrahSoundEffects.playTap(isMuted)
                                activeGame = game
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            } else {
                // Actieve Spel-Shell (zonder afleidende onderbalk)
                when (current) {
                    HijrahGameId.BOARD_HIJRAH -> BoardJourneyGame(
                        ageGroup = ageGroup,
                        isMuted = isMuted,
                        onGameFinished = { score, won -> onRecordGameResult(current, score, won) },
                        onExitToHub = { activeGame = null },
                        modifier = Modifier.weight(1f)
                    )
                    HijrahGameId.QUIZ_ARENA -> QuizArenaGame(
                        ageGroup = ageGroup,
                        isMuted = isMuted,
                        onGameFinished = { score, won -> onRecordGameResult(current, score, won) },
                        onExitToHub = { activeGame = null },
                        modifier = Modifier.weight(1f)
                    )
                    HijrahGameId.CARDS_MEMORY -> MemoryCardsGame(
                        ageGroup = ageGroup,
                        isMuted = isMuted,
                        onGameFinished = { score, won -> onRecordGameResult(current, score, won) },
                        onExitToHub = { activeGame = null },
                        modifier = Modifier.weight(1f)
                    )
                    HijrahGameId.CARDS_DUEL -> CountryDuelCardsGame(
                        ageGroup = ageGroup,
                        isMuted = isMuted,
                        onGameFinished = { score, won -> onRecordGameResult(current, score, won) },
                        onExitToHub = { activeGame = null },
                        modifier = Modifier.weight(1f)
                    )
                    HijrahGameId.WORDS_PUZZLE -> WordBuilderGame(
                        ageGroup = ageGroup,
                        isMuted = isMuted,
                        onGameFinished = { score, won -> onRecordGameResult(current, score, won) },
                        onExitToHub = { activeGame = null },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun gameIcon(game: HijrahGameId): ImageVector = when (game) {
    HijrahGameId.BOARD_HIJRAH -> Icons.Rounded.Casino
    HijrahGameId.QUIZ_ARENA -> Icons.Rounded.Quiz
    HijrahGameId.CARDS_MEMORY -> Icons.Rounded.GridView
    HijrahGameId.CARDS_DUEL -> Icons.Rounded.Style
    HijrahGameId.WORDS_PUZZLE -> Icons.Rounded.Spellcheck
}

@Composable
private fun AgeSelectorPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = HijrahRadii.Input,
        color = if (selected) HijrahRust else HijrahSurfaceTinted,
        border = BorderStroke(1.dp, if (selected) HijrahRust else HijrahBorderSubtle),
        modifier = modifier
            .heightIn(min = 50.dp)
            .clip(HijrahRadii.Input)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) HijrahWhite else HijrahBrown, modifier = Modifier.size(18.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) HijrahWhite else HijrahDarkBrown
            )
        }
    }
}

@Composable
private fun GameCatalogCard(
    game: HijrahGameId,
    icon: ImageVector,
    stats: GameScoreRecord?,
    ageGroup: GameAgeGroup,
    onPlay: () -> Unit
) {
    HijrahElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}"),
        onClick = onPlay,
        borderColor = HijrahBorderGold
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(HijrahRadii.Input)
                    .background(HijrahDarkBrown)
                    .border(1.dp, HijrahBorderGold, HijrahRadii.Input),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = HijrahGold, modifier = Modifier.size(26.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(game.arabicTag, style = HijrahArabicStyles.KufiAccent, color = HijrahRust)
                Text(game.titleNl, style = MaterialTheme.typography.titleLarge, color = HijrahDarkBrown)
                Text(game.genreNl, style = MaterialTheme.typography.labelMedium, color = HijrahBrown)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(game.descriptionNl, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = HijrahRadii.Pill,
                color = HijrahGoldTint,
                border = BorderStroke(1.dp, HijrahBorderGold),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (stats != null && stats.gamesPlayed > 0L) {
                        "Record: ${stats.bestScore} pt • ${stats.gamesWon}/${stats.gamesPlayed} gewonnen"
                    } else {
                        "Niveau: ${ageGroup.shortNl}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahDarkBrown,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            HijrahPrimaryButton(
                text = "Spelen",
                leadingIcon = Icons.Rounded.PlayArrow,
                onClick = onPlay,
                modifier = Modifier.widthIn(min = 112.dp)
            )
        }
    }
}

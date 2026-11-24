package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.LocalOnSelectLanguage
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun LanguageSelectorCard(
    modifier: Modifier = Modifier
) {
    val currentLanguage = LocalAppLanguage.current
    val strings = LocalAppStrings.current
    val onSelectLanguage = LocalOnSelectLanguage.current

    HijrahElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("language_selector_card"),
        borderColor = HijrahBorderGold,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = HijrahRadii.Chip,
                color = HijrahGoldTint,
                border = BorderStroke(1.dp, HijrahBorderGold)
            ) {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Translate,
                        contentDescription = null,
                        tint = HijrahDarkBrown,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.languageSectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = HijrahDarkBrown
                )
                Text(
                    text = strings.languageSectionSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.entries.forEach { lang ->
                val selected = lang == currentLanguage
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                    border = BorderStroke(
                        1.dp,
                        if (selected) HijrahGold else HijrahBorderSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clip(HijrahRadii.Chip)
                        .clickable { onSelectLanguage(lang) }
                        .testTag("language_option_${lang.code}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = HijrahGold,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 2.dp)
                            )
                        }
                        Text(
                            text = lang.nativeLabel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selected) HijrahWhite else HijrahDarkBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageCompactToggleChip(
    modifier: Modifier = Modifier
) {
    val currentLanguage = LocalAppLanguage.current
    val strings = LocalAppStrings.current
    val onSelectLanguage = LocalOnSelectLanguage.current
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            shape = HijrahRadii.Input,
            color = HijrahWhite,
            border = BorderStroke(1.dp, HijrahBorderGold),
            shadowElevation = 2.dp,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clip(HijrahRadii.Input)
                .clickable { expanded = true }
                .testTag("top_language_toggle_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Translate,
                    contentDescription = strings.cdLanguageToggle,
                    tint = HijrahRust,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = currentLanguage.shortBadge,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = HijrahDarkBrown
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = HijrahWhite
        ) {
            AppLanguage.entries.forEach { lang ->
                val selected = lang == currentLanguage
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${lang.nativeLabel} (${lang.shortBadge})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selected) HijrahRust else HijrahDarkBrown
                            )
                        }
                    },
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = HijrahRust,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Translate,
                                contentDescription = null,
                                tint = HijrahBrown,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelectLanguage(lang)
                    },
                    modifier = Modifier.testTag("dropdown_language_${lang.code}")
                )
            }
        }
    }
}

@Composable
fun LanguagePillBar(
    modifier: Modifier = Modifier
) {
    val currentLanguage = LocalAppLanguage.current
    val onSelectLanguage = LocalOnSelectLanguage.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLanguage.entries.forEach { lang ->
            val selected = lang == currentLanguage
            Surface(
                shape = HijrahRadii.Chip,
                color = if (selected) HijrahDarkBrown else HijrahSurfaceTinted,
                border = BorderStroke(1.dp, if (selected) HijrahGold else HijrahBorderSubtle),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(HijrahRadii.Chip)
                    .clickable { onSelectLanguage(lang) }
                    .testTag("pill_language_${lang.code}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = lang.nativeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) HijrahWhite else HijrahDarkBrown
                    )
                }
            }
        }
    }
}

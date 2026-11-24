package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedLabel
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahShadowWarm
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

enum class HijrahTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("home", R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home, "nav_tab_home"),
    DISCOVER("discover", R.string.nav_discover, Icons.Rounded.Explore, Icons.Outlined.Explore, "nav_tab_discover"),
    NAVIGATION("navigation", R.string.nav_navigation, Icons.Rounded.Map, Icons.Outlined.Map, "nav_tab_navigation"),
    COMMUNITY("community", R.string.nav_community, Icons.Rounded.Forum, Icons.Outlined.Forum, "nav_tab_community"),
    PROFILE("profile", R.string.nav_profile, Icons.Rounded.Person, Icons.Outlined.Person, "nav_tab_profile")
}

@Composable
fun HijrahTopBar(
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    hasPendingNotice: Boolean,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(HijrahRadii.Chip)
                    .background(HijrahDarkBrown)
                    .border(1.dp, HijrahBorderGold, HijrahRadii.Chip),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "هـ",
                    style = HijrahArabicStyles.KufiDisplay.copy(fontSize = 18.sp),
                    color = HijrahGold
                )
            }
            Column {
                Text(
                    text = strings.appName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = HijrahDarkBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (language == AppLanguage.ARABIC) "Hijrah Netwerk" else "شبكة الهجرة",
                    style = HijrahArabicStyles.KufiAccent.copy(fontSize = 12.sp, lineHeight = 14.sp),
                    color = HijrahBrown
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LanguageCompactToggleChip()

            Surface(
                shape = HijrahRadii.Input,
                color = HijrahWhite,
                border = BorderStroke(1.dp, HijrahBorderSubtle),
                shadowElevation = 2.dp
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = strings.cdSearch,
                        tint = HijrahDarkBrown
                    )
                }
            }

            Surface(
                shape = HijrahRadii.Input,
                color = HijrahWhite,
                border = BorderStroke(1.dp, HijrahBorderSubtle),
                shadowElevation = 2.dp
            ) {
                Box {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsNone,
                            contentDescription = strings.cdNotifications,
                            tint = HijrahDarkBrown
                        )
                    }
                    if (hasPendingNotice) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 11.dp, end = 11.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(HijrahRust)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HijrahElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = HijrahBorderSubtle,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(
            elevation = 8.dp,
            shape = HijrahRadii.LargeCard,
            ambientColor = HijrahShadowWarm,
            spotColor = HijrahShadowWarm
        )
        .clip(HijrahRadii.LargeCard)
        .background(HijrahWhite)
        .border(1.dp, borderColor, HijrahRadii.LargeCard)
        .let { base ->
            if (onClick != null) base.clickable(onClick = onClick) else base
        }
        .padding(contentPadding)

    Column(
        modifier = cardModifier,
        content = content
    )
}

@Composable
fun HijrahDarkCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(
            elevation = 10.dp,
            shape = HijrahRadii.LargeCard,
            ambientColor = HijrahShadowWarm,
            spotColor = HijrahShadowWarm
        )
        .clip(HijrahRadii.LargeCard)
        .background(HijrahDarkBrown)
        .border(1.dp, HijrahBorderGold, HijrahRadii.LargeCard)
        .let { base ->
            if (onClick != null) base.clickable(onClick = onClick) else base
        }
        .padding(contentPadding)

    Column(
        modifier = cardModifier,
        content = content
    )
}

@Composable
fun HijrahPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = HijrahRadii.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = HijrahRust,
            contentColor = HijrahWhite,
            disabledContainerColor = HijrahSurfaceTinted,
            disabledContentColor = HijrahTextMuted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier.defaultMinSize(minHeight = 50.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun HijrahSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = HijrahRadii.Button,
        border = BorderStroke(1.dp, HijrahBorderSubtle),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = HijrahWhite,
            contentColor = HijrahDarkBrown
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = HijrahBrown,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun HijrahReliabilityBadge(
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(HijrahRadii.Chip)
            .background(HijrahGoldTint)
            .border(1.dp, HijrahBorderGold, HijrahRadii.Chip)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Verified,
            contentDescription = null,
            tint = HijrahGold,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = HijrahDarkBrown,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HijrahSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = HijrahDarkBrown
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextMuted
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = HijrahRust,
                modifier = Modifier
                    .clip(HijrahRadii.Chip)
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HijrahEmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    HijrahElevatedCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(22.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(HijrahRadii.Input)
                    .background(HijrahSurfaceTinted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HijrahBrown,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = HijrahDarkBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HijrahTextSecondary
                )
                if (actionText != null && onActionClick != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HijrahSecondaryButton(
                        text = actionText,
                        onClick = onActionClick
                    )
                }
            }
        }
    }
}

@Composable
fun HijrahBottomNavBar(
    currentRoute: String,
    onSelectTab: (HijrahTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                ambientColor = HijrahShadowWarm,
                spotColor = HijrahShadowWarm
            ),
        color = HijrahWhite,
        border = BorderStroke(1.dp, HijrahBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HijrahTab.entries.forEach { tab ->
                val selected = currentRoute == tab.route
                val tabLabel = tab.localizedLabel(strings)
                val pillColor by animateColorAsState(
                    targetValue = if (selected) HijrahDarkBrown else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "navPill"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (selected) HijrahWhite else HijrahTextMuted,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "navIcon"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (selected) HijrahDarkBrown else HijrahTextMuted,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "navLabel"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(HijrahRadii.Button)
                        .clickable { onSelectTab(tab) }
                        .testTag(tab.testTag)
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(HijrahRadii.Pill)
                            .background(pillColor)
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tabLabel,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = tabLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

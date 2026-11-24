package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.CountryGuide
import com.example.data.FacilityCategory
import com.example.data.FacilityLocation
import com.example.data.SavedFavorite
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedLabel
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahGoldTint
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahRustTint
import com.example.ui.theme.HijrahShadowWarm
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HijrahInteractiveMapCard(
    countries: List<CountryGuide>,
    locations: List<FacilityLocation>,
    favorites: List<SavedFavorite>,
    selectedCountryId: String?,
    onSelectCountry: (CountryGuide) -> Unit,
    onOpenLocationDetail: (FacilityLocation) -> Unit,
    onToggleSaveLocation: (FacilityLocation, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var activeLocation by remember(locations) {
        mutableStateOf(locations.firstOrNull())
    }

    val animatedZoom by animateFloatAsState(
        targetValue = zoom,
        animationSpec = tween(durationMillis = 160),
        label = "map_zoom"
    )
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HijrahDarkCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("interactive_map_card"),
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.navMapTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = HijrahWhite
                    )
                    Text(
                        text = "${strings.navMapSubPrefix} (${locations.size})",
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahWhite.copy(alpha = 0.8f)
                    )
                }
                Icon(Icons.Rounded.Public, contentDescription = null, tint = HijrahGold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(HijrahRadii.MediumCard)
                    .background(HijrahWhite)
                    .border(1.5.dp, HijrahBorderGold, HijrahRadii.MediumCard)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            val newZoom = (zoom * gestureZoom).coerceIn(1f, 2.6f)
                            zoom = newZoom
                            val maxPanX = size.width * (newZoom - 1f) * 0.55f
                            val maxPanY = size.height * (newZoom - 1f) * 0.55f
                            panX = (panX + pan.x).coerceIn(-maxPanX, maxPanX)
                            panY = (panY + pan.y).coerceIn(-maxPanY, maxPanY)
                        }
                    }
                    .testTag("interactive_map_canvas")
            ) {
                val mapWidthPx = constraints.maxWidth.toFloat()
                val mapHeightPx = constraints.maxHeight.toFloat()
                val density = LocalDensity.current

                fun projectX(normX: Float): Float {
                    val centered = (normX - 0.5f) * animatedZoom + 0.5f
                    return centered * mapWidthPx + panX
                }

                fun projectY(normY: Float): Float {
                    val centered = (normY - 0.5f) * animatedZoom + 0.5f
                    return centered * mapHeightPx + panY
                }

                // Achtergrond vector-kaart met regio-contouren, grid en routes
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridStroke = Color(0x16684A25)
                    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    for (i in 1..4) {
                        val y = size.height * (i / 5f)
                        drawLine(gridStroke, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f, pathEffect = dash)
                    }
                    for (i in 1..5) {
                        val x = size.width * (i / 6f)
                        drawLine(gridStroke, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5f, pathEffect = dash)
                    }

                    // Gestileerde landmassa-silhouetten (Noord-Afrika, Anatolië/Levant, Zuidoost-Azië)
                    fun drawRegionPolygon(points: List<Pair<Float, Float>>) {
                        val path = Path()
                        points.forEachIndexed { idx, (nx, ny) ->
                            val px = projectX(nx)
                            val py = projectY(ny)
                            if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        path.close()
                        drawPath(path = path, color = Color(0x12684A25))
                        drawPath(path = path, color = Color(0x2E684A25), style = Stroke(width = 2.dp.toPx()))
                    }

                    // Noord-Afrika (Marokko -> Egypte)
                    drawRegionPolygon(
                        listOf(
                            0.08f to 0.30f, 0.31f to 0.27f, 0.55f to 0.42f,
                            0.55f to 0.66f, 0.35f to 0.64f, 0.08f to 0.58f
                        )
                    )
                    // Turkije & Levant
                    drawRegionPolygon(
                        listOf(
                            0.38f to 0.14f, 0.56f to 0.14f, 0.67f to 0.34f,
                            0.64f to 0.48f, 0.52f to 0.42f, 0.38f to 0.29f
                        )
                    )
                    // Zuidoost-Azië / Maleisisch Schiereiland
                    drawRegionPolygon(
                        listOf(
                            0.71f to 0.55f, 0.92f to 0.55f, 0.94f to 0.82f,
                            0.73f to 0.82f
                        )
                    )

                    // Subtiele verbindingslijnen tussen landen
                    if (countries.size >= 2) {
                        for (idx in 0 until countries.lastIndex) {
                            val a = countries[idx]
                            val b = countries[idx + 1]
                            drawLine(
                                color = Color(0x45C9A227),
                                start = Offset(projectX(a.mapX), projectY(a.mapY)),
                                end = Offset(projectX(b.mapX), projectY(b.mapY)),
                                strokeWidth = 2.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )
                        }
                    }
                }

                // Landbadges op de kaart
                countries.forEach { country ->
                    val isSelected = country.id == selectedCountryId
                    val px = projectX(country.mapX).roundToInt() - with(density) { 38.dp.roundToPx() }
                    val py = projectY(country.mapY).roundToInt() - with(density) { 16.dp.roundToPx() }
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = if (isSelected) HijrahRust else HijrahDarkBrown,
                        border = BorderStroke(1.dp, HijrahGold),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .offset { IntOffset(px, py) }
                            .clip(HijrahRadii.Chip)
                            .clickable { onSelectCountry(country) }
                            .testTag("map_country_chip_${country.id}")
                    ) {
                        Text(
                            text = country.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = HijrahWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Voorzieningen-pinnen (48dp aanraakvlak voor toegankelijkheid)
                val pinHalfPx = with(density) { 24.dp.roundToPx() }
                locations.forEach { loc ->
                    val isSelected = activeLocation?.id == loc.id
                    val px = projectX(loc.mapX).roundToInt() - pinHalfPx
                    val py = projectY(loc.mapY).roundToInt() - pinHalfPx
                    val pinColor = mapCategoryColor(loc.category)

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(px, py) }
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { activeLocation = loc }
                            .testTag("map_pin_${loc.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 38.dp else 32.dp)
                                .shadow(if (isSelected) 8.dp else 4.dp, CircleShape, ambientColor = HijrahShadowWarm)
                                .clip(CircleShape)
                                .background(if (isSelected) pinColor else HijrahWhite)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.5.dp,
                                    color = if (isSelected) HijrahGold else pinColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mapCategoryIcon(loc.category),
                                contentDescription = "${loc.name} (${loc.category.localizedLabel(strings)})",
                                tint = if (isSelected) HijrahWhite else pinColor,
                                modifier = Modifier.size(if (isSelected) 19.dp else 16.dp)
                            )
                        }
                    }
                }

                // Zoom- en Reset-bediening rechtsonder
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MapControlButton(
                        icon = Icons.Rounded.Add,
                        contentDescription = "Inzoomen op kaart",
                        onClick = { zoom = (zoom + 0.4f).coerceAtMost(2.6f) },
                        modifier = Modifier.testTag("map_zoom_in_button")
                    )
                    MapControlButton(
                        icon = Icons.Rounded.Remove,
                        contentDescription = "Uitzoomen op kaart",
                        onClick = {
                            val nextZoom = (zoom - 0.4f).coerceAtLeast(1f)
                            zoom = nextZoom
                            if (nextZoom == 1f) {
                                panX = 0f
                                panY = 0f
                            }
                        },
                        modifier = Modifier.testTag("map_zoom_out_button")
                    )
                    MapControlButton(
                        icon = Icons.Rounded.CenterFocusStrong,
                        contentDescription = "Kaartweergave herstellen",
                        onClick = {
                            zoom = 1f
                            panX = 0f
                            panY = 0f
                        },
                        modifier = Modifier.testTag("map_reset_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compacte legenda voor essentiele locatietypes
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    FacilityCategory.MOSQUES,
                    FacilityCategory.COMMUNITY_CENTERS,
                    FacilityCategory.SHOPS,
                    FacilityCategory.SCHOOLS,
                    FacilityCategory.HEALTHCARE
                ).forEach { cat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = mapCategoryIcon(cat),
                            contentDescription = null,
                            tint = HijrahGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = cat.localizedLabel(strings),
                            style = MaterialTheme.typography.labelSmall,
                            color = HijrahWhite.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        }

        // Geselecteerde locatie-callout kaart onder de kaart
        AnimatedVisibility(visible = activeLocation != null, enter = fadeIn()) {
            activeLocation?.let { loc ->
                val isSaved = favorites.any { it.targetId == loc.id }
                HijrahElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_selected_callout"),
                    borderColor = HijrahBorderGold,
                    onClick = { onOpenLocationDetail(loc) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(HijrahRadii.Chip)
                                    .background(HijrahRustTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = mapCategoryIcon(loc.category),
                                    contentDescription = null,
                                    tint = mapCategoryColor(loc.category),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "${loc.cityName} • ${loc.category.localizedLabel(strings)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HijrahRust
                                )
                                Text(
                                    text = loc.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HijrahDarkBrown
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleSaveLocation(loc, isSaved) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = strings.save,
                                    tint = if (isSaved) HijrahRust else HijrahBrown
                                )
                            }
                            IconButton(
                                onClick = { activeLocation = null },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = strings.close,
                                    tint = HijrahTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = loc.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = HijrahTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = HijrahRadii.Chip,
                        color = HijrahGoldTint,
                        border = BorderStroke(1.dp, HijrahBorderGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Praktische tip: ${loc.practicalNote}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = HijrahDarkBrown,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = HijrahRadii.Chip,
        color = HijrahWhite,
        border = BorderStroke(1.dp, HijrahBorderSubtle),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = HijrahDarkBrown,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun mapCategoryColor(category: FacilityCategory): Color = when (category) {
    FacilityCategory.MOSQUES -> HijrahDarkBrown
    FacilityCategory.COMMUNITY_CENTERS -> HijrahRust
    FacilityCategory.SHOPS -> HijrahBrown
    FacilityCategory.SCHOOLS -> HijrahRust
    FacilityCategory.HEALTHCARE -> HijrahDarkBrown
    FacilityCategory.GOVERNMENT -> HijrahBrown
    FacilityCategory.ALL -> HijrahDarkBrown
}

fun mapCategoryIcon(category: FacilityCategory): ImageVector = when (category) {
    FacilityCategory.SCHOOLS -> Icons.Rounded.School
    FacilityCategory.HEALTHCARE -> Icons.Rounded.LocalHospital
    FacilityCategory.MOSQUES -> Icons.Rounded.Mosque
    FacilityCategory.COMMUNITY_CENTERS -> Icons.Rounded.Groups
    FacilityCategory.SHOPS -> Icons.Rounded.Storefront
    FacilityCategory.GOVERNMENT -> Icons.Rounded.AccountBalance
    FacilityCategory.ALL -> Icons.Rounded.Map
}

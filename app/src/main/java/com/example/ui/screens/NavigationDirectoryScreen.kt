package com.example.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CityGuide
import com.example.data.CountryGuide
import com.example.data.ExperienceSubmission
import com.example.data.FacilityCategory
import com.example.data.FacilityLocation
import com.example.data.HijrahNavigationCatalog
import com.example.data.SavedFavorite
import com.example.ui.components.HijrahElevatedCard
import com.example.ui.components.HijrahEmptyStateCard
import com.example.ui.components.HijrahInteractiveMapCard
import com.example.ui.components.HijrahPrimaryButton
import com.example.ui.components.HijrahReliabilityBadge
import com.example.ui.components.HijrahSectionHeader
import com.example.ui.components.mapCategoryIcon
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.localizedLabel
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahSurfaceSoftBrown
import com.example.ui.theme.HijrahSurfaceTinted
import com.example.ui.theme.HijrahTextMuted
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

private enum class NavDisplayMode { LIST, MAP, COMPARE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDirectoryScreen(
    focusCountry: String,
    favorites: List<SavedFavorite>,
    approvedExperiences: List<ExperienceSubmission>,
    onToggleFavorite: (targetType: String, targetId: String, title: String, subtitle: String, saved: Boolean) -> Unit,
    onOpenShareExperience: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCountryId by remember {
        mutableStateOf(
            HijrahNavigationCatalog.countries.firstOrNull {
                it.name.equals(focusCountry, ignoreCase = true)
            }?.id
        )
    }
    var selectedCityId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf(FacilityCategory.ALL) }
    var displayMode by remember { mutableStateOf(NavDisplayMode.MAP) }

    var compareLeftId by remember { mutableStateOf(HijrahNavigationCatalog.countries.first().id) }
    var compareRightId by remember { mutableStateOf(HijrahNavigationCatalog.countries.getOrElse(1) { HijrahNavigationCatalog.countries.first() }.id) }

    var detailCountry by remember { mutableStateOf<CountryGuide?>(null) }
    var detailFacility by remember { mutableStateOf<FacilityLocation?>(null) }

    val visibleCities = remember(selectedCountryId) {
        if (selectedCountryId == null) HijrahNavigationCatalog.cities
        else HijrahNavigationCatalog.cities.filter { it.countryId == selectedCountryId }
    }

    val filteredLocations = remember(searchQuery, selectedCountryId, selectedCityId, selectedCategory) {
        val q = searchQuery.trim().lowercase()
        HijrahNavigationCatalog.locations.filter { loc ->
            val countryOk = selectedCountryId == null || loc.countryId == selectedCountryId
            val cityOk = selectedCityId == null || loc.cityId == selectedCityId
            val catOk = selectedCategory == FacilityCategory.ALL || loc.category == selectedCategory
            val queryOk = q.isEmpty() ||
                loc.name.lowercase().contains(q) ||
                loc.cityName.lowercase().contains(q) ||
                loc.topic.lowercase().contains(q) ||
                loc.description.lowercase().contains(q)
            countryOk && cityOk && catOk && queryOk
        }
    }

    val filteredCountries = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) HijrahNavigationCatalog.countries
        else HijrahNavigationCatalog.countries.filter {
            it.name.lowercase().contains(q) || it.regionLabel.lowercase().contains(q)
        }
    }

    val strings = LocalAppStrings.current

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
                .testTag("navigation_directory_list"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = strings.navTitle,
                        style = MaterialTheme.typography.displayMedium,
                        color = HijrahDarkBrown
                    )
                    Text(
                        text = strings.navSub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HijrahTextSecondary
                    )
                }
            }

            // Search & Mode Switcher (Lijst | Kaart | Vergelijken)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = HijrahBrown) },
                        placeholder = { Text(strings.navSearchPlaceholder) },
                        singleLine = true,
                        shape = HijrahRadii.Input,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("navigation_search_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeTogglePill(
                            label = strings.navModeList,
                            tagSuffix = "list",
                            icon = Icons.Rounded.ViewList,
                            selected = displayMode == NavDisplayMode.LIST,
                            onClick = { displayMode = NavDisplayMode.LIST },
                            modifier = Modifier.weight(1f)
                        )
                        ModeTogglePill(
                            label = strings.navModeMap,
                            tagSuffix = "map",
                            icon = Icons.Rounded.Map,
                            selected = displayMode == NavDisplayMode.MAP,
                            onClick = { displayMode = NavDisplayMode.MAP },
                            modifier = Modifier.weight(1f)
                        )
                        ModeTogglePill(
                            label = strings.navModeCompare,
                            tagSuffix = "compare",
                            icon = Icons.Rounded.CompareArrows,
                            selected = displayMode == NavDisplayMode.COMPARE,
                            onClick = { displayMode = NavDisplayMode.COMPARE },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Country & Category Filters (shown for LIST & MAP)
            if (displayMode != NavDisplayMode.COMPARE) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterPill(
                                label = strings.catAll,
                                selected = selectedCountryId == null,
                                onClick = {
                                    selectedCountryId = null
                                    selectedCityId = null
                                }
                            )
                            HijrahNavigationCatalog.countries.forEach { country ->
                                FilterPill(
                                    label = country.name,
                                    selected = selectedCountryId == country.id,
                                    onClick = {
                                        selectedCountryId = if (selectedCountryId == country.id) null else country.id
                                        selectedCityId = null
                                    }
                                )
                            }
                        }

                        if (visibleCities.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterPill(
                                    label = strings.catAll,
                                    selected = selectedCityId == null,
                                    onClick = { selectedCityId = null }
                                )
                                visibleCities.forEach { city ->
                                    FilterPill(
                                        label = city.name,
                                        selected = selectedCityId == city.id,
                                        onClick = {
                                            selectedCityId = if (selectedCityId == city.id) null else city.id
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FacilityCategory.entries.forEach { cat ->
                                FilterPill(
                                    label = cat.localizedLabel(strings),
                                    selected = selectedCategory == cat,
                                    accentRust = true,
                                    onClick = { selectedCategory = cat }
                                )
                            }
                        }
                    }
                }
            }

            when (displayMode) {
                NavDisplayMode.MAP -> {
                    item {
                        HijrahInteractiveMapCard(
                            countries = filteredCountries,
                            locations = filteredLocations,
                            favorites = favorites,
                            selectedCountryId = selectedCountryId,
                            onSelectCountry = { country ->
                                selectedCountryId = if (selectedCountryId == country.id) null else country.id
                                selectedCityId = null
                                detailCountry = country
                            },
                            onOpenLocationDetail = { loc -> detailFacility = loc },
                            onToggleSaveLocation = { loc, saved ->
                                onToggleFavorite(
                                    "LOCATION",
                                    loc.id,
                                    loc.name,
                                    "${loc.cityName} • ${loc.category.labelNl}",
                                    saved
                                )
                            }
                        )
                    }
                    if (filteredLocations.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.Map,
                                title = "Geen locaties op de kaart voor dit filter",
                                description = "Pas je land-, stad- of categoriefilter aan om moskeeën, gemeenschapscentra en halal winkels te zien.",
                                actionText = "Filters resetten",
                                onActionClick = {
                                    selectedCountryId = null
                                    selectedCityId = null
                                    selectedCategory = FacilityCategory.ALL
                                    searchQuery = ""
                                }
                            )
                        }
                    } else {
                        items(filteredLocations, key = { it.id }) { loc ->
                            val isSaved = favorites.any { it.targetId == loc.id }
                            FacilityLocationCard(
                                location = loc,
                                isSaved = isSaved,
                                onToggleSave = {
                                    onToggleFavorite("LOCATION", loc.id, loc.name, "${loc.cityName} • ${loc.category.labelNl}", isSaved)
                                },
                                onClick = { detailFacility = loc }
                            )
                        }
                    }
                }

                NavDisplayMode.COMPARE -> {
                    item {
                        CountryComparisonSection(
                            leftCountryId = compareLeftId,
                            rightCountryId = compareRightId,
                            onSelectLeft = { compareLeftId = it },
                            onSelectRight = { compareRightId = it }
                        )
                    }
                }

                NavDisplayMode.LIST -> {
                    item {
                        HijrahSectionHeader(
                            title = "Landen",
                            subtitle = "Klik voor verblijf, onderwijs en zorgkader"
                        )
                    }
                    items(filteredCountries, key = { it.id }) { country ->
                        val isSaved = favorites.any { it.targetId == country.id }
                        CountryGuideCard(
                            country = country,
                            isSaved = isSaved,
                            onToggleSave = {
                                onToggleFavorite("COUNTRY", country.id, country.name, country.regionLabel, isSaved)
                            },
                            onClick = { detailCountry = country }
                        )
                    }

                    item {
                        HijrahSectionHeader(
                            title = "Steden",
                            subtitle = "${visibleCities.size} geselecteerde steden"
                        )
                    }
                    items(visibleCities, key = { it.id }) { city ->
                        val isSaved = favorites.any { it.targetId == city.id }
                        CityGuideCard(
                            city = city,
                            isSaved = isSaved,
                            onToggleSave = {
                                onToggleFavorite("CITY", city.id, city.name, city.countryName, isSaved)
                            },
                            onClick = {
                                selectedCountryId = city.countryId
                                selectedCityId = city.id
                            }
                        )
                    }

                    item {
                        HijrahSectionHeader(
                            title = "Praktische voorzieningen",
                            subtitle = "${filteredLocations.size} locatiethema's gevonden"
                        )
                    }

                    if (filteredLocations.isEmpty()) {
                        item {
                            HijrahEmptyStateCard(
                                icon = Icons.Rounded.Map,
                                title = "Geen voorzieningen voor dit filter",
                                description = "Pas je land-, stad- of categoriefilter aan om voorzieningen te bekijken.",
                                actionText = "Filters resetten",
                                onActionClick = {
                                    selectedCountryId = null
                                    selectedCityId = null
                                    selectedCategory = FacilityCategory.ALL
                                    searchQuery = ""
                                }
                            )
                        }
                    } else {
                        items(filteredLocations, key = { it.id }) { loc ->
                            val isSaved = favorites.any { it.targetId == loc.id }
                            FacilityLocationCard(
                                location = loc,
                                isSaved = isSaved,
                                onToggleSave = {
                                    onToggleFavorite("LOCATION", loc.id, loc.name, "${loc.cityName} • ${loc.category.labelNl}", isSaved)
                                },
                                onClick = { detailFacility = loc }
                            )
                        }
                    }
                }
            }
        }
    }

    detailCountry?.let { country ->
        val relatedExperiences = approvedExperiences.filter {
            it.countryName.equals(country.name, ignoreCase = true)
        }
        ModalBottomSheet(
            onDismissRequest = { detailCountry = null },
            containerColor = HijrahWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HijrahReliabilityBadge(label = country.reliability.localizedLabel(strings))
                    Text(
                        text = country.arabicName,
                        style = HijrahArabicStyles.KufiDisplay,
                        color = HijrahBrown
                    )
                }
                Text(country.name, style = MaterialTheme.typography.displayMedium, color = HijrahDarkBrown)
                Text("${country.regionLabel} • Talen: ${country.languages}", style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)

                DetailInfoBlock("Verblijf & Visum", country.residenceSummary)
                DetailInfoBlock("Onderwijs & Gezin", country.educationSummary)
                DetailInfoBlock("Zorg & Voorzieningen", country.healthcareSummary)
                DetailInfoBlock("Kostenindicatie", country.costIndication)

                Text(
                    text = "Laatst gecontroleerd: ${country.lastChecked} • ${relatedExperiences.size} goedgekeurde ervaring(en)",
                    style = MaterialTheme.typography.labelSmall,
                    color = HijrahBrown
                )

                HijrahPrimaryButton(
                    text = "Ervaring over ${country.name} insturen",
                    onClick = {
                        detailCountry = null
                        onOpenShareExperience()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    detailFacility?.let { loc ->
        ModalBottomSheet(
            onDismissRequest = { detailFacility = null },
            containerColor = HijrahWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HijrahReliabilityBadge(label = loc.reliability.localizedLabel(strings))
                Text("${loc.cityName} • ${loc.category.localizedLabel(strings)}", style = MaterialTheme.typography.labelMedium, color = HijrahRust)
                Text(loc.name, style = MaterialTheme.typography.headlineLarge, color = HijrahDarkBrown)
                Text(loc.description, style = MaterialTheme.typography.bodyLarge, color = HijrahTextSecondary)
                DetailInfoBlock("Praktische aandachtspunt", loc.practicalNote)
                Text("Laatst gecontroleerd: ${loc.lastChecked}", style = MaterialTheme.typography.labelSmall, color = HijrahTextMuted)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}


@Composable
private fun CountryComparisonSection(
    leftCountryId: String,
    rightCountryId: String,
    onSelectLeft: (String) -> Unit,
    onSelectRight: (String) -> Unit
) {
    val left = HijrahNavigationCatalog.countries.firstOrNull { it.id == leftCountryId }
        ?: HijrahNavigationCatalog.countries.first()
    val right = HijrahNavigationCatalog.countries.firstOrNull { it.id == rightCountryId }
        ?: HijrahNavigationCatalog.countries.last()

    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HijrahSectionHeader(
            title = strings.navModeCompare,
            subtitle = strings.navSub
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CountrySelectorRow(
                label = "A",
                selected = left,
                onSelect = onSelectLeft,
                modifier = Modifier.weight(1f)
            )
            CountrySelectorRow(
                label = "B",
                selected = right,
                onSelect = onSelectRight,
                modifier = Modifier.weight(1f)
            )
        }

        ComparisonRowCard("Taalomgeving", left.languages, right.languages)
        ComparisonRowCard("Verblijfsopties", left.residenceSummary, right.residenceSummary)
        ComparisonRowCard("Onderwijs & Gezin", left.educationSummary, right.educationSummary)
        ComparisonRowCard("Zorg & Toegang", left.healthcareSummary, right.healthcareSummary)
        ComparisonRowCard("Kostenindicatie", left.costIndication, right.costIndication)
        ComparisonRowCard("Status controle", left.reliability.localizedLabel(strings), right.reliability.localizedLabel(strings))
    }
}

@Composable
private fun CountrySelectorRow(
    label: String,
    selected: CountryGuide,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HijrahElevatedCard(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = HijrahRust)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HijrahNavigationCatalog.countries.forEach { country ->
                val isSelected = country.id == selected.id
                Surface(
                    shape = HijrahRadii.Chip,
                    color = if (isSelected) HijrahDarkBrown else HijrahSurfaceTinted,
                    modifier = Modifier
                        .clip(HijrahRadii.Chip)
                        .clickable { onSelect(country.id) }
                ) {
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) HijrahWhite else HijrahDarkBrown,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonRowCard(
    aspect: String,
    leftValue: String,
    rightValue: String
) {
    HijrahElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(aspect, style = MaterialTheme.typography.titleSmall, color = HijrahRust)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = leftValue,
                style = MaterialTheme.typography.bodySmall,
                color = HijrahDarkBrown,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(HijrahBorderSubtle)
            )
            Text(
                text = rightValue,
                style = MaterialTheme.typography.bodySmall,
                color = HijrahDarkBrown,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CountryGuideCard(
    country: CountryGuide,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    HijrahElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HijrahReliabilityBadge(label = country.reliability.localizedLabel(strings))
            IconButton(onClick = onToggleSave, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = strings.save,
                    tint = if (isSaved) HijrahRust else HijrahBrown
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(country.name, style = MaterialTheme.typography.headlineMedium, color = HijrahDarkBrown)
            Text(country.arabicName, style = HijrahArabicStyles.KufiAccent, color = HijrahBrown)
        }
        Text("${country.regionLabel} • ${country.languages}", style = MaterialTheme.typography.bodySmall, color = HijrahTextMuted)
        Spacer(modifier = Modifier.height(6.dp))
        Text(country.residenceSummary, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
    }
}

@Composable
private fun CityGuideCard(
    city: CityGuide,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    HijrahElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.LocationCity, contentDescription = null, tint = HijrahBrown, modifier = Modifier.size(18.dp))
                Text("${city.name} (${city.countryName})", style = MaterialTheme.typography.titleMedium, color = HijrahDarkBrown)
            }
            IconButton(onClick = onToggleSave, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Stad opslaan",
                    tint = if (isSaved) HijrahRust else HijrahBrown
                )
            }
        }
        Text(city.environmentSummary, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Gezinspassendheid: ${city.familySuitability}", style = MaterialTheme.typography.bodySmall, color = HijrahBrown)
    }
}

@Composable
private fun FacilityLocationCard(
    location: FacilityLocation,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    HijrahElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(HijrahRadii.Chip)
                        .background(HijrahSurfaceTinted),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mapCategoryIcon(location.category),
                        contentDescription = null,
                        tint = HijrahRust,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text("${location.cityName} • ${location.category.localizedLabel(strings)}", style = MaterialTheme.typography.labelSmall, color = HijrahRust)
                    Text(location.name, style = MaterialTheme.typography.titleSmall, color = HijrahDarkBrown)
                }
            }
            IconButton(onClick = onToggleSave, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = strings.save,
                    tint = if (isSaved) HijrahRust else HijrahBrown
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(location.description, style = MaterialTheme.typography.bodySmall, color = HijrahTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        HijrahReliabilityBadge(label = "${location.reliability.localizedLabel(strings)} • ${location.lastChecked}")
    }
}

@Composable
private fun DetailInfoBlock(title: String, body: String) {
    Surface(
        shape = HijrahRadii.Input,
        color = HijrahSurfaceTinted,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = HijrahBrown)
            Spacer(modifier = Modifier.height(2.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, color = HijrahDarkBrown)
        }
    }
}

@Composable
private fun ModeTogglePill(
    label: String,
    tagSuffix: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = HijrahRadii.Chip,
        color = if (selected) HijrahDarkBrown else HijrahWhite,
        border = BorderStroke(1.dp, if (selected) HijrahBorderGold else HijrahBorderSubtle),
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(HijrahRadii.Chip)
            .clickable(onClick = onClick)
            .testTag("nav_mode_$tagSuffix")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) HijrahGold else HijrahBrown,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = if (selected) HijrahWhite else HijrahDarkBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    accentRust: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        selected && accentRust -> HijrahRust
        selected -> HijrahDarkBrown
        else -> HijrahSurfaceSoftBrown
    }
    Surface(
        shape = HijrahRadii.Chip,
        color = bg,
        modifier = Modifier
            .clip(HijrahRadii.Chip)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (selected) HijrahWhite else HijrahDarkBrown,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}


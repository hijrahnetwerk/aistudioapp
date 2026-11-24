package com.example.ui.i18n

import com.example.data.DocumentStatus
import com.example.data.FacilityCategory
import com.example.data.HijrahPhase
import com.example.data.ReliabilityStatus
import com.example.data.ReminderCadence
import com.example.ui.components.HijrahTab

fun HijrahTab.localizedLabel(strings: HijrahStrings): String = when (this) {
    HijrahTab.HOME -> strings.navHome
    HijrahTab.DISCOVER -> strings.navDiscover
    HijrahTab.NAVIGATION -> strings.navNavigation
    HijrahTab.COMMUNITY -> strings.navCommunity
    HijrahTab.PROFILE -> strings.navProfile
}

fun HijrahPhase.localizedTitle(strings: HijrahStrings): String = when (this) {
    HijrahPhase.ORIENTATIE -> strings.phaseOrientationTitle
    HijrahPhase.ONDERZOEK -> strings.phaseResearchTitle
    HijrahPhase.VOORBEREIDING -> strings.phasePreparationTitle
    HijrahPhase.INTEGRATIE -> strings.phaseIntegrationTitle
}

fun HijrahPhase.localizedSubtitle(strings: HijrahStrings): String = when (this) {
    HijrahPhase.ORIENTATIE -> strings.phaseOrientationSub
    HijrahPhase.ONDERZOEK -> strings.phaseResearchSub
    HijrahPhase.VOORBEREIDING -> strings.phasePreparationSub
    HijrahPhase.INTEGRATIE -> strings.phaseIntegrationSub
}

fun DocumentStatus.localizedLabel(strings: HijrahStrings): String = when (this) {
    DocumentStatus.NOG_REGELEN -> strings.docStatusTodo
    DocumentStatus.IN_AANVRAAG -> strings.docStatusInProgress
    DocumentStatus.GEREED -> strings.docStatusReady
}

fun ReminderCadence.localizedLabel(strings: HijrahStrings): String = when (this) {
    ReminderCadence.WEEKLY -> strings.cadenceWeekly
    ReminderCadence.DAILY -> strings.cadenceDaily
    ReminderCadence.OFF -> strings.cadenceOff
}

fun FacilityCategory.localizedLabel(strings: HijrahStrings): String = when (this) {
    FacilityCategory.ALL -> strings.catAll
    FacilityCategory.MOSQUES -> strings.catMosques
    FacilityCategory.COMMUNITY_CENTERS -> strings.catCenters
    FacilityCategory.SHOPS -> strings.catShops
    FacilityCategory.SCHOOLS -> strings.catSchools
    FacilityCategory.HEALTHCARE -> strings.catHealthcare
    FacilityCategory.GOVERNMENT -> strings.catGovernment
}

fun ReliabilityStatus.localizedLabel(strings: HijrahStrings): String = when (this) {
    ReliabilityStatus.VERIFIED -> strings.relVerified
    ReliabilityStatus.ORIENTING -> strings.relOrienting
    ReliabilityStatus.CHECK_NEEDED -> strings.relCheckNeeded
}

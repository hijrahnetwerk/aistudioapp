package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Hijrah Netwerk — Strikte Huisstijlkleuren
 *
 * Uitsluitend de vijf vastgestelde merkkleuren en transparante afgeleiden daarvan:
 * - Wit (#FFFFFF): Hoofdachtergrond
 * - Bruin (#684A25): Primaire merktoon
 * - Donkerbruin (#4F3A23): Donkere vlakken en hoofdtekst
 * - Roest (#DF842C): Primaire actieknoppen en accenten
 * - Goud (#C9A227): Subtiele premium details en statusaccenten (nooit als groot vlak)
 */
val HijrahWhite = Color(0xFFFFFFFF)
val HijrahBrown = Color(0xFF684A25)
val HijrahDarkBrown = Color(0xFF4F3A23)
val HijrahRust = Color(0xFFDF842C)
val HijrahGold = Color(0xFFC9A227)

// Transparante contrast- en dieptetokens strikt op de wit/bruin-as
val HijrahSurfaceTinted = Color(0x0A684A25)   // 4% bruin voor subtiele invoervelden en chips
val HijrahSurfaceSoftBrown = Color(0x14684A25) // 8% bruin voor inactieve balken en scheiders
val HijrahBorderSubtle = Color(0x22684A25)    // 13% bruin voor kaartomlijning
val HijrahBorderStrong = Color(0x3D4F3A23)    // 24% donkerbruin voor actieve kaders
val HijrahBorderGold = Color(0x73C9A227)      // 45% goud voor subtiele kwaliteitsrandjes
val HijrahGoldTint = Color(0x1AC9A227)        // 10% goud voor kleine statusbadges
val HijrahRustTint = Color(0x1ADF842C)        // 10% roest voor subtiele actie-badges
val HijrahTextSecondary = Color(0xBF4F3A23)   // 75% donkerbruin voor leesbare secundaire tekst
val HijrahTextMuted = Color(0x8C4F3A23)       // 55% donkerbruin voor bijschriften
val HijrahShadowWarm = Color(0x244F3A23)      // Zachte warme schaduw op wit
val HijrahScrimDark = Color(0xCC4F3A23)       // 80% donkerbruin overlay op hero-fotografie
val HijrahErrorRust = Color(0xFFB84A1B)       // Donkere roesttint voor foutmeldingen binnen palet

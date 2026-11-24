package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Afgeronde vormen conform de Hijrah Netwerk ontwerprichtlijnen:
 * - 16–26 dp voor grotere kaarten en containers
 * - 12–18 dp voor knoppen, chips en kleinere interactieve elementen
 */
object HijrahRadii {
    val HeroCard = RoundedCornerShape(26.dp)
    val LargeCard = RoundedCornerShape(22.dp)
    val MediumCard = RoundedCornerShape(18.dp)
    val Button = RoundedCornerShape(16.dp)
    val Input = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(12.dp)
    val Pill = RoundedCornerShape(50)
}

val HijrahShapes = Shapes(
    extraSmall = HijrahRadii.Chip,
    small = HijrahRadii.Input,
    medium = HijrahRadii.MediumCard,
    large = HijrahRadii.LargeCard,
    extraLarge = HijrahRadii.HeroCard
)

package com.example.data

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class HijrahPhase(
    val titleNl: String,
    val subtitleNl: String,
    val stepIndex: Int
) {
    ORIENTATIE(
        titleNl = "Oriëntatie",
        subtitleNl = "Verkennen of en waarom hijrah bij jouw gezin past",
        stepIndex = 0
    ),
    ONDERZOEK(
        titleNl = "Onderzoek",
        subtitleNl = "Landen, steden, onderwijs en verblijfsopties vergelijken",
        stepIndex = 1
    ),
    VOORBEREIDING(
        titleNl = "Voorbereiding",
        subtitleNl = "Documenten, financiën en vertrekplan stap voor stap afronden",
        stepIndex = 2
    ),
    INTEGRATIE(
        titleNl = "Aankomst & Integratie",
        subtitleNl = "Wonen, scholen, zorg en zusterschap ter plaatse opbouwen",
        stepIndex = 3
    );

    companion object {
        fun fromRaw(raw: String?): HijrahPhase =
            entries.firstOrNull { it.name == raw } ?: ORIENTATIE
    }
}

enum class DocumentStatus(val labelNl: String) {
    NOG_REGELEN("Nog regelen"),
    IN_AANVRAAG("In aanvraag"),
    GEREED("Gereed");

    fun next(): DocumentStatus = when (this) {
        NOG_REGELEN -> IN_AANVRAAG
        IN_AANVRAAG -> GEREED
        GEREED -> NOG_REGELEN
    }

    companion object {
        fun fromRaw(raw: String?): DocumentStatus =
            entries.firstOrNull { it.name == raw } ?: NOG_REGELEN
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayAlias: String = "",
    val originCountry: String = "",
    val currentCity: String = "",
    val hijrahPhase: String = HijrahPhase.ORIENTATIE.name,
    val focusCountry: String = "",
    val focusCity: String = "",
    val onboardingCompleted: Boolean = false,
    val lastVisitedTitle: String = "Startgids: Hoe begin je met oriënteren?",
    val lastVisitedRoute: String = "discover",
    val completedStepsCount: Long = 0L,
    val totalStepsCount: Long = 4L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun phaseEnum(): HijrahPhase = HijrahPhase.fromRaw(hijrahPhase)
}

@Keep
@IgnoreExtraProperties
data class ChecklistItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val phase: String = HijrahPhase.ORIENTATIE.name,
    val title: String = "",
    val category: String = "Oriëntatie",
    val completed: Boolean = false,
    val sortOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BudgetItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Algemeen",
    val amountEur: Long = 0L,
    val recurringMonthly: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DocumentItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Identiteit",
    val docStatus: String = DocumentStatus.NOG_REGELEN.name,
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun statusEnum(): DocumentStatus = DocumentStatus.fromRaw(docStatus)
}

@Keep
@IgnoreExtraProperties
data class SavedFavorite(
    @DocumentId val id: String = "",
    val userId: String = "",
    val targetType: String = "GUIDE",
    val targetId: String = "",
    val title: String = "",
    val subtitle: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PersonalNote(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val topic: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ExperienceSubmission(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorAlias: String = "",
    val countryName: String = "",
    val cityName: String = "",
    val topic: String = "",
    val title: String = "",
    val story: String = "",
    val prosAndCons: String = "",
    val lessonLearned: String = "",
    val practicalTip: String = "",
    val status: String = "PENDING",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val verifiedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CommunityPost(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorAlias: String = "",
    val groupTitle: String = "",
    val title: String = "",
    val body: String = "",
    val status: String = "PUBLISHED",
    val replyCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CommunityReply(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorAlias: String = "",
    val body: String = "",
    val status: String = "PUBLISHED",
    @ServerTimestamp val createdAt: Timestamp? = null
)

enum class ReminderCadence(val labelNl: String) {
    WEEKLY("Wekelijks"),
    DAILY("Dagelijks"),
    OFF("Uit");

    companion object {
        fun fromRaw(raw: String?): ReminderCadence =
            entries.firstOrNull { it.name == raw } ?: WEEKLY
    }
}

@Keep
@IgnoreExtraProperties
data class NotificationPreferences(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fcmToken: String = "",
    val forumRepliesEnabled: Boolean = true,
    val milestoneRemindersEnabled: Boolean = true,
    val reminderCadence: String = ReminderCadence.WEEKLY.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun cadenceEnum(): ReminderCadence = ReminderCadence.fromRaw(reminderCadence)
}

data class EditorialHighlight(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val reliabilityLabel: String,
    val lastCheckedLabel: String,
    val targetRoute: String,
    @DrawableRes val imageRes: Int? = null
)

data class FaqEntry(
    val id: String,
    val category: String,
    val question: String,
    val answer: String
)

object EditorialCatalog {
    val featuredHighlights: List<EditorialHighlight> = listOf(
        EditorialHighlight(
            id = "guide_orientation_framework",
            category = "Oriëntatiekader",
            title = "Van oriëntatie tot integratie: keuzes zonder tijdsdruk",
            summary = "Een overzicht van de vier fasen die gezinnen helpen om nuchter en weloverwogen afwegingen te maken rondom geloof, gezin, werk en verblijfsrecht.",
            keyPoints = listOf(
                "Breng eerst gezinsprioriteiten en taaldoelen in kaart",
                "Vergelijk verblijfsvoorwaarden los van vakantie-indrukken",
                "Maak onderscheid tussen tijdelijk oriënteren en duurzaam vestigen"
            ),
            reliabilityLabel = "Redactioneel kader",
            lastCheckedLabel = "Laatst gecontroleerd: november 2026",
            targetRoute = "discover",
            imageRes = R.drawable.img_city_featured
        ),
        EditorialHighlight(
            id = "guide_residence_documents",
            category = "Documenten & Visum",
            title = "Checklist basisdocumenten en legalisaties",
            summary = "Welke officiële documenten (geboorteaktes, huwelijksakte, diploma's en apostilles) vrijwel in elk traject als eerste worden gevraagd.",
            keyPoints = listOf(
                "Controleer geldigheid van paspoorten voor alle gezinsleden",
                "Informeer vooraf naar apostille- of ambassadelegalisatie",
                "Bewaar digitale én gewaarmerkte papieren kopieën gescheiden"
            ),
            reliabilityLabel = "Praktische checklist",
            lastCheckedLabel = "Laatst gecontroleerd: november 2026",
            targetRoute = "tools",
            imageRes = null
        ),
        EditorialHighlight(
            id = "guide_family_education",
            category = "Onderwijs & Gezin",
            title = "Onderwijsopties vergelijken voor kinderen",
            summary = "Aandachtspunten bij het beoordelen van islamitisch onderwijs, internationaal onderwijs en thuisonderwijs in een nieuwe woonplaats.",
            keyPoints = listOf(
                "Let op voertaal, erkenning en aansluiting op leeftijd",
                "Vraag naar inschrijftermijnen en wachttijden vóór vertrek",
                "Combineer schoolkeuze met reistijd vanuit mogelijke wijken"
            ),
            reliabilityLabel = "Oriëntatiegids",
            lastCheckedLabel = "Laatst gecontroleerd: november 2026",
            targetRoute = "navigation",
            imageRes = null
        ),
        EditorialHighlight(
            id = "guide_healthcare_insurance",
            category = "Zorg & Verzekeringen",
            title = "Zorgtoegang en internationale dekking voor gezinnen",
            summary = "Hoe je publieke en private zorgvoorzieningen vergelijkt, inclusief verloskunde, kinderzorg en chronische medicatie.",
            keyPoints = listOf(
                "Controleer wachttijden bij lokale en internationale polissen",
                "Neem medische dossiers in het Engels of Arabisch mee",
                "Kijk naar afstand tot ziekenhuizen met vrouwelijke specialisten"
            ),
            reliabilityLabel = "Geverifieerd kader",
            lastCheckedLabel = "Laatst gecontroleerd: november 2026",
            targetRoute = "discover",
            imageRes = null
        ),
        EditorialHighlight(
            id = "guide_arrival_integration",
            category = "Aankomst & Integratie",
            title = "De eerste 90 dagen na aankomst in een nieuwe stad",
            summary = "Praktische volgorde voor lokaal telefoonnummer, bankrekening, huurcontract, registratie en het vinden van een rustig ritme.",
            keyPoints = listOf(
                "Kies voor de eerste maand een flexibele gemeubileerde woning",
                "Regel verblijfsregistratie voordat je langlopende contracten tekent",
                "Bouw stapsgewijs vertrouwd contact op met zusters in de buurt"
            ),
            reliabilityLabel = "Integratiegids",
            lastCheckedLabel = "Laatst gecontroleerd: november 2026",
            targetRoute = "tools",
            imageRes = null
        )
    )

    val faqEntries: List<FaqEntry> = listOf(
        FaqEntry(
            id = "faq_1",
            category = "Oriëntatie",
            question = "Moet ik al zeker weten dat ik wil emigreren om deze app te gebruiken?",
            answer = "Nee. Hijrah Netwerk is bedoeld om rustig en onafhankelijk te informeren, vanaf het allereerste oriëntatiegesprek tot en met aankomst en integratie."
        ),
        FaqEntry(
            id = "faq_2",
            category = "Privacy",
            question = "Wie kan mijn checklist, budget en notities zien?",
            answer = "Niemand behalve jij. Jouw checklist, documentenoverzicht, budgetplanner, favorieten en notities zijn strikt privé opgeslagen onder jouw eigen account."
        ),
        FaqEntry(
            id = "faq_3",
            category = "Ervaringen",
            question = "Waarom verschijnt een ingezonden ervaring niet meteen openbaar?",
            answer = "Elke ervaring wordt eerst door beheer gecontroleerd op privacy, zorgvuldigheid en relevantie voordat deze voor andere zusters zichtbaar wordt."
        ),
        FaqEntry(
            id = "faq_4",
            category = "Documenten",
            question = "Wanneer begin ik het best met legalisaties en apostilles?",
            answer = "Begin bij voorkeur 3 tot 6 maanden voor een gepland vertrek. Geldigheidstermijnen van uittreksels verschillen echter per land van bestemming."
        )
    )

    val recommendedTopics: List<String> = listOf(
        "Verblijfsvergunning",
        "Onderwijs & Gezin",
        "Huisvesting",
        "Zorg & Verzekeringen",
        "Werk & Ondernemen",
        "Taal & Integratie",
        "Moskeeën & Voorzieningen"
    )

    val originCountrySuggestions: List<String> = listOf(
        "Nederland",
        "België",
        "Duitsland",
        "Frankrijk",
        "Verenigd Koninkrijk",
        "Anders"
    )

    val focusCountrySuggestions: List<String> = listOf(
        "Nog aan het verkennen",
        "Marokko",
        "Egypte",
        "Maleisië",
        "Indonesië",
        "Verenigde Arabische Emiraten",
        "Qatar",
        "Turkije",
        "Jordanië"
    )

    val communityGroups: List<String> = listOf(
        "Alle groepen",
        "Oriëntatie & Vragen",
        "Marokko",
        "Egypte",
        "Maleisië",
        "Onderwijs & Gezin",
        "Werk & Ondernemen"
    )
}

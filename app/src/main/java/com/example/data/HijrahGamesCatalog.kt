package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class GameAgeGroup(val labelNl: String, val shortNl: String) {
    KINDEREN("Kinderen (6–12 jr)", "Kinderen"),
    VOLWASSENEN("Volwassenen & Gezin", "Volwassenen")
}

enum class HijrahGameId(
    val id: String,
    val titleNl: String,
    val genreNl: String,
    val arabicTag: String,
    val descriptionNl: String
) {
    BOARD_HIJRAH(
        id = "BOARD_HIJRAH",
        titleNl = "De Hijrah-Reis",
        genreNl = "Bordspel • 1–4 spelers",
        arabicTag = "رحلة الهجرة",
        descriptionNl = "Dobbel, volg de route over 24 vakjes van voorbereiding tot integratie, pak Barakah-bonussen en beantwoord landvragen."
    ),
    QUIZ_ARENA(
        id = "QUIZ_HIJRAH",
        titleNl = "Hijrah Kennisquiz",
        genreNl = "Quiz • Meerkeuze & Streaks",
        arabicTag = "مسابقة المعرفة",
        descriptionNl = "Test je kennis over islamitische landen, moskeeën, Arabische begroetingen, documenten en wonen met handige hints."
    ),
    CARDS_MEMORY(
        id = "CARDS_MEMORY",
        titleNl = "Steden & Cultuur Memory",
        genreNl = "Kaartspel • Geheugen",
        arabicTag = "بطاقات الذاكرة",
        descriptionNl = "Draai de kaarten om en verbind steden en Arabische begrippen aan hun juiste betekenis of bekende moskee."
    ),
    CARDS_DUEL(
        id = "CARDS_DUEL",
        titleNl = "Landen-Kwartet Duel",
        genreNl = "Kaartspel • Strategie",
        arabicTag = "تحدي البلدان",
        descriptionNl = "Kies elke ronde de sterkste eigenschap van jouw bestemmingskaart en versla de tegenspeler."
    ),
    WORDS_PUZZLE(
        id = "WORDS_HIJRAH",
        titleNl = "Woord & Taal Puzzel",
        genreNl = "Woordspel • Puzzel",
        arabicTag = "ألغاز الكلمات",
        descriptionNl = "Puzzel Arabische reizigerswoorden en emigratiebegrippen letter voor letter op de juiste plek."
    )
}

@Keep
@IgnoreExtraProperties
data class GameScoreRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val gameId: String = "",
    val bestScore: Long = 0L,
    val gamesPlayed: Long = 0L,
    val gamesWon: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class BoardTileKind {
    START, NORMAL, BLESSING, QUIZ_GATE, DELAY, FINISH
}

data class BoardTileSpec(
    val index: Int,
    val titleNl: String,
    val subtitleNl: String,
    val kind: BoardTileKind
)

data class QuizQuestionItem(
    val id: String,
    val countryTag: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val hintText: String,
    val explanation: String
)

data class MemoryCardPair(
    val pairId: String,
    val frontLabel: String,
    val frontSub: String,
    val matchLabel: String,
    val matchSub: String
)

data class DestinationDuelCard(
    val id: String,
    val cityAndCountry: String,
    val arabicName: String,
    val summary: String,
    val familyAndSchools: Int,
    val mosquesAndCommunity: Int,
    val languageEase: Int,
    val livingAndCare: Int
)

data class WordPuzzleItem(
    val id: String,
    val word: String,
    val category: String,
    val meaning: String,
    val hint: String
)

object HijrahGamesCatalog {

    val boardTiles: List<BoardTileSpec> = listOf(
        BoardTileSpec(0, "Start: Thuis", "Intentie & plan", BoardTileKind.START),
        BoardTileSpec(1, "Paspoorten", "Geldigheid checken", BoardTileKind.NORMAL),
        BoardTileSpec(2, "Arabische les", "Eerste begroeting", BoardTileKind.BLESSING),
        BoardTileSpec(3, "Vraagpunt", "Kennischeck", BoardTileKind.QUIZ_GATE),
        BoardTileSpec(4, "Spaardoel", "Budget opstellen", BoardTileKind.NORMAL),
        BoardTileSpec(5, "Inpakken", "Koffers sorteren", BoardTileKind.DELAY),
        BoardTileSpec(6, "Apostille", "Documenten regelen", BoardTileKind.NORMAL),
        BoardTileSpec(7, "Vraagpunt", "Landenkennis", BoardTileKind.QUIZ_GATE),
        BoardTileSpec(8, "Afscheid", "Du'a onderweg", BoardTileKind.BLESSING),
        BoardTileSpec(9, "Vliegveld", "Instappen gezin", BoardTileKind.NORMAL),
        BoardTileSpec(10, "Aankomst", "Eerste dag!", BoardTileKind.NORMAL),
        BoardTileSpec(11, "SIM-kaart", "Vertraging loket", BoardTileKind.DELAY),
        BoardTileSpec(12, "Vraagpunt", "Lokale gewoonte", BoardTileKind.QUIZ_GATE),
        BoardTileSpec(13, "Eerste Masjid", "Salaam zeggen", BoardTileKind.BLESSING),
        BoardTileSpec(14, "Wijk verkennen", "Markt & bakker", BoardTileKind.NORMAL),
        BoardTileSpec(15, "Schoolbezoek", "Klas bekijken", BoardTileKind.NORMAL),
        BoardTileSpec(16, "Vraagpunt", "Zorg & wonen", BoardTileKind.QUIZ_GATE),
        BoardTileSpec(17, "Verblijfspas", "Aanvraag loopt", BoardTileKind.NORMAL),
        BoardTileSpec(18, "Burenhulp", "Kopje thee delen", BoardTileKind.BLESSING),
        BoardTileSpec(19, "Verhuisdoos", "Spullen zoeken", BoardTileKind.DELAY),
        BoardTileSpec(20, "Zusterkring", "Nieuwe contacten", BoardTileKind.NORMAL),
        BoardTileSpec(21, "Vraagpunt", "Taal & integratie", BoardTileKind.QUIZ_GATE),
        BoardTileSpec(22, "Eigen ritme", "Rust in huis", BoardTileKind.BLESSING),
        BoardTileSpec(23, "Thuis!", "Integratie geslaagd", BoardTileKind.FINISH)
    )

    val kidsQuizQuestions: List<QuizQuestionItem> = listOf(
        QuizQuestionItem(
            id = "kq_1",
            countryTag = "Arabisch",
            question = "Wat zeg je als je vriendelijk iemand begroet in een islamitisch land?",
            options = listOf("Assalaamu alaykum", "Tot ziens", "Goedenacht", "Alstublieft"),
            correctIndex = 0,
            hintText = "Het betekent 'Vrede zij met jullie'.",
            explanation = "Met 'Assalaamu alaykum' begroet je broeders en zusters over de hele wereld op een warme manier."
        ),
        QuizQuestionItem(
            id = "kq_2",
            countryTag = "Marokko",
            question = "Welke bekende oude moskee en universiteit staat in de Marokkaanse stad Fes?",
            options = listOf("Al-Qarawiyyin", "Eiffeltoren", "Taj Mahal", "Big Ben"),
            correctIndex = 0,
            hintText = "Hij werd opgericht door een moslimvrouw genaamd Fatima al-Fihri.",
            explanation = "Al-Qarawiyyin in Fes is volgens UNESCO de oudste nog bestaande universiteit ter wereld, opgericht in het jaar 859."
        ),
        QuizQuestionItem(
            id = "kq_3",
            countryTag = "Egypte",
            question = "Welke lange rivier stroomt dwars door de hoofdstad Caïro in Egypte?",
            options = listOf("De Rijn", "De Nijl", "De Amstel", "De Donau"),
            correctIndex = 1,
            hintText = "De naam begint met een N en is heel beroemd in Afrika.",
            explanation = "De Nijl stroomt door heel Egypte en komt bij Caïro uit richting de Middellandse Zee."
        ),
        QuizQuestionItem(
            id = "kq_4",
            countryTag = "Maleisië",
            question = "In welke stad in Maleisië staan de beroemde hoge Petronas-torens naast een groot stadspark?",
            options = listOf("Kuala Lumpur", "Tanger", "Istanbul", "Amman"),
            correctIndex = 0,
            hintText = "Het is de hoofdstad van Maleisië en begint met een K.",
            explanation = "Kuala Lumpur is een moderne, groene hoofdstad met veel halal eetgelegenheden en mooie moskeeën."
        ),
        QuizQuestionItem(
            id = "kq_5",
            countryTag = "Turkije",
            question = "Welke grote stad in Turkije ligt op twee werelddelen tegelijk: Europa én Azië?",
            options = listOf("Istanbul", "Marrakech", "Doha", "Jakarta"),
            correctIndex = 0,
            hintText = "De zeestraat de Bosporus splitst deze stad in twee delen.",
            explanation = "In Istanbul kun je met een bootje binnen een kwartier van Europa naar Azië varen."
        ),
        QuizQuestionItem(
            id = "kq_6",
            countryTag = "Arabisch",
            question = "Wat betekent het Arabische woord 'Shukran'?",
            options = listOf("Dank je wel", "Ik heb honger", "Waar is de school?", "Tot morgen"),
            correctIndex = 0,
            hintText = "Je zegt het wanneer iemand je helpt of iets aangeeft.",
            explanation = "'Shukran' betekent dank je wel. Je hoort het dagelijks in winkels, op school en bij buren."
        ),
        QuizQuestionItem(
            id = "kq_7",
            countryTag = "Indonesië",
            question = "Welk land heeft de grootste moslimbevolking ter wereld en bestaat uit duizenden eilanden?",
            options = listOf("Indonesië", "België", "Zwitserland", "IJsland"),
            correctIndex = 0,
            hintText = "Jakarta en het eiland Java liggen in dit land.",
            explanation = "Indonesië bestaat uit meer dan 17.000 eilanden en kent een enorme gastvrijheid en rijke islamitische cultuur."
        ),
        QuizQuestionItem(
            id = "kq_8",
            countryTag = "Reizen & Voorbereiden",
            question = "Welk document heeft elk kind en elke ouder nodig om met het vliegtuig naar een ander land te reizen?",
            options = listOf("Een geldig paspoort", "Een zwemdiploma", "Een bibliotheekpas", "Een fietsbel"),
            correctIndex = 0,
            hintText = "De douane op het vliegveld stempelt dit boekje.",
            explanation = "Iedere reiziger, ook een baby of kind, heeft een eigen geldig paspoort nodig voor internationale reizen."
        ),
        QuizQuestionItem(
            id = "kq_9",
            countryTag = "Qatar",
            question = "Wat is de naam van de hoofdstad van Qatar aan de Perzische Golf?",
            options = listOf("Doha", "Rabat", "Medina", "Izmir"),
            correctIndex = 0,
            hintText = "Een korte naam van vier letters, beroemd om het Museum of Islamic Art.",
            explanation = "Doha ligt aan zee, heeft een bekende wandelboulevard (de Corniche) en veel gezinsvriendelijke parken."
        ),
        QuizQuestionItem(
            id = "kq_10",
            countryTag = "Arabisch",
            question = "Wat betekent het woord 'Madrasa'?",
            options = listOf("School", "Vliegveld", "Ziekenhuis", "Supermarkt"),
            correctIndex = 0,
            hintText = "Kinderen gaan hier naartoe om te leren lezen, schrijven en rekenen.",
            explanation = "'Madrasa' betekent school in het Arabisch."
        )
    )

    val adultQuizQuestions: List<QuizQuestionItem> = listOf(
        QuizQuestionItem(
            id = "aq_1",
            countryTag = "Documenten",
            question = "Waarvoor dient een 'apostille' op bijvoorbeeld een huwelijks- of geboorteakte bij emigratie?",
            options = listOf(
                "Een internationale stempel zodat het document in aangesloten landen rechtsgeldig is",
                "Een kortingsbon voor een vliegticket naar het buitenland",
                "Een bewijs dat je de lokale taal vloeiend spreekt",
                "Een medische verklaring voor tropische vaccinaties"
            ),
            correctIndex = 0,
            hintText = "In Nederland haal je deze legalisatie meestal bij de rechtbank.",
            explanation = "Een apostille bevestigt de echtheid van een handtekening op een officieel document voor gebruik in het buitenland."
        ),
        QuizQuestionItem(
            id = "aq_2",
            countryTag = "Marokko",
            question = "Hoeveel dagen mogen Nederlandse of Belgische paspoorthouders doorgaans visumvrij in Marokko verblijven voordat verlenging nodig is?",
            options = listOf("90 dagen", "14 dagen", "365 dagen", "7 dagen"),
            correctIndex = 0,
            hintText = "Ongeveer drie maanden.",
            explanation = "Het toeristisch verblijf bedraagt maximaal 90 dagen; voor langer verblijf vraag je lokaal een Carte de Séjour aan."
        ),
        QuizQuestionItem(
            id = "aq_3",
            countryTag = "Oriëntatie",
            question = "Wat is een verstandige eerste stap bij het kiezen van een woonwijk na aankomst in een nieuwe stad?",
            options = listOf(
                "Eerst kortdurend gemeubileerd huren en reistijd naar scholen en voorzieningen testen",
                "Direct op afstand een jaarcontract tekenen zonder de wijk te zien",
                "Alleen afgaan op vakantiefilmpjes uit het hoogseizoen",
                "Geen vragen stellen over contractvoorwaarden of borg"
            ),
            correctIndex = 0,
            hintText = "Flexibiliteit in de eerste weken voorkomt dure fouten.",
            explanation = "Door eerst 2–6 weken tijdelijk te huren ervaar je verkeer, moskee-afstand, schoolroutes en sfeer in het dagelijks leven."
        ),
        QuizQuestionItem(
            id = "aq_4",
            countryTag = "Maleisië",
            question = "Welke voertaal helpt naast het Maleis (Bahasa Melayu) enorm om in Maleisië direct contact te leggen bij scholen en ziekenhuizen?",
            options = listOf("Engels", "Spaans", "Italiaans", "Duits"),
            correctIndex = 0,
            hintText = "Deze taal wordt breed gesproken in het bestuur en internationale onderwijs.",
            explanation = "Engels wordt in Maleisië wijdverbreid gesproken in de zorg, het bedrijfsleven en op internationale scholen."
        ),
        QuizQuestionItem(
            id = "aq_5",
            countryTag = "Onderwijs",
            question = "Waar let je als gezin het eerst op wanneer je scholen in het buitenland vergelijkt?",
            options = listOf(
                "Voertaal, erkenning van diploma's, inschrijftermijnen en aansluiting voor kinderen",
                "Alleen de kleur van het schooluniform",
                "Of de school een groot zwembad heeft",
                "Alleen de afstand tot het vliegveld"
            ),
            correctIndex = 0,
            hintText = "Denk aan doorstroming en wachttijden.",
            explanation = "Internationale, islamitische en lokale scholen hanteren vaak vroege inschrijfrondes en verschillende leerplannen."
        ),
        QuizQuestionItem(
            id = "aq_6",
            countryTag = "Zorg",
            question = "Welke tip voorkomt verrassingen rondom medische zorg na emigratie?",
            options = listOf(
                "Neem medische dossiers in het Engels (of lokaal vertaald) mee en check wachttijden op polissen",
                "Zeg alle zorgverzekeringen op voordat je een nieuw alternatief hebt afgesloten",
                "Neem alleen Nederlandse brieven mee zonder vertaling",
                "Ga ervan uit dat receptmedicatie overal dezelfde naam heeft"
            ),
            correctIndex = 0,
            hintText = "Continuïteit bij chronische zorg of zwangerschap vraagt voorbereiding.",
            explanation = "Vertaalde dossiers en inzicht in lokale privéklinieken of internationale dekking geven veel rust in de eerste maanden."
        ),
        QuizQuestionItem(
            id = "aq_7",
            countryTag = "Egypte",
            question = "Welke wijk in Groot-Caïro staat bij veel expat- en hijrah-gezinnen bekend om taalscholen en voorzieningen?",
            options = listOf("Nasr City / Maadi / 5th Settlement", "Tanger Medina", "Putrajaya", "Üsküdar"),
            correctIndex = 0,
            hintText = "Maadi is groen en geliefd bij gezinnen; Nasr City ligt dicht bij taalscholen.",
            explanation = "Maadi, Nasr City en New Cairo bieden verschillende woonstijlen, internationale scholen en Arabische taalinstituten."
        ),
        QuizQuestionItem(
            id = "aq_8",
            countryTag = "Jordanië",
            question = "Waarom is helder dialect- en standaardtaalverschil belangrijk in Arabischsprekende landen?",
            options = listOf(
                "Officiële stukken zijn in Fusha (MSA), terwijl op straat het lokale dialect (zoals Darija of Levants) wordt gesproken",
                "Er bestaat maar één enkel dialect in alle 22 landen",
                "Straattaal wordt alleen gebruikt op vliegvelden",
                "Fusha wordt nergens geschreven"
            ),
            correctIndex = 0,
            hintText = "Denk aan het verschil tussen Marokkaans Darija en klassiek Arabisch.",
            explanation = "Met Fusha lees je borden en formulieren, terwijl een paar basiszinnen in het lokale dialect meteen deuren openen bij buren en marktkooplieden."
        ),
        QuizQuestionItem(
            id = "aq_9",
            countryTag = "Integratie",
            question = "Wat helpt tijdens de eerste 90 dagen na aankomst het meest tegen heimwee of overweldiging?",
            options = listOf(
                "Een rustig dagritme opbouwen, lokale moskee bezoeken en stap voor stap vertrouwd contact met zusters leggen",
                "Proberen in week één alles voor vijf jaar vooruit op te lossen",
                "Volledig binnen blijven tot alle papieren klaar zijn",
                "Elke dag vergelijken met hoe het thuis in Nederland was"
            ),
            correctIndex = 0,
            hintText = "Kleine stabiele gewoontes geven houvast voor het hele gezin.",
            explanation = "Integratie kost tijd. Een overzichtelijk dagritme rond gebed, boodschappen en rustige kennismaking helpt het hele gezin landen."
        ),
        QuizQuestionItem(
            id = "aq_10",
            countryTag = "VAE & Golf",
            question = "Waarvoor is een geldig arbeidscontract, eigen bedrijf (freezone) of erkend verblijfsprogramma in de VAE meestal vereist?",
            options = listOf(
                "Voor een Emirates ID en meerjarige verblijfsvergunning",
                "Alleen voor het huren van een fiets",
                "Voor een museumbezoek",
                "Om een ansichtkaart te versturen"
            ),
            correctIndex = 0,
            hintText = "Zonder deze ID-kaart kun je vaak geen vast internet of jaarcontract afsluiten.",
            explanation = "De Emirates ID is gekoppeld aan je verblijfsvisum en is onmisbaar voor bankzaken, huurcontracten en schoolinschrijving."
        )
    )

    val kidsMemoryPairs: List<MemoryCardPair> = listOf(
        MemoryCardPair("km_1", "Assalaamu alaykum", "Arabische groet", "Vrede zij met jullie", "Betekenis"),
        MemoryCardPair("km_2", "Masjid", "Arabisch woord", "Moskee", "Gebedsplek"),
        MemoryCardPair("km_3", "Madrasa", "Arabisch woord", "School", "Leren & klas"),
        MemoryCardPair("km_4", "Fes", "Stad in Marokko", "Al-Qarawiyyin", "Oudste universiteit"),
        MemoryCardPair("km_5", "Caïro", "Stad in Egypte", "De Nijl", "Grote rivier"),
        MemoryCardPair("km_6", "Shukran", "Arabisch woord", "Dank je wel", "Beleefdheid")
    )

    val adultMemoryPairs: List<MemoryCardPair> = listOf(
        MemoryCardPair("am_1", "Apostille", "Documenten", "Rechtbankstempel", "Internationale geldigheid"),
        MemoryCardPair("am_2", "Tanger", "Marokko", "Carte de Séjour", "Verblijfskaart"),
        MemoryCardPair("am_3", "Kuala Lumpur", "Maleisië", "Bahasa & Engels", "Dagelijkse voertalen"),
        MemoryCardPair("am_4", "Maadi / Nasr City", "Caïro", "Taalinstituten", "Arabisch leren"),
        MemoryCardPair("am_5", "Sharjah", "VAE", "Emirates ID", "Basis voor contracten"),
        MemoryCardPair("am_6", "Eerste 90 dagen", "Integratie", "Tijdelijk huren", "Wijk rustig testen")
    )

    val destinationDuelCards: List<DestinationDuelCard> = listOf(
        DestinationDuelCard("dc_1", "Tanger, Marokko", "طنجة", "Havenstad dichtbij Europa met scholen en gezinnen.", 8, 9, 6, 7),
        DestinationDuelCard("dc_2", "Kuala Lumpur, Maleisië", "كوالالمبور", "Groene metropool, Engelstalig en veel halal voorzieningen.", 9, 9, 9, 9),
        DestinationDuelCard("dc_3", "Caïro, Egypte", "القاهرة", "Centrum voor Arabische taalstudie met lage vaste lasten.", 7, 10, 7, 6),
        DestinationDuelCard("dc_4", "Istanbul, Turkije", "إسطنبول", "Rijke historie, sterke zorg en levendige wijken.", 8, 9, 5, 8),
        DestinationDuelCard("dc_5", "Sharjah, VAE", "الشارقة", "Gezinsgericht emiraat met sterke infrastructuur.", 9, 9, 8, 9),
        DestinationDuelCard("dc_6", "Amman, Jordanië", "عمّان", "Rustige cultuur en sterk in klassiek Arabisch onderwijs.", 8, 9, 7, 7),
        DestinationDuelCard("dc_7", "Doha, Qatar", "الدوحة", "Veilige gezinswijken, internationale scholen en hoge zorgstandaard.", 9, 9, 8, 10),
        DestinationDuelCard("dc_8", "Yogyakarta, Indonesië", "يوغياكارتا", "Gastvrije studentenstad met vriendelijk tempo.", 8, 9, 6, 7),
        DestinationDuelCard("dc_9", "Rabat, Marokko", "الرباط", "Rustige hoofdstad met groene lanen en internationale scholen.", 9, 8, 6, 8),
        DestinationDuelCard("dc_10", "Muscat, Oman", "مسقط", "Kalme kuststad bekend om veiligheid en vriendelijke gemeenschap.", 9, 9, 7, 8)
    )

    val kidsWordPuzzles: List<WordPuzzleItem> = listOf(
        WordPuzzleItem("kw_1", "SALAAM", "Begroeting", "Vrede — deel van de islamitische groet", "Begint met S en eindigt op M."),
        WordPuzzleItem("kw_2", "MASJID", "Plek", "Moskee — plek waar je samen bidt", "Begint met M, lijkt op moskee."),
        WordPuzzleItem("kw_3", "SAFAR", "Reis", "Reizen of op pad gaan", "Kort woord van 5 letters."),
        WordPuzzleItem("kw_4", "HIJRAH", "Thema", "Verhuizen naar een nieuw land omwille van geloof en rust", "De naam van onze app!"),
        WordPuzzleItem("kw_5", "BAYT", "Thuis", "Huis of thuis", "Slechts 4 letters, begint met een B."),
        WordPuzzleItem("kw_6", "KAIRO", "Stad", "Grote stad aan de rivier de Nijl in Egypte", "Met een K of C geschreven, hier met K.")
    )

    val adultWordPuzzles: List<WordPuzzleItem> = listOf(
        WordPuzzleItem("aw_1", "APOSTILLE", "Documenten", "Internationale echtheidsstempel op officiële aktes", "9 letters, begint met APO."),
        WordPuzzleItem("aw_2", "VERBLIJF", "Juridisch", "Rechtmatig wonen in het land van vestiging", "8 letters, begint met VER."),
        WordPuzzleItem("aw_3", "TAALCURSUS", "Voorbereiding", "Basis leggen in Arabisch, Maleis of Turks vóór vertrek", "10 letters: taal + cursus."),
        WordPuzzleItem("aw_4", "PASPOORT", "Reis", "Essentieel reisdocument met minimaal 6-12 maanden geldigheid", "8 letters, begint met PAS."),
        WordPuzzleItem("aw_5", "INTEGRATIE", "Aankomst", "Stap voor stap je plek vinden in een nieuwe buurt en school", "10 letters, staat in onze slogan."),
        WordPuzzleItem("aw_6", "HUURCONTRACT", "Wonen", "Schriftelijke afspraak voor woonruimte, borg en opzegtermijn", "12 letters: huur + contract.")
    )

    val preparationQuizThemes: List<String> = listOf(
        "Alle thema's",
        "Documenten & Visum",
        "Wonen & Financiën",
        "Gezin & Zorg",
        "Taal & Integratie"
    )

    val preparationQuizQuestions: List<QuizQuestionItem> = listOf(
        QuizQuestionItem(
            id = "prep_1",
            countryTag = "Documenten & Visum",
            question = "Waarom controleren emigranten vóór vertrek of hun paspoorten nog minstens 6 tot 12 maanden geldig zijn?",
            options = listOf(
                "Veel landen geven geen inreisstempel of verblijfsaanvraag als een paspoort binnen een halfjaar verloopt",
                "Alleen met een nieuw paspoort krijg je korting op vliegtickets",
                "Een paspoort dat minder dan twee jaar oud is vervangt een geboorteakte",
                "De gemeente verplicht een nieuw paspoort bij elke vliegreis"
            ),
            correctIndex = 0,
            hintText = "Denk aan de eisen bij de grens én voor een verblijfsvergunning.",
            explanation = "Zowel luchtvaartmaatschappijen als immigratiediensten eisen vaak minimaal 6 maanden restgeldigheid, en voor verblijfsaanvragen is 12 maanden verstandig."
        ),
        QuizQuestionItem(
            id = "prep_2",
            countryTag = "Documenten & Visum",
            question = "Welke combinatie van stappen maakt een Nederlandse geboorte- of huwelijksakte meestal geldig voor gebruik in het buitenland?",
            options = listOf(
                "Internationaal uittreksel aanvragen, apostille of legalisatie regelen en indien nodig beëdigd laten vertalen",
                "Alleen een scan op je telefoon bewaren",
                "Het document opsturen naar het reisbureau",
                "Een stempel van de basisschool vragen"
            ),
            correctIndex = 0,
            hintText = "Het begint bij de gemeente en gaat via de rechtbank of het ministerie.",
            explanation = "Voor landen onder het Apostilleverdrag (zoals Marokko of Turkije) volstaat een apostille; voor andere landen is legalisatie via ministerie en ambassade nodig."
        ),
        QuizQuestionItem(
            id = "prep_3",
            countryTag = "Documenten & Visum",
            question = "Wat moet je controleren als één ouder alleen reist met minderjarige kinderen richting het nieuwe woonland?",
            options = listOf(
                "Een ondertekend toestemmingsformulier van de andere gezaghebbende ouder met paspoortkopie",
                "Alleen de zwemdiploma's van de kinderen",
                "Niets, kinderen mogen altijd zonder controle met één ouder vliegen",
                "Alleen een briefje van de buren"
            ),
            correctIndex = 0,
            hintText = "Marechaussee en grenscontroles vragen hier streng naar bij minderjarigen.",
            explanation = "Bij reizen met één ouder controleert de grensbewaking streng op toestemming van de andere ouder om misverstanden aan de grens te voorkomen."
        ),
        QuizQuestionItem(
            id = "prep_4",
            countryTag = "Wonen & Financiën",
            question = "Waarom raden ervaren gezinnen aan om na aankomst eerst 2 tot 6 weken tijdelijk te huren?",
            options = listOf(
                "Je ervaart ter plekke het verkeer, de reistijd naar moskee en school en de echte staat van woningen",
                "Omdat jaarcontracten in het buitenland verboden zijn",
                "Tijdelijke huur is altijd gratis voor nieuwkomers",
                "Je mag pas na zes weken een wijk inlopen"
            ),
            correctIndex = 0,
            hintText = "Foto's op internet laten zelden geluidsoverlast, vocht of spitsverkeer zien.",
            explanation = "Ter plaatse kijken voorkomt langdurige contracten in een wijk die qua voorzieningen, geluid of reistijd toch niet bij het gezin past."
        ),
        QuizQuestionItem(
            id = "prep_5",
            countryTag = "Wonen & Financiën",
            question = "Waar moet je financieel rekening mee houden bij het opstellen van een hijrah-startbudget?",
            options = listOf(
                "Naast vliegtickets en eerste huur ook aanloopkosten zoals borg, lokale simkaarten, documentkosten en een noodbuffer",
                "Alleen de kosten van de enkele vliegreis",
                "Alleen het eerste etentje na aankomst",
                "Dat vaste lasten in het buitenland vanaf dag één precies nul zijn"
            ),
            correctIndex = 0,
            hintText = "De eerste drie maanden vragen vaak extra éénmalige uitgaven.",
            explanation = "Borg voor een woning, schoolinschrijving, meubels, visa en een reservebuffer voor 3–6 maanden geven rust in de opstartfase."
        ),
        QuizQuestionItem(
            id = "prep_6",
            countryTag = "Wonen & Financiën",
            question = "Wat controleer je altijd voordat je een huurcontract en borg tekent in een nieuw land?",
            options = listOf(
                "Opzegtermijn, welke kosten inclusief zijn, voorwaarden voor teruggaaf van borg en identiteit van de verhuurder",
                "Alleen of de voordeur een mooie kleur heeft",
                "Of de verhuurder hetzelfde geboortejaar heeft",
                "Mondelinge beloftes zonder iets op papier"
            ),
            correctIndex = 0,
            hintText = "Leg afspraken over water, elektra en onderhoud schriftelijk vast.",
            explanation = "Een helder schriftelijk contract met foto's van de opleverstaat voorkomt discussie over borg of verborgen servicekosten."
        ),
        QuizQuestionItem(
            id = "prep_7",
            countryTag = "Gezin & Zorg",
            question = "Welke voorbereiding helpt het meest voor schoolgaande kinderen vóór vertrek?",
            options = listOf(
                "Schoolrapporten vertalen, inschrijfmomenten checken en kinderen stap voor stap meenemen in taal en gewoontes",
                "Wachten tot na aankomst voordat je uitzoekt welke talen scholen aanbieden",
                "Alle schoolspullen weggooien zonder overdracht",
                "Kinderen niets vertellen tot de dag op het vliegveld"
            ),
            correctIndex = 0,
            hintText = "Internationale en islamitische scholen werken vaak met wachtlijsten.",
            explanation = "Vroeg contact met scholen, vertaalde rapporten en speels voorbereiden op de nieuwe taal maken de overgang voor kinderen veel zachter."
        ),
        QuizQuestionItem(
            id = "prep_8",
            countryTag = "Gezin & Zorg",
            question = "Wat neem je mee om continuïteit van medische zorg na emigratie te borgen?",
            options = listOf(
                "Een vertaald medisch dossier, actueel medicatieoverzicht met stofnamen en een duidelijke zorgdekking vanaf dag één",
                "Alleen Nederlandse recepten zonder stofnamen",
                "Je zegt alle polissen al maanden voor vertrek op",
                "Alleen een oude apotheekbon"
            ),
            correctIndex = 0,
            hintText = "Merknamen van medicijnen verschillen per land; werkzame stofnamen niet.",
            explanation = "Met generieke stofnamen, vaccinatieboekjes en controle op wachttijden van nieuwe zorgpolissen kom je niet voor verrassingen te staan."
        ),
        QuizQuestionItem(
            id = "prep_9",
            countryTag = "Gezin & Zorg",
            question = "Wat helpt bij het kiezen tussen publieke zorg en privéklinieken in populaire bestemmingen zoals Marokko, Turkije of Maleisië?",
            options = listOf(
                "Onderzoeken hoe afspraken, wachttijden, taalondersteuning en vergoedingen voor gezinnen lokaal geregeld zijn",
                "Ervan uitgaan dat elk ziekenhuis automatisch Nederlands spreekt",
                "Alleen kijken naar het uiterlijk van de lobby",
                "Pas bij spoed opzoeken waar een kliniek zit"
            ),
            correctIndex = 0,
            hintText = "Kijk vooral ook naar verloskunde, kinderzorg en spoedposten in jouw wijk.",
            explanation = "In veel islamitische steden zijn moderne privéklinieken goed bereikbaar, mits je vooraf checkt welke verzekering wordt geaccepteerd."
        ),
        QuizQuestionItem(
            id = "prep_10",
            countryTag = "Taal & Integratie",
            question = "Welk taaladvies helpt het snelst bij dagelijkse contacten in Arabischsprekende landen?",
            options = listOf(
                "Combineer basis-Fusha (voor borden en documenten) met dagelijkse begroetingen en zinnen in het lokale dialect",
                "Wacht tien jaar voordat je één woord probeert te spreken",
                "Spreek alleen Nederlands op de lokale markt",
                "Leer uitsluitend juridische vaktermen uit het hoofd"
            ),
            correctIndex = 0,
            hintText = "Buren en winkeliers reageren heel warm op een paar lokale basiszinnen.",
            explanation = "Een vriendelijke begroeting, getallen en beleefdheidsvormen in het lokale dialect bouwen meteen vertrouwen op in de buurt."
        ),
        QuizQuestionItem(
            id = "prep_11",
            countryTag = "Taal & Integratie",
            question = "Wat voorkomt teleurstelling en uitputting tijdens de eerste 90 dagen na aankomst?",
            options = listOf(
                "Realistische verwachtingen, een rustig dagritme en stap voor stap vertrouwd contact leggen met lokale zusters",
                "Alles in week één perfect willen regelen zonder rustmomenten",
                "Elk detail blijven vergelijken met Nederland of België",
                "Helemaal geen hulp of informatie vragen aan anderen"
            ),
            correctIndex = 0,
            hintText = "Wennen aan een nieuw klimaat, ritme en cultuur kost tijd.",
            explanation = "De eerste maanden zijn een overgangsfase; rust in het gezin en kleinschalig contact via moskee of zusterkringen geven houvast."
        ),
        QuizQuestionItem(
            id = "prep_12",
            countryTag = "Taal & Integratie",
            question = "Hoe toets je vooraf of jouw beroep of inkomen aansluit bij het land waar je naartoe wilt emigreren?",
            options = listOf(
                "Onderzoek visumvoorwaarden voor werk op afstand of lokaal ondernemen, diploma-erkenning en belastingregels",
                "Alleen afgaan op een enkel bericht op sociale media",
                "Geen navraag doen over werkvergunningen",
                "Ervan uitgaan dat een toeristenvisum automatisch recht geeft op lokaal werk"
            ),
            correctIndex = 0,
            hintText = "Een toeristenstempel geeft meestal géén recht om lokaal te werken.",
            explanation = "Controleer vooraf of je een werkvergunning, eigen bedrijfsvisum of remote-work-regeling nodig hebt voordat je inkomsten plant."
        )
    )
}

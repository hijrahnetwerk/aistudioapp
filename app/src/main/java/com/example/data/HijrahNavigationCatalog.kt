package com.example.data

enum class ReliabilityStatus(val labelNl: String) {
    VERIFIED("Gecontroleerd kader"),
    ORIENTING("Oriënterend overzicht"),
    CHECK_NEEDED("Aanvullende controle nodig")
}

enum class FacilityCategory(val labelNl: String) {
    ALL("Alle"),
    MOSQUES("Moskeeën"),
    COMMUNITY_CENTERS("Gemeenschapscentra"),
    SHOPS("Halal Winkels"),
    SCHOOLS("Scholen"),
    HEALTHCARE("Zorg"),
    GOVERNMENT("Visum & Loket")
}

data class CountryGuide(
    val id: String,
    val name: String,
    val arabicName: String,
    val regionLabel: String,
    val languages: String,
    val residenceSummary: String,
    val educationSummary: String,
    val healthcareSummary: String,
    val costIndication: String,
    val reliability: ReliabilityStatus,
    val lastChecked: String,
    val mapX: Float,
    val mapY: Float
)

data class CityGuide(
    val id: String,
    val countryId: String,
    val countryName: String,
    val name: String,
    val environmentSummary: String,
    val familySuitability: String,
    val reliability: ReliabilityStatus,
    val lastChecked: String,
    val mapX: Float,
    val mapY: Float
)

data class FacilityLocation(
    val id: String,
    val countryId: String,
    val cityId: String,
    val cityName: String,
    val name: String,
    val category: FacilityCategory,
    val topic: String,
    val description: String,
    val practicalNote: String,
    val reliability: ReliabilityStatus,
    val lastChecked: String,
    val mapX: Float,
    val mapY: Float
)

object HijrahNavigationCatalog {
    val countries: List<CountryGuide> = listOf(
        CountryGuide(
            id = "country_ma",
            name = "Marokko",
            arabicName = "المغرب",
            regionLabel = "Noord-Afrika",
            languages = "Arabisch (Darija / Fus'ha) en Frans",
            residenceSummary = "Jaarlijkse verblijfsvergunning (Carte de Séjour) via werk, onderneming of aantoonbaar inkomen/gezinshereniging.",
            educationSummary = "Breed aanbod van privé-, islamitische en internationale scholen (Frans/Arabisch/Engels) in grotere steden.",
            healthcareSummary = "Privéklinieken in stedelijke regio's bieden goede toegang; ziektekostenverzekering (AMO of privaat) essentieel.",
            costIndication = "Gemiddeld — afhankelijk van stad en internationale schoolkeuze",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.18f,
            mapY = 0.38f
        ),
        CountryGuide(
            id = "country_eg",
            name = "Egypte",
            arabicName = "مصر",
            regionLabel = "Noord-Afrika / Midden-Oosten",
            languages = "Arabisch (Egyptisch / Fus'ha) en Engels in internationale wijken",
            residenceSummary = "Toeristen- of studievisum (bijv. via taalscholen) vaak startpunt; langduriger verblijf via studie, werk of vastgoed.",
            educationSummary = "Sterke focus op Arabische taal en Koranonderwijs; internationale en Britse/Amerikaanse scholen in Nieuw-Caïro en 6th of October.",
            healthcareSummary = "Uitgebreid aanbod aan privéklinieken en vrouwelijke specialisten in Caïro en Alexandrië.",
            costIndication = "Toegankelijk — internationale scholen vormen grootste variabele",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.45f,
            mapY = 0.50f
        ),
        CountryGuide(
            id = "country_tr",
            name = "Turkije",
            arabicName = "تركيا",
            regionLabel = "Eurazië",
            languages = "Turks (basis Turks leren is belangrijk voor dagelijks contact)",
            residenceSummary = "Korte en langere verblijfsvergunningen (Ikamet) kennen wijkquota en wisselende regels; actuele controle vereist.",
            educationSummary = "Publieke scholen, Imam Hatip-scholen en internationale privéscholen in grote steden.",
            healthcareSummary = "Sterke medische infrastructuur via staatsziekenhuizen (SGK) en privéklinieken.",
            costIndication = "Sterk wisselend door inflatie en huurprijzen",
            reliability = ReliabilityStatus.CHECK_NEEDED,
            lastChecked = "November 2026",
            mapX = 0.46f,
            mapY = 0.20f
        ),
        CountryGuide(
            id = "country_jo",
            name = "Jordanië",
            arabicName = "الأردن",
            regionLabel = "Midden-Oosten",
            languages = "Arabisch en Engels in steden",
            residenceSummary = "Verblijfsvergunning via werk, studie of gezin; registratie binnen wettelijke termijn na aankomst verplicht.",
            educationSummary = "Goede reputatie voor Arabisch taalonderwijs en privéscholen in Amman.",
            healthcareSummary = "Hoogwaardige medische zorg in Amman met veel gespecialiseerde klinieken.",
            costIndication = "Gemiddeld tot hoger in Amman",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "Oktober 2026",
            mapX = 0.58f,
            mapY = 0.37f
        ),
        CountryGuide(
            id = "country_my",
            name = "Maleisië",
            arabicName = "ماليزيا",
            regionLabel = "Zuidoost-Azië",
            languages = "Maleis (Bahasa) en breed gesproken Engels",
            residenceSummary = "Werk-, ondernemers-, studentenvisum of lange-termijnprogramma's (MM2H met inkomens- en spaareisen).",
            educationSummary = "Veel erkende internationale islamitische scholen met Engels als voertaal en Arabisch als vak.",
            healthcareSummary = "Moderne private en publieke ziekenhuizen op hoog niveau met Engelssprekende artsen.",
            costIndication = "Gemiddeld tot hoger — afhankelijk van visumtype en schoolgeld",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.82f,
            mapY = 0.68f
        )
    )

    val cities: List<CityGuide> = listOf(
        CityGuide(
            id = "city_tanger",
            countryId = "country_ma",
            countryName = "Marokko",
            name = "Tanger",
            environmentSummary = "Kuststad in het noorden met directe verbindingen naar Europa, levendige gezinswijken en groeiende voorzieningen.",
            familySuitability = "Geschikt voor gezinnen die nabijheid van Europa en een gematigd zeeklimaat zoeken.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.21f,
            mapY = 0.32f
        ),
        CityGuide(
            id = "city_rabat",
            countryId = "country_ma",
            countryName = "Marokko",
            name = "Rabat",
            environmentSummary = "Rustige, groene hoofdstad met overzichtelijke infrastructuur, tramnetwerk en ambassades.",
            familySuitability = "Zeer geschikt voor gezinnen die rust, orde en gestructureerd onderwijs verkiezen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.17f,
            mapY = 0.48f
        ),
        CityGuide(
            id = "city_cairo_new",
            countryId = "country_eg",
            countryName = "Egypte",
            name = "Caïro (Nieuw-Caïro & Maadi)",
            environmentSummary = "Ruime voorsteden met veel mogelijkheden voor Arabische taalstudie, gezinswoningen en actieve zusternetwerken.",
            familySuitability = "Veel gezinnen kiezen wijkgericht voor rustige compounds of groene woonwijken.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.46f,
            mapY = 0.54f
        ),
        CityGuide(
            id = "city_istanbul",
            countryId = "country_tr",
            countryName = "Turkije",
            name = "Istanbul (Üsküdar & Başakşehir)",
            environmentSummary = "Historische stad verdeeld over twee continenten met actieve familiewijken en veel buurmoskeeën.",
            familySuitability = "Geschikt voor gezinnen die stedelijke voorzieningen, halal aanbod en scholen willen combineren.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "November 2026",
            mapX = 0.46f,
            mapY = 0.22f
        ),
        CityGuide(
            id = "city_amman",
            countryId = "country_jo",
            countryName = "Jordanië",
            name = "Amman",
            environmentSummary = "Heuvelachtige hoofdstad met rustige woonwijken, Arabische taalinstituten en sterke familiecultuur.",
            familySuitability = "Prettig voor gezinnen die Arabische onderdompeling zoeken in een ordelijke stad.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "Oktober 2026",
            mapX = 0.59f,
            mapY = 0.39f
        ),
        CityGuide(
            id = "city_kl",
            countryId = "country_my",
            countryName = "Maleisië",
            name = "Kuala Lumpur & Selangor",
            environmentSummary = "Groene metropool met halal-infrastructuur overal, internationale scholen en hoge veiligheid.",
            familySuitability = "Comfortabel voor gezinnen met Engelssprekende achtergrond en werk op afstand.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.82f,
            mapY = 0.70f
        )
    )

    val locations: List<FacilityLocation> = listOf(
        // Marokko
        FacilityLocation(
            id = "loc_tanger_mosque",
            countryId = "country_ma",
            cityId = "city_tanger",
            cityName = "Tanger",
            name = "Centrale Moskeeën & Vrouwen-studiekringen",
            category = FacilityCategory.MOSQUES,
            topic = "Moskeeën & Voorzieningen",
            description = "Grote buurmoskeeën (zoals rond Iberia en Malabata) beschikken over een eigen vrouwenruimte en wekelijkse Koranlessen.",
            practicalNote = "Vraag ter plaatse naar lestijden voor zusters en kinderen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.20f,
            mapY = 0.30f
        ),
        FacilityLocation(
            id = "loc_tanger_shops",
            countryId = "country_ma",
            cityId = "city_tanger",
            cityName = "Tanger",
            name = "Halal Slagerijen & Buurtmarkten (Souk / Marjane)",
            category = FacilityCategory.SHOPS,
            topic = "Halal Winkels & Dagelijkse Boodschappen",
            description = "Volledig halal vlees- en kruideniersaanbod in elke woonwijk, aangevuld met moderne supermarkten voor gezinsboodschappen.",
            practicalNote = "Vraag bij verse markten vroeg in de ochtend voor het ruimste aanbod groenten, olijven en vis.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.27f,
            mapY = 0.36f
        ),
        FacilityLocation(
            id = "loc_tanger_school",
            countryId = "country_ma",
            cityId = "city_tanger",
            cityName = "Tanger",
            name = "Oriëntatiepunt Islamitisch & Tweetalig Onderwijs",
            category = FacilityCategory.SCHOOLS,
            topic = "Onderwijs & Gezin",
            description = "Overzicht van wijkgebonden particuliere basisscholen en middelbare scholen met Arabisch en Frans/Engels curriculum.",
            practicalNote = "Informeer vooraf naar inschrijfdata (vaak februari-mei) en testmomenten.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.14f,
            mapY = 0.38f
        ),
        FacilityLocation(
            id = "loc_rabat_community",
            countryId = "country_ma",
            cityId = "city_rabat",
            cityName = "Rabat",
            name = "Zuster- & Gezinscentrum Agdal / Hay Riad",
            category = FacilityCategory.COMMUNITY_CENTERS,
            topic = "Gemeenschapscentra & Zusterschap",
            description = "Culturele en educatieve ontmoetingsplekken voor moeders en gezinnen met voorleesmiddagen, taalhulp en wijkactiviteiten.",
            practicalNote = "Ideaal om in de eerste maanden andere Nederlandstalige en lokale zusters te leren kennen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.15f,
            mapY = 0.50f
        ),
        FacilityLocation(
            id = "loc_rabat_health",
            countryId = "country_ma",
            cityId = "city_rabat",
            cityName = "Rabat",
            name = "Stedelijke Privéklinieken & Verloskunde",
            category = FacilityCategory.HEALTHCARE,
            topic = "Zorg & Verzekeringen",
            description = "Gespecialiseerde klinieken in wijken zoals Agdal en Hay Riad met ruime keuze aan vrouwelijke artsen.",
            practicalNote = "Controleer vooraf welke polissen direct worden vergoed (tiers payant).",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.24f,
            mapY = 0.54f
        ),

        // Egypte
        FacilityLocation(
            id = "loc_cairo_mosque",
            countryId = "country_eg",
            cityId = "city_cairo_new",
            cityName = "Caïro",
            name = "Al-Azhar Omgeving & Wijkmasajid (Maadi / Nasr City)",
            category = FacilityCategory.MOSQUES,
            topic = "Moskeeën & Studie",
            description = "Uitgebreide gebedsruimtes voor vrouwen, dagelijkse lezingen en laagdrempelige Korankringen voor gezinnen.",
            practicalNote = "Let op tijden voor vrouwentoespraken na het Asr- of Maghrib-gebed.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.41f,
            mapY = 0.48f
        ),
        FacilityLocation(
            id = "loc_cairo_community",
            countryId = "country_eg",
            cityId = "city_cairo_new",
            cityName = "Caïro",
            name = "Gezins- & Zustercentrum Maadi Degla",
            category = FacilityCategory.COMMUNITY_CENTERS,
            topic = "Gemeenschapscentra & Zusterschap",
            description = "Actieve gemeenschapscentra waar expat- en lokale zusters samenkomen voor opvoeding, Arabische conversatie en wegwijsworkshops.",
            practicalNote = "Veel bijeenkomsten hebben een aparte kinderopvang of speelruimte tijdens lezingen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.49f,
            mapY = 0.56f
        ),
        FacilityLocation(
            id = "loc_cairo_study",
            countryId = "country_eg",
            cityId = "city_cairo_new",
            cityName = "Caïro",
            name = "Arabische Taalinstituten (Nasr City & Maadi)",
            category = FacilityCategory.SCHOOLS,
            topic = "Taal & Integratie",
            description = "Erkende centra voor Klassiek Arabisch (Fus'ha) en Koranstudie met aparte klassen voor zusters en kinderen.",
            practicalNote = "Sommige erkende instituten kunnen ondersteunen bij aanvraag van een studievisum.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.44f,
            mapY = 0.62f
        ),
        FacilityLocation(
            id = "loc_cairo_shops",
            countryId = "country_eg",
            cityId = "city_cairo_new",
            cityName = "Caïro",
            name = "Halal Versmarkten & Gezinssupermarkten",
            category = FacilityCategory.SHOPS,
            topic = "Halal Winkels & Huisvesting",
            description = "Supermarkten, halal slagers en apotheken zijn in woonwijken vaak tot laat in de avond open en bezorgen aan huis.",
            practicalNote = "Let bij woningkeuze op loopafstand tot basisvoorzieningen en betrouwbare water/elektra-aansluiting.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "November 2026",
            mapX = 0.52f,
            mapY = 0.49f
        ),

        // Turkije
        FacilityLocation(
            id = "loc_istanbul_mosque",
            countryId = "country_tr",
            cityId = "city_istanbul",
            cityName = "Istanbul",
            name = "Wijk- en Historische Moskeeën (Üsküdar & Fatih)",
            category = FacilityCategory.MOSQUES,
            topic = "Moskeeën & Voorzieningen",
            description = "Ruime, verzorgde vrouwengedeeltes, aangrenzende gezinsbibliotheken en regelmatige zusterbijeenkomsten.",
            practicalNote = "In de winter zijn de vrouwengedeeltes van grote moskeeën aangenaam verwarmd.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.42f,
            mapY = 0.20f
        ),
        FacilityLocation(
            id = "loc_istanbul_community",
            countryId = "country_tr",
            cityId = "city_istanbul",
            cityName = "Istanbul",
            name = "Vrouwen- & Familiecentrum Başakşehir",
            category = FacilityCategory.COMMUNITY_CENTERS,
            topic = "Gemeenschapscentra & Integratie",
            description = "Gemeentelijke en particuliere familiecentra met Turkse taallessen voor vrouwen, handwerk en kinderactiviteiten.",
            practicalNote = "İSMEKs (gemeentelijke centra) bieden vaak gratis basiscursussen Turks voor nieuwkomers.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "November 2026",
            mapX = 0.50f,
            mapY = 0.24f
        ),
        FacilityLocation(
            id = "loc_istanbul_shops",
            countryId = "country_tr",
            cityId = "city_istanbul",
            cityName = "Istanbul",
            name = "Halal Delicatessen & Wijkbazaars",
            category = FacilityCategory.SHOPS,
            topic = "Halal Winkels & Dagelijkse Boodschappen",
            description = "Wekelijkse openluchtmarkten (Semt Pazarı) en gecertificeerde halal slagerijen en bakkerijen in elk district.",
            practicalNote = "Controleer bij verpakte producten altijd het Turkse Helal-keurmerk (GİMDES).",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.45f,
            mapY = 0.29f
        ),

        // Jordanië
        FacilityLocation(
            id = "loc_amman_mosque",
            countryId = "country_jo",
            cityId = "city_amman",
            cityName = "Amman",
            name = "Koning Abdullah I Moskee & Wijkmasajid",
            category = FacilityCategory.MOSQUES,
            topic = "Moskeeën & Onderwijs",
            description = "Goed bereikbare moskeeën in Abdoun en Khalda met aparte vrouwenetages en weekendlessen voor kinderen.",
            practicalNote = "Vraag naar de Koran-memoratieprogramma's voor de zomervakantie.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "Oktober 2026",
            mapX = 0.58f,
            mapY = 0.35f
        ),
        FacilityLocation(
            id = "loc_amman_community",
            countryId = "country_jo",
            cityId = "city_amman",
            cityName = "Amman",
            name = "Cultuur- & Taalontmoetingscentrum Sweifieh",
            category = FacilityCategory.COMMUNITY_CENTERS,
            topic = "Gemeenschapscentra & Taal",
            description = "Kennisuitwisseling tussen Arabische studenten, moeders en gezinnen met thema-avonden en lokale wegwijsinformatie.",
            practicalNote = "Prettige plek om taalpartners te vinden en ervaringen over scholen te delen.",
            reliability = ReliabilityStatus.ORIENTING,
            lastChecked = "Oktober 2026",
            mapX = 0.63f,
            mapY = 0.42f
        ),

        // Maleisië
        FacilityLocation(
            id = "loc_kl_mosque",
            countryId = "country_my",
            cityId = "city_kl",
            cityName = "Kuala Lumpur",
            name = "Wilayah Persekutuan Moskee & Suraus",
            category = FacilityCategory.MOSQUES,
            topic = "Moskeeën & Gezin",
            description = "Moderne hoofdmoskee en schone gebedsruimtes (surau) in bijna elk winkelcentrum en wooncomplex.",
            practicalNote = "Vrijwel elke publieke ruimte biedt schone woedoe-faciliteiten en gebedskleding voor vrouwen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.77f,
            mapY = 0.64f
        ),
        FacilityLocation(
            id = "loc_kl_community",
            countryId = "country_my",
            cityId = "city_kl",
            cityName = "Kuala Lumpur",
            name = "Internationale Zusterkring & Gezinshub Ampang",
            category = FacilityCategory.COMMUNITY_CENTERS,
            topic = "Gemeenschapscentra & Zusterschap",
            description = "Engelstalige zusterkringen, speelgroepen en aankomstbegeleiding voor nieuw gevestigde gezinnen uit Europa.",
            practicalNote = "Ideale startplek voor vragen over huishouden, scholen en lokale gebruiken.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.86f,
            mapY = 0.68f
        ),
        FacilityLocation(
            id = "loc_kl_shops",
            countryId = "country_my",
            cityId = "city_kl",
            cityName = "Kuala Lumpur",
            name = "JAKIM Halal Supermarkten & Pasar Malam",
            category = FacilityCategory.SHOPS,
            topic = "Halal Winkels & Voeding",
            description = "Strenge JAKIM halal-certificering in supermarkten (zoals Jaya Grocer & Aeon) en verse avondmarkten.",
            practicalNote = "Kijk uit naar het officiële JAKIM-logo op geïmporteerde producten.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.75f,
            mapY = 0.74f
        ),
        FacilityLocation(
            id = "loc_kl_school",
            countryId = "country_my",
            cityId = "city_kl",
            cityName = "Kuala Lumpur",
            name = "Internationale Islamitische Scholen (Gombak / Ampang)",
            category = FacilityCategory.SCHOOLS,
            topic = "Onderwijs & Gezin",
            description = "Engelstalig internationaal curriculum gecombineerd met islamitische opvoeding en Arabisch.",
            practicalNote = "Houd rekening met jaarlijks collegegeld en verplichte registratie voor studentenpassen.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.84f,
            mapY = 0.77f
        ),
        FacilityLocation(
            id = "loc_kl_visa",
            countryId = "country_my",
            cityId = "city_kl",
            cityName = "Kuala Lumpur",
            name = "Immigratie & Verblijfsloketten (Putrajaya / MDEC)",
            category = FacilityCategory.GOVERNMENT,
            topic = "Verblijfsvergunning",
            description = "Centrale afhandeling voor werk-, digital-nomad- (DE Rantau) en lange-termijnvergunningen.",
            practicalNote = "Veel vooraanvragen verlopen digitaal voordat je persoonlijk langsgaat voor biometrie.",
            reliability = ReliabilityStatus.VERIFIED,
            lastChecked = "November 2026",
            mapX = 0.89f,
            mapY = 0.62f
        )
    )
}

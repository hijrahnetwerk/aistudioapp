Hijrah Netwerk — gedeelde appbasis

Deze map is de nieuwe Expo-basis voor Android en iOS. Het bestaande Kotlin/Jetpack Compose-project blijft ongewijzigd in de hoofdmap staan als referentie en back-up.

Starten (Node.js 22.13 of hoger):

    cd app-expo
    npm install
    npx expo start

Techniek: Expo SDK 58, React Native 0.88 en TypeScript. Eén gedeelde UI-codebasis voor Android en iOS. Backendkeuze staat nog niet vast: eerst vergelijken we de bestaande Firebase-configuratie met de HN-Supabase-omgeving. Nieuwe schermen worden eerst visueel opgebouwd; daarna koppelen we navigatie, authenticatie, database en meldingen.

De eerste App.tsx is een visuele dashboardstart, geen afgeronde of backend-gekoppelde app.

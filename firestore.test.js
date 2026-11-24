const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const SISTER_A = "sister_amina";
const SISTER_B = "sister_khadija";
const ADMIN_UID = "admin_hijrah";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Budget, documents, and notes are strictly isolated to owner", async () => {
  const sisterADb = testEnv.authenticatedContext(SISTER_A).firestore();
  const sisterBDb = testEnv.authenticatedContext(SISTER_B).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("budget_items").doc("b_1").set({
      userId: SISTER_A,
      title: "Vliegtickets gezin",
      category: "Vertrek",
      amountEur: 1850,
      recurringMonthly: false,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("documents").doc("d_1").set({
      userId: SISTER_A,
      title: "Huwelijksakte apostille",
      category: "Legalisatie",
      docStatus: "IN_AANVRAAG",
      note: "Afspraak rechtbank",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("notes").doc("n_1").set({
      userId: SISTER_A,
      title: "Scholen Tanger",
      body: "Informeer naar inschrijftermijn voor september.",
      topic: "Onderwijs & Gezin",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertFails(sisterBDb.collection("users").doc(SISTER_A).collection("budget_items").get());
  await assertFails(sisterBDb.collection("users").doc(SISTER_A).collection("documents").get());
  await assertFails(sisterBDb.collection("users").doc(SISTER_A).collection("notes").get());
});

test("Experience moderation prevents self-approval and admin can approve", async () => {
  const sisterADb = testEnv.authenticatedContext(SISTER_A).firestore();
  const sisterBDb = testEnv.authenticatedContext(SISTER_B).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    sisterADb.collection("experiences").doc("exp_1").set({
      userId: SISTER_A,
      authorAlias: "Amina",
      countryName: "Marokko",
      cityName: "Tanger",
      topic: "Onderwijs",
      title: "Eerste indrukken van scholen in Tanger",
      story: "Wij hebben verschillende basisscholen bezocht tijdens onze oriëntatiereis.",
      prosAndCons: "Voordeel: veel keuze. Nadeel: wachtlijsten.",
      lessonLearned: "Achteraf zou ik eerder een proefweek plannen.",
      practicalTip: "Plan kennismakingsgesprekken ruim voor de zomervakantie.",
      status: "PENDING",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertFails(sisterBDb.collection("experiences").doc("exp_1").get());
  await assertFails(
    sisterADb.collection("experiences").doc("exp_1").update({
      status: "APPROVED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "hijrahnetwerk@gmail.com",
    email_verified: true,
  }).firestore();
  await assertSucceeds(
    adminDb.collection("experiences").doc("exp_1").update({
      status: "APPROVED",
      verifiedAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("User registration profile supports originCountry and currentCity and isolates across users", async () => {
  const sisterADb = testEnv.authenticatedContext(SISTER_A).firestore();
  const sisterBDb = testEnv.authenticatedContext(SISTER_B).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).set({
      userId: SISTER_A,
      displayAlias: "Amina",
      originCountry: "",
      currentCity: "",
      hijrahPhase: "ORIENTATIE",
      focusCountry: "Nog aan het verkennen",
      focusCity: "",
      onboardingCompleted: false,
      lastVisitedTitle: "Start",
      lastVisitedRoute: "tools",
      completedStepsCount: 0,
      totalStepsCount: 4,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).update({
      displayAlias: "Amina",
      originCountry: "Nederland",
      currentCity: "Amsterdam",
      hijrahPhase: "ONDERZOEK",
      focusCountry: "Marokko",
      focusCity: "Tanger",
      onboardingCompleted: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(sisterBDb.collection("users").doc(SISTER_A).get());
  await assertFails(
    sisterBDb.collection("users").doc(SISTER_A).update({
      originCountry: "België",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Game scores are isolated to owner and enforce valid gameIds", async () => {
  const sisterADb = testEnv.authenticatedContext(SISTER_A).firestore();
  const sisterBDb = testEnv.authenticatedContext(SISTER_B).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("game_scores").doc("BOARD_HIJRAH").set({
      userId: SISTER_A,
      gameId: "BOARD_HIJRAH",
      bestScore: 150,
      gamesPlayed: 1,
      gamesWon: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("game_scores").doc("BOARD_HIJRAH").update({
      bestScore: 220,
      gamesPlayed: 2,
      gamesWon: 2,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(
    sisterADb.collection("users").doc(SISTER_A).collection("game_scores").doc("INVALID_GAME").set({
      userId: SISTER_A,
      gameId: "INVALID_GAME",
      bestScore: 100,
      gamesPlayed: 1,
      gamesWon: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertFails(sisterBDb.collection("users").doc(SISTER_A).collection("game_scores").get());
});

test("Notification settings & FCM tokens are strictly isolated to owner and validated", async () => {
  const sisterADb = testEnv.authenticatedContext(SISTER_A).firestore();
  const sisterBDb = testEnv.authenticatedContext(SISTER_B).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("notification_settings").doc("fcm").set({
      userId: SISTER_A,
      fcmToken: "fcm_sample_token_abc123",
      forumRepliesEnabled: true,
      milestoneRemindersEnabled: true,
      reminderCadence: "WEEKLY",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    sisterADb.collection("users").doc(SISTER_A).collection("notification_settings").doc("fcm").update({
      forumRepliesEnabled: false,
      reminderCadence: "DAILY",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(
    sisterADb.collection("users").doc(SISTER_A).collection("notification_settings").doc("fcm").update({
      reminderCadence: "HOURLY",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(
    sisterADb.collection("users").doc(SISTER_A).collection("notification_settings").doc("fcm").update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertFails(sisterBDb.collection("users").doc(SISTER_A).collection("notification_settings").get());
});


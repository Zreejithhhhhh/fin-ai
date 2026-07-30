# MoneyMoment AI — fin-ai

**Regret-aware spending decisions, powered by AI.**

MoneyMoment AI is an Android application that helps you make smarter spending decisions by predicting purchase regret before it happens. Built with **Jetpack Compose**, **Material 3**, **Room** database, and **MVVM** architecture.

> **Core innovation:** The "Decision Point" — before you buy, the AI analyzes your personal regret history and delivers a verdict (green/yellow/red) with a reason and goal impact estimate.

---

## Features

### 1. Decision Point
The flagship feature. Describe a planned purchase — AI evaluates it against your regret history and returns a verdict.

- **Traffic-light verdict:** green (safe), yellow (caution), red (skip)
- **Regret formula:** `category_regret_rate x 0.4 + recency_weight x 0.3 + amount_ratio x 0.3`
- **Goal impact:** Shows how the purchase affects your active savings goals
- **Reason engine:** Natural language explanation for every verdict

### 2. Regret Journal
Log purchases and rate them — "Worth It" or "Regretted".

- Category-based purchase tracking
- One-tap rating after logging
- Regret rate statistics per category

### 3. Goal Tracker
Set up to 3 active savings goals with visual progress bars.

- Target amount + saved amount tracking
- Automated progress percentage
- Goal impact shown during Decision Point evaluation

### 4. Dashboard
Monthly spending overview at a glance.

- Income vs. spent comparison
- Regret rate percentage
- Savings rate calculation
- Category breakdown with spending totals
- 4-week trend visualization

### 5. Weekly Digest
Automated weekly summary of your spending health.

- Overall regret rate for the week
- Highest regret category identification
- Actionable tip based on your behavior
- Total spent vs. regretted spent breakdown

---

## Screenshots

*(Add screenshots here once built on your machine)*

| Screen | Description |
|--------|-------------|
| Dashboard | Monthly stats, income, category breakdown |
| Decision Point | AI verdict input and result |
| Regret Journal | Purchase log with ratings |
| Goal Tracker | Savings goals with progress |
| Weekly Digest | Auto-generated weekly insights |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | **Kotlin** 1.9.22 |
| UI | **Jetpack Compose** + **Material 3** (Material Icons Extended) |
| Architecture | **MVVM** (Model-View-ViewModel) |
| Database | **Room** 2.6.1 with KSP |
| Navigation | **Navigation Compose** 2.7.7 |
| Build | **Gradle** 8.5 + **AGP** 8.2.2 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

---

## Project Structure

```
app/src/main/java/com/moneymoment/ai/
├── domain/                    # Business logic layer
│   ├── model/                 # Domain models (Purchase, Goal, Verdict, Category)
│   └── engine/                # AIEngine — regret prediction engine
├── data/                      # Data layer
│   ├── local/
│   │   ├── entity/            # Room entities
│   │   ├── dao/               # Room Data Access Objects
│   │   └── AppDatabase.kt     # Room database singleton
│   └── repository/            # Repository pattern (Purchase, Goal)
├── ui/
│   ├── theme/                 # Material 3 theme (Color, Typography, Theme)
│   ├── components/            # Reusable composables (VerdictCard, StatCard, etc.)
│   └── screens/               # 5 feature screens
├── viewmodel/                 # 5 ViewModels with StateFlow
├── navigation/                # Navigation graph (5 routes)
├── MainActivity.kt            # Single Activity with Scaffold
└── MoneyMomentApp.kt          # Application class (DB initialization)
```

---

## Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Clone & Build

```bash
git clone https://github.com/Zreejithhhhhh/fin-ai.git
cd fin-ai
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Build Release
```bash
./gradlew assembleRelease
```

---

## AI Engine: How It Works

The AI Engine uses a **rule-based formula** (no ML model, no internet required):

```
regret_score = categoryRegretRate x 0.4 + recencyWeight x 0.3 + amountRatio x 0.3
```

| Component | Description |
|-----------|-------------|
| **categoryRegretRate** | How often you regret purchases in this category (0-1) |
| **recencyWeight** | How recently you regretted a similar purchase (0-1) |
| **amountRatio** | Transaction amount relative to your average in this category (0-1) |

**Verdict thresholds:**
- < 35%: green — Safe to spend
- 35-69%: yellow — Think twice
- >= 70%: red — Skip it

Default regret rates are pre-seeded for 14 categories and evolve as you log purchases.

---

## Architecture Decisions

- **No dependency injection framework** — keeps the project accessible for learning and modification. Manual dependency injection via `MoneyMomentApp` singleton.
- **Room over DataStore** — relational queries needed for purchase analytics and category breakdowns.
- **StateFlow over LiveData** — better Compose integration and coroutine support.
- **Material 3 (Material You)** — modern design language with dynamic color support on Android 12+.

---

## License

```
MIT License

Copyright (c) 2026 Zreejithhhhhh

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

*Built by MAX-BUDDY Autonomous Engineering Loop Agent*

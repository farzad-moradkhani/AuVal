# AuVal — Agent Instructions

Android app (single `:app` module) for tracking 18k gold prices and the value of gold holdings.
UI is 100% Jetpack Compose + Material 3; there are no XML layouts. Package: `io.farzadmoradkani.auval`.

## Commands
- Compile Kotlin (fastest check): `./gradlew :app:compileDebugKotlin`
- Build debug APK: `./gradlew :app:assembleDebug`
- Unit tests: `./gradlew :app:testDebugUnitTest`
- Single test: `./gradlew :app:testDebugUnitTest --tests "io.farzadmoradkani.auval.ExampleUnitTest"`
- Instrumented tests (need a device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew :app:lintDebug`

`local.properties` contains the machine-specific SDK path; leave it alone.

## Architecture
- `MainActivity.kt` is the entrypoint. Navigation is a `when` over the `Screen` sealed class (`ui/Screen.kt`) driven by local `mutableStateOf` — there is no Navigation Compose and no DI framework.
- Features live in `ui/<feature>/` as `<Feature>Screen.kt` + `<Feature>ViewModel.kt`. ViewModels are constructed directly in the composable (`val vm = XViewModel()`), and state is collected with `collectAsStateWithLifecycle()`.
- Expose state as `MutableStateFlow` + a read-only `StateFlow` (follow `HomeViewModel`).
- Shared composables are in `ui/components`: `BasicCard`, `LargeTitle`, `SmallTitle`. `BasicCard` aligns its column content to the start (right in RTL) and already applies padding/width.
- Use `util/extentions/Number.kt` helpers (`separate3Digits()`, `toCompactString()`) instead of ad-hoc number formatting.
- Localization is applied manually in `MainActivity` by providing a locale-specific `Context`, `Resources`, `Configuration`, and `LocalLayoutDirection` through `CompositionLocalProvider`. There is no AppCompat per-app-locale dependency. Add every user-visible string to `values/strings.xml` and all of `values-fa`, `values-ar`, `values-fr`, `values-de`, and `values-zh`. Use `Start`/`End` layout semantics instead of assuming left/right; `Start` is the right side in RTL.
- `ui/settings/AppSettings.kt` owns `AppLanguage`, `GoldPriceSource`, and the SharedPreferences helpers backed by `auval_settings`.
- `AuValApplication` is the app container. It creates `Gold18PriceRepository` and the Room-backed `GoldEntryRepository`; `MainActivity` passes them down to screens.
- Gold prices are scraped with Jsoup in `Gold18PriceRepository` from Iranjib, Talasea, and TGJU. The source sites are parsed as "mesghal" prices and converted to Toman/gram with `mesghal / 4.3`. The repository exposes a shared `StateFlow<GoldPriceState>` so every screen updates when the source or price changes.
- Persistence uses Room: `GoldEntryEntity` + `GoldEntryDao` + `AuValDatabase`, wrapped by `GoldEntryRepository`. `HomeViewModel` and `PortfolioViewModel` receive repositories through their constructors (they are created with `remember` in the screens).
- KSP with AGP 9's built-in Kotlin requires `android.disallowKotlinSourceSets=false` in `gradle.properties`. Keep Room/KSP versions aligned with `libs.versions.toml`.
- `preview/` contains saved source HTML used only as a reference for scraper selectors; it is not an app asset.

## Conventions
- All user-visible text belongs in `app/src/main/res/values/strings.xml`, named `screen_<feature>_...` or `bottomNav_...`.
- Use `MaterialTheme.colorScheme.*`; the gold/charcoal/bone-white palette is defined in `ui/theme`. Don't hardcode colors.
- Money is in Toman, gold is 18k, and weights are grams.
- The Transactions screen and nav item are intentionally commented out for the first release; keep them disabled unless asked.
- Dependencies and versions are managed in `gradle/libs.versions.toml`; Compose versions come from the BOM. Do not hardcode versions or add libraries (navigation, DI, networking) without need.

## Workflow
1. Read only the files relevant to the task.
2. Make the smallest complete change and match the surrounding style.
3. Run `:app:compileDebugKotlin` after edits; run tests when behavior changes. Never claim a check passed unless it actually ran.
4. If a build fails twice on the same approach, stop and explain the blocker.

## Safety
- Never overwrite user work or run destructive commands without confirmation.
- Do not expose secrets, API keys, or the contents of `local.properties`.

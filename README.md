# Venture Client (Fabric, Minecraft 26.3)

Lekki, legalny mod client-side pod PvP. Menu: **Prawy Shift**.

## Funkcje
- **Optymalizacja**: redukcja partikli (dym, ogień, wybuchy), wyłączenie vanilla partikli ataków, brak screen shake.
- **Dźwięki**: mod *wycisza vanillowe dźwięki uderzeń* i gra własne (6 wbudowanych + opcja "Własny"). Osobno trafienie i crit, głośność, wysokość, przyciski testowe.
- **Particle trafień**: Crit / Enchant / Serca / End Rod / własny kolor (RGB). Max 20 sztuk, max raz na tick.
- **HP przeciwnika**, **Keystrokes** (prawy górny róg), ukrywanie crosshaira.
- **Freecam** (domyślnie `V`): kamera odczepia się od ciała, WASD/Spacja/Shift ją ruszają. Limit dystansu, auto-wyjście po otrzymaniu obrażeń. UWAGA: wiele serwerów zabrania freecama - sprawdź regulamin.
- **Quick-swap** (domyślnie `C`): ręczna zmiana slotu klawiszem.

## Własne dźwięki
Użyj folderu `custom-sound-pack-template` (instrukcja w środku): dodajesz `custom_hit.ogg` / `custom_crit.ogg`, pakujesz ZIP, wrzucasz do `resourcepacks`, a w menu wybierasz "Własny (RP)".

## Lekkość
Brak pętli po stringach w gorących ścieżkach (partikle: lookup po identyfikatorze typu), HUD cache'uje teksty, config zapisuje się tylko przy zmianie, wszystkie mixiny kończą się na jednym ifie gdy opcja wyłączona.

## Budowanie
1. JDK 25 + Gradle 9.6.0. Raz: `gradle wrapper --gradle-version 9.6.0`.
2. Sprawdź wersje w `gradle.properties` na https://fabricmc.net/develop (yarn_mappings!).
3. `./gradlew build` -> `build/libs/venture-client-1.0.0.jar` -> do folderu `mods` (+ Fabric API).

## Uwaga
Kod nie był kompilowany ani testowany (brak dostępu do Mavena w środowisku, w którym powstał). API Minecrafta 26.3 mogło się zmienić - jeśli build lub start gry zgłosi błąd, wyślij log. Mixiny mają `defaultRequire: 0`, więc nieudane wstrzyknięcie loguje ostrzeżenie zamiast crashować grę (sprawdź `latest.log`: "Mixin apply failed").

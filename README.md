# Turingov stroj pre násobenie binárnych čísel
*(Turing Machine for binary product calculation)*

## Prehľad
Tento projekt implementuje simulátor deterministického k-páskového Turingovho stroja DTM v jazyku Java (v21). 
Riešenie je navrhnuté ako univerzálny simulátor, ktorý umožňuje definovať:
- ľubovoľný počet pások (teda k-páskový stroj),
- konečnú množinu stavov,
- množinu koncových (resp. akceptujúcich) stavov,
- počiatočný stav,
- konečnú abecedu (vrátane prázdneho symbolu/blank `#`),
- prechodové funkcie v tvare: `(aktualny_stav, cítane_symboly) -> (nasledujuci_stav, zapisovane_symboly, posun_hlavy)`.

## Násobenie binárnych čísel
Stroj používa 4 pásky:
- **IN**: vstup binárnych čísel (napr. `#101#10#` pre 5*2)
- **ACC**: akumulátor pre násobenie (medzivýsledok v unárnej reprezentácií)
- **FAC**: aktuálny faktor (unárne)
- **OUT**: výstupná páska, je na nej výsledok v binárnej sústave

### Algoritmus:
1. Inicializácia - Kontrola vstupu
2. binárne na unárne - načítanie binárneho čísla z pásky IN a jeho prevod do unárnej reprezentácie na pásku FAC
3. Unárne násobenie - obsah pásky ACC sa pripočíta k výsledku toľkokrát, koľko je symbolov na páske FAC (teda opakované sčítanie)
4. Cyklus sa opakuje pre ďalšie číslo na vstupe
5. Unárne na binárne - výsledná unárna hodnota z ACC sa prevedie späť do binárnej sústavy pomocou delenia dvoma a zapíše sa na pásku OUT

## Štruktúra projektu a požiadavky
-Main.java hlavná a vstupná trieda, obsahuje definíciu prechodových funkcií (pravidlá) a spúšta simuláciu
-MultiTapeTM.java: Jadro simulátora (vykonávanie krokov, posun hláv a správa stavov)
-TMBuilder.java: pattern Builder, pomocná trieda pre definovanie pravidiel turingovho stroja
-Tape.java: predstavuje "nekonečnú" pásku, implementovanú cez hashmap. Operácie načítania, zápisu a posunu hlavy

Projekt potrebuje Java JDK 21 (vyvíjané v Eclipse Temurin 21 )


## Spustenie programu
1. Kompilácia
```
javac Tape.java TMBuilder.java MultiTapeTM.java Main.java
```
2. Spustenie simulácie so vstupným stringom
```
java Main "#101#10#"
```
Tento príkaz vypočíta 5 (101) × 2 (10) = 10 (1010).

**Vstupný formát**: čísla v binárnej sústave oddelené oddelovačom resp. separátorom `#`, vložené medzi oddelovače (napr. `#101#10#`)
Pre použitie v IDE stačí štačí spustiť projekt (s kompiláciou) a vložiť vstupný string ako parameter programu.

## Formát výstupu
Výstup obsahuje stavy pások po jednotlivých krokoch a konečný zakódovaný stroj.

1. Vstup, pôvodný reťazec
2. Krokovanie simulácie - pre každý krok sa vypíše aktuálny stav a okolie hlavy pre všetky pásky
3. Výsledok - stav výstupnej pásky po zastavení stroja
4. Zakódovaný stroj - textová reprezentácia všetkých prechodových funkcií





**Príklad výstupu:**
```
----- machine encoding ---
Num of Tapes: 4
Start State: q_INIT
Accept States: [ERROR_HALT, HALT]
Alfabet: [#, 0, 1, x]

Transitions (Rules):
(q_INIT, [#, #, #, #]) -> (q_CHECK_INPUT, [#, x, #, #], [R, S, S, S])
(q_INIT, [0, #, #, #]) -> (q_CHECK_INPUT, [#, x, #, #], [S, S, S, S])
...
```


## Zdroje a nástroje
- Skripta: https://sites.google.com/view/7tilo-25/materiály
    - VYSL1.pdf: Vyčíslitelnost  a složitost, 
    - VYSL2.pdf: Vyčíslitelnost a  složitost 2,
    - LZUI1.pdf: Logika pro informatiky
    - LZUI2.pdf: Logika pro umělou inteligenci
- https://turingmachinesimulator.com/
- JetBrains AI Assistant
- Google Gemini LLM



# Catan Project

## Descrizione del progetto

Questo progetto è un'implementazione Java del gioco da tavolo Catan, sviluppata per un corso di Software Engineering.

Il codice segue una progettazione object-oriented basata sul UML class diagram ed è organizzato con un'architettura simile a MVC usando `controller`, `model` e `view`.

## Architettura

- `controller`: gestisce le azioni dell'utente e coordina l'interazione tra `view` e `model`.
- `model`: contiene la logica di dominio e le principali entità del gioco.
- `view`: contiene le classi dell'interfaccia grafica/utente.

## Struttura dei package

```text
it.univaq.caffeine.catan
├── Main
├── controller
│   └── GameController
├── model
│   ├── Game
│   ├── GameSetup
│   ├── Bank
│   ├── BotStrategy
│   ├── board
│   │   ├── GameMap
│   │   ├── HexagonalTile
│   │   ├── Intersection
│   │   ├── Edge
│   │   ├── Token
│   │   ├── Port
│   │   └── Robber
│   ├── cards
│   │   ├── Deck
│   │   ├── DevelopmentCard
│   │   └── ResourceCard
│   ├── player
│   │   ├── Player
│   │   ├── Building
│   │   ├── Colony
│   │   ├── City
│   │   └── Street
│   ├── rules
│   │   ├── GameRules
│   │   └── CostRegistry
│   └── enums
│       ├── ResourceType
│       └── BuildingType
└── view
    ├── CatanBoardPanel
    └── GameWindow
```

## Componenti principali

- `Main`: punto di ingresso dell'applicazione. Avvia il flusso di setup e apre la finestra di gioco.
- `GameController`: coordina le azioni di setup, la creazione dei `Player`, l'inizializzazione di `Game` e le richieste di placement.
- `Game`: classe centrale del `model` che memorizza players, map, deck, bank, rules, robber e game state.
- `GameSetup`: memorizza le scelte di setup come game mode, number of players, player names e bot difficulty.
- `Bank`: gestisce lo stock di resource cards e trasferisce risorse verso o dai players.
- `GameMap`: genera e memorizza il grafo del board, inclusi tiles, intersections, edges, tokens e ports.
- `HexagonalTile`: rappresenta un hex del board e il suo terrain/resource type.
- `Intersection`: rappresenta un nodo del board dove può essere posizionato un `Building`.
- `Edge`: rappresenta un collegamento del board dove può essere posizionata una `Street`.
- `Token`: memorizza il dice number assegnato a un tile produttivo.
- `Port`: rappresenta un maritime exchange port.
- `Robber`: rappresenta il robber token e il suo tile corrente.
- `Deck`: gestisce il development card deck.
- `DevelopmentCard`: rappresenta una development card e il suo `CardType`.
- `ResourceCard`: rappresenta una resource card di un dato `ResourceType`.
- `Player`: memorizza i dati del player, cards, resources, buildings posseduti e bot strategy se applicabile.
- `Building`: classe base astratta per le strutture del board possedute dai players.
- `Colony`: settlement-type building posizionato su una `Intersection`.
- `City`: city-type building model, attualmente presente per il supporto futuro alle regole.
- `Street`: road-type building posizionato su un `Edge`.
- `GameRules`: valida le placement rules attualmente usate dal game flow.
- `CostRegistry`: memorizza i resource costs per gli elementi costruibili.
- `ResourceType`: enum per resource e terrain types.
- `BuildingType`: enum per buildable element types.
- `CatanBoardPanel`: pannello Swing che disegna il board e gestisce l'interazione mouse sul board.
- `GameWindow`: finestra Swing principale con board, status, resources, deck e bank panels.
- `BotStrategy`: fornisce decisioni di base del bot per initial colony e road placement.

## Funzionalità principali

Il progetto attualmente include supporto per:

- setup del gioco;
- creazione dei player;
- selezione della game mode;
- rappresentazione della map con tiles, intersections ed edges;
- colonies, cities e streets come classi del `model`;
- classi per resources e development cards;
- gestione delle risorse della bank;
- validazione delle rules per initial colony e road placements;
- una struttura GUI di base con Swing;
- supporto alla bot strategy per initial placement.

Alcune funzionalità complete del gameplay di Catan sono ancora incomplete o pianificate.

## Come eseguire il progetto

I metadata del progetto IntelliJ IDEA sono configurati per Java 21 (`JDK_21`). Usa Java 21 per mantenere la coerenza con la configurazione del progetto.

Questo progetto non include attualmente configurazioni Maven o Gradle. Può essere eseguito direttamente da IntelliJ IDEA.

1. Apri IntelliJ IDEA.
2. Seleziona **Open** e scegli la cartella del progetto clonato.
3. Assicurati che `CatanProject/src` sia marcata come source folder.
4. Configura un Java 21 SDK se IntelliJ non lo rileva automaticamente.
5. Apri `CatanProject/src/it/univaq/caffeine/catan/Main.java`.
6. Esegui la classe `Main`.

## Come clonare il repository

```bash
git clone <repository-url>
```

Dopo il clone, apri la cartella clonata in IntelliJ IDEA ed esegui `Main`.

## Allineamento UML

La struttura del codice segue il UML class diagram. I package sono organizzati per riflettere le principali aree architetturali: `controller`, `model` e `view`.

Il package `model` è ulteriormente diviso in subpackage specifici del dominio come `model.board`, `model.cards`, `model.player`, `model.rules` e `model.enums`.

## Note di sviluppo

Il progetto è ancora in sviluppo. Iterazioni future potrebbero aggiungere o migliorare:

- regole complete di Catan;
- gestione dei turni;
- produzione delle risorse;
- trading;
- comportamento di `Robber`;
- effetti delle development cards;
- controllo delle victory conditions;
- interazioni GUI migliorate;
- sistema save/load.

## Autori

Sviluppato da:

- Lorenzo Rodorigo
- Mario Del Corvo
- Gennaro ranalli

## Licenza

Questo progetto è destinato a scopi educativi.

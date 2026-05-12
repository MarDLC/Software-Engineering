# Catan Project

## Project Description

This project is a Java implementation of the board game Catan, developed for a Software Engineering course.

The code follows an object-oriented design based on the UML class diagram and is organized with an MVC-like architecture using `controller`, `model`, and `view`.

## Architecture

- `controller`: handles user actions and coordinates the interaction between the view and the model.
- `model`: contains the domain logic and the main game entities.
- `view`: contains the graphical/user interface classes.

## Package Structure

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

## Main Components

- `Main`: application entry point. It starts the setup flow and opens the game window.
- `GameController`: coordinates setup actions, player creation, game initialization, and placement requests.
- `Game`: central model class that stores players, map, deck, bank, rules, robber, and game state.
- `GameSetup`: stores setup choices such as game mode, number of players, player names, and bot difficulty.
- `Bank`: manages the stock of resource cards and transfers resources to or from players.
- `GameMap`: generates and stores the board graph, including tiles, intersections, edges, tokens, and ports.
- `HexagonalTile`: represents a board hex and its terrain/resource type.
- `Intersection`: represents a board node where a `Building` can be placed.
- `Edge`: represents a board connection where a `Street` can be placed.
- `Token`: stores the dice number assigned to a producing tile.
- `Port`: represents a maritime exchange port.
- `Robber`: represents the robber token and its current tile.
- `Deck`: manages the development card deck.
- `DevelopmentCard`: represents a development card and its `CardType`.
- `ResourceCard`: represents one resource card of a given `ResourceType`.
- `Player`: stores player data, owned cards, resources, buildings, and bot strategy if applicable.
- `Building`: abstract base class for player-owned board structures.
- `Colony`: settlement-type building placed on an `Intersection`.
- `City`: city-type building model, currently present for future rule support.
- `Street`: road-type building placed on an `Edge`.
- `GameRules`: validates placement rules currently used by the game flow.
- `CostRegistry`: stores resource costs for buildable elements.
- `ResourceType`: enum for resource and terrain types.
- `BuildingType`: enum for buildable element types.
- `CatanBoardPanel`: Swing panel that renders the board and handles board mouse interaction.
- `GameWindow`: main Swing window with board, status, resources, deck, and bank panels.
- `BotStrategy`: provides basic bot decisions for initial colony and road placement.

## Main Features

The project currently includes support for:

- game setup;
- player creation;
- game mode selection;
- map representation with tiles, intersections, and edges;
- colonies, cities, and streets as model classes;
- resource and development card classes;
- bank resource management;
- rule validation for initial colony and road placements;
- a basic Swing GUI structure;
- bot strategy support for initial placement.

Some full Catan gameplay features are still incomplete or planned.

## How to Run the Project

The IntelliJ IDEA project metadata is configured for Java 21 (`JDK_21`). Use Java 21 to match the project configuration.

This project does not currently include Maven or Gradle configuration. It can be run directly from IntelliJ IDEA.

1. Open IntelliJ IDEA.
2. Select **Open** and choose the cloned project folder.
3. Make sure `CatanProject/src` is marked as a source folder.
4. Configure a Java 21 SDK if IntelliJ does not detect it automatically.
5. Open `CatanProject/src/it/univaq/caffeine/catan/Main.java`.
6. Run the `Main` class.

## How to Clone the Repository

```bash
git clone <repository-url>
```

After cloning, open the cloned folder in IntelliJ IDEA and run `Main`.

## UML Alignment

The code structure follows the UML class diagram. Packages are organized to reflect the main architectural areas: `controller`, `model`, and `view`.

The `model` package is further divided into domain-specific subpackages such as `model.board`, `model.cards`, `model.player`, `model.rules`, and `model.enums`.

## Development Notes

The project is still under development. Future iterations may add or improve:

- complete Catan rules;
- turn management;
- resource production;
- trading;
- robber behavior;
- development card effects;
- victory condition checking;
- improved GUI interactions;
- a save/load system.

## Authors

Developed by:

- Lorenzo Rodorigo
- Mario Del Corvo
- Gennaro ranalli

## License

This project is intended for educational purposes.

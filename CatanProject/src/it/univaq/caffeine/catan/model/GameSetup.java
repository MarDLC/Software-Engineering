package it.univaq.caffeine.catan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSetup {
    private String       gameMode;
    private int          numberOfPlayers;
    private List<String> playerNames;
    private String       botDifficulty;

    public GameSetup() { playerNames = new ArrayList<>(); }

    public List<String> getAvailableGameModes() {
        return Arrays.asList("Local", "Against Bots");
    }

    public void setGameMode(String mode)        { this.gameMode = mode; }
    public void setNumberOfPlayers(int n)       { this.numberOfPlayers = n; }
    public void addPlayerName(String name)      { playerNames.add(name); }
    public void setBotDifficulty(String diff)   { this.botDifficulty = diff; }

    public String       getGameMode()           { return gameMode; }
    public int          getNumberOfPlayers()    { return numberOfPlayers; }
    public String       getPlayerName(int idx)  { return playerNames.get(idx); }
    public List<String> getPlayerNames()        { return playerNames; }
    public String       getBotDifficulty()      { return botDifficulty; }
}

package gmbh.norisknofun.game.networkmessages.attack;


import java.io.Serializable;

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;

/**
 * Generic message that a player won the game
 *
 */
public class PlayerWon extends BasicMessageImpl implements Serializable{

    String playerName;

    public PlayerWon(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
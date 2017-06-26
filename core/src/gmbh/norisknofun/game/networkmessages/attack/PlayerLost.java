package gmbh.norisknofun.game.networkmessages.attack;


import java.io.Serializable;

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;

/**
 * Generic message that the player lost the game
 *
 */
public class PlayerLost extends BasicMessageImpl implements Serializable{
    public PlayerLost() {
        // empty because client only needs to react on message type
    }
}

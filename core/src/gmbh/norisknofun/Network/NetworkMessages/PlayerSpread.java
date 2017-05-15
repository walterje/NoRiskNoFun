package gmbh.norisknofun.Network.NetworkMessages;

/**
 * Created by Philipp Mödritscher on 10.05.2017.
 */

/**
 *
 * After Start Game,GameServer should choose which Player starts an then send this Message
 * or after others Spread was finished choose next Player to spread
 * Tell the Player he should Spread his troops
 *
 * Server -> Client
 */


public class PlayerSpread extends BasicMessageImpl {

    String playername;
    boolean playersTurn = true;

}

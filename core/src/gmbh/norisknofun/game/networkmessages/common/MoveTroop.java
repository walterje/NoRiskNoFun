package gmbh.norisknofun.game.networkmessages.common;

/**
 * Created by Philipp Mödritscher on 10.05.2017.
 */

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;

/**
 *
 * After moved a Troops
 *
 * Client -> Server
 */


public class MoveTroop extends BasicMessageImpl {

    public String playername;
    public int troopamount;
    public String destinationregion;
    public String originregion;




}
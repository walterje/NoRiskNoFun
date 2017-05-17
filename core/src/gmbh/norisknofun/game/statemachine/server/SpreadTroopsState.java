package gmbh.norisknofun.game.statemachine.server;

import com.badlogic.gdx.Gdx;

import java.util.List;

import gmbh.norisknofun.assets.impl.map.AssetMap;
import gmbh.norisknofun.game.GameData;
import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;
import gmbh.norisknofun.game.networkmessages.common.MoveTroop;
import gmbh.norisknofun.game.networkmessages.common.MoveTroopCheck;
import gmbh.norisknofun.game.networkmessages.spread.PlayerSpread;
import gmbh.norisknofun.game.networkmessages.spread.PlayerSpreadCheck;
import gmbh.norisknofun.game.networkmessages.waitingforplayers.PlayerJoinedCheck;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by pippp on 15.05.2017.
 */

public class SpreadTroopsState implements State {

    private ServerContext context;
    private final GameData data;
    private int currentplayerindex=0;
    public SpreadTroopsState(ServerContext context){

        this.context=context;
        data=context.getGameData();
        assignRegionsToPlayer();
        assignTroopsToPlayer();
        setCurrentPlayer(currentplayerindex);

    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void handleMessage(BasicMessageImpl message) {


        if(message.getType().equals(MoveTroop.class)){
            moveTroop((MoveTroop)message);
        }
//        else if (message.getType().equals(PlayerSpreadCheck.class)){
//            setNextPlayer();
//        }
        else{
            Gdx.app.log("SpreadTroopsState","message unknown");
        }
    }

    private void moveTroop(MoveTroop message){
        List<AssetMap.Region> regions=context.getGameData().getMapAsset().getRegions();
        int i=0;
        if(message.playername.equals(context.getGameData().getCurrentplayer())) {
            while (!regions.get(i).getName().equals(message.destinationregion) && i < regions.size()) {
                i++;
            }
            if (regions.get(i).getOwner().equals(message.playername)) { // check if player is owner of selected region
                MoveTroop moveTroop = new MoveTroop();
                moveTroop.destinationregion = message.destinationregion;
                moveTroop.originregion = message.originregion;
                moveTroop.playername = message.playername;
                moveTroop.troopamount = message.troopamount;
                context.sendMessage(moveTroop);

                setNextPlayer();

            } else {
                MoveTroopCheck response = new MoveTroopCheck();
                response.playername = message.playername;
                response.movePossible = false;
                context.sendMessage(response); // todo how to send to specific client
            }
        }
    }

    private void setNextPlayer(){
        if(currentplayerindex>data.getPlayers().size()-1){
            currentplayerindex=0;
        }else {
            currentplayerindex++;
        }
        setCurrentPlayer(currentplayerindex);

    }


    private void setCurrentPlayer(int playerindex){
        data.setCurrentplayer(data.getPlayers().get(playerindex).getPlayername());
        PlayerSpread playerSpread = new PlayerSpread();
        playerSpread.playername=data.getCurrentplayer().getPlayername();
        playerSpread.playersTurn=true;
        context.sendMessage(playerSpread); //todo send to specific client
    }

    private void assignRegionsToPlayer(){

    }

    private void assignTroopsToPlayer(){

    }
}
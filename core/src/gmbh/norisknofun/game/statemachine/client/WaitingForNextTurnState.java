package gmbh.norisknofun.game.statemachine.client;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.assets.AssetMap;
import gmbh.norisknofun.game.GameData;
import gmbh.norisknofun.game.gamemessages.gui.MoveTroopGui;
import gmbh.norisknofun.game.gamemessages.gui.RemoveTroopGui;
import gmbh.norisknofun.game.gamemessages.gui.SpawnTroopGui;
import gmbh.norisknofun.game.gamemessages.gui.UpdateRegionOwnerGui;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.networkmessages.attack.PlayerWon;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.AttackResult;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.IsAttacked;
import gmbh.norisknofun.game.networkmessages.attack.PlayerLost;
import gmbh.norisknofun.game.networkmessages.common.MoveTroop;
import gmbh.norisknofun.game.networkmessages.common.NextPlayer;
import gmbh.norisknofun.game.networkmessages.common.SpawnTroop;
import gmbh.norisknofun.game.statemachine.State;


/**
 * Created by Katharina on 31.05.2017.
 */

class WaitingForNextTurnState extends State {

    private final ClientContext context;
    private final GameData data;

    WaitingForNextTurnState(ClientContext context){
        this.context=context;
        this.data=context.getGameData();
    }


    @Override
    public void handleMessage(String senderId, Message message) {
        if(message.getType().equals(NextPlayer.class)){
            setNextPlayer(((NextPlayer)message).getPlayername());
        } else if (message.getType().equals(IsAttacked.class)) {
            context.setState(new AttackState(context, false));
        } else if (message.getType().equals(AttackResult.class)) { // react on broadcast attack result as we have to update the regions
            updateRegions((AttackResult)message);
        } else if (message.getType().equals(SpawnTroop.class)) {
            doSpawnTroop((SpawnTroop) message);
        } else if (message.getType().equals(MoveTroop.class)) {
            doMoveTroop((MoveTroop) message);
        } else if (message.getType().equals(PlayerLost.class)) {
            playerLost();
        } else if (message.getType().equals(PlayerWon.class)) {
            playerWon((PlayerWon) message);
        }
        else{
            Gdx.app.log("WaitingForNextTurnState","unknown messgae:"+message.getType().getSimpleName());
        }
    }

    private void doMoveTroop(MoveTroop message) {
        context.getGameData().setGuiChanges(new MoveTroopGui(message.getFromRegion(),message.getToRegion(),message.getFigureId()));
    }

    private void doSpawnTroop(SpawnTroop message) {
        context.getGameData().setGuiChanges(new SpawnTroopGui(message.getRegionname(), message.getId()));
    }

    private void updateRegions(AttackResult message) {
        AssetMap.Region attackerRegion = data.getMapAsset().getRegion(message.getAttackerRegion());
        AssetMap.Region defenderRegion = data.getMapAsset().getRegion(message.getDefenderRegion());

        // if difference is negative
        // e.g. defender had 1 troop, got attacked with 3 -> difference = -2, so 2 troops need to be added
        int defenderRegionTroops = defenderRegion.getTroops() - message.getDefenderTroops();
        if (defenderRegionTroops < 0) {
            // spawn the correct amount of troops as an amount can't be set in SpawnTroopGui
            for (int i = 0; i < Math.abs(defenderRegionTroops); i++) {
                data.setGuiChanges(new SpawnTroopGui(defenderRegion.getName(), data.nextId()));
            }
        } else  {
            data.setGuiChanges(new RemoveTroopGui(defenderRegion.getName(), defenderRegion.getTroops() - message.getDefenderTroops()));
        }
        data.setGuiChanges(new UpdateRegionOwnerGui(defenderRegion.getName(), message.getDefenderRegionOwner()));

        data.setGuiChanges(new RemoveTroopGui(attackerRegion.getName(), attackerRegion.getTroops() - message.getAttackerTroops()));


        Gdx.app.log("EvaluateDiceResultState", "Attacker Region: " + attackerRegion.getName() +
                ", Troops: " + attackerRegion.getTroops());
        Gdx.app.log("EvaluateDiceResultState", "Defender Region: " + defenderRegion.getName() +
                ", Troops: " + defenderRegion.getTroops() +
                ", Owner: " + defenderRegion.getOwner());

    }

    private void setNextPlayer(String playerName){
        if(playerName!=null){
            data.setCurrentPlayer(playerName);

            if(data.isMyTurn()){
                context.setState(new DistributionState(context));
            }
        }
    }

    private void playerLost() {
        Gdx.app.log("Waiting State", "Received PlayerWon");
        data.setLastError("No regions left.\nYou lost.");
    }

    private void playerWon(PlayerWon message) {
        Gdx.app.log("Waiting State", "Received PlayerWon");
        data.setWinner(message.getPlayerName());
        context.setState(new EndGameState(context));
    }
}

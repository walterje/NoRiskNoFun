package gmbh.norisknofun.game.statemachine.client;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.game.gamemessages.gui.ActionDoneGui;
import gmbh.norisknofun.game.gamemessages.gui.MoveTroopGui;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.networkmessages.attack.PlayerLost;
import gmbh.norisknofun.game.networkmessages.attack.PlayerWon;
import gmbh.norisknofun.game.networkmessages.choosetarget.AttackRegion;
import gmbh.norisknofun.game.networkmessages.choosetarget.AttackRegionCheck;
import gmbh.norisknofun.game.networkmessages.choosetarget.NoAttack;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by Katharina on 19.05.2017.
 */

class ChooseTargetState extends State {

    private final ClientContext context;


    ChooseTargetState(ClientContext context){
        this.context=context;
    }


    @Override
    public void handleMessage(String senderId, Message message) {
        Gdx.app.log("Client ChooseTargetState", "Handling message: " + message.getClass().getName());
        
       if(message.getType().equals(AttackRegionCheck.class)){
           handleAttackRegionCheckMessage((AttackRegionCheck)message);
        } else if(message.getType().equals(ActionDoneGui.class)){ //player doesn't want to attack
           context.sendMessage(new NoAttack());
       } else if (message.getType().equals(MoveTroopGui.class)) {
           requestAttack((MoveTroopGui) message);
       } else if (message.getType().equals(NoAttack.class)) {
            context.setState(new MoveTroopsState(context));
       } else if (message.getType().equals(PlayerLost.class)) { // temporary
           playerLost();
       } else if (message.getType().equals(PlayerWon.class)) {
           playerWon((PlayerWon) message);
       }
       else {
           Gdx.app.log("Client ChooseTargetState", "unknown message:"+message.getClass().getSimpleName());
        }
    }

    private void handleAttackRegionCheckMessage(AttackRegionCheck message){
        if(message.isAttackreachable()){
            context.setState(new AttackState(context, true));
        }else{
            context.getGameData().setLastError(message.getErrorMessage());
        }
    }

    private void requestAttack(MoveTroopGui message){
        AttackRegion attackRegion= new AttackRegion(message.getFromRegion(),message.getToRegion());
        context.sendMessage(attackRegion);
    }

    private void playerLost() {
        Gdx.app.log("Waiting State", "Received PlayerLost");
        context.getGameData().setLastError("No regions left.\nYou lost.");
    }

    private void playerWon(PlayerWon message) {
        Gdx.app.log("Waiting State", "Received PlayerWon");
        context.getGameData().setWinner(message.getPlayerName());
        context.setState(new EndGameState(context));
    }
}

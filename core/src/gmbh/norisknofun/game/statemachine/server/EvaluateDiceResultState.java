package gmbh.norisknofun.game.statemachine.server;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.game.GameDataServer;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.AttackResult;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceAmount;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceResult;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by pippp on 15.05.2017.
 */

public class EvaluateDiceResultState extends State {

    private ServerContext context;
    private AttackState attackState;
    private final GameDataServer data;
    public EvaluateDiceResultState(ServerContext context, AttackState state){

        this.context=context;
        this.data=this.context.getGameData();
        this.attackState=state;
        sendDiceAmountToPlayers();
    }

    @Override
    public void handleMessage(String senderId, Message message) {
        if(message.getType().equals(DiceResult.class)){
            handleDiceResult(senderId,(DiceResult)message);
        }
    }

    private void handleDiceResult(String senderId, DiceResult message){


        if(getAttackerId().equals(senderId)){
            data.setAttackerDiceResult(message.getDiceResults());
        }else if(getDefenderId().equals(senderId)){
            data.setDefenderDiceResult(message.getDiceResults());
        }

        if(!isEmpty(data.getDefenderDiceResult()) && !isEmpty(data.getAttackerDiceResult())) {
            int [] result=calculateAttackResult();
            handleAttackResult(result[0],result[1]);
        }

    }

    private void handleAttackResult(int winsOfAttacker, int winsOfDefender){
        Gdx.app.log("Server EvaluateDiceResult", "Attacker Troops: " + data.getAttackerRegion().getTroops() + " Wins: " + winsOfAttacker);
        Gdx.app.log("Server EvaluateDiceResult", "Defender Troops: " + data.getDefendersRegion().getTroops() + " Wins: " + winsOfDefender);

        // all attacking troops will be either killed on lose or moved to new region on win, so subtract getAttackingTroops()
        data.getAttackerRegion().setTroops(data.getAttackerRegion().getTroops() - data.getAttackingTroops());
        data.getDefendersRegion().setTroops(data.getDefendersRegion().getTroops()-winsOfAttacker);

        int attackerTroopsRemaining = data.getAttackingTroops() - winsOfDefender;
        int defenderTroopsRemaining = data.getDefendersRegion().getTroops();

        if(defenderTroopsRemaining<=0){ // if attacker has won

            // owner changes on successful attack
            data.getDefendersRegion().setOwner(data.getPlayerById(getAttackerId()).getPlayerName());
            // all attacking troops are removed from attacker region, at least one is added to defender region
            broadcastResult(getAttackerId(), getDefenderId());
            Gdx.app.log("Server EvaluateDiceResult", "Attacker won.");
        } else if (attackerTroopsRemaining > 0 && defenderTroopsRemaining > 0) { // attacker won, but defender still has troops
            data.getAttackerRegion().updateTroops(attackerTroopsRemaining); // add the remaining troops back to the attacker
            broadcastResult(getAttackerId(), getDefenderId());
            Gdx.app.log("Server EvaluateDiceResult", "Attacker won, but defender still has troops");
        }
        else { // if defender has won

            broadcastResult(getDefenderId(), getAttackerId());
            Gdx.app.log("Server EvaluateDiceResult", "Attacker won.");
        }

        Gdx.app.log("Server EvaluateDiceResult", "Attacker Region Troops remaining: "
                + data.getAttackerRegion().getTroops() + ", Owner: "  + data.getAttackerRegion().getOwner());
        Gdx.app.log("Server EvaluateDiceResult", "Defender Region Troops remaining: "
                + data.getDefendersRegion().getTroops() + "Owner: " + data.getDefendersRegion().getOwner());

        context.setState(new ChooseTargetState(context));

    }

    private void broadcastResult(String winnerId, String loserId) {
        AttackResult attackResult= new AttackResult();

        attackResult.setAttackerRegion(data.getAttackerRegion().getName());
        attackResult.setAttackerTroops(data.getAttackerRegion().getTroops());
        attackResult.setDefenderRegion(data.getDefendersRegion().getName());
        attackResult.setDefenderTroops(data.getDefendersRegion().getTroops());
        attackResult.setDefenderRegionOwner(data.getDefendersRegion().getOwner());
        attackResult.setWinnerId(winnerId);
        attackResult.setLoserId(loserId);

        context.sendMessage(attackResult);
    }

    private void sendAttackResult(boolean won, String senderId){
        AttackResult attackResult= new AttackResult();

        attackResult.setAttackerRegion(data.getAttackerRegion().getName());
        attackResult.setAttackerTroops(data.getAttackerRegion().getTroops());
        attackResult.setDefenderRegion(data.getDefendersRegion().getName());
        attackResult.setDefenderTroops(data.getDefendersRegion().getTroops());
        attackResult.setDefenderRegionOwner(data.getDefendersRegion().getOwner());
        attackResult.setWon(won);
        context.sendMessage(attackResult,senderId);

    }

    private int [] calculateAttackResult(){

        int winsOfAttacker=0;
        int winsOfDefender=0;
        int [] defenderDiceResult = data.getDefenderDiceResult();
        int [] attackerDiceResult = data.getAttackerDiceResult();


          while (!isEmpty(defenderDiceResult) && !isEmpty(attackerDiceResult)){
              if(getMaxValue(attackerDiceResult)>getMaxValue(defenderDiceResult)){
                  winsOfAttacker++;
              }else {
                  winsOfDefender++;
              }
          }
        return new int[]{winsOfAttacker,winsOfDefender};
    }

    private int getMaxValue(int [] dice){
        int result=0;
        int index=-1;
        for(int i=0; i<dice.length; i++){
            if(dice[i]>result){
                result=dice[i];
                index=i;
            }
        }
        dice[index]=0;
        return result;
    }

    private boolean isEmpty(int [] dice){
        boolean check=true;
        for(int i=0; i<dice.length; i++){
            if(dice[i]!=0){
                check=false;
            }
        }
        return check;
    }
    private void sendDiceAmountToPlayers(){

        DiceAmount diceAmount= new DiceAmount(data.getAttackingTroops());
        context.sendMessage(diceAmount,getAttackerId());

        diceAmount= new DiceAmount(data.getDefendersRegion().getTroops()<2? 1:2);
        context.sendMessage(diceAmount,getDefenderId());

    }

    private String getDefenderId(){
        return data.getPlayerByName(data.getDefendersRegion().getOwner()).getId();
    }

    private String getAttackerId(){
        return data.getPlayerByName(data.getAttackerRegion().getOwner()).getId();
    }
}

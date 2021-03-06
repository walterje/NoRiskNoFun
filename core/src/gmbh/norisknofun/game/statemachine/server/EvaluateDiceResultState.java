package gmbh.norisknofun.game.statemachine.server;

import com.badlogic.gdx.Gdx;

import java.util.Arrays;

import gmbh.norisknofun.game.GameDataServer;
import gmbh.norisknofun.game.Player;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.networkmessages.attack.PlayerWon;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.AttackResult;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceAmount;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceResult;
import gmbh.norisknofun.game.networkmessages.attack.PlayerLost;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by pippp on 15.05.2017.
 */

class EvaluateDiceResultState extends State {

    private static final String LOG_TAG_EVALUATE_RESULT_NAME = "Server EvaluateDiceResult";
    private static final String LOG_TAG_CALCULATE_RESULT_NAME = "Server CalculateResult";

    private final ServerContext context;
    private final GameDataServer data;
    private String defenderName;
    private String defenderId;

    EvaluateDiceResultState(ServerContext context){

        this.context=context;
        this.data=this.context.getGameData();
    }

    @Override
    public void enter() {
        sendDiceAmountToPlayers();
    }

    @Override
    public void handleMessage(String senderId, Message message) {
        if(message.getType().equals(DiceResult.class)){
            handleDiceResult(senderId,(DiceResult)message);
        }
    }

    private void handleDiceResult(String senderId, DiceResult message){
        Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Handling Dice Result");


        if(getAttackerId().equals(senderId)){
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Setting result of Attacker" + Arrays.toString(message.getDiceResults()));

            data.setAttackerDiceResult(message.getDiceResults());
        }else if(getDefenderId().equals(senderId)){
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Setting result of Defender: " + Arrays.toString(message.getDiceResults()));

            data.setDefenderDiceResult(message.getDiceResults());
        }

        if(!isEmpty(data.getDefenderDiceResult()) && !isEmpty(data.getAttackerDiceResult())) {
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Results not empty");

            int [] result=calculateAttackResult();
            handleAttackResult(result[0],result[1]);

            resetResults();
        }

    }

    private void handleAttackResult(int winsOfAttacker, int winsOfDefender){
        Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Attacker Troops: " + data.getAttackerRegion().getTroops() + " Wins: " + winsOfAttacker);
        Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Defender Troops: " + data.getDefendersRegion().getTroops() + " Wins: " + winsOfDefender);

        defenderName = data.getDefendersRegion().getOwner();
        defenderId = data.getPlayerByName(defenderName).getId();
        Gdx.app.log("Server Evaluate", "Defender Name: " + defenderName);

        // all attacking troops will be either killed on lose or moved to new region on win, so subtract getAttackingTroops()
        data.getAttackerRegion().updateTroops(-data.getAttackingTroops());
        data.getDefendersRegion().updateTroops(-winsOfAttacker);

        int attackerTroopsRemaining = data.getAttackingTroops() - winsOfDefender;
        int defenderTroopsRemaining = data.getDefendersRegion().getTroops();

        if(defenderTroopsRemaining<=0){ // if attacker has won

            // owner changes on successful attack
            data.getDefendersRegion().setOwner(data.getPlayerById(getAttackerId()).getPlayerName());
            // all attacking troops are removed from attacker region, at least one is added to defender region
            data.getDefendersRegion().setTroops(attackerTroopsRemaining);
            broadcastResult(getAttackerId(), getDefenderId());
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Attacker won.");
        } else if (attackerTroopsRemaining > 0 && defenderTroopsRemaining > 0) { // attacker won, but defender still has troops
            data.getAttackerRegion().updateTroops(attackerTroopsRemaining); // add the remaining troops back to the attacker
            broadcastResult("", "");
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Attacker won, but defender still has troops");
        }
        else { // if defender has won
            data.getAttackerRegion().updateTroops(data.getAttackingTroops() - winsOfDefender);
            broadcastResult(getDefenderId(), getAttackerId());
            Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Attacker won.");
        }

        Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Attacker Region Troops remaining: "
                + data.getAttackerRegion().getTroops() + ", Owner: "  + data.getAttackerRegion().getOwner());
        Gdx.app.log(LOG_TAG_EVALUATE_RESULT_NAME, "Defender Region Troops remaining: "
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

        checkIfDefenderLost();
    }

    private int [] calculateAttackResult(){

        int winsOfAttacker=0;
        int winsOfDefender=0;
        int [] defenderDiceResult = data.getDefenderDiceResult();
        int [] attackerDiceResult = data.getAttackerDiceResult();

        Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "Attacker: " + Arrays.toString(attackerDiceResult));
        Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "Defender: " + Arrays.toString(defenderDiceResult));


        while (!isEmpty(defenderDiceResult) && !isEmpty(attackerDiceResult)){
            Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "None empty");

            if(getMaxValue(attackerDiceResult)>getMaxValue(defenderDiceResult)){
                winsOfAttacker++;
                Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "Attacker won: " + winsOfAttacker);

            }else {
                winsOfDefender++;
                Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "Defender won: " + winsOfDefender);

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
        Gdx.app.log(LOG_TAG_CALCULATE_RESULT_NAME, "Max: " + result +", Index: " + index);

        return result;
    }

    private boolean isEmpty(int [] dice){
        boolean check=true;
        for (int aDice : dice) {
            if (aDice != 0) {
                check = false;
            }
        }
        return check;
    }
    private void sendDiceAmountToPlayers(){

        DiceAmount diceAmount= new DiceAmount(data.getAttackingTroops());
        context.sendMessage(diceAmount,getAttackerId());

        diceAmount= new DiceAmount(data.getDefendersRegion().getTroops()<2 ? 1:2);
        context.sendMessage(diceAmount,getDefenderId());

    }

    private void resetResults() {
        for (int i = 0; i < data.getAttackerDiceResult().length; i++) {
            data.getAttackerDiceResult()[i] = 0;
            data.getDefenderDiceResult()[i] = 0;
        }
    }

    private String getDefenderId(){
        return data.getPlayerByName(data.getDefendersRegion().getOwner()).getId();
    }

    private String getAttackerId(){
        return data.getPlayerByName(data.getAttackerRegion().getOwner()).getId();
    }

    private void checkIfDefenderLost() {
        Gdx.app.log("Server Evaluate", "Def Region Remaining: " +
                data.getNumberOfRegionOwnedByPlayer(defenderName));

        if (data.getNumberOfRegionOwnedByPlayer(defenderName) <= 0) {

            Gdx.app.log("Server Evaluate", "Sending PlayerLost() to " + data.getPlayerById(defenderId).getPlayerName());

            data.getPlayers().removePlayer(defenderName);
            context.sendMessage(new PlayerLost(), defenderId);

            checkIfSomeoneHasWon(); // may only happen if the defender lost, so only check here
        }
    }

    /**
     * Check if all regions belong to the same player
     */
    private void checkIfSomeoneHasWon(){
        Gdx.app.log("Server Evaluate", "Checking if won...");
        int numOfRegion;
        for(Player player: data.getPlayers().getPlayerlist()){
            numOfRegion=data.getNumberOfRegionOwnedByPlayer(player.getPlayerName());
            if(numOfRegion==data.getMapAsset().getRegions().size()){
                Gdx.app.log("Server Evaluate", "Player Won!: " + player.getPlayerName());
                context.sendMessage(new PlayerWon(player.getPlayerName()));
            }
        }
    }
}

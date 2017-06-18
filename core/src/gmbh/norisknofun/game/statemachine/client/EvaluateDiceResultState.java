package gmbh.norisknofun.game.statemachine.client;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.assets.AssetMap;
import gmbh.norisknofun.game.gamemessages.gui.EvaluateDiceResultGui;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.AttackResult;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceAmount;
import gmbh.norisknofun.game.networkmessages.attack.evaluatedice.DiceResult;
import gmbh.norisknofun.game.statemachine.State;
import gmbh.norisknofun.scene.SceneManager;
import gmbh.norisknofun.scene.SceneNames;

/**
 * Created by Katharina on 19.05.2017.
 */

public class EvaluateDiceResultState extends State {

    private ClientContext context;
    private AttackState attackState;
    public EvaluateDiceResultState(ClientContext context, AttackState state){
        this.context=context;
        this.attackState=state;
    }
    @Override
    public void handleMessage(String senderId, Message message) {
        if(message.getType().equals(DiceAmount.class)){

            context.getGameData().setAvailableDice(((DiceAmount)message).getAmount());
            SceneManager.getInstance().setActiveScene(SceneNames.DICE_SCENE);

        }else if(message.getType().equals(EvaluateDiceResultGui.class)){
            context.sendMessage(new DiceResult(context.getGameData().getDiceRoll()));

        }else if(message.getType().equals(AttackResult.class)){

            handleAttackResult((AttackResult)message);

        }else{
            Gdx.app.log("Client - EvaluateDiceResultState","unknown message:"+message.getClass().getSimpleName());
        }
    }


    private void handleAttackResult(AttackResult attackResult){

        updateRegions(attackResult);
        if(context.getGameData().isMyTurn()){ //  I am attacker
            if(attackResult.isWon()) {
                context.getGameData().setLastError("Congratulations!  \n  you captured region: " + attackResult.getDefenderRegion());
            }else {
                context.getGameData().setLastError("You lost!  \n  more luck next time.");
            }
            context.setState(new ChooseTargetState(context));
        }else { //  I am Defender
            if(attackResult.isWon()) {
                context.getGameData().setLastError("Congratulations!  \n  you defended region: " + attackResult.getDefenderRegion());
            }else {
                context.getGameData().setLastError("Sorry!  \n  you lost region:"+attackResult.getDefenderRegion());
            }
            context.setState(new WaitingForNextTurnState(context));
        }


        // switch back to Game Scene after the attack was done
        SceneManager.getInstance().setActiveScene(SceneNames.GAME_SCENE);
    }

    /**
     * Update Troops on attacker and defender region
     * if defender lost -> also update region owner
     * @param message
     */
    private void updateRegions(AttackResult message){
        AssetMap.Region attackerRegion = context.getGameData().getMapAsset().getRegion(message.getAttackerRegion());
        AssetMap.Region defenderRegion = context.getGameData().getMapAsset().getRegion(message.getDefenderRegion());

        attackerRegion.setTroops(message.getAttackerTroops());

        defenderRegion.setTroops(message.getDefenderTroops());
        defenderRegion.setOwner(message.getDefenderRegionOwner());
    }
}

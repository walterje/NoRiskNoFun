package gmbh.norisknofun.game.statemachine.client;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;
import gmbh.norisknofun.game.networkmessages.common.MoveTroop;
import gmbh.norisknofun.game.networkmessages.common.MoveTroopCheck;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by Katharina on 19.05.2017.
 */

public class MoveTroopsState extends State {

    private ClientContext context;

    public MoveTroopsState(ClientContext context){
        this.context=context;
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
            // todo interface between statemachine and GUI
        }else if(message.getType().equals(MoveTroopCheck.class)){
            // todo interface between statemachine and GUI
        }
        else {
            Gdx.app.log("WaitingForPlayers","unknown message");
        }
    }
}
package gmbh.norisknofun.game.statemachine.server;

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;
import gmbh.norisknofun.game.statemachine.State;

/**
 * Created by pippp on 15.05.2017.
 */

public class ChooseTargetState implements State {

    private ServerContext context;
    public ChooseTargetState(ServerContext context){
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

    }
}
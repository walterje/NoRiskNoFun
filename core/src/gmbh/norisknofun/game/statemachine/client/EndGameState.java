package gmbh.norisknofun.game.statemachine.client;

import com.badlogic.gdx.Gdx;

import gmbh.norisknofun.game.gamemessages.gui.EndGameGui;
import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.game.statemachine.State;
import gmbh.norisknofun.scene.SceneManager;
import gmbh.norisknofun.scene.SceneNames;

/**
 * Created by user on 25.06.17.
 */

public class EndGameState extends State {

    private ClientContext context;

    public EndGameState (ClientContext context) {

        this.context = context;
    }

    @Override
    public void enter() {
        SceneManager.getInstance().setActiveScene(SceneNames.END_GAME_SCENE);
    }


    @Override
    public void handleMessage(String senderId, Message message) {
        if (message.getType().equals(EndGameGui.class)) {
            SceneManager.getInstance().setActiveScene(SceneNames.MAIN_MENU_SCENE);
        }
        else {
            Gdx.app.log(getClass().getSimpleName(), "Unknown Message: " + message.getClass().getSimpleName());
        }
    }
}

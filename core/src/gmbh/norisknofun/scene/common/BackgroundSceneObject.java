package gmbh.norisknofun.scene.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import gmbh.norisknofun.scene.SceneObject;

/**
 * Created by pippp on 06.05.2017.
 */

public class BackgroundSceneObject extends SceneObject {

    private Texture texture;

    public BackgroundSceneObject(){
        super();
        texture = new Texture("img/menu.png");

    }

    @Override
    public void draw(Batch batch, float parentAlpha){
       batch.draw(texture,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }
}

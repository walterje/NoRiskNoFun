package gmbh.norisknofun.scene.common;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


import gmbh.norisknofun.GdxTest;
import gmbh.norisknofun.assets.AssetFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


@Ignore("TODO refactor")
public class TextButtonSceneObjectTest extends GdxTest{


    @Test
    public void setBoundsSetsPositionCorrectly() {

        TextButtonSceneObject sceneObject = new TextButtonSceneObject(mock(AssetFactory.class), "test", null);

        sceneObject.setBounds(2,3,1,1);
        assertEquals(2, (int)sceneObject.getX());
        assertEquals(3, (int)sceneObject.getY());
    }

    @Test
    public void setBoundsSetsSizeCorrectly() {

        TextButtonSceneObject sceneObject = new TextButtonSceneObject(mock(AssetFactory.class), "test", null);

        sceneObject.setBounds(1,1,2,3);
        assertEquals(2, (int)sceneObject.getWidth());
        assertEquals(3, (int)sceneObject.getHeight());
    }

}

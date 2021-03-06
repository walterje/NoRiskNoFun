package gmbh.norisknofun.scene.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import gmbh.norisknofun.assets.AssetSound;
import gmbh.norisknofun.scene.Assets;
import gmbh.norisknofun.scene.SceneBase;
import gmbh.norisknofun.scene.SceneData;
import gmbh.norisknofun.scene.SceneNames;
import gmbh.norisknofun.scene.Texts;
import gmbh.norisknofun.scene.common.BackgroundSceneObject;
import gmbh.norisknofun.scene.common.ImageButtonSceneObject;
import gmbh.norisknofun.scene.common.LabelSceneObject;
import gmbh.norisknofun.scene.common.SwitchSceneClickListener;
import gmbh.norisknofun.scene.common.TextButtonSceneObject;

/**
 * Map selection scene.
 */
public final class MapSelectionScene extends SceneBase {

    private static final String MAP_ONE_BUTTON_TEXT = "Carinthia";
    private static final String MAP_TWO_BUTTON_TEXT = "Styria";
    private static final String MAP_THREE_BUTTON_TEXT = "Tyrol";

    private final SceneData sceneData;
    private final AssetSound buttonPressedSound;

    private final float buttonWidth = Gdx.graphics.getWidth()/5.5f;
    private final float buttonHight = Gdx.graphics.getHeight()/5.5f;

    public MapSelectionScene(SceneData sceneData) {

        super(SceneNames.MAP_SELECTION_SCENE, Color.WHITE);
        this.sceneData = sceneData;
        this.buttonPressedSound = sceneData.createSound(Assets.BUTTON_PRESSED_SOUND_FILENAME);

        setBackground();
        initMapSelectionButtons();
        initLabel();
    }

    private void setBackground() {
        addSceneObject(new BackgroundSceneObject(sceneData.getAssetFactory()));
    }

    private void initMapSelectionButtons() {
        TextButtonSceneObject buttonMapOne = new TextButtonSceneObject(
                sceneData.createTextButton(MAP_ONE_BUTTON_TEXT, Assets.DEFAULT_TEXT_BUTTON_DESCRIPTOR), buttonPressedSound);
        TextButtonSceneObject buttonMapTwo = new TextButtonSceneObject(
                sceneData.createTextButton(MAP_TWO_BUTTON_TEXT, Assets.DEFAULT_TEXT_BUTTON_DESCRIPTOR), buttonPressedSound);
        TextButtonSceneObject buttonMapThree = new TextButtonSceneObject(
                sceneData.createTextButton(MAP_THREE_BUTTON_TEXT, Assets.DEFAULT_TEXT_BUTTON_DESCRIPTOR), buttonPressedSound);
        ImageButtonSceneObject backButton = new ImageButtonSceneObject(sceneData.createImageButton(Assets.BACK_BUTTON_FILENAME), buttonPressedSound);

        buttonMapOne.setBounds((Gdx.graphics.getWidth()/7.0f),Gdx.graphics.getHeight()/2.0f,buttonWidth+4.0f,buttonHight);
        buttonMapTwo.setBounds((Gdx.graphics.getWidth() / 4.5f),Gdx.graphics.getHeight()/6.0f,buttonWidth,buttonHight);
        buttonMapThree.setBounds(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight()/2.0f,buttonWidth , buttonHight);
        backButton.setBounds(Gdx.graphics.getWidth() / 1.6f, Gdx.graphics.getHeight() / 10.0f, Gdx.graphics.getWidth()/6.0f, Gdx.graphics.getHeight()/6.0f);

        buttonMapOne.addListener(new SetSelectedMapClickListener("maps/carinthia.map"));
        buttonMapOne.addListener(new SwitchSceneClickListener(SceneNames.LOBBY_SCENE));
        buttonMapTwo.addListener(new SetSelectedMapClickListener("maps/styria.map"));
        buttonMapTwo.addListener(new SwitchSceneClickListener(SceneNames.LOBBY_SCENE));
        buttonMapThree.addListener(new SetSelectedMapClickListener("maps/tyrol.map"));
        buttonMapThree.addListener(new SwitchSceneClickListener(SceneNames.LOBBY_SCENE));
        backButton.addListener(new SetSelectedMapClickListener(null));
        backButton.addListener(new SwitchSceneClickListener(SceneNames.CREATE_GAME_SCENE));

        addSceneObject(buttonMapOne);
        addSceneObject(buttonMapTwo);
        addSceneObject(buttonMapThree);
        addSceneObject(backButton);
    }

    /**
     * Initialise the label shown on main menu.
     */
    private void initLabel() {

        LabelSceneObject sceneObject = new LabelSceneObject(sceneData.createLabel(Texts.MAP_SELECTION, Assets.FONT_110PX_WHITE_WITH_BORDER));
        addSceneObject(sceneObject);
        sceneObject.setBounds((Gdx.graphics.getWidth() - sceneObject.getWidth()) / 2.0f,
                Gdx.graphics.getHeight() - (sceneObject.getHeight() * 2.0f),
                sceneObject.getWidth(),
                sceneObject.getHeight());
    }

    @Override
    public void dispose() {
        buttonPressedSound.dispose();
        super.dispose();
    }

    private final class SetSelectedMapClickListener extends ClickListener {

        private final String mapFilename;

        SetSelectedMapClickListener(String mapFilename) {

            this.mapFilename = mapFilename;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            sceneData.setMapFilename(mapFilename);
        }
    }
}

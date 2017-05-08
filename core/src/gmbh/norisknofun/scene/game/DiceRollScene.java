package gmbh.norisknofun.scene.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gmbh.norisknofun.game.GameData;
import gmbh.norisknofun.scene.SceneBase;
import gmbh.norisknofun.scene.SceneManager;
import gmbh.norisknofun.scene.SceneNames;
import gmbh.norisknofun.scene.common.DiceSceneObject;
import gmbh.norisknofun.scene.common.LabelSceneObject;
import gmbh.norisknofun.scene.common.TextButtonSceneObject;

public class DiceRollScene extends SceneBase {

    private final GameData data;

    private BitmapFont font;
    private LabelSceneObject cheatLabel;
    private List<DiceSceneObject> dieObjects;

    private static final float GRAVITY_EARTH = 9.80665f;
    private static final float SHAKE_GRAVITY_THRESHOLD = 2.0f;

    private long lastShakeTime;
    private int[] rollResults = {0, 0, 0};
    private int cheatsAvailable;
    private boolean hasBeenShaken;
    private boolean canRoll;
    private int dieAmount = 3;


    public DiceRollScene(GameData data) {
        super(SceneNames.DICE_SCENE, Color.BLACK);
        this.data = data;
    }

    @Override
    public void show() {
        dieObjects = new ArrayList<>();

        cheatsAvailable = 3;
        hasBeenShaken = false;
        canRoll = true;

        initDie(1, 0, 100, 540);
        initDie(1, 1, 700, 540);
        initDie(1, 2, 1300, 540);

        setDiceClickListener();

        cheatLabel = initLabel();
        addSceneObject(cheatLabel);

        initBackButton();

        super.show();
    }

    /**
     * Initialize a DiceSceneObject and add it to the scene
     * @param number dice image to show
     * @param index place in the dieObjects list
     * @param x coordinate
     * @param y coordinate
     */
    private void initDie(int number, int index, int x, int y) {
        DiceSceneObject dieObject;
        dieObject = new DiceSceneObject(number, index, x, y, 500, 500);
        dieObjects.add(dieObject);
        addSceneObject(dieObject);
    }

    /**
     * Create a back button to return to the game scene
     */
    private void initBackButton() {
        TextButtonSceneObject backButton;
        backButton = createButton("Back");
        backButton.setBounds(1000, 100, 500, 100);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                writeRollResult(); // write roll result only now as we're done when we press back
                // clear stage here as we have to redraw it on next press anyway
                getStage().clear();
                SceneManager.getInstance().setActiveScene(SceneNames.GAME_SCENE);
            }
        });
        addSceneObject(backButton);
    }

    /**
     * Create a generic text button with the given text
     * @param buttonText Text on the button
     * @return created TextButtonSceneObject
     */
    private TextButtonSceneObject createButton(String buttonText) {

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture("button.png")));
        style.down = new TextureRegionDrawable(new TextureRegion(new Texture("button.png")));
        style.fontColor = new Color(0.9f, 0.5f, 0.5f, 1);
        style.downFontColor = new Color(0, 0.4f, 0, 1);

        return new TextButtonSceneObject(new TextButton(buttonText, style));
    }

    /**
     * Create a label
     * @return created LabelSceneObject
     */
    private LabelSceneObject initLabel() {

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3.5f);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        return new LabelSceneObject(new Label(""+cheatsAvailable, style));
    }

    /**
     * Check if the device is currently being shaken
     *
     * @return true if the device registered a shake
     */
    private boolean hasShaken() {
        // get current gravity readings of accelerometer axes in relation to earth's gravity
        float xGrav = Gdx.input.getAccelerometerX() / GRAVITY_EARTH;
        float yGrav = Gdx.input.getAccelerometerY() / GRAVITY_EARTH;
        float zGrav = Gdx.input.getAccelerometerZ() / GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float) Math.sqrt((xGrav * xGrav) + (yGrav * yGrav) + (zGrav * zGrav));

        if (gForce > SHAKE_GRAVITY_THRESHOLD) {
            return true;
        }
        return false;
    }

    /**
     * Roll all dice and update their results
     */
    private void diceRoll() {
        Random rnd = new Random();
        rnd.setSeed(TimeUtils.nanoTime());

        // generate a random number from 1-6
        for (int i = 0; i < rollResults.length; i++) {
            rollResults[i] = rnd.nextInt(6) + 1;
        }
    }

    /**
     * Roll only a single die and update the result
     *
     * @param index die to roll
     */
    private void diceRoll(int index) {
        Random rnd = new Random();
        rnd.setSeed(TimeUtils.nanoTime());

        rollResults[index] = rnd.nextInt(6) + 1;
    }

    /**
     * "Animate" the dice roll
     */
    private void randomizeDice() {
        Random rnd = new Random();
        rnd.setSeed(TimeUtils.nanoTime());

        for (int i = 0; i < dieAmount; i++) {
            dieObjects.get(i).setDieNumber(rnd.nextInt(6) + 1);
        }
    }

    /**
     * Set the images to actual roll results
     */
    private void showRollResult() {

        for (int i = 0; i < dieAmount; i++) {
            dieObjects.get(i).setDieNumber(rollResults[i]);
        }
    }

    /**
     * Set the images to the roll result of a specific die
     *
     * @param index of die and result to update
     */
    private void showRollResult(int index) {
        dieObjects.get(index).setDieNumber(rollResults[index]);
    }

    /**
     * Check if player can cheat and do so if it's available.
     *
     * @param index index of the die to re-roll
     */
    private void tryCheat(int index) {
        if (cheatsAvailable > 0) {
            diceRoll(index);
            showRollResult(index);
            cheatsAvailable--;
            cheatLabel.getLabel().setText(""+cheatsAvailable);
            System.out.println("[DEBUG] Cheat successful. " + cheatsAvailable + " remaining.");
        } else {
            System.out.println("[DEBUG] No cheats remaining.");
        }
    }

    /**
     * Set ClickListener to all die.
     * Used for cheat function.
     * TODO: Dynamically depending on the amount of dice available
     */
    private void setDiceClickListener() {

        dieObjects.get(0).addListener(new ClickListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("FIRST DIE PRESSED!");
                    tryCheat(0);
                    System.out.printf("[DEBUG] %d, %d, %d\n", rollResults[0], rollResults[1], rollResults[2]);
                    return true;
                }
            });

        dieObjects.get(1).addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("SECOND DIE PRESSED!");
                tryCheat(1);
                System.out.printf("[DEBUG] %d, %d, %d\n", rollResults[0], rollResults[1], rollResults[2]);
                return true;
            }
        });

        dieObjects.get(2).addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("THIRD DIE PRESSED!");
                tryCheat(2);
                System.out.printf("[DEBUG] %d, %d, %d\n", rollResults[0], rollResults[1], rollResults[2]);
                return true;
            }
        });
    }


    /**
     * Set the amount of dice available in the scene.
     * This changes according to the Risiko rules
     * @param dieAmount amount of dice available
     */
    public void setDieAmount(int dieAmount) {
        this.dieAmount = dieAmount;
    }

    /**
     * Write the roll result back to GameData after the roll is done
     */
    private void writeRollResult() {
        data.setDiceRoll(rollResults);
    }


    @Override
    public void render(float delta) {

        if ((hasShaken() || hasBeenShaken) && canRoll) {


            // only update if it hasn't been shaken in the last 2 seconds
            if (TimeUtils.millis() - lastShakeTime > 5000) {
                diceRoll();
                System.out.printf("[DEBUG] %d, %d, %d\n", rollResults[0], rollResults[1], rollResults[2]);
                lastShakeTime = TimeUtils.millis();
                hasBeenShaken = true;
            }

            // randomize results for 2 seconds after a shake
            if (TimeUtils.millis() - lastShakeTime < 2000) {
                randomizeDice();
            } else {
                showRollResult();
                hasBeenShaken = false;
                canRoll = false;
            }
        }

        super.render(delta);
    }

}

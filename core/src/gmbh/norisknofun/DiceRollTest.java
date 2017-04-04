package gmbh.norisknofun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Random;

import static gmbh.norisknofun.NoRiskNoFun.SCREEN_HEIGHT;
import static gmbh.norisknofun.NoRiskNoFun.SCREEN_WIDTH;

public class DiceRollTest implements Screen {

    private NoRiskNoFun game;

    private long lastShakeTime;
    private String diceRollText;
    private int[] rollResults = new int[3];

    private static final float GRAVITY_EARTH = 9.80665f;
    private static final float SHAKE_GRAVITY_THRESHOLD = 3.0f;

    // temporarily declare gravities as class variables
    private float xGrav;
    private float yGrav;
    private float zGrav;


    public DiceRollTest(NoRiskNoFun game) {
        this.game = game;
    }

    private boolean hasShaken() {
        // get current gravity readings of accelerometer axes in relation to earth's gravity
        xGrav = Gdx.input.getAccelerometerX() / GRAVITY_EARTH;
        yGrav = Gdx.input.getAccelerometerY() / GRAVITY_EARTH;
        zGrav = Gdx.input.getAccelerometerZ() / GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float) Math.sqrt((xGrav * xGrav) + (yGrav * yGrav) + (zGrav * zGrav));

        if (gForce > SHAKE_GRAVITY_THRESHOLD) {
            return true;
        }
        return false;
    }

    private void diceRoll() {
        Random rnd = new Random();
        rnd.setSeed(TimeUtils.nanoTime());

        // generate a random number from 1-6
        for (int i = 0; i < rollResults.length; i++) {
            rollResults[i] = rnd.nextInt(6) + 1;
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        // millis() should be precise enough for this. No need for nanoTime()
        if (hasShaken()) {
            // only update if it hasn't been shaken in the last 2 seconds
            if (TimeUtils.millis() - lastShakeTime > 2000) {
                diceRoll();
                diceRollText = "HAS BEEN SHAKEN!";
                lastShakeTime = TimeUtils.millis();
            }
        }
        if (TimeUtils.millis() - lastShakeTime > 2000) {
            diceRollText = "SHAKE ME!";
        }

        game.font.draw(game.batch, diceRollText ,0 , SCREEN_HEIGHT);

        // show gravity values
        game.font.draw(game.batch, "Gravity X: " + xGrav, SCREEN_WIDTH/2, SCREEN_HEIGHT);
        game.font.draw(game.batch, "Gravity Y: " + yGrav, SCREEN_WIDTH/2, SCREEN_HEIGHT - 200);
        game.font.draw(game.batch, "Gravity Z: " + zGrav, SCREEN_WIDTH/2, SCREEN_HEIGHT - 400);

        // show roll results
        game.font.draw(game.batch, "1st Roll: " + rollResults[0], 0, SCREEN_HEIGHT - 200);
        game.font.draw(game.batch, "2nd Roll: " + rollResults[1], 0, SCREEN_HEIGHT - 400);
        game.font.draw(game.batch, "3rd Roll: " + rollResults[2], 0, SCREEN_HEIGHT - 600);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

}

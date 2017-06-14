package gmbh.norisknofun.game.gamemessages.gui;

import gmbh.norisknofun.game.networkmessages.BasicMessageImpl;

/**
 * Inform the Statemachine that a Region's troops have been updated
 */

public class SpawnTroopGui extends BasicMessageImpl {
    private String regionName;
    private float x;
    private float y;

    public SpawnTroopGui(String regionName, float x, float y) {
        this.regionName = regionName;
        this.x = x;
        this.y = y;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
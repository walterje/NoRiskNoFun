package gmbh.norisknofun.game;

import com.badlogic.gdx.Gdx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmbh.norisknofun.assets.AssetLoaderFactory;
import gmbh.norisknofun.assets.impl.map.AssetMap;

/**
 * Class containing game related data.
 */
public class GameData {

    private final AssetLoaderFactory assetLoaderFactory;

    private String mapFilename = null;
    private AssetMap mapAsset = null;
    private int[] diceRoll;
    private String currentplayer;

    private List<Player> players = new ArrayList<>();

    public GameData(AssetLoaderFactory assetLoaderFactory) {
        this.assetLoaderFactory = assetLoaderFactory;
    }

    AssetLoaderFactory getAssetLoaderFactory() {
        return assetLoaderFactory;
    }

    public void setMapFile(String mapFilename) {
        this.mapFilename = mapFilename;
        mapAsset = null;
    }

    public AssetMap getMapAsset() {
        if (mapFilename == null)
            throw new IllegalStateException("mapFile was not set");

        if (mapAsset == null) {
            try (InputStream stream = Gdx.files.internal(mapFilename).read()) {
                mapAsset = assetLoaderFactory.createAssetLoaderMap().load(stream);
            } catch (Exception e) {
                Gdx.app.error("MAP", "Failed to load map asset", e);
            }
        }

        return mapAsset;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public List<Player> getPlayers(){
        return players;
    }

    public void setDiceRoll(int[] roll) {
        diceRoll = roll;
    }
    public int[] getDiceRoll() {
        return diceRoll;
    }

    public void setCurrentplayer(String currentplayer){
        this.currentplayer=currentplayer;
    }
    public Player getCurrentplayer(){
        Player player= null;
        for(Player p: players){
            if(p.getPlayername().equals(currentplayer)){
                player=p;
            }
        }
        return player;
    }
}

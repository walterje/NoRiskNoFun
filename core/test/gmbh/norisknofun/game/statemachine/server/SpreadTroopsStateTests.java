package gmbh.norisknofun.game.statemachine.server;


import org.junit.Test;
import org.junit.Before;

import gmbh.norisknofun.assets.impl.AssetFactoryImpl;

import gmbh.norisknofun.game.GameDataServer;
import gmbh.norisknofun.game.Player;
import gmbh.norisknofun.game.networkmessages.common.MoveTroop;
import gmbh.norisknofun.game.networkmessages.common.SpawnTroop;
import gmbh.norisknofun.game.server.MessageBus;
import gmbh.norisknofun.game.server.messaging.MessageBusImpl;

import gmbh.norisknofun.GdxTest;


public class SpreadTroopsStateTests extends GdxTest {
    GameDataServer data;
    ServerContext context;
    Player player;
    Player player2;

    @Before
    public void setup() {
        MessageBus messageBus = new MessageBusImpl(); // can't mock because it's a final class
        data = new GameDataServer(new AssetFactoryImpl());
        player = new Player("Player1", "123");
        player2 = new Player("Player2", "987");


        data.getPlayers().addPlayer(player);
        data.getPlayers().addPlayer(player2);

        context = new ServerContext(data, messageBus);
        context.setState(new SpreadTroopsState(context));
    }

    @Test
    public void nullNameWillNotThrowException() {
        SpawnTroop message = new SpawnTroop(null, "Otherregion");

        context.handle("123", message);

    }

    @Test
    public void nullRegionWillNotThrowException() {
        SpawnTroop message = new SpawnTroop("hubert",null);

        context.handle("123", message);
    }



}
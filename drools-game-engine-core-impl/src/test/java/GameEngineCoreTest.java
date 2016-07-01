/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import org.drools.game.core.GameConfigurationImpl;
import org.drools.game.core.GameSessionImpl;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameSession;
import org.drools.game.model.api.Player;
import org.drools.game.model.impl.base.PlayerImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class GameEngineCoreTest {

    public GameEngineCoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void hello() {
        GameSession gameSession = new GameSessionImpl();

        Player p = new PlayerImpl( "salaboy" );
        List initialData = null;
        gameSession.bootstrap( p, new GameConfigurationImpl( initialData, "" ) );

        gameSession.execute( new Command<List<Player>>() {
            @Override
            public List<Player> execute( Context ctx ) {
                return null;
            }
        } );

    }
}


package org.drools.game.capture.flag.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.drools.game.capture.flag.cmds.EnterZoneCommand;
import org.drools.game.capture.flag.cmds.PickFlagCommand;
import org.drools.game.capture.flag.model.Chest;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.capture.flag.model.Location;
import org.drools.game.capture.flag.model.NamedLocation;
import org.drools.game.capture.flag.model.Zone;
import org.drools.game.capture.flag.model.Team;
import org.drools.game.core.api.GameMessage;
import org.drools.game.core.api.GameSession;
import org.drools.game.core.CommandExecutorImpl;
import org.drools.game.core.BaseGameConfigurationImpl;
import org.drools.game.core.BasePlayerConfigurationImpl;
import org.drools.game.core.GameCallbackServiceImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.core.GameSessionImpl;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.GameCallbackService;
import org.drools.game.core.api.GameConfiguration;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.core.api.PlayerConfiguration;

import org.drools.game.model.api.Player;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 *
 */
@RunWith( Arquillian.class )
public class GameAPITest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addClass( GameSessionImpl.class )
                .addClass( CommandExecutorImpl.class )
                .addClass( GameMessageService.class )
                .addClass( GameMessageServiceImpl.class )
                .addClass( GameCallbackService.class )
                .addClass( GameCallbackServiceImpl.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    private GameSession game;

    /*

     */
    @Test
    public void pickTheFlagHitTheChasmTest() {
        Player player = new BasePlayerImpl( "salaboy" );

        List initFacts = new ArrayList();
        Chest chest = new Chest( "Flag Chest", new Location( 0, 0, 0 ) );
        initFacts.add( chest );
        Team redTeam = new Team( "red" );
        initFacts.add( redTeam );
        Zone scoreZoneRed = new Zone( "red" );
        initFacts.add( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( "red" );
        initFacts.add( redSpawn );
        Team blueTeam = new Team( "blue" );
        initFacts.add( blueTeam );
        Zone scoreZoneBlue = new Zone( "blue" );
        initFacts.add( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( "blue" );
        initFacts.add( blueSpawn );
        Flag flag = new Flag( "Flag", "banner" );
        initFacts.add( flag );
        Zone chasm = new Zone( "Chasm" );
        initFacts.add( chasm );
        // Bootstrap the Game with the constructed house for this player
        GameConfiguration gameConfiguration = new BaseGameConfigurationImpl( initFacts, "" );

        game.bootstrap( gameConfiguration );

        PlayerConfiguration playerConfiguration = new BasePlayerConfigurationImpl( null );

        game.join( player, playerConfiguration );

        game.execute( new PickFlagCommand( player, flag ) );

        game.execute( new EnterZoneCommand( player, chasm ) );

        assertEquals( 0, player.getInventory().getItems().size() );

        assertTrue( chasm.getPlayersInZone().isEmpty() );

        List<GameMessage> messages = game.getAllMessages( player.getName() );
        messages.addAll( game.getAllMessages( "system" ) );

        assertEquals( 6, messages.size() );

        Set<String> messageTexts = messages.stream().map( m -> m.getText() ).collect( Collectors.toSet() );

        String assignedTeam = "";
        if ( blueTeam.getPlayersInTeam().contains( player.getName() ) ) {
            assignedTeam = blueTeam.getName();
        } else if ( redTeam.getPlayersInTeam().contains( player.getName() ) ) {
            assignedTeam = redTeam.getName();
        }
        assertTrue( assignedTeam != null && !assignedTeam.isEmpty() );

        assertThat( messageTexts,
                Matchers.containsInAnyOrder( "Player: salaboy assigned to team: " + assignedTeam,
                        "New Team Resource Created for team: red",
                        "New Team Resource Created for team: blue",
                        "You entered the Zone: Chasm",
                        "Picked the Flag",
                        "Player (salaboy) with the Flag in the Chasm! , Respawing in: NamedLocation{name=" + assignedTeam + "}" ) );

        Queue<Command> callbacks = game.getCallbacks();
        assertEquals( 8, callbacks.size() );
        // TODO assert each callback in order with callbacks poll. Assert Type and Arguments of each callback
        game.drop( player );

        game.destroy();
    }

    @Test
    public void pickTheFlagAndScoreTest() {
        Player player = new BasePlayerImpl( "salaboy" );

        List initFacts = new ArrayList();
        Chest chest = new Chest( "Flag Chest", new Location( 0, 0, 0 ) );
        initFacts.add( chest );
        Team redTeam = new Team( "red" );
        initFacts.add( redTeam );
        Zone scoreZoneRed = new Zone( "red" );
        initFacts.add( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( "red" );
        initFacts.add( redSpawn );
        Team blueTeam = new Team( "blue" );
        initFacts.add( blueTeam );
        Zone scoreZoneBlue = new Zone( "blue" );
        initFacts.add( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( "blue" );
        initFacts.add( blueSpawn );
        Flag flag = new Flag( "Flag", "banner" );
        initFacts.add( flag );
        Zone chasm = new Zone( "Chasm" );
        initFacts.add( chasm );
        // Bootstrap the Game with the constructed house for this player
        GameConfiguration gameConfiguration = new BaseGameConfigurationImpl( initFacts, "" );

        game.bootstrap( gameConfiguration );

        PlayerConfiguration playerConfiguration = new BasePlayerConfigurationImpl( null );

        game.join( player, playerConfiguration );

        game.execute( new PickFlagCommand( player, flag ) );

        String assignedTeam = null;
        String enemyTeam = null;
        if ( blueTeam.getPlayersInTeam().contains( player.getName() ) ) {
            assignedTeam = blueTeam.getName();
            enemyTeam = redTeam.getName();
        } else if ( redTeam.getPlayersInTeam().contains( player.getName() ) ) {
            assignedTeam = redTeam.getName();
            enemyTeam = blueTeam.getName();
        }
        assertTrue( assignedTeam != null && !assignedTeam.isEmpty() );
        assertTrue( enemyTeam != null && !enemyTeam.isEmpty() );
        // Select the enemy score Zone
        Zone selectedScoreZone = null;
        if ( assignedTeam.equals( "red" ) ) {
            selectedScoreZone = scoreZoneBlue;
        } else if ( assignedTeam.equals( "blue" ) ) {
            selectedScoreZone = scoreZoneRed;
        }
        Assert.assertNotNull( selectedScoreZone );
        game.execute( new EnterZoneCommand( player, selectedScoreZone ) );

        assertEquals( 0, player.getInventory().getItems().size() );
        
        assertTrue( selectedScoreZone.getPlayersInZone().isEmpty() );

        List<GameMessage> messages = game.getAllMessages( player.getName() );
        messages.addAll( game.getAllMessages( "system" ) );

        assertEquals( 6, messages.size() );

        Set<String> messageTexts = messages.stream().map( m -> m.getText() ).collect( Collectors.toSet() );

        assertThat( messageTexts,
                Matchers.containsInAnyOrder( "Player: salaboy assigned to team: " + assignedTeam,
                        "New Team Resource Created for team: red",
                        "New Team Resource Created for team: blue",
                        "You entered the Zone: " + enemyTeam,
                        "Picked the Flag",
                        "Score! Player: salaboy just enter the " + enemyTeam + " Score Zone ( Team " + assignedTeam + " - score: 1 )" ) );

        Queue<Command> callbacks = game.getCallbacks();
        assertEquals( 4, callbacks.size() );
        // TODO assert each callback in order with callbacks poll. Assert Type and Arguments of each callback

        game.drop( player );

        game.destroy();
    }

}

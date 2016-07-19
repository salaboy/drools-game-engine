
package org.drools.game.capture.flag.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.inject.Inject;
import org.drools.game.capture.flag.cmds.CommandRegistry;
import org.drools.game.capture.flag.model.Chest;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.capture.flag.model.Location;
import org.drools.game.capture.flag.model.NamedLocation;
import org.drools.game.capture.flag.model.Team;
import org.drools.game.capture.flag.model.Zone;
import org.drools.game.core.GameCallbackServiceImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.Player;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 *
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class GameRulesTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addClass( GameMessageService.class )
                .addClass( GameMessageServiceImpl.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @BeforeClass
    public static void setup() {
        CommandRegistry.set( "TELEPORT_CALLBACK", "org.drools.game.capture.flag.tests.cmds.TeleportPlayerCommand" );
        CommandRegistry.set( "CLEAR_INVENTORY_CALLBACK", "org.drools.game.capture.flag.tests.cmds.ClearPlayerInventoryCommand" );
        CommandRegistry.set( "NOTIFY_VIA_CHAT_CALLBACK", "org.drools.game.capture.flag.tests.cmds.NotifyViaChatCommand" );
        CommandRegistry.set( "NOTIFY_ALL_VIA_CHAT_CALLBACK", "org.drools.game.capture.flag.tests.cmds.NotifyViaChatCommand" );
        CommandRegistry.set( "RESET_FLAG_CALLBACK", "org.drools.game.capture.flag.tests.cmds.ResetFlagCommand" );
        CommandRegistry.set( "SET_PLAYER_HEALTH_CALLBACK", "org.drools.game.capture.flag.tests.cmds.SetPlayerHealthCommand" );
        CommandRegistry.set( "SET_PLAYER_PARAM_CALLBACK", "org.drools.game.capture.flag.tests.cmds.SetPlayerParamCommand" );
    }

    @Inject
    @KBase( "gameKBase" )
    private KieBase kBase;

    /*
    * This test checks that the player is teleported back to the spawn location of its team
    * when it hits the chasm. If the player has the Flag, the flag needs to return to the chest. 
     */
    @Test
    public void playerHitTheChasmTest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        kSession.setGlobal( "callback", new GameCallbackServiceImpl() );

        // Init Player
        Player salaboy = new BasePlayerImpl( "salaboy" );
        FactHandle playerFH = kSession.insert( salaboy );

        Chest chest = new Chest( "Flag Chest", new Location( 0, 0, 0 ) );
        kSession.insert( chest );

        // Red team
        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Zone scoreZoneRed = new Zone( "red" );
        kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( "red" );
        kSession.insert( redSpawn );

        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Zone scoreZoneBlue = new Zone( "blue" );
        kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( "blue" );
        kSession.insert( blueSpawn );

        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );

        Zone chasm = new Zone( "Chasm" );
        FactHandle chasmFH = kSession.insert( chasm );

        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player picks the flag
        salaboy.getInventory().getItems().add( flag );
        kSession.update( playerFH, salaboy );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player moves to the Chasm/. and needs to be teleported back
        chasm.addPlayer( salaboy.getName() );
        kSession.update( chasmFH, chasm );

        fired = kSession.fireAllRules();
        assertEquals( 1, fired );

        assertEquals( 0, salaboy.getInventory().getItems().size() );

        assertTrue( chasm.getPlayersInZone().isEmpty() );

        kSession.dispose();
    }

    /*
     * 
     */
    @Test
    public void playerScoreTest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        kSession.setGlobal( "callback", new GameCallbackServiceImpl() );

        // Init Player
        Player salaboy = new BasePlayerImpl( "salaboy" );
        FactHandle playerFH = kSession.insert( salaboy );

        Chest chest = new Chest( "Flag Chest", new Location( 0, 0, 0 ) );
        kSession.insert( chest );

        // Red team
        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Zone scoreZoneRed = new Zone( "red" );
        FactHandle redScoreZoneFH = kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( "red" );
        kSession.insert( redSpawn );

        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Zone scoreZoneBlue = new Zone( "blue" );
        FactHandle blueScoreZoneFH = kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( "blue" );
        kSession.insert( blueSpawn );

        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );

        Zone chasm = new Zone( "Chasm" );
        FactHandle chasmFH = kSession.insert( chasm );

        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player picks the flag
        salaboy.getInventory().getItems().add( flag );
        kSession.update( playerFH, salaboy );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        String assignedTeam = null;
        String enemyTeam = null;
        if ( blueTeam.getPlayersInTeam().contains( salaboy.getName() ) ) {
            assignedTeam = blueTeam.getName();
            enemyTeam = redTeam.getName();
        } else if ( redTeam.getPlayersInTeam().contains( salaboy.getName() ) ) {
            assignedTeam = redTeam.getName();
            enemyTeam = blueTeam.getName();
        }
        assertTrue( assignedTeam != null && !assignedTeam.isEmpty() );
        assertTrue( enemyTeam != null && !enemyTeam.isEmpty() );
        // Select the enemy score Zone
        Zone selectedScoreZone = null;
        FactHandle selectedZoneFH = null;
        if ( assignedTeam.equals( "red" ) ) {
            selectedScoreZone = scoreZoneBlue;
            selectedZoneFH = blueScoreZoneFH;

        } else if ( assignedTeam.equals( "blue" ) ) {
            selectedScoreZone = scoreZoneRed;
            selectedZoneFH = redScoreZoneFH;
        }
        assertNotNull( selectedScoreZone );
        assertNotNull( selectedZoneFH );

        // Player moves to the Score Zone and needs to be teleported back
        selectedScoreZone.addPlayer( salaboy.getName() );
        kSession.update( selectedZoneFH, selectedScoreZone );

        fired = kSession.fireAllRules();
        assertEquals( 1, fired );

        assertEquals( 0, salaboy.getInventory().getItems().size() );

        assertTrue( scoreZoneRed.getPlayersInZone().isEmpty() );

        kSession.dispose();
    }

    /*
   
     */
    @Test
    public void gameTeamAssignmentTest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        kSession.setGlobal( "callback", new GameCallbackServiceImpl() );

        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );

        NamedLocation redTeamSpawn = new NamedLocation( "red" );
        kSession.insert( redTeamSpawn );
        NamedLocation blueTeamSpawn = new NamedLocation( "blue" );
        kSession.insert( blueTeamSpawn );
        Zone redScoreZone = new Zone( "red" );
        kSession.insert( redScoreZone );
        Zone blueScoreZone = new Zone( "blue" );
        kSession.insert( blueScoreZone );

        Player salaboy = new BasePlayerImpl( "salaboy" );

        kSession.insert( salaboy );
        int fired = kSession.fireAllRules();
        // Two for assignments and two for TeamBundle creation, required for spawning
        assertEquals( 4, fired );

        assertTrue( ( !blueTeam.getPlayersInTeam().isEmpty() )
                || ( !redTeam.getPlayersInTeam().isEmpty() ) );

        Player sam = new BasePlayerImpl( "Sam" );
        kSession.insert( sam );

        fired = kSession.fireAllRules();
        assertEquals( 2, fired );
        assertTrue( ( !blueTeam.getPlayersInTeam().isEmpty() )
                && ( !redTeam.getPlayersInTeam().isEmpty() ) );

        Player barney = new BasePlayerImpl( "barney" );
        kSession.insert( barney );

        fired = kSession.fireAllRules();
        assertEquals( 2, fired );

        Player pikachu = new BasePlayerImpl( "pikachu" );
        kSession.insert( pikachu );

        fired = kSession.fireAllRules();
        assertEquals( 2, fired );

        assertTrue( blueTeam.getPlayersInTeam().size() == redTeam.getPlayersInTeam().size() );

    }

}


package org.drools.game.capture.flag.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.inject.Inject;
import org.drools.game.capture.flag.model.Chest;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.capture.flag.model.Location;
import org.drools.game.capture.flag.model.NamedLocation;
import org.drools.game.capture.flag.model.Room;
import org.drools.game.capture.flag.model.Team;
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
        Room scoreZoneRed = new Room( 0, 0, 0, 0, 0, 0, "red" );
        kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( 0, 0, 0, "red" );
        kSession.insert( redSpawn );

        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Room scoreZoneBlue = new Room( 0, 0, 0, 0, 0, 0, "blue" );
        kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( 0, 0, 0, "blue" );
        kSession.insert( blueSpawn );

        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );

        Room chasm = new Room( 0, 0, 0, 0, 0, 0, "Chasm" );
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
        Room scoreZoneRed = new Room(  "red" );
        FactHandle redScoreZoneFH = kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( "red" );
        kSession.insert( redSpawn );

        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Room scoreZoneBlue = new Room(  "blue" );
        kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation(  "blue" );
        kSession.insert( blueSpawn );

        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );

        Room chasm = new Room(  "Chasm" );
        FactHandle chasmFH = kSession.insert( chasm );

        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player picks the flag
        salaboy.getInventory().getItems().add( flag );
        kSession.update( playerFH, salaboy );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player moves to the Score Zone and needs to be teleported back
        scoreZoneRed.addPlayer( salaboy.getName() );
        kSession.update( redScoreZoneFH, scoreZoneRed );

        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

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
        NamedLocation blueTeamSpawn = new NamedLocation(  "blue" );
        kSession.insert( blueTeamSpawn );
        Room redScoreZone = new Room( "red" );
        kSession.insert( redScoreZone );
        Room blueScoreZone = new Room( "blue" );
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

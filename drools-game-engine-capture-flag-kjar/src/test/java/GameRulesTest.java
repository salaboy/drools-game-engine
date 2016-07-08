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

    // Original Bootstrap code
//       Chest chest = new Chest( "Flag Chest", new Location( 182, 94, -276 ) );
//        kSession.insert( chest );
////        cmds.notifyGenerateChest(chest);
//        Flag flag = new Flag( "Flag", "banner" );
////	cmds.notifyItemToChest(chest, flag);
//
//        Room scorezone_red = new Room( 155, 94, -280, 151, 99, -272, "Scorezone_Red" );
//        kSession.insert( scorezone_red );
//
//        Room scorezone_blue = new Room( 209, 94, -272, 213, 99, -280, "Scorezone_Blue" );
//        kSession.insert( scorezone_blue );
//
//        kSession.insert( new NamedLocation( 153, 98, -275, "Redspawn" ) );
//        kSession.insert( new NamedLocation( 211, 97, -275, "Bluespawn" ) );
//
//        Room chasm = new Room( 141, 80, -310, 260, 62, -199, "Chasm" );
//        kSession.insert( chasm );
//
//        kSession.insert( new Team( "red" ) );
//        kSession.insert( new Team( "blue" ) );
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

        // Init Player
        Player salaboy = new BasePlayerImpl( "salaboy" );
        FactHandle playerFH = kSession.insert( salaboy );

        
        
        Chest chest = new Chest( "Flag Chest", new Location( 182, 94, -276 ) );
        kSession.insert( chest );
        
        // Red team
        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Room scoreZoneRed = new Room( 155, 94, -280, 151, 99, -272, "red" );
        kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( 153, 98, -275, "red" );
        kSession.insert( redSpawn );
        
        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Room scoreZoneBlue = new Room( 209, 94, -272, 213, 99, -280, "blue" );
        kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( 211, 97, -275, "blue" );
        kSession.insert( blueSpawn );
       
        
        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );
        
        Room chasm = new Room( 141, 80, -310, 260, 62, -199, "Chasm" );
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
        assertEquals( 1, fired);

        
        kSession.dispose();
    }

    /*
     * 
    */
    
    @Test
    public void playerScoreTest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );

        // Init Player
        Player salaboy = new BasePlayerImpl( "salaboy" );
        FactHandle playerFH = kSession.insert( salaboy );

        
        
        Chest chest = new Chest( "Flag Chest", new Location( 182, 94, -276 ) );
        kSession.insert( chest );
        
        // Red team
        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Room scoreZoneRed = new Room( 155, 94, -280, 151, 99, -272, "red" );
        FactHandle redScoreZoneFH = kSession.insert( scoreZoneRed );
        NamedLocation redSpawn = new NamedLocation( 153, 98, -275, "red" );
        kSession.insert( redSpawn );
        
        // Blue team
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        Room scoreZoneBlue = new Room( 209, 94, -272, 213, 99, -280, "blue" );
        kSession.insert( scoreZoneBlue );
        NamedLocation blueSpawn = new NamedLocation( 211, 97, -275, "blue" );
        kSession.insert( blueSpawn );
       
        
        int fired = kSession.fireAllRules();
        assertEquals( 4, fired );
        
        // Make the flag appear in the world
        Flag flag = new Flag( "Flag", "banner" );
        kSession.insert( flag );
        
        Room chasm = new Room( 141, 80, -310, 260, 62, -199, "Chasm" );
        FactHandle chasmFH = kSession.insert( chasm );
        
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Player picks the flag
        salaboy.getInventory().getItems().add( flag );
        kSession.update( playerFH, salaboy );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );
        
        // Player moves to the Chasm/. and needs to be teleported back
        scoreZoneRed.addPlayer( salaboy.getName() );
        kSession.update( redScoreZoneFH, scoreZoneRed );
        
        fired = kSession.fireAllRules();
        assertEquals( 1, fired);

        
        kSession.dispose();
    }
    
    /*
   
     */
    @Test
    public void gameTeamAssignmentTest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );
        Team redTeam = new Team( "red" );
        kSession.insert( redTeam );
        Team blueTeam = new Team( "blue" );
        kSession.insert( blueTeam );
        
        Player salaboy = new BasePlayerImpl( "salaboy" );
        
        kSession.insert( salaboy );
        int fired = kSession.fireAllRules();
        
        assertEquals( 2, fired );
        
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


package org.drools.game.horserace.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.inject.Inject;
import org.drools.game.core.GameCallbackServiceImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.horserace.model.Room;
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

@RunWith( Arquillian.class )
public class GameRulesTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                //.addClass( GameMessageService.class )
                //.addClass( GameMessageServiceImpl.class )
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
    public void mytest() {
        KieSession kSession = kBase.newKieSession();
        assertNotNull( kSession );
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        kSession.setGlobal( "callback", new GameCallbackServiceImpl() );
        
        Player player = new BasePlayerImpl("Morton Abenthy Halputz");
        FactHandle playerFH = kSession.insert(player);
        
        Room startfinish = new Room(-9, 89, 189, -2, 95, 184, "StartFinish");
        FactHandle startfinishFH = kSession.insert(startfinish);
        
        Room checkpointone = new Room(32, 89, 184, 37, 100, 190, "CheckPointOne");
        FactHandle checkpointoneFH = kSession.insert(checkpointone);

        Room checkpointtwo = new Room(32, 89, 159, 38, 100, 154, "CheckPointTwo");
        FactHandle checkpointtwoFH = kSession.insert(checkpointtwo);

        Room checkpointthree = new Room(-21, 89, 159, -26, 100, 153, "CheckPointThree");
        FactHandle checkpointthreeFH = kSession.insert(checkpointthree);

        Room checkpointfour = new Room(-21, 89, 184, -27, 100, 189, "CheckPointFour");
        FactHandle checkpointfourFH = kSession.insert(checkpointfour);
        
        int fired = kSession.fireAllRules();
        assertEquals( 2, fired );
        
        startfinish.addPlayer( player.getName() );
        kSession.update( startfinishFH, startfinish );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );
        
        startfinish.removePlayer( player.getName() );
        kSession.update( startfinishFH, startfinish );
        checkpointone.addPlayer( player.getName() );
        kSession.update( checkpointoneFH, checkpointone );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointone.removePlayer( player.getName() );
        kSession.update( checkpointoneFH, checkpointone );
        checkpointtwo.addPlayer( player.getName() );
        kSession.update( checkpointtwoFH, checkpointtwo );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointtwo.removePlayer( player.getName() );
        kSession.update( checkpointtwoFH, checkpointtwo );
        checkpointthree.addPlayer( player.getName() );
        kSession.update( checkpointthreeFH, checkpointthree );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );

        checkpointthree.removePlayer( player.getName() );
        kSession.update( checkpointthreeFH, checkpointthree );
        checkpointfour.addPlayer( player.getName() );
        kSession.update( checkpointfourFH, checkpointfour );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointfour.removePlayer( player.getName() );
        kSession.update( checkpointfourFH, checkpointfour );
        startfinish.addPlayer( player.getName() );
        kSession.update( startfinishFH, startfinish );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );

        kSession.dispose();
    }
}

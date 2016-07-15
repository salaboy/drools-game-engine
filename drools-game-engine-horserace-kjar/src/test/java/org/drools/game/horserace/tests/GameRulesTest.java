
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
import org.drools.game.horserace.model.Checkpoint;
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
        
        Checkpoint startfinish = new Checkpoint("StartFinish", null);
        FactHandle startfinishFH = kSession.insert(startfinish);
        
        Checkpoint checkpointone = new Checkpoint("CheckPointOne", startfinish);
        FactHandle checkpointoneFH = kSession.insert(checkpointone);

        Checkpoint checkpointtwo = new Checkpoint("CheckPointTwo", checkpointone);
        FactHandle checkpointtwoFH = kSession.insert(checkpointtwo);

        Checkpoint checkpointthree = new Checkpoint("CheckPointThree", checkpointtwo);
        FactHandle checkpointthreeFH = kSession.insert(checkpointthree);

        Checkpoint checkpointfour = new Checkpoint("CheckPointFour", checkpointthree);
        FactHandle checkpointfourFH = kSession.insert(checkpointfour);
        
        startfinish.setRequired(checkpointfour);
        
        int fired = kSession.fireAllRules();
        assertEquals( 2, fired );
        
        startfinish.addPlayer( player.getName() );
        kSession.update( startfinishFH, startfinish );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointone.addPlayer( player.getName() );
        kSession.update( checkpointoneFH, checkpointone );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointtwo.addPlayer( player.getName() );
        kSession.update( checkpointtwoFH, checkpointtwo );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        checkpointthree.addPlayer( player.getName() );
        kSession.update( checkpointthreeFH, checkpointthree );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );

        checkpointfour.addPlayer( player.getName() );
        kSession.update( checkpointfourFH, checkpointfour );
        fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        
        startfinish.addPlayer( player.getName() );
        kSession.update( startfinishFH, startfinish );
        fired = kSession.fireAllRules();
        assertEquals( 2, fired );

        kSession.dispose();
    }
}

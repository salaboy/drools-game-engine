package org.drools.game.services.endpoint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.drools.game.capture.flag.cmds.EnterZoneCommand;
import org.drools.game.capture.flag.cmds.PickFlagCommand;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.capture.flag.model.Zone;
import org.drools.game.core.api.StoreFacts;
import org.drools.game.model.api.Player;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.endpoint.impl.CommandRest;
import org.drools.game.services.endpoint.impl.GameServiceImpl;
import org.drools.game.services.infos.GameSessionInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

@RunWith( Arquillian.class )
public class CaptureTheFlagTest {

	private Client client;
    private ResteasyWebTarget target;
    private static final String APP_URL = "http://localhost:8080/";
    
    @BeforeClass
    public static void setupCmds() {
    	// FIXME Relocated temporarily on StoreFactsTestImp
    	
//        CommandRegistry.set( "TELEPORT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.TeleportPlayerCommand" );
//        CommandRegistry.set( "CLEAR_INVENTORY_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ClearPlayerInventoryCommand" );
//        CommandRegistry.set( "NOTIFY_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyViaChatCommand" );
//        CommandRegistry.set( "NOTIFY_ALL_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyAllViaChatCommand" );
//        CommandRegistry.set( "RESET_FLAG_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ResetFlagCommand" );
//        CommandRegistry.set( "SET_PLAYER_HEALTH_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerHealthCommand" );
//        CommandRegistry.set( "SET_PLAYER_PARAM_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerParamCommand" );
    }
    
    @Before
    public void setup() throws Exception {
        client = ClientBuilder.newBuilder().build();
        final WebTarget webtarget = client.target( APP_URL );
        target = ( ResteasyWebTarget ) webtarget;
        
    }
    
    @After
    public void tearDown() throws Exception {
        client.close();
    }
    
    @Deployment(testable=false)     
    public static Archive createDeployment() throws IllegalArgumentException, Exception {
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
        deployment.addPackages( true, "com.google.common" );
        deployment.addPackage( "org.drools.game.services.endpoint.test.cmds" );
        deployment.addClass( GameService.class );
        deployment.addClass( GameServiceImpl.class );
        deployment.addClass( GameSessionInfo.class );
        deployment.addClass( StoreFacts.class );
        deployment.addClass( StoreFactsTestImp.class );
        deployment.addClass( CommandRest.class );
        deployment.addClass( Player.class );
        
        deployment.addAllDependencies()
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        
        return deployment;
    }
    
	@Test
	public void pickTheFlagHitTheChasmTest() {
		
		GameService game = target.proxy(GameService.class);

		String sessionId = game.newGameSession();
		String playerName = "salaboy";
        Player player = new BasePlayerImpl( playerName );

        game.joinGameSession(sessionId, player );

        Flag flag = new Flag( "Flag", "banner" );
        Zone chasm = new Zone( "Chasm" );
  
        game.execute(sessionId, new CommandRest( new PickFlagCommand( player, flag )));
        game.execute(sessionId, new CommandRest( new EnterZoneCommand( player, chasm )));
        
        player = game.getPlayer(sessionId, playerName);
        assertEquals( 0, player.getInventory().getItems().size() );

        assertTrue( game.getItem( sessionId, chasm.getName() ).getPlayersInZone().isEmpty() );


       
        //TODO ....  

	}	
	
	
}

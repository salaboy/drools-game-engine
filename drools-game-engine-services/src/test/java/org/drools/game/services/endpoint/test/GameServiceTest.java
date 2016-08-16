
package org.drools.game.services.endpoint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.drools.game.capture.flag.cmds.CommandRegistry;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.endpoint.impl.GameServiceImpl;
import org.drools.game.services.infos.GameSessionInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
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
public class GameServiceTest {
    
    private Client client;
    private ResteasyWebTarget target;
    private static final String PATH = "game";
    private static final String APP_URL = "http://localhost:8080/";
    
    @BeforeClass
    public static void setupCmds() {
        CommandRegistry.set( "TELEPORT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.TeleportPlayerCommand" );
        CommandRegistry.set( "CLEAR_INVENTORY_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ClearPlayerInventoryCommand" );
        CommandRegistry.set( "NOTIFY_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyViaChatCommand" );
        CommandRegistry.set( "NOTIFY_ALL_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyAllViaChatCommand" );
        CommandRegistry.set( "RESET_FLAG_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ResetFlagCommand" );
        CommandRegistry.set( "SET_PLAYER_HEALTH_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerHealthCommand" );
        CommandRegistry.set( "SET_PLAYER_PARAM_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerParamCommand" );
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
    
    @Deployment
    public static Archive createDeployment() throws IllegalArgumentException, Exception {
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
        deployment.addPackages( true, "com.google.common" );
        deployment.addClass( GameService.class );
        deployment.addClass( GameServiceImpl.class );
        deployment.addClass( GameSessionInfo.class );
        
        deployment.addAllDependencies()
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        
        return deployment;
    }
    
	@Test
	@RunAsClient
	public void newGameSession() {
		GameService proxy = target.proxy(GameService.class);
		String playerName = "salaboy";

		final int numberSessions = proxy.getAllGameSessions().size();

		String id = proxy.newGameSession();
		assertNotNull(id);
		List<GameSessionInfo> allGameSessions = proxy.getAllGameSessions();
		assertEquals(1 + numberSessions, allGameSessions.size());
		GameSessionInfo sessionInfo = filterSessionsById(id, allGameSessions);
		assertEquals(id, sessionInfo.getId());
		assertTrue(sessionInfo.getPlayers().isEmpty());

		proxy.joinGameSession(id, new BasePlayerImpl(playerName));

		allGameSessions = proxy.getAllGameSessions();
		assertEquals(1, allGameSessions.size());
		sessionInfo = allGameSessions.get(0);
		assertEquals(id, sessionInfo.getId());
		assertTrue(sessionInfo.getPlayers().contains(playerName));

	}

	@Test
	@RunAsClient
	public void dropUserFromSession() {
		GameService proxy = target.proxy(GameService.class);
		String playerName = "salaboy";

		String id = proxy.newGameSession();
		proxy.joinGameSession(id, new BasePlayerImpl(playerName));

		List<GameSessionInfo> allGameSessions = proxy.getAllGameSessions();
		GameSessionInfo sessionInfo = filterSessionsById(id, allGameSessions);
		
		assertEquals(1, sessionInfo.getPlayers().size());

		proxy.drop(id, playerName);

		allGameSessions = proxy.getAllGameSessions();
	
		sessionInfo = filterSessionsById(id, allGameSessions);
		assertEquals(0, sessionInfo.getPlayers().size());

	}

	@Test
	@RunAsClient
	public void destroySession() {

		GameService proxy = target.proxy(GameService.class);
		String playerName = "salaboy";

		String id = proxy.newGameSession();
		proxy.joinGameSession(id, new BasePlayerImpl(playerName));
		proxy.drop(id, playerName);
		proxy.destroy(id);
		
	}

	private GameSessionInfo filterSessionsById(String id, List<GameSessionInfo> allGameSessions) {
		for (GameSessionInfo gameSessionInfo : allGameSessions) {
			if (gameSessionInfo.getId().equals(id)) {
				return gameSessionInfo;
			}
		}
		return null;
	}

}
package com.drools.game.services.endpoint.test;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.drools.game.services.infos.GameSessionInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GameServiceTest 
{

	private Client client;
	
	@Before
	public void setup() throws Exception {
		client = ClientBuilder.newClient();

	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    

	@Test
	public void getAllGameSessions() {
		WebTarget target = client.target("http://localhost:8080/api/game");

		List<GameSessionInfo> sessions = target.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<GameSessionInfo>>() {
				});
		org.junit.Assert.assertEquals(0, sessions.size());
	}

}

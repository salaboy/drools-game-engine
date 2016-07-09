package com.drools.game.services.endpoint.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.drools.game.core.api.GameConfiguration;
import org.drools.game.model.api.Player;
import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.endpoint.impl.GameServiceImpl;
import org.drools.game.services.infos.GameSessionInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

@RunWith(Arquillian.class)
public class GameServiceTest {

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
	public static Archive createDeployment() {
		JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
		deployment.setContextRoot("/api");
		deployment.addClass(GameService.class).addClass(GameServiceImpl.class).addClass(GameSessionInfo.class)
				.addPackage(GameConfiguration.class.getPackage()).addPackage(Player.class.getPackage());
		return deployment;
	}

	@Test
	public void checkService() {
		WebTarget target = client.target("http://localhost:8080/api/game");
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

}

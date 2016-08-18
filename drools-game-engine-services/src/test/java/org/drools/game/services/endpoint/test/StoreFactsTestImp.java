package org.drools.game.services.endpoint.test;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.drools.game.capture.flag.cmds.CommandRegistry;
import org.drools.game.capture.flag.model.Chest;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.capture.flag.model.Location;
import org.drools.game.capture.flag.model.NamedLocation;
import org.drools.game.capture.flag.model.Team;
import org.drools.game.capture.flag.model.Zone;
import org.drools.game.core.api.StoreFacts;
import org.drools.game.model.api.Player;
import org.drools.game.model.impl.base.BasePlayerImpl;


@ApplicationScoped
public class StoreFactsTestImp implements StoreFacts{
	
	
	public StoreFactsTestImp() {
        CommandRegistry.set( "TELEPORT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.TeleportPlayerCommand" );
        CommandRegistry.set( "CLEAR_INVENTORY_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ClearPlayerInventoryCommand" );
        CommandRegistry.set( "NOTIFY_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyViaChatCommand" );
        CommandRegistry.set( "NOTIFY_ALL_VIA_CHAT_CALLBACK", "org.drools.game.services.endpoint.test.cmds.NotifyAllViaChatCommand" );
        CommandRegistry.set( "RESET_FLAG_CALLBACK", "org.drools.game.services.endpoint.test.cmds.ResetFlagCommand" );
        CommandRegistry.set( "SET_PLAYER_HEALTH_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerHealthCommand" );
        CommandRegistry.set( "SET_PLAYER_PARAM_CALLBACK", "org.drools.game.services.endpoint.test.cmds.SetPlayerParamCommand" );
	}
	
	
	@Override
	public List getInitialFacts() {
		
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
        Zone speedPowerup = new Zone( "SpeedPowerup" );
        initFacts.add( speedPowerup );
        Zone jumpPowerup = new Zone( "JumpPowerup" );
        initFacts.add( jumpPowerup );
        
        return initFacts;

	}

}

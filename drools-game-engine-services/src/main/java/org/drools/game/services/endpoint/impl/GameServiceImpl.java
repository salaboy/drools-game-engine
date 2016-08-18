/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.game.services.endpoint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.game.capture.flag.model.Zone;
import org.drools.game.core.BaseGameConfigurationImpl;
import org.drools.game.core.BasePlayerConfigurationImpl;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameSession;
import org.drools.game.core.api.StoreFacts;
import org.drools.game.model.api.Player;
import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.infos.GameSessionInfo;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;

@ApplicationScoped
public class GameServiceImpl implements GameService {

    @Inject
    private Instance<GameSession> sessions;
    
    @Inject
    private Instance<StoreFacts> storeFacts;
    
    private Map<String, GameSession> games = new HashMap<String, GameSession>();

    @Override
    public String newGameSession( ) {
        GameSession gameSession = sessions.get();
        List initialData = storeFacts.get().getInitialFacts(); // Get the initial facts from store
        gameSession.bootstrap( new BaseGameConfigurationImpl( initialData, "" ) );
        String id = UUID.randomUUID().toString().substring( 0, 6 );
        games.put( id, gameSession );
        return id;
    }

    @Override
    public void joinGameSession( String sessionId, Player p ) {
        GameSession gameSession = games.get( sessionId );
        List initialData = storeFacts.get().getInitialFacts(); // Get the initial facts from store
        gameSession.join( p, new BasePlayerConfigurationImpl( initialData ) );
    }

    @Override
    public List<GameSessionInfo> getAllGameSessions() {
        List<GameSessionInfo> infos = new ArrayList<>();
        for ( String id : games.keySet() ) {
            infos.add( new GameSessionInfo( id, games.get( id ).getPlayers() ) );
        }
        return infos;
    }
    
   
    @Override
    public void drop( String sessionId, String playerName ) {
        GameSession gameSession = games.get( sessionId );
        Player p = gameSession.getPlayerByName( playerName );
		gameSession.drop(p);
    }
    

    @Override
    public void destroy( String sessionId ) {
        GameSession gameSession = games.get( sessionId );        
		gameSession.destroy();
	}

	@Override
	public <T> void execute(String sessionId,  CommandRest<T> cmdRest ) {
        GameSession gameSession = games.get( sessionId );        
        gameSession.execute( cmdRest.getCommand() );
 	}

	@Override
	public Player getPlayer(String sessionId, String playerName){
		GameSession gameSession = games.get( sessionId );        
        Player p = gameSession.getPlayerByName( playerName );
        return p;
	}
	
	@Override
	public Zone getItem(String sessionId, String itemName){
		GameSession gameSession = games.get( sessionId );        
		return gameSession.execute(new Command<Zone>() {

			@Override
			public Zone execute(Context ctx) {
				KieSession session = ( KieSession ) ctx.getData().get( "session" );
				return (Zone) session.getObjects(new ObjectFilter() {
					public boolean accept(Object object) {
						if (object instanceof Zone) {
							if (itemName.equals(((Zone) object).getName())) {
								return true;
							}
						}
						return false;
					}
				}).iterator().next();		        
			}

			@Override
			public Player getPlayer() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		
	}
	
	
	
	
}

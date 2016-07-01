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
import org.drools.game.core.GameConfigurationImpl;
import org.drools.game.core.api.GameSession;
import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.infos.GameSessionInfo;

import org.drools.game.model.impl.base.PlayerImpl;

@ApplicationScoped
public class GameServiceImpl implements GameService {

    @Inject
    private Instance<GameSession> sessions;

    private Map<String, GameSession> games = new HashMap<String, GameSession>();

    @Override
    public String newGameSession( String playerId ) {
        GameSession gameSession = sessions.get();
        List initialData = new ArrayList(); // Get the initial facts from store
        gameSession.bootstrap( new PlayerImpl( playerId ), new GameConfigurationImpl(initialData, "") );
        String id = UUID.randomUUID().toString().substring( 0, 6 );
        games.put( id, gameSession );
        return id;
    }

    @Override
    public List<GameSessionInfo> getAllGameSessions() {
        List<GameSessionInfo> infos = new ArrayList<>();
        for ( String id : games.keySet() ) {
            infos.add( new GameSessionInfo( id, games.get( id ).getPlayer().getName() ) );
        }
        return infos;
    }

}

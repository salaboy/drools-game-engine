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

package org.drools.game.services.endpoint.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.drools.game.capture.flag.model.Zone;
import org.drools.game.model.api.Player;
import org.drools.game.services.endpoint.impl.CommandRest;
import org.drools.game.services.infos.GameSessionInfo;

@Path( "game" )
public interface GameService {

    @POST
    @Path( "" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    String newGameSession( );

    @GET
    @Path( "" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    List<GameSessionInfo> getAllGameSessions();

    @POST
    @Path( "{sessionId}" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    void joinGameSession( @PathParam( "sessionId" ) String sessionId, Player p );



    @DELETE
    @Path( "{sessionId}" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    void drop( @PathParam( "sessionId" ) String sessionId, String playerName );


    @DELETE
    @Path( "" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    void destroy( String sessionId );
    
    @POST
    @Path( "{sessionId}/execute" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    <T> void execute( @PathParam( "sessionId" ) String sessionId, CommandRest<T> cmdRest );

    
    @GET
    @Path(  "{sessionId}/player/{playerName}" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
	Player getPlayer( @PathParam( "sessionId" ) String sessionId,  @PathParam( "playerName" ) String playerName );

    @GET
    @Path(  "{sessionId}/item/{itemName}" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
	Zone getItem( @PathParam( "sessionId" ) String sessionId, @PathParam( "itemName" ) String itemName);


}

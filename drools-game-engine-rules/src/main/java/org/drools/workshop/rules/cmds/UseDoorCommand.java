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

package org.drools.workshop.rules.cmds;

import java.util.Iterator;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.rules.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class UseDoorCommand implements Command<Void> {

    private Player player;
    private Door door;
    private Room roomIn;

    public UseDoorCommand() {
    }

    public UseDoorCommand( Player player, Room room, Door door ) {
        this.player = player;
        this.door = door;
        this.roomIn = room;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        FactHandle roomInFH = session.getFactHandle( roomIn );
        roomIn.getPeopleInTheRoom().remove( player.getName() );
        session.update( roomInFH, roomIn );
        QueryResults queryResults = session.getQueryResults( "getRoomByName", door.getLeadsTo() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room roomTo = null;
        while ( iterator.hasNext() ) {
            roomTo = ( Room ) iterator.next().get( "$r" );
            break;
        }
        if ( roomTo != null ) {
            FactHandle roomToFH = session.getFactHandle( roomTo );
            roomTo.getPeopleInTheRoom().add( player.getName() );
            session.update( roomToFH, roomTo );
            session.insert( new GameMessage( "Player moved from  " + roomIn.getName() + " to " + roomTo.getName() ) );
        } else {
            session.insert( new GameMessage( "ERROR: Door cannot be used because the room:   " + door.getLeadsTo() + " was not found. " ) );
        }
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer( Player player ) {
        this.player = player;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor( Door door ) {
        this.door = door;
    }

    public Room getRoomIn() {
        return roomIn;
    }

    public void setRoomIn( Room roomIn ) {
        this.roomIn = roomIn;
    }

    @Override
    public String toString() {
        return "UseDoorCommand{" + "player=" + player + ", door=" + door + ", roomIn=" + roomIn + '}';
    }

}

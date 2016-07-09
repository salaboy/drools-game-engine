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

package org.drools.game.escape.room.cmds;

import java.util.Iterator;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.Player;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.Room;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class UseDoorCommand extends BaseCommand<Void> {

    private Door door;
    private Room roomIn;

    public UseDoorCommand( Player player, Room room, Door door ) {
        super( player );
        this.door = door;
        this.roomIn = room;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );
        FactHandle roomInFH = session.getFactHandle( roomIn );
        roomIn.getPeopleInTheRoom().remove( getPlayer().getName() );
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
            roomTo.getPeopleInTheRoom().add( getPlayer().getName() );
            session.update( roomToFH, roomTo );
            session.insert( messageService.newGameMessage( getPlayer().getName(), "Player moved from  " + roomIn.getName() + " to " + roomTo.getName() ) );
        } else {
            session.insert( messageService.newGameMessage( getPlayer().getName(), "ERROR: Door cannot be used because the room:   " + door.getLeadsTo() + " was not found. " ) );
        }
        return null;
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
        return "UseDoorCommand{" + "player=" + getPlayer() + ", door=" + door + ", roomIn=" + roomIn + '}';
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.escape.room.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.Player;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.Room;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 */
public class ExploreDoorsCommand extends BaseCommand<List<Door>> {

    private Room room;

    public ExploreDoorsCommand( Player player, Room room ) {
        super( player );
        this.room = room;
    }

    @Override
    public List<Door> execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );
        
        QueryResults queryResults = session.getQueryResults( "getDoors", room.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Door> doors = new ArrayList<Door>();
        while ( iterator.hasNext() ) {
            Door door = ( Door ) iterator.next().get( "$d" );
            doors.add( door );
        }
        session.insert( messageService.newGameMessage( getPlayer().getName(), doors.size() + " Door(s) unlocked and open" ) );
        return doors;
    }

    @Override
    public String toString() {
        return "ExploreDoorsCommand{" + "room=" + room + '}';
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom( Room room ) {
        this.room = room;
    }

}

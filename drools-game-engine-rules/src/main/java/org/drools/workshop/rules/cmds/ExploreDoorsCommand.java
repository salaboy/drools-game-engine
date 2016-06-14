/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.rules.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 */
public class ExploreDoorsCommand implements Command<List<Door>> {

    private Room room;

    public ExploreDoorsCommand() {
    }

    public ExploreDoorsCommand( Room room ) {
        this.room = room;
    }

    @Override
    public List<Door> execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        QueryResults queryResults = session.getQueryResults( "getDoors", room.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Door> doors = new ArrayList<Door>();
        while ( iterator.hasNext() ) {
            Door door = ( Door ) iterator.next().get( "$d" );
            doors.add( door );
        }
        session.insert( new GameMessage( doors.size() + " Doors unlocked and open" ) );
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

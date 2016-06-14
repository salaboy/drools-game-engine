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
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.items.Item;
import org.drools.workshop.rules.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 */
public class ExploreRoomCommand implements Command<List<Item>> {

    private Room room;

    public ExploreRoomCommand() {
    }

    public ExploreRoomCommand( Room room ) {
        this.room = room;
    }

    @Override
    public List<Item> execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        List<Item> items = new ArrayList<Item>();
        //First try at looking for the items, if there is no item it might be that it is dark
        QueryResults queryResults = session.getQueryResults( "getVisibleItems", room.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
        }
        queryResults = session.getQueryResults( "getDarkItems", room.getName() );
        iterator = queryResults.iterator();
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
        }
        session.insert( new GameMessage( items.size() + " Items available" ) );
        return items;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom( Room room ) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "ExploreRoomCommand{" + "room=" + room + '}';
    }

}

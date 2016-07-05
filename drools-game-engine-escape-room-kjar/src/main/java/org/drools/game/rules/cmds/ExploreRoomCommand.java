/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.rules.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.house.Room;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 */
public class ExploreRoomCommand extends BaseCommand<List<Item>> {

    private Room room;

    public ExploreRoomCommand( Player player, Room room ) {
        super( player );
        this.room = room;
    }

    @Override
    public List<Item> execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );

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
        session.insert( messageService.newGameMessage( getPlayer().getName(), items.size() + " Items available" ) );
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules.cmds;

import java.util.Iterator;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.api.Player;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.core.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 */
public class WhereAmICommand implements Command<Room> {

    private Player player;

    public WhereAmICommand() {
    }

    public WhereAmICommand( Player player ) {
        this.player = player;
    }

    @Override
    public Room execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );

        QueryResults queryResults = session.getQueryResults( "WhereAmI", player.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room room = null;
        while ( iterator.hasNext() ) {
            room = ( Room ) iterator.next().get( "$r" );
            break;
        }
        if ( room != null ) {
            session.insert( new GameMessage( "You are in " + room.getName() ) );
        }
        return room;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer( Player player ) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "WhereAmICommand{" + "player=" + player + '}';
    }

}

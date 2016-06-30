/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core.tests.cmds;

import java.util.List;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.Context;
import org.drools.game.model.house.Room;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.ItemContainer;
import org.drools.game.model.api.Player;

/**
 *
 * @author salaboy
 */
public class PickItemCommand implements Command<Void> {

    private final Item item;

    public PickItemCommand( Item item ) {
        this.item = item;
    }

    @Override
    public Void execute( Context ctx ) {
        Player player = ( Player ) ctx.getData().get( "player" );
        Room localRoom = ( Room ) ctx.getData().get( "room" );
        List<String> messages = ( List<String> ) ctx.getData().get( "messages" );
        boolean removed = localRoom.getItems().remove( item );
        if ( removed ) {
            player.getInventory().getItems().add( item );
            messages.add( "Item picked! " + item );
        } else {
            for ( Item i : localRoom.getItems() ) {
                if ( i instanceof ItemContainer ) {
                    removed = ( ( ItemContainer ) i ).getItems().remove( item );
                    if ( removed ) {
                        player.getInventory().getItems().add( item );
                        messages.add( "Item picked!" );
                        return null;
                    }
                }
            }
            messages.add( "Item not found!" );
        }
        return null;
    }

}

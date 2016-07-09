/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core.tests.cmds;

import java.util.List;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.Room;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;
import org.drools.game.escape.room.model.items.Key;

/**
 *
 * @author salaboy
 */
public class OpenDoorCommand extends BaseCommand<Void> {

    public OpenDoorCommand( Player player ) {
        super( player );
    }

    @Override
    public Void execute( Context ctx ) {
        Player player = ( Player ) ctx.getData().get( "player" );
        Room localRoom = ( Room ) ctx.getData().get( "room" );
        List<String> messages = ( List<String> ) ctx.getData().get( "messages" );
        List<Item> inventory = player.getInventory().getItems();
        List<Door> localDoors = localRoom.getDoors();
        for ( Item i : inventory ) {
            if ( i instanceof Key ) {
                for ( Door d : localDoors ) {
                    if ( d.getName().equals( ( ( Key ) i ).getName() ) ) {
                        d.setLocked( false );
                        d.setOpen( true );
                        messages.add( "Door Opened!" );
                        return null;
                    }

                }
            }
        }
        messages.add( "There is no door/key to open" );
        return null;
    }

}

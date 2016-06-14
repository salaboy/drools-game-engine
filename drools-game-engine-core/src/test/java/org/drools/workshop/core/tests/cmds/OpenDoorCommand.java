/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.core.tests.cmds;

import java.util.List;
import org.drools.workshop.core.Context;
import org.drools.workshop.core.Command;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.items.Item;
import org.drools.workshop.model.items.Key;

/**
 *
 * @author salaboy
 */
public class OpenDoorCommand implements Command<Void> {

    public OpenDoorCommand() {

    }

    @Override
    public Void execute( Context ctx ) {
        Player player = ( Player ) ctx.getData().get( "player" );
        Room localRoom = ( Room ) ctx.getData().get( "room" );
        List<String> messages = ( List<String> ) ctx.getData().get( "messages" );
        List<Item> inventory = player.getItems();
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

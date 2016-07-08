/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core.tests;

import java.util.ArrayList;
import java.util.List;
import org.drools.game.core.api.Context;
import org.drools.game.core.CommandExecutor;
import org.drools.game.core.ContextImpl;
import org.drools.game.core.tests.cmds.ExploreCommand;
import org.drools.game.core.tests.cmds.OpenDoorCommand;
import org.drools.game.core.tests.cmds.PickItemCommand;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.House;
import org.drools.game.escape.room.model.house.Room;
import org.drools.game.escape.room.model.items.Chest;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;
import org.drools.game.escape.room.model.items.Key;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class PlayersCommandsTest {

    @Test
    public void hello() {
        Player player = new BasePlayerImpl( "salaboy" );

        House house = new House( "my mansion" );

        /* Room A:  
        *  - 1 Door
        *  - 1 chest with a key 
         */
        Room roomA = new Room( "Room A" );
        List<Door> doorsA = new ArrayList<Door>();
        Door doorA = new Door( "Door A" );
        doorA.setLeadsTo( "Room B" );
        doorsA.add( doorA );
        roomA.setDoors( doorsA );

        List<Item> itemsRoomA = new ArrayList<Item>();
        Chest chestA = new Chest( "Chest A" );
        List<Item> itemsChestA = new ArrayList<Item>();
        Key keyA = new Key( "Door A" );
        itemsChestA.add( keyA );
        chestA.setItems( itemsChestA );
        itemsRoomA.add( chestA );

        roomA.setItems( itemsRoomA );

        List<Room> rooms = new ArrayList<Room>();
        rooms.add( roomA );
        house.setRooms( rooms );

        List<String> initPlayer = new ArrayList<String>();
        initPlayer.add( player.getName() );
        roomA.setPeopleInTheRoom( initPlayer );

        Assert.assertEquals( 1, house.getRooms().size() );
        Assert.assertTrue( roomA.getPeopleInTheRoom().contains( player.getName() ) );
        Assert.assertTrue( player.getInventory().getItems().isEmpty() );

        CommandExecutor executor = new CommandExecutor();
        List<String> messages = new ArrayList<String>();
        Context context = new ContextImpl();
        context.getData().put( "player", player );
        context.getData().put( "room", roomA );
        context.getData().put( "messages", messages );

        List<Item> items = executor.execute( new ExploreCommand( player ), context );
        assertEquals( 1, items.size() );
        Item item = items.get( 0 );
        assertTrue( item instanceof Chest );
        Chest chest = ( Chest ) item;
        assertEquals( 1, chest.getItems().size() );

        item = chest.getItems().get( 0 );
        assertTrue( item instanceof Key );
        Key key = ( Key ) item;

        executor.execute( new PickItemCommand( player, key ), context );

        assertTrue( messages.contains( "Item picked!" ) );

        executor.execute( new OpenDoorCommand( player ), context );

        assertTrue( messages.contains( "Door Opened!" ) );

    }
}

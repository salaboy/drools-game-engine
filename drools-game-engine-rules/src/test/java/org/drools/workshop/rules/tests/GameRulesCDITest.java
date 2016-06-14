/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules.tests;

import java.util.List;
import javax.inject.Inject;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.CommandExecutor;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.House;
import org.drools.workshop.model.house.Outside;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.items.Chest;
import org.drools.workshop.model.items.Item;
import org.drools.workshop.model.items.ItemContainer;
import org.drools.workshop.model.items.Key;
import org.drools.workshop.model.items.LightBulb;
import org.drools.workshop.model.items.LightSwitch;
import org.drools.workshop.model.items.MagicStone;
import org.drools.workshop.model.items.PickableItem;
import org.drools.workshop.model.items.ShineInTheDarkItem;
import org.drools.workshop.rules.GameMessage;
import org.drools.workshop.rules.GameSession;
import org.drools.workshop.rules.GameSessionImpl;
import org.drools.workshop.rules.cmds.ExploreDoorsCommand;
import org.drools.workshop.rules.cmds.ExploreRoomCommand;
import org.drools.workshop.rules.cmds.PickItemCommand;
import org.drools.workshop.rules.cmds.TurnOffTheLightsCommand;
import org.drools.workshop.rules.cmds.TurnOnTheLightsCommand;
import org.drools.workshop.rules.cmds.UseDoorCommand;
import org.drools.workshop.rules.cmds.WhereAmICommand;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class GameRulesCDITest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addClass(GameSessionImpl.class )
                .addClass( CommandExecutor.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    private GameSession game;

    @Test
    public void bootstrapGameTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        List<GameMessage> messages = game.getAllMessages();
        assertEquals( 5, messages.size() );

        assertTrue( messages.get( 4 ).getText().startsWith( "There is a house in the world" ) );

        assertTrue( messages.get( 3 ).getText().startsWith( "There is a room(Room A" ) );

        assertTrue( messages.get( 2 ).getText().startsWith( "There is a room(Outside" ) );

        assertTrue( messages.get( 1 ).getText().startsWith( "Wake up! You are trapped" ) );

        assertTrue( messages.get( 0 ).getText().startsWith( "There is light in the room!" ) );

        game.destroy();
    }

    @Test
    public void exploreRoomTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        Room roomIn = game.execute( new WhereAmICommand( player ) );

        List<Item> itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );

        assertEquals( 3, itemsInRoom.size() );
        for ( Item i : itemsInRoom ) {
            System.out.println( "Item: " + i );
        }

        assertTrue( itemsInRoom.get( 0 ) instanceof LightSwitch );
        assertTrue( itemsInRoom.get( 1 ) instanceof LightBulb );
        assertTrue( itemsInRoom.get( 2 ) instanceof Chest );

        List<GameMessage> messages = game.getAllMessages();
        assertEquals( 7, messages.size() );
        for ( GameMessage m : messages ) {
            System.out.println( "Message: " + m.getText() );
        }

        assertTrue( messages.get( 6 ).getText().startsWith( "There is a house in the world" ) );

        assertTrue( messages.get( 5 ).getText().startsWith( "There is a room(Room A" ) );

        assertTrue( messages.get( 4 ).getText().startsWith( "There is a room(Outside" ) );

        assertTrue( messages.get( 3 ).getText().startsWith( "Wake up! You are trapped" ) );

        assertTrue( messages.get( 2 ).getText().startsWith( "There is light in the room!" ) );

        assertTrue( messages.get( 1 ).getText().startsWith( "You are in Room A" ) );

        assertTrue( messages.get( 0 ).getText().startsWith( "3 Items available" ) );

        game.destroy();
    }

    @Test
    public void turnOffTheLightsTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        Room roomIn = game.execute( new WhereAmICommand( player ) );

        List<Item> itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );

        assertEquals( 3, itemsInRoom.size() );
        for ( Item i : itemsInRoom ) {
            System.out.println( "Item: " + i );
        }

        assertTrue( itemsInRoom.get( 0 ) instanceof LightSwitch );
        assertTrue( itemsInRoom.get( 1 ) instanceof LightBulb );
        assertTrue( itemsInRoom.get( 2 ) instanceof Chest );

        LightSwitch lightSwitch = ( LightSwitch ) itemsInRoom.get( 0 );

        game.execute( new TurnOffTheLightsCommand( lightSwitch ) );

        itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );
        for ( Item i : itemsInRoom ) {
            System.out.println( "Item: " + i );
        }
        assertEquals( 1, itemsInRoom.size() );
        assertTrue( itemsInRoom.get( 0 ) instanceof ShineInTheDarkItem );
        assertTrue( itemsInRoom.get( 0 ) instanceof MagicStone );

        game.execute( new TurnOnTheLightsCommand( lightSwitch ) );

        itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );
        assertEquals( 3, itemsInRoom.size() );

        game.destroy();
    }

    @Test
    public void exploreContainerAndOpenDoorTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        Room roomIn = game.execute( new WhereAmICommand( player ) );

        List<Door> doors = roomIn.getDoors();
        assertEquals( 1, doors.size() );
        assertTrue( doors.get( 0 ).isLocked() );
        assertTrue( !doors.get( 0 ).isOpen() );

        List<Item> itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );

        assertEquals( 3, itemsInRoom.size() );
        for ( Item i : itemsInRoom ) {
            System.out.println( "Item: " + i );
        }

        assertTrue( itemsInRoom.get( 2 ) instanceof Chest );
        assertTrue( itemsInRoom.get( 2 ) instanceof ItemContainer );
        ItemContainer container = ( ItemContainer ) itemsInRoom.get( 2 );

        List<Item> itemsInChest = container.getItems();
        assertEquals( 1, itemsInChest.size() );
        assertTrue( itemsInChest.get( 0 ) instanceof Key );

        Key key = ( Key ) itemsInChest.get( 0 );

        assertEquals( 0, player.getItems().size() );

        game.execute( new PickItemCommand( player, container, new PickableItem( key ) ) );

        assertEquals( 1, player.getItems().size() );

        List<GameMessage> allMessages = game.getAllMessages();
        assertEquals( 11, allMessages.size() );
        assertTrue( allMessages.get( 0 ).getText().startsWith( "Door 'Door A' Unlocked and Opened!" ) );

        assertTrue( !doors.get( 0 ).isLocked() );
        assertTrue( doors.get( 0 ).isOpen() );

        game.destroy();
    }

    @Test
    public void useOpenedDoorReachGoalTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        Room roomIn = game.execute( new WhereAmICommand( player ) );
        assertEquals( "Room A", roomIn.getName() );
        List<Door> doors = game.execute( new ExploreDoorsCommand( roomIn ) );
        assertEquals( 1, doors.size() );
        assertTrue( doors.get( 0 ).isLocked() );
        assertTrue( !doors.get( 0 ).isOpen() );

        List<Item> itemsInRoom = game.execute( new ExploreRoomCommand( roomIn ) );

        assertEquals( 3, itemsInRoom.size() );

        assertTrue( itemsInRoom.get( 2 ) instanceof Chest );
        assertTrue( itemsInRoom.get( 2 ) instanceof ItemContainer );
        ItemContainer container = ( ItemContainer ) itemsInRoom.get( 2 );

        List<Item> itemsInChest = container.getItems();
        assertEquals( 1, itemsInChest.size() );
        assertTrue( itemsInChest.get( 0 ) instanceof Key );

        Key key = ( Key ) itemsInChest.get( 0 );

        assertEquals( 0, player.getItems().size() );

        game.execute( new PickItemCommand( player, container, new PickableItem( key ) ) );

        assertEquals( 1, player.getItems().size() );

        List<GameMessage> allMessages = game.getAllMessages();
        assertEquals( 12, allMessages.size() );
        assertTrue( allMessages.get( 0 ).getText().startsWith( "Door 'Door A' Unlocked and Opened!" ) );

        Door door = doors.get( 0 );
        assertTrue( !door.isLocked() );
        assertTrue( door.isOpen() );

        game.execute( new UseDoorCommand( player, roomIn, door ) );

        roomIn = game.execute( new WhereAmICommand( player ) );

        assertEquals( "Outside", roomIn.getName() );

        game.destroy();
    }

    @Test
    public void gameSuggestionsTest() {
        Player player = new Player( "salaboy" );

        House house = createHouse( "My Escape The Room House", player );

        // Bootstrap the Game with the constructed house for this player
        game.bootstrap( house, player );

        List<Command> suggestions = game.getSuggestions();

        assertEquals( 7, suggestions.size() );

    }


    /* House With:
        * Room A:
        *  - 1 Door to Outside
        *  - 1 chest with a key
        *  - 1 LightSwitch
        *  - 1 LightBulb
        *  - 1 Magic Stone
        * Outside (Goal)
     */
    private House createHouse( String name, Player player ) {
        House house = new House( name );

        Room roomA = new Room( "Room A" );
        Door doorA = new Door( "Door A" );
        doorA.setLeadsTo( "Outside" );
        roomA.getDoors().add( doorA );
        Chest chestA = new Chest( "Chest A" );
        Key keyA = new Key( "Door A" );
        chestA.getItems().add( keyA );
        LightBulb lightBulbA = new LightBulb( "Room A LightBulb", true, false );
        LightSwitch lightSwitchA = new LightSwitch( "Room A LightSwitch", true );
        MagicStone magicStone = new MagicStone();
        roomA.getItems().add( chestA );
        roomA.getItems().add( lightBulbA );
        roomA.getItems().add( lightSwitchA );
        roomA.getItems().add( magicStone );
        // Adding Rooms to my House
        house.getRooms().add( roomA );
        house.getRooms().add( new Outside() );
        // Setting player to Room A
        roomA.getPeopleInTheRoom().add( player.getName() );
        return house;
    }
}

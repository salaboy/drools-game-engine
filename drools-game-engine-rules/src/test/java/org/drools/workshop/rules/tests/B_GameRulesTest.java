
package org.drools.workshop.rules.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.drools.workshop.model.impl.base.PlayerImpl;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.House;
import org.drools.workshop.model.house.Outside;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.items.Chest;
import org.drools.workshop.model.api.Item;
import org.drools.workshop.model.api.ItemContainer;
import org.drools.workshop.model.items.Key;
import org.drools.workshop.model.items.LightBulb;
import org.drools.workshop.model.items.LightSwitch;
import org.drools.workshop.model.items.MagicStone;
import org.drools.workshop.model.items.ShineInTheDarkItem;
import org.drools.workshop.rules.GameMessage;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author salaboy
 * This test interacts with the Rule Engine to test different behaviors of the game rules.
 * We need to avoid our users to interact directly, because as you can see it can get complex
 */
@RunWith( Arquillian.class )
public class B_GameRulesTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    @KBase( "gameKBase" )
    private KieBase kBase;

    /*
     * Test that the main hookpoints for a game are kicking in
     * Hook points: 
     * - There is a house , then we can do something here.. some extra initializations
     * - There is a room, extra initialization per room
     * - Everything is ready for the player to start ("Wake up call")
     * - There is light, so you can see things...
     */
    @Test
    public void gameRulesForANewHouseTest() {
        KieSession kSession = kBase.newKieSession();

        PlayerImpl player = new PlayerImpl( "salaboy" );

        kSession.insert( player );

        House house = createHouse( "My Escape The Room House", player );

        kSession.insert( house );
        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }

        // firing all the rules for the initial state
        int fired = kSession.fireAllRules();

        assertEquals( 5, fired );

        QueryResults queryResults = kSession.getQueryResults( "getAllMessages", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }

        assertEquals( 5, messages.size() );

        Set<String> messageTexts = messages.stream().map( m -> m.getText()).collect( Collectors.toSet());
        
        assertThat( messageTexts, 
                Matchers.containsInAnyOrder( "There is a house(My Escape The Room House) in the world",  
                                             "There is a room(Room A) in the house",
                                             "There is a room(Outside) in the house",
                                             "Wake up! You are trapped in Room A! You need to escape!", 
                                             "There is light in the room!"));
        


        kSession.dispose();

    }

    /*
     * This test shows how to execute actions agains the world by modifying our facts
     */
    @Test
    public void exploreTheRoomToFindItemsTest() {
        KieSession kSession = kBase.newKieSession();

        PlayerImpl player = new PlayerImpl( "salaboy" );
        kSession.insert( player );
        House house = createHouse( "My Escape The Room House", player );

        kSession.insert( house );
        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }

        // firing all the rules for the initial state
        int fired = kSession.fireAllRules();

        assertEquals( 5, fired );

        // Where Am I?
        QueryResults queryResults = kSession.getQueryResults( "WhereAmI", player.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room roomIn = null;
        while ( iterator.hasNext() ) {
            roomIn = ( Room ) iterator.next().get( "$r" );
            break;
        }

        assertNotNull( roomIn );

        kSession.insert( new GameMessage( "You are in " + roomIn.getName() ) );
        fired = kSession.fireAllRules();

        assertEquals( 0, fired );

        // First try to get all the items that are visible 
        //   (will return items if there is light and items are visible in daylight)
        List<Item> items = new ArrayList<Item>();
        queryResults = kSession.getQueryResults( "getVisibleItems", roomIn.getName() );
        iterator = queryResults.iterator();
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
        }

        // Next try to get all the items that shine in the dark 
        //   (will return items if there is no light and items shine in the dark)
        queryResults = kSession.getQueryResults( "getDarkItems", roomIn.getName() );
        iterator = queryResults.iterator();
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
        }

        kSession.insert( new GameMessage( items.size() + " Items available" ) );
        // We changed the state of the world, so we need to fire all the rules
        fired = kSession.fireAllRules();
        //No rule was fired, because i just inserted a new GameMessage that is only evaluated by a query,
        // but the query has the new message already
        assertEquals( 0, fired );

        assertEquals( 3, items.size() );

        assertTrue( items.get( 0 ) instanceof LightSwitch );
        assertTrue( items.get( 1 ) instanceof LightBulb );
        assertTrue( items.get( 2 ) instanceof Chest );

        queryResults = kSession.getQueryResults( "getAllMessages", ( Object ) null );
        iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }

        assertEquals( 7, messages.size() );
        
        Set<String> messageTexts = messages.stream().map( m -> m.getText()).collect( Collectors.toSet());
        
        assertThat( messageTexts, 
                Matchers.containsInAnyOrder( "There is a house(My Escape The Room House) in the world",  
                                             "There is a room(Room A) in the house",
                                             "There is a room(Outside) in the house",
                                             "Wake up! You are trapped in Room A! You need to escape!", 
                                             "There is light in the room!",
                                             "You are in Room A",
                                             "3 Items available"));

       

        kSession.destroy();
    }

    /*
     * This test shows what happens when we turn of the lights of the room.
     * Now after the lights are off, we can see objects that Shine In the Dark (implements ShineInTheDarkItem interface)
    */
    @Test
    public void turnOffTheLightsTest() {
        // Bootstrap the game
        KieSession kSession = kBase.newKieSession();

        PlayerImpl player = new PlayerImpl( "salaboy" );
        kSession.insert( player );
        House house = createHouse( "My Escape The Room House", player );

        kSession.insert( house );
        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }

        // firing all the rules for the initial state
        int fired = kSession.fireAllRules();

        assertEquals( 5, fired );

        // Where Am I?
        QueryResults queryResults = kSession.getQueryResults( "WhereAmI", player.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room roomIn = null;
        while ( iterator.hasNext() ) {
            roomIn = ( Room ) iterator.next().get( "$r" );
            break;
        }

        assertNotNull( roomIn );

        kSession.insert( new GameMessage( "You are in " + roomIn.getName() ) );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        // Let's explore the Room
        // First try to get all the items that are visible 
        //   (will return items if there is light and items are visible in daylight)
        List<Item> items = new ArrayList<Item>();
        queryResults = kSession.getQueryResults( "getVisibleItems", roomIn.getName() );
        iterator = queryResults.iterator();
        int counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 3 visible items
        assertEquals( 3, counter );

        // Next try to get all the items that shine in the dark 
        //   (will return items if there is no light and items shine in the dark)
        queryResults = kSession.getQueryResults( "getDarkItems", roomIn.getName() );
        iterator = queryResults.iterator();
        counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 0 shine in the dark items
        assertEquals( 0, counter );

        kSession.insert( new GameMessage( items.size() + " Items available" ) );
        // We changed the state of the world, so we need to fire all the rules
        fired = kSession.fireAllRules();
        //No rule was fired, because i just inserted a new GameMessage that is only evaluated by a query,
        // but the query has the new message already
        assertEquals( 0, fired );

        assertEquals( 3, items.size() );

        assertTrue( items.get( 0 ) instanceof LightSwitch );
        assertTrue( items.get( 1 ) instanceof LightBulb );
        assertTrue( items.get( 2 ) instanceof Chest );

        LightSwitch lightSwitch = ( LightSwitch ) items.get( 0 );

        // Turn off the lights
        FactHandle lightSwitchFH = kSession.getFactHandle( lightSwitch );
        lightSwitch.setOn( false );
        kSession.update( lightSwitchFH, lightSwitch );
        kSession.insert( new GameMessage( "Lights Turned Off" ) );
        kSession.fireAllRules();

        // First try to get all the items that are visible 
        //   (will return items if there is light and items are visible in daylight)
        items = new ArrayList<Item>();
        queryResults = kSession.getQueryResults( "getVisibleItems", roomIn.getName() );
        iterator = queryResults.iterator();
        counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 0 visible items
        assertEquals( 0, counter );

        // Next try to get all the items that shine in the dark 
        //   (will return items if there is no light and items shine in the dark)
        queryResults = kSession.getQueryResults( "getDarkItems", roomIn.getName() );
        iterator = queryResults.iterator();
        counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There is 1 shine in the dark items
        assertEquals( 1, counter );

        assertEquals( 1, items.size() );
        assertTrue( items.get( 0 ) instanceof ShineInTheDarkItem );
        assertTrue( items.get( 0 ) instanceof MagicStone );
        
        queryResults = kSession.getQueryResults( "getAllMessages", ( Object ) null );
        iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }

        assertEquals( 8, messages.size() );
        
        Set<String> messageTexts = messages.stream().map( m -> m.getText()).collect( Collectors.toSet());
        
        assertThat( messageTexts, 
                Matchers.containsInAnyOrder( "There is a house(My Escape The Room House) in the world",  
                                             "There is a room(Room A) in the house",
                                             "There is a room(Outside) in the house",
                                             "Wake up! You are trapped in Room A! You need to escape!", 
                                             "There is light in the room!",
                                             "You are in Room A",
                                             "3 Items available", 
                                             "Lights Turned Off"));
        

        kSession.dispose();
    }

    /*
     * This test shows how when the user picks a key from the Chest, the door labeled with 
     *  the same name as the key automatically unlocks, based on the rule that define that behavior
    */
    @Test
    public void exploreContainerAndOpenDoorTest() {
        // Bootstrap Game
        KieSession kSession = kBase.newKieSession();

        PlayerImpl player = new PlayerImpl( "salaboy" );
        kSession.insert( player );
        House house = createHouse( "My Escape The Room House", player );

        kSession.insert( house );
        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }

        // firing all the rules for the initial state
        int fired = kSession.fireAllRules();

        assertEquals( 5, fired );

        // Where Am I?
        QueryResults queryResults = kSession.getQueryResults( "WhereAmI", player.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room roomIn = null;
        while ( iterator.hasNext() ) {
            roomIn = ( Room ) iterator.next().get( "$r" );
            break;
        }

        assertNotNull( roomIn );

        kSession.insert( new GameMessage( "You are in " + roomIn.getName() ) );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        List<Door> doors = roomIn.getDoors();
        assertEquals( 1, doors.size() );
        assertTrue( doors.get( 0 ).isLocked() );
        assertTrue( !doors.get( 0 ).isOpen() );

        // Now let's explore the room
        // Let's explore the Room
        // First try to get all the items that are visible 
        //   (will return items if there is light and items are visible in daylight)
        List<Item> items = new ArrayList<Item>();
        queryResults = kSession.getQueryResults( "getVisibleItems", roomIn.getName() );
        iterator = queryResults.iterator();
        int counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 3 visible items
        assertEquals( 3, counter );

        // Next try to get all the items that shine in the dark 
        //   (will return items if there is no light and items shine in the dark)
        queryResults = kSession.getQueryResults( "getDarkItems", roomIn.getName() );
        iterator = queryResults.iterator();
        counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 0 shine in the dark items
        assertEquals( 0, counter );

        kSession.insert( new GameMessage( items.size() + " Items available" ) );
        // We changed the state of the world, so we need to fire all the rules
        fired = kSession.fireAllRules();
        //No rule was fired, because i just inserted a new GameMessage that is only evaluated by a query,
        // but the query has the new message already
        assertEquals( 0, fired );

        assertEquals( 3, items.size() );

        assertTrue( items.get( 0 ) instanceof LightSwitch );
        assertTrue( items.get( 1 ) instanceof LightBulb );
        assertTrue( items.get( 2 ) instanceof Chest );

        assertTrue( items.get( 2 ) instanceof Chest );
        assertTrue( items.get( 2 ) instanceof ItemContainer );
        ItemContainer container = ( ItemContainer ) items.get( 2 );

        List<Item> itemsInChest = container.getItems();
        assertEquals( 1, itemsInChest.size() );
        assertTrue( itemsInChest.get( 0 ) instanceof Key );

        Key key = ( Key ) itemsInChest.get( 0 );

        assertEquals( 0, player.getInventory().getItems().size() );

        // Pick up the key
        FactHandle playerFH = kSession.getFactHandle( player );
        player.getInventory().getItems().add( key );
        FactHandle containerFH = kSession.getFactHandle( container );
        container.getItems().remove( key );
        kSession.update( playerFH, player );
        kSession.update( containerFH, container );
        kSession.insert( new GameMessage( "Item Picked! " + key ) );
        fired = kSession.fireAllRules();
        
        assertEquals( 3, fired );

        assertEquals( 1, player.getInventory().getItems().size() );

        queryResults = kSession.getQueryResults( "getAllMessages", ( Object ) null );
        iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }
        
        
        assertEquals( 11, messages.size() );
        
        Set<String> messageTexts = messages.stream().map( m -> m.getText()).collect( Collectors.toSet());
        
        assertThat( messageTexts, 
                Matchers.containsInAnyOrder( "There is a house(My Escape The Room House) in the world",  
                                             "There is a room(Room A) in the house",
                                             "There is a room(Outside) in the house",
                                             "Wake up! You are trapped in Room A! You need to escape!", 
                                             "There is light in the room!",
                                             "You are in Room A",
                                             "3 Items available", 
                                             "Item Picked! Key{name=Door A}",
                                             "There is a new item in our inventory!Key{name=Door A}",
                                             "Door 'Door A' Unlocked and Opened!"));
        

        assertTrue( !doors.get( 0 ).isLocked() );
        assertTrue( doors.get( 0 ).isOpen() );

        kSession.dispose();
    }
    
    /*
     * This test Open the door and uses it to walk Outside and accomplish the game goal! 
    */
    @Test
    public void useOpenedDoorReachGoalTest() {
        // Bootstrap Game
        KieSession kSession = kBase.newKieSession();

        PlayerImpl player = new PlayerImpl( "salaboy" );
        kSession.insert( player );
        House house = createHouse( "My Escape The Room House", player );

        kSession.insert( house );
        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }

        // firing all the rules for the initial state
        int fired = kSession.fireAllRules();

        assertEquals( 5, fired );

        // Where Am I?
        QueryResults queryResults = kSession.getQueryResults( "WhereAmI", player.getName() );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        Room roomIn = null;
        while ( iterator.hasNext() ) {
            roomIn = ( Room ) iterator.next().get( "$r" );
            break;
        }

        assertNotNull( roomIn );

        kSession.insert( new GameMessage( "You are in " + roomIn.getName() ) );
        fired = kSession.fireAllRules();
        assertEquals( 0, fired );

        List<Door> doors = roomIn.getDoors();
        assertEquals( 1, doors.size() );
        assertTrue( doors.get( 0 ).isLocked() );
        assertTrue( !doors.get( 0 ).isOpen() );

        // Now let's explore the room
        // Let's explore the Room
        // First try to get all the items that are visible 
        //   (will return items if there is light and items are visible in daylight)
        List<Item> items = new ArrayList<Item>();
        queryResults = kSession.getQueryResults( "getVisibleItems", roomIn.getName() );
        iterator = queryResults.iterator();
        int counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 3 visible items
        assertEquals( 3, counter );

        // Next try to get all the items that shine in the dark 
        //   (will return items if there is no light and items shine in the dark)
        queryResults = kSession.getQueryResults( "getDarkItems", roomIn.getName() );
        iterator = queryResults.iterator();
        counter = 0;
        while ( iterator.hasNext() ) {
            Item item = ( Item ) iterator.next().get( "$i" );
            items.add( item );
            counter++;
        }
        // There are 0 shine in the dark items
        assertEquals( 0, counter );

        kSession.insert( new GameMessage( items.size() + " Items available" ) );
        // We changed the state of the world, so we need to fire all the rules
        fired = kSession.fireAllRules();
        //No rule was fired, because i just inserted a new GameMessage that is only evaluated by a query,
        // but the query has the new message already
        assertEquals( 0, fired );

        assertEquals( 3, items.size() );

        assertTrue( items.get( 0 ) instanceof LightSwitch );
        assertTrue( items.get( 1 ) instanceof LightBulb );
        assertTrue( items.get( 2 ) instanceof Chest );

        assertTrue( items.get( 2 ) instanceof Chest );
        assertTrue( items.get( 2 ) instanceof ItemContainer );
        ItemContainer container = ( ItemContainer ) items.get( 2 );

        List<Item> itemsInChest = container.getItems();
        assertEquals( 1, itemsInChest.size() );
        assertTrue( itemsInChest.get( 0 ) instanceof Key );

        Key key = ( Key ) itemsInChest.get( 0 );

        assertEquals( 0, player.getInventory().getItems().size() );

        // Pick up the key
        FactHandle playerFH = kSession.getFactHandle( player );
        player.getInventory().getItems().add( key );
        FactHandle containerFH = kSession.getFactHandle( container );
        container.getItems().remove( key );
        kSession.update( playerFH, player );
        kSession.update( containerFH, container );
        kSession.insert( new GameMessage( "Item Picked! " + key ) );
        fired = kSession.fireAllRules();
        
        assertEquals( 3, fired );

        assertEquals( 1, player.getInventory().getItems().size() );
        
        assertTrue( !doors.get( 0 ).isLocked() );
        assertTrue( doors.get( 0 ).isOpen() );

        // Use the opened Door
        FactHandle roomInFH = kSession.getFactHandle( roomIn );
        roomIn.getPeopleInTheRoom().remove( player.getName() );
        kSession.update( roomInFH, roomIn );
        queryResults = kSession.getQueryResults( "getRoomByName", doors.get( 0 ).getLeadsTo() );
        iterator = queryResults.iterator();
        Room roomTo = null;
        while ( iterator.hasNext() ) {
            roomTo = ( Room ) iterator.next().get( "$r" );
            break;
        }
        if ( roomTo != null ) {
            FactHandle roomToFH = kSession.getFactHandle( roomTo );
            roomTo.getPeopleInTheRoom().add( player.getName() );
            kSession.update( roomToFH, roomTo );
            kSession.insert( new GameMessage( "Player moved from  " + roomIn.getName() + " to " + roomTo.getName() ) );
        } else {
            kSession.insert( new GameMessage( "ERROR: Door cannot be used because the room:   " + doors.get( 0 ).getLeadsTo() + " was not found. " ) );
        }
        
         // Where Am I? Again
        queryResults = kSession.getQueryResults( "WhereAmI", player.getName() );
        iterator = queryResults.iterator();
        roomIn = null;
        while ( iterator.hasNext() ) {
            roomIn = ( Room ) iterator.next().get( "$r" );
            break;
        }

        assertNotNull( roomIn );

        kSession.insert( new GameMessage( "You are in " + roomIn.getName() ) );
        fired = kSession.fireAllRules();
        assertEquals( 4, fired );

        assertEquals( "Outside", roomIn.getName() );
        
        queryResults = kSession.getQueryResults( "getAllMessages", ( Object ) null );
        iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }
        
        assertEquals( 17, messages.size() );
        Set<String> messageTexts = messages.stream().map( m -> m.getText()).collect( Collectors.toSet());
        
        assertThat( messageTexts, 
                Matchers.containsInAnyOrder( "There is a house(My Escape The Room House) in the world",  
                                             "There is a room(Room A) in the house",
                                             "There is a room(Outside) in the house",
                                             "Wake up! You are trapped in Room A! You need to escape!", 
                                             "There is light in the room!",
                                             "You are in Room A",
                                             "3 Items available", 
                                             "Item Picked! Key{name=Door A}",
                                             "There is a new item in our inventory!Key{name=Door A}",
                                             "Door 'Door A' Unlocked and Opened!", 
                                             "Congrats! You manage to escape the Room!",
                                             "You are in Outside",
                                             "Player moved from  Room A to Outside"));

        kSession.destroy();
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
    private House createHouse( String name, PlayerImpl player ) {
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

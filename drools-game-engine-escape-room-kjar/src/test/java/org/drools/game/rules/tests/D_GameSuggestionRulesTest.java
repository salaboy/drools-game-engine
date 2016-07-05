/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.rules.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.Context;
import org.drools.game.core.ContextImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.model.house.Door;
import org.drools.game.model.house.House;
import org.drools.game.model.house.Outside;
import org.drools.game.model.house.Room;
import org.drools.game.model.items.Broom;
import org.drools.game.model.items.Chest;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.ItemContainer;
import org.drools.game.model.api.Player;
import org.drools.game.model.items.Key;
import org.drools.game.model.items.LightBulb;
import org.drools.game.model.items.LightSwitch;
import org.drools.game.model.items.MagicStone;
import org.drools.game.rules.cmds.ExploreContainersCommand;
import org.drools.game.rules.cmds.OpenContainerCommand;
import org.drools.game.rules.cmds.PickItemCommand;
import org.drools.game.rules.cmds.TurnOffTheLightsCommand;
import org.drools.game.rules.cmds.TurnOnTheLightsCommand;
import org.drools.game.rules.cmds.UseDoorCommand;
import org.drools.game.rules.cmds.WhereAmICommand;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/*
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class D_GameSuggestionRulesTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    @KBase( name = "fullKBase" )
    private KieBase kieBase;

    /*
     * This test covers the suggestion mechanism with a simple scenario. 
     *  By checking that as soon as there is a player, he/she can ask for his/her location
     */
    @Test
    public void engineSuggestionsSimpleTest() {
        KieSession kSession = kieBase.newKieSession();
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        
        Player player = new BasePlayerImpl( "salaboy" );
        FactHandle playerFH = kSession.insert( player );

        kSession.fireAllRules();

        // Test suggestions with just the Player (no house, no room)
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        assertEquals( 1, cmds.size() );

        //WhereAmI command found
        Command cmdWhereAmI = cmds.get( 0 );
        Assert.assertTrue( cmdWhereAmI instanceof WhereAmICommand );

        // Remove Player
        kSession.delete( playerFH );

        kSession.fireAllRules();

        // The suggestions should be gone, if the player is not there anymore.
        queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        Assert.assertEquals( 0, cmds.size() );
        kSession.dispose();
    }

    /*
    * Testing suggestions with a more complex scenario. 
    *  For this test we want to check that when there is a light switch and a light bulb
    *  in the same room that the player, the suggestion mechanims gives the player
    *  the option of turning off the light. The test also checks the suggestions based on the 
    *  state of the light, so different PickItem commands should be recommend in both states \
    *  (visible items vs shine in the dark)
     */
    @Test
    public void lightsOnOffSuggestionsTest() {
        KieSession kSession = kieBase.newKieSession();
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        
        configureListeners( kSession );
        // Create house & Bootstrap
        Player player = new BasePlayerImpl( "salaboy" );
        House house = new House( "Maniac Mansion" );
        Room roomA = new Room( "Room A" );
        Door doorA = new Door( "Door A" );
        doorA.setLeadsTo( "Outside" );
        roomA.getDoors().add( doorA );

        LightBulb lightBulbA = new LightBulb( "Room A LightBulb", true, false );
        LightSwitch lightSwitchA = new LightSwitch( "Room A LightSwitch", true );
        Broom broom = new Broom( "Old Broom" );
        MagicStone magicStone = new MagicStone();

        roomA.getItems().add( lightBulbA );
        roomA.getItems().add( lightSwitchA );
        roomA.getItems().add( magicStone );
        roomA.getItems().add( broom );
        // Adding Rooms to my House
        house.getRooms().add( roomA );
        house.getRooms().add( new Outside() );
        // Setting player to Room A
        roomA.getPeopleInTheRoom().add( player.getName() );

        for ( Room r : house.getRooms() ) {
            kSession.insert( r );
            for ( Item i : r.getItems() ) {
                kSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                kSession.insert( d );
            }
        }
        kSession.insert( player );

        kSession.fireAllRules();

        // Get All suggestions
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }
        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors)
        *  - Pick Item (Broom)
        *  - TurnOff the lights
         */
        assertEquals( 5, cmds.size() );

        TurnOffTheLightsCommand turnOffTheLightsCmd = null;
        PickItemCommand pickItemCmd = null;
        for ( Command cmd : cmds ) {
            if ( cmd instanceof TurnOffTheLightsCommand ) {
                turnOffTheLightsCmd = ( TurnOffTheLightsCommand ) cmd;
            }
            if ( cmd instanceof PickItemCommand ) {
                pickItemCmd = ( PickItemCommand ) cmd;
            }
        }

        assertNotNull( turnOffTheLightsCmd );
        assertNotNull( pickItemCmd );
        assertEquals( "Old Broom", pickItemCmd.getPickableItem().getPickable().getName() );

        // We can execute the command returned by the suggestion mechanism
        Context ctx = new ContextImpl();
        ctx.getData().put( "session", kSession );
        ctx.getData().put( "messageService", new GameMessageServiceImpl() );
        turnOffTheLightsCmd.execute( ctx );

        kSession.fireAllRules();

        // Get all suggestions again after turning off the lights
        queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );

        }
        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors)
        *  - Pick Item (Magic Stone)
        *  - TurnOn the lights
         */
        Assert.assertEquals( 5, cmds.size() );

        TurnOnTheLightsCommand turnOnTheLightsCmd = null;
        pickItemCmd = null;
        for ( Command cmd : cmds ) {
            if ( cmd instanceof TurnOnTheLightsCommand ) {
                turnOnTheLightsCmd = ( TurnOnTheLightsCommand ) cmd;
            }
            if ( cmd instanceof PickItemCommand ) {
                pickItemCmd = ( PickItemCommand ) cmd;
            }
        }

        assertNotNull( turnOnTheLightsCmd );
        assertNotNull( pickItemCmd );

        assertEquals( "Magic Stone", pickItemCmd.getPickableItem().getPickable().getName() );

    }

    /*
     * This test check that the correct suggestions are given to the player to: 
    *    - Get the containers in the room
    *    - Open the containers
    *    - Picking the key inside the container
    *    - And using the Door when is finally opened to achieve the goal
     */
    @Test
    public void useDoorSuggestionsTest() {
        KieSession kSession = kieBase.newKieSession();
        kSession.setGlobal( "messageService", new GameMessageServiceImpl() );
        //Create House and Bootstrap
        configureListeners( kSession );
        Player player = new BasePlayerImpl( "salaboy" );

        kSession.insert( player );

        House house = createHouse( "Maniac Mansion", player );

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

        kSession.fireAllRules();

        // Get all suggestions
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors, explore containers)
        *  - TurnOff the lights
        *  - Open Container (Chest)
         */
        Assert.assertEquals( 6, cmds.size() );

        ExploreContainersCommand exploreContainerCmd = null;
        OpenContainerCommand openContainerCmd = null;

        for ( Command cmd : cmds ) {
            if ( cmd instanceof ExploreContainersCommand ) {
                exploreContainerCmd = ( ExploreContainersCommand ) cmd;
            }
            if ( cmd instanceof OpenContainerCommand ) {
                openContainerCmd = ( OpenContainerCommand ) cmd;
            }

        }

        assertNotNull( exploreContainerCmd );
        assertNotNull( openContainerCmd );
        // First of all Explore all the containers in the room
        Context ctx = new ContextImpl();
        ctx.getData().put( "session", kSession );
        ctx.getData().put( "messageService", new GameMessageServiceImpl() );
        List<ItemContainer> itemContainers = exploreContainerCmd.execute( ctx );

        assertEquals( 1, itemContainers.size() );

        // Open the container
        openContainerCmd.execute( ctx );
        kSession.fireAllRules();

        // Get all the suggestions again
        queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors, explore containers)
        *  - TurnOff the lights
        *  - PickItem key inside container
         */
        assertEquals( 6, cmds.size() );

        PickItemCommand pickItemCmd = null;

        for ( Command cmd : cmds ) {
            if ( cmd instanceof PickItemCommand ) {
                pickItemCmd = ( PickItemCommand ) cmd;
            }

        }
        assertNotNull( pickItemCmd );
        // Pick up the Key
        pickItemCmd.execute( ctx );

        kSession.fireAllRules();

        // Get all the suggestions after picking up the key
        queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors, explore containers)
        *  - TurnOff the lights
        *  - Use the Door
         */
        assertEquals( 6, cmds.size() );

        UseDoorCommand useDoorCmd = null;

        for ( Command cmd : cmds ) {
            if ( cmd instanceof UseDoorCommand ) {
                useDoorCmd = ( UseDoorCommand ) cmd;
            }

        }
        // Let's use the door
        assertNotNull( useDoorCmd );

        useDoorCmd.execute( ctx );
        kSession.fireAllRules();

        // Get all suggestions now, being outside
        queryResults = kSession.getQueryResults( "getAllSuggestions", player );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();

        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }

        /* The suggestions here should include
        *  - WhereAmI command
        *  - Explore commands (explore the room, explore the doors, explore containers)
         */
        assertEquals( 3, cmds.size() );

        kSession.dispose();

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

    private void configureListeners( KieSession kSession ) {

        kSession.addEventListener( new AgendaEventListener() {
            @Override
            public void matchCreated( MatchCreatedEvent mce ) {

            }

            @Override
            public void matchCancelled( MatchCancelledEvent mce ) {

            }

            @Override
            public void beforeMatchFired( BeforeMatchFiredEvent bmfe ) {

            }

            @Override
            public void afterMatchFired( AfterMatchFiredEvent amfe ) {

                System.out.println( ">>> Rule Fired: " + amfe.getMatch().getRule().getName() );
            }

            @Override
            public void agendaGroupPopped( AgendaGroupPoppedEvent agpe ) {

            }

            @Override
            public void agendaGroupPushed( AgendaGroupPushedEvent agpe ) {

            }

            @Override
            public void beforeRuleFlowGroupActivated( RuleFlowGroupActivatedEvent rfgae ) {

            }

            @Override
            public void afterRuleFlowGroupActivated( RuleFlowGroupActivatedEvent rfgae ) {

            }

            @Override
            public void beforeRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent rfgde ) {

            }

            @Override
            public void afterRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent rfgde ) {

            }
        } );
    }

}

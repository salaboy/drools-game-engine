/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.CommandExecutor;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.House;
import org.drools.workshop.model.house.Outside;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.items.Broom;
import org.drools.workshop.model.items.Chest;
import org.drools.workshop.model.items.Item;
import org.drools.workshop.model.items.ItemContainer;
import org.drools.workshop.model.items.Key;
import org.drools.workshop.model.items.LightBulb;
import org.drools.workshop.model.items.LightSwitch;
import org.drools.workshop.model.items.MagicStone;
import org.drools.workshop.rules.GameSessionImpl;
import org.drools.workshop.rules.cmds.ExploreContainersCommand;
import org.drools.workshop.rules.cmds.OpenContainerCommand;
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
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

/**
 *
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class GameEngineRulesCDITest {
    
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
    @KBase( name = "engineKBase" )
    private KieBase kieBase;
    
    @Test
    public void engineSuggestionsSimpleTest() {
        KieSession kSession = kieBase.newKieSession();
        
        FactHandle playerFH = kSession.insert( new Player( "salaboy" ) );
        
        int fireAllRules = kSession.fireAllRules();
        
        Assert.assertEquals( 1, fireAllRules );
        
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }
        
        Assert.assertEquals( 1, cmds.size() );
        
        Command cmdWhereAmI = cmds.get( 0 );
        Assert.assertTrue( cmdWhereAmI instanceof WhereAmICommand );
        
        kSession.delete( playerFH );
        kSession.fireAllRules();
        
        queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }
        
        Assert.assertEquals( 0, cmds.size() );
        kSession.dispose();
    }
    
    @Test
    public void lightsOnOffSuggestionsTest() {
        KieSession kSession = kieBase.newKieSession();
        configureListeners( kSession );
        Player player = new Player( "salaboy" );
        House house = new House( "Manic Mansion" );
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
        
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();
        System.out.println( ">> With Lights on: " );
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        Assert.assertEquals( 6, cmds.size() );
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
        
        Context ctx = new Context();
        ctx.getData().put( "session", kSession );
        turnOffTheLightsCmd.execute( ctx );
        
        kSession.fireAllRules();
        
        queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        System.out.println( ">> With Lights off: " );
        
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        
        Assert.assertEquals( 6, cmds.size() );
        
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
    
    @Test
    public void useDoorSuggestionsTest() {
        KieSession kSession = kieBase.newKieSession();
        configureListeners( kSession );
        Player player = new Player( "salaboy" );
        
        kSession.insert( player );
        
        House house = createHouse( "Manic Mansion", player );
        
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
        
        QueryResults queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();
        
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        
        Assert.assertEquals( 7, cmds.size() );
        
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
        
        Context ctx = new Context();
        ctx.getData().put( "session", kSession );
        List<ItemContainer> itemContainers = exploreContainerCmd.execute( ctx );
        
        assertEquals( 1, itemContainers.size() );
        
        openContainerCmd.execute( ctx );
        kSession.fireAllRules();
        queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        
        Assert.assertEquals( 8, cmds.size() );
        
        PickItemCommand pickItemCmd = null;
        
        for ( Command cmd : cmds ) {
            if ( cmd instanceof PickItemCommand ) {
                pickItemCmd = ( PickItemCommand ) cmd;
            }
            
        }
        assertNotNull( pickItemCmd );
        
        pickItemCmd.execute( ctx );
        
        kSession.fireAllRules();
        
        queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        
        Assert.assertEquals( 8, cmds.size() );
        
        UseDoorCommand useDoorCmd = null;
        
        for ( Command cmd : cmds ) {
            if ( cmd instanceof UseDoorCommand ) {
                useDoorCmd = ( UseDoorCommand ) cmd;
            }
            
        }
        assertNotNull( useDoorCmd );
        
        useDoorCmd.execute( ctx );
        kSession.fireAllRules();
        
        queryResults = kSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        iterator = queryResults.iterator();
        cmds = new ArrayList<Command>();
        
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
            System.out.println( "CMD: " + cmd );
        }
        
        Assert.assertEquals( 4, cmds.size() );
        
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

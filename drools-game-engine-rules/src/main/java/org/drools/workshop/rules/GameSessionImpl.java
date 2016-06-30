/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.drools.workshop.core.Command;
import org.drools.workshop.core.CommandExecutor;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.impl.base.PlayerImpl;
import org.drools.workshop.model.house.Door;
import org.drools.workshop.model.house.House;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.api.Item;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.LiveQuery;

import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

/**
 *
 * @author salaboy
 */
public class GameSessionImpl implements GameSession {

    @Inject
    @KBase
    private KieBase kBase;

    @Inject
    private CommandExecutor executor;

    private KieSession currentSession = null;

    private LiveQuery gameMessageNotifications = null;

    private final Context context;
    
    private PlayerImpl currentPlayer;

    public GameSessionImpl() {
        context = new Context();
    }

    public void bootstrap( House house, PlayerImpl player, boolean debugEnabled, PrintStream out ) {
        if ( currentSession != null ) {
            throw new IllegalStateException( "Error: There is another game session in progress, destroy the current session first!" );
        }
        if( player == null){
            throw new IllegalStateException( "Error: We need a player to bootstrap a new game session" );
        }
        this.currentPlayer = player;
        //Setting globals first
        currentSession = kBase.newKieSession();
        if ( debugEnabled ) {
            setupListeners();
        }
        if ( out != null ) {
            setupMessageNotifications( out );
        }
        //inserting world facts
        currentSession.insert( house );
        for ( Room r : house.getRooms() ) {
            currentSession.insert( r );
            for ( Item i : r.getItems() ) {
                currentSession.insert( i );
            }
            for ( Door d : r.getDoors() ) {
                currentSession.insert( d );
            }
        }
        currentSession.insert( player );

        // firing all the rules for the initial state
        currentSession.fireAllRules();

        context.getData().put( "session", currentSession );
    }

    @Override
    public PlayerImpl getPlayer() {
        return currentPlayer;
    }

    
    @Override
    public void bootstrap( House house, PlayerImpl player ) {
        bootstrap( house, player, false, System.out );
    }

    @Override
    public List<Command> getSuggestions() {
        QueryResults queryResults = currentSession.getQueryResults( "getAllSuggestions", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<Command> cmds = new ArrayList<Command>();
        while ( iterator.hasNext() ) {
            Command cmd = ( Command ) iterator.next().get( "$c" );
            cmds.add( cmd );
        }
        return cmds;
    }

    @Override
    public <T> T execute( Command<T> cmd ) {
        T results = executor.execute( cmd, context );
        currentSession.fireAllRules();
        return results;
    }

    private void setupMessageNotifications( PrintStream out ) {
        gameMessageNotifications = currentSession.openLiveQuery( "getAllMessages", new Object[]{ "" }, new ViewChangedEventListener() {

            @Override
            public void rowInserted( Row row ) {
                GameMessage msg = ( GameMessage ) row.get( "$m" );
                out.println( "> Notification: (" + System.currentTimeMillis() + ")" + msg.getText() );
            }

            @Override
            public void rowDeleted( Row row ) {

            }

            @Override
            public void rowUpdated( Row row ) {

            }
        } );
    }

    @Override
    public List<GameMessage> getAllMessages() {
        QueryResults queryResults = currentSession.getQueryResults( "getAllMessages", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }
        return messages;
    }

    private void setupListeners() {
        currentSession.addEventListener( new AgendaEventListener() {
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

        currentSession.addEventListener( new RuleRuntimeEventListener() {
            @Override
            public void objectInserted( ObjectInsertedEvent oie ) {

            }

            @Override
            public void objectUpdated( ObjectUpdatedEvent oue ) {

            }

            @Override
            public void objectDeleted( ObjectDeletedEvent ode ) {

            }
        } );
    }
    
    

    @Override
    public void destroy() {
        if ( currentSession == null ) {
            throw new IllegalStateException( "Error: There is no game session to destroy!" );
        }
        gameMessageNotifications.close();
        currentSession.dispose();
        currentSession = null;
    }

}

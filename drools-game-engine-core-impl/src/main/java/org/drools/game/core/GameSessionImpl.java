/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core;

import org.drools.game.core.api.Command;
import org.drools.game.core.api.GameSession;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.game.core.api.GameConfiguration;
import org.drools.game.core.api.GameMessage;
import org.drools.game.model.api.Player;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
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
import org.kie.api.runtime.KieContainer;
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
    private CommandExecutor executor;

    private KieSession currentSession = null;

    private LiveQuery gameMessageNotifications = null;

    private final ContextImpl context;

    private Player currentPlayer;

    private GameConfiguration currentConfig;

    public GameSessionImpl() {
        context = new ContextImpl();
    }

    @Override
    public Player getPlayer() {
        return currentPlayer;
    }

    @Override
    public void bootstrap( Player player, GameConfiguration config ) {
        if ( currentSession != null ) {
            throw new IllegalStateException( "Error: There is another game session in progress, destroy the current session first!" );
        }
        if ( currentConfig != null ) {
            throw new IllegalStateException( "Error: There is another game configuration being used." );
        }
        
        if ( player == null ) {
            throw new IllegalStateException( "Error: We need a player to bootstrap a new game session" );
        }
        this.currentPlayer = player;
        String[] releaseIdPlusKbase = config.getGamePackage().split( ":" );
        KieServices ks = KieServices.Factory.get();
        KieBase kBase = null;
        if ( releaseIdPlusKbase.length == 1 ) {
            KieContainer kContainer = ks.getKieClasspathContainer();
            kBase = kContainer.getKieBase();
        } else {
            KieContainer kContainer = ks.newKieContainer( new ReleaseIdImpl( releaseIdPlusKbase[0], releaseIdPlusKbase[1], releaseIdPlusKbase[2] ) );
            kBase = kContainer.getKieBase( releaseIdPlusKbase[3] );
        }
        currentSession = kBase.newKieSession();
        if ( config.isDebugEnabled() ) {
            setupListeners();
        }
        if ( config.getLogStream() != null ) {
            setupMessageNotifications( config.getLogStream() );
        }

        currentSession.insert( player );
        //insert all the initial facts
        if ( config.getInitialData() != null ) {
            for ( Object o : config.getInitialData() ) {
                currentSession.insert( o );
            }
        }
        context.getData().put( "session", currentSession );
        context.getData().put( "messageService", new GameMessageServiceImpl() );
        // firing all the rules for the initial state
        currentSession.fireAllRules();
        
    }

    @Override
    public void join( Player player ) {
        currentSession.insert( player );
        currentSession.fireAllRules();
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

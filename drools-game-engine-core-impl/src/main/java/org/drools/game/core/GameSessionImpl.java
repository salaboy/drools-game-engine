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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.inject.Inject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.game.core.api.GameCallbackService;
import org.drools.game.core.api.GameConfiguration;
import org.drools.game.core.api.GameMessage;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.core.api.PlayerConfiguration;
import org.drools.game.model.api.Player;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
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

    private KieSession currentGameSession = null;

    private ContextImpl currentGameContext = null;

    private GameConfiguration currentConfig = null;

    private LiveQuery gameMessageNotifications = null;

    @Inject
    private GameMessageService messageService;
    
    @Inject 
    private GameCallbackService callbackService;

    private Map<String, FactHandle> currentPlayers = null;

    public GameSessionImpl() {

    }

    @Override
    public void bootstrap( GameConfiguration config ) {
        if ( currentGameSession != null ) {
            throw new IllegalStateException( "0001 - Error: There is another game session in progress, destroy the current session first!" );
        }

        if ( currentConfig != null ) {
            throw new IllegalStateException( "0002 - Error: There is another game configuration being used." );
        }
        if ( currentGameContext != null ) {
            throw new IllegalStateException( "0003 - Error: There is another game context defined, please make sure you destroy "
                    + "the previous game session before bootstraping a new one." );
        }
        currentGameContext = new ContextImpl();
        currentPlayers = new HashMap<String, FactHandle>();
        currentConfig = config;
        // Bootstrapping the Game Session
        String[] gameKbaseGAVK = config.getGamePackage().split( ":" );
        KieServices ks = KieServices.Factory.get();
        KieBase gameKbase = null;
        if ( gameKbaseGAVK.length == 1 ) {
            KieContainer kContainer = ks.getKieClasspathContainer();
            gameKbase = kContainer.getKieBase();
        } else {
            KieContainer kContainer = ks.newKieContainer( new ReleaseIdImpl( gameKbaseGAVK[0], gameKbaseGAVK[1], gameKbaseGAVK[2] ) );
            if ( gameKbaseGAVK.length == 3 ) {
                gameKbase = kContainer.getKieBase();
            } else if ( gameKbaseGAVK.length == 4 ) {
                gameKbase = kContainer.getKieBase( gameKbaseGAVK[3] );
            }
        }
        if ( gameKbase != null ) {
            currentGameSession = gameKbase.newKieSession();
            messageService = new GameMessageServiceImpl();
            currentGameSession.setGlobal( "messageService", messageService );
            currentGameSession.setGlobal( "callback",  callbackService);

            if ( config.isDebugEnabled() ) {
                setupGameListeners();
            }

            //insert all the initial facts
            if ( config.getInitialData() != null ) {
                for ( Object o : config.getInitialData() ) {
                    currentGameSession.insert( o );
                }
            }

            currentGameContext.getData().put( "session", currentGameSession );
            currentGameContext.getData().put( "messageService", messageService );
            // firing all the rules for the initial state
            currentGameSession.fireAllRules();

        } else {
            throw new IllegalStateException( "0004 - We coudn't build the Knolwedge Base for your game :( " );
        }

    }

    @Override
    public void join( Player player, PlayerConfiguration playerConfig ) {
        if ( currentGameSession == null ) {
            throw new IllegalStateException( "0007 - There is no game session active, you cannot join here " );
        }
        if ( playerConfig.getLogStream() != null ) {
            setupPlayerMessageNotifications( player, playerConfig.getLogStream() );
        }
        FactHandle playerFH = currentGameSession.insert( player );
        currentPlayers.put( player.getName(), playerFH );
        currentGameSession.fireAllRules();
    }

    @Override
    public List<Command> getSuggestions( Player p ) {

        if ( p == null ) {
            throw new IllegalStateException( "0006 - Error: getting all suggestions, the player is null." );
        }
        QueryResults queryResults = currentGameSession.getQueryResults( "getAllSuggestions", p );
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
        if(executor == null){
            throw new IllegalStateException(" 0008 - Error: Make sure that the game session is properly bootstraped before executing any cmd.");
        }
        if(currentGameSession == null){
            throw new IllegalStateException(" 0009 - Error: There is no session for the current game, please make sure that you properly bootstraped the game");
        }
        T results = executor.execute( cmd, currentGameContext );
        currentGameSession.fireAllRules();
        return results;
    }

    private void setupGameListeners() {
        currentGameSession.addEventListener( new DefaultAgendaEventListener() );
        currentGameSession.addEventListener( new DefaultRuleRuntimeEventListener() );
    }

    private void setupPlayerMessageNotifications( Player p, PrintStream out ) {
        Object[] params = new Object[1];
        params[0] = p.getName();
        gameMessageNotifications = currentGameSession.openLiveQuery( "getAllPlayerMessages", params, new ViewChangedEventListener() {

            @Override
            public void rowInserted( Row row ) {
                GameMessage msg = ( GameMessage ) row.get( "$m" );
                out.println( "LOG: > Player Notification: (" + System.currentTimeMillis() + ")" + msg.getText() );
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
    public List<GameMessage> getAllMessages( String playerName ) {

        QueryResults queryResults = currentGameSession.getQueryResults( "getAllPlayerMessages", playerName );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<GameMessage> messages = new ArrayList<GameMessage>();
        while ( iterator.hasNext() ) {
            GameMessage msg = ( GameMessage ) iterator.next().get( "$m" );
            messages.add( msg );
        }
        return messages;
    }

    @Override
    public void destroy() {
        if ( currentGameSession == null ) {
            throw new IllegalStateException( "0008 - Error: There is no game session to destroy!" );
        }
        gameMessageNotifications.close();
        currentGameSession.dispose();
        currentGameSession = null;
        currentGameContext = null;
        currentConfig = null;
        currentPlayers.clear();
        currentPlayers = null;
        executor = null;
        messageService = null;

    }

    @Override
    public List<String> getPlayers() {
        return new ArrayList<String>( currentPlayers.keySet() );
    }

    @Override
    public void drop( Player p ) {
        FactHandle playerFH = currentPlayers.remove( p.getName() );
        currentGameSession.delete( playerFH );
    }
    
    public Queue<Command> getCallbacks(){
        return callbackService.getCallbacks();
    }

}

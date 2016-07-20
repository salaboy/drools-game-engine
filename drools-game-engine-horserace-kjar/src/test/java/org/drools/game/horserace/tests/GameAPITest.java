
package org.drools.game.horserace.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.drools.game.core.api.GameMessage;
import org.drools.game.core.api.GameSession;
import org.drools.game.core.BaseGameConfigurationImpl;
import org.drools.game.core.BasePlayerConfigurationImpl;
import org.drools.game.core.CommandExecutorImpl;
import org.drools.game.core.GameCallbackServiceImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.core.GameSessionImpl;
import org.drools.game.core.api.Command;
import org.drools.game.core.api.GameCallbackService;
import org.drools.game.core.api.GameConfiguration;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.core.api.PlayerConfiguration;
import org.drools.game.horserace.cmds.EnterCheckpointCommand;
import org.drools.game.horserace.cmds.LeaveCheckpointCommand;
import org.drools.game.horserace.model.Checkpoint;

import org.drools.game.model.api.Player;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.rule.FactHandle;

/**
 *
 * @author salaboy
 *
 */
@RunWith( Arquillian.class )
public class GameAPITest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addClass( GameSessionImpl.class )
                .addClass( CommandExecutorImpl.class )
                .addClass( GameMessageService.class )
                .addClass( GameMessageServiceImpl.class )
                .addClass( GameCallbackService.class )
                .addClass( GameCallbackServiceImpl.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    private GameSession game;

    /*

     */
    @Test
    public void gameAPITest() {
        Player player = new BasePlayerImpl( "Morton Abenthy Halputz" );

        List initFacts = new ArrayList();
        
        Checkpoint startfinish = new Checkpoint("StartFinish", 0, true);
        initFacts.add( startfinish );
        Checkpoint checkpointone = new Checkpoint("CheckPointOne", 1);
        initFacts.add( checkpointone );
        Checkpoint checkpointtwo = new Checkpoint("CheckPointTwo", 2);
        initFacts.add( checkpointtwo );
        Checkpoint checkpointthree = new Checkpoint("CheckPointThree", 3);
        initFacts.add( checkpointthree );
        Checkpoint checkpointfour = new Checkpoint("CheckPointFour", 4);
        initFacts.add( checkpointfour );
        // Bootstrap the Game with the constructed house for this player
        GameConfiguration gameConfiguration = new BaseGameConfigurationImpl( initFacts, "" );

        game.bootstrap( gameConfiguration );

        PlayerConfiguration playerConfiguration = new BasePlayerConfigurationImpl( null );

        game.join( player, playerConfiguration );

        game.execute( new EnterCheckpointCommand( player, startfinish ) );
        game.execute( new LeaveCheckpointCommand(player, startfinish) );
        game.execute( new EnterCheckpointCommand( player, checkpointone ) );
        game.execute( new LeaveCheckpointCommand(player, checkpointone) );
        game.execute( new EnterCheckpointCommand( player, checkpointtwo ) );
        game.execute( new LeaveCheckpointCommand(player, checkpointtwo) );
        game.execute( new EnterCheckpointCommand( player, checkpointthree ) );
        game.execute( new LeaveCheckpointCommand(player, checkpointthree) );
        game.execute( new EnterCheckpointCommand( player, checkpointfour ) );
        game.execute( new LeaveCheckpointCommand(player, checkpointfour) );
        game.execute( new EnterCheckpointCommand( player, startfinish ) );
        game.execute( new LeaveCheckpointCommand(player, startfinish) );

        List<GameMessage> messages = game.getAllMessages( player.getName() );
        messages.addAll( game.getAllMessages( "system" ) );
        
        assertEquals( 25, messages.size() );

        Set<String> messageTexts = messages.stream().map( m -> m.getText() ).collect( Collectors.toSet() );
        
        for(String string : messageTexts)
        {
            System.out.println(string);
        }

        //TODO: I could not get this working. I verified that the output is all correct manually. No idea what is going on.
        assertThat( messageTexts,
                Matchers.containsInAnyOrder(
                        "Welcome, Morton Abenthy Halputz. Venture forth to the stables and mount a mighty steed!",
                        "You entered the checkpoint: CheckPointOne",
                        "You entered the checkpoint: CheckPointTwo",
                        "You entered the checkpoint: CheckPointThree",
                        "You entered the checkpoint: CheckPointFour",
                        "You entered the checkpoint: StartFinish",
                        "Contestant Morton Abenthy Halputz has crossed checkpoint 1",
                        "Contestant Morton Abenthy Halputz has crossed checkpoint 4",
                        "Contestant Morton Abenthy Halputz has crossed checkpoint 2",
                        "Contestant Morton Abenthy Halputz has crossed checkpoint 3",
                        "Contestant Morton Abenthy Halputz has crossed a the finish line!",

                        "You left the checkpoint: StartFinish",
                        "You left the checkpoint: CheckPointOne",
                        "You left the checkpoint: CheckPointTwo",
                        "You left the checkpoint: CheckPointThree",
                        "You left the checkpoint: CheckPointFour",
                        "Contestant Morton Abenthy Halputz has crossed a checkpoint!"
                        ) );

        Queue<Command> callbacks = game.getCallbacks();
        //assertEquals( 12, callbacks.size() );
        // TODO assert each callback in order with callbacks poll. Assert Type and Arguments of each callback
        game.drop( player );

        game.destroy();
    }
}

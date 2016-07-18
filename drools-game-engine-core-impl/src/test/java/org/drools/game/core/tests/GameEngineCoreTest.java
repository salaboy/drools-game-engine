
package org.drools.game.core.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.List;
import javax.inject.Inject;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.BaseGameConfigurationImpl;
import org.drools.game.core.BasePlayerConfigurationImpl;
import org.drools.game.core.CommandExecutorImpl;
import org.drools.game.core.GameCallbackServiceImpl;
import org.drools.game.core.GameMessageServiceImpl;
import org.drools.game.core.GameSessionImpl;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameCallbackService;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.core.api.GameSession;
import org.drools.game.model.api.Player;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class GameEngineCoreTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addClass( GameSession.class )
                .addClass( GameSessionImpl.class )
                .addClass(CommandExecutorImpl.class )
                .addClass( GameMessageService.class )
                .addClass( GameMessageServiceImpl.class )
                .addClass( GameCallbackService.class )
                .addClass( GameCallbackServiceImpl.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    private GameSession gameSession;

    @Test
    public void hello() {

        Player p = new BasePlayerImpl( "salaboy" );
        List initialData = null;

        gameSession.bootstrap( new BaseGameConfigurationImpl( initialData,
                "org.drools.game:drools-game-engine-core-kjar:1.0-SNAPSHOT:coreGameKBase" ) );

        gameSession.join( p, new BasePlayerConfigurationImpl( initialData ) );

        gameSession.execute( new MockCommand( p ) );
        
        

    }

    private static class MockCommand extends BaseCommand<List<Player>> {

        public MockCommand( Player player ) {
            super( player );
        }

        @Override
        public List<Player> execute( Context ctx ) {
            return null;
        }
    }
}

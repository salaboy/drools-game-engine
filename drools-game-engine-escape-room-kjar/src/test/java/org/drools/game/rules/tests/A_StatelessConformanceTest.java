package org.drools.game.rules.tests;

import java.util.ArrayList;
import javax.inject.Inject;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.House;
import org.drools.game.escape.room.model.house.Room;
import org.drools.game.model.api.Item;
import org.drools.game.escape.room.model.items.Key;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.KieSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 * @author salaboy
 * 
 * This tests represent "stateless" checks to validate the coherence of 
 * our model instances. Here we must define all the checks for making sure
 * that a house is consistent for creating a game
 * 
 * You can find the rules for these tests in: rules.conf/house-conformance-rules.drl
 * 
 */
@RunWith( Arquillian.class )
public class A_StatelessConformanceTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
//        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    @KBase( "confKBase" )
    private KieBase kBase;

    /*
     * A house with no rooms should return a warning
     */
    @Test
    public void houseWithNoRoomsTest() {

        KieSession kSession = kBase.newKieSession();

        ArrayList<String> errors = new ArrayList<String>();
        kSession.setGlobal( "errors", errors );
        House house = new House( "Mansion" );
        kSession.insert( house );

        int fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        assertEquals( 1, errors.size() );
        assertThat(errors, Matchers.contains("Warn: Your House ( " + house.getName() + " ) has no Rooms"));
        
        kSession.dispose();
        
    }

    /*
     * A room without a house containing it should return a warning
     * A room without doors should return a warning
     */
    @Test
    public void roomWithoutAHouseAndDoorsTest() {
        KieSession kSession = kBase.newKieSession();

        ArrayList<String> errors = new ArrayList<String>();
        kSession.setGlobal( "errors", errors );
        Room roomA = new Room( "Room A" );
        kSession.insert( roomA );

        int fired = kSession.fireAllRules();
        assertEquals( 2, fired );
        assertEquals( 2, errors.size() );
        assertThat(errors, Matchers.containsInAnyOrder("Warn: there is room ( " + roomA.getName() + " ) but there is no house", 
                "Warn: Your Room ( " + roomA.getName() + " ) has no Doors"));
        
        
        kSession.dispose();
    }

    /*
     * If there is a locked door in the house there must be a key to open that door
     */
    @Test
    public void lockedDoorWithNoKeyRulesTest() {
        KieSession kSession = kBase.newKieSession();

        ArrayList<String> errors = new ArrayList<String>();
        kSession.setGlobal( "errors", errors );
        House house = new House( "Mansion" );

        Room roomA = new Room( "Room A" );
        Door doorA = new Door( "Door A" );
        kSession.insert( doorA );
        roomA.getDoors().add( doorA );
        kSession.insert( roomA );
        house.getRooms().add( roomA );
        kSession.insert( house );

        int fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        assertEquals( 1, errors.size() );
        assertThat(errors, Matchers.contains("Warn: There is no key for this Door ( " + doorA.getName() + " )"));

        kSession.dispose();

    }

 
    
    /*
     * A correct house should have no warnings
     */
    @Test
    public void correctHouseTest() {
        KieSession kSession = kBase.newKieSession();

        ArrayList<String> errors = new ArrayList<String>();
        kSession.setGlobal( "errors", errors );
        House house = new House( "Mansion" );

        Room roomA = new Room( "Room A" );
        Key key = new Key( "Door A" );
        roomA.getItems().add( key );
        Door doorA = new Door( "Door A" );
        kSession.insert( doorA );
        roomA.getDoors().add( doorA );
        kSession.insert( roomA );
        house.getRooms().add( roomA );
        kSession.insert( house );
        
        // I need to insert all the items from the room
        //  This will be performed by rules on the game
        for(Item i : roomA.getItems()){
            kSession.insert( key );
        }

        int fired = kSession.fireAllRules();
        assertEquals( 0, fired );
        assertEquals( 0, errors.size() );

        kSession.dispose();
    }

}

package org.drools.game.rules.tests;

import java.util.ArrayList;
import org.drools.game.model.house.House;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 * 
 * This test shows how to bootstrap the Rule Engine in a no CDI environment.
 * Just plain JUnit is used to run this. 
 */
public class E_NoCDIRulesTest {

    @Test
    @Ignore
    public void helloDroolsTest() {

        System.out.println( "Bootstrapping the Rule Engine ..." );
        // Bootstrapping a Rule Engine Session
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase kBase = kContainer.getKieBase( "confKBase" );

        KieSession kSession = kBase.newKieSession();
        ArrayList<String> errors = new ArrayList<String>();
        kSession.setGlobal( "errors", errors );
        House house = new House( "Maniac Mansion" );
        kSession.insert( house );

        int fired = kSession.fireAllRules();
        assertEquals( 1, fired );
        assertEquals( 1, errors.size() );
        assertThat( errors, Matchers.contains( "Warn: Your House ( " + house.getName() + " ) has no Rooms" ) );

        kSession.dispose();

    }
}

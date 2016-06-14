/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.rules.tests;

import java.util.ArrayList;
import javax.inject.Inject;
import org.drools.workshop.model.house.House;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
@RunWith( Arquillian.class )
public class StatelessCDIConformanceTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create( JavaArchive.class )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        System.out.println( jar.toString( true ) );
        return jar;
    }

    @Inject
    @KBase(name = "confKBase")
    private KieBase kBase;

    public StatelessCDIConformanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() {

        // here do some rules to check rooms: 
        //  1 ) with no doors
        //  2 ) rooms with doors locked with no key to open it
        //  3...
        KieSession newKieSession = kBase.newKieSession();
        ArrayList<String> errors = new ArrayList<String>();
        newKieSession.setGlobal( "errors", errors );
        newKieSession.insert( new House( "Mansion" ) );
        
        int fired = newKieSession.fireAllRules();

        Assert.assertEquals( 3, errors.size() );
    }
}

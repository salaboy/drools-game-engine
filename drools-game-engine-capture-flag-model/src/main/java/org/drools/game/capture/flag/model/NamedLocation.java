/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

/**
 *
 * @author Samuel
 */
public class NamedLocation extends Location {

    private String name;

    public NamedLocation( int x, int y, int z, String name ) {
        super( x, y, z );
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NamedLocation{" + "name=" + name + '}';
    }
    
    

}

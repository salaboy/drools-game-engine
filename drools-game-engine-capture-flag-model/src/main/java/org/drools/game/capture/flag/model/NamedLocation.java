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

    public NamedLocation( String name ) {
        super( 0, 0, 0 );
        this.name = name;
    }

    public NamedLocation( String name, int x, int y, int z ) {
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

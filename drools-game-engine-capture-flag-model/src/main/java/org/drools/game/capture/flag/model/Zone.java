/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuel
 */
public class Zone {

    private String name;

    private Location lowerBound;
    private Location upperBound;

    private int dimension;

    private List<String> playersInZone = new ArrayList<>();

    private Zone() {
        this.dimension = 0;
    }

    public Zone( String name ) {
        this();
        this.name = name;
    }

    public Zone( String name, int x, int y, int z, int fx, int fy, int fz ) {
        this( name );
        lowerBound = new Location( Math.min( x, fx ), Math.min( y, fy ), Math.min( z, fz ) );
        upperBound = new Location( Math.max( x, fx ), Math.max( y, fy ), Math.max( z, fz ) );
    }

    public Location getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound( Location lowerBound ) {
        this.lowerBound = lowerBound;
    }

    public Location getUpperBound() {
        return upperBound;
    }

    public void setUpperBound( Location upperBound ) {
        this.upperBound = upperBound;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension( int dimension ) {
        this.dimension = dimension;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<String> getPlayersInZone() {
        return playersInZone;
    }

    public void setPlayersInZone( List<String> playersInZone ) {
        this.playersInZone = playersInZone;
    }

    public void addPlayer( String player ) {
        this.playersInZone.add( player );
    }

    public void removePlayer( String player ) {
        this.playersInZone.remove( player );
    }

    @Override
    public String toString() {
        return "Zone{" + "id=" + name + ", lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", dimension=" + dimension + ", playersInZone=" + playersInZone + '}';
    }
    
    
    @Override
    public boolean equals(Object obj) {
    	//TODO implement
    	return this.name.equals(((Zone)obj).getName());
    }
    
    
    @Override
    public int hashCode() {
    	//TODO implement
    	return name.hashCode();
    }

}

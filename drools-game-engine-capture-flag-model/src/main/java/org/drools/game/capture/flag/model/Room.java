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
public class Room {

    private String id;

    private Location lowerBound;
    private Location upperBound;

    private int dimension;

    private List<WorldItem> items;

    private List<String> playersInRoom = new ArrayList<>();

    private Room() {
        this.dimension = 0;
        items = new ArrayList<>();

    }

    public Room( String id ) {
        this();
        this.id = id;
    }

    public Room( int x, int y, int z, int fx, int fy, int fz, String id ) {
        this( id );
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

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void addItem( WorldItem item ) {
        if ( this.items == null ) {
            this.items = new ArrayList<>();
        }
        this.items.add( item );
    }

    public List<WorldItem> getItems() {
        return items;
    }

    public List<String> getPlayersInRoom() {
        return playersInRoom;
    }

    public void setPlayersInRoom( List<String> playersInRoom ) {
        this.playersInRoom = playersInRoom;
    }

    public void addPlayer( String player ) {
        this.playersInRoom.add( player );
    }

    public void removePlayer( String player ) {
        this.playersInRoom.remove( player );
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", dimension=" + dimension + ", items=" + items + ", playersInRoom=" + playersInRoom + '}';
    }
    
    

}

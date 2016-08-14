/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.horserace.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuel
 */
public class Checkpoint {

    private String id;
    private int order;
    private boolean isStartFinish;
    private List<String> players;
    private Location lowerBound;
    private Location upperBound;

    private Checkpoint() {
        players = new ArrayList<>();
    }

    public Checkpoint( String id, int order, int x, int y, int z, int fx, int fy, int fz, boolean isStartFinish ) {
        this( id, order, isStartFinish );
        lowerBound = new Location( Math.min( x, fx ), Math.min( y, fy ), Math.min( z, fz ) );
        upperBound = new Location( Math.max( x, fx ), Math.max( y, fy ), Math.max( z, fz ) );
    }

    public Checkpoint( String id, int order, int x, int y, int z, int fx, int fy, int fz ) {
        this( id, order, false );
        lowerBound = new Location( Math.min( x, fx ), Math.min( y, fy ), Math.min( z, fz ) );
        upperBound = new Location( Math.max( x, fx ), Math.max( y, fy ), Math.max( z, fz ) );
    }

    public Checkpoint( String id, int order ) {
        this();
        this.id = id;
        this.order = order;
        this.isStartFinish = false;
    }

    public Checkpoint( String id, int order, boolean isStartFinish ) {
        this();
        this.id = id;
        this.order = order;
        this.isStartFinish = isStartFinish;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers( List<String> players ) {
        this.players = players;
    }

    public void addPlayer( String player ) {
        this.players.add( player );
    }

    public void removePlayer( String player ) {
        this.players.remove( player );
    }

    public int getOrder() {
        return order;
    }

    public void setOrder( int order ) {
        this.order = order;
    }

    public boolean isIsStartFinish() {
        return isStartFinish;
    }

    public void setIsStartFinish( boolean isStartFinish ) {
        this.isStartFinish = isStartFinish;
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

    @Override
    public String toString() {
        StringBuilder playerString = new StringBuilder();
        playerString.append( "[" );
        for ( String str : players ) {
            playerString.append( str + ", " );
        }
        playerString.append( "]" );
        return "Checkpoint{" + "id=" + id + "; order=" + order + "; isStartFinish=" + isStartFinish + "; players = " + playerString + "}";
    }

}

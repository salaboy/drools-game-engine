/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dimension;
		result = prime * result + ((lowerBound == null) ? 0 : lowerBound.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((playersInZone == null) ? 0 : Arrays.hashCode(playersInZone.toArray()));
		result = prime * result + ((upperBound == null) ? 0 : upperBound.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Zone))
			return false;
		Zone other = (Zone) obj;
		if (dimension != other.dimension)
			return false;
		if (lowerBound == null) {
			if (other.lowerBound != null)
				return false;
		} else if (!lowerBound.equals(other.lowerBound))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (playersInZone == null) {
			if (other.playersInZone != null){
				return false;
		} else if (!Arrays.equals(playersInZone.toArray(), other.playersInZone.toArray()))
			return false;
		}
		if (upperBound == null) {
			if (other.upperBound != null)
				return false;
		} else if (!upperBound.equals(other.upperBound))
			return false;
		return true;
	}

	
}

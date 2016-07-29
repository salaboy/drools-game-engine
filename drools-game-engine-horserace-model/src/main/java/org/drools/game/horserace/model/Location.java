/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.horserace.model;

/**
 *
 * @author Samuel
 */
public class Location {

    private int x;
    private int y;
    private int z;
    private int dimension = 0;

    public Location( int x, int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ( int z ) {
        this.z = z;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension( int dimension ) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "Location{" + "x=" + x + ", y=" + y + ", z=" + z + ", dimension=" + dimension + '}';
    }

}
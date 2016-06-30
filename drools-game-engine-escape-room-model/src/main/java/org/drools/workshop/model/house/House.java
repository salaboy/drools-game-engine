/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.model.house;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class House {

    private String name;
    private List<Room> rooms;

    public House( String name ) {
        this.name = name;
        this.rooms = new ArrayList<Room>();
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms( List<Room> rooms ) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "House{" + "name=" + name + ", rooms=" + rooms + '}';
    }

}

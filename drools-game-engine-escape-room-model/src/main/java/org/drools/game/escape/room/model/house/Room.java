/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.escape.room.model.house;

import java.util.ArrayList;
import java.util.List;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.ItemContainer;

/**
 *
 * @author salaboy
 */
public class Room implements ItemContainer {

    private String name;
    private List<Door> doors;
    private List<String> peopleInTheRoom;
    private List<Item> itemsInTheRoom;
    private boolean lightsOn = true;
    private boolean open = true;

    public Room( String name ) {
        this.name = name;
        this.peopleInTheRoom = new ArrayList<String>();
        this.itemsInTheRoom = new ArrayList<Item>();
        this.doors = new ArrayList<Door>();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public void setDoors( List<Door> doors ) {
        this.doors = doors;
    }

    public List<String> getPeopleInTheRoom() {
        return peopleInTheRoom;
    }

    public void setPeopleInTheRoom( List<String> peopleInTheRoom ) {
        this.peopleInTheRoom = peopleInTheRoom;
    }

    @Override
    public List<Item> getItems() {
        return itemsInTheRoom;
    }

    public void setItems( List<Item> itemsInTheRoom ) {
        this.itemsInTheRoom = itemsInTheRoom;
    }

    public boolean isLightsOn() {
        return lightsOn;
    }

    public void setLightsOn( boolean lightsOn ) {
        this.lightsOn = lightsOn;
    }

    public List<Item> getItemsInTheRoom() {
        return itemsInTheRoom;
    }

    public void setItemsInTheRoom( List<Item> itemsInTheRoom ) {
        this.itemsInTheRoom = itemsInTheRoom;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen( boolean open ) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "Room{" + "name=" + name + ", doors=" + doors + ", peopleInTheRoom=" + peopleInTheRoom + ", itemsInTheRoom=" + itemsInTheRoom + ", lightsOn=" + lightsOn + '}';
    }

}

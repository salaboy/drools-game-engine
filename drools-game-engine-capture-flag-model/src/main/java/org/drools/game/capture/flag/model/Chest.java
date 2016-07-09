/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

import java.util.ArrayList;
import java.util.List;
import org.drools.game.model.api.Item;

/**
 *
 * @author salaboy
 */
public class Chest implements WorldItem {

    private String name;
    private List<Item> contents;
    private Location location;

    public Chest(String name) {
        this.name = name;
    }

    public Chest(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public List<Item> getContents() {
        return contents;
    }

    public void setContents(List<Item> content) {
        this.contents = content;
    }

    public void addItem(Item item) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add(item);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String getType() {
        return "item.chest";
    }

    @Override
    public String toString() {
        return "Chest{" + "name=" + name + ", content=" + contents + '}';
    }

}

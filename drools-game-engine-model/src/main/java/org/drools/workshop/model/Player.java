/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.model;

import java.util.ArrayList;
import java.util.List;
import org.drools.workshop.model.items.Item;

/**
 *
 * @author salaboy
 */
public class Player {

    private String name;
    private List<Item> items;

    public Player( String name ) {
        this.name = name;
        this.items = new ArrayList<Item>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems( List<Item> items ) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", items=" + items + '}';
    }

}

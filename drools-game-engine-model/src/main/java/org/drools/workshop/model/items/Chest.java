/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.model.items;

import org.drools.workshop.model.api.Item;
import org.drools.workshop.model.api.ItemContainer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Chest implements VisibleInDayLightItem, ItemContainer {

    private String name;
    private List<Item> items;
    private boolean open = false;

    public Chest() {
    }

    public Chest( String name ) {
        this();
        this.name = name;
        this.items = new ArrayList<Item>();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    public void setItems( List<Item> items ) {
        this.items = items;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen( boolean open ) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "Chest{" + "name=" + name + ", items=" + items + '}';
    }

}

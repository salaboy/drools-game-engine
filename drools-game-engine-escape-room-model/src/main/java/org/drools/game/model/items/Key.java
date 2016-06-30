/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.model.items;

import org.drools.game.model.api.Pickable;

/**
 *
 * @author salaboy
 */
public class Key implements VisibleInDayLightItem, Pickable {

    private String name;

    public Key() {
    }

    public Key( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Key{" + "name=" + name + '}';
    }

}

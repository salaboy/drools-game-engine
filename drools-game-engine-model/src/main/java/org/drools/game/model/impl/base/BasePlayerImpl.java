/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.model.impl.base;

import java.util.ArrayList;
import java.util.UUID;
import org.drools.game.model.api.Inventory;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;

/**
 *
 * @author salaboy
 */
public class BasePlayerImpl implements Player {

    private String id;

    private String name;

    private Inventory inventory;

    public BasePlayerImpl( String name ) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.inventory = new BaseInventoryImpl( this, new ArrayList<Item>() );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public void setInventory( Inventory inventory ) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "BasePlayerImpl{" + "id=" + id + ", name=" + name + ", inventory=" + inventory + '}';
    }

}

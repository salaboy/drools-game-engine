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

    public BasePlayerImpl() {
    }

    public BasePlayerImpl( String name ) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.inventory = new BaseInventoryImpl( this.id, new ArrayList<Item>() );
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BasePlayerImpl))
			return false;
		BasePlayerImpl other = (BasePlayerImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inventory == null) {
			if (other.inventory != null)
				return false;
		} else if (!inventory.equals(other.inventory))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


    

}

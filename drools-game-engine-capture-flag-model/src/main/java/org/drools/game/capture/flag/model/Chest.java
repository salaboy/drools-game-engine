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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contents == null) ? 0 : contents.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Chest))
			return false;
		Chest other = (Chest) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}

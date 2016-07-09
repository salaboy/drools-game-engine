/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.escape.room.model.house;

/**
 *
 * @author salaboy
 */
public class Door {

    private String name;
    private String leadsTo;
    private boolean open = false;
    private boolean locked = true;

    public Door() {
    }

    public Door( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getLeadsTo() {
        return leadsTo;
    }

    public void setLeadsTo( String leadsTo ) {
        this.leadsTo = leadsTo;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen( boolean open ) {
        this.open = open;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked( boolean locked ) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "Door{" + "name=" + name + ", leadsTo=" + leadsTo + ", open=" + open + ", locked=" + locked + '}';
    }
}

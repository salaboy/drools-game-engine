/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.model.items;

import java.util.List;

/**
 *
 * @author salaboy
 */
public interface ItemContainer {

    String getName();

    List<Item> getItems();

    boolean isOpen();

    void setOpen( boolean open );
}

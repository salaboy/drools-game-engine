/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.model.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 *
 * @author salaboy
 */
@JsonTypeInfo( use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT )
public interface ItemContainer {

    String getName();

    List<Item> getItems();

    boolean isOpen();

    void setOpen( boolean open );
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

import org.drools.game.model.api.Item;

/**
 *
 * @author salaboy
 */
public interface WorldItem extends Item {
    public String getType();
    public Location getLocation();
}

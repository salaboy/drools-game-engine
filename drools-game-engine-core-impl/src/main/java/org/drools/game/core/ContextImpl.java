/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core;

import java.util.HashMap;
import java.util.Map;
import org.drools.game.core.api.Context;

/**
 *
 * @author salaboy
 */
public class ContextImpl implements Context {

    private final Map<String, Object> data;

    public ContextImpl() {
        this.data = new HashMap<String, Object>();
    }

    public Map<String, Object> getData() {
        return data;
    }

}

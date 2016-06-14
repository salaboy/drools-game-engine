/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class Context {

    private final Map<String, Object> data;

    public Context() {
        this.data = new HashMap<String, Object>();
    }

    public Map<String, Object> getData() {
        return data;
    }

}

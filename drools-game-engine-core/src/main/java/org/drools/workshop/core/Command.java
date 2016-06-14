/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 *
 * @author salaboy
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Command<T> {
    public T execute(Context ctx);
}

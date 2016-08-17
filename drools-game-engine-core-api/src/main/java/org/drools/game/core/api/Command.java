/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import org.drools.game.model.api.Player;

/**
 * Represents a command used to interact with the model and/or
 * the world that model was meant to represent.
 * @author salaboy
 */
@JsonTypeInfo( use = CLASS, include = WRAPPER_OBJECT )
public interface Command<T> {

    /**
     * Executes the command given a context.
     * @param ctx
     * @return 
     */
    T execute( Context ctx );

    /**
     * Each command needs access to a player. Returns that player.
     * @return 
     */
    Player getPlayer();
}

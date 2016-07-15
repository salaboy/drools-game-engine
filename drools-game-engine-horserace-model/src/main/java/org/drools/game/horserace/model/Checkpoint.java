/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.horserace.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuel
 */
public class Checkpoint {

    private String id;
    private Checkpoint required;
    private List<String> playersCrossedCheckpoint = new ArrayList<>();

    private Checkpoint() {
        
    }

    public Checkpoint( String id , Checkpoint required) {
        this();
        this.id = id;
        this.required = required;
    }
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }
    
    public List<String> getPlayersCrossedCheckpoint() {
        return playersCrossedCheckpoint;
    }

    public void setPlayersCrossedCheckpoint( List<String> playersCrossedCheckpoint ) {
        this.playersCrossedCheckpoint = playersCrossedCheckpoint;
    }

    public void addPlayer( String player ) {
        this.playersCrossedCheckpoint.add( player );
    }

    public void removePlayer( String player ) {
        this.playersCrossedCheckpoint.remove( player );
    }

    public Checkpoint getRequired()
    {
        return required;
    }

    public void setRequired(Checkpoint required)
    {
        this.required = required;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + "}";
    }
    
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.capture.flag.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuel
 */
public class Team {

    private String name;
    private int points;
    private List<String> playersInTeam;

    public Team( String name ) {
        this.name = name;
        this.points = 0;
        this.playersInTeam = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints( int points ) {
        this.points = points;
    }

    public List<String> getPlayersInTeam() {
        return playersInTeam;
    }

    public void setPlayersInTeam( List<String> playersInTeam ) {
        this.playersInTeam = playersInTeam;
    }

    public void addPlayer( String playerName ) {
        if ( this.playersInTeam == null ) {
            this.playersInTeam = new ArrayList<String>();
        }
        this.playersInTeam.add( playerName );
    }

}

package org.drools.game.services.endpoint.impl;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

import org.drools.game.core.api.Command;
import org.drools.game.model.api.Player;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public class CommandRest<T> {
	private Command<T> command;
	
	public CommandRest() {
	}
	
	public CommandRest( Command<T> command) {
		this.command = command; 
	}

	
	public Command<T> getCommand() {
		return command;
	}
	


}

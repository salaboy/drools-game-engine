/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workshop.rules.cmds;

import org.drools.workshop.core.Command;
import org.drools.workshop.core.Context;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.items.ItemContainer;
import org.drools.workshop.rules.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.workshop.model.items.PickableItem;

public class PickItemCommand implements Command<Void> {

    private Player player;
    private PickableItem pickableItem;
    private ItemContainer container;

    public PickItemCommand() {
    }

    public PickItemCommand( Player player, ItemContainer container, PickableItem item ) {
        this.player = player;
        this.pickableItem = item;
        this.container = container;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        FactHandle playerFH = session.getFactHandle( player );
        player.getItems().add( pickableItem.getPickable() );
        FactHandle containerFH = session.getFactHandle( container );
        container.getItems().remove( pickableItem.getPickable() );
        session.update( playerFH, player );
        session.update( containerFH, container );
        session.insert( new GameMessage( "Item Picked! " + pickableItem ) );
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public PickableItem getPickableItem() {
        return pickableItem;
    }

    public ItemContainer getContainer() {
        return container;
    }

    public void setPlayer( Player player ) {
        this.player = player;
    }

    public void setPickableItem( PickableItem pickableItem ) {
        this.pickableItem = pickableItem;
    }

    public void setContainer( ItemContainer container ) {
        this.container = container;
    }
    
    
    

    @Override
    public String toString() {
        return "PickItemCommand{" + "player=" + player + ", item=" + pickableItem + ", container=" + container + '}';
    }

}

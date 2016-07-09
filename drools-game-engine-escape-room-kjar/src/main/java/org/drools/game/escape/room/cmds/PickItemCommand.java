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

package org.drools.game.escape.room.cmds;

import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.ItemContainer;
import org.drools.game.model.api.Player;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.game.escape.room.model.items.PickableItem;

public class PickItemCommand extends BaseCommand<Void> {

    private PickableItem pickableItem;
    private ItemContainer container;

    public PickItemCommand( Player player, ItemContainer container, PickableItem item ) {
        super( player );
        this.pickableItem = item;
        this.container = container;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );
        FactHandle playerFH = session.getFactHandle( getPlayer() );
        getPlayer().getInventory().getItems().add( pickableItem.getPickable() );
        FactHandle containerFH = session.getFactHandle( container );
        container.getItems().remove( pickableItem.getPickable() );
        session.update( playerFH, getPlayer() );
        session.update( containerFH, container );
        session.insert( messageService.newGameMessage( getPlayer().getName(), "Item Picked! " + pickableItem ) );
        return null;
    }

    public PickableItem getPickableItem() {
        return pickableItem;
    }

    public ItemContainer getContainer() {
        return container;
    }

    public void setPickableItem( PickableItem pickableItem ) {
        this.pickableItem = pickableItem;
    }

    public void setContainer( ItemContainer container ) {
        this.container = container;
    }

    @Override
    public String toString() {
        return "PickItemCommand{" + "player=" + getPlayer() + ", item=" + pickableItem + ", container=" + container + '}';
    }

}

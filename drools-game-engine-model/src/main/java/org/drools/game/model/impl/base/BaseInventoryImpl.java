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

package org.drools.game.model.impl.base;

import java.util.List;
import org.drools.game.model.api.Inventory;
import org.drools.game.model.api.Item;

public class BaseInventoryImpl implements Inventory {

    
    private String playerName;
    private List<Item> items;
    private String name;
    private boolean open = true;

    public BaseInventoryImpl() {
    }

    public BaseInventoryImpl( String playerName, List<Item> items ) {
        this.playerName = playerName;
        this.items = items;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName( String playerName ) {
        this.playerName = playerName;
    }

    public void setItems( List<Item> items ) {
        this.items = items;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen( boolean open ) {
        this.open = open;
    }

}

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

package org.drools.game.rules.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.house.Room;
import org.drools.game.model.api.ItemContainer;
import org.drools.game.model.api.Player;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class ExploreContainersCommand extends BaseCommand<List<ItemContainer>> {

    private Room room;


    public ExploreContainersCommand(Player player, Room room ) {
        super(player);
        this.room = room;
    }

    @Override
    public List<ItemContainer> execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );

        QueryResults queryResults = session.getQueryResults( "getAllContainers", ( Object ) null );
        Iterator<QueryResultsRow> iterator = queryResults.iterator();
        List<ItemContainer> itemContainers = new ArrayList<ItemContainer>();
        while ( iterator.hasNext() ) {
            ItemContainer itemContainer = ( ItemContainer ) iterator.next().get( "$ic" );
            itemContainers.add( itemContainer );
        }
        session.insert( messageService.newGameMessage( getPlayer().getName(), itemContainers.size() + " Item Containers available" ) );
        return itemContainers;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom( Room room ) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "ExploreContainersCommand{" + "room=" + room + '}';
    }

}

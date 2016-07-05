/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.core.tests.cmds;

import java.util.List;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.model.house.Room;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;

/**
 *
 * @author salaboy
 */
public class ExploreCommand extends BaseCommand<List<Item>> {

    public ExploreCommand( Player player ) {
        super( player );
    }

    @Override
    public List<Item> execute( Context ctx ) {
        Room localRoom = ( Room ) ctx.getData().get( "room" );
        return localRoom.getItems();
    }

}

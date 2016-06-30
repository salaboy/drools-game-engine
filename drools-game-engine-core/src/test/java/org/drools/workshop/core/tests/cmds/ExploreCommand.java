/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop.core.tests.cmds;

import java.util.List;
import org.drools.workshop.core.Context;
import org.drools.workshop.core.Command;
import org.drools.workshop.model.house.Room;
import org.drools.workshop.model.api.Item;

/**
 *
 * @author salaboy
 */
public class ExploreCommand implements Command<List<Item>> {

    @Override
    public List<Item> execute( Context ctx ) {
        Room localRoom = ( Room ) ctx.getData().get( "room" );
        return localRoom.getItems();
    }


}

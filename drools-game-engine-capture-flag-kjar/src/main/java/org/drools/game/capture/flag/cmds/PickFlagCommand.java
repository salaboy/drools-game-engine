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

package org.drools.game.capture.flag.cmds;

import org.drools.game.capture.flag.model.Flag;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.Player;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;


public class PickFlagCommand extends BaseCommand<Void> {

    private Flag flag;

    public PickFlagCommand() {
	
    }
    
    public PickFlagCommand( Player player, Flag flag ) {
        super( player );
        this.flag = flag;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );
        
        FactHandle playerFH = session.getFactHandle( getPlayer());

//        FactHandle playerFH = session.getFactHandles(new ObjectFilter() {
//			@Override
//			public boolean accept(Object object) {
//				if(object instanceof Player){
//					Player p = (Player)object;
//        			if(p.getName().equals(getPlayer().getName())){
//						return true;
//					}
//				}
//				return false;
//			}
//		}).iterator().next();

        getPlayer().getInventory().getItems().add( flag );
        session.update( playerFH, getPlayer() );
        session.insert( messageService.newGameMessage( getPlayer().getName(), "Picked the Flag" ) );
        return null;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag( Flag flag ) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "PickFlagCommand{" + "flag=" + flag + '}';
    }

   

}

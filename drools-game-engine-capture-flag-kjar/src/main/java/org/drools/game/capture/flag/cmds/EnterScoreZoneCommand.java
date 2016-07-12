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

import org.drools.game.capture.flag.model.Room;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.core.api.GameMessageService;
import org.drools.game.model.api.Player;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class EnterScoreZoneCommand extends BaseCommand<Void> {

    private Room scoreZone;

    public EnterScoreZoneCommand( Player player, Room scoreZone ) {
        super( player );
        this.scoreZone = scoreZone;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        GameMessageService messageService = ( GameMessageService ) ctx.getData().get( "messageService" );
        FactHandle scoreZoneFH = session.getFactHandle( scoreZone );
        scoreZone.addPlayer( getPlayer().getName() );
        session.update( scoreZoneFH, scoreZone );
        session.insert( messageService.newGameMessage( getPlayer().getName(), "You entered the Score Zone: " + scoreZone.getId() ) );
        return null;
    }

    public Room getScoreZone() {
        return scoreZone;
    }

    public void setScoreZone( Room scoreZone ) {
        this.scoreZone = scoreZone;
    }

    @Override
    public String toString() {
        return "EnterScoreZoneCommand{" + "scoreZone=" + scoreZone + '}';
    }

}

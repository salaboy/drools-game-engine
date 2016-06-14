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
import org.drools.workshop.model.items.LightSwitch;
import org.drools.workshop.rules.GameMessage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class TurnOffTheLightsCommand implements Command<Void> {

    private LightSwitch lightSwitch;

    public TurnOffTheLightsCommand() {
    }

    public TurnOffTheLightsCommand( LightSwitch lightSwitch ) {
        this.lightSwitch = lightSwitch;
    }

    @Override
    public Void execute( Context ctx ) {
        KieSession session = ( KieSession ) ctx.getData().get( "session" );
        FactHandle lightSwitchFH = session.getFactHandle( lightSwitch );
        lightSwitch.setOn( false );
        session.update( lightSwitchFH, lightSwitch );
        session.insert( new GameMessage( "Lights Turned Off " ) );
        return null;
    }

    public LightSwitch getLightSwitch() {
        return lightSwitch;
    }

    public void setLightSwitch( LightSwitch lightSwitch ) {
        this.lightSwitch = lightSwitch;
    }

    @Override
    public String toString() {
        return "TurnOffTheLightsCommand{" + "lightSwitch=" + lightSwitch + '}';
    }

}

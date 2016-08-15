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

package org.drools.game.services.endpoint.test.cmds;

import org.drools.game.capture.flag.model.Chest;
import org.drools.game.capture.flag.model.Flag;
import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.model.api.Player;

public class ResetFlagCommand extends BaseCommand<Void> {

    private Chest chest;
    private Flag flag;

    public ResetFlagCommand( Player player, Chest chest, Flag flag ) {
        super( player );
        this.chest = chest;
        this.flag = flag;
    }

    @Override
    public Void execute( Context ctx ) {

        return null;
    }

    public Chest getChest() {
        return chest;
    }

    public void setChest( Chest chest ) {
        this.chest = chest;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag( Flag flag ) {
        this.flag = flag;
    }

}

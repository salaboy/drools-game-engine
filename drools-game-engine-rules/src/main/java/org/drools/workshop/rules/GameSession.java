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

package org.drools.workshop.rules;

import java.util.List;
import org.drools.workshop.core.Command;
import org.drools.workshop.model.Player;
import org.drools.workshop.model.house.House;

public interface GameSession {

    void bootstrap( House house, Player player );

    void destroy();

    <T> T execute( Command<T> cmd );

    List<GameMessage> getAllMessages();

    List<Command> getSuggestions();

}

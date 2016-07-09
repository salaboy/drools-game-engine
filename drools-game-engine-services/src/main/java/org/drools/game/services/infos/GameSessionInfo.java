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

package org.drools.game.services.infos;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import java.util.List;

@JsonTypeInfo( use = CLASS, include = WRAPPER_OBJECT )
public class GameSessionInfo {

    private String id;
    private List<String> playerId;

    public GameSessionInfo() {
    }

    public GameSessionInfo( String id, List<String> playerId ) {
        this.id = id;
        this.playerId = playerId;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public List<String> getPlayers() {
        return playerId;
    }

    public void setPlayers( List<String> playerId ) {
        this.playerId = playerId;
    }

}

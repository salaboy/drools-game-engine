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

package org.drools.game.services;

import org.drools.game.services.endpoint.api.GameService;
import org.drools.game.services.endpoint.impl.GameServiceImpl;
import org.drools.game.services.infos.GameSessionInfo;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class App {

    public static void main( String[] args ) throws Exception {
        Container container = new Swarm();

        container.start();

        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );

        deployment.setContextRoot( "/api" );
        deployment.addClass( GameService.class );
        deployment.addClass( GameServiceImpl.class );
        deployment.addClass( GameSessionInfo.class );
        deployment.addAllDependencies();

        container.deploy( deployment );
    }
}

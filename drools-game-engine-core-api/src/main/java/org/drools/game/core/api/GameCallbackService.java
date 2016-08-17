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

package org.drools.game.core.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Queue;

/**
 * Stores callbacks and makes callbacks accessible to rules.
 * @author salaboy
 */
public interface GameCallbackService {

    /**
     * Queues a command into the callback service.
     * @param cmd 
     */
    void addCallback( Command cmd );

    /**
     * Returns queued callbacks.
     * @return 
     */
    Queue<Command> getCallbacks();
    
    

    static Command newCallback( String cmdName, Object... args ) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> forName = Class.forName( cmdName );
        
        Constructor<?>[] constructors = forName.getDeclaredConstructors();

        return ( Command ) constructors[0].newInstance( args );
    }

}

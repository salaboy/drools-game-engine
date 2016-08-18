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

package org.drools.game.capture.flag.model;

public class Flag implements WorldItem {

    private String name;
    private String type;
    private Location location;

    public Flag() {
	}
    
    public Flag( String name, String type ) {
        this.name = name;
        this.type = type;
    }

    public Flag( String name, String type, Location location ) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
    	//TODO implement
    	return this.name.equals(((Flag)obj).getName());
    }
    
    
    @Override
    public int hashCode() {
    	//TODO implement    	
    	return name.hashCode();
    }

    
}

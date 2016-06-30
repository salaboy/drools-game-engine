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

package org.drools.workshop.model.items;

public class LightBulb implements VisibleInDayLightItem {

    private String name;
    private boolean connected;
    private boolean broken;

    public LightBulb() {
    }

    public LightBulb( String name, boolean connected, boolean broken ) {
        this.name = name;
        this.connected = connected;
        this.broken = broken;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected( boolean connected ) {
        this.connected = connected;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken( boolean broken ) {
        this.broken = broken;
    }

    @Override
    public String toString() {
        return "LightBulb{" + "name=" + name + ", connected=" + connected + ", broken=" + broken + '}';
    }

    
}

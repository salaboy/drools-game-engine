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

public class LightSwitch implements VisibleInDayLightItem {

    private String name;
    private boolean on;

    public LightSwitch( String name, boolean on ) {
        this.name = name;
        this.on = on;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn( boolean on ) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "LightSwitch{" + "name=" + name + ", on=" + on + '}';
    }

}

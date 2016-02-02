/*
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestProfile {

    long duration;
    int instance;

    List<Cluster> devices = new ArrayList<>();
    List<Cluster> services = new ArrayList<>();

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public List<Cluster> getDevices() {
        return devices;
    }

    public void setDevices(List<Cluster> devices) {
        this.devices = devices;
    }

    public List<Cluster> getServices() {
        return services;
    }

    public void setServices(List<Cluster> services) {
        this.services = services;
    }

    public List<Driver> getDrivers() {
     List<Driver> drivers = new ArrayList<>();
     List<Cluster> templates = new ArrayList(devices);
     templates.addAll(services);
     templates.forEach(cluster -> {
         Driver template = cluster.getDriver();
         IntStream.range(0, cluster.getSize()).forEach(position -> {
             Driver driver = template.loadFromTemplate(cluster, instance, position);
             driver.init();
             drivers.add(driver);
         });
     });


     return drivers;
    }
}
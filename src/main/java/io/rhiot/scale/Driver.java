/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package io.rhiot.scale;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.rhiot.scale.device.MQTTTelemetryDevice;
import io.rhiot.scale.feature.Feature;
import io.rhiot.scale.service.MQTTConsumingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
              include = JsonTypeInfo.As.PROPERTY,
              property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MQTTTelemetryDevice.class, name = "mqtt-telemetry-device"),
    @JsonSubTypes.Type(value = MQTTConsumingService.class, name = "mqtt-consuming-service")
})
abstract public class Driver implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(Driver.class);

    protected String name;

    List<Feature> features = new ArrayList<Feature>();
    Transport transport;
    Result result = new Result();

    ExecutorService executorService;

    public Driver(String name) {
        this.name = name;
    }

    public abstract Driver loadFromTemplate(Cluster cluster, int position);

    public abstract void init();

    @Override
    public Void call() throws Exception {
        LOG.info(this + " started");
        if (transport != null) {
            transport.connect();
        }

        executorService = Executors.newFixedThreadPool(features.size());
        executorService.invokeAll(features);
        executorService.shutdown();

        if (transport != null) {
            transport.disconnect();
        }
        LOG.info(this + " stopped");
        return null;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Result getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void stop() {
        features.forEach(driver -> {
            driver.stop();
        });
        try {
            transport.disconnect();
        } catch (Exception e) {
            LOG.warn("Could not stop transport", e);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

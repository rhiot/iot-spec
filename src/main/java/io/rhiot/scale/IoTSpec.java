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

import io.rhiot.scale.device.MQTTTelemetryDevice;
import io.rhiot.scale.service.MQTTConsumingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IoTSpec {

    private static final Logger LOG = LoggerFactory.getLogger(IoTSpec.class);

    public static void main(String[] args) throws Exception {

        final List<Driver> drivers = new ArrayList();
        //TODO duration config
        LOG.info("Scale Test Started");
        //TODO load configuration
        //add devices
        for (int i = 0; i < 200; i++) {
            drivers.add(new MQTTTelemetryDevice("tcp://localhost:1883", "kura-" + i, "kura-" + i));
        }
        //add services
        drivers.add(new MQTTConsumingService("tcp://localhost:1883", "wiretap", "#"));


        // start drivers
        ExecutorService executorService = Executors.newFixedThreadPool(drivers.size());
        executorService.invokeAll(drivers, 5, TimeUnit.MINUTES);
        executorService.shutdownNow();

        drivers.forEach(driver -> {
            driver.stop();
            LOG.info("Driver " + driver);
            LOG.info("\t " + driver.getResult());
        });

        LOG.info("Scale Test Finished");

    }
}

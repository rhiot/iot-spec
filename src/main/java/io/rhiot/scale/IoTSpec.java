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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.rhiot.scale.device.MQTTTelemetryDevice;
import io.rhiot.scale.service.MQTTConsumingService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IoTSpec {

    private static final Logger LOG = LoggerFactory.getLogger(IoTSpec.class);
    private static final String CONFIG = "config";

    public static void main(String[] args) throws Exception {

        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption(Option.builder("c")
                .longOpt(CONFIG)
                .desc("Location of the test configuration file. A default value is 'src/main/resources/test.yaml' for easy IDE testing")
                .hasArg()
                .build());

        CommandLine line = parser.parse(options, args);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TestProfile test = mapper.readValue(new File(line.getOptionValue(CONFIG, "src/main/resources/test.yaml")), TestProfile.class);

        LOG.info("Scale Test Started");
        final List<Driver> drivers = test.getDrivers();
        ExecutorService executorService = Executors.newFixedThreadPool(drivers.size());
        executorService.invokeAll(drivers, test.getDuration(), TimeUnit.MILLISECONDS);
        executorService.shutdownNow();

        drivers.forEach(driver -> {
            driver.stop();
            LOG.info("Driver " + driver);
            LOG.info("\t " + driver.getResult());
        });

        LOG.info("Scale Test Finished");

    }
}

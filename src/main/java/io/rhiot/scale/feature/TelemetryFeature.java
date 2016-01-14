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
package io.rhiot.scale.feature;

import io.rhiot.scale.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryFeature extends Feature {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryFeature.class);

    public TelemetryFeature(Driver device) {
        super(device);
    }

    @Override
    public Void call() throws Exception {
        LOG.debug("Starting telemetry feature");
        for (int i = 0; i < 1000; i++) {
            device.getTransport().publish("test", ("test " + i).getBytes());
            device.getResult().published();
            Thread.sleep(50);
        }
        LOG.debug("Telemetry feature finished");
        return null;
    }

}

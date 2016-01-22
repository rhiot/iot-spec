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
package io.rhiot.spec.feature;

import io.rhiot.spec.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TelemetryFeature extends Feature {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryFeature.class);

    String topic;
    long delay = 500;
    int messageSize = 1024;
    int messageNumber = -1;

    public TelemetryFeature(Driver device, String topic) {
        super(device);
        this.topic = topic;
    }

    @Override
    public Void call() throws Exception {
        LOG.debug("Starting telemetry feature");
        Random rnd = new Random();
        byte[] message = new byte[messageSize];
        rnd.nextBytes(message);
        int count = 0;
        while (!stop || (messageNumber < 0) || count < messageNumber) {
            device.getTransport().publish(topic, message);
            device.getResult().published();
            Thread.sleep(delay);
            count++;
        }
        LOG.debug("Telemetry feature finished");
        return null;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}

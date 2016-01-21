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
import io.rhiot.spec.transport.CountListener;
import io.rhiot.spec.transport.LatchListener;

public class ConsumeFeature extends Feature {

    String topic;
    long timeout = -1;
    int messageNumber = -1;

    LatchListener listener;

    public ConsumeFeature(Driver device, String topic) {
        super(device);
        this.topic = topic;
    }

    @Override
    public Void call() throws Exception {
        listener = new LatchListener(messageNumber, timeout);
        device.getTransport().addListener(listener);
        device.getTransport().addListener(new CountListener(device.getResult()));
        device.getTransport().subscribe(topic);
        listener.await();
        return null;
    }

}

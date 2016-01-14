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
import io.rhiot.scale.transport.CountListener;
import io.rhiot.scale.transport.Listener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConsumeFeature extends Feature {

    Listener listener = new CountListener(device.getResult());
    CountDownLatch latch = new CountDownLatch(1000000);

    public ConsumeFeature(Driver device) {
        super(device);
    }

    @Override
    public Void call() throws Exception {
        device.getTransport().subscribe("#", listener);
        latch.await(2, TimeUnit.MINUTES);
        return null;
    }

}

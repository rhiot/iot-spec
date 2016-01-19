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
package io.rhiot.scale.device;

import io.rhiot.scale.Driver;
import io.rhiot.scale.Transport;
import io.rhiot.scale.feature.TelemetryFeature;
import io.rhiot.scale.transport.MQTTTransport;

public class MQTTTelemetryDevice extends Driver {

    String brokerURL;
    String dataTopic;

    public MQTTTelemetryDevice(String brokerURL, String name, String dataTopic) {
        super(name);
        this.brokerURL = brokerURL;
        this.dataTopic = dataTopic;
        Transport transport = new MQTTTransport(brokerURL, name);
        this.setTransport(transport);
        this.getFeatures().add(new TelemetryFeature(this, dataTopic));
    }

    @Override
    public String toString() {
        return "MQTTTelemetryDevice{" +
                "name='" + name + '\'' +
                '}';
    }
}

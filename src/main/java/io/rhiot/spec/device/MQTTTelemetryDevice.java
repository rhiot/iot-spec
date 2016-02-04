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
package io.rhiot.spec.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.rhiot.spec.Cluster;
import io.rhiot.spec.Driver;
import io.rhiot.spec.Transport;
import io.rhiot.spec.feature.TelemetryFeature;
import io.rhiot.spec.transport.MQTTFuseTransport;
import io.rhiot.spec.transport.MQTTPahoTransport;

@JsonRootName("mqtt-telemetry-device")
public class MQTTTelemetryDevice extends Driver {

    String brokerURL;
    String username;
    String password;
    String dataTopic;
    long delay = 500;

    @JsonCreator
    public MQTTTelemetryDevice(@JsonProperty("brokerURL")String brokerURL, @JsonProperty("name")String name, @JsonProperty("data-topic")String dataTopic) {
        super(name);
        this.brokerURL = brokerURL;
        this.dataTopic = dataTopic;
    }

    public MQTTTelemetryDevice(MQTTTelemetryDevice original) {
        this(original.getBrokerURL(), original.getName(), original.getDataTopic()) ;
    }

    @Override
    public void init() {
        MQTTPahoTransport transport = new MQTTPahoTransport(brokerURL, name);
        if (username != null) {
            transport.setUsername(username);
        }
        if (password != null) {
            transport.setPassword(password);
        }
        //Transport transport = new MQTTFuseTransport(brokerURL, name);
        this.setTransport(transport);
        TelemetryFeature telemetry = new TelemetryFeature(this, dataTopic);
        telemetry.setDelay(delay);
        this.getFeatures().add(telemetry);
    }

    @Override
    public Driver loadFromTemplate(Cluster cluster, int instance, int position) {
        MQTTTelemetryDevice result = new MQTTTelemetryDevice(this);
        result.setDelay(delay);
        // init device from cluster properties
        if (result.getName() == null && cluster.getName() != null) {
            result.setName(cluster.getName() + "-" + instance + "-" + position);
        }
        if (result.dataTopic == null && cluster.getName() != null) {
            result.setDataTopic(cluster.getName() + "-" + instance + "-" + position);
        }
        return result;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataTopic() {
        return dataTopic;
    }

    public void setDataTopic(String dataTopic) {
        this.dataTopic = dataTopic;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "MQTTTelemetryDevice{" +
                "brokerURL='" + brokerURL + '\'' +
                ", dataTopic='" + dataTopic + '\'' +
                ", delay=" + delay +
                ", name=" + name +
                '}';
    }
}

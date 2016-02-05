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

import io.rhiot.spec.Cluster;
import io.rhiot.spec.Driver;
import io.rhiot.spec.feature.TelemetryFeature;
import io.rhiot.spec.transport.QpidJMSTransport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Telemetry Device that communicates over AMQP
 */
@JsonRootName("amqp-telemetry-device")
public class AMQPTelemetryDevice extends Driver {

    String brokerURL;
    String username;
    String password;
    String dataDestination;
    long delay = 500;

    @JsonCreator
    public AMQPTelemetryDevice(@JsonProperty("brokerURL")String brokerURL, @JsonProperty("name")String name, @JsonProperty("data-destination")String dataDestination) {
        super(name);
        this.brokerURL = brokerURL;
        this.dataDestination = dataDestination;
    }

    public AMQPTelemetryDevice(AMQPTelemetryDevice original) {
        this(original.getBrokerURL(), original.getName(), original.getDataDestination()) ;
    }

    @Override
    public Driver loadFromTemplate(Cluster cluster, int instance, int position) {
        AMQPTelemetryDevice result = new AMQPTelemetryDevice(this);
        result.setDelay(delay);

        // Initialize device from cluster properties
        if (result.getName() == null && cluster.getName() != null) {
            result.setName(cluster.getName() + "-" + instance + "-" + position);
        }

        // Use the cluster properties, by default this will create a Topic destination.
        if (result.dataDestination == null && cluster.getName() != null) {
            result.setDataDestination(cluster.getName() + "-" + instance + "-" + position);
        }

        return result;
    }

    @Override
    public void init() {
        QpidJMSTransport transport = new QpidJMSTransport(brokerURL, name);

        if (username != null) {
            transport.setUsername(username);
        }
        if (password != null) {
            transport.setPassword(password);
        }

        setTransport(transport);

        TelemetryFeature telemetry = new TelemetryFeature(this, dataDestination);
        telemetry.setDelay(delay);

        getFeatures().add(telemetry);
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

    public String getDataDestination() {
        return dataDestination;
    }

    public void setDataDestination(String dataDestination) {
        this.dataDestination = dataDestination;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "AMQPTelemetryDevice{" +
                "brokerURL='" + brokerURL + '\'' +
                ", dataDestination='" + dataDestination + '\'' +
                ", delay=" + delay +
                ", name=" + name +
                '}';
    }
}

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
package io.rhiot.spec.service;

import io.rhiot.spec.Cluster;
import io.rhiot.spec.Driver;
import io.rhiot.spec.Transport;
import io.rhiot.spec.feature.ConsumeFeature;
import io.rhiot.spec.transport.QpidJMSTransport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("amqp-consuming-service")
public class AMQPConsumingService extends Driver {

    String brokerURL;
    String destination;
    String username;
    String password;

    @JsonCreator
    public AMQPConsumingService(@JsonProperty("brokerURL")String brokerURL, @JsonProperty("name")String name, @JsonProperty("destination")String destination) {
        super(name);
        this.brokerURL = brokerURL;
        this.destination = destination;
    }

    public AMQPConsumingService(AMQPConsumingService original) {
        this(original.getBrokerURL(), original.getName(), original.getDestination());
    }

    @Override
    public Driver loadFromTemplate(Cluster cluster, int instance, int position) {
        AMQPConsumingService result = new AMQPConsumingService(this);

        // init device from cluster properties
        if (result.getName() == null && cluster.getName() != null) {
            result.setName(cluster.getName() + "-" + position);
        }

        return result;
    }

    @Override
    public void init() {
        Transport transport = new QpidJMSTransport(brokerURL, name);
        this.setTransport(transport);
        this.getFeatures().add(new ConsumeFeature(this, destination));
    }

    @Override
    public String toString() {
        return "AMQPConsumingService{" +
                "brokerURL='" + brokerURL + '\'' +
                ", destination='" + destination + '\'' +
                ", name='" + name + '\'' +
                '}';
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

    public String getDestination() {
        return destination;
    }

    public void setTopic(String destination) {
        this.destination = destination;
    }
}

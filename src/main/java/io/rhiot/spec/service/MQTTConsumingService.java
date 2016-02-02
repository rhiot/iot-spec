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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.rhiot.spec.Cluster;
import io.rhiot.spec.Driver;
import io.rhiot.spec.Transport;
import io.rhiot.spec.feature.ConsumeFeature;
import io.rhiot.spec.transport.MQTTPahoTransport;

@JsonRootName("mqtt-consuming-service")
public class MQTTConsumingService extends Driver {

    String brokerURL;
    String topic;

    @JsonCreator
    public MQTTConsumingService(@JsonProperty("brokerURL")String brokerURL, @JsonProperty("name")String name, @JsonProperty("topic")String topic) {
        super(name);
        this.brokerURL = brokerURL;
        this.topic = topic;
    }

    public MQTTConsumingService(MQTTConsumingService original) {
        this(original.getBrokerURL(), original.getName(), original.getTopic());
    }

    @Override
    public void init() {
        Transport transport = new MQTTPahoTransport(brokerURL, name);
        this.setTransport(transport);
        this.getFeatures().add(new ConsumeFeature(this, topic));
    }

    @Override
    public Driver loadFromTemplate(Cluster cluster, int instance, int position) {
        MQTTConsumingService result = new MQTTConsumingService(this);
        // init device from cluster properties
        if (result.getName() == null && cluster.getName() != null) {
            result.setName(cluster.getName() + "-" + position);
        }
        return result;
    }

    @Override
    public String toString() {
        return "MQTTConsumingService{" +
                "brokerURL='" + brokerURL + '\'' +
                ", topic='" + topic + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}

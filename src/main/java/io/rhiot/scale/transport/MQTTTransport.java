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
package io.rhiot.scale.transport;

import io.rhiot.scale.Result;
import io.rhiot.scale.Transport;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQTTTransport implements Transport {

    private static final Logger LOG = LoggerFactory.getLogger(MQTTTransport.class);

    private MqttClient client;
    private String clientId;
    private String brokerUrl;

    public MQTTTransport(String brokerUrl, String clientId) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
    }

    @Override
    public void connect() throws Exception {
        client = new MqttClient(brokerUrl, clientId);
        //TODO handle options
        client.connect();
    }

    @Override
    public void disconnect() throws Exception {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void subscribe(String topic, Listener listener) throws Exception {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                listener.onMessage(s, mqttMessage.getPayload());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.subscribe(topic, 1);
    }

    @Override
    public void publish(String topic, byte[] message) throws Exception {
        client.publish(topic, message, 1, false);
    }

}

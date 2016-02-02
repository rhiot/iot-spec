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
package io.rhiot.spec.transport;

import io.rhiot.spec.Transport;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MQTTPahoTransport implements Transport {

    private static final Logger LOG = LoggerFactory.getLogger(MQTTPahoTransport.class);

    private MqttClient client;
    private String clientId;
    private String brokerUrl;
    private List<Listener> listeners = new ArrayList<Listener>();

    public MQTTPahoTransport(String brokerUrl, String clientId) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
    }

    @Override
    public void connect() throws Exception {
        client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        //TODO handle options
        client.connect();
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                listeners.forEach(listener -> {
                    listener.onMessage(s, mqttMessage.getPayload());
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public void disconnect() throws Exception {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void subscribe(String topic) throws Exception {
        client.subscribe(topic, 1);
    }

    @Override
    public void publish(String topic, byte[] message) throws Exception {
        client.publish(topic, message, 1, false);
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}

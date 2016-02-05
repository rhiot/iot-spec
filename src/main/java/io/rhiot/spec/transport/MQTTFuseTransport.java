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
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.util.ArrayList;
import java.util.List;

public class MQTTFuseTransport implements Transport {

    private String clientId;
    private String brokerUrl;
    private List<Listener> listeners = new ArrayList<Listener>();

    private final MQTT mqtt = new MQTT();
    private CallbackConnection connection;

    public MQTTFuseTransport(String brokerUrl, String clientId) {
        this.clientId = clientId;
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void connect() throws Exception {
        mqtt.setHost(brokerUrl);
        mqtt.setClientId(clientId);
        // shut off connect retry
        mqtt.setConnectAttemptsMax(0);
        mqtt.setReconnectAttemptsMax(0);
        connection = mqtt.callbackConnection();
        connection.resume();
        connection.connect(new EmptyCallback<>());
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer content, Runnable runnable) {
                listeners.forEach(listener -> {
                    listener.onMessage(topic.getData().toString(), content.getData().toString());
                });
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Override
    public void disconnect() throws Exception {
        if (connection != null) {
            connection.disconnect(new EmptyCallback<Void>());
        }
    }

    @Override
    public void subscribe(String destination) throws Exception {
        connection.subscribe(new Topic[]{new Topic(destination, QoS.AT_MOST_ONCE)}, new EmptyCallback<>());
    }

    @Override
    public void publish(String destination, byte[] message) throws Exception {
        connection.publish(destination, message, QoS.AT_MOST_ONCE, false, new EmptyCallback<Void>());
    }

    @Override
    public void addListener(Listener listener) {

    }

    class EmptyCallback<T> implements Callback<T> {

        @Override
        public void onSuccess(T value) {}

        @Override
        public void onFailure(Throwable value) {}
    }
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transport that wraps the Qpid JMS client connection.
 */
public class QpidJMSTransport implements Transport, MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(QpidJMSTransport.class);

    private static final String QUEUE_PREFIX = "queue://";
    private static final String TOPIC_PREFIX = "topic://";

    private String clientId;
    private String brokerURL;
    private String username;
    private String password;
    private List<Listener> listeners = new ArrayList<Listener>();

    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private Map<Destination, MessageConsumer> consumers = new HashMap<Destination, MessageConsumer>();

    public QpidJMSTransport(String brokerURL, String clientId) {
        this.brokerURL = brokerURL;
        this.clientId = clientId;
    }

    @Override
    public void connect() throws Exception {
        ConnectionFactory cf = new JmsConnectionFactory(brokerURL);

        if (username != null && !username.isEmpty()) {
            connection = cf.createConnection(username, password);
        } else {
            connection = cf.createConnection();
        }
        connection.setClientID(clientId);
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
    }

    @Override
    public void disconnect() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void subscribe(String destination) throws Exception {
        Destination target = convertDestination(destination);

        if (!consumers.containsKey(target)) {
            MessageConsumer consumer = session.createConsumer(target);
            consumer.setMessageListener(this);
            consumers.put(target, consumer);
        }
    }

    @Override
    public void publish(String destination, byte[] message) throws Exception {
        Destination target = convertDestination(destination);
        BytesMessage outbound = session.createBytesMessage();

        if (message != null && message.length > 0) {
            outbound.writeBytes(message);
        }

        producer.send(target, outbound);
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private Destination convertDestination(String destination) throws JMSException {

        Destination result = null;

        if (destination.startsWith(QUEUE_PREFIX)) {
            result = session.createQueue(destination.substring(QUEUE_PREFIX.length()));
        } else if (destination.startsWith(TOPIC_PREFIX)) {
            result = session.createTopic(destination.substring(TOPIC_PREFIX.length()));
        } else {
            result = session.createTopic(destination);
        }

        return result;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof BytesMessage) {
                Destination source = message.getJMSDestination();
                BytesMessage bytesMsg = (BytesMessage) message;

                byte[] payload = new byte[(int) bytesMsg.getBodyLength()];
                bytesMsg.readBytes(payload);

                listeners.forEach(listener -> {
                    listener.onMessage(source.toString(), payload);
                });
            } else {
                LOG.debug("Received message type we don't yet handle: {}", message);
            }

            // TODO - Handle other message types.

        } catch (Exception ex) {
            LOG.error("Error delivering incoming message to listeners: {}", ex.getMessage());
            LOG.trace("Error detail", ex);
        }
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
}

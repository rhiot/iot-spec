# IoT Spec

This project is a tool for integration and scalability testing of IoT server platforms (brokers, connectors, etc.). 
It consists of a framework for modeling different kinds of IoT devices and backend services which can be easily configured and
ran at scale against the platform. You can use it to test different server platforms, configurations and architectures and get 
a comparable results in terms of the scalability of the platform for the specific scenarios.

## Abstractions

Two main abstractions used in this test framework are **devices** and **services**. Both of them contains of a **transport** and a list of
**features** that uses the transport to send or receive the messages.

For example, `mqtt-telemetry-device`, we'll use in the later example, use `MQTT` transport and have a `Telemetry` feature that periodically send
messages over the transport. In the test we usually multiple **clusters** of similarly configured devices (as we will see soon). 

## Configuration

Here's the example configuration.

```yaml
duration: 300000

devices:
- name: kura
  size: 100
  driver:
    type: mqtt-telemetry-device
    brokerURL: tcp://localhost:1883

- name: sensor
  size: 100
  driver:
    type: mqtt-telemetry-device
    brokerURL: tcp://localhost:1883
    delay: 1000

services:
- driver:
    type: mqtt-consuming-service
    brokerURL: tcp://localhost:1883
    name: wiretap
    topic: '#'
```
    
It's a YAML formatted file, containing the following. The test duration (5 minutes in this example) and the list of devices and services clusters.
As you can see in this example, we have defined two clusters of 100 devices named kura and sensor.
    
## Running Test

At the moment, the test is built as executable jar, so it can be run from the command line 
    
    java -jar target/iot-spec-1.0-SNAPSHOT.jar -c src/main/resources/test.yaml
    
The only configuration option for now is the location of the configuration (`-c` or `--config`) 
We will provide more deployment options in the future.

## Modeling

The idea of the project is to create a framework that can be used to model various devices and services often found in IoT deployments.
For more information on how to write your own transports, features, devices and services please take look at the code, for example [MQTTTelemetryDevice class](src/main/java/io/rhiot/spec/device/MQTTTelemetryDevice.java)

More documentation on this topic is coming in the future.

## TODO

* Add more transports (AMQP, JMS, LwM2M, ...) and options (autentication, SSL, ...)
* Add more features and more options (request/reply, reconnecting, bursting, randomness, ...)
* Provide more programmable test execution, like don't start all devices at once, but add them linearly over time or in bursts
* Reporting - save test results in suitable formats (csv for example) for later analysis
* Deployment options - make tests deployable in different environments (Maven, Docker, Jenkins, ...)
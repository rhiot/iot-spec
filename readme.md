# IoT Spec

This project is a tool for integration and scalability testing of IoT server platforms (brokers, connectors, adapters, etc.). 
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

The test is built as an executable jar, so it can be run from the command line 
    
    java -jar target/iot-spec-1.0-SNAPSHOT.jar -c src/main/resources/test.yaml
    
You can specify location of the configuration file with the `-c` (or `--config`) option. 

## Scaling the test

The purpose of this test is to put a scale test to the server infrastructure. To do that, we want to create a lot of connections, which is impossible to do with a single process.
The main limit is the number of threads a process is allowed to create on your OS. Find that number and usually, the number of drivers you can run from a single process is 3 to 5
times less.

To overcome this, we should either start multiple test instances on a single machine or distribute them in the cloud environment. You can do that by starting the test multiple times, like 
 
    java -jar target/iot-spec-1.0-SNAPSHOT.jar -i 1 -c src/main/resources/test.yaml
    java -jar target/iot-spec-1.0-SNAPSHOT.jar -i 2 -c src/main/resources/test.yaml
    java -jar target/iot-spec-1.0-SNAPSHOT.jar -i 3 -c src/main/resources/test.yaml
             
It's recommended to use `-i` option to numerate instances of the running test, so that drivers and features can use it to define different client ids and destination names if needed.
There's an [example test script](bin/test) that can be used as a basis for scaling-out the test locally. It models MQTT telemetry-wiretap scenario, where we have 5 instances of the test running
telemetry devices and a single services that consumes everything.
 
More options to easily scale out tests locally or in the cloud are in the future plans. 

## Reporting

After running the test, you'd be interested in seeing how well the system performed. Currently, the report is saved in the CSV format showing how many messages every device has sent and received.
For example, the above mentioned MQTT telemetry-wiretap test could produce a report with the following snippet. 

    wiretap,0,461362
    kura-1-0,354,0
    kura-1-1,343,0
    kura-1-2,344,0
    ...

By default, the report is saved at `target/report.csv` but that can be changed with `-r` option.

## Modeling

The idea of the project is to create a framework that can be used to model various devices and services often found in IoT deployments.

To create new transports implement the [Transport interface](src/main/java/io/rhiot/Transport.java). See [MQTTPahoTransport](src/main/java/io/rhiot/transport/MQTTPahoTransport.java) for the reference.

To create new models of communication implement the [Feature interface](src/main/java/io/rhiot/feature/Feature.java). See [Telemetry](src/main/java/io/rhiot/feature/TelemetryFeature.java) or
[Consume](src/main/java/io/rhiot/feature/ConsumeFeature.java) features for the reference.

Finally, package it all up in a desired device or service, like [MQTTTelemetryDevice class](src/main/java/io/rhiot/spec/device/MQTTTelemetryDevice.java) for example.

## TODO

* Add more transports (AMQP, JMS, LwM2M, ...) and options (SSL, ...)
* Add more features and more options (request/reply, reconnecting, bursting, randomness, ...)
* Provide more programmable test execution, like don't start all devices at once, but add them linearly over time or in bursts
* Deployment options - make tests deployable in different environments (Maven, Docker, Jenkins, ...)
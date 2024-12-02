# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol

For the communication protocol in this project, we use TCP (Transmission Control Protocol) as the transport-layer protocol.  
Port Number(s): We use port 8080 for communication between nodes.  
Reasons for choosing TCP:
1. Reliability: TCP provides reliable data transfer by ensuring that all packets are delivered in the correct order and without errors. This is crucial for the accurate transmission of sensor data and control commands.
2. Connection-oriented: TCP establishes a connection before data transfer, which helps maintain a consistent communication state between nodes.
3. Error Checking: TCP includes error-checking mechanisms to detect and retransmit lost or corrupted packets.
4. Flow Control: TCP manages the rate of data transmission to prevent network congestion and ensure smooth communication.

These features make TCP a suitable choice for the reliable and orderly communication required in this distributed application.

## The architecture

The general architecture of the network for the distributed smart greenhouse application consists of the following components:

1. Sensor/Actuator Nodes: These nodes are responsible for sensing environmental data and controlling actuators. Each node has direct access to a set of sensors and actuators and is connected to the Internet. These nodes act as clients in the network.

2. Control Panel Nodes: These nodes provide a graphical user interface (GUI) for visualizing the status of sensor and actuator nodes and sending control commands to them. Control panel nodes also act as clients in the network.

3. Central Server: This server manages the communication between sensor/actuator nodes and control panel nodes. It receives data from sensor/actuator nodes and forwards it to control panel nodes. It also receives control commands from control panel nodes and forwards them to the appropriate sensor/actuator nodes.

```
+-------------------+       +-------------------+       +-------------------+
|                   |       |                   |       |                   |
| Sensor/Actuator   |       | Sensor/Actuator   |       | Sensor/Actuator   |
| Node 1            |       | Node 2            |       | Node 3            |
|                   |       |                   |       |                   |
+-------------------+       +-------------------+       +-------------------+
          |                           |                           |
          |                           |                           |
          +---------------------------+---------------------------+
                                      |
                                      |
                            +-------------------+
                            |                   |
                            |   Central Server  |
                            |                   |
                            +-------------------+
                                      |
                                      |
          +---------------------------+---------------------------+
          |                           |                           |
          |                           |                           |
+-------------------+       +-------------------+       +-------------------+
|                   |       |                   |       |                   |
| Control Panel     |       | Control Panel     |       | Control Panel     |
| Node 1            |       | Node 2            |       | Node 3            |
|                   |       |                   |       |                   |
+-------------------+       +-------------------+       +-------------------+
```
## The flow of information and events

### Sensor/Actuator Nodes
### Responsibilities:
* Periodically collect data from sensors.
* Send sensor data to the central server.
* Receive and execute commands from the central server to control actuators.

### Events:
* Periodic Sensor Data Collection: At regular intervals, the node collects data from its sensors and sends a SENSOR_DATA message to the central server.
* Incoming Actuator Commands: When the node receives an ACTUATOR_COMMAND message from the central server, it executes the command on the specified actuator and may send a status update back to the server.

### Control Panel Nodes
### Responsibilities:
* Provide a GUI for users to monitor sensor data and control actuators.
* Send control commands to the central server.
* Receive and display sensor data and actuator statuses from the central server.

### Events:
* User Interaction: When a user interacts with the GUI (e.g., turning an actuator on or off), the control panel node sends an ACTUATOR_COMMAND message to the central server.
* Incoming Sensor Data: The control panel node receives SENSOR_DATA messages from the central server and updates the GUI to reflect the current sensor readings.
* Incoming Actuator Status: The control panel node receives NODE_STATUS messages from the central server and updates the GUI to reflect the current status of actuators.

### Central Server
### Responsibilities:
* Manage communication between sensor/actuator nodes and control panel nodes.
* Forward sensor data from sensor/actuator nodes to control panel nodes.
* Forward control commands from control panel nodes to sensor/actuator nodes.

### Events:
* Receiving Sensor Data: When the central server receives a SENSOR_DATA message from a sensor/actuator node, it forwards the data to all connected control panel nodes.
* Receiving Actuator Commands: When the central server receives an ACTUATOR_COMMAND message from a control panel node, it forwards the command to the appropriate sensor/actuator node.
* Node Status Updates: The central server may also handle NODE_STATUS messages to keep track of the status of each node and forward relevant updates to control panel nodes.

## Connection and state

The communication protocol used in this project is connection-oriented and stateful.
* Connection-oriented: The protocol uses TCP (Transmission Control Protocol), which establishes a connection before data transfer and ensures reliable communication between nodes.
* Stateful: The protocol maintains the state of the connection and the communication context between nodes, ensuring that messages are delivered in the correct order and without errors.

## Types, constants


In the communication protocol, there are several specific value types used in multiple messages. These include:
#### Message Types
* SENSOR_DATA: Indicates a message containing sensor readings.
* ACTUATOR_COMMAND: Indicates a message containing a command for an actuator.
* NODE_STATUS: Indicates a message containing the status of a node.
* ERROR: Indicates a message containing an error report.

#### Common Fields
* Node ID: An integer representing the unique identifier of the node sending the message.
* Timestamp: A long integer representing the time the message was sent, in milliseconds since the epoch.

####  Payload Fields
* Sensor Data: A list of sensor readings, each containing:
  * sensorId: An integer representing the unique identifier of the sensor.
  * value: An integer representing the sensor reading.
* Actuator Command: Contains:
  * actuatorId: An integer representing the unique identifier of the actuator.
  * command: A string representing the command to be executed (e.g., "ON", "OFF").
* Node Status: Contains:
  * status: A string representing the status of the node (e.g., "ONLINE", "OFFLINE").
* Error: Contains:
  * errorCode: An integer representing the error code.
  * errorMessage: A string describing the error.
## Error Messages

We currently have exeption-handling. We will add error messages in the future.

## Message format

All messages in the protocol follow a common structure to ensure consistency and ease of parsing. The general format includes the following fields, separated by a delimiter (e.g., |):  
1. Message Type: A string indicating the type of the message (e.g., SENSOR_DATA, ACTUATOR_COMMAND).

2. Node ID: An integer representing the unique identifier of the node sending the message.

3. Timestamp: A long integer representing the time the message was sent, in milliseconds since the epoch.

4. Payload: A string containing the specific data relevant to the message type, with fields separated by the same delimiter.

## An example scenario

1. Sensor Node with ID=1 is Started
   * Sensors: 1 temperature sensor, 2 humidity sensors.
   * Actuators: Can open a window.
   * Action: Establishes a TCP connection to the central server.
2. Sensor Node with ID=2 is Started
   * Sensors: 1 temperature sensor.
   * Actuators: Can control 2 fans and a heater.
   * Action: Establishes a TCP connection to the central server.
3. Control Panel Node is Started
   * Action: Establishes a TCP connection to the central server.
4. Another Control Panel Node is Started
   * Action: Establishes a TCP connection to the central server.
5. Sensor Node with ID=3 is Started
   * Sensors: 2 temperature sensors.
   * Actuators: None.
   * Action: Establishes a TCP connection to the central server.
6. Sensor Nodes Broadcast Sensor Data
   * Time: After 5 seconds.
   * Packets Sent:
   * Node 1: SENSOR_DATA|1|<timestamp>|101,25|102,60|103,55
   * Node 2: SENSOR_DATA|2|<timestamp>|201,22
   * Node 3: SENSOR_DATA|3|<timestamp>|301,20|302,21
   * Action: Central server forwards sensor data to all connected control panel nodes.
7. User of First Control Panel Presses "ON" for First Fan of Node 2
   * Packet Sent: ACTUATOR_COMMAND|<control_panel_id>|<timestamp>|201,ON
   * Action: Central server forwards the command to Node 2.
   * Node 2 Reaction: Executes the command to turn on the first fan.
8. User of Second Control Panel Presses "Turn Off All Actuators"
   * Packet Sent: ACTUATOR_COMMAND|<control_panel_id>|<timestamp>|201,OFF|202,OFF|203,OFF
   * Action: Central server forwards the command to Node 2.
   * Node 2 Reaction: Executes the command to turn off all actuators (fans and heater).

## Reliability and security

1. Authentication: Nodes authenticate themselves to the central server using a pre-shared key or certificate-based authentication to ensure only authorized nodes can connect.

2. Encryption: All communication between nodes and the central server is encrypted using TLS (Transport Layer Security) to protect data from eavesdropping and tampering.  

3. Access Control: The central server enforces access control policies to ensure that only authorized control panel nodes can send commands to sensor/actuator nodes.  

4. Data Integrity: Messages include checksums or digital signatures to verify the integrity of the data and ensure it has not been altered during transmission.  

5. Logging and Monitoring: The central server logs all communication and monitors for suspicious activity, such as repeated failed authentication attempts or unusual message patterns, to detect and respond to potential security threats.
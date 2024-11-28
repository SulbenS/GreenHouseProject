# Communication Protocol

This document describes the protocol used for communication between the different nodes of the distributed application.

---

## Terminology

- **Sensor**: A device that senses the environment and provides a value (e.g., an integer value in this project).  
  Examples: temperature sensor, humidity sensor.

- **Actuator**: A device that influences the environment.  
  Examples: a fan, a window opener/closer, door opener/closer, heater.

- **Sensor and Actuator Node**: A computer that has direct access to a set of sensors and actuators and is connected to the Internet.

- **Control-Panel Node**: A device connected to the Internet that visualizes the status of sensor and actuator nodes and sends control commands to them.

- **Graphical User Interface (GUI)**: A graphical interface where users can interact with the system.

---

## The Underlying Transport Protocol

- **Protocol**: TCP (Transmission Control Protocol).
- **Port Number**: 12345.
- **Reason for Choosing TCP**:
    - TCP ensures reliable communication, maintaining the order of packets and preventing data loss.
    - Ideal for real-time state synchronization and consistent communication between nodes and clients.

---

## The Architecture

### Overview

- **Server**: The central hub that maintains the global state, processes commands, and broadcasts updates.
- **Clients**:
    - **Sensor and Actuator Nodes**: Devices that send sensor data to the server and receive actuator commands.
    - **Control-Panel Nodes**: Devices that display sensor and actuator statuses and allow users to send control commands.

### Diagram

![Network Architecture](network_architecture_placeholder.png)  
*(Replace with an actual diagram.)*

---

## The Flow of Information and Events

### Sensor and Actuator Nodes

1. **Startup**:
    - Connect to the server.
    - Send an initial state update.

2. **Periodic Events**:
    - Periodically send sensor data to the server.
    - React to incoming actuator commands by updating local actuators.

### Control-Panel Nodes

1. **Startup**:
    - Connect to the server.
    - Request and display the current state of all sensor/actuator nodes.

2. **User Actions**:
    - Send commands (e.g., turn actuators on/off) to the server when users interact with the GUI.

### Server

1. **Startup**:
    - Initialize the state for all connected nodes.
    - Accept connections from clients.

2. **Event Handling**:
    - Relay sensor updates from sensor/actuator nodes to all control-panel nodes.
    - Process commands from control-panel nodes and update the corresponding actuator states.

---

## Connection and State

- **Connection-Oriented Protocol**:  
  The communication is connection-oriented, relying on TCP to establish a persistent connection between clients and the server.

- **Stateful Communication**:  
  The server maintains the state of all connected nodes, including sensor readings and actuator statuses.

---

## Types and Constants

- **Message Types**:
    - `UPDATE`: Sent by the server to broadcast sensor data and actuator states to clients.
    - `COMMAND`: Sent by control-panel nodes to instruct sensor/actuator nodes to change actuator states.

- **Value Types**:
    - **Temperature**: A floating-point value in Celsius (e.g., `25.5`).
    - **Humidity**: A floating-point value in percentage (e.g., `60.0`).
    - **Actuator States**: Strings (`on` or `off`).

---

## Message Format

### General Format

- Messages are delimited by the `|` symbol.
- Format: `MESSAGE_TYPE|key=value|key=value|...`

### Message Types

1. **UPDATE**:  
   Sent by the server to broadcast the current state of a sensor/actuator node.  
 
        Format:   UPDATE|node_id=1|temperature=25.5|humidity=60.0|fan=on|window=off

        Example:  UPDATE|nodeId=1|temperature=25.50|humidity=50.00|heater=off|window=off|fan=on
   

2. **COMMAND**:  
   Sent by a control-panel node to instruct a specific actuator to change its state.

        Format:   COMMAND|nodeId=<id>|<actuator>=<state>

        Example:  COMMAND|nodeId=1|heater=on


---

## Error Messages

- **`ERROR|reason=<description>`**  
  Sent by the server or a node when a problem occurs.  
  
        Example:  ERROR|reason=Invalid node ID


---

## An Example Scenario

1. A sensor/actuator node with `ID=1` starts, featuring a temperature sensor, a humidity sensor, and a window actuator.
2. A sensor/actuator node with `ID=2` starts, featuring a temperature sensor, a fan, and a heater actuator.
3. A control-panel node starts and connects to the server.
4. Another control-panel node starts and connects to the server.
5. A sensor/actuator node with `ID=3` starts, featuring two temperature sensors but no actuators.
6. All three sensor/actuator nodes periodically send sensor data to the server.
7. The first control-panel node sends a `COMMAND` to turn the fan on for node `ID=2`.  
   
         Example:  COMMAND|nodeId=2|fan=on

8. The second control-panel node sends a `COMMAND` to turn off all actuators for node `ID=1`.  
   
         Example:  COMMAND|nodeId=1|heater=off|window=off

9. The server processes the commands and broadcasts the updated state of the nodes to all clients.

---

## Reliability and Security

- **Reliability**:
- TCP ensures reliable data delivery and correct ordering of messages.
- The server broadcasts periodic updates to maintain consistent state across clients.

- **Security**:
- No authentication is currently implemented (optional future improvement).
- Network communication could be encrypted using TLS to prevent eavesdropping or tampering.

---

This protocol ensures seamless interaction between all nodes, maintaining state synchronization and providing a robust foundation for the distributed system.





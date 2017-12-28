package nodetestingstuff;

import java.util.ArrayList;
import java.util.HashMap;

/*
Simulates certain interactions that would otherwise be handled by real life. All
interactions with the outside world are handled over this static class.
*/
public class Simulator {
    public static ArrayList<SimulatedNode> simulatedNodes = new ArrayList<>();
    // Maps are used for quicker look-up, since look-up is frequent
    private static HashMap NodeUUIDMap = new HashMap();
    private static HashMap AntennaUUIDMap = new HashMap();
    
    // Strength threshold at which signals will be "detectable"
    private static final double REQUIRED_SIGNAL_STRENGTH = 0;
    
    // Adds a node and its antennas into the maps
    public static void register(SimulatedNode node) {
        NodeUUIDMap.put(node.node.UUID, node);
        
        ArrayList<Antenna> antennas = node.node.antennas;
        for (int i = 0; i < antennas.size(); i++) {
            AntennaUUIDMap.put(antennas.get(i).UUID, antennas.get(i));
        }
    }
    
    public static SimulatedNode getSimulatedNodeByUUID(String UUID) {
        return (SimulatedNode) NodeUUIDMap.get(UUID);
    }
    
    public static Antenna getAntennaByUUID(String UUID) {
        return (Antenna) AntennaUUIDMap.get(UUID);
    }
    
    // Returns an array of all the visible access points from the standpoint of a given antenna
    public static ArrayList<AntennaSignal> getDetectableSignals(Antenna antenna) {
        ArrayList<AntennaSignal> detectableSignals = new ArrayList<>();

        SimulatedNode referenceNode = Simulator.getSimulatedNodeByUUID(antenna.connectedNode.UUID);
        
        for (int i = 0; i < Simulator.simulatedNodes.size(); i++) {
            SimulatedNode otherNode = Simulator.simulatedNodes.get(i);

            for (int j = 0; j < otherNode.node.antennas.size(); j++) {
                Antenna otherAntenna = otherNode.node.antennas.get(j);

                if (otherAntenna.isOn() && otherAntenna.isAccessPoint() && Simulator.isVisibleAntenna(otherAntenna, antenna)) {
                    double signalStrength = Simulator.getSignalStrength(otherAntenna, antenna);

                    if (signalStrength >= Simulator.REQUIRED_SIGNAL_STRENGTH) {
                        detectableSignals.add(new AntennaSignal(signalStrength, otherAntenna.UUID));
                    }
                }
            }           
        }
        
        return detectableSignals;
    }
    
    public static void addNode(SimulatedNode node) {
        Simulator.simulatedNodes.add(node);
    }
    
    // Removes node and clears all maps
    public static void deleteNode(SimulatedNode node) {
        Simulator.simulatedNodes.remove(node);
        node.node.stop(); // Stops thread
        
        NodeUUIDMap.remove(node.node.UUID); // Remove UUID from map
        
        ArrayList<Antenna> antennas = node.node.antennas;
        for (int i = 0; i < antennas.size(); i++) { // Remove antenna UUIDs
            antennas.get(i).setPowerState(0); // Disconnects 'em
            AntennaUUIDMap.remove(antennas.get(i).UUID);
        }
    }
    
    // Calculates the received signal strength of another antenna from the standpoint of an antenna
    private static double getSignalStrength(Antenna transmitter, Antenna receiver) {
        SimulatedNode node0 = Simulator.getSimulatedNodeByUUID(transmitter.connectedNode.UUID);
        SimulatedNode node1 = Simulator.getSimulatedNodeByUUID(receiver.connectedNode.UUID);
        
        double distance = Math.hypot(node0.x - node1.x, node0.y - node1.y);
        
        return transmitter.strength / Math.pow(distance, 2); // Quadratic falloff
    }
    private static boolean isVisibleAntenna(Antenna transmitter, Antenna receiver) {
        return Simulator.getSignalStrength(transmitter, receiver) >= Simulator.REQUIRED_SIGNAL_STRENGTH;
    }
    
    // When client tries to connect to access point
    public static void requestConnection(Antenna transmitter, String receiverUUID) {
        Antenna receiver = Simulator.getAntennaByUUID(receiverUUID);
        
        if (receiver != null && Simulator.isVisibleAntenna(transmitter, receiver)) {
            receiver.receiveConnectionRequest(transmitter.UUID);
        }
    }
    // When access point accepts said requested connection
    public static void acceptConnectionRequest(Antenna transmitter, String receiverUUID) {
        Antenna receiver = Simulator.getAntennaByUUID(receiverUUID);
        
        if (receiver != null && Simulator.isVisibleAntenna(transmitter, receiver)) {
            receiver.receiveConnectionAccept(transmitter.UUID);
        }
    }
    // Connects client and access point
    public static void connectToAccessPoint(Antenna client, String accessPointUUID) {
        Antenna accessPoint = Simulator.getAntennaByUUID(accessPointUUID);
        
        if (accessPoint.isAccessPoint() && !client.isAccessPoint()) {
            client.connectToAccessPoint(accessPointUUID);
            accessPoint.connectToClient(client.UUID);
            
            if (client.isOn()) client.fireAntennaEvent(new AntennaEvent(client, "onAccessPointConnect", null, accessPointUUID));
            // Checking if client is still connected, could be that client instantly disconnected after connection was established
            if (client.isOn() && client.connectedAccessPointUUID != null && client.connectedAccessPointUUID.equals(accessPointUUID)) {
                if (accessPoint.isOn()) accessPoint.fireAntennaEvent(new AntennaEvent(accessPoint, "onClientConnect", null, client.UUID));
            }
        }
    }
    // Used to send a string from one antenna to another, if they are connected
    public static void transmitMessage(Antenna transmitter, String receiverUUID, String message) {
        Antenna receiver = Simulator.getAntennaByUUID(receiverUUID);
        
        if (receiver != null && Simulator.isVisibleAntenna(transmitter, receiver) && transmitter.isConnectedTo(receiverUUID)) {
            receiver.receiveMessage(transmitter.UUID, message);
        }
    }
    // Causes disconnection of an antenna
    public static void killConnections(Antenna antenna) {
        if (antenna.isAccessPoint()) {
            for (int i = 0; i < antenna.connectedClientUUIDs.size(); i++) {
                Simulator.getAntennaByUUID(antenna.connectedClientUUIDs.get(i)).disconnectAntenna(antenna.UUID);
            }
        } else {
            if (antenna.connectedAccessPointUUID != null) {
                Simulator.getAntennaByUUID(antenna.connectedAccessPointUUID).disconnectAntenna(antenna.UUID);
            }
        }
    }
}
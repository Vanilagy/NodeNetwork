package nodetestingstuff;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.EventListenerList;

interface AntennaEventListener extends EventListener {
    public void antennaEventOccurred(AntennaEvent evt);
}

class AntennaEvent extends EventObject {
    public String type;
    public String transmitterUUID;
    public String data;
    
    /*
    Possible types:
    
    onConnectionRequest - fires when antenna receives a connection request
    onAccessPointConnect - fires when antenna connects to an access point
    onClientConnect - fires when antenna connects to a client
    onMessage - fires when antenna receices a message
    onDisconnect - fires when a connected antenna (either client or access point) loses connection
    */
    
    public AntennaEvent(Object source, String type, String transmitterUUID, String data) {
        super(source);
        this.type = type;
        this.transmitterUUID = transmitterUUID;
        this.data = data;
    }
}

public class Antenna {
    public String UUID;
    public Node connectedNode;
    public double strength;
    private boolean isAccessPoint = false;
    private boolean isOn = false;
    
    public String connectedAccessPointUUID;
    protected String connectedAccessPointNodeUUID;
    public ArrayList<String> connectedClientUUIDs = new ArrayList<>();
    private String connectionRequestUUID;
    
    // Event stuff starts here
    protected EventListenerList listenerList = new EventListenerList();
    
    public void addAntennaEventListener(AntennaEventListener listener) {
        listenerList.add(AntennaEventListener.class, listener);
    }
    public void removeAntennaEventListener(AntennaEventListener listener) {
        listenerList.remove(AntennaEventListener.class, listener);
    }
    
    public void fireAntennaEvent(AntennaEvent evt) {
        Object[] listeners = listenerList.getListenerList();
    
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AntennaEventListener.class) {
                ((AntennaEventListener) listeners[i+1]).antennaEventOccurred(evt);
            }
        }
    }
    
    public Antenna(Node connectedNode, double strength, int mode) {
        this.UUID = Utils.getRandomHexString(16);
        this.connectedNode = connectedNode;
        this.strength = strength;
        this.setMode(mode);
    }
    
    public boolean isAccessPoint() {
        return this.isAccessPoint;
    }
    public void setMode(int mode) {
        int powerStateBefore = this.isOn() ? 1 : 0;
        
        this.setPowerState(0); // Done for resetting stuff
        this.setPowerState(powerStateBefore);
        
        this.isAccessPoint = mode == 1;
    }
    public boolean isOn() {
        return this.isOn;
    }
    public void setPowerState(int state) {
        if (state == 1) {
            this.isOn = true;
        } else if (state == 0) {
            // Make sure antenna closes open connections
            Simulator.killConnections(this);
            
            // Reset variables
            this.isOn = false;
            this.connectedAccessPointUUID = null;
            this.connectedAccessPointNodeUUID = null;
            this.connectedClientUUIDs = new ArrayList<>();
            this.connectionRequestUUID = null;
        }
    }
    
    // Returns a list of all the other nodes' antennas' signals which this antenna can detect
    public ArrayList<AntennaSignal> scanSignals() {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                return Simulator.getDetectableSignals(this);
            } else {
                throw new java.lang.RuntimeException("Can't scan for signals as an access point");
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }   
    }
    
    // These two methods are used for connecting antennas
    public void requestConnection(String antennaUUID) {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                connectionRequestUUID = antennaUUID;
                Simulator.requestConnection(this, antennaUUID);
            } else {
                throw new java.lang.RuntimeException("Can only request connection as client");
            }   
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }  
    }
    public void acceptConnectionRequest(String antennaUUID) {
        if (this.isOn()) {
            if (this.isAccessPoint()) {
                Simulator.acceptConnectionRequest(this, antennaUUID);
            } else {
                throw new java.lang.RuntimeException("Can only accept requests as access point");
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }   
    }
    // Return true if antenna is connected to given other antenna
    public boolean isConnectedTo(String UUID) {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                return (this.connectedAccessPointUUID != null) ? this.connectedAccessPointUUID.equals(UUID) : false;
            } else {
                for (int i = 0; i < this.connectedClientUUIDs.size(); i++) {
                    if (this.connectedClientUUIDs.get(i).equals(UUID)) {
                        return true;
                    }
                }

                return false;
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }
    }
    // These two methods handle sending messages
    public void sendToAccessPoint(String message) {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                if (this.connectedAccessPointUUID != null) {
                    Simulator.transmitMessage(this, connectedAccessPointUUID, message);
                } else {
                    throw new java.lang.RuntimeException("Tried to send message as client; not connected to an access point");
                }
            } else {
                throw new java.lang.RuntimeException("Can't send message to access point as access point");
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }  
    }
    public void sendToClient(String clientUUID, String message) {
        if (this.isOn()) {
            if (this.isAccessPoint()) {
                for (int i = 0; i < this.connectedClientUUIDs.size(); i++) {
                    if (this.connectedClientUUIDs.get(i).equals(clientUUID)) {
                        Simulator.transmitMessage(this, clientUUID, message);
                        break;
                    }

                    if (i == this.connectedClientUUIDs.size()) {
                        throw new java.lang.RuntimeException("Not connected to antenna " + clientUUID + ", can't send message");
                    }
                }
            } else {
                throw new java.lang.RuntimeException("Can't send message to client as client!");
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }    
    }
    // Disconnects given UUID from antenna
    public void disconnectAntenna(String UUID) {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                if (this.connectedAccessPointUUID.equals(UUID)) {
                    this.connectedAccessPointUUID = null;
                    this.connectedAccessPointNodeUUID = null;

                    fireAntennaEvent(new AntennaEvent(this, "onDisconnect", UUID, null));
                }
            } else {
                for (int i = 0; i < this.connectedClientUUIDs.size(); i++) {
                    if (this.connectedClientUUIDs.get(i).equals(UUID)) {
                        this.connectedClientUUIDs.remove(i);
                        i--;

                        fireAntennaEvent(new AntennaEvent(this, "onDisconnect", UUID, null));
                    }
                }
            }
        } else {
            throw new java.lang.RuntimeException("Antenna not powered on");
        }  
    }
    
    /*
    ********************************************************
        Following methods should ONLY be called by Simulator and not by the Node's process
    ********************************************************
    */
    public void receiveConnectionRequest(String antennaUUID) {
        if (this.isOn()) {
            fireAntennaEvent(new AntennaEvent(this, "onConnectionRequest", antennaUUID, null));
        }
    }
    public void receiveConnectionAccept(String antennaUUID, String antennaNodeUUID) {  
        if (this.isOn()) {
            if (antennaUUID.equals(connectionRequestUUID)) {
                Simulator.connectToAccessPoint(this, antennaUUID, antennaNodeUUID);
                connectionRequestUUID = null;
            }
        } 
    }
    public void connectToAccessPoint(String accessPointUUID, String accessPointNodeUUID) {
        if (this.isOn()) {
            if (!this.isAccessPoint()) {
                connectedAccessPointUUID = accessPointUUID;
                connectedAccessPointNodeUUID = accessPointNodeUUID;
            } else {
                throw new java.lang.RuntimeException("Tried to connect to access point as access point");
            }
        }  
    }
    public void connectToClient(String clientUUID) {
        if (this.isOn()) {
            if (this.isAccessPoint()) {
                connectedClientUUIDs.add(clientUUID);
            } else {
                throw new java.lang.RuntimeException("Tried to connect to client as client");
            }
        }    
    }
    public void receiveMessage(String senderUUID, String message) {
        if (this.isOn()) {
            fireAntennaEvent(new AntennaEvent(this, "onMessage", senderUUID, message));
        }
    }
    /*
    ********************************************************
        Ends here
    ********************************************************
    */
    
    @Override
    public String toString() {
        String output = "";
        
        output += "Power: " + ((this.isOn()) ? "On" : "Off") + "\n";
        output += "Strength: " + this.strength + "\n";
        output += "Mode: " + ((this.isAccessPoint()) ? "Access point" : "Client") + "\n";
        if (this.isAccessPoint()) {
            output += "Connected clients: " + this.connectedClientUUIDs.size() + "\n";
        } else {
            output += "Connected: " + (this.connectedAccessPointUUID != null) + "\n";
        }
        
        return output;
    }
}
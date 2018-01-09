package nodetestingstuff;

import java.util.ArrayList;

public class Node extends Thread {
    // Stores (hopefully) unique identified for this node (randomly generated)
    public String UUID;
    public ArrayList<Antenna> antennas = new ArrayList<>();
    protected ArrayList<String> antennaUUIDs = new ArrayList<>();
    
    public Node() {
        this.UUID = Utils.getRandomHexString(16);
        
        // Adds an antenna
        this.antennas.add(new Antenna(this, 100, 0));
        this.antennas.add(new Antenna(this, 100, 0));
        
        for(int i = 0; i<antennas.size(); i++) {
            antennaUUIDs.add(antennas.get(i).UUID);
        }
    }
    
    @Override // Main thread of the node
    public void run() {
        /*
        TEMPORARY:
        If no access points are found, make yourself one. Otherwise connect to the one that's found. An access point
        shuts off once it's connected to six clients. The process will repeat with the node placed after that.
        */
        
        for(int g = 0; g<antennas.size(); g++) {
            Antenna antenna = antennas.get(g);
            antenna.setPowerState(1);

            ArrayList<AntennaSignal> signals = antennas.get(g).scanSignals();
            ArrayList<AntennaSignal> singalsToRemove = new ArrayList<>();
            for(int k = 0; k<signals.size(); k++) {
                AntennaSignal signal = signals.get(k);
                for(int l = 0; l<antennas.size(); l++) {
                    if(signal.transmitterUUID.equals(antennas.get(l).UUID)) singalsToRemove.add(signal);
                    else if(signal.transmitterUUID.equals(antennas.get(l).connectedAccessPointUUID)) singalsToRemove.add(signal);
                    else if(signal.transmitterNodeUUID.equals(antennas.get(l).connectedAccessPointNodeUUID)) singalsToRemove.add(signal);
                }
            }
            while(!singalsToRemove.isEmpty()) {
                signals.remove(singalsToRemove.get(0));
                singalsToRemove.remove(0);
            }
            if (signals.isEmpty()) {
                System.out.println("Server node opened up!");

                antenna.setMode(1);
                antenna.addAntennaEventListener((AntennaEvent evt) -> {
                    if (evt.type.equals("onConnectionRequest")) {
                        antenna.acceptConnectionRequest(evt.transmitterUUID);
                    } else if (evt.type.equals("onClientConnect")) {
                        System.out.println("Connected to a new client innit!");

                        if (antenna.connectedClientUUIDs.size() > 10) antenna.setPowerState(0);
                        if (antenna.connectedClientUUIDs.size() > 4) {
                            for (int i = 0; i < antenna.connectedClientUUIDs.size(); i++) {
                                antenna.sendToClient(antenna.connectedClientUUIDs.get(i), "BOYS I AM FAMOUS!");
                            }
                        }
                    } else if (evt.type.equals("onMessage")) {
                        // TODO: Add message handling
                    } else if (evt.type.equals("onDisconnect")) {
                        System.out.println("Client disconnected! " + antenna.connectedClientUUIDs.size());
                    }
                });
            } else {
                antenna.addAntennaEventListener((AntennaEvent evt) -> {
                    if (evt.type.equals("onMessage")) {
                        System.out.println(evt.data);
                    } else if (evt.type.equals("onDisconnect")) {
                        System.out.println("Server disconnected!");
                    } else if (evt.type.equals("onAccessPointConnect")) {
                        // antenna.setPowerState(0);
                    }
                });

                if(signals.size() > 1) {
                    AntennaSignal transmitterAntenna = signals.get(0);
                    for(int i = 1; i<signals.size(); i++) {
                        if(signals.get(i).strength > transmitterAntenna.strength) {
                            transmitterAntenna = signals.get(i);
                        }
                    }
                    antenna.requestConnection(transmitterAntenna.transmitterUUID);
                }
                else antenna.requestConnection(signals.get(0).transmitterUUID);
            }
        }
    }
    
    @Override
    public String toString() {
        return "Node " + this.UUID + "\nAntenna #0 strength: " + this.antennas.get(0).strength + "\nAntenna #1 strength: " + this.antennas.get(1).strength;
    }
}
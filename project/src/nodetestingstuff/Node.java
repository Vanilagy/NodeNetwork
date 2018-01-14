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
        this.antennas.add(new Antenna(this, 100, 0, 1));
        this.antennas.add(new Antenna(this, 100, 0, 1));
        
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
        
        while(true) {

            for(int g = 0; g<antennas.size(); g++) {
                if(antennas.get(g).isWaiting()) {
                    //Removes all signals of antennas, which another of the own antennas is ...
                    Antenna antenna = antennas.get(g);
                    antenna.setPowerState(1);

                    ArrayList<AntennaSignal> signals = antennas.get(g).scanSignals();
                    ArrayList<AntennaSignal> singalsToRemove = new ArrayList<>();
                    for(int k = 0; k<signals.size(); k++) {
                        AntennaSignal signal = signals.get(k);
                        for(int l = 0; l<antennas.size(); l++) {
                            if(signal.transmitterUUID.equals(antennas.get(l).UUID)) singalsToRemove.add(signal); //one of the own Nodes
                            else if(signal.transmitterUUID.equals(antennas.get(l).connectedAccessPointUUID)) singalsToRemove.add(signal); //already connected to
                            else if(signal.transmitterNodeUUID.equals(antennas.get(l).connectedAccessPointNodeUUID)) singalsToRemove.add(signal); //already connected to one of the own antennas
                        }
                    }
                    while(!singalsToRemove.isEmpty()) {
                        signals.remove(singalsToRemove.get(0));
                        singalsToRemove.remove(0);
                    }

                    //Protocol
                    if (signals.isEmpty() && !hasAccessPoint()) {
                        System.out.println("Server node opened up!");

                        antenna.setMode(1);
                        antenna.addAntennaEventListener((AntennaEvent evt) -> {
                            switch (evt.type) {
                                case "onConnectionRequest":
                                    antenna.acceptConnectionRequest(evt.transmitterUUID);
                                    break;
                                case "onClientConnect":
                                    System.out.println("Connected to a new client innit!");
                                    if (antenna.connectedClientUUIDs.size() > 10) antenna.setPowerState(0);
                                    if (antenna.connectedClientUUIDs.size() > 4) {
                                        for (int i = 0; i < antenna.connectedClientUUIDs.size(); i++) {
                                            antenna.sendToClient(antenna.connectedClientUUIDs.get(i), "BOYS I AM FAMOUS!");
                                        }
                                    }   break;
                                case "onMessage":
                                    // TODO: Add message handling
                                    break;
                                case "onDisconnect":
                                    System.out.println("Client disconnected! " + antenna.connectedClientUUIDs.size());
                                    antenna.setWaitingState(1);
                                    antenna.setMode(0);
                                    break;
                            }
                        });
                        antenna.setWaitingState(0);
                    } else if(signals.isEmpty()) {
                        antenna.setWaitingState(1);
                    }
                    else {
                        antenna.addAntennaEventListener((AntennaEvent evt) -> {
                            switch (evt.type) {
                                case "onMessage":
                                    System.out.println(evt.data);
                                    break;
                                case "onDisconnect":
                                    System.out.println("Server disconnected!");
                                    antenna.setWaitingState(1);
                                    break;
                                case "onAccessPointConnect":
                                    break;
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
                        antenna.setWaitingState(0);
                    }
                }
            }
            try {
                synchronized(this) {
                    this.wait(200);
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    @Override
    public String toString() {
        return "Node " + this.UUID + "\nAntenna #0 strength: " + this.antennas.get(0).strength + "\nAntenna #1 strength: " + this.antennas.get(1).strength;
    }
    public boolean hasAccessPoint() {
        for(int i = 0; i<antennas.size(); i++) {
            if(antennas.get(i).isAccessPoint()) return true;
        }
        return false;
    }
}
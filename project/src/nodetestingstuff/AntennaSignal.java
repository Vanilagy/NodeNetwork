package nodetestingstuff;

public class AntennaSignal {
    public double strength;
    public String transmitterUUID;
    protected String transmitterNodeUUID;
    
    public AntennaSignal(double strength, String transmitterUUID, String transmitterNodeUUID) {
        this.strength = strength;
        this.transmitterUUID = transmitterUUID;
        this.transmitterNodeUUID = transmitterNodeUUID;
    }
}
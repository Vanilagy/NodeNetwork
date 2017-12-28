package nodetestingstuff;

import java.text.DecimalFormat;

/*
General class representing a device in the simulation. Knows stuff the actual node wouldn't know,  such as its position.
*/
public class SimulatedNode {
    public double x;
    public double y;
    public Node node;
    
    public SimulatedNode(double x, double y) {
        this.x = x;
        this.y = y;
        this.node = new Node();
        Simulator.register(this);

        this.node.start();
    }
    
    // Generate a number from the UUID (basically convert the UUID into a number) - NOT UNIQUE
    public long getNumberFromUUID() {
        String UUID = this.node.UUID;
        long sum = 0;
        
        for (int i = 0; i < UUID.length(); i++) {
            char c = UUID.charAt(i);
            
            sum += (int) c * Math.pow(3, i);
        }
        
        return sum / 97;
    }
    
    @Override
    public String toString() {
        String output = "";
        DecimalFormat df = new DecimalFormat("#.##");
        
        output += "x: " + df.format(this.x) + " m\n";
        output += "y: " + df.format(this.y) + " m\n";
        output += "========\n";
        output += "UUID: " + this.node.UUID + "\n";
        
        for (int i = 0; i < node.antennas.size(); i++) {
            output += "--------\nAntenna #" + i + ": " + node.antennas.get(i).UUID + "\n" + node.antennas.get(i).toString();
        }
               
        return output;
    }
}
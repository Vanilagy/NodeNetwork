package nodetestingstuff;

public class Main {
    public static GUI gui = new GUI();
    
    public static void main(String[] args) {
        Simulator.addNode(new SimulatedNode(-10, 0));
        
        for (int i = 0; i < 0; i++) {
            Simulator.addNode(new SimulatedNode(Math.random() * 100 - 50, Math.random() * 100 - 50));
        }
        
        gui.setVisible(true);
    }
}
package nodetestingstuff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class MainCanvas extends java.awt.Canvas {
    private int width;
    private int height;
    private boolean dimensionsSet = false;
    public final double coordinateWidth = 100;
    public final int renderedNodeRadius = 9;
    
    @Override
    public void paint(Graphics canvasG) {  
        if (!dimensionsSet) {
            width = getWidth();
            height = getHeight();
            dimensionsSet = true;
        }
        
        Image buffer = createImage(width, height);
        Graphics g = buffer.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        for (int i = 0; i < Simulator.simulatedNodes.size(); i++) {
            SimulatedNode node = Simulator.simulatedNodes.get(i);
            int x = (int) convertXCoordinate(node.x, false);
            int y = (int) convertYCoordinate(node.y, false);
            
            g.setColor(new Color(Color.HSBtoRGB((float) ((double) (node.getNumberFromUUID() % 360) / 360), 1, 1)));
            g.fillOval(x - renderedNodeRadius, y - renderedNodeRadius, renderedNodeRadius * 2, renderedNodeRadius * 2);
        }
        
        if (Main.gui.selectedNode != null) {
            SimulatedNode node = Main.gui.selectedNode;
            
            Color rgb = new Color(Color.HSBtoRGB((float) ((double) (node.getNumberFromUUID() % 360) / 360), 1, 1));
            
            for (int i = 0; i < node.node.antennas.size(); i++) {
                Antenna antenna = node.node.antennas.get(i); 
                double angle = -Math.PI * 3/4 + (i * Math.PI / 2);
                double distance = (renderedNodeRadius + 2) * Math.sqrt(2);
                int x = (int) (convertXCoordinate(node.x, false) + Math.cos(angle) * distance);
                int y = (int) (convertYCoordinate(node.y, false) + Math.sin(angle) * distance);
                
                if (!antenna.isAccessPoint()) {
                    if (antenna.connectedAccessPointUUID != null) {
                        SimulatedNode otherNode = Simulator.getSimulatedNodeByUUID(Simulator.getAntennaByUUID(antenna.connectedAccessPointUUID).connectedNode.UUID);
                        int x2 = (int) convertXCoordinate(otherNode.x, false);
                        int y2 = (int) convertYCoordinate(otherNode.y, false);
                        
                        g.setColor(Color.WHITE);
                        g.drawLine(x, y, x2, y2);
                    }
                    
                    g.setColor(Color.GREEN);
                    g.fillOval(x-3, y-3, 6, 6);
                } else {
                    for (int j = 0; j < antenna.connectedClientUUIDs.size(); j++) {
                        SimulatedNode otherNode = Simulator.getSimulatedNodeByUUID(Simulator.getAntennaByUUID(antenna.connectedClientUUIDs.get(j)).connectedNode.UUID);
                        int x2 = (int) convertXCoordinate(otherNode.x, false);
                        int y2 = (int) convertYCoordinate(otherNode.y, false);
                        
                        g.setColor(Color.WHITE);
                        g.drawLine(x, y, x2, y2);
                    }
                    
                    g.setColor(new Color(0, 169, 255));
                    g.fillOval(x-3, y-3, 6, 6);
                }
            }
            
            int x = (int) (convertXCoordinate(node.x, false));
            int y = (int) (convertYCoordinate(node.y, false));
            int radius = renderedNodeRadius + 3;
            
            g.setColor(Color.WHITE);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
        
        canvasG.drawImage(buffer, 0, 0, this);
    }
    
    public void drawMouse() {
        String cursorMode = Main.gui.cursorMode;
        int x = Main.gui.mouseX;
        int y = Main.gui.mouseY;
        
        if (cursorMode == "addNode") {
            Graphics g = getGraphics();
            paint(g);
            
            int radius = renderedNodeRadius + 1;
            
            g.setColor(Color.WHITE);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }
    
    public double convertXCoordinate(double x, boolean inverse) {
        if (!inverse) {
            return (x / this.coordinateWidth * this.width + this.width / 2);
        } else {
            return (x - this.width / 2) / this.width * this.coordinateWidth;
        }
    }
    public double convertYCoordinate(double y, boolean inverse) {
        if (!inverse) {
            return y / this.coordinateWidth * this.height + this.height / 2;
        } else {
            return (y - this.height / 2) / this.height * this.coordinateWidth;
        }
    }
}
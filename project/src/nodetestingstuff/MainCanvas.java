package nodetestingstuff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

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
        
        HashMap positionMap = new HashMap();
        for (int i = 0; i < Simulator.simulatedNodes.size(); i++) {
            SimulatedNode node = Simulator.simulatedNodes.get(i);
            int x = (int) convertXCoordinate(node.x, false);
            int y = (int) convertYCoordinate(node.y, false);
            
            for (int j = 0; j < node.node.antennas.size(); j++) {
                Antenna antenna = node.node.antennas.get(j);
                double angle = -Math.PI * 3/4 + (j * Math.PI / 2);
                double distance = (renderedNodeRadius + 2) * Math.sqrt(2);
                int x2 = (int) (convertXCoordinate(node.x, false) + Math.cos(angle) * distance);
                int y2 = (int) (convertYCoordinate(node.y, false) + Math.sin(angle) * distance);
                
                positionMap.put(antenna.UUID, new Position(x2, y2));
                
                if (!antenna.isAccessPoint()) {
                    g.setColor(new Color(0, 255, 0, antenna.isOn() ? 255 : 127));  
                } else {
                    g.setColor(new Color(0, 169, 255, antenna.isOn() ? 255 : 127));
                }
                g.fillOval(x2-3, y2-3, 6, 6);
            }
            
            g.setColor(new Color(Color.HSBtoRGB((float) ((double) (node.getNumberFromUUID() % 360) / 360), 1, 1)));
            g.fillOval(x - renderedNodeRadius, y - renderedNodeRadius, renderedNodeRadius * 2, renderedNodeRadius * 2);
        }
        
        for (int i = 0; i < Simulator.simulatedNodes.size(); i++) {
            SimulatedNode node = Simulator.simulatedNodes.get(i);
            
            for (int j = 0; j < node.node.antennas.size(); j++) {
                Antenna antenna = node.node.antennas.get(j);
                Position position = (Position) positionMap.get(antenna.UUID);
                
                if (!antenna.isAccessPoint()) {
                    if (antenna.connectedAccessPointUUID != null) {
                        Position accessPointPosition = (Position) positionMap.get(antenna.connectedAccessPointUUID);
                        
                        if (accessPointPosition != null) {
                            g.setColor(Color.WHITE);
                            g.drawLine(position.x, position.y, accessPointPosition.x, accessPointPosition.y);
                        }
                    }
                } else {
                    for (int k = 0; k < antenna.connectedClientUUIDs.size(); k++) {
                        Position clientPosition = (Position) positionMap.get(antenna.connectedClientUUIDs.get(k));
                        
                        if (clientPosition != null) {
                            g.setColor(Color.WHITE);
                            g.drawLine(position.x, position.y, clientPosition.x, clientPosition.y);
                        }
                    }
                }
                
            }
        }
        
        if (Main.gui.selectedNode != null) {
            SimulatedNode node = Main.gui.selectedNode;
            
            Color rgb = new Color(Color.HSBtoRGB((float) ((double) (node.getNumberFromUUID() % 360) / 360), 1, 1));
            
            int x = (int) (convertXCoordinate(node.x, false));
            int y = (int) (convertYCoordinate(node.y, false));
            int radius = renderedNodeRadius + 3;
            
            g.setColor(Color.WHITE);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            
            for (int i = 0; i < node.node.antennas.size(); i++) {
                Antenna antenna = node.node.antennas.get(i);
                double range = Math.sqrt(antenna.strength / Simulator.REQUIRED_SIGNAL_STRENGTH) / coordinateWidth * width;
                
                g.setColor(new Color(255, 255, 255, 80));
                g.drawOval((int)(x - range), (int)(y - range), (int)(range * 2), (int)(range * 2));
            }
        }
        
        canvasG.drawImage(buffer, 0, 0, this);
    }
    
    public void drawMouse() {
        String cursorMode = Main.gui.cursorMode;
        int x = Main.gui.mouseX;
        int y = Main.gui.mouseY;
        
        if (cursorMode == "addNode") {
            Image buffer = createImage(width, height);
            Graphics g = buffer.getGraphics();
            paint(g);
            
            int radius = renderedNodeRadius + 1;
            
            g.setColor(Color.WHITE);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            this.getGraphics().drawImage(buffer, 0, 0, this);
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

class Position {
    public int x;
    public int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
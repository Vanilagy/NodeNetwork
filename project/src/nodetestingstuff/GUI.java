package nodetestingstuff;

import java.util.Timer;
import java.util.TimerTask;
import static javax.swing.ScrollPaneConstants.*;

public class GUI extends javax.swing.JFrame {
    public GUI() {
        initComponents();
        
        jScrollPane1.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                mainCanvas.paint(mainCanvas.getGraphics());
                if (selectedNode != null) {
                    selectNode(selectedNode);
                }
                if(cursorMode == "addNode") {
                    mainCanvas.drawMouse();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000 / 10);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainCanvas = new nodetestingstuff.MainCanvas();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedNodeTextArea = new javax.swing.JTextArea();
        selectedNodeInfoLabel = new javax.swing.JLabel();
        addNodeButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        deleteNodeButton = new javax.swing.JButton();
        cursorModeDisplay = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Node Network Simulation");
        setPreferredSize(new java.awt.Dimension(857, 640));
        setResizable(false);

        mainCanvas.setName(""); // NOI18N
        mainCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mainCanvasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainCanvasMouseReleased(evt);
            }
        });
        mainCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                mainCanvasMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mainCanvasMouseMoved(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        selectedNodeTextArea.setEditable(false);
        selectedNodeTextArea.setColumns(20);
        selectedNodeTextArea.setRows(5);
        selectedNodeTextArea.setEnabled(false);
        selectedNodeTextArea.setPreferredSize(new java.awt.Dimension(80, 75));
        jScrollPane1.setViewportView(selectedNodeTextArea);

        selectedNodeInfoLabel.setText("Selected node info:");
        selectedNodeInfoLabel.setEnabled(false);

        addNodeButton.setText("Add node");
        addNodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addNodeButtonMouseClicked(evt);
            }
        });

        deleteNodeButton.setText("Delete selected node");
        deleteNodeButton.setEnabled(false);
        deleteNodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteNodeButtonMouseClicked(evt);
            }
        });

        cursorModeDisplay.setText("Mode: Select");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                    .addComponent(jSeparator2)
                    .addComponent(deleteNodeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addNodeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectedNodeInfoLabel)
                            .addComponent(cursorModeDisplay))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cursorModeDisplay)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addNodeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedNodeInfoLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteNodeButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(mainCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public SimulatedNode selectedNode;
    public String cursorMode = "select";
    public boolean mouseDown = false;
    public int mouseX = 0;
    public int mouseY = 0;
    public int selectedNodeOffsetX;
    public int selectedNodeOffsetY;
    
    private void click(int x, int y) {
        if (cursorMode == "select") {
            for (int i = Simulator.simulatedNodes.size() - 1; i >= 0; i--) {
                SimulatedNode node = Simulator.simulatedNodes.get(i);
                int nodeX = (int) mainCanvas.convertXCoordinate(node.x, false);
                int nodeY = (int) mainCanvas.convertYCoordinate(node.y, false);

                double distance = Math.hypot(x - nodeX, y - nodeY);

                if (distance <= mainCanvas.renderedNodeRadius + 1) {
                    selectedNodeOffsetX = (int) mainCanvas.convertXCoordinate(node.x, false) - mouseX;
                    selectedNodeOffsetY = (int) mainCanvas.convertYCoordinate(node.y, false) - mouseY;
                    selectNode(node);
                    mainCanvas.paint(mainCanvas.getGraphics());
                    break;
                }

                if (i == 0) deselectNode();
            }
        } else if (cursorMode == "addNode") {
            Simulator.addNode(new SimulatedNode(mainCanvas.convertXCoordinate(mouseX, true), mainCanvas.convertYCoordinate(mouseY, true)));
            cursorMode = "select";
            cursorModeDisplay.setText("Mode: Select");
        }         
    }
    
    private void selectNode(SimulatedNode node) {
        selectedNode = node;
        
        selectedNodeInfoLabel.setEnabled(true);
        selectedNodeTextArea.setEnabled(true);
        deleteNodeButton.setEnabled(true);
        
        selectedNodeTextArea.setText(node.toString());
    }
    
    private void deselectNode() {
        selectedNode = null;
        mainCanvas.paint(mainCanvas.getGraphics());
        
        selectedNodeInfoLabel.setEnabled(false);
        selectedNodeTextArea.setEnabled(false);
        deleteNodeButton.setEnabled(false);
        
        selectedNodeTextArea.setText("");
    }
    
    private void mainCanvasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainCanvasMousePressed
        mouseDown = true;
        click(evt.getX(), evt.getY());
    }//GEN-LAST:event_mainCanvasMousePressed

    private void addNodeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNodeButtonMouseClicked
        cursorMode = "addNode";
        cursorModeDisplay.setText("Mode: Add node");
    }//GEN-LAST:event_addNodeButtonMouseClicked

    private void moveMouse(java.awt.event.MouseEvent evt) {
        mouseX = evt.getX();
        mouseY = evt.getY();
        
        if (mouseDown && selectedNode != null) {
            selectedNode.x = Math.max(-mainCanvas.coordinateWidth / 2, Math.min(mainCanvas.coordinateWidth / 2, mainCanvas.convertXCoordinate(mouseX + selectedNodeOffsetX, true)));
            selectedNode.y = Math.max(-mainCanvas.coordinateWidth / 2, Math.min(mainCanvas.coordinateWidth / 2, mainCanvas.convertYCoordinate(mouseY + selectedNodeOffsetY, true)));
            
            Simulator.checkAntennaConnections(selectedNode);
            selectNode(selectedNode);
            mainCanvas.paint(mainCanvas.getGraphics());
        }
        
        mainCanvas.drawMouse();
    }
    
    
    private void mainCanvasMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainCanvasMouseMoved
        moveMouse(evt);
    }//GEN-LAST:event_mainCanvasMouseMoved

    private void deleteNodeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteNodeButtonMouseClicked
        Simulator.deleteNode(selectedNode);
        deselectNode();
    }//GEN-LAST:event_deleteNodeButtonMouseClicked

    private void mainCanvasMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainCanvasMouseDragged
        moveMouse(evt);
    }//GEN-LAST:event_mainCanvasMouseDragged

    private void mainCanvasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainCanvasMouseReleased
        mouseDown = false;
    }//GEN-LAST:event_mainCanvasMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNodeButton;
    private javax.swing.JLabel cursorModeDisplay;
    private javax.swing.JButton deleteNodeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private nodetestingstuff.MainCanvas mainCanvas;
    private javax.swing.JLabel selectedNodeInfoLabel;
    private javax.swing.JTextArea selectedNodeTextArea;
    // End of variables declaration//GEN-END:variables
}
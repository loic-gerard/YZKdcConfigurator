/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author lgerard
 */
public class GraphicLauncherPanel extends JPanel implements ActionListener{
    JTextField port;
    JButton go;
    JButton goLock;
    JButton goUnLock;
    private YZKdcConfigurator main;
    
    public GraphicLauncherPanel(YZKdcConfigurator in_main){
        
        super();
        main = in_main;
        this.setLayout(null);
     
        
        port = new JTextField("COM?");
        port.setBounds(20, 20, 280, 30);
        go = new JButton("Configuration initiale");
        go.setBounds(20, 80, 280, 60);
        go.addActionListener(this);
        
        goLock = new JButton("Vérouillage");
        goLock.setBounds(20, 160, 280, 60);
        goLock.addActionListener(this);
        
        goUnLock = new JButton("Déverrouillage");
        goUnLock.setBounds(20, 240, 280, 60);
        goUnLock.addActionListener(this);
        
        add(port);
        add(go);
        add(goLock);
        add(goUnLock);

        
    }
    
    public void actionPerformed(ActionEvent e) {
        go.setEnabled(false);
        goLock.setEnabled(false);
        goUnLock.setEnabled(false);
        port.setEnabled(false);
        this.repaint();
        
        if(e.getSource().equals(go)){
            main.startProcess(port.getText());
        }else if(e.getSource().equals(goLock)){
            main.lockProcess(port.getText());
        }else if(e.getSource().equals(goUnLock)){
            main.unlockProcess(port.getText());
        }
	
    }
}

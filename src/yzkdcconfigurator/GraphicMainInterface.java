/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author lgerard
 */
public class GraphicMainInterface  extends JFrame{
    
    private JTextArea logs;
    private GraphicLauncherPanel launcher;
    private YZKdcConfigurator main;
    
    public GraphicMainInterface(YZKdcConfigurator in_main){
	super();
	
        main = in_main;
        
	setSize(650,500);
	setTitle(YZKdcConfigurator.appzName);
	
	WindowAdapter winCloser = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	};
	addWindowListener(winCloser);
	
	Container contenu = getContentPane();
	contenu.setLayout(new GridLayout(0,2));
	
	
	//Logs d'activité
	logs = new JTextArea();
        logs.setVisible(true);
        logs.setText("");

        JScrollPane ScrollBar = new javax.swing.JScrollPane();
        ScrollBar.setViewportView(logs);
        ScrollBar.setVisible(true);
	ScrollBar.setBounds(20, 90, 250, 200);
	
	contenu.add(ScrollBar, BorderLayout.EAST);
	logs.setText("");
	
	//Panel avec les ports COM
	launcher = new GraphicLauncherPanel(main);
	contenu.add(launcher);
	
	setEnabled(true);
	setVisible(true);
	this.toFront();
	
	this.setDefaultLookAndFeelDecorated(true);
    }
    
    
    public void writeInLog(String log){
	logs.setText(log+"\n"+logs.getText());
    }
    
    public void showStartMessage(){
        JOptionPane.showMessageDialog(this,
	    "Le KDC a été connecté. La procédure de configuration va pouvoir débuter.",
	    "Début du processus",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTestsMessage(){
        JOptionPane.showMessageDialog(this,
	    "La configuration a été correctement envoyée au KDC."
                    + "Le KDC va a présent effectuer une série de tests.",
	    "Configuration effectuee",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTest1Message(){
        JOptionPane.showMessageDialog(this,
	    "test 1 : vous allez entendre un BIP de succès.",
	    "Test 1",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTest2Message(){
        JOptionPane.showMessageDialog(this,
	    "test 2 : vous allez entendre un BIP d'erreur.",
	    "Test 2",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTest3Message(){
        JOptionPane.showMessageDialog(this,
	    "test 3 : vous allez voir les leds en ROUGE",
	    "Test 3",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTest4Message(){
        JOptionPane.showMessageDialog(this,
	    "test 4 : vous allez voir les leds en ORANGE",
	    "Test 4",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showTest5Message(){
        JOptionPane.showMessageDialog(this,
	    "test 5 : vous allez voir les leds en VERT",
	    "Test 5",
	    JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showEndMessage(){
        JOptionPane.showMessageDialog(this,
	    "Les tests sont à présent effectués. Vous pouvez déconnecter le KDC de l'USB et initier l'appairage Bluetooth. Attention à supprimer au prélable toute configuration rémanante sur l'ordinateur cible.",
	    "SUCCES !",
	    JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
}

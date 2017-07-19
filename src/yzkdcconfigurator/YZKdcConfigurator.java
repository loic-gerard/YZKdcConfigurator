/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import java.util.Enumeration;
import javax.comm.CommPortIdentifier;

/**
 *
 * @author lgerard
 */
public class YZKdcConfigurator {

    public static String appzName = "Yzeure And Rock - KDC Configurator";
    public static String version = "Version 0.0.1";
    public static GraphicMainInterface interf;
    public static String port;
    public static KDCCommunication kdc;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new YZKdcConfigurator();
    }
    
    public YZKdcConfigurator(){
        Log.log(appzName);
	Log.log(version);
		Log.log("----------------------------------------");

		//Création de l'interface
		interf = new GraphicMainInterface(this);

		interf.repaint();
		interf.revalidate();

		//Log ready
		Log.log("Configuration et interface prêtes à l'utilisation");

        Commands.init();
		
    }
    
    public void startProcess(String in_port){
        port = in_port;
        kdc = new KDCCommunication(port);
        kdc.init();
        kdc.configProcess();
    }
    
    public void lockProcess(String in_port){
        port = in_port;
        kdc = new KDCCommunication(port);
        kdc.init();
        kdc.lockProcess();
    }
    
    public void unlockProcess(String in_port){
        port = in_port;
        kdc = new KDCCommunication(port);
        kdc.init();
        kdc.unlockProcess();
    }
	
}

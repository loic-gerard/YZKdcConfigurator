/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import java.util.Enumeration;
import java.util.Vector;
import javax.comm.CommPortIdentifier;


public class KDCDevicesDiscover extends Thread{
	
	private Vector devices;
	private boolean endPortsDiscover = false;
	private boolean interrupt = false;
	
	public KDCDevicesDiscover(){
		devices = new Vector();
		
		Log.log("Initialisation d'une procédure de découverte des matériels KDC connectés");
		start();
		discoverPortsCOM();
		
	}
	
	public void run() {
		
		try {
            while (1 == 1 && !interrupt) {
				Log.log("TEST");
				if(endPortsDiscover){
					boolean allTested = true;
					for(int i = 0; i < devices.size(); i++){
						KDCDeviceTester device = (KDCDeviceTester) devices.get(i);
						if(!device.isTested()){
							allTested = false;
						}
					}
					
					if(allTested){
						interrupt = true;
						endTreatement();
					}
				}
				
				
				pause(2000);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void endTreatement(){
		Log.log("FIN DU TRAITEMENT DE DETECTION");
		for (int i = 0; i < devices.size(); i++) {
			KDCDeviceTester device = (KDCDeviceTester) devices.get(i);
			if (device.isReady()) {
				Log.log("Device prêt : "+device.getPort());
			}
		}
	}
	
	private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }
	
	private void discoverPortsCOM() {
		Enumeration ports = null;
		ports = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;
		
		Vector testedCOMPorts = new Vector();
		
		while(ports.hasMoreElements()) {

			portId = (CommPortIdentifier) ports.nextElement();
			
			if (!portId.isCurrentlyOwned() && portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				Log.log("Ce port sera testé : "+portId.getName());
				testedCOMPorts.add(portId.getName());
			}
		}

		for(int i = 0; i < testedCOMPorts.size(); i++){
			String portName = (String) testedCOMPorts.get(i);
			
			KDCDeviceTester device = new KDCDeviceTester(portName);
			devices.add(device);
		}
		
		endPortsDiscover = true;
		
		for(int i = 0; i < devices.size(); i++){
			KDCDeviceTester device = (KDCDeviceTester) devices.get(i);
			device.init();
			device.testProcess();
		}
		
		

	}
}

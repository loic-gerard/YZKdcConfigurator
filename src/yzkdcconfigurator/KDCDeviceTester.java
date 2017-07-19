/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import com.sun.comm.Win32Driver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

/**
 *
 * @author lgerard
 */
public class KDCDeviceTester extends Thread{
	private String port;			//Port COM utilisé
    private CommPortIdentifier portIdentifier;	//Identifiant du port COM
    private SerialPort serialPort;		//Objet Port serie
    private BufferedReader inStream;		//flux de lecture du port
    private OutputStream outStream;		//flux d'écriture du port

    private Vector commandStack;

    private boolean connected = false;		//Indique si le KDC a bien été reconnu

    private boolean locked = false;		//Détermine si un envoi d'une commande est en cours. (lock pour nouvel envoi)

    private String readed = "";			//Chaine lue du buffer.
	
	
	
	
	private boolean ready = false;
	private boolean tested = false;

    public KDCDeviceTester(String portCOM) {
        //Initialisation des variables
        port = portCOM;
        commandStack = new Vector();
    }

    public void init() {
        log("Début du test du port "+port);

        //initialisation du driver
        log("Initialisation du driver portCOM Windows : WAIT");
        Win32Driver w32Driver = new Win32Driver();
        w32Driver.initialize();
        log("Initialisation du driver portCOM Windows : OK");

        //récupération de l'identifiant du port
        log("Recuperation de l'identifiant du port : WAIT");
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(port);
        } catch (Exception e) {
			tested = true;
            e.printStackTrace();
        }
        log("Recuperation de l'identifiant du port : OK");

        //ouverture du port
        log("Ouverture du port : WAIT");
        try {
            serialPort = (SerialPort) portIdentifier.open("KDC-" + port, 30000);
        } catch (Exception e) {
			tested = true;
            e.printStackTrace();
        }
        log("Ouverture du port : OK");

        //règle les paramètres de la connexion
        log("Réglage des paramètres de la connexion du port : WAIT");
        try {
            serialPort.setSerialPortParams(
                    9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (Exception e) {
			tested = true;
            e.printStackTrace();
        }
        log("Réglage des paramètres de la connexion du port : OK");

        //récupération du flux de lecture et écriture du port
        log("Ouverture des flux de lecture/ecriture sur port : WAIT");
        try {
            outStream = serialPort.getOutputStream();
            inStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (Exception e) {
			tested = true;
            e.printStackTrace();
        }
        log("Ouverture des flux de lecture/ecriture sur port : OK");

        log("Fin d'initialisation d'un KDC : SUCCES");
		
    }
	
	public void kill(){
		Log.log("KILL");
		
		tested = true;
		
		for(int i = 0; i < commandStack.size(); i++){
			KDCDeviceTesterCommand com = (KDCDeviceTesterCommand) commandStack.get(i);
			com.setError();
		}
		
		try{
			outStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			inStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			serialPort.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Log.log("END KILL");
		stop();
	}
	
	public void testProcess(){
        start();
		sendCommand("W");
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public boolean isTested(){
		return tested;
	}
    
    public void sendCommand(String cmd) {
        saveCommand("W");
        saveCommand(cmd);
        sendNext();
    }

    private void saveCommand(String cmd) {
        KDCDeviceTesterCommand c = new KDCDeviceTesterCommand(cmd, this);
        commandStack.add(c);
    }

    public void sendNext() {
        if (!locked && commandStack.size() > 0) {
            KDCDeviceTesterCommand c = (KDCDeviceTesterCommand) commandStack.firstElement();

           	try {
				log(c.getCommand() + " : STARTED");
				locked = true;

				String cmd = c.getCommand();

				//Démarrage d'un Thread de contrôle et de renvoi de la commande W au besoin.
				if (cmd == "W") {
					c.setCalled();
				}

				for (int i = 0; i < cmd.length(); i++) {
					char ctosend = cmd.charAt(i);
					if (ctosend == '@') {
						ctosend = 13;
					}
					log("SENDED : " + ctosend);
					outStream.write((int) ctosend);

					if (i == (cmd.length() - 1)) {
						log(c.getCommand() + " : WAIT FOR TERMINAL RESPONSE");
					} else {
						pause(15);
					}
				}

				locked = false;
			} catch (Exception e) {
				log(c.getCommand() + " : ERROR !");
				e.printStackTrace();
			}


        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    private void log(String message) {
        Log.log(port + " : " + message);
    }

    public void run() {
        try {
            while (1 == 1 && !tested) {
                int c = inStream.read();
                char ca = (char) c;

                if (commandStack.size() > 0) {
                    if (ca == '@') {
                        KDCDeviceTesterCommand command = (KDCDeviceTesterCommand) commandStack.firstElement();
                        log(command.getCommand() + " : OK with response : " + readed);
                        command.setResponse(readed);
                        readed = "";
						
						log("RECU B");
						ready = true;
						tested = true;
						kill();

                        //Si première connexion. KDC bien reconnu
                        if (!connected) {
                            connected = true;
							log("RECU A");
							ready = true;
                        }

                        commandStack.remove(0);
                        sendNext();
                    } else if (ca == '!') {
                        KDCDeviceTesterCommand command = (KDCDeviceTesterCommand) commandStack.firstElement();
                        log(command.getCommand() + " : COMMANDE NON RECONNUE");
                        command.setError();
                        readed = "";

                        commandStack.remove(0);
                        sendNext();
                    } else {
                        readed += ca;
                    }
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cmdTimeout() {
        log("Timeout commande");
		tested = true;
		kill();
    }

    public String getPort() {
        return port;
    }

	public void kdcCmd_message(String line1, String line2) {
        int charsByLine = 13;
        String bline1 = "";
        for (int i = 0; i < charsByLine; i++) {
            if (line1.length() >= (i + 1)) {
                bline1 += line1.charAt(i);
            } else {
                bline1 += " ";
            }
        }
        String bline2 = "";
        for (int i = 0; i < charsByLine; i++) {
            if (line2.length() >= (i + 1)) {
                bline2 += line2.charAt(i);
            } else {
                bline2 += " ";
            }
        }

        sendCommand("GMT" + bline1 + bline2 + "@");
    }

}

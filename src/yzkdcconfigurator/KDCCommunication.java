/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import com.sun.comm.Win32Driver;
import javax.comm.SerialPort;
import java.io.BufferedReader;
import java.io.OutputStream;
import javax.comm.CommPortIdentifier;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JOptionPane;

public class KDCCommunication extends Thread {

    private String port;			//Port COM utilisé
    private CommPortIdentifier portIdentifier;	//Identifiant du port COM
    private SerialPort serialPort;		//Objet Port serie
    private BufferedReader inStream;		//flux de lecture du port
    private OutputStream outStream;		//flux d'écriture du port

    private Vector commandStack;

    private boolean connected = false;		//Indique si le KDC a bien été reconnu

    private boolean locked = false;		//Détermine si un envoi d'une commande est en cours. (lock pour nouvel envoi)

    private String readed = "";			//Chaine lue du buffer.

    public KDCCommunication(String portCOM) {
        //Initialisation des variables
        port = portCOM;
        commandStack = new Vector();
    }

    public void init() {
        log("Debut d'initialisation d'un KDC");

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
            e.printStackTrace();
        }
        log("Recuperation de l'identifiant du port : OK");

        //ouverture du port
        log("Ouverture du port : WAIT");
        try {
            serialPort = (SerialPort) portIdentifier.open("KDC-" + port, 30000);
        } catch (Exception e) {
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
            e.printStackTrace();
        }
        log("Réglage des paramètres de la connexion du port : OK");

        //récupération du flux de lecture et écriture du port
        log("Ouverture des flux de lecture/ecriture sur port : WAIT");
        try {
            outStream = serialPort.getOutputStream();
            inStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("Ouverture des flux de lecture/ecriture sur port : OK");

        log("Fin d'initialisation d'un KDC : SUCCES");

        
    }
    
    public void configProcess(){
        YZKdcConfigurator.interf.showStartMessage();
        kdcCmd_message("KDC CONFIG", "START");
        kdcCmd_okBeep();
        for (int i = 0; i < Commands.getNbCmds(); i++) {
            kdcCmd_message("KDC CONFIG", "STEP " + (i + 1) + "/" + Commands.getNbCmds());
            saveCommand(Commands.getCmd(i));
        }
        kdcCmd_okBeep();
        kdcCmd_message("KDC CONFIG", "SUCCESS !");
        kdcCmd_greenLight();
        start();    
    }
    
    public void lockProcess(){
        YZKdcConfigurator.interf.showStartMessage();
        kdcCmd_message("LOCK", "START");
        kdcCmd_okBeep();
        saveCommand("GW1UUDDS#@");
        kdcCmd_okBeep();
        kdcCmd_message("LOCK", "SUCCESS !");
        kdcCmd_greenLight();
        saveCommand("<MSSG-END>");
        start();
    }
    
    public void unlockProcess(){
        YZKdcConfigurator.interf.showStartMessage();
        kdcCmd_message("ULOCK", "START");
        kdcCmd_okBeep();
        saveCommand("GW0@");
        kdcCmd_okBeep();
        kdcCmd_message("ULOCK", "SUCCESS !");
        kdcCmd_greenLight();
        saveCommand("<MSSG-END>");
        start();
    }

    public void sendCommand(String cmd) {
        log("SENDCOMMAND : " + cmd);
        saveCommand("W");
        saveCommand(cmd);
        sendNext();
    }

    private void saveCommand(String cmd) {
        log("SAVECOMMAND : " + cmd);
        KDCCommand c = new KDCCommand(cmd, this);
        commandStack.add(c);
    }

    public void sendNext() {
        if (!locked && commandStack.size() > 0) {
            KDCCommand c = (KDCCommand) commandStack.firstElement();

            if (c.getCommand().equals("<MSSG-TESTS>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTestsMessage();
            } else if (c.getCommand().equals("<MSSG-END>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showEndMessage();
            } else if (c.getCommand().equals("<MSSG-TEST1>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTest1Message();
            } else if (c.getCommand().equals("<MSSG-TEST2>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTest2Message();
            } else if (c.getCommand().equals("<MSSG-TEST3>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTest3Message();
            } else if (c.getCommand().equals("<MSSG-TEST4>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTest4Message();
            } else if (c.getCommand().equals("<MSSG-TEST5>")) {
                commandStack.remove(0);
                YZKdcConfigurator.interf.showTest5Message();
            } else {
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
            while (1 == 1) {
                int c = inStream.read();
                char ca = (char) c;

                if (commandStack.size() > 0) {
                    if (ca == '@') {
                        KDCCommand command = (KDCCommand) commandStack.firstElement();
                        log(command.getCommand() + " : OK with response : " + readed);
                        command.setResponse(readed);
                        readed = "";

                        //Si première connexion. KDC bien reconnu
                        if (!connected) {
                            connected = true;
                        }

                        commandStack.remove(0);
                        sendNext();
                    } else if (ca == '!') {
                        KDCCommand command = (KDCCommand) commandStack.firstElement();
                        log(command.getCommand() + " : COMMANDE NON RECONNUE");
                        command.setError();
                        readed = "";

                        commandStack.remove(0);
                        sendNext();
                    } else {
                        readed += ca;
                    }
                } else {
                    if (ca != 13 && ca != 10 && ca != 64) {
                        readed += ca;
                    }
                    if (ca == 13) {
                        log("CODE BARRE RECU : " + readed);
                        readed = "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cmdTimeout() {
        log("Timeout commande");
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

    public void kdcCmd_redLight() {
        kdcCmd_clearLights();
        sendCommand("GML3#@");
    }

    public void kdcCmd_orangeLight() {
        sendCommand("GML5#@");
    }

    public void kdcCmd_greenLight() {
        kdcCmd_clearLights();
        sendCommand("GML1#@");
    }

    public void kdcCmd_clearLights() {
        sendCommand("GML4#@");
    }

    public void kdcCmd_errorBeep() {
        sendCommand("GMB0@");
    }

    public void kdcCmd_okBeep() {
        sendCommand("GMB1@");
    }
}

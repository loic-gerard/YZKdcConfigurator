/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.io.StringWriter;
import java.io.PrintWriter;

public class CrashError {
    public CrashError(String message, Exception e, JFrame parent){
	if(e != null){
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    sw.toString(); // stack trace as a string
	    
	    JOptionPane.showMessageDialog(parent,
	    message+"\n\n Détails : \n"+sw.toString(),
	    "Une erreur grave a été rencontrée.",
	    JOptionPane.ERROR_MESSAGE);
	}else{
	    JOptionPane.showMessageDialog(parent,
	    message,
	    "Une erreur grave a été rencontrée.",
	    JOptionPane.ERROR_MESSAGE);
	}
	
    }
}

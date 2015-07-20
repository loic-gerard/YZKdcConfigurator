/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import org.ini4j.Wini;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class Commands {
    
    private static Vector commandes;
    
    public static void init(){
        commandes = new Vector();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("installconfig.txt"));
            String line = br.readLine();

            while (line != null) {
                commandes.add(line);
                line = br.readLine();
                System.out.println(line);
            }
            
            br.close();
        }catch (Exception e){  
            new CrashError("Erreur de lecture du fichier de configuration", e, null);
        }
    }
    
    public static int getNbCmds(){
        return commandes.size();
    }
    
    public static String getCmd(int i){
        return (String)commandes.get(i);
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import java.util.Date;

public class Log {

	public static void log(String message) {
		Date date = new Date();
		message = date.toString() + " : " + message;

		System.out.println(message);

		if (YZKdcConfigurator.interf != null) {
			YZKdcConfigurator.interf.writeInLog(message);
		}
	}
}

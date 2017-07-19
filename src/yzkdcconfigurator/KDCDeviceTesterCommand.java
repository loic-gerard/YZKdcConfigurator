/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

/**
 *
 * @author lgerard
 */
public class KDCDeviceTesterCommand {

	private String command;
	private String response;
	private boolean called = false;
	private boolean returned = false;
	private boolean error = false;
	private TimelapsThread ctrlThread;
	private KDCDeviceTester com;
	private int retryCount = 0;

	public KDCDeviceTesterCommand(String cmd, KDCDeviceTester in_com) {
		command = cmd;
		com = in_com;
	}

	public void setCalled() {
		called = true;
		if (command == "W") {
			ctrlThread = new TimelapsThread(500, this, "controlExecution");
		}
	}

	public void controlExecution() {
		if (called && !returned && ctrlThread != null) {
			retryCount++;

			if (retryCount == 10) {
				com.cmdTimeout();
			} else {
				ctrlThread = null;
				com.sendNext();
			}
		}
	}

	public String getCommand() {
		return command;
	}

	public void setResponse(String in_response) {
		if (ctrlThread != null) {
			ctrlThread.interrupt();
		}
		returned = true;
		called = true;
		response = in_response;

	}

	public void setError() {
		if (ctrlThread != null) {
			ctrlThread.interrupt();
		}
		returned = true;
		error = true;
	}
}

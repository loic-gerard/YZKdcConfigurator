/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yzkdcconfigurator;

import java.lang.reflect.Method;

public class TimelapsThread extends Thread{
    long waitTime;
    boolean interrupt = false;
    Object target;
    String method;
    
    public TimelapsThread(long ms, Object in_target, String in_method){
	waitTime = ms;
	target = in_target;
	method = in_method;
	start();
    }
    
    public void interrupt(){
	interrupt = true;
    }
    
    public void run(){
	try{
	    Thread.sleep(waitTime);
	}catch(Exception e){
	}

	if(!interrupt){
	    try{
		Class cl = target.getClass();
		String methodName = method;
		Method method = cl.getMethod(methodName);
		method.invoke(target); //
	    }catch(Exception e){
		e.printStackTrace();
	    }
	}
    }
}

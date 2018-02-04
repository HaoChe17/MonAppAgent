package com.che.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	static Logger log= Logger.getRootLogger();
	
	public static synchronized void writeLog(String level,String message){
		switch(level){
		case "debug":log.debug(message);break;
		case "info":log.info(message);break;
		case "warn":log.warn(message);break;
		case "error":log.error(message);break;
		case "fatal":log.fatal(message);break;
		}
	}
}

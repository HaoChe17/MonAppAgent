package com.che.service.mon;

import com.che.utils.ConfigData;
import com.che.utils.GetAppPid;
import com.che.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通过此类，可以获取所有的监控信息
 * @author chehao
 * @version 2017年10月3日 上午10:28:03
 */
@Service
@Slf4j
public class GetAllInfo {


	private GetAppPid getAppPid=new GetAppPid();
	/**
	 * 获取所有监控信息，整理为json，并返回
	 * @param jdkPath
	 * @param appKeyWord
	 * @param infoType
	 * @return
	 */
	public String getAllInfo(String jdkPath,String appKeyWord,String infoType){
		String allJsonInfo="{";
		String appName=appKeyWord;
		jdkPath=" /usr/java/jdk1.8.0_65/";
		if(jdkPath==null)log.error("the parameter 'jdkPath' of '"+appName+"' is null!");
		if(appName==null)log.error("the parameter 'appKeyWord' "+" is null!");
		if(infoType==null)log.debug("the parameter 'infoType' "+" is null! The default value is 'all'");

//		String pid="";
		String pid=getAppPid.getAppPid(appKeyWord).replace("\n", "").replace("\n\r", "");
		log.debug("the pid of "+appKeyWord+" is:"+pid);


		//获取监控信息的时间：
		String currentTime=System.currentTimeMillis()+"";

		//获取JVM监控信息：
		String jstatCmd=jdkPath+ConfigData.jstatBin;
		String jvmInfo=null;
		if(infoType.contains(ConfigData.jvmMonitorType)){
			jvmInfo=new GetJvmInfo().getJvmInfo(jstatCmd, pid);
		}

		//获取系统信息：
		String osInfo=null;
		if(infoType.contains(ConfigData.osMonitorType)){
			osInfo=new GetOsInfo().getOsInfoExcludeNet();
		}


		//获取应用pidstat信息：
		String appStatInfo=null;
		if(infoType.contains(ConfigData.appMonitorType)){
			appStatInfo=new GetAppInfo().getAppInfo(pid);
		}


		//组装成json数据：
		allJsonInfo+="\"time\":"+currentTime+",\"monitorInfo\":{\"jvmInfo\":"+jvmInfo+",\"osInfo\":"+osInfo+",\"appStatInfo\":"+appStatInfo+"}}";
		log.debug("allJsonInfo is : "+allJsonInfo);
		return allJsonInfo;
	}
}

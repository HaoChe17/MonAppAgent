package com.che.service.mon;

import com.che.utils.ConfigData;
import com.che.utils.ExecShellCmd;
import com.che.utils.GetSpecialDelimiterStr;
import com.che.utils.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 获取JVM的监控信息
 * @author chehao
 * @version 2017年9月27日 下午10:00:56
 */
@Slf4j
public class GetJvmInfo {
	private  String pid=null;
	private  String privateJstatCmd=null;
	private String delimiter=ConfigData.delimiter;

	private long lastYgc=0;
	private long lastFgc=0;
	private double lastYgct=0;
	private double lastFgct=0;
	private long thisYgc=0;
	private long thisFgc=0;
	private double thisYgct=0;
	private double thisFgct=0;

	/**
	 * 获取JVM信息的唯一方法。获取所有的jvm信息，整理为json，并返回。
	 * @param jstatCmd
	 * @param appPid
	 * @return
	 */
	public String getJvmInfo(String jstatCmd,String appPid){
		String jvmInfo="";
		privateJstatCmd=jstatCmd;
		pid=appPid;
		jvmInfo+=getGCInfo();
		return jvmInfo;
	}

	/**
	 * 获取GC的监控数据
	 */

	private String getGCInfo(){
		String gcJsonInfo="";
		String gcCommand=privateJstatCmd+pid;
		log.debug("the gcCommand of "+pid+" is:"+gcCommand);
		//获取jstat -gc命令的返回结果
		String gcInfo=new ExecShellCmd().exec(gcCommand);
//		String gcInfo=" S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT\n15872.0 15872.0  0.0   14272.6 666624.0 575722.8 1398272.0   387967.1  101760.0 99058.0 12160.0 11671.1    449   15.918   0      0.000   15.918";

		if(gcInfo==null || gcInfo.equals("")){
			log.error("获取gc信息失败，gcInfo："+gcInfo);
			return "";
		}
		log.debug("the gcInfo of "+pid+" is:"+gcInfo);

		//解析GC命令返回的结果，提取出满足要求的结果
		String[] gc_array=gcInfo.split(ConfigData.newLineSeparator);

		String[] gcKey_array=null;
		String[] gcValue_array=null;
		String gcKeyStr=null;
		String gcValueStr=null;
		Pattern pattern=Pattern.compile(ConfigData.gcValueregular);
		for(String str:gc_array){
			if(str.equals("") || str==null || str.length()<4){
				log.error("应用（"+pid+"）获取jvm信息失败");
				return "";
			}else if(str.substring(1,4).equals(ConfigData.gcKeyKeyWords)){
				gcKeyStr=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
				gcKey_array=gcKeyStr.split(delimiter);
				log.debug("gcKeyStr:"+gcKeyStr);
				continue;
			}else if(pattern.matcher(str.substring(0,3)).matches() || str.contains("0.0")){
				gcValueStr=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
				gcValue_array=gcValueStr.split(delimiter);
				log.debug("gcValueStr:"+gcValueStr);
				continue;
			}
		}

		if(gcKey_array==null){
			log.error("获取gc信息失败，gcKey_array为空");
			return "";
		}
		if(gcValue_array==null){
			log.error("获取gc信息失败，gcValue_array为空");
			return "";
		}
		if(gcKey_array.length != gcValue_array.length){
			log.error("get gcInfo is wrong! gcKeyInfo=["+gcKeyStr+"],gcValueStr="+gcValueStr);
		}
		//把结果组装成json格式的字符串
		for(int i=0;i<gcKey_array.length;i++){
//			gcJsonInfo=gcJsonInfo+'"'+gcKey_array[i]+"\":"+'"'+gcValue_array[i]+'"';
			if(gcKey_array[i].equals("YGC")){
				thisYgc=new Long(gcValue_array[i]);
				if (lastYgc==0)lastYgc=thisYgc;
				gcValue_array[i]=(thisYgc-lastYgc)+"";
				lastYgc=thisYgc;
			}
			if(gcKey_array[i].equals("YGCT")){
				thisYgct=new Double(gcValue_array[i]);
				if (lastYgct==0)lastYgct=thisYgct;
				gcValue_array[i]=(thisYgct-lastYgct)+"";
				lastYgct=thisYgct;
			}
			if(gcKey_array[i].equals("FGC")){
				thisFgc=new Long(gcValue_array[i]);
				if (lastFgc==0)lastFgc=thisFgc;
				gcValue_array[i]=(thisFgc-lastFgc)+"";
				lastFgc=thisFgc;
			}
			if(gcKey_array[i].equals("FGCT")){
				thisFgct=new Double(gcValue_array[i]);
				if (lastFgct==0)lastFgct=thisFgct;
				gcValue_array[i]=(thisFgct-lastFgct)+"";
				lastFgct=thisFgct;
			}
			gcJsonInfo=gcJsonInfo+'"'+gcKey_array[i]+"\":"+'"'+gcValue_array[i]+'"';
			if(i!=gcKey_array.length-1)gcJsonInfo+=",";
		}
		log.debug("gcJsonInfo:{"+gcJsonInfo+"}");
		return "{"+gcJsonInfo+"}";

	}

	/**
	 * 获取jvm的启动参数配置
	 * @param jvmStartParaCommand
	 * @param appKeyName
	 * @return
	 */
	public String getJvmStartPara(String jvmStartParaCommand,String appKeyName){
		log.debug("the jvmStartParaCommand of "+appKeyName+" is:"+jvmStartParaCommand);

		String jvmStartPara=new ExecShellCmd().exec(jvmStartParaCommand);
//		String jvmStartPara="Command line:  -javaagent:/alidata/server/portal-envent-center/OneAPM-portal-event-center-1.0-SNAPSHOT-exec/OneAPM/oneapm.jar -Dsun.misc.URLClassPath.disableJarChecking=true -Xmx2048M -Xms2048M -XX:MaxMetaspaceSize=128M -Xss256K -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:./GC_portal-event-center-1.0-SNAPSHOT-exec_20180125.log -Djava.awt.headless=true -Dspring.config.location=configs-portal-event-center-1.0-SNAPSHOT-exec/bootstrap.yml";
		if(jvmStartPara==null || jvmStartPara.equals("")){
			log.error("获取jvm启动信息失败，jvmStartPara："+jvmStartPara);
			return "";
		}

		return jvmStartPara.replace("Command line:  ","");
	}
}

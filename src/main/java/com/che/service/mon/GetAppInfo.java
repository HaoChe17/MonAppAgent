package com.che.service.mon;

import com.che.utils.ConfigData;
import com.che.utils.ExecShellCmd;
import com.che.utils.GetSpecialDelimiterStr;
import com.che.utils.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class GetAppInfo {
	public String pidstatCmd;
	private String delimiter=ConfigData.delimiter;
	/**
	 * 根据pidstat命令，获取应用本身的信息
	 * @param pid
	 * @return
	 */
	public String getAppInfo(String pid){
		String appInfoJson="";
		pidstatCmd=ConfigData.pidstatCmdPrefix+pid+ConfigData.pidstatCmdPrePost;
		String appInfo=new ExecShellCmd().exec(pidstatCmd);
		//String appInfo="Linux 2.6.32-220.23.2.al.ali1.2.alios6.x86_64 (iZbp17iei7ef2wxs183ljbZ)   01/19/2018      _x86_64_        (8 CPU)\n\n#      Time       PID    %usr %system  %guest    %CPU   CPU  minflt/s  majflt/s     VSZ    RSS   %MEM   kB_rd/s   kB_wr/s kB_ccwr/s  Command\n 1516327521     13101    1.00    0.00    0.00    1.00     1      0.00      0.00 7974700 1284056   3.91      0.00      0.00      0.00  java";
		if(appInfo==null || appInfo.equals("")){
			log.error("获取应用监控信息失败，appInfo：{}"+appInfo);
			return "";
		}

		log.debug("the pidstatCmd of "+pid+" is:"+pidstatCmd);
		log.debug("the pidstatCmd's executing result is:  "+appInfo);

		//把pidstat命令的结果，根据换行符分割成数组
		String[] appInfo_array=appInfo.split(ConfigData.newLineSeparator);
		String appInfoKey="";
		String appInfoValue="";
		//从结果数组中，筛选出需要的结果
		Pattern pattern = Pattern.compile("[0-9]*");//判断是否全为数字
		try{
			for(String str:appInfo_array){
				if(str.length()<5)continue;
				if(str.startsWith(ConfigData.appKeyLinePrefix)){
					appInfoKey=new GetSpecialDelimiterStr().getSpecialStr(str.replace("#", ""), delimiter);
					log.debug("The appInfoKey of "+pid+" is: "+appInfoKey);
					continue;
				}else if(pattern.matcher(str.substring(1, 10)).matches()){
					appInfoValue=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					log.debug("The appInfoKey of "+pid+" is: "+appInfoValue);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}



		String[] appInfoKey_array=appInfoKey.split(delimiter);
		String[] appInfoValue_array=appInfoValue.split(delimiter);
		if(appInfoKey_array.length != appInfoValue_array.length){
			log.error("get appInfo is wrong! appInfo=["+appInfoKey+"],gcValueStr="+appInfoValue);
			return "";
		}

		//把结果组装成json格式的字符串
		for(int i=0;i<appInfoKey_array.length;i++){
			appInfoJson=appInfoJson+'"'+appInfoKey_array[i]+"\":"+'"'+appInfoValue_array[i]+'"';
			if(i!=appInfoKey_array.length-1)appInfoJson+=",";
		}
		return "{"+appInfoJson+"}";
	}
}

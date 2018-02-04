package com.che.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 执行Linux-shell命令，并返回执行结果
 * @author chehao
 * @version 2017年9月24日 上午11:28:36
 */
@Service
@Slf4j
public class ExecShellCmd {

	public String exec(String shellCmd){
		String[] cmd=new String[]{"/bin/sh","-c",shellCmd};
		Process ps=null;
		try {
			ps=Runtime.getRuntime().exec(cmd);
			//Log.writeLog("debug", "execute the shell command\""+shellCmd+"\"");
			log.debug("execute the shell command\""+shellCmd+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			Log.writeLog("error", e.getMessage());
			log.error("执行命令"+shellCmd+"失败",e);
//			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while((line=br.readLine())!=null){
				sb.append(line).append("\n");
			}
//			Log.writeLog("info", "execute command \""+shellCmd+"\" success!");
//			Log.writeLog("debug", "the executing result of \""+shellCmd+"\":"+sb.toString());
			log.debug("the executing result of \""+shellCmd+"\":"+sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			Log.writeLog("error", "execute command \""+shellCmd+"\" failed!");
//			Log.writeLog("error", e.getMessage());
			log.error("execute command \"" + shellCmd + "\" failed!",e);
//			e.printStackTrace();
		}

		return sb.toString();
	}
}

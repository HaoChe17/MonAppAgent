package com.che.service.mon;

import com.che.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**获取系统监控数据
 *
 * @author chehao
 * @version 2017年10月7日 上午9:43:40
 */
@Slf4j
public class GetOsInfo {

	private static String delimiter=ConfigData.delimiter;
	private long lastRxbyte=0;
	private long lastSxByte=0;
	private double intervale;


	public String getOsInfo(int intervale){
		this.intervale=(double)intervale;
		String disk=getDiskInfo();
		String mem=getMemInfo();
		String cpu=getCpuInfo();
		String ioNet=getNetIo();
		return "{"+disk+","+mem+","+cpu+","+ioNet+"}";
	}

	public String getOsInfoExcludeNet(){
//		this.intervale=(double)intervale;
		String disk=getDiskInfo();
		String mem=getMemInfo();
		String cpu=getCpuInfo();
//		String ioNet=getNetIo();
		return "{"+disk+","+mem+","+cpu+","+"}";
	}

	/**
	 * 获取磁盘设备IO信息
	 * @return
	 */
	private String getDiskInfo(){
		String devJsonInfo="";
		String devCommand=ConfigData.devCommand;
		String devInfo_str=new ExecShellCmd().exec(devCommand);
		String[] devInfo_array=devInfo_str.split(ConfigData.newLineSeparator);

		String devInfoKey="";
		String[] devInfoKey_array=null;
		boolean exitDevice=false;
		Map<String,String[]> devsInfo_map=new HashMap<String,String[]>();
		int devMapKeyserial=1;
		String devMapKeyPrefix="device";
		String tmpInfo="";
		int DataNum=0;
		try{
			for(String str:devInfo_array){
				if(str.length()>4 && str.substring(0,6).equals(ConfigData.devKeyPrefix))DataNum++;
				if(DataNum==2){
					if(str.length()>4 && str.substring(0,6).equals(ConfigData.devKeyPrefix)){
						devInfoKey=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
						exitDevice=true;
						devInfoKey_array=devInfoKey.split(delimiter);
						log.debug("devKeyStr:"+devInfoKey);
						continue;
					}else if(exitDevice && str.length()>4){
						tmpInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
						devsInfo_map.put(devMapKeyPrefix+devMapKeyserial, tmpInfo.split(delimiter));
						log.debug(devMapKeyPrefix+devMapKeyserial+":"+tmpInfo);
						devMapKeyserial++;
						continue;
					}
				}
			}
		}catch(Exception e){
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}


		//判断结果解析是否正确
		if(devInfoKey_array==null || devInfoKey_array.length != devsInfo_map.get(devMapKeyPrefix+(devMapKeyserial-1)).length){
			log.error("get devInfo is wrong! devKeyInfo=["+devInfoKey+"],devValueStr="+tmpInfo);
			return "";
		}

		//组装json
		int devsNum=devMapKeyserial-1;
		devJsonInfo="\"diskNum\":"+devsNum;
		String[] dev_array=null;
		for(int i=0;i<devsNum;i++){
			dev_array=devsInfo_map.get(devMapKeyPrefix+(i+1));
			devJsonInfo+=",\""+dev_array[0]+"\":{";
			for(int j=1;j<dev_array.length;j++){
				devJsonInfo+="\""+devInfoKey_array[j]+"\":\""+dev_array[j]+"\"";
				if(j<dev_array.length-1)devJsonInfo+=",";
			}
			devJsonInfo+="}";
		}
		String diskLoadInfo="\"diskLoadInfo\":{"+devJsonInfo+"}";
		log.debug("diskLoadInfo:"+"\"devsInfo\":{"+diskLoadInfo+"}");

		//获取磁盘空间大小：
		String diskSpaceInfo="\"diskSpaceInfo\":"+getDiskSpace();

		return "\"disksInfo\":{"+diskLoadInfo+","+diskSpaceInfo+"}";
	}

	/**
	 * 获取磁盘空间大小信息
	 * @return
	 */
	private String getDiskSpace(){
		String diskSpaceJsonInfo="";
		String diskSpaceCommand=ConfigData.diskSpaceCommand;
		String diskSpaceInfo_str=new ExecShellCmd().exec(diskSpaceCommand);
		String[] diskSpaceInfo_array=diskSpaceInfo_str.split(ConfigData.newLineSeparator);

		//解析获取到的数据
		String diskSpaceInfoKey="";
		String[] diskSpaceInfoKey_array=null;
		boolean keyFlag=false;
		Map<String,String[]> diskSpaceInfo_map=new HashMap<String,String[]>();
		int diskSpaceMapKeyserial=0;
		String tmpInfo="";
		try{
			for(String str:diskSpaceInfo_array){
				log.debug("str's length:"+str.length()+",str is:"+str);
				if(str.length()<10)continue;
				if(str.substring(0,10).equals(ConfigData.diskSpaceKeyPrefixKeyWords)){
					diskSpaceInfoKey=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					keyFlag=true;
					diskSpaceInfoKey_array=diskSpaceInfoKey.split(delimiter);
					log.debug("diskSpaceKeyStr:"+diskSpaceInfoKey);
					continue;
				}else if(keyFlag && !str.contains(ConfigData.diskSpaceKeyPrefixKeyWords)){
					tmpInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					diskSpaceInfo_map.put(""+diskSpaceMapKeyserial, tmpInfo.split(delimiter));
					log.debug("disk"+diskSpaceMapKeyserial+":"+tmpInfo);
					diskSpaceMapKeyserial++;
					continue;
				}
			}
		}catch(Exception e){
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}

		//判断结果解析是否正确
		if(diskSpaceInfoKey_array.length-1 != diskSpaceInfo_map.get(""+(diskSpaceMapKeyserial-1)).length){
			log.error("get devInfo is wrong! diskSpaceKeyInfo=["+diskSpaceInfoKey+"],diskSpaceValueStr="+tmpInfo);
			return "";
		}

		//组装json
		for(int i=0;i<diskSpaceMapKeyserial;i++){
			String[] SpacePerDisk=diskSpaceInfo_map.get(i+"");
			diskSpaceJsonInfo+="\""+SpacePerDisk[0]+"\":{";
			for(int j=0;j<SpacePerDisk.length;j++){
				if(j==SpacePerDisk.length-1){
					diskSpaceJsonInfo+="\""+diskSpaceInfoKey_array[j]+"-"+diskSpaceInfoKey_array[j+1]+"\":\""+SpacePerDisk[j]+"\"";
				}else{diskSpaceJsonInfo+="\""+diskSpaceInfoKey_array[j]+"\":\""+SpacePerDisk[j]+"\"";}
				if(j<SpacePerDisk.length-1)diskSpaceJsonInfo+=",";
			}
			if(i<diskSpaceMapKeyserial-1)diskSpaceJsonInfo+="},";
			else{diskSpaceJsonInfo+="}";}
		}
		log.debug("diskSpaceInfo is:{"+diskSpaceJsonInfo+"}");
		return "{"+diskSpaceJsonInfo+"}";
	}

	/**
	 * 获取CPU信息
	 * @return
	 */
	private String getCpuInfo(){
		String cpuJsonInfo="";
		String cpuCommand=ConfigData.cpuCommand;
		String cpuInfo_str=new ExecShellCmd().exec(cpuCommand);
		String[] cpuInfo_array=cpuInfo_str.split(ConfigData.newLineSeparator);

		String cpuInfoKey="";
		String[] cpuInfoKey_array=null;
		Map<String,String[]> cpusInfo_map=new HashMap<String,String[]>();
		int devMapKey=0;
		String tmpInfo="";

		try{
			for(String str:cpuInfo_array){
				log.debug("str's length:"+str.length()+",str:"+str);
				if(str.length()<10)continue;
				if(str.length()>4 && str.substring(0,8).equals(ConfigData.cpuKeyPrefixKeyWords)  && str.contains(ConfigData.cpuKeyKeyWords)){
					cpuInfoKey=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					cpuInfoKey_array=cpuInfoKey.split(delimiter);
					log.debug("cpuKeyStr:"+cpuInfoKey);
					continue;
				}else if(str.substring(0,8).equals(ConfigData.cpuKeyPrefixKeyWords) && str.length()>4 && !str.contains(ConfigData.cpuKeyKeyWords)){
					tmpInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					cpusInfo_map.put(devMapKey+"", tmpInfo.split(delimiter));
					log.debug(devMapKey+":"+tmpInfo);
					devMapKey++;
					continue;
				}
			}
		}catch(Exception e){
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}

		String cpuFrameWork=new GetSpecialDelimiterStr().getSpecialStr(cpuInfo_array[0], delimiter).split(delimiter)[4];

		//组装json字符串
		log.debug("cpuFrameWork:"+cpuFrameWork);
		cpuJsonInfo+="\"cpuFrameWork\":\""+cpuFrameWork+"\",\"cpuCount\":\""+(devMapKey-1)+"\",\"cpuLoad\":{";
		for(int i=0;i<devMapKey;i++){
			String[] infoPerCpu=cpusInfo_map.get(i+"");
			cpuJsonInfo+="\""+infoPerCpu[1]+"\":{";
			for(int j=1;j<cpuInfoKey_array.length;j++){
				cpuJsonInfo+="\""+cpuInfoKey_array[j]+"\":\""+infoPerCpu[j]+"\"";
				if(j<cpuInfoKey_array.length-1)cpuJsonInfo+=",";
			}
			cpuJsonInfo+="}";
			if(i<devMapKey-1)cpuJsonInfo+=",";
		}

		cpuJsonInfo+="}";
		log.debug("cpuJsonInfo:{"+cpuJsonInfo+"}");
		return "\"cpuInfo\":{"+cpuJsonInfo+"}";
	}

	/**
	 * 获取内存信息
	 * @return
	 */
	private String getMemInfo(){
		String memJsonInfo="";
		String memCommand=ConfigData.memCommand;
		String memInfo_str=new ExecShellCmd().exec(memCommand);
		String[] memInfo_array=memInfo_str.split(ConfigData.newLineSeparator);

		String memInfoKey="";
		String tmpMemInfo="";
		String[] memInfoKey_array=null;
		Map<String,String[]> memsInfo_map=new HashMap<String,String[]>();
		int memMapKeyserial=1;
		String memMapKeyPrefix="mem";
		String tmpInfo="";
		try{
			for(String str:memInfo_array){
				log.debug(str.length()+"-str: "+str);
				if(str.contains(ConfigData.memKeyKeyWords) && str.length()>4){
					memInfoKey=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					memInfoKey_array=memInfoKey.split(delimiter);
					log.debug("memKeyStr:"+memInfoKey);
					continue;
				}else if(str.contains(ConfigData.memMemValueKeyWords) && str.length()>4){
					tmpMemInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					memsInfo_map.put("Mem", tmpMemInfo.split(delimiter));
					log.debug(memMapKeyPrefix+memMapKeyserial+":"+tmpMemInfo);
					memMapKeyserial++;
					continue;
				}else if(str.contains(ConfigData.memBcValueKeyWords) && str.length()>4){
					tmpInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					memsInfo_map.put("-/+", tmpInfo.split(delimiter));
					log.debug(memMapKeyPrefix+memMapKeyserial+":"+tmpInfo);
					memMapKeyserial++;
					continue;
				}else if(str.contains(ConfigData.memSwapValueKeyWords) && str.length()>4){
					tmpInfo=new GetSpecialDelimiterStr().getSpecialStr(str, delimiter);
					memsInfo_map.put("Swap", tmpInfo.split(delimiter));
					log.debug(memMapKeyPrefix+memMapKeyserial+":"+tmpInfo);
					memMapKeyserial++;
					continue;
				}
			}
		}catch(Exception e){
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}

		//判断结果解析是否正确
		if(memInfoKey_array==null || memInfoKey_array.length != memsInfo_map.get("Mem").length-1){
			log.error("get memInfo is wrong! memInfoKey_array=["+memInfoKey+"],memValueStr="+tmpMemInfo);
			return "";
		}

		//组装json
		log.debug("memInfoKey_array's length:"+memInfoKey_array.length+",Mem's length:"+memsInfo_map.get("Mem").length);
		for(int i=0;i<memInfoKey_array.length;i++){
			log.debug("memInfoKey_array:"+memInfoKey_array[i]+",Mem:"+memsInfo_map.get("Mem")[i+1]);
			memJsonInfo+="\"mem-"+memInfoKey_array[i]+"\":\""+memsInfo_map.get("Mem")[i+1]+"\",";
		}
		memJsonInfo+="\"-/+buf/cac-used\":\""+memsInfo_map.get("-/+")[2]+"\",\"-/+buf/cac-free\":\""+memsInfo_map.get("-/+")[3]+"\",";
		memJsonInfo+="\"Swap-total\":\""+memsInfo_map.get("Swap")[1]+"\",\"Swap-used\":\""+memsInfo_map.get("Swap")[2]+"\",\"Swap-free\":\""+memsInfo_map.get("Swap")[3]+"\"";

		log.debug("memJsonInfo:"+"\"memInfo\":{"+memJsonInfo+"}");
		return "\"memsInfo\":{"+memJsonInfo+"}";
	}

	/**
	 * 获取网络流量
	 * @return
	 */

//	public static void main(String[] args) {
////		new GetOsInfo().getNetIo();
//		JSONObject js=new JSONObject("{\"sxKbPerSec\":\"0.0\",\"rxKbPerSec\":\"0.0\"}");
//		System.out.println("json:"+js.toString());
//		System.out.println(js.getString("sxKbPerSec"));
//		for(String key:js.keySet()){
//			System.out.println(key+"="+js.getString(key));
//		}
//	}
	private String getNetIo(){
		String netCommand=ConfigData.netIoCommand;
		String ioNet=new ExecShellCmd().exec(netCommand);
//		String ioNet="371181597696,425324503285";
		if(ioNet==null || ioNet.equals("")){
			log.error("获取网络流量失败，ioNet:{}",ioNet);
			return "";
		}
		ioNet=ioNet.replace(" ","").replace("\t","").replace(ConfigData.newLineSeparator,"");
		String[] ioNetArr=ioNet.split(",");
		if(ioNetArr.length!=2)log.error("获取网络流量的数据格式有误，ioNet：{}",ioNet);
		Long thisRxByte=new Long(ioNetArr[0]);
		Long thisSxByte=new Long(ioNetArr[1]);
		if(lastRxbyte==0)lastRxbyte=thisRxByte;
		if (lastSxByte==0)lastSxByte=thisSxByte;

		double rxKbPerSec=new BigDecimal((thisRxByte-lastRxbyte)/intervale).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		double sxKbPerSec=new BigDecimal((thisSxByte-lastSxByte)/intervale).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

		lastRxbyte=thisRxByte;
		lastSxByte=thisSxByte;

		String ioNetJson="\""+ConfigData.thirdLevel_ioNetInfoKey+"\":"+"{\""+ConfigData.fourthLevel_rxKbPerSec+"\":\""+rxKbPerSec+
				"\",\""+ConfigData.fourthLevel_sxKbPerSec+"\":\""+sxKbPerSec+"\"}";
		log.debug("ioNetJson:{}",ioNetJson);
		return ioNetJson;
	}
}

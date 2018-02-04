package com.che.utils;

import com.che.pojo.App;
import com.che.pojo.Host;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 配置的设置
 * @author chehao
 * @version 2017年10月8日 上午8:35:14
 */
public class ConfigData {

	//自动监控配置
	public static boolean isAutoMon=true;//是否开启自动监控
	public static String getIpShellComd="ifconfig|sed -n 2p|awk '{print $2}'|awk -F \":\" '{print $2}'";//获取本机ip的shell命令
	public static String deafaultJdkPath="/usr/java/jdk1.8.0_65/";//默认的jdk路径
	public static int getJvmIntervalCounts=20;//获取jvm启动该参数的间隔次数，每次的时间为intervalTime
	public static String localIp;//本机ip；
	public static String intervalTime="5";//自动监控的间隔时间，单位：秒
	public static String influxdb_ip="172.16.31.69";
	public static String influxdb_port="8086";
	public static String appKeyWords="wechat";//需要自动监控的应用关键字名称，多个之间用逗号隔开
	public static Map<String,App> appMap=new HashMap<String,App>();//存放需要监控的应用的对象
	public static Map<String,Host> hostMap=new HashMap<String,Host>();//存放需要监控的服务器的对象
	public static String appKeyWordName="appKeyWords";
	public static String appMapKeys_app_ip="app_ip";
	public static String monMapKeys_app_cpuUsage_os="app_cpuUsage_os";//自动监控中需要收集的监控项
	public static String monMapKeys_app_cpuUsage_user="app_cpuUsage_user";
	public static String monMapKeys_app_cpuUsage_all="app_cpuUsage_all";
	public static String monMapKeys_app_memUsage="app_memUsage";
	public static String monMapKeys_app_kb_readPerSec="app_kb_readPerSec";
	public static String monMapKeys_app_kb_writePerSec="app_kb_writePerSec";
	public static String monMapKeys_jvm_ygc="jvm_ygc";
	public static String monMapKeys_jvm_ygct="jvm_ygct";
	public static String monMapKeys_jvm_fgc="jvm_fgc";
	public static String monMapKeys_jvm_fgct="jvm_fgct";
	public static String monMapKeys_jvm_gct="jvm_gct";
	public static String monMapKeys_jvm_s0c="jvm_S0C";
	public static String monMapKeys_jvm_s1c="jvm_S1C";
	public static String monMapKeys_jvm_ec="jvm_EC";
	public static String monMapKeys_jvm_oc="jvm_OC";
	public static String monMapKeys_jvm_mc="jvm_MC";
	public static String monMapKeys_jvm_ccsc="CCSC";
	public static String monMapKeys_os_readsPerSec="os_readsPerSec,,,";
	public static String monMapKeys_os_writesPerSec="os_writesPerSec";
	public static String monMapKeys_os_readKbPerSec="os_readKbPerSec";
	public static String monMapKeys_os_writeKbPerSec="os_writeKbPerSec";
	public static String monMapKeys_os_util="os_util";

	//全局配置
	public static String delimiter="\t";//处理获取的监控数据时，需要替换成的分隔符
	public static String newLineSeparator=System.getProperty("line.separator");//系统换行符

	//获取JVM信息
	public static String jstatBin="/bin/jstat -gc ";//执行的jstat命令
	public static String jinfoBin="/bin/jinfo ";//执行的jinfo命令
	public static String jinfoJvm=" |grep \"Command line:\"";//执行获取jvm启动参数的命令

	public static String jvmMonitorType="jvm";//判断需要获取的监控数据是否为jvm
	public static String gcValueregular="^\\d{3}";//判断是否为GC数据行的正则表达式。此行的前三个字符否均为数字
	public static String gcKeyKeyWords="S0C";//判断是否为GC列名行的关键字。此行的第2到4个字符串为“S0C”

	//获取os信息
	public static String osMonitorType="os";//判断需要获取的监控数据是否为os
	public static String devCommand="iostat -dx -k -c 1 2";//iostat命令，用于获取磁盘IO信息
	public static String devKeyPrefix="Device";//判断是否为磁盘列名行的关键字。此行以“Device”字符串开头
	public static String memCommand="free -k";//free命令，用于获取系统内存信息
	public static String netIoCommand="cat /proc/net/dev|grep eth0:|awk '{print $1\",\"$9}'|awk -F \":\" '{print $2}'";//获取网络流量
	public static String memKeyKeyWords="total";//判断是否为内存数据（free命令）的列名行。此行以包含“total”关键字
	public static String memMemValueKeyWords="Mem";//判断是否为内存数据（free命令）的Mem数据行。此行以包含Mem关键字
	public static String memBcValueKeyWords="buffers/cache";//判断是否为内存数据（free命令）的“buffers/cache”数据行。此行以包含“buffers/cache”关键字
	public static String memSwapValueKeyWords="Swap";//判断是否为内存数据（free命令）的“Swap”数据行。此行以包含“Swap”关键字
	public static String cpuCommand="mpstat -P ALL 1 1";//mpstat命令
	public static String cpuKeyPrefixKeyWords="Average:";//cpu监控数据的行以average开头
	public static String cpuKeyKeyWords="%iowait";//CPU列名所在的行包含了“%iowait”字符串
	public static String diskSpaceCommand="df -h";//获取磁盘空间大小的命令
	public static String diskSpaceKeyPrefixKeyWords="Filesystem";//磁盘空间大小数据的列名以“Filesystem”字符串开头

	//获取application信息
	public static String appMonitorType="app";//判断需要获取的监控数据是否为app
	public static String appPid;//进程pid
	public static String pidstatCmdPrefix="pidstat -udr -h -p ";//pidstat前缀
	public static String pidstatCmdPrePost=" 1 1";//pidstat后缀
	public static String appKeyLinePrefix="#";//用于判断pidstat的输出中，是否为列名所在行，此行以“#”开头
	public static String appValueLinePrefix="150";//用于判断pidstat的输出中，是否为数据所在行，此行以Unix时间戳开头


	//json格式监控数据的键
	public static final String firstLevel_monInfoKey="monitorInfo";

	public static final String secondLevel_osInfoKey="osInfo";
	public static final String secondLevel_jvmInfoKey="jvmInfo";
	public static final String secondLevel_appStatInfoKey="appStatInfo";

	public static final String thirdLevel_disksInfoKey="disksInfo";
	public static final String thirdLevel_memsInfoKey="memsInfo";
	public static final String thirdLevel_cpuInfoKey="cpuInfo";
	public static final String thirdLevel_ioNetInfoKey="ioNetInfo";
	public static final String thirdLevel_FGCKey="FGC";
	public static final String thirdLevel_FGCTKey="FGCT";
	public static final String thirdLevel_YGCKey="YGC";
	public static final String thirdLevel_YGCTKey="YGCT";
	public static final String thirdLevel_GCTKey="GCT";
	public static final String thirdLevel_S0CKey="S0C";
	public static final String thirdLevel_S1CKey="S1C";
	public static final String thirdLevel_ECKey="EC";
	public static final String thirdLevel_OCKey="OC";
	public static final String thirdLevel_MCKey="MC";
	public static final String thirdLevel_CCSCKey="CCSC";
	public static final String thirdLevel_appCpuKey="%CPU";
	public static final String thirdLevel_appMemKey="%MEM";
	public static final String thirdLevel_appCpuUserKey="%usr";
	public static final String thirdLevel_appCpuOsKey="%system";
	public static final String thirdLevel_appReadKbKey="kB_rd/s";
	public static final String thirdLevel_appWriteKbKey="kB_wr/s";

	public static final String fourthLevel_rxKbPerSec="rxKbPerSec";
	public static final String fourthLevel_sxKbPerSec="sxKbPerSec";
	public static final String fourthLevel_memTotalKey="mem-total";
	public static final String fourthLevel_bufCahcheUsedKey="-/+buf/cac-used";
	public static final String fourthLevel_diskLoadInfoKey="diskLoadInfo";
	public static final String fourthLevel_diskSpaceInfoKey="diskSpaceInfo";
	public static final String fourthLevel_cpuLoadKey="cpuLoad";

	public static final String fifthLevel_diskNumKey="diskNum";
	public static final String fifthLevel_allCpuKey="all";

	public static final String sixthLevel_diskUtilRateKey="%util";
	public static final String sixthLevel_diskMountedOnKey="Mounted-on";
	public static final String sixthLevel_diskSpaceUsedKey="Use%";
	public static final String sixthLevel_cpuIdleRateKey="%idle";

	/**读取配置数据
	 *
	 */
	public static void initConfigData(){

	}

}

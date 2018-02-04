package com.che.service.mon;

import com.che.dao.influxdb.SendMonAppToInfluxdb;
import com.che.dao.influxdb.SendMonHostToInfluxdb;
import com.che.pojo.App;
import com.che.pojo.Host;
import com.che.utils.ConfigData;
import com.che.utils.GetAppPid;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by che on 2018/1/18.
 */
@Slf4j
@Service
public class AutoMonitor extends Thread {
    private String appKeyWord;
    private String jstatCmd;
    private String pid;
    private String monType;

    private boolean isFirstForThisRunning=true;
    private int intervalTimes=1;
    private int intervalTime=5;

    private String jvmInfo=null;
    private String appStatInfo=null;
    private String osInfo=null;
    private String jvmStartPara=null;

    private Map<String,String> monInfoMap=new HashMap<String,String>();
    private Map<String,String> hostMonInfoMap=new HashMap<String,String>();
    private GetOsInfo getOsInfo=new GetOsInfo();
    private GetJvmInfo getJvmInfo=new GetJvmInfo();
    private String[] excludeKey={"Filesystem","Mounted-on"};

    private String key;
    private App app;
    private Host host;
    private JSONObject jsObject;
    JSONObject osJs;
    private JSONObject diskLoadInfoJs;
    private JSONObject diskJs;
    private JSONObject cpuLoadJs;
    private JSONObject cpuSingleJs;
    private JSONObject ioNetJs;
    private JSONObject cpuJs;
    private SendMonAppToInfluxdb sendMonAppToInfluxdb=new SendMonAppToInfluxdb();
    private SendMonHostToInfluxdb sendMonHostToInfluxdb=new SendMonHostToInfluxdb();



    private OperateApp operateApp;

    private GetAppPid getAppPid=new GetAppPid();



    public AutoMonitor(String key,OperateApp operateApp,String monType){
        this.monType=monType;
        this.key=key;
        this.operateApp=operateApp;
    }

    public AutoMonitor(Host host,String monType){
        this.monType=monType;
        this.host=host;
    }

    public AutoMonitor(){

    }

    public void run(){
        while (true){
            if(monType.equals("app")){
                //判断该应用监控是否还存在且自动监控是否有效
                if(!ConfigData.appMap.containsKey(key) || !ConfigData.isAutoMon)return;
                app=ConfigData.appMap.get(key);
                intervalTime=Integer.valueOf(app.getIntervalTime());
                this.appKeyWord=app.getAppKeyName();
                this.pid= getAppPid.getAppPid(appKeyWord).replace("\n", "").replace("\n\r", "");

                //获取应用监控的属性
                jstatCmd=app.getJdkPath()+ConfigData.jstatBin;

                Pattern pattern = Pattern.compile("[0-9]*");//判断是否全为数字
                if(pid==null || pid.equals("")){
                    log.error("\""+appKeyWord+"\"应用未启动，没有获取到pid！");
                }else if (pattern.matcher(pid).matches()){
                    jvmInfo=getJvmInfo.getJvmInfo(jstatCmd, pid);
//                osInfo=new GetOsInfo().getOsInfo();
//                    osInfo="{\"disksInfo\":{\"diskLoadInfo\":{\"diskNum\":2,\"vda\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.00\",\"r/s\":\"1.00\",\"w/s\":\"0.00\",\"rkB/s\":\"8.00\",\"wkB/s\":\"0.00\",\"avgrq-sz\":\"16.00\",\"avgqu-sz\":\"0.01\",\"await\":\"14.00\",\"svctm\":\"14.00\",\"%util\":\"1.40\"},\"vdb\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.00\",\"r/s\":\"0.00\",\"w/s\":\"0.00\",\"rkB/s\":\"0.00\",\"wkB/s\":\"0.00\",\"avgrq-sz\":\"0.00\",\"avgqu-sz\":\"0.00\",\"await\":\"0.00\",\"svctm\":\"0.00\",\"%util\":\"0.00\"}},\"diskSpaceInfo\":{\"/dev/vda1\":{\"Filesystem\":\"/dev/vda1\",\"Size\":\"40G\",\"Used\":\"18G\",\"Avail\":\"21G\",\"Use%\":\"45%\",\"Mounted-on\":\"/\"},\"tmpfs\":{\"Filesystem\":\"tmpfs\",\"Size\":\"16G\",\"Used\":\"0\",\"Avail\":\"16G\",\"Use%\":\"0%\",\"Mounted-on\":\"/dev/shm\"},\"/dev/vdb1\":{\"Filesystem\":\"/dev/vdb1\",\"Size\":\"500G\",\"Used\":\"371G\",\"Avail\":\"130G\",\"Use%\":\"75%\",\"Mounted-on\":\"/alidata\"}}},\"memsInfo\":{\"mem-total\":\"32881144\",\"mem-used\":\"32664888\",\"mem-free\":\"216256\",\"mem-shared\":\"0\",\"mem-buffers\":\"145960\",\"mem-cached\":\"19127304\",\"-/+buf/cac-used\":\"13391624\",\"-/+buf/cac-free\":\"19489520\",\"Swap-total\":\"0\",\"Swap-used\":\"0\",\"Swap-free\":\"0\"},\"cpuInfo\":{\"cpuFrameWork\":\"_x86_64_\",\"cpuCount\":\"8\",\"cpuLoad\":{\"all\":{\"CPU\":\"all\",\"%usr\":\"0.50\",\"%nice\":\"0.00\",\"%sys\":\"0.13\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.37\"},\"0\":{\"CPU\":\"0\",\"%usr\":\"2.02\",\"%nice\":\"0.00\",\"%sys\":\"1.01\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"96.97\"},\"1\":{\"CPU\":\"1\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"2\":{\"CPU\":\"2\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"3\":{\"CPU\":\"3\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"4\":{\"CPU\":\"4\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"5\":{\"CPU\":\"5\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.01\"},\"6\":{\"CPU\":\"6\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"7\":{\"CPU\":\"7\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"}}}}";
                    appStatInfo=new GetAppInfo().getAppInfo(pid);

                    //获取jvm启动参数
                    if(isFirstForThisRunning || app.getGetJvmIntervalCounts()==intervalTimes){
                        jvmStartPara=new GetJvmInfo().getJvmStartPara(app.getJdkPath()+ConfigData.jinfoBin+pid+ConfigData.jinfoJvm,appKeyWord);
                        app.setStartJvmPara(jvmStartPara);
                        operateApp.addAppStartJvmPara(app);
                        isFirstForThisRunning=false;
                        intervalTimes=0;
                    }
                    intervalTimes++;

                    //解析应用监控数据到map中
                    if(!appStatInfo.equals("") && appStatInfo!=null){
                        jsObject=new JSONObject(appStatInfo);
                        monInfoMap.put(ConfigData.monMapKeys_app_cpuUsage_os,jsObject.getString(ConfigData.thirdLevel_appCpuOsKey));
                        monInfoMap.put(ConfigData.monMapKeys_app_cpuUsage_user,jsObject.getString(ConfigData.thirdLevel_appCpuUserKey));
                        monInfoMap.put(ConfigData.monMapKeys_app_cpuUsage_all,jsObject.getString(ConfigData.thirdLevel_appCpuKey));
                        monInfoMap.put(ConfigData.monMapKeys_app_memUsage,jsObject.getString(ConfigData.thirdLevel_appMemKey));
                        monInfoMap.put(ConfigData.monMapKeys_app_kb_readPerSec,jsObject.getString(ConfigData.thirdLevel_appReadKbKey));
                        monInfoMap.put(ConfigData.monMapKeys_app_kb_writePerSec,jsObject.getString(ConfigData.thirdLevel_appWriteKbKey));
                    }
                    //解析jvm监控数据到map中
                    if(!jvmInfo.equals("") && jvmInfo!=null){
                        jsObject=new JSONObject(jvmInfo);
                        monInfoMap.put(ConfigData.monMapKeys_jvm_ygc,jsObject.getString(ConfigData.thirdLevel_YGCKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_ygct,jsObject.getString(ConfigData.thirdLevel_YGCTKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_fgc,jsObject.getString(ConfigData.thirdLevel_FGCKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_fgct,jsObject.getString(ConfigData.thirdLevel_FGCTKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_gct,jsObject.getString(ConfigData.thirdLevel_GCTKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_s0c,jsObject.getString(ConfigData.thirdLevel_S0CKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_s1c,jsObject.getString(ConfigData.thirdLevel_S1CKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_ec,jsObject.getString(ConfigData.thirdLevel_ECKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_oc,jsObject.getString(ConfigData.thirdLevel_OCKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_mc,jsObject.getString(ConfigData.thirdLevel_MCKey));
                        monInfoMap.put(ConfigData.monMapKeys_jvm_ccsc,jsObject.getString(ConfigData.thirdLevel_CCSCKey));
                    }

                    //解析系统监控数据到map中
                    if (osInfo!=null && !osInfo.equals("")){
                        jsObject=new JSONObject(osInfo);
                        for(String key:jsObject.keySet()){
                            osJs=jsObject.getJSONObject(key);
                            //系统硬盘信息解析为map
                            if(key.equals(ConfigData.thirdLevel_disksInfoKey)){
                                for (String diskKey:osJs.keySet()){
                                    diskLoadInfoJs=osJs.getJSONObject(diskKey);
                                    for(String diskLoadInfoJsKey:diskLoadInfoJs.keySet()){
                                        if(!diskLoadInfoJsKey.equals(ConfigData.fifthLevel_diskNumKey)){
                                            diskJs=diskLoadInfoJs.getJSONObject(diskLoadInfoJsKey);
                                            for (String diskJsKey:diskJs.keySet()){
                                                hostMonInfoMap.put(diskLoadInfoJsKey+"_"+diskJsKey,diskJs.getString(diskJsKey));
                                            }
                                        }
                                    }
                                }

                            }
                            //系统内存信息解析为map
                            else if (key.equals(ConfigData.thirdLevel_memsInfoKey)){
                                for(String memKey:osJs.keySet()){
                                    hostMonInfoMap.put(ConfigData.thirdLevel_memsInfoKey+"_"+memKey,osJs.getString(memKey));
                                }
                            }
                            //系统cpu信息解析为map
                            else if(key.equals(ConfigData.thirdLevel_cpuInfoKey)){
                                cpuLoadJs=osJs.getJSONObject(ConfigData.fourthLevel_cpuLoadKey);
                                for (String cpuLoadJsKey:cpuLoadJs.keySet()){
                                    cpuSingleJs=cpuLoadJs.getJSONObject(cpuLoadJsKey);
                                    for (String cpuSingeJsKey:cpuSingleJs.keySet()){
                                        hostMonInfoMap.put(cpuLoadJsKey+"_"+cpuSingeJsKey,cpuSingleJs.getString(cpuSingeJsKey));
                                    }
                                }
                            }
                            else {
                                log.error("系统监控信息的json字符串中存在未定义的Key值：{}",key);
                            }

                        }
                    }

                    //发送数据到influxdb
                    if(!monInfoMap.isEmpty())sendMonAppToInfluxdb.send(monInfoMap,app);
                    else {
                        log.error("获取\""+appKeyWord+"\"应用监控数据失败！");
                    }
                }else {
                    log.error(appKeyWord+"应用获取pid错误，pid："+pid);
                }
            }else if (monType.equals("host")){
                //判断该应用监控是否还存在且自动监控是否有效
                if(host==null || !ConfigData.isAutoMon){
                    log.error("未获取到当前主机的监控对象或未开启该客户端的自动监控功能");
                    return;
                }
                intervalTime=Integer.valueOf(host.getIntervalTime());
                osInfo=getOsInfo.getOsInfo(intervalTime);
//                osInfo="{\"disksInfo\":{\"diskLoadInfo\":{\"diskNum\":2,\"vda\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.00\",\"r/s\":\"1.00\",\"w/s\":\"0.00\",\"rkB/s\":\"8.00\",\"wkB/s\":\"0.00\",\"avgrq-sz\":\"16.00\",\"avgqu-sz\":\"0.01\",\"await\":\"14.00\",\"svctm\":\"14.00\",\"%util\":\"1.40\"},\"vdb\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.00\",\"r/s\":\"0.00\",\"w/s\":\"0.00\",\"rkB/s\":\"0.00\",\"wkB/s\":\"0.00\",\"avgrq-sz\":\"0.00\",\"avgqu-sz\":\"0.00\",\"await\":\"0.00\",\"svctm\":\"0.00\",\"%util\":\"0.00\"}},\"diskSpaceInfo\":{\"/dev/vda1\":{\"Filesystem\":\"/dev/vda1\",\"Size\":\"40G\",\"Used\":\"18G\",\"Avail\":\"21G\",\"Use%\":\"45%\",\"Mounted-on\":\"/\"},\"tmpfs\":{\"Filesystem\":\"tmpfs\",\"Size\":\"16G\",\"Used\":\"0\",\"Avail\":\"16G\",\"Use%\":\"0%\",\"Mounted-on\":\"/dev/shm\"},\"/dev/vdb1\":{\"Filesystem\":\"/dev/vdb1\",\"Size\":\"500G\",\"Used\":\"371G\",\"Avail\":\"130G\",\"Use%\":\"75%\",\"Mounted-on\":\"/alidata\"}}},\"memsInfo\":{\"mem-total\":\"32881144\",\"mem-used\":\"32664888\",\"mem-free\":\"216256\",\"mem-shared\":\"0\",\"mem-buffers\":\"145960\",\"mem-cached\":\"19127304\",\"-/+buf/cac-used\":\"13391624\",\"-/+buf/cac-free\":\"19489520\",\"Swap-total\":\"0\",\"Swap-used\":\"0\",\"Swap-free\":\"0\"},\"cpuInfo\":{\"cpuFrameWork\":\"_x86_64_\",\"cpuCount\":\"8\",\"cpuLoad\":{\"all\":{\"CPU\":\"all\",\"%usr\":\"0.50\",\"%nice\":\"0.00\",\"%sys\":\"0.13\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.37\"},\"0\":{\"CPU\":\"0\",\"%usr\":\"2.02\",\"%nice\":\"0.00\",\"%sys\":\"1.01\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"96.97\"},\"1\":{\"CPU\":\"1\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"2\":{\"CPU\":\"2\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"3\":{\"CPU\":\"3\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"4\":{\"CPU\":\"4\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"5\":{\"CPU\":\"5\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.01\"},\"6\":{\"CPU\":\"6\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"7\":{\"CPU\":\"7\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"}}}}";
                //解析系统监控数据到map中
                if (!osInfo.equals("") && osInfo!=null){
                    jsObject=new JSONObject(osInfo);
                    for(String key:jsObject.keySet()){
                        osJs=jsObject.getJSONObject(key);
                        //系统硬盘信息解析为map
                        if(key.equals(ConfigData.thirdLevel_disksInfoKey)){
                            for (String diskKey:osJs.keySet()){
                                diskLoadInfoJs=osJs.getJSONObject(diskKey);
                                for(String diskLoadInfoJsKey:diskLoadInfoJs.keySet()){
                                    if(!diskLoadInfoJsKey.equals(ConfigData.fifthLevel_diskNumKey)){
                                        diskJs=diskLoadInfoJs.getJSONObject(diskLoadInfoJsKey);
                                        for (String diskJsKey:diskJs.keySet()){
                                            if(diskJsKey.equals("Mounted-on") || diskJsKey.equals("Filesystem"))continue;
                                            hostMonInfoMap.put(diskLoadInfoJsKey+"_"+diskJsKey,
                                                    diskJs.getString(diskJsKey).replace("%","").replace("G",""));
                                        }
                                    }
                                }
                            }

                        }
                            //系统内存信息解析为map
                            else if (key.equals(ConfigData.thirdLevel_memsInfoKey)){
                                for(String memKey:osJs.keySet()){
                                    hostMonInfoMap.put(ConfigData.thirdLevel_memsInfoKey+"_"+memKey,osJs.getString(memKey));
                                }
                            }
                            //系统cpu信息解析为map
                            else if(key.equals(ConfigData.thirdLevel_cpuInfoKey)){
                                cpuLoadJs=osJs.getJSONObject(ConfigData.fourthLevel_cpuLoadKey);
                                for (String cpuLoadJsKey:cpuLoadJs.keySet()){
                                    cpuSingleJs=cpuLoadJs.getJSONObject(cpuLoadJsKey);
                                    for (String cpuSingeJsKey:cpuSingleJs.keySet()){
                                        hostMonInfoMap.put(cpuLoadJsKey+"_"+cpuSingeJsKey,cpuSingleJs.getString(cpuSingeJsKey).replace("all","9999"));
                                    }
                                }
                            }
                            //系统网络io解析为map
                             else if (key.contentEquals(ConfigData.thirdLevel_ioNetInfoKey)){
                                for(String ioNetKey:osJs.keySet()){
                                    hostMonInfoMap.put(ioNetKey,osJs.getString(ioNetKey));
                                }
                            }
                            else {
                                log.error("系统监控信息的json字符串中存在未定义的Key值：{}",key);
                            }

                        }
                    }

//                    for (String str:hostMonInfoMap.keySet()){
//                        log.info("------"+str+":{}-----",hostMonInfoMap.get(str));
//                    }

                    //发送数据到influxdb
                    if(!hostMonInfoMap.isEmpty())sendMonHostToInfluxdb.send(hostMonInfoMap,host);
                    else {
                        log.error("获取\""+host.getIp()+"\"服务器监控数据失败！");
                    }
                }else {
                    log.error("监控类型错误，只能是app或host！");
                }



                try {
                    Thread.sleep(intervalTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }


    }
}

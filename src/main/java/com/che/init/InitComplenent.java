package com.che.init;

import com.che.pojo.Host;
import com.che.service.mon.AutoMonitor;
import com.che.service.mon.OperateApp;
import com.che.service.mon.OperateHost;
import com.che.utils.ConfigData;
import com.che.utils.ExecShellCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by che on 2018/1/23.
 */
@Component
@Slf4j
//public class InitComplenent{
public class InitComplenent implements CommandLineRunner {
    @Autowired
    private OperateApp operateApp;
    @Autowired
    private OperateHost operateHost;

    @Autowired
    ExecShellCmd execShellCmd;

//    @PostConstruct
    public void init(){

        log.info("开始获取当前服务器的ip……");
        ConfigData.localIp=execShellCmd.exec(ConfigData.getIpShellComd).replace(ConfigData.newLineSeparator,"");
//        ConfigData.localIp="172.16.31.11";
        log.info("当前服务器的ip："+ConfigData.localIp);

        log.info("获取被监控应用的初始化数据……");
        ConfigData.appMap=operateApp.findAllAppsByIp(ConfigData.localIp);
        Host host=operateHost.addHost(new Host(ConfigData.localIp,ConfigData.intervalTime));

        if(ConfigData.isAutoMon){
            new AutoMonitor(host,"host").start();
            if(!ConfigData.appMap.isEmpty()){
                for(String key:ConfigData.appMap.keySet()){
                    new AutoMonitor(key,operateApp,"app").start();

                }
            }
        }

 //       log.info("---------------------------------开启新线程成功！-----------------------");
    }

    @Override
    public void run(String... strings) throws Exception {
        init();
    }
}

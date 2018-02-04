package com.che.service.mon;

import com.che.dao.mapper.AppMapper;
import com.che.dao.mapper.HostMapper;
import com.che.pojo.App;
import com.che.pojo.Host;
import com.che.utils.ConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by che on 2018/1/28.
 */
@Service
@Slf4j
public class OperateHost {

    @Autowired
    HostMapper hostMapper;

    public Host addHost(Host host){
        Host oldHost=hostMapper.findHostByMeasurement(host.getMeasurement());
        if(oldHost==null){
            hostMapper.addHost(host);
            ConfigData.hostMap.put(host.getMeasurement(),host);
//            new AutoMonitor(host.getMeasurement(),"host").start();
            log.info("已把当前服务器{}纳入监控",host.getIp());
            return host;
        }else {
            log.info("当前服务器{}已经纳入监控",oldHost.getIp());
            return oldHost;
        }


    }

//    public HashMap<String,Host> findAllHosts(){
//        return hostMapper.findAllHostsByIp();
//    }
//
//    public String removeHostByMeasurement(){
//
//        String measurement=ConfigData.localIp;
//        Host oldHost=hostMapper.findHostByMeasurement(measurement);
//        if(oldHost==null){
//            return "the app \""+measurement+"\" is not exiting.";
//        }else if (oldHost.isIsvalid()==false){
//
//            return "the app \""+oldHost.getIp()+"\" has already been removed";
//        }else {
//            ConfigData.hostMap.remove(measurement);
//            hostMapper.deleteHostByMeasurement(measurement);
//            return "remove \""+measurement+"\" successful!";
//        }
//
//
//    }
}

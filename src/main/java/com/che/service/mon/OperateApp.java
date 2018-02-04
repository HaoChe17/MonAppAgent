package com.che.service.mon;

import com.che.dao.mapper.AppMapper;
import com.che.pojo.App;
import com.che.utils.ConfigData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by che on 2018/1/22.
 */
@Service
public class OperateApp {

    @Autowired
    AppMapper appMapper;

    public String addApp(App app){
        App oldApp=appMapper.findAppByMeasurement(app.getMeasurement());
        if(oldApp==null){
            appMapper.addApp(app);
            ConfigData.appMap.put(app.getMeasurement(),app);
            new AutoMonitor(app.getMeasurement(),this,"app").start();
            return "add \""+app.getAppKeyName()+"\" successfule!";
        }else if (oldApp.isIsvalid()==false){
            oldApp.setIsvalid(true);
            appMapper.recoverAppIsvalidByMeasurement(oldApp.getMeasurement());
            ConfigData.appMap.put(oldApp.getMeasurement(),oldApp);
            new AutoMonitor(oldApp.getMeasurement(),this,"app").start();
            return "add \""+oldApp.getAppKeyName()+"\" successfule!";
        }else {
            return "add \""+app.getAppKeyName()+"\" failed! It has already existed。 ";
        }


    }

    public HashMap<String,App> findAllAppsByIp(String ip){
        return appMapper.findAllAppsByIp(ip);
    }

    public String removeAppByMeasurement(String appKeyName){

        String measurement=ConfigData.localIp+"_"+appKeyName;
        App oldApp=appMapper.findAppByMeasurement(measurement);
        if(oldApp==null){
            return "the app \""+appKeyName+"\" is not exiting.";
        }else if (oldApp.isIsvalid()==false){

            return "the app \""+oldApp.getAppKeyName()+"\" has already been removed";
        }else {
            ConfigData.appMap.remove(measurement);
            appMapper.deleteAppByMeasurement(measurement);
            return "remove \""+appKeyName+"\" successful!";
        }


    }

    public void addAppStartJvmPara(App app){
        appMapper.addAppJvmParaByMeasurement(app);
    }
}

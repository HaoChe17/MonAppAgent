package com.che.controller;

import com.che.pojo.App;
import com.che.service.mon.AutoMonitor;
import com.che.service.mon.OperateApp;
import com.che.utils.ConfigData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by che on 2018/1/22.
 */
@RestController
public class MonSetController {

    @Autowired
    OperateApp operateApp;

    @GetMapping("addMonApp")
    public String addMonApp(@RequestParam(value = "akn",required = true) String appKeyName,
                            @RequestParam(value = "jp",required = false) String jdkPath,
                            @RequestParam(value = "it",required = false) String intervalTime){
        if(jdkPath==null)jdkPath=ConfigData.deafaultJdkPath;
        if(intervalTime==null)intervalTime=ConfigData.intervalTime;
        App app=new App(ConfigData.localIp,appKeyName, jdkPath,intervalTime);


        return operateApp.addApp(app);
    }


    @GetMapping("remove")
    public String removeApp(@RequestParam(value = "akn",required = true)String appKeyName){

        return operateApp.removeAppByMeasurement(appKeyName);
    }

}

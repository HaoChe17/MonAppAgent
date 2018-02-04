package com.che.pojo;

import com.che.utils.ConfigData;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by che on 2018/1/22.
 */
@Getter
@Setter
public class App {
    private String ip;
    private String appKeyName;
    private String jdkPath;
    private String measurement;
    private String intervalTime;//采集数据的间隔时间
    private long id;
    private boolean isvalid=true;//该应用的监控是否还有效。1，有效；0，无效。
    private int getJvmIntervalCounts=20;//获取jvm应用配置的间隔次数
    private String startJvmPara;//JVM启动参数

    public App(String ip,String appKeyName){
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.measurement=ip+"_"+appKeyName;
    }

    public App(String ip,String appKeyName,String jdkPath){
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.jdkPath=jdkPath;
        this.measurement=ip+"_"+appKeyName;
    }

    public App(String ip,String appKeyName,String jdkPath,String intervalTime){
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.jdkPath=jdkPath;
        this.intervalTime=intervalTime;
        this.measurement=ip+"_"+appKeyName;
    }

    public App(long id,String ip,String appKeyName,String jdkPath,String measurement,String intervalTime){
        this.id=id;
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.jdkPath=jdkPath;
        this.measurement=measurement;
        this.intervalTime=intervalTime;
    }

    public App(long id,String ip,String appKeyName,String jdkPath,String measurement,String intervalTime,boolean isvalid){
        this.id=id;
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.jdkPath=jdkPath;
        this.measurement=measurement;
        this.intervalTime=intervalTime;
        this.isvalid=isvalid;
    }

    public App(long id,String ip,String appKeyName,String jdkPath,String measurement,String intervalTime,boolean isvalid,int getJvmIntervalCounts,String startJvmPara){
        this.id=id;
        this.ip=ip;
        this.appKeyName=appKeyName;
        this.jdkPath=jdkPath;
        this.measurement=measurement;
        this.intervalTime=intervalTime;
        this.isvalid=isvalid;
        this.getJvmIntervalCounts=getJvmIntervalCounts;
        this.startJvmPara=startJvmPara;
    }
}

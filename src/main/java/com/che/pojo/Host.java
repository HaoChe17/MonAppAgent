package com.che.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by che on 2018/1/28.
 */
@Setter
@Getter
public class Host {
    private String ip;
    private String measurement;
    private String intervalTime;//采集数据的间隔时间
    private long id;

    public Host(){}

    public Host(String ip,String intervalTime){
        this.ip=ip;
        this.measurement=ip;
        this.intervalTime=intervalTime;
    }

    public Host(String ip,String measurement,String intervalTime,long id){
        this.ip=ip;
        this.measurement=measurement;
        this.intervalTime=intervalTime;
        this.id=id;
    }

}

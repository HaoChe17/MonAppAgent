package com.che.dao.mapper;

import com.che.pojo.App;
import com.che.pojo.Host;
import org.apache.ibatis.annotations.MapKey;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * Created by che on 2018/1/28.
 */
@MapperScan
@Repository
public interface HostMapper {


    public Host findHostByIp(String ip);

    public Host findHostByMeasurement(String measurement);

    public void addHost(Host Host);

//    public void deleteHostByMeasurement(String measurement);
//
//    public void recoverHostIsvalidByMeasurement(String measurement);
}

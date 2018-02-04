package com.che.dao.mapper;

import com.che.pojo.App;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by che on 2018/1/22.
 */
@Repository
@Mapper
public interface AppMapper {
    @MapKey("measurement")
    public HashMap<String,App> findAllAppsByIp(String ip);

    public App findAppByMeasurement(String measurement);

    public void addApp(App app);

    public void deleteAppByMeasurement(String measurement);

    public void recoverAppIsvalidByMeasurement(String measurement);

    public void addAppJvmParaByMeasurement(App app);
}

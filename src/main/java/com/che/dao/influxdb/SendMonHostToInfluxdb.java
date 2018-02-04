package com.che.dao.influxdb;

import com.che.pojo.App;
import com.che.pojo.Host;
import com.che.utils.ConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by che on 2018/1/28.
 */
@Slf4j
public class SendMonHostToInfluxdb {

    public boolean send(Map<String,String> hostMonMap, Host host){
        //拼接请求url
        String url=new StringBuffer("http://").append(ConfigData.influxdb_ip).append(":").append(ConfigData.influxdb_port).append("/write?db=mon").toString();
        log.debug("influxdb url：{}",url);
        //拼接请求数据
        String measurement=host.getMeasurement();
        StringBuffer insertDataBuf=new StringBuffer(measurement)
                .append(",host=").append(host.getIp());
        StringBuffer monDataBuf=new StringBuffer(" ");
        for (String key:hostMonMap.keySet()){
            monDataBuf.append(",").append(key).append("=").append(hostMonMap.get(key));
        }
        insertDataBuf.append(monDataBuf.toString().replaceFirst(",",""));
        log.debug("data:"+insertDataBuf.toString());

        //发送请求数据
        RestTemplate restTemplate=new RestTemplate();
        HttpEntity<String> request=new HttpEntity<String>(insertDataBuf.toString());
        ResponseEntity<String> response=restTemplate.postForEntity(url,request,String.class);
        log.debug("the influxdb url:"+url);
        log.debug("the influxdb request data:"+insertDataBuf.toString());
        String httpStatus=response.getStatusCode().toString();

        if(httpStatus.equals("204")){
            log.debug("influxdb request successful! the http status:"+httpStatus);
            return true;
        }else{
            log.error("influxdb request failed! the http status:"+httpStatus);
            return false;
        }
    }
}

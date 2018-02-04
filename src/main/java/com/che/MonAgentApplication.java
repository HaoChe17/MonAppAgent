package com.che;

import com.che.pojo.App;
import com.che.service.mon.AutoMonitor;
import com.che.service.mon.OperateApp;
import com.che.utils.ConfigData;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.che.dao.mapper")
public class MonAgentApplication {



	public static void main(String[] args) {
		SpringApplication.run(MonAgentApplication.class, args);
	}


}

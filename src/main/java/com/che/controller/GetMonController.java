package com.che.controller;

import com.che.service.mon.GetAllInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by che on 2018/1/17.
 */
@RestController
public class GetMonController {
    @Autowired
    GetAllInfo getAllInfo;

    @GetMapping("getInfo")
    public String getJsonMonInfo(String akw,String it){
        return getAllInfo.getAllInfo("",akw,it);
    }
}

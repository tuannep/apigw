package com.leadon.apigw.service.impl;

import com.leadon.apigw.repository.UserLogRepo;
import com.leadon.apigw.web.entity.UserLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;

@Service
public class UserLogServiceImpl {

    @Autowired
    private UserLogRepo repo;

    public void traceLogUser(UserLogEntity entity) {
        if ("0:0:0:0:0:0:0:1".equals(entity.getIp())) {
            entity.setIp("127.0.0.1");
        }


        entity.setActionTime(new Date());
        repo.save(entity);

    }
}

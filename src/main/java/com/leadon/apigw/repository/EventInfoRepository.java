package com.leadon.apigw.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.time.Instant;

@Slf4j
@Repository
public class EventInfoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String sql = "INSERT INTO eventinfo(eventid , type_id,hostname,name,itemname,descript,lasttime) VALUES (seq_eventinfo.nextval ,?,?,?,?,?,?)";

    public void insetLog(String nameMethod , Exception exception) {

        try {
            InetAddress ip = InetAddress.getLocalHost();

            jdbcTemplate.update(sql, preparedStatement -> {
                preparedStatement.setInt(1 , 1);
                preparedStatement.setString(2 , ip.getHostName());
                preparedStatement.setString(3 , "ACH Gateway");
                preparedStatement.setString(4 , nameMethod);
                preparedStatement.setString(5 , exception.getMessage());
                preparedStatement.setLong(6 , Instant.now().getEpochSecond());

            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }


}

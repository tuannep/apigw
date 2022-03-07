package com.leadon.apigw.repository;

import com.leadon.apigw.web.entity.UserLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface UserLogRepo extends JpaRepository<UserLogEntity, Long> {

    @Query("select u from UserLogEntity u where ( :username is null or u.username = :username) and " +
            "u.actionTime between to_date(:fromDt, 'yyyy-MM-dd HH24:mi:ss') and to_date(:toDt, 'yyyy-MM-dd HH24:mi:ss') \n")
    Page<UserLogEntity> findByUsername(@Param("username") String username ,
                                       @Param("fromDt") String fromDt,
                                       @Param("toDt") String toDt ,
                                       Pageable pageable);

}

package com.leadon.apigw.repository;


import com.leadon.apigw.web.entity.RoleEntity;
import com.leadon.apigw.web.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<UserEntity> findAll(Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE (:username is null OR u.username LIKE CONCAT('%',lower(:username),'%') ) AND (:roleEntity is null OR u.role = :roleEntity) ")
    List<UserEntity> findByUsernameAndRole(@Param("username") String username , @Param("roleEntity") RoleEntity roleEntity);


}

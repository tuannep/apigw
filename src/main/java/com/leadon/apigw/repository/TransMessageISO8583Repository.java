package com.leadon.apigw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.leadon.apigw.model.TransMessageIso8583;

public interface TransMessageISO8583Repository extends JpaRepository<TransMessageIso8583, Long>, com.leadon.apigw.repository.TransMessageISO8583RepositoryCustom {

}

package com.leadon.apigw.repository;

import com.leadon.apigw.model.AchCustomerInfo;
import com.leadon.apigw.model.key.AchCustomerInfoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Repository
public interface AchCustomerInfoRepo extends JpaRepository<AchCustomerInfo, AchCustomerInfoKey> {
    @Transactional(readOnly = true)
    @Query(value = "SELECT T.* FROM ACH_CUSTOMER_INFO T WHERE T.CDTR_ACCT_NO = :cdtrAcctNo", nativeQuery = true)
    Stream<AchCustomerInfo> getAchCustomerByAccNo(@Param(value = "cdtrAcctNo") String cdtrAcctNo);
}

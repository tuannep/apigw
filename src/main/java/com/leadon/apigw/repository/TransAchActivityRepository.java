package com.leadon.apigw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.leadon.apigw.model.TransAchActivity;

public interface TransAchActivityRepository extends JpaRepository<TransAchActivity, Long>, com.leadon.apigw.repository.TransAchActivityRepositoryCustom {

//    @Query(value = "SELECT T.MSG_CONTENT FROM TRANS_ACH_ACTIVITY AS T " +
//            "WHERE T.SENDER_REF_ID = :senderRefId AND T.MSG_IDENTIFIER = :msgIdentifier " +
//            "AND T.ACTIVITY_STEP = :activityStep", nativeQuery = true)
//    Clob selectMessage(@Param(value = "senderRefId") String senderRefId,
//                       @Param(value = "msgIdentifier") String msgIdentifier,
//                       @Param(value = "activityStep") String activityStep);

    @Query(value = "SELECT T.ACTIVITY_ID FROM TRANS_ACH_ACTIVITY T " +
            "WHERE T.TRANS_ID = :transId AND T.MSG_IDENTIFIER = :msgIdentifier AND T.ACTIVITY_STEP != :activityStep", nativeQuery = true)
    Long checkExsitIso8583ToBank(@Param(value = "transId") Long transId, @Param(value = "msgIdentifier") String msgIdentifier
            , @Param(value = "activityStep") String activityStep);

    @Query(value = "SELECT T.ACTIVITY_ID FROM TRANS_ACH_ACTIVITY AS T " +
            "WHERE T.TRANS_ID = :transId AND T.MSG_IDENTIFIER = :msgIdentifier AND T.ACTIVITY_STEP = :activityStep", nativeQuery = true)
    Long checkExsitIso8583ForPacs004ToBank(@Param(value = "transId") Long transId, @Param(value = "msgIdentifier") String msgIdentifier
            , @Param(value = "activityStep") String activityStep);

    @Query(value = "SELECT T.ERR_CODE FROM (SELECT * FROM TRANS_ACH_ACTIVITY ORDER BY ACTIVITY_ID DESC) AS T " +
            "WHERE T.TRANS_ID = :transId AND T.MSG_IDENTIFIER = :msgIdentifier AND ROWNUM = 1", nativeQuery = true)
    String selectErrCode(@Param(value = "transId") Long transId, @Param(value = "msgIdentifier") String msgIdentifier);

    @Query(value = "SELECT COUNT(1) FROM TRANS_ACH_DETAIL AS T " +
            "WHERE T.ORG_SENDER_REF_ID = :orgSenderRefId AND T.IS_COPY = '1'", nativeQuery = true)
    int checkIsCopyReturn(@Param(value = "orgSenderRefId") String orgSenderRefId);

    @Query(value = "SELECT COUNT(1) FROM TRANS_ACH_ACTIVITY AS T " +
            "WHERE T.TRANS_ID = :transId AND T.MSG_IDENTIFIER = :msgIdentifier", nativeQuery = true)
    int checkExsitUpdatedPacs002(@Param(value = "transId") Long transId, @Param(value = "msgIdentifier") String msgIdentifier);

    @Query(value = "SELECT T.ACTIVITY_ID FROM TRANS_ACH_ACTIVITY AS T " +
            "WHERE T.SENDER_REF_ID = :senderRefId AND T.MSG_IDENTIFIER = :msgIdentifier AND T.ACTIVITY_STEP = :activityStep", nativeQuery = true)
    Long checkExsitIso8583ForPacs004InCamt034ToBank(@Param(value = "senderRefId") String senderRefId, @Param(value = "msgIdentifier") String msgIdentifier
            , @Param(value = "activityStep") String activityStep);
}

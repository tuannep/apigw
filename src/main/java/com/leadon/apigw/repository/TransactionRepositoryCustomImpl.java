package com.leadon.apigw.repository;

import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchActivity;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;
import org.hibernate.procedure.ProcedureOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.util.*;

public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    public static Logger logger = LoggerFactory.getLogger(TransactionRepositoryCustomImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DataObj initTrans(Transaction trans) {
        return initTrans(trans, new TransAchDetail(), new TransAchActivity(), "1");
    }

    @Override
    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail) {
        return initTrans(trans, transAchDetail, new TransAchActivity(), "2");
    }
    @Override
    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail, TransAchActivity transAchActivity, String initType) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_INIT_TRANS")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.INOUT)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(12, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(13, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(14, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(15, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(16, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(17, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(18, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(19, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(20, BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter(21, BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter(22, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(23, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(24, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(25, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(26, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(27, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(28, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(29, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(30, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(31, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(32, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(33, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(34, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(35, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(36, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(37, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(38, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(39, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(40, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(41, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(42, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(43, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(44, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(45, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(46, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(47, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(48, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(49, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(50, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(51, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(52, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(53, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(54, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(55, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(56, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(57, Long.class, ParameterMode.INOUT)
                .registerStoredProcedureParameter(58, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(59, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(60, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(61, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(62, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(63, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(64, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(65, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(66, String.class, ParameterMode.OUT)
                //1:transId
                .setParameter(2, trans.getXrefId())
                .setParameter(3, trans.getClientId())
                .setParameter(4, trans.getChannelId())
                .setParameter(5, trans.getServiceGroupId())
                .setParameter(6, trans.getServiceId())
                .setParameter(7, trans.getTransCate())
                .setParameter(8, trans.getTransInout())
                .setParameter(9, trans.getTransDesc())
                .setParameter(10, trans.getAmount())
                .setParameter(11, trans.getCcy())
                .setParameter(12, trans.getTransDt())
                .setParameter(13, trans.getTransStat())
                .setParameter(14, trans.getTransStatDesc())
                .setParameter(15, transAchDetail.getOrgTransId())
                .setParameter(16, transAchDetail.getSenderRefId())
                .setParameter(17, transAchDetail.getOrgSenderRefId())
                .setParameter(18, transAchDetail.getRefSenderRefId())
                .setParameter(19, transAchDetail.getMsgIdentifier())
                .setParameter(20, transAchDetail.getFeeAmount())
                .setParameter(21, transAchDetail.getVatAmount())
                .setParameter(22, transAchDetail.getSettleDt())
                .setParameter(23, transAchDetail.getSettleMtd())
                .setParameter(24, transAchDetail.getInstrId())
                .setParameter(25, transAchDetail.getEndtoendId())
                .setParameter(26, transAchDetail.getTxId())
                .setParameter(27, transAchDetail.getChargeBr())
                .setParameter(28, transAchDetail.getDbtrBrn())
                .setParameter(29, transAchDetail.getDbtrName())
                .setParameter(30, transAchDetail.getDbtrAddress())
                .setParameter(31, transAchDetail.getDbtrAcctType())
                .setParameter(32, transAchDetail.getDbtrAcctNo())
                .setParameter(33, transAchDetail.getDbtrMemId())
                .setParameter(34, transAchDetail.getDbtrMemCode())
                .setParameter(35, transAchDetail.getCdtrBrn())
                .setParameter(36, transAchDetail.getCdtrName())
                .setParameter(37, transAchDetail.getCdtrAddress())
                .setParameter(38, transAchDetail.getCdtrAcctType())
                .setParameter(39, transAchDetail.getCdtrAcctNo())
                .setParameter(40, transAchDetail.getCdtrMemId())
                .setParameter(41, transAchDetail.getCdtrMemCode())
                .setParameter(42, transAchDetail.getCoreRef())
                .setParameter(43, transAchDetail.getTrnRefNo())
                .setParameter(44, transAchDetail.getTransDetail())
                .setParameter(45, transAchDetail.getTransStep())
                .setParameter(46, transAchDetail.getTransStepStat())
                .setParameter(47, transAchDetail.getErrCode())
                .setParameter(48, transAchDetail.getErrDesc())
                .setParameter(49, transAchDetail.getSessionNo())
                .setParameter(50, transAchDetail.getGroupStatus())
                .setParameter(51, transAchDetail.getIsCopy())
                .setParameter(52, transAchDetail.getTransType())
                .setParameter(53, transAchDetail.getNumberOfTxs())
                .setParameter(54, transAchDetail.getMaker())
                .setParameter(55, transAchDetail.getModifier())
                .setParameter(56, transAchDetail.getChecker())
                //57:activityId
                .setParameter(58, transAchActivity.getActivityDesc())
                .setParameter(59, transAchActivity.getMsgType())
                .setParameter(60, transAchActivity.getMsgContent())
                .setParameter(61, transAchActivity.getMsgDt())
                .setParameter(62, transAchActivity.getActivityStep())
                .setParameter(63, transAchDetail.getCreatedBy())
                .setParameter(64, initType);

        try {
            spQuery.execute();

            Long transId = null;
            if (spQuery.getOutputParameterValue(1) != null) {
                transId = (Long) spQuery.getOutputParameterValue(1);
                trans.setTransId(transId);
            }

            Long activityId = null;
            if (spQuery.getOutputParameterValue(57) != null) {
                activityId = (Long) spQuery.getOutputParameterValue(57);
            }

            if (transAchDetail != null) {
                transAchDetail.setTransId(transId);
            }

            if (transAchActivity != null) {
                transAchActivity.setTransId(transId);
                transAchActivity.setActivityId(activityId);
            }

            String ecode = (String) spQuery.getOutputParameterValue(65);
            String edesc = (String) spQuery.getOutputParameterValue(66);

            res = new DataObj(ecode, edesc, null);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            res = new DataObj("96", "Init trans fail", null);
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }


}

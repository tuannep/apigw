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

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public DataObj initTrans(Transaction trans) {
        return initTrans(trans, new TransAchDetail(), new TransAchActivity(), "1");
    }

    @Override
    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail) {
        return initTrans(trans, transAchDetail, new TransAchActivity(), "2");
    }

    @Override
    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail, TransAchActivity transAchActivity) {
        return initTrans(trans, transAchDetail, transAchActivity, "3");
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

    @Override
    public DataObj updateTransStatus(Long transId, String transStat, String transStatDesc) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_UPDATE_TRANS_STS")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, transId)
                .setParameter(2, transStat)
                .setParameter(3, transStatDesc);
        try {
            spQuery.execute();
            res = new DataObj("00", "Updated success", null);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            res = new DataObj("96", "Updated fail", null);
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }
    @Override
    public DataObj mapErrorCode(String partner, String category, String partnerErrCode) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_MAP_ERROR_CODE")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .setParameter(1, partner)
                .setParameter(2, category)
                .setParameter(3, partnerErrCode);

        try {
            spQuery.execute();
            String ecode = (String) spQuery.getOutputParameterValue(4);
            String edesc = (String) spQuery.getOutputParameterValue(5);
            String reverse = (String) spQuery.getOutputParameterValue(6);

            res = new DataObj(ecode, edesc, reverse);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }
    @Override
    public DataObj handleAchDetailActivity(TransAchDetail transAchDetail, TransAchActivity transAchActivity) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_HANDLE_ACH_DETAIL_ACTIVITY")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(12, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(13, BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter(14, BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter(15, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(16, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(17, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(18, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(19, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(20, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(21, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(22, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(23, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(24, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(25, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(26, Long.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(27, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(28, String.class, ParameterMode.OUT)//
                .setParameter(1, transAchDetail.getTransId())//
                .setParameter(2, transAchDetail.getSenderRefId())//
                .setParameter(3, transAchDetail.getMsgIdentifier())//
                .setParameter(4, transAchActivity.getActivityDesc())//
                .setParameter(5, transAchActivity.getMsgType())//
                .setParameter(6, transAchActivity.getMsgContent())//
                .setParameter(7, transAchActivity.getMsgDt())//
                .setParameter(8, transAchDetail.getErrCode())//
                .setParameter(9, transAchDetail.getErrDesc())//
                .setParameter(10, transAchDetail.getOrgTransId())//
                .setParameter(11, transAchDetail.getOrgSenderRefId())//
                .setParameter(12, transAchDetail.getRefSenderRefId())//
                .setParameter(13, transAchDetail.getVatAmount())//
                .setParameter(14, transAchDetail.getFeeAmount())//
                .setParameter(15, transAchDetail.getSettleDt())//
                .setParameter(16, transAchDetail.getInstrId())//
                .setParameter(17, transAchDetail.getEndtoendId())//
                .setParameter(18, transAchDetail.getTxId())//
                .setParameter(19, transAchDetail.getChargeBr())//
                .setParameter(20, transAchDetail.getCdtrBrn())//
                .setParameter(21, transAchDetail.getTransStep())//
                .setParameter(22, transAchDetail.getTransStepStat())//
                .setParameter(23, transAchDetail.getTrnRefNo())//
                .setParameter(24, transAchDetail.getGroupStatus())//
                .setParameter(25, transAchDetail.getSessionNo());
        try {
            spQuery.execute();
            String activityId = String.valueOf(spQuery.getOutputParameterValue(26));
            String ecode = (String) spQuery.getOutputParameterValue(27);
            String edesc = (String) spQuery.getOutputParameterValue(28);

            res = new DataObj(ecode, edesc, activityId);
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            spQuery.unwrap(ProcedureOutputs.class).release();
        }
        return res;
    }
    @Override
    public DataObj checkInvestgtnInq(String xref, String checkType, String numInqBank, String inqTimeBank,
                                     String numInqAch, String numDayInq, String inqTime2Ach, String inqTime3Ach) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_CHECK_INVESTGTN_INQ")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(10, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(12, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(13, String.class, ParameterMode.OUT)
                .setParameter(1, xref)
                .setParameter(2, checkType)
                .setParameter(3, numInqBank)
                .setParameter(4, inqTimeBank)
                .setParameter(5, numInqAch)
                .setParameter(6, numDayInq)
                .setParameter(7, inqTime2Ach)
                .setParameter(8, inqTime3Ach);

        try {
            spQuery.execute();
            String orgTransId = (String) spQuery.getOutputParameterValue(9);
            String orgSenderRefId = (String) spQuery.getOutputParameterValue(10);
            String isCallAch = (String) spQuery.getOutputParameterValue(11);
            String ecode = (String) spQuery.getOutputParameterValue(12);
            String edesc = (String) spQuery.getOutputParameterValue(13);

            Map<String, String> map = new HashMap<>();
            map.put("orgTransId", orgTransId);
            map.put("orgSenderRefId", orgSenderRefId);
            map.put("isCallNapas", isCallAch);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }

    @Override
    public DataObj getTransById(Long transId) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_TRANS_BY_ID")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(10, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(12, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(13, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(14, String.class, ParameterMode.OUT)
                .setParameter(1, transId);

        try {
            spQuery.execute();
            String isFinalState = (String) spQuery.getOutputParameterValue(2);
            String dbtrAcctNo = (String) spQuery.getOutputParameterValue(3);
            String cdtrAcctNo = (String) spQuery.getOutputParameterValue(4);
            String amount = (String) spQuery.getOutputParameterValue(5);
            String cdtrMemId = (String) spQuery.getOutputParameterValue(6);
            String createdOn = (String) spQuery.getOutputParameterValue(7);
            String orgSenderRefId = (String) spQuery.getOutputParameterValue(8);
            String transCode = (String) spQuery.getOutputParameterValue(9);
            String transDesc = (String) spQuery.getOutputParameterValue(10);
            String transCodeDetail = (String) spQuery.getOutputParameterValue(11);
            String transDescDetail = (String) spQuery.getOutputParameterValue(12);
            String ecode = (String) spQuery.getOutputParameterValue(13);
            String edesc = (String) spQuery.getOutputParameterValue(14);

            Map<String, String> map = new HashMap<>();
            map.put("isFinalState", isFinalState);
            map.put("dbtrAcctNo", dbtrAcctNo);
            map.put("cdtrAcctNo", cdtrAcctNo);
            map.put("amount", amount);
            map.put("transCode", transCode);
            map.put("transDesc", transDesc);
            map.put("transCodeDetail", transCodeDetail);
            map.put("transDescDetail", transDescDetail);
            map.put("cdtrMemId", cdtrMemId);
            map.put("createdOn", createdOn);
            map.put("orgSenderRefId", orgSenderRefId);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }
    @Override
    public DataObj updateTransAchDetailStatus(Long transId, String errCode, String errDesc) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_UPDATE_TRANS_ACH_DTL_STS")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, transId)
                .setParameter(2, errCode)
                .setParameter(3, errDesc);
        try {
            spQuery.execute();
            res = new DataObj("00", "Updated success", null);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            res = new DataObj("96", "Updated fail", null);
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }
    @Override
    public DataObj getTransBySenderRef(String senderRefId) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_TRANS_BY_SENDERREF")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(3, Long.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(4, Long.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(10, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(12, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(13, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(14, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(15, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(16, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(17, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(18, String.class, ParameterMode.OUT).setParameter(1, senderRefId);
        try {
            spQuery.execute();
            String ecode = (String) spQuery.getOutputParameterValue(17);
            String edesc = (String) spQuery.getOutputParameterValue(18);
            Map<String, String> map = null;
            if ("00".equals(ecode)) {
                String transInOut = (String) spQuery.getOutputParameterValue(2);
                String transId = String.valueOf(spQuery.getOutputParameterValue(3));
                String orgTransId = String.valueOf(spQuery.getOutputParameterValue(4));
                String dbtrBrn = (String) spQuery.getOutputParameterValue(5);
                String trnRefNo = (String) spQuery.getOutputParameterValue(6);
                String transType = (String) spQuery.getOutputParameterValue(7);
                String transCate = (String) spQuery.getOutputParameterValue(8);
                String channelId = (String) spQuery.getOutputParameterValue(9);
                String xrefId = (String) spQuery.getOutputParameterValue(10);
                String dbtrAcctNo = (String) spQuery.getOutputParameterValue(11);
                String cdtrAcctNo = (String) spQuery.getOutputParameterValue(12);
                String amount = (String) spQuery.getOutputParameterValue(13);
                String ccy = (String) spQuery.getOutputParameterValue(14);
                String createdOn = (String) spQuery.getOutputParameterValue(15);
                String orgSenderRefId = (String) spQuery.getOutputParameterValue(16);

                map = new HashMap<>();
                map.put("transInOut", transInOut);
                map.put("transId", transId);
                map.put("orgTransId", orgTransId);
                map.put("dbtrBrn", dbtrBrn);
                map.put("trnRefNo", trnRefNo);
                map.put("transType", transType);
                map.put("transCate", transCate);
                map.put("channelId", channelId);
                map.put("xrefId", xrefId);
                map.put("dbtrAcctNo", dbtrAcctNo);
                map.put("cdtrAcctNo", cdtrAcctNo);
                map.put("amount", amount);
                map.put("ccy", ccy);
                map.put("createdOn", createdOn);
                map.put("orgSenderRefId", orgSenderRefId);
            }

            res = new DataObj(ecode, edesc, map);
        } catch (Exception e) {
            logger.error("Exception when handle getTransBySenderRef:" + e.getMessage());
            System.out.println("Exception: " + e.toString());
            res = new DataObj("96", "Fail", null);
        } finally {
            spQuery.unwrap(ProcedureOutputs.class).release();
        }
        return res;
    }
    @Override
    public DataObj updateTransStatusUpdated(Long transId, String transStat, String transStatDesc) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_UPDATE_TRANS_STS_NP")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, transId)
                .setParameter(2, transStat)
                .setParameter(3, transStatDesc);
        try {
            spQuery.execute();
            res = new DataObj("00", "Updated success", null);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            res = new DataObj("96", "Updated fail", null);
        } finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }

    @Override
    public void updateTransactionStatusJpa(Long transId, String transStat, String transStatDesc) {
        Optional<Transaction> optional = transactionRepository.findById(transId);

        if (!optional.isPresent()) {
            logger.error("Can't find transId {} , data may be damaged or deleted", transId);
            return;
        }

        Transaction transaction = optional.get();
        transaction.setTransStat(transStat);
        transaction.setTransStatDesc(transStatDesc);

        transactionRepository.save(transaction);
    }

}

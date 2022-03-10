package com.leadon.apigw.repository;

import org.hibernate.procedure.ProcedureOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchActivity;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransAchActivityRepositoryCustomImpl implements TransAchActivityRepositoryCustom {
	
	public static Logger logger = LoggerFactory.getLogger(TransAchActivityRepositoryCustomImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private TransAchActivityRepository transAchActivityRepository;

	@Override
	public void pushActivity(long transId, String senderRefId, String msgIdentifier, String activityDesc,
			String msgType, String msgContent, Date msgDt, String activityStep, String errCode, String errDesc) {
		
		TransAchActivity transAchActivity = new TransAchActivity();
		transAchActivity.setTransId(transId);
		transAchActivity.setSenderRefId(senderRefId);
		transAchActivity.setMsgIdentifier(msgIdentifier);
		transAchActivity.setActivityDesc(activityDesc);
		transAchActivity.setMsgType(msgType);
		transAchActivity.setMsgContent(msgContent);
		transAchActivity.setMsgDt(msgDt);
		transAchActivity.setActivityStep(activityStep);
		transAchActivity.setErrCode(errCode);
		transAchActivity.setErrDesc(errDesc);
		transAchActivity.setCreatedOn(new Date());
		
		transAchActivityRepository.save(transAchActivity);
	}

    @Override
    public DataObj checkExsitPacs028SendToNP(String transId) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_CHECK_EXIST_INVEST_SEND_NP_BY_DE090")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .setParameter(1, transId);

        try {
            spQuery.execute();
            String orgSenderRefId = (String)spQuery.getOutputParameterValue(2);
            String message = (String)spQuery.getOutputParameterValue(3);
            String ecode = (String)spQuery.getOutputParameterValue(4);
            String edesc = (String)spQuery.getOutputParameterValue(5);
            String countCheck = (String)spQuery.getOutputParameterValue(6);

            Map<String, String> map = new HashMap<>();
            map.put("orgSenderRefId", orgSenderRefId);
            map.put("message" , message);
            map.put("countCheck" , countCheck);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("checkExsitPacs028SendToNP Exception: " + e.toString());
        }
        finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }

    @Override
    public DataObj selectMsgIso8583(String senderRefId, String msgIdentifier, String activityStep) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_MSG_ISO8583")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .setParameter(1, senderRefId)
                .setParameter(2, msgIdentifier)
                .setParameter(3, activityStep);

        try {
            spQuery.execute();
            String message = (String)spQuery.getOutputParameterValue(4);
            String ecode = (String)spQuery.getOutputParameterValue(5);
            String edesc = (String)spQuery.getOutputParameterValue(6);

            Map<String, String> map = new HashMap<>();
            map.put("msgContent" , message);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("selectMsgIso8583 Exception: " + e.toString());
        }
        finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }

    @Override
    public DataObj selectMsgReturnBySenderRefId(String senderRefId, String msgIdentifier, String msgType) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_MSG_RETURN_BY_SENDERREFID")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.OUT)
                .setParameter(1, senderRefId)
                .setParameter(2, msgIdentifier)
                .setParameter(3, msgType);

        try {
            spQuery.execute();
            String message = (String)spQuery.getOutputParameterValue(4);
            String ecode = (String)spQuery.getOutputParameterValue(5);
            String edesc = (String)spQuery.getOutputParameterValue(6);
            String transId = (String)spQuery.getOutputParameterValue(7);

            Map<String, String> map = new HashMap<>();
            map.put("msgContent" , message);
            map.put("transId" , transId);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("selectMsgReturnBySenderRefId Exception: " + e.toString());
        }
        finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }


}

package com.leadon.apigw.repository;

import org.hibernate.procedure.ProcedureOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import  com.leadon.apigw.model.DataObj;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.HashMap;
import java.util.Map;

public class TransMessageISO8583RepositoryCustomImpl implements TransMessageISO8583RepositoryCustom {

    public static Logger logger = LoggerFactory.getLogger(TransMessageISO8583RepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DataObj getPacs008FromIso8583(String mti, String de011TraceNo, String de007TrnDt, String de032AcqCd) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_MXRETURN_BY_DE")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
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
                .setParameter(1, mti)
                .setParameter(2, de011TraceNo)
                .setParameter(3, de007TrnDt)
                .setParameter(4, de032AcqCd);

        try {
            spQuery.execute();
            String orgSenderRefId = (String)spQuery.getOutputParameterValue(5);
            String orgTransId = (String)spQuery.getOutputParameterValue(6);
            String pacs008Content = (String)spQuery.getOutputParameterValue(7);
            String ecode = (String)spQuery.getOutputParameterValue(8);
            String edesc = (String)spQuery.getOutputParameterValue(9);
            String de039 = (String)spQuery.getOutputParameterValue(10);
            String orgXrefId = (String)spQuery.getOutputParameterValue(11);
            String channelId = (String)spQuery.getOutputParameterValue(12);
            String transDt = (String)spQuery.getOutputParameterValue(13);
            String transType = (String)spQuery.getOutputParameterValue(14);

            Map<String, String> map = new HashMap<>();
            map.put("orgSenderRefId", orgSenderRefId);
            map.put("orgTransId", orgTransId);
            map.put("message" , pacs008Content);
            map.put("de039" , de039);
            map.put("orgXrefId" , orgXrefId);
            map.put("channelId" , channelId);
            map.put("transDt" , transDt);
            map.put("transType" , transType);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("getPacs008FromIso8583 Exception: " + e.toString());
        }
        finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }

    @Override
    public DataObj selectMesageReturn8583ByOrgSenderRefId(String orgSenderRefId) {
        DataObj res = null;
        StoredProcedureQuery spQuery = entityManager.createStoredProcedureQuery("PKG_ACH.PR_GET_MX8583RETURN_BY_SENDERREFID")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                .setParameter(1, orgSenderRefId);

        try {
            spQuery.execute();
            String msContent = (String)spQuery.getOutputParameterValue(2);
            String ecode = (String)spQuery.getOutputParameterValue(3);
            String edesc = (String)spQuery.getOutputParameterValue(4);

            Map<String, String> map = new HashMap<>();
            map.put("msContent", msContent);

            res = new DataObj(ecode, edesc, map);

        } catch (Exception e) {
            System.out.println("selectMesageReturn8583ByOrgSenderRefId Exception: " + e.toString());
        }
        finally {
            spQuery.unwrap(ProcedureOutputs.class)
                    .release();
        }
        return res;
    }
}

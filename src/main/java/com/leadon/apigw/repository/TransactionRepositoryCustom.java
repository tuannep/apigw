package com.leadon.apigw.repository;

import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchActivity;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;

public interface TransactionRepositoryCustom {
    public DataObj initTrans(Transaction trans);

    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail);

    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail, TransAchActivity transAchActivity, String initType);
    public DataObj updateTransStatus(Long transId, String transStat, String transStatDesc);
    public DataObj mapErrorCode(String partner, String category, String partnerErrCode);
    public DataObj handleAchDetailActivity(TransAchDetail transAchDetail, TransAchActivity transAchActivity);
    public DataObj checkInvestgtnInq(String xref, String checkType, String numInqBank, String inqTimeBank,
                                     String numInqAch, String numDayInq, String inqTime2Ach, String inqTime3Ach);
    public DataObj getTransById(Long transId);
    public DataObj updateTransAchDetailStatus(Long transId, String errCode, String errDesc);
}

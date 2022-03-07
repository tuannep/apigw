package com.leadon.apigw.repository;

import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchActivity;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;

public interface TransactionRepositoryCustom {
    public DataObj initTrans(Transaction trans);

    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail);

    public DataObj initTrans(Transaction trans, TransAchDetail transAchDetail, TransAchActivity transAchActivity, String initType);
}

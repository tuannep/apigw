package com.leadon.apigw.service;

import com.leadon.apigw.model.AchCustomerInfo;

public interface AchCustomerInfoService {

    AchCustomerInfo getAchCustomerById(String cdtrAcctNo, String cdtrMemId);

    AchCustomerInfo getAchCustomerByCardNo(String cdtrAcctNo);

    void saveAchCustomerInfo(AchCustomerInfo achCustomerInfo);
}

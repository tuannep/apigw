package com.leadon.apigw.service;

import com.leadon.apigw.model.DataObj;

public interface NRTService {
    public DataObj fundTransferNRT(String iso8583Message);
}

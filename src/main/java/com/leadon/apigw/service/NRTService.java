package com.leadon.apigw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.model.DataObj;

public interface NRTService {
    public DataObj fundTransferNRT(String iso8583Message);
    public DataObj handlePacs008(JsonNode root, String message);
}

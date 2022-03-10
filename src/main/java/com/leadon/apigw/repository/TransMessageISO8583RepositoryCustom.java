package com.leadon.apigw.repository;

import com.leadon.apigw.model.DataObj;

public interface TransMessageISO8583RepositoryCustom {

    public DataObj getPacs008FromIso8583(String mti, String de011, String de007, String de032);

    public DataObj selectMesageReturn8583ByOrgSenderRefId(String orgSenderRefId);
}

package com.leadon.apigw.repository;

import com.leadon.apigw.model.DataObj;

import java.util.Date;

public interface TransAchActivityRepositoryCustom {

	public void pushActivity(long transId, String senderRefId, String msgIdentifier, String activityDesc,
			String msgType, String msgContent, Date msgDt, String activityStep, String errCode, String errDesc);

	DataObj checkExsitPacs028SendToNP(String transId);

	DataObj selectMsgIso8583(String senderRefId, String msgIdentifier, String activityStep);

	DataObj selectMsgReturnBySenderRefId(String senderRefId, String msgIdentifier, String activityStep);

}

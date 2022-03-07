package com.leadon.apigw.service.impl;

import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.model.AchCustomerInfo;
import com.leadon.apigw.model.key.AchCustomerInfoKey;
import com.leadon.apigw.repository.AchCustomerInfoRepo;
import com.leadon.apigw.service.AchCustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;


@Service
public class AchCustomerInfoServiceImpl implements AchCustomerInfoService {
    @Autowired
    AchCustomerInfoRepo achCustomerInfoRepo;

    @Override
    public AchCustomerInfo getAchCustomerById(String cdtrAcctNo, String cdtrMemId) {
        AchCustomerInfo achCustomerInfo;
        try {
            AchCustomerInfoKey achCustomerInfoKey = new AchCustomerInfoKey();
            achCustomerInfoKey.setCdtrAcctNo(cdtrAcctNo);
            achCustomerInfoKey.setCdtrMemId(cdtrMemId);

            Optional<AchCustomerInfo> optional = achCustomerInfoRepo.findById(achCustomerInfoKey);

            if (optional.isPresent()) {
                achCustomerInfo = optional.get();
            } else {
                achCustomerInfo = new AchCustomerInfo();
                achCustomerInfo.setCdtrName(AppConstant.ChannelId.ACH);
            }

            return achCustomerInfo;
        } catch (Exception e) {
            achCustomerInfo = new AchCustomerInfo();
            achCustomerInfo.setCdtrName(AppConstant.ChannelId.ACH);

            return achCustomerInfo;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public AchCustomerInfo getAchCustomerByCardNo(String cdtrAcctNo) {
        AchCustomerInfo achCustomerInfo;
        try {
            Stream<AchCustomerInfo> stream = achCustomerInfoRepo.getAchCustomerByAccNo(cdtrAcctNo);

            Optional<AchCustomerInfo> optional = stream.findFirst();
            if (optional.isPresent()) {
                AchCustomerInfo result = optional.get();

                return result;
            } else {
                achCustomerInfo = new AchCustomerInfo();
                achCustomerInfo.setCdtrName(AppConstant.ChannelId.ACH);

                return achCustomerInfo;
            }
        } catch (Exception e) {
            achCustomerInfo = new AchCustomerInfo();
            achCustomerInfo.setCdtrName(AppConstant.ChannelId.ACH);

            return achCustomerInfo;
        }
    }

    @Override
    public void saveAchCustomerInfo(AchCustomerInfo achCustomerInfo) {
        achCustomerInfoRepo.save(achCustomerInfo);
    }
}

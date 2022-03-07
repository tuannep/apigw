package com.leadon.apigw.controller;

import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;
import com.leadon.apigw.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/apigw/v1")
public class TestController extends BaseController {

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping(value = "/trans")
    public ResponseEntity<Object> trans() {
        Transaction trans = new Transaction();
        trans.setXrefId("1");
        trans.setClientId("1");
        trans.setChannelId("ABC");
        trans.setServiceGroupId("G1");
        trans.setServiceId("S1");
        trans.setAmount(new BigDecimal(1000));
        transactionRepository.save(trans);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    @GetMapping(value = "/initTrans")
    public ResponseEntity<Object> initTrans() {

        Transaction trans = new Transaction();
        trans.setXrefId("2");
        trans.setClientId("2");
        trans.setChannelId("ABC");
        trans.setServiceGroupId("G1");
        trans.setServiceId("S1");
        trans.setAmount(new BigDecimal(1000));

        TransAchDetail transAchDtl = new TransAchDetail();
        transAchDtl.setAmount(new BigDecimal(1000));
        transAchDtl.setSenderRefId("123");
        transAchDtl.setCdtrAcctNo("123456");
        transAchDtl.setDbtrAcctNo("654321");

        transactionRepository.initTrans(trans, transAchDtl);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }


}

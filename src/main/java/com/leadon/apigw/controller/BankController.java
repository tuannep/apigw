package com.leadon.apigw.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.dto.InquiryTransactionDto;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.service.InquiryService;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.service.NRTService;
import com.leadon.apigw.util.JsonUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import java.math.BigDecimal;

@RestController
//@RequestMapping("/apigw/v1")
public class BankController extends BaseController{
    public static Logger logger = LoggerFactory.getLogger(BankController.class);

    @Autowired
    private NRTService nrtService;

    @Autowired
    private KafkaProducerService producer;

    @Autowired
    private InquiryService inquiryService;

    @PutMapping(value = "/cms/iach/91", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataObj> transfer1(@RequestBody String iso8583Message)
    {
        System.out.println("==== Received message from bank client:");
        System.out.println(iso8583Message);

        DataObj response = nrtService.fundTransferNRT(iso8583Message);

        // Return bank client
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping(value = "/cms/iach/43", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> inquiryDAS(@RequestBody String iso8583Message) {
        logger.debug("Received inquiry msg from bank client");
        System.out.println("------------------------------------ IsoString 8583 : " + iso8583Message);
        //InquiryDto inquiryDto = ISOUtil.convert8583toDas(iso8583);
        String response = inquiryService.inquiryDAS(iso8583Message);
        // Return bank client
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/transfer1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transfer(@RequestBody String iso8583Message)
    {
        System.out.println("==== Received message from bank client:");
        System.out.println(iso8583Message);
        // Return bank client
        HttpStatus errorCode = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(null, errorCode);
    }
}

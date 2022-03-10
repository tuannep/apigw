package com.leadon.apigw.controller;

import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.service.NRTService;
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


    @PutMapping(value = "/cms/iach/91", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataObj> transfer1(@RequestBody String iso8583Message)
    {
        System.out.println("==== Received message from bank client:");
        System.out.println(iso8583Message);

        DataObj response = nrtService.fundTransferNRT(iso8583Message);

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

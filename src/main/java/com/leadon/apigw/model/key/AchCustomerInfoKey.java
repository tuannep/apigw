package com.leadon.apigw.model.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AchCustomerInfoKey implements Serializable {

    private String cdtrAcctNo;
    private String cdtrMemId;
}

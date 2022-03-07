package com.leadon.apigw.model;

import java.util.HashMap;
import java.util.Map;

public class DataObj {

    private String ecode;
    private String edesc;
    private Object data;

    public DataObj() {
        super();
    }

    public DataObj(String ecode, String edesc, Object data) {
        this.ecode = ecode;
        this.edesc = edesc;
        this.data = data;
    }

    public String getEcode() {
        return ecode;
    }
    public void setEcode(String ecode) {
        this.ecode = ecode;
    }
    public String getEdesc() {
        return edesc;
    }
    public void setEdesc(String edesc) {
        this.edesc = edesc;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

    public String getDataVal(String key) {
        if(data != null && data instanceof HashMap<?, ?>) {
            Map<String, String> map = (HashMap<String, String>)data;
            return map.get(key);
        }
        return null;
    }

}


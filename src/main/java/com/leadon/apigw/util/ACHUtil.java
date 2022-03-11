package com.leadon.apigw.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.model.DAS;
import com.leadon.apigw.model.TransAchActivity;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.leadon.apigw.security.Crypto;

public class ACHUtil {
    public static Logger logger = LoggerFactory.getLogger(ACHUtil.class);

    public static final long ACCOUNT_LENGHT_LIMIT = 1;
    public static final String ECODE_SUCCESS = "0";
    public static final String ECODE_FAIL = "1";
    public static final String EDESC_SUCCESS = "Thanh cong";
    public static final String EDESC_FAIL = "Khong thanh cong";


    /* validate AccountNo */
    public static Boolean validateAccount(String accNo) {
        try {
            long lValue = convertStringToNumber(accNo);
            if (lValue >= 0) {
                if (accNo.length() >= ACCOUNT_LENGHT_LIMIT) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* validate Amount */
    public static Boolean validateAmount(String amount) {
        try {
            int index = amount.indexOf(".");
            if (index > 0) {
                amount = amount.substring(0, index);
            }
            long lValue = convertStringToNumber(amount);
            if (lValue >= 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* validate number */
    public static Boolean validateNumber(String value) {
        try {
            long lValue = convertStringToNumber(value);
            if (lValue >= 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* get Number */
    public static Long convertStringToNumber(String value) {
        long lValue = 0;
        try {
            if (value == null || ("").equalsIgnoreCase(value)) {
                lValue = -1;
            } else {
                lValue = Long.parseLong(value);
            }
        } catch (Exception e) {
            lValue = -1;
        }
        return lValue;
    }

    public static void attachFileProcess(String sourceDir, String fileName, String contentFile, String fileFormatType,
                                         String fileFormatList, String[] errorCode) {
        boolean isImage = false;
        boolean isFile = false;
        errorCode[0] = "96";
        try {
            // xu ly file image
            // check source chua co se tao moi
            File fSourceDir = new File(sourceDir);
            if (!fSourceDir.isDirectory()) {
                fSourceDir.mkdir();
            }

            isImage = vaildateImageFile(fileName, fileFormatList);
            if (isImage && fileName.endsWith(fileFormatType)) {
                boolean result = decodeBase64ToImage(sourceDir, fileName, contentFile, fileFormatType);
                if (result)
                    errorCode[0] = "00";
            } else {
                // loai file khac
                isFile = vaildateFile(fileName, fileFormatList);
                if (isFile) {
                    boolean result = decodeBase64ToFile(sourceDir, fileName, contentFile, fileFormatType);
                    if (result)
                        errorCode[0] = "00";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean decodeBase64ToImage(String dir, String imageFileName, String imageValue, String formatImage) {

        try {
//            String fileDir = dir + File.separator + imageFileName;
//            File file = new File(dir);
//            if (!file.exists())
//                file.mkdirs();
//
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] imgBytes = decoder.decodeBuffer(imageValue);
//            BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
//            File imgOutFile = new File(fileDir);
//            ImageIO.write(bufImg, formatImage, imgOutFile);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean decodeBase64ToFile(String dir, String fileName, String fileValue, String formatFile) {
        FileOutputStream fos = null;
        try {
//            String fileDir = dir + File.separator + fileName;
//            File file = new File(dir);
//            if (!file.exists())
//                file.mkdirs();
//            fos = new FileOutputStream(fileDir);
//
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] imgBytes = decoder.decodeBuffer(fileValue);
//            fos.write(imgBytes);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }
        return false;
    }

    public static String encodeImageToBase64(String dir, String fileName) {
        FileInputStream fileInputStreamReader = null;
        try {
            String soureDir = dir + File.separator + fileName;
            File file = new File(soureDir);
            if (!file.exists())
                return "";
            fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            return new String(Base64.encodeBase64(bytes), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStreamReader != null)
                    fileInputStreamReader.close();
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }
        return "";
    }

    public static boolean vaildateImageFile(String fileName, String extendFile) {
        try {
            // jpg|jpeg|gif|png
            Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(jpg|jpeg|gif|png))$)");
            Matcher mtch = fileExtnPtrn.matcher(fileName);
            if (mtch.matches()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean vaildateDocFile(String fileName, String extendFile) {
        try {
            // txt|doc|csv|pdf|xlsx|xls|...
            Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(txt|doc|csv|pdf|xlsx|xls))$)");
            Matcher mtch = fileExtnPtrn.matcher(fileName);
            if (mtch.matches()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean vaildateFile(String fileName, String extendFile) {
        try {
            // txt|doc|csv|pdf|jpg|jpeg|gif|png|rar|zip|7z|
            Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(" + extendFile + "))$)");
            Matcher mtch = fileExtnPtrn.matcher(fileName);
            if (mtch.matches()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String generateSenderRefId(String transId, String msgId, String orgsenderId, String refBuildType,
                                             String[] addParams) {

        String senderReferId = "";
        String categoryService = "";
        String senderRefer_Prefix1 = "";
        String senderRefer_Prefix2 = "";
        String senderRefer_Prefix3 = "";
        String senderRefer_Prefix4 = "";
        String senderRefer_Prefix5 = "";
        String senderRefer_Prefix6 = "";
        String senderRefer_Prefix7 = "";
        if (AppConstant.SenderRefType.SENDER_REF_DAS.equals(refBuildType)) {
            if (AppConstant.MsgIdr.PACS008.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0200";

            if (!DataUtil.isNullOrEmpty(orgsenderId))
                senderRefer_Prefix2 = orgsenderId;
            else
                senderRefer_Prefix2 = "9704xx";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate;
            if (addParams == null || addParams.length == 0) {
                strDate = formatter.format(new Date());
            }
            else {
                strDate = addParams[2];
            }

            senderRefer_Prefix3 = strDate.substring(4, 8);
            senderRefer_Prefix4 = strDate.substring(8);
            senderRefer_Prefix5 = strDate.substring(0, 4);
            senderRefer_Prefix6 = UUID.randomUUID().toString().substring(1, 5);

            senderReferId = senderRefer_Prefix1 + senderRefer_Prefix2 + senderRefer_Prefix3 + senderRefer_Prefix4
                    + senderRefer_Prefix5 + senderRefer_Prefix6 + addParams[1];
        } else if (AppConstant.SenderRefType.SENDER_REF_NORMAL.equals(refBuildType)) {

            if (AppConstant.MsgIdr.PACS008.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0200";
            else if (AppConstant.MsgIdr.PACS004.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0300";
            else if (AppConstant.MsgIdr.PACS002.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0210";
            else if (AppConstant.MsgIdr.PACS028.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0228";
            else if (AppConstant.MsgIdr.CAMT033.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0633";
            else if (AppConstant.MsgIdr.CAMT009.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0609";
            else if (AppConstant.MsgIdr.CAMT010.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0610";
            else if (AppConstant.MsgIdr.CAMT011.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0611";
            else if (AppConstant.MsgIdr.CAMT998_DISPUTE.equalsIgnoreCase(msgId))
                senderRefer_Prefix1 = "0698";

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate;
            if (addParams == null || addParams.length == 0) {
                strDate = formatter.format(new Date());
            }
            else {
                strDate = addParams[0];
            }
            senderRefer_Prefix3 = strDate.substring(4, 8);
            senderRefer_Prefix4 = strDate.substring(8, strDate.length());
            senderRefer_Prefix5 = strDate.substring(0, 4);

            senderRefer_Prefix6 = UUID.randomUUID().toString().substring(1, 5);

            if (!DataUtil.isNullOrEmpty(orgsenderId))
                senderRefer_Prefix2 = orgsenderId;
            else
                senderRefer_Prefix2 = "9704xx";

            if (!DataUtil.isNullOrEmpty(transId))
                senderRefer_Prefix7 = subStringbyIndex(transId, 6);
            else
                senderRefer_Prefix7 = "000000";
            senderReferId = senderRefer_Prefix1 + senderRefer_Prefix2 + senderRefer_Prefix3 + senderRefer_Prefix4
                    + senderRefer_Prefix5 + senderRefer_Prefix6 + senderRefer_Prefix7;
        } else if (AppConstant.SenderRefType.SENDER_REF_SPEC.equals(refBuildType)) {
            if (AppConstant.MsgIdr.CAMT998_DISPUTE.equalsIgnoreCase(msgId)) {
                senderRefer_Prefix1 = "0698";
                categoryService = "02";
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = formatter.format(new Date());
            senderRefer_Prefix3 = strDate.substring(4, 8);
            senderRefer_Prefix4 = strDate.substring(8, strDate.length());
            senderRefer_Prefix5 = strDate.substring(0, 4);
            senderRefer_Prefix6 = categoryService + addParams[0];

            if (!DataUtil.isNullOrEmpty(orgsenderId))
                senderRefer_Prefix2 = orgsenderId;
            else
                senderRefer_Prefix2 = "9704xx";
            if (!DataUtil.isNullOrEmpty(transId))
                senderRefer_Prefix7 = subStringbyIndex(transId, 6);
            else
                senderRefer_Prefix7 = "000000";
            senderReferId = senderRefer_Prefix1 + senderRefer_Prefix2 + senderRefer_Prefix3 + senderRefer_Prefix4
                    + senderRefer_Prefix5 + senderRefer_Prefix6 + senderRefer_Prefix7;

        }
        return senderReferId;
    }

    public static String subStringbyIndex(String value, Integer limitSize) {
        if (StringUtils.isEmpty(value))
            value = "";
        String strCounter = "";
        Integer iSize = value.length();

        if (iSize > limitSize) {
            strCounter = value.substring((iSize - limitSize), iSize);
        } else if (iSize < limitSize) {
            String strTmp = "";
            Integer iTempSize = (limitSize - iSize);
            for (int i = 0; i < iTempSize; i++) {
                strTmp = strTmp + "0";
            }
            strCounter = strTmp + value;
        } else
            strCounter = value;

        return strCounter;
    }

    public static String subStringbyChar(String value, String charSlip) {
        String strCounter = "";

        if (value.indexOf(charSlip) > 0) {
            strCounter = value.substring(0, value.indexOf(charSlip));
        } else
            strCounter = value;

        return strCounter;
    }

    public static String prepareURL(String preURL, String version, String kindOfMsg, String senderCode,
                                    String transType, String msgDefine, String senderRef) {
        String prepareURL = preURL + "/" + version + "/" + kindOfMsg + "/" + senderCode + "/" + transType + "/"
                + msgDefine + "/" + senderRef;
        return prepareURL;
    }

    public static String sendRequestToNapas(String requestUrl, String requestBody) {
        StringBuffer response = new StringBuffer();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String outputLine;
            System.out.println("Output from Server .... \n");
            while ((outputLine = br.readLine()) != null) {
                response.append(outputLine);
            }

        } catch (Exception e) {
            logger.error("Exception when handle putRequest:" + e.getMessage());
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return response.toString();
    }

    public static String formatAmount(String amount) {
        String suffixAmount = ".00";
        String formatAmount = "";
        if (!amount.endsWith(suffixAmount)) {
            formatAmount = amount + suffixAmount;
        } else {
            formatAmount = amount;
        }
        return formatAmount;
    }

    public static String formatAmountLimit(String amount) {
        String suffixAmount = ".00000";
        String formatAmount = "";
        if (!amount.endsWith(suffixAmount)) {
            formatAmount = amount + suffixAmount;
        } else {
            formatAmount = amount;
        }
        return formatAmount;
    }

    public static String[] addElement(String[] a, String e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    public static void parseDasOut2Obj(JsonNode root, Transaction transaction, TransAchDetail transAchDetail) {
        try {
            // Transaction
            String xrefId = JsonUtil.getVal(root, "/body/iso8583/DE037_REL_REF_NO").asText() + UUID.randomUUID().toString().replace("-", "").substring(0,23).toUpperCase();
            String clientId = JsonUtil.getVal(root, "/body/iso8583/DE060_CNL_TP").asText();
            String channelId = JsonUtil.getVal(root, "/body/iso8583/DE060_CNL_TP").asText();
            String serviceGroupId = AppConstant.SrvGroup.ACH_INQ;
            String serviceId = AppConstant.ServiceId.SERVICE_INQ_DAS;
            String transCate = JsonUtil.getVal(root, "/body/iso8583/DE003_PROC_CD").asText();
            String transInOut = AppConstant.TransDirection.TRANS_OUT;
            String transDesc = JsonUtil.getVal(root, "/body/iso8583/DE104_TRN_CONT").asText();
            long amount = 0;
            String ccy = "";
            String transStat = "";
            String transStatDesc = "";
            String transDt = JsonUtil.getVal(root, "/body/iso8583/DE007_TRN_DT").asText();

            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = "";
            String orgSenderRefId = "";
            String refSenderRefId = "";
            String msgIdentifier = "";
            long feeAmount = 0;
            long vatAmount = 0;
            Date settleDt = new Date();
            String settleMtd = "";
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";

            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = JsonUtil.getVal(root, "/body/iso8583/DE102_SND_ACC_INF").asText();
            String dbtrMemId = "";
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = JsonUtil.getVal(root, "/body/iso8583/DE103_RCV_ACC_INF").asText();
            String cdtrMemId = "";
            String cdtrMemCode = "";
            String coreRef = JsonUtil.getVal(root, "/body/iso8583/DE037_REL_REF_NO").asText();
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = "";
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "SYSTEM";

            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setAmount(BigDecimal.valueOf(amount));
            transAchDetail.setCcy(ccy);
            transAchDetail.setFeeAmount(BigDecimal.valueOf(feeAmount));
            transAchDetail.setVatAmount(BigDecimal.valueOf(vatAmount));
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);
        } catch (Exception e) {
            logger.error("Exception when handle parseDasOut2Obj:" + e.getMessage());
        }
    }

//    public static void parseMsg2Obj(Object obj, Transaction transaction, TransAchDetail transAchDetail) {
//        try {
//            // Transaction
//            String xrefId = "";
//            String clientId = "";
//            String channelId = "";
//            String serviceGroupId = "";
//            String serviceId = "";
//            String transCate = "";
//            String transInOut = AppConstant.TransDirection.TRANS_OUT;
//            String transDesc = "";
//            long amount = 0;
//            String ccy = "";
//            String transStat = "";
//            String transStatDesc = "";
//            String transDt = "";
//
//            // Trans_ach_detail
//            Long orgTransId = null;
//            String senderRefId = "";
//            String orgSenderRefId = "";
//            String refSenderRefId = "";
//            String msgIdentifier = "";
//            long feeAmount = 0;
//            long vatAmount = 0;
//            Date settleDt = new Date();
//            String settleMtd = "";
//            String instrId = "";
//            String endtoendId = "";
//            String txId = "";
//            String chargeBr = "";
//            String dbtrBrn = "";
//            String dbtrName = "";
//
//            String dbtrAddress = "";
//            String dbtrAcctType = "";
//            String dbtrAcctNo = "";
//            String dbtrMemId = "";
//            String dbtrMemCode = "";
//            String cdtrBrn = "";
//            String cdtrName = "";
//            String cdtrAddress = "";
//            String cdtrAcctType = "";
//            String cdtrAcctNo = "";
//            String cdtrMemId = "";
//            String cdtrMemCode = "";
//            String coreRef = "";
//            String trnRefNo = "";
//            String transDetail = transDesc;
//            String transStep = "";
//            String transStepStat = "";
//            String errCode = "";
//            String errDesc = "";
//            String sessionNo = "";
//            String groupStatus = "";
//            String isCopy = "";
//            String transType = "";
//            int numberOfTxs = 0;
//            String maker = "";
//            String modifier = "";
//            String checker = "";
//            String createdBy = "SYSTEM";
//
//            if (senderRefId.length() >= 25) {
//                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
//            } else {
//                xrefId = senderRefId;
//            }
//
//            if (obj instanceof LimitDto) {
//                xrefId = ((LimitDto) obj).getTraceNumber();
//                clientId = ((LimitDto) obj).getChannelId();
//                channelId = ((LimitDto) obj).getChannelId();
//                serviceGroupId = AppConstant.SrvGroup.ACH_LIMIT;
//                if (AppConstant.LimitConfig.GET_METHOD.equals(((LimitDto) obj).getLimitType())) {
//                    transDesc = "Quan ly truy van han muc ACH";
//                    serviceId = AppConstant.ServiceId.SERVICE_LIMIT_GET;
//                    transDetail = transDesc;
//                    transCate = AppConstant.TransCate.ACH_GET_LIMIT;
//                } else {
//                    transDesc = "Quan ly thiet lap han muc ACH";
//                    serviceId = AppConstant.ServiceId.SERVICE_LIMIT_SET;
//                    transDetail = transDesc;
//                    amount = Long.parseLong(DataUtil.isNullObject(((LimitDto) obj).getAmountLimit()) ? "0"
//                            : ((LimitDto) obj).getAmountLimit());
//                    ccy = ((LimitDto) obj).getCurrency();
//                    transCate = AppConstant.TransCate.ACH_SET_LIMIT;
//                }
//            } else if (obj instanceof CopyPaymentDto) {
//                xrefId = ((CopyPaymentDto) obj).getTraceNumber();
//                clientId = ((CopyPaymentDto) obj).getChannelId();
//                channelId = ((CopyPaymentDto) obj).getChannelId();
//                serviceGroupId = AppConstant.SrvGroup.ACH_COPY;
//                transDesc = "Copy giao dich";
//                serviceId = AppConstant.ServiceId.SERVICE_COPY_PAYMENT;
//                transDetail = transDesc;
//                transCate = AppConstant.TransCate.ACH_COPY_PAYMENT;
//                orgSenderRefId = ((CopyPaymentDto) obj).getOrgSenderRefId();
//            } else if (obj instanceof DisputeDto) {
//                xrefId = ((DisputeDto) obj).getTraceNumber();
//                clientId = ((DisputeDto) obj).getChannelId();
//                channelId = ((DisputeDto) obj).getChannelId();
//                serviceGroupId = AppConstant.SrvGroup.ACH_DISP;
//                orgSenderRefId = ((DisputeDto) obj).getOrgSenderRefId();
//                orgTransId = Long.parseLong(DataUtil.isNullObject(((DisputeDto) obj).getOrgTransId()) ? "0"
//                        : ((DisputeDto) obj).getOrgTransId());
//                msgIdentifier = AppConstant.MsgIdr.CAMT998_DISPUTE;
//                if (AppConstant.DisputeConfig.DisputeType.DISP_EDIT_TYPE.equals(((DisputeDto) obj).getDispType())) {
//                    serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
//                    transDesc = "Yeu cau tra soat chinh sua thong tin giao dich";
//                    transDetail = transDesc;
//                    transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_EDIT;
//                } else if (AppConstant.DisputeConfig.DisputeType.DISP_RTN_TYPE
//                        .equals(((DisputeDto) obj).getDispType())) {
//                    serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
//                    transDesc = "Yeu cau tra soat hoan tra giao dich";
//                    transDetail = transDesc;
//                    transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_RTN;
//                } else if (AppConstant.DisputeConfig.DisputeType.DISP_SUPPORT_TYPE
//                        .equals(((DisputeDto) obj).getDispType())) {
//                    serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
//                    transDesc = "Yeu cau tra soat cung cap thong tin giao dich";
//                    transDetail = transDesc;
//                    transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_SUPPORT;
//                } else if (AppConstant.DisputeConfig.DisputeType.DISP_INFO_TYPE
//                        .equals(((DisputeDto) obj).getDispType())) {
//                    serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
//                    transDesc = "Yeu cau tra soat ho tro thu hoi giao dich";
//                    transDetail = transDesc;
//                    transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_INFO;
//                } else if (AppConstant.DisputeConfig.DisputeType.DISP_FAITH_TYPE
//                        .equals(((DisputeDto) obj).getDispType())) {
//                    serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
//                    transDesc = "Yeu cau tra soat ho tro tra soat giao dich";
//                    transDetail = transDesc;
//                    transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_FAITH;
//                }
//            } else if (obj instanceof ReturnPaymentDto) {
//                xrefId = ((ReturnPaymentDto) obj).getTraceNumber();
//                clientId = ((ReturnPaymentDto) obj).getChannelId();
//                channelId = ((ReturnPaymentDto) obj).getChannelId();
//                serviceGroupId = AppConstant.SrvGroup.ACH_RTN;
//                transDesc = "Hoan tra giao dich";
//                serviceId = AppConstant.ServiceId.SERVICE_NRT_RETURN;
//                transDetail = ((ReturnPaymentDto) obj).getDescription();
//                String preAmount = ((ReturnPaymentDto) obj).getAmount();
//                if (StringUtils.isNotEmpty(preAmount) && preAmount.contains(".")) {
//                    preAmount = preAmount.substring(0, preAmount.indexOf("."));
//                    amount = Long.parseLong(DataUtil.isNullObject(preAmount) ? "0"
//                            : preAmount);
//                } else {
//                    amount = Long.parseLong(DataUtil.isNullObject(((ReturnPaymentDto) obj).getAmount()) ? "0"
//                            : ((ReturnPaymentDto) obj).getAmount());
//                }
//                ccy = ((ReturnPaymentDto) obj).getCurrency();
//                transCate = AppConstant.TransCate.ACH_RTN_NRT_PAYMENT;
//                orgSenderRefId = ((ReturnPaymentDto) obj).getOrgSenderRefId();
//                orgTransId = Long.parseLong(DataUtil.isNullObject(((ReturnPaymentDto) obj).getOrgTransId()) ? "0"
//                        : ((ReturnPaymentDto) obj).getOrgTransId());
//                msgIdentifier = AppConstant.MsgIdr.PACS004;
//            } else if (obj instanceof ManualResponsePaymentDto) {
//                xrefId = ((ManualResponsePaymentDto) obj).getTraceNumber();
//                clientId = ((ManualResponsePaymentDto) obj).getChannelId();
//                channelId = ((ManualResponsePaymentDto) obj).getChannelId();
//                serviceGroupId = AppConstant.SrvGroup.ACH_RES_PAYMENT;
//                transDesc = "Phan hoi trang thai giao dich";
//                serviceId = AppConstant.ServiceId.SERVICE_RESPONSE_PAYMENT;
//                transDetail = transDesc;
//                transCate = AppConstant.TransCate.ACH_RESPONSE_PAYMENT;
//                orgSenderRefId = ((ManualResponsePaymentDto) obj).getOrgSenderRefId();
//            }
//
//            transaction.setXrefId(xrefId);
//            transaction.setClientId(clientId);
//            transaction.setChannelId(channelId);
//            transaction.setServiceGroupId(serviceGroupId);
//            transaction.setServiceId(serviceId);
//            transaction.setTransCate(transCate);
//            transaction.setTransInout(transInOut);
//            transaction.setTransDesc(transDesc);
//            transaction.setAmount(BigDecimal.valueOf(amount));
//            transaction.setCcy(ccy);
//            transaction.setTransDt(transDt);
//            transaction.setTransStat(transStat);
//            transaction.setTransStatDesc(transStatDesc);
//
//            transAchDetail.setOrgTransId(orgTransId);
//            transAchDetail.setSenderRefId(senderRefId);
//            transAchDetail.setOrgSenderRefId(orgSenderRefId);
//            transAchDetail.setRefSenderRefId(refSenderRefId);
//            transAchDetail.setMsgIdentifier(msgIdentifier);
//            transAchDetail.setAmount(BigDecimal.valueOf(amount));
//            transAchDetail.setCcy(ccy);
//            transAchDetail.setFeeAmount(BigDecimal.valueOf(feeAmount));
//            transAchDetail.setVatAmount(BigDecimal.valueOf(vatAmount));
//            transAchDetail.setSettleDt(settleDt);
//            transAchDetail.setSettleMtd(settleMtd);
//            transAchDetail.setInstrId(instrId);
//            transAchDetail.setEndtoendId(endtoendId);
//            transAchDetail.setTxId(txId);
//            transAchDetail.setChargeBr(chargeBr);
//            transAchDetail.setDbtrBrn(dbtrBrn);
//            transAchDetail.setDbtrName(dbtrName);
//            transAchDetail.setDbtrAddress(dbtrAddress);
//            transAchDetail.setDbtrAcctType(dbtrAcctType);
//            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
//            transAchDetail.setDbtrMemId(dbtrMemId);
//            transAchDetail.setDbtrMemCode(dbtrMemCode);
//            transAchDetail.setCdtrBrn(cdtrBrn);
//            transAchDetail.setCdtrName(cdtrName);
//            transAchDetail.setCdtrAddress(cdtrAddress);
//            transAchDetail.setCdtrAcctType(cdtrAcctType);
//            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
//            transAchDetail.setCdtrMemId(cdtrMemId);
//            transAchDetail.setCdtrMemCode(cdtrMemCode);
//            transAchDetail.setCoreRef(coreRef);
//            transAchDetail.setTrnRefNo(trnRefNo);
//            transAchDetail.setTransDetail(transDetail);
//            transAchDetail.setTransStep(transStep);
//            transAchDetail.setTransStepStat(transStepStat);
//            transAchDetail.setErrCode(errCode);
//            transAchDetail.setErrDesc(errDesc);
//            transAchDetail.setSessionNo(sessionNo);
//            transAchDetail.setGroupStatus(groupStatus);
//            transAchDetail.setIsCopy(isCopy);
//            transAchDetail.setTransType(transType);
//            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
//            transAchDetail.setMaker(maker);
//            transAchDetail.setModifier(modifier);
//            transAchDetail.setChecker(checker);
//            transAchDetail.setCreatedBy(createdBy);
//        } catch (Exception e) {
//            logger.error("Exception when handle initTrans:" + e.getMessage());
//        }
//    }

    public static boolean validationNPMessage(JsonNode jsonNode) {
        try {
            // header validation
            JsonNode SenderReference = JsonUtil.getVal(jsonNode, "/Header/SenderReference");
            JsonNode MessageIdentifier = JsonUtil.getVal(jsonNode, "/Header/MessageIdentifier");
            JsonNode Format = JsonUtil.getVal(jsonNode, "/Header/Format");
            JsonNode SenderID = JsonUtil.getVal(jsonNode, "/Header/Sender/ID");
            JsonNode ReceiverID = JsonUtil.getVal(jsonNode, "/Header/Receiver/ID");
            JsonNode Timestamps = JsonUtil.getVal(jsonNode, "/Header/Timestamp");
            JsonNode Signature = JsonUtil.getVal(jsonNode, "/Header/Signature");

//			if (SenderReference == null || MessageIdentifier == null || Format == null || SenderID == null
//					|| ReceiverID == null || Timestamps == null || Signature == null) {
//				return false;
//			}
            //test

            if (SenderReference == null || MessageIdentifier == null || Format == null || SenderID == null
                    || ReceiverID == null || Timestamps == null) {
                return false;
            }
            //end test
            // AppHdr validation
            JsonNode FrMmbId = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/Fr/FIId/FinInstnId/ClrSysMmbId/MmbId");
            JsonNode ToMmbId = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/To/FIId/FinInstnId/ClrSysMmbId/MmbId");
            JsonNode BizMsgIdr = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/BizMsgIdr");
            JsonNode BizSvc = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/BizSvc");
            JsonNode CreDt = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/CreDt");
            JsonNode MsgDefIdr = JsonUtil.getVal(jsonNode, "/Payload/AppHdr/MsgDefIdr");

            if (!AppConstant.MsgIdr.ACKNACK.equals(MessageIdentifier.asText())) {
                if (FrMmbId == null || ToMmbId == null || BizMsgIdr == null || BizSvc == null || CreDt == null
                        || MsgDefIdr == null) {
                    return false;
                }
            }
            // Document validation
            if (AppConstant.MsgIdr.ACKNACK.equals(MessageIdentifier.asText())) {
                JsonNode Type = JsonUtil.getVal(jsonNode, "/Payload/ack_nak/type");
                if (Type == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.ADMI002.equals(MessageIdentifier.asText())) {
                JsonNode RjctgPtyRsn = JsonUtil.getVal(jsonNode, "/Payload/Document/admi.002.001.01/Rsn/RjctgPtyRsn");
                JsonNode RsnDesc = JsonUtil.getVal(jsonNode, "/Payload/Document/admi.002.001.01/Rsn/RsnDesc");
                if (RjctgPtyRsn == null || RsnDesc == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT025.equals(MessageIdentifier.asText())) {
                JsonNode OrgnlMsgId = JsonUtil.getVal(jsonNode, "/Payload/Document/Rct/RctDtls/OrgnlMsgId/MsgId");
                if (OrgnlMsgId == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.PACS002.equals(MessageIdentifier.asText())) {
                JsonNode MsgId = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/MsgId");
                JsonNode CreDtTm = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/CreDtTm");
                JsonNode OrgnlMsgId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId");
                JsonNode OrgnlMsgNmId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId");
                JsonNode OrgnlCreDtTm = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlCreDtTm");
                if (MsgId == null || CreDtTm == null || OrgnlMsgId == null || OrgnlMsgNmId == null
                        || OrgnlCreDtTm == null) {
                    return false;
                }

                JsonNode TxInfAndSts = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts");
                if (TxInfAndSts.size() == 0) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT998.equals(MessageIdentifier.asText())) {
                JsonNode typeCamt998 = JsonUtil.getVal(jsonNode, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Tp");
                if (typeCamt998 == null) {
                    return false;
                }

                if (AppConstant.ReportConfig.CAMT998_REPORT_MSG.contains(typeCamt998.asText())) {
                    JsonNode msgId = JsonUtil.getVal(jsonNode, "/Payload/Document/CshMgmtPrtryMsg/MsgHdr/MsgId");
                    if (msgId == null) {
                        return false;
                    }
                } else if (AppConstant.DisputeConfig.CAMT998_DSPT_MSG.contains(typeCamt998.asText())) {
                    JsonNode CaseId = JsonUtil.getVal(jsonNode,
                            "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Case/Id");
                    JsonNode DsptTpCd = JsonUtil.getVal(jsonNode,
                            "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptTpCd");
                    if (CaseId == null || DsptTpCd == null) {
                        return false;
                    }
                }
            } else if (AppConstant.MsgIdr.PACS004.equals(MessageIdentifier.asText())) {
                JsonNode Prtry = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry");
                JsonNode OrgnlMsgId = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgId");
                JsonNode CreDtTm = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/CreDtTm");
                JsonNode NbOfTxs = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/NbOfTxs");
                JsonNode Value = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value");
                JsonNode dbAccNo = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id");
                JsonNode dbAccName = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/Nm");
                JsonNode dbOrgCode = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId");
                JsonNode crAccNo = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id");
                if (Prtry == null || OrgnlMsgId == null || CreDtTm == null || NbOfTxs == null || Value == null
                        || dbAccNo == null || dbAccName == null || dbOrgCode == null || crAccNo == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT010.equals(MessageIdentifier.asText())) {
                JsonNode MsgId = JsonUtil.getVal(jsonNode, "/Payload/Document/RtrLmt/MsgHdr/OrgnlBizQry/MsgId");
                JsonNode MsgNmId = JsonUtil.getVal(jsonNode, "/Payload/Document/RtrLmt/MsgHdr/OrgnlBizQry/MsgNmId");
                JsonNode AcctId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/RtrLmt/RptOrErr/BizRpt/CurLmt/LmtId/AcctId/Othr/Id");
                if (MsgId == null || MsgNmId == null || AcctId == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT034.equals(MessageIdentifier.asText())) {
                JsonNode rootReferenceId = JsonUtil.getVal(jsonNode, "/Payload/Document/Dplct/Case/Id");
                JsonNode messageIdentifierCopy = JsonUtil.getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Tp");
                if (rootReferenceId == null || messageIdentifierCopy == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.PACS008.equals(MessageIdentifier.asText())) {
                JsonNode InstrId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId");
                JsonNode EndToEndId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/EndToEndId");
                JsonNode TxId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/TxId");
                JsonNode CCY = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Ccy");
                JsonNode Value = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Value");
                JsonNode InstgAgtMmbId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId");
                JsonNode InstdAgtMmbId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstdAgt/FinInstnId/ClrSysMmbId/MmbId");
                JsonNode DbtrNm = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm");
                JsonNode DbtrAcctId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id");
                JsonNode DbtrAcctPrtry = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Tp/Prtry");
                JsonNode DbtrAgtMmbId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId");
                JsonNode CdtrAgtMmbId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId");
                JsonNode CdtrNm = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/Nm");
                JsonNode CdtrAcctId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Id/Othr/Id");
                JsonNode CdtrAcctPrtry = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Tp/Prtry");
                JsonNode NbOfTxs = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs");
                if (InstrId == null || EndToEndId == null || TxId == null || CCY == null || Value == null
                        || InstgAgtMmbId == null || InstdAgtMmbId == null || DbtrNm == null || DbtrAcctId == null
                        || DbtrAcctPrtry == null || DbtrAgtMmbId == null || CdtrAgtMmbId == null || CdtrNm == null
                        || CdtrAcctId == null || CdtrAcctPrtry == null || NbOfTxs == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT052.equals(MessageIdentifier.asText())) {
                JsonNode msgSessionId = JsonUtil.getVal(jsonNode, "/Payload/Document/BkToCstmrAcctRpt/Rpt/AddtlRptInf");
                JsonNode msgId = JsonUtil.getVal(jsonNode, "/Payload/Document/BkToCstmrAcctRpt/GrpHdr/MsgId");
                if (msgSessionId == null || msgId == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.CAMT053.equals(MessageIdentifier.asText())) {
                JsonNode msgSessionId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/BkToCstmrStmt/Stmt/Ntry/0/AcctSvcrRef");
                JsonNode msgId = JsonUtil.getVal(jsonNode, "/Payload/Document/BkToCstmrStmt/GrpHdr/MsgId");
                if (msgSessionId == null || msgId == null) {
                    return false;
                }
            } else if (AppConstant.MsgIdr.PACS028.equals(MessageIdentifier.asText())) {
                JsonNode OrgnlMsgId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgId");
                JsonNode OrgnlMsgNmId = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgNmId");
                JsonNode OrgnlCreDtTm = JsonUtil.getVal(jsonNode,
                        "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlCreDtTm");
                if (OrgnlMsgId == null || OrgnlMsgNmId == null || OrgnlCreDtTm == null) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception when handle validationMessageRecevieNP:" + e.getMessage());
            return false;
        }
        return true;
    }

    public static void parsePacs004Nrt2Obj(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                           TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_RTN;
            String serviceId = AppConstant.ServiceId.SERVICE_NRT_RETURN;
            String transCate = AppConstant.TransCate.ACH_RTN_NRT_PAYMENT;
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            String transDesc = AppConstant.MsgDesc.DESC_PACS004_COPY;
            long amount = Long.parseLong(subStringbyChar(JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Value")
                    .asText(), "."));
            String ccy = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Ccy")
                    .asText();
            String transDt = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/GrpHdr/IntrBkSttlmDt")
                    .asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/GrpHdr/MsgId").asText();
            String orgSenderRefId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgId")
                    .asText();
            String refSenderRefId = senderRefId;
            String msgIdentifier = AppConstant.MsgIdr.PACS004;
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = null;
            if (StringUtils.isNotEmpty(transDt)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                settleDt = dateFormat.parse(transDt);
            }
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/Nm")
                    .asText();
            String dbtrAddress = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/0")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/1")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/2")
                    .asText();
            String dbtrAcctType = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Tp/Prtry")
                    .asText();
            String dbtrAcctNo = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id")
                    .asText();
            String dbtrMemId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlInstrId")
                    .asText().substring(0, 6);
            String dbtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String cdtrBrn = "";
            String cdtrName = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/Nm")
                    .asText();
            String cdtrAddress = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/0")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/1")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/2")
                    .asText();
            String cdtrAcctType = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Tp/Prtry")
                    .asText();
            String cdtrAcctNo = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id")
                    .asText();
            String cdtrMemId = "";
            String cdtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = AppConstant.MsgDesc.DESC_PACS004_COPY;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = AppConstant.CopyConfig.IS_TRANS_COPY;
            String transType = AppConstant.Common.TRANS_TYPE_NRT;
            int numberOfTxs = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/PmtRtr/GrpHdr/NbOfTxs").asInt();
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (senderRefId.length() >= 25) {
                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
            } else {
                xrefId = senderRefId;
            }

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS004_COPY);
            transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransCopyPacs004:" + e.getMessage());
        }
    }

    public static void initTransPacs004Nrt(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                           TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_RTN;
            String serviceId = AppConstant.ServiceId.SERVICE_NRT_RETURN;
            String transCate = AppConstant.TransCate.ACH_RTN_NRT_PAYMENT;
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            String transDesc = AppConstant.MsgDesc.DESC_PACS004;
            long amount = Long.parseLong(subStringbyChar(
                    JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Value").asText(),
                    "."));
            String ccy = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Ccy")
                    .asText();
            String intrBkSttlmDt= JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/IntrBkSttlmDt").asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            // String senderRefId = JsonUtil
            // .getVal(jsonNode,
            // "/Payload/Document/PmtRtr/GrpHdr/MsgId").asText();
            String senderRefId = JsonUtil.getVal(jsonNode, "/Header/SenderReference").asText(); //
            String orgSenderRefId = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgId")
                    .asText();
            String refSenderRefId = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/MsgId").asText();
            String msgIdentifier = AppConstant.MsgIdr.PACS004;
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = null;
            if (StringUtils.isNotEmpty(intrBkSttlmDt)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                settleDt = dateFormat.parse(intrBkSttlmDt);
            }
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/Nm")
                    .asText();
            String dbtrAddress = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/0").asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/1")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/PstlAdr/AdrLine/2")
                    .asText();
            String dbtrAcctType = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Tp/Prtry").asText();
            String dbtrAcctNo = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id").asText();
            String dbtrMemId = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlInstrId").asText()
                    .substring(0, 6);
            String dbtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String cdtrBrn = "";
            String cdtrName = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/Nm")
                    .asText();
            String cdtrAddress = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/0").asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/1")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/PstlAdr/AdrLine/2")
                    .asText();
            String cdtrAcctType = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Tp/Prtry").asText();
            String cdtrAcctNo = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id").asText();
            String cdtrMemId = "";
            String cdtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = AppConstant.MsgDesc.DESC_PACS004;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = AppConstant.Common.TRANS_TYPE_NRT;
            int numberOfTxs = JsonUtil.getVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/NbOfTxs").asInt();
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (senderRefId.length() >= 25) {
                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
            } else {
                xrefId = senderRefId;
            }

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(intrBkSttlmDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS004);
            transAchActivity.setMsgType(AppConstant.LogConfig.REQUEST); //anhtn sua RESPONSE -> REQUEST (NAPAS gui yeu cau)
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransPacs004Nrt:" + e.getMessage());
        }
    }

    public static void parsePacs008Nrt2Obj(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                           TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_NRT;
            String serviceId = AppConstant.ServiceId.SERVICE_NRT_IN;
            String transCate = ""; // chua set bien o day
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            //
            JsonNode transDesc_Sub1 = JsonUtil.getVal(jsonNode,
                    "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/2/InstrInf");
            JsonNode transDesc_Sub2 = JsonUtil.getVal(jsonNode,
                    "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/3/InstrInf");
            //
            String transDesc = transDesc_Sub1.asText().split("/CTR/")[1] + transDesc_Sub2.asText().split("/MAC/")[0];
            long amount = Long.parseLong(subStringbyChar(JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Value")
                    .asText(), "."));
            String ccy = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Ccy")
                    .asText();
            String transDt = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
                    .asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId")
                    .asText();
            String orgSenderRefId = senderRefId;
            String refSenderRefId = senderRefId;
            String msgIdentifier = AppConstant.MsgIdr.PACS008;
            // Long feeAmount = null;
            // Long vatAmount = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date settleDt = dateFormat.parse(JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
                    .asText());
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm")
                    .asText();
            //
            int iIter = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine")
                    .asInt();
            String addressDtl = "";
            for (int i = 1; i <= iIter; i++) {
                if (addressDtl.equals("")) {
                    addressDtl = JsonUtil
                            .getVal(jsonNode,
                                    "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine")
                            .get(i).asText();
                } else {
                    addressDtl = addressDtl + "-"
                            + JsonUtil
                            .getVal(jsonNode,
                                    "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine")
                            .get(i).asText();
                }
            }
            //
            String dbtrAddress = addressDtl;
            String dbtrAcctType = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Tp/Prtry")
                    .asText();
            String dbtrAcctNo = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id")
                    .asText();
            String dbtrMemId = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId")
                    .asText().substring(0, 6);
            String dbtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String cdtrBrn = "";
            String cdtrName = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/Nm")
                    .asText();
            String cdtrAddress = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/0")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1")
                    .asText()
                    + "-"
                    + JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2")
                    .asText();
            String cdtrAcctType = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Tp/Prtry")
                    .asText();
            String cdtrAcctNo = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Id/Othr/Id")
                    .asText();
            String cdtrMemId = AppConstant.Common.SENDER_ID;
            String cdtrMemCode = AppConstant.Common.SENDER_CODE;
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = AppConstant.CopyConfig.IS_TRANS_COPY;
            String transType = AppConstant.Common.TRANS_TYPE_NRT;
            int numberOfTxs = JsonUtil.getVal(jsonNode,
                    "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs").asInt();
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (senderRefId.length() >= 25) {
                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
            } else {
                xrefId = senderRefId;
            }
            if (cdtrAcctType.equals(AppConstant.MethodType.ACCT_TYPE)) {
                transCate = "912020";
            } else {
                transCate = "912000";
            }

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS008_COPY);
            transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransCopyPacs008Nrt:" + e.getMessage());
        }
    }

    public static void initTransPacs008Nrt(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                           TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_NRT;
            String serviceId = AppConstant.ServiceId.SERVICE_NRT_IN;
            String transCate = ""; // chua set bien o day
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            //
            JsonNode transDesc_Sub1 = JsonUtil.getVal(jsonNode,
                    "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/2/InstrInf");
            JsonNode transDesc_Sub2 = JsonUtil.getVal(jsonNode,
                    "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/3/InstrInf");
            //
            String transDesc = transDesc_Sub1.asText().split("/CTR/")[1] + transDesc_Sub2.asText().split("/MAC/")[0];
            long amount = Long
                    .valueOf(subStringbyChar(
                            JsonUtil.getVal(jsonNode,
                                    "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Value").asText(),
                            "."));
            String ccy = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Ccy")
                    .asText();
            String transDt = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
                    .asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            // String senderRefId = JsonUtil
            // .getVal(jsonNode,
            // "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId")
            // .asText();
            String senderRefId = "";
            String orgSenderRefId = JsonUtil.getVal(jsonNode, "/Header/SenderReference").asText();
            ;
            String refSenderRefId = "";
            String msgIdentifier = AppConstant.MsgIdr.PACS008;
            // Long feeAmount = null;
            // Long vatAmount = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date settleDt = dateFormat.parse(
                    JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt").asText());
            //20201218-anhtn-add stmt date yyyy-MM-dd
            //String settleMtd = ""; // chua xac dinh duoc
            String settleMtd =  JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt").asText();
            //end
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm")
                    .asText();
            //
            int iIter = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine").asInt();
            String addressDtl = "";
            for (int i = 1; i <= iIter; i++) {
                if (addressDtl.equals("")) {
                    addressDtl = JsonUtil
                            .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine")
                            .get(i).asText();
                } else {
                    addressDtl = addressDtl + "-" + JsonUtil
                            .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine")
                            .get(i).asText();
                }
            }
            //
            String dbtrAddress = addressDtl;
            String dbtrAcctType = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Tp/Prtry").asText();
            String dbtrAcctNo = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
            String dbtrMemId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId").asText()
                    .substring(0, 6);
            String dbtrMemCode = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
                    .asText();
            String cdtrBrn = "";
            String cdtrName = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/Nm")
                    .asText();
            //
            String cdtrAddress = "";
            int iCdIter2 = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine").asInt();
            if (iCdIter2 >= 3) {
                cdtrAddress = JsonUtil
                        .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/0")
                        .asText()
                        + "-"
                        + JsonUtil
                        .getVal(jsonNode,
                                "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1")
                        .asText()
                        + "-"
                        + JsonUtil
                        .getVal(jsonNode,
                                "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2")
                        .asText();
            }
            //
            String cdtrAcctType = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Tp/Prtry").asText();
            String cdtrAcctNo = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Id/Othr/Id").asText();
            String cdtrMemId = AppConstant.Common.SENDER_ID;
            String cdtrMemCode = AppConstant.Common.SENDER_CODE;
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = AppConstant.Common.TRANS_TYPE_NRT;
            int numberOfTxs = JsonUtil.getVal(jsonNode, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs").asInt();
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (orgSenderRefId.length() >= 25) {
                xrefId = orgSenderRefId.substring(orgSenderRefId.length() - 25, orgSenderRefId.length());
            } else {
                xrefId = orgSenderRefId;
            }
            if (cdtrAcctType.equals(AppConstant.MethodType.ACCT_TYPE)) {
                transCate = "912020";
            } else {
                transCate = "912000";
            }
            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setSenderRefId(orgSenderRefId);
            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS008);
            transAchActivity.setMsgType(AppConstant.LogConfig.REQUEST);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransPacs008Nrt:" + e.getMessage());
        }
    }

    public static void initTransPacs028(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                        TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_INQ;
            String serviceId = AppConstant.ServiceId.SERVICE_INQ_INVEST;
            String transCate = AppConstant.TransCate.ACH_INVEST;
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            String transDesc = AppConstant.MsgDesc.DESC_PACS028;
            // Long amount = null;
            String ccy = "";
            String transDt = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxRef/IntrBkSttlmDt").asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = JsonUtil.getVal(jsonNode, "/Header/SenderReference").asText();
            String orgSenderRefId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgId").asText();
            String refSenderRefId = "";
            String msgIdentifier = AppConstant.MsgIdr.PACS028;
            // Long feeAmount = null;
            // Long vatAmount = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date settleDt = dateFormat.parse(JsonUtil
                    .getVal(jsonNode, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxRef/IntrBkSttlmDt").asText());
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";
            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = "";
            String dbtrMemId = "";
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = "";
            String cdtrMemId = "";
            String cdtrMemCode = "";
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = "";
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (senderRefId.length() >= 25) {
                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
            } else {
                xrefId = senderRefId;
            }

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(null);
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS028);
            transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransPacs028:" + e.getMessage());
        }
    }

    public static void parseCamt998Dispute2Obj(JsonNode jsonNode, Transaction transaction,
                                               TransAchDetail transAchDetail, TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = "";
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_DISP;
            String serviceId = AppConstant.ServiceId.SERVICE_DISPUTE_IN;
            String transCate = "";
            String typeDispute = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptTpCd")
                    .asText();
            if (AppConstant.DisputeConfig.DisputeType.DISP_EDIT_TYPE.equals(typeDispute)) {
                transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_EDIT;
            } else if (AppConstant.DisputeConfig.DisputeType.DISP_RTN_TYPE.equals(typeDispute)) {
                transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_RTN;
            } else if (AppConstant.DisputeConfig.DisputeType.DISP_INFO_TYPE.equals(typeDispute)) {
                transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_INFO;
            } else if (AppConstant.DisputeConfig.DisputeType.DISP_SUPPORT_TYPE.equals(typeDispute)) {
                transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_SUPPORT;
            } else if (AppConstant.DisputeConfig.DisputeType.DISP_FAITH_TYPE.equals(typeDispute)) {
                transCate = AppConstant.DisputeConfig.DisputeTransCate.DISP_FAITH;
            }
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            String transDesc = AppConstant.MsgDesc.DESC_CAMT998_DPT;
            // Long amount = null;
            String ccy = "";
            String transDt = "";
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = JsonUtil.getVal(jsonNode, "/Header/SenderReference").asText();
            String orgSenderRefId = JsonUtil
                    .getVal(jsonNode,
                            "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlGrpInf/OrgnlMsgId")
                    .asText();
            String refSenderRefId = JsonUtil
                    .getVal(jsonNode, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Case/Id")
                    .asText();
            String msgIdentifier = AppConstant.MsgIdr.CAMT998;
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = null;
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";
            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = "";
            String dbtrMemId = "";
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = "";
            String cdtrMemId = "";
            String cdtrMemCode = "";
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = "";
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            if (senderRefId.length() >= 25) {
                xrefId = senderRefId.substring(senderRefId.length() - 25, senderRefId.length());
            } else {
                xrefId = senderRefId;
            }

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(null);
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_PACS028);
            transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity
                    .setMsgDt(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(jsonNode, "/Header/Timestamp").asText()));
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransCamt998Dispute:" + e.getMessage());
        }
    }

    public static void initTransInquiryTransNRT(JsonNode jsonNode, Transaction transaction,
                                                TransAchDetail transAchDetail) {
        try {
            // Transaction
            long transId = 0;
            String xrefId = JsonUtil.getVal(jsonNode, "/xrefId").asText();
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = JsonUtil.getVal(jsonNode, "/channelId").asText();
            String serviceGroupId = AppConstant.SrvGroup.ACH_INQ;
            String serviceId = AppConstant.ServiceId.SERVICE_INQ_INVEST;
            String transCate = AppConstant.TransCate.ACH_INVEST;
            String transInOut = AppConstant.TransDirection.TRANS_OUT;
            String transDesc = "";
            // Long amount = null;
            String ccy = "";
            String transDt = JsonUtil.getVal(jsonNode, "/transDt").asText();
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = "";
            String orgSenderRefId = "";
            String refSenderRefId = "";
            String msgIdentifier = "";
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = new Date();
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";
            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = "";
            String dbtrMemId = "";
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = "";
            String cdtrMemId = "";
            String cdtrMemCode = "";
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = JsonUtil.getVal(jsonNode, "/transType").asText();
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(null);
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);
        } catch (Exception e) {
            logger.error("Exception when handle initTransInquiryTransaction:" + e.getMessage());
        }
    }

    public static void initTransInquiryTransNRTCore(JsonNode jsonNode, Transaction transaction,
                                                TransAchDetail transAchDetail , String orgXrefId, String preChannelId, String preTransDt, String preTransType) {
        try {
            // Transaction
            long transId = 0;
//            String xrefId = JsonUtil.getVal(jsonNode, "/xrefId").asText();
            String xrefId = orgXrefId;
            String clientId = AppConstant.ClientId.APP_SYSTEM;
//            String channelId = JsonUtil.getVal(jsonNode, "/channelId").asText();
            String channelId = preChannelId;
            String serviceGroupId = AppConstant.SrvGroup.ACH_INQ;
            String serviceId = AppConstant.ServiceId.SERVICE_INQ_INVEST;
            String transCate = AppConstant.TransCate.ACH_INVEST;
            String transInOut = AppConstant.TransDirection.TRANS_OUT;
            String transDesc = "";
            // Long amount = null;
            String ccy = "";
//            String transDt = JsonUtil.getVal(jsonNode, "/transDt").asText();
            String transDt = preTransDt;
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = "";
            String orgSenderRefId = "";
            String refSenderRefId = "";
            String msgIdentifier = "";
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = new Date();
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";
            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = "";
            String dbtrMemId = "";
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = "";
            String cdtrMemId = "";
            String cdtrMemCode = "";
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
//            String transType = JsonUtil.getVal(jsonNode, "/transType").asText();
            String transType = preTransType;
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(null);
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);
        } catch (Exception e) {
            logger.error("Exception when handle initTransInquiryTransNRTCore:" + e.getMessage());
        }
    }

    public static void initTransDasIn(JsonNode jsonNode, Transaction transaction, TransAchDetail transAchDetail,
                                      TransAchActivity transAchActivity) {
        try {
            // Transaction
            long transId = 0;

            String systemTraceAuditNum = JsonUtil.getVal(jsonNode, "/systemTraceAuditNum").asText();
            String processingCode = JsonUtil.getVal(jsonNode, "/processingCode").asText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String xrefId = processingCode + systemTraceAuditNum + sdf.format(new Date());
            if (xrefId.length() >= 25) {
                xrefId = xrefId.substring(xrefId.length() - 25, xrefId.length());
            }
            String clientId = AppConstant.ClientId.APP_SYSTEM;
            String channelId = AppConstant.ChannelId.ACH;
            String serviceGroupId = AppConstant.SrvGroup.ACH_INQ;
            String serviceId = AppConstant.ServiceId.SERVICE_INQ_DAS;
            String transCate = JsonUtil.getVal(jsonNode, "/processingCode").asText();
            String transInOut = AppConstant.TransDirection.TRANS_IN;
            String transDesc = JsonUtil.getVal(jsonNode, "/contentTransfers").asText();
            ;
            // Long amount = null;
            String ccy = "";
            String transDt = "";
            String transStat = "";
            String transStatDesc = "";
            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = "";
            String orgSenderRefId = "";
            String refSenderRefId = "";
            String msgIdentifier = "";
            // Long feeAmount = null;
            // Long vatAmount = null;
            Date settleDt = new Date();
            String settleMtd = ""; // chua xac dinh duoc
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = "";
            String dbtrBrn = "";
            String dbtrName = "";
            String dbtrAddress = "";
            String dbtrAcctType = "";
            String dbtrAcctNo = JsonUtil.getVal(jsonNode, "/senderAcc").asText();
            String dbtrMemId = JsonUtil.getVal(jsonNode, "/sendingMember").asText();
            String dbtrMemCode = "";
            String cdtrBrn = "";
            String cdtrName = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            String cdtrAcctNo = JsonUtil.getVal(jsonNode, "/receiverAcc").asText();
            String cdtrMemId = JsonUtil.getVal(jsonNode, "/forwardingmember").asText();
            String cdtrMemCode = "";
            String coreRef = "";
            String trnRefNo = "";
            String transDetail = JsonUtil.getVal(jsonNode, "/contentTransfers").asText();
            ;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = "";
            int numberOfTxs = 0;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "";

            transaction.setTransId(transId);
            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(null);
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setFeeAmount(null);
            transAchDetail.setVatAmount(null);
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);

            transAchActivity.setActivityDesc(AppConstant.MsgDesc.DESC_DAS);
            transAchActivity.setMsgType(AppConstant.LogConfig.REQUEST);
            transAchActivity.setMsgContent(jsonNode.toPrettyString());
            transAchActivity.setMsgDt(new Date());
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
        } catch (Exception e) {
            logger.error("Exception when handle initTransDasIn:" + e.getMessage());
        }
    }

    // public static void initTransReturnTransactionNRT(JsonNode jsonNode,
    // Transaction transaction,
    // TransAchDetail transAchDetail) {
    // try {
    // // Transaction
    // long transId = 0;
    // String xrefId = JsonUtil.getVal(jsonNode, "/xrefId").asText();
    // String clientId = AppConstant.ClientId.APP_SYSTEM;
    // String channelId = JsonUtil.getVal(jsonNode, "/channelId").asText();
    // String serviceGroupId = AppConstant.SrvGroup.ACH_RTN;
    // String serviceId = JsonUtil.getVal(jsonNode, "/serviceId").asText();
    // String transCate = AppConstant.TransCate.ACH_RTN_NRT_PAYMENT;
    // String transInOut = AppConstant.TransDirection.TRANS_OUT;
    // String transDesc = JsonUtil.getVal(jsonNode, "/description").asText();
    // long amount = JsonUtil.getVal(jsonNode, "/amount").asLong();
    // String ccy = JsonUtil.getVal(jsonNode, "/currency").asText();
    // String transDt = JsonUtil.getVal(jsonNode, "/transDt").asText();
    // String transStat = "";
    // String transStatDesc = "";
    // // Trans_ach_detail
    // Long orgTransId = null;
    // String senderRefId = "";
    // String orgSenderRefId = "";
    // String refSenderRefId = "";
    // String msgIdentifier = "";
    //// Long feeAmount = null;
    //// Long vatAmount = null;
    // Date settleDt = new Date();
    // String settleMtd = ""; // chua xac dinh duoc
    // String instrId = "";
    // String endtoendId = "";
    // String txId = "";
    // String chargeBr = "";
    // String dbtrBrn = "";
    // String dbtrName = JsonUtil.getVal(jsonNode,
    // "/debitAccountName").asText();
    // String dbtrAddress = "";
    // String dbtrAcctType = JsonUtil.getVal(jsonNode,
    // "/debitAccountType").asText();
    // String dbtrAcctNo = JsonUtil.getVal(jsonNode,
    // "/debitAccountNo").asText();
    // String dbtrMemId = JsonUtil.getVal(jsonNode, "/debitOrgId").asText();
    // String dbtrMemCode = JsonUtil.getVal(jsonNode, "/debitOrgId").asText();
    // String cdtrBrn = "";
    // String cdtrName = JsonUtil.getVal(jsonNode, "/creditAccountNo").asText();
    // String cdtrAddress = JsonUtil.getVal(jsonNode,
    // "/creditAccountNo").asText();
    // String cdtrAcctType = JsonUtil.getVal(jsonNode,
    // "/creditAccountNo").asText();
    // String cdtrAcctNo = JsonUtil.getVal(jsonNode,
    // "/creditAccountNo").asText();
    // String cdtrMemId = JsonUtil.getVal(jsonNode,
    // "/creditAccountNo").asText();
    // String cdtrMemCode = JsonUtil.getVal(jsonNode,
    // "/creditAccountNo").asText();
    // String coreRef = "";
    // String trnRefNo = "";
    // String transDetail = transDesc;
    // String transStep = "";
    // String transStepStat = "";
    // String errCode = "";
    // String errDesc = "";
    // String sessionNo = "";
    // String groupStatus = "";
    // String isCopy = "";
    // String transType = "";
    // int numberOfTxs = 1;
    // String maker = "";
    // String modifier = "";
    // String checker = "";
    // String createdBy = "";
    //
    // transaction.setTransId(transId);
    // transaction.setXrefId(xrefId);
    // transaction.setClientId(clientId);
    // transaction.setChannelId(channelId);
    // transaction.setServiceGroupId(serviceGroupId);
    // transaction.setServiceId(serviceId);
    // transaction.setTransCate(transCate);
    // transaction.setTransInout(transInOut);
    // transaction.setTransDesc(transDesc);
    // transaction.setAmount(BigDecimal.valueOf(amount));
    // transaction.setCcy(ccy);
    // transaction.setTransDt(transDt);
    // transaction.setTransStat(transStat);
    // transaction.setTransStatDesc(transStatDesc);
    //
    // transAchDetail.setOrgTransId(orgTransId);
    // transAchDetail.setSenderRefId(senderRefId);
    // transAchDetail.setOrgSenderRefId(orgSenderRefId);
    // transAchDetail.setRefSenderRefId(refSenderRefId);
    // transAchDetail.setMsgIdentifier(msgIdentifier);
    // transAchDetail.setFeeAmount(null);
    // transAchDetail.setVatAmount(null);
    // transAchDetail.setSettleDt(settleDt);
    // transAchDetail.setSettleMtd(settleMtd);
    // transAchDetail.setInstrId(instrId);
    // transAchDetail.setEndtoendId(endtoendId);
    // transAchDetail.setTxId(txId);
    // transAchDetail.setChargeBr(chargeBr);
    // transAchDetail.setDbtrBrn(dbtrBrn);
    // transAchDetail.setDbtrName(dbtrName);
    // transAchDetail.setDbtrAddress(dbtrAddress);
    // transAchDetail.setDbtrAcctType(dbtrAcctType);
    // transAchDetail.setDbtrAcctNo(dbtrAcctNo);
    // transAchDetail.setDbtrMemId(dbtrMemId);
    // transAchDetail.setDbtrMemCode(dbtrMemCode);
    // transAchDetail.setCdtrBrn(cdtrBrn);
    // transAchDetail.setCdtrName(cdtrName);
    // transAchDetail.setCdtrAddress(cdtrAddress);
    // transAchDetail.setCdtrAcctType(cdtrAcctType);
    // transAchDetail.setCdtrAcctNo(cdtrAcctNo);
    // transAchDetail.setCdtrMemId(cdtrMemId);
    // transAchDetail.setCdtrMemCode(cdtrMemCode);
    // transAchDetail.setCoreRef(coreRef);
    // transAchDetail.setTrnRefNo(trnRefNo);
    // transAchDetail.setTransDetail(transDetail);
    // transAchDetail.setTransStep(transStep);
    // transAchDetail.setTransStepStat(transStepStat);
    // transAchDetail.setErrCode(errCode);
    // transAchDetail.setErrDesc(errDesc);
    // transAchDetail.setSessionNo(sessionNo);
    // transAchDetail.setGroupStatus(groupStatus);
    // transAchDetail.setIsCopy(isCopy);
    // transAchDetail.setTransType(transType);
    // transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
    // transAchDetail.setMaker(maker);
    // transAchDetail.setModifier(modifier);
    // transAchDetail.setChecker(checker);
    // transAchDetail.setCreatedBy(createdBy);
    // } catch (Exception e) {
    // logger.error("Exception when handle initTransReturnTransactionNRT:" +
    // e.getMessage());
    // }
    // }

    // public static boolean validationDASIn(JsonNode jsonNode, String ecode,
    // String edesc, String isCardFlag) {
    //// if (JsonUtil.getVal(jsonNode, "/msgType") == null
    //// || JsonUtil.getVal(jsonNode, "/msgType").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "msgType is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/PAN") == null ||
    // JsonUtil.getVal(jsonNode, "/PAN").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "PAN is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/processingCode") == null
    //// || JsonUtil.getVal(jsonNode, "/processingCode").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "processingCode is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/transAmount") == null
    //// || JsonUtil.getVal(jsonNode, "/transAmount").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "transAmount is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/transmissionDateTime") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/transmissionDateTime").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "transmissionDateTime is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/settlementDate") == null
    //// || JsonUtil.getVal(jsonNode, "/settlementDate").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "settlementDate is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/systemTraceAuditNum") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/systemTraceAuditNum").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "systemTraceAuditNum is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/localTime") == null
    //// || JsonUtil.getVal(jsonNode, "/localTime").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "localTime is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/localDate") == null
    //// || JsonUtil.getVal(jsonNode, "/localDate").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "localDate is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/pointOfServiceEntryCode") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/pointOfServiceEntryCode").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "pointOfServiceEntryCode is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/pointOfServiceConditionCode") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/pointOfServiceConditionCode").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "pointOfServiceConditionCode is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/sendingMember") == null
    //// || JsonUtil.getVal(jsonNode, "/sendingMember").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "sendingMember is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/receivingMember") == null
    //// || JsonUtil.getVal(jsonNode, "/receivingMember").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "Error Validate (Mess receivingMember). msgType is field
    // require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/retRefNumber") == null
    //// || JsonUtil.getVal(jsonNode, "/retRefNumber").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "retRefNumber is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/cardAcceptorTerminalId") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/cardAcceptorTerminalId").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "cardAcceptorTerminalId is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/cardAcceptorId") == null
    //// || JsonUtil.getVal(jsonNode, "/cardAcceptorId").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "cardAcceptorId is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/cardAcceptorNameLocation") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/cardAcceptorNameLocation").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "cardAcceptorNameLocation is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/additionalDataPrivate") == null
    //// || JsonUtil.getVal(jsonNode,
    // "/additionalDataPrivate").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "additionalDataPrivate is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/transCurrencyCode") == null
    //// || JsonUtil.getVal(jsonNode, "/transCurrencyCode").asText().equals(""))
    // {
    //// ecode = "A4";
    //// edesc = "transCurrencyCode is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/usrDefinedField") == null
    //// || JsonUtil.getVal(jsonNode, "/usrDefinedField").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "usrDefinedField is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/serviceCode") == null
    //// || JsonUtil.getVal(jsonNode, "/serviceCode").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "serviceCode is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/receivingMember") == null
    //// || JsonUtil.getVal(jsonNode, "/receivingMember").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "receivingMember is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/senderAcc") == null
    //// || JsonUtil.getVal(jsonNode, "/senderAcc").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "senderAcc is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/transRefNumber") == null
    //// || JsonUtil.getVal(jsonNode, "/transRefNumber").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "transRefNumber is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/receiverAcc") == null
    //// || JsonUtil.getVal(jsonNode, "/receiverAcc").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "receiverAcc is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/contentTransfers") == null
    //// || JsonUtil.getVal(jsonNode, "/contentTransfers").asText().equals(""))
    // {
    //// ecode = "A4";
    //// edesc = "contentTransfers is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/MAC") == null ||
    // JsonUtil.getVal(jsonNode, "/MAC").asText().equals("")) {
    //// ecode = "A4";
    //// edesc = "MAC is field require";
    //// return false;
    //// }
    //// if (JsonUtil.getVal(jsonNode, "/processingCode") == null) {
    //// ecode = "A4";
    //// edesc = "processingCode is field require";
    //// return false;
    //// }
    //// if (!JsonUtil.getVal(jsonNode,
    // "/processingCode").asText().equals(AppConstant.InquiryConfig.Das.DAS_CARD_CARD)
    //// || !JsonUtil.getVal(jsonNode, "/processingCode").asText()
    //// .equals(AppConstant.InquiryConfig.Das.DAS_ACCT_CARD)
    //// || !JsonUtil.getVal(jsonNode, "/processingCode").asText()
    //// .equals(AppConstant.InquiryConfig.Das.DAS_CARD_ACCT)
    //// || !JsonUtil.getVal(jsonNode, "/processingCode").asText()
    //// .equals(AppConstant.InquiryConfig.Das.DAS_ACCT_ACCT)) {
    //// ecode = "A4";
    //// edesc = "processingCode invalid";
    //// return false;
    //// }
    //
    // if (JsonUtil.getVal(jsonNode,
    // "/processingCode").asText().substring(4).equals(AppConstant.Common.CARD))
    // {
    // isCardFlag = AppConstant.Common.IS_CARD_FLAG;
    // if (JsonUtil.getVal(jsonNode, "/receiverAcc").asText().length() !=
    // Integer
    // .valueOf(AppConstant.Common.CARD_LENGTH)) {
    // ecode = "44";
    // edesc = "Card invalid";
    // return false;
    // }
    // if (!JsonUtil.getVal(jsonNode,
    // "/receiverAcc").asText().startsWith(AppConstant.Common.SENDER_ID)) {
    // ecode = "44";
    // edesc = "Card invalid";
    // return false;
    // }
    // } else if (JsonUtil.getVal(jsonNode,
    // "/processingCode").asText().substring(4).equals(AppConstant.Common.ACCT))
    // {
    // isCardFlag = AppConstant.Common.IS_ACCT_FLAG;
    // if (!validateAccount(JsonUtil.getVal(jsonNode, "/receiverAcc").asText()))
    // {
    // ecode = "38";
    // edesc = "Account invalid";
    // return false;
    // }
    // }
    // System.out.println("ABNG TEST: " + isCardFlag);
    // return true;
    // }

    public static boolean validationInquiryTransNRT(JsonNode jsonNode, String ecode, String edesc) {
        if (JsonUtil.getVal(jsonNode, "/transType") == null || JsonUtil.getVal(jsonNode, "/orgXrefId") == null
                || JsonUtil.getVal(jsonNode, "/channelId") == null) {
            ecode = "51";
            edesc = "Data do not meet the requirements";
            return false;
        }
        return true;
    }

    // validation theo tung nghiep vu khac nhau tuy bank
//    public static boolean validateAccountInfo(Account account, String ecode, String edesc) {
//        try {
//            if (!account.getCurrency().equals(AppConstant.Common.CURRENCY)) {
//                ecode = "32";
//                edesc = "Currency invalid";
//                return false;
//            }
//            if (!account.getAccountStatus().equals(AppConstant.Common.ACCT_STT_OPEN)) {
//                ecode = "35";
//                edesc = "Account is closed";
//                return false;
//            }
//        } catch (Exception e) {
//            logger.error("Exception when handle validateAccountInfo:" + e.getMessage());
//            return false;
//        }
//        return false;
//    }

    public static boolean validateReturnNRT(JsonNode jsonNode, String ecode, String edesc) {
        try {
            if (JsonUtil.getVal(jsonNode, "/amount") == null
                    || validateAmount(JsonUtil.getVal(jsonNode, "/amount").asText())) {
                ecode = "32";
                edesc = "amount invalid";
                return false;
            }
            if (JsonUtil.getVal(jsonNode, "/currency").asText().equals(AppConstant.Common.CURRENCY)) {
                ecode = "33";
                edesc = "currency invalid";
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception when handle validateReturnNRT:" + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean validationSignature(JsonNode jsonNode) {
        boolean verify;
        try {
            String payload = JsonUtil.getVal(jsonNode, "/Payload").toString();
            String signature = JsonUtil.getVal(jsonNode, "/Header/Signature").asText();
            if (StringUtils.isNotEmpty(signature)) {
                signature = StringUtils.replace(signature, "\\/", "/");
                //verify = Crypto.verifySign("RSA2048", payload, signature, null);
                verify = true;
            }
            else {
                return false;
            }

        } catch (Exception e) {
            logger.error("Exception when handle validationSignature:" + e.getMessage());
            return false;
        }
        return verify;
    }

    public static DAS buildResponseDasIn(JsonNode jsonNode, String responseCode, String accountHolderName,
                                         String authIdResponse) {
        DAS das = new DAS();
        try {
//            das.setMsgType((JsonUtil.getVal(jsonNode, "/msgType")) != null
//                    ? JsonUtil.getVal(jsonNode, "/msgType").asText() : "");
            das.setMsgType((JsonUtil.getVal(jsonNode, "/msgType")) != null
                    ? AppConstant.InquiryConfig.Das.MSG_TYPE_DAS_RESP : "");
            //open when finish test
            das.setProcessingCode((JsonUtil.getVal(jsonNode, "/processingCode")) != null
                    ? JsonUtil.getVal(jsonNode, "/processingCode").asText() : "");
            //end
            das.setTransAmount((JsonUtil.getVal(jsonNode, "/transAmount")) != null
                    ? JsonUtil.getVal(jsonNode, "/transAmount").asText() : "");
            das.setTransmissionDateTime((JsonUtil.getVal(jsonNode, "/transmissionDateTime")) != null
                    ? JsonUtil.getVal(jsonNode, "/transmissionDateTime").asText() : "");
            das.setSystemTraceAuditNum((JsonUtil.getVal(jsonNode, "/systemTraceAuditNum")) != null
                    ? JsonUtil.getVal(jsonNode, "/systemTraceAuditNum").asText() : "");
            das.setLocalTime((JsonUtil.getVal(jsonNode, "/localTime")) != null
                    ? JsonUtil.getVal(jsonNode, "/localTime").asText() : "");
            das.setLocalDate((JsonUtil.getVal(jsonNode, "/localDate")) != null
                    ? JsonUtil.getVal(jsonNode, "/localDate").asText() : "");
            das.setSettlementDate((JsonUtil.getVal(jsonNode, "/settlementDate")) != null
                    ? JsonUtil.getVal(jsonNode, "/settlementDate").asText() : "");
            das.setSendingMember((JsonUtil.getVal(jsonNode, "/sendingMember")) != null
                    ? JsonUtil.getVal(jsonNode, "/sendingMember").asText() : "");
            das.setRetRefNumber((JsonUtil.getVal(jsonNode, "/retRefNumber")) != null
                    ? JsonUtil.getVal(jsonNode, "/retRefNumber").asText() : "");
            das.setCardAcceptorTerminalId((JsonUtil.getVal(jsonNode, "/cardAcceptorTerminalId")) != null
                    ? JsonUtil.getVal(jsonNode, "/cardAcceptorTerminalId").asText() : "");
//            das.setCardAcceptorId((JsonUtil.getVal(jsonNode, "/cardAcceptorId")) != null
//                    ? JsonUtil.getVal(jsonNode, "/cardAcceptorId").asText() : "");
//            das.setCardAcceptorNameLocation((JsonUtil.getVal(jsonNode, "/cardAcceptorNameLocation")) != null
//                    ? JsonUtil.getVal(jsonNode, "/cardAcceptorNameLocation").asText() : "");
            das.setAdditionalDataPrivate((JsonUtil.getVal(jsonNode, "/additionalDataPrivate")) != null
                    ? JsonUtil.getVal(jsonNode, "/additionalDataPrivate").asText() : "");
            das.setTransCurrencyCode((JsonUtil.getVal(jsonNode, "/transCurrencyCode")) != null
                    ? JsonUtil.getVal(jsonNode, "/transCurrencyCode").asText() : "");
            das.setUsrDefinedField((JsonUtil.getVal(jsonNode, "/usrDefinedField")) != null
                    ? JsonUtil.getVal(jsonNode, "/usrDefinedField").asText() : "");
            das.setServiceCode((JsonUtil.getVal(jsonNode, "/serviceCode")) != null
                    ? JsonUtil.getVal(jsonNode, "/serviceCode").asText() : "");
            das.setTransRefNumber((JsonUtil.getVal(jsonNode, "/transRefNumber")) != null
                    ? JsonUtil.getVal(jsonNode, "/transRefNumber").asText() : "");
            das.setReceivingMember((JsonUtil.getVal(jsonNode, "/receivingMember")) != null
                    ? JsonUtil.getVal(jsonNode, "/receivingMember").asText() : "");
            das.setSenderAcc((JsonUtil.getVal(jsonNode, "/senderAcc")) != null
                    ? JsonUtil.getVal(jsonNode, "/senderAcc").asText() : "");
            das.setReceiverAcc((JsonUtil.getVal(jsonNode, "/receiverAcc")) != null
                    ? JsonUtil.getVal(jsonNode, "/receiverAcc").asText() : "");
            das.setContentTransfers((JsonUtil.getVal(jsonNode, "/contentTransfers")) != null
                    ? JsonUtil.getVal(jsonNode, "/contentTransfers").asText() : "");
            das.setPAN((JsonUtil.getVal(jsonNode, "/PAN")) != null ? JsonUtil.getVal(jsonNode, "/PAN").asText() : "");
            das.setMAC((JsonUtil.getVal(jsonNode, "/MAC")) != null ? JsonUtil.getVal(jsonNode, "/MAC").asText() : "");
            das.setResponseCode(responseCode);

            das.setAccountHolderName(accountHolderName);
            das.setAuthIdResponse(authIdResponse);
        } catch (Exception e) {
            logger.error("Exception when handle buildResponseDasIn:" + e.getMessage());
            return null;
        }
        return das;
    }

//    public static void buildReqHandleAchMsg(TransAchDetail transAchDetail, TransAchActivity transAchActivity,
//                                            TransactionDto transactionDto) {
//        try {
//            transAchDetail.setTransId(transactionDto.getTransId());
//            transAchDetail.setSenderRefId(transactionDto.getSenderRefId());
//            transAchDetail.setMsgIdentifier(transactionDto.getMessageIdentifier());
//            transAchActivity.setActivityDesc(transactionDto.getTransDesc());
//            transAchActivity.setMsgType(transactionDto.getMsgType());
//            transAchActivity.setMsgContent(transactionDto.getMsgContent());
//            transAchActivity.setSenderRefId(transactionDto.getSenderRefId());
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            transAchActivity.setMsgDt(DateUtil.parseTimestampXXX2Date(transactionDto.getMsgDt()));
//            transAchDetail.setErrCode(transactionDto.getErrorCode());
//            transAchDetail.setErrDesc(transactionDto.getErrorDesc());
//            transAchDetail.setOrgTransId(transactionDto.getOrgTransId());
//            transAchDetail.setOrgSenderRefId(transactionDto.getOrgSenderRefId());
//            transAchDetail.setRefSenderRefId(transactionDto.getRefSenderRefId());
//            transAchDetail.setVatAmount((transactionDto.getVatAmount() != null)
//                    ? BigDecimal.valueOf(Long.parseLong(transactionDto.getVatAmount())) : BigDecimal.valueOf(0));
//            transAchDetail.setFeeAmount((transactionDto.getFeeAmount() != null)
//                    ? BigDecimal.valueOf(Long.parseLong(transactionDto.getFeeAmount())) : BigDecimal.valueOf(0));
//            transAchDetail.setSettleDt(
//                    (transactionDto.getSettleDt() == null ? null : sdf.parse(transactionDto.getSettleDt())));
//            transAchDetail.setInstrId(transactionDto.getInstrId());
//            transAchDetail.setEndtoendId(transactionDto.getEndtoendId());
//            transAchDetail.setTxId(transactionDto.getTxId());
//            transAchDetail.setChargeBr(transactionDto.getChargeBr());
//            transAchDetail.setCdtrBrn(transactionDto.getCdtrBrn());
//            transAchDetail.setTransStep(transactionDto.getTransStep());
//            transAchDetail.setTransStepStat(transactionDto.getTransStepStat());
//            transAchDetail.setTrnRefNo(transactionDto.getTrnRefNo());
//            transAchDetail.setGroupStatus(transactionDto.getGroupStatus());
//            transAchDetail.setSessionNo(transactionDto.getSessionNo());
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Exception when handle buildReqHandleAchMsg:" + e.getMessage());
//        }
//
//    }
//
    /*
    ** investigation
    ** update trans status and detail for pacs028
    * return TransAchDetail
     */
    public static TransAchDetail  hadleUpdateTranStatusAndTransDetail(Long transId, String orgTransId,  String errCode, String errDesc) {
        TransAchDetail transAchDetail = new TransAchDetail();
        transAchDetail.setTransId(transId);
        transAchDetail.setErrCode(errCode);
        transAchDetail.setErrDesc(errDesc);
        if (StringUtils.isNotEmpty(orgTransId))
            transAchDetail.setOrgTransId(Long.parseLong(orgTransId));
        if ("01".equals(errCode))
            transAchDetail.setNumberOfTxs(1);

        return transAchDetail;
    }

    public static String parseObjectToString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            logger.error("Parse error " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String getAmount(String preAmount) {
        String amount;
        if (StringUtils.isNotEmpty(preAmount) && preAmount.contains(".")) {
            amount = StringUtils.leftPad(preAmount.substring(0, preAmount.indexOf(".")) + "00", 12 ,"0");
        } else {
            amount = preAmount;
        }

        return amount;
    }

    /*
    ** valid json format
     */
    public static boolean isJSON8583Valid(String jsonInString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonInString);
            String de003 = JsonUtil.getVal(root, "/body/iso8583/DE003_PROC_CD").asText();
            String de090 = JsonUtil.getVal(root, "/body/iso8583/DE090_ORG_TRN_KEY").asText();
            if (StringUtils.isEmpty(de003) || StringUtils.isEmpty(de090))
                return false;
            else
                return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     ** valid das response from NP format
     * return true else false
     */
    public static boolean isNotEmtyDasResponseNP(String jsonInString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootDas = objectMapper.readTree(jsonInString);

            String pan = JsonUtil.getVal(rootDas, "/PAN").asText();
            String processingCode = JsonUtil.getVal(rootDas, "/processingCode").asText();

            if (StringUtils.isEmpty(pan) || StringUtils.isEmpty(processingCode))
                return false;
            else
                return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * compare credttm with current date yyyy-MM-dd
     * @param timeStamp
     * @return true if equal current Date else false
     */
    public static boolean isEqualCurrentDate(String timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date1 = sdf.parse(timeStamp);
            Date date2 = sdf.parse(sdf.format(new Date()));

            logger.info("+++isEqualCurrentDate, date1: " + date1 + ", date2: " + date2);

            if (date1.compareTo(date2) > 0) {
                return false;
            } else if (date1.compareTo(date2) < 0) {
                return false;
            } else if (date1.compareTo(date2) == 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            return false;
        }
    }

    /**
     *
     * @param instrInf
     * @return
     */
    public static Map<String, String> getInstrInf1(String instrInf) {
        Map<String, String> map = new HashMap<>();

        //        String TDT, SCR, MCC, AIC, PEM, PCD, FID, MID;
        if (0 <instrInf.indexOf("TAM"))
            map.put("TAM", instrInf.split("TAM")[1].split("/")[1]);
        if (0 <instrInf.indexOf("TDT"))
            map.put("TDT", instrInf.split("TDT")[1].split("/")[1]);
        if (0 <instrInf.indexOf("SCR"))
            map.put("SCR", instrInf.split("SCR")[1].split("/")[1]);
        if (0 <instrInf.indexOf("MCC"))
            map.put("MCC", instrInf.split("MCC")[1].split("/")[1]);
        if (0 <instrInf.indexOf("AIC"))
            map.put("AIC", instrInf.split("AIC")[1].split("/")[1]);
        if (0 <instrInf.indexOf("PEM"))
            map.put("PEM", instrInf.split("PEM")[1].split("/")[1]);
        if (0 <instrInf.indexOf("PCD"))
            map.put("PCD", instrInf.split("PCD")[1].split("/")[1]);
        if (0 <instrInf.indexOf("FID"))
            map.put("FID", instrInf.split("FID")[1].split("/")[1]);
        if (0 <instrInf.indexOf("MID"))
            map.put("MID", instrInf.split("MID")[1].split("/")[1]);

        //        String MNM, SCC, BID, FAI, TAI;
        if (0 <instrInf.indexOf("MNM"))
            map.put("MNM", instrInf.split("MNM")[1].split("/")[1]);
        if (0 <instrInf.indexOf("SCC"))
            map.put("SCC", instrInf.split("SCC")[1].split("/")[1]);
        if (0 <instrInf.indexOf("BID"))
            map.put("BID", instrInf.split("BID")[1].split("/")[1]);
        if (0 <instrInf.indexOf("FAI"))
            map.put("FAI", instrInf.split("FAI")[1].split("/")[1]);
        if (0 <instrInf.indexOf("TAI"))
            map.put("TAI", instrInf.split("TAI")[1].split("/")[1]);

        //        String CTR;
        if (0 <instrInf.indexOf("CTR"))
            map.put("CTR", instrInf.split("CTR")[1].split("/")[1]);

        return map;
    }

    public static String generateXrefNo(String functionType) {
        String prefix1 = "0200";
        String prefix2 = "970457";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String prefix3 = simpleDateFormat.format(new Date());
        String prefix4 = RandomStringUtils.randomNumeric(4);
        return prefix1 + functionType + prefix2 + prefix3 + prefix4 ;
    }

    /**
     *  get number random
     * @param length
     * @return
     */
    public static String getNumberRandom(int length) {
        String result = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));

        return subStringbyIndex(result, length);
    }

    public static void main(String[] args) {
        String test = "000020000000";
        System.out.println(Long.valueOf(test.substring(0, 10)));

        System.out.println("ket qua: " + getAmount("100000000.00"));
        System.out.println("compare date: " + isEqualCurrentDate("2020-12-19T23:49:29.101+08:00"));

        String test2 = "/TAM/10000/TDT/0402074929/SCR/00000001/MCC/7399/AIC/704/PEM/000/PCD/00/FID/970472/MID/000000000000000";
        String test3 = "\"/MNM/IBT SMARTLINK            HANOI       VNM/SCC/704/BID/970415/FAI/123456789/TAI/987654321\"";
        String test4 = "/CTR/Chuyen tien lien ngan hang";
        Map<String, String> map;
        map = getInstrInf1( test2);
        Map<String, String> map3;
        map3 = getInstrInf1( test3);
        Map<String, String> map4;
        map4 = getInstrInf1( test4);
        System.out.println("map, TAM: " + map.get("TAM") + ", TDT: " + map.get("TDT")
                + ", SCR: " + map.get("SCR")
                + ", MCC: " + map.get("MCC")
                + ", AIC: " + map.get("AIC")
                + ", PEM: " + map.get("PEM")
                + ", PCD: " + map.get("PCD")
                + ", FID: " + map.get("FID")
                + ", MID: " + map.get("MID"));
//        MNM, SCC, BID, FAI, TAI
        System.out.println("map, MNM: " + map3.get("MNM") + ", SCC: " + map3.get("SCC")
                + ", BID: " + map3.get("BID")
                + ", FAI: " + map3.get("FAI")
                + ", TAI: " + map3.get("TAI"));
        //CTR
        System.out.println("map, CTR: " + map4.get("CTR") );

        System.out.println(generateXrefNo("4234324"));

    }
}

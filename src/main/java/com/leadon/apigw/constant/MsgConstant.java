package com.leadon.apigw.constant;

public class MsgConstant {

    public static final class subJson {
        public static final String AdrLine1 = "\"AdrLine\": [\"\"]";
        public static final String AdrLine2 = "\"AdrLine\": [\"\", \"\"]";
        public static final String AdrLine = "\"AdrLine\": [\"\", \"\", \"\"]";
    }

    public static final class message {
        public static final String pacs004_1 = "{\n" +
                "\t\"Header\": {\n" +
                "\t\t\"SenderReference\": \"\",\n" +
                "\t\t\"MessageIdentifier\": \"pacs.004.001.08\",\n" +
                "\t\t\"Format\": \"MX\",\n" +
                "\t\t\"Sender\": {\n" +
                "\t\t\t\"ID\": \"\",\n" +
                "\t\t\t\"Name\": \"\"\n" +
                "\t\t},\n" +
                "\t\t\"Receiver\": {\n" +
                "\t\t\t\"ID\": \"NAPASVNV\",\n" +
                "\t\t\t\"Name\": \"Napas\"\n" +
                "\t\t},\n" +
                "\t\t\"Timestamp\": \"\",\n" +
                "\t\t\"Signature\": \"\"\n" +
                "\t},\n" +
                "\t\"Payload\": {\n" +
                "\t\t\"AppHdr\": {\n" +
                "\t\t\t\"Fr\": {\n" +
                "\t\t\t\t\"FIId\": {\n" +
                "\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"To\": {\n" +
                "\t\t\t\t\"FIId\": {\n" +
                "\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\"MmbId\": \"NAPASVNV\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"BizMsgIdr\": \"\",\n" +
                "\t\t\t\"MsgDefIdr\": \"pacs.004.001.08\",\n" +
                "\t\t\t\"BizSvc\": \"ACH\",\n" +
                "\t\t\t\"CreDt\": \"\"\n" +
                "\t\t},\n" +
                "\t\t\"Document\": {\n" +
                "\t\t\t\"PmtRtr\": {\n" +
                "\t\t\t\t\"GrpHdr\": {\n" +
                "\t\t\t\t\t\"MsgId\": \"\",\n" +
                "\t\t\t\t\t\"CreDtTm\": \"\",\n" +
                "\t\t\t\t\t\"NbOfTxs\": 1,\n" +
                "\t\t\t\t\t\"TtlRtrdIntrBkSttlmAmt\": {\n" +
                "\t\t\t\t\t\t\"Ccy\": \"VND\",\n" +
                "\t\t\t\t\t\t\"Value\": \"\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"IntrBkSttlmDt\": \"\",\n" +
                "\t\t\t\t\t\"SttlmInf\": {\n" +
                "\t\t\t\t\t\t\"SttlmMtd\": \"CLRG\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"InstgAgt\": {\n" +
                "\t\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"InstdAgt\": {\n" +
                "\t\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"OrgnlGrpInf\": {\n" +
                "\t\t\t\t\t\"OrgnlMsgId\": \"\",\n" +
                "\t\t\t\t\t\"OrgnlMsgNmId\": \"pacs.008.001.07\",\n" +
                "\t\t\t\t\t\"OrgnlCreDtTm\": \"\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"TxInf\": [{\n" +
                "\t\t\t\t\t\"RtrId\": \"\",\n" +
                "\t\t\t\t\t\"OrgnlInstrId\": \"\",\n" +
                "\t\t\t\t\t\"OrgnlEndToEndId\": \"\",\n" +
                "\t\t\t\t\t\"OrgnlTxId\": \"\",\n" +
                "\t\t\t\t\t\"OrgnlIntrBkSttlmDt\": \"\",\n" +
                "\t\t\t\t\t\"RtrdIntrBkSttlmAmt\": {\n" +
                "\t\t\t\t\t\t\"Ccy\": \"VND\",\n" +
                "\t\t\t\t\t\t\"Value\": \"\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"IntrBkSttlmDt\": \"\",\n" +
                "\t\t\t\t\t\"ChrgBr\": \"SLEV\",\n" +
                "\t\t\t\t\t\"InstgAgt\": {\n" +
                "\t\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"RtrRsnInf\": [{\n" +
                "\t\t\t\t\t\t\"Orgtr\": {\n" +
                "\t\t\t\t\t\t\t\"Id\": {\n" +
                "\t\t\t\t\t\t\t\t\"OrgId\": {\n" +
                "\t\t\t\t\t\t\t\t\t\"Othr\": {\n" +
                "\t\t\t\t\t\t\t\t\t\t\"Id\": \"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"Rsn\": {\n" +
                "\t\t\t\t\t\t\t\"Prtry\": \"\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"AddtlInf\": [\"\"]\n" +
                "\t\t\t\t\t}],\n" +
                "\t\t\t\t\t\"OrgnlTxRef\": {\n" +
                "\t\t\t\t\t\t\"IntrBkSttlmAmt\": {\n" +
                "\t\t\t\t\t\t\t\"Ccy\": \"VND\",\n" +
                "\t\t\t\t\t\t\t\"Value\": \"\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"PmtTpInf\": {\n" +
                "\t\t\t\t\t\t\t\"ClrChanl\": \"RTNS\",\n" +
                "\t\t\t\t\t\t\t\"SvcLvl\": {\n" +
                "\t\t\t\t\t\t\t\t\"Prtry\": \"0100\"\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\"LclInstrm\": {\n" +
                "\t\t\t\t\t\t\t\t\"Prtry\": \"CSDC\"\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\"CtgyPurp\": {\n" +
                "\t\t\t\t\t\t\t\t\"Prtry\": \"001\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"Dbtr\": {\n" +
                "\t\t\t\t\t\t\t\"Pty\": {\n" +
                "\t\t\t\t\t\t\t\t\"Nm\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"PstlAdr\": {";

        public static final String pacs004_2 = "}\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"DbtrAcct\": {\n" +
                "\t\t\t\t\t\t\t\"Id\": {\n" +
                "\t\t\t\t\t\t\t\t\"Othr\": {\n" +
                "\t\t\t\t\t\t\t\t\t\"Id\": \"\"\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\"Tp\": {\n" +
                "\t\t\t\t\t\t\t\t\"Prtry\": \"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"DbtrAgt\": {\n" +
                "\t\t\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"CdtrAgt\": {\n" +
                "\t\t\t\t\t\t\t\"FinInstnId\": {\n" +
                "\t\t\t\t\t\t\t\t\"ClrSysMmbId\": {\n" +
                "\t\t\t\t\t\t\t\t\t\"MmbId\": \"\"\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"Cdtr\": {\n" +
                "\t\t\t\t\t\t\t\"Pty\": {\n" +
                "\t\t\t\t\t\t\t\t\"Nm\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"PstlAdr\": {";

        public static final String pacs004_3 = "}\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"CdtrAcct\": {\n" +
                "\t\t\t\t\t\t\t\"Id\": {\n" +
                "\t\t\t\t\t\t\t\t\"Othr\": {\n" +
                "\t\t\t\t\t\t\t\t\t\"Id\": \"\"\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\"Tp\": {\n" +
                "\t\t\t\t\t\t\t\t\"Prtry\": \"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}]\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
    }
}

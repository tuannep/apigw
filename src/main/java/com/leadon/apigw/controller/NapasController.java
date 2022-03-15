package com.leadon.apigw.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.dto.NPResponse;
import com.leadon.apigw.model.DAS;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.service.InquiryService;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.service.NRTService;
import com.leadon.apigw.service.TransactionService;
import com.leadon.apigw.util.ACHUtil;
import com.leadon.apigw.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class NapasController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(NapasController.class);

	@Autowired
	private KafkaProducerService producer;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private InquiryService inquiryService;

	@Autowired
	private NRTService nrtService;

//	@Autowired
//	private LimitService limitService;
//
//	@Autowired
//	private CopyPaymentService copyPaymentService;
//
//	@Autowired
//	private DisputeService disputeService;
//
//	@Autowired
//	private ReturnPaymentService returnPaymentService;
//
//	@Autowired
//	private ReportService reportService;
	
	/**
	 * Single
	 * Receive msg from napas /single/napasvnv/pacs.008.001.07/{SenderRef}
	 * /single/napasvnv/acknack/{SenderRef}
	 * /single/napasvnv/camt.025.001.04/{SenderRef}
	 * /single/napasvnv/pacs.002.001.09/{SenderRef}
	 *
	 * @param messageIdentifier
	 * @param senderRef
	 * @param messageRequest
	 * @return
	 */
	@PutMapping(value = "/ach/v1/single/napasvnv/{MessageIdentifier}/{SenderRef}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NPResponse> receiveMessageSingle(@PathVariable("MessageIdentifier") String messageIdentifier,
														   @PathVariable("SenderRef") String senderRef, @RequestBody String messageRequest) {
		NPResponse npResponse = null;
		DataObj dataObj = null;
		try {
			logger.info(
					"Received msg from Napas, messageIdentifier: " + messageIdentifier + ", senderRef: " + senderRef);
			logger.debug("message: " + messageRequest);

			//Parse msg
			JsonNode root = JsonUtil.toJsonNode(messageRequest);

			if (!ACHUtil.validationNPMessage(root)) {
				npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				logger.debug("----------resp : " + npResponse);
				return new ResponseEntity<>(npResponse, HttpStatus.OK);
			}

			if (!ACHUtil.validationSignature(root)) {
				npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_SIGNATURE_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				logger.debug("----------resp : " + npResponse);
				return new ResponseEntity<>(npResponse, HttpStatus.OK);
			}

			switch (messageIdentifier) {
			case AppConstant.MsgIdr.PACS008:
				dataObj = nrtService.handlePacs008(root, messageRequest);
				break;
			case AppConstant.MsgIdr.ACKNACK:
				System.out.println("acknack");
				dataObj = transactionService.handleAckNack(root, messageRequest);
				break;
//			case AppConstant.MsgIdr.CAMT025:
//				System.out.println("camt025");
//				dataObj = transactionService.handleCamt025(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.ADMI002:
//				System.out.println("admi002");
//				dataObj = transactionService.handleAdmi002(root, messageRequest);
//				break;
			case AppConstant.MsgIdr.PACS002:
				System.out.println("pacs002");
				dataObj = transactionService.handlePacs002(root, messageRequest);
				break;
//			case AppConstant.MsgIdr.PACS004:
//				System.out.println("pacs004");
//				dataObj = returnPaymentService.handlePacs004(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.CAMT998:
//				System.out.println("camt998");
//				String typeCamt998 = JsonUtil.getVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Tp").asText();
//				if (typeCamt998.contains(AppConstant.DisputeConfig.CAMT998_DSPT_MSG)) {
//					dataObj = disputeService.handleCamt998(root, messageRequest);
//				} else if (typeCamt998.contains(AppConstant.ReportConfig.CAMT998_REPORT_MSG)) {
//					dataObj = reportService.handleCamt998(root, messageRequest);
//				} else {
//					npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//							AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//					return new ResponseEntity<>(npResponse, HttpStatus.OK);
//				}
//				break;
//			case AppConstant.MsgIdr.CAMT010:
//				System.out.println("camt010");
//				dataObj = limitService.handleCamt010(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.CAMT034:
//				System.out.println("camt034");
//				dataObj = copyPaymentService.handleCamt034(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.PACS028:
//				System.out.println("pacs028");
//				dataObj = inquiryService.handlePacs028(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.CAMT052:
//				System.out.println("camt052");
//				dataObj = reportService.handleCamt052(root, messageRequest);
//				break;
//			case AppConstant.MsgIdr.CAMT053:
//				System.out.println("camt053");
//				dataObj = reportService.handleCamt053(root, messageRequest);
//				break;
			default:
				System.out.println("MessageIdentifier doesn't match");
				npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				return new ResponseEntity<>(npResponse, HttpStatus.OK);
			}

			npResponse = new NPResponse(dataObj.getEcode(), dataObj.getEdesc(), dataObj.getDataVal("duplicated"));

			//Push log response
			if (dataObj.getDataVal("transId") != null && !"".equals(dataObj.getDataVal("transId"))) {
				producer.pushMsgLogRes(dataObj.getDataVal("transId"), npResponse.toString(), AppConstant.LogConfig.NAPAS,AppConstant.LogConfig.BANK , AppConstant.LogConfig.CATEGORY_NAPAS);
			}

		} catch (Exception e) {
			logger.error("Received msg from Napas, exception:" + e.getMessage());
		}
		return new ResponseEntity<>(npResponse, HttpStatus.OK);
	}

//	/**
//	 * Batch
//	 * Receive msg from napas /batch/napasvnv/pacs.008.001.07/{SenderRef}
//	 * /batch/napasvnv/acknack/{SenderRef}
//	 * /batch/napasvnv/camt.025.001.04/{SenderRef}
//	 * /batch/napasvnv/pacs.002.001.09/{SenderRef}
//	 *
//	 * @param messageIdentifier
//	 * @param senderRef
//	 * @param messageRequest
//	 * @return
//	 */
//	@PutMapping(value = "/ach/v1/batch/napasvnv/{MessageIdentifier}/{SenderRef}", produces=MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<NPResponse> receiveMessageBatch(@PathVariable("MessageIdentifier") String messageIdentifier,
//													 @PathVariable("SenderRef") String senderRef, @RequestBody String messageRequest) {
//		NPResponse npResponse = null;
//		DataObj dataObj = null;
//		try {
//			logger.info(
//					"Received msg from Napas, messageIdentifier: " + messageIdentifier + ", senderRef: " + senderRef);
//			logger.debug("message: " + messageRequest);
//
//			//Parse msg
//			JsonNode root = JsonUtil.toJsonNode(messageRequest);
//
//			if (!ACHUtil.validationNPMessage(root)) {
//				npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				logger.debug("----------resp : " + npResponse);
//				return new ResponseEntity<>(npResponse, HttpStatus.OK);
//			}
//
//			if (!ACHUtil.validationSignature(root)) {
//				npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//						AppConstant.ResponseMsg.RESP_SIGNATURE_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				logger.debug("----------resp : " + npResponse);
//				return new ResponseEntity<>(npResponse, HttpStatus.OK);
//			}
//
//			switch (messageIdentifier) {
//				case AppConstant.MsgIdr.PACS008:
//					dataObj = nrtService.handlePacs008(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.ACKNACK:
//					System.out.println("acknack");
//					dataObj = transactionService.handleAckNack(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.CAMT025:
//					System.out.println("camt025");
//					dataObj = transactionService.handleCamt025(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.ADMI002:
//					System.out.println("admi002");
//					dataObj = transactionService.handleAdmi002(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.PACS002:
//					System.out.println("pacs002");
//					dataObj = transactionService.handlePacs002(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.PACS004:
//					System.out.println("pacs004");
//					dataObj = returnPaymentService.handlePacs004(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.CAMT998:
//					System.out.println("camt998");
//					String typeCamt998 = JsonUtil.getVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Tp").asText();
//					if (typeCamt998.contains(AppConstant.DisputeConfig.CAMT998_DSPT_MSG)) {
//						dataObj = disputeService.handleCamt998(root, messageRequest);
//					} else if (typeCamt998.contains(AppConstant.ReportConfig.CAMT998_REPORT_MSG)) {
//						dataObj = reportService.handleCamt998(root, messageRequest);
//					} else {
//						npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//								AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//						return new ResponseEntity<>(npResponse, HttpStatus.OK);
//					}
//					break;
//				case AppConstant.MsgIdr.CAMT010:
//					System.out.println("camt010");
//					dataObj = limitService.handleCamt010(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.CAMT034:
//					System.out.println("camt034");
//					dataObj = copyPaymentService.handleCamt034(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.PACS028:
//					System.out.println("pacs028");
//					dataObj = inquiryService.handlePacs028(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.CAMT052:
//					System.out.println("camt052");
//					dataObj = reportService.handleCamt052(root, messageRequest);
//					break;
//				case AppConstant.MsgIdr.CAMT053:
//					System.out.println("camt053");
//					dataObj = reportService.handleCamt053(root, messageRequest);
//					break;
//				default:
//					System.out.println("MessageIdentifier doesn't match");
//					npResponse = new NPResponse(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//							AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//					return new ResponseEntity<>(npResponse, HttpStatus.OK);
//			}
//
//			npResponse = new NPResponse(dataObj.getEcode(), dataObj.getEdesc(), dataObj.getDataVal("duplicated"));
//
//			//Push log response
//			if (dataObj.getDataVal("transId") != null && !"".equals(dataObj.getDataVal("transId"))) {
//				producer.pushMsgLogRes(dataObj.getDataVal("transId"), npResponse.toString(), AppConstant.LogConfig.NAPAS,AppConstant.LogConfig.BANK , AppConstant.LogConfig.CATEGORY_NAPAS);
//			}
//
//		} catch (Exception e) {
//			logger.error("Received msg from Napas, exception:" + e.getMessage());
//		}
//		return new ResponseEntity<>(npResponse, HttpStatus.OK);
//	}

	@PutMapping(value = "/ach/v1/api/{SenderId}/{SenderRef}/das/account", produces=MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<DAS> receiveDAS(@PathVariable("SenderId") String senderId,
									  @PathVariable("SenderRef") String senderReference, @RequestBody String message){
		DAS das = inquiryService.inquiryDASInComing(senderId, senderReference, message);
		return new ResponseEntity<>(das, HttpStatus.OK);
	}

}

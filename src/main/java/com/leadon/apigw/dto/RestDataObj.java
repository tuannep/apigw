package com.leadon.apigw.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class RestDataObj extends AbstractDto {

	private static final long serialVersionUID = 4074420667953242944L;
	
	private String httpStatus;
	private String response;
	private boolean isReadTimedOut;
	private String errorMsg;


}

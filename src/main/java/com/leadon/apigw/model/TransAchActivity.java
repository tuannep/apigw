package com.leadon.apigw.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "TRANS_ACH_ACTIVITY")
public class TransAchActivity extends AbstractModel {

	private static final long serialVersionUID = 5152498861045460512L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITY_GEN")
    @SequenceGenerator(sequenceName = "SEQ_TRANS_ACH_ACTIVITY", allocationSize = 1, name = "ACTIVITY_GEN")
	@Column(name = "ACTIVITY_ID")
    private Long activityId;
	
	@Column(name = "TRANS_ID")
    private Long transId;
	
	@Column(name = "SENDER_REF_ID")
	private String senderRefId;
	
	@Column(name = "MSG_IDENTIFIER")
	private String msgIdentifier;
	
	@Column(name = "ACTIVITY_DESC")
	private String activityDesc;
	
	@Column(name = "MSG_TYPE")
	private String msgType;
	
	@Lob
	@Column(name = "MSG_CONTENT")
	private String msgContent;
	
	@Column(name = "MSG_DT")
	private Date msgDt;
	
	@Column(name = "ACTIVITY_STEP")
	private String activityStep;
	
	@Column(name = "ERR_CODE")
	private String errCode;
	
	@Column(name = "ERR_DESC")
	private String errDesc;
	
	@Column(name = "CREATED_ON")
	private Date createdOn;

}

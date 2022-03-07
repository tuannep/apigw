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
@Table(name = "TRANS_MESSAGE_LOG")
public class TransMessageLog extends AbstractModel {

	private static final long serialVersionUID = 608866742902774422L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MSG_GEN")
    @SequenceGenerator(sequenceName = "SEQ_TRANS_MESSAGE_LOG", allocationSize = 1, name = "MSG_GEN")
	@Column(name = "MSG_ID")
    private Long msgId;
	
	@Column(name = "TRANS_ID")
    private Long transId;
	
	@Column(name = "SENDER")
	private String sender;
	
	@Column(name = "RECEIVER")
	private String receiver;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "MSG_TYPE")
	private String msgType;
	
	@Lob
	@Column(name = "MSG_CONTENT")
	private String msgContent;
	
	@Column(name = "CREATED_ON")
    private Date createdOn;

}

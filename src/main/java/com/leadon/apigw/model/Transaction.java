package com.leadon.apigw.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "TRANSACTION")
public class Transaction extends AbstractModel {
	
	private static final long serialVersionUID = -6970117103102197699L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANS_GEN")
    @SequenceGenerator(sequenceName = "SEQ_TRANSACTION", allocationSize = 1, name = "TRANS_GEN")
	@Column(name = "TRANS_ID")
    private Long transId;

	@Column(name = "XREF_ID")
	private String xrefId;
	
	@Column(name = "CLIENT_ID")
	private String clientId;
	
	@Column(name = "CHANNEL_ID")
	private String channelId;
	
	@Column(name = "SERVICE_GROUP_ID")
	private String serviceGroupId;
	
	@Column(name = "SERVICE_ID")
	private String serviceId;
	
	@Column(name = "TRANS_CATE")
	private String transCate;
	
	@Column(name = "TRANS_INOUT")
	private String transInout;
	
	@Column(name = "TRANS_DESC")
	private String transDesc;
	
	@Column(name = "AMOUNT")
	private BigDecimal amount;
	
	@Column(name = "CCY")
	private String ccy;
	
	@Column(name = "TRANS_DT")
	private String transDt;
	
	@Column(name = "TRANS_STAT")
	private String transStat;
	
	@Column(name = "TRANS_STAT_DESC")
	private String transStatDesc;
	
    @Column(name = "CREATED_ON")
    private Date createdOn;
    
    @Column(name = "CREATED_BY")
    private String createdBy;
	
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;
    
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "IS_INVEST_TO_CORE_SUCCESS")
	private Integer isInvestToCoreSuccess;

    @Column(name = "INVEST_TO_CORE_TOTAL")
	private Integer investToCoreTotal;

    @Column(name = "INVEST_TO_CORE_RESULT")
	private String investToCoreResult;

	@Column(name = "IN_ACTION")
	private String inAction;
}

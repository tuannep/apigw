package com.leadon.apigw.model;

import lombok.*;
import com.leadon.apigw.model.key.AchCustomerInfoKey;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@IdClass( AchCustomerInfoKey.class )
@Table(name = "ACH_CUSTOMER_INFO")
public class AchCustomerInfo extends AbstractModel {
	
	private static final long serialVersionUID = 8815691838465754493L;

	@Id
	@Column(name = "CDTR_ACCT_NO")
	private String cdtrAcctNo;

	@Column(name = "CDTR_NAME")
	private String cdtrName;
	
	@Column(name = "CDTR_ADDRESS")
	private String cdtrAddress;
	
	@Column(name = "CDTR_ACCT_TYPE")
	private String cdtrAcctType;

	@Id
	@Column(name = "CDTR_MEM_ID")
	private String cdtrMemId;

	@Column(name = "CCY")
	private String ccy;

	@Column(name = "DBTR_ACCT_NO")
	private String dbtrAcctNo;

	@Column(name = "DBTR_MEM_ID")
	private String dbtrMemId;
	
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

	public AchCustomerInfoKey getId() {
		return new AchCustomerInfoKey(
				cdtrAcctNo,
				cdtrMemId
		);
	}

	public void setId(AchCustomerInfoKey id) {
		this.cdtrAcctNo = id.getCdtrAcctNo();
		this.cdtrMemId = id.getCdtrMemId();
	}

}

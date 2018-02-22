package com.test.dao.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "testtable")
public class FlightDetails implements Serializable {
	private static final long serialVersionUID = 885754996983539552L;
	@EmbeddedId
	private HaulTypeKeyGeneratorPK id;
	@Column(name = "EXLR_CURRENCY")
	private String currency;
	@Column(name = "EXLR_AMOUNT")
	private String amount;
	@Column(name = "EXLR_EFFECTIVE_TO")
	private Date effectiveTo;
	@Column(name = "EXLR_CREATE_USER")
	private String createUser;
	@Column(name = "EXLR_CREATE_TIMESTAMP")
	private Date createTimeStamp;
	@Column(name = "EXLR_LAST_UPDATE_USER")
	private String lastUpdateUser;
	@Column(name = "EXLR_LAST_UPDATE_TIMESTAMP")
	private Date lastUpdateTimeStamp;

	public HaulTypeKeyGeneratorPK getId() {
		return id;
	}

	public void setId(HaulTypeKeyGeneratorPK id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(Date createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getLastUpdateTimeStamp() {
		return lastUpdateTimeStamp;
	}

	public void setLastUpdateTimeStamp(Date lastUpdateTimeStamp) {
		this.lastUpdateTimeStamp = lastUpdateTimeStamp;
	}

	class HaulTypeKeyGeneratorPK implements Serializable {
		private static final long serialVersionUID = -5764384324263673587L;
		private Integer id;
		private String num;
	}
}

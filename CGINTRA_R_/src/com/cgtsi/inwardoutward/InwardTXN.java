package com.cgtsi.inwardoutward;

import java.util.Date;

public class InwardTXN {
	public InwardTXN() {
	}

	private String acctNo;
	private Date txnDate;
	private String txnReference;
	private String txnType;
	private String bankIFSC;
	private String bankName;
	private String bankPlace;
	private String utr;
	private String remarks;
	private double txnAmt;
	private String remitterAcctNo;
	private String remitterName;
	private Date txnTime;
	private String txnTimeStr;
	private String txnDateStr;
	private String txnAmtStr;

	public String getTxnTimeStr() {
		return txnTimeStr;
	}

	public void setTxnTimeStr(String txnTimeStr) {
		this.txnTimeStr = txnTimeStr;
	}

	public String getTxnDateStr() {
		return txnDateStr;
	}

	public void setTxnDateStr(String txnDateStr) {
		this.txnDateStr = txnDateStr;
	}

	public String getTxnAmtStr() {
		return txnAmtStr;
	}

	public void setTxnAmtStr(String txnAmtStr) {
		this.txnAmtStr = txnAmtStr;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnReference(String txnReference) {
		this.txnReference = txnReference;
	}

	public String getTxnReference() {
		return txnReference;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setBankIFSC(String bankIFSC) {
		this.bankIFSC = bankIFSC;
	}

	public String getBankIFSC() {
		return bankIFSC;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankPlace(String bankPlace) {
		this.bankPlace = bankPlace;
	}

	public String getBankPlace() {
		return bankPlace;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getUtr() {
		return utr;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setTxnAmt(double txnAmt) {
		this.txnAmt = txnAmt;
	}

	public double getTxnAmt() {
		return txnAmt;
	}

	public void setRemitterAcctNo(String remitterAcctNo) {
		this.remitterAcctNo = remitterAcctNo;
	}

	public String getRemitterAcctNo() {
		return remitterAcctNo;
	}

	public void setRemitterName(String remitterName) {
		this.remitterName = remitterName;
	}

	public String getRemitterName() {
		return remitterName;
	}

	public void setTxnTime(Date txnTime) {
		this.txnTime = txnTime;
	}

	public Date getTxnTime() {
		return txnTime;
	}
}

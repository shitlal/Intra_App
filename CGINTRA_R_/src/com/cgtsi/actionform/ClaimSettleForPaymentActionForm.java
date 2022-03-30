/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cgtsi.actionform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.validator.ValidatorActionForm;

/**
 * @author PC2184
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ClaimSettleForPaymentActionForm extends ValidatorActionForm{

	/**
	 * 
	 */	
	public ClaimSettleForPaymentActionForm() {
		
		colletralCoulmnName = new ArrayList();
		colletralCoulmnValue = new ArrayList();
		startDt = "";
		//AcctDtl = "";
		AcctDtlAvlFlag="";
		checkboxfield = new HashMap();
		endDt = "";
		memberId = "";
		status="";
	}
	
	private List colletralCoulmnName = new ArrayList();
	private List colletralCoulmnValue = new ArrayList();
	private String AcctDtl;
	//private String AcctDtl;
	private String AcctDtlAvlFlag;
 
	public String getAcctDtlAvlFlag() {
		return AcctDtlAvlFlag;
	}
	public void setAcctDtlAvlFlag(String acctDtlAvlFlag) {
		AcctDtlAvlFlag = acctDtlAvlFlag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	private String status;
	
	private String checkValue;
	
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}

	private String check;
	  



	public String getCheckValue() {
		return checkValue;
	}
	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}

	private String startDt;
	private Map checkboxfield;
	
	public Map getCheckboxfield() {
		return checkboxfield;
	}
	public void setCheckboxfield(Map checkboxfield) {
		this.checkboxfield = checkboxfield;
	}
	public String getStartDt() {
		return startDt;
	}
	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}
	public String getEndDt() {
		return endDt;
	}
	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}

	private String endDt;
	private String memberId;
	
	
	 
	
	public List getColletralCoulmnName() {
		return colletralCoulmnName;
	}
	public void setColletralCoulmnName(List colletralCoulmnName) {
		this.colletralCoulmnName = colletralCoulmnName;
	}
	public List getColletralCoulmnValue() {
		return colletralCoulmnValue;
	}
	public void setColletralCoulmnValue(List colletralCoulmnValue) {
		this.colletralCoulmnValue = colletralCoulmnValue;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	 
	
	
	
	
	
}

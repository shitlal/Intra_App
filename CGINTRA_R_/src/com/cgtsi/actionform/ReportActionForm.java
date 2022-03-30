/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cgtsi.actionform;

import java.util.ArrayList;
import java.util.List;

import com.cgtsi.guaranteemaintenance.LegalSuitDetail;
import com.cgtsi.guaranteemaintenance.NPADetails;
import com.cgtsi.guaranteemaintenance.RecoveryProcedure;
import com.cgtsi.reports.QueryBuilderFields;
import org.apache.struts.validator.ValidatorActionForm;

/**
 * @author PC2184
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReportActionForm extends ValidatorActionForm{

	/**
	 * 
	 */
	private QueryBuilderFields queryBuilderFields= new QueryBuilderFields();
	private ArrayList queryReport = new ArrayList();
	private String memberId;
	private String borrowerId;
	private String cgpan;
	private String borrowerName;
	private String payId;
	private String zoneId;
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	private String bankId;

	private ArrayList borrowerDetailsForPIReport= new ArrayList();
	
	private ArrayList osPeriodicInfoDetails = new ArrayList();
	private ArrayList disbPeriodicInfoDetails = new ArrayList();
	private ArrayList repayPeriodicInfoDetails = new ArrayList();
	private NPADetails npaDetails = new NPADetails();
	private LegalSuitDetail legalSuitDetail = new LegalSuitDetail();
	private RecoveryProcedure recoveryProcedure= new RecoveryProcedure();
	private ArrayList recoveryProcedures = new ArrayList();
	private ArrayList recoveryDetails = new ArrayList();	
	
/*	private Map disbursementDate = new TreeMap();
	private Map disbursementAmount = new TreeMap();
	private Map finalDisbursement = new TreeMap();
	
	private Map repaymentAmount = new TreeMap() ;
	private Map repaymentDate = new TreeMap() ;
	
	private Map tcPrincipalOutstandingAmount = new TreeMap();
	private Map tcInterestOutstandingAmount = new TreeMap();
	private Map tcOutstandingAsOnDate = new TreeMap();
	private Map wcFBPrincipalOutstandingAmount = new TreeMap();
	private Map wcFBInterestOutstandingAmount = new TreeMap();
	private Map wcFBOutstandingAsOnDate = new TreeMap();
	private Map wcNFBInterestOutstandingAmount = new TreeMap();
	private Map wcNFBPrincipalOutstandingAmount = new TreeMap();
	private Map wcNFBOutstandingAsOnDate = new TreeMap();
	
*/	
	
	private String checkValue;
	public String getCheckValue() {
		return checkValue;
	}
	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}
	public ReportActionForm() {
		super();
	}
/**
   * 
   * @param payId
   */
public void setPayId(String payId)
{
 this.payId= payId;
}
/**
   * 
   * @return payId
   */
public String getPayId()
{
 return this.payId;
}
	/**
	 * @return
	 */
	public QueryBuilderFields getQueryBuilderFields() {
		return queryBuilderFields;
	}

	/**
	 * @param fields
	 */
	public void setQueryBuilderFields(QueryBuilderFields fields) {
		queryBuilderFields = fields;
	}

	/**
	 * @return
	 */
	public ArrayList getQueryReport() {
		return queryReport;
	}

	/**
	 * @param report
	 */
	public void setQueryReport(ArrayList report) {
		queryReport = report;
	}

	/**
	 * @return
	 */
	public String getBorrowerId() {
		return borrowerId;
	}

	/**
	 * @return
	 */
	public String getBorrowerName() {
		return borrowerName;
	}

	/**
	 * @return
	 */
	public String getCgpan() {
		return cgpan;
	}

	/**
	 * @param string
	 */
	public void setBorrowerId(String string) {
		borrowerId = string;
	}

	/**
	 * @param string
	 */
	public void setBorrowerName(String string) {
		borrowerName = string;
	}

	/**
	 * @param string
	 */
	public void setCgpan(String string) {
		cgpan = string;
	}

	/**
	 * @return
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * @param string
	 */
	public void setMemberId(String string) {
		memberId = string;
	}


	/**
	 * @return
	 */
	public ArrayList getDisbPeriodicInfoDetails() {
		return disbPeriodicInfoDetails;
	}

	/**
	 * @return
	 */
	public ArrayList getOsPeriodicInfoDetails() {
		return osPeriodicInfoDetails;
	}

	/**
	 * @return
	 */
	public ArrayList getRepayPeriodicInfoDetails() {
		return repayPeriodicInfoDetails;
	}

	/**
	 * @param list
	 */
	public void setDisbPeriodicInfoDetails(ArrayList list) {
		disbPeriodicInfoDetails = list;
	}

	/**
	 * @param list
	 */
	public void setOsPeriodicInfoDetails(ArrayList list) {
		osPeriodicInfoDetails = list;
	}

	/**
	 * @param list
	 */
	public void setRepayPeriodicInfoDetails(ArrayList list) {
		repayPeriodicInfoDetails = list;
	}


	/**
	 * @return
	 */
	public ArrayList getBorrowerDetailsForPIReport() {
		return borrowerDetailsForPIReport;
	}

	/**
	 * @param list
	 */
	public void setBorrowerDetailsForPIReport(ArrayList list) {
		borrowerDetailsForPIReport = list;
	}

	/**
	 * @return
	 */
	public NPADetails getNpaDetails() {
		return npaDetails;
	}

	/**
	 * @return
	 */
	public ArrayList getRecoveryDetails() {
		return recoveryDetails;
	}

	/**
	 * @param details
	 */
	public void setNpaDetails(NPADetails details) {
		npaDetails = details;
	}

	/**
	 * @param list
	 */
	public void setRecoveryDetails(ArrayList list) {
		recoveryDetails = list;
	}

	/**
	 * @return
	 */
	public LegalSuitDetail getLegalSuitDetail() {
		return legalSuitDetail;
	}

	/**
	 * @return
	 */
	public RecoveryProcedure getRecoveryProcedure() {
		return recoveryProcedure;
	}

	/**
	 * @return
	 */
	public ArrayList getRecoveryProcedures() {
		return recoveryProcedures;
	}

	/**
	 * @param detail
	 */
	public void setLegalSuitDetail(LegalSuitDetail detail) {
		legalSuitDetail = detail;
	}

	/**
	 * @param procedure
	 */
	public void setRecoveryProcedure(RecoveryProcedure procedure) {
		recoveryProcedure = procedure;
	}

	/**
	 * @param list
	 */
	public void setRecoveryProcedures(ArrayList list) {
		recoveryProcedures = list;
	}
	
	private List danDetails = new ArrayList();
	private List colletralCoulmnName = new ArrayList();
	private List colletralCoulmnValue = new ArrayList();
	private List reportTypeList = new ArrayList();
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
	public List getReportTypeList() {
		return reportTypeList;
	}
	public void setReportTypeList(List reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	private java.util.Date dateOfTheDocument20;
	private java.util.Date dateOfTheDocument21;
	private String reportType;

	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public List getDanDetails() {
		return danDetails;
	}
	public void setDanDetails(List danDetails) {
		this.danDetails = danDetails;
	}
	public java.util.Date getDateOfTheDocument20() {
		return dateOfTheDocument20;
	}
	public void setDateOfTheDocument20(java.util.Date dateOfTheDocument20) {
		this.dateOfTheDocument20 = dateOfTheDocument20;
	}
	public java.util.Date getDateOfTheDocument21() {
		return dateOfTheDocument21;
	}
	public void setDateOfTheDocument21(java.util.Date dateOfTheDocument21) {
		this.dateOfTheDocument21 = dateOfTheDocument21;
	}
	

}

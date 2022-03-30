/*************************************************************
   *
   * Name of the class: AdministrationActionForm.java
   * This Administration Action Form class holds the dynamic values to be
   * displayed on the jsp pages.
   *
   *
   * @author : Veldurai T
   * @version:
   * @since: Nov 7, 2003
   **************************************************************/
package com.cgtsi.actionform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

//import org.apache.struts.action.ActionForm;
import org.apache.struts.validator.ValidatorActionForm;
import org.apache.struts.action.ActionMapping;
import com.cgtsi.common.Log;
import com.cgtsi.admin.PLRMaster;
import java.util.Set;
import java.util.Iterator;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
//import org.apache.struts.action.ActionMapping;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;


/**
 * @author Veldurai T
 *
 * This class holds the roles and privileges in a mapped format.
 * These mapped values are used to display the roles and privileges in the
 * jsp page. Also, while submitting the jsp page to values from the jsp page
 * are retrieved using this AdministrationActionForm class.
 *
 */
public class AdministrationActionForm extends ValidatorActionForm {

	private Map roles=new HashMap();
	
	private Map queryRespon=new HashMap();
	
	


	
	public Map getQueryRespon() {
		return queryRespon;
	}


	public void setQueryRespon(Map queryRespon) {
		this.queryRespon = queryRespon;
	}

	private Map privileges=new HashMap();
	private String userId="";
	private ArrayList roleNames=new ArrayList();
	private String memberId="";
	private ArrayList plrBanks;

	private Map plrMasters=new HashMap();
	private String shortNameMemId;
	private String bankName;
  //added zoneName and branchName by sukumar@path on 16-05-2009
  private String zoneName;
  private String branchName;
	private ArrayList plrDetails;
	private int plrIndexValue;
	
	public Date getDateOfTheDocument() {
		return dateOfTheDocument;
	}


	public void setDateOfTheDocument(Date dateOfTheDocument) {
		this.dateOfTheDocument = dateOfTheDocument;
	}


	public Date getDateOfTheDocument1() {
		return dateOfTheDocument1;
	}


	public void setDateOfTheDocument1(Date dateOfTheDocument1) {
		this.dateOfTheDocument1 = dateOfTheDocument1;
	}


	public String getCheck1() {
		return check1;
	}


	public void setCheck1(String check1) {
		this.check1 = check1;
	}

	private Date dateOfTheDocument;
	  
	  private Date dateOfTheDocument1;
	  

	  private String check1;
	  
	

	public String getQueryStatus() {
		return queryStatus;
	}

	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}

	private String queryStatus;
	
	
	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getQueryDescription() {
		return queryDescription;
	}

	public void setQueryDescription(String queryDescription) {
		this.queryDescription = queryDescription;
	}

	public String getContPerson() {
		return contPerson;
	}

	public void setContPerson(String contPerson) {
		this.contPerson = contPerson;
	}

	private String departments;

	   private String queryDescription;

	public String getQueryResponse() {
		return queryResponse;
	}

	public void setQueryResponse(String queryResponse) {
		this.queryResponse = queryResponse;
	}

	private String queryResponse;
	
	  
	   private String contPerson;
	   
	  
	

	private String queryRaisDt;
	   
	   public String getQueryRaisDt() {
		return queryRaisDt;
	}


	public void setQueryRaisDt(String queryRaisDt) {
		this.queryRaisDt = queryRaisDt;
	}


	public String getQueryId() {
		return queryId;
	}


	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	private String queryId;
	   
	   public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	  public String[] getQryRemarks() {
		return qryRemarks;
	}

	public void setQryRemarks(String[] qryRemarks) {
		this.qryRemarks = qryRemarks;
	}

	

	private String qryRemarks[];

	private String phoneNo;
	   
	   private String emailId;
	   
	

	   public Map getQueryRespons() {
		return queryRespon;
	}

	public void setQueryRespons(Map queryRespons) {
		this.queryRespon = queryRespons;
	}

	
	   
	   
	   
	  
	   public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	private String bankId;
	   
	   
	   private String zoneId;
	   
	  
	   private String branchId;
	   
	 
	private ArrayList mliQueryList;
	
	   public ArrayList getMliQueryList() {
		return mliQueryList;
	}

	public void setMliQueryList(ArrayList mliQueryList) {
		this.mliQueryList = mliQueryList;
	}

	
	
	   
	
	
	

	private PLRMaster plrMaster=null;

	public Map getPlrMasters()
	{
		return this.plrMasters;
	}

	public void setPlrMasters(Map object)
	{
		this.plrMasters = object;
	}

	public Object getPlrMasters(String key)
	{
		return this.plrMasters.get(key);
	}

	public void setPlrMasters(String key, Object value)
	{
        Log.log(Log.ERROR,"AdministrationActionForm","setPlrMasters","Printing key :" + key + " value :" + value);
		this.plrMasters.put(key,value);
	}
/**
   * 
   * @return zoneName
   */
 public String getZoneName(){
   return this.zoneName;
 }
 /**
   * 
   * @param zoneName
   */
 public void setZoneName(String zoneName){
   this.zoneName = zoneName;
 }
 /**
   * 
   * @return branchName
   */
 public String getBranchName()
 {
  return this.branchName;
 }
 /**
   * 
   * @param branchName
   */
public void setBranchName(String branchName){
  this.branchName = branchName;
}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId=userId;
	}

	public void setRole(String key, Object value)
	{
		roles.put(key,value);
	}

	public void setPrivilege(String key, Object value)
	{
		privileges.put(key,value);
	}

	public Object getRole(String key)
	{
		return roles.get(key);
	}

	public Object getPrivilege(String key)
	{
		return privileges.get(key);
	}

	public Map getRoles()
	{
		return roles;
	}

	public Map getPrivileges()
	{
		return privileges;
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1)
	{
		super.reset(arg0, arg1);
		//System.out.println("reset called");
		roles.clear();
		privileges.clear();
	}
	/**
	 * @return
	 */
	public ArrayList getRoleNames() {
		return roleNames;
	}

	/**
	 * @param map
	 */
	public void setPrivileges(Map map) {
		privileges = map;
	}

	/**
	 * @param list
	 */
	public void setRoleNames(ArrayList list) {
		roleNames = list;
	}

	/**
	 * @param map
	 */
	public void setRoles(Map map) {
		roles = map;
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

	public ArrayList getPlrBanks()
	{
		return this.plrBanks;
	}

	public void setPlrBanks(ArrayList banks)
	{
		this.plrBanks = banks;
	}

	public String getShortNameMemId()
	{
		return this.shortNameMemId;
	}

	public void setShortNameMemId(String id)
	{
		this.shortNameMemId = id;
	}

	public String getBankName()
	{
		return this.bankName;
	}

	public void setBankName(String name)
	{
		this.bankName = name;
	}

	public ArrayList getPlrDetails()
	{
		return this.plrDetails;
	}

	public void setPlrDetails(ArrayList obj)
	{
		this.plrDetails = obj;
	}

    public int getPlrIndexValue()
    {
		return this.plrIndexValue;
	}

	public void setPlrIndexValue(int val)
	{
		this.plrIndexValue = val;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors=super.validate(mapping, request);

		if(errors.isEmpty())
		{

			Log.log(Log.INFO,"RPActionForm","validate","param method value is "+request.getParameter("method"));

			if(mapping.getPath().equals("/modifyPLR"))
			{
				Log.log(Log.INFO,"AdministrationActionForm","validate"," PLRMaster");

				Set plrMastersSet=plrMasters.keySet();
				Iterator plrMastersIterator=plrMastersSet.iterator();
				boolean fromDate=false;
				boolean toDate=false;
				boolean shortTermPLRBoolean=false;
				boolean mediumTermPLRBoolean=false;
				boolean longTermPLRBoolean=false;
				boolean shortTermPeriodBoolean=false;
				boolean mediumTermPeriodBoolean=false;
				boolean longTermPeriodBoolean=false;
				boolean validPLRType = false;

				while(plrMastersIterator.hasNext())
				{
					String key=(String)plrMastersIterator.next();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," key "+key);

					PLRMaster plrmaster=(PLRMaster)plrMasters.get(key);

					Log.log(Log.INFO,"AdministrationActionForm","validate"," Printing PLRMaster :" + plrmaster);

					java.util.Date startDateParam = plrmaster.getStartDate();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," startDateParam :" + startDateParam);
					java.util.Date endDateParam = plrmaster.getEndDate();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," endDateParam :" + endDateParam);
					double shortTermPLR = plrmaster.getShortTermPLR();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," shortTermPLR :" + shortTermPLR);
					double mediumTermPLR = plrmaster.getMediumTermPLR();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," mediumTermPLR :" + mediumTermPLR);
					double longTermPLR = plrmaster.getLongTermPLR();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," longTermPLR :" + longTermPLR);
					int shortTermPeriod = plrmaster.getShortTermPeriod();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," shortTermPeriod :" + shortTermPeriod);
					int mediumTermPeriod = plrmaster.getMediumTermPeriod();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," mediumTermPeriod :" + mediumTermPeriod);
					int longTermPeriod = plrmaster.getLongTermPeriod();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," longTermPeriod :" + longTermPeriod);
					String plrType = plrmaster.getPLR();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," plrType :" + plrType);
					double bplr = plrmaster.getBPLR();
					Log.log(Log.INFO,"AdministrationActionForm","validate"," bplr :" + bplr);

                    // Validating Start Date
					if(startDateParam==null || startDateParam.toString().equals(""))
					{
						ActionError error=new ActionError("errors.required","From Date ");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

						Log.log(Log.INFO,"RPActionForm","validate"," From date is null ");
						fromDate=true;
					}
					else
					{
						if((startDateParam.toString().trim()).length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="From Date ";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							fromDate=true;

							Log.log(Log.INFO,"AdministrationActionForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							Log.log(Log.INFO,"AdministrationActionForm","validate"," startDateParam :" + startDateParam);
                            java.util.Date fromUtilDate = dateFormat.parse(startDateParam.toString(),new ParsePosition(0));
                            Log.log(Log.INFO,"AdministrationActionForm","validate"," fromUtilDate :" + fromUtilDate);
							if((fromUtilDate == null) || ((fromUtilDate.toString()).equals("")))
							{
								ActionError error=new ActionError("errors.date","From Date");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								fromDate=true;
							}

						}
					}

					// Validating End Date
					if(endDateParam==null || endDateParam.toString().equals(""))
					{
						Log.log(Log.INFO,"RPActionForm","validate"," End date is null ");
					}
					else
					{
						if((endDateParam.toString().trim()).length()<10)
						{
							String [] errorStrs=new String[2];
							errorStrs[0]="End Date ";
							errorStrs[1]="10";

							ActionError error=new ActionError("errors.minlength",errorStrs);

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							toDate=true;

							Log.log(Log.INFO,"AdministrationActionForm","validate"," length is less than zero");
						}
						else
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							Log.log(Log.INFO,"AdministrationActionForm","validate"," endDateParam :" + endDateParam);
                            java.util.Date toUtilDate = dateFormat.parse(endDateParam.toString(),new ParsePosition(0));
                            Log.log(Log.INFO,"AdministrationActionForm","validate"," endDateStr :" + toUtilDate);
							if((toUtilDate == null) || ((toUtilDate.toString()).equals("")))
							{
								ActionError error=new ActionError("errors.date","End Date");

								errors.add(ActionErrors.GLOBAL_ERROR,error);
								toDate=true;
							}
							else
							{
								if((endDateParam.compareTo(startDateParam)) < 0)
								{
									ActionError error=new ActionError("errors.date","End Date cannot be earlier than From Date, ");

									errors.add(ActionErrors.GLOBAL_ERROR,error);
									toDate=true;
								}
							}

						}
					}

                    // Validating Short Term PLR, Medium Term PLR and Long Term PLR
                    if(shortTermPLR <= 0.0)
                    {
						ActionError error=new ActionError("errors.required","Short Term PLR greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						shortTermPLRBoolean = true;
					}

                    if(mediumTermPLR <= 0.0)
                    {
						ActionError error=new ActionError("errors.required","Medium Term PLR greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						mediumTermPLRBoolean = true;
					}

                    if(longTermPLR <= 0.0)
                    {
						ActionError error=new ActionError("errors.required","Long Term PLR greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						longTermPLRBoolean = true;
					}

					if(shortTermPeriod <= 0)
					{
						ActionError error=new ActionError("errors.required","Short Term Period greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						shortTermPeriodBoolean = true;
					}

					if(mediumTermPeriod <= 0)
					{
						ActionError error=new ActionError("errors.required","Medium Term Period greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						mediumTermPeriodBoolean = true;
					}

					if(longTermPeriod <= 0)
					{
						ActionError error=new ActionError("errors.required","Long Term Period greater then 0");
						errors.add(ActionErrors.GLOBAL_ERROR,error);
						longTermPeriodBoolean = true;
					}
					// String plrType = plrmaster.getPLR();
					// Log.log(Log.INFO,"AdministrationActionForm","validate"," plrType :" + plrType);
					// String bplr = plrmaster.getBPLR();
					// Log.log(Log.INFO,"AdministrationActionForm","validate"," bplr :" + bplr);

					if((plrType != null) && (plrType.equals("B")))
					{
						if(bplr <= 0)
						{
							ActionError error=new ActionError("errors.required","Invalid Benchmark PLR value");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
							validPLRType = true;
						}
					}
					if((plrType != null) && (plrType.equals("T")))
					{
						if(bplr >= 0)
						{
							ActionError error=new ActionError("errors.required","Benchmark PLR not required for Tenure PLR.");
							errors.add(ActionErrors.GLOBAL_ERROR,error);
							validPLRType = true;
						}
					}

					if(fromDate && toDate
					&& shortTermPLRBoolean && mediumTermPLRBoolean && longTermPLRBoolean&& shortTermPeriodBoolean && mediumTermPeriodBoolean && longTermPeriodBoolean)
					{
						break;
					}
				}

			}

		}
		return errors;
	}
	/**
	 * @return
	 */
	public PLRMaster getPlrMaster() {
		return plrMaster;
	}

	/**
	 * @param master
	 */
	public void setPlrMaster(PLRMaster master) {
		plrMaster = master;
	}
}

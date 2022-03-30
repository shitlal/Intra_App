/*************************************************************
   *
   * Name of the class: Validator.java
   * This class is created to write the custom validations.
   *
   *
   * @author : Veldurai T
   * @version:
   * @since: Nov 4, 2003
   ***************************************************************/
package com.cgtsi.util;

import java.io.File;
import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorUtil;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.DynaValidatorActionForm;
import org.apache.struts.validator.Resources;

import com.cgtsi.actionform.APForm;
import com.cgtsi.actionform.AdministrationActionForm;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.actionform.InvestmentForm;
import com.cgtsi.actionform.RPActionForm;
import com.cgtsi.actionform.ReportActionForm;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.MenuOptions_back;
import com.cgtsi.admin.PLRMaster;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.Privileges;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.application.NoApplicationFoundException;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimDetail;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.guaranteemaintenance.GMDAO;
import com.cgtsi.guaranteemaintenance.RecoveryProcedureTemp;
import com.cgtsi.investmentfund.IFProcessor;
import com.cgtsi.investmentfund.TransactionDetail;
import com.cgtsi.inwardoutward.IOProcessor;
import com.cgtsi.inwardoutward.Inward;
import com.cgtsi.reports.QueryBuilderFields;
import com.cgtsi.risk.RiskManagementProcessor;

/**
 * @author Veldurai T
 *
 * This class is created to write the custom validations.
 *
 */
public class Validator implements Serializable
{
	private Validator()
	{
	}



            
	public static boolean validateFileFormat(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;		
		FormFile file = (FormFile)dynaForm.get("bankStatementUploadFile");		

		if(file!=null)
		{
			if(!file.getFileName().endsWith(".xml"))
			{
				ActionError actionError  = new ActionError("enterXmlFile");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}


	public static boolean validateForBank(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;
		String flag = (String)dynaForm.get("ifChequed");

		if(flag.equals("Y"))
		{
			String bankName = (String)dynaForm.get("bankName");
			if((bankName == null) || (bankName.equals("")))
			{
				ActionError actionError  = new ActionError("enterBankName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}


	public static boolean validateForChequeNumber(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;
		String flag = (String)dynaForm.get("ifChequed");

		if(flag.equals("Y"))
		{
			String chequeNumber = (String)dynaForm.get("chequeNumber");
			if((chequeNumber == null) || (chequeNumber.equals("")))
			{
				ActionError actionError  = new ActionError("enterChequeNumber");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}

		}
		return errors.isEmpty();
	}


	public static boolean validateForChequeDate(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;
		String flag = (String)dynaForm.get("ifChequed");

		if(flag.equals("Y"))
		{
			java.util.Date chequeDate = (Date)dynaForm.get("chequeDate");
			if((chequeDate == null) || (chequeDate.toString().equals("")))
			{
				ActionError actionError  = new ActionError("enterChequeDate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}

		}
		return errors.isEmpty();
	}

	public static boolean validateForChequeIssued(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;
		String flag = (String)dynaForm.get("ifChequed");

		if(flag.equals("Y"))
		{

			String chequeIssuedTo = (String)dynaForm.get("chequeIssuedTo");
			if((chequeIssuedTo == null) || (chequeIssuedTo.equals("")))
			{
				ActionError actionError  = new ActionError("enterChequeIssuedTo");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}


	public static boolean checkValueEntryForCheque(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{

	//	System.out.println("checkValueEntryForCheque");
		DynaActionForm dynaForm  = (DynaActionForm)bean;
		String flag = (String)dynaForm.get("ifChequed");

		if(flag.equals("Y"))
		{
			String doubleFieldName=field.getProperty();
			java.lang.Double doubleFieldVal=new Double(ValidatorUtil.getValueAsString(bean, doubleFieldName));
			double doubleFieldValue=doubleFieldVal.doubleValue();
			if (doubleFieldValue==0)
			{
				ActionError error=new ActionError(doubleFieldName + "required");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if (doubleFieldValue==0.0)
			{
				ActionError error=new ActionError(doubleFieldName + "required");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
		}
		 return errors.isEmpty();
	}

	public static boolean checkSubScheme(Object bean, ValidatorAction validAction,
													Field field, ActionErrors errors, HttpServletRequest request) throws DatabaseException
	{
		  RiskManagementProcessor riskManagementProcessor = new RiskManagementProcessor();
		  DynaActionForm dynaForm  = (DynaActionForm)bean;
		  ArrayList danRaisedArray = new ArrayList();
		  String subScheme = (String)dynaForm.get("subScheme");
		  danRaisedArray = riskManagementProcessor.getAllSubSchemeNames();

		  if(danRaisedArray.contains(subScheme))
		  {
			ActionError actionError  = new ActionError("enterDifferentname");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		  }
		  return errors.isEmpty();
	}

	public static boolean differentBanks(Object bean, ValidatorAction validAction,
													Field field, ActionErrors errors, HttpServletRequest request) throws DatabaseException
	{
		  DynaActionForm dynaForm  = (DynaActionForm)bean;
		  String fromBank = (String)dynaForm.get("bankName");
		  String toBank = (String)dynaForm.get("toBankName");

		  if ((fromBank.equals(toBank)))
		  {
				ActionError actionError  = new ActionError("enterDifferentBanks");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		  }
		  return errors.isEmpty();
	}

	/**
	 * This method checks whether atleast one privilege is selected or not.
	 * It is mandatory that one privilege should be selected while adding a role,
	 * modifying an existing role etc.
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean checkPrivilegesSelected(Object bean,ValidatorAction validAction,
						   Field field,ActionErrors errors,HttpServletRequest request)
	{

	   DynaActionForm dynaForm=(DynaActionForm)bean;



	   Set privilegeKeys=Privileges.getKeys();
	   Iterator iterator=privilegeKeys.iterator();
	   boolean isPrivilegeAvl=false;
	   while(iterator.hasNext())
	   {
	   		String key=(String)iterator.next();
	   		if(dynaForm.get(key)!=null && !dynaForm.get(key).equals(""))
	   		{
				isPrivilegeAvl=true;
	   			break;
	   		}
	   }

	   //If first and second names are equal add the action error
	   if(!isPrivilegeAvl)
	   {
	   		ActionError actionMessage=new ActionError("PrivilegeRequired");
		   errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
	   }

	   return errors.isEmpty();
   }


   /**
	* This method checks whether atleast one privilege is selected or not.
	* It is mandatory that one privilege should be selected while assigning/modifyig
	* roles and privileges for the selected user.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/
   public static boolean checkRolesAndPrivilegesSelected(Object bean,ValidatorAction validAction,
						  Field field,ActionErrors errors,HttpServletRequest request)
   {

		Log.log(Log.INFO,"Validator","checkRolesAndPrivilegesSelected","Entered");

	  AdministrationActionForm adminForm=(AdministrationActionForm)bean;

	  Set privilegeKeys=Privileges.getKeys();
	  Iterator iterator=privilegeKeys.iterator();
	  boolean isPrivilegeAvl=false;

	  Map privileges=adminForm.getPrivileges();
	  //System.out.println("Executing checkRolesAndPrivilegesSelected");
	  while(iterator.hasNext())
	  {
		   String key=(String)iterator.next();
		   if(privileges.get(key)!=null && !privileges.get(key).equals(""))
		   {
			   isPrivilegeAvl=true;
			   break;
		   }
	  }

	  //If first and second names are equal add the action error
	  if(!isPrivilegeAvl)
	  {
			//System.out.println("Errors are found");
		Log.log(Log.DEBUG,"Validator","checkRolesAndPrivilegesSelected","Found Errors...");
		ActionError actionMessage=new ActionError("PrivilegeRequired");
		errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
	  }

		Log.log(Log.INFO,"Validator","checkRolesAndPrivilegesSelected","Exited");

	  	return errors.isEmpty();
  }

   public static boolean checkNewAndConfirmPassword(Object bean,ValidatorAction validAction,
						  Field field,ActionErrors errors,HttpServletRequest request)
   {

		DynaActionForm dynaForm=(DynaActionForm)bean;

		String newPassword=(String)dynaForm.get("newPassword");
		String oldPassword=(String)dynaForm.get("oldPassword");
		String confirm=(String)dynaForm.get("confirm");

		if(!newPassword.equals(confirm))
		{
			dynaForm.set("oldPassword","");
			dynaForm.set("confirm","");
			dynaForm.set("newPassword","");
			ActionError actionMessage=new ActionError("passwordNotSame");
		 	errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);

			return errors.isEmpty();
		}

		if(oldPassword.equalsIgnoreCase(newPassword))
		{
			dynaForm.set("oldPassword","");
			dynaForm.set("confirm","");
			dynaForm.set("newPassword","");

			if(newPassword==null || newPassword.equals("") ||
				oldPassword==null || oldPassword.equals("") ||
				oldPassword==null || oldPassword.equals(""))
			{
				return errors.isEmpty();
			}
			ActionError actionMessage=new ActionError("oldAndNewPasswordsSame");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		}

		return errors.isEmpty();
  }

	/**
	 * This method checks whether the From Date is before the To Date
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateFromToDates(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String sProperty2=field.getVarValue("toDate");
		String toValue=ValidatorUtil.getValueAsString(bean, sProperty2);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		boolean fromDateValue = false;
		boolean toDateValue = false;

		if (!(GenericValidator.isBlankOrNull(fromValue)) && !(GenericValidator.isBlankOrNull(toValue)))
		{
			try{
				java.util.Date fromDate = (java.util.Date)sdf.parse(fromValue,new ParsePosition(0));
				if(fromDate==null)
				{
					fromDateValue = false;
				}
				else{

					fromDateValue = true;
				}

			}
			catch(Exception e)
			{
				fromDateValue = false;
			}

			try{

				java.util.Date toDate = (java.util.Date)sdf.parse(toValue,new ParsePosition(0));
				if(toDate==null)
				{
					toDateValue = false;
				}
				else{

					toDateValue = true;
				}

			}
			catch(Exception e)
			{
				toDateValue = false;
			}

			if(fromDateValue && toDateValue)
			{
					if (! DateHelper.day1BeforeDay2(fromValue, toValue))
					{
						ActionError actionMessage=new ActionError("fromDateGT" + sProperty2);
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}
			}

		}



		return errors.isEmpty();
	}

/**
   * This function is used for validate the Sanctioned date
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   */

	public static boolean validateSanctionedDates(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
    
    DynaActionForm dynaForm=(DynaActionForm)bean;
    
  //  String termCreditSanctioned = (String)request.getParameter("termCreditSanctioned");
  //  double sanctionedamount= Double.parseDouble(termCreditSanctioned);
  //  System.out.println("termCreditSanctioned:"+sanctionedamount);
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
   
    String toValue = null;
     /* added by sukumar@path 27-01-2009 for 
      Proposals approved by the MLIs on or after December 8, 2008 will be eligible for the coverage upto Rs.100 lakh */
      String dateString = "08/12/2008"; 
    // System.out.println("from value:"+fromValue);
    Date dt = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dt);
     int year = calendar.get(Calendar.YEAR)  ;
		// System.out.println("Year:"+year);
		 int month = calendar.get(Calendar.MONTH) ;
	//	 System.out.println("Month:"+month);
		 int day = calendar.get(Calendar.DATE) ;
   //  System.out.println("Day:"+day);
		
    if(month >=0 && month <=2){
  //  System.out.println("test1");
    year=year-1;
    calendar.set(Calendar.MONTH,6);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year);
    }
    else if(month >= 3 && month <=5 ){
   // System.out.println("test2");
    year=year-1;
    calendar.set(Calendar.MONTH,9);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    }
    else if(month >=6 && month <=8){
  //  System.out.println("test3");
    calendar.set(Calendar.MONTH,0);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    }
    else if(month >=9 && month <=11){
  //  System.out.println("test4");
   // calendar.set(Calendar.MONTH,3);
    calendar.set(Calendar.MONTH,3);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    }
  //  System.out.println("Result Date:"+calendar.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
   
		boolean fromDateValue = false;
		boolean toDateValue = false;

		if (!(GenericValidator.isBlankOrNull(fromValue)))
		{
       
			try{
				java.util.Date fromDate = (java.util.Date)sdf.parse(fromValue,new ParsePosition(0));
       // System.out.println("From Date:"+fromDate);
              
				if(fromDate==null)
				{
					fromDateValue = false;
				}
				else{

					fromDateValue = true;
				}

			}
			catch(Exception e)
			{
				fromDateValue = false;
			}

			try{
        java.util.Date toDate = calendar.getTime() ;
      //  System.out.println("To Date:"+toDate);
        toValue = sdf.format(toDate);
				if(toDate==null)
				{
					toDateValue = false;
				}
				else{

					toDateValue = true;
				}

			}
			catch(Exception e)
			{
				toDateValue = false;
			}

			if(fromDateValue && toDateValue)
			{
          if( DateHelper.day1BeforeDay2(fromValue,toValue)){
            // System.out.println("Final");
            ActionError actionMessage=new ActionError("amountSanctionedDate"+" cannot be before " + toValue);
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
          }
          
				/*	if (! DateHelper.day1BeforeDay2(fromValue, toValue))
					{
						ActionError actionMessage=new ActionError("fromDateGT" + toValue);
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					} */
			}
   /*   if(sanctionedamount > 5000000.0){
    //  System.out.println("Final");
    //  System.out.println("FromValue"+fromValue);
   //   System.out.println("Date String:"+dateString);
       if( DateHelper.day1BeforeDay2(fromValue,dateString)){
            
            ActionError actionMessage=new ActionError("Proposals approved by the MLIs on or after December 8, 2008 will be eligible for the coverage upto Rs.100 lakh (Ref: Circular 50/2008-09)");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
          }
      } */

		}



		return errors.isEmpty();
	}

	/**
	 * This method is used to validate the user limit screen.
	 *
	 * This method checks whether atleast either of application approval limit
	 * or claim approval limit is entered.
	 * An error is added if neither of the 2 is entered.
	 *
	 * If the limit is entered, the from date and to date is validated.
	 * An error is added if the limit is entered but the dates are not valid.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateUserLimits(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		String appLimitValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String sProperty2=field.getVarValue("claimLimitValue");
		String claimLimitValue=ValidatorUtil.getValueAsString(bean, sProperty2);

		String sAppFrom=field.getVarValue("appFromDate");
		String sAppTo=field.getVarValue("appToDate");
		String appFromDate=ValidatorUtil.getValueAsString(bean, sAppFrom);
		String appToDate=ValidatorUtil.getValueAsString(bean, sAppTo);

		String sClaimFrom=field.getVarValue("claimFromDate");
		String sClaimTo=field.getVarValue("claimToDate");
		String claimFromDate=ValidatorUtil.getValueAsString(bean, sClaimFrom);
		String claimToDate=ValidatorUtil.getValueAsString(bean, sClaimTo);

		boolean appFromValue = false;
		boolean appToValue = false;

		boolean claimFromValue = false;
		boolean claimToValue = false;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		/**
		 * Checks if the application limit and claim limit is blank or null.
		 * If true, an error is added.
		 */
		 if ((! GenericValidator.isBlankOrNull(appLimitValue) && Double.parseDouble(appLimitValue) == 0.0))
		 {
			 appLimitValue="";
		 }
		 if ((! GenericValidator.isBlankOrNull(appFromDate) || ! GenericValidator.isBlankOrNull(appToDate)) &&
			 GenericValidator.isBlankOrNull(appLimitValue))
		 {
			ActionError actionMessage=new ActionError("appLimitRequired");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		 }
		 if (! GenericValidator.isBlankOrNull(claimLimitValue) && Double.parseDouble(claimLimitValue) == 0.0)
		 {
			 claimLimitValue="";
		 }
		 if ((! GenericValidator.isBlankOrNull(claimFromDate) || ! GenericValidator.isBlankOrNull(claimToDate)) &&
			 GenericValidator.isBlankOrNull(claimLimitValue))
		 {
			ActionError actionMessage=new ActionError("claimLimitRequired");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		 }
		if ((GenericValidator.isBlankOrNull(appLimitValue)) && (GenericValidator.isBlankOrNull(claimLimitValue)))
		{
			ActionError actionMessage=new ActionError("appOrClaimLimit");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		}
		/**
		 * If the application limit is entered, the from date and to date are validated.
		 * If from date or to date is blank or null, an error is added.
		 * If from date is after or same as the to date, an error is added.
		 */
		if (!GenericValidator.isBlankOrNull(appLimitValue))
		{
			if (GenericValidator.isBlankOrNull(appFromDate))
			{
				ActionError actionMessage=new ActionError("appFromDate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}

/*			if (GenericValidator.isBlankOrNull(appToDate))
			{
				ActionError actionMessage=new ActionError("appToDate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}*/

			if (!(GenericValidator.isBlankOrNull(appFromDate)) && !(GenericValidator.isBlankOrNull(appToDate)))
			{
				try{
						java.util.Date appValidFromDate = (java.util.Date)sdf.parse(appFromDate,new ParsePosition(0));

						if(appValidFromDate==null)
						{
							appFromValue = false;

						}
						else{

							appFromValue = true;
						}
				}
					catch(Exception n)
					{

						appFromValue = false;
					}

				try{
					java.util.Date appValidToDate = (java.util.Date)sdf.parse(appToDate,new ParsePosition(0));
					if(appValidToDate==null)
					{
						appToValue = false;

					}
					else{

						appToValue = true;
					}


				}
				catch(Exception n)
				{
					appToValue = false;
				}

				if(appFromValue && appToValue)
				{
					if (! DateHelper.day1BeforeDay2(appFromDate, appToDate))
					{
						ActionError actionMessage=new ActionError("appFromGTToDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}

				}
			}
		}
		/**
		 * If the claim limit is entered, the from date and to date are validated.
		 * If from date or to date is blank or null, an error is added.
		 * If from date is after or same as the to date, an error is added.
		 */
		if (!GenericValidator.isBlankOrNull(claimLimitValue))
		{
			if (GenericValidator.isBlankOrNull(claimFromDate))
			{
				ActionError actionMessage=new ActionError("claimFromDate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}

/*			if (GenericValidator.isBlankOrNull(claimToDate))
			{
				ActionError actionMessage=new ActionError("claimToDate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}*/

			if (!(GenericValidator.isBlankOrNull(claimFromDate)) && !(GenericValidator.isBlankOrNull(claimToDate)))
			{
				try{
						java.util.Date claimValidFromDate = (java.util.Date)sdf.parse(claimFromDate,new ParsePosition(0));

						if(claimValidFromDate==null)
						{
							claimFromValue = false;

						}
						else{

							claimFromValue = true;
						}
				}
					catch(Exception n)
					{

						claimFromValue = false;
					}

				try{
					java.util.Date claimValidToDate = (java.util.Date)sdf.parse(claimToDate,new ParsePosition(0));
					if(claimValidToDate==null)
					{
						claimToValue = false;

					}
					else{

						claimToValue = true;
					}


				}
				catch(Exception n)
				{
					claimToValue = false;
				}

				if(claimFromValue && claimToValue)
				{
					if (! DateHelper.day1BeforeDay2(claimFromDate, claimToDate))
					{
						ActionError actionMessage=new ActionError("claimFromGTToDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}

				}

			}
		}

		return errors.isEmpty();
	}


  public static boolean checkSFCalculationDate(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
  {
		Log.log(Log.INFO,"Validator","checkSFCalculationDate","Entered");

		DynaActionForm dynaForm=(DynaActionForm)bean;

		Integer day=(Integer)dynaForm.get("serviceFeeCalculationDay");


		String month=(String)dynaForm.get("serviceFeeCalculationMonth");

		Log.log(Log.DEBUG,"Validator","checkSFCalculationDate","Day and Months are "+day+" "+month);

		if(day==null || day.equals("0"))
		{
			return false;
		}
		int dayInt=day.intValue();

		if(dayInt==0)
		{
			return false;
		}

		Log.log(Log.DEBUG,"Validator","checkSFCalculationDate","Day integer is "+dayInt);

		boolean isAvl=DateHelp.isDayAvlInMonth(dayInt,month);

		if(!isAvl)
		{
			Log.log(Log.DEBUG,"Validator","checkSFCalculationDate","Day is not available in Month");

			ActionError actionError=new ActionError("serviceDayAndMonth");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		Log.log(Log.INFO,"Validator","checkSFCalculationDate","Exited");

	  	return errors.isEmpty();
  }

   public static boolean checkIdType(Object bean,ValidatorAction validAction,
							 Field field,ActionErrors errors,HttpServletRequest request)
	 {

			Log.log(Log.INFO,"Validator","checkIdType","Entered");

	 		DynaActionForm dynaForm=(DynaActionForm)bean;

			String idType=(String)dynaForm.get("idType");
			String idOther=(String)dynaForm.get("idTypeOther");

			Log.log(Log.DEBUG,"Validator","checkIdType","idType,idOther "+idType+" "+idOther);

			if(!idType.equals("none") && (idOther==null || idOther.equals("")))
			{
				Log.log(Log.DEBUG,"Validator","checkIdType","Id other is not entered.");

				ActionError error=null;

				if(idType.equals("cgbid"))
				{
					Log.log(Log.DEBUG,"Validator","checkIdType","CGBID");
					error=new ActionError("cgbidRequired");
				}
				else
				{
					Log.log(Log.DEBUG,"Validator","checkIdType","CGPAN");
					error=new ActionError("cgpanRequired");
				}

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}

			Log.log(Log.INFO,"Validator","checkIdType","Exited");

	 		return errors.isEmpty();
	 }

	public static boolean checkConstitution(Object bean,ValidatorAction validAction,
						   Field field,ActionErrors errors,HttpServletRequest request)
   {

		  Log.log(Log.INFO,"Validator","checkConstitution","Entered");

		  DynaActionForm dynaForm=(DynaActionForm)bean;

		  String constituition=(String)dynaForm.get("constitution");
		  String constituitionOther=(String)dynaForm.get("constitutionOther");

		  if(constituition==null || constituition.equals("") || constituition.equals("Others"))
		  {
			  Log.log(Log.DEBUG,"Validator","checkConstitution","constituition is not selected.");

			  if(constituitionOther==null || constituitionOther.equals(""))
			  {
				  	Log.log(Log.DEBUG,"Validator","checkConstitution","constituitionOther is not entered");

					ActionError error=new ActionError("constituitionRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,error);
			  }
		  }

		  Log.log(Log.INFO,"Validator","checkConstitution","Exited");

		  return errors.isEmpty();
   }
   public static boolean checkDistrict(Object bean,ValidatorAction validAction,
						  Field field,ActionErrors errors,HttpServletRequest request)
  {

		 Log.log(Log.INFO,"Validator","checkDistrict","Entered");

		 DynaActionForm dynaForm=(DynaActionForm)bean;

		 String district=(String)dynaForm.get("district");
		 String districtOther=(String)dynaForm.get("districtOthers");

		 if(district==null || district.equals("") || district.equals("Others"))
		 {
			 Log.log(Log.DEBUG,"Validator","checkDistrict","Distict is not selected.");

			 if(districtOther==null || districtOther.equals(""))
			 {
				   Log.log(Log.DEBUG,"Validator","checkDistrict","Distict Other is not entered");

				   ActionError error=new ActionError("districtRequired");

				   errors.add(ActionErrors.GLOBAL_ERROR,error);
			 }
		 }

		 Log.log(Log.INFO,"Validator","checkDistrict","Exited");

		 return errors.isEmpty();
  }
  public static boolean checkLegalId(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
 {

		Log.log(Log.INFO,"Validator","checkLegalId","Entered");

		DynaActionForm dynaForm=(DynaActionForm)bean;

		String legalId=(String)dynaForm.get("cpLegalID");
		String legalIdOther=(String)dynaForm.get("otherCpLegalID");

		String legalIdValue=(String)dynaForm.get("cpLegalIdValue");

		if(legalId!=null && !legalId.equals("") && legalId.equals("Others"))
		{
			Log.log(Log.DEBUG,"Validator","checkLegalId","Legal Id is not selected.");

			if(legalIdOther==null || legalIdOther.equals(""))
			{
				  Log.log(Log.DEBUG,"Validator","checkLegalId","Legal Id Other is not entered");

				  ActionError error=new ActionError("legalIdRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			else if(legalIdOther!=null || !legalIdOther.equals(""))
			{
				if(legalIdValue==null || legalIdValue.equals(""))
				{
					ActionError error=new ActionError("errors.required","Legal Id Value");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
			}
		}
		else if(legalId!=null && !legalId.equals("")&& !legalId.equals("Others"))
		{			
			if(legalIdValue==null || legalIdValue.equals(""))
			{
				ActionError error=new ActionError("errors.required","Legal Id Value");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

		}

		Log.log(Log.INFO,"Validator","checkLegalId","Exited");

		return errors.isEmpty();
 }

    public static boolean checkSubsidyName(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
    {
        Log.log(4, "Validator", "checkSubsidyName", "Entered");
        DynaActionForm dynaForm = (DynaActionForm)bean;
        String subsidyName = (String)dynaForm.get("subsidyName");
        String subsidyNameOther = (String)dynaForm.get("otherSubsidyEquityName");
    //    if((subsidyName == null || subsidyName.equals("")) && subsidyName.equals("Others"))
        if(!GenericValidator.isBlankOrNull(subsidyName) && "Others".equals(subsidyName))
        {
            Log.log(4, "Validator", "checkSubsidyName", "Subsidy Name is not selected.");
    //         if(subsidyNameOther == null || subsidyNameOther.equals(""))
            if(GenericValidator.isBlankOrNull(subsidyNameOther))
            {
                Log.log(4, "Validator", "checkSubsidyName", "Subsidy Name is not entered");
                ActionError error = new ActionError("subsidyNamerequired");
                errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
            }
        }
        Log.log(4, "Validator", "checkSubsidyName", "Exited");
        return errors.isEmpty();
    }


  /**
 * This method checks whether the worth has been entered if name has been entered
 */

	public static boolean checkWorthForName(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		String name=field.getProperty();

		String fieldName=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String fieldWorth=field.getVarValue("worth");
		java.lang.Double doubleWorth=new Double(ValidatorUtil.getValueAsString(bean, fieldWorth));
		double nameWorth=doubleWorth.doubleValue();

		if ((fieldName!=null)&&(!(fieldName.equals(""))))
		{
			if (nameWorth==0)
			{
				Log.log(Log.DEBUG,"Validator","checkWorthForName",fieldWorth + "for" + fieldName);

				  ActionError error=new ActionError(fieldWorth);

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
		}
		else if ((fieldName==null)||(fieldName.equals("")))
		{
			if (nameWorth!=0)
			{
				 ActionError error=new ActionError(name);

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
		}
		return errors.isEmpty();

	}

/*
	* This method checks whether more than one field has been entered in the modify application or not
	*/

	public static boolean checkMoreField(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		String mliID=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String cgbid=field.getVarValue("cgbid");
		String cgbidVal=ValidatorUtil.getValueAsString(bean, cgbid);
		String cgpan=field.getVarValue("cgpan");
		String cgpanVal=ValidatorUtil.getValueAsString(bean, cgpan);
		String applicationRefNo=field.getVarValue("applicationRefNo");
		String applicationRefNoVal=ValidatorUtil.getValueAsString(bean, applicationRefNo);
		String borrowerName=field.getVarValue("borrowerName");
		String borrowerNameVal=ValidatorUtil.getValueAsString(bean, borrowerName);

		if (!(GenericValidator.isBlankOrNull(mliID)))
		{
			if ((GenericValidator.isBlankOrNull(cgbidVal)) && (GenericValidator.isBlankOrNull(cgpanVal)) &&
				(GenericValidator.isBlankOrNull(applicationRefNoVal)) && (GenericValidator.isBlankOrNull(borrowerNameVal)))
			{
				 ActionError error=new ActionError("oneFieldRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if ((!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal))) ||
					 (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(borrowerNameVal))) ||
					 (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(cgpanVal))) ||
					 (!(GenericValidator.isBlankOrNull(applicationRefNoVal)) && !(GenericValidator.isBlankOrNull(borrowerNameVal))) ||
					 (!(GenericValidator.isBlankOrNull(applicationRefNoVal)) && !(GenericValidator.isBlankOrNull(cgpanVal))) ||
					 (!(GenericValidator.isBlankOrNull(cgpanVal)) && !(GenericValidator.isBlankOrNull(borrowerNameVal)))  ||
					 (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal)) &&!(GenericValidator.isBlankOrNull(borrowerNameVal))) ||
					 (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(cgpanVal)) &&!(GenericValidator.isBlankOrNull(borrowerNameVal))) ||
					 (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal)) &&!(GenericValidator.isBlankOrNull(cgpanVal))) ||
					 (!(GenericValidator.isBlankOrNull(borrowerNameVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal)) &&!(GenericValidator.isBlankOrNull(cgpanVal))) ||
					 (!(GenericValidator.isBlankOrNull(borrowerNameVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal)) &&!(GenericValidator.isBlankOrNull(cgpanVal)) && !(GenericValidator.isBlankOrNull(borrowerNameVal)) && !(GenericValidator.isBlankOrNull(applicationRefNoVal)) &&!(GenericValidator.isBlankOrNull(cgpanVal))))
			{
				ActionError error=new ActionError("oneFieldRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}
				return errors.isEmpty();

	}

	/**
	* This method checks if any one of the other fields other than MLI ID are entered
	*/
	public static boolean checkAnyOneField(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		String mliID=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String cgbid=field.getVarValue("cgbid");
		String cgbidVal=ValidatorUtil.getValueAsString(bean, cgbid);
		String cgpan=field.getVarValue("cgpan");
		String cgpanVal=ValidatorUtil.getValueAsString(bean, cgpan);

		if (!(GenericValidator.isBlankOrNull(mliID)))
		{
			if ((GenericValidator.isBlankOrNull(cgbidVal)) && (GenericValidator.isBlankOrNull(cgpanVal)))
			{
				 ActionError error=new ActionError("anyOneFieldRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if (!(GenericValidator.isBlankOrNull(cgbidVal)) && !(GenericValidator.isBlankOrNull(cgpanVal)))
			{
				ActionError error=new ActionError("anyOneFieldRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
		}

		return errors.isEmpty();
	}

	/*
	* This method checks the entry of sanctioned fields
	*/

	public static boolean checkEntryField(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		String loanType=dynaForm.get("loanType").toString();

		//Interest
		java.lang.Double fundBasedInterestVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double fundBasedInt=fundBasedInterestVal.doubleValue();

		String fundBasedDate= field.getVarValue("fundBasedSanctionedDate");
		String fundBasedDateVal=ValidatorUtil.getValueAsString(bean,fundBasedDate);

		String wcFundBasedSanctionedVal= field.getVarValue("fundBasedSanctioned");
		java.lang.Double wcFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcFundBasedSanctionedVal));
		double wcFundBasedSanctionedValue=wcFundBasedSanctioned.doubleValue();

		String creditFundBasedVal= field.getVarValue("creditFundBased");
		java.lang.Double creditFundBased=new Double(ValidatorUtil.getValueAsString(bean, creditFundBasedVal));
		double creditFundBasedValue=creditFundBased.doubleValue();

		String creditNonFundBasedVal= field.getVarValue("creditNonFundBased");
		java.lang.Double creditNonFundBased=new Double(ValidatorUtil.getValueAsString(bean, creditNonFundBasedVal));
		double creditNonFundBasedValue=creditNonFundBased.doubleValue();

		String nonFundBasedDate= field.getVarValue("nonFundBasedSanctionedDate");
		String nonFundBasedDateVal=ValidatorUtil.getValueAsString(bean,nonFundBasedDate);

		String wcNonFundBasedSanctionedVal= field.getVarValue("nonfundBasedSanctioned");
		java.lang.Double wcNonFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcNonFundBasedSanctionedVal));
		double wcNonFundBasedSanctionedValue=wcNonFundBasedSanctioned.doubleValue();

		String wcFundBasedCommissionVal= field.getVarValue("nonFundBasedCommission");
		java.lang.Double wcFundBasedCommission=new Double(ValidatorUtil.getValueAsString(bean, wcFundBasedCommissionVal));
		double wcFundBasedCommissionValue=wcFundBasedCommission.doubleValue();

		if (wcFundBasedSanctionedValue!=0)
		{
			if (fundBasedInt==0)
			{
				 ActionError error=new ActionError("interestRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if (fundBasedDateVal.equals(""))
			{
				ActionError error=new ActionError("errors.required","Date of Sanction For Fund Based");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if(creditFundBasedValue==0)
			{
				ActionError error=new ActionError("errors.required","Credit to be Guaranteed Fund Based Value ");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

		}
		else
		{
			if ((fundBasedInt!=0)|| (!(fundBasedDateVal.equals(""))))
			{
				if (wcFundBasedSanctionedValue==0)
				{
					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("fundBasedRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("fundBasedRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

/*					 ActionError error=new ActionError("fundBasedRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
*/
				}
			}
			if(creditFundBasedValue!=0 && wcNonFundBasedSanctionedValue!=0)
			{
				if (wcFundBasedSanctionedValue==0)
				{
					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("fundBasedRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("fundBasedRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

/*					 ActionError error=new ActionError("fundBasedRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
*/
				}

			}
		}

		if (wcNonFundBasedSanctionedValue!=0)
		{
			if (wcFundBasedCommissionValue==0)
			{
				 ActionError error=new ActionError("commissionRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if (nonFundBasedDateVal.equals(""))
			{
				ActionError error=new ActionError("sanctionedDateNfbRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if(creditNonFundBasedValue==0)
			{
				ActionError error=new ActionError("errors.required","Credit Non Fund Based Value");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}
		else
		{
			if ((wcFundBasedCommissionValue!=0)|| (!(nonFundBasedDateVal.equals(""))))
			{
				if (wcNonFundBasedSanctionedValue==0)
				{
					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("nonFundBasedRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("nonFundBasedRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

					/*
					 ActionError error=new ActionError("nonFundBasedRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
*/
				}

			}
			if(creditNonFundBasedValue!=0)
			{
				if (wcNonFundBasedSanctionedValue==0 && wcFundBasedSanctionedValue!=0)
				{
					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("nonFundBasedRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("nonFundBasedRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

				}

			}

		}
		return errors.isEmpty();
	}

	/*
	* This method checks the validation for the credit to be guaranteed field
	*

public static boolean checkCreditField(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		java.lang.Double creditFundBasedVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double creditFundBased=creditFundBasedVal.doubleValue();

		String wcFundBasedSanctionedVal= field.getVarValue("fundBasedSanctioned");
		java.lang.Double wcFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcFundBasedSanctionedVal));
		double wcFundBasedSanctionedValue=wcFundBasedSanctioned.doubleValue();

/*		String wcNonFundBasedSanctionedVal= field.getVarValue("nonfundBasedSanctioned");
		java.lang.Double wcNonFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcNonFundBasedSanctionedVal));
		double wcNonFundBasedSanctionedValue=wcNonFundBasedSanctioned.doubleValue();

		String creditNFBVal= field.getVarValue("creditNFB");
		java.lang.Double creditNFB=new Double(ValidatorUtil.getValueAsString(bean, creditNFBVal));
		double creditNFBValue=creditNFB.doubleValue();
*

		if (creditFundBased!=0)
		{
			if (wcFundBasedSanctionedValue==0)
			{
				 ActionError error=new ActionError("fundBasedRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);


			}
		}
/*		if (creditNFBValue!=0)
		{
			if (wcNonFundBasedSanctionedValue==0)
			{
				 ActionError error=new ActionError("nonFundBasedRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);


			}
		}*
		return errors.isEmpty();
	}

	/*
	* This method checks the validation for the outstanding fund based and non fund based fields
	*/

public static boolean checkOsField(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{

		java.lang.Double osFundBasedPplVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double osFundBasedPpl=osFundBasedPplVal.doubleValue();

		/*String osFBInterestAmtVal= field.getVarValue("osFBInterestAmt");
		java.lang.Double osFBInterestAmt=new Double(ValidatorUtil.getValueAsString(bean, osFBInterestAmtVal));
		double osFBInterestAmtValue=osFBInterestAmt.doubleValue();*/

		String osFBAsOnDateVal= field.getVarValue("osFBAsOnDate");
		String osFBAsOnDateValue=ValidatorUtil.getValueAsString(bean,osFBAsOnDateVal);

		String osNonFBPplVal= field.getVarValue("osNonFBPpl");
		java.lang.Double osNonFBPpl=new Double(ValidatorUtil.getValueAsString(bean, osNonFBPplVal));
		double osNonFBPplValue=osNonFBPpl.doubleValue();

/*		String osNFBCommissionAmtVal= field.getVarValue("osNFBCommissionAmt");
		java.lang.Double osNFBCommissionAmt=new Double(ValidatorUtil.getValueAsString(bean, osNFBCommissionAmtVal));
		double osNFBCommissionAmtValue=osNFBCommissionAmt.doubleValue();
*/
		String osNFBAsOnDateVal= field.getVarValue("osNFBAsOnDate");
		String osNFBAsOnDateValue=ValidatorUtil.getValueAsString(bean,osNFBAsOnDateVal);

		String wcFundBasedSanctionedVal= field.getVarValue("fundBasedSanctioned");
		java.lang.Double wcFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcFundBasedSanctionedVal));
		double wcFundBasedSanctionedValue=wcFundBasedSanctioned.doubleValue();

		String wcNonFundBasedSanctionedVal= field.getVarValue("nonfundBasedSanctioned");
		java.lang.Double wcNonFundBasedSanctioned=new Double(ValidatorUtil.getValueAsString(bean, wcNonFundBasedSanctionedVal));
		double wcNonFundBasedSanctionedValue=wcNonFundBasedSanctioned.doubleValue();

		String fbSancDate= field.getVarValue("limitFundBasedSanctionedDate");
		String fbSancDateValue=ValidatorUtil.getValueAsString(bean,fbSancDate);

		String nfbSancDate= field.getVarValue("limitNonFundBasedSanctionedDate");
		String nfbSancDateValue=ValidatorUtil.getValueAsString(bean,nfbSancDate);

		HttpSession session=request.getSession(false);

		if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && (session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("9") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8")
		||((session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("18")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")) && (session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("WC")||
		session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("CC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("BO")))))
		{

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

/*		if (wcFundBasedSanctionedValue!=0)
		{
			if(!session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("6"))
			{
				if (osFundBasedPpl==0)
				{
					ActionError error=new ActionError("osfundbasedrequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);

				}

			}
		}
		if (wcNonFundBasedSanctionedValue!=0)
		{
			if (osNonFBPplValue==0)
			{
				ActionError error=new ActionError("osnonfundbasedrequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}*/
		if (osFundBasedPpl!=0)
		{
			if (wcFundBasedSanctionedValue==0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("fundBasedRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("fundBasedRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				 ActionError error=new ActionError("fundBasedRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);
*/			}
			else if (wcFundBasedSanctionedValue!=0)
			{
				if (osFBAsOnDateValue.equals(""))
				{
					 ActionError error=new ActionError("outFBDate");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
			}

/*			boolean osFundBasedDate = false;
			boolean fbSancDateVal = false;

			if((osFBAsOnDateValue!=null && !osFBAsOnDateValue.equals("")) && (fbSancDateValue!=null && !fbSancDateValue.equals("")))
			{
				osFundBasedDate = true;
				fbSancDateVal = true;

				Date osFBDate=null;

				try{
					osFBDate = (java.util.Date)sdf.parse(osFBAsOnDateValue,new ParsePosition(0));
					if(osFBDate==null)
					{
						osFundBasedDate = false;

					}

				}
				catch(Exception n)
				{
					osFundBasedDate = false;
				}
				Date fbSanctionedDt=null;

				try{
					fbSanctionedDt = (java.util.Date)sdf.parse(fbSancDateValue,new ParsePosition(0));
					if(fbSanctionedDt==null)
					{
						fbSancDateVal = false;

					}

				}
				catch(Exception n)
				{
					fbSancDateVal = false;
				}
				if(osFundBasedDate && fbSancDateVal)
				{
					if(fbSanctionedDt.compareTo(osFBDate)==1)
					{
						ActionError actionMessage=new ActionError("fromDateGTfirstInstalmentDueDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}

				}
			}*/
		}
		else if(osFundBasedPpl==0)
		{
			if (!osFBAsOnDateValue.equals(""))
			{
				 ActionError error=new ActionError("errors.required","Outstanding Fund Based Principal");

				 errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

		}
/*		if(osFBAsOnDateValue!=null && !osFBAsOnDateValue.equals(""))
		{
			if (osFundBasedPpl==0)
			{
				ActionError error=new ActionError("pplOSrequired");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}
*/
		if (osNonFBPplValue!=0)
		{
			if (wcNonFundBasedSanctionedValue==0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("nonFundBasedRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("nonFundBasedRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				/*
				 ActionError error=new ActionError("nonFundBasedRequired");

				  errors.add(ActionErrors.GLOBAL_ERROR,error);*/
			}
			else if (wcNonFundBasedSanctionedValue!=0)
			{
/*				if (osNFBCommissionAmtValue==0)
				{
					 ActionError error=new ActionError("outNFBCommission");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
*/				if (osNFBAsOnDateValue.equals(""))
				{
					 ActionError error=new ActionError("outNFBDate");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);

				}

			}
/*			boolean osNonFundBasedDate = false;
			boolean nfbSancDateVal = false;

			if((osNFBAsOnDateValue!=null && !osNFBAsOnDateValue.equals("")) && (nfbSancDateValue!=null && !nfbSancDateValue.equals("")))
			{
				osNonFundBasedDate = true;
				nfbSancDateVal = true;

				Date osNFBDate=null;

				try{
					osNFBDate = (java.util.Date)sdf.parse(osFBAsOnDateValue,new ParsePosition(0));
					if(osNFBDate==null)
					{
						osNonFundBasedDate = false;

					}

				}
				catch(Exception n)
				{
					osNonFundBasedDate = false;
				}
				Date nfbSanctionedDt=null;

				try{
					nfbSanctionedDt = (java.util.Date)sdf.parse(nfbSancDateValue,new ParsePosition(0));
					if(nfbSanctionedDt==null)
					{
						nfbSancDateVal = false;

					}

				}
				catch(Exception n)
				{
					nfbSancDateVal = false;
				}
				if(nfbSancDateVal && osNonFundBasedDate)
				{
					if(nfbSanctionedDt.compareTo(osNFBDate)==1)
					{
						ActionError actionMessage=new ActionError("fromDateGTfirstInstalmentDueDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}

				}
			}
			*/
		}
		else if(osNonFBPplValue==0)
		{
			if (!osNFBAsOnDateValue.equals(""))
			{
				 ActionError error=new ActionError("errors.required","Outstanding Non Fund Based Principal");

				 errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

		}
		}

		return errors.isEmpty();
	}
	/*
	* This method checks whether the enhanced total is greater than exixting total
	*/
	public static boolean checkEnhancedTotal(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		java.lang.Double existingTotalVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double existingTotalValue=existingTotalVal.doubleValue();

		String enhanceTotalVal= field.getVarValue("enhanceTotal");
		java.lang.Double enhanceTotal=new Double(ValidatorUtil.getValueAsString(bean, enhanceTotalVal));
		double enhanceTotalValue=enhanceTotal.doubleValue();

		 if (existingTotalValue > enhanceTotalValue)
		 {
			ActionError error=new ActionError("enhancedTotalCheck");

			errors.add(ActionErrors.GLOBAL_ERROR,error);

		 }
		 return errors.isEmpty();

	}

	/*
	* This method checks whether the integer or double fields
	*/
	public static boolean checkValueEntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{
		String doubleFieldName=field.getProperty();
		java.lang.Double doubleFieldVal=new Double(ValidatorUtil.getValueAsString(bean, doubleFieldName));
		double doubleFieldValue=doubleFieldVal.doubleValue();
		if (doubleFieldValue==0)
		{
			ActionError error=new ActionError(doubleFieldName + "required");

			errors.add(ActionErrors.GLOBAL_ERROR,error);

		}
		else if (doubleFieldValue==0.0)
		{
			ActionError error=new ActionError(doubleFieldName + "required");

			errors.add(ActionErrors.GLOBAL_ERROR,error);
		}
		 return errors.isEmpty();

	}

	public static boolean checkCoveredValue(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)throws DatabaseException
	{
		String cgtsiCoveredValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String typeValue=field.getVarValue("noneValue");
		String typeVal=ValidatorUtil.getValueAsString(bean, typeValue);
		String unitTypeVal=field.getVarValue("unit");
		String unittypeValue=ValidatorUtil.getValueAsString(bean, unitTypeVal);

		HttpSession session=request.getSession(false);
	                DynaActionForm dynaForm1=(DynaActionForm)bean;
	                String previouslyCovered=(String)dynaForm1.get("previouslyCovered");
	                  if(previouslyCovered==null||previouslyCovered.equals(""))
	                    {
	                    ActionError actionError=new ActionError("previouslyCoveredrequired");
                             errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	                    }
	          

		if (cgtsiCoveredValue.equals("Y"))
		{


			if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") ||
			session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("9") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("10"))
			{

				if (typeVal.equals("cgpan"))
				{
					if ((unittypeValue==null)||(unittypeValue.equals("")))
					{
						ActionError error=new ActionError("cgpanRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
				}else if (typeVal.equals("cgbid"))
				{
					if ((unittypeValue==null)||(unittypeValue.equals("")))
					{
						ActionError error=new ActionError("cgbidRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
				}
				else if (typeVal.equals("none"))
				{
					ActionError error=new ActionError("cgpanCgbidSelected");

					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
			}
			//if 'N' is selected do the validations
		}else if (cgtsiCoveredValue.equals("N") && !(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("3") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("4") ||
		session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("5") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("6") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14")||
		session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("18")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")))
    {

			DynaActionForm dynaForm=(DynaActionForm)bean;
			if(dynaForm.get("constitution")==null || dynaForm.get("constitution").equals("") || dynaForm.get("constitution").equals("Others"))
			{
				if(dynaForm.get("constitutionOther")==null || dynaForm.get("constitutionOther").equals(""))
				{

					  Log.log(Log.DEBUG,"Validator","checkConstitution","constituitionOther is not entered");

					  ActionError error=new ActionError("constituitionRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
				}

			}
			if(dynaForm.get("state")==null || dynaForm.get("state").equals(""))
			{
				ActionError error=new ActionError("errors.required","State");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

			if(dynaForm.get("city")==null || dynaForm.get("city").equals(""))
			{
				ActionError error=new ActionError("errors.required","City");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

			if(dynaForm.get("district")==null || dynaForm.get("district").equals("") || dynaForm.get("district").equals("Others"))
			{
				if(dynaForm.get("districtOthers")==null || dynaForm.get("districtOthers").equals(""))
				{
					  Log.log(Log.DEBUG,"Validator","checkDistrict","Distict Other is not entered");

					  ActionError error=new ActionError("districtRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
				}

			}
			if(dynaForm.get("ssiType")==null || dynaForm.get("ssiType").equals(""))
			{
				ActionError error=new ActionError("errors.required","Unit Type");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("ssiName")==null || dynaForm.get("ssiName").equals(""))
			{
				ActionError error=new ActionError("errors.required","SSI Name");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}    
  
  
    if(dynaForm.get("employeeNos")==null || dynaForm.get("employeeNos").equals(""))
			{
				ActionError error=new ActionError("errors.required","noOfEmployees");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
       if(dynaForm.get("projectedSalesTurnover")==null || dynaForm.get("projectedSalesTurnover").equals(""))
			{
				ActionError error=new ActionError("errors.required","turnover");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
      
      if(dynaForm.get("projectedExports")==null || dynaForm.get("projectedExports").equals(""))
			{
				ActionError error=new ActionError("errors.required","exports");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
      
      
      
      
/*			if(dynaForm.get("regNo")==null || dynaForm.get("regNo").equals(""))
			{
				ActionError error=new ActionError("errors.required","SSI Registration Number");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
*/			if(dynaForm.get("activityType")==null || dynaForm.get("activityType").equals(""))
			{
				ActionError error=new ActionError("errors.required","Activity Type");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("address")==null || dynaForm.get("address").equals(""))
			{
				ActionError error=new ActionError("errors.required","Unit Address");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("pincode")==null || dynaForm.get("pincode").equals(""))
			{
				ActionError error=new ActionError("errors.required","Pincode");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("cpTitle")==null || dynaForm.get("cpTitle").equals(""))
			{
				ActionError error=new ActionError("errors.required","Promoter Title");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("cpFirstName")==null || dynaForm.get("cpFirstName").equals(""))
			{
				ActionError error=new ActionError("errors.required","Chief Promoter First Name");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("cpLastName")==null || dynaForm.get("cpLastName").equals(""))
			{
				ActionError error=new ActionError("errors.required","Chief Promoter Last Name (Surname)");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(dynaForm.get("cpLegalID")!=null && dynaForm.get("cpLegalID").equals("Others"))
			{
				if(dynaForm.get("otherCpLegalID")==null || dynaForm.get("otherCpLegalID").equals(""))
				{
					  Log.log(Log.DEBUG,"Validator","checkLegalId","Legal Id Other is not entered");

					  ActionError error=new ActionError("legalIdRequired");

					  errors.add(ActionErrors.GLOBAL_ERROR,error);
				}
				else if(dynaForm.get("otherCpLegalID")!=null && !dynaForm.get("otherCpLegalID").equals(""))
				{
					if(dynaForm.get("cpLegalIdValue")==null || dynaForm.get("cpLegalIdValue").equals(""))
					{
						ActionError error=new ActionError("errors.required","Legal Id Value");

						errors.add(ActionErrors.GLOBAL_ERROR,error);

					}
				}

			}
			else if(dynaForm.get("cpLegalID")!=null && !dynaForm.get("cpLegalID").equals("Others")&& !dynaForm.get("cpLegalID").equals(""))
			{
				if(dynaForm.get("cpLegalIdValue")==null || dynaForm.get("cpLegalIdValue").equals(""))
				{
					ActionError error=new ActionError("errors.required","Legal Id Value");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}

			}
			if(dynaForm.get("subsidyName")!=null && dynaForm.get("subsidyName").equals("Others"))
			{
				if(dynaForm.get("otherSubsidyEquityName")==null || dynaForm.get("otherSubsidyEquityName").equals(""))
				{
					Log.log(Log.DEBUG,"Validator","checkSubsidyName","Subsidy Name is not entered");

					ActionError error=new ActionError("subsidyNamerequired");

					errors.add(ActionErrors.GLOBAL_ERROR,error);
				}

			}

			Administrator admin = new Administrator();
			ParameterMaster parameterMaster = admin.getParameter();
			double minAmount = parameterMaster.getMinAmtForMandatoryITPAN();
      
      
      
      
      
			if(dynaForm.get("cpITPAN")==null || dynaForm.get("cpITPAN").equals(""))
			{
     
				if(dynaForm.get("loanType").equals("TC"))
				{
					if(((java.lang.Double)dynaForm.get("termCreditSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					}
				}
				else if(dynaForm.get("loanType").equals("CC") || dynaForm.get("loanType").equals("BO"))
				{
					if(((java.lang.Double)dynaForm.get("termCreditSanctioned")).doubleValue() + ((java.lang.Double)dynaForm.get("wcFundBasedSanctioned")).doubleValue()+ ((java.lang.Double)dynaForm.get("wcNonFundBasedSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					}
				}
				else if(dynaForm.get("loanType").equals("WC"))
				{
					if(((java.lang.Double)dynaForm.get("wcFundBasedSanctioned")).doubleValue() + ((java.lang.Double)dynaForm.get("wcNonFundBasedSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					} 
				} 

			}
      
      
    /*
      * added by sukumar@path on 30-Dec-2010 for validating entered Itpan number *
     */
    
      if(!(dynaForm.get("cpITPAN")==null || dynaForm.get("cpITPAN").equals("")))
			{
      
       // System.out.println("Chief Promoter Itpan:"+(String)dynaForm.get("cpITPAN"));
       
       String itpan = (String)dynaForm.get("cpITPAN");
       boolean itpanCheck = false;
       char array1[] = itpan.toCharArray();
        if(array1.length==10  && (Character.isLetter(array1[0]) && Character.isLetter(array1[1]) && Character.isLetter(array1[2]) && Character.isLetter(array1[3]) && Character.isLetter(array1[4]))
                    && (Character.isDigit(array1[5]) && Character.isDigit(array1[6]) && Character.isDigit(array1[7]) && Character.isDigit(array1[8])) 
                   &&	(Character.isLetter(array1[9])))
                   {
                   
                   itpanCheck = true;
                   
                   } else {
                     Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");
                  itpanCheck = false;
						      ActionError error=new ActionError("errors.itpan","Chief Promoter ITPAN");

						      errors.add(ActionErrors.GLOBAL_ERROR,error);
                   
                   }
       
			/*	if(dynaForm.get("loanType").equals("TC"))
				{
					if(((java.lang.Double)dynaForm.get("termCreditSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					}
				}
				else if(dynaForm.get("loanType").equals("CC") || dynaForm.get("loanType").equals("BO"))
				{
					if(((java.lang.Double)dynaForm.get("termCreditSanctioned")).doubleValue() + ((java.lang.Double)dynaForm.get("wcFundBasedSanctioned")).doubleValue()+ ((java.lang.Double)dynaForm.get("wcNonFundBasedSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					}
				}
				else if(dynaForm.get("loanType").equals("WC"))
				{
					if(((java.lang.Double)dynaForm.get("wcFundBasedSanctioned")).doubleValue() + ((java.lang.Double)dynaForm.get("wcNonFundBasedSanctioned")).doubleValue() >= minAmount)
					{
						Log.log(Log.DEBUG,"Validator","checkSubsidyName","ITPAN is not entered");

						ActionError error=new ActionError("errors.required","Chief Promoter ITPAN");

						errors.add(ActionErrors.GLOBAL_ERROR,error);


					}
				} */

			}
      
      
      

		}
		return errors.isEmpty();
	}

	/*
	* This method checks for disbursement entry
	*/
	public static boolean checkDBREntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{
		HttpSession session=request.getSession(false);

			java.lang.Double amtDisbursedVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
			double amtDisbursedValue=amtDisbursedVal.doubleValue();

			String firstDBRDate=field.getVarValue("firstDate");
			String firstDBRDateValue=ValidatorUtil.getValueAsString(bean, firstDBRDate);

			String finalDBRDate=field.getVarValue("finalDate");
			String finalDBRDateValue=ValidatorUtil.getValueAsString(bean, finalDBRDate);

	//		if(((firstDBRDateValue!=null) && !(firstDBRDateValue.equals(""))) || ((finalDBRDateValue!=null) && !(finalDBRDateValue.equals(""))))
	//		{
		if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("0"))
		{


			if (amtDisbursedValue!=0)
			{
				if ((firstDBRDateValue==null) || (firstDBRDateValue.equals("")))
				{
					ActionError actionError=new ActionError("firstDBRDateRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

				}else if ((finalDBRDateValue!=null) && !(finalDBRDateValue.equals("")))
				{
					if ((firstDBRDateValue==null) || (firstDBRDateValue.equals("")))
					{
						ActionError actionError=new ActionError("firstDBRDateRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

				}
			}else if (amtDisbursedValue==0)
			{
				if ((firstDBRDateValue!=null) && !(firstDBRDateValue.equals("")))

				{
					ActionError actionError=new ActionError("amtDisbursedRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

				}
				else if ((finalDBRDateValue!=null) && !(finalDBRDateValue.equals("")))
				{
					if ((firstDBRDateValue==null) || (firstDBRDateValue.equals("")))
					{

						ActionError actionError=new ActionError("amtDisbursedDateRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
			//}
			}
		}


		return errors.isEmpty();
	}
	public static boolean checkWcEntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		String loanType=(String)dynaForm.get("loanType");
		
		HttpSession session = request.getSession(false);		

		java.lang.Double wcFBVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double wcFBValue=wcFBVal.doubleValue();

		String wcNFBSanctioned= field.getVarValue("wcNFB");
		java.lang.Double wcNFBSanctionedVal=new Double(ValidatorUtil.getValueAsString(bean, wcNFBSanctioned));
		double wcNFBSanctionedValue=wcNFBSanctionedVal.doubleValue();

		String wcPromoterCont= field.getVarValue("wcPCont");
		java.lang.Double wcPromoterContVal=new Double(ValidatorUtil.getValueAsString(bean, wcPromoterCont));
		double wcPromoterContValue=wcPromoterContVal.doubleValue();

		String wcSubsidyEquity= field.getVarValue("wcSubsidy");
		java.lang.Double wcSubsidyEquityVal=new Double(ValidatorUtil.getValueAsString(bean, wcSubsidyEquity));
		double wcSubsidyEquityValue=wcSubsidyEquityVal.doubleValue();

		String others= field.getVarValue("wcOther");
		java.lang.Double wcOthersVal=new Double(ValidatorUtil.getValueAsString(bean, others));
		double wcOthersValue=wcOthersVal.doubleValue();
		
		Log.log(Log.INFO,"Validator","checkWcEntry","session.getAttribute(SessionConstants.APPLICATION_TYPE) :" + session.getAttribute(SessionConstants.APPLICATION_TYPE));

		if (loanType.equals("WC") || ((loanType.equals("CC") && (session.getAttribute(SessionConstants.APPLICATION_TYPE)!=null && !session.getAttribute(SessionConstants.APPLICATION_TYPE).equals("TCE")))|| (loanType.equals("CC") && session.getAttribute(SessionConstants.APPLICATION_TYPE)==null)) || loanType.equals("BO"))
		{
			Log.log(Log.INFO,"Validator","checkWcEntry","loan type :" + loanType);
			if ((wcFBValue==0) && (wcNFBSanctionedValue==0))
   		{
				Log.log(Log.INFO,"Validator","checkWcEntry","both WC Values not entered");
				ActionError actionError=new ActionError("fbNFBRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}

		if ((wcPromoterContValue!=0) || (wcSubsidyEquityValue!=0) || (wcOthersValue!=0))
		{
			if ((wcFBValue==0) && (wcNFBSanctionedValue==0)) //changed by sukumar@path for allowing only fund based or non fund based applications also
			{
				ActionError actionError=new ActionError("fbNFBRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			} 
		}

		 return errors.isEmpty();
	}

	public static boolean checkTcEntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		String loanType=(String)dynaForm.get("loanType");

		java.lang.Double sanctionedVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double sanctionedValue=sanctionedVal.doubleValue();

		//String wcNFBSanctioned= field.getVarValue("wcNFB");
		//java.lang.Double wcNFBSanctionedVal=new Double(ValidatorUtil.getValueAsString(bean, wcNFBSanctioned));
		//double wcNFBSanctionedValue=wcNFBSanctionedVal.doubleValue();

		String tcPromoterCont= field.getVarValue("tcPromoterContribution");
		java.lang.Double tcPromoterContVal=new Double(ValidatorUtil.getValueAsString(bean, tcPromoterCont));
		double tcPromoterContValue=tcPromoterContVal.doubleValue();

		String tcSubsidyEquity= field.getVarValue("tcSubsidyOrEquity");
		java.lang.Double tcSubsidyEquityVal=new Double(ValidatorUtil.getValueAsString(bean, tcSubsidyEquity));
		double tcSubsidyEquityValue=tcSubsidyEquityVal.doubleValue();

		String tcother= field.getVarValue("tcOthers");
		java.lang.Double tcotherVal=new Double(ValidatorUtil.getValueAsString(bean, tcother));
		double tcotherValue=tcotherVal.doubleValue();

		/*if (loanType.equals("WC"))
		{
			if ((wcFBValue==0) || (wcNFBSanctionedValue==0))
			{
				ActionError actionError=new ActionError("fbNFBRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}*/

		if ((tcPromoterContValue!=0) || (tcSubsidyEquityValue!=0) || (tcotherValue!=0))
		{
			if (sanctionedValue==0)
			{
				ActionError actionError=new ActionError("termCreditSanctionedrequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}

		 return errors.isEmpty();
	}


		public static boolean checkRateValue(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {

		String rateProperty=field.getProperty();
		java.lang.Double rateVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double rateValue=rateVal.doubleValue();

		if(rateValue!=0)
		{


			if (rateValue > 100.0)
			{
				ActionError actionError=new ActionError(rateProperty + "greater");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}
			else if (rateValue == 100.0)
			{
				ActionError actionError=new ActionError(rateProperty + "equal");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}
		}
		 return errors.isEmpty();

	  }

	  public static boolean checkEnhanceValue(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {
		java.lang.Double existingVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double existingValue=existingVal.doubleValue();

		String enhanceTotal= field.getVarValue("enhanceTotal");
		java.lang.Double enhanceTotalVal=new Double(ValidatorUtil.getValueAsString(bean, enhanceTotal));
		double enhanceTotalValue=enhanceTotalVal.doubleValue();

		if (enhanceTotalValue < existingValue)
		{
			ActionError actionError=new ActionError("enhanceTotalgreater");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}
		 return errors.isEmpty();


	  }

	   public static boolean checkEnhanceEntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {
		   String enhanceRenewFbValue=field.getProperty();
		   java.lang.Double enhancedRenewFB=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
			double enhancedRenewFBValue=enhancedRenewFB.doubleValue();

			String enhancedRenewNFB= field.getVarValue("enhancedRenewNFB");
			java.lang.Double enhancedRenewNFBVal=new Double(ValidatorUtil.getValueAsString(bean, enhancedRenewNFB));
			double enhancedRenewNFBValue=enhancedRenewNFBVal.doubleValue();

			String enhancedrenewFBInterest= field.getVarValue("enhancedrenewFBInterest");
			java.lang.Double enhancedrenewFBInterestVal=new Double(ValidatorUtil.getValueAsString(bean, enhancedrenewFBInterest));
			double enhancedrenewFBInterestValue=enhancedrenewFBInterestVal.doubleValue();

			String enhancedrenewNFBComission= field.getVarValue("enhancedrenewNFBComission");
			java.lang.Double enhancedrenewNFBComissionVal=new Double(ValidatorUtil.getValueAsString(bean, enhancedrenewNFBComission));
			double enhancedrenewNFBComissionValue=enhancedrenewNFBComissionVal.doubleValue();

			if ((enhancedRenewFBValue==0) && (enhancedRenewNFBValue==0))
			{
				ActionError actionError=new ActionError("errors.required","Working Capital Limit Sanctioned");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
			else{
				if (enhancedRenewFBValue!=0)
				{
					if ((enhancedrenewFBInterestValue==0) || (enhancedrenewFBInterestValue==0.0))
					{
						ActionError actionError=new ActionError("errors.required","Fund Based Interest");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
				else if(enhancedRenewFBValue==0){

					if ((enhancedrenewFBInterestValue!=0))
					{
						ActionError actionError=new ActionError("errors.required","Fund Based Limit Sanctioned");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
				if (enhancedRenewNFBValue!=0)
				{
					if (enhancedrenewNFBComissionValue==0)
					{
						
						ActionError actionError=new ActionError("errors.required","Non Fund Based Commission");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
				else if (enhancedRenewNFBValue==0){

					if ((enhancedrenewNFBComissionValue!=0))
					{
						ActionError actionError=new ActionError("errors.required","Non Fund Based Limit Sanctioned");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}

			}

			 return errors.isEmpty();

	  }

/**
* This method whether working capital limit has been entered if Y has been selected
*/
	public static boolean checkMarginMoneyAsTL(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	{
		String marginMoneyValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String wcFBSanctioned= field.getVarValue("wcFB");
		java.lang.Double wcFBSanctionedVal=new Double(ValidatorUtil.getValueAsString(bean, wcFBSanctioned));
		double wcFBSanctionedValue=wcFBSanctionedVal.doubleValue();

		if (marginMoneyValue.equals("Y"))
		{
			if (wcFBSanctionedValue==0)
			{
				ActionError actionError=new ActionError("wcFBrequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}
		}
		return errors.isEmpty();

	}

/*
 * This method checks whether the comments and approved amount have been entered for the
 * corresponding status for clear applications
 */

	 public static boolean checkClearStatusComments(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		APForm apForm=(APForm)bean;

		Map clearRemarks=apForm.getClearRemarks();
		Set clearRemarksSet=clearRemarks.keySet();
		Iterator clearRemarksIterator=clearRemarksSet.iterator();

		Map clearStatus=apForm.getClearStatus();
		Set clearStatusSet=clearStatus.keySet();
		Iterator clearStatusIterator=clearStatusSet.iterator();

		Map clearApprovedAmt=apForm.getClearApprovedAmt();
		Set clearApprovedAmtSet=clearApprovedAmt.keySet();
		Iterator clearApprovedAmtIterator=clearApprovedAmtSet.iterator();

		Map clearCreditAmt=apForm.getClearCreditAmt();
		Set clearCreditAmtSet=clearCreditAmt.keySet();
		Iterator clearCreditAmtIterator=clearCreditAmtSet.iterator();

		Map clearAppRefNo=apForm.getClearAppRefNo();
		Set clearAppRefNoSet=clearAppRefNo.keySet();
		Iterator clearAppRefNoIterator=clearAppRefNoSet.iterator();

		boolean clearStatusVal=false;

		if(clearStatus!=null && clearStatus.size()!=0)
		{

		while(clearStatusIterator.hasNext())
		{

			String key=(String)clearStatusIterator.next();
//			String approvedStatus=(String)clearStatus.get(clearStatusIterator.next());
			if (clearStatus.get(key).equals("AP"))
			{

/*			Double doubleAmt = new Double((String)clearApprovedAmt.get(clearApprovedAmtIterator.next()));
				double approvedAmount=doubleAmt.doubleValue();
*/				if(clearApprovedAmt.get(key).equals("") || (!clearApprovedAmt.get(key).equals("") && Double.parseDouble((String)clearApprovedAmt.get(key))==0))
				{

					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("approvedAmtRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("approvedAmtRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
/*
					ActionError actionError=new ActionError("approvedAmtRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/

				}else if((new Double((String)clearApprovedAmt.get(key))).doubleValue() > (new Double((String)clearCreditAmt.get(key))).doubleValue())
				{
					Log.log(Log.INFO,"Validator","checkClearStatusComments","Entered if greater");

/*					Arg arg = new Arg();
					Log.log(Log.INFO,"Validator","checkClearStatusComments","greater for app ref no " + (String) clearAppRefNo.get(key));
					arg.setKey((String) clearAppRefNo.get(key));
					field.addArg0(arg);
					field.setKey((String) clearAppRefNo.get(key));
					ActionError actionError=new ActionError("checkClearStatusComments");
					errors.add(field.getKey(),actionError);

/*					Log.log(Log.INFO,"Validator","checkClearStatusComments","key " + (String) field.getKey());
					errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
					ActionMessage actionMessage=new ActionMessage("checkClearStatusComments");
					errors.add("error ", actionMessage);

*/
					ActionError actionError=new ActionError("checkClearStatusComments",clearAppRefNo.get(key));

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);


					//errors.add(ActionErrors.GLOBAL_ERROR,actionError);


				}
			}else if (clearStatus.get(key).equals("HO") || clearStatus.get(key).equals("RE") || clearStatus.get(key).equals("PE"))
			{
/*		Double doubleAmt = new Double((String)clearApprovedAmt.get(clearApprovedAmtIterator.next()));
				double approvedAmount=doubleAmt.doubleValue();

			String remarks=(String)clearRemarks.get(clearRemarksIterator.next());
*/				if(clearRemarks.get(key)==null || clearRemarks.get(key).equals(""))
				{

					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("remarksRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("remarksRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
/*
					ActionError actionError=new ActionError("remarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
				}
			if(!(clearApprovedAmt.get(key).equals("")) && Double.parseDouble((String)clearApprovedAmt.get(key))!=0)
				{
					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("amountNotRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("amountNotRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
/*
					ActionError actionError=new ActionError("amountNotRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
				}

			}

	/*		if (!(clearStatus.get(key).equals("")))
			{
				clearStatusVal=true;
				break;

			ActionError actionError=new ActionError("statusRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
*/
		}

/*		if(!clearStatusVal)
		{
			ActionError actionError=new ActionError("statusRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}
*/		}

		return errors.isEmpty();

	}

/*
 * This method checks the amounts and comments entered for the corresponding status
 * for probable Duplicate Applications
 */

 public static boolean checkDupStatusComments(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	APForm apForm=(APForm)bean;

	Map duplicateRemarks=apForm.getDuplicateRemarks();
	Map duplicateStatus=apForm.getDuplicateStatus();
	Map duplicateApprovedAmt=apForm.getDuplicateApprovedAmt();

	Set duplicateRemarksSet=duplicateRemarks.keySet();
	Set duplicateStatusSet=duplicateStatus.keySet();
	Set duplicateApprovedAmtSet=duplicateApprovedAmt.keySet();

	Iterator duplicateRemarksIterator=duplicateRemarksSet.iterator();
	Iterator duplicateStatusIterator=duplicateStatusSet.iterator();
	Iterator duplicateApprovedAmtIterator=duplicateApprovedAmtSet.iterator();

	Map dupCreditAmt=apForm.getDuplicateCreditAmt();
	Set dupCreditAmtSet=dupCreditAmt.keySet();
	Iterator dupCreditAmtIterator=dupCreditAmtSet.iterator();

	Map dupAppRefNo=apForm.getDuplicateAppRefNo();
	Set dupAppRefNoSet=dupAppRefNo.keySet();
	Iterator dupAppRefNoIterator=dupAppRefNoSet.iterator();

	ApplicationProcessor appProcessor = new ApplicationProcessor();

	boolean statusVal=false;

	if(duplicateStatus!=null && duplicateStatus.size()!=0)
	{


	while(duplicateStatusIterator.hasNext())
	{

		String key=(String)duplicateStatusIterator.next();
//		String remarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next());
		if (duplicateStatus.get(key).equals("AP") || duplicateStatus.get(key).equals("ATL") || duplicateStatus.get(key).equals("WCR"))
		{
			Application testApplication = new Application();
			try{

				testApplication = appProcessor.getApplication(null,"",(String)dupAppRefNo.get(key));
			}
			catch(NoApplicationFoundException e)
			{
			}
			catch(DatabaseException e)
			{
			}


/*			Double doubleAmt = new Double((String)duplicateApprovedAmt.get(duplicateApprovedAmtIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();
*/			if(duplicateApprovedAmt.get(key).equals("") || (!duplicateApprovedAmt.get(key).equals("") && Double.parseDouble((String)duplicateApprovedAmt.get(key))==0))
			{

					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("approvedAmtforATLRequired"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("approvedAmtforATLRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
/*						ActionError actionError=new ActionError("approvedAmtforATLRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
/*					}
				}
*/
			}
			else if(duplicateRemarks.get(key)==null && duplicateRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForATLRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForATLRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("remarksForATLRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}else if((new Double((String)duplicateApprovedAmt.get(key))).doubleValue() > (new Double((String)dupCreditAmt.get(key))).doubleValue())
				{
					Log.log(Log.INFO,"Validator","checkClearStatusComments","Entered if greater");

/*					Arg arg = new Arg();
					Log.log(Log.INFO,"Validator","checkClearStatusComments","greater for app ref no " + (String) clearAppRefNo.get(key));
					arg.setKey((String) clearAppRefNo.get(key));
					field.addArg0(arg);
					field.setKey((String) clearAppRefNo.get(key));
					ActionError actionError=new ActionError("checkClearStatusComments");
					errors.add(field.getKey(),actionError);

/*					Log.log(Log.INFO,"Validator","checkClearStatusComments","key " + (String) field.getKey());
					errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
					ActionMessage actionMessage=new ActionMessage("checkClearStatusComments");
					errors.add("error ", actionMessage);

*/
					ActionError actionError=new ActionError("checkClearStatusComments",dupAppRefNo.get(key));

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);


					//errors.add(ActionErrors.GLOBAL_ERROR,actionError);


				}
				else if(testApplication.getStatus().equals("EN") && testApplication.getApprovedAmount() >= (new Double((String)duplicateApprovedAmt.get(key))).doubleValue())
				{
					ActionError actionError=new ActionError("checkEnhancedApprovedAmount",dupAppRefNo.get(key));

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

				}

				testApplication=null;
		}else if (duplicateStatus.get(key).equals("HO") || duplicateStatus.get(key).equals("RE") || duplicateStatus.get(key).equals("PE"))
		{
/*			Double doubleAmt = new Double((String)duplicateApprovedAmt.get(duplicateApprovedAmtIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();

			String dupRemarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next());

*/


		if(duplicateRemarks.get(key)==null || duplicateRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

			}
			if(!(duplicateApprovedAmt.get(key).equals("")) && Double.parseDouble((String)duplicateApprovedAmt.get(key))!=0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("amountNotRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("amountNotRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("amountNotRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}

		}else if (duplicateStatus.get(key).equals("EN"))
		{
//			String dupRemarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next());
			if(duplicateRemarks.get(key)==null || duplicateRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForENRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForENRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

				/*ActionError actionError=new ActionError("remarksForENRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
		}
/*		if (!(duplicateStatus.get(key).equals("")))
		{
			statusVal=true;
			break;
			/*
			ActionError actionError=new ActionError("statusRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
*/
	}
/*	if(!statusVal)
	{
		ActionError actionError=new ActionError("dupStatusRequired");

		errors.add(ActionErrors.GLOBAL_ERROR,actionError);

	}
*/	}

	return errors.isEmpty();
}

/*
 * This method checks the amounts and comments entered for the corresponding status
 * for probable IneligibleApplications
 */

 public static boolean checkIneligibleStatusComments(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	APForm apForm=(APForm)bean;


	Map ineligibleRemarks=apForm.getIneligibleRemarks();
	Map ineligibleStatus=apForm.getIneligibleStatus();
	Map ineligibleApprovedAmt=apForm.getIneligibleApprovedAmt();

	Set ineligibleRemarksSet=ineligibleRemarks.keySet();
	Set ineligibleStatusSet=ineligibleStatus.keySet();
	Set ineligibleApprovedAmtSet=ineligibleApprovedAmt.keySet();

	Iterator ineligibleRemarksIterator=ineligibleRemarksSet.iterator();
	Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();
	Iterator ineligibleApprovedAmtIterator=ineligibleApprovedAmtSet.iterator();

	Map ineligibleCreditAmt=apForm.getIneligibleCreditAmt();
	Set ineligibleCreditAmtSet=ineligibleCreditAmt.keySet();
	Iterator ineligibleCreditAmtIterator=ineligibleCreditAmtSet.iterator();

	Map ineligibleAppRefNo=apForm.getIneligibleAppRefNo();
	Set ineligibleAppRefNoSet=ineligibleAppRefNo.keySet();
	Iterator ineligibleAppRefNoIterator=ineligibleAppRefNoSet.iterator();

	ApplicationProcessor appProcessor = new ApplicationProcessor();
	boolean statusVal=false;

	if(ineligibleStatus!=null && ineligibleStatus.size()!=0)
	{

	while(ineligibleStatusIterator.hasNext())
	{

		String key=(String)ineligibleStatusIterator.next();
/*		String remarks=(String)ineligibleRemarks.get(ineligibleRemarksIterator.next());
*/		if (ineligibleStatus.get(key).equals("AP") || ineligibleStatus.get(key).equals("ATL") || ineligibleStatus.get(key).equals("WCR"))
		{

			Application testApplication = new Application();
			try{

				testApplication = appProcessor.getApplication(null,"",(String)ineligibleAppRefNo.get(key));
			}
			catch(NoApplicationFoundException e)
			{
			}
			catch(DatabaseException e)
			{
			}

/*			Double doubleAmt = new Double((String)ineligibleApprovedAmt.get(ineligibleApprovedAmtIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();
*/			if(ineligibleApprovedAmt.get(key).equals("") || (!ineligibleApprovedAmt.get(key).equals("") && Double.parseDouble((String)ineligibleApprovedAmt.get(key))==0))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("approvedAmtforATLRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("approvedAmtforATLRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("approvedAmtforATLRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/			}
			else if(ineligibleRemarks.get(key)==null && ineligibleRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForATLRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForATLRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("remarksForATLRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}else if((new Double((String)ineligibleApprovedAmt.get(key))).doubleValue() > (new Double((String)ineligibleCreditAmt.get(key))).doubleValue())
				{
					Log.log(Log.INFO,"Validator","checkClearStatusComments","Entered if greater");

/*					Arg arg = new Arg();
					Log.log(Log.INFO,"Validator","checkClearStatusComments","greater for app ref no " + (String) clearAppRefNo.get(key));
					arg.setKey((String) clearAppRefNo.get(key));
					field.addArg0(arg);
					field.setKey((String) clearAppRefNo.get(key));
					ActionError actionError=new ActionError("checkClearStatusComments");
					errors.add(field.getKey(),actionError);

/*					Log.log(Log.INFO,"Validator","checkClearStatusComments","key " + (String) field.getKey());
					errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
					ActionMessage actionMessage=new ActionMessage("checkClearStatusComments");
					errors.add("error ", actionMessage);

*/
					ActionError actionError=new ActionError("checkClearStatusComments",ineligibleAppRefNo.get(key));

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);


					//errors.add(ActionErrors.GLOBAL_ERROR,actionError);


				}
			else if(testApplication.getStatus().equals("EN") && testApplication.getApprovedAmount() >= (new Double((String)ineligibleApprovedAmt.get(key))).doubleValue())
			{
				ActionError actionError=new ActionError("checkEnhancedApprovedAmount",ineligibleAppRefNo.get(key));

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}

			testApplication=null;


		}else if (ineligibleStatus.get(key).equals("HO") || ineligibleStatus.get(key).equals("RE") || ineligibleStatus.get(key).equals("PE"))
		{
/*			Double doubleAmt = new Double((String)ineligibleApprovedAmt.get(ineligibleApprovedAmtIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();
*/
			//String ineligibleRemark=(String)ineligibleRemarks.get(ineligibleRemarksIterator.next());
			if(ineligibleRemarks.get(key)==null || ineligibleRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("remarksRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if(!(ineligibleApprovedAmt.get(key).equals("")) && Double.parseDouble((String)ineligibleApprovedAmt.get(key))!=0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("amountNotRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("amountNotRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}


/*				ActionError actionError=new ActionError("amountNotRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}

		}else if (ineligibleStatus.get(key).equals("EN"))
		{
//			String ineligibleRemark=(String)ineligibleRemarks.get(ineligibleRemarksIterator.next());
			if(ineligibleRemarks.get(key)==null || ineligibleRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForENRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForENRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("remarksForENRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
		}
/*		if (!(ineligibleStatus.get(key).equals("")))
		{
			statusVal=true;
			break;
		/*	ActionError actionError=new ActionError("statusRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
*/
	}

	}
	return errors.isEmpty();
}

/*
 * This method checks the amounts and comments entered for the corresponding status
 * for Ineligible Duplicate Applications
 */

 public static boolean checkIneligibleDupStatusComments(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	APForm apForm=(APForm)bean;

	Map ineligibleDupRemarks=apForm.getIneligibleDupRemarks();
	Map ineligibleDupStatus=apForm.getIneligibleDupStatus();
	Map ineligibleDupApprovedAmt=apForm.getIneligibleDupApprovedAmt();

	Set ineligibleDupRemarksSet=ineligibleDupRemarks.keySet();
	Set ineligibleDupStatusSet=ineligibleDupStatus.keySet();
	Set ineligibleDupApprovedAmtSet=ineligibleDupApprovedAmt.keySet();

	Iterator ineligibleDupRemarksIterator=ineligibleDupRemarksSet.iterator();
	Iterator ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();
	Iterator ineligibleDupApprovedAmtIterator=ineligibleDupApprovedAmtSet.iterator();

	Map ineligibleDupCreditAmt=apForm.getIneligibleDupCreditAmt();
	Set ineligibleDupCreditAmtSet=ineligibleDupCreditAmt.keySet();
	Iterator ineligibleDupCreditAmtIterator=ineligibleDupCreditAmtSet.iterator();

	Map ineligibleDupAppRefNo=apForm.getIneligibleDupAppRefNo();
	Set ineligibleDupAppRefNoSet=ineligibleDupAppRefNo.keySet();
	Iterator ineligibleDupAppRefNoIterator=ineligibleDupAppRefNoSet.iterator();

	ApplicationProcessor appProcessor = new ApplicationProcessor();

	boolean statusVal=false;

	if(ineligibleDupStatus!=null && ineligibleDupStatus.size()!=0)
	{


	while(ineligibleDupStatusIterator.hasNext())
	{

		String key=(String)ineligibleDupStatusIterator.next();
		//String approvedStatus=(String)ineligibleDupStatus.get(ineligibleDupStatusIterator.next());
		//String remarks=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator.next());
		if (ineligibleDupStatus.get(key).equals("AP") || ineligibleDupStatus.get(key).equals("ATL") || ineligibleDupStatus.get(key).equals("WCR"))
		{

			Application testApplication = new Application();
			try{

				testApplication = appProcessor.getApplication(null,"",(String)ineligibleDupAppRefNo.get(key));
			}
			catch(NoApplicationFoundException e)
			{
			}
			catch(DatabaseException e)
			{
			}

			//Double doubleAmt = new Double((String)ineligibleDupApprovedAmt.get(ineligibleDupApprovedAmtIterator.next()));
			//double approvedAmount=doubleAmt.doubleValue();
			if(ineligibleDupApprovedAmt.get(key).equals("") || (!ineligibleDupApprovedAmt.get(key).equals("") && Double.parseDouble((String)ineligibleDupApprovedAmt.get(key))==0))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("approvedAmtforATLRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("approvedAmtforATLRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("approvedAmtforATLRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/			}
			else if(ineligibleDupRemarks.get(key)==null && ineligibleDupRemarks.get(key).equals(""))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForATLRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForATLRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("remarksForATLRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}else if((new Double((String)ineligibleDupApprovedAmt.get(key))).doubleValue() > (new Double((String)ineligibleDupCreditAmt.get(key))).doubleValue())
				{
					Log.log(Log.INFO,"Validator","checkClearStatusComments","Entered if greater");

/*					Arg arg = new Arg();
					Log.log(Log.INFO,"Validator","checkClearStatusComments","greater for app ref no " + (String) clearAppRefNo.get(key));
					arg.setKey((String) clearAppRefNo.get(key));
					field.addArg0(arg);
					field.setKey((String) clearAppRefNo.get(key));
					ActionError actionError=new ActionError("checkClearStatusComments");
					errors.add(field.getKey(),actionError);

/*					Log.log(Log.INFO,"Validator","checkClearStatusComments","key " + (String) field.getKey());
					errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
					ActionMessage actionMessage=new ActionMessage("checkClearStatusComments");
					errors.add("error ", actionMessage);

*/
					ActionError actionError=new ActionError("checkClearStatusComments",ineligibleDupAppRefNo.get(key));

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);


					//errors.add(ActionErrors.GLOBAL_ERROR,actionError);


				}
			else if(testApplication.getStatus().equals("EN") && testApplication.getApprovedAmount() >= (new Double((String)ineligibleDupApprovedAmt.get(key))).doubleValue())
			{
				ActionError actionError=new ActionError("checkEnhancedApprovedAmount",ineligibleDupAppRefNo.get(key));

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}


		}else if (ineligibleDupStatus.get(key).equals("HO") || ineligibleDupStatus.get(key).equals("RE") || ineligibleDupStatus.get(key).equals("PE"))
		{
			//Double doubleAmt = new Double((String)ineligibleDupApprovedAmt.get(ineligibleDupApprovedAmtIterator.next()));
			//double approvedAmount=doubleAmt.doubleValue();

			//String ineligibleDupRemark=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator.next());
			if(ineligibleDupRemarks.get(key)==null || ineligibleDupRemarks.get(key).equals(""))
			{

				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("remarksRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if(!(ineligibleDupApprovedAmt.get(key).equals("")) && Double.parseDouble((String)ineligibleDupApprovedAmt.get(key))!=0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("amountNotRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("amountNotRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("amountNotRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}

		}else if (ineligibleDupStatus.get(key).equals("EN"))
		{
			//String ineligibleDupRemark=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator.next());
			if(ineligibleDupRemarks.get(key)==null || ineligibleDupRemarks.get(key).equals(""))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("remarksForENRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("remarksForENRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("remarksForENRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
		}
/*		if (!(ineligibleDupStatus.get(key).equals("")))
		{
			statusVal=true;
			break;

			ActionError actionError=new ActionError("statusRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}
*/
	}

/*	if(ineligibleDupStatus.size()==0)
	{
		ActionError actionError=new ActionError("ineligibleDupStatusRequired");

		errors.add(ActionErrors.GLOBAL_ERROR,actionError);

	}
*/	}



	return errors.isEmpty();
}

/*
 * This method if atleast one status has been selected
 */
 public static boolean checkStatusSelect(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	APForm apForm=(APForm)bean;

	Map clearStatus=apForm.getClearStatus();
	Set clearStatusSet=clearStatus.keySet();
	Iterator clearStatusIterator=clearStatusSet.iterator();

	Map duplicateStatus=apForm.getDuplicateStatus();
	Set duplicateStatusSet=duplicateStatus.keySet();
	Iterator duplicateStatusIterator=duplicateStatusSet.iterator();

	Map ineligibleStatus=apForm.getIneligibleStatus();
	Set ineligibleStatusSet=ineligibleStatus.keySet();
	Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();

	Map ineligibleDupStatus=apForm.getIneligibleDupStatus();
	Set ineligibleDupStatusSet=ineligibleDupStatus.keySet();
	Iterator ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();

	boolean clearVal=false;
	boolean dupVal=false;
	boolean ineligibleVal=false;
	boolean ineligibleDupVal=false;


	if(clearStatus!=null && clearStatus.size()!=0)
	{
		while(clearStatusIterator.hasNext())
		{

			String key=(String)clearStatusIterator.next();

			if (!(clearStatus.get(key).equals("")))
			{
				clearVal=true;
				break;

/*				ActionError actionError=new ActionError("statusRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/			}
		}
	}

	if(duplicateStatus!=null && duplicateStatus.size()!=0)
	{
		while(duplicateStatusIterator.hasNext())
		{

			String key=(String)duplicateStatusIterator.next();

			if (!(duplicateStatus.get(key).equals("")))
			{
				dupVal=true;
				break;
						/*
						ActionError actionError=new ActionError("statusRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	*/		}
		}

	}
	if(ineligibleStatus!=null && ineligibleStatus.size()!=0)
	{

		while(ineligibleStatusIterator.hasNext())
		{

			String key=(String)ineligibleStatusIterator.next();
			if (!(ineligibleStatus.get(key).equals("")))
			{
				ineligibleVal=true;
				break;
					/*	ActionError actionError=new ActionError("statusRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/			}

		}
	}

	if(ineligibleDupStatus!=null && ineligibleDupStatus.size()!=0)
	{
		while(ineligibleDupStatusIterator.hasNext())
		{

			String key=(String)ineligibleDupStatusIterator.next();
			if (!(ineligibleDupStatus.get(key).equals("")))
			{
				ineligibleDupVal=true;
				break;

	/*					ActionError actionError=new ActionError("statusRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
		}
	}

	if(!clearVal && !dupVal && !ineligibleVal && !ineligibleDupVal)
	{
		ActionError actionError=new ActionError("statusRequired");

		errors.add(ActionErrors.GLOBAL_ERROR,actionError);


	}




	return errors.isEmpty();
}

/*
 * This method checks the comments the reapproved amount and comments depending on the status
 */

public static boolean checkReapproveStatusComments(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	APForm apForm=(APForm)bean;

	Map reapprovedStatus = apForm.getReapprovalStatus();
	Map reapprovedAmt=apForm.getReApprovedAmt();
	Map reapprovedRemarks=apForm.getReApprovalRemarks();
	Map reapprovedCreditAmt=apForm.getCreditAmt();
	Map reapprovedCgpan=apForm.getCgpanNo();

	Set statusSet=reapprovedStatus.keySet();
	Set amountSet=reapprovedAmt.keySet();
	Set remarksSet=reapprovedRemarks.keySet();
	Set reapprovedCreditAmtSet=reapprovedCreditAmt.keySet();
	Set reapprovedCgpanSet=reapprovedCgpan.keySet();


	Iterator statusIterator=statusSet.iterator();
	Iterator amountIterator=amountSet.iterator();
	Iterator remarksIterator=remarksSet.iterator();
	Iterator reapprovedCreditIterator=reapprovedCreditAmtSet.iterator();
	Iterator reapprovedCgpanIterator=reapprovedCgpanSet.iterator();

	while(statusIterator.hasNext())
	{
		String key=(String)statusIterator.next();
		//String approvedStatus=(String)reapprovedStatus.get(statusIterator.next());
		//System.out.println("status :" +approvedStatus);
		if (reapprovedStatus.get(key).equals("AP") || reapprovedStatus.get(key).equals("EN"))
		{
			if(reapprovedAmt.get(key).equals("") || (!reapprovedAmt.get(key).equals("") && Double.parseDouble((String)reapprovedAmt.get(key))==0))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("reApprovedAmtRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("reApprovedAmtRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("reApprovedAmtRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if(reapprovedRemarks.get(key)==null || reapprovedRemarks.get(key).equals(""))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("reapprovalRemarksRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("reapprovalRemarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError=new ActionError("reapprovalRemarksRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if((new Double((String)reapprovedAmt.get(key))).doubleValue() > (new Double((String)reapprovedCreditAmt.get(key))).doubleValue())
			{
				ActionError actionError=new ActionError("checkReapproveStatusComments",reapprovedCgpan.get(key));

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}


/*	Double doubleAmt = new Double((String)reapprovedAmt.get(amountIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();
			if(approvedAmount==0)
			{


			}*/
		}else if (reapprovedStatus.get(key).equals("HO") || reapprovedStatus.get(key).equals("RE"))
		{
			/*Double doubleAmt = new Double((String)reapprovedAmt.get(amountIterator.next()));
			double approvedAmount=doubleAmt.doubleValue();
*/
			//String remarks=(String)reapprovedRemarks.get(remarksIterator.next());
			if(reapprovedRemarks.get(key)==null || reapprovedRemarks.get(key).equals(""))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("reapprovalRemarksRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("reapprovalRemarksRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

/*				ActionError actionError=new ActionError("reapprovalRemarksRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if(!(reapprovedAmt.get(key).equals("")) && Double.parseDouble((String)reapprovedAmt.get(key))!=0)
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("reapprovedAmountNotRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("reapprovedAmountNotRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*

				ActionError actionError=new ActionError("reapprovedAmountNotRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}

		}/*else if (reapprovedStatus.get(key).equals(""))
		{
			ActionError actionError=new ActionError("statusRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}*/
	}
	return errors.isEmpty();

}

/**
 * This method checks if atleast one decision has been taken for reqpproved Applications
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */

 public static boolean checkReapproveStatus(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{

	APForm apForm=(APForm)bean;

	Map reapprovedStatus = apForm.getReapprovalStatus();
	Set reapproveStatusSet=reapprovedStatus.keySet();
	Iterator reapproveStatusIterator=reapproveStatusSet.iterator();

	boolean reapproveStatus=false;

	if(reapprovedStatus!=null && reapprovedStatus.size()!=0)
	{
		while(reapproveStatusIterator.hasNext())
		{

			String key=(String)reapproveStatusIterator.next();

			if (!(reapprovedStatus.get(key).equals("")))
			{
				reapproveStatus=true;
				break;

/*				ActionError actionError=new ActionError("statusRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/			}
		}
	}

	if(!reapproveStatus)
	{
		ActionError actionError=new ActionError("statusRequired");

		errors.add(ActionErrors.GLOBAL_ERROR,actionError);


	}

	return errors.isEmpty();
}

/**
 * This method checks whether credit to be guaranteed amount is greater than sanctioned amount
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */

 public static boolean checkCreditAmtGreater(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	java.lang.Double creditVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
	String creditString=field.getProperty();
	double creditValue=creditVal.doubleValue();

	String termCreditSanctionedVal= field.getVarValue("termCreditSanctioned");
	java.lang.Double termCreditSanctioned=new Double(ValidatorUtil.getValueAsString(bean, termCreditSanctionedVal));
	double termCreditSanctionedValue=termCreditSanctioned.doubleValue();

	if(creditValue!=0 && termCreditSanctionedValue!=0)
	{
		if(creditValue > termCreditSanctionedValue)
		{
			ActionError error=new ActionError("creditAmtLess" + creditString);

			 errors.add(ActionErrors.GLOBAL_ERROR,error);
		}

	}



	return errors.isEmpty();
}

/**
 * This method validates the entry of outstanding amount only if amount disbursed is
 * entered
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */
 public static boolean checkDisburseOsEntry(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)throws Exception
{

	java.lang.Double amtdisbursedVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
 //System.out.println("checkDisburseOsEntry Entered");
	double amtdisbursed=amtdisbursedVal.doubleValue();
// System.out.println("checkDisburseOsEntry-amtdisbursed:"+amtdisbursed);
	String pplOSVal= field.getVarValue("pplOS");

	java.lang.Double pplOS=new Double(ValidatorUtil.getValueAsString(bean, pplOSVal));
	double pplOSValue=pplOS.doubleValue();

	String pplOsAsOnDateVal= field.getVarValue("pplOsAsOnDate");
	String pplOsAsOnDateValue=ValidatorUtil.getValueAsString(bean,pplOsAsOnDateVal);

	String firstInstallmentDueDateVal= field.getVarValue("firstInstallmentDueDate");
	String firstInstallmentDueDateValue=ValidatorUtil.getValueAsString(bean,firstInstallmentDueDateVal);

	String finalDisDateVal= field.getVarValue("finalDate");
	String finalDisDate=ValidatorUtil.getValueAsString(bean,finalDisDateVal);

	String firstDisDateVal= field.getVarValue("firstDisbursementDate");
	String firstDBRDateValue=ValidatorUtil.getValueAsString(bean,firstDisDateVal);

	HttpSession session=request.getSession(false);
  //added by sukumar@path on 31-Dec-2009 for testing result of loanType
  /*  try
    {
          String loanType = (String) session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
          if (loanType!=null && !loanType.equals(""))
          System.out.println("validator.java line number loanType:"+loanType);
    } 
    catch(NullPointerException nullpointerexception)
		   {
					ActionError actionError=new ActionError("Application Loan Type is Null");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			
		   } */
       
    if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)==null||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("")){
        System.out.println("Line Number 3790 Application Loan Type is Null");
       } else if(session.getAttribute("APPLICATION_TYPE_FLAG")!=null||session.getAttribute("APPLICATION_TYPE_FLAG").toString().equals("")){
         System.out.println("Line number 3792 Loan Type:"+session.getAttribute("APPLICATION_TYPE_FLAG").toString());
       }
	if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("3") ||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("0")
	|| session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("10")
	||((session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")) || (session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC")||
	session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("CC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("WC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("BO"))))
	{
  
    if(amtdisbursed!=0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			boolean finaldateValue = false;
			boolean firstdateValue = false;
			boolean firstInstdateValue = false;
			boolean pplOsdateValue = false;

			java.util.Date pplOsAsOnDate=null;
			Date firstDisDate=null;

			if ((pplOsAsOnDateValue!=null) && !(pplOsAsOnDateValue.equals("")))
			{
				pplOsdateValue = true;
				try{
					pplOsAsOnDate = (java.util.Date)sdf.parse(pplOsAsOnDateValue,new ParsePosition(0));
					if(pplOsAsOnDate==null)
					{
						ActionError actionError=new ActionError("errors.date","Outstanding As On Date");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

						pplOsdateValue = false;

					}

				}
				catch(Exception n)
				{
					ActionError actionError=new ActionError("errors.date","Outstanding As On Date");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					pplOsdateValue = false;
				}

			}


			if ((finalDisDate!=null) && !(finalDisDate.equals("")))
			{
				finaldateValue = true;
				try{
					java.util.Date finalDisDateValue = (java.util.Date)sdf.parse(finalDisDate,new ParsePosition(0));
					if(finalDisDateValue==null)
					{
						ActionError actionError=new ActionError("errors.date","Final Disbursement Date");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

						finaldateValue = false;

					}

				}
				catch(Exception n)
				{
					ActionError actionError=new ActionError("errors.date","Final Disbursement Date");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					finaldateValue = false;
				}

			}
			if (firstDBRDateValue!=null && !firstDBRDateValue.equals(""))
			{
				firstdateValue = true;
				try{
					firstDisDate = sdf.parse(firstDBRDateValue,new ParsePosition(0));
					if(firstDisDate==null)
					{
						ActionError actionError=new ActionError("errors.date","First Disbursement Date");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

						firstdateValue = false;

					}

				}
				catch(Exception n)
				{
					ActionError actionError=new ActionError("errors.date","First Disbursement Date");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					firstdateValue = false;
				}

			}
			if(firstInstallmentDueDateValue!=null && !firstInstallmentDueDateValue.equals(""))
			{
				firstInstdateValue = true;
				try{
					Date firstInstallmentDueDate = sdf.parse(firstInstallmentDueDateValue,new ParsePosition(0));

					if(firstInstallmentDueDate==null)
					{
						ActionError actionError=new ActionError("errors.date","First Instalment Date");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

						firstInstdateValue = false;

					}

				}
				catch(NumberFormatException n)
				{
					ActionError actionError=new ActionError("errors.date","First Instalment Due Date");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					firstInstdateValue = false;
				}

			}

			if(GenericValidator.isBlankOrNull(firstDBRDateValue) && GenericValidator.isBlankOrNull(finalDisDate))
			{
					ActionError error=new ActionError("firstDBRDateRequired");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);
			}

			if(GenericValidator.isBlankOrNull(firstDBRDateValue) && !GenericValidator.isBlankOrNull(finalDisDate))
			{
					ActionError error=new ActionError("firstDBRDateRequired");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);
			}

			if(firstdateValue && finaldateValue )
			{
				if(!GenericValidator.isBlankOrNull(firstDBRDateValue) && !GenericValidator.isBlankOrNull(finalDisDate))
				{
        //commented by sukumar@path on 08-09-2009 for First Disbursement Date should be earlier than or equal to Final Disbursement Date
			/*		if (firstDBRDateValue.compareTo(finalDisDate)>0)
					{

						ActionError actionMessage=new ActionError("fromDatefinalDisDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					} */
				}

			}


			if(firstdateValue && firstInstdateValue)
			{
				if(!GenericValidator.isBlankOrNull(firstDBRDateValue) && !GenericValidator.isBlankOrNull(firstInstallmentDueDateValue))
				{
					if (! DateHelper.day1BeforeDay2(firstDBRDateValue, firstInstallmentDueDateValue))
					{
						ActionError actionMessage=new ActionError("fromDateGTfirstInstalmentDueDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
					}
				}
			}

			if(firstdateValue && pplOsdateValue)
			{
				if(!GenericValidator.isBlankOrNull(pplOsAsOnDateValue)&& !GenericValidator.isBlankOrNull(firstDBRDateValue))
				{
					if(firstDisDate.compareTo(pplOsAsOnDate)==1)
					{
						ActionError error=new ActionError("dbrDateBeforeOsDate");

						 errors.add(ActionErrors.GLOBAL_ERROR,error);
					}

				}
			}


			if(pplOSValue==0)
			{
				ActionError error=new ActionError("pplOsrequired");

				 errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if (pplOSValue!=0)
			{
				if(GenericValidator.isBlankOrNull(pplOsAsOnDateValue))
				{
					ActionError error=new ActionError("pplOsDaterequired");

					 errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
			}
			if(firstInstallmentDueDateValue==null || firstInstallmentDueDateValue.equals(""))
			{
				ActionError error=new ActionError("installmentDateRequired");

				 errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}
	}
  
	return errors.isEmpty();
}

/**
 * This method validates whether Outstanding amount has been when Bank Assistance
 * is chosen as Yes
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 *

 public static boolean checkBankOsEntry(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	String assistanceValue=ValidatorUtil.getValueAsString(bean, field.getProperty());

	String osAmtValue=field.getVarValue("osAmt");
	java.lang.Double osAmt=new Double(ValidatorUtil.getValueAsString(bean, osAmtValue));
	double osAmtVal=osAmt.doubleValue();

	if(assistanceValue.equals("N") && osAmtVal!=0)
	{
		ActionError error=new ActionError("osAmtNotrequired");

		  errors.add(ActionErrors.GLOBAL_ERROR,error);
	}

	return errors.isEmpty();
}


/**
 * This method validates the entry of outstanding Date when Outstanding amount is entered.
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 *

public static boolean osDateEntry(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
{
	java.lang.Double pplOsAmountVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
	double pplOsAmount=pplOsAmountVal.doubleValue();

	String pplOsAsOnDateVal= field.getVarValue("pplOsAsOnDate");
	String pplOsAsOnDateValue=ValidatorUtil.getValueAsString(bean,pplOsAsOnDateVal);

	if(pplOsAmount!=0)
	{
		if(GenericValidator.isBlankOrNull(pplOsAsOnDateValue))
		{
			ActionError actionMessage=new ActionError("pplOs");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);

		}
	}


	return errors.isEmpty();
}
*/

/**
 * This method checks whether promoter Name has been entered when Date of Birth
 * and ITPAN are entered
 */

	public static boolean validatePromoterEntry(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {

		String pName=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String pNameValue=field.getProperty();

		String pItpan=field.getVarValue("itpan");
		String pItpanValue=ValidatorUtil.getValueAsString(bean, pItpan);

		String pDob=field.getVarValue("dob");
		String pDobValue=ValidatorUtil.getValueAsString(bean, pDob);

		if(((pItpanValue!=null && !(pItpanValue.equals(""))) || (pDobValue!=null && !(pDobValue.equals(""))))
		&& (pName==null || pName.equals("")))
		{
			ActionError error=new ActionError(pNameValue + "required");

			 errors.add(ActionErrors.GLOBAL_ERROR,error);

		}

		return errors.isEmpty();
	  }

/**
 * This method checks if particiapting Banks have been entered if an MCGF User
 * has logged in.
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */

public static boolean validateMcgfBankEntry(Object bean, ValidatorAction validAction,
						Field field, ActionErrors errors, HttpServletRequest request)
  {

	HttpSession session=request.getSession(false);

	if(session.getAttribute(SessionConstants.MCGF_FLAG).equals("M"))
	{

		String pBankName=ValidatorUtil.getValueAsString(bean, field.getProperty());
		if(pBankName==null || pBankName.equals(""))
		{
			ActionError error=new ActionError("participatingBankrequired");

			 errors.add(ActionErrors.GLOBAL_ERROR,error);
		}
	}
	return errors.isEmpty();
  }


  /**
   * This method checks whether the From Date is before or equal to the To Date
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return
   */
  public static boolean validateFromToEqualDates(Object bean, ValidatorAction validAction,
						Field field, ActionErrors errors, HttpServletRequest request)throws Exception
  {
	  String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
	  String sProperty2=field.getVarValue("toDate");
	  String toValue=ValidatorUtil.getValueAsString(bean, sProperty2);

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	boolean fromDateValue = false;
	boolean toDateValue = false;

	if (!(GenericValidator.isBlankOrNull(fromValue)) && !(GenericValidator.isBlankOrNull(toValue)))
	{
		try{
			java.util.Date fromDate = (java.util.Date)sdf.parse(fromValue,new ParsePosition(0));
			if(fromDate==null)
			{
				fromDateValue = false;
			}
			else{

				fromDateValue = true;
			}

		}
		catch(Exception e)
		{
			fromDateValue = false;
		}

		try{

			java.util.Date toDate = (java.util.Date)sdf.parse(toValue,new ParsePosition(0));
			if(toDate==null)
			{
				toDateValue = false;
			}
			else{

				toDateValue = true;
			}

		}
		catch(Exception e)
		{
			toDateValue = false;
		}

		if(fromDateValue && toDateValue)
		{
			java.util.Date fromDate = (java.util.Date)sdf.parse(fromValue,new ParsePosition(0));
			java.util.Date toDate = (java.util.Date)sdf.parse(toValue,new ParsePosition(0));
			if(fromDate.compareTo(toDate)==1)
			{
				ActionError error=new ActionError("fromDate" + sProperty2);

				 errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
		}
	  }

	  return errors.isEmpty();
  }


/**
 * This method checks if the application has been entered if NPA is selected as Yes
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */

	public static boolean checkNpaEntry(Object bean, ValidatorAction validAction,
					  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String npaValue=ValidatorUtil.getValueAsString(bean, field.getProperty());

		if(npaValue.equals("Y"))
		{
			ActionError error=new ActionError("applicationNoEntry");

			 errors.add(ActionErrors.GLOBAL_ERROR,error);

		}

		return errors.isEmpty();
	}
  
/**
   * added by sukumar@path for validating the I Card Number when reimbursement is required 
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   * @throws java.lang.Exception
   */
public static boolean checkIcardEntry(Object bean, ValidatorAction validAction,
					  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		 String handiCrafts  = (String)request.getParameter("handiCrafts");
     String dcHandicrafts  = (String)request.getParameter("dcHandicrafts");
		if(handiCrafts.equals("Y") && dcHandicrafts.equals("Y"))
		{
			ActionError error=new ActionError("applicationIcardEntry");

			 errors.add(ActionErrors.GLOBAL_ERROR,error);

		}

		return errors.isEmpty();
	}
	/**
	 * This method checks if all the entered ITPANs are the same or not
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 *

	public static boolean checkItpanEntry(Object bean, ValidatorAction validAction,
					  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String itpan1Value=ValidatorUtil.getValueAsString(bean, field.getProperty());

		String itpan2=field.getVarValue("firstItpan");
		String itpan2Val=ValidatorUtil.getValueAsString(bean, itpan2);

		String itpan3=field.getVarValue("secondItpan");
		String itpan3Val=ValidatorUtil.getValueAsString(bean, itpan3);

		String itpan4=field.getVarValue("thirdItpan");
		String itpan4Val=ValidatorUtil.getValueAsString(bean, itpan4);

		String itpan5=field.getVarValue("cpITPAN");
		String itpan5Val=ValidatorUtil.getValueAsString(bean, itpan5);

		String itpan1 ="";
		String itpan2Value = "";
		String itpan3Value = "";
		String itpan4Value = "";
		String itpan5Value = "";

		if((itpan1Value!=null && !itpan1Value.equals(""))
			|| (itpan2Val!=null && !itpan2Val.equals(""))
			|| (itpan3Val!=null && !itpan3Val.equals(""))
			|| (itpan4Val!=null && !itpan4Val.equals(""))
			|| (itpan5Val!=null && !itpan5Val.equals("")))
		{
			if(itpan1Value!=null && !itpan1Value.equals(""))
			{
			itpan1 = itpan1Value.trim();
			}
			if(itpan2Val!=null && !itpan2Val.equals(""))
			{
			itpan2Value = itpan2Val.trim();
			}
			if(itpan3Val!=null && !itpan3Val.equals(""))
			{
				itpan3Value = itpan3Val.trim();
			}
			if(itpan4Val!=null && !itpan4Val.equals(""))
			{
				itpan4Value = itpan4Val.trim();
			}
			if(itpan5Val!=null && !itpan5Val.equals(""))
			{
				itpan5Value = itpan5Val.trim();
			}

			if((itpan1!=null && !itpan1.equals("")) && (itpan2Value!=null && !itpan2Value.equals("")) && itpan1.equals(itpan2Value)
				&& !itpan1.equalsIgnoreCase("APPLIED FO") && !itpan2Value.equalsIgnoreCase("APPLIED FO") && !itpan1.equalsIgnoreCase("N/A") && !itpan2Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("borrowerfirst");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan1!=null && !itpan1.equals("")) && (itpan3Value!=null && !itpan3Value.equals("")) && itpan1.equals(itpan3Value)
			&& !itpan1.equalsIgnoreCase("APPLIED FO") && !itpan3Value.equalsIgnoreCase("APPLIED FO") && !itpan1.equalsIgnoreCase("N/A") && !itpan3Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("borrowersecond");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan1!=null && !itpan1.equals("")) && (itpan4Value!=null && !itpan4Value.equals("")) && itpan1.equals(itpan4Value)
			&& !itpan1.equalsIgnoreCase("APPLIED FO") && !itpan4Value.equalsIgnoreCase("APPLIED FO") && !itpan1.equalsIgnoreCase("N/A") && !itpan4Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("borrowerthird");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan1!=null && !itpan1.equals("")) && (itpan5Value!=null && !itpan5Value.equals("")) && itpan1.equals(itpan5Value)
			&& !itpan1.equalsIgnoreCase("APPLIED FO") && !itpan5Value.equalsIgnoreCase("APPLIED FO") && !itpan1.equalsIgnoreCase("N/A") && !itpan5Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("borrowerPromoter");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan2Value!=null && !itpan2Value.equals("")) && (itpan3Value!=null && !itpan3Value.equals("")) && itpan2Value.equals(itpan3Value)
			&& !itpan2Value.equalsIgnoreCase("APPLIED FO") && !itpan3Value.equalsIgnoreCase("APPLIED FO") && !itpan2Value.equalsIgnoreCase("N/A") && !itpan3Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("firstSecond");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan2Value!=null && !itpan2Value.equals("")) && (itpan4Value!=null && !itpan4Value.equals("")) && itpan2Value.equals(itpan4Value)
			&& !itpan2Value.equalsIgnoreCase("APPLIED FO") && !itpan4Value.equalsIgnoreCase("APPLIED FO") && !itpan2Value.equalsIgnoreCase("N/A") && !itpan4Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("firstThird");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan2Value!=null && !itpan2Value.equals("")) && (itpan5Value!=null && !itpan5Value.equals("")) && itpan2Value.equals(itpan5Value)
			&& !itpan2Value.equalsIgnoreCase("APPLIED FO") && !itpan5Value.equalsIgnoreCase("APPLIED FO") && !itpan2Value.equalsIgnoreCase("N/A") && !itpan5Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("firstPromoter");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan3Value!=null && !itpan3Value.equals("")) && (itpan4Value!=null && !itpan4Value.equals("")) && itpan3Value.equals(itpan4Value)
			&& !itpan3Value.equalsIgnoreCase("APPLIED FO") && !itpan4Value.equalsIgnoreCase("APPLIED FO") && !itpan3Value.equalsIgnoreCase("N/A") && !itpan4Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("secondThird");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan3Value!=null && !itpan3Value.equals("")) && (itpan5Value!=null && !itpan5Value.equals(""))&& itpan3Value.equals(itpan5Value)
			&& !itpan3Value.equalsIgnoreCase("APPLIED FO") && !itpan5Value.equalsIgnoreCase("APPLIED FO") && !itpan3Value.equalsIgnoreCase("N/A") && !itpan5Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("secondPromoter");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
			if((itpan4Value!=null && !itpan4Value.equals("")) && (itpan5Value!=null && !itpan5Value.equals("")) && itpan4Value.equals(itpan5Value)
			&& !itpan4Value.equalsIgnoreCase("APPLIED FO") && !itpan5Value.equalsIgnoreCase("APPLIED FO") && !itpan4Value.equalsIgnoreCase("N/A") && !itpan5Value.equalsIgnoreCase("N/A"))
			{
				ActionError error=new ActionError("thirdPromoter");

				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}

		}
		return errors.isEmpty();
	}
*/
/**
 *  This method checks if the no of installments have been entered only incase of a
 *  new application
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 * @throws Exception
 */

	public static boolean checkInstallmentEntry(Object bean, ValidatorAction validAction,
					  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		//String installmentValue=ValidatorUtil.getValueAsString(bean, field.getProperty());

		java.lang.Integer installmentVal=new Integer(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		int installmentValue=installmentVal.intValue();

		HttpSession session=request.getSession(false);
		if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG)!=null && (session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("0")
		||((session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")) && (session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC")||
			session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("CC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("BO")))))
		{
			if(installmentValue==0)
			{
				ActionError actionError=new ActionError("noOfInstallmentsrequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}
		}

		return errors.isEmpty();
	}

	public static boolean checkEnhancedAmount(Object bean, ValidatorAction validAction,
					  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	  {
		String property= field.getProperty();
		java.lang.Double existingVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		double existingAmount=existingVal.doubleValue();

		String wcFundBasedVal= field.getVarValue("wcFundBasedSanctioned");
		java.lang.Double wcFundBasedValue=new Double(ValidatorUtil.getValueAsString(bean, wcFundBasedVal));
		double wcFundBased=wcFundBasedValue.doubleValue();

		String wcNonFundBasedVal= field.getVarValue("wcNonFundBasedSanctioned");
		java.lang.Double wcNonFundBasedValue=new Double(ValidatorUtil.getValueAsString(bean, wcNonFundBasedVal));
		double wcNonFundBased=wcNonFundBasedValue.doubleValue();

		String existingNFBVal= field.getVarValue("existingNonFundBasedTotal");
		java.lang.Double existingNFBValue=new Double(ValidatorUtil.getValueAsString(bean, existingNFBVal));
		double existingNFB=existingNFBValue.doubleValue();

		Log.log(Log.INFO,"Validator","checkEnhancedAmount","wcFundBased ;" + wcFundBased);
		Log.log(Log.INFO,"Validator","checkEnhancedAmount","existingAmount ;" + existingAmount);
		Log.log(Log.INFO,"Validator","checkEnhancedAmount","wcNonFundBased ;" + wcNonFundBased);
		Log.log(Log.INFO,"Validator","checkEnhancedAmount","existingNFB ;" + existingNFB);
		Log.log(Log.INFO,"Validator","checkEnhancedAmount","sum FB ;" + (wcFundBased + wcNonFundBased));
		Log.log(Log.INFO,"Validator","checkEnhancedAmount","sum NFB;" + (existingAmount + existingNFB));

		if(existingAmount < wcFundBased)
		{
			ActionError actionError=new ActionError("enhancedAmountGreater" + property);

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}
		if(wcNonFundBased<existingNFB)
		{
			ActionError actionError=new ActionError("enhancedAmountGreaterwcNonFundBasedSanctioned");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}
		if(existingAmount + wcNonFundBased <= wcFundBased + existingNFB)
		{
			ActionError actionError=new ActionError("enhancedSumGreater");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}

/*		if(existingAmount!=0 && wcFundBased!=0)
		{
			if(wcFundBased >= existingAmount)
			{
				ActionError actionError=new ActionError("enhancedAmountGreater" + property);

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}

		}
*/		return errors.isEmpty();
	  }

	  public static boolean validateAmtSanctioned(Object bean, ValidatorAction validAction,
						Field field, ActionErrors errors, HttpServletRequest request)throws Exception
		{
			String amtSanctioned=ValidatorUtil.getValueAsString(bean, field.getProperty());

			String pplOsAsOnDateVal= field.getVarValue("pplOsAsOnDate");
			String pplOsAsOnDateValue=ValidatorUtil.getValueAsString(bean,pplOsAsOnDateVal);

			String firstInstallmentDueDateVal= field.getVarValue("firstInstallmentDueDate");
			String firstInstallmentDueDateValue=ValidatorUtil.getValueAsString(bean,firstInstallmentDueDateVal);

			String finalDisDateVal= field.getVarValue("finalDate");
			String finalDisDate=ValidatorUtil.getValueAsString(bean,finalDisDateVal);

			String firstDisDateVal= field.getVarValue("firstDisbursementDate");
			String firstDBRDateValue=ValidatorUtil.getValueAsString(bean,firstDisDateVal);

			HttpSession session=request.getSession(false);

			if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("0")
			|| session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("10")
			||((session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")) && (session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC")||
			session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("CC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("BO"))))
			{

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				boolean amtSanctionedValue = false;
				boolean finaldateValue = false;
				boolean firstdateValue = false;
				boolean firstInstdateValue = false;
				boolean pplOsdateValue = false;


				if ((amtSanctioned!=null) && !(amtSanctioned.equals("")))
				{
					amtSanctionedValue = true;
					try{
						java.util.Date amtSanctionedDate = (java.util.Date)sdf.parse(amtSanctioned,new ParsePosition(0));
						if(amtSanctionedDate==null)
						{
							ActionError actionError=new ActionError("errors.date","Date of Sanction");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);

							amtSanctionedValue = false;

						}
						else{
							amtSanctionedValue = true;
								java.util.Date currentDate=new java.util.Date();
							amtSanctionedDate = (java.util.Date)sdf.parse(amtSanctioned,new ParsePosition(0));

								try{

									String stringDate=sdf.format(currentDate);

										if(amtSanctionedDate.compareTo(currentDate)==1)
										{
											ActionError actionError=new ActionError("currentDateamountSanctionedDate");
											errors.add(ActionErrors.GLOBAL_ERROR,actionError);


										}

								}catch(NumberFormatException numberFormatException){

									ActionError actionError=new ActionError("Date of Sanction is not a valid Date");
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);

								}
							}

					}
					catch(Exception n)
					{
						ActionError actionError=new ActionError("errors.date","Date of Sanction");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

						amtSanctionedValue = false;
					}

				}


				if ((pplOsAsOnDateValue!=null) && !(pplOsAsOnDateValue.equals("")))
				{
					pplOsdateValue = true;
					try{
						java.util.Date pplOsAsOnDate = (java.util.Date)sdf.parse(pplOsAsOnDateValue,new ParsePosition(0));
						if(pplOsAsOnDate==null)
						{
							pplOsdateValue = false;

						}
						else{

							pplOsdateValue = true;
						}
					}
					catch(Exception n)
					{
						pplOsdateValue = false;
					}

				}


				if ((finalDisDate!=null) && !(finalDisDate.equals("")))
				{
					finaldateValue = true;
					try{
						java.util.Date finalDisDateValue = (java.util.Date)sdf.parse(finalDisDate,new ParsePosition(0));
						if(finalDisDateValue==null)
						{
							finaldateValue = false;

						}
						else{
							finaldateValue = true;
						}
					}
					catch(Exception n)
					{
						finaldateValue = false;
					}

				}
				if (firstDBRDateValue!=null && !firstDBRDateValue.equals(""))
				{

					firstdateValue = true;
					try{
						Date firstDisDate = sdf.parse(firstDBRDateValue,new ParsePosition(0));
						if(firstDisDate==null)
						{
							firstdateValue = false;
						}
						else{

							firstdateValue = true;
						}
					}
					catch(Exception n)
					{
						firstdateValue = false;
					}

				}
				if(firstInstallmentDueDateValue!=null && !firstInstallmentDueDateValue.equals(""))
				{
					firstInstdateValue = true;
					try{
						Date firstInstallmentDueDate = sdf.parse(firstInstallmentDueDateValue,new ParsePosition(0));

						if(firstInstallmentDueDate==null)
						{
							firstInstdateValue = false;

						}
						else{

							firstInstdateValue = true;
						}
					}
					catch(NumberFormatException n)
					{

						firstInstdateValue = false;
					}

				}
				if(amtSanctionedValue)
				{
					Date amtSanctionedDate = sdf.parse(amtSanctioned,new ParsePosition(0));
					if(firstdateValue)
					{
						Date firstDBRDate = sdf.parse(firstDBRDateValue,new ParsePosition(0));

						if(amtSanctionedDate.compareTo(firstDBRDate)==1)
						{
							ActionError error=new ActionError("fromDatefirstDisbursementDate" );

							 errors.add(ActionErrors.GLOBAL_ERROR,error);

						}
					}
					if(pplOsdateValue)
					{
						java.util.Date pplOsAsOnDate = (java.util.Date)sdf.parse(pplOsAsOnDateValue,new ParsePosition(0));
						if(amtSanctionedDate.compareTo(pplOsAsOnDate)==1)
						{
							ActionError error=new ActionError("fromDatepplOsAsOnDate" );

							 errors.add(ActionErrors.GLOBAL_ERROR,error);

						}

					}
					if(finaldateValue)
					{
						java.util.Date finalDisDateValue = (java.util.Date)sdf.parse(finalDisDate,new ParsePosition(0));
						if(amtSanctionedDate.compareTo(finalDisDateValue)==1)
						{
							ActionError error=new ActionError("fromDatefinalDisbursementDate" );

							 errors.add(ActionErrors.GLOBAL_ERROR,error);

						}

					}

					if(firstInstdateValue)
					{
						if (! DateHelper.day1BeforeDay2(amtSanctioned, firstInstallmentDueDateValue))
						{
							ActionError actionMessage=new ActionError("fromDateGTfirstInstallmentDueDate");
							errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
						}


					}
				}

			}


			return errors.isEmpty();
		}


		/*
		 * This method if atleast one status has been selected
		 */
		 public static boolean checkTcConvStatus(Object bean, ValidatorAction validAction,
								 Field field, ActionErrors errors, HttpServletRequest request)
		{
			APForm apForm=(APForm)bean;

			Map tcStatus=apForm.getTcDecision();
			Set tcStatusSet=tcStatus.keySet();
			Iterator tcStatusIterator=tcStatusSet.iterator();

			Map tcCgpan=apForm.getTcCgpan();
			Set tcCgpanSet=tcCgpan.keySet();
			Iterator tcCgpanIterator=tcCgpanSet.iterator();

			boolean clearVal=false;

			if(tcStatus!=null && tcStatus.size()!=0)
			{
				while(tcStatusIterator.hasNext())
				{

					String key=(String)tcStatusIterator.next();

					if (request.getParameter("tcDecision("+key+")")!=null && tcStatus.get(key)!=null && !(tcStatus.get(key).equals("")))
					{
						clearVal=true;
						break;

					}
				}
			}

			if(!clearVal)
			{
				ActionError actionError=new ActionError("statusRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);


			}

			return errors.isEmpty();
		}


		/*
		 * This method if atleast one status has been selected
		 */
		 public static boolean checkWcConvStatus(Object bean, ValidatorAction validAction,
								 Field field, ActionErrors errors, HttpServletRequest request)
		{
			APForm apForm=(APForm)bean;

			Map wcStatus=apForm.getWcDecision();
			Set wcStatusSet=wcStatus.keySet();
			Iterator wcStatusIterator=wcStatusSet.iterator();

			Map wcCgpan=apForm.getWcCgpan();
			Set wcCgpanSet=wcCgpan.keySet();
			Iterator wcCgpanIterator=wcCgpanSet.iterator();

			boolean clearVal=false;


			if(wcStatus!=null && wcStatus.size()!=0)
			{
				while(wcStatusIterator.hasNext())
				{

					String key=(String)wcStatusIterator.next();

					if (!(wcStatus.get(key).equals("")))
					{
						clearVal=true;
						break;

					}
				}
			}

			if(!clearVal)
			{
				ActionError actionError=new ActionError("statusRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);


			}

			return errors.isEmpty();
		}


		/*
		 * This method if CGPAN has been entered when the decision is selected
		 */
		 public static boolean checkTcCgpanEntry(Object bean, ValidatorAction validAction,
								 Field field, ActionErrors errors, HttpServletRequest request)
		{
			APForm apForm=(APForm)bean;

			Map tcStatus=apForm.getTcDecision();
			Set tcStatusSet=tcStatus.keySet();
			Iterator tcStatusIterator=tcStatusSet.iterator();

			Map tcCgpan=apForm.getTcCgpan();
			Set tcCgpanSet=tcCgpan.keySet();
			Iterator tcCgpanIterator=tcCgpanSet.iterator();

			boolean clearVal=false;


			if(tcStatus!=null && tcStatus.size()!=0)
			{
				while(tcStatusIterator.hasNext())
				{

					String key=(String)tcStatusIterator.next();

					if ((request.getParameter("tcDecision("+key+")")!=null && tcStatus.get(key)!=null && tcStatus.get(key).equals("ATL")) && (tcCgpan.get(key)==null || tcCgpan.get(key).equals("")))
					{
						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();
							if(error.getKey().equals("cgpanRequired"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("cgpanRequired");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

					}
				}
			}

			return errors.isEmpty();
		}

		/*
		 * This method if CGPAN has been entered when the decision is selected
		 */
		 public static boolean checkWcCgpanEntry(Object bean, ValidatorAction validAction,
								 Field field, ActionErrors errors, HttpServletRequest request)
		{

			APForm apForm=(APForm)bean;

			Map wcStatus=apForm.getWcDecision();
			Set wcStatusSet=wcStatus.keySet();
			Iterator wcStatusIterator=wcStatusSet.iterator();

			Map wcCgpan=apForm.getWcCgpan();
			Set wcCgpanSet=wcCgpan.keySet();
			Iterator wcCgpanIterator=wcCgpanSet.iterator();

			boolean clearVal=false;


			if(wcStatus!=null && wcStatus.size()!=0)
			{
				while(wcStatusIterator.hasNext())
				{

					String key=(String)wcStatusIterator.next();

					if ((wcStatus.get(key).equals("WCE") || wcStatus.get(key).equals("WCR")) && (wcCgpan.get(key)==null || wcCgpan.get(key).equals("")))
					{
						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();
							if(error.getKey().equals("cgpanReqd"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("cgpanReqd");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

					}
				}
			}

			return errors.isEmpty();
		}

	/**
	 * This method checks whether credit to be guaranteed amount is greater than sanctioned amount
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */

	 public static boolean checkAmtDisbursedGreater(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{

		java.lang.Double creditVal=new Double(ValidatorUtil.getValueAsString(bean, field.getProperty()));
		String creditString=field.getProperty();
		double creditValue=creditVal.doubleValue();

		String termCreditSanctionedVal= field.getVarValue("termCreditSanctioned");
		java.lang.Double termCreditSanctioned=new Double(ValidatorUtil.getValueAsString(bean, termCreditSanctionedVal));
		double termCreditSanctionedValue=termCreditSanctioned.doubleValue();
		HttpSession session=request.getSession(false);

		if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("0")
		|| session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("10")||
		((session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("14")||session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("19")) && (session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("TC")||
			session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("CC")||session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE).equals("BO"))))
		{

			if(creditValue !=0 && termCreditSanctionedValue!=0)
			{
				if(creditValue > termCreditSanctionedValue)
				{
					ActionError error=new ActionError("amtDisbursedLess" + creditString);

					 errors.add(ActionErrors.GLOBAL_ERROR,error);
				}


			}
		}

		return errors.isEmpty();
	}

	public static boolean checkCoveredValueOnView(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)throws DatabaseException
	{
		String cgtsiCoveredValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String typeValue=field.getVarValue("noneValue");
		String typeVal=ValidatorUtil.getValueAsString(bean, typeValue);
		String unitTypeVal=field.getVarValue("unit");
		String unittypeValue=ValidatorUtil.getValueAsString(bean, unitTypeVal);

		HttpSession session=request.getSession(false);

		if (cgtsiCoveredValue.equals("Y"))
		{
			if(session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("7") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("8") ||
			session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("9") || session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG).equals("10"))
			{
				if (!typeVal.equals("cgpan") && !typeVal.equals("cgbid"))
				{
					ActionError error=new ActionError("cgpanCgbidSelected");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
				else if (typeVal.equals("cgpan") && (unittypeValue==null && unittypeValue.equals("")))
				{
					ActionError error=new ActionError("cgpanRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
				else if (typeVal.equals("cgbid") && (unittypeValue==null && unittypeValue.equals("")))
				{
					ActionError error=new ActionError("cgbidRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}


			}
		}

		return errors.isEmpty();
	}

/**************************************************************************************/


/*public static boolean checkDesignationExist(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {
			  DynaActionForm dynaForm=(DynaActionForm)bean;

			  String newDesigName=(String)dynaForm.get("newDesigName");
			  ArrayList designations=(ArrayList)dynaForm.get("designations");
			  int size=designations.size();
			  for(int i=0;i<size;i++){
				  String designation=(String)designations.get(i);
				  if((newDesigName.trim()).equalsIgnoreCase(designation))
				  {
					  ActionError actionError=new ActionError("Designationalreadyexists");

					  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				  }

			  }
		  return errors.isEmpty();
	  }
public static boolean checkExceptionExist(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
	  {
			  DynaActionForm dynaForm=(DynaActionForm)bean;

			  String newExceptionTitle=(String)dynaForm.get("newExceptionTitle");
			  ArrayList exceptionTitles=(ArrayList)dynaForm.get("exceptionTitles");
			  int size=exceptionTitles.size();
			  for(int i=0;i<size;i++){
				  String exception=(String)exceptionTitles.get(i);
				  if((newExceptionTitle.trim()).equalsIgnoreCase(exception))
				  {
					  ActionError actionError=new ActionError("Exceptionalreadyexists");

					  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				  }

			  }
		  return errors.isEmpty();
	  }
public static boolean checkAlertExist(Object bean, ValidatorAction validAction,
								Field field, ActionErrors errors, HttpServletRequest request)
		  {
				  DynaActionForm dynaForm=(DynaActionForm)bean;

				  String newAlert=(String)dynaForm.get("newAlert");
				  ArrayList alertTitles=(ArrayList)dynaForm.get("alertTitles");
				  int size=alertTitles.size();
				  for(int i=0;i<size;i++){
					  String alert=(String)alertTitles.get(i);
					  if((newAlert.trim()).equalsIgnoreCase(alert))
					  {
						  ActionError actionError=new ActionError("Alertalreadyexists");

						  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					  }

				  }
			  return errors.isEmpty();
		  }
public static boolean checkPlrExist(Object bean, ValidatorAction validAction,
								Field field, ActionErrors errors, HttpServletRequest request)
		  {
				  DynaActionForm dynaForm=(DynaActionForm)bean;

				  String newBankName=(String)dynaForm.get("newBankName");
				  ArrayList banks=(ArrayList)dynaForm.get("plrBanks");
				  int size=banks.size();
				  for(int i=0;i<size;i++){
					  String bank=(String)banks.get(i);
					  if((newBankName.trim()).equalsIgnoreCase(bank))
					  {
						  ActionError actionError=new ActionError("Plralreadyexists");

						  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					  }

				  }
			  return errors.isEmpty();
		  }*/



	/**
	 * This method is used to validate the sub scheme parameters screen screen.
	 *
	 * This method checks whether atleast one of the sub scheme parameters (state, mli, industry sector,
	 * gender, social category) is entered.
	 * An error is added if neither of the above sub scheme parameters are entered.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateSubSchemeParameters(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateSubSchemeParameters","Entered");

		String mliProperty = field.getVarValue("mli");
		String industryProperty = field.getVarValue("industry");
		String genderProperty = field.getVarValue("gender");
		String socialCategoryProperty = field.getVarValue("socialCategory");

		String stateValue = ValidatorUtil.getValueAsString(bean, field.getProperty());
		Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","state -- " + stateValue);
		String mliValue = ValidatorUtil.getValueAsString(bean, mliProperty);
		Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","mli -- " + mliValue);
		String industryValue = ValidatorUtil.getValueAsString(bean, industryProperty);
		Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","industry -- " + industryValue);
		String genderValue = ValidatorUtil.getValueAsString(bean, genderProperty);
		Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","gender -- " + genderValue);
		String socialCategoryValue = ValidatorUtil.getValueAsString(bean, socialCategoryProperty);
		Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","social category -- " + socialCategoryValue);

		if ((GenericValidator.isBlankOrNull(stateValue)) &&
			(GenericValidator.isBlankOrNull(mliValue)) &&
			(GenericValidator.isBlankOrNull(industryValue)) &&
			(GenericValidator.isBlankOrNull(genderValue)) &&
			(GenericValidator.isBlankOrNull(socialCategoryValue)))
		{
			Log.log(Log.DEBUG,"Validator","validateSubSchemeParameters","adding error message");
			ActionError actionMessage=new ActionError("subSchemeErrorMessage");
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		}

		Log.log(Log.INFO,"Validator","validateSubSchemeParameters","Exited");
		return errors.isEmpty();
	}

	/** This is method is used to validate that atleast one field(either Cgpan or SSi name) should  be entered .
	*An error is added if neither of the above  parameters are entered.
 * @param bean
 * @param validAction
 * @param field
 * @param errors
 * @param request
 * @return
 */
	public static boolean validateCgpanOrSSi(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;

		String cgpan = (String)dynaForm.get("enterCgpan");
		String ssi = (String)dynaForm.get("enterSSI");
		if(((ssi == null) || (ssi.equals(""))) && ((cgpan == null) || (cgpan.equals(""))))
		{
			ActionError actionError  = new ActionError("enterOne");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		return errors.isEmpty();
	}

	////////////////////// Methods for Claims Processing //////////////////////////////////

	////////////////////// Methods for Claims Processing //////////////////////////////////
	 public static boolean checkLegalForumName(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	 {
	     Log.log(4, "Validator", "checkLegalForumName", "Entered");
	     ClaimActionForm claimForm = (ClaimActionForm)bean;
	     String legalForumName = claimForm.getForumthrulegalinitiated();
	     Log.log(4, "Validator", "checkLegalForumName", (new StringBuilder()).append("legalForumName :").append(legalForumName).toString());
	     String legalForumNameOther = claimForm.getOtherforums();
	     Log.log(4, "Validator", "checkLegalForumName", (new StringBuilder()).append("legalForumNameOther :").append(legalForumNameOther).toString());
	     if(!GenericValidator.isBlankOrNull(legalForumName)){
	         if("Others".equals(legalForumName) && GenericValidator.isBlankOrNull(legalForumNameOther)){
	             ActionError error = new ActionError("legalForumRequired");
	             errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
	         }
	     }else{
	         ActionError error = new ActionError("legalForumRequired");
	         errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
	     }
	     String caseregnumber = claimForm.getCaseregnumber();
	     String filingDate = claimForm.getLegaldate();
	     if(!("Securitisation Act, 2002".equals(legalForumName))){
	         if(GenericValidator.isBlankOrNull(caseregnumber)){
	             ActionError error = new ActionError("caseregnumber");
	             errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
	         }
	         if(GenericValidator.isBlankOrNull(filingDate)){
	             ActionError error = new ActionError("legalDate");
	             errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
	         }
	     }
	 //        if(legalForumName == null || legalForumName.equals("") || legalForumName.equals("Others"))
	 //        {
	 //            Log.log(4, "Validator", "checkLegalForumName", "Forum Name is not selected.");
	 //            if(legalForumNameOther == null || legalForumNameOther.equals(""))
	 //            {
	 //                Log.log(4, "Validator", "checkLegalForumName", "Legal Forum is not entered");
	 //                ActionError error = new ActionError("legalForumRequired");
	 //                errors.add("org.apache.struts.action.GLOBAL_ERROR", error);
	 //            }
	 //        }
	     Log.log(4, "Validator", "checkLegalForumName", "Exited");
	     return errors.isEmpty();
	 }



	public static boolean validateClaimStatus(Object bean,ValidatorAction validAction,
						   Field field,ActionErrors errors,HttpServletRequest request)
   {

		  Log.log(Log.INFO,"Validator","validateClaimStatus","Entered");
     System.out.println("validateClaimStatus Entered");
		  ClaimActionForm claimForm=(ClaimActionForm)bean;

		  String claimRefNum=(String)claimForm.getClaimRefNum();
		  String enterCgpan=(String)claimForm.getEnterCgpan();
		 
		  if((claimRefNum==null || claimRefNum.equals("")) && (enterCgpan==null || enterCgpan.equals("")))
		  {
			    Log.log(Log.DEBUG,"Validator","validateClaimStatus","Forum Name is not selected.");

					ActionError error=new ActionError("cgpanorclaimref");

					errors.add(ActionErrors.GLOBAL_ERROR,error);
			 
		  }

		  Log.log(Log.INFO,"Validator","validateClaimStatus","Exited");

		  return errors.isEmpty();
   }








    public static boolean validateTCandWCDetails(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
        ClaimActionForm claimForm = (ClaimActionForm)bean;
        Vector cgpanDetails = claimForm.getCgpnDetails();
        HashMap hashmap = null;
        String cgpanType = null;
        boolean isTC = false;
        boolean isWC = false;
                if(cgpanDetails != null)
                {
			if(cgpanDetails.size() > 0)
			{
				for(int i=0; i<cgpanDetails.size(); i++)
				{
                    hashmap = (HashMap)cgpanDetails.elementAt(i);
                    // System.out.println("Printing hashmap :" + hashmap);
                    if(hashmap != null)
                    {
						cgpanType = (String)hashmap.get(ClaimConstants.CGPAN_LOAN_TYPE);
						if(cgpanType != null)
						{
							if(cgpanType.equals(ClaimConstants.CGPAN_TC_LOAN_TYPE))
							{
								isTC = true;
							}
							else if(cgpanType.equals(ClaimConstants.CGPAN_WC_LOAN_TYPE))
							{
                                isWC = true;
							}
						}
					}
				}
			}
		}

		// System.out.println("isTC :" + isTC);
		if(isTC)
		{
			validateTCDetailsFields(bean,validAction,field,errors,request);
		}
		// System.out.println("isWC :" + isWC);
		if(isWC)
		{
			validateWCDetailsFields(bean,validAction,field,errors,request);
		}

		return true;
	}

    private static boolean validateTCDetailsFields(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		ActionError actionError = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map tclastDisbursementDts = claimForm.getLastDisbursementDate();
		Set tclastDisbursementDtsSet = tclastDisbursementDts.keySet();
		Iterator tclastDisbursementDtsIterator = tclastDisbursementDtsSet.iterator();
		boolean isAmntFieldValid = true;
		boolean isDtFieldValid = true;
		int count = 0;
		while(tclastDisbursementDtsIterator.hasNext())
		{
			count++;
            String key = (String)tclastDisbursementDtsIterator.next();
            String lastDisbursmntField = (String)claimForm.getLastDisbursementDate(key);
            // System.out.println("lastDisbursmntField :" + lastDisbursmntField);
            if(lastDisbursmntField.equals(""))
            {
                 isDtFieldValid = false;
                 break;
			}
			else
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try
				{
					java.util.Date temp = (java.util.Date)sdf.parse(lastDisbursmntField,new ParsePosition(0));
					// System.out.println("Date of last disbursement is :" + temp);
					if(temp != null)
					{
						java.util.Date currDate = new java.util.Date();
						if((temp.compareTo(currDate)) > 0)
						{
							isDtFieldValid = false;
							break;
						}
						isDtFieldValid = true;
					}
					else if(temp == null)
					{
	                    isDtFieldValid = false;
     	                break;
					}
				}
				catch(Exception iaeException)
				{
                    // isDtFieldValid = false;
                    // break;
				}
			}
			String tcPrincipalField = (String)claimForm.getTcprincipal(key);
			if(tcPrincipalField.equals(""))
			{
				isAmntFieldValid = false;
				break;
			}
			else
			{
				  isAmntFieldValid = true;
			}
			String tcInterestChargesField = (String)claimForm.getTcInterestCharge(key);
            if(tcInterestChargesField.equals(""))
            {
				isAmntFieldValid = false;
				break;
			}
			else
			{
					isAmntFieldValid = true;
			}
			String osAsOnDtOfNPAField = (String)claimForm.getTcOsAsOnDateOfNPA(key);
			if(osAsOnDtOfNPAField.equals(""))
			{
				isAmntFieldValid = false;
				break;
			}
			else
			{
					isAmntFieldValid = true;
			}
			String osStatedInCivilSuitField = (String)claimForm.getTcOsAsStatedInCivilSuit(key);
			if(osStatedInCivilSuitField.equals(""))
			{
				isAmntFieldValid = false;
				break;
			}
			else
			{
					isAmntFieldValid = true;
			}
			String osAsOnLodgmntOfClaimField = (String)claimForm.getTcOsAsOnLodgementOfClaim(key);
			if(osAsOnLodgmntOfClaimField.equals(""))
			{
                isAmntFieldValid = false;
                break;
			}
			else
			{
					isAmntFieldValid = true;
			}
			if((((String)session.getAttribute("mainMenu")).equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR))) && ((session.getAttribute("subMenuItem")).equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR_SECOND_INSTALLMENT))))
			{
				String osAsOnLodgmntOfSecClaimField = (String)claimForm.getTcOsAsOnLodgementOfSecondClaim(key);
				if(osAsOnLodgmntOfSecClaimField.equals(""))
				{
					 isAmntFieldValid = false;
					 break;
				}
				else
				{
						isAmntFieldValid = true;
				}
			}
		}
		// System.out.println("Value of count is :" + count);
		if(count > 0)
		{
			if(!isDtFieldValid)
			{
				actionError  = new ActionError("invaliddtmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				actionError = new ActionError("invalidAmntMsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
			if(!isAmntFieldValid)
			{
				actionError = new ActionError("invalidAmntMsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		else
		{
				actionError  = new ActionError("invaliddtmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				actionError = new ActionError("invalidAmntMsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        // tclastDisbursementDts.clear();
		return errors.isEmpty();
	}

	private static boolean validateWCDetailsFields(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		ActionError actionError= null;
        Map wcOsAsOnNPADtls = claimForm.getWcOsAsOnDateOfNPA();
		Set wcOsAsOnNPADtlsSet = wcOsAsOnNPADtls.keySet();
		Iterator wcOsAsOnNPADtlsIterator = wcOsAsOnNPADtlsSet.iterator();
		String dtOfReleaseOfWC = claimForm.getDateOfReleaseOfWC();
		boolean isAmntFieldValid = true;
		int count = 0;
		while(wcOsAsOnNPADtlsIterator.hasNext())
		{
            String key = (String)wcOsAsOnNPADtlsIterator.next();
            String osnpawc = (String)claimForm.getWcOsAsOnDateOfNPA(key);
            if(osnpawc.equals(""))
            {
				isAmntFieldValid = false;
				break;
			}
			else
			{
                 isAmntFieldValid = true;
			}
			String oswccivilsuit = (String)claimForm.getWcOsAsStatedInCivilSuit(key);
			if(oswccivilsuit.equals(""))
			{
				isAmntFieldValid = false;
				break;
			}
			else
			{
					isAmntFieldValid = true;
			}
			String oswclodgement = (String)claimForm.getWcOsAsOnLodgementOfClaim(key);
			if(oswclodgement.equals(""))
			{
				isAmntFieldValid = false;
				break;
			}
			else
			{
				isAmntFieldValid = true;
			}
			if((((String)session.getAttribute("mainMenu")).equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR))) && ((session.getAttribute("subMenuItem")).equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR_SECOND_INSTALLMENT))))
			{
				String oswclodgementSecClm = (String)claimForm.getWcOsAsOnLodgementOfSecondClaim(key);
				if(oswclodgementSecClm.equals(""))
				{
					isAmntFieldValid = false;
					break;
				}
				else
				{
					isAmntFieldValid = true;
				}
			}
			count++;
		}
		if(count > 0)
		{
			if(!isAmntFieldValid)
			{
				  actionError = new ActionError("invalidwcamntmsg");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		else
		{
				  actionError = new ActionError("invalidwcamntmsg");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		return errors.isEmpty();
	}

	public static boolean validateRecoveryDetails(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateRecoveryDetails","Entered");
		ActionError actionError = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map recoveryDetails = claimForm.getCgpandetails();
		// System.out.println("Printing the Map :" + recoveryDetails);
		Set recoveryDetailsSet = recoveryDetails.keySet();
		Iterator recoveryDetailsIterator = recoveryDetailsSet.iterator();
		String dtOfSeekingOTS = claimForm.getDateOfSeekingOTS();
		boolean isTCPFieldValidFlag = true;
		boolean isWCPFieldValidFlag = true;
		boolean isTCIFieldValidFlag = true;
		boolean isWCIFieldValidFlag = true;
		boolean isRecoveyModeValid = true;
		boolean allFieldsValid = true;
		boolean isOTSFlag = true;
		// String lastRecoveryModeField = null;
		String thisfield = null;
		// double value = 0.0;
		int count = 0;
		String substring = "$recovery#key-".trim();
		String tcfield = "TC".trim();
		String wcfield = "WC".trim();
		// java.util.Collection coll = (java.util.Vector)recoveryDetails.values();
		 String prinField = null;
		 String interestField = null;
		 String amountField = null;
		 String otherChrgs = null;
		 String recModeField = null;
		 // int count = 1;
		 int num = -1;
		 Vector recDtls = new Vector();
		 HashMap dtl = null;
		 String lastCh = null;
		 String otsSeekingDate = claimForm.getDateOfSeekingOTS();

		while(recoveryDetailsIterator.hasNext())
		{
			 boolean elementAdded = false;
             String key = (String)recoveryDetailsIterator.next();
             if(key == null)
             {
				 continue;
			 }
             String ch = key.substring(key.length()-1, key.length());
             // System.out.println("Printing the index CH :" + ch);
			 if((key.indexOf("tcprincipal$recovery".trim())) >= 0)
			 {
				 prinField = (String)claimForm.getCgpandetails(key);
				 if(ch != null)
				 {
					 if(count == 0)
					 {
						  dtl = new HashMap();
						  dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
						  dtl.put(ClaimConstants.CLM_REC_TC_PRINCIPAL, prinField);
						  recDtls.addElement(dtl);
					 }
					 if(count > 0)
					 {
						 for(int i=0; i<recDtls.size(); i++)
						 {
							 HashMap tempMap = (HashMap)recDtls.elementAt(i);
							 String tempFlag = (String)tempMap.get(ClaimConstants.CLM_REC_FLAG);
							 if(tempFlag == null)
							 {
								 continue;
							 }
							 if(tempFlag.equals(ch))
							 {
								 tempMap = (HashMap)recDtls.remove(i);
								 tempMap.put(ClaimConstants.CLM_REC_TC_PRINCIPAL, prinField);
								 recDtls.addElement(tempMap);
								 elementAdded = true;
							 }
						 }
						 if(!elementAdded)
						 {
							 dtl = new HashMap();
							 dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
							 dtl.put(ClaimConstants.CLM_REC_TC_PRINCIPAL, prinField);
							 recDtls.addElement(dtl);
							 elementAdded = true;
						 }
					 }
				 }
			 }
			 if((key.indexOf("tcInterestCharges$recovery".trim())) >= 0)
			 {
				 interestField = (String)claimForm.getCgpandetails(key);
				 if(ch != null)
				 {
					 if(count == 0)
					 {
						  dtl = new HashMap();
						  dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
						  dtl.put(ClaimConstants.CLM_REC_TC_INTEREST, interestField);
						  recDtls.addElement(dtl);
					 }
					 if(count > 0)
					 {
						 for(int i=0; i<recDtls.size(); i++)
						 {
							 HashMap tempMap = (HashMap)recDtls.elementAt(i);
							 String tempFlag = (String)tempMap.get(ClaimConstants.CLM_REC_FLAG);
							 if(tempFlag == null)
							 {
								 continue;
							 }
							 if(tempFlag.equals(ch))
							 {
								 tempMap = (HashMap)recDtls.remove(i);
								 tempMap.put(ClaimConstants.CLM_REC_TC_INTEREST, interestField);
								 recDtls.addElement(tempMap);
								 elementAdded = true;
							 }
						 }
						 if(!elementAdded)
						 {
							 dtl = new HashMap();
							 dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
							 dtl.put(ClaimConstants.CLM_REC_TC_INTEREST, interestField);
							 recDtls.addElement(dtl);
							 elementAdded = true;
						 }
					 }
				 }
			 }
			 if((key.indexOf("wcAmount$recovery".trim())) >= 0)
			 {
				 amountField = (String)claimForm.getCgpandetails(key);
				 if(ch != null)
				 {
					 if(count == 0)
					 {
						  dtl = new HashMap();
						  dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
						  dtl.put(ClaimConstants.CLM_REC_WC_AMOUNT, amountField);
						  recDtls.addElement(dtl);
					 }
					 if(count > 0)
					 {
						 for(int i=0; i<recDtls.size(); i++)
						 {
							 HashMap tempMap = (HashMap)recDtls.elementAt(i);
							 String tempFlag = (String)tempMap.get(ClaimConstants.CLM_REC_FLAG);
							 if(tempFlag == null)
							 {
								 continue;
							 }
							 if(tempFlag.equals(ch))
							 {
								 tempMap = (HashMap)recDtls.remove(i);
								 tempMap.put(ClaimConstants.CLM_REC_WC_AMOUNT, amountField);
								 recDtls.addElement(tempMap);
								 elementAdded = true;
							 }
						 }
						 if(!elementAdded)
						 {
							 dtl = new HashMap();
							 dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
							 dtl.put(ClaimConstants.CLM_REC_WC_AMOUNT, amountField);
							 recDtls.addElement(dtl);
							 elementAdded = true;
						 }
					 }
				 }
			 }
			 if((key.indexOf("wcOtherCharges$recovery".trim())) >= 0)
			 {
				 otherChrgs = (String)claimForm.getCgpandetails(key);
				 if(ch != null)
				 {
					 if(count == 0)
					 {
						  dtl = new HashMap();
						  dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
						  dtl.put(ClaimConstants.CLM_REC_WC_OTHER, otherChrgs);
						  recDtls.addElement(dtl);
					 }
					 if(count > 0)
					 {
						 for(int i=0; i<recDtls.size(); i++)
						 {
							 HashMap tempMap = (HashMap)recDtls.elementAt(i);
							 String tempFlag = (String)tempMap.get(ClaimConstants.CLM_REC_FLAG);
							 if(tempFlag == null)
							 {
								 continue;
							 }
							 if(tempFlag.equals(ch))
							 {
								 tempMap = (HashMap)recDtls.remove(i);
								 tempMap.put(ClaimConstants.CLM_REC_WC_OTHER, otherChrgs);
								 recDtls.addElement(tempMap);
								 elementAdded = true;
							 }
						 }
						 if(!elementAdded)
						 {
							 dtl = new HashMap();
							 dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
							 dtl.put(ClaimConstants.CLM_REC_WC_OTHER, otherChrgs);
							 recDtls.addElement(dtl);
							 elementAdded = true;
						 }
					 }
				 }
			 }
			 if((key.indexOf("recoveryMode$recovery".trim())) >= 0)
			 {
				 recModeField = (String)claimForm.getCgpandetails(key);
				 if(ch != null)
				 {
					 if(count == 0)
					 {
						  dtl = new HashMap();
						  dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
						  dtl.put(ClaimConstants.CLM_REC_MODE, recModeField);
						  recDtls.addElement(dtl);
					 }
					 if(count > 0)
					 {
						 for(int i=0; i<recDtls.size(); i++)
						 {
							 HashMap tempMap = (HashMap)recDtls.elementAt(i);
							 String tempFlag = (String)tempMap.get(ClaimConstants.CLM_REC_FLAG);
							 if(tempFlag == null)
							 {
								 continue;
							 }
							 if(tempFlag.equals(ch))
							 {
								 tempMap = (HashMap)recDtls.remove(i);
								 tempMap.put(ClaimConstants.CLM_REC_MODE, recModeField);
								 recDtls.addElement(tempMap);
								 elementAdded = true;
							 }
						 }
						 if(!elementAdded)
						 {
							 dtl = new HashMap();
							 dtl.put(ClaimConstants.CLM_REC_FLAG, ch);
							 dtl.put(ClaimConstants.CLM_REC_MODE, recModeField);
							 recDtls.addElement(dtl);
							 elementAdded = true;
						 }
					 }
				 }
			 }
			 count++;
			 dtl = null;
		 }

		 // System.out.println("Size of the Vector :" + recDtls.size());
		 boolean isRecModeValid = true;
		 boolean OTSPresent = false;
		 String lastRecoveryModeField = null;
		 int counter = 0;
		 boolean jumpOutOfForLoop = false;
		 for(int k=0; k<recDtls.size(); k++)
		 {
			 HashMap map = (HashMap)recDtls.elementAt(k);
			 // System.out.println("Printing HashMap :" + map);
			 if(map == null)
			 {
				 continue;
			 }
			 Set mapSet = (Set)map.keySet();
			 Iterator mapIterator = mapSet.iterator();
			 boolean finalFieldsValidity = true;
			 boolean areFieldsValid = false;
			 Vector tempVec = new Vector();
			 while(mapIterator.hasNext())
			 {
				 String temFlag = (String)mapIterator.next();
				 // System.out.println("Printing Temp Flag :" + temFlag);
				 if(temFlag == null)
				 {
					 continue;
				 }
				 if(temFlag.equals(ClaimConstants.CLM_REC_FLAG))
				 {
					 continue;
				 }
				 String value = (String)map.get(temFlag);
				 /*
				 if((value != null) && (value.equals("0.0")))
				 {
					 continue;
				 }
				 */
				 if((value == null) || (value.equals("")))
				 {
					 value = "-";
				 }
				 // System.out.println("Value :" + value);
				 tempVec.addElement(value);
				 if(temFlag.equals(ClaimConstants.CLM_REC_MODE))
				 {
                     if(value != null)
                     {
						 if((value.indexOf("OTS".trim())) != -1)
						 {
							 // isRecModeValid = isRecModeValid || true;
							 OTSPresent = true;
						 }
					 }


					 if(counter == 0)
					 {
						 /*
						 if(OTSPresent)
						 {
					 		lastRecoveryModeField = value;
						 }
						 */
						 if(!value.equals("-"))
						 {
						 	lastRecoveryModeField = value;
						 }
					 }

                     // System.out.println("Value of Counter :" + counter);
					 // System.out.println("lastRecoveryModeField :" + lastRecoveryModeField);
					 // System.out.println("value :" + value);

					 if(counter > 0)
					 {
						if((lastRecoveryModeField != null) && (value != null && (!value.equals("-"))))
						{
							// System.out.println("Inner Loop - lastRecoveryModeField :" + lastRecoveryModeField);
							// System.out.println("Inner Loop - value :" + value);
							if((((lastRecoveryModeField.indexOf("OTS".trim())) == -1) && ((value.indexOf("OTS".trim())) != -1)) ||
							  (((lastRecoveryModeField.indexOf("OTS".trim())) != -1) && ((value.indexOf("OTS".trim())) == -1)))
							{
								isRecModeValid = false;
								// System.out.println(counter +" :" + isRecModeValid);
								jumpOutOfForLoop = true;
								break;
							}
					    }
						if(!value.equals("-"))
						{
						    lastRecoveryModeField = value;
						}
					 }
				 }
			 }
			 if(jumpOutOfForLoop)
			 {
				 break;
			 }
			 // System.out.println("Size of the temp Vector :" + tempVec.size());
			 // System.out.println("(String)tempVec.elementAt(0) :" + (String)tempVec.elementAt(0));
			 // System.out.println("(String)tempVec.elementAt(1) :" + (String)tempVec.elementAt(1));
			 // System.out.println("(String)tempVec.elementAt(2) :" + (String)tempVec.elementAt(2));
			 if(tempVec.size() == 3)
			 {
				 // System.out.println("Nothing is entered");
				 if((((String)tempVec.elementAt(0)).equals("-")) &&
				   (((String)tempVec.elementAt(1)).equals("-")) &&
				   (((String)tempVec.elementAt(2)).equals("-")))
				   {
					   // System.out.println("Nothing is entered");
					   areFieldsValid = true;
				   }

			 }
			 if(tempVec.size() == 3)
			 {
				 // System.out.println("Everything is entered");
				 if(((((String)tempVec.elementAt(0)) != null) && (!((String)tempVec.elementAt(0)).equals("-"))) &&
				   ((((String)tempVec.elementAt(1)) != null) && (!((String)tempVec.elementAt(1)).equals("-"))) &&
				   ((((String)tempVec.elementAt(2)) != null) && (!((String)tempVec.elementAt(2)).equals("-"))))
				   {
					   // System.out.println("Everything is entered");
					   areFieldsValid = true;
				   }
			 }
             // System.out.println("areFieldsValid flag :" + areFieldsValid);
			 if(!areFieldsValid)
			 {
				 // Throw the Error
				 actionError = new ActionError("validrecoverydetailsmsg");
				 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				 break;
			 }

			 // finalFieldsValidity = finalFieldsValidity && areFieldsValid;
			 // System.out.println("****************************************");
			 counter++;
			 map = null;
		 }
		 if(!isRecModeValid)
		 {
			 // Throw the Error
			 actionError = new ActionError("validrecoverymodemsg");
			 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		 }
		 if(OTSPresent)
		 {
			 if((otsSeekingDate == null) || (otsSeekingDate.equals("")))
			 {
				 actionError = new ActionError("invalidOTSDate");
				 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			 }
		 }
		 recDtls = null;

	   /*
		* Clearing all the Objects
		*/
		recoveryDetails = null;
		recoveryDetailsSet = null;
		recoveryDetailsIterator = null;
		dtOfSeekingOTS = null;
		thisfield = null;
		substring = null;
		tcfield = null;
		wcfield = null;
		prinField = null;
		interestField = null;
		amountField = null;
		otherChrgs = null;
		recModeField = null;
		recDtls = null;
		dtl = null;
		lastCh = null;
		otsSeekingDate = claimForm.getDateOfSeekingOTS();
        Log.log(Log.INFO,"Validator","validateRecoveryDetails","Exited");
		return errors.isEmpty();
	}


/**
   * 
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   */
    public static boolean validateSPGDetails(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
        ActionError actionError = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map asOnDtOfSanctionDtls = claimForm.getAsOnDtOfSanctionDtl();
		// System.out.println("asOnDtOfSanctionDtls :" + asOnDtOfSanctionDtls);
		Set asOnDtOfSanctionDtlsSet = asOnDtOfSanctionDtls.keySet();
		Iterator asOnDtOfSanctionDtlsIterator = asOnDtOfSanctionDtlsSet.iterator();
		// Map asOnNPADtls = claimForm.getAsOnDtOfNPA();
		Map asOnNPADtls = null;
		// System.out.println("asOnNPADtls :" + asOnNPADtls);
		// Set asOnNPADtlsSet = asOnNPADtls.keySet();
		Set asOnNPADtlsSet = null;
		// Iterator asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
		Iterator asOnNPADtlsIterator = null;
		// Map asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
		Map asOnLodgemntDtls = null;
		// System.out.println("asOnLodgemntDtls :" + asOnLodgemntDtls);
		// Set asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
		Set asOnLodgemntDtlsSet = null;
		// Iterator asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
		Iterator asOnLodgemntDtlsIterator = null;

		boolean isNetWorthFieldValid = true;
    
    
		boolean isLandfieldvalid = false;
		boolean ismcfieldvalid = false;
		boolean isbldgfieldvalid = false;
		boolean isofmafieldvalid = false;
		boolean iscurrassetsvalid = false;
		boolean isothersvalid = false;
		boolean isreasonforreductionfieldvalid = false;
    
    

		// int count = 0;
    
    boolean sanctionChk = true;
    boolean npaChk = true;
    boolean lodgementChk = true;
    
    
    

		while(asOnDtOfSanctionDtlsIterator.hasNext())
		{
			 String thisfieldSanction = "";
			 String thisfieldNPA = "";
			 String thisfieldClmLodgemnt = "";

             String keySanction = (String)asOnDtOfSanctionDtlsIterator.next();

             // Checking for Land
             if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("LAND keyNPA :" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);

				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
 //System.out.println("LAND :" + thisfieldSanction);
// System.out.println("LAND :" + thisfieldNPA);
// System.out.println("LAND :" + thisfieldClmLodgemnt);
// System.out.println("LAND :" + isLandfieldvalid);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					isLandfieldvalid = true;
      //  actionError = new ActionError("sapgforreasonforreduction");
		//	  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
        
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					isLandfieldvalid = true;
      //    System.out.println("Test Land Values");
      //    actionError = new ActionError("sapgforreasonforreduction");
			//  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
        
        /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             // Checking for Networth of Guarantor
             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				if(!thisfieldSanction.equals(""))
				{
					asOnNPADtls = claimForm.getAsOnDtOfNPA();
					asOnNPADtlsSet = asOnNPADtls.keySet();
					asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
					while(asOnNPADtlsIterator.hasNext())
					{
					   String keyNPA = (String)asOnNPADtlsIterator.next();
					   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
					   {
							thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
							if(thisfieldNPA.equals(""))
							{
								isNetWorthFieldValid = false;
							}
					   }
					}

					asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
					asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
					asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
					while(asOnLodgemntDtlsIterator.hasNext())
					{
					   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
					   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
					   {
							thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
							if(thisfieldClmLodgemnt.equals(""))
							{
								isNetWorthFieldValid = false;
							}
					   }
					}
				}
				else
				{
					isNetWorthFieldValid = false;
				}
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA BLDG:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
						// System.out.println("thisfieldNPA :" + thisfieldNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt BLDG :" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
// System.out.println("BLDG :" + thisfieldSanction);
// System.out.println("BLDG :" + thisfieldNPA);
// System.out.println("BLDG :" + thisfieldClmLodgemnt);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					isbldgfieldvalid = true;
				}
                if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					isbldgfieldvalid = true;
				}
        
        /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
        
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA MC:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt MC:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
// System.out.println("MC :" + thisfieldSanction);
// System.out.println("MC :" + thisfieldNPA);
// System.out.println("MC :" + thisfieldClmLodgemnt);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					ismcfieldvalid = true;
				}
                if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					ismcfieldvalid = true;
				}
        
        
       /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA OFMA:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt OFMA:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
// System.out.println("OFMA :" + thisfieldSanction);
// System.out.println("OFMA :" + thisfieldNPA);
// System.out.println("OFMA :" + thisfieldClmLodgemnt);

                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					isofmafieldvalid = true;
				}
                if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					isofmafieldvalid = true;
				}
        
        
        /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Integer.parseInt(thisfieldNPA) > Integer.parseInt(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA CURR:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
						if(thisfieldNPA.equals(""))
						{
							iscurrassetsvalid = false;
						}
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt CURR:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
						if(thisfieldClmLodgemnt.equals(""))
						{
							iscurrassetsvalid = false;
						}
				   }
				}
// System.out.println("CURR :" + thisfieldSanction);
// System.out.println("CURR :" + thisfieldNPA);
// System.out.println("CURR :" + thisfieldClmLodgemnt);

                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					iscurrassetsvalid = true;
				}
                if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					iscurrassetsvalid = true;
				}
        
        
         /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
        
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA OTHERS:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt OTHERS:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
// System.out.println("OTHERS :" + thisfieldSanction);
// System.out.println("OTHERS :" + thisfieldNPA);
// System.out.println("OTHERS :" + thisfieldClmLodgemnt);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
                {
					isothersvalid = true;
				}
                if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
                {
					isothersvalid = true;
				}
        
        /** 
         *  added by sukumar@path on 09-Mar-2011 for Validating Primary Security Details as on Dt of Sanction, 
         * as on Dt of NPA and as on Dt of Lodgement of Claim 
        */
        if((!thisfieldSanction.equals(""))){
          if(thisfieldNPA.equals("")){
             npaChk = false;
          } else if ((!thisfieldNPA.equals(""))){
             if(Double.parseDouble(thisfieldSanction) > Double.parseDouble(thisfieldNPA) ){
                npaChk = false;
             }
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Double.parseDouble(thisfieldNPA) > Double.parseDouble(thisfieldClmLodgemnt) ){
                lodgementChk = false;
             }
             
             }
             
          }
              
        } else if(thisfieldSanction.equals("")) {
        
            if ((!thisfieldNPA.equals(""))){
            
             if(thisfieldClmLodgemnt.equals("")){
               lodgementChk = false;
             } else if((!thisfieldClmLodgemnt.equals(""))) {
                if(Integer.parseInt(thisfieldNPA) > Integer.parseInt(thisfieldClmLodgemnt) ){
                lodgementChk = false;
              }    
        
           }        
         }
        }
        
        
        
        
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}
        
   // System.out.println("sanctionChk:"+sanctionChk);
   // System.out.println("npaChk"+npaChk);
  //  System.out.println("lodgementChk"+lodgementChk);
        
      if(sanctionChk == false){
        if(thisfieldSanction.equals("")){
          actionError = new ActionError("reasonforreductionmsgsanction");
			    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
        }
      
      }  
      
        if(npaChk == false){
        if(thisfieldNPA.equals("")){
          actionError = new ActionError("reasonforreductionmsgnpa");
			    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
        }
      
      }  
      
       if(lodgementChk == false){
        if(thisfieldClmLodgemnt.equals("")){
          actionError = new ActionError("reasonforreductionmsgclmlodgmnt");
			    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
        }
      
      }  
      
      
        
        
				if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")))
				{
					isreasonforreductionfieldvalid = true;
				}
				if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")))
				{
					isreasonforreductionfieldvalid = true;
				}
   
        
        
        
        
        
			 }
		}
// System.out.println("LAND :" + isLandfieldvalid);
// System.out.println("NETWORTH :" + isNetWorthFieldValid);
// System.out.println("BLDG :" + isbldgfieldvalid);

		if(!isLandfieldvalid)
		{
			  actionError = new ActionError("sapgforland");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        /*
		if(!isNetWorthFieldValid)
		{
			  actionError = new ActionError("sapgfornetworthofguarantor");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        */
		if(!isbldgfieldvalid)
		{
			  actionError = new ActionError("sapgforbldg");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		if(!ismcfieldvalid)
		{
			  actionError = new ActionError("sapgformachine");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!isofmafieldvalid)
		{
			  actionError = new ActionError("sapgforofma");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!iscurrassetsvalid)
		{
			  actionError = new ActionError("sapgforparticularassets");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!isothersvalid)
		{
			  actionError = new ActionError("sapgforotherassets");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!isreasonforreductionfieldvalid)
		{
			  actionError = new ActionError("sapgforreasonforreduction");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
    

    
    
    
    
    
    
    
        return errors.isEmpty();
	}
  
  
  
  
  
  

    public static boolean validateSPGDetailsAsOnSecondLodgement(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		// System.out.println("Control is in validateSPGDetailsAsOnSecondLodgement.");
        ActionError actionError = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;

		Map asOnDtOfSanctionDtls = claimForm.getAsOnDtOfSanctionDtl();
		Set asOnDtOfSanctionDtlsSet = asOnDtOfSanctionDtls.keySet();
		Iterator asOnDtOfSanctionDtlsIterator = asOnDtOfSanctionDtlsSet.iterator();

		Map asOnNPADtls = null;
		Set asOnNPADtlsSet = null;
		Iterator asOnNPADtlsIterator = null;

		Map asOnLodgemntDtls = null;
		Set asOnLodgemntDtlsSet = null;
		Iterator asOnLodgemntDtlsIterator = null;

		Map asOnDateOfLodgemntOfSecondClm = null;
		Set asOnDateOfLodgemntOfSecondClmSet = null;
		Iterator asOnDateOfLodgemntOfSecondClmIterator = null;

		Map amntRealizedThruDisposalOfSecurity = null;
		Set amntRealizedThruDisposalOfSecuritySet = null;
		Iterator amntRealizedThruDisposalOfSecIterator = null;

		boolean isNetWorthFieldValid = true;
		boolean isLandfieldvalid = false;
		boolean ismcfieldvalid = false;
		boolean isbldgfieldvalid = false;
		boolean isofmafieldvalid = false;
		boolean iscurrassetsvalid = false;
		boolean isothersvalid = false;
		boolean isreasonforreductionfieldvalid = false;

		while(asOnDtOfSanctionDtlsIterator.hasNext())
		{
			 String thisfieldSanction = "";
			 String thisfieldNPA = "";
			 String thisfieldClmLodgemnt = "";
			 String thisfieldSecClmLodgemnt = "";
			 String thisfieldAmntRealzedThruSec = "";

             String keySanction = (String)asOnDtOfSanctionDtlsIterator.next();

             // Checking for Land
             if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("LAND keyNPA :" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);

				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}

// System.out.println("thisfieldSanction :" + thisfieldSanction);
// System.out.println("thisfieldNPA :" + thisfieldNPA);
// System.out.println("thisfieldClmLodgemnt :" + thisfieldClmLodgemnt);
// System.out.println("thisfieldSecClmLodgemnt :" + thisfieldSecClmLodgemnt);
// System.out.println("thisfieldAmntRealzedThruSec :" + thisfieldAmntRealzedThruSec);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
					    isLandfieldvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
						isLandfieldvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             // Checking for Networth of Guarantor
             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				if(!thisfieldSanction.equals(""))
				{
					asOnNPADtls = claimForm.getAsOnDtOfNPA();
					asOnNPADtlsSet = asOnNPADtls.keySet();
					asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
					while(asOnNPADtlsIterator.hasNext())
					{
					   String keyNPA = (String)asOnNPADtlsIterator.next();
					   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
					   {
							thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
							if(thisfieldNPA.equals(""))
							{
								isNetWorthFieldValid = false;
							}
					   }
					}

					asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
					asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
					asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
					while(asOnLodgemntDtlsIterator.hasNext())
					{
					   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
					   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
					   {
							thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
							if(thisfieldClmLodgemnt.equals(""))
							{
								isNetWorthFieldValid = false;
							}
					   }
					}

					asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
					asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
					asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
					while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
					{
					   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
					   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
					   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR))
					   {
							thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
							if(thisfieldSecClmLodgemnt.equals(""))
							{
								isNetWorthFieldValid = false;
							}
					   }
					}
				}
				else
				{
					isNetWorthFieldValid = false;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				// thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA BLDG:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
						// System.out.println("thisfieldNPA :" + thisfieldNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt BLDG :" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}
// System.out.println("thisfieldSanction :" + thisfieldSanction);
// System.out.println("thisfieldNPA :" + thisfieldNPA);
// System.out.println("thisfieldClmLodgemnt :" + thisfieldClmLodgemnt);
// System.out.println("thisfieldSecClmLodgemnt :" + thisfieldSecClmLodgemnt);
// System.out.println("thisfieldAmntRealzedThruSec :" + thisfieldAmntRealzedThruSec);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
				    isbldgfieldvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
					isbldgfieldvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA MC:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt MC:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}
// System.out.println("MC :" + thisfieldSanction);
// System.out.println("MC :" + thisfieldNPA);
// System.out.println("MC :" + thisfieldClmLodgemnt);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
				    ismcfieldvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
					ismcfieldvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA OFMA:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt OFMA:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}
// System.out.println("OFMA :" + thisfieldSanction);
// System.out.println("OFMA :" + thisfieldNPA);
// System.out.println("OFMA :" + thisfieldClmLodgemnt);

                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
				    isofmafieldvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
					isofmafieldvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA CURR:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
						if(thisfieldNPA.equals(""))
						{
							iscurrassetsvalid = false;
						}
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt CURR:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
						if(thisfieldClmLodgemnt.equals(""))
						{
							iscurrassetsvalid = false;
						}
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}
// System.out.println("CURR :" + thisfieldSanction);
// System.out.println("CURR :" + thisfieldNPA);
// System.out.println("CURR :" + thisfieldClmLodgemnt);

                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
				    iscurrassetsvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
					iscurrassetsvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   // System.out.println("keyNPA OTHERS:" + keyNPA);
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   // System.out.println("keyClmLodgemnt OTHERS:" + keyClmLodgemnt);
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
				   }
				}

				amntRealizedThruDisposalOfSecurity = claimForm.getAmntRealizedThruDisposalOfSecurity();
				amntRealizedThruDisposalOfSecuritySet = amntRealizedThruDisposalOfSecurity.keySet();
				amntRealizedThruDisposalOfSecIterator = amntRealizedThruDisposalOfSecuritySet.iterator();
				while(amntRealizedThruDisposalOfSecIterator.hasNext())
				{
				   String amntRealzedThruSec = (String)amntRealizedThruDisposalOfSecIterator.next();
				   // System.out.println("keyClmLodgemnt LAND:" + keyClmLodgemnt);
				   if(amntRealzedThruSec.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))
				   {
						thisfieldAmntRealzedThruSec = (String)claimForm.getAmntRealizedThruDisposalOfSecurity(amntRealzedThruSec);
				   }
				}
// System.out.println("OTHERS :" + thisfieldSanction);
// System.out.println("OTHERS :" + thisfieldNPA);
// System.out.println("OTHERS :" + thisfieldClmLodgemnt);
                if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")) && (thisfieldAmntRealzedThruSec.equals("")))
                {
				    isothersvalid = true;
				}
                else if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")) && (!thisfieldAmntRealzedThruSec.equals("")))
                {
						isothersvalid = true;
				}
				thisfieldSanction = "";
				thisfieldNPA ="";
				thisfieldClmLodgemnt="";
				thisfieldSecClmLodgemnt="";
				thisfieldAmntRealzedThruSec = "";
			 }

             else if(keySanction.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
             {
				thisfieldSanction = (String)claimForm.getAsOnDtOfSanctionDtl(keySanction);
				asOnNPADtls = claimForm.getAsOnDtOfNPA();
				asOnNPADtlsSet = asOnNPADtls.keySet();
				asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
				while(asOnNPADtlsIterator.hasNext())
				{
				   String keyNPA = (String)asOnNPADtlsIterator.next();
				   if(keyNPA.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
				   {
						thisfieldNPA = (String)claimForm.getAsOnDtOfNPA(keyNPA);
				   }
				}

				asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
				asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
				asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
				while(asOnLodgemntDtlsIterator.hasNext())
				{
				   String keyClmLodgemnt = (String)asOnLodgemntDtlsIterator.next();
				   if(keyClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION))
				   {
						thisfieldClmLodgemnt = (String)claimForm.getAsOnLodgemntOfCredit(keyClmLodgemnt);
				   }
				}

				asOnDateOfLodgemntOfSecondClm = claimForm.getAsOnDateOfLodgemntOfSecondClm();
				asOnDateOfLodgemntOfSecondClmSet = asOnDateOfLodgemntOfSecondClm.keySet();
				asOnDateOfLodgemntOfSecondClmIterator = asOnDateOfLodgemntOfSecondClmSet.iterator();
				while(asOnDateOfLodgemntOfSecondClmIterator.hasNext())
				{
				   String keySecClmLodgemnt = (String)asOnDateOfLodgemntOfSecondClmIterator.next();
				   if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_LAND)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
					 else if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
					 else if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_MC)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
					 else if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
					 else if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
					 else if((keySanction.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS)) &&
					 (keySecClmLodgemnt.equals(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION+ClaimConstants.CLM_DELIMITER1 +ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS)))
					 {
						 thisfieldSecClmLodgemnt = (String)claimForm.getAsOnDateOfLodgemntOfSecondClm(keySecClmLodgemnt);
					 }
				 }
				 if((thisfieldSanction.equals("")) && (thisfieldNPA.equals("")) && (thisfieldClmLodgemnt.equals("")) && (thisfieldSecClmLodgemnt.equals("")))
				 {
					 isreasonforreductionfieldvalid = true;
				 }
				 if((!thisfieldSanction.equals("")) && (!thisfieldNPA.equals("")) && (!thisfieldClmLodgemnt.equals("")) && (!thisfieldSecClmLodgemnt.equals("")))
				 {
					 isreasonforreductionfieldvalid = true;
				 }
			 }
		}
// System.out.println("LAND FLAG:" + isLandfieldvalid);
// System.out.println("NETWORTH FLAG:" + isNetWorthFieldValid);
// System.out.println("BLDG FLAG:" + isbldgfieldvalid);
// System.out.println("LAND FLAG:" + isLandfieldvalid);
// System.out.println("NETWORTH :" + isNetWorthFieldValid);
// System.out.println("MACHINE FLAG :" + ismcfieldvalid);
// System.out.println("OFMA FLAG :" + isofmafieldvalid);
// System.out.println("CURR ASSETS FLAG :" + iscurrassetsvalid);
// System.out.println("OTHERS FLAG :" + isothersvalid);
// System.out.println("REASOn FLAG :" + isreasonforreductionfieldvalid);

		if(!isLandfieldvalid)
		{
			  actionError = new ActionError("sapgforlandSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        /*
		if(!isNetWorthFieldValid)
		{
			  actionError = new ActionError("sapgforNetwothOfGuarantorSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        */
		if(!isbldgfieldvalid)
		{
			  actionError = new ActionError("sapgforbldgSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		if(!ismcfieldvalid)
		{
			  actionError = new ActionError("sapgformachineSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!isofmafieldvalid)
		{
			  actionError = new ActionError("sapgforofmaSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!iscurrassetsvalid)
		{
			  actionError = new ActionError("sapgforparcurrSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

        if(!isothersvalid)
		{
			  actionError = new ActionError("sapgforOthersSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
        /*
        if(!isreasonforreductionfieldvalid)
		{
			  actionError = new ActionError("sapgforRsnForReductionSecClm");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		*/
        return errors.isEmpty();
	}

	public static boolean validateClaimAmounts(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		ActionError actionError = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map claimSummaryDtls = claimForm.getClaimSummaryDetails();
		Set claimSummaryDtlsSet = claimSummaryDtls.keySet();
		Iterator claimSummaryDtlsIterator = claimSummaryDtlsSet.iterator();
		boolean isValueValid = true;
		double value = 0.0;
		int counter = 0;
        while(claimSummaryDtlsIterator.hasNext())
        {
			String key = (String)claimSummaryDtlsIterator.next();
			String thisfield = (String)claimForm.getClaimSummaryDetails(key);
			if((thisfield != null) && (thisfield.equals("")))
			{
				isValueValid = false;
				break;
			}
			else if((thisfield != null) && (!thisfield.equals("")))
			{
				try
				{
                    double val = Double.parseDouble(thisfield);
                    if(val <= 0.0)
                    {
						break;
					}
				}
				catch(Exception e)
				{
				    break;
				}

			}
			counter++;
		}
		if( counter == 0)
		{
			actionError = new ActionError("invalidclaimamntmsg");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		else if(counter > 0)
		{
			if(!isValueValid)
			{
				actionError = new ActionError("invalidclaimamntmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}

	public static boolean validateBorrowerIdOrCgpan(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		 HttpSession session = (HttpSession)request.getSession(false);
         ClaimActionForm claimForm = (ClaimActionForm)bean;
         // System.out.println("ClaimForm :" + claimForm);
         ActionError actionError = null;
         String memid = null;
         // System.out.println("Member Id :" + memid);
         String borrowerid = null;
         // System.out.println("Borrower Id :" + borrowerid);
         String cgpan = null;
         // System.out.println("CGPAN :" + cgpan);

	 String srcmenu = (String)session.getAttribute("mainMenu");
	 // System.out.println("SRC MENU :" + srcmenu);
	 if(srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR)) ||
	    srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.CP_OTS)) ||
	    srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.CP_EXPORT_CLAIM_FILE)))
	 {
		 // System.out.println("1");
         memid = claimForm.getMemberId();
         // System.out.println("Member Id :" + memid);
         borrowerid = claimForm.getBorrowerID();
         // System.out.println("Borrower Id :" + borrowerid);
         cgpan = claimForm.getCgpan();
	 }
	 if(srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.GM_PERIODIC_INFO)))
	 {
		 // System.out.println("2");
		 memid = (String)session.getAttribute(ClaimConstants.CLM_MEMBER_ID);
		 // System.out.println("Member Id :" + memid);
		 borrowerid = (String)session.getAttribute(ClaimConstants.CLM_BORROWER_ID);
		 // System.out.println("Borrower Id :" + borrowerid);
		 claimForm.setMemberId(memid);
		 claimForm.setBorrowerID(borrowerid);
	 }


         if((memid == null) || (memid.equals("")))
         {
			 actionError  = new ActionError("memreqd");
             errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		 }
         if(((borrowerid == null) || (borrowerid.equals(""))) && ((cgpan == null) || (cgpan.equals(""))))
         {
   			   actionError  = new ActionError("enterbidorcgpan");
               errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		 }
		 return errors.isEmpty();
	}

	public static boolean validateOTSAmounts(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map proposedAmntsPaidByBorrower = claimForm.getProposedAmntPaidByBorrower();
		Set proposedAmntsPaidByBorrowerSet = proposedAmntsPaidByBorrower.keySet();
		Iterator proposedAmntsPaidByBorrowerIterator = proposedAmntsPaidByBorrowerSet.iterator();
		Map proposedAmntsSacrificed = claimForm.getProposedAmntSacrificed();
		Set proposedAmntsSacrificedSet = proposedAmntsSacrificed.keySet();
		Iterator proposedAmntsSacrificedIterator = proposedAmntsSacrificedSet.iterator();
		Map osAmntsOnDateForOTS = claimForm.getOsAmntOnDateForOTS();
		Set osAmntsOnDateForOTSSet = osAmntsOnDateForOTS.keySet();
		Iterator osAmntsOnDateForOTSIterator = osAmntsOnDateForOTSSet.iterator();
		boolean areFieldsValid = false;
		boolean proposedGreaterOs = false;
		boolean sacrificedAmntGreaterThanDiff = false;
		while(proposedAmntsPaidByBorrowerIterator.hasNext())
		{
			areFieldsValid = false;
			proposedGreaterOs = false;
			double amount = 0.0;
			String key = (String)proposedAmntsPaidByBorrowerIterator.next();
			String proposedValue = ((String)proposedAmntsPaidByBorrower.get(key)).trim();
			String sacrificedValue = ((String)proposedAmntsSacrificed.get(key)).trim();
			String osValue = ((String)osAmntsOnDateForOTS.get(key)).trim();

			if((proposedValue.equals("")) && (sacrificedValue.equals("")) && (osValue.equals("")))
			{
				areFieldsValid = true;
			}
			if((!proposedValue.equals("")) && (!sacrificedValue.equals("")) && (!osValue.equals("")))
			{
				areFieldsValid = true;
                double proposedVal = Double.parseDouble(proposedValue);
                double sacrificedVal = Double.parseDouble(sacrificedValue);
                double osVal = Double.parseDouble(osValue);
                if(proposedVal > osVal)
                {
                    proposedGreaterOs = true;
                    break;
				}
                if((osVal - proposedVal) < sacrificedVal)
                {
					sacrificedAmntGreaterThanDiff = true;
					break;
				}
			}
			if(!areFieldsValid)
			{
				break;
			}
		}
		if(!areFieldsValid)
		{
			ActionError actionError  = new ActionError("invalidOTSAmounts");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		if(proposedGreaterOs)
		{
			ActionError actionError  = new ActionError("proposedAmntGreaterThanOS");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		if(sacrificedAmntGreaterThanDiff)
		{
			ActionError actionError  = new ActionError("sacrificedAmntGreaterThanDiff");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		return errors.isEmpty();
	}

	public static boolean validateProposedAmntToBePaidByBorrower(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map proposedAmntsPaidByBorrower = claimForm.getProposedAmntPaidByBorrower();
		Set proposedAmntsPaidByBorrowerSet = proposedAmntsPaidByBorrower.keySet();
		Iterator proposedAmntsPaidByBorrowerIterator = proposedAmntsPaidByBorrowerSet.iterator();
		boolean proposedAmntToBePaidByBorrowerFlag = true;
		while(proposedAmntsPaidByBorrowerIterator.hasNext())
		{
			double amount = 0.0;
			String key = (String)proposedAmntsPaidByBorrowerIterator.next();
			String value = ((String)proposedAmntsPaidByBorrower.get(key)).trim();

			if(value.equals(""))
			{
				proposedAmntToBePaidByBorrowerFlag = false;
			}

			if(!proposedAmntToBePaidByBorrowerFlag)
			{
				ActionError actionError  = new ActionError("proposedamntpaidbyBorrower");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				break;
			}
		}
		return errors.isEmpty();
	}

	public static boolean validateProposedAmntSacrificed(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map proposedAmntsSacrificed = claimForm.getProposedAmntSacrificed();
		Set proposedAmntsSacrificedSet = proposedAmntsSacrificed.keySet();
		Iterator proposedAmntsSacrificedIterator = proposedAmntsSacrificedSet.iterator();
		boolean proposedAmntSacrificedFlag = true;
		while(proposedAmntsSacrificedIterator.hasNext())
		{
			double amount = 0.0;
			String key = (String)proposedAmntsSacrificedIterator.next();
			String value = ((String)proposedAmntsSacrificed.get(key)).trim();

			if(value.equals(""))
			{

				proposedAmntSacrificedFlag = false;
			}

			if(!proposedAmntSacrificedFlag)
			{
				ActionError actionError  = new ActionError("proposedamttobesacrificed");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				break;
			}
		}
		return errors.isEmpty();
	}

	public static boolean validateOsAmntOnDateForOTS(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
	{
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map osAmntsOnDateForOTS = claimForm.getOsAmntOnDateForOTS();
		Set osAmntsOnDateForOTSSet = osAmntsOnDateForOTS.keySet();
		Iterator osAmntsOnDateForOTSIterator = osAmntsOnDateForOTSSet.iterator();
		boolean osAmntOnDateForOTSFlag = true;
		while(osAmntsOnDateForOTSIterator.hasNext())
		{
			double amount = 0.0;
			String key = (String)osAmntsOnDateForOTSIterator.next();
			String value = ((String)osAmntsOnDateForOTS.get(key)).trim();

			if(value.equals(""))
			{
				osAmntOnDateForOTSFlag = false;
			}

			if(!osAmntOnDateForOTSFlag)
			{
				ActionError actionError  = new ActionError("osamntasondate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				break;
			}
		}
		return errors.isEmpty();
	}


	public static boolean validateRecoveryOrOTSFlag(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		 ClaimActionForm claimForm = (ClaimActionForm)bean;
		 String recoveryFlag = claimForm.getRecoveryFlag();
		 // System.out.println("recoveryFlag :" + recoveryFlag);

		 String otsFlag = claimForm.getOtsFlag();
		 // System.out.println("otsFlag :" + otsFlag);
		 if((recoveryFlag == null) && (otsFlag == null))
		 {
			ActionError error=new ActionError("enteraflag");

			errors.add(ActionErrors.GLOBAL_ERROR,error);

		 }
		 return errors.isEmpty();

	}

	public static boolean validateSaveOTSDetails(Object bean,ValidatorAction validAction,
									 Field field,ActionErrors errors,HttpServletRequest request)
		{
			ActionError actionError = null;
			ClaimActionForm claimForm = (ClaimActionForm)bean;
			Map decisions = claimForm.getDecision();
			Set decisionset = decisions.keySet();
			Iterator decisionIterator = decisionset.iterator();
			boolean isDecisionFieldValid = true;
			boolean isRemarksFieldValid = true;
			boolean areFieldsValid = false;
			String decisionfield = null;
			String remarksfield = null;
			double thisvalue = 0.0;
			int counter = 0;
			while(decisionIterator.hasNext())
			{
				counter++;
				areFieldsValid = false;
				String key = (String)decisionIterator.next();

	            decisionfield = (String)claimForm.getDecision(key);
	            // System.out.println("decisionfield :" + decisionfield);
	            remarksfield = (String)claimForm.getRemarks(key);
	            // System.out.println("remarksfield :" + remarksfield);
	            if((decisionfield.equals("")) && (remarksfield.equals("")))
	            {
					areFieldsValid = true;
				}
	            if((decisionfield.equals("AP")) && (remarksfield.equals("")))
	            {
					areFieldsValid = true;
				}
				if(!decisionfield.equals("") && !remarksfield.equals(""))
				{
					areFieldsValid = true;
				}
				if(!areFieldsValid)
				{
					break;
				}
			}
			if(counter == 0)
			{
				areFieldsValid = true;
			}
			if(counter > 0)
			{
				if(!areFieldsValid)
				{
					actionError = new ActionError("invaliddecisionmsg");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
			}
			/*
			if(!isRemarksFieldValid)
			{
				actionError = new ActionError("invalidremarksmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
			*/
	        return errors.isEmpty();
	}

	public static boolean validateDisplayClaimApproval(Object bean,ValidatorAction validAction,
								 Field field,ActionErrors errors,HttpServletRequest request)
	{
		double clmEligibleAmnt = 0.0;
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval","Entered");
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		String flag = (String)claimForm.getClmRefDtlSet();
		ClaimDetail clmdtl = claimForm.getClaimdetail();
		if(clmdtl != null)
		{
			clmEligibleAmnt = clmdtl.getEligibleClaimAmt();
		}
		String payAmntNow = (String)claimForm.getTotalAmntPayableNow();
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","*******************************");
		// Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","flagClmRefDtl :" + flag);
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","clmdtl :" + clmdtl);
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","clmEligibleAmnt :" + clmEligibleAmnt);
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","payAmntNow :" + payAmntNow);
		Log.log(Log.INFO,"Validator","validateDisplayClaimApproval()","*******************************");
        Log.log(Log.INFO,"Validator","validateDisplayClaimApproval","Exited");
        if((flag != null) && (flag.equals(ClaimConstants.DISBRSMNT_YES_FLAG)))
        {

			if((payAmntNow != null) &&(!payAmntNow.equals("")) &&(Double.parseDouble(payAmntNow)) > clmEligibleAmnt)
			{
				claimForm.setTotalAmntPayableNow(payAmntNow);
				ActionError actionError  = new ActionError("invalidClmPayable");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				Log.log(Log.INFO,"Validator","validateDisplayClaimApproval","claimForm.setTotalAmntPayableNow() :" + claimForm.getTotalAmntPayableNow());
				Log.log(Log.INFO,"Validator","validateDisplayClaimApproval","claimForm.getClmRefDtlSet() :" + claimForm.getClmRefDtlSet());
				return errors.isEmpty();
			}
		}
		return true;
   }

	public static boolean validateClaimProcessingDtls(Object bean,ValidatorAction validAction,
								 Field field,ActionErrors errors,HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Entered");
		ActionError actionerror = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map approvedClaimAmounts = claimForm.getApprovedClaimAmount();
		Set approvedClaimAmountsSet = approvedClaimAmounts.keySet();
        Iterator approvedClaimAmountIterator = approvedClaimAmountsSet.iterator();

        boolean areFieldsValid = false;
        double apprvdClmAmnt = 0.0;
        String decisionfield = null;
        String remarksfield = null;
        String clmamntfield = null;
        String forwardedToUser = null;
        int counter = 0;
        while(approvedClaimAmountIterator.hasNext())
        {
			counter++;
			String key = (String)approvedClaimAmountIterator.next();
			decisionfield = (String)claimForm.getDecision(key);
      System.out.println("decisionfield:"+decisionfield);
	//		Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","decisionfield :" + decisionfield);
            clmamntfield = (String)claimForm.getApprovedClaimAmount(key);
            Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","clmamntfield :" + clmamntfield);
            remarksfield = (String)claimForm.getRemarks(key);
            Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","remarksfield :" + remarksfield);
            forwardedToUser = (String)claimForm.getForwardedToIds(key);
            Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","forwardedToUser :" + forwardedToUser);
            Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 1");
            if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_REJECT_STATUS)) && (!remarksfield.equals("")) && (clmamntfield.equals("")))
            {
					areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 2");
			      }
            else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_HOLD_STATUS)) && (!remarksfield.equals("")) && (clmamntfield.equals("")))
            {
					areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 3");
			      }
            else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_FORWARD_STATUS)) && (!remarksfield.equals("")) && (clmamntfield.equals("")) && (forwardedToUser != null && (!forwardedToUser.equals(""))))
            {
					areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 4");
			      }
			    else if(((decisionfield == null) || (decisionfield != null && decisionfield.equals(""))) && (remarksfield.equals("")) && (clmamntfield.equals("")))
            {
				areFieldsValid = true;
				Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 5");
			   }
            else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_APPROVAL_STATUS)) && ((!clmamntfield.equals("")) && (Double.parseDouble(clmamntfield) > 0.0)))
            {
					areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 6");
			}
            else if((decisionfield != null && decisionfield.equals("")) && ((!remarksfield.equals("")) || (!clmamntfield.equals(""))))
            {
					areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 7");
			} 
      else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)) && (!remarksfield.equals("")) && (clmamntfield.equals("")))
      {
      	  areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 8");
      }
       else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)) && (remarksfield.equals("")) && (clmamntfield.equals("")))
      {
      	  areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 8");
      }
      else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_TEMPORARY_REJECT)) && (!remarksfield.equals("")) && (clmamntfield.equals("")))
      {
      	  areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 9");
      }
      else if((decisionfield != null && decisionfield.equals(ClaimConstants.CLM_WITHDRAWN)) && (!remarksfield.equals("")) && (clmamntfield.equals("")))
      {
      	  areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 10");
      }
			else
			{
				areFieldsValid = false;
				Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 11");
			}

			if(!areFieldsValid)
			{
				Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 12");
				actionerror = new ActionError("invalidapprvldecision");
				errors.add(ActionErrors.GLOBAL_ERROR,actionerror);
				break;
			}
		}
		Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Control 13");
		Log.log(Log.INFO,"Validator","validateClaimProcessingDtls","Exited");
		return errors.isEmpty();
	}
  
  
  
  
  
  
  
  public static boolean validateLtrRefNoDtls(Object bean,ValidatorAction validAction,
								 Field field,ActionErrors errors,HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Entered");
		ActionError actionerror = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map ltrRefNoSet = claimForm.getLtrRefNoSet();
		Set ltrRefNoList = ltrRefNoSet.keySet();
    Iterator ltrRefNoDtls = ltrRefNoList.iterator();

        boolean areFieldsValid = true;
        double apprvdClmAmnt = 0.0;
        String ltrRefNo = null;
        String ltrDt = null;   
        int counter = 0;
        while(ltrRefNoDtls.hasNext())
        {
			counter++;
			String key = (String)ltrRefNoDtls.next();
			ltrRefNo = (String)claimForm.getLtrRefNoSet(key);
    //  System.out.println("ltrRefNo:"+ltrRefNo);
      ltrDt = (String)claimForm.getLtrDtSet(key);
     // System.out.println("ltrDt:"+ltrDt);
      Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","ltrDt :" + ltrDt);
     
     if((ltrRefNo != null) && (!ltrRefNo.toString().equals(""))) 
     {
          areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Control 1"); 
     }
     else {
        areFieldsValid = false;
				Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Control 2");     
     }
     
     if((ltrDt != null) && (!ltrDt.toString().equals(""))) 
     {
          areFieldsValid = true;
					Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Control 1"); 
     }
     else {
        areFieldsValid = false;
				Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Control 2");     
     }

			if(!areFieldsValid)
			{
				Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Control 3");
				actionerror = new ActionError("invalidLtrRefDtls");
				errors.add(ActionErrors.GLOBAL_ERROR,actionerror);
				break;
			}
		}
	
		Log.log(Log.INFO,"Validator","validateLtrRefNoDtls","Exited");
		return errors.isEmpty();
	}
  
  
  
  
  
  
  
  

	public static boolean validateSettlementDetails(Object bean,ValidatorAction validAction,
							 Field field,ActionErrors errors,HttpServletRequest request)
	{
		Log.log(Log.INFO,"CPDAO","validateSettlementDetails","Entered");
		ActionError error = null;
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map settlementDetails = claimForm.getSettlementAmounts();
		Set settlementDetailsSet = settlementDetails.keySet();
		Iterator settlementDetailsIterator = settlementDetailsSet.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		String settlemntField = null;
		boolean areFieldsValid = true;
		double settlementAmnt = 0.0;
		String settlemntDtField = null;
		String finalInstallmentFlag = null;
		StringTokenizer tokenizer = null;
		int count = 0;
		String dayField = null;
		String monthField = null;
		String yearField = null;
		while(settlementDetailsIterator.hasNext())
		{
			boolean dayFieldRead = false;
			boolean monthFieldRead = false;
			boolean yearFieldRead = false;
			int day = -1;
			int month = -1;
			int year = -1;
			count++;
			String key = (String)settlementDetailsIterator.next();
			// System.out.println("Key in the Settlement Details :" + key);
			settlemntField = (String)claimForm.getSettlementAmounts(key);
			// System.out.println("1. settlemntField :" + settlemntField);
			settlemntDtField = (String)claimForm.getSettlementDates(key);
			tokenizer = new StringTokenizer(settlemntDtField,"/");
			// System.out.println("StringTokenizer :" + tokenizer);
			while(tokenizer.hasMoreTokens())
			{
				if(!yearFieldRead)
				{
					if(!monthFieldRead)
					{
						if(!dayFieldRead)
						{
							dayField = (String)tokenizer.nextToken();
							dayFieldRead = true;
							continue;
						}
						monthField = (String)tokenizer.nextToken();
						monthFieldRead = true;
						continue;
					}
					yearField = (String)tokenizer.nextToken();
					yearFieldRead = true;
					continue;
				}
			}
			// System.out.println("Day Field :" + dayField);
			// System.out.println("Month Field :" + monthField);
			// System.out.println("Year Field :" + yearField);
			// System.out.println("2. settlemntDtField :" + settlemntDtField);
			finalInstallmentFlag = (String)claimForm.getFinalSettlementFlags(key);
			// System.out.println("finalInstallmentFlag :" + finalInstallmentFlag);
			Log.log(Log.INFO,"CPDAO","validateSettlementDetails","1- settlemntField :" + settlemntField);
			Log.log(Log.INFO,"CPDAO","validateSettlementDetails","1- settlemntDtField :" + settlemntDtField);
			if((settlemntField.equals("0") || settlemntField.equals("0.0") || (settlemntField.equals(""))) && (settlemntDtField.equals("")))
			{
				Log.log(Log.INFO,"CPDAO","validateSettlementDetails","2- settlemntField :" + settlemntField);
				areFieldsValid = true;
			}
			/*
			if((settlemntField.equals("")) && (settlemntDtField.equals("")) && (finalInstallmentFlag == null))
			{
				areFieldsValid = true;
			}
			*/
			else if((!settlemntField.equals("0")) && (!settlemntDtField.equals("")))
			{
				try
				{
					// day = Integer.parseInt(dayField);
					// month = Integer.parseInt(monthField);
					// year = Integer.parseInt(yearField);
					/*
					if((1 <= day) && (day <= 31) && (1 <= month)&&(month <= 12) && (1 <= year)&&(year <= 9999))
					{
					*/
                        // areFieldsValid = true;
                        date = sdf.parse(settlemntDtField, new ParsePosition(0));
                        java.util.Date currDate = new java.util.Date();
                        if(date != null)
                        {
							if((date.compareTo(currDate)) > 0)
							{
								Log.log(Log.INFO,"CPDAO","validateSettlementDetails","3- settlemntField :" + settlemntField);
                                areFieldsValid = false;
							}
							else
							{
								Log.log(Log.INFO,"CPDAO","validateSettlementDetails","4- settlemntField :" + settlemntField);
								areFieldsValid = true;
							}
						}
						else
						{
							areFieldsValid = false;
						}
						if((Double.parseDouble(settlemntField)) < 0.0)
						{
							Log.log(Log.INFO,"CPDAO","validateSettlementDetails","5- settlemntField :" + settlemntField);
							areFieldsValid = false;
						}
					/*
					}
                    else
                    {
						Log.log(Log.INFO,"CPDAO","validateSettlementDetails","6- settlemntField :" + settlemntField);
						areFieldsValid = false;
					}
					*/
                    /*
					date = sdf.parse(settlemntDtField, new ParsePosition(0));
					// System.out.println("Parsed Date is :" + date);
					if(date != null)
					{
						areFieldsValid = true;
					}
					else if(date == null)
					{
	                    areFieldsValid = false;
     	                // break;
					}
					// areFieldsValid = true;
					*/
				}
				catch(Exception exception)
				{
					Log.log(Log.INFO,"CPDAO","validateSettlementDetails","7- settlemntField :" + settlemntField);
					exception.printStackTrace();
					areFieldsValid = false;
				}
			}
			else
			{
				Log.log(Log.INFO,"CPDAO","validateSettlementDetails","8- settlemntField :" + settlemntField);
				areFieldsValid = false;
			}

			if(!areFieldsValid)
			{
				error = new ActionError("invalidsettlementamntmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,error);
				break;
			}
		}
		// System.out.println("Value of count :" + count);
		/*
		if(count > 0)
		{
			if(!areFieldsValid)
			{
				error = new ActionError("invalidsettlementamntmsg");
				errors.add(ActionErrors.GLOBAL_ERROR,error);
			}
		}
		*/
        return errors.isEmpty();
	}

	public static boolean validateAllandSpecificFlag(Object bean,ValidatorAction validAction,
							 Field field,ActionErrors errors,HttpServletRequest request)
	{
		// System.out.println("Control in validateAllandSpecificFlag");
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		String memberIdFlag = claimForm.getMemberIdFlag();
		// System.out.println("MemberId Flag :" + memberIdFlag);
		String memberId = (claimForm.getMemberId()).trim();
		// System.out.println("MemberId :" + memberId);
		ActionError error = null;
		if(memberIdFlag.equals(""))
		{
			// System.out.println("Control 1");
			error = new ActionError("allorspecific");
			errors.add(ActionErrors.GLOBAL_ERROR,error);
			// return errors.isEmpty();
		}
		else if((memberIdFlag.equals(ClaimConstants.CLM_STLMNT_MEMBER_SPECIFIC)) && (memberId.equals("")))
        {
			// System.out.println("Control 2");
			error = new ActionError("specificnomemid");
			errors.add(ActionErrors.GLOBAL_ERROR,error);
			// return errors.isEmpty();
		}
		return errors.isEmpty();
	}

	public static boolean validateMemIdAndBid(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		 GMActionForm gmActionForm = (GMActionForm)bean;
		 HttpSession session = (HttpSession)request.getSession(false);
		 ActionError error = null;
		 String memid = null;
		 String bid = null;
		 // String srcmenu = (String)session.getAttribute(ClaimConstants.SRC_MAIN_MENU);
		 String srcmenu = (String)session.getAttribute("mainMenu");

		 if(srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.CP_CLAIM_FOR)))
		 {
			 memid = (String)session.getAttribute(ClaimConstants.CLM_MEMBER_ID);
			 bid = (String)session.getAttribute(ClaimConstants.CLM_BORROWER_ID);
			 gmActionForm.setMemberId(memid);
			 gmActionForm.setBorrowerId(bid);

			 // Validating the Member Id and Borrower Id
			 if((memid.equals("")) || (bid.equals("")))
			 {
				  Log.log(Log.INFO,"Validator","validateMemIdAndBid","Member Id is :" + memid);
				  Log.log(Log.INFO,"Validator","validateMemIdAndBid","Borrower Id is :" + bid);
				  error = new ActionError("entermemidandbid");
				  errors.add(ActionErrors.GLOBAL_ERROR,error);
			 }
		 }
		 if(srcmenu.equals(MenuOptions_back.getMenu(MenuOptions_back.GM_PERIODIC_INFO)))
		 {
   			 validateBIdOrCgpanOrBName(bean, validAction, field, errors, request);
		 }

		 /*
		 memid = gmActionForm.getMemberId();
		 bid = gmActionForm.getBorrowerId();
		 if((memid.equals("")) || (bid.equals("")))
		 {
              Log.log(Log.INFO,"Validator","validateMemIdAndBid","Member Id is :" + memid);
              Log.log(Log.INFO,"Validator","validateMemIdAndBid","Borrower Id is :" + bid);
              error = new ActionError("entermemidandbid");
              errors.add(ActionErrors.GLOBAL_ERROR,error);
		 }
		 */
		 Log.log(Log.INFO,"Validator","validateMemIdAndBid","Exited");
		 return errors.isEmpty();
	}

	public static boolean validateVoucherIds(Object bean,ValidatorAction validAction,
						 Field field,ActionErrors errors,HttpServletRequest request)
	{
		ActionError error = null;
		String voucherId = null;
		ClaimsProcessor processor = new ClaimsProcessor();
		boolean areVouchersInDB = true;
		boolean isVoucherValid = true;
		Vector voucherIdsFromDB = null;
		try
		{
			voucherIdsFromDB = processor.getAllVoucherIds();
			for(int i=0; i<voucherIdsFromDB.size(); i++)
			{
				// System.out.println("Voucher Id :" + (Integer)voucherIdsFromDB.elementAt(i));
			}
		}
		catch(DatabaseException dbex)
		{
           // Exception from the SP will be handled by CPDAO.
		}
		if(voucherIdsFromDB == null)
		{
            areVouchersInDB = false;
			error = new ActionError("novoucheridsindb");
			errors.add(ActionErrors.GLOBAL_ERROR,error);
			return errors.isEmpty();
		}
		else if(voucherIdsFromDB.size() == 0)
		{
            areVouchersInDB = false;
			error = new ActionError("novoucheridsindb");
			errors.add(ActionErrors.GLOBAL_ERROR,error);
			return errors.isEmpty();
		}
		ClaimActionForm claimForm = (ClaimActionForm)bean;
		Map paymentVoucherIds = claimForm.getPaymentVoucherIds();
		// System.out.println("Printing payment voucher ids :" + paymentVoucherIds);
		Set paymentVoucherIdsSet = paymentVoucherIds.keySet();
		Iterator paymentVoucherIdsIterator = paymentVoucherIdsSet.iterator();
        while(paymentVoucherIdsIterator.hasNext())
        {
             String key = (String)paymentVoucherIdsIterator.next();
             if(key == null)
             {
				 isVoucherValid = false;
				 break;
			 }
			 voucherId = (String)claimForm.getPaymentVoucherIds(key);

			 if((voucherId == null) || (voucherId.equals("")))
			 {
				 isVoucherValid = false;
				 break;
			 }
			 int lvoucherId = Integer.parseInt(voucherId);
			 // System.out.println("Voucher Id from the screen :" + lvoucherId);
			 if(voucherIdsFromDB.contains(new Integer(lvoucherId)))
			 {
				 isVoucherValid = true;
			 }
			 else
			 {
				 isVoucherValid = false;
			 }
		}
        if(!isVoucherValid)
	    {
			error = new ActionError("invalidvoucherid");
			errors.add(ActionErrors.GLOBAL_ERROR,error);
			return errors.isEmpty();
		}
		return errors.isEmpty();
	}
	////////////////////////// End of Methods for Claims Processing ///////////////////////

	/**
	 * This method is used to validate whether the integer or decimal value entered is zero.
	 *
	 * In case of amount or rate fields where a numerical value is expected,
	 * if the entered value is zero,an error is added.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateIfZero(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateIfZero","Entered");

		String fieldValue = ValidatorUtil.getValueAsString(bean, field.getProperty());
		Log.log(Log.DEBUG,"Validator","validateIfZero","value entered -- " + fieldValue);

		if (!GenericValidator.isBlankOrNull(fieldValue) && ((fieldValue.equals("0")) || (Double.parseDouble(fieldValue) == 0.0)))
		{
			Log.log(Log.DEBUG,"Validator","validateIfZero","adding error message");
			ActionError actionMessage=new ActionError("zeroNotAllowed");
//			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
		}

		Log.log(Log.INFO,"Validator","validateIfZero","Exited");
		return errors.isEmpty();
	}

	/**
	 * This method is used to validate application filing limit rule.
	 *
	 * If the application filing rule is Days and the no of days has not been entered,
	 * an error is added.
	 *
	 * If the application filing rule is Periodicity and the period is not entered,
	 * an error is added.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateAppFilingLimitRule(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateAppFilingLimitRule","Entered");

		String rule = ValidatorUtil.getValueAsString(bean, field.getProperty());
		Log.log(Log.DEBUG,"Validator","validateAppFilingLimitRule","filing rule -- " + rule);

		String limit;
		String limitValue;

		if (rule.equals("Days"))
		{
			limit = field.getVarValue("days");
			limitValue = ValidatorUtil.getValueAsString(bean, limit);
			if (limitValue.equals("0"))
			{
				Log.log(Log.DEBUG,"Validator","validateAppFilingLimitRule","No of days is zero");
				ActionError actionMessage=new ActionError("zeroAppFilingDaysNotAllowed");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}
		}
		else if (rule.equals("Periodicity"))
		{
			limit = field.getVarValue("period");
			limitValue = ValidatorUtil.getValueAsString(bean, limit);
			Log.log(Log.DEBUG,"Validator","validateAppFilingLimitRule","Period " + limitValue);
			if (limitValue.equals("0"))
			{
				Log.log(Log.DEBUG,"Validator","validateAppFilingLimitRule","Periodicity not selected");
				ActionError actionMessage=new ActionError("zeroAppFilingPeriodNotAllowed");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}
		}

		Log.log(Log.INFO,"Validator","validateAppFilingLimitRule","Exited");
		return errors.isEmpty();
	}

	/**
	 * This method is used to validate default rate applicable.
	 *
	 * If the default rate is chosen to be applicable and if the default rate, default rate valid from
	 * and default rate valid to dates are not entered, then an error is added.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateDefaultRate(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","validateDefaultRate","Entered");

		String applicable = ValidatorUtil.getValueAsString(bean, field.getProperty());
		Log.log(Log.DEBUG,"Validator","validateDefaultRate","default rate applicable -- " + applicable);

		String rate;
		String rateValue;
		String validFrom;
		String validFromValue;
		String validTo;
		String validToValue;

		if (applicable.equals("Y"))
		{
			rate = field.getVarValue("defaultRate");
			rateValue = ValidatorUtil.getValueAsString(bean, rate);
			if (Double.parseDouble(rateValue)==0.0 || Double.parseDouble(rateValue) > 99)
			{
				Log.log(Log.DEBUG,"Validator","validateDefaultRate","default rate invalid");
				ActionError actionMessage=new ActionError("invalidDefaultRate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}

			validFrom = field.getVarValue("validFromDate");
			validFromValue = ValidatorUtil.getValueAsString(bean, validFrom);
			validTo = field.getVarValue("validToDate");
			validToValue = ValidatorUtil.getValueAsString(bean, validTo);
			if(GenericValidator.isBlankOrNull(validFromValue))
			{
				Log.log(Log.DEBUG,"Validator","validateDefaultRate","default rate valid from is required");
				ActionError actionMessage=new ActionError("defFromDateRequired");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}
/*			if (GenericValidator.isBlankOrNull(validToValue))
			{
				Log.log(Log.DEBUG,"Validator","validateDefaultRate","default rate valid to is required");
				ActionError actionMessage=new ActionError("defToDateRequired");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}*/

			if (!GenericValidator.isBlankOrNull(validFromValue) && !GenericValidator.isBlankOrNull(validToValue))
			{
				if (! DateHelper.day1BeforeDay2(validFromValue, validToValue))
				{
					Log.log(Log.DEBUG,"Validator","validateDefaultRate","default rate valid from not earlier than valid to");
					ActionError actionMessage=new ActionError("fromDateGT" + validTo);
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
				}
			}
		}

		Log.log(Log.INFO,"Validator","validateDefaultRate","Exited");
		return errors.isEmpty();
	}


/*****************************************************************************/
/**********************For Guarantee maintenance *****************************/
/*****************************************************************************/
    public static boolean validateBIdOrCgpanOrBName(Object bean,
   							ValidatorAction validAction, Field field,
   							ActionErrors errors, HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateBIdOrCgpanOrBName","Enetred");
		GMActionForm gmActionForm = (GMActionForm)bean;
		String memId = gmActionForm.getMemberId().trim();
		String borrowerId = gmActionForm.getBorrowerId().trim();
		String cgpan = gmActionForm.getCgpan().trim();
		String borrowerName = gmActionForm.getBorrowerName();
		Log.log(Log.DEBUG,"Validator","bid --","-->"+borrowerId);
		boolean remainingFieldsValid = true;


		if( ((borrowerId == null) || (borrowerId.equals(""))) &&
		    ((cgpan == null) || (cgpan.equals(""))) &&
		    ((borrowerName == null) || (borrowerName.equals(""))) ) {
			  // ActionError actionError  = new ActionError("enterbidorcgpanorbName");
			  // errors.add(ActionErrors.GLOBAL_ERROR,actionError);
              remainingFieldsValid = false;
		}


		if( !((borrowerId==null) || (borrowerId.equals(""))) ) {
			if( (!((cgpan==null) || (cgpan.equals("")))) ||
			(!((borrowerName==null) || (borrowerName.equals(""))))) {
				// ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				// errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				remainingFieldsValid = false;
			}
		}else if( !((borrowerName==null) || (borrowerName.equals(""))) ) {
			if((!((cgpan==null) || (cgpan.equals("")))) ||
				(!((borrowerId==null) || (borrowerId.equals(""))))) {
				// ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				// errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				remainingFieldsValid = false;
			}
		}else if( !((cgpan==null) || (cgpan.equals(""))) ) {
			if( (!((borrowerName==null) || (borrowerName.equals("")))) ||
				(!((borrowerId==null) || (borrowerId.equals(""))))) {
				// ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				// errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				remainingFieldsValid = false;
			}
		}

		if(((memId == null) || (memId.equals(""))) && (remainingFieldsValid))
		{
				ActionError actionError  = new ActionError("entermemid");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		if(((memId == null) || (memId.equals(""))) && (!remainingFieldsValid))
		{
				ActionError actionError  = new ActionError("entermemidbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		if(((memId != null) && (!memId.equals(""))) && (!remainingFieldsValid))
		{
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}
		Log.log(Log.INFO,"Validator","validateBIdOrCgpanOrBName","Exited");
		return errors.isEmpty();
   }

   public static boolean validateIdsForSchedule(Object bean,
							ValidatorAction validAction, Field field,
							ActionErrors errors, HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateIdsForSchedule","Enetred");
		GMActionForm gmActionForm = (GMActionForm)bean;
		String borrowerId = gmActionForm.getBorrowerIdForSchedule().trim();
		String cgpan = gmActionForm.getCgpanForSchedule().trim();
		String borrowerName = gmActionForm.getBorrowerNameForSchedule().trim();
		Log.log(Log.DEBUG,"Validator","bid --","-->"+borrowerId);

		if( ((borrowerId == null) || (borrowerId.equals(""))) &&
			((cgpan == null) || (cgpan.equals(""))) &&
			((borrowerName == null) || (borrowerName.equals(""))) ) {
			  ActionError actionError  = new ActionError("enterbidorcgpanorbName");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}

		if( !((borrowerId==null) || (borrowerId.equals(""))) ) {
			if( (!((cgpan==null) || (cgpan.equals("")))) ||
			(!((borrowerName==null) || (borrowerName.equals(""))))) {
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}else if( !((borrowerName==null) || (borrowerName.equals(""))) ) {
			if( (!((cgpan==null) || (cgpan.equals("")))) ||
				(!((borrowerId==null) || (borrowerId.equals(""))))) {
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}else if( !((cgpan==null) || (cgpan.equals(""))) ) {
			if( (!((borrowerName==null) || (borrowerName.equals("")))) ||
				(!((borrowerId==null) || (borrowerId.equals(""))))) {
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}

		Log.log(Log.INFO,"Validator","validateIdsForSchedule","Exited");
		return errors.isEmpty();
   }


   public static boolean validateIdsForShifting(Object bean,
							ValidatorAction validAction, Field field,
							ActionErrors errors, HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateIdsForShifting","Enetred");
		GMActionForm gmActionForm = (GMActionForm)bean;
		String borrowerId = gmActionForm.getBorrowerIdForShifting();
		String cgpan = gmActionForm.getCgpanForShifting();

		Log.log(Log.DEBUG,"Validator","bid --","-->"+borrowerId);

		if( ((borrowerId == null) || (borrowerId.equals(""))) &&
			((cgpan == null) || (cgpan.equals(""))) )
		{
			  ActionError actionError  = new ActionError("enterbidorcgpan");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}else if( !borrowerId.equals("") && !cgpan.equals("") )
		{
		 	 ActionError actionError  = new ActionError("enterbidorcgpan");
		  	 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

	 Log.log(Log.INFO,"Validator","validateIdsForShifting","Exited");
	 return errors.isEmpty();
	}


   public static boolean validateIdsForModifyBorrDtl(Object bean,ValidatorAction validAction,
						Field field,ActionErrors errors,HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateIdsForModifyBorrDtl","Entered");
	   String mliID=ValidatorUtil.getValueAsString(bean, field.getProperty());

	   String cgbid=field.getVarValue("borrowerIdForModifyBorrDtl");
	   String cgbidVal=ValidatorUtil.getValueAsString(bean, cgbid);

	   String cgpan=field.getVarValue("cgpanForModifyBorrDtl");
	   String cgpanVal=ValidatorUtil.getValueAsString(bean, cgpan);

	   String borrowerName=field.getVarValue("borrowerNameForModifyBorrDtl");
	   String borrowerNameVal=ValidatorUtil.getValueAsString(bean, borrowerName);

	   if( GenericValidator.isBlankOrNull(cgbidVal) &&
	   		GenericValidator.isBlankOrNull(cgpanVal) &&
			GenericValidator.isBlankOrNull(borrowerNameVal) ) {

				ActionError error=new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,error);
	   }

	   if ( !(GenericValidator.isBlankOrNull(cgbidVal)) )
	   {
		   if ( !(GenericValidator.isBlankOrNull(cgpanVal)) ||
		   		!(GenericValidator.isBlankOrNull(borrowerNameVal)) )
		   {
				ActionError error=new ActionError("enterbidorcgpanorbName");
                errors.add(ActionErrors.GLOBAL_ERROR,error);

		   }
	   }
	   else if ( !GenericValidator.isBlankOrNull(cgpanVal) )
	   {
		   if ( !GenericValidator.isBlankOrNull(cgbidVal) ||
		   		!GenericValidator.isBlankOrNull(borrowerNameVal) )
		   {
			     ActionError error=new ActionError("enterbidorcgpanorbName");
				 errors.add(ActionErrors.GLOBAL_ERROR,error);

		   }
	   }
	   else if ( !GenericValidator.isBlankOrNull(borrowerNameVal) )
	   {
		   if ( !GenericValidator.isBlankOrNull(cgbidVal) ||
		   		 !GenericValidator.isBlankOrNull(cgpanVal) )
		   {
		         ActionError error=new ActionError("enterbidorcgpanorbName");
				 errors.add(ActionErrors.GLOBAL_ERROR,error);

		   }
	   }
		Log.log(Log.INFO,"Validator","validateIdsForModifyBorrDtl","Exited");
	   return errors.isEmpty();

   }

   public static boolean validateTcOutstanding(Object bean,
				    ValidatorAction validAction, Field field, ActionErrors errors,
				    HttpServletRequest request)throws Exception
   {
	Log.log(Log.INFO,"Validator","validateTcOutstanding","Entered");

	GMActionForm gmActionForm = (GMActionForm)bean;

	Map cgpanTcMap = gmActionForm.getCgpansForTc() ;
	Set cgpanTcSet = cgpanTcMap.keySet();
	Iterator cgpanTcIterator = cgpanTcSet.iterator() ;
	Log.log(Log.DEBUG,"Validator","validateTcOutstanding","cgpanTcMap "+cgpanTcMap.size());

	Map tcPrAmountMap = gmActionForm.getTcPrincipalOutstandingAmount();
	Set tcPrAmountSet = tcPrAmountMap.keySet();
	Iterator tcPrAmountIterator = tcPrAmountSet.iterator();

	Map tcDateMap = gmActionForm.getTcOutstandingAsOnDate() ;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	String cgpan = null;
	int count = 0;
	String tcKey = null;
	Date tcDate = null;
	boolean finalFlag = true;
	while(cgpanTcIterator.hasNext())
	{
		cgpan  = (String)cgpanTcMap.get(cgpanTcIterator.next()) ;
		Log.log(Log.DEBUG,"Validator","validateTcOutstanding","Inside CgpanIterator cgpan  :"+cgpan);

		tcPrAmountIterator = tcPrAmountSet.iterator() ;

		count = 0;
		boolean dateFlag = false;
		while(tcPrAmountIterator.hasNext())
		{
			tcPrAmountIterator.next();
			tcKey = cgpan+"-"+count;
			Log.log(Log.DEBUG,"Validator","validateTcOutstanding","key ->"+tcKey);

			if(tcKey !=null && !tcKey.equals("") && tcPrAmountMap.containsKey(tcKey) )
			{
				String tcPrAmtVal = (String)tcPrAmountMap.get(tcKey);

				String tcDateVal = (String)tcDateMap.get(tcKey);

				if(!tcDateVal.equals("") && tcPrAmtVal.equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateTcOutstanding","Key is matched  Date :");
					ActionError actionError  = new ActionError("enterTcOutAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				else if(!tcPrAmtVal.equals("") && tcDateVal.equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateTcOutstanding","Key is matched  Date :");
					ActionError actionError  = new ActionError("enterTcOutAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				else if(!tcPrAmtVal.equals("") && !tcDateVal.equals("")&& finalFlag)
				{
					java.util.Date currentDate=new java.util.Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					try{

						String stringDate=dateFormat.format(currentDate);
						if(!tcDateVal.equals("") && !tcPrAmtVal.equals(""))
						{

							if(DateHelper.compareDates(tcDateVal,stringDate)!=0 && DateHelper.compareDates(tcDateVal,stringDate)!=1)
								{
									ActionError actionError=new ActionError("futureDate",tcDateVal);
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);


								}
						}
					}catch(NumberFormatException numberFormatException){

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();

							System.out.println(error.getKey());

							if(error.getKey().equals("errors.date"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("errors.date","Term Loan Outstanding As On Date");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

/*
								ActionError actionError=new ActionError("errors.date","Term Loan Outstanding Date");
								errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
					}
				}

			}
			++count;
		}
	}

	Log.log(Log.INFO,"Validator","validateTcOutstanding","Exited");
	   return errors.isEmpty();
   }

   public static boolean validateTcOutstandings(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	Log.log(Log.INFO,"Validator","validateTcOutstandings","Entered");

	GMActionForm gmActionForm = (GMActionForm)bean;

	Map cgpanTcMap = gmActionForm.getCgpansForTc() ;
	Set cgpanTcSet = cgpanTcMap.keySet();
	Iterator cgpanTcIterator = cgpanTcSet.iterator() ;
	Log.log(Log.DEBUG,"Validator","validateTcOutstandings","cgpanTcMap "+cgpanTcMap.size());

	Map tcPrAmountMap = gmActionForm.getTcPrincipalOutstandingAmount();
	Set tcPrAmountSet = tcPrAmountMap.keySet();
	Iterator tcPrAmountIterator = tcPrAmountSet.iterator();

	Map tcDateMap = gmActionForm.getTcOutstandingAsOnDate() ;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	String cgpan = null;
	int count = 0;
	String tcKey = null;
	Date tcDate = null;
	boolean dateFlag = false;
	while(cgpanTcIterator.hasNext())
	{
		cgpan  = (String)cgpanTcMap.get(cgpanTcIterator.next()) ;
		Log.log(Log.DEBUG,"Validator","validateTcOutstandings","Inside CgpanIterator cgpan  :"+cgpan);

		tcPrAmountIterator = tcPrAmountSet.iterator() ;

		Log.log(Log.DEBUG,"Validator","validateTcOutstandings","tcPrAmountMap"+tcPrAmountMap.size());

		count = 0;

		while(tcPrAmountIterator.hasNext())
		{
			tcPrAmountIterator.next();
			tcKey = cgpan+"-"+count;
			Log.log(Log.DEBUG,"Validator","validateTcOutstandings","key ->"+tcKey);

			if(tcKey !=null && !tcKey.equals("") && tcPrAmountMap.containsKey(tcKey) )
			{
				String tcPrAmtVal = (String)tcPrAmountMap.get(tcKey);

				String tcDateVal = (String)tcDateMap.get(tcKey);

				if(!tcDateVal.equals("") && !tcPrAmtVal.equals(""))
				{
					dateFlag=true;
					break;
/*					ActionError actionError  = new ActionError("enterTcOutAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/				}

			}
			++count;
		}
	}
	if(cgpanTcMap.size()>0 && !dateFlag)
	{
		ActionError actionError  = new ActionError("enterTcOutValues");
		errors.add(ActionErrors.GLOBAL_ERROR,actionError);

	}


	Log.log(Log.INFO,"Validator","validateTcOutstandings","Exited");
	   return errors.isEmpty();
   }


   public static boolean validateWcOutstanding(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)throws Exception
   {
	Log.log(Log.INFO,"Validator","validateWcOutstanding","Entered");

	GMActionForm gmActionForm = (GMActionForm)bean;

	java.util.Map cgpanWcMap = gmActionForm.getCgpansForWc() ;
	java.util.Set cgpanWcSet = cgpanWcMap.keySet();
	java.util.Iterator cgpanWcIterator = cgpanWcSet.iterator() ;
	Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","cgpanWcMap "+cgpanWcMap.size());

	Map wcFBPrAmountMap = gmActionForm.getWcFBPrincipalOutstandingAmount();
	Set wcFBPrAmountSet = wcFBPrAmountMap.keySet();
	Iterator wcFBPrAmountIterator = wcFBPrAmountSet.iterator();

	Map wcNFBPrAmountMap = gmActionForm.getWcNFBPrincipalOutstandingAmount();
	Set wcNFBPrAmountSet = wcNFBPrAmountMap.keySet();
	Iterator wcNFBPrAmountIterator = wcNFBPrAmountSet.iterator();

	Map wcFBIntAmountMap = gmActionForm.getWcFBInterestOutstandingAmount();

	Map wcNFBIntAmountMap = gmActionForm.getWcNFBInterestOutstandingAmount();

	Map wcFBDateMap = gmActionForm.getWcFBOutstandingAsOnDate() ;

	Map wcNFBDateMap = gmActionForm.getWcNFBOutstandingAsOnDate() ;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	String cgpan = null;
	int count = 0;
	String wcKey = null;
	boolean finalFlag = true;
	Date wcDate = null;

	Date wcNFBDate = null;
	while(cgpanWcIterator.hasNext())
	{
		cgpan  = (String)cgpanWcMap.get(cgpanWcIterator.next()) ;
		Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Inside CgpanIterator cgpan  :"+cgpan);

		wcFBPrAmountIterator = wcFBPrAmountSet.iterator() ;
		boolean dateFlag = false;
		count = 0;
		while(wcFBPrAmountIterator.hasNext())
		{
			wcFBPrAmountIterator.next();
			wcKey = cgpan+"-"+count;
			Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","key ->"+wcKey);

			if(wcKey !=null && !wcKey.equals("") && wcFBPrAmountMap.containsKey(wcKey) )
			{
				String wcFBPrAmtVal = (String)wcFBPrAmountMap.get(wcKey);

				String wcFBIntAmtVal =(String)wcFBIntAmountMap.get(wcKey);

				String wcFBDateVal =  (String)wcFBDateMap.get(wcKey);

				String wcNFBPrAmtVal = (String)wcNFBPrAmountMap.get(wcKey);

				String wcNFBIntAmtVal =(String)wcNFBIntAmountMap.get(wcKey);

				String wcNFBDateVal =  (String)wcNFBDateMap.get(wcKey);

			//	System.out.println("wcFBPrAmtVal :" + wcFBPrAmtVal);
		//		System.out.println("wcFBIntAmtVal :" + wcFBIntAmtVal);
		//		System.out.println("wcFBDateVal :" + wcFBDateVal);

		//		System.out.println("wcFBPrAmtVal :" + (!wcFBPrAmtVal.equals("") && Double.parseDouble(wcFBPrAmtVal)>0));
		//		System.out.println("wcFBIntAmtVal :" + (!wcFBIntAmtVal.equals("") && Double.parseDouble(wcFBIntAmtVal)>0));
		//		System.out.println("wcFBDateVal :" + (wcFBDateVal!=null && wcFBDateVal.toString().equals("")));



				if(((!wcFBPrAmtVal.equals("") && Double.parseDouble(wcFBPrAmtVal)>=0)
					|| (!wcFBIntAmtVal.equals("") && Double.parseDouble(wcFBIntAmtVal)>=0)) && (wcFBDateVal!=null && wcFBDateVal.toString().equals("")))
				{

					System.out.println("entering");

/*					ActionError actionError=new ActionError("errors.required","Working Capital Outstanding As On Date ");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/

					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();

				//		System.out.println(error.getKey());

						if(error.getKey().equals("errors.required"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("errors.required","Working Capital Outstanding As On Date ");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}


				}
				else if(wcFBDateVal!=null && !wcFBDateVal.toString().equals("") &&
				(wcFBPrAmtVal.equals("")
					|| wcFBIntAmtVal.equals("")))
					{
						ActionError actionError  = new ActionError("enterWcOutAmtandDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					}
				else if(wcFBDateVal!=null && !wcFBDateVal.toString().equals("") && finalFlag)
				{
			//		System.out.println("entering 6");

					java.util.Date currentDate=new java.util.Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					try{
						wcDate = simpleDateFormat.parse(wcFBDateVal,new ParsePosition(0));
						if(wcDate==null)
						{
							dateFlag = false;

							boolean remarksVal = false;

							Iterator errorsIterator =errors.get();

							while(errorsIterator.hasNext())
							{
								ActionError error=(ActionError)errorsIterator.next();

					//			System.out.println(error.getKey());

								if(error.getKey().equals("errors.date"))
								{
									remarksVal = true;
									break;
								}
							}
							if(!remarksVal)
							{

								ActionError actionError=new ActionError("errors.date","Working Capital Outstanding As On Date ");

								errors.add(ActionErrors.GLOBAL_ERROR,actionError);
							}

							finalFlag = false;

						}else
						{
							dateFlag = true;

							try{

								String stringDate=dateFormat.format(currentDate);
								if(!wcFBDateVal.equals("") && !wcFBPrAmtVal.equals(""))
								{

									//System.out.println("date :" + wcFBDateVal);
										if(DateHelper.compareDates(wcFBDateVal,stringDate)!=0 && DateHelper.compareDates(wcFBDateVal,stringDate)!=1)
										{
											ActionError actionError=new ActionError("futureDate",wcFBDateVal);
											errors.add(ActionErrors.GLOBAL_ERROR,actionError);


										}
								}
							}catch(NumberFormatException numberFormatException){

								boolean remarksVal = false;

								Iterator errorsIterator =errors.get();

								while(errorsIterator.hasNext())
								{
									ActionError error=(ActionError)errorsIterator.next();

									System.out.println(error.getKey());

									if(error.getKey().equals("errors.date"))
									{
										remarksVal = true;
										break;
									}
								}
								if(!remarksVal)
								{

									ActionError actionError=new ActionError("errors.date","Working Capital Outstanding As On Date ");

									errors.add(ActionErrors.GLOBAL_ERROR,actionError);
								}
							}


						}
					}catch(NumberFormatException numberFormatException){

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();

							System.out.println(error.getKey());

							if(error.getKey().equals("errors.date"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("errors.date","Working Capital Outstanding As On Date ");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

				}

				}
        
        if((wcNFBPrAmtVal==null||wcNFBPrAmtVal.equals(""))||(wcNFBIntAmtVal==null||wcNFBIntAmtVal.equals(""))){
         System.out.println("wcNFBPrAmtVal and  wcNFBIntAmtVal is null");
        
        } else if(((!wcNFBPrAmtVal.equals("") && Double.parseDouble(wcNFBPrAmtVal)>=0)
					|| (!wcNFBIntAmtVal.equals("") && Double.parseDouble(wcNFBIntAmtVal)>=0)) && (wcNFBDateVal!=null && wcNFBDateVal.toString().equals("")))
				{

/*					ActionError actionError=new ActionError("errors.required","Working Capital Outstanding As On Date ");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/

					boolean remarksVal = false;

					Iterator errorsIterator =errors.get();

					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();

						if(error.getKey().equals("errors.required"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{

						ActionError actionError=new ActionError("errors.required","Working Capital Non Fund Based Outstanding As On Date ");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}


				}
				else if(wcNFBDateVal!=null && !wcNFBDateVal.toString().equals("") &&
				(wcNFBPrAmtVal.equals("")
					|| wcNFBIntAmtVal.equals("")))
					{
						ActionError actionError  = new ActionError("enterWcOutAmtandDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					}
				else if(wcNFBDateVal!=null && !wcNFBDateVal.toString().equals("") && finalFlag)
				{


					java.util.Date currentDate=new java.util.Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					try{
						wcDate = simpleDateFormat.parse(wcNFBDateVal,new ParsePosition(0));
						if(wcDate==null)
						{
							dateFlag = false;

							boolean remarksVal = false;

							Iterator errorsIterator =errors.get();

							while(errorsIterator.hasNext())
							{
								ActionError error=(ActionError)errorsIterator.next();

								if(error.getKey().equals("errors.date"))
								{
									remarksVal = true;
									break;
								}
							}
							if(!remarksVal)
							{

								ActionError actionError=new ActionError("errors.date","Working Capital Non Fund Based Outstanding As On Date ");

								errors.add(ActionErrors.GLOBAL_ERROR,actionError);
							}

							finalFlag = false;

						}else
						{
							dateFlag = true;

							try{

								String stringDate=dateFormat.format(currentDate);
								if(!wcNFBDateVal.equals("") && !wcNFBPrAmtVal.equals(""))
								{

									//System.out.println("date :" + wcFBDateVal);
										if(DateHelper.compareDates(wcNFBDateVal,stringDate)!=0 && DateHelper.compareDates(wcNFBDateVal,stringDate)!=1)
										{
											ActionError actionError=new ActionError("futureDate",wcNFBDateVal);
											errors.add(ActionErrors.GLOBAL_ERROR,actionError);


										}
								}
							}catch(NumberFormatException numberFormatException){

								boolean remarksVal = false;

								Iterator errorsIterator =errors.get();

								while(errorsIterator.hasNext())
								{
									ActionError error=(ActionError)errorsIterator.next();

									if(error.getKey().equals("errors.date"))
									{
										remarksVal = true;
										break;
									}
								}
								if(!remarksVal)
								{

									ActionError actionError=new ActionError("errors.date","Working Capital Non Fund Based Outstanding As On Date ");

									errors.add(ActionErrors.GLOBAL_ERROR,actionError);
								}
							}


						}
					}catch(NumberFormatException numberFormatException){

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();

							if(error.getKey().equals("errors.date"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("errors.date","Working Capital Non Fund Based Outstanding As On Date ");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

				}

				}


			}
			++count;
		}
	}


	Log.log(Log.INFO,"Validator","validateWcOutstanding","Exited");
	   return errors.isEmpty();
   }


   public static boolean validateWcOutstandings(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	Log.log(Log.INFO,"Validator","validateWcOutstanding","Entered");

	GMActionForm gmActionForm = (GMActionForm)bean;

	java.util.Map cgpanWcMap = gmActionForm.getCgpansForWc() ;
	java.util.Set cgpanWcSet = cgpanWcMap.keySet();
	java.util.Iterator cgpanWcIterator = cgpanWcSet.iterator() ;
	Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","cgpanWcMap "+cgpanWcMap.size());

	Map wcFBPrAmountMap = gmActionForm.getWcFBPrincipalOutstandingAmount();
	Set wcFBPrAmountSet = wcFBPrAmountMap.keySet();
	Iterator wcFBPrAmountIterator = wcFBPrAmountSet.iterator();

	Map wcNFBPrAmountMap = gmActionForm.getWcNFBPrincipalOutstandingAmount();
	Set wcNFBPrAmountSet = wcNFBPrAmountMap.keySet();
	Iterator wcNFBPrAmountIterator = wcNFBPrAmountSet.iterator();

	Map wcFBIntAmountMap = gmActionForm.getWcFBInterestOutstandingAmount();

	Map wcNFBIntAmountMap = gmActionForm.getWcNFBInterestOutstandingAmount();

	Map wcFBDateMap = gmActionForm.getWcFBOutstandingAsOnDate() ;

	Map wcNFBDateMap = gmActionForm.getWcNFBOutstandingAsOnDate() ;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	String cgpan = null;
	int count = 0;
	String wcKey = null;
	boolean dateFlag = false;
	Date wcDate = null;
	while(cgpanWcIterator.hasNext())
	{
		cgpan  = (String)cgpanWcMap.get(cgpanWcIterator.next()) ;
		Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Inside CgpanIterator cgpan  :"+cgpan);

		wcFBPrAmountIterator = wcFBPrAmountSet.iterator() ;

		count = 0;
		while(wcFBPrAmountIterator.hasNext())
		{
			wcFBPrAmountIterator.next();
			wcKey = cgpan+"-"+count;
			Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","key ->"+wcKey);

			if(wcKey !=null && !wcKey.equals("") && wcFBPrAmountMap.containsKey(wcKey) )
			{
				String wcFBPrAmtVal = (String)wcFBPrAmountMap.get(wcKey);

				String wcFBIntAmtVal =(String)wcFBIntAmountMap.get(wcKey);

				String wcFBDateVal =  (String)wcFBDateMap.get(wcKey);

				String wcNFBPrAmtVal = (String)wcNFBPrAmountMap.get(wcKey);

				String wcNFBIntAmtVal =(String)wcNFBIntAmountMap.get(wcKey);

				String wcNFBDateVal =  (String)wcNFBDateMap.get(wcKey);


				if((!wcFBPrAmtVal.equals("") && !wcFBIntAmtVal.equals("") &&
										!wcFBDateVal.equals(""))||
				(!wcNFBPrAmtVal.equals("") && !wcNFBIntAmtVal.equals("") &&
									!wcNFBDateVal.equals("")))
				{
					dateFlag=true;
					break;
/*					ActionError actionError  = new ActionError("enterWcOutValues");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/				}

			}
			++count;
		}
	}

	if(cgpanWcMap.size()>0 && !dateFlag)
	{
		ActionError actionError  = new ActionError("enterWcOutValues");
		errors.add(ActionErrors.GLOBAL_ERROR,actionError);

	}

	Log.log(Log.INFO,"Validator","validateWcOutstanding","Exited");
	   return errors.isEmpty();
   }



/*   public static boolean validateTcInterestOutstandingAmount(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	Log.log(Log.INFO,"Validator","validateTcInterestOutstandingAmount","Entered");
	   GMActionForm gmActionForm = (GMActionForm)bean;
	   Map tcIntOsAmt = gmActionForm.getTcInterestOutstandingAmount();
	   Set tcIntOsAmtSet = tcIntOsAmt.keySet();
	   Iterator tcIntOsAmtIterator = tcIntOsAmtSet.iterator();
	   boolean tcIntOsAmtFlag = false;
	   while(tcIntOsAmtIterator.hasNext())
	   {
		   double amount = 0.0;
		   String key = (String)tcIntOsAmtIterator.next();
		   String value = ((String)tcIntOsAmt.get(key)).trim();

		   try
		   {
			   if(!value.equals(""))
			   {
				   amount = Double.parseDouble(value);
				   tcIntOsAmtFlag = true;
			   }
			   else
			   {
				tcIntOsAmtFlag = false;
			   }
		   }
		   catch(NumberFormatException numberformatexception)
		   {
				tcIntOsAmtFlag = false;
		   }
		   if(!tcIntOsAmtFlag)
		   {
			   ActionError actionError  = new ActionError("enterTcIntOsAmnt");
			   errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			   break;
		   }
	   }
	Log.log(Log.INFO,"Validator","validateTcInterestOutstandingAmount","Entered");
	   return errors.isEmpty();
   }
*/

  public static boolean validateDisbursementAmount(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)throws Exception
   {
	Log.log(Log.INFO,"Validator","validateDisbursementAmount","Entered");
	GMActionForm gmActionForm = (GMActionForm)bean;

	java.util.Map cgpanMap = gmActionForm.getCgpans() ;
	java.util.Set cgpanSet = cgpanMap.keySet();
	java.util.Iterator cgpanIterator = cgpanSet.iterator() ;
	Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","cgpan Map size = "+cgpanMap.size() );

	Map disbAmtMap = gmActionForm.getDisbursementAmount();
	Set disbAmtSet = disbAmtMap.keySet();
	Iterator disbAmtIterator = disbAmtSet.iterator();

	Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Amount map size = "+disbAmtMap.size() );

	Map disbDateMap = gmActionForm.getDisbursementDate();
	Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Date map size = "+disbDateMap.size() );

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Date disbDate = null;
	String cgpan = null;
	int count = 0;
	String key = null;
	boolean finalFlag = true;

	while(cgpanIterator.hasNext())
	{
		cgpan = (String)cgpanMap.get(cgpanIterator.next()) ;
		Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","cgpanIterator cgpan: "+cgpan);

		disbAmtIterator = disbAmtSet.iterator();

		count = 0;
		boolean dateFlag = false;

		while(disbAmtIterator.hasNext())
		{
			disbAmtIterator.next();

			key = cgpan+"-"+count;
			Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Amount Iterator key : "+key);

			if(key !=null && !key.equals("") && disbAmtMap.containsKey(key))
			{
				String disbAmount = (String)disbAmtMap.get(key);
				Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Key is matched Amount :"+disbAmount);

				String disbursementDate = (String)disbDateMap.get(key);
				Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Key is matched Date :"+disbursementDate);

				if(!disbAmount.equals("") && disbursementDate.equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Key is matched Amount :");
					ActionError actionError  = new ActionError("enterDisbursementAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				else if (disbAmount.equals("") && !disbursementDate.equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Key is matched  Date :");
					ActionError actionError  = new ActionError("enterDisbursementAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}else if(!disbAmount.equals("") && !disbursementDate.equals("")&& finalFlag)
				{
					java.util.Date currentDate=new java.util.Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					try{
						disbDate = simpleDateFormat.parse(disbursementDate,new ParsePosition(0));
							Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","inside try block date format :"+disbursementDate);
							Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","after set ing true:"+disbursementDate);
						if(disbDate==null)
						{
							dateFlag = false;
							ActionError actionError=new ActionError("errors.date","Disbursement Date");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
							finalFlag = false;

						}else
						{
							dateFlag = true;
							try{

								String stringDate=dateFormat.format(currentDate);
								if(!disbursementDate.equals("") && !disbursementDate.equals(""))
								{

									//System.out.println("date :" + wcFBDateVal);
										if(DateHelper.compareDates(disbursementDate,stringDate)!=0 && DateHelper.compareDates(disbursementDate,stringDate)!=1)
										{
											ActionError actionError=new ActionError("futureDate",disbursementDate);
											errors.add(ActionErrors.GLOBAL_ERROR,actionError);


										}
								}
							}catch(NumberFormatException numberFormatException){

								boolean remarksVal = false;

								Iterator errorsIterator =errors.get();

								while(errorsIterator.hasNext())
								{
									ActionError error=(ActionError)errorsIterator.next();

									System.out.println(error.getKey());

									if(error.getKey().equals("errors.date"))
									{
										remarksVal = true;
										break;
									}
								}
								if(!remarksVal)
								{

									ActionError actionError=new ActionError("errors.date","Disbursement Date ");

									errors.add(ActionErrors.GLOBAL_ERROR,actionError);
								}

							}

						}
					}catch(NumberFormatException numberFormatException){

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();

							System.out.println(error.getKey());

							if(error.getKey().equals("errors.date"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("errors.date","Disbursement Date");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
				}



/*			disbDate = simpleDateFormat.parse(disbursementDate,new ParsePosition(0));
						Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","inside try block date format :"+disbursementDate);
						Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","after set ing true:"+disbursementDate);
					if(disbDate==null)
					{
						dateFlag = false;
					}else
					{
						dateFlag = true;
					}
					if(!dateFlag )
					{
						Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","date flag:"+dateFlag);
						ActionError actionError  = new ActionError("enterdisbursementDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						finalFlag = false;
					}
*/		}

			}
			++count;
		}
	}
	return errors.isEmpty();

   }


   public static boolean validateDisbursementAmounts(Object bean,
					 ValidatorAction validAction, Field field, ActionErrors errors,
					 HttpServletRequest request)
	{
	 Log.log(Log.INFO,"Validator","validateDisbursementAmounts","Entered");
	 GMActionForm gmActionForm = (GMActionForm)bean;

	 java.util.Map cgpanMap = gmActionForm.getCgpans() ;
	 java.util.Set cgpanSet = cgpanMap.keySet();
	 java.util.Iterator cgpanIterator = cgpanSet.iterator() ;
	 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","cgpan Map size = "+cgpanMap.size() );

	 Map disbAmtMap = gmActionForm.getDisbursementAmount();
	 Set disbAmtSet = disbAmtMap.keySet();
	 Iterator disbAmtIterator = disbAmtSet.iterator();

	 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","Amount map size = "+disbAmtMap.size() );

	 Map disbDateMap = gmActionForm.getDisbursementDate();
	 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","Date map size = "+disbDateMap.size() );

	 Map finalDisFlag = gmActionForm.getFinalDisbursement();
	 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","finalDisFlag size = "+finalDisFlag.size() );

	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	 Date disbDate = null;
	 String cgpan = null;
	 int count = 0;
	 String key = null;
	 boolean dateFlag = false;

	 while(cgpanIterator.hasNext())
	 {
		 cgpan = (String)cgpanMap.get(cgpanIterator.next()) ;
		 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","cgpanIterator cgpan: "+cgpan);

		 disbAmtIterator = disbAmtSet.iterator();

		 count = 0;


		 while(disbAmtIterator.hasNext())
		 {
			 disbAmtIterator.next();

			 key = cgpan+"-"+count;
			 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","Amount Iterator key : "+key);

			 if(key !=null && !key.equals("") && disbAmtMap.containsKey(key))
			 {
				 String disbAmount = (String)disbAmtMap.get(key);
				 Log.log(Log.DEBUG,"Validator","validateDisbursementAmounts","Key is matched Amount :"+disbAmount);

				 String disbursementDate = (String)disbDateMap.get(key);
				 Log.log(Log.DEBUG,"Validator","validateDisbursementAmount","Key is matched Date :"+disbursementDate);

				 if(!disbAmount.equals("") && !disbursementDate.equals(""))
				 {
					dateFlag=true;
					break;
/*					ActionError actionError  = new ActionError("enterDisbursementAmtandDate");
					 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/				 }
			 }
			 ++count;
		 }
		 if(!dateFlag)
		 {
				ActionError actionError  = new ActionError("enterDisbursementDetail");
				 errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		 }
	 }
	 return errors.isEmpty();

	}

/*   public static boolean validateDisbursementDate(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	Log.log(Log.INFO,"Validator","validateDisbursementDate","Entered");
	   GMActionForm gmActionForm = (GMActionForm)bean;
	   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");

	   Map dDate = gmActionForm.getDisbursementDate();
	   Set dDateSet = dDate.keySet();
	   Iterator dDateIterator = dDateSet.iterator();
	   boolean dDateFlag = false;
	   while(dDateIterator.hasNext())
	   {

		   String key = (String)dDateIterator.next();
		   String value = ((String)dDate.get(key)).trim();
		   Date disDate;
		   try
		   {
			   if(!value.equals(""))
			   {
				   disDate = simpleDateFormat.parse(value,new ParsePosition(0));
				   dDateFlag = true;
			   }
			   else
			   {
				dDateFlag = false;
			   }
		   }
		   catch(NullPointerException nullpointerexception)
		   {
				dDateFlag = false;
		   }
		   if(!dDateFlag )
		   {
			   ActionError actionError  = new ActionError("enterdisbursementDate");
			   errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			   break;
		   }
	   }
	Log.log(Log.INFO,"Validator","validateDisbursementDate","Exited");
	   return errors.isEmpty();
   } */

   public static boolean validateRepaymentAmount(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)throws Exception
   {
	   GMActionForm gmActionForm = (GMActionForm)bean;

	Map cgpanMap = gmActionForm.getCgpans() ;

	String cgpan = null;
    int count  =0;
	String repayAmtKey = null;
	String key = null;

	Set cgpanSet = cgpanMap.keySet();
	Iterator cgpanIterator = cgpanSet.iterator() ;
	Log.log(Log.DEBUG,"Validator"," validateRepaymentAmount ","cgpan Map size = "+cgpanMap.size() );

	Map repayAmountMap = gmActionForm.getRepaymentAmount();
	Set repayAmountSet = repayAmountMap.keySet();
	Iterator repayAmountIterator = repayAmountSet.iterator() ;
	Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","Amount map size = "+repayAmountMap.size() );

	Map repayDateMap = gmActionForm.getRepaymentDate();
	Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","Date map size = "+repayDateMap.size() );

	Date repmtDate = null;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	boolean finalFlag = true;
	while(cgpanIterator.hasNext())
	{
		cgpan  = (String)cgpanMap.get(cgpanIterator.next()) ;
		Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","Inside CgpanIterator cgpan  :"+cgpan);

		repayAmountIterator = repayAmountSet.iterator() ;

		count = 0;
		if(repayDateMap.size() == 1)
		{
			count =1;
		}
		boolean dateFlag = false;

		while(repayAmountIterator.hasNext())
		{
			repayAmtKey = (String)repayAmountIterator.next();

			key = cgpan+"-"+count;

			Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","key ->"+key);

			if(key !=null && !key.equals("") && repayAmountMap.containsKey(key) )
			{
				String repamt = (String)repayAmountMap.get(key);
				String  repdate = (String)repayDateMap.get(key);

				Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","repdate :"+repdate);
				if(!repamt.equals("") && repdate.toString().equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","Key is matched Amount :");
					ActionError actionError  = new ActionError("enterRepaymentAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

				}
				else if (repamt.equals("") && !repdate.equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","Key is matched  Date :");
					ActionError actionError  = new ActionError("enterRepaymentAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);

				}
				else if(!repamt.equals("") && !repdate.equals("")&& finalFlag)
				{
					java.util.Date currentDate=new java.util.Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					try{

						repmtDate = simpleDateFormat.parse(repdate.toString(),new ParsePosition(0));
						if(repmtDate==null)
						{
							dateFlag = false;

							ActionError actionError=new ActionError("errors.date","Repayment Date ");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);

/*				Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","date flag:"+dateFlag);
							ActionError actionError  = new ActionError("enterrepaymentDate");
							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/							finalFlag = false;


						}else{
							dateFlag = true;

							try{
								String stringDate=dateFormat.format(currentDate);

								if(DateHelper.compareDates(repdate,stringDate)!=0 && DateHelper.compareDates(repdate,stringDate)!=1)
								{
									ActionError actionError=new ActionError("futureDate",repdate);
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);


								}
							}catch(NumberFormatException numberFormatException){

								boolean remarksVal = false;

								Iterator errorsIterator =errors.get();

								while(errorsIterator.hasNext())
								{
									ActionError error=(ActionError)errorsIterator.next();

									System.out.println(error.getKey());

									if(error.getKey().equals("errors.date"))
									{
										remarksVal = true;
										break;
									}
								}
								if(!remarksVal)
								{

									ActionError actionError=new ActionError("errors.date","Repayment Date ");

									errors.add(ActionErrors.GLOBAL_ERROR,actionError);
								}

						}
						}


/*				String stringDate=dateFormat.format(currentDate);
						if(!repdate.equals("") && !repdate.equals(""))
						{

							// System.out.println("date :" + repdate);
								if(DateHelper.compareDates(repdate,stringDate)!=0 && DateHelper.compareDates(repdate,stringDate)!=1)
								{
									ActionError actionError=new ActionError("futureDate",repdate);
									errors.add(ActionErrors.GLOBAL_ERROR,actionError);


								}
						}
*/			}catch(NumberFormatException numberFormatException){

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();

							System.out.println(error.getKey());

							if(error.getKey().equals("errors.date"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("errors.date","Repayment Date ");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

					}

/*		repmtDate = simpleDateFormat.parse(repdate.toString(),new ParsePosition(0));
						Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","inside try block date format :"+repmtDate);
						Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","after set ing true:"+repmtDate);
					if(repmtDate==null)
					{
						dateFlag = false;
					}else{
						dateFlag = true;
					}
					if(!dateFlag )
					{
						Log.log(Log.DEBUG,"Validator","validateRepaymentAmount","date flag:"+dateFlag);
						ActionError actionError  = new ActionError("enterrepaymentDate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						finalFlag = false;
					}
*/
		}
			}
			++count;
		}
	}
	return errors.isEmpty();

   }

   public static boolean validateRepaymentAmounts(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	   GMActionForm gmActionForm = (GMActionForm)bean;

	Map cgpanMap = gmActionForm.getCgpans() ;

	String cgpan = null;
	int count  =0;
	String repayAmtKey = null;
	String key = null;

	Set cgpanSet = cgpanMap.keySet();
	Iterator cgpanIterator = cgpanSet.iterator() ;
	Log.log(Log.DEBUG,"Validator"," validateRepaymentAmounts ","cgpan Map size = "+cgpanMap.size() );

	Map repayAmountMap = gmActionForm.getRepaymentAmount();
	Set repayAmountSet = repayAmountMap.keySet();
	Iterator repayAmountIterator = repayAmountSet.iterator() ;
	Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","Amount map size = "+repayAmountMap.size() );

	Map repayDateMap = gmActionForm.getRepaymentDate();
	Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","Date map size = "+repayDateMap.size() );

	Date repmtDate = null;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	boolean dateFlag = false;

	while(cgpanIterator.hasNext())
	{
		cgpan  = (String)cgpanMap.get(cgpanIterator.next()) ;
		Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","Inside CgpanIterator cgpan  :"+cgpan);

		repayAmountIterator = repayAmountSet.iterator() ;

		count = 0;
		if(repayDateMap.size() == 1)
		{
			count =1;
		}


		while(repayAmountIterator.hasNext())
		{
			repayAmtKey = (String)repayAmountIterator.next();

			key = cgpan+"-"+count;

			Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","key ->"+key);

			if(key !=null && !key.equals("") && repayAmountMap.containsKey(key) )
			{
				String repamt = (String)repayAmountMap.get(key);
				String  repdate = (String)repayDateMap.get(key);

				Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","repdate :"+repdate);
				Log.log(Log.DEBUG,"Validator","validateRepaymentAmounts","repamt :"+repamt);

				if(!repamt.equals("") && !repdate.toString().equals(""))
				{
					dateFlag=true;
					break;
/*					ActionError actionError  = new ActionError("enterRepaymentAmtandDate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
				}
			}
			++count;
		}
	}
	if(!dateFlag)
	{
		ActionError actionError  = new ActionError("enterRepaymentValues");
		errors.add(ActionErrors.GLOBAL_ERROR,actionError);

	}

	return errors.isEmpty();

   }


  /* public static boolean validateRepaymentDate(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	   GMActionForm gmActionForm = (GMActionForm)bean;
	   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
	   Map rDate= gmActionForm.getRepaymentDate();
	   Set rDateSet = rDate.keySet();
	   Iterator rDateIterator = rDateSet.iterator();
	   boolean rDateFlag = false;

	   while(rDateIterator.hasNext())
	   {
		   Date repmtDate = null ;
		   String key = (String)rDateIterator.next();
		   Date value = (Date)rDate.get(key);
		   String val = value.toString();
		   try
		   {
			   if(!val.equals(""))
			   {
				   repmtDate = simpleDateFormat.parse(val,new ParsePosition(0));
				   rDateFlag = true;
			   }
			   else
			   {
				rDateFlag = false;
			   }
		   }
		   catch(NullPointerException nullpointerexception)
		   {
				rDateFlag = false;
		   }
		   if(!rDateFlag )
		   {
			   ActionError actionError  = new ActionError("enterrepaymentDate");
			   errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			   break;
		   }
	   }
	   return errors.isEmpty();
   }
*/

    public static boolean validateRepaymentSchedule(Object bean,
					   ValidatorAction validAction, Field field, ActionErrors errors,
					   HttpServletRequest request)
	  {
	  	GMActionForm gmActionForm = (GMActionForm)bean;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Map cgpanMap = gmActionForm.getCgpans() ;
		Set cgpanSet = cgpanMap.keySet();
		Iterator cgpanIterator = cgpanSet.iterator() ;


		Map dueDateMap = gmActionForm.getFirstInstallmentDueDate();
		Log.log(Log.DEBUG,"GMAction","saveRepaymentSchedule","first installment due date "+ dueDateMap.toString());

		String key = null;
		boolean isDtFieldValid = true;
		while(cgpanIterator.hasNext())
		{
		  key = (String)cgpanIterator.next();
		  String dateValue = (String)dueDateMap.get(key);
	      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		  java.util.Date tempDate = sdf.parse(dateValue,new ParsePosition(0));

		  if(tempDate==null)
		  {
		  	isDtFieldValid = false;
		  }else
		  {
			isDtFieldValid = true;
		  }

		  if(!isDtFieldValid )
		  {
			  ActionError actionError  = new ActionError("enterValiddueDate");
			  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			  break;
		  }
		}
		return errors.isEmpty();
	  }


   public static boolean validateIdsForClosure(Object bean,
							ValidatorAction validAction, Field field,
							ActionErrors errors, HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateIdsForClosure","Enetred");
		GMActionForm gmActionForm = (GMActionForm)bean;
		String memberId = gmActionForm.getMemberIdForClosure();
		String borrowerId = gmActionForm.getBorrowerIdForClosure();
		String cgpan = gmActionForm.getCgpanForClosure();
		String borrowerName = gmActionForm.getBorrowerNameForClosure();

		if( (cgpan.equals("")) && (borrowerName.equals("")) &&
									 (borrowerId.equals("")) )
		{
			ActionError actionError  = new ActionError("enterbidorcgpanorbName");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		if( !borrowerId.equals("") )
		{
			if( !borrowerName.equals("") || !cgpan.equals("") )
			{
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}else if( !cgpan.equals("") )
		{
			if( !borrowerName.equals("") || !borrowerId.equals("") )
			{
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}else if( !borrowerName.equals("") )
		{
			if( !cgpan.equals("") || !borrowerId.equals("") )
			{
				ActionError actionError  = new ActionError("enterbidorcgpanorbName");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}

		Log.log(Log.INFO,"Validator","validateIdsForClosure","Exited");
		return errors.isEmpty();
   }

   public static boolean checkForCl(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","checkForCl","Entered");
		GMActionForm gmActionForm = (GMActionForm)bean;

		String cgpan = null;
		Map cgpanMap = gmActionForm.getClCgpan();
		Set cgpanSet = cgpanMap.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();

		Map reasonMap = gmActionForm.getReasonForCl();

		Map closeFlagMap = gmActionForm.getClFlag();

		Log.log(Log.DEBUG,"Validator","checkForCl","closeFlagMap Size"+closeFlagMap.size());

		if( closeFlagMap.size() == 0 )
		{
			ActionError actionError  = new ActionError("enterAtleastOneClosure");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			return errors.isEmpty();
		}
		int i = 0;
		while(cgpanIterator.hasNext())
		{
			cgpan = (String)cgpanIterator.next();
			Log.log(Log.DEBUG,"Validator","checkForCl","inside cgpan iterator cgpan : "+ cgpan);
			Log.log(Log.DEBUG,"Validator","checkForCl","inside cgpan iterator CNT :"+ ++i);
			Log.log(Log.DEBUG,"Validator","checkForCl","inside cgpan iterator reason :"+ reasonMap.get(cgpan));
			Log.log(Log.DEBUG,"Validator","checkForCl","inside cgpan iterator Cl Flag :"+ closeFlagMap.get(cgpan));
			if( (reasonMap.get(cgpan).equals("")) && closeFlagMap.get(cgpan)!=null )
			{
				Log.log(Log.DEBUG,"Validator","checkForCl","inside if loop ");
				ActionError actionError  = new ActionError("enterReasonAndClosureflag");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				break;
			}
		}
		Log.log(Log.INFO,"Validator","checkForCl","Exited");
	   return errors.isEmpty();
   }




   public static boolean checkForClosure(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
	Log.log(Log.INFO,"Validator","checkForClosure","Entered");
	    GMActionForm gmActionForm = (GMActionForm)bean;

		String cgpan = null;
		Map cgpanMap = gmActionForm.getClosureCgpans();
		Set cgpanSet = cgpanMap.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();

	   	Map reasonMap = gmActionForm.getReasonForClosure();

	   	Map closeFlagMap = gmActionForm.getClosureFlag();

		Log.log(Log.DEBUG,"Validator","checkForClosure","closeFlagMap Size"+closeFlagMap.size());

		if( closeFlagMap.size() == 0 )
		{
			ActionError actionError  = new ActionError("enterAtleastOneClosure");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			return errors.isEmpty();
		}
		int i = 0;
		while(cgpanIterator.hasNext())
		{
			cgpan = (String)cgpanIterator.next();
			Log.log(Log.DEBUG,"Validator","checkForClosure","inside cgpan iterator cgpan : "+ cgpan);
			Log.log(Log.DEBUG,"Validator","checkForClosure","inside cgpan iterator CNT :"+ ++i);
			Log.log(Log.DEBUG,"Validator","checkForClosure","inside cgpan iterator reason :"+ reasonMap.get(cgpan));
			Log.log(Log.DEBUG,"Validator","checkForClosure","inside cgpan iterator Flag :"+ closeFlagMap.get(cgpan));
			if( (reasonMap.get(cgpan).equals("")) && !(closeFlagMap.get(cgpan)).equals("") )
			{
				Log.log(Log.DEBUG,"Validator","checkForClosure","inside if loop ");
				ActionError actionError  = new ActionError("enterReasonAndClosureflag");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				break;
			}
		}
		Log.log(Log.INFO,"Validator","checkForClosure","Exited");
	   return errors.isEmpty();
   }



   public static boolean validateRecProcs(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateRecProcs","Entered");
		GMActionForm gmActionForm = (GMActionForm)bean;

		Map recMap = gmActionForm.getRecProcedures() ;
		Set recSet = recMap.keySet() ;
		Iterator recIterator = recSet.iterator();
		int size = recMap.size();
		Log.log(Log.DEBUG,"Validator","validateRecProcs","rec Map Size :"+size);

		while(recIterator.hasNext())
		{
			String key  = (String)recIterator.next();
			Log.log(Log.DEBUG,"Validator","validateRecProcs","key : "+ key);
			RecoveryProcedureTemp recTemp =(RecoveryProcedureTemp)recMap.get(key);

			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionType());
			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDetails());
			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDate());

			String addFlag = (String)request.getParameter("addmoreflag");

			if( recTemp.getActionType().equals("") &&recTemp.getActionDetails().equals("")&&
					recTemp.getActionDate().toString().equals(""))
			{
				Log.log(Log.DEBUG,"Validator","validateRecProcs","inside if loop ");
				if( (size>1))
				{
					Log.log(Log.DEBUG,"Validator","validateRecProcs","inside if size >1 ");
					break;
				}
				ActionError actionError  = new ActionError("enteratleastonerecproc");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		Log.log(Log.INFO,"Validator","validateRecProcs","Exited");
	    return errors.isEmpty();
   }


   public static boolean validateRecProcDate(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateRecProcDate","Entered");
		GMActionForm gmActionForm = (GMActionForm)bean;

		Map recMap = gmActionForm.getRecProcedures() ;
		Set recSet = recMap.keySet() ;
		Iterator recIterator = recSet.iterator();

		Date aDate = null;
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		while(recIterator.hasNext())
		{
			String key  = (String)recIterator.next();
			Log.log(Log.DEBUG,"Validator","validateRecProcDate","key : "+ key);
			RecoveryProcedureTemp recTemp =(RecoveryProcedureTemp)recMap.get(key);

			Log.log(Log.DEBUG,"Validator","validateRecProcDate","recTemp :"+ recTemp.getActionType());
			Log.log(Log.DEBUG,"Validator","validateRecProcDate","recTemp :"+ recTemp.getActionDetails());
			Log.log(Log.DEBUG,"Validator","validateRecProcDate","recTemp :"+ recTemp.getActionDate());

			if((recTemp.getActionType()!=null && !recTemp.getActionType().equals("")) && (recTemp.getActionDetails()==null || recTemp.getActionDetails().equals("")))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("enterRecDetails"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("enterRecDetails");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*

				ActionError actionError  = new ActionError("enterRecDetails");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}
			if(((recTemp.getActionType()!=null && !recTemp.getActionType().equals(""))&&(recTemp.getActionDetails()!=null && !recTemp.getActionDetails().equals("")))
			&& ((recTemp.getActionDate()==null) || recTemp.getActionDate().toString().equals("")))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("enterRecdate"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("enterRecdate");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
/*
				ActionError actionError  = new ActionError("enterRecdate");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
*/
			}

			if( (recTemp.getActionDate()!=null) && !recTemp.getActionDate().toString().equals("") )
			{
				Log.log(Log.INFO,"Validator","validateRecProcDate","inside if loop ");
				aDate = dateFormat.parse(recTemp.getActionDate().toString(),new ParsePosition(0));
				Log.log(Log.INFO,"Validator","validateRecProcDate","dateFormat parse ");
				if( (aDate==null))
				{
					Log.log(Log.DEBUG,"Validator","validateRecProcDate","inside if aDate is null ");
					ActionError actionError  = new ActionError("entervalidrecdate");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				else{

					java.util.Date currentDate=new java.util.Date();
					aDate = dateFormat.parse(recTemp.getActionDate().toString(),new ParsePosition(0));

				//	System.out.println("compare value :" + aDate.compareTo(currentDate));

					if(aDate.compareTo(currentDate)==1)
					{

						System.out.println("compare value :" + aDate.compareTo(currentDate));

						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();
							if(error.getKey().equals("currentDateRecActionDate"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("currentDateRecActionDate");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}

					}

				}
			}
		}
		Log.log(Log.INFO,"Validator","validateRecProcs","Exited");
		return errors.isEmpty();
   }

   public static boolean validateOneRowRecProcs(Object bean,
					ValidatorAction validAction, Field field, ActionErrors errors,
					HttpServletRequest request)
   {
		Log.log(Log.INFO,"Validator","validateRecProcs","Entered");
		GMActionForm gmActionForm = (GMActionForm)bean;

		if(gmActionForm.getIsRecoveryInitiated().equals(Constants.NO))
		{
			/*
			If Recovery action initiated is 'NO', no need to add more
			and more rows.
			Added by Veldurai on 16th October 2004.
			*/
			return true;
		}

		Map recMap = gmActionForm.getRecProcedures() ;
		Set recSet = recMap.keySet() ;
		Iterator recIterator = recSet.iterator();

		Log.log(Log.DEBUG,"Validator","validateRecProcs","rec Map Size"+recMap.size());

		while(recIterator.hasNext())
		{
			String key  = (String)recIterator.next();
			Log.log(Log.DEBUG,"Validator","validateRecProcs","key : "+ key);
			RecoveryProcedureTemp recTemp =(RecoveryProcedureTemp)recMap.get(key);
			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionType());
			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDetails());
			Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDate());

			String addFlag = (String)request.getParameter("addmoreflag");
			//if(addFlag.equalsIgnoreCase("set"))
			//{
				if( recTemp.getActionType().equals("") &&recTemp.getActionDetails().equals("")&&
				recTemp.getActionDate().toString().equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateRecProcs","inside if loop ");
					ActionError actionError  = new ActionError("enterrecproc");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					break;
				}
			//}
		}
		Log.log(Log.INFO,"Validator","validateRecProcs","Exited");
	   return errors.isEmpty();
   }

   public static boolean checkNPAValidations(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
   {
		String recInitiated=ValidatorUtil.getValueAsString(bean, field.getProperty());

		GMActionForm gmActionForm = (GMActionForm)bean;

		if(recInitiated.equals("Y"))
		{

			if(gmActionForm.getCourtName()==null || gmActionForm.getCourtName().equals(""))
			{
				System.out.println("Court Name in Validation :" + gmActionForm.getCourtName());
				ActionError error=new ActionError("errors.required","Forum through which legal proceedings were	initiated against borrower");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if (gmActionForm.getCourtName()!=null && !gmActionForm.getCourtName().equals(""))
			{
				if(gmActionForm.getCourtName().equals("others")&& (gmActionForm.getInitiatedName()==null||gmActionForm.getInitiatedName().equals("")))
				{
					ActionError error=new ActionError("errors.required","Forum through which legal proceedings were	initiated against borrower");

					errors.add(ActionErrors.GLOBAL_ERROR,error);

				}
			}

			if(gmActionForm.getLegalSuitNo()==null || gmActionForm.getLegalSuitNo().equals(""))
			{
				ActionError error=new ActionError("errors.required","Suit/Case Registration No");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			if(gmActionForm.getDtOfFilingLegalSuit()==null || gmActionForm.getDtOfFilingLegalSuit().toString().equals(""))
			{
				ActionError error=new ActionError("errors.required","Filing Date ");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}
			else if (gmActionForm.getDtOfFilingLegalSuit()!=null && !gmActionForm.getDtOfFilingLegalSuit().equals(""))
			{
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

				try{

					Date filingdate = dateFormat.parse(gmActionForm.getDtOfFilingLegalSuit().toString(),new ParsePosition(0));

					if(filingdate==null)
					{

						ActionError actionError  = new ActionError("errors.date","Filing Date");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);

					}
					else{

							java.util.Date currentDate=new java.util.Date();
						filingdate = (java.util.Date)dateFormat.parse(gmActionForm.getDtOfFilingLegalSuit().toString(),new ParsePosition(0));

							try{

								String stringDate=dateFormat.format(currentDate);

									if(filingdate.compareTo(currentDate)==1)
									{
										ActionError actionError=new ActionError("currentDatelegaldate");
										errors.add(ActionErrors.GLOBAL_ERROR,actionError);


									}

							}catch(NumberFormatException numberFormatException){

								ActionError actionError  = new ActionError("errors.date","Filing Date");
								errors.add(ActionErrors.GLOBAL_ERROR,actionError);

							}
						}
				}

			catch(Exception n)
			{
				ActionError actionError=new ActionError("errors.date","Filing Date");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);

			}
			}


			if(gmActionForm.getForumName()==null || gmActionForm.getForumName().equals(""))
			{
				ActionError error=new ActionError("errors.required","Forum Name");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

			if(gmActionForm.getLocation()==null || gmActionForm.getLocation().equals(""))
			{
				ActionError error=new ActionError("errors.required","Location");

				errors.add(ActionErrors.GLOBAL_ERROR,error);

			}

			/**
			 * Validating Recovery Procs
			 */

			Map recMap = gmActionForm.getRecProcedures() ;
			Set recSet = recMap.keySet() ;
			Iterator recIterator = recSet.iterator();
			int size = recMap.size();
			Log.log(Log.DEBUG,"Validator","validateRecProcs","rec Map Size :"+size);

			while(recIterator.hasNext())
			{
				String key  = (String)recIterator.next();
				Log.log(Log.DEBUG,"Validator","validateRecProcs","key : "+ key);
				RecoveryProcedureTemp recTemp =(RecoveryProcedureTemp)recMap.get(key);

				Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionType());
				Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDetails());
				Log.log(Log.DEBUG,"Validator","validateRecProcs","recTemp :"+ recTemp.getActionDate());

				String addFlag = (String)request.getParameter("addmoreflag");

				if( recTemp.getActionType().equals("") &&recTemp.getActionDetails().equals("")&&
						recTemp.getActionDate().toString().equals(""))
				{
					Log.log(Log.DEBUG,"Validator","validateRecProcs","inside if loop ");
					if( (size>1))
					{
						Log.log(Log.DEBUG,"Validator","validateRecProcs","inside if size >1 ");
						break;
					}
					ActionError actionError  = new ActionError("enteratleastonerecproc");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				else{

					Date aDate = null;
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

					if((recTemp.getActionType()!=null && !recTemp.getActionType().equals("")) && (recTemp.getActionDetails()==null || recTemp.getActionDetails().equals("")))
					{
						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();
							if(error.getKey().equals("enterRecDetails"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("enterRecDetails");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
		/*

						ActionError actionError  = new ActionError("enterRecDetails");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		*/
					}
					if(((recTemp.getActionType()!=null && !recTemp.getActionType().equals(""))&&(recTemp.getActionDetails()!=null && !recTemp.getActionDetails().equals("")))
					&& ((recTemp.getActionDate()==null) || recTemp.getActionDate().toString().equals("")))
					{
						boolean remarksVal = false;

						Iterator errorsIterator =errors.get();

						while(errorsIterator.hasNext())
						{
							ActionError error=(ActionError)errorsIterator.next();
							if(error.getKey().equals("enterRecdate"))
							{
								remarksVal = true;
								break;
							}
						}
						if(!remarksVal)
						{

							ActionError actionError=new ActionError("enterRecdate");

							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
		/*
						ActionError actionError  = new ActionError("enterRecdate");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		*/
					}

					if( (recTemp.getActionDate()!=null) && !recTemp.getActionDate().toString().equals("") )
					{
						Log.log(Log.DEBUG,"Validator","validateRecProcDate","inside if loop ");
						aDate = dateFormat.parse(recTemp.getActionDate().toString(),new ParsePosition(0));
						Log.log(Log.DEBUG,"Validator","validateRecProcDate","dateFormat parse ");
						if( (aDate==null))
						{
							Log.log(Log.DEBUG,"Validator","validateRecProcDate","inside if aDate is null ");
							ActionError actionError  = new ActionError("entervalidrecdate");
							errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						}
						else{

							java.util.Date currentDate=new java.util.Date();
							aDate = dateFormat.parse(recTemp.getActionDate().toString(),new ParsePosition(0));

							System.out.println("compare value :" + aDate.compareTo(currentDate));

							if(aDate.compareTo(currentDate)==1)
							{

								System.out.println("compare value :" + aDate.compareTo(currentDate));

								boolean remarksVal = false;

								Iterator errorsIterator =errors.get();

								while(errorsIterator.hasNext())
								{
									ActionError error=(ActionError)errorsIterator.next();
									if(error.getKey().equals("currentDateRecActionDate"))
									{
										remarksVal = true;
										break;
									}
								}
								if(!remarksVal)
								{

									ActionError actionError=new ActionError("currentDateRecActionDate");

									errors.add(ActionErrors.GLOBAL_ERROR,actionError);
								}

							}

						}

					}

				}

			}

		}


		return errors.isEmpty();
   }


   /**
	* This method is used to validate whether atleast one transaction details are
	* entered or not.
	*
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/
   public static boolean checkNpaDateRequired(Object bean, ValidatorAction validAction,
						 Field field, ActionErrors errors, HttpServletRequest request)
   {
		String npaReported=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String reportingDateVal=field.getVarValue("reportingDate");
		String reportingDate=ValidatorUtil.getValueAsString(bean, reportingDateVal);

		if(npaReported.equals("Y") && (reportingDate==null || reportingDate.equals("")))
		{
			ActionError error=new ActionError("npaDateRequired");

			 errors.add(ActionErrors.GLOBAL_ERROR,error);

		}

	return errors.isEmpty();
   }




   /************************************************************************/
   /*******************End Of Guarantee Maintenance ************************/
   /************************************************************************/

    /***********************************Admin,common,reg(rp14480)*************************************/

	/**
	 * This method checks that during Define organisation structure when RO radio button is checked
	 * the reporting zone should be selected from the combo box.
	 * author rp14480
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */

  public static boolean checkReportingZoneSelected(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
			Log.log(Log.INFO,"Validator","checkReportingZoneSelected","Entered");

			DynaActionForm dynaForm=(DynaActionForm)bean;

			//Get the option chosen ,value is ZO for zone and RO for Region.

			String zoOrRo=(String)dynaForm.get("setZoRo");

			Log.log(Log.DEBUG,"Validator","checkReportingZoneSelected","zone(ZO)or Region(RO) "+zoOrRo);

			if(zoOrRo!=null && zoOrRo.equals("RO"))
			{

				//Get the reporting zone chosen for the Region.
				String reportingZone=(String)dynaForm.get("reportingZone");

				Log.log(Log.DEBUG,"Validator","checkReportingZoneSelected","reportingZone "+reportingZone);

				if(reportingZone==null || reportingZone.equals(""))
				{
					ActionError actionError=new ActionError("reportingZoneSelected");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
			}

			Log.log(Log.INFO,"Validator","checkReportingZoneSelected","Exited");

			return errors.isEmpty();
	}

	/**
		 * This method checks that during Define organisation structure when new branch is radio button is checked
		 * a zone should be selected from the combo box.
		 * author rp14480
		 * @param bean
		 * @param validAction
		 * @param field
		 * @param errors
		 * @param request
		 * @return
		 */

	  public static boolean selectZoneForBranch(Object bean, ValidatorAction validAction,
							  Field field, ActionErrors errors, HttpServletRequest request)
		{
				Log.log(Log.INFO,"Validator","selectZoneForBranch","Entered");

				DynaActionForm dynaForm=(DynaActionForm)bean;

				//Get the option chosen ,

				String zoOrRo=(String)dynaForm.get("setZoRo");

				Log.log(Log.DEBUG,"Validator","checkReportingZoneSelected","zone(ZO)or Region(RO) "+zoOrRo);

				if(zoOrRo!=null && zoOrRo.equals("NBR"))
				{

					//Get the reporting zone chosen for the Region.
					String zoneList=(String)dynaForm.get("zoneList");

					Log.log(Log.DEBUG,"Validator","selectZoneForBranch","zone  "+zoneList);

					if(zoneList==null || zoneList.equals(""))
					{
						ActionError actionError=new ActionError("selectZoneForBranch");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}

				Log.log(Log.INFO,"Validator","selectZoneForBranch","Exited");

				return errors.isEmpty();
		}

	/**
		 * This method checks whether the value entered in a text field already exists in a combobox
		 * in the same screen.
		 * Used in update master tables
		 * author rp14480
		 * @param bean
		 * @param validAction
		 * @param field
		 * @param errors
		 * @param request
		 * @return
		 *
		public static boolean checkAlreadyExist(Object bean, ValidatorAction validAction,
							  Field field, ActionErrors errors, HttpServletRequest request)
		{

			Log.log(Log.INFO,"Validator","checkAlreadyExist","Entered");
            HttpSession session = (HttpSession)request.getSession(false);
            session.setAttribute("modifiedInvesteeGroup",null);
			//Get the text box property name.
			String textBoxProperty=field.getProperty();
			Log.log(Log.INFO,"Validator","checkAlreadyExist","textBoxProperty :" + textBoxProperty);

			//Get the value entered in the text box.
			String textBoxValue=ValidatorUtil.getValueAsString(bean, textBoxProperty);
			Log.log(Log.INFO,"Validator","checkAlreadyExist","textBoxValue :" + textBoxValue);

			//Get the combo box property name.
			String comboBoxProperty=field.getVarValue("fromCombo");

			Log.log(Log.DEBUG,"Validator","checkAlreadyExist","textBoxProperty "+textBoxProperty);
			Log.log(Log.DEBUG,"Validator","checkAlreadyExist","textBoxValue "+textBoxValue);
			Log.log(Log.DEBUG,"Validator","checkAlreadyExist","comboBoxProperty "+comboBoxProperty);

			DynaActionForm dynaForm=(DynaActionForm)bean;

			//Get the arraylist of values from the combo box.
			ArrayList comboBox=(ArrayList)dynaForm.get(comboBoxProperty);
			String selectedInvesteeGrp = (String)dynaForm.get("investeeGroup");
			Log.log(Log.INFO,"Validator","checkAlreadyExist","selectedInvesteeGrp :" + selectedInvesteeGrp);

			//If a value is entered in the text box check it against the values in the combobox.
			//If the value exists in the combobox error is thrown.

		   if(  (textBoxValue!=null)&& (!textBoxValue.equals("")) ){

			  int size=comboBox.size();

			  for(int i=0;i<size;i++){

				  String comboValue=(String)comboBox.get(i);
				  Log.log(Log.INFO,"Validator","checkAlreadyExist","comboValue :" + comboValue);

				  if((textBoxValue.trim()).equalsIgnoreCase(comboValue)){
					  if((textBoxValue.trim()).equals(selectedInvesteeGrp.trim()))
					  {
						  continue;
					  }
					  Log.log(Log.INFO,"Validator","checkAlreadyExist","2 -> textBoxValue :" + textBoxValue);
					  session.setAttribute("modifiedInvesteeGroup",textBoxValue.trim());
					  ActionError actionError=new ActionError("fromCombo"+textBoxProperty);
					  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				  }
			  }
		   }

		   Log.log(Log.INFO,"Validator","checkAlreadyExist","Exited");
			return errors.isEmpty();
		}*/
	public static boolean checkAlreadyExist(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{

		Log.log(Log.INFO,"Validator","checkAlreadyExist","Entered");
		HttpSession session = (HttpSession)request.getSession(false);
		session.setAttribute("modifiedInvesteeGroup",null);

		//Get the text box property name ie. new value
		String textBoxProperty=field.getProperty();
		Log.log(Log.INFO,"Validator","checkAlreadyExist","textBoxProperty :" + textBoxProperty);

		//Get the value entered in the text box.
		String textBoxValue=ValidatorUtil.getValueAsString(bean, textBoxProperty).trim();
		Log.log(Log.INFO,"Validator","checkAlreadyExist","textBoxValue :" + textBoxValue);

		//Get the combo box property name.
		String comboBoxProperty=field.getVarValue("fromCombo");

		Log.log(Log.DEBUG,"Validator","checkAlreadyExist","textBoxProperty "+textBoxProperty);
		Log.log(Log.DEBUG,"Validator","checkAlreadyExist","textBoxValue "+textBoxValue);
		Log.log(Log.DEBUG,"Validator","checkAlreadyExist","comboBoxProperty "+comboBoxProperty);

		DynaActionForm dynaForm=(DynaActionForm)bean;
		Collection comboBox=null;

		//Get the arraylist of values from the combo box.
		if (dynaForm.get(comboBoxProperty) instanceof java.util.ArrayList)
		{
			comboBox=(ArrayList)dynaForm.get(comboBoxProperty);
		}
		else if (dynaForm.get(comboBoxProperty) instanceof java.util.Vector)
		{
			comboBox=(Vector)dynaForm.get(comboBoxProperty);
		}

		//Get the modify property and value entered.
		String modValueProperty=field.getVarValue("modValue");
		String modValue=ValidatorUtil.getValueAsString(bean, modValueProperty).trim();

		//Get the main property and value entered.
		String mainProperty=field.getVarValue("mainProp");
		String mainValue=ValidatorUtil.getValueAsString(bean, mainProperty);

		if (mainValue==null)
		{
			mainValue="";
		}

		if (modValue==null)
		{
			modValue="";
		}

		if (textBoxValue==null)
		{
			textBoxValue="";
		}

		Log.log(Log.INFO,"Validator","checkAlreadyExist","main value " + mainValue);
		Log.log(Log.INFO,"Validator","checkAlreadyExist","mod value " + modValue);
		Log.log(Log.INFO,"Validator","checkAlreadyExist","textbox value "+ textBoxValue);

		//If the combo value is selected for modification and the mod value is left empty, error is thrown.

		//If the combo is not selected and a new value is entered, it is checked against existing ones in the combo.
		//If it exists, error is thrown.

		//If combo is selected and the value is modified, it is checked against the existing list in the combo.
		//If it exists, error is thrown.

	   if(!mainValue.equals("") && modValue.equals("")){

		Log.log(Log.INFO,"Validator","checkAlreadyExist","combo has value but mod value is empty ");
		ActionError actionError=new ActionError(modValueProperty+"req");
		errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	   }
	   else if (!textBoxValue.equals(""))
	   {
	   	if (comboBox.contains(textBoxValue))
	   	{
			Log.log(Log.INFO,"Validator","checkAlreadyExist","new value is entered. but it is already present in combo ");
			ActionError actionError=new ActionError(mainProperty+ "exist");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	   	}
	   	else if (mainProperty.equalsIgnoreCase("budgetHead")|| mainProperty.equalsIgnoreCase("budgetSubHeadTitle"))
	   	{
	   		if (textBoxValue.indexOf('.')>-1)
	   		{
				Log.log(Log.INFO,"Validator","checkAlreadyExist","new value is entered for budget head / sub head and contains '.'");
				ActionError actionError=new ActionError(mainProperty+ "Dot");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	   		}
	   	}
	   }
	   else if (!mainValue.equals("") && !modValue.equals(""))
	   {
	   	if (!mainValue.equalsIgnoreCase(modValue))
	   	{
			if (comboBox.contains(modValue))
			{
				Log.log(Log.INFO,"Validator","checkAlreadyExist","mod value is entered. but it is already present in combo ");
				ActionError actionError=new ActionError(mainProperty+ "exist");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
			else if (mainProperty.equalsIgnoreCase("budgetHead")|| mainProperty.equalsIgnoreCase("budgetSubHeadTitle"))
			{
				if (modValue.indexOf('.')>-1)
				{
					Log.log(Log.INFO,"Validator","checkAlreadyExist","mod value is entered for budget head / sub head and contains '.'");
					ActionError actionError=new ActionError(mainProperty+ "Dot");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
			}
	   	}
	   }
	   session.setAttribute("modifiedInvesteeGroup",textBoxValue.trim());

	   Log.log(Log.INFO,"Validator","checkAlreadyExist","Exited");
		return errors.isEmpty();
	}

	/**
	* This method checks whether atleast one of the Dan delivery mode is selected.
	* while Registering MLI
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/
	public static boolean multiboxRequired(Object bean, ValidatorAction validAction,
							  Field field, ActionErrors errors, HttpServletRequest request)
	{

		Log.log(Log.INFO,"Validator","multiboxRequired","Entered");

		//Get the property name of the multibox.

		String multiboxProperty=field.getProperty();

		DynaActionForm dynaForm=(DynaActionForm)bean;

		String[]multiBoxArray=(String[])dynaForm.get(multiboxProperty);

		int size=multiBoxArray.length;

		Log.log(Log.DEBUG,"Validator","multiboxRequired","multiBoxArray is   "+size);

		//If the size of the array is zero ,that is no check box is selected, throw error.

		if(size==0){
				ActionError actionError=new ActionError("multibox"+multiboxProperty);
    	    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		Log.log(Log.INFO,"Validator","multiboxRequired","Exited");

		return errors.isEmpty();
	}

  /**
	* This method checks that a Bank is chosen when a message is to be Broadcasted to Bank.
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/

	public static boolean bankRequired(Object bean, ValidatorAction validAction,
								  Field field, ActionErrors errors, HttpServletRequest request)
		{
			Log.log(Log.INFO,"Validator","bankRequired","Entered");

			DynaActionForm dynaForm=(DynaActionForm)bean;

			//Get the bank name chosen and the radio button chosen
			String bankName=(String)dynaForm.get("bankName");
			String radioChosen=(String)dynaForm.get("selectBM");


			Log.log(Log.DEBUG,"Validator","bankRequired","bankName is"+bankName);
			Log.log(Log.DEBUG,"Validator","bankRequired","radioChosen is"+radioChosen);

			//If

			if(!(radioChosen.equals("AllHos"))&& bankName.equals("Select")){
				ActionError actionError=new ActionError("bankRequired");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}

			Log.log(Log.INFO,"Validator","bankRequired","Exited");
			return errors.isEmpty();
		}

/**
	* This method checks that a zone is chosen when a message is to be Broadcasted to a zone.
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/
	public static boolean zoneRequired(Object bean, ValidatorAction validAction,
									  Field field, ActionErrors errors, HttpServletRequest request)
			{

				Log.log(Log.INFO,"Validator","zoneRequired","Entered");

				DynaActionForm dynaForm=(DynaActionForm)bean;

				//Get the zone or region name combo values.
				String[] zones=(String[])dynaForm.get("zoneRegionNames");

				String zoneRegionName=null;

				//If the array is not of zero size get the first value.
				if(zones.length!=0){
				   zoneRegionName=zones[0];
				}

				String radioChosen=(String)dynaForm.get("selectBM");

				Log.log(Log.DEBUG,"Validator","zoneRequired","zoneRegionName is"+zoneRegionName);
				Log.log(Log.DEBUG,"Validator","zoneRequired","radioChosen is"+radioChosen);

				//If members of zone or NO of zone is chosen and zone is not present throw an error.
				if(  ( radioChosen.equals("membersOfZone") || radioChosen.equals("noOfZones") )
				   &&(zoneRegionName.equals("None")) ) {
					ActionError actionError=new ActionError("zoneRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

				Log.log(Log.INFO,"Validator","zoneRequired","Exited");

				return errors.isEmpty();
			}

/**
	* This method checks that a Branch is chosen when a message is to be Broadcasted to Branch.
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/
	public static boolean branchRequired(Object bean, ValidatorAction validAction,
										  Field field, ActionErrors errors, HttpServletRequest request)
		{
			Log.log(Log.INFO,"Validator","branchRequired","Entered");

			DynaActionForm dynaForm=(DynaActionForm)bean;

			//Get the branch selected.
			String[] branches=(String[])dynaForm.get("branchNames");

			String branchName=null;

			if(branches.length!=0){
				branchName=branches[0];
			}
			String radioChosen=(String)dynaForm.get("selectBM");

			Log.log(Log.DEBUG,"Validator","branchRequired","branchName is"+branchName);
			Log.log(Log.DEBUG,"Validator","branchRequired","radioChosen is"+radioChosen);

			//If members of branch or NO of branches is chosen and branch is none throw error.
			if(  ( radioChosen.equals("membersOfBranch") || radioChosen.equals("noOfBranches") )
			   &&(  branchName.equals("None") )) {
				ActionError actionError=new ActionError("branchRequired");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}

			Log.log(Log.INFO,"Validator","branchRequired","Exited");

			return errors.isEmpty();
		}
/**
	* This method checks that bank id is not entered as zeros.
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/

	public static boolean bankIdNotZeros(Object bean, ValidatorAction validAction,
							  Field field, ActionErrors errors, HttpServletRequest request)
		{
			Log.log(Log.INFO,"Validator","bankIdNotZeros","Entered");

			String fieldValue = ValidatorUtil.getValueAsString(bean, field.getProperty());

			String bankId=fieldValue.substring(0,4);

			Log.log(Log.DEBUG,"Validator","bankIdNotZeros","value entered -- " + fieldValue);

			if ((bankId.equals(Constants.CGTSI_USER_BANK_ID)) )
			{
				Log.log(Log.DEBUG,"Validator","bankIdNotZeros","adding error message");
				ActionError actionMessage=new ActionError("bankIdNotZeros");
				errors.add(field.getKey(), Resources.getActionError(request, validAction, field));
			}

			Log.log(Log.INFO,"Validator","bankIdNotZeros","Exited");
			return errors.isEmpty();
		}



/*	public static boolean validateApplicationReport(Object bean, ValidatorAction validAction,
									Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm  = (DynaActionForm)bean;

		String selectAll = (String)dynaForm.get("selectAll");
		String applicationDate = (String)dynaForm.get("applicationDate");
		String promoter = (String)dynaForm.get("promoter");
		String itpan = (String)dynaForm.get("itpan");
		String ssiDetails = (String)dynaForm.get("ssiDetails");
		String industryType = (String)dynaForm.get("industryType");
		String termCreditSanctioned = (String)dynaForm.get("termCreditSanctioned");
		String tcInterest = (String)dynaForm.get("tcInterest");
		String tcTenure = (String)dynaForm.get("tcTenure");
		String tcPlr = (String)dynaForm.get("tcPlr");
		String tcOutlay = (String)dynaForm.get("tcOutlay");
		String workingCapitalSanctioned = (String)dynaForm.get("workingCapitalSanctioned");
		String wcPlr = (String)dynaForm.get("wcPlr");
		String wcOutlay = (String)dynaForm.get("wcOutlay");
		String rejection = (String)dynaForm.get("rejection");

		if(((applicationDate == null) || (applicationDate.equals("")))
		 && ((promoter == null) || (promoter.equals("")))
		&& ((itpan == null) || (itpan.equals("")))
		&& ((ssiDetails == null) || (ssiDetails.equals("")))
		&& ((industryType == null) || (industryType.equals("")))
		&& ((termCreditSanctioned == null) || (termCreditSanctioned.equals("")))
		&& ((tcInterest == null) || (tcInterest.equals("")))
		&& ((tcTenure == null) || (tcTenure.equals("")))
		&& ((tcPlr == null) || (tcPlr.equals("")))
		&& ((tcOutlay == null) || (tcOutlay.equals("")))
		&& ((workingCapitalSanctioned == null) || (workingCapitalSanctioned.equals("")))
		&& ((wcPlr == null) || (wcPlr.equals("")))
		&& ((wcOutlay == null) || (wcOutlay.equals("")))
		&& ((rejection == null) || (rejection.equals(""))))
		{
			ActionError actionError  = new ActionError("enterAnyOneField");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
		}

		return errors.isEmpty();
	}*/

/**
	* This method checks that either selection is made from the combobox or a new entry
	* is given in the text box. Used in Update Master Table screens.
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/

	public static boolean anyOneRequired(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","anyOneRequired","Entered");

		String comboBoxValue = ValidatorUtil.getValueAsString(bean, field.getProperty());

		String textBoxValue=field.getVarValue("textBoxValue");

		String textValue=ValidatorUtil.getValueAsString(bean, textBoxValue);

		Log.log(Log.DEBUG,"Validator","anyOneRequired","comboBoxValue " + comboBoxValue);
		Log.log(Log.DEBUG,"Validator","anyOneRequired","textBoxValue " + textBoxValue);
		Log.log(Log.DEBUG,"Validator","anyOneRequired","textValue " + textValue);

		Arg keyValue=field.getArg0();

		String key=keyValue.toString();

		Log.log(Log.DEBUG,"Validator","anyOneRequired","key "+ key);

		//If both combobox value and textbox values are blank throw error.

		if ( comboBoxValue.equals("")&& textValue.equals("")){

			ActionError actionMessage=new ActionError(key);
			errors.add(field.getKey(), Resources.getActionError(request, validAction, field));

		}
	   Log.log(Log.INFO,"Validator","anyOneRequired","Exited");
	   return errors.isEmpty();
	}

	/**
		* This method validates that appropriate values are entered according to the
		* chosen in the define organisation structure screen.
		* author rp14480.
		*
		* @param bean
		* @param validAction
		* @param field
		* @param errors
		* @param request
		* @return
		*/

		public static boolean defineOrgStr(Object bean, ValidatorAction validAction,
										  Field field, ActionErrors errors, HttpServletRequest request)
				{
					Log.log(Log.INFO,"Validator","defineOrgStr","Entered");

					DynaActionForm dynaForm=(DynaActionForm)bean;

					//Get the value of the  radio button chosen.
					String radioChosen=(String)dynaForm.get("setZoRo");
					String newZone=(String)dynaForm.get("zoneName");
					String branch=(String)dynaForm.get("branchName");
					String zoneList=(String)dynaForm.get("zoneList");
					String reportingZone=(String)dynaForm.get("reportingZone");

					Log.log(Log.DEBUG,"Validator","defineOrgStr","radioChosen  "+radioChosen);
					Log.log(Log.DEBUG,"Validator","defineOrgStr","newZone"+newZone);
					Log.log(Log.DEBUG,"Validator","defineOrgStr","branch "+branch);
					Log.log(Log.DEBUG,"Validator","defineOrgStr","zoneList"+zoneList);
					Log.log(Log.DEBUG,"Validator","defineOrgStr","reportingZone"+reportingZone);

					//If a radio is chosen and the corresponding values not entered then throw error.
					if((radioChosen.equals("ZO"))&& (newZone==null||newZone.equals(""))){
						ActionError actionError=new ActionError("newZoneRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if((radioChosen.equals("RO"))&& (newZone==null||newZone.equals(""))){
						ActionError actionError=new ActionError("newRegionRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if((radioChosen.equals("RO"))&& (reportingZone==null||reportingZone.equals(""))){
						ActionError actionError=new ActionError("reportingZoneRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if((radioChosen.equals("NBR"))&& (branch==null||branch.equals(""))){
						ActionError actionError=new ActionError("branchRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if((radioChosen.equals("NBR"))&& (zoneList==null||zoneList.equals(""))){
						ActionError actionError=new ActionError("zoneRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
					if((radioChosen.equals("BRB"))&& (branch==null||branch.equals(""))){
						ActionError actionError=new ActionError("branchRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

					Log.log(Log.INFO,"Validator","defineOrgStr","Exited");
					return errors.isEmpty();

				}

		/**
		* This method checks that a value is entered for BPLR when the BPLR radio is checked in
		* Update PLR master screen.
		*
		* author rp14480.
		*
		* @param bean
		* @param validAction
		* @param field
		* @param errors
		* @param request
		* @return
		*/

		public static boolean plrChosenInModify(Object bean, ValidatorAction validAction,
										  Field field, ActionErrors errors, HttpServletRequest request)
				{
					Log.log(Log.INFO,"Validator","plrChosenInModify","Entered");

					AdministrationActionForm dynaForm=(AdministrationActionForm)bean;
					PLRMaster plrMaster=dynaForm.getPlrMaster();

					if(plrMaster==null)
					{
						Log.log(Log.ERROR,"Validator","plrChosenInModify","PLR master is empty");

						return false;
						//
					}

					//Get the value of the PLR radio button chosen.
					String PLR=plrMaster.getPLR();

					Log.log(Log.INFO,"Validator","plrChosenInModify","PLR :" + PLR);

					double BPLR = 0.0;
					BPLR=plrMaster.getBPLR();

					Log.log(Log.DEBUG,"Validator","plrChosenInModify","PLR "+PLR);
					Log.log(Log.DEBUG,"Validator","plrChosenInModify","BPLR "+BPLR);

					//If BPLR is chosen and BPLR value is not entered throw error.
					if((PLR.equals("B"))&& BPLR==0.0){
						ActionError actionError=new ActionError("BPLRRequired");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}

					Log.log(Log.INFO,"Validator","plrChosenInModify","Exited");
					return errors.isEmpty();

				}



	/**
	* This method checks that a value is entered for BPLR when the BPLR radio is checked in
	* Update PLR master screen.
	*
	* author rp14480.
	*
	* @param bean
	* @param validAction
	* @param field
	* @param errors
	* @param request
	* @return
	*/

	public static boolean plrChosen(Object bean, ValidatorAction validAction,
									  Field field, ActionErrors errors, HttpServletRequest request)
			{
				Log.log(Log.INFO,"Validator","plrChosen","Entered");

				DynaActionForm dynaForm=(DynaActionForm)bean;

				//Get the value of the PLR radio button chosen.
				String PLR=(String)dynaForm.get("PLR");

				Log.log(Log.INFO,"Validator","plrChosen","PLR :" + PLR);

				Double bplrObj = (Double)dynaForm.get("BPLR");

				double BPLR = 0.0;

				if(bplrObj != null)
				{
					BPLR = bplrObj.doubleValue();
				}

                /*
				//Get the BPLR value entered.
				if((PLR != null) && (!PLR.equals("")))
				{
					BPLR=().doubleValue();
				}
				*/

				Log.log(Log.DEBUG,"Validator","plrChosen","PLR "+PLR);
				Log.log(Log.DEBUG,"Validator","plrChosen","BPLR "+BPLR);

				//If BPLR is chosen and BPLR value is not entered throw error.
				if((PLR.equals("B"))&& BPLR==0.0){
					ActionError actionError=new ActionError("BPLRRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}

				Log.log(Log.INFO,"Validator","plrChosen","Exited");
				return errors.isEmpty();

			}

	/************************************************************************/
	/******************General Validations**********************************/

	/**
	 * This method checks whether the from date is before the current date
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateFromNPACurrentDates(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
        GMActionForm  gmPeriodicInfoForm = (GMActionForm)bean;
        GMDAO gmDAO = new GMDAO() ;
                    String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
                    String fromString=field.getProperty();
       // System.out.println("fromString:"+fromString);
       // System.out.println("fromValue:"+fromValue);
        java.util.Date npaDate = gmPeriodicInfoForm.getNpaDate();
        
        String borrowerId = gmPeriodicInfoForm.getBorrowerId().toUpperCase();
        int count  = gmDAO.getExceptionBIDCount(borrowerId);
        System.out.println("Borrower Id for Update Npa details:"+borrowerId);
    
    
    
   //  System.out.println("NPA Date:"+npaDate);
		java.util.Date currentDate=new java.util.Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
    
    String toValue = null;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DATE);
    int temp = 0;
    if(month >=0 && month <=2){  
    year=year-1;
    calendar.set(Calendar.MONTH,9);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year);
    temp=0;
    }
    else if(month >= 3 && month <=5 ){       
    calendar.set(Calendar.MONTH,0);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    temp=1;
    }
    else if(month >=6 && month <=8){  
    calendar.set(Calendar.MONTH,3);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    temp=2;
    }
    else if(month >=9 && month <=11){ 
    calendar.set(Calendar.MONTH,6);
    calendar.set(Calendar.DATE,1);
    calendar.set(Calendar.YEAR,year); 
    temp=3;
    }
    
        java.util.Date toDate = calendar.getTime() ;      
        toValue = dateFormat.format(toDate);
			//	System.out.println("To Date:"+toValue);
		try{

			String stringDate=dateFormat.format(currentDate);

			if (!(GenericValidator.isBlankOrNull(fromValue)))
			{

				if(DateHelper.compareDates(fromValue,stringDate)!=0 && DateHelper.compareDates(fromValue,stringDate)!=1)
				{
					ActionError actionError=new ActionError("currentDate" + fromString);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
        if(count == 0) {
       if(DateHelper.compareDates(toValue,fromValue)!=0 && DateHelper.compareDates(toValue,fromValue)!=1)
	     {
          ActionError actionError=new ActionError("newNpaDate"+temp);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
        } 
      }
			} 
	   }catch(NumberFormatException numberFormatException){

			ActionError actionError=new ActionError("errors.date", fromString);
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}

		return errors.isEmpty();
	}

/**
   * 
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   * @throws java.lang.Exception
   */
public static boolean validateFromCurrentDates(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String fromString=field.getProperty();
               
		java.util.Date currentDate=new java.util.Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

			String stringDate=dateFormat.format(currentDate);

			if (!(GenericValidator.isBlankOrNull(fromValue)))
			{

				if(DateHelper.compareDates(fromValue,stringDate)!=0 && DateHelper.compareDates(fromValue,stringDate)!=1)
				{
					ActionError actionError=new ActionError("currentDate" + fromString);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
			}
		return errors.isEmpty();
	}



/**
   * 
   * added by sukumar@path for validating the filing date
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   * @throws java.lang.Exception
   */
   /*
public static boolean validateFilingDate(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String fromString=field.getProperty();
    System.out.println("From value:"+fromValue);
    ClaimActionForm claimForm=(ClaimActionForm)bean;
		java.util.Date currentDate=new java.util.Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

	//	try{

			String stringDate=dateFormat.format(currentDate);      
      String npaDateNew = claimForm.getNpaDateNew();
      System.out.println("npaDateNew:"+npaDateNew);
   
    
			if (!(GenericValidator.isBlankOrNull(fromValue)))
			{

				if(DateHelper.compareDates(fromValue,stringDate)!=0 && DateHelper.compareDates(fromValue,stringDate)!=1)
				{
					ActionError actionError=new ActionError("currentDate" + fromString);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
        	if(DateHelper.compareDates(npaDateNew,fromValue)!=0 && DateHelper.compareDates(npaDateNew,fromValue)!=1)
				{
				//	ActionError actionError=new ActionError("filingdate" + fromString);
      //  ActionError actionMessage=new ActionError("filingEntry");
		    ActionError actionMessage=new ActionError("Filing Date can not be prior to the NPA Classified Date");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
				}
			}
	//	}catch(NumberFormatException numberFormatException){

		//	ActionError actionError=new ActionError("errors.date", fromString);
		//	errors.add(ActionErrors.GLOBAL_ERROR,actionError);

//		}

		return errors.isEmpty();
	}
*/

	/**
	 * This method checks whether amount value is less than 100 or not
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateAmountValue(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String amountValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String amountProperty = field.getProperty();
		java.lang.Double amountVal=new Double(amountValue);
		double amtValue=amountVal.doubleValue();
		if(amtValue>0 || amtValue>0.0)
		{
			if(amtValue < 100)
			{
				ActionError actionError=new ActionError("amtgreater" + amountProperty);
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}
	
  /**
   * 
   * @param bean
   * @param validAction
   * @param field
   * @param errors
   * @param request
   * @return 
   * @throws java.lang.Exception
   */
  public static boolean validateRSFAmountValue(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String amountValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String amountProperty = field.getProperty();
		java.lang.Double amountVal=new Double(amountValue);
		double amtValue=amountVal.doubleValue();
		if(amtValue>0 || amtValue>0.0)
		{
			if(amtValue < 5000000)
			{
				ActionError actionError=new ActionError("amtgreater" + amountProperty);
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		}
		return errors.isEmpty();
	}
	/**
	 * This method checks whether the from date is after the current date
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateAfterCurrentDates(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	{
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String fromString=field.getProperty();
		java.util.Date currentDate=new java.util.Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

		try{

			String stringDate=dateFormat.format(currentDate);

			if (!(GenericValidator.isBlankOrNull(fromValue)))
			{

				if(DateHelper.compareDates(fromValue,stringDate)!=-1 && DateHelper.compareDates(fromValue,stringDate)!=0)
				{
					ActionError actionError=new ActionError("futureDate" + fromString);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);


				}
			}
		}catch(NumberFormatException numberFormatException){

			ActionError actionError=new ActionError(fromString + " is not a valid Date");
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}


		return errors.isEmpty();
	}
	

/******************************************************************************************/

//Fix on 17.9.2004
	  public static boolean validateApplicationReport(Object bean, ValidatorAction validAction,
                                                      Field field, ActionErrors errors, HttpServletRequest request)
      {
            DynaActionForm dynaForm  = (DynaActionForm)bean;

			String selectAll = (String)request.getParameter("selectAll");
			String applicationDate = (String)request.getParameter("applicationDate");
			String promoter = (String)request.getParameter("promoter");
			String itpan = (String)request.getParameter("itpan");
			String ssiDetails = (String)request.getParameter("ssiDetails");
			String industryType = (String)request.getParameter("industryType");
			String termCreditSanctioned = (String)request.getParameter("termCreditSanctioned");
			String tcInterest = (String)request.getParameter("tcInterest");
			String tcTenure = (String)request.getParameter("tcTenure");
			String tcPlr = (String)request.getParameter("tcPlr");
			String tcOutlay = (String)request.getParameter("tcOutlay");
			String workingCapitalSanctioned = (String)request.getParameter("wcPlr");
			String wcPlr = (String)request.getParameter("wcPlr");
			String wcOutlay = (String)request.getParameter("wcOutlay");
			String rejection = (String)request.getParameter("rejection");

			if(((applicationDate == null) || (applicationDate.equals("")))
			 && ((promoter == null) || (promoter.equals("")))
			&& ((itpan == null) || (itpan.equals("")))
			&& ((ssiDetails == null) || (ssiDetails.equals("")))
			&& ((industryType == null) || (industryType.equals("")))
			&& ((termCreditSanctioned == null) || (termCreditSanctioned.equals("")))
			&& ((tcInterest == null) || (tcInterest.equals("")))
			&& ((tcTenure == null) || (tcTenure.equals("")))
			&& ((tcPlr == null) || (tcPlr.equals("")))
			&& ((tcOutlay == null) || (tcOutlay.equals("")))
			&& ((workingCapitalSanctioned == null) || (workingCapitalSanctioned.equals("")))
			&& ((wcPlr == null) || (wcPlr.equals("")))
			&& ((wcOutlay == null) || (wcOutlay.equals("")))
			&& ((rejection == null) || (rejection.equals(""))))
			{
				  ActionError actionError  = new ActionError("enterAnyOneField");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
            return errors.isEmpty();
      }

//Fix Completed

	  public static boolean searchDocument(Object bean, ValidatorAction validAction,
                                                      Field field, ActionErrors errors, HttpServletRequest request)
      {
            DynaActionForm dynaForm  = (DynaActionForm)bean;
            String fileNumber = (String)dynaForm.get("fileNumber");
            String fileTitle = (String)dynaForm.get("fileTitle");
            String subject = (String)dynaForm.get("subject");
            String category = (String)dynaForm.get("category");
            Date dateOfTheDocument = (Date)dynaForm.get("dateOfTheDocument");
            String documentDate = dateOfTheDocument.toString();
            String remarks = (String)dynaForm.get("remarks");

            if(((fileNumber == null) || (fileNumber.equals("")))
            && ((fileTitle == null) || (fileTitle.equals("")))
            && ((subject == null) || (subject.equals("")))
            && ((category == null) || (category.equals("")))
            && ((documentDate == null) || (documentDate.equals("")))
            && ((remarks == null) || (remarks.equals(""))))
            {
                  ActionError actionError  = new ActionError("enterAnyOneField");
                  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
            }
            return errors.isEmpty();
      }

	  public static boolean outwardIdValidation(Object bean, ValidatorAction validAction,
                        Field field, ActionErrors errors, HttpServletRequest request) throws DatabaseException
      {
            DynaActionForm dynaForm  = (DynaActionForm)bean;
            String mappedOutward = (String)dynaForm.get("mappedOutwardID");
            String memberId = null;
			ArrayList OfmemberIds = new ArrayList();
			IOProcessor ioprocessor = new IOProcessor();
			ArrayList outwardIds = new ArrayList();
			outwardIds = ioprocessor.getAllOutwardIds();
			int count = 0;
			String tempId = "";
			Inward inward=new Inward();


			if(!(mappedOutward == null || mappedOutward.equals("")))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(mappedOutward,",");
				while(stringTokenizer.hasMoreTokens())
				{
					memberId = stringTokenizer.nextToken();
					String newMemberId1 = memberId.trim();
					String newMemberId2 = newMemberId1.toUpperCase();
					 if(!(OfmemberIds.contains(newMemberId2)))
					 {
						OfmemberIds.add(newMemberId2);
					 }
					 else
					 {
						continue;
					 }
				}

				int OfmemberIdsSize = OfmemberIds.size();
				for(int i=0; i<OfmemberIdsSize; i++)
				{
					String id =  (String) OfmemberIds.get(i);
					String newId = id.trim();
					String newOutwardId = newId.toUpperCase();

					if(outwardIds.contains(newOutwardId))
					{
						count++;

						if(count == OfmemberIdsSize)
						{
							for(int j=0; j<OfmemberIdsSize; j++)
							{
								String mappedInwardId =  (String) OfmemberIds.get(j);
								String newMappedInwardId =  mappedInwardId.trim();
								String mappedInwardId1 =  newMappedInwardId.toUpperCase();
								String mappedInwardId2 = mappedInwardId1 + ",";
								tempId =  tempId + mappedInwardId2 ;

							}
							//inward.setMappedOutwardID(tempId);
						}

						else
						{
							continue;
						}
					}
					else
					{
						ActionError actionError  = new ActionError("enterValidOutwardId");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						break;
					}
				}
			}
        return errors.isEmpty();
      }


	  public static boolean inwardIdValidation(Object bean, ValidatorAction validAction,
						Field field, ActionErrors errors, HttpServletRequest request) throws DatabaseException
	  {
			DynaActionForm dynaForm  = (DynaActionForm)bean;
			String mappedInward = (String)dynaForm.get("mappedInward");
			String memberId = null;
			ArrayList OfmemberIds = new ArrayList();
			IOProcessor ioprocessor = new IOProcessor();
			ArrayList outwardIds = new ArrayList();
			outwardIds = ioprocessor.getAllInwardIds();
			int count = 0;
			String tempId = "";
			Inward inward=new Inward();


			if(!(mappedInward == null || mappedInward.equals("")))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(mappedInward,",");
				while(stringTokenizer.hasMoreTokens())
				{
					memberId = stringTokenizer.nextToken();
					String newMemberId1 = memberId.trim();
					String newMemberId2 = newMemberId1.toUpperCase();
					 if(!(OfmemberIds.contains(newMemberId2)))
					 {
						OfmemberIds.add(newMemberId2);
					 }
					 else
					 {
						continue;
					 }
				}

				int OfmemberIdsSize = OfmemberIds.size();
				for(int i=0; i<OfmemberIdsSize; i++)
				{
					String id =  (String) OfmemberIds.get(i);
					String newId = id.trim();
					String newOutwardId = newId.toUpperCase();

					if(outwardIds.contains(newOutwardId))
					{
						count++;

						if(count == OfmemberIdsSize)
						{
							for(int j=0; j<OfmemberIdsSize; j++)
							{
								String mappedInwardId =  (String) OfmemberIds.get(j);
								String newMappedInwardId =  mappedInwardId.trim();
								String mappedInwardId1 =  newMappedInwardId.toUpperCase();
								String mappedInwardId2 = mappedInwardId1 + ",";
								tempId =  tempId + mappedInwardId2 ;

							}
							//inward.setMappedOutwardID(tempId);
						}

						else
						{
							continue;
						}
					}
					else
					{
						ActionError actionError  = new ActionError("enterValidInwardId");
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
						break;
					}
				}
			}
		return errors.isEmpty();
	  }

	  public static boolean idForCategory(Object bean, ValidatorAction validAction,
                        Field field, ActionErrors errors, HttpServletRequest request) throws DatabaseException
      {
            DynaActionForm dynaForm  = (DynaActionForm)bean;
            String category = (String)dynaForm.get("category");
            String inResponseToID = (String)dynaForm.get("inResponseToID");
            String newId = inResponseToID.toUpperCase();

            ArrayList outwardIds = new ArrayList();
            ArrayList inwardIds = new ArrayList();
            IOProcessor ioprocessor = new IOProcessor();
            outwardIds = ioprocessor.getAllOutwardIds();
            inwardIds = ioprocessor.getAllInwardIds();

            if(category.equals("Inward"))
            {
                  if (!(inwardIds.contains(newId)))
                  {
                        ActionError actionError  = new ActionError("enterValidInwardId");
                        errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
            }
            else if(category.equals("Outward"))
            {
                  if (!(outwardIds.contains(newId)))
                  {
                        ActionError actionError  = new ActionError("enterValidOutwardId");
                        errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
            }
            return errors.isEmpty();
      }


	  	public static boolean fileInwardValidation(Object bean, ValidatorAction validAction,
													Field field, ActionErrors errors, HttpServletRequest request) throws Exception
	{
		  DynaActionForm dynaForm  = (DynaActionForm)bean;
		  FormFile file=(FormFile)dynaForm.get("filePathInward");

			if((file != null) && (!(file.toString()).equals("")))
			{
			  //Get the File Path
			  String contextPath=request.getSession().getServletContext().getRealPath("");
			  String path= PropertyLoader.changeToOSpath(contextPath+"/"+
			  Constants.FILE_UPLOAD_DIRECTORY+File.separator+file.getFileName());
			  String fileName = file.getFileName();
			  int index = fileName.lastIndexOf(".");

			  if(index == -1)
			  {
				  ActionError actionError  = new ActionError("enterValidFile");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			  }
			}
			return errors.isEmpty();

	}


	public static boolean fileOutwardValidation(Object bean, ValidatorAction validAction,
													Field field, ActionErrors errors, HttpServletRequest request) throws Exception
	{
		  DynaActionForm dynaForm  = (DynaActionForm)bean;
		  FormFile file=(FormFile)dynaForm.get("filePathOutward");

		  if((file != null) && (!(file.toString()).equals("")))
		  {
			//Get the File Path
			String contextPath=request.getSession().getServletContext().getRealPath("");
			String path= PropertyLoader.changeToOSpath(contextPath+"/"+
			Constants.FILE_UPLOAD_DIRECTORY+File.separator+file.getFileName());
			String fileName = file.getFileName();
			int index = fileName.lastIndexOf(".");

			if(index == -1)
			{
				ActionError actionError  = new ActionError("enterValidFile");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			}
		  }
		  return errors.isEmpty();
	}

      /******************************************************/
      /*******************Query builder**********************/
	  /******************************************************/



	  public static boolean checkQueryFieldsSelected(Object bean,ValidatorAction validAction,
								   Field field,ActionErrors errors,HttpServletRequest request)
			{

				 Log.log(Log.INFO,"Validator","checkQueryFieldsSelected","Entered");

			   ReportActionForm reportForm=(ReportActionForm)bean;

			   QueryBuilderFields queryFields = reportForm.getQueryBuilderFields();
			   if(!(queryFields.isApplnRefnoSelChkBox() ||queryFields.isCgpanSelChkBox() ||
			   queryFields.isApprovedAmtSelChkBox() || queryFields.isApprovedDateSelChkBox() ||
			  queryFields.isAppSubmittedSelChkBox() || queryFields.isBankRefNoSelChkBox() ||
			  queryFields.isChiefPromoterSelChkBox() || queryFields.isGuarFeeSelChkBox() ||
			  queryFields.isGuarFeeStartDateSelChkBox() || queryFields.isItPANSelChkBox() ||
			  queryFields.isProjectOutlaySelChkBox() || queryFields.isSsiDetailsSelChkBox() ||
			  queryFields.isTcInterestRateSelChkBox() || queryFields.isTcPLRSelChkBox() ||
			  queryFields.isTcSanctionedSelChkBox() || queryFields.isTcTenureSelChkBox() ||
			  queryFields.isWcPLRSelChkBox() || queryFields.isWcSanctionedSelChkBox()))
			  {
				  Log.log(Log.DEBUG,"Validator","checkQueryFieldsSelected","Found Errors...");
				  ActionError actionMessage=new ActionError("QuerySelectionRequired");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			  }

			  Log.log(Log.INFO,"Validator","checkQueryFieldsSelected","Exited");

			  return errors.isEmpty();
		   }

	/**
	 * This method is used to validate 2 amounts.
	 *
	 * This method checks whether amount 1 is greater than amount 2.
	 * An error is added if it is greater.
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateAmounts(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		String amount1=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String sProperty2=field.getVarValue("amount2");
		String amount2=ValidatorUtil.getValueAsString(bean, sProperty2);

		if ((! GenericValidator.isBlankOrNull(amount1) && Double.parseDouble(amount1) == 0.0))
		{
			amount1="";
		}
		if ((! GenericValidator.isBlankOrNull(amount2) && Double.parseDouble(amount2) == 0.0))
		{
			amount2="";
		}
		if ((!amount1.equals("") && !amount2.equals("")) && Double.parseDouble(amount1)>Double.parseDouble(amount2))
		{
			ActionError actionMessage=new ActionError("amount1GT"+sProperty2);
			errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		}
		return errors.isEmpty();
	}

	/**
	 * This method is used to validate whether atleast one transaction details are
	 * entered or not.
	 *
	 *
	 * @param bean
	 * @param validAction
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean checkTransactionDetails(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","checkTransactionDetails","Entered");

		DynaActionForm form=(DynaActionForm)bean;

		java.util.Map transactions=(java.util.Map)form.get("transactions");

		Set keys=transactions.keySet();

		Iterator iterator=keys.iterator();
		boolean fromToRequired=true;
		boolean natureRequired=true;
		boolean dateRequired=true;
		boolean valueDateRequired=true;
		boolean chequeNumberRequired=true;
		boolean amountRequired=true;

		while(iterator.hasNext())
		{
			Object key=iterator.next();

			TransactionDetail transactionDetail=(TransactionDetail)transactions.get(key);
			if(fromToRequired)
			{
				if(transactionDetail.getTransactionFromTo()==null
				|| transactionDetail.getTransactionFromTo().equals(""))
				{
					ActionError actionMessage=new ActionError("transactionFromToRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);

					fromToRequired=false;
				}

			}

			Log.log(Log.DEBUG,"Validator","checkTransactionDetails"," Transaction Date "+transactionDetail.getTransactionDate());

			if(dateRequired)
			{
				if(transactionDetail.getTransactionDate()==null
				|| transactionDetail.getTransactionDate().equals(""))
				{
					Log.log(Log.DEBUG,"Validator","checkTransactionDetails","Date not entered:"+transactionDetail.getTransactionDate());
					ActionError actionMessage=new ActionError("transactionDateRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);

					dateRequired=false;
				}
				else
				{
					String transactionDate=transactionDetail.getTransactionDate();

					if(transactionDate.trim().length()<10)
					{
						String [] errorStrs=new String[2];
						errorStrs[0]="Transaction Date ";
						errorStrs[1]="10";

						ActionError error=new ActionError("errors.minlength",errorStrs);

						errors.add(ActionErrors.GLOBAL_ERROR,error);
						dateRequired=false;

						Log.log(Log.DEBUG,"checkTransactionDetails","validate"," length is less than zero");
					}
					else
					{
						SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

						java.util.Date date=dateFormat.parse(transactionDate, new ParsePosition(0));
						Log.log(Log.DEBUG,"checkTransactionDetails","validate"," date "+date);

						if(date==null)
						{
							ActionError error=new ActionError("errors.date","Transaction Date");

							errors.add(ActionErrors.GLOBAL_ERROR,error);
							dateRequired=false;
						}

					}
				}
			}


			if(amountRequired)
			{
				if(transactionDetail.getTransactionAmount()==0)
				{
					ActionError actionMessage=new ActionError("transactionAmountRequired");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);

					amountRequired=false;
				}

			}

		}

		Log.log(Log.INFO,"Validator","checkTransactionDetails","Exited");

		return errors.isEmpty();

	}

    public static boolean validateDefaulterReportFields(Object bean,ValidatorAction validAction,
							   Field field,ActionErrors errors,HttpServletRequest request)
    {
		Log.log(Log.INFO,"Validator","validateDefaulterReportFields","Entered");

		DynaActionForm dynaForm=(DynaActionForm)bean;

        String borrowerUnitName = ((String)dynaForm.get("borrUnitName")).trim();
        String itpanOfTheUnit = ((String)dynaForm.get("itpanOfTheUnit")).trim();
        String chiefPromoterName = ((String)dynaForm.get("chiefPromoterName")).trim();
        java.util.Date chiefPromoterDOB = (java.util.Date)dynaForm.get("chiefPromoterDOB");
        String itpanOfChiefPromoter = ((String)dynaForm.get("itpanOfTheChiefPromoter")).trim();
        String legalIdOfChiefPromoter = ((String)dynaForm.get("legalIDOfTheChiefPromoter")).trim();

        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","borrowerUnitName :" + borrowerUnitName);
        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","itpanOfTheUnit :" + itpanOfTheUnit);
        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","chiefPromoterName :" + chiefPromoterName);
        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","chiefPromoterDOB :" + chiefPromoterDOB);
        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","itpanOfChiefPromoter :" + itpanOfChiefPromoter);
        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","legalIdOfChiefPromoter :" + legalIdOfChiefPromoter);


        if(((borrowerUnitName != null) && (borrowerUnitName.equals(""))) &&
           ((itpanOfTheUnit != null) && (itpanOfTheUnit.equals(""))) &&
           ((chiefPromoterName != null) && (chiefPromoterName.equals(""))) &&
           ((chiefPromoterDOB != null) && ((chiefPromoterDOB.toString()).equals(""))) &&
           ((itpanOfChiefPromoter != null) && (itpanOfChiefPromoter.equals(""))) &&
           ((legalIdOfChiefPromoter != null) && (legalIdOfChiefPromoter.equals("")))
          )
          {
			        Log.log(Log.INFO,"Validator","validateDefaulterReportFields","INVALID INPUT");
					ActionError actionMessage=new ActionError("invalidDefaulterReportInput");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
		  }

		return errors.isEmpty();
	}

	public static boolean validateBuySell(Object bean,ValidatorAction validAction,
								   Field field,ActionErrors errors,HttpServletRequest request)
		{
			String instValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
			String sProperty2=field.getVarValue("noOfUnits");
			String noOfUnitsValue=ValidatorUtil.getValueAsString(bean, sProperty2);

			String sProperty3=field.getVarValue("isBuyOrSellRequest");
			String buySellValue=ValidatorUtil.getValueAsString(bean, sProperty3);

			String sProperty4=field.getVarValue("investmentReferenceNumber");
			String invRefNoValue=ValidatorUtil.getValueAsString(bean, sProperty4);

			if (buySellValue.equalsIgnoreCase("S"))
			{
				if (GenericValidator.isBlankOrNull(invRefNoValue))
				{
					Log.log(Log.INFO,"Validator","validateBuySell","inv ref no not selected");
					ActionError actionMessage=new ActionError("errors.required", "Investment Reference Number");
					errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
				}
			}

			if ((!GenericValidator.isBlankOrNull(instValue) && !instValue.equalsIgnoreCase("FIXED DEPOSIT")) &&
				GenericValidator.isBlankOrNull(noOfUnitsValue))
			{
				Log.log(Log.INFO,"Validator","validateBuySell","units not entered");
				ActionError actionMessage=new ActionError("errors.required", "Number Of Units");
				errors.add(ActionErrors.GLOBAL_ERROR,actionMessage);
			}

			return errors.isEmpty();

		}

	public static boolean checkDefaultRate(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.DEBUG,"Validator","checkDefaultRate","entered");
		String isDefaultRateApplicable=ValidatorUtil.getValueAsString(bean, field.getProperty());

		Log.log(Log.DEBUG,"Validator","checkDefaultRate","isDefaultRateApplicable :" + isDefaultRateApplicable);

		String defaultRate=field.getVarValue("defaultRate");

		Log.log(Log.DEBUG,"Validator","checkDefaultRate","defaultRate :" + defaultRate);

		java.lang.Double defaultRateValue=new Double(ValidatorUtil.getValueAsString(bean, defaultRate));
		double defltRate=defaultRateValue.doubleValue();
		Log.log(Log.DEBUG,"Validator","checkDefaultRate","defaultRate :" + defltRate);

		if(((isDefaultRateApplicable != null) && (isDefaultRateApplicable.equals("Y"))) && defltRate <= 0.0)
		{
			ActionError error=new ActionError("defRateMoreThanZero");

			  errors.add(ActionErrors.GLOBAL_ERROR,error);
		}

		return errors.isEmpty();
	}

	public static boolean checkAlreadyExistInCombo(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{

		Log.log(Log.INFO,"Validator","checkAlreadyExistInCombo","Entered");

		//Get the text box property name.
		String textBoxProperty=field.getProperty();

		//Get the value entered in the text box.
		String textBoxValue=ValidatorUtil.getValueAsString(bean, textBoxProperty);

		//Get the combo box property name.
		String comboBoxProperty=field.getVarValue("fromCombo");

		Log.log(Log.DEBUG,"Validator","checkAlreadyExistInCombo","textBoxProperty "+textBoxProperty);
		Log.log(Log.DEBUG,"Validator","checkAlreadyExistInCombo","textBoxValue "+textBoxValue);
		Log.log(Log.DEBUG,"Validator","checkAlreadyExistInCombo","comboBoxProperty "+comboBoxProperty);

		DynaActionForm dynaForm=(DynaActionForm)bean;

		//Get the arraylist of values from the combo box.
		ArrayList comboBox=(ArrayList)dynaForm.get(comboBoxProperty);


		//If a value is entered in the text box check it against the values in the combobox.
		//If the value exists in the combobox error is thrown.

	   if(  (textBoxValue!=null)&& (!textBoxValue.equals("")) ){

		  int size=comboBox.size();

		  for(int i=0;i<size;i++){

			  String comboValue=(String)comboBox.get(i);

			  if((textBoxValue.trim()).equalsIgnoreCase(comboValue)){

				  ActionError actionError=new ActionError("fromCombo"+textBoxProperty);

				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			  }
		  }
	   }

	   Log.log(Log.INFO,"Validator","checkAlreadyExistInCombo","Exited");
		return errors.isEmpty();
	}


	public static boolean checkGFCardRateEntry(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		RPActionForm actionForm=(RPActionForm)bean;

		Map rateId = actionForm.getRateId();
		Map gfLowAmount = actionForm.getLowAmount();
		Map gfLowHigh = actionForm.getHighAmount();
		Map gfCardRate = actionForm.getGfRate();

		Set rateIdSet = rateId.keySet();
		Iterator rateIdIterator =rateIdSet.iterator();

		boolean rateValue = false;

		while(rateIdIterator.hasNext())
		{
			String key=(String)rateIdIterator.next();

			Iterator errorsIterator =errors.get();

			rateValue = false;

			if(gfCardRate.get(key).equals("")||(!gfCardRate.get(key).equals("") && Double.parseDouble((String)gfCardRate.get(key))==0))
			{
				rateValue = true;
				break;
			}
		}
		if(rateValue)
		{
			ActionError actionError=new ActionError("cardRateRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);

		}

		return errors.isEmpty();
	}

	public static boolean calMaturityDateAmt(Object bean, ValidatorAction validAction,
	Field field, ActionErrors errors, HttpServletRequest request)
	{
		Log.log(Log.INFO,"Validator","calMaturityDateAmt","Entered");

		IFProcessor ifProcessor=new IFProcessor();
		int balDays=0;
		String tenureType="";
		Date invDate =null;
		Date matDate = null;

		DynaActionForm dynaForm=(DynaActionForm) bean;

		tenureType=(String)dynaForm.get("tenureType");
		if (((String)dynaForm.get("instrumentName")).equalsIgnoreCase("Commercial Papers"))
		{
			tenureType="M";
		}		

		if (!((String)dynaForm.get("tenure")).equals(""))
		{
			int iTenure=Integer.parseInt((String)dynaForm.get("tenure"));
			Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","type from form " + tenureType);
			Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","tenure from form " + iTenure);
			matDate = (Date)dynaForm.get("maturityDate");
			Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","date from form " + matDate);
			Calendar calendar = Calendar.getInstance();

			if (dynaForm.get("dateOfInvestment")!=null && !((Date)dynaForm.get("dateOfInvestment")).toString().equals(""))
			{
				invDate = (Date)dynaForm.get("dateOfInvestment");
			}
			else
			{
				invDate = (Date)dynaForm.get("dateOfDeposit");
			}
			Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","inv date from form "+ invDate);
			calendar.setTime(invDate);
			if (tenureType.equalsIgnoreCase("D"))
			{
				calendar.add(Calendar.DATE, iTenure);
			}
			else if (tenureType.equalsIgnoreCase("M"))
			{
				calendar.add(Calendar.MONTH, iTenure);
			}
			else if (tenureType.equalsIgnoreCase("Y"))
			{
				calendar.add(Calendar.YEAR, iTenure);
			}
			matDate = calendar.getTime();
	
	
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day " + dayOfWeek);
	
			if (dayOfWeek == Calendar.SUNDAY)
			{
				Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day sun");
				ActionError actionError=new ActionError("matDateSunday");
				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				dynaForm.set("maturityDate", null);
			}
	
			if (errors.isEmpty())
			{
				matDate = calendar.getTime();
				dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day " + dayOfWeek);
	
				Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","date " + matDate);
				ArrayList holidays = new ArrayList();
				try 
				{
					holidays=ifProcessor.getAllHolidays();
				}
				catch(DatabaseException de)
				{
					Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","exception while getting holiday list "+ de.getMessage());
				}
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	
				Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","contains " + holidays.contains(matDate));
				if (holidays.contains(matDate))
				{
					ActionError actionError=new ActionError("matDateHoliday");
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					dynaForm.set("maturityDate", null);
				}
			}
		}

		Log.log(Log.INFO,"Validator","calMaturityDateAmt","Exited");
		return errors.isEmpty();
	}
	
	public static boolean checkExposureEntry(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		
		double liveInv  = 0;
		double investedAmt = 0;
		double maturedAmt = 0;
		double corpusAmt = 0;
		double otherAmt = 0;
		double expAmt = 0;
		
		if(dynaForm.get("availableLiveInv").equals("Y"))
		{
			liveInv = ((java.lang.Double)dynaForm.get("liveInvtAmount")).doubleValue();
		}
		if(dynaForm.get("availableInvAmount").equals("Y"))
		{
			investedAmt = ((java.lang.Double)dynaForm.get("investedAmount")).doubleValue();
		}
		if(dynaForm.get("availableMaturingAmount").equals("Y"))
		{
			maturedAmt = ((java.lang.Double)dynaForm.get("maturedAmount")).doubleValue();
		}
		if(dynaForm.get("availableCorpusAmount").equals("Y"))
		{
			corpusAmt = ((java.lang.Double)dynaForm.get("exposureCorpusAmount")).doubleValue();
		}
		if(dynaForm.get("otherReceiptsAmount").equals("Y"))
		{
			otherAmt = ((java.lang.Double)dynaForm.get("otherReceiptsAmount")).doubleValue();
		}
		if(dynaForm.get("availableExpAmount").equals("Y"))
		{
			expAmt = ((java.lang.Double)dynaForm.get("expenditureAmount")).doubleValue();
		}		
		
		double surplusAmount =liveInv + investedAmt + maturedAmt + corpusAmt + otherAmt - expAmt;
		
		if(surplusAmount<=0)
		{
			ActionError actionError=new ActionError("surplusAmountRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);			
			
		}
		
		return errors.isEmpty();
	}
	
	public static boolean checkStartEndNumber(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		int startNo = ((Integer)dynaForm.get("chqStartNo")).intValue();
		int endNo = ((Integer)dynaForm.get("chqEndingNo")).intValue();
		
		if(startNo > endNo)
		{
			ActionError actionError=new ActionError("startNoLesser");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);			
		}
		return errors.isEmpty();
	}	
	
	public static boolean validateNumberEntry(Object bean, ValidatorAction validAction,
						  Field field, ActionErrors errors, HttpServletRequest request)
	{
		InvestmentForm ifForm=(InvestmentForm)bean;

		Map chqId=ifForm.getChequeId();
		Set chqIdSet=chqId.keySet();
		Iterator chqIdSetIterator=chqIdSet.iterator();

		Map startNo=ifForm.getStartNo();
		Set startNoSet=startNo.keySet();
		Iterator startNoSetIterator=startNoSet.iterator();

		Map endNo=ifForm.getEndNo();
		Set endNoSet=endNo.keySet();
		Iterator endNoSetIterator=endNoSet.iterator();
		
		while(chqIdSetIterator.hasNext())
		{
			String key=(String)chqIdSetIterator.next();
			if((startNo.get(key)==null || startNo.get(key).equals(""))|| (startNo.get(key)!=null && !startNo.get(key).equals("") && Integer.parseInt((String)startNo.get(key))==0))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("startNoRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("startNoRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				
			}
			
			if((endNo.get(key)==null || endNo.get(key).equals(""))|| (endNo.get(key)!=null && !endNo.get(key).equals("") && Integer.parseInt((String)endNo.get(key))==0))
			{
				boolean remarksVal = false;

				Iterator errorsIterator =errors.get();

				while(errorsIterator.hasNext())
				{
					ActionError error=(ActionError)errorsIterator.next();
					if(error.getKey().equals("endNoRequired"))
					{
						remarksVal = true;
						break;
					}
				}
				if(!remarksVal)
				{

					ActionError actionError=new ActionError("endNoRequired");

					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				}
				
			}
			
			
			if((endNo.get(key)!=null && !endNo.get(key).equals("") && Integer.parseInt((String)endNo.get(key))!=0) && (startNo.get(key)!=null && !startNo.get(key).equals("") && Integer.parseInt((String)startNo.get(key))!=0))
			{
				if(Integer.parseInt((String)startNo.get(key)) > Integer.parseInt((String)endNo.get(key)))
				{
					boolean remarksVal = false;
	
					Iterator errorsIterator =errors.get();
	
					while(errorsIterator.hasNext())
					{
						ActionError error=(ActionError)errorsIterator.next();
						if(error.getKey().equals("startNoLesser"))
						{
							remarksVal = true;
							break;
						}
					}
					if(!remarksVal)
					{
	
						ActionError actionError=new ActionError("startNoLesser");
	
						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
				
			}
			
			
		}
		
		return errors.isEmpty();
	}


	/*
	 * This method if atleast one cheque has been cancelled
	 */
	 public static boolean validateChequeCancelled(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		InvestmentForm ifForm=(InvestmentForm)bean;

		Map cancelledChq=ifForm.getCancelledChq();
		Set cancelledChqSet=cancelledChq.keySet();
		Iterator cancelledChqIterator=cancelledChqSet.iterator();

		boolean clearVal=false;

		if(cancelledChq!=null && cancelledChq.size()!=0)
		{
			while(cancelledChqIterator.hasNext())
			{
				String key=(String)cancelledChqIterator.next();

				if (request.getParameter("cancelledChq("+key+")")!=null && cancelledChq.get(key)!=null && !(cancelledChq.get(key).equals("")))
				{
					clearVal=true;
					break;

				}
			}
		}

		if(!clearVal)
		{
			ActionError actionError=new ActionError("cancelledChqRequired");

			errors.add(ActionErrors.GLOBAL_ERROR,actionError);


		}

		return errors.isEmpty();
	}

	/*
	 * This method if atleast one cheque has been cancelled
	 */
	 public static boolean validateBankEntry(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		DynaActionForm dynaForm=(DynaActionForm)bean;
		String bankName = (String)dynaForm.get("bnkName");
		String instrumentType = (String)dynaForm.get("instrumentType");
		
		if(instrumentType.equals("CHEQUE"))
		{
			if(bankName==null || bankName.equals(""))
			{
				ActionError actionError=new ActionError("bankNameRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				
			}
		}
		
		return errors.isEmpty();
	}	
	
	/*
	 * This method if atleast one cheque has been cancelled
	 */
	 public static boolean validateClaimBankEntry(Object bean, ValidatorAction validAction,
							 Field field, ActionErrors errors, HttpServletRequest request)
	{
		ClaimActionForm dynaForm=(ClaimActionForm)bean;
		String bankName = dynaForm.getBnkName();
		String instrumentType = dynaForm.getModeOfPayment();
		
		if(instrumentType.equals("CHEQUE"))
		{
			if(bankName==null || bankName.equals(""))
			{
				ActionError actionError=new ActionError("bankNameRequired");

				errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				
			}
		}
		
		return errors.isEmpty();
	}
	
	public static boolean validateRatingAgency(Object bean, ValidatorAction validAction,
							Field field, ActionErrors errors, HttpServletRequest request)
   {
		DynaActionForm dynaForm=(DynaActionForm)bean;
		
		String agencyName = (String)dynaForm.get("ratingAgency");
		String[] ratings = (String[])dynaForm.get("allowableRating");
		
		IFProcessor ifProcessor =new IFProcessor();
		ArrayList ratingsList = new ArrayList();
		
		try{
			
			ArrayList agencyRatings = ifProcessor.showRatingAgencyWithRatings();
			for(int i=0; i<agencyRatings.size(); i++)
			{
				String rating = (String)agencyRatings.get(i);
				ratingsList.add(rating);	
			}
			
			if(agencyName!=null && !agencyName.equals(""))
			{
				if(!ratingsList.contains(agencyName))
				{
					if(ratings==null || ratings.length==0)
					{
						ActionError actionError=new ActionError("ratingsRequired");

						errors.add(ActionErrors.GLOBAL_ERROR,actionError);
					}
				}
			}
			
		}catch(DatabaseException e)
		{
			Log.logException(e);
		}
		
		
		
		return errors.isEmpty();
   }
   
   public static boolean validateDateFormat(Object bean, ValidatorAction validAction,
						   Field field, ActionErrors errors, HttpServletRequest request)throws Exception
	  {
		String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
		String fromString=field.getProperty();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	
		try{
			Log.log(Log.DEBUG,"Validator","validateDateFormat","date " + fromValue);	
			Date date = dateFormat.parse(fromValue, new ParsePosition(0));
			Log.log(Log.DEBUG,"Validator","validateDateFormat","date " + date);			
	
/*			if (!(GenericValidator.isBlankOrNull(fromValue)))
			{
	
				if(DateHelper.compareDates(fromValue,stringDate)!=0 && DateHelper.compareDates(fromValue,stringDate)!=1)
				{
					ActionError actionError=new ActionError("currentDate" + fromString);
					errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	
	
				}
			}*/

		}catch(NumberFormatException numberFormatException){
	
			ActionError actionError=new ActionError("errors.date", fromString);
			errors.add(ActionErrors.GLOBAL_ERROR,actionError);
	
		}
	
	
		return errors.isEmpty();
	  }   

	  public static boolean checkMaturityDate(Object bean, ValidatorAction validAction,
	  Field field, ActionErrors errors, HttpServletRequest request)
	  {
		  Log.log(Log.INFO,"Validator","calMaturityDateAmt","Entered");

		  IFProcessor ifProcessor=new IFProcessor();
		  Date invDate =null;
		  Date matDate = null;

		  DynaActionForm dynaForm=(DynaActionForm) bean;

		  if (matDate!=null && !matDate.toString().equals(""))
		  {
			  Calendar calendar = Calendar.getInstance();
			  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","date from form not null");
			  calendar.setTime(matDate);

			  int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day " + dayOfWeek);
	
			  if (dayOfWeek == Calendar.SUNDAY)
			  {
				  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day sun");
				  ActionError actionError=new ActionError("matDateSunday");
				  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
			  }
	
			  if (errors.isEmpty())
			  {
				  matDate = calendar.getTime();
				  dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","day " + dayOfWeek);
	
				  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","date " + matDate);
				  ArrayList holidays = new ArrayList();
				  try 
				  {
					  holidays=ifProcessor.getAllHolidays();
				  }
				  catch(DatabaseException de)
				  {
					  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","exception while getting holiday list "+ de.getMessage());
				  }
				  SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	
				  Log.log(Log.DEBUG,"Validator","calMaturityDateAmt","contains " + holidays.contains(matDate));
				  if (holidays.contains(matDate))
				  {
					  ActionError actionError=new ActionError("matDateHoliday");
					  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
				  }
			  }
		  }

		  Log.log(Log.INFO,"Validator","calMaturityDateAmt","Exited");
		  return errors.isEmpty();
	  }
          
          
       
           
    //added on 23/10/2013       
           
     public static boolean validateFromNPACurrentDatesNew(Object bean, ValidatorAction validAction,
                                         Field field, ActionErrors errors, HttpServletRequest request)throws Exception
     {
     DynaValidatorActionForm  gmPeriodicInfoForm = (DynaValidatorActionForm)bean;
     GMDAO gmDAO = new GMDAO() ;
           String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
           String fromString=field.getProperty();
     
     java.util.Date npaDate = (java.util.Date)gmPeriodicInfoForm.get("npaDt");
     
     String borrowerId = ((String)gmPeriodicInfoForm.get("borrowerId")).toUpperCase();
     int count  = gmDAO.getExceptionBIDCount(borrowerId);
     //  System.out.println("Borrower Id for Update Npa details:"+borrowerId);
     
     Date npaCreatedDate = (java.util.Date)gmPeriodicInfoForm.get("npaCreatedDate");
     
     //  System.out.println("NPA Date:"+npaDate);
       java.util.Date currentDate=new java.util.Date();
       SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
     
     String toValue = null;
     Calendar calendar = Calendar.getInstance();
     calendar.setTime(currentDate);
     int year = calendar.get(Calendar.YEAR);
     int month = calendar.get(Calendar.MONTH);
     int day = calendar.get(Calendar.DATE);
     int temp = 0;
     if(month >=0 && month <=2){
     year=year-1;
     calendar.set(Calendar.MONTH,9);
     calendar.set(Calendar.DATE,1);
     calendar.set(Calendar.YEAR,year);
     temp=0;
     }
     else if(month >= 3 && month <=5 ){
     calendar.set(Calendar.MONTH,0);
     calendar.set(Calendar.DATE,1);
     calendar.set(Calendar.YEAR,year);
     temp=1;
     }
     else if(month >=6 && month <=8){
     calendar.set(Calendar.MONTH,3);
     calendar.set(Calendar.DATE,1);
     calendar.set(Calendar.YEAR,year);
     temp=2;
     }
     else if(month >=9 && month <=11){
     calendar.set(Calendar.MONTH,6);
     calendar.set(Calendar.DATE,1);
     calendar.set(Calendar.YEAR,year);
     temp=3;
     }
     
     java.util.Date toDate = calendar.getTime() ;
     toValue = dateFormat.format(toDate);
               //      System.out.println("To Date:"+toValue);
       try{

               String stringDate=dateFormat.format(currentDate);

               if (!(GenericValidator.isBlankOrNull(fromValue)))
               {

                       if(DateHelper.compareDates(fromValue,stringDate)!=0 && DateHelper.compareDates(fromValue,stringDate)!=1)
                       {
                               ActionError actionError=new ActionError("currentDate" + fromString);
                               errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                       }
                   if(npaCreatedDate != null)
                   {
                       if(count == 0) {
                         if(DateHelper.compareDates(toValue,fromValue)!=0 && DateHelper.compareDates(toValue,fromValue)!=1)
                            {
                             //    
                                                     ActionError actionError=new ActionError("newNpaDate"+temp);
                                                     errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                             //   
                         } 
                       }
                    }
               } 
     }catch(NumberFormatException numberFormatException){

               ActionError actionError=new ActionError("errors.date", fromString);
               errors.add(ActionErrors.GLOBAL_ERROR,actionError);

       }

       return errors.isEmpty();
     }
    
    
    public static boolean validateRequiredFields(Object bean, ValidatorAction validAction,
                                        Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
    	
    	System.out.println("Validator..validateRequiredFields...S");
    DynaValidatorActionForm  newNpaForm = (DynaValidatorActionForm)bean;
    double totalApprovedAmount = (java.lang.Double)newNpaForm.get("totalApprovedAmount");
    //      java.util.Date lastInspectionDt = (java.util.Date)newNpaForm.get("lastInspectionDt");
    int total = (Integer)newNpaForm.get("size");
    double totalSecurityAsOnSanc = (java.lang.Double)newNpaForm.get("totalSecurityAsOnSanc");
    double totalSecurityAsOnNpa = (java.lang.Double)newNpaForm.get("totalSecurityAsOnNpa");
    
    String insdt = ValidatorUtil.getValueAsString(bean,field.getProperty());
    
    String lastInspectionDt = ValidatorUtil.getValueAsString(bean,"lastInspectionDt");
    String networthAsOnSancDtStr = ValidatorUtil.getValueAsString(bean,"networthAsOnSancDt");
    String networthAsOnNpaDtStr = ValidatorUtil.getValueAsString(bean,"networthAsOnNpaDt");
    String reasonForReductionNpa = ValidatorUtil.getValueAsString(bean,"reasonForReductionAsOnNpaDt");
    
    /*checking subsidy date and amount */
    String subsidyFlag = (String)newNpaForm.get("subsidyFlag");
    String subsidyRcvdFlag = ValidatorUtil.getValueAsString(bean,"isSubsidyRcvd");
    String subAdjustedFlag = (String)newNpaForm.get("isSubsidyAdjusted");
    String subsidyDt = ValidatorUtil.getValueAsString(bean,"subsidyLastRcvdDt");
    String subsidyAmt = ValidatorUtil.getValueAsString(bean,"subsidyLastRcvdAmt");
    
    if("Y".equals(subsidyFlag)){
        if("Y".equals(subsidyRcvdFlag)){
            if("Y".equals(subAdjustedFlag)){
                if(GenericValidator.isBlankOrNull(subsidyDt)){
                    ActionError actionError=new ActionError("subsidyDate");
                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                }
                if(!GenericValidator.isBlankOrNull(subsidyAmt)){
                    if(((Double)Double.parseDouble(subsidyAmt)).doubleValue() <= 0.0){
                         ActionError actionError=new ActionError("subsidyAmount");
                        errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                    }
                }else{
                     ActionError actionError=new ActionError("subsidyAmount");
                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                }
            }else if(GenericValidator.isBlankOrNull(subAdjustedFlag)){
                ActionError actionError=new ActionError("subAdjustedFlag");
                errors.add(ActionErrors.GLOBAL_ERROR,actionError);
            }
        }else if(GenericValidator.isBlankOrNull(subsidyRcvdFlag)){
            ActionError actionError=new ActionError("subsidyRcvdFlag");
            errors.add(ActionErrors.GLOBAL_ERROR,actionError);
        }
    }
    /*checking for last inspection date*/
    
    if(totalApprovedAmount > 750000){
      if(GenericValidator.isBlankOrNull(lastInspectionDt)){
          ActionError actionError=new ActionError("LastInspectionDate");
          errors.add(ActionErrors.GLOBAL_ERROR,actionError);
      }
    }
    
    /*checking for reason for reduction in security amount*/
    if(GenericValidator.isBlankOrNull(reasonForReductionNpa)){
      if(totalSecurityAsOnNpa < totalSecurityAsOnSanc){
          ActionError actionError=new ActionError("ReasonForReduction");
          errors.add(ActionErrors.GLOBAL_ERROR,actionError);
      }
    }
    
    
    /*checking networth*/
    
    if(GenericValidator.isBlankOrNull(networthAsOnSancDtStr) || ((Double)Double.parseDouble(networthAsOnSancDtStr)).doubleValue() == 0.0){
       ActionError actionError=new ActionError("NetworthAmountSanc");
       errors.add(ActionErrors.GLOBAL_ERROR,actionError);
    }
    
    if(GenericValidator.isBlankOrNull(networthAsOnNpaDtStr) ||  ((Double)Double.parseDouble(networthAsOnNpaDtStr)).doubleValue() == 0.0){
       ActionError actionError=new ActionError("NetworthAmountNpa");
       errors.add(ActionErrors.GLOBAL_ERROR,actionError);
    }
    
    /*checking security vales*/
    double landval = 0.0;
    double buildval = 0.0;
    double machineval = 0.0;
    double movval = 0.0;
    double currval = 0.0;
    double othersval = 0.0;
    
    Map securityAsOnSancDt = (Map)newNpaForm.get("securityAsOnSancDt");
    Map securityAsOnNpaDt = (Map)newNpaForm.get("securityAsOnNpaDt");
    String landvalstr = (String)securityAsOnSancDt.get("LAND");
    String buildvalstr = (String)securityAsOnSancDt.get("BUILDING");
    String machinevalstr = (String)securityAsOnSancDt.get("MACHINE");
    String movvalstr = (String)securityAsOnSancDt.get("OTHER_FIXED_MOVABLE_ASSETS");
    String currvalstr = (String)securityAsOnSancDt.get("CUR_ASSETS");
    String othersvalstr = (String)securityAsOnSancDt.get("OTHERS");
    
    if(!GenericValidator.isBlankOrNull(landvalstr)){
      landval = Double.parseDouble(landvalstr);
    }
    if(!GenericValidator.isBlankOrNull(buildvalstr)){
      buildval = Double.parseDouble(buildvalstr);
    }
    if(!GenericValidator.isBlankOrNull(machinevalstr)){
      machineval = Double.parseDouble(machinevalstr);
    }
    if(!GenericValidator.isBlankOrNull(movvalstr)){
      movval = Double.parseDouble(movvalstr);
    }
    if(!GenericValidator.isBlankOrNull(currvalstr)){
      currval = Double.parseDouble(currvalstr);
    }
    if(!GenericValidator.isBlankOrNull(othersvalstr)){
      othersval = Double.parseDouble(othersvalstr);
    }
    
    if(landval <= 0 && buildval <= 0 && machineval <= 0 && movval <= 0 && currval <= 0 && othersval <= 0){
              ActionError actionError=new ActionError("secuirtyValuesAsOnSanc");
              errors.add(ActionErrors.GLOBAL_ERROR,actionError);
          }
          
    
    /*    landvalstr = (String)securityAsOnNpaDt.get("LAND");
    buildvalstr = (String)securityAsOnNpaDt.get("BUILDING");
    machinevalstr = (String)securityAsOnNpaDt.get("MACHINE");
    movvalstr = (String)securityAsOnNpaDt.get("OTHER_FIXED_MOVABLE_ASSETS");
    currvalstr = (String)securityAsOnNpaDt.get("CUR_ASSETS");
    othersvalstr = (String)securityAsOnNpaDt.get("OTHERS");
    
          landval = 0.0;
          buildval = 0.0;
          machineval = 0.0;
          movval = 0.0;
          currval = 0.0;
          othersval = 0.0;
    
    if(!GenericValidator.isBlankOrNull(landvalstr)){
      landval = Double.parseDouble(landvalstr);
    }
    if(!GenericValidator.isBlankOrNull(buildvalstr)){
      buildval = Double.parseDouble(buildvalstr);
    }
    if(!GenericValidator.isBlankOrNull(machinevalstr)){
      machineval = Double.parseDouble(machinevalstr);
    }
    if(!GenericValidator.isBlankOrNull(movvalstr)){
      movval = Double.parseDouble(movvalstr);
    }
    if(!GenericValidator.isBlankOrNull(currvalstr)){
      currval = Double.parseDouble(currvalstr);
    }
    if(!GenericValidator.isBlankOrNull(othersvalstr)){
      othersval = Double.parseDouble(othersvalstr);
    }
          
    if(landval <= 0 && buildval <= 0 && machineval <= 0 && movval <= 0 && currval <= 0 && othersval <= 0){
               ActionError actionError=new ActionError("secuirtyValuesAsOnNpa");
               errors.add(ActionErrors.GLOBAL_ERROR,actionError);
           }
    */
    
    /*checking for instalment date,disbursement date,moratorium,total disbursed amt,repayment amt,outstanding amt*/
    boolean isFirstDisbTaken = false;
    boolean isLastDisbTaken = false;
    boolean isFirstInstTaken = false;
    boolean isPMoratoriumTaken = false;
    boolean isIMoratoriumTaken = false;
    boolean isDisbAmtTaken = false;
    boolean isPRepayTaken = false;
    boolean isIRepayTaken = false;
    boolean isPOSTaken = false;
    boolean isIOSTaken = false;
    
    String cgpan = null;
    String guarStartDt = null;
    String sanctionDt = null;
    String firstDisbDt = null;
    String lastDisbDt = null;
    String firstInstDt = null;
    String moratoriumPrincipal = null;
    String moratoriumInterest = null;
    
    String totalDisbAmt = null;
    String repayPrincipal = null;
    String repayInterest = null;
    String outstandingPrincipal = null;
    String outstandingInterest = null;
    
    //     String approvedAmount = null;
    String interestRate = null;
    
    for(int i=1;i<=total;i++){
    
      cgpan = "cgpan"+i;
      guarStartDt = "guarStartDt"+i;
      sanctionDt = "sanctionDt"+i;
      firstDisbDt = "firstDisbDt"+i;
      lastDisbDt = "lastDisbDt"+i;
      firstInstDt = "firstInstDt"+i;
      moratoriumPrincipal = "moratoriumPrincipal"+i;
    //      moratoriumInterest = "moratoriumInterest"+i;
      
      totalDisbAmt = "totalDisbAmt"+i;
      repayPrincipal = "repayPrincipal"+i;
      repayInterest = "repayInterest"+i;
      outstandingPrincipal = "outstandingPrincipal"+i;
      outstandingInterest = "outstandingInterest"+i;
      
    //      approvedAmount = "approvedAmount"+i;
      interestRate = "interestRate"+i;
      
      String cgpanNo = (String)newNpaForm.get(cgpan);
      String loanType = cgpanNo.substring(cgpanNo.length()-2);
      
      
      java.util.Date firstDisbursedmentDate = (java.util.Date)newNpaForm.get(firstDisbDt);
      java.util.Date firstInstalmentDate = (java.util.Date)newNpaForm.get(firstInstDt);
      
      
      

          String firstDisbursedmentDateStr = ValidatorUtil.getValueAsString(bean,firstDisbDt);
          String lastDisbursedmentDateStr = ValidatorUtil.getValueAsString(bean,lastDisbDt);
          String firstInstalmentDateStr = ValidatorUtil.getValueAsString(bean,firstInstDt);
    /*      String moratoriumPrincipalStr = ValidatorUtil.getValueAsString(bean,moratoriumPrincipal);
          String moratoriumInterestStr = ValidatorUtil.getValueAsString(bean,moratoriumInterest);
    */
          String totalDisbAmtStr = ValidatorUtil.getValueAsString(bean,totalDisbAmt);
          String repayPrincipalStr = ValidatorUtil.getValueAsString(bean,repayPrincipal);
          String repayInterestStr = ValidatorUtil.getValueAsString(bean,repayInterest);
          String outstandingPrincipalStr = ValidatorUtil.getValueAsString(bean,outstandingPrincipal);
          String outstandingInterestStr = ValidatorUtil.getValueAsString(bean,outstandingInterest);
    //      String approvedAmountStr = ValidatorUtil.getValueAsString(bean,approvedAmount);
    //       String interestRateStr = ValidatorUtil.getValueAsString(bean,interestRate);
          
          double rate = (Double)newNpaForm.get(interestRate);
          double pos = (Double)newNpaForm.get(outstandingPrincipal);
          double ios = (Double)newNpaForm.get(outstandingInterest);
          
          double amt = Math.round((pos*rate*3)/(12*100));
          //double minAmt = Math.round(95*amt/100);
           double minAmt = Math.round(70*amt/100);
          //double maxAmt = Math.round(105*amt/100);
           double maxAmt = Math.round(130*amt/100);
          if(ios <= maxAmt && ios >= minAmt){
          
          }else{
              String msg = "interestOverDues";
              ActionError actionError=new ActionError(msg);
              errors.add(ActionErrors.GLOBAL_ERROR,actionError);
          }
    
      
          if("TC".equals(loanType) || "CC".equals(loanType)){
              int principalMoratorium = (Integer)newNpaForm.get(moratoriumPrincipal);
              if(!(GenericValidator.isBlankOrNull(firstDisbursedmentDateStr)) && !(GenericValidator.isBlankOrNull(firstInstalmentDateStr))){
              
                  Calendar cal = Calendar.getInstance();
                  cal.setTime(firstDisbursedmentDate);
                  //System.out.println("firstDisbursedmentDate after adding moratorium:"+cal.getTime());
                  cal.add(Calendar.MONTH,principalMoratorium);
                  //System.out.println("firstDisbursedmentDate after adding moratorium:"+cal.getTime());
                  
                  SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                  Date d = cal.getTime();
                  String dstr = dateFormat.format(d);
                  Date disbDateWithPeriod = dateFormat.parse(dstr);
                 
                  
                  Date instalmentDate = dateFormat.parse(firstInstalmentDateStr);
                  
                  long difference = instalmentDate.getTime()-disbDateWithPeriod.getTime();
                  long dayDiff = difference/(1000*24*60*60);
                  //System.out.println("difference in days:"+dayDiff);
                  
                  if(dayDiff > 45){
                      ActionError actionError=new ActionError("DisbursementAndInstalmentDate");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
          
              if(GenericValidator.isBlankOrNull(firstDisbursedmentDateStr)){
                  if(!isFirstDisbTaken){
                      isFirstDisbTaken = true;
                      ActionError actionError=new ActionError("FirstDisbursementDate");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
          
              if(GenericValidator.isBlankOrNull(lastDisbursedmentDateStr)){
                  if(!isLastDisbTaken){
                      isLastDisbTaken = true;
                      ActionError actionError=new ActionError("LastDisbursementDate");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
              if(GenericValidator.isBlankOrNull(firstInstalmentDateStr)){
                  if(!isFirstInstTaken){
                      isFirstInstTaken = true;
                      ActionError actionError=new ActionError("FirstInstalmentDate");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
    /*            if(GenericValidator.isBlankOrNull(moratoriumPrincipalStr)){
                  if(!isPMoratoriumTaken){
                      isPMoratoriumTaken = true;
                      ActionError actionError=new ActionError("PrincipalMoratorium");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
              if(GenericValidator.isBlankOrNull(moratoriumInterestStr)){
                  if(!isIMoratoriumTaken){
                      isIMoratoriumTaken = true;
                      ActionError actionError=new ActionError("InterestMoratorium");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              } 
    */
              
              if(GenericValidator.isBlankOrNull(totalDisbAmtStr) || ((Double)Double.parseDouble(totalDisbAmtStr)).doubleValue() == 0.0){
                  if(!isDisbAmtTaken){
                      isDisbAmtTaken = true;
                      ActionError actionError=new ActionError("TotalDisbursedAmount");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
              if(GenericValidator.isBlankOrNull(repayPrincipalStr) ||  ((Double)Double.parseDouble(repayPrincipalStr)).doubleValue() == 0.0){
                  if(!isPRepayTaken){
                      isPRepayTaken = true;
                      ActionError actionError=new ActionError("PrincipalRepayAmount");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
              if(GenericValidator.isBlankOrNull(repayInterestStr) ||  ((Double)Double.parseDouble(repayInterestStr)).doubleValue() == 0.0){
                  if(!isIRepayTaken){
                      isIRepayTaken = true;
                      ActionError actionError=new ActionError("InterestRepayAmount");
                      errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                  }
              }
              
          }
     
          if(GenericValidator.isBlankOrNull(outstandingPrincipalStr) ||  ((Double)Double.parseDouble(outstandingPrincipalStr)).doubleValue() == 0.0){
              if(!isPOSTaken){
                  isPOSTaken = true;
                  ActionError actionError=new ActionError("PrincipalOutstandingAmount");
                  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
              }
          }
      
          if(GenericValidator.isBlankOrNull(outstandingInterestStr) ||  ((Double)Double.parseDouble(outstandingInterestStr)).doubleValue() == 0.0){
              if(!isIOSTaken){
                  isIOSTaken = true;
                  ActionError actionError=new ActionError("InterestOutstandingAmount");
                  errors.add(ActionErrors.GLOBAL_ERROR,actionError);
              }
          }
          
          
      
    }
    System.out.println("validateRequiredFields....E");
    
    return errors.isEmpty();
    }
    
    
    /*this mathod will compare npa date with earliest guarantee start date*/
    public static boolean validateDates(Object bean, ValidatorAction validAction,
                                              Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
         DynaValidatorActionForm  newNpaForm = (DynaValidatorActionForm)bean;
         GMDAO gmDAO = new GMDAO() ;
      //   String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
        java.util.Date npaDate = (java.util.Date)newNpaForm.get("npaDt");
        String npaDateStr = ValidatorUtil.getValueAsString(bean, "npaDt");
        String propertyName = field.getProperty();
        System.out.println("fromString:"+propertyName);
        System.out.println("npaDateStr:"+npaDateStr);
        String borrowerId = ((String)newNpaForm.get("borrowerId")).toUpperCase();
        int count  = gmDAO.getExceptionBIDCount(borrowerId);
         
         int total = (Integer)newNpaForm.get("size"); 
         
         String guarStartDt = null;
        ArrayList dates = new ArrayList();
            for(int i=1;i<=total;i++){
                guarStartDt = "guarStartDt"+i;
             //  String guarStartDateStr = ValidatorUtil.getValueAsString(bean,guarStartDt);
                Date gsd = (Date)newNpaForm.get(guarStartDt);
                dates.add(gsd);
               
            }
            Collections.sort(dates);
            Date guarDate = null;
            Iterator itr = dates.iterator();
            if(itr.hasNext()){
                 guarDate = (Date)itr.next();
            }
    
         System.out.println("NPA Date:"+npaDate);
     //    java.util.Date currentDate=new java.util.Date();//here in place of current date, take earliest guarstartdate
         SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
    
        String toValue = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(guarDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int temp = 0;
        if(month >=0 && month <=2){
        year=year-1;
        calendar.set(Calendar.MONTH,9);
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.YEAR,year);
        temp=0;
        }
        else if(month >= 3 && month <=5 ){
        calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.YEAR,year);
        temp=1;
        }
        else if(month >=6 && month <=8){
        calendar.set(Calendar.MONTH,3);
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.YEAR,year);
        temp=2;
        }
        else if(month >=9 && month <=11){
        calendar.set(Calendar.MONTH,6);
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.YEAR,year);
        temp=3;
        }
    
    java.util.Date toDate = calendar.getTime() ;      
    toValue = dateFormat.format(toDate);
                    //      System.out.println("To Date:"+toValue);
            try{

                    String guarDateStr=dateFormat.format(guarDate);//guarDate

                    if (!(GenericValidator.isBlankOrNull(npaDateStr)))//if npa date is not null
                    {

                        //    if(DateHelper.compareDates(npaDateStr,guarDateStr)< 0)
                        if(!GenericValidator.isBlankOrNull(npaDateStr)){
                            if((npaDate.compareTo(guarDate)< 0))
                            {
                                    ActionError actionError=new ActionError("NpaGuarDate");
                                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                            }
                        }
//                            if(count == 0) {
//                            if(DateHelper.compareDates(toValue,npaDateStr)!=0 && DateHelper.compareDates(toValue,npaDateStr)!=1)
//                                 {
//                              ActionError actionError=new ActionError("newNpaDate"+temp);
//                                                            errors.add(ActionErrors.GLOBAL_ERROR,actionError);
//                            } 
//                            }
                    } 
       }catch(NumberFormatException numberFormatException){

                    ActionError actionError=new ActionError("errors.date", propertyName);
                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);

            }

            return errors.isEmpty();
    }
	
        
    public static boolean npaRecallDates(Object bean, ValidatorAction validAction,
                                              Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
    ClaimActionForm  cpTcDetailsForm = (ClaimActionForm)bean;
   
            String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
           
    String recallDtStr = cpTcDetailsForm.getDateOfRecallNotice();
    String reasonForIssueRecallNotice = cpTcDetailsForm.getReasonForIssueRecallNotice();
    HashMap npaDetails = cpTcDetailsForm.getNpaDetails();
    String npaDtStr = (String)npaDetails.get("NPAClassifiedDate");
  //  Date npaDt = (java.util.Date)npaDetails.get("NPAClassifiedDate");
   
   
    Date npaDt = null;
    Date recallDt = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
   
    
            try{
            if(!GenericValidator.isBlankOrNull(npaDtStr)){
                 npaDt = sdf.parse(npaDtStr);
            }
            if(!GenericValidator.isBlankOrNull(recallDtStr)){
                recallDt = sdf.parse(recallDtStr);
            }
            
                    if (!(GenericValidator.isBlankOrNull(recallDtStr)))
                    {

                            if((npaDt.compareTo(recallDt) > 0) && GenericValidator.isBlankOrNull(reasonForIssueRecallNotice))
                            {
                                    ActionError actionError=new ActionError("reasonForIssueRecallNotice");
                                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                            }
                    } 
       }catch(NumberFormatException numberFormatException){

                    ActionError actionError=new ActionError("errors.date");
                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);

            }

            return errors.isEmpty();
    }  
    
    public static boolean npaLegalDate(Object bean, ValidatorAction validAction,
                                              Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
    ClaimActionForm  cpTcDetailsForm = (ClaimActionForm)bean;
    
    String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
           
    String legalDtStr = cpTcDetailsForm.getLegaldate();
    String reasonForFilingSuit = cpTcDetailsForm.getReasonForFilingSuit();
    HashMap npaDetails = cpTcDetailsForm.getNpaDetails();
    String npaDtStr = (String)npaDetails.get("NPAClassifiedDate");
    Date npaDt =  null;
    Date legalDt = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try{
          
                   if(!GenericValidator.isBlankOrNull(npaDtStr)){
                        npaDt = sdf.parse(npaDtStr);
                   }
                   if(!GenericValidator.isBlankOrNull(legalDtStr)){
                       legalDt = sdf.parse(legalDtStr);
                   }
                 
                  

                    if (!(GenericValidator.isBlankOrNull(legalDtStr)))
                    {

                            if((npaDt.compareTo(legalDt) > 0) && GenericValidator.isBlankOrNull(reasonForFilingSuit))
                            {
                                    ActionError actionError=new ActionError("reasonForFilingSuit");
                                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                            }
            } 
       }catch(NumberFormatException numberFormatException){

                    ActionError actionError=new ActionError("errors.date");
                    errors.add(ActionErrors.GLOBAL_ERROR,actionError);

            }

            return errors.isEmpty();
    }
    
    public static boolean checkSuitAmount(Object bean, ValidatorAction validAction,
                                              Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
        ClaimActionForm  cpTcDetailsForm = (ClaimActionForm)bean;
        String suitAmount = (String)cpTcDetailsForm.getAmountclaimed();
        String forumName = (String)cpTcDetailsForm.getForumthrulegalinitiated();
        if(!GenericValidator.isBlankOrNull(suitAmount)){
            double suitamt = Double.parseDouble(suitAmount);
            if(suitamt <= 0 && !"Securitisation Act, 2002".equals(forumName)){
                ActionError actionError=new ActionError("suitAmount");
                errors.add(ActionErrors.GLOBAL_ERROR,actionError);
            }
        }
        return errors.isEmpty();  
    }
    
    
    public static boolean npaAssetPossessionDate(Object bean, ValidatorAction validAction,
                                        Field field, ActionErrors errors, HttpServletRequest request)throws Exception
    {
    ClaimActionForm  cpTcDetailsForm = (ClaimActionForm)bean;
    
    String fromValue=ValidatorUtil.getValueAsString(bean, field.getProperty());
     
    
    String forumName = cpTcDetailsForm.getForumthrulegalinitiated();
    Date assetDt = cpTcDetailsForm.getAssetPossessionDate();
    String assetDtStr = ValidatorUtil.getValueAsString(bean,"assetPossessionDate");
    
    
    
    HashMap npaDetails = cpTcDetailsForm.getNpaDetails();
    String npaDtStr = (String)npaDetails.get("NPAClassifiedDate");
    Date npaDt =  null;
    Date legalDt = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      try{
          if("Securitisation Act, 2002".equals(forumName)){  
             if(!GenericValidator.isBlankOrNull(npaDtStr)){
                  npaDt = sdf.parse(npaDtStr);
             }
             if(!GenericValidator.isBlankOrNull(assetDtStr)){
                 legalDt = sdf.parse(assetDtStr);
             }else{
                 ActionError actionError=new ActionError("assetPossessionDateRequired");
                 errors.add(ActionErrors.GLOBAL_ERROR,actionError);
             }
           
              if (!(GenericValidator.isBlankOrNull(assetDtStr)))
              {

                      if((npaDt.compareTo(assetDt) > 0))
                      {
                              ActionError actionError=new ActionError("assetPossessionDate");
                              errors.add(ActionErrors.GLOBAL_ERROR,actionError);
                      }
            } 
          }
    }catch(NumberFormatException numberFormatException){

              ActionError actionError=new ActionError("errors.date");
              errors.add(ActionErrors.GLOBAL_ERROR,actionError);

      }

      return errors.isEmpty();
    }
    
    
    public static boolean validateClaimPageFields(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
            throws Exception
        {
            ClaimActionForm cpForm = (ClaimActionForm)bean;
            String inclusionOfReciept = cpForm.getInclusionOfReciept();
            if(GenericValidator.isBlankOrNull(inclusionOfReciept))
            {
                ActionError actionError = new ActionError("inclusionOfReciept");
                errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
            }
            String forumName = cpForm.getForumthrulegalinitiated();
            String suitAmount = cpForm.getAmountclaimed();
            if(!GenericValidator.isBlankOrNull(suitAmount))
            {
                double suitamt = Double.parseDouble(suitAmount);
                if(suitamt <= 0.0D && !"Securitisation Act, 2002".equals(forumName))
                {
                    ActionError actionError = new ActionError("suitAmount");
                    errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                }
            }
            String assetDtStr = ValidatorUtil.getValueAsString(bean, "assetPossessionDate");
            if("Securitisation Act, 2002".equals(forumName) && GenericValidator.isBlankOrNull(assetDtStr))
            {
                ActionError actionError = new ActionError("assetPossessionDateRequired");
                errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
            }
            String officerName = cpForm.getDealingOfficerName();
            if(GenericValidator.isBlankOrNull(officerName))
            {
                ActionError actionError = new ActionError("officerName");
                errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
            }
            String subsidyFlag = cpForm.getSubsidyFlag();
            String subsidyRcvdFlag = cpForm.getIsSubsidyRcvdAfterNpa();
            String subAdjustedFlag = cpForm.getIsSubsidyAdjustedOnDues();
            String subsidyDt = ValidatorUtil.getValueAsString(bean, "subsidyDate");
            String subsidyAmt = ValidatorUtil.getValueAsString(bean, "subsidyAmt");
            if("Y".equals(subsidyFlag))
                if("Y".equals(subsidyRcvdFlag))
                {
                    if("Y".equals(subAdjustedFlag))
                    {
                        if(GenericValidator.isBlankOrNull(subsidyDt))
                        {
                            ActionError actionError = new ActionError("subsidyDate");
                            errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                        }
                        if(!GenericValidator.isBlankOrNull(subsidyAmt))
                        {
                            if(Double.valueOf(Double.parseDouble(subsidyAmt)).doubleValue() <= 0.0D)
                            {
                                ActionError actionError = new ActionError("subsidyAmount");
                                errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                            }
                        } else
                        {
                            ActionError actionError = new ActionError("subsidyAmount");
                            errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                        }
                    } else
                    if(GenericValidator.isBlankOrNull(subAdjustedFlag))
                    {
                        ActionError actionError = new ActionError("subAdjustedFlag");
                        errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                    }
                } else
                if(GenericValidator.isBlankOrNull(subsidyRcvdFlag))
                {
                    ActionError actionError = new ActionError("subsidyRcvdFlag");
                    errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                }
            String isAcctFraud = cpForm.getIsAcctFraud();
            String isEnquiryConcluded = cpForm.getIsEnquiryConcluded();
            String isMliInvolved = cpForm.getIsMLIInvolved();
            if("Y".equals(isAcctFraud))
                if("Y".equals(isEnquiryConcluded))
                {
                    if(GenericValidator.isBlankOrNull(isMliInvolved))
                    {
                        ActionError actionError = new ActionError("isMliInvolved");
                        errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                    }
                } else
                if(GenericValidator.isBlankOrNull(isEnquiryConcluded))
                {
                    ActionError actionError = new ActionError("isEnquiryConcluded");
                    errors.add("org.apache.struts.action.GLOBAL_ERROR", actionError);
                }
            return errors.isEmpty();
        }
        
        
    public static boolean validateClaimApproval(Object bean, ValidatorAction validAction, Field field, ActionErrors errors, HttpServletRequest request)
            throws Exception
        {          
            ClaimActionForm claimForm = (ClaimActionForm)bean;
            Map approvedClaimAmounts = claimForm.getApprovedClaimAmount();
            Set approvedClaimAmountsSet = approvedClaimAmounts.keySet();
            Iterator approvedClaimAmountIterator = approvedClaimAmountsSet.iterator();
            while(approvedClaimAmountIterator.hasNext()) 
            {
                String key = (String)approvedClaimAmountIterator.next();
                String cgtsidecision = (String)claimForm.getDecision(key);
                int index = key.indexOf("C");
                String tempkey = "F#";
                tempkey = (new StringBuilder()).append(tempkey).append(key.substring(index)).toString();
                String rejectionReason = "";
                rejectionReason = (String)claimForm.getReasonData(tempkey);
                String msg = "<li>Please specify rejection reason for "+key.substring(index)+"</li>";
                if("RT".equals(cgtsidecision) && rejectionReason.equals(""))
                {
                    String.format(Locale.ENGLISH,msg,null);
                    ActionError actionError = new ActionError(msg);                   
                    errors.add("org.apache.struts.action.GLOBAL_ERROR",actionError);
                }
            }
            return errors.isEmpty();
        }
    
    
    public static boolean PrimarySecurityCheck(Object bean, ValidatorAction validAction,
			Field field, ActionErrors errors, HttpServletRequest request)
    {
    	DynaActionForm dynaForm  = (DynaActionForm)bean;
    	String flag = (String)dynaForm.get("pSecurity");
    	if(flag.equals("N"))
    	{
    		ActionError actionError  = new ActionError("PlsEnterSecFalg");
    		errors.add(ActionErrors.GLOBAL_ERROR,actionError);
    	}
    	return errors.isEmpty();
    }           
}
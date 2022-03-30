/*************************************************************
   *
   * Name of the class: IFProcessor
   * This is main manager level class of this module.
   *
   * @author : Ramvel
   * @version:
   * @since:13-October-2003.
   * Modification History.
   * Name:			Date
   * Veldurai T		03-01-2004.
   **************************************************************/
package com.cgtsi.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.upload.FormFile;

import com.cgtsi.common.Constants;
import com.cgtsi.common.Log;
import com.cgtsi.actionform.InvestmentForm;
import com.cgtsi.admin.User;
import com.cgtsi.investmentfund.IFProcessor;
import com.cgtsi.investmentfund.*;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.util.PropertyLoader;
import com.cgtsi.common.NoDataException;
import com.cgtsi.common.MessageException;
import com.cgtsi.admin.MenuOptions_back;

import com.cgtsi.receiptspayments.RpConstants;
import com.cgtsi.receiptspayments.RpProcessor;
import com.cgtsi.receiptspayments.Voucher;
import com.cgtsi.receiptspayments.VoucherDetail;
import com.cgtsi.util.CustomisedDate;
import com.cgtsi.guaranteemaintenance.GMProcessor;
import com.cgtsi.guaranteemaintenance.CgpanDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import com.cgtsi.util.DBConnection;
import java.sql.Connection;
import java.sql.CallableStatement;

import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.ArrayList;
//import com.cgtsi.util.CustomisedDate;
import java.util.Calendar;

import com.cgtsi.reports.GeneralReport;
/**
 * @author VT8150
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IFAction extends BaseAction
{

	/* (non-Javadoc)
	* @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/


	public ActionForward updateAllowableRatingsForAgency(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		Log.log(Log.INFO,"IFAction","updateAllowableRatingsForAgency","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;
		IFProcessor ifProcessor = new IFProcessor();
		RatingDetails ratingDetails = null;
		ArrayList ratingDetailsArray =new ArrayList();
		
		String agencyName = (String)dynaForm.get("ratingAgency");
		//System.out.println("agencyName:"+agencyName);
		
		String[] ratingName = (String[])dynaForm.get("allowableRating");
		ArrayList ratings =ifProcessor.getAllRatings();
		int  ratingsSize = ratings.size();
		
		for(int j=0; j<ratingsSize; j++)
		{
			String mainRating =  (String) ratings.get(j);
			
			for(int i=0; i<ratingName.length; i++) 
			{
				if(mainRating.equals(ratingName[i]))
				{
					ratingDetails = new RatingDetails();
					ratingDetails.setRating(mainRating);
					ratingDetails.setStatus("A");
					ratingDetails.setRatingAgency(agencyName);
					break;
				}
				else
				{
					ratingDetails = new RatingDetails();
					ratingDetails.setStatus("I");
				}
			}
			if(ratingDetails.getStatus().equals("I"))
			{
				ratingDetails = new RatingDetails();
				ratingDetails.setRating(mainRating);
				ratingDetails.setStatus("I");
				ratingDetails.setRatingAgency(agencyName);
			}
			ratingDetailsArray.add(ratingDetails);
			int ratingDetailsArraySize = ratingDetailsArray.size();
		}
		ArrayList agencyNames = ifProcessor.showRatingAgencyWithRatings();
		if(agencyNames.contains(agencyName))
		{
			ifProcessor.updateAllowableRatingsForAgency(ratingDetailsArray);
			request.setAttribute("message","Rating Agency Details Updated Successfully");			 
		}
		else
		{
			ifProcessor.insertAllowableRatingsForAgency(ratingDetailsArray);
			request.setAttribute("message","Rating Agency Details Inserted Successfully");			
		}
		Log.log(Log.INFO,"IFAction","updateAllowableRatingsForAgency","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	
	
	public ActionForward showAllowableRatingsForAgency(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showAllowableRatingsForAgency","Entered");
		HttpSession session =request.getSession(false);
		session.setAttribute("statusFlag", "0");
		
		ArrayList agencyNames = new ArrayList();
		ArrayList ratings = new ArrayList();
		
		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		IFProcessor ifProcessor = new IFProcessor();
		agencyNames = ifProcessor.showRatingAgency();
		dynaForm.set("agencies",agencyNames);
		ratings =ifProcessor.getAllRatings();
		dynaForm.set("allowableRatings",ratings);

		Log.log(Log.INFO,"IFAction","showAllowableRatingsForAgency","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}
	
	public ActionForward showAllowableRatingsForAgencyDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showAllowableRatingsForAgencyDetails","Entered");
		IFProcessor ifProcessor = new IFProcessor();
		HttpSession session =request.getSession(false);
		session.setAttribute("statusFlag", "1");
		
		ArrayList agencyNames = new ArrayList();
		ArrayList ratings = new ArrayList();
		
		DynaActionForm dynaForm=(DynaActionForm)form;
		String agencyName = (String)dynaForm.get("ratingAgency");
		ratings =ifProcessor.getAllRatings();
		dynaForm.set("allowableRatings",ratings);		
		
		//dynaForm.set("agencies",agencyNames);
		
		if (agencyName!=null && !agencyName.equals(""))
		{
			agencyNames = ifProcessor.getRatingsForAgency(agencyName);
			//agencyNames.add("ALL");
			String agencyArr[]=new String[agencyNames.size()];
			for (int i=0; i<agencyNames.size(); i++)
			{
				agencyArr[i]=(String)agencyNames.get(i);
			}

			dynaForm.set("allowableRating", agencyArr);
		}
		else{
			dynaForm.set("allowableRatings",ratings);	
		}

		Log.log(Log.INFO,"IFAction","showAllowableRatingsForAgencyDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward updateRatingAgency(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateRatingAgency","Entered");
		DynaActionForm dynaForm = (DynaActionForm)form;
		IFProcessor ifProcessor = new IFProcessor();
		RatingDetails ratingDetails = new RatingDetails();
		BeanUtils.populate(ratingDetails,dynaForm.getMap());
		
		String oldAgency = ratingDetails.getAgency();
		//System.out.println("OldAgency:"+oldAgency);
				
		String agency = (String) dynaForm.get("newAgency");
		
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		ratingDetails.setUser(user);
		
		String desc = ratingDetails.getModAgencyDesc();
		//System.out.println("Description:"+desc);
		
		String modName = ratingDetails.getModAgencyName();
		//System.out.println("Modified Name:"+modName);
		
		if((agency ==  null) || (agency.equals("")))
		{
			ifProcessor.updateRatingAgency(ratingDetails);
			Log.log(Log.INFO,"IFAction","updateRatingAgency","Exited");
			request.setAttribute("message","Rating Agency Details Updated Successfully");
			
			return mapping.findForward("success");
		}
		else
		{
			ifProcessor.insertRatingAgency(ratingDetails);
			Log.log(Log.INFO,"IFAction","updateRatingAgency","Exited");
			
			request.setAttribute("message","Rating Agency Details Inserted Successfully");
			
			return mapping.findForward("success");
		}
	}


	public ActionForward showRatingAgencyDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showRatingAgencyDetails","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("statusFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String agencyName = (String)dynaForm.get("agency");

		RatingDetails ratDtl = new RatingDetails();
		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showRatingAgencyDetails","mat type " + agencyName);
		if (agencyName!=null && !agencyName.equals(""))
		{
			ratDtl = ifProcessor.showRatingAgencyDetails(agencyName);

			dynaForm.set("modAgencyName", ratDtl.getRatingAgency());
			dynaForm.set("modAgencyDesc", ratDtl.getRatingDescription());
			dynaForm.set("newAgency", "");
		}
		else
		{
			dynaForm.set("modAgencyName", "");
			dynaForm.set("modAgencyDesc", "");
			dynaForm.set("newAgency", "");
		}

		Log.log(Log.INFO,"IFAction","showRatingAgencyDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}	
	
	
	public ActionForward showRatingAgency(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showRatingAgency","Entered");
		HttpSession session =request.getSession(false);
		session.setAttribute("statusFlag", "0");
		
		ArrayList agencyNames = new ArrayList();
		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		dynaForm.initialize(mapping);
		IFProcessor ifProcessor = new IFProcessor();
		agencyNames = ifProcessor.showRatingAgency();
		
		dynaForm.set("agencies",agencyNames);
		Log.log(Log.INFO,"IFAction","showRatingAgency","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}



	public ActionForward bankStatementUploadResult( 
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","bankStatementUploadResult","Entered");
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		XMLParser xmlParser = new XMLParser();
		HttpSession session = (HttpSession)request.getSession(false);
		StatementDetail statementDetail = new StatementDetail(); 
		String path="";
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();


		FormFile formFile = (FormFile)dynaForm.get("bankStatementUploadFile");
		//System.out.println("formFile:"+formFile);
	
		String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
		String contextPath = PropertyLoader.changeToOSpath(contextPath1);
		
		String fileName=contextPath+File.separator+
						Constants.FILE_DOWNLOAD_DIRECTORY+File.separator+
								formFile.getFileName();
		   FileOutputStream fileOut=new FileOutputStream(fileName);
		   InputStream input=formFile.getInputStream();
		   int readByte=0;
		   byte[] buffer = new byte[1024];
		   while((readByte=(int)input.read(buffer,0,buffer.length))!=-1)
		   {
			   fileOut.write(buffer,0,readByte);
		
			   //Log.log(Log.DEBUG,"CommonAction","uploadFile","writing ...");
		   }
		   buffer=null;
		   fileOut.flush();
		   fileOut.close();
		   input.close();
		   formFile.destroy();
		   File file=new File(fileName);  
		   String abPath = file.getAbsolutePath(); 
		   //System.out.println("XML invoked");
		   statementDetail = xmlParser.xmlParse(abPath);
		   statementDetail.setUserId(user);
		   //System.out.println("Processor invoked");
		   ifProcessor.bankStatementUploadResult(statementDetail);

		Log.log(Log.INFO,"IFAction","bankStatementUploadResult","Exited");
		request.setAttribute("message","Bank Statement Uploaded successfully");
		return mapping.findForward("success");
	}


	public ActionForward accruedInterestIncomeReportInput( 
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
			Log.log(Log.INFO,"IFAction","accruedInterestIncomeReportInput","Entered");
			DynaActionForm dynaForm=(DynaActionForm)form;
			Date date = new Date();
			Calendar calendar =Calendar.getInstance();
			Dates dates =  new Dates();
			dates.setDateOfTheDocument17(date);
			BeanUtils.copyProperties(dynaForm, dates);

			Log.log(Log.INFO,"IFAction","accruedInterestIncomeReportInput","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	
	public ActionForward accruedInterestIncomeReport( 
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
			Log.log(Log.INFO,"IFAction","accruedInterestIncomeReport","Entered");
			DynaActionForm dynaForm=(DynaActionForm)form;
			ArrayList dan = new ArrayList();
			IFProcessor ifProcessor = new IFProcessor();
			java.sql.Date endDate = null;
			java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument17");
			endDate = new java.sql.Date (eDate.getTime());
			dan = (ArrayList)ifProcessor.accruedInterestIncomeReport(endDate);
			dynaForm.set("mliDetails",dan);


			if(dan == null || dan.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","accruedInterestIncomeReport","Exited");
				return mapping.findForward(Constants.SUCCESS);
			}

	}



	public ActionForward investeeWiseReportInput(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investeeWiseReportInput","Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day= day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE,day);
		Date prevDate = calendar.getTime();

		Dates generalReport = new Dates();
		generalReport.setDateOfTheDocument15(prevDate); 
		generalReport.setDateOfTheDocument16(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO,"IFAction","investeeWiseReportInput","Exited");
		return mapping.findForward("success");
		}
		
	public ActionForward investeeWiseReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investeeWiseReport","Entered");

		ArrayList fdReport = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument15");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null) 
		{
			startDate = new java.sql.Date (sDate.getTime()); 
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument16");
		endDate = new java.sql.Date (eDate.getTime());

			fdReport = ifProcessor.getFdReportForMaturity(startDate,endDate);
			dynaForm.set("fdReport",fdReport);
			if(fdReport == null  || fdReport.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
			Log.log(Log.INFO,"IFAction","investeeWiseReport","Exited");
			return mapping.findForward("success");
			}
		}
		
		
	public ActionForward investeeWiseReportDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investeeWiseReportDetails","Entered");
		ArrayList fdReceiptDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String investee = request.getParameter("investee");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument15");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument16");
		endDate = new java.sql.Date (eDate.getTime());
//		System.out.println("Processor Invoked");
		fdReceiptDetails = (ArrayList)ifProcessor.investeeWiseReportDetails(investee,startDate,endDate);
		dynaForm.set("fdReport",fdReceiptDetails);

		if(fdReceiptDetails == null || fdReceiptDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
		Log.log(Log.INFO,"IFAction","investeeWiseReportDetails","Exited");
		return mapping.findForward("success");
		}
	}		
	
	public ActionForward investeeWiseReportDetailsSummary(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investeeWiseReportDetailsSummary","Entered");
		ArrayList fdReceiptDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String investee = request.getParameter("investee");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument15");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument16");
		endDate = new java.sql.Date (eDate.getTime());
//		System.out.println("Processor Invoked");
		fdReceiptDetails = (ArrayList)ifProcessor.investeeWiseReportDetailsSummary(investee,startDate,endDate);
		dynaForm.set("fdReport",fdReceiptDetails);

		if(fdReceiptDetails == null || fdReceiptDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
		Log.log(Log.INFO,"IFAction","investeeWiseReportDetailsSummary","Exited");
		return mapping.findForward("success"); 
		}
	}


/*	public ActionForward bankStatementUploadResult( 
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","bankStatementUploadResult","Entered");
		DynaActionForm dynaForm = (DynaActionForm)form;
		XMLParser xmlParser = new XMLParser();
		HttpSession session = (HttpSession)request.getSession(false);
		StatementDetail statementDetail = new StatementDetail(); 
		String path="";


		FormFile formFile = (FormFile)dynaForm.get("bankStatementUploadFile");
		//System.out.println("formFile:"+formFile);
	
		String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
		String contextPath = PropertyLoader.changeToOSpath(contextPath1);
		
		String fileName=contextPath+File.separator+
						Constants.FILE_DOWNLOAD_DIRECTORY+File.separator+
								formFile.getFileName();
		   FileOutputStream fileOut=new FileOutputStream(fileName);
		   InputStream input=formFile.getInputStream();
		   int readByte=0;
		   byte[] buffer = new byte[1024];
		   while((readByte=(int)input.read(buffer,0,buffer.length))!=-1)
		   {
			   fileOut.write(buffer,0,readByte);
		
			   //Log.log(Log.DEBUG,"CommonAction","uploadFile","writing ...");
		   }
		   buffer=null;
		   fileOut.flush();
		   fileOut.close();
		   input.close();
		   formFile.destroy();
		   File file=new File(fileName);  
		   String abPath = file.getAbsolutePath(); 
		   statementDetail = xmlParser.xmlParse(abPath);
		   
		   ArrayList transactionArray = statementDetail.getTransactionDetail(); 
		   int transactionArraySize = transactionArray.size();
/*		
		   String transactionFromTo=null;
		   String transactionNature=null;
		   String transactionDate=null;
		   String valueDate=null;
		   String chequeNumber=null;
		   double transactionAmount=0;		
		
		   for(int i=0; i<transactionArraySize; i++)
		   {
			   TransactionDetail transactionDetail =  (TransactionDetail) transactionArray.get(i);
			   System.out.println("transactionFromTo:"+transactionDetail.getTransactionFromTo());
			   System.out.println("transactionNature:"+transactionDetail.getTransactionNature());
			   System.out.println("transactionDate:"+transactionDetail.getTransactionDate());
			   System.out.println("valueDate:"+transactionDetail.getValueDate());
			   System.out.println("chequeNumber:"+transactionDetail.getChequeNumber());
			   System.out.println("transactionAmount:"+transactionDetail.getTransactionAmount()); 
		   }		   

		Log.log(Log.INFO,"IFAction","bankStatementUploadResult","Exited");
		return mapping.findForward("success");
	}*/


// Fix on 17.9.2004
	public ActionForward chequeDetailsUpdateSuccessForPayment( 
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdateSuccessForPayment","Entered");
		IFProcessor ifProcessor=new IFProcessor();
		ChequeDetails chequeDetails  = new ChequeDetails();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String chequeNumber = (String) dynaForm.get("number");
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		BeanUtils.populate(chequeDetails,dynaForm.getMap());
		String bank = (String) dynaForm.get("bankName");
		//System.out.println("bank:"+bank);
		int i = bank.indexOf(",");
		//System.out.println("i:"+i);
		String newBank =  bank.substring(0,i);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setBankName(newBank);

		int j = bank.indexOf("(");
		//System.out.println("j:"+j);
		String newBranch =  bank.substring(i+1,j);
		//System.out.println("newBranch:"+newBranch);
		chequeDetails.setBranchName(newBranch);

		chequeDetails.setUserId(user);
		ifProcessor.chequeDetailsUpdateSuccess(chequeDetails,chequeNumber);

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdateSuccessForPayment","Exited");
		return mapping.findForward("success");
	}

// Fix Completed


	public ActionForward fdiReportInput(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","fdiReportInput","Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day= day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE,day);
		Date prevDate = calendar.getTime();

		Dates generalReport = new Dates();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO,"IFAction","fdiReportInput","Exited");
		return mapping.findForward("success");
		}

	public ActionForward chequeDetailsReportInput(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsReportInput","Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day= day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE,day);
		Date prevDate = calendar.getTime();

		Dates generalReport = new Dates();
		generalReport.setDateOfTheDocument2(prevDate);
		generalReport.setDateOfTheDocument3(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO,"IFAction","chequeDetailsReportInput","Exited");
		return mapping.findForward("success");
		}

	public ActionForward investmentReportInput(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentReportInput","Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day= day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE,day);
		Date prevDate = calendar.getTime();

		Dates generalReport = new Dates();
		generalReport.setDateOfTheDocument5(prevDate);
		generalReport.setDateOfTheDocument6(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO,"IFAction","investmentReportInput","Exited");
		return mapping.findForward("success");
		}

	public ActionForward investmentMaturityReportInput(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "investmentMaturityReportInput", "Entered");
			DynaActionForm dynaForm = (DynaActionForm)form;
			Date date = new Date();
			Calendar calendar =Calendar.getInstance();
			Dates dates =  new Dates();
			dates.setDateOfTheDocument7(date);
			BeanUtils.copyProperties(dynaForm, dates);

			Log.log(Log.INFO, "IFAction", "investmentMaturityReportInput", "Exited");
			return mapping.findForward("display");
	}


	public ActionForward investmentMaturityReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentMaturityReport","Entered");

		ArrayList investment = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		InvestmentDetails investmentDetails = new InvestmentDetails();

		java.sql.Date startDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument7");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
			dynaForm.set("documentDate",startDate);
		}

		Calendar calendar =Calendar.getInstance();
		calendar.setTime(startDate);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int year = calendar.get(Calendar.YEAR);

		day = day + 15;
		calendar.set(Calendar.DATE,day);
		Date date1 = calendar.getTime();
		java.sql.Date sqlDate1 = new java.sql.Date (date1.getTime());
		dynaForm.set("documentDate1",date1);


		day = day + 1;
		calendar.set(Calendar.DATE,day);
		Date date2 = calendar.getTime();
		java.sql.Date sqlDate2 = new java.sql.Date (date2.getTime());
		dynaForm.set("documentDate2",date2);

		day = day + 14;
		calendar.set(Calendar.DATE,day);
		Date date3 = calendar.getTime();
		java.sql.Date sqlDate3 = new java.sql.Date (date3.getTime());
		dynaForm.set("documentDate3",date3);

		day = day + 1;
		calendar.set(Calendar.DATE,day);
		Date date4 = calendar.getTime();
		java.sql.Date sqlDate4 = new java.sql.Date (date4.getTime());
		dynaForm.set("documentDate4",date4);

		month = month + 2 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date5 = calendar.getTime();
		java.sql.Date sqlDate5 = new java.sql.Date (date5.getTime());
		dynaForm.set("documentDate5",date5);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date6 = calendar.getTime();
		java.sql.Date sqlDate6 = new java.sql.Date (date6.getTime());
		dynaForm.set("documentDate6",date6);

		month = month + 3 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date7= calendar.getTime();
		java.sql.Date sqlDate7 = new java.sql.Date (date7.getTime());
		dynaForm.set("documentDate7",date7);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date8= calendar.getTime();
		java.sql.Date sqlDate8 = new java.sql.Date (date8.getTime());
		dynaForm.set("documentDate8",date8);

		month = month + 6 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date9= calendar.getTime();
		java.sql.Date sqlDate9 = new java.sql.Date (date9.getTime());
		dynaForm.set("documentDate9",date9);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date10= calendar.getTime();
		java.sql.Date sqlDate10 = new java.sql.Date (date10.getTime());
		dynaForm.set("documentDate10",date10);

		year = year + 2;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		Date date11= calendar.getTime();
		java.sql.Date sqlDate11 = new java.sql.Date (date11.getTime());
		dynaForm.set("documentDate11",date11);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date12= calendar.getTime();
		java.sql.Date sqlDate12 = new java.sql.Date (date12.getTime());
		dynaForm.set("documentDate12",date12);

		year = year + 2;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		Date date13= calendar.getTime();
		java.sql.Date sqlDate13 = new java.sql.Date (date13.getTime());
		dynaForm.set("documentDate13",date13);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date14= calendar.getTime();
		java.sql.Date sqlDate14 = new java.sql.Date (date14.getTime());
		dynaForm.set("documentDate14",date14);

		investment = ifProcessor.investmentMaturityReport(startDate,sqlDate1,sqlDate2,
		sqlDate3,sqlDate4,sqlDate5,sqlDate6,sqlDate7,sqlDate8,sqlDate9,sqlDate10,
		sqlDate11,sqlDate12,sqlDate13,sqlDate14);

		dynaForm.set("investmentArray",investment);

		if(investment == null || investment.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","investmentMaturityReport","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward investmentMaturityReportDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentMaturityReportDetails","Entered");

		ArrayList investment = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		InvestmentDetails investmentDetails = new InvestmentDetails();
		String instrument = (String) dynaForm.get("number");
		//System.out.println("instrument:"+instrument);
		String range = (String) dynaForm.get("flag");
		//System.out.println("range:"+range);

		java.sql.Date startDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument7");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
			//System.out.println("startDate:"+startDate);
		}

		Calendar calendar =Calendar.getInstance();
		calendar.setTime(startDate);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int year = calendar.get(Calendar.YEAR);

		day = day + 15;
		calendar.set(Calendar.DATE,day);
		Date date1 = calendar.getTime();
		java.sql.Date sqlDate1 = new java.sql.Date (date1.getTime());
		//System.out.println("sqlDate1:"+sqlDate1);

		day = day + 1;
		calendar.set(Calendar.DATE,day);
		Date date2 = calendar.getTime();
		java.sql.Date sqlDate2 = new java.sql.Date (date2.getTime());
		//System.out.println("sqlDate2:"+sqlDate2);

		day = day + 14;
		calendar.set(Calendar.DATE,day);
		Date date3 = calendar.getTime();
		java.sql.Date sqlDate3 = new java.sql.Date (date3.getTime());
		//System.out.println("sqlDate3:"+sqlDate3);

		day = day + 1;
		calendar.set(Calendar.DATE,day);
		Date date4 = calendar.getTime();
		java.sql.Date sqlDate4 = new java.sql.Date (date4.getTime());
		//System.out.println("sqlDate4:"+sqlDate4);

		month = month + 2 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date5 = calendar.getTime();
		java.sql.Date sqlDate5 = new java.sql.Date (date5.getTime());
		//System.out.println("sqlDate5:"+sqlDate5);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date6 = calendar.getTime();
		java.sql.Date sqlDate6 = new java.sql.Date (date6.getTime());
		//System.out.println("sqlDate6:"+sqlDate6);

		month = month + 3 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date7= calendar.getTime();
		java.sql.Date sqlDate7 = new java.sql.Date (date7.getTime());
		//System.out.println("sqlDate7:"+sqlDate7);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date8= calendar.getTime();
		java.sql.Date sqlDate8 = new java.sql.Date (date8.getTime());
		//System.out.println("sqlDate8:"+sqlDate8);

		month = month + 6 ;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		Date date9= calendar.getTime();
		java.sql.Date sqlDate9 = new java.sql.Date (date9.getTime());
		//System.out.println("sqlDate9:"+sqlDate9);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date10= calendar.getTime();
		java.sql.Date sqlDate10 = new java.sql.Date (date10.getTime());
		//System.out.println("sqlDate10:"+sqlDate10);

		year = year + 2;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		Date date11= calendar.getTime();
		java.sql.Date sqlDate11 = new java.sql.Date (date11.getTime());
		//System.out.println("sqlDate11:"+sqlDate11);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date12= calendar.getTime();
		java.sql.Date sqlDate12 = new java.sql.Date (date12.getTime());
		//System.out.println("sqlDate12:"+sqlDate12);

		year = year + 2;
		day = day-1;
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		Date date13= calendar.getTime();
		java.sql.Date sqlDate13 = new java.sql.Date (date13.getTime());
		//System.out.println("sqlDate13:"+sqlDate13);

		day = day+1;
		calendar.set(Calendar.DATE, day);
		Date date14= calendar.getTime();
		java.sql.Date sqlDate14 = new java.sql.Date (date14.getTime());
		//System.out.println("sqlDate14:"+sqlDate14);

		if(range.equals("range1"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(startDate,sqlDate1,instrument);
		}
		else if(range.equals("range2"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate2,sqlDate3,instrument);
		}
		else if(range.equals("range3"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate4,sqlDate5,instrument);
		}
		else if(range.equals("range4"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate6,sqlDate7,instrument);
		}
		else if(range.equals("range5"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate8,sqlDate9,instrument);
		}
		else if(range.equals("range6"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate10,sqlDate11,instrument);
		}
		else if(range.equals("range7"))
		{
			investment = ifProcessor.investmentMaturityReportDetails(sqlDate12,sqlDate13,instrument);
		}
		else
		{
			investment = ifProcessor.investmentMaturityReportDetailsForEndDate(sqlDate14,instrument);
		}
		dynaForm.set("investmentArray",investment);

		if(investment == null || investment.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","investmentMaturityReportDetails","Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward investmentReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentReport","Entered");
		ArrayList investmentDetails = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument5");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument6");
		endDate = new java.sql.Date (eDate.getTime());
		investmentDetails = ifProcessor.investmentReport(startDate,endDate);
		dynaForm.set("investmentArray",investmentDetails);

		if(investmentDetails == null || investmentDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","investmentReport","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward investmentReportDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentReportDetails","Entered");
		ArrayList investmentDetails = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String type = (String) dynaForm.get("number");
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument5");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument6");
		endDate = new java.sql.Date (eDate.getTime());
		investmentDetails = ifProcessor.investmentReportDetails(startDate,endDate,type);
		dynaForm.set("investmentArray",investmentDetails);

		if(investmentDetails == null || investmentDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","investmentReportDetails","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward investmentReportDetailsFinal(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Entered");
		ArrayList investmentDetails = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		String type = (String) dynaForm.get("number");
		String investmentType =  type.substring(0,2);
		if(investmentType.equals("FI"))
		{
			investmentDetails = ifProcessor.investmentReportDetailsForFixedDeposit(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success1");
			}
		}
		else if(investmentType.equals("MU"))
		{
			investmentDetails = ifProcessor.investmentReportDetailsForMutualFund(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success2");
			}
		}
		else if(investmentType.equals("BO"))
		{
			investmentDetails = ifProcessor.investmentReportDetailsForBonds(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success3");
			}
		}
		else if(investmentType.equals("CO"))
		{
			investmentDetails = ifProcessor.investmentReportDetailsForCommercialpapers(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success4");
			}
		}
		else if(investmentType.equals("DE"))
		{
			investmentDetails = ifProcessor.investmentReportDetailsForDebentures(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success5");
			}
		}
		else
		{
			investmentDetails = ifProcessor.investmentReportDetailsForGSecurities(type);
			dynaForm.set("investmentArray",investmentDetails);

			if(investmentDetails == null || investmentDetails.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
				Log.log(Log.INFO,"IFAction","investmentReportDetailsFinal","Exited");
				return mapping.findForward("success6");
			}
		}
	}


	public ActionForward chequeDetailsInsertInput(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsInsertInput","Entered" );
		ArrayList bankDetails =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		ArrayList bankNames=new ArrayList(bankDetails.size());
		String bankName="";
		for(int i=0;i<bankDetails.size();i++)
		{
			BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
			bankName=bankAccountDetail.getBankName()
			+" ,"+bankAccountDetail.getBankBranchName()+
							"("+bankAccountDetail.getAccountNumber()+")";

			bankNames.add(bankName);
		}
		dynaForm.set("banks",bankNames);
		bankDetails = null;
		ifProcessor = null;
		Log.log(Log.INFO,"IFAction","chequeDetailsInsertInput","Exited");
		return mapping.findForward("success");
	}



	public ActionForward chequeDetailsInsertSuccess(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsInsertSuccess","Entered");
		IFProcessor ifProcessor=new IFProcessor();

		ChequeDetails chequeDetails  = new ChequeDetails();

		DynaActionForm dynaForm = (DynaActionForm)form;
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		String bank = (String) dynaForm.get("bankName");
		System.out.println("bank:"+bank);

		BeanUtils.populate(chequeDetails,dynaForm.getMap());
		int i = bank.indexOf(",");
		//System.out.println("i:"+i);
		String newBank =  bank.substring(0,i);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setBankName(newBank);

		int j = bank.indexOf("(");
		//System.out.println("j:"+j);
		String newBranch =  bank.substring(i+1,j);
		//System.out.println("newBranch:"+newBranch);
		chequeDetails.setBranchName(newBranch);

		chequeDetails.setUserId(user);
		//System.out.println("remarks:"+remarks);
		//chequeDetails.setAddedDate(new java.util.Date());

		String contextPath = request.getSession(false).getServletContext().getRealPath("");
		Log.log(Log.DEBUG,"RPAction","getPaymentsMade","path " + contextPath);
		ifProcessor.chequeDetailsInsertSuccess(chequeDetails,contextPath,user);

		Log.log(Log.INFO,"IFAction","chequeDetailsInsertSuccess","Exited");
		return mapping.findForward("success");
	}




	public ActionForward chequeDetailsUpdate(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdate","Entered");
		ArrayList cheque = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());

		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument3");
		endDate = new java.sql.Date (eDate.getTime());
		cheque = ifProcessor.chequeDetailsModify(startDate,endDate);
		dynaForm.set("chequeArray",cheque);

		if(cheque == null || cheque.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","chequeDetailsUpdate","Exited");
			return mapping.findForward("success");
		}

	}


	public ActionForward chequeDetailsUpdatePage(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdatePage","Entered");
		ChequeDetails cheque = new ChequeDetails();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
//		String chequeNumber = (String) dynaForm.get("number");
		String chequeNumber = (String) request.getParameter("number");
		if (chequeNumber==null)
		{
			chequeNumber =(String) dynaForm.get("chequeId");
		}
		cheque = ifProcessor.chequeDetailsUpdatePageReport(chequeNumber);
		ArrayList bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		ArrayList bankNames=new ArrayList(bankDetails.size());
		String bankName="";
		for(int i=0;i<bankDetails.size();i++)
		{
			BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
			bankName=bankAccountDetail.getBankName()
			+ " ,"+bankAccountDetail.getBankBranchName()+
			"("+bankAccountDetail.getAccountNumber()+")";

			bankNames.add(bankName);
		}
		dynaForm.set("banks",bankNames);
		//System.out.println("bank name :" + cheque.getBankName());
		dynaForm.set("bankName",cheque.getBankName());
		BeanUtils.copyProperties(dynaForm,cheque);
		//dynaForm.set("chequeNumbers",cheque);
		Log.log(Log.INFO,"IFAction","chequeDetailsUpdatePage","Exited");
		return mapping.findForward("success");
	}


	public ActionForward chequeDetailsUpdateSuccess(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdateSuccess","Entered");
		IFProcessor ifProcessor=new IFProcessor();
		ChequeDetails chequeDetails  = new ChequeDetails();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String chequeNumber = (String) dynaForm.get("number");
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		BeanUtils.populate(chequeDetails,dynaForm.getMap());
		String bank = (String) dynaForm.get("bankName");
		//System.out.println("bank:"+bank);
		int i = bank.indexOf(",");
		//System.out.println("i:"+i);
		String newBank =  bank.substring(0,i);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setBankName(newBank);

		int j = bank.indexOf("(");
		//System.out.println("j:"+j);
		String newBranch =  bank.substring(i+1,j);
		//System.out.println("newBranch:"+newBranch);
		chequeDetails.setBranchName(newBranch);

		chequeDetails.setUserId(user);
		ifProcessor.chequeDetailsUpdateSuccess(chequeDetails,chequeNumber);

		Log.log(Log.INFO,"IFAction","chequeDetailsUpdateSuccess","Exited");
		return mapping.findForward("success");
	}


	public ActionForward chequeDetailsReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsReport","Entered");
		ArrayList cheque = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument3");
		endDate = new java.sql.Date (eDate.getTime());
		cheque = ifProcessor.chequeDetailsUpdate(startDate,endDate);
		dynaForm.set("chequeArray",cheque);

		if(cheque == null || cheque.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","chequeDetailsReport","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward chequeDetailsReportPage(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsReportPage","Entered");
		ChequeDetails cheque = new ChequeDetails();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
//		String chequeNumber = (String) dynaForm.get("number");
		String chequeNumber = (String) request.getParameter("number");
		Log.log(Log.INFO,"IFAction","chequeDetailsReportPage","number *" + chequeNumber + "*");
		if (chequeNumber==null || chequeNumber.trim().equals(""))
		{
			chequeNumber=(String)dynaForm.get("chqId");
		}
		Log.log(Log.INFO,"IFAction","chequeDetailsReportPage","number #" + chequeNumber +"#");
		cheque = ifProcessor.chequeDetailsUpdatePageReport(chequeNumber);
		cheque.setChqId(chequeNumber);
//		dynaForm.set("chequeNumbers",cheque);
		BeanUtils.copyProperties(dynaForm, cheque);
		Log.log(Log.INFO,"IFAction","chequeDetailsReportPage","Exited");
		return mapping.findForward("success");
	}


/*
 * HVC
 *
 */


	 public ActionForward hvcInsertInput(
		 ActionMapping mapping,
		 ActionForm form,
		 HttpServletRequest request,
		 HttpServletResponse response)
		 throws Exception {

		 Log.log(Log.INFO,"IFAction","hvcInsertInput","Entered" );
		 ArrayList bankDetails =new ArrayList();
		 IFProcessor ifProcessor=new IFProcessor();
		 DynaActionForm dynaForm=(DynaActionForm)form;
		 dynaForm.initialize(mapping);
		 bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		 ArrayList bankNames=new ArrayList(bankDetails.size());
		 String bankName="";
		 for(int i=0;i<bankDetails.size();i++)
		 {
			 BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
			 bankName=bankAccountDetail.getBankName()
			 +" ,"+bankAccountDetail.getBankBranchName()+
							 "("+bankAccountDetail.getAccountNumber()+")";

			 bankNames.add(bankName);
		 }
		 dynaForm.set("banks",bankNames);
		 bankDetails = null;
		 ifProcessor = null;
		 Log.log(Log.INFO,"IFAction","hvcInsertInput","Exited");
		 return mapping.findForward("success");
	 }


	public ActionForward hvcInsertSuccess(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","hvcInsertSuccess","Entered");
		IFProcessor ifProcessor=new IFProcessor();
		ChequeDetails chequeDetails  = new ChequeDetails();
		DynaActionForm dynaForm = (DynaActionForm)form;
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		BeanUtils.populate(chequeDetails,dynaForm.getMap());
		String bank = (String) dynaForm.get("bankName");
		//System.out.println("bank:"+bank);
		String toBank = (String) dynaForm.get("toBankName");
		//System.out.println("toBank:"+toBank);

		int i = bank.indexOf(",");
		//System.out.println("i:"+i);
		String newBank =  bank.substring(0,i);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setBankName(newBank);

		int k = bank.indexOf("(");
		//System.out.println("k:"+k);
		String newBranch =  bank.substring(i+1,k);
		//System.out.println("newBranch:"+newBranch);
		chequeDetails.setBranchName(newBranch);

		int j = toBank.indexOf(",");
		//System.out.println("i:"+i);
		String toNewBank =  toBank.substring(0,j);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setToBankName(toNewBank);


		int l = toBank.indexOf("(");
		//System.out.println("l:"+l);
		String newToBranch =  toBank.substring(j+1,l);
		//System.out.println("newBranch:"+newToBranch);
		chequeDetails.setToBranchName(newToBranch);

		chequeDetails.setUserId(user);
		String remarks = chequeDetails.getChequeRemarks();
		//System.out.println("remarks:"+remarks);
		//chequeDetails.setAddedDate(new java.util.Date());
		ifProcessor.hvcInsertSuccess(chequeDetails);

		Log.log(Log.INFO,"IFAction","hvcInsertSuccess","Exited");
		return mapping.findForward("success");

		//System.out.println("bank:"+bank);
	}


	public ActionForward hvcUpdate(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","hvcUpdate","Entered");
		ArrayList cheque = new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());

		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument3");
		endDate = new java.sql.Date (eDate.getTime());
		cheque = ifProcessor.hvcUpdate(startDate,endDate);
		dynaForm.set("chequeArray",cheque);

		if(cheque == null || cheque.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","hvcUpdate","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward hvcUpdatePage(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","hvcUpdatePage","Entered");
		ChequeDetails cheque = new ChequeDetails();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String chequeNumber = (String) dynaForm.get("number");
		//System.out.println("chequeNumber:"+chequeNumber);
		ArrayList bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		ArrayList bankNames=new ArrayList(bankDetails.size());
		String bankName="";
		for(int i=0;i<bankDetails.size();i++)
		{
			BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
			bankName=bankAccountDetail.getBankName()
			+ " ,"+bankAccountDetail.getBankBranchName()+
			"("+bankAccountDetail.getAccountNumber()+")";

			bankNames.add(bankName);
		}
		dynaForm.set("banks",bankNames);
		//System.out.println("bank name :" + cheque.getBankName());
//		dynaForm.set("bankName",cheque.getBankName());
//		System.out.println(cheque.getBankName());
//		dynaForm.set("toBankName",cheque.getToBankName());
//		System.out.println(cheque.getToBankName());
//		System.out.println("manager invoked ");
		cheque = ifProcessor.hvcUpdatePage(chequeNumber);
		BeanUtils.copyProperties(dynaForm,cheque);


		//dynaForm.set("chequeNumbers",cheque);
		Log.log(Log.INFO,"IFAction","hvcUpdatePage","Exited");
		return mapping.findForward("success");
	}


	public ActionForward hvcUpdateSuccess(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","hvcUpdateSuccess","Entered");
		IFProcessor ifProcessor=new IFProcessor();
		ChequeDetails chequeDetails  = new ChequeDetails();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String chequeNumber = (String) dynaForm.get("number");
		User creatingUser=getUserInformation(request);
		String user=creatingUser.getUserId();
		BeanUtils.populate(chequeDetails,dynaForm.getMap());
		String bank = (String) dynaForm.get("bankName");
		//System.out.println("bank:"+bank);
		String toBank = (String) dynaForm.get("toBankName");
		//System.out.println("toBank:"+toBank);

		int i = bank.indexOf(",");
		//System.out.println("i:"+i);
		String newBank =  bank.substring(0,i);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setBankName(newBank);

		int k = bank.indexOf("(");
		//System.out.println("k:"+k);
		String newBranch =  bank.substring(i+1,k);
		//System.out.println("newBranch:"+newBranch);
		chequeDetails.setBranchName(newBranch);

		int j = toBank.indexOf(",");
		//System.out.println("i:"+i);
		String toNewBank =  toBank.substring(0,j);
		//System.out.println("newBank:"+newBank);
		chequeDetails.setToBankName(toNewBank);

		int l = toBank.indexOf("(");
		//System.out.println("l:"+l);
		String newToBranch =  toBank.substring(j+1,l);
		//System.out.println("newBranch:"+newToBranch);
		chequeDetails.setToBranchName(newToBranch);

		chequeDetails.setUserId(user);
		ifProcessor.hvcUpdateSuccess(chequeDetails,chequeNumber);

		Log.log(Log.INFO,"IFAction","hvcUpdateSuccess","Exited");
		return mapping.findForward("success");
	}


	public ActionForward showInflow(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInflow","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;

		dynaForm.initialize(mapping);

		dynaForm.set("inflowOrOutflow","INFLOW");
		Log.log(Log.INFO,"IFAction","showInflow","Exited");
		return mapping.findForward("budgetDetails");
	}

	public ActionForward showInflowForecastDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInflowForecastDetails","Entered");

		InvestmentForm ifForm=(InvestmentForm)form;
		ifForm.setInflowOrOutflow(InvestmentFundConstants.INFLOW);
		getInvestmentForm(ifForm, InvestmentFundConstants.INFLOW);

		Log.log(Log.INFO,"IFAction","showInflowForecastDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showOutflowForecastDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		Log.log(Log.INFO,"IFAction","showOutflowForecastDetails","Entered");

		InvestmentForm ifForm=(InvestmentForm)form;
		ifForm.setInflowOrOutflow(InvestmentFundConstants.OUTFLOW);
		getInvestmentForm(ifForm, InvestmentFundConstants.OUTFLOW);

		Log.log(Log.INFO,"IFAction","showOutflowForecastDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showAnnualFundsInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showAnnualFundsInflowDetails","Entered");

		InvestmentForm ifForm=(InvestmentForm)form;

		ifForm.setInflowOrOutflow(InvestmentFundConstants.INFLOW);
		ifForm.setAnnualOrShortTerm(InvestmentFundConstants.ANNUAL);

		ifForm.getHeadsToRender().clear();
		ifForm.getSubHeadsToRender().clear();
		ifForm.setDateOfFlow(null);
		getInvestmentForm(ifForm, InvestmentFundConstants.INFLOW);

		ifForm.getHeads().clear();
		ifForm.getSubHeads().clear();

		Log.log(Log.INFO,"IFAction","showAnnualFundsInflowDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showShortTermFundsInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showShortTermFundsInflowDetails","Entered");

		InvestmentForm ifForm=(InvestmentForm)form;
		ifForm.setInflowOrOutflow(InvestmentFundConstants.INFLOW);
		ifForm.setAnnualOrShortTerm(InvestmentFundConstants.SHORT_TERM);
		getInvestmentForm(ifForm, InvestmentFundConstants.INFLOW);

		Log.log(Log.INFO,"IFAction","showShortTermFundsInflowDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showAnnualFundsOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showAnnualFundsOutflowDetails","Entered");

		InvestmentForm ifForm=(InvestmentForm)form;

		ifForm.setInflowOrOutflow(InvestmentFundConstants.OUTFLOW);

		ifForm.setAnnualOrShortTerm(InvestmentFundConstants.ANNUAL);

		ifForm.getHeadsToRender().clear();
		ifForm.getSubHeadsToRender().clear();
		ifForm.setDateOfFlow(null);
		getInvestmentForm(ifForm, InvestmentFundConstants.OUTFLOW);

		ifForm.getHeads().clear();
		ifForm.getSubHeads().clear();

		Log.log(Log.INFO,"IFAction","showAnnualFundsOutflowDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showShortTermFundsOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showShortTermFundsOutflowDetails","Entered");
		InvestmentForm ifForm=(InvestmentForm)form;
		ifForm.setInflowOrOutflow(InvestmentFundConstants.OUTFLOW);
		ifForm.setAnnualOrShortTerm(InvestmentFundConstants.SHORT_TERM);
		getInvestmentForm(ifForm, InvestmentFundConstants.OUTFLOW);

		Log.log(Log.INFO,"IFAction","showShortTermFundsOutflowDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		Log.log(Log.INFO,"IFAction","showBudgetOutflowDetails","Entered");
		String forwardPage="";
		String budget="";
		InvestmentForm ifForm=(InvestmentForm)form;
		budget=ifForm.getAnnualOrShortTerm();

		ifForm.resetWhenRequired(mapping,request);
		ifForm.setAnnualOrShortTerm(budget);

		ifForm.setInflowOrOutflow(InvestmentFundConstants.OUTFLOW);
		getInvestmentForm(ifForm, InvestmentFundConstants.OUTFLOW);

		if (budget.equals("Annual"))
		{
			forwardPage="annualOutflowBudget";
		}
		else if (budget.equals("ShortTerm"))
		{
			forwardPage="shortTermOutflowBudget";
		}
		else
		{
			forwardPage="notSelected";
		}
		Log.log(Log.INFO,"IFAction","showBudgetOutflowDetails","Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward showBudgetInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
//		System.out.println("Entered show Budget Inflow details ");

		Log.log(Log.INFO,"IFAction","showBudgetInflowDetails","Entered");
		String forwardPage="";
		String budget="";

		InvestmentForm ifForm=(InvestmentForm)form;

		budget=ifForm.getAnnualOrShortTerm();
		ifForm.resetWhenRequired(mapping,request);
		ifForm.setAnnualOrShortTerm(budget);

		Log.log(Log.DEBUG,"IFAction","showBudgetInflowDetails","Budget Type "+ifForm.getAnnualOrShortTerm());

		ifForm.setInflowOrOutflow(InvestmentFundConstants.INFLOW);
		getInvestmentForm(ifForm, InvestmentFundConstants.INFLOW);

		//System.out.println("Size of head "+ifForm.getHeadsToRender().size());
		if (budget.equals("Annual"))
		{
			forwardPage="annualInflowBudget";
		}
		else if (budget.equals("ShortTerm"))
		{
			forwardPage="shortTermInflowBudget";
		}
		else
		{
			forwardPage="notSelected";
		}
		Log.log(Log.INFO,"IFAction","showBudgetInflowDetails","Exited");
		return mapping.findForward(forwardPage);
	}

	public ActionForward updateBudgetHeadMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Entered");
		String budgetHeadType = "";
		String budgetInOutFlag = "";

		DynaActionForm dynaForm=(DynaActionForm)form;
		BudgetHead budgetHead=new BudgetHead();
		BeanUtils.populate(budgetHead,dynaForm.getMap());

		IFProcessor ifProcessor=new IFProcessor();


		//Get the logged in user's userId.
	    User loggedInUser=getUserInformation(request);
	    String loggedUserId=loggedInUser.getUserId();

		String newBudgetHead = budgetHead.getNewBudgetHead();
		Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Entered " +newBudgetHead);
		while(true)
		{
			if (budgetHead.getBudgetHeadType().equalsIgnoreCase(InvestmentFundConstants.BOTH))
			{
				Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Entered Both new");
				ArrayList budgetHeads=ifProcessor.getBudgetHeadTypes(InvestmentFundConstants.INFLOW);
				for (int i=0;i<budgetHeads.size();i++)
				{
					if (((String)budgetHeads.get(i)).equalsIgnoreCase(newBudgetHead))
					{
						throw new MessageException("Budget Head Already Exists.");
					}
				}

				budgetHeads=ifProcessor.getBudgetHeadTypes(InvestmentFundConstants.OUTFLOW);
				for (int i=0;i<budgetHeads.size();i++)
				{
					if (((String)budgetHeads.get(i)).equalsIgnoreCase(budgetHead.getNewBudgetHead()))
					{
						throw new MessageException("Budget Head Already Exists.");
					}
				}
			}

			int indexOfLastAnd = newBudgetHead.lastIndexOf(".".trim());
			if(indexOfLastAnd != -1)
			{
				String firstPartOfBudgetHead = newBudgetHead.substring(0,indexOfLastAnd);
				String secPartOfBudgetHead = newBudgetHead.substring(indexOfLastAnd+1,newBudgetHead.length());
				newBudgetHead = firstPartOfBudgetHead + secPartOfBudgetHead;
			}
			if(indexOfLastAnd == -1)
			{
				break;
			}
		}
		String modBudgetHead = budgetHead.getModBudgetHead();
		Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Entered " +modBudgetHead);
		while(true)
		{
			if (budgetHead.getBudgetHeadType().equalsIgnoreCase(InvestmentFundConstants.BOTH))
			{

				Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Entered Both mod");
				ArrayList budgetHeads=ifProcessor.getBudgetHeadTypes(InvestmentFundConstants.INFLOW);
				for (int i=0;i<budgetHeads.size();i++)
				{
					if (((String)budgetHeads.get(i)).equalsIgnoreCase(modBudgetHead))
					{
						throw new MessageException("Budget Head Already Exists.");
					}
				}

				budgetHeads=ifProcessor.getBudgetHeadTypes(InvestmentFundConstants.OUTFLOW);
				for (int i=0;i<budgetHeads.size();i++)
				{
					if (((String)budgetHeads.get(i)).equalsIgnoreCase(modBudgetHead))
					{
						throw new MessageException("Budget Head Already Exists.");
					}
				}
			}

			int indexOfLastAnd = modBudgetHead.lastIndexOf(".".trim());
			if(indexOfLastAnd != -1)
			{
				String firstPartOfBudgetHead = modBudgetHead.substring(0,indexOfLastAnd);
				String secPartOfBudgetHead = modBudgetHead.substring(indexOfLastAnd+1,modBudgetHead.length());
				modBudgetHead = firstPartOfBudgetHead + secPartOfBudgetHead;
			}
			if(indexOfLastAnd == -1)
			{
				break;
			}
		}
		budgetHead.setNewBudgetHead(newBudgetHead);
		budgetHead.setModBudgetHead(modBudgetHead);

		ifProcessor.updateBudgetHeadsMaster(budgetHead,loggedUserId);
		Log.log(Log.INFO,"IFAction","updateBudgetHeadMaster","Exited");

		String message="Budget Head saved successfully";

		request.setAttribute("message",message);

		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetSubHeadMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		getBudgetHeadTypes(dynaForm);

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateBudgetSubHeadMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateBudgetSubHeadMaster","Entered");

		String budgetHeadType= "";
		String budgetSubHeadTitle= "";
		Hashtable budgetSubHeadDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;

		BudgetSubHead subHead=new BudgetSubHead();

		BeanUtils.populate(subHead,dynaForm.getMap());

		Log.log(Log.INFO,"IFAction","updateBudgetSubHeadMaster","Head "+subHead.getBudgetHead());
		Log.log(Log.INFO,"IFAction","updateBudgetSubHeadMaster","Sub-Head  "+subHead.getBudgetSubHeadTitle());
		Log.log(Log.INFO,"IFAction","updateBudgetSubHeadMaster"," Head Type "+subHead.getBudgetSubHeadType());

		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		IFProcessor ifProcessor=new IFProcessor();
		String newBudgetSubHeadTitle = subHead.getNewBudgetSubHeadTitle();
		while(true)
		{
			int indexOfLastAnd = newBudgetSubHeadTitle.lastIndexOf(".".trim());
			if(indexOfLastAnd != -1)
			{
				String firstPartOfBudgetSubHead = newBudgetSubHeadTitle.substring(0,indexOfLastAnd);
				String secPartOfBudgetSubHead = newBudgetSubHeadTitle.substring(indexOfLastAnd+1,newBudgetSubHeadTitle.length());
				newBudgetSubHeadTitle = firstPartOfBudgetSubHead + secPartOfBudgetSubHead;
			}
			if(indexOfLastAnd == -1)
			{
				break;
			}
		}
		subHead.setNewBudgetSubHeadTitle(newBudgetSubHeadTitle);

		String modBudgetSubHeadTitle = subHead.getModBudgetSubHeadTitle();
		while(true)
		{
			int indexOfLastAnd = modBudgetSubHeadTitle.lastIndexOf(".".trim());
			if(indexOfLastAnd != -1)
			{
				String firstPartOfBudgetSubHead = modBudgetSubHeadTitle.substring(0,indexOfLastAnd);
				String secPartOfBudgetSubHead = modBudgetSubHeadTitle.substring(indexOfLastAnd+1,modBudgetSubHeadTitle.length());
				modBudgetSubHeadTitle = firstPartOfBudgetSubHead + secPartOfBudgetSubHead;
			}
			if(indexOfLastAnd == -1)
			{
				break;
			}
		}
		subHead.setModBudgetSubHeadTitle(modBudgetSubHeadTitle);

		ifProcessor.updateBudgetSubHeadMaster(subHead,loggedUserId);

		String message="Budget Sub Head saved successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateBudgetSubHeadMaster","Exited");

		return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward updateHolidayMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateHolidayMaster","Entered");

		String holidayDate = "";
		String holidayDesc= "";
		Hashtable holidayDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		holidayDate	=(String)dynaForm.get("holidayDate");
		holidayDesc	=(String)dynaForm.get("holidayDescription");
		String newHolDate =(String)dynaForm.get("newHolidayDate");
		String modHolDate =(String)dynaForm.get("modHolidayDate");

		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		holidayDetails.put("Updated By", loggedUserId);
		IFProcessor ifProcessor=new IFProcessor();
		if (holidayDate.equals(""))
		{
			holidayDetails.put("New Holiday Date", newHolDate);
			holidayDetails.put("Holiday Description", holidayDesc);
			ifProcessor.insertHolidayMaster(holidayDetails);
		}
		else
		{
			holidayDetails.put("Holiday Date", holidayDate);
			holidayDetails.put("Mod Holiday Date", modHolDate);
			holidayDetails.put("Holiday Description", holidayDesc);
			ifProcessor.updateHolidayMaster(holidayDetails);
		}

		String message="Holiday details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateHolidayMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateCorpusMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateCorpusMaster","Entered");

		String corpusContributor = "";
		Double corpusAmount;
		Date corpusDate;
		Hashtable corpusDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		String corpusId = (String)dynaForm.get("corpusId");
		corpusContributor	=(String)dynaForm.get("corpusContributor");
		corpusAmount=(Double)dynaForm.get("corpusAmount");
		corpusDate	=(Date)dynaForm.get("corpusDate");
		corpusDetails.put("Corpus Contributor", corpusContributor);
		corpusDetails.put("Corpus Contribution", corpusAmount);
		corpusDetails.put("Corpus Date", corpusDate);
		corpusDetails.put("Corpus Id", corpusId);

		User user=getUserInformation(request);

		corpusDetails.put("Updated By", user.getUserId());
		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.updateCorpusMaster(corpusDetails);

		String message="Corpus details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateCorpusMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstrumentMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstrumentMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		//getAllRatings(dynaForm);
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments=ifProcessor.getInstrumentTypes("G");

		dynaForm.set("instrumentNames", instruments);

		Log.log(Log.INFO,"IFAction","showInstrumentMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateInstrumentMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateInstrumentMaster","Entered");

		/*
		String instrumentRating ="";
		String instrumentPeriod ="";
		String instrumentDescription ="";
		String instrumentType ="";
		Hashtable instrumentDetails = new Hashtable();

		DynaActionForm dynaForm=(DynaActionForm)form;
		instrumentType	=(String)dynaForm.get("instrumentName");
		instrumentDescription =(String)dynaForm.get("instrumentDescription");
		//instrumentPeriod =(String)dynaForm.get("instrumentPeriod");
		//instrumentRating =(String)dynaForm.get("instrumentRating");
		instrumentDetails.put("Instrument Type", instrumentType);
		instrumentDetails.put("Instrument Description", instrumentDescription);
		instrumentDetails.put("Instrument Period", instrumentPeriod);
		instrumentDetails.put("Instrument Rating", instrumentRating);
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		instrumentDetails.put("Updated By", loggedUserId);

		*/
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		DynaActionForm dynaForm=(DynaActionForm)form;
		InstrumentDetail instrumentDetail=new InstrumentDetail();

		BeanUtils.populate(instrumentDetail,dynaForm.getMap());

		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.updateInstrumentMaster(instrumentDetail,loggedUserId);

		String message="Instrument details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateInstrumentMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateInstrumentFeatureMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateInstrumentFeatureMaster","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;

		InstrumentFeature instrumentFeature=new InstrumentFeature();

		BeanUtils.populate(instrumentFeature,dynaForm.getMap());
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.updateInstrumentFeature(instrumentFeature,loggedUserId);

		String message="Instrument Feature details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateInstrumentFeatureMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstrumentSchemeMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstrumentSchemeMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		getInstrumentTypes(dynaForm);

		Log.log(Log.INFO,"IFAction","showInstrumentSchemeMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateInstrumentSchemeMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateInstrumentSchemeMaster","Entered");

		String instrument= "";
		String instrumentSchemeType= "";
		String instrumentSchemeDescription= "";
		Hashtable instrumentSchemeDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		instrument	=(String)dynaForm.get("instrument");
		instrumentSchemeType	=(String)dynaForm.get("instrumentSchemeType");
		instrumentSchemeDescription	=(String)dynaForm.get("instrumentSchemeDescription");
		String newInstScheme = (String)dynaForm.get("newInstrumentSchemeType");
		String modInstScheme = (String)dynaForm.get("modInstrumentSchemeType");
		instrumentSchemeDetails .put("Instrument", instrument);
		instrumentSchemeDetails .put("Instrument Scheme Type", instrumentSchemeType);
		instrumentSchemeDetails .put("New Instrument Scheme Type", newInstScheme);
		instrumentSchemeDetails .put("Mod Instrument Scheme Type", modInstScheme);
		instrumentSchemeDetails .put("Instrument Scheme Description", instrumentSchemeDescription);
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		instrumentSchemeDetails .put("Updated By", loggedUserId);
		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.updateInstrumentScheme(instrumentSchemeDetails );

		String message="Instrument Scheme details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateInstrumentSchemeMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInvesteeMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvesteeMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		getAllInvesteeGroups(dynaForm);

		Log.log(Log.INFO,"IFAction","showInvesteeMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateInvesteeMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateInvesteeMaster","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;
		InvesteeDetail investeeDetail=new InvesteeDetail();

		BeanUtils.populate(investeeDetail,dynaForm.getMap());
		investeeDetail.setInvestee((String)dynaForm.get("investee1"));
		Log.log(Log.DEBUG,"IFAction","updateInvesteeMaster","net worth "+investeeDetail.getInvesteeNetWorth());
		Log.log(Log.DEBUG,"IFAction","updateInvesteeMaster","form...net worth "+dynaForm.get("investeeNetWorth"));

		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		//investeeDetails.put("Updated By",  loggedUserId);
		//investeeDetails.put("Updated By", "CGTSI001");//Hardcoded By Ramvel
		IFProcessor ifProcessor=new IFProcessor();
		Log.log(Log.DEBUG,"IFAction","updateInvesteeMaster","investee "+investeeDetail.getInvestee());
		Log.log(Log.DEBUG,"IFAction","updateInvesteeMaster","mod investee "+investeeDetail.getModInvestee());
		Log.log(Log.DEBUG,"IFAction","updateInvesteeMaster","new investee "+investeeDetail.getNewInvestee());
		ifProcessor.updateInvesteeMaster(investeeDetail,loggedUserId);

		String message="Investee details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateInvesteeMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateMaturityMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateMaturityMaster","Entered");

		String maturityType = "";
		String maturityDescription = "";
		DynaActionForm dynaForm=(DynaActionForm)form;

		MaturityDetail matDetail = new MaturityDetail();
		BeanUtils.populate(matDetail,dynaForm.getMap());

		Log.log(Log.INFO,"IFAction","updateMaturityMaster","mat type " + matDetail.getMaturityType());
		Log.log(Log.INFO,"IFAction","updateMaturityMaster","mod mat type " + matDetail.getModMaturityType());
		Log.log(Log.INFO,"IFAction","updateMaturityMaster","new mat type " + matDetail.getNewMaturityType());
		Log.log(Log.INFO,"IFAction","updateMaturityMaster","mat desc " + matDetail.getMaturityDescription());

		IFProcessor ifProcessor=new IFProcessor();
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		ifProcessor.updateMaturityMaster(matDetail, loggedUserId);

		String message="Maturity details saved Successfully";

		request.setAttribute("message",message);


		Log.log(Log.INFO,"IFAction","updateMaturityMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateRatingMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateRatingMaster","Entered");
		String rating="";
		String ratingDescription= "";
		String ratingGivenBy= "";

		Hashtable ratingDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		rating	=(String)dynaForm.get("rating");
		String newRating = (String)dynaForm.get("newRating");
		String modRating = (String)dynaForm.get("modRating");
		ratingDescription	=(String)dynaForm.get("ratingDescription");
		//ratingGivenBy	=(String)dynaForm.get("ratingGivenBy");
		ratingDetails.put("Rating", rating);
		ratingDetails.put("New Rating", newRating);
		ratingDetails.put("Mod Rating", modRating);
		ratingDetails.put("Rating Description", ratingDescription);
		//ratingDetails.put("Rating Given By", ratingGivenBy);
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		ratingDetails.put("Updated By", loggedUserId);

		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.updateRatingMaster(ratingDetails);

		String message="Rating details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateRatingMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showPeriodicProjection(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showPeriodicProjection","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			Log.log(Log.INFO,"IFAction","showPeriodicProjection","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getPeriodicProjectionDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","getPeriodicProjectionDetails","Entered");


			DynaActionForm dynaForm=(DynaActionForm)form;

			String startDate=(String)dynaForm.get("startDate");

			String endDate=(String)dynaForm.get("endDate");


			SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

			Date stDate=format.parse(startDate,new ParsePosition(0));

			Date edDate=format.parse(endDate,new ParsePosition(0));

			IFProcessor processor=new IFProcessor();

			ArrayList projections=processor.getClaimProjection(stDate,edDate);

			if(projections==null|| projections.size()==0)
			{
				throw new DatabaseException("No Data Available For Periodic Projection.");
			}
			if(projections!=null)
			{
				Log.log(Log.DEBUG,"IFAction","getPeriodicProjectionDetails","projections "+projections.size());
			}


			dynaForm.set("projections",projections);

			HttpSession session = request.getSession(false);
			session.setAttribute("projectionFlag", "1");

			Log.log(Log.INFO,"IFAction","getPeriodicProjectionDetails","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getCumulativeProjectionDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			DynaActionForm dynaForm=(DynaActionForm)form;

			String endDate=(String)dynaForm.get("endDate");

			SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

			Date edDate=format.parse(endDate,new ParsePosition(0));

			IFProcessor processor=new IFProcessor();

			ArrayList projections=processor.getClaimProjection(null,edDate);

			if(projections==null|| projections.size()==0)
			{
				throw new DatabaseException("No Data Available for Cumulative Projection.");
			}

			if(projections!=null)
			{
				Log.log(Log.DEBUG,"IFAction","getPeriodicProjectionDetails","projections "+projections.size());
			}
			dynaForm.set("projections",projections);

			HttpSession session = request.getSession(false);
			session.setAttribute("projectionFlag", "1");

			return mapping.findForward(Constants.SUCCESS);
		}


	public ActionForward updateCumulativeProjectionDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateCumulativeProjectionDetails","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;

		ArrayList projections=(ArrayList)dynaForm.get("projections");

		Log.log(Log.DEBUG,"IFAction","updateCumulativeProjectionDetails","projections "+projections.size());

		double totalAmount=0;
		for(int i=0;i<projections.size();i++)
		{
			ProjectExpectedClaimDetail projection=(ProjectExpectedClaimDetail)projections.get(i);

			totalAmount+=projection.getProjectedClaimAmount();
		}

		Log.log(Log.DEBUG,"IFAction","updateCumulativeProjectionDetails","totalAmount "+totalAmount);

		String startDate=(String)dynaForm.get("startDate");

		String endDate=(String)dynaForm.get("endDate");

		Log.log(Log.DEBUG,"IFAction","updateCumulativeProjectionDetails","startDate,endDate "+startDate+","+endDate);

		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

		Date stDate=format.parse(startDate,new ParsePosition(0));

		Date edDate=format.parse(endDate,new ParsePosition(0));

		ProjectExpectedClaimDetail projectExpectedClaimDetail = new ProjectExpectedClaimDetail();
		projectExpectedClaimDetail.setStartDate(stDate);
		projectExpectedClaimDetail.setEndDate(edDate);
		projectExpectedClaimDetail.setProjectedClaimAmount(totalAmount);


		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		IFProcessor ifProcessor=new IFProcessor();

		ifProcessor.saveProjectExpectedClaimDetail(projectExpectedClaimDetail,loggedUserId);

		String message="Cumulative Projection details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateCumulativeProjectionDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updatePeriodicProjectionDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updatePeriodicProjectionDetails","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;

		ArrayList projections=(ArrayList)dynaForm.get("projections");

		Log.log(Log.DEBUG,"IFAction","updatePeriodicProjectionDetails","projections "+projections.size());

		double totalAmount=0;
		for(int i=0;i<projections.size();i++)
		{
			ProjectExpectedClaimDetail projection=(ProjectExpectedClaimDetail)projections.get(i);

			totalAmount+=projection.getProjectedClaimAmount();
		}

		Log.log(Log.DEBUG,"IFAction","updatePeriodicProjectionDetails","totalAmount "+totalAmount);

		String startDate=(String)dynaForm.get("startDate");

		String endDate=(String)dynaForm.get("endDate");

		Log.log(Log.DEBUG,"IFAction","updatePeriodicProjectionDetails","startDate,endDate "+startDate+","+endDate);

		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

		Date stDate=format.parse(startDate,new ParsePosition(0));

		Date edDate=format.parse(endDate,new ParsePosition(0));

		ProjectExpectedClaimDetail projectExpectedClaimDetail = new ProjectExpectedClaimDetail();
		projectExpectedClaimDetail.setStartDate(stDate);
		projectExpectedClaimDetail.setEndDate(edDate);
		projectExpectedClaimDetail.setProjectedClaimAmount(totalAmount);
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		//ratingDetails.put("Updated By", loggedUserId);

		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.saveProjectExpectedClaimDetail(projectExpectedClaimDetail,loggedUserId);

		String message = "";

		HttpSession session = (HttpSession)request.getSession(false);

        if((((String)session.getAttribute("mainMenu")).equals(MenuOptions_back.getMenu(MenuOptions_back.IF_PROJECT_EXPECTED_CLAIMS))) && ((session.getAttribute("subMenuItem")).equals(MenuOptions_back.getMenu(MenuOptions_back.IF_PROJECT_EXPECTED_CLAIMS_PERIODIC))))
        {
			message="Periodic Projection details saved Successfully";
		}
		if((((String)session.getAttribute("mainMenu")).equals(MenuOptions_back.getMenu(MenuOptions_back.IF_PROJECT_EXPECTED_CLAIMS))) && ((session.getAttribute("subMenuItem")).equals(MenuOptions_back.getMenu(MenuOptions_back.IF_PROJECT_EXPECTED_CLAIMS_CUMULATIVE))))
		{
			message="Cumulative Projection details saved Successfully";
		}

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updatePeriodicProjectionDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward showTDSDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showTDSDetails","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		HttpSession session = request.getSession(false);
		session.setAttribute("flag", "I");
		getAllInvestees(dynaForm);
		getInstrumentTypes(dynaForm);
		getReceiptNumbers(dynaForm);

		Log.log(Log.INFO,"IFAction","showTDSDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward updateTDSDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateTDSDetails","Entered");
		Hashtable ratingDetails = new Hashtable();

		DynaActionForm dynaForm=(DynaActionForm)form;
		TDSDetail tdsDetail= new TDSDetail();
		String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		BeanUtils.populate(tdsDetail,dynaForm.getMap());
		tdsDetail.setModifiedBy(loggedUserId);

		Log.log(Log.DEBUG,"IFAction","updateTDSDetails","getInvestmentRefNumber "+tdsDetail.getInvestmentRefNumber());
		Log.log(Log.DEBUG,"IFAction","updateTDSDetails","getTDSAmount "+tdsDetail.getTDSAmount());
		Log.log(Log.DEBUG,"IFAction","updateTDSDetails","getReminderDate "+tdsDetail.getReminderDate());
		Log.log(Log.DEBUG,"IFAction","updateTDSDetails","getTDSCertificateReceivedORNot "+tdsDetail.getTDSCertificateReceivedORNot());
		Log.log(Log.DEBUG,"IFAction","updateTDSDetails","getTDSDeductedDate "+tdsDetail.getTDSDeductedDate());

		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.saveTDSDetail(tdsDetail);

		String message="TDS details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","updateTDSDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showPlanInvestmentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showPlanInvestmentDetails","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			IFProcessor ifProcessor=new IFProcessor();

			PlanInvestment planInvestment=ifProcessor.getPlanInvestmentDetails();

			BeanUtils.copyProperties(dynaForm,planInvestment);

			Log.log(Log.INFO,"IFAction","showPlanInvestmentDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showPlanInvestmentBuyOrSell(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showPlanInvestmentBuyOrSell","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;
		getAllInvestees(dynaForm);
		getInstrumentTypes(dynaForm);
		Log.log(Log.INFO,"IFAction","showPlanInvestmentBuyOrSell","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updatePlanInvestmentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updatePlanInvestmentDetails","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;

		String flag=(String)dynaForm.get("isBuyOrSellRequest");

		String forwardFlag="";
		if(flag.equals(Constants.YES))
		{
			forwardFlag="showBuySellRequest";
		}
		else
		{
			String message="No Buy/Sell request is made.";
			request.setAttribute("message",message);
			forwardFlag="success";
		}
		Log.log(Log.INFO,"IFAction","updatePlanInvestmentDetails","Exited");
		return mapping.findForward(forwardFlag);
	}


	public ActionForward updatePlanInvestmentBuyOrSell(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updatePlanInvestmentBuyOrSell","Entered");
		Hashtable ratingDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		InvestmentPlanningDetail investmentPlanningDetail= new InvestmentPlanningDetail();
			//String modifiedByUser	= null;
			//Get the logged in user's userId.
			User loggedInUser=getUserInformation(request);
			String loggedUserId=loggedInUser.getUserId();
			//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
			BeanUtils.populate(investmentPlanningDetail,dynaForm.getMap());
			investmentPlanningDetail.setModifiedBy(loggedUserId);
			//investmentPlanningDetail.setModifiedBy(modifiedByUser);

			Log.log(Log.DEBUG,"IFAction","updatePlanInvestmentBuyOrSell","getInvesteeName "+investmentPlanningDetail.getInvesteeName());
			Log.log(Log.DEBUG,"IFAction","updatePlanInvestmentBuyOrSell","getInstrumentName "+investmentPlanningDetail.getInstrumentName());
			Log.log(Log.DEBUG,"IFAction","updatePlanInvestmentBuyOrSell","getNoOfUnits "+investmentPlanningDetail.getNoOfUnits());
			Log.log(Log.DEBUG,"IFAction","updatePlanInvestmentBuyOrSell","getIsBuyOrSellRequest "+investmentPlanningDetail.getIsBuyOrSellRequest());

			IFProcessor ifProcessor=new IFProcessor();
			ifProcessor.saveInvestmentPlanningDetail(investmentPlanningDetail);

			String message="Investment Planning details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updatePlanInvestmentBuyOrSell","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward showMakeRequestBuyOrSell(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showMakeRequestBuyOrSell","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("flag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;

			//reset to default values.
			dynaForm.initialize(mapping);

			//get the required details.

			getAllInvestees(dynaForm);
			getInstrumentTypes(dynaForm);
//			getReceiptNumbers(dynaForm);
			Log.log(Log.INFO,"IFAction","showMakeRequestBuyOrSell","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateMakeRequestBuyOrSell(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateMakeRequestBuyOrSell","Entered");
		Hashtable ratingDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		BuySellDetail buySellDetail= new BuySellDetail();

			//Get the logged in user's userId.
			User loggedInUser=getUserInformation(request);
			String loggedUserId=loggedInUser.getUserId();

			BeanUtils.populate(buySellDetail,dynaForm.getMap());
			buySellDetail.setModifiedBy(loggedUserId);

			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getInvesteeName "+buySellDetail.getInvesteeName());
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getInstrumentName "+buySellDetail.getInstrumentName());
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getNoOfUnits "+buySellDetail.getNoOfUnits());
			if (buySellDetail.getInstrumentName().equalsIgnoreCase("FIXED DEPOSIT"))
			{
				buySellDetail.setNoOfUnits("1");
			}
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getNoOfUnits "+buySellDetail.getNoOfUnits());
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getWorthOfUnits "+buySellDetail.getWorthOfUnits());
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getInvestmentRefNumber "+buySellDetail.getInvestmentRefNumber());
			Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","getIsBuyOrSellRequest "+buySellDetail.getIsBuyOrSellRequest());
			
			
			IFDAO ifDAO = new IFDAO();
			IFProcessor ifProcessor= new IFProcessor();
			if (buySellDetail.getIsBuyOrSellRequest().equalsIgnoreCase("B"))
			{
				String investeeName = buySellDetail.getInvesteeName();
				double corpusAmt = ifDAO.getCorpusAmount();
				Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","corpus amount "+corpusAmt);
				Date date = new Date();
				ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
				
				double ceilingAmt = 0;
				boolean ceilingAvail=false;
				
				for (int i=0; i<investeeWiseExpDetails.size(); i++)
				{
					ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
					String invName = exposureDetailsTemp.getInvesteeName();
					Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","inv name "+ invName);
					if (invName.equalsIgnoreCase(investeeName))
					{
						ceilingAvail=true;
						ceilingAmt=exposureDetailsTemp.getGapAvailableAmount();
						Log.log(Log.DEBUG,"IFAction","updateMakeRequestBuyOrSell","ceiling amount "+ceilingAmt);
						break;
					}
				}
				
				if (! ceilingAvail)
				{
					throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
				}
				else if (ceilingAmt==0 || Double.parseDouble(buySellDetail.getWorthOfUnits()) > ceilingAmt)
				{
					throw new MessageException("Invested Amount for " + investeeName + " has exceeded the Ceiling Limit");
				}
			}
						
			String buySellId=ifProcessor.saveBuyOrSellDetails(buySellDetail, request.getSession(false).getServletContext().getRealPath(""));

			String message="Investment Reference number is "+buySellId+". <br><b>Please note down the number for future reference</b>";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateMakeRequestBuyOrSell","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateMakeRquestFundTransfer(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateMakeRquestFundTransfer","Entered");
		Hashtable ratingDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		FundTransferDetail fundTransferDetail= new FundTransferDetail();
			IFProcessor ifProcessor=new IFProcessor();
			ifProcessor.saveFundTransferDetail(fundTransferDetail);
			Log.log(Log.INFO,"IFAction","updateMakeRquestFundTransfer","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showFixedDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showFixedDetails","Entered");

		HttpSession session = request.getSession(false);
		IFProcessor ifProcessor=new IFProcessor();
		session.setAttribute("invFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Fixed Deposit");
			
			getAllInvestees(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames); 

			Log.log(Log.INFO,"IFAction","showFixedDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
  
  /**
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return 
   * @throws java.lang.Exception
   */
  public ActionForward investmentEntry(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","investmentEntry","Entered");

		HttpSession session = request.getSession(false);
		IFProcessor ifProcessor=new IFProcessor();
		//session.setAttribute("invFlag", "0");
			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping); 
      ArrayList partyNames=(ArrayList)getInvestmentPartyList();
      dynaForm.set("bankNames",partyNames);  
      partyNames=null;		
			Log.log(Log.INFO,"IFAction","investmentEntry","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
  
   /**
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return 
   * @throws java.lang.Exception
   */
  	public ActionForward insertInvestmentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		 Log.log(Log.INFO,"IFAction","insertInvestmentDetails","Entered");
     DynaActionForm dynaForm = (DynaActionForm) form;
     User loggedInUser=getUserInformation(request);
		 String loggedUserId=loggedInUser.getUserId();	
     IFProcessor ifProcessor=new IFProcessor();
		 
     String depositDate = (String) dynaForm.get("depositDate");
     String bankName= (String)dynaForm.get("bankName");
     double depositAmt =((java.lang.Double)dynaForm.get("depositAmt")).doubleValue();
     String compoundingFrequency =(String)dynaForm.get("compoundingFrequency");
     int years=((java.lang.Integer)dynaForm.get("years")).intValue();
     int months=((java.lang.Integer)dynaForm.get("months")).intValue();
     int days=((java.lang.Integer)dynaForm.get("days")).intValue();
     double rateOfInterest =((java.lang.Double)dynaForm.get("rateOfInterest")).doubleValue();
     Date maturityDate =(java.util.Date) dynaForm.get("maturityDate");
     String maturityAmount= (java.lang.String)dynaForm.get("maturityAmount");
     String receiptNumber = (String)dynaForm.get("receiptNumber");
    System.out.println("Deposit Date:"+depositDate+" bankName:"+bankName+ "depositAmt :"+depositAmt+" compoundingFrequency:"+compoundingFrequency+ 
            " years:"+years+" Months:"+ months+" days:"+ days+" rateOfInterest:"+rateOfInterest+" maturityDate:"+maturityDate+
            " maturityAmount:"+maturityAmount+" receiptNumber:"+receiptNumber);
     ifProcessor.insertInvestmentDetails(depositDate,bankName,depositAmt,compoundingFrequency,years,months,days,rateOfInterest,maturityDate,
            maturityAmount,loggedUserId,receiptNumber);
        System.out.println("after insertInvestmentDetails");
     String message="Investment details saved Successfully";
		 request.setAttribute("message",message);  
		 return mapping.findForward(Constants.SUCCESS);
    
    }
    
    
    public ActionForward getMaturityDate(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
    
    	DynaActionForm dynaForm=(DynaActionForm)form;
		  HttpSession session1=request.getSession(false);
      System.out.println("getMaturityDate Entered");
      String depositDate=(String)dynaForm.get("depositDate");
      String tenureyear =(String)dynaForm.get("tenureyear");
      String tenuremonth =(String) dynaForm.get("tenuremonth");
      String tenureday =  (String) dynaForm.get("tenureday");
      System.out.println("depositDate:"+depositDate+"tenureyear:"+tenureyear+"tenuremonth:"+tenuremonth+"tenureday:"+tenureday);
      
      
	
      return mapping.findForward(Constants.SUCCESS);
    
    }
    
    
    
    
    
    /**
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return 
   * @throws java.lang.Exception
   */
     public ActionForward updateInvestmentDatailInput(ActionMapping mapping,ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception
 {
 
       DynaActionForm dynaForm=(DynaActionForm)form;
       dynaForm.initialize(mapping);
       User user=getUserInformation(request);
       String userId=user.getUserId();
       return mapping.findForward("success");
 }
 
 /**
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return 
   * @throws java.lang.Exception
   */
 public ActionForward displayInvestmentDetails(ActionMapping mapping,ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception
 {
    
    DynaActionForm dynaForm=(DynaActionForm)form;
    String investmentId = (String) dynaForm.get("investmentId");    
    //System.out.println("investmentId:"+investmentId);
    InvestmentForm ifForm = new InvestmentForm();
    InvestmentDetails investmentDetail =new InvestmentDetails();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DecimalFormat df = new DecimalFormat("#############.##");
    
    ArrayList inwardSummary = null;
    
    java.util.Date date       = new java.util.Date();
    Calendar calendar         = Calendar.getInstance();
    calendar.setTime(date);
    String systemDate = dateFormat.format(date);
    IFDAO ifDAO=new IFDAO();
     int i = 0;
    if(investmentId==null||investmentId.equals("investmentId")){
     throw new NoDataException("Please Enter Valid Investment Id");
    }else{   
   //   System.out.println("Input Inward Id:"+inwardId); 
   try{
      investmentDetail = ifDAO.getInvestmentDetails(investmentId);
      if(investmentDetail == null){
            throw new NoDataException("No data is avaiable for this id.");
      }else{
        System.out.println("hello");
      }
       System.out.println("0");
      dynaForm.set("investmentId",investmentId);
      //System.out.println("Deposit Date:"+investmentDetail.getDepositDate());
      System.out.println("1");
      dynaForm.set("depositDate",dateFormat.format(investmentDetail.getDepositDate())); 
       System.out.println("2");
      dynaForm.set("bankName",investmentDetail.getBankName());
       System.out.println("3");
      dynaForm.set("depositAmt",new Double(investmentDetail.getDepositAmt()));
       System.out.println("4");
      dynaForm.set("compoundingFrequency",investmentDetail.getCompoundingFrequency());
       System.out.println("5");
      dynaForm.set("years",new Integer(investmentDetail.getYears()));
      dynaForm.set("months",new Integer(investmentDetail.getMonths()));
      dynaForm.set("days",new Integer(investmentDetail.getDays()));
      dynaForm.set("rateOfInterest",new Double(investmentDetail.getRateOfInterest()));  
       System.out.println("6");
      dynaForm.set("maturityDt",dateFormat.format(investmentDetail.getMaturityDate())); 
       System.out.println("7");
      dynaForm.set("maturityAmount",df.format(investmentDetail.getMaturityAmount()));  
       System.out.println("8");
       dynaForm.set("receiptNumber", investmentDetail.getReceiptNumber());
       System.out.println("9");
               String fdStats = investmentDetail.getFdStatus();
               if(fdStats.equals("A"))
                   dynaForm.set("fdStatus", "A");
               else
               if(fdStats.equals("M"))
                   dynaForm.set("fdStatus", "M");
               else
                   dynaForm.set("fdStatus", "C");
      investmentDetail = null;
   }catch(Exception e){
        e.printStackTrace();
   }finally{
   
   }
      return mapping.findForward("displayInvestmentDetails");        
      
   }
     
 }
 
 /**
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return 
   * @throws java.lang.Exception
   */
 public ActionForward afterUpdateInvestmentDetails(ActionMapping mapping,ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception
                                          {
  
  
  DynaActionForm dynaForm=(DynaActionForm)form;
  IFProcessor ifProcessor=new IFProcessor();
  IFDAO ifDAO = new IFDAO();
  String investmentId = (String)dynaForm.get("investmentId");
  String  depositDate = (String)dynaForm.get("depositDate");
  String bankName=(String)dynaForm.get("bankName");
  double depositAmt = Double.parseDouble((String)dynaForm.get("depositAmount"));
  String compoundingFrequency=(String)dynaForm.get("compoundingFrequency");
  int years=((java.lang.Integer)dynaForm.get("years")).intValue();
  int months=((java.lang.Integer)dynaForm.get("months")).intValue();
  int days=((java.lang.Integer)dynaForm.get("days")).intValue();
  double rateOfInterest =((java.lang.Double)dynaForm.get("rateOfInterest")).doubleValue();
     
  String maturityDt = (String)dynaForm.get("maturityDt");
 double maturityAmount=Double.parseDouble((String)dynaForm.get("maturityAmount"));      
 //System.out.println("investmentId:"+investmentId+" depositDate:"+depositDate+" bankName:"+bankName+" depositAmt:"+depositAmt );
 //System.out.println("Freq:"+compoundingFrequency+" years:"+years+" months:"+months+" days:"+days);
 //System.out.println("rateOfInterest:"+rateOfInterest+" maturityDt:"+maturityDt +"maturityAmount:"+maturityAmount);
  String receiptNumber = (String)dynaForm.get("receiptNumber");
         String fdStatus = (String)dynaForm.get("fdStatus");
  User user=getUserInformation(request);
  String userId=user.getUserId();
  ifProcessor.afterUpdateInvestmentDetails(investmentId,depositDate,bankName,depositAmt,compoundingFrequency,years,months,days,
            rateOfInterest,maturityDt,maturityAmount,userId, receiptNumber, fdStatus);
  request.setAttribute("message","<b>Investment Details Modified Successfully");
  return mapping.findForward("success");
  
  }
  
  /**
   * 
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
  private Collection getInvestmentPartyList()throws DatabaseException
	{
  
		 ArrayList partyList=new ArrayList();   
     ResultSet resultSet       = null;
     String partyName = "";
     Connection connection     = DBConnection.getConnection();
     try
        { 
      CallableStatement callable=connection.prepareCall("{?=call PackGetIvestmentPartyList.FuncGetInvestmentPartyList(?,?)}");
    	callable.registerOutParameter(1,Types.INTEGER);
    	callable.registerOutParameter(2,Constants.CURSOR);
  		callable.registerOutParameter(3,Types.VARCHAR);
			callable.executeUpdate();

			int errorCode=callable.getInt(1);
			String error=callable.getString(3);
      resultSet = (ResultSet)callable.getObject(2) ;
      if(errorCode==Constants.FUNCTION_FAILURE)
			{
        callable.close();
			  callable=null;
				throw new DatabaseException(error);
			}
     
      while(resultSet.next())
		   {
         partyName =(String) resultSet.getString(1);
         partyList.add(partyName);         
       }
       resultSet.close();
			 resultSet=null;
	   }
		 catch(SQLException e)
		{
			Log.log(Log.ERROR,"IOAction","getInvestmentPartyList",e.getMessage());
			Log.logException(e);

			throw new DatabaseException("Failed to fetch Investmebt Party Names");
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		return partyList;

	}
  
  
  
  

	public ActionForward showRatingDetailsForFixedDeposit(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForFixedDeposit","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "3");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForFixedDeposit","Exited");

			return mapping.findForward("success");
		}


	private void getAllMaturities(DynaActionForm form) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		ArrayList maturities=ifProcessor.getAllMaturities();
		form.set("maturities",maturities);
	}
  
 
  
  
	public ActionForward updateFixedDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateFixedDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		FDDetail fdDetail= new FDDetail();
		BeanUtils.populate(fdDetail,dynaForm.getMap());

		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getInvesteeName "+fdDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getInstrumentName "+fdDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getInvestmentName "+fdDetail.getInvestmentName());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getPrincipalAmount "+fdDetail.getPrincipalAmount());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getCompoundingFrequency "+fdDetail.getCompoundingFrequency());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getInterestRate "+fdDetail.getInterestRate());

		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getTenure "+fdDetail.getTenure());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getTenureType "+fdDetail.getTenureType());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getReceiptNumber "+fdDetail.getReceiptNumber());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getDateOfDeposit "+fdDetail.getDateOfDeposit());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getMaturityName "+fdDetail.getMaturityName());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","getMaturityDate "+fdDetail.getMaturityDate());


		IFProcessor ifProcessor=new IFProcessor();
		ArrayList receiptNos = ifProcessor.getFDReceiptNumbers();
		if (receiptNos.contains(fdDetail.getReceiptNumber()))
		{
			throw new MessageException("FD Receipt Number is not unique.");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fdDetail.getMaturityDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			throw new MessageException("Maturity Date falls on Saturday. Please Change.");
		}*/
		if (dayOfWeek == Calendar.SUNDAY)
		{
			throw new MessageException("Maturity Date falls on Sunday. Please Change.");
		}

		ArrayList holidays = ifProcessor.getAllHolidays();

		if (holidays.contains(fdDetail.getMaturityDate()))
		{
			throw new MessageException("Maturity Date falls on a Holiday. Please change.");
		}
		
		IFDAO ifDAO = new IFDAO();
		double corpusAmt = ifDAO.getCorpusAmount();
		Date date = new Date();
		
		Map matDetails = new TreeMap();
		Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
		Map mainDetails = (Map)ioReportDetails.get("DT");
		Set mainSet = mainDetails.keySet();
		Iterator mainIterator = mainSet.iterator();
		InflowOutflowReport ioReport = new InflowOutflowReport(); 
		while (mainIterator.hasNext())
		{
			ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
			if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
			{
				break;
			}
		}
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
		
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","matuirty amount "+exposureDetails.getMaturedAmount());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","stmt balance "+ioReport.getStmtBalance());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","fund transfer amount "+ioReport.getFundTransferInflow());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","provisions "+ioReport.getProvisionFundsAmt());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","min balance "+ioReport.getMinBalance());
		Log.log(Log.DEBUG,"IFAction","updateFixedDetails","invested amount "+exposureDetails.getInvestedAmount());
		
		double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
								Double.parseDouble(ioReport.getFundTransferInflow())) - 
								Double.parseDouble(ioReport.getChqissuedAmt()) - 
								Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
								Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
		
		double invCeilingAmt = 0;
		double matCeilingAmt = 0;
		double instCeilingAmt = 0;
		double ceilingAmt = 0;
		boolean ceilingAvail = false;
		
		String investeeName = fdDetail.getInvesteeName();
		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
		for (int i=0; i<investeeWiseExpDetails.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
			String invName = exposureDetailsTemp.getInvesteeName();
			Log.log(Log.DEBUG,"IFAction","updateFixedDetails","inv name "+ invName);
			if (invName.equalsIgnoreCase(investeeName))
			{
				ceilingAvail=true;
				invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
		}
		
		ArrayList maturityCeilingArr = ifProcessor.getMaturityWiseDetails(date, date, surplusAmt);
		String maturityName = fdDetail.getMaturityName();
		ceilingAvail = false;
			
		for (int i=0; i<maturityCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)maturityCeilingArr.get(i);
			String matName = exposureDetailsTemp.getMaturityName();
			Log.log(Log.DEBUG,"IFAction","updateFixedDetails","mat name "+ matName);
			if (matName.equalsIgnoreCase(maturityName))
			{
				ceilingAvail=true;
				matCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Maturity " + maturityName + " not available.");
		}
		
		ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
		String instCatName = fdDetail.getInstrumentCategory();
		ceilingAvail = false;
		
		for (int i=0; i<instCatCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
			String instName = exposureDetailsTemp.getInstCatName();
			Log.log(Log.DEBUG,"IFAction","updateFixedDetails","inst cat name "+ instName);
			if (instName.equalsIgnoreCase(instCatName))
			{
				ceilingAvail=true;
				instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
		}
		String limitExceeded = "";
		if(instCeilingAmt==-1 && matCeilingAmt==-1)
		{
			ceilingAmt = invCeilingAmt;
			limitExceeded=" for Investee - " + fdDetail.getInvesteeName();
		}
		else{
			if (matCeilingAmt < instCeilingAmt)
			{
				if (invCeilingAmt < matCeilingAmt)
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + fdDetail.getInvesteeName();
				}
				else
				{
					ceilingAmt = matCeilingAmt;
					limitExceeded=" for Maturity - " + fdDetail.getMaturityName();
				}
			}
			else
			{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + fdDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + fdDetail.getInvesteeName();
				}
			}
		}
		
		if (ceilingAmt==0 || fdDetail.getPrincipalAmount() > ceilingAmt)
		{
			throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
		}

		fdDetail.setModifiedBy(loggedUserId);
		//fdDetail.setModifiedBy(modifiedByUser);
			ifProcessor.saveInvestmentDetail(fdDetail);
			Log.log(Log.INFO,"IFAction","updateFixedDetails","Exited");

			String message="Fixed deposit details saved Successfully";

			request.setAttribute("message",message);

			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showCommercialPapersDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showCommercialPapersDetails","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("invFlag", "0");
		IFProcessor ifProcessor=new IFProcessor();

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Commercial Papers");

			getAllInvestees(dynaForm);
			getInstrumentFeatures(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames);	
			Log.log(Log.INFO,"IFAction","showCommercialPapersDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	public ActionForward showRatingDetailsForCommercialPapers(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForCommercialPapers","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "3");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForCommercialPapers","Exited");
			return mapping.findForward("success");
		}

	public ActionForward updateCommercialPapersDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateCommercialPapersDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		CommercialPaperDetail commercialPaperDetail= new CommercialPaperDetail();
		BeanUtils.populate(commercialPaperDetail,dynaForm.getMap());
		commercialPaperDetail.setModifiedBy(loggedUserId);
		commercialPaperDetail.setTenureType("M");


		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getInvesteeName "+commercialPaperDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getInstrumentName "+commercialPaperDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getInvestmentName "+commercialPaperDetail.getInvestmentName());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getCommercialPaperNumber "+commercialPaperDetail.getCommercialPaperNumber());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getNameOfCompany "+commercialPaperDetail.getNameOfCompany());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getFaceValue "+commercialPaperDetail.getFaceValue());



		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","getNoOfCommercialPapers = "+commercialPaperDetail.getNoOfCommercialPapers());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","costOfPurchase = "+commercialPaperDetail.getCostOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","couponRate = "+commercialPaperDetail.getCouponRate());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","tenure = "+commercialPaperDetail.getTenure());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","tenureType = "+commercialPaperDetail.getTenureType());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","maturityName = "+commercialPaperDetail.getMaturityName());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","dateOfInvestment = "+commercialPaperDetail.getDateOfInvestment());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","dateOfMaturity = "+commercialPaperDetail.getMaturityDate());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","maturityAmount = "+commercialPaperDetail.getMaturityAmount());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","callOrPutOption = "+commercialPaperDetail.getCallOrPutOption());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","callOrPutDuration = "+commercialPaperDetail.getCallOrPutDuration());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(commercialPaperDetail.getMaturityDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			throw new MessageException("Maturity Date falls on Saturday. Please Change.");
		}*/
		if (dayOfWeek == Calendar.SUNDAY)
		{
			throw new MessageException("Maturity Date falls on Sunday. Please Change.");
		}

		IFProcessor ifProcessor=new IFProcessor();
		ArrayList holidays = ifProcessor.getAllHolidays();

		if (holidays.contains(commercialPaperDetail.getMaturityDate()))
		{
			throw new MessageException("Maturity Date falls on a Holiday. Please change.");
		}

		IFDAO ifDAO = new IFDAO();
		double corpusAmt = ifDAO.getCorpusAmount();
		Date date = new Date();
		
		Map matDetails = new TreeMap();
		Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
		Map mainDetails = (Map)ioReportDetails.get("DT");
		Set mainSet = mainDetails.keySet();
		Iterator mainIterator = mainSet.iterator();
		InflowOutflowReport ioReport = new InflowOutflowReport(); 
		while (mainIterator.hasNext())
		{
			ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
			if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
			{
				break;
			}
		}
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
		
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","matuirty amount "+exposureDetails.getMaturedAmount());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","stmt balance "+ioReport.getStmtBalance());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","fund transfer amount "+ioReport.getFundTransferInflow());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","provisions "+ioReport.getProvisionFundsAmt());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","min balance "+ioReport.getMinBalance());
		Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","invested amount "+exposureDetails.getInvestedAmount());
		
		double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
								Double.parseDouble(ioReport.getFundTransferInflow())) - 
								Double.parseDouble(ioReport.getChqissuedAmt()) - 
								Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
								Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
		
		double invCeilingAmt = 0;
		double matCeilingAmt = 0;
		double instCeilingAmt = 0;
		double ceilingAmt = 0;
		boolean ceilingAvail = false;
		
		String investeeName = commercialPaperDetail.getInvesteeName();
		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
		for (int i=0; i<investeeWiseExpDetails.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
			String invName = exposureDetailsTemp.getInvesteeName();
			Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","inv name "+ invName);
			if (invName.equalsIgnoreCase(investeeName))
			{
				ceilingAvail=true;
				invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
		}
		
		ArrayList maturityCeilingArr = ifProcessor.getMaturityWiseDetails(date, date, surplusAmt);
		String maturityName = commercialPaperDetail.getMaturityName();
		ceilingAvail = false;
			
		for (int i=0; i<maturityCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)maturityCeilingArr.get(i);
			String matName = exposureDetailsTemp.getMaturityName();
			Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","mat name "+ matName);
			if (matName.equalsIgnoreCase(maturityName))
			{
				ceilingAvail=true;
				matCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Maturity " + maturityName + " not available.");
		}
		
		ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
		String instCatName = commercialPaperDetail.getInstrumentCategory();
		ceilingAvail = false;
		
		for (int i=0; i<instCatCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
			String instName = exposureDetailsTemp.getInstCatName();
			Log.log(Log.DEBUG,"IFAction","updateCommercialPapersDetails","inst cat name "+ instName);
			if (instName.equalsIgnoreCase(instCatName))
			{
				ceilingAvail=true;
				instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
		}
		String limitExceeded = "";
		if(instCeilingAmt==-1 && matCeilingAmt==-1)
		{
			ceilingAmt = invCeilingAmt;
			limitExceeded=" for Investee - " + commercialPaperDetail.getInvesteeName();
		}
		else{
			if (matCeilingAmt < instCeilingAmt)
			{
				if (invCeilingAmt < matCeilingAmt)
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + commercialPaperDetail.getInvesteeName();
				}
				else
				{
					ceilingAmt = matCeilingAmt;
					limitExceeded=" for Maturity - " + commercialPaperDetail.getMaturityName();
				}
			}
			else
			{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + commercialPaperDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + commercialPaperDetail.getInvesteeName();
				}
			}
		}

		if (ceilingAmt==0 || commercialPaperDetail.getCostOfPurchase() > ceilingAmt)
		{
			throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
		}

		String instrfeatureArrayTemp[] = commercialPaperDetail.getInstrumentFeature();
		int cnt = instrfeatureArrayTemp.length;
			ifProcessor.saveInvestmentDetail(commercialPaperDetail);

			String message="Commercial Paper details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateCommercialPapersDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showMutualFundsDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showMutualFundsDetails","Entered");

		HttpSession session = request.getSession(false);
		IFProcessor ifProcessor = new IFProcessor();
		session.setAttribute("invFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Mutual Funds");

			getAllInvestees(dynaForm);
			dynaForm.set("instrument", "Mutual Funds");
			getInstrumentSchemeTypes(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames);
			Log.log(Log.INFO,"IFAction","showMutualFundsDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	public ActionForward showRatingDetailsForMutualFunds( 
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForMutualFunds","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "1");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForMutualFunds","Exited");
			return mapping.findForward("success");
		}	


	public ActionForward updateMutualFundsDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateMutualFundsDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		MutualFundDetail mutualFundDetail= new MutualFundDetail();
		BeanUtils.populate(mutualFundDetail,dynaForm.getMap());

		mutualFundDetail.setModifiedBy(loggedUserId);
		//mutualFundDetail.setModifiedBy(modifiedByUser);

		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getInvesteeName = "+mutualFundDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getInstrumentName = "+mutualFundDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getInvestmentName = "+mutualFundDetail.getInvestmentName());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","mutualFundId = "+mutualFundDetail.getMutualFundId());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getParValue = "+mutualFundDetail.getParValue());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getCostOfPurchase = "+mutualFundDetail.getCostOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getDateOfPurchase = "+mutualFundDetail.getDateOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getNavAsOnDateOfPurchase = "+mutualFundDetail.getNavAsOnDateOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getNavAsOnDate = "+mutualFundDetail.getNavAsOnDate());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getNoOfUnits = "+mutualFundDetail.getNoOfUnits());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getIsinNumber = "+mutualFundDetail.getIsinNumber());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getMutualFundName = "+mutualFundDetail.getMutualFundName());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getOpenOrClose = "+mutualFundDetail.getOpenOrClose());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getSchemeNature = "+mutualFundDetail.getSchemeNature());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getExitLoad = "+mutualFundDetail.getExitLoad());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getEntryLoad = "+mutualFundDetail.getEntryLoad());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getMarkToMarket = "+mutualFundDetail.getMarkToMarket());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getDateOfSelling = "+mutualFundDetail.getDateOfSelling());
		Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","getReferenceNumber = "+mutualFundDetail.getReferenceNumber());

			IFProcessor ifProcessor=new IFProcessor();
			
			IFDAO ifDAO = new IFDAO();
			double corpusAmt = ifDAO.getCorpusAmount();
			Date date = new Date();
		
			Map matDetails = new TreeMap();
			Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
			Map mainDetails = (Map)ioReportDetails.get("DT");
			Set mainSet = mainDetails.keySet();
			Iterator mainIterator = mainSet.iterator();
			InflowOutflowReport ioReport = new InflowOutflowReport(); 
			while (mainIterator.hasNext())
			{
				ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
				if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
				{
					break;
				}
			}
		
			ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
			
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","matuirty amount "+exposureDetails.getMaturedAmount());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","stmt balance "+ioReport.getStmtBalance());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","fund transfer amount "+ioReport.getFundTransferInflow());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","provisions "+ioReport.getProvisionFundsAmt());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","min balance "+ioReport.getMinBalance());
			Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","invested amount "+exposureDetails.getInvestedAmount());
		
			double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
									Double.parseDouble(ioReport.getFundTransferInflow())) - 
									Double.parseDouble(ioReport.getChqissuedAmt()) - 
									Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
									Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
		
			double invCeilingAmt = 0;
			double instCeilingAmt = 0;
			double ceilingAmt = 0;
			boolean ceilingAvail = false;
		
			String investeeName = mutualFundDetail.getInvesteeName();
			ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
			for (int i=0; i<investeeWiseExpDetails.size(); i++)
			{
				ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
				String invName = exposureDetailsTemp.getInvesteeName();
				Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","inv name "+ invName);
				if (invName.equalsIgnoreCase(investeeName))
				{
					ceilingAvail=true;
					invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
					break;
				}
			}
			
			if (! ceilingAvail)
			{
				throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
			}
		
			ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
			String instCatName = mutualFundDetail.getInstrumentCategory();
			ceilingAvail = false;
		
			for (int i=0; i<instCatCeilingArr.size(); i++)
			{
				ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
				String instName = exposureDetailsTemp.getInstCatName();
				Log.log(Log.DEBUG,"IFAction","updateMutualFundsDetails","inst cat name "+ instName);
				if (instName.equalsIgnoreCase(instCatName))
				{
					ceilingAvail=true;
					instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
					break;
				}
			}
			
			if (! ceilingAvail)
			{
				throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
			}
			String limitExceeded = "";
			if(instCeilingAmt==-1)
			{
				ceilingAmt = invCeilingAmt;
				limitExceeded=" for Investee - " + mutualFundDetail.getInvesteeName();
			}
			else{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + mutualFundDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + mutualFundDetail.getInvesteeName();
				}
			}
			//ceilingAmt = Math.min(invCeilingAmt, instCeilingAmt);
			
			Log.log(Log.INFO,"IFAction","updateMutualFundsDetails","mutualFundDetail.getCostOfPurchase() "+ mutualFundDetail.getCostOfPurchase());
			Log.log(Log.INFO,"IFAction","updateMutualFundsDetails","ceilingAmt "+ ceilingAmt);
		
			if (ceilingAmt==0 || mutualFundDetail.getCostOfPurchase() > ceilingAmt)
			{
				throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
			}
			
			ifProcessor.saveInvestmentDetail(mutualFundDetail);

			String message="Mutual Fund details saved Successfully";

			request.setAttribute("message",message);


			Log.log(Log.INFO,"IFAction","updateMutualFundsDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showGovtSecuritiesDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showGovtSecuritiesDetails","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("invFlag", "0");
		IFProcessor ifProcessor = new IFProcessor();

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Government Securities");

			getAllInvestees(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames);	
			Log.log(Log.INFO,"IFAction","showGovtSecuritiesDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	
	public ActionForward showRatingDetailsForGovtSecurities(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForGovtSecurities","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "3");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForGovtSecurities","Exited");
			return mapping.findForward("success");
		}

	public ActionForward updateGovtSecuritiesDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateGovtSecuritiesDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		GovtSecurityDetail govtSecurityDetail= new GovtSecurityDetail();
		BeanUtils.populate(govtSecurityDetail,dynaForm.getMap());

		govtSecurityDetail.setModifiedBy(loggedUserId);
		//govtSecurityDetail.setModifiedBy(modifiedByUser);

		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","getInvesteeName = "+govtSecurityDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","getInstrumentName = "+govtSecurityDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","getInvestmentName = "+govtSecurityDetail.getInvestmentName());

		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","getSeriesName = "+govtSecurityDetail.getSeriesName());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","faceValue = "+govtSecurityDetail.getFaceValue());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","numberOfSecurities = "+govtSecurityDetail.getNumberOfSecurities());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","folioNumber = "+govtSecurityDetail.getFolioNumber());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","certificateNumber = "+govtSecurityDetail.getCertificateNumber());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","costOfPurchase = "+govtSecurityDetail.getCostOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","couponRate = "+govtSecurityDetail.getCouponRate());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","tenure = "+govtSecurityDetail.getTenure());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","tenureType = "+govtSecurityDetail.getTenureType());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","dateOfInvestment = "+govtSecurityDetail.getDateOfInvestment());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","dateOfMaturity = "+govtSecurityDetail.getMaturityDate());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","maturityAmount = "+govtSecurityDetail.getMaturityAmount());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","callOrPutOption = "+govtSecurityDetail.getCallOrPutOption());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","callOrPutDuration = "+govtSecurityDetail.getCallOrPutDuration());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","maturityName = "+govtSecurityDetail.getMaturityName());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(govtSecurityDetail.getMaturityDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			throw new MessageException("Maturity Date falls on Saturday. Please Change.");
		}*/
		if (dayOfWeek == Calendar.SUNDAY)
		{
			throw new MessageException("Maturity Date falls on Sunday. Please Change.");
		}

		IFProcessor ifProcessor=new IFProcessor();
		ArrayList holidays = ifProcessor.getAllHolidays();

		if (holidays.contains(govtSecurityDetail.getMaturityDate()))
		{
			throw new MessageException("Maturity Date falls on a Holiday. Please change.");
		}

		IFDAO ifDAO = new IFDAO();
		double corpusAmt = ifDAO.getCorpusAmount();
		Date date = new Date();
		
		Map matDetails = new TreeMap();
		Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
		Map mainDetails = (Map)ioReportDetails.get("DT");
		Set mainSet = mainDetails.keySet();
		Iterator mainIterator = mainSet.iterator();
		InflowOutflowReport ioReport = new InflowOutflowReport(); 
		while (mainIterator.hasNext())
		{
			ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
			if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
			{
				break;
			}
		}
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
		
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","matuirty amount "+exposureDetails.getMaturedAmount());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","stmt balance "+ioReport.getStmtBalance());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","fund transfer amount "+ioReport.getFundTransferInflow());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","provisions "+ioReport.getProvisionFundsAmt());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","min balance "+ioReport.getMinBalance());
		Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","invested amount "+exposureDetails.getInvestedAmount());
		
		double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
								Double.parseDouble(ioReport.getFundTransferInflow())) - 
								Double.parseDouble(ioReport.getChqissuedAmt()) - 
								Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
								Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
		
		double invCeilingAmt = 0;
		double matCeilingAmt = 0;
		double instCeilingAmt = 0;
		double ceilingAmt = 0;
		boolean ceilingAvail = false;
		
		String investeeName = govtSecurityDetail.getInvesteeName();
		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
		for (int i=0; i<investeeWiseExpDetails.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
			String invName = exposureDetailsTemp.getInvesteeName();
			Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","inv name "+ invName);
			if (invName.equalsIgnoreCase(investeeName))
			{
				ceilingAvail=true;
				invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
		}
		
		ArrayList maturityCeilingArr = ifProcessor.getMaturityWiseDetails(date, date, surplusAmt);
		String maturityName = govtSecurityDetail.getMaturityName();
		ceilingAvail = false;
			
		for (int i=0; i<maturityCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)maturityCeilingArr.get(i);
			String matName = exposureDetailsTemp.getMaturityName();
			Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","mat name "+ matName);
			if (matName.equalsIgnoreCase(maturityName))
			{
				ceilingAvail=true;
				matCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Maturity " + maturityName + " not available.");
		}
		
		ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
		String instCatName = govtSecurityDetail.getInstrumentCategory();
		ceilingAvail = false;
		
		for (int i=0; i<instCatCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
			String instName = exposureDetailsTemp.getInstCatName();
			Log.log(Log.DEBUG,"IFAction","updateGovtSecuritiesDetails","inst cat name "+ instName);
			if (instName.equalsIgnoreCase(instCatName))
			{
				ceilingAvail=true;
				instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
		}
		String limitExceeded = "";
		if(instCeilingAmt==-1 && matCeilingAmt==-1)
		{
			ceilingAmt = invCeilingAmt;
			limitExceeded=" for Investee - " + govtSecurityDetail.getInvesteeName();
		}
		else{
			if (matCeilingAmt < instCeilingAmt)
			{
				if (invCeilingAmt < matCeilingAmt)
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + govtSecurityDetail.getInvesteeName();
				}
				else
				{
					ceilingAmt = matCeilingAmt;
					limitExceeded=" for Maturity - " + govtSecurityDetail.getMaturityName();
				}
			}
			else
			{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + govtSecurityDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + govtSecurityDetail.getInvesteeName();
				}
			}
		}
		
		
		if (ceilingAmt==0 || govtSecurityDetail.getCostOfPurchase() > ceilingAmt)
		{
			throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
		}

			ifProcessor.saveInvestmentDetail(govtSecurityDetail);

			String message="Government Security details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateGovtSecuritiesDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showDebenturesDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showDebenturesDetails","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("invFlag", "0");
		IFProcessor ifProcessor = new IFProcessor();

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Debentures");

			getAllInvestees(dynaForm);
			getInstrumentFeatures(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames);			
			Log.log(Log.INFO,"IFAction","showDebenturesDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	
	public ActionForward showRatingDetailsForDebentures(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForDebentures","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "3");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForDebentures","Exited");
			return mapping.findForward("success");
		}

	public ActionForward updateDebenturesDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateDebenturesDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		DebentureDetail debentureDetail= new DebentureDetail();
		BeanUtils.populate(debentureDetail,dynaForm.getMap());
		debentureDetail.setModifiedBy(loggedUserId);
		//debentureDetail.setModifiedBy(modifiedByUser);

		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","getInvesteeName = "+debentureDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","getInvesteeName = "+debentureDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","getInvesteeName = "+debentureDetail.getInvestmentName());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","debentureName = "+debentureDetail.getDebentureName());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","faceValue = "+debentureDetail.getFaceValue());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","numberOfSecurities = "+debentureDetail.getNumberOfSecurities());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","folioNumber = "+debentureDetail.getFolioNumber());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","certificateNumber = "+debentureDetail.getCertificateNumber());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","costOfPurchase = "+debentureDetail.getCostOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","couponRate = "+debentureDetail.getCouponRate());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","tenure = "+debentureDetail.getTenure());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","tenureType = "+debentureDetail.getTenureType());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","maturityName = "+debentureDetail.getMaturityName());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","dateOfInvestment = "+debentureDetail.getDateOfInvestment());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","dateOfMaturity = "+debentureDetail.getMaturityDate());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","maturityAmount = "+debentureDetail.getMaturityAmount());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","callOrPutOption = "+debentureDetail.getCallOrPutOption());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","callOrPutDuration = "+debentureDetail.getCallOrPutDuration());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(debentureDetail.getMaturityDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			throw new MessageException("Maturity Date falls on Saturday. Please Change.");
		}*/
		if (dayOfWeek == Calendar.SUNDAY)
		{
			throw new MessageException("Maturity Date falls on Sunday. Please Change.");
		}

		IFProcessor ifProcessor=new IFProcessor();
		ArrayList holidays = ifProcessor.getAllHolidays();

		if (holidays.contains(debentureDetail.getMaturityDate()))
		{
			throw new MessageException("Maturity Date falls on a Holiday. Please change.");
		}
		
		IFDAO ifDAO = new IFDAO();
		double corpusAmt = ifDAO.getCorpusAmount();
		Date date = new Date();
		
		Map matDetails = new TreeMap();
		Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
		Map mainDetails = (Map)ioReportDetails.get("DT");
		Set mainSet = mainDetails.keySet();
		Iterator mainIterator = mainSet.iterator();
		InflowOutflowReport ioReport = new InflowOutflowReport(); 
		while (mainIterator.hasNext())
		{
			ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
			if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
			{
				break;
			}
		}
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
		
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","matuirty amount "+exposureDetails.getMaturedAmount());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","stmt balance "+ioReport.getStmtBalance());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","fund transfer amount "+ioReport.getFundTransferInflow());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","provisions "+ioReport.getProvisionFundsAmt());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","min balance "+ioReport.getMinBalance());
		Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","invested amount "+exposureDetails.getInvestedAmount());
		
		double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
								Double.parseDouble(ioReport.getFundTransferInflow())) - 
								Double.parseDouble(ioReport.getChqissuedAmt()) - 
								Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
								Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
		
		double invCeilingAmt = 0;
		double matCeilingAmt = 0;
		double instCeilingAmt = 0;
		double ceilingAmt = 0;
		boolean ceilingAvail = false;
		
		String investeeName = debentureDetail.getInvesteeName();
		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
		for (int i=0; i<investeeWiseExpDetails.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
			String invName = exposureDetailsTemp.getInvesteeName();
			Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","inv name "+ invName);
			if (invName.equalsIgnoreCase(investeeName))
			{
				ceilingAvail=true;
				invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
		}
		
		ArrayList maturityCeilingArr = ifProcessor.getMaturityWiseDetails(date, date, surplusAmt);
		String maturityName = debentureDetail.getMaturityName();
		ceilingAvail = false;
			
		for (int i=0; i<maturityCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)maturityCeilingArr.get(i);
			String matName = exposureDetailsTemp.getMaturityName();
			Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","mat name "+ matName);
			if (matName.equalsIgnoreCase(maturityName))
			{
				ceilingAvail=true;
				matCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Maturity " + maturityName + " not available.");
		}
		
		ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
		String instCatName = debentureDetail.getInstrumentCategory();
		ceilingAvail = false;
		
		for (int i=0; i<instCatCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
			String instName = exposureDetailsTemp.getInstCatName();
			Log.log(Log.DEBUG,"IFAction","updateDebenturesDetails","inst cat name "+ instName);
			if (instName.equalsIgnoreCase(instCatName))
			{
				ceilingAvail=true;
				instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
		}
		String limitExceeded = "";
		if(instCeilingAmt==-1 && matCeilingAmt==-1)
		{
			ceilingAmt = invCeilingAmt;
			limitExceeded=" for Investee - " + debentureDetail.getInvesteeName();
		}
		else{
			if (matCeilingAmt < instCeilingAmt)
			{
				if (invCeilingAmt < matCeilingAmt)
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + debentureDetail.getInvesteeName();
				}
				else
				{
					ceilingAmt = matCeilingAmt;
					limitExceeded=" for Maturity - " + debentureDetail.getMaturityName();
				}
			}
			else
			{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + debentureDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + debentureDetail.getInvesteeName();
				}
			}
		}
		
		
		if (ceilingAmt==0 || debentureDetail.getCostOfPurchase() > ceilingAmt)
		{
			throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
		}

//		String instrfeatureArrayTemp[] = debentureDetail.getDebentureFeature();
//		int cnt = instrfeatureArrayTemp.length;
		ifProcessor.saveInvestmentDetail(debentureDetail);

			String message="Debenture details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateDebenturesDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBondDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBondDetails","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("invFlag", "0");
		IFProcessor ifProcessor = new IFProcessor();
			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			dynaForm.set("instrumentName", "Bonds");
			getAllInvestees(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			getAllMaturities(dynaForm);
			getAllRatings(dynaForm);
			getInstrumentCategories(dynaForm);
			ArrayList agencyNames = ifProcessor.showRatingAgency();
			dynaForm.set("agencies", agencyNames);
			Log.log(Log.INFO,"IFAction","showBondDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	
	public ActionForward showRatingDetailsForBonds(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showRatingDetailsForBonds","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "3");
			IFProcessor ifProcessor = new IFProcessor();
			ArrayList empty = new ArrayList();

			ArrayList allowableRatings = new ArrayList();
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			String agencyName = (String) dynaForm.get("agency");
			//System.out.println("agencyName:"+agencyName);
			String investee = (String) dynaForm.get("investeeName");
			//System.out.println("investee:"+investee);
			String instrument = (String) dynaForm.get("instrumentName");
			//System.out.println("instrument:"+instrument);		
			String newInstrument = 	instrument.toUpperCase();
			//System.out.println("newInstrument:"+newInstrument);				
			
			String ceiling = null;

			if (agencyName.equals(""))
			{
				dynaForm.set("instrumentRatings",allowableRatings);
				dynaForm.set("ratingCeiling", "");

			}
			else
			{
				ifProcessor = new IFProcessor();
				allowableRatings = ifProcessor.getRatingsForAgency(agencyName);
				dynaForm.set("instrumentRatings", allowableRatings);
				ceiling = ifProcessor.getCeiling(agencyName,investee,newInstrument);
				dynaForm.set("ratingCeiling", ceiling);
				
			}
			
			Log.log(Log.INFO,"IFAction","showRatingDetailsForBonds","Exited");
			return mapping.findForward("success");
		}

	public ActionForward updateBondDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateBondDetails","Entered");
		Hashtable ratingDetails = new Hashtable();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		DynaActionForm dynaForm=(DynaActionForm)form;
		BondsDetail bondsDetail= new BondsDetail();
		BeanUtils.populate(bondsDetail,dynaForm.getMap());
		bondsDetail.setModifiedBy(loggedUserId);

		Log.log(Log.DEBUG,"IFAction","updateBondDetails","getInvesteeName = "+bondsDetail.getInvesteeName());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","getInstrumentName = "+bondsDetail.getInstrumentName());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","getInvestmentName = "+bondsDetail.getInvestmentName());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","bondName = "+bondsDetail.getBondName());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","faceValue = "+bondsDetail.getFaceValue());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","noOfSecurities = "+bondsDetail.getNumberOfSecurities());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","folioNumber = "+bondsDetail.getFolioNumber());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","certificateNumber = "+bondsDetail.getCertificateNumber());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","costOfPurchase = "+bondsDetail.getCostOfPurchase());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","couponRate = "+bondsDetail.getCouponRate());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","tenure = "+bondsDetail.getTenure());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","tenureType = "+bondsDetail.getTenureType());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","maturityName = "+bondsDetail.getMaturityName());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","dateOfInvestment = "+bondsDetail.getDateOfInvestment());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","dateOfMaturity = "+bondsDetail.getMaturityDate());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","maturityAmount = "+bondsDetail.getMaturityAmount());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","callOrPutOption = "+bondsDetail.getCallOrPutOption());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","callOrPutDuration = "+bondsDetail.getCallOrPutDuration());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bondsDetail.getMaturityDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			throw new MessageException("Maturity Date falls on Saturday. Please Change.");
		}*/
		if (dayOfWeek == Calendar.SUNDAY)
		{
			throw new MessageException("Maturity Date falls on Sunday. Please Change.");
		}

		IFProcessor ifProcessor=new IFProcessor();
		ArrayList holidays = ifProcessor.getAllHolidays();

		if (holidays.contains(bondsDetail.getMaturityDate()))
		{
			throw new MessageException("Maturity Date falls on a Holiday. Please change.");
		}
		
		IFDAO ifDAO = new IFDAO();
		double corpusAmt = ifDAO.getCorpusAmount();
		Date date = new Date();
		
		Map matDetails = new TreeMap();
		Map ioReportDetails = ifProcessor.showInflowOutflowReport(date, matDetails, "");
		Map mainDetails = (Map)ioReportDetails.get("DT");
		Set mainSet = mainDetails.keySet();
		Iterator mainIterator = mainSet.iterator();
		InflowOutflowReport ioReport = new InflowOutflowReport(); 
		while (mainIterator.hasNext())
		{
			ioReport = (InflowOutflowReport)mainDetails.get(mainIterator.next());
			if ((ioReport.getBankName().substring(0,9)).equalsIgnoreCase("IDBI Bank"))
			{
				break;
			}
		}
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(date, date);
		
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","matuirty amount "+exposureDetails.getMaturedAmount());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","stmt balance "+ioReport.getStmtBalance());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","fund transfer amount "+ioReport.getFundTransferInflow());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","chq issued but not presented amount "+ioReport.getChqissuedAmt());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","provisions "+ioReport.getProvisionFundsAmt());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","min balance "+ioReport.getMinBalance());
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","invested amount "+exposureDetails.getInvestedAmount());
		
		double surplusAmt = (exposureDetails.getMaturedAmount() + Double.parseDouble(ioReport.getStmtBalance()) +
								Double.parseDouble(ioReport.getFundTransferInflow())) - 
								Double.parseDouble(ioReport.getChqissuedAmt()) - 
								Double.parseDouble(ioReport.getProvisionFundsAmt()) - 
								Double.parseDouble(ioReport.getMinBalance()) - exposureDetails.getInvestedAmount();
								
		Log.log(Log.DEBUG,"IFAction","updateBondDetails","surplus amount "+surplusAmt);
		
		double invCeilingAmt = 0;
		double matCeilingAmt = 0;
		double instCeilingAmt = 0;
		double ceilingAmt = 0;
		boolean ceilingAvail = false;
		
		String investeeName = bondsDetail.getInvesteeName();
		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(date, date,corpusAmt);
			
		for (int i=0; i<investeeWiseExpDetails.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)investeeWiseExpDetails.get(i);
			String invName = exposureDetailsTemp.getInvesteeName();
			Log.log(Log.DEBUG,"IFAction","updateBondDetails","inv name "+ invName);
			if (invName.equalsIgnoreCase(investeeName))
			{
				ceilingAvail=true;
				invCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Investee " + investeeName + " not available.");
		}
		
		ArrayList maturityCeilingArr = ifProcessor.getMaturityWiseDetails(date, date, surplusAmt);
		String maturityName = bondsDetail.getMaturityName();
		ceilingAvail = false;
			
		for (int i=0; i<maturityCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)maturityCeilingArr.get(i);
			String matName = exposureDetailsTemp.getMaturityName();
			Log.log(Log.DEBUG,"IFAction","updateBondDetails","mat name "+ matName);
			if (matName.equalsIgnoreCase(maturityName))
			{
				ceilingAvail=true;
				matCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Maturity " + maturityName + " not available.");
		}
		
		ArrayList instCatCeilingArr = ifProcessor.getInstCategoryWiseDetails(date, date, surplusAmt);
		String instCatName = bondsDetail.getInstrumentCategory();
		ceilingAvail = false;
		
		for (int i=0; i<instCatCeilingArr.size(); i++)
		{
			ExposureDetails exposureDetailsTemp=(ExposureDetails)instCatCeilingArr.get(i);
			String instName = exposureDetailsTemp.getInstCatName();
			Log.log(Log.DEBUG,"IFAction","updateBondDetails","inst cat name "+ instName);
			if (instName.equalsIgnoreCase(instCatName))
			{
				ceilingAvail=true;
				instCeilingAmt=exposureDetailsTemp.getGapAvailableAmount();
				break;
			}
		}
			
		if (! ceilingAvail)
		{
			throw new MessageException("Ceiling for Instrument Category " + instCatName + " not available.");
		}
		String limitExceeded = "";
		if(instCeilingAmt==-1 && matCeilingAmt==-1)
		{
			ceilingAmt = invCeilingAmt;
			limitExceeded=" for Investee - " + bondsDetail.getInvesteeName();
		}
		else{
			if (matCeilingAmt < instCeilingAmt)
			{
				if (invCeilingAmt < matCeilingAmt)
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + bondsDetail.getInvesteeName();
				}
				else
				{
					ceilingAmt = matCeilingAmt;
					limitExceeded=" for Maturity - " + bondsDetail.getMaturityName();
				}
			}
			else
			{
				if (instCeilingAmt < invCeilingAmt)
				{
					ceilingAmt = instCeilingAmt;
					limitExceeded=" for Instrument Category - " + bondsDetail.getInstrumentCategory();
				}
				else
				{
					ceilingAmt = invCeilingAmt;
					limitExceeded=" for Investee - " + bondsDetail.getInvesteeName();
				}
			}
		}
		
		//ceilingAmt = Math.min(invCeilingAmt, Math.min(matCeilingAmt, instCeilingAmt));
		
		if (ceilingAmt==0 || bondsDetail.getCostOfPurchase() > ceilingAmt)
		{
			throw new MessageException("Invested Amount has exceeded the Ceiling Limits" + limitExceeded);
		}

			ifProcessor.saveInvestmentDetail(bondsDetail);

			String message="Bond details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateBondDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInvestementFullfillment(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvestementFullfillment","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("fullfilmentFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			
			IFProcessor ifProcessor=new IFProcessor();

			getAllInvestees(dynaForm);
			getInstrumentTypes(dynaForm);
			getGenInstrumentTypes(dynaForm);
			getInstrumentFeatures(dynaForm);

			getInstrumentSchemes(dynaForm);
			getInvestmentReferenceNumbers(dynaForm);
			
			ArrayList bankDetails = ifProcessor.getBankAccounts();
			
			ArrayList bankNames = new ArrayList();
							
			for(int i=0; i<bankDetails.size(); i++)
			{
				BankAccountDetail bankAccountDetail = (BankAccountDetail)bankDetails.get(i);
				String bankName = bankAccountDetail.getBankName();
				String branchName = bankAccountDetail.getBankBranchName();
								
				String bankBranchName = bankName + "," + branchName;
				bankNames.add(bankBranchName);
			}
							
			dynaForm.set("bankAcctDetails",bankNames);
			

			Log.log(Log.INFO,"IFAction","showInvestementFullfillment","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward updateInvestementFullfillment(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","Entered");
		Hashtable ratingDetails = new Hashtable();
		DynaActionForm dynaForm=(DynaActionForm)form;
		InvestmentFulfillmentDetail investmentFulfillmentDetail = new InvestmentFulfillmentDetail();
		//String modifiedByUser	= null;
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";
		BeanUtils.populate(investmentFulfillmentDetail,dynaForm.getMap());
		investmentFulfillmentDetail.setModifiedBy(loggedUserId);
		//investmentFulfillmentDetail.setModifiedBy(modifiedByUser);
		
		String contextPath = request.getSession(false).getServletContext().getRealPath("");

		//Log.log(Log.DEBUG,"IFAction","updateInvestementFullfillment","Inves = "+investmentFulfillmentDetail.getInvestment());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInstrumentName = "+investmentFulfillmentDetail.getInstrumentName());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInflowOutFlowFlag = "+investmentFulfillmentDetail.getInflowOutFlowFlag());

		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInstrumentType = "+investmentFulfillmentDetail.getInstrumentType());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInstrumentNumber = "+investmentFulfillmentDetail.getInstrumentNumber());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInstrumentDate = "+investmentFulfillmentDetail.getInstrumentDate());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getInstrumentAmount = "+investmentFulfillmentDetail.getInstrumentAmount());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getDrawnBank = "+investmentFulfillmentDetail.getDrawnBank());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getDrawnBranch = "+investmentFulfillmentDetail.getDrawnBranch());
		Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","getPayableAt = "+investmentFulfillmentDetail.getPayableAt());
			IFProcessor ifProcessor=new IFProcessor();
			HttpSession session = request.getSession(false);
			String updateFlag = "";
			if (session.getAttribute("updateFlag")!=null && !session.getAttribute("updateFlag").equals(""));
			{
				updateFlag = (String)session.getAttribute("updateFlag");
			}
			
			ChequeDetails chequeDetails = new ChequeDetails();
			
			if(investmentFulfillmentDetail.getInstrumentType().equals("CHEQUE"))
			{
				/**
				 * insert details into cheque_issued detail
				 */
				if(dynaForm.get("bnkName")==null || dynaForm.get("bnkName").equals(""))
				{
					throw new MessageException("Since no bank names are available,Cheque Details cannot be inserted");
				}
				String bankBranchName = (String)dynaForm.get("bnkName");
				int start=bankBranchName.indexOf(",");
				String bankName = bankBranchName.substring(0,start);
						
				String branchName =  bankBranchName.substring(start + 1);
 
				chequeDetails.setUserId(loggedUserId);
				chequeDetails.setChequeAmount(investmentFulfillmentDetail.getInstrumentAmount());
				chequeDetails.setChequeDate(investmentFulfillmentDetail.getInstrumentDate());
				chequeDetails.setChequeNumber(investmentFulfillmentDetail.getInstrumentNumber());
				chequeDetails.setChequeIssuedTo(RpConstants.RP_CGTSI);
				chequeDetails.setBankName(bankName);
				chequeDetails.setBranchName(branchName);
				chequeDetails.setChequeRemarks(null);
				chequeDetails.setPayId(null);
				
				//ifProcessor.chequeDetailsInsertSuccess(chequeDetails,contextPath,loggedUserId);
			}
			else{
				chequeDetails = null;
			}
			
			ifProcessor.saveInvestmentFulfillmentDetail(investmentFulfillmentDetail, updateFlag,chequeDetails,contextPath,loggedUserId);
			
			//ifProcessor.chequeDetailsInsertSuccess()
			
			/**
			 * generate voucher for investment fulfillment
			 */
			
				//ifProcessor.insertVoucherForFulfillment(investmentFulfillmentDetail,contextPath,loggedInUser);
			/**
			 * 
			 */

			String message="Investment Fulfillment details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateInvestementFullfillment","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}


	public ActionForward setBudgetInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

//		System.out.println("Entered Set Budget Inflow details ");

		Log.log(Log.INFO,"IFAction","setBudgetInflowDetails","Entered");
		String budget="";
		HashMap heads	=new HashMap();
		HashMap subHeads=new HashMap();
		Set headsSet;
		Iterator headsIterator;
		Set subHeadsSet;
		Iterator subHeadsIterator;

		InvestmentForm ifForm=(InvestmentForm) form;
		heads=(HashMap) ifForm.getHeads();

		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","Budget Type "+ifForm.getAnnualOrShortTerm());

		HashMap subHeads1=new HashMap();
		Set subHeadsSet1;
		Iterator subHeadsIterator1;
		subHeads1=(HashMap) ifForm.getSubHeads();
		subHeadsSet1=subHeads1.keySet();
		subHeadsIterator1=subHeadsSet1.iterator();

		while(subHeadsIterator1.hasNext())
		{
			String key=(String)subHeadsIterator1.next();
			String value=(String)subHeads1.get(key);
			Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","key, value "+key+", "+value);
			int index=key.indexOf("_");
			Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","index "+index);
			String key1=key.substring(0,index);
			Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","key, value "+key1);
			heads.put(key1,"");
		}
		headsSet=heads.keySet();
		headsIterator=headsSet.iterator();
		subHeadsIterator1=subHeadsSet1.iterator();

		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","sub-heads = "+subHeads1);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","sub-headsSet = "+subHeadsSet1);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","sub-headsIterator = "+subHeadsIterator1);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","heads = "+heads);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","headsSet = "+headsSet);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","headsIterator = "+headsIterator);


		HashMap headsToRender	=new HashMap();
		HashMap subHeadsToRender=new HashMap();
		Set subHeadsSetToRender;
		Iterator subHeadsIteratorToRender;
		headsToRender=(HashMap) ifForm.getHeadsToRender();

		String headTitle			="";
		BudgetDetails budgetDetails = new BudgetDetails();
		ArrayList aBudgetHeadDetails= new ArrayList();

		String inflowOrOutflow	= null;
		String term				= null;
		String annualToDate		= null;
		String annualFromDate	= null;
		String month			= null;
		String year				= null;
		String modifiedByUser	= null;

		inflowOrOutflow	=	(String)ifForm.getInflowOrOutflow();
		term			=	(String)ifForm.getAnnualOrShortTerm();
		annualToDate	=	(String)ifForm.getAnnualToDate();
		annualFromDate	=	(String)ifForm.getAnnualFromDate();
		month			=	(String)ifForm.getMonth();
		year			=	(String)ifForm.getYear();
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		String headsAndVals = (String)ifForm.getSubHeadsRam();

		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","inflowOrOutflow = "+inflowOrOutflow);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","loggerd man");
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","term = "+term);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","annualToDate = "+annualToDate);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","annualFromDate = "+annualFromDate);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","month = "+month);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","year = "+year);
		Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","Heads and Val = "+headsAndVals);
		budgetDetails.setAnnualOrShortTerm(term);
		budgetDetails.setAnnualToDate(annualToDate);
		budgetDetails.setAnnualFromDate(annualFromDate);
		budgetDetails.setMonth(month);
		budgetDetails.setYear(year);
		budgetDetails.setModifiedBy(loggedUserId);
		budgetDetails.setInflowOrOutflow(inflowOrOutflow);

		while (headsIterator.hasNext())
		{
			BudgetHeadDetails budgetHeadDetails = new BudgetHeadDetails();
			headTitle=(String)headsIterator.next();

			Log.log(Log.INFO,"IFAction","setBudgetInflowDetails","headTitle "+headTitle);

			if (heads.get(headTitle) != null)
			{
				budgetHeadDetails.setBudgetHead(headTitle);
				ArrayList aBudgetSubHeadDetails = null;
				subHeadsToRender=(HashMap) headsToRender.get(headTitle);
				String budgetAmount = (String)heads.get(headTitle);

				if (budgetAmount == null || budgetAmount.equals(""))
				{
					 budgetAmount = "0.0";
				}

				budgetHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));
				Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","subHeadsToRender "+subHeadsToRender);

				if(subHeadsToRender != null)
				{
					aBudgetSubHeadDetails = new ArrayList();
					subHeadsSetToRender=subHeadsToRender.keySet();
					subHeadsIteratorToRender=subHeadsSetToRender.iterator();
					String subHeadTitle="";
					while (subHeadsIteratorToRender.hasNext())
					{
						BudgetSubHeadDetails budgetSubHeadDetails = new BudgetSubHeadDetails();
						subHeadTitle=subHeadsIteratorToRender.next().toString();
						budgetSubHeadDetails.setSubHeadTitle(subHeadTitle);

						Log.log(Log.DEBUG,"IFAction","setBudgetInflowDetails","subHeadTitle "+subHeadTitle);

						budgetAmount = (String)subHeads1.get(headTitle+"_"+subHeadTitle);

						if (budgetAmount == null || budgetAmount.equals(""))
						{
							 budgetAmount = "0.0";
						}

						budgetSubHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));
						aBudgetSubHeadDetails.add(budgetSubHeadDetails);
						budgetSubHeadDetails = null;
					}
				}
				budgetHeadDetails.setBudgetSubHeadDetails(aBudgetSubHeadDetails);
			}
			else
			{
			Log.log(Log.INFO,"IFAction","setBudgetInflowDetails","No head details ");

			}
			aBudgetHeadDetails.add(budgetHeadDetails);
		}
		budgetDetails.setBudgetHeadDetails(aBudgetHeadDetails);
		ArrayList budgetHeadDetailsram = budgetDetails.getBudgetHeadDetails();
		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.saveBudgetDetails(budgetDetails);


		String message="Budget Inflow details saved Successfully";

		if(inflowOrOutflow.equals(InvestmentFundConstants.OUTFLOW))
		{
			message="Budget Outflow details saved Successfully";
		}

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","setBudgetInflowDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward setInflowForecastDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","setInflowForecastDetails","Entered");
		String budget="";
		HashMap heads	=new HashMap();
		HashMap subHeads=new HashMap();
		Set headsSet;
		Iterator headsIterator;
		Set subHeadsSet;
		Iterator subHeadsIterator;
		InvestmentForm ifForm=(InvestmentForm) form;
			heads=(HashMap) ifForm.getHeads();
			headsSet=heads.keySet();
			headsIterator=headsSet.iterator();
			HashMap subHeads1=new HashMap();
			Set subHeadsSet1;
			Iterator subHeadsIterator1;
			subHeads1=(HashMap) ifForm.getSubHeads();
			subHeadsSet1=subHeads1.keySet();
			subHeadsIterator1=subHeadsSet1.iterator();

			//ramvel render begin
			HashMap headsToRender	=new HashMap();
			HashMap subHeadsToRender=new HashMap();
			Set subHeadsSetToRender;
			Iterator subHeadsIteratorToRender;
			headsToRender=(HashMap) ifForm.getHeadsToRender();
			//ramvel render end
			String headTitle			="";
			ForecastDetails forecastDetails = new ForecastDetails();
			ArrayList aForecastHeadDetails= new ArrayList();

			String inflowOrOutflow	= null;
			String term				= null;
			String startDate		= null;
			String endDate			= null;
			String modifiedByUser	= null;

			inflowOrOutflow	=	(String)ifForm.getInflowOrOutflow();
			endDate			=	(String)ifForm.getEndDate();
			startDate		=	(String)ifForm.getStartDate();
			//Get the logged in user's userId.
			User loggedInUser=getUserInformation(request);
			String loggedUserId=loggedInUser.getUserId();

			//modifiedByUser	=	"CGTSI001";//"PVAIDY01";

			String headsAndVals = (String)ifForm.getSubHeadsRam();

			Log.log(Log.DEBUG,"IFAction","setInflowForecastDetails","inflowOrOutflow = "+inflowOrOutflow);
			Log.log(Log.DEBUG,"IFAction","setInflowForecastDetails","loggerd man");
			Log.log(Log.DEBUG,"IFAction","setInflowForecastDetails","startDate = "+startDate);
			Log.log(Log.DEBUG,"IFAction","setInflowForecastDetails","endDate = "+endDate);
			Log.log(Log.DEBUG,"IFAction","setInflowForecastDetails","Heads and Val = "+headsAndVals);

			forecastDetails.setStartDate(startDate);
			forecastDetails.setEndDate(endDate);
			forecastDetails.setModifiedBy(modifiedByUser);
			forecastDetails.setModifiedBy(loggedUserId);

			while (headsIterator.hasNext())
			{
				ForecastHeadDetails forecastHeadDetails = new ForecastHeadDetails();
				headTitle=(String)headsIterator.next();
				if (heads.get(headTitle) != null)
				{
					forecastHeadDetails.setForecastHead(headTitle);
					ArrayList aForecastSubHeadDetails = null;
					subHeadsToRender=(HashMap) headsToRender.get(headTitle);
					if(subHeadsToRender != null)
					{
						aForecastSubHeadDetails = new ArrayList();
						subHeadsSetToRender=subHeadsToRender.keySet();
						subHeadsIteratorToRender=subHeadsSetToRender.iterator();
						String subHeadTitle="";
						while (subHeadsIteratorToRender.hasNext())
						{
							ForecastSubHeadDetails forecastSubHeadDetails = new ForecastSubHeadDetails();
							subHeadTitle=subHeadsIteratorToRender.next().toString();
							forecastSubHeadDetails.setSubHeadTitle(subHeadTitle);
							String forecastAmount = (String)subHeads1.get(headTitle+"_"+subHeadTitle);
							if (forecastAmount == null) forecastAmount = "0.0";
							forecastSubHeadDetails.setForecastAmount(Double.parseDouble(forecastAmount));
							aForecastSubHeadDetails.add(forecastSubHeadDetails);
							forecastSubHeadDetails = null;
						}
					}
					forecastHeadDetails.setForecastSubHeadDetails(aForecastSubHeadDetails);
				}
				else{
					Log.log(Log.INFO,"IFAction","setInflowForecastDetails","No head details ");
				}
				aForecastHeadDetails.add(forecastHeadDetails);
			}
			forecastDetails.setForecastHeadDetails(aForecastHeadDetails);
			IFProcessor ifProcessor=new IFProcessor();
			ifProcessor.saveForecastingDetail(forecastDetails);
			Log.log(Log.INFO,"IFAction","setInflowForecastDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward setAnnualFundsInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","setAnnualFundsInflowDetails","Entered");

			InvestmentForm investmentForm=(InvestmentForm)form;

			saveFundsInflowOutflowDetails(investmentForm,request);

			Log.log(Log.INFO,"IFAction","setAnnualFundsInflowDetails","Exited");

			String message="Inflow details saved Successfully";

			request.setAttribute("message",message);

			return mapping.findForward(Constants.SUCCESS);
		}
	private void saveFundsInflowOutflowDetails(InvestmentForm ifForm,
			HttpServletRequest request) throws DatabaseException
	{
		Log.log(Log.INFO,"IFAction","saveFundsInflowOutflowDetails","Entered");
		String budget="";
		HashMap heads	=new HashMap();
		HashMap subHeads=new HashMap();
		Set headsSet;
		Iterator headsIterator;
		Set subHeadsSet;
		Iterator subHeadsIterator;


		heads=(HashMap) ifForm.getHeads();
		HashMap subHeads1=new HashMap();
		Set subHeadsSet1;
		Iterator subHeadsIterator1;
		subHeads1=(HashMap) ifForm.getSubHeads();
		subHeadsSet1=subHeads1.keySet();
		subHeadsIterator1=subHeadsSet1.iterator();

		while(subHeadsIterator1.hasNext())
		{
			String key=(String)subHeadsIterator1.next();
			String value=(String)subHeads1.get(key);
			Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","key, value "+key+", "+value);
			int index=key.indexOf("_");

			if(index==-1)
			{
				continue;
			}
			Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","index "+index);
			String key1=key.substring(0,index);
			Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","key, value "+key1);
			heads.put(key1,"");
		}

		subHeadsIterator1=subHeadsSet1.iterator();

		headsSet=heads.keySet();
		headsIterator=headsSet.iterator();

		HashMap headsToRender	=new HashMap();
		HashMap subHeadsToRender=new HashMap();
		Set subHeadsSetToRender;
		Iterator subHeadsIteratorToRender;
		headsToRender=(HashMap) ifForm.getHeadsToRender();

		String headTitle			="";
		ActualInflowOutflowDetails actualInflowOutflowDetails = new ActualInflowOutflowDetails();
		ArrayList aActualIOHeadDetails= new ArrayList();

		String inflowOrOutflow	= null;
		String term				= null;
		String dateOfFlow		= null;
		String month			= null;
		String year				= null;
		String modifiedByUser	= null;

		inflowOrOutflow	=	(String)ifForm.getInflowOrOutflow();
		term			=	(String)ifForm.getAnnualOrShortTerm();
		dateOfFlow		=	(String)ifForm.getDateOfFlow();
		month			=	(String)ifForm.getMonth();
		year			=	(String)ifForm.getYear();

		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();
		//modifiedByUser	=	"CGTSI001";//"PVAIDY01";

		//String headsAndVals = (String)ifForm.getSubHeadsRam();
		Log.log(Log.INFO,"IFAction","saveFundsInflowOutflowDetails","Entered");

		Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","inflowOrOutflow = "+inflowOrOutflow);
		Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","term = "+term);
		Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","dateOfFlow= "+dateOfFlow);
		//Log.log(Log.DEBUG,"IFAction","setAnnualFundsInflowDetails","Heads and Val = "+headsAndVals);
		actualInflowOutflowDetails.setIsInflowOrOutflow(inflowOrOutflow);
		actualInflowOutflowDetails.setIsAnnualOrShortTerm(term);
		actualInflowOutflowDetails.setDateOfFlow(dateOfFlow);
		//actualInflowOutflowDetails.setMonth(month);
		//actualInflowOutflowDetails.setYear(year);
		actualInflowOutflowDetails.setModifiedBy(loggedUserId);
		//actualInflowOutflowDetails.setModifiedBy(modifiedByUser);
		String budgetAmount =null;

		while (headsIterator.hasNext())
		{
			ActualIOHeadDetail actualIOHeadDetails = new ActualIOHeadDetail();
			headTitle=(String)headsIterator.next();

			Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","headTitle= "+headTitle);

			if (heads.get(headTitle) != null)
			{
				actualIOHeadDetails.setBudgetHead(headTitle);// the head budget amt to be captured if thr is no subheads
				ArrayList aActualIOSubHeadDetails = null;
				subHeadsToRender=(HashMap) headsToRender.get(headTitle);

				budgetAmount =(String)heads.get(headTitle);

				if(budgetAmount ==null || budgetAmount.equals("") )
				{
					budgetAmount="0.0";
				}

				Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","budget amount = "+budgetAmount);

				actualIOHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));

				if(subHeadsToRender != null)
				{
					Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","sub-heads available ");

					aActualIOSubHeadDetails = new ArrayList();
					subHeadsSetToRender=subHeadsToRender.keySet();
					subHeadsIteratorToRender=subHeadsSetToRender.iterator();
					String subHeadTitle="";

					while (subHeadsIteratorToRender.hasNext())
					{
						ActualIOSubHeadDetail actualIOSubHeadDetails = new ActualIOSubHeadDetail();
						subHeadTitle=subHeadsIteratorToRender.next().toString();
						actualIOSubHeadDetails.setSubHeadTitle(subHeadTitle);
						budgetAmount = (String)subHeads1.get(headTitle+"_"+subHeadTitle);

						if (budgetAmount == null || budgetAmount.equals(""))
						{
							 budgetAmount = "0.0";
						}
						actualIOSubHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));
						aActualIOSubHeadDetails.add(actualIOSubHeadDetails);
						actualIOSubHeadDetails = null;

						Log.log(Log.DEBUG,"IFAction","saveFundsInflowOutflowDetails","subHeadTitle,budgetAmount "+subHeadTitle+", "+budgetAmount);
					}
				}
				actualIOHeadDetails.setActualIOSubHeadDetails(aActualIOSubHeadDetails);
			}
			else
			{
				Log.log(Log.INFO,"IFAction","saveFundsInflowOutflowDetails","No head details ");
			}
			aActualIOHeadDetails.add(actualIOHeadDetails);
		}
		actualInflowOutflowDetails.setActualIOHeadDetails(aActualIOHeadDetails);
		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.saveActualInflowOutflowDetails(actualInflowOutflowDetails);

		String message="Outflow details saved Successfully";

		if(inflowOrOutflow.equals(InvestmentFundConstants.INFLOW))
		{
			message="Inflow details saved Successfully";
		}

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","saveFundsInflowOutflowDetails","Exited");

	}
	public ActionForward setAnnualFundsOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","setAnnualFundsOutflowDetails","Entered");
		InvestmentForm ifForm=(InvestmentForm) form;

		saveFundsInflowOutflowDetails(ifForm,request);

		/*
		String budget="";
		HashMap heads	=new HashMap();
		HashMap subHeads=new HashMap();
		Set headsSet;
		Iterator headsIterator;
		Set subHeadsSet;
		Iterator subHeadsIterator;
		InvestmentForm ifForm=(InvestmentForm) form;
			heads=(HashMap) ifForm.getHeads();
			headsSet=heads.keySet();
			headsIterator=headsSet.iterator();
			HashMap subHeads1=new HashMap();
			Set subHeadsSet1;
			Iterator subHeadsIterator1;
			subHeads1=(HashMap) ifForm.getSubHeads();
			subHeadsSet1=subHeads1.keySet();
			subHeadsIterator1=subHeadsSet1.iterator();

			HashMap headsToRender	=new HashMap();
			HashMap subHeadsToRender=new HashMap();
			Set subHeadsSetToRender;
			Iterator subHeadsIteratorToRender;
			headsToRender=(HashMap) ifForm.getHeadsToRender();

			String headTitle			="";
			ActualInflowOutflowDetails actualInflowOutflowDetails = new ActualInflowOutflowDetails();
			ArrayList aActualIOHeadDetails= new ArrayList();
			String inflowOrOutflow	= null;
			String term				= null;
			String dateOfFlow		= null;
			String month			= null;
			String year				= null;
			String modifiedByUser	= null;

			inflowOrOutflow	=	(String)ifForm.getInflowOrOutflow();
			term			=	(String)ifForm.getAnnualOrShortTerm();
			dateOfFlow		=	(String)ifForm.getDateOfFlow();
			month			=	(String)ifForm.getMonth();
			year			=	(String)ifForm.getYear();
			//Get the logged in user's userId.
			User loggedInUser=getUserInformation(request);
			String loggedUserId=loggedInUser.getUserId();
			//modifiedByUser	=	"CGTSI001";//"PVAIDY01";

			String headsAndVals = (String)ifForm.getSubHeadsRam();
			Log.log(Log.INFO,"IFAction","setAnnualFundsOutflowDetails","Entered");
			Log.log(Log.WARNING,"IFAction","setAnnualFundsOutflowDetails","warning");
			Log.log(Log.ERROR,"IFAction","setAnnualFundsOutflowDetails","errror");

			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","inflowOrOutflow = "+inflowOrOutflow);
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","loggerd man");
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","term = "+term);
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","dateOfFlow= "+dateOfFlow);
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","month = "+month);
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","year = "+year);
			Log.log(Log.DEBUG,"IFAction","setAnnualFundsOutflowDetails","Heads and Val = "+headsAndVals);
			actualInflowOutflowDetails.setIsInflowOrOutflow(inflowOrOutflow);
			actualInflowOutflowDetails.setIsAnnualOrShortTerm(term);
			actualInflowOutflowDetails.setDateOfFlow(dateOfFlow);
			actualInflowOutflowDetails.setMonth(month);
			actualInflowOutflowDetails.setYear(year);
			actualInflowOutflowDetails.setModifiedBy(loggedUserId);
			//actualInflowOutflowDetails.setModifiedBy(modifiedByUser);

			while (headsIterator.hasNext())
				{
				ActualIOHeadDetail actualIOHeadDetails = new ActualIOHeadDetail();
				headTitle=(String)headsIterator.next();
				if (heads.get(headTitle) != null)
				{
					actualIOHeadDetails.setBudgetHead(headTitle);// the head budget amt to be captured if thr is no subheads
					ArrayList aActualIOSubHeadDetails = null;
					subHeadsToRender=(HashMap) headsToRender.get(headTitle);
					if(subHeadsToRender != null)
					{
						aActualIOSubHeadDetails = new ArrayList();
						subHeadsSetToRender=subHeadsToRender.keySet();
						subHeadsIteratorToRender=subHeadsSetToRender.iterator();
						String subHeadTitle="";
						while (subHeadsIteratorToRender.hasNext())
						{
							ActualIOSubHeadDetail actualIOSubHeadDetails = new ActualIOSubHeadDetail();
							subHeadTitle=subHeadsIteratorToRender.next().toString();
							actualIOSubHeadDetails.setSubHeadTitle(subHeadTitle);
							String budgetAmount = (String)subHeads1.get(headTitle+"_"+subHeadTitle);
							if (budgetAmount == null) budgetAmount = "0.0";
							actualIOSubHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));
							aActualIOSubHeadDetails.add(actualIOSubHeadDetails);
							actualIOSubHeadDetails = null;
						}
					}
					actualIOHeadDetails.setActualIOSubHeadDetails(aActualIOSubHeadDetails);
				}
				else{
					Log.log(Log.INFO,"IFAction","setAnnualFundsOutflowDetails","No head details ");
				}
				aActualIOHeadDetails.add(actualIOHeadDetails);
			}
			actualInflowOutflowDetails.setActualIOHeadDetails(aActualIOHeadDetails);
			IFProcessor ifProcessor=new IFProcessor();
			ifProcessor.saveActualInflowOutflowDetails(actualInflowOutflowDetails);

			*/
			String message="Outflow details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","setAnnualFundsOutflowDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	private void getInvestmentForm(InvestmentForm form, String ioFlag) throws DatabaseException, MessageException
	{
		IFProcessor ifProcessor=new IFProcessor();
		Vector budgetHeads=new Vector();
		Vector budgetSubHeads=new Vector();
		String headTitle="";
		int noOfHeads=0;
		int noOfSubHeads=0;
		int i=0;
		int j=0;

		budgetHeads=ifProcessor.getBudgetHeadTitles(ioFlag);
		noOfHeads=budgetHeads.size();

		Log.log(Log.DEBUG,"IFAction","getInvestmentForm","No of Heads " + noOfHeads);

		if (noOfHeads==0)
		{
			throw new MessageException("Budget Heads are not available.");
		}

		for (i=0;i<noOfHeads;i++)
		{
			headTitle=budgetHeads.get(i).toString();
			Log.log(Log.DEBUG,"IFAction","getInvestmentForm","Head: " + headTitle);
			budgetSubHeads=ifProcessor.getBudgetSubHeadTitles(headTitle,ioFlag);
			noOfSubHeads=budgetSubHeads.size();
			if (noOfSubHeads==0)
			{
				form.setHeadsToRender(headTitle, null);
				Log.log(Log.INFO,"IFAction","getInvestmentForm","No Sub Heads");
			}
			else
			{
				Map subHeadMap=new HashMap();
				Log.log(Log.INFO,"IFAction","getInvestmentForm","Has Sub Heads");
				for (j=0;j<noOfSubHeads;j++)
				{
					String s=budgetSubHeads.get(j).toString();
					form.setSubHeadsToRender(s, null);
					subHeadMap.put(s,null);
				}

				Log.log(Log.DEBUG,"IFAction","getInvestmentForm","Sub Head Size " + subHeadMap.size());

				form.setHeadsToRender(headTitle, subHeadMap);
			}
		}
	}


	private void getAllInvestees(DynaActionForm form)  throws DatabaseException
	{
		ArrayList investeeNames1=new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		investeeNames1=ifProcessor.getAllInvesteeNames();
		form.set("investeeNames",investeeNames1);

	}
	private void getAllInvesteeNamesForGroup(DynaActionForm form)  throws DatabaseException
	{
		ArrayList investeeNames1=new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		investeeNames1=ifProcessor.getAllInvesteeNamesForGroup((String)form.get("investeeGroup"));
		form.set("investeeNames",investeeNames1);

	}

	private void getAllInvesteeGroups(DynaActionForm form)  throws DatabaseException
	{
		ArrayList investeeGroups=null;
		IFProcessor ifProcessor=new IFProcessor();
		investeeGroups=ifProcessor.getInvesteeGroups();
		form.set("investeeGroups",investeeGroups);

	}
	private void getInstrumentTypes(DynaActionForm form) throws DatabaseException
	{
		ArrayList instrtypes=new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
//		instrtypes=ifProcessor.getInstrumentTypes(InvestmentFundConstants.GENERAL_INVESTMENT_TYPE);

//		form.set("instrumentTypes",instrtypes);

		instrtypes=ifProcessor.getInstrumentTypes(InvestmentFundConstants.IF_INVESTMENT_TYPE);
//		form.set("instrumentTypes",instrtypes);

		form.set("instrumentNames",instrtypes);

	}

	private void getGenInstrumentTypes(DynaActionForm form) throws DatabaseException
	{
		ArrayList instrtypes=new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		instrtypes=ifProcessor.getInstrumentTypes(InvestmentFundConstants.GENERAL_INVESTMENT_TYPE);

		form.set("instrumentTypes",instrtypes);

//		instrtypes=ifProcessor.getInstrumentTypes(InvestmentFundConstants.IF_INVESTMENT_TYPE);
//		form.set("instrumentTypes",instrtypes);

//		form.set("instrumentNames",instrtypes);

	}

	private void getAllRatings(DynaActionForm form) throws DatabaseException
	{
		ArrayList ratings =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		ratings =ifProcessor.getAllRatings();
		form.set("instrumentRatings",ratings);
	}

	private void getBudgetHeadTypes(DynaActionForm form) throws DatabaseException
	{
		ArrayList budgetHeads =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		String headType=(String)form.get("budgetHeadType");

		budgetHeads =ifProcessor.getBudgetHeadTypes(headType);
		form.set("budgetHeadsList",budgetHeads);
	}

	private void getInstrumentSchemeTypes(DynaActionForm form) throws DatabaseException
	{
		ArrayList instrumentSchemeTypes =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		String inst = (String)form.get("instrument");
		instrumentSchemeTypes  =ifProcessor.getInstrumentSchemeTypes(inst);
		form.set("instrumentSchemeTypes",instrumentSchemeTypes );
	}

	private void getInstrumentFeatures(DynaActionForm form) throws DatabaseException
	{
		ArrayList instrumentFeatures =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		instrumentFeatures  =ifProcessor.getInstrumentFeatures();
		form.set("instrumentFeaturesList",instrumentFeatures );
	}

	private void getReceiptNumbers(DynaActionForm form) throws DatabaseException
	{
		ArrayList receiptNos =new ArrayList();
		IFProcessor ifProcessor=new IFProcessor();
		receiptNos  =ifProcessor.getReceiptNumbers();
		form.set("receiptNumbers",receiptNos);
	}

	private void getAvailableBalance(DynaActionForm form,String acctNo) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		//form.set("dClosingBalance",ifProcessor.getAvailableBalance(acctNo));
		form.set("dClosingBalance","12212.0");
	}

	private void getMonthlyExpense(DynaActionForm form,String acctNo,String frmDate,String toDate)
					throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		form.set("dExpensesForTheMonth",ifProcessor.getMonthlyExpense(acctNo,frmDate,toDate));
	}

	private void getTodayExpense(DynaActionForm form,String acctNo) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		form.set("dCreditPendingForDay",ifProcessor.getTodayExpense(acctNo));
	}
	private void getInstrumentSchemes(DynaActionForm form) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		ArrayList instrumentSchemes=ifProcessor.getInstrumentSchemes();

		form.set("instrumentSchemes",instrumentSchemes);
	}
	private void getInvestmentReferenceNumbers(DynaActionForm dynaForm) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		String inst = (String) dynaForm.get("instrumentName");
		ArrayList investmentRefNumbers=ifProcessor.getInvestmentRefNumbers(inst);

		dynaForm.set("investmentRefNumbers",investmentRefNumbers);
	}


	public ActionForward saveInvesteeGroup(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","Entered");
			DynaActionForm dynaForm=(DynaActionForm)form;
			User user=getUserInformation(request);
			String investeeGroup=(String)dynaForm.get("investeeGroup");
			String newInvesteeGroup=(String)dynaForm.get("newInvesteeGrp");
			String modInvesteeGroup=(String)dynaForm.get("modInvesteeGroup");
			InvesteeGrpDetail invGrp = new InvesteeGrpDetail();
			invGrp.setSIGRName(investeeGroup);
			invGrp.setSNewIGRName(newInvesteeGroup);
			invGrp.setSModIGRName(modInvesteeGroup);
			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","investee grp " + invGrp.getSIGRName());
			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","new investee grp " + invGrp.getSNewIGRName());
			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","mod investee grp " + invGrp.getSModIGRName());
			IFProcessor ifProcessor=new IFProcessor();

			ifProcessor.saveInvesteeGroup(invGrp,user.getUserId());

			String message="Investee Group details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","Investee Group name "+investeeGroup);
			Log.log(Log.INFO,"IFAction","saveInvesteeGroup","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getAnnualBudgetInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","getAnnualInflowDetails","Entered");
			InvestmentForm ifForm=(InvestmentForm)form;

			getBudgetInflowOutflowDetails(ifForm,InvestmentFundConstants.INFLOW);

			Log.log(Log.INFO,"IFAction","getAnnualInflowDetails","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getAnnualBudgetOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","getAnnualOutflowDetails","Entered");
			InvestmentForm ifForm=(InvestmentForm)form;

			getBudgetInflowOutflowDetails(ifForm,InvestmentFundConstants.OUTFLOW);

			Log.log(Log.INFO,"IFAction","getAnnualOutflowDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

		private void getBudgetInflowOutflowDetails(InvestmentForm ifForm,String flag) throws DatabaseException
		{
			Log.log(Log.INFO,"IFAction","getBudgetInflowOutflowDetails","Entered ");

			String fromDate=(String)ifForm.getAnnualFromDate();
			String toDate=(String)ifForm.getAnnualToDate();

			ifForm.getHeads().clear();
			ifForm.getSubHeads().clear();

			Log.log(Log.DEBUG,"IFAction","getBudgetInflowOutflowDetails","fromDate "+fromDate);
			Log.log(Log.DEBUG,"IFAction","getAnnuagetBudgetInflowOutflowDetailslInflowDetails","toDate "+toDate);

			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

			Date annualFromDate=dateFormat.parse(fromDate,new ParsePosition(0));
			Date annualToDate=dateFormat.parse(toDate,new ParsePosition(0));

			Log.log(Log.DEBUG,"IFAction","getBudgetInflowOutflowDetails","annualFromDate "+annualFromDate);
			Log.log(Log.DEBUG,"IFAction","getBudgetInflowOutflowDetails","annualToDate "+annualToDate);

			IFProcessor ifProcessor=new IFProcessor();

			HashMap headDetails=ifProcessor.getAnnualHeadDetails(annualFromDate,annualToDate,flag);

			Set headSet=headDetails.keySet();

			Iterator headIterator=headSet.iterator();

			Vector budgetHeads=ifProcessor.getBudgetHeadTitles(flag);

			for(int i=0;i<budgetHeads.size();i++)
			{
				String head=(String)budgetHeads.get(i);

				HashMap subHeadDetails=ifProcessor.getAnnualSubHeadDetails(
				annualFromDate,annualToDate,head,flag);

				if(subHeadDetails!=null && subHeadDetails.size()!=0)
				{
					Log.log(Log.DEBUG,"IFAction","getBudgetInflowOutflowDetails","Sub-Heads are available ");

					Set subHeadSet=subHeadDetails.keySet();
					Iterator subHeadIterator=subHeadSet.iterator();

					while(subHeadIterator.hasNext())
					{
						String subHeadKey=(String)subHeadIterator.next();

						Log.log(Log.DEBUG,"IFAction","getBudgetInflowOutflowDetails","Sub-Heads Key "+subHeadKey);

						ifForm.setSubHead(subHeadKey,subHeadDetails.get(subHeadKey));
					}
				}
			}

			while(headIterator.hasNext())
			{
				String key=(String)headIterator.next();
				ifForm.setHead(key,headDetails.get(key));
			}

			Log.log(Log.INFO,"IFAction","getBudgetInflowOutflowDetails","Exited ");
		}

	private void getShortInflowOutflowDetails(InvestmentForm ifForm,String flag) throws DatabaseException
	{
		Log.log(Log.INFO,"IFAction","getShortInflowOutflowDetails","Entered ");

		String year=ifForm.getYear();
		String month=ifForm.getMonth();


		Log.log(Log.DEBUG,"IFAction","getShortInflowOutflowDetails","year "+year);
		Log.log(Log.DEBUG,"IFAction","getShortInflowOutflowDetails","month "+month);


		IFProcessor ifProcessor=new IFProcessor();

		HashMap headDetails=ifProcessor.getShortHeadDetails(year,month,flag);

		Set headSet=headDetails.keySet();

		Iterator headIterator=headSet.iterator();

		Vector budgetHeads=ifProcessor.getBudgetHeadTitles(flag);

		for(int i=0;i<budgetHeads.size();i++)
		{
			String head=(String)budgetHeads.get(i);

			HashMap subHeadDetails=ifProcessor.getShortSubHeadDetails(
			year,month,head,flag);

			if(subHeadDetails!=null && subHeadDetails.size()!=0)
			{
				Log.log(Log.DEBUG,"IFAction","getShortInflowOutflowDetails","Sub-Heads are available ");

				Set subHeadSet=subHeadDetails.keySet();
				Iterator subHeadIterator=subHeadSet.iterator();

				while(subHeadIterator.hasNext())
				{
					String subHeadKey=(String)subHeadIterator.next();

					Log.log(Log.DEBUG,"IFAction","getShortInflowOutflowDetails","Sub-Heads Key,amount "+subHeadKey+", "+subHeadDetails.get(subHeadKey));

					ifForm.setShortSubHead(subHeadKey,subHeadDetails.get(subHeadKey));
				}
			}
		}

		while(headIterator.hasNext())
		{
			String key=(String)headIterator.next();
			ifForm.setShortHead(key,headDetails.get(key));
		}

		Log.log(Log.INFO,"IFAction","getShortInflowOutflowDetails","Exited ");
	}

	public ActionForward getShortTermInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","getShortTermInflowDetails","Entered ");

		InvestmentForm investmentForm=(InvestmentForm)form;

		String year=investmentForm.getYear();
		String month=investmentForm.getMonth();


		Map headsToRender=investmentForm.getHeadsToRender();

		//copy the heads into temp Map so that when the headsToRender is cleaned up
		// the contents of the map is available.
		Map tempMap=new HashMap(headsToRender);

		investmentForm.resetWhenRequired(mapping,request);

		Set headsSet=tempMap.keySet();
		Iterator headsIterator=headsSet.iterator();

		while(headsIterator.hasNext())
		{
			String key=(String)headsIterator.next();

			investmentForm.setHeadsToRender(key,tempMap.get(key));
		}


		int intYear=Integer.parseInt(year);
		intYear++;

		String annualFromDate="01/01/"+year;
		String annualToDate="01/01/"+intYear;

		investmentForm.setAnnualFromDate(annualFromDate);
		investmentForm.setAnnualToDate(annualToDate);
		investmentForm.setYear(year);
		investmentForm.setMonth(month);

		//Get the annual budget details.
		getBudgetInflowOutflowDetails(investmentForm,InvestmentFundConstants.INFLOW);

		investmentForm.getShortHeads().clear();
		investmentForm.getShortSubHeads().clear();

		getShortInflowOutflowDetails(investmentForm,InvestmentFundConstants.INFLOW);
		//Get the short term budget details also.

		Log.log(Log.INFO,"IFAction","getShortTermInflowDetails","Exited ");

		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward getShortTermOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","getShortTermOutflowDetails","Entered ");

		InvestmentForm investmentForm=(InvestmentForm)form;

		String year=investmentForm.getYear();
		String month=investmentForm.getMonth();


		Map headsToRender=investmentForm.getHeadsToRender();

		//copy the heads into temp Map so that when the headsToRender is cleaned up
		// the contents of the map is available.
		Map tempMap=new HashMap(headsToRender);

		investmentForm.resetWhenRequired(mapping,request);

		Set headsSet=tempMap.keySet();
		Iterator headsIterator=headsSet.iterator();

		while(headsIterator.hasNext())
		{
			String key=(String)headsIterator.next();

			investmentForm.setHeadsToRender(key,tempMap.get(key));
		}


		int intYear=Integer.parseInt(year);
		intYear++;

		String annualFromDate="01/01/"+year;
		String annualToDate="01/01/"+intYear;

		investmentForm.setAnnualFromDate(annualFromDate);
		investmentForm.setAnnualToDate(annualToDate);
		investmentForm.setYear(year);
		investmentForm.setMonth(month);

		//Get the annual budget details.
		getBudgetInflowOutflowDetails(investmentForm,InvestmentFundConstants.OUTFLOW);

		investmentForm.getShortHeads().clear();
		investmentForm.getShortSubHeads().clear();

		getShortInflowOutflowDetails(investmentForm,InvestmentFundConstants.OUTFLOW);
		//Get the short term budget details also.

		Log.log(Log.INFO,"IFAction","getShortTermOutflowDetails","Exited ");

		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward setShortTermBudgetInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","setShortTermBudgetInflowDetails","Entered ");

		InvestmentForm ifForm=(InvestmentForm)form;

		saveShortTermBudgetDetails(ifForm,InvestmentFundConstants.INFLOW,request);

		String message="Short Term Budget Inflow details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","setShortTermBudgetInflowDetails","Exited ");

		return mapping.findForward(Constants.SUCCESS);
	}
	public ActionForward setShortTermBudgetOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","setShortTermBudgetOutflowDetails","Entered ");

		InvestmentForm ifForm=(InvestmentForm)form;

		saveShortTermBudgetDetails(ifForm,InvestmentFundConstants.OUTFLOW,request);

		String message="Short Term Budget Outflow details saved Successfully";

		request.setAttribute("message",message);


		Log.log(Log.INFO,"IFAction","setShortTermBudgetOutflowDetails","Exited ");

		return mapping.findForward(Constants.SUCCESS);
	}

	private void saveShortTermBudgetDetails(InvestmentForm ifForm,
			String flag,HttpServletRequest request) throws DatabaseException
	{
		String budget="";
		HashMap heads	=null;
		HashMap subHeads=null;
		Set headsSet;
		Iterator headsIterator;
		Set subHeadsSet;
		Iterator subHeadsIterator;

		heads=(HashMap) ifForm.getShortHeads();

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","heads "+heads);

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","Budget Type "+ifForm.getAnnualOrShortTerm());

		HashMap subHeads1=null;
		Set subHeadsSet1;
		Iterator subHeadsIterator1;
		subHeads1=(HashMap) ifForm.getShortSubHeads();

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","subHeads1 "+subHeads1);

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","short sub heads "+ifForm.getShortSubHeads());

		subHeadsSet1=subHeads1.keySet();
		subHeadsIterator1=subHeadsSet1.iterator();

		while(subHeadsIterator1.hasNext())
		{
			String key=(String)subHeadsIterator1.next();
			String value=(String)subHeads1.get(key);
			Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","key, value "+key+", "+value);
			int index=key.indexOf("_");

			if(index==-1)
			{
				continue;
			}
			Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","index "+index);
			String key1=key.substring(0,index);
			Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","key, value "+key1);
			heads.put(key1,"");
		}
		headsSet=heads.keySet();
		headsIterator=headsSet.iterator();

		subHeads1=(HashMap) ifForm.getShortSubHeads();
		subHeadsSet1=subHeads1.keySet();
		subHeadsIterator1=subHeadsSet1.iterator();

		subHeadsIterator1=subHeadsSet1.iterator();

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","sub-heads = "+subHeads1);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","sub-headsSet = "+subHeadsSet1);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","sub-headsIterator = "+subHeadsIterator1);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","heads = "+heads);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","headsSet = "+headsSet);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","headsIterator = "+headsIterator);


		HashMap headsToRender	=new HashMap();
		HashMap subHeadsToRender=new HashMap();
		Set subHeadsSetToRender;
		Iterator subHeadsIteratorToRender;


		headsToRender=(HashMap) ifForm.getHeadsToRender();

		String headTitle			="";
		BudgetDetails budgetDetails = new BudgetDetails();
		ArrayList aBudgetHeadDetails= new ArrayList();

		String inflowOrOutflow	= null;
		String term				= null;
		String annualToDate		= null;
		String annualFromDate	= null;
		String month			= null;
		String year				= null;
		String modifiedByUser	= null;

		inflowOrOutflow	=	(String)ifForm.getInflowOrOutflow();
		term			=	(String)ifForm.getAnnualOrShortTerm();
		annualToDate	=	(String)ifForm.getAnnualToDate();
		annualFromDate	=	(String)ifForm.getAnnualFromDate();
		month			=	(String)ifForm.getMonth();
		year			=	(String)ifForm.getYear();
		//Get the logged in user's userId.
		User loggedInUser=getUserInformation(request);
		String loggedUserId=loggedInUser.getUserId();

		//String headsAndVals = (String)ifForm.getSubHeadsRam();

		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","inflowOrOutflow = "+inflowOrOutflow);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","loggerd man");
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","term = "+term);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","annualToDate = "+annualToDate);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","annualFromDate = "+annualFromDate);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","month = "+month);
		Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","year = "+year);
		//Log.log(Log.DEBUG,"IFAction","setShortTermBudgetInflowDetails","Heads and Val = "+headsAndVals);
		budgetDetails.setAnnualOrShortTerm(term);
		budgetDetails.setAnnualToDate(annualToDate);
		budgetDetails.setAnnualFromDate(annualFromDate);
		budgetDetails.setMonth(month);
		budgetDetails.setYear(year);
		budgetDetails.setModifiedBy(loggedUserId);
		budgetDetails.setInflowOrOutflow(inflowOrOutflow);

		while (headsIterator.hasNext())
		{
			BudgetHeadDetails budgetHeadDetails = new BudgetHeadDetails();
			headTitle=(String)headsIterator.next();

			Log.log(Log.INFO,"IFAction","saveShortTermBudgetDetails","headTitle "+headTitle);

			if (heads.get(headTitle) != null)
			{
				budgetHeadDetails.setBudgetHead(headTitle);
				ArrayList aBudgetSubHeadDetails = null;
				subHeadsToRender=(HashMap) headsToRender.get(headTitle);
				String budgetAmount = (String)heads.get(headTitle);

				if (budgetAmount == null || budgetAmount.equals(""))
				{
					 budgetAmount = "0.0";
				}

				Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","budgetAmount "+budgetAmount);

				budgetHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));

				Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","subHeadsToRender "+subHeadsToRender);

				if(subHeadsToRender != null)
				{
					aBudgetSubHeadDetails = new ArrayList();
					subHeadsSetToRender=subHeadsToRender.keySet();
					subHeadsIteratorToRender=subHeadsSetToRender.iterator();
					String subHeadTitle="";
					while (subHeadsIteratorToRender.hasNext())
					{
						BudgetSubHeadDetails budgetSubHeadDetails = new BudgetSubHeadDetails();
						subHeadTitle=subHeadsIteratorToRender.next().toString();
						budgetSubHeadDetails.setSubHeadTitle(subHeadTitle);

						Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","subHeadTitle "+subHeadTitle);

						budgetAmount = (String)subHeads1.get(headTitle+"_"+subHeadTitle);

						if (budgetAmount == null || budgetAmount.equals(""))
						{
							 budgetAmount = "0.0";
						}

						Log.log(Log.DEBUG,"IFAction","saveShortTermBudgetDetails","budgetAmount "+budgetAmount);

						budgetSubHeadDetails.setBudgetAmount(Double.parseDouble(budgetAmount));
						aBudgetSubHeadDetails.add(budgetSubHeadDetails);
						budgetSubHeadDetails = null;
					}
				}
				budgetHeadDetails.setBudgetSubHeadDetails(aBudgetSubHeadDetails);
			}
			else
			{
				Log.log(Log.INFO,"IFAction","saveShortTermBudgetDetails","No head details ");
			}
			aBudgetHeadDetails.add(budgetHeadDetails);
		}
		budgetDetails.setBudgetHeadDetails(aBudgetHeadDetails);
		budgetDetails.setAnnualOrShortTerm(InvestmentFundConstants.SHORT_TERM);

		IFProcessor ifProcessor=new IFProcessor();
		ifProcessor.saveBudgetDetails(budgetDetails);

		String message="Short Term Budget details saved Successfully";

		request.setAttribute("message",message);

		Log.log(Log.INFO,"IFAction","saveShortTermBudgetDetails","Exited");

	}
	public ActionForward getFundsInflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","getFundsInflowDetails","Entered");

		InvestmentForm investmentForm=(InvestmentForm)form;

		investmentForm.getHeads().clear();
		investmentForm.getSubHeads().clear();

		getInflowOutflowDetails(investmentForm, InvestmentFundConstants.INFLOW);

		Log.log(Log.INFO,"IFAction","getFundsInflowDetails","Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward getFundsOutflowDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		Log.log(Log.INFO,"IFAction","getFundsOutflowDetails","Entered");

		InvestmentForm investmentForm=(InvestmentForm)form;

		investmentForm.getHeads().clear();
		investmentForm.getSubHeads().clear();

		getInflowOutflowDetails(investmentForm, InvestmentFundConstants.OUTFLOW);

		Log.log(Log.INFO,"IFAction","getFundsOutflowDetails","Exited");

		return mapping.findForward(Constants.SUCCESS);
	}
	private void getInflowOutflowDetails(InvestmentForm ifForm,String flag) throws DatabaseException
	{
		Log.log(Log.INFO,"IFAction","getInflowOutflowDetails","Entered ");

		String dateOfFlow=(String)ifForm.getDateOfFlow();

		Log.log(Log.DEBUG,"IFAction","getInflowOutflowDetails","dateOfFlow "+dateOfFlow);


		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

		Date convertedDateOfFlow=dateFormat.parse(dateOfFlow,new ParsePosition(0));


		Log.log(Log.DEBUG,"IFAction","getInflowOutflowDetails","conveted dateOfFlow "+convertedDateOfFlow);

		IFProcessor ifProcessor=new IFProcessor();

		HashMap headDetails=ifProcessor.getInflowOutflowHeadDetails(convertedDateOfFlow,flag);

		Set headSet=headDetails.keySet();

		Iterator headIterator=headSet.iterator();

		Vector budgetHeads=ifProcessor.getBudgetHeadTitles(flag);

		for(int i=0;i<budgetHeads.size();i++)
		{
			String head=(String)budgetHeads.get(i);

			HashMap subHeadDetails=ifProcessor.getInflowOutflowSubHeadDetails(
			head,convertedDateOfFlow,flag);

			if(subHeadDetails!=null && subHeadDetails.size()!=0)
			{
				Log.log(Log.DEBUG,"IFAction","getInflowOutflowDetails","Sub-Heads are available ");

				Set subHeadSet=subHeadDetails.keySet();
				Iterator subHeadIterator=subHeadSet.iterator();

				while(subHeadIterator.hasNext())
				{
					String subHeadKey=(String)subHeadIterator.next();

					Log.log(Log.DEBUG,"IFAction","getInflowOutflowDetails","Sub-Heads Key "+subHeadKey);

					ifForm.setSubHead(subHeadKey,subHeadDetails.get(subHeadKey));
				}
			}
		}

		while(headIterator.hasNext())
		{
			String key=(String)headIterator.next();
			ifForm.setHead(key,headDetails.get(key));
		}

		Log.log(Log.INFO,"IFAction","getInflowOutflowDetails","Exited ");
	}
	public ActionForward updateBankAccountMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","updateBankAccountMaster","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			BankAccountDetail accountDetail=new BankAccountDetail();

			User user=getUserInformation(request);
			String userId=user.getUserId();

			IFProcessor ifProcessor=new IFProcessor();

			String accChosen=(String) dynaForm.get("accComb");
			BeanUtils.populate(accountDetail, dynaForm.getMap());
			
			String idbi = "IDBI BANK";
			Log.log(Log.DEBUG,"IFAction","updateBankAccountMaster","acc chosen "+accChosen);

			if (!accChosen.equals(""))
			{
				accountDetail.setModAccountNumber(accountDetail.getAccountNumber());
				accountDetail.setModBankBranchName(accountDetail.getBankBranchName());
				accountDetail.setModBankName(accountDetail.getBankName());
				int index=accChosen.indexOf(",");
				String bankName=(accChosen.substring(0,index)).trim();
				int index1=accChosen.indexOf("(");
				String branchName=(accChosen.substring((index+1), index1)).trim();
				index=accChosen.indexOf(")");
				String accNumber=(accChosen.substring((index1+1), index)).trim();
				accountDetail.setAccountNumber(accNumber);
				accountDetail.setBankBranchName(branchName);
				accountDetail.setBankName(bankName);
			}
			else
			{
				ArrayList names = (ArrayList)dynaForm.get("bankNames");
				String comb = accountDetail.getBankName().trim()+" ,"+accountDetail.getBankBranchName().trim()+"("+accountDetail.getAccountNumber().trim()+")";
				for (int i=0;i<names.size();i++)
				{
					Log.log(Log.DEBUG,"IFAction","updateBankAccountMaster","1 "+idbi.indexOf(accountDetail.getBankName().trim().toUpperCase()));
					Log.log(Log.DEBUG,"IFAction","updateBankAccountMaster","2 "+(((String)names.get(i)).toUpperCase()).indexOf(accountDetail.getBankName().trim().toUpperCase()));
					if (idbi.indexOf(accountDetail.getBankName().trim().toUpperCase())>=0 && (((String)names.get(i)).toUpperCase()).indexOf(accountDetail.getBankName().trim().toUpperCase())>=0)
					{
						throw new MessageException("Bank Account Already Exists.");
					}
					if (((String)names.get(i)).equalsIgnoreCase(comb))
					{
						throw new MessageException("Bank Account Already Exists.");
					}
				}
			}

			ifProcessor.addBankAccountDetail(accountDetail,userId);

			String message="Bank Account details saved Successfully";

			request.setAttribute("message",message);
			Log.log(Log.INFO,"IFAction","updateBankAccountMaster","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}
	public ActionForward showStatementDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showStatementDetails","Entered ");

			IFProcessor ifProcessor=new IFProcessor();
			ArrayList bankDetails=ifProcessor.getAllBanksWithAccountNumbers();

			ArrayList bankNames=new ArrayList(bankDetails.size());
			String bankName="";
			for(int i=0;i<bankDetails.size();i++)
			{
				BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
				bankName=bankAccountDetail.getBankName()+
								" ,"+bankAccountDetail.getBankBranchName()+
								"("+bankAccountDetail.getAccountNumber()+")";

				bankNames.add(bankName);
			}
			Log.log(Log.INFO,"IFAction","showStatementDetails","Exited");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			TransactionDetail transactionDetail=new TransactionDetail();
			java.util.Map transactionMap=new TreeMap();

			transactionMap.put("key-0",transactionDetail);

			java.util.Date currDate = new java.util.Date();

			CustomisedDate customToDate=new CustomisedDate();
			customToDate.setDate(currDate);

			dynaForm.set("transactions",transactionMap);

			dynaForm.set("bankNames",bankNames);

			dynaForm.set("statementDate", customToDate);

			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward addMoreTransactionDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","addMoreTransactionDetails","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			TransactionDetail transactionDetail=new TransactionDetail();

			java.util.Map transactions=(Map)dynaForm.get("transactions");

			Set keys=transactions.keySet();

			Iterator iterator=keys.iterator();

			int keyValue=0;

			while(iterator.hasNext())
			{
				String key=(String)iterator.next();

				Log.log(Log.DEBUG,"IFAction","addMoreTransactionDetails","key "+key);

				Log.log(Log.DEBUG,"IFAction","addMoreTransactionDetails","key "+key);

				String substr=key.substring(key.indexOf("-")+1,key.length());

				Log.log(Log.DEBUG,"IFAction","addMoreTransactionDetails","substr "+substr);

				if(keyValue<=Integer.parseInt(substr))
				{
					keyValue=Integer.parseInt(substr);
					keyValue++;
				}
			}

			Log.log(Log.DEBUG,"IFAction","addMoreTransactionDetails","keyValue "+keyValue);

			java.util.Map transactionMap=(java.util.Map)dynaForm.get("transactions");

			transactionMap.put("key-"+keyValue,transactionDetail);

			dynaForm.set("transactions",transactionMap);


			Log.log(Log.INFO,"IFAction","addMoreTransactionDetails","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward updateStatementDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","updateStatementDetails","Entered ");

			IFProcessor ifProcessor=new IFProcessor();

			DynaActionForm dynaForm=(DynaActionForm)form;

			Map transactionDetails=(Map)dynaForm.get("transactions");

			ArrayList transactions=new ArrayList();

			 Set transactionSet=transactionDetails.keySet();

			 Iterator transactionIterator=transactionSet.iterator();

			 while(transactionIterator.hasNext())
			 {
				Object key=transactionIterator.next();
				Log.log(Log.DEBUG,"IFAction","updateStatementDetails","Key "+key);

				TransactionDetail transDetails=(TransactionDetail)transactionDetails.get(key);
				Log.log(Log.DEBUG,"IFAction","updateStatementDetails","Transaction object details " +
				transDetails.getChequeNumber()+" "+
				transDetails.getTransactionDate()+" "+
				transDetails.getValueDate());
				transactions.add(transactionDetails.get(key));

			 }

			StatementDetail statementDetail=new StatementDetail();


			BeanUtils.populate(statementDetail,dynaForm.getMap());

			statementDetail.setTransactionDetail(transactions);

			User user=getUserInformation(request);
			String userId=user.getUserId();
			String bankName=statementDetail.getBankName();

			int index=bankName.indexOf(",");

			int index1=bankName.indexOf("(");

			String actualBankName=bankName.substring(0,index);

			String actualBranchName=bankName.substring((index+1),index1);

			String accountNumber=bankName.substring((index1+1),(bankName.length()-1));

			Log.log(Log.DEBUG,"IFAction","updateStatementDetails","actual bank name, branch name, account numbers" +
				""+index+","+index1+","+actualBankName+","+actualBranchName+","+accountNumber);

			statementDetail.setBankName(actualBankName);
			statementDetail.setBankBranchName(actualBranchName);
			statementDetail.setAccountNumber(accountNumber);


			ifProcessor.addStatementDetail(statementDetail,userId);

			String message="Bank Statement Details stored sucessfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateStatementDetails","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

/*
	public ActionForward showStatementDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showStatementDetails","Entered ");

			IFProcessor ifProcessor=new IFProcessor();
			ArrayList bankDetails=ifProcessor.getAllBanksWithAccountNumbers();

			ArrayList bankNames=new ArrayList(bankDetails.size());
			String bankName="";
			for(int i=0;i<bankDetails.size();i++)
			{
				BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
				bankName=bankAccountDetail.getBankName()+
								" ,"+bankAccountDetail.getBankBranchName()+
								"("+bankAccountDetail.getAccountNumber()+")";

				bankNames.add(bankName);
			}
			Log.log(Log.INFO,"IFAction","showStatementDetails","Exited");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			dynaForm.set("bankNames",bankNames);

			return mapping.findForward(Constants.SUCCESS);
		}
	public ActionForward updateStatementDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","updateStatementDetails","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			StatementDetail statementDetail=new StatementDetail();

			BeanUtils.populate(statementDetail,dynaForm.getMap());

			User user=getUserInformation(request);
			String userId=user.getUserId();
			String bankName=statementDetail.getBankName();

			int index=bankName.indexOf(",");

			int index1=bankName.indexOf("(");

			String actualBankName=bankName.substring(0,index);

			String actualBranchName=bankName.substring((index+1),index1);

			String accountNumber=bankName.substring((index1+1),(bankName.length()-1));

			Log.log(Log.DEBUG,"IFAction","updateStatementDetails","actual bank name, branch name, account numbers" +
				""+index+","+index1+","+actualBankName+","+actualBranchName+","+accountNumber);

			statementDetail.setBankName(actualBankName);
			statementDetail.setBankBranchName(actualBranchName);
			statementDetail.setAccountNumber(accountNumber);

			IFProcessor ifProcessor=new IFProcessor();
			ifProcessor.addStatementDetail(statementDetail,userId);

			String message="Statement details saved Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","updateStatementDetails","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}
*/
	/*
	public ActionForward getAccountNumber(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","getAccountNumber","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			String bankName=(String)dynaForm.get("bankName");

			int index=bankName.indexOf(",");

			int index1=bankName.indexOf("(");

			String actualBankName=bankName.substring(0,index);

			String actualBranchName=bankName.substring(index,index1);

			String accountNumber=bankName.substring(index1,bankName.length());

			Log.log(Log.DEBUG,"IFAction","getAccountNumber","actual bank name, branch name, account numbers" +
				""+index+","+index1+","+actualBankName+","+actualBranchName+","+accountNumber);


			Log.log(Log.INFO,"IFAction","getAccountNumber","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}
	*/
	public ActionForward showMaturityWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showMaturityWiseCeiling","Entered ");

			HttpSession session = request.getSession(false);
			session.setAttribute("ceilingFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			IFProcessor ifProcessor=new IFProcessor();

			ArrayList maturities=ifProcessor.getAllMaturities();
			dynaForm.set("maturities",maturities);

			Log.log(Log.INFO,"IFAction","showMaturityWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward fetchMaturityWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","fetchMaturityWiseCeiling","Entered ");

			HttpSession session = request.getSession(false);
			session.setAttribute("ceilingFlag", "1");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String maturityType = (String)dynaForm.get("maturityType");
			IFProcessor ifProcessor=new IFProcessor();
			MaturityWiseCeiling matWiseCeiling = ifProcessor.getMaturityWiseCeiling(maturityType);
			if (matWiseCeiling!=null)
			{
				BeanUtils.copyProperties(dynaForm,matWiseCeiling);
			}
			else
			{
				dynaForm.set("ceilingStartDate", null);
				dynaForm.set("ceilingEndDate", null);
				dynaForm.set("ceilingLimit", "0.0");
			}
			Log.log(Log.INFO,"IFAction","fetchMaturityWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward setMaturityWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","setMaturityWiseCeiling","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			MaturityWiseCeiling maturityCeiling=new MaturityWiseCeiling();

			BeanUtils.populate(maturityCeiling,dynaForm.getMap());

			Log.log(Log.DEBUG,"IFAction","setMaturityWiseCeiling","end date " + maturityCeiling.getCeilingEndDate());

			IFProcessor ifProcessor=new IFProcessor();

			User user=getUserInformation(request);
			String userId=user.getUserId();

			ifProcessor.addMaturityWiseCeiling(maturityCeiling,userId);

			String message="Maturity wise ceiling set Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","setMaturityWiseCeiling","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showInstrumenetWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showInstrumenetWiseCeiling","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			IFProcessor ifProcessor=new IFProcessor();
			getInstrumentCategories(dynaForm);

			Log.log(Log.INFO,"IFAction","showInstrumenetWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward fetchInstrumentWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","fetchInstrumentWiseCeiling","Entered ");
			DynaActionForm dynaForm=(DynaActionForm)form;
			String instrName = (String)dynaForm.get("instrumentName");
			IFProcessor ifProcessor=new IFProcessor();
			InstrumentCategoryWiseCeiling instrWiseCeiling = ifProcessor.getInstrumentWiseCeiling(instrName);
			if (instrWiseCeiling!=null)
			{
				BeanUtils.copyProperties(dynaForm,instrWiseCeiling);
			}
			else
			{
				dynaForm.set("ceilingStartDate", null);
				dynaForm.set("ceilingEndDate", null);
				dynaForm.set("ceilingLimit", "0.0");
			}
			Log.log(Log.INFO,"IFAction","fetchInstrumentWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward setInstrumentWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","setInstrumenetWiseCeiling","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			InstrumentCategoryWiseCeiling instCategory = new InstrumentCategoryWiseCeiling(); 
			//InstrumentWiseCeiling instrumentCeiling=new InstrumentWiseCeiling();

			BeanUtils.populate(instCategory,dynaForm.getMap());

			IFProcessor ifProcessor=new IFProcessor();

			User user=getUserInformation(request);
			String userId=user.getUserId();

			ifProcessor.addInstrumentCatWiseCeiling(instCategory,userId);

			String message="Instrument Category Wise ceiling set Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","setInstrumenetWiseCeiling","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}
	public ActionForward showInvesteeGroupWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showInvesteeGroupWiseCeiling","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			IFProcessor ifProcessor=new IFProcessor();
			getAllInvesteeGroups(dynaForm);

			Log.log(Log.INFO,"IFAction","showInvesteeGroupWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward fetchInvestGrpWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","fetchInvestGrpWiseCeiling","Entered ");
			DynaActionForm dynaForm=(DynaActionForm)form;
			String investGroup = (String)dynaForm.get("investeeGroup");
			IFProcessor ifProcessor=new IFProcessor();
			InvesteeGroupWiseCeiling investGrpWiseCeiling = ifProcessor.getIGroupWiseCeiling(investGroup);
			if (investGrpWiseCeiling!=null)
			{
				BeanUtils.copyProperties(dynaForm,investGrpWiseCeiling);
			}
			else
			{
				dynaForm.set("ceilingStartDate", null);
				dynaForm.set("ceilingEndDate", null);
				dynaForm.set("ceilingLimit", "0.0");
				dynaForm.set("ceilingAmount", "0.0");
			}
			Log.log(Log.INFO,"IFAction","fetchInvestGrpWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward setInvesteeGroupWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","setInvesteeGroupWiseCeiling","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			InvesteeGroupWiseCeiling investeeGroupCeiling=new InvesteeGroupWiseCeiling();

			BeanUtils.populate(investeeGroupCeiling,dynaForm.getMap());

			IFProcessor ifProcessor=new IFProcessor();

			User user=getUserInformation(request);
			String userId=user.getUserId();

			ifProcessor.addInvesteeGroupWiseCeiling(investeeGroupCeiling,userId);


			String message="Investee group wise ceiling set Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","setInvesteeGroupWiseCeiling","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}
	public ActionForward showInvesteeWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","showInvesteeWiseCeiling","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			dynaForm.initialize(mapping);

			IFProcessor ifProcessor=new IFProcessor();
			getAllInvesteeGroups(dynaForm);

			Log.log(Log.INFO,"IFAction","showInvesteeWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward fetchInvesteeWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","fetchInvesteeWiseCeiling","Entered ");
			DynaActionForm dynaForm=(DynaActionForm)form;
			String investeeGroup = (String)dynaForm.get("investeeGroup");
			String investeeName = (String)dynaForm.get("investeeName");
			IFProcessor ifProcessor=new IFProcessor();
			InvesteeWiseCeiling investeWiseCeiling = ifProcessor.getInvesteeWiseCeiling(investeeGroup,investeeName);
			if (investeWiseCeiling!=null)
			{
				BeanUtils.copyProperties(dynaForm,investeWiseCeiling);
			}
			else
			{
				dynaForm.set("ceilingStartDate", null);
				dynaForm.set("ceilingEndDate", null);
				dynaForm.set("networth", "0.0");
				dynaForm.set("tangibleAssets", "0.0");
				dynaForm.set("ceilingLimit", "0.0");
				dynaForm.set("ceilingAmount", "0.0");
			}
			Log.log(Log.INFO,"IFAction","fetchInvesteeWiseCeiling","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getInvesteeNames(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","getInvesteeNames","Entered ");

			DynaActionForm dynaForm=(DynaActionForm)form;

			IFProcessor ifProcessor=new IFProcessor();
			getAllInvesteeNamesForGroup(dynaForm);

			Log.log(Log.INFO,"IFAction","getInvesteeNames","Exited ");
			return mapping.findForward(Constants.SUCCESS);
		}
	public ActionForward setInvesteeWiseCeiling(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
		{
			Log.log(Log.INFO,"IFAction","setInvesteeWiseCeiling","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			InvesteeWiseCeiling investeeCeiling=new InvesteeWiseCeiling();

			BeanUtils.populate(investeeCeiling,dynaForm.getMap());

			IFProcessor ifProcessor=new IFProcessor();

			User user=getUserInformation(request);
			String userId=user.getUserId();

			ifProcessor.addInvesteeWiseCeiling(investeeCeiling,userId);

			String message="Investee wise ceiling set Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","setInvesteeWiseCeiling","Exited");

			return mapping.findForward(Constants.SUCCESS);
		}

		public ActionForward showRatingWiseCeiling(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
			{
				Log.log(Log.INFO,"IFAction","showRatingWiseCeiling","Entered ");

				DynaActionForm dynaForm=(DynaActionForm)form;

				dynaForm.initialize(mapping);

				IFProcessor ifProcessor=new IFProcessor();
				getAllInvesteeGroups(dynaForm);
				getInstrumentTypes(dynaForm);
				getAllRatings(dynaForm);
			
				ArrayList agencyNames = ifProcessor.showRatingAgency();
				dynaForm.set("agencies",agencyNames);
			
				Log.log(Log.INFO,"IFAction","showRatingWiseCeiling","Exited ");
				return mapping.findForward(Constants.SUCCESS);
			}

			public ActionForward fetchRatingWiseCeiling(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception
				{
					Log.log(Log.INFO,"IFAction","fetchRatingWiseCeiling","Entered ");
					DynaActionForm dynaForm=(DynaActionForm)form;
					String investeeGroup = (String)dynaForm.get("investeeGroup");
					String investeeName = (String)dynaForm.get("investeeName");
					String instrumentName = (String)dynaForm.get("instrumentName");
					IFProcessor ifProcessor=new IFProcessor();
					RatingWiseCeiling ratingWiseCeiling = ifProcessor.getRatingWiseCeiling(investeeGroup,investeeName,instrumentName);
					if (ratingWiseCeiling!=null)
					{
						String agencyName = ratingWiseCeiling.getRatingAgency();
						Log.log(Log.INFO,"IFAction","fetchRatingWiseCeiling","agencyName " + agencyName);
						ArrayList ratings = ifProcessor.getRatingsForAgency(agencyName);
						Log.log(Log.INFO,"IFAction","fetchRatingWiseCeiling","ratings " + ratings.size());
				
						BeanUtils.copyProperties(dynaForm,ratingWiseCeiling);
						dynaForm.set("instrumentRatings",ratings);
						String rating = ratingWiseCeiling.getRating();
						Log.log(Log.INFO,"IFAction","fetchRatingWiseCeiling","rating " + rating);
						if(ratings.contains(rating))
						{
							dynaForm.set("rating",rating);
						}
				
					}
					else
					{
						dynaForm.set("rating","");
						dynaForm.set("ratingAgency","");
						dynaForm.set("ceilingStartDate",null);
						dynaForm.set("ceilingEndDate",null);
					}
					Log.log(Log.INFO,"IFAction","fetchRatingWiseCeiling","Exited ");
					return mapping.findForward(Constants.SUCCESS);
				}

	public ActionForward setRatingWiseCeiling(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
			{
				Log.log(Log.INFO,"IFAction","setRatingWiseCeiling","Entered");

				DynaActionForm dynaForm=(DynaActionForm)form;

				RatingWiseCeiling ratingCeiling=new RatingWiseCeiling();

				BeanUtils.populate(ratingCeiling,dynaForm.getMap());

				IFProcessor ifProcessor=new IFProcessor();

				User user=getUserInformation(request);
				String userId=user.getUserId();

				ifProcessor.setRatingWiseCeiling(ratingCeiling,userId);

				String message="Rating wise ceiling set Successfully";

				request.setAttribute("message",message);

				Log.log(Log.INFO,"IFAction","setRatingWiseCeiling","Exited");

				return mapping.findForward(Constants.SUCCESS);
			}

	public ActionForward chooseInvestee(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
	{

		Log.log(Log.INFO,"IFAction","chooseInvestee","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;
		Log.log(Log.INFO,"IFAction","chooseInvestee","Entered " + dynaForm);
		dynaForm.initialize(mapping);
		
		dynaForm.set("currentDate",new java.util.Date());
		//getAllInvesteeGroups(dynaForm);
		Log.log(Log.INFO,"IFAction","chooseInvestee","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward getExposureDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
	{

		Log.log(Log.INFO,"IFAction","getExposureDetails","Entered");

		DynaActionForm dynaForm=(DynaActionForm)form;

//		String investeeGroup=(String)dynaForm.get("investeeGroup");
//		String investeeName=(String)dynaForm.get("investeeName");
//		String proposedAmntToBeInvested = (String)dynaForm.get("proposedAmntToBeInvested");
		Date proposedDate = (Date) dynaForm.get("proposedDate");
		
		Date sysDate = (Date)dynaForm.get("currentDate");
		double corpusAmount = ((java.lang.Double)dynaForm.get("exposureCorpusAmount")).doubleValue();

		IFProcessor ifProcessor=new IFProcessor();
		
		/**
		 * Investee Wise Details
		 */

		ArrayList investeeWiseExpDetails=ifProcessor.getExposure(sysDate, proposedDate,corpusAmount);
		dynaForm.set("investeeWiseDetails",investeeWiseExpDetails);
		
		/**
		 * Investee Group Wise Details
		 */
		ArrayList investeeGrpWiseDetails = ifProcessor.getInvesteeGrpWiseDetails(sysDate, proposedDate,corpusAmount);
		dynaForm.set("investeeGrpWiseDetails",investeeGrpWiseDetails);

		/**
		 * Maturity Wise Details
		 */
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
		//double surplusAmount = ((java.lang.Double)dynaForm.get("totalSurplusAmount")).doubleValue();
		ArrayList maturityWiseDetails = ifProcessor.getMaturityWiseDetails(sysDate, proposedDate,surplusAmount);
		dynaForm.set("maturityWiseDetails",maturityWiseDetails);
		
		/**
		 * Instrument Category Wise
		 */
		ArrayList instCatWiseExpReport = ifProcessor.getInstCategoryWiseDetails(sysDate, proposedDate,surplusAmount);
		dynaForm.set("instCatWiseDetails",instCatWiseExpReport);

		Log.log(Log.INFO,"IFAction","getExposureDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward getPositionDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
	{
		
		Log.log(Log.INFO,"IFAction","getPositionDetails","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;
		
		IFProcessor ifProcessor=new IFProcessor();
		
		Date sysDate = (Date)dynaForm.get("currentDate");
		Date proposedDate = (Date) dynaForm.get("proposedDate");
		
		Log.log(Log.INFO,"IFAction","getPositionDetails","proposedDate :" + proposedDate);
		
		ExposureDetails exposureDetails=ifProcessor.getPositionDetails(sysDate, proposedDate);
		
		BeanUtils.copyProperties(dynaForm,exposureDetails);
		
		
		
		Log.log(Log.INFO,"IFAction","getPositionDetails","Exited");
		
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward statementReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","statementReport","Entered");
		ArrayList dan = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		java.sql.Date endDate = null;
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument");
		endDate = new java.sql.Date (eDate.getTime());
		dan = (ArrayList)ifProcessor.statementReport(endDate);
		dynaForm.set("mliDetails",dan);


		if(dan == null || dan.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","statementReport","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward statementReportDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","statementReportDetails","Entered");
		ArrayList dan = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String cgpan = request.getParameter("cgpan");
		java.sql.Date endDate = null;
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument");
		endDate = new java.sql.Date (eDate.getTime());
		dan = (ArrayList)ifProcessor.statementReportDetails(cgpan,endDate);
		dynaForm.set("statementDetails",dan);

		if(dan == null || dan.size()==0)
		{
			throw new NoDataException("No Data is available for this particular date," +
										" Please Enter Any Other Date ");
		}
		else
		{
			Log.log(Log.INFO,"IFAction","statementReportDetails","Exited");
			return mapping.findForward("success");
		}
	}


	public ActionForward fdiReport(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","fdiReport","Entered");
		ArrayList fdReport = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String status = (String) dynaForm.get("status");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument1");
		endDate = new java.sql.Date (eDate.getTime());

		if(status.equals("depositDate"))
		{
			fdReport = ifProcessor.getFdReportForDepositDate(startDate,endDate);
//			System.out.println("Processor Invoked3");
			dynaForm.set("fdReport",fdReport);
			if(fdReport == null  || fdReport.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
			Log.log(Log.INFO,"IFAction","fdiReport","Exited");
			return mapping.findForward("success2");
			}

		}
		else if (status.equals("maturityDate"))
		{
//			System.out.println("Processor Invoked for maturity");
			fdReport = ifProcessor.getFdReportForMaturityDate(startDate,endDate);
			dynaForm.set("fdReport",fdReport);
			if(fdReport == null  || fdReport.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
			Log.log(Log.INFO,"IFAction","fdiReport","Exited");
			return mapping.findForward("success3");
			}
		}

		else
		{
			fdReport = ifProcessor.getFdReport(startDate,endDate);
//			System.out.println("Processor Invoked");
			dynaForm.set("fdReport",fdReport);
			if(fdReport == null  || fdReport.size()==0)
			{
				throw new NoDataException("No Data is available for the values entered," +
											" Please Enter Any Other Value ");
			}
			else
			{
			Log.log(Log.INFO,"IFAction","fdiReport","Exited");
			return mapping.findForward("success1");
			}
		}
	}


	public ActionForward fdReceiptDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","fdReceiptDetails","Entered");
		ArrayList fdReceiptDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String number = (String) dynaForm.get("number");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument1");
		endDate = new java.sql.Date (eDate.getTime());
		fdReceiptDetails = (ArrayList)ifProcessor.fdReceiptDetails(number,startDate,endDate);
		dynaForm.set("fdReceiptDetails",fdReceiptDetails);

		if(fdReceiptDetails == null || fdReceiptDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
		Log.log(Log.INFO,"IFAction","fdReceiptDetails","Exited");
		return mapping.findForward("success");
		}
	}

	public ActionForward fdDetailsForDeposit(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","fdDetailsForDeposit","Entered");
		ArrayList fdReceiptDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String investee = request.getParameter("investee");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument1");
		endDate = new java.sql.Date (eDate.getTime());
		fdReceiptDetails = (ArrayList)ifProcessor.fdDetailsForDeposit(investee,startDate,endDate);
		dynaForm.set("fdReport",fdReceiptDetails);

		if(fdReceiptDetails == null || fdReceiptDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
		Log.log(Log.INFO,"IFAction","fdDetailsForDeposit","Exited");
		return mapping.findForward("success");
		}
	}

	public ActionForward fdDetailsForMaturity(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","fdDetailsForMaturity","Entered");
		ArrayList fdReceiptDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String investee = request.getParameter("investee");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		if((stDate == null) ||(stDate.equals("")))
		{
			startDate = null;
		}
		else if(stDate != null)
		{
			startDate = new java.sql.Date (sDate.getTime());
		}
		java.util.Date eDate =  (java.util.Date) dynaForm.get("dateOfTheDocument1");
		endDate = new java.sql.Date (eDate.getTime());
//		System.out.println("Processor Invoked");
		fdReceiptDetails = (ArrayList)ifProcessor.fdDetailsForMaturity(investee,startDate,endDate);
		dynaForm.set("fdReport",fdReceiptDetails);

		if(fdReceiptDetails == null || fdReceiptDetails.size()==0)
		{
			throw new NoDataException("No Data is available for the values entered," +
										" Please Enter Any Other Value ");
		}
		else
		{
		Log.log(Log.INFO,"IFAction","fdDetailsForMaturity","Exited");
		return mapping.findForward("success");
		}
	}

	public ActionForward showInvesteeGroup(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvesteeGroup","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		getAllInvesteeGroups(dynaForm);

		Log.log(Log.INFO,"IFAction","showInvesteeGroup","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showModInvesteeGroup(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showModInvesteeGroup","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
//		dynaForm.initialize(mapping);
		getAllInvesteeGroups(dynaForm);

		dynaForm.set("modInvesteeGroup", dynaForm.get("investeeGroup"));

		Log.log(Log.INFO,"IFAction","showModInvesteeGroup","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInvesteeList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvesteeMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		getAllInvesteeNamesForGroup(dynaForm);
		dynaForm.set("investeeTangibleAssets", new Double(0.0).toString());
		dynaForm.set("investeeNetWorth", new Double(0.0).toString());
		dynaForm.set("modInvestee", "");
		dynaForm.set("newInvestee", "");

		Log.log(Log.INFO,"IFAction","showInvesteeMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInvesteeDetail(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvesteeDetail","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "2");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String invGrp = (String)dynaForm.get("investeeGroup");
		String invName = (String)dynaForm.get("investee1");

		InvesteeDetail invDtl = new InvesteeDetail();
		IFProcessor ifProcessor = new IFProcessor();
		if (invName!=null && !invName.equals(""))
		{
			invDtl = ifProcessor.getInvesteeDetails(invGrp, invName);

			DecimalFormat df = new DecimalFormat("#############.##");
			df.setDecimalSeparatorAlwaysShown(false);

			dynaForm.set("investeeTangibleAssets", df.format(invDtl.getInvesteeTangibleAssets()));
			dynaForm.set("investeeNetWorth", df.format(invDtl.getInvesteeNetWorth()));
			//BeanUtils.copyProperties(dynaForm, invDtl);
			dynaForm.set("modInvestee", invName);
			dynaForm.set("newInvestee", "");
			
			Log.log(Log.INFO,"IFAction","showInvesteeDetail","ta " + dynaForm.get("investeeTangibleAssets"));
			Log.log(Log.INFO,"IFAction","showInvesteeDetail","nw " + dynaForm.get("investeeNetWorth"));
		}
		else
		{
			dynaForm.set("investeeTangibleAssets", "");
			dynaForm.set("investeeNetWorth", "");
			dynaForm.set("modInvestee", "");
			dynaForm.set("newInvestee", "");
		}

		Log.log(Log.INFO,"IFAction","showInvesteeDetail","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showMaturityMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showMaturityMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);
		getAllMaturities(dynaForm);

		Log.log(Log.INFO,"IFAction","showMaturityMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showMaturityDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showMaturityDetails","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String matType = (String)dynaForm.get("maturityType");

		MaturityDetail matDtl = new MaturityDetail();
		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showMaturityDetails","mat type " + matType);
		if (matType!=null && !matType.equals(""))
		{
			matDtl = ifProcessor.getMaturityDetails(matType);

			dynaForm.set("modMaturityType", matDtl.getMaturityType());
			dynaForm.set("maturityDescription", matDtl.getMaturityDescription());
			dynaForm.set("newMaturityType", "");
		}
		else
		{
			dynaForm.set("modMaturityType", "");
			dynaForm.set("maturityDescription", "");
			dynaForm.set("newMaturityType", "");
		}

		Log.log(Log.INFO,"IFAction","showMaturityDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetHeadMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetHeadMaster","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.initialize(mapping);

		IFProcessor ifProcessor = new IFProcessor();
		ArrayList budgetHeads=ifProcessor.getBudgetHeadTypes("I");

		dynaForm.set("budgetHeadsList", budgetHeads);

		Log.log(Log.INFO,"IFAction","showBudgetHeadMaster","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetHeadList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetHeadList","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String headType = (String)dynaForm.get("budgetHeadType");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showBudgetHeadList","head type " + headType);
		ArrayList budgetHeads=ifProcessor.getBudgetHeadTypes(headType);

		dynaForm.set("budgetHeadsList", budgetHeads);
//		dynaForm.set("budgetHead","");
//		dynaForm.set("newBudgetHead","");
//		dynaForm.set("modBudgetHead","");

		Log.log(Log.INFO,"IFAction","showBudgetHeadList","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetHeadDetail(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetHeadDetail","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "2");

		DynaActionForm dynaForm=(DynaActionForm)form;
/*		String headType = (String)dynaForm.get("budgetHeadType");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showBudgetHeadList","head type " + headType);
		ArrayList budgetHeads=ifProcessor.getBudgetHeadTypes(headType);

		dynaForm.set("budgetHeadsList", budgetHeads);
		dynaForm.set("budgetHead","");
		dynaForm.set("newBudgetHead","");
		dynaForm.set("modBudgetHead","");*/
		dynaForm.set("modBudgetHead", dynaForm.get("budgetHead"));

		Log.log(Log.INFO,"IFAction","showBudgetHeadDetail","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetSubHeadList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String headType = (String)dynaForm.get("budgetHeadType");
		String headTitle = (String)dynaForm.get("budgetHead");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","head type " + headType);
		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","head title " + headTitle);
		Vector budgetSubHeads=ifProcessor.getBudgetSubHeadTitles(headTitle,headType);

		dynaForm.set("budgetSubHeadsList", budgetSubHeads);

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBudgetSubHeadDetail(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadDetail","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "2");

		DynaActionForm dynaForm=(DynaActionForm)form;
/*		String headType = (String)dynaForm.get("budgetHeadType");
		String headTitle = (String)dynaForm.get("budgetHead");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","head type " + headType);
		Log.log(Log.INFO,"IFAction","showBudgetSubHeadList","head title " + headTitle);
		Vector budgetSubHeads=ifProcessor.getBudgetSubHeadTitles(headTitle,headType);

		dynaForm.set("budgetSubHeadsList", budgetSubHeads);*/
		dynaForm.set("modBudgetSubHeadTitle", dynaForm.get("budgetSubHeadTitle"));

		Log.log(Log.INFO,"IFAction","showBudgetSubHeadDetail","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstrumentList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstrumentList","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String instType = (String)dynaForm.get("instrumentType");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showInstrumentList","instrument type " + instType);
		ArrayList instruments=ifProcessor.getInstrumentTypes(instType);

		dynaForm.set("instrumentNames", instruments);
		dynaForm.set("newInstrumentName","");
		dynaForm.set("modInstrumentName","");
		dynaForm.set("instrumentDescription","");

		Log.log(Log.INFO,"IFAction","showInstrumentList","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstrumentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstrumentDetails","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "2");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String instName = (String)dynaForm.get("instrumentName");

		IFProcessor ifProcessor = new IFProcessor();
		Log.log(Log.INFO,"IFAction","showInstrumentDetails","instrument name " + instName);
		InstrumentDetail instDetail = new InstrumentDetail();

		if (!instName.equals(""))
		{
			instDetail=ifProcessor.getInstrumentDetail(instName);
			dynaForm.set("newInstrumentName","");
			dynaForm.set("modInstrumentName", instName);
			dynaForm.set("instrumentDescription", instDetail.getInstrumentDescription());
		}
		else
		{
			dynaForm.set("newInstrumentName","");
			dynaForm.set("modInstrumentName", "");
			dynaForm.set("instrumentDescription", "");
		}

		Log.log(Log.INFO,"IFAction","showInstrumentList","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstrumentFeatures(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstrumentFeatures","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "0");

		DynaActionForm dynaForm=(DynaActionForm)form;
		getInstrumentFeatures(dynaForm);

		Log.log(Log.INFO,"IFAction","showInstrumentFeatures","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstFeaturesDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInstFeaturesDetails","Entered");

		HttpSession session =request.getSession(false);
		session.setAttribute("modFlag", "1");

		DynaActionForm dynaForm=(DynaActionForm)form;
		String instFeature = (String)dynaForm.get("instrumentFeatures");
		IFProcessor ifProcessor=new IFProcessor();

		InstrumentFeature instrumentFeature = new InstrumentFeature();
		if (!instFeature.equals(""))
		{
			instrumentFeature=ifProcessor.getInstFeaturesDetails(instFeature);
			BeanUtils.copyProperties(dynaForm, instrumentFeature);
			dynaForm.set("newInstrumentFeatures", "");
		}
		else
		{
			dynaForm.set("newInstrumentFeatures", "");
			dynaForm.set("modInstrumentFeatures", "");
			dynaForm.set("instrumentFeatureDescription", "");
		}

		Log.log(Log.INFO,"IFAction","showInstFeaturesDetails","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInstSchemeDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showInstSchemeDetails","Entered");

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "2");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String instScheme = (String)dynaForm.get("instrumentSchemeType");
			IFProcessor ifProcessor=new IFProcessor();

			if (!instScheme.equals(""))
			{
				String instSchemeDesc=ifProcessor.getInstSchemeDetails(instScheme);
				dynaForm.set("newInstrumentSchemeType", "");
				dynaForm.set("modInstrumentSchemeType", instScheme);
				dynaForm.set("instrumentSchemeDescription", instSchemeDesc);
			}
			else
			{
				dynaForm.set("newInstrumentSchemeType", "");
				dynaForm.set("modInstrumentSchemeType", "");
				dynaForm.set("instrumentSchemeDescription", "");
			}

			Log.log(Log.INFO,"IFAction","showInstSchemeDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward getInstrumentSchemes(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "1");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String inst = (String) dynaForm.get("instrument");
			if (!inst.equals(""))
			{
				getInstrumentSchemeTypes(dynaForm);
			}
			else
			{
				dynaForm.set("instrumentSchemeTypes", new ArrayList());
				dynaForm.set("instrumentSchemeType", "");
				dynaForm.set("newInstrumentSchemeType", "");
				dynaForm.set("modInstrumentSchemeType", "");
				dynaForm.set("instrumentSchemeDescription", "");
			}
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showRatingMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showRatingMaster","Exited");

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;
			getAllRatings(dynaForm);
			Log.log(Log.INFO,"IFAction","showRatingMaster","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showRatingDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showRatingDetails","Entered");

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "1");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String rating = (String)dynaForm.get("rating");
			IFProcessor ifProcessor=new IFProcessor();

			if (!rating.equals(""))
			{
				Hashtable ratingDetails=ifProcessor.getRatingDetails(rating);
				dynaForm.set("newRating", "");
				dynaForm.set("modRating", rating);
				dynaForm.set("ratingDescription", ratingDetails.get("Description"));
				dynaForm.set("ratingGivenBy", ratingDetails.get("Given By"));
			}
			else
			{
				dynaForm.set("newRating", "");
				dynaForm.set("modRating", "");
				dynaForm.set("ratingDescription", "");
				dynaForm.set("ratingGivenBy", "");
			}

			Log.log(Log.INFO,"IFAction","showRatingDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showBankAccountMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showBankAccountMaster","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			HttpSession session=request.getSession(false);
			session.setAttribute("flag", "2");

			IFProcessor ifProcessor=new IFProcessor();
			ArrayList bankDetails=ifProcessor.getAllBanksWithAccountNumbers();

			ArrayList bankNames=new ArrayList(bankDetails.size());
			String bankName="";
			for(int i=0;i<bankDetails.size();i++)
			{
				BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
				if (bankAccountDetail.getAccountType().equals("S"))
				{
					bankName=bankAccountDetail.getBankName()+
									" ,"+bankAccountDetail.getBankBranchName()+
									"("+bankAccountDetail.getAccountNumber()+")";

					bankNames.add(bankName);
				}
			}
			dynaForm.set("bankNames", bankNames);
			dynaForm.set("minBalance", new Double(0.0));

			Log.log(Log.INFO,"IFAction","showBankAccountMaster","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}	
		

	public ActionForward showBankAccountList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showBankAccountList","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String accType=(String)dynaForm.get("accountType");

			IFProcessor ifProcessor=new IFProcessor();
			ArrayList bankDetails=ifProcessor.getAllBanksWithAccountNumbers();

			ArrayList bankNames=new ArrayList(bankDetails.size());
			String bankName="";
			for(int i=0;i<bankDetails.size();i++)
			{
				BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
				if (bankAccountDetail.getAccountType().equals(accType))
				{
					bankName=bankAccountDetail.getBankName()+
									" ,"+bankAccountDetail.getBankBranchName()+
									"("+bankAccountDetail.getAccountNumber()+")";

					bankNames.add(bankName);
				}
			}
			dynaForm.set("bankNames", bankNames);
			dynaForm.set("accountNumber", "");
			dynaForm.set("bankName", "");
			dynaForm.set("bankBranchName", "");
			dynaForm.set("minBalance", new Double(0.0));
			HttpSession session=request.getSession(false);
			session.setAttribute("flag", "2");

			Log.log(Log.INFO,"IFAction","showBankAccountList","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showBankAccountDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showBankAccountDetails","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			String accType =(String)dynaForm.get("accountType");
			String accChosen=(String)dynaForm.get("accComb");
			String bankName="";
			String branchName="";
			String accNumber="";
			double minBal = 0;

			if (accChosen!=null && !accChosen.equals(""))
			{
				int index=accChosen.indexOf(",");
				bankName=(accChosen.substring(0,index)).trim();
				int index1=accChosen.indexOf("(");
				branchName=(accChosen.substring((index+1), index1)).trim();
				index=accChosen.indexOf(")");
				accNumber=(accChosen.substring((index1+1), index)).trim();
				IFProcessor ifProcessor = new IFProcessor();
				minBal = ifProcessor.getBankAccountDetails(bankName, branchName, accNumber);
			}
			
			BankAccountDetail accDetail = new BankAccountDetail();
			accDetail.setAccountNumber(accNumber);
			accDetail.setBankName(bankName);
			accDetail.setBankBranchName(branchName);
			accDetail.setMinBalance(minBal);

/*			dynaForm.set("accountNumber", accNumber);
			dynaForm.set("bankName", bankName);
			dynaForm.set("bankBranchName", branchName);
			dynaForm.set("minBalance", new Double(minBal));*/
			
			BeanUtils.copyProperties(dynaForm, accDetail);
			dynaForm.set("accountType", accType);
			HttpSession session=request.getSession(false);
			session.setAttribute("flag", "3");

			Log.log(Log.INFO,"IFAction","showBankAccountDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}


	public ActionForward showUpdateCorpusMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showUpdateCorpusMaster","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			HttpSession session = request.getSession(false);
			session.setAttribute("flag", "U");

			Date date = new Date();
			Calendar calendar =Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			month = month - 1;
			day= day + 1;
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE,day);
			Date prevDate = calendar.getTime();

			Dates generalReport = new Dates();
			generalReport.setCorpusFromDate(prevDate);
			generalReport.setCorpusToDate(date);
			BeanUtils.copyProperties(dynaForm, generalReport);

			Log.log(Log.INFO,"IFAction","showUpdateCorpusMaster","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showInsertCorpusMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showInsertCorpusMaster","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			HttpSession session = request.getSession(false);
			session.setAttribute("flag", "I");

			Log.log(Log.INFO,"IFAction","showInsertCorpusMaster","Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showCorpusList(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showCorpusList","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			Date fromDate = (Date)dynaForm.get("corpusFromDate");
			Date toDate = (Date)dynaForm.get("corpusToDate");

			IFProcessor ifProcessor = new IFProcessor();
			ArrayList corpusList = ifProcessor.getCorpusList(fromDate, toDate);
			dynaForm.set("corpusList", corpusList);

			Log.log(Log.INFO,"IFAction","showCorpusList","Exited");
			return mapping.findForward("displayList");
		}

	public ActionForward showCorpusDetail(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showCorpusDetail","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			CorpusDetail corpus = new CorpusDetail();

			String corpusId = (String) request.getParameter("id");
			if (corpusId==null)
			{
				corpusId=(String) dynaForm.get("corpusId");
			}

			IFProcessor ifProcessor= new IFProcessor();
			corpus = ifProcessor.getCorpusDetails(corpusId);

/*			dynaForm.set("corpusId", corpus.getCorpusId());
			dynaForm.set("corpusContributor", corpus.getCorpusContributor());
			dynaForm.set("corpusAmount", new Double(corpus.getCorpusAmount()));
			dynaForm.set("corpusDate", corpus.getCorpusDate());*/
			BeanUtils.copyProperties(dynaForm, corpus);

			Log.log(Log.INFO,"IFAction","showCorpusDetail","Exited");
			return mapping.findForward("display");
		}

	public ActionForward showHolidayMaster(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showHolidayMaster","Entered");

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "0");

			DynaActionForm dynaForm=(DynaActionForm)form;

			IFProcessor ifProcessor= new IFProcessor();
			ArrayList holDates = ifProcessor.getHolidayDates();

			dynaForm.set("holidayDates", holDates);
			dynaForm.set("holidayDescription", "");
			dynaForm.set("modHolidayDate", "");
			dynaForm.set("holidayDate", "");
			dynaForm.set("newHolidayDate", "");

			Log.log(Log.INFO,"IFAction","showHolidayMaster","Exited");
			return mapping.findForward("display");
		}

	public ActionForward showHolidayDetail(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
			Log.log(Log.INFO,"IFAction","showHolidayDetail","Entered");

			HttpSession session =request.getSession(false);
			session.setAttribute("modFlag", "1");

			DynaActionForm dynaForm=(DynaActionForm)form;

			IFProcessor ifProcessor= new IFProcessor();
			String strDate = (String)dynaForm.get("holidayDate");
			Date holDate = (new SimpleDateFormat("dd/MM/yyyy")).parse(strDate, new ParsePosition(0));
			if (holDate!=null && !holDate.toString().equals(""))
			{
				String holDesc = ifProcessor.getHolidayDetail(holDate);
				dynaForm.set("holidayDescription", holDesc);
				dynaForm.set("modHolidayDate", strDate);
				dynaForm.set("holidayDate", strDate);
				dynaForm.set("newHolidayDate", "");
			}
			else
			{
				dynaForm.set("holidayDescription", "");
				dynaForm.set("modHolidayDate", "");
				dynaForm.set("holidayDate", "");
				dynaForm.set("newHolidayDate", "");
			}

			Log.log(Log.INFO,"IFAction","showHolidayDetail","Exited");
			return mapping.findForward("display");
		}

	public ActionForward showEnterPaymentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)	throws Exception
	{
		Log.log(Log.INFO, "IFAction", "showEnterPaymentDetails", "Entered");
		ArrayList bankDetails = new ArrayList();
		IFProcessor ifProcessor = new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		dynaForm.initialize(mapping);
		bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		ArrayList bankNames = new ArrayList(bankDetails.size());
		String bankName = "";
		for(int i=0; i<bankDetails.size(); i++)
		{
			BankAccountDetail bankAccountDetail = (BankAccountDetail)bankDetails.get(i);
			bankName= bankAccountDetail.getBankName()+ " , " +
				bankAccountDetail.getBankBranchName()+"("+bankAccountDetail.getAccountNumber()+")";
			bankNames.add(bankName);
		}
		dynaForm.set("banks", bankNames);
		bankDetails = null;
		ifProcessor = null;
		Log.log(Log.INFO, "IFAction", "showEnterpaymentDetails", "Exited");
		return mapping.findForward("success");
	}



	public ActionForward saveEnterPaymentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;

			PaymentDetails paydetails=new PaymentDetails();
			ChequeDetails chequeDetails= null;

			String contextPath = request.getSession(false).getServletContext().getRealPath("");
			Log.log(Log.DEBUG,"RPAction","getPaymentsMade","path " + contextPath);

			User user = getUserInformation(request);
			String userId = user.getUserId();

			BeanUtils.populate(paydetails,dynaForm.getMap());
			String ifChequed = (String) dynaForm.get("ifChequed");
			if(ifChequed.equals("Y"))
				{
					chequeDetails=new ChequeDetails();
					BeanUtils.populate(chequeDetails, dynaForm.getMap());
					String bankName = chequeDetails.getBankName();

					int i = bankName.indexOf(",");
					//System.out.println("i:"+i);
					String newBank =  bankName.substring(0,i);
					//System.out.println("newBank:"+newBank);
					chequeDetails.setBankName(newBank);

					int j = bankName.indexOf("(");
					//System.out.println("j:"+j);
					String newBranch =  bankName.substring(i+1,j);
					//System.out.println("newBranch:"+newBranch);
					chequeDetails.setBranchName(newBranch);

					chequeDetails.setUserId(userId);
					paydetails.setUserId(userId);
					Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","bank " + chequeDetails.getBankName());
					Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","chequ no " + chequeDetails.getChequeNumber());
					Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","date " + chequeDetails.getChequeDate().toString());
					Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","amount " + chequeDetails.getChequeAmount());
					Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","to " + chequeDetails.getChequeIssuedTo());
				}
				paydetails.setUserId(userId);

			IFProcessor ifProcessor=new IFProcessor();

			ifProcessor.saveEnterPaymentDetails(paydetails,chequeDetails, contextPath);

			String message="Payment Details are entered Successfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO,"IFAction","saveEnterPaymentDetails","Exited");

			return mapping.findForward(Constants.SUCCESS);

		}

	public ActionForward saveModifyPaymentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO, "IFAction", "saveModifyPaymentDetails", "Entered");

			DynaActionForm dynaForm = (DynaActionForm)form;

			PaymentDetails paydetails = new PaymentDetails();
			User user = getUserInformation(request);
			String userId = user.getUserId();


			BeanUtils.populate(paydetails,dynaForm.getMap());
			paydetails.setUserId(userId);

			IFProcessor ifProcessor = new IFProcessor();

			ifProcessor.saveModifyPaymentDetails(paydetails);

			String message = "Payment Details are updated succesfully";

			request.setAttribute("message",message);

			Log.log(Log.INFO, "IFAction", "saveModifyPaymentDetails", "Exited");

			return mapping.findForward(Constants.SUCCESS);

	}

	public  ActionForward showPaymentDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)  throws Exception
		{

			Log.log(Log.INFO, "IFAction", "showPaymentDetails", "Entered");
			DynaActionForm dynaForm = (DynaActionForm)form;
			dynaForm.initialize(mapping);
			Log.log(Log.INFO, "IFAction", "showPaymentDetails", "Entered");
			return mapping.findForward(Constants.SUCCESS);

		}


	public  ActionForward showListOfPaymentDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)  throws Exception
		{

			Log.log(Log.INFO, "IFAction", "showListOfPaymentDetails", "Displayed");

			DynaActionForm dynaForm = (DynaActionForm)form;

			IFProcessor ifProcessor = new IFProcessor();

			PaymentDetails paydetails = new PaymentDetails();
			BeanUtils.populate(paydetails,dynaForm.getMap());

			ArrayList payments = ifProcessor.showListOfPaymentDetails(paydetails);
			dynaForm.set("payments", payments);

			if((payments.size()==0) || (payments == null))
			{
				throw new NoDataException("No Data is available for this value," +
										" Please Choose Any Other Value ");
			}
			else
			{
				payments = null;
				Log.log(Log.INFO, "IFAction", "showListOfPaymentDetails", "Exited");
				return mapping.findForward(Constants.SUCCESS);
			}
		}


	public ActionForward getPaymentDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "getPaymentDetails", "Entered");
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
			DynaActionForm dynaForm = (DynaActionForm)form;
			PaymentDetails paydetails = new PaymentDetails();
			String payId = (String) request.getParameter("Id");
			IFProcessor ifProcessor = new IFProcessor();
			paydetails = ifProcessor.getPaymentDetails(payId);
			BeanUtils.copyProperties(dynaForm,paydetails);
/*			dynaForm.set("payId", paydetails.getPayId());
			dynaForm.set("paymentsTo", paydetails.getPaymentsTo());
			dynaForm.set("amount", new Double(paydetails.getAmount()));
			dynaForm.set("remarks", paydetails.getRemarks());
			//java.util.Date payDate = paydetails.getPaymentDate();
			//String newPayDate = dateFormat.format(payDate);
			dynaForm.set("paymentDate", paydetails.getPaymentDate());
			System.out.println("paymentDate"+ paydetails.getPaymentDate());*/
			Log.log(Log.INFO, "IFAction", "getPaymentDetails", "Exited");
			return mapping.findForward("success");
	}


	public ActionForward chequeDetailsForPayment(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","chequeDetailsForPayment","Entered");
		ChequeDetails cheque = new ChequeDetails();
		IFProcessor ifProcessor=new IFProcessor();
		DynaActionForm dynaForm = (DynaActionForm)form;
		String chequeNumber = (String) dynaForm.get("number");
		cheque = ifProcessor.chequeDetailsForPayment(chequeNumber);
		ArrayList bankDetails = ifProcessor.getAllBanksWithAccountNumbers();
		ArrayList bankNames=new ArrayList(bankDetails.size());
		String bankName="";
		for(int i=0;i<bankDetails.size();i++)
		{
			BankAccountDetail bankAccountDetail=(BankAccountDetail)bankDetails.get(i);
			bankName=bankAccountDetail.getBankName()
			+ " ,"+bankAccountDetail.getBankBranchName()+
			"("+bankAccountDetail.getAccountNumber()+")";

			bankNames.add(bankName);
		}
		dynaForm.set("banks",bankNames);
		//System.out.println("bank name :" + cheque.getBankName());
		dynaForm.set("bankName",cheque.getBankName());
		BeanUtils.copyProperties(dynaForm,cheque);
		//dynaForm.set("chequeNumbers",cheque);
		Log.log(Log.INFO,"IFAction","chequeDetailsForPayment","Exited");
		return mapping.findForward("success");
	}


	public ActionForward showInflowOutflowReportInput(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "showInflowOutflowReportInput", "Entered");
			InvestmentForm dynaForm = (InvestmentForm)form;
			//dynaForm.initialize(mapping);
			Date date = new Date();
			Calendar calendar =Calendar.getInstance();
			Dates dates =  new Dates();
			dates.setValueDate(date);
			BeanUtils.copyProperties(dynaForm, dates);

			Log.log(Log.INFO, "IFAction", "showInflowOutflowReportInput", "Exited");
			return mapping.findForward("display");
	}

	public ActionForward showInvstMaturingDetails(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "showInvstMaturingDetails", "Entered");
			InvestmentForm ifForm = (InvestmentForm)form;
			Date date = ifForm.getValueDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			if (date!=null && !date.toString().equals(""))
			{
				IFProcessor ifProcessor = new IFProcessor();
				Map reportDetails = ifProcessor.showInvstMaturingDetails(date);
				
				ifForm.setInvstMaturingDetails(reportDetails);
				ifForm.setValueDate(date);
			}

			Log.log(Log.INFO, "IFAction", "showInvstMaturingDetails", "Exited");
			return mapping.findForward("display");
	}
	
	public ActionForward showInflowOutflowReport(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "showInflowOutflowReport", "Entered");
			InvestmentForm ifForm = (InvestmentForm)form;
			Date date = ifForm.getValueDate();
			Map invMatDetails = ifForm.getInvstMaturingDetails();
			
			Set imSet = invMatDetails.keySet();
			Iterator imIterator=imSet.iterator();
			int notPresentCount=0;
			boolean toAdd=false;
			String removeKey="";
			while(imIterator.hasNext())
			{
				String key = (String)imIterator.next();
				InvestmentMaturityDetails imDetail=(InvestmentMaturityDetails)invMatDetails.get(key);
				if (imDetail.getPliId()==0 && (imDetail.getInvName()==null || imDetail.getInvName().equals("")) &&
					(imDetail.getMaturityAmt()==null || imDetail.getMaturityAmt().equals("") || Double.parseDouble(imDetail.getMaturityAmt())==0) &&
					(imDetail.getOtherDesc()==null || imDetail.getOtherDesc().equals("")))
				{
					toAdd=false;
					removeKey=key;
				}
				else
				{
					toAdd=true;
				}
			}
			
			if (!toAdd)
			{
				invMatDetails.remove(removeKey);
			}
			
			User user = getUserInformation(request);
			String userId = user.getUserId();
			
			IFProcessor ifProcessor = new IFProcessor();
			Map ioReport = ifProcessor.showInflowOutflowReport(date, invMatDetails, userId);
			
			ArrayList maturityDetails = (ArrayList) ioReport.get("MA");
			ArrayList fundTransfers = (ArrayList) ioReport.get("FT");
			ArrayList provisionDetails = (ArrayList) ioReport.get("PR");
			Map mainDetails = (Map) ioReport.get("DT");
			
			ifForm.setValueDate(date);
			ifForm.setPlanInvMainDetails(mainDetails);
			ifForm.setPlanInvMatDetails(maturityDetails);
			ifForm.setPlanInvFTDetails(fundTransfers);
			ifForm.setPlanInvProvisionDetails(provisionDetails);

/*			imSet = ioReport.keySet();
			imIterator = imSet.iterator();
			while (imIterator.hasNext())
			{
				InflowOutflowReport io = (InflowOutflowReport) ioReport.get(imIterator.next());
				Log.log(Log.DEBUG, "IFAction", "showInflowOutflowReport", "bank name " + io.getBankName());
				Log.log(Log.DEBUG, "IFAction", "showInflowOutflowReport", "bank name " + io.getStmtBalance());
			}*/

			Log.log(Log.INFO, "IFAction", "showInflowOutflowReport", "Exited");
			return mapping.findForward("display");
	}
	
	public ActionForward saveInflowOutflowReportInput(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
			Log.log(Log.INFO, "IFAction", "saveInflowOutflowReportInput", "Entered");
			InvestmentForm ifForm = (InvestmentForm)form;
			Date date = ifForm.getValueDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Map planInvProvisionDetails = ifForm.getProvisionRemarks();
			Map planInvMainDetails = ifForm.getPlanInvMainDetails();
			
			User user = getUserInformation(request);
			String userId = user.getUserId();
			
			IFProcessor ifProcessor = new IFProcessor();
			ifProcessor.saveInflowOutflowReport(date, planInvMainDetails, planInvProvisionDetails, userId);
			
			request.setAttribute("message", "Plan Investment Report Details saved successfully");
				
			Log.log(Log.INFO, "IFAction", "saveInflowOutflowReportInput", "Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showInvDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

			Log.log(Log.INFO,"IFAction","showInvDetails","Exited");

			HttpSession session = request.getSession(false);
			session.setAttribute("invFlag", "1");

			DynaActionForm dynaForm = (DynaActionForm) form;
			String invRefNo = (String) dynaForm.get("investmentReferenceNumber");

			if (invRefNo.equals(""))
			{
				dynaForm.set("investeeName", "");
				dynaForm.set("principalAmount", "");
				dynaForm.set("costOfPurchase", "");
				dynaForm.set("noOfUnits", "");
				dynaForm.set("numberOfSecurities", "");
				dynaForm.set("noOfCommercialPapers", "");
			}
			else
			{
				IFProcessor ifProcessor = new IFProcessor();
				ArrayList details = ifProcessor.getInvDetails(invRefNo);
				String invName = (String)details.get(0);
				Double amt = (Double)details.get(1);
				int units = ((Integer)details.get(2)).intValue();
				dynaForm.set("investeeName", invName);
				dynaForm.set("principalAmount", ""+amt);
				dynaForm.set("costOfPurchase", ""+amt);
				dynaForm.set("noOfUnits", ""+units);
				dynaForm.set("numberOfSecurities", ""+units);
				dynaForm.set("noOfCommercialPapers", ""+units);
//				dynaForm.set("ratingCeiling", (String)details.get(3));
			}

			Log.log(Log.INFO,"IFAction","showInvDetails","Exited");

			return mapping.findForward("success");
		}

	public ActionForward showInvFullfilmentRefNos(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvFullfilmentRefNos","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("fullfilmentFlag", "1");
		
		IFProcessor ifProcessor = new IFProcessor(); 

			DynaActionForm dynaForm=(DynaActionForm)form;
//			dynaForm.initialize(mapping);

			getAllInvestees(dynaForm);
			getInstrumentTypes(dynaForm);
			getGenInstrumentTypes(dynaForm);
			getInstrumentFeatures(dynaForm);

			getInstrumentSchemes(dynaForm);
			getInvRefNosForFullfilment(dynaForm);
			

/*			dynaForm.set("instrumentType","");
			dynaForm.set("instrumentNumber","");
			dynaForm.set("instrumentDate",null);
			dynaForm.set("instrumentAmount","");
			dynaForm.set("drawnBank","");
			dynaForm.set("drawnBranch","");
			dynaForm.set("payableAt","");
*/
			Log.log(Log.INFO,"IFAction","showInvFullfilmentRefNos","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	private void getInvRefNosForFullfilment(DynaActionForm dynaForm) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		String inst = (String) dynaForm.get("instrumentName");
		String inv = (String) dynaForm.get("investeeName");
		Log.log(Log.DEBUG, "IFAction", "getInvestmentReferenceNumbers", "inv name  " + inv);
		ArrayList investmentRefNumbers=ifProcessor.getInvRefNosForFullfilment(inst, inv);

		dynaForm.set("investmentRefNumbers",investmentRefNumbers);
	}

	public ActionForward showTDSInvRefNos(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showTDSInvRefNos","Entered");
		DynaActionForm dynaForm=(DynaActionForm)form;
//		dynaForm.initialize(mapping);

		getAllInvestees(dynaForm);
		getInstrumentTypes(dynaForm);
		getInvRefNosForTDS(dynaForm);

		Log.log(Log.INFO,"IFAction","showTDSInvRefNos","Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	private void getInvRefNosForTDS(DynaActionForm dynaForm) throws DatabaseException
	{
		IFProcessor ifProcessor=new IFProcessor();
		String inst = (String) dynaForm.get("instrumentName");
		String inv = (String) dynaForm.get("investeeName");
		Log.log(Log.DEBUG, "IFAction", "getInvestmentReferenceNumbers", "inv name  " + inv);
		ArrayList investmentRefNumbers=ifProcessor.getInvRefNosForTDS(inst, inv);

		dynaForm.set("investmentRefNumbers",investmentRefNumbers);
	}

	public ActionForward calMaturityDateAmt(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","calMaturityDateAmt","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("invFlag", "2");

		IFProcessor ifProcessor=new IFProcessor();
		double prlAmt=0;
		int compFreq=0;
		double intRate=0;
		double tenure=0;
		double amount=0;
		double intAmt=0;
		double matAmt=0;
		int balDays=0;
		String tenureType="";
		Date invDate =null;
		Date matDate = null;
		String instName="";

		DynaActionForm dynaForm=(DynaActionForm) form;


		InvestmentDetails invDetail = new InvestmentDetails();

		BeanUtils.populate(invDetail, dynaForm.getMap());
		
		if (!((String)dynaForm.get("principalAmount")).equals(""))
		{
			prlAmt=Double.parseDouble((String)dynaForm.get("principalAmount"));
		}
		else
		{
			prlAmt=Double.parseDouble((String)dynaForm.get("faceValue"));
		}

		if (!((String)dynaForm.get("compoundingFrequency")).equals(""))
		{
			compFreq=Integer.parseInt((String)dynaForm.get("compoundingFrequency"));
		}

		if (((String)dynaForm.get("interestRate")).equals("") || Double.parseDouble((String)dynaForm.get("interestRate"))==0)
		{
			intRate=Double.parseDouble((String)dynaForm.get("couponRate"));
		}
		else
		{
			intRate=Double.parseDouble((String)dynaForm.get("interestRate"));
		}

		tenure=Double.parseDouble((String)dynaForm.get("tenure"));
		tenureType=(String)dynaForm.get("tenureType");
		if (((String)dynaForm.get("instrumentName")).equalsIgnoreCase("Commercial Papers"))
		{
			tenureType="M";
		}

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","prl amt " + prlAmt);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","comp Freq " + compFreq);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","int rate " + intRate);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","tenure " + tenure);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","tenure type " + tenureType);

		if (tenureType.equalsIgnoreCase("M"))
		{
			tenure=tenure/12;
		}

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","tenure " + tenure);

		if (compFreq==4)
		{
			intRate=intRate/4;
			tenure=tenure*4;
		}
		else if(compFreq==2)
		{
			intRate=intRate/2;
			tenure=tenure*2;
		}
		else if (compFreq==12)
		{
			intRate=intRate/12;
			tenure=tenure*12;
		}

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","tenure " + tenure);

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","bal days " + balDays);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","rate " + intRate);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","tenure " + tenure);

		if (tenureType.equalsIgnoreCase("D"))
		{
			matAmt=prlAmt + (prlAmt*(tenure/365)*intRate/100);
		}
		else
		{

			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","1 " + (1+(intRate/100)));
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","2 " + (Math.pow((1+(intRate/100)), tenure)));

			amount = prlAmt * (Math.pow((1+(intRate/100)), tenure));
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","3 " + amount);
//			intAmt = (amount * (intRate/100) * balDays)/365;
//			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","4 " + intAmt);

			matAmt=amount + intAmt;
		}

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","mat amt" + matAmt);
		DecimalFormat df = new DecimalFormat("###############.##");
		df.setDecimalSeparatorAlwaysShown(false);

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","mat amt" + df.format(matAmt));
		invDetail.setMaturityAmount(matAmt);
		//dynaForm.set("maturityAmount", df.format(matAmt));

		int iTenure=Integer.parseInt((String)dynaForm.get("tenure"));
		if (dynaForm.get("dateOfInvestment")!=null && !((Date)dynaForm.get("dateOfInvestment")).toString().equals(""))
		{
			invDate = (Date)dynaForm.get("dateOfInvestment");
		}
		else
		{
			invDate = (Date)dynaForm.get("dateOfDeposit");
		}

		Calendar calendar = Calendar.getInstance();
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
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","day " + dayOfWeek);
/*		if (dayOfWeek == Calendar.SATURDAY)
		{
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","day sat");
			calendar.add(Calendar.DATE, 2);					//maturity date falls on sat, so add 2 days so that the maturity date falls on monday
		}*/
/*		if (dayOfWeek == Calendar.SUNDAY)
		{
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","day sun");
			calendar.add(Calendar.DATE, 1);					//maturity date falls on sat, so add 1 day so that the maturity date falls on monday
		}*/

		matDate = calendar.getTime();
		dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","day " + dayOfWeek);

		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","date " + matDate);
		ArrayList holidays = ifProcessor.getAllHolidays();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
/*		matDate=dateFormat.parse(dateFormat.format(matDate));
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","date " + matDate);

		for (int i=0;i<holidays.size();i++)
		{
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","holiday date " + holidays.get(i));
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","holiday date " + dateFormat.format((Date)holidays.get(i)));
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","holiday date " + dateFormat.format(matDate));
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","contains " + dateFormat.format((Date)holidays.get(i)).equalsIgnoreCase(dateFormat.format(matDate)));
		}*/
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","contains " + holidays.contains(matDate));
//		maturity date falls on a holiday so keep adding 1 day until the maturity date does not fall on a holiday or sat or sun
/*		while (holidays.contains(matDate) || dayOfWeek == Calendar.SUNDAY)
		{
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","containsp " + holidays.contains(matDate));
			calendar.add(Calendar.DATE, 1);
			matDate = calendar.getTime();
			dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","day " + dayOfWeek);
		}*/
		matDate = calendar.getTime();
		Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","mat date" + matDate);
		invDetail.setMaturityDate(matDate);
//		dynaForm.set("maturityDate", matDate);

		instName=invDetail.getInstrumentName();
		double ytm=0;
		if (!instName.equalsIgnoreCase("Fixed Deposit"))
		{
			double parValue=0;
			if (invDetail.getParValue()>0)
			{
				parValue = invDetail.getParValue();
			}
			else
			{
				parValue = invDetail.getFaceValue();
			}
			double purchasePrice=invDetail.getCostOfPurchase();

			if (((String)dynaForm.get("interestRate")).equals("") || Double.parseDouble((String)dynaForm.get("interestRate"))==0)
			{
				intRate=Double.parseDouble((String)dynaForm.get("couponRate"));
			}
			else
			{
				intRate=Double.parseDouble((String)dynaForm.get("interestRate"));
			}
			
			tenure = invDetail.getTenure();
			tenureType = invDetail.getTenureType();
			if (invDetail.getInstrumentName().equalsIgnoreCase("Commercial Papers"))
			{
				tenureType="M";
			}
			if (tenureType.equalsIgnoreCase("D"))
			{
				tenure=tenure/365;
				tenure=Double.parseDouble(df.format(tenure));
			}
			else if (tenureType.equalsIgnoreCase("M"))
			{
				tenure=tenure/12;
			}
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","ytm calc parvalue" + parValue);
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","ytm calc purchaseprice" + purchasePrice);
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","ytm calc intrate" + intRate);
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","ytm calc tenure" + tenure);
			ytm=calculateYTM(parValue, purchasePrice, intRate, tenure);
			if (new Double(ytm).isNaN())
			{
				ytm=0;
			}
			Log.log(Log.DEBUG,"IFAction","calMaturityDateAmt","ytm value" + df.format(ytm));
		}
		
		invDetail.setYtmValue(ytm);

		BeanUtils.copyProperties(dynaForm, invDetail);
		dynaForm.set("maturityAmount", df.format(matAmt));

		Log.log(Log.INFO,"IFAction","calMaturityDateAmt","Exited");
		return mapping.findForward("display");
	}

	public ActionForward showInvFullfilmentDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Log.log(Log.INFO,"IFAction","showInvFullfilmentDetails","Entered");

		HttpSession session = request.getSession(false);
		session.setAttribute("fullfilmentFlag", "2");

			DynaActionForm dynaForm=(DynaActionForm)form;
//			dynaForm.initialize(mapping);

			getAllInvestees(dynaForm);
			getInstrumentTypes(dynaForm);
			getGenInstrumentTypes(dynaForm);
			getInstrumentFeatures(dynaForm);

			getInstrumentSchemes(dynaForm);
			getInvRefNosForFullfilment(dynaForm);

			InvestmentFulfillmentDetail investmentFulfillmentDetail = new InvestmentFulfillmentDetail();
			BeanUtils.populate(investmentFulfillmentDetail, dynaForm.getMap());
			Log.log(Log.INFO,"IFAction","showInvFullfilmentDetails","ref no " + dynaForm.get("investmentRefNumber"));
			IFProcessor ifProcessor=new IFProcessor();
			investmentFulfillmentDetail = ifProcessor.getInvFullfilmentDetails(investmentFulfillmentDetail);
			session.setAttribute("updateFlag", "0");
			if (investmentFulfillmentDetail!=null)
			{
				session.setAttribute("updateFlag", "1");
				BeanUtils.copyProperties(dynaForm, investmentFulfillmentDetail);
			}
			else
			{
				dynaForm.set("instrumentType","");
				dynaForm.set("instrumentNumber","");
				dynaForm.set("instrumentDate",null);
				dynaForm.set("instrumentAmount","");
				dynaForm.set("drawnBank","");
				dynaForm.set("drawnBranch","");
				dynaForm.set("payableAt","");
			}

			Log.log(Log.INFO,"IFAction","showInvFullfilmentDetails","ref no " + dynaForm.get("investmentRefNumber"));

			Log.log(Log.INFO,"IFAction","showInvFullfilmentDetails","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBuyOrSellInvRefNos(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

			Log.log(Log.INFO,"IFAction","showBuyOrSellInvRefNos","Entered");

			HttpSession session = request.getSession(false);
			session.setAttribute("flag", "1");

				DynaActionForm dynaForm=(DynaActionForm)form;
//				dynaForm.initialize(mapping);

				getAllInvestees(dynaForm);
				getInstrumentTypes(dynaForm);

				ArrayList investmentRefNumbers=new ArrayList();
				String inst = (String) dynaForm.get("instrumentName");
				String inv = (String) dynaForm.get("investeeName");
				if (((String)dynaForm.get("isBuyOrSellRequest")).equalsIgnoreCase("S") && !inst.equals("") && !inv.equals(""))
				{
					IFProcessor ifProcessor=new IFProcessor();
					Log.log(Log.DEBUG, "IFAction", "getInvestmentReferenceNumbers", "inv name  " + inv);
					investmentRefNumbers=ifProcessor.getInvRefNosForBuySell(inst, inv);
				}
				else
				{
					dynaForm.set("noOfUnits", "");
					dynaForm.set("worthOfUnits", "");
				}

				dynaForm.set("investmentRefNumbers",investmentRefNumbers);


				Log.log(Log.INFO,"IFAction","showBuyOrSellInvRefNos","Exited");
				return mapping.findForward(Constants.SUCCESS);
		}

		public ActionForward statementReportInput(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
				Log.log(Log.INFO,"IFAction","statementReportInput","Entered");
		        DynaActionForm dynaForm=(DynaActionForm)form;
				Date date = new Date();
				Calendar calendar =Calendar.getInstance();
				Dates dates =  new Dates();
				dates.setDateOfTheDocument(date);
				BeanUtils.copyProperties(dynaForm, dates);

				Log.log(Log.INFO,"IFAction","statementReportInput","Exited");
		        return mapping.findForward(Constants.SUCCESS);
		}

	public ActionForward showCgpanDetails(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception{

		Log.log(Log.INFO,"IFAction","showCgpanDetails","Entered");

		GMProcessor gmProcessor = new GMProcessor ();
		CgpanDetail cgpanDetail = null;

		String cgpan = (String)request.getParameter("cgpanDetail");
		Log.log(Log.DEBUG,"GMAction","showCgpanDetailsLink","cgpan"+cgpan);
		cgpanDetail = gmProcessor.getCgpanDetails(cgpan);
		DynaActionForm dynaForm=(DynaActionForm)form;
		dynaForm.set("cgpanDtl", cgpanDetail);
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward investmentROI(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
			Log.log(Log.INFO,"IFAction","investmentROI","Entered");

			DynaActionForm dynaForm=(DynaActionForm)form;
			dynaForm.initialize(mapping);
			CustomisedDate customDate=new CustomisedDate();
			customDate.setDate(new Date());
			dynaForm.set("referenceDate",customDate);
			Log.log(Log.INFO,"IFAction","investmentROI","Exited");
			return mapping.findForward(Constants.SUCCESS);
	}
	public ActionForward getInvestmentROI(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
				Log.log(Log.INFO,"IFAction","getInvestmentROI","Entered");

				DynaActionForm dynaForm=(DynaActionForm)form;
				IFProcessor ifProcessor=new IFProcessor();
				Date referenceDate=(Date)dynaForm.get("referenceDate");
				ArrayList rois=ifProcessor.getROI(referenceDate);


				dynaForm.set("rateOfInterests",rois);

				Log.log(Log.INFO,"IFAction","getInvestmentROI","Exited");
				return mapping.findForward(Constants.SUCCESS);
		}
		
		
		public ActionForward showInstCategoryMaster(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
			
			
				DynaActionForm dynaForm=(DynaActionForm)form;
				dynaForm.initialize(mapping);
				
				HttpSession session = request.getSession(false);
			
				IFProcessor ifProcessor=new IFProcessor();
				getInstrumentCategories(dynaForm);

				session.setAttribute("modFlag", "0");

				return mapping.findForward(Constants.SUCCESS);
			}
		
			public ActionForward showModInstCategoryMaster(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
			
			
					DynaActionForm dynaForm=(DynaActionForm)form;
					
					IFProcessor ifProcessor=new IFProcessor();
					InstrumentCategory instCategory = new InstrumentCategory(); 
				
					HttpSession session =request.getSession(false);
					session.setAttribute("modFlag", "1");
					
					String instCatName = (String)dynaForm.get("instrumentCategory");
					
//					dynaForm.initialize(mapping);
					if (instCatName!=null && !instCatName.equals(""))
					{
						instCategory = ifProcessor.getInstCategoryDetails(instCatName);

						dynaForm.set("modInstrumentCat", instCategory.getIctName());
						dynaForm.set("ictDesc", instCategory.getIctDesc());
						dynaForm.set("newInstrumentCat", "");
					}
					else
					{
						dynaForm.set("modInstrumentCat", "");
						dynaForm.set("ictDesc", "");
						dynaForm.set("newInstrumentCat", "");
					}
					return mapping.findForward(Constants.SUCCESS);
				}
				
			private void getInstrumentCategories(DynaActionForm form)  throws DatabaseException
			{
				ArrayList investeeCats=null;
				IFProcessor ifProcessor=new IFProcessor();
				investeeCats=ifProcessor.getInstrumentCategories();
				form.set("instrumentCategories",investeeCats);
			}
				
			public ActionForward saveInstrumentCategory(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception
				{
					Log.log(Log.INFO,"IFAction","saveInstrumentCategory","Entered");
					DynaActionForm dynaForm=(DynaActionForm)form;
					User user=getUserInformation(request);
					String instrumentCat=(String)dynaForm.get("instrumentCategory");
					String newInstrumentCat=(String)dynaForm.get("newInstrumentCat");
					String modInstrumentCat=(String)dynaForm.get("modInstrumentCat");
					String instCatDesc=(String)dynaForm.get("ictDesc");
					InstrumentCategory instCategory = new InstrumentCategory();
					
					instCategory.setIctName(instrumentCat);
					instCategory.setIctNewName(newInstrumentCat);
					instCategory.setIctModName(modInstrumentCat);
					instCategory.setIctDesc(instCatDesc);
					IFProcessor ifProcessor=new IFProcessor();

					ifProcessor.saveInstrumentCategory(instCategory,user.getUserId());

					String message="Instrument Category details saved Successfully";

					request.setAttribute("message",message);
					
					Log.log(Log.INFO,"IFAction","saveInstrumentCategory","Exited");
					return mapping.findForward(Constants.SUCCESS);
				}

		public ActionForward showModTDSFilter(
					ActionMapping mapping,
					ActionForm form,
					HttpServletRequest request,
					HttpServletResponse response)
					throws Exception {
					Log.log(Log.INFO,"IFAction","showModTDSFilter","Entered");

					DynaActionForm dynaForm=(DynaActionForm)form;
					dynaForm.initialize(mapping);
					HttpSession session = request.getSession(false);
					session.setAttribute("flag", "U");

					Date date = new Date();
					Calendar calendar =Calendar.getInstance();
					calendar.setTime(date);
					int month = calendar.get(Calendar.MONTH);
					int day = calendar.get(Calendar.DATE);
					month = month - 1;
					day= day + 1;
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.DATE,day);
					Date prevDate = calendar.getTime();

					Dates generalReport = new Dates();
					generalReport.setTdsStartDate(prevDate);
					generalReport.setTdsEndDate(date);
					BeanUtils.copyProperties(dynaForm, generalReport);

					Log.log(Log.INFO,"IFAction","showModTDSFilter","Exited");
					return mapping.findForward(Constants.SUCCESS);
			}		

			public ActionForward showTDSList(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
					Log.log(Log.INFO,"IFAction","showTDSList","Entered");

					DynaActionForm dynaForm=(DynaActionForm)form;

					Date fromDate = (Date)dynaForm.get("tdsStartDate");
					Date toDate = (Date)dynaForm.get("tdsEndDate");

					IFProcessor ifProcessor = new IFProcessor();
					ArrayList tdsList = ifProcessor.getTDSList(fromDate, toDate);
					if (tdsList.isEmpty())
					{
						throw new MessageException("No TDS Detail available for the given Dates.");
					}
					dynaForm.set("tdsList", tdsList);

					Log.log(Log.INFO,"IFAction","showTDSList","Exited");
					return mapping.findForward("displayList");
				}

			public ActionForward showUpdateTDSDetail(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
					Log.log(Log.INFO,"IFAction","showUpdateTDSDetail","Entered");

					DynaActionForm dynaForm=(DynaActionForm)form;
					TDSDetail tdsDetail = new TDSDetail();

					String tdsId = (String) request.getParameter("id");

					IFProcessor ifProcessor= new IFProcessor();
					tdsDetail = ifProcessor.getTDSDetails(tdsId);

		/*			dynaForm.set("corpusId", corpus.getCorpusId());
					dynaForm.set("corpusContributor", corpus.getCorpusContributor());
					dynaForm.set("corpusAmount", new Double(corpus.getCorpusAmount()));
					dynaForm.set("corpusDate", corpus.getCorpusDate());*/
					BeanUtils.copyProperties(dynaForm, tdsDetail);
					
//					dynaForm.set("tdsId", tdsDetail.getTdsID());
					
					Log.log(Log.INFO,"IFAction","showUpdateTDSDetail","tds id " + dynaForm.get("tdsID"));
					Log.log(Log.INFO,"IFAction","showUpdateTDSDetail","tds id " + tdsDetail.getTdsID());

					Log.log(Log.INFO,"IFAction","showUpdateTDSDetail","Exited");
					return mapping.findForward("displayTDS");
				}

			public ActionForward showMiscReceiptsFilter(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","showMiscReceiptsFilter","Entered");

						InvestmentForm ifForm=(InvestmentForm)form;

						Date date = new Date();
						Calendar calendar =Calendar.getInstance();
						calendar.setTime(date);

						Dates generalReport = new Dates();
						generalReport.setMiscReceiptsDate(date);
						BeanUtils.copyProperties(ifForm, generalReport);

						Log.log(Log.INFO,"IFAction","showMiscReceiptsFilter","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward showMiscReceipts(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
					
					Log.log(Log.INFO,"IFAction","showMiscReceipts","Entered");
					InvestmentForm ifForm=(InvestmentForm)form;
					Date miscDate = ifForm.getMiscReceiptsDate();


					
					ifForm.resetWhenRequired(mapping, request);					
					IFProcessor ifProcessor = new IFProcessor();
					Map receipts = ifProcessor.getMiscReceiptsForDate(miscDate);
					Log.log(Log.DEBUG,"IFAction","showMiscReceipts","size " + receipts.size() );
					ifForm.setMiscReceipts(receipts);
					if (receipts.size()==0)
					{
						request.setAttribute("IsRequired",Boolean.valueOf(true));
					}
					
					Log.log(Log.INFO,"IFAction","showMiscReceipts","Exited");
					return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward addMoreMiscReceipts(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception
				{
					Log.log(Log.INFO,"IFAction","addMoreMiscReceipts","Entered");

					InvestmentForm ifForm=(InvestmentForm)form;

					Map miscReceiptsDetails=ifForm.getMiscReceipts();

					Set miscReceiptsDetailsSet=miscReceiptsDetails.keySet();

					Iterator miscReceiptsDetailsIterator=miscReceiptsDetailsSet.iterator();
					String count=null;
					int counter=0;
					boolean toAdd=false;
					String removeKey="";

					while(miscReceiptsDetailsIterator.hasNext())
					{
						String key=(String)miscReceiptsDetailsIterator.next();

						Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," key "+key);

						count=key.substring(key.indexOf("-")+1,key.length());

						Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," count "+count);
						
						MiscReceipts miscReceipts = (MiscReceipts) miscReceiptsDetails.get(key);
						if (miscReceipts.getId()==0 && (miscReceipts.getSourceOfFund()==null || miscReceipts.getSourceOfFund().equals("")) &&
							(miscReceipts.getInstrumentNo()==null || miscReceipts.getInstrumentNo().equals("")) &&
							(miscReceipts.getInstrumentDate()==null || miscReceipts.getInstrumentDate().toString().equals("")) &&
							(miscReceipts.getDateOfReceipt()==null || miscReceipts.getDateOfReceipt().toString().equals("")))
							{
								toAdd=false;
								removeKey=key;
							}
							else
							{
								toAdd=true;
							}
					}

					Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," counter "+counter);

					if (toAdd)
					{
						request.setAttribute("IsRequired",Boolean.valueOf(true));
					}
					else
					{
						miscReceiptsDetails.remove(removeKey);
						request.setAttribute("IsRequired",Boolean.valueOf(false));

					}

					Log.log(Log.INFO,"IFAction","addMoreMiscReceipts","Exited");

					return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward insertMiscReceipts(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
					
					Log.log(Log.INFO,"IFAction","insertMoreMiscReceipts","Entered");
					
					InvestmentForm ifForm = (InvestmentForm)form;
					Date date = ifForm.getMiscReceiptsDate();

					Map receipts = ifForm.getMiscReceipts();
					Set miscReceiptsDetailsSet=receipts.keySet();

					Iterator miscReceiptsDetailsIterator=miscReceiptsDetailsSet.iterator();
					String count=null;
					int counter=0;
					boolean toAdd=false;
					String removeKey="";

					User user = getUserInformation(request);
					String userId = user.getUserId();
					
					while(miscReceiptsDetailsIterator.hasNext())
					{
						String key=(String)miscReceiptsDetailsIterator.next();

						Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," key "+key);

						count=key.substring(key.indexOf("-")+1,key.length());

						Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," count "+count);
						
						MiscReceipts miscReceipts = (MiscReceipts) receipts.get(key);
						if (miscReceipts.getId()==0 && (miscReceipts.getSourceOfFund()==null || miscReceipts.getSourceOfFund().equals("")) &&
							(miscReceipts.getInstrumentNo()==null || miscReceipts.getInstrumentNo().equals("")) &&
							(miscReceipts.getInstrumentDate()==null || miscReceipts.getInstrumentDate().toString().equals("")) &&
							(miscReceipts.getDateOfReceipt()==null || miscReceipts.getDateOfReceipt().toString().equals("")))
							{
								toAdd=false;
								removeKey=key;
							}
							else
							{
								toAdd=true;
							}
					}

					Log.log(Log.DEBUG,"IFAction","addMoreMiscReceipts"," counter "+counter);

					if (!toAdd)
					{
						receipts.remove(removeKey);
					}
					
					IFProcessor ifProcessor = new IFProcessor();
					ifProcessor.insertMiscReceipts(date, receipts, userId);
					
					String message="Miscellaneous Receipts saved successfully";
					request.setAttribute("message",message);

					Log.log(Log.INFO,"IFAction","insertMoreMiscReceipts","Exited");
					return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward showFundTransferFilter(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","showFundTransferFilter","Entered");

						InvestmentForm ifForm=(InvestmentForm)form;

						Date date = new Date();
						Calendar calendar =Calendar.getInstance();
						calendar.setTime(date);

						Dates generalReport = new Dates();
						generalReport.setStatementDate(date);
						BeanUtils.copyProperties(ifForm, generalReport);

						Log.log(Log.INFO,"IFAction","showFundTransferFilter","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward showFundTransfer(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","showFundTransfer","Entered");

						InvestmentForm ifForm=(InvestmentForm)form;
						ifForm.resetWhenRequired(mapping, request);	

						Date date = ifForm.getStatementDate();
						
						IFProcessor ifProcessor = new IFProcessor();
						Map fundTransfers=ifProcessor.getFundTransfersForDate(date);
						Log.log(Log.DEBUG,"IFAction","showFundTransfer","size " + fundTransfers.size() );
						ifForm.setFundTransfers(fundTransfers);

						Log.log(Log.INFO,"IFAction","showFundTransfer","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward updateFundTransfer(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
					
					Log.log(Log.INFO,"IFAction","updateFundTransfer","Entered");
					
					InvestmentForm ifForm = (InvestmentForm)form;
					Date date = ifForm.getStatementDate();

					Map ftDetails = ifForm.getFundTransfers();
					Map addFtDetails = new TreeMap();
					Set ftSet=ftDetails.keySet();

					Iterator ftIterator=ftSet.iterator();
					String count=null;
					int counter=0;
					boolean toAdd=false;
					ArrayList removeKey= new ArrayList();

					User user = getUserInformation(request);
					String userId = user.getUserId();
					
					while(ftIterator.hasNext())
					{
						String key=(String)ftIterator.next();

						Log.log(Log.DEBUG,"IFAction","updateFundTransfer"," key "+key);

						count=key.substring(key.indexOf("-")+1,key.length());

						Log.log(Log.DEBUG,"IFAction","updateFundTransfer"," count "+count);
						
						FundTransferDetail fundTransfer = (FundTransferDetail) ftDetails.get(key);
						if (fundTransfer.getId()==0 && (fundTransfer.getClosingBalanceDate()==null || fundTransfer.getClosingBalanceDate().equals("")) &&
							(fundTransfer.getBalanceAsPerStmt()==null || fundTransfer.getBalanceAsPerStmt().equals("")) &&
							(fundTransfer.getUnclearedBalance()==null || fundTransfer.getUnclearedBalance().equals("")) &&
							(fundTransfer.getAmtCANotReflected()==null || fundTransfer.getAmtCANotReflected().equals("")))
							{
								Log.log(Log.DEBUG,"IFAction","updateFundTransfer"," remove "+key);
								toAdd=false;
								removeKey.add(key);
							}
							else
							{
								addFtDetails.put(key, fundTransfer);
							}
					}

					Log.log(Log.DEBUG,"IFAction","updateFundTransfer"," counter "+counter);

/*					if (!toAdd)
					{
						for (int i=0; i<removeKey.size(); i++)
						{
							ftDetails.remove(removeKey.get(i));
						}
					}*/
					Log.log(Log.DEBUG,"IFAction","updateFundTransfer"," counter "+addFtDetails.size());
					
					IFProcessor ifProcessor = new IFProcessor();
					ifProcessor.updateFundTransfers(date, addFtDetails, userId);
					
					String message="Fund Transfers saved successfully";
					request.setAttribute("message",message);

					Log.log(Log.INFO,"IFAction","updateFundTransfer","Exited");
					return mapping.findForward(Constants.SUCCESS);
				}
				
			public ActionForward showClaimProjection(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							DynaActionForm dynaForm = (DynaActionForm)form;
							
							dynaForm.initialize(mapping);
							
							return mapping.findForward(Constants.SUCCESS);
						}				
				
			public ActionForward showClaimProjectionDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","Entered");
							
							IFProcessor ifProcessor = new IFProcessor();
							
							DynaActionForm dynaForm = (DynaActionForm)form;
							
							String forward = "";
							Date filterDate = (Date)dynaForm.get("filterDate");							
							
							if(dynaForm.get("npaAccounts").equals("N"))
							{
								ArrayList projectionMap = (ArrayList)ifProcessor.getApplicationsForProjection(filterDate);
								
									dynaForm.set("nonNPAAccountsMap",projectionMap.get(0));		
									
									Map map = 	(Map)projectionMap.get(0);
									Set set = map.keySet();
									Iterator iterator = set.iterator();
									while(iterator.hasNext())
									{
										String key = (String)iterator.next();
										Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","key :" + key);
										Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","valu :" + map.get(key));
												
									}
									dynaForm.set("nonNPAAccountsArrayList",projectionMap.get(1));
									
									forward = "nonNPAPage";
								
							}
							else if(dynaForm.get("npaAccounts").equals("A"))
							{
								ArrayList npaProjectionMap = (ArrayList)ifProcessor.getApplicationsForNPAAccounts(filterDate);
								
								dynaForm.set("npaAccountsMap",npaProjectionMap.get(0));						
								dynaForm.set("npaAccountsArrayList",npaProjectionMap.get(1));
								
								for(int i=0; i<((ArrayList)npaProjectionMap.get(1)).size(); i++)
								{
									String year = (String)((ArrayList)npaProjectionMap.get(1)).get(i);
									Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","year :" + year);
								}
								

								forward = "nonNPAPage";
							}
							else if(dynaForm.get("npaAccounts").equals("B"))
							{
								ArrayList projectionMap = (ArrayList)ifProcessor.getApplicationsForProjection(filterDate);
								
									dynaForm.set("nonNPAAccountsMap",projectionMap.get(0));						
									dynaForm.set("nonNPAAccountsArrayList",projectionMap.get(1));
									
								ArrayList npaProjectionMap = (ArrayList)ifProcessor.getApplicationsForNPAAccounts(filterDate);
								
								dynaForm.set("npaAccountsMap",npaProjectionMap.get(0));						
								dynaForm.set("npaAccountsArrayList",npaProjectionMap.get(1));
									
								for(int i=0; i<((ArrayList)npaProjectionMap.get(1)).size(); i++)
								{
									String year = (String)((ArrayList)npaProjectionMap.get(1)).get(i);
									Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","year :" + year);
								}
									forward = "nonNPAPage";
								
							}
							
							Log.log(Log.INFO,"IFAction","showClaimProjectionDetails","Exited");
							return mapping.findForward(forward);
						}
						
			private String getMonth(int calendarMonth)
			{
				String month = "";
				if(calendarMonth==0)
				{
					month="JAN";
				}
				else if(calendarMonth==1){
					
					month="FEB";
				}
				else if(calendarMonth==2){
					
					month="MAR";
				}
				else if(calendarMonth==3){
					
					month="APR";
				}
				else if(calendarMonth==4){
					
					month="MAY";
				}
				else if(calendarMonth==5){
					
					month="JUN";
				}
				else if(calendarMonth==6){
					
					month="JUL";
				}
				else if(calendarMonth==7){
					
					month="AUG";
				}
				else if(calendarMonth==8){
					
					month="SEP";
				}
				else if(calendarMonth==9){
					
					month="OCT";
				}
				else if(calendarMonth==10){
					
					month="NOV";
				}
				else if(calendarMonth==11){
					
					month="DEC";
				}

				return month;
			}


			public ActionForward showVoucherDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showVoucherDetails","Entered");
							DynaActionForm ifForm=(DynaActionForm)form;
							
							ifForm.initialize(mapping);
							
							return mapping.findForward(Constants.SUCCESS);
						}
						
										
			public ActionForward generateVoucherForInv(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","generateVoucherForInv","Entered");
							DynaActionForm ifForm=(DynaActionForm)form;
							
							IFProcessor ifProcessor = new IFProcessor();
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							User user = getUserInformation(request) ;
							String contextPath = request.getSession(false).getServletContext().getRealPath("");
							
							Date fromDate = (Date)ifForm.get("voucherFromDate");
							Log.log(Log.INFO,"IFAction","showVoucherDetails","fromDate :" + fromDate);
							
							Date toDate = (Date)ifForm.get("voucherToDate");							
							
							if(toDate==null || toDate.toString().equals(""))
							{
								Log.log(Log.INFO,"IFAction","showVoucherDetails","before toDate :" + toDate);
								toDate = new java.util.Date();
								
								Log.log(Log.INFO,"IFAction","showVoucherDetails","after toDate :" + toDate);
							}
							Log.log(Log.INFO,"IFAction","showVoucherDetails","toDate :" + toDate);
							
							ArrayList invVoucherDetails = ifProcessor.getMaturedVoucherDetails(fromDate,toDate);
							
							if(invVoucherDetails==null || invVoucherDetails.size()==0)
							{
								throw new MessageException("There are no Instruments available for Voucher Generation");
							}
							
							Log.log(Log.INFO,"IFAction","showVoucherDetails","cinvVoucherDetails :" + invVoucherDetails.size());
							
							ifProcessor.insertVoucherInvDetails(invVoucherDetails,fromDate,toDate,contextPath,user);
							Log.log(Log.INFO,"IFAction","showVoucherDetails","Exited");
							
							request.setAttribute("message","Vouchers Generated Successfully");
							
							return mapping.findForward(Constants.SUCCESS);
						}
						
			public ActionForward showBankReconFilter(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","showBankReconFilter","Entered");

						DynaActionForm ifForm=(DynaActionForm)form;

						Date date = new Date();
						Calendar calendar =Calendar.getInstance();
						calendar.setTime(date);

						Dates generalReport = new Dates();
						generalReport.setReconDate(date);
						BeanUtils.copyProperties(ifForm, generalReport);

						Log.log(Log.INFO,"IFAction","showBankReconFilter","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward showBankRecon(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","showBankRecon","Entered");

						DynaActionForm ifForm=(DynaActionForm)form;

						Date date = (Date)ifForm.get("reconDate");
						
						IFProcessor ifProcessor = new IFProcessor();
						BankReconcilation bankReconcilation=ifProcessor.getBankReconDetails(date);
						BeanUtils.copyProperties(ifForm, bankReconcilation);

						Log.log(Log.INFO,"IFAction","showBankRecon","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}

			public ActionForward updateBankRecon(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","updateBankRecon","Entered");

						DynaActionForm ifForm=(DynaActionForm)form;

						Date date = (Date)ifForm.get("reconDate");
						BankReconcilation bankReconcilation=new BankReconcilation();
						BeanUtils.populate(bankReconcilation, ifForm.getMap());
						
						Log.log(Log.DEBUG,"IFAction","updateBankRecon","1 "+bankReconcilation.getCgtsiBalance());
						Log.log(Log.DEBUG,"IFAction","updateBankRecon","2 "+bankReconcilation.getChequeIssuedAmount());
						Log.log(Log.DEBUG,"IFAction","updateBankRecon","3 "+bankReconcilation.getDirectCredit());
						Log.log(Log.DEBUG,"IFAction","updateBankRecon","4 "+bankReconcilation.getDirectDebit());
						Log.log(Log.DEBUG,"IFAction","updateBankRecon","5 "+bankReconcilation.getClosingBalanceIDBI());

						User user = getUserInformation(request);
						String userId = user.getUserId();
						
						IFProcessor ifProcessor = new IFProcessor();
						ifProcessor.updateBankRecon(date, bankReconcilation, userId);
						
						request.setAttribute("message", "Bank Reconciliation Details saved successfully");

						Log.log(Log.INFO,"IFAction","updateBankRecon","Exited");
						return mapping.findForward(Constants.SUCCESS);
				}
				

			public ActionForward showDtlsForChqLeavesInsert(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							DynaActionForm ifForm = (DynaActionForm)form;
							
							IFProcessor ifProcessor = new IFProcessor();
							
							ArrayList bankNames = new ArrayList();
							
							ifForm.initialize(mapping);
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesInsert","Entered");
							
							ArrayList bankDetails = ifProcessor.getBankAccounts();
							
							for(int i=0; i<bankDetails.size(); i++)
							{
								BankAccountDetail bankAccountDetail = (BankAccountDetail)bankDetails.get(i);
								String bankName = bankAccountDetail.getBankName();
								String branchName = bankAccountDetail.getBankBranchName();
								
								String bankBranchName = bankName + "," + branchName + "(" + bankAccountDetail.getAccountNumber() + ")";
								bankNames.add(bankBranchName);
							}
							
							ifForm.set("bankAcctDetails",bankNames);
							
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesInsert","Exited");
							return mapping.findForward(Constants.SUCCESS);
						}
				
			public ActionForward insertChqDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
						Log.log(Log.INFO,"IFAction","insertChqDetails","Entered");
						
						DynaActionForm ifForm=(DynaActionForm)form;
						
						ChequeLeavesDetails chequeLeavesDetails = new ChequeLeavesDetails();
						
						Log.log(Log.INFO,"IFAction","insertChqDetails","banrkBranchName :" + ifForm.get("bnkName"));
						Log.log(Log.INFO,"IFAction","insertChqDetails","start no :" + ifForm.get("chqStartNo"));
						Log.log(Log.INFO,"IFAction","insertChqDetails","end no :" + ifForm.get("chqEndingNo"));
						//Log.log(Log.INFO,"IFAction","insertChqDetails","bnk Name :" + ifForm.get("chqEndingNo"));
						
						BeanUtils.populate(chequeLeavesDetails, ifForm.getMap());

						User user = getUserInformation(request);
						String userId = user.getUserId();
						
						
						String bankBranchName = (String)ifForm.get("bnkName");
						int start=bankBranchName.indexOf(",");
						String bankName = bankBranchName.substring(0,start);
						chequeLeavesDetails.setChqBankName(bankName);
						
						int start1=bankBranchName.indexOf("(");
						int finish1=bankBranchName.indexOf(")");					
						
						
						String branchName =  bankBranchName.substring(start + 1,start1);						
						chequeLeavesDetails.setChqBranchName(branchName);
						
						
						String bnkAcctNo = bankBranchName.substring(start1 + 1,finish1);
						
						Log.log(Log.INFO,"IFAction","insertChqDetails","bnkAcctNo :" + bnkAcctNo);						
						
						chequeLeavesDetails.setBnkAccountNumber(bnkAcctNo);
						
						IFProcessor ifProcessor = new IFProcessor();						
						ifProcessor.insertChqLeavesDetails(chequeLeavesDetails, user);
						
						request.setAttribute("message","Cheque Details Inserted Successfully");
						
						Log.log(Log.INFO,"IFAction","insertChqDetails","Exited");
						
						return mapping.findForward(Constants.SUCCESS);
						}
						
			public ActionForward showDtlsForChqLeavesUpdate(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Entered");
							
							InvestmentForm actionForm = (InvestmentForm)form;
							
							actionForm.resetMaps();
							
							IFProcessor ifProcessor = new IFProcessor();
							
							ArrayList bankNames = new ArrayList();							
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Entered");
							
							ArrayList bankDetails = ifProcessor.getBankAccounts();
							
							actionForm.setBankBranchName("");
							
							String bankName = "";
							String branchName = "";
							String bankBranchName = "";
							
							for(int i=0; i<bankDetails.size(); i++)
							{
								BankAccountDetail bankAccountDetail = (BankAccountDetail)bankDetails.get(i);
								bankName = bankAccountDetail.getBankName();
								branchName = bankAccountDetail.getBankBranchName();
								
								bankBranchName = bankName + "," + branchName + "(" + bankAccountDetail.getAccountNumber() + ")";
								bankNames.add(bankBranchName);
							}
							
							actionForm.setBnkChqDetails(bankNames);
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Exited");
						
							return mapping.findForward(Constants.SUCCESS);
						}
						

			public ActionForward showBankChqDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Entered");
							
							InvestmentForm actionForm = (InvestmentForm)form;
							
							IFProcessor ifProcessor = new IFProcessor();
							
							TreeMap startNoMap = new TreeMap();
							TreeMap endNoMap = new TreeMap();
							
							ArrayList bankNames = new ArrayList();
							
							String bankBranchName = actionForm.getBankBranchName();
							int start=bankBranchName.indexOf(",");
							String bankName = bankBranchName.substring(0,start);
							
							int start1=bankBranchName.indexOf("(");
							int finish1=bankBranchName.indexOf(")");					
						
							String branchName =  bankBranchName.substring(start + 1,start1);
							
							ArrayList bnkChqDetails = new ArrayList(); 
							
							String bnkAcctNo = bankBranchName.substring(start1 + 1,finish1);
														
							bnkChqDetails = ifProcessor.getBankChqLeavesDetails(bankName,branchName,bnkAcctNo);
							
							if(bnkChqDetails==null || bnkChqDetails.size()==0)
							{
								throw new MessageException("There are no Cheque Leaves For Updation");
							}
							
							for(int i=0; i<bnkChqDetails.size(); i++)
							{
								ChequeLeavesDetails chequeLeavesDetails = (ChequeLeavesDetails)bnkChqDetails.get(i);
								int chqId = chequeLeavesDetails.getChqId();
								int startNo = chequeLeavesDetails.getChqStartNo();
								int endNo = chequeLeavesDetails.getChqEndingNo();
								
								String key="key-" + i;
								
								startNoMap.put(key,new Integer(startNo));
								endNoMap.put(key,new Integer(endNo));
								
								actionForm.setStartNo(startNoMap);
								actionForm.setEndNo(endNoMap);
								
							}
							
							actionForm.setBnkChqDetails(bnkChqDetails);
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Exited");
							
							return mapping.findForward(Constants.SUCCESS);
							
						}
						
			public ActionForward saveUpdatedChqDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Entered");
							
							InvestmentForm actionForm = (InvestmentForm)form;
							
							IFProcessor ifProcessor = new IFProcessor();
							
							User user = getUserInformation(request);
							
							Map chqId= new TreeMap();
							
							chqId = actionForm.getChequeId();
							Map startNo=actionForm.getStartNo();
							Map endNo=actionForm.getEndNo();							
							Map bankId=actionForm.getBankId();

							Set chqIdSet=chqId.keySet();
							Set startNoSet=startNo.keySet();
							Set endNoSet=endNo.keySet();

							Iterator chqIdSetIterator=chqIdSet.iterator();
							
							while(chqIdSetIterator.hasNext())
							{
								ChequeLeavesDetails chqLeavesDetails = new ChequeLeavesDetails(); 
								String key = (String)chqIdSetIterator.next();
								
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","key :" + key);
								int chequeId = Integer.parseInt((String)chqId.get(key));
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","chequeId :" + chequeId);
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","cqlBankId :" + (String)bankId.get(key));
								int cqlBankId = Integer.parseInt((String)bankId.get(key));
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","cqlBankId :" + cqlBankId);
								int startNumber = Integer.parseInt((String)startNo.get(key));
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","startNumber :" + startNumber);
								int endNumber = Integer.parseInt((String)endNo.get(key));
								Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","endNumber :" + endNumber);
								
								chqLeavesDetails.setChqId(chequeId);
								chqLeavesDetails.setChqBankId(cqlBankId);
								chqLeavesDetails.setChqStartNo(startNumber);
								chqLeavesDetails.setChqEndingNo(endNumber);
								
								ifProcessor.updateChqLeavesDetails(chqLeavesDetails,user);
								
							}
							
							
							Log.log(Log.INFO,"IFAction","showDtlsForChqLeavesUpdate","Exited");
							
							//actionForm.setBankBranchName("");
							
							request.setAttribute("message","Cheque Details Updated Successfully");
							
							return mapping.findForward(Constants.SUCCESS);							
						}



			public ActionForward showUnUtilizedChqDetails(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","showUnUtilizedChqDetails","Entered");
							
							InvestmentForm actionForm = (InvestmentForm)form;
							
							//actionForm.resetMaps();
							
							IFProcessor ifProcessor = new IFProcessor();
							
							ArrayList bankNames = new ArrayList();
							
							
							String bankBranchName = actionForm.getBankBranchName();
							int start=bankBranchName.indexOf(",");
							String bankName = bankBranchName.substring(0,start);	
													
							int start1=bankBranchName.indexOf("(");
							int finish1=bankBranchName.indexOf(")");					
						
							String branchName =  bankBranchName.substring(start + 1,start1);
							
							String bnkAcctNo = bankBranchName.substring(start1 + 1,finish1);							
							
							ArrayList unutilizedChqDetails = ifProcessor.getUnUtilizedChqLeaves(bankName,branchName,bnkAcctNo);
							
							if(unutilizedChqDetails==null || unutilizedChqDetails.size()==0)
							{
								throw new MessageException("There are no UnUtilized Cheque Leaves For Conversion");
							}
							
							actionForm.setUnutilizedChqDetails(unutilizedChqDetails);
							
							//actionForm.resetMaps();
							
							Log.log(Log.INFO,"IFAction","showUnUtilizedChqDetails","Exited");
							
							return mapping.findForward(Constants.SUCCESS);
							
						}
						
			public ActionForward saveCancelledCheques(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							Log.log(Log.INFO,"IFAction","saveCancelledCheques","Exited");
							
							InvestmentForm actionForm = (InvestmentForm)form;
							
							IFProcessor ifProcessor = new IFProcessor();
							
							String bankBranchName = actionForm.getBankBranchName();
							int start=bankBranchName.indexOf(",");
							String bankName = bankBranchName.substring(0,start);
							
							int start1=bankBranchName.indexOf("(");
							int finish1=bankBranchName.indexOf(")");					
						
							String branchName =  bankBranchName.substring(start + 1,start1);
							
							String bnkAcctNo = bankBranchName.substring(start1 + 1,finish1);
							
							User user = getUserInformation(request);
							
							Map chqId=actionForm.getChequeId();
							
							Map cancelledChq=actionForm.getCancelledChq();

							Set chqIdSet=chqId.keySet();

							Iterator chqIdSetIterator=chqIdSet.iterator();
							
							while(chqIdSetIterator.hasNext())
							{								
								ChequeDetails chequeDetails = new ChequeDetails();
								 
								chequeDetails.setBankName(bankName);
								chequeDetails.setBranchName(branchName);
								
								Log.log(Log.INFO,"IFAction","saveCancelledCheques","bankName :" + bankName);
								Log.log(Log.INFO,"IFAction","saveCancelledCheques","branchName :" + branchName);
								
								String key = (String)chqIdSetIterator.next();
								
								Log.log(Log.INFO,"IFAction","saveCancelledCheques","key :" + key);
								int chequeId = Integer.parseInt((String)chqId.get(key));
								
								Log.log(Log.INFO,"IFAction","saveCancelledCheques","chequeId :" + chequeId);
								
								chequeDetails.setChqNumber(chequeId);
								
								String cancelledFlag = (String)cancelledChq.get(key);
								
								if(cancelledFlag!=null && cancelledFlag.equals("Y"))
								{
									ifProcessor.saveCancelledChqDetails(chequeDetails,user);
								}
							}							
							
							
							Log.log(Log.INFO,"IFAction","saveCancelledCheques","Exited");
							
							actionForm.setBankBranchName("");
							
							request.setAttribute("message","Cheque Cancelled Successfully");
							
							return mapping.findForward(Constants.SUCCESS);
						}						


			private double calculateYTM(double parValue,double purchacePrice,
													double couponRate,double maturityYears)
			{
					double z = couponRate/100;
					double c = couponRate*parValue/100;
					double r=couponRate/100;
					double E = .00001;

					for (int i = 0; i < 100; i++)
					{
						if (Math.abs(fYTM(z,purchacePrice,c,parValue,maturityYears)) < E) break;

						while (Math.abs(dfYTM(z,purchacePrice,c,parValue,maturityYears)) < E) z+= .1;
						z = z - (fYTM(z,purchacePrice,c,parValue,maturityYears)/dfYTM(z,purchacePrice,c,parValue,maturityYears));
					}
					if (Math.abs(fYTM(z,purchacePrice,c,parValue,maturityYears)) >= E) return -1;  // error
					return (1/z) - 1;

			}

			private double fYTM(double z,double p,double c,double b,double y)
			{
				return (c + b)*Math.pow(z,y+1) - b*Math.pow(z,y) - (c+p)*z + p;
			}
			private double dfYTM(double z,double p,double c,double b,double y)
			{
				return (y+1)*(c + b)*Math.pow(z,y) - y*b*Math.pow(z,y - 1) - (c+p);
			}
			
			public ActionForward showRatingsForAgencyName(
						ActionMapping mapping,
						ActionForm form,
						HttpServletRequest request,
						HttpServletResponse response)
						throws Exception {
							
							DynaActionForm actionForm = (DynaActionForm)form;
							String agencyName = (String)actionForm.get("ratingAgency");
							
							IFProcessor ifProcessor = new IFProcessor();
							
							ArrayList ratingsList = new ArrayList();
							if(agencyName!=null && !agencyName.equals(""))
							{
								ratingsList = ifProcessor.getRatingsForAgency(agencyName);
								
								Log.log(Log.INFO,"IFAction","showRatingsForAgencyName","ratingsList :" + ratingsList.size());
							}
							
							actionForm.set("instrumentRatings",ratingsList);
							
							return mapping.findForward(Constants.SUCCESS);
						}			
			

}
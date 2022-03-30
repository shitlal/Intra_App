/*************************************************************
 *
 * Name of the class: GMAction.java
 * This class handles all the requests pertaining to GuaranteeMaintenance.
 *
 *
 * @author : Gowri Shankar.K.U
 * @version:
 * @since: Nov 18, 2003
 ***************************************************************/
package com.cgtsi.action;

import com.cgtsi.application.*;

import com.cgtsi.guaranteemaintenance.GMDAO;
import com.cgtsi.receiptspayments.DANSummary;
import com.cgtsi.receiptspayments.MissingDANDetailsException;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.reports.ApplicationReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Connection;

import com.cgtsi.util.DBConnection;

import java.sql.SQLException;

import java.io.ObjectOutputStream;

import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.cgtsi.reports.ReportManager;

import java.util.Vector;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.commons.beanutils.BeanUtils;

import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.admin.MenuOptions;
import com.cgtsi.claim.ExportFailedException;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MailerException;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;

import com.cgtsi.guaranteemaintenance.GMProcessor;

import com.cgtsi.guaranteemaintenance.DisbursementAmount;

import com.cgtsi.guaranteemaintenance.ClosureDetail;
import com.cgtsi.registration.NoMemberFoundException;

import com.cgtsi.util.CustomisedDate;
import com.cgtsi.util.PropertyLoader;
import com.cgtsi.util.SessionConstants;
import com.cgtsi.guaranteemaintenance.BorrowerInfo;
import com.cgtsi.guaranteemaintenance.CgpanDetail;
import com.cgtsi.guaranteemaintenance.CgpanInfo;
import com.cgtsi.guaranteemaintenance.Disbursement;
import com.cgtsi.guaranteemaintenance.GMConstants;
import com.cgtsi.guaranteemaintenance.OutstandingAmount;
import com.cgtsi.guaranteemaintenance.OutstandingDetail;
import com.cgtsi.guaranteemaintenance.PeriodicInfo;
import com.cgtsi.guaranteemaintenance.RecoveryProcedure;
import com.cgtsi.guaranteemaintenance.RecoveryProcedureTemp;
import com.cgtsi.guaranteemaintenance.Repayment;
import com.cgtsi.guaranteemaintenance.RepaymentAmount;
import com.cgtsi.guaranteemaintenance.RepaymentSchedule;
import com.cgtsi.guaranteemaintenance.Recovery;
import com.cgtsi.guaranteemaintenance.NPADetails;
import com.cgtsi.guaranteemaintenance.LegalSuitDetail;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.admin.AdminConstants;
import com.cgtsi.admin.AdminDAO;
import com.cgtsi.admin.AdminHelper;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.PasswordManager;
import com.cgtsi.admin.User;
import com.cgtsi.claim.CPDAO;
import com.cgtsi.claim.DtlsAsOnDateOfNPA;
import com.cgtsi.claim.DtlsAsOnDateOfSanction;
import com.cgtsi.common.Constants;

import com.cgtsi.guaranteemaintenance.TCLoanDetails;

import com.cgtsi.guaranteemaintenance.WCLoanDetails;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import java.util.Calendar;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.validator.DynaValidatorActionForm;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class GMAction extends BaseAction {

	AdminHelper adminHelper;

	public OutstandingDetail getOutstandingDetailsForCgpan(String cgpan)
			throws NoDataException, DatabaseException, SQLException {
		Log.log(Log.INFO, "GMDAO", "getOutstandingDetailsForCgpan", "Entered");
		GMProcessor gmProcessor = new GMProcessor();
		OutstandingDetail outstandingDetail = null;
		outstandingDetail = gmProcessor.getOutstandingDetailsForCgpan(cgpan);
		Log.log(Log.INFO, "GMDAO", "getOutstandingDetailsForCgpan", "Exited");
		return outstandingDetail;
 
	}

	//rajuk
	 public ActionForward cgpanForReduction(ActionMapping mapping,
	           ActionForm form,
	           HttpServletRequest request,
	           HttpServletResponse response
	           )throws Exception {


	DynaActionForm dynaForm = (DynaActionForm) form;
	dynaForm.initialize(mapping);
	return mapping.findForward("success");
	}
	//rajuk
	 
	 
	 public ActionForward cgpanForReductionprocess(ActionMapping mapping,
	           ActionForm form,
	           HttpServletRequest request,
	           HttpServletResponse response
	           )throws Exception 
	           {


       DynaActionForm dynaForm     = (DynaActionForm)form;
       String cgpan                = (String) dynaForm.get("enterCgpan");
    
      // System.out.println("cgpan====="+cgpan);  
       
     
	    ResultSet rs = null;
	    ResultSet res=null;
	    PreparedStatement pst = null;
	    int cnt=0;

      ArrayList aList = new ArrayList();

      Connection connection = DBConnection.getConnection();
	     try { 
      String query = "select CGPAN,nvl(WCP_FB_LIMIT_SANCTIONED,0),nvl(WCP_NFB_LIMIT_SANCTIONED,0),nvl(WCP_FB_CREDIT_TO_GUARANTEE,0),nvl(WCP_NFB_CREDIT_TO_GUARANTEE,0) from WORKING_CAPITAL_detail where CGPAN='"+ cgpan + "'";
      pst = connection.prepareStatement(query);
      rs = pst.executeQuery(query);
     // System.out.println("query===1=="+query);
  
      String query1="SELECT count(*) FROM REDUCTION_WORKING_STAGING WHERE   CHECKER_RED_DT >= SYSDATE - 1 AND MAKER_CHEKER_FLAG='CA' and CGPAN='"+ cgpan + "'"; 
     // System.out.println("query5"+query1);
      pst = connection.prepareStatement(query1);
      res = pst.executeQuery(query1);
      
      ApplicationReport appReport = new ApplicationReport();
      while(res.next() )
      {
    	  cnt=res.getInt(1);
    	  
      }
      
  	
      while (rs.next()) {
    	  if(cnt==0){
      	appReport.setCGPAN(rs.getString(1));
      	
		   appReport.setWCP_FB_LIMIT_SANCTIONED(rs.getString(2));
		   appReport.setWCP_NFB_LIMIT_SANCTIONED(rs.getString(3));
		   appReport.setWCP_FB_CREDIT_TO_GUARANTEE(rs.getString(4));
		   appReport.setWCP_NFB_CREDIT_TO_GUARANTEE(rs.getString(5));
		   
			aList.add(appReport);
			  dynaForm.set("cgpanHistoryReport",aList);
    	  }
    	  
    	 else {
				/*request.setAttribute("message",
						"<b>Reduction already done.Kindly request after 24 hours.</b>");*/
				return mapping.findForward("showerror");
			}
    	  
    	
	     //  }
		
    	 /* request.setAttribute("message",
			"<b>Reduction already done.Kindly request after 24 hours.</b>");*/

      }
	     }
	
		 catch (Exception exception) {
		 		Log.logException(exception);
		 		throw new DatabaseException(exception.getMessage());
		 		}
		 	    finally 
		 	    {
		 	    	if(pst != null)
		 	    		pst.close();
		 		DBConnection.freeConnection(connection);
		 		}
		 	    
		 	
		 			
		return mapping.findForward(Constants.SUCCESS);
  
        

	}

	 public ActionForward saveCgpanReductiondetails( ActionMapping mapping,  ActionForm form,  HttpServletRequest request,  HttpServletResponse response) throws Exception {
	         User user = this.getUserInformation(request);
	         String userid = user.getUserId();
	        System.out.println("userid" + userid);
	         DynaActionForm dynaForm = (DynaActionForm)form;
	         String cgpan = (String)dynaForm.get("cgpanr");
	         String wcPFBLimitsanctioned = (String)dynaForm.get("wcpfb");
	         String wcpNFBLimitsancioned = (String)dynaForm.get("wcpnfb");
	         String wcPFBCredittoguarantee = (String)dynaForm.get("wcpfbc");
	         String wcpNFBCredittoguarantee = (String)dynaForm.get("wcpnfbc");
	         int Totalapporved = Integer.parseInt(wcPFBLimitsanctioned) + Integer.parseInt(wcpNFBLimitsancioned);
	         String remarks = (String)dynaForm.get("appremarks");
	         String mliRequestDate = (String)dynaForm.get("mliRequestDate");
	        ResultSet rs = null;
	        ResultSet rs1 = null;
	        ResultSet res = null;
	        PreparedStatement pst = null;
	         Connection connection = DBConnection.getConnection();
	        int Cnt = 0;
	         int count = 0;
	        try {
	             String query = "select  * FROM WORKING_CAPITAL_detail   where cgpan = '" + cgpan + "' ";
	            pst = connection.prepareStatement(query);
	            rs = pst.executeQuery(query);
	             String query1 = "select count(*) FROM REDUCTION_WORKING_STAGING  where cgpan = '" + cgpan + "' and  MAKER_CHEKER_FLAG='MA'";
	            pst = connection.prepareStatement(query1);
	            res = pst.executeQuery(query1);
	            while (res.next()) {
	                Cnt = res.getInt(1);
	            }
	            while (rs.next()) {
	                if (Cnt == 0) {
	                     String querypriviliges1 = "INSERT INTO REDUCTION_WORKING_STAGING(CGPAN,WCP_FB_LIMIT_SANCTIONED,WCP_NFB_LIMIT_SANCTIONED,WCP_FB_CREDIT_TO_GUARANTEE,WCP_NFB_CREDIT_TO_GUARANTEE,MAKER_USER_ID,MAKER_RED_DT,MAKER_REMARKS,OLD_WCP_FB_LIMIT_SANCTIONED,OLD_WCP_NFB_LIMIT_SANCTIONED,OLD_WCP_FB_CREDIT_TO_GUR,OLD_WCP_NFB_CREDIT_TO_GUR,MAKER_CHEKER_FLAG,MLI_REQUEST_DT) VALUES ('" + cgpan + "','" + wcPFBLimitsanctioned + "','" + wcpNFBLimitsancioned + "','" + wcPFBCredittoguarantee + "','" + wcpNFBCredittoguarantee + "','" + userid + "',SYSDATE" + ",'" + remarks + "','" + rs.getString("WCP_FB_LIMIT_SANCTIONED") + "','" + rs.getString("WCP_NFB_LIMIT_SANCTIONED") + "','" + rs.getString("WCP_FB_CREDIT_TO_GUARANTEE") + "','" + rs.getString("WCP_NFB_CREDIT_TO_GUARANTEE") + "','MA'" + ",to_date('" + mliRequestDate + "','dd/MM/yyyy'))";
	                    System.out.println("InsertQuery is==" + querypriviliges1);
	                    rs1 = pst.executeQuery(querypriviliges1);
	                }
	                else {
	                     String query3 = "update REDUCTION_WORKING_STAGING set WCP_FB_LIMIT_SANCTIONED = '" + wcPFBLimitsanctioned + "',WCP_NFB_LIMIT_SANCTIONED = '" + wcpNFBLimitsancioned + "',WCP_FB_CREDIT_TO_GUARANTEE='" + wcPFBCredittoguarantee + "'," + " WCP_NFB_CREDIT_TO_GUARANTEE='" + wcpNFBCredittoguarantee + "',MAKER_USER_ID='" + userid + "',MAKER_REMARKS='" + remarks + "', MAKER_RED_DT=SYSDATE,MAKER_CHEKER_FLAG='MA' , MLI_REQUEST_DT=to_date('" + mliRequestDate + "','dd/MM/yyyy') where cgpan = '" + cgpan + "' and MAKER_CHEKER_FLAG='MA'";
	                    pst = connection.prepareStatement(query3);
	                    pst.executeUpdate(query3);
	                }
	            }
	        }
	        catch (Exception exception) {
	            Log.logException((Throwable)exception);
	            throw new DatabaseException(exception.getMessage());
	        }
	        finally {
	            DBConnection.freeConnection(connection);
	        }
	       
	        return mapping.findForward("success");
	    }
	
//end rajuk	
	
	public Disbursement getDisbursementDetailsForCgpan(String cgpan)
			throws DatabaseException {
		Log.log(Log.INFO, "GMProcessor", "getDisbursementDetailsForCgpan",
				"Entered");
		Disbursement disbursement = null;
		GMProcessor gmProcessor = new GMProcessor();
		disbursement = gmProcessor.getDisbursementDetailsForCgpan(cgpan);
		Log.log(Log.INFO, "GMProcessor", "getDisbursementDetailsForCgpan",
				"Exited");
		return disbursement;

	}

	public Repayment getRepaymentDetailsForCgpan(String cgpan)
			throws DatabaseException {
		Log.log(Log.INFO, "GMProcessor", "getRepaymentDetailsForCgpan",
				"Entered");
		Repayment repayment = null;
		GMProcessor gmProcessor = new GMProcessor();
		repayment = gmProcessor.getRepaymentDetailsForCgpan(cgpan);
		Log.log(Log.INFO, "GMProcessor", "getRepaymentDetailsForCgpan",
				"Exited");
		return repayment;

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
	public ActionForward exceptionalNpaUpdateInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "GMAction", "exceptionalNpaUpdateInput", "Entered");
		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		gmActionForm.setBankId(bankId);
		if (bankId.equals("0000"))
			memberId = "";
		gmActionForm.setMemberId(memberId);
		gmActionForm.setCgpan("");
		gmActionForm.setRemarks("");
		Log.log(4, "GMAction", "exceptionalNpaUpdateInput", "Exited");
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
	public ActionForward exceptionalNpaUpdate(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "GMAction", "exceptionalNpaUpdate", "Entered");
		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;
		ApplicationReport appReport = new ApplicationReport();
		ReportManager reportManager = new ReportManager();
		String memberId = gmActionForm.getMemberId();
		String cgpan = gmActionForm.getCgpan().toUpperCase();
		gmActionForm.setMemberId(memberId);
		gmActionForm.setCgpan(cgpan);
		appReport = reportManager.getApplicationReportForCgpan(cgpan);
		String mliId = appReport.getMemberId();
		if (!mliId.equals(memberId)) {
			throw new NoMemberFoundException("Entered Cgpan " + cgpan
					+ " does not belongs to the entered Member Id :" + memberId);
		}
		gmActionForm.setRemarks("");
		gmActionForm.setApplicationReport(appReport);
		java.util.Date expDate = appReport.getExpiryDate();
		String expDateStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (expDate != null) {
			expDateStr = sdf.format(expDate);
		}
		gmActionForm.setExpiryDate(expDateStr);
		Log.log(4, "GMAction", "exceptionalNpaUpdate", "Exited");
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
	public ActionForward submitexceptionalNpaUpdaterequest(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "GMAction", "submitexceptionalNpaUpdaterequest", "Entered");
		GMProcessor gmProcessor = new GMProcessor();
		GMDAO gmDAO = new GMDAO();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ClosureDetail closureDtl = new ClosureDetail();
		HashMap closureDetails = null;
		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String memberId = gmActionForm.getMemberId();
		String cgpan = gmActionForm.getCgpan().toUpperCase();
		String remarks = gmActionForm.getRemarks();
		String ssiRefNo = gmActionForm.getApplicationReport()
				.getSsiReferenceNumber();
		int type = 0;
		String forward = "";
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!memberids.contains(memberId))
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);
		if (!cgpan.equals("")) {
			type = 1;
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(4, "GMAction", "submitClosureDetails", " Bid For Pan - "
					+ bIdForThisCgpan);
			if (!borrowerIds.contains(bIdForThisCgpan))
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0)
				throw new MessageException(
						"Application cannot be Updated for this borrower since Claim Application has been submitted");
			Application application = new Application();
			try {
				application = appProcessor.getAppForCgpan(memberId, cgpan);
			} catch (DatabaseException databaseException) {
				if (databaseException.getMessage().equals(
						"Application does not exist."))
					throw new DatabaseException(
							"The application is not a live application");
			}
			if (application.getStatus().equals("CL"))
				throw new DatabaseException(
						"The application has already been closed");
			forward = "success";
		}
		Log.log(4, "GMAction", "submitexceptionalNpaUpdaterequest", "Exited");
		gmDAO.submitexceptionalNpaUpdaterequest(memberId, cgpan, remarks,
				userId, ssiRefNo);
		request.setAttribute("message", "<b>Provision for  " + cgpan
				+ " for update  Npa details request added successfully.<b><br>");
		return mapping.findForward(forward);
	}

	/**
	 * This method is used to get the borrowerId or Cgpan for modifying borrower
	 * details. The memberId is got from the the loggrd in userid
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward ApplicationInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "ApplicationInfo", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		dynaActionForm.set("borrowerIdForModifyBorrDtl", "");
		dynaActionForm.set("cgpanForModifyBorrDtl", "");
		dynaActionForm.set("borrowerNameForModifyBorrDtl", "");

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String branchId = user.getBranchId();
		String zoneId = user.getZoneId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaActionForm.set("memberIdForModifyBorrDtl", memberId);
		dynaActionForm.set("bankId", bankId);
		Log.log(Log.INFO, "GMAction", "ApplicationInfo", "Exited");
		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.BORROWER_FLAG,
				Constants.MODIFY_BORROWER_DETAILS);
		// dynaActionForm.initialize(mapping) ;

		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward showModifiedBorrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showModifiedBorrowerDetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		/*
		 * HttpSession session=request.getSession(false);
		 * session.setAttribute(SessionConstants.BORROWER_FLAG,
		 * Constants.APPROVE_BORROWER_DETAILS); dynaForm.initialize(mapping) ;
		 */
		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();
		TreeMap bidsList = gmProcessor.getBidsForApproval();
		if (bidsList.isEmpty() || bidsList.size() == 0) {
			Log.log(Log.INFO, "GMAction", "showModifiedBorrowerDetails",
					"emty bid list");
			request.setAttribute("message",
					"No Borrower Details available for Approval");
			forward = "success";

		} else {

			gmActionForm.setBidsList(bidsList);
			forward = "bidList";
		}

		return mapping.findForward(forward);

	}

	public ActionForward showApprovalPeriodicInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showApprovalPeriodicInfo", "Entered");

		// DynaActionForm dynaForm = (DynaActionForm) form;
		GMActionForm gmActionForm = (GMActionForm) form;

		/*
		 * HttpSession session=request.getSession(false);
		 * session.setAttribute(SessionConstants.BORROWER_FLAG, null);
		 * dynaForm.initialize(mapping) ;
		 */

		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();
		TreeMap bidsList = gmProcessor.getBidsForPerInfoApproval();
		if (bidsList.isEmpty() || bidsList.size() == 0) {
			Log.log(Log.INFO, "GMAction", "showModifiedBorrowerDetails",
					"emty bid list");
			request.setAttribute("message",
					"No Periodic Info Details available for Approval");
			forward = "success";

		} else {

			gmActionForm.setBidsList(bidsList);
			forward = "memberBidList";
		}

		return mapping.findForward(forward);

	}

	public ActionForward backToApproval(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "backToApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.BORROWER_FLAG, null);

		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * This method is used to modify the borrower details.
	 * 
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward modifyBorrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		BorrowerDetails borrowerDetails = null;
		SSIDetails ssiDetails = null;
		Administrator admin = new Administrator();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		ArrayList states = null;
		ArrayList districts = null;
		String state = "";

		String forward = "";

		String memberId = ((String) dynaActionForm
				.get("memberIdForModifyBorrDtl")).toUpperCase();
		String cgpan = ((String) dynaActionForm.get("cgpanForModifyBorrDtl"))
				.toUpperCase();
		String borrowerId = ((String) dynaActionForm
				.get("borrowerIdForModifyBorrDtl")).toUpperCase();
		String borrowerName = ((String) dynaActionForm
				.get("borrowerNameForModifyBorrDtl")).toUpperCase();

		states = admin.getAllStates();
		dynaActionForm.set("states", states);

		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		int type = 0;
		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}
			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Borrower Details for this borrower cannot be modified since Claim Application has been submitted");
			}

			borrowerDetails = gmProcessor.viewBorrowerDetails(memberId,
					borrowerId, type);

			forward = "success";
		}

		else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			type = 1;
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}

			dynaActionForm.set("borrowerIdForModifyBorrDtl", bIdForThisCgpan);

			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Borrower Details for this borrower cannot be modified since Claim Application has been submitted");
			}

			borrowerDetails = gmProcessor.viewBorrowerDetails(memberId, cgpan,
					type);

			forward = "success";

		} else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * }
			 * 
			 * dynaActionForm.set("borrowerIdForModifyBorrDtl",bIdForBorrowerName
			 * );
			 * 
			 * borrowerDetails = gmProcessor.viewBorrowerDetails(memberId,
			 * borrowerName, type);
			 */

			if ((bIdForBorrowerName.size() == 0)
					|| (bIdForBorrowerName == null)) {
				throw new NoDataException(
						"No Data Exists for this combination, "
								+ " Enter any other value");
			}

			dynaActionForm.set("borrowerIds", bIdForBorrowerName);
			forward = "bidList";
		}

		if (borrowerDetails != null) {

			int borrowerRefNo = borrowerDetails.getSsiDetails()
					.getBorrowerRefNo();

			Integer intRefNo = new Integer(borrowerRefNo);
			dynaActionForm.set("borrowerRefNo", intRefNo);

			ssiDetails = borrowerDetails.getSsiDetails();

			BeanUtils.copyProperties(dynaActionForm, ssiDetails);
			BeanUtils.copyProperties(dynaActionForm, borrowerDetails);

			state = ssiDetails.getState();
			Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails", "state "
					+ state);

			ArrayList districtList = admin.getAllDistricts(state);
			dynaActionForm.set("districts", districtList);

			String districtName = ssiDetails.getDistrict();
			Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
					"districtName " + districtName);

			if (districtList.contains(districtName)) {
				Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
						"setting in dyna form districtName " + districtName);
				dynaActionForm.set("district", districtName);
			} else {
				Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
						"districtName " + districtName);
				dynaActionForm.set("districtOthers", districtName);
				dynaActionForm.set("district", "Others");

			}
			String constitutionVal = ssiDetails.getConstitution();
			if (!(constitutionVal.equals("proprietary"))
					&& !(constitutionVal.equals("partnership"))
					&& !(constitutionVal.equals("private"))
					&& !(constitutionVal.equals("public"))) {
				dynaActionForm.set("constitutionOther", constitutionVal);
				dynaActionForm.set("constitution", "Others");
			} else {
				dynaActionForm.set("constitution", constitutionVal);
			}

			// Setting legal ID
			String legalIDString = ssiDetails.getCpLegalID();
			if (legalIDString != null && !(legalIDString.equals(""))) {
				if (!(legalIDString.equals("VoterIdentityCard"))
						&& !(legalIDString.equals("RationCardnumber"))
						&& !(legalIDString.equals("PASSPORT"))
						&& !(legalIDString.equals("Driving License"))) {
					dynaActionForm.set("otherCpLegalID", legalIDString);
					dynaActionForm.set("cpLegalID", "Others");
				} else {
					dynaActionForm.set("cpLegalID", legalIDString);
				}

			}
			ArrayList socialList = getSocialCategory();
			dynaActionForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = admin.getAllIndustryNature();
			dynaActionForm.set("industryNatureList", industryNatureList);

			// ssiDetails = borrowerDetails.getSsiDetails() ;
			String industryNature = ssiDetails.getIndustryNature();

			Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails",
					"industry nature :" + industryNature);

			if (industryNature != null && !(industryNature.equals(""))
					&& !industryNature.equals("OTHERS")) {
				ArrayList industrySectors = admin
						.getIndustrySectors(industryNature);
				dynaActionForm.set("industrySectors", industrySectors);
			} else {
				ArrayList industrySectors = new ArrayList();
				String industrySector = ssiDetails.getIndustrySector();
				industrySectors.add(industrySector);
				dynaActionForm.set("industrySectors", industrySectors);
			}

			Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails", "Exited");
			// dynaActionForm.initialize(mapping) ;

			states = null;
			districts = null;

			borrowerIds = null;
			memberids = null;

		}
		return mapping.findForward(forward);

	}

	public ActionForward showBorrowerDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "Entered");

		HttpSession session = (HttpSession) request.getSession(false);

		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		BorrowerDetails borrowerDetails = null;

		String formFlag = (String) request.getParameter("formFlag");

		Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "formFlag :"
				+ formFlag);

		String memberId = null;

		if (formFlag.equalsIgnoreCase("periodic")) {
			memberId = gmActionForm.getMemberId();
		} else if (formFlag.equalsIgnoreCase("closure")) {
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "closure");
			memberId = (String) session.getAttribute("closureMemberId");
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink",
					"memberId :" + memberId);
		} else if (formFlag.equalsIgnoreCase("schedule")) {
			memberId = (String) session.getAttribute("scheduleMemberId");
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "Schedule");
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink",
					"memberId :" + memberId);
		} else if (formFlag.equalsIgnoreCase("clNotPaid")) {
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink",
					"inside clNot Paid: ");
			memberId = (String) session.getAttribute("clNotPaid");
			Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink",
					"memberId :" + memberId);
		}

		String borrowerId = (String) request.getParameter("bidLink");

		Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "borrowerId :"
				+ borrowerId);

		borrowerDetails = gmProcessor.viewBorrowerDetails(memberId, borrowerId,
				GMConstants.TYPE_ZERO);

		gmActionForm.setBorrowerDetails(borrowerDetails);

		Log.log(Log.INFO, "GMAction", "showBorrowerDetailsLink", "Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * This method returns an arraylist of social Categories from the database
	 */
	private ArrayList getSocialCategory() {
		ArrayList socialCategoryList = null;
		try {
			Administrator admin = new Administrator();
			socialCategoryList = admin.getAllSocialCategories();
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "GMAction", "getSocialCategoryList",
					e.getMessage());
			Log.logException(e);

		}
		return socialCategoryList;
	}

	/**
	 * This method returns an arraylist of states from the database
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getDistricts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "getAllDistricts", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;
		// GMProcessor gmProcessor = new GMProcessor ();
		Administrator admin = new Administrator();
		String state = (String) dynaActionForm.get("state");

		ArrayList districts = admin.getAllDistricts(state);
		dynaActionForm.set("districts", districts);

		Log.log(Log.INFO, "GMAction", "getAllDistricts", "Exited");

		districts = null;

		request.setAttribute(SessionConstants.GM_FOCUS_FIELD, "GMDistrict");
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * This method gets the industry sectors from the database
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getIndustrySectors(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getIndustrySectors", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		Administrator admin = new Administrator();
		String industryNature = (String) dynaActionForm.get("industryNature");

		ArrayList industrySectors = admin.getIndustrySectors(industryNature);
		dynaActionForm.set("industrySectors", industrySectors);

		Log.log(Log.INFO, "GMAction", "getIndustrySectors", "Exited");

		industrySectors = null;

		request.setAttribute(SessionConstants.GM_FOCUS_FIELD, "GMSector");

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * This method saves the modified borrowerdetails in to the database
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveBorrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "BorrowerDetailsSaved", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		// System.out.println("Before Populate " + dynaActionForm.get("osAmt"));
		int borrowerRefNo = ((java.lang.Integer) dynaActionForm
				.get("borrowerRefNo")).intValue();
		GMProcessor gmProcessor = new GMProcessor();
		SSIDetails ssiDetails = new SSIDetails();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		String message = "Borrower Details Saved Successfully";
		BeanUtils.populate(ssiDetails, dynaActionForm.getMap());
		ssiDetails.setBorrowerRefNo(borrowerRefNo);
		// System.out.println("first name "+ dynaActionForm.get("firstName"));
		// System.out.println("first name "+ dynaActionForm.get("secondName"));
		// System.out.println("first name "+ dynaActionForm.get("thirdName"));
		String constitutionValue = (String) dynaActionForm
				.get("constitutionOther");
		if (dynaActionForm.get("constitution").equals("Others")) {
			ssiDetails.setConstitution(constitutionValue);
		}

		String districtOthersValue = (String) dynaActionForm
				.get("districtOthers");
		Log.log(Log.DEBUG, "GMAction", "BorrowerDetailsSaved",
				"dist other val " + districtOthersValue);
		if (dynaActionForm.get("district").equals("Others")) {
			Log.log(Log.DEBUG, "GMAction", "BorrowerDetailsSaved",
					"dist other val " + districtOthersValue);
			ssiDetails.setDistrict(districtOthersValue);
		}

		String otherLegalIdValue = (String) dynaActionForm
				.get("otherCpLegalID");
		if (dynaActionForm.get("cpLegalID").equals("Others")) {
			ssiDetails.setCpLegalID(otherLegalIdValue);
		}
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaActionForm.getMap());
		User user = getUserInformation(request);
		String userId = user.getUserId();
		Log.log(Log.DEBUG, "GMAction", "BorrowerDetailsSaved", "ref no"
				+ borrowerDetails.getSsiDetails().getBorrowerRefNo());
		gmProcessor.updateBorrowerDetails(borrowerDetails, userId);

		// System.out.println("After Populate" + borrowerDetails.getNpa());

		request.setAttribute("message", message);
		Log.log(Log.INFO, "GMAction", "BorrowerDetailsSaved", "Exited");
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
	public ActionForward getMemberForShiftingrequest(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getMemberForShiftingrequest", "Entered");
		// System.out.println("GMAction getMemberForShiftingrequest Entered");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		// System.out.println("Bank Id:"+bankId);
		HttpSession session = request.getSession(false);

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.setMemberIdForShifting("");
		gmActionForm.setBorrowerIdForShifting("");
		gmActionForm.setCgpanForShifting("");
		gmActionForm.setMemberIdToShift("");

		if (bankId.equalsIgnoreCase(Constants.CGTSI_USER_BANK_ID)) {
			// System.out.println("CGTSI_USER_BANK_ID:"+Constants.CGTSI_USER_BANK_ID);
			gmActionForm.setSelectMember("");

			session.setAttribute(SessionConstants.TARGET_URL,
					"selectGMMember.do?method=getShiftingCgpans");

			return mapping.findForward("memberInfo");
		} else {
			request.setAttribute("pageValue", "1");

			Log.log(Log.INFO, "GMAction", "getMemberForShiftingrequest",
					"Exited");
			// System.out.println("GMAction getMemberForShiftingrequest Exited");
			getShiftingCgpans(mapping, form, request, response);
			return mapping.findForward("danSummary");
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
	public ActionForward getShiftingCgpans(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getShiftingCgpans", "Entered");
		// System.out.println("getShiftingCgpans entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";

		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans", "Bank Id : "
				+ bankId);
		Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans", "Zone Id : "
				+ zoneId);
		Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans", "Branch Id : "
				+ branchId);

		if (bankId.equals("0000")) {
			memberId = gmActionForm.getSelectMember();
			// System.out.println("Selected MemberId:"+memberId);
			if (memberId == null || memberId.equals("")) {
				memberId = gmActionForm.getMemberId();
				// System.out.println("MemberId:"+memberId);
			}

			Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans", "mliId = "
					+ memberId);

			if (memberId == null || memberId.equals("")) {

				session.setAttribute(SessionConstants.TARGET_URL,
						"selectGMMember.do?method=getShiftingCgpans");
				// System.out.println("TGT url: "+request.getAttribute("targetURL"));

				return mapping.findForward("memberInfo");
			} else {
				bankId = memberId.substring(0, 4);
				zoneId = memberId.substring(4, 8);
				branchId = memberId.substring(8, 12);

				ClaimsProcessor cpProcessor = new ClaimsProcessor();
				Vector memberIds = cpProcessor.getAllMemberIds();
				if (!(memberIds.contains(memberId))) {
					throw new NoMemberFoundException(
							"The Member ID does not exist");
				}

			}
		}

		Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans",
				"Selected Bank Id,zone and branch ids : " + bankId + ","
						+ zoneId + "," + branchId);

		GMProcessor gmProcessor = new GMProcessor();
		ArrayList danSummaries = gmProcessor.displayShiftingCgpans(bankId,
				zoneId, branchId);

		Log.log(Log.DEBUG, "GMAction", "getShiftingCgpans",
				"dan summary size : " + danSummaries.size());

		if (danSummaries.size() == 0) {
			gmActionForm.setSelectMember(null);

			throw new MissingDANDetailsException(
					"No CGPANs available for Shifting");
		} else {
			boolean isDanAvailable = false;
			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);
				if (danSummary.getAmountDue() != danSummary.getAmountPaid()) {
					isDanAvailable = true;
					break;
				}
			}
			if (!isDanAvailable) {
				gmActionForm.setSelectMember(null);

				throw new MissingDANDetailsException(
						"No CGPANs available for Shifting");
			}
			gmActionForm.setDanSummaries(danSummaries);
			ArrayList memberList = (ArrayList) getMemberList(bankId);
			memberList.remove(memberId);
			gmActionForm.setMemberList(memberList);
			gmActionForm.setBankId(bankId);
			gmActionForm.setZoneId(zoneId);
			gmActionForm.setBranchId(branchId);

			Log.log(Log.INFO, "GMAction", "getShiftingCgpans", "Exited");
			if (gmActionForm.getSelectMember() != null) {
				gmActionForm.setMemberId(gmActionForm.getSelectMember());

			} else {
				gmActionForm.setMemberId(bankId + zoneId + branchId);

			}

			gmActionForm.setSelectMember(null);
			// System.out.println(actionForm.getSelectMember());
			// System.out.println("getPendingGFDANs exited");

			return mapping.findForward("danSummary2");
		}
	}

	/**
	 * 
	 * @return
	 */
	private Collection getMemberList(String bankId) {
		// System.out.println("getMemberList Entered:");
		ArrayList memberList = null;
		try {
			Administrator admin = new Administrator();
			memberList = admin.getMemberList(bankId);
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "GMAction", "getMemberList", e.getMessage());
			// System.out.println("e.getMessage:"+e.getMessage());
			Log.logException(e);

		}
		// System.out.println("getMemberList exited:");
		return memberList;

	}

	public ActionForward submitCgpanRequests(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GMActionForm gmPeriodicInfoForm = (GMActionForm) form;

		Map clearCgpan = gmPeriodicInfoForm.getClearCgpan();
		Set clearCgpanSet = clearCgpan.keySet();
		Iterator clearCgpanIterator = clearCgpanSet.iterator();
		String oldMemberId = gmPeriodicInfoForm.getMemberId();
		// System.out.println("oldMemberId:"+oldMemberId);
		while (clearCgpanIterator.hasNext()) {
			String key = (String) clearCgpanIterator.next();
			String newMemberId = (String) clearCgpan.get(key);
			if (!(newMemberId.equals(""))) {
				// System.out.println(key+"-->"+"newMemberId:"+newMemberId);
				request.setAttribute("message",
						"<b>The Request for Cgpan Shifting make successfully.<b><br>");
			}
		}
		clearCgpan.clear();
		return mapping.findForward("success");
	}

	/**
	 * This method gets the member for which the shifting of Borrower or cgpan
	 * is done
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getMemberForShifting(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getMemberForShifting", "Entered");

		// GMProcessor gmProcessor = new GMProcessor();
		/*
		 * ArrayList mliInfos = null; ArrayList mliMemberIds = new ArrayList();
		 * MLIInfo mliInfo = null;
		 * 
		 * String cgpan = ""; String mliBankId = ""; String mliZoneId = "" ;
		 * String mliBranchId = ""; String mliMemberId = ""; Registration
		 * registration = new Registration(); mliInfos =
		 * registration.getAllMembers(); int mliSize = mliInfos.size() ; for(int
		 * i = 0; i<mliSize; ++i) { mliInfo = (MLIInfo)mliInfos.get(i);
		 * mliBankId = mliInfo.getBankId(); mliZoneId = mliInfo.getZoneId();
		 * mliBranchId = mliInfo.getBranchId(); mliMemberId =
		 * mliBankId.concat(mliZoneId).concat(mliBranchId);
		 * mliMemberIds.add(mliMemberId); }
		 */
		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.setMemberIdForShifting("");
		gmActionForm.setBorrowerIdForShifting("");
		gmActionForm.setCgpanForShifting("");
		gmActionForm.setMemberIdToShift("");

		// gmActionForm.setMemberIdsForShifting(mliMemberIds);

		// gmActionForm.setMemberIdsToShift(mliMemberIds);
		Log.log(Log.INFO, "GMAction", "getMemberForShifting", "Exited");

		return mapping.findForward(Constants.SUCCESS);

	}

	// ArrayList borrowerIds = null;

	// String memberId = gmActionForm.getMemberIdForShifting();
	// borrowerIds = gmProcessor.getBorrowerIds(memberId);
	// gmActionForm.setBorrowerIdsForShifting(borrowerIds);

	// gmActionForm.setMemberIdsToShift(mliMemberIds);

	// User user = getUserInformation(request);
	// String userId = user.getUserId() ;

	// gmProcessor.shiftCgpanBorrower(memberId, userId, cgpan, memberId);

	/**
	 * This method gets the borrower for the member for shifting
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 * 
	 *             public ActionForward getBorrowerForShifting( ActionMapping
	 *             mapping, ActionForm form, HttpServletRequest request,
	 *             HttpServletResponse response) throws Exception{
	 * 
	 *             Log.log(Log.INFO,"GMAction","getBorrowerForShifting",
	 *             "Entered"); GMActionForm gmActionForm=(GMActionForm)form;
	 *             GMProcessor gmProcessor = new GMProcessor(); ArrayList
	 *             borrowerIds = null;
	 * 
	 *             String memberId = gmActionForm.getMemberIdForShifting();
	 *             if((memberId != null) && !memberId.equals("") ) { borrowerIds
	 *             = gmProcessor.getBorrowerIds(memberId);
	 *             gmActionForm.setBorrowerIdsForShifting(borrowerIds); }
	 *             Log.log
	 *             (Log.INFO,"GMAction","getBorrowerForShifting","Exited");
	 * 
	 *             borrowerIds = null;
	 * 
	 *             return mapping.findForward(Constants.SUCCESS); }
	 */
	/**
	 * This method gets the cgpan for the borrower for shifting
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	/*
	 * public ActionForward getCgpanForShifting( ActionMapping mapping,
	 * ActionForm form, HttpServletRequest request, HttpServletResponse
	 * response) throws Exception{
	 * 
	 * Log.log(Log.INFO,"GMAction","SelectCgpanForShifting","Entered"); Vector
	 * noOfCgpan = null; ArrayList cgpans = new ArrayList(); String cgpan = "";
	 * ClaimsProcessor claimsProcessor = new ClaimsProcessor(); GMActionForm
	 * gmActionForm=(GMActionForm)form; String borrowerId =
	 * gmActionForm.getBorrowerIdForShifting(); noOfCgpan =
	 * claimsProcessor.getCGPANDetailsForBorrowerId(borrowerId); int cgpansSize
	 * = noOfCgpan.size() ; for(int i=0; i<cgpansSize; ++i) { HashMap cgpanDtls
	 * =(HashMap)noOfCgpan.get(i); cgpan = (String)cgpanDtls.get("cgpan");
	 * cgpans.add(cgpan); } //gmActionForm.setCgpansForShifting(cgpans);
	 * 
	 * Log.log(Log.INFO,"GMAction","SelectCgpanForShifting","Exited"); return
	 * mapping.findForward(Constants.SUCCESS);
	 * 
	 * }
	 */
	/**
	 * This method gets the member to which the shifting has to be done
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	/*
	 * public ActionForward getMemberToShift( ActionMapping mapping, ActionForm
	 * form, HttpServletRequest request, HttpServletResponse response) throws
	 * Exception{
	 * 
	 * Log.log(Log.INFO,"GMAction","getMemberToShift","Entered"); ArrayList
	 * mliInfos = null; ArrayList mliMemberIds = new ArrayList(); MLIInfo
	 * mliInfo = null; String mliBankId = ""; String mliZoneId = "" ; String
	 * mliBranchId = ""; String mliMemberId = ""; Registration registration =
	 * new Registration(); mliInfos = registration.getAllMembers(); int mliSize
	 * = mliInfos.size() ; for(int i = 0; i<mliSize; ++i) { mliInfo =
	 * (MLIInfo)mliInfos.get(i); mliBankId = mliInfo.getBankId(); mliZoneId =
	 * mliInfo.getZoneId(); mliBranchId = mliInfo.getBranchId(); mliMemberId =
	 * mliBankId.concat(mliZoneId).concat(mliBranchId);
	 * //System.out.println("mli member " + mliMemberId);
	 * mliMemberIds.add(mliMemberId); } GMActionForm
	 * gmActionForm=(GMActionForm)form;
	 * gmActionForm.setMemberIdsToShift(mliMemberIds);
	 * 
	 * Log.log(Log.INFO,"GMAction","getMemberToShift","Exited"); return
	 * mapping.findForward(Constants.SUCCESS);
	 * 
	 * }
	 */

	/**
	 * This method does the shifting process
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward shiftCgpanOrBorrower(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "shiftCgpanOrBorrower", "Entered");
		// System.out.println("GMAction shiftCgpanOrBorrower Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		Vector noOfCgpan = null;
		String cgpan = "";
		String message = "Shift Borrower Process Completed";
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		String borrowerId = null;
		String cgpanToShift = gmActionForm.getCgpanForShifting();
		// System.out.println("cgpanToShift:"+cgpanToShift);
		if (!cgpanToShift.equals("") && cgpanToShift != null) {
			borrowerId = claimsProcessor.getBorowwerForCGPAN(cgpanToShift);
			// System.out.println("borrowerId:"+borrowerId);
			Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower",
					"Bid for the cgpan :" + cgpanToShift + " ," + borrowerId);
		} else {
			borrowerId = (gmActionForm.getBorrowerIdForShifting())
					.toUpperCase();
			// System.out.println("BorrowerId:"+borrowerId);
			Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower", "Bid :"
					+ borrowerId);
		}

		String srcMemberId = gmActionForm.getMemberIdForShifting();
		String srcBankId = srcMemberId.substring(0, 4);
		// System.out.println("srcMemberId:"+srcMemberId);
		Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower", "src member id:"
				+ srcMemberId);

		Vector memberids = claimsProcessor.getAllMemberIds();
		if (!(memberids.contains(srcMemberId))) {
			// System.out.println("Form Member Id :"+ srcMemberId
			// +" does not exist in the database.");
			throw new NoMemberFoundException("Form Member Id :" + srcMemberId
					+ " does not exist in the database.");
		}

		// validate the borrower ids.
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(srcMemberId);
		String memberIdforCgpan = "";
		memberIdforCgpan = gmProcessor.getMemIdforCgpan(cgpanToShift);
		// System.out.println("memberIdforCgpan:"+memberIdforCgpan);
		/* COMMENTED PART START HERE -- SUKUMAR */
		String allocationStatus = "";
		allocationStatus = gmProcessor
				.getallocationStatusforCgpan(cgpanToShift);
		// System.out.println("allocationStatus:"+allocationStatus);
		if (!(borrowerIds.contains(borrowerId))) {
			if (allocationStatus.equals("Y")) {
				/*
				 * System.out.println("Borrower ID "+borrowerId +
				 * " is not among the borrower" + " Ids for the Member Id :" +
				 * srcMemberId +". The member ID for selected CGPAN " +
				 * cgpanToShift+" is "
				 * +memberIdforCgpan+". DAN(s) for the selected CGPAN are pending"
				 * + "for appropriation. Please appropriate and proceed");
				 */
				throw new NoDataException("Borrower ID " + borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + srcMemberId
						+ ". The member ID for selected CGPAN " + cgpanToShift
						+ " is " + memberIdforCgpan
						+ ". DAN(s) for the selected CGPAN are pending"
						+ "for appropriation. Please appropriate and proceed");

			} else {
				/*
				 * System.out.println("Borrower ID "+borrowerId +
				 * " is not among the borrower" + " Ids for the Member Id :" +
				 * srcMemberId +". The member ID for selected CGPAN " +
				 * cgpanToShift+" is " +memberIdforCgpan+".");
				 */
				throw new NoDataException("Borrower ID " + borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + srcMemberId
						+ ". The member ID for selected CGPAN " + cgpanToShift
						+ " is " + memberIdforCgpan + ".");

			}
		} else if (allocationStatus.equals("Y")) {
			/*
			 * System.out.println("DAN(s) for the selected CGPAN are pending"+
			 * "for appropriation. Please appropriate and proceed");
			 */
			throw new NoDataException(
					"DAN(s) for the selected CGPAN are pending"
							+ "for appropriation. Please appropriate and proceed");
		}

		/* COMMENTED PART END HERE */
		String memberId = gmActionForm.getMemberIdToShift();
		String destBankId = memberId.substring(0, 4);
		/*
		 * if(!(srcBankId.equals("0085"))){ if(!(srcBankId.equals(destBankId)))
		 * { // System.out.println(
		 * "The From Member ID should not be same as the To Member Id"); throw
		 * new DatabaseException(
		 * "The From Member Bank ID should  be same as the To Member Bank Id");
		 * } } COMMENTED FOR SHIFTING CGPANS BETWEEN BANKS ON 10/05/2011 BY
		 * AVSHYAM
		 */
		// System.out.println("GMAction"+"shiftCgpanOrBorrower"+"dest member Id:"+memberId);
		Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower",
				"dest member Id:" + memberId);

		if (!(memberids.contains(memberId))) {
			// System.out.println("To Member Id :"+
			// memberId+" does not exist in the database.");
			throw new NoMemberFoundException("To Member Id :" + memberId
					+ " does not exist in the database.");
		}

		if (srcMemberId.equals(memberId)) {
			// System.out.println("The From Member ID should not be same as the To Member Id");
			throw new DatabaseException(
					"The From Member ID should not be same as the To Member Id");
		}

		noOfCgpan = gmProcessor.getCGPANs(borrowerId);
		int cgpansSize = noOfCgpan.size();
		// System.out.println("GMAction"+"shiftCgpanOrBorrower"+"no of pans for the borrower:"+cgpansSize);

		Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower",
				"no of pans for the borrower:" + cgpansSize);

		ArrayList cgpans = new ArrayList();

		for (int i = 0; i < cgpansSize; ++i) {
			HashMap cgpanDtls = (HashMap) noOfCgpan.get(i);
			cgpan = (String) cgpanDtls.get(ClaimConstants.CLM_CGPAN);
			// System.out.println("GMAction"+"shiftCgpanOrBorrower"+"inside loop - the cgpan is :"+cgpan);
			Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower",
					"inside loop - the cgpan is :" + cgpan);
			if (cgpan != null) {
				cgpans.add(cgpan);
			}
		}

		User user = getUserInformation(request);
		String userId = user.getUserId();
		// System.out.println("GMAction"+"shiftCgpanOrBorrower"+"user id:"+userId);
		Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower", "user id:"
				+ userId);

		int size = cgpans.size();
		// System.out.println("GMAction"+"shiftCgpanOrBorrower"+"cgpans size :"+size);
		Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower", "cgpans size :"
				+ size);
		for (int i = 0; i < size; i++) {
			String cgpanForBorrower = (String) cgpans.get(i);
			// System.out.println("GMAction"+"shiftCgpanOrBorrower"+" shifted cgpan :"+cgpanForBorrower);
			Log.log(Log.DEBUG, "GMAction", "shiftCgpanOrBorrower",
					" shifted cgpan :" + cgpanForBorrower);
			gmProcessor.shiftCgpanBorrower(srcMemberId, userId,
					cgpanForBorrower, memberId);
		}
		// System.out.println("GMAction"+"shiftingDone"+"Exited");
		Log.log(Log.INFO, "GMAction", "shiftingDone", "Exited");

		noOfCgpan = null;
		cgpans = null;

		request.setAttribute("message", message);
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * //For recovery Details This method gets the borrowerId for the recovery
	 * details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward BorrowerIdRecovery(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "BorrowerIdRecovery", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);

		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "BorrowerIdRecovery", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward getBorrowerIdForUpdateRecovery(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getBorrowerIdForUpdateRecovery",
				"Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);
		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "getBorrowerIdForUpdateRecovery",
				"Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * //For recovery Details This method gets the recovery details for the
	 * borrower selected
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRecoveryDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRecoveryDetails", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();
		HttpSession session = (HttpSession) request.getSession(false);
		String srcmainmenu = (String) session.getAttribute("mainMenu");

		if (srcmainmenu.equals(MenuOptions.getMenu(MenuOptions.CP_CLAIM_FOR))) {
			String memberId = (String) session
					.getAttribute(ClaimConstants.CLM_MEMBER_ID);
			String borrowerId = (String) session
					.getAttribute(ClaimConstants.CLM_BORROWER_ID);
			gmActionForm.setMemberId(memberId);
			gmActionForm.setBorrowerId(borrowerId);

		} else if (srcmainmenu.equals(MenuOptions
				.getMenu(MenuOptions.GM_PERIODIC_INFO))
				&& (session.getAttribute("subMenuItem")).equals(MenuOptions
						.getMenu(MenuOptions.GM_PI_RECOVERY_DETAILS))) {
			Log.log(Log.DEBUG,
					"GMAction",
					"showRecoveryDetails",
					"subMenuItem inside  if "
							+ session.getAttribute("subMenuItem"));

			// Validating the member Id
			String memberId = gmActionForm.getMemberId();

			ClaimsProcessor processor = new ClaimsProcessor();
			Vector memberids = processor.getAllMemberIds();
			if (!(memberids.contains(memberId))) {
				throw new NoMemberFoundException("Member Id :" + memberId
						+ " does not exist in the database.");
			}

			// validating the borr ids
			ArrayList borrowerIds = new ArrayList();
			borrowerIds = gmProcessor.getBorrowerIds(memberId);
			String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
			String cgpan = (gmActionForm.getCgpan()).toUpperCase();
			String borrowerName = gmActionForm.getBorrowerName();

			if ((!borrowerId.equals("")) && (cgpan.equals(""))
					&& (borrowerName.equals(""))) {
				if (!(borrowerIds.contains(borrowerId))) {
					gmActionForm.setBorrowerId("");
					throw new NoDataException(borrowerId
							+ " is not among the borrower"
							+ " Ids for the Member Id :" + memberId
							+ ". Please enter correct"
							+ " Member Id and Borrower Id.");
				}
			} else if ((!cgpan.equals("")) && (borrowerId.equals(""))
					&& (borrowerName.equals(""))) {
				int type = 1;

				borrowerId = processor.getBorowwerForCGPAN(cgpan);
				Log.log(Log.DEBUG, "GMAction", "showOutstandingDetails",
						" Bid For Pan - " + borrowerId);
				if (!(borrowerIds.contains(borrowerId))) {
					throw new NoDataException(cgpan
							+ "is not a valid Cgpan for " + "the Member Id :"
							+ memberId + ". Please enter correct Cgpan");
				}
				gmActionForm.setBorrowerId(borrowerId);

			} else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
					&& (cgpan.equals(""))) {
				int type = 2;
				ArrayList bIdForBorrowerName = gmProcessor
						.getBorrowerIdForBorrowerName(borrowerName, memberId);
				if (bIdForBorrowerName == null
						|| bIdForBorrowerName.size() == 0) {
					throw new NoDataException(
							"There are no Borrower Ids for this member");

				}
				session.setAttribute("displayFlag", "6");
				gmActionForm.setBorrowerIds(bIdForBorrowerName);
				return mapping.findForward("bidList");

			}
		}
		/*
		 * if(
		 * srcmainmenu.equals(MenuOptions.getMenu(MenuOptions.GM_PERIODIC_INFO))
		 * && (session.getAttribute("subMenuItem")).equals(MenuOptions.
		 * GM_PI_UPDATE_RECOVERY_DETAILS) ) {
		 * System.out.println("subMenuItem "+session
		 * .getAttribute("subMenuItem"));
		 * Log.log(Log.INFO,"GMAction","updateRecoveryDetails","Exited"); //
		 * Validating the member Id String memberId =
		 * gmActionForm.getMemberId();
		 * 
		 * ClaimsProcessor processor = new ClaimsProcessor(); Vector memberids =
		 * processor.getAllMemberIds(); if(!(memberids.contains(memberId))) {
		 * throw new NoMemberFoundException("Member Id :"+ memberId
		 * +" does not exist in the database."); }
		 * 
		 * //validating the borr ids ArrayList borrowerIds = new ArrayList();
		 * borrowerIds = gmProcessor.getBorrowerIds(memberId); String borrowerId
		 * = (gmActionForm.getBorrowerId()).toUpperCase();
		 * if(!(borrowerIds.contains(borrowerId))) {
		 * gmActionForm.setBorrowerId(""); throw new NoDataException(borrowerId
		 * + " is not among the borrower" + " Ids for the Member Id :" +
		 * memberId +". Please enter correct" + " Member Id and Borrower Id.");
		 * }
		 * 
		 * Log.log(Log.INFO,"GMAction","updateRecoveryDetails","Exited"); return
		 * mapping.findForward("updateRecovery"); }
		 */

		gmActionForm.resetWhenRequired(mapping, request);
		Log.log(Log.INFO, "GMAction", "showRecoveryDetails", "Exited");
		return mapping.findForward("insertRecovery");
	}

	public ActionForward modifyRecoveryDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "modifyRecoveryDetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		GMProcessor gmProcessor = new GMProcessor();
		// Validating the member Id
		String memberId = gmActionForm.getMemberId();

		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}
		} else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			int type = 1;

			borrowerId = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showOutstandingDetails",
					" Bid For Pan - " + borrowerId);
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			gmActionForm.setBorrowerId(borrowerId);

		} else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			int type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			} else {
				session.setAttribute("displayFlag", "7");
				gmActionForm.setBorrowerIds(bIdForBorrowerName);
				return mapping.findForward("bidList");
			}

		}

		Log.log(Log.INFO, "GMAction", "modifyRecoveryDetails", "Exited");

		ArrayList recoveryDetails = gmProcessor.getRecoveryDetails(borrowerId);

		Map recoveryMap = new HashMap();
		String recId = null;
		for (int i = 0; i < recoveryDetails.size(); ++i) {
			Recovery reco = (Recovery) recoveryDetails.get(i);
			recId = reco.getRecoveryNo();
			recoveryMap.put(recId, reco);
		}

		gmActionForm.setRecoveryDetails(recoveryMap);

		return mapping.findForward("updateRecovery");

	}

	public ActionForward updateRecovery(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "updateRecovery", "Entered");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();

		Map recMap = gmActionForm.getRecoveryDetails();

		String recId = (String) request.getParameter("recoId");
		// System.out.println("rec ID fro m jsp : "+recId);
		Recovery recovery = (Recovery) recMap.get(recId);
		HttpSession session = (HttpSession) request.getSession(false);

		session.setAttribute("recoIdFromDataBase", recId);

		double amt = recovery.getAmountRecovered();
		CustomisedDate custom = new CustomisedDate();
		custom.setDate(recovery.getDateOfRecovery());

		double legalcharge = recovery.getLegalCharges();

		/*
		 * String dtStr = null; if(dt!=null){ dtStr = dt.toString(); }
		 */
		String rem = recovery.getRemarks();
		String isRecoveryBySaleOfAsset = recovery.getIsRecoveryBySaleOfAsset();
		String detailsOfAssetSold = recovery.getDetailsOfAssetSold();
		String isRecoveryByOTS = recovery.getIsRecoveryByOTS();

		// System.out.println(" action Update method : Recovery Id " +
		// recovery.getRecoveryNo());

		// CustomisedDate customDate = new CustomisedDate(dt);

		// System.out.println("custom Date : "+customDate);

		gmActionForm.setDateOfRecovery(custom);
		gmActionForm.setAmountRecovered(amt);
		gmActionForm.setLegalCharges(legalcharge);

		gmActionForm.setRemarks(rem);
		gmActionForm.setIsRecoveryBySaleOfAsset(isRecoveryBySaleOfAsset);
		gmActionForm.setDetailsOfAssetSold(detailsOfAssetSold);
		gmActionForm.setIsRecoveryByOTS(isRecoveryByOTS);

		Log.log(Log.INFO, "GMAction", "updateRecovery", "Exited");
		request.setAttribute("message",
				"Recovery Details updated Successfully.");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * //For recovery Details This method saves the recovery details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveRecoveryDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveRecoveryDetails", "Entered");
		HttpSession session = (HttpSession) request.getSession(false);
		GMProcessor gmProcessor = new GMProcessor();
		ClaimsProcessor processor = new ClaimsProcessor();
		Recovery recovery = new Recovery();

		String recId = (String) session.getAttribute("recoIdFromDataBase");
		// System.out.println("save method : "+recId);

		GMActionForm gmActionForm = (GMActionForm) form;
		String memberId = gmActionForm.getMemberId();

		Date recoveryDate = gmActionForm.getDateOfRecovery();
		double amtRecovered = gmActionForm.getAmountRecovered();
		double legalCharges = gmActionForm.getLegalCharges();
		String remarks = gmActionForm.getRemarks().trim();
		String saleOfAsset = gmActionForm.getIsRecoveryBySaleOfAsset();
		String detailsOfAsset = gmActionForm.getDetailsOfAssetSold().trim();
		String recoveryByOts = gmActionForm.getIsRecoveryByOTS();

		recovery.setRecoveryNo(recId);

		if (amtRecovered != 0) {
			recovery.setAmountRecovered(amtRecovered);
		}

		if (recoveryDate != null && !(recoveryDate.toString()).equals("")) {
			recovery.setDateOfRecovery(recoveryDate);
		} else {
			recovery.setDateOfRecovery(null);
		}
		recovery.setDetailsOfAssetSold(detailsOfAsset);
		recovery.setIsRecoveryByOTS(recoveryByOts);
		recovery.setIsRecoveryBySaleOfAsset(saleOfAsset);
		recovery.setRemarks(remarks);

		if (legalCharges != 0) {
			recovery.setLegalCharges(legalCharges);
		}

		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();

		recovery.setCgbid(borrowerId);

		if (recoveryByOts.equals(ClaimConstants.DISBRSMNT_YES_FLAG)) {
			Log.log(Log.INFO, "GMAction", "saveRecoveryDetails()",
					"Checking to see if OTS Details are available...");
			// HashMap otsdetails = processor.getOTSDetails(borrowerId);
			Vector otsdetails = processor.getOTSDetails(borrowerId);

			/*
			 * if(otsdetails.size() == 0) {
			 * Log.log(Log.INFO,"GMAction","saveRecoveryDetails()"
			 * ,"Redirecting to the OTS Details Page...");
			 * session.setAttribute("mainMenu"
			 * ,MenuOptions.getMenu(MenuOptions.GM_PERIODIC_INFO));
			 * System.out.println("From GM Action Member Id :" + memberId);
			 * session.setAttribute(ClaimConstants.CLM_MEMBER_ID,memberId);
			 * System.out.println("From GM Action Borrower Id :" + borrowerId);
			 * session.setAttribute(ClaimConstants.CLM_BORROWER_ID,borrowerId);
			 * session
			 * .setAttribute(ClaimConstants.CLM_RECOVERY_OBJECT,recovery);
			 * return mapping.findForward("otsdetails"); }
			 */

			boolean willfulDefaulterFlagPresent = false;
			for (int i = 0; i < otsdetails.size(); i++) {
				HashMap map = (HashMap) otsdetails.elementAt(i);
				if (map != null) {
					String willfuldefaulter = (String) map
							.get(ClaimConstants.CLM_OTS_WILLFUL_DEFAULTER);
					// System.out.println("Willful Defaulter :" +
					// willfuldefaulter);
					if (willfuldefaulter == null) {
						// return mapping.findForward("otsdetails");
						willfulDefaulterFlagPresent = willfulDefaulterFlagPresent || false;
					} else {
						willfulDefaulterFlagPresent = willfulDefaulterFlagPresent || true;
						// continue;
					}
				}
			}

			if (!willfulDefaulterFlagPresent) {
				Log.log(Log.INFO, "GMAction", "saveRecoveryDetails()",
						"Redirecting to the OTS Details Page...");
				session.setAttribute("mainMenu",
						MenuOptions.getMenu(MenuOptions.GM_PERIODIC_INFO));
				// System.out.println("From GM Action Member Id :" + memberId);
				session.setAttribute(ClaimConstants.CLM_MEMBER_ID, memberId);
				// System.out.println("From GM Action Borrower Id :" +
				// borrowerId);
				session.setAttribute(ClaimConstants.CLM_BORROWER_ID, borrowerId);
				session.setAttribute(ClaimConstants.CLM_RECOVERY_OBJECT,
						recovery);
				return mapping.findForward("otsdetails");
			}
		}

		if (recId != null && !recId.equals("")) {
			// System.out.println("rec Id is not null : "+recId);
			gmProcessor.modifyRecoveryDetails(recovery);
			session.setAttribute("recoIdFromDataBase", "");
			request.setAttribute("message",
					"Recovery details updated Successfully");
			return mapping.findForward("successmessage");

		} else {

			// System.out.println("rec Id is null : "+recId);
			gmProcessor.addRecoveryDetails(recovery);
		}
		Log.log(Log.INFO, "GMAction", "saveRecoveryDetails", "Exited");
		String srcMenu = (String) session.getAttribute("mainMenu");
		// System.out.println("SRC Menu :" + srcMenu);
		String srcSubMenu = (String) session.getAttribute("subMenuItem");
		// System.out.println("SRC Sub Menu :" + srcSubMenu);
		if ((srcMenu.equals(MenuOptions.getMenu(MenuOptions.CP_CLAIM_FOR)))
				&& (srcSubMenu.equals(MenuOptions
						.getMenu(MenuOptions.CP_CLAIM_FOR_FIRST_INSTALLMENT)))) {
			return mapping.findForward("firstClaimDetails");
		}
		if ((srcMenu.equals(MenuOptions.getMenu(MenuOptions.CP_CLAIM_FOR)))
				&& (srcSubMenu.equals(MenuOptions
						.getMenu(MenuOptions.CP_CLAIM_FOR_SECOND_INSTALLMENT)))) {
			return mapping.findForward("secondClaimDetails");
		}
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward showAdditionalRecovery(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showAdditionalRecovery", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;

		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();

		gmActionForm.setBorrowerId(borrowerId);
		gmActionForm.resetWhenRequired(mapping, request);
		Log.log(Log.INFO, "GMAction", "showAdditionalRecovery", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * //For NPA Details This method gets the borrowerId for the NPA details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward NpaDetailsGetBorrowerId(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "NpaDetailsGetBorrowerId", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);

		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "NpaDetailsGetBorrowerId", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * //For NPA Details This method shows the NPA details for the selected
	 * borrower
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showNPADetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// System.out.println("---entered in showNPADetails------");
		Log.log(Log.INFO, "GMAction", "showNPADetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.getRecProcedures().clear();
		String memberId = gmActionForm.getMemberId();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String cgpan = (gmActionForm.getCgpan());
		String borrowerName = gmActionForm.getBorrowerName();

		int type;

		GMProcessor gmProcessor = new GMProcessor();
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		HttpSession session = request.getSession(false);

		if ((borrowerId != null) && (!borrowerId.equals(""))) {
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");

				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}

		} else if ((cgpan != null) && (!cgpan.equals(""))) {
			type = 1;

			borrowerId = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.INFO, "GMAction", "showOutstandingDetails",
					" Bid For Pan - " + borrowerId);
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			gmActionForm.setBorrowerId(borrowerId);

		} else if ((borrowerName != null) && (!borrowerName.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			} else {
				session.setAttribute("displayFlag", "5");
				gmActionForm.setBorrowerIds(bIdForBorrowerName);

				return mapping.findForward("bidList");
			}

		}

		HashMap inputDetail = new HashMap();
		inputDetail.put("memberId", memberId);
		inputDetail.put("borrowerId", borrowerId);
		// inputDetail.put("cgpan",cgpan);

		session.setAttribute("inputDetail", inputDetail);

		return mapping.findForward(Constants.SUCCESS);
	}

	/*
	 * This is new method to get npa details. Added on 14/10/2013
	 */

	public ActionForward showNpaDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// System.out.println("---entered in showNPADetailsNew------");
		Log.log(Log.INFO, "GMAction", "showNPADetails", "Entered");

		// System.out.println("taking npa details");
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new MessageException("Please re-login and try again.");
		}
		DynaValidatorActionForm gmActionForm = (DynaValidatorActionForm) form;
		gmActionForm.initialize(mapping);
		// gmActionForm.reset(mapping,request);

		// System.out.println("npaDt"+gmActionForm.get("npaDt")+"  isAsPerRBI"+gmActionForm.get("isAsPerRBI")+"  npaConfirm"+gmActionForm.get("npaConfirm")+"  isAcctReconstructed"+gmActionForm.get("isAcctReconstructed"));

		/* clearing npa form */
		gmActionForm.set("borrowerId", "");
		gmActionForm.set("borrowerName", "");
		gmActionForm.set("operationType", "");
		gmActionForm.set("size", 0);
		gmActionForm.set("totalApprovedAmount", 0.0);
		gmActionForm.set("totalSecurityAsOnSanc", 0.0);
		gmActionForm.set("totalSecurityAsOnNpa", 0.0);
		gmActionForm.set("npaId", "");
		gmActionForm.set("npaDt", null);
		gmActionForm.set("isAsPerRBI", "");
		gmActionForm.set("npaConfirm", "");
		gmActionForm.set("npaReason", "");
		gmActionForm.set("effortsTaken", "");
		gmActionForm.set("isAcctReconstructed", "");
		gmActionForm.set("subsidyFlag", "");
		gmActionForm.set("isSubsidyRcvd", "");
		gmActionForm.set("isSubsidyAdjusted", "");
		gmActionForm.set("subsidyLastRcvdAmt", 0.0);
		gmActionForm.set("subsidyLastRcvdDt", null);
		gmActionForm.set("lastInspectionDt", null);
		gmActionForm.set("securityAsOnSancDt", null);
		gmActionForm.set("securityAsOnNpaDt", null);
		gmActionForm.set("networthAsOnSancDt", 0.0);
		gmActionForm.set("networthAsOnNpaDt", 0.0);
		gmActionForm.set("reasonForReductionAsOnNpaDt", "");
		gmActionForm.set("cgpansVector", null);

		// System.out.println("npaDt"+gmActionForm.get("npaDt")+"  isAsPerRBI"+gmActionForm.get("isAsPerRBI")+"  npaConfirm"+gmActionForm.get("npaConfirm")+"  isAcctReconstructed"+gmActionForm.get("isAcctReconstructed"));

		HashMap inputDetail = (HashMap) session.getAttribute("inputDetail");

		String memberId = (String) inputDetail.get("memberId");
		String borrowerId = (String) inputDetail.get("borrowerId");
		// String cgpan = (String)inputDetail.get("cgpan");

		session.removeAttribute("inputDetail");

		gmActionForm.set("memberId", memberId);
		gmActionForm.set("borrowerId", borrowerId);
		// gmActionForm.set("cgpan",cgpan);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String unitName = null;
		int noOfClaims = 0;
		String query = null;

		conn = DBConnection.getConnection();

		query = "select ssi_unit_name from ssi_detail where bid='" + borrowerId
				+ "'";

		// }else if(cgpan != null || cgpan != ""){
		// query =
		// "select ssi_unit_name from ssi_detail where ssi_reference_number=(select ssi_reference_number from application_detail where cgpan='"
		// + cgpan + "')";
		// }

		try {
			if (conn != null) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
			}

			if (rs.next()) {
				unitName = rs.getString("ssi_unit_name");
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;

			query = "select sum(cnt) totcnt from\n" + "(\n"
					+ "select count(*) cnt from claim_detail_temp where bid ='"
					+ borrowerId + "'\n" + "union all\n"
					+ "select count(*) cnt from claim_detail where bid ='"
					+ borrowerId + "')";

			if (conn != null) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
			}
			if (rs.next()) {
				noOfClaims = rs.getInt("totcnt");
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (Exception e) {

		} finally {
			DBConnection.freeConnection(conn);
		}

		if (noOfClaims > 0) {
			throw new MessageException(
					"Npa details can not be update because claim has already been filed.");
		}

		gmActionForm.set("unitName", unitName);

		GMProcessor gmProcessor = new GMProcessor();

		NPADetails npaDetails = gmProcessor.getNPADetails(borrowerId);

		GMDAO dao = new GMDAO();
		Vector cgpans = new Vector();
		Vector tccgpans = new Vector();
		Vector wccgpans = new Vector();
		// if(npaDetails == null){
		Vector cgpnDetails = dao.getCGPANDetailsNPA(borrowerId, memberId); // here
																			// guaranteestartdate,sanctiondate,approved_amount,loanType,appStatus

		if (cgpnDetails != null) {
			if (cgpnDetails.size() == 0) {
				throw new NoDataException(
						"There are no Loan Account(s) for this Borrower or the existing Loan Account(s) may have been closed.");
			}
		} else {
			throw new NoDataException(
					"There are no Loan Account(s) for this Borrower or the existing Loan Account(s) may have been closed.");
		}

		HashMap hashmap = null;
		ApplicationDAO appDao = new ApplicationDAO();
		Administrator admin = new Administrator();
		ParameterMaster pm = (ParameterMaster) admin.getParameter();
		// int periodTenureExpiryLodgementClaims =
		// pm.getPeriodTenureExpiryLodgementClaims();
		// java.util.Date currentDate = new java.util.Date();
		double totalApprovedAmount = 0.0;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String guardatestr = null;
		String sanctiondtstr = null;
		for (int i = 0; i < cgpnDetails.size(); i++) {
			hashmap = (HashMap) cgpnDetails.elementAt(i);

			if (hashmap != null) {

				String cgpanNO = (String) hashmap.get("CGPAN");
				String cgpanStatus = (String) hashmap.get("APPLICATION_STATUS");
				String loanType = (String) hashmap.get("CGPAN_LOAN_TYPE");
				java.util.Date guarStartDt = (java.util.Date) hashmap
						.get("GUARANTEE_START_DT"); // -------------guar date
				java.util.Date sanctionDt = (java.util.Date) hashmap
						.get("SANCTION_DT");
				Double approvedAmount = (Double) hashmap.get("APPROVED_AMOUNT");
				Double rate = (Double) hashmap.get("RATE");
				double r = 0.0;
				double appAmt = 0.0;
				if (approvedAmount != null || !approvedAmount.equals("")) {
					appAmt = approvedAmount.doubleValue();
				}
				totalApprovedAmount = totalApprovedAmount + appAmt;

				if (rate != null || !("".equals(rate))) {
					r = rate.doubleValue();
				}

				if (guarStartDt != null) {
					guardatestr = sdf.format(guarStartDt);
				}

				if (sanctionDt != null) {
					sanctiondtstr = sdf.format(sanctionDt);
				}

				if ((cgpanNO != null) && (!(cgpanNO.equals("")))) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpanNO);
					map.put("CGPAN_LOAN_TYPE", loanType);
					map.put("GUARANTEE_START_DT", guardatestr);
					map.put("SANCTION_DT", sanctiondtstr);
					map.put("APPROVED_AMOUNT", appAmt);
					map.put("RATE", r);

					if (!cgpans.contains(cgpanNO)) {
						cgpans.addElement(map);
					}
				}

			}
		}
		// System.out.println("totalApprovedAmount:"+totalApprovedAmount);
		// System.out.println("size:"+cgpans.size());

		gmActionForm.set("cgpansVector", cgpans);
		gmActionForm.set("totalApprovedAmount", totalApprovedAmount);
		gmActionForm.set("size", cgpans.size());

		String totalLandValueStr = "";
		Double totalLandValDouble = 0.0;
		String totalMachineValueStr = "";
		Double totalMachineValDouble = 0.0;
		String totalBldgValueStr = "";
		Double totalBldgValueDouble = 0.0;
		String totalOFMAValueStr = "";
		Double totalOFMAValDouble = 0.0;
		String totalCurrAssetsValueStr = "";
		Double totalCurrAssetsValDouble = 0.0;
		String totalOthersValueStr = "";
		Double totalOthersValDouble = 0.0;
		HashMap hashmap2 = null;
		Vector securityVector = new Vector();
		Map securityMap = new HashMap();
		Map securitymapnpa = new HashMap();
		double networthAsOnSancDt = 0.0;
		Double totalNetWorthDouble = 0.0;
		double networthAsOnNpaDt = 0.0;

		double totalSecurityAsOnSanc = 0.0;
		double totalSecurityAsOnNpa = 0.0;

		Map securitydetails = null;

		if (npaDetails == null) {
			// securitydetails = dao.getPrimarySecurity(borrowerId,memberId);
			securitydetails = dao
					.getPrimarySecurityAndNetworthOfGuarantorsAsOnSanc(
							borrowerId, memberId);

			if (securitydetails != null) {

				totalNetWorthDouble = (Double) securitydetails.get("networth");
				if (totalNetWorthDouble != null) {
					networthAsOnSancDt = totalNetWorthDouble.doubleValue();
				}

				totalLandValDouble = (Double) securitydetails.get("land");
				if (totalLandValDouble != null) {
					if (totalLandValDouble.doubleValue() > 0.0) {
						totalLandValueStr = String.valueOf(totalLandValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalLandValDouble.doubleValue();
					} else {
						totalLandValueStr = "";
					}
				}

				totalMachineValDouble = (Double) securitydetails.get("machine");
				if (totalMachineValDouble != null) {
					if (totalMachineValDouble.doubleValue() > 0.0) {
						totalMachineValueStr = String
								.valueOf(totalMachineValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalMachineValDouble.doubleValue();
					} else {
						totalMachineValueStr = "";
					}
				}
				totalBldgValueDouble = (Double) securitydetails.get("building");
				if (totalBldgValueDouble != null) {
					if (totalBldgValueDouble.doubleValue() > 0.0) {
						totalBldgValueStr = String
								.valueOf(totalBldgValueDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalBldgValueDouble.doubleValue();
					} else {
						totalBldgValueStr = "";
					}
				}
				totalOFMAValDouble = (Double) securitydetails
						.get("fixed_mov_asset");
				if (totalOFMAValDouble != null) {
					if (totalOFMAValDouble.doubleValue() > 0.0) {
						totalOFMAValueStr = String.valueOf(totalOFMAValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalOFMAValDouble.doubleValue();
					} else {
						totalOFMAValueStr = "";
					}
				}
				totalCurrAssetsValDouble = (Double) securitydetails
						.get("current_asset");
				if (totalCurrAssetsValDouble != null) {
					if (totalCurrAssetsValDouble.doubleValue() > 0.0) {
						totalCurrAssetsValueStr = String
								.valueOf(totalCurrAssetsValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalCurrAssetsValDouble.doubleValue();
					} else {
						totalCurrAssetsValueStr = "";
					}
				}
				totalOthersValDouble = (Double) securitydetails.get("others");
				if (totalOthersValDouble != null) {
					if (totalOthersValDouble.doubleValue() > 0.0) {
						totalOthersValueStr = String
								.valueOf(totalOthersValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalOthersValDouble.doubleValue();
					} else {
						totalOthersValueStr = "";
					}
				}
			}
			securityMap.put("LAND", totalLandValueStr);
			securityMap.put("MACHINE", totalMachineValueStr);
			securityMap.put("BUILDING", totalBldgValueStr);
			securityMap.put("OTHER_FIXED_MOVABLE_ASSETS", totalOFMAValueStr);
			securityMap.put("CUR_ASSETS", totalCurrAssetsValueStr);
			securityMap.put("OTHERS", totalOthersValueStr);

			securitymapnpa.put("LAND", "");
			securitymapnpa.put("MACHINE", "");
			securitymapnpa.put("BUILDING", "");
			securitymapnpa.put("OTHER_FIXED_MOVABLE_ASSETS", "");
			securitymapnpa.put("CUR_ASSETS", "");
			securitymapnpa.put("OTHERS", "");

			gmActionForm.set("securityAsOnSancDt", securityMap);
			gmActionForm.set("networthAsOnSancDt", networthAsOnSancDt);
			gmActionForm.set("totalSecurityAsOnSanc", totalSecurityAsOnSanc);

			gmActionForm.set("securityAsOnNpaDt", securitymapnpa);
			gmActionForm.set("networthAsOnNpaDt", 0.0);
			gmActionForm.set("totalSecurityAsOnNpa", 0.0);

		}

		if (npaDetails == null) {
			Log.log(Log.INFO, "GMAction", "showNPADetails",
					"Npa Details is Null");

			// gmActionForm.getRecProcedures().put("key-0",new
			// RecoveryProcedureTemp());

			// gmActionForm.resetNpaDetailsPage(mapping,request);
			session.setAttribute("npaAvailable", null);

			session.setAttribute("recInitiated", "N");
		}

		if (npaDetails != null) {
			String operationType = "NCU";
			Date npaCreatedDate = npaDetails.getNpaCreatedDate();
			if (npaCreatedDate == null) {
				operationType = "OCU";
			}

			String npaId = npaDetails.getNpaId();

			session.setAttribute("npaAvailable", npaId);
			CustomisedDate custom = new CustomisedDate();
			custom.setDate(npaDetails.getNpaDate());

			gmActionForm.set("npaId", npaId);
			gmActionForm.set("npaDt", custom);
			gmActionForm.set("isAsPerRBI", npaDetails.getIsAsPerRBI());
			gmActionForm.set("npaConfirm", npaDetails.getNpaConfirm());
			gmActionForm.set("npaReason", npaDetails.getNpaReason());
			gmActionForm.set("effortsTaken", npaDetails.getEffortsTaken());
			gmActionForm.set("isAcctReconstructed",
					npaDetails.getIsAcctReconstructed());
			gmActionForm.set("subsidyFlag", npaDetails.getSubsidyFlag());
			gmActionForm.set("isSubsidyRcvd", npaDetails.getIsSubsidyRcvd());
			gmActionForm.set("isSubsidyAdjusted",
					npaDetails.getIsSubsidyAdjusted());
			gmActionForm.set("subsidyLastRcvdAmt",
					npaDetails.getSubsidyLastRcvdAmt());
			custom = new CustomisedDate();
			custom.setDate(npaDetails.getSubsidyLastRcvdDt());
			gmActionForm.set("subsidyLastRcvdDt", custom);
			custom = new CustomisedDate();
			custom.setDate(npaDetails.getLastInspectionDt());
			gmActionForm.set("lastInspectionDt", custom);
			gmActionForm.set("npaCreatedDate", npaCreatedDate);
			gmActionForm.set("operationType", operationType);
			Vector allCgpans = null;

			allCgpans = dao.getCgpanDetailsAsOnNpa(npaId);

			CustomisedDate custom2 = null;
			for (int i = 1; i <= cgpans.size(); i++) {
				Map map = (Map) cgpans.get(i - 1);
				String cgpanNo = (String) map.get("CGPAN");
				// logic to set all properties fdd,ldd,fid,pm,im
				if (allCgpans != null) {
					for (int j = 0; j < allCgpans.size(); j++) {

						Map cgMap = (Map) allCgpans.get(j);
						String cg = (String) cgMap.get("CGPAN");
						String loanType = cg.substring(cg.length() - 2);
						if (cgpanNo.equals(cg)) {
							gmActionForm.set("cgpan" + i, cgpanNo);
							if ("TC".equals(loanType) || "CC".equals(loanType)) {
								custom2 = new CustomisedDate();
								custom2.setDate((java.util.Date) cgMap
										.get("FIRSTDISBDT"));
								gmActionForm.set("firstDisbDt" + i, custom2);
								custom2 = new CustomisedDate();
								custom2.setDate((java.util.Date) cgMap
										.get("LASTDISBDT"));
								gmActionForm.set("lastDisbDt" + i, custom2);
								custom2 = new CustomisedDate();
								custom2.setDate((java.util.Date) cgMap
										.get("FIRSTINSTDT"));
								gmActionForm.set("firstInstDt" + i, custom2);
								gmActionForm.set("moratoriumPrincipal" + i,
										(Integer) cgMap
												.get("PRINCIPALMORATORIUM"));
								gmActionForm.set("moratoriumInterest" + i,
										(Integer) cgMap
												.get("INTERESTMORATORIUM"));

								gmActionForm.set("totalDisbAmt" + i,
										(Double) cgMap.get("TOTALDISBAMT"));
								gmActionForm.set("repayPrincipal" + i,
										(Double) cgMap.get("PRINCIPALREPAY"));
								gmActionForm.set("repayInterest" + i,
										(Double) cgMap.get("INTERESTREPAY"));
							}
							gmActionForm.set("outstandingPrincipal" + i,
									(Double) cgMap.get("PRINCIPALOS"));
							gmActionForm.set("outstandingInterest" + i,
									(Double) cgMap.get("INTERESTOS"));
							break;
						}
					}
				}

			}

			GMAction gm = new GMAction();
			totalLandValueStr = "";
			totalMachineValueStr = "";
			totalBldgValueStr = "";
			totalOFMAValueStr = "";
			totalCurrAssetsValueStr = "";
			totalOthersValueStr = "";
			securitydetails = null;

			CPDAO cpdao = new CPDAO();

			securitydetails = cpdao
					.getPrimarySecurityAndNetworthOfGuarantors(npaId);
			HashMap sancmap = (HashMap) securitydetails.get("SAN");
			HashMap npamap = (HashMap) securitydetails.get("NPA");

			if (sancmap != null) {

				totalNetWorthDouble = (Double) sancmap.get("networth");
				if (totalNetWorthDouble != null) {
					networthAsOnSancDt = totalNetWorthDouble.doubleValue();
				}

				totalLandValDouble = (Double) sancmap.get("LAND");
				if (totalLandValDouble != null) {
					if (totalLandValDouble.doubleValue() > 0.0) {
						totalLandValueStr = String.valueOf(totalLandValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalLandValDouble.doubleValue();
					} else {
						totalLandValueStr = "";
					}
				}

				totalMachineValDouble = (Double) sancmap.get("MACHINE");
				if (totalMachineValDouble != null) {
					if (totalMachineValDouble.doubleValue() > 0.0) {
						totalMachineValueStr = String
								.valueOf(totalMachineValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalMachineValDouble.doubleValue();
					} else {
						totalMachineValueStr = "";
					}
				}
				totalBldgValueDouble = (Double) sancmap.get("BUILDING");
				if (totalBldgValueDouble != null) {
					if (totalBldgValueDouble.doubleValue() > 0.0) {
						totalBldgValueStr = String
								.valueOf(totalBldgValueDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalBldgValueDouble.doubleValue();
					} else {
						totalBldgValueStr = "";
					}
				}
				totalOFMAValDouble = (Double) sancmap
						.get("OTHER FIXED MOVABLE ASSETS");
				if (totalOFMAValDouble != null) {
					if (totalOFMAValDouble.doubleValue() > 0.0) {
						totalOFMAValueStr = String.valueOf(totalOFMAValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalOFMAValDouble.doubleValue();
					} else {
						totalOFMAValueStr = "";
					}
				}
				totalCurrAssetsValDouble = (Double) sancmap
						.get("CURRENT ASSETS");
				if (totalCurrAssetsValDouble != null) {
					if (totalCurrAssetsValDouble.doubleValue() > 0.0) {
						totalCurrAssetsValueStr = String
								.valueOf(totalCurrAssetsValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalCurrAssetsValDouble.doubleValue();
					} else {
						totalCurrAssetsValueStr = "";
					}
				}
				totalOthersValDouble = (Double) sancmap.get("OTHERS");
				if (totalOthersValDouble != null) {
					if (totalOthersValDouble.doubleValue() > 0.0) {
						totalOthersValueStr = String
								.valueOf(totalOthersValDouble);
						totalSecurityAsOnSanc = totalSecurityAsOnSanc
								+ totalOthersValDouble.doubleValue();
					} else {
						totalOthersValueStr = "";
					}
				}
			}
			securityMap.put("LAND", totalLandValueStr);
			securityMap.put("MACHINE", totalMachineValueStr);
			securityMap.put("BUILDING", totalBldgValueStr);
			securityMap.put("OTHER_FIXED_MOVABLE_ASSETS", totalOFMAValueStr);
			securityMap.put("CUR_ASSETS", totalCurrAssetsValueStr);
			securityMap.put("OTHERS", totalOthersValueStr);

			totalLandValueStr = "";
			totalMachineValueStr = "";
			totalBldgValueStr = "";
			totalOFMAValueStr = "";
			totalCurrAssetsValueStr = "";
			totalOthersValueStr = "";
			securitydetails = null;
			String reasonReduction = "";

			// securitydetails =
			// dao.getPrimarySecurityAndNetworthOfGuarantorsAsOnNpa(borrowerId,memberId,npaId);

			if (npamap != null) {

				totalNetWorthDouble = (Double) npamap.get("networth");
				if (totalNetWorthDouble != null) {
					networthAsOnNpaDt = totalNetWorthDouble.doubleValue();
				}

				totalLandValDouble = (Double) npamap.get("LAND");
				if (totalLandValDouble != null) {
					if (totalLandValDouble.doubleValue() > 0.0) {
						totalLandValueStr = String.valueOf(totalLandValDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalLandValDouble.doubleValue();
					} else {
						totalLandValueStr = "";
					}
				}

				totalMachineValDouble = (Double) npamap.get("MACHINE");
				if (totalMachineValDouble != null) {
					if (totalMachineValDouble.doubleValue() > 0.0) {
						totalMachineValueStr = String
								.valueOf(totalMachineValDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalMachineValDouble.doubleValue();
					} else {
						totalMachineValueStr = "";
					}
				}
				totalBldgValueDouble = (Double) npamap.get("BUILDING");
				if (totalBldgValueDouble != null) {
					if (totalBldgValueDouble.doubleValue() > 0.0) {
						totalBldgValueStr = String
								.valueOf(totalBldgValueDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalBldgValueDouble.doubleValue();
					} else {
						totalBldgValueStr = "";
					}
				}
				totalOFMAValDouble = (Double) npamap
						.get("OTHER FIXED MOVABLE ASSETS");
				if (totalOFMAValDouble != null) {
					if (totalOFMAValDouble.doubleValue() > 0.0) {
						totalOFMAValueStr = String.valueOf(totalOFMAValDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalOFMAValDouble.doubleValue();
					} else {
						totalOFMAValueStr = "";
					}
				}
				totalCurrAssetsValDouble = (Double) npamap
						.get("CURRENT ASSETS");
				if (totalCurrAssetsValDouble != null) {
					if (totalCurrAssetsValDouble.doubleValue() > 0.0) {
						totalCurrAssetsValueStr = String
								.valueOf(totalCurrAssetsValDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalCurrAssetsValDouble.doubleValue();
					} else {
						totalCurrAssetsValueStr = "";
					}
				}
				totalOthersValDouble = (Double) npamap.get("OTHERS");
				if (totalOthersValDouble != null) {
					if (totalOthersValDouble.doubleValue() > 0.0) {
						totalOthersValueStr = String
								.valueOf(totalOthersValDouble);
						totalSecurityAsOnNpa = totalSecurityAsOnNpa
								+ totalOthersValDouble.doubleValue();
					} else {
						totalOthersValueStr = "";
					}
				}
				reasonReduction = (String) npamap.get("reasonReduction");
			}
			securitymapnpa.put("LAND", totalLandValueStr);
			securitymapnpa.put("MACHINE", totalMachineValueStr);
			securitymapnpa.put("BUILDING", totalBldgValueStr);
			securitymapnpa.put("OTHER_FIXED_MOVABLE_ASSETS", totalOFMAValueStr);
			securitymapnpa.put("CUR_ASSETS", totalCurrAssetsValueStr);
			securitymapnpa.put("OTHERS", totalOthersValueStr);

			gmActionForm.set("securityAsOnSancDt", securityMap);
			gmActionForm.set("networthAsOnSancDt", networthAsOnSancDt);
			gmActionForm.set("totalSecurityAsOnSanc", totalSecurityAsOnSanc);

			gmActionForm.set("securityAsOnNpaDt", securitymapnpa);
			gmActionForm.set("networthAsOnNpaDt", networthAsOnNpaDt);
			gmActionForm.set("reasonForReductionAsOnNpaDt", reasonReduction);
			gmActionForm.set("totalSecurityAsOnNpa", totalSecurityAsOnNpa);
		}

		Log.log(Log.INFO, "GMAction", "showNPADetails", "Exited");

		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward addMoreRecoProcs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "GMAction", "addMoreRecoProcs", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		if (gmActionForm.getIsRecoveryInitiated().equals(Constants.NO)) {

			/*
			 * If Recovery action initiated is 'NO', no need to add more and
			 * more rows. Added by Veldurai on 16th October 2004.
			 */
			return mapping.findForward(Constants.SUCCESS);
		}

		Map recoveryProcedures = gmActionForm.getRecProcedures();

		Set recoveryProceduresSet = recoveryProcedures.keySet();

		Iterator recoveryProceduresIterator = recoveryProceduresSet.iterator();

		String count = null;

		while (recoveryProceduresIterator.hasNext()) {
			String key = (String) recoveryProceduresIterator.next();

			Log.log(Log.DEBUG, "GMAction", "addMoreRecoProcs", " key " + key);

			count = key.substring(key.indexOf("-") + 1, key.length());

			Log.log(Log.DEBUG, "GMAction", "addMoreRecoProcs", " count "
					+ count);
		}

		request.setAttribute("IsRecProcRequired", new Boolean(true));

		Log.log(Log.INFO, "GMAction", "addMoreRecoProcs", "Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * 
	 * This method saves the NPA Details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveNpaDetailsOld(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveNpaDetails", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		HttpSession session = (HttpSession) request.getSession(false);

		NPADetails npaDetail = new NPADetails();
		LegalSuitDetail legalSuitDetail = new LegalSuitDetail();

		GMActionForm gmActionForm = (GMActionForm) form;
		String message = "NPA Details Saved Successfully.";
		int noOfActions = gmActionForm.getNoOfActions(); // ---------------------------------------------number
															// of
															// actions--------remove
		npaDetail.setNoOfActions(noOfActions);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "No of Act-"
				+ noOfActions);

		String borrowerId = gmActionForm.getBorrowerId().toUpperCase(); // ------------------------------borrower
																		// id
		npaDetail.setCgbid(borrowerId);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "bid-" + borrowerId);

		Date npaTurnDate = gmActionForm.getNpaDate(); // ------------------------------------------------npa
														// date---------ok
		npaDetail.setNpaDate(npaTurnDate);

		// throw new DatabaseException("Success");

		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "npaTurnDt-"
				+ npaTurnDate);

		double osAmt = gmActionForm.getOsAmtOnNPA(); // -------------------------------------------outstanding
														// amount-----------------
		npaDetail.setOsAmtOnNPA(osAmt);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "osAmt-" + osAmt);

		String npaReported = gmActionForm.getWhetherNPAReported(); // ---------------------------------whether
																	// npa
																	// reported
		npaDetail.setWhetherNPAReported(npaReported);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "npa Rep"
				+ npaReported);

		Date reportingDate = gmActionForm.getReportingDate(); // ----------------------------------------reporting
																// date
		npaDetail.setReportingDate(reportingDate);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "rep Dt"
				+ reportingDate);

		String npaReason = gmActionForm.getNpaReason(); // ----------------------------------------------npa
														// reason
		npaDetail.setNpaReason(npaReason);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "Reason" + npaReason);

		String willfulDefaulter = gmActionForm.getWillfulDefaulter(); // ---------------------------------willful
																		// defaulter
		npaDetail.setWillfulDefaulter(willfulDefaulter);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "will Defaul"
				+ willfulDefaulter);

		String effortsTaken = gmActionForm.getEffortsTaken(); // ------------------------------------------efforts
																// taken
		npaDetail.setEffortsTaken(effortsTaken);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "eff taken"
				+ effortsTaken);

		String isRecoveryInitiated = gmActionForm.getIsRecoveryInitiated(); // ------------------------------recovery
																			// initiated
		npaDetail.setIsRecoveryInitiated(isRecoveryInitiated);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "is rec int"
				+ isRecoveryInitiated);

		Date effortsConclusionDate = gmActionForm.getEffortsConclusionDate(); // ---------------------------effort
																				// conclusion
		npaDetail.setEffortsConclusionDate(effortsConclusionDate);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "efrt Conl Dt"
				+ effortsConclusionDate);

		String commentOnFinPosition = gmActionForm.getMliCommentOnFinPosition(); // ------------------------mli
																					// comment
		npaDetail.setMliCommentOnFinPosition(commentOnFinPosition);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "Fin Pos"
				+ commentOnFinPosition);

		String finAssistDetails = gmActionForm.getDetailsOfFinAssistance(); // -----------------------------finanace
																			// assistance
		npaDetail.setDetailsOfFinAssistance(finAssistDetails);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "fin Ass Dtl"
				+ finAssistDetails);

		String creditSupport = gmActionForm.getCreditSupport(); // ----------------------------------------credit
																// support
		npaDetail.setCreditSupport(creditSupport);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "cr suprt"
				+ creditSupport);

		String bankFacility = gmActionForm.getBankFacilityDetail(); // -------------------------------------bank
																	// facility
		npaDetail.setBankFacilityDetail(bankFacility);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "bankFacility"
				+ bankFacility);

		String watchList = gmActionForm.getPlaceUnderWatchList(); // ------------------------------------watch
																	// list
		npaDetail.setPlaceUnderWatchList(watchList);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "watchList"
				+ watchList);

		String remarks = gmActionForm.getRemarksOnNpa(); // ---------------------------------------------remarks
		npaDetail.setRemarksOnNpa(remarks);
		Log.log(Log.DEBUG, "GMAction", "saveNpaDetails", "remarks" + remarks);

		/*
		 * here set NPAApplication object inside it set tcloandetails
		 * ,wcloandetails,primarysecuritydetails inside primarysecuritydetails
		 * set details as on sancdate
		 */

		TCLoanDetails tcLoanDetails = null;
		Vector tccgpans = gmActionForm.getTccgpansVector();
		Map tcCgpans = gmActionForm.getTcCgpanMap();
		Map lastDisbursementDates = gmActionForm.getLastDisbursementDates();
		Map tcDisbursementAmount = gmActionForm.getTcDisbursementAmount();
		Map repaymentPrincipal = gmActionForm.getRepaymentAmountPrincipal();
		Map repaymentInterest = gmActionForm.getRepaymentAmountInterest();
		Map tcPrincipalOS = gmActionForm.getTcPrincipalOutstandingAmount();
		Map tcInterestOS = gmActionForm.getTcInterestOutstandingAmount();
		ArrayList tcDetails = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// special fields for outstanding amount
		double totalTCOS = 0.0d;
		double totalWCOS = 0.0d;
		Iterator tccgpansIterator = tccgpans.iterator();
		while (tccgpansIterator.hasNext()) {

			HashMap map = (HashMap) tccgpansIterator.next();
			String key = (String) map.get("CGPAN");
			tcLoanDetails = new TCLoanDetails();
			String cgpan = (String) tcCgpans.get("key-" + key);
			String lastDisbDate = (String) lastDisbursementDates.get("key-"
					+ key);
			Date disbdate = null;

			String disbAmount = (String) tcDisbursementAmount.get("key-" + key);
			String repaymentPrincipalAmount = (String) repaymentPrincipal
					.get("key-" + key);
			String repaymentInterestAmount = (String) repaymentInterest
					.get("key-" + key);
			String principalOSAmount = (String) tcPrincipalOS.get("key-" + key);
			String interestOSAmount = (String) tcInterestOS.get("key-" + key);

			//System.out.println("cgpan:" + cgpan + "---disbdate:" + lastDisbDate
					//+ "---disbamount:" + disbAmount + "---tcprincipal:"
					//+ repaymentPrincipalAmount + "---tcinterest:"
					//+ repaymentInterestAmount + "-----principalOSAmount:"
					//+ principalOSAmount + "---interestOSAmount"
					//+ interestOSAmount);

			tcLoanDetails.setCgpan(cgpan);
			if (lastDisbDate != null || lastDisbDate != "") {
				disbdate = sdf.parse(lastDisbDate, new ParsePosition(0));
				tcLoanDetails.setLastDisbursementDate(disbdate);
			}
			if (disbAmount.equals("") || disbAmount == null)
				disbAmount = "0.0";
			if (repaymentPrincipalAmount.equals("")
					|| repaymentPrincipalAmount == null)
				repaymentPrincipalAmount = "0.0";
			if (repaymentInterestAmount.equals("")
					|| repaymentInterestAmount == null)
				repaymentInterestAmount = "0.0";
			if (principalOSAmount.equals("") || principalOSAmount == null)
				principalOSAmount = "0.0";
			if (interestOSAmount.equals("") || interestOSAmount == null)
				interestOSAmount = "0.0";

			totalTCOS = totalTCOS + Double.parseDouble(principalOSAmount)
					+ Double.parseDouble(interestOSAmount);

			tcLoanDetails.setDisbursementAmount(Double.parseDouble(disbAmount));
			tcLoanDetails.setPrincipalRepayment(Double
					.parseDouble(repaymentPrincipalAmount));
			tcLoanDetails.setInterestAndOtherCharges(Double
					.parseDouble(repaymentInterestAmount));
			tcLoanDetails.setOsAsOnSancDtPrinciple(Double
					.parseDouble(principalOSAmount));
			tcLoanDetails.setOsAsOnSancDtInterest(Double
					.parseDouble(interestOSAmount));
			tcDetails.add(tcLoanDetails);

		}

		WCLoanDetails wcLoanDetails = null;
		Vector wccgpans = gmActionForm.getWccgpansVector();
		Map wcCgpanMap = gmActionForm.getWcCgpanMap();
		Map wcDisbursementAmount = gmActionForm.getWcDisbursementAmount();
		Map wcFBPrincipalOS = gmActionForm.getWcFBPrincipalOutstandingAmount();
		Map wcFBInterestOS = gmActionForm.getWcFBInterestOutstandingAmount();
		ArrayList wcDetails = new ArrayList();

		Iterator wccgpanIterator = wccgpans.iterator();

		while (wccgpanIterator.hasNext()) {
			HashMap map = (HashMap) wccgpanIterator.next();
			String key = (String) map.get("CGPAN");
			wcLoanDetails = new WCLoanDetails();
			String cgpan = (String) wcCgpanMap.get("key-" + key);
			String disbAmount = (String) wcDisbursementAmount.get("key-" + key);
			String wcFBPrincipalOSAmount = (String) wcFBPrincipalOS.get("key-"
					+ key);
			String wcFBInterestOSAmount = (String) wcFBInterestOS.get("key-"
					+ key);

			// if(disbAmount.equals("") || disbAmount == null)
			// disbAmount = "0.0";
			// if(wcFBPrincipalOSAmount.equals("") || wcFBPrincipalOSAmount ==
			// null)
			// wcFBPrincipalOSAmount = "0.0";
			// if(wcFBInterestOSAmount.equals("") || wcFBInterestOSAmount ==
			// null)
			// wcFBInterestOSAmount = "0.0";

			//System.out.println("cgpan:" + cgpan + "---disbAmount:" + disbAmount
				//	+ "---wcFBPrincipalOSAmount:" + wcFBPrincipalOSAmount
				//	+ "---wcFBInterestOSAmount:" + wcFBInterestOSAmount);

			totalWCOS = totalWCOS + Double.parseDouble(wcFBInterestOSAmount)
					+ Double.parseDouble(wcFBPrincipalOSAmount);

			wcLoanDetails.setCgpan(cgpan);
			wcLoanDetails.setDisbursementAmount(Double.parseDouble(disbAmount));
			wcDetails.add(wcLoanDetails);

		}

		npaDetail.setOsAmtOnNPA((totalTCOS + totalWCOS));

		Map asOnDtOfSanction = gmActionForm.getAsOnDtOfSanctionDtl();
		DtlsAsOnDateOfSanction dtlsAsOnDateOfSanction = new DtlsAsOnDateOfSanction();

		String landAsOnSanc = (String) asOnDtOfSanction.get("LAND");
		String buildingAsOnSanc = (String) asOnDtOfSanction.get("BUILDING");
		String machineAsOnSanc = (String) asOnDtOfSanction.get("MACHINE");
		String otherAssetsAsOnSanc = (String) asOnDtOfSanction
				.get("OTHER_FIXED_MOVABLE_ASSETS");
		String currentAssetsAsOnSanc = (String) asOnDtOfSanction
				.get("CUR_ASSETS");
		String othersAsOnSanc = (String) asOnDtOfSanction.get("OTHERS");

		if (landAsOnSanc == null || landAsOnSanc.equals(""))
			landAsOnSanc = "0.0";
		if (buildingAsOnSanc == null || buildingAsOnSanc.equals(""))
			buildingAsOnSanc = "0.0";
		if (machineAsOnSanc == null || machineAsOnSanc.equals(""))
			machineAsOnSanc = "0.0";
		if (otherAssetsAsOnSanc == null || otherAssetsAsOnSanc.equals(""))
			otherAssetsAsOnSanc = "0.0";
		if (currentAssetsAsOnSanc == null || currentAssetsAsOnSanc.equals(""))
			currentAssetsAsOnSanc = "0.0";
		if (othersAsOnSanc == null || othersAsOnSanc.equals(""))
			othersAsOnSanc = "0.0";

		dtlsAsOnDateOfSanction.setValueOfLand(Double.parseDouble(landAsOnSanc));
		dtlsAsOnDateOfSanction.setValueOfBuilding(Double
				.parseDouble(buildingAsOnSanc));
		dtlsAsOnDateOfSanction.setValueOfMachine(Double
				.parseDouble(machineAsOnSanc));
		dtlsAsOnDateOfSanction.setValueOfCurrentAssets(Double
				.parseDouble(currentAssetsAsOnSanc));
		dtlsAsOnDateOfSanction.setValueOfOtherFixedMovableAssets(Double
				.parseDouble(otherAssetsAsOnSanc));
		dtlsAsOnDateOfSanction.setValueOfOthers(Double
				.parseDouble(othersAsOnSanc));

		Map asOnDtOfNPA = gmActionForm.getAsOnDtOfNPADtl();
		DtlsAsOnDateOfNPA dtlsAsOnDateOfNPA = new DtlsAsOnDateOfNPA();

		String landAsOnNPA = (String) asOnDtOfNPA.get("LAND"); // change to nPA
																// map
		String buildingAsOnNPA = (String) asOnDtOfNPA.get("BUILDING");
		String machineAsOnNPA = (String) asOnDtOfNPA.get("MACHINE");
		String otherAssetsAsOnNPA = (String) asOnDtOfNPA
				.get("OTHER_FIXED_MOVABLE_ASSETS");
		String currentAssetsAsOnNPA = (String) asOnDtOfNPA.get("CUR_ASSETS");
		String othersAsOnNPA = (String) asOnDtOfNPA.get("OTHERS");

		if (landAsOnNPA == null || landAsOnNPA.equals(""))
			landAsOnNPA = "0.0";
		if (buildingAsOnNPA == null || buildingAsOnNPA.equals(""))
			buildingAsOnNPA = "0.0";
		if (machineAsOnNPA == null || machineAsOnNPA.equals(""))
			machineAsOnNPA = "0.0";
		if (currentAssetsAsOnNPA == null || currentAssetsAsOnNPA.equals(""))
			currentAssetsAsOnNPA = "0.0";
		if (otherAssetsAsOnNPA == null || otherAssetsAsOnNPA.equals(""))
			otherAssetsAsOnNPA = "0.0";
		if (othersAsOnNPA == null || othersAsOnNPA.equals(""))
			othersAsOnNPA = "0.0";

		dtlsAsOnDateOfNPA.setValueOfLand(Double.parseDouble(landAsOnNPA));
		dtlsAsOnDateOfNPA.setValueOfBuilding(Double
				.parseDouble(buildingAsOnNPA));
		dtlsAsOnDateOfNPA.setValueOfMachine(Double.parseDouble(machineAsOnNPA));
		dtlsAsOnDateOfNPA.setValueOfCurrentAssets(Double
				.parseDouble(currentAssetsAsOnNPA));
		;
		dtlsAsOnDateOfNPA.setValueOfOtherFixedMovableAssets(Double
				.parseDouble(otherAssetsAsOnNPA));
		dtlsAsOnDateOfNPA.setValueOfOthers(Double.parseDouble(othersAsOnNPA));

		/* here set primary security for both dates in primarysecurity MAP */

		Map primarySecurityMap = new HashMap();
		primarySecurityMap.put("asOnDtOfSanction", dtlsAsOnDateOfSanction);
		primarySecurityMap.put("asOnDtOfNPA", dtlsAsOnDateOfNPA);

		NPADetails npaDetailsFromDB = gmProcessor.getNPADetails(borrowerId);
		String npaId = null;

		if (npaDetailsFromDB != null) {
			npaId = npaDetailsFromDB.getNpaId();
			gmActionForm.setNpaId(npaId);
			npaDetail.setNpaId(npaId);
		}

		ArrayList newRecoveryProcedures = new ArrayList();
		ArrayList modifiedRecoveryProcedures = new ArrayList();

		GMDAO dao = new GMDAO();
		if (npaDetailsFromDB == null) {
			String srcMenu = (String) session.getAttribute("mainMenu");
			// System.out.println("SRC Menu :" + srcMenu);
			String srcSubMenu = (String) session.getAttribute("subMenuItem");
			// System.out.println("SRC Sub Menu :" + srcSubMenu);
			if ((srcMenu != null) && (srcSubMenu != null)) {
				// System.out.println("SRC Menu :" + srcMenu);
				// System.out.println("SRC Sub Menu :" + "srcSubMenu");
				// System.out.println("Control 1");
				if ((srcMenu.equals(MenuOptions
						.getMenu(MenuOptions.CP_CLAIM_FOR)))
						&& (srcSubMenu
								.equals(MenuOptions
										.getMenu(MenuOptions.CP_CLAIM_FOR_FIRST_INSTALLMENT)))) {
					// System.out.println("Control 2");
					// gmProcessor.insertNPADetails(npaDetail,
					// newRecoveryProcedures,
					// modifiedRecoveryProcedures, legalSuitDetail);

					srcMenu = null;
					srcSubMenu = null;
					Log.log(Log.INFO, "GMAction",
							"saveNpaDetails for Claim Application", "Exited");
					return mapping.findForward("claimdetails");
				}
			}
			Log.log(Log.DEBUG, "GMAction", "saveNpaDetails",
					"New NPA Details is Saved");
			// gmProcessor.insertNPADetails(npaDetail, newRecoveryProcedures,
			// modifiedRecoveryProcedures, legalSuitDetail);

			/* here new method for insertNPADetails */
			// gmProcessor.insertNPADetails(npaDetail, repaymentDetail,
			// outstandingDetail);
		} else {
			Log.log(Log.DEBUG, "GMAction", "saveNpaDetails",
					"Modified NPA Details is Saved");
			// gmProcessor.updateNPADetails(npaDetail, newRecoveryProcedures,
			// modifiedRecoveryProcedures, legalSuitDetail);

		}
		request.setAttribute("message", message);
		Log.log(Log.INFO, "GMAction", "saveNpaDetails", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the borrowerId or the cgpan for the Outstanding details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward GetBidCgpanOutstanding(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "GetBidCgpanOutstanding", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		GMProcessor gmProcessor = new GMProcessor();

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);

		Log.log(Log.INFO, "GMAction", "GetBidCgpanOutstanding", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the outstanding details for the borrowerId or the cgpan
	 * selected
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showOutstandingDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showOutstandingDetails", "Entered");

		ArrayList periodicInfoDetails = new ArrayList();

		GMProcessor gmProcessor = new GMProcessor();

		HttpSession session = request.getSession(false);

		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		int type = 0;

		String forward = "";

		ClaimsProcessor processor = new ClaimsProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		// Validating the member Id
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}
		// validating the borr ids.. gets all the bids for the memberid
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}

			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Outstanding Details for this borrower cannot be modified since Claim Application has been submitted");
			}

			periodicInfoDetails = gmProcessor.viewOutstandingDetails(
					borrowerId, type);

			gmActionForm.setOsPeriodicInfoDetails(periodicInfoDetails);

			forward = "success";
		}
		// cgpan
		else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;

			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showOutstandingDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}

			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Outstanding Details for this borrower cannot be modified since Claim Application has been submitted");
			}

			gmActionForm.setBorrowerId(bIdForThisCgpan);
			periodicInfoDetails = gmProcessor.viewOutstandingDetails(
					bIdForThisCgpan, type);

			gmActionForm.setOsPeriodicInfoDetails(periodicInfoDetails);

			bIdForThisCgpan = null;

			forward = "success";
		}
		// borrower name
		else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * } gmActionForm.setBorrowerId(bIdForBorrowerName);
			 * 
			 * periodicInfoDetails =
			 * gmProcessor.viewOutstandingDetails(borrowerName,type);
			 */
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			}
			session.setAttribute("displayFlag", "2");
			gmActionForm.setBorrowerIds(bIdForBorrowerName);
			forward = "bidList";

		}

		Log.log(Log.INFO, "GMAction", "showOutstandingDetails", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(forward);
	}

	/**
	 * 
	 * This method saves the outstanding details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveOutstandingDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveOutstandingDetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = gmActionForm.getBorrowerId();

		Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails", "borrowerId "
				+ borrowerId);

		ArrayList tempPeriodicDtls = gmProcessor.viewOutstandingDetails(
				borrowerId, GMConstants.TYPE_ZERO);
		ArrayList tempPeriodicDtls1 = gmProcessor.viewOutstandingDetails(
				borrowerId, GMConstants.TYPE_ZERO);

		ArrayList tempOutDtls = null;

		ArrayList outDtls = null;
		ArrayList outDtls1 = null;

		ArrayList tempOutAmts = null;
		ArrayList tempOutAmts1 = null;

		ArrayList newWcOutstandingAmounts = new ArrayList();
		ArrayList modifiedWcOutstandingAmounts = new ArrayList();

		ArrayList newTcOutstandingAmounts = new ArrayList();
		ArrayList modifiedTcOutstandingAmounts = new ArrayList();

		OutstandingAmount outstandingAmount = null;

		OutstandingAmount modifiedOutstandingAmount = null;
		OutstandingAmount newOutstandingAmount = null;

		OutstandingAmount modifiedTcOutstandingAmount = null;
		OutstandingAmount newTcOutstandingAmount = null;

		java.util.Map cgpanWcMap = gmActionForm.getCgpansForWc();
		java.util.Set cgpanWcSet = cgpanWcMap.keySet();
		java.util.Iterator cgpanWcIterator = cgpanWcSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails", "cgpanWcMap "
				+ cgpanWcMap.size());

		Map cgpanTcMap = gmActionForm.getCgpansForTc();
		Set cgpanTcSet = cgpanTcMap.keySet();
		Iterator cgpanTcIterator = cgpanTcSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails", "cgpanTcMap "
				+ cgpanTcMap.size());

		Map wcFBPrAmountMap = gmActionForm.getWcFBPrincipalOutstandingAmount();
		Set wcFBPrAmountSet = wcFBPrAmountMap.keySet();
		Iterator wcFBPrAmountIterator = wcFBPrAmountSet.iterator();

		Map wcNFBPrAmountMap = gmActionForm
				.getWcNFBPrincipalOutstandingAmount();
		Set wcNFBPrAmountSet = wcNFBPrAmountMap.keySet();
		Iterator wcNFBPrAmountIterator = wcNFBPrAmountSet.iterator();

		Map wcFBIntAmountMap = gmActionForm.getWcFBInterestOutstandingAmount();

		Map wcNFBIntAmountMap = gmActionForm
				.getWcNFBInterestOutstandingAmount();

		Map tcPrAmountMap = gmActionForm.getTcPrincipalOutstandingAmount();
		Set tcPrAmountSet = tcPrAmountMap.keySet();
		Iterator tcPrAmountIterator = tcPrAmountSet.iterator();

		Map tcDateMap = gmActionForm.getTcOutstandingAsOnDate();

		Map wcFBDateMap = gmActionForm.getWcFBOutstandingAsOnDate();
		Map wcNFBDateMap = gmActionForm.getWcNFBOutstandingAsOnDate();

		Map wcoIdMap = gmActionForm.getWorkingCapitalId();
		Set wcoIdSet = wcoIdMap.keySet();
		Iterator wcoIdIterator = wcoIdSet.iterator();

		Map tcoIdMap = gmActionForm.getTermCreditId();
		Set tcoIdSet = tcoIdMap.keySet();
		Iterator tcoIdIterator = wcoIdSet.iterator();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String cgpan = null;
		String wcKey = null;
		int count = 0;
		double wcFBPrAmt = 0;
		double wcFBIntAmt = 0;
		Date wcFBDate = null;

		double wcNFBPrAmt = 0;
		double wcNFBIntAmt = 0;
		Date wcNFBDate = null;

		String tcKey = null;
		double tcPrAmt = 0;
		Date tcDate = null;

		String tempWcId = null;
		String tempCgpan = null;

		String tempTcId = null;
		double tempTcPrAmt = 0;
		Date tempTcDate = null;

		double tempWcFBIntAmt = 0;
		double tempWcFBPrAmt = 0;
		Date tempWcFBDate = null;

		double tempWcNFBIntAmt = 0;
		double tempWcNFBPrAmt = 0;
		Date tempWcNFBDate = null;

		while (cgpanWcIterator.hasNext()) {
			cgpan = (String) cgpanWcMap.get(cgpanWcIterator.next());
			Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
					"Inside CgpanIterator cgpan  :" + cgpan);

			wcoIdIterator = wcoIdSet.iterator();
			wcFBPrAmountIterator = wcFBPrAmountSet.iterator();

			count = 0;
			while (wcFBPrAmountIterator.hasNext()) {
				// wcFBPrAmountIterator.next();
				// wcKey = cgpan+"-"+count;
				wcKey = (String) wcFBPrAmountIterator.next();

				Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
						"key ->" + wcKey);
				Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
						"contains key ->" + wcFBPrAmountMap.containsKey(wcKey));

				if (wcKey != null && !wcKey.equals("")
						&& wcFBPrAmountMap.containsKey(wcKey)) {
					Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
							"contains key entering..");
					String wcFBPrAmtVal = (String) wcFBPrAmountMap.get(wcKey);
					String wcNFBPrAmtVal = (String) wcNFBPrAmountMap.get(wcKey);
					if (wcFBPrAmtVal != null && !wcFBPrAmtVal.equals("")) {
						wcFBPrAmt = Double.parseDouble(wcFBPrAmtVal);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Key is matched wcFBPrAmt Amount :" + wcFBPrAmt);
					}
					if (wcNFBPrAmtVal != null && !wcNFBPrAmtVal.equals("")) {
						wcNFBPrAmt = Double.parseDouble(wcNFBPrAmtVal);
					}
					Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
							"wcFBPrAmt value:" + wcFBPrAmt);
					Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
							"wcNFBPrAmt value:" + wcNFBPrAmt);
					/*
					 * if(wcFBPrAmtVal.equals("") &&wcNFBPrAmtVal.equals("") ){
					 * 
					 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails",
					 * "both values null"); continue; }
					 */
					String wcFBIntAmtVal = (String) wcFBIntAmountMap.get(wcKey);
					String wcNFBIntAmtVal = (String) wcNFBIntAmountMap
							.get(wcKey);
					if (wcFBIntAmtVal != null && !wcFBIntAmtVal.equals("")) {
						wcFBIntAmt = Double.parseDouble(wcFBIntAmtVal);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Key is matched wcFBIntAmt Amount :"
										+ wcFBIntAmt);
					}
					if (wcNFBIntAmtVal != null && !wcNFBIntAmtVal.equals("")) {

						wcNFBIntAmt = Double.parseDouble(wcNFBIntAmtVal);
					}
					/*
					 * if(wcFBIntAmtVal.equals("") &&wcNFBIntAmtVal.equals("")
					 * ){
					 * 
					 * continue; }
					 */
					String wcFBDateVal = (String) wcFBDateMap.get(wcKey);
					String wcNFBDateVal = (String) wcNFBDateMap.get(wcKey);
					if (wcFBDateVal != null && !wcFBDateVal.equals("")) {
						wcFBDate = simpleDateFormat.parse(wcFBDateVal,
								new ParsePosition(0));
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Key is matched  wcFB Date :" + wcFBDate);
					}

					if (wcNFBDateVal != null && !wcNFBDateVal.equals("")) {
						wcNFBDate = simpleDateFormat.parse(wcNFBDateVal,
								new ParsePosition(0));
					}
					Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
							"Key is matched  wcFB Date :" + wcNFBDate);
					/*
					 * if(wcFBDateVal==null &&wcNFBDateVal==null){
					 * 
					 * continue; }
					 */
					Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
							"wckey 1->" + wcKey);

					String wcIdKey = null;

					while (wcoIdIterator.hasNext()) {
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails", "wckey 2->" + wcKey);
						wcIdKey = (String) wcoIdIterator.next();
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "WcidKey " + wcIdKey);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "WcidKey " + wcKey);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "WcidKey starts"
										+ wcIdKey.startsWith(wcKey));

						if (wcIdKey != null && !wcIdKey.equals("")
								&& wcIdKey.startsWith(wcKey)) {
							wcIdKey = wcIdKey.substring(
									wcIdKey.lastIndexOf("-") + 1,
									wcIdKey.length());

							Log.log(Log.DEBUG, "GMAction",
									"saveOutstandingDetails ",
									"idKey obtained is " + wcIdKey);

							break;
						}

						wcIdKey = null;
					}
					if (wcIdKey != null) // func for setting updates
					{
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "is not null");
						breakLabel: for (int i = 0; i < tempPeriodicDtls.size(); ++i) {
							PeriodicInfo pr = (PeriodicInfo) tempPeriodicDtls
									.get(i);
							outDtls = pr.getOutstandingDetails();
							for (int j = 0; j < outDtls.size(); ++j) {
								OutstandingDetail outDtl = (OutstandingDetail) outDtls
										.get(j);
								tempOutAmts = outDtl.getOutstandingAmounts();
								Log.log(Log.DEBUG, "GMAction",
										"saveOutstandingDetails",
										"2-for,Temp OutDtl" + "list size :"
												+ tempOutAmts.size());

								for (int k = 0; k < tempOutAmts.size(); ++k) {
									OutstandingAmount tempOutAmt = (OutstandingAmount) tempOutAmts
											.get(k);
									tempWcId = tempOutAmt.getWcoId();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop tempWcId:"
													+ tempWcId);

									tempCgpan = tempOutAmt.getCgpan();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop TempCgpan from tempOutAmt OBJ  : "
													+ tempCgpan);

									tempWcFBPrAmt = tempOutAmt
											.getWcFBPrincipalOutstandingAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp Wc FB Pr Amount from tempOutAmt OBJ : "
													+ tempWcFBPrAmt);

									tempWcFBIntAmt = tempOutAmt
											.getWcFBInterestOutstandingAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp WC FB Int Amount from tempOutAmt OBJ : "
													+ tempWcFBIntAmt);

									tempWcFBDate = tempOutAmt
											.getWcFBOutstandingAsOnDate();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp Date from tempOutAmt OBJ: "
													+ tempWcFBDate);

									tempWcNFBPrAmt = tempOutAmt
											.getWcNFBPrincipalOutstandingAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp Wc FB Pr Amount from tempOutAmt OBJ : "
													+ tempWcNFBPrAmt);

									tempWcNFBIntAmt = tempOutAmt
											.getWcNFBInterestOutstandingAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp WC FB Int Amount from tempOutAmt OBJ : "
													+ tempWcNFBIntAmt);

									tempWcNFBDate = tempOutAmt
											.getWcNFBOutstandingAsOnDate();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp Date from tempOutAmt OBJ: "
													+ tempWcNFBDate);

									if (tempWcId != null
											&& tempWcId.equals(wcIdKey)) {
										Log.log(Log.DEBUG, "GMAction",
												"saveOutstandingDetails",
												"TempOutId == Out Id ");
										Log.log(Log.INFO, "GMAction",
												"saveOutstandingDetails",
												"wcFBPrAmt :" + wcFBPrAmt);
										Log.log(Log.INFO, "GMAction",
												"saveOutstandingDetails",
												"wcNFBPrAmt:" + wcNFBPrAmt);

										if ((tempWcFBPrAmt != 0 && wcFBPrAmt == 0)
												&& (tempWcNFBPrAmt != 0 && wcNFBPrAmt == 0)
												&& ((tempWcFBDate != null && !tempWcFBDate
														.toString().equals("")) && (wcFBDate == null || wcFBDate
														.toString().equals("")))) {
											Log.log(Log.INFO, "GMAction",
													"saveOutstandingDetails",
													"data being deleted");
											throw new MessageException(
													"Existing Data cannot be deleted.It can be only modified");
										}
										if ((tempWcFBPrAmt != 0 && tempWcFBPrAmt != wcFBPrAmt)
												|| (tempWcFBIntAmt != 0 && tempWcFBIntAmt != wcFBIntAmt)
												|| (((tempWcFBDate != null && tempWcFBDate
														.compareTo(wcFBDate) != 0) || tempWcFBDate == null
														&& wcFBDate != null))
												|| (tempWcNFBPrAmt != 0 && tempWcNFBPrAmt != wcNFBPrAmt)
												|| (tempWcNFBIntAmt != 0 && tempWcNFBIntAmt != wcNFBIntAmt)
												|| ((tempWcNFBDate != null && tempWcNFBDate
														.compareTo(wcNFBDate) != 0) || (tempWcNFBDate == null && wcNFBDate != null))) {
											modifiedOutstandingAmount = new OutstandingAmount();
											modifiedOutstandingAmount
													.setWcoId(wcIdKey);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modified id ="
															+ modifiedOutstandingAmount
																	.getWcoId());

											modifiedOutstandingAmount
													.setCgpan(cgpan);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modified cgpan "
															+ modifiedOutstandingAmount
																	.getCgpan());

											modifiedOutstandingAmount
													.setWcFBInterestOutstandingAmount(wcFBIntAmt);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount FB Int = "
															+ modifiedOutstandingAmount
																	.getWcFBInterestOutstandingAmount());

											modifiedOutstandingAmount
													.setWcFBPrincipalOutstandingAmount(wcFBPrAmt);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount FB PR = "
															+ modifiedOutstandingAmount
																	.getWcFBPrincipalOutstandingAmount());

											modifiedOutstandingAmount
													.setWcFBOutstandingAsOnDate(wcFBDate);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount Date ="
															+ modifiedOutstandingAmount
																	.getWcFBOutstandingAsOnDate());

											modifiedOutstandingAmount
													.setWcNFBInterestOutstandingAmount(wcNFBIntAmt);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount FB Int = "
															+ modifiedOutstandingAmount
																	.getWcNFBInterestOutstandingAmount());

											modifiedOutstandingAmount
													.setWcNFBPrincipalOutstandingAmount(wcNFBPrAmt);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount FB PR = "
															+ modifiedOutstandingAmount
																	.getWcNFBPrincipalOutstandingAmount());

											modifiedOutstandingAmount
													.setWcNFBOutstandingAsOnDate(wcNFBDate);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount Date ="
															+ modifiedOutstandingAmount
																	.getWcNFBOutstandingAsOnDate());

											modifiedWcOutstandingAmounts
													.add(modifiedOutstandingAmount);
										}
										break breakLabel;
									}
								}
							}
						}
					} else {
						if ((wcFBIntAmt != 0 && wcFBPrAmt != 0 && (wcFBDate != null && !wcFBDate
								.equals("")))
								|| (wcNFBIntAmt != 0 && wcNFBPrAmt != 0 && (wcNFBDate != null && !wcNFBDate
										.equals("")))) {
							Log.log(Log.DEBUG, "GMAction",
									"saveOutstandingDetails",
									"Inside Else loop seting new OutAmt");
							newOutstandingAmount = new OutstandingAmount();

							newOutstandingAmount.setCgpan(cgpan);
							Log.log(Log.DEBUG, "GMAction",
									"saveOutstandingDetails",
									"NewOutAmount CGpan="
											+ newOutstandingAmount.getCgpan());

							newOutstandingAmount
									.setWcFBInterestOutstandingAmount(wcFBIntAmt);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Amount="
											+ newOutstandingAmount
													.getWcFBInterestOutstandingAmount());

							newOutstandingAmount
									.setWcFBPrincipalOutstandingAmount(wcFBPrAmt);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Date="
											+ newOutstandingAmount
													.getWcFBPrincipalOutstandingAmount());

							newOutstandingAmount
									.setWcFBOutstandingAsOnDate(wcFBDate);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Date="
											+ newOutstandingAmount
													.getWcFBOutstandingAsOnDate());

							newOutstandingAmount
									.setWcNFBInterestOutstandingAmount(wcNFBIntAmt);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Amount="
											+ newOutstandingAmount
													.getWcNFBInterestOutstandingAmount());

							newOutstandingAmount
									.setWcNFBPrincipalOutstandingAmount(wcNFBPrAmt);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Date="
											+ newOutstandingAmount
													.getWcNFBPrincipalOutstandingAmount());

							newOutstandingAmount
									.setWcNFBOutstandingAsOnDate(wcNFBDate);
							Log.log(Log.DEBUG,
									"GMAction",
									"saveOutstandingDetails",
									"NewOutAmount Date="
											+ newOutstandingAmount
													.getWcNFBOutstandingAsOnDate());

							newWcOutstandingAmounts.add(newOutstandingAmount);

						}
					}
				}
				++count;
			}
		}

		User user = getUserInformation(request);
		String userId = user.getUserId();
		String fromId = user.getEmailId();
		String message = "Outstanding Details are Successfully Saved";
		String errorMessage = "";

		while (cgpanTcIterator.hasNext()) {
			cgpan = (String) cgpanTcMap.get(cgpanTcIterator.next());
			Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
					"Inside CgpanTCIterator cgpan  :" + cgpan);

			tcoIdIterator = tcoIdSet.iterator();
			tcPrAmountIterator = tcPrAmountSet.iterator();

			double totalTcAmt = 0;

			count = 0;
			boolean closeFlag = true;

			while (tcPrAmountIterator.hasNext()) {
				tcPrAmountIterator.next();
				tcKey = cgpan + "-" + count;
				Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
						"key ->" + tcKey);

				if (tcKey != null && !tcKey.equals("")
						&& tcPrAmountMap.containsKey(tcKey)) {
					String tcPrAmtVal = (String) tcPrAmountMap.get(tcKey);
					if (!tcPrAmtVal.equals("")) {
						tcPrAmt = Double.parseDouble(tcPrAmtVal);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Key is matched for TC-pr Amt Amount :"
										+ tcPrAmt);

						if ((tcPrAmt == 0) && (closeFlag == true)) {
							closeFlag = false;
							gmProcessor.closure(cgpan,
									GMConstants.CLOSURE_REASON,
									GMConstants.CLOSURE_REASON,
									user.getUserId());

							try {
								String reason = "Tenor Expired";
								gmProcessor.sendMailForClosure(cgpan, userId,
										fromId, reason);
							} catch (MailerException e) {
								errorMessage = " But Sending E-mails for TC application whose Outstanding is zero is failed.";
							}

						}

					} else {
						continue;
					}

					String tcDateVal = (String) tcDateMap.get(tcKey);
					if (!tcDateVal.equals("")) {
						tcDate = simpleDateFormat.parse(tcDateVal,
								new ParsePosition(0));
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Key is matched Tc- Date :" + tcDate);
					} else {
						continue;
					}

					String tcIdKey = null;

					while (tcoIdIterator.hasNext()) {
						tcIdKey = (String) tcoIdIterator.next();
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "TcidKey " + tcIdKey);

						if (tcIdKey != null && !tcIdKey.equals("")
								&& tcIdKey.startsWith(tcKey)) {
							tcIdKey = tcIdKey.substring(
									tcIdKey.lastIndexOf("-") + 1,
									tcIdKey.length());

							Log.log(Log.DEBUG, "GMAction",
									"saveOutstandingDetails ",
									"idKey obtained is " + tcIdKey);

							break;
						}

						tcIdKey = null;
					}
					if (tcIdKey != null) // func for setting updates
					{
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails ", "is not null");
						breakLabel: for (int i = 0; i < tempPeriodicDtls1
								.size(); ++i) {
							PeriodicInfo pr = (PeriodicInfo) tempPeriodicDtls1
									.get(i);
							outDtls1 = pr.getOutstandingDetails();
							for (int j = 0; j < outDtls1.size(); ++j) {
								OutstandingDetail outDtl = (OutstandingDetail) outDtls1
										.get(j);
								tempOutAmts1 = outDtl.getOutstandingAmounts();
								Log.log(Log.DEBUG, "GMAction",
										"saveOutstandingDetails",
										"2-for,Temp OutDtl" + "list size :"
												+ tempOutAmts1.size());

								for (int k = 0; k < tempOutAmts1.size(); ++k) {
									OutstandingAmount tempOutAmt = (OutstandingAmount) tempOutAmts1
											.get(k);
									tempTcId = tempOutAmt.getTcoId();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop tempTcId:"
													+ tempTcId);

									tempCgpan = tempOutAmt.getCgpan();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop TempCgpan from tempOutAmt OBJ  : "
													+ tempCgpan);

									tempTcPrAmt = tempOutAmt
											.getTcPrincipalOutstandingAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp TC PR Amount from tempOutAmt OBJ : "
													+ tempTcPrAmt);

									tempTcDate = tempOutAmt
											.getTcOutstandingAsOnDate();
									Log.log(Log.DEBUG, "GMAction",
											"saveOutstandingDetails",
											"Inside 3 For loop Temp TC Date from tempOutAmt OBJ: "
													+ tempTcDate);

									if (tempTcId != null
											&& tempTcId.equals(tcIdKey)) {
										Log.log(Log.DEBUG, "GMAction",
												"saveOutstandingDetails",
												"TempOutId == Out Id ");
										if ((tempTcPrAmt != tcPrAmt)
												|| (tempTcDate
														.compareTo(tcDate) != 0)) {
											modifiedTcOutstandingAmount = new OutstandingAmount();
											modifiedTcOutstandingAmount
													.setTcoId(tcIdKey);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modified id ="
															+ modifiedTcOutstandingAmount
																	.getTcoId());

											modifiedTcOutstandingAmount
													.setCgpan(cgpan);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modified cgpan "
															+ modifiedTcOutstandingAmount
																	.getCgpan());

											modifiedTcOutstandingAmount
													.setTcPrincipalOutstandingAmount(tcPrAmt);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modified TC OutstandingAmount = "
															+ modifiedTcOutstandingAmount
																	.getTcPrincipalOutstandingAmount());

											modifiedTcOutstandingAmount
													.setTcOutstandingAsOnDate(tcDate);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveOutstandingDetails",
													"modifiedOutstandingAmount Date ="
															+ modifiedTcOutstandingAmount
																	.getTcOutstandingAsOnDate());

											modifiedTcOutstandingAmounts
													.add(modifiedTcOutstandingAmount);
										}
										break breakLabel;
									}
								}
							}
						}
					} else {
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails",
								"Inside Else loop seting new OutAmt");
						newTcOutstandingAmount = new OutstandingAmount();

						newTcOutstandingAmount.setCgpan(cgpan);
						Log.log(Log.DEBUG, "GMAction",
								"saveOutstandingDetails", "NewOutAmount CGpan="
										+ newTcOutstandingAmount.getCgpan());

						newTcOutstandingAmount
								.setTcPrincipalOutstandingAmount(tcPrAmt);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveOutstandingDetails",
								"NewOutAmount Date="
										+ newTcOutstandingAmount
												.getTcPrincipalOutstandingAmount());

						newTcOutstandingAmount.setTcOutstandingAsOnDate(tcDate);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveOutstandingDetails",
								"NewOutAmount Date="
										+ newTcOutstandingAmount
												.getTcOutstandingAsOnDate());

						newTcOutstandingAmounts.add(newTcOutstandingAmount);
					}
				}
				++count;
			}

		}

		// System.out.println("List of Modified WC OutstandingAmount , size = "+modifiedWcOutstandingAmounts.size());
		// System.out.println("List of New WC OutstandingAmount , size = "+newWcOutstandingAmounts.size());

		// System.out.println("List of Modified TC OutstandingAmount , size = "+modifiedTcOutstandingAmounts.size());
		// System.out.println("List of New TC OutstandingAmount , size = "+newTcOutstandingAmounts.size());

		if (modifiedWcOutstandingAmounts.size() > 0) {
			Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
					"modifiedWcOutstandingAmounts Size ="
							+ modifiedWcOutstandingAmounts.size());
			gmProcessor.updateWcOutstanding(modifiedWcOutstandingAmounts);
		}

		if (newWcOutstandingAmounts.size() > 0) {
			Log.log(Log.DEBUG,
					"GMAction",
					"saveOutstandingDetails",
					"newWcOutstandingAmounts Size ="
							+ newWcOutstandingAmounts.size());
			gmProcessor.insertWcOutstanding(newWcOutstandingAmounts);
		}

		if (modifiedTcOutstandingAmounts.size() > 0) {
			Log.log(Log.DEBUG, "GMAction", "saveOutstandingDetails",
					"modifiedTcOutstandingAmounts Size ="
							+ modifiedTcOutstandingAmounts.size());
			gmProcessor.updateTcOutstanding(modifiedTcOutstandingAmounts);
		}

		if (newTcOutstandingAmounts.size() > 0) {
			Log.log(Log.DEBUG,
					"GMAction",
					"saveOutstandingDetails",
					"newTcOutstandingAmounts Size ="
							+ newTcOutstandingAmounts.size());
			gmProcessor.insertTcOutstanding(newTcOutstandingAmounts);
		}

		/*
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails",
		 * "!!!!!!!!Printing modified Out amounts!!!!!!!!!!! ");
		 * 
		 * for (int i=0;i<modifiedWcOutstandingAmounts.size();i++) {
		 * OutstandingAmount tempAmt = (OutstandingAmount)
		 * modifiedWcOutstandingAmounts.get(i);
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","id "+tempAmt.
		 * getWcoId());
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Wc Fb Pr amount "
		 * +tempAmt.getWcFBInterestOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Wc FB Int amount "
		 * +tempAmt.getWcFBPrincipalOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Wc FB date  "+tempAmt
		 * .getWcFBOutstandingAsOnDate()); }
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails",
		 * "!!!!!!!!!!!Printing new  Out amounts !!!!!!!!!!!");
		 * 
		 * for (int i=0;i<newWcOutstandingAmounts.size();i++) {
		 * OutstandingAmount tempAmt = (OutstandingAmount)
		 * newWcOutstandingAmounts.get(i);
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","id "+tempAmt.
		 * getWcoId());
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Wc FB Pr amount "
		 * +tempAmt.getWcFBInterestOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Wc FB Int amount "
		 * +tempAmt.getWcFBPrincipalOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Wc FB date  "+tempAmt
		 * .getWcFBOutstandingAsOnDate()); }
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails",
		 * "*****************Printing Modi TC Out amounts *****************");
		 * 
		 * for (int i=0;i<modifiedTcOutstandingAmounts.size();i++) {
		 * OutstandingAmount tempAmt = (OutstandingAmount)
		 * modifiedTcOutstandingAmounts.get(i);
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","id "+tempAmt.
		 * getTcoId());
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Tc Pr amount "
		 * +tempAmt.getTcPrincipalOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Tc date  "+tempAmt
		 * .getTcOutstandingAsOnDate()); }
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails",
		 * "*****************Printing new  TC Out amounts *****************");
		 * 
		 * for (int i=0;i<newTcOutstandingAmounts.size();i++) {
		 * OutstandingAmount tempAmt = (OutstandingAmount)
		 * newTcOutstandingAmounts.get(i);
		 * 
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","id "+tempAmt.
		 * getTcoId());
		 * Log.log(Log.DEBUG,"GMAction","saveOutstandingDetails","Tc INT amount "
		 * +tempAmt.getTcPrincipalOutstandingAmount());
		 * Log.log(Log.DEBUG,"GMAction"
		 * ,"saveOutstandingDetails","Tc date  "+tempAmt
		 * .getTcOutstandingAsOnDate()); }
		 */
		request.setAttribute("message", message + errorMessage);

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * 
	 * This method gets the borrowerId or the cgpan for the disbursement details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward GetBidCgpanDisbursement(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "GetBidCgpanDisbursement", "Entered");
		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);

		Log.log(Log.INFO, "GMAction", "GetBidCgpanDisbursement", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the disbursement details for the selected borrower or
	 * cgpan
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDisbursementDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showDisbursementDetails", "Entered");

		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);
		session.setAttribute("displayFlag", "1");

		GMProcessor gmProcessor = new GMProcessor();

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		if (cgpan != null && !(cgpan.equals(""))) {
			Application partApp = appProcessor.getPartApplication(memberId,
					cgpan, "");
			/*
			 * if(partApp.getLoanType().equals("WC")) { throw new
			 * MessageException
			 * ("Disbursement Details cannot be entered for Working Capital Loan"
			 * ); }
			 */

		}

		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		gmActionForm.getDisbursementAmount().clear();
		gmActionForm.getDisbursementDate().clear();
		gmActionForm.getFinalDisbursement().clear();
		int type = 0;

		String forward = "";

		// Validating the member Id
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		// System.out.println("borrowerName :" + borrowerName);

		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}

			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Disbursement Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			periodicInfoDetails = gmProcessor.viewDisbursementDetails(
					borrowerId, type);

			if (periodicInfoDetails.isEmpty() || periodicInfoDetails == null
					|| periodicInfoDetails.size() == 0) {
				Log.log(Log.DEBUG, "GMAction", "showDisbursementDetails",
						"No Data");
				throw new NoDataException(
						"There are no Disbursement Details for the combination of inputs");
			}

			gmActionForm.setDisbPeriodicInfoDetails(periodicInfoDetails);

			forward = "success";
		}

		// cgpan
		else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;

			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showDisbursementDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}

			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Disbursement Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			gmActionForm.setBorrowerId(bIdForThisCgpan);

			periodicInfoDetails = gmProcessor.viewDisbursementDetails(
					bIdForThisCgpan, type);

			if (periodicInfoDetails.isEmpty() || periodicInfoDetails == null
					|| periodicInfoDetails.size() == 0) {
				Log.log(Log.DEBUG, "GMAction", "showDisbursementDetails",
						"No Data");
				throw new NoDataException(
						"There are no Disbursement Details for the combination of inputs");
			}

			gmActionForm.setDisbPeriodicInfoDetails(periodicInfoDetails);

			forward = "success";
		}

		// borrower name

		else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * } gmActionForm.setBorrowerId(bIdForBorrowerName);
			 */
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			}
			gmActionForm.setBorrowerIds(bIdForBorrowerName);
			forward = "bidList";
			// periodicInfoDetails =
			// gmProcessor.viewDisbursementDetails(borrowerName,type);
		}
		Log.log(Log.DEBUG, "GMAction", "showDisbursementDetails", "Data "
				+ periodicInfoDetails.size());

		/*
		 * if (periodicInfoDetails.isEmpty()) {
		 * Log.log(Log.DEBUG,"GMAction","showDisbursementDetails","No Data");
		 * throw new NoDataException(
		 * "There are no Disbursement Details for the combination of inputs"); }
		 * 
		 * gmActionForm.setDisbPeriodicInfoDetails(periodicInfoDetails);
		 */
		Log.log(Log.INFO, "GMAction", "showDisbursementDetails", "Exited");

		memberids = null;
		borrowerIds = null;
		return mapping.findForward(forward);

	}

	/**
	 * 
	 * This method saves the disbursement details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveDisbursementDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveDisbursementDetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = gmActionForm.getBorrowerId();
		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails", "borrowerId "
				+ borrowerId);
		String message = "Disbursement Details Saved Successfully";
		ArrayList tempPeriodicDtls = gmProcessor.viewDisbursementDetails(
				borrowerId, 0);

		ArrayList tempDisbAmts = null;

		java.util.Map cgpanMap = gmActionForm.getCgpans();
		java.util.Set cgpanSet = cgpanMap.keySet();
		java.util.Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails ",
				"cgpan Map size = " + cgpanMap.size());
		// System.out.println("cgpan Map size = "+cgpanMap.size());
		String cgpan = null;
		Date disbursementDate = null;
		double disbAmount = 0;
		String finalDisbursement = null;
		int count = 0;
		String key = null;
		String disbId = null;

		String tempCgpan = null;
		String tempDisbId = null;
		double tempDisbAmount = 0;
		Date tempDisbDate = null;
		String tempFinalDisb = null;

		DisbursementAmount disbursementAmount = null;
		DisbursementAmount newDisbursementAmount = null;
		DisbursementAmount modifiedDisbursementAmount = null;

		ArrayList disbursements = null;
		ArrayList newDisbursementAmounts = null;
		ArrayList modifiedDisbursementAmounts = null;

		modifiedDisbursementAmounts = new ArrayList();
		newDisbursementAmounts = new ArrayList();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Map disbAmtMap = gmActionForm.getDisbursementAmount();
		Set disbAmtSet = disbAmtMap.keySet();
		Iterator disbAmtIterator = disbAmtSet.iterator();

		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails",
				"Amount map size = " + disbAmtMap.size());

		Map disbDateMap = gmActionForm.getDisbursementDate();
		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails",
				"Date map size = " + disbDateMap.size());

		Map finalDisbursementMap = gmActionForm.getFinalDisbursement();
		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails",
				"final Disb map size = " + finalDisbursementMap.size());

		Map disbIdMap = gmActionForm.getDisbursementId();
		Set disbIdSet = disbIdMap.keySet();
		Iterator disbIdIterator = disbIdSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails",
				"disbursement Id map size = " + disbIdMap.size());

		while (cgpanIterator.hasNext()) {
			cgpan = (String) cgpanMap.get(cgpanIterator.next());
			// System.out.println("CGPAN:"+cgpan);
			Log.log(Log.DEBUG, "GMAction", "saveDisbursementDetails",
					"cgpanIterator cgpan: " + cgpan);

			disbIdIterator = disbIdSet.iterator();
			disbAmtIterator = disbAmtSet.iterator();

			count = 0;

			/*
			 * if(disbAmtMap.size() == 1) {
			 * Log.log(Log.INFO,"GMAction","saveDisbursementDetails"
			 * ,"@@@@@@@@@@@@@@@"); count =1; }
			 */
			while (disbAmtIterator.hasNext()) {
				disbAmtIterator.next();

				key = cgpan + "-" + count;
				Log.log(Log.DEBUG, "GM Action", "saveDisbursementDetails",
						"Amount Iterator key : " + key);

				if (key != null && !key.equals("")
						&& disbAmtMap.containsKey(key)) {
					String disbValue = (String) disbAmtMap.get(key);
					if (!disbValue.equals("")) {
						disbAmount = Double.parseDouble(disbValue);
						// System.out.println("disbAmount:"+disbAmount);
						Log.log(Log.DEBUG, "GM Action",
								"saveDisbursementDetails",
								"Key is matched Amount :" + disbAmount);
					} else {
						break;
					}
					String dateValue = (String) disbDateMap.get(key);
					if (!dateValue.equals("")) {
						disbursementDate = simpleDateFormat.parse(dateValue,
								new ParsePosition(0));
						// System.out.println("disbursementDate:"+disbursementDate);
						Log.log(Log.DEBUG, "GM Action",
								"saveDisbursementDetails",
								"Key is matched Date :" + disbursementDate);
					} else {
						break;
					}

					finalDisbursement = (String) finalDisbursementMap.get(key);
					if (request.getParameter("finalDisbursement(" + key + ")") == null) {
						Log.log(Log.DEBUG, "GM Action",
								"saveDisbursementDetails",
								"*****finalDisbursement = N******");
						finalDisbursement = "N";
					}

					String idKey = null;

					while (disbIdIterator.hasNext()) {
						idKey = (String) disbIdIterator.next();
						Log.log(Log.DEBUG, "GMAction",
								"saveDisbursementDetails", "idKey " + idKey);

						if (idKey != null && !idKey.equals("")
								&& idKey.startsWith(key)) {
							idKey = idKey.substring(idKey.lastIndexOf("-") + 1,
									idKey.length());

							Log.log(Log.DEBUG, "GMAction",
									"saveDisbursementDetails",
									"idKey obtained is " + idKey);

							break;
						}

						idKey = null;
					}

					if (idKey != null) // func for setting updates
					{
						Log.log(Log.DEBUG, "GMAction",
								"saveDisbursementDetails", "idkey is not null");
						breakLabel: for (int i = 0; i < tempPeriodicDtls.size(); ++i) {
							PeriodicInfo pr = (PeriodicInfo) tempPeriodicDtls
									.get(i);
							disbursements = pr.getDisbursementDetails();
							for (int j = 0; j < disbursements.size(); ++j) {
								Disbursement disbursement = (Disbursement) disbursements
										.get(j);
								tempDisbAmts = disbursement
										.getDisbursementAmounts();
								Log.log(Log.DEBUG,
										"GMAction",
										"saveDisbursementDetails",
										"2-for,Temp Disbursement"
												+ "list size :"
												+ tempDisbAmts.size());

								for (int k = 0; k < tempDisbAmts.size(); ++k) {
									DisbursementAmount tempDisbAmt = (DisbursementAmount) tempDisbAmts
											.get(k);
									tempDisbId = tempDisbAmt
											.getDisbursementId();
									Log.log(Log.DEBUG, "GMAction",
											"saveDisbursementDetails",
											"Inside 3 For loop tempDisbId:"
													+ tempDisbId);

									tempCgpan = tempDisbAmt.getCgpan();
									Log.log(Log.DEBUG, "GMAction",
											"saveDisbursementDetails",
											"Inside 3 For loop TempCgpan from tempDisbAmt OBJ  : "
													+ tempCgpan);

									tempDisbAmount = tempDisbAmt
											.getDisbursementAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveDisbursementDetails",
											"Inside 3 For loop Temp Amount from tempDisbAmt OBJ : "
													+ tempDisbAmount);

									tempDisbDate = tempDisbAmt
											.getDisbursementDate();
									Log.log(Log.DEBUG, "GMAction",
											"saveDisbursementDetails",
											"Inside 3 For loop Temp Date from tempDisbAmt OBJ: "
													+ tempDisbDate);

									tempFinalDisb = tempDisbAmt
											.getFinalDisbursement();
									Log.log(Log.DEBUG, "GMAction",
											"saveDisbursementDetails",
											"Inside 3 For loop Temp Final Disbursement from tempDisbAmt OBJ: "
													+ tempFinalDisb);

									if (tempDisbId != null
											&& tempDisbId.equals(idKey)) {
										Log.log(Log.DEBUG, "GMAction",
												"saveDisbursementDetails",
												"TempDisbursementId == Disb Id ");
										if ((tempDisbAmount != disbAmount)
												|| (tempDisbDate
														.compareTo(disbursementDate) != 0)) {
											modifiedDisbursementAmount = new DisbursementAmount();
											modifiedDisbursementAmount
													.setDisbursementId(idKey);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveDisbursementDetails",
													"modified id ="
															+ modifiedDisbursementAmount
																	.getDisbursementId());

											modifiedDisbursementAmount
													.setCgpan(cgpan);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveDisbursementDetails",
													"modified cgpan "
															+ modifiedDisbursementAmount
																	.getCgpan());

											modifiedDisbursementAmount
													.setDisbursementAmount(disbAmount);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveDisbursementDetails",
													"modified Disbursement Amount = "
															+ modifiedDisbursementAmount
																	.getDisbursementAmount());

											modifiedDisbursementAmount
													.setDisbursementDate(disbursementDate);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveDisbursementDetails",
													"modified Disbursement Date ="
															+ modifiedDisbursementAmount
																	.getDisbursementDate());

											modifiedDisbursementAmount
													.setFinalDisbursement(finalDisbursement);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveDisbursementDetails",
													"modified Disbursement Date ="
															+ modifiedDisbursementAmount
																	.getFinalDisbursement());

											modifiedDisbursementAmounts
													.add(modifiedDisbursementAmount);
										}
										Log.log(Log.DEBUG, "GMAction",
												"saveDisbursementDetails",
												"####Break Label Called#####");
										break breakLabel;
									}
								}
							}
						}
						Log.log(Log.DEBUG, "GMAction",
								"saveDisbursementDetails",
								" End of if(idKey!=null) //func for setting updates");
					} else {
						Log.log(Log.DEBUG, "GMAction",
								"saveDisbursementDetails",
								"Inside Else loop seting new Disbursement");
						newDisbursementAmount = new DisbursementAmount();

						newDisbursementAmount.setCgpan(cgpan);
						Log.log(Log.DEBUG, "GMAction",
								"saveDisbursementDetails",
								"NewDisbursementAmount Cgpan="
										+ newDisbursementAmount.getCgpan());

						newDisbursementAmount.setDisbursementAmount(disbAmount);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveDisbursementDetails",
								"NewDisbursementAmount Amount="
										+ newDisbursementAmount
												.getDisbursementAmount());

						newDisbursementAmount
								.setDisbursementDate(disbursementDate);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveDisbursementDetails",
								"NewDisbursementAmount Date="
										+ newDisbursementAmount
												.getDisbursementDate());

						newDisbursementAmount
								.setFinalDisbursement(finalDisbursement);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveDisbursementDetails",
								"NewDisbursementAmount Final Disb ="
										+ newDisbursementAmount
												.getFinalDisbursement());

						newDisbursementAmounts.add(newDisbursementAmount);
					}
					Log.log(Log.INFO, "GMAction", "saveDisbursementDetails",
							"End of if(key !=null && !key.equals() && disbAmtMap.containsKey(key)");
				}
				++count;
				Log.log(Log.INFO, "GMAction", "saveDisbursementDetails",
						"End of one While Disb Amt iterator");
			}
		}
		User user = getUserInformation(request);

		// System.out.println("List of Modified Disbursement Amount , size = "+modifiedDisbursementAmounts.size());
		// System.out.println("List of New DisbursementAmount , size = "+newDisbursementAmounts.size());

		if (modifiedDisbursementAmounts.size() > 0) {
			gmProcessor.updateDisbursement(modifiedDisbursementAmounts,
					user.getUserId());
		}

		if (newDisbursementAmounts.size() > 0) {
			gmProcessor.insertDisbursement(newDisbursementAmounts,
					user.getUserId());
		}

		request.setAttribute("message", message);
		Log.log(Log.INFO, "GMAction", "saveDisbursementDetails", "Exited");

		/*
		 * modifiedDisbursementAmounts.clear(); modifiedDisbursementAmounts =
		 * null; newDisbursementAmounts.clear(); newDisbursementAmounts = null;
		 */
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward addMoreDisbursement(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "addMoreDisbursement", "Entered");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		Map cgpanMap = gmActionForm.getCgpans();
		Set cgpanSet = cgpanMap.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
				"Cgpan Map size :" + cgpanMap.size());

		Map idMap = gmActionForm.getDisbursementId();
		Set idSet = idMap.keySet();
		Iterator idIterator = idSet.iterator();

		Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement", "Id Map size :"
				+ idMap.size());

		Date disbursementDate = null;
		double disburseAmount = 0;
		String key = null;
		int count = 0;
		String cgpan = null;

		DisbursementAmount disbursementAmount = null;
		ArrayList disbursementAmounts = null;
		disbursementAmounts = new ArrayList();
		HashMap disbursementDtls = new HashMap();

		while (cgpanIterator.hasNext()) {
			idIterator = idSet.iterator();

			cgpan = (String) cgpanMap.get(cgpanIterator.next());
			Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
					"Inside cgpan Iterator Cgpan : " + cgpan);

			Map disbAmountMap = gmActionForm.getDisbursementAmount();
			Set disbAmountSet = disbAmountMap.keySet();
			Iterator disbAmountIterator = disbAmountSet.iterator();

			Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
					"Inside cgpan Iterator disbAmountMap size: "
							+ disbAmountMap.size());

			Map disbDateMap = gmActionForm.getDisbursementDate();
			Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
					"Inside cgpan Iterator disbDateMap: " + disbDateMap.size());

			Map finalDisbursementMap = gmActionForm.getFinalDisbursement();
			Set finalDisbursementSet = finalDisbursementMap.keySet();
			Iterator finalDisbursementIterator = finalDisbursementSet
					.iterator();
			Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
					"Inside cgpan Iterator finalDisbursementMap : "
							+ finalDisbursementMap.size());

			count = 0;

			if (disbAmountMap.size() == 1) {
				count = 1;
			}

			count = 0;

			ArrayList temp = new ArrayList();
			String amount = null;
			String date = null;
			String finalDisb = null;
			while (disbAmountIterator.hasNext()) {
				key = cgpan + "-" + count;
				disbAmountIterator.next();

				if (disbAmountMap.containsKey(key)) {
					Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
							"Inside if loop Amount Map contains key");

					amount = (String) disbAmountMap.get(key);
					Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
							"amount " + amount + "and  Key " + key);

					if (amount == null || amount.equals("")) {
						continue;
					}

					disburseAmount = Double.parseDouble(amount);

					date = (String) disbDateMap.get(key);
					Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
							"date " + date + "and  Key " + key);
					if (date == null || date.equals("")) {
						continue;
					}
					disbursementDate = simpleDateFormat.parse(date,
							new ParsePosition(0));

					String idKey = null;
					while (idIterator.hasNext()) {
						idKey = (String) idIterator.next();

						Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
								"DbId Key :" + idKey);

						if (idKey != null && !idKey.equals("")
								&& idKey.startsWith(key)) {
							idKey = idKey.substring(idKey.lastIndexOf("-") + 1,
									idKey.length());

							Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
									"DbId Key obtained is :" + idKey);

							break;
						}

						idKey = null;
					}

					disbursementAmount = new DisbursementAmount();
					disbursementAmount.setCgpan(cgpan);
					disbursementAmount.setDisbursementAmount(disburseAmount);
					disbursementAmount.setDisbursementDate(disbursementDate);

					disbursementAmounts.add(disbursementAmount);
					temp.add(disbursementAmount);
					Log.log(Log.DEBUG, "GMAction", "addMoreDisbursement",
							"END of IF loop -Amount Map contains key");
				}
				++count;
			}
			disbursementDtls.put(cgpan, temp);
		}
		ArrayList periodicInfoDetails = null;

		periodicInfoDetails = gmActionForm.getDisbPeriodicInfoDetails();

		for (int i = 0; i < periodicInfoDetails.size(); ++i) {
			PeriodicInfo pr = (PeriodicInfo) periodicInfoDetails.get(i);
			ArrayList disbursements = pr.getDisbursementDetails();
			for (int j = 0; j < disbursements.size(); ++j) {
				Disbursement disbursement = (Disbursement) disbursements.get(j);
				String cgpan1 = disbursement.getCgpan();
				ArrayList temp = (ArrayList) disbursementDtls.get(cgpan1);
				disbursement.setDisbursementAmounts(temp);
			}
		}

		Log.log(Log.INFO, "GMAction", "addMoreDisbursement", "Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	// Repayment Details

	/**
	 * 
	 * This method gets the borrowerId or the cgpan for repayment details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward GetBidCgpanRepayment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "GetBidCgpanRepayment", "Entered");
		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.setBorrowerId("");
		gmActionForm.setCgpan("");
		gmActionForm.setBorrowerName("");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setPeriodicBankId(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberId(memberId);

		Log.log(Log.INFO, "GMAction", "GetBidCgpanRepayment", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the repayment details for he selected borrower
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRepaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentDetails", "Entered");
		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();
		gmActionForm.getRepaymentAmount().clear();
		gmActionForm.getRepaymentDate().clear();
		// gmActionForm.getRepaymentId().clear();
		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		int type = 0;

		String forward = "";
		// Validating the member Id
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}
			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Repayment Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			periodicInfoDetails = gmProcessor.viewRepaymentDetails(borrowerId,
					type);

			if (periodicInfoDetails == null) {
				throw new NoDataException(
						"There are no Repayment Details for this "
								+ borrowerName);
			}

			// Setting the object
			gmActionForm.setRepayPeriodicInfoDetails(periodicInfoDetails);
			gmActionForm.setRepayPeriodicInfoDetailsTemp(periodicInfoDetails);

			forward = "success";
		}

		// cgpan
		else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showRepaymentDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}

			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Repayment Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			gmActionForm.setBorrowerId(bIdForThisCgpan);

			periodicInfoDetails = gmProcessor.viewRepaymentDetails(
					bIdForThisCgpan, type);
			if (periodicInfoDetails == null) {
				throw new NoDataException(
						"There are no Repayment Details for this "
								+ borrowerName);
			}

			// Setting the object
			gmActionForm.setRepayPeriodicInfoDetails(periodicInfoDetails);
			gmActionForm.setRepayPeriodicInfoDetailsTemp(periodicInfoDetails);

			forward = "success";

		}
		// borrower name
		else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * } gmActionForm.setBorrowerId(bIdForBorrowerName);
			 * 
			 * periodicInfoDetails =
			 * gmProcessor.viewRepaymentDetails(borrowerName,type);
			 */
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			}
			session.setAttribute("displayFlag", "3");
			gmActionForm.setBorrowerIds(bIdForBorrowerName);
			forward = "bidList";
		}

		for (int i = 0; i < periodicInfoDetails.size(); ++i) {
			PeriodicInfo pr = (PeriodicInfo) periodicInfoDetails.get(i);
			ArrayList repayments = pr.getRepaymentDetails();

			Log.log(Log.DEBUG, "GMAction", "showRepaymentDetails",
					" size of repaymentz" + repayments.size());

			for (int j = 0; j < repayments.size(); ++j) {
				Repayment repayment = (Repayment) repayments.get(j);
				ArrayList repaymentAmts = repayment.getRepaymentAmounts();
				Log.log(Log.DEBUG, "GMAction", "showRepaymentDetails",
						"cgpan, " + "size of amts" + repayment.getCgpan() + ","
								+ repaymentAmts.size());

				for (int k = 0; k < repaymentAmts.size(); ++k) {
					RepaymentAmount rpamt = (RepaymentAmount) repaymentAmts
							.get(k);

					/*
					 * Log.log(Log.DEBUG,"GMAction","showRepaymentDetails",
					 * "id,amount,date at "+ k+","
					 * +rpamt.getRepaymentAmount()+","+rpamt.getRepaymentDate()+
					 * ","+rpamt.getRepayId());
					 */
				}
			}
		}

		Log.log(Log.INFO, "GMAction", "showRepaymentDetails", "Exited");
		return mapping.findForward(forward);

	}

	/**
	 * 
	 * This method saves the repayment details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveRepaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveRepaymentDetails", "Entered");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = gmActionForm.getBorrowerId();
		String message = "Repayment Details Saved Successfully";
		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "borrowerId "
				+ borrowerId);

		ArrayList tempPeriodicDtls = gmProcessor.viewRepaymentDetails(
				borrowerId, 0);

		// getting the arraylist object.
		// ArrayList tempPeriodicDtls =
		// gmActionForm.getRepayPeriodicInfoDetailsTemp();

		ArrayList tempRepAmts = null;

		Map cgpanMap = gmActionForm.getCgpans();
		Set cgpanSet = cgpanMap.keySet();
		Iterator cgpanIterator = cgpanSet.iterator();
		Log.log(Log.DEBUG, "GM Action", " SaveRepaymentDetail ",
				"cgpan Map size = " + cgpanMap.size());

		Date repaymentDate = null;
		double repayAmount = 0;
		String key = null;
		int count = 0;
		String cgpan = null;
		String tempCgpan = null;

		String repayId = null;
		String tempRepayId = null;
		String repayAmtKey = null;

		double tempAmount = 0;
		Date tempDate = null;

		int flag = 0;

		RepaymentAmount repaymentAmount = null;
		RepaymentAmount modifiedRepaymentAmount = null;
		RepaymentAmount newRepaymentAmount = null;

		ArrayList newRepaymentAmounts = null;
		ArrayList repayments = null;
		ArrayList repaymentAmounts = null;
		ArrayList modifiedRepaymentAmounts = null;

		repaymentAmounts = new ArrayList();
		modifiedRepaymentAmounts = new ArrayList();
		newRepaymentAmounts = new ArrayList();

		Map repayAmountMap = gmActionForm.getRepaymentAmount();
		Set repayAmountSet = repayAmountMap.keySet();
		Iterator repayAmountIterator = repayAmountSet.iterator();

		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
				"Amount map size = " + repayAmountMap.size());

		Map repayDateMap = gmActionForm.getRepaymentDate();
		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
				"Date map size = " + repayDateMap.size());

		Map repayIdMap = gmActionForm.getRepaymentId();
		Set repayIdSet = repayIdMap.keySet();
		Iterator repayIdIterator = repayIdSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
				"repay ID map size = " + repayIdMap.size());

		while (cgpanIterator.hasNext()) {
			cgpan = (String) cgpanMap.get(cgpanIterator.next());
			Log.log(Log.DEBUG, "GMAction", "Inside CgpanIterator", "cgpan  :"
					+ cgpan);

			repayIdIterator = repayIdSet.iterator();
			repayAmountIterator = repayAmountSet.iterator();

			count = 0;
			/*
			 * if(repayDateMap.size() == 1) { count =1; }
			 */
			while (repayAmountIterator.hasNext()) {
				repayAmtKey = (String) repayAmountIterator.next();

				key = cgpan + "-" + count;
				Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "key ->"
						+ key);

				if (key != null && !key.equals("")
						&& repayAmountMap.containsKey(key)) {
					String repamt = (String) repayAmountMap.get(key);
					if (!repamt.equals("")) {
						repayAmount = Double.parseDouble(repamt);
						Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
								"Key is matched Amount :" + repayAmount);
					} else {
						break;
					}
					String repdate = (String) repayDateMap.get(key);

					if (!repdate.equals("")) {
						repaymentDate = simpleDateFormat.parse(repdate,
								new ParsePosition(0));
						Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
								"Key is matched  Date :" + repaymentDate);

					} else {
						break;
					}

					String idKey = null;

					while (repayIdIterator.hasNext()) {
						idKey = (String) repayIdIterator.next();
						Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails ",
								"idKey " + idKey);

						if (idKey != null && !idKey.equals("")
								&& idKey.startsWith(key)) {
							idKey = idKey.substring(idKey.lastIndexOf("-") + 1,
									idKey.length());

							Log.log(Log.DEBUG, "GMAction",
									"saveRepaymentDetails ",
									"idKey obtained is " + idKey);

							break;
						}

						idKey = null;
					}

					if (idKey != null) // func for setting updates
					{
						Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails ",
								"is not null");
						breakLabel: for (int i = 0; i < tempPeriodicDtls.size(); ++i) {
							PeriodicInfo pr = (PeriodicInfo) tempPeriodicDtls
									.get(i);
							repayments = pr.getRepaymentDetails();
							for (int j = 0; j < repayments.size(); ++j) {
								Repayment repayment = (Repayment) repayments
										.get(j);
								tempRepAmts = repayment.getRepaymentAmounts();
								Log.log(Log.DEBUG, "GMAction",
										"saveRepaymentDetails",
										"2-for,Temp Repayment" + "list size :"
												+ tempRepAmts.size());

								for (int k = 0; k < tempRepAmts.size(); ++k) {
									RepaymentAmount tempRpAmt = (RepaymentAmount) tempRepAmts
											.get(k);
									tempRepayId = tempRpAmt.getRepayId();
									Log.log(Log.DEBUG, "GMAction",
											"saveRepaymentDetails",
											"Inside 3 For loop tempRepayId:"
													+ tempRepayId);
									Log.log(Log.DEBUG, "GMAction",
											"saveRepaymentDetails",
											"Inside 3 For loop repayId:"
													+ repayId);

									tempCgpan = tempRpAmt.getCgpan();
									Log.log(Log.DEBUG, "GMAction",
											"saveRepaymentDetails",
											"Inside 3 For loop TempCgpan from tempRepayAmt OBJ  : "
													+ tempCgpan);

									tempAmount = tempRpAmt.getRepaymentAmount();
									Log.log(Log.DEBUG, "GMAction",
											"saveRepaymentDetails",
											"Inside 3 For loop Temp Amount from tempRepayAmt OBJ : "
													+ tempAmount);

									tempDate = tempRpAmt.getRepaymentDate();
									Log.log(Log.DEBUG, "GMAction",
											"saveRepaymentDetails",
											"Inside 3 For loop Temp Date from tempRepayAmt OBJ: "
													+ tempDate);

									if (tempRepayId != null
											&& tempRepayId.equals(idKey)) {
										Log.log(Log.DEBUG, "GMAction",
												"saveRepaymentDetails",
												"TempRepaymentId == Repay Id ");
										if ((tempAmount != repayAmount)
												|| (tempDate
														.compareTo(repaymentDate) != 0)) {
											modifiedRepaymentAmount = new RepaymentAmount();
											modifiedRepaymentAmount
													.setRepayId(idKey);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveRepaymentDetails",
													"modified id ="
															+ modifiedRepaymentAmount
																	.getRepayId());

											modifiedRepaymentAmount
													.setCgpan(cgpan);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveRepaymentDetails",
													"modified cgpan "
															+ modifiedRepaymentAmount
																	.getCgpan());

											modifiedRepaymentAmount
													.setRepaymentAmount(repayAmount);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveRepaymentDetails",
													"modified RepaymentAmount = "
															+ modifiedRepaymentAmount
																	.getRepaymentAmount());

											modifiedRepaymentAmount
													.setRepaymentDate(repaymentDate);
											Log.log(Log.DEBUG,
													"GMAction",
													"saveRepaymentDetails",
													"modified RepaymentDate ="
															+ modifiedRepaymentAmount
																	.getRepaymentDate());

											modifiedRepaymentAmounts
													.add(modifiedRepaymentAmount);
										}
										break breakLabel;
									}
								}
							}
						}
					} else {
						Log.log(Log.DEBUG, "GMAction", "SaveRepaymentDetails",
								"Inside Else loop seting new Repayment");
						newRepaymentAmount = new RepaymentAmount();

						newRepaymentAmount.setCgpan(cgpan);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveRepaymentDetails",
								"NewRepayAmount CGpan="
										+ newRepaymentAmount.getCgpan());

						newRepaymentAmount.setRepaymentAmount(repayAmount);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveRepaymentDetails",
								"NewRepayAmount Amount="
										+ newRepaymentAmount
												.getRepaymentAmount());

						newRepaymentAmount.setRepaymentDate(repaymentDate);
						Log.log(Log.DEBUG,
								"GMAction",
								"saveRepaymentDetails",
								"NewRepayAmount Date="
										+ newRepaymentAmount.getRepaymentDate());

						newRepaymentAmounts.add(newRepaymentAmount);
					}
				}
				++count;
			}
		}
		User user = getUserInformation(request);

		// System.out.println("List of ModifiedRepaymentAmount , size = "+modifiedRepaymentAmounts.size());
		// System.out.println("List of NewRepaymentAmount , size = "+newRepaymentAmounts.size());

		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
				"Printing modified repayment amounts ");

		for (int i = 0; i < modifiedRepaymentAmounts.size(); i++) {
			RepaymentAmount tempAmt = (RepaymentAmount) modifiedRepaymentAmounts
					.get(i);

			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "id "
					+ tempAmt.getRepayId());
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "amount "
					+ tempAmt.getRepaymentAmount());
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "date  "
					+ tempAmt.getRepaymentDate());
		}

		Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails",
				"Printing new  repayment amounts ");

		for (int i = 0; i < newRepaymentAmounts.size(); i++) {
			RepaymentAmount tempAmt = (RepaymentAmount) newRepaymentAmounts
					.get(i);

			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "id "
					+ tempAmt.getRepayId());
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "amount "
					+ tempAmt.getRepaymentAmount());
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentDetails", "date  "
					+ tempAmt.getRepaymentDate());
		}

		if (modifiedRepaymentAmounts.size() != GMConstants.TYPE_ZERO) {
			gmProcessor.updateRepaymentDetails(modifiedRepaymentAmounts,
					user.getUserId());
		}
		if (newRepaymentAmounts.size() != GMConstants.TYPE_ZERO) {
			gmProcessor.insertRepaymentDetails(newRepaymentAmounts,
					user.getUserId());
		}

		request.setAttribute("message", message);
		Log.log(Log.INFO, "GMAction", "saveRepaymentDetails", "Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * This method gets an additional row for a cgpan in repayment details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward addMoreRepayment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "GMAction", "addMoreRepayment", "Entered");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		java.util.Map cgpanMap = gmActionForm.getCgpans();
		java.util.Set cgpanSet = cgpanMap.keySet();
		java.util.Iterator cgpanIterator = cgpanSet.iterator();

		Log.log(Log.DEBUG, "GMAction", "addMoreRepayment", "Cgpan Map size :"
				+ cgpanMap.size());

		Date repaymentDate = null;
		double repayAmount = 0;
		String key = null;
		int count = 0;
		String cgpan = null;

		RepaymentAmount repaymentAmount = null;
		ArrayList repaymentAmounts = null;
		repaymentAmounts = new ArrayList();
		HashMap repaymentDtls = new HashMap();

		Map idMap = gmActionForm.getRepaymentId();
		Set idSet = idMap.keySet();
		Iterator idIterator = idSet.iterator();

		Log.log(Log.DEBUG, "GMAction", "addMoreRepayment", "Id Map size :"
				+ idMap.size());

		while (idIterator.hasNext()) {
			Log.log(Log.DEBUG, "GMAction", "addMoreRepayment", "Element :"
					+ idIterator.next());
		}

		while (cgpanIterator.hasNext()) {
			idIterator = idSet.iterator();

			cgpan = (String) cgpanMap.get(cgpanIterator.next());
			Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
					"Inside cgpan Iterator -Cgpan : " + cgpan);

			Map repayAmountMap = gmActionForm.getRepaymentAmount();
			Set repayAmountSet = repayAmountMap.keySet();
			Iterator repayAmountIterator = repayAmountSet.iterator();

			Log.log(Log.DEBUG,
					"GMAction",
					"addMoreRepayment",
					"Inside cgpan Iterator Amount Map size : "
							+ repayAmountMap.size());

			Map repayDateMap = gmActionForm.getRepaymentDate();
			// Set repayDateSet = repayDateMap .keySet();
			// Iterator repayDateIterator = repayDateSet.iterator() ;

			count = 0;
			ArrayList temp = new ArrayList();
			String amount = null;
			String date = null;
			while (repayAmountIterator.hasNext()) {
				key = cgpan + "-" + count;
				Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
						"Inside Amount Iterator, key : " + key);
				repayAmountIterator.next();
				// repayDateIterator.next();

				if (repayAmountMap.containsKey(key)) {
					Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
							"Inside if loop Amount Map contains key");
					amount = (String) repayAmountMap.get(key);
					Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
							"Amount " + amount + "and  Key " + key);
					if (amount == null || amount.equals("")) {
						continue;
					}

					repayAmount = Double.parseDouble(amount);

					date = (String) repayDateMap.get(key);
					Log.log(Log.DEBUG, "GMAction", "addMoreRepayment", "Date "
							+ date + "and  Key " + key);
					if (date == null || date.equals("")) {
						continue;
					}
					repaymentDate = simpleDateFormat.parse(date,
							new ParsePosition(0));
					Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
							"Date  for the key :" + repaymentDate);

					String idKey = null;
					while (idIterator.hasNext()) {
						idKey = (String) idIterator.next();

						Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
								"RpId Key :" + idKey);

						if (idKey != null && !idKey.equals("")
								&& idKey.startsWith(key)) {
							idKey = idKey.substring(idKey.lastIndexOf("-") + 1,
									idKey.length());

							Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
									"RpId Key obtained is :" + idKey);

							break;
						}

						idKey = null;
					}

					repaymentAmount = new RepaymentAmount();
					repaymentAmount.setRepayId(idKey);
					repaymentAmount.setCgpan(cgpan);
					repaymentAmount.setRepaymentAmount(repayAmount);
					repaymentAmount.setRepaymentDate(repaymentDate);

					repaymentAmounts.add(repaymentAmount);
					temp.add(repaymentAmount);
					Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
							"END of IF loop -Amount Map contains key");
				}
				++count;
			}
			repaymentDtls.put(cgpan, temp);
		}
		ArrayList periodicInfoDetails = null;

		periodicInfoDetails = gmActionForm.getRepayPeriodicInfoDetails();

		for (int i = 0; i < periodicInfoDetails.size(); ++i) {
			PeriodicInfo pr = (PeriodicInfo) periodicInfoDetails.get(i);
			ArrayList repayments = pr.getRepaymentDetails();
			for (int j = 0; j < repayments.size(); ++j) {
				Repayment repayment = (Repayment) repayments.get(j);
				String cgpan1 = repayment.getCgpan();
				ArrayList temp = (ArrayList) repaymentDtls.get(cgpan1);
				repayment.setRepaymentAmounts(temp);
			}
		}

		Log.log(Log.DEBUG, "GMAction", "addMoreRepayment",
				" printing the temp periodic info details after add more...");
		/*
		 * for(int i=0; i <
		 * gmActionForm.getRepayPeriodicInfoDetailsTemp().size();++i) {
		 * PeriodicInfo pr =(PeriodicInfo)periodicInfoDetails.get(i); ArrayList
		 * repayments = pr.getRepaymentDetails();
		 * 
		 * Log.log(Log.DEBUG,"GMAction","addMoreRepayment"," size of repaymentz"
		 * +repayments.size());
		 * 
		 * for(int j=0; j<repayments.size(); ++j) { Repayment repayment =
		 * (Repayment)repayments.get(j); ArrayList repaymentAmts =
		 * repayment.getRepaymentAmounts();
		 * 
		 * if(repaymentAmts==null) { continue; }
		 * 
		 * Log.log(Log.DEBUG,"GMAction","addMoreRepayment","cgpan, " +
		 * "size of amts" +repayment.getCgpan()+"," + repaymentAmts.size());
		 * 
		 * for(int k=0; k < repaymentAmts.size(); ++k) { RepaymentAmount rpamt =
		 * (RepaymentAmount)repaymentAmts.get(k);
		 * 
		 * Log.log(Log.DEBUG,"GMAction","addMoreRepayment","id,amount,date at "+
		 * k+"," +rpamt.getRepaymentAmount()+","+rpamt.getRepaymentDate()+
		 * ","+rpamt.getRepayId()); } } }
		 */
		Log.log(Log.INFO, "GMAction", "addMoreRepayment", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	// dd

	/**
	 * 
	 * This method gets the borrowerId or cgpan for the repayment Schedule
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward GetBidOrCgpanForSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "GetBidOrCgpanForSchedule", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		gmActionForm.setBorrowerIdForSchedule("");
		gmActionForm.setCgpanForSchedule("");
		gmActionForm.setBorrowerNameForSchedule("");

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();

		String memberId = (bankId.concat(zoneId)).concat(branchId);

		gmActionForm.setBankIdForSchedule(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		gmActionForm.setMemberIdForSchedule(memberId);

		Log.log(Log.INFO, "GMAction", "GetBidOrCgpanForSchedule", "Exited");

		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the repayment schedule details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentSchedule", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);

		String memberId = gmActionForm.getMemberIdForSchedule();
		session.setAttribute("scheduleMemberId", memberId);
		Log.log(Log.DEBUG, "GMAction", "showRepaymentSchedule", "mem id -->"
				+ memberId);

		String cgpan = (gmActionForm.getCgpanForSchedule()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerIdForSchedule())
				.toUpperCase();
		String borrowerName = gmActionForm.getBorrowerNameForSchedule();

		Log.log(Log.DEBUG, "GMAction", "", "bid " + borrowerId);
		Log.log(Log.DEBUG, "GMAction", "", "bid " + cgpan);
		ArrayList repaymentSchedules = new ArrayList();
		GMProcessor gmProcessor = new GMProcessor();
		RepaymentSchedule repaymentSchedule = new RepaymentSchedule();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		// Validating the member Id
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not" + " exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);
		int type = 0;

		String forward = "";

		if (!borrowerId.equals("") && (cgpan.equals(""))) {
			type = 0;
			if (!borrowerIds.contains(borrowerId)) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}
			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Repayment Schedule Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			repaymentSchedules = gmProcessor.viewRepaymentSchedule(borrowerId,
					type);

			gmActionForm.setRepaymentSchedules(repaymentSchedules);

			Log.log(Log.INFO, "GMAction", "showRepaymentSchedule",
					"repqyment schedule size :" + repaymentSchedules.size());
			if (repaymentSchedules == null || repaymentSchedules.size() == 0) {
				request.setAttribute("message",
						"No Repayment Schedule Details Available");
				forward = Constants.SUCCESS;
			} else {

				forward = "forwardPage";
			}

		}

		else if ((cgpan != null && !(cgpan.equals("")))
				&& ((borrowerId == null) || (borrowerId.equals("")))) {
			type = 1;

			Log.log(Log.DEBUG, "GMAction", "showRepaymentSchedule", " cgpan - "
					+ cgpan);

			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showRepaymentSchedule",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			gmActionForm.setBorrowerIdForSchedule(bIdForThisCgpan);

			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Repayment Schedule Details cannot be modified for this borrower since Claim Application has been submitted");
			}

			repaymentSchedules = gmProcessor.viewRepaymentSchedule(cgpan, type);

			gmActionForm.setRepaymentSchedules(repaymentSchedules);

			Log.log(Log.INFO, "GMAction", "showRepaymentSchedule",
					"repqyment schedule size :" + repaymentSchedules.size());
			if (repaymentSchedules == null || repaymentSchedules.size() == 0) {
				request.setAttribute("message",
						"No Repayment Schedule Details Available");
				forward = Constants.SUCCESS;
			} else {

				forward = "forwardPage";
			}

		}
		// borrower name
		else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * } gmActionForm.setBorrowerIdForSchedule(bIdForBorrowerName);
			 * 
			 * repaymentSchedules =
			 * gmProcessor.viewRepaymentSchedule(borrowerName, type);
			 */
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			}
			Log.log(Log.INFO, "GMAction", "showRepaymentSchedule",
					"bIdForBorrowerName" + bIdForBorrowerName.size());
			gmActionForm.setBorrowerIds(bIdForBorrowerName);

			Log.log(Log.INFO, "GMAction", "showRepaymentSchedule",
					"gmActionForm" + gmActionForm.getBorrowerIds());
			// session.setAttribute("displayFlag","4");

			forward = "bidList";
		}

		memberids = null;
		borrowerIds = null;

		Log.log(Log.INFO, "GMAction", "showRepaymentSchedule", "Exited");
		return mapping.findForward(forward);

	}

	/**
	 * 
	 * This method saves the repayment schedule details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward saveRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "repaymentScheduleSaved", "Entered");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		GMActionForm gmActionForm = (GMActionForm) form;
		RepaymentSchedule repaymentSchedule;
		ArrayList repaymentSchedules = new ArrayList();
		GMProcessor gmProcessor = new GMProcessor();

		String message = "Repayment Schedule Saved Successfully";
		java.util.Map cgpanMap = gmActionForm.getCgpans();
		java.util.Set cgpanSet = cgpanMap.keySet();
		java.util.Iterator cgpanIterator = cgpanSet.iterator();

		java.util.Map moratoriumMap = gmActionForm.getMoratorium();
		java.util.Set moratoriumSet = moratoriumMap.keySet();
		java.util.Iterator moratoriumIterator = moratoriumSet.iterator();

		java.util.Map dueDateMap = gmActionForm.getFirstInstallmentDueDate();
		Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule",
				"first installment due date " + dueDateMap.toString());
		java.util.Set dueDateSet = dueDateMap.keySet();
		java.util.Iterator dueDateIterator = dueDateSet.iterator();

		java.util.Map periodicityMap = gmActionForm.getPeriodicity();
		Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule",
				"periodicity map " + periodicityMap.toString());
		java.util.Set periodicitySet = periodicityMap.keySet();
		java.util.Iterator periodicityIterator = periodicitySet.iterator();

		java.util.Map noOfInstallmentMap = gmActionForm.getNoOfInstallment();
		java.util.Set noOfInstallmentSet = noOfInstallmentMap.keySet();
		java.util.Iterator noOfInstallmentIterator = noOfInstallmentSet
				.iterator();

		String cgpan = null;
		Date dueDate = null;
		int moratorium = 0;
		int noOfInstallment = 0;
		String periodicity = null;
		String key = null;
		String mor = null;
		String noOfInstall = null;
		String per = null;
		String dtStr = null;

		while (cgpanIterator.hasNext()) {
			key = (String) cgpanIterator.next();
			repaymentSchedule = new RepaymentSchedule();

			cgpan = (String) cgpanMap.get(key);
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule", "cgpan"
					+ cgpan);
			repaymentSchedule.setCgpan(cgpan);

			mor = (String) moratoriumMap.get(key);
			if (!mor.equals("")) {
				moratorium = Integer.parseInt(mor);
				repaymentSchedule.setMoratorium(moratorium);
			}
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule",
					"moratorium" + moratorium);

			noOfInstall = (String) noOfInstallmentMap.get(key);
			if (!noOfInstall.equals("")) {
				noOfInstallment = Integer.parseInt(noOfInstall);
				repaymentSchedule.setNoOfInstallment(noOfInstallment);
			}
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule",
					"no of installment " + noOfInstallment);

			periodicity = ((String) periodicityMap.get(key));
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule",
					"periodicity " + periodicity);
			repaymentSchedule.setPeriodicity(periodicity);

			dtStr = (String) dueDateMap.get(key);
			dueDate = simpleDateFormat.parse(dtStr, new ParsePosition(0));
			Log.log(Log.DEBUG, "GMAction", "saveRepaymentSchedule", "due date"
					+ dueDate);
			repaymentSchedule.setFirstInstallmentDueDate(dueDate);

			repaymentSchedules.add(repaymentSchedule);
		}
		User user = getUserInformation(request);

		gmProcessor.updateRepaymentSchedule(repaymentSchedules,
				user.getUserId());

		request.setAttribute("message", message);
		Log.log(Log.INFO, "GMAction", "repaymentScheduleSaved", "Exited");
		// dynaActionForm.initialize(mapping) ;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * This method gets the outstanding details when the link is clicked
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showOutstandingDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsLink", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String cgpan = gmActionForm.getCgpan().toUpperCase();
		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		if (cgpan == null) {
			gmActionForm.setCgpan("");
		} else if (!cgpan.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		if (borrowerName == null) {
			gmActionForm.setBorrowerName("");
		} else if (!borrowerName.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsLink", "Exited");
		return showOutstandingDetails(mapping, form, request, response);

	}

	/**
	 * 
	 * This method gets the disbursement details when the link is clicked
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDisbursementDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsLink", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String cgpan = gmActionForm.getCgpan().toUpperCase();
		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();

		// System.out.println("borrower Id :" +
		// gmActionForm.getBorrowerId().toUpperCase());
		String borrowerName = gmActionForm.getBorrowerName();

		if (cgpan == null) {
			gmActionForm.setCgpan("");
		} else if (!cgpan.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		if (borrowerName == null) {
			gmActionForm.setBorrowerName("");
		} else if (!borrowerName.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsLink", "Exited");
		return showDisbursementDetails(mapping, form, request, response);

		// return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the repayment details when the link is clicked
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRepaymentDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsLink", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String cgpan = gmActionForm.getCgpan();
		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		if (cgpan == null) {
			gmActionForm.setCgpan("");
		} else if (!cgpan.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		if (borrowerName == null) {
			gmActionForm.setBorrowerName("");
		} else if (!borrowerName.equals("")) {
			gmActionForm.setBorrowerId("");
		}

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsLink", "Exited");
		return showRepaymentDetails(mapping, form, request, response);

	}

	/**
	 * 
	 * This method gets the NPA details when the link is clicked
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showNPADetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showNPADetailsLink", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();
		String cgpan = gmActionForm.getCgpan().toUpperCase();
		Log.log(Log.DEBUG, "GMAction", "showNPADetailsLink", "cgpan " + cgpan);
		String borrowerName = gmActionForm.getBorrowerName();
		Log.log(Log.DEBUG, "GMAction", "showNPADetailsLink", "B name"
				+ borrowerName);

		if (cgpan == null) {
			gmActionForm.setCgpan("");
		}
		if (borrowerName == null) {
			gmActionForm.setBorrowerName("");
		}

		Log.log(Log.INFO, "GMAction", "showNPADetailsLink", "Exited");
		return showNPADetails(mapping, form, request, response);

	}

	/**
	 * 
	 * This method gets the recovery details when the link is clicked
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRecoveryDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRecoveryDetailsLink", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		String memberId = gmActionForm.getMemberId();
		String borrowerId = gmActionForm.getBorrowerId().toUpperCase();
		String cgpan = gmActionForm.getCgpan();
		String borrowerName = gmActionForm.getBorrowerName();

		if (cgpan == null) {
			gmActionForm.setCgpan("");
		}
		if (borrowerName == null) {
			gmActionForm.setBorrowerName("");
		}

		Log.log(Log.INFO, "GMAction", "showRecoveryDetailsLink", "Exited");
		return showRecoveryDetails(mapping, form, request, response);

	}

	/**
	 * 
	 * This method gets the id either a borrower , cgpan or a member, all
	 * members to show closure details
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward getIdForClosure(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getIdForClosure", "Entered");

		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setBankIdForClosure(bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberIdForClosure(memberId);
		gmActionForm.setBorrowerIdForClosure("");
		gmActionForm.setBorrowerNameForClosure("");
		gmActionForm.setCgpanForClosure("");

		/*
		 * ArrayList mliInfos = null; ArrayList mliMemberIds = new ArrayList();
		 * 
		 * String mliMemberId = ""; String mliBankId = ""; String mliZoneId =
		 * ""; String mliBranchId = ""; GMActionForm
		 * gmActionForm=(GMActionForm)form;
		 * 
		 * GMProcessor gmProcessor = new GMProcessor (); Registration
		 * registration = new Registration(); MLIInfo mliInfo = null;
		 * 
		 * mliInfos = registration.getAllMembers(); int mliSize =
		 * mliInfos.size() ; for(int i = 0; i<mliSize; ++i) { mliInfo =
		 * (MLIInfo)mliInfos.get(i); mliBankId = mliInfo.getBankId(); mliZoneId
		 * = mliInfo.getZoneId(); mliBranchId = mliInfo.getBranchId();
		 * mliMemberId = mliBankId.concat(mliZoneId).concat(mliBranchId);
		 * mliMemberIds.add(mliMemberId); }
		 * gmActionForm.setMemberIdsForClosure(mliMemberIds) ;
		 */

		Log.log(Log.INFO, "GMAction", "getIdForClosure", "Exited");
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
	public ActionForward getIdForClosureRequest(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getIdForClosureRequest", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;
		ApplicationReport appReport = new ApplicationReport();
		ReportManager reportManager = new ReportManager();

		String memberId = gmActionForm.getMemberIdForClosure();
		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		// System.out.println("Member Id:"+memberId+" Cgpan:"+cgpan);;

		gmActionForm.setMemberIdForClosure(memberId);
		gmActionForm.setCgpanForClosure(cgpan);
		appReport = reportManager.getApplicationReportForCgpan(cgpan);
		gmActionForm.setClosureRemarks("");
		gmActionForm.setApplicationReport(appReport);
		Log.log(Log.INFO, "GMAction", "getIdForClosureRequest", "Exited");
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
	public ActionForward getIdForClosureRequestInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "getIdForClosureRequestInput", "Entered");
		// System.out.println("getIdForClosureRequestInput  Entered:");
		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		gmActionForm.setBankIdForClosure(bankId);

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		gmActionForm.setMemberIdForClosure(memberId);
		gmActionForm.setClosureRemarks("");
		gmActionForm.setCgpanForClosure("");

		Log.log(Log.INFO, "GMAction", "getIdForClosureRequestInput", "Exited");
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
	public ActionForward submitClosureDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "submitClosureDetails", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		GMDAO gmDAO = new GMDAO();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ClosureDetail closureDtl = new ClosureDetail();
		HashMap closureDetails = null;

		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);

		User user = getUserInformation(request);

		String userId = user.getUserId();
		// System.out.println("User Id:"+userId);

		String memberId = gmActionForm.getMemberIdForClosure();
		// System.out.println("Member Id : "+memberId);

		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		// System.out.println("CGPAN : "+cgpan);

		// String borrowerId =
		// gmActionForm.getBorrowerIdForClosure().toUpperCase();
		// gmActionForm.setMemberIdForClosure(memberId) ;
		// session.setAttribute("closureMemberId",memberId);

		String closureRemarks = gmActionForm.getclosureRemarks();

		java.sql.Date startDate = null;
		java.util.Date sDate = (java.util.Date) gmActionForm.getclosureDate();
		String dateString = "01/04/2011";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d;
		try {
			d = dateFormat.parse(dateString);
			dateFormat.applyPattern("yyyy-MM-dd");
			dateString = dateFormat.format(d);
			// System.out.println("dateString:"+dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.sql.Date a3 = java.sql.Date.valueOf(dateString);
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("a3:"+a3);
		// System.out.println("startDate:"+startDate);
		if (startDate != null && (a3.compareTo(startDate) > 0)) {

			throw new DatabaseException(
					" Closure date can not be less than 01/04/2011 ");

		}
		// bug fixed by sukumar on 16-07-2009--closure date can not be exceed
		// system date
		java.util.Date toDate = new java.util.Date();
		java.sql.Date sysDate = new java.sql.Date(toDate.getTime());
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		String endDate = dateFormat1.format(toDate);
		if (startDate.compareTo(sysDate) > 0) {
			throw new DatabaseException(
					" Closure date can not be greater than " + endDate);
		}

		// System.out.println("Closure Date : "+startDate);
		// System.out.println("Remarks : "+closureRemarks);

		int type = 0;

		String forward = "";

		// Validating the member Id
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}
		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		// cgpan
		if ((!cgpan.equals(""))) {
			type = 1;
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "submitClosureDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Application cannot be Closed for this borrower since Claim Application has been submitted");
			}

			Application application = new Application();
			try {
				application = appProcessor.getAppForCgpan(memberId, cgpan);
			} catch (DatabaseException databaseException) {
				if (databaseException.getMessage().equals(
						"Application does not exist."))
					throw new DatabaseException(
							"The application is not a live application");
			}
			if (application.getStatus().equals("CL"))
				throw new DatabaseException(
						"The application has already been closed");
			closureDetails = gmProcessor.viewClosureDetails(cgpan, type,
					memberId);
			if (closureDetails.isEmpty())
				throw new NoDataException(
						"There are no Closure Details for the Entered ID");
			gmActionForm.setClosureDetails(closureDetails);

			forward = "success";

		}

		Log.log(Log.INFO, "GMAction", "submitClosureDetails", "Exited");

		gmDAO.submitClosureDetails(memberId, cgpan, startDate, closureRemarks,
				userId);

		request.setAttribute("message", "<b>Closure request for " + cgpan
				+ " updated successfully.<b><br>");

		return mapping.findForward(forward);

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
	public ActionForward showClosureApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showClosureApproval", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		GMActionForm gmActionForm = (GMActionForm) form;

		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();
		ArrayList closureDetails = gmProcessor
				.displayRequestedForClosureApproval();

		ArrayList closureDetailsMod1 = (ArrayList) closureDetails.get(0);
		ArrayList closureDetailsMod2 = (ArrayList) closureDetails.get(1);

		if (closureDetails.size() == 0 && closureDetailsMod1.size() == 0
				&& closureDetailsMod2.size() == 0) {
			Log.log(Log.INFO, "GMAction", "showClosureApproval",
					"emty closure list");
			request.setAttribute("message",
					"No Closure Details available for Approval");
			forward = "success";
		} else {
			gmActionForm.setDanSummaries(closureDetailsMod1);
			gmActionForm.setClosureDetailsReq(closureDetailsMod2);
			forward = "closureList";
		}
		return mapping.findForward(forward);

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
	public ActionForward afterClosureApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GMActionForm gmPeriodicInfoForm = (GMActionForm) form;
		GMDAO gmDAO = new GMDAO();
		RpDAO rpDAO = new RpDAO();
		Map clearCgpan = gmPeriodicInfoForm.getClearCgpan();
		Set clearCgpanSet = clearCgpan.keySet();
		Iterator clearCgpanIterator = clearCgpanSet.iterator();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Connection connection = DBConnection.getConnection(false);
		User user = getUserInformation(request);
		String userId = user.getUserId();

		while (clearCgpanIterator.hasNext()) {
			String key = (String) clearCgpanIterator.next();
			String decision = (String) clearCgpan.get(key);
			if (!(decision.equals(""))) {
				if (decision
						.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
					DANSummary danSummary = new DANSummary();
					danSummary = gmDAO.getRequestedForClosureApplication(key);
					// System.out.println("Member Id: "+danSummary.getMemberId()+"Dan Id:-->"+danSummary.getDanId()+"-->CGPAN "+key+"-->"+"decision:"+decision+"closure date:-->"+danSummary.getClosureDate());
					if (danSummary.getDanId() == null
							|| danSummary.getDanId().equals("")) {
						gmDAO.updateApplicationStatusForClosedCases(key,
								danSummary, user);

					} else {
						gmDAO.insertDanDetailsForClosure(danSummary.getDanId(),
								key, danSummary, user, connection);
					}
					// String bankId1 = danSummary.getMemberId().substring(0,4);
					// String danIdNew=null;
					// danIdNew = rpDAO.getDANId("SF",bankId1,connection);
					// gmDAO.insertDanDetailsForClosure(danIdNew,key,danSummary,user,connection);

					request.setAttribute("message",
							"<b>The Request for Closure application approved successfully.<b><br>");
				}
				/*
				 * else { System.out.println(key+"-->"+"decision:"+decision);
				 * request.setAttribute("message",
				 * "<b>The Request for Closure application rejected successfully.<b><br>"
				 * ); }
				 */
			}
		}

		Map closureCgpan = gmPeriodicInfoForm.getClosureCgpan();
		Set closureCgpanSet = closureCgpan.keySet();
		Iterator closureCgpanIterator = closureCgpanSet.iterator();
		while (closureCgpanIterator.hasNext()) {
			String key = (String) closureCgpanIterator.next();

			String decision = (String) closureCgpan.get(key);
			if (!(decision.equals(""))) {
				if (decision
						.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
					DANSummary danSummaryNew = new DANSummary();
					danSummaryNew = gmDAO
							.getRequestedForClosureApplication(key);
					String bankId = danSummaryNew.getMemberId().substring(0, 4);
					String danId = null;
					double currentSFee = danSummaryNew.getAmountDue();
					double estSFee = danSummaryNew.getAmountBeingPaid();
					if (Math.round(estSFee) <= 0) {
						danId = null;
						// System.out.println("Member Id: "+danSummaryNew.getMemberId()+"Dan Id:-->"+danId+"-->CGPAN "+key+"-->"+"decision:"+decision+"closure date:-->"+danSummaryNew.getClosureDate()+"Est SFee:-->"+estSFee);
						gmDAO.updateApplicationStatusForClosedCases(key,
								danSummaryNew, user);
					} else {

						Connection conn = DBConnection.getConnection(false);
						String query = "  select APP_SANCTION_DT from application_detail where cgpan='"
								+ key + "'";
						ResultSet rs = null;
						Statement prepStatement = null;
						try {
							prepStatement = conn.createStatement();
							rs = prepStatement.executeQuery(query);
							Date sancDt = null;
							if (rs.next()) {
								sancDt = rs.getDate(1);
							}
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.DATE, 31);
							cal.set(Calendar.MONTH, 11);
							cal.set(Calendar.YEAR, 2012);
							Date dt1 = cal.getTime(); // before 2013
							Calendar cal2 = Calendar.getInstance();
							cal2.set(Calendar.DATE, 1);
							cal2.set(Calendar.MONTH, 0);
							cal2.set(Calendar.YEAR, 2013);
							Date dt2 = cal2.getTime(); // from 2013

							if (sancDt.compareTo(dt1) == -1) {
								danId = rpDAO
										.getDANId("SF", bankId, connection);
							} else if (sancDt.compareTo(dt2) == 1) {
								danId = rpDAO
										.getDANId("AF", bankId, connection);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							if (rs != null) {
								rs.close();
							}
							rs = null;
							if (prepStatement != null) {
								prepStatement.close();
							}
							prepStatement = null;
							if (conn != null) {
								DBConnection.freeConnection(conn);
							}
							conn = null;
						}

						// danId = rpDAO.getDANId("SF",bankId,connection);
						// REPLACING IT WITH APP_SACTION_DT
						// System.out.println("Member Id: "+danSummaryNew.getMemberId()+"Dan Id:-->"+danId+"-->CGPAN "+key+"-->"+"decision:"+decision+"closure date:-->"+danSummaryNew.getClosureDate()+"Est SFee:-->"+estSFee);
						gmDAO.insertDanDetailsForClosure(danId, key,
								danSummaryNew, user, connection);
					}
					request.setAttribute("message",
							"<b>The Request for Closure application approved successfully.<b><br>");
				}
				/*
				 * else { System.out.println(key+"-->"+"decision:"+decision);
				 * request.setAttribute("message",
				 * "<b>The Request for Closure application rejected successfully.<b><br>"
				 * ); }
				 */
			}
		}
		clearCgpan.clear();
		closureCgpan.clear();

		return mapping.findForward("success");
	}

	/**
	 * 
	 * This method gets the closure details for the selected id
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showClosureDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showClosureDetails", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ClosureDetail closureDtl = new ClosureDetail();
		HashMap closureDetails = null;
		// HashMap borrowerInfos = new HashMap();

		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);

		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		String borrowerId = gmActionForm.getBorrowerIdForClosure()
				.toUpperCase();
		String memberId = gmActionForm.getMemberIdForClosure();
		// System.out.println("memberId : "+memberId);
		gmActionForm.setMemberIdForClosure(memberId);
		session.setAttribute("closureMemberId", memberId);
		String borrowerName = gmActionForm.getBorrowerNameForClosure();
		int type = 0;

		String forward = "";

		// Validating the member Id
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		if ((!borrowerId.equals("")) && (cgpan.equals(""))
				&& (borrowerName.equals(""))) {
			type = 0;
			Log.log(Log.DEBUG, "GMAction", "showClosureDetails", "borrowerId");
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}
			int claimCount = appProcessor.getClaimCount(borrowerId);
			if (claimCount > 0) {
				throw new MessageException(
						"Application cannot be closed for this borrower since Claim Application has been submitted");
			}

			closureDetails = gmProcessor.viewClosureDetails(borrowerId, type,
					memberId);

			if (closureDetails.isEmpty()) {
				throw new NoDataException(
						"There are no Closure Details for the Entered ID");
			}

			ArrayList reasons = gmProcessor.getAllReasonsForClosure();
			gmActionForm.setClosureReasons(reasons);
			gmActionForm.setClosureDetails(closureDetails);

			forward = "success";

		} // cgpan
		else if ((!cgpan.equals("")) && (borrowerId.equals(""))
				&& (borrowerName.equals(""))) {
			type = 1;
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showClosureDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			int claimCount = appProcessor.getClaimCount(bIdForThisCgpan);
			if (claimCount > 0) {
				throw new MessageException(
						"Application cannot be Closed for this borrower since Claim Application has been submitted");
			}

			Application application = new Application();
			try {

				application = appProcessor.getAppForCgpan(memberId, cgpan);
				// System.out.println("Comming out PATH from GMAction appProcessor.getAppForCgpan(memberId,cgpan) memberId = "+memberId+"  cgpan ="+cgpan);

			} catch (DatabaseException databaseException) {
				// System.out.println("database Exception :" +
				// databaseException.getMessage());
				if (databaseException.getMessage().equals(
						"Application does not exist.")) {
					throw new DatabaseException(
							"The application is not a live application");
				}

			}
			/*
			 * if(application.getStatus().equals("CL")) { throw new
			 * DatabaseException("The application has already been closed"); }
			 */closureDetails = gmProcessor.viewClosureDetails(cgpan, type,
					memberId);
			if (closureDetails.isEmpty()) {
				throw new NoDataException(
						"There are no Closure Details for the Entered ID");
			}

			ArrayList reasons = gmProcessor.getAllReasonsForClosure();
			gmActionForm.setClosureReasons(reasons);
			gmActionForm.setClosureDetails(closureDetails);

			forward = "success";

		}
		// borrowerName
		else if ((!borrowerName.equals("")) && (borrowerId.equals(""))
				&& (cgpan.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			/*
			 * if(!(borrowerIds.contains(bIdForBorrowerName)) ) { throw new
			 * NoDataException(borrowerName+"is not a valid SSI name for " +
			 * "the Member Id :" + memberId +". Please enter correct SSI name");
			 * }
			 * 
			 * closureDetails =
			 * gmProcessor.viewClosureDetails(borrowerName,type);
			 */
			gmActionForm.setBorrowerIds(bIdForBorrowerName);

			forward = "bidList";
		}

		Log.log(Log.INFO, "GMAction", "showClosureDetails", "Exited");

		return mapping.findForward(forward);

	}

	public ActionForward saveClosureDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "saveClosureDetails", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();
		CgpanInfo closureCgpanInfo = null;
		// System.out.println("PATH Inside GMAction  saveClosureDetails");
		Enumeration parameters = request.getParameterNames();

		while (parameters.hasMoreElements()) {
			String parameter = (String) parameters.nextElement();

			String value = request.getParameter(parameter);
			Log.log(Log.DEBUG, "GMAction", "##saveClosureDetails##",
					"param and value " + parameter + ", " + value);
		}

		java.util.Map closureCgpanMap = gmActionForm.getClosureCgpans();
		java.util.Set closureCgpanSet = closureCgpanMap.keySet();
		java.util.Iterator closureCgpanIterator = closureCgpanSet.iterator();

		// System.out.println("GMAction cgpan map closureCgpanMap.size() "+closureCgpanMap.size());

		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "cgpan map "
				+ closureCgpanMap.size());

		java.util.Map reasonMap = gmActionForm.getReasonForClosure();
		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "reason map "
				+ reasonMap.size());
		java.util.Set reasonSet = reasonMap.keySet();
		java.util.Iterator reasonIterator = reasonSet.iterator();

		java.util.Map closureFlagMap = gmActionForm.getClosureFlag();
		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "closureFlag map "
				+ closureFlagMap.size());
		java.util.Set closureFlagSet = closureFlagMap.keySet();
		java.util.Iterator closureFlagIterator = closureFlagSet.iterator();

		java.util.Map remarksForClosureMap = gmActionForm
				.getRemarksForClosure();
		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "Remarks map "
				+ remarksForClosureMap.size());
		java.util.Set remarksForClosureSet = remarksForClosureMap.keySet();
		java.util.Iterator remarksForClosureIterator = remarksForClosureSet
				.iterator();

		User user = getUserInformation(request);
		String userId = user.getUserId();
		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "userId " + userId);

		String fromId = user.getEmailId();
		Log.log(Log.DEBUG, "GMAction", "saveClosureDetails", "fromId " + fromId);

		String cgpan = null;
		String reason = null;
		String closeFlag = null;
		String closureRemarks = null;
		ArrayList closureCgpanInfos = new ArrayList();

		boolean closureStatus = false;
		String message = "Application are successfully closed";
		String errorMessage = "";
		while (closureCgpanIterator.hasNext()) {
			cgpan = (String) closureCgpanMap.get(closureCgpanIterator.next());
			// Start Code by Path on 12Oct06
			closureCgpanIterator.remove();
			// End Code By Path
			Log.log(Log.DEBUG, "GMAction--", "Closure", "cgpan" + cgpan);
			if (closureFlagMap.containsKey(cgpan)) {

				closeFlag = (String) closureFlagMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "Flag" + closeFlag);

				reason = (String) reasonMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "reason" + reason);

				closureRemarks = (String) remarksForClosureMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "remarks"
						+ closureRemarks);

				closureCgpanInfo = new CgpanInfo();
				closureCgpanInfo.setCgpan(cgpan);
				closureCgpanInfo.setReasonForClosure(reason);
				closureCgpanInfo.setRemarks(closureRemarks);
				closureCgpanInfos.add(closureCgpanInfo);
				try {
					// System.out.println("PATH before GMAction.gmProcessor.sendMailForClosure(cgpan,userId,fromId,reason)");
					gmProcessor.sendMailForClosure(cgpan, userId, fromId,
							reason);
					// System.out.println("PATH after GMAction.gmProcessor.sendMailForClosure(cgpan,userId,fromId,reason)");
				} catch (MailerException e) {
					errorMessage = " But Sending E-mails failed.";
				}
				// System.out.println("PATH before GMAction.gmProcessor.closure(cgpan, reason, closureRemarks, userId)");
				closureStatus = gmProcessor.closure(cgpan, reason,
						closureRemarks, userId);
				// System.out.println("PATH after GMAction.gmProcessor.closure(cgpan, reason, closureRemarks, userId)");
			}
		}
		request.setAttribute("message", message + errorMessage);
		Log.log(Log.INFO, "GMAction", "saveClosureDetails", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showFilterForFeeNotPaid(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showFilterForFeeNotPaid", "Entered");

		Log.log(Log.INFO, "GMAction", "showFilterForFeeNotPaid", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showDetailsForFeeNotPaid(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showDetailsForFeeNotPaid", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;

		String feeType = gmActionForm.getFeeNotPaid();
		Log.log(Log.DEBUG, "GMAction", "showDetailsForFeeNotPaid", "Fee Type"
				+ feeType);

		TreeMap memberIdCgpans = gmProcessor
				.viewMemberIdCgpansForClosure(feeType);
		gmActionForm.setMemberIdCgpans(memberIdCgpans);

		Log.log(Log.INFO, "GMAction", "showDetailsForFeeNotPaid", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward closeAppsForFeeNotPaid(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "closeAppsForFeeNotPaid", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;

		String feeType = gmActionForm.getFeeNotPaid();
		Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid", "Fee Type"
				+ feeType);

		String memberId = (String) request.getParameter("memberIdClosure");
		Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid", "memberId "
				+ memberId);

		HttpSession session = request.getSession(false);
		session.setAttribute("clNotPaid", memberId);

		ArrayList cgpans = gmProcessor.viewCgpansForClosure(memberId, feeType);

		int size = cgpans.size();
		Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid", "cgpans.size "
				+ size);
		Set memidSet = null;
		Iterator idIter = null;

		String cgpan = null;

		String memberIdCl = null;
		String bIdCl = null;
		String bNameCl = null;
		String panCl = null;
		String schemeCl = null;
		double sancAmtCl = 0;

		int panSize = 0;

		Set borrSet = null;
		Iterator borrIter = null;

		HashMap borrInfos = null;
		ArrayList cgpanInfos = null;

		BorrowerInfo borrInfo = null;
		CgpanInfo cgpanInfo = null;

		HashMap borrInfosTemp = null;
		ArrayList cgpanInfosTemp = null;
		ArrayList cgpanInfosTemp1 = new ArrayList();

		HashMap clDtls = null;
		HashMap clDtlsTemp = new HashMap();

		BorrowerInfo borrInfoTemp = null;
		CgpanInfo cgpanInfoTemp = null;

		borrInfosTemp = new HashMap();

		for (int i = 0; i < size; ++i) {
			cgpan = (String) cgpans.get(i);
			Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid", "cgpan "
					+ cgpan);

			clDtls = gmProcessor.getClosureDetailsForFeeNotPaid(cgpan);

			memidSet = clDtls.keySet();
			idIter = memidSet.iterator();

			while (idIter.hasNext()) {
				memberIdCl = (String) idIter.next();
				Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid",
						"memberIdCl " + memberIdCl);

				borrInfos = (HashMap) clDtls.get(memberIdCl);

				borrSet = borrInfos.keySet();
				borrIter = borrSet.iterator();

				while (borrIter.hasNext()) {
					bIdCl = (String) borrIter.next();
					Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid",
							"bIdCl " + bIdCl);

					borrInfo = (BorrowerInfo) borrInfos.get(bIdCl);

					bNameCl = borrInfo.getBorrowerName();
					Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid",
							"bNameCl " + bNameCl);

					cgpanInfos = borrInfo.getCgpanInfos();
					panSize = cgpanInfos.size();
					Log.log(Log.DEBUG, "GMAction", "closeAppsForFeeNotPaid",
							"panSize " + panSize);

					for (int j = 0; j < panSize; ++j) {
						cgpanInfo = (CgpanInfo) cgpanInfos.get(j);

						panCl = cgpanInfo.getCgpan();
						Log.log(Log.DEBUG, "GMAction",
								"closeAppsForFeeNotPaid", "panCl " + panCl);

						schemeCl = cgpanInfo.getScheme();
						Log.log(Log.DEBUG, "GMAction",
								"closeAppsForFeeNotPaid", "schemeCl "
										+ schemeCl);

						sancAmtCl = cgpanInfo.getSanctionedAmount();
						Log.log(Log.DEBUG, "GMAction",
								"closeAppsForFeeNotPaid", "sancAmtCl "
										+ sancAmtCl);
					}
				}
			}

			borrInfoTemp = new BorrowerInfo();
			cgpanInfoTemp = new CgpanInfo();
			cgpanInfosTemp = new ArrayList();

			cgpanInfoTemp.setCgpan(panCl);
			cgpanInfoTemp.setScheme(schemeCl);
			cgpanInfoTemp.setSanctionedAmount(sancAmtCl);
			cgpanInfosTemp.add(cgpanInfoTemp);

			borrInfoTemp.setBorrowerId(bIdCl);
			borrInfoTemp.setBorrowerName(bNameCl);

			if (borrInfosTemp.containsKey(bIdCl)) {
				borrInfoTemp = (BorrowerInfo) borrInfosTemp.get(bIdCl);
				cgpanInfosTemp = borrInfoTemp.getCgpanInfos();
				cgpanInfosTemp.add(cgpanInfoTemp);

				borrInfoTemp.setCgpanInfos(cgpanInfosTemp);
				borrInfosTemp.put(bIdCl, borrInfoTemp);
			} else {
				borrInfoTemp.setCgpanInfos(cgpanInfosTemp);
				borrInfosTemp.put(bIdCl, borrInfoTemp);
			}
		}
		clDtlsTemp.put(memberIdCl, borrInfosTemp);

		ArrayList reasons = gmProcessor.getAllReasonsForClosure();
		gmActionForm.setClReasons(reasons);

		gmActionForm.setClosureDetailsNotPaid(clDtlsTemp);

		Log.log(Log.INFO, "GMAction", "closeAppsForFeeNotPaid", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward closeCgpans(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "closeCgpans", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		java.util.Map clCgpanMap = gmActionForm.getClCgpan();
		java.util.Set clCgpanSet = clCgpanMap.keySet();
		java.util.Iterator clCgpanIterator = clCgpanSet.iterator();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans",
				"cgpan map " + clCgpanMap.size());

		java.util.Map reasonMap = gmActionForm.getReasonForCl();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans",
				"reason map " + reasonMap.size());
		java.util.Set reasonSet = reasonMap.keySet();
		java.util.Iterator reasonIterator = reasonSet.iterator();

		java.util.Map clFlagMap = gmActionForm.getClFlag();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans", "closureFlag map "
				+ clFlagMap.size());
		java.util.Set clFlagSet = clFlagMap.keySet();
		java.util.Iterator clFlagIterator = clFlagSet.iterator();

		java.util.Map remarksForClMap = gmActionForm.getRemarksForCl();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans", "Remarks map "
				+ remarksForClMap.size());
		java.util.Set remarksForClSet = remarksForClMap.keySet();
		java.util.Iterator remarksForClIterator = remarksForClSet.iterator();

		User user = getUserInformation(request);
		String userId = user.getUserId();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans", "userId " + userId);

		String fromId = user.getEmailId();
		Log.log(Log.DEBUG, "GMAction", "closeCgpans", "fromId " + fromId);

		String cgpan = null;
		String reason = null;
		String clFlag = null;
		String clRemarks = null;

		boolean clStatus = false;
		String message = "Application are successfully closed";
		String errorMessage = "";
		while (clCgpanIterator.hasNext()) {
			cgpan = (String) clCgpanMap.get(clCgpanIterator.next());
			Log.log(Log.DEBUG, "GMAction--", "Closure", "cgpan" + cgpan);

			if (clFlagMap.containsKey(cgpan)) {
				clFlag = (String) clFlagMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "Flag" + clFlag);

				reason = (String) reasonMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "reason" + reason);

				clRemarks = (String) remarksForClMap.get(cgpan);
				Log.log(Log.DEBUG, "GMAction--", "Closure", "remarks"
						+ clRemarks);

				try {
					gmProcessor.sendMailForClosure(cgpan, userId, fromId,
							reason);
				} catch (MailerException e) {
					errorMessage = " But Sending E-mails failed.";
				}
				clStatus = gmProcessor
						.closure(cgpan, reason, clRemarks, userId);
			}

		}

		request.setAttribute("message", message + errorMessage);

		Log.log(Log.INFO, "GMAction", "closeCgpans", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward getIdForExport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "getIdForExport", "Entered");

		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;

		User user = getUserInformation(request);

		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		if (!bankId.equalsIgnoreCase(Constants.CGTSI_USER_BANK_ID)) {
			gmActionForm.setMemberIdForExport(memberId);
			Log.log(Log.INFO, "GMAction", "getIdForExport", "memberId :"
					+ memberId);
			exportPeriodicInfo(mapping, form, request, response);
			return mapping.findForward("ExportForInternet");
		} else {
			gmActionForm.setMemberIdForExport("");
			Log.log(Log.INFO, "GMAction", "getIdForExport", "Exited");
			return mapping.findForward(Constants.SUCCESS);
		}

	}

	public ActionForward showCgpanDetailsLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showCgpanDetailsLink", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		CgpanDetail cgpanDetail = null;

		GMActionForm gmActionForm = (GMActionForm) form;

		String cgpan = (String) request.getParameter("cgpanDetail");
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "cgpan" + cgpan);
		cgpanDetail = gmProcessor.getCgpanDetails(cgpan);

		String bid = cgpanDetail.getBorrowerId();
		// gmActionForm.setBorrowerIdForLink(bid);
		gmActionForm.setBidForCgpanLink(bid);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "bID" + bid);
		String bName = cgpanDetail.getBorrowerName();
		gmActionForm.setBorrowerNameForLink(bName);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "bname" + bName);
		String city = cgpanDetail.getCity();
		gmActionForm.setCityForCgpanLink(city);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "city" + city);
		String prName = cgpanDetail.getChiefPromoterName();
		gmActionForm.setPromoterNameForLink(prName);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "pr name "
				+ prName);
		double amountApproved = cgpanDetail.getAmountApproved();
		gmActionForm.setAmountApprovedForLink(amountApproved);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "amt appd "
				+ amountApproved);
		double tcAmountSanctioned = cgpanDetail.getTcAmountSanctioned();
		gmActionForm.setTcAmountSanctionedForLink(tcAmountSanctioned);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "Tc Amt Sanc"
				+ tcAmountSanctioned);
		double wcAmountSanctioned = cgpanDetail.getWcAmountSanctioned();
		gmActionForm.setWcAmountSanctionedForLink(wcAmountSanctioned);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "wc Amt "
				+ wcAmountSanctioned);
		Date guaranteeIssueDate = cgpanDetail.getGuaranteeIssueDate();
		gmActionForm.setGuaranteeIssueDateForLink(guaranteeIssueDate);
		Log.log(Log.DEBUG, "GMAction", "showCgpanDetailsLink", "date"
				+ guaranteeIssueDate);
		Log.log(Log.INFO, "GMAction", "showCgpanDetailsLink", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward exportPeriodicInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "exportPeriodicInfo", "Entered");

		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);

		String memberId = null;

		Hashtable periodicInfos = new Hashtable();
		PeriodicInfo periodicInfo = null;

		ArrayList borrowerIds = new ArrayList();

		ArrayList osPeriodicInfoDetails = null;
		ArrayList rpPeriodicInfoDetails = null;
		ArrayList dbPeriodicInfoDetails = null;
		ArrayList recoveryDetails = null;
		NPADetails npaDetails = null;

		memberId = gmActionForm.getMemberIdForExport();

		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberIds = claimsProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		ArrayList osDtls = null;
		ArrayList dbDtls = null;
		ArrayList rpDtls = null;

		ClaimsProcessor cl = new ClaimsProcessor();
		String borrowerName = null;
		String borrowerId = null;

		int type = 0;

		for (int i = 0; i < borrowerIds.size(); ++i) {
			periodicInfo = new PeriodicInfo();

			borrowerId = (String) borrowerIds.get(i);

			periodicInfo.setBorrowerId(borrowerId);

			com.cgtsi.claim.BorrowerInfo bInfoCl = (com.cgtsi.claim.BorrowerInfo) cl
					.getBorrowerDetails(borrowerId);
			borrowerName = bInfoCl.getBorrowerName();
			Log.log(Log.DEBUG, "GMAction", "exportPeriodicInfo",
					"borrowerName " + borrowerName);
			Log.log(Log.DEBUG, "GMAction", "exportPeriodicInfo", "borrowerId "
					+ borrowerId);
			periodicInfo.setBorrowerName(borrowerName);

			Vector CgpansForBid = gmProcessor.getCGPANs(borrowerId);
			int vectorSize = CgpansForBid.size();

			// Disbursement Detail
			Disbursement disbursementDetail = null;
			dbDtls = new ArrayList();

			for (int j = 0; j < vectorSize; j++) {
				HashMap cgpanDetails = (HashMap) CgpansForBid.get(j);
				Set cgpanSet = cgpanDetails.keySet();
				Iterator cgpanIterator = cgpanSet.iterator();

				while (cgpanIterator.hasNext()) {
					String cgpan = (String) cgpanIterator.next();
					if (cgpan.equals("CGPAN")) {
						String cgpanValue = (String) cgpanDetails.get(cgpan);
						// check TC or WC
						int cgpanLength = cgpanValue.length();
						int type1 = cgpanLength - 2;
						int type2 = cgpanLength - 1;
						String cgpanType = cgpanValue.substring(type1, type2);
						if (cgpanType.equalsIgnoreCase("t")) {
							disbursementDetail = getDisbursementDetailsForCgpan(cgpanValue);
							String scheme = disbursementDetail.getScheme();
							double sanctionedAmt = disbursementDetail
									.getSanctionedAmount();
							ArrayList disbursementAmounts = disbursementDetail
									.getDisbursementAmounts();

							if ((disbursementAmounts == null)
									|| (disbursementAmounts.size() == 0)) {
								disbursementDetail = new Disbursement();
								disbursementDetail.setCgpan(cgpanValue);
								disbursementDetail.setScheme(scheme);
								disbursementDetail
										.setSanctionedAmount(sanctionedAmt);
								dbDtls.add(disbursementDetail);
								Log.log(Log.DEBUG, "GMAction",
										"exportPeriodicInfo",
										"cgpan added dis dtl " + cgpanValue);
							} else {
								dbDtls.add(disbursementDetail);
							}
						}
					}

					else {
						continue;
					}
				}
			}
			periodicInfo.setDisbursementDetails(dbDtls);

			// Repayment Detail
			Repayment repaymentDetail = null;
			rpDtls = new ArrayList();

			for (int j = 0; j < vectorSize; j++) {
				HashMap cgpanDetails = (HashMap) CgpansForBid.get(j);
				Set cgpanSet = cgpanDetails.keySet();
				Iterator cgpanIterator = cgpanSet.iterator();

				while (cgpanIterator.hasNext()) {
					String cgpan = (String) cgpanIterator.next();
					if (cgpan.equals("CGPAN")) {
						String cgpanValue = (String) cgpanDetails.get(cgpan);

						int cgpanLength = cgpanValue.length();
						int type1 = cgpanLength - 2;
						int type2 = cgpanLength - 1;
						String cgpanType = cgpanValue.substring(type1, type2);
						if (cgpanType.equalsIgnoreCase("t")) {
							repaymentDetail = getRepaymentDetailsForCgpan(cgpanValue);
							String scheme = repaymentDetail.getScheme();
							ArrayList repayAmts = repaymentDetail
									.getRepaymentAmounts();

							if (repayAmts == null || repayAmts.size() == 0) {
								repaymentDetail = new Repayment();
								repaymentDetail.setCgpan(cgpanValue);
								repaymentDetail.setScheme(scheme);
								rpDtls.add(repaymentDetail);
								Log.log(Log.DEBUG, "GMAction",
										"exportPeriodicInfo",
										"cgpan added dis dtl " + cgpanValue);
							} else {
								rpDtls.add(repaymentDetail);
							}
						}
					}

					else {
						continue;
					}
				}
			}
			periodicInfo.setRepaymentDetails(rpDtls);

			// Outstanding Detail
			OutstandingDetail outstandingDetail = null;
			osDtls = new ArrayList();

			for (int j = 0; j < vectorSize; j++) {
				HashMap cgpanDetails = (HashMap) CgpansForBid.get(j);
				Set cgpanSet = cgpanDetails.keySet();
				Iterator cgpanIterator = cgpanSet.iterator();

				while (cgpanIterator.hasNext()) {
					String cgpan = (String) cgpanIterator.next();
					if (cgpan.equals("CGPAN")) {
						String cgpanValue = (String) cgpanDetails.get(cgpan);
						outstandingDetail = getOutstandingDetailsForCgpan(cgpanValue);
						String scheme = outstandingDetail.getScheme();
						double sancAmtTc = outstandingDetail
								.getTcSanctionedAmount();
						double sancAmtWcFb = outstandingDetail
								.getWcFBSanctionedAmount();
						double sancAmtWcNfb = outstandingDetail
								.getWcNFBSanctionedAmount();
						ArrayList outstandingAmounts = outstandingDetail
								.getOutstandingAmounts();

						if ((outstandingAmounts == null)
								|| (outstandingAmounts.size() == 0)) {
							outstandingDetail = new OutstandingDetail();
							outstandingDetail.setCgpan(cgpanValue);
							outstandingDetail.setScheme(scheme);
							outstandingDetail.setTcSanctionedAmount(sancAmtTc);
							outstandingDetail
									.setWcFBSanctionedAmount(sancAmtWcFb);
							outstandingDetail
									.setWcNFBSanctionedAmount(sancAmtWcNfb);
							osDtls.add(outstandingDetail);
							Log.log(Log.DEBUG, "GMAction",
									"exportPeriodicInfo",
									"cgpan added dis dtl " + cgpanValue);
						} else {
							osDtls.add(outstandingDetail);
						}
					}

					else {
						continue;
					}
				}
			}
			periodicInfo.setOutstandingDetails(osDtls);

			// Recovery Details
			recoveryDetails = gmProcessor.getRecoveryDetails(borrowerId);
			if (recoveryDetails != null) {
				periodicInfo.setRecoveryDetails(recoveryDetails);
			}
			npaDetails = gmProcessor.getNPADetails(borrowerId);

			if (npaDetails != null) {
				periodicInfo.setNpaDetails(npaDetails);
			} else {
				NPADetails npa = new NPADetails();
				periodicInfo.setNpaDetails(npa);
			}
			periodicInfos.put(borrowerId, periodicInfo);
		}
		try {
			String contextPath1 = request.getSession(false).getServletContext()
					.getRealPath("");
			String contextPath = PropertyLoader.changeToOSpath(contextPath1);
			Log.log(Log.DEBUG, "GMAction", "exportPeriodicInfo",
					"contextPAth=>" + contextPath);
			FileOutputStream fileOutputStream = null;
			;
			ObjectOutputStream objectOutputStream = null;

			Date date = new Date();
			Format formatter;
			formatter = new SimpleDateFormat("ddMMyy");
			String s = formatter.format(date);
			Log.log(Log.DEBUG, "GMAction", "exportPeriodicInfo", "date =>" + s);

			String nameOfFile = "PER.EXP";

			String fileName = contextPath + File.separator
					+ Constants.FILE_DOWNLOAD_DIRECTORY + File.separator + s
					+ "-" + memberId + "-" + nameOfFile;
			// System.out.println("fileName:"+fileName);
			String formattedToOSPath = request.getContextPath()
					+ File.separator + Constants.FILE_DOWNLOAD_DIRECTORY
					+ File.separator + s + "-" + memberId + "-" + nameOfFile;
			;
			session.setAttribute("fileName", formattedToOSPath);

			Log.log(Log.DEBUG, "GMAction", "exportPeriodicInfo", "File PAth"
					+ fileName);

			File exportFlatFile = new File(fileName);
			if (!exportFlatFile.exists()) {
				exportFlatFile.createNewFile();
			}

			fileOutputStream = new FileOutputStream(exportFlatFile);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.writeObject(periodicInfos);
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (IOException ioexception) {
			Log.log(Log.ERROR, "GMAction", "Export Periodic Info File",
					"Export failed because " + ioexception.getMessage());
			throw new ExportFailedException("Export Failed.");
		}

		Log.log(Log.INFO, "GMAction", "exportPeriodicInfo", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward uploadFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "GMAction", "uploadFile", "Entered");

		GMProcessor gmProcessor = new GMProcessor();

		GMActionForm gmActionForm = (GMActionForm) form;

		User user = getUserInformation(request);

		/*
		 * String bankId = user.getBankId(); String zoneId = user.getZoneId();
		 * String branchId = user.getBranchId(); String memberId =
		 * bankId.concat(zoneId).concat(branchId );
		 * 
		 * gmActionForm.setMemberIdForExport(memberId);
		 */
		Log.log(Log.INFO, "GMAction", "uploadFile", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward approvePeriodicInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "approvePeriodicInfo", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		Map approveFlags = gmActionForm.getApproveBorrowerFlag();
		Set approveFlagSet = approveFlags.keySet();
		Iterator approveFlagIterator = approveFlagSet.iterator();

		GMProcessor gmProcessor = new GMProcessor();
		if (approveFlags.isEmpty() || approveFlags.size() == 0) {
			throw new MessageException(
					"Atleast One Periodic Info Details should be approved");
		}
		while (approveFlagIterator.hasNext()) {
			String key = (String) approveFlagIterator.next();

			Log.log(Log.INFO, "GMAction", "approveBorrowerDetails", "key :"
					+ key);
			String memBorrowerId = (String) approveFlags.get(key);

			Log.log(Log.INFO, "GMAction", "approveBorrowerDetails",
					"memBorrowerId :" + memBorrowerId);
			int index = memBorrowerId.indexOf("-");
			String memberId = memBorrowerId.substring(0, index);
			String borrowerId = memBorrowerId.substring(index + 1,
					memBorrowerId.length());

			/*
			 * DynaActionForm dynaForm = (DynaActionForm)form; GMProcessor
			 * gmProcessor = new GMProcessor ();
			 * 
			 * String borrowerId = (String) dynaForm.get("borrowerId"); String
			 * memberId = (String) dynaForm.get("memberId");
			 * 
			 * ClaimsProcessor cpProcessor = new ClaimsProcessor(); Vector
			 * memberIds=cpProcessor.getAllMemberIds(); if
			 * (!(memberIds.contains(memberId))) { throw new
			 * NoMemberFoundException("The Member ID does not exist"); }
			 * 
			 * if (! borrowerId.equals("")) { ArrayList
			 * borrowerIds=cpProcessor.getAllBorrowerIDs(memberId); if
			 * (!(borrowerIds.contains(borrowerId))) { throw new
			 * NoDataException(
			 * "The Borrower ID does not exist for this Member ID"); } }
			 */
			Log.log(Log.INFO, "GMAction", "approvePeriodicInfo",
					"calling approve from processor");
			gmProcessor.approvePeriodicInfo(memberId, borrowerId);
			Log.log(Log.INFO, "GMAction", "approvePeriodicInfo",
					"approved details");
		}

		/*
		 * if (! borrowerId.equals("") && ! memberId.equals("")) {
		 * request.setAttribute("message", "Periodic Info Details for " +
		 * borrowerId + " Approved Successfully"); } else if (!
		 * memberId.equals("")) { request.setAttribute("message",
		 * "Periodic Info Details for " + memberId + " Approved Successfully");
		 * }
		 */

		request.setAttribute("message",
				"Periodic Info Details have been Approved Successfully");
		Log.log(Log.INFO, "GMAction", "approvePeriodicInfo", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showOutstandingsForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showOutstandingsForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");
		String memberId = (String) dynaForm.get("memberId");

		Log.log(Log.INFO, "GMAction", "showOutstandingsForApproval",
				"borrowerId :" + borrowerId);
		Log.log(Log.INFO, "GMAction", "showOutstandingsForApproval",
				"memberId :" + memberId);

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		if (!borrowerId.equals("")) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(memberId);
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}
		}

		ArrayList osDetails = gmProcessor.getOutstandingsForBid(borrowerId,
				memberId);

		dynaForm.set("outstandingDetails", osDetails);

		/*************************************/
		/*
		 * ArrayList oldArr = (ArrayList) osDetails.get(0); ArrayList newArr =
		 * (ArrayList) osDetails.get(1);
		 * 
		 * for (int i=0;i<oldArr.size();i++) { OutstandingDetail oldOsDtl =
		 * (OutstandingDetail) oldArr.get(i); OutstandingDetail newOsDtl =
		 * (OutstandingDetail) newArr.get(i);
		 * 
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval","old cgpan -- "
		 * + oldOsDtl.getCgpan());
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval"
		 * ,"new cgpan -- " + newOsDtl.getCgpan());
		 * 
		 * ArrayList oldOsAmtsArr = oldOsDtl.getOutstandingAmounts(); ArrayList
		 * newOsAmtsArr = newOsDtl.getOutstandingAmounts();
		 * 
		 * for (int j=0;j<oldOsAmtsArr.size();j++) { OutstandingAmount oldOsAmt
		 * = (OutstandingAmount) oldOsAmtsArr.get(j); String oldCgpan =
		 * oldOsAmt.getCgpan(); for (int k=0;k<newOsAmtsArr.size();k++) {
		 * OutstandingAmount newOsAmt = (OutstandingAmount) newOsAmtsArr.get(k);
		 * String newCgpan = newOsAmt.getCgpan(); String type =
		 * newCgpan.substring(newCgpan.length()-2, newCgpan.length()-1); if
		 * (oldCgpan.equals(newCgpan) && type.equals("T")) { if
		 * (oldOsAmt.getTcoId().equals(newOsAmt.getTcoId())) {
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","old tco id -- " +
		 * oldOsAmt.getTcoId());
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval"
		 * ,"new wco id -- " + newOsAmt.getTcoId());
		 * Log.log(Log.INFO,"GMAction",
		 * "showOutstandingsForApproval","old amount -- " +
		 * oldOsAmt.getTcPrincipalOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","new amount -- " +
		 * newOsAmt.getTcPrincipalOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","old date -- " +
		 * oldOsAmt.getTcOutstandingAsOnDate());
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval"
		 * ,"new date -- " + newOsAmt.getTcOutstandingAsOnDate()); break; } }
		 * else if (oldCgpan.equals(newCgpan) && (type.equals("W")||
		 * (type.equals("R")))) { if
		 * (oldOsAmt.getWcoId().equals(newOsAmt.getWcoId())) {
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","old wco id -- " +
		 * oldOsAmt.getWcoId());
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval"
		 * ,"new wco id -- " + newOsAmt.getWcoId());
		 * Log.log(Log.INFO,"GMAction",
		 * "showOutstandingsForApproval","old principal amount -- " +
		 * oldOsAmt.getWcFBPrincipalOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","new principal amount -- " +
		 * newOsAmt.getWcFBPrincipalOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","old interest amount -- " +
		 * oldOsAmt.getWcFBInterestOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","new interest amount -- " +
		 * newOsAmt.getWcFBInterestOutstandingAmount());
		 * Log.log(Log.INFO,"GMAction"
		 * ,"showOutstandingsForApproval","old date -- " +
		 * oldOsAmt.getWcFBOutstandingAsOnDate());
		 * Log.log(Log.INFO,"GMAction","showOutstandingsForApproval"
		 * ,"new date -- " + newOsAmt.getWcFBOutstandingAsOnDate()); break; } }
		 * } } }
		 */
		/*************************************/

		Log.log(Log.INFO, "GMAction", "showOutstandingsForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showDisbursementsForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showDisbursementsForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");
		String memberId = (String) dynaForm.get("memberId");

		ArrayList disDetails = gmProcessor.getDisbursementsForBid(borrowerId,
				memberId);

		dynaForm.set("disbursementDetails", disDetails);

		Log.log(Log.INFO, "GMAction", "showDisbursementsForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showRepaymentsForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentsForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");
		String memberId = (String) dynaForm.get("memberId");

		ArrayList repayDetails = gmProcessor.getRepaymentsForBid(borrowerId,
				memberId);

		dynaForm.set("repaymentDetails", repayDetails);

		Log.log(Log.INFO, "GMAction", "showRepaymentsForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showNpaForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showNpaForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");

		ArrayList npaDetails = gmProcessor.getNpaDetailsForApproval(borrowerId);

		dynaForm.set("npaDetails", npaDetails);

		Log.log(Log.INFO, "GMAction", "showNpaForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showBorrowerDetailsForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showBorrowerDetailsForApproval",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		// String borrowerId = (String) dynaForm.get("borrowerId");
		// String memberId = (String) dynaForm.get("memberId");

		String memberId = (String) request.getParameter("memberId");
		String borrowerId = (String) request.getParameter("borrowerId");

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		if (!borrowerId.equals("")) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(memberId);
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}
		}

		BorrowerDetails borrowerDetails = gmProcessor.viewBorrowerDetails(
				memberId, borrowerId, 0);

		int borrowerRefNo = borrowerDetails.getSsiDetails().getBorrowerRefNo();

		ArrayList modifiedBorrowerDetails = gmProcessor
				.getBorrowerDetailsForApproval(borrowerRefNo);

		dynaForm.set("borrowerDetails", modifiedBorrowerDetails);
		dynaForm.set("borrowerId", borrowerId);

		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward approveBorrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "GMAction", "approveBorrowerDetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		Map approveFlags = gmActionForm.getApproveBorrowerFlag();
		Set approveFlagSet = approveFlags.keySet();
		Iterator approveFlagIterator = approveFlagSet.iterator();

		GMProcessor gmProcessor = new GMProcessor();
		if (approveFlags.isEmpty() || approveFlags.size() == 0) {
			throw new MessageException(
					"Atleast One Borrower Details should be approved");
		}

		while (approveFlagIterator.hasNext()) {
			String key = (String) approveFlagIterator.next();

			Log.log(Log.INFO, "GMAction", "approveBorrowerDetails", "key :"
					+ key);
			String memBorrowerId = (String) approveFlags.get(key);

			Log.log(Log.INFO, "GMAction", "approveBorrowerDetails",
					"memBorrowerId :" + memBorrowerId);
			int index = memBorrowerId.indexOf("-");
			String memId = memBorrowerId.substring(0, index);
			String bid = memBorrowerId.substring(index + 1,
					memBorrowerId.length());

			if (bid == null || bid.equals("")) {
				ClaimsProcessor cpProcessor = new ClaimsProcessor();
				ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(memId);

				for (int i = 0; i < borrowerIds.size(); i++) {
					bid = (String) borrowerIds.get(i);
					BorrowerDetails borrowerDetails = gmProcessor
							.viewBorrowerDetails(memId, bid, 0);
					int borrowerRefNo = borrowerDetails.getSsiDetails()
							.getBorrowerRefNo();
					gmProcessor.approveBorrowerDetails(borrowerRefNo);
				}
				request.setAttribute("message",
						"Borrower Details have been approved Successfully");
			} else {
				BorrowerDetails borrowerDetails = gmProcessor
						.viewBorrowerDetails(memId, bid, 0);
				int borrowerRefNo = borrowerDetails.getSsiDetails()
						.getBorrowerRefNo();
				gmProcessor.approveBorrowerDetails(borrowerRefNo);
				request.setAttribute("message",
						"Borrower Details have been approved Successfully");
			}

		}

		Log.log(Log.INFO, "GMAction", "approveBorrowerDetails", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showMemberForViewCgpan(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GMProcessor gmProcessor = new GMProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String brnchId = user.getBranchId();
		String forwardPage = "";
		if (bankId.equals("0000")) {
			dynaForm.initialize(mapping);
			forwardPage = "showMember";
		} else {
			ArrayList cgpanMapping = gmProcessor.getCgpanMapping(bankId
					+ zoneId + brnchId);
			dynaForm.set("cgpanMapping", cgpanMapping);
			forwardPage = "showCgpan";
		}
		return mapping.findForward(forwardPage);
	}

	public ActionForward viewCgpanMapping(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GMProcessor gmProcessor = new GMProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;
		String memberId = (String) dynaForm.get("memberId");

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector members = cpProcessor.getAllMemberIds();
		if (!members.contains(memberId)) {
			throw new NoMemberFoundException("Member Id does not exist.");
		}

		ArrayList cgpanMapping = gmProcessor.getCgpanMapping(memberId);
		dynaForm.set("cgpanMapping", cgpanMapping);

		return mapping.findForward("showCgpan");
	}

	public ActionForward showRecoveryForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRecoveryForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");

		ArrayList recoveryDetails = gmProcessor
				.getRecoveryForApproval(borrowerId);

		dynaForm.set("recoveryDetails", recoveryDetails);

		Log.log(Log.INFO, "GMAction", "showRecoveryForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showRepaySchForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaySchForApproval", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		GMProcessor gmProcessor = new GMProcessor();

		String borrowerId = (String) dynaForm.get("borrowerId");
		String memberId = (String) dynaForm.get("memberId");

		ArrayList repaySchDetails = gmProcessor.getRepayScheduleForApproval(
				borrowerId, memberId);

		dynaForm.set("repayScheduleDetails", repaySchDetails);

		Log.log(Log.INFO, "GMAction", "showRepaySchForApproval", "Exited");
		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * 
	 * This method gets the disbursement details for the selected borrower or
	 * cgpan
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showDisbursementDetailsForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsForName",
				"Entered");

		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		gmActionForm.getDisbursementAmount().clear();
		gmActionForm.getDisbursementDate().clear();
		gmActionForm.getFinalDisbursement().clear();
		int type = 2;

		String forward = "";

		String bidName = request.getParameter("bidName");

		String Bid = "";
		String ssiName = "";

		// System.out.println("substring :" + bidName.substring(0,4));

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);

			gmActionForm.setBorrowerId(Bid);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);

			gmActionForm.setBorrowerId(ssiName);
		}

		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Disbursement Details cannot be modified for this borrower since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsForName",
				"bid from request :" + Bid);

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsForName",
				"ssiName from request :" + ssiName);

		// gmActionForm.setBorrowerId(Bid);

		periodicInfoDetails = gmProcessor
				.viewDisbursementDetails(ssiName, type);
		if (periodicInfoDetails.isEmpty()) {
			Log.log(Log.DEBUG, "GMAction", "showDisbursementDetails", "No Data");
			throw new NoDataException(
					"There are no Disbursement Details for the combination of inputs");
		}

		gmActionForm.setDisbPeriodicInfoDetails(periodicInfoDetails);

		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "showDisbursementDetailsForName",
				"Exited");

		// memberids = null;
		// borrowerIds = null;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the disbursement details for the selected borrower or
	 * cgpan
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showOutstandingDetailsForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsForName",
				"Entered");

		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();

		String forward = "";
		String Bid = "";
		String ssiName = "";

		int type = 2;

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);

			gmActionForm.setBorrowerId(Bid);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);

			gmActionForm.setBorrowerId(ssiName);
		}

		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Outstanding Details for this borrower since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsForName",
				"bid from request :" + Bid);

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsForName",
				"ssiName from request :" + ssiName);

		// gmActionForm.setBorrowerId(Bid);

		periodicInfoDetails = gmProcessor.viewOutstandingDetails(ssiName, type);
		if (periodicInfoDetails.isEmpty()) {
			Log.log(Log.DEBUG, "GMAction", "showOutstandingDetailsForName",
					"No Data");
			throw new NoDataException(
					"There are no Disbursement Details for the combination of inputs");
		}

		gmActionForm.setOsPeriodicInfoDetails(periodicInfoDetails);

		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "showOutstandingDetailsForName", "Exited");

		// memberids = null;
		// borrowerIds = null;
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the repayment details for he selected borrower
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRepaymentDetailsForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsForName", "Entered");
		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		String memberId = gmActionForm.getMemberId();
		String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		String borrowerName = gmActionForm.getBorrowerName();
		gmActionForm.getRepaymentAmount().clear();
		gmActionForm.getRepaymentDate().clear();
		// gmActionForm.getRepaymentId().clear();
		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		int type = 2;
		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);

			gmActionForm.setBorrowerId(Bid);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);

			gmActionForm.setBorrowerId(ssiName);
		}

		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Repayment Details cannot be modified for this borrower since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsForName",
				"bid from request :" + Bid);

		// String ssiName = bidName.substring(10,bidName.length()-1);

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsForName",
				"ssiName from request :" + ssiName);

		// gmActionForm.setBorrowerId(Bid);
		periodicInfoDetails = gmProcessor.viewRepaymentDetails(ssiName, type);

		if (periodicInfoDetails == null) {
			throw new NoDataException(
					"There are no Repayment Details for this " + borrowerName);
		}

		// Setting the object
		gmActionForm.setRepayPeriodicInfoDetails(periodicInfoDetails);
		gmActionForm.setRepayPeriodicInfoDetailsTemp(periodicInfoDetails);

		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "showRepaymentDetailsForName", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	/**
	 * 
	 * This method gets the repayment details for he selected borrower
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showRepaymentScheduleForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showRepaymentScheduleForName", "Entered");
		ArrayList periodicInfoDetails = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		String forward = "";

		RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
		ArrayList repaymentSchedules = new ArrayList();

		String memberId = gmActionForm.getMemberIdForSchedule();
		session.setAttribute("scheduleMemberId", memberId);
		Log.log(Log.DEBUG, "GMAction", "showRepaymentSchedule", "mem id -->"
				+ memberId);

		String cgpan = (gmActionForm.getCgpanForSchedule()).toUpperCase();
		String borrowerId = (gmActionForm.getBorrowerIdForSchedule())
				.toUpperCase();
		String borrowerName = gmActionForm.getBorrowerNameForSchedule();

		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		int type = 2;
		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);

			gmActionForm.setBorrowerId(Bid);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);

			gmActionForm.setBorrowerId(ssiName);
		}
		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Repayment Schedule Details cannot be modified for this borrower since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showRepaymentScheduleForName",
				"bid from request :" + Bid);

		// String ssiName = bidName.substring(10,bidName.length()-1);

		Log.log(Log.INFO, "GMAction", "showRepaymentScheduleForName",
				"ssiName from request :" + ssiName);

		// gmActionForm.setBorrowerId(Bid);
		repaymentSchedules = gmProcessor.viewRepaymentSchedule(ssiName, type);

		// Setting the object
		gmActionForm.setRepaymentSchedules(repaymentSchedules);

		if (repaymentSchedules == null || repaymentSchedules.size() == 0) {
			throw new NoDataException(
					"There are no Repayment Details for this " + borrowerName);

		}

		forward = "forwardPage";

		gmActionForm.setBorrowerName("");

		Log.log(Log.INFO, "GMAction", "showRepaymentScheduleForName", "Exited");
		return mapping.findForward(forward);

	}

	/**
	 * 
	 * This method gets the closure details for he selected borrower
	 * 
	 * @param mapping
	 *            The ActionMapping object
	 * @param form
	 *            The ActionForm object
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServeltResponse object
	 * @return ActionForward The ActionForward object
	 * @throws Exception
	 *             If any error occurs, Exception would be thrown.
	 */
	public ActionForward showClosureDetailsForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showClosureDetailsForName", "Entered");
		ClosureDetail closureDtl = new ClosureDetail();
		HashMap closureDetails = null;

		GMActionForm gmActionForm = (GMActionForm) form;
		HttpSession session = request.getSession(false);

		String forward = "";

		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		String borrowerId = gmActionForm.getBorrowerIdForClosure()
				.toUpperCase();
		String memberId = gmActionForm.getMemberIdForClosure();
		// System.out.println("memberId : "+memberId);
		gmActionForm.setMemberIdForClosure(memberId);
		session.setAttribute("closureMemberId", memberId);
		String borrowerName = gmActionForm.getBorrowerNameForClosure();

		GMProcessor gmProcessor = new GMProcessor();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		int type = 2;
		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);

			gmActionForm.setBorrowerId(Bid);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);

			gmActionForm.setBorrowerId(ssiName);
		}
		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Application cannot be Closed for this borrower since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showClosureDetailsForName",
				"bid from request :" + Bid);

		// String ssiName = bidName.substring(10,bidName.length()-1);

		Log.log(Log.INFO, "GMAction", "showClosureDetailsForName",
				"ssiName from request :" + ssiName);

		// gmActionForm.setBorrowerId(Bid);

		closureDetails = gmProcessor
				.viewClosureDetails(ssiName, type, memberId);

		if (closureDetails.isEmpty()) {
			throw new NoDataException(
					"There are no Closure Details for the Entered ID");
		}

		ArrayList reasons = gmProcessor.getAllReasonsForClosure();
		gmActionForm.setClosureReasons(reasons);
		gmActionForm.setClosureDetails(closureDetails);

		forward = "forwardPage";

		Log.log(Log.INFO, "GMAction", "showRepaymentScheduleForName", "Exited");
		return mapping.findForward(forward);

	}

	public ActionForward modifyBorrowerDetailsForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails", "Entered");

		DynaActionForm dynaActionForm = (DynaActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		BorrowerDetails borrowerDetails = null;
		SSIDetails ssiDetails = null;
		Administrator admin = new Administrator();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		ArrayList states = null;
		ArrayList districts = null;
		String state = "";

		String forward = "";
		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			Bid = bidName.substring(0, 9);
			ssiName = bidName.substring(10, bidName.length() - 1);
		} else {

			Bid = "";
			ssiName = bidName.substring(5, bidName.length() - 1);
		}

		int claimCount = appProcessor.getClaimCount(Bid);
		if (claimCount > 0) {
			throw new MessageException(
					"Borrower Details for this borrower cannot be modified since Claim Application has been submitted");
		}

		Log.log(Log.INFO, "GMAction", "showClosureDetailsForName",
				"bid from request :" + Bid);

		// String ssiName = bidName.substring(10,bidName.length()-1);

		states = admin.getAllStates();
		dynaActionForm.set("states", states);

		String memberId = ((String) dynaActionForm
				.get("memberIdForModifyBorrDtl")).toUpperCase();
		if (Bid.equals("")) {
			int type = 2;
			borrowerDetails = gmProcessor.viewBorrowerDetails(memberId,
					ssiName, type);
		} else {

			int type = 0;

			borrowerDetails = gmProcessor.viewBorrowerDetails(memberId, Bid,
					type);
		}

		int borrowerRefNo = borrowerDetails.getSsiDetails().getBorrowerRefNo();

		Integer intRefNo = new Integer(borrowerRefNo);
		dynaActionForm.set("borrowerRefNo", intRefNo);

		ssiDetails = borrowerDetails.getSsiDetails();

		BeanUtils.copyProperties(dynaActionForm, ssiDetails);
		BeanUtils.copyProperties(dynaActionForm, borrowerDetails);

		state = ssiDetails.getState();
		Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails", "state "
				+ state);

		ArrayList districtList = admin.getAllDistricts(state);
		dynaActionForm.set("districts", districtList);

		String districtName = ssiDetails.getDistrict();
		Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails", "districtName "
				+ districtName);

		if (districtList.contains(districtName)) {
			Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
					"setting in dyna form districtName " + districtName);
			dynaActionForm.set("district", districtName);
		} else {
			Log.log(Log.DEBUG, "GMAction", "modifyBorrowerDetails",
					"districtName " + districtName);
			dynaActionForm.set("districtOthers", districtName);
			dynaActionForm.set("district", "Others");

		}
		String constitutionVal = ssiDetails.getConstitution();
		if (!(constitutionVal.equals("proprietary"))
				&& !(constitutionVal.equals("partnership"))
				&& !(constitutionVal.equals("private"))
				&& !(constitutionVal.equals("public"))) {
			dynaActionForm.set("constitutionOther", constitutionVal);
			dynaActionForm.set("constitution", "Others");
		} else {
			dynaActionForm.set("constitution", constitutionVal);
		}

		// Setting legal ID
		String legalIDString = ssiDetails.getCpLegalID();
		if (legalIDString != null && !(legalIDString.equals(""))) {
			if (!(legalIDString.equals("VoterIdentityCard"))
					&& !(legalIDString.equals("RationCardnumber"))
					&& !(legalIDString.equals("PASSPORT"))
					&& !(legalIDString.equals("Driving License"))) {
				dynaActionForm.set("otherCpLegalID", legalIDString);
				dynaActionForm.set("cpLegalID", "Others");
			} else {
				dynaActionForm.set("cpLegalID", legalIDString);
			}

		}
		ArrayList socialList = getSocialCategory();
		dynaActionForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = admin.getAllIndustryNature();
		dynaActionForm.set("industryNatureList", industryNatureList);

		// ssiDetails = borrowerDetails.getSsiDetails() ;
		String industryNature = ssiDetails.getIndustryNature();

		Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails",
				"industry nature :" + industryNature);

		if (industryNature != null && !(industryNature.equals(""))
				&& !industryNature.equals("OTHERS")) {
			ArrayList industrySectors = admin
					.getIndustrySectors(industryNature);
			dynaActionForm.set("industrySectors", industrySectors);
		} else {
			ArrayList industrySectors = new ArrayList();
			String industrySector = ssiDetails.getIndustrySector();
			industrySectors.add(industrySector);
			dynaActionForm.set("industrySectors", industrySectors);
		}

		Log.log(Log.INFO, "GMAction", "ModifyBorrowerDetails", "Exited");
		// dynaActionForm.initialize(mapping) ;

		states = null;
		districts = null;

		return mapping.findForward("success");

	}

	public ActionForward showNpaForName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.getRecProcedures().clear();
		HttpSession session = request.getSession(false);

		GMProcessor gmProcessor = new GMProcessor();
		String bid = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			bid = bidName.substring(0, 9).toUpperCase();

			gmActionForm.setBorrowerId(bid);
		}

		String memberId = gmActionForm.getMemberId();

		gmActionForm.setBorrowerName("");
		gmActionForm.setMemberId(memberId);
		HashMap inputDetail = new HashMap();
		inputDetail.put("memberId", memberId);
		inputDetail.put("borrowerId", bid);
		// inputDetail.put("cgpan",cgpan);

		session.setAttribute("inputDetail", inputDetail);

		// return showNPADetails(mapping, form, request, response);
		// return mapping.findForward("success");
		return mapping.findForward("npaPage");
	}

	public ActionForward showRecoveryForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();

		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			ssiName = bidName.substring(0, 9);

			gmActionForm.setBorrowerId(ssiName);
		}
		gmActionForm.setBorrowerName("");

		gmActionForm.resetWhenRequired(mapping, request);
		Log.log(Log.INFO, "GMAction", "showRecoveryDetails", "Exited");
		return mapping.findForward("insertRecovery");
	}

	public ActionForward showUpdateRecoveryForName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GMActionForm gmActionForm = (GMActionForm) form;

		HttpSession session = request.getSession(false);

		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();

		String Bid = "";
		String ssiName = "";

		String bidName = request.getParameter("bidName");

		if (!bidName.substring(0, 4).equals("null")) {
			ssiName = bidName.substring(0, 9);

			gmActionForm.setBorrowerId(ssiName);
		}

		ArrayList recoveryDetails = gmProcessor.getRecoveryDetails(ssiName);

		Map recoveryMap = new HashMap();
		String recId = null;
		for (int i = 0; i < recoveryDetails.size(); ++i) {
			Recovery reco = (Recovery) recoveryDetails.get(i);
			recId = reco.getRecoveryNo();
			recoveryMap.put(recId, reco);
		}

		gmActionForm.setRecoveryDetails(recoveryMap);

		return mapping.findForward("updateRecovery");

	}

	// added by upchar@path on 18-06-2013 to show npa details

	public ActionForward showNPADetailsNewOld(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("---entered in showNPADetails------");
		Log.log(Log.INFO, "GMAction", "showNPADetails", "Entered");

		GMActionForm gmActionForm = (GMActionForm) form;
		gmActionForm.getRecProcedures().clear();
		GMProcessor gmProcessor = new GMProcessor();

		// Validating the member Id
		String memberId = gmActionForm.getMemberId();

		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// validating the borr ids
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		String borrowerId = (gmActionForm.getBorrowerId()).toUpperCase();
		// String cgpan = (gmActionForm.getCgpan()).toUpperCase();
		String cgpan = (gmActionForm.getCgpan());
		String borrowerName = gmActionForm.getBorrowerName();
		// String borrowerName =

		int type;

		// gmActionForm.resetWhenRequired(mapping,request);

		HttpSession session = request.getSession(false);

		if ((borrowerId != null) && (!borrowerId.equals(""))) {
			if (!(borrowerIds.contains(borrowerId))) {
				gmActionForm.setBorrowerId("");
				throw new NoDataException(borrowerId
						+ " is not among the borrower"
						+ " Ids for the Member Id :" + memberId
						+ ". Please enter correct"
						+ " Member Id and Borrower Id.");
			}

		} else if ((cgpan != null) && (!cgpan.equals(""))) {
			type = 1;

			borrowerId = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "showOutstandingDetails",
					" Bid For Pan - " + borrowerId);
			if (!(borrowerIds.contains(borrowerId))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
			gmActionForm.setBorrowerId(borrowerId);

		} else if ((borrowerName != null) && (!borrowerName.equals(""))) {
			type = 2;
			ArrayList bIdForBorrowerName = gmProcessor
					.getBorrowerIdForBorrowerName(borrowerName, memberId);
			if (bIdForBorrowerName == null || bIdForBorrowerName.size() == 0) {
				throw new NoDataException(
						"There are no Borrower Ids for this member");

			} else {
				session.setAttribute("displayFlag", "5");
				gmActionForm.setBorrowerIds(bIdForBorrowerName);
				return mapping.findForward("bidList");
			}

		}

		ArrayList actionTypes = null;

		actionTypes = gmProcessor.getAllActions();

		gmActionForm.setRecTypes(actionTypes);

		NPADetails npaDetails = gmProcessor.getNPADetails(borrowerId);

		/* testing zone */
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		HashMap securityMap = new HashMap();

		conn = DBConnection.getConnection();
		stmt = conn.createStatement();
		rs = stmt
				.executeQuery("SELECT cgpan,ssi_unit_name FROM APPLICATION_DETAIL a,ssi_detail s "
						+ "WHERE s.ssi_reference_number=(SELECT ssi_reference_number FROM SSI_DETAIL WHERE bid='"
						+ borrowerId
						+ "') and a.ssi_reference_number=s.ssi_reference_number");

		while (rs.next()) {

			String cgpantest = rs.getString("cgpan");
			String unitName = rs.getString("ssi_unit_name");

			rs2 = stmt
					.executeQuery("select App_guar_start_date_time,TRM_REPAYMENT_MORATORIUM from Application_detail a,term_loan_detail t where a.App_ref_no=(select app_ref_no from application_detail "
							+ "where cgpan='"
							+ cgpantest
							+ "') And A.App_ref_no=t.App_ref_no");

			CPDAO cp = new CPDAO();
			CallableStatement callableStmt = null;
			String totalValOfLand = null;
			String totalValOfBuilding = null;
			String totalValOfMachine = null;
			String totalValOfOFMA = null;
			String totalValOfCurrAssets = null;
			String totalValOfOthers = null;

			if (cgpantest != null) {
				String appRefNumber = cp.getAppRefNumber(cgpantest);
				if (appRefNumber != null) {
					callableStmt = conn
							.prepareCall("{?=call packGetPrimarySecurity.funcGetPriSecforAppRef(?,?,?)}");
					callableStmt.setString(2, appRefNumber);
					callableStmt.registerOutParameter(1, 4);
					callableStmt.registerOutParameter(4, 12);
					callableStmt.registerOutParameter(3, -10);
					callableStmt.executeQuery();
					int status = callableStmt.getInt(1);
					if (status == 1) {
						String error = callableStmt.getString(4);
						callableStmt.close();
						callableStmt = null;
						conn.rollback();
						throw new DatabaseException(error);
					}
					ResultSet psResults = null;
					for (psResults = (ResultSet) callableStmt.getObject(3); psResults
							.next();) {
						if (psResults.getString(1).equals("Land"))
							totalValOfLand += psResults.getDouble(3);
						if (psResults.getString(1).equals("Building"))
							totalValOfBuilding += psResults.getDouble(3);
						if (psResults.getString(1).equals("Machinery"))
							totalValOfMachine += psResults.getDouble(3);
						if (psResults.getString(1).equals("Fixed Assets"))
							totalValOfOFMA += psResults.getDouble(3);
						if (psResults.getString(1).equals("Current Assets"))
							totalValOfCurrAssets += psResults.getDouble(3);
						if (psResults.getString(1).equals("Others"))
							totalValOfOthers += psResults.getDouble(3);
					}

					psResults.close();
					psResults = null;
					callableStmt.close();
					callableStmt = null;
				}
			}

			securityMap.put("LAND", String.valueOf(new Double(totalValOfLand)));
			securityMap.put("BUILDING",
					String.valueOf(new Double(totalValOfBuilding)));
			securityMap.put("MACHINE",
					String.valueOf(new Double(totalValOfMachine)));
			securityMap.put("OTHER FIXED MOVABLE ASSETS",
					String.valueOf(new Double(totalValOfOFMA)));
			securityMap.put("CURRENT ASSETS",
					String.valueOf(new Double(totalValOfCurrAssets)));
			securityMap.put("OTHERS",
					String.valueOf(new Double(totalValOfOthers)));

			gmActionForm.setUnitName(unitName);
			gmActionForm.setGuaranteeStartDate(rs2
					.getDate("app_guar_start_date_time"));
			gmActionForm.setMoratoriumperiod(rs2
					.getInt("TRM_REPAYMENT_MORATORIUM"));
			// gmActionForm.setSecurityAsOnDtOfSanction(securityMap);

		}

		/* testing zone */

		if (npaDetails == null) {
			Log.log(Log.DEBUG, "GMAction", "showNPADetails",
					"Npa Details is Null");

			gmActionForm.getRecProcedures().put("key-0",
					new RecoveryProcedureTemp());

			gmActionForm.resetNpaDetailsPage(mapping, request);
			session.setAttribute("npaAvailable", null);

			session.setAttribute("recInitiated", "N");
		}
		if (npaDetails != null) {
			String npId = npaDetails.getNpaId();
			gmActionForm.setNpaId(npId); // ------------------------------------------------npaid
			session.setAttribute("npaAvailable", npId);

			gmActionForm.setNoOfActions(npaDetails.getNoOfActions()); // -------------------noofactions

			CustomisedDate custom = new CustomisedDate();
			custom.setDate(npaDetails.getNpaDate());
			gmActionForm.setNpaDate(custom); // --------------------------------------------npadate
			gmActionForm.setOsAmtOnNPA(npaDetails.getOsAmtOnNPA()); // ---------------------osamount
			gmActionForm.setWhetherNPAReported(npaDetails
					.getWhetherNPAReported()); // -----npareportedflag
			gmActionForm.setEffortsTaken(npaDetails.getEffortsTaken()); // -----------------effortstaken

			CustomisedDate custom1 = new CustomisedDate();
			custom1.setDate(npaDetails.getReportingDate());
			gmActionForm.setReportingDate(custom1); // ------------------------------------reportingdate
			gmActionForm.setNpaReason(npaDetails.getNpaReason()); // ----------------------npareason
			gmActionForm.setWillfulDefaulter(npaDetails.getWillfulDefaulter()); // --------willfuldefaulter

			gmActionForm.setIsRecoveryInitiated(npaDetails
					.getIsRecoveryInitiated()); // --isrecoveryinitiatedflag
			if (gmActionForm.getIsRecoveryInitiated().equals("Y")) {
				session.setAttribute("recInitiated", "Y");
			} else {
				session.setAttribute("recInitiated", "N");
			}

			CustomisedDate custom2 = new CustomisedDate();
			custom2.setDate(npaDetails.getEffortsConclusionDate());
			gmActionForm.setEffortsConclusionDate(custom2); // -----------------------------effortconclusiondate

			gmActionForm.setMliCommentOnFinPosition(npaDetails
					.getMliCommentOnFinPosition()); // ------mlicomment
			gmActionForm.setDetailsOfFinAssistance(npaDetails
					.getDetailsOfFinAssistance()); // --------detailsofassistance
			gmActionForm.setCreditSupport(npaDetails.getCreditSupport()); // --------------------------credit
																			// support
			gmActionForm.setBankFacilityDetail(npaDetails
					.getBankFacilityDetail()); // ----------------bankfacility
			gmActionForm.setPlaceUnderWatchList(npaDetails
					.getPlaceUnderWatchList()); // --------------placeunderwatchlist
			gmActionForm.setRemarksOnNpa(npaDetails.getRemarksOnNpa()); // ----------------------------remarks

			LegalSuitDetail legalDetail = npaDetails.getLegalSuitDetail();
			if (legalDetail.getCourtName() != null
					&& !legalDetail.getCourtName().equals("")) {
				if (!legalDetail.getCourtName().equals("Civil Court")
						&& !legalDetail.getCourtName().equals("DRT")
						&& !legalDetail.getCourtName().equals("LokAdalat")
						&& !legalDetail.getCourtName().equals(
								"Revenue Recovery Autority")
						&& !legalDetail.getCourtName().equals(
								"Securitisation Act ")) {
					gmActionForm.setCourtName("others"); // -----------------------------------courtname
					gmActionForm.setInitiatedName(legalDetail.getCourtName());
				} else {

					gmActionForm.setCourtName(legalDetail.getCourtName());
				}

				gmActionForm.setLegalSuitNo(legalDetail.getLegalSuiteNo()); // ------------------------legalsuitnumber
				CustomisedDate custom3 = new CustomisedDate();
				custom3.setDate(legalDetail.getDtOfFilingLegalSuit());
				gmActionForm.setDtOfFilingLegalSuit(custom3); // -------------------------------------dateoffilingsuit

				gmActionForm.setForumName(legalDetail.getForumName()); // ---------------------------forumname
				gmActionForm.setLocation(legalDetail.getLocation()); // -----------------------------location
				gmActionForm.setAmountClaimed(legalDetail.getAmountClaimed()); // -------------------amountclaimed
				gmActionForm.setCurrentStatus(legalDetail.getCurrentStatus()); // -------------------currentstatus
				gmActionForm.setRecoveryProceedingsConcluded(legalDetail
						.getRecoveryProceedingsConcluded()); // ----------recoveryproceedingconcluded
			}
			ArrayList recProcs = new ArrayList();
			recProcs = npaDetails.getRecoveryProcedure();
			if (recProcs != null && recProcs.size() > 0) {
				gmActionForm.setRecoveryProcedures(recProcs); // -------------------------------------recoveryprocedures
			}
			/*
			 * else{ gmActionForm.setRecoveryProcedures(new ArrayList()); }
			 */
			String actype = null;
			String acdtl = null;
			Date acdate = null;
			String file = null;

			Map recProcsMap = new HashMap();

			// Log.log(Log.DEBUG,"GMAction","showNPADetails","size of Recovery Procedure"
			// +recProcs.size()) ;

			CustomisedDate custom4 = null; // new CustomisedDate();

			if ((recProcs != null) && (recProcs.size() > 0)) {
				for (int i = 0; i < recProcs.size(); ++i) {
					RecoveryProcedure rProc = (RecoveryProcedure) recProcs
							.get(i);

					RecoveryProcedureTemp temp = new RecoveryProcedureTemp();

					custom4 = new CustomisedDate();

					custom4.setDate(rProc.getActionDate());
					temp.setActionDate(custom4);
					temp.setActionDetails(rProc.getActionDetails());
					temp.setActionType(rProc.getActionType());

					/*
					 * FormFile file123=new FormFile() { public void
					 * setContentType(String s) {
					 * 
					 * }
					 * 
					 * }; file12.
					 * temp.setAttachmentName(rProc.getAttachmentName());
					 */

					/*
					 * actype = rProc.getActionType() ; acdtl=
					 * rProc.getActionDetails() ; acdate= rProc.getActionDate();
					 * file= rProc.getAttachmentName();
					 * recProcsMap.put("key-"+i,actype);
					 * recProcsMap.put("key-"+i,acdtl);
					 * recProcsMap.put("key-"+i,acdate);
					 * recProcsMap.put("key-"+i,file);
					 */
					gmActionForm.getRecProcedures().put("key-" + i, temp);
				}
				// gmActionForm.setRecProcedures(recProcsMap);
			}
		}
		Log.log(Log.INFO, "GMAction", "showNPADetails", "Exited");

		memberids = null;
		borrowerIds = null;

		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward saveNpaDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GMProcessor gmProcessor = new GMProcessor();
		HttpSession session = request.getSession(false);
		NPADetails npaDetail = new NPADetails();
		DynaValidatorActionForm dynaActionForm = (DynaValidatorActionForm) form;
		String message = "NPA Details Saved Successfully.";
		Integer size = (Integer) dynaActionForm.get("size");
		int total = size.intValue();
		String borrowerId = (String) dynaActionForm.get("borrowerId");
		npaDetail.setCgbid(borrowerId);
		Log.log(4, "GMAction", "saveNpaDetails",
				(new StringBuilder()).append("bid-").append(borrowerId)
						.toString());
		String unitName = (String) dynaActionForm.get("unitName");
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
		String approvedAmount = null;
		Map tcMap = null;
		Map wcMap = null;
		Vector tcVector = new Vector();
		Vector wcVector = new Vector();
		for (int i = 1; i <= total; i++) {
			cgpan = (new StringBuilder()).append("cgpan").append(i).toString();
			outstandingPrincipal = (new StringBuilder())
					.append("outstandingPrincipal").append(i).toString();
			outstandingInterest = (new StringBuilder())
					.append("outstandingInterest").append(i).toString();
			String cgpanNo = (String) dynaActionForm.get(cgpan);
			String loanType = cgpanNo.substring(cgpanNo.length() - 2);
			if ("TC".equals(loanType)) {
				firstDisbDt = (new StringBuilder()).append("firstDisbDt")
						.append(i).toString();
				lastDisbDt = (new StringBuilder()).append("lastDisbDt")
						.append(i).toString();
				firstInstDt = (new StringBuilder()).append("firstInstDt")
						.append(i).toString();
				moratoriumPrincipal = (new StringBuilder())
						.append("moratoriumPrincipal").append(i).toString();
				moratoriumInterest = (new StringBuilder())
						.append("moratoriumInterest").append(i).toString();
				totalDisbAmt = (new StringBuilder()).append("totalDisbAmt")
						.append(i).toString();
				repayPrincipal = (new StringBuilder()).append("repayPrincipal")
						.append(i).toString();
				repayInterest = (new StringBuilder()).append("repayInterest")
						.append(i).toString();
				tcMap = new HashMap();
				tcMap.put("CGPAN", cgpanNo);
				tcMap.put("FIRST_DISB_DT", dynaActionForm.get(firstDisbDt));
				tcMap.put("LAST_DISB_DT", dynaActionForm.get(lastDisbDt));
				tcMap.put("FIRST_INST_DT", dynaActionForm.get(firstInstDt));
				tcMap.put("PRINCIPAL_MORATORIUM",
						dynaActionForm.get(moratoriumPrincipal));
				tcMap.put("INTEREST_MORATORIUM",
						dynaActionForm.get(moratoriumInterest));
				tcMap.put("TOTAL_DISB_AMT", dynaActionForm.get(totalDisbAmt));
				tcMap.put("PRINCIPAL_REPAY", dynaActionForm.get(repayPrincipal));
				tcMap.put("INTEREST_REPAY", dynaActionForm.get(repayInterest));
				tcMap.put("PRINCIPAL_OS",
						dynaActionForm.get(outstandingPrincipal));
				tcMap.put("INTEREST_OS",
						dynaActionForm.get(outstandingInterest));
				tcVector.add(tcMap);
			} else {
				wcMap = new HashMap();
				wcMap.put("CGPAN", cgpanNo);
				wcMap.put("PRINCIPAL_OS",
						dynaActionForm.get(outstandingPrincipal));
				wcMap.put("INTEREST_OS",
						dynaActionForm.get(outstandingInterest));
				wcVector.add(wcMap);
			}
		}

		Date npaTurnDate = (Date) dynaActionForm.get("npaDt");
		npaDetail.setNpaDate(npaTurnDate);
		String isAsPerRBI = (String) dynaActionForm.get("isAsPerRBI");
		npaDetail.setIsAsPerRBI(isAsPerRBI);
		String npaConfirm = (String) dynaActionForm.get("npaConfirm");
		npaDetail.setNpaConfirm(npaConfirm);
		String npaReason = (String) dynaActionForm.get("npaReason");
		npaDetail.setNpaReason(npaReason);
		String effortsTaken = (String) dynaActionForm.get("effortsTaken");
		npaDetail.setEffortsTaken(effortsTaken);
		String isAcctReconstructed = (String) dynaActionForm
				.get("isAcctReconstructed");
		npaDetail.setIsAcctReconstructed(isAcctReconstructed);
		String subsidyFlag = "";
		String isSubsidyRcvd = "";
		String isSubsidyAdjusted = "";
		Date subLastRcvdDt = null;
		Double subLastRcvdAmt = Double.valueOf(0.0D);
		subsidyFlag = (String) dynaActionForm.get("subsidyFlag");
		if (!GenericValidator.isBlankOrNull(subsidyFlag)
				&& "Y".equals(subsidyFlag)) {
			isSubsidyRcvd = (String) dynaActionForm.get("isSubsidyRcvd");
			if (!GenericValidator.isBlankOrNull(isSubsidyRcvd)
					&& "Y".equals(isSubsidyRcvd)) {
				isSubsidyAdjusted = (String) dynaActionForm
						.get("isSubsidyAdjusted");
				if (!GenericValidator.isBlankOrNull(isSubsidyAdjusted)
						&& "Y".equals(isSubsidyAdjusted)) {
					subLastRcvdDt = (Date) dynaActionForm
							.get("subsidyLastRcvdDt");
					subLastRcvdAmt = (Double) dynaActionForm
							.get("subsidyLastRcvdAmt");
				}
			}
		}
		npaDetail.setSubsidyFlag(subsidyFlag);
		npaDetail.setIsSubsidyRcvd(isSubsidyRcvd);
		npaDetail.setIsSubsidyAdjusted(isSubsidyAdjusted);
		npaDetail.setSubsidyLastRcvdAmt(subLastRcvdAmt.doubleValue());
		npaDetail.setSubsidyLastRcvdDt(subLastRcvdDt);
		Date lastinspectionDt = (Date) dynaActionForm.get("lastInspectionDt");
		npaDetail.setLastInspectionDt(lastinspectionDt);
		Map securityMap = new HashMap();
		Map securityAsOnSancDt = null;
		Map securityAsOnNpaDt = null;
		Double networthAsOnSancDt = Double.valueOf(0.0D);
		Double networthAsOnNpaDt = Double.valueOf(0.0D);
		String reasonForReductionAsOnNpaDt = "";
		securityAsOnSancDt = (Map) dynaActionForm.get("securityAsOnSancDt");
		securityAsOnNpaDt = (Map) dynaActionForm.get("securityAsOnNpaDt");
		networthAsOnSancDt = (Double) dynaActionForm.get("networthAsOnSancDt");
		networthAsOnNpaDt = (Double) dynaActionForm.get("networthAsOnNpaDt");
		reasonForReductionAsOnNpaDt = (String) dynaActionForm
				.get("reasonForReductionAsOnNpaDt");
		securityMap.put("securityAsOnSancDt", securityAsOnSancDt);
		securityMap.put("securityAsOnNpaDt", securityAsOnNpaDt);
		securityMap.put("networthAsOnSancDt", networthAsOnSancDt);
		securityMap.put("networthAsOnNpaDt", networthAsOnNpaDt);
		securityMap.put("reasonForReductionAsOnNpaDt",
				reasonForReductionAsOnNpaDt);
		NPADetails npaDetailsFromDB = gmProcessor.getNPADetails(borrowerId);
		String npaId = null;
		if (npaDetailsFromDB != null) {
			npaId = npaDetailsFromDB.getNpaId();
			dynaActionForm.set("npaId", npaId);
			npaDetail.setNpaId(npaId);
		}
		if (npaDetailsFromDB == null) {
			String srcMenu = (String) session.getAttribute("mainMenu");
			String srcSubMenu = (String) session.getAttribute("subMenuItem");
			if (srcMenu != null
					&& srcSubMenu != null
					&& srcMenu.equals(MenuOptions.getMenu("CP_CLAIM_FOR"))
					&& srcSubMenu.equals(MenuOptions
							.getMenu("CP_CLAIM_FOR_FIRST_INSTALLMENT"))) {
				gmProcessor.insertNPADetails(npaDetail, tcVector, wcVector,
						securityMap);
				srcMenu = null;
				srcSubMenu = null;
				Log.log(4, "GMAction", "saveNpaDetails for Claim Application",
						"Exited");
				return mapping.findForward("claimdetails");
			}
			Log.log(4, "GMAction", "saveNpaDetails", "New NPA Details is Saved");
			gmProcessor.insertNPADetails(npaDetail, tcVector, wcVector,
					securityMap);
		} else {
			Log.log(4, "GMAction", "saveNpaDetails",
					"Modified NPA Details is Saved");
			gmProcessor.updateNPADetails(npaDetail, tcVector, wcVector,
					securityMap);
		}
		request.setAttribute("message", message);
		Log.log(4, "GMAction", "saveNpaDetails", "Exited");
		return mapping.findForward("success");
	}

	// added by vinod@path 28-jan-2016
	public ActionForward showEmpDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(5, "GMAction", "showApprRegistrationForm", "Entered");
		HttpSession session = request.getSession(false);
		GMActionForm ApprovalForm = (GMActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		// System.out.println("GMA memberId : "+memberId);
		ArrayList formList = displayNpaRegistrationFormList(memberId);

		String forward = "";
		if (formList == null || formList.size() == 0) {
			request.setAttribute("message",
					"No Applications Available For Approval");
			forward = "success";
		} else {
			ApprovalForm.setNpaRegistFormList(formList);
			forward = "npaRegistList";
		}

		Log.log(5, "GMAction", "showApprRegistrationForm", "Exited");
		return mapping.findForward(forward);
	}

	public ArrayList displayNpaRegistrationFormList(String memberId)
			throws DatabaseException {
		Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Entered");
		//System.out.println("GMA displayNpaRegistrationFormList S : " + memberId);
		ArrayList NpaRegistList = new ArrayList();
		GMActionForm npaRegistForm = null;
		Connection connection = DBConnection.getConnection(false);

		ResultSet rs = null;
		Statement stmt = null;
		String npaRegistQuery = "SELECT  MI.MEM_BNK_ID || MI.MEM_ZNE_ID || MI.MEM_BRN_ID, MEM_ZONE_NAME, USR_FIRST_NAME, USR_MIDDLE_NAME, USR_LAST_NAME, USR_EMP_ID, USR_DSG_NAME, USR_PHONE_NO, USR_EMAIL_ID,MEM_BANK_NAME, MLI_CHECKER_ID"
				+ " FROM member_info mi, mli_checker_info mci"
				+ " WHERE mi.mem_bnk_id || mi.mem_zne_id || mi.mem_brn_id = mci.mem_bnk_id || mci.mem_zne_id || mci.mem_brn_id"
				+ " AND MCI.USR_STATUS = 'MA' AND mi.MEM_STATUS = 'A' ORDER BY MEM_BANK_NAME,MI.MEM_BNK_ID || MI.MEM_ZNE_ID || MI.MEM_BRN_ID  ";
		// " AND mci.mem_bnk_id || mci.mem_zne_id || mci.mem_brn_id = ?";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery((npaRegistQuery));
			// pst.setString(1, "001900010000");
			//System.out.println("GMA npaRegistQuery : " + npaRegistQuery);
			// rs = pst.executeQuery();
			while (rs.next()) {
				npaRegistForm = new GMActionForm();
				npaRegistForm.setMemberId(rs.getString(1));
				// System.out.println("GMA MLI Id : "+rs.getString(1));

				npaRegistForm.setZoneName(rs.getString(2));
				// System.out.println("GMA Zone Name : "+rs.getString(2));

				npaRegistForm.setEmpFName(rs.getString(3));
				// System.out.println("GMA Fname : "+rs.getString(3));

				npaRegistForm.setEmpMName(rs.getString(4));
				// System.out.println("GMA Mname : "+rs.getString(4));

				npaRegistForm.setEmpLName(rs.getString(5));
				// System.out.println("GMA Lname : "+rs.getString(5));

				npaRegistForm.setEmpId(rs.getString(6));
				// System.out.println("GMA EID : "+rs.getString(6));

				npaRegistForm.setDesignation(rs.getString(7));
				// System.out.println("GMA Designation : "+rs.getString(7));

				npaRegistForm.setPhoneNo(rs.getString(8));
				// System.out.println("GMA Phone No : "+rs.getString(8));

				npaRegistForm.setEmailId(rs.getString(9));
				
				npaRegistForm.setBankName(rs.getString(10));
				// System.out.println("GMA EMAIL_ID : "+rs.getString(9));

				npaRegistForm.setCheckerId(rs.getString(11));
				// System.out.println("GMA checker ID : "+rs.getString(10));

				NpaRegistList.add(npaRegistForm);
			}
			connection.commit();
			stmt.close();
			stmt = null;
			rs.close();
			rs = null;
		} catch (Exception sql) {
			sql.printStackTrace();
		} finally {
			DBConnection.freeConnection(connection);
		}
		//System.out.println("GMA displayNpaRegistrationFormList E");
		Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Exited");
		return NpaRegistList;
	}
	//Diksha
	/*public ActionForward showApprRegistrationFormSubmit(ActionMapping mapping,*/
	public ActionForward showApprRegistrationFormSubmitOLD(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Entered");
		HttpSession session = request.getSession(false);
		// System.out.println("GMA showApprRegistrationFormSubmit start");
		Connection connection = DBConnection.getConnection(false);
		// PreparedStatement prepareStatement = null;
		Statement statement = null;

		connection.setAutoCommit(false);

		ResultSet rs = null;
		GMActionForm gmActionForm = (GMActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		// System.out.println("GMA memberId : "+memberId);

		String actionType = request.getParameter("action");
		// System.out.println("GMA actionType : "+actionType);

		Map commentIdVal = gmActionForm.getEmpComments();
		//System.out.println("GMA commentIdVal : " + commentIdVal);

		String checkId[] = gmActionForm.getCheck();
		//System.out.println("GMA checkId : " + checkId.length);

		// System.out.println("GMA checkId : "+checkId[1]);

		String chSelectId = "";
		String commVal = "";
		String query = "";
		AdminHelper adminHelper = new AdminHelper();
		
		 boolean mailssend=false;
		// koteswar start generate passwore
		/*
		 * PasswordManager passwordManager=new PasswordManager(); String
		 * encryptedPassword= adminHelper.generatePassword(); String
		 * password=passwordManager.decryptPassword(encryptedPassword);
		 * System.out.println("GMA password : "+password);
		 * 
		 * String subject = "User Password Generation" ; String mailBody =
		 * "Dear User This is Your  Password geneated  from CGTMSE+'"
		 * +password+"' ";
		 */
		String filePath = "";

		String emailid = "";

		String mliUsrId = "";
		
		String mlibankid = "";
		
		
		boolean sendMailStatus = true;
		// user.setPassword(encryptedPassword);
		// koteswar end

		try {
			if (actionType.equals("update")) {

				statement = connection.createStatement();
				for (int i = 0; i < checkId.length; i++) {
					chSelectId = checkId[i];
					commVal = (String) commentIdVal.get(chSelectId);
					//System.out.println("GMA chSelectId : " + chSelectId
						//	+ "\t commVal : " + commVal);

					PasswordManager passwordManager = new PasswordManager();
					String encryptedPassword = adminHelper.generatePassword();
				//	System.out.println("GMA encryptedPassword : "
							//+ encryptedPassword);
					String password = passwordManager
							.decryptPassword(encryptedPassword);
				//	System.out.println("GMA password : " + password);

					// koteswar new
					String userId = "";
					String usrIdqry = "select distinct usr_id from  mli_checker_info where  MLI_CHECKER_ID='"
							+ chSelectId + "'";
					// System.out.println("GMA emailQry : "+emailQry);
					rs = statement.executeQuery(usrIdqry);
					while (rs.next()) {
						usrIdqry = rs.getString(1);

						// System.out.println("GMA USR_EMAIL_ID : "+emailid+
						// "\t USR_ID : "+mliUsrId);
					}

					// koteswar end

					String subject = "User Password Generation from CGTMSE(New User ID)";
					// String mailBody =
					// "Dear User, \n \n This is Your Password for the new User Id :"+usrIdqry+" has been generated from CGTMSE. \n"
					// +
					// " Your Password is : "+password+" '\n \n Regards, \n CGTMSE.";

					String mailBody = "Dear User, \n \n Congrats,Your Password has been generated from CGTMSE,corresponding to your Checker User Id.Please find the below details."
						+ " \n "
							+ "Transaction Id :"
							+ usrIdqry
							+ " \n "
							+ "Password :"
							+ password
							+ "\n Please note that this Id and  Password is being given to you for approval of the transaction, as a Checker \n submitted by  your operating leval officer and this Id/Password must not be shared with anybody." +
									" \n" +
									"you may login the CGTMSE portal by using the Checker User Id for approving the transaction.\n While first time Login  using the User Checker Id/Password you must change the Password,hint question and hint answer.     \n \n Regards, \n CGTMSE.   ";
					// " Your Password is : "+password+" '\n \n Regards, \n CGTMSE.";
				//	System.out.println("GMA mailBody : " + mailBody);

					// insert into user_info table from mli_checker_info table S

					String queryuserinfo = " INSERT INTO user_info(usr_id, usr_first_name, usr_middle_name, usr_last_name, usr_dsg_name, mem_bnk_id, mem_zne_id, mem_brn_id,"
							+ " usr_email_id, usr_password, usr_status, usr_pwd_changed_on, usr_first_login, usr_unsuccessful_login_atmpts, usr_last_login_dt,"
							+ " usr_created_modified_by, usr_created_modified_dt,USR_HINT_QUESTION,USR_HINT_ANSWER)"
							+ " (select  usr_id, usr_first_name, usr_middle_name, usr_last_name, usr_dsg_name, mem_bnk_id, mem_zne_id, mem_brn_id,"
							+ " usr_email_id, '"
							+ encryptedPassword
							+ "', 'A', usr_pwd_changed_on, usr_first_login,  usr_unsuccessful_login_atmpts, usr_last_login_dt,"
							+ " '"+user.getUserId()+"', to_char(sysdate),USR_HINT_QUESTION,USR_HINT_ANSWER"
							+ " from mli_checker_info"
							+ " WHERE MLI_CHECKER_ID = '" + chSelectId + "')";
					
					// System.out.println("GMA queryuserinfo : "+queryuserinfo);

					int resuserinfo = statement.executeUpdate(queryuserinfo);

					query = " INSERT INTO user_info@repuser(usr_id, usr_first_name, usr_middle_name, usr_last_name, usr_dsg_name, mem_bnk_id, mem_zne_id, mem_brn_id,"
							+ " usr_email_id, usr_password, usr_status, usr_pwd_changed_on, usr_first_login, usr_unsuccessful_login_atmpts, usr_last_login_dt,"
							+ " usr_created_modified_by, usr_created_modified_dt,USR_HINT_QUESTION,USR_HINT_ANSWER)"
							+ " (select  usr_id, usr_first_name, usr_middle_name, usr_last_name, usr_dsg_name, mem_bnk_id, mem_zne_id, mem_brn_id,"
							+ " usr_email_id, '"
							+ encryptedPassword
							+ "', 'A', usr_pwd_changed_on, usr_first_login,  usr_unsuccessful_login_atmpts, usr_last_login_dt,"
							+ " '"+user.getUserId()+"', to_char(sysdate),USR_HINT_QUESTION,USR_HINT_ANSWER"
							+ " from mli_checker_info"
							+ " WHERE MLI_CHECKER_ID = '" + chSelectId + "')";
					
				//	 System.out.println("GMA query : "+query);

					int res = statement.executeUpdate(query);

					connection.commit();

					// update mli_checker_info with status CU S
					String updateQuery = "UPDATE mli_checker_info SET usr_status = 'CA' WHERE mli_checker_id = '"
							+ chSelectId + "'";
					// System.out.println("GMA updateQuery : "+updateQuery);
					statement.executeUpdate(updateQuery);

					// update mli_checker_info with status CU E

					// if (res > 0) {
					// connection.commit();

					String emailQry = "select distinct USR_EMAIL_ID,USR_ID from mli_checker_info where  MLI_CHECKER_ID='"
							+ chSelectId + "'";
					// System.out.println("GMA emailQry : "+emailQry);
					rs = statement.executeQuery(emailQry);
					while (rs.next()) {
						emailid = rs.getString(1);
						mliUsrId = rs.getString(2);
						//System.out.println("GMA USR_EMAIL_ID : " + emailid
							//	+ "\t USR_ID : " + mliUsrId);
					}
					// {

					// koteswr

					User creatingUser = getUserInformation(request);
					String createdBy = creatingUser.getUserId();
					// Assign OO Role to the MLI user.

					AdminDAO adminDAO = new AdminDAO();
					ArrayList privileges = getPrivilegesForRole("MLI CHECKER");
					ArrayList noRoles = new ArrayList();
					noRoles.add("MLI CHECKER");

					// int roleid=94;

					int roleid = 0;

					String getrolidQry = "select distinct rol_id  from role_master  where  LTRIM(RTRIM(UPPER(rol_name)))='MLI CHECKER' ";

				//	 System.out.println(getrolidQry);

					// System.out.println("GMA emailQry : "+emailQry);
					rs = statement.executeQuery(getrolidQry);

					while (rs.next()) {
						roleid = rs.getInt(1);

						String queryroles = "INSERT INTO user_role@repuser(usr_id, rol_id, url_flag, url_assign_dt) VALUES "
								+ "('"
								+ mliUsrId
								+ "','"
								+ roleid
								+ "','A',SYSDATE) ";
						 System.out.println(queryroles);

						int res1 = statement.executeUpdate(queryroles);
						// System.out.println(res1);

					}

					// for (int k = 0; i < privileges.size(); i++)
					// {

					String privilege = (String) privileges.get(0);

					String getrolidQry1 = "SELECT prv_id FROM privilege_master WHERE LTRIM(RTRIM(UPPER(prv_name)))"
							+ "= '" + privilege + "' ";
				//	 System.out.println(getrolidQry1);

					// System.out.println("GMA emailQry : "+emailQry);
					rs = statement.executeQuery(getrolidQry1);
					while (rs.next()) {
						int prvid = rs.getInt(1);

						String querypriviliges1 = "INSERT INTO user_privilege@repuser(usr_id, prv_id, upr_flag, upr_assign_dt) VALUES "
								+ "('"
								+ mliUsrId
								+ "','"
								+ prvid
								+ "','A',SYSDATE)";

					//	System.out.println(querypriviliges1);

						int res3 = statement.executeUpdate(querypriviliges1);
					}

					// System.out.println(res3);

					// }

					// }

					// adminDAO.assignRolesAndPrivileges(noRoles,privileges,mliUsrId,createdBy);

					// Clear the memory.
					noRoles.clear();
					privileges.clear();

					noRoles = null;
					privileges = null;

					// koteswar

					String host = "192.168.10.118";
					boolean sessionDebug = false;
					Properties props = System.getProperties();
					props.put("mail.host", host);
					props.put("mail.transport.protocol", "smtp");
					props.put("mail.smtp.host", host);
					props.put("mail.from", "support@cgtmse.in");

					Session session1 = null;
					session1 = Session.getDefaultInstance(props, null);
					session1.setDebug(sessionDebug);

					javax.mail.internet.MimeMessage msg = new javax.mail.internet.MimeMessage(
							session1);
					msg.setFrom(new javax.mail.internet.InternetAddress(
							"support@cgtmse.in"));
					System.out.println("GMA emailid send mail : " + emailid);
					javax.mail.internet.InternetAddress[] Toaddress = { new javax.mail.internet.InternetAddress(
							emailid) };
					msg.setRecipients(javax.mail.Message.RecipientType.TO,
							Toaddress);
					msg.setSubject(subject);
					msg.setSentDate(new Date());

					if (!filePath.equals("")) {
						BodyPart messageBodyPart = new MimeBodyPart();
						messageBodyPart.setText(mailBody);
						Multipart multipart = new MimeMultipart();
						multipart.addBodyPart(messageBodyPart);
						messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(filePath);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(filePath);
						multipart.addBodyPart(messageBodyPart);
						msg.setContent(multipart);
					} else {
						System.out.println("GMA else : " + mailBody);
						msg.setText(mailBody);
					}
					System.out.println(" BEFORE  send mail : " + emailid);
					Transport.send(msg);
					System.out.println("AFTER emailid send mail : " + emailid);
					// }// end while

					// }
				}
			}
			
			

			// end if(res>0)
			// request.setAttribute("message",
			// "<b>The Request For Submission Completed.</b>");

			// if(actionType.equals("update"))
			else if (actionType.equals("delete")) {
				
				statement = connection.createStatement();
				for (int i = 0; i < checkId.length; i++) {
					chSelectId = checkId[i];
					commVal = (String) commentIdVal.get(chSelectId);
					//System.out.println("GMA chSelectId : " + chSelectId
						//	+ "\t commVal : " + commVal);
					
					 ArrayList emailids=new  ArrayList();
					

					String emailQry = "select distinct USR_EMAIL_ID,mem_bnk_id  from mli_checker_info where  MLI_CHECKER_ID='"
							+ chSelectId + "'";
					// System.out.println("GMA emailQry : "+emailQry);
					rs = statement.executeQuery(emailQry);
					while (rs.next()) {
						emailid = rs.getString(1);
						mlibankid=rs.getString(2);
						
					
					}
					
					String emailHOQry="select distinct MEM_EMAIL from member_info  where  mem_bnk_id='"+mlibankid+"' and mem_zne_id='0000'  " +
					" and mem_brn_id='0000'   and mem_status='A' ";
		
			rs=statement.executeQuery(emailHOQry); 
			while(rs.next())
			{
				mliUsrId = rs.getString(1);
			}		
			
			
			emailids.add(emailid);
			emailids.add(mliUsrId);
		
			

			String subject = "Checker User Id Rejection from CGTMSE";
			// String mailBody =
			// "Dear User, \n \n This is Your Password for the new User Id :"+usrIdqry+" has been generated from CGTMSE. \n"
			// +
			// " Your Password is : "+password+" '\n \n Regards, \n CGTMSE.";

			String mailBody = "Dear User, \n \n Sorry,Your Request for Creation of Checker User Id has been Rejected by CGMTSE due to "+commVal+" .\n You are requested to contact your HO and operating officer to resubmit the request.\n"
					+ " \n \n Regards, \n CGTMSE.   ";
			
			 
				  mailssend= sendMailBisenessLogic(subject, mailBody, emailids, filePath);
				 // request.setAttribute("message","<b>The Requested Form Submitted.</b><br>");
				
					
						
				
			//	String Delquery = "UPDATE mli_checker_info SET usr_status = 'CR' WHERE mli_checker_id = '"
					//	+ chSelectId + "'";
					String Delquery = "delete mli_checker_info  WHERE mli_checker_id = '"
						+ chSelectId + "'";
				
				// System.out.println("GMA Delquery : "+Delquery);
				 statement = connection.createStatement();
				statement.executeUpdate(Delquery);
				request.setAttribute("message",
						"The Request For Deletion Completed.");
				// System.out.println("GMA delete called");
				}
			} else {
				request.setAttribute("message",
						"<b>A Problem Occured At Action Level.</b>");
			}

			rs.close();
			rs = null;

			connection.commit();
			// end for
		}// end try
		catch (Exception sql) {
			// sql.printStackTrace();
			
			
			connection.rollback();
			throw new DatabaseException(
					"A Problem Occured While Performing Updation/Deletion Action"
							+ sql.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (Exception ss) {
				ss.printStackTrace();
			}
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("message",
				"<b>The Request For Submission Completed.</b>");
		Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Exited");
		// System.out.println("GMA showApprRegistrationFormSubmit end");
		return mapping.findForward("success");
	}


	public ActionForward showApprRegistrationFormSubmit( ActionMapping mapping,  ActionForm form,  HttpServletRequest request,  HttpServletResponse response) throws Exception {
        Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Entered");
         HttpSession session = request.getSession(false);
         Connection connection = DBConnection.getConnection(false);
        Statement statement = null;
        connection.setAutoCommit(false);
        ResultSet rs = null;
         GMActionForm gmActionForm = (GMActionForm)form;
         User user = this.getUserInformation(request);
         String bankId = user.getBankId();
         String zoneId = user.getZoneId();
         String branchId = user.getBranchId();
         String memberId = bankId + zoneId + branchId;
         String actionType = request.getParameter("action");
         Map commentIdVal = gmActionForm.getEmpComments();
         String[] checkId = gmActionForm.getCheck();
        String chSelectId = "";
        String commVal = "";
         String query = "";
         AdminHelper adminHelper = new AdminHelper();
        boolean mailssend = false;
         String filePath = "";
        String emailid = "";
        String mliUsrId = "";
        String mlibankid = "";
         boolean sendMailStatus = true;
        CallableStatement callable = null;
        CallableStatement callable2 = null;
        try {
            if (actionType.equals("update")) {
                statement = connection.createStatement();
                for (int i = 0; i < checkId.length; ++i) {
                    chSelectId = checkId[i];
                    commVal = (String) commentIdVal.get(chSelectId);
                     PasswordManager passwordManager = new PasswordManager();
                     String encryptedPassword = adminHelper.generatePassword();
                     String password = passwordManager.decryptPassword(encryptedPassword);
                     String userId = "";
                    String usrIdqry = "select distinct usr_id from  mli_checker_info where  MLI_CHECKER_ID='" + chSelectId + "'";
                    rs = statement.executeQuery(usrIdqry);
                    while (rs.next()) {
                        usrIdqry = rs.getString(1);
                    }
                     String subject = "User Password Generation from CGTMSE(New User ID)";
                     String mailBody = "Dear User, \n \n Congrats,Your Password has been generated from CGTMSE,corresponding to your Checker User Id.Please find the below details. \n Transaction Id :" + usrIdqry + " \n " + "Password :" + password + "\n Please note that this Id and  Password is being given to you for approval of the transaction, as a Checker \n submitted by  your operating leval officer and this Id/Password must not be shared with anybody." + " \n" + "you may login the CGTMSE portal by using the Checker User Id for approving the transaction.\n While first time Login  using the User Checker Id/Password you must change the Password,hint question and hint answer.     \n \n Regards, \n CGTMSE.   ";
                    callable = connection.prepareCall("{?=call FUNCMLICHECKERSAPPROVAL(?,?,?,?)}");
                    callable.registerOutParameter(1, 4);
                    callable.setString(2, encryptedPassword);
                    callable.setString(3, user.getUserId());
                    callable.setString(4, chSelectId);
                    callable.registerOutParameter(5, 12);
                    callable.execute();
                     int errorCode = callable.getInt(1);
                     String error = callable.getString(5);
                    Log.log(5, "GMDAO", "submitUpgradationDetails", "error code and error" + errorCode + "," + error);
                    if (errorCode == 1) {
                        Log.log(2, "GMDAO", "submitUpgradationDetails", error);
                        callable.close();
                        callable = null;
                        throw new DatabaseException(error);
                    }
                     String emailQry = "select distinct USR_EMAIL_ID,USR_ID from mli_checker_info where  MLI_CHECKER_ID='" + chSelectId + "'";
                    rs = statement.executeQuery(emailQry);
                    while (rs.next()) {
                        emailid = rs.getString(1);
                        mliUsrId = rs.getString(2);
                    }
                    /*try {
                        callable2 = connection.prepareCall("{ call CGTSIINTRANETUSER.SENDTEXTMAIL_GEN(?,?,?,?) }");
                        callable2.setString(1, emailid);
                        callable2.setString(2, "support@cgtmse.in");
                        callable2.setString(3, subject);
                        callable2.setString(4, mailBody);
                        callable2.execute();
                    }
                    catch (Exception err) {
                        Log.log(5, "GMAction", "showApprRegistrationFormSubmit", err.toString());
                        callable2.close();
                        callable2 = null;
                        throw new DatabaseException(err.toString());
                    }*/
                }
            }
            else if (actionType.equals("delete")) {
                statement = connection.createStatement();
                for (int i = 0; i < checkId.length; ++i) {
                    chSelectId = checkId[i];
                    commVal = (String) commentIdVal.get(chSelectId);
                     ArrayList emailids = new ArrayList();
                     String emailQry = "select distinct USR_EMAIL_ID,mem_bnk_id  from mli_checker_info where  MLI_CHECKER_ID='" + chSelectId + "'";
                    rs = statement.executeQuery(emailQry);
                    while (rs.next()) {
                        emailid = rs.getString(1);
                        mlibankid = rs.getString(2);
                    }
                     String emailHOQry = "select distinct MEM_EMAIL from member_info  where  mem_bnk_id='" + mlibankid + "' and mem_zne_id='0000'  " + " and mem_brn_id='0000'   and mem_status='A' ";
                    rs = statement.executeQuery(emailHOQry);
                    while (rs.next()) {
                        mliUsrId = rs.getString(1);
                    }
                    emailids.add(emailid);
                    emailids.add(mliUsrId);
                     String subject = "Checker User Id Rejection from CGTMSE";
                     String mailBody = "Dear User, \n \n Sorry,Your Request for Creation of Checker User Id has been Rejected by CGMTSE due to " + commVal + " .\n You are requested to contact your HO and operating officer to resubmit the request.\n" + " \n \n Regards, \n CGTMSE.   ";
                   // mailssend = this.sendMailBisenessLogic(subject, mailBody, emailids, filePath);
                     String Delquery = "update  mli_checker_info set USR_STATUS='CR',CGTMSE_approvar_remarks='" + commVal + "' WHERE mli_checker_id = '" + chSelectId + "'";
                    statement = connection.createStatement();
                    statement.executeUpdate(Delquery);
                    request.setAttribute("message", (Object)"The Request For Deletion Completed.");
                }
            }
            else {
                request.setAttribute("message", (Object)"<b>A Problem Occured At Action Level.</b>");
            }
            rs.close();
            rs = null;
            connection.commit();
        }
        catch (Exception sql) {
            connection.rollback();
            throw new DatabaseException("A Problem Occured While Performing Updation/Deletion Action" + sql.getMessage());
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (Exception ss) {
                ss.printStackTrace();
            }
            DBConnection.freeConnection(connection);
        }
        try {
            if (statement != null) {
                statement.close();
            }
        }
        catch (Exception ss) {
            ss.printStackTrace();
        }
        DBConnection.freeConnection(connection);
        request.setAttribute("message", (Object)"<b>The Request For Submission Completed.</b>");
        Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Exited");
        return mapping.findForward("success");
    }
    
	
	 public boolean sendMailBisenessLogic(String mailSubject,String mailBody1, ArrayList mailIDs , String FilePath )
		{
			//LogClass.StepWritter("In side sendMailBisenessLogic"+mailID);
		  //System.out.println("AA sendMailBisenessLogic S");
			//System.out.println("sendMailBisenessLogic called"+mailIDs.size());
			//System.out.println("AA mailBody1 : "+mailBody1);
			//System.out.println("AA mailBodyMli : "+mailBodyMli);
			boolean sendMailStatus=true;
			try
			{
				
				int emilids=mailIDs.size()-1;
				
				
				for(int i=0;i<=emilids;i++)
				{
					
				String	mailID=(String)mailIDs.get(i);
				System.out.println("AA mailID : "+mailID);
				
				String host = "192.168.10.118";
				
				boolean sessionDebug = false;			
				Properties props = System.getProperties();
			    props.put("mail.host", host);
			    props.put("mail.transport.protocol", "smtp");
			    props.put("mail.smtp.host", host);
			    props.put("mail.from", "support@cgtmse.in");
				
				Session session1= null;
		        session1 = Session.getDefaultInstance(props, null);
		        session1.setDebug(sessionDebug);
		               
		        javax.mail.internet.MimeMessage msg = new javax.mail.internet.MimeMessage(session1);
		        msg.setFrom(new javax.mail.internet.InternetAddress("support@cgtmse.in"));
		     //   System.out.println("AA send Mail : "+mailID);
		        javax.mail.internet.InternetAddress[] Toaddress = {new javax.mail.internet.InternetAddress(mailID)};     
		        msg.setRecipients(javax.mail.Message.RecipientType.TO, Toaddress);
		        msg.setSubject(mailSubject);
		        msg.setSentDate(new Date());
		       
	          	msg.setText(mailBody1);
	                 
	        Transport.send(msg);
			}  
				
			}
			
			catch(Exception e)
			{
				
			}
			return sendMailStatus;
		}
	
	public ArrayList getPrivilegesForRole(String roleName)
			throws DatabaseException {
		if (roleName != null && !roleName.equals("")) {
			AdminDAO adminDAO = new AdminDAO();
			return adminDAO.getPrivilegesForRole(roleName);
		}

		return null;
	}

	//Diksha
	
	
	public ActionForward cgpanReductionApproval(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(5, "GMAction", "showApprRegistrationForm", "Entered");
		HttpSession session = request.getSession(false);
		//GMActionForm ApprovalForm = (GMActionForm) form;
		DynaActionForm dynaForm     = (DynaActionForm)form;
		GMActionForm ApprovalForm=new GMActionForm();
		
		// System.out.println("GMA memberId : "+memberId);
		ArrayList formList = getcgpanReductionApproval();
//		System.out.println("formList="+formList);
		String forward = "";
		/*if (formList == null || formList.size() == 0) {
			request.setAttribute("message",
					"No Applications Available For Approval");
			forward = "success";
		} else {
			ApprovalForm.setCgmap(formList);
			forward = "cgpanList";
		}*/

		dynaForm.set("cgmap",formList);
		
		Log.log(5, "GMAction", "showApprRegistrationForm", "Exited");
		return mapping.findForward("cgpanList");
	}

	
public ArrayList getcgpanReductionApproval()throws Exception 
{
//DynaActionForm dynaForm     = (DynaActionForm)form;
 //String cgpan                = (String) dynaForm.get("enterCgpan");

// System.out.println("cgpan====="+cgpan);  
		// ArrayList cgmap = new ArrayList();
			

	    ResultSet rs = null;
		PreparedStatement pst = null;
		ArrayList aList=new ArrayList();
		//TreeMap cgmap = new TreeMap();

	Connection connection = DBConnection.getConnection();
   try { 
	   
	String query = "select CGPAN,nvl(WCP_FB_LIMIT_SANCTIONED,0),nvl(WCP_NFB_LIMIT_SANCTIONED,0),nvl(WCP_FB_CREDIT_TO_GUARANTEE,0),nvl(WCP_NFB_CREDIT_TO_GUARANTEE,0),to_char(MLI_REQUEST_DT,'dd/MM/yyyy') from REDUCTION_WORKING_STAGING WHERE MAKER_CHEKER_FLAG='MA' ";

//	System.out.println("query===1=="+query);

pst = connection.prepareStatement(query);
rs = pst.executeQuery();

		
	while (rs.next()) { 
		GMActionForm redForm =new GMActionForm();
		redForm.setCGPAN(rs.getString(1));
//		System.out.println("setCGPAN=="+(rs.getString(1)));
		
		redForm.setWCP_FB_LIMIT_SANCTIONED(rs.getString(2));
//		System.out.println("setWCP_FB_LIMIT_SANCTIONED=="+(rs.getString(2)));
		
		redForm.setWCP_NFB_LIMIT_SANCTIONED(rs.getString(3));
//		System.out.println("setWCP_NFB_LIMIT_SANCTIONED=="+(rs.getString(3)));
		
		redForm.setWCP_FB_CREDIT_TO_GUARANTEE(rs.getString(4));
//		System.out.println("setWCP_FB_CREDIT_TO_GUARANTEE=="+(rs.getString(4)));
		
		redForm.setWCP_NFB_CREDIT_TO_GUARANTEE(rs.getString(5));
//		System.out.println("setWCP_NFB_CREDIT_TO_GUARANTEE=="+(rs.getString(5)));
		redForm.setMliRequestDate(rs.getString(6));
			   
		aList.add(redForm);
		}
		
	   //dynaForm.set("cgpanHistoryReport",aList);

	System.out.println("aList="+aList);
			connection.commit();
			pst.close();
			pst = null;
			rs.close();
			rs = null;
		} catch (Exception sql) {
			sql.printStackTrace();
		} finally {
			DBConnection.freeConnection(connection);
		}
		//System.out.println("GMA displayNpaRegistrationFormList E");
		Log.log(5, "GMAction", "getcgpanReductionApproval", "Exited");
		return aList;
	    }  

		public ActionForward showApprReductionFormSubmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws Exception
		{
			 Log.log(5, "GMAction", "showApprReductionFormSubmit", "Entered");

			HttpSession session = request.getSession(false);
			Connection connection = DBConnection.getConnection();
			Statement statement;
		    ResultSet rs = null;
		    ResultSet rs1 = null;
		    ResultSet rs2 = null;
		    ResultSet res = null; 
		    PreparedStatement pst = null;
		 
			connection = DBConnection.getConnection(false);
			statement = null;
			connection.setAutoCommit(false);
			int cnt=0;

		    User user = getUserInformation(request);
			String userid = user.getUserId();
//			System.out.println("userid"+userid);
			//String actionType = request.getParameter("action");
			String values=request.getParameter("values");
			String Totalapporved="",OldTotalapporved="";
			String wcPFBLimitsanctioned="",wcpNFBLimitsancioned="",wcPFBCredittoguarantee="",wcpNFBCredittoguarantee="",Remark="";
	        String IDs[];
	        IDs=values.split(",");
	        try {
	        if (request.getParameter("Actiontype").equals("update")) 
	        	
	        {
			for (int i=0; i< IDs.length ;i++) 
			{
			   String vals =IDs[i];
//			   System.out.println(IDs[i]); 
			    //String queryofammount="SELECT NVL(WCP_FB_LIMIT_SANCTIONED,0) + NVL(WCP_NFB_LIMIT_SANCTIONED,0)  FROM REDUCTION_WORKING_STAGING   where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG='MA' "; 
			   String queryofammount="SELECT NVL(WCP_FB_CREDIT_TO_GUARANTEE,0) + NVL(WCP_NFB_CREDIT_TO_GUARANTEE,0)  FROM REDUCTION_WORKING_STAGING   where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG='MA' ";
//		        System.out.println("query"+queryofammount);
		        pst = connection.prepareStatement(queryofammount);
		        rs1 = pst.executeQuery(queryofammount);
		        while(rs1.next()){
		        	Totalapporved=rs1.getString(1);	
		        	
		        }
		        String queryofOldAmmt="SELECT NVL(OLD_WCP_FB_LIMIT_SANCTIONED,0) + NVL(OLD_WCP_NFB_LIMIT_SANCTIONED,0)  FROM REDUCTION_WORKING_STAGING   where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG='MA' "; 
//		        System.out.println("query"+queryofOldAmmt);
		        pst = connection.prepareStatement(queryofOldAmmt);
		        rs2 = pst.executeQuery(queryofOldAmmt);
		        while(rs2.next()){
		        	OldTotalapporved=rs2.getString(1);	
		        	
		        }
		            String query="select  * FROM REDUCTION_WORKING_STAGING   where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG='MA'"; 
//			        System.out.println("query5"+query);
			        pst = connection.prepareStatement(query);
			        rs = pst.executeQuery(query);
			     
			   while(rs.next() )
			        {
				  wcPFBLimitsanctioned=rs.getString("WCP_FB_LIMIT_SANCTIONED");
			      wcpNFBLimitsancioned=rs.getString("WCP_NFB_LIMIT_SANCTIONED");
			      wcPFBCredittoguarantee=rs.getString("WCP_FB_CREDIT_TO_GUARANTEE");
			      wcpNFBCredittoguarantee=rs.getString("WCP_NFB_CREDIT_TO_GUARANTEE");
			      Remark=rs.getString("MAKER_REMARKS");
			      
				  String query1 ="UPDATE WORKING_CAPITAL_detail SET WCP_FB_LIMIT_SANCTIONED = '"+ wcPFBLimitsanctioned + "',WCP_NFB_LIMIT_SANCTIONED ='"+ wcpNFBLimitsancioned + "',WCP_FB_CREDIT_TO_GUARANTEE = '"+ wcPFBCredittoguarantee + "',WCP_NFB_CREDIT_TO_GUARANTEE ='"+ wcpNFBCredittoguarantee + "'  where cgpan = '"+ vals + "' ";
			  	  pst = connection.prepareStatement(query1);
			      pst.executeUpdate(query1);
//		          System.out.println("query"+query1);
			      
			      String query4 ="UPDATE WORKING_CAPITAL_detail@repuser SET WCP_FB_LIMIT_SANCTIONED = '"+ wcPFBLimitsanctioned + "',WCP_NFB_LIMIT_SANCTIONED ='"+ wcpNFBLimitsancioned + "',WCP_FB_CREDIT_TO_GUARANTEE = '"+ wcPFBCredittoguarantee + "',WCP_NFB_CREDIT_TO_GUARANTEE ='"+ wcpNFBCredittoguarantee + "'  where cgpan = '"+ vals + "' ";
			  	  pst = connection.prepareStatement(query4);
			      pst.executeUpdate(query4);
//		          System.out.println("query"+query1);
		          
			  	  
			  	  String query2 ="update application_detail set APP_APPROVED_AMOUNT = '"+ Totalapporved + "',app_remarks = app_remarks|| '"+ Remark + "'  where cgpan = '"+ vals + "'";
  	  	 	      pst = connection.prepareStatement(query2);
                  pst.executeUpdate(query2);
//                  System.out.println("query"+query2);
                  
                  String query5 ="update application_detail@repuser set APP_APPROVED_AMOUNT = '"+ Totalapporved + "',app_remarks = app_remarks|| '"+ Remark + "'  where cgpan = '"+ vals + "'";
  	  	 	      pst = connection.prepareStatement(query5);
                  pst.executeUpdate(query5);
//                  System.out.println("query"+query2);
                  
                  String query3 ="update REDUCTION_WORKING_STAGING set OLD_GURENTEE_FEE = '"+ OldTotalapporved + "',CHECKER_USER_ID = '"+ userid + "',CHECKER_RED_DT=SYSDATE,MAKER_CHEKER_FLAG='CA' where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG='MA'";
  	  	 	      pst = connection.prepareStatement(query3);
                  pst.executeUpdate(query3);
//                  System.out.println("query"+query3);
                  
			 
			        }
			} 
			request.setAttribute("message", "<b>The Request For Submission Completed.</b>");
	        }
			
	        //Diksha
	        else if (request.getParameter("Actiontype").equals("reject"))
	        {
	        	for (int i=0; i< IDs.length ;i++) 
				{
				   String vals =IDs[i];
				  // System.out.println(IDs[i]); 
				   
				   String query3 ="update REDUCTION_WORKING_STAGING set CHECKER_USER_ID = '"+ userid + "',CHECKER_RED_DT=SYSDATE,MAKER_CHEKER_FLAG='RE' where cgpan = '"+ vals + "' and MAKER_CHEKER_FLAG<>'CA'";
	  	  	 	      pst = connection.prepareStatement(query3);
	                  pst.executeUpdate(query3);
	                  //System.out.println("query"+query3);
				    
			   
				}
	        	request.setAttribute("message", "<b>The Request For Submission Rejected.</b>");
				} else {
					request.setAttribute("message",
							"<b>A Problem Occured At Action Level.</b>");
				}
				
	        }
	       
			catch(Exception ss)
			{
			    ss.printStackTrace();
			    System.out.println("Error"+ss);
			}
			 finally {
				 if(statement != null)
					 statement.close();
					DBConnection.freeConnection(connection);
					}
			//	request.setAttribute("message", "<b>The Request For Submission Completed.</b>");
			Log.log(5, "GMAction", "showApprRegistrationFormSubmit", "Exited");
			return mapping.findForward("success");
	        
		}	
		
		//added RAJUK
		
		
		public ActionForward showClaimAccountDetails( ActionMapping mapping,  ActionForm form,  HttpServletRequest request,  HttpServletResponse response) throws Exception {
	        Log.log(5, "GMAction", "showApprRegistrationForm", "Entered");
	         HttpSession session = request.getSession(false);
	         GMActionForm ApprovalForm = (GMActionForm)form;
	         User user = this.getUserInformation(request);
	         String bankId = user.getBankId();
	         String zoneId = user.getZoneId();
	         String branchId = user.getBranchId();
	         String memberId = bankId + zoneId + branchId;
	         ArrayList formList = this.displayClaimAccountFormList(memberId);
	        String forward = "";
	        if (formList == null || formList.size() == 0) {
	            request.setAttribute("message", (Object)"No Applications Available For Approval");
	            forward = "success";
	        }
	        else {
	            ApprovalForm.setNpaRegistFormList(formList);
	            forward = "npaRegistList";
	        }
	        Log.log(5, "GMAction", "showApprRegistrationForm", "Exited");
	        return mapping.findForward(forward);
	    }
	    
	    public ArrayList displayClaimAccountFormList( String memberId) throws DatabaseException {
	        Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Entered");
	         ArrayList NpaRegistList = new ArrayList();
	        GMActionForm npaRegistForm = null;
	         Connection connection = DBConnection.getConnection(false);
	        ResultSet rs = null;
	        Statement stmt = null;
	         String query = "select MLI_ID,MLI_NAME,MEM_BENEFICIARY,BENEFICIARY_BANK_NAME,MEM_ACC_NO,MEM_RTGS_NO,MEM_REMARKS from  MEMBER_ACCOUNT_INFO  where   MEM_STATUS='MA' ";
	        Label_0232: {
	            try {
	                stmt = connection.createStatement();
	                rs = stmt.executeQuery(query);
	                while (rs.next()) {
	                    npaRegistForm = new GMActionForm();
	                    npaRegistForm.setMemberId(rs.getString(1));
	                    npaRegistForm.setZoneName(rs.getString(2));
	                    npaRegistForm.setEmpFName(rs.getString(3));
	                    npaRegistForm.setCGPAN(rs.getString(4));
	                    npaRegistForm.setEmpMName(rs.getString(5));
	                    npaRegistForm.setEmpLName(rs.getString(6));
	                    npaRegistForm.setReasonForReduction(rs.getString(7));
	                    NpaRegistList.add(npaRegistForm);
	                }
	                connection.commit();
	                stmt.close();
	                stmt = null;
	                rs.close();
	                rs = null;
	            }
	            catch (Exception sql) {
	                sql.printStackTrace();
	                break Label_0232;
	            }
	            finally {
	                DBConnection.freeConnection(connection);
	            }
	            
	        }
	        Log.log(5, "GMAction", "displayNpaRegistrationFormList", "Exited");
	        return NpaRegistList;
	    }
	    
	    public ActionForward showApprClaimAccountSubmit( ActionMapping mapping,  ActionForm form,  HttpServletRequest request,  HttpServletResponse response) throws Exception {
	         HttpSession session = request.getSession(false);
	         Connection connection = DBConnection.getConnection(false);
	        Statement statement = null;
	        connection.setAutoCommit(false);
	         ResultSet rs = null;
	         GMActionForm gmActionForm = (GMActionForm)form;
	         User user = this.getUserInformation(request);
	         String bankId = user.getBankId();
	         String zoneId = user.getZoneId();
	         String branchId = user.getBranchId();
	         String memberId = bankId + zoneId + branchId;
	         String userid = user.getUserId();
	         String actionType = request.getParameter("action");
	        System.out.println("GMA actionType : " + actionType);
	         Map commentIdVal = gmActionForm.getEmpComments();
	        System.out.println("GMA commentIdVal : " + commentIdVal);
	         String[] checkId = gmActionForm.getCheck();
	        System.out.println("GMA checkId : " + checkId.length);
	         HashMap claimapps = new HashMap();
	         Vector appclms = new Vector();
	         Vector retclms = new Vector();
	        String chSelectId = "";
	        String commVal = "";
	         String query = "";
	         Statement str1 = connection.createStatement();
	         String query2 = "SELECT  CURRENT_DATE FROM DUAL";
	         ResultSet rs1 = str1.executeQuery(query2);
	        Date sysdate = null;
	        while (rs1.next()) {
	            sysdate = rs1.getDate(1);
	        }
	        System.out.println("sysdate==" + sysdate);
	        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	         String date = sdf.format((java.sql.Date)sysdate);
	        System.out.println("date==" + date);
	        sdf = new SimpleDateFormat("dd-MM-yyyy");
	        try {
	            statement = connection.createStatement();
	            if (actionType.equals("update")) {
	                System.out.println("checkId.length :" + checkId.length);
	                for (int i = 0; i < checkId.length; ++i) {
	                    chSelectId = checkId[i];
	                    commVal = (String) commentIdVal.get(chSelectId);
	                    System.out.println("commVal==" + commVal);
	                     String quryforSelect = "update MEMBER_ACCOUNT_INFO  set  MEM_STATUS='CA' ,MEM_REMARKS='" + commVal + "'  ,CG_CK_USER_ID='" + userid + "' , CG_CK_MODIFIED_DATE='" + date + "'   where MLI_ID='" + chSelectId + "' ";
	                    System.out.println("testing...1" + quryforSelect);
	                     int qrystatus = statement.executeUpdate(quryforSelect);
	                    System.out.println("qrystatus :" + qrystatus);
	                    appclms.add(chSelectId);
	                }
	                connection.commit();
	                statement.close();
	            }
	            else if (actionType.equals("delete")) {
	                for (int i = 0; i < checkId.length; ++i) {
	                    chSelectId = checkId[i];
	                    commVal = (String) commentIdVal.get(chSelectId);
	                     String Delquery = "update MEMBER_ACCOUNT_INFO  set  MEM_STATUS='CR' ,MEM_REMARKS='" + commVal + "' ,CG_CK_USER_ID='" + userid + "' , CG_CK_MODIFIED_DATE='" + date + "'   where MLI_ID='" + chSelectId + "' ";
	                    statement.executeUpdate(Delquery);
	                    retclms.add(chSelectId);
	                }
	                connection.commit(); 
	                statement.close();
	            }
	            claimapps.put("apprvdClaims", appclms);
	            claimapps.put("retClaims", retclms);
	            gmActionForm.setCgpandetails((Map)claimapps);
	            request.setAttribute("claimappsMap", (Object)claimapps);
	        }
	        catch (Exception sql) {
	            sql.printStackTrace();
	            connection.rollback();
	            throw new DatabaseException("A Problem Occured While Performing Updation/Deletion Action" + sql.getMessage());
	        }
	        finally {
	            DBConnection.freeConnection(connection);
	        }
	       
	        return mapping.findForward("claimsuccessSummary");
	    }


	
}
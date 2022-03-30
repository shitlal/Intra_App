package com.cgtsi.action;

import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.reports.ReportManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

import com.cgtsi.admin.Message;
import com.cgtsi.admin.User;
import com.cgtsi.admin.Administrator;
//import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.registration.Registration;
import com.cgtsi.registration.NoMemberFoundException;
import com.cgtsi.registration.RegistrationDAO;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.mcgs.MCGSProcessor;
import com.cgtsi.mcgs.MCGFDetails;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.DuplicateApplication;
import com.cgtsi.application.PrimarySecurityDetails;
import com.cgtsi.application.ProjectOutlayDetails;
import com.cgtsi.application.SSIDetails;
import com.cgtsi.application.TermLoan;
import com.cgtsi.application.WorkingCapital;
import com.cgtsi.application.SpecialMessage;
import com.cgtsi.application.Securitization;
import com.cgtsi.application.EligibleApplication;
//import com.cgtsi.application.DuplicateApplication;
import com.cgtsi.application.ApplicationConstants;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.util.SessionConstants;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.Mailer;
import com.cgtsi.common.MailerException;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoUserFoundException;
import com.cgtsi.actionform.APForm;
import com.cgtsi.claim.ClaimsProcessor;
//import com.cgtsi.receiptspayments.DemandAdvice;
import com.cgtsi.receiptspayments.RpHelper;
import com.cgtsi.receiptspayments.RpProcessor;
import com.cgtsi.risk.RiskManagementProcessor;
//import com.cgtsi.risk.RiskManagementProcessor;
//import com.cgtsi.risk.SubSchemeValues;
import com.cgtsi.guaranteemaintenance.GMProcessor;

public class ApplicationProcessingAction extends BaseAction {
	Application application = new Application();
	Registration registration = new Registration();

	/*
	 * This method sets the loan type for a term loan application
	 */
 
	public ActionForward getTCMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getTCMliInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();

		String forward = "";

		HttpSession session = request.getSession(false);
		session.setAttribute("page", "MLIInfo");
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "TC");
		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "7");

		dynaForm.set("loanType", ApplicationConstants.TC_APPLICATION);
		application.setLoanType(ApplicationConstants.TC_APPLICATION);

		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			user = null;

			forward = "mliPage";

		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "getTCMliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getTCMliInfo",
					"mcgfsupport :" + mcgfsupport);
			if (mcgfsupport.equals("Y")) {
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"getTCMliInfo", "Size :" + ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getTCMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				ssiRefNosList = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "CGFSI");

				forward = "tcForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getTCMliInfo",
				"Exited");

		return mapping.findForward(forward);
	}

	/**
	 * 
	 * addded by sukumar@path on 20/06/2009 for PCGS for Term Loan Applications
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */

	public ActionForward getPCGSMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getPCGSMliInfo",
				"Entered");
		// System.out.println("getPCGSMliInfo Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();

		String forward = "";

		HttpSession session = request.getSession(false);
		// session.setAttribute("page","MLIInfo1");
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "PCGS");
		// System.out.println("line number244-"+session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));
		// System.out.println("PCGS");
		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "7");
		// System.out.println("Line number 244-"+session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
		// dynaForm.set("loanType",ApplicationConstants.RSF_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.TC_APPLICATION);
		// System.out.println(dynaForm.get("loanType"));
		application.setLoanType(ApplicationConstants.TC_APPLICATION);
		// System.out.println("application.getLoanType"+application.getLoanType());
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		// System.out.println("Line number 257-"+bankId);

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			user = null;

			forward = "mliPage";

		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;
			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFMliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFMliInfo",
					"mcgfsupport :" + mcgfsupport);
			if (mcgfsupport.equals("Y")) {
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"getRSFMliInfo", "Size :" + ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				ssiRefNosList = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "PCGS");

				forward = "pcgsForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			ArrayList industrySectorList = getIndustrySector();
			// System.out.println("industrySectorList:"+industrySectorList.size());
			dynaForm.set("industrySectorList", industrySectorList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			industrySectorList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFMliInfo",
				"Exited");

		// System.out.println("Line number 355-"+forward);
		return mapping.findForward(forward);
	}

	/**
	 * added this method for MLIs to apply PCGS term loan application
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward submitPCGSApp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// System.out.println("Line number 375 - ApplicationProcessingAction"+"submitPCGSApp"+"Entered");
		String successPage = "";
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession applicationSession = request.getSession(false);
		// System.out.println("Line number 381-Application Loan Type:"+applicationSession.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));
		// System.out.println("Line number 382-Application Flag:"+applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		// System.out.println("Line number 404 userId:"+userId);
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitPCGSApp",
				"user Id :" + userId);
		String zoneId = "";
		String branchId = "";
		String mliId = "";

		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();

		String internalRating = (String) dynaForm.get("internalRating");
		// System.out.println("internalRating:"+internalRating);
		application.setInternalRate(internalRating);
		String handiCrafts = (String) dynaForm.get("handiCrafts");
		String dcHandicrafts = (String) dynaForm.get("dcHandicrafts");
		String icardNo = (String) dynaForm.get("icardNo");
		// java.util.Date icardIssueDate = (java.util.Date)
		// dynaForm.get("iCardIssueDate");

		// System.out.println("icardIssueDate:"+icardIssueDate);
		application.setHandiCrafts(handiCrafts);
		application.setDcHandicrafts(dcHandicrafts);
		application.setIcardNo(icardNo);
		String mseCategory = (String) dynaForm.get("mseCategory");
		// System.out.println("mseCategory:"+mseCategory);
		java.util.Date expiryDate = (java.util.Date) dynaForm.get("expiryDate");
		// System.out.println("expiryDate:"+expiryDate);
		// application.setIcardIssueDate(icardIssueDate) ;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			String memberName = (String) dynaForm.get("selectMember");
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);

				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = bankId + zoneId + branchId;
				application.setMliID(mliId);
			}

		} else {

			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = bankId + zoneId + branchId;
			application.setMliID(mliId);

		}
		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);
		dynaForm.set("mliID", mliId);
		// System.out.println("MLI Id:"+mliId);

		String applicationType = "";
		String applicationLoanType = "";

		if (applicationSession
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE) == null) {
			applicationType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitPCGSApp",
					"ApplicationLoan Type :" + applicationType);
		} else {
			applicationLoanType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitPCGSApp",
					"ApplicationLoan Type :" + applicationLoanType);
			// System.out.println("Line number 458 - Application Loan Type :" +
			// applicationLoanType);
			application.setLoanType(ApplicationConstants.TC_APPLICATION); // setting
																			// the
																			// loan
																			// type
			String type = (String) dynaForm.get("loanType");
			application.setLoanType(type);
		}
		Log.log(Log.INFO, "ApplicationProcessingAction", "submitPCGSApp",
				"Calling Bean Utils...");
		BeanUtils.populate(ssiDetails, dynaForm.getMap());
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());

		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);
		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());

		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(securitization, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());
		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);

		}
		application.setBorrowerDetails(borrowerDetails);
		// System.out.println("State:"+ssiDetails.getState());
		// System.out.println("District:"+ssiDetails.getDistrict());
		// System.out.println("City:"+ssiDetails.getCity());
		// System.out.println("Unit Name:"+ssiDetails.getActivityType());
		double termCreditSanctioned = ((java.lang.Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		// System.out.println("termCreditSanctioned:"+termCreditSanctioned);
		// double
		// tcPromoterContribution=((java.lang.Double)dynaForm.get("tcPromoterContribution")).doubleValue();
		// System.out.println("tcPromoterContribution:"+tcPromoterContribution);
		double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("tcSubsidyOrEquity")).doubleValue();
		// System.out.println("tcSubsidyOrEquity:"+tcSubsidyOrEquity);
		// double
		// tcOthers=((java.lang.Double)dynaForm.get("tcOthers")).doubleValue();
		// System.out.println("tcOthers:"+tcOthers);
		double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		// System.out.println("wcFundBasedSanctioned:"+wcFundBasedSanctioned);
		// double
		// wcNonFundBasedSanctioned=((java.lang.Double)dynaForm.get("wcNonFundBasedSanctioned")).doubleValue();
		// System.out.println("wcNonFundBasedSanctioned:"+wcNonFundBasedSanctioned);
		// double
		// wcPromoterContribution=((java.lang.Double)dynaForm.get("wcPromoterContribution")).doubleValue();
		// System.out.println("wcPromoterContribution:"+wcPromoterContribution);
		double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("wcSubsidyOrEquity")).doubleValue();
		// System.out.println("wcSubsidyOrEquity:"+wcSubsidyOrEquity);
		// double
		// wcOthers=((java.lang.Double)dynaForm.get("wcOthers")).doubleValue();
		// System.out.println("wcOthers:"+wcOthers);
		// double projectOutlayVal=termCreditSanctioned + tcPromoterContribution
		// + tcSubsidyOrEquity + tcOthers
		// + wcFundBasedSanctioned + wcNonFundBasedSanctioned +
		// wcPromoterContribution
		// + wcSubsidyOrEquity + wcOthers;

		double projectCost = ((java.lang.Double) dynaForm.get("projectCost"))
				.doubleValue();
		double minValue = 1000.0;
		double maxValue = 10000000.0;
		if (projectCost < minValue) {
			throw new MessageException(
					"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :" + 10000000);
		}
		Double projectOutlayCost = new Double(projectCost);
		// System.out.println("Project Cost:"+projectOutlayCost);
		double projectOutlay = projectOutlayCost.doubleValue();
		projectOutlayDetails.setProjectOutlay(projectOutlay);
		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setTermLoan(termLoan);
		application.setWc(workingCapital);
		application.setSecuritization(securitization);
		BeanUtils.populate(application, dynaForm.getMap());
		if (dynaForm.get("none").equals("cgpan")) {
			application.setCgpan((String) dynaForm.get("unitValue"));
			// System.out.println("Unit value:"+(String)dynaForm.get("unitValue"));
		} else if (dynaForm.get("none").equals("cgbid")) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!(borrowerIds.contains(dynaForm.get("unitValue")))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}

			application.getBorrowerDetails().getSsiDetails()
					.setCgbid((String) dynaForm.get("unitValue"));
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
				"application type :" + applicationType);
		// setting the scheme Name
		application.setScheme("PCGS");
		application.setCgpanReference("");
		ClaimsProcessor claimProcessor = new ClaimsProcessor();
		String cgbid = "";
		if (application.getCgpan() != null
				&& !application.getCgpan().equals("")) {
			cgbid = claimProcessor.getBorowwerForCGPAN(application.getCgpan());
			// System.out.println("Line number-578"+cgbid);
		} else if (application.getBorrowerDetails().getSsiDetails().getCgbid() != null
				&& !(application.getBorrowerDetails().getSsiDetails()
						.getCgbid().equals(""))) {

			cgbid = application.getBorrowerDetails().getSsiDetails().getCgbid();
			// System.out.println("Line number-584"+cgbid);
		}

		int claimCount = appProcessor.getClaimCount(cgbid);
		if (claimCount > 0) {
			throw new MessageException(
					"Application cannot be filed by this borrower since Claim Application has been submitted");
		}
		application.setLoanType(application.getLoanType());
		application.getBorrowerDetails().getSsiDetails()
				.setEnterprise((String) dynaForm.get("enterprise"));
		// System.out.println("Enterprise:"+(String)dynaForm.get("enterprise"));
		application.getBorrowerDetails().getSsiDetails()
				.setUnitAssisted((String) dynaForm.get("unitAssisted"));
		// System.out.println("Unit Assisted:"+(String)dynaForm.get("unitAssisted"));
		application.getBorrowerDetails().getSsiDetails()
				.setWomenOperated((String) dynaForm.get("womenOperated"));

		// System.out.println("MSE  IN APPLICATION IS:"+
		// application.getBorrowerDetails().getSsiDetails().setMSE((String)dynaForm.get("mSE")));
		application = appProcessor.submitNewApplication(application, userId);
		String appRefNo = application.getAppRefNo();
		dynaForm.set("appRefNo", appRefNo);
		int borrowerRefNo = ((application.getBorrowerDetails()).getSsiDetails())
				.getBorrowerRefNo();
		Integer refNoValue = new Integer(borrowerRefNo);
		dynaForm.set("borrowerRefNo", refNoValue);
		request.setAttribute("message", "Application (Reference No:" + appRefNo
				+ ")Submitted Successfully");
		if (applicationLoanType.equals("BO")) {
			String wcAppRefNo = application.getWcAppRefNo();
			dynaForm.set("wcAppRefNo", wcAppRefNo);
			request.setAttribute("message", "Application (Reference Nos:"
					+ wcAppRefNo + "," + appRefNo + ")Submitted Successfully");
		}
		successPage = "success";

		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		securitization = null;
		mcgfDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;

		return mapping.findForward(successPage);
	}

	/**
	 * 
	 * added this method by sukumar@path for RSF applications
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getRSFMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFMliInfo",
				"Entered");
		// System.out.println("getRSFMliInfo Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();

		String forward = "";

		HttpSession session = request.getSession(false);
		// session.setAttribute("page","MLIInfo1");
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "RSF");
		// System.out.println("line number241-"+session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));
		// System.out.println("RSF");
		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "7");
		// System.out.println("Line number 244-"+session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
		// dynaForm.set("loanType",ApplicationConstants.RSF_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.TC_APPLICATION);
		// System.out.println(dynaForm.get("loanType"));
		application.setLoanType(ApplicationConstants.TC_APPLICATION);
		// System.out.println("application.getLoanType"+application.getLoanType());
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		// System.out.println("Line number 257-"+bankId);

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			user = null;

			forward = "mliPage";

		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			// System.out.println("Line number 269-"+bankName);
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;
			// System.out.println("Line number274-"+memberId);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			// System.out.println("Line number 278-"+statusFlag);
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFMliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			// System.out.println("Line number 286-"+mcgfsupport);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFMliInfo",
					"mcgfsupport :" + mcgfsupport);
			if (mcgfsupport.equals("Y")) {
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"getRSFMliInfo", "Size :" + ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				ssiRefNosList = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "RSF");

				forward = "rsfForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFMliInfo",
				"Exited");

		// System.out.println("Line number 355-"+forward);
		return mapping.findForward(forward);
	}

	public ActionForward getRSF2MliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRSF2MliInfo", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		String forward = "";
		HttpSession session = request.getSession(false);
		session.setAttribute("APPLICATION_LOAN_TYPE", "RSF");
		session.setAttribute("APPLICATION_TYPE_FLAG", "7");
		dynaForm.set("loanType", "TC");
		application.setLoanType("TC");
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");
		String zoneId = "";
		String branchId = "";
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals("0000")) {
			user = null;
			forward = "mliPage";
		} else {
			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = (new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString();
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member :").append(memberId)
						.append("has been deactivated.").toString());
			Log.log(4, "ApplicationProcessingAction", "getRSF2MliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			Log.log(5,
					"ApplicationProcessingAction",
					"getRSF2MliInfo",
					(new StringBuilder()).append("mcgfsupport :")
							.append(mcgfsupport).toString());
			if (mcgfsupport.equals("Y")) {
				session.setAttribute("MCGF_FLAG", "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(4,
						"ApplicationProcessingAction",
						"getRSF2MliInfo",
						(new StringBuilder()).append("Size :")
								.append(ssiRefNosList.size()).toString());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction", "getRSF2MliInfo",
							"No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				mcgsProcessor = null;
				ssiRefNosList = null;
			} else {
				session.setAttribute("MCGF_FLAG", "NM");
				dynaForm.set("scheme", "RSF2");
				forward = "rsfForward";
			}
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);
			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);
			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);
			statesList = null;
			socialList = null;
			industryNatureList = null;
			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}
		Log.log(4, "ApplicationProcessingAction", "getRSF2MliInfo", "Exited");
		return mapping.findForward(forward);
	}

	public ActionForward getRSF2WcMliInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRSFWcMliInfo", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		String forward = "";
		HttpSession session = request.getSession(false);
		session.setAttribute("APPLICATION_LOAN_TYPE", "RSF");
		session.setAttribute("APPLICATION_TYPE_FLAG", "9");
		dynaForm.set("loanType", "WC");
		application.setLoanType("WC");
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");
		String zoneId = "";
		String branchId = "";
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals("0000")) {
			user = null;
			forward = "mliPage";
		} else {
			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = (new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString();
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member :").append(memberId)
						.append("has been deactivated.").toString());
			Log.log(4, "ApplicationProcessingAction", "getRSFWcMliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			Log.log(5,
					"ApplicationProcessingAction",
					"getRSFWcMliInfo",
					(new StringBuilder()).append("mcgfsupport :")
							.append(mcgfsupport).toString());
			if (mcgfsupport.equals("Y")) {
				session.setAttribute("MCGF_FLAG", "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(4,
						"ApplicationProcessingAction",
						"getRSFWcMliInfo",
						(new StringBuilder()).append("Size :")
								.append(ssiRefNosList.size()).toString());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction",
							"getRSFWcMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				mcgsProcessor = null;
				ssiRefNosList = null;
			} else {
				session.setAttribute("MCGF_FLAG", "NM");
				dynaForm.set("scheme", "RSF2");
				forward = "rsfForward";
			}
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);
			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);
			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);
			statesList = null;
			socialList = null;
			industryNatureList = null;
			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}
		Log.log(4, "ApplicationProcessingAction", "getRSFWcMliInfo", "Exited");
		return mapping.findForward(forward);
	}

	/**
	 * 
	 * added this method by sukumar@path for RSF application for WC
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getRSFWcMliInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcMliInfo",
				"Entered");
		// System.out.println("getRSFWcMliInfo Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();

		String forward = "";

		HttpSession session = request.getSession(false);
		// session.setAttribute("page","MLIInfo1");
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "RSF");
		// System.out.println("line number384-"+session.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));
		// System.out.println("RSF");
		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "9");
		// System.out.println("Line number 387-"+session.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
		// dynaForm.set("loanType",ApplicationConstants.RSF_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.WC_APPLICATION);
		// System.out.println(dynaForm.get("loanType"));
		application.setLoanType(ApplicationConstants.WC_APPLICATION);
		// System.out.println("application.getLoanType"+application.getLoanType());
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		// System.out.println("Line number 401-"+bankId);

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			user = null;

			forward = "mliPage";

		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			// System.out.println("Line number 413-"+bankName);
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;
			// System.out.println("Line number418-"+memberId);
			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			// System.out.println("Line number 422-"+statusFlag);
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcMliInfo",
					"Entered to get mliinfo object");
			String mcgfsupport = mliInfo.getMcgf();
			// System.out.println("Line number 430-"+mcgfsupport);
			Log.log(Log.DEBUG, "ApplicationProcessingAction",
					"getRSFWcMliInfo", "mcgfsupport :" + mcgfsupport);
			if (mcgfsupport.equals("Y")) {
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"getRSFWcMliInfo", "Size :" + ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFWcMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				ssiRefNosList = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "RSF");

				forward = "rsfForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcMliInfo",
				"Exited");

		// System.out.println("Line number 499-"+forward);
		return mapping.findForward(forward);
	}

	/**
	 * 
	 * added this method by sukumar@path for RSF application for Both Term loan
	 * and Working capital application
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getRsfBothMliInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRsfBothMliInfo",
				"Entered");
		// System.out.println("Line number 527-getRsfBothMliInfo Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application application = new Application();

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "RSF");

		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "10");
		application.setLoanType(ApplicationConstants.BOTH_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.BOTH_APPLICATION);

		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String forward = "";
		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {

			forward = "mliPage";
		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			String mcgfsupport = mliInfo.getMcgf();
			if (mcgfsupport.equals("Y")) {
				dynaForm.set("scheme", "MCGS");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRsfBothMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				ssiRefNosList = null;

				mcgsProcessor = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "RSF");

				forward = "rsfForward";
			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;

		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRsfBothMliInfo",
				"Exited");

		return mapping.findForward(forward);
	}

	public ActionForward getRsf2BothMliInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRsfBothMliInfo",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		HttpSession session = request.getSession(false);
		session.setAttribute("APPLICATION_LOAN_TYPE", "RSF");
		session.setAttribute("APPLICATION_TYPE_FLAG", "10");
		application.setLoanType("BO");
		dynaForm.set("loanType", "BO");
		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");
		String forward = "";
		String zoneId = "";
		String branchId = "";
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals("0000")) {
			forward = "mliPage";
		} else {
			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = (new StringBuilder()).append(bankId)
					.append(zoneId).append(branchId).toString();
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member :").append(memberId)
						.append("has been deactivated.").toString());
			String mcgfsupport = mliInfo.getMcgf();
			if (mcgfsupport.equals("Y")) {
				dynaForm.set("scheme", "MCGS");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session.setAttribute("MCGF_FLAG", "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction",
							"getRsfBothMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				ssiRefNosList = null;
				mcgsProcessor = null;
			} else {
				session.setAttribute("MCGF_FLAG", "NM");
				dynaForm.set("scheme", "RSF");
				forward = "rsfForward";
			}
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);
			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);
			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);
			statesList = null;
			socialList = null;
			industryNatureList = null;
			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;
		}
		Log.log(4, "ApplicationProcessingAction", "getRsfBothMliInfo", "Exited");
		return mapping.findForward(forward);
	}

	/**
	 * 
	 * added this method by sukumar@path for provision of MLIs to re-apply
	 * rejected applications
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward rejectApplicationInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"rejectApplicationInput", "Entered");

		APForm dynaForm = (APForm) form;
		// System.out.println("Application Processing Action-Reject Application Entered:");

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"rejectApplicationInput", "Exited");
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
	public ActionForward rejectApplication(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "rejectApplication",
				"Entered");
		// System.out.println("ApplicationProcessingAction"+"method:rejectApplication() Entered:");

		APForm actionForm = (APForm) form;
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		ReportManager reportManager = new ReportManager();
		ArrayList cgpans = new ArrayList();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		// System.out.println("userId:"+user.getUserId());
		String cgpan = null;
		// cgpan = request.getParameter("cgpan");
		cgpan = actionForm.getCgpan();
		String remarks = actionForm.getRemarks();
		
		//cgpans = reportManager.getAllCgpans();
		String newCgpan = (String) cgpan.toUpperCase();
		// System.out.println("newCgpan:"+newCgpan);
		appProcessor.rejectApplication(newCgpan, remarks, userId);
		/*if (newCgpan != null && cgpans.contains(newCgpan)) {
			appProcessor.rejectApplication(newCgpan, remarks, userId);
			request.setAttribute("message", "CGPAN : " + newCgpan
					+ " Cancelled Successfully:");
		} else if (newCgpan.equals("") || newCgpan == null
				|| (!cgpans.contains(newCgpan))) {
			throw new NoDataException("Enter a Valid CGPAN");
		}*/

		actionForm.setCgpan(null);
		actionForm.setRemarks(null);
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"ApplicationProcessingAction", "Exited");
		// System.out.println("Application Status changed Successfully:");
		return mapping.findForward("success");

	}

	/*
	 * This method sets the loan type for a composite loan application
	 */

	public ActionForward getCCMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getCCMliInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application application = new Application();

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "CC");

		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "8");

		application.setLoanType(ApplicationConstants.CC_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.CC_APPLICATION);

		dynaForm.set("compositeLoan", "Y");
		application.setCompositeLoan("Y");

		String forward = "";
		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {

			user = null;

			forward = "mliPage";
		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			String mcgfsupport = mliInfo.getMcgf();
			if (mcgfsupport.equals("Y")) {
				dynaForm.set("scheme", "MCGS");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getCCMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				ssiRefNosList = null;

				mcgsProcessor = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "CGFSI");

				forward = "ccForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;

		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getCCMliInfo",
				"Exited");

		return mapping.findForward(forward);
	}

	/*
	 * This method sets the loan type for a working capital loan application
	 */

	public ActionForward getWCMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getWCMliInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application application = new Application();

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "WC");

		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "9");
		application.setLoanType(ApplicationConstants.WC_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.WC_APPLICATION);

		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String forward = "";
		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {

			forward = "mliPage";
		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			String mcgfsupport = mliInfo.getMcgf();
			if (mcgfsupport.equals("Y")) {

				dynaForm.set("scheme", "MCGS");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getWCMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				ssiRefNosList = null;

				mcgsProcessor = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "CGFSI");

				forward = "wcForward";

			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;

		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getWCMliInfo",
				"Exited");

		return mapping.findForward(forward);
	}

	/*
	 * This method sets the loan type for a Both TC and WC application
	 */

	public ActionForward getBothMliInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getBothMliInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application application = new Application();

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.APPLICATION_LOAN_TYPE, "BO");

		session.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "10");
		application.setLoanType(ApplicationConstants.BOTH_APPLICATION);
		dynaForm.set("loanType", ApplicationConstants.BOTH_APPLICATION);

		dynaForm.set("compositeLoan", "N");
		application.setCompositeLoan("N");

		String forward = "";
		String zoneId = "";
		String branchId = "";

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {

			forward = "mliPage";
		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			String bankName = mliInfo.getBankName();
			bankId = mliInfo.getBankId();
			branchId = mliInfo.getBranchId();
			zoneId = mliInfo.getZoneId();
			String memberId = bankId + zoneId + branchId;

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member :" + memberId
						+ "has been deactivated.");
			}

			String mcgfsupport = mliInfo.getMcgf();
			if (mcgfsupport.equals("Y")) {
				dynaForm.set("scheme", "MCGS");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getBothMliInfo", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}
				forward = "ssiRefNosPage";

				ssiRefNosList = null;

				mcgsProcessor = null;

			} else {

				session.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				dynaForm.set("scheme", "CGFSI");

				forward = "bothForward";
			}

			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			ArrayList industryNatureList = getIndustryNature();
			dynaForm.set("industryNatureList", industryNatureList);

			statesList = null;

			socialList = null;

			industryNatureList = null;

			mliInfo = null;
			bankId = null;
			zoneId = null;
			branchId = null;

		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "getBothMliInfo",
				"Exited");

		return mapping.findForward(forward);
	}

	/*
	 * This method sets the loan type for a Additional term loan application
	 */

	public ActionForward getAddtlTCInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getAddtlTCInfo",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "TCE");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) 
		{
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getAddtlTCInfo",
				"Exited");

		return mapping.findForward("appDetailPage");
	}

	/*
	 * This method sets the loan type for a Working Capital Enhancement
	 * application
	 */

	public ActionForward getEnhanceWCInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getEnhanceWCInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "WCE");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;
		memberId = null;
		bankId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getEnhanceWCInfo",
				"Exited");

		return mapping.findForward("appDetailPage");
	}

	/*
	 * This method sets the loan type for a WC Renewal application
	 */

	public ActionForward getRenewWCInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRenewWCInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "WCR");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRenewWCInfo",
				"Exited");

		return mapping.findForward("appDetailPage");
	}

	/*
	 * This method sets the loan type for a application to be modified
	 */

	public ActionForward getModifyInfoold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getModifyInfo",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);

		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "MA");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getModifyInfo",
				"Exited");

		return mapping.findForward("modifyDetailPage");
	}

	public ActionForward getModifyInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getModifyInfo", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		HttpSession session = request.getSession(false);
		session.removeAttribute("APPLICATION_LOAN_TYPE");
		session.setAttribute("APPLICATION_TYPE", "MA");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);
		zoneId = null;
		branchId = null;
		Log.log(4, "ApplicationProcessingAction", "getModifyInfo", "Exited");
		return mapping.findForward("modifyDetailPage");
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
	public ActionForward modifyAppBranchName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "modifyAppBranchName",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "modifyAppBranchName",
				"Exited");

		return mapping.findForward("modifyDetailPage");
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
	public ActionForward aftermodifyAppBranchName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"aftermodifyAppBranchName", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession session = request.getSession(false);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		GMProcessor gmProcessor = new GMProcessor();

		String mliBranchName = "";
		String appRefNo = "";
		String memberId = (String) dynaForm.get("selectMember");
		// System.out.println("Member Id:"+memberId);
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (!(memberids.contains(memberId))) {
			throw new NoMemberFoundException("Member Id :" + memberId
					+ " does not exist in the database.");
		}

		// System.out.println("Member Id:"+memberId);

		String cgpan = (String) dynaForm.get("cgpan");
		// System.out.println("CGPAN:"+cgpan);
		ArrayList borrowerIds = new ArrayList();
		borrowerIds = gmProcessor.getBorrowerIds(memberId);

		// cgpan
		if ((!cgpan.equals(""))) {
			String bIdForThisCgpan = processor.getBorowwerForCGPAN(cgpan);
			Log.log(Log.DEBUG, "GMAction", "submitClosureDetails",
					" Bid For Pan - " + bIdForThisCgpan);
			if (!(borrowerIds.contains(bIdForThisCgpan))) {
				throw new NoDataException(cgpan + "is not a valid Cgpan for "
						+ "the Member Id :" + memberId
						+ ". Please enter correct Cgpan");
			}
		}
		// System.out.println("CGPAN:"+cgpan);
		mliBranchName = applicationDAO.getBranchName(cgpan);
		dynaForm.set("mliBranchName", mliBranchName);
		appRefNo = applicationDAO.getAppRefNo(cgpan);
		dynaForm.set("appRefNo", appRefNo);
		// System.out.println("Existing Branch Name:"+mliBranchName);
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"aftermodifyAppBranchName", "Exited");

		return mapping.findForward("modifyDetailPage");
	}

	public ActionForward submitmodifyAppBranchName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"submitmodifyAppBranchName", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession session = request.getSession(false);
		ApplicationDAO applicationDAO = new ApplicationDAO();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String memberId = (String) dynaForm.get("selectMember");
		String appRefNo = (String) dynaForm.get("appRefNo");
		String cgpan = (String) dynaForm.get("cgpan");
		String mliBranchName = (String) dynaForm.get("mliBranchName");
		applicationDAO.updateBranchNameForCgpan(memberId, appRefNo, cgpan,
				mliBranchName, userId);
		request.setAttribute("message", "<b> Modify Branch Name request for "
				+ cgpan + " updated successfully.<b><br>");

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"submitmodifyAppBranchName", "Exited");

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
	public ActionForward getReapplyRejectedApps(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getReapplyRejectedApps", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);

		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "MA");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}

		dynaForm.set("selectMember", memberId);
		dynaForm.set("mliID", memberId);
		dynaForm.set("bankId", bankId);

		zoneId = null;
		branchId = null;

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getReapplyRejectedApps", "Exited");

		return mapping.findForward("modifyDetailPage");
	}

	/*
	 * This method retrieves all the states from the database and populates in
	 * the screen
	 */
	public ActionForward getApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getApps", "Entered");

		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		Registration registration = new Registration();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;

		String forward = "";

		String memberId = (String) dynaForm.get("selectMember");

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
				"MEmber Id :" + memberId);

		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
					"mli Info mcgf.. :" + mliInfo.getSupportMCGF());
			if ((mliInfo.getSupportMCGF()).equals("Y")) {
				Log.log(Log.DEBUG, "ApplicationProcessingAction", "getApps",
						"MCGF Flag entered..");

				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);

				// System.out.println("ssi ref size :" + ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction", "getApps",
							"No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;

			} else {
				dynaForm.set("scheme", "CGFSI");

				session1.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				forward = setApps(applicationType);
			}
		} else {

			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);

		dynaForm.set("dcHandicrafts", "N");
		dynaForm.set("handiCrafts", "N");

		dynaForm.set("dcHandlooms", "N");

		dynaForm.set("previouslyCovered", "");

		statesList = null;

		socialList = null;

		industryNatureList = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getApps", "Exited");

		return mapping.findForward(forward);

	}

	/**
	 * This method returns an arraylist of states from the database
	 */
	private Collection getStateList() {
		ArrayList statesList = null;
		try {
			Administrator admin = new Administrator();
			statesList = admin.getAllStates();
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "ApplicationProcessingAction", "getStateList",
					e.getMessage());
			Log.logException(e);

		}
		return statesList;

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
	public ActionForward getRSFApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFApps",
				"Entered");
		// System.out.println("Line number 1196 getRSFApps Entered");
		HttpSession session1 = request.getSession(false);

		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		// System.out.println("Line number 1200 applicationType:"+applicationType);
		Registration registration = new Registration();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;

		String forward = "";

		String memberId = (String) dynaForm.get("selectMember");

		// System.out.println("line number 1211-"+memberId);

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFApps",
				"MEmber Id :" + memberId);

		ArrayList rsfParticipatingBanks = getAllRsfParticipatingBanks();

		if (!(rsfParticipatingBanks.contains(memberId.substring(0, 4)))) {
			throw new NoDataException("Member Id:" + memberId
					+ "  has not been registered under RSF.");
		}
		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFApps",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			// System.out.println("Line number 1236 Status Flag:"+statusFlag);
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFApps",
					"mli Info mcgf.. :" + mliInfo.getSupportMCGF());
			if ((mliInfo.getSupportMCGF()).equals("Y")) {
				Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFApps",
						"MCGF Flag entered..");
				// System.out.println("ApplicationProcessingAction"+"getRSFApps"+"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				// System.out.println("Line number 1259 "+bankName);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);

				// System.out.println("Line number 1266 - ssi ref size :" +
				// ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFApps", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;

			} else {
				// System.out.println("Line number 1288-RSF");
				dynaForm.set("scheme", "RSF");

				session1.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				forward = setApps(applicationType);
			}
		} else {

			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);

		statesList = null;

		socialList = null;

		industryNatureList = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFApps", "Exited");
		// System.out.println("getRSFApps Exited"+forward);
		return mapping.findForward(forward);

	}

	public ActionForward getRSF2Apps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRSF2Apps", "Entered");
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute("APPLICATION_LOAN_TYPE");
		Registration registration = new Registration();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String forward = "";
		String memberId = (String) dynaForm.get("selectMember");
		Log.log(5, "ApplicationProcessingAction", "getRSF2Apps",
				(new StringBuilder()).append("MEmber Id :").append(memberId)
						.toString());
		ArrayList rsfParticipatingBanks = getAllRsf2ParticipatingBanks();
		if (!rsfParticipatingBanks.contains(memberId.substring(0, 4)))
			throw new NoDataException((new StringBuilder())
					.append("Member Id:").append(memberId)
					.append("  has not been registered1 under RSF2.")
					.toString());
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(memberId))
			throw new NoMemberFoundException("The Member ID does not exist");
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		if (mliInfo != null) {
			Log.log(5, "ApplicationProcessingAction", "getRSF2Apps",
					(new StringBuilder()).append("mli Info.. :")
							.append(mliInfo).toString());
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member Id:").append(memberId)
						.append("  has been deactivated.").toString());
			Log.log(5,
					"ApplicationProcessingAction",
					"getRSFApps",
					(new StringBuilder()).append("mli Info mcgf.. :")
							.append(mliInfo.getSupportMCGF()).toString());
			if (mliInfo.getSupportMCGF().equals("Y")) {
				Log.log(5, "ApplicationProcessingAction", "getRSF2Apps",
						"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute("MCGF_FLAG", "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction", "getRSFApps",
							"No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;
			} else {
				dynaForm.set("scheme", "RSF2");
				session1.setAttribute("MCGF_FLAG", "NM");
				forward = setApps(applicationType);
			}
		} else {
			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;
		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);
		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);
		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);
		statesList = null;
		socialList = null;
		industryNatureList = null;
		applicationType = null;
		Log.log(4, "ApplicationProcessingAction", "getRSF2Apps", "Exited");
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
	public ActionForward getPCGSApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getPCGSApps",
				"Entered");
		HttpSession session1 = request.getSession(false);

		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		Registration registration = new Registration();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;

		String forward = "";

		String memberId = (String) dynaForm.get("selectMember");
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getPCGSApps",
				"MEmber Id :" + memberId);

		/*
		 * ArrayList rsfParticipatingBanks=getAllRsfParticipatingBanks();
		 * 
		 * if(!(rsfParticipatingBanks.contains(memberId.substring(0,4)))) {
		 * throw new NoDataException("Member Id:" + memberId +
		 * "  has not been registered under RSF."); }
		 */
		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getPCGSApps",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getPCGSApps",
					"mli Info mcgf.. :" + mliInfo.getSupportMCGF());
			dynaForm.set("scheme", "PCGS");

			session1.setAttribute(SessionConstants.MCGF_FLAG, "NM");

			forward = setApps(applicationType);

		} else {

			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);

		ArrayList industrySectorList = getIndustrySector();
		// System.out.println("industrySectorList:"+industrySectorList.size());
		dynaForm.set("industrySectorList", industrySectorList);

		statesList = null;

		socialList = null;

		industryNatureList = null;

		industrySectorList = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getPCGSApps",
				"Exited");
		// System.out.println("Forward:"+forward);

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
	public ActionForward getRSFBothApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcApps",
				"Entered");
		// System.out.println("Line number 1498 getRSFBothApps Entered");
		HttpSession session1 = request.getSession(false);

		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		// System.out.println("Line number 1502 applicationType:"+applicationType);
		Registration registration = new Registration();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;

		String forward = "";

		String memberId = (String) dynaForm.get("selectMember");

		// System.out.println("line number 1513-"+memberId);

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFBothApps",
				"MEmber Id :" + memberId);

		ArrayList rsfParticipatingBanks = getAllRsfParticipatingBanks();

		if (!(rsfParticipatingBanks.contains(memberId.substring(0, 4)))) {
			throw new NoDataException("Member Id:" + memberId
					+ "  has not been registered under RSF.");
		}

		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFBothApps",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			// System.out.println("Line number 1236 Status Flag:"+statusFlag);
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFBothApps",
					"mli Info mcgf.. :" + mliInfo.getSupportMCGF());
			if ((mliInfo.getSupportMCGF()).equals("Y")) {
				Log.log(Log.DEBUG, "ApplicationProcessingAction",
						"getRSFBothApps", "MCGF Flag entered..");
				// System.out.println("ApplicationProcessingAction"+"getRSFBothApps"+"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				// System.out.println("Line number 1561 "+bankName);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);

				// System.out.println("Line number 1568 - ssi ref size :" +
				// ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFBothApps", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;

			} else {
				// System.out.println("Line number 1590-RSF");
				dynaForm.set("scheme", "RSF");

				session1.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				forward = setApps(applicationType);
			}
		} else {

			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);

		statesList = null;

		socialList = null;

		industryNatureList = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFBothApps",
				"Exited");
		// System.out.println("getRSFBothApps Exited"+forward);
		return mapping.findForward(forward);

	}

	public ActionForward getRSF2BothApps(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRSFWcApps", "Entered");
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute("APPLICATION_LOAN_TYPE");
		Registration registration = new Registration();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String forward = "";
		String memberId = (String) dynaForm.get("selectMember");
		Log.log(5, "ApplicationProcessingAction", "getRSFBothApps",
				(new StringBuilder()).append("MEmber Id :").append(memberId)
						.toString());
		ArrayList rsfParticipatingBanks = getAllRsfParticipatingBanks();
		if (!rsfParticipatingBanks.contains(memberId.substring(0, 4)))
			throw new NoDataException((new StringBuilder())
					.append("Member Id:").append(memberId)
					.append("  has not been registered under RSF2both.")
					.toString());
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(memberId))
			throw new NoMemberFoundException("The Member ID does not exist");
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		if (mliInfo != null) {
			Log.log(5, "ApplicationProcessingAction", "getRSFBothApps",
					(new StringBuilder()).append("mli Info.. :")
							.append(mliInfo).toString());
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member Id:").append(memberId)
						.append("  has been deactivated.").toString());
			Log.log(5,
					"ApplicationProcessingAction",
					"getRSFBothApps",
					(new StringBuilder()).append("mli Info mcgf.. :")
							.append(mliInfo.getSupportMCGF()).toString());
			if (mliInfo.getSupportMCGF().equals("Y")) {
				Log.log(5, "ApplicationProcessingAction", "getRSFBothApps",
						"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute("MCGF_FLAG", "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction", "getRSFBothApps",
							"No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;
			} else {
				dynaForm.set("scheme", "RSF2");
				session1.setAttribute("MCGF_FLAG", "NM");
				forward = setApps(applicationType);
			}
		} else {
			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;
		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);
		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);
		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);
		statesList = null;
		socialList = null;
		industryNatureList = null;
		applicationType = null;
		Log.log(4, "ApplicationProcessingAction", "getRSFBothApps", "Exited");
		return mapping.findForward(forward);
	}

	/* For Getting RSF WC application */
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getRSFWcApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcApps",
				"Entered");
		// System.out.println("Line number 1498 getRSFWcApps Entered");
		HttpSession session1 = request.getSession(false);

		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		// System.out.println("Line number 1502 applicationType:"+applicationType);
		Registration registration = new Registration();

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		DynaActionForm dynaForm = (DynaActionForm) form;

		String forward = "";

		String memberId = (String) dynaForm.get("selectMember");

		// System.out.println("line number 1513-"+memberId);

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFWcApps",
				"MEmber Id :" + memberId);

		ArrayList rsfParticipatingBanks = getAllRsfParticipatingBanks();

		if (!(rsfParticipatingBanks.contains(memberId.substring(0, 4)))) {
			throw new NoDataException("Member Id:" + memberId
					+ "  has not been registered under RSF.");
		}

		String bankId = memberId.substring(0, 4); // getting the bank id
		String zoneId = memberId.substring(4, 8); // getting the zone id
		String branchId = memberId.substring(8, 12); // getting the branch id

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!(memberIds.contains(memberId))) {
			throw new NoMemberFoundException("The Member ID does not exist");
		}

		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);

		if (mliInfo != null) {

			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFWcApps",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			// System.out.println("Line number 1236 Status Flag:"+statusFlag);
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "getRSFWcApps",
					"mli Info mcgf.. :" + mliInfo.getSupportMCGF());
			if ((mliInfo.getSupportMCGF()).equals("Y")) {
				Log.log(Log.DEBUG, "ApplicationProcessingAction",
						"getRSFWcApps", "MCGF Flag entered..");
				// System.out.println("ApplicationProcessingAction"+"getRSFWcApps"+"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute(SessionConstants.MCGF_FLAG, "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0) {
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				} else {

					dynaForm.set("participatingBanks", participatingBanks);
				}

				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				// System.out.println("Line number 1561 "+bankName);
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");

				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);

				// System.out.println("Line number 1568 - ssi ref size :" +
				// ssiRefNosList.size());
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"getRSFWcApps", "No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");

				} else {

					dynaForm.set("allSsiRefNos", ssiRefNosList);

				}

				forward = "ssiRefNosPage";

				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;

			} else {
				// System.out.println("Line number 1590-RSF");
				dynaForm.set("scheme", "RSF");

				session1.setAttribute(SessionConstants.MCGF_FLAG, "NM");

				forward = setApps(applicationType);
			}
		} else {

			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);

		statesList = null;

		socialList = null;

		industryNatureList = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getRSFWcApps",
				"Exited");
		// System.out.println("getRSFWcApps Exited"+forward);
		return mapping.findForward(forward);

	}

	public ActionForward getRSF2WcApps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ApplicationProcessingAction", "getRSFWcApps", "Entered");
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute("APPLICATION_LOAN_TYPE");
		Registration registration = new Registration();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String forward = "";
		String memberId = (String) dynaForm.get("selectMember");
		Log.log(5, "ApplicationProcessingAction", "getRSFWcApps",
				(new StringBuilder()).append("MEmber Id :").append(memberId)
						.toString());
		ArrayList rsfParticipatingBanks = getAllRsf2ParticipatingBanks();
		if (!rsfParticipatingBanks.contains(memberId.substring(0, 4)))
			throw new NoDataException((new StringBuilder())
					.append("Member Id:").append(memberId)
					.append("  has not  been registered under RSF2wc.")
					.toString());
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		Vector memberIds = cpProcessor.getAllMemberIds();
		if (!memberIds.contains(memberId))
			throw new NoMemberFoundException("The Member ID does not exist");
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		if (mliInfo != null) {
			Log.log(5, "ApplicationProcessingAction", "getRSFWcApps",
					(new StringBuilder()).append("mli Info.. :")
							.append(mliInfo).toString());
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I"))
				throw new NoDataException((new StringBuilder())
						.append("Member Id:").append(memberId)
						.append("  has been deactivated.").toString());
			Log.log(5,
					"ApplicationProcessingAction",
					"getRSFWcApps",
					(new StringBuilder()).append("mli Info mcgf.. :")
							.append(mliInfo.getSupportMCGF()).toString());
			if (mliInfo.getSupportMCGF().equals("Y")) {
				Log.log(5, "ApplicationProcessingAction", "getRSFWcApps",
						"MCGF Flag entered..");
				MCGSProcessor mcgsProcessor = new MCGSProcessor();
				session1.setAttribute("MCGF_FLAG", "M");
				ArrayList participatingBanks = mcgsProcessor
						.getAllParticipatingBanks(memberId);
				if (participatingBanks == null
						|| participatingBanks.size() == 0)
					throw new NoDataException(
							"Participating Banks are not available for this member.Hence Application cannot be submitted.");
				dynaForm.set("participatingBanks", participatingBanks);
				dynaForm.set("participatingBanks", participatingBanks);
				String bankName = mliInfo.getBankName();
				dynaForm.set("mcgfName", bankName);
				dynaForm.set("mcgfId", memberId);
				dynaForm.set("scheme", "MCGS");
				ArrayList ssiRefNosList = appProcessor
						.getSsiRefNosForMcgf(memberId);
				if (ssiRefNosList == null || ssiRefNosList.size() == 0) {
					Log.log(4, "ApplicationProcessingAction", "getRSFWcApps",
							"No Borrowers");
					throw new NoDataException(
							"There are no borrowers for this Member");
				}
				dynaForm.set("allSsiRefNos", ssiRefNosList);
				forward = "ssiRefNosPage";
				mcgsProcessor = null;
				bankName = null;
				participatingBanks = null;
			} else {
				dynaForm.set("scheme", "RSF2");
				session1.setAttribute("MCGF_FLAG", "NM");
				forward = setApps(applicationType);
			}
		} else {
			throw new NoMemberFoundException("No Member Details Found");
		}
		mliInfo = null;
		cpProcessor = null;
		registration = null;
		memberIds = null;
		memberId = null;
		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);
		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);
		ArrayList industryNatureList = getIndustryNature();
		dynaForm.set("industryNatureList", industryNatureList);
		statesList = null;
		socialList = null;
		industryNatureList = null;
		applicationType = null;
		Log.log(4, "ApplicationProcessingAction", "getRSFWcApps", "Exited");
		return mapping.findForward(forward);
	}

	private ArrayList getAllRsf2ParticipatingBanks() {
		ArrayList allRsfParticipatingBanks = null;
		try {
			Administrator admin = new Administrator();
			allRsfParticipatingBanks = admin.getAllRsf2ParticipatingBanks();
			admin = null;
		} catch (Exception e) {
			Log.log(2, "ApplicationProcessingAction",
					"getAllRsfParticipatingBanks", e.getMessage());
			Log.logException(e);
		}
		return allRsfParticipatingBanks;
	}

	private ArrayList getAllRsfParticipatingBanks() {
		ArrayList allRsfParticipatingBanks = null;
		try {
			Administrator admin = new Administrator();
			allRsfParticipatingBanks = admin.getAllRsfParticipatingBanks();
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "ApplicationProcessingAction",
					"getAllRsfParticipatingBanks", e.getMessage());
			Log.logException(e);

		}
		return allRsfParticipatingBanks;
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
			Log.log(Log.ERROR, "ApplicationProcessingAction",
					"getSocialCategoryList", e.getMessage());
			Log.logException(e);

		}
		return socialCategoryList;

	}

	/**
	 * This method returns an arraylist of industry nature from the database
	 */
	private ArrayList getIndustryNature() {
		ArrayList industryNatureList = null;
		try {
			Administrator admin = new Administrator();
			industryNatureList = admin.getAllIndustryNature();
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "ApplicationProcessingAction",
					"getSocialCategoryList", e.getMessage());
			Log.logException(e);

		}
		return industryNatureList;

	}

	private ArrayList getIndustrySector() {
		ArrayList industrySectorList = null;
		try {
			Administrator admin = new Administrator();
			industrySectorList = admin.getIndustrySectors();
			admin = null;

		} catch (Exception e) {
			Log.log(Log.ERROR, "ApplicationProcessingAction",
					"getIndustrySector", e.getMessage());
			Log.logException(e);

		}
		return industrySectorList;

	}

	/*
	 * This method retrieves all the districts for the state selected and
	 * returns it to the screen
	 */

	public ActionForward getDistricts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getDistricts",
				"Entered");
		// System.out.println("Application Processing Action getDistricts() Entered");
		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		String stateName = (String) dynaForm.get("state");
		// System.out.println("State Name :"+stateName);
		ArrayList districtList = admin.getAllDistricts(stateName);
		dynaForm.set("districtList", districtList);
		// System.out.println("District List size:"+districtList.size());
		request.getSession().setAttribute("districtList", districtList);
		// response.setContentType("text/html");

		request.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "15");
		PrintWriter out = response.getWriter();
		String test = makeOutputString(districtList);
		out.print(test);
		String forward = setApps(applicationType);

		admin = null;
		districtList = null;
		applicationType = null;
		stateName = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getDistricts",
				"Exited");

		return mapping.findForward(forward);

	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public String makeOutputString(ArrayList districtList) {
		// System.out.println("makeOutputString entered");
		String outputString = "Select";
		for (int i = 0; i < districtList.size(); i++) {
			// outputString=outputString+districtList.get(i)+";"+districtList.get(i)+"||";
			outputString = outputString + "||" + districtList.get(i);
		}
		// System.out.println("the String is :-->"+outputString);

		return outputString;

	}

	/**
	 * 
	 * added by sukumar@path on 16-Sep-2010 for dynamically displays city names
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward getCityNames(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getCityNames",
				"Entered");
		// System.out.println("Application Processing Action getCityNames() Entered");
		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		String cityName = (String) dynaForm.get("city");
		// System.out.println("City Name :"+cityName);
		ArrayList cityList = admin.getCityNames(cityName.toUpperCase());
		dynaForm.set("cityList", cityList);
		// System.out.println("city List size:"+cityList.size());
		request.getSession().setAttribute("cityList", cityList);
		// response.setContentType("text/html");

		request.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "15");
		PrintWriter out = response.getWriter();
		String test = makeOutputString(cityList);
		out.print(test);
		String forward = setApps(applicationType);

		admin = null;
		cityList = null;
		applicationType = null;
		cityName = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getCityNames",
				"Exited");

		return mapping.findForward(forward);

	}

	/*
	 * This method returns the industry sector for the industry nature selected
	 */
	public ActionForward getIndustrySector(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getIndustrySector",
				"Entered");

		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		ArrayList industrySectorList = new ArrayList();

		String industryNature = (String) dynaForm.get("industryNature");

		if (!(industryNature.equals(""))) {
			industrySectorList = admin.getIndustrySectors(industryNature);

		} else {

			industrySectorList.clear();
		}
		dynaForm.set("industrySectorList", industrySectorList);

		request.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "16");

		String forward = setApps(applicationType);

		admin = null;
		industryNature = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getIndustrySector",
				"Exited");

		return mapping.findForward(forward);
	}

	public ActionForward getTypeOfActivity(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "getIndustrySector",
				"Entered");

		Administrator admin = new Administrator();
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		ArrayList industrySectorList = new ArrayList();

		String industrySector = (String) dynaForm.get("industrySector");

		if ((industrySector.equals("HANDLOOM WEAVING"))) {
			dynaForm.set("activityType", industrySector);

		} else {

			dynaForm.set("activityType", "");
		}

		request.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "16");

		String forward = setApps(applicationType);

		admin = null;

		applicationType = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "getIndustrySector",
				"Exited");

		return mapping.findForward(forward);
	}

	/*
	 * This method sets the loan type depending on the application
	 */

	private String setApps(String applicationType) {
		String forward = null;

		if (applicationType.equals(ApplicationConstants.TC_APPLICATION)) {
			forward = "tcForward";
			application.setLoanType(ApplicationConstants.TC_APPLICATION);
		} else if (applicationType.equals(ApplicationConstants.CC_APPLICATION)) {
			forward = "ccForward";
			application.setLoanType(ApplicationConstants.CC_APPLICATION);
		} else if (applicationType.equals(ApplicationConstants.WC_APPLICATION)) {
			forward = "wcForward";
			application.setLoanType(ApplicationConstants.WC_APPLICATION);
		} else if (applicationType
				.equals(ApplicationConstants.BOTH_APPLICATION)) {
			forward = "bothForward";
			application.setLoanType(ApplicationConstants.BOTH_APPLICATION);
		} else if (applicationType.equals(ApplicationConstants.RSF_APPLICATION)) {
			forward = "rsfForward";
			application.setLoanType(ApplicationConstants.RSF_APPLICATION);
		} else if (applicationType
				.equals(ApplicationConstants.PCGS_APPLICATION)) {
			forward = "pcgsForward";
			application.setLoanType(ApplicationConstants.PCGS_APPLICATION);
		}

		return forward;
	}

	/**
	 * This method is called for Addtl term Loan,WC Enhancement and WC Renewal
	 * and Modify Application It retrieves either the application or the list of
	 * cgpans depending on the parameters passed
	 */

	public ActionForward showCgpanList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Entered");
		System.out.println("*******debug************");

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator admin = new Administrator();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		Application application = new Application();
		MCGFDetails mcgfDetails = new MCGFDetails();

		String cgpan = "";// cgpan
		String mliId = "";
		String cgbid = "";

		BeanUtils.populate(ssiDetails, dynaForm.getMap()); // for setting cgbid

		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());
		application.setBorrowerDetails(borrowerDetails);

		BeanUtils.populate(application, dynaForm.getMap()); // for setting cgpan
															// and apprefno

		HttpSession appSession = request.getSession(false);

		String applicationType = (String) appSession
				.getAttribute(SessionConstants.APPLICATION_TYPE);
		System.out.println("application loan type :" + applicationType + "S");
		if (applicationType.equals("APP") || applicationType.equals("EL")
				|| applicationType.equals("DUP")) {
			dynaForm.initialize(mapping);
			application = new Application();
			borrowerDetails = new BorrowerDetails();
			ssiDetails = new SSIDetails();

			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);
			mliId = null;

		}

		/*
		 * This loop checks whether a cgpan / application reference number link
		 * has been clicked for the list of cgpans and app ref nos for modify
		 * application / enhancement / additional term loan /renewal application
		 */

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"mli id :" + dynaForm.get("selectMember"));
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"cgpan :" + application.getCgpan());
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"flag :" + (String) request.getParameter("flag"));

		// if (((dynaForm.get("cgpan")==null) ||
		// (dynaForm.get("cgpan").equals("")))&&
		// ((dynaForm.get("appRefNo")==null) ||
		// (dynaForm.get("appRefNo").equals(""))))
		// {
		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Entering when cgpan and ref no are null");

		if (((applicationType.equals("MA")) || (applicationType.equals("TCE"))
				|| (applicationType.equals("WCE"))
				|| (applicationType.equals("WCR"))
				|| applicationType.equals("APP")
				|| applicationType.equals("EL") || applicationType
				.equals("DUP")) && ((request.getParameter("flag")).equals("0"))) {
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when flag is 0");
			cgpan = request.getParameter("cgpan");

			if (cgpan == null) {
				cgpan = "";
			}
			application.setCgpan(cgpan);
			application.setAppRefNo("");
		} else if ((applicationType.equals("MA")
				|| applicationType.equals("APP")
				|| applicationType.equals("EL") || applicationType
				.equals("DUP")) && ((request.getParameter("flag")).equals("1"))) {
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when flag is 1");
			String appRefNo = request.getParameter("appRef");
			if (appRefNo == null) {
				appRefNo = "";
			}
			application.setAppRefNo(appRefNo);
			application.setCgpan("");
		}
		// }

		// Retreiving the cgpan,application ref no,cgbid and borrowerName for
		// the application object
		cgbid = ""; // cgbid
		if (((application.getBorrowerDetails()).getSsiDetails()).getCgbid() == null) {
			cgbid = "";
		} else {
			cgbid = ((application.getBorrowerDetails()).getSsiDetails())
					.getCgbid();
		}

		if (application.getCgpan() != null
				&& !(application.getCgpan().equals(""))) {
			cgpan = application.getCgpan();

			dynaForm.set("previouslyCovered", "Y");
			dynaForm.set("none", "cgpan");
			dynaForm.set("unitValue", cgpan);

		} else {
			dynaForm.set("unitValue", "");
		}

		String appRefNo = application.getAppRefNo(); // application ref no
		String borrowerName = "";
		// borrower Name
		if (((application.getBorrowerDetails()).getSsiDetails()).getSsiName() == null) {
			borrowerName = "";
		} else {

			borrowerName = ((application.getBorrowerDetails()).getSsiDetails())
					.getSsiName();

		}

		mliId = (String) dynaForm.get("selectMember");
		dynaForm.set("mcgfId", mliId);

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		if (mliId != null && !(mliId.equals(""))) {

			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!(memberIds.contains(mliId))) {
				throw new NoMemberFoundException("The Member ID does not exist");
			}
		} else {

			mliId = null;
		}

		if ((cgbid != null && !(cgbid.equals("")))
				&& (mliId != null && !(mliId.equals("")))) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!(borrowerIds.contains(cgbid.toUpperCase()))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}
		}

		int type = 0;
		String pageForward = "";
		// System.out.println("mliId:"+mliId);
		// System.out.println("cgbid:"+cgbid);
		// System.out.println("borrowerName:"+borrowerName);

		/*
		 * If cgbid /application ref no / borrower name is entered,the list of
		 * cgpans / application reference nos are retrieved
		 */
		if (Integer.parseInt(request.getParameter("flag")) == 2) {
			System.out.println("Seconddebug");
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
					"flag :" + (String) request.getParameter("flag"));
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
					"borrower name:" + borrowerName);
			if ((!(mliId.equals("")) && !(cgbid.equals("")))
					|| (!(mliId.equals("")) && !(borrowerName.equals("")))) {
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"Entering when mli di,borrower name and cgbid r entered");
				if (applicationType.equals("TCE")) {
					type = 0;
					borrowerName = "";

					ClaimsProcessor claimProcessor = new ClaimsProcessor();

					if (cgbid == null || cgbid.equals("")) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

					}

					int claimCount = appProcessor.getClaimCount(cgbid);
					if (claimCount > 0) {
						throw new MessageException(
								"Application cannot be filed by this borrower since Claim Application has been submitted");
					}

					ArrayList appCgpansList = appProcessor.getCgpans(mliId,
							cgbid, type, borrowerName);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "APPCgpanList:" + appCgpansList);

					dynaForm.set("allCgpans", appCgpansList);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "DynaForm after setting..:"
									+ dynaForm.get("allCgpans"));

				} else if (applicationType.equals("WCE")) {
					type = 1;
					borrowerName = "";

					ClaimsProcessor claimProcessor = new ClaimsProcessor();

					if (cgbid == null || cgbid.equals("")) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

					}

					int claimCount = appProcessor.getClaimCount(cgbid);
					if (claimCount > 0) {
						throw new MessageException(
								"Application cannot be filed by this borrower since Claim Application has been submitted");
					}

					ArrayList appCgpansList = appProcessor.getCgpans(mliId,
							cgbid, type, borrowerName);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "APPCgpanList:" + appCgpansList);

					dynaForm.set("allCgpans", appCgpansList);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "DynaForm after setting..:"
									+ dynaForm.get("allCgpans"));

				} else if (applicationType.equals("WCR")) {
					type = 2;
					borrowerName = "";

					ClaimsProcessor claimProcessor = new ClaimsProcessor();

					if (cgbid == null || cgbid.equals("")) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

					}

					int claimCount = appProcessor.getClaimCount(cgbid);
					if (claimCount > 0) {
						throw new MessageException(
								"Application cannot be filed by this borrower since Claim Application has been submitted");
					}

					ArrayList appCgpansList = appProcessor.getCgpans(mliId,
							cgbid, type, borrowerName);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "APPCgpanList:" + appCgpansList);

					dynaForm.set("allCgpans", appCgpansList);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "DynaForm after setting..:"
									+ dynaForm.get("allCgpans"));

				}

				// If its a modify application, then the list of cgpans and app
				// ref nos are retrieved
				else if (applicationType.equals("MA")) {

					ClaimsProcessor claimProcessor = new ClaimsProcessor();

					if ((cgbid == null || cgbid.equals(""))
							&& (borrowerName == null || borrowerName.equals(""))
							&& (appRefNo == null || borrowerName.equals(""))
							&& (cgpan != null || !(cgpan.equals("")))) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);
					}

					if (cgbid != null && !(cgbid.equals(""))) {
						int claimCount = appProcessor.getClaimCount(cgbid);
						if (claimCount > 0) {
							throw new MessageException(
									"Application cannot be filed by this borrower since Claim Application has been submitted");
						}

					}

					ArrayList cgpanAppRefNoList = appProcessor.getAppRefNos(
							mliId, cgbid, borrowerName);

					ArrayList appRefNosList = (ArrayList) cgpanAppRefNoList
							.get(0);
					ArrayList appCgpanList = (ArrayList) cgpanAppRefNoList
							.get(1);

					if (appRefNosList.size() == 0 && appCgpanList.size() == 0) {
						throw new MessageException(
								"There are no Application Reference Numbers or CGPANs for modification");
					}

					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "AppRefNos Array List :"
									+ appRefNosList);

					dynaForm.set("allAppRefNos", appRefNosList);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "DynaForm after setting..:"
									+ dynaForm.get("allAppRefNos"));

					dynaForm.set("allCgpans", appCgpanList);
					Log.log(Log.DEBUG, "ApplicationProcessingAction",
							"showCgpanList", "DynaForm after setting..:"
									+ dynaForm.get("allCgpans"));

				}

				pageForward = "listofcgpans";

			}
		}

		// If cgpan or application ref no is entered,then the application
		// details are retrieved
		if (((mliId == null || mliId.equals("")) && !(appRefNo.equals("")))
				|| ((mliId == null || mliId.equals("")) && !(cgpan.equals("")))
				|| (!(mliId.equals("")) && !(cgpan.equals("")))
				|| (!(mliId.equals("")) && !(appRefNo.equals("")))) {
			HttpSession appSession1 = request.getSession(false);
			String applicationTypes = (String) appSession1
					.getAttribute(SessionConstants.APPLICATION_TYPE);

			if (!(appRefNo.equals(""))) {
				appProcessor.checkCgpanPool(appRefNo);
			} else if (applicationTypes.equals("WCR") && !(cgpan.equals(""))) {
				if (cgpan.substring(11, 13).equals("TC")) {
					throw new MessageException(
							"Not a valid Application for Working capital Renewal");
				}
				String renewcgpan = appProcessor.checkRenewCgpan(cgpan);
				if (renewcgpan.equals("0")) {
					// cgpan=cgpan;
					dynaForm.set("unitValue", cgpan);
				} else {

					cgpan = renewcgpan;

				}

				if (cgpan.substring(11, 13).equals("R9")) {
					throw new MessageException(
							"This application cannot be renewed further");
				}

				dynaForm.set("none", "cgpan");
				dynaForm.set("unitValue", cgpan);

			}

			if (!(cgpan.equals(""))) {
				application = appProcessor.getAppForCgpan(mliId, cgpan);
				appRefNo = application.getAppRefNo();
				appProcessor.checkCgpanPool(appRefNo);
				/* added by sukumar@path for getting guarantee start date time */
				RpDAO rpDAO = new RpDAO();
				Date guarStartDate = rpDAO.getGuarStartDate(application);

				// System.out.println("Guarantee Start Date of "+cgpan+" is"
				// +guarStartDate);
				// bug fixed by sukumar@path on 10-08-2009,system does not allow
				// wc enhancement applications where guarantee was not issued
				if (applicationType.equals("WCE")) {
					if (guarStartDate == null || guarStartDate.equals("")) {
						throw new NoDataException(
								"Guarantee has not started for this " + cgpan);
					}
					int paymentFlag = applicationDAO.getPaymentStatus(cgpan);
					if (paymentFlag > 0) {
						throw new MessageException(
								"Guarantee Fee for this account has not been received");
					}
				}
				/*
				 * if(guarStartDate==null || guarStartDate.equals("") ){ throw
				 * new
				 * NoDataException("Guarantee has not started for this "+cgpan);
				 * }
				 */
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when cgpan and app ref no are entered");
			application = appProcessor.getApplication(mliId, cgpan, appRefNo);

			mliId = application.getMliID();
			String bankId = mliId.substring(0, 4);
			String zoneId = mliId.substring(4, 8);
			String branchId = mliId.substring(8, 12);
			Registration registration = new Registration();
			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"application types :" + applicationTypes);

			String applicationLoanType = application.getLoanType();

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Setting the values in copy Properties....");

			// copying ssi details
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();
			BeanUtils.copyProperties(dynaForm, ssiDetails);

			// copying borrower details
			borrowerDetails = application.getBorrowerDetails();
			BeanUtils.copyProperties(dynaForm, borrowerDetails);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Borrower Details...");

			// copying primary security details
			PrimarySecurityDetails primarySecurityDetails = (application
					.getProjectOutlayDetails()).getPrimarySecurityDetails();
			BeanUtils.copyProperties(dynaForm, primarySecurityDetails);
			double landValue = ((java.lang.Double) dynaForm.get("landValue"))
					.doubleValue();
			double bldgValue = ((java.lang.Double) dynaForm.get("bldgValue"))
					.doubleValue();
			double machineValue = ((java.lang.Double) dynaForm
					.get("machineValue")).doubleValue();
			double assetsValue = ((java.lang.Double) dynaForm
					.get("assetsValue")).doubleValue();
			double currentAssetsValue = ((java.lang.Double) dynaForm
					.get("currentAssetsValue")).doubleValue();
			double othersValue = ((java.lang.Double) dynaForm
					.get("othersValue")).doubleValue();
			double psTotalValue = landValue + bldgValue + machineValue
					+ assetsValue + currentAssetsValue + othersValue;
			Double intPsTotal = new Double(psTotalValue);
			dynaForm.set("psTotal", intPsTotal);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Primary Security Details...");

			// copying project Outlay details
			ProjectOutlayDetails projectOutlayDetails = application
					.getProjectOutlayDetails();
			BeanUtils.copyProperties(dynaForm, projectOutlayDetails);
			/*
			 * double projectOutlayValue=
			 * projectOutlayDetails.getProjectOutlay(); Double doubleValue=new
			 * Double(projectOutlayValue); int
			 * doubleIntValue=doubleValue.intValue(); Integer intValue=new
			 * Integer(doubleIntValue); dynaForm.set("projectOutlay",intValue);
			 */
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Project Outlay Details...");

			// copying term loan details
			if (!(applicationTypes.equals("TCE"))) {

				TermLoan termLoan = application.getTermLoan();
				BeanUtils.copyProperties(dynaForm, termLoan);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set Term Loan Details...");
				double termCreditSanctioned = ((java.lang.Double) dynaForm
						.get("termCreditSanctioned")).doubleValue();
				java.lang.Double termCreditSanctionedVal = new Double(
						termCreditSanctioned);
				double tcPromoterContribution = ((java.lang.Double) dynaForm
						.get("tcPromoterContribution")).doubleValue();
				double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
						.get("tcSubsidyOrEquity")).doubleValue();
				double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
						.doubleValue();
				double projectCost = termCreditSanctioned
						+ tcPromoterContribution + tcSubsidyOrEquity + tcOthers;
				java.lang.Double projectCostIntVal = new Double(projectCost);
				dynaForm.set("projectCost", projectCostIntVal);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "project Cost:" + projectCostIntVal);
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"project cost in the form:"
								+ dynaForm.get("projectCost"));

				dynaForm.set("amountSanctioned", termCreditSanctionedVal);

				// copying working capital details
				WorkingCapital workingCapital = application.getWc();
				BeanUtils.copyProperties(dynaForm, workingCapital);
				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// double existingTotal=wcFundBasedSanctioned +
				// wcNonFundBasedSanctioned;
				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);
				// Log.log(Log.DEBUG,"ApplicationProcessingAction","showCgpanList","existing total :"
				// + existingTotalVal);

				double wcPromoterContribution = ((java.lang.Double) dynaForm
						.get("wcPromoterContribution")).doubleValue();
				double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
						.get("wcSubsidyOrEquity")).doubleValue();
				double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
						.doubleValue();

				double wcAssessed = wcFundBasedSanctioned
						+ wcNonFundBasedSanctioned + wcPromoterContribution
						+ wcSubsidyOrEquity + wcOthers;
				java.lang.Double wcAssessedIntVal = new Double(wcAssessed);
				dynaForm.set("wcAssessed", wcAssessedIntVal);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set Working Capital Details...");

				double projectCostValue = ((java.lang.Double) dynaForm
						.get("projectCost")).doubleValue();
				double wcAssessedValue = ((java.lang.Double) dynaForm
						.get("wcAssessed")).doubleValue();
				double projectOutlayValueCost = projectCostValue
						+ wcAssessedValue;
				java.lang.Double projectOutlayVal = new Double(
						projectOutlayValueCost);
				dynaForm.set("projectOutlay", projectOutlayVal); // setting
																	// project
																	// outlay to
																	// the form
			} else {

				double value = 0;
				Double intVal = new Double(value);
				dynaForm.set("termCreditSanctioned", intVal);
				dynaForm.set("tcPromoterContribution", intVal);
				dynaForm.set("tcSubsidyOrEquity", intVal);
				dynaForm.set("tcOthers", intVal);
				dynaForm.set("projectCost", intVal);
				dynaForm.set("wcFundBasedSanctioned", intVal);
				dynaForm.set("wcNonFundBasedSanctioned", intVal);
				dynaForm.set("wcSubsidyOrEquity", intVal);
				dynaForm.set("wcOthers", intVal);
				dynaForm.set("wcAssessed", intVal);
				dynaForm.set("projectOutlay", intVal);
			}

			// copying secruittization details
			Securitization securitization = application.getSecuritization();
			BeanUtils.copyProperties(dynaForm, securitization);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Securitization Details...");

			// copying mcgfDetails
			String mcgfFlag = mliInfo.getSupportMCGF();
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"MCGF Flag" + mcgfFlag);
			if (mcgfFlag.equals("Y")) {

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "M");

				mcgfDetails = application.getMCGFDetails();
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf App ref no:"
								+ mcgfDetails.getApplicationReferenceNumber());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf approved Amount:"
								+ mcgfDetails.getMcgfApprovedAmt());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf district:" + mcgfDetails.getMcgfDistrict());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf bank:" + mcgfDetails.getParticipatingBank());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf branch :"
								+ mcgfDetails.getParticipatingBankBranch());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf name :" + mcgfDetails.getMcgfName());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "mcgf Id:" + mcgfDetails.getMcgfId());

				mcgfDetails.setMcgfId(mliId);
				application.setMCGFDetails(mcgfDetails);
				BeanUtils.copyProperties(dynaForm, mcgfDetails);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set MCGF Details...");

			} else {
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Not under MCGF");

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "NM");
			}

			// copying application to the form
			BeanUtils.copyProperties(dynaForm, application);

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting constitution Others on the screen
			String constitutionVal = ssiDetails.getConstitution();

			if (!(constitutionVal.equals("proprietary"))
					&& !(constitutionVal.equals("partnership"))
					&& !(constitutionVal.equals("private"))
					&& !(constitutionVal.equals("public"))) {

				dynaForm.set("constitutionOther", constitutionVal);
				dynaForm.set("constitution", "Others");
			} else {
				dynaForm.set("constitution", constitutionVal);
			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting legal ID
			String legalIDString = ssiDetails.getCpLegalID();
			if (legalIDString != null && !(legalIDString.equals(""))) {
				if (!(legalIDString.equals("VoterIdentityCard"))
						&& !(legalIDString.equals("RationCardnumber"))
						&& !(legalIDString.equals("PASSPORT"))
						&& !(legalIDString.equals("Driving License"))) {
					dynaForm.set("otherCpLegalID", legalIDString);
					dynaForm.set("cpLegalID", "Others");
				} else {
					dynaForm.set("cpLegalID", legalIDString);
				}

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting subsidy / Equity Name
			String subsidyEquityName = projectOutlayDetails.getSubsidyName();
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Subsidy Name :" + projectOutlayDetails.getSubsidyName());
			if (subsidyEquityName != null && !(subsidyEquityName.equals(""))) {
				if (!(subsidyEquityName.equals("PMRY"))
						&& !(subsidyEquityName.equals("SJRY"))) {
					dynaForm.set("otherSubsidyEquityName", subsidyEquityName);
					dynaForm.set("subsidyName", "Others");
				} else {
					dynaForm.set("subsidyName", subsidyEquityName);
				}
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// The states the populated on the screen while retreival
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			// The district for the particular state has to be populated
			String state = (String) dynaForm.get("state");

			dynaForm.set("state", ssiDetails.getState());// Added by pradeep on
															// 23-01-2012
			ArrayList districtList = admin.getAllDistricts(state);
			dynaForm.set("districtList", districtList);
			// int districtSize=districtList.size();
			String districtName = ssiDetails.getDistrict();

			if (districtList.contains(districtName)) {
				dynaForm.set("district", districtName);
			} else {
				dynaForm.set("districtOthers", districtName);
				dynaForm.set("district", "Others");

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			/*
			 * ArrayList industryNatureList=getIndustryNature();
			 * dynaForm.set("industryNatureList",industryNatureList);
			 * 
			 * String industryNature=(String)dynaForm.get("industryNature");
			 * if(industryNature!=null && !(industryNature.equals(""))) {
			 * ArrayList
			 * industrySectorList=admin.getIndustrySectors(industryNature);
			 * dynaForm.set("industrySectorList",industrySectorList);
			 * 
			 * }
			 */
			dynaForm.set("industryNature", ssiDetails.getIndustryNature());
			dynaForm.set("industrySector", ssiDetails.getIndustrySector());

			// kot
			dynaForm.set("unitAssisted", ssiDetails.getUnitAssisted());
			dynaForm.set("womenOperated", ssiDetails.getWomenOperated());
			// dynaForm.set("appmsE",ssiDetails.getWomenOperated());
			// dynaForm.set("mSE",application.get;

			dynaForm.set("mSE", application.getMsE());

			//System.out.println("mse iss " + application.getMsE());
			dynaForm.set("enterprise", ssiDetails.getEnterprise());

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			ArrayList participatingBanks = mcgsProcessor
					.getAllParticipatingBanks(mliId);
			dynaForm.set("participatingBanks", participatingBanks);

			// If it is a additional Term Loan
			if (applicationTypes.equals("TCE")) {

				if (application.getBorrowerDetails().getPreviouslyCovered()
						.equals("Y")) {
					if (application.getCgpanReference() != null
							&& !(application.getCgpanReference().equals(""))) {
						String cgpanRef = application.getCgpanReference();
						dynaForm.set("unitValue", cgpanRef);
						dynaForm.set("none", "cgpan");

					}
				}
				dynaForm.set("previouslyCovered", "Y");

				if (application.getLoanType().equals("WC")) {
					throw new MessageException(
							"Not a valid Application for Additional Term Loan");
				}

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

				/*
				 * if (mcgfDetails!=null) {
				 * appSession1.setAttribute(SessionConstants.MCGF_FLAG,"M"); }
				 */
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Additional Term Loan");
				if (application.getStatus().equals("EX")
						|| application.getStatus().equals("AP")) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "0");
				} else {

					throw new MessageException(
							"Not a valid Application for Additional Term Loan");
				}

				pageForward = "AddtlTermLoanPage";

			}
			// If it is a Enhancement Working Capital Loan
			else if (applicationTypes.equals("WCE")) {
				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				double balanceAppAmt = appProcessor
						.getBalanceApprovedAmt(application);

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "balanceAppAmt :" + balanceAppAmt);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				dynaForm.set("unitValue", cgpan);
				dynaForm.set("none", "cgpan");
				dynaForm.set("previouslyCovered", "Y");

				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				java.lang.Double wcFundBasedSanctionedVal = new Double(
						wcFundBasedSanctioned);
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// java.lang.Double wcFundBasedSanctionedVal=new
				// Double(wcFundBasedSanctioned);

				// dynaForm.set("existingTotal",wcFundBasedSanctionedVal);

				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);

				if (application.getLoanType().equals("TC")
						|| application.getLoanType().equals("CC")) {
					throw new MessageException(
							"Not a valid Application for Enhancement of Working Capital");
				}

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

				if (application.getStatus().equals("EX")) {
					throw new MessageException("This application has expired");
				} else {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "1");
					pageForward = "EnhancementPage";
				}

			}
			// If it is a Renewal Working Capital Loan
			else if (applicationTypes.equals("WCR")) {
				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				dynaForm.set("none", "cgpan");
				dynaForm.set("unitValue", cgpan);
				dynaForm.set("previouslyCovered", "Y");

				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				java.lang.Double wcFundBasedSanctionedVal = new Double(
						wcFundBasedSanctioned);
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// java.lang.Double wcFundBasedSanctionedVal=new
				// Double(wcFundBasedSanctioned);

				// dynaForm.set("existingTotal",wcFundBasedSanctionedVal);

				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);

				ClaimsProcessor claimProcessor = new ClaimsProcessor();
                System.out.println("");
				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}
                 
				if (!(application.getStatus().equals("EX"))) {
					throw new MessageException(
							"This application has not expired");
				} else if (application.getStatus().equals("EX")) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "2");
					pageForward = "RenewalPage";
				}
             
			} else if (applicationTypes.equals("MA")) {

				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if (cgpan != null && !cgpan.equals("")) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);
				} else if (appRefNo != null && !appRefNo.equals("")) {
					Application tempApp = appProcessor.getPartApplication(
							mliId, "", appRefNo);
					cgpan = tempApp.getCgpan();

					if (cgpan != null && !(cgpan.equals(""))) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);
					}
				}

				if (cgbid != null && !(cgbid.equals(""))) {
					int claimCount = appProcessor.getClaimCount(cgbid);
					if (claimCount > 0) {
						throw new MessageException(
								"Application cannot be filed by this borrower since Claim Application has been submitted");
					}

				}

				if (cgpan != null && !(cgpan.equals(""))) {
					dynaForm.set("unitValue", cgpan);
					dynaForm.set("none", "cgpan");
					dynaForm.set("previouslyCovered", "Y");

				}

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Loan Type :" + applicationTypes);

				if (application.getStatus().equals("PE")
						|| application.getStatus().equals("EP")) {
					throw new MessageException(
							"This application cannot be modified since decision to be taken by CGTSI is pending");
				}

				if (application.getStatus().equals("EX")
						|| application.getStatus().equals("RE")) {
					throw new MessageException(
							"This application cannot be modified since the application is not live");
				}

				// If the application to be modified is a term loan
				if (applicationLoanType
						.equals(ApplicationConstants.TC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "3");
					pageForward = "tcForward";

					// If the application to .be modified is a Working Capital
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.WC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "4");
					pageForward = "wcForward";

					// If the application to be modified is a Both TC and WC
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.BOTH_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "5");
					pageForward = "bothForward";

					// If the application to be modified is a Composite loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.CC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "6");
					pageForward = "ccForward";
				}

			}
			// if it's a approval application
			else if (applicationTypes.equals("APP")
					|| applicationTypes.equals("REAPP")
					|| applicationTypes.equals("EL")
					|| applicationTypes.equals("DUP")) {

				if (cgpan != null && !(cgpan.equals(""))) {
					dynaForm.set("unitValue", cgpan);
					dynaForm.set("none", "cgpan");
					dynaForm.set("previouslyCovered", "Y");

				}

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Loan Type :" + applicationTypes);

				// If the application to be modified is a term loan
				if (applicationLoanType
						.equals(ApplicationConstants.TC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "11");
					Log.log(Log.INFO,
							"ApplicationProcessingAction",
							"showCgpanList",
							"Session Attribute :"
									+ appSession1
											.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
					pageForward = "tcForward";

					// If the application to be modified is a Working Capital
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.WC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "12");
					pageForward = "wcForward";

					// If the application to be modified is a Composite loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.CC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "13");
					pageForward = "ccForward";
				}
			}
		}

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"Page to be forwaded :" + pageForward);
		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Exited");

		cgbid = null;
		cgpan = null;

		dynaForm.set("previouslyCovered", "Y");
		dynaForm.set("dcHandlooms", application.getDcHandlooms());
		dynaForm.set("WeaverCreditScheme", application.getWeaverCreditScheme());
		dynaForm.set("handloomchk", application.getHandloomchk());

		return mapping.findForward(pageForward);
	}

	/* -------- Reapply Rejected Application Starts here -------- */

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward showCgpanListNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator admin = new Administrator();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		Application application = new Application();
		MCGFDetails mcgfDetails = new MCGFDetails();

		String cgpan = "";// cgpan
		String mliId = "";
		String cgbid = "";

		BeanUtils.populate(ssiDetails, dynaForm.getMap()); // for setting cgbid

		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());
		application.setBorrowerDetails(borrowerDetails);

		BeanUtils.populate(application, dynaForm.getMap()); // for setting cgpan
															// and apprefno

		HttpSession appSession = request.getSession(false);

		String applicationType = (String) appSession
				.getAttribute(SessionConstants.APPLICATION_TYPE);
		// System.out.println("application loan type :" + applicationType+"S");

		/*
		 * This loop checks whether a cgpan / application reference number link
		 * has been clicked for the list of cgpans and app ref nos for modify
		 * application / enhancement / additional term loan /renewal application
		 */

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"mli id :" + dynaForm.get("selectMember"));
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"cgpan :" + application.getCgpan());
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"flag :" + (String) request.getParameter("flag"));
		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Entering when cgpan and ref no are null");

		if ((applicationType.equals("MA") || applicationType.equals("APP")
				|| applicationType.equals("EL") || applicationType
				.equals("DUP")) && ((request.getParameter("flag")).equals("1"))) {
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when flag is 1");
			String appRefNo = request.getParameter("appRef");
			if (appRefNo == null) {
				appRefNo = "";
			}
			application.setAppRefNo(appRefNo);
			application.setCgpan("");
		}
		// }

		// Retreiving the cgpan,application ref no,cgbid and borrowerName for
		// the application object
		cgbid = ""; // cgbid
		if (((application.getBorrowerDetails()).getSsiDetails()).getCgbid() == null) {
			cgbid = "";
		} else {
			cgbid = ((application.getBorrowerDetails()).getSsiDetails())
					.getCgbid();
		}

		if (application.getCgpan() != null
				&& !(application.getCgpan().equals(""))) {
			cgpan = application.getCgpan();

			dynaForm.set("previouslyCovered", "Y");
			dynaForm.set("none", "cgpan");
			dynaForm.set("unitValue", cgpan);

		} else {
			dynaForm.set("unitValue", "");
		}

		String appRefNo = application.getAppRefNo(); // application ref no
		// System.out.println("Application Processing Action --Application Reference Number:"+appRefNo);
		String borrowerName = "";
		// borrower Name
		if (((application.getBorrowerDetails()).getSsiDetails()).getSsiName() == null) {
			borrowerName = "";
		} else {

			borrowerName = ((application.getBorrowerDetails()).getSsiDetails())
					.getSsiName();

		}

		mliId = (String) dynaForm.get("selectMember");
		dynaForm.set("mcgfId", mliId);

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		if (mliId != null && !(mliId.equals(""))) {

			Vector memberIds = cpProcessor.getAllMemberIds();
			if (!(memberIds.contains(mliId))) {
				throw new NoMemberFoundException("The Member ID does not exist");
			}
		} else {

			mliId = null;
		}

		if ((cgbid != null && !(cgbid.equals("")))
				&& (mliId != null && !(mliId.equals("")))) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!(borrowerIds.contains(cgbid.toUpperCase()))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}
		}

		int type = 0;
		String pageForward = "";

		// If cgpan or application ref no is entered,then the application
		// details are retrieved
		if (((mliId == null || mliId.equals("")) && !(appRefNo.equals("")))
				|| ((mliId == null || mliId.equals("")) && !(cgpan.equals("")))
				|| (!(mliId.equals("")) && !(cgpan.equals("")))
				|| (!(mliId.equals("")) && !(appRefNo.equals("")))) {
			HttpSession appSession1 = request.getSession(false);
			String applicationTypes = (String) appSession1
					.getAttribute(SessionConstants.APPLICATION_TYPE);

			if (!(appRefNo.equals(""))) {
				appProcessor.checkCgpanPool(appRefNo);
			} else if (applicationTypes.equals("WCR") && !(cgpan.equals(""))) {
				if (cgpan.substring(11, 13).equals("TC")) {
					throw new MessageException(
							"Not a valid Application for Working capital Renewal");
				}
				String renewcgpan = appProcessor.checkRenewCgpan(cgpan);
				if (renewcgpan.equals("0")) {
					// cgpan=cgpan;
					dynaForm.set("unitValue", cgpan);
				} else {

					cgpan = renewcgpan;

				}

				if (cgpan.substring(11, 13).equals("R9")) {
					throw new MessageException(
							"This application cannot be renewed further");
				}

				dynaForm.set("none", "cgpan");
				dynaForm.set("unitValue", cgpan);

			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when cgpan and app ref no are entered");
			// System.out.println("Line number 3254");
			application = appProcessor.getApplication(mliId, cgpan, appRefNo);
			// System.out.println("Line number 3256");
			mliId = application.getMliID();
			String bankId = mliId.substring(0, 4);
			String zoneId = mliId.substring(4, 8);
			String branchId = mliId.substring(8, 12);

			Registration registration = new Registration();
			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);

			if (!(application.getStatus()).equals("RE") || !(cgpan.equals(""))) {
				// System.out.println("The Application can not be a Rejected Application");
				throw new MessageException(
						"This application cannot be modified since the application is not a rejected application");
			}
			if (application.getRemarks() == null) {
				// System.out.println("temp Remarks:"+tempremarks);
			} else {
				if (application.getRemarks().equals("INELIGIBLE ACTIVITY")
						|| application.getRemarks().equals(
								"Ineligible activity")) {

					throw new MessageException(
							"This application cannot be modified as the activity is ineligible");
				}
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"application types :" + applicationTypes);

			String applicationLoanType = application.getLoanType();

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Setting the values in copy Properties....");

			// copying ssi details
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();
			BeanUtils.copyProperties(dynaForm, ssiDetails);

			// copying borrower details
			borrowerDetails = application.getBorrowerDetails();
			BeanUtils.copyProperties(dynaForm, borrowerDetails);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Borrower Details...");

			// copying primary security details
			PrimarySecurityDetails primarySecurityDetails = (application
					.getProjectOutlayDetails()).getPrimarySecurityDetails();
			BeanUtils.copyProperties(dynaForm, primarySecurityDetails);
			double landValue = ((java.lang.Double) dynaForm.get("landValue"))
					.doubleValue();
			double bldgValue = ((java.lang.Double) dynaForm.get("bldgValue"))
					.doubleValue();
			double machineValue = ((java.lang.Double) dynaForm
					.get("machineValue")).doubleValue();
			double assetsValue = ((java.lang.Double) dynaForm
					.get("assetsValue")).doubleValue();
			double currentAssetsValue = ((java.lang.Double) dynaForm
					.get("currentAssetsValue")).doubleValue();
			double othersValue = ((java.lang.Double) dynaForm
					.get("othersValue")).doubleValue();
			double psTotalValue = landValue + bldgValue + machineValue
					+ assetsValue + currentAssetsValue + othersValue;
			Double intPsTotal = new Double(psTotalValue);
			dynaForm.set("psTotal", intPsTotal);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Primary Security Details...");

			// copying project Outlay details
			ProjectOutlayDetails projectOutlayDetails = application
					.getProjectOutlayDetails();
			BeanUtils.copyProperties(dynaForm, projectOutlayDetails);

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Project Outlay Details...");

			// copying term loan details
			if (!(applicationTypes.equals("TCE"))) {

				TermLoan termLoan = application.getTermLoan();
				BeanUtils.copyProperties(dynaForm, termLoan);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set Term Loan Details...");
				double termCreditSanctioned = ((java.lang.Double) dynaForm
						.get("termCreditSanctioned")).doubleValue();
				java.lang.Double termCreditSanctionedVal = new Double(
						termCreditSanctioned);
				double tcPromoterContribution = ((java.lang.Double) dynaForm
						.get("tcPromoterContribution")).doubleValue();
				double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
						.get("tcSubsidyOrEquity")).doubleValue();
				double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
						.doubleValue();
				double projectCost = termCreditSanctioned
						+ tcPromoterContribution + tcSubsidyOrEquity + tcOthers;
				java.lang.Double projectCostIntVal = new Double(projectCost);
				dynaForm.set("projectCost", projectCostIntVal);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "project Cost:" + projectCostIntVal);
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"project cost in the form:"
								+ dynaForm.get("projectCost"));

				dynaForm.set("amountSanctioned", termCreditSanctionedVal);

				// copying working capital details
				WorkingCapital workingCapital = application.getWc();
				BeanUtils.copyProperties(dynaForm, workingCapital);
				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// double existingTotal=wcFundBasedSanctioned +
				// wcNonFundBasedSanctioned;
				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);
				// Log.log(Log.DEBUG,"ApplicationProcessingAction","showCgpanList","existing total :"
				// + existingTotalVal);

				double wcPromoterContribution = ((java.lang.Double) dynaForm
						.get("wcPromoterContribution")).doubleValue();
				double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
						.get("wcSubsidyOrEquity")).doubleValue();
				double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
						.doubleValue();

				double wcAssessed = wcFundBasedSanctioned
						+ wcNonFundBasedSanctioned + wcPromoterContribution
						+ wcSubsidyOrEquity + wcOthers;
				java.lang.Double wcAssessedIntVal = new Double(wcAssessed);
				dynaForm.set("wcAssessed", wcAssessedIntVal);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set Working Capital Details...");

				double projectCostValue = ((java.lang.Double) dynaForm
						.get("projectCost")).doubleValue();
				double wcAssessedValue = ((java.lang.Double) dynaForm
						.get("wcAssessed")).doubleValue();
				double projectOutlayValueCost = projectCostValue
						+ wcAssessedValue;
				java.lang.Double projectOutlayVal = new Double(
						projectOutlayValueCost);
				dynaForm.set("projectOutlay", projectOutlayVal); // setting
																	// project
																	// outlay to
																	// the form
			} else {

				double value = 0;
				Double intVal = new Double(value);
				dynaForm.set("termCreditSanctioned", intVal);
				dynaForm.set("tcPromoterContribution", intVal);
				dynaForm.set("tcSubsidyOrEquity", intVal);
				dynaForm.set("tcOthers", intVal);
				dynaForm.set("projectCost", intVal);
				dynaForm.set("wcFundBasedSanctioned", intVal);
				dynaForm.set("wcNonFundBasedSanctioned", intVal);
				dynaForm.set("wcSubsidyOrEquity", intVal);
				dynaForm.set("wcOthers", intVal);
				dynaForm.set("wcAssessed", intVal);
				dynaForm.set("projectOutlay", intVal);
			}

			// copying secruittization details
			Securitization securitization = application.getSecuritization();
			BeanUtils.copyProperties(dynaForm, securitization);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Securitization Details...");

			// copying mcgfDetails
			String mcgfFlag = mliInfo.getSupportMCGF();
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"MCGF Flag" + mcgfFlag);
			if (mcgfFlag.equals("Y")) {

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "M");

				mcgfDetails = application.getMCGFDetails();
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf App ref no:"
								+ mcgfDetails.getApplicationReferenceNumber());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf approved Amount:"
								+ mcgfDetails.getMcgfApprovedAmt());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf district:" + mcgfDetails.getMcgfDistrict());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf bank:" + mcgfDetails.getParticipatingBank());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf branch :"
								+ mcgfDetails.getParticipatingBankBranch());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf name :" + mcgfDetails.getMcgfName());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "mcgf Id:" + mcgfDetails.getMcgfId());

				mcgfDetails.setMcgfId(mliId);
				application.setMCGFDetails(mcgfDetails);
				BeanUtils.copyProperties(dynaForm, mcgfDetails);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set MCGF Details...");

			} else {
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Not under MCGF");

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "NM");
			}

			// copying application to the form
			BeanUtils.copyProperties(dynaForm, application);

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting constitution Others on the screen
			String constitutionVal = ssiDetails.getConstitution();

			if (!(constitutionVal.equals("proprietary"))
					&& !(constitutionVal.equals("partnership"))
					&& !(constitutionVal.equals("private"))
					&& !(constitutionVal.equals("public"))) {

				dynaForm.set("constitutionOther", constitutionVal);
				dynaForm.set("constitution", "Others");
			} else {
				dynaForm.set("constitution", constitutionVal);
			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting legal ID
			String legalIDString = ssiDetails.getCpLegalID();
			if (legalIDString != null && !(legalIDString.equals(""))) {
				if (!(legalIDString.equals("VoterIdentityCard"))
						&& !(legalIDString.equals("RationCardnumber"))
						&& !(legalIDString.equals("PASSPORT"))
						&& !(legalIDString.equals("Driving License"))) {
					dynaForm.set("otherCpLegalID", legalIDString);
					dynaForm.set("cpLegalID", "Others");
				} else {
					dynaForm.set("cpLegalID", legalIDString);
				}

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting subsidy / Equity Name
			String subsidyEquityName = projectOutlayDetails.getSubsidyName();
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Subsidy Name :" + projectOutlayDetails.getSubsidyName());
			if (subsidyEquityName != null && !(subsidyEquityName.equals(""))) {
				if (!(subsidyEquityName.equals("PMRY"))
						&& !(subsidyEquityName.equals("SJRY"))) {
					dynaForm.set("otherSubsidyEquityName", subsidyEquityName);
					dynaForm.set("subsidyName", "Others");
				} else {
					dynaForm.set("subsidyName", subsidyEquityName);
				}
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// The states the populated on the screen while retreival
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			// The district for the particular state has to be populated
			String state = (String) dynaForm.get("state");
			ArrayList districtList = admin.getAllDistricts(state);
			dynaForm.set("state", ssiDetails.getState());// added by pradeep on
															// 23012012
			dynaForm.set("districtList", districtList);
			// int districtSize=districtList.size();
			String districtName = ssiDetails.getDistrict();

			if (districtList.contains(districtName)) {
				dynaForm.set("district", districtName);
			} else {
				dynaForm.set("districtOthers", districtName);
				dynaForm.set("district", "Others");

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			dynaForm.set("industryNature", ssiDetails.getIndustryNature());
			dynaForm.set("industrySector", ssiDetails.getIndustrySector());

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			ArrayList participatingBanks = mcgsProcessor
					.getAllParticipatingBanks(mliId);
			dynaForm.set("participatingBanks", participatingBanks);

			// If it is a additional Term Loan
			if (applicationTypes.equals("TCE")) {
				if (application.getBorrowerDetails().getPreviouslyCovered()
						.equals("Y")) {
					if (application.getCgpanReference() != null
							&& !(application.getCgpanReference().equals(""))) {
						String cgpanRef = application.getCgpanReference();
						dynaForm.set("unitValue", cgpanRef);
						dynaForm.set("none", "cgpan");

					}
				}
				dynaForm.set("previouslyCovered", "Y");

				if (application.getLoanType().equals("WC")) {
					throw new MessageException(
							"Not a valid Application for Additional Term Loan");
				}

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

				/*
				 * if (mcgfDetails!=null) {
				 * appSession1.setAttribute(SessionConstants.MCGF_FLAG,"M"); }
				 */
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Additional Term Loan");
				if (application.getStatus().equals("EX")
						|| application.getStatus().equals("AP")) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "0");
				} else {

					throw new MessageException(
							"Not a valid Application for Additional Term Loan");
				}
				pageForward = "AddtlTermLoanPage";

			}
			// If it is a Enhancement Working Capital Loan
			else if (applicationTypes.equals("WCE")) {
				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				double balanceAppAmt = appProcessor
						.getBalanceApprovedAmt(application);

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "balanceAppAmt :" + balanceAppAmt);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				dynaForm.set("unitValue", cgpan);
				dynaForm.set("none", "cgpan");
				dynaForm.set("previouslyCovered", "Y");

				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				java.lang.Double wcFundBasedSanctionedVal = new Double(
						wcFundBasedSanctioned);
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// java.lang.Double wcFundBasedSanctionedVal=new
				// Double(wcFundBasedSanctioned);

				// dynaForm.set("existingTotal",wcFundBasedSanctionedVal);

				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);

				if (application.getLoanType().equals("TC")
						|| application.getLoanType().equals("CC")) {
					throw new MessageException(
							"Not a valid Application for Enhancement of Working Capital");
				}

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

				if (application.getStatus().equals("EX")) {
					throw new MessageException("This application has expired");
				} else {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "1");
					pageForward = "EnhancementPage";
				}

			}
			// If it is a Renewal Working Capital Loan
			else if (applicationTypes.equals("WCR")) {
				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				dynaForm.set("none", "cgpan");
				dynaForm.set("unitValue", cgpan);
				dynaForm.set("previouslyCovered", "Y");

				double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcFundBasedSanctioned")).doubleValue();
				java.lang.Double wcFundBasedSanctionedVal = new Double(
						wcFundBasedSanctioned);
				double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
						.get("wcNonFundBasedSanctioned")).doubleValue();
				// java.lang.Double wcFundBasedSanctionedVal=new
				// Double(wcFundBasedSanctioned);

				// dynaForm.set("existingTotal",wcFundBasedSanctionedVal);

				java.lang.Double existingTotalFBVal = new Double(
						wcFundBasedSanctioned);
				java.lang.Double existingTotalNFBVal = new Double(
						wcNonFundBasedSanctioned);
				dynaForm.set("existingFundBasedTotal", existingTotalFBVal);
				dynaForm.set("existingNonFundBasedTotal", existingTotalNFBVal);

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if ((cgbid == null || cgbid.equals(""))
						&& (cgpan != null && !cgpan.equals(""))) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				}

				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

				if (!(application.getStatus().equals("EX"))) {
					throw new MessageException(
							"This application has not expired");
				} else if (application.getStatus().equals("EX")) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "2");
					pageForward = "RenewalPage";
				}

			} else if (applicationTypes.equals("MA")) {

				String remarks = application.getRemarks();
				application.setExistingRemarks(remarks);

				dynaForm.set("remarks", "");
				dynaForm.set("existingRemarks",
						application.getExistingRemarks());

				ClaimsProcessor claimProcessor = new ClaimsProcessor();

				if (cgpan != null && !cgpan.equals("")) {
					cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);
				} else if (appRefNo != null && !appRefNo.equals("")) {
					Application tempApp = appProcessor.getPartApplication(
							mliId, "", appRefNo);
					cgpan = tempApp.getCgpan();

					dynaForm.set("WeaverCreditScheme",
							tempApp.getWeaverCreditScheme());
					if (cgpan != null && !(cgpan.equals(""))) {
						cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);
					}
				}

				if (cgbid != null && !(cgbid.equals(""))) {
					int claimCount = appProcessor.getClaimCount(cgbid);
					if (claimCount > 0) {
						throw new MessageException(
								"Application cannot be filed by this borrower since Claim Application has been submitted");
					}

				}

				if (cgpan != null && !(cgpan.equals(""))) {
					dynaForm.set("unitValue", cgpan);
					dynaForm.set("none", "cgpan");
					dynaForm.set("previouslyCovered", "Y");

				}

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Loan Type :" + applicationTypes);

				if (application.getStatus().equals("PE")
						|| application.getStatus().equals("EP")) {
					throw new MessageException(
							"This application cannot be modified since decision to be taken by CGTSI is pending");
				}

				// if(application.getStatus().equals("EX") ||
				// application.getStatus().equals("RE"))
				if (application.getStatus().equals("EX")) {
					throw new MessageException(
							"This application cannot be modified since the application is not live");
				}

				// If the application to be modified is a term loan
				if (applicationLoanType
						.equals(ApplicationConstants.TC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "3");
					pageForward = "tcForward";

					// If the application to .be modified is a Working Capital
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.WC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "4");
					pageForward = "wcForward";

					// If the application to be modified is a Both TC and WC
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.BOTH_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "5");
					pageForward = "bothForward";

					// If the application to be modified is a Composite loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.CC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "6");
					pageForward = "ccForward";
				}

			}
			// if it's a approval application
			else if (applicationTypes.equals("APP")
					|| applicationTypes.equals("REAPP")
					|| applicationTypes.equals("EL")
					|| applicationTypes.equals("DUP")) {

				if (cgpan != null && !(cgpan.equals(""))) {
					dynaForm.set("unitValue", cgpan);
					dynaForm.set("none", "cgpan");
					dynaForm.set("previouslyCovered", "Y");

				}

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Loan Type :" + applicationTypes);

				// If the application to be modified is a term loan
				if (applicationLoanType
						.equals(ApplicationConstants.TC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "11");
					Log.log(Log.INFO,
							"ApplicationProcessingAction",
							"showCgpanList",
							"Session Attribute :"
									+ appSession1
											.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));
					pageForward = "tcForward";

					// If the application to be modified is a Working Capital
					// loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.WC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "12");
					pageForward = "wcForward";

					// If the application to be modified is a Composite loan
				}
				if (applicationLoanType
						.equals(ApplicationConstants.CC_APPLICATION)) {

					appSession1.setAttribute(
							SessionConstants.APPLICATION_TYPE_FLAG, "13");
					pageForward = "ccForward";
				}
			}
		}

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"Page to be forwaded :" + pageForward);
		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Exited");

		cgbid = null;
		cgpan = null;
		// System.out.println("pageForward:"+pageForward);
		return mapping.findForward(pageForward);
	}

	/* ------- Reapply Rejected Application ends here -------- */
	/*
	 * This method is called when a Term Credit Application is submitted
	 */

	public ActionForward submitApp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp", "Entered");

		String successPage = "";

		DynaActionForm dynaForm = (DynaActionForm) form;

		String mSE = (String) dynaForm.get("mSE");

		System.out.println("msee is " + mSE);
		// String coFinance = request.getParameter("coFinanceTaken1");
		// String coFinance = (String)dynaForm.get("coFinanceTaken1");
		// System.out.println("Co Finance:"+coFinance);

		HttpSession applicationSession = request.getSession(false);

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp",
				"Creating Objects");

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO applicaitonDAO = new ApplicationDAO();
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();

		ssiDetails.setMSE(mSE);

		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		User user = getUserInformation(request);

		// System.out.println("CoFianceTaken1"+application.getCoFinanceTaken1());
		// if(coFinance.equals("Y")|| coFinance=="Y"){
		// application.setCoFinanceTaken1(coFinance);
		// }
		// application.setCoFinanceTaken1(coFinance);
		// Log.log(Log.DEBUG,"ApplicationProcessingAction","submitApp","CoFianceTaken1 :"
		// + application.getCoFinanceTaken1());
		// Log.log(Log.DEBUG,"ApplicationProcessingAction","submitApp","CoFianceTaken1 :"
		// + coFinance);
		/*
		 * added internal rating for CGFSI scheme applications by sukumar@path
		 * on 16-01-20096
		 */
		String internalRating = (String) dynaForm.get("internalRating");
		application.setInternalRate(internalRating);
		String handiCrafts = (String) dynaForm.get("handiCrafts");
		String dcHandicrafts = (String) dynaForm.get("dcHandicrafts");
		String icardNo = (String) dynaForm.get("icardNo");
		java.util.Date icardIssueDate = (java.util.Date) dynaForm
				.get("icardIssueDate");

		String dcHandlooms = (String) dynaForm.get("dcHandlooms");

		String WeaverCreditScheme = (String) dynaForm.get("WeaverCreditScheme");
		String handloomchk = (String) dynaForm.get("handloomchk");
		String industrySector = (String) dynaForm.get("industrySector");
		String unitValue = (String) dynaForm.get("unitValue");
		String activityConfirm = (String) dynaForm.get("activityConfirm");

		if (handiCrafts != null && !handiCrafts.equals("null")) {
			if (handiCrafts.equals("N")) {
				dcHandicrafts = "N";
			}

			if (WeaverCreditScheme != null
					&& !WeaverCreditScheme.equals("null")) {
				if ((dcHandlooms.equals("N"))
						&& WeaverCreditScheme.equals("Select")) {
					WeaverCreditScheme = "";
				}
			}
		}

		application.setHandiCrafts(handiCrafts);
		application.setDcHandicrafts(dcHandicrafts);

		application.setIcardNo(icardNo);
		application.setIcardIssueDate(icardIssueDate);
		application.setDcHandlooms(dcHandlooms);

		application.setWeaverCreditScheme(WeaverCreditScheme);
		application.setHandloomchk(handloomchk);
		application.setActivityConfirm(activityConfirm);
		String jointFinance = (String) dynaForm.get("jointFinance");
		// System.out.println("Joint Finance:" +jointFinance);

		String jointcgpan = (String) dynaForm.get("jointcgpan");
		// System.out.println("jointcgpan:"+jointcgpan);
		application.setJointcgpan(jointcgpan);

		java.util.Date expiryDate = (java.util.Date) dynaForm.get("expiryDate");
		application.setAppExpiryDate(expiryDate);
		// System.out.println("expiryDate:"+expiryDate);

		/*
		 * String stDate = String.valueOf(icardIssueDate); if((stDate == null)
		 * ||(stDate.equals(""))) { icardIssueDate = null; } else if(stDate !=
		 * null) { icardIssueDate = new java.sql.Date
		 * (icardIssueDate.getTime());
		 * 
		 * }
		 */

		String zoneId = "";
		String branchId = "";
		String mliId = "";

		String userId = user.getUserId();
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitApp",
				"user Id :" + userId);

		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			// if(applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE)==null)
			// {

			String memberName = (String) dynaForm.get("selectMember");
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);

				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = bankId + zoneId + branchId;
				application.setMliID(mliId);

				// System.out.println("memberName:"+memberName);
			}

		} else {

			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = bankId + zoneId + branchId;
			application.setMliID(mliId);

		}
		double exposureLimit = applicaitonDAO.getExposureLimit(bankId);
		double maxExposureLimit = applicaitonDAO.getMaxExposureLimit(bankId);
		// System.out.println("exposureLimit for Bank Id :"+bankId+"----->"+exposureLimit/10000000);
		// System.out.println("Maximum ExposureLimit for "+bankId+
		// "---"+maxExposureLimit);
		if (exposureLimit > 0) {
			if ((exposureLimit / 10000000) >= maxExposureLimit) {

				throw new MessageException(
						"The Maximum Limit of Guarantee Approved Amount should be less than "
								+ maxExposureLimit);
			}
		}
		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);
		dynaForm.set("mliID", mliId);

		String applicationType = "";
		String applicationLoanType = "";

		if (applicationSession
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE) == null) {
			applicationType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitApp",
					"ApplicationLoan Type :" + applicationType);
			// System.out.println("ApplicationLoan Type :" + applicationType);
		} else {
			applicationLoanType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitApp",
					"ApplicationLoan Type :" + applicationLoanType);
			// System.out.println("ApplicationLoan Type :" + applicationType);
			application.setLoanType(applicationType); // setting the loan type

		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp",
				"Calling Bean Utils...");
		// System.out.println("ApplicationProcessingAction submitApp Calling Bean Utils...");

		BeanUtils.populate(ssiDetails, dynaForm.getMap());

		String constitutionValue = (String) dynaForm.get("constitutionOther");
		if (dynaForm.get("constitution") != null
				&& !(dynaForm.get("constitution").equals(""))) {
			if (dynaForm.get("constitution").equals("Others")) {
				ssiDetails.setConstitution(constitutionValue);
			}

		}
		// System.out.println("constitutionValue:"+constitutionValue);
		String districtOthersValue = (String) dynaForm.get("districtOthers");
		if (dynaForm.get("district") != null
				&& !(dynaForm.get("district").equals(""))) {
			if (dynaForm.get("district").equals("Others")) {
				ssiDetails.setDistrict(districtOthersValue);
			}

		}
		// System.out.println("districtOthersValue:"+districtOthersValue);
		String otherLegalIdValue = (String) dynaForm.get("otherCpLegalID");
		if (dynaForm.get("cpLegalID") != null
				&& !(dynaForm.get("cpLegalID").equals(""))) {
			if (dynaForm.get("cpLegalID").equals("Others")) {
				ssiDetails.setCpLegalID(otherLegalIdValue);
			}

		}
		// System.out.println("otherLegalIdValue:"+otherLegalIdValue);

		// added by sumumar@path for getting the ssi activity type
		String activity = (String) dynaForm.get("activityType");
		// System.out.println("Activity:"+activity);
		if (dynaForm.get("activityType") != null
				&& !(dynaForm.get("activityType").equals(""))) {
			ssiDetails.setActivityType(activity);
		}
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());

		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);
		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());

		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(securitization, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());

		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);

		}

		application.setBorrowerDetails(borrowerDetails);
		String otherSubsidyNameValue = (String) dynaForm
				.get("otherSubsidyEquityName");
		if (dynaForm.get("subsidyName") != null
				&& !(dynaForm.get("subsidyName").equals(""))) {
			if (dynaForm.get("subsidyName").equals("Others")) {
				projectOutlayDetails.setSubsidyName(otherSubsidyNameValue);
			}

		}
		// System.out.println("otherSubsidyNameValue:"+otherSubsidyNameValue);
		double termCreditSanctioned = ((java.lang.Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		double tcPromoterContribution = ((java.lang.Double) dynaForm
				.get("tcPromoterContribution")).doubleValue();
		double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("tcSubsidyOrEquity")).doubleValue();
		double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
				.doubleValue();
		double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcNonFundBasedSanctioned")).doubleValue();
		double wcPromoterContribution = ((java.lang.Double) dynaForm
				.get("wcPromoterContribution")).doubleValue();
		double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("wcSubsidyOrEquity")).doubleValue();
		double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
				.doubleValue();

		double projectOutlayVal = termCreditSanctioned + tcPromoterContribution
				+ tcSubsidyOrEquity + tcOthers + wcFundBasedSanctioned
				+ wcNonFundBasedSanctioned + wcPromoterContribution
				+ wcSubsidyOrEquity + wcOthers;
		// System.out.println("projectOutlayVal"+projectOutlayVal);

		// System.out.println("Credit to be Guarantee Amount:"+projectOutlayVal);

		Double projectOutlayCost = new Double(projectOutlayVal);
		double projectOutlay = projectOutlayCost.doubleValue();
		projectOutlayDetails.setProjectOutlay(projectOutlay);

		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setTermLoan(termLoan);
		application.setWc(workingCapital);
		application.setSecuritization(securitization);

		double creditGuaranteed = 0.0;
		double creditFundBased = 0.0;
		double creditNonFundBased = 0.0;
		String type = (String) dynaForm.get("loanType");
		// System.out.println("Loan Type:"+type);
		// System.out.println("Application Status New:"+application.getStatus());
		if (type.equals("BO")) {
			creditGuaranteed = application.getTermLoan().getCreditGuaranteed();
			creditFundBased = application.getWc().getCreditFundBased();
			creditNonFundBased = application.getWc().getCreditNonFundBased();
			// System.out.println("BO Amt:"+(creditGuaranteed+creditFundBased+creditNonFundBased));
		} else if (type.equals("CC")) {
			creditGuaranteed = application.getTermLoan().getCreditGuaranteed();
			creditFundBased = application.getWc().getCreditFundBased();
			creditNonFundBased = application.getWc().getCreditNonFundBased();
			// System.out.println("CC Amt:"+(creditGuaranteed+creditFundBased+creditNonFundBased));
		} else if (type.equals("TC")) {
			creditGuaranteed = application.getTermLoan().getCreditGuaranteed();
			// System.out.println("TC Amt:"+creditGuaranteed);
		} else if (type.equals("WC")) {
			creditFundBased = application.getWc().getCreditFundBased();
			creditNonFundBased = application.getWc().getCreditNonFundBased();
			// System.out.println("WC Amt:"+(creditFundBased+creditNonFundBased));
		}

		double credittoguaranteeamount = creditGuaranteed + creditFundBased
				+ creditNonFundBased;
		// System.out.println("credittoguaranteeamount:"+credittoguaranteeamount);

		double minValue = 999.0;
		double handloomMaxValue = 200000.0;
		double maxValue = 10000000.0;
		double rrbValue = 5000000.0;
		String classificationofMLI = applicaitonDAO
				.getClassificationMLI(bankId);

		if (dcHandlooms != null && !dcHandlooms.equals("null")
				&& unitValue.equals("")) {
			if ((dcHandlooms.equals("Y"))
					&& ((WeaverCreditScheme == null) || (WeaverCreditScheme
							.equals("Select")))) {
				throw new MessageException(
						"Please Select the Weaver Credit Scheme ");
			}

			if ((!(industrySector.equals("")) && (dcHandlooms.equals("Y")) && !(industrySector
					.equals("HANDLOOM WEAVING")))) {
				throw new MessageException(
						"Reimbursement of GF/ASF under DC(HL) is eligible only if Industry Sector/Activity of the borrower is 'handloom weaving'. Please refer our Circular No: 61/2012-13 dated 12/06/2012 for details. ");
			}

		}

		if (classificationofMLI.equals("RRB") || classificationofMLI == "RRB") {
			if (credittoguaranteeamount > rrbValue) {
				// System.out.println("credittoguaranteeamount:"+credittoguaranteeamount);
				throw new MessageException(
						"Maximum ‘credit to be guaranteed’ amount per eligible borrower for RRBs is caped at Rs.50 lakh. Please refer our <b> Circular No. 50 / 2008 – 09 </b> dated January 07, 2009  ");
			}
		}
		if (dcHandlooms != null && !dcHandlooms.equals("null")) {
			if (credittoguaranteeamount > handloomMaxValue
					&& (dcHandlooms.equals("Y"))) {

				throw new MessageException(
						"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto  Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			}
		}

		if (credittoguaranteeamount < minValue) {
			throw new MessageException(
					"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto" + 10000000);
		} else if (credittoguaranteeamount > maxValue) {
			throw new MessageException(
					"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto" + 10000000);
		}

		BeanUtils.populate(application, dynaForm.getMap());

		if (dcHandlooms != null && !dcHandlooms.equals("null")) {
			if (dcHandlooms.equals("N")) {
				application.setHandloomchk("N");
				application.setWeaverCreditScheme("");
			}
		}
		if (handiCrafts != null && !handiCrafts.equals("null")) {
			if (handiCrafts.equals("N")) {
				application.setDcHandicrafts("N");
			}
		}
		if (dynaForm.get("none").equals("cgpan")) {
			application.setCgpan((String) dynaForm.get("unitValue"));
		} else if (dynaForm.get("none").equals("cgbid")) {

			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!(borrowerIds.contains(dynaForm.get("unitValue")))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}

			application.getBorrowerDetails().getSsiDetails()
					.setCgbid((String) dynaForm.get("unitValue"));
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp",
				"application type :" + applicationType);

		// System.out.println("Line number 2605-applicationSession.getAttribute(SessionConstants.MCGF_FLAG)"+applicationSession.getAttribute(SessionConstants.MCGF_FLAG));
		// setting the scheme Name
		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			application.setScheme("MCGS");

			if (applicationSession.getAttribute("ssiRefNumber") != null) {
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"submitApp",
						"applicationSession.getAttribute ssiRefNumber"
								+ applicationSession
										.getAttribute("ssiRefNumber"));
				/**
				 * check if the credit to be guaranteed amount is greater than 5
				 * times the corpus contribution amount
				 */
				double corpusContAmt = appProcessor
						.getCorpusContAmt(((Integer) applicationSession
								.getAttribute("ssiRefNumber")).intValue());
				double totalCorpusContAmt = corpusContAmt * 5;

				double creditAmount = 0;
				if (application.getLoanType().equals("TC")) {
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed();
					// System.out.println("creditAmount:"+creditAmount);
				} else if (application.getLoanType().equals("CC")) {
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed()
							+ application.getWc().getCreditFundBased()
							+ +application.getWc().getCreditNonFundBased();
					// System.out.println("creditAmount:"+creditAmount);
				} else if (application.getLoanType().equals("WC")) {
					creditAmount = application.getWc().getCreditFundBased()
							+ application.getWc().getCreditNonFundBased();
					// System.out.println("creditAmount:"+creditAmount);
				}
				if (totalCorpusContAmt != 0) {
					if (creditAmount > totalCorpusContAmt) {
						throw new MessageException(
								"Credit Limit should not exceed the Corpus Amount");
					}

				}

				BorrowerDetails borrowerDtlTemp = application
						.getBorrowerDetails();
				SSIDetails ssiDtlTemp = borrowerDtlTemp.getSsiDetails();
				ssiDtlTemp.setBorrowerRefNo(((Integer) applicationSession
						.getAttribute("ssiRefNumber")).intValue());
				borrowerDtlTemp.setSsiDetails(ssiDtlTemp);
				application.setBorrowerDetails(borrowerDtlTemp);
			}

		} else {

			application.setScheme("CGFSI");
		}

		// setting the unit value as cgpan or cgbid

		// If the application is to be modified,then the details have to be
		// updated into the database
		if (applicationType.equals("MA")) {
			application.setCgpanReference("");

			appProcessor.updateApplication(application, userId);
			Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp",
					"After updating....");

			request.setAttribute("message", "Application Modified Successfully");

			successPage = "success";
		} else if (applicationType.equals("TCE")) {
			if (dynaForm.get("none").equals("cgpan")) {
				String cgpanRef = (String) dynaForm.get("unitValue");
				application.setCgpanReference(cgpanRef);

			} else if (dynaForm.get("none").equals("none")) {
				application.setCgpanReference("");
			}

			String appRefNo = appProcessor.submitAddlTermCredit(application,
					userId);
			Log.log(Log.INFO, "ApplicationProcessingAction", "submitApp",
					"After submitting Addtl Term Credit....");
			dynaForm.set("appRefNo", appRefNo);
			request.setAttribute("message", "Application (Reference No:"
					+ appRefNo + ")Submitted Successfully");

			successPage = "success";
		} else {

			application.setCgpanReference("");

			ClaimsProcessor claimProcessor = new ClaimsProcessor();

			String cgbid = "";

			if (application.getCgpan() != null
					&& !application.getCgpan().equals("")) {
				cgbid = claimProcessor.getBorowwerForCGPAN(application
						.getCgpan());

			} else if (application.getBorrowerDetails().getSsiDetails()
					.getCgbid() != null
					&& !(application.getBorrowerDetails().getSsiDetails()
							.getCgbid().equals(""))) {

				cgbid = application.getBorrowerDetails().getSsiDetails()
						.getCgbid();
			}

			int claimCount = appProcessor.getClaimCount(cgbid);
			if (claimCount > 0) {
				throw new MessageException(
						"Application cannot be filed by this borrower since Claim Application has been submitted");
			}

			// System.out.println("Term Loan Sanctioned Date:"+application.getTermLoan().getAmountSanctionedDate());
			// System.out.println("WC Fundbased Sanctioned Date:"+application.getWc().getLimitFundBasedSanctionedDate());
			// System.out.println("WC Fundbased Sanctioned Date:"+application.getWc().getLimitNonFundBasedSanctionedDate());

			application = appProcessor
					.submitNewApplication(application, userId);
			String appRefNo = application.getAppRefNo();
			dynaForm.set("appRefNo", appRefNo);
			int borrowerRefNo = ((application.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo();
			Integer refNoValue = new Integer(borrowerRefNo);
			dynaForm.set("borrowerRefNo", refNoValue);
			request.setAttribute("message", "Application (Reference No:"
					+ appRefNo + ")Submitted Successfully");
			// If its a both application, the second application reference
			// number generated is stored
			if (applicationLoanType.equals("BO")) {
				String wcAppRefNo = application.getWcAppRefNo();
				dynaForm.set("wcAppRefNo", wcAppRefNo);
				request.setAttribute("message", "Application (Reference Nos:"
						+ wcAppRefNo + "," + appRefNo
						+ ")Submitted Successfully");
			}
			successPage = "success";
		}

		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		securitization = null;
		mcgfDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;

		return mapping.findForward(successPage);
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
	public ActionForward submitRsfApp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
				"Entered");
		// System.out.println("Line number 3366 - ApplicationProcessingAction"+"submitRsfApp"+"Entered");
		String successPage = "";

		DynaActionForm dynaForm = (DynaActionForm) form;

		// String coFinance = request.getParameter("coFinanceTaken1");
		// String coFinance = (String)dynaForm.get("coFinanceTaken1");
		// System.out.println("Co Finance:"+coFinance);

		HttpSession applicationSession = request.getSession(false);

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
				"Creating Objects");
		// System.out.println("Line number 2786 - ApplicationProcessingAction"+"submitRsfApp"+"Creating Objects");
		// System.out.println("Line number 2787-Application Loan Type:"+applicationSession.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE));

		// System.out.println("Line number 2789-Application Flag:"+applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE_FLAG));

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();

		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		User user = getUserInformation(request);
		// System.out.println("Line number 3397-Application Type:"+application.getLoanType());
		// System.out.println("Line number 3398"+(String)dynaForm.get("loanType"));
		// String
		// loanType=(String)applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE);
		// System.out.println("Line number 2809-loan Type"+loanType);
		// Log.log(Log.DEBUG,"ApplicationProcessingAction","submitRsfApp","ApplicationLoan Type :"
		// + loanType);

		// System.out.println("CoFianceTaken1"+application.getCoFinanceTaken1());
		// if(coFinance.equals("Y")|| coFinance=="Y"){
		// application.setCoFinanceTaken1(coFinance);
		// }
		// application.setCoFinanceTaken1(coFinance);
		// System.out.println("Shyam:"+application.getCoFinanceTaken1());
		// System.out.println("Sukumar:"+dynaForm.get("coFinanceTaken1"));
		// Log.log(Log.DEBUG,"ApplicationProcessingAction","submitApp","CoFianceTaken1 :"
		// + application.getCoFinanceTaken1());
		// Log.log(Log.DEBUG,"ApplicationProcessingAction","submitApp","CoFianceTaken1 :"
		// + coFinance);
		String internalRating = (String) dynaForm.get("internalRating");
		// System.out.println("internalRating:"+internalRating);
		String externalRating = (String) dynaForm.get("externalRating");
		// System.out.println("externalRating:"+externalRating);
		application.setInternalRate(internalRating);
		application.setExternalRate(externalRating);
		// System.out.println("Line number 3458"+application.getInternalRate());
		// System.out.println("Line number 3459"+application.getExternalRate());

		String zoneId = "";
		String branchId = "";
		String mliId = "";

		String userId = user.getUserId();
		// System.out.println("Line number 3418 userId:"+userId);
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitRsfApp",
				"user Id :" + userId);

		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			// if(applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE)==null)
			// {

			String memberName = (String) dynaForm.get("selectMember");
			// System.out.println("line number 3430-"+memberName);
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);

				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = bankId + zoneId + branchId;
				application.setMliID(mliId);

				// System.out.println("memberName:"+memberName);
			}

		} else {

			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = bankId + zoneId + branchId;
			// System.out.println("Line number-3455"+mliId);
			application.setMliID(mliId);

		}
		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);
		dynaForm.set("mliID", mliId);

		String applicationType = "";
		String applicationLoanType = "";

		if (applicationSession
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE) == null) {
			applicationType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitRsfApp",
					"ApplicationLoan Type :" + applicationType);
			// System.out.println("Line number 3471 - Application Type :" +
			// applicationType);
		} else {
			applicationLoanType = (String) applicationSession
					.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitRsfApp",
					"ApplicationLoan Type :" + applicationLoanType);
			// System.out.println("Line number 3476 - Application Loan Type :" +
			// applicationLoanType);
			// application.setLoanType(ApplicationConstants.TC_APPLICATION);
			// //setting the loan type
			String type = (String) dynaForm.get("loanType");
			application.setLoanType(type);
			// System.out.println("Line Number-3480"+application.getLoanType());
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
				"Calling Bean Utils...");
		// System.out.println("Line number 3485 - ApplicationProcessingAction submitRsfApp Calling Bean Utils...");

		BeanUtils.populate(ssiDetails, dynaForm.getMap());
		// System.out.println("Line number 3488"+ssiDetails.getConstitution());
		// System.out.println("Line number 3489-"+ssiDetails.getDistrict());
		String constitutionValue = (String) dynaForm.get("constitutionOther");
		// System.out.println("Line number 3491-"+constitutionValue);
		if (dynaForm.get("constitution") != null
				&& !(dynaForm.get("constitution").equals(""))) {
			if (dynaForm.get("constitution").equals("Others")) {
				ssiDetails.setConstitution(constitutionValue);
			}

		}
		// System.out.println("constitutionValue:"+constitutionValue);
		String districtOthersValue = (String) dynaForm.get("districtOthers");
		if (dynaForm.get("district") != null
				&& !(dynaForm.get("district").equals(""))) {
			if (dynaForm.get("district").equals("Others")) {
				ssiDetails.setDistrict(districtOthersValue);
			}

		}
		// System.out.println("Line number 3510 - districtOthersValue:"+districtOthersValue);
		String otherLegalIdValue = (String) dynaForm.get("otherCpLegalID");
		// System.out.println("Line number 3512- "+otherLegalIdValue);
		if (dynaForm.get("cpLegalID") != null
				&& !(dynaForm.get("cpLegalID").equals(""))) {
			if (dynaForm.get("cpLegalID").equals("Others")) {
				ssiDetails.setCpLegalID(otherLegalIdValue);
			}

		}
		// System.out.println("otherLegalIdValue:"+otherLegalIdValue);
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());

		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);
		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());

		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(securitization, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());
		// System.out.println("Line number - 3532"+applicationSession.getAttribute(SessionConstants.MCGF_FLAG));
		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);

		}

		application.setBorrowerDetails(borrowerDetails);
		String otherSubsidyNameValue = (String) dynaForm
				.get("otherSubsidyEquityName");
		// System.out.println("Line number 3543-otherSubsidyNameValue"+otherSubsidyNameValue);
		if (dynaForm.get("subsidyName") != null
				&& !(dynaForm.get("subsidyName").equals(""))) {
			if (dynaForm.get("subsidyName").equals("Others")) {
				projectOutlayDetails.setSubsidyName(otherSubsidyNameValue);
			}

		}
		// System.out.println("otherSubsidyNameValue:"+otherSubsidyNameValue);
		double termCreditSanctioned = ((java.lang.Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		// System.out.println("Line number 3554-termCreditSanctioned"+termCreditSanctioned);
		double tcPromoterContribution = ((java.lang.Double) dynaForm
				.get("tcPromoterContribution")).doubleValue();
		// System.out.println("Line number 3556-tcPromoterContribution"+tcPromoterContribution);
		double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("tcSubsidyOrEquity")).doubleValue();
		// System.out.println("Line number 3558-tcSubsidyOrEquity"+tcSubsidyOrEquity);
		double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
				.doubleValue();
		double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		// System.out.println("Line number 3561 wcFundBasedSanctioned:"+wcFundBasedSanctioned
		// );
		double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcNonFundBasedSanctioned")).doubleValue();
		// System.out.println("Line number 3563 wcNonFundBasedSanctioned:"+wcNonFundBasedSanctioned
		// );
		double wcPromoterContribution = ((java.lang.Double) dynaForm
				.get("wcPromoterContribution")).doubleValue();
		double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("wcSubsidyOrEquity")).doubleValue();
		double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
				.doubleValue();

		double projectOutlayVal = termCreditSanctioned + tcPromoterContribution
				+ tcSubsidyOrEquity + tcOthers + wcFundBasedSanctioned
				+ wcNonFundBasedSanctioned + wcPromoterContribution
				+ wcSubsidyOrEquity + wcOthers;
		// System.out.println("Line number-3571-projectOutlayVal"+projectOutlayVal);
		// System.out.println("Line number 3572 Application Loan Type"+application.getLoanType());
		// System.out.println("Line number 3573 Application Scheme Name"+applicationLoanType);
		double minValue = 5000000.0;
		double maxValue = 10000000.0;
		/*
		 * if(projectOutlayVal<minValue || projectOutlayVal>maxValue){ throw new
		 * MessageException(
		 * "Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
		 * + 10000000); }
		 */
		if (projectOutlayVal < minValue) {
			throw new MessageException(
					"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :" + 10000000);
		}
		Double projectOutlayCost = new Double(projectOutlayVal);
		double projectOutlay = projectOutlayCost.doubleValue();
		projectOutlayDetails.setProjectOutlay(projectOutlay);

		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setTermLoan(termLoan);
		application.setWc(workingCapital);
		application.setSecuritization(securitization);

		BeanUtils.populate(application, dynaForm.getMap());

		if (dynaForm.get("none").equals("cgpan")) {
			application.setCgpan((String) dynaForm.get("unitValue"));
		} else if (dynaForm.get("none").equals("cgbid")) {

			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!(borrowerIds.contains(dynaForm.get("unitValue")))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}

			application.getBorrowerDetails().getSsiDetails()
					.setCgbid((String) dynaForm.get("unitValue"));
		}

		Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
				"application type :" + applicationType);

		// setting the scheme Name
		// System.out.println("Line number-3001"+applicationSession.getAttribute(SessionConstants.MCGF_FLAG));
		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			application.setScheme("MCGS");

			if (applicationSession.getAttribute("ssiRefNumber") != null) {
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"submitRsfApp",
						"applicationSession.getAttribute ssiRefNumber"
								+ applicationSession
										.getAttribute("ssiRefNumber"));
				/**
				 * check if the credit to be guaranteed amount is greater than 5
				 * times the corpus contribution amount
				 */
				double corpusContAmt = appProcessor
						.getCorpusContAmt(((Integer) applicationSession
								.getAttribute("ssiRefNumber")).intValue());
				double totalCorpusContAmt = corpusContAmt * 5;
				// System.out.println("Line number-3014"+totalCorpusContAmt);
				double creditAmount = 0;
				if (application.getLoanType().equals("TC")) {
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed();
					// System.out.println("Line number - 3618 - creditAmount:"+creditAmount);
				} else if (application.getLoanType().equals("CC")) {
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed()
							+ application.getWc().getCreditFundBased()
							+ +application.getWc().getCreditNonFundBased();
					// System.out.println("Line number 3623 - creditAmount:"+creditAmount);
				} else if (application.getLoanType().equals("WC")) {
					creditAmount = application.getWc().getCreditFundBased()
							+ application.getWc().getCreditNonFundBased();
					// System.out.println("Line number 3628 - creditAmount:"+creditAmount);
				}
				// System.out.println("Line number 3630-totalCorpusContAmt"+totalCorpusContAmt);
				// System.out.println("Line number 3631-creditAmount"+creditAmount);
				if (totalCorpusContAmt != 0) {
					if (creditAmount > totalCorpusContAmt) {
						// System.out.println("Credit Limit should not exceed the Corpus Amount");
						throw new MessageException(
								"Credit Limit should not exceed the Corpus Amount");
					}

				}

				BorrowerDetails borrowerDtlTemp = application
						.getBorrowerDetails();
				SSIDetails ssiDtlTemp = borrowerDtlTemp.getSsiDetails();
				ssiDtlTemp.setBorrowerRefNo(((Integer) applicationSession
						.getAttribute("ssiRefNumber")).intValue());
				borrowerDtlTemp.setSsiDetails(ssiDtlTemp);
				application.setBorrowerDetails(borrowerDtlTemp);
			}

		} else {

			application.setScheme("RSF");
		}

		// System.out.println("Application Processing Action Line number 3655 Application Loan Type"+applicationType);
		// setting the unit value as cgpan or cgbid

		// If the application is to be modified,then the details have to be
		// updated into the database
		if (applicationType.equals("MA")) {
			application.setCgpanReference("");

			appProcessor.updateApplication(application, userId);
			Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
					"After updating....");
			// System.out.println("ApplicationProcessingAction submitRsfApp After updating....");
			request.setAttribute("message", "Application Modified Successfully");

			successPage = "success";
		} else if (applicationType.equals("TCE")) {
			if (dynaForm.get("none").equals("cgpan")) {
				String cgpanRef = (String) dynaForm.get("unitValue");
				application.setCgpanReference(cgpanRef);

			} else if (dynaForm.get("none").equals("none")) {
				application.setCgpanReference("");
			}
			// System.out.println("application.getCgpanReference:"+application.getCgpanReference());
			String appRefNo = appProcessor.submitAddlTermCredit(application,
					userId);
			Log.log(Log.INFO, "ApplicationProcessingAction", "submitRsfApp",
					"After submitting Addtl Term Credit....");
			dynaForm.set("appRefNo", appRefNo);
			request.setAttribute("message", "Application (Reference No:"
					+ appRefNo + ")Submitted Successfully");

			successPage = "success";
		} else {

			application.setCgpanReference("");

			ClaimsProcessor claimProcessor = new ClaimsProcessor();

			String cgbid = "";

			if (application.getCgpan() != null
					&& !application.getCgpan().equals("")) {
				cgbid = claimProcessor.getBorowwerForCGPAN(application
						.getCgpan());
				// System.out.println("Line number-3101"+cgbid);

			} else if (application.getBorrowerDetails().getSsiDetails()
					.getCgbid() != null
					&& !(application.getBorrowerDetails().getSsiDetails()
							.getCgbid().equals(""))) {

				cgbid = application.getBorrowerDetails().getSsiDetails()
						.getCgbid();
				// System.out.println("Line number-3107"+cgbid);
			}

			int claimCount = appProcessor.getClaimCount(cgbid);
			if (claimCount > 0) {
				// System.out.println("Application cannot be filed by this borrower since Claim Application has been submitted");
				throw new MessageException(
						"Application cannot be filed by this borrower since Claim Application has been submitted");
			}
			// System.out.println("Line number - 3717-application.getLoanType"+application.getLoanType());
			application.setLoanType(application.getLoanType());

			application.getBorrowerDetails().getSsiDetails()
					.setEnterprise((String) dynaForm.get("enterprise"));
			// System.out.println("Line number 3721-EnterPrise"+(String)dynaForm.get("enterprise"));
			application.getBorrowerDetails().getSsiDetails()
					.setUnitAssisted((String) dynaForm.get("unitAssisted"));
			// System.out.println("Line number 3723-UnitAssisted"+
			// (String)dynaForm.get("unitAssisted"));
			application.getBorrowerDetails().getSsiDetails()
					.setWomenOperated((String) dynaForm.get("womenOperated"));
			// System.out.println("Line number 3725-Women Operated"+(String)dynaForm.get("womenOperated"));

			application = appProcessor.submitNewRSFApplication(application,
					userId);

			String appRefNo = application.getAppRefNo();
			// System.out.println("Line Number 3730-ApplicationRef Number:"+appRefNo);
			dynaForm.set("appRefNo", appRefNo);
			int borrowerRefNo = ((application.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo();
			Integer refNoValue = new Integer(borrowerRefNo);
			dynaForm.set("borrowerRefNo", refNoValue);
			request.setAttribute("message", "Application (Reference No:"
					+ appRefNo + ")Submitted Successfully");
			// If its a both application, the second application reference
			// number generated is stored
			if (applicationLoanType.equals("BO")) {
				String wcAppRefNo = application.getWcAppRefNo();
				dynaForm.set("wcAppRefNo", wcAppRefNo);
				request.setAttribute("message", "Application (Reference Nos:"
						+ wcAppRefNo + "," + appRefNo
						+ ")Submitted Successfully");
			}
			successPage = "success";
		}

		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		securitization = null;
		mcgfDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;

		return mapping.findForward(successPage);
	}

	public ActionForward submitRsf2App(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ApplicationProcessingAction", "submitRsfApp", "Entered");
		String successPage = "";
		DynaActionForm dynaForm = (DynaActionForm) form;
		HttpSession applicationSession = request.getSession(false);
		Log.log(4, "ApplicationProcessingAction", "submitRsf2App",
				"Creating Objects");
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();
		ClaimsProcessor cpProcessor = new ClaimsProcessor();
		User user = getUserInformation(request);
		String internalRating = (String) dynaForm.get("internalRating");
		String externalRating = (String) dynaForm.get("externalRating");
		application.setInternalRate(internalRating);
		application.setExternalRate(externalRating);
		String zoneId = "";
		String branchId = "";
		String mliId = "";
		String userId = user.getUserId();
		Log.log(5, "ApplicationProcessingAction", "submitRsf2App",
				(new StringBuilder()).append("user Id :").append(userId)
						.toString());
		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();
		if (bankId.equals("0000")) {
			String memberName = (String) dynaForm.get("selectMember");
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);
				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = (new StringBuilder()).append(bankId).append(zoneId)
						.append(branchId).toString();
				application.setMliID(mliId);
			}
		} else {
			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = (new StringBuilder()).append(bankId).append(zoneId)
					.append(branchId).toString();
			application.setMliID(mliId);
		}
		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);
		dynaForm.set("mliID", mliId);
		String applicationType = "";
		String applicationLoanType = "";
		if (applicationSession.getAttribute("APPLICATION_LOAN_TYPE") == null) {
			applicationType = (String) applicationSession
					.getAttribute("APPLICATION_TYPE");
			Log.log(5, "ApplicationProcessingAction", "submitRsf2App",
					(new StringBuilder()).append("ApplicationLoan Type :")
							.append(applicationType).toString());
		} else {
			applicationLoanType = (String) applicationSession
					.getAttribute("APPLICATION_LOAN_TYPE");
			Log.log(5, "ApplicationProcessingAction", "submitRsf2App",
					(new StringBuilder()).append("ApplicationLoan Type :")
							.append(applicationLoanType).toString());
			String type = (String) dynaForm.get("loanType");
			application.setLoanType(type);
		}
		Log.log(4, "ApplicationProcessingAction", "submitRsf2App",
				"Calling Bean Utils...");
		BeanUtils.populate(ssiDetails, dynaForm.getMap());
		String constitutionValue = (String) dynaForm.get("constitutionOther");
		if (dynaForm.get("constitution") != null
				&& !dynaForm.get("constitution").equals("")
				&& dynaForm.get("constitution").equals("Others"))
			ssiDetails.setConstitution(constitutionValue);
		String districtOthersValue = (String) dynaForm.get("districtOthers");
		if (dynaForm.get("district") != null
				&& !dynaForm.get("district").equals("")
				&& dynaForm.get("district").equals("Others"))
			ssiDetails.setDistrict(districtOthersValue);
		String otherLegalIdValue = (String) dynaForm.get("otherCpLegalID");
		if (dynaForm.get("cpLegalID") != null
				&& !dynaForm.get("cpLegalID").equals("")
				&& dynaForm.get("cpLegalID").equals("Others"))
			ssiDetails.setCpLegalID(otherLegalIdValue);
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());
		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);
		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());
		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(securitization, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());
		if (applicationSession.getAttribute("MCGF_FLAG").equals("M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);
		}
		application.setBorrowerDetails(borrowerDetails);
		String otherSubsidyNameValue = (String) dynaForm
				.get("otherSubsidyEquityName");
		if (dynaForm.get("subsidyName") != null
				&& !dynaForm.get("subsidyName").equals("")
				&& dynaForm.get("subsidyName").equals("Others"))
			projectOutlayDetails.setSubsidyName(otherSubsidyNameValue);
		double termCreditSanctioned = ((Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		double tcPromoterContribution = ((Double) dynaForm
				.get("tcPromoterContribution")).doubleValue();
		double tcSubsidyOrEquity = ((Double) dynaForm.get("tcSubsidyOrEquity"))
				.doubleValue();
		double tcOthers = ((Double) dynaForm.get("tcOthers")).doubleValue();
		double wcFundBasedSanctioned = ((Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		double wcNonFundBasedSanctioned = ((Double) dynaForm
				.get("wcNonFundBasedSanctioned")).doubleValue();
		double wcPromoterContribution = ((Double) dynaForm
				.get("wcPromoterContribution")).doubleValue();
		double wcSubsidyOrEquity = ((Double) dynaForm.get("wcSubsidyOrEquity"))
				.doubleValue();
		double wcOthers = ((Double) dynaForm.get("wcOthers")).doubleValue();
		double termCreditToGuarantee = 0.0D;
		double wcFBCreditToGuarantee = 0.0D;
		double wcNFBCreditToGuarantee = 0.0D;
		if (application.getLoanType().equals("TC")
				|| application.getLoanType().equals("BO")) {
			String tcg = dynaForm.get("creditGuaranteed").toString();
			if (tcg.equals(null) || tcg.equals(""))
				termCreditToGuarantee = 0.0D;
			else
				termCreditToGuarantee = ((Double) dynaForm
						.get("creditGuaranteed")).doubleValue();
		}
		if (application.getLoanType().equals("WC")
				|| application.getLoanType().equals("BO")) {
			String fbcg = dynaForm.get("creditFundBased").toString();
			String nfbcg = dynaForm.get("creditNonFundBased").toString();
			if (fbcg.equals(null) || fbcg.equals(""))
				wcFBCreditToGuarantee = 0.0D;
			else
				wcFBCreditToGuarantee = ((Double) dynaForm
						.get("creditFundBased")).doubleValue();
			if (nfbcg.equals(null) || nfbcg.equals(""))
				wcNFBCreditToGuarantee = 0.0D;
			else
				wcNFBCreditToGuarantee = ((Double) dynaForm
						.get("creditNonFundBased")).doubleValue();
		}
		double projectOutlayVal = termCreditToGuarantee + wcFBCreditToGuarantee
				+ wcNFBCreditToGuarantee;
		double minValue = 10000000D;
		double maxValue = 20000000D;
		if (projectOutlayVal < minValue || projectOutlayVal > maxValue)
			throw new MessageException(
					"Credit to be Guaranteed Amount for RSF 2 should be within the eligible amount i.e between 10000000 and 20000000");
		Double projectOutlayCost = new Double(projectOutlayVal);
		double projectOutlay = projectOutlayCost.doubleValue();
		projectOutlayDetails.setProjectOutlay(projectOutlay);
		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setTermLoan(termLoan);
		application.setWc(workingCapital);
		application.setSecuritization(securitization);
		BeanUtils.populate(application, dynaForm.getMap());
		if (dynaForm.get("none").equals("cgpan"))
			application.setCgpan((String) dynaForm.get("unitValue"));
		else if (dynaForm.get("none").equals("cgbid")) {
			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);
			if (!borrowerIds.contains(dynaForm.get("unitValue")))
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			application.getBorrowerDetails().getSsiDetails()
					.setCgbid((String) dynaForm.get("unitValue"));
		}
		Log.log(4,
				"ApplicationProcessingAction",
				"submitRsf2App",
				(new StringBuilder()).append("application type :")
						.append(applicationType).toString());
		if (applicationSession.getAttribute("MCGF_FLAG").equals("M")) {
			application.setScheme("MCGS");
			if (applicationSession.getAttribute("ssiRefNumber") != null) {
				Log.log(4,
						"ApplicationProcessingAction",
						"submitRsf2App",
						(new StringBuilder())
								.append("applicationSession.getAttribute ssiRefNumber")
								.append(applicationSession
										.getAttribute("ssiRefNumber"))
								.toString());
				double corpusContAmt = appProcessor
						.getCorpusContAmt(((Integer) applicationSession
								.getAttribute("ssiRefNumber")).intValue());
				double totalCorpusContAmt = corpusContAmt * 5D;
				double creditAmount = 0.0D;
				if (application.getLoanType().equals("TC"))
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed();
				else if (application.getLoanType().equals("CC"))
					creditAmount = application.getTermLoan()
							.getCreditGuaranteed()
							+ application.getWc().getCreditFundBased()
							+ application.getWc().getCreditNonFundBased();
				else if (application.getLoanType().equals("WC"))
					creditAmount = application.getWc().getCreditFundBased()
							+ application.getWc().getCreditNonFundBased();
				if (totalCorpusContAmt != 0.0D
						&& creditAmount > totalCorpusContAmt)
					throw new MessageException(
							"Credit Limit should not exceed the Corpus Amount");
				BorrowerDetails borrowerDtlTemp = application
						.getBorrowerDetails();
				SSIDetails ssiDtlTemp = borrowerDtlTemp.getSsiDetails();
				ssiDtlTemp.setBorrowerRefNo(((Integer) applicationSession
						.getAttribute("ssiRefNumber")).intValue());
				borrowerDtlTemp.setSsiDetails(ssiDtlTemp);
				application.setBorrowerDetails(borrowerDtlTemp);
			}
		} else {
			application.setScheme("RSF2");
		}
		if (applicationType.equals("MA")) {
			application.setCgpanReference("");
			appProcessor.updateApplication(application, userId);
			Log.log(4, "ApplicationProcessingAction", "submitRsf2App",
					"After updating....");
			request.setAttribute("message", "Application Modified Successfully");
			successPage = "success";
		} else if (applicationType.equals("TCE")) {
			if (dynaForm.get("none").equals("cgpan")) {
				String cgpanRef = (String) dynaForm.get("unitValue");
				application.setCgpanReference(cgpanRef);
			} else if (dynaForm.get("none").equals("none"))
				application.setCgpanReference("");
			System.out.println((new StringBuilder())
					.append("application.getCgpanReference1:")
					.append(application.getCgpanReference()).toString());
			String appRefNo = appProcessor.submitAddlTermCredit(application,
					userId);
			Log.log(4, "ApplicationProcessingAction", "submitRsf2App",
					"After submitting Addtl Term Credit....");
			dynaForm.set("appRefNo", appRefNo);
			request.setAttribute("message",
					(new StringBuilder()).append("Application (Reference No:")
							.append(appRefNo).append(")Submitted Successfully")
							.toString());
			successPage = "success";
		} else {
			application.setCgpanReference("");
			ClaimsProcessor claimProcessor = new ClaimsProcessor();
			String cgbid = "";
			if (application.getCgpan() != null
					&& !application.getCgpan().equals(""))
				cgbid = claimProcessor.getBorowwerForCGPAN(application
						.getCgpan());
			else if (application.getBorrowerDetails().getSsiDetails()
					.getCgbid() != null
					&& !application.getBorrowerDetails().getSsiDetails()
							.getCgbid().equals(""))
				cgbid = application.getBorrowerDetails().getSsiDetails()
						.getCgbid();
			int claimCount = appProcessor.getClaimCount(cgbid);
			if (claimCount > 0)
				throw new MessageException(
						"Application cannot be filed by this borrower since Claim Application has been submitted");
			application.setLoanType(application.getLoanType());
			application.getBorrowerDetails().getSsiDetails()
					.setEnterprise((String) dynaForm.get("enterprise"));
			application.getBorrowerDetails().getSsiDetails()
					.setUnitAssisted((String) dynaForm.get("unitAssisted"));
			application.getBorrowerDetails().getSsiDetails()
					.setWomenOperated((String) dynaForm.get("womenOperated"));
			System.out.println((new StringBuilder())
					.append("submitNewRSF2Application:")
					.append(application.getCgpanReference()).toString());
			application = appProcessor.submitNewRSF2Application(application,
					userId);
			String appRefNo = application.getAppRefNo();
			dynaForm.set("appRefNo", appRefNo);
			int borrowerRefNo = application.getBorrowerDetails()
					.getSsiDetails().getBorrowerRefNo();
			Integer refNoValue = new Integer(borrowerRefNo);
			dynaForm.set("borrowerRefNo", refNoValue);
			request.setAttribute("message",
					(new StringBuilder()).append("Application (Reference No:")
							.append(appRefNo).append(")Submitted Successfully")
							.toString());
			if (applicationLoanType.equals("BO")) {
				String wcAppRefNo = application.getWcAppRefNo();
				dynaForm.set("wcAppRefNo", wcAppRefNo);
				request.setAttribute(
						"message",
						(new StringBuilder())
								.append("Application (Reference Nos:")
								.append(wcAppRefNo).append(",")
								.append(appRefNo)
								.append(")Submitted Successfully").toString());
			}
			successPage = "success";
		}
		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		securitization = null;
		mcgfDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;
		return mapping.findForward(successPage);
	}

	/*
	 * This method submits the renewal Application
	 */
	public ActionForward afterWcRenewalApp(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession applicationSession = request.getSession(false);

		DynaActionForm dynaForm = (DynaActionForm) form;
		Application application = new Application();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		SSIDetails ssiDetails = new SSIDetails();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		TermLoan termLoan = new TermLoan();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();

		User user = getUserInformation(request);

		// added by sukumar@path on 11-11-2010 for capturing application type
		String applicationTypes = (String) applicationSession
				.getAttribute(SessionConstants.APPLICATION_TYPE);
		// System.out.println("applicationTypes in 5533:"+applicationTypes);

		String zoneId = "";
		String branchId = "";
		String mliId = "";

		String userId = user.getUserId();
		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			// if(applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE)==null)
			// {

			String memberName = (String) dynaForm.get("selectMember");
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);

				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = bankId + zoneId + branchId;
				application.setMliID(mliId);

			}
		} else {

			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = bankId + zoneId + branchId;
			application.setMliID(mliId);

		}

		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);

		BeanUtils.populate(ssiDetails, dynaForm.getMap());

		String constitutionValue = (String) dynaForm.get("constitutionOther");
		if (dynaForm.get("constitution") != null
				&& !(dynaForm.get("constitution").equals(""))) {
			if (dynaForm.get("constitution").equals("Others")) {
				ssiDetails.setConstitution(constitutionValue);
			}

		}

		String districtOthersValue = (String) dynaForm.get("districtOthers");
		if (dynaForm.get("district") != null
				&& !(dynaForm.get("district").equals(""))) {
			if (dynaForm.get("district").equals("Others")) {
				ssiDetails.setDistrict(districtOthersValue);
			}

		}

		String otherLegalIdValue = (String) dynaForm.get("otherCpLegalID");
		if (dynaForm.get("cpLegalID") != null
				&& !(dynaForm.get("cpLegalID").equals(""))) {
			if (dynaForm.get("cpLegalID") != null
					&& !(dynaForm.get("cpLegalID").equals(""))) {
				if (dynaForm.get("cpLegalID").equals("Others")) {
					ssiDetails.setCpLegalID(otherLegalIdValue);
				}
			}
		}

		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());
		application.setBorrowerDetails(borrowerDetails);

		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);

		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());

		String otherSubsidyNameValue = (String) dynaForm
				.get("otherSubsidyEquityName");
		if (dynaForm.get("subsidyName") != null
				&& !(dynaForm.get("subsidyName").equals(""))) {
			if (dynaForm.get("subsidyName").equals("Others")) {
				projectOutlayDetails.setSubsidyName(otherSubsidyNameValue);
			}

		}

		double termCreditSanctioned = ((java.lang.Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		double tcPromoterContribution = ((java.lang.Double) dynaForm
				.get("tcPromoterContribution")).doubleValue();
		double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("tcSubsidyOrEquity")).doubleValue();
		double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
				.doubleValue();
		double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcNonFundBasedSanctioned")).doubleValue();
		double wcPromoterContribution = ((java.lang.Double) dynaForm
				.get("wcPromoterContribution")).doubleValue();
		double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("wcSubsidyOrEquity")).doubleValue();
		double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
				.doubleValue();

		double projectOutlayVal = termCreditSanctioned + tcPromoterContribution
				+ tcSubsidyOrEquity + tcOthers + wcFundBasedSanctioned
				+ wcNonFundBasedSanctioned + wcPromoterContribution
				+ wcSubsidyOrEquity + wcOthers;
		Double projectOutlayValue = new Double(projectOutlayVal);
		double projectOutlay = projectOutlayValue.doubleValue();
		String handiCrafts = (String) dynaForm.get("handiCrafts");
		String dcHandicrafts = (String) dynaForm.get("dcHandicrafts");
		String icardNo = (String) dynaForm.get("icardNo");
		java.util.Date icardIssueDate = (java.util.Date) dynaForm
				.get("icardIssueDate");
		String dcHandlooms = (String) dynaForm.get("dcHandlooms");
		String WeaverCreditScheme = (String) dynaForm.get("WeaverCreditScheme");
		String handloomchk = (String) dynaForm.get("handloomchk");

		projectOutlayDetails.setProjectOutlay(projectOutlay);

		application.setProjectOutlayDetails(projectOutlayDetails);

		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());

		application.setTermLoan(termLoan);
		application.setWc(workingCapital);

		application.setHandiCrafts(handiCrafts);
		application.setDcHandicrafts(dcHandicrafts);
		application.setIcardNo(icardNo);
		application.setIcardIssueDate(icardIssueDate);
		application.setDcHandlooms(dcHandlooms);
		application.setWeaverCreditScheme(WeaverCreditScheme);
		application.setHandloomchk(handloomchk);

		BeanUtils.populate(workingCapital, dynaForm.getMap());

		double renewalFBInterest = ((java.lang.Double) dynaForm
				.get("renewalFBInterest")).doubleValue();

		workingCapital.setLimitFundBasedInterest(renewalFBInterest);
		double renewalNFBComission = ((java.lang.Double) dynaForm
				.get("renewalNFBComission")).doubleValue();
		workingCapital.setLimitNonFundBasedCommission(renewalNFBComission);
		java.util.Date renewalDate = (java.util.Date) dynaForm
				.get("renewalDate");
		Double wcFundBasedSanctionedVal = new Double(wcFundBasedSanctioned);
		Double wcNonFundBasedSanctionedVal = new Double(
				wcNonFundBasedSanctioned);
		dynaForm.set("renewalFundBased", wcFundBasedSanctionedVal);
		dynaForm.set("renewalNonFundBased", wcNonFundBasedSanctionedVal);

		// workingCapital.setRenewalFundBased(renewalFB);
		// workingCapital.setRenewalFBInterest(renewalFBInterest);
		// workingCapital.setRenewalNonFundBased(renewalNFB);
		// workingCapital.setRenewalNFBCommission(renewalNFBComission);

		if (wcFundBasedSanctioned == 0) {
			workingCapital.setLimitNonFundBasedSanctionedDate(renewalDate);
		} else if (wcNonFundBasedSanctioned == 0) {
			workingCapital.setLimitFundBasedSanctionedDate(renewalDate);
		} else {
			workingCapital.setLimitNonFundBasedSanctionedDate(renewalDate);
			workingCapital.setLimitFundBasedSanctionedDate(renewalDate);
		}

		application.setWc(workingCapital);
		BeanUtils.populate(securitization, dynaForm.getMap());
		application.setSecuritization(securitization);

		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);

		}

		BeanUtils.populate(application, dynaForm.getMap());

		if (dcHandlooms != null && !dcHandlooms.equals("null")) {
			if (dcHandlooms.equals("N")) {
				application.setHandloomchk("N");
				application.setWeaverCreditScheme("");
			}

		}

		if (handiCrafts != null && !handiCrafts.equals("null")) {
			if (handiCrafts.equals("N")) {
				application.setDcHandicrafts("N");
			}
		}
		application.setBankId(bankId);
		application.setZoneId(zoneId);
		application.setBranchId(branchId);
		MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
				branchId);
		String mcgfFlag = mliInfo.getSupportMCGF();
		if (mcgfFlag.equals("Y")) {
			application.setScheme("MCGS");
		} else {

			application.setScheme("CGFSI");
		}

		if (dynaForm.get("unitValue") != null
				&& !(dynaForm.get("unitValue").equals(""))) {
			String cgpan = (String) dynaForm.get("unitValue");
			// System.out.println("cgpan:"+cgpan);
			application.setCgpan(cgpan);
			application.setCgpanReference(cgpan);

		}

		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			if (dynaForm.get("unitValue") != null
					&& !(dynaForm.get("unitValue").equals(""))) {

				borrowerDetails = appProcessor.fetchBorrowerDetails("",
						(String) dynaForm.get("unitValue"));
				double corpusContAmt = appProcessor
						.getCorpusContAmt(borrowerDetails.getSsiDetails()
								.getBorrowerRefNo());
				double totalCorpusContAmt = corpusContAmt * 5;

				double creditAmount = 0;
				creditAmount = application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased();
				if (totalCorpusContAmt != 0) {
					if (creditAmount > totalCorpusContAmt) {
						throw new MessageException(
								"Credit Limit should not exceed the Corpus Amount");
					}

				}
			}
		}

		if (applicationTypes.equals("WCR")) {
			System.out.println("Renewal of Application:"
					+ application.getCgpan());
		}

		String appRefNo = appProcessor.submitWcRenewal(application, userId);
		dynaForm.set("appRefNo", appRefNo);
		request.setAttribute("message", "Application (Reference No:" + appRefNo
				+ ")Submitted Successfully");

		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		securitization = null;
		mcgfDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;
		mliInfo = null;

		return mapping.findForward("success");

	}

	/*
	 * This method submits the enhancement Application
	 */
	public ActionForward afterWcEnhanceApp(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession applicationSession = request.getSession(false);

		Application application = new Application();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		SSIDetails ssiDetails = new SSIDetails();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();
		MCGFDetails mcgfDetails = new MCGFDetails();

		User user = getUserInformation(request);

		String zoneId = "";
		String branchId = "";
		String mliId = "";
		System.out.println("in  Application11");
		String userId = user.getUserId();
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "submitApp",
				"user Id :" + userId);

		String bankId = user.getBankId();
		zoneId = user.getZoneId();
		branchId = user.getBranchId();
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			// if(applicationSession.getAttribute(SessionConstants.APPLICATION_TYPE)==null)
			// {

			String memberName = (String) dynaForm.get("selectMember");
			if (memberName != null) {
				bankId = memberName.substring(0, 4);
				zoneId = memberName.substring(4, 8);
				branchId = memberName.substring(8, 12);

				application.setBankId(bankId);
				application.setZoneId(zoneId);
				application.setBranchId(branchId);
				mliId = bankId + zoneId + branchId;
				application.setMliID(mliId);

			}
		} else {

			bankId = user.getBankId();
			application.setBankId(bankId);
			zoneId = user.getZoneId();
			application.setZoneId(zoneId);
			branchId = user.getBranchId();
			application.setBranchId(branchId);
			mliId = bankId + zoneId + branchId;
			application.setMliID(mliId);

		}

		dynaForm.set("bankId", bankId);
		dynaForm.set("zoneId", zoneId);
		dynaForm.set("branchId", branchId);

		BeanUtils.populate(ssiDetails, dynaForm.getMap());

		String constitutionValue = (String) dynaForm.get("constitutionOther");
		if (dynaForm.get("constitution") != null
				&& !(dynaForm.get("constitution").equals(""))) {
			if (dynaForm.get("constitution").equals("Others")) {
				ssiDetails.setConstitution(constitutionValue);
			}

		}

		String districtOthersValue = (String) dynaForm.get("districtOthers");
		if (dynaForm.get("district") != null
				&& !(dynaForm.get("district").equals(""))) {
			if (dynaForm.get("district").equals("Others")) {
				ssiDetails.setDistrict(districtOthersValue);
			}

		}

		String otherLegalIdValue = (String) dynaForm.get("otherCpLegalID");
		if (dynaForm.get("cpLegalID") != null
				&& !(dynaForm.get("cpLegalID").equals(""))) {
			if (dynaForm.get("cpLegalID").equals("Others")) {
				ssiDetails.setCpLegalID(otherLegalIdValue);
			}

		}

		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());

		BeanUtils.populate(primarySecurityDetails, dynaForm.getMap());
		projectOutlayDetails.setPrimarySecurityDetails(primarySecurityDetails);
		BeanUtils.populate(projectOutlayDetails, dynaForm.getMap());

		BeanUtils.populate(termLoan, dynaForm.getMap());
		BeanUtils.populate(workingCapital, dynaForm.getMap());

		application.setBorrowerDetails(borrowerDetails);
		String otherSubsidyNameValue = (String) dynaForm
				.get("otherSubsidyEquityName");
		if (dynaForm.get("subsidyName") != null
				&& !(dynaForm.get("subsidyName").equals(""))) {
			if (dynaForm.get("subsidyName").equals("Others")) {
				projectOutlayDetails.setSubsidyName(otherSubsidyNameValue);
			}

		}
		String handiCrafts = (String) dynaForm.get("handiCrafts");
		String dcHandicrafts = (String) dynaForm.get("dcHandicrafts");
		String icardNo = (String) dynaForm.get("icardNo");
		java.util.Date icardIssueDate = (java.util.Date) dynaForm
				.get("icardIssueDate");
		String dcHandlooms = (String) dynaForm.get("dcHandlooms");

		String WeaverCreditScheme = (String) dynaForm.get("WeaverCreditScheme");

		String handloomchk = (String) dynaForm.get("handloomchk");
		System.out.println("in  Application12");

		double termCreditSanctioned = ((java.lang.Double) dynaForm
				.get("termCreditSanctioned")).doubleValue();
		double tcPromoterContribution = ((java.lang.Double) dynaForm
				.get("tcPromoterContribution")).doubleValue();
		double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("tcSubsidyOrEquity")).doubleValue();
		double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
				.doubleValue();
		double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcFundBasedSanctioned")).doubleValue();
		double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
				.get("wcNonFundBasedSanctioned")).doubleValue();
		double wcPromoterContribution = ((java.lang.Double) dynaForm
				.get("wcPromoterContribution")).doubleValue();
		double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
				.get("wcSubsidyOrEquity")).doubleValue();
		double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
				.doubleValue();

		double projectOutlayVal = termCreditSanctioned + tcPromoterContribution
				+ tcSubsidyOrEquity + tcOthers + wcFundBasedSanctioned
				+ wcNonFundBasedSanctioned + wcPromoterContribution
				+ wcSubsidyOrEquity + wcOthers;
		Double projectOutlayValue = new Double(projectOutlayVal);
		double projectOutlay = projectOutlayValue.doubleValue();
		projectOutlayDetails.setProjectOutlay(projectOutlay);

		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setTermLoan(termLoan);

		Double wcFundBasedSanctionedValue = new Double(wcFundBasedSanctioned);
		double wcFundBasedSanctionedVal = wcFundBasedSanctionedValue
				.doubleValue();
		Double wcNonFundBasedSanctionedValue = new Double(
				wcNonFundBasedSanctioned);
		double wcNonFundBasedSanctionedVal = wcNonFundBasedSanctionedValue
				.doubleValue();

		double fundBasedInterest = ((java.lang.Double) dynaForm
				.get("enhancedFBInterest")).doubleValue();
		workingCapital.setLimitFundBasedInterest(fundBasedInterest);

		double nonfundBasedCommission = ((java.lang.Double) dynaForm
				.get("enhancedNFBComission")).doubleValue();
		workingCapital.setLimitNonFundBasedCommission(nonfundBasedCommission);

		workingCapital.setEnhancedFundBased(wcFundBasedSanctionedVal);
		workingCapital.setEnhancedNonFundBased(wcNonFundBasedSanctionedVal);
		workingCapital.setWcInterestType("T");
		workingCapital.setCreditFundBased(wcFundBasedSanctionedVal);
		workingCapital.setCreditNonFundBased(wcNonFundBasedSanctionedVal);
		application.setWc(workingCapital);
		Log.log(Log.ERROR, "ApplicationProcessingAction", "afterWcEnhanceApp",
				"app ref no from dynaform :" + dynaForm.get("appRefNo"));

		BeanUtils.populate(securitization, dynaForm.getMap());

		application.setSecuritization(securitization);

		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			BeanUtils.populate(mcgfDetails, dynaForm.getMap());
			application.setMCGFDetails(mcgfDetails);

		}

		BeanUtils.populate(application, dynaForm.getMap());
		String cgpan = "";
		if (dynaForm.get("unitValue") != null
				&& !(dynaForm.get("unitValue").equals(""))) {
			cgpan = (String) dynaForm.get("unitValue");
			application.setCgpan(cgpan);

		}
		System.out.println("in  Application13");
		if (applicationSession.getAttribute(SessionConstants.MCGF_FLAG).equals(
				"M")) {
			if (dynaForm.get("unitValue") != null
					&& !(dynaForm.get("unitValue").equals(""))) {

				borrowerDetails = appProcessor.fetchBorrowerDetails("", cgpan);
				double corpusContAmt = appProcessor
						.getCorpusContAmt(borrowerDetails.getSsiDetails()
								.getBorrowerRefNo());
				double totalCorpusContAmt = corpusContAmt * 5;

				double creditAmount = 0;
				creditAmount = application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased();

				Log.log(Log.ERROR, "ApplicationProcessingAction",
						"afterWcEnhanceApp", "creditAmount:" + creditAmount);

				if (totalCorpusContAmt != 0) {
					if (creditAmount > totalCorpusContAmt) {
						throw new MessageException(
								"Credit Limit should not exceed the Corpus Amount");
					}

				}
			}
		}
		System.out.println("in  Application14");
		// ApplicationDAO appDAO = new ApplicationDAO();
		Application tempApplication = appProcessor.getApplication(
				application.getMliID(), cgpan, "");
		WorkingCapital tempWc = application.getWc();
		tempWc.setEnhancedFundBased(wcFundBasedSanctionedVal
				- tempApplication.getProjectOutlayDetails()
						.getWcFundBasedSanctioned());
		tempWc.setEnhancedNonFundBased(wcNonFundBasedSanctionedVal
				- tempApplication.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned());
		if (tempApplication.getProjectOutlayDetails()
				.getWcNonFundBasedSanctioned() == 0
				&& wcNonFundBasedSanctionedVal != 0) {
			tempWc.setLimitNonFundBasedSanctionedDate(tempWc
					.getEnhancementDate());
		} else if (tempApplication.getProjectOutlayDetails()
				.getWcNonFundBasedSanctioned() != 0
				&& wcNonFundBasedSanctionedVal != 0) {
			tempWc.setLimitNonFundBasedSanctionedDate(tempWc
					.getEnhancementDate());
		}
		if (tempApplication.getProjectOutlayDetails()
				.getWcFundBasedSanctioned() == 0
				&& wcFundBasedSanctionedVal != 0) {
			tempWc.setLimitFundBasedSanctionedDate(tempWc.getEnhancementDate());
		} else if (tempApplication.getProjectOutlayDetails()
				.getWcFundBasedSanctioned() != 0
				&& wcFundBasedSanctionedVal != 0) {
			tempWc.setLimitFundBasedSanctionedDate(tempWc.getEnhancementDate());
		}
		Log.log(Log.ERROR, "ApplicationProcessingAction", "afterWcEnhanceApp",
				"wcFundBasedSanctionedVal:" + wcFundBasedSanctionedVal);
		Log.log(Log.ERROR, "ApplicationProcessingAction", "afterWcEnhanceApp",
				"wcApp.getFundBasedLimitSanctioned()"
						+ tempApplication.getProjectOutlayDetails()
								.getWcFundBasedSanctioned());
		Log.log(Log.ERROR, "ApplicationProcessingAction", "afterWcEnhanceApp",
				"enhanced fund based:" + tempWc.getEnhancedFundBased());
		tempApplication = null;
		application.setWc(tempWc);

		application.setHandiCrafts(handiCrafts);
		application.setDcHandicrafts(dcHandicrafts);
		application.setIcardNo(icardNo);
		application.setIcardIssueDate(icardIssueDate);
		application.setDcHandlooms(dcHandlooms);

		application.setWeaverCreditScheme(WeaverCreditScheme);
		application.setHandloomchk(handloomchk);

		if (dcHandlooms != null && !dcHandlooms.equals("null")) {
			if (dcHandlooms.equals("N")) {
				application.setHandloomchk("N");
				application.setWeaverCreditScheme("");
			}
		}
		if (handiCrafts != null && !handiCrafts.equals("null")) {
			if (handiCrafts.equals("N")) {
				application.setDcHandicrafts("N");
			}
		}

		application.setWcEnhancement(true);
		System.out.println("in  Application15");
		appProcessor.submitWcEnhancement(application, userId);

		application = null;
		appProcessor = null;
		ssiDetails = null;
		borrowerDetails = null;
		termLoan = null;
		workingCapital = null;
		primarySecurityDetails = null;
		projectOutlayDetails = null;
		user = null;
		userId = null;
		bankId = null;
		zoneId = null;
		branchId = null;

		request.setAttribute("message",
				"Enhancement of Working Capital completed Successfully");

		return mapping.findForward("success");

	}

	/*
	 * This method sets the flag for the corresponding Menu selected
	 */
	public ActionForward newSplMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.SPL_MESSAGE_FLAG, "1");

		return mapping.findForward("specialMessagePage");

	}

	/*
	 * This method sets the flag for the corresponding Menu selected
	 */
	public ActionForward updateSpecialMessage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		session.setAttribute(SessionConstants.SPL_MESSAGE_FLAG, "0");

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ArrayList msgTitlesContentsList = appProcessor.getMessageTitleContent();
		ArrayList msgTitlesList = (ArrayList) msgTitlesContentsList.get(0);

		dynaForm.set("msgTitlesList", msgTitlesList);

		msgTitlesList = null;
		msgTitlesContentsList = null;
		appProcessor = null;

		Log.log(Log.DEBUG, "ApplicationProcessingAction",
				"updateSpecialMessage",
				"Msg Title List :" + dynaForm.get("msgTitlesList"));

		return mapping.findForward("specialMessagePage");

	}

	/**
	 * This method retrieves the message titles for Special Message and forwards
	 * it to the jsp page
	 */

	public ActionForward getSplTitle(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		DynaActionForm dynaForm = (DynaActionForm) form;

		ArrayList msgTitlesContentsList = appProcessor.getMessageTitleContent();
		ArrayList msgTitlesList = (ArrayList) msgTitlesContentsList.get(0);

		dynaForm.set("msgTitlesList", msgTitlesList);
		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getSplTitle",
				"Msg Title List :" + dynaForm.get("msgTitlesList"));

		appProcessor = null;
		msgTitlesContentsList = null;
		msgTitlesList = null;

		return mapping.findForward("specialMessagePage");

	}

	/**
	 * This method displays the corresponding message for the message title
	 * selected
	 */

	public ActionForward getSplMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;
		String messageTitle = (String) dynaForm.get("msgTitle");
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		SpecialMessage specialMessage = new SpecialMessage();
		if (messageTitle.equals("")) {
			specialMessage = new SpecialMessage();
		} else {
			specialMessage = appProcessor.getMessageDesc(messageTitle);
			specialMessage.setMsgTitle(messageTitle);

		}
		BeanUtils.copyProperties(dynaForm, specialMessage);

		appProcessor = null;
		specialMessage = null;
		messageTitle = null;

		return mapping.findForward("specialMessagePage");
	}

	/**
	 * This method inserts the details into the DB
	 */

	public ActionForward insertSplMessage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		SpecialMessage specialMessage = new SpecialMessage();

		BeanUtils.populate(specialMessage, dynaForm.getMap());

		HttpSession session = request.getSession(false);
		String sessionFlag = (String) session
				.getAttribute(SessionConstants.SPL_MESSAGE_FLAG);
		if (sessionFlag.equals("1")) {
			appProcessor.addSpecialMessage(specialMessage);
			request.setAttribute("message",
					"Special Message Inserted Successfully");
		} else if (sessionFlag.equals("0")) {
			appProcessor.updateSpecialMessage(specialMessage);
			request.setAttribute("message",
					"Special Message Updated Successfully");
		}

		appProcessor = null;
		specialMessage = null;

		return mapping.findForward("success");
	}

	/**
	 * This method updates the details into the DB
	 */

	public ActionForward updateSplMessage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;
		SpecialMessage specialMessage = new SpecialMessage();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		BeanUtils.populate(specialMessage, dynaForm.getMap());

		appProcessor.addSpecialMessage(specialMessage);

		appProcessor = null;
		specialMessage = null;

		request.setAttribute("message", "Special Message Updated Successfully");

		return mapping.findForward("success");
	}

	/*
	 * This method gets the borrower Details for a cgbid / cgpan and populates
	 * it on the screen
	 */

	public ActionForward getBorrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;

		HttpSession bidSession = request.getSession(false);
		String applicationType = (String) bidSession
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		// System.out.println("Line Number 6155 applicationType:"+applicationType);
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		String cgbid = "";
		String cgpan = "";
		String value = (String) dynaForm.get("unitValue");
		// System.out.println("value:"+value);

		Log.log(Log.INFO, "ApplicationProcessingAction", "getBorrowerDetails",
				"Value :" + value);

		Log.log(Log.INFO, "ApplicationProcessingAction", "getBorrowerDetails",
				"Value :" + dynaForm.get("none"));

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String mliId = "";
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			mliId = (String) dynaForm.get("selectMember");

		} else {

			MLIInfo mliInfo = getMemberInfo(request);
			bankId = mliInfo.getBankId();
			String branchId = mliInfo.getBranchId();
			String zoneId = mliInfo.getZoneId();
			mliId = bankId + zoneId + branchId;

		}

		if ((dynaForm.get("none")).equals("cgbid")) {
			cgbid = value;
			dynaForm.set("unitValue", cgbid);
			dynaForm.set("none", "cgbid");

			ClaimsProcessor cpProcessor = new ClaimsProcessor();

			ArrayList borrowerIds = cpProcessor.getAllBorrowerIDs(mliId);

			if (!(borrowerIds.contains(cgbid))) {
				throw new NoDataException(
						"The Borrower ID does not exist for this Member ID");
			}

			/*
			 * if(application.getCgpan()!=null &&
			 * !application.getCgpan().equals("")) { cgbid =
			 * claimProcessor.getBorowwerForCGPAN(application.getCgpan());
			 * 
			 * } else
			 */
			if (cgbid != null && !(cgbid.equals(""))) {

				// cgbid =
				// application.getBorrowerDetails().getSsiDetails().getCgbid();
				int claimCount = appProcessor.getClaimCount(cgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}
			}

		} else if ((dynaForm.get("none")).equals("cgpan")) {

			ClaimsProcessor claimProcessor = new ClaimsProcessor();
			cgpan = value;
			dynaForm.set("unitValue", cgpan);
			dynaForm.set("none", "cgpan");
			// System.out.println("dynaForm.get(none):"+dynaForm.get("none"));
			if (cgpan != null && !cgpan.equals("")) {
				String tempCgbid = claimProcessor.getBorowwerForCGPAN(cgpan);

				int claimCount = appProcessor.getClaimCount(tempCgbid);
				if (claimCount > 0) {
					throw new MessageException(
							"Application cannot be filed by this borrower since Claim Application has been submitted");
				}

			}

			// System.out.println("mliId:"+mliId+" cgpan:"+cgpan);

			Application application = appProcessor.getAppForCgpan(mliId, cgpan);
		}

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getBorrowerDetails",
				"Cgbid from dynaForm :" + cgbid);

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "getBorrowerDetails",
				"Cgpan from dynaForm :" + cgpan);

		Administrator admin = new Administrator();

		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();

		// borrowerDetails=appProcessor.fetchBorrowerDetails(cgbid,cgpan,applicationType);
		borrowerDetails = appProcessor.fetchBorrowerDetailsNew(cgbid, cgpan,
				applicationType);

		ssiDetails = borrowerDetails.getSsiDetails();

		Application tempApplication = new Application();
		tempApplication.setBorrowerDetails(borrowerDetails);
		tempApplication.setMliID(mliId);

		double balanceAppAmt = appProcessor
				.getBalanceApprovedAmt(tempApplication);

		BeanUtils.copyProperties(dynaForm, ssiDetails);

		BeanUtils.copyProperties(dynaForm, borrowerDetails);

		dynaForm.set("balanceApprovedAmt", new Double(balanceAppAmt));

		dynaForm.set("previouslyCovered", "Y");

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		// The district for the particular state has to be populated
		String state = (String) dynaForm.get("state");
		// System.out.println("State:"+state);
		ArrayList districtList = admin.getAllDistricts(state);
		dynaForm.set("districtList", districtList);
		// int districtSize=districtList.size();
		String districtName = ssiDetails.getDistrict();
		// System.out.println("District:"+districtName);

		if (districtList.contains(districtName)) {
			dynaForm.set("district", districtName);
		} else {
			dynaForm.set("districtOthers", districtName);
			dynaForm.set("district", "Others");

		}
		Log.log(Log.INFO, "ApplicationProcessingAction", "getBorrowerDetails",
				"Set Application Details...");

		/*
		 * ArrayList industryNatureList=getIndustryNature();
		 * dynaForm.set("industryNatureList",industryNatureList);
		 * 
		 * String industryNature=(String)dynaForm.get("industryNature");
		 * if(industryNature!=null && !(industryNature.equals(""))) { ArrayList
		 * industrySectorList=admin.getIndustrySectors(industryNature);
		 * dynaForm.set("industrySectorList",industrySectorList);
		 * industrySectorList=null; }
		 */
		dynaForm.set("industryNature", ssiDetails.getIndustryNature());
		// System.out.println("industry Nature:"+ssiDetails.getIndustryNature());
		// System.out.println("Industry Sector:"+ssiDetails.getIndustrySector());
		dynaForm.set("industrySector", ssiDetails.getIndustrySector());

		bidSession.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "17");

		String forward = setApps(applicationType);

		applicationType = null;

		// industryNatureList=null;
		appProcessor = null;
		admin = null;
		ssiDetails = null;
		borrowerDetails = null;
		statesList = null;
		districtList = null;

		return mapping.findForward(forward);

	}

	/*
	 * This method displays the duplicate Application
	 */

	public ActionForward showDuplicates(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "DUP");

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;

		String forward = "";

		String mliFlag = apForm.getWithinMlis();
		if (mliFlag.equals(Constants.WITHIN_MLIS)) {
			mliFlag = Constants.WITHIN_MLIS;
		} else if (mliFlag.equals(Constants.ACROSS_MLIS)) {
			mliFlag = Constants.ACROSS_MLIS;
		}

		ArrayList duplicateApp = new ArrayList();
		ArrayList duplicateApplications = appProcessor.checkDuplicate(mliFlag);
		ArrayList tcDuplicateApp = (ArrayList) duplicateApplications.get(0);
		ArrayList wcDuplicateApp = (ArrayList) duplicateApplications.get(1);

		for (int i = 0; i < tcDuplicateApp.size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) tcDuplicateApp
					.get(i);
			duplicateApp.add(duplicateApplication);
		}
		for (int j = 0; j < wcDuplicateApp.size(); j++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) wcDuplicateApp
					.get(j);
			duplicateApp.add(duplicateApplication);
		}

		if (duplicateApp == null || duplicateApp.size() == 0) {
			request.setAttribute("message", "No Duplicate Application Found");
			forward = "success";
		} else {

			apForm.setDuplicateApplications(duplicateApp);
			forward = "duplicatePage";
		}

		appProcessor = null;
		duplicateApp = null;

		return mapping.findForward(forward);
	}

	/**
	 * This method generates cgpan for approved applications
	 */

	/*
	 * public ActionForward afterApprovalApps1( ActionMapping mapping,
	 * ActionForm form, HttpServletRequest request, HttpServletResponse
	 * response) throws Exception {
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","Entered"
	 * );
	 * 
	 * ApplicationProcessor appProcessor=new ApplicationProcessor(); RpProcessor
	 * rpProcessor=new RpProcessor(); RpHelper rpHelper = new RpHelper() ;
	 * 
	 * String message=""; String subject="";
	 * 
	 * User user=getUserInformation(request);
	 * 
	 * String userId=user.getUserId();
	 * 
	 * APForm apForm=(APForm)form;
	 * 
	 * //Clear Applications ArrayList clearApprovedApplications=new ArrayList();
	 * ArrayList clearHoldApplications=new ArrayList(); ArrayList
	 * clearRejectedApplications=new ArrayList(); ArrayList
	 * clearPendingApplications=new ArrayList();
	 * 
	 * Map clearAppRefNos=apForm.getClearAppRefNo(); Map
	 * clearRemarks=apForm.getClearRemarks(); Map
	 * clearStatus=apForm.getClearStatus(); Map
	 * clearApprovedAmt=apForm.getClearApprovedAmt();
	 * 
	 * Set clearAppRefNosSet=clearAppRefNos.keySet(); Set
	 * clearRemarksSet=clearRemarks.keySet(); Set
	 * clearStatusSet=clearStatus.keySet(); Set
	 * clearApprovedAmtSet=clearApprovedAmt.keySet();
	 * 
	 * Iterator clearAppRefNosIterator=clearAppRefNosSet.iterator(); Iterator
	 * clearRemarksIterator=clearRemarksSet.iterator(); Iterator
	 * clearStatusIterator=clearStatusSet.iterator(); Iterator
	 * clearApprovedAmtIterator=clearApprovedAmtSet.iterator();
	 * 
	 * while (clearAppRefNosIterator.hasNext()) { Application application=new
	 * Application();
	 * 
	 * String key=(String)clearAppRefNosIterator.next();
	 * 
	 * String appRefNumber=(String)clearAppRefNos.get(key);
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","app ref no :" + appRefNumber); String
	 * approvedStatus=(String)clearStatus.get(key);
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","status:" + approvedStatus);
	 * 
	 * double approvedAmount; String remarks;
	 * 
	 * String mliId=null; String cgpan=""; String cgbid=""; application =
	 * appProcessor.getApplication(mliId,cgpan,appRefNumber);
	 * 
	 * 
	 * /** This loop calculates the guarantee fee,cgpan and cgbid for approved
	 * applications
	 * 
	 * if
	 * (approvedStatus.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
	 * { // application=new Application();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "entering approved applications");
	 * 
	 * approvedAmount=Double.parseDouble((String)clearApprovedAmt.get(key));
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals(""))) {
	 * remarks=(String)clearRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * //generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks); application.setStatus(approvedStatus);
	 * 
	 * cgpan=appProcessor.generateCgpan(application);
	 * 
	 * application.setCgpan(cgpan);
	 * 
	 * //update application status
	 * appProcessor.updateApplicationsStatus(application,userId); /*
	 * appProcessor.updateCgpan(application);
	 * 
	 * 
	 * //generate cgbid int
	 * ssiRefNo=application.getBorrowerDetails().getSsiDetails
	 * ().getBorrowerRefNo(); cgbid = appProcessor.generateCgbid(ssiRefNo); if
	 * (cgbid!=null && !(cgbid.equals(""))) {
	 * application.getBorrowerDetails().getSsiDetails().setCgbid(cgbid);
	 * 
	 * //updation of cgbid appProcessor.updateCgbid(ssiRefNo,cgbid);
	 * 
	 * }
	 * 
	 * clearApprovedApplications.add(application);
	 * 
	 * 
	 * } else if
	 * (approvedStatus.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals(""))) {
	 * remarks=(String)clearRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * application.setStatus(approvedStatus);
	 * 
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * clearHoldApplications.add(application);
	 * //apForm.setClearHoldApplications(clearHoldApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been put on hold because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * } else if
	 * (approvedStatus.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS
	 * )){
	 * 
	 * //application=new Application();
	 * 
	 * if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals(""))) {
	 * remarks=(String)clearRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * application.setStatus(approvedStatus);
	 * 
	 * appProcessor.updatePendingRejectedStatus(application,userId);
	 * 
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * clearRejectedApplications.add(application);
	 * //apForm.setClearRejectedApplications(clearRejectedApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been rejected because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * 
	 * }else if
	 * (approvedStatus.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){
	 * 
	 * //application=new Application();
	 * 
	 * if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals(""))) {
	 * remarks=(String)clearRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * application.setStatus(approvedStatus);
	 * 
	 * appProcessor.updatePendingRejectedStatus(application,userId);
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * clearPendingApplications.add(application);
	 * //apForm.setClearPendingApplications(clearPendingApplications);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * } apForm.setClearApprovedApplications(clearApprovedApplications);
	 * apForm.setClearHoldApplications(clearHoldApplications);
	 * apForm.setClearRejectedApplications(clearRejectedApplications);
	 * apForm.setClearPendingApplications(clearPendingApplications);
	 * 
	 * //Probable Duplicates Applications
	 * 
	 * //Array lists for the display ArrayList dupApprovedApplications=new
	 * ArrayList(); ArrayList dupHoldApplications=new ArrayList(); ArrayList
	 * dupRejectedApplications=new ArrayList(); ArrayList
	 * dupPendingApplications=new ArrayList();
	 * 
	 * Map duplicateAppRefNos=apForm.getDuplicateAppRefNo(); Map
	 * duplicateRemarks=apForm.getDuplicateRemarks(); Map
	 * duplicateStatus=apForm.getDuplicateStatus(); Map
	 * duplicateApprovedAmt=apForm.getDuplicateApprovedAmt();
	 * 
	 * Set duplicateAppRefNosSet=duplicateAppRefNos.keySet(); Set
	 * duplicateRemarksSet=duplicateRemarks.keySet(); Set
	 * duplicateStatusSet=duplicateStatus.keySet(); Set
	 * duplicateApprovedAmtSet=duplicateApprovedAmt.keySet();
	 * 
	 * Iterator duplicateAppRefNosIterator=duplicateAppRefNosSet.iterator();
	 * Iterator duplicateRemarksIterator=duplicateRemarksSet.iterator();
	 * Iterator duplicateStatusIterator=duplicateStatusSet.iterator(); Iterator
	 * duplicateApprovedAmtIterator=duplicateApprovedAmtSet.iterator();
	 * 
	 * while (duplicateAppRefNosIterator.hasNext()) { Application
	 * application=new Application();
	 * 
	 * String key=(String)duplicateAppRefNosIterator.next();
	 * 
	 * String appRefNumber=(String)duplicateAppRefNos.get(key);
	 * Log.log(Log.INFO,
	 * "ApplicationProcessingAction","afterApprovalApps","app ref no :" +
	 * appRefNumber); String status=(String)duplicateStatus.get(key);
	 * Log.log(Log
	 * .INFO,"ApplicationProcessingAction","afterApprovalApps","status:" +
	 * status); // String
	 * remarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next()); //
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved remarks:" + remarks);
	 * 
	 * double approvedAmount; String remarks;
	 * 
	 * String mliId=null; String cgpan=""; String cgbid=""; application =
	 * appProcessor.getApplication(mliId,cgpan,appRefNumber);
	 * 
	 * 
	 * String appStatus =application.getStatus();
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","appStatus:" + appStatus);
	 * 
	 * if(application.getLoanType().equals("TC")||
	 * application.getLoanType().equals("CC")) { if(status.equals("WEN")) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC ENHANCEMENT decision cannot be taken"
	 * ); } else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC RENEWAL decision cannot be taken"
	 * ); }
	 * 
	 * 
	 * }else if (application.getLoanType().equals("WC")) {
	 * if((application.getStatus().equals("NE") ||
	 * application.getStatus().equals("HO")) && status.equals("WEN")) { throw
	 * new MessageException("Decision for application :" + appRefNumber +
	 * " cannot be APPROVED since it has been applied for Working Capital Enhancement"
	 * ); } else if ((application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) &&
	 * (status.equals(ApplicationConstants
	 * .APPLICATION_APPROVED_STATUS)||status.equals("WCR"))) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be ACCEPT / RENEWAL since it has been applied for Working Capital Enhancement"
	 * ); } else if(status.equals("ATL")) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Additional Term Loan since it is working capital application"
	 * ); } else if((application.getCgpanReference()!=null &&
	 * !application.getCgpanReference().equals("")) &&
	 * application.getCgpanReference().startsWith("CG") && (status.equals("AP")
	 * || status.equals("WEN"))) { Application testApplication =
	 * appProcessor.getPartApplication(null,application.getCgpanReference(),"");
	 * if(testApplication.getStatus().equals("EX")) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Accept since the Working Capital Application has been applied for Renewal"
	 * ); } testApplication=null;
	 * 
	 * } else if((application.getCgpanReference()==null ||
	 * application.getCgpanReference().equals("")) && status.equals("WCR")) {
	 * throw new MessageException("Decision for application :" + appRefNumber +
	 * " cannot be RENEWAL since the Working Capital Application has not been applied for Renewal"
	 * ); }
	 * 
	 * }
	 * 
	 * /** This loop calculates generates cgpan and cgbid for approved
	 * applications
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * //application=new Application();
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","entering approved applications");
	 * 
	 * approvedAmount=Double.parseDouble((String)duplicateApprovedAmt.get(key));
	 * 
	 * if (duplicateRemarks.get(key)!=null &&
	 * !(duplicateRemarks.get(key).equals(""))) {
	 * remarks=(String)duplicateRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * 
	 * // generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * if(!(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)))
	 * { cgpan=appProcessor.generateCgpan(application); } else {
	 * 
	 * String renewCgpan=application.getCgpanReference();
	 * 
	 * cgpan=appProcessor.generateRenewCgpan(renewCgpan); }
	 * 
	 * application.setCgpan(cgpan);
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) {
	 * application.setAdditionalTC(true);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * application.setWcRenewal(true); }
	 * 
	 * 
	 * //update application status
	 * appProcessor.updateApplicationsStatus(application,userId); /*
	 * appProcessor.updateCgpan(application);
	 * 
	 * if (!(status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) &&
	 * !(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS))) {
	 * int
	 * ssiRefNo=application.getBorrowerDetails().getSsiDetails().getBorrowerRefNo
	 * (); cgbid = appProcessor.generateCgbid(ssiRefNo); if (cgbid!=null &&
	 * !(cgbid.equals(""))) {
	 * application.getBorrowerDetails().getSsiDetails().setCgbid(cgbid);
	 * 
	 * //updation of cgbid appProcessor.updateCgbid(ssiRefNo,cgbid);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * //generate cgbid
	 * 
	 * dupApprovedApplications.add(application);
	 * //apForm.setDupApprovedApplications(dupApprovedApplications);
	 * 
	 * 
	 * } else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {
	 * 
	 * //application=new Application();
	 * 
	 * if (duplicateRemarks.get(key)!=null &&
	 * !(duplicateRemarks.get(key).equals(""))) {
	 * remarks=(String)duplicateRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * 
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("EH");
	 * 
	 * }else{
	 * 
	 * application.setStatus(status); }
	 * 
	 * 
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * dupHoldApplications.add(application);
	 * //apForm.setDupHoldApplications(dupHoldApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been put on hold because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * } else if
	 * (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {
	 * 
	 * // application=new Application();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "duplicateRemarks.get(key):" + duplicateRemarks.get(key));
	 * 
	 * if (duplicateRemarks.get(key)!=null &&
	 * !(duplicateRemarks.get(key).equals(""))) {
	 * remarks=(String)duplicateRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * 
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("ER");
	 * appProcessor.updateRejectStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * 
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * dupRejectedApplications.add(application);
	 * //apForm.setDupRejectedApplications(dupApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been rejected because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){
	 * 
	 * //application=new Application();
	 * 
	 * if(duplicateRemarks.get(key)!=null &&
	 * !(duplicateRemarks.get(key).equals(""))) {
	 * remarks=(String)duplicateRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * 
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) { application.setStatus("EP");
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * 
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * dupPendingApplications.add(application);
	 * //apForm.setDupPendingApplications(dupPendingApplications);
	 * 
	 * } else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_ENHANCE_STATUS)){
	 * 
	 * approvedAmount=Double.parseDouble((String)duplicateApprovedAmt.get(key));
	 * 
	 * if (duplicateRemarks.get(key)!=null &&
	 * !(duplicateRemarks.get(key).equals(""))) {
	 * remarks=(String)duplicateRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * 
	 * // generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * /** DAN Generation for Enhanced amount
	 * 
	 * 
	 * ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber); int
	 * countDan = ((Integer)countAmount.get(0)).intValue(); double
	 * enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "countDan:" + countDan);
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
	 * ,"enhancedAmount:" + enhancedAmount);
	 * 
	 * if(countDan>0) { application.setApprovedAmount(approvedAmount -
	 * enhancedAmount); appProcessor.generateDanForEnhance(application,user);
	 * 
	 * }
	 * 
	 * 
	 * appProcessor.updateEnhanceAppStatus(application,userId);
	 * 
	 * 
	 * 
	 * dupApprovedApplications.add(application);
	 * 
	 * }
	 * 
	 * } apForm.setDupApprovedApplications(dupApprovedApplications);
	 * apForm.setDupHoldApplications(dupHoldApplications);
	 * apForm.setDupRejectedApplications(dupRejectedApplications);
	 * apForm.setDupPendingApplications(dupPendingApplications);
	 * 
	 * 
	 * //Probable Ineligible Applications
	 * 
	 * ArrayList ineligibleApprovedApplications=new ArrayList(); ArrayList
	 * ineligibleHoldApplications=new ArrayList(); ArrayList
	 * ineligibleRejectedApplications=new ArrayList(); ArrayList
	 * ineligiblePendingApplications=new ArrayList();
	 * 
	 * 
	 * Map ineligibleAppRefNos=apForm.getIneligibleAppRefNo(); Map
	 * ineligibleRemarks=apForm.getIneligibleRemarks(); Map
	 * ineligibleStatus=apForm.getIneligibleStatus(); Map
	 * ineligibleApprovedAmt=apForm.getIneligibleApprovedAmt();
	 * 
	 * Set ineligibleAppRefNosSet=ineligibleAppRefNos.keySet(); Set
	 * ineligibleRemarksSet=ineligibleRemarks.keySet(); Set
	 * ineligibleStatusSet=ineligibleStatus.keySet(); Set
	 * ineligibleApprovedAmtSet=ineligibleApprovedAmt.keySet();
	 * 
	 * Iterator ineligibleAppRefNosIterator=ineligibleAppRefNosSet.iterator();
	 * Iterator ineligibleRemarksIterator=ineligibleRemarksSet.iterator();
	 * Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();
	 * Iterator
	 * ineligibleApprovedAmtIterator=ineligibleApprovedAmtSet.iterator();
	 * 
	 * while (ineligibleAppRefNosIterator.hasNext()) { Application
	 * application=new Application();
	 * 
	 * String key=(String)ineligibleAppRefNosIterator.next();
	 * 
	 * String appRefNumber=(String)ineligibleAppRefNos.get(key);
	 * Log.log(Log.INFO
	 * ,"ApplicationProcessingAction","afterApprovalApps","app ref no :" +
	 * appRefNumber); String status=(String)ineligibleStatus.get(key);
	 * Log.log(Log
	 * .INFO,"ApplicationProcessingAction","afterApprovalApps","status:" +
	 * status); /* String
	 * remarks=(String)ineligibleRemarks.get(ineligibleRemarksIterator.next());
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved remarks:" + remarks);
	 * 
	 * double approvedAmount; String remarks;
	 * 
	 * String mliId=null; String cgpan=""; String cgbid=""; application =
	 * appProcessor.getApplication(mliId,cgpan,appRefNumber);
	 * 
	 * String appStatus =application.getStatus();
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","appStatus:" + appStatus);
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","status:" + status);
	 * 
	 * 
	 * if(application.getLoanType().equals("TC")||
	 * application.getLoanType().equals("CC")) { if(status.equals("WEN")) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC ENHANCEMENT decision cannot be taken"
	 * ); } else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC RENEWAL decision cannot be taken"
	 * ); }
	 * 
	 * 
	 * }else if (application.getLoanType().equals("WC")) {
	 * if((application.getStatus().equals("NE") ||
	 * application.getStatus().equals("HO")) && status.equals("WEN")) { throw
	 * new MessageException("Decision for application :" + appRefNumber +
	 * " cannot be APPROVED since it has been applied for Working Capital Enhancement"
	 * ); } else if ((application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) &&
	 * (status.equals(ApplicationConstants
	 * .APPLICATION_APPROVED_STATUS)||status.equals("WCR"))) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be ACCEPT since it has been applied for Working Capital Enhancement"
	 * ); } else if(status.equals("ATL")) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Additional Term Loan since it is working capital application"
	 * ); } else if((application.getCgpanReference()!=null &&
	 * !application.getCgpanReference().equals("") &&
	 * application.getCgpanReference().startsWith("CG")) && (status.equals("AP")
	 * || status.equals("WEN"))) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Accept since the Working Capital Application has been applied for Renewal"
	 * ); }
	 * 
	 * 
	 * }
	 * 
	 * /** This loop calculates the guarantee fee,cgpan and cgbid for approved
	 * applications
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) { //
	 * application=new Application();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "entering approved applications");
	 * 
	 * if (ineligibleApprovedAmt.get(key)!=null) {
	 * approvedAmount=Double.parseDouble
	 * ((String)ineligibleApprovedAmt.get(key));
	 * 
	 * }else {
	 * 
	 * approvedAmount=0;
	 * 
	 * } if (ineligibleRemarks.get(key)!=null &&
	 * !(ineligibleRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * //generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * if(!(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)))
	 * { cgpan=appProcessor.generateCgpan(application); } else {
	 * 
	 * String renewCgpan=application.getCgpanReference();
	 * 
	 * cgpan=appProcessor.generateRenewCgpan(renewCgpan);
	 * 
	 * }
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "generated Cgpan :" + cgpan);
	 * 
	 * application.setCgpan(cgpan);
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) {
	 * application.setAdditionalTC(true);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * application.setWcRenewal(true); }
	 * 
	 * 
	 * //update application status
	 * appProcessor.updateApplicationsStatus(application,userId); /*
	 * appProcessor.updateCgpan(application);
	 * 
	 * //generate cgbid if
	 * (!(status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) &&
	 * !(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS))) {
	 * int
	 * ssiRefNo=application.getBorrowerDetails().getSsiDetails().getBorrowerRefNo
	 * (); cgbid = appProcessor.generateCgbid(ssiRefNo);
	 * application.getBorrowerDetails().getSsiDetails().setCgbid(cgbid);
	 * 
	 * //updation of cgbid appProcessor.updateCgbid(ssiRefNo,cgbid);
	 * 
	 * }
	 * 
	 * ineligibleApprovedApplications.add(application);
	 * //apForm.setIneligibleApprovedApplications
	 * (ineligibleApprovedApplications);
	 * 
	 * 
	 * } else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if (ineligibleRemarks.get(key)!=null &&
	 * !(ineligibleRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * 
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("EH");
	 * 
	 * }else{
	 * 
	 * application.setStatus(status); }
	 * 
	 * 
	 * //application.setStatus(status);
	 * 
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligibleHoldApplications.add(application);
	 * //apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been put on hold beacuse " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * }
	 * 
	 * else if
	 * (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if (ineligibleRemarks.get(key)!=null &&
	 * !(ineligibleRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("ER");
	 * appProcessor.updateRejectStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligibleRejectedApplications.add(application);
	 * //apForm.setIneligibleRejectedApplications
	 * (ineligibleRejectedApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been rejected because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if(ineligibleRemarks.get(key)!=null &&
	 * !(ineligibleRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) { application.setStatus("EP");
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligiblePendingApplications.add(application);
	 * //apForm.setIneligiblePendingApplications(ineligiblePendingApplications);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_ENHANCE_STATUS)){
	 * 
	 * approvedAmount=Double.parseDouble((String)ineligibleApprovedAmt.get(key));
	 * 
	 * if (ineligibleRemarks.get(key)!=null &&
	 * !(ineligibleRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * 
	 * // generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * /** DAN Generation for Enhanced amount
	 * 
	 * 
	 * ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber); int
	 * countDan = ((Integer)countAmount.get(0)).intValue(); double
	 * enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "countDan:" + countDan);
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
	 * ,"enhancedAmount:" + enhancedAmount);
	 * 
	 * if(countDan>0) { application.setApprovedAmount(approvedAmount -
	 * enhancedAmount); appProcessor.generateDanForEnhance(application,user);
	 * 
	 * }
	 * 
	 * appProcessor.updateEnhanceAppStatus(application,userId);
	 * 
	 * ineligibleApprovedApplications.add(application);
	 * 
	 * }
	 * 
	 * }
	 * apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);
	 * apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
	 * apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);
	 * apForm.setIneligiblePendingApplications(ineligiblePendingApplications);
	 * 
	 * 
	 * 
	 * //Ineligible Duplicate Applications ArrayList
	 * ineligibleDupApprovedApplications=new ArrayList(); ArrayList
	 * ineligibleDupHoldApplications=new ArrayList(); ArrayList
	 * ineligibleDupRejectedApplications=new ArrayList(); ArrayList
	 * ineligibleDupPendingApplications=new ArrayList();
	 * 
	 * 
	 * Map ineligibleDupAppRefNos=apForm.getIneligibleDupAppRefNo(); Map
	 * ineligibleDupRemarks=apForm.getIneligibleDupRemarks(); Map
	 * ineligibleDupStatus=apForm.getIneligibleDupStatus(); Map
	 * ineligibleDupApprovedAmt=apForm.getIneligibleDupApprovedAmt();
	 * 
	 * Set ineligibleDupAppRefNosSet=ineligibleDupAppRefNos.keySet(); Set
	 * ineligibleDupRemarksSet=ineligibleDupRemarks.keySet(); Set
	 * ineligibleDupStatusSet=ineligibleDupStatus.keySet(); Set
	 * ineligibleDupApprovedAmtSet=ineligibleDupApprovedAmt.keySet();
	 * 
	 * Iterator
	 * ineligibleDupAppRefNosIterator=ineligibleDupAppRefNosSet.iterator();
	 * Iterator ineligibleDupRemarksIterator=ineligibleDupRemarksSet.iterator();
	 * Iterator ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();
	 * Iterator
	 * ineligibleDupApprovedAmtIterator=ineligibleDupApprovedAmtSet.iterator();
	 * 
	 * while (ineligibleDupAppRefNosIterator.hasNext()) { Application
	 * application=new Application();
	 * 
	 * String key=(String)ineligibleDupAppRefNosIterator.next();
	 * 
	 * String appRefNumber=(String)ineligibleDupAppRefNos.get(key);
	 * Log.log(Log.INFO
	 * ,"ApplicationProcessingAction","afterApprovalApps","app ref no :" +
	 * appRefNumber); String status=(String)ineligibleDupStatus.get(key);
	 * Log.log
	 * (Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" +
	 * status); /* String
	 * remarks=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator
	 * .next());
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
	 * ,"approved remarks:" + remarks);
	 * 
	 * double approvedAmount; String remarks;
	 * 
	 * String mliId=null; String cgpan=""; String cgbid=""; application =
	 * appProcessor.getApplication(mliId,cgpan,appRefNumber);
	 * 
	 * String appStatus =application.getStatus();
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","appStatus:" + appStatus);
	 * Log.log(Log.INFO,"ApplicationProcessingAction"
	 * ,"afterApprovalApps","status:" + status);
	 * 
	 * if(application.getLoanType().equals("TC")||
	 * application.getLoanType().equals("CC")) { if(status.equals("WEN")) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC ENHANCEMENT decision cannot be taken"
	 * ); } else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * throw new MessageException("Application :" + appRefNumber +
	 * " is a Term Credit Application.Hence WC RENEWAL decision cannot be taken"
	 * ); }
	 * 
	 * 
	 * }else if (application.getLoanType().equals("WC")) {
	 * 
	 * if((application.getStatus().equals("NE") ||
	 * application.getStatus().equals("HO")) && status.equals("WEN")) { throw
	 * new MessageException("Decision for application :" + appRefNumber +
	 * " cannot be APPROVED since it has been applied for Working Capital Enhancement"
	 * ); } else if ((application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) &&
	 * (status.equals(ApplicationConstants
	 * .APPLICATION_APPROVED_STATUS)||status.equals("WCR"))) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be ACCEPT since it has been applied for Working Capital Enhancement"
	 * ); } else if(status.equals("ATL")) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Additional Term Loan since it is working capital application"
	 * ); } else if((application.getCgpanReference()!=null &&
	 * !application.getCgpanReference().equals("")) &&
	 * application.getCgpanReference().startsWith("CG") && (status.equals("AP")
	 * || status.equals("WEN"))) { throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * " cannot be Accept since the Working Capital Application has been applied for Renewal"
	 * ); }
	 * 
	 * 
	 * } // if(appStatus.equals("EN") && status.equals("AP")) // { // throw new
	 * MessageException("Decision for application :" + appRefNumber +
	 * "cannot be APPROVED since it has been applied for Working Capital Enhancement"
	 * ); // }
	 * 
	 * /** This loop calculates the guarantee fee,cgpan and cgbid for approved
	 * applications
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS) ||
	 * status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) { //
	 * application=new Application();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "entering approved applications");
	 * 
	 * if (ineligibleDupApprovedAmt.get(key)!=null) {
	 * approvedAmount=Double.parseDouble
	 * ((String)ineligibleDupApprovedAmt.get(key));
	 * 
	 * }else {
	 * 
	 * approvedAmount=0;
	 * 
	 * } if (ineligibleDupRemarks.get(key)!=null &&
	 * !(ineligibleDupRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleDupRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * 
	 * //generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * if(!(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)))
	 * { cgpan=appProcessor.generateCgpan(application); } else {
	 * 
	 * String renewCgpan=application.getCgpanReference();
	 * 
	 * cgpan=appProcessor.generateRenewCgpan(renewCgpan); }
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "generated Cgpan :" + cgpan); application.setCgpan(cgpan);
	 * 
	 * if (status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) {
	 * application.setAdditionalTC(true);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS)) {
	 * application.setWcRenewal(true); }
	 * 
	 * 
	 * //update application status
	 * appProcessor.updateApplicationsStatus(application,userId); /*
	 * appProcessor.updateCgpan(application);
	 * 
	 * //generate cgbid if
	 * (!(status.equals(ApplicationConstants.APPLICATION_ADDTL_TL_STATUS)) &&
	 * !(status.equals(ApplicationConstants.APPLICATION_WC_RENEWAL_STATUS))) {
	 * int
	 * ssiRefNo=application.getBorrowerDetails().getSsiDetails().getBorrowerRefNo
	 * (); cgbid = appProcessor.generateCgbid(ssiRefNo);
	 * application.getBorrowerDetails().getSsiDetails().setCgbid(cgbid);
	 * 
	 * //updation of cgbid appProcessor.updateCgbid(ssiRefNo,cgbid); }
	 * 
	 * ineligibleDupApprovedApplications.add(application);
	 * //apForm.setIneligibleDupApprovedApplications
	 * (ineligibleDupApprovedApplications);
	 * 
	 * 
	 * } else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if (ineligibleDupRemarks.get(key)!=null &&
	 * !(ineligibleDupRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleDupRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * 
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("EH");
	 * 
	 * }else{
	 * 
	 * application.setStatus(status); }
	 * 
	 * 
	 * 
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligibleDupHoldApplications.add(application);
	 * //apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been put on hold because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * } else if
	 * (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if (ineligibleDupRemarks.get(key)!=null &&
	 * !(ineligibleDupRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleDupRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EH") ||
	 * application.getStatus().equals("EP")) { application.setStatus("ER");
	 * appProcessor.updateRejectStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligibleDupRejectedApplications.add(application);
	 * //apForm.setIneligibleDupRejectedApplications
	 * (ineligibleDupRejectedApplications);
	 * 
	 * message="The Application Reference No. :" + appRefNumber +
	 * " has been been rejected because " + remarks;
	 * 
	 * subject="Status of Application Reference No. :" + appRefNumber;
	 * 
	 * sendMailEmail(message,application.getMliID(),user,subject);
	 * 
	 * }else if
	 * (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){
	 * 
	 * // application=new Application();
	 * 
	 * if(ineligibleDupRemarks.get(key)!=null &&
	 * !(ineligibleDupRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleDupRemarks.get(key);
	 * 
	 * }else {
	 * 
	 * remarks=""; }
	 * 
	 * application.setAppRefNo(appRefNumber); application.setRemarks(remarks);
	 * if(application.getStatus().equals("EN") ||
	 * application.getStatus().equals("EP") ||
	 * application.getStatus().equals("EH")) { application.setStatus("EP");
	 * appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * }else{
	 * 
	 * application.setStatus(status);
	 * appProcessor.updatePendingRejectedStatus(application,userId); }
	 * //appProcessor.updateGeneralStatus(application,userId);
	 * 
	 * ineligibleDupPendingApplications.add(application);
	 * //apForm.setIneligibleDupPendingApplications
	 * (ineligibleDupPendingApplications);
	 * 
	 * }else if (status.equals("WEN")){
	 * 
	 * approvedAmount=Double.parseDouble((String)ineligibleDupApprovedAmt.get(key
	 * ));
	 * 
	 * if (ineligibleDupRemarks.get(key)!=null &&
	 * !(ineligibleDupRemarks.get(key).equals(""))) {
	 * remarks=(String)ineligibleDupRemarks.get(key); } else {
	 * 
	 * remarks=""; }
	 * 
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "approved amount:" + approvedAmount);
	 * 
	 * 
	 * // generation of CGPAN application.setAppRefNo(appRefNumber);
	 * application.setApprovedAmount(approvedAmount);
	 * application.setRemarks(remarks);
	 * application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	 * 
	 * /** DAN Generation for Enhanced amount
	 * 
	 * 
	 * ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber); int
	 * countDan = ((Integer)countAmount.get(0)).intValue(); double
	 * enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
	 * 
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps",
	 * "countDan:" + countDan);
	 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
	 * ,"enhancedAmount:" + enhancedAmount);
	 * 
	 * if(countDan>0) { application.setApprovedAmount(approvedAmount -
	 * enhancedAmount); appProcessor.generateDanForEnhance(application,user);
	 * 
	 * }
	 * 
	 * appProcessor.updateEnhanceAppStatus(application,userId);
	 * 
	 * 
	 * 
	 * ineligibleDupApprovedApplications.add(application);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 * apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications
	 * );
	 * apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
	 * apForm
	 * .setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
	 * apForm
	 * .setIneligibleDupPendingApplications(ineligibleDupPendingApplications);
	 * 
	 * 
	 * return mapping.findForward("afterApprovalPage");
	 * 
	 * }
	 *//******************/
//bhu
	
	public ActionForward afterApprovalApps(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

				Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","Entered");

				ApplicationProcessor appProcessor=new ApplicationProcessor();
	      ApplicationDAO applicationDAO = new ApplicationDAO();
	      Application applicationNew = new Application();
				//RpProcessor rpProcessor=new  RpProcessor();
				//RpHelper rpHelper = new RpHelper() ;
		//	 HttpSession applicationSession=request.getSession(false);
				String message="";
				String subject="";
	      
				User user=getUserInformation(request);

				String userId=user.getUserId();

				APForm apForm=(APForm)form;

				//Clear Applications
				ArrayList clearApprovedApplications=new ArrayList();
				ArrayList clearHoldApplications=new ArrayList();
				ArrayList clearRejectedApplications=new ArrayList();
				ArrayList clearPendingApplications=new ArrayList();

				Map clearAppRefNos=apForm.getClearAppRefNo();
				Map clearRemarks=apForm.getClearRemarks();
				Map clearStatus=apForm.getClearStatus();
				Map clearApprovedAmt=apForm.getClearApprovedAmt();
	            Map clearRsfApprovedAmt=apForm.getClearRsfApprovedAmt();
				Set clearAppRefNosSet=clearAppRefNos.keySet();
				Set clearRemarksSet=clearRemarks.keySet();
				Set clearStatusSet=clearStatus.keySet();
				Set clearApprovedAmtSet=clearApprovedAmt.keySet();
	            Set clearRsfApprovedAmtSet=clearRsfApprovedAmt.keySet();
				Iterator clearAppRefNosIterator=clearAppRefNosSet.iterator();
	/*			Iterator clearRemarksIterator=clearRemarksSet.iterator();
				Iterator clearStatusIterator=clearStatusSet.iterator();
				Iterator clearApprovedAmtIterator=clearApprovedAmtSet.iterator();
	*/
				while (clearAppRefNosIterator.hasNext())
				{
					Application application=new Application();
	        
					String key=(String)clearAppRefNosIterator.next();

					String appRefNumber=(String)clearAppRefNos.get(key);
	    //    System.out.println("app ref no :" + appRefNumber);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","app ref no :" + appRefNumber);
					String approvedStatus=(String)clearStatus.get(key);
	    //   System.out.println("status:" + approvedStatus);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + approvedStatus);

					double approvedAmount;
					String remarks;

					String mliId=null;
					String cgpan="";
					String cgbid="";
					application = appProcessor.getApplication(mliId,cgpan,appRefNumber);		
	        
	                applicationNew=applicationDAO.getAppForAppRef(mliId,appRefNumber);

					/**
					* This loop calculates the guarantee fee,cgpan and cgbid for approved applications
					*/
					if (approvedStatus.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
					{
						
						Date timeTrack=new Date();
						System.out.println("------1-----------------"+timeTrack);
//						application=new Application();
	    //      System.out.println("afterApprovalApps"+"entering approved applications");
	       //    System.out.println("Scheme"+application.getScheme());
						Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");
	       /*    if((application.getScheme()).equals("RSF") || application.getScheme()=="RSF")
	             {
	                	approvedAmount=Double.parseDouble((String)clearRsfApprovedAmt.get(key));
	             } else{
	                      	approvedAmount=Double.parseDouble((String)clearApprovedAmt.get(key));
	              }
					*/
	        approvedAmount=Double.parseDouble((String)clearApprovedAmt.get(key));
		//				System.out.println("approved amount:" + approvedAmount);
	         Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved amount:" + approvedAmount);

						if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals("")))
						{
							remarks=(String)clearRemarks.get(key);
	    //     System.out.println("remarks:"+remarks);

						}else {

							remarks="";
						}


						//generation of CGPAN
					application.setAppRefNo(appRefNumber);
	  //    System.out.println("App RefNo:"+appRefNumber);
	          
						application.setApprovedAmount(approvedAmount);
	  //     System.out.println(application.getApprovedAmount());
						application.setRemarks(remarks);
	          
						application.setStatus(approvedStatus);
	         
	    //    System.out.println(application.getAppRefNo());
	   //       System.out.println(application.getRemarks());
	    //      System.out.println(application.getStatus());
					//	System.out.println("CGPAN Reference:"+application.getCgpanReference());
						if((application.getCgpanReference()!=null && !application.getCgpanReference().equals("")) && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
						{
							Application testApplication = appProcessor.getPartApplication(null,application.getCgpanReference(),"");
							if(testApplication.getStatus().equals("EX"))
							{
								String renewCgpan=application.getCgpanReference();
	             System.out.println("renewCgpan:"+renewCgpan);

								cgpan=appProcessor.generateRenewCgpan(renewCgpan);
	       //   System.out.println("cgpan:"+cgpan);
								
								application.setCgpan(cgpan);
					
							application.setAdditionalTC(true);
							application.setWcRenewal(true);
	              
	            int applicationRefNoCount = applicationDAO.getAppRefNoCount(application.getAppRefNo());
	       //     System.out.println("Test applicationRefNoCount:"+applicationRefNoCount);
	      //      System.out.println("Test applicationNew.getStatus():"+applicationNew.getStatus());
	              
	              

								//update application status
								
					 if(applicationNew.getStatus().equals("MO") && applicationRefNoCount >=1){
	          //  System.out.println("Modified Application 7872 -App ref No:"+application.getAppRefNo()+ " approvedStatus:"+approvedStatus);
	            appProcessor.updateRejectedApplicationsStatus(application,userId);
	            }
	            else {
	            //	System.out.println("------before update app status-----------------"+timeTrack);
	       			appProcessor.updateApplicationsStatus(application,userId);
	       			//System.out.println("------after update app status-----------------"+timeTrack);
	            }
								
								
							}
							testApplication=null;
						
						}
						else{
							
							//appProcessor.updateAppCgpanReference(application);					

							Date d=new Date();
							System.out.println("------before cgpan generation-----------------"+d);
							cgpan=appProcessor.generateCgpan(application);
							Date d1=new Date();
							System.out.println("------after cgpan generation-----------------"+d1);
							
							application.setCgpan(cgpan);
							System.out.println("Cgpan:"+cgpan);
	       //     System.out.println("Application Status:"+applicationNew.getStatus());
	        int applicationRefNoCount = applicationDAO.getAppRefNoCount(application.getAppRefNo());
	       System.out.println("applicationRefNoCount:"+applicationRefNoCount);
	        if(applicationNew.getStatus().equals("MO") && applicationRefNoCount >=1){
	         System.out.println("Modified Application 7872 -App ref No:"+application.getAppRefNo()+ " approvedStatus:"+approvedStatus);
	            appProcessor.updateRejectedApplicationsStatus(application,userId);
	            }
	            else {
	            	Date d2=new Date();
	            	System.out.println("-------before updating app status----------------"+d2);
	       			//appProcessor.updateApplicationsStatus(application,userId);
	       			Date d3=new Date();
	       			System.out.println("------after updating app status------------------"+d3);
	            }
							
						}
						
						

						

						//update application status
						
		/*				appProcessor.updateCgpan(application);


						//generate cgbid
						int ssiRefNo=application.getBorrowerDetails().getSsiDetails().getBorrowerRefNo();
						cgbid = appProcessor.generateCgbid(ssiRefNo);
						if (cgbid!=null && !(cgbid.equals("")))
						{
							application.getBorrowerDetails().getSsiDetails().setCgbid(cgbid);

							//updation of cgbid
							appProcessor.updateCgbid(ssiRefNo,cgbid);

						}

			*/			clearApprovedApplications.add(application);
					
			Date d5=new Date();
			System.out.println("------after approving aplication-----------------"+d5);

					}
					else if (approvedStatus.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){

//						application=new Application();

						if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals("")))
						{
							remarks=(String)clearRemarks.get(key);
	        //    System.out.println("remarks1:"+remarks);

						}else {

							remarks="";
						}


						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
						application.setStatus(approvedStatus);
	       //   System.out.println(application.getAppRefNo());
	       //   System.out.println(application.getRemarks());
	       //   System.out.println(application.getStatus());
						appProcessor.updateGeneralStatus(application,userId);

						clearHoldApplications.add(application);
						//apForm.setClearHoldApplications(clearHoldApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been put on hold because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
				//		System.out.println("Rejected Application MLI Id :"+application.getMliID());			
						sendMailEmail(message,application.getMliID(),user,subject);
					
					}
					else if (approvedStatus.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS))
	        {

						//application=new Application();

						if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals("")))
						{
							remarks=(String)clearRemarks.get(key);

						}else {

							remarks="";
						}


						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
						application.setStatus(approvedStatus);
					
						appProcessor.updatePendingRejectedStatus(application,userId);

						//appProcessor.updateGeneralStatus(application,userId);

						clearRejectedApplications.add(application);
						//apForm.setClearRejectedApplications(clearRejectedApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been rejected because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				


					}else if (approvedStatus.equals(ApplicationConstants.APPLICATION_PENDING_STATUS))
	        {

						//application=new Application();
		
						if(clearRemarks.get(key)!=null && !(clearRemarks.get(key).equals("")))
						{
							remarks=(String)clearRemarks.get(key);
		
						}else {
		
							remarks="";
						}
		
		
						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
						application.setStatus(approvedStatus);
		    //   System.out.println(application.getAppRefNo());
	      //    System.out.println(application.getRemarks());
	      //    System.out.println(application.getStatus());
						appProcessor.updatePendingRejectedStatus(application,userId);
						//appProcessor.updateGeneralStatus(application,userId);
		
						clearPendingApplications.add(application);
						//apForm.setClearPendingApplications(clearPendingApplications);

					}

					application=null;
				}
				apForm.setClearApprovedApplications(clearApprovedApplications);
				apForm.setClearHoldApplications(clearHoldApplications);
				apForm.setClearRejectedApplications(clearRejectedApplications);
				apForm.setClearPendingApplications(clearPendingApplications);

				//Probable Duplicates Applications

				//Array lists for the display
				ArrayList dupApprovedApplications=new ArrayList();
				ArrayList dupHoldApplications=new ArrayList();
				ArrayList dupRejectedApplications=new ArrayList();
				ArrayList dupPendingApplications=new ArrayList();

				Map duplicateAppRefNos=apForm.getDuplicateAppRefNo();
				Map duplicateRemarks=apForm.getDuplicateRemarks();
				Map duplicateStatus=apForm.getDuplicateStatus();
				Map duplicateApprovedAmt=apForm.getDuplicateApprovedAmt();

				Set duplicateAppRefNosSet=duplicateAppRefNos.keySet();
				Set duplicateRemarksSet=duplicateRemarks.keySet();
				Set duplicateStatusSet=duplicateStatus.keySet();
				Set duplicateApprovedAmtSet=duplicateApprovedAmt.keySet();

				Iterator duplicateAppRefNosIterator=duplicateAppRefNosSet.iterator();
	/*			Iterator duplicateRemarksIterator=duplicateRemarksSet.iterator();
				Iterator duplicateStatusIterator=duplicateStatusSet.iterator();
				Iterator duplicateApprovedAmtIterator=duplicateApprovedAmtSet.iterator();
	*/
				while (duplicateAppRefNosIterator.hasNext())
				{
					Application application=new Application();

					String key=(String)duplicateAppRefNosIterator.next();

					String appRefNumber=(String)duplicateAppRefNos.get(key);
	     //  System.out.println("app ref no :" + appRefNumber);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","app ref no :" + appRefNumber);
					String status=(String)duplicateStatus.get(key);
					//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
//					String remarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next());
//					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved remarks:" + remarks);
	    //     System.out.println("remarks:" + status);
					double approvedAmount;
					String remarks;
				
					String mliId=null;
					String cgpan="";
					String cgbid="";
					application = appProcessor.getApplication(mliId,cgpan,appRefNumber);
				  applicationNew=applicationDAO.getAppForAppRef(mliId,appRefNumber);
							
					String appStatus =application.getStatus();
	   //    System.out.println("approved remarks:" + appStatus);
					//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","appStatus:" + appStatus);
				

					/**
					* This loop calculates generates cgpan and cgbid for approved applications
					*/
					if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
					{
						//application=new Application();
						//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");
						
						Date timeTrack=new Date();
						
						System.out.println("------1-----------------"+timeTrack);

							approvedAmount=Double.parseDouble((String)duplicateApprovedAmt.get(key));

						if (duplicateRemarks.get(key)!=null && !(duplicateRemarks.get(key).equals("")))
						{
							remarks=(String)duplicateRemarks.get(key);
	   //        System.out.println("remarks:"+remarks);
						}
						else {

							remarks="";
						}


						//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved amount:" + approvedAmount);

						application.setAppRefNo(appRefNumber);
						application.setApprovedAmount(approvedAmount);
						application.setRemarks(remarks);
		//			 System.out.println(application.getAppRefNo());
	  //        System.out.println(application.getApprovedAmount());
	  //       System.out.println(application.getRemarks());
	  //      System.out.println(application.getStatus());
					
						/**
						 * Approval for Enhancement Applications
						 */
						
						if (application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
						{
							ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
							int countDan = ((Integer)countAmount.get(0)).intValue();
	   //       System.out.println("countDan:"+countDan);
							double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
					  // System.out.println("enhancedAmount:" + enhancedAmount);
							//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","countDan:" + countDan);
							//Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","enhancedAmount:" + enhancedAmount);
							application.setCgpan((String)countAmount.get(2));
						//User user=getUserInformation(request);
				     //String userId=user.getUserId();
							if(countDan>0)
							{							
								application.setApprovedAmount(approvedAmount - enhancedAmount);
	           //  System.out.println("approvedAmount - enhancedAmount:"+application.getApprovedAmount());
	           // added and modified by sukumar for capturing the enhancement amount
						   application.setEnhancementAmount(approvedAmount - enhancedAmount);
							 appProcessor.generateDanForEnhance(application,user);
						
							}
					
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
	     //      System.out.println("application.setStatus():"+application.getStatus());
							application.setApprovedAmount(approvedAmount);
	      //     System.out.println(application.getApprovedAmount());
							
							appProcessor.updateEnhanceAppStatus(application,userId);
							
							
						}
						/**
						 * Approval for Renewal Applications
						 */
						else if((application.getCgpanReference()!=null && !application.getCgpanReference().equals("")) && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
						{
							Application testApplication = appProcessor.getPartApplication(null,application.getCgpanReference(),"");
							if(testApplication.getStatus().equals("EX"))
							{
								String renewCgpan=application.getCgpanReference();

								cgpan=appProcessor.generateRenewCgpan(renewCgpan);
								
								application.setCgpan(cgpan);
					
								application.setAdditionalTC(true);
								application.setWcRenewal(true);

								//update application status
								
								application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
								
								appProcessor.updateApplicationsStatus(application,userId);
							
	                                                
	                                                	
								
							}
							testApplication=null;
						
						}
						else{
							
							Date d=new Date();
							
							System.out.println("------2-----------------"+d);
							appProcessor.updateAppCgpanReference(application);
							Date d2=new Date();
							System.out.println("------before cgpan generation-----3-----------"+d2);
							
							cgpan=appProcessor.generateCgpan(application);
							Date d22=new Date();
							
							System.out.println("-----after cgpan generation---4-----------------"+d22);

							application.setCgpan(cgpan);
	            
	     //       System.out.println("CGPAN:"+cgpan);
							
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
							
							 if(applicationNew.getStatus().equals("MO")){
	            appProcessor.updateRejectedApplicationsStatus(application,userId);
	            }
	            else {
	            	Date d3=new Date();
	            	System.out.println("-----before update app status-5-----------------"+d3);
	       			appProcessor.updateApplicationsStatus(application,userId);
	       			Date d4=new Date();
	       			System.out.println("-----after update app status-6-----------------"+d4);
	            }
							
						}

						dupApprovedApplications.add(application);
						//apForm.setDupApprovedApplications(dupApprovedApplications);


					}
					else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {

						//application=new Application();

						if (duplicateRemarks.get(key)!=null && !(duplicateRemarks.get(key).equals("")))
						{
							remarks=(String)duplicateRemarks.get(key);
						}
						else {

							remarks="";
						}


						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
					
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("EH");
						
						}else{
						
							application.setStatus(status);
						}
					

						appProcessor.updateGeneralStatus(application,userId);

						dupHoldApplications.add(application);
						//apForm.setDupHoldApplications(dupHoldApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been put on hold because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				

					}
					else if (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {

//						application=new Application();

						Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","duplicateRemarks.get(key):" + duplicateRemarks.get(key));

						if (duplicateRemarks.get(key)!=null && !(duplicateRemarks.get(key).equals("")))
						{
							remarks=(String)duplicateRemarks.get(key);
						}
						else {

							remarks="";
						}


						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
					
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("ER");
							appProcessor.updateRejectStatus(application,userId);
						
						}else{
						
							application.setStatus(status);
							appProcessor.updatePendingRejectedStatus(application,userId);
						}
					
						//appProcessor.updateGeneralStatus(application,userId);

						dupRejectedApplications.add(application);
						//apForm.setDupRejectedApplications(dupRejectedApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been rejected because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				

					}else if (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){

					//application=new Application();
		
					if(duplicateRemarks.get(key)!=null && !(duplicateRemarks.get(key).equals("")))
					{
						remarks=(String)duplicateRemarks.get(key);
		
					}else {
		
						remarks="";
					}
		
		
					application.setAppRefNo(appRefNumber);
					application.setRemarks(remarks);
				
					if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
					{
						application.setStatus("EP");
						appProcessor.updateGeneralStatus(application,userId);
						
					}else{
						
						application.setStatus(status);
						appProcessor.updatePendingRejectedStatus(application,userId);
					}				
				
					//appProcessor.updateGeneralStatus(application,userId);
		
					dupPendingApplications.add(application);
					//apForm.setDupPendingApplications(dupPendingApplications);

				}
				application=null;
			}
				apForm.setDupApprovedApplications(dupApprovedApplications);
				apForm.setDupHoldApplications(dupHoldApplications);
				apForm.setDupRejectedApplications(dupRejectedApplications);
				apForm.setDupPendingApplications(dupPendingApplications);
		

				//Probable Ineligible Applications

				ArrayList ineligibleApprovedApplications=new ArrayList();
				ArrayList ineligibleHoldApplications=new ArrayList();
				ArrayList ineligibleRejectedApplications=new ArrayList();
				ArrayList ineligiblePendingApplications=new ArrayList();


				Map ineligibleAppRefNos=apForm.getIneligibleAppRefNo();
				Map ineligibleRemarks=apForm.getIneligibleRemarks();
				Map ineligibleStatus=apForm.getIneligibleStatus();
				Map ineligibleApprovedAmt=apForm.getIneligibleApprovedAmt();

				Set ineligibleAppRefNosSet=ineligibleAppRefNos.keySet();
				Set ineligibleRemarksSet=ineligibleRemarks.keySet();
				Set ineligibleStatusSet=ineligibleStatus.keySet();
				Set ineligibleApprovedAmtSet=ineligibleApprovedAmt.keySet();

				Iterator ineligibleAppRefNosIterator=ineligibleAppRefNosSet.iterator();
	/*			Iterator ineligibleRemarksIterator=ineligibleRemarksSet.iterator();
				Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();
				Iterator ineligibleApprovedAmtIterator=ineligibleApprovedAmtSet.iterator();
	*/
				while (ineligibleAppRefNosIterator.hasNext())
				{
					Application application=new Application();

					String key=(String)ineligibleAppRefNosIterator.next();

					String appRefNumber=(String)ineligibleAppRefNos.get(key);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","app ref no :" + appRefNumber);
					String status=(String)ineligibleStatus.get(key);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
		/*			String remarks=(String)ineligibleRemarks.get(ineligibleRemarksIterator.next());
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved remarks:" + remarks);
		*/
					double approvedAmount;
					String remarks;
				
					String mliId=null;
					String cgpan="";
					String cgbid="";
					application = appProcessor.getApplication(mliId,cgpan,appRefNumber);	
				
					String appStatus =application.getStatus();
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","appStatus:" + appStatus);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
							
				
		
					/**
					* This loop calculates the guarantee fee,cgpan and cgbid for approved applications
					*/
					if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
					{
//						application=new Application();

						Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");

						if (ineligibleApprovedAmt.get(key)!=null)
						{
							approvedAmount=Double.parseDouble((String)ineligibleApprovedAmt.get(key));

						}else {

							approvedAmount=0;

						}
						if (ineligibleRemarks.get(key)!=null && !(ineligibleRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleRemarks.get(key);
						}
						else {

							remarks="";
						}


						//generation of CGPAN
						application.setAppRefNo(appRefNumber);
						application.setApprovedAmount(approvedAmount);
						application.setRemarks(remarks);
						

						/**
						 * Approval for Enhancement Applications
						 */
						
						if (application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
						{
							ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
							int countDan = ((Integer)countAmount.get(0)).intValue();
							double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
					
							Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","countDan:" + countDan);
							Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","enhancedAmount:" + enhancedAmount);
							application.setCgpan((String)countAmount.get(2));
					
							if(countDan>0)
							{
								application.setApprovedAmount(approvedAmount - enhancedAmount);
						
								appProcessor.generateDanForEnhance(application,user);
						
							}
					
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
							
							application.setApprovedAmount(approvedAmount);
							
							appProcessor.updateEnhanceAppStatus(application,userId);
							
							
						}
						/**
						 * Approval for Renewal Applications
						 */
						else if((application.getCgpanReference()!=null && !application.getCgpanReference().equals("")) && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
						{
							Application testApplication = appProcessor.getPartApplication(null,application.getCgpanReference(),"");
							if(testApplication.getStatus().equals("EX"))
							{
								String renewCgpan=application.getCgpanReference();

								cgpan=appProcessor.generateRenewCgpan(renewCgpan);
								
								application.setCgpan(cgpan);
					
								application.setAdditionalTC(true);
								application.setWcRenewal(true);

								//update application status
								application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
								
								appProcessor.updateApplicationsStatus(application,userId);
								
								
							}
							testApplication=null;
						
						}
						else{
							appProcessor.updateAppCgpanReference(application);
							
							cgpan=appProcessor.generateCgpan(application);

							application.setCgpan(cgpan);
							
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
							
							appProcessor.updateApplicationsStatus(application,userId);
							
						}
						ineligibleApprovedApplications.add(application);
						//apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);


					}
					else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){

//						application=new Application();

						if (ineligibleRemarks.get(key)!=null && !(ineligibleRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleRemarks.get(key);
						}
						else {

							remarks="";
						}

						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
					
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("EH");
						
						}else{
						
							application.setStatus(status);
						}
					
					
						//application.setStatus(status);

						appProcessor.updateGeneralStatus(application,userId);

						ineligibleHoldApplications.add(application);
						//apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been put on hold beacuse "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				
		
					}

					else if (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)){

//						application=new Application();

						if (ineligibleRemarks.get(key)!=null && !(ineligibleRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleRemarks.get(key);
						}
						else {

							remarks="";
						}

						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("ER");
							appProcessor.updateRejectStatus(application,userId);
						
						}else{
						
							application.setStatus(status);
							appProcessor.updatePendingRejectedStatus(application,userId);
						}
						//appProcessor.updateGeneralStatus(application,userId);

						ineligibleRejectedApplications.add(application);
						//apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been rejected because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				
				
					
					}else if (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){

//					application=new Application();
		
					if(ineligibleRemarks.get(key)!=null && !(ineligibleRemarks.get(key).equals("")))
					{
						remarks=(String)ineligibleRemarks.get(key);
		
					}else {
		
						remarks="";
					}
		
		
					application.setAppRefNo(appRefNumber);
					application.setRemarks(remarks);
					if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
					{
						application.setStatus("EP");
						appProcessor.updateGeneralStatus(application,userId);
						
					}else{
						
						application.setStatus(status);
						appProcessor.updatePendingRejectedStatus(application,userId);
					}				
					//appProcessor.updateGeneralStatus(application,userId);
		
					ineligiblePendingApplications.add(application);
					//apForm.setIneligiblePendingApplications(ineligiblePendingApplications);

				}
				application=null;
			}
				apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);
				apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
				apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);
				apForm.setIneligiblePendingApplications(ineligiblePendingApplications);
			
			

				//Ineligible Duplicate Applications
				ArrayList ineligibleDupApprovedApplications=new ArrayList();
				ArrayList ineligibleDupHoldApplications=new ArrayList();
				ArrayList ineligibleDupRejectedApplications=new ArrayList();
				ArrayList ineligibleDupPendingApplications=new ArrayList();


				Map ineligibleDupAppRefNos=apForm.getIneligibleDupAppRefNo();
				Map ineligibleDupRemarks=apForm.getIneligibleDupRemarks();
				Map ineligibleDupStatus=apForm.getIneligibleDupStatus();
				Map ineligibleDupApprovedAmt=apForm.getIneligibleDupApprovedAmt();

				Set ineligibleDupAppRefNosSet=ineligibleDupAppRefNos.keySet();
				Set ineligibleDupRemarksSet=ineligibleDupRemarks.keySet();
				Set ineligibleDupStatusSet=ineligibleDupStatus.keySet();
				Set ineligibleDupApprovedAmtSet=ineligibleDupApprovedAmt.keySet();

				Iterator ineligibleDupAppRefNosIterator=ineligibleDupAppRefNosSet.iterator();
	/*			Iterator ineligibleDupRemarksIterator=ineligibleDupRemarksSet.iterator();
				Iterator ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();
				Iterator ineligibleDupApprovedAmtIterator=ineligibleDupApprovedAmtSet.iterator();
	*/
				while (ineligibleDupAppRefNosIterator.hasNext())
				{
					Application application=new Application();

					String key=(String)ineligibleDupAppRefNosIterator.next();

					String appRefNumber=(String)ineligibleDupAppRefNos.get(key);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","app ref no :" + appRefNumber);
					String status=(String)ineligibleDupStatus.get(key);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
		/*			String remarks=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator.next());
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved remarks:" + remarks);
		*/
					double approvedAmount;
					String remarks;
				
					String mliId=null;
					String cgpan="";
					String cgbid="";
					application = appProcessor.getApplication(mliId,cgpan,appRefNumber);
				
					String appStatus =application.getStatus();
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","appStatus:" + appStatus);
					Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
				
					/**
					* This loop calculates the guarantee fee,cgpan and cgbid for approved applications
					*/
					if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
					{
//						application=new Application();

						Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");

						if (ineligibleDupApprovedAmt.get(key)!=null)
						{
							approvedAmount=Double.parseDouble((String)ineligibleDupApprovedAmt.get(key));

						}else {

							approvedAmount=0;

						}
						if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleDupRemarks.get(key);
						}
						else {

							remarks="";
						}



						Log.log(Log.INFO,"ApplicationProcessingAction","","approved amount:" + approvedAmount);


						//generation of CGPAN
						application.setAppRefNo(appRefNumber);
						application.setApprovedAmount(approvedAmount);
						application.setRemarks(remarks);
						

						/**
						 * Approval for Enhancement Applications
						 */
						
						if (application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
						{
							ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
							int countDan = ((Integer)countAmount.get(0)).intValue();
							double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
					
							Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","countDan:" + countDan);
							Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","enhancedAmount:" + enhancedAmount);
							application.setCgpan((String)countAmount.get(2));
					
							if(countDan>0)
							{
								application.setApprovedAmount(approvedAmount - enhancedAmount);
						
								appProcessor.generateDanForEnhance(application,user);
						
							}
					
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
							
							application.setApprovedAmount(approvedAmount);
							
							appProcessor.updateEnhanceAppStatus(application,userId);
							
							
						}
						/**
						 * Approval for Renewal Applications
						 */
						else if((application.getCgpanReference()!=null && !application.getCgpanReference().equals("")) && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
						{
							Application testApplication = appProcessor.getPartApplication(null,application.getCgpanReference(),"");
							if(testApplication.getStatus().equals("EX"))
							{
								String renewCgpan=application.getCgpanReference();

								cgpan=appProcessor.generateRenewCgpan(renewCgpan);
								
								application.setCgpan(cgpan);
					
								application.setAdditionalTC(true);
								application.setWcRenewal(true);

								//update application status
								application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
								
								appProcessor.updateApplicationsStatus(application,userId);
								
							}
							testApplication=null;
						
						}
						else{
							
							appProcessor.updateAppCgpanReference(application);
							
							cgpan=appProcessor.generateCgpan(application);

							application.setCgpan(cgpan);
							
							application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
							
							appProcessor.updateApplicationsStatus(application,userId);
							
						}
						ineligibleDupApprovedApplications.add(application);
						//apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);


					}
					else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){

//						application=new Application();

						if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleDupRemarks.get(key);
						}
						else {

							remarks="";
						}

						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
					
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("EH");
						
						}else{
						
							application.setStatus(status);
						}
					


						appProcessor.updateGeneralStatus(application,userId);

						ineligibleDupHoldApplications.add(application);
						//apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been put on hold because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;							
																			
						sendMailEmail(message,application.getMliID(),user,subject);				

					}
					else if (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)){

//						application=new Application();

						if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
						{
							remarks=(String)ineligibleDupRemarks.get(key);
						}
						else {

							remarks="";
						}

						application.setAppRefNo(appRefNumber);
						application.setRemarks(remarks);
						if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
						{
							application.setStatus("ER");
							appProcessor.updateRejectStatus(application,userId);
						
						}else{
						
							application.setStatus(status);
							appProcessor.updatePendingRejectedStatus(application,userId);
						}
						//appProcessor.updateGeneralStatus(application,userId);

						ineligibleDupRejectedApplications.add(application);
						//apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
					
						message="The Application Reference No. :" + appRefNumber + " has been been rejected because "
								+ remarks;
							
						subject="Status of Application Reference No. :" + appRefNumber;				
																			
						sendMailEmail(message,application.getMliID(),user,subject);				

					}else if (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){

//					application=new Application();
		
					if(ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
					{
						remarks=(String)ineligibleDupRemarks.get(key);
		
					}else {
		
						remarks="";
					}
		
					application.setAppRefNo(appRefNumber);
					application.setRemarks(remarks);
					if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
					{
						application.setStatus("EP");
						appProcessor.updateGeneralStatus(application,userId);
						
					}else{
						
						application.setStatus(status);
						appProcessor.updatePendingRejectedStatus(application,userId);
					}				
					//appProcessor.updateGeneralStatus(application,userId);
		
					ineligibleDupPendingApplications.add(application);
					//apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);

				}
				application=null;
			}
			apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);
			apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
			apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
			apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);
			
			
				return mapping.findForward("afterApprovalPage");

			}
	
	
	
	
	
	
	
	public ActionForward afterApprovalAppsEN(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

	Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","Entered");

	ApplicationProcessor appProcessor=new ApplicationProcessor();
ApplicationDAO applicationDAO = new ApplicationDAO();
Application applicationNew = new Application();
	//RpProcessor rpProcessor=new  RpProcessor();
	//RpHelper rpHelper = new RpHelper() ;
//	 HttpSession applicationSession=request.getSession(false);
	String message="";
	String subject="";

	User user=getUserInformation(request);

	String userId=user.getUserId();

	APForm apForm=(APForm)form;

	//Clear Applications
	ArrayList clearApprovedApplications=new ArrayList();
	ArrayList clearHoldApplications=new ArrayList();
	ArrayList clearRejectedApplications=new ArrayList();
	ArrayList clearPendingApplications=new ArrayList();

	Map clearAppRefNos=apForm.getClearAppRefNo();
	Map clearRemarks=apForm.getClearRemarks();
	Map clearStatus=apForm.getClearStatus();
	Map clearApprovedAmt=apForm.getClearApprovedAmt();
    Map clearRsfApprovedAmt=apForm.getClearRsfApprovedAmt();
	Set clearAppRefNosSet=clearAppRefNos.keySet();
	Set clearRemarksSet=clearRemarks.keySet();
	Set clearStatusSet=clearStatus.keySet();
	Set clearApprovedAmtSet=clearApprovedAmt.keySet();
    Set clearRsfApprovedAmtSet=clearRsfApprovedAmt.keySet();
	Iterator clearAppRefNosIterator=clearAppRefNosSet.iterator();
/*			Iterator clearRemarksIterator=clearRemarksSet.iterator();
	Iterator clearStatusIterator=clearStatusSet.iterator();
	Iterator clearApprovedAmtIterator=clearApprovedAmtSet.iterator();
*/
	
	//Probable Duplicates Applications

	//Array lists for the display
	ArrayList dupApprovedApplications=new ArrayList();
	ArrayList dupHoldApplications=new ArrayList();
	ArrayList dupRejectedApplications=new ArrayList();
	ArrayList dupPendingApplications=new ArrayList();

	Map duplicateAppRefNos=apForm.getDuplicateAppRefNo();
	Map duplicateRemarks=apForm.getDuplicateRemarks();
	Map duplicateStatus=apForm.getDuplicateStatus();
	Map duplicateApprovedAmt=apForm.getDuplicateApprovedAmt();

	Set duplicateAppRefNosSet=duplicateAppRefNos.keySet();
	Set duplicateRemarksSet=duplicateRemarks.keySet();
	Set duplicateStatusSet=duplicateStatus.keySet();
	Set duplicateApprovedAmtSet=duplicateApprovedAmt.keySet();

	Iterator duplicateAppRefNosIterator=duplicateAppRefNosSet.iterator();
/*			Iterator duplicateRemarksIterator=duplicateRemarksSet.iterator();
	Iterator duplicateStatusIterator=duplicateStatusSet.iterator();
	Iterator duplicateApprovedAmtIterator=duplicateApprovedAmtSet.iterator();
*/



	//Probable Ineligible Applications

	ArrayList ineligibleApprovedApplications=new ArrayList();
	ArrayList ineligibleHoldApplications=new ArrayList();
	ArrayList ineligibleRejectedApplications=new ArrayList();
	ArrayList ineligiblePendingApplications=new ArrayList();


	Map ineligibleAppRefNos=apForm.getIneligibleAppRefNo();
	Map ineligibleRemarks=apForm.getIneligibleRemarks();
	Map ineligibleStatus=apForm.getIneligibleStatus();
	Map ineligibleApprovedAmt=apForm.getIneligibleApprovedAmt();

	Set ineligibleAppRefNosSet=ineligibleAppRefNos.keySet();
	Set ineligibleRemarksSet=ineligibleRemarks.keySet();
	Set ineligibleStatusSet=ineligibleStatus.keySet();
	Set ineligibleApprovedAmtSet=ineligibleApprovedAmt.keySet();

	Iterator ineligibleAppRefNosIterator=ineligibleAppRefNosSet.iterator();
/*			Iterator ineligibleRemarksIterator=ineligibleRemarksSet.iterator();
	Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();
	Iterator ineligibleApprovedAmtIterator=ineligibleApprovedAmtSet.iterator();
*/




	//Ineligible Duplicate Applications
	ArrayList ineligibleDupApprovedApplications=new ArrayList();
	ArrayList ineligibleDupHoldApplications=new ArrayList();
	ArrayList ineligibleDupRejectedApplications=new ArrayList();
	ArrayList ineligibleDupPendingApplications=new ArrayList();


	Map ineligibleDupAppRefNos=apForm.getIneligibleDupAppRefNo();
	Map ineligibleDupRemarks=apForm.getIneligibleDupRemarks();
	Map ineligibleDupStatus=apForm.getIneligibleDupStatus();
	Map ineligibleDupApprovedAmt=apForm.getIneligibleDupApprovedAmt();

	Set ineligibleDupAppRefNosSet=ineligibleDupAppRefNos.keySet();
	Set ineligibleDupRemarksSet=ineligibleDupRemarks.keySet();
	Set ineligibleDupStatusSet=ineligibleDupStatus.keySet();
	Set ineligibleDupApprovedAmtSet=ineligibleDupApprovedAmt.keySet();

	Iterator ineligibleDupAppRefNosIterator=ineligibleDupAppRefNosSet.iterator();
/*			Iterator ineligibleDupRemarksIterator=ineligibleDupRemarksSet.iterator();
	Iterator ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();
	Iterator ineligibleDupApprovedAmtIterator=ineligibleDupApprovedAmtSet.iterator();
*/
	while (ineligibleDupAppRefNosIterator.hasNext())
	{
		Application application=new Application();

		String key=(String)ineligibleDupAppRefNosIterator.next();

		String appRefNumber=(String)ineligibleDupAppRefNos.get(key);
		Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","app ref no :" + appRefNumber);
		String status=(String)ineligibleDupStatus.get(key);
		Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
/*			String remarks=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator.next());
		Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved remarks:" + remarks);
*/
		double approvedAmount;
		String remarks;
	
		String mliId=null;
		String cgpan="";
		String cgbid="";
		application = appProcessor.getApplication(mliId,cgpan,appRefNumber);
	
		String appStatus =application.getStatus();
		Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","appStatus:" + appStatus);
		Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:" + status);
	
		/**
		* This loop calculates the guarantee fee,cgpan and cgbid for approved applications
		*/
		if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS))
		{
//			application=new Application();

			Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");

			if (ineligibleDupApprovedAmt.get(key)!=null)
			{
				approvedAmount=Double.parseDouble((String)ineligibleDupApprovedAmt.get(key));

			}else {

				approvedAmount=0;

			}
			if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
			{
				remarks=(String)ineligibleDupRemarks.get(key);
			}
			else {

				remarks="";
			}



			Log.log(Log.INFO,"ApplicationProcessingAction","","approved amount:" + approvedAmount);


			//generation of CGPAN
			application.setAppRefNo(appRefNumber);
			application.setApprovedAmount(approvedAmount);
			application.setRemarks(remarks);
			

			/**
			 * Approval for Enhancement Applications
			 */
			
			if (application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
			{
				ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
				int countDan = ((Integer)countAmount.get(0)).intValue();
				double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
		
				Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","countDan:" + countDan);
				Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","enhancedAmount:" + enhancedAmount);
				application.setCgpan((String)countAmount.get(2));
		
				if(countDan>0)
				{
					application.setApprovedAmount(approvedAmount - enhancedAmount);
			
					appProcessor.generateDanForEnhance(application,user);
			
				}
		
				application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
				
				application.setApprovedAmount(approvedAmount);
				
				appProcessor.updateEnhanceAppStatus(application,userId);
				
				
			}
			/**
			 * Approval for Renewal Applications
			 */
			else if((application.getCgpanReference()!=null && !application.getCgpanReference().equals("")) && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
			{
				Application testApplication = appProcessor.getPartApplication(null,application.getCgpanReference(),"");
				if(testApplication.getStatus().equals("EX"))
				{
					String renewCgpan=application.getCgpanReference();

					cgpan=appProcessor.generateRenewCgpan(renewCgpan);
					
					application.setCgpan(cgpan);
		
					application.setAdditionalTC(true);
					application.setWcRenewal(true);

					//update application status
					application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
					
					appProcessor.updateApplicationsStatus(application,userId);
					
				}
				testApplication=null;
			
			}
			else{
				
				appProcessor.updateAppCgpanReference(application);
				
				cgpan=appProcessor.generateCgpan(application);

				application.setCgpan(cgpan);
				
				application.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
				
				appProcessor.updateApplicationsStatus(application,userId);
				
			}
			ineligibleDupApprovedApplications.add(application);
			//apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);


		}
		else if (status.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)){

//			application=new Application();

			if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
			{
				remarks=(String)ineligibleDupRemarks.get(key);
			}
			else {

				remarks="";
			}

			application.setAppRefNo(appRefNumber);
			application.setRemarks(remarks);
		
			if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
			{
				application.setStatus("EH");
			
			}else{
			
				application.setStatus(status);
			}
		


			appProcessor.updateGeneralStatus(application,userId);

			ineligibleDupHoldApplications.add(application);
			//apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
		
			message="The Application Reference No. :" + appRefNumber + " has been been put on hold because "
					+ remarks;
				
			subject="Status of Application Reference No. :" + appRefNumber;							
																
			sendMailEmail(message,application.getMliID(),user,subject);				

		}
		else if (status.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)){

//			application=new Application();

			if (ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
			{
				remarks=(String)ineligibleDupRemarks.get(key);
			}
			else {

				remarks="";
			}

			application.setAppRefNo(appRefNumber);
			application.setRemarks(remarks);
			if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
			{
				application.setStatus("ER");
				appProcessor.updateRejectStatus(application,userId);
			
			}else{
			
				application.setStatus(status);
				appProcessor.updatePendingRejectedStatus(application,userId);
			}
			//appProcessor.updateGeneralStatus(application,userId);

			ineligibleDupRejectedApplications.add(application);
			//apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
		
			message="The Application Reference No. :" + appRefNumber + " has been been rejected because "
					+ remarks;
				
			subject="Status of Application Reference No. :" + appRefNumber;				
																
			sendMailEmail(message,application.getMliID(),user,subject);				

		}else if (status.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)){

//		application=new Application();

		if(ineligibleDupRemarks.get(key)!=null && !(ineligibleDupRemarks.get(key).equals("")))
		{
			remarks=(String)ineligibleDupRemarks.get(key);

		}else {

			remarks="";
		}

		application.setAppRefNo(appRefNumber);
		application.setRemarks(remarks);
		if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
		{
			application.setStatus("EP");
			appProcessor.updateGeneralStatus(application,userId);
			
		}else{
			
			application.setStatus(status);
			appProcessor.updatePendingRejectedStatus(application,userId);
		}				
		//appProcessor.updateGeneralStatus(application,userId);

		ineligibleDupPendingApplications.add(application);
		//apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);

	}
	application=null;
}
apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);
apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);


	return mapping.findForward("afterApprovalPage");

}


	

	
	
	
	
	/*public ActionForward afterApprovalAppsold2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception
{
		System.out.println("<br>application processing==9051===");
    Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", "Entered");
    ApplicationProcessor appProcessor = new ApplicationProcessor();
    ApplicationDAO applicationDAO = new ApplicationDAO();
    Application applicationNew = new Application();
    String message = "";
    String subject = "";
    User user = getUserInformation(request);
    String userId = user.getUserId();
    APForm apForm = (APForm)form;
    ArrayList clearApprovedApplications = new ArrayList();
    ArrayList clearHoldApplications = new ArrayList();
    ArrayList clearRejectedApplications = new ArrayList();
    ArrayList clearPendingApplications = new ArrayList();
    Map clearAppRefNos = apForm.getClearAppRefNo();
    Map clearRemarks = apForm.getClearRemarks();
    Map clearStatus = apForm.getClearStatus();
    Map clearApprovedAmt = apForm.getClearApprovedAmt();
    Map clearRsfApprovedAmt = apForm.getClearRsfApprovedAmt();
    Set clearAppRefNosSet = clearAppRefNos.keySet();
    Set clearRemarksSet = clearRemarks.keySet();
    Set clearStatusSet = clearStatus.keySet();
    Set clearApprovedAmtSet = clearApprovedAmt.keySet();
    Set clearRsfApprovedAmtSet = clearRsfApprovedAmt.keySet();
    for(Iterator clearAppRefNosIterator = clearAppRefNosSet.iterator(); clearAppRefNosIterator.hasNext();)
    {
        Application application = new Application();
        String key = (String)clearAppRefNosIterator.next();
        String appRefNumber = (String)clearAppRefNos.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("app ref no :").append(appRefNumber).toString());
        String approvedStatus = (String)clearStatus.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("status:").append(approvedStatus).toString());
        String mliId = null;
        String cgpan = "";
        String cgbid = "";
        application = appProcessor.getApplication(mliId, cgpan, appRefNumber);
        applicationNew = applicationDAO.getAppForAppRef(mliId, appRefNumber);
        if(approvedStatus.equals("AP"))
        {
        	System.out.println("<br>application processing==9090===");
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", "entering approved applications");
            double approvedAmount = Double.parseDouble((String)clearApprovedAmt.get(key));
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("approved amount:").append(approvedAmount).toString());
            String remarks;
            if(clearRemarks.get(key) != null && !clearRemarks.get(key).equals(""))
                remarks = (String)clearRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setApprovedAmount(approvedAmount);
            application.setRemarks(remarks);
            application.setStatus(approvedStatus);
            if(application.getCgpanReference() != null && !application.getCgpanReference().equals("") && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
            {
                Application testApplication = appProcessor.getPartApplication(null, application.getCgpanReference(), "");
                if(testApplication.getStatus().equals("EX"))
                {
                    String renewCgpan = application.getCgpanReference();
                    cgpan = appProcessor.generateRenewCgpan(renewCgpan);
                    application.setCgpan(cgpan);
                    application.setAdditionalTC(true);
                    application.setWcRenewal(true);
                    int applicationRefNoCount = applicationDAO.getAppRefNoCount(application.getAppRefNo());
                    if(applicationNew.getStatus().equals("MO") && applicationRefNoCount >= 1)
                        appProcessor.updateRejectedApplicationsStatus(application, userId);
                    else
                        appProcessor.updateApplicationsStatus(application, userId);
                }
                testApplication = null;
            } else
            {
                appProcessor.updateAppCgpanReference(application);
                cgpan = appProcessor.generateCgpan(application);
                application.setCgpan(cgpan);
                System.out.println((new StringBuilder()).append("Cgpan:").append(cgpan).toString());
                int applicationRefNoCount = applicationDAO.getAppRefNoCount(application.getAppRefNo());
                if(applicationNew.getStatus().equals("MO") && applicationRefNoCount >= 1)
                    appProcessor.updateRejectedApplicationsStatus(application, userId);
                else
                    appProcessor.updateApplicationsStatus(application, userId);
            }
            clearApprovedApplications.add(application);
        } else
        if(approvedStatus.equals("HO"))
        {
        	System.out.println("<br>application processing==9136===");
            String remarks;
            if(clearRemarks.get(key) != null && !clearRemarks.get(key).equals(""))
                remarks = (String)clearRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            application.setStatus(approvedStatus);
            appProcessor.updateGeneralStatus(application, userId);
            clearHoldApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been put on hold because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(approvedStatus.equals("RE"))
        {
        	System.out.println("<br>application processing==9153===");
            String remarks;
            if(clearRemarks.get(key) != null && !clearRemarks.get(key).equals(""))
                remarks = (String)clearRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            System.out.println("<br>application processing==9060===");
            application.setRemarks(remarks);
            application.setStatus(approvedStatus);
            appProcessor.updatePendingRejectedStatus(application, userId);
            clearRejectedApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been rejected because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(approvedStatus.equals("PE"))
        {
        	System.out.println("<br>application processing==9171===");
            String remarks;
            if(clearRemarks.get(key) != null && !clearRemarks.get(key).equals(""))
                remarks = (String)clearRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            System.out.println("<br>application processing==9178===");
            application.setRemarks(remarks);
            application.setStatus(approvedStatus);
            appProcessor.updatePendingRejectedStatus(application, userId);
            clearPendingApplications.add(application);
        }
        application = null;
    }

    apForm.setClearApprovedApplications(clearApprovedApplications);
    apForm.setClearHoldApplications(clearHoldApplications);
    apForm.setClearRejectedApplications(clearRejectedApplications);
    apForm.setClearPendingApplications(clearPendingApplications);
    ArrayList dupApprovedApplications = new ArrayList();
    ArrayList dupHoldApplications = new ArrayList();
    ArrayList dupRejectedApplications = new ArrayList();
    ArrayList dupPendingApplications = new ArrayList();
    Map duplicateAppRefNos = apForm.getDuplicateAppRefNo();
    Map duplicateRemarks = apForm.getDuplicateRemarks();
    Map duplicateStatus = apForm.getDuplicateStatus();
    Map duplicateApprovedAmt = apForm.getDuplicateApprovedAmt();
    Set duplicateAppRefNosSet = duplicateAppRefNos.keySet();
    Set duplicateRemarksSet = duplicateRemarks.keySet();
    Set duplicateStatusSet = duplicateStatus.keySet();
    Set duplicateApprovedAmtSet = duplicateApprovedAmt.keySet();
    for(Iterator duplicateAppRefNosIterator = duplicateAppRefNosSet.iterator(); duplicateAppRefNosIterator.hasNext();)
    {
        Application application = new Application();
        String key = (String)duplicateAppRefNosIterator.next();
        String appRefNumber = (String)duplicateAppRefNos.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("app ref no :").append(appRefNumber).toString());
        String status = (String)duplicateStatus.get(key);
        String mliId = null;
        String cgpan = "";
        String cgbid = "";
        application = appProcessor.getApplication(mliId, cgpan, appRefNumber);
        applicationNew = applicationDAO.getAppForAppRef(mliId, appRefNumber);
        String appStatus = application.getStatus();
        if(status.equals("AP"))
        {
        	System.out.println("<br>application processing==9219===");
            double approvedAmount = Double.parseDouble((String)duplicateApprovedAmt.get(key));
            String remarks;
            if(duplicateRemarks.get(key) != null && !duplicateRemarks.get(key).equals(""))
                remarks = (String)duplicateRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setApprovedAmount(approvedAmount);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
            	System.out.println("<br>application processing==9230===");
                ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
                int countDan = ((Integer)countAmount.get(0)).intValue();
                double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
                application.setCgpan((String)countAmount.get(2));
                if(countDan > 0)
                {
                    application.setApprovedAmount(approvedAmount - enhancedAmount);
                    application.setEnhancementAmount(approvedAmount - enhancedAmount);
                    appProcessor.generateDanForEnhance(application, user);
                }
                application.setStatus("AP");
                application.setApprovedAmount(approvedAmount);
                appProcessor.updateEnhanceAppStatus(application, userId);
            } else
            if(application.getCgpanReference() != null && !application.getCgpanReference().equals("") && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
            {
            	System.out.println("<br>application processing==9247===");
                Application testApplication = appProcessor.getPartApplication(null, application.getCgpanReference(), "");
                if(testApplication.getStatus().equals("EX"))
                {
                	System.out.println("<br>application processing==9251===");
                    String renewCgpan = application.getCgpanReference();
                    cgpan = appProcessor.generateRenewCgpan(renewCgpan);
                    application.setCgpan(cgpan);
                    application.setAdditionalTC(true);
                    application.setWcRenewal(true);
                    application.setStatus("AP");
                    appProcessor.updateApplicationsStatus(application, userId);
                    System.out.println("<br>application processing==9259===");
                }
                testApplication = null;
            } else
            {
            	System.out.println("<br>application processing==9264===");
                appProcessor.updateAppCgpanReference(application);
                cgpan = appProcessor.generateCgpan(application);
                application.setCgpan(cgpan);
                application.setStatus("AP");
                if(applicationNew.getStatus().equals("MO"))
                	
                    appProcessor.updateRejectedApplicationsStatus(application, userId);
                else
                	System.out.println("<br>application processing==9273===");
                    appProcessor.updateApplicationsStatus(application, userId);
                    System.out.println("<br>application processing==9274===");
            }
            dupApprovedApplications.add(application);
        } else
        if(status.equals("HO"))
        {
            String remarks;
            if(duplicateRemarks.get(key) != null && !duplicateRemarks.get(key).equals(""))
                remarks = (String)duplicateRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
                application.setStatus("EH");
            else
                application.setStatus(status);
            appProcessor.updateGeneralStatus(application, userId);
            System.out.println("<br>application processing==9293===");
            dupHoldApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been put on hold because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("RE"))
        {
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("duplicateRemarks.get(key):").append(duplicateRemarks.get(key)).toString());
            String remarks;
            if(duplicateRemarks.get(key) != null && !duplicateRemarks.get(key).equals(""))
                remarks = (String)duplicateRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
            {
                application.setStatus("ER");
                appProcessor.updateRejectStatus(application, userId);
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
                System.out.println("<br>application processing==9317===");
            }
            dupRejectedApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been rejected because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("PE"))
        {
            String remarks;
            if(duplicateRemarks.get(key) != null && !duplicateRemarks.get(key).equals(""))
                remarks = (String)duplicateRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
                application.setStatus("EP");
                appProcessor.updateGeneralStatus(application, userId);
                System.out.println("<br>application processing==9337===");
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
                System.out.println("<br>application processing==9342===");
            }
            dupPendingApplications.add(application);
        }
        application = null;
    }

    apForm.setDupApprovedApplications(dupApprovedApplications);
    apForm.setDupHoldApplications(dupHoldApplications);
    apForm.setDupRejectedApplications(dupRejectedApplications);
    apForm.setDupPendingApplications(dupPendingApplications);
    ArrayList ineligibleApprovedApplications = new ArrayList();
    ArrayList ineligibleHoldApplications = new ArrayList();
    ArrayList ineligibleRejectedApplications = new ArrayList();
    ArrayList ineligiblePendingApplications = new ArrayList();
    Map ineligibleAppRefNos = apForm.getIneligibleAppRefNo();
    Map ineligibleRemarks = apForm.getIneligibleRemarks();
    Map ineligibleStatus = apForm.getIneligibleStatus();
    Map ineligibleApprovedAmt = apForm.getIneligibleApprovedAmt();
    Set ineligibleAppRefNosSet = ineligibleAppRefNos.keySet();
    Set ineligibleRemarksSet = ineligibleRemarks.keySet();
    Set ineligibleStatusSet = ineligibleStatus.keySet();
    Set ineligibleApprovedAmtSet = ineligibleApprovedAmt.keySet();
    for(Iterator ineligibleAppRefNosIterator = ineligibleAppRefNosSet.iterator(); ineligibleAppRefNosIterator.hasNext();)
    {
        Application application = new Application();
        String key = (String)ineligibleAppRefNosIterator.next();
        String appRefNumber = (String)ineligibleAppRefNos.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("app ref no :").append(appRefNumber).toString());
        String status = (String)ineligibleStatus.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("status:").append(status).toString());
        String mliId = null;
        String cgpan = "";
        String cgbid = "";
        application = appProcessor.getApplication(mliId, cgpan, appRefNumber);
        String appStatus = application.getStatus();
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("appStatus:").append(appStatus).toString());
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("status:").append(status).toString());
        if(status.equals("AP"))
        {
        	System.out.println("<br>application processing==9382===");
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", "entering approved applications");
            double approvedAmount;
            if(ineligibleApprovedAmt.get(key) != null)
                approvedAmount = Double.parseDouble((String)ineligibleApprovedAmt.get(key));
            else
                approvedAmount = 0.0D;
            String remarks;
            if(ineligibleRemarks.get(key) != null && !ineligibleRemarks.get(key).equals(""))
                remarks = (String)ineligibleRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setApprovedAmount(approvedAmount);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
                ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
                int countDan = ((Integer)countAmount.get(0)).intValue();
                double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
                Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("countDan:").append(countDan).toString());
                Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("enhancedAmount:").append(enhancedAmount).toString());
                application.setCgpan((String)countAmount.get(2));
                if(countDan > 0)
                {
                    application.setApprovedAmount(approvedAmount - enhancedAmount);
                    appProcessor.generateDanForEnhance(application, user);
                }
                application.setStatus("AP");
                application.setApprovedAmount(approvedAmount);
                appProcessor.updateEnhanceAppStatus(application, userId);
                System.out.println("<br>application processing==9413===");
            } else
            if(application.getCgpanReference() != null && !application.getCgpanReference().equals("") && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
            {
                Application testApplication = appProcessor.getPartApplication(null, application.getCgpanReference(), "");
                if(testApplication.getStatus().equals("EX"))
                {
                    String renewCgpan = application.getCgpanReference();
                    cgpan = appProcessor.generateRenewCgpan(renewCgpan);
                    application.setCgpan(cgpan);
                    application.setAdditionalTC(true);
                    application.setWcRenewal(true);
                    application.setStatus("AP");
                    System.out.println("<br>application processing==9426===");
                    appProcessor.updateApplicationsStatus(application, userId);
                    System.out.println("<br>application processing==9428===");
                }
                testApplication = null;
            } else
            {
                appProcessor.updateAppCgpanReference(application);
                cgpan = appProcessor.generateCgpan(application);
                application.setCgpan(cgpan);
                application.setStatus("AP");
                System.out.println("<br>application processing==9437===");
                appProcessor.updateApplicationsStatus(application, userId);
                System.out.println("<br>application processing==9439===");
            }
            ineligibleApprovedApplications.add(application);
        } else
        if(status.equals("HO"))
        {
            String remarks;
            if(ineligibleRemarks.get(key) != null && !ineligibleRemarks.get(key).equals(""))
                remarks = (String)ineligibleRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
                application.setStatus("EH");
            else
                application.setStatus(status);
            appProcessor.updateGeneralStatus(application, userId);
            ineligibleHoldApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been put on hold beacuse ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("RE"))
        {
            String remarks;
            if(ineligibleRemarks.get(key) != null && !ineligibleRemarks.get(key).equals(""))
                remarks = (String)ineligibleRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
            {
                application.setStatus("ER");
                appProcessor.updateRejectStatus(application, userId);
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
            }
            ineligibleRejectedApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been rejected because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("PE"))
        {
            String remarks;
            if(ineligibleRemarks.get(key) != null && !ineligibleRemarks.get(key).equals(""))
                remarks = (String)ineligibleRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
                application.setStatus("EP");
                appProcessor.updateGeneralStatus(application, userId);
                System.out.println("<br>application processing==9498===");
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
            }
            ineligiblePendingApplications.add(application);
        }
        application = null;
    }

    apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);
    apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
    apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);
    apForm.setIneligiblePendingApplications(ineligiblePendingApplications);
    ArrayList ineligibleDupApprovedApplications = new ArrayList();
    ArrayList ineligibleDupHoldApplications = new ArrayList();
    ArrayList ineligibleDupRejectedApplications = new ArrayList();
    ArrayList ineligibleDupPendingApplications = new ArrayList();
    Map ineligibleDupAppRefNos = apForm.getIneligibleDupAppRefNo();
    Map ineligibleDupRemarks = apForm.getIneligibleDupRemarks();
    Map ineligibleDupStatus = apForm.getIneligibleDupStatus();
    Map ineligibleDupApprovedAmt = apForm.getIneligibleDupApprovedAmt();
    Set ineligibleDupAppRefNosSet = ineligibleDupAppRefNos.keySet();
    Set ineligibleDupRemarksSet = ineligibleDupRemarks.keySet();
    Set ineligibleDupStatusSet = ineligibleDupStatus.keySet();
    Set ineligibleDupApprovedAmtSet = ineligibleDupApprovedAmt.keySet();
    for(Iterator ineligibleDupAppRefNosIterator = ineligibleDupAppRefNosSet.iterator(); ineligibleDupAppRefNosIterator.hasNext();)
    {
        Application application = new Application();
        String key = (String)ineligibleDupAppRefNosIterator.next();
        String appRefNumber = (String)ineligibleDupAppRefNos.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("app ref no :").append(appRefNumber).toString());
        String status = (String)ineligibleDupStatus.get(key);
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("status:").append(status).toString());
        String mliId = null;
        String cgpan = "";
        String cgbid = "";
        application = appProcessor.getApplication(mliId, cgpan, appRefNumber);
        String appStatus = application.getStatus();
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("appStatus:").append(appStatus).toString());
        Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("status:").append(status).toString());
        if(status.equals("AP"))
        {
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", "entering approved applications");
            double approvedAmount;
            if(ineligibleDupApprovedAmt.get(key) != null)
                approvedAmount = Double.parseDouble((String)ineligibleDupApprovedAmt.get(key));
            else
                approvedAmount = 0.0D;
            String remarks;
            if(ineligibleDupRemarks.get(key) != null && !ineligibleDupRemarks.get(key).equals(""))
                remarks = (String)ineligibleDupRemarks.get(key);
            else
                remarks = "";
            Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("approved amount:").append(approvedAmount).toString());
            application.setAppRefNo(appRefNumber);
            application.setApprovedAmount(approvedAmount);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
                ArrayList countAmount = appProcessor.getCountForDanGen(appRefNumber);
                int countDan = ((Integer)countAmount.get(0)).intValue();
                double enhancedAmount = ((Double)countAmount.get(1)).doubleValue();
                Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("countDan:").append(countDan).toString());
                Log.log(4, "ApplicationProcessingAction", "afterApprovalApps", (new StringBuilder()).append("enhancedAmount:").append(enhancedAmount).toString());
                application.setCgpan((String)countAmount.get(2));
                if(countDan > 0)
                {
                    application.setApprovedAmount(approvedAmount - enhancedAmount);
                    appProcessor.generateDanForEnhance(application, user);
                }
                application.setStatus("AP");
                application.setApprovedAmount(approvedAmount);
                appProcessor.updateEnhanceAppStatus(application, userId);
                System.out.println("<br>application processing==9573===");
            } else
            if(application.getCgpanReference() != null && !application.getCgpanReference().equals("") && application.getCgpanReference().startsWith("CG") && application.getLoanType().equals("WC"))
            {
                Application testApplication = appProcessor.getPartApplication(null, application.getCgpanReference(), "");
                if(testApplication.getStatus().equals("EX"))
                {
                    String renewCgpan = application.getCgpanReference();
                    cgpan = appProcessor.generateRenewCgpan(renewCgpan);
                    application.setCgpan(cgpan);
                    application.setAdditionalTC(true);
                    application.setWcRenewal(true);
                    application.setStatus("AP");
                    System.out.println("<br>application processing==9586===");
                    appProcessor.updateApplicationsStatus(application, userId);
                    System.out.println("<br>application processing==9588===");
                }
                testApplication = null;
            } else
            {
                appProcessor.updateAppCgpanReference(application);
                cgpan = appProcessor.generateCgpan(application);
                application.setCgpan(cgpan);
                application.setStatus("AP");
                System.out.println("<br>application processing==9597===");
                appProcessor.updateApplicationsStatus(application, userId);
                System.out.println("<br>application processing==9599===");
            }
            ineligibleDupApprovedApplications.add(application);
        } else
        if(status.equals("HO"))
        {
            String remarks;
            if(ineligibleDupRemarks.get(key) != null && !ineligibleDupRemarks.get(key).equals(""))
                remarks = (String)ineligibleDupRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
                application.setStatus("EH");
            else
                application.setStatus(status);
            System.out.println("<br>application processing==9616===");
            appProcessor.updateGeneralStatus(application, userId);
            System.out.println("<br>application processing==9616===");
            ineligibleDupHoldApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been put on hold because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("RE"))
        {
            String remarks;
            if(ineligibleDupRemarks.get(key) != null && !ineligibleDupRemarks.get(key).equals(""))
                remarks = (String)ineligibleDupRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EH") || application.getStatus().equals("EP"))
            {
                application.setStatus("ER");
                appProcessor.updateRejectStatus(application, userId);
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
            }
            ineligibleDupRejectedApplications.add(application);
            message = (new StringBuilder()).append("The Application Reference No. :").append(appRefNumber).append(" has been been rejected because ").append(remarks).toString();
            subject = (new StringBuilder()).append("Status of Application Reference No. :").append(appRefNumber).toString();
            sendMailEmail(message, application.getMliID(), user, subject);
        } else
        if(status.equals("PE"))
        {
        	System.out.println("<br>application processing==9649===");
            String remarks;
            if(ineligibleDupRemarks.get(key) != null && !ineligibleDupRemarks.get(key).equals(""))
                remarks = (String)ineligibleDupRemarks.get(key);
            else
                remarks = "";
            application.setAppRefNo(appRefNumber);
            application.setRemarks(remarks);
            if(application.getStatus().equals("EN") || application.getStatus().equals("EP") || application.getStatus().equals("EH"))
            {
                application.setStatus("EP");
                appProcessor.updateGeneralStatus(application, userId);
                System.out.println("<br>application processing==9661===");
            } else
            {
                application.setStatus(status);
                appProcessor.updatePendingRejectedStatus(application, userId);
            }
            ineligibleDupPendingApplications.add(application);
        }
        application = null;
    }

    apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);
    apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
    apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
    apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);
    return mapping.findForward("afterApprovalPage");
}*/

	
	public ActionForward afterApprovalAppsold(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "afterApprovalApps",
				"Entered");

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Application applicationNew = new Application();
		// RpProcessor rpProcessor=new RpProcessor();
		// RpHelper rpHelper = new RpHelper() ;
		// HttpSession applicationSession=request.getSession(false);
		String message = "";
		String subject = "";
		User user = getUserInformation(request);
		String userId = user.getUserId();
		APForm apForm = (APForm) form;
		// Clear Applications
		ArrayList clearApprovedApplications = new ArrayList();
		ArrayList clearHoldApplications = new ArrayList();
		ArrayList clearRejectedApplications = new ArrayList();
		ArrayList clearPendingApplications = new ArrayList();

		Map clearAppRefNos = apForm.getClearAppRefNo();
		Map duplicateAppRefNos = apForm.getDuplicateAppRefNo();
		Map ineligibleAppRefNos = apForm.getIneligibleAppRefNo();
		Map ineligibleDupAppRefNos = apForm.getIneligibleDupAppRefNo();

		Map clearRemarks = apForm.getClearRemarks();
		Map clearStatus = apForm.getClearStatus();
		Map clearApprovedAmt = apForm.getClearApprovedAmt();
		Map clearRsfApprovedAmt = apForm.getClearRsfApprovedAmt();
		Set clearAppRefNosSet = clearAppRefNos.keySet();
		Set clearRemarksSet = clearRemarks.keySet();
		Set clearStatusSet = clearStatus.keySet();
		Set clearApprovedAmtSet = clearApprovedAmt.keySet();
		Set clearRsfApprovedAmtSet = clearRsfApprovedAmt.keySet();
		Iterator clearAppRefNosIterator = clearAppRefNosSet.iterator();
		/*
		 * Iterator clearRemarksIterator=clearRemarksSet.iterator(); Iterator
		 * clearStatusIterator=clearStatusSet.iterator(); Iterator
		 * clearApprovedAmtIterator=clearApprovedAmtSet.iterator();
		 */
		while (clearAppRefNosIterator.hasNext()) {
			Application application = new Application();

			String key = (String) clearAppRefNosIterator.next();

			String appRefNumber = (String) clearAppRefNos.get(key);
			// System.out.println("app ref no :" + appRefNumber);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "app ref no :" + appRefNumber);
			String approvedStatus = (String) clearStatus.get(key);
			// System.out.println("status:" + approvedStatus);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "status:" + approvedStatus);

			double approvedAmount;
			String remarks;

			String mliId = null;
			String cgpan = "";
			String cgbid = "";
			application = appProcessor.getApplication(mliId, cgpan,
					appRefNumber);

			applicationNew = applicationDAO
					.getAppForAppRef(mliId, appRefNumber);

			Date sancDate1 = application.getSanctionedDate();
			Date sancDate2 = applicationNew.getSanctionedDate();

			if (sancDate1 == null && sancDate2 != null) {
				application.setSanctionedDate(sancDate2);
			}

			/**
			 * This loop calculates the guarantee fee,cgpan and cgbid for
			 * approved applications
			 */
			if (approvedStatus
					.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
				System.out.println("clearAppRefNosIterator AP");
				System.out.println("<br>application processing==9764===");
				// application=new Application();
				// System.out.println("afterApprovalApps"+"entering approved applications");
				// System.out.println("Scheme"+application.getScheme());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "entering approved applications");
				/*
				 * if((application.getScheme()).equals("RSF") ||
				 * application.getScheme()=="RSF") {
				 * approvedAmount=Double.parseDouble
				 * ((String)clearRsfApprovedAmt.get(key)); } else{
				 * approvedAmount
				 * =Double.parseDouble((String)clearApprovedAmt.get(key)); }
				 */
				approvedAmount = Double.parseDouble((String) clearApprovedAmt
						.get(key));
				// System.out.println("approved amount:" + approvedAmount);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "approved amount:"
								+ approvedAmount);

				if (clearRemarks.get(key) != null
						&& !(clearRemarks.get(key).equals(""))) {
					remarks = (String) clearRemarks.get(key);
					// System.out.println("remarks:"+remarks);

				} else {

					remarks = "";
				}

				// generation of CGPAN
				application.setAppRefNo(appRefNumber);
				// System.out.println("App RefNo:"+appRefNumber);

				application.setApprovedAmount(approvedAmount);
				// System.out.println(application.getApprovedAmount());
				application.setRemarks(remarks);

				application.setStatus(approvedStatus);

				// System.out.println(application.getAppRefNo());
				// System.out.println(application.getRemarks());
				// System.out.println(application.getStatus());
				// System.out.println("CGPAN Reference:"+application.getCgpanReference());
				if ((application.getCgpanReference() != null && !application
						.getCgpanReference().equals(""))
						&& application.getCgpanReference().startsWith("CG")
						&& application.getLoanType().equals("WC")) {
					Application testApplication = appProcessor
							.getPartApplication(null,
									application.getCgpanReference(), "");
					if (testApplication.getStatus().equals("EX")) {
						String renewCgpan = application.getCgpanReference();
						// System.out.println("renewCgpan:"+renewCgpan);

						cgpan = appProcessor.generateRenewCgpan(renewCgpan);
						// System.out.println("cgpan:"+cgpan);

						application.setCgpan(cgpan);

						application.setAdditionalTC(true);
						application.setWcRenewal(true);

						int applicationRefNoCount = applicationDAO
								.getAppRefNoCount(application.getAppRefNo());
						// System.out.println("Test applicationRefNoCount:"+applicationRefNoCount);
						// System.out.println("Test applicationNew.getStatus():"+applicationNew.getStatus());

						// update application status

						if (applicationNew.getStatus().equals("MO")
								&& applicationRefNoCount >= 1) {
							System.out.println("clearAppRefNosIterator MO");
							// System.out.println("Modified Application 7872 -App ref No:"+application.getAppRefNo()+
							// " approvedStatus:"+approvedStatus);
							appProcessor.updateRejectedApplicationsStatus(
									application, userId);
						} else {
							appProcessor.updateApplicationsStatus(application,
									userId);
							System.out.println("<br>application processing==9845===");
						}

					}
					testApplication = null;

				} else {

					appProcessor.updateAppCgpanReference(application);

					cgpan = appProcessor.generateCgpan(application);

					application.setCgpan(cgpan);
					System.out.println("Cgpan:" + cgpan);
					// System.out.println("Application Status:"+applicationNew.getStatus());
					int applicationRefNoCount = applicationDAO
							.getAppRefNoCount(application.getAppRefNo());
					// System.out.println("applicationRefNoCount:"+applicationRefNoCount);
					if (applicationNew.getStatus().equals("MO")
							&& applicationRefNoCount >= 1) {
						// System.out.println("Modified Application 7872 -App ref No:"+application.getAppRefNo()+
						// " approvedStatus:"+approvedStatus);
						appProcessor.updateRejectedApplicationsStatus(
								application, userId);
					} else {
						appProcessor.updateApplicationsStatus(application,
								userId);
						System.out.println("<br>application processing==9872===");
					}

				}

				// update application status

				/*
				 * appProcessor.updateCgpan(application);
				 * 
				 * 
				 * //generate cgbid int
				 * ssiRefNo=application.getBorrowerDetails()
				 * .getSsiDetails().getBorrowerRefNo(); cgbid =
				 * appProcessor.generateCgbid(ssiRefNo); if (cgbid!=null &&
				 * !(cgbid.equals(""))) {
				 * application.getBorrowerDetails().getSsiDetails
				 * ().setCgbid(cgbid);
				 * 
				 * //updation of cgbid appProcessor.updateCgbid(ssiRefNo,cgbid);
				 * 
				 * }
				 */clearApprovedApplications.add(application);

			} else if (approvedStatus
					.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {

				// application=new Application();

				if (clearRemarks.get(key) != null
						&& !(clearRemarks.get(key).equals(""))) {
					remarks = (String) clearRemarks.get(key);
					// System.out.println("remarks1:"+remarks);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				application.setStatus(approvedStatus);
				// System.out.println(application.getAppRefNo());
				// System.out.println(application.getRemarks());
				// System.out.println(application.getStatus());
				appProcessor.updateGeneralStatus(application, userId);

				clearHoldApplications.add(application);
				// apForm.setClearHoldApplications(clearHoldApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been put on hold because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;
				// System.out.println("Rejected Application MLI Id :"+application.getMliID());
				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (approvedStatus
					.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {

				// application=new Application();

				if (clearRemarks.get(key) != null
						&& !(clearRemarks.get(key).equals(""))) {
					remarks = (String) clearRemarks.get(key);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				application.setStatus(approvedStatus);

				appProcessor.updatePendingRejectedStatus(application, userId);

				// appProcessor.updateGeneralStatus(application,userId);

				clearRejectedApplications.add(application);
				// apForm.setClearRejectedApplications(clearRejectedApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been rejected because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (approvedStatus
					.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)) {

				// application=new Application();

				if (clearRemarks.get(key) != null
						&& !(clearRemarks.get(key).equals(""))) {
					remarks = (String) clearRemarks.get(key);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				application.setStatus(approvedStatus);
				// System.out.println(application.getAppRefNo());
				// System.out.println(application.getRemarks());
				// System.out.println(application.getStatus());
				appProcessor.updatePendingRejectedStatus(application, userId);
				// appProcessor.updateGeneralStatus(application,userId);

				clearPendingApplications.add(application);
				// apForm.setClearPendingApplications(clearPendingApplications);

			}
			//System.out.println("application approved::" + appRefNumber);
			application = null;
			break;
		}
		apForm.setClearApprovedApplications(clearApprovedApplications);
		apForm.setClearHoldApplications(clearHoldApplications);
		apForm.setClearRejectedApplications(clearRejectedApplications);
		apForm.setClearPendingApplications(clearPendingApplications);

		// Probable Duplicates Applications

		// Array lists for the display
		ArrayList dupApprovedApplications = new ArrayList();
		ArrayList dupHoldApplications = new ArrayList();
		ArrayList dupRejectedApplications = new ArrayList();
		ArrayList dupPendingApplications = new ArrayList();

		// Map duplicateAppRefNos=apForm.getDuplicateAppRefNo();
		Map duplicateRemarks = apForm.getDuplicateRemarks();
		Map duplicateStatus = apForm.getDuplicateStatus();
		Map duplicateApprovedAmt = apForm.getDuplicateApprovedAmt();

		Set duplicateAppRefNosSet = duplicateAppRefNos.keySet();
		Set duplicateRemarksSet = duplicateRemarks.keySet();
		Set duplicateStatusSet = duplicateStatus.keySet();
		Set duplicateApprovedAmtSet = duplicateApprovedAmt.keySet();

		Iterator duplicateAppRefNosIterator = duplicateAppRefNosSet.iterator();
		/*
		 * Iterator duplicateRemarksIterator=duplicateRemarksSet.iterator();
		 * Iterator duplicateStatusIterator=duplicateStatusSet.iterator();
		 * Iterator
		 * duplicateApprovedAmtIterator=duplicateApprovedAmtSet.iterator();
		 */
		while (duplicateAppRefNosIterator.hasNext()) {
			Application application = new Application();

			String key = (String) duplicateAppRefNosIterator.next();

			String appRefNumber = (String) duplicateAppRefNos.get(key);
			// System.out.println("app ref no :" + appRefNumber);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "app ref no :" + appRefNumber);
			String status = (String) duplicateStatus.get(key);
			// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","status:"
			// + status);
			// String
			// remarks=(String)duplicateRemarks.get(duplicateRemarksIterator.next());
			// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved remarks:"
			// + remarks);
			// System.out.println("remarks:" + status);
			double approvedAmount;
			String remarks;

			String mliId = null;
			String cgpan = "";
			String cgbid = "";
			application = appProcessor.getApplication(mliId, cgpan,
					appRefNumber);
			System.out.println("<br>application processing==10049===");
			// applicationNew=applicationDAO.getAppForAppRef(mliId,appRefNumber);
			//
			// Date sancDate1 = application.getSanctionedDate();
			// Date sancDate2 = applicationNew.getSanctionedDate();
			//
			// if(sancDate1 == null && sancDate2 != null){
			// application.setSanctionedDate(sancDate2);
			// }

			String appStatus = application.getStatus();
			// System.out.println("approved remarks:" + appStatus);
			// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","appStatus:"
			// + appStatus);

			/**
			 * This loop calculates generates cgpan and cgbid for approved
			 * applications
			 */
			if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
				System.out.println("duplicateAppRefNosIterator AP");
				System.out.println("<br>application processing==10070===");
				// application=new Application();
				// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","entering approved applications");

				approvedAmount = Double
						.parseDouble((String) duplicateApprovedAmt.get(key));

				if (duplicateRemarks.get(key) != null
						&& !(duplicateRemarks.get(key).equals(""))) {
					remarks = (String) duplicateRemarks.get(key);
					// System.out.println("remarks:"+remarks);
				} else {

					remarks = "";
				}

				// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","approved amount:"
				// + approvedAmount);

				application.setAppRefNo(appRefNumber);
				application.setApprovedAmount(approvedAmount);
				application.setRemarks(remarks);
				// System.out.println(application.getAppRefNo());
				// System.out.println(application.getApprovedAmount());
				// System.out.println(application.getRemarks());
				// System.out.println(application.getStatus());

				/**
				 * Approval for Enhancement Applications
				 */

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
				//	System.out.println("enhancement app entered -- EN");
					ArrayList countAmount = appProcessor
							.getCountForDanGen(appRefNumber);
					int countDan = ((Integer) countAmount.get(0)).intValue();
					// System.out.println("countDan:"+countDan);
					double enhancedAmount = ((Double) countAmount.get(1))
							.doubleValue();// application_detail
					// System.out.println("enhancedAmount11:" + enhancedAmount);
					// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","countDan:"
					// + countDan);
					// Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps","enhancedAmount:"
					// + enhancedAmount);
					application.setCgpan((String) countAmount.get(2));
					// System.out.println("cgpan:"+(String) countAmount.get(2));
					// User user=getUserInformation(request);
					// String userId=user.getUserId();
					if (countDan > 0) {
						
						// System.out.println("approvedAmount:" + approvedAmount);
						 
						// System.out.println("enhancedAmount:" + enhancedAmount);
						application.setApprovedAmount(approvedAmount
								- enhancedAmount);

						application.setEnhancementAmount(approvedAmount
								- enhancedAmount);
						//System.out.println("sanction date ::"
							//	+ application.getSanctionedDate());
						// if(application.getSanctionedDate() != null){
						// System.out.println("going to ganarate dan for enhance");
						appProcessor.generateDanForEnhance(application, user);
						// }else{
						// continue;
						// }

					}

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
					// System.out.println("application.setStatus():"+application.getStatus());
					application.setApprovedAmount(approvedAmount);
					// System.out.println(application.getApprovedAmount());
					// if(application.getSanctionedDate() != null)
					appProcessor.updateEnhanceAppStatus(application, userId);
					System.out.println("<br>application processing==10148===");

				}
				/**
				 * Approval for Renewal Applications
				 */
				else if ((application.getCgpanReference() != null && !application
						.getCgpanReference().equals(""))
						&& application.getCgpanReference().startsWith("CG")
						&& application.getLoanType().equals("WC")) {
					Application testApplication = appProcessor
							.getPartApplication(null,
									application.getCgpanReference(), "");
					if (testApplication.getStatus().equals("EX")) {
						String renewCgpan = application.getCgpanReference();

						cgpan = appProcessor.generateRenewCgpan(renewCgpan);

						application.setCgpan(cgpan);

						application.setAdditionalTC(true);
						application.setWcRenewal(true);

						// update application status

						application
								.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

						appProcessor.updateApplicationsStatus(application,
								userId);

					}
					testApplication = null;

				} else {

					appProcessor.updateAppCgpanReference(application);

					cgpan = appProcessor.generateCgpan(application);

					application.setCgpan(cgpan);

					// System.out.println("CGPAN:"+cgpan);

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

					if (applicationNew.getStatus().equals("MO")) {
					//	System.out.println("duplicateAppRefNosIterator MO");
						appProcessor.updateRejectedApplicationsStatus(
								application, userId);
					} else {
						appProcessor.updateApplicationsStatus(application,
								userId);
						System.out.println("<br>application processing==10202===");
					}

				}

				dupApprovedApplications.add(application);
				// apForm.setDupApprovedApplications(dupApprovedApplications);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {

				// application=new Application();

				if (duplicateRemarks.get(key) != null
						&& !(duplicateRemarks.get(key).equals(""))) {
					remarks = (String) duplicateRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("EH");

				} else {

					application.setStatus(status);
				}

				appProcessor.updateGeneralStatus(application, userId);

				dupHoldApplications.add(application);
				// apForm.setDupHoldApplications(dupHoldApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been put on hold because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {

				// application=new Application();

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "duplicateRemarks.get(key):"
								+ duplicateRemarks.get(key));

				if (duplicateRemarks.get(key) != null
						&& !(duplicateRemarks.get(key).equals(""))) {
					remarks = (String) duplicateRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("ER");
					appProcessor.updateRejectStatus(application, userId);

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
					System.out.println("<br>application processing==10280===");
				}

				// appProcessor.updateGeneralStatus(application,userId);

				dupRejectedApplications.add(application);
				// apForm.setDupRejectedApplications(dupRejectedApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been rejected because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)) {

				// application=new Application();
				System.out.println("<br>application processing==10300===");
				if (duplicateRemarks.get(key) != null
						&& !(duplicateRemarks.get(key).equals(""))) {
					remarks = (String) duplicateRemarks.get(key);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
					application.setStatus("EP");
					appProcessor.updateGeneralStatus(application, userId);
					System.out.println("<br>application processing==10318===");

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
				}

				// appProcessor.updateGeneralStatus(application,userId);

				dupPendingApplications.add(application);
				// apForm.setDupPendingApplications(dupPendingApplications);

			}
			System.out.println("application approved::" + appRefNumber);
			application = null;
		}
		apForm.setDupApprovedApplications(dupApprovedApplications);
		apForm.setDupHoldApplications(dupHoldApplications);
		apForm.setDupRejectedApplications(dupRejectedApplications);
		apForm.setDupPendingApplications(dupPendingApplications);

		// Probable Ineligible Applications

		ArrayList ineligibleApprovedApplications = new ArrayList();
		ArrayList ineligibleHoldApplications = new ArrayList();
		ArrayList ineligibleRejectedApplications = new ArrayList();
		ArrayList ineligiblePendingApplications = new ArrayList();

		// Map ineligibleAppRefNos=apForm.getIneligibleAppRefNo();
		Map ineligibleRemarks = apForm.getIneligibleRemarks();
		Map ineligibleStatus = apForm.getIneligibleStatus();
		Map ineligibleApprovedAmt = apForm.getIneligibleApprovedAmt();

		Set ineligibleAppRefNosSet = ineligibleAppRefNos.keySet();
		Set ineligibleRemarksSet = ineligibleRemarks.keySet();
		Set ineligibleStatusSet = ineligibleStatus.keySet();
		Set ineligibleApprovedAmtSet = ineligibleApprovedAmt.keySet();

		Iterator ineligibleAppRefNosIterator = ineligibleAppRefNosSet
				.iterator();
		/*
		 * Iterator ineligibleRemarksIterator=ineligibleRemarksSet.iterator();
		 * Iterator ineligibleStatusIterator=ineligibleStatusSet.iterator();
		 * Iterator
		 * ineligibleApprovedAmtIterator=ineligibleApprovedAmtSet.iterator();
		 */
		while (ineligibleAppRefNosIterator.hasNext()) {
			Application application = new Application();

			String key = (String) ineligibleAppRefNosIterator.next();

			String appRefNumber = (String) ineligibleAppRefNos.get(key);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "app ref no :" + appRefNumber);
			String status = (String) ineligibleStatus.get(key);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "status:" + status);
			/*
			 * String
			 * remarks=(String)ineligibleRemarks.get(ineligibleRemarksIterator
			 * .next());
			 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
			 * ,"approved remarks:" + remarks);
			 */
			double approvedAmount;
			String remarks;

			String mliId = null;
			String cgpan = "";
			String cgbid = "";
			application = appProcessor.getApplication(mliId, cgpan,
					appRefNumber);

			// added on 20-09-2013 to get sanction date
			// applicationNew=applicationDAO.getAppForAppRef(mliId,appRefNumber);
			//
			// Date sancDate1 = application.getSanctionedDate();
			// Date sancDate2 = applicationNew.getSanctionedDate();
			//
			// if(sancDate1 == null && sancDate2 != null){
			// application.setSanctionedDate(sancDate2);
			// }

			String appStatus = application.getStatus();
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "appStatus:" + appStatus);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "status:" + status);

			/**
			 * This loop calculates the guarantee fee,cgpan and cgbid for
			 * approved applications
			 */
			if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
				//System.out.println("ineligibleAppRefNosIterator AP");
				// application=new Application();
				System.out.println("<br>application processing==10416===");
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "entering approved applications");

				if (ineligibleApprovedAmt.get(key) != null) {
					approvedAmount = Double
							.parseDouble((String) ineligibleApprovedAmt
									.get(key));

				} else {

					approvedAmount = 0;

				}
				if (ineligibleRemarks.get(key) != null
						&& !(ineligibleRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleRemarks.get(key);
				} else {

					remarks = "";
				}

				// generation of CGPAN
				application.setAppRefNo(appRefNumber);
				application.setApprovedAmount(approvedAmount);
				application.setRemarks(remarks);

				/**
				 * Approval for Enhancement Applications
				 */

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
				//	System.out.println("ineligibleAppRefNosIterator AP--EN");
					ArrayList countAmount = appProcessor
							.getCountForDanGen(appRefNumber);
					int countDan = ((Integer) countAmount.get(0)).intValue();
					double enhancedAmount = ((Double) countAmount.get(1))
							.doubleValue();

					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterApprovalApps", "countDan:" + countDan);
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterApprovalApps", "enhancedAmount:"
									+ enhancedAmount);
					application.setCgpan((String) countAmount.get(2));

					if (countDan > 0) {
						application.setApprovedAmount(approvedAmount
								- enhancedAmount);
						// if(application.getSanctionedDate() != null){
						appProcessor.generateDanForEnhance(application, user);
						// }else{
						// continue;
						// }

					}

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

					application.setApprovedAmount(approvedAmount);

					appProcessor.updateEnhanceAppStatus(application, userId);

				}
				/**
				 * Approval for Renewal Applications
				 */
				else if ((application.getCgpanReference() != null && !application
						.getCgpanReference().equals(""))
						&& application.getCgpanReference().startsWith("CG")
						&& application.getLoanType().equals("WC")) {
					Application testApplication = appProcessor
							.getPartApplication(null,
									application.getCgpanReference(), "");
					if (testApplication.getStatus().equals("EX")) {
						String renewCgpan = application.getCgpanReference();

						cgpan = appProcessor.generateRenewCgpan(renewCgpan);

						application.setCgpan(cgpan);

						application.setAdditionalTC(true);
						application.setWcRenewal(true);

						// update application status
						application
								.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

						appProcessor.updateApplicationsStatus(application,
								userId);
						System.out.println("<br>application processing==10509===");

					}
					testApplication = null;

				} else {
					appProcessor.updateAppCgpanReference(application);

					cgpan = appProcessor.generateCgpan(application);

					application.setCgpan(cgpan);

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

					appProcessor.updateApplicationsStatus(application, userId);
					System.out.println("<br>application processing==10525===");

				}
				ineligibleApprovedApplications.add(application);
				// apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {

				// application=new Application();

				if (ineligibleRemarks.get(key) != null
						&& !(ineligibleRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("EH");

				} else {

					application.setStatus(status);
				}

				// application.setStatus(status);

				appProcessor.updateGeneralStatus(application, userId);
				System.out.println("<br>application processing==10560===");

				ineligibleHoldApplications.add(application);
				// apForm.setIneligibleHoldApplications(ineligibleHoldApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been put on hold beacuse " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			}

			else if (status
					.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {

				// application=new Application();

				if (ineligibleRemarks.get(key) != null
						&& !(ineligibleRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("ER");
					appProcessor.updateRejectStatus(application, userId);

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
					System.out.println("<br>application processing==10601===");
				}
				// appProcessor.updateGeneralStatus(application,userId);

				ineligibleRejectedApplications.add(application);
				// apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been rejected because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)) {

				// application=new Application();

				if (ineligibleRemarks.get(key) != null
						&& !(ineligibleRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleRemarks.get(key);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
					application.setStatus("EP");
					appProcessor.updateGeneralStatus(application, userId);
					System.out.println("<br>application processing==10637===");

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
				}
				// appProcessor.updateGeneralStatus(application,userId);

				ineligiblePendingApplications.add(application);
				// apForm.setIneligiblePendingApplications(ineligiblePendingApplications);

			}
		//	System.out.println("application approved::" + appRefNumber);
			application = null;
		}
		apForm.setIneligibleApprovedApplications(ineligibleApprovedApplications);
		apForm.setIneligibleHoldApplications(ineligibleHoldApplications);
		apForm.setIneligibleRejectedApplications(ineligibleRejectedApplications);
		apForm.setIneligiblePendingApplications(ineligiblePendingApplications);

		// Ineligible Duplicate Applications
		ArrayList ineligibleDupApprovedApplications = new ArrayList();
		ArrayList ineligibleDupHoldApplications = new ArrayList();
		ArrayList ineligibleDupRejectedApplications = new ArrayList();
		ArrayList ineligibleDupPendingApplications = new ArrayList();

		// Map ineligibleDupAppRefNos=apForm.getIneligibleDupAppRefNo();
		Map ineligibleDupRemarks = apForm.getIneligibleDupRemarks();
		Map ineligibleDupStatus = apForm.getIneligibleDupStatus();
		Map ineligibleDupApprovedAmt = apForm.getIneligibleDupApprovedAmt();

		Set ineligibleDupAppRefNosSet = ineligibleDupAppRefNos.keySet();
		Set ineligibleDupRemarksSet = ineligibleDupRemarks.keySet();
		Set ineligibleDupStatusSet = ineligibleDupStatus.keySet();
		Set ineligibleDupApprovedAmtSet = ineligibleDupApprovedAmt.keySet();

		Iterator ineligibleDupAppRefNosIterator = ineligibleDupAppRefNosSet
				.iterator();
		/*
		 * Iterator
		 * ineligibleDupRemarksIterator=ineligibleDupRemarksSet.iterator();
		 * Iterator
		 * ineligibleDupStatusIterator=ineligibleDupStatusSet.iterator();
		 * Iterator
		 * ineligibleDupApprovedAmtIterator=ineligibleDupApprovedAmtSet.
		 * iterator();
		 */
		while (ineligibleDupAppRefNosIterator.hasNext()) {
			Application application = new Application();

			String key = (String) ineligibleDupAppRefNosIterator.next();

			String appRefNumber = (String) ineligibleDupAppRefNos.get(key);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "app ref no :" + appRefNumber);
			String status = (String) ineligibleDupStatus.get(key);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "status:" + status);
			/*
			 * String
			 * remarks=(String)ineligibleDupRemarks.get(ineligibleDupRemarksIterator
			 * .next());
			 * Log.log(Log.INFO,"ApplicationProcessingAction","afterApprovalApps"
			 * ,"approved remarks:" + remarks);
			 */
			double approvedAmount;
			String remarks;

			String mliId = null;
			String cgpan = "";
			String cgbid = "";
			application = appProcessor.getApplication(mliId, cgpan,
					appRefNumber);

			// added on 20-09-2013 to get sanction date
			// applicationNew=applicationDAO.getAppForAppRef(mliId,appRefNumber);
			//
			// Date sancDate1 = application.getSanctionedDate();
			// Date sancDate2 = applicationNew.getSanctionedDate();
			//
			// if(sancDate1 == null && sancDate2 != null){
			// application.setSanctionedDate(sancDate2);
			// }

			String appStatus = application.getStatus();
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "appStatus:" + appStatus);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterApprovalApps", "status:" + status);

			/**
			 * This loop calculates the guarantee fee,cgpan and cgbid for
			 * approved applications
			 */
			if (status.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
				System.out.println("ineligibleDupAppRefNosIterator AP");
				// application=new Application();

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "entering approved applications");

				if (ineligibleDupApprovedAmt.get(key) != null) {
					approvedAmount = Double
							.parseDouble((String) ineligibleDupApprovedAmt
									.get(key));

				} else {

					approvedAmount = 0;

				}
				if (ineligibleDupRemarks.get(key) != null
						&& !(ineligibleDupRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleDupRemarks.get(key);
				} else {

					remarks = "";
				}

				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterApprovalApps", "approved amount:"
								+ approvedAmount);

				// generation of CGPAN
				application.setAppRefNo(appRefNumber);
				application.setApprovedAmount(approvedAmount);
				application.setRemarks(remarks);

				/**
				 * Approval for Enhancement Applications
				 */

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
				//	System.out.println("ineligibleDupAppRefNosIterator AP-EN");
					ArrayList countAmount = appProcessor
							.getCountForDanGen(appRefNumber);
					int countDan = ((Integer) countAmount.get(0)).intValue();
					double enhancedAmount = ((Double) countAmount.get(1))
							.doubleValue();

					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterApprovalApps", "countDan:" + countDan);
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterApprovalApps", "enhancedAmount:"
									+ enhancedAmount);
					application.setCgpan((String) countAmount.get(2));

					if (countDan > 0) {
						application.setApprovedAmount(approvedAmount
								- enhancedAmount);
						// if(application.getSanctionedDate() != null){
						appProcessor.generateDanForEnhance(application, user);
						// }else{
						// continue;
						// }

					}

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

					application.setApprovedAmount(approvedAmount);

					appProcessor.updateEnhanceAppStatus(application, userId);

				}
				/**
				 * Approval for Renewal Applications
				 */
				else if ((application.getCgpanReference() != null && !application
						.getCgpanReference().equals(""))
						&& application.getCgpanReference().startsWith("CG")
						&& application.getLoanType().equals("WC")) {
					Application testApplication = appProcessor
							.getPartApplication(null,
									application.getCgpanReference(), "");
					if (testApplication.getStatus().equals("EX")) {
						String renewCgpan = application.getCgpanReference();

						cgpan = appProcessor.generateRenewCgpan(renewCgpan);

						application.setCgpan(cgpan);

						application.setAdditionalTC(true);
						application.setWcRenewal(true);

						// update application status
						application
								.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

						appProcessor.updateApplicationsStatus(application,
								userId);

					}
					testApplication = null;

				} else {

					appProcessor.updateAppCgpanReference(application);

					cgpan = appProcessor.generateCgpan(application);

					application.setCgpan(cgpan);

					application
							.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);

					appProcessor.updateApplicationsStatus(application, userId);

				}
				ineligibleDupApprovedApplications.add(application);
				// apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {

				// application=new Application();

				if (ineligibleDupRemarks.get(key) != null
						&& !(ineligibleDupRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleDupRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);

				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("EH");

				} else {

					application.setStatus(status);
				}

				appProcessor.updateGeneralStatus(application, userId);

				ineligibleDupHoldApplications.add(application);
				// apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been put on hold because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {

				// application=new Application();

				if (ineligibleDupRemarks.get(key) != null
						&& !(ineligibleDupRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleDupRemarks.get(key);
				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EH")
						|| application.getStatus().equals("EP")) {
					application.setStatus("ER");
					appProcessor.updateRejectStatus(application, userId);

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
				}
				// appProcessor.updateGeneralStatus(application,userId);

				ineligibleDupRejectedApplications.add(application);
				// apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);

				message = "The Application Reference No. :" + appRefNumber
						+ " has been been rejected because " + remarks;

				subject = "Status of Application Reference No. :"
						+ appRefNumber;

				sendMailEmail(message, application.getMliID(), user, subject);

			} else if (status
					.equals(ApplicationConstants.APPLICATION_PENDING_STATUS)) {

				// application=new Application();

				if (ineligibleDupRemarks.get(key) != null
						&& !(ineligibleDupRemarks.get(key).equals(""))) {
					remarks = (String) ineligibleDupRemarks.get(key);

				} else {

					remarks = "";
				}

				application.setAppRefNo(appRefNumber);
				application.setRemarks(remarks);
				if (application.getStatus().equals("EN")
						|| application.getStatus().equals("EP")
						|| application.getStatus().equals("EH")) {
					application.setStatus("EP");
					appProcessor.updateGeneralStatus(application, userId);

				} else {

					application.setStatus(status);
					appProcessor.updatePendingRejectedStatus(application,
							userId);
					System.out.println("<br>application processing==10960==");
				}
				// appProcessor.updateGeneralStatus(application,userId);

				ineligibleDupPendingApplications.add(application);
				// apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);

			}
			application = null;
		}
		apForm.setIneligibleDupApprovedApplications(ineligibleDupApprovedApplications);
		apForm.setIneligibleDupHoldApplications(ineligibleDupHoldApplications);
		apForm.setIneligibleDupRejectedApplications(ineligibleDupRejectedApplications);
		apForm.setIneligibleDupPendingApplications(ineligibleDupPendingApplications);

		return mapping.findForward("afterApprovalPage");

	}

	
	//bhu
	/********************************/

	/**
	 * This method sends mail to all the hold and rejected applications
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private void sendMailEmail(String message, String mliId, User user,
			String subject) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationProcessingAction", "sendMailEmail",
				"entering");

		Administrator administrator = new Administrator();
		MLIInfo mliInfo = new MLIInfo();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		Mailer mailer = new Mailer();
		ArrayList users = new ArrayList();
		// String mailPrivelege = "" ;
		// String emailPrivelege = "" ;
		// String hardCopyPrivelege = "" ;
		User mailUser = null;
		// String userId = user.getUserId() ;
		String fromEmail = user.getUserId();

		try {
			// users = rpDAO.getActiveBankUsers(mliId) ;
			users = administrator.getAllUsers(mliId);
			Log.log(Log.INFO, "ApplicationProcessingAction", "sendMailEmail",
					"users size :" + users.size());
		} catch (NoUserFoundException exception) {
			Log.log(Log.WARNING, "ApplicationProcessingAction",
					"sendMailEmail",
					"Exception getting user details for the MLI. Error="
							+ exception.getMessage());
		} catch (DatabaseException exception) {
			Log.log(Log.WARNING, "ApplicationProcessingAction",
					"sendMailEmail",
					"Exception getting user details for the MLI. Error="
							+ exception.getMessage());
		}
		mliInfo = registrationDAO.getMemberDetails(mliId.substring(0, 4),
				mliId.substring(4, 8), mliId.substring(8, 12));
		int userSize = users.size();
		ArrayList emailIds = new ArrayList();
		ArrayList mailIds = new ArrayList();

		for (int j = 0; j < userSize; j++) {
			mailUser = (User) users.get(j);
			emailIds.add(mailUser.getUserId()); // mail Ids
			Log.log(Log.INFO, "ApplicationProcessingAction", "sendMailEmail",
					"email ID:" + mailUser.getUserId());
			mailIds.add(mailUser.getEmailId()); // e-mail Ids
			// emailIds.add(mailUser.getEmailId()) ;
			// Log.log
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Member Id" + mliId + ", User mail " + mailUser.getUserId());
			Log.log(Log.DEBUG,
					"ApplicationProcessingAction",
					"sendMailEmail",
					"Member Id" + mliId + ", User email "
							+ mailUser.getEmailId());
		}

		if (emailIds != null) {
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Before instantiating message");
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Subject = " + message);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Email Message = " + message);
			Message mailMessage = new Message(emailIds, null, null, subject,
					message);
			mailMessage.setFrom(fromEmail);
			// try
			// {
			// mailer.sendEmail(message);
			administrator.sendMail(mailMessage);
			/*
			 * }catch(MailerException mailerException) { Log.log(Log.WARNING,
			 * className
			 * ,methodName,"Exception sending Mail. Error="+mailerException
			 * .getMessage()) ; }
			 */// administrator(message) ;
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"After instantiating message");
			// mailStatus = mailer.sendEmail(message) ;
		}

		// sending e-mail

		if (mailIds != null) {
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Before instantiating message");
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Subject = " + subject);
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"Email Message = " + message);
			Message mailMessage = new Message(mailIds, null, null, subject,
					message);
			mailMessage.setFrom(fromEmail);
			/*
			 * try { mailer.sendEmail(mailMessage);
			 * //administrator.sendMail(message); }catch(MailerException
			 * mailerException) { Log.log(Log.WARNING,
			 * "ApplicationProcessingAction",
			 * "sendMailEmail","Exception sending Mail. Error="
			 * +mailerException.getMessage()) ; }
			 */

			// administrator(message) ;
			Log.log(Log.DEBUG, "ApplicationProcessingAction", "sendMailEmail",
					"After instantiating message");
			// mailStatus = mailer.sendEmail(message) ;
		}

		message = ""; // resetting the message.
	}

	/**
	 * This method retrieves all the applications for reapproval
	 */
	public ActionForward showAppsForReApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "REAPP");

		APForm apForm = (APForm) form;

		apForm.resetReApproveMaps();
		String forward = "";

		TreeMap ineligibleMap = new TreeMap();
		TreeMap eligibleMap = new TreeMap();

		User user = getUserInformation(request);

		String userId = user.getUserId();

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ArrayList appsForReApproval = appProcessor
				.getApplicationsForReapproval(userId);

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForReApproval", "reapproval apps size :"
						+ appsForReApproval.size());

		ArrayList tcReApprovalApps = (ArrayList) appsForReApproval.get(0);
		for (int i = 0; i < tcReApprovalApps.size(); i++) {
			EligibleApplication eligibleApplication = (EligibleApplication) tcReApprovalApps
					.get(i);
			ineligibleMap.put(eligibleApplication.getCgpan(),
					eligibleApplication.getStatus());
			apForm.setIneligibleReapproveMap(ineligibleMap);

		}
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForReApproval", "reapproval apps size :"
						+ tcReApprovalApps.size());
		ArrayList wcReApprovalApps = (ArrayList) appsForReApproval.get(1);

		for (int i = 0; i < wcReApprovalApps.size(); i++) {
			Application tempApplication = (Application) wcReApprovalApps.get(i);
			eligibleMap.put(tempApplication.getCgpan(),
					tempApplication.getStatus());
			apForm.setEligibleReapproveMap(eligibleMap);
		}

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForReApproval", "reapproval apps size :"
						+ wcReApprovalApps.size());
		if ((tcReApprovalApps.size() != 0 && tcReApprovalApps != null)
				|| (wcReApprovalApps.size() != 0 && wcReApprovalApps != null)) {

			apForm.setTcClearApplications(tcReApprovalApps);

			apForm.setWcClearApplications(wcReApprovalApps);

			tcReApprovalApps = null;
			wcReApprovalApps = null;

			forward = "reApprovalPage";
		} else {
			request.setAttribute("message",
					"No Pending Applications For ReApproval");
			forward = "success";
		}

		appProcessor = null;
		user = null;
		appsForReApproval = null;

		return mapping.findForward(forward);

	}

	/*
	 * This method updates the status for reapproval
	 */
	public ActionForward afterReApprovalApps(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"updateApplicationStatus", "Entered");

		Application application = new Application();
		ApplicationProcessor appProcessor = new ApplicationProcessor();

		RpProcessor rpProcessor = new RpProcessor();

		User user = getUserInformation(request);

		String userId = user.getUserId();
		// System.out.println("userId:"+userId);
		ArrayList acceptedApplications = new ArrayList();
		ArrayList holdApplications = new ArrayList();
		ArrayList rejectedApplications = new ArrayList();

		Map cgpanNo = new TreeMap();
		Map reapprovedStatus = new TreeMap();
		Map reapprovedAmt = new TreeMap();
		Map reapprovedRemarks = new TreeMap();

		APForm apForm = (APForm) form;

		cgpanNo = apForm.getCgpanNo();
		// System.out.println("cgpanNo:"+cgpanNo);
		reapprovedStatus = apForm.getReapprovalStatus();
		// System.out.println("reapprovedStatus:"+reapprovedStatus);
		reapprovedAmt = apForm.getReApprovedAmt();
		// System.out.println("reapprovedAmt:"+reapprovedAmt);
		reapprovedRemarks = apForm.getReApprovalRemarks();
		// System.out.println("reapprovedRemarks:"+reapprovedRemarks);

		Set cgpanSet = cgpanNo.keySet();
		Set statusSet = reapprovedStatus.keySet();
		Set amountSet = reapprovedAmt.keySet();
		Set remarksSet = reapprovedRemarks.keySet();

		Iterator cgpanIterator = cgpanSet.iterator();
		/*
		 * Iterator statusIterator=statusSet.iterator(); Iterator
		 * amountIterator=amountSet.iterator(); Iterator
		 * remarksIterator=remarksSet.iterator();
		 */
		while (cgpanIterator.hasNext()) {
			application = new Application();
			String key = (String) cgpanIterator.next();
			String cgpanNumber = (String) cgpanNo.get(key);
			// System.out.println("cgpanNumber:"+cgpanNumber);
			String reapproveStatus = (String) reapprovedStatus.get(key);
			// System.out.println("reapproveStatus:"+reapproveStatus);

			double reapprovedAmount;
			String reapproveComments;

			if (reapproveStatus
					.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {

				reapprovedAmount = Double.parseDouble((String) reapprovedAmt
						.get(key));
				// System.out.println("reapprovedAmount:"+reapprovedAmount);

				if (reapprovedRemarks.get(key) != null
						&& !(reapprovedRemarks.get(key).equals(""))) {
					reapproveComments = (String) reapprovedRemarks.get(key);
				} else {

					reapproveComments = "";
				}

				application = new Application();
				application.setCgpan(cgpanNumber);
				application
						.setStatus(ApplicationConstants.APPLICATION_APPROVED_STATUS);
				application.setReapprovedAmount(reapprovedAmount);
				application.setReapprovalRemarks(reapproveComments);

				// calling a method in RP Processor for handling correction in
				// loan amounts
				rpProcessor.reapproveLoanAmount(application, reapprovedAmount,
						user, request.getSession(false).getServletContext()
								.getRealPath(""));

				appProcessor.updateReapprovalStatus(application, userId);

				acceptedApplications.add(application);

			} else if (reapproveStatus
					.equals(ApplicationConstants.APPLICATION_REJECTED_STATUS)) {
				application = new Application();
				Application partApplication = appProcessor.getPartApplication(
						null, cgpanNumber, "");

				application.setAppRefNo(partApplication.getAppRefNo());
				application.setCgpan(cgpanNumber);
				application.setStatus(reapproveStatus);
				application.setReapprovedAmount(0);

				if (reapprovedRemarks.get(key) != null
						&& !(reapprovedRemarks.get(key).equals(""))) {
					reapproveComments = (String) reapprovedRemarks.get(key);
				} else {

					reapproveComments = "";
				}
				application.setReapprovalRemarks(reapproveComments);

				appProcessor.updateRejectStatus(application, userId);

				rejectedApplications.add(application);

			} else if (reapproveStatus
					.equals(ApplicationConstants.APPLICATION_HOLD_STATUS)) {
				application = new Application();
				application.setCgpan(cgpanNumber);
				application
						.setStatus(ApplicationConstants.APPLICATION_REAPPROVE_HOLD_STATUS);
				application.setReapprovedAmount(0);

				if (reapprovedRemarks.get(key) != null
						&& !(reapprovedRemarks.get(key).equals(""))) {
					reapproveComments = (String) reapprovedRemarks.get(key);
				} else {

					reapproveComments = "";
				}
				application.setReapprovalRemarks(reapproveComments);

				appProcessor.updateReapprovalStatus(application, userId);

				holdApplications.add(application);

			}

		}

		apForm.setApprovedApplications(acceptedApplications);
		apForm.setHoldApplications(holdApplications);
		apForm.setRejectedApplications(rejectedApplications);

		return mapping.findForward("afterReapprovalPage");
	}

	/**
	 * This method retrieves all the applications for reapproval
	 */
	public ActionForward showAppsForEligibility(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		session.setAttribute(SessionConstants.APPLICATION_TYPE, "EL");

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		// Application application = new Application();
		EligibleApplication eligibleApplication = new EligibleApplication();

		ArrayList ineligibleApps = new ArrayList();

		APForm apForm = (APForm) form;

		String forward = "";

		// returns all the pending applications
		ArrayList pendingAppList = appProcessor.getPendingApps();

		int pendingAppListSize = pendingAppList.size();

		Application application = null;

		for (int i = 0; i < pendingAppListSize; i++) {

			eligibleApplication = new EligibleApplication();

			application = (Application) pendingAppList.get(i);
			String appRefNo = application.getAppRefNo();

			String mliID = null;
			String cgpan = "";
			application = appProcessor.getApplication(mliID, cgpan, appRefNo);

			java.util.Date submittedDate = application.getSubmittedDate();
			String stringDate = submittedDate.toString();

			eligibleApplication = appProcessor
					.getAppsForEligibilityCheck(appRefNo);

			if (!(eligibleApplication.getFailedCondition().equals(""))) {

				eligibleApplication.setAppRefNo(appRefNo);
				eligibleApplication.setSubmissiondate(stringDate);

				ineligibleApps.add(eligibleApplication);

				eligibleApplication = null;
			}
		}

		if (ineligibleApps != null && ineligibleApps.size() != 0) {
			apForm.setEligibleAppList(ineligibleApps);
			ineligibleApps = null;

			forward = "eligibleAppPage";
		} else {
			request.setAttribute("message",
					"No InEligible Applications Available");

			forward = "success";
		}

		return mapping.findForward(forward);
	}

	/**
	 * This method retrieves the borrower details with ssi reference number as
	 * the input parameter
	 */
	public ActionForward showborrowerDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();

		Integer ssiRefValue = new Integer(request.getParameter("ssiRef"));

		int ssiRefNo = ssiRefValue.intValue();
		borrowerDetails = appProcessor.viewBorrowerDetails(ssiRefNo);

		ssiDetails = borrowerDetails.getSsiDetails();
		BeanUtils.copyProperties(dynaForm, ssiDetails);

		BeanUtils.copyProperties(dynaForm, borrowerDetails);

		dynaForm.set("previouslyCovered", "Y");

		appProcessor = null;
		application = null;
		ssiDetails = null;
		borrowerDetails = null;
		ssiRefValue = null;

		return mapping.findForward("borrowerPage");
	}

	// /////ADDED BY RITESH ON 21NO2006 to filter the records based on mlis//
	/**
	 * This method retrieves all the applications for approval based on mli
	 * added by ritesh
	 */
	public ActionForward showAppsForApprovalNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("In showAppsForApprovalNew11111111");
		//System.out.println("<br>Application processing===11451===");
		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String forward = "";
		String bank = "";
		String bank1 = request.getParameter("Link");
		// System.out.println("Application ProcessingAction Line number 6971 Link"+bank1);
		// modify by bhupendra 28-dec-2006 Now PATH replace with &

		//bank1 = bank1 + "$";
		if (bank1.length() > 1) {
			bank = bank1.replaceAll("PATH", "&");
		}

		bank1 = request.getParameter("Link");

		// end changed......................
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;
		apForm.resetMaps();
		TreeMap tempStatusMap = new TreeMap();
		TreeMap clearStatusMap = new TreeMap();
		// added by sukumar@path for RSF clear Applications
		TreeMap clearRsfStatusMap = new TreeMap();
		TreeMap dupStatusMap = new TreeMap();
		TreeMap ineligibleStatusMap = new TreeMap();
		TreeMap tempRemarksMap = new TreeMap();
		TreeMap clearRemarksMap = new TreeMap();
		TreeMap clearRsfRemarksMap = new TreeMap();
		TreeMap dupRemarksMap = new TreeMap();
		TreeMap ineligibleRemarksMap = new TreeMap();
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"Before callin getappsforapproval frm processor");
		//hh----->
		System.out.println("<br>Application processing===12681===");
		System.out.println("user_id=="+userId);
		System.out.println("bank=="+bank);
		ArrayList applicationsList = appProcessor	.getApplicationsForApprovalPath(userId, bank);
		System.out.println("In showAppsForApprovalNew22222222222222222222222"+applicationsList);
		// retrieving the application count
		Integer intAppCount = (Integer) applicationsList.get(4);
		int appCount = intAppCount.intValue();
		 System.out.println("Application ProcessingAction Line number 6971 appCount"+appCount);
		apForm.setIntApplicationCount(appCount);
		ArrayList eligibleNonDupRsfApps = (ArrayList) applicationsList.get(5);
		 System.out.println("eligibleNonDupRsfApps count"+eligibleNonDupRsfApps.size());
		ArrayList eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		ArrayList eligibleDupApps = (ArrayList) applicationsList.get(1);
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"After callin getappsforapproval frm processor");
		ArrayList messagesTitleList = appProcessor.getMessageTitleContent();
		ArrayList messagesList = (ArrayList) messagesTitleList.get(1);
		apForm.setSpecialMessagesList(messagesList);
		messagesTitleList = null;
		messagesList = null;
		for (int r = 0; r < eligibleNonDupRsfApps.size(); r++) {
			Application tempRsfApplication = (Application) eligibleNonDupRsfApps
					.get(r);
			String tempAppRefNo = tempRsfApplication.getAppRefNo();
			String tempStatus = tempRsfApplication.getStatus();
			String remarks = tempRsfApplication.getRemarks();
			clearRsfStatusMap.put(tempAppRefNo, tempStatus);
			clearRsfRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearRsfTempMap(clearRsfStatusMap);
			apForm.setClearRsfRemMap(clearRsfRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		System.out.println("<br>Application processing===11524===");
		for (int i = 0; i < eligibleNonDupApps.size(); i++) {
			Application tempApplication = (Application) eligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempApplication.getAppRefNo();

			String loan_type=tempApplication.getLoanType();
			ApplicationProcessor ApplicationProcessorNew=new ApplicationProcessor();
			Application result=ApplicationProcessorNew.getApplicationsForNew(loan_type ,tempAppRefNo);
			System.out.println("In showAppsForApprovalNew3333333333333333333333333==="+result);
			System.out.println("<br>Application processing===11533===");
			Double plr=result.getTRM_PLR();
			Double int_rate =result.getTRM_INTEREST_RATE();
			Double plr_wc=result.getWCP_PLR();
			Double int_rate_wc=result.getWCP_INTEREST();  
			if(loan_type.equals("TC"))
			{
				tempApplication.setPLR(plr);
				tempApplication.setINTEREST_RATE(int_rate);
			}
			else if(loan_type.equals("WC"))
			{
				tempApplication.setPLR(plr_wc);
				tempApplication.setINTEREST_RATE(int_rate_wc);
			}

			 System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempApplication.getStatus();
			String remarks = tempApplication.getRemarks();
			clearStatusMap.put(tempAppRefNo, tempStatus);
			clearRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearTempMap(clearStatusMap);
			apForm.setClearRemMap(clearRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		for (int i = 0; i < eligibleDupApps.size(); i++) {
			DuplicateApplication tempDupApplication = (DuplicateApplication) eligibleDupApps
					.get(i);
			String tempAppRefNo = tempDupApplication.getNewAppRefNo();
			
			String loan_type=tempDupApplication.getLoanType();
			ApplicationProcessor ApplicationProcessorNew1=new ApplicationProcessor();
			System.out.println("<br>Application processing===11568===");
			DuplicateApplication result1=ApplicationProcessorNew1.getApplicationsForNew1(loan_type ,tempAppRefNo);
			System.out.println("In showAppsForApprovalNew44444444444444444444==="+result1);
			System.out.println("<br>Application processing===11569===");
			Double plr=result1.getTRM_PLR();
			Double int_rate =result1.getTRM_INTEREST_RATE();
			Double plr_wc=result1.getWCP_PLR();
			Double int_rate_wc=result1.getWCP_INTEREST();  
			if(loan_type.equals("TC"))
			{
				tempDupApplication.setPLR(plr);
				tempDupApplication.setINTEREST_RATE(int_rate);
			}
			else if(loan_type.equals("WC"))
			{
				tempDupApplication.setPLR(plr_wc);
				tempDupApplication.setINTEREST_RATE(int_rate_wc);
			}
			
			 System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempDupApplication.getStatus();
			String remarks = tempDupApplication.getDuplicateRemarks();
			dupStatusMap.put(tempAppRefNo, tempStatus);
			dupRemarksMap.put(tempAppRefNo, remarks);
			apForm.setDupTempMap(dupStatusMap);
			apForm.setDupRemMap(dupRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleNonDupApps = (ArrayList) applicationsList.get(2);
		for (int i = 0; i < ineligibleNonDupApps.size(); i++) {
			EligibleApplication tempEligibleApplication = (EligibleApplication) ineligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempEligibleApplication.getAppRefNo();
			 System.out.println("ritesh in ApplicationProcessingAction on line 8577 tempAppRefNo "+tempAppRefNo);

			String tempStatus = tempEligibleApplication.getStatus();
			String remarks = tempEligibleApplication.getEligibleRemarks();
			ineligibleStatusMap.put(tempAppRefNo, tempStatus);
			ineligibleRemarksMap.put(tempAppRefNo, remarks);
			apForm.setIneligibleTempMap(ineligibleStatusMap);
			apForm.setIneligibleRemMap(ineligibleRemarksMap);
		}
		ArrayList ineligibleDupApps = (ArrayList) applicationsList.get(3);
		for (int i = 0; i < ((ArrayList) ineligibleDupApps.get(0)).size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) ((ArrayList) ineligibleDupApps
					.get(0)).get(i);
			String tempAppRefNo = duplicateApplication.getNewAppRefNo();
			String tempStatus = duplicateApplication.getStatus();
			 System.out.println("ritesh in ApplicationProcessingAction on line 8571 tempAppRefNo "+tempAppRefNo);

			String remarks = duplicateApplication.getDuplicateRemarks();
			System.out.println("remarks-------------------------------==="+remarks);
			tempStatusMap.put(tempAppRefNo, tempStatus);
			tempRemarksMap.put(tempAppRefNo, remarks);
			apForm.setTempMap(tempStatusMap);
			apForm.setTempRemMap(tempRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		ArrayList privilegeList = user.getPrivileges();
		if (privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleNonDupApps(ineligibleNonDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")) {
			apForm.setEligibleDupApps(eligibleDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")
				&& privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleDupApps(ineligibleDupApps);
		}
		apForm.setEligibleNonDupRsfApps(eligibleNonDupRsfApps);

		apForm.setEligibleNonDupApps(eligibleNonDupApps);

		if ((apForm.getIneligibleNonDupApps() == null || apForm
				.getIneligibleNonDupApps().size() == 0)
				&& (apForm.getEligibleNonDupRsfApps() == null || apForm
						.getEligibleNonDupRsfApps().size() == 0)
				&& (apForm.getEligibleDupApps() == null || apForm
						.getEligibleDupApps().size() == 0)
				&& (apForm.getEligibleNonDupApps() == null || apForm
						.getEligibleNonDupApps().size() == 0)
				&& (((ArrayList) apForm.getIneligibleDupApps().get(0)).size() == 0 || (ArrayList) apForm
						.getIneligibleDupApps().get(0) == null)) {
			forward = "successNew";
			request.setAttribute("message",
					"No Applications for Approval Available");
		} else {
			forward = "approvalPageNew";
		}
		appProcessor = null;
		applicationsList = null;
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForApprovalPath", "Exited. Memory : "
						+ Runtime.getRuntime().freeMemory());
		return mapping.findForward(forward);
	}
	
	
	//koteswaR ON 05 08 15 FOR TESTING APPROVE APPROVE LIKE NEW APPROVAL
	
	public ActionForward showAppsForApprovalApprovenew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String forward = "";
		String bank = "";
		String bank1 = request.getParameter("Link");
		// System.out.println("Application ProcessingAction Line number 6971 Link"+bank1);
		// modify by bhupendra 28-dec-2006 Now PATH replace with &

		bank1 = bank1 + "$";
		if (bank1.length() > 1) {
			bank = bank1.replaceAll("PATH", "&");
		}

		bank1 = request.getParameter("Link");

		// end changed......................
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;
		apForm.resetMaps();
		TreeMap tempStatusMap = new TreeMap();
		TreeMap clearStatusMap = new TreeMap();
		// added by sukumar@path for RSF clear Applications
		TreeMap clearRsfStatusMap = new TreeMap();
		TreeMap dupStatusMap = new TreeMap();
		TreeMap ineligibleStatusMap = new TreeMap();
		TreeMap tempRemarksMap = new TreeMap();
		TreeMap clearRemarksMap = new TreeMap();
		TreeMap clearRsfRemarksMap = new TreeMap();
		TreeMap dupRemarksMap = new TreeMap();
		TreeMap ineligibleRemarksMap = new TreeMap();
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"Before callin getappsforapproval frm processor");
		ArrayList applicationsList = appProcessor
				.getApplicationsForApprovalPathnew(userId, bank);
		// retrieving the application count
		Integer intAppCount = (Integer) applicationsList.get(4);
		int appCount = intAppCount.intValue();
		// System.out.println("Application ProcessingAction Line number 6971 appCount"+appCount);
		apForm.setIntApplicationCount(appCount);
		ArrayList eligibleNonDupRsfApps = (ArrayList) applicationsList.get(5);
		// System.out.println("eligibleNonDupRsfApps count"+eligibleNonDupRsfApps.size());
		ArrayList eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		ArrayList eligibleDupApps = (ArrayList) applicationsList.get(1);
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"After callin getappsforapproval frm processor");
		ArrayList messagesTitleList = appProcessor.getMessageTitleContent();
		ArrayList messagesList = (ArrayList) messagesTitleList.get(1);
		apForm.setSpecialMessagesList(messagesList);
		messagesTitleList = null;
		messagesList = null;
		for (int r = 0; r < eligibleNonDupRsfApps.size(); r++) {
			Application tempRsfApplication = (Application) eligibleNonDupRsfApps
					.get(r);
			String tempAppRefNo = tempRsfApplication.getAppRefNo();
			String tempStatus = tempRsfApplication.getStatus();
			String remarks = tempRsfApplication.getRemarks();
			clearRsfStatusMap.put(tempAppRefNo, tempStatus);
			clearRsfRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearRsfTempMap(clearRsfStatusMap);
			apForm.setClearRsfRemMap(clearRsfRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		for (int i = 0; i < eligibleNonDupApps.size(); i++) {
			Application tempApplication = (Application) eligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempApplication.getAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempApplication.getStatus();
			String remarks = tempApplication.getRemarks();
			clearStatusMap.put(tempAppRefNo, tempStatus);
			clearRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearTempMap(clearStatusMap);
			apForm.setClearRemMap(clearRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		for (int i = 0; i < eligibleDupApps.size(); i++) {
			DuplicateApplication tempDupApplication = (DuplicateApplication) eligibleDupApps
					.get(i);
			String tempAppRefNo = tempDupApplication.getNewAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempDupApplication.getStatus();
			String remarks = tempDupApplication.getDuplicateRemarks();
			dupStatusMap.put(tempAppRefNo, tempStatus);
			dupRemarksMap.put(tempAppRefNo, remarks);
			apForm.setDupTempMap(dupStatusMap);
			apForm.setDupRemMap(dupRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleNonDupApps = (ArrayList) applicationsList.get(2);
		for (int i = 0; i < ineligibleNonDupApps.size(); i++) {
			EligibleApplication tempEligibleApplication = (EligibleApplication) ineligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempEligibleApplication.getAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 8577 tempAppRefNo "+tempAppRefNo);

			String tempStatus = tempEligibleApplication.getStatus();
			String remarks = tempEligibleApplication.getEligibleRemarks();
			ineligibleStatusMap.put(tempAppRefNo, tempStatus);
			ineligibleRemarksMap.put(tempAppRefNo, remarks);
			apForm.setIneligibleTempMap(ineligibleStatusMap);
			apForm.setIneligibleRemMap(ineligibleRemarksMap);
		}
		ArrayList ineligibleDupApps = (ArrayList) applicationsList.get(3);
		for (int i = 0; i < ((ArrayList) ineligibleDupApps.get(0)).size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) ((ArrayList) ineligibleDupApps
					.get(0)).get(i);
			String tempAppRefNo = duplicateApplication.getNewAppRefNo();
			String tempStatus = duplicateApplication.getStatus();
			// System.out.println("ritesh in ApplicationProcessingAction on line 8571 tempAppRefNo "+tempAppRefNo);

			String remarks = duplicateApplication.getDuplicateRemarks();
			tempStatusMap.put(tempAppRefNo, tempStatus);
			tempRemarksMap.put(tempAppRefNo, remarks);
			apForm.setTempMap(tempStatusMap);
			apForm.setTempRemMap(tempRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		ArrayList privilegeList = user.getPrivileges();
		if (privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleNonDupApps(ineligibleNonDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")) {
			apForm.setEligibleDupApps(eligibleDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")
				&& privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleDupApps(ineligibleDupApps);
		}
		apForm.setEligibleNonDupRsfApps(eligibleNonDupRsfApps);

		apForm.setEligibleNonDupApps(eligibleNonDupApps);

		if ((apForm.getIneligibleNonDupApps() == null || apForm
				.getIneligibleNonDupApps().size() == 0)
				&& (apForm.getEligibleNonDupRsfApps() == null || apForm
						.getEligibleNonDupRsfApps().size() == 0)
				&& (apForm.getEligibleDupApps() == null || apForm
						.getEligibleDupApps().size() == 0)
				&& (apForm.getEligibleNonDupApps() == null || apForm
						.getEligibleNonDupApps().size() == 0)
				&& (((ArrayList) apForm.getIneligibleDupApps().get(0)).size() == 0 || (ArrayList) apForm
						.getIneligibleDupApps().get(0) == null)) {
			forward = "successNew";
			request.setAttribute("message",
					"No Applications for Approval Available");
		} else {
			forward = "approvalPageNew";
		}
		appProcessor = null;
		applicationsList = null;
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForApprovalPath", "Exited. Memory : "
						+ Runtime.getRuntime().freeMemory());
		return mapping.findForward(forward);
	}
	
	//KOTESWAR END
	
	
	//koteswar for approve approve new
	
	public ActionForward showAppsForApprovalApprove(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String forward = "";
		String bank = "";
		String bank1 = request.getParameter("Link");
		// System.out.println("Application ProcessingAction Line number 6971 Link"+bank1);
		// modify by bhupendra 28-dec-2006 Now PATH replace with &

		bank1 = bank1 + "$";
		if (bank1.length() > 1) {
			bank = bank1.replaceAll("PATH", "&");
		}

		bank1 = request.getParameter("Link");

		// end changed......................
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;
		apForm.resetMaps();
		TreeMap tempStatusMap = new TreeMap();
		TreeMap clearStatusMap = new TreeMap();
		// added by sukumar@path for RSF clear Applications
		TreeMap clearRsfStatusMap = new TreeMap();
		TreeMap dupStatusMap = new TreeMap();
		TreeMap ineligibleStatusMap = new TreeMap();
		TreeMap tempRemarksMap = new TreeMap();
		TreeMap clearRemarksMap = new TreeMap();
		TreeMap clearRsfRemarksMap = new TreeMap();
		TreeMap dupRemarksMap = new TreeMap();
		TreeMap ineligibleRemarksMap = new TreeMap();
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"Before callin getappsforapproval frm processor");
		ArrayList applicationsList = appProcessor
				.getApplicationsForApprovalPath(userId, bank);
		// retrieving the application count
		Integer intAppCount = (Integer) applicationsList.get(4);
		int appCount = intAppCount.intValue();
		// System.out.println("Application ProcessingAction Line number 6971 appCount"+appCount);
		apForm.setIntApplicationCount(appCount);
		ArrayList eligibleNonDupRsfApps = (ArrayList) applicationsList.get(5);
		// System.out.println("eligibleNonDupRsfApps count"+eligibleNonDupRsfApps.size());
		ArrayList eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		ArrayList eligibleDupApps = (ArrayList) applicationsList.get(1);
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApprovalPath",
				"After callin getappsforapproval frm processor");
		ArrayList messagesTitleList = appProcessor.getMessageTitleContent();
		ArrayList messagesList = (ArrayList) messagesTitleList.get(1);
		apForm.setSpecialMessagesList(messagesList);
		messagesTitleList = null;
		messagesList = null;
		for (int r = 0; r < eligibleNonDupRsfApps.size(); r++) {
			Application tempRsfApplication = (Application) eligibleNonDupRsfApps
					.get(r);
			String tempAppRefNo = tempRsfApplication.getAppRefNo();
			String tempStatus = tempRsfApplication.getStatus();
			String remarks = tempRsfApplication.getRemarks();
			clearRsfStatusMap.put(tempAppRefNo, tempStatus);
			clearRsfRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearRsfTempMap(clearRsfStatusMap);
			apForm.setClearRsfRemMap(clearRsfRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		for (int i = 0; i < eligibleNonDupApps.size(); i++) {
			Application tempApplication = (Application) eligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempApplication.getAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempApplication.getStatus();
			String remarks = tempApplication.getRemarks();
			clearStatusMap.put(tempAppRefNo, tempStatus);
			clearRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearTempMap(clearStatusMap);
			apForm.setClearRemMap(clearRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		for (int i = 0; i < eligibleDupApps.size(); i++) {
			DuplicateApplication tempDupApplication = (DuplicateApplication) eligibleDupApps
					.get(i);
			String tempAppRefNo = tempDupApplication.getNewAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 6155 tempAppRefNo "+tempAppRefNo);
			String tempStatus = tempDupApplication.getStatus();
			String remarks = tempDupApplication.getDuplicateRemarks();
			dupStatusMap.put(tempAppRefNo, tempStatus);
			dupRemarksMap.put(tempAppRefNo, remarks);
			apForm.setDupTempMap(dupStatusMap);
			apForm.setDupRemMap(dupRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleNonDupApps = (ArrayList) applicationsList.get(2);
		for (int i = 0; i < ineligibleNonDupApps.size(); i++) {
			EligibleApplication tempEligibleApplication = (EligibleApplication) ineligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempEligibleApplication.getAppRefNo();
			// System.out.println("ritesh in ApplicationProcessingAction on line 8577 tempAppRefNo "+tempAppRefNo);

			String tempStatus = tempEligibleApplication.getStatus();
			String remarks = tempEligibleApplication.getEligibleRemarks();
			ineligibleStatusMap.put(tempAppRefNo, tempStatus);
			ineligibleRemarksMap.put(tempAppRefNo, remarks);
			apForm.setIneligibleTempMap(ineligibleStatusMap);
			apForm.setIneligibleRemMap(ineligibleRemarksMap);
		}
		ArrayList ineligibleDupApps = (ArrayList) applicationsList.get(3);
		for (int i = 0; i < ((ArrayList) ineligibleDupApps.get(0)).size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) ((ArrayList) ineligibleDupApps
					.get(0)).get(i);
			String tempAppRefNo = duplicateApplication.getNewAppRefNo();
			String tempStatus = duplicateApplication.getStatus();
			// System.out.println("ritesh in ApplicationProcessingAction on line 8571 tempAppRefNo "+tempAppRefNo);

			String remarks = duplicateApplication.getDuplicateRemarks();
			tempStatusMap.put(tempAppRefNo, tempStatus);
			tempRemarksMap.put(tempAppRefNo, remarks);
			apForm.setTempMap(tempStatusMap);
			apForm.setTempRemMap(tempRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApprovalPath", "apForm Map size :"
							+ apForm.getTempMap().size());
		}
		ArrayList privilegeList = user.getPrivileges();
		if (privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleNonDupApps(ineligibleNonDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")) {
			apForm.setEligibleDupApps(eligibleDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")
				&& privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleDupApps(ineligibleDupApps);
		}
		apForm.setEligibleNonDupRsfApps(eligibleNonDupRsfApps);

		apForm.setEligibleNonDupApps(eligibleNonDupApps);

		if ((apForm.getIneligibleNonDupApps() == null || apForm
				.getIneligibleNonDupApps().size() == 0)
				&& (apForm.getEligibleNonDupRsfApps() == null || apForm
						.getEligibleNonDupRsfApps().size() == 0)
				&& (apForm.getEligibleDupApps() == null || apForm
						.getEligibleDupApps().size() == 0)
				&& (apForm.getEligibleNonDupApps() == null || apForm
						.getEligibleNonDupApps().size() == 0)
				&& (((ArrayList) apForm.getIneligibleDupApps().get(0)).size() == 0 || (ArrayList) apForm
						.getIneligibleDupApps().get(0) == null)) {
			forward = "successNew";
			request.setAttribute("message",
					"No Applications for Approval Available");
		} else {
			forward = "approvalPageNew";
		}
		appProcessor = null;
		applicationsList = null;
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"showAppsForApprovalPath", "Exited. Memory : "
						+ Runtime.getRuntime().freeMemory());
		return mapping.findForward(forward);
	}

	// /////////END ADD BY RITESH ///////////////////////////////////////

	/**
	 * This method retrieves all the applications for approval 
	 */
	
	
	
	
	public ActionForward showAppsForApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");

		User user = getUserInformation(request);

		String userId = user.getUserId();

		String forward = "";

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		// EligibleApplication eligibleApplication=new EligibleApplication();
		// DuplicateApplication duplicateApplication=new DuplicateApplication();
		// Application application = new Application();

		APForm apForm = (APForm) form;

		apForm.resetMaps();

		TreeMap tempStatusMap = new TreeMap();
		TreeMap clearStatusMap = new TreeMap();
		TreeMap dupStatusMap = new TreeMap();
		TreeMap ineligibleStatusMap = new TreeMap();

		TreeMap tempRemarksMap = new TreeMap();
		TreeMap clearRemarksMap = new TreeMap();
		TreeMap dupRemarksMap = new TreeMap();
		TreeMap ineligibleRemarksMap = new TreeMap();

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApproval",
				"Before callin getappsforapproval frm processor");
		ArrayList applicationsList = appProcessor
				.getApplicationsForApproval(userId);

		// retrieving the application count
		Integer intAppCount = (Integer) applicationsList.get(4);
		int appCount = intAppCount.intValue();
		apForm.setIntApplicationCount(appCount);

		// double approvedAmount=0;
		ArrayList eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		ArrayList eligibleDupApps = (ArrayList) applicationsList.get(1);

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApproval",
				"After callin getappsforapproval frm processor");
		ArrayList messagesTitleList = appProcessor.getMessageTitleContent();
		ArrayList messagesList = (ArrayList) messagesTitleList.get(1);
		apForm.setSpecialMessagesList(messagesList);
		messagesTitleList = null;
		messagesList = null;

		// ArrayList appRefNosList=new ArrayList();

		eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		// System.out.println("eligibleNonDupApps.size"+eligibleNonDupApps.size());
		for (int i = 0; i < eligibleNonDupApps.size(); i++) {
			Application tempApplication = (Application) eligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempApplication.getAppRefNo();
			String tempStatus = tempApplication.getStatus();
			String remarks = tempApplication.getRemarks();

			clearStatusMap.put(tempAppRefNo, tempStatus);
			clearRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearTempMap(clearStatusMap);
			apForm.setClearRemMap(clearRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		eligibleDupApps = (ArrayList) applicationsList.get(1);
		// System.out.println("eligibleDupApps.size()"+eligibleDupApps.size());
		for (int i = 0; i < eligibleDupApps.size(); i++) {
			DuplicateApplication tempDupApplication = (DuplicateApplication) eligibleDupApps
					.get(i);
			String tempAppRefNo = tempDupApplication.getNewAppRefNo();
			String tempStatus = tempDupApplication.getStatus();
			String remarks = tempDupApplication.getDuplicateRemarks();

			dupStatusMap.put(tempAppRefNo, tempStatus);
			dupRemarksMap.put(tempAppRefNo, remarks);

			apForm.setDupTempMap(dupStatusMap);
			apForm.setDupRemMap(dupRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleNonDupApps = (ArrayList) applicationsList.get(2);
		// System.out.println("ineligibleNonDupApps.size"+ineligibleNonDupApps.size());
		for (int i = 0; i < ineligibleNonDupApps.size(); i++) {
			EligibleApplication tempEligibleApplication = (EligibleApplication) ineligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempEligibleApplication.getAppRefNo();
			String tempStatus = tempEligibleApplication.getStatus();
			String remarks = tempEligibleApplication.getEligibleRemarks();

			ineligibleStatusMap.put(tempAppRefNo, tempStatus);
			ineligibleRemarksMap.put(tempAppRefNo, remarks);

			apForm.setIneligibleTempMap(ineligibleStatusMap);
			apForm.setIneligibleRemMap(ineligibleRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleDupApps = (ArrayList) applicationsList.get(3);
		// System.out.println("ineligibleDupApps.size()"+ineligibleDupApps.size());
		for (int i = 0; i < ((ArrayList) ineligibleDupApps.get(0)).size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) ((ArrayList) ineligibleDupApps
					.get(0)).get(i);
			String tempAppRefNo = duplicateApplication.getNewAppRefNo();
			String tempStatus = duplicateApplication.getStatus();
			String remarks = duplicateApplication.getDuplicateRemarks();
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval",
					"remarks of the ineligible and duplicate:" + remarks);

			tempStatusMap.put(tempAppRefNo, tempStatus);
			tempRemarksMap.put(tempAppRefNo, remarks);

			apForm.setTempMap(tempStatusMap);
			apForm.setTempRemMap(tempRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList privilegeList = user.getPrivileges();
		if (privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "has privilege");

			apForm.setIneligibleNonDupApps(ineligibleNonDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")) {
			apForm.setEligibleDupApps(eligibleDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")
				&& privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleDupApps(ineligibleDupApps);
		}

		// apForm.setIneligibleDupApps(ineligibleDupApps);
		apForm.setEligibleNonDupApps(eligibleNonDupApps);

		if ((apForm.getIneligibleNonDupApps() == null || apForm
				.getIneligibleNonDupApps().size() == 0)
				&& (apForm.getEligibleDupApps() == null || apForm
						.getEligibleDupApps().size() == 0)
				&& (apForm.getEligibleNonDupApps() == null || apForm
						.getEligibleNonDupApps().size() == 0)
				&& (((ArrayList) apForm.getIneligibleDupApps().get(0)).size() == 0 || (ArrayList) apForm
						.getIneligibleDupApps().get(0) == null)) {
			forward = "success";
			request.setAttribute("message",
					"No Applications for Approval Available");
		} else {

			forward = "approvalPage";
		}

		appProcessor = null;
		applicationsList = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "showAppsForApproval",
				"Exited. Memory : " + Runtime.getRuntime().freeMemory());

		return mapping.findForward(forward);
	}

	/**
	 * This method retrieves the borrower details with ssi reference number as
	 * the input parameter
	 */
	public ActionForward afterSsiRefPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session1 = request.getSession(false);
		String applicationType = (String) session1
				.getAttribute(SessionConstants.APPLICATION_LOAN_TYPE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		GMProcessor gmProcessor = new GMProcessor();

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();

		User user = getUserInformation(request);
		String memberName = "";
		String bankId = user.getBankId();

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberName = (String) dynaForm.get("selectMember");

		} else {

			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			memberName = bankId + zoneId + branchId;
		}

		String ssiDtl = request.getParameter("ssiRefNo");

		int start = ssiDtl.indexOf("(");
		// int finish=ssiDtl.indexOf(")");
		String ssiRefNumber = ssiDtl.substring(0, start);
		if (ssiRefNumber.length() == 9) {
			borrowerDetails = gmProcessor.getBorrowerDetailsForBID(memberName,
					ssiRefNumber);

			ssiDetails = borrowerDetails.getSsiDetails();
			BeanUtils.copyProperties(dynaForm, ssiDetails);

			BeanUtils.copyProperties(dynaForm, borrowerDetails);

			dynaForm.set("unitValue", ssiRefNumber);
			dynaForm.set("none", "cgbid");
			dynaForm.set("previouslyCovered", "Y");

			Application tempApplication = new Application();
			tempApplication.setBorrowerDetails(borrowerDetails);
			tempApplication.setMliID(memberName);

			double balanceAppAmt = appProcessor
					.getBalanceApprovedAmt(tempApplication);

			dynaForm.set("balanceApprovedAmt", new Double(balanceAppAmt));

			session1.setAttribute("ssiRefNumber", new Integer(borrowerDetails
					.getSsiDetails().getBorrowerRefNo()));

		} else {

			Integer ssiRefValue = new Integer(ssiRefNumber);
			int ssiRefNo = ssiRefValue.intValue();

			borrowerDetails = appProcessor.viewBorrowerDetails(ssiRefNo);

			ssiDetails = borrowerDetails.getSsiDetails();
			BeanUtils.copyProperties(dynaForm, ssiDetails);

			BeanUtils.copyProperties(dynaForm, borrowerDetails);

			dynaForm.set("unitValue", "");
			dynaForm.set("none", "none");
			dynaForm.set("previouslyCovered", "N");

			session1.setAttribute("ssiRefNumber", ssiRefValue);

		}

		Administrator admin = new Administrator();

		ArrayList statesList = (ArrayList) getStateList();
		dynaForm.set("statesList", statesList);

		String state = (String) dynaForm.get("state");
		ArrayList districtList = admin.getAllDistricts(state);
		dynaForm.set("districtList", districtList);
		// int districtSize=districtList.size();
		String districtName = ssiDetails.getDistrict();

		if (districtList.contains(districtName)) {
			dynaForm.set("district", districtName);
		} else {
			dynaForm.set("districtOthers", districtName);
			dynaForm.set("district", "Others");

		}

		ArrayList socialList = getSocialCategory();
		dynaForm.set("socialCategoryList", socialList);

		/*
		 * ArrayList industryNatureList=getIndustryNature();
		 * dynaForm.set("industryNatureList",industryNatureList); String
		 * industryNature=(String)dynaForm.get("industryNature");
		 * if(industryNature!=null && !(industryNature.equals(""))) { ArrayList
		 * industrySectorList=admin.getIndustrySectors(industryNature);
		 * dynaForm.set("industrySectorList",industrySectorList);
		 * 
		 * }
		 */dynaForm.set("industryNature", ssiDetails.getIndustryNature());
		dynaForm.set("industrySector", ssiDetails.getIndustrySector());

		String forward = setApps(applicationType);
		if (applicationType.equals("TC")) {
			session1.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "14");
		} else if (applicationType.equals("WC")) {
			session1.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "18");
		} else if (applicationType.equals("CC") || applicationType.equals("BO")) {
			session1.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG, "19");
		}

		// session1.setAttribute(SessionConstants.APPLICATION_TYPE_FLAG,"14");

		/*
		 * MCGSProcessor mcgsProcessor=new MCGSProcessor(); ArrayList
		 * participatingBanks=mcgsProcessor.getAllParticipatingBanks();
		 * dynaForm.set("participatingBanks",participatingBanks);
		 * dynaForm.set("mcgfName",bankName); dynaForm.set("mcgfId",memberId);
		 * dynaForm.set("schemeName","MCGS");
		 */

		appProcessor = null;
		application = null;
		ssiDetails = null;
		borrowerDetails = null;
		applicationType = null;

		return mapping.findForward(forward);
	}

	/**
	 * This method retrieves the old application details
	 */
	public ActionForward showOldAppDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String pageForward = "";

		DynaActionForm dynaForm = (DynaActionForm) form;

		Administrator admin = new Administrator();
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		Application application = new Application();
		MCGFDetails mcgfDetails = new MCGFDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();

		String cgpan = "";// cgpan
		String appRefNo = "";
		String mliId = null;

		BeanUtils.populate(ssiDetails, dynaForm.getMap()); // for setting cgbid
		borrowerDetails.setSsiDetails(ssiDetails);
		BeanUtils.populate(borrowerDetails, dynaForm.getMap());
		application.setBorrowerDetails(borrowerDetails);

		BeanUtils.populate(application, dynaForm.getMap()); // for setting cgpan
															// and apprefno

		cgpan = request.getParameter("cgpan");

		application.setCgpan(cgpan);
		application.setAppRefNo("");

		if (((mliId == null || mliId.equals("")) && !(cgpan.equals("")))) {
			HttpSession appSession1 = request.getSession(false);

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Entering when cgpan and app ref no are entered");
			application = appProcessor
					.getOldApplication(mliId, cgpan, appRefNo);
			mliId = application.getMliID();
			String bankId = mliId.substring(0, 4);
			String zoneId = mliId.substring(4, 8);
			String branchId = mliId.substring(8, 12);
			Registration registration = new Registration();
			MLIInfo mliInfo = registration.getMemberDetails(bankId, zoneId,
					branchId);

			String applicationLoanType = application.getLoanType();

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Setting the values in copy Properties....");

			// copying ssi details
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();
			BeanUtils.copyProperties(dynaForm, ssiDetails);

			// copying borrower details
			borrowerDetails = application.getBorrowerDetails();
			BeanUtils.copyProperties(dynaForm, borrowerDetails);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Borrower Details...");

			// copying primary security details
			PrimarySecurityDetails primarySecurityDetails = (application
					.getProjectOutlayDetails()).getPrimarySecurityDetails();
			BeanUtils.copyProperties(dynaForm, primarySecurityDetails);
			double landValue = ((java.lang.Double) dynaForm.get("landValue"))
					.doubleValue();
			double bldgValue = ((java.lang.Double) dynaForm.get("bldgValue"))
					.doubleValue();
			double machineValue = ((java.lang.Double) dynaForm
					.get("machineValue")).doubleValue();
			double assetsValue = ((java.lang.Double) dynaForm
					.get("assetsValue")).doubleValue();
			double currentAssetsValue = ((java.lang.Double) dynaForm
					.get("currentAssetsValue")).doubleValue();
			double othersValue = ((java.lang.Double) dynaForm
					.get("othersValue")).doubleValue();
			double psTotalValue = landValue + bldgValue + machineValue
					+ assetsValue + currentAssetsValue + othersValue;
			Double intPsTotal = new Double(psTotalValue);
			dynaForm.set("psTotal", intPsTotal);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Primary Security Details...");

			// copying project Outlay details
			ProjectOutlayDetails projectOutlayDetails = application
					.getProjectOutlayDetails();
			BeanUtils.copyProperties(dynaForm, projectOutlayDetails);
			/*
			 * double projectOutlayValue=
			 * projectOutlayDetails.getProjectOutlay(); Double doubleValue=new
			 * Double(projectOutlayValue); int
			 * doubleIntValue=doubleValue.intValue(); Integer intValue=new
			 * Integer(doubleIntValue); dynaForm.set("projectOutlay",intValue);
			 */
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Project Outlay Details...");

			termLoan = application.getTermLoan();
			BeanUtils.copyProperties(dynaForm, termLoan);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Term Loan Details...");
			double termCreditSanctioned = ((java.lang.Double) dynaForm
					.get("termCreditSanctioned")).doubleValue();
			java.lang.Double termCreditSanctionedVal = new Double(
					termCreditSanctioned);
			double tcPromoterContribution = ((java.lang.Double) dynaForm
					.get("tcPromoterContribution")).doubleValue();
			double tcSubsidyOrEquity = ((java.lang.Double) dynaForm
					.get("tcSubsidyOrEquity")).doubleValue();
			double tcOthers = ((java.lang.Double) dynaForm.get("tcOthers"))
					.doubleValue();
			double projectCost = termCreditSanctioned + tcPromoterContribution
					+ tcSubsidyOrEquity + tcOthers;
			java.lang.Double projectCostIntVal = new Double(projectCost);
			dynaForm.set("projectCost", projectCostIntVal);
			dynaForm.set("amountSanctioned", termCreditSanctionedVal);

			// copying working capital details
			workingCapital = application.getWc();
			BeanUtils.copyProperties(dynaForm, workingCapital);
			double wcFundBasedSanctioned = ((java.lang.Double) dynaForm
					.get("wcFundBasedSanctioned")).doubleValue();
			double wcNonFundBasedSanctioned = ((java.lang.Double) dynaForm
					.get("wcNonFundBasedSanctioned")).doubleValue();

			java.lang.Double existingFBTotalVal = new Double(
					wcFundBasedSanctioned);
			java.lang.Double existingNFBTotalVal = new Double(
					wcNonFundBasedSanctioned);
			dynaForm.set("existingFundBasedTotal", existingFBTotalVal);
			dynaForm.set("existingNonFundBasedTotal", existingNFBTotalVal);
			// Log.log(Log.DEBUG,"ApplicationProcessingAction","showCgpanList","existing total :"
			// + existingTotalVal);

			double wcPromoterContribution = ((java.lang.Double) dynaForm
					.get("wcPromoterContribution")).doubleValue();
			double wcSubsidyOrEquity = ((java.lang.Double) dynaForm
					.get("wcSubsidyOrEquity")).doubleValue();
			double wcOthers = ((java.lang.Double) dynaForm.get("wcOthers"))
					.doubleValue();

			double wcAssessed = wcFundBasedSanctioned
					+ wcNonFundBasedSanctioned + wcPromoterContribution
					+ wcSubsidyOrEquity + wcOthers;
			java.lang.Double wcAssessedIntVal = new Double(wcAssessed);
			dynaForm.set("wcAssessed", wcAssessedIntVal);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Working Capital Details...");

			double projectCostValue = ((java.lang.Double) dynaForm
					.get("projectCost")).doubleValue();
			double wcAssessedValue = ((java.lang.Double) dynaForm
					.get("wcAssessed")).doubleValue();
			double projectOutlayValueCost = projectCostValue + wcAssessedValue;
			java.lang.Double projectOutlayVal = new Double(
					projectOutlayValueCost);
			dynaForm.set("projectOutlay", projectOutlayVal); // setting project
																// outlay to the
																// form
			// copying secruittization details
			Securitization securitization = application.getSecuritization();
			BeanUtils.copyProperties(dynaForm, securitization);
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Securitization Details...");

			// copying mcgfDetails
			String mcgfFlag = mliInfo.getSupportMCGF();
			if (mcgfFlag.equals("Y")) {

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "M");

				mcgfDetails = application.getMCGFDetails();
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf App ref no:"
								+ mcgfDetails.getApplicationReferenceNumber());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf approved Amount:"
								+ mcgfDetails.getMcgfApprovedAmt());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf district:" + mcgfDetails.getMcgfDistrict());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf bank:" + mcgfDetails.getParticipatingBank());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"showCgpanList",
						"mcgf branch :"
								+ mcgfDetails.getParticipatingBankBranch());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList",
						"mcgf name :" + mcgfDetails.getMcgfName());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "mcgf Id:" + mcgfDetails.getMcgfId());

				mcgfDetails.setMcgfId(mliId);
				application.setMCGFDetails(mcgfDetails);
				BeanUtils.copyProperties(dynaForm, mcgfDetails);
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Set MCGF Details...");

			} else {
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"showCgpanList", "Not under MCGF");

				appSession1.setAttribute(SessionConstants.MCGF_FLAG, "NM");
			}

			String remarks = application.getRemarks();
			application.setExistingRemarks(remarks);

			// copying application to the form
			BeanUtils.copyProperties(dynaForm, application);

			// setting the value of borrower covered and unit value
			/*
			 * if(application.getBorrowerDetails().getPreviouslyCovered().equals(
			 * "Y")) { if(application.getCgpanReference()!=null &&
			 * !(application.getCgpanReference().equals(""))) { String
			 * cgpanRef=application.getCgpanReference();
			 * dynaForm.set("unitValue",cgpanRef); dynaForm.set("none","cgpan");
			 * 
			 * }else {
			 * 
			 * String
			 * cgbidRef=application.getBorrowerDetails().getSsiDetails().getCgbid
			 * (); dynaForm.set("unitValue",cgbidRef);
			 * dynaForm.set("none","cgbid"); }
			 * dynaForm.set("previouslyCovered","Y"); }
			 */

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting constitution Others on the screen
			String constitutionVal = ssiDetails.getConstitution();
			if (constitutionVal != null && !(constitutionVal.equals(""))) {
				if (!(constitutionVal.equals("proprietary"))
						&& !(constitutionVal.equals("partnership"))
						&& !(constitutionVal.equals("private"))
						&& !(constitutionVal.equals("public"))) {
					dynaForm.set("constitutionOther", constitutionVal);
					dynaForm.set("constitution", "Others");
				} else {
					dynaForm.set("constitution", constitutionVal);
				}

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting legal ID
			String legalIDString = ssiDetails.getCpLegalID();
			if (legalIDString != null && !(legalIDString.equals(""))) {
				if (!(legalIDString.equals("VoterIdentityCard"))
						&& !(legalIDString.equals("RationCardnumber"))
						&& !(legalIDString.equals("PASSPORT"))
						&& !(legalIDString.equals("Driving License"))) {
					dynaForm.set("otherCpLegalID", legalIDString);
					dynaForm.set("cpLegalID", "Others");
				} else {
					dynaForm.set("cpLegalID", legalIDString);
				}

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// Setting subsidy / Equity Name
			String subsidyEquityName = projectOutlayDetails.getSubsidyName();
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Subsidy Name :" + projectOutlayDetails.getSubsidyName());
			if (subsidyEquityName != null && !(subsidyEquityName.equals(""))) {
				if (!(subsidyEquityName.equals("PMRY"))
						&& !(subsidyEquityName.equals("SJRY"))) {
					dynaForm.set("otherSubsidyEquityName", subsidyEquityName);
					dynaForm.set("subsidyName", "Others");
				} else {
					dynaForm.set("subsidyName", subsidyEquityName);
				}
			}

			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			// The states the populated on the screen while retreival
			ArrayList statesList = (ArrayList) getStateList();
			dynaForm.set("statesList", statesList);

			// The district for the particular state has to be populated
			String state = (String) dynaForm.get("state");
			ArrayList districtList = admin.getAllDistricts(state);
			dynaForm.set("districtList", districtList);
			// int districtSize=districtList.size();
			String districtName = ssiDetails.getDistrict();

			if (districtList.contains(districtName)) {
				dynaForm.set("district", districtName);
			} else {
				dynaForm.set("districtOthers", districtName);
				dynaForm.set("district", "Others");

			}
			Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
					"Set Application Details...");

			ArrayList socialList = getSocialCategory();
			dynaForm.set("socialCategoryList", socialList);

			/*
			 * ArrayList industryNatureList=getIndustryNature();
			 * dynaForm.set("industryNatureList",industryNatureList);
			 * 
			 * String industryNature=(String)dynaForm.get("industryNature");
			 * if(industryNature!=null && !(industryNature.equals(""))) {
			 * ArrayList
			 * industrySectorList=admin.getIndustrySectors(industryNature);
			 * dynaForm.set("industrySectorList",industrySectorList);
			 * 
			 * }
			 */dynaForm.set("industryNature", ssiDetails.getIndustryNature());
			dynaForm.set("industrySector", ssiDetails.getIndustrySector());

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			ArrayList participatingBanks = mcgsProcessor
					.getAllParticipatingBanks(mliId);
			dynaForm.set("participatingBanks", participatingBanks);

			dynaForm.set("previouslyCovered", "Y");
			dynaForm.set("none", "cgpan");
			dynaForm.set("unitValue", cgpan);

			dynaForm.set("mSE", application.getMsE());

			System.out.println("mse IN APPOLD APP " + application.getMsE());
			// If the application to be modified is a term loan
			if (applicationLoanType.equals(ApplicationConstants.TC_APPLICATION)) {
				/*
				 * if (mcgfDetails!=null) {
				 * appSession1.setAttribute(SessionConstants.MCGF_FLAG,"M"); }
				 */
				appSession1.setAttribute(
						SessionConstants.APPLICATION_TYPE_FLAG, "11");
				pageForward = "tcForward";

				// If the application to be modified is a Working Capital loan
			} else if (applicationLoanType
					.equals(ApplicationConstants.WC_APPLICATION)) {

				/*
				 * if (mcgfDetails!=null) {
				 * appSession1.setAttribute(SessionConstants.MCGF_FLAG,"M"); }
				 */
				appSession1.setAttribute(
						SessionConstants.APPLICATION_TYPE_FLAG, "12");
				pageForward = "wcForward";

				// If the application to be modified is a Composite loan
			} else if (applicationLoanType
					.equals(ApplicationConstants.CC_APPLICATION)) {

				/*
				 * if (mcgfDetails!=null) {
				 * appSession1.setAttribute(SessionConstants.MCGF_FLAG,"M"); }
				 */
				appSession1.setAttribute(
						SessionConstants.APPLICATION_TYPE_FLAG, "13");
				pageForward = "ccForward";
			}
		}

		Log.log(Log.DEBUG, "ApplicationProcessingAction", "showCgpanList",
				"Page to be forwaded :" + pageForward);
		Log.log(Log.INFO, "ApplicationProcessingAction", "showCgpanList",
				"Exited");

		application = null;
		borrowerDetails = null;
		ssiDetails = null;
		mcgfDetails = null;
		termLoan = null;
		workingCapital = null;

		dynaForm.set("cgbid", null);

		return mapping.findForward(pageForward);
	}

	public ActionForward showTCAppDetailsForConv(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = "";

		HttpSession session = request.getSession(false);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;

		apForm.resetTCConvMaps();

		ArrayList tcAppForConv = appProcessor.getCountForTCConv();

		if (tcAppForConv == null || tcAppForConv.size() == 0) {
			request.setAttribute("message",
					"No Term Loan Applications available for Conversion");
			forward = "success";
		} else {

			apForm.setTcApplications(tcAppForConv);

			forward = "tcConvPage";

		}
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");

		return mapping.findForward(forward);
	}

	public ActionForward showWCAppDetailsForConv(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = "";

		HttpSession session = request.getSession(false);

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		APForm apForm = (APForm) form;

		apForm.resetWCConvMaps();

		ArrayList wcAppForConv = appProcessor.getCountForWCConv();

		if (wcAppForConv == null || wcAppForConv.size() == 0) {
			request.setAttribute("message",
					"No Working Capital Applications available for Conversion");
			forward = "success";
		} else {

			apForm.setWcApplications(wcAppForConv);
			forward = "wcConvPage";
		}

		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");

		return mapping.findForward(forward);
	}

	/**
	 * Fix Bug Id 55 on 2/9/2004
	 */

	public ActionForward afterTcConversion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "afterTcConversion",
				"Entered");

		String forward = "";

		APForm apForm = (APForm) form;
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO appDAO = new ApplicationDAO();

		ArrayList tcConvertedApps = new ArrayList();

		Map tcAppRefNo = apForm.getTcAppRefNo();
		Map tcCgpan = apForm.getTcCgpan();
		Map tcDecision = apForm.getTcDecision();

		Set tcAppRefNoSet = tcAppRefNo.keySet();
		Set tcCgpanSet = tcCgpan.keySet();
		Set tcDecisionSet = tcDecision.keySet();

		Iterator tcAppRefNoIterator = tcAppRefNoSet.iterator();
		/*
		 * Iterator tcCgpanIterator = tcCgpanSet.iterator(); Iterator
		 * tcDecisionIterator = tcDecisionSet.iterator();
		 */
		while (tcAppRefNoIterator.hasNext()) {
			String key = (String) tcAppRefNoIterator.next();

			String appRefNo = (String) tcAppRefNo.get(key);
			String cgpanNumber = (String) tcCgpan.get(key);
			String decision = (String) tcDecision.get(key);

			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterTcConversion", "appRefNo :" + appRefNo);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterTcConversion", "appRefNo :" + appRefNo);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterTcConversion", "decision :" + decision);

			Application cgpanApplication = new Application();

			int cgpanSSIRef;
			String cgpanSubScheme = "";

			if (decision != null && decision.equals("ATL")) {
				Application application = appProcessor.getPartApplication(null,
						"", appRefNo);
				int appSSIRef = application.getBorrowerDetails()
						.getSsiDetails().getBorrowerRefNo();

				ArrayList cgpans = appDAO.getAllCgpans();
				if (!(cgpans.contains(cgpanNumber))) {
					throw new MessageException(cgpanNumber
							+ " is an invalid CGPAN");
				}

				try {
					// checking if the cgpan exists for the MLI ID passed

					cgpanApplication = appProcessor.getApplication(application
							.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12),
							cgpanNumber, "");
					cgpanSSIRef = cgpanApplication.getBorrowerDetails()
							.getSsiDetails().getBorrowerRefNo();

					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterTcConversion", "cgpanSSIRef :" + cgpanSSIRef);
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterTcConversion", "cgpanSubScheme :"
									+ cgpanSubScheme);

					if (!cgpanApplication.getStatus().equals("AP")
							&& !cgpanApplication.getStatus().equals("EX")) {
						throw new MessageException(cgpanNumber
								+ " is not an Approved / Expired Application");
					}

				} catch (MessageException MessageException) {

					throw new MessageException("The CGPAN :" + cgpanNumber
							+ " does not belong the Member "
							+ application.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12));

				}

				if (!cgpanNumber.substring(11, 13).equals("TC")) {
					throw new MessageException(cgpanNumber
							+ " should be a Term Loan CGPAN");
				}

				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				Application riskApplication = new Application();
				BorrowerDetails riskBorrowerDetails = new BorrowerDetails();
				SSIDetails riskSSIDetails = new SSIDetails();
				riskBorrowerDetails.setSsiDetails(riskSSIDetails);
				riskApplication.setBorrowerDetails(riskBorrowerDetails);

				riskApplication.setMliID(cgpanApplication.getMliID());

				BorrowerDetails rBorrowerDetails = riskApplication
						.getBorrowerDetails();
				SSIDetails rSSIDetails = rBorrowerDetails.getSsiDetails();
				rSSIDetails.setState(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getState());
				rSSIDetails.setIndustryNature(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getIndustryNature());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterTcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getCpGender() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getCpGender());
				rSSIDetails.setCpGender(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getCpGender());
				rSSIDetails.setSocialCategory(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getSocialCategory());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterTcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getSocialCategory() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getSocialCategory());
				rBorrowerDetails.setSsiDetails(rSSIDetails);
				riskApplication.setBorrowerDetails(rBorrowerDetails);

				String subSchemeName = rpProcessor
						.getSubScheme(riskApplication);

				Application tcApplication = new Application();
				BorrowerDetails tcBorrowerDetails = new BorrowerDetails();
				SSIDetails tcSsiDetails = new SSIDetails();

				tcSsiDetails.setBorrowerRefNo(cgpanSSIRef);
				tcBorrowerDetails.setSsiDetails(tcSsiDetails);
				tcApplication.setBorrowerDetails(tcBorrowerDetails);

				tcApplication.setAppRefNo(appRefNo);
				tcApplication.setCgpan(cgpanNumber);
				tcApplication.setSubSchemeName(subSchemeName);

				appProcessor.updateTCConv(tcApplication, appSSIRef);

				tcConvertedApps.add(tcApplication);
				apForm.setTcConvertedApplications(tcConvertedApps);

				application = null;
				cgpanApplication = null;
			}

		}

		return mapping.findForward("success");
	}

	/**
	 * Fix is completed
	 **/

	/**
	 * To fix bug 55 - 2/9/2004
	 */
	public ActionForward afterWcConversion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ApplicationProcessingAction", "afterWcConversion",
				"Entered");

		APForm apForm = (APForm) form;
		ApplicationProcessor appProcessor = new ApplicationProcessor();
		ApplicationDAO appDAO = new ApplicationDAO();

		ArrayList enhanceConvertedApps = new ArrayList();
		ArrayList renewConvertedApps = new ArrayList();

		Map wcAppRefNo = new TreeMap();
		Map wcCgpan = new TreeMap();
		Map wcDecision = new TreeMap();

		wcAppRefNo = apForm.getWcAppRefNo();
		wcCgpan = apForm.getWcCgpan();
		wcDecision = apForm.getWcDecision();

		Set wcAppRefNoSet = wcAppRefNo.keySet();
		Set wcCgpanSet = wcAppRefNo.keySet();
		Set wcDecisionSet = wcDecision.keySet();

		Iterator wcAppRefNoIterator = wcAppRefNoSet.iterator();
		/*
		 * Iterator wcCgpanIterator = wcCgpanSet.iterator(); Iterator
		 * wcDecisionIterator = wcDecisionSet.iterator();
		 */
		Log.log(Log.INFO, "ApplicationProcessingAction", "afterWcConversion",
				"wcAppRefNo :" + wcAppRefNo.size());

		while (wcAppRefNoIterator.hasNext()) {
			String key = (String) wcAppRefNoIterator.next();

			String appRefNo = (String) wcAppRefNo.get(key);
			String cgpanNumber = (String) wcCgpan.get(key);
			String decision = (String) wcDecision.get(key);

			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterWcConversion", "key :" + key);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterWcConversion", "cgpanNumber :" + cgpanNumber);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterWcConversion", "appRefNo :" + appRefNo);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"afterWcConversion", "decision :" + decision);

			Application cgpanApplication = new Application();

			int cgpanSSIRef;
			String cgpanSubScheme = "";

			if (decision.equals("WCR")) {
				Application application = appProcessor.getApplication(null, "",
						appRefNo);
				int appSSIRef = application.getBorrowerDetails()
						.getSsiDetails().getBorrowerRefNo();

				ArrayList cgpans = appDAO.getAllCgpans();
				if (!(cgpans.contains(cgpanNumber))) {
					throw new MessageException(cgpanNumber
							+ " is an invalid CGPAN");
				}

				String renewcgpan = appProcessor.checkRenewCgpan(cgpanNumber);
				if (renewcgpan.equals("0")) {
					renewcgpan = cgpanNumber;
				}
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion", "renewcgpan :" + renewcgpan);
				try {
					// checking if the cgpan exists for the MLI ID passed

					cgpanApplication = appProcessor.getApplication(application
							.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12),
							renewcgpan, "");
					cgpanSSIRef = cgpanApplication.getBorrowerDetails()
							.getSsiDetails().getBorrowerRefNo();

					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterWcConversion", "cgpanSSIRef :" + cgpanSSIRef);
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterWcConversion", "cgpanSubScheme :"
									+ cgpanSubScheme);

					if (!cgpanApplication.getStatus().equals("EX")) {
						throw new MessageException(cgpanNumber
								+ " is not an Expired Application");
					}

				} catch (MessageException MessageException) {

					throw new MessageException("The CGPAN :" + cgpanNumber
							+ " does not belong the Member "
							+ application.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12));

				}

				if (!cgpanNumber.substring(11, 13).equals("WC")
						&& !cgpanNumber.substring(11, 12).equals("R")) {
					throw new MessageException(cgpanNumber
							+ " should be a Working Capital CGPAN");
				}

				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				Application riskApplication = new Application();
				BorrowerDetails riskBorrowerDetails = new BorrowerDetails();
				SSIDetails riskSSIDetails = new SSIDetails();
				riskBorrowerDetails.setSsiDetails(riskSSIDetails);
				riskApplication.setBorrowerDetails(riskBorrowerDetails);

				riskApplication.setMliID(cgpanApplication.getMliID());

				BorrowerDetails rBorrowerDetails = riskApplication
						.getBorrowerDetails();
				SSIDetails rSSIDetails = rBorrowerDetails.getSsiDetails();
				rSSIDetails.setState(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getState());
				rSSIDetails.setIndustryNature(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getIndustryNature());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getCpGender() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getCpGender());
				rSSIDetails.setCpGender(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getCpGender());
				rSSIDetails.setSocialCategory(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getSocialCategory());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getSocialCategory() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getSocialCategory());
				rBorrowerDetails.setSsiDetails(rSSIDetails);
				riskApplication.setBorrowerDetails(rBorrowerDetails);

				String subSchemeName = rpProcessor
						.getSubScheme(riskApplication);

				Application wcApplication = new Application();
				BorrowerDetails wcBorrowerDetails = new BorrowerDetails();
				SSIDetails wcSsiDetails = new SSIDetails();

				wcSsiDetails.setBorrowerRefNo(cgpanSSIRef);
				wcBorrowerDetails.setSsiDetails(wcSsiDetails);
				wcApplication.setBorrowerDetails(wcBorrowerDetails);

				wcApplication.setAppRefNo(appRefNo);
				wcApplication.setCgpan(renewcgpan);
				wcApplication.setSubSchemeName(subSchemeName);

				appProcessor.updateTCConv(wcApplication, appSSIRef);

				renewConvertedApps.add(wcApplication);
				apForm.setRenewConvertedApplications(renewConvertedApps);

				wcApplication = null;
				wcBorrowerDetails = null;
				wcSsiDetails = null;

				riskApplication = null;
				riskBorrowerDetails = null;
				riskSSIDetails = null;
				rBorrowerDetails = null;
				rSSIDetails = null;

			} else if (decision.equals("WCE")) {
				Application application = appProcessor.getApplication(null, "",
						appRefNo);
				int appSSIRef = application.getBorrowerDetails()
						.getSsiDetails().getBorrowerRefNo();

				ArrayList cgpans = appDAO.getAllCgpans();
				if (!(cgpans.contains(cgpanNumber))) {
					throw new MessageException(cgpanNumber
							+ " is an invalid CGPAN");
				}
				try {
					// checking if the cgpan exists for the MLI ID passed

					cgpanApplication = appProcessor.getApplication(application
							.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12),
							cgpanNumber, "");
					cgpanSSIRef = cgpanApplication.getBorrowerDetails()
							.getSsiDetails().getBorrowerRefNo();

					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterWcConversion", "cgpanSSIRef :" + cgpanSSIRef);
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterWcConversion", "cgpanSubScheme :"
									+ cgpanSubScheme);

					if (!cgpanApplication.getStatus().equals("AP")) {
						throw new MessageException(cgpanNumber
								+ " is not an Approved Application");
					}

				} catch (MessageException MessageException) {
					Log.log(Log.INFO, "ApplicationProcessingAction",
							"afterWcConversion", "excpetion :"
									+ MessageException.getMessage());

					throw new MessageException("The CGPAN :" + cgpanNumber
							+ " does not belong the Member "
							+ application.getMliID().substring(0, 4)
							+ application.getMliID().substring(4, 8)
							+ application.getMliID().substring(8, 12));

				}

				if (!cgpanNumber.substring(11, 13).equals("WC")
						&& !cgpanNumber.substring(11, 12).equals("R")) {
					throw new MessageException(cgpanNumber
							+ " should be a Working Capital CGPAN");
				}

				if (cgpanApplication.getProjectOutlayDetails()
						.getWcFundBasedSanctioned() > application
						.getProjectOutlayDetails().getWcFundBasedSanctioned()) {
					throw new MessageException(
							"The Working Capital Fund Based Sanctioned Amount should be greater than the existing Fund Based Sanctioned Amount");
				}

				if (cgpanApplication.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned() > application
						.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned()) {
					throw new MessageException(
							"The Working Capital Non Fund Based Sanctioned Amount should be greater than the existing Non Fund Based Sanctioned Amount");
				}

				if (cgpanApplication.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned()
						+ cgpanApplication.getProjectOutlayDetails()
								.getWcFundBasedSanctioned() > application
						.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned()
						+ application.getProjectOutlayDetails()
								.getWcFundBasedSanctioned()) {
					throw new MessageException(
							"Total Enhanced Amount should not be lesser than Total Existing Amount");
				}

				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				Application riskApplication = new Application();
				BorrowerDetails riskBorrowerDetails = new BorrowerDetails();
				SSIDetails riskSSIDetails = new SSIDetails();
				riskBorrowerDetails.setSsiDetails(riskSSIDetails);
				riskApplication.setBorrowerDetails(riskBorrowerDetails);

				riskApplication.setMliID(cgpanApplication.getMliID());

				BorrowerDetails rBorrowerDetails = riskApplication
						.getBorrowerDetails();
				SSIDetails rSSIDetails = rBorrowerDetails.getSsiDetails();
				rSSIDetails.setState(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getState());
				rSSIDetails.setIndustryNature(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getIndustryNature());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getCpGender() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getCpGender());
				rSSIDetails.setCpGender(cgpanApplication.getBorrowerDetails()
						.getSsiDetails().getCpGender());
				rSSIDetails.setSocialCategory(cgpanApplication
						.getBorrowerDetails().getSsiDetails()
						.getSocialCategory());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion",
						"cgpanApplication.getBorrowerDetails().getSsiDetails().getSocialCategory() :"
								+ cgpanApplication.getBorrowerDetails()
										.getSsiDetails().getSocialCategory());
				rBorrowerDetails.setSsiDetails(rSSIDetails);
				riskApplication.setBorrowerDetails(rBorrowerDetails);

				String subSchemeName = rpProcessor
						.getSubScheme(riskApplication);

				Application wcApplication = new Application();
				BorrowerDetails wcBorrowerDetails = new BorrowerDetails();
				SSIDetails wcSsiDetails = new SSIDetails();
				WorkingCapital wcDetails = new WorkingCapital();

				wcSsiDetails.setBorrowerRefNo(cgpanSSIRef);
				wcBorrowerDetails.setSsiDetails(wcSsiDetails);
				wcDetails.setFundBasedLimitSanctioned(application
						.getProjectOutlayDetails().getWcFundBasedSanctioned()
						- cgpanApplication.getProjectOutlayDetails()
								.getWcFundBasedSanctioned());
				wcDetails.setNonFundBasedLimitSanctioned(application
						.getProjectOutlayDetails()
						.getWcNonFundBasedSanctioned()
						- cgpanApplication.getProjectOutlayDetails()
								.getWcNonFundBasedSanctioned());
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"afterWcConversion",
						"difference :"
								+ (cgpanApplication.getProjectOutlayDetails()
										.getWcFundBasedSanctioned()));
				Log.log(Log.INFO,
						"ApplicationProcessingAction",
						"afterWcConversion",
						"application difference :"
								+ (application.getProjectOutlayDetails()
										.getWcFundBasedSanctioned()));
				wcDetails.setWcInterestRate(application.getWc()
						.getLimitFundBasedInterest());
				wcDetails.setLimitNonFundBasedCommission(application.getWc()
						.getLimitNonFundBasedCommission());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion", "interest rate :"
								+ cgpanApplication.getWc()
										.getLimitFundBasedInterest());
				wcDetails.setWcInterestType(application.getWc()
						.getWcInterestType());
				Log.log(Log.INFO, "ApplicationProcessingAction",
						"afterWcConversion", "interest type :"
								+ cgpanApplication.getWc().getWcInterestType());
				wcApplication.setBorrowerDetails(wcBorrowerDetails);
				wcApplication.setWc(wcDetails);

				wcApplication.setAppRefNo(appRefNo);
				wcApplication.setCgpan(cgpanNumber);
				wcApplication.setSubSchemeName(subSchemeName);
				wcApplication.setStatus("EN");

				appProcessor.updateWCConv(wcApplication, appSSIRef);

				enhanceConvertedApps.add(wcApplication);
				apForm.setEnhanceConvertedApplications(enhanceConvertedApps);

				wcApplication = null;
				wcBorrowerDetails = null;
				wcSsiDetails = null;

				riskApplication = null;
				riskBorrowerDetails = null;
				riskSSIDetails = null;
				rBorrowerDetails = null;
				rSSIDetails = null;

			}
			cgpanApplication = null;
			application = null;

		}

		return mapping.findForward(Constants.SUCCESS);
	}
	/**
	 * Fix is completed
	 **/
	public HashMap getCollateralamouts(String bankName) throws DatabaseException{
		System.out.println("bankName>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getCollateralamouts>>>>>>>>>>>>"+bankName);
		ApplicationDAO applicationDAO = new ApplicationDAO();		
		HashMap<String,Double> colleralMap = applicationDAO.getImmovableDeatils(bankName); //new HashMap<String,Double>();
		System.out.println("colleralMap????????????????????????????????????????????????SIZE>>>>>>>>>>>>>>>>>>>>"+colleralMap.size());				
				return colleralMap;	
	}
	
	
	public ActionForward showAppsForApprovalForMLIWise(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		session.removeAttribute(SessionConstants.APPLICATION_LOAN_TYPE);
		session.setAttribute(SessionConstants.APPLICATION_TYPE, "APP");

		User user = getUserInformation(request);

		String userId = user.getUserId();

		String forward = "";

		String bank = "";

		String bank1 = request.getParameter("Link");
		System.out.println("APA bank1 : "+bank1);
		// bank1 = bank1 + "$";
		if (bank1.length() > 1) {
			bank = bank1.replaceAll("PATH", "&");
			System.out.println("APA bank : "+bank);
		}

		ApplicationProcessor appProcessor = new ApplicationProcessor();
		// EligibleApplication eligibleApplication=new EligibleApplication();
		// DuplicateApplication duplicateApplication=new DuplicateApplication();
		// Application application = new Application();

		APForm apForm = (APForm) form;

		apForm.resetMaps();

		TreeMap tempStatusMap = new TreeMap();
		TreeMap clearStatusMap = new TreeMap();
		TreeMap dupStatusMap = new TreeMap();
		TreeMap ineligibleStatusMap = new TreeMap();

		TreeMap tempRemarksMap = new TreeMap();
		TreeMap clearRemarksMap = new TreeMap();
		TreeMap dupRemarksMap = new TreeMap();
		TreeMap ineligibleRemarksMap = new TreeMap();
try
{
		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApproval",
				"Before callin getappsforapproval frm processor");
		System.out.println("APA getApplicationsForApprovalForMLIWise start");
		ArrayList applicationsList = appProcessor
				.getApplicationsForApprovalForMLIWise(userId, bank);
		
	       HashMap colleralMap = new HashMap();
		
		colleralMap = getCollateralamouts(bank);
		System.out.println("APA getApplicationsForApprovalForMLIWise end");
		// retrieving the application count
		Integer intAppCount = (Integer) applicationsList.get(4);
		int appCount = intAppCount.intValue();
		apForm.setIntApplicationCount(appCount);

		// double approvedAmount=0;
		ArrayList eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		ArrayList eligibleDupApps = (ArrayList) applicationsList.get(1);

		Log.log(Log.INFO, "ApplicationProcessingAction",
				"getApplicationsForApproval",
				"After callin getappsforapproval frm processor");
		ArrayList messagesTitleList = appProcessor.getMessageTitleContent();
		ArrayList messagesList = (ArrayList) messagesTitleList.get(1);
		apForm.setSpecialMessagesList(messagesList);
		messagesTitleList = null;
		messagesList = null;

		// ArrayList appRefNosList=new ArrayList();

		eligibleNonDupApps = (ArrayList) applicationsList.get(0);
		// System.out.println("eligibleNonDupApps.size"+eligibleNonDupApps.size());
		for (int i = 0; i < eligibleNonDupApps.size(); i++) {
			Application tempApplication = (Application) eligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempApplication.getAppRefNo();
			String tempStatus = tempApplication.getStatus();
			String remarks = tempApplication.getRemarks();

			clearStatusMap.put(tempAppRefNo, tempStatus);
			clearRemarksMap.put(tempAppRefNo, remarks);

			apForm.setClearTempMap(clearStatusMap);
			apForm.setClearRemMap(clearRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		eligibleDupApps = (ArrayList) applicationsList.get(1);
		// System.out.println("eligibleDupApps.size()"+eligibleDupApps.size());
		for (int i = 0; i < eligibleDupApps.size(); i++) {
			DuplicateApplication tempDupApplication = (DuplicateApplication) eligibleDupApps
					.get(i);
			String tempAppRefNo = tempDupApplication.getNewAppRefNo();
			String tempStatus = tempDupApplication.getStatus();
			String remarks = tempDupApplication.getDuplicateRemarks();
 
			tempDupApplication.setImmovCollateratlSecurityAmt((Double)colleralMap.get(tempAppRefNo));
			HashMap<String,Double> itemMap = new HashMap<String,Double>(); 
			itemMap.put(tempAppRefNo, tempDupApplication.getImmovCollateratlSecurityAmt()); 
		     apForm.setColleralAmtMap(itemMap);
			dupStatusMap.put(tempAppRefNo, tempStatus);
			dupRemarksMap.put(tempAppRefNo, remarks);

			apForm.setDupTempMap(dupStatusMap);
			apForm.setDupRemMap(dupRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleNonDupApps = (ArrayList) applicationsList.get(2);
		// System.out.println("ineligibleNonDupApps.size"+ineligibleNonDupApps.size());
		for (int i = 0; i < ineligibleNonDupApps.size(); i++) {
			EligibleApplication tempEligibleApplication = (EligibleApplication) ineligibleNonDupApps
					.get(i);
			String tempAppRefNo = tempEligibleApplication.getAppRefNo();
			String tempStatus = tempEligibleApplication.getStatus();
			String remarks = tempEligibleApplication.getEligibleRemarks();

			ineligibleStatusMap.put(tempAppRefNo, tempStatus);
			ineligibleRemarksMap.put(tempAppRefNo, remarks);

			apForm.setIneligibleTempMap(ineligibleStatusMap);
			apForm.setIneligibleRemMap(ineligibleRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
		}

		ArrayList ineligibleDupApps = (ArrayList) applicationsList.get(3);
		// System.out.println("ineligibleDupApps.size()"+ineligibleDupApps.size());
		for (int i = 0; i < ((ArrayList) ineligibleDupApps.get(0)).size(); i++) {
			DuplicateApplication duplicateApplication = (DuplicateApplication) ((ArrayList) ineligibleDupApps
					.get(0)).get(i);
			String tempAppRefNo = duplicateApplication.getNewAppRefNo();
			String tempStatus = duplicateApplication.getStatus();
			String remarks = duplicateApplication.getDuplicateRemarks();
duplicateApplication.setImmovCollateratlSecurityAmt((Double)colleralMap.get(tempAppRefNo));
			
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval",
					"remarks of the ineligible and duplicate:" + remarks);

			tempStatusMap.put(tempAppRefNo, tempStatus);
			tempRemarksMap.put(tempAppRefNo, remarks);
			
			HashMap<String,Double> itemMap = new HashMap<String,Double>(); 
			itemMap.put(tempAppRefNo, duplicateApplication.getImmovCollateratlSecurityAmt());   
			
            apForm.setColleralAmtMap(itemMap);   // CBD
			apForm.setTempMap(tempStatusMap);
			apForm.setTempRemMap(tempRemarksMap);
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "apForm Map size :"
							+ apForm.getTempMap().size());
			itemMap=null;
		}
		
		ArrayList privilegeList = user.getPrivileges();
		if (privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			Log.log(Log.INFO, "ApplicationProcessingAction",
					"getApplicationsForApproval", "has privilege");

			apForm.setIneligibleNonDupApps(ineligibleNonDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")) {
			apForm.setEligibleDupApps(eligibleDupApps);
		}
		if (privilegeList.contains("APPROVE_DUPLICATE_APPLICATION")
				&& privilegeList.contains("APPROVE_INELIGIBLE_APPLICATION")) {
			apForm.setIneligibleDupApps(ineligibleDupApps);
		}

		// apForm.setIneligibleDupApps(ineligibleDupApps);
		apForm.setEligibleNonDupApps(eligibleNonDupApps);

		if ((apForm.getIneligibleNonDupApps() == null || apForm
				.getIneligibleNonDupApps().size() == 0)
				&& (apForm.getEligibleDupApps() == null || apForm
						.getEligibleDupApps().size() == 0)
				&& (apForm.getEligibleNonDupApps() == null || apForm
						.getEligibleNonDupApps().size() == 0)
				&& (((ArrayList) apForm.getIneligibleDupApps().get(0)).size() == 0 || (ArrayList) apForm
						.getIneligibleDupApps().get(0) == null)) {
			forward = "success";
			request.setAttribute("message",
					"No Applications for Approval Available");
		} else {

			forward = "approvalPagenew";
		}

		appProcessor = null;
		applicationsList = null;

		Log.log(Log.INFO, "ApplicationProcessingAction", "showAppsForApproval",
				"Exited. Memory : " + Runtime.getRuntime().freeMemory());
			  }catch(Exception ex) {
			    	System.out.println(ex.getMessage());
				
					ex.printStackTrace();
				}
		return mapping.findForward(forward);
	}


}
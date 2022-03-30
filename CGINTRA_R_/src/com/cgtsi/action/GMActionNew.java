package com.cgtsi.action;

import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.admin.User;
import com.cgtsi.application.ApplicationConstants;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.guaranteemaintenance.CgpanDetail;
import com.cgtsi.guaranteemaintenance.ClosureDetail;
import com.cgtsi.guaranteemaintenance.GMDAO;
import com.cgtsi.guaranteemaintenance.GMProcessor;
import com.cgtsi.receiptspayments.DANSummary;
import com.cgtsi.receiptspayments.DemandAdvice;
import com.cgtsi.receiptspayments.MissingDANDetailsException;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.registration.NoMemberFoundException;
//import com.cgtsi.reports.ApplicationReport;
import com.cgtsi.reports.ApplicationReport;
import com.cgtsi.reports.ReportDAO;
import com.cgtsi.reports.ReportManager;
import com.cgtsi.util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.struts.validator.DynaValidatorActionForm;
import java.sql.Types;
import java.sql.CallableStatement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

// Referenced classes of package com.cgtsi.action:
//            BaseAction

public class GMActionNew extends BaseAction {
	public GMActionNew() {
	}

	public ActionForward submitSSIDetailForTenure(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMActionNew", "submitSSIDetailForTenure", "Entered");
		GMActionForm gmActionForm = (GMActionForm) form;
		User user = getUserInformation(request);
		String memberId = gmActionForm.getMemberIdForClosure();

		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		String reviseOfTenure = gmActionForm.getReviseOfTenure();
		String modificationOfRemarks = gmActionForm.getModificationOfRemarks();
		String cgpan = gmActionForm.getCgpanForClosure();
		String tenure = gmActionForm.getTenure();
		String userID = user.getUserId();
		String lastDateOfPayment = gmActionForm.getLastDateOfPayment();
		if ((reviseOfTenure == null) || (reviseOfTenure.equals(""))) {

			throw new NoDataException("reviseOfTenure  is Required");

		}

		if ((modificationOfRemarks == null)
				|| (modificationOfRemarks.equals(""))) {

			throw new NoDataException("modificationOfRemarks  is Required");

		}
		if ((lastDateOfPayment == null) || (lastDateOfPayment.equals(""))) {

			throw new NoDataException("lastDateOfPayment  is Required");

		}
		Connection connection = DBConnection.getConnection(false);
		CallableStatement setTenureDetail = null;
		ResultSet resultSet = null;
		String exception = "";
		String functionName = null;
		try {

			functionName = "{?=call Funcinstenuremodreq(?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,?,?)}";
			setTenureDetail = connection.prepareCall(functionName);
			setTenureDetail.registerOutParameter(1, java.sql.Types.INTEGER);
			setTenureDetail.setString(2, bankId);
			setTenureDetail.setString(3, zoneId);
			setTenureDetail.setString(4, branchId);
			setTenureDetail.setString(5, cgpan);
			setTenureDetail.setString(6, tenure);
			setTenureDetail.setString(7, reviseOfTenure);
			setTenureDetail.setString(8, lastDateOfPayment);
			setTenureDetail.setString(9, modificationOfRemarks);
			setTenureDetail.setString(10, userID);
			setTenureDetail.registerOutParameter(11, java.sql.Types.VARCHAR);
			setTenureDetail.executeQuery();
			int error = setTenureDetail.getInt(1);
			exception = setTenureDetail.getString(11);
			Log.log(Log.DEBUG, "GMActionNew", "submitSSIDetailForTenure",
					"errorCode " + error);

			if (error == Constants.FUNCTION_FAILURE) {
				setTenureDetail.close();
				setTenureDetail = null;
				connection.rollback();
				Log.log(Log.ERROR, "GMActionNew", "submitSSIDetailForTenure",
						"error in SP " + exception);

				throw new DatabaseException(exception);
			} else {

			}

			gmActionForm.setReviseOfTenure("");
			gmActionForm.setLastDateOfPayment("");
			gmActionForm.setModificationOfRemarks("");
		} catch (SQLException e) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "GMActionNew", "reactivate User",
						ignore.getMessage());
			}

			Log.log(Log.ERROR, "GMActionNew", "reactivate User", e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to reactivate user");

		} finally {

			DBConnection.freeConnection(connection);
		}

		return mapping.findForward("success");

	}

	public ActionForward requestModifyTenure(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

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
		// gmActionForm.setClosureRemarks("");
		gmActionForm.setCgpanForClosure("");

		return mapping.findForward("requestModifyClosure");

	}

	public ActionForward getUnitForTenureRequest(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "Request for Modification of tenure",
				"Entered");
		String cgpan = null;
		GMActionForm gmActionForm = (GMActionForm) form;
		User user = getUserInformation(request);

		String bankId = null;
		String zoneId = null;
		String branchId = null;

		String memberId = gmActionForm.getMemberIdForClosure();

		cgpan = gmActionForm.getCgpanForClosure();

		ApplicationReport appReport = new ApplicationReport();
		if ((memberId == null) || (memberId.equals(""))) {

			throw new NoDataException("Member Id is Required");

		} else if ((cgpan == null) || (cgpan.equals(""))) {

			throw new NoDataException("Cgpan  is Required");
		} else if (memberId.length() < 12) {
			throw new NoDataException(
					"Member Id can not be less than 12 characters");
		} else {
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
		}
		cgpan = gmActionForm.getCgpanForClosure();
		gmActionForm.setApplicationReport(appReport);

		Connection connection = DBConnection.getConnection(false);
		CallableStatement getTenureDetail = null;
		ResultSet resultSet = null;

		try {

			String exception = "";
			String functionName = null;
			functionName = "{?=call Funcgetssidetailfortenure(?,?,?,?,?,?,?,?,?,?,?)}";

			getTenureDetail = connection.prepareCall(functionName);
			getTenureDetail.registerOutParameter(1, java.sql.Types.INTEGER);
			getTenureDetail.setString(2, bankId);
			getTenureDetail.setString(3, zoneId);
			getTenureDetail.setString(4, branchId);
			getTenureDetail.setString(5, cgpan);
			getTenureDetail.registerOutParameter(6, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(7, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(8, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(9, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(10, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(11, java.sql.Types.VARCHAR);
			getTenureDetail.registerOutParameter(12, java.sql.Types.VARCHAR);
			getTenureDetail.executeQuery();

			// Log.log(Log.DEBUG,"GMDAO","repayment detail","exception "+exception);
			int error = getTenureDetail.getInt(1);
			exception = getTenureDetail.getString(12);
			Log.log(Log.DEBUG, "GMActionNEW",
					"Request for Modification of tenure", "errorCode "
							+ exception);

			if (error == Constants.FUNCTION_FAILURE) {
				getTenureDetail.close();
				getTenureDetail = null;
				connection.rollback();
				Log.log(Log.ERROR, "GMActionNEW", "Funcgetssidetailfortenure",
						"error in SP " + exception);

				throw new DatabaseException(exception);
			} else {

				gmActionForm.setBankName(getTenureDetail.getString(6));
				gmActionForm.setZoneName(getTenureDetail.getString(7));
				gmActionForm.setBranchName(getTenureDetail.getString(8));
				gmActionForm.setUnitName(getTenureDetail.getString(9));
				gmActionForm.setTenure(getTenureDetail.getString(10));
				gmActionForm.setExpiryDate(getTenureDetail.getString(11));
			}

		} catch (SQLException e) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "GMActionNew", "reactivate User",
						ignore.getMessage());
			}

			Log.log(Log.ERROR, "GMActionNew", "reactivate User", e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to reactivate user");

		} finally {

			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "GMActionNew", "Request for Modification of tenure",
				"Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward afterTenureApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GMActionForm gmPeriodicInfoForm = (GMActionForm) form;
		GMDAO gmDAO = new GMDAO();
		RpDAO rpDAO = new RpDAO();
		// ApplicationProcessor appProcessor=new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Connection connection = DBConnection.getConnection(false);
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Map modifCgpan = gmPeriodicInfoForm.getClosureCgpan();
		Set modifCgpanSet = modifCgpan.keySet();
		Iterator modifCgpanIterator = modifCgpanSet.iterator();
		while (modifCgpanIterator.hasNext()) {
			String key = (String) modifCgpanIterator.next();
			String decision = (String) modifCgpan.get(key);
			if (!(decision.equals(""))) {

				if (decision
						.equals(ApplicationConstants.APPLICATION_APPROVED_STATUS)) {
					DANSummary danSummaryNew = new DANSummary();
					danSummaryNew = getRequestedForTenureApplication(key);
					updateApplicationStatusForTenureCases(key, danSummaryNew,
							user);
					request.setAttribute("message",
							"<b>The Request for Modification of Tenure approved successfully.<b><br>");
				}

			}
		}

		modifCgpan.clear();

		return mapping.findForward("success");
	}

	public DANSummary getRequestedForTenureApplication(String cgpan)
			throws DatabaseException {

		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getTenureDetailsStmt;
		DANSummary Summary1 = null;
		int getSchemesStatus = 0;
		String getDanDetailsErr = "";
		connection = DBConnection.getConnection(false);
		try {

			getTenureDetailsStmt = connection
					.prepareCall("{?= call Packgettenuremoddetail.funcGetCgpanTenureDetails(?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			getTenureDetailsStmt
					.registerOutParameter(1, java.sql.Types.INTEGER);
			getTenureDetailsStmt.setString(2, cgpan);
			getTenureDetailsStmt
					.registerOutParameter(3, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(4, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(5, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(6, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(7, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(8, java.sql.Types.VARCHAR);
			getTenureDetailsStmt
					.registerOutParameter(9, java.sql.Types.VARCHAR);
			getTenureDetailsStmt.registerOutParameter(10,
					java.sql.Types.VARCHAR);
			getTenureDetailsStmt.registerOutParameter(11,
					java.sql.Types.VARCHAR);
			getTenureDetailsStmt.registerOutParameter(12,
					java.sql.Types.VARCHAR);
			getTenureDetailsStmt.registerOutParameter(13,
					java.sql.Types.VARCHAR);
			getTenureDetailsStmt.registerOutParameter(14,
					java.sql.Types.VARCHAR);
			getTenureDetailsStmt.execute();

			getSchemesStatus = getTenureDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				Summary1 = new DANSummary();
				Summary1.setMemberId(getTenureDetailsStmt.getString(3));
				Summary1.setCgpan(getTenureDetailsStmt.getString(4));
				Summary1.setAppStatus(getTenureDetailsStmt.getString(5));
				Summary1.setOriginalTenure(getTenureDetailsStmt.getInt(6));
				Summary1.setAppExpiryDate(getTenureDetailsStmt.getString(7));
				Summary1.setRevisedTenure(getTenureDetailsStmt.getInt(8));
				Summary1.setTermAmountSanctionedtDate(getTenureDetailsStmt
						.getString(9));
				Summary1.setLastDateOfRePayment(getTenureDetailsStmt
						.getString(10));
				Summary1.setReason(getTenureDetailsStmt.getString(11));
				Summary1.setRequestCreatedUserId(getTenureDetailsStmt
						.getString(12));
				Summary1.setRequestCreatedDate(getTenureDetailsStmt
						.getString(13));

				getTenureDetailsStmt.close();
				getTenureDetailsStmt = null;

			} else {
				getDanDetailsErr = getTenureDetailsStmt.getString(12);

				getTenureDetailsStmt.close();
				getTenureDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		return Summary1;
	}

	public void updateApplicationStatusForTenureCases(String cgpan,
			DANSummary danSummary, User user) throws DatabaseException {

		Log.log(Log.INFO, "GMActionNew",
				"updateApplicationStatusForTenureCases", "Entered");
		Connection connection = DBConnection.getConnection(false);
		CallableStatement updateCgpanTenureDetails = null;
		int updateStatus = 0;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = "";
		String formatedDate1 = "";
		java.util.Date utilDate = new java.util.Date();
		java.sql.Date sqlDate;
		java.sql.Date sqlDate1;
		String userId = user.getUserId();

		try {
			updateCgpanTenureDetails = connection
					.prepareCall("{?=call Packgettenuremoddetail.funcupdtenuredetails(?,?,to_date(?,'dd/mm/yyyy'),?,to_date(?,'dd/mm/yyyy'),?,to_date(?,'dd/mm/yyyy'),?,?,?)}");
			updateCgpanTenureDetails.registerOutParameter(1,
					java.sql.Types.INTEGER);
			updateCgpanTenureDetails.setString(2, danSummary.getCgpan());
			updateCgpanTenureDetails.setInt(3, danSummary.getOriginalTenure());
			updateCgpanTenureDetails
					.setString(4, danSummary.getAppExpiryDate());
			updateCgpanTenureDetails.setInt(5, danSummary.getRevisedTenure());
			updateCgpanTenureDetails.setString(6,
					danSummary.getLastDateOfRePayment());
			updateCgpanTenureDetails.setString(7,
					danSummary.getRequestCreatedUserId());
			updateCgpanTenureDetails.setString(8,
					danSummary.getRequestCreatedDate());
			updateCgpanTenureDetails.setString(9, danSummary.getReason());
			updateCgpanTenureDetails.setString(10, user.getUserId());
			updateCgpanTenureDetails.registerOutParameter(11,
					java.sql.Types.VARCHAR);
			updateCgpanTenureDetails.executeQuery();
			updateStatus = updateCgpanTenureDetails.getInt(1);
			String error = updateCgpanTenureDetails.getString(11);

			if (updateStatus == Constants.FUNCTION_SUCCESS) {
				updateCgpanTenureDetails.close();
				updateCgpanTenureDetails = null;
				connection.commit();
				Log.log(Log.DEBUG, "GMActionNew",
						"updateApplicationStatusForTenureCases", "success-SP");
			} else if (updateStatus == Constants.FUNCTION_FAILURE) {
				updateCgpanTenureDetails.close();
				updateCgpanTenureDetails = null;
				Log.log(Log.ERROR, "GMActionNew",
						"updateApplicationStatusForTenureCases", "Error : "
								+ error);
				connection.rollback();
				throw new DatabaseException(error);
			}

		} catch (Exception exception) {
			Log.logException(exception);
			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "GMActionNew",
				"updateApplicationDetailForTenureCases", "Exited");

	}

	public ActionForward showTenureApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "showTenureApproval", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = "";
		GMActionForm gmActionForm = (GMActionForm) form;

		String forward = "";

		GMProcessor gmProcessor = new GMProcessor();

		ArrayList tenureApprovalDetail = displayRequestedForTenureApproval();

		ArrayList tenureApprovalDetailMod1 = (ArrayList) tenureApprovalDetail
				.get(0);

		if (tenureApprovalDetail.size() == 0
				&& tenureApprovalDetailMod1.size() == 0) {
			Log.log(Log.INFO, "GMAction", "showTenureApproval",
					"emty Tenure list");
			request.setAttribute("message",
					"No Tenure Details available for Approval");
			forward = "success";
		} else {

			gmActionForm.setClosureDetailsReq(tenureApprovalDetailMod1);
			forward = "tenureList";
		}
		return mapping.findForward(forward);

	}

	public ArrayList displayRequestedForTenureApproval()
			throws DatabaseException {

		DemandAdvice demandAdvice = null;
		Connection connection = null;

		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = new ArrayList();
		ArrayList tenDetails2 = new ArrayList();

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			getDanDetailsStmt = connection
					.prepareCall("{?= call Packgettenuremoddetail.funcGetTenureModDet(?,?)}");

			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getDanDetailsStmt.registerOutParameter(2, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();
			getSchemesStatus = getDanDetailsStmt.getInt(1);

			if (getSchemesStatus == Constants.FUNCTION_FAILURE) {

				String error = getDanDetailsStmt.getString(4);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;
				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet rsPaidDetails = (ResultSet) getDanDetailsStmt
						.getObject(2);

				while (rsPaidDetails.next()) {
					DANSummary Summary1 = new DANSummary();

					Summary1.setMemberId(rsPaidDetails.getString(1));
					Summary1.setCgpan(rsPaidDetails.getString(2));
					Summary1.setAppStatus(rsPaidDetails.getString(3));
					Summary1.setOriginalTenure(rsPaidDetails.getInt(4));
					Summary1.setAppExpiryDate(rsPaidDetails.getString(5));
					Summary1.setRevisedTenure(rsPaidDetails.getInt(6));
					Summary1.setTermAmountSanctionedtDate(rsPaidDetails
							.getString(7));
					Summary1.setLastDateOfRePayment(rsPaidDetails.getString(8));
					Summary1.setReason(rsPaidDetails.getString(9));
					tenDetails2.add(Summary1);
				}
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			}

			danDetails.add(0, tenDetails2);
			connection.commit();

		} catch (Exception exception) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		return danDetails;
	}

	public ActionForward upgradationfromnpatostandard(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "GMAction", "upgradationfromnpatostandard", "Entered");
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

		Log.log(Log.INFO, "GMAction", "upgradationfromnpatostandard", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward upgradationNpa(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "GMAction", "upgradationNpa", "Entered");
		GMProcessor gmProcessor = new GMProcessor();
		GMActionForm gmActionForm = (GMActionForm) form;
		ApplicationReport appReport = new ApplicationReport();
		ReportManager reportManager = new ReportManager();
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		String memberId = gmActionForm.getMemberIdForClosure();
		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		// System.out.println("Member Id:"+memberId+" Cgpan:"+cgpan);
		Log.log(Log.INFO, "GMDAO", "submitUpgradationDetails", "Entered");
		if ((memberId == null) || (memberId.equals(""))) {

			throw new NoDataException("Member Id is Required");

		} else if ((cgpan == null) || (cgpan.equals(""))) {

			throw new NoDataException("Cgpan  is Required");
		} else if (memberId.length() < 12) {
			throw new NoDataException(
					"Member Id can not be less than 12 characters");
		} else {
			bankId = memberId.substring(0, 4);
			zoneId = memberId.substring(4, 8);
			branchId = memberId.substring(8, 12);
		}
		gmActionForm.setMemberIdForClosure(memberId);
		gmActionForm.setCgpanForClosure(cgpan);
		appReport = reportManager.getApplicationReportForCgpan(cgpan);
		gmActionForm.setClosureRemarks("");
		gmActionForm.setApplicationReport(appReport);
		java.util.Date expDate = appReport.getExpiryDate();
		String expDateStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (expDate != null) {
			expDateStr = sdf.format(expDate);
		}
		gmActionForm.setExpiryDate(expDateStr);
		Log.log(Log.INFO, "GMAction", "upgradationNpa", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward submitUpgradationDetails(ActionMapping mapping,
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
		String memberId = gmActionForm.getMemberIdForClosure();
		String cgpan = gmActionForm.getCgpanForClosure().toUpperCase();
		String npaRemarks = gmActionForm.getclosureRemarks();
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		if ((npaRemarks == null) || (npaRemarks.equals(""))) {

			throw new NoDataException("Remarks is Required");

		}

		Log.log(Log.INFO, "GMDAO", "submitUpgradationDetails", "Entered");
		bankId = memberId.substring(0, 4);
		zoneId = memberId.substring(4, 8);
		branchId = memberId.substring(8, 12);
		Connection connection = DBConnection.getConnection();
		try {
			CallableStatement callable = connection.prepareCall("{?=call "
					+ "Funcupgnpastand(?,?,?,?,?)}");
			callable.registerOutParameter(1, Types.INTEGER);
			callable.setString(2, memberId);
			callable.setString(3, cgpan);
			callable.setString(4, npaRemarks);
			callable.setString(5, userId);
			callable.registerOutParameter(6, Types.VARCHAR);
			callable.execute();
			int errorCode = callable.getInt(1);
			String error = callable.getString(6);
			// System.out.println("Error:"+error);

			Log.log(Log.DEBUG, "GMDAO", "submitUpgradationDetails",
					"error code and error" + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "GMDAO", "submitUpgradationDetails", error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, "GMDAO", "submitUpgradationDetails",
					e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to delete the NPA Details.");

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "GMAction", "upgradationfromnpatostandard", "Exited");
		return mapping.findForward(Constants.SUCCESS);

	}

	public ActionForward requestChangeStatus(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("requestModAppStatus");

	}

	public ActionForward checkCgpanAndStatus(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("requestModAppStatus");

	}

	public ActionForward submitCgpanForStatusChange(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "NewReportsAction", "displayListOfClaimRefNumbers",
				"Entered");
		HttpSession session = request.getSession(false);
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();

		String existAppStatus = (String) request
				.getParameter("cgPanExistStatus");

		String cgpan = (String) request.getParameter("cgPan");

		String checkValue = (String) request.getParameter("checkValue");
		String closurRemarks = (String) request.getParameter("closureRemarks");
		if ((existAppStatus.equals("CL")) && (checkValue.equals("RE"))
				|| (existAppStatus.equals("AP")) && (checkValue.equals("RE"))
				|| (existAppStatus.equals("EX")) && (checkValue.equals("AP"))
				|| (existAppStatus.equals("EX")) && (checkValue.equals("RE"))) {

			try {

				String query1 = " update  APPLICATION_DETAIL set app_status='"
						+ checkValue + "' ,app_remarks=app_remarks||'"
						+ closurRemarks + "'   WHERE CGPAN='" + cgpan + "'  ";

				String query2 = " update  APPLICATION_DETAIL@REPUSER set app_status='"
						+ checkValue
						+ "' ,app_remarks=app_remarks||'"
						+ closurRemarks + "'   WHERE CGPAN='" + cgpan + "'  ";

				String query3 = "INSERT INTO APPL_CGPAN_STATUS_STAGING  VALUES ('"
						+ cgpan
						+ "','"
						+ existAppStatus
						+ "','"
						+ checkValue
						+ "')";

				stmt = connection.createStatement();
				int i = stmt.executeUpdate(query1);
				stmt1 = connection.createStatement();
				int i2 = stmt1.executeUpdate(query2);
				stmt2 = connection.createStatement();
				int i3 = stmt.executeUpdate(query3);

			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
				// connection.rollback();
			}

			connection.commit();
		} else {
			throw new DatabaseException(
					"PLEASE CHECK THE CURRENT STATUS AND PRAPOSED STATUS OF CGPANS.CURRENT OPTION IS NOT  ELIGIBLE FOR CHANGE OF APPLICATION STATUS ");
		}

		return mapping.findForward("success");
	}

	public ActionForward checkCgpanAndStatusReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "NewReportsAction", "displayListOfClaimRefNumbers",
				"Entered");
		HttpSession session = request.getSession(false);
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();

		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;

		String cgpan = (String) dynaForm.get("cgPan");

		try {

			String query1 = " SELECT COUNT(*) FROM APPLICATION_DETAIL WHERE CGPAN='"
					+ cgpan + "'  ";

			stmt = connection.createStatement();
			result = stmt.executeQuery(query1);

			while (result.next()) {
				int i = result.getInt(1);

				if (i != 1) {
					throw new DatabaseException(
							"CGPAN DOES NOT EXIST PLEASE CHECK YOUR CGPAN ONCE AGAIN  AND ENTER");
				}

			}

			String query = " SELECT CGPAN,APP_STATUS FROM APPLICATION_DETAIL WHERE SSI_REFERENCE_NUMBER IN (SELECT SSI_REFERENCE_NUMBER  FROM APPLICATION_DETAIL WHERE CGPAN='"
					+ cgpan + "')";

			result = stmt.executeQuery(query);
			String cgpanstatdetails[] = null;
			ArrayList cgpanstatus = new ArrayList();
			while (result.next()) {
				cgpanstatdetails = new String[2];
				cgpanstatdetails[0] = result.getString(1);
				cgpanstatdetails[1] = result.getString(2);
				cgpanstatus.add(cgpanstatdetails);
			}

			// gmActionForm.setCgpansAndStatuses(cgpanstatus);
			session.setAttribute("cgpanstatus", cgpanstatus);
			session.setAttribute("cgpanstatusSize", cgpanstatus.size());
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		return mapping.findForward("requestModAppStatus");

	}

	//

	public ActionForward changeApplicationStatus(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "NewReportsAction", "displayListOfClaimRefNumbers",
				"Entered");

		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();

		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;

		ArrayList cgpanDetailaArray = new ArrayList();

		String cgpan = (String) dynaForm.get("cgPan");
		/*
		 * String appRemarks = (String)dynaForm.get("applicationRemarks");
		 * 
		 * String applicatonStatus = (String)dynaForm.get("checkValue");
		 * 
		 * 
		 * // DynaValidatorActionForm= df= (DynaValidatorActionForm)form;
		 * 
		 * // GMActionForm gmActionForm = (GMActionForm)form;
		 * 
		 * // String cgpanEntered=(String)gmActionForm.getCgPan();
		 * 
		 * // String
		 * applicationStatus=(String)gmActionForm.getApplicationStatus();
		 * 
		 * 
		 * // if(cgpan.equals("")||cgpan.equals(null)||appRemarks.equals("")||
		 * appRemarks.equals(null)) {
		 * 
		 * // throw new
		 * NoDataException("Please Enter Cgpan and Application Remarks "); // }
		 * 
		 * 
		 * User user = getUserInformation(request); String userId =
		 * user.getUserId(); String bankid = (user.getBankId()).trim(); String
		 * zoneid = (user.getZoneId()).trim(); String branchid =
		 * (user.getBranchId()).trim(); String memberId = bankid + zoneid +
		 * branchid;
		 */

		try {

			String cgpanDet[] = null;

			CallableStatement cgpanDetails = null;
			String exception = "";
			String functionName = null;
			functionName = "{?=call funcCheckCgpan(?,?,?,?,?,?,?,?,?,?)}";

			cgpanDetails = connection.prepareCall(functionName);
			cgpanDetails.registerOutParameter(1, java.sql.Types.INTEGER);
			cgpanDetails.setString(2, cgpan);
			cgpanDetails.registerOutParameter(3, java.sql.Types.VARCHAR);

			cgpanDetails.registerOutParameter(4, java.sql.Types.VARCHAR);

			cgpanDetails.registerOutParameter(5, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(6, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(7, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(8, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(9, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(10, java.sql.Types.VARCHAR);
			cgpanDetails.registerOutParameter(11, java.sql.Types.VARCHAR);

			// cgpanDetails.registerOutParameter(6, java.sql.Types.VARCHAR);

			cgpanDetails.execute();

			// Log.log(Log.DEBUG,"GMDAO","repayment detail","exception "+exception);
			int error = cgpanDetails.getInt(1);
			exception = cgpanDetails.getString(11);
			Log.log(Log.DEBUG, "GMActionNEW",
					"Request for Modification of tenure", "errorCode "
							+ exception);

			if (error == Constants.FUNCTION_FAILURE) {
				cgpanDetails.close();
				cgpanDetails = null;
				connection.rollback();
				Log.log(Log.ERROR, "GMActionNEW", "Funcgetssidetailfortenure",
						"error in SP " + exception);

				throw new DatabaseException(
						"CGPAN DOES NOT EXIST PLEASE CHECK YOUR CGPAN ONCE AGAIN  AND ENTER");

			}

			else {

				cgpanDet = new String[11];
				

				String cgpan5 = cgpanDetails.getString(4);

				String status = cgpanDetails.getString(5);
				String unitName = cgpanDetails.getString(6);

				String zoneName = cgpanDetails.getString(7);

				String bankName = cgpanDetails.getString(8);

				String bnkId = cgpanDetails.getString(9);
				String zoneId = cgpanDetails.getString(10);
				String branchId = cgpanDetails.getString(11);

				String memberId1 = bnkId + zoneId + branchId;

				// String cgpan5=cgpanDetails.getString(10);

				String appStatus = cgpanDetails.getString(11);

				System.out.println("11" + cgpan5 + "222" + appStatus);

				cgpanDet[0] = cgpan5;
				cgpanDet[1] = unitName;

				cgpanDet[2] = status;
				cgpanDet[3] = zoneName;

				cgpanDet[4] = bankName;
				cgpanDet[5] = appStatus;

				cgpanDet[6] = memberId1;

				cgpanDetailaArray.add(cgpanDet);

			}

		} catch (SQLException e)

		{
			e.getMessage();
		}
		request.setAttribute("cgpanDetailaArray", cgpanDetailaArray);

		/*
		 * String query1 = " update application_detail set app_status='" +
		 * applicatonStatus +
		 * "' ,app_remarks=app_remarks||'as req by claim dept'   where  cgpan= '"
		 * + cgpan + "'  "; stmt = connection.createStatement();
		 * stmt.executeUpdate(query1);
		 * 
		 * String query2 = " update application_detail@repuser set app_status='"
		 * + applicatonStatus +
		 * "' ,app_remarks=app_remarks||'as req by claim dept'   where  cgpan= '"
		 * + cgpan + "'  "; stmt1 = connection.createStatement();
		 * stmt1.executeUpdate(query2);
		 */

		return mapping.findForward("requestModAppStatus");
	}

	public ActionForward requestForRevivalInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException,
			MessageException, Exception {
		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;

		String cgpan = (String) dynaForm.get("cgpan");
		if (cgpan == null || "".equals(cgpan)) {
			throw new MessageException("Please enter CGPAN.");
		}
		cgpan = cgpan.toUpperCase();
		GMProcessor processor = new GMProcessor();
		// this ,method provides cgpan related reports.
		CgpanDetail cgpanDetails = processor.getCgpanDetails(cgpan);
		// this method provides dan details.
		ReportDAO dao = new ReportDAO();
		ArrayList applicationDetails = dao.getCgpanHistoryReportDetails(cgpan);

		dynaForm.set("cgpanHistoryReport", applicationDetails);
		dynaForm.set("CgpanDetail", cgpanDetails);
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward requestForRevival(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException,
			MessageException, Exception {
		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;

		dynaForm.set("danfys", "");
		dynaForm.set("baseAmts", 0.0);
		dynaForm.set("remarks", "");
		String cgpan = (String) dynaForm.get("cgpan");
		if (cgpan == null || "".equals(cgpan)) {
			throw new MessageException("Please enter CGPAN.");
		}
		GMDAO gmdao = new GMDAO();
		ArrayList list = gmdao.showRevivalRequestForCGPAN(cgpan);
		if (list.size() > 0) {
			throw new MessageException(
					"Revival request already submitted for cgpan.");
		}
		GMProcessor processor = new GMProcessor();
		// this ,method provides cgpan related reports.
		CgpanDetail cgpanDetails = processor.getCgpanDetails(cgpan);
		// this method provides dan details.
		ReportDAO dao = new ReportDAO();
		ArrayList applicationDetails = dao.getCgpanHistoryReportDetails(cgpan);

		dynaForm.set("cgpanHistoryReport", applicationDetails);
		dynaForm.set("CgpanDetail", cgpanDetails);
		return mapping.findForward(Constants.SUCCESS);
	}

	/* This method submits request for revival */
	public ActionForward submitRequestForRevival(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws MessageException,
			DatabaseException, Exception {

		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;
		User user = getUserInformation(request);
		if (user == null) {
			throw new MessageException("TimeOut. Please re-login.");
		}
		String userId = user.getUserId();
		String totalEntries = (String) request.getParameter("rowcount");
		// String cgpans[] = (String[])request.getParameterValues("cgpans");
		String danfys[] = (String[]) request.getParameterValues("danfys");
		String baseAmts[] = (String[]) request.getParameterValues("baseAmts");
		String remarks[] = (String[]) request.getParameterValues("remarks");
		String cgpan = (String) dynaForm.get("cgpan");
		int totalEntry = 0;
		if (totalEntries != null && !"".equals(totalEntries)) {
			totalEntry = Integer.parseInt(totalEntries);
		}
		GMDAO dao = new GMDAO();
		dao.saveRevivalRequest(totalEntry, cgpan, danfys, baseAmts, remarks,
				userId);
		request.setAttribute("message", "Revival Request Saved Successfully.");
		return mapping.findForward(Constants.SUCCESS);
	}

	/**/
	public ActionForward showRevivalRequests(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		GMDAO dao = new GMDAO();
		ArrayList revivals = new ArrayList();
		revivals = dao.showRevivalRequests();
		if(revivals.size() == 0){
			throw new NoDataException("No Revival Request available.");
		}
		request.setAttribute("REVIVALS", revivals);
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward showRevivalRequestForCGPAN(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		DynaValidatorActionForm dynaForm = (DynaValidatorActionForm) form;
		String cgpan = (String) dynaForm.get("cgpan");
		GMDAO dao = new GMDAO();
		dao.showRevivalRequestForCGPAN(cgpan);
		return mapping.findForward(Constants.SUCCESS);
	}

	/**/
	public ActionForward approveRevivalRequest(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		// Generate dans based on added dan fields
		// Check expiry date and based on that change status to AP or EX
		return mapping.findForward(Constants.SUCCESS);
	}

	public ActionForward approveBatchRevivalRequests(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		// Generate dans based on added dan fields
		// Check expiry date and based on that change status to AP or EX

		String[] cgpans = (String[]) request.getParameterValues("cgpans");
		String[] baseAmts = (String[]) request.getParameterValues("baseAmts");
		String[] danfys = (String[]) request.getParameterValues("danfys");
		String[] decisions = (String[]) request.getParameterValues("decisions");
		String[] remarks = (String[]) request.getParameterValues("remarks");
		GMDAO dao = new GMDAO();
		dao.approveRevivalRequests(cgpans, danfys, baseAmts, decisions, remarks);
		return mapping.findForward(Constants.SUCCESS);
	}
	
	//added by vinod@path 05-jan-2016
	public ActionForward mliPendingList(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		Log.log(Log.INFO,"GMActionNew","mliPendingList","Entered");
		HttpSession session = request.getSession(false);
		//System.out.println("GMAN mliPendingList start");
		String approvalFormName = request.getParameter("formType");
		//System.out.println("GMAN approvalFormName : "+approvalFormName);
		ArrayList mli = new ArrayList();
		GMActionForm gmActionForm = (GMActionForm)form;
		mli = getNpaUpdationListMliWiseForApproval(approvalFormName);
		//System.out.println("GMAN mli : "+mli);
				
		String forward = "";
		if(mli == null || mli.size() == 0)
		{
			request.setAttribute("message","No NPA Application Details available for Approval");
			forward = "success";
			//throw new MessageException("No Applications for Approval Available");
		}
		else
		{
			//System.out.println("GGGGGGG");
			session.setAttribute("APPFORMNAME", approvalFormName);
        	gmActionForm.setMemberList(mli);        	
        	forward = "mliListNpa";
		}        
		Log.log(Log.INFO,"GMActionNew","mliPendingList","Exited");
		//System.out.println("GMAN mliPendingList end");
		return mapping.findForward(forward);
	}
	public ArrayList getNpaUpdationListMliWiseForApproval(String formName)throws DatabaseException, MessageException
	{
		Log.log(Log.INFO,"GMActionNew","getNpaUpdationListMliWiseForApproval","Entered");
		//System.out.println("GMAN getNpaUpdationListMliWiseForApproval start : "+formName);
		ArrayList mliArray = new ArrayList();
		String upgrAndModifQry = "";
		GMActionForm gmActionForm = null;
	    Connection connection = null;
	    ResultSet rs = null;
	    Statement st = null;
	    
	    if(formName.equals("updation"))
	    {
	    	upgrAndModifQry = " select distinct mem_bank_name,count(b.cgpan) from member_info a,NPA_UPGRADATION_DETAIL b " +
	    			" where a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id=b.mem_bnk_id||b.mem_zne_id||b.mem_brn_id  " +
	    			" and  NUD_UPGRADE_CHANG_FLAG='LA' group by mem_bank_name";
	    }	    
	    else if(formName.equals("modification"))
	    {
	    	upgrAndModifQry = " select distinct mem_bank_name,count(b.cgpan) from member_info a,NPA_DATE_CHANGE_DETAIL b " +
	    			" where a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id=b.mem_bnk_id||b.mem_zne_id||b.mem_brn_id  " +
	    			" and  NDC_NPA_DT_CHANG_FLAG='LA' group by mem_bank_name";
	    }
	    else
	    {
	    	throw new MessageException("A Problem Occured While Getting the Records According to Form");
	    }
	    
	    //System.out.println("GMA getquery : "+upgrAndModifQry);
	    try
	    {
	    	connection = DBConnection.getConnection(false);
	    	st = connection.createStatement();
	    	rs = st.executeQuery(upgrAndModifQry);
	    	while(rs.next())
	    	{
	    		//System.out.println("GMAN while");
	    		gmActionForm = new GMActionForm();
	    		gmActionForm.setBankName(rs.getString(1));
	    		//System.out.println("GMAN bank Name : "+rs.getString(1));
	    		gmActionForm.setNoOfActions(rs.getInt(2));
	    		//System.out.println("GMAN cases : "+rs.getString(2));
	    		mliArray.add(gmActionForm);	    		
	    	}	    	
	    	rs.close();
	    	rs = null;
	    	st.close();
	    	st = null;
	    }
	    catch(Exception e)
	    {
	    	Log.logException(e);
	        throw new DatabaseException("Unable To Get Records"+e.getMessage());
	    }
	    finally
	    {
	    	DBConnection.freeConnection(connection);
	    }
	    
		Log.log(Log.INFO,"GMActionNew","getNpaUpdationListMliWiseForApproval","Exited");
		//System.out.println("GMAN getNpaUpdationListMliWiseForApproval end");
		return mliArray;
	}
	public ActionForward afterNpaUpdationApproval(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApproval","Entered");
		//System.out.println("GMAN afterNpaUpdationApproval start");
		HttpSession session = request.getSession(false);
		GMActionForm gmform = (GMActionForm) form;
		ArrayList npaUpdateList = new ArrayList();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String forward = "";
		String bank = "";
		String bank1 = request.getParameter("Link");
		String formApp = request.getParameter("formName");
		//System.out.println("GMAN ffnn : "+formApp);
		//System.out.println("GMAN bank1 : "+bank1);							
		if (bank1.length() > 1) {
			bank = bank1.replaceAll("PATH", "&");
			//System.out.println("GMAN bank : "+bank);
		}
		npaUpdateList = getNpaUpdationApprovalForMLIWise(userId, bank, formApp);
		
		if(npaUpdateList == null || npaUpdateList.size()==0)
        {        	
        	request.setAttribute("message","No Applications for Approval Available");
        	forward = "success";
        }
        else
        {
        	session.setAttribute("FORMNAME", formApp);
        	gmform.setNpaApprovalList(npaUpdateList);
        	forward = "afterNpaApproval";
        }
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApproval","Exited");
		//System.out.println("GMAN afterNpaUpdationApproval end");
		return mapping.findForward(forward);
	}

	public ArrayList getNpaUpdationApprovalForMLIWise(String userId, String bank, String formApp)throws DatabaseException,MessageException
	{
		Log.log(Log.INFO,"GMActionNew","getNpaUpdationApprovalForMLIWise","Entered");
		//System.out.println("GMAN getNpaUpdationApprovalForMLIWise start"+bank+ "\t formApp : "+formApp);
		ArrayList npaApprovalList = new ArrayList();
		String ApprovalListQry = "";
		GMActionForm appList = null;
		Connection connection = DBConnection.getConnection(false);
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		try
		{
			if(formApp.equals("updation"))
			{
				ApprovalListQry = "SELECT DISTINCT mem_bank_name, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, d.cgpan, ssi_unit_name," +
						" TO_CHAR (c.NPA_EFFECTIVE_DT, 'dd-mm-yyyy'), NPA_REASONS_TURNING_NPA, TO_CHAR (NPA_UPGRADE_DT, 'dd-mm-yyyy'), NUD_USER_REMARKS" +
						" FROM application_detail a, ssi_detail b, npa_detail_temp c, npa_upgradation_detail d, member_info m" +
						" WHERE     a.ssi_reference_number = b.ssi_reference_number AND b.bid = c.bid AND c.npa_id = d.npa_id " +
						" AND d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id" +
						" AND NUD_UPGRADE_CHANG_FLAG = 'LA' AND mem_bank_name =?";
				//System.out.println("GMAN ApprovalListQry : "+ApprovalListQry);
				
				ps = connection.prepareStatement(ApprovalListQry);
				ps.setString(1,bank);
				rs = ps.executeQuery();
				while(rs.next())
				{
					appList = new GMActionForm();
					appList.setBankName(rs.getString(1));
					appList.setMemberId(rs.getString(2));				
					appList.setCgpan(rs.getString(3));
					appList.setUnitName(rs.getString(4));
					appList.setLstNpaDt(rs.getString(5));
					appList.setReasonForNpa(rs.getString(6));
					appList.setUpGradationDt(rs.getString(7));
					appList.setMliRemarks(rs.getString(8));
					npaApprovalList.add(appList);				
				}
				rs.close();
				rs = null;
				ps.close();
				ps = null;
				
			}			
			else if(formApp.equals("modification"))
			{
				ApprovalListQry = " SELECT DISTINCT mem_bank_name, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, d.cgpan, ssi_unit_name," +
						" TO_CHAR (c.NPA_EFFECTIVE_DT, 'dd-mm-yyyy'), NPA_REASONS_TURNING_NPA, TO_CHAR(NDC_NEW_NPA_EFFECTIVE_DT,'dd-mm-yyyy')," +
						" TO_CHAR (NDC_UPGRADE_DT, 'dd-mm-yyyy'), NDC_USER_REMARKS" +
						" FROM application_detail a, ssi_detail b, npa_detail_temp c, NPA_DATE_CHANGE_DETAIL d, member_info m" +
						" WHERE a.ssi_reference_number = b.ssi_reference_number AND b.bid = c.bid AND c.npa_id = d.npa_id" +
						" AND d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id" +
						" AND NDC_NPA_DT_CHANG_FLAG = 'LA' AND mem_bank_name = ?";
				//System.out.println("GMAN ApprovalListQry : "+ApprovalListQry);
				
				ps = connection.prepareStatement(ApprovalListQry);
				ps.setString(1,bank);
				rs = ps.executeQuery();
				while(rs.next())
				{
					appList = new GMActionForm();
					appList.setBankName(rs.getString(1));
					appList.setMemberId(rs.getString(2));				
					appList.setCgpan(rs.getString(3));
					appList.setUnitName(rs.getString(4));
					appList.setLstNpaDt(rs.getString(5));
					appList.setReasonForNpa(rs.getString(6));
					appList.setNewNpaDt(rs.getString(7));				
					appList.setUpGradationDt(rs.getString(8));
					appList.setMliRemarks(rs.getString(9));
					npaApprovalList.add(appList);				
				}
				rs.close();
				rs = null;
				ps.close();
				ps = null;				
			}
			else
			{
				throw new MessageException("A Problem Occured While Displaying Data According to Form");
			}
		}
		catch(Exception e)
		{
	        throw new DatabaseException("Unable To Get Records"+e.getMessage());		
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMActionNew","getNpaUpdationApprovalForMLIWise","Exited");
		//System.out.println("GMAN getNpaUpdationApprovalForMLIWise end");
		return npaApprovalList;
	}
	
	public ActionForward afterNpaUpdationApprovalSubmit(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApprovalSubmit","Entered");
		//System.out.println("GMAN afterNpaUpdationApprovalSubmit start");
		HttpSession session = request.getSession(false);
		GMActionForm gmForm = (GMActionForm)form;
		Connection connection = null;
		Statement stmt = null;		
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String submitFormName = (String)session.getAttribute("FormType");
		//System.out.println("GMAN submitFormName : "+submitFormName);	
		String sumbitQry = "";
		String mainSubmitQry = "";
		int counter [] = null;
		//Map cgpanSelect = gmForm.getClosureCgpan();
		Map commentsCgpan = gmForm.getCommentCgpan();
		//System.out.println("GMAN cgpanSelect : "+cgpanSelect);
		System.out.println("GMAN commentsCgpan : "+commentsCgpan);
		//System.out.println("action type="+gmForm.getActionType());
		
		
		String arr[]=gmForm.getTextarea();
		
		for (int i = 0; i < arr.length; i++) {
			//System.out.println("textarea="+arr[i]);
			String  map2Value = (String)commentsCgpan.get(arr[i]);
			System.out.println(map2Value+"textarea="+arr[i]);
			String msg="";
			String status="HA";
			if(gmForm.getActionType().equals("delete"))
			{
				status="HR";  
				msg="The Request for NPA Upgradation Delete Successfully.";
			}
			else
			{
				 msg="The Request for NPA Upgradation Approved Successfully.";
			}
			
			if(submitFormName.equals("updation"))
			{
				if(gmForm.getActionType().equals("delete"))
				{				
					 msg="The Request for NPA Upgradation Deleted Successfully.";
				}
				else
				{
					 msg="The Request for NPA Upgradation Approved Successfully.";
				}
				sumbitQry = "update npa_upgradation_detail set NUD_UPGRADE_CHANG_FLAG = '"+status+"', NUD_CGTMSE_REMARKS = '"+map2Value+"'" +
						"where cgpan = '"+arr[i]+"'";
				
				request.setAttribute("message","<b>"+msg+"<b><br>");
				//System.out.println("GMAN key : "+key);
			}
			
			else if(submitFormName.equals("modification"))
			{
				if(gmForm.getActionType().equals("delete"))
				{				
					 msg="The Request for NPA modification Deleted Successfully.";
				}
				else
				{
					 msg="The Request for NPA modification Approved Successfully.";
				}
				sumbitQry = "update NPA_DATE_CHANGE_DETAIL set NDC_NPA_DT_CHANG_FLAG =  '"+status+"', NDC_CGTMSE_REMARKS = '"+map2Value+"'" +
						"where cgpan = '"+arr[i]+"'";
				
				mainSubmitQry = "UPDATE NPA_DETAIL_TEMP SET NPA_EFFECTIVE_DT = (SELECT DISTINCT NDC_NEW_NPA_EFFECTIVE_DT" +
						" FROM NPA_DATE_CHANGE_DETAIL WHERE CGPAN = '"+arr[i]+"') WHERE BID = (SELECT BID FROM SSI_DETAIL" +
						" WHERE SSI_REFERENCE_NUMBER IN (SELECT DISTINCT SSI_REFERENCE_NUMBER FROM APPLICATION_DETAIL" +
						" WHERE CGPAN = '"+arr[i]+"'))"; 
				request.setAttribute("message","<b>"+msg+"<b><br>");
				//System.out.println("GMAN key : "+key);												
			}
			else
			{
				throw new MessageException("A Problem Occured While Performing Submission/Deletion According To Form");
			}
			System.out.println("GMAN sumbitQry : "+sumbitQry);
			System.out.println("GMAN mainSubmitQry : "+mainSubmitQry);
			try
			{
				connection = DBConnection.getConnection(false);				
				stmt = connection.createStatement();
				if(submitFormName.equals("modification"))					
				{
					System.out.println("if(submitFormName.equals(modification))");
					stmt.addBatch(sumbitQry);
					stmt.addBatch(mainSubmitQry);
				}
				if(submitFormName.equals("updation"))
				{
					System.out.println("if(submitFormName.equals(updation))");
					stmt.addBatch(sumbitQry);
				}
				counter = stmt.executeBatch();
				for(int j=1; j<counter.length; j++)
				{
					System.out.println("GMAN Query : "+i+ "\t has Effect : "+counter[i]+" items");
				}
				//System.out.println("GMAN counter : "+counter);
				if(counter.length > 0 )
				{						
					connection.commit();
				}
				else
				{
					connection.rollback();
				}
			}
			catch(Exception e)
			{
				throw new DatabaseException("A Problem Occured While Submiting/Deletion Records"+e.getMessage());
			}
			finally
			{
				try
				{
					if(stmt!=null)
					{
						stmt.close();
					}				
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				DBConnection.freeConnection(connection);
			}
		}
		
//		for(Object key : cgpanSelect.keySet())
//		{
//			commentsCgpan.containsKey(key);
//			System.out.println("cgpanSelect : "+cgpanSelect.get(key)+ "\t commentsCgpan : "+commentsCgpan.get(key));
//			String map1DecisionValue = (String)cgpanSelect.get(key);
//			String  map2Value = (String)commentsCgpan.get(key);
//		//	System.out.println();
//			if(map1DecisionValue != null && map2Value != null)
//			{
//				//System.out.println("GMAN if(map1DecisionValue != null && map2Value != null)");
//				if(map1DecisionValue.equals("Y") && map2Value.trim().length() > 0)
//				{
//					//System.out.println("if(map1DecisionValue.equals(Y) && map2Value.trim().length() > 0)");
//					//System.out.println("GMAN map1DecisionValue : "+map1DecisionValue+ "\t map2Value : "+map2Value);
//					
//					if(submitFormName.equals("updation"))
//					{
//						sumbitQry = "update npa_upgradation_detail set NUD_UPGRADE_CHANG_FLAG = 'HA', NUD_CGTMSE_REMARKS = '"+map2Value+"'" +
//								"where cgpan = '"+key+"'";
//						request.setAttribute("message","<b>The Request for NPA Upgradation approved successfully.<b><br>");
//						//System.out.println("GMAN key : "+key);
//					}
//					
//					if(submitFormName.equals("modification"))
//					{
//						sumbitQry = "update NPA_DATE_CHANGE_DETAIL set NDC_NPA_DT_CHANG_FLAG = 'HA', NDC_CGTMSE_REMARKS = '"+map2Value+"'" +
//								"where cgpan = '"+key+"'";
//						request.setAttribute("message","<b>The Request for NPA Modification approved successfully.<b><br>");
//						//System.out.println("GMAN key : "+key);
//					}
//					//System.out.println("GMAN sumbitQry : "+sumbitQry);
//					try
//					{
//						connection = DBConnection.getConnection(false);
//						stmt = connection.createStatement();
//						int counter = stmt.executeUpdate(sumbitQry);
//						//System.out.println("GMAN counter : "+counter);
//						if(counter > 0)
//						{							
//							connection.commit();
//						}
//						else
//						{
//							connection.rollback();
//						}
//					}
//					catch(Exception e)
//					{
//						throw new DatabaseException("A Problem Occured While Submiting the Records"+e.getMessage());
//					}
//					finally
//					{
//						DBConnection.freeConnection(connection);
//					}
//				}
//				else
//				{
//					throw new MessageException("Please Write Comment According to Selected CGPAN");
//				}
//			}						
//		}
	
		commentsCgpan.clear();
		//cgpanSelect.clear();
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApprovalSubmit","Exited");
		//System.out.println("GMAN afterNpaUpdationApprovalSubmit end");				
		return mapping.findForward("success");
	}
	
	public ActionForward afterNpaUpdationApprovalDelete(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApprovalDelete","Entered");
		System.out.println("GMAN afterNpaUpdationApprovalDelete start");
		GMActionForm gmForm = (GMActionForm)form;
		Connection connection = null;
		Statement stmt = null;		
		User user = getUserInformation(request);
		String userId = user.getUserId();
		String deleteFormName = (String)request.getParameter("FormType");
		//System.out.println("GMAN deleteFormName : "+deleteFormName);
		String deleteQry = "";
		Map cgpanDelete = gmForm.getClosureCgpan();
		Map commentDel = gmForm.getCommentCgpan();
		System.out.println("GMAN cgpanSelect : "+gmForm.getActionType());
		String sumbitQry = "";
	String arr[]=gmForm.getTextarea();
		
		for (int i = 0; i < arr.length; i++) {
			//System.out.println("textarea="+arr[i]);
			String  map2Value = (String)commentDel.get(arr[i]);
			System.out.println(map2Value+"textarea="+arr[i]);
			
			if(deleteFormName.equals("updation"))
			{
				sumbitQry = "update npa_upgradation_detail set NUD_UPGRADE_CHANG_FLAG = 'HR', NUD_CGTMSE_REMARKS = '"+map2Value+"'" +
						"where cgpan = '"+arr[i]+"'";
				request.setAttribute("message","<b>The Request for NPA Upgradation approved successfully.<b><br>");
				//System.out.println("GMAN key : "+key);
			}
			
			if(deleteFormName.equals("modification"))
			{
				sumbitQry = "update NPA_DATE_CHANGE_DETAIL set NDC_NPA_DT_CHANG_FLAG = 'HR', NDC_CGTMSE_REMARKS = '"+map2Value+"'" +
						"where cgpan = '"+arr[i]+"'";
				request.setAttribute("message","<b>The Request for NPA Modification approved successfully.<b><br>");
				//System.out.println("GMAN key : "+key);
			}
			   
			try
			{
				connection = DBConnection.getConnection(false);
				stmt = connection.createStatement();
				int counter = stmt.executeUpdate(sumbitQry);
				//System.out.println("GMAN counter : "+counter);
				if(counter > 0)
				{							
					connection.commit();
				}
				else
				{
					connection.rollback();
				}
			}
			catch(Exception e)
			{
				throw new DatabaseException("A Problem Occured While Submiting the Records"+e.getMessage());
			}
			finally
			{
				try
				{
					if(stmt!=null)
					{
						stmt.close();
					}				
					
				}
				catch(Exception e)
				{
					
				}
				DBConnection.freeConnection(connection);
			}
		}
		
//		
//		for(Object key : cgpanDelete.keySet())
//		{
//			commentDel.containsKey(key);
//			String map1delVal =  (String)cgpanDelete.get(key);
//			String map2delVal = (String)commentDel.get(key);
//			
//			if(map1delVal != null && map2delVal != null)
//			{
//				if(map1delVal.equals("Y") && map2delVal.trim().length() > 0)
//				{
//					if(deleteFormName.equals("updation"))
//					{
//						deleteQry = "update npa_upgradation_detail set NUD_UPGRADE_CHANG_FLAG = 'HR', NUD_CGTMSE_REMARKS = '"+map2delVal+"' " +
//								"where cgpan = '"+key+"'";					
//						request.setAttribute("message","<b>The Request for Deletion of NPA Upgradation successfully.<b><br>");
//					}
//					else if(deleteFormName.equals("modification"))
//					{
//						deleteQry = "update NPA_DATE_CHANGE_DETAIL set NDC_NPA_DT_CHANG_FLAG = 'HR', NDC_CGTMSE_REMARKS = '"+map2delVal+"' " +
//						"where cgpan = '"+key+"'";
//						request.setAttribute("message","<b>The Request for Deletion of NPA Modification successfully.<b><br>");
//					}
//					else
//					{
//						throw new MessageException("A Problem Occured While Deleting Data According to Form");
//					}
//					
//					try
//					{
//						connection = DBConnection.getConnection(false);
//						stmt = connection.createStatement();
//						int counter = stmt.executeUpdate(deleteQry);
//						if(counter > 0)
//						{							
//							connection.commit();
//						}
//						else
//						{
//							connection.rollback();
//						}
//						
//					}
//					catch(Exception e)
//					{
//						throw new DatabaseException("A Problem Occured While Deleting the Records : "+e.getMessage());
//					}
//					finally
//					{
//						DBConnection.freeConnection(connection);
//					}
//					
//				}
//				else
//				{
//					throw new MessageException("Please Write Comment According to Selected CGPAN For Deletion.");
//				}
//			}
//			else
//			{
//				throw new MessageException("Please Select CGPAN and Write Comment According to Selected.");
//			}
//			
//		}		
//		stmt.close();
//		stmt = null;
		cgpanDelete.clear();
		commentDel.clear();
		Log.log(Log.INFO,"GMActionNew","afterNpaUpdationApprovalDelete","Exited");
		//System.out.println("GMAN afterNpaUpdationApprovalDelete end");
		return mapping.findForward("success");
	}
}
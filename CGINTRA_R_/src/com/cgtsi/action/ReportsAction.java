package com.cgtsi.action;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.actionform.RenewalOfWorkingCapitalAfterTenYearsActionForm;
import com.cgtsi.actionform.ReportActionForm;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.claim.ClaimApplication;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimSummaryDtls;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.claim.UploadFileProperties;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.guaranteemaintenance.CgpanInfo;
import com.cgtsi.guaranteemaintenance.Disbursement;
import com.cgtsi.guaranteemaintenance.DisbursementAmount;
import com.cgtsi.guaranteemaintenance.GMConstants;
import com.cgtsi.guaranteemaintenance.GMProcessor;
import com.cgtsi.guaranteemaintenance.NPADetails;
import com.cgtsi.guaranteemaintenance.OutstandingAmount;
import com.cgtsi.guaranteemaintenance.OutstandingDetail;
import com.cgtsi.guaranteemaintenance.PeriodicInfo;
import com.cgtsi.guaranteemaintenance.Repayment;
import com.cgtsi.guaranteemaintenance.RepaymentAmount;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.NoMemberFoundException;
import com.cgtsi.registration.Registration;
import com.cgtsi.reports.ApplicationReport;
import com.cgtsi.reports.DefaulterInputFields;
import com.cgtsi.reports.GeneralReport;
import com.cgtsi.reports.QueryBuilderFields;
import com.cgtsi.reports.RecoveryReport;

import java.io.PrintWriter;
import com.cgtsi.reports.ReportDAO;
import com.cgtsi.reports.ReportManager;
import com.cgtsi.util.CustomisedDate;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;
import com.cgtsi.util.PropertyLoader;
import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.DocumentException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Types;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Iterator;

import oracle.jdbc.OracleTypes;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.validator.DynaValidatorActionForm;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * modification@sudeep.dhiman are done request attribute named 'radioVlaue' are
 * set in some action to display the the radio button value in report.
 */

public class ReportsAction extends BaseAction {

	private ReportDAO reportDao = null;
	private ReportManager reportManager = null;

	public ReportsAction() {

		reportDao = new ReportDAO();
		reportManager = new ReportManager();
	}

	public ActionForward otsReportInput(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "otsReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument32(prevDate);
		generalReport.setDateOfTheDocument33(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "otsReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward otsReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "otsReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument32");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument33");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.otsReport(startDate, endDate);
		dynaForm.set("danRaised", dan);

		if (dan == null || dan.size() == 0)
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "otsReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward otsReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "otsReportDetails", "Exited");
		ArrayList danReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("danValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument32");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		request.setAttribute("OTSDAN", flag);
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument33");
		endDate = new java.sql.Date(eDate.getTime());
		danReport = reportManager.otsReportDetails(startDate, endDate, flag);
		dynaForm.set("danRaisedReport", danReport);

		if (danReport == null || danReport.size() == 0)
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		else {
			danReport = null;
			Log.log(Log.INFO, "ReportsAction", "otsReportDetails", "Exited");
			return mapping.findForward("success");
		}
	}

	// Fix 07092004 - 11
	public ActionForward applicationReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Exited");
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
	public ActionForward rpCancelledReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "rpCancelledReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);
		Log.log(Log.INFO, "ReportsAction", "rpCancelledReport", "Exited");
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
	public ActionForward rpCancelledReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "rpCancelledReportDetails",
				"Entered");
		ArrayList rpCancelledReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		String payId = (String) dynaForm.get("payId");

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		ReportDAO rpDao = new ReportDAO();
		String newRp = payId.toUpperCase();

		// System.out.println("RP Number:"+newRp);
		if (newRp != null && !newRp.equals("")) {
			rpCancelledReport = rpDao.getAllocationCancelledReport(newRp, id);
			if (rpCancelledReport == null || rpCancelledReport.size() == 0) {

				throw new NoDataException(
						"No Data is available for the values entered RP and MemberId,"
								+ " Please Enter Any Other Details ");
			}
		} else if (newRp == null || newRp.equals("")) {
			rpCancelledReport = rpDao.getAllocationCancelledList(id, startDate,
					endDate);
			if (rpCancelledReport == null || rpCancelledReport.size() == 0) {

				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Values ");
			} else {
				dynaForm.set("rpCancelledReport", rpCancelledReport);
				rpCancelledReport = null;
			}
			return mapping.findForward("cancelledList");
		}

		if (rpCancelledReport == null || rpCancelledReport.size() == 0) {

			throw new NoDataException(
					"No Data is available for the values entered RP,"
							+ " Please Enter Any Other Payment Id ");
		} else {

			dynaForm.set("rpCancelledReport", rpCancelledReport);
			rpCancelledReport = null;
			Log.log(Log.INFO, "ReportsAction", "rpCancelledReportDetails",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward applicationHistoryReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Exited");
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
	public ActionForward cgpanHistoryReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "cgpanHistoryReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(Log.INFO, "ReportsAction", "cgpanHistoryReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward shortReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "shortReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument30(prevDate);
		generalReport.setDateOfTheDocument31(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "shortReportInput", "Exited");
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
	public ActionForward dayReportInput(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dayReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		// generalReport.setDateOfTheDocument30(prevDate);
		generalReport.setDateOfTheDocument31(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "dayReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward shortReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "shortReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument30");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument31");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.shortReport(startDate, endDate);
		dynaForm.set("danRaised", dan);
		if (dan == null || dan.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "shortReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward dayReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dayReport", "Entered");
		ArrayList dayDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		// java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		// java.util.Date sDate = (java.util.Date)
		// dynaForm.get("dateOfTheDocument30");
		// String stDate = String.valueOf(sDate);

		// if((stDate == null) ||(stDate.equals("")))
		// startDate = null;
		// else if(stDate != null)
		// startDate = new java.sql.Date (sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument31");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("End Date:"+endDate);
		dayDetails = reportManager.dayReport(endDate);
		dynaForm.set("dayDetails", dayDetails);
		if (dayDetails == null || dayDetails.size() == 0) {

			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dayDetails = null;
			Log.log(Log.INFO, "ReportsAction", "dayReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward shortReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "shortReportDetails", "Exited");
		ArrayList danReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("paymentValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument30");
		String stDate = String.valueOf(sDate);
		request.setAttribute("PAYMENTVALUE", flag);
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument31");
		endDate = new java.sql.Date(eDate.getTime());
		danReport = reportManager.shortReportDetails(startDate, endDate, flag);
		dynaForm.set("danRaisedReport", danReport);
		if (danReport == null || danReport.size() == 0)
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		else {
			danReport = null;
			Log.log(Log.INFO, "ReportsAction", "shortReportDetails", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward excessReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "excessReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument28(prevDate);
		generalReport.setDateOfTheDocument29(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "excessReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward excessReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "excessReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument28");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument29");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.excessReport(startDate, endDate);
		dynaForm.set("danRaised", dan);

		if (dan == null || dan.size() == 0)
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "excessReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward excessReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "excessReportDetails", "Exited");
		ArrayList danReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("danValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument28");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument29");
		endDate = new java.sql.Date(eDate.getTime());
		danReport = reportManager.excessReportDetails(startDate, endDate, flag);
		dynaForm.set("danRaisedReport", danReport);

		request.setAttribute("DANVALUE", flag);

		if (danReport == null || danReport.size() == 0)
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		else {
			danReport = null;
			Log.log(Log.INFO, "ReportsAction", "excessReportDetails", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward sectorWiseReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorWiseReport", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument8(prevDate);
		generalReport.setDateOfTheDocument9(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "sectorWiseReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward applicationRecievedReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationRecievedReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument22(prevDate);
		generalReport.setDateOfTheDocument23(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "applicationRecievedReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * This method added by sukumar@path on 16-Jan-2010 for providing Slab wise
	 * Claim Settled Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward slabWiseClaimReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaValidatorActionForm Form = (DynaValidatorActionForm) form;
		HttpSession session = request.getSession();
		ArrayList stateList = new ArrayList();
		ArrayList districtList = new ArrayList();
		ArrayList sectorList = new ArrayList();
		ArrayList rangeList = new ArrayList();

		Log.log(Log.INFO, "slabWiseClaimReport", "slabWiseClaimReport",
				"Entered");
		Statement stmt = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();

		// ///////////////////////////////

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument42(prevDate);
		generalReport.setDateOfTheDocument43(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		// getting state details
		try {
			String query = " SELECT ste_code,ste_name FROM state_master order by ste_name";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String state[] = null;
			while (result.next()) {
				state = new String[2];
				state[0] = result.getString(1);
				state[1] = result.getString(2);
				stateList.add(state);
			}

			session.setAttribute("stateList", stateList);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}

		if (request.getParameter("hiddenvalue") != null)// setting the district
														// list only if state is
														// selected
		{
			String state = (String) Form.get("slabState");
			String stateCode = "";
			try {
				String query = " select ste_code FROM state_master where ste_name='"
						+ state + "'";
				stmt = connection.createStatement();
				result = stmt.executeQuery(query);

				while (result.next()) {
					stateCode = result.getString(1);
				}
				result = null;
				stmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			}

			try {
				String query = " select dst_code,dst_name,ste_code  from district_master where ste_code='"
						+ stateCode + "' order by dst_name";
				stmt = connection.createStatement();
				result = stmt.executeQuery(query);
				String district[] = null;
				while (result.next()) {
					district = new String[2];
					district[0] = (result.getInt(1)) + "";
					district[1] = result.getString(2);
					districtList.add(district);
				}
				session.setAttribute("districtList", districtList);
				result = null;
				stmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			}

			request.setAttribute("districtSet", "districtSet");
		}

		// getting sector details
		try {
			String query = " SELECT isc_code, isc_name FROM INDUSTRY_SECTOR ORDER BY isc_name";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String sector[] = null;

			while (result.next()) {
				sector = new String[2];
				sector[0] = (result.getInt(1)) + "";
				sector[1] = result.getString(2);
				sectorList.add(sector);
			}

			session.setAttribute("sectorList", sectorList);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}

		// select range details
		try {
			String query = "SELECT range_id,range_desc FROM range_master";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String range[] = null;

			while (result.next()) {
				range = new String[2];
				range[0] = result.getString(1);
				range[1] = result.getString(2);
				rangeList.add(range);
			}

			session.setAttribute("rangeList", rangeList);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return mapping.findForward("success");
	}// slabWiseOutPut

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward slabWiseClaimReportOutPut(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DynaActionForm Form = (DynaActionForm) form;

		Log.log(Log.INFO, "SlabWiseOutPutReportAction",
				"slabWiseClaimReportOutPut", "Entered");
		Statement stmt = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();
		Vector list = new Vector();
		double amount = 0.0;
		int proposals = 0;
		// for formating the total to the 5 point of decimals
		DecimalFormat decimalFormat = new DecimalFormat("##########0.00");

		java.util.Date resta = (java.util.Date) Form.get("dateOfTheDocument42");

		java.util.Date resend = (java.util.Date) Form
				.get("dateOfTheDocument43");
		String state = (String) Form.get("slabState");
		String district = (String) Form.get("slabDistrict");
		String sector = (String) Form.get("slabIndustrySector");
		String mliID = ((String) Form.get("mliID")).trim();
		String rangeFrom = (((String) Form.get("rangeFrom")).trim());
		String rangeTo = (((String) Form.get("rangeTo")).trim());
		String range = "";

		Date toDate = null;
		Date fromDate = null;

		if (!resend.equals("") && resend != null)
			toDate = (Date) Form.get("dateOfTheDocument43");

		if (!resta.equals("") && resta != null)
			fromDate = (Date) Form.get("dateOfTheDocument42");
		// System.out.println("From date:"+fromDate);
		// System.out.println("To date:"+toDate);
		// System.out.println("Range From:"+rangeFrom);
		// System.out.println("Range To:"+rangeTo);
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		if ((fromDate != null) && !(fromDate.equals("")))
			startDate = new java.sql.Date(fromDate.getTime());

		if ((toDate != null) && !(toDate.equals("")))
			endDate = new java.sql.Date(toDate.getTime());
		// System.out.println("From  Date:"+startDate+"End Date:"+endDate);
		// System.out.println("State:"+state);
		if (state.equals("select"))
			state = null;
		if (district.equals("select"))
			district = null;
		if (sector.equals("select"))
			sector = null;
		if (mliID.equals(""))
			mliID = null;
		if (rangeTo.equals(""))
			rangeTo = null;
		if (rangeFrom.equals(""))
			rangeFrom = null;

		if (rangeFrom == null || rangeTo == null)
			range = null;
		else
			range = rangeFrom + "-" + rangeTo;
		// System.out.println("Range:"+range);

		CallableStatement slab = null;

		// getting state details

		try {
			slab = connection
					.prepareCall("call Packallslabforclaim.PROCCLAIMSLABREPORT(?,?,?,?,?,?,?,?)");
			slab.setDate(1, startDate);
			slab.setDate(2, endDate);
			slab.setString(3, state);
			slab.setString(4, district);
			slab.setString(5, range);
			slab.setString(6, sector);
			slab.setString(7, mliID);
			slab.registerOutParameter(8, Constants.CURSOR);
			slab.execute();
			result = (ResultSet) slab.getObject(8);
			String slabList[] = null;

			while (result.next()) {
				slabList = new String[3];
				slabList[0] = result.getString(1);
				System.out.println("0===" + slabList[0]);
				slabList[1] = result.getInt(2) + "";
				System.out.println("r===" + slabList[1]);
				slabList[2] = decimalFormat.format(result.getDouble(3)) + "";
				System.out.println("3===" + slabList[2]);
				// SlabWiseReport slabList = new SlabWiseReport();
				// slabList.setRange(result.getString(1));
				// slabList.setProposals(result.getInt(2));
				// slabList.setAmount(result.getDouble(3));
				amount = amount + result.getDouble(3);// Double.parseDouble(decimalFormat.format(result.getDouble(3)));

				proposals = proposals + result.getInt(2);
				list.add(slabList);
			}

			request.setAttribute("slabList", list);
			request.setAttribute("totalProposals", proposals + "");
			request.setAttribute("totalLoan", decimalFormat.format(amount));
			result = null;
			stmt = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
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
	public ActionForward applicationApprovedReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationApprovedReportInput",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "applicationApprovedReportInput",
				"Exited");
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
	public ActionForward applicationApprovedReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationApprovedReport",
				"Entered");
		ArrayList applicationApprovedReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar:shyam");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		applicationApprovedReport = reportManager.getapplicationApprovedReport(
				startDate, endDate);
		dynaForm.set("applicationApprovedReport", applicationApprovedReport);
		if (applicationApprovedReport == null
				|| applicationApprovedReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "applicationApprovedReport",
					"Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward applicationWiseReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationWiseReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument24(prevDate);
		generalReport.setDateOfTheDocument25(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "applicationWiseReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward securitizationReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "securitizationReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "securitizationReportInput",
				"Exited");
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
	public ActionForward NotAppropriatedDetailsfromInward(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "NotAppropriatedDetailsfromInward",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "NotAppropriatedDetailsfromInward",
				"Exited");
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
	public ActionForward AfterNotAppropriatedDetailsfromInward(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// System.out.println("AfterNotAppropriatedDetailsfromInward...........");
		Log.log(Log.INFO, "ReportsAction",
				"AfterNotAppropriatedDetailsfromInward", "Entered");
		ArrayList notAppropriatedCasesList = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");

		// Diksha..............................
		/*
		 * SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); Date
		 * parsedStartDate =
		 * format.parse(dynaForm.get("dateOfTheDocument26").toString());
		 * java.sql.Date sqlStartDate = new
		 * java.sql.Date(parsedStartDate.getTime());
		 * 
		 * Date parsedendDate =
		 * format.parse(dynaForm.get("dateOfTheDocument27").toString());
		 * java.sql.Date sqlEndDate = new
		 * java.sql.Date(parsedendDate.getTime());
		 */
		// Diksha end...........................

		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		/* startDate=sqlStartDate; */

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		/* endDate =sqlEndDate; */
		notAppropriatedCasesList = reportManager
				.AfterNotAppropriatedDetailsfromInward(startDate, endDate);
		dynaForm.set("danRaised", notAppropriatedCasesList);

		if (notAppropriatedCasesList == null
				|| notAppropriatedCasesList.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			notAppropriatedCasesList = null;
			Log.log(Log.INFO, "ReportsAction",
					"AfterNotAppropriatedDetailsfromInward", "Exited");
			return mapping.findForward("success");
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
	public ActionForward investmentInputNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "investmentInputNew", "Entered");
		// System.out.println("investmentInputNew entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "investmentInputNew", "Exited");
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
	public ActionForward investmentReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "investmentReportNew", "Entered");
		// System.out.println("investmentReportNew entered");
		ArrayList investmentDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		investmentDetails = reportManager.investmentReport(startDate, endDate);
		dynaForm.set("danRaised", investmentDetails);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", investmentDetails);

		if (investmentDetails == null || investmentDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			investmentDetails = null;
			Log.log(Log.INFO, "ReportsAction", "investmentReportNew", "Exited");
			return mapping.findForward("success");
		}
	}

	/**
	 * 
	 * added by sukumar@path for providing Workshop Details
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward workshopReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "workshopReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "workshopReportInput", "Exited");
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
	public ActionForward workshopReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "workshopReport", "Entered");
		ArrayList workshopDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		workshopDetails = reportManager.workshopReport(startDate, endDate);
		dynaForm.set("danRaised", workshopDetails);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", workshopDetails);

		if (workshopDetails == null || workshopDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			workshopDetails = null;
			Log.log(Log.INFO, "ReportsAction", "workshopReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward mliwiseWorkshopReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliwiseWorkshopReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "mliwiseWorkshopReportInput",
				"Exited");
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

	public ActionForward mliworkshopReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliworkshopReport", "Entered");
		ArrayList mliworkshopReports = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		mliworkshopReports = reportManager
				.mliworkshopReport(startDate, endDate);
		dynaForm.set("danRaised", mliworkshopReports);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", mliworkshopReports);

		if (mliworkshopReports == null || mliworkshopReports.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			mliworkshopReports = null;
			Log.log(Log.INFO, "ReportsAction", "mliworkshopReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward statewiseWorkshopReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statewiseWorkshopReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "statewiseWorkshopReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward stateworkshopReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateworkshopReport", "Entered");
		ArrayList statewiseWorkshopDtls = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		statewiseWorkshopDtls = reportManager.stateworkshopReportDetails(
				startDate, endDate);
		dynaForm.set("danRaised", statewiseWorkshopDtls);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", statewiseWorkshopDtls);

		if (statewiseWorkshopDtls == null || statewiseWorkshopDtls.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			statewiseWorkshopDtls = null;
			Log.log(Log.INFO, "ReportsAction", "stateworkshopReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward asf2011notallocatedSummaryInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asf2011notallocatedSummaryInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "asf2011notallocatedSummaryInput",
				"Exited");
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
	public ActionForward asf2011notallocatedSummary(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asf2011notallocatedSummary",
				"Entered");
		ArrayList asf2011notallocatedSummaryDtls = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		asf2011notallocatedSummaryDtls = reportManager
				.asf2011notallocatedSummaryDtl(startDate, endDate);
		dynaForm.set("danRaised", asf2011notallocatedSummaryDtls);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", asf2011notallocatedSummaryDtls);

		if (asf2011notallocatedSummaryDtls == null
				|| asf2011notallocatedSummaryDtls.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			asf2011notallocatedSummaryDtls = null;
			Log.log(Log.INFO, "ReportsAction", "asf2011notallocatedSummary",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward agencywiseWorkshopReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "agencywiseWorkshopReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "agencywiseWorkshopReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward agencyworkshopReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "agencyworkshopReport", "Entered");
		ArrayList agencywiseWorkshopDtls = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		agencywiseWorkshopDtls = reportManager.agencyworkshopReportDetails(
				startDate, endDate);
		dynaForm.set("danRaised", agencywiseWorkshopDtls);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", agencywiseWorkshopDtls);

		if (agencywiseWorkshopDtls == null
				|| agencywiseWorkshopDtls.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			agencywiseWorkshopDtls = null;
			Log.log(Log.INFO, "ReportsAction", "agencyworkshopReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward schemewiseWorkshopReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "schemewiseWorkshopReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "schemewiseWorkshopReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward schemeworkshopReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "schemeworkshopReport", "Entered");
		ArrayList schemewiseWorkshopDtls = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		schemewiseWorkshopDtls = reportManager.schemeworkshopReportDetails(
				startDate, endDate);
		dynaForm.set("danRaised", schemewiseWorkshopDtls);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", schemewiseWorkshopDtls);

		if (schemewiseWorkshopDtls == null
				|| schemewiseWorkshopDtls.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			schemewiseWorkshopDtls = null;
			Log.log(Log.INFO, "ReportsAction", "schemeworkshopReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward stateworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateworkshopReportNew", "Entered");
		ArrayList statesWiseReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);

		statesWiseReport = reportManager.getStatesWorkshopWiseReportNew(
				state.toUpperCase(), startDate, endDate);
		dynaForm.set("statesWiseReport", statesWiseReport);
		if (statesWiseReport == null || statesWiseReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			statesWiseReport = null;
			Log.log(Log.INFO, "ReportsAction", "stateworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward schemeworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "schemeworkshopReportNew", "Entered");
		ArrayList schemeworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		schemeworkshopReport = reportManager.schemeworkshopReportDetailsNew(
				state.toUpperCase(), startDate, endDate);
		dynaForm.set("statesWiseReport", schemeworkshopReport);
		if (schemeworkshopReport == null || schemeworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			schemeworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "schemeworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward districtworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtworkshopReportNew",
				"Entered");
		ArrayList districtworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		districtworkshopReport = reportManager.districtworkshopReportDtls(
				state.toUpperCase(), startDate, endDate);
		dynaForm.set("statesWiseReport", districtworkshopReport);
		if (districtworkshopReport == null
				|| districtworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			districtworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "districtworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward statemliworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statemliworkshopReportNew",
				"Entered");
		ArrayList statemliworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		statemliworkshopReport = reportManager.statemliworkshopReportDtls(
				state.toUpperCase(), startDate, endDate);
		dynaForm.set("statesWiseReport", statemliworkshopReport);
		if (statemliworkshopReport == null
				|| statemliworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			statemliworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "statemliworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward stateagencyworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateagencyworkshopReportNew",
				"Entered");
		ArrayList stateagencyworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		stateagencyworkshopReport = reportManager
				.stateagencyworkshopReportDtls(state.toUpperCase(), startDate,
						endDate);
		dynaForm.set("statesWiseReport", stateagencyworkshopReport);
		if (stateagencyworkshopReport == null
				|| stateagencyworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			stateagencyworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "stateagencyworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward stateprogaramworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateprogaramworkshopReportNew",
				"Entered");
		ArrayList stateprogaramworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		stateprogaramworkshopReport = reportManager
				.stateprogaramworkshopReportDtls(state.toUpperCase(),
						startDate, endDate);
		dynaForm.set("statesWiseReport", stateprogaramworkshopReport);
		if (stateprogaramworkshopReport == null
				|| stateprogaramworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			stateprogaramworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"stateprogaramworkshopReportNew", "Exited");
			return mapping.findForward("success");
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
	public ActionForward propagationmliworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "propagationmliworkshopReportNew",
				"Entered");
		ArrayList propagationmliworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		propagationmliworkshopReport = reportManager
				.propagationmliworkshopReportDtls(state.toUpperCase(),
						startDate, endDate);
		dynaForm.set("statesWiseReport", propagationmliworkshopReport);
		if (propagationmliworkshopReport == null
				|| propagationmliworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			propagationmliworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"propagationmliworkshopReportNew", "Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward propagationstateworkshopReportNew(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "propagationstateworkshopReportNew",
				"Entered");
		ArrayList propagationstateworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		propagationstateworkshopReport = reportManager
				.propagationstateworkshopReportDtls(state.toUpperCase(),
						startDate, endDate);
		dynaForm.set("statesWiseReport", propagationstateworkshopReport);
		if (propagationstateworkshopReport == null
				|| propagationstateworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			propagationstateworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"propagationstateworkshopReportNew", "Exited");
			return mapping.findForward("success");
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
	public ActionForward propagationagencyworkshopReportNew(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"propagationagencyworkshopReportNew", "Entered");
		ArrayList propagationagencyworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		propagationagencyworkshopReport = reportManager
				.propagationagencyworkshopReportDtls(state.toUpperCase(),
						startDate, endDate);
		dynaForm.set("statesWiseReport", propagationagencyworkshopReport);
		if (propagationagencyworkshopReport == null
				|| propagationagencyworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			propagationagencyworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"propagationagencyworkshopReportNew", "Exited");
			return mapping.findForward("success");
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
	public ActionForward asf2011notallocatedSummaryDtls(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asf2011notallocatedSummaryDtls",
				"Entered");
		ArrayList asf2011notallocatedSummaryDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		String mliAddress = null;

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);

		// System.out.println("Proposals"+request.getParameter("proposals"));
		// System.out.println("Asf Due :"+request.getParameter("asfdanamtDue"));

		// System.out.println("state:"+state);
		asf2011notallocatedSummaryDetails = reportManager
				.unitwiseasf2011notallocatedSummaryDtls(state.toUpperCase(),
						startDate, endDate);
		mliAddress = reportManager
				.getMLIAddressforMemberId(state.toUpperCase());
		dynaForm.set("statesWiseReport", asf2011notallocatedSummaryDetails);
		dynaForm.set("ssiDetails", mliAddress);
		if (asf2011notallocatedSummaryDetails == null
				|| asf2011notallocatedSummaryDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			asf2011notallocatedSummaryDetails = null;
			mliAddress = null;
			Log.log(Log.INFO, "ReportsAction",
					"asf2011notallocatedSummaryDtls", "Exited");
			return mapping.findForward("success");
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
	public ActionForward agencystateworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "agencystateworkshopReportNew",
				"Entered");
		ArrayList agencystateworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		agencystateworkshopReport = reportManager
				.agencystateworkshopReportDtls(state.toUpperCase(), startDate,
						endDate);
		dynaForm.set("statesWiseReport", agencystateworkshopReport);
		if (agencystateworkshopReport == null
				|| agencystateworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			agencystateworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "agencystateworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward agencypropagationworkshopReportNew(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"agencypropagationworkshopReportNew", "Entered");
		ArrayList agencypropagationworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		agencypropagationworkshopReport = reportManager
				.agencypropagationworkshopReportDtls(state.toUpperCase(),
						startDate, endDate);
		dynaForm.set("statesWiseReport", agencypropagationworkshopReport);
		if (agencypropagationworkshopReport == null
				|| agencypropagationworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			agencypropagationworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"agencypropagationworkshopReportNew", "Exited");
			return mapping.findForward("success");
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
	public ActionForward inwardReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "inwardReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "inwardReportInput", "Exited");
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
	public ActionForward agencymliworkshopReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "agencymliworkshopReportNew",
				"Entered");
		ArrayList agencymliworkshopReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("state");
		request.setAttribute("bankName", state);
		// System.out.println("state:"+state);
		agencymliworkshopReport = reportManager.agencymliworkshopReportDtls(
				state.toUpperCase(), startDate, endDate);
		dynaForm.set("statesWiseReport", agencymliworkshopReport);
		if (agencymliworkshopReport == null
				|| agencymliworkshopReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			agencymliworkshopReport = null;
			Log.log(Log.INFO, "ReportsAction", "agencymliworkshopReportNew",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward inwardReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "inwardReport", "Entered");
		ArrayList inward = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		// String condition = request.getParameter("condition");
		// String dateForReport = null;
		//
		// if ("inwardDateWiseReport".equals(condition)){
		//
		// dateForReport = "INW_DT";
		//
		// }else if ("ltrDateWiseReport".equals(condition)){
		//
		// dateForReport = "LTR_DT";
		//
		// }

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		inward = reportManager.inwardReport(startDate, endDate);

		// added by upchar@path on 27-05-2013
		// if("inwardDateWiseReport".equals(condition) ||
		// "ltrDateWiseReport".equals(condition)){
		//
		// ReportDAO reportDAO = new ReportDAO();
		// inward =
		// reportDAO.inwardReportDetailsNew(startDate,endDate,dateForReport);
		//
		// }else{
		//
		// inward = reportManager.inwardReport(startDate,endDate);
		//
		// }
		dynaForm.set("danRaised", inward);

		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", inward);

		if (inward == null || inward.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			inward = null;
			Log.log(Log.INFO, "ReportsAction", "inwardReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward npaReportInput(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "npaReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);
		Log.log(Log.INFO, "ReportsAction", "npaReportInput", "Exited");
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
	public ActionForward npaReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "npaReport", "Entered");
		ArrayList npaDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		String id = (String) dynaForm.get("memberId");

		npaDetails = reportManager.npaReport(startDate, endDate, id);
		dynaForm.set("danRaised", npaDetails);

		if (npaDetails == null || npaDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			npaDetails = null;
			Log.log(Log.INFO, "ReportsAction", "npaReport", "Exited");
			return mapping.findForward("success");
		}
	}

	// start 11/01/2018 for npa percentage report NPA ABOVE 10% PERCENT
	public ActionForward npaPercentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "npaPercentReportInput", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "npaPercentReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward npaPercentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "npaPercentReport", "Entered");
		ArrayList npaDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		// String id = (String) dynaForm.get("memberId");

		npaDetails = reportManager.npaPercentReport(startDate, endDate);
		dynaForm.set("danRaised", npaDetails);

		if (npaDetails == null || npaDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			npaDetails = null;
			Log.log(Log.INFO, "ReportsAction", "npaPercentReport", "Exited");
			return mapping.findForward("success");
		}
	}

	// dipika end 11/01/2018 for npa percentage report

	/**
	 * 
	 * Used to provide dd deposited cases report during specific period.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward ddDepositReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "ddDepositReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "ddDepositReportInput", "Exited");
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
	public ActionForward ddDepositReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "ddDepositReport", "Entered");
		ArrayList dddepositedDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		dddepositedDetails = reportManager.ddDepositReport(startDate, endDate);
		dynaForm.set("danRaised", dddepositedDetails);

		if (dddepositedDetails == null || dddepositedDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dddepositedDetails = null;
			Log.log(Log.INFO, "ReportsAction", "ddDepositReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward dcHandicraftInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dcHandicraftInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "dcHandicraftInput", "Exited");
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
	public ActionForward dcHandicraftReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dcHandicraftReport", "Entered");
		ArrayList dcHandicraftReportDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		String id = (String) dynaForm.get("memberId");

		dcHandicraftReportDetails = reportManager.dcHandicraftReport(startDate,
				endDate, id);
		dynaForm.set("danRaised", dcHandicraftReportDetails);

		if (dcHandicraftReportDetails == null
				|| dcHandicraftReportDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dcHandicraftReportDetails = null;
			Log.log(Log.INFO, "ReportsAction", "dcHandicraftReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward dcHandloomInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dcHandicraftInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "dcHandicraftInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward dcHandloomReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dcHandloomReport", "Entered");
		ArrayList dcHandloomReportDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		String id = (String) dynaForm.get("memberId");

		dcHandloomReportDetails = reportManager.dcHandloomReport(startDate,
				endDate, id);
		dynaForm.set("danRaised", dcHandloomReportDetails);

		if (dcHandloomReportDetails == null
				|| dcHandloomReportDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dcHandloomReportDetails = null;
			Log.log(Log.INFO, "ReportsAction", "dcHandicraftReport", "Exited");
			return mapping.findForward("success");
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
	public ActionForward paymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "paymentReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "paymentReportInput", "Exited");
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
	public ActionForward dailypaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailypaymentReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "dailypaymentReportInput", "Exited");
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
	public ActionForward dailydchpaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailydchpaymentReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "dailydchpaymentReportInput",
				"Exited");
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
	public ActionForward dailyasfpaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailyasfpaymentReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "dailyasfpaymentReportInput",
				"Exited");
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
	public ActionForward asfpaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfpaymentReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "asfpaymentReportInput", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * added by sukumar 14-04-2008 for ASF Allocated Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward asfallocatedpaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfallocatedpaymentReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "asfallocatedpaymentReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	/* ------------------------ */

	public ActionForward gfallocatedpaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfallocatedpaymentReportInput",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "gfallocatedpaymentReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward disbursementReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "disbursementReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument18(prevDate);
		generalReport.setDateOfTheDocument19(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "disbursementReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward sanctionedApplicationReportInput(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sanctionedApplicationReportInput",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "sanctionedApplicationReportInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward danReportInput(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument20(prevDate);
		generalReport.setDateOfTheDocument21(date);
		// Fix 07092004 - 19
		dynaForm.set("memberId", "");
		dynaForm.set("ssi", "");
		// Fix Completed
		BeanUtils.copyProperties(dynaForm, generalReport);
		// ApplicationReport applicationReport = new ApplicationReport();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "danReportInput", "Exited");
		return mapping.findForward("success");
	}

	/* ---------------------------------- */
	public ActionForward gfdanReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfdanReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument20(prevDate);
		generalReport.setDateOfTheDocument21(date);
		// Fix 07092004 - 19
		dynaForm.set("memberId", "");
		dynaForm.set("ssi", "");
		// Fix Completed
		BeanUtils.copyProperties(dynaForm, generalReport);
		// ApplicationReport applicationReport = new ApplicationReport();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "gfdanReportInput", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * added by sukumar@path for Dan Report for ASF
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward asfdanReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfdanReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument20(prevDate);
		generalReport.setDateOfTheDocument21(date);
		// Fix 07092004 - 19
		dynaForm.set("memberId", "");
		dynaForm.set("ssi", "");

		BeanUtils.copyProperties(dynaForm, generalReport);
		// ApplicationReport applicationReport = new ApplicationReport();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "asfdanReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward securitizationReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "securitizationReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.securitizationReport(startDate, endDate);
		dynaForm.set("danRaised", dan);

		if (dan == null || dan.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "securitizationReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward securitizationReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "securitizationReportDetails",
				"Exited");
		ArrayList danReport = new ArrayList();
		ArrayList securitizationArray = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("bank");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);
		request.setAttribute("SECURITIZATIONBANK", flag);

		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		danReport = reportManager.securitizationReportDetails(startDate,
				endDate, flag);
		int danReportLength = danReport.size();
		for (int i = 0; i < danReportLength; i++) {
			securitizationArray = (ArrayList) danReport.get(i);
			if (i == 0) {
				dynaForm.set("securitizationReport1", securitizationArray);
			} else if (i == 1) {
				dynaForm.set("securitizationReport2", securitizationArray);
			} else if (i == 2) {
				dynaForm.set("securitizationReport3", securitizationArray);
			} else if (i == 3) {
				dynaForm.set("securitizationReport4", securitizationArray);
			} else if (i == 4) {
				dynaForm.set("securitizationReport5", securitizationArray);
			} else if (i == 5) {
				dynaForm.set("securitizationReport6", securitizationArray);
			}
		}

		if (danReport == null || danReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			danReport = null;
			Log.log(Log.INFO, "ReportsAction", "securitizationReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward securitizationReportDetailsForCgpan(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"securitizationReportDetailsForCgpan", "Entered");
		ApplicationReport dan = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String application = (String) dynaForm.get("number");

		dan = reportManager.securitizationReportDetailsForCgpan(application);
		dynaForm.set("statusDetails", dan);
		String key = dan.getMemberId();

		request.setAttribute("radioValue", "");
		if (key == null || key.equals("")) {
			throw new NoDataException("No Data is available for this value,"
					+ " Please Choose Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction",
					"securitizationReportDetailsForCgpan", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationWiseReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationWiseReport", "Entered");
		ArrayList status = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument24");
		String stDate = String.valueOf(sDate);
		String application = (String) dynaForm.get("applicationStatus1");
		/* modification@sudeep.dhiman */
		if (application.equals("generation"))
			request.setAttribute("radioValue", "DAN Generated");
		else if (application.equals("allocation"))
			request.setAttribute("radioValue", "Allocated");
		else if (application.equals("appropriation"))
			request.setAttribute("radioValue", "Appropriated");

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument25");
		endDate = new java.sql.Date(eDate.getTime());
		status = (ArrayList) reportManager.applicationWiseReport(startDate,
				endDate, application);
		dynaForm.set("danRaised", status);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument24"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument25"));
		if (status == null || status.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			status = null;
			Log.log(Log.INFO, "ReportsAction", "applicationWiseReport",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationWiseReportDetails",
				"Entered");
		ApplicationReport dan = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String application = (String) dynaForm.get("number");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument24");
		String stDate = String.valueOf(sDate);
		String radio = (String) dynaForm.get("applicationStatus1");
		/* modification@sudeep.dhiman */
		if (radio.equals("generation"))
			request.setAttribute("radioValue", "DAN Generated");
		else if (radio.equals("allocation"))
			request.setAttribute("radioValue", "Allocated");
		else if (radio.equals("appropriation"))
			request.setAttribute("radioValue", "Appropriated");

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument25");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.applicationWiseReportDetails(startDate, endDate,
				application);
		dynaForm.set("statusDetails", dan);
		String key = dan.getMemberId();

		if (key == null || key.equals("")) {
			throw new NoDataException("No Data is available for this value,"
					+ " Please Choose Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "applicationWiseReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationRecievedReportDetails(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationRecievedReportDetails",
				"Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument22");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument23");
		endDate = new java.sql.Date(eDate.getTime());
		dan = (ArrayList) reportManager.applicationRecievedReportDetails(
				startDate, endDate);
		dynaForm.set("danRaised", dan);

		if (dan == null || dan.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction",
					"applicationRecievedReportDetails", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationRecievedReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationRecievedReport",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.set("selectAll", "");
		dynaForm.set("applicationDate", "");
		dynaForm.set("promoter", "");
		dynaForm.set("itpan", "");
		dynaForm.set("ssiDetails", "");
		dynaForm.set("industryType", "");
		dynaForm.set("termCreditSanctioned", "");
		dynaForm.set("tcInterest", "");
		dynaForm.set("tcTenure", "");
		dynaForm.set("tcPlr", "");
		dynaForm.set("tcOutlay", "");
		dynaForm.set("workingCapitalSanctioned", "");
		dynaForm.set("wcPlr", "");
		dynaForm.set("wcOutlay", "");
		dynaForm.set("rejection", "");

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument22");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument23");
		endDate = new java.sql.Date(eDate.getTime());
		Log.log(Log.INFO, "ReportsAction", "applicationRecievedReport",
				"Exited");

		return mapping.findForward("success");
	}

	public ActionForward applicationStatusWiseReportDetails(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"applicationStatusWiseReportDetails", "Entered");
		ApplicationReport dan = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String application = (String) dynaForm.get("number");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument14");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument15");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.getApplicationStatusWiseReportDetails(startDate,
				endDate, application);
		dynaForm.set("statusDetails", dan);
		String key = dan.getMemberId();

		String radio = (String) dynaForm.get("applicationStatus");

		if (radio.equals("NE"))
			request.setAttribute("radioValue", "New");
		else if (radio.equals("AP"))
			request.setAttribute("radioValue", "Approved");
		else if (radio.equals("PE"))
			request.setAttribute("radioValue", "Pending");
		else if (radio.equals("CL"))
			request.setAttribute("radioValue", "Closed");
		else if (radio.equals("HO"))
			request.setAttribute("radioValue", "Hold");
		else if (radio.equals("MO"))
			request.setAttribute("radioValue", "Modified");
		else if (radio.equals("RE"))
			request.setAttribute("radioValue", "Rejected");
		String member = request.getParameter("number");
		request.setAttribute("MEMBERID", member);

		if (key == null || key.equals("")) {
			throw new NoDataException("No Data is available for this value,"
					+ " Please Choose Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction",
					"applicationStatusWiseReportDetails", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationStatusWiseReportDetails1(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"applicationStatusWiseReportDetails1", "Entered");
		ApplicationReport dan = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String application = (String) dynaForm.get("number");
		// System.out.println("application:"+application);
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument14");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
			// System.out.println("startDate:"+startDate);
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
			// System.out.println("startDate:"+startDate);
		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument15");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("endDate:"+endDate);
		dan = reportManager.getApplicationStatusWiseReportDetails1(startDate,
				endDate, application);
		dynaForm.set("statusDetails", dan);
		String key = dan.getMemberId();

		if (key == null || key.equals("")) {
			throw new NoDataException("No Data is available for this value,"
					+ " Please Choose Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction",
					"applicationStatusWiseReportDetails1", "Exited");
			return mapping.findForward("success");
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
	public ActionForward applicationHistoryReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationHistoryReportDetails",
				"Entered");
		ArrayList applicationHistory = new ArrayList();
		ArrayList cgpans = new ArrayList();
		ApplicationReport appReport = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String cgpan = (String) dynaForm.get("enterCgpan");
		// String ssiName = (String) dynaForm.get("enterSSI");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		ReportDAO rpDao = new ReportDAO();

		// cgpans = reportManager.getAllCgpans();
		String newCgpan = cgpan.toUpperCase();
		applicationHistory = rpDao.getCgpanHistoryReportDetails(newCgpan);
		dynaForm.set("cgpanHistoryReport", applicationHistory);
		// System.out.println("newCgpan:"+newCgpan);
		appReport = reportManager.getApplicationReportForCgpan(newCgpan);

		String key = appReport.getMemberId();

		if (key == null || key.equals("")) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		}
		dynaForm.set("applicationReport", appReport);
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
	public ActionForward cgpanHistoryReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "cgpanHistoryReportDetails",
				"Entered");
		ArrayList applicationHistory = new ArrayList();
		ArrayList cgpans = new ArrayList();
		ApplicationReport appReport = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String cgpan = (String) dynaForm.get("enterCgpan");
		// String ssiName = (String) dynaForm.get("enterSSI");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		ReportDAO rpDao = new ReportDAO();

		cgpans = reportManager.getAllCgpans();
		String newCgpan = cgpan.toUpperCase();
		applicationHistory = rpDao.getCgpanHistoryReportDetails(newCgpan);
		dynaForm.set("cgpanHistoryReport", applicationHistory);
		// System.out.println("newCgpan:"+newCgpan);
		appReport = reportManager.getApplicationReportForCgpan(newCgpan);
		dynaForm.set("applicationReport", appReport);
		Log.log(Log.INFO, "ReportsAction", "cgpanHistoryReportDetails",
				"Exited");
		return mapping.findForward("success");

	}

	// Fix 070904 - 09,10, 02
	public ActionForward applicationReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationReportDetails",
				"Entered");
		ArrayList apr = new ArrayList();
		ArrayList cgpans = new ArrayList();
		ApplicationReport appReport = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String cgpan = (String) dynaForm.get("enterCgpan");
		String ssiName = (String) dynaForm.get("enterSSI");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId + zoneId + branchId;
		// System.out.println("ssiName"+ssiName);
		// System.out.println("cgpan"+cgpan);
		if (!(branchId.equals("0000"))) {
			if (cgpan == null || cgpan.equals("")) {
				apr = reportManager.getCgpanForBranch(ssiName, memberId);
				dynaForm.set("cgpanList", apr);
				if (apr == null || apr.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					apr = null;
					Log.log(Log.INFO, "applicationReportDetails",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success3");
				}
			} else {
				// cgpans = reportManager.getAllCgpans();
				String newCgpan = cgpan.toUpperCase();
				// System.out.println("newCgpan:"+newCgpan);
				// if(cgpans.contains(newCgpan))
				// {
				appReport = reportManager.applicationReportForBranch(cgpan,
						ssiName, memberId);
				dynaForm.set("applicationReport", appReport);
				String key = appReport.getMemberId();

				if (key == null || key.equals("")) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					appReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success4");
				}
				// }
				// else
				// {
				// throw new NoDataException("Enter a Valid CGPAN");
				// }
			}
		}

		else if (!(zoneId.equals("0000"))) {
			// String newZoneId = bankId+zoneId;
			if (cgpan == null || cgpan.equals("")) {
				apr = reportManager.getCgpanForZone(ssiName, bankId, zoneId);
				dynaForm.set("cgpanList", apr);
				if (apr == null || apr.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					apr = null;
					Log.log(Log.INFO, "applicationReportDetails",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success5");
				}
			} else {
				// cgpans = reportManager.getAllCgpans();
				String newCgpan = cgpan.toUpperCase();
				// System.out.println("newCgpan:"+newCgpan);
				// if(cgpans.contains(newCgpan))
				// {
				appReport = reportManager.applicationReportForZone(cgpan,
						ssiName, bankId, zoneId);
				dynaForm.set("applicationReport", appReport);
				String key = appReport.getMemberId();

				if (key == null || key.equals("")) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					appReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success6");
				}
				// }
				// else
				// {
				// throw new NoDataException("Enter a Valid CGPAN");
				// }
			}
		}

		else if (!(bankId.equals("0000"))) {
			String newBankId = bankId;
			if (cgpan == null || cgpan.equals("")) {
				apr = reportManager.getCgpanForMember(ssiName, newBankId);
				dynaForm.set("cgpanList", apr);
				if (apr == null || apr.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					apr = null;
					Log.log(Log.INFO, "applicationReportDetails",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success7");
				}
			} else {
				// cgpans = reportManager.getAllCgpans();
				String newCgpan = cgpan.toUpperCase();
				// System.out.println("newCgpan:"+newCgpan);
				// if(cgpans.contains(newCgpan))
				// {
				appReport = reportManager.getApplicationReportForMember(cgpan,
						ssiName, newBankId);
				dynaForm.set("applicationReport", appReport);
				String key = appReport.getMemberId();

				if (key == null || key.equals("")) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					appReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success8");
				}
				// }
				// else
				// {
				// throw new NoDataException("Enter a Valid CGPAN");
				// }
			}
		}

		else {
			if (cgpan == null || cgpan.equals("")) {
				apr = reportManager.getCgpan(ssiName);
				dynaForm.set("cgpanList", apr);
				if (apr == null || apr.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					apr = null;
					Log.log(Log.INFO, "applicationReportDetails",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success1");
				}

			} else {
				// cgpans = reportManager.getAllCgpans();
				String newCgpan = cgpan.toUpperCase();
				// System.out.println("newCgpan:"+newCgpan);
				// if(cgpans.contains(newCgpan))
				// {

				appReport = reportManager.getApplicationReport(cgpan, ssiName);
				dynaForm.set("applicationReport", appReport);
				String key = appReport.getMemberId();

				if (key == null || key.equals("")) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					appReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"applicationReportDetails", "Exited");
					return mapping.findForward("success2");
				}
				// }
				// else
				// {
				// throw new NoDataException("Enter a Valid CGPAN");
				// }
			}
		}
	}// Fix Completed

	public ActionForward cgpanList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "cgpanList", "Entered");
		ApplicationReport appReport = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String cgpan = (String) dynaForm.get("enterCgpan");
		String ssiName = (String) dynaForm.get("enterSSI");

		appReport = reportManager.getApplicationReport(cgpan, ssiName);
		dynaForm.set("applicationReport", appReport);
		if (appReport == null) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			appReport = null;
			Log.log(Log.INFO, "ReportsAction", "cgpanList", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward applicationReportDetails1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationReportDetails1",
				"Entered");
		ApplicationReport appReport = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String cgpan = request.getParameter("cgpan");
		appReport = reportManager.getApplicationReportForCgpan(cgpan);
		dynaForm.set("applicationReport", appReport);
		if (appReport == null) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			appReport = null;
			Log.log(Log.INFO, "ReportsAction", "applicationReportDetails1",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward danRaised(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danRaised", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument2(prevDate);
		// System.out.println("prevDate:"+prevDate);
		generalReport.setDateOfTheDocument3(date);
		// System.out.println("date:"+date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "danRaised", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward danRaisedMlis(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danRaisedMlis", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument3");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.getDanRaisedMlis(startDate, endDate);
		dynaForm.set("danRaised", dan);
		if (dan == null || dan.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "danRaisedMlis", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward danRaisedReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danRaisedReport", "Exited");
		ArrayList danReport = new ArrayList();
		HttpSession session = request.getSession();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument3");
		endDate = new java.sql.Date(eDate.getTime());
		String flag = (String) dynaForm.get("bank");
		session.setAttribute("BankForDispaly", flag);

		int i = flag.indexOf("$");

		if (i != -1) {
			String newFlag = flag.replace('$', '&');
			danReport = reportManager.getDanRaisedReport(startDate, endDate,
					newFlag);
			dynaForm.set("danRaisedReport", danReport);
			if (danReport == null || danReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				danReport = null;
				Log.log(Log.INFO, "ReportsAction", "danRaisedReport", "Exited");
				return mapping.findForward("success");
			}

		} else {
			danReport = reportManager.getDanRaisedReport(startDate, endDate,
					flag);
			dynaForm.set("danRaisedReport", danReport);
			if (danReport == null || danReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				danReport = null;
				Log.log(Log.INFO, "ReportsAction", "danRaisedReport", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward danDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danDetails", "Entered");
		ArrayList danDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		// String bank = (String) dynaForm.get("bank");
		String cgdan = (String) dynaForm.get("danDetail");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument2");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument3");
		endDate = new java.sql.Date(eDate.getTime());

		String bank = (String) dynaForm.get("bank");
		int i = bank.indexOf("$");

		if (i != -1) {
			String newBank = bank.replace('$', '&');
			danDetails = reportManager.getDanDetails(startDate, endDate,
					newBank, cgdan);
			dynaForm.set("danDetails", danDetails);

			if (danDetails == null || danDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// danDetails = null;
				Log.log(Log.INFO, "ReportsAction", "danDetails", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			danDetails = reportManager.getDanDetails(startDate, endDate, bank,
					cgdan);
			dynaForm.set("danDetails", danDetails);

			if (danDetails == null || danDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// danDetails = null;
				Log.log(Log.INFO, "ReportsAction", "danDetails", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward gfOutstanding(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfOutstanding", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument4(prevDate);
		generalReport.setDateOfTheDocument5(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "gfOutstanding", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward gfOutstandingMlis(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfOutstandingMlis", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument4");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument5");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.getGfOutstandingMli(startDate, endDate);
		dynaForm.set("guaranteeFee", dan);
		if (dan == null || dan.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction", "gfOutstandingMlis", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward gfOutstandingReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfOutstandingReport", "Entered");
		ArrayList gFee = new ArrayList();
		HttpSession session = request.getSession();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument4");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument5");
		endDate = new java.sql.Date(eDate.getTime());
		String bank = (String) dynaForm.get("bank");
		session.removeAttribute("BankForDispaly");
		session.setAttribute("BankForDispaly", bank);
		int i = bank.indexOf("$");

		if (i != -1) {
			String newBank = bank.replace('$', '&');
			gFee = reportManager.getGfOutstanding(startDate, endDate, newBank);
			dynaForm.set("gFeeReport", gFee);
			if (gFee == null || gFee.size() == 0) {
				throw new NoDataException(
						"There are no outstanding Demand Advices "
								+ " available for this MLI ");
			} else {
				gFee = null;
				Log.log(Log.INFO, "ReportsAction", "gfOutstandingReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			gFee = reportManager.getGfOutstanding(startDate, endDate, bank);
			dynaForm.set("gFeeReport", gFee);
			if (gFee == null || gFee.size() == 0) {
				throw new NoDataException(
						"There are no outstanding Demand Advices "
								+ " available for this MLI ");
			} else {
				gFee = null;
				Log.log(Log.INFO, "ReportsAction", "gfOutstandingReport",
						"Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward danDetailsGf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danDetailsGf", "Entered");
		ArrayList danDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String bank = (String) dynaForm.get("bank");
		String cgdan = (String) dynaForm.get("danDetail");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument4");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument5");
		endDate = new java.sql.Date(eDate.getTime());
		danDetails = reportManager.getDanDetailsGf(startDate, endDate, bank,
				cgdan);
		dynaForm.set("danDetails", danDetails);
		if (danDetails == null || danDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// danDetails = null;
			Log.log(Log.INFO, "ReportsAction", "danDetailsGf", "Exited");
			return mapping.findForward("success");
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
	public ActionForward monthlyProgressReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "monthlyProgressReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "monthlyProgressReport", "Exited");
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
	public ActionForward turnoverReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// System.out.println("turnoverReport entered");
		Log.log(Log.INFO, "ReportsAction", "turnoverReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "turnoverReport", "Exited");
		// System.out.println("turnoverReport exited");
		return mapping.findForward("success");
	}

	/* ---------------------- */

	/* Added by sukumar@path 01-04-2008 */
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward minorityReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minorityReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "minorityReport", "Exited");
		return mapping.findForward("success");
	}

	/* ---------------------- */
	/* Added by SUKUMAR@path 03-04-2008 */
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward minorityStateReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minorityStateReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "minorityStateReport", "Exited");
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
	public ActionForward stateWiseReportAll(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateWiseReportAll", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument10(prevDate);
		generalReport.setDateOfTheDocument11(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "stateWiseReportAll", "Exited");
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
	public ActionForward stateWiseReportDetailsAll(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetailsAll",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ArrayList mliReport = new ArrayList();
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("startDate:"+startDate);
		// System.out.println("endDate:"+endDate);
		String id = (String) dynaForm.get("checkValue");
		// System.out.println("id:"+id);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		mliReport = reportManager.getStateWiseReportDetailsNew(startDate,
				endDate, id);
		dynaForm.set("mliWiseReport", mliReport);
		if (mliReport == null || mliReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			mliReport = null;
			Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetailsAll",
					"Exited");
			return mapping.findForward("success");
		}

	}

	/* Added by SUKUMAR@path 08-04-2008 */
	public ActionForward categorywiseguaranteeissuedReport(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "categorywiseguaranteeissuedReport",
				"Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "categorywiseguaranteeissuedReport",
				"Exited");
		return mapping.findForward("success");
	}

	/* ---------------------- */

	/* added by shyam */
	public ActionForward minorityprogressReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minorityprogressReport", "Entered");
		ArrayList minorityprogressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar:shyam");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		minorityprogressReport = reportManager.getminorityProgressReport(
				startDate, endDate);
		dynaForm.set("minorityprogressReport", minorityprogressReport);
		if (minorityprogressReport == null
				|| minorityprogressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "minorityprogressReport",
					"Exited");
			return mapping.findForward("success");
		}
		/*
		 * progressReport =
		 * reportManager.getMonthlyProgressReport(startDate,endDate);
		 * dynaForm.set("progressReport",progressReport); if(progressReport ==
		 * null || progressReport.size()==0) { throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); } else {
		 * Log.log(Log.INFO,"ReportsAction","minorityprogressReport","Exited");
		 * return mapping.findForward("success"); }
		 */

	}

	/* --------------- */

	/* added by shyam */
	public ActionForward turnoverprogressReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "turnoverprogressReport", "Entered");
		ArrayList turnoverprogressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar:shyam");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		turnoverprogressReport = reportManager.getturnoverProgressReport(
				startDate, endDate);
		dynaForm.set("turnoverprogressReport", turnoverprogressReport);
		if (turnoverprogressReport == null
				|| turnoverprogressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "turnoverprogressReport",
					"Exited");
			return mapping.findForward("success");
		}

	}

	/* --------------- */
	/* added by sukumar@path 03-04-2008 */
	public ActionForward minoritystateprogressReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minoritystateprogressReport",
				"Entered");
		ArrayList minoritystateprogressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar:shyam");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		minoritystateprogressReport = reportManager
				.getminorityStateProgressReport(startDate, endDate);
		dynaForm.set("minoritystateprogressReport", minoritystateprogressReport);
		if (minoritystateprogressReport == null
				|| minoritystateprogressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "minoritystateprogressReport",
					"Exited");
			return mapping.findForward("success");
		}
		/*
		 * progressReport =
		 * reportManager.getMonthlyProgressReport(startDate,endDate);
		 * dynaForm.set("progressReport",progressReport); if(progressReport ==
		 * null || progressReport.size()==0) { throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); } else {
		 * Log.log(Log.INFO,"ReportsAction","minorityprogressReport","Exited");
		 * return mapping.findForward("success"); }
		 */

	}

	/**
	 * 
	 * added by sukumar@path 08-04-2008 for providing Categorywise guarantee
	 * Approved Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward categorywiseguaranteeissuedprogressReport(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"categorywiseguaranteeissuedprogressReport", "Entered");
		ArrayList categorywiseguaranteeissuedprogressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar1:shyam1");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		categorywiseguaranteeissuedprogressReport = reportManager
				.getcategorywiseguaranteeissuedprogressReport(startDate,
						endDate);
		dynaForm.set("categorywiseguaranteeissuedprogressReport",
				categorywiseguaranteeissuedprogressReport);
		if (categorywiseguaranteeissuedprogressReport == null
				|| categorywiseguaranteeissuedprogressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction",
					"categorywiseguaranteeissuedprogressReport", "Exited");
			return mapping.findForward("success");
		}

	}

	/* --------------- */

	/* added by sukumar@path 12-04-2008 */
	public ActionForward turnoverandexportsReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "turnoverandexportsReport",
				"Entered");
		ArrayList turnoverandexportprogressreport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar1:shyam1");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		turnoverandexportprogressreport = reportManager
				.getcategorywiseguaranteeissuedprogressReport(startDate,
						endDate);
		dynaForm.set("turnoverandexportprogressreport",
				turnoverandexportprogressreport);
		if (turnoverandexportprogressreport == null
				|| turnoverandexportprogressreport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "turnoverandexportsReport",
					"Exited");
			return mapping.findForward("success");
		}

	}

	/* --------------- */

	public ActionForward progressReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "progressReport", "Entered");
		ArrayList progressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String userId = user.getUserId();
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// if(bankId.equals(Constants.CGTSI_USER_BANK_ID) &&
		// !userId.equals("DEMOUSER")){
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			progressReport = reportManager.getMonthlyProgressReport(startDate,
					endDate);
		} else {
			progressReport = reportManager.getMonthlyProgressReportNew(
					startDate, endDate);
		}
		dynaForm.set("progressReport", progressReport);
		if (progressReport == null || progressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "progressReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward sanctionedApplicationReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sanctionedApplicationReport",
				"Entered");
		ArrayList sar = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String memberId = (String) dynaForm.get("memberId");
		String flag = (String) dynaForm.get("checkValue");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// modification@sudeep.dhiman
		if (flag.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (flag.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");
		// end

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if ((memberId == null) || (memberId.equals(""))) {
			sar = reportManager.getSanctionedApplicationReport(startDate,
					endDate, flag);
			dynaForm.set("sar", sar);
			if (sar == null || sar.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				Log.log(Log.INFO, "ReportsAction",
						"sanctionedApplicationReport", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			if (memberids.contains(memberId)) {
				String bankId = memberId.substring(0, 4);
				String zoneId = memberId.substring(4, 8);
				String branchId = memberId.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String id = bankId + zoneId + branchId;
					sar = reportManager
							.getSanctionedApplicationReportForBranch(startDate,
									endDate, id, flag);
					dynaForm.set("sar", sar);
					if (sar == null || sar.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"sanctionedApplicationReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					String id = bankId + zoneId;
					sar = reportManager.getSanctionedApplicationReportForZone(
							startDate, endDate, id, flag);
					dynaForm.set("sar", sar);
					if (sar == null || sar.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"sanctionedApplicationReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String id = bankId;
					sar = reportManager.getSanctionedApplicationReportForBank(
							startDate, endDate, id, flag);
					dynaForm.set("sar", sar);
					if (sar == null || sar.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"sanctionedApplicationReport", "Exited");
						return mapping.findForward("success");
					}
				}
			}

			else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
		}
	}

	public ActionForward danReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument20");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
			// System.out.println("startDate:"+startDate);

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
			// System.out.println("startDate:"+startDate);

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument21");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("endDate:"+endDate);
		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		String ssi = (String) dynaForm.get("ssi");
		// System.out.println("ssi:"+ssi);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			// System.out.println("manager invoked");
			dan = reportManager.getDanReport(startDate, endDate, id, ssi);
			dynaForm.set("dan", dan);
			if (dan == null || dan.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// dan = null;
				memberids = null;
				claimsProcessor = null;
				Log.log(Log.INFO, "ReportsAction", "danReport", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				// System.out.println("bankId:"+bankId);
				String zoneId = id.substring(4, 8);
				// System.out.println("zoneId:"+zoneId);
				String branchId = id.substring(8, 12);
				// System.out.println("branchId:"+branchId);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					dan = reportManager.getDanReportForBranch(startDate,
							endDate, memberId, ssi);
					dynaForm.set("dan", dan);
					if (dan == null || dan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "danReport",
								"Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {// Fix bug 07092004 - 02
														// String memberId =
														// bankId+zoneId;
					dan = reportManager.getDanReportForZone(startDate, endDate,
							bankId, ssi, zoneId);
					dynaForm.set("dan", dan);
					if (dan == null || dan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "danReport",
								"Exited");
						return mapping.findForward("success");
					}// Fix Completed
				} else {
					String memberId = bankId;
					dan = reportManager.getDanReportForBank(startDate, endDate,
							memberId, ssi);
					dynaForm.set("dan", dan);
					if (dan == null || dan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "danReport",
								"Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Enter Valid Member Id");
			}
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
	public ActionForward gfdanReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfdanReport", "Entered");
		ArrayList gfdan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument20");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
			// System.out.println("startDate:"+startDate);

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
			// System.out.println("startDate:"+startDate);

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument21");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("endDate:"+endDate);
		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		String ssi = (String) dynaForm.get("ssi");
		// System.out.println("ssi:"+ssi);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {

			gfdan = reportManager.getGFDanReport(startDate, endDate, id, ssi);
			dynaForm.set("gfdan", gfdan);
			if (gfdan == null || gfdan.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				memberids = null;
				claimsProcessor = null;
				Log.log(Log.INFO, "ReportsAction", "gfdanReport", "Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				// System.out.println("bankId:"+bankId);
				String zoneId = id.substring(4, 8);
				// System.out.println("zoneId:"+zoneId);
				String branchId = id.substring(8, 12);
				// System.out.println("branchId:"+branchId);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					gfdan = reportManager.getGFDanReportForBranch(startDate,
							endDate, memberId, ssi);
					dynaForm.set("gfdan", gfdan);
					if (gfdan == null || gfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "gfdanReport",
								"Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {// Fix bug 07092004 - 02
														// String memberId =
														// bankId+zoneId;
					gfdan = reportManager.getGFDanReportForZone(startDate,
							endDate, bankId, ssi, zoneId);
					dynaForm.set("gfdan", gfdan);
					if (gfdan == null || gfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "gfdanReport",
								"Exited");
						return mapping.findForward("success");
					}// Fix Completed
				} else {
					String memberId = bankId;
					gfdan = reportManager.getGFDanReportForBank(startDate,
							endDate, memberId, ssi);
					dynaForm.set("gfdan", gfdan);
					if (gfdan == null || gfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "gfdanReport",
								"Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Enter Valid Member Id");
			}
		}
	}

	/**
	 * 
	 * added by sukumar@path 0n 28-05-2008
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward asfdanReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfdanReport", "Entered");
		ArrayList asfdan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument20");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
			// System.out.println("startDate:"+startDate);

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
			// System.out.println("startDate:"+startDate);

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument21");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("endDate:"+endDate);
		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		String ssi = (String) dynaForm.get("ssi");
		// System.out.println("ssi:"+ssi);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			// System.out.println("manager invoked");
			asfdan = reportManager.getASFDanReport(startDate, endDate, id, ssi);
			dynaForm.set("asfdan", asfdan);
			if (asfdan == null || asfdan.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// dan = null;
				memberids = null;
				claimsProcessor = null;
				Log.log(Log.INFO, "ReportsAction", "asfdanReport", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				// System.out.println("bankId:"+bankId);
				String zoneId = id.substring(4, 8);
				// System.out.println("zoneId:"+zoneId);
				String branchId = id.substring(8, 12);
				// System.out.println("branchId:"+branchId);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					asfdan = reportManager.getASFDanReportForBranch(startDate,
							endDate, memberId, ssi);
					dynaForm.set("asfdan", asfdan);
					if (asfdan == null || asfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "asfdanReport",
								"Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {// Fix bug 07092004 - 02
														// String memberId =
														// bankId+zoneId;
					asfdan = reportManager.getASFDanReportForZone(startDate,
							endDate, bankId, ssi, zoneId);
					dynaForm.set("asfdan", asfdan);
					if (asfdan == null || asfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "asfdanReport",
								"Exited");
						return mapping.findForward("success");
					}// Fix Completed
				} else {
					String memberId = bankId;
					asfdan = reportManager.getASFDanReportForBank(startDate,
							endDate, memberId, ssi);
					dynaForm.set("asfdan", asfdan);
					if (asfdan == null || asfdan.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						memberids = null;
						claimsProcessor = null;
						// dan = null;
						Log.log(Log.INFO, "ReportsAction", "asfdanReport",
								"Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Enter Valid Member Id");
			}
		}
	}

	public ActionForward danReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danReportDetails", "Entered");
		ArrayList danDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		// MliDetails mliDetails = new MliDetails();
		String danId = request.getParameter("danValue");
		// System.out.println("danId:"+danId);
		String ssiName = (String) dynaForm.get("ssi");
		// System.out.println("ssiName:"+ssiName);
		if ((ssiName == null) || (ssiName.equals(""))) {
			// System.out.println("ssiName is Null");
			danDetails = reportManager.getDanReportDetails(danId);
			dynaForm.set("dan", danDetails);
			if (danDetails == null || danDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "danReportDetails", "Exited");
				return mapping.findForward("success");
			}
		} else {
			// System.out.println("ssiName is not Null");
			danDetails = reportManager
					.getDanReportDetailsForSsi(danId, ssiName);
			dynaForm.set("dan", danDetails);
			if (danDetails == null || danDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "danReportDetails", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward disbursementReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "disbursementReport", "Entered");
		ArrayList disbursement = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument18");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument19");

		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			disbursement = reportManager.getDisbursementReport(startDate,
					endDate, id);
			dynaForm.set("disbursement", disbursement);
			if (disbursement == null || disbursement.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "disbursementReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					disbursement = reportManager
							.getDisbursementReportForBranch(startDate, endDate,
									memberId);
					dynaForm.set("disbursement", disbursement);

					if (disbursement == null || disbursement.size() == 0)
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"disbursementReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					disbursement = reportManager.getDisbursementReportForZone(
							startDate, endDate, bankId, zoneId);
					dynaForm.set("disbursement", disbursement);

					if (disbursement == null || disbursement.size() == 0)
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"disbursementReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					disbursement = reportManager.getDisbursementReportForBank(
							startDate, endDate, memberId);
					dynaForm.set("disbursement", disbursement);
					if (disbursement == null || disbursement.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						// disbursement = null;
						Log.log(Log.INFO, "ReportsAction",
								"disbursementReport", "Exited");
						return mapping.findForward("success");
					}
				}
			}

			else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
		}
	}

	/**
	 * added by sukumar 14-04-2008 for ASF Allocated Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward asfallocatedpaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfallocatedpaymentReport",
				"Entered");
		ArrayList asfallocatedpayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("StartDate:"+stDate);
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		String dantype=(String) dynaForm.get("dantype");
		 System.out.println("dantype:"+dantype);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			asfallocatedpayment = reportManager.getAllocatedPaymentReport(
					startDate, endDate, id,dantype);
			dynaForm.set("asfallocatedpayment", asfallocatedpayment);
			if (asfallocatedpayment == null || asfallocatedpayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "asfallocatedpaymentReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					asfallocatedpayment = reportManager
							.getAllocatedPaymentReportForBranch(startDate,
									endDate, memberId);
					dynaForm.set("asfallocatedpayment", asfallocatedpayment);
					if (asfallocatedpayment == null
							|| asfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"asfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					asfallocatedpayment = reportManager
							.getAllocatePaymentReportForZone(startDate,
									endDate, bankId, zoneId,dantype);
					dynaForm.set("asfallocatedpayment", asfallocatedpayment);
					if (asfallocatedpayment == null
							|| asfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"asfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					asfallocatedpayment = reportManager
							.getAllocatePaymentReportForBank(startDate,
									endDate, memberId);
					dynaForm.set("asfallocatedpayment", asfallocatedpayment);
					if (asfallocatedpayment == null
							|| asfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"asfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
		}
	}

	/* ------------------------ */

	public ActionForward gfallocatedpaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfallocatedpaymentReport",
				"Entered");
		ArrayList gfallocatedpayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bank = user.getBankId();
		String zone = user.getZoneId();
		String branch = user.getBranchId();
		// System.out.println("Bank:"+bank+"Zone:"+zone+"Branch:"+branch);

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("StartDate:"+stDate);
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		// System.out.println("Member Id:"+id);
		// System.out.println("EndDate:"+endDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			gfallocatedpayment = reportManager.getGFAllocatedPaymentReport(
					startDate, endDate, id);
			dynaForm.set("gfallocatedpayment", gfallocatedpayment);
			if (gfallocatedpayment == null || gfallocatedpayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "gfallocatedpaymentReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					gfallocatedpayment = reportManager
							.getGFAllocatedPaymentReportForBranch(startDate,
									endDate, memberId);
					dynaForm.set("gfallocatedpayment", gfallocatedpayment);
					if (gfallocatedpayment == null
							|| gfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"gfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					gfallocatedpayment = reportManager
							.getGFAllocatePaymentReportForZone(startDate,
									endDate, bankId, zoneId);
					dynaForm.set("gfallocatedpayment", gfallocatedpayment);
					if (gfallocatedpayment == null
							|| gfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"gfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					gfallocatedpayment = reportManager
							.getGFAllocatePaymentReportForBank(startDate,
									endDate, memberId);
					dynaForm.set("gfallocatedpayment", gfallocatedpayment);
					if (gfallocatedpayment == null
							|| gfallocatedpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"gfallocatedpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
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
	//paymentReport
	public ActionForward paymentReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		

		Log.log(Log.INFO, "ReportsAction", "paymentReport", "Entered");
		ArrayList payment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		/*if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)*/
		
		if ((sDate == null)||(stDate.equals("")))
			startDate = null;
		else if (sDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");
		endDate = new java.sql.Date(eDate.getTime());
		
		//System.out.println("StartDate:"+sDate+" ========jkjk==========endDate :"+eDate);
		//System.out.println("sql date:"+startDate+" ========jkjk==========sqlDate :"+endDate);
		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		// System.out.println("EndDate:"+endDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			payment = reportManager.getPaymentReport(startDate, endDate, id);
			dynaForm.set("payment", payment);
			if (payment == null || payment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "paymentReport", "Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					payment = reportManager.getPaymentReportForBranch(
							startDate, endDate, memberId);
					dynaForm.set("payment", payment);
					if (payment == null || payment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction", "paymentReport",
								"Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					// System.out.println("memberid1:"+memberId);
					payment = reportManager.getPaymentReportForZone(startDate,
							endDate, bankId, zoneId);
					dynaForm.set("payment", payment);
					if (payment == null || payment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction", "paymentReport",
								"Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					// System.out.println("memberid:"+memberId);
					payment = reportManager.getPaymentReportForBank(startDate,
							endDate, memberId);
					dynaForm.set("payment", payment);
					if (payment == null || payment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction", "paymentReport",
								"Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
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

	public ActionForward dailypaymentReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailypaymentReportDetails",
				"Entered");
		ArrayList dailypaymentDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		// System.out.println("date:"+request.getParameter("date"));
		String id = request.getParameter("memId");
		// System.out.println("memberId:"+id);

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		String stDate = (String) request.getParameter("date");
		// System.out.println("StartDate:"+stDate);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d;
		try {
			d = dateFormat.parse(stDate);
			dateFormat.applyPattern("yyyy-MM-dd");
			stDate = dateFormat.format(d);
			// System.out.println("stDate:"+stDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		startDate = java.sql.Date.valueOf(stDate);
		endDate = java.sql.Date.valueOf(stDate);

		// System.out.println("startDate:"+startDate);
		// System.out.println("endDate:"+endDate);

		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailypaymentDetails = reportManager.getPaymentReport(startDate,
					endDate, id);
			dynaForm.set("dailypaymentDetails", dailypaymentDetails);
			if (dailypaymentDetails == null || dailypaymentDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "dailypaymentReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		} else if (memberids.contains(id)) {

			String bankId = id.substring(0, 4);
			String zoneId = id.substring(4, 8);
			String branchId = id.substring(8, 12);

			if (!(branchId.equals("0000"))) {
				String memberId = bankId + zoneId + branchId;
				// System.out.println(memberId);
				dailypaymentDetails = reportManager.getPaymentReportForBranch(
						startDate, endDate, memberId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailypaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}

			} else if (!(zoneId.equals("0000"))) {
				// System.out.println("memberid1:"+memberId);
				dailypaymentDetails = reportManager.getPaymentReportForZone(
						startDate, endDate, bankId, zoneId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailypaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			} else {
				String memberId = bankId;
				// System.out.println("memberid:"+memberId);
				dailypaymentDetails = reportManager.getPaymentReportForBank(
						startDate, endDate, memberId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailypaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			}

		}
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
	public ActionForward dailyDCHpaymentReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailyDCHpaymentReportDetails",
				"Entered");
		ArrayList dailydchpaymentDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String id = request.getParameter("memId");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		String stDate = (String) request.getParameter("date");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d;
		try {
			d = dateFormat.parse(stDate);
			dateFormat.applyPattern("yyyy-MM-dd");
			stDate = dateFormat.format(d);
			// System.out.println("stDate:"+stDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		startDate = java.sql.Date.valueOf(stDate);
		endDate = java.sql.Date.valueOf(stDate);

		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailydchpaymentDetails = reportManager.getDCHPaymentReport(
					startDate, endDate, id);
			dynaForm.set("dailydchpaymentDetails", dailydchpaymentDetails);
			if (dailydchpaymentDetails == null
					|| dailydchpaymentDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "dailypaymentReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		} else if (memberids.contains(id)) {

			String bankId = id.substring(0, 4);
			String zoneId = id.substring(4, 8);
			String branchId = id.substring(8, 12);

			if (!(branchId.equals("0000"))) {
				String memberId = bankId + zoneId + branchId;
				// System.out.println(memberId);
				dailydchpaymentDetails = reportManager
						.getDCHPaymentReportForBranch(startDate, endDate,
								memberId);
				dynaForm.set("dailydchpaymentDetails", dailydchpaymentDetails);
				if (dailydchpaymentDetails == null
						|| dailydchpaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailypaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}

			} else if (!(zoneId.equals("0000"))) {
				// System.out.println("memberid1:"+memberId);
				dailydchpaymentDetails = reportManager
						.getDCHPaymentReportForZone(startDate, endDate, bankId,
								zoneId);
				dynaForm.set("dailydchpaymentDetails", dailydchpaymentDetails);
				if (dailydchpaymentDetails == null
						|| dailydchpaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailypaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			} else {
				String memberId = bankId;
				// System.out.println("memberid:"+memberId);
				dailydchpaymentDetails = reportManager
						.getDCHPaymentReportForBank(startDate, endDate,
								memberId);
				dynaForm.set("dailydchpaymentDetails", dailydchpaymentDetails);
				if (dailydchpaymentDetails == null
						|| dailydchpaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailyDCHpaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			}

		}
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
	public ActionForward dailyasfpaymentReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailyasfpaymentReportDetails",
				"Entered");
		ArrayList dailypaymentDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		// System.out.println("date:"+request.getParameter("date"));
		String id = request.getParameter("memId");
		// System.out.println("memberId:"+id);

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		String stDate = (String) request.getParameter("date");
		// System.out.println("StartDate:"+stDate);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d;
		try {
			d = dateFormat.parse(stDate);
			dateFormat.applyPattern("yyyy-MM-dd");
			stDate = dateFormat.format(d);
			// System.out.println("stDate:"+stDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		startDate = java.sql.Date.valueOf(stDate);
		endDate = java.sql.Date.valueOf(stDate);

		// System.out.println("startDate:"+startDate);
		// System.out.println("endDate:"+endDate);

		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailypaymentDetails = reportManager.getASFPaymentReportNew(
					startDate, endDate, id);
			dynaForm.set("dailypaymentDetails", dailypaymentDetails);
			if (dailypaymentDetails == null || dailypaymentDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction",
						"dailyasfpaymentReportDetails", "Exited");
				return mapping.findForward("success");
			}
		} else if (memberids.contains(id)) {

			String bankId = id.substring(0, 4);
			String zoneId = id.substring(4, 8);
			String branchId = id.substring(8, 12);

			if (!(branchId.equals("0000"))) {
				String memberId = bankId + zoneId + branchId;
				// System.out.println(memberId);
				dailypaymentDetails = reportManager
						.getASFPaymentReportForBranchNew(startDate, endDate,
								memberId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailyasfpaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}

			} else if (!(zoneId.equals("0000"))) {
				// System.out.println("memberid1:"+memberId);
				dailypaymentDetails = reportManager
						.getASFPaymentReportForZoneNew(startDate, endDate,
								bankId, zoneId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailyasfpaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			} else {
				String memberId = bankId;
				// System.out.println("memberid:"+memberId);
				dailypaymentDetails = reportManager
						.getASFPaymentReportForBankNew(startDate, endDate,
								memberId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(Log.INFO, "ReportsAction",
							"dailyasfpaymentReportDetails", "Exited");
					return mapping.findForward("success");
				}
			}

		}
		return mapping.findForward("success");

	}

	public ActionForward dailypaymentReportInputforASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "dailypaymentReportInput", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument16(prevDate);
		generalReport.setDateOfTheDocument17(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("memberId", memberId);
		Log.log(4, "ReportsAction", "dailypaymentReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward dailypaymentReportDetailsforASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "dailypaymentReportDetails", "Entered");
		ArrayList dailypaymentDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String id = request.getParameter("memId");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String stDate = request.getParameter("date");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date d = dateFormat.parse(stDate);
			dateFormat.applyPattern("yyyy-MM-dd");
			stDate = dateFormat.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		startDate = java.sql.Date.valueOf(stDate);
		endDate = java.sql.Date.valueOf(stDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		if (id == null || id.equals("")) {
			dailypaymentDetails = reportManager.getPaymentReportforRSF(
					startDate, endDate, id);
			dynaForm.set("dailypaymentDetails", dailypaymentDetails);
			if (dailypaymentDetails == null || dailypaymentDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered, Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(4, "ReportsAction", "dailypaymentReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		}
		if (memberids.contains(id)) {
			String bankId = id.substring(0, 4);
			String zoneId = id.substring(4, 8);
			String branchId = id.substring(8, 12);
			if (!branchId.equals("0000")) {
				String memberId = (new StringBuilder()).append(bankId)
						.append(zoneId).append(branchId).toString();
				dailypaymentDetails = reportManager
						.getPaymentReportForBranchforRSF(startDate, endDate,
								memberId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered, Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(4, "ReportsAction", "dailypaymentReportDetails",
							"Exited");
					return mapping.findForward("success");
				}
			}
			if (!zoneId.equals("0000")) {
				dailypaymentDetails = reportManager
						.getPaymentReportForZoneforRSF(startDate, endDate,
								bankId, zoneId);
				dynaForm.set("dailypaymentDetails", dailypaymentDetails);
				if (dailypaymentDetails == null
						|| dailypaymentDetails.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered, Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(4, "ReportsAction", "dailypaymentReportDetails",
							"Exited");
					return mapping.findForward("success");
				}
			}
			String memberId = bankId;
			dailypaymentDetails = reportManager.getPaymentReportForBankforRSF(
					startDate, endDate, memberId);
			dynaForm.set("dailypaymentDetails", dailypaymentDetails);
			if (dailypaymentDetails == null || dailypaymentDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered, Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(4, "ReportsAction", "dailypaymentReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			return mapping.findForward("success");
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
	public ActionForward dailypaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailypaymentReport", "Entered");
		ArrayList dailypayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		java.util.Date sDate = (java.util.Date) dynaForm.get("dateOfTheDocument16");
		//System.out.println("sDate==="+sDate);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date parsedStartDate = format.parse(dynaForm.get("dateOfTheDocument16").toString());
		java.sql.Date sqlStartDate = new java.sql.Date(parsedStartDate.getTime());
		Date parsedendDate = format.parse(dynaForm.get("dateOfTheDocument17").toString());
		java.sql.Date sqlEndDate = new java.sql.Date(parsedendDate.getTime());
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		startDate = sqlStartDate;

		java.util.Date eDate = (java.util.Date) dynaForm.get("dateOfTheDocument17");
		 //System.out.println("eDate........"+eDate);
		// System.out.println("e Date:"+eDate);
		// System.out.println("0"+sDate.getTime());
		// System.out.println("1"+eDate.getTime());
		 ///Ajit            03/03/2020          <----29/02/2020
		//endDate = new java.sql.Date(eDate.getTime());
		 endDate = new java.sql.Date(eDate.getTime());
		endDate = sqlEndDate;

		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		// System.out.println("EndDate:"+endDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailypayment = reportManager.getdailyPaymentReport(startDate,
					endDate, id);
			dynaForm.set("dailypayment", dailypayment);
			if (dailypayment == null || dailypayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "dailypaymentReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					dailypayment = reportManager
							.getdailyPaymentReportForBranch(startDate, endDate,
									memberId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailypaymentReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					// System.out.println("memberid1:"+memberId);
					dailypayment = reportManager.getdailyPaymentReportForZone(
							startDate, endDate, bankId, zoneId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailypaymentReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					// System.out.println("memberid:"+memberId);
					dailypayment = reportManager.getdailyPaymentReportForBank(
							startDate, endDate, memberId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailypaymentReport", "Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
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
	public ActionForward dailydchpaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailydchpaymentReport", "Entered");
		ArrayList dailydchpayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("StartDate:"+stDate);
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");

		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		// System.out.println("EndDate:"+endDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailydchpayment = reportManager.getdailydchPaymentReport(startDate,
					endDate, id);
			dynaForm.set("dailydchpayment", dailydchpayment);
			if (dailydchpayment == null || dailydchpayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "dailydchpaymentReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					dailydchpayment = reportManager
							.getdailyDCHPaymentReportForBranch(startDate,
									endDate, memberId);
					dynaForm.set("dailydchpayment", dailydchpayment);
					if (dailydchpayment == null || dailydchpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailydchpaymentReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					// System.out.println("memberid1:"+memberId);
					dailydchpayment = reportManager
							.getdailyDCHPaymentReportForZone(startDate,
									endDate, bankId, zoneId);
					dynaForm.set("dailydchpayment", dailydchpayment);
					if (dailydchpayment == null || dailydchpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailydchpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					// System.out.println("memberid:"+memberId);
					dailydchpayment = reportManager
							.getdailyDCHPaymentReportForBank(startDate,
									endDate, memberId);
					dynaForm.set("dailydchpayment", dailydchpayment);
					if (dailydchpayment == null || dailydchpayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailydchpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
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
	public ActionForward dailyasfpaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "dailyasfpaymentReport", "Entered");
		ArrayList dailypayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("StartDate:"+stDate);
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument17");

		// System.out.println("e Date:"+eDate);
		// System.out.println("0"+sDate.getTime());
		// System.out.println("1"+eDate.getTime());

		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		// System.out.println("EndDate:"+endDate);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		if (id == null || id.equals("")) {
			dailypayment = reportManager.getasfdailyPaymentReport(startDate,
					endDate, id);
			dynaForm.set("dailypayment", dailypayment);
			if (dailypayment == null || dailypayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(Log.INFO, "ReportsAction", "dailyasfpaymentReport",
						"Exited");
				return mapping.findForward("success");
			}
		} else {
			if (memberids.contains(id)) {
				String bankId = id.substring(0, 4);
				String zoneId = id.substring(4, 8);
				String branchId = id.substring(8, 12);

				if (!(branchId.equals("0000"))) {
					String memberId = bankId + zoneId + branchId;
					// System.out.println(memberId);
					dailypayment = reportManager
							.getASFdailyPaymentReportForBranch(startDate,
									endDate, memberId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailyasfpaymentReport", "Exited");
						return mapping.findForward("success");
					}

				} else if (!(zoneId.equals("0000"))) {
					// System.out.println("memberid1:"+memberId);
					dailypayment = reportManager
							.getASFdailyPaymentReportForZone(startDate,
									endDate, bankId, zoneId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailyasfpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				} else {
					String memberId = bankId;
					// System.out.println("memberid:"+memberId);
					dailypayment = reportManager
							.getASFdailyPaymentReportForBank(startDate,
									endDate, memberId);
					dynaForm.set("dailypayment", dailypayment);
					if (dailypayment == null || dailypayment.size() == 0) {
						throw new NoDataException(
								"No Data is available for the values entered,"
										+ " Please Enter Any Other Value ");
					} else {
						claimsProcessor = null;
						memberids = null;
						Log.log(Log.INFO, "ReportsAction",
								"dailyasfpaymentReport", "Exited");
						return mapping.findForward("success");
					}
				}
			} else {
				throw new NoDataException("Please Enter Valid Member Id");
			}
		}
	}

	/**
	 * 
	 * ADDED BY SUKUMR@PATH 28-04-2008
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	 public ActionForward asfpaymentReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
     Log.log(4, "ReportsAction", "asfpaymentReport", "Entered");
     ArrayList asfpayment = new ArrayList();
     DynaActionForm dynaForm = (DynaActionForm)form;
     java.sql.Date startDate = null;
     java.sql.Date endDate = null;
     Date sDate = (Date)dynaForm.get("dateOfTheDocument16");
     String stDate = String.valueOf(sDate);
     if(stDate == null || stDate.equals(""))
         startDate = null;
     else
         startDate = new java.sql.Date(sDate.getTime());
     Date eDate = (Date)dynaForm.get("dateOfTheDocument17");
     endDate = new java.sql.Date(eDate.getTime());
     String id = (String)dynaForm.get("memberId");
     String status=(String)dynaForm.get("dantype");
     System.out.println("status====="+status);        
     ClaimsProcessor claimsProcessor = new ClaimsProcessor();
     Vector memberids = new Vector();
     memberids = claimsProcessor.getAllMemberIds();
     if(status.equals("null")||status.equals(""))
     {
    	 throw new Exception("please select dantype."); 
     }
     if(id == null || id.equals(""))
     {
         asfpayment = reportManager.getASFPaymentReport(startDate, endDate, id,status);
         dynaForm.set("asfpayment", asfpayment);
         if(asfpayment == null || asfpayment.size() == 0)
         {
             throw new NoDataException("No Data is available for the values entered, Please Enter Any Other Value ");
         } else
         {
             claimsProcessor = null;
             memberids = null;
             Log.log(4, "ReportsAction", "asfpaymentReport", "Exited");
             return mapping.findForward("success");
         }
     }
     if(memberids.contains(id))
     {
         String bankId = id.substring(0, 4);
         String zoneId = id.substring(4, 8);
         String branchId = id.substring(8, 12);
         String memberId;
         if(!branchId.equals("0000"))
         {
             memberId = (new StringBuilder(String.valueOf(bankId))).append(zoneId).append(branchId).toString();
             asfpayment = reportManager.getASFPaymentReportForBranch(startDate, endDate, memberId,status);
             dynaForm.set("asfpayment", asfpayment);
             if(asfpayment == null || asfpayment.size() == 0)
             {
                 throw new NoDataException("No Data is available for the values entered, Please Enter Any Other Value ");
             } else
             {
                 claimsProcessor = null;
                 memberids = null;
                 Log.log(4, "ReportsAction", "asfpaymentReport", "Exited");
                 return mapping.findForward("success");
             }
         }
         if(!zoneId.equals("0000"))
         {
             asfpayment = reportManager.getASFPaymentReportForZone(startDate, endDate, bankId, zoneId,status);
             dynaForm.set("asfpayment", asfpayment);
             if(asfpayment == null || asfpayment.size() == 0)
             {
                 throw new NoDataException("No Data is available for the values entered, Please Enter Any Other Value ");
             } else
             {
                 claimsProcessor = null;
                 memberids = null;
                 Log.log(4, "ReportsAction", "asfpaymentReport", "Exited");
                 return mapping.findForward("success");
             }
         }
         memberId = bankId;
         asfpayment = reportManager.getASFPaymentReportForBank(startDate, endDate, memberId,status);
         dynaForm.set("asfpayment", asfpayment);
         if(asfpayment == null || asfpayment.size() == 0)
         {
             throw new NoDataException("No Data is available for the values entered, Please Enter Any Other Value ");
         } else
         {
             claimsProcessor = null;
             memberids = null;
             Log.log(4, "ReportsAction", "asfpaymentReport", "Exited");
             return mapping.findForward("success");
         }
     } else
     {
         throw new NoDataException("Please Enter Valid Member Id");
     }
 }
	/* ------------------------------- */

	public ActionForward listOfMLI(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "listOfMLI", "Entered");
		ArrayList mli = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mli = reportManager.getMliListForBranch(memberId);
			dynaForm.set("mli", mli);

			if (mli == null || mli.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				Log.log(Log.INFO, "ReportsAction", "listOfMLI", "Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			mli = reportManager.getMliListForZone(bankId, zoneId);
			dynaForm.set("mli", mli);
			if (mli == null || mli.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				Log.log(Log.INFO, "ReportsAction", "listOfMLI", "Exited");
				return mapping.findForward("success1");
			}
		} else if (!(bankId.equals("0000"))) {
			String memberId = bankId;
			mli = reportManager.getMliListForBank(memberId);
			dynaForm.set("mli", mli);
			if (mli == null || mli.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				Log.log(Log.INFO, "ReportsAction", "listOfMLI", "Exited");
				return mapping.findForward("success1");
			}
		}

		else {
			mli = reportManager.getMliList();
			dynaForm.set("mli", mli);
			if (mli == null || mli.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				Log.log(Log.INFO, "ReportsAction", "listOfMLI", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	// ////Start Code Added By Path 0n 10Oct06. //
	// Created new Function to show the MLIs list on fron page of the
	// application////////
	public ActionForward listOfMLIPath(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "listOfMLIPath", "Entered");
		ArrayList mli = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		// String bankId = mliInfo.getBankId();
		// String zoneId = mliInfo.getZoneId();
		// String branchId = mliInfo.getBranchId();

		// System.out.println("cgtsi user");
		mli = reportManager.getMliList();
		dynaForm.set("mli", mli);
		if (mli == null || mli.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			mli = null;
			Log.log(Log.INFO, "ReportsAction", "listOfMLIPath", "Exited");
			return mapping.findForward("success1");
		}
	}

	// ///Start Code Added By Path 0n 10Oct06////////
	// ////Start Code Added By Path 0n 20Nov2006. //
	// Created new Function to show the Summary report on click on
	// approval////////
	public ActionForward showMliListPath(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showMliListPath", "Entered");
		ArrayList mli = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		mli = reportManager.getAppListMliWise();
		dynaForm.set("mli", mli);
		String forward = "";
		if (mli == null || mli.size() == 0) {
			Log.log(Log.INFO, "ReportsAction", "showMliListPath", "Exited");
			request.setAttribute("message",
					"No Applications for Approval Available");
			forward = "success";
			// throw new
			// NoDataException("No Applications for Approval Available");
		} else {
			mli = null;
			Log.log(Log.INFO, "ReportsAction", "showMliListPath", "Exited");
			forward = "approvalPageList";
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
	public ActionForward showClaimMliListPath(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Entered");
		ArrayList mli = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		mli = reportManager.getClaimAppListMliWise();
		dynaForm.set("mli", mli);
		String forward = "";
		if (mli == null || mli.size() == 0) {
			Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Exited");
			request.setAttribute("message",
					"No Forwarded Applications for Claim Approval Available");
			forward = "success";

		} else {
			mli = null;
			Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Exited");
			forward = "clmapprovalPageList";
		}
		return mapping.findForward(forward);
	}

	// Diksha...................................
	public ActionForward showApplicationStatus(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		String appRefNO = (String) dynaForm.get("appRefNo");
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			String appDetails = (new StringBuilder(
					"select APP_REF_NO,APP_STATUS,cgpan,APP_REMARKS \nfrom application_detail \nwhere  APP_REF_NO='"))
					.append(appRefNO)
					.append("' \nunion  \nselect APP_REF_NO,APP_STATUS,cgpan,APP_REMARKS \nfrom application_detail_temp \nwhere  APP_REF_NO='")
					.append(appRefNO).append("'").toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(appDetails);
			String APP_REF_NO = "";
			String CGPAN = "";
			String APP_STATUS = "";
			String APP_REMARKS = "";
			if (rs.next()) {
				APP_REF_NO = rs.getString("APP_REF_NO");
				CGPAN = rs.getString("CGPAN");
				APP_STATUS = rs.getString("APP_STATUS");
				APP_REMARKS = rs.getString("APP_REMARKS");
			} else {
				throw new NoMemberFoundException(
						"Entered  Number not Available. please enter correct Application Reference Number: ");
			}
			request.setAttribute("APP_REF_NO", APP_REF_NO);
			request.setAttribute("CGPAN", CGPAN);
			request.setAttribute("APP_STATUS", APP_STATUS);
			request.setAttribute("APP_REMARKS", APP_REMARKS);
		} catch (Exception e) {
			throw new NoMemberFoundException(
					(new StringBuilder(
							"Something Went wrong, Kindly contact to support Team(support@cgtmse.in) "))
							.append(e.getMessage()).toString());
		}
		return mapping.findForward("success");
	}

	// Diksha end.....................................
	// Diksha 02/11/2017

	public ActionForward searchHistory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(Log.INFO, "ReportsAction", "applicationReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward showSearchHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showSearchHistory", "Entered");
		ArrayList searchHistory = new ArrayList();
		ArrayList itpans = new ArrayList();
		Application appReport = new Application();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String itpan = (String) dynaForm.get("itpan");
		// String ssiName = (String) dynaForm.get("enterSSI");

		ReportDAO rpDao = new ReportDAO();

		// cgpans = reportManager.getAllCgpans();
		String newItpan = itpan.toUpperCase();
		searchHistory = rpDao.getSearchHistory(itpan);
		dynaForm.set("searchHistory", searchHistory);
		// System.out.println("newCgpan:"+newCgpan);
		/* String key = appReport.getItpan(); */

		/* System.out.println("Itpan:"+appReport.getItpan()); */

		if (itpan == null || itpan.equals("")) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");

		}

		// dynaForm.set("application",appReport);
		return mapping.findForward("success");

	}

	// Diksha 02/11/2017

	// ///End Code Added By Path 0n 10Oct06////////
	public ActionForward listOfMLIReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "listOfMLIReport", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String bank = request.getParameter("Link");
		// System.out.println("Bank:"+bank);
		mliReport = reportManager.getMliListReport(bank);
		dynaForm.set("mliReport", mliReport);

		request.setAttribute("BANKNAME", bank);

		if (mliReport == null || mliReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "listOfMLIReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward mliWisePendingReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWisePendingReport", "Entered");
		ArrayList mliPendingReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		mliPendingReport = reportManager.getMliPendingReport();
		dynaForm.set("mliPending", mliPendingReport);
		if (mliPendingReport == null || mliPendingReport.size() == 0) {
			throw new NoDataException("There are no Pending  Applications");
		} else {
			mliPendingReport = null;
			Log.log(Log.INFO, "ReportsAction", "mliWisePendingReport", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward stateWiseReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateWiseReport", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument10(prevDate);
		generalReport.setDateOfTheDocument11(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "stateWiseReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward stateWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetails", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ArrayList mliReport = new ArrayList();
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("startDate:"+startDate);
		// System.out.println("endDate:"+endDate);
		String id = (String) dynaForm.get("checkValue");
		// System.out.println("id:"+id);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			// System.out.println("memberId:"+memberId);
			mliReport = reportManager.getStateBranchApplicationDetails(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetails",
						"Exited");
				return mapping.findForward("success1");
			}

		} else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			// System.out.println("memberId:"+memberId);
			mliReport = reportManager.getStateZoneApplicationDetails(startDate,
					endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				// System.out.println("ReportsAction"+"stateWiseReportDetails"+"Exited");
				Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetails",
						"Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			String memberId = bankId;
			mliReport = reportManager.getStateBankApplicationDetails(startDate,
					endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetails",
						"Exited");
				return mapping.findForward("success3");
			}
		} else {
			mliReport = reportManager.getStateWiseReportDetails(startDate,
					endDate, id);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateWiseReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward stateDetailsForState(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateDetailsForState", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ArrayList mliReport = new ArrayList();
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getBranchApplicationDetailsForState(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForState",
						"Exited");
				return mapping.findForward("success1");
			}

		} else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			mliReport = reportManager.getZoneApplicationDetailsForState(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForState",
						"Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			String memberId = bankId;
			mliReport = reportManager.getBankApplicationDetailsForState(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForState",
						"Exited");
				return mapping.findForward("success3");
			}
		} else {
			mliReport = reportManager.getMliApplicationDetails(startDate,
					endDate, id);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForState",
						"Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward districtDetailsForState(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtDetailsForState", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ArrayList mliReport = new ArrayList();
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getBranchApplicationDetailsForDistrict(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "districtDetailsForState",
						"Exited");
				return mapping.findForward("success1");
			}

		}

		else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			mliReport = reportManager.getZoneApplicationDetailsForDistrict(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "districtDetailsForState",
						"Exited");
				return mapping.findForward("success1");
			}

		} else {
			String memberId = bankId;
			mliReport = reportManager.getBankApplicationDetailsForDistrict(
					startDate, endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "districtDetailsForState",
						"Exited");
				return mapping.findForward("success2");
			}
		}
	}

	public ActionForward statusWiseReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statusWiseReport", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument14(prevDate);
		generalReport.setDateOfTheDocument15(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "statusWiseReport", "Exited");
		return mapping.findForward("success");
	}

	// Fix Bug 02 - 07092004
	public ActionForward statusWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statusWiseReportDetails", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String application = (String) dynaForm.get("applicationStatus");
		// System.out.println("application status:"+application);
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument14");
		String stDate = String.valueOf(sDate);
		// System.out.println("stDate:"+stDate);
		/*
		 * setting requeste attribute to display checked button value in
		 * reporting jsp
		 */
		if (application.equals("NE"))
			request.setAttribute("radioValue", "New");
		else if (application.equals("AP"))
			request.setAttribute("radioValue", "Approved");
		else if (application.equals("PE"))
			request.setAttribute("radioValue", "Pending");
		else if (application.equals("CL"))
			request.setAttribute("radioValue", "CLosed");
		else if (application.equals("HO"))
			request.setAttribute("radioValue", "HOld");
		else if (application.equals("MO"))
			request.setAttribute("radioValue", "Modified");
		else if (application.equals("RE"))
			request.setAttribute("radioValue", "Rejected");
		else if (application.equals("EX"))
			request.setAttribute("radioValue", "Expired");

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;

		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument15");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			// System.out.println("testA:");
			if ((application.equals("PE")) || (application.equals("RE"))
					|| (application.equals("NE")) || (application.equals("MO"))
					|| (application.equals("HO"))) {
				String memberId = bankId + zoneId + branchId;
				// mliReport =
				// reportManager.getStatusDetailsForBranch(startDate,endDate,memberId,application);
				mliReport = reportManager.getStatusDetailsForBranch(startDate,
						endDate, memberId, application);

				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// System.out.println("testB:");
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success1");
				}
			} else {
				// System.out.println("testC:");
				String memberId = bankId + zoneId + branchId;
				// mliReport =
				// reportManager.getStatusDetailsForBranch1(startDate,endDate,memberId,application);
				mliReport = reportManager.StatusDetailsForBranchMod(startDate,
						endDate, memberId, application);

				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success4");
				}
			}
		} else if (!(zoneId.equals("0000"))) {
			// System.out.println("testD:");
			if ((application.equals("PE")) || (application.equals("RE"))
					|| (application.equals("NE")) || (application.equals("MO"))
					|| (application.equals("HO"))) {
				mliReport = reportManager.getStatusDetailsForZone(startDate,
						endDate, bankId, zoneId, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// System.out.println("testE:");
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success2");
				}
			} else {
				// System.out.println("testF:");
				mliReport = reportManager.getStatusDetailsForZone1(startDate,
						endDate, bankId, zoneId, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {

					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success5");
				}
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("testG:");
			if ((application.equals("PE")) || (application.equals("RE"))
					|| (application.equals("NE")) || (application.equals("MO"))
					|| (application.equals("HO"))) {
				String memberId = bankId;
				mliReport = reportManager.getStatusDetailsForBank(startDate,
						endDate, memberId, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// System.out.println("testH:");
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success3");
				}
			} else {
				// System.out.println("test:");
				String memberId = bankId;
				mliReport = reportManager.getStatusDetailsForBank1(startDate,
						endDate, memberId, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success6");
				}
			}
		} else {
			if ((application.equals("PE")) || (application.equals("RE"))
					|| (application.equals("NE")) || (application.equals("MO"))
					|| (application.equals("HO"))) {
				// System.out.println("CGTSI--PE/RE");
				mliReport = reportManager.getStatusWiseReportDetails(startDate,
						endDate, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success");
				}
			} else {
				// System.out.println("CGTSI--AP/PE/CL/MO/NE");
				mliReport = reportManager.getStatusWiseReportDetails1(
						startDate, endDate, application);
				dynaForm.set("mliWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"statusWiseReportDetails", "Exited");
					return mapping.findForward("success7");
				}
			}
		}
	}

	// Fix Completed

	public ActionForward applicationWiseReportDetailsForCgpan(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction",
				"applicationWiseReportDetailsForCgpan", "Entered");
		ApplicationReport dan = new ApplicationReport();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String application = (String) dynaForm.get("number");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument14");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		String radio = (String) dynaForm.get("applicationStatus");

		if (radio.equals("NE"))
			request.setAttribute("radioValue", "New");
		else if (radio.equals("AP"))
			request.setAttribute("radioValue", "Approved");
		else if (radio.equals("PE"))
			request.setAttribute("radioValue", "Pending");
		else if (radio.equals("CL"))
			request.setAttribute("radioValue", "Closed");
		else if (radio.equals("HO"))
			request.setAttribute("radioValue", "Hold");
		else if (radio.equals("MO"))
			request.setAttribute("radioValue", "Modified");
		else if (radio.equals("RE"))
			request.setAttribute("radioValue", "Rejected");

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument15");
		endDate = new java.sql.Date(eDate.getTime());
		dan = reportManager.applicationWiseReportDetails(startDate, endDate,
				application);
		dynaForm.set("statusDetails", dan);
		String key = dan.getMemberId();
		String member = request.getParameter("number");
		request.setAttribute("MEMBERID", member);

		if (key == null || key.equals("")) {
			throw new NoDataException("No Data is available for this value,"
					+ " Please Choose Any Other Value ");
		} else {
			dan = null;
			Log.log(Log.INFO, "ReportsAction",
					"applicationWiseReportDetailsForCgpan", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward stateApplicationDetails1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateApplicationDetails1",
				"Entered");
		ArrayList stateReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("checkValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		String state = request.getParameter("State");

		if (flag.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (flag.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("stateName", state);

		int i = state.indexOf("$");
		if (i != -1) {
			String newState = state.replace('$', '&');
			stateReport = reportManager.getStateDetailsNew(newState, flag,
					startDate, endDate);
			dynaForm.set("stateReport", stateReport);
			if (stateReport == null || stateReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// stateReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateApplicationDetails1",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			stateReport = reportManager.getStateDetailsNew(state, flag,
					startDate, endDate);
			dynaForm.set("stateReport", stateReport);
			if (stateReport == null || stateReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// stateReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateApplicationDetails1",
						"Exited");
				return mapping.findForward("success");
			}

		}
	}

	public ActionForward districtApplicationDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtApplicationDetails",
				"Entered");
		ArrayList districtReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("checkValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		String district = request.getParameter("District");

		if (flag.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (flag.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("stateName", district);

		int i = district.indexOf("$");

		if (i != -1) {
			String newDistrict = district.replace('$', '&');
			districtReport = reportManager.getStateDistrictDetails(newDistrict,
					flag, startDate, endDate);
			dynaForm.set("districtReport", districtReport);
			if (districtReport == null || districtReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// districtReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"districtApplicationDetails", "Exited");
				return mapping.findForward("success");
			}
		} else {
			districtReport = reportManager.getStateDistrictDetails(district,
					flag, startDate, endDate);
			dynaForm.set("districtReport", districtReport);
			if (districtReport == null || districtReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// districtReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"districtApplicationDetails", "Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward districtApplicationDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtApplicationDetailsNew",
				"Entered");
		ArrayList mliWiseNEDistrictReportDetailsNew = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		// String flag = (String) dynaForm.get("checkValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument10");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument11");
		endDate = new java.sql.Date(eDate.getTime());
		String District = request.getParameter("State");

		// System.out.println("District:"+District);
		// if(flag.equals("yes"))
		// request.setAttribute("radioValue","Guarantee Approved");
		// else if(flag.equals("no"))
		// request.setAttribute("radioValue","Guarantee Issued");

		request.setAttribute("stateName", District);

		int i = District.indexOf("$");

		if (i != -1) {
			String newDistrict = District.replace('$', '&');
			mliWiseNEDistrictReportDetailsNew = reportManager
					.getStateDistrictDetailsNew(newDistrict, startDate, endDate);
			dynaForm.set("mliWiseNEDistrictReportDetailsNew",
					mliWiseNEDistrictReportDetailsNew);
			if (mliWiseNEDistrictReportDetailsNew == null
					|| mliWiseNEDistrictReportDetailsNew.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// districtReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"districtApplicationDetailsNew", "Exited");
				return mapping.findForward("success");
			}
		} else {
			mliWiseNEDistrictReportDetailsNew = reportManager
					.getStateDistrictDetailsNew(District, startDate, endDate);
			dynaForm.set("mliWiseNEDistrictReportDetailsNew",
					mliWiseNEDistrictReportDetailsNew);
			if (mliWiseNEDistrictReportDetailsNew == null
					|| mliWiseNEDistrictReportDetailsNew.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// districtReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"districtApplicationDetailsNew", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward mliWiseReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseReport", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * This method is used to provide MLI wise claim details report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward mliWiseClaimReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReport", "Exited");
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
	public ActionForward mliWiseClaimSummaryReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimSummaryReport",
				"Entered");
		// System.out.println("mliWiseClaimSummaryReport Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimSummaryReport",
				"Exited");
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
	public ActionForward mliWiseClaimPendigCasesReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimPendigCasesReport",
				"Entered");
		// System.out.println("mliWiseClaimPendigCasesReport Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimPendigCasesReport",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * added by sukuamar@path on 15-Oct-2010 for provding Claim Pending Cases
	 * (Declaration Received and Not Received)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward mliWiseClaimPendingReportDetails(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimPendingReportDetails",
				"Entered");
		// System.out.println("mliWiseClaimPendingReportDetails entered");

		ArrayList mliWiseClaimSummaryReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		// System.out.println("test7:");
		mliWiseClaimSummaryReport = reportManager
				.mliWiseClaimPendingReportDetails(endDate);
		dynaForm.set("mliWiseClaimSummaryReport", mliWiseClaimSummaryReport);
		if (mliWiseClaimSummaryReport == null
				|| mliWiseClaimSummaryReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseClaimSummaryReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"mliWiseClaimPendingReportDetails", "Exited");
			return mapping.findForward("success");
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
	public ActionForward mliWiseClaimSummaryReportDetails(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimSummaryReportDetails",
				"Entered");
		// System.out.println("mliWiseClaimSummaryReportDetails entered");

		ArrayList mliWiseClaimSummaryReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		String id = (String) dynaForm.get("memberId");
		if ((id == null) || (id.equals(""))) {
			id = "";
		}
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		// System.out.println("test7:");
		mliWiseClaimSummaryReport = reportManager
				.mliWiseClaimSummaryReportDetails(endDate, id);
		dynaForm.set("mliWiseClaimSummaryReport", mliWiseClaimSummaryReport);
		if (mliWiseClaimSummaryReport == null
				|| mliWiseClaimSummaryReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseClaimSummaryReport = null;
			Log.log(Log.INFO, "ReportsAction",
					"mliWiseClaimSummaryReportDetails", "Exited");
			return mapping.findForward("success");
		}

	}

	/**
	 * 
	 * This method is used to provide State wise claim details report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward stateWiseClaimReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateWiseClaimReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "stateWiseClaimReport", "Exited");
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
	public ActionForward sectorWiseClaimReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorWiseClaimReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "sectorWiseClaimReport", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * 
	 * this method is used to provide no.of claim applications received &
	 * settled cases report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward mliWiseClaimApplReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimApplReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReport", "Exited");
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
	public ActionForward mliWiseReportForRsf(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseReportForRsf", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseReportForRsf", "Exited");
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

	public ActionForward mliWiseReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseReportNew", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseReportNew", "Exited");
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
	public ActionForward mliWiseNEReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseNEReportNew", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseNEReportNew", "Exited");
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
	public ActionForward mliWiseNEStateReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseNEStateReportNew", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument12(prevDate);
		generalReport.setDateOfTheDocument13(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "mliWiseNEStateReportNew", "Exited");
		return mapping.findForward("success");
	}
//Pandit
	public ActionForward mliWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetails", "Entered");

		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm.get("dateOfTheDocument13");
		
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);
		String id = (String) dynaForm.get("checkValue");
		// System.out.println("id:"+id);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		/*
		 * following attributes are set to display the radio button value in
		 * reporting JSP
		 */
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		if (!(branchId.equals("0000"))) {
			 //System.out.println("test1:");
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getBranchApplicationDetails(startDate,
					endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);

			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				 //System.out.println("test2:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetails",
						"Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			 //System.out.println("test3:");
			String memberId = bankId + zoneId;
			mliReport = reportManager.getZoneApplicationDetails(startDate,
					endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test4:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetails",
						"Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("test5:");
			String memberId = bankId;
			mliReport = reportManager.getBankApplicationDetails(startDate,
					endDate, id, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test6:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetails",
						"Exited");
				return mapping.findForward("success3");
			}
		} else {
			 //System.out.println("test7:");
			mliReport = reportManager.getMliApplicationDetails(startDate,
					endDate, id);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// System.out.println("test8:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward mliWiseClaimReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReportDetails",
				"Entered");
		// System.out.println("mliWiseClaimReportDetails entered");

		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			// System.out.println("test1:");
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getBranchClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);

			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test2:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReportDetails",
						"Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			// System.out.println("test3:");
			String memberId = bankId + zoneId;
			mliReport = reportManager.getZoneClaimApplicationDetails(startDate,
					endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test4:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReportDetails",
						"Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("test5:");
			String memberId = bankId;
			mliReport = reportManager.getBankClaimApplicationDetails(startDate,
					endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test6:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReportDetails",
						"Exited");
				return mapping.findForward("success3");
			}
		} else {
			// System.out.println("test7:");
			mliReport = reportManager.getMliClaimApplicationDetails(startDate,
					endDate);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// System.out.println("test8:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseClaimReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward stateWiseClaimReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "stateWiseClaimReportDetails",
				"Entered");
		// System.out.println("stateWiseClaimReportDetails entered");

		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			// System.out.println("test1:");
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getStateBranchClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);

			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test2:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"stateWiseClaimReportDetails", "Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			// System.out.println("test3:");
			String memberId = bankId + zoneId;
			mliReport = reportManager.getStateZoneClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test4:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"stateWiseClaimReportDetails", "Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("test5:");
			String memberId = bankId;
			mliReport = reportManager.getStateBankClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test6:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"stateWiseClaimReportDetails", "Exited");
				return mapping.findForward("success3");
			}
		} else {
			// System.out.println("test7:");
			mliReport = reportManager.getStateClaimApplicationDetails(
					startDate, endDate);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// System.out.println("test8:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"stateWiseClaimReportDetails", "Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward sectorWiseClaimReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "sectorWiseClaimReportDetails",
				"Entered");
		// System.out.println("sectorWiseClaimReportDetails entered");

		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			// System.out.println("test1:");
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getSectorBranchClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);

			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test2:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseClaimReportDetails", "Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			// System.out.println("test3:");
			String memberId = bankId + zoneId;
			mliReport = reportManager.getSectorZoneClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test4:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseClaimReportDetails", "Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("test5:");
			String memberId = bankId;
			mliReport = reportManager.getSectorBankClaimApplicationDetails(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test6:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseClaimReportDetails", "Exited");
				return mapping.findForward("success3");
			}
		} else {
			// System.out.println("test7:");
			mliReport = reportManager.getSectorClaimApplicationDetails(
					startDate, endDate);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// System.out.println("test8:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseClaimReportDetails", "Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward mliWiseClaimApplReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseClaimApplReportDetails",
				"Entered");
		// System.out.println("mliWiseClaimApplReportDetails entered");

		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			// System.out.println("test1:");
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.getBranchClaimApplicationDetailsNew(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);

			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test2:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"mliWiseClaimApplReportDetails", "Exited");
				return mapping.findForward("success1");
			}
		}

		else if (!(zoneId.equals("0000"))) {
			// System.out.println("test3:");
			String memberId = bankId + zoneId;
			mliReport = reportManager.getZoneClaimApplicationDetailsNew(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test4:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"mliWiseClaimApplReportDetails", "Exited");
				return mapping.findForward("success2");
			}
		} else if (!(bankId.equals("0000"))) {
			// System.out.println("test5:");
			String memberId = bankId;
			mliReport = reportManager.getBankClaimApplicationDetailsNew(
					startDate, endDate, memberId);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				// System.out.println("test6:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"mliWiseClaimApplReportDetails", "Exited");
				return mapping.findForward("success3");
			}
		} else {
			// System.out.println("test7:");
			mliReport = reportManager.getMliClaimApplicationDetailsNew(
					startDate, endDate);
			dynaForm.set("mliWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// System.out.println("test8:");
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"mliWiseClaimApplReportDetails", "Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward mliWiseReportDetailsForRsf(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetailsForRsf",
				"Entered");

		ArrayList mliReportForRsf = new ArrayList();
		ArrayList mliReportForRsfPend = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);
		String id = (String) dynaForm.get("checkValue");
		// System.out.println("id:"+id);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		/*
		 * following attributes are set to display the radio button value in
		 * reporting JSP
		 */
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		// System.out.println("test7:");
		mliReportForRsf = reportManager.getMliApplicationDetailsForRsf(
				startDate, endDate, id);
		dynaForm.set("mliReportForRsf", mliReportForRsf);
		mliReportForRsfPend = reportManager.getMliApplicationDetailsForRsfPend(
				startDate, endDate, id);
		dynaForm.set("mliReportForRsfPend", mliReportForRsfPend);
		if (mliReportForRsf == null || mliReportForRsf.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliReportForRsf = null;
			Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetailsForRsf",
					"Exited");
			return mapping.findForward("success");
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

	public ActionForward mliWiseReportDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetailsNew", "Entered");

		ArrayList mliWiseReportNew = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);
		String id = (String) dynaForm.get("checkValue");
		// System.out.println("id:"+id);
		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		/*
		 * following attributes are set to display the radio button value in
		 * reporting JSP
		 */
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		/*
		 * if(!(branchId.equals("0000"))) { System.out.println("test1:"); String
		 * memberId = bankId+zoneId+branchId; mliReport =
		 * reportManager.getBranchApplicationDetails
		 * (startDate,endDate,id,memberId);
		 * dynaForm.set("mliWiseReport",mliReport);
		 * 
		 * if( mliReport == null || mliReport.size() == 0 ) throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); else {
		 * System.out.println("test2:"); mliReport = null;
		 * Log.log(Log.INFO,"ReportsAction","mliWiseReportDetailsNew","Exited");
		 * return mapping.findForward("success1"); } }
		 * 
		 * else if(!(zoneId.equals("0000"))) { System.out.println("test3:");
		 * String memberId = bankId+zoneId; mliReport =
		 * reportManager.getZoneApplicationDetails
		 * (startDate,endDate,id,memberId);
		 * dynaForm.set("mliWiseReport",mliReport); if(mliReport == null ||
		 * mliReport.size()==0) throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); else {
		 * System.out.println("test4:"); mliReport = null;
		 * Log.log(Log.INFO,"ReportsAction","mliWiseReportDetailsNew","Exited");
		 * return mapping.findForward("success2"); } } else
		 * if(!(bankId.equals("0000"))) { System.out.println("test5:"); String
		 * memberId = bankId; mliReport =
		 * reportManager.getBankApplicationDetails
		 * (startDate,endDate,id,memberId);
		 * dynaForm.set("mliWiseReport",mliReport); if(mliReport == null ||
		 * mliReport.size()==0) throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); else {
		 * System.out.println("test6:"); mliReport = null;
		 * Log.log(Log.INFO,"ReportsAction","mliWiseReportDetailsNew","Exited");
		 * return mapping.findForward("success3"); } }
		 */
		// else
		// {
		// System.out.println("test7:");
		if (startDate != null) {
			mliWiseReportNew = reportManager.getMliApplicationDetails(
					startDate, endDate, id);
			dynaForm.set("mliWiseReportNew", mliWiseReportNew);
		} else {
			mliWiseReportNew = reportManager.getMliApplicationDetails(
					startDate, endDate, id);
			dynaForm.set("mliWiseReportNew", mliWiseReportNew);
			return mapping.findForward("success1");
		}

		if (mliWiseReportNew == null || mliWiseReportNew.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseReportNew = null;
			Log.log(Log.INFO, "ReportsAction", "mliWiseReportDetailsNew",
					"Exited");
			return mapping.findForward("success");
		}
		// }
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
	public ActionForward mliWiseNEReportDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseNEReportDetailsNew",
				"Entered");

		ArrayList mliWiseNEReportNew = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();
		String mliId = bankId + zoneId + branchId;
		// System.out.println("mliId:"+mliId);
		dynaForm.set("memberId", mliId);
		dynaForm.set("bankId", bankId);
		mliWiseNEReportNew = reportManager.getmliWiseNEReportDetailsNew(
				startDate, endDate);
		dynaForm.set("mliWiseNEReportNew", mliWiseNEReportNew);
		if (mliWiseNEReportNew == null || mliWiseNEReportNew.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseNEReportNew = null;
			Log.log(Log.INFO, "ReportsAction", "mliWiseNEReportDetailsNew",
					"Exited");
			return mapping.findForward("success");
		}
		// }
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
	public ActionForward mliWiseNEStateReportDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "mliWiseNEStateReportDetailsNew",
				"Entered");

		ArrayList mliWiseNEStateReportNew = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		// System.out.println("startDate:"+startDate);
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("eDate:"+eDate);

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();
		String mliId = bankId + zoneId + branchId;
		// System.out.println("mliId:"+mliId);
		dynaForm.set("memberId", mliId);
		dynaForm.set("bankId", bankId);
		mliWiseNEStateReportNew = reportManager
				.getmliWiseNEStateReportDetailsNew(startDate, endDate);
		dynaForm.set("mliWiseNEStateReportNew", mliWiseNEStateReportNew);
		if (mliWiseNEStateReportNew == null
				|| mliWiseNEStateReportNew.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseNEStateReportNew = null;
			Log.log(Log.INFO, "ReportsAction",
					"mliWiseNEStateReportDetailsNew", "Exited");
			return mapping.findForward("success");
		}
		// }
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

	public ActionForward mliWiseNEDistrictReportDetailsNew1(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseNEDistrictReportDetailsNew",
				"Entered");
		ArrayList mliWiseNEDistrictReportDetailsNew = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String district = request.getParameter("District");

		// System.out.println("District:"+district);
		// System.out.println("Start Date:"+startDate);
		// System.out.println("End Date:"+endDate);

		// request.setAttribute("stateName",district);

		mliWiseNEDistrictReportDetailsNew = reportManager
				.mliWiseNEDistrictReportDetailsNew1(startDate, endDate,
						district);

		dynaForm.set("mliWiseNEDistrictReportDetailsNew",
				mliWiseNEDistrictReportDetailsNew);

		if (mliWiseNEDistrictReportDetailsNew == null
				|| mliWiseNEDistrictReportDetailsNew.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// System.out.println("test8:");
			mliWiseNEDistrictReportDetailsNew = null;
			Log.log(Log.INFO, "ReportsAction",
					"mliWiseNEDistrictReportDetailsNew1", "Exited");
			return mapping.findForward("success1");
		}

	}

	public ActionForward zoneDetailsForMli(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "zoneDetailsForMli", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		// String zone = request.getParameter("Zone");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.ZoneDetailsForBranch(startDate, endDate,
					id, memberId);
			dynaForm.set("zoneWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "zoneDetailsForMli",
						"Exited");
				return mapping.findForward("success1");
			}

		} else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			mliReport = reportManager.ZoneDetailsForZone(startDate, endDate,
					id, memberId);
			dynaForm.set("zoneWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "zoneDetailsForMli",
						"Exited");
				return mapping.findForward("success2");
			}
		} else // if(!(bankId.equals("0000")))
		{
			String memberId = bankId;
			mliReport = reportManager.ZoneDetailsForBank(startDate, endDate,
					id, memberId);
			dynaForm.set("zoneWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "zoneDetailsForMli",
						"Exited");
				return mapping.findForward("success3");
			}
		}
	}

	public ActionForward sectorDetailsForMli(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorDetailsForMli", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.sectorDetailsForBranch(startDate,
					endDate, id, memberId);
			dynaForm.set("sectorWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "sectorDetailsForMli",
						"Exited");
				return mapping.findForward("success1");
			}

		} else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			mliReport = reportManager.sectorDetailsForZone(startDate, endDate,
					id, memberId);
			dynaForm.set("sectorWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "sectorDetailsForMli",
						"Exited");
				return mapping.findForward("success2");
			}
		} else // if(!(bankId.equals("0000")))
		{
			String memberId = bankId;
			mliReport = reportManager.sectorDetailsForBank(startDate, endDate,
					id, memberId);
			dynaForm.set("sectorWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "sectorDetailsForMli",
						"Exited");
				return mapping.findForward("success3");
			}
		}
	}

	public ActionForward stateDetailsForMli(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateDetailsForMli", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		if (!(branchId.equals("0000"))) {
			String memberId = bankId + zoneId + branchId;
			mliReport = reportManager.stateDetailsForBranch(startDate, endDate,
					id, memberId);
			dynaForm.set("stateWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForMli",
						"Exited");
				return mapping.findForward("success1");
			}

		} else if (!(zoneId.equals("0000"))) {
			String memberId = bankId + zoneId;
			mliReport = reportManager.stateDetailsForZone(startDate, endDate,
					id, memberId);
			dynaForm.set("stateWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForMli",
						"Exited");
				return mapping.findForward("success2");
			}
		} else // if(!(bankId.equals("0000")))
		{
			String memberId = bankId;
			mliReport = reportManager.stateDetailsForBank(startDate, endDate,
					id, memberId);
			dynaForm.set("stateWiseReport", mliReport);
			if (mliReport == null || mliReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				mliReport = null;
				Log.log(Log.INFO, "ReportsAction", "stateDetailsForMli",
						"Exited");
				return mapping.findForward("success3");
			}
		}
	}

	public ActionForward districtDetailsForMli(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli", "Entered");
		ArrayList mliReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		BaseAction baseAction = new BaseAction();
		MLIInfo mliInfo = baseAction.getMemberInfo(request);
		String bankId = mliInfo.getBankId();
		String zoneId = mliInfo.getZoneId();
		String branchId = mliInfo.getBranchId();

		String state = (String) dynaForm.get("state");
		// System.out.println("state:"+state);
		int i = state.indexOf("$");
		if (i != -1) {
			String newState = state.replace('$', '&');
			if (!(branchId.equals("0000"))) {
				String memberId = bankId + zoneId + branchId;
				mliReport = reportManager.districtDetailsForBranch(startDate,
						endDate, id, memberId, newState);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success1");
				}

			} else if (!(zoneId.equals("0000"))) {
				String memberId = bankId + zoneId;
				mliReport = reportManager.districtDetailsForZone(startDate,
						endDate, id, memberId, newState);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success2");
				}
			} else // if(!(bankId.equals("0000")))
			{
				String memberId = bankId;
				mliReport = reportManager.districtDetailsForBank(startDate,
						endDate, id, memberId, newState);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success3");
				}
			}
		}

		else {
			if (!(branchId.equals("0000"))) {
				String memberId = bankId + zoneId + branchId;
				mliReport = reportManager.districtDetailsForBranch(startDate,
						endDate, id, memberId, state);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success1");
				}

			} else if (!(zoneId.equals("0000"))) {
				String memberId = bankId + zoneId;
				mliReport = reportManager.districtDetailsForZone(startDate,
						endDate, id, memberId, state);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success2");
				}
			} else // if(!(bankId.equals("0000")))
			{
				String memberId = bankId;
				mliReport = reportManager.districtDetailsForBank(startDate,
						endDate, id, memberId, state);
				dynaForm.set("districtWiseReport", mliReport);
				if (mliReport == null || mliReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					mliReport = null;
					Log.log(Log.INFO, "ReportsAction", "districtDetailsForMli",
							"Exited");
					return mapping.findForward("success3");
				}
			}
		}
	}

	public ActionForward zoneWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetails", "Entered");
		ArrayList zoneReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String zone = request.getParameter("Zone");
		System.out.println("zone:"+zone);
		int i = zone.indexOf("$");
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", zone);

		if (i != -1) {
			String newZone = zone.replace('$', '&');
			zoneReport = reportManager.getZoneDetails(newZone, id, startDate,
					endDate);
			dynaForm.set("zoneReport", zoneReport);

			if (zoneReport == null || zoneReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			zoneReport = reportManager.getZoneDetails(zone, id, startDate,
					endDate);
			dynaForm.set("zoneReport", zoneReport);
			if (zoneReport == null || zoneReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// zoneReport = null;
				Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward mliWiseRSFReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "mliWiseRSFReportDetails", "Entered");
		ArrayList mliWiseRSFReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String zone = request.getParameter("Zone");
		// System.out.println("zone:"+zone);
		// System.out.println("id:"+id);
		// System.out.println("STARTDATE:"+startDate);
		// System.out.println("ENDDATE"+endDate);
		int i = zone.indexOf("$");
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", zone);

		if (i != -1) {
			String newZone = zone.replace('$', '&');
			mliWiseRSFReport = reportManager.getRSFDetails(newZone, id,
					startDate, endDate);
			dynaForm.set("mliWiseRSFReport", mliWiseRSFReport);

			if (mliWiseRSFReport == null || mliWiseRSFReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				Log.log(Log.INFO, "ReportsAction", "mliWiseRSFReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			mliWiseRSFReport = reportManager.getRSFDetails(zone, id, startDate,
					endDate);
			dynaForm.set("mliWiseRSFReport", mliWiseRSFReport);
			if (mliWiseRSFReport == null || mliWiseRSFReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// zoneReport = null;
				Log.log(Log.INFO, "ReportsAction", "mliWiseRSFReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward gfAllocatedReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "gfAllocatedReportDetails",
				"Entered");
		ArrayList gfallocatedpaymentdetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String payId = request.getParameter("payId");
		// System.out.println("payId:"+payId);

		gfallocatedpaymentdetails = reportManager
				.getGFAllocatedReportDetails(payId);
		dynaForm.set("gfallocatedpaymentdetails", gfallocatedpaymentdetails);
		if (gfallocatedpaymentdetails == null
				|| gfallocatedpaymentdetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {

			Log.log(Log.INFO, "ReportsAction", "gfAllocatedReportDetails",
					"Exited");
			return mapping.findForward("success");
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
	public ActionForward payInstrumentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "payInstrumentDetails", "Entered");
		ArrayList gfallocatedpaymentdetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String payInstrumentNo = request.getParameter("payInstrumentNo");
		// System.out.println("payInstrumentNo:"+payInstrumentNo);
		String memberId = request.getParameter("memberId");
		// System.out.println("memberId:"+memberId);
		gfallocatedpaymentdetails = reportManager.getPayInstrumentDetails(
				payInstrumentNo, memberId);
		dynaForm.set("gfallocatedpaymentdetails", gfallocatedpaymentdetails);
		if (gfallocatedpaymentdetails == null
				|| gfallocatedpaymentdetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {

			Log.log(Log.INFO, "ReportsAction", "payInstrumentDetails", "Exited");
			return mapping.findForward("success");
		}

	}

	
	public ActionForward payInstrumentDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		System.out.println("Entered======");
		Log.log(Log.INFO, "ReportsAction", "payInstrumentDetails", "Entered");
		ArrayList gfallocatedpaymentdetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String payInstrumentNo = request.getParameter("payInstrumentNo");
		// System.out.println("payInstrumentNo:"+payInstrumentNo);
		String memberId = request.getParameter("memberId");
		// System.out.println("memberId:"+memberId);
		gfallocatedpaymentdetails = reportManager.getPayInstrumentDetailsNew(
				payInstrumentNo, memberId);
		dynaForm.set("gfallocatedpaymentdetails", gfallocatedpaymentdetails);
		if (gfallocatedpaymentdetails == null
				|| gfallocatedpaymentdetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {

			Log.log(Log.INFO, "ReportsAction", "payInstrumentDetails", "Exited");
			return mapping.findForward("success");
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
	public ActionForward asfAllocatedReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "asfAllocatedReportDetails",
				"Entered");
		ArrayList asfallocatedpaymentdetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String payId = request.getParameter("payId");
		// System.out.println("payId:"+payId);

		asfallocatedpaymentdetails = reportManager
				.getASFAllocatedReportDetails(payId);
		dynaForm.set("asfallocatedpaymentdetails", asfallocatedpaymentdetails);
		if (asfallocatedpaymentdetails == null
				|| asfallocatedpaymentdetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {

			Log.log(Log.INFO, "ReportsAction", "asfAllocatedReportDetails",
					"Exited");
			return mapping.findForward("success");
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

	public ActionForward branchWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "branchWiseReportDetails", "Entered");
		ArrayList branchReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");

		// System.out.println("test-Id:"+id);
		// System.out.println("stDate:"+stDate);
		// System.out.println("eDate:"+eDate);
		String bank = request.getParameter("bank");
		String zone = request.getParameter("Zone");
		// System.out.println("TEST-bank:"+bank);
		// System.out.println("TEST-zone:"+zone);
		// int i = zone.indexOf("$");
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("zoneName", zone);

		/*
		 * if(i != -1) { String newZone = zone.replace('$','&'); branchReport =
		 * reportManager.getZoneDetails(newZone,id,startDate, endDate);
		 * dynaForm.set("branchReport",branchReport);
		 * 
		 * if(branchReport == null || branchReport.size()==0) throw new
		 * NoDataException("No Data is available for the values entered," +
		 * " Please Enter Any Other Value "); else {
		 * Log.log(Log.INFO,"ReportsAction","branchWiseReportDetails","Exited");
		 * return mapping.findForward("success"); } }
		 */

		// else
		// {
		branchReport = reportManager.getbranchDetails(bank, zone, id,
				startDate, endDate);
		dynaForm.set("branchReport", branchReport);
		if (branchReport == null || branchReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// zoneReport = null;
			Log.log(Log.INFO, "ReportsAction", "branchWiseReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
		// }
	}

	// added by raju konkati
	/*
	public ActionForward misCummulativeReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danReportInput", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument20(prevDate);
		generalReport.setDateOfTheDocument21(date);
		generalReport.setDateOfTheDocument22(prevDate);
		generalReport.setDateOfTheDocument23(date);
		generalReport.setDateOfTheDocument24(prevDate);
		generalReport.setDateOfTheDocument25(date);
		
		// Fix 07092004 - 19
		//dynaForm.set("memberId", "");
	//	dynaForm.set("ssi", "");
		// Fix Completed
		BeanUtils.copyProperties(dynaForm, generalReport);
		// ApplicationReport applicationReport = new ApplicationReport();
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		Log.log(Log.INFO, "ReportsAction", "danReportInput", "Exited");
		return mapping.findForward("success");
	}

	
	public ActionForward misCummulativeReportDetail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "danReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument20");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
			// System.out.println("startDate:"+startDate);

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
			// System.out.println("startDate:"+startDate);

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument21");
		endDate = new java.sql.Date(eDate.getTime());
	
		dan = reportManager.getMisReport(startDate, endDate);
		dynaForm.set("dan", dan);
	
		

		return mapping.findForward("success");
	}*/

	public ActionForward womenEntrepreneurReports(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "NewReportsAction", "asfSummeryReport", "Entered");
		HttpSession session = request.getSession();
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument34(prevDate);
		generalReport.setDateOfTheDocument35(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("memberId", "");
		Statement womenEntrepreneurStmt = null;
		String StateStringArray[] = null;
		ArrayList womenEntrepreneurReportArray = new ArrayList();
		ArrayList womenEntrepreneurReportArray1 = new ArrayList();
		ArrayList womenEntrepreneurReportArray2 = new ArrayList();
		Connection connection = null;
		try {
			connection = DBConnection.getConnection();
			String query2 = "select STE_CODE,STE_NAME from State_master order by STE_NAME";
			Statement womenEntrepreneurStmt2 = connection.createStatement();
			ResultSet womenEntrepreneurReportResult2;
			for (womenEntrepreneurReportResult2 = womenEntrepreneurStmt2
					.executeQuery(query2); womenEntrepreneurReportResult2
					.next(); womenEntrepreneurReportArray2
					.add(StateStringArray)) {
				StateStringArray = new String[3];
				StateStringArray[0] = womenEntrepreneurReportResult2
						.getString(1);
				StateStringArray[1] = womenEntrepreneurReportResult2
						.getString(2);
			}

			womenEntrepreneurReportResult2.close();
			womenEntrepreneurReportResult2 = null;
			womenEntrepreneurStmt2.close();
			womenEntrepreneurStmt2 = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("district_arraylist_data", null);
		request.setAttribute("district_arraylist_data_size", null);
		session.setAttribute("womenEntrepreneurReportArray2",
				womenEntrepreneurReportArray2);
		Log.log(4, "NewReportsAction", "womenEntrepreneurReports", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward womenEntrepreneurReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Entered");
		Connection connection = DBConnection.getConnection();
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument34");
		Date todate = (Date) dynaForm.get("dateOfTheDocument35");
		String memberID = (String) dynaForm.get("memberId");
		String District = (String) dynaForm.get("District");
		String state = (String) dynaForm.get("State");
		String WomenStringArray[] = null;
		ArrayList womenEntrepreneurDetailsArray = new ArrayList();
		String id = "";
		if (memberID.equals(null) || memberID.equals("")) {
			query = (new StringBuilder())
					.append(" select round(sum(decode(PMR_chief_gender,'F', nvl(APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT),null)/100000),2) Female,decode(PMR_chief_gender,'F', count(A.CGPAN)) F_count  from application_detail A, PROMOTER_DETAIL B, ssi_detail c ,member_info m WHERE a.SSI_REFERENCE_NUMBER = B.SSI_REFERENCE_NUMBER and a.app_status not in ('RE')  AND m.MEM_BNK_ID||m.MEM_ZNE_ID||m.MEM_BRN_ID=A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID and  B.SSI_REFERENCE_NUMBER = c.SSI_REFERENCE_NUMBER  AND trunc(APP_APPROVED_DATE_TIME) between to_date('")
					.append(fromdate).append("','dd/mm/yyyy')")
					.append("  AND to_date('").append(todate)
					.append("','dd/mm/yyyy')").toString();
			//System.out.println("query1--" + query);
			if (!state.equals(""))
				query = (new StringBuilder())
						.append(query)
						.append("and a.app_status not in ('RE') and c.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
						.append(state.trim()).append("')").toString();
			//System.out.println("query2--" + query);
			if (!District.equals(""))
				query = (new StringBuilder())
						.append(query)
						.append("and a.app_status not in ('RE') and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
						.append(District.trim()).append("')").toString();
			//System.out.println("query3--" + query);
			query = (new StringBuilder()).append(query)
					.append("group by PMR_chief_gender").toString();
			//System.out.println("query4--" + query);
		} else if (!memberID.equals(null)) {
			String bankId = memberID.substring(0, 4);
			String zoneId = memberID.substring(4, 8);
			String branchId = memberID.substring(8, 12);
			if (!branchId.equals("0000")) {
				id = (new StringBuilder()).append(bankId).append(zoneId)
						.append(branchId).toString();
				query = (new StringBuilder())
						.append(" select round(sum(decode(PMR_chief_gender,'F', nvl( APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT),null)/100000),2) Female,decode(PMR_chief_gender,'F', count(A.CGPAN)) F_count  from application_detail A, PROMOTER_DETAIL B, ssi_detail c  WHERE a.SSI_REFERENCE_NUMBER = B.SSI_REFERENCE_NUMBER and a.app_status not in ('RE')  and  B.SSI_REFERENCE_NUMBER = c.SSI_REFERENCE_NUMBER and a.MEM_BNK_ID||a.MEM_ZNE_ID||a.MEM_BRN_ID=nvl('")
						.append(id)
						.append("',a.MEM_BNK_ID||a.MEM_ZNE_ID||a.MEM_BRN_ID)")
						.append(" and APP_APPROVED_AMOUNT is not null AND trunc(APP_APPROVED_DATE_TIME) between to_date('")
						.append(fromdate).append("','dd/mm/yyyy')")
						.append("  AND to_date('").append(todate)
						.append("','dd/mm/yyyy')").toString();
				 //System.out.println("query1=="+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and a.app_status not in ('RE') and c.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				//System.out.println("query2=="+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and a.app_status not in ('RE') and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				 //System.out.println("query3=="+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				 //System.out.println("query4=="+query);
			} else if (!zoneId.equals("0000")) {
				id = (new StringBuilder()).append(bankId).append(zoneId)
						.toString();
				query = (new StringBuilder())
						.append(" select round(sum(decode(PMR_chief_gender,'F', nvl(APP_APPROVED_AMOUNT,0),null)/100000),2) Female,decode(PMR_chief_gender,'F', count(*)) F_count  from application_detail A, PROMOTER_DETAIL B, ssi_detail c  WHERE a.SSI_REFERENCE_NUMBER = B.SSI_REFERENCE_NUMBER and  B.SSI_REFERENCE_NUMBER = c.SSI_REFERENCE_NUMBER and a.MEM_BNK_ID||a.MEM_ZNE_ID=nvl('")
						.append(id)
						.append("',a.MEM_BNK_ID||a.MEM_ZNE_ID)")
						.append(" and APP_APPROVED_AMOUNT is not null AND trunc(APP_APPROVED_DATE_TIME) between to_date('")
						.append(fromdate).append("','dd/mm/yyyy')")
						.append("  AND to_date('").append(todate)
						.append("','dd/mm/yyyy')").toString();
				// System.out.println("query5=="+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				// System.out.println("query6"+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				// System.out.println("query7"+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				// System.out.println("query8"+query);
			} else {
				id = bankId;
				query = (new StringBuilder())
						.append(" select round(sum(decode(PMR_chief_gender,'F', nvl(APP_APPROVED_AMOUNT,0),null)/100000),2) Female,decode(PMR_chief_gender,'F', count(*)) F_count  from application_detail A, PROMOTER_DETAIL B, ssi_detail c  WHERE a.SSI_REFERENCE_NUMBER = B.SSI_REFERENCE_NUMBER and  B.SSI_REFERENCE_NUMBER = c.SSI_REFERENCE_NUMBER and a.MEM_BNK_ID=nvl('")
						.append(id)
						.append("',a.MEM_BNK_ID)")
						.append(" and APP_APPROVED_AMOUNT is not null AND trunc(APP_APPROVED_DATE_TIME) between to_date('")
						.append(fromdate).append("','dd/mm/yyyy')")
						.append("  AND to_date('").append(todate)
						.append("','dd/mm/yyyy')").toString();
				// System.out.println("query9"+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				// System.out.println("query10"+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				// System.out.println("query11"+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				// System.out.println("query12"+query);
			}
		}
		try {
			Statement womenEntrepreneurDetailsStmt = null;
			ResultSet womenEntrepreneurDetailsResult = null;
			womenEntrepreneurDetailsStmt = connection.createStatement();
			/*for (womenEntrepreneurDetailsResult = womenEntrepreneurDetailsStmt
					.executeQuery(query); womenEntrepreneurDetailsResult.next(); womenEntrepreneurDetailsArray
					.add(WomenStringArray)) {
				WomenStringArray = new String[2];
				WomenStringArray[0] = womenEntrepreneurDetailsResult
						.getString(1);
				WomenStringArray[1] = womenEntrepreneurDetailsResult
						.getString(2);
			}*/

			/*Edit By VinodSingh on 05-NOV-2020 START*/
			womenEntrepreneurDetailsResult = womenEntrepreneurDetailsStmt.executeQuery(query);
			while(womenEntrepreneurDetailsResult.next()) {
				WomenStringArray = new String[2];
				if(womenEntrepreneurDetailsResult.getString(1)!=null && womenEntrepreneurDetailsResult.getString(2)!=null){
					WomenStringArray[0] = womenEntrepreneurDetailsResult.getString(1);
					WomenStringArray[1] = womenEntrepreneurDetailsResult.getString(2);
					womenEntrepreneurDetailsArray.add(WomenStringArray);
				}	
			}
			/*Edit By VinodSingh on 05-NOV-2020 END*/
			
			womenEntrepreneurDetailsResult.close();
			womenEntrepreneurDetailsResult = null;
			womenEntrepreneurDetailsStmt.close();
			womenEntrepreneurDetailsStmt = null; 
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		int total1 = womenEntrepreneurDetailsArray.size();
		String total = (new Integer(total1)).toString();
		if (total == "1" || total == "0") {
			throw new NoDataException(
					"No Data is available for the values entered, Please Enter Any Other Value ");
		} else {
			request.setAttribute("womenEntrepreneurDetailsArray",
					womenEntrepreneurDetailsArray);
			request.setAttribute("womenEntrepreneurDetailsArray_size",
					(new Integer(womenEntrepreneurDetailsArray.size()))
							.toString());
			Log.log(4, "NewReportsAction", "womenEntrepreneurReportDetails",
					"Exited");
			return mapping.findForward("success1");
		}
	}

	public ActionForward sectorWiseReportDetails1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetails1",
				"Entered");
		ArrayList sectorReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String sector = request.getParameter("Sector");

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", sector);

		int i = sector.indexOf("$");
		if (i != -1) {
			String newSector = sector.replace('$', '&');
			sectorReport = reportManager.getSectorDetails(newSector, id,
					startDate, endDate);
			dynaForm.set("sectorReport", sectorReport);
			if (sectorReport == null || sectorReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// sectorReport = null;
				Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetails1",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			sectorReport = reportManager.getSectorDetails(sector, id,
					startDate, endDate);
			dynaForm.set("sectorReport", sectorReport);
			if (sectorReport == null || sectorReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// sectorReport = null;
				Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetails1",
						"Exited");
				return mapping.findForward("success");
			}
		}

	}

	public ActionForward statesWiseReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statesWiseReport", "Entered");
		ArrayList statesWiseReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String state = request.getParameter("state");

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", state);

		int i = state.indexOf("$");
		if (i != -1) {
			String newState = state.replace('$', '&');
			statesWiseReport = reportManager.getStatesWiseReport(newState, id,
					startDate, endDate);
			dynaForm.set("statesWiseReport", statesWiseReport);
			if (statesWiseReport == null || statesWiseReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// statesWiseReport = null;
				Log.log(Log.INFO, "ReportsAction", "statesWiseReport", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			statesWiseReport = reportManager.getStatesWiseReport(state, id,
					startDate, endDate);
			dynaForm.set("statesWiseReport", statesWiseReport);
			if (statesWiseReport == null || statesWiseReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// statesWiseReport = null;
				Log.log(Log.INFO, "ReportsAction", "statesWiseReport", "Exited");
				return mapping.findForward("success");
			}
		}
	}

	/**
	 * 
	 * added by sukumar@path for providing State wise - Current Year,Previous
	 * Year and Cumulative Year data
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.lang.Exception
	 */
	public ActionForward statesWiseReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "statesWiseReportNew", "Entered");
		ArrayList statesWiseReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String state = request.getParameter("state");

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", state);
		// System.out.println("Bank Name:"+state);
		int i = state.indexOf("$");
		if (i != -1) {
			String newState = state.replace('$', '&');
			statesWiseReport = reportManager.getStatesWiseReportNew(newState,
					id, startDate, endDate);
			dynaForm.set("statesWiseReport", statesWiseReport);
			if (statesWiseReport == null || statesWiseReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// statesWiseReport = null;
				Log.log(Log.INFO, "ReportsAction", "statesWiseReportNew",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			statesWiseReport = reportManager.getStatesWiseReportNew(state, id,
					startDate, endDate);
			dynaForm.set("statesWiseReport", statesWiseReport);
			if (statesWiseReport == null || statesWiseReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// statesWiseReport = null;
				Log.log(Log.INFO, "ReportsAction", "statesWiseReportNew",
						"Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward sectorWiseReportDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetailsNew",
				"Entered");
		ArrayList sectorReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String sector = request.getParameter("Sector");

		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", sector);

		int i = sector.indexOf("$");
		if (i != -1) {
			String newSector = sector.replace('$', '&');
			sectorReport = reportManager.getSectorDetailsNew(newSector, id,
					startDate, endDate);
			dynaForm.set("sectorReport", sectorReport);
			if (sectorReport == null || sectorReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// sectorReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseReportDetailsNew", "Exited");
				return mapping.findForward("success");
			}
		}

		else {
			sectorReport = reportManager.getSectorDetailsNew(sector, id,
					startDate, endDate);
			dynaForm.set("sectorReport", sectorReport);
			if (sectorReport == null || sectorReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// sectorReport = null;
				Log.log(Log.INFO, "ReportsAction",
						"sectorWiseReportDetailsNew", "Exited");
				return mapping.findForward("success");
			}
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
	public ActionForward zoneWiseReportDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetailsNew",
				"Entered");
		ArrayList zoneReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		String zone = request.getParameter("Zone");
		// System.out.println("zone:"+zone);
		int i = zone.indexOf("$");
		request.setAttribute("STARTDATE", dynaForm.get("dateOfTheDocument12"));
		request.setAttribute("ENDDATE", dynaForm.get("dateOfTheDocument13"));
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("bankName", zone);

		if (i != -1) {
			String newZone = zone.replace('$', '&');
			zoneReport = reportManager.getZoneDetailsNew(newZone, id,
					startDate, endDate);
			dynaForm.set("zoneReport", zoneReport);

			if (zoneReport == null || zoneReport.size() == 0)
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			else {
				Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetails",
						"Exited");
				return mapping.findForward("success");
			}
		}

		else {
			zoneReport = reportManager.getZoneDetailsNew(zone, id, startDate,
					endDate);
			dynaForm.set("zoneReport", zoneReport);
			if (zoneReport == null || zoneReport.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				// zoneReport = null;
				Log.log(Log.INFO, "ReportsAction", "zoneWiseReportDetailsNew",
						"Exited");
				return mapping.findForward("success");
			}
		}
	}

	public ActionForward stateApplicationDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "stateApplicationDetails", "Entered");
		ArrayList stateReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		// GeneralReport mli = new GeneralReport();
		String state = request.getParameter("State");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("checkValue");
		stateReport = reportManager.getStateDetails(state, id, startDate,
				endDate);
		dynaForm.set("stateReport", stateReport);
		if (stateReport == null || stateReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			// stateReport = null;
			Log.log(Log.INFO, "ReportsAction", "stateApplicationDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}
	
	
	public ActionForward showWCMliList(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showWCMliList", "Entered");
		ArrayList mli = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		mli = reportManager.getWcAppListMliWise();
		dynaForm.set("mli", mli);
		String forward = "";
		if (mli == null || mli.size() == 0) {
			Log.log(Log.INFO, "ReportsAction", "showWCMliList", "Exited");
			request.setAttribute("message","No Applications for Approval Available");
			forward = "success";
		} else {
			mli = null;
			Log.log(Log.INFO, "ReportsAction", "showWCMliList", "Exited");
			forward = "approvalWcMliList";
		}
		return mapping.findForward(forward);
	}
	
  public ActionForward approvalWcCgpanList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
      Log.log(Log.INFO, "ReportAction", "approvalWcCgpanList()", "Entered");
      RenewalOfWorkingCapitalAfterTenYearsActionForm claimForm = (RenewalOfWorkingCapitalAfterTenYearsActionForm)form;
      Administrator admin = new Administrator();
      String cgtsiBankId = "0000";
      String cgtsiZoneId = "0000";
      String cgtsiBrnId = "0000";
      ArrayList userIds = admin.getUsers((new StringBuilder()).append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId).toString());
      double maxClmApprvdAmnt = 0.0D;
     
      User user = getUserInformation(request);
      String designation = user.getDesignation();
      String bankName = request.getParameter("Link");
      bankName = bankName.replaceAll("PATH", "&");
      String loggedUsr = user.getUserId();
      System.out.println("<<approvalWcCgpanList>>"+loggedUsr);
     // System.out.println("<<bankName>>"+bankName);
      Log.log(Log.INFO, "ReportAction", "approvalWcCgpanList()", (new StringBuilder()).append("designation :").append(designation).toString());
      //Log.log(Log.INFO, "ClaimAction", "approvalWcCgpanList()", (new StringBuilder()).append("maxClmApprvdAmnt :").append(maxClmApprvdAmnt).toString());
   
      Vector firstinstllmntclaims = reportManager.getWcApprovalDetails(loggedUsr, bankName);
      if(firstinstllmntclaims.size() == 0)
      {
          request.setAttribute("message", "No application(s) for approval.");
          return mapping.findForward("success");
      } else
      {
          //claimForm.setLimit(outOfLimit);
          claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
          claimForm.setFirstCounter(firstinstllmntclaims.size());
          claimForm.setClmRefDtlSet("N");
          firstinstllmntclaims = null;
          Log.log(Log.INFO, "ReportAction", "ReportAction()-->approvalWcCgpanList()", "Exited");
          return mapping.findForward("approvalWcCgpanList");
      }
  }
  
  public ActionForward getMliFormDetails(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception {
	  

		 RenewalOfWorkingCapitalAfterTenYearsActionForm RenewalOfWorkingCapitalAfterTenYearsActionFormObj1 = (RenewalOfWorkingCapitalAfterTenYearsActionForm) form;
		// ArrayList arrayListObj = new ArrayList();
		 String loginUserId="";
		 String memberId="";
		 ResultSet resultset1 = null;
		 CallableStatement callableStmt = null;
		 RenewalOfWorkingCapitalAfterTenYearsActionForm rwcatyafObj=null;
		// ClaimActionForm claimFormobj = (ClaimActionForm) form;
			Connection conn = null;
			String cgpan="";
			String unitName="";
			String unitPAN="";
			String lineOfActivity="";
			String classification="";
			String loanAccountNumber="";
			String promotersName="";
			String chiefPromoterPAN="";
			String address="";
			String guranteeStartDate="";
			String guranteeExpiryDate="";
			String lastRenewalDateOfWC="";
			String existing="0";
			String nonfundbasedexisting="0";
			String forwardPageName="";
			String errorMsg="";
			String failureOrSucessMsg="";
			String proposed="";
			String nonfundbasedproposed = "";
			String satcibilreport = "";
			String pddefaulter = "";
			String satposstatdues = "";
			String repaymenttracksat = "";
			String availofstockasset = "";
			String workingoffactory = "";
			String generalupkeep = "";
			String stockregister = "";
			String stockstatement = "";
			
			String groupAssociateNPA = "";
			String satisfactoryCreditReports = "";
			String currentRatio="";
			String tolOrTNW="";
			String drOrCrDays="";
			String interestCoverage="";
			String assetCoverageRatio="";
			String fixedAssetCoverageRatio="";
			
			String currrentRatioActualValue="";
			String tolOrTNWActualValue="";
			String drOrCrDaysActualValue="";
			String interestCoverageActualValue="";
			String assetCoverageRatioActualValue="";
			String fixedAssetCoverageRatioActualValue="";
			
			String psMovablesValue="";
			String psMovablesRemarks="";
			String psImmovalableValue="";
			String psImmovalableRemarks="";
			String clMovablesValue="";
			String clMovablesRemarks="";
			String clImmovalableValue="";
			String clImmovalableRemarks="";
			
			String salesPreviousYear="";
			String salesCurrentYear="";
			String salesProjectionNextYear="";

			String pbditPreviousYear="";
			String pbditCurrentYear="";
			String pbditProjectionNextYear="";

			String equityCapitalPreviousYear="";
			String equityCapitalCurrentYear="";
			String equityCapitalProjectionNextYear="";

			String totalScoreGradePYAPAAppraial="";
			String totalScoreGradeCYAPAAppraial="";

			String commentsOnScorePYAPAAppraial="";
			String commentsOnScoreCYAPAAppraial="";

			String workingCapitalLimitExisting="";
			String workingCapitalLimitProposed="";
			String workingCapitalLimitComments="";
			String workingCapitalLimitStatus="";
			
			String proposalinvestmentgrade = "";
			
			long approvedAmtInLong = 0l;
			
			System.out.println("CGPAN>>"+request.getParameterValues("cgPanValue"));
			System.out.println("mliLodgeDate>>"+request.getParameterValues("lodgedate"));
			
			System.out.println("In CGINTRA>>"+RenewalOfWorkingCapitalAfterTenYearsActionFormObj1.getCgpan()+"<<lodgeDate>>"+RenewalOfWorkingCapitalAfterTenYearsActionFormObj1.getLodgedate());
			String cgpann=RenewalOfWorkingCapitalAfterTenYearsActionFormObj1.getCgpan();
			try{
				//Connection conn = null;
				conn = DBConnection.getConnection();
				
				callableStmt = conn.prepareCall("{call GET_REVIEW_FORM_DTLS(?,?,?)}");
				callableStmt.setString(1, cgpann);
				//callableStmt.setString(1,"CG20120132402WC");
				callableStmt.registerOutParameter(2, Types.VARCHAR);
				callableStmt.registerOutParameter(3, OracleTypes.CURSOR);
				callableStmt.execute();
				failureOrSucessMsg = callableStmt.getString(2);
				
				System.out.println("failureOrSucessMsg>>"+failureOrSucessMsg);
				if(failureOrSucessMsg!=null){
					forwardPageName="failure";
					request.setAttribute("errorMsg", failureOrSucessMsg);
					request.setAttribute("cgpan", cgpan);
					request.setAttribute("unitName", unitName);
					request.setAttribute("unitPAN", unitPAN);
					request.setAttribute("lineOfActivity", lineOfActivity);
					request.setAttribute("classification", classification);
					request.setAttribute("loanAccountNumber", loanAccountNumber);
					request.setAttribute("promotersName", promotersName);
					request.setAttribute("chiefPromoterPAN", chiefPromoterPAN);
					request.setAttribute("address", address);
					request.setAttribute("guranteeStartDate", guranteeStartDate);
					request.setAttribute("guranteeExpiryDate", guranteeExpiryDate);
					request.setAttribute("lastRenewalDateOfWC", lastRenewalDateOfWC);
					request.setAttribute("existing", existing);
					request.setAttribute("nonfundbasedexisting", nonfundbasedexisting);
					
					request.setAttribute("proposed",proposed);
					request.setAttribute("nonfundbasedproposed",nonfundbasedproposed);
					request.setAttribute("satcibilreport",satcibilreport);
					request.setAttribute("pddefaulter",pddefaulter);
					request.setAttribute("satposstatdues",satposstatdues);
					request.setAttribute("repaymenttracksat",repaymenttracksat);
					request.setAttribute("availofstockasset",availofstockasset);
					request.setAttribute("workingoffactory",workingoffactory);
					request.setAttribute("generalupkeep",generalupkeep);
					request.setAttribute("stockregister",stockregister);
					request.setAttribute("stockstatement",stockstatement);
					
					request.setAttribute("currentRatio",currentRatio);
					request.setAttribute("tolOrTNW",tolOrTNW);
					request.setAttribute("drOrCrDays",drOrCrDays);
					request.setAttribute("interestCoverage",interestCoverage);
					request.setAttribute("assetCoverageRatio",assetCoverageRatio);
					request.setAttribute("fixedAssetCoverageRatio",fixedAssetCoverageRatio);
					
					request.setAttribute("currrentRatioActualValue",currrentRatioActualValue);
					request.setAttribute("tolOrTNWActualValue",tolOrTNWActualValue);
					request.setAttribute("drOrCrDaysActualValue",drOrCrDaysActualValue);
					request.setAttribute("interestCoverageActualValue",interestCoverageActualValue);
					request.setAttribute("assetCoverageRatioActualValue",assetCoverageRatioActualValue);
					request.setAttribute("fixedAssetCoverageRatioActualValue",fixedAssetCoverageRatioActualValue);
					
					request.setAttribute("satisfactoryCreditReports",satisfactoryCreditReports);
					request.setAttribute("groupAssociateNPA",groupAssociateNPA);
					request.setAttribute("psMovablesValue",psMovablesValue);
					request.setAttribute("psMovablesRemarks",psMovablesRemarks);
					request.setAttribute("psImmovalableValue",psImmovalableValue);
					request.setAttribute("psImmovalableRemarks",psImmovalableRemarks);
					request.setAttribute("clMovablesValue",clMovablesValue);
					request.setAttribute("clMovablesRemarks",clMovablesRemarks);
					request.setAttribute("clImmovalableValue",clImmovalableValue);
					request.setAttribute("clImmovalableRemarks",clImmovalableRemarks);
					request.setAttribute("salesPreviousYear",salesPreviousYear);
					request.setAttribute("salesCurrentYear",salesCurrentYear);
					request.setAttribute("salesProjectionNextYear",salesProjectionNextYear);
					request.setAttribute("pbditPreviousYear",pbditPreviousYear);
					request.setAttribute("pbditCurrentYear",pbditCurrentYear);
					request.setAttribute("pbditProjectionNextYear",pbditProjectionNextYear);
					request.setAttribute("equityCapitalPreviousYear",equityCapitalPreviousYear);
					request.setAttribute("equityCapitalCurrentYear",equityCapitalCurrentYear);
					request.setAttribute("equityCapitalProjectionNextYear",equityCapitalProjectionNextYear);
					request.setAttribute("totalScoreGradePYAPAAppraial",totalScoreGradePYAPAAppraial);
					request.setAttribute("totalScoreGradeCYAPAAppraial",totalScoreGradeCYAPAAppraial);
					request.setAttribute("commentsOnScorePYAPAAppraial",commentsOnScorePYAPAAppraial);
					request.setAttribute("commentsOnScoreCYAPAAppraial",commentsOnScoreCYAPAAppraial);
			
					request.setAttribute("proposalinvestmentgrade",proposalinvestmentgrade);
				}
				else{
					resultset1 = (ResultSet) callableStmt.getObject(3);
				while (resultset1.next()) {
					rwcatyafObj = new RenewalOfWorkingCapitalAfterTenYearsActionForm();
					cgpan=resultset1.getString(1);
					unitName=resultset1.getString(2);
					unitPAN=resultset1.getString(3);
					lineOfActivity=resultset1.getString(4);
					classification=resultset1.getString(5);
					loanAccountNumber=resultset1.getString(6);
					promotersName=resultset1.getString(7);
					chiefPromoterPAN=resultset1.getString(8);
					address=resultset1.getString(9);
					guranteeStartDate=resultset1.getString(10);
					guranteeExpiryDate=resultset1.getString(11);
					lastRenewalDateOfWC=resultset1.getString(12);
					existing=resultset1.getString(13);
					nonfundbasedexisting=resultset1.getString(14);
					
					proposed=resultset1.getString(15);
					nonfundbasedproposed =resultset1.getString(16);
					
					satcibilreport =resultset1.getString(17);
					pddefaulter =resultset1.getString(18);
					satposstatdues =resultset1.getString(19);
					repaymenttracksat =resultset1.getString(20); 
					availofstockasset =resultset1.getString(21);
					workingoffactory =resultset1.getString(22);
					generalupkeep =resultset1.getString(23);
					stockregister =resultset1.getString(24);
					stockstatement =resultset1.getString(25);
					
					approvedAmtInLong = Long.parseLong(proposed)+ Long.parseLong(nonfundbasedproposed) ;
					
					
					if(approvedAmtInLong>1000000 && approvedAmtInLong<=4999999){
						
						currentRatio = resultset1.getString(26);
						tolOrTNW =	resultset1.getString(27);
						drOrCrDays =	resultset1.getString(28);
						interestCoverage = resultset1.getString(29);
						assetCoverageRatio = resultset1.getString(30);
						fixedAssetCoverageRatio =	resultset1.getString(31);
							
						currrentRatioActualValue =	resultset1.getString(32);
						tolOrTNWActualValue =	resultset1.getString(33);
						drOrCrDaysActualValue =	resultset1.getString(34);
						interestCoverageActualValue =	resultset1.getString(35);
						assetCoverageRatioActualValue =	resultset1.getString(36);
						fixedAssetCoverageRatioActualValue = resultset1.getString(37);	
						
					}else if(approvedAmtInLong>=5000000){
						
						currentRatio = resultset1.getString(26);
						tolOrTNW =	resultset1.getString(27);
						drOrCrDays =	resultset1.getString(28);
						interestCoverage = resultset1.getString(29);
						assetCoverageRatio = resultset1.getString(30);
						fixedAssetCoverageRatio =	resultset1.getString(31);
							
						currrentRatioActualValue =	resultset1.getString(32);
						tolOrTNWActualValue =	resultset1.getString(33);
						drOrCrDaysActualValue =	resultset1.getString(34);
						interestCoverageActualValue =	resultset1.getString(35);
						assetCoverageRatioActualValue =	resultset1.getString(36);
						fixedAssetCoverageRatioActualValue = resultset1.getString(37);	
						
						satisfactoryCreditReports = resultset1.getString(38);
						groupAssociateNPA = resultset1.getString(39);
						
						psMovablesValue = resultset1.getString(40);
						psMovablesRemarks = resultset1.getString(41);
						
						psImmovalableValue = resultset1.getString(42);
						psImmovalableRemarks = resultset1.getString(43);
						
						clMovablesValue = resultset1.getString(44);
						clMovablesRemarks = resultset1.getString(45);
						
						clImmovalableValue = resultset1.getString(46);
						clImmovalableRemarks = resultset1.getString(47);
						
						salesPreviousYear = resultset1.getString(48);
						salesCurrentYear = resultset1.getString(49);
						salesProjectionNextYear = resultset1.getString(50);
						
						pbditPreviousYear = resultset1.getString(51);
						pbditCurrentYear = resultset1.getString(52);
						pbditProjectionNextYear = resultset1.getString(53);
						
						equityCapitalPreviousYear = resultset1.getString(54);
						equityCapitalCurrentYear = resultset1.getString(55);
						equityCapitalProjectionNextYear = resultset1.getString(56);
						
						totalScoreGradePYAPAAppraial = resultset1.getString(57);
						totalScoreGradeCYAPAAppraial = resultset1.getString(58);
						
						commentsOnScorePYAPAAppraial = resultset1.getString(59);
						commentsOnScoreCYAPAAppraial = resultset1.getString(60);
					
					}
					
					proposalinvestmentgrade =resultset1.getString(61);
					
					//////------------------------------------------------------------------------------/////
					
					rwcatyafObj.setCgpan((resultset1.getString(1)));
					rwcatyafObj.setUnitName((resultset1.getString(2)));
					rwcatyafObj.setUnitPAN((resultset1.getString(3)));
					rwcatyafObj.setLineOfActivity((resultset1.getString(4)));
					rwcatyafObj.setClassification((resultset1.getString(5)));
					rwcatyafObj.setLoanAccountNumber((resultset1.getString(6)));
					rwcatyafObj.setPromotersName((resultset1.getString(7)));
					rwcatyafObj.setChiefPromoterPAN((resultset1.getString(8)));
					rwcatyafObj.setAddress((resultset1.getString(9)));
					rwcatyafObj.setGuranteeStartDate((resultset1.getString(10)));
					rwcatyafObj.setGuranteeExpiryDate((resultset1.getString(11)));
					rwcatyafObj.setLastRenewalDateOfWC((resultset1.getString(12)));
					rwcatyafObj.setExisting((resultset1.getString(13)));
					rwcatyafObj.setNonfundbasedexisting((resultset1.getString(14)));
					
					rwcatyafObj.setProposed((resultset1.getString(15)));
					rwcatyafObj.setNonfundbasedproposed((resultset1.getString(16)));
					
					rwcatyafObj.setSatisfactoryCibilReports((resultset1.getString(17)));
					rwcatyafObj.setPddefaulter((resultset1.getString(18)));
					rwcatyafObj.setSatisfactoryPositionStatDues((resultset1.getString(19)));
					rwcatyafObj.setRepaymentTrackSatisfactory((resultset1.getString(20)));
					rwcatyafObj.setAvailabilityOfStockAsset((resultset1.getString(21)));
					rwcatyafObj.setWorkingOfTheFactory((resultset1.getString(22)));
					rwcatyafObj.setGeneralUpkeep((resultset1.getString(23)));
					rwcatyafObj.setStockRegister((resultset1.getString(24)));
					rwcatyafObj.setStockStatement(resultset1.getString(25));
					
					if(approvedAmtInLong>1000000 && approvedAmtInLong<=4999999){
						System.out.println("Setting value in Approved amount between 10 Lkh && 4999999 Lkh.");
						rwcatyafObj.setCurrentRatio(resultset1.getString(26));
						rwcatyafObj.setTolOrTNW(resultset1.getString(27));
						rwcatyafObj.setDrOrCrDays(resultset1.getString(28));
						rwcatyafObj.setInterestCoverage(resultset1.getString(29));
						rwcatyafObj.setAssetCoverageRatio(resultset1.getString(30));
						rwcatyafObj.setFixedAssetCoverageRatio(resultset1.getString(31));
						
						rwcatyafObj.setCurrrentRatioActualValue(resultset1.getString(32));
						rwcatyafObj.setTolOrTNWActualValue(resultset1.getString(33));
						rwcatyafObj.setDrOrCrDaysActualValue(resultset1.getString(34));
						rwcatyafObj.setInterestCoverageActualValue(resultset1.getString(35));
						rwcatyafObj.setAssetCoverageRatioActualValue(resultset1.getString(36));
						rwcatyafObj.setFixedAssetCoverageRatioActualValue(resultset1.getString(37));
						
					}
					else if(approvedAmtInLong>=5000000 ){
						System.out.println("Setting value in Approved amount Above 5000000 Lkh.");
						
						rwcatyafObj.setCurrentRatio(resultset1.getString(26));
						rwcatyafObj.setTolOrTNW(resultset1.getString(27));
						rwcatyafObj.setDrOrCrDays(resultset1.getString(28));
						rwcatyafObj.setInterestCoverage(resultset1.getString(29));
						rwcatyafObj.setAssetCoverageRatio(resultset1.getString(30));
						rwcatyafObj.setFixedAssetCoverageRatio(resultset1.getString(31));
						
						rwcatyafObj.setCurrrentRatioActualValue(resultset1.getString(32));
						rwcatyafObj.setTolOrTNWActualValue(resultset1.getString(33));
						rwcatyafObj.setDrOrCrDaysActualValue(resultset1.getString(34));
						rwcatyafObj.setInterestCoverageActualValue(resultset1.getString(35));
						rwcatyafObj.setAssetCoverageRatioActualValue(resultset1.getString(36));
						rwcatyafObj.setFixedAssetCoverageRatioActualValue(resultset1.getString(37));
						
						rwcatyafObj.setSatisfactoryCreditReports(resultset1.getString(38));
						rwcatyafObj.setGroupAssociateNPA(resultset1.getString(39));
						
						rwcatyafObj.setPsMovablesValue(resultset1.getString(40));
						rwcatyafObj.setPsMovablesRemarks(resultset1.getString(41));
						
						rwcatyafObj.setPsImmovalableValue(resultset1.getString(42));
						rwcatyafObj.setPsImmovalableRemarks(resultset1.getString(43));
						
						rwcatyafObj.setClMovablesValue(resultset1.getString(44));
						rwcatyafObj.setClMovablesRemarks(resultset1.getString(45));
						
						rwcatyafObj.setClImmovalableValue(resultset1.getString(46));
						rwcatyafObj.setClImmovalableRemarks(resultset1.getString(47));
						
						rwcatyafObj.setSalesPreviousYear(resultset1.getString(48));
						rwcatyafObj.setSalesCurrentYear(resultset1.getString(49));
						rwcatyafObj.setSalesProjectionNextYear(resultset1.getString(50));
						
						rwcatyafObj.setPbditPreviousYear(resultset1.getString(51));
						rwcatyafObj.setPbditCurrentYear(resultset1.getString(52));
						rwcatyafObj.setPbditProjectionNextYear(resultset1.getString(53));
						
						rwcatyafObj.setEquityCapitalPreviousYear(resultset1.getString(54));
						rwcatyafObj.setEquityCapitalCurrentYear(resultset1.getString(55));
						rwcatyafObj.setEquityCapitalProjectionNextYear(resultset1.getString(56));
						
						rwcatyafObj.setTotalScoreGradePYAPAAppraial(resultset1.getString(57));
						rwcatyafObj.setTotalScoreGradeCYAPAAppraial(resultset1.getString(58));
						
						rwcatyafObj.setCommentsOnScorePYAPAAppraial(resultset1.getString(59));
						rwcatyafObj.setCommentsOnScoreCYAPAAppraial(resultset1.getString(60));
									
					}
					rwcatyafObj.setProposalInvstGrade((resultset1.getString(61)));
					
				//////------------------------------------------------------------------------------/////
			}
					request.setAttribute("cgpan", cgpan);
					request.setAttribute("unitName", unitName);
					request.setAttribute("unitPAN", unitPAN);
					request.setAttribute("lineOfActivity", lineOfActivity);
					request.setAttribute("classification", classification);
					request.setAttribute("loanAccountNumber", loanAccountNumber);
					request.setAttribute("promotersName", promotersName);
					request.setAttribute("chiefPromoterPAN", chiefPromoterPAN);
					request.setAttribute("address", address);
					request.setAttribute("guranteeStartDate", guranteeStartDate);
					request.setAttribute("guranteeExpiryDate", guranteeExpiryDate);
					request.setAttribute("lastRenewalDateOfWC", lastRenewalDateOfWC);
					request.setAttribute("existing", existing);
					request.setAttribute("nonfundbasedexisting", nonfundbasedexisting);
					
					request.setAttribute("proposed",proposed);
					request.setAttribute("nonfundbasedproposed",nonfundbasedproposed);
					request.setAttribute("satcibilreport",satcibilreport);
					request.setAttribute("pddefaulter",pddefaulter);
					request.setAttribute("satposstatdues",satposstatdues);
					request.setAttribute("repaymenttracksat",repaymenttracksat);
					request.setAttribute("availofstockasset",availofstockasset);
					request.setAttribute("workingoffactory",workingoffactory);
					request.setAttribute("generalupkeep",generalupkeep);
					request.setAttribute("stockregister",stockregister);
					request.setAttribute("stockstatement",stockstatement);
					
					
					request.setAttribute("proposalinvestmentgrade",proposalinvestmentgrade);
					
					if(approvedAmtInLong<=1000000){
						forwardPageName="displayRenewalOfWorkingCapitalUpto10LakhInputForm";
						System.out.println("<<displayRenewalOfWorkingCapitalUpto10LakhInputForm>>");
					}
					else if(approvedAmtInLong>1000000 && approvedAmtInLong<=4999999){
						
						System.out.println("setAttribute value in Approved amount between 10 Lkh && 4999999 Lkh.");
						request.setAttribute("currentRatio",currentRatio);
						request.setAttribute("tolOrTNW",tolOrTNW);
						request.setAttribute("drOrCrDays",drOrCrDays);
						request.setAttribute("interestCoverage",interestCoverage);
						request.setAttribute("assetCoverageRatio",assetCoverageRatio);
						request.setAttribute("fixedAssetCoverageRatio",fixedAssetCoverageRatio);
						
						request.setAttribute("currrentRatioActualValue",currrentRatioActualValue);
						request.setAttribute("tolOrTNWActualValue",tolOrTNWActualValue);
						request.setAttribute("drOrCrDaysActualValue",drOrCrDaysActualValue);
						request.setAttribute("interestCoverageActualValue",interestCoverageActualValue);
						request.setAttribute("assetCoverageRatioActualValue",assetCoverageRatioActualValue);
						request.setAttribute("fixedAssetCoverageRatioActualValue",fixedAssetCoverageRatioActualValue);
						
						forwardPageName="displayRenewalOfWorkingCapital10To50LakhInputForm";
						System.out.println("<<displayRenewalOfWorkingCapital10To50LakhInputForm>>");
					}
					else if(approvedAmtInLong>=5000000 ){
						
						request.setAttribute("currentRatio",currentRatio);
						request.setAttribute("tolOrTNW",tolOrTNW);
						request.setAttribute("drOrCrDays",drOrCrDays);
						request.setAttribute("interestCoverage",interestCoverage);
						request.setAttribute("assetCoverageRatio",assetCoverageRatio);
						request.setAttribute("fixedAssetCoverageRatio",fixedAssetCoverageRatio);
						
						request.setAttribute("currrentRatioActualValue",currrentRatioActualValue);
						request.setAttribute("tolOrTNWActualValue",tolOrTNWActualValue);
						request.setAttribute("drOrCrDaysActualValue",drOrCrDaysActualValue);
						request.setAttribute("interestCoverageActualValue",interestCoverageActualValue);
						request.setAttribute("assetCoverageRatioActualValue",assetCoverageRatioActualValue);
						request.setAttribute("fixedAssetCoverageRatioActualValue",fixedAssetCoverageRatioActualValue);
						
						request.setAttribute("satisfactoryCreditReports",satisfactoryCreditReports);
						request.setAttribute("groupAssociateNPA",groupAssociateNPA);
						request.setAttribute("psMovablesValue",psMovablesValue);
						request.setAttribute("psMovablesRemarks",psMovablesRemarks);
						request.setAttribute("psImmovalableValue",psImmovalableValue);
						request.setAttribute("psImmovalableRemarks",psImmovalableRemarks);
						request.setAttribute("clMovablesValue",clMovablesValue);
						request.setAttribute("clMovablesRemarks",clMovablesRemarks);
						request.setAttribute("clImmovalableValue",clImmovalableValue);
						request.setAttribute("clImmovalableRemarks",clImmovalableRemarks);
						request.setAttribute("salesPreviousYear",salesPreviousYear);
						request.setAttribute("salesCurrentYear",salesCurrentYear);
						request.setAttribute("salesProjectionNextYear",salesProjectionNextYear);
						request.setAttribute("pbditPreviousYear",pbditPreviousYear);
						request.setAttribute("pbditCurrentYear",pbditCurrentYear);
						request.setAttribute("pbditProjectionNextYear",pbditProjectionNextYear);
						request.setAttribute("equityCapitalPreviousYear",equityCapitalPreviousYear);
						request.setAttribute("equityCapitalCurrentYear",equityCapitalCurrentYear);
						request.setAttribute("equityCapitalProjectionNextYear",equityCapitalProjectionNextYear);
						request.setAttribute("totalScoreGradePYAPAAppraial",totalScoreGradePYAPAAppraial);
						request.setAttribute("totalScoreGradeCYAPAAppraial",totalScoreGradeCYAPAAppraial);
						request.setAttribute("commentsOnScorePYAPAAppraial",commentsOnScorePYAPAAppraial);
						request.setAttribute("commentsOnScoreCYAPAAppraial",commentsOnScoreCYAPAAppraial);

						forwardPageName="displayRenewalOfWorkingCapitalAfterTenYearsInputForm";
						System.out.println("<<displayRenewalOfWorkingCapital Rs.50 Lakh and Above>>");
					}
				}
			}catch(Exception e){
				System.out.println("Exception occured==="+e);
				errorMsg="Please contact to Admin";
				request.setAttribute("errorMsg", errorMsg);
			}
		 
		 System.out.println("getMliFormDetails method called First time when Input page will come to fill the data.................");
		 System.out.println("cgpan=="+RenewalOfWorkingCapitalAfterTenYearsActionFormObj1.getCgpan());
		 return mapping.findForward(forwardPageName);
	 
  }
	//Parmanand
  public ActionForward saveWcApproval(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	System.out.println("saveWcApproval===================================================================================");
	ResultSet rs = null;
	PreparedStatement pst = null;
	
	ResultSet rsRenWc = null;
	PreparedStatement pstRenWc = null;
	
	ResultSet rsWc = null;
	PreparedStatement pstWc = null;
	
	Connection conn = DBConnection.getConnection();
	try {
		
		Statement str = null;
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		String memberId = (new StringBuilder()).append(bankid).append(zoneid).append(branchid).toString();
		String loggedUsr = user.getUserId();
		RenewalOfWorkingCapitalAfterTenYearsActionForm renewalFormDataObj = (RenewalOfWorkingCapitalAfterTenYearsActionForm)form;
		
		Vector claimRefSetCases = renewalFormDataObj.getFirstInstallmentClaims();
		List voList=renewalFormDataObj.getFirstInstallmentClaims();   
		System.out.println("<<Cgpan List>>"+voList.size());
		String actionStauts[] = request.getParameterValues("statuss");
		String mliComments[] = request.getParameterValues("mlicomments");
		String cgpan[] = request.getParameterValues("cgPanValue");
		String proposedAmt[] = request.getParameterValues("proposedAmt");
		String nonFundBasedProposedAmt[] = request.getParameterValues("nonFundBasedProposedAmt");
		String mliLodgeDate[] = request.getParameterValues("mliLodgeDate");
		
		String actFlg = "";
		
		for(int i = 0; i < voList.size(); i++){
			RenewalOfWorkingCapitalAfterTenYearsActionForm renFormData = (RenewalOfWorkingCapitalAfterTenYearsActionForm)form;
	        String act = actionStauts[i];
	        if(act.equals("AP")) {
	            actFlg = "AP";
	        }
	        if(act.equals("RT")) {
	            actFlg = "RT";
	        }
	        if(act.equals("RE")) {
	            actFlg = "RE";
	        }
	        if(act.equals("RN")) {
	            actFlg = "P";
	        }
	        
		        String remarks = mliComments[i];
		        String selectedCgpan=cgpan[i];
		        System.out.println(" selectedCgpan==="+selectedCgpan);
		      /*  pst = conn.prepareStatement("UPDATE APPLICATION_DETAIL SET APP_STATUS = ?, APP_REMARKS = ? WHERE CGPAN = ?");
		    	pst.setString(1, actFlg);
		    	pst.setString(2, remarks);
		    	pst.setString(3, selectedCgpan);
		    	int rowAffected=pst.executeUpdate();
		    	System.out.println("Data rowAffected rowAffected==="+rowAffected);*/
		    	
		    	String mli_Lodge_Date = mliLodgeDate[i];
		    	pstRenWc = conn.prepareStatement("UPDATE REN_OF_WC_AFTER_TEN_Y@cginter SET WS_STATUS = ?, WC_LIMIT_COMMENTS = ?, APPROVAL_DATE=sysdate, MODIFIED_DATE=sysdate,updated_by=? WHERE CGPAN = ? and to_char(MLI_LODGE_DATE,'dd-mm-yyyy')=?");
		    	pstRenWc.setString(1, actFlg);
		    	pstRenWc.setString(2, remarks);
		    	pstRenWc.setString(3, loggedUsr);
		    	pstRenWc.setString(4, selectedCgpan);
		    	pstRenWc.setString(5, mli_Lodge_Date);
		    	int rowAffectedRenWc = pstRenWc.executeUpdate();
		       	System.out.println("Data Updated Successfully rowAffectedRenWc==="+rowAffectedRenWc);
		    	
		       	String fundbasedproposedAmt = proposedAmt[i];
		       	String nonfundbasedproposedAmt = nonFundBasedProposedAmt[i];
		    	pstWc = conn.prepareStatement("UPDATE WORKING_CAPITAL_DETAIL@cginter set WCP_FB_LIMIT_SANCTIONED = ?,WCP_NFB_LIMIT_SANCTIONED = ? WHERE CGPAN =?");
		    	pstWc.setString(1,fundbasedproposedAmt);
		    	pstWc.setString(2,nonfundbasedproposedAmt);
		    	pstWc.setString(3,selectedCgpan);
		    	int rowAffectedWc = pstWc.executeUpdate();
		    	System.out.println("Data Updated Successfully rowAffectedWc>>"+rowAffectedWc);
		    	//WCP_FB_LIMIT_SANCTIONED,WCP_NFB_LIMIT_SANCTIONED,WCP_FB_CREDIT_TO_GUARANTEE,WCP_NFB_CREDIT_TO_GUARANTEE from WORKING_CAPITAL_DETAIL 
	        
	    }
		conn.commit();
	}catch (SQLException ex) {
        System.out.println("Ëxception===="+ex.getMessage());
    }
	finally{
		conn.commit();
		conn.close();
		conn = null;
        DBConnection.freeConnection(conn);
        }

	return mapping.findForward("summaryPageWC");
	  
  }


	public ActionForward districtApplicationDetails1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "districtApplicationDetails1",
				"Entered");
		ArrayList mliDistrictReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String flag = (String) dynaForm.get("checkValue");
		String bank = (String) dynaForm.get("state");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument12");
		String stDate = String.valueOf(sDate);
		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;

		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument13");
		endDate = new java.sql.Date(eDate.getTime());
		String district = request.getParameter("District");
		String id = (String) dynaForm.get("checkValue");
		if (id.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (id.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		request.setAttribute("sate", district);

		int i = district.indexOf("$");
		int j = bank.indexOf("$");

		if (j != -1) {
			String newBank = bank.replace('$', '&');
			if (i != -1) {
				String newDistrict = district.replace('$', '&');
				mliDistrictReport = reportManager.getMliDistrictDetails(
						newDistrict, flag, newBank, startDate, endDate);
				dynaForm.set("mliDistrictReport", mliDistrictReport);
				if (mliDistrictReport == null || mliDistrictReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// mliDistrictReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"districtApplicationDetails1", "Exited");
					return mapping.findForward("success");
				}
			} else {
				mliDistrictReport = reportManager.getMliDistrictDetails(
						district, flag, newBank, startDate, endDate);
				dynaForm.set("mliDistrictReport", mliDistrictReport);
				if (mliDistrictReport == null || mliDistrictReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// mliDistrictReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"districtApplicationDetails1", "Exited");
					return mapping.findForward("success");
				}
			}

		} else {
			if (i != -1) {
				String newDistrict = district.replace('$', '&');
				mliDistrictReport = reportManager.getMliDistrictDetails(
						newDistrict, flag, bank, startDate, endDate);
				dynaForm.set("mliDistrictReport", mliDistrictReport);
				if (mliDistrictReport == null || mliDistrictReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// mliDistrictReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"districtApplicationDetails1", "Exited");
					return mapping.findForward("success");
				}
			} else {
				mliDistrictReport = reportManager.getMliDistrictDetails(
						district, flag, bank, startDate, endDate);
				dynaForm.set("mliDistrictReport", mliDistrictReport);
				if (mliDistrictReport == null || mliDistrictReport.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered,"
									+ " Please Enter Any Other Value ");
				} else {
					// mliDistrictReport = null;
					Log.log(Log.INFO, "ReportsAction",
							"districtApplicationDetails1", "Exited");
					return mapping.findForward("success");
				}
			}
		}
	}

	public ActionForward guaranteeCover(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "guaranteeCover", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(Log.INFO, "ReportsAction", "guaranteeCover", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward guaranteeCoverReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReport", "Entered");

		ArrayList guaranteeDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		String guarantee = (String) dynaForm.get("check");
		// modification@sudeep.dhiman
		if (guarantee.equals("yes"))
			request.setAttribute("radioValue", "CGPAN");
		else if (guarantee.equals("no"))
			request.setAttribute("radioValue", "BORROWER/Unit Name");

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());

		if (guarantee.equals("yes")) {
			guaranteeDetails = reportManager.guaranteeCoverMli(startDate,
					endDate);
			dynaForm.set("guaranteeCoverSsi", guaranteeDetails);

			if (guaranteeDetails == null || guaranteeDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				guaranteeDetails = null;
				Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReport",
						"Exited");
				return mapping.findForward("success1");
			}
		} else {
			guaranteeDetails = reportManager.guaranteeCoverSsiMli(startDate,
					endDate);
			dynaForm.set("guaranteeCoverSsi", guaranteeDetails);
			if (guaranteeDetails == null || guaranteeDetails.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered,"
								+ " Please Enter Any Other Value ");
			} else {
				guaranteeDetails = null;
				Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReport",
						"Exited");
				return mapping.findForward("success2");
			}
		}
		// return mapping.findForward("success2");
	}

	public ActionForward guaranteeCoverReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReportDetails",
				"Entered");
		ArrayList guaranteeDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String bank = (String) dynaForm.get("bank");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		String guarantee = (String) dynaForm.get("check");
		// modification@sudeep.dhiman
		if (guarantee.equals("yes"))
			request.setAttribute("radioValue", "CGPAN");
		else if (guarantee.equals("no"))
			request.setAttribute("radioValue", "BORROWER/Unit Name");

		request.setAttribute("BANKNAME", request.getParameter("name"));

		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());

		guaranteeDetails = reportManager.getGuaranteeCover(startDate, endDate,
				bank);
		dynaForm.set("guaranteeCoverSsi", guaranteeDetails);
		if (guaranteeDetails == null || guaranteeDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			guaranteeDetails = null;
			Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward guaranteeCoverReportDetailsSsi(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "guaranteeCoverReportDetailsSsi",
				"Entered");
		ArrayList guaranteeDetails = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String bank = (String) dynaForm.get("bank");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		String guarantee = (String) dynaForm.get("check");
		// modification@sudeep.dhiman
		if (guarantee.equals("yes"))
			request.setAttribute("radioValue", "CGPAN");
		else if (guarantee.equals("no"))
			request.setAttribute("radioValue", "BORROWER/Unit Name");

		request.setAttribute("BANKNAME", request.getParameter("name"));

		if ((stDate == null) || (stDate.equals(""))) {
			startDate = null;
		} else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());

		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());

		guaranteeDetails = reportManager.getGuaranteeCoverSsi(startDate,
				endDate, bank);
		dynaForm.set("guaranteeCoverSsi", guaranteeDetails);
		if (guaranteeDetails == null || guaranteeDetails.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			guaranteeDetails = null;
			Log.log(Log.INFO, "ReportsAction",
					"guaranteeCoverReportDetailsSsi", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward sizeWiseReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sizeWiseReport", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument6(prevDate);
		generalReport.setDateOfTheDocument7(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "sizeWiseReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward sizeWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sizeWiseReportDetails", "Entered");
		ArrayList ProposalDistrictReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String guarantee = (String) dynaForm.get("checkValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument6");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		/*
		 * change by Sudeep: here are request variables added to display the
		 * radio buton value to be displayed in repor
		 */

		if (guarantee.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (guarantee.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument7");
		endDate = new java.sql.Date(eDate.getTime());
		ProposalDistrictReport = reportManager.getProposalSizeReport(startDate,
				endDate, guarantee);
		dynaForm.set("proposalSizeReport", ProposalDistrictReport);
		if (ProposalDistrictReport == null
				|| ProposalDistrictReport.size() == 0) {

			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			ProposalDistrictReport = null;
			Log.log(Log.INFO, "ReportsAction", "sizeWiseReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward sectorWiseReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetails", "Entered");
		ArrayList ProposalDistrictReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		String guarantee = (String) dynaForm.get("checkValue");
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument8");
		String stDate = String.valueOf(sDate);

		if (guarantee.equals("yes"))
			request.setAttribute("radioValue", "Guarantee Approved");
		else if (guarantee.equals("no"))
			request.setAttribute("radioValue", "Guarantee Issued");

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument9");
		endDate = new java.sql.Date(eDate.getTime());
		ProposalDistrictReport = reportManager.getProposalSectorReport(
				startDate, endDate, guarantee);
		dynaForm.set("proposalDistrictReport", ProposalDistrictReport);
		if (ProposalDistrictReport == null
				|| ProposalDistrictReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			ProposalDistrictReport = null;
			Log.log(Log.INFO, "ReportsAction", "sectorWiseReportDetails",
					"Exited");
			return mapping.findForward("success");
		}
	}

	// /////////////////////// Reports for Claims Processing //////////////////
	public ActionForward showFilterForClaimDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		CustomisedDate customDate = new CustomisedDate();
		customDate.setDate(prevDate);

		CustomisedDate customToDate = new CustomisedDate();
		customToDate.setDate(date);

		claimForm.setFromDate(customDate);
		claimForm.setToDate(customToDate);
		claimForm.setClmApplicationStatus(ClaimConstants.CLM_APPROVAL_STATUS);
		return mapping.findForward("displayFilterForClaimDtls");
	}

	public ActionForward displayListOfClaimRefNumbers(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "displayListOfClaimRefNumbers",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;

		// Retrieving the Member Id from the User Object
		User user = getUserInformation(request);
		String bankid = (user.getBankId()).trim();
		String zoneid = (user.getZoneId()).trim();
		String branchid = (user.getBranchId()).trim();
		String memberId = bankid + zoneid + branchid;

		java.util.Date fromDate = (java.util.Date) claimForm.getFromDate();
		java.util.Date toDate = (java.util.Date) claimForm.getToDate();
		String clmApplicationStatus = (String) claimForm
				.getClmApplicationStatus();
		// System.out.println("clmApplicationStatus:"+clmApplicationStatus);
		claimForm.setstatusFlag(clmApplicationStatus);
		ReportManager manager = new ReportManager();
		Vector listOfClmRefNumbers = null;
		java.sql.Date sqlFromDate = null;

		if ((fromDate.toString()).equals(""))
			listOfClmRefNumbers = manager.getListOfClaimRefNumbersNew(null,
					new java.sql.Date(toDate.getTime()), clmApplicationStatus,
					memberId);
		else {
			sqlFromDate = new java.sql.Date(fromDate.getTime());
			listOfClmRefNumbers = manager.getListOfClaimRefNumbersNew(
					sqlFromDate, new java.sql.Date(toDate.getTime()),
					clmApplicationStatus, memberId);
		}

		if (clmApplicationStatus.equals("AP"))
			request.setAttribute("radioValue", "Approved");
		else if (clmApplicationStatus.equals("NE"))
			request.setAttribute("radioValue", "NEW");
		else if (clmApplicationStatus.equals("RE"))
			request.setAttribute("radioValue", "Rejected");
		else if (clmApplicationStatus.equals("HO"))
			request.setAttribute("radioValue", "Hold");
		else if (clmApplicationStatus.equals("FW"))
			request.setAttribute("radioValue", "Forwarded");
		else if (clmApplicationStatus.equals("TC"))
			request.setAttribute("radioValue", "Temporary Closed");
		else if (clmApplicationStatus.equals("TR"))
			request.setAttribute("radioValue", "Temporary Rejected");
		else if (clmApplicationStatus.equals("WD"))
			request.setAttribute("radioValue", "Claim Withdrawn");

		// added by upchar@path on 03/07/2013
		else if (clmApplicationStatus.equals("RR"))
			request.setAttribute("radioValue", "Reply Received");

		/* end */

		else if (clmApplicationStatus.equals("RT"))
			request.setAttribute("radioValue", "Returned");

		claimForm.setListOfClmRefNumbers(listOfClmRefNumbers);
		if (listOfClmRefNumbers != null) {
			if (listOfClmRefNumbers.size() == 0) {
				throw new NoDataException("There are no Claim Ref Numbers "
						+ "that match the query.");
			}
		}
		Log.log(Log.INFO, "ReportsAction", "displayListOfClaimRefNumbers",
				"Exited");
		return mapping.findForward("getListOfClmRefNumbers");
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
	public ActionForward displayClmRefNumberDtl(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "displayClmRefNumberDtl", "Entered");
		ReportManager manager = new ReportManager();
		ClaimActionForm claimForm = (ClaimActionForm) form;

		String clmApplicationStatus = claimForm.getClmApplicationStatus();
		Log.log(Log.INFO, "ReportsAction", "displayClmRefNumberDtl",
				"Claim Application Status being queried :"
						+ clmApplicationStatus);

		// Retrieving the Member Id from the User Object
		User user = getUserInformation(request);
		String bankid = (user.getBankId()).trim();
		String zoneid = (user.getZoneId()).trim();
		String branchid = (user.getBranchId()).trim();
		String memberId = bankid + zoneid + branchid;
		// System.out.println("MemId:"+memberId);

		/*
		 * modification@sudeep.dhiman setting request Attribute to display the
		 * radio button value in report
		 */
		// String clmApplicationStatus =
		// (String)claimForm.getClmApplicationStatus();
		if (clmApplicationStatus.equals("AP"))
			request.setAttribute("radioValue", "Approved");
		else if (clmApplicationStatus.equals("NE"))
			request.setAttribute("radioValue", "NEW");
		else if (clmApplicationStatus.equals("RE"))
			request.setAttribute("radioValue", "Rejected");
		else if (clmApplicationStatus.equals("HO"))
			request.setAttribute("radioValue", "Hold");
		else if (clmApplicationStatus.equals("FW"))
			request.setAttribute("radioValue", "Forwarded");
		else if (clmApplicationStatus.equals("TC"))
			request.setAttribute("radioValue", "Temporary Closed");
		else if (clmApplicationStatus.equals("TR"))
			request.setAttribute("radioValue", "Temporary Rejected");
		/* end */
		// System.out.println("clmApplicationStatus:"+clmApplicationStatus);
		else if (clmApplicationStatus.equals("RT"))
			request.setAttribute("radioValue", "Returned");
		else if (clmApplicationStatus.equals("RU"))
			request.setAttribute("radioValue", "ReturnedUpdated");

		request.setAttribute("CLAIMREFNO",
				request.getParameter("ClaimRefNumber"));

		ClaimsProcessor processor = new ClaimsProcessor();
		String claimRefNumber = (String) request
				.getParameter(ClaimConstants.CLM_CLAIM_REF_NUMBER);
		// System.out.println("claimRefNumber:"+claimRefNumber);
		ClaimApplication claimapplication = manager.displayClmRefNumberDtl(
				claimRefNumber, clmApplicationStatus, memberId);
		ArrayList clmSummryDtls = claimapplication.getClaimSummaryDtls();
		// System.out.println("clmSummryDtls"+clmSummryDtls.size());

		User userInfo = getUserInformation(request);
		if (claimapplication.getFirstInstallment()) {
			// System.out.println("First Installment:");
			String thiscgpn = null;
			String bid = claimapplication.getBorrowerId();
			// System.out.println("bid:"+bid);
			String memId = (String) claimapplication.getMemberId();
			// System.out.println("memId:"+memId);
			// Vector cgpnDetails =
			// processor.getCGPANDetailsForBorrowerId(bid,memId);

			// Vector clmAppliedAmnts =
			// processor.getClaimAppliedAmounts(bid,ClaimConstants.FIRST_INSTALLMENT);
			/*
			 * ArrayList updateClmDtls = new ArrayList(); for(int j=0;
			 * j<clmSummryDtls.size(); j++) { ClaimSummaryDtls csdtl =
			 * (ClaimSummaryDtls)clmSummryDtls.remove(j); if(csdtl == null) {
			 * continue; } String pan = csdtl.getCgpan(); for(int i=0;
			 * i<cgpnDetails.size(); i++) { double apprvdAmnt = 0.0; HashMap dtl
			 * = (HashMap)cgpnDetails.remove(i); if(dtl == null) { continue; }
			 * 
			 * thiscgpn = (String)dtl.get(ClaimConstants.CLM_CGPAN); if(thiscgpn
			 * == null) { continue; } if(thiscgpn.equals(pan)) { Double db =
			 * (Double)dtl.get(ClaimConstants.CGPAN_APPRVD_AMNT); if(db != null)
			 * { apprvdAmnt = db.doubleValue(); }
			 * csdtl.setLimitCoveredUnderCGFSI(String.valueOf(apprvdAmnt));
			 * updateClmDtls.add(csdtl); } // dtl.clear(); dtl = null; } }
			 * claimapplication.setClaimSummaryDtls(updateClmDtls);
			 * claimForm.setClaimapplication(claimapplication);
			 */
			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimForm.setClaimapplication(claimapplication);
			// claimForm.setUpdatedClaimDtls(updateClmDtls);

			boolean internetUser = true;
			// All users belong to member id 0000 0000 0000 are intranet users
			// except demo user.
			if ((userInfo.getBankId() + userInfo.getZoneId() + userInfo
					.getBranchId()).equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER")) {
				internetUser = false;
			}
			// Get the attachements if available.
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);

			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Recall Notice Attachment path " + formattedToOSPath);

				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}

			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Legal Details Attachment path " + formattedToOSPath);

				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			if (claimapplication == null)
				throw new NoDataException(
						"Unable to get Claim Application details.");

			return mapping.findForward("showFirstClmDetails");
		} else if (claimapplication.getSecondInstallment()) {
			claimForm.setClaimapplication(claimapplication);
			/*
			 * Retrieving the Claim Applied Amounts for CGPAN(s) of the Borrower
			 */
			String bid = claimapplication.getBorrowerId();
			String memId = (String) claimapplication.getMemberId();
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(bid,
					ClaimConstants.FIRST_INSTALLMENT);
			Vector updateClmDtls = new Vector();
			String thiscgpn = null;
			// System.out.println("CGPN Details size :" + cgpnDetails.size());
			for (int i = 0; i < cgpnDetails.size(); i++) {
				HashMap dtl = (HashMap) cgpnDetails.elementAt(i);
				// System.out.println("Printing HashMap :" + dtl);
				if (dtl != null) {
					thiscgpn = (String) dtl.get(ClaimConstants.CLM_CGPAN);
					if (thiscgpn != null) {
						for (int j = 0; j < clmAppliedAmnts.size(); j++) {
							HashMap clmAppliedDtl = (HashMap) clmAppliedAmnts
									.elementAt(j);
							String cgpnInAppliedAmntsVec = null;
							if (clmAppliedDtl != null) {
								cgpnInAppliedAmntsVec = (String) clmAppliedDtl
										.get(ClaimConstants.CLM_CGPAN);
								if (cgpnInAppliedAmntsVec != null) {
									if (cgpnInAppliedAmntsVec.equals(thiscgpn)) {
										double clmAppliedAmnt = 0.0;
										Double clmAppAmntObj = (Double) clmAppliedDtl
												.get(ClaimConstants.CGPAN_CLM_APPLIED_AMNT);
										if (clmAppAmntObj != null) {
											clmAppliedAmnt = clmAppAmntObj
													.doubleValue();
										} else {
											clmAppliedAmnt = 0.0;
										}

										// Setting the Claim Applied Amount
										dtl.put(ClaimConstants.CGPAN_CLM_APPLIED_AMNT,
												new Double(clmAppliedAmnt));
										if (!updateClmDtls.contains(dtl)) {
											updateClmDtls.addElement(dtl);
										}

										// Clearing up the HashMap
										// clmAppliedDtl.clear();
										clmAppliedDtl = null;
										break;
									} else {
										continue;
									}
								} else {
									continue;
								}
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				} else {
					continue;
				}
				// dtl.clear();
				dtl = null;
			}

			/*
			 * Getting Claim Settlement Details and setting in the form
			 */
			HashMap settlmntDetails = processor
					.getClaimSettlementDetailForBorrower(bid);
			double firstSettlementAmnt = 0.0;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT);
			if (firstSettlementAmntObj != null) {
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			}
			java.util.Date firstSettlementDt = (java.util.Date) settlmntDetails
					.get(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT);

			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			// System.out.println("Size of Updated Dtls Vector :" +
			// updateClmDtls.size());
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				// System.out.println("Printing HashMap Reports Action :" +
				// dtl);
				if (dtl != null) {
					dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
							new Double(firstSettlementAmnt));
					dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
							firstSettlementDt);
					if (!finalUpdatedDtls.contains(dtl)) {
						finalUpdatedDtls.addElement(dtl);
					}
					// dtl.clear();
					dtl = null;
				}
			}
			ArrayList clmSummaryDtls = claimapplication.getClaimSummaryDtls();
			for (int j = 0; j < clmSummaryDtls.size(); j++) {
				ClaimSummaryDtls dtls = (ClaimSummaryDtls) clmSummaryDtls
						.get(j);
				String cgpan = null;
				double clmappliedamnt = 0.0;
				if (dtls != null) {
					cgpan = dtls.getCgpan();
					clmappliedamnt = dtls.getAmount();
				}
				for (int i = 0; i < updateClmDtls.size(); i++) {
					dtl = (HashMap) updateClmDtls.elementAt(i);
					// System.out.println("Printing HashMap Reports Action :" +
					// dtl);
					if (dtl != null) {
						String pan = (String) dtl.get(ClaimConstants.CLM_CGPAN);
						if (pan.equals(cgpan)) {
							dtl = (HashMap) updateClmDtls.remove(i);
							dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
									new Double(firstSettlementAmnt));
							dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
									firstSettlementDt);
							dtl.put(ClaimConstants.CGPAN_SEC_CLM_APPLIED_AMNT,
									new Double(clmappliedamnt));
							/*
							 * if(!finalUpdatedDtls.contains(dtl)) {
							 * finalUpdatedDtls.addElement(dtl); }
							 */
						}
						// dtl.clear();
						dtl = null;
					}
				}

			}
			claimForm.setUpdatedClaimDtls(finalUpdatedDtls);
			boolean internetUser = true;
			// All users belong to member id 0000 0000 0000 are intranet users
			// except demo user.
			if ((userInfo.getBankId() + userInfo.getZoneId() + userInfo
					.getBranchId()).equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER")) {
				internetUser = false;
			}
			// Get the attachements if available.
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);

			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Recall Notice Attachment path " + formattedToOSPath);

				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}

			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Legal Details Attachment path " + formattedToOSPath);

				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showSecClmDetails");
		}
		Log.log(Log.INFO, "ReportsAction", "displayClmRefNumberDtl", "Exited");
		return null;
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

	public ActionForward displayClmApplicationDtlNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "displayClmApplicationDtlNew",
				"Entered");
		ReportManager manager = new ReportManager();
		ClaimActionForm claimForm = (ClaimActionForm) form;

		String clmApplicationStatus = claimForm.getClmApplicationStatus();
		clmApplicationStatus = "NE";
		Log.log(Log.INFO, "ReportsAction", "displayClmApplicationDtlNew",
				"Claim Application Status being queried :"
						+ clmApplicationStatus);

		// Retrieving the Member Id from the User Object
		User user = getUserInformation(request);
		String bankid = (user.getBankId()).trim();
		String zoneid = (user.getZoneId()).trim();
		String branchid = (user.getBranchId()).trim();
		String memberId = bankid + zoneid + branchid;
		// System.out.println("MemId:"+memberId);
		request.setAttribute("CLAIMREFNO",
				request.getParameter("ClaimRefNumber"));

		ClaimsProcessor processor = new ClaimsProcessor();
		String claimRefNumber = (String) request
				.getParameter(ClaimConstants.CLM_CLAIM_REF_NUMBER);
		// System.out.println("claimRefNumber:"+claimRefNumber);
		ClaimApplication claimapplication = manager.displayClmRefNumberDtl(
				claimRefNumber, clmApplicationStatus, memberId);
		ArrayList clmSummryDtls = claimapplication.getClaimSummaryDtls();
		// System.out.println("clmSummryDtls"+clmSummryDtls.size());
		User userInfo = getUserInformation(request);
		if (claimapplication.getFirstInstallment()) {
			// System.out.println("First Installment:");
			String thiscgpn = null;
			String bid = claimapplication.getBorrowerId();
			// System.out.println("bid:"+bid);
			String memId = (String) claimapplication.getMemberId();
			// System.out.println("memId:"+memId);
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);

			// Vector clmAppliedAmnts =
			// processor.getClaimAppliedAmounts(bid,ClaimConstants.FIRST_INSTALLMENT);
			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimForm.setClaimapplication(claimapplication);
			// claimForm.setUpdatedClaimDtls(updateClmDtls);

			boolean internetUser = true;
			// All users belong to member id 0000 0000 0000 are intranet users
			// except demo user.
			if ((userInfo.getBankId() + userInfo.getZoneId() + userInfo
					.getBranchId()).equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER")) {
				internetUser = false;
			}
			// Get the attachements if available.
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);

			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Recall Notice Attachment path " + formattedToOSPath);

				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}

			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Legal Details Attachment path " + formattedToOSPath);

				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}

			return mapping.findForward("showFirstClmDetails");
		} else if (claimapplication.getSecondInstallment()) {
			claimForm.setClaimapplication(claimapplication);
			/*
			 * Retrieving the Claim Applied Amounts for CGPAN(s) of the Borrower
			 */
			String bid = claimapplication.getBorrowerId();
			String memId = (String) claimapplication.getMemberId();
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(bid,
					ClaimConstants.FIRST_INSTALLMENT);
			Vector updateClmDtls = new Vector();
			String thiscgpn = null;
			// System.out.println("CGPN Details size :" + cgpnDetails.size());
			for (int i = 0; i < cgpnDetails.size(); i++) {
				HashMap dtl = (HashMap) cgpnDetails.elementAt(i);
				// System.out.println("Printing HashMap :" + dtl);
				if (dtl != null) {
					thiscgpn = (String) dtl.get(ClaimConstants.CLM_CGPAN);
					if (thiscgpn != null) {
						for (int j = 0; j < clmAppliedAmnts.size(); j++) {
							HashMap clmAppliedDtl = (HashMap) clmAppliedAmnts
									.elementAt(j);
							String cgpnInAppliedAmntsVec = null;
							if (clmAppliedDtl != null) {
								cgpnInAppliedAmntsVec = (String) clmAppliedDtl
										.get(ClaimConstants.CLM_CGPAN);
								if (cgpnInAppliedAmntsVec != null) {
									if (cgpnInAppliedAmntsVec.equals(thiscgpn)) {
										double clmAppliedAmnt = 0.0;
										Double clmAppAmntObj = (Double) clmAppliedDtl
												.get(ClaimConstants.CGPAN_CLM_APPLIED_AMNT);
										if (clmAppAmntObj != null) {
											clmAppliedAmnt = clmAppAmntObj
													.doubleValue();
										} else {
											clmAppliedAmnt = 0.0;
										}

										// Setting the Claim Applied Amount
										dtl.put(ClaimConstants.CGPAN_CLM_APPLIED_AMNT,
												new Double(clmAppliedAmnt));
										if (!updateClmDtls.contains(dtl)) {
											updateClmDtls.addElement(dtl);
										}

										// Clearing up the HashMap
										// clmAppliedDtl.clear();
										clmAppliedDtl = null;
										break;
									} else {
										continue;
									}
								} else {
									continue;
								}
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				} else {
					continue;
				}
				// dtl.clear();
				dtl = null;
			}

			/*
			 * Getting Claim Settlement Details and setting in the form
			 */
			HashMap settlmntDetails = processor
					.getClaimSettlementDetailForBorrower(bid);
			double firstSettlementAmnt = 0.0;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT);
			if (firstSettlementAmntObj != null) {
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			}
			java.util.Date firstSettlementDt = (java.util.Date) settlmntDetails
					.get(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT);

			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			// System.out.println("Size of Updated Dtls Vector :" +
			// updateClmDtls.size());
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				// System.out.println("Printing HashMap Reports Action :" +
				// dtl);
				if (dtl != null) {
					dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
							new Double(firstSettlementAmnt));
					dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
							firstSettlementDt);
					if (!finalUpdatedDtls.contains(dtl)) {
						finalUpdatedDtls.addElement(dtl);
					}
					// dtl.clear();
					dtl = null;
				}
			}
			ArrayList clmSummaryDtls = claimapplication.getClaimSummaryDtls();
			for (int j = 0; j < clmSummaryDtls.size(); j++) {
				ClaimSummaryDtls dtls = (ClaimSummaryDtls) clmSummaryDtls
						.get(j);
				String cgpan = null;
				double clmappliedamnt = 0.0;
				if (dtls != null) {
					cgpan = dtls.getCgpan();
					clmappliedamnt = dtls.getAmount();
				}
				for (int i = 0; i < updateClmDtls.size(); i++) {
					dtl = (HashMap) updateClmDtls.elementAt(i);
					// System.out.println("Printing HashMap Reports Action :" +
					// dtl);
					if (dtl != null) {
						String pan = (String) dtl.get(ClaimConstants.CLM_CGPAN);
						if (pan.equals(cgpan)) {
							dtl = (HashMap) updateClmDtls.remove(i);
							dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
									new Double(firstSettlementAmnt));
							dtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
									firstSettlementDt);
							dtl.put(ClaimConstants.CGPAN_SEC_CLM_APPLIED_AMNT,
									new Double(clmappliedamnt));
							/*
							 * if(!finalUpdatedDtls.contains(dtl)) {
							 * finalUpdatedDtls.addElement(dtl); }
							 */
						}
						// dtl.clear();
						dtl = null;
					}
				}

			}
			claimForm.setUpdatedClaimDtls(finalUpdatedDtls);
			boolean internetUser = true;
			// All users belong to member id 0000 0000 0000 are intranet users
			// except demo user.
			if ((userInfo.getBankId() + userInfo.getZoneId() + userInfo
					.getBranchId()).equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER")) {
				internetUser = false;
			}
			// Get the attachements if available.
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);

			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Recall Notice Attachment path " + formattedToOSPath);

				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}

			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");

				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());

				Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
						" Legal Details Attachment path " + formattedToOSPath);

				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showSecClmDetails");
		}
		Log.log(Log.INFO, "ReportsAction", "displayClmApplicationDtlNew",
				"Exited");
		return null;
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

	public ActionForward displayClmApplicationDtlNewRev(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportManager manager;
		ClaimActionForm claimForm;
		String clmApplicationStatus;
		String memberId;
		String bid1;
		ArrayList cgpanList;
		ArrayList payDetailList;
		Connection connection;
		Log.log(4, "ReportsAction", "displayClmApplicationDtlNewRev", "Entered");
		manager = new ReportManager();
		claimForm = (ClaimActionForm) form;
		clmApplicationStatus = claimForm.getClmApplicationStatus();
		clmApplicationStatus = "NE";
		Log.log(4,
				"ReportsAction",
				"displayClmApplicationDtlNewRev",
				(new StringBuilder())
						.append("Claim Application Status being queried :")
						.append(clmApplicationStatus).toString());
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		memberId = (new StringBuilder()).append(bankid).append(zoneid)
				.append(branchid).toString();
		bid1 = null;
		String cgpanDetailsArray[] = null;
		cgpanList = new ArrayList();
		String danDetailsArray[] = null;
		payDetailList = new ArrayList();
		request.setAttribute("CLAIMREFNO",
				request.getParameter("ClaimRefNumber"));
		String ClmRefNo = request.getParameter("ClaimRefNumber");
		Statement stmt = null;
		ResultSet result = null;
		connection = DBConnection.getConnection();
		try {
			String query = (new StringBuilder())
					.append("select bid from ssi_detail  where Bid IN ( SELECT BID FROM claim_detail_temp where clm_ref_no='")
					.append(ClmRefNo).append("')").toString();
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String state[] = null;
			for (; result.next(); System.out.println((new StringBuilder())
					.append("bid1 is ").append(bid1).toString())) {
				state = new String[2];
				bid1 = result.getString(1);
			}

			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		try {
			// System.out.println("functionReturnValue1");
			CallableStatement approvedApps = connection
					.prepareCall("{?=call packgetappldetail.funcgetappldetail(?,?,?)}");
			// System.out.println("functionReturnValue2");
			approvedApps.registerOutParameter(1, 4);
			approvedApps.setString(2, bid1);
			approvedApps.registerOutParameter(3, -10);
			approvedApps.registerOutParameter(4, 12);
			approvedApps.execute();
			int functionReturnValue = approvedApps.getInt(1);
			// System.out.println((new
			// StringBuilder()).append("functionReturnValue").append(functionReturnValue).toString());
			if (functionReturnValue == 1) {
				String error = approvedApps.getString(3);
				approvedApps.close();
				approvedApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			}
			ResultSet cgpanDetStmtsResult = (ResultSet) approvedApps
					.getObject(3);
			// System.out.println((new
			// StringBuilder()).append("functionReturnValue").append(functionReturnValue).toString());

			for (; cgpanDetStmtsResult.next(); cgpanList.add(cgpanDetailsArray)) {
				cgpanDetailsArray = new String[61];
				cgpanDetailsArray[0] = cgpanDetStmtsResult.getString(1);
				cgpanDetailsArray[1] = cgpanDetStmtsResult.getString(2);
				cgpanDetailsArray[2] = cgpanDetStmtsResult.getString(3);
				cgpanDetailsArray[3] = cgpanDetStmtsResult.getString(4);
				cgpanDetailsArray[4] = cgpanDetStmtsResult.getString(5);
				cgpanDetailsArray[5] = cgpanDetStmtsResult.getString(6);
				cgpanDetailsArray[6] = cgpanDetStmtsResult.getString(7);
				cgpanDetailsArray[7] = cgpanDetStmtsResult.getString(8);
				cgpanDetailsArray[8] = cgpanDetStmtsResult.getString(9);
				cgpanDetailsArray[9] = cgpanDetStmtsResult.getString(10);
				cgpanDetailsArray[10] = cgpanDetStmtsResult.getString(11);
				cgpanDetailsArray[11] = cgpanDetStmtsResult.getString(12);
				cgpanDetailsArray[12] = cgpanDetStmtsResult.getString(13);
				cgpanDetailsArray[13] = cgpanDetStmtsResult.getString(14);
				cgpanDetailsArray[14] = cgpanDetStmtsResult.getString(15);
				cgpanDetailsArray[15] = cgpanDetStmtsResult.getString(16);
				cgpanDetailsArray[16] = cgpanDetStmtsResult.getString(17);
				cgpanDetailsArray[17] = cgpanDetStmtsResult.getString(18);
				cgpanDetailsArray[18] = cgpanDetStmtsResult.getString(19);
				cgpanDetailsArray[19] = cgpanDetStmtsResult.getString(20);
				cgpanDetailsArray[20] = cgpanDetStmtsResult.getString(21);
				cgpanDetailsArray[21] = cgpanDetStmtsResult.getString(22);
				cgpanDetailsArray[22] = cgpanDetStmtsResult.getString(23);
				cgpanDetailsArray[23] = cgpanDetStmtsResult.getString(24);
				cgpanDetailsArray[24] = cgpanDetStmtsResult.getString(25);
				cgpanDetailsArray[25] = cgpanDetStmtsResult.getString(26);
				cgpanDetailsArray[26] = cgpanDetStmtsResult.getString(27);
				cgpanDetailsArray[27] = cgpanDetStmtsResult.getString(28);
				cgpanDetailsArray[28] = cgpanDetStmtsResult.getString(29);
				cgpanDetailsArray[29] = cgpanDetStmtsResult.getString(30);
				cgpanDetailsArray[30] = cgpanDetStmtsResult.getString(31);
				cgpanDetailsArray[31] = cgpanDetStmtsResult.getString(32);
				cgpanDetailsArray[32] = cgpanDetStmtsResult.getString(33);
				cgpanDetailsArray[33] = cgpanDetStmtsResult.getString(34);
				cgpanDetailsArray[34] = cgpanDetStmtsResult.getString(35);
				cgpanDetailsArray[35] = cgpanDetStmtsResult.getString(36);
				cgpanDetailsArray[36] = cgpanDetStmtsResult.getString(37);
				cgpanDetailsArray[37] = cgpanDetStmtsResult.getString(38);
				cgpanDetailsArray[38] = cgpanDetStmtsResult.getString(39);
				cgpanDetailsArray[39] = cgpanDetStmtsResult.getString(40);
				cgpanDetailsArray[40] = cgpanDetStmtsResult.getString(41);
				cgpanDetailsArray[41] = cgpanDetStmtsResult.getString(42);
				cgpanDetailsArray[42] = cgpanDetStmtsResult.getString(43);
				cgpanDetailsArray[43] = cgpanDetStmtsResult.getString(44);
				cgpanDetailsArray[44] = cgpanDetStmtsResult.getString(45);
				cgpanDetailsArray[45] = cgpanDetStmtsResult.getString(46);
				cgpanDetailsArray[46] = cgpanDetStmtsResult.getString(47);
				cgpanDetailsArray[47] = cgpanDetStmtsResult.getString(48);
				cgpanDetailsArray[48] = cgpanDetStmtsResult.getString(49);
				cgpanDetailsArray[49] = cgpanDetStmtsResult.getString(50);
				cgpanDetailsArray[50] = cgpanDetStmtsResult.getString(51);
				cgpanDetailsArray[51] = cgpanDetStmtsResult.getString(52);
				cgpanDetailsArray[52] = cgpanDetStmtsResult.getString(53);
				cgpanDetailsArray[53] = cgpanDetStmtsResult.getString(54);
				cgpanDetailsArray[54] = cgpanDetStmtsResult.getString(55);
				cgpanDetailsArray[55] = cgpanDetStmtsResult.getString(56);
				cgpanDetailsArray[56] = cgpanDetStmtsResult.getString(57);
				cgpanDetailsArray[57] = cgpanDetStmtsResult.getString(58);
				cgpanDetailsArray[58] = cgpanDetStmtsResult.getString(59);
				cgpanDetailsArray[59] = cgpanDetStmtsResult.getString(60);
			}

			cgpanDetStmtsResult.close();
			cgpanDetStmtsResult = null;
			approvedApps.close();
			approvedApps = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("cgpanList", cgpanList);
		try {
			CallableStatement payDetailstmt = connection
					.prepareCall("{?=call packgetappldetail.funcgetpaymentdet(?,?,?)}");
			payDetailstmt.registerOutParameter(1, 4);
			payDetailstmt.setString(2, bid1);
			payDetailstmt.registerOutParameter(3, -10);
			payDetailstmt.registerOutParameter(4, 12);
			payDetailstmt.execute();
			int functionReturnValue = payDetailstmt.getInt(1);
			if (functionReturnValue == 1) {
				String error = payDetailstmt.getString(3);
				payDetailstmt.close();
				payDetailstmt = null;
				connection.rollback();
				throw new DatabaseException(error);
			}
			ResultSet paymentDetailStmtsResult;

			for (paymentDetailStmtsResult = (ResultSet) payDetailstmt
					.getObject(3); paymentDetailStmtsResult.next(); payDetailList
					.add(danDetailsArray)) {
				danDetailsArray = new String[13];
				danDetailsArray[0] = paymentDetailStmtsResult.getString(1);
				danDetailsArray[1] = paymentDetailStmtsResult.getString(2);
				danDetailsArray[2] = paymentDetailStmtsResult.getString(3);
				danDetailsArray[3] = paymentDetailStmtsResult.getString(4);
				danDetailsArray[4] = paymentDetailStmtsResult.getString(5);
				danDetailsArray[5] = paymentDetailStmtsResult.getString(6);
				danDetailsArray[6] = paymentDetailStmtsResult.getString(7);
				danDetailsArray[7] = paymentDetailStmtsResult.getString(8);
				danDetailsArray[8] = paymentDetailStmtsResult.getString(9);
				danDetailsArray[9] = paymentDetailStmtsResult.getString(10);
				danDetailsArray[10] = paymentDetailStmtsResult.getString(11);
				danDetailsArray[11] = paymentDetailStmtsResult.getString(12);
			}

			paymentDetailStmtsResult.close();
			paymentDetailStmtsResult = null;
			payDetailstmt.close();
			payDetailstmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("payDetailList", payDetailList);
		ClaimsProcessor processor = new ClaimsProcessor();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		ClaimApplication claimapplication = manager.displayClmRefNumberDtl(
				claimRefNumber, clmApplicationStatus, memberId);
		ArrayList clmSummryDtls = claimapplication.getClaimSummaryDtls();
		User userInfo = getUserInformation(request);
		if (claimapplication.getFirstInstallment()) {
			String thiscgpn = null;
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			// Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
			// memId);//commented because cgpnDetails not used by this block
			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimForm.setClaimapplication(claimapplication);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showFirstClmDetails");
		}
		if (claimapplication.getSecondInstallment()) {
			claimForm.setClaimapplication(claimapplication);
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(bid, "F");
			Vector updateClmDtls = new Vector();
			String thiscgpn = null;
			for (int i = 0; i < cgpnDetails.size(); i++) {
				HashMap dtl = (HashMap) cgpnDetails.elementAt(i);
				if (dtl != null) {
					thiscgpn = (String) dtl.get("CGPAN");
					if (thiscgpn != null) {
						for (int j = 0; j < clmAppliedAmnts.size(); j++) {
							HashMap clmAppliedDtl = (HashMap) clmAppliedAmnts
									.elementAt(j);
							String cgpnInAppliedAmntsVec = null;
							if (clmAppliedDtl == null)
								continue;
							cgpnInAppliedAmntsVec = (String) clmAppliedDtl
									.get("CGPAN");
							if (cgpnInAppliedAmntsVec == null
									|| !cgpnInAppliedAmntsVec.equals(thiscgpn))
								continue;
							double clmAppliedAmnt = 0.0D;
							Double clmAppAmntObj = (Double) clmAppliedDtl
									.get("ClaimAppliedAmnt");
							if (clmAppAmntObj != null)
								clmAppliedAmnt = clmAppAmntObj.doubleValue();
							else
								clmAppliedAmnt = 0.0D;
							dtl.put("ClaimAppliedAmnt", new Double(
									clmAppliedAmnt));
							if (!updateClmDtls.contains(dtl))
								updateClmDtls.addElement(dtl);
							clmAppliedDtl = null;
							break;
						}

						dtl = null;
					}
				}
			}

			HashMap settlmntDetails = processor
					.getClaimSettlementDetailForBorrower(bid);
			double firstSettlementAmnt = 0.0D;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get("FirstSettlmntAmnt");
			if (firstSettlementAmntObj != null)
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			Date firstSettlementDt = (Date) settlmntDetails
					.get("FirstSettlmntDt");
			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				if (dtl != null) {
					dtl.put("FirstSettlmntAmnt",
							new Double(firstSettlementAmnt));
					dtl.put("FirstSettlmntDt", firstSettlementDt);
					if (!finalUpdatedDtls.contains(dtl))
						finalUpdatedDtls.addElement(dtl);
					dtl = null;
				}
			}

			ArrayList clmSummaryDtls = claimapplication.getClaimSummaryDtls();
			for (int j = 0; j < clmSummaryDtls.size(); j++) {
				ClaimSummaryDtls dtls = (ClaimSummaryDtls) clmSummaryDtls
						.get(j);
				String cgpan = null;
				double clmappliedamnt = 0.0D;
				if (dtls != null) {
					cgpan = dtls.getCgpan();
					clmappliedamnt = dtls.getAmount();
				}
				for (int i = 0; i < updateClmDtls.size(); i++) {
					dtl = (HashMap) updateClmDtls.elementAt(i);
					if (dtl != null) {
						String pan = (String) dtl.get("CGPAN");
						if (pan.equals(cgpan)) {
							dtl = (HashMap) updateClmDtls.remove(i);
							dtl.put("FirstSettlmntAmnt", new Double(
									firstSettlementAmnt));
							dtl.put("FirstSettlmntDt", firstSettlementDt);
							dtl.put("SECClaimAppliedAmnt", new Double(
									clmappliedamnt));
						}
						dtl = null;
					}
				}

			}

			claimForm.setUpdatedClaimDtls(finalUpdatedDtls);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showSecClmDetails");
		} else {
			Log.log(4, "ReportsAction", "displayClmApplicationDtlNewRev",
					"Exited");
			return null;
		}
	}

	public ActionForward claimRefNoClaimDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "claimRefNoClaimDetail", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(4, "ReportsAction", "claimRefNoClaimDetail", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward displayClmRefNumberDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportManager manager;
		ClaimActionForm claimForm;
		String clmApplicationStatus;
		String memberId;
		String cgpanno;
		String claimRefNumber = "";
		Connection connection;
		Log.log(4, "ReportsAction", "displayClmRefNumberDtl", "Entered");
		manager = new ReportManager();
		claimForm = (ClaimActionForm) form;
		clmApplicationStatus = "";
		Log.log(4,
				"ReportsAction",
				"displayClmRefNumberDtl",
				(new StringBuilder())
						.append("Claim Application Status being queried :")
						.append(clmApplicationStatus).toString());
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		memberId = (new StringBuilder()).append(bankid).append(zoneid)
				.append(branchid).toString();
		request.setAttribute("CLAIMREFNO", request.getParameter("clmRefNumber"));
		cgpanno = request.getParameter("cgpan");
		claimRefNumber = request.getParameter("clmRefNumber");

		PreparedStatement pstmt = null;
		ResultSet rst = null;
		String clmRefNo = "";

		if (GenericValidator.isBlankOrNull(cgpanno)
				&& GenericValidator.isBlankOrNull(claimRefNumber)) {
			throw new MessageException(
					"Please enter either cgpan or claim reference number.");
		}

		// if(cgpanno != null || !cgpanno.equals(""))
		if (!GenericValidator.isBlankOrNull(cgpanno)) {
			connection = DBConnection.getConnection();
			try {
				String query = "select distinct clm_ref_no,clm_status \nfrom claim_detail_temp ct,application_detail a,ssi_detail s\nwhere ct.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand a.cgpan = ?\nunion all\nselect distinct clm_ref_no,clm_status \nfrom claim_detail c,application_detail a,ssi_detail s\nwhere c.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand a.cgpan = ? ";
				// System.out.println("Query :"+query);
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, cgpanno);
				pstmt.setString(2, cgpanno);

				for (rst = pstmt.executeQuery(); rst.next();) {
					claimRefNumber = rst.getString(1);
					clmApplicationStatus = rst.getString(2);
				}
				// System.out.println("displayClmRefNumberDetail "+claimRefNumber);

				if (clmApplicationStatus.equals(""))
					throw new DatabaseException("Enter a valid cgpan.");
				rst.close();
				rst = null;
				pstmt.close();
				pstmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		} else
		// if(claimRefNumber != null || !"".equals(claimRefNumber))
		if (!GenericValidator.isBlankOrNull(claimRefNumber)) {
			pstmt = null;
			rst = null;
			clmRefNo = "";
			connection = DBConnection.getConnection();
			try {
				String query = "select distinct clm_status \nfrom claim_detail_temp ct,application_detail a,ssi_detail s\nwhere ct.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand ct.clm_ref_no = ?\nunion all\nselect distinct clm_status \nfrom claim_detail c,application_detail a,ssi_detail s\nwhere c.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand c.clm_ref_no = ? ";
				// System.out.println("Query:"+query);
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, claimRefNumber);
				pstmt.setString(2, claimRefNumber);

				for (rst = pstmt.executeQuery(); rst.next();)
					clmApplicationStatus = rst.getString(1);

				if (clmApplicationStatus.equals(""))
					throw new DatabaseException("Enter a valid Claim Ref No.");
				rst.close();
				rst = null;
				pstmt.close();
				pstmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		}
		if ("AP".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Approved");
		else if ("RE".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Rejected");
		else if ("NE".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "NEW");
		else if ("HO".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Hold");
		else if ("FW".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Forwarded");
		else if ("TC".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Temporary Closed");
		else if ("TR".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Temporary Rejected");
		else if ("RR".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Reply Received");
		else if ("RT".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Retruned");
		else if ("WD".equals(clmApplicationStatus))
			request.setAttribute("radioValue", "Claim Withdrawn");

		// System.out.println((new
		// StringBuilder()).append("clmApplicationStatus:").append(clmApplicationStatus).toString());
		ClaimsProcessor processor = new ClaimsProcessor();
		ClaimApplication claimapplication = manager.displayClmRefNumberDtl(
				claimRefNumber, clmApplicationStatus, memberId);
		// System.out.println("ClaimApplication"+claimapplication);
		ArrayList clmSummryDtls = claimapplication.getClaimSummaryDtls();
		// System.out.println("13063");
		User userInfo = getUserInformation(request);
		if (claimapplication.getFirstInstallment()) {
			String thiscgpn = null;
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			// Vector cgpnDetails = processor.getCGPANDetailsForBidClaim(bid,
			// memId);

			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimForm.setClaimapplication(claimapplication);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showFirstClmDetails");
		}
		if (claimapplication.getSecondInstallment()) {
			claimForm.setClaimapplication(claimapplication);
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(bid, "F");
			Vector updateClmDtls = new Vector();
			String thiscgpn = null;
			for (int i = 0; i < cgpnDetails.size(); i++) {
				HashMap dtl = (HashMap) cgpnDetails.elementAt(i);
				if (dtl != null) {
					thiscgpn = (String) dtl.get("CGPAN");
					if (thiscgpn != null) {
						for (int j = 0; j < clmAppliedAmnts.size(); j++) {
							HashMap clmAppliedDtl = (HashMap) clmAppliedAmnts
									.elementAt(j);
							String cgpnInAppliedAmntsVec = null;
							if (clmAppliedDtl == null)
								continue;
							cgpnInAppliedAmntsVec = (String) clmAppliedDtl
									.get("CGPAN");
							if (cgpnInAppliedAmntsVec == null
									|| !cgpnInAppliedAmntsVec.equals(thiscgpn))
								continue;
							double clmAppliedAmnt = 0.0D;
							Double clmAppAmntObj = (Double) clmAppliedDtl
									.get("ClaimAppliedAmnt");
							if (clmAppAmntObj != null)
								clmAppliedAmnt = clmAppAmntObj.doubleValue();
							else
								clmAppliedAmnt = 0.0D;
							dtl.put("ClaimAppliedAmnt", new Double(
									clmAppliedAmnt));
							if (!updateClmDtls.contains(dtl))
								updateClmDtls.addElement(dtl);
							clmAppliedDtl = null;
							break;
						}

						dtl = null;
					}
				}
			}

			HashMap settlmntDetails = processor
					.getClaimSettlementDetailForBorrower(bid);
			double firstSettlementAmnt = 0.0D;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get("FirstSettlmntAmnt");
			if (firstSettlementAmntObj != null)
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			Date firstSettlementDt = (Date) settlmntDetails
					.get("FirstSettlmntDt");
			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				if (dtl != null) {
					dtl.put("FirstSettlmntAmnt",
							new Double(firstSettlementAmnt));
					dtl.put("FirstSettlmntDt", firstSettlementDt);
					if (!finalUpdatedDtls.contains(dtl))
						finalUpdatedDtls.addElement(dtl);
					dtl = null;
				}
			}

			ArrayList clmSummaryDtls = claimapplication.getClaimSummaryDtls();
			for (int j = 0; j < clmSummaryDtls.size(); j++) {
				ClaimSummaryDtls dtls = (ClaimSummaryDtls) clmSummaryDtls
						.get(j);
				String cgpan = null;
				double clmappliedamnt = 0.0D;
				if (dtls != null) {
					cgpan = dtls.getCgpan();
					clmappliedamnt = dtls.getAmount();
				}
				for (int i = 0; i < updateClmDtls.size(); i++) {
					dtl = (HashMap) updateClmDtls.elementAt(i);
					if (dtl != null) {
						String pan = (String) dtl.get("CGPAN");
						if (pan.equals(cgpan)) {
							dtl = (HashMap) updateClmDtls.remove(i);
							dtl.put("FirstSettlmntAmnt", new Double(
									firstSettlementAmnt));
							dtl.put("FirstSettlmntDt", firstSettlementDt);
							dtl.put("SECClaimAppliedAmnt", new Double(
									clmappliedamnt));
						}
						dtl = null;
					}
				}

			}

			claimForm.setUpdatedClaimDtls(finalUpdatedDtls);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showSecClmDetails");
		} else {
			Log.log(4, "ReportsAction", "displayClmRefNumberDtl", "Exited");
			return null;
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
	public ActionForward displayClmApplicationDtlModified(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("test1");
		ReportManager manager;
		ClaimActionForm claimForm;
		String clmApplicationStatus;
		String memberId;
		String bid1;
		ArrayList cgpanList;
		ArrayList payDetailList;
		Connection connection;
		Log.log(4, "ReportsAction", "displayClmApplicationDtlModified",
				"Entered");
		manager = new ReportManager();
		claimForm = (ClaimActionForm) form;
		clmApplicationStatus = claimForm.getClmApplicationStatus();
		clmApplicationStatus = "NE";
		Log.log(4,
				"ReportsAction",
				"displayClmApplicationDtlNew",
				(new StringBuilder())
						.append("Claim Application Status being queried :")
						.append(clmApplicationStatus).toString());
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		memberId = (new StringBuilder()).append(bankid).append(zoneid)
				.append(branchid).toString();
		request.setAttribute("CLAIMREFNO",
				request.getParameter("ClaimRefNumber"));
		bid1 = null;
		String cgpanDetailsArray[] = null;
		cgpanList = new ArrayList();
		String danDetailsArray[] = null;
		payDetailList = new ArrayList();
		request.setAttribute("CLAIMREFNO",
				request.getParameter("ClaimRefNumber"));
		String ClmRefNo = request.getParameter("ClaimRefNumber");
		Statement stmt = null;
		ResultSet result = null;
		connection = DBConnection.getConnection();
		try {
			String query = (new StringBuilder())
					.append("select bid from ssi_detail  where Bid IN ( SELECT BID FROM claim_detail_temp where clm_ref_no='")
					.append(ClmRefNo).append("')").toString();
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String state[] = null;
			for (; result.next(); System.out.println((new StringBuilder())
					.append("bid1 is ").append(bid1).toString())) {
				state = new String[2];
				bid1 = result.getString(1);
			}

			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		try {
			// System.out.println("functionReturnValue1");
			CallableStatement approvedApps = connection
					.prepareCall("{?=call packgetappldetail.funcgetappldetail(?,?,?)}");
			// System.out.println("functionReturnValue2");
			approvedApps.registerOutParameter(1, 4);
			approvedApps.setString(2, bid1);
			approvedApps.registerOutParameter(3, -10);
			approvedApps.registerOutParameter(4, 12);
			approvedApps.execute();
			int functionReturnValue = approvedApps.getInt(1);
			// System.out.println((new
			// StringBuilder()).append("functionReturnValue").append(functionReturnValue).toString());
			if (functionReturnValue == 1) {
				String error = approvedApps.getString(3);
				approvedApps.close();
				approvedApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			}
			ResultSet cgpanDetStmtsResult;
			// String cgpanDetailsArray[];
			for (cgpanDetStmtsResult = (ResultSet) approvedApps.getObject(3); 
			cgpanDetStmtsResult.next();
			cgpanList.add(cgpanDetailsArray)) {
				cgpanDetailsArray = new String[62];
				cgpanDetailsArray[0] = cgpanDetStmtsResult.getString(1);
				cgpanDetailsArray[1] = cgpanDetStmtsResult.getString(2);
				cgpanDetailsArray[2] = cgpanDetStmtsResult.getString(3);
				cgpanDetailsArray[3] = cgpanDetStmtsResult.getString(4);
				cgpanDetailsArray[4] = cgpanDetStmtsResult.getString(5);
				cgpanDetailsArray[5] = cgpanDetStmtsResult.getString(6);
				cgpanDetailsArray[6] = cgpanDetStmtsResult.getString(7);
				cgpanDetailsArray[7] = cgpanDetStmtsResult.getString(8);
				cgpanDetailsArray[8] = cgpanDetStmtsResult.getString(9);
				cgpanDetailsArray[9] = cgpanDetStmtsResult.getString(10);
				cgpanDetailsArray[10] = cgpanDetStmtsResult.getString(11);
				cgpanDetailsArray[11] = cgpanDetStmtsResult.getString(12);
				cgpanDetailsArray[12] = cgpanDetStmtsResult.getString(13);
				cgpanDetailsArray[13] = cgpanDetStmtsResult.getString(14);
				cgpanDetailsArray[14] = cgpanDetStmtsResult.getString(15);
				cgpanDetailsArray[15] = cgpanDetStmtsResult.getString(16);
				cgpanDetailsArray[16] = cgpanDetStmtsResult.getString(17);
				cgpanDetailsArray[17] = cgpanDetStmtsResult.getString(18);
				cgpanDetailsArray[18] = cgpanDetStmtsResult.getString(19);
				cgpanDetailsArray[19] = cgpanDetStmtsResult.getString(20);
				cgpanDetailsArray[20] = cgpanDetStmtsResult.getString(21);
				cgpanDetailsArray[21] = cgpanDetStmtsResult.getString(22);
				cgpanDetailsArray[22] = cgpanDetStmtsResult.getString(23);
				cgpanDetailsArray[23] = cgpanDetStmtsResult.getString(24);
				cgpanDetailsArray[24] = cgpanDetStmtsResult.getString(25);
				cgpanDetailsArray[25] = cgpanDetStmtsResult.getString(26);
				cgpanDetailsArray[26] = cgpanDetStmtsResult.getString(27);
				cgpanDetailsArray[27] = cgpanDetStmtsResult.getString(28);
				cgpanDetailsArray[28] = cgpanDetStmtsResult.getString(29);
				cgpanDetailsArray[29] = cgpanDetStmtsResult.getString(30);
				cgpanDetailsArray[30] = cgpanDetStmtsResult.getString(31);
				cgpanDetailsArray[31] = cgpanDetStmtsResult.getString(32);
				cgpanDetailsArray[32] = cgpanDetStmtsResult.getString(33);
				cgpanDetailsArray[33] = cgpanDetStmtsResult.getString(34);
				cgpanDetailsArray[34] = cgpanDetStmtsResult.getString(35);
				cgpanDetailsArray[35] = cgpanDetStmtsResult.getString(36);
				cgpanDetailsArray[36] = cgpanDetStmtsResult.getString(37);
				cgpanDetailsArray[37] = cgpanDetStmtsResult.getString(38);
				cgpanDetailsArray[38] = cgpanDetStmtsResult.getString(39);
				cgpanDetailsArray[39] = cgpanDetStmtsResult.getString(40);
				cgpanDetailsArray[40] = cgpanDetStmtsResult.getString(41);
				cgpanDetailsArray[41] = cgpanDetStmtsResult.getString(42);
				cgpanDetailsArray[42] = cgpanDetStmtsResult.getString(43);
				cgpanDetailsArray[43] = cgpanDetStmtsResult.getString(44);
				cgpanDetailsArray[44] = cgpanDetStmtsResult.getString(45);
				cgpanDetailsArray[45] = cgpanDetStmtsResult.getString(46);
				cgpanDetailsArray[46] = cgpanDetStmtsResult.getString(47);
				cgpanDetailsArray[47] = cgpanDetStmtsResult.getString(48);
				cgpanDetailsArray[48] = cgpanDetStmtsResult.getString(49);
				cgpanDetailsArray[49] = cgpanDetStmtsResult.getString(50);
				cgpanDetailsArray[50] = cgpanDetStmtsResult.getString(51);
				cgpanDetailsArray[51] = cgpanDetStmtsResult.getString(52);
				cgpanDetailsArray[52] = cgpanDetStmtsResult.getString(53);
				cgpanDetailsArray[53] = cgpanDetStmtsResult.getString(54);
				cgpanDetailsArray[54] = cgpanDetStmtsResult.getString(55);
				cgpanDetailsArray[55] = cgpanDetStmtsResult.getString(56);
				cgpanDetailsArray[56] = cgpanDetStmtsResult.getString(57);
				cgpanDetailsArray[57] = cgpanDetStmtsResult.getString(58);
				cgpanDetailsArray[58] = cgpanDetStmtsResult.getString(59);
				cgpanDetailsArray[59] = cgpanDetStmtsResult.getString(60);
				cgpanDetailsArray[60] = cgpanDetStmtsResult.getString(61);
				
				System.out.println("===="+cgpanDetStmtsResult.getString(61));

			}

			cgpanDetStmtsResult.close();
			cgpanDetStmtsResult = null;
			approvedApps.close();
			approvedApps = null;
			
			
			
      		
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("cgpanList", cgpanList);

		// System.out.println("test11");

		// kot
		try {
			String query = " select cgpan, to_char(ntd_first_disbursement_dt,'dd/mm/yyyy'),to_char(ntd_first_instalment_dt,'dd/mm/yyyy'),ntd_principal_moratorium,ntd_interest_moratorium "
					+ "from npa_tc_detail_temp where cgpan in (select cgpan from application_Detail where ssi_reference_number"
					+ " in (select ssi_reference_number from ssi_detail where bid='"
					+ bid1 + "' ))";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String npaTcDetails[] = null;

			ArrayList npaTcArray = new ArrayList();
			while (result.next()) {
				npaTcDetails = new String[5];
				npaTcDetails[0] = result.getString(1);
				npaTcDetails[1] = result.getString(2);
				npaTcDetails[2] = result.getString(3);
				npaTcDetails[3] = result.getString(4);
				npaTcDetails[4] = result.getString(5);

				npaTcArray.add(npaTcDetails);
			}

			request.setAttribute("npaTcArray", npaTcArray);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		// kot
		//RAJ
		try {
			String query = "SELECT  m.mem_bank_name,a.CGPAN," 
				+" PMR_CHIEF_IT_PAN,"
				+"SSI_CONSTITUTION, "
				+"S.SSI_UNIT_NAME, "
				+"A.APP_APPROVED_AMOUNT, "
				+"S.SSI_MICRO_ENTERPRISE AS MICRO, "
				+"P.PMR_CHIEF_GENDER as GENDER, "
				+"CASE "
				+"  WHEN  m.MEM_STATE_NAME  in (select SCR_STATE_NAME from state_gr_map)  " 
				+"         AND a.CGPAN IS NOT NULL  "
				+"  THEN  "
				+"     'Y'  "
				+"     ELSE 'N'  "
				+" END as NER,  "
				+" nvl(( select ct.clm_ref_no from claim_detail_temp ct where ct.bid=s.bid ),( select c.clm_ref_no from claim_detail c where c.bid=s.bid )) as clm_ref_no, "
				+" nvl(( select ct.CLM_STATUS from claim_detail_temp ct where ct.bid=s.bid ),( select c.CLM_STATUS from claim_detail c where c.bid=s.bid )) as CLAIM_STATUS, "
				+" nvl(( select ct.CLM_APPROVED_AMT from claim_detail_temp ct where ct.bid=s.bid ),( select c.CLM_APPROVED_AMT from claim_detail c where c.bid=s.bid )) as CLM_APPROVED_AMT, "
				+" nvl(nvl(( select ct.CTC_CLM_SETTLED_PERCENT from claim_tc_detail ct where ct.cgpan=a.cgpan ),( select ct.CTC_CLM_SETTLED_PERCENT from claim_tc_detail_temp ct where ct.cgpan=a.cgpan )), "
				+" nvl(( select cd.CWC_CLM_SETTLED_PERCENT from claim_wc_detail cd where cd.cgpan=a.cgpan ),( select cd.CWC_CLM_SETTLED_PERCENT from claim_wc_detail_temp cd where cd.cgpan=a.cgpan ))) as CLM_SETTLED_PERCENT, "
				+" trunc(a.APP_SANCTION_DT)"
				+" FROM application_detail a, "
				+" ssi_detail s,  "
				+" PROMOTER_DETAIL p, "
				+" member_info m "
				+" WHERE     A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER "
				+"  AND A.SSI_REFERENCE_NUMBER = p.SSI_REFERENCE_NUMBER "
				+" AND A.app_status not in ('RE') "
				+" AND a.MEM_BNK_ID || a.MEM_ZNE_ID || a.MEM_BRN_ID =m.MEM_BNK_ID || m.MEM_ZNE_ID || m.MEM_BRN_ID "
				+" AND P.PMR_CHIEF_IT_PAN IN (SELECT PMR_CHIEF_IT_PAN FROM application_detail a, ssi_detail s, PROMOTER_DETAIL p   WHERE     A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER "
				+" AND A.SSI_REFERENCE_NUMBER = p.SSI_REFERENCE_NUMBER "
				+" AND a.cgpan IN(SELECT cgpan FROM application_Detail aa, ssi_detail ss WHERE     aa.ssi_reference_number =ss.ssi_reference_number AND  bid='"+ bid1 + "' )) "
				+" AND S.SSI_CONSTITUTION IN(SELECT S.SSI_CONSTITUTION FROM application_detail a, ssi_detail s, PROMOTER_DETAIL p  WHERE     A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER  "
				+" AND A.SSI_REFERENCE_NUMBER = p.SSI_REFERENCE_NUMBER "
				+" AND a.cgpan IN(SELECT cgpan FROM application_Detail aa, ssi_detail ss  WHERE     aa.ssi_reference_number =ss.ssi_reference_number AND  bid='"+ bid1 + "')) ";

				
		    System.out.println("query==="+query);
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String iTpanDetails[] = null;

			ArrayList iTpanArray = new ArrayList();
			while (result.next()) {
				iTpanDetails = new String[14];
				iTpanDetails[0] = result.getString(1);
				iTpanDetails[1] = result.getString(2);
				iTpanDetails[2] = result.getString(3);
				iTpanDetails[3] = result.getString(4);
				iTpanDetails[4] = result.getString(5);
				iTpanDetails[5] = result.getString(6);
				iTpanDetails[6] = result.getString(7);
				iTpanDetails[7] = result.getString(8);
				iTpanDetails[8] = result.getString(9);
				iTpanDetails[9] = result.getString(10);
				iTpanDetails[10] = result.getString(11);
				iTpanDetails[11] = result.getString(12);
				iTpanDetails[12] = result.getString(13);
				iTpanDetails[13] = result.getString(14);
				iTpanArray.add(iTpanDetails);
			}

			request.setAttribute("iTpanArray", iTpanArray);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}

		//ENDRAJ

		try {
			// System.out.println("paymentDetailStmtsResult1");
			CallableStatement payDetailstmt = connection
					.prepareCall("{?=call packgetappldetail.funcgetpaymentdet(?,?,?)}");
			// System.out.println("paymentDetailStmtsResult2");
			payDetailstmt.registerOutParameter(1, 4);
			payDetailstmt.setString(2, bid1);
			payDetailstmt.registerOutParameter(3, -10);
			payDetailstmt.registerOutParameter(4, 12);
			payDetailstmt.execute();
			int functionReturnValue = payDetailstmt.getInt(1);
			if (functionReturnValue == 1) {
				String error = payDetailstmt.getString(3);
				payDetailstmt.close();
				payDetailstmt = null;
				connection.rollback();
				throw new DatabaseException(error);
			}
			ResultSet paymentDetailStmtsResult;
			// String danDetailsArray[];
			for (paymentDetailStmtsResult = (ResultSet) payDetailstmt
					.getObject(3); paymentDetailStmtsResult.next(); payDetailList
					.add(danDetailsArray)) {
				danDetailsArray = new String[13];
				danDetailsArray[0] = paymentDetailStmtsResult.getString(1);
				danDetailsArray[1] = paymentDetailStmtsResult.getString(2);
				danDetailsArray[2] = paymentDetailStmtsResult.getString(3);
				danDetailsArray[3] = paymentDetailStmtsResult.getString(4);
				danDetailsArray[4] = paymentDetailStmtsResult.getString(5);
				danDetailsArray[5] = paymentDetailStmtsResult.getString(6);
				danDetailsArray[6] = paymentDetailStmtsResult.getString(7);
				danDetailsArray[7] = paymentDetailStmtsResult.getString(8);
				danDetailsArray[8] = paymentDetailStmtsResult.getString(9);
				danDetailsArray[9] = paymentDetailStmtsResult.getString(10);
				danDetailsArray[10] = paymentDetailStmtsResult.getString(11);
				danDetailsArray[11] = paymentDetailStmtsResult.getString(12);
			}

			paymentDetailStmtsResult.close();
			paymentDetailStmtsResult = null;
			payDetailstmt.close();
			payDetailstmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("payDetailList", payDetailList);
		ClaimsProcessor processor = new ClaimsProcessor();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		// System.out.println("test12");

		// ClaimApplication claimapplication =
		// manager.displayClmRefNumberDtl(claimRefNumber, clmApplicationStatus,
		// memberId);
		ClaimApplication claimapplication = manager.displayClmRefNumberDtlNew(
				claimRefNumber, clmApplicationStatus, memberId);
		// System.out.println("test13");
		ArrayList clmSummryDtls = claimapplication.getClaimSummaryDtls();
		User userInfo = getUserInformation(request);
		if (claimapplication.getFirstInstallment()) {

			String thiscgpn = null;
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			// Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
			// memId);
			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimForm.setClaimapplication(claimapplication);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",	formattedToOSPath);
			}
			// System.out.println("test14");
			return mapping.findForward("showFirstClmDetails");
		}
		if (claimapplication.getSecondInstallment()) {
			claimForm.setClaimapplication(claimapplication);
			String bid = claimapplication.getBorrowerId();
			String memId = claimapplication.getMemberId();
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(bid,
					memId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(bid, "F");
			Vector updateClmDtls = new Vector();
			String thiscgpn = null;
			for (int i = 0; i < cgpnDetails.size(); i++) {
				HashMap dtl = (HashMap) cgpnDetails.elementAt(i);
				if (dtl != null) {
					thiscgpn = (String) dtl.get("CGPAN");
					if (thiscgpn != null) {
						for (int j = 0; j < clmAppliedAmnts.size(); j++) {
							HashMap clmAppliedDtl = (HashMap) clmAppliedAmnts
									.elementAt(j);
							String cgpnInAppliedAmntsVec = null;
							if (clmAppliedDtl == null)
								continue;
							cgpnInAppliedAmntsVec = (String) clmAppliedDtl
									.get("CGPAN");
							if (cgpnInAppliedAmntsVec == null
									|| !cgpnInAppliedAmntsVec.equals(thiscgpn))
								continue;
							double clmAppliedAmnt = 0.0D;
							Double clmAppAmntObj = (Double) clmAppliedDtl
									.get("ClaimAppliedAmnt");
							if (clmAppAmntObj != null)
								clmAppliedAmnt = clmAppAmntObj.doubleValue();
							else
								clmAppliedAmnt = 0.0D;
							dtl.put("ClaimAppliedAmnt", new Double(
									clmAppliedAmnt));
							if (!updateClmDtls.contains(dtl))
								updateClmDtls.addElement(dtl);
							clmAppliedDtl = null;
							break;
						}

						dtl = null;
					}
				}
			}

			HashMap settlmntDetails = processor
					.getClaimSettlementDetailForBorrower(bid);
			double firstSettlementAmnt = 0.0D;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get("FirstSettlmntAmnt");
			if (firstSettlementAmntObj != null)
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			Date firstSettlementDt = (Date) settlmntDetails
					.get("FirstSettlmntDt");
			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				if (dtl != null) {
					dtl.put("FirstSettlmntAmnt",
							new Double(firstSettlementAmnt));
					dtl.put("FirstSettlmntDt", firstSettlementDt);
					if (!finalUpdatedDtls.contains(dtl))
						finalUpdatedDtls.addElement(dtl);
					dtl = null;
				}
			}

			ArrayList clmSummaryDtls = claimapplication.getClaimSummaryDtls();
			for (int j = 0; j < clmSummaryDtls.size(); j++) {
				ClaimSummaryDtls dtls = (ClaimSummaryDtls) clmSummaryDtls
						.get(j);
				String cgpan = null;
				double clmappliedamnt = 0.0D;
				if (dtls != null) {
					cgpan = dtls.getCgpan();
					clmappliedamnt = dtls.getAmount();
				}
				for (int i = 0; i < updateClmDtls.size(); i++) {
					dtl = (HashMap) updateClmDtls.elementAt(i);
					if (dtl != null) {
						String pan = (String) dtl.get("CGPAN");
						if (pan.equals(cgpan)) {
							dtl = (HashMap) updateClmDtls.remove(i);
							dtl.put("FirstSettlmntAmnt", new Double(
									firstSettlementAmnt));
							dtl.put("FirstSettlmntDt", firstSettlementDt);
							dtl.put("SECClaimAppliedAmnt", new Double(
									clmappliedamnt));
						}
						dtl = null;
					}
				}

			}

			claimForm.setUpdatedClaimDtls(finalUpdatedDtls);
			boolean internetUser = true;
			if ((new StringBuilder()).append(userInfo.getBankId())
					.append(userInfo.getZoneId())
					.append(userInfo.getBranchId()).toString()
					.equals("000000000000")
					&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
				internetUser = false;
			Map attachments = manager.getClaimAttachments(claimRefNumber,
					clmApplicationStatus, internetUser);
			if (attachments.get("recallNotice") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("recallNotice");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Recall Notice Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("recallNoticeAttachment",
						formattedToOSPath);
			}
			if (attachments.get("legalDetails") != null) {
				UploadFileProperties uploadFile = (UploadFileProperties) attachments
						.get("legalDetails");
				String formattedToOSPath = createNewFile(request,
						uploadFile.getFileName(), uploadFile.getFileSize());
				Log.log(5,
						"ReportsAction",
						"createNewFile",
						(new StringBuilder())
								.append(" Legal Details Attachment path ")
								.append(formattedToOSPath).toString());
				request.setAttribute("legalDetailsAttachment",
						formattedToOSPath);
			}
			return mapping.findForward("showSecClmDetails");
		} else {
			Log.log(4, "ReportsAction", "displayClmApplicationDtlModified",
					"Exited");
			return null;
		}

	}

	public ActionForward showFilterForSettlementReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		CustomisedDate customDate = new CustomisedDate();
		customDate.setDate(prevDate);

		CustomisedDate customToDate = new CustomisedDate();
		customToDate.setDate(date);

		claimForm.setFromDate(customDate);
		claimForm.setToDate(customToDate);
		return mapping.findForward("showFilter");
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
	public ActionForward declarationReceivedReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "declarationReceivedReport",
				"Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		dynaForm.set("memberId", "");
		dynaForm.set("ssi", "");
		BeanUtils.copyProperties(dynaForm, generalReport);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		dynaForm.set("bankId", bankId);
		// System.out.println("bankId:"+bankId);
		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			memberId = "";
		}
		dynaForm.set("memberId", memberId);

		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "declarationReceivedReport",
				"Exited");
		return mapping.findForward("showFilter");
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
	public ActionForward declarationReceivedReportDetails(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "declarationReceivedReportDetails",
				"Entered");
		ArrayList declarationReceivedCases = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());

		String id = (String) dynaForm.get("memberId");
		// System.out.println("id:"+id);
		String ssi = (String) dynaForm.get("ssi");
		// System.out.println("ssi:"+ssi);

		ClaimsProcessor processor = new ClaimsProcessor();
		Vector memberids = processor.getAllMemberIds();
		if (((id == null) || (id.equals("")))
				&& ((ssi == null) || (ssi.equals("")))) {
			declarationReceivedCases = reportManager.declarationReceivedCases(
					startDate, endDate);
			dynaForm.set("danRaised", declarationReceivedCases);
		} else if (((ssi == null) || (ssi.equals(""))) && (id != null)) {
			if (!(memberids.contains(id))) {
				throw new NoMemberFoundException("Member Id :" + id
						+ " does not exist in the database.");
			}

			declarationReceivedCases = reportManager
					.declarationReceivedCasesNew(startDate, endDate, id, ssi);
			dynaForm.set("danRaised", declarationReceivedCases);
		} else {
			declarationReceivedCases = reportManager
					.declarationReceivedCasesNew(startDate, endDate, id, ssi);
			dynaForm.set("danRaised", declarationReceivedCases);
		}

		if (declarationReceivedCases == null
				|| declarationReceivedCases.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			declarationReceivedCases = null;
			Log.log(Log.INFO, "ReportsAction",
					"declarationReceivedReportDetails", "Exited");
			return mapping.findForward("success");
		}

	}

	public ActionForward displayMemberSettlementDtls(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "displayMemberSettlementDtls",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		java.util.Date fromDate = (java.util.Date) claimForm.getFromDate();
		java.util.Date toDate = (java.util.Date) claimForm.getToDate();
		Vector settlementDetails = null;

		ReportManager manager = new ReportManager();
		java.sql.Date sqlFromDate = null;

		if ((fromDate.toString()).equals(""))
			settlementDetails = manager.displayMemberSettlementDtls(null,
					new java.sql.Date(toDate.getTime()));
		else {
			sqlFromDate = new java.sql.Date(fromDate.getTime());
			settlementDetails = manager.displayMemberSettlementDtls(
					sqlFromDate, new java.sql.Date(toDate.getTime()));
		}
		claimForm.setSettledClms(settlementDetails);
		Log.log(Log.INFO, "ReportsAction", "displayMemberSettlementDtls",
				"Exited");
		return mapping.findForward("showMemberDetails");
	}

	public ActionForward displaySettlementDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ReportsAction", "displaySettlementDetails",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;

		java.util.Date fromDate = (java.util.Date) claimForm.getFromDate();
		java.util.Date toDate = (java.util.Date) claimForm.getToDate();
		String memberId = (String) request
				.getParameter(ClaimConstants.CLM_MEMBER_ID);
		ReportManager manager = new ReportManager();
		Vector firstClaimSettlementDetails = null;
		Vector secClaimSettlementDetails = null;
		java.sql.Date sqlFromDate = null;
		request.setAttribute("MEMBERID", request.getParameter("MEMBERID"));
		if ((fromDate.toString()).equals("")) {
			firstClaimSettlementDetails = manager.getSettlementDetails(null,
					new java.sql.Date(toDate.getTime()), memberId,
					ClaimConstants.FIRST_INSTALLMENT);
			secClaimSettlementDetails = manager.getSettlementDetails(null,
					new java.sql.Date(toDate.getTime()), memberId,
					ClaimConstants.SECOND_INSTALLMENT);
		} else {
			sqlFromDate = new java.sql.Date(fromDate.getTime());
			firstClaimSettlementDetails = manager.getSettlementDetails(
					sqlFromDate, new java.sql.Date(toDate.getTime()), memberId,
					ClaimConstants.FIRST_INSTALLMENT);
			secClaimSettlementDetails = manager.getSettlementDetails(
					sqlFromDate, new java.sql.Date(toDate.getTime()), memberId,
					ClaimConstants.SECOND_INSTALLMENT);
		}
		if ((firstClaimSettlementDetails.size() == 0)
				&& (secClaimSettlementDetails.size() == 0)) {
			request.setAttribute("message",
					"There are no Settlement Details that match the query..");
			return mapping.findForward("success");
		}
		claimForm.setSettlementsOfFirstClaim(firstClaimSettlementDetails);
		claimForm.setSettlementsOfSecondClaim(secClaimSettlementDetails);
		Log.log(Log.INFO, "ReportsAction", "displaySettlementDetails", "Exited");
		return mapping.findForward("showDetails");
	}

	// ///////////// End of Methods for Reports for Claims Processing
	// //////////////

	/*
	 * This method returns the query report.
	 */
	public ActionForward queryBuilderResult(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "queryBuilderResult", "Entered");
		ReportActionForm reportForm = (ReportActionForm) form;
		QueryBuilderFields qb = reportForm.getQueryBuilderFields();
		// System.out.println("queryBuilderResult");
		// System.out.println("checkbox: " +qb.isAppSubmittedSelChkBox());
		ArrayList report = reportManager.getQueryReport(qb);
		if (report == null || report.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			reportForm.setQueryReport(report);
			Log.log(Log.INFO, "ReportsAction", "queryBuilderResult", "Exited");
			return mapping.findForward("success");
		}
	}

	/*
	 * This method returns the defaulter report
	 */

	public ActionForward defaulterReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "defaulterReport", "Entered");

		ArrayList report = null;

		DefaulterInputFields di = new DefaulterInputFields();
		DynaActionForm dynaForm = (DynaActionForm) form;

		BeanUtils.populate(di, dynaForm.getMap()); // for setting cgbid
		report = reportManager.getDefaulterReport(di);
		dynaForm.set("default", report);

		if (report == null || report.size() == 0) {
			throw new NoDataException(
					"No Data is availabale for the data entered,"
							+ "Please Enter Any Other value");
		} else {
			Log.log(Log.INFO, "ReportsAction", "defaulterReport", "Exited");
			return mapping.findForward("success");
		}
	}

	/*
	 * This method forwards the query fields page from which the user can select
	 * the fields he wants to see in the query report.
	 */
	public ActionForward queryBuilderInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "queryBuilderInput", "Entered");
		HttpSession session = request.getSession(false);
		Object reportForm = session.getAttribute("reportForm");
		if (reportForm != null) {
			session.removeAttribute("reportForm");
		}

		Log.log(Log.INFO, "ReportsAction", "queryBuilderInput",
				"Exited. mapping : " + mapping.findForward("success"));
		return mapping.findForward("success");
	}

	/*
	 * This method forwards the query fields page from which the user can select
	 * the fields he wants to see in the query report.
	 */
	public ActionForward queryBuilderSelection(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "queryBuilderSelection", "Entered");
		ReportActionForm reportForm = (ReportActionForm) form;
		QueryBuilderFields qb = reportForm.getQueryBuilderFields();
		// System.out.println("testing testing");
		// System.out.println("combo: " +qb.getCgpanCombo());
		// System.out.println("chkbox : "+qb.isAppSubmittedChkBox());
		// System.out.println("cgpan : "+qb.getCgpan());
		Log.log(Log.INFO, "ReportsAction", "queryBuilderSelection",
				"Exited. mapping : " + mapping.findForward("success"));
		return mapping.findForward("success");
	}

	public ActionForward queryBuilderCancel(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "queryBuilderCancel", "Entered");
		HttpSession session = request.getSession(false);
		session.removeAttribute("reportForm");
		Log.log(Log.INFO, "ReportsAction", "queryBuilderCancel", "Exited");
		return mapping.findForward("success");
	}

	/**
	 * This method gets the borrowerId or the cgpan or the borrower name for
	 * repayment details
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

	public ActionForward showFilterForPIReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showFilterForPIReport", "Entered");

		ReportActionForm rpActionForm = (ReportActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
				"memberId " + memberId);
		GMProcessor gmProcessor = new GMProcessor();

		String forwardPage = "";

		if (bankId.equals(Constants.CGTSI_USER_BANK_ID)) {
			rpActionForm.setMemberId("");
			forwardPage = "success1";
		} else {

			ArrayList borrowerIds = gmProcessor.getBorrowerIds(memberId);
			int sizeOfList = borrowerIds.size();
			Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
					"borrowerIds.size " + sizeOfList);

			String borrowerName = null;

			ClaimsProcessor cl = new ClaimsProcessor();

			ArrayList borrInfos = new ArrayList();

			for (int i = 0; i < sizeOfList; ++i) {
				com.cgtsi.guaranteemaintenance.BorrowerInfo bInfoGm = new com.cgtsi.guaranteemaintenance.BorrowerInfo();
				String borrowerId = (String) borrowerIds.get(i);
				Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
						"borrowerId " + borrowerId);
				com.cgtsi.claim.BorrowerInfo bInfoCl = (com.cgtsi.claim.BorrowerInfo) cl
						.getBorrowerDetails(borrowerId);

				borrowerName = bInfoCl.getBorrowerName();
				Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
						"borrowerName " + borrowerName);

				Vector cgpans = gmProcessor.getCGPANs(borrowerId);
				Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
						"cgpans  size " + cgpans.size());

				ArrayList cgpanInfos = new ArrayList();
				String cgpan = null;

				if (cgpans != null && cgpans.size() != 0) {
					for (int j = 0; j < cgpans.size(); ++j) {
						HashMap cgpanMap = (HashMap) cgpans.get(j);
						cgpan = (String) cgpanMap.get(ClaimConstants.CLM_CGPAN);
						Log.log(Log.DEBUG, "ReportsAction",
								"showFilterForPIReport", "cgpan " + cgpan);
						CgpanInfo cgpanInfo = new CgpanInfo();
						cgpanInfo.setCgpan(cgpan);
						cgpanInfos.add(cgpanInfo);
					}
					bInfoGm.setBorrowerId(borrowerId);
					bInfoGm.setBorrowerName(borrowerName);
					bInfoGm.setCgpanInfos(cgpanInfos);

					borrInfos.add(bInfoGm);

				}
				// cgpanInfos.clear();
			}
			Log.log(Log.DEBUG, "ReportsAction", "showFilterForPIReport",
					"borrInfos size " + borrInfos.size());
			rpActionForm.setBorrowerDetailsForPIReport(borrInfos);

			borrInfos = null;
			borrowerIds = null;

			forwardPage = "success";
		}

		Log.log(Log.INFO, "ReportsAction", "showFilterForPIReport", "Exited");

		return mapping.findForward(forwardPage);
	}

	public ActionForward afterFilterForPIReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showFilterForPIReport", "Entered");

		ReportActionForm rpActionForm = (ReportActionForm) form;

		GMProcessor gmProcessor = new GMProcessor();
		Registration registration = new Registration();

		String memberId = rpActionForm.getMemberId();

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

			Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
					"mli Info.. :" + mliInfo);

			// checking if the member is active or not
			String statusFlag = mliInfo.getStatus();
			if (statusFlag.equals("I")) {
				throw new NoDataException("Member Id:" + memberId
						+ "  has been deactivated.");
			}
		}

		ArrayList borrowerIds = gmProcessor.getBorrowerIds(memberId);
		int sizeOfList = borrowerIds.size();
		Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
				"borrowerIds.size " + sizeOfList);

		String borrowerName = null;

		ClaimsProcessor cl = new ClaimsProcessor();

		ArrayList borrInfos = new ArrayList();

		for (int i = 0; i < sizeOfList; ++i) {
			com.cgtsi.guaranteemaintenance.BorrowerInfo bInfoGm = new com.cgtsi.guaranteemaintenance.BorrowerInfo();
			String borrowerId = (String) borrowerIds.get(i);
			Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
					"borrowerId " + borrowerId);
			com.cgtsi.claim.BorrowerInfo bInfoCl = (com.cgtsi.claim.BorrowerInfo) cl
					.getBorrowerDetails(borrowerId);

			borrowerName = bInfoCl.getBorrowerName();
			Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
					"borrowerName " + borrowerName);

			Vector cgpans = gmProcessor.getCGPANs(borrowerId);
			Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
					"cgpans  size " + cgpans.size());

			ArrayList cgpanInfos = new ArrayList();
			String cgpan = null;

			if (cgpans != null && cgpans.size() != 0) {
				for (int j = 0; j < cgpans.size(); ++j) {
					HashMap cgpanMap = (HashMap) cgpans.get(j);
					cgpan = (String) cgpanMap.get(ClaimConstants.CLM_CGPAN);
					Log.log(Log.DEBUG, "ReportsAction",
							"afterFilterForPIReport", "cgpan " + cgpan);
					CgpanInfo cgpanInfo = new CgpanInfo();
					cgpanInfo.setCgpan(cgpan);
					cgpanInfos.add(cgpanInfo);
				}
				bInfoGm.setBorrowerId(borrowerId);
				bInfoGm.setBorrowerName(borrowerName);
				bInfoGm.setCgpanInfos(cgpanInfos);

				borrInfos.add(bInfoGm);

			}

			// cgpanInfos.clear();
		}
		Log.log(Log.DEBUG, "ReportsAction", "afterFilterForPIReport",
				"borrInfos size " + borrInfos.size());
		rpActionForm.setBorrowerDetailsForPIReport(borrInfos);

		borrInfos = null;
		borrowerIds = null;

		return mapping.findForward(Constants.SUCCESS);
	}

	/**
	 * This method shows the Periodic Info Report for the Selected borrowerId
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

	public ActionForward showPeriodicInfoReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showRepaymentReport", "Entered");

		ReportActionForm rpActionForm = (ReportActionForm) form;

		HttpSession reportSession = request.getSession(false);

		GMProcessor gmProcessor = new GMProcessor();

		ArrayList repayPeriodicInfoDetailsTemp = null;
		// ArrayList repayPeriodicInfoDetails = null;

		// ArrayList osPeriodicInfoDetailsTemp = null;
		ArrayList osPeriodicInfoDetails = null;

		ArrayList disbPeriodicInfoDetailsTemp = null;
		// ArrayList disbPeriodicInfoDetails = null;

		NPADetails npaDetails = null;
		ArrayList recoveryDetails = null;

		String borrowerId = (String) request.getParameter("bidForPIReport");
		Log.log(Log.DEBUG, "ReportsAction", "showRepaymentReport",
				"borrowerId " + borrowerId);
		rpActionForm.setBorrowerId(borrowerId);

		repayPeriodicInfoDetailsTemp = gmProcessor
				.viewRepaymentDetailsForReport(borrowerId,
						GMConstants.TYPE_ZERO);

		reportSession.removeAttribute("reportFlag");
		reportSession.removeAttribute("outFlag");
		reportSession.removeAttribute("disFlag");
		reportSession.removeAttribute("recFlag");

		for (int i = 0; i < repayPeriodicInfoDetailsTemp.size(); i++) {
			PeriodicInfo pfTemp = (PeriodicInfo) repayPeriodicInfoDetailsTemp
					.get(i);
			ArrayList repaymentDtlTemp = (ArrayList) pfTemp
					.getRepaymentDetails();
			for (int j = 0; j < repaymentDtlTemp.size(); j++) {
				Repayment rDtlTemp = (Repayment) repaymentDtlTemp.get(j);
				ArrayList repaymentAmtTemp = (ArrayList) rDtlTemp
						.getRepaymentAmounts();
				for (int k = 0; k < repaymentAmtTemp.size(); k++) {
					RepaymentAmount rpAmtTemp = (RepaymentAmount) repaymentAmtTemp
							.get(k);
					if (rpAmtTemp != null) {
						rpActionForm
								.setRepayPeriodicInfoDetails(repayPeriodicInfoDetailsTemp);
					}
				}
				if (repaymentAmtTemp != null && repaymentAmtTemp.size() > 0) {
					reportSession.setAttribute("reportFlag", "Yes");
				} else {

					reportSession.setAttribute("reportFlag", "No");
				}
			}
		}

		osPeriodicInfoDetails = gmProcessor.viewOutstandingDetailsForReport(
				borrowerId, GMConstants.TYPE_ZERO);
		for (int i = 0; i < osPeriodicInfoDetails.size(); i++) {
			PeriodicInfo pfTemp = (PeriodicInfo) osPeriodicInfoDetails.get(i);
			ArrayList outDtlTemp = (ArrayList) pfTemp.getOutstandingDetails();
			for (int j = 0; j < outDtlTemp.size(); j++) {
				OutstandingDetail outStDtlTemp = (OutstandingDetail) outDtlTemp
						.get(j);
				ArrayList outAmtTemp = (ArrayList) outStDtlTemp
						.getOutstandingAmounts();
				for (int k = 0; k < outAmtTemp.size(); k++) {
					OutstandingAmount outstAmtTemp = (OutstandingAmount) outAmtTemp
							.get(k);
					if (outstAmtTemp != null) {
						rpActionForm
								.setOsPeriodicInfoDetails(osPeriodicInfoDetails);
					}
				}
				if (outAmtTemp != null && outAmtTemp.size() > 0) {
					reportSession.setAttribute("outFlag", "Yes");
				} else {

					reportSession.setAttribute("outFlag", "No");
				}
			}
		}

		disbPeriodicInfoDetailsTemp = gmProcessor
				.viewDisbursementDetailsForReport(borrowerId,
						GMConstants.TYPE_ZERO);
		PeriodicInfo pfTemp = (PeriodicInfo) disbPeriodicInfoDetailsTemp.get(0);
		ArrayList disburseDtlTemp = (ArrayList) pfTemp.getDisbursementDetails();
		for (int j = 0; j < disburseDtlTemp.size(); j++) {
			Disbursement rDtlTemp = (Disbursement) disburseDtlTemp.get(j);
			ArrayList disburseAmtTemp = (ArrayList) rDtlTemp
					.getDisbursementAmounts();
			for (int k = 0; k < disburseAmtTemp.size(); k++) {
				DisbursementAmount rpAmtTemp = (DisbursementAmount) disburseAmtTemp
						.get(k);
				if (rpAmtTemp != null) {
					rpActionForm
							.setDisbPeriodicInfoDetails(disbPeriodicInfoDetailsTemp);
				}
			}
			if (disburseAmtTemp != null && disburseAmtTemp.size() > 0) {
				reportSession.setAttribute("disFlag", "Yes");
			} else {

				reportSession.setAttribute("disFlag", "No");
			}

		}

		npaDetails = gmProcessor.getNPADetailsForReport(borrowerId);
		HttpSession session = request.getSession(false);

		session.setAttribute("isNPAAvailable", "Yes");
		if (npaDetails == null) {
			session.setAttribute("isNPAAvailable", "No");
		}

		recoveryDetails = gmProcessor.getRecoveryDetailsForReport(borrowerId);
		if (recoveryDetails != null && recoveryDetails.size() > 0) {
			reportSession.setAttribute("recFlag", "Yes");
		} else {

			reportSession.setAttribute("recFlag", "No");
		}

		rpActionForm.setNpaDetails(npaDetails);
		rpActionForm.setRecoveryDetails(recoveryDetails);

		Log.log(Log.INFO, "ReportsAction", "showRepaymentReport", "Exited");

		return mapping.findForward(Constants.SUCCESS);
	}

	private String createNewFile(HttpServletRequest request, String fileName,
			byte[] data) {
		Log.log(Log.INFO, "ReportsAction", "createNewFile", "Exited");

		String formattedToOSPath = request.getContextPath() + File.separator
				+ Constants.FILE_DOWNLOAD_DIRECTORY + File.separator + fileName;

		Log.log(Log.DEBUG, "ReportsAction", "createNewFile",
				"formattedToOSPath " + formattedToOSPath);

		try {

			String realPath = request.getSession(false).getServletContext()
					.getRealPath("");

			Log.log(Log.DEBUG, "ReportsAction", "createNewFile", "realPath "
					+ realPath);

			String contextPath = PropertyLoader.changeToOSpath(realPath);

			Log.log(Log.DEBUG, "ReportsAction", "createNewFile", "contextPath "
					+ contextPath);

			String filePath = contextPath + File.separator
					+ Constants.FILE_DOWNLOAD_DIRECTORY + File.separator
					+ fileName;

			Log.log(Log.DEBUG, "ReportsAction", "createNewFile", "filePath "
					+ filePath);

			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			fileOutputStream.write(data);
			fileOutputStream.flush();
			fileOutputStream.close();

			/*
			 * if(filePath != null) { filePath =
			 * PropertyLoader.changeToOSpath(filePath); }
			 */
		} catch (FileNotFoundException e) {
			Log.log(Log.ERROR, "ReportsAction", "createNewFile",
					"Error " + e.getMessage());
			Log.logException(e);
		} catch (Exception e) {
			Log.log(Log.ERROR, "ReportsAction", "createNewFile",
					"Error " + e.getMessage());
			Log.logException(e);
		}

		Log.log(Log.INFO, "ReportsAction", "createNewFile", "Exited");

		return formattedToOSPath;
	}

	// //////code for OTS report 27042011
	public ActionForward getOtsReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// JAGDISH CODE FOR CLAIM RECOVERY REPORT

		// System.out.println("<-=-----Inside Method getOTSRerport Report------>");
		Log.log(Log.INFO, "ReportsAction", "getOTSRerport", "Entered");
		// ActionForm dynaForm = (DynaActionForm)form;
		// dynaForm.initialize(mapping);
		ClaimActionForm clmActionform = (ClaimActionForm) form;
		clmActionform.setCp_ots_enterMember("");
		clmActionform.setCp_ots_unitName("");

		Log.log(Log.INFO, "ReportsAction", "getOTSRerport", "Exited");
		return mapping.findForward("success");
	}

	// /////////////////////
	public ActionForward getOtsReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimform = (ClaimActionForm) form;

		// DynaActionForm claimform=(DynaActionForm)form;

		try {
			Connection connection = DBConnection.getConnection();
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ArrayList claimformdata = new ArrayList();
			ClaimActionForm claimFormmainObj = new ClaimActionForm();
			Date fromdate = null;
			Date todate = null;
			String memberId = "";

			String appRovDate = "";
			String mem_condition = "";
			String unit_like = "";
			String forUnitName = "";

			// Date fdate=claimform.getCp_ots_fromDate();
			// System.out.println("The from Date  is :--->"+fdate);
			//
			// Date tdate=claimform.getCp_ots_toDate();
			// System.out.println("The to Date is :--->"+tdate);
			// //<form-property name="cp_ots_fromDate" type="java.util.Date" />
			// <form-property name="cp_ots_toDate" type="java.util.Date" />
			//
			//
			String fdate = claimform.getCp_ots_fromDate();
			// System.out.println("The from Date  is :--->"+fdate);
			String tdate = claimform.getCp_ots_toDate();
			// System.out.println("The to Date is :--->"+tdate);

			String member = claimform.getCp_ots_enterMember();
			// System.out.println("The memberId is :-->"+member);
			// boolean claimType=claimform.isClm_type();

			String claimDecision = "";
			try {
				claimDecision = claimform.getClm_decision();
				// System.out.println("The selected Deciion is :-->"+claimDecision);
				if (claimDecision.equals("") || claimDecision.equals(null)) {
					throw new NoMemberFoundException(
							" Please  Select any Decision Type. ");
				}
			} catch (Exception e) {
				throw new NoMemberFoundException(
						" Please  Select any Decision Type. ");
			}

			// System.out.println("The selected Deciion is 2 :-->"+claimDecision);
			// if (claimDecision)
			claimDecision = claimDecision.trim();

			String uniName = claimform.getCp_ots_unitName().toUpperCase();
			// System.out.println("the Inserted String is :--->"+uniName);

			// Date fdate=claimform.get("cp_ots_fromDate");
			// System.out.println("The from Date  is :--->"+fdate);
			// Date tdate=claimform.get("cp_ots_toDate");
			// System.out.println("The to Date is :--->"+tdate);
			// String member=claimform.get(cp_ots_enterMember();
			// System.out.println("The memberId is :-->"+member);
			// boolean claimType=claimform.isClm_type();
			// System.out.println("The claim Type Selected is :--->"+claimType);
			// String uniName=claimform.getCp_ots_unitName().toUpperCase();
			// System.out.println("the Inserted String is :--->"+uniName);
			//

			Date da = Calendar.getInstance().getTime();
			Date dm = new Date();

			String fmdat = fdate;
			String tmdate = tdate;
			if (!fmdat.equals("")) {
				String frmdate = DateHelper.stringToDBDate(fmdat);
				String trmdate = DateHelper.stringToDBDate(tmdate);

				appRovDate = " AND CA.APPROVER_DATE BETWEEN '" + frmdate
						+ "' AND '" + trmdate + "'";
			} else if (fmdat.equals("")) {
				String trmdate1 = DateHelper.stringToDBDate(tmdate);
				appRovDate = " AND CA.APPROVER_DATE <= '" + trmdate1 + "'";
			} else if (tmdate.equals("")) {
				throw new NoMemberFoundException(" plz provide to date . ");
			}

			if (!member.equals("")) {
				memberId = member;
				mem_condition = " AND T.MEMBERID='" + memberId + "'";
			}
			String query = "";
			if (!uniName.equals("")) {
				forUnitName = " AND T.UNIT_NAME LIKE '" + uniName + "%'";
			} else if (uniName.equals("")) {
				forUnitName = "";
			}

			String a = "SELECT T.MEMBERID,T.CLM_REF_NO,T.CGPAN, T.UNIT_NAME,T.GURANTEE_AMT,T.FIRST_INSTALL_PAID_AMT, \n"
					+ "T.NET_REC_AMT,T.SEC_INSTALL_AMT,T.FINAL_PAY_AMT,CTD_TC_CLM_ELIG_AMT CLM_ELIGI  FROM OTS T,CLAIM_TC_DETAIL TC,OTS_TOTAL CA \n"
					+ "WHERE T.CGPAN=TC.CGPAN \n"
					+ "AND CA.CLM_REF_NO=T.CLM_REF_NO \n"
					+ "AND CA.STATUS='"
					+ claimDecision + "' \n" + "" + appRovDate + "\n" + "";

			String b = "" + mem_condition + " \n" + "";
			query = a + b;
			query = query.trim();

			String c = "" + forUnitName + " \n" + "";
			query = query + c;
			query = query.trim();

			String d = " UNION ALL \n"
					+ "SELECT T.MEMBERID,T.CLM_REF_NO,T.CGPAN,T.UNIT_NAME,T.GURANTEE_AMT,T.FIRST_INSTALL_PAID_AMT,T.NET_REC_AMT,T.SEC_INSTALL_AMT,T.FINAL_PAY_AMT,CWD_WC_CLM_ELIG_AMT CLM_ELIGI FROM OTS T,CLAIM_WC_DETAIL W,OTS_TOTAL CA \n"
					+ "WHERE W.CGPAN=T.CGPAN \n"
					+ "AND CA.CLM_REF_NO=T.CLM_REF_NO \n" + "AND CA.STATUS='"
					+ claimDecision + "' \n" + "";
			query = query + d;
			query = query.trim();

			String e = "" + appRovDate + "\n" + "";

			query = query + e;
			query = query.trim();

			String f = "" + mem_condition + "\n" + "";
			query = query + f;
			query = query.trim();

			String g = "" + forUnitName + "\n" + "";
			query = query + g;
			query = query.trim();

			// System.out.println("===>"+query);

			// ////////////////////////////////////////////////////////////////////////////////////////////
			ResultSet rsData = str.executeQuery(query);
			while (!rsData.next()) {
				throw new NoMemberFoundException(" There Is No Data . ");
			}
			rsData.beforeFirst();

			while (rsData.next()) {
				ClaimActionForm claimActionForm = new ClaimActionForm();

				String memid = rsData.getString(1);
				claimActionForm.setCp_ots_enterMember(memid);
				// System.out.println("The Member Id is ==>"+memid);
				String clmrefno = rsData.getString(2);
				claimActionForm.setCp_ots_appRefNo(clmrefno);
				// System.out.println("The Claim Ref Number is ==>"+clmrefno);
				String cgpan = rsData.getString(3);
				claimActionForm.setCp_ots_enterCgpan(cgpan);
				// System.out.println("The CGPAN is==>"+cgpan);
				String unitname = rsData.getString(4);
				claimActionForm.setCp_ots_unitName(unitname);
				// System.out.println("The Unit Name==>"+unitname);
				double gaurnteeamt = rsData.getDouble(5);
				claimActionForm.setCp_ots_cgpanGaurnteeAmt(gaurnteeamt);
				// System.out.println("The Gaurntee Amount is ==>"+gaurnteeamt);
				double firstisntallamt = rsData.getDouble(6);
				claimActionForm.setCp_ots_firstIntalpaidAmount(firstisntallamt);
				// System.out.println("The First Installment Amount is ==>"+firstisntallamt);
				double netrecamt = rsData.getDouble(7);
				claimActionForm.setCp_ots_netRecovAmt(netrecamt);
				// System.out.println("The Net Recovery is ==>"+netrecamt);

				double secondinstall = rsData.getDouble(8);
				claimActionForm.setCp_ots_secIntalAMt(secondinstall);
				// System.out.println("The Second Installment Amount is ==>"+secondinstall);

				double finalpay = rsData.getDouble(9);
				claimActionForm.setCp_ots_finalPayout(finalpay);
				// System.out.println("The Final Pay Out is ==>"+finalpay);

				double clmelgiamt = rsData.getDouble(10);
				claimActionForm.setCp_ots_clmeligibleamt(clmelgiamt);
				// System.out.println("The Claim Amount is ==>"+clmelgiamt);
				// add objects ins arraylist
				claimformdata.add(claimActionForm);
			}
			claimFormmainObj.setClaimformdataReport(claimformdata);

			BeanUtils.copyProperties(claimform, claimFormmainObj);

		}// try close
			// catch(Exception e)
			// {
			// e.printStackTrace();
			// }
		finally {

		}

		return mapping.findForward("xyz");
	}

	public ActionForward tcQueryClaimDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "tcclaimRefNoClaimDetail", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(4, "ReportsAction", "tcclaimRefNoClaimDetail", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward displayTCQueryDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "displayClmRefNumberDtl", "Entered");
		ReportManager manager = new ReportManager();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String clmApplicationStatus = "";
		Log.log(4, "ReportsAction", "displayTCQueryDetail",
				"Claim Application Status being queried ");
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		String memberId = bankid + zoneid + branchid;
		String cgpanno = null;
		String claimRefNumber = null;
		cgpanno = request.getParameter("cgpan");
		claimRefNumber = request.getParameter("clmRefNumber");
		if (cgpanno != null && !cgpanno.equals("")) {
			PreparedStatement pstmt = null;
			ResultSet rst = null;
			String clmRefNo = "";
			Connection connection = DBConnection.getConnection();
			try {
				String query = "select distinct clm_ref_no,clm_status \nfrom claim_detail_temp ct,application_detail a,ssi_detail s\nwhere ct.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand a.cgpan = ?\nunion all\nselect distinct clm_ref_no,clm_status \nfrom claim_detail c,application_detail a,ssi_detail s\nwhere c.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand a.cgpan = ? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, cgpanno);
				pstmt.setString(2, cgpanno);
				for (rst = pstmt.executeQuery(); rst.next();) {
					claimRefNumber = rst.getString(1);
					clmApplicationStatus = rst.getString(2);
				}

				if (clmApplicationStatus.equals(""))
					throw new DatabaseException("Enter a valid cgpan.");
				rst.close();
				rst = null;
				pstmt.close();
				pstmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		} else {
			claimRefNumber = request.getParameter("clmRefNumber");
			PreparedStatement pstmt = null;
			ResultSet rst = null;
			String clmRefNo = "";
			Connection connection = DBConnection.getConnection();
			try {
				String query = "select distinct clm_status \nfrom claim_detail_temp ct,application_detail a,ssi_detail s\nwhere ct.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand ct.clm_ref_no = ?\nunion all\nselect distinct clm_status \nfrom claim_detail c,application_detail a,ssi_detail s\nwhere c.bid = s.bid\nand s.ssi_reference_number = a.ssi_reference_number\nand c.clm_ref_no = ? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, claimRefNumber);
				pstmt.setString(2, claimRefNumber);
				for (rst = pstmt.executeQuery(); rst.next();)
					clmApplicationStatus = rst.getString(1);

				if (clmApplicationStatus.equals(""))
					throw new DatabaseException("Enter a valid Claim Ref No.");
				rst.close();
				rst = null;
				pstmt.close();
				pstmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		}
		if (!clmApplicationStatus.equals("TC")
				&& !clmApplicationStatus.equals(""))
			throw new DatabaseException(
					"Please enter correct details. Claim should in Temporary Closed Status");
		Connection connection = DBConnection.getConnection(false);
		ArrayList rsfBanks = new ArrayList();
		ArrayList tcQueryList = new ArrayList();
		try {
			CallableStatement callable = connection
					.prepareCall("{?=call Packgetclmqrydetails.funcGetClmQryDetails(?,?,?,?)}");
			callable.registerOutParameter(1, 4);
			callable.setString(2, cgpanno);
			callable.setString(3, claimRefNumber);
			callable.registerOutParameter(4, Constants.CURSOR);
			callable.registerOutParameter(5, Types.VARCHAR);
			callable.execute();
			int errorCode = callable.getInt(1);
			String error = callable.getString(5);
			Log.log(5, "ReportAction", "displayTCQueryDetail",
					"Error code and error are " + errorCode + " " + error);
			if (errorCode == 1) {
				connection.rollback();
				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}
			tcQueryList = new ArrayList();
			ResultSet result;
			String str[];
			for (result = (ResultSet) callable.getObject(4); result.next(); tcQueryList
					.add(str)) {
				str = new String[19];
				str[0] = result.getString(1);
				str[1] = result.getString(2);
				str[2] = result.getString(3);
				str[3] = result.getString(4);
				str[4] = result.getString(5);
				str[5] = result.getString(6);
				str[6] = result.getString(7);
				str[7] = result.getString(8);
				str[8] = result.getString(9);
				str[9] = result.getString(10);
				str[10] = result.getString(11);
				str[11] = result.getString(12);
				str[12] = result.getString(13);
				str[13] = result.getString(14);
				str[14] = result.getString(15);
				str[15] = result.getString(16);
				str[16] = result.getString(17);
				str[17] = result.getString(18);
			}

			claimForm.setDanSummary(tcQueryList);
			result.close();
			result = null;
			callable.close();
			callable = null;
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(2, "ReportAction", "displayTCQueryDetail",
						ignore.getMessage());
			}
			Log.log(2, "ReportAction", "displayTCQueryDetail", e.getMessage());
			Log.logException(e);
		} finally {
			DBConnection.freeConnection(connection);
		}
		request.setAttribute("danSummary", tcQueryList);
		// System.out.println("danSummarysize...******************.." +
		// tcQueryList.size());
		return mapping.findForward("success");
	}

	public ActionForward dailypaymentReportforASF(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "dailypaymentReport", "Entered");
		ArrayList dailypayment = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		Date sDate = (Date) dynaForm.get("dateOfTheDocument16");
		String stDate = String.valueOf(sDate);
		if (stDate == null || stDate.equals(""))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		Date eDate = (Date) dynaForm.get("dateOfTheDocument17");
		endDate = new java.sql.Date(eDate.getTime());
		String id = (String) dynaForm.get("memberId");
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		if (id == null || id.equals("")) {
			dailypayment = reportManager.getdailyPaymentReportforRSF(startDate,
					endDate, id);
			dynaForm.set("dailypayment", dailypayment);
			if (dailypayment == null || dailypayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered, Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(4, "ReportsAction", "dailypaymentReport", "Exited");
				return mapping.findForward("success");
			}
		}
		if (memberids.contains(id)) {
			String bankId = id.substring(0, 4);
			String zoneId = id.substring(4, 8);
			String branchId = id.substring(8, 12);
			if (!branchId.equals("0000")) {
				String memberId = (new StringBuilder()).append(bankId)
						.append(zoneId).append(branchId).toString();
				dailypayment = reportManager
						.getdailyPaymentReportForBranchforRSF(startDate,
								endDate, memberId);
				dynaForm.set("dailypayment", dailypayment);
				if (dailypayment == null || dailypayment.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered, Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(4, "ReportsAction", "dailypaymentReport", "Exited");
					return mapping.findForward("success");
				}
			}
			if (!zoneId.equals("0000")) {
				dailypayment = reportManager
						.getdailyPaymentReportForZoneforRSF(startDate, endDate,
								bankId, zoneId);
				dynaForm.set("dailypayment", dailypayment);
				if (dailypayment == null || dailypayment.size() == 0) {
					throw new NoDataException(
							"No Data is available for the values entered, Please Enter Any Other Value ");
				} else {
					claimsProcessor = null;
					memberids = null;
					Log.log(4, "ReportsAction", "dailypaymentReport", "Exited");
					return mapping.findForward("success");
				}
			}
			String memberId = bankId;
			dailypayment = reportManager.getdailyPaymentReportForBankforRSF(
					startDate, endDate, memberId);
			dynaForm.set("dailypayment", dailypayment);
			if (dailypayment == null || dailypayment.size() == 0) {
				throw new NoDataException(
						"No Data is available for the values entered, Please Enter Any Other Value ");
			} else {
				claimsProcessor = null;
				memberids = null;
				Log.log(4, "ReportsAction", "dailypaymentReport", "Exited");
				return mapping.findForward("success");
			}
		} else {
			throw new NoDataException("Please Enter Valid Member Id");
		}
	}

	// added on 08-03-2014
	public ActionForward showClaimMliListPathForNewCases(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Entered");
		HashMap<String,ArrayList> datamap  = new HashMap<String,ArrayList>();
		
		ArrayList mli = new ArrayList();
		ArrayList totcnt = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;

		datamap = reportManager.getClaimAppListMliWiseForNewCases();
		mli = datamap.get("AppList"); 
	    totcnt = datamap.get("TotCntList");
	     
		
		dynaForm.set("mli", mli);
		dynaForm.set("totcnt",totcnt);
		String forward = "";
		if (mli == null || mli.size() == 0) {
			Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Exited");
			request.setAttribute("message",
					"No Forwarded Applications for Claim Approval Available");
			forward = "success";

		} else {
			mli = null;
			Log.log(Log.INFO, "ReportsAction", "showClaimMliListPath", "Exited");
			forward = "clmapprovalPageList";
		}
		return mapping.findForward(forward);
	}

	// Diksha........................

	public ActionForward InspectionReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		String memberId = (new StringBuilder(String.valueOf(bankid)))
				.append(zoneid).append(branchid).toString();
		String inspectstatus = claimForm.getInspectstatus();
		request.setAttribute("inspectstatus", inspectstatus);
		ClaimActionForm claimdetail = (ClaimActionForm) form;
		java.io.PrintWriter out = response.getWriter();
		String memid = "";
		String unitName = "";
		String cgpan = request.getParameter("cgpan");
		String CGPAN = claimForm.getCgpan();
		String memberID = claimForm.getMemberId();
		Connection connection = DBConnection.getConnection();
		Statement str = connection.createStatement();
		claimdetail.setInspectPerson("");
		claimdetail.setInspectRemarks("");
		claimdetail.setCgpan("");
		claimdetail.setInspectstatus(inspectstatus);
		claimdetail.setIsrecproposed("");
		claimdetail.setInsrecoveryamt("");
		claimdetail.setAmnt_rec_descion("");
		String Query = "";
		ArrayList cgpanList = new ArrayList();
		ArrayList UnitList = new ArrayList();
		ArrayList insPacDateList = new ArrayList();
		ArrayList rptSubDateList = new ArrayList();
		ArrayList statutsList = new ArrayList();
		ArrayList recvAmtList = new ArrayList();
		ArrayList RecvryAmtList = new ArrayList();
		ArrayList Amount_RCVDList = new ArrayList();
		ArrayList RemarksList = new ArrayList();
		ArrayList MliIDList = new ArrayList();
		ArrayList MliNameList = new ArrayList();
		ArrayList ZoneNameList = new ArrayList();
		ArrayList compl_recvd_date = new ArrayList();
		ArrayList issued_date = new ArrayList();
		ArrayList further_compl = new ArrayList();
		ArrayList inspectorList = new ArrayList();
		if (inspectstatus != null && inspectstatus.equals("OP")) {
			Query = "   SELECT CLAIM_INSPECT_BY,  CI.CGPAN,    m.MEM_ZONE_NAME,S.ssi_unit_name,CI.CLM_MLI_NAME,CI.CLM_MEMBER_ID,nvl(CI.CLM_FURTHER_COMPLIANCE,'null')CLM_FURTHER_COMPLIANCE,NVL(CLM_COMPLIANCE_RCVD_DATE,null)CLM_COMPLIANCE_RCVD_DATE,NVL(CLM_ISSUED_DATE,null)CLM_ISSUED_DATE,   NVL(CI.CLM_INSPECT_DATE,null)CLM_INSPECT_DATE,    NVL(CI.CLM_REPORT_DATE,null) CLM_REPORT_DATE,     CI.CLM_INPSTATUS,    nvl(CI.CLM_RECOVERY_AMT,'0') CLM_RECOVERY_AMT,     nvl(CI.CLM_INSRECEIVED_AMT,'N')CLM_INSRECEIVED_AMT, nvl(CLM_AMOUNT_RECVD,'0')CLM_AMOUNT_RECVD,  nvl(CLAIM_INSPECT_REMARKS,'null')CLAIM_INSPECT_REMARKS FROM CLAIM_INSPECTION_DATA CI, APPLICATION_DETAIL A, ssi_detail S,member_info m  WHERE     CI.CGPAN = A.CGPAN  AND A.ssi_reference_number = S.ssi_reference_number  AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id and CI.CLM_INPSTATUS='OP' ";
			if (CGPAN != null && CGPAN.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append(" AND A.CGPAN='").append(CGPAN).append("'")
						.toString();
			if (memberID != null && memberID.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append(" AND m.MEM_BNK_ID||m.MEM_ZNE_ID||m.MEM_BRN_ID='")
						.append(memberID).append("'").toString();
		} else if (inspectstatus != null && inspectstatus.equals("CL")) {
			Query = "   SELECT CLAIM_INSPECT_BY,  CI.CGPAN,    m.MEM_ZONE_NAME,S.ssi_unit_name,CI.CLM_MLI_NAME,CI.CLM_MEMBER_ID,NVL(CLM_COMPLIANCE_RCVD_DATE,null)CLM_COMPLIANCE_RCVD_DATE,NVL(CLM_ISSUED_DATE,null)CLM_ISSUED_DATE,CLM_FURTHER_COMPLIANCE,   NVL(CI.CLM_INSPECT_DATE,null)CLM_INSPECT_DATE,    NVL(CI.CLM_REPORT_DATE,null)CLM_REPORT_DATE,     CI.CLM_INPSTATUS,     nvl(CI.CLM_RECOVERY_AMT,'0')CLM_RECOVERY_AMT,    nvl(CI.CLM_INSRECEIVED_AMT,'N')CLM_INSRECEIVED_AMT, nvl(CLM_AMOUNT_RECVD,'0')CLM_AMOUNT_RECVD,  nvl(CLAIM_INSPECT_REMARKS,'null')CLAIM_INSPECT_REMARKS FROM CLAIM_INSPECTION_DATA CI, APPLICATION_DETAIL A, ssi_detail S,member_info m  WHERE CI.CGPAN = A.CGPAN  AND A.ssi_reference_number = S.ssi_reference_number  AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id and CI.CLM_INPSTATUS='CL' ";
			if (CGPAN != null && CGPAN.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append(" AND A.CGPAN='").append(CGPAN).append("'")
						.toString();
			if (memberID != null && memberID.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append(" AND m.MEM_BNK_ID||m.MEM_ZNE_ID||m.MEM_BRN_ID='")
						.append(memberID).append("'").toString();
		} else {
			Query = "   SELECT CLAIM_INSPECT_BY,  CI.CGPAN,    m.MEM_ZONE_NAME,S.ssi_unit_name,CI.CLM_MLI_NAME,CI.CLM_MEMBER_ID,NVL(CLM_COMPLIANCE_RCVD_DATE,null)CLM_COMPLIANCE_RCVD_DATE,NVL(CLM_ISSUED_DATE,null)CLM_ISSUED_DATE,CLM_FURTHER_COMPLIANCE,   NVL(CI.CLM_INSPECT_DATE,null)CLM_INSPECT_DATE,  NVL(CI.CLM_REPORT_DATE,null)CLM_REPORT_DATE,   CI.CLM_INPSTATUS,   nvl(CI.CLM_RECOVERY_AMT,'0')CLM_RECOVERY_AMT,  nvl(CLM_AMOUNT_RECVD,'0')CLM_AMOUNT_RECVD,    nvl(CI.CLM_INSRECEIVED_AMT,'N')CLM_INSRECEIVED_AMT,    nvl(CLAIM_INSPECT_REMARKS,'null')CLAIM_INSPECT_REMARKS FROM CLAIM_INSPECTION_DATA CI, APPLICATION_DETAIL A, ssi_detail S,member_info m  WHERE CI.CGPAN = A.CGPAN  AND A.ssi_reference_number = S.ssi_reference_number AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id ";
			if (CGPAN != null && CGPAN.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append("  AND A.CGPAN='").append(CGPAN).append("'")
						.toString();
			if (memberID != null && memberID.length() > 0)
				Query = (new StringBuilder(String.valueOf(Query)))
						.append("  AND m.MEM_BNK_ID||m.MEM_ZNE_ID||m.MEM_BRN_ID='")
						.append(memberID).append("'").toString();
		}
		ResultSet rsforvalid = str.executeQuery(Query);
		String memberid = "";
		while (rsforvalid.next()) {
			if (rsforvalid.getString("CLAIM_INSPECT_BY") != null)
				inspectorList.add(rsforvalid.getString("CLAIM_INSPECT_BY"));
			else
				inspectorList.add("");
			if (rsforvalid.getString("CGPAN") != null)
				cgpanList.add(rsforvalid.getString("CGPAN"));
			else
				cgpanList.add("");
			if (rsforvalid.getString("SSI_UNIT_NAME") != null)
				UnitList.add(rsforvalid.getString("SSI_UNIT_NAME"));
			else
				UnitList.add("");
			if (rsforvalid.getString("CLM_INSPECT_DATE") != null)
				insPacDateList.add(rsforvalid.getString("CLM_INSPECT_DATE")
						.replaceAll("00:00:00.0", ""));
			else
				insPacDateList.add("");
			if (rsforvalid.getString("CLM_REPORT_DATE") != null)
				rptSubDateList.add(rsforvalid.getString("CLM_REPORT_DATE")
						.replaceAll("00:00:00.0", ""));
			else
				rptSubDateList.add("");
			if (rsforvalid.getString("CLM_INPSTATUS") != null)
				statutsList.add(rsforvalid.getString("CLM_INPSTATUS"));
			else
				statutsList.add("");
			if (rsforvalid.getString("CLM_INSRECEIVED_AMT") != null)
				recvAmtList.add(rsforvalid.getString("CLM_INSRECEIVED_AMT"));
			else
				recvAmtList.add("");
			if (rsforvalid.getString("CLM_AMOUNT_RECVD") != null)
				Amount_RCVDList.add(rsforvalid.getString("CLM_AMOUNT_RECVD"));
			else
				Amount_RCVDList.add("");
			if (rsforvalid.getString("CLM_RECOVERY_AMT") != null)
				RecvryAmtList.add(rsforvalid.getString("CLM_RECOVERY_AMT"));
			else
				Amount_RCVDList.add("");
			if (rsforvalid.getString("CLM_MEMBER_ID") != null)
				MliIDList.add(rsforvalid.getString("CLM_MEMBER_ID"));
			else
				MliIDList.add("");
			if (rsforvalid.getString("CLM_MLI_NAME") != null)
				MliNameList.add(rsforvalid.getString("CLM_MLI_NAME"));
			else
				MliNameList.add("");
			if (rsforvalid.getString("CLAIM_INSPECT_REMARKS") != null)
				RemarksList.add(rsforvalid.getString("CLAIM_INSPECT_REMARKS"));
			else
				RemarksList.add("");
			if (rsforvalid.getString("MEM_ZONE_NAME") != null)
				ZoneNameList.add(rsforvalid.getString("MEM_ZONE_NAME"));
			else
				ZoneNameList.add("");
			if (rsforvalid.getString("CLM_COMPLIANCE_RCVD_DATE") != null)
				compl_recvd_date.add(rsforvalid.getString(
						"CLM_COMPLIANCE_RCVD_DATE")
						.replaceAll("00:00:00.0", ""));
			else
				compl_recvd_date.add("");
			if (rsforvalid.getString("CLM_ISSUED_DATE") != null)
				issued_date.add(rsforvalid.getString("CLM_ISSUED_DATE")
						.replaceAll("00:00:00.0", ""));
			else
				issued_date.add("");
			if (rsforvalid.getString("CLM_FURTHER_COMPLIANCE") != null)
				further_compl.add(rsforvalid
						.getString("CLM_FURTHER_COMPLIANCE"));
			else
				further_compl.add("");
		}
		request.setAttribute("cgpanList", cgpanList);
		request.setAttribute("UnitList", UnitList);
		request.setAttribute("insPacDateList", insPacDateList);
		request.setAttribute("rptSubDateList", rptSubDateList);
		request.setAttribute("statutsList", statutsList);
		request.setAttribute("recvAmtList", recvAmtList);
		request.setAttribute("RecvryAmtList", RecvryAmtList);
		request.setAttribute("RemarksList", RemarksList);
		request.setAttribute("Amount_RCVDList", Amount_RCVDList);
		request.setAttribute("MliNameList", MliNameList);
		request.setAttribute("MliIDList", MliIDList);
		request.setAttribute("ZoneNameList", ZoneNameList);
		request.setAttribute("inspectorList", inspectorList);
		request.setAttribute("compl_recvd_date", compl_recvd_date);
		request.setAttribute("issued_date", issued_date);
		request.setAttribute("further_compl", further_compl);
		return mapping.findForward("success");
	}

	public ActionForward InspectionReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "InspectionReport", "Entered");
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		if (bankId.equals("0000"))
			memberId = "";
		Log.log(4, "ReportsAction", "InspectionReport", "Exited");
		return mapping.findForward("success");
	}

	// Diksha end.....................

	// Diksha

	public ActionForward minorityStateReportDcmse(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minorityStateReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		Date prevDate = calendar.getTime();
		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);

		Log.log(Log.INFO, "ReportsAction", "minorityStateReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward minoritystateprogressReportDcmse(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "minoritystateprogressReport",
				"Entered");
		ArrayList minoritystateprogressReport = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);

		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());

		// System.out.println("sukumar:shyam");
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println(" startDate:"+ startDate);
		// System.out.println("endDate :"+endDate );
		minoritystateprogressReport = reportManager
				.getminorityStateProgressReport(startDate, endDate);
		dynaForm.set("minoritystateprogressReport", minoritystateprogressReport);
		if (minoritystateprogressReport == null
				|| minoritystateprogressReport.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		} else {
			Log.log(Log.INFO, "ReportsAction", "minoritystateprogressReport",
					"Exited");
			return mapping.findForward("success");
		}
	}

	/*
	 * progressReport =
	 * reportManager.getMonthlyProgressReport(startDate,endDate);
	 * dynaForm.set("progressReport",progressReport); if(progressReport == null
	 * || progressReport.size()==0) { throw new
	 * NoDataException("No Data is available for the values entered," +
	 * " Please Enter Any Other Value "); } else {
	 * Log.log(Log.INFO,"ReportsAction","minorityprogressReport","Exited");
	 * return mapping.findForward("success"); }
	 */

	// Diksha
	public ActionForward epcsPaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "stateWiseReport", "Entered");

		DynaActionForm dynaForm = (DynaActionForm) form;
		java.util.Date date = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month -= 1;
		day += 1;
		calendar.set(2, month);
		calendar.set(5, day);
		java.util.Date prevDate = calendar.getTime();

		GeneralReport generalReport = new GeneralReport();
		generalReport.setDateOfTheDocument(prevDate);
		generalReport.setDateOfTheDocument1(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		return mapping.findForward("success");

	}

	public ActionForward epcsPaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "securitizationReport", "Entered");
		ArrayList dan = new ArrayList();
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument");
		String stDate = String.valueOf(sDate);
		// System.out.println("stDate"+stDate);
		if ((stDate == null) || (stDate.equals("")))
			startDate = null;
		else if (stDate != null) {
			startDate = new java.sql.Date(sDate.getTime());
		}
		java.util.Date eDate = (java.util.Date) dynaForm
				.get("dateOfTheDocument1");
		endDate = new java.sql.Date(eDate.getTime());
		// System.out.println("endDate"+endDate);

		String chkvalue = (String) dynaForm.get("checks");
		String danType = (String) dynaForm.get("dantype");

		String memid = (String) dynaForm.get("memberId");
		// System.out.println("memid"+ memid);
  
		// System.out.println("chkvalue is"+chkvalue);
		CallableStatement callableStmt = null;
		Connection conn = null;
		ClaimActionForm claimForm = null;

		int status = -1;
		String errorCode = null;
		String errorCode1 = null;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		// String danType="AF";
		
		System.out.println("memid"+memid);
		 System.out.println("startDate"+startDate);

		 System.out.println("endDate"+endDate);
		
		System.out.println("chkvalue"+chkvalue);
		 System.out.println("danType"+danType);
		 
		 if (memid.equals("")||memid.equals(null)) {
			 memid = memberId;
			}
		 
		 System.out.println("memid==="+memid);
		/*if (chkvalue.equals("I")) {
			startDate = null;
		}*/

		try {
			conn = DBConnection.getConnection();
			// callableStmt =
			// conn.prepareCall("{? = call PACK_online_PAYMENT_DETAIL.PAYMENT_STATUS_REPORT(?,?,?,?,?,?,?,?)}");
			callableStmt = conn.prepareCall("{? = call PAYMENT_STATUS_REPORT_NEW(?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, memid);
			callableStmt.setString(3, chkvalue);
			callableStmt.setDate(4, startDate);
			callableStmt.setDate(5, endDate);
			callableStmt.setString(6,danType );
			callableStmt.registerOutParameter(7, Constants.CURSOR);
			callableStmt.registerOutParameter(8, Constants.CURSOR);
			callableStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			// errorCode = callableStmt.getString(7);
			// errorCode1 = callableStmt.getString(8);

			if (status == Constants.FUNCTION_FAILURE) {

				// System.out.println("status1"+status);

				Log.log(Log.ERROR, "CPDAO", "getMemberIDForCGPAN()",
						"SP returns a 1. Error code is :" + errorCode);

				callableStmt.close();

				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// System.out.println("status2"+status);
				ResultSet allPaymentResult = (ResultSet) callableStmt
						.getObject(7);
				ResultSet allPaymentResult1 = (ResultSet) callableStmt.getObject(8);

				ArrayList paymentArray = new ArrayList();
				while (allPaymentResult.next()) {
					// System.out.println("paymentArray");
					GeneralReport generalReport = new GeneralReport();
					generalReport.setMliIds(allPaymentResult.getString(2));
					generalReport.setPayids(allPaymentResult.getString(3));
					generalReport.setVaccno(allPaymentResult.getString(4));
					// System.out.println("date setVaccno"+allPaymentResult.getString(4));
					generalReport.setPaymentDate(allPaymentResult.getDate(5));
					// System.out.println("date setPaymentDate"+allPaymentResult.getDate(5));
					generalReport.setPaymentcreditDate(allPaymentResult
							.getDate(6));
					generalReport.setAmounts(allPaymentResult.getDouble(7));
					// System.out.println("amount iss"+allPaymentResult.getDouble(7));
					generalReport.setDantype(allPaymentResult.getString(8));
					generalReport.setDanstatus(allPaymentResult.getString(9));
					generalReport.setPaymentstatus(allPaymentResult
							.getString(10));
					paymentArray.add(generalReport);
					dynaForm.set("PaymentReportStatus", paymentArray);

				}

				while (allPaymentResult1.next()) {
					ArrayList paymentArray1 = new ArrayList();
					// System.out.println("paymentArray211111");
					GeneralReport generalReport = new GeneralReport();
					generalReport.setCount(allPaymentResult1.getString(1));
					// System.out.println("count=="+allPaymentResult1.getString(1));
					generalReport.setToatlAmounts(allPaymentResult1
							.getDouble(2));
					// System.out.println("amt"+allPaymentResult1.getDouble(2));

					paymentArray1.add(generalReport);
					dynaForm.set("PaymentReportStatuscount", paymentArray1);
				}

				callableStmt.close();
			}

		} catch (SQLException sqlexception) {

			// sqlexception.getMessage();
			sqlexception.printStackTrace();

			Log.log(Log.ERROR, "CPDAO", "getMemberIDForCGPAN()",
					"Error retrieving MemberId for the CGPAN!");
			throw new DatabaseException(sqlexception.getMessage());
		}

		finally {
			DBConnection.freeConnection(conn);
		}

		callableStmt.close();

		return mapping.findForward("success");
	}

	//--------------Say Cgpan reduction report---------------------------
	public ActionForward cgpanReductionEnhanceReport(ActionMapping mapping,
	        ActionForm form,
	        HttpServletRequest request,
	        HttpServletResponse response
	        )throws Exception {

	Log.log(Log.INFO,"ReportsAction","cgpanReductionEnhanceReport","Entered");
	DynaActionForm dynaForm = (DynaActionForm) form;
	dynaForm.initialize(mapping);
	Log.log(Log.INFO,"ReportsAction","cgpanReductionEnhanceReport","Exited");
	return mapping.findForward("successreduction");
	}
	    
	//---say----Cgpan Reduction report Details--------------------
	public ActionForward cgpanReductionReportDetails(ActionMapping mapping,
	        ActionForm form,
	        HttpServletRequest request,
	        HttpServletResponse response
	        )throws Exception {

	Log.log(Log.INFO,"ReportsAction","cagpanReductionReportDetails","Entered");
	ArrayList applicationReductionHistory   = new ArrayList();
	ArrayList applicationeEnhanceHistory   = new ArrayList();
	ArrayList cgpans            = new ArrayList();
	ApplicationReport appReport = new ApplicationReport();
	DynaActionForm dynaForm     = (DynaActionForm)form;
	String cgpan                = (String) dynaForm.get("enterCgpan");
	//  String ssiName              = (String) dynaForm.get("enterSSI");
	User user                   = getUserInformation(request);
	String bankId               = user.getBankId();
	String zoneId               = user.getZoneId();
	String branchId             = user.getBranchId();
	String memberId             = bankId + zoneId + branchId;
	//ReportDAO reportDao = new ReportDAO();

	//cgpans = reportManager.getAllCgpans();
	String newCgpan = cgpan.toUpperCase();
	applicationReductionHistory = reportDao.getCgpanReductionHistoryReportDetails(newCgpan);
	dynaForm.set("cgpanReductionHistoryReport",applicationReductionHistory);
	//System.out.println("newCgpan:"+newCgpan);
	//applicationeEnhanceHistory = rpDao.getCgpanEnhanceHistoryReportDetails(newCgpan);
	//dynaForm.set("cgpanEnhanceHistoryReport",applicationeEnhanceHistory);
	//System.out.println("newCgpan:"+newCgpan);
	appReport = reportManager.getReductionReportForCgpan(newCgpan);


	String key = appReport.getMemberId();
	  
	  if(key == null || key.equals(""))
	{
	throw new NoDataException("No Data is available for the values entered," +
	" Please Enter Any Other Value ");
	}
	dynaForm.set("applicationReport",appReport);
	return mapping.findForward("successreduction");

	}
	
	
	
	//## Added by Kailash
	// Added for colletral Hybrid Retail
	public ActionForward coletralHybridRetailReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		// dynaForm.set("dateOfTheDocument20", null);
		// dynaForm.set("dateOfTheDocument21", null);

		ArrayList list = getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("coletralInputPage");
	}

	private ArrayList getReportType() throws DatabaseException {
		Connection conn = null;
		Statement stmt = null;
		ArrayList reportType = new ArrayList();
		try {

			conn = DBConnection.getConnection();
			Statement pstmt = null;
			ResultSet rs = null;
			ArrayList list = new ArrayList();
			// stmt = connection.createStatement();
			stmt = conn.createStatement();
			String query = "select COLUMN_TAB from report_table_tab";

			// stmt = conn.createStatement();

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getString("COLUMN_TAB") != null) {
					reportType.add(rs.getString("COLUMN_TAB"));
				}
			}

		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getReportType()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return reportType;

	}

	public ActionForward coletralHybridRetailReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {

		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String flag = (String) dynaForm.getReportType();

		if (flag == null || flag.equals("")) {
			throw new MessageException("Please choose Report type.");
		} else {
			if (fromDt == null && toDt == null) {
				throw new MessageException("Please select Date.");
			} else {
				list = colletralHybridRetailReport(connection, flag,
						sqlfromdate, sqltodate);

			}
		}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("colletralReport");
	}

	private List colletralHybridRetailReport(Connection conn, String flag,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_colletral_report(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, flag);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(5);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				//System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}

	//##
	//rajuk
	public ActionForward actionHistoryReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
				
		
	//	ArrayList list=getReportType();
		//dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("coletralInputPage");
	}


	

	public ActionForward actionHistoryReportDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {

		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		String memberid=dynaForm.getMemberId();
		System.out.println("memberid=="+memberid);
		String cgpan=dynaForm.getCgpan();
		System.out.println("cgpan=="+cgpan);
		String type=dynaForm.getCheckValue();
		System.out.println("Type=="+type);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		
		if (  type==null || type.equals("") ){
			throw new MessageException("Please select Radio button. ");
		}
		if (fromDt == null || fromDt.equals("") ) {
			throw new MessageException("Please Select From Date. ");
		} else {
			if (toDt  == null || toDt.equals("")) {
				throw new MessageException("Please select ToDate.");
			} else {
				list = actionHistoryDetailReport(connection, memberid,cgpan,type,
						sqlfromdate, sqltodate);

			}
		}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("colletralReport");
	}

	private List actionHistoryDetailReport(Connection conn, String memberid,String cgpan,String type,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException {
		Log.log(Log.INFO, "reportaction", "actionHistoryDetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_ACCOUNT_HISTORY_report(?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, memberid);
			callableStmt.setString(5, cgpan);
			callableStmt.setString(6, type);
			callableStmt.registerOutParameter(7, Constants.CURSOR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(8);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(7);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}
	
	
	
	public ActionForward clmSettldReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		
		
		//ArrayList list=getReportType();
		//dynaForm.setReportTypeList(list);
		String memberId=null;
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId1 = bankId.concat(zoneId).concat(branchId);
		System.out.println("memberId"+memberId1);
		//general.setMemberId(memberId1);

		//dynaForm.set("memberId", memberId);
		return mapping.findForward("success");
	}
	
	
	
	public ActionForward claimSettledReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		
		System.out.println("entered== NEW REPORT");
		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		String memberid=dynaForm.getMemberId();
		System.out.println("memberid=="+memberid);
		//String cgpan=dynaForm.getCgpan();
		//System.out.println("cgpan=="+cgpan);
		String type=dynaForm.getCheckValue();
		System.out.println("Type=="+type);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		
		
			if (fromDt == null || fromDt.equals("")  && toDt == null || toDt.equals("") ) {
				throw new MessageException("Please select  Date.");
			} else {
				list = claimsettledReportDetail(connection, memberid, type,	sqlfromdate, sqltodate);

			
		}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		
		return mapping.findForward("claimsettleReport");
		
	}
		

	private List claimsettledReportDetail(Connection conn, String memberID,String type,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException {
		Log.log(Log.INFO, "reportaction", "actionHistoryDetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList claimdata = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call   CGTSIINTRANETUSER.FUN_CLAIM_SETTLED_REPORT(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, memberID);
			//callableStmt.setString(5, cgpan);
			callableStmt.setString(5, type);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(6);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				claimdata.add(0, coulmName);
				claimdata.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return claimdata;
	}
	
	//end rajuk
	
	//rajuk CLAIM REVIEW REPORT UAT
	/*
	public ActionForward clmSettldReportReview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		
		
		//	ArrayList list=getReportType();
			//dynaForm.setReportTypeList(list);

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			month = month - 1;
			day = day + 1;
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			Date prevdate = cal.getTime();

			GeneralReport general = new GeneralReport();
			general.setDateOfTheDocument20(prevdate);
			general.setDateOfTheDocument21(date);
			BeanUtils.copyProperties(dynaForm, general);
			return mapping.findForward("coletralInputPage");
	}
	
	
	
	public ActionForward clmSettldReportReviewNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		
		System.out.println("entered== NEW REPORT");
		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		String memberid=dynaForm.getMemberId();
		System.out.println("memberid=="+memberid);
		//String cgpan=dynaForm.getCgpan();
		//System.out.println("cgpan=="+cgpan);
		String type=dynaForm.getCheckValue();
		System.out.println("Type=="+type);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		
		
			if (fromDt == null || fromDt.equals("")  && toDt == null || toDt.equals("") ) {
				throw new MessageException("Please select  Date.");
			} else {
				list = clmSettldReportReviewNewDetail(connection, memberid, type,	sqlfromdate, sqltodate);

			
		}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		
		return mapping.findForward("claimsettleReport");
		
	}
		

	private List clmSettldReportReviewNewDetail(Connection conn, String memberID,String type,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException {
		Log.log(Log.INFO, "reportaction", "actionHistoryDetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList claimdata = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call   CGTSIINTRANETUSER.FUN_CLAIM_SETTLED_REPORT(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, memberID);
			//callableStmt.setString(5, cgpan);
			callableStmt.setString(5, type);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(6);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				claimdata.add(0, coulmName);
				claimdata.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return claimdata;
	}
	
	*/
	//end rajuk
	//Added for new report by sayali
	public ActionForward RevivalHistoryReportNewInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		// dynaForm.set("dateOfTheDocument20", null);
		// dynaForm.set("dateOfTheDocument21", null);

		ArrayList list=getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("RevivalInputNew");
	}
	
	public ActionForward RevivalHistoryReportDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		Date fromDt=null;
		Date toDt =null;
		ReportActionForm dynaForm = (ReportActionForm) form;
		fromDt =  dynaForm.getDateOfTheDocument20();
		System.out.println("fromDt---------" +fromDt);
		toDt = (Date) dynaForm.getDateOfTheDocument21();
		System.out.println("toDt---------" +toDt);
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String cgpan = (String) dynaForm.getCgpan();
		String mliid = (String) dynaForm.getMemberId();
		String flag = (String) dynaForm.getReportType();

//		if (flag == null || flag.equals("")) {
//			throw new MessageException("Please choose Report type.");
//		} else {
			if (fromDt!= null && !fromDt.equals(""))
			{
				list = RevivalHistoryReport(connection, cgpan,mliid,
						sqlfromdate, sqltodate);
			
			} else {
				
				throw new MessageException("Please select Date.");
			}
		//}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("RevivalReportDetail");
	}

	private List RevivalHistoryReport(Connection conn, String cgpan,String mliid,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_REVIVAL_HISTORY_report(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliid);
			callableStmt.setString(5, cgpan);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "RevivalHistoryReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(6);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "RevivalHistoryReport()",
					"Error Revival History Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}

	//ended by sayali
	
		//----------------------Live_outstanding_amount_report------------------------------
	public ActionForward LiveOutstandingReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		// dynaForm.set("dateOfTheDocument20", null);
		// dynaForm.set("dateOfTheDocument21", null);

		ArrayList list=getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("LiveOutstandingAmtInput");
	}
	
	public ActionForward LiveOutstandingReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		Date fromDt=null;
		Date toDt =null;
		ReportActionForm dynaForm = (ReportActionForm) form;
		fromDt =  dynaForm.getDateOfTheDocument20();
		System.out.println("fromDt---------" +fromDt);
		toDt = (Date) dynaForm.getDateOfTheDocument21();
		System.out.println("toDt---------" +toDt);
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String cgpan = (String) dynaForm.getCgpan();
		String mliid = (String) dynaForm.getMemberId();
		String flag = (String) dynaForm.getReportType();

//		if (flag == null || flag.equals("")) {
//			throw new MessageException("Please choose Report type.");
//		} else {
			if (fromDt!= null && !fromDt.equals(""))
			{
				list = LiveOutstandingAmtReport(connection, cgpan,mliid,
						sqlfromdate, sqltodate);
			
			} else {
				
				throw new MessageException("Please select Date.");
			}
		//}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("LiveOutstandingReportDetail");
	}

	private List LiveOutstandingAmtReport(Connection conn, String cgpan,String mliid,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_PM_77_DISTRICT_report(?,?,?,?)}");//Need to procedure change
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			//callableStmt.setString(4, mliid);
		//	callableStmt.setString(5, cgpan);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "LiveOutstandingAmtReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(4);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "LiveOutstandingAmtReport()",
					"Error Live Guarantee Amount Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}
	
	//----------------------PM_Demand_Destrict_Reportt------------------------------
	public ActionForward PMDemandDestrictReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		// dynaForm.set("dateOfTheDocument20", null);
		// dynaForm.set("dateOfTheDocument21", null);

		ArrayList list=getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("PMDemandDestrictReportInput");
	}
	
	public ActionForward PMDemandDestrictReportDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		Date fromDt=null;
		Date toDt =null;
		ReportActionForm dynaForm = (ReportActionForm) form;
		fromDt =  dynaForm.getDateOfTheDocument20();
		System.out.println("fromDt---------" +fromDt);
		toDt = (Date) dynaForm.getDateOfTheDocument21();
		System.out.println("toDt---------" +toDt);
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String cgpan = (String) dynaForm.getCgpan();
		String mliid = (String) dynaForm.getMemberId();
		String flag = (String) dynaForm.getReportType();

//		if (flag == null || flag.equals("")) {
//			throw new MessageException("Please choose Report type.");
//		} else {
			if (fromDt!= null && !fromDt.equals(""))
			{
				list = PMDemandDestrictReport(connection, cgpan,mliid,
						sqlfromdate, sqltodate);
			
			} else {
				
				throw new MessageException("Please select Date.");
			}
		//}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("PMDemandDestrictReportDetail");
	}

	private List PMDemandDestrictReport(Connection conn, String cgpan,String mliid,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "PMDemandDestrictReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_PM_77_DISTRICT_report(?,?,?,?)}");//Need to procedure change
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			//callableStmt.setString(4, mliid);
			//callableStmt.setString(5, cgpan);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "PMDemandDestrictReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(4);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "PMDemandDestrictReport()",
					"Error PM Demand Destrict Report Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}
	
	
	//new raju
	public ActionForward claimAccountReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		
	/*	ArrayList list=getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();*/
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		GeneralReport general = new GeneralReport();
		
		general.setMemberId(memberId);
		//general.setDateOfTheDocument21(date);
		//BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("coletralInputPage");
	}

	

	public ActionForward claimAccountReportDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		
	///	System.out.println("entered 1====");

		ReportActionForm dynaForm = (ReportActionForm) form;
		/*Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());*/
		String memid = (String) dynaForm.getMemberId();
		//System.out.println("memid==="+memid);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		//String flag = (String) dynaForm.getReportType();

	
			if (memid == null && memid == null) {
				throw new MessageException("Please select  member id.");
			} else {
				list = claimAccountReportDetails(connection, memid);

			}
		//}

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("colletralReport");
	}

	private List claimAccountReportDetails(Connection conn, String memid)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
		//System.out.println("entered 2 ====");
		CallableStatement callableStmt = null;
		//System.out.println("memid=="+memid);
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_CLAIM_ACCOUNT_report(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setString(2, memid);
	    	callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(3);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}
	public ActionForward  accountDetailHistoryReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {
		System.out.println("claimAccountDetailReportHistory  entered====");

		ReportActionForm dynaForm = (ReportActionForm) form;
	
		request.setAttribute("CLAIMREFNO", request.getParameter("clmRefNumber"));
	
		String memberid = request.getParameter("clmRefNumber");
		System.out.println("memberid=="+memberid);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		
		list = claimAccountReportHistorydetail(connection, memberid);
	
	

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("colletralReport1");
	}
	
	private List claimAccountReportHistorydetail(Connection conn, String memberid)
	throws DatabaseException {
Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
System.out.println("entered 2 ====");
CallableStatement callableStmt = null;
System.out.println("memid=="+memberid);
// Connection conn = null;
ResultSet resultset = null;
ResultSetMetaData resultSetMetaData = null;
ArrayList coulmName = new ArrayList();
ArrayList nestData = new ArrayList();
ArrayList colletralData = new ArrayList();
int status = -1;
String errorCode = null;
try {
	conn = DBConnection.getConnection();
	callableStmt = conn
			.prepareCall("{?=call CGTSIINTRANETUSER.Fun_CLAIM_ACCOUNT_history(?,?,?)}");
	callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

	callableStmt.setString(2, memberid);
	callableStmt.registerOutParameter(3, Constants.CURSOR);
	callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

	callableStmt.execute();
	status = callableStmt.getInt(1);
	errorCode = callableStmt.getString(4);

	if (status == Constants.FUNCTION_FAILURE) {
		Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
				"SP returns a 1. Error code is :" + errorCode);
		callableStmt.close();
		throw new DatabaseException(errorCode);
	} else if (status == Constants.FUNCTION_SUCCESS) {
		resultset = (ResultSet) callableStmt.getObject(3);

		resultSetMetaData = resultset.getMetaData();
		int coulmnCount = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= coulmnCount; i++) {
			coulmName.add(resultSetMetaData.getColumnName(i));
		}

		while (resultset.next()) {

			ArrayList columnValue = new ArrayList();
			for (int i = 1; i <= coulmnCount; i++) {
				columnValue.add(resultset.getString(i));
			}

			nestData.add(columnValue);

		}
		System.out.println("list data " + nestData);
		colletralData.add(0, coulmName);
		colletralData.add(1, nestData);
	}
	resultset.close();
	resultset = null;
	callableStmt.close();
	callableStmt = null;
	resultSetMetaData = null;
} catch (SQLException sqlexception) {
	Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
			"Error retrieving all colletral data!");
	throw new DatabaseException(sqlexception.getMessage());
} finally {
	DBConnection.freeConnection(conn);
}
return colletralData;
}
	
	public ActionForward asfSummeryMliwiseDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Entered");
		System.out.println("RRR====RRAAA++");
		Connection connection = DBConnection.getConnection();
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument36");
		Date todate = (Date) dynaForm.get("dateOfTheDocument37");
		
		String  dantype = (String) dynaForm.get("dantype");
		System.out.println("dantype=="+dantype);
		// System.out.println((new
		// StringBuilder()).append("fromdate;").append(fromdate).append(" todate:").append(todate).toString());
		String number = request.getParameter("num");
		String AsfMLIStringArray[] = null;
		ArrayList asfSummeryMLIDetailsArray = new ArrayList();
		query = (new StringBuilder())
				.append("SELECT p.PMR_BANK_ACCOUNT_NO ,c.DAN_ID, a.CGPAN,d.SSI_UNIT_NAME, b.DCI_AMOUNT_RAISED,decode(b.DCI_APPROPRIATION_FLAG,'N','Not Paid' ,'P','NOT PAID', 'Yes') , b.DCI_REMARKS , a.APP_STATUS,app_mli_branch_name,IGST_AMT,CGST_AMT,SGST_AMT,DCI_BASE_AMT, R_AMT,EFFRATE_FIN  FROM asf_yearly_dan ay,application_detail a, dan_cgpan_info b ,demand_advice_info c , ssi_detail d,PROMOTER_DETAIL p where C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID=")
				.append(number)
				.append(" and a.SSI_REFERENCE_NUMBER = d.SSI_REFERENCE_NUMBER")
				.append(" and b.dan_id = ay.dan_id(+) ")
				.append(" and a.SSI_REFERENCE_NUMBER = p.SSI_REFERENCE_NUMBER")
				.append(" and a.CGPAN = b.CGPAN and c.DAN_ID = b.dan_id and c.DAN_TYPE ='"+dantype+"'")
				.append(" and (DCI_AMOUNT_RAISED-NVL(DCI_AMOUNT_CANCELLED,0))>0 and trunc(c.dan_generated_dt) between to_date('")
				.append(fromdate)
				.append("','dd/mm/yyyy')")
				.append("AND to_date('")
				.append(todate)
				.append("','dd/mm/yyyy') order by 1,app_mli_branch_name,2,3, 4")
				.toString();
		System.out.println("query=="+query);
		try {
			Statement asfSummeryMLIDetailsStmt = null;
			ResultSet asfSummeryMLIDetailsResult = null;
			asfSummeryMLIDetailsStmt = connection.createStatement();
			for (asfSummeryMLIDetailsResult = asfSummeryMLIDetailsStmt
					.executeQuery(query); asfSummeryMLIDetailsResult.next(); asfSummeryMLIDetailsArray
					.add(AsfMLIStringArray)) {
				AsfMLIStringArray = new String[15];
				AsfMLIStringArray[0] = asfSummeryMLIDetailsResult.getString(1);
				AsfMLIStringArray[1] = asfSummeryMLIDetailsResult.getString(2);
				AsfMLIStringArray[2] = asfSummeryMLIDetailsResult.getString(3);
				AsfMLIStringArray[3] = asfSummeryMLIDetailsResult.getString(4);
				AsfMLIStringArray[4] = asfSummeryMLIDetailsResult.getString(5);
				AsfMLIStringArray[5] = asfSummeryMLIDetailsResult.getString(6);
				AsfMLIStringArray[6] = asfSummeryMLIDetailsResult.getString(7);
				AsfMLIStringArray[7] = asfSummeryMLIDetailsResult.getString(8);
				AsfMLIStringArray[8] = asfSummeryMLIDetailsResult.getString(9);
				AsfMLIStringArray[9] = asfSummeryMLIDetailsResult.getString(10);
				AsfMLIStringArray[10] = asfSummeryMLIDetailsResult.getString(11);
				AsfMLIStringArray[11] = asfSummeryMLIDetailsResult.getString(12);
				AsfMLIStringArray[12] = asfSummeryMLIDetailsResult.getString(13);
				AsfMLIStringArray[13] = asfSummeryMLIDetailsResult.getString(14);
				AsfMLIStringArray[14] = asfSummeryMLIDetailsResult.getString(15);
			}

			asfSummeryMLIDetailsResult.close();
			asfSummeryMLIDetailsResult = null;
			asfSummeryMLIDetailsStmt.close();
			asfSummeryMLIDetailsStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("asfSummeryMLIDetailsArray",
				asfSummeryMLIDetailsArray);
		request.setAttribute("asfSummeryMLIDetailsArray_size", (new Integer(
				asfSummeryMLIDetailsArray.size())).toString());
		Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Exited");
		return mapping.findForward("success2");
	}
	
	//FOR LIVE OUTSTANDING REPORT
	public ActionForward liveoutstandingamountReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		// dynaForm.set("dateOfTheDocument20", null);
		// dynaForm.set("dateOfTheDocument21", null);

		
		
		
		
		ArrayList list=getReportType();
		dynaForm.setReportTypeList(list);

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		Date prevdate = cal.getTime();

		GeneralReport general = new GeneralReport();
		general.setDateOfTheDocument20(prevdate);
		general.setDateOfTheDocument21(date);
		BeanUtils.copyProperties(dynaForm, general);
		return mapping.findForward("coletralInputPage");
	}

	

	public ActionForward liveoutstandingamountReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {

		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String flag = (String) dynaForm.getReportType();

		
			if (fromDt == null && toDt == null) {
				throw new MessageException("Please select Date.");
			} else {
				list = liveoutstandingamountReports(connection, flag,
						sqlfromdate, sqltodate);

			}
	

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("colletralReport");
	}

	private List liveoutstandingamountReports(Connection conn, String flag,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "colletralHybridRetailReport()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList colletralData = new ArrayList();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.FUN_LIVE_OUTSTANDING_REPORT(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
		//	callableStmt.setString(4, flag);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(4);

				resultSetMetaData = resultset.getMetaData();
				int coulmnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= coulmnCount; i++) {
					coulmName.add(resultSetMetaData.getColumnName(i));
				}

				while (resultset.next()) {

					ArrayList columnValue = new ArrayList();
					for (int i = 1; i <= coulmnCount; i++) {
						columnValue.add(resultset.getString(i));
					}

					nestData.add(columnValue);

				}
				System.out.println("list data " + nestData);
				colletralData.add(0, coulmName);
				colletralData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
					"Error retrieving all colletral data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return colletralData;
	}
	
	
//Added by Diksha
	
	
	
	
	public ActionForward clmSettledExcelReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		
		ReportActionForm dynaForm = (ReportActionForm) form;
		Log.log(4, "NewReportsAction", "Claim settled", "Entered");
		HttpSession sess = request.getSession();
		System.out.println("entered== NEW REPORT");
		
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		String memberid=dynaForm.getMemberId();
		//String memberid = (String) dynaForm.get("memberId");
		System.out.println("memberid=="+memberid);
		//String cgpan=dynaForm.getCgpan();
		//System.out.println("cgpan=="+cgpan);
		String type = request.getParameter("checkValue");
		//String type=dynaForm.getCheckValue();
		System.out.println("Type=="+type);
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List clmSettledExcelReport = new ArrayList();
		String flag=request.getParameter("Flag");
		System.out.println("Flag---"+flag);
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		String fileType = request.getParameter("fileType");
		
		System.out.println("fileType----"+fileType);
		String FlowLevel = request.getParameter("FlowLevel");
		//String d=(String)request.getAttribute("FileReport");
		//System.out.println("d--"+d);
		System.out.println("@@@@@@@@@@@@FlowLevel :" + FlowLevel);
		
		if (memberid.length() == 12 && !memberids.contains(memberid)) {
			Log.log(2, "CPDAO", "getAllMemberIds()",
					"No Member Ids in the database!");
			throw new DatabaseException("No Member Ids in the database");
		}
		
		
		if (sqlfromdate == null && sqltodate == null) 
		{
				throw new MessageException("Please select Date.");
		}
		if(flag!=null && flag.equals("A"))
				{
				clmSettledExcelReport = claimsettledReportDetail(connection, memberid, type,	sqlfromdate, sqltodate);
				System.out.println("FileReport LIST----"+clmSettledExcelReport.size());
				sess.setAttribute("ClmSettledExcelReport", clmSettledExcelReport);
				//RPAction r=new RPAction();
				//r.bulkUploadReportFile(mapping, dynaForm, request, response);
				this.clmSettledExportToFile(mapping, dynaForm, request, response);
				if(clmSettledExcelReport==null||clmSettledExcelReport.equals("")){
					throw new MessageException("Data not available");
				}
				else{
					
				dynaForm.setColletralCoulmnName((ArrayList) clmSettledExcelReport.get(0));
				dynaForm.setColletralCoulmnValue((ArrayList) clmSettledExcelReport.get(1));
				}	
			}
		
		return mapping.findForward("success");
	}
	
	public ActionForward clmSettledExportToFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OutputStream os = response.getOutputStream();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String strDate = sdf.format(cal.getTime());

		// System.out.println("ExportToFile Calling..");

		String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
		String contextPath = PropertyLoader.changeToOSpath(contextPath1);

		// System.out.println("contextPath1 :"+contextPath1);
		// System.out.println("contextPath :"+contextPath);

		HttpSession sess = request.getSession(false);
		String fileType = request.getParameter("fileType");
		String FlowLevel = request.getParameter("FlowLevel");

		System.out.println("@@@@@@@@@@@@FlowLevel :" + FlowLevel);
		// ArrayList ClmDataList =
		// (ArrayList)sess.getAttribute("ClaimSettledDatalist");
		ArrayList ClmDataList = (ArrayList) sess.getAttribute(FlowLevel);
		//System.out.println("@@@@@@@@@@@@ClmDataList:" + ClmDataList);
		ArrayList HeaderArrLst = (ArrayList) ClmDataList.get(0);
		//System.out.println("@@@@@@@@@@@@HeaderArrLst:" + HeaderArrLst);
		int NoColumn = HeaderArrLst.size();

		// System.out.println("fileType:"+fileType);

		if (fileType.equals("CSVType")) {
			byte[] b = generateCSV(ClmDataList, NoColumn, contextPath);

			if (response != null)
				response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					"attachment; filename=ClmSettledExcelData" + strDate
							+ ".csv");
			os.write(b);
			os.flush();
		}
		 if(fileType.equals("XLSType"))
	        {
	            System.out.println("XLS====File==");
	            byte b[] = generateEXL(ClmDataList, NoColumn, contextPath);
	            if(response != null)
	                response.setContentType("APPLICATION/OCTET-STREAM");
	            response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=ExcelData")).append(strDate).append(".xls").toString());
	            os.write(b);
	            os.flush();
	        }
		return null;
	}

	public byte[] generateCSV(ArrayList<ArrayList> ParamDataList,
			int No_Column, String contextPath) throws IOException {

		System.out.println("---generateCSV()---");
		StringBuffer strbuff = new StringBuffer();
		//System.out.println("ParamDataList:" + ParamDataList);
		//System.out.println("contextPath :" + contextPath);
		ArrayList<String> rowDataLst = new ArrayList<String>();
		ArrayList<String> HeaderLst = (ArrayList) ParamDataList.get(0);
		ArrayList<ArrayList> RecordWiseLst = (ArrayList) ParamDataList.get(1);
		//System.out.println("HeaderLst" + HeaderLst);
		//System.out.println("RecordWiseLst" + RecordWiseLst);
		// #### Header List
		for (String headerdata : HeaderLst) {
			rowDataLst.add(headerdata);
			//System.out.println("Loop--headerdata:" + headerdata);
		}
		//System.out.println("rowDataLst:" + rowDataLst);
		// #### Header List

		// #### Data List
		for (ArrayList<String> RecordWiseLstObj : RecordWiseLst) {
			//System.out.println("RecordWiseLstObj:" + RecordWiseLstObj);
			for (String SingleRecordDataObj : RecordWiseLstObj) {
				//System.out.println("DataLstInnerObj :" + SingleRecordDataObj);
				if (null != SingleRecordDataObj) {
					// rowDataLst.add(SingleRecordDataObj.replace("<b>","").replace("</b>",""));
					rowDataLst.add(SingleRecordDataObj.replace("<b>", "")
							.replace("</b>", ""));
				} else {
					rowDataLst.add(SingleRecordDataObj);
				}
			}
			// System.out.println("DataLstObj :"+DataLstObj);
		}
		//System.out.println("rowDataLst::" + rowDataLst);
		// #### Data List

		ArrayList FinalrowDatalist = new ArrayList<String>();
		//System.out.println("1");
		int y = 0;
		// System.out.println("2"+No_Column);
		for (int n = 0; n < rowDataLst.size(); n++) {

			if (n % No_Column == 0 && n != 0) {
				FinalrowDatalist.add(rowDataLst.get(n));
				FinalrowDatalist.add(n + y, "\n");
				// System.out.println("2n value inside if:"+n);
			//	System.out.println("n:" + n);
				y++;
			} else {
				// System.out.println("2n inside else:"+n);
				if (null != rowDataLst.get(n)) {
					if (rowDataLst.get(n).contains(",")) {
						rowDataLst.set(n, rowDataLst.get(n).replace(",", ";"));
					}
				}
				FinalrowDatalist.add(rowDataLst.get(n));
			}
			// System.out.println("rowDataLst.get "+rowDataLst.get(n)+"    "+n%3);
		}
		// System.out.println("rowDataLst :"+rowDataLst.toString().replace("\n,","\n"));
		// String tempStr = rowDataLst.toString().replace("\n,", "\n");
		//System.out.println("3");

		String tempStr = FinalrowDatalist.toString().replace("\n,", "\n").replace(" ,", ",").replace(", ", ",");
		// String tempStr = FinalrowDatalist.toString().replace("\n,", "\n");

		//System.out.println("4");
		// strbuff.append(ParamDataList.toString().substring(2,
		// ParamDataList.toString().length() - 2).replace("endrow,", "\n"));
		strbuff.append(tempStr.substring(1, tempStr.length() - 1));
		// System.out.println("strbuff :"+strbuff);
		///System.out.println("5");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String strDate = sdf.format(cal.getTime());
		BufferedWriter output = null;
		OutputStream outStrm;
		// File genfile = new File("D:\\GenerateFiles\\SampleFile" + strDate+
		// ".csv");
		File genfile = new File(contextPath + "\\Download\\DataCSVFile"
				+ strDate + ".csv");

		//System.out.println("6");
		output = new BufferedWriter(new FileWriter(genfile));
		output.write(strbuff.toString());
		//System.out.println("7");
		output.flush();
		output.close();
		//System.out.println("8");

		// ##
		// FileInputStream fis = new
		// FileInputStream("D:\\GenerateFiles\\SampleFile" + strDate+ ".csv");
		FileInputStream fis = new FileInputStream(contextPath
				+ "\\Download\\DataCSVFile" + strDate + ".csv");

		//System.out.println("9");
		byte b[];
		int x = fis.available();
		b = new byte[x];
		// System.out.println(" b size"+b.length);

		fis.read(b);
		// ##
		return b;
		// genfile.setReadOnly();
	}
	
	//Working now
	public byte[] generateEXL(ArrayList<ArrayList> ParamDataList,int No_Column, String contextPath) throws DocumentException,
	IOException {
				System.out.println("---generateEXL()---");
				StringBuffer strbuff = new StringBuffer();
				//System.out.println("ParamDataList:" + ParamDataList);
				ArrayList<String> rowDataLst = new ArrayList<String>();
				ArrayList<String> HeaderLst = (ArrayList) ParamDataList.get(0);
				ArrayList<ArrayList> RecordWiseLst = (ArrayList) ParamDataList.get(1);
				
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("Data1");
				
				DataFormat format = workbook.createDataFormat();
				HSSFCellStyle style = workbook.createCellStyle();
				style.setDataFormat(format.getFormat("#,##0.00"));
				
				// #### Header List Wrinting
				Row row = sheet.createRow(0);	
				int hdcolnum = 0;
				for (String headerdata : HeaderLst) {
					Cell cell = row.createCell(hdcolnum);
					cell.setCellValue(headerdata);
					hdcolnum++;
				}
				// #### Header List Wrinting
				
				
				// #### Data List Writing
				//Existing below Code is Modified By Parmanand 05-Feb-2020 Start
					int rownum = 1;
						for (ArrayList<String> RecordWiseLstObj : RecordWiseLst) {
								int colnum = 0;
								row = sheet.createRow(rownum);			
								for (String SingleRecordDataObj : RecordWiseLstObj) {
									Cell cell = row.createCell(colnum);
										if(colnum==5||colnum==6||colnum==7||colnum==8||colnum==9||colnum==10||colnum==11||colnum==12||colnum==13||colnum==20||colnum==22||colnum==23){
											if(SingleRecordDataObj!=null && SingleRecordDataObj.length()>0 ){
												//To set the excel sheet cell as from strign to number
												cell.setCellType(cell.CELL_TYPE_NUMERIC);
												cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
												colnum++;			
												rowDataLst.add(SingleRecordDataObj);
											}else{
												double defaultDVal=0.00;
												cell.setCellValue(defaultDVal);
												colnum++;			
												rowDataLst.add(SingleRecordDataObj);
											}
										}else{
											//cell.setCellType(cell.CELL_TYPE_STRING);
											cell.setCellValue(SingleRecordDataObj);
											colnum++;			
											rowDataLst.add(SingleRecordDataObj);
									}
								}
								rownum++;
								System.out.println("rownum==="+rownum);
					
						}
				//Existing above Code is Modified By Parmanand 05-Feb-2020 End
				// #### Data List Writing
				
				
				 
				
				
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
				String strDate = sdf.format(cal.getTime());
				try {			
					FileOutputStream out = new FileOutputStream(new File(contextPath+ "\\Download\\DataCSVFile" + strDate + ".xls"));
					workbook.write(out);
					out.close();
					//System.out.println("Excel written successfully..");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
				FileInputStream fis = new FileInputStream(contextPath+ "\\Download\\DataCSVFile" + strDate + ".xls");
				//System.out.println("9");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				// System.out.println(" b size"+b.length);
				fis.read(b);		
				return b;
}
	
	// Recovery report added by DKR   DNEW
	public ActionForward claimRecoveryReportInput(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Log.log(4, "ReportsAction", "claimRecoveryReportInput", "Entered");			
			System.out.println("entereddd==");
											
			DynaActionForm dynaForm = (DynaActionForm) form;
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId.concat(zoneId).concat(branchId);

			/*	java.util.Date date = new java.util.Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(2);
			int day = calendar.get(5);
			month -= 1;
			day += 1;
			calendar.set(2, month);
			calendar.set(5, day);
			java.util.Date prevDate = calendar.getTime();

			GeneralReport generalReport = new GeneralReport();
			generalReport.setDateOfTheDocument18(prevDate);
			generalReport.setDateOfTheDocument19(date);
			BeanUtils.copyProperties(dynaForm, generalReport);				
			dynaForm.set("bankId", bankId);

			if (bankId.equals("0000")) {
				memberId = "";
			}
			dynaForm.set("memberId", memberId);	*/			
			

			return mapping.findForward("success");
		}
		
		// DKR RCV REPORT	DNEW
		public ActionForward claimRecoveryReport(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws MessageException,Exception {
			
     	DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date sDate = null;
		java.util.Date eDate = null;
		String mliId="00000";
		String indMliId="00000";
		String bankIdType="";
		sDate = (java.util.Date) dynaForm.get("dateOfTheDocument18");	
		
		String stDate = String.valueOf(sDate);	
		if (stDate.length()>0){
				startDate = new java.sql.Date(sDate.getTime());	
		}
		
		eDate = (java.util.Date) dynaForm.get("dateOfTheDocument19");
       if (eDate.toString().length()>0){
    	   endDate = new java.sql.Date(eDate.getTime());
		}
		
		if(dynaForm.get("bankName").toString().length()>0 && null!=dynaForm.get("bankName").toString()){
		 mliId = (String) dynaForm.get("bankName");
		 bankIdType="BANK_NAME_TYPE";
		 System.out.println(bankIdType+"mliId=>>>>>BANK_NAME_TYPE>>>>1>>>>>>>>>>=="+mliId);
		}
		
		//String indMliId=request.getParameter("bank_id");
		if(request.getParameter("bank_id").toString().length()>0 && null!=request.getParameter("bank_id").toString()){
			indMliId=request.getParameter("bank_id");
			bankIdType="BANK_ID_TYPE";
			 System.out.println(bankIdType+"indMliId=>>>>>>>BANK_ID_TYPE>>>>2>>>>>>>>=="+indMliId);
			}
		
		  
		 if((stDate.length()>0 && eDate.toString().length()>0) || ( mliId.length()>0)  || ( indMliId.length()>0)) {
			                                                    
		    ArrayList recoveryDisbursement = this.reportManager.getClaimRecoveryReport(sDate,eDate, mliId,indMliId,bankIdType);	
		    System.out.println(eDate+" eDate  sDate::::"+indMliId+":::::::::::::::::"+sDate);
			
			if (recoveryDisbursement.size() > 0) {
				dynaForm.set("recoveryDisbursement", recoveryDisbursement);				
			}else{
				throw new NoDataException(	"Invalid input!.. <br/>Data are not available.");
	    	}
		 }
		return mapping.findForward("success");	
	  }
		
// Recovery Created by DKR  
		
		public ActionForward recoverySettledExcelReportNew(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			DynaActionForm dynaForm = (DynaActionForm) form;
			java.sql.Date startDate = null;
			java.sql.Date endDate = null;
			java.util.Date sDate = null;
			java.util.Date eDate = null;
			String mliId="00000";
			String indMliId="00000";
			String bankIdType="";
			sDate = (java.util.Date) dynaForm.get("dateOfTheDocument18");	
			//  Created by DKR 
			String stDate = String.valueOf(sDate);	
			//System.out.println("stDate==="+stDate);
			//System.out.println("stDate length==="+stDate.length());
			if (stDate.toString().length()>0){
					startDate = new java.sql.Date(sDate.getTime());	
					//System.out.println("startDate==="+startDate);
			}
			
			eDate = (java.util.Date) dynaForm.get("dateOfTheDocument19");
			//System.out.println("eDate==="+eDate);
			//System.out.println("eDate==="+eDate.toString());
	       if (eDate.toString().length()>0){
	    	   endDate = new java.sql.Date(eDate.getTime());
			}
	      // System.out.println("bankName==="+dynaForm.get("bankName"));
	      // System.out.println("bankName==="+dynaForm.get("bankName").toString());
	      // System.out.println("bankName lenght==="+dynaForm.get("bankName").toString().length());
			if(dynaForm.get("bankName").toString().length()>0 && null!=dynaForm.get("bankName").toString()){
			 mliId = (String) dynaForm.get("bankName");
			 //System.out.println("mliId==="+mliId);
			 bankIdType="BANK_NAME_TYPE";
			// System.out.println(bankIdType+"mliId=>>>>>BANK_NAME_TYPE>>>>111>>>>>>>>>>=="+mliId);
			}
			
			//String indMliId=request.getParameter("bank_id");
			//System.out.println("bank_id==="+request.getParameter("bank_id").toString());
			//System.out.println("bank_id==="+request.getParameter("bank_id").toString().length());
			if(request.getParameter("bank_id").toString().length()>0 && null!=request.getParameter("bank_id").toString()){
				indMliId=request.getParameter("bank_id");
				bankIdType="BANK_ID_TYPE";
				//System.out.println(bankIdType+"indMliId=>>>>>>>BANK_ID_TYPE>>>>222>>>>>>>>=="+indMliId);
			}
			
			 if((stDate.toString().length()>0 && eDate.toString().length()>0) || ( mliId.length()>0)  || ( indMliId.length()>0)) {
				                                                    
				ArrayList<RecoveryReport> RecordWiseLst = this.reportManager.getClaimRecoveryReport(sDate,eDate, mliId.trim(),indMliId.trim(),bankIdType);	
				// System.out.println(bankIdType+"indMliId=>>>>>>>BANK_ID_TYPE>>>>2>>>>>>>>=="+indMliId);
				if (RecordWiseLst.size() > 0) {
			try{				
				 ArrayList<RecoveryReport> rowDataLst = new ArrayList<RecoveryReport>();
				 String columns[] = {"MLI Id", "MLI Name", "Zone Name", "Unit Name", "Cgpan No.","Guarantee Amount", "1st Installment Amount", "Recovery Type", "Recovery Received Date","Payment Date","Virtual A/C No.","Recovery Received Amount"};
					 
				 ArrayList<String> HeaderLst= new ArrayList<String>(Arrays.asList(columns));
		         String contextPath_dkr = request.getSession(false).getServletContext().getRealPath("");
				 String contextPath = PropertyLoader.changeToOSpath(contextPath_dkr);
				 HSSFWorkbook workbook = new HSSFWorkbook();
				 HSSFSheet sheet = workbook.createSheet("Recovery Report");
				
				DataFormat format = workbook.createDataFormat();
				HSSFCellStyle style = workbook.createCellStyle();
				style.setDataFormat(format.getFormat("#,##0.00"));
				
				Row row = sheet.createRow(0);	
				int hdcolnum = 0;
				for (String headerdata : HeaderLst) {
					Cell cell = row.createCell(hdcolnum);
					style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
					style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
					Font font = workbook.createFont();
					font.setFontName("Arial");
					font.setColor(IndexedColors.WHITE.getIndex());
					font.setBoldweight(Font.BOLDWEIGHT_BOLD); 					
					style.setFont(font);
					cell.setCellStyle(style); 
					cell.setCellValue(headerdata);
					hdcolnum++;
				}
					int rownum = 1;
				    double recoveryTotalAmt=0.0d;
					for (RecoveryReport obj1 : RecordWiseLst) {
								int colnum = 0;
								row = sheet.createRow(rownum);	
								row.createCell((short) 0).setCellValue(obj1.getMliId());// Done								
								row.createCell((short) 1).setCellValue(obj1.getBankName());// Done
								row.createCell((short) 2).setCellValue(obj1.getZoneName());// Done
								row.createCell((short) 3).setCellValue(obj1.getUnitName());// Done
								row.createCell((short) 4).setCellValue(obj1.getCgpanNumber());// Done
								row.createCell((short) 5).setCellValue(obj1.getGuaranteedAmt());// Done
								row.createCell((short) 6).setCellValue(obj1.getRecoRecvAmt());// Done 6
								row.createCell((short) 7).setCellValue(obj1.getRecoveryType());// Done 7
								row.createCell((short) 8).setCellValue(new java.text.SimpleDateFormat("dd/MM/yyyy").format(obj1.getDateRecovRecvMli()));
								row.createCell((short) 9).setCellValue(new java.text.SimpleDateFormat("dd/MM/yyyy").format(obj1.getDdDate()));// Done
								row.createCell((short) 10).setCellValue(obj1.getDdUtrNo());// Done 11
								row.createCell((short) 11).setCellValue(obj1.getRecoRecvAmt());
								recoveryTotalAmt=  recoveryTotalAmt + obj1.getRecoRecvAmt();
								colnum++;														
								rownum++;	
						}					   
						//row.createCell((short) 12).setCellValue(recoveryTotalAmt);// 
					//System.out.println("rownum==="+rownum);	
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
				String strDate = "Recovery".concat(sdf.format(cal.getTime()));
				try {			
					FileOutputStream out = new FileOutputStream(new File(contextPath+ "\\Download\\DataCSVFile" + strDate + ".xls"));
					workbook.write(out);
					out.close();
					//System.out.println("Excel written successfully..");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				OutputStream os = response.getOutputStream();
				FileInputStream fis = new FileInputStream(new File(contextPath+ "\\Download\\DataCSVFile" + strDate + ".xls"));
				//System.out.println("9");
				
				int x = fis.available();
				byte b[] = new byte[x];
				System.out.println(" b size"+b.length);
				fis.read(b);		
				 if(response != null)
		              response.setContentType("APPLICATION/OCTET-STREAM");
		          response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=ExcelData")).append(strDate).append(".xls").toString());
		          os.write(b);
		          os.flush();
			}catch(IOException io){
				io.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}		
		   }else{
				throw new NoDataException("Data are not available.");
	    	}
		  }else{
				throw new NoDataException(	"Invalid input!.. ");
	    	}			
			return null;
		}
		
		/*public ActionForward outstandingReportInputNew(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			ReportActionForm dynaForm = (ReportActionForm) form;

		
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			month = month - 1;
			day = day + 1;
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			Date prevdate = cal.getTime();
			
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId.concat(zoneId).concat(branchId);
			GeneralReport general = new GeneralReport();
			general.setDateOfTheDocument20(prevdate);
			general.setDateOfTheDocument21(date);
			//general.setMemberId(memberId);
			BeanUtils.copyProperties(dynaForm, general);
		
			return mapping.findForward("coletralInputPage");
		}

		
		
		
			public ActionForward outstandingNewReportDetail(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws DatabaseException, Exception {

			ReportActionForm dynaForm = (ReportActionForm) form;
			Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
			Date toDt = (Date) dynaForm.getDateOfTheDocument21();
			java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
			java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
			
			
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId.concat(zoneId).concat(branchId);
			
			String memberid = dynaForm.getMemberId();
			 System.out.println("memberid=="+memberid);
			 String bnkId = memberid.substring(0, 4);
				String zneId = memberid.substring(4, 8);
				String brnId = memberid.substring(8, 12);
			System.out.println("sqltodate=="+sqltodate);
			System.out.println("sqlfromdate=="+sqlfromdate);
			String cgpan = dynaForm.getCgpan();
			// System.out.println("cgpan=="+cgpan);
			String type = dynaForm.getCheckValue();
			// System.out.println("Type=="+type);
			Connection connection = null;
			Statement stmt = null;
			ResultSet rs = null;
			String query = null;
			List list = new ArrayList();
			
			if (memberid == null || memberid.equals("")) 
			{
				throw new MessageException("Please Enter MLI ID .");
			} 
			
			
				list = outstandingNewDetailReport(connection, memberid,sqltodate);
			
			
			

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

			return mapping.findForward("colletralReport");
		}

		
		
		private List outstandingNewDetailReport(Connection conn, String memberid,java.sql.Date sqltodate) throws DatabaseException {
			Log.log(Log.INFO, "reportaction", "outstandingNewDetailReport()",
					"Entered!");
			CallableStatement callableStmt = null;
			// Connection conn = null;
			ResultSet resultset = null;
			ResultSetMetaData resultSetMetaData = null;
			ArrayList coulmName = new ArrayList();
			ArrayList nestData = new ArrayList();
			ArrayList colletralData = new ArrayList();
			int status = -1;
			String errorCode = null;
			try {
				conn = DBConnection.getConnection();
				callableStmt = conn
						.prepareCall("{?=call CGTSITEMPUSER.Fun_CLAIM_OS_report(?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

				
				callableStmt.setString(2, memberid);
				callableStmt.setDate(3, sqltodate);
				
				callableStmt.registerOutParameter(4, Constants.CURSOR);
				callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(5);

				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
							"SP returns a 1. Error code is :" + errorCode);
					callableStmt.close();
					throw new DatabaseException(errorCode);
				} else if (status == Constants.FUNCTION_SUCCESS) {
					resultset = (ResultSet) callableStmt.getObject(4);

					resultSetMetaData = resultset.getMetaData();
					int coulmnCount = resultSetMetaData.getColumnCount();
					for (int i = 1; i <= coulmnCount; i++) {
						coulmName.add(resultSetMetaData.getColumnName(i));
					}

					while (resultset.next()) {

						ArrayList columnValue = new ArrayList();
						for (int i = 1; i <= coulmnCount; i++) {
							columnValue.add(resultset.getString(i));
						}

						nestData.add(columnValue);

					}
					// System.out.println("list data " + nestData);
					colletralData.add(0, coulmName);
					colletralData.add(1, nestData);
				}
				resultset.close();
				resultset = null;
				callableStmt.close();
				callableStmt = null;
				resultSetMetaData = null;
			} catch (SQLException sqlexception) {
				Log.log(Log.ERROR, "CPDAO", "colletralHybridRetailReport()",
						"Error retrieving all colletral data!");
				throw new DatabaseException(sqlexception.getMessage());
			} finally {
				DBConnection.freeConnection(conn);
			}
			return colletralData;
		}
		*/
		
		
		
		
		
		
		public ActionForward asfSummeryReport(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Log.log(4, "NewReportsAction", "asfSummeryReport", "Entered");
			HttpSession session = request.getSession();
			DynaActionForm dynaForm = (DynaActionForm) form;
			dynaForm.set("memberId", "");
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId.concat(zoneId).concat(branchId);
			dynaForm.set("bankId", bankId);
			if (bankId.equals("0000")) {
				memberId = "";
				dynaForm.set("memberId", memberId);
			} else {
				dynaForm.set("mliID", memberId);
			}
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(2);
			int day = calendar.get(5);
			month--;
			day++;
			calendar.set(2, month);
			calendar.set(5, day);
			Date prevDate = calendar.getTime();
			GeneralReport generalReport = new GeneralReport();
			generalReport.setDateOfTheDocument36(prevDate);
			generalReport.setDateOfTheDocument37(date);
			BeanUtils.copyProperties(dynaForm, generalReport);
			dynaForm.set("memberId", "");
			Log.log(4, "NewReportsAction", "asfSummeryReport", "Exited");
			return mapping.findForward("success");
		}

		public ActionForward asfSummeryReportDetails(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			System.out.println("ENTERED++++RPACTION");
			DynaActionForm dynaForm = (DynaActionForm) form;
			Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Entered");
			Connection connection = DBConnection.getConnection();
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(2);
			int day = calendar.get(5);
			month--;
			day++;
			String query = "";
			Date fromdate = (Date) dynaForm.get("dateOfTheDocument36");
			Date todate = (Date) dynaForm.get("dateOfTheDocument37");
			String memberID = (String) dynaForm.get("memberId");
			String mliId = (String) dynaForm.get("mliID");
			String bankID = (String) dynaForm.get("bankId");
			String dantype = (String) dynaForm.get("dantype");
			System.out.println("dantype==="+dantype);
			if (!bankID.equals("0000"))
				memberID = mliId;
			String AsfStringArray[] = null;
			ArrayList asfSummeryDetailsArray = new ArrayList();
			String id = "";
		query = (new StringBuilder())
					.append(" SELECT MEM,DUE_C,DUE,PAID_C,PAID, (DUE-PAID) DIFF,(DUE_C-PAID_C) CASES,M.MEM_BANK_NAME,M.MEM_ZONE_NAME  FROM (SELECT MEM,SUM(DUE_C) DUE_C,SUM(DUE) DUE, SUM(PAID_C) PAID_C,SUM(PAID) PAID FROM (SELECT C.DAN_ID,C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID MEM, nvl(DUE_C,0) DUE_C, DUE , nvl(PAID_C,0) PAID_C, nvl(PAID,0) PAID FROM (SELECT DAN_ID , SUM(DCI_AMOUNT_RAISED) DUE , COUNT(*) DUE_C  FROM DAN_CGPAN_INFO WHERE (DAN_ID LIKE 'SF%' OR DAN_ID LIKE 'AF%' OR DAN_ID LIKE 'RF%' OR DAN_ID LIKE 'TF%' OR DAN_ID LIKE 'RD%')  AND (DCI_AMOUNT_RAISED-NVL(DCI_AMOUNT_CANCELLED,0)) >0 GROUP BY DAN_ID ) A, (SELECT DAN_ID, SUM(DCI_AMOUNT_RAISED) PAID, COUNT(*) PAID_C  FROM DAN_CGPAN_INFO WHERE (DAN_ID LIKE 'SF%' OR DAN_ID LIKE 'AF%' OR DAN_ID LIKE 'RF%' OR DAN_ID LIKE 'TF%' OR DAN_ID LIKE 'RD%') AND DCI_APPROPRIATION_FLAG = 'Y' AND (DCI_AMOUNT_RAISED-NVL(DCI_AMOUNT_CANCELLED,0)) >0  GROUP BY DAN_ID) B , DEMAND_ADVICE_INFO C WHERE A.DAN_ID= B.DAN_ID (+) AND A.DAN_ID = C.DAN_ID AND c.dan_type='"+dantype+"' and c.dan_generated_dt between to_date('")
					.append(fromdate).append("','dd/mm/yyyy')")
					.append("  AND to_date('").append(todate)
					.append("','dd/mm/yyyy')").toString();
		            //.append("  AND to_date('").append(todate)
		System.out.println("query1"+query);
			if (memberID.equals(null) || memberID.equals(""))
			
				query = (new StringBuilder())
						.append(query)
						.append("AND C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID=NVL('")
						.append(memberID)
						.append("',C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID)")
						.toString();
			
			//System.out.println("..............2"+query);
			else if (!memberID.equals(null)) {
				String bankId = memberID.substring(0, 4);
				String zoneId = memberID.substring(4, 8);
				String branchId = memberID.substring(8, 12);
				if (!branchId.equals("0000")) {
					id = (new StringBuilder()).append(bankId).append(zoneId)
							.append(branchId).toString();
					query = (new StringBuilder())
							.append(query)
							.append("and C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID=nvl('")
							.append(id)
							.append("',C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID)")
							.toString();
					System.out.println("query2"+query);
			} else if (!zoneId.equals("0000")) {
					id = (new StringBuilder()).append(bankId).append(zoneId)
							.toString();
					query = (new StringBuilder()).append(query)
							.append("and C.MEM_BNK_ID||C.MEM_ZNE_ID=nvl('")
							.append(id).append("',C.MEM_BNK_ID||C.MEM_ZNE_ID)")
							.toString();
					System.out.println("query3"+query);
			} else {
					id = bankId;
					query = (new StringBuilder()).append(query)
							.append("and C.MEM_BNK_ID=nvl('").append(id)
							.append("',C.MEM_BNK_ID)").toString();
				}
			}
		query = (new StringBuilder())
					.append(query)
					.append("GROUP BY C.DAN_ID,C.MEM_BNK_ID, C.MEM_ZNE_ID, MEM_BRN_ID,DUE_C,DUE,PAID_C,PAID) GROUP BY MEM ORDER BY 1),MEMBER_INFO M where m.mem_bnk_id = substr(mem,1,4) and m.mem_zne_id = substr(mem,5,4) and mem_brn_id = substr(mem,9,4) order by 8,9")
					.toString();
		System.out.println("query4"+query);
			
			try {
				Statement asfSummeryDetailsStmt = null;
				ResultSet asfSummeryDetailsResult = null;
				asfSummeryDetailsStmt = connection.createStatement();
				for (asfSummeryDetailsResult = asfSummeryDetailsStmt
						.executeQuery(query); asfSummeryDetailsResult.next(); asfSummeryDetailsArray
						.add(AsfStringArray)) {
					AsfStringArray = new String[9];
					AsfStringArray[0] = asfSummeryDetailsResult.getString(1);
					AsfStringArray[1] = asfSummeryDetailsResult.getString(2);
					AsfStringArray[2] = asfSummeryDetailsResult.getString(3);
					AsfStringArray[3] = asfSummeryDetailsResult.getString(4);
					AsfStringArray[4] = asfSummeryDetailsResult.getString(5);
					AsfStringArray[5] = asfSummeryDetailsResult.getString(6);
					AsfStringArray[6] = asfSummeryDetailsResult.getString(7);
					AsfStringArray[7] = asfSummeryDetailsResult.getString(8);
					AsfStringArray[8] = asfSummeryDetailsResult.getString(9);
				}

				asfSummeryDetailsResult.close();
				asfSummeryDetailsResult = null;
				asfSummeryDetailsStmt.close();
				asfSummeryDetailsStmt = null;
			} catch (Exception e) {
				Log.logException(e);
				throw new DatabaseException(e.getMessage());
			}
			DBConnection.freeConnection(connection);
			request.setAttribute("asfSummeryDetailsArray", asfSummeryDetailsArray);
			request.setAttribute("asfSummeryDetailsArray_size", (new Integer(
					asfSummeryDetailsArray.size())).toString());
			Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Exited");
			return mapping.findForward("success1");
		}

		public ActionForward asfSummeryMliwiseDetails(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			DynaActionForm dynaForm = (DynaActionForm) form;
			Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Entered");
			Connection connection = DBConnection.getConnection();
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(2);
			int day = calendar.get(5);
			month--;
			day++;
			String query = "";
			Date fromdate = (Date) dynaForm.get("dateOfTheDocument36");
			Date todate = (Date) dynaForm.get("dateOfTheDocument37");
			// //System.out.println((new
			// StringBuilder()).append("fromdate;").append(fromdate).append(" todate:").append(todate).toString());
			String number = request.getParameter("num");
			String AsfMLIStringArray[] = null;
			ArrayList asfSummeryMLIDetailsArray = new ArrayList();
			query = (new StringBuilder())
					.append("SELECT  p.PMR_BANK_ACCOUNT_NO, a.CGPAN,d.SSI_UNIT_NAME, b.DCI_AMOUNT_RAISED,decode(b.DCI_APPROPRIATION_FLAG,'N','Not Appropriated' , 'Yes') , b.DCI_REMARKS , a.APP_STATUS,app_mli_branch_name FROM application_detail a, dan_cgpan_info b ,demand_advice_info c , ssi_detail d,promoter_detail p  where C.MEM_BNK_ID||C.MEM_ZNE_ID||C.MEM_BRN_ID=")
					.append(number)
					.append(" and a.SSI_REFERENCE_NUMBER = d.SSI_REFERENCE_NUMBER")
					.append(" and a.SSI_REFERENCE_NUMBER = p.SSI_REFERENCE_NUMBER")
					.append(" and a.CGPAN = b.CGPAN and c.DAN_ID = b.dan_id and c.DAN_TYPE = 'SF'")
					.append(" and (DCI_AMOUNT_RAISED-NVL(DCI_AMOUNT_CANCELLED,0))>0 and trunc(c.dan_generated_dt) between to_date('")
					.append(fromdate)
					.append("','dd/mm/yyyy')")
					.append("AND to_date('")
					.append(todate)
					.append("','dd/mm/yyyy') order by 1,app_mli_branch_name,2,3, 4")
					.toString();
			//System.out.println("query==RRR=="+query);
			try {
				Statement asfSummeryMLIDetailsStmt = null;
				ResultSet asfSummeryMLIDetailsResult = null;
				asfSummeryMLIDetailsStmt = connection.createStatement();
				for (asfSummeryMLIDetailsResult = asfSummeryMLIDetailsStmt.executeQuery(query); asfSummeryMLIDetailsResult.next(); asfSummeryMLIDetailsArray.add(AsfMLIStringArray)) {
					AsfMLIStringArray = new String[9];
					AsfMLIStringArray[0] = asfSummeryMLIDetailsResult.getString(1);
					AsfMLIStringArray[1] = asfSummeryMLIDetailsResult.getString(2);
					AsfMLIStringArray[2] = asfSummeryMLIDetailsResult.getString(3);
					AsfMLIStringArray[3] = asfSummeryMLIDetailsResult.getString(4);
					AsfMLIStringArray[4] = asfSummeryMLIDetailsResult.getString(5);
					AsfMLIStringArray[5] = asfSummeryMLIDetailsResult.getString(6);
					AsfMLIStringArray[6] = asfSummeryMLIDetailsResult.getString(7);
					AsfMLIStringArray[7] = asfSummeryMLIDetailsResult.getString(8);
					AsfMLIStringArray[8] = asfSummeryMLIDetailsResult.getString(9);
				}

				asfSummeryMLIDetailsResult.close();
				asfSummeryMLIDetailsResult = null;
				asfSummeryMLIDetailsStmt.close();
				asfSummeryMLIDetailsStmt = null;
			} catch (Exception e) {
				Log.logException(e);
				throw new DatabaseException(e.getMessage());
			}
			DBConnection.freeConnection(connection);
			request.setAttribute("asfSummeryMLIDetailsArray",
					asfSummeryMLIDetailsArray);
			request.setAttribute("asfSummeryMLIDetailsArray_size", (new Integer(
					asfSummeryMLIDetailsArray.size())).toString());
			Log.log(4, "NewReportsAction", "asfSummeryReportDetails", "Exited");
			return mapping.findForward("success2");
		}

		
		
		
		
		
	}	
		
		


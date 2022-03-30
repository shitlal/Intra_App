// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   ClaimAction.java

package com.cgtsi.action;

import com.cgtsi.actionform.ClaimSettleForPaymentActionForm;
import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.InvalidDataException;
import com.cgtsi.admin.MenuOptions;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.SSIDetails;
import com.cgtsi.application.TermLoan;
import com.cgtsi.claim.ApplicationAlreadyFiledException;
import com.cgtsi.claim.BorrowerInfo;
import com.cgtsi.claim.CPDAO;
import com.cgtsi.claim.ClaimApplication;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimDetail;
import com.cgtsi.claim.ClaimSummaryDtls;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.claim.DtlsAsOnDateOfNPA;
import com.cgtsi.claim.DtlsAsOnDateOfSanction;
import com.cgtsi.claim.DtlsAsOnLogdementOfClaim;
import com.cgtsi.claim.DtlsAsOnLogdementOfSecondClaim;
import com.cgtsi.claim.ExportFailedException;
import com.cgtsi.claim.LegalProceedingsDetail;
import com.cgtsi.claim.LockInPeriodNotCompletedException;
import com.cgtsi.claim.MemberInfo;
import com.cgtsi.claim.OTSApprovalDetail;
import com.cgtsi.claim.OTSRequestDetail;
import com.cgtsi.claim.RecoveryDetails;
import com.cgtsi.claim.SecurityAndPersonalGuaranteeDtls;
import com.cgtsi.claim.SettlementDetail;
import com.cgtsi.claim.TermLoanCapitalLoanDetail;
import com.cgtsi.claim.WorkingCapitalDetail;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.guaranteemaintenance.GMDAO;
import com.cgtsi.guaranteemaintenance.GMProcessor;
import com.cgtsi.guaranteemaintenance.Recovery;
import com.cgtsi.investmentfund.BankAccountDetail;
import com.cgtsi.investmentfund.ChequeDetails;
import com.cgtsi.investmentfund.IFProcessor;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.receiptspayments.Voucher;
import com.cgtsi.receiptspayments.VoucherDetail;
import com.cgtsi.registration.CollectingBank;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.NoMemberFoundException;
import com.cgtsi.registration.Registration;
import com.cgtsi.reports.OtsDetails;
import com.cgtsi.util.BulkUpload;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;
import com.cgtsi.util.PropertyLoader;
import com.cgtsi.util.TableDetailBean;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.DynaValidatorActionForm;

// Referenced classes of package com.cgtsi.action:
//            BaseAction 

public class ClaimAction extends BaseAction {

	private static final String dbDateSeparator = "-";

	// Diksha
	
	public ActionForward claiminspectionModify(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ClaimAction", "getDeclartionDeatils", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		HttpSession session = request.getSession(false);
		Statement stmt = null;
		ResultSet rs = null;
		
		Connection conn = null;
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CLM_INPECT_DATA"))
				&& session.getAttribute("subMenuItem").equals(
						MenuOptions.getMenu("CLM_MODIFY_DATA"))) {
			Log.log(4, "ClaimAction", "forwardToNextPage", "Entered");
			ClaimsProcessor processor = new ClaimsProcessor();
			String clm_refNo = claimForm.getClmRefNumber().toUpperCase().trim();
			clm_refNo = claimForm.getClmRefNumber().toUpperCase().trim();
			ArrayList OBSER_LTR_DATE = new ArrayList();
			ArrayList OBSERVATION = new ArrayList();
			ArrayList OBSERVATION_ID = new ArrayList();
			ArrayList CMPLNC_LTR_DATE = new ArrayList();
			ArrayList COMPLINACE = new ArrayList();
			ArrayList COMPLINACE_ID = new ArrayList();
			String l_Str_FURTHER_OBSERVATION = "";
			String l_Str_FURTHER_DATE = "";
			String l_Str_FURTHER_COMPLIANCE = "";
			String l_Str_FURTHER_COMPLIANCE_DATE = "";
			try {
				conn = DBConnection.getConnection();
				int CGPAN_new = 0;
				String cgpanChkQuery = (new StringBuilder(
						"select count(CLM_REF_NO) count from CLAIM_INSPECTION_DATA where CLM_REF_NO='"))
						.append(clm_refNo).append("'").toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(cgpanChkQuery);
				if (rs.next())
					CGPAN_new = rs.getInt("count");
				if (CGPAN_new == 0)
					try {
						throw new NoMemberFoundException(
								" Entered Claim Ref Number not Available for Update.Please Enter Another Claim Ref No.");
					} catch (Exception e) {
						throw new NoMemberFoundException(
								"Entered Claim Ref Number not Available for Update.Please Enter Another Claim Ref No.");
					}
				String FetchMain_TblQuery = (new StringBuilder(
						"SELECT CGPAN,CLM_REF_NO,CLM_FURTHER_COMPLIANCE,CLM_LATEST_LTR_ISSUE_DATE,CLM_LATEST_COMPL_RCVD_DATE,CLM_INSPECT_DATE,CLAIM_INSPECT_BY,CLAIM_INSPECT_REMARKS,CLM_REPORT_DATE,CLM_INPSTATUS,ISRECVRYPROPOS,CLM_RECOVERY_AMT,CLM_INSRECEIVED_AMT,CLM_AMOUNT_RECVD,CLM_MLI_NAME, CLM_MEMBER_ID,CLM_ISSUED_DATE,CLM_COMPLIANCE_RCVD_DATE FROM CLAIM_INSPECTION_DATA where CLM_REF_NO='"))
						.append(clm_refNo).append("'").toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(FetchMain_TblQuery);
				if (rs.next()) {
					if (rs.getString("CLM_REF_NO") != null)
						claimForm.setClmRefNumber(rs.getString("CLM_REF_NO"));
					if (rs.getString("CLM_LATEST_LTR_ISSUE_DATE") != null)
						claimForm.setLetest_letterIssue_Date(rs.getString(
								"CLM_LATEST_LTR_ISSUE_DATE").replaceAll(
								"00:00:00.0", ""));
					if (rs.getString("CLM_LATEST_COMPL_RCVD_DATE") != null)
						claimForm.setLetestCompliance_recvd_date(rs.getString(
								"CLM_LATEST_COMPL_RCVD_DATE").replaceAll(
								"00:00:00.0", ""));
					if (rs.getString("CLM_INSPECT_DATE") != null)
						claimForm.setInspectDate(rs.getString(
								"CLM_INSPECT_DATE")
								.replaceAll("00:00:00.0", ""));
					if (rs.getString("CLAIM_INSPECT_BY") != null)
						claimForm.setInspectPerson(rs
								.getString("CLAIM_INSPECT_BY"));
					if (rs.getString("CLAIM_INSPECT_REMARKS") != null)
						claimForm.setInspectRemarks(rs
								.getString("CLAIM_INSPECT_REMARKS"));
					if (rs.getString("CLM_INSRECEIVED_AMT") != null)
						claimForm.setAmnt_rec_descion(rs
								.getString("CLM_INSRECEIVED_AMT"));
					if (rs.getString("CLM_RECOVERY_AMT") != null)
						claimForm.setInsrecoveryamt(rs
								.getString("CLM_RECOVERY_AMT"));
					if (rs.getString("CLM_INPSTATUS") != null)
						claimForm.setInspectstatus(rs
								.getString("CLM_INPSTATUS"));
					if (rs.getString("CLM_REPORT_DATE") != null)
						claimForm
								.setReportsubmDate(rs.getString(
										"CLM_REPORT_DATE").replaceAll(
										"00:00:00.0", ""));
					if (rs.getString("ISRECVRYPROPOS") != null)
						claimForm.setIsrecproposed(rs
								.getString("ISRECVRYPROPOS"));
					if (rs.getString("CLM_MEMBER_ID") != null)
						claimForm.setMliid(rs.getString("CLM_MEMBER_ID"));
					if (rs.getString("CLM_MLI_NAME") != null)
						claimForm.setMliname(rs.getString("CLM_MLI_NAME"));
					if (rs.getString("CLM_AMOUNT_RECVD") != null)
						claimForm.setAmount_Recvd(rs
								.getString("CLM_AMOUNT_RECVD"));
					if (rs.getString("CLM_FURTHER_COMPLIANCE") != null)
						claimForm.setFurher_compliance(rs
								.getString("CLM_FURTHER_COMPLIANCE"));
					if (rs.getString("CLM_ISSUED_DATE") != null)
						claimForm.setIssue_Date(rs.getString("CLM_ISSUED_DATE")
								.replaceAll("00:00:00.0", ""));
					if (rs.getString("CLM_COMPLIANCE_RCVD_DATE") != null)
						claimForm.setCompliance_Date(rs.getString(
								"CLM_COMPLIANCE_RCVD_DATE").replaceAll(
								"00:00:00.0", ""));
				}
				String obserDetlQry = (new StringBuilder(
						"SELECT distinct COD_DET_ID,NVL (cod.COD_OBSER_LTR_DATE, NULL) COD_OBSER_LTR_DATE,NVL (cod.COD_OBSERVATION, NULL) COD_OBSERVATION,cod.COD_ENTERED_DATE,cod.COD_ENTERED_BY FROM CLAIM_INSPECTION_DATA cl, CLAIM_OBSERVATION_DETAIL cod  WHERE cl.CLM_REF_NO = cod.COD_CLM_REF_NO AND cl.CLM_REF_NO ='"))
						.append(clm_refNo).append("' order by COD_DET_ID asc")
						.toString();
				stmt = conn.createStatement();
				for (rs = stmt.executeQuery(obserDetlQry); rs.next(); OBSERVATION_ID
						.add(rs.getString("COD_DET_ID"))) {
					OBSERVATION.add(rs.getString("COD_OBSERVATION"));
					OBSER_LTR_DATE.add(rs.getString("COD_OBSER_LTR_DATE"));
				}

				String ComplDetlQry = (new StringBuilder(
						"SELECT distinct CCD_DET_ID,CCD_CLM_REF_NO,NVL (ccd.CCD_COMPL_LTR_DATE, NULL) CCD_COMPL_LTR_DATE,NVL (ccd.CCD_COMPLIANCE, NULL) CCD_COMPLIANCE,ccd.CCD_ENTERED_DATE,ccd.CCD_ENTERED_BY FROM CLAIM_INSPECTION_DATA cl, CLAIM_COMPLIANCE_DETAIL ccd WHERE cl.CLM_REF_NO = ccd.CCD_CLM_REF_NO AND cl.CLM_REF_NO ='"))
						.append(clm_refNo).append("' order by CCD_DET_ID asc")
						.toString();
				stmt = conn.createStatement();
				for (rs = stmt.executeQuery(ComplDetlQry); rs.next(); COMPLINACE_ID
						.add(rs.getString("CCD_DET_ID"))) {
					COMPLINACE.add(rs.getString("CCD_COMPLIANCE"));
					CMPLNC_LTR_DATE.add(rs.getString("CCD_COMPL_LTR_DATE"));
				}

				session.setAttribute("OBSERVATION", OBSERVATION);
				session.setAttribute("OBSER_LTR_DATE", OBSER_LTR_DATE);
				session.setAttribute("OBSERVATION_ID", OBSERVATION_ID);
				session.setAttribute("COMPLINACE", COMPLINACE);
				session.setAttribute("CMPLNC_LTR_DATE", CMPLNC_LTR_DATE);
				session.setAttribute("COMPLINACE_ID", COMPLINACE_ID);
				ArrayList CGPAN = new ArrayList();
				String CGPANQuery = (new StringBuilder(
						"select a.clm_ref_no,ssi_unit_name unitname,B.cgpan FROM claim_detail a,claim_tc_detail b,SSI_DETAIL S,APPLICATION_DETAIL D, promoter_detail p,member_info m  WHERE a.clm_ref_no = b.clm_ref_no AND clm_status = 'AP' AND S.BID = A.BID AND B.CGPAN = D.CGPAN AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id AND p.ssi_reference_number = s.ssi_reference_number AND APP_LOAN_TYPE = 'TC' and a.CLM_REF_NO='"))
						.append(clm_refNo)
						.append("'")
						.append(" UNION ALL")
						.append(" SELECT a.clm_ref_no,ssi_unit_name unitname,B.cgpan FROM claim_detail a,claim_wc_detail b,SSI_DETAIL S,")
						.append(" APPLICATION_DETAIL D, promoter_detail p,member_info m WHERE a.clm_ref_no = b.clm_ref_no AND clm_status = 'AP'")
						.append("  AND S.BID = A.BID AND B.CGPAN = D.CGPAN AND p.ssi_reference_number = s.ssi_reference_number AND APP_LOAN_TYPE = 'WC' AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =")
						.append("  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id and a.CLM_REF_NO='")
						.append(clm_refNo).append("'").toString();
				stmt = conn.createStatement();
				for (rs = stmt.executeQuery(CGPANQuery); rs.next(); CGPAN
						.add(rs.getString("cgpan")))
					;
				session.setAttribute("CGPAN", CGPAN);
			} catch (SQLException e) {
				throw new NoMemberFoundException("SQL Exception ");
			}
			Log.log(4, "ClaimAction", "showClmInspOption", "Exited");
		}
		return mapping.findForward("success");
	}

	public ActionForward addClaimInspData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection connection = null;
		try {

			String clm_Ref_num;
			String INSPECTION_DONE_BY;
			String REMARKS;
			String STATUS;
			String IS_RECVRY_PROPOSED;
			String RECOVERY_AMNT;
			String RECV_RECD_DESCISION;
			String RECV_AMNT;
			String MLIID;
			String MLINAME;
			String furher_compliance;
			String issueDate_new;
			String compl_rec_date_new;
			String INSP_DATE_new;
			String REPORT_SUB_DATE_new;
			int insert;
			String userId;
			Log.log(4, "ClaimAction", "getDeclartionDeatils", "Entered");
			ClaimActionForm claimForm = (ClaimActionForm) form;
			clm_Ref_num = claimForm.getClmRefNumber();
			INSPECTION_DONE_BY = claimForm.getInspectPerson();
			REMARKS = claimForm.getInspectRemarks();
			String INSP_DATE = claimForm.getInspectDate();
			String REPORT_SUB_DATE = claimForm.getReportsubmDate();
			STATUS = claimForm.getInspectstatus();
			IS_RECVRY_PROPOSED = claimForm.getIsrecproposed();
			RECOVERY_AMNT = claimForm.getInsrecoveryamt();
			RECV_RECD_DESCISION = claimForm.getAmnt_rec_descion();
			RECV_AMNT = claimForm.getAmount_Recvd();
			MLIID = claimForm.getMliid();
			MLINAME = claimForm.getMliname();
			furher_compliance = claimForm.getFurher_compliance();
			String issueDate = claimForm.getIssue_Date();
			String compl_rec_date = claimForm.getCompliance_Date();
			issueDate_new = "";
			compl_rec_date_new = "";
			if (issueDate != null && issueDate.length() > 0)
				issueDate_new = issueDate;
			else
				issueDate_new = "null";
			if (compl_rec_date != null && compl_rec_date.length() > 0)
				compl_rec_date_new = compl_rec_date;
			else
				compl_rec_date_new = "null";
			INSP_DATE_new = "";
			REPORT_SUB_DATE_new = "";
			if (INSP_DATE != null && INSP_DATE.length() > 0)
				INSP_DATE_new = INSP_DATE;
			else
				INSP_DATE_new = "null";
			if (REPORT_SUB_DATE != null && REPORT_SUB_DATE.length() > 0)
				REPORT_SUB_DATE_new = REPORT_SUB_DATE;
			else
				REPORT_SUB_DATE_new = "null";
			insert = 0;
			connection = DBConnection.getConnection();
			User user = getUserInformation(request);
			userId = user.getUserId();
			Statement str = connection.createStatement();
			int Clm_ref_new = 0;
			String cgpanChkQuery = (new StringBuilder(
					"select count(CLM_REF_NO) count from CLAIM_INSPECTION_DATA where CLM_REF_NO=upper('"))
					.append(clm_Ref_num).append("')").toString();
			ResultSet clm_ref = str.executeQuery(cgpanChkQuery);
			if (clm_ref.next())
				Clm_ref_new = clm_ref.getInt("count");
			if (Clm_ref_new > 0)
				try {
					throw new NoMemberFoundException(
							" Claim Ref Number Already Exist. Please Enter Another Claim Ref No.");
				} catch (Exception e) {
					throw new NoMemberFoundException(
							" Claim Ref Number Already Exist. Please Enter Another Claim Ref No.");
				}
			String InsertQry = "";
			InsertQry = (new StringBuilder(
					"INSERT INTO CLAIM_INSPECTION_DATA(CLM_REF_NO,CGPAN,CLM_INSPECT_DATE,CLAIM_INSPECT_BY,CLM_REPORT_DATE,CLAIM_INSPECT_REMARKS,data_enterd_by,data_entered_date,isrecvrypropos,CLM_RECOVERY_AMT,CLM_AMOUNT_RECVD,CLM_INPSTATUS,CLM_MLI_NAME,CLM_MEMBER_ID,CLM_INSRECEIVED_AMT,CLM_FURTHER_COMPLIANCE,CLM_ISSUED_DATE,CLM_COMPLIANCE_RCVD_DATE)values('"))
					.append(clm_Ref_num).append("','null','")
					.append(INSP_DATE_new).append("','")
					.append(INSPECTION_DONE_BY).append("','")
					.append(REPORT_SUB_DATE_new).append("','").append(REMARKS)
					.append("','").append(userId).append("',sysdate,'")
					.append(IS_RECVRY_PROPOSED).append("','")
					.append(RECOVERY_AMNT).append("','").append(RECV_AMNT)
					.append("','").append(STATUS).append("','").append(MLINAME)
					.append("','").append(MLIID).append("','")
					.append(RECV_RECD_DESCISION).append("','")
					.append(furher_compliance).append("','")
					.append(issueDate_new).append("','")
					.append(compl_rec_date_new).append("')").toString();
			insert = str.executeUpdate(InsertQry);

			connection.commit();
			if (insert > 0)
				request.setAttribute("message",
						"<b>The Request For Inspection is Successfull.<b><br>");
			else if (insert == 0)
				request.setAttribute("message",
						"<b>The Request for inspection is Failed.<b><br>");
			Log.log(4, "ClaimAction", "addClaimInspData", "Exited");
		} catch (Exception e) {
			throw new NoMemberFoundException(
					" Problem in saving inspection data , kindly contact IT team");
		} finally {
			DBConnection.freeConnection(connection);
		}
		return mapping.findForward("success");
	}

	public ActionForward updateClaimInspData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws SQLException,
			DatabaseException {
		Connection connection;
		String clm_Ref_num;
		String INSPECTION_DONE_BY;
		String REMARKS;
		String STATUS;
		String IS_RECVRY_PROPOSED;
		String RECOVERY_AMNT;
		String RECV_RECD_DESCISION;
		String RECV_AMNT;
		String MLIID;
		String MLINAME;
		String MLI_compliance;
		String latest_ltr_issue_date_new;
		String latest_compl_rcvd_date_new;
		String issueDate_new;
		String compl_rec_date_new;
		String INSP_DATE_new;
		String REPORT_SUB_DATE_new;
		System.out.println("==update inspection data==394===");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		connection = null;
		clm_Ref_num = claimForm.getClmRefNumber();
		INSPECTION_DONE_BY = claimForm.getInspectPerson();
		REMARKS = claimForm.getInspectRemarks();
		String INSP_DATE = claimForm.getInspectDate();
		String REPORT_SUB_DATE = claimForm.getReportsubmDate();
		STATUS = claimForm.getInspectstatus();
		IS_RECVRY_PROPOSED = claimForm.getIsrecproposed();
		RECOVERY_AMNT = claimForm.getInsrecoveryamt();
		RECV_RECD_DESCISION = claimForm.getAmnt_rec_descion();
		RECV_AMNT = claimForm.getAmount_Recvd();
		MLIID = claimForm.getMliid();
		MLINAME = claimForm.getMliname();
		MLI_compliance = claimForm.getFurher_compliance();
		String latest_ltr_issue_date = claimForm.getLetest_letterIssue_Date();
		String latest_compl_rcvd_date = claimForm
				.getLetestCompliance_recvd_date();
		latest_ltr_issue_date_new = "";
		latest_compl_rcvd_date_new = "";
		if (latest_ltr_issue_date != null && latest_ltr_issue_date.length() > 0)
			latest_ltr_issue_date_new = latest_ltr_issue_date;
		else
			latest_ltr_issue_date_new = "null";
		if (latest_compl_rcvd_date != null
				&& latest_compl_rcvd_date.length() > 0)
			latest_compl_rcvd_date_new = latest_compl_rcvd_date;
		else
			latest_compl_rcvd_date_new = "null";
		String issueDate = claimForm.getIssue_Date();
		String compl_rec_date = claimForm.getCompliance_Date();
		issueDate_new = "";
		compl_rec_date_new = "";
		if (issueDate != null && issueDate.length() > 0)
			issueDate_new = issueDate;
		else
			issueDate_new = "null";
		if (compl_rec_date != null && compl_rec_date.length() > 0)
			compl_rec_date_new = compl_rec_date;
		else
			compl_rec_date_new = "null";
		INSP_DATE_new = "";
		REPORT_SUB_DATE_new = "";
		if (INSP_DATE != null && INSP_DATE.length() > 0)
			INSP_DATE_new = INSP_DATE;
		else
			INSP_DATE_new = "null";
		if (REPORT_SUB_DATE != null && REPORT_SUB_DATE.length() > 0)
			REPORT_SUB_DATE_new = REPORT_SUB_DATE;
		else
			REPORT_SUB_DATE_new = "null";
		Statement stmt = null;
		ResultSet rs = null;
		int UpdateInspect = 0;
		int Update_frthr_obs = 0;
		int Update_compl = 0;
		Date date = new Date();
		long t = date.getTime();
		java.sql.Date createdModfyDate = new java.sql.Date(t);
		try {
			connection = DBConnection.getConnection();
			Statement str = connection.createStatement();
			String update_insp_qty = (new StringBuilder(
					"Update CLAIM_INSPECTION_DATA set CLM_INSPECT_DATE='"))
					.append(INSP_DATE_new).append("',CLAIM_INSPECT_BY='")
					.append(INSPECTION_DONE_BY)
					.append("',CLAIM_INSPECT_REMARKS='").append(REMARKS)
					.append("',CLM_REPORT_DATE='").append(REPORT_SUB_DATE_new)
					.append("',CLM_INPSTATUS='").append(STATUS)
					.append("',ISRECVRYPROPOS='").append(IS_RECVRY_PROPOSED)
					.append("',CLM_RECOVERY_AMT='").append(RECOVERY_AMNT)
					.append("',CLM_INSRECEIVED_AMT='")
					.append(RECV_RECD_DESCISION).append("',CLM_AMOUNT_RECVD='")
					.append(RECV_AMNT).append("',CLM_MLI_NAME='")
					.append(MLINAME).append("',CLM_MEMBER_ID='").append(MLIID)
					.append("',CLM_FURTHER_COMPLIANCE='")
					.append(MLI_compliance).append("',CLM_ISSUED_DATE='")
					.append(issueDate_new)
					.append("',CLM_COMPLIANCE_RCVD_DATE='")
					.append(compl_rec_date_new)
					.append("',CLM_LATEST_LTR_ISSUE_DATE='")
					.append(latest_ltr_issue_date_new)
					.append("',CLM_LATEST_COMPL_RCVD_DATE='")
					.append(latest_compl_rcvd_date_new)
					.append("' where CLM_REF_NO='").append(clm_Ref_num)
					.append("'").toString();
			UpdateInspect = str.executeUpdate(update_insp_qty);
			String further_obsr_letter_issue_date[] = request
					.getParameterValues("further_obsr_letter_issue_date");
			String further_observation[] = request
					.getParameterValues("further_observation");
			String further_observation_new[] = request
					.getParameterValues("further_observation_new");
			String further_obsr_letter_issue_date_new[] = request
					.getParameterValues("further_obsr_letter_issue_date_new");
			if (UpdateInspect > 0 && clm_Ref_num.length() > 0
					&& further_observation_new != null
					&& further_observation_new.length > 0) {
				int InsertInspect = 0;
				for (int h = 0; h < further_observation_new.length; h++) {
					String obser_NewAdd_Qry = (new StringBuilder(
							"INSERT INTO CLAIM_OBSERVATION_DETAIL(COD_DET_ID,COD_CLM_REF_NO,CGPAN,COD_OBSER_LTR_DATE,COD_OBSERVATION,COD_ENTERED_BY,COD_ENTERED_DATE)VALUES(SEQ_OBSERV_ID.nextval,'"))
							.append(clm_Ref_num).append("','','")
							.append(further_obsr_letter_issue_date_new[h])
							.append("','").append(further_observation_new[h])
							.append("','").append(INSPECTION_DONE_BY)
							.append("',sysdate)").toString();
					InsertInspect = str.executeUpdate(obser_NewAdd_Qry);
					connection.commit();
				}

			}
			String l_strObservation_ID[] = request
					.getParameterValues("observation_update");
			if (l_strObservation_ID != null && l_strObservation_ID.length > 0) {
				for (int a = 0; a < l_strObservation_ID.length; a++) {
					String update_Observ_qry = (new StringBuilder(
							"Update CLAIM_OBSERVATION_DETAIL set COD_OBSER_LTR_DATE='"))
							.append(further_obsr_letter_issue_date[a])
							.append("',COD_ENTERED_BY='")
							.append(INSPECTION_DONE_BY)
							.append("',COD_OBSERVATION='")
							.append(further_observation[a])
							.append("',COD_ENTERED_DATE='' where COD_DET_ID='")
							.append(l_strObservation_ID[a]).append("'")
							.toString();
					System.out.println((new StringBuilder(
							"update_Observ_qry===617===")).append(
							update_Observ_qry).toString());
					Update_compl = str.executeUpdate(update_Observ_qry);
					System.out.println((new StringBuilder(
							"Update_compl===667===")).append(Update_compl)
							.toString());
					connection.commit();
				}

			}
			String frthr_comp_lter_iss_date_new[] = request
					.getParameterValues("frthr_comp_lter_iss_date_new");
			String furher_compliance_new[] = request
					.getParameterValues("furher_compliance_new");
			String frthr_comp_lter_iss_date[] = request
					.getParameterValues("frthr_comp_lter_iss_date");
			String furher_compliance[] = request
					.getParameterValues("furher_complianc");
			if (frthr_comp_lter_iss_date_new != null
					&& furher_compliance_new != null) {
				int InsertInspect = 0;
				for (int j = 0; j < furher_compliance_new.length; j++) {
					String complNewAddQry = (new StringBuilder(
							"INSERT INTO CLAIM_COMPLIANCE_DETAIL(CCD_DET_ID,CCD_CLM_REF_NO,CGPAN,CCD_COMPL_LTR_DATE,CCD_COMPLIANCE,CCD_ENTERED_BY,CCD_ENTERED_DATE)VALUES(SEQ_COMPL_ID.nextval,'"))
							.append(clm_Ref_num).append("','','")
							.append(frthr_comp_lter_iss_date_new[j])
							.append("','").append(furher_compliance_new[j])
							.append("','").append(INSPECTION_DONE_BY)
							.append("',sysdate)").toString();
					InsertInspect = str.executeUpdate(complNewAddQry);
					connection.commit();
				}

			}
			String l_strCompliace_ID[] = request
					.getParameterValues("complnc_update");
			if (l_strCompliace_ID != null && l_strCompliace_ID.length > 0) {
				for (int k = 0; k < l_strCompliace_ID.length; k++) {
					String update_Compl_qty = (new StringBuilder(
							"Update CLAIM_COMPLIANCE_DETAIL set CCD_COMPL_LTR_DATE='"))
							.append(frthr_comp_lter_iss_date[k])
							.append("',CCD_ENTERED_BY='")
							.append(INSPECTION_DONE_BY)
							.append("',CCD_COMPLIANCE='")
							.append(furher_compliance[k])
							.append("',CCD_ENTERED_DATE='' where CCD_DET_ID='")
							.append(l_strCompliace_ID[k]).append("'")
							.toString();
					Update_compl = str.executeUpdate(update_Compl_qty);
					connection.commit();
				}

			}
			if (UpdateInspect > 0)
				request.setAttribute("message",
						"<b>Inspection Detail Updated Successfully.<b><br>");
			else if (UpdateInspect == 0)
				request.setAttribute("message",
						"<b>Inspection Detail Updation Failed!!.<b><br>");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to Update Inspection Details.");
		}

		DBConnection.freeConnection(connection);

		DBConnection.freeConnection(connection);
		Log.log(4, "ClaimAction", "Update ClaimInspData", "Exited");
		return mapping.findForward("success");
	}

	// Diksha end

	public ActionForward setBankId(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "ClaimAction", "setBankId", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetTheForm(mapping, request);
		Log.log(Log.INFO, "ClaimAction", "setBankId",
				"Retrieving user info from request object");
		User user = getUserInformation(request);
		String bankid = user.getBankId().trim();
		String zoneid = user.getZoneId().trim();
		String branchid = user.getBranchId().trim();
		String memberId = (new StringBuilder()).append(bankid).append(zoneid)
				.append(branchid).toString();
		if (!bankid.equals("0000"))
			claimForm.setMemberId(memberId);
		claimForm.setBankId(bankid);
		Log.log(Log.INFO, "ClaimAction", "setBankId", "Exited");
		return mapping.findForward("getBorrowerId");
	}

	public ActionForward claimStatusChange(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "claimStatusChange", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.setClaimRefNum("");
		claimForm.setEnterCgpan("");

		return mapping.findForward("changeStatus");
	}

	public ActionForward stringToBarCode(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "claimStatusChange", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.setClaimRefNum("");
		claimForm.setEnterCgpan("");

		String enterclaimReff = claimForm.getAppRefNo().toString()
				.toUpperCase();

		/***** code for bar code generation *****/
		int width, height;
		String filename = "barcode_" + enterclaimReff + ".jpg";
		File saveFile = new File(
				"D:/software in Use/jdevstudio10133/jdev/mywork/CGTSI116TEST/ViewController/public_html/images/barcodes/"
						+ filename);
		String format = new String("jpg");
		BufferedImage bi, biFiltered;
		;
		width = 250;
		height = 50;

		BufferedImage bufimg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics graphicsobj = bufimg.createGraphics();

		File file1 = new File("D:/barcodeimage/3of9.TTF");
		FileInputStream fin = new FileInputStream(file1);
		Font font = Font.createFont(Font.TRUETYPE_FONT, fin);
		Font font1 = font.deriveFont(46f);

		graphicsobj.setFont(font1);
		graphicsobj.setFont(font.getFont("3 of 9 Barcode"));
		graphicsobj.setColor(Color.WHITE);

		// Generate barcode image for claim reference number
		graphicsobj.fillRect(1, 1, 248, 48);
		graphicsobj.setColor(Color.BLACK);
		graphicsobj.drawString(enterclaimReff, 20, 30);

		ImageIO.write(bufimg, format, saveFile);

		fin.close();
		/***** end *****/
		String filename2 = "barcode_" + enterclaimReff + ".jpg";
		File barCodeToStr = new File(
				"D:/software in Use/jdevstudio10133/jdev/mywork/CGTSI116TEST/ViewController/public_html/images/barcodes/"
						+ filename2);

		FileReader reader = new FileReader(
				"D:/software in Use/jdevstudio10133/jdev/mywork/CGTSI116TEST/ViewController/public_html/images/barcodes/"
						+ filename2);
		FileInputStream fis = new FileInputStream(filename2);
		byte[] imagedata = new byte[(int) filename2.length()];
		fis.read(imagedata);

		fis.close();
		claimForm.setClaimRefNum("");

		return mapping.findForward("barcode");
	}

	public ActionForward afterclaimStatusChange(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "afterclaimStatusChange", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String claimRefNum = claimForm.getClaimRefNum();
		String cgpan = claimForm.getEnterCgpan();
		CPDAO cpDAO = new CPDAO();
		String claimRefNo = null;
		boolean statusFlag = false;
		User user = getUserInformation(request);
		String userId = user.getUserId();
		if (claimRefNum != null && !claimRefNum.equals("") && cgpan != null
				&& !cgpan.equals("")) {
			claimRefNo = cpDAO.getClaimRefNo(cgpan);
			if (!claimRefNum.equals(claimRefNo))
				throw new DatabaseException(
						"CGPAN and Claim Ref no does not Match");
			cpDAO.updateClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		} else if (claimRefNum != null && !claimRefNum.equals("")) {
			cpDAO.updateClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		} else if (cgpan != null && !cgpan.equals("")) {
			cpDAO.updateClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		}
		if (!statusFlag) {
			throw new DatabaseException(
					"Unable Update Claim Application Status");
		} else {
			request.setAttribute("message",
					"Claim Application Status Changed Successfully");
			Log.log(Log.INFO, "ClaimAction", "afterclaimStatusChange", "Exited");
			return mapping.findForward("success");
		}
	}

	public ActionForward getDeclartaionDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "getDeclartionDeatils", "Entered");

		Log.log(Log.INFO, "ClaimAction", "getDeclartionDeatils", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward getDeclartaionDetailData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String memberId;
		String enterclaimReff;
		String enterCgpan;
		ClaimActionForm claimForm;
		Connection connection = null;
		String claimreff;
		Log.log(Log.INFO, "ClaimAction", "getDeclartaionDetailData", "Entered");
		memberId = null;
		enterclaimReff = null;
		enterCgpan = null;
		claimForm = (ClaimActionForm) form;
		enterclaimReff = claimForm.getAppRefNo().toString().toUpperCase();

		try {
			memberId = claimForm.getEnterMember().toString();
			enterclaimReff = claimForm.getAppRefNo().toString().toUpperCase();
			enterCgpan = claimForm.getEnterCgpan().toString().toUpperCase();
		} catch (Exception e) {
			throw new NoMemberFoundException(
					"Enter CGPAN Or Claim Reff Number.");
		}
		if (memberId.equals("")) {
			throw new NoMemberFoundException("Enter valid Member ID.");
		}
		if (!memberId.equals("")) {
			connection = DBConnection.getConnection();
			claimreff = null;
			try {
				if (enterclaimReff.equals("") && enterCgpan.equals("")) {
					throw new NoMemberFoundException(
							"Enter CGPAN Or Claim Reff Number.");
				}
				// Statement str = connection.createStatement(1005, 1007);
				Statement str = connection.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				String one = memberId.substring(0, 4);
				String two = memberId.substring(4, 8);
				String three = memberId.substring(8, 12);
				String MemIdavailbel = (new StringBuilder())
						.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c \nwhere  c.mem_bnk_id='")
						.append(one).append("' and\n").append("c.mem_zne_id='")
						.append(two).append("' and\n").append("c.mem_brn_id='")
						.append(three).append("'").toString();
				ResultSet rsforavailmemid = str.executeQuery(MemIdavailbel);
				if (!rsforavailmemid.next())
					throw new NoMemberFoundException(
							" Member Id  Not Exsist : ");
				if (!enterCgpan.equals("")) {
					String cgQury = (new StringBuilder())
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='")
							.append(enterCgpan).append("'").toString();
					ResultSet rsforvalid = str.executeQuery(cgQury);
					String memberid = "";
					if (!rsforvalid.next())
						throw new NoMemberFoundException(
								" CGPAN  Not Exsist : ");
					rsforvalid.beforeFirst();
					while (rsforvalid.next())
						memberid = rsforvalid.getString(1);
					if (memberid.equals(memberId)) {
						String chck = (new StringBuilder())
								.append(" select clm_Ref_no from claim_detail\nwhere bid in\n(select s.bid from ssi_detail s,application_detail a\nwhere a.ssi_Reference_number = s.ssi_Reference_number\nand a.cgpan = '")
								.append(enterCgpan)
								.append("'\n")
								.append("and  a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id='")
								.append(memberId).append("')").toString();
						ResultSet rsChk = str.executeQuery(chck);
						if (!rsChk.next()) {
							String qury1 = (new StringBuilder())
									.append("select clm_Ref_no from claim_detail_temp\nwhere bid in\n(select s.bid from ssi_detail s,application_detail a\nwhere a.ssi_Reference_number = s.ssi_Reference_number\nand a.cgpan = '")
									.append(enterCgpan)
									.append("'\n")
									.append("and  a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id='")
									.append(memberId).append("')").toString();
							ResultSet rs1 = str.executeQuery(qury1);
							if (!rs1.next())
								throw new NoMemberFoundException(
										"Claim Application Not Lodged : ");
							rs1.beforeFirst();
							while (rs1.next())
								claimreff = rs1.getString(1);
						} else {
							String ClaimReffNo = "";
							rsChk.beforeFirst();
							while (rsChk.next())
								ClaimReffNo = rsChk.getString(1);
							throw new NoMemberFoundException(
									(new StringBuilder())
											.append("Claim Application Already Approved for Claim Reff Number ")
											.append(ClaimReffNo).toString());
						}
					} else if (!memberid.equals(memberId))
						throw new NoMemberFoundException(
								" Member Id and CGPAN are not Associated: ");
				}
				String qury2 = null;
				if (!enterclaimReff.equals("")) {
					if (claimreff != null && !enterclaimReff.equals(claimreff))
						throw new NoMemberFoundException(
								"Claim Reff Number Does Not Exsist.");
					qury2 = (new StringBuilder())
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,C.clm_ref_no,ssi_unit_name,npa_effective_dt,D.cgpan,\na.app_approved_date_time,a.app_approved_amount \nfrom claim_detail_temp c,ssi_detail s,npa_detail n,application_detail a,claim_application_amount_temp d \nwhere c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("' \n")
							.append("and C.clm_ref_no='")
							.append(enterclaimReff)
							.append("' \n")
							.append("and C.CLM_REF_NO=D.CLM_REF_NO AND s.bid=c.BID \n")
							.append("and n.bid=c.bid\n")
							.append("and a.ssi_reference_number=s.ssi_reference_number \n")
							.append("union\n")
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,C.clm_ref_no,ssi_unit_name,npa_effective_dt,D.cgpan,\n")
							.append("a.app_approved_date_time,a.app_approved_amount \n")
							.append("from claim_detail_temp c,ssi_detail s,npa_detail_temp n,application_detail a,CLAIM_APPLICATION_AMOUNT_TEMP D \n")
							.append("where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("' \n")
							.append("and C.clm_ref_no='")
							.append(enterclaimReff)
							.append("' \n")
							.append("and C.CLM_REF_NO=D.CLM_REF_NO AND s.bid=c.BID \n")
							.append("and app_status not in ('RE') and n.bid=c.bid\n")
							.append("and a.ssi_reference_number=s.ssi_reference_number\n")
							.append("and d.cgpan = a.cgpan").toString();
				} else {
					qury2 = (new StringBuilder())
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,C.clm_ref_no,ssi_unit_name,npa_effective_dt,D.cgpan,\na.app_approved_date_time,a.app_approved_amount \nfrom claim_detail_temp c,ssi_detail s,npa_detail n,application_detail a,CLAIM_APPLICATION_AMOUNT_TEMP D \nwhere c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("' \n")
							.append("and C.clm_ref_no='")
							.append(claimreff)
							.append("' \n")
							.append("and C.CLM_REF_NO=D.CLM_REF_NO AND  s.bid=c.BID \n")
							.append("and n.bid=c.bid\n")
							.append("and a.ssi_reference_number=s.ssi_reference_number \n")
							.append("union\n")
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,C.clm_ref_no,ssi_unit_name,npa_effective_dt,D.cgpan,\n")
							.append("a.app_approved_date_time,a.app_approved_amount \n")
							.append("from claim_detail_temp c,ssi_detail s,npa_detail_temp n,application_detail a,CLAIM_APPLICATION_AMOUNT_TEMP D \n")
							.append("where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("' \n")
							.append("and C.clm_ref_no='")
							.append(claimreff)
							.append("' \n")
							.append("and C.CLM_REF_NO=D.CLM_REF_NO AND s.bid=c.BID \n")
							.append(" and app_status not in ('RE') and n.bid=c.bid\n")
							.append("and a.ssi_reference_number=s.ssi_reference_number\n")
							.append("and d.cgpan = a.cgpan").toString();
				}
				if (enterclaimReff.equals("") && claimreff.equals(""))
					throw new NoMemberFoundException(
							"Claim Reff Number Does Not Exist ");
				Connection connection1 = DBConnection.getConnection();
				Statement str1 = connection1.createStatement();
				String quryforSelect = "select * from Claim_detail_temp where clm_declaration_recvd='Y' and clm_decl_recvd_dt is Not Null";
				ResultSet rs = str1.executeQuery(quryforSelect);
				String claimreffNo = null;
				while (rs.next()) {
					claimreffNo = rs.getString(1);
					if (claimreffNo.equals(enterclaimReff)
							|| claimreffNo.equals(claimreff))
						throw new NoMemberFoundException(
								"Declaration Already updated for  Claim Reffrence Number  ");
				}
				String chkMemForClaim = "";
				if (!enterclaimReff.equals("")) {
					chkMemForClaim = (new StringBuilder())
							.append("select clm_ref_no cnt from claim_detail_temp where clm_ref_no='")
							.append(enterclaimReff)
							.append("'   \n")
							.append("union all\n")
							.append("select clm_ref_no cnt from claim_detail  where clm_ref_no='")
							.append(enterclaimReff).append("'").toString();
					ResultSet chkclaimRefavil;
					for (chkclaimRefavil = str1.executeQuery(chkMemForClaim); !chkclaimRefavil
							.next();)
						throw new NoMemberFoundException(
								"Claim Reference Number Not Exist  : ");

					chkclaimRefavil.close();
					chkclaimRefavil = null;
				}
				String claimappro = (new StringBuilder())
						.append("select clm_ref_no from claim_detail where clm_ref_no='")
						.append(enterclaimReff).append("' ").toString();
				ResultSet claimapptest;
				for (claimapptest = str1.executeQuery(claimappro); claimapptest
						.next();)
					throw new NoMemberFoundException(
							"Claim Application Already Approved  : ");

				claimapptest.close();
				claimapptest = null;
				ResultSet results = str.executeQuery(qury2);
				String memID = null;
				ArrayList claimDeclrationArr = new ArrayList();
				if (!results.next())
					throw new NoMemberFoundException(
							"Member Id and claim Reference Number Mismatch   : ");
				results.beforeFirst();
				ClaimActionForm claimdetail2 = new ClaimActionForm();
				for (; results.next(); claimdetail2
						.setClaimdeatil(claimDeclrationArr)) {
					ClaimActionForm claimdetail = new ClaimActionForm();
					memID = results.getString(1);
					claimdetail2.setMemberID(memID);
					claimdetail2.setClaimRefNum(results.getString(2));
					claimdetail2.setSsiUnitName(results.getString(3));
					claimdetail2.setDtOfNPAReportedToCGTSI(results.getDate(4));
					claimdetail.setCgpanNo(results.getString(5));
					claimdetail.setAppApproveDate(results.getDate(6));
					claimdetail.setAppAmount(results.getString(7));
					claimDeclrationArr.add(claimdetail);
				}

				if (memID.equals(""))
					throw new NoMemberFoundException(
							"Member ID Does Not Exsist : ");
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
				claimdetail2.setDateOfdeclartionRecive(date);
				BeanUtils.copyProperties(claimForm, claimdetail2);
			} finally {

				DBConnection.freeConnection(connection);
				Log.log(Log.INFO, "ClaimAction", "getDeclarationDetailData",
						"Exited");
			}
		}
		return mapping.findForward("detailsuccess");
	}

	public ActionForward SaveDeclartaionDetailData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm dynaForm;
		String Clamireff;
		String converttoDBform;
		String falg;
		Connection connection;
		Log.log(Log.INFO, "ClaimAction", "SaveDeclartaionDetailData", "Entered");
		dynaForm = (ClaimActionForm) form;
		boolean flagValue = false;
		String Dateofdeclartionrecvied = "";
		Clamireff = "";
		String memId = "";
		try {
			Clamireff = dynaForm.getClaimRefNum().toString();
			Dateofdeclartionrecvied = request.getParameter(
					"dateOfdeclartionRecive").toString();
			flagValue = dynaForm.getBooleanProperty();
		} catch (Exception e) {
			e.getMessage();
		}
		if (Dateofdeclartionrecvied.equals("")) {
			throw new NoMemberFoundException("Date Is Null  : ");
		}
		converttoDBform = DateHelper.stringToDBDate(Dateofdeclartionrecvied);
		// if(!falgvalue){
		if (flagValue) {
			// break MISSING_BLOCK_LABEL_268;

			falg = "Y";
			connection = DBConnection.getConnection();
			try {
				HttpSession session = request.getSession(false);
				String createdModifiedBy = (String) session
						.getAttribute("USER_ID");
				Statement str = connection.createStatement();
				String qury = (new StringBuilder())
						.append("update Claim_detail_temp set clm_declaration_recvd='")
						.append(falg)
						.append("',clm_decl_recvd_dt='")
						.append(converttoDBform)
						.append("',CLM_CREATED_MODIFIED_BY='")
						.append(createdModifiedBy)
						.append("',CLM_CREATED_MODIFIED_DT=SYSDATE where clm_ref_no='")
						.append(Clamireff).append("'").toString();
				str.executeUpdate(qury);
				request.setAttribute("message",
						"Declaration Recived Update Sucsessfully ");
				dynaForm.setEnterCgpan("");
				dynaForm.setAppRefNo("");
				dynaForm.setEnterCgpan("");
				dynaForm.setBooleanProperty(false);
				str.close();
				str = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBConnection.freeConnection(connection);
			}
		} else {
			// break MISSING_BLOCK_LABEL_279;
			throw new NoMemberFoundException(
					"Please Select Check Box For Update  : ");
		}
		Log.log(Log.INFO, "ClaimAction", "SaveDeclartaionDetailData", "Exited");
		return mapping.findForward("successfullUpdate");
	}

	public ActionForward getRecoveryDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "getRecoveryDetail", "Entered");
		ClaimActionForm claimfm = (ClaimActionForm) form;
		claimfm.setEnterMember("");
		claimfm.setEnterCgpan("");
		Log.log(Log.INFO, "ClaimAction", "getRecoveryDetail", "Exited");
		return mapping.findForward("recoveryDetailsuccess");
	}

	public ActionForward getRecoveryDetailData1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String enteredMemberId;
		String enterCgpan;
		ActionForm dynaForm;
		Connection connection = null;
		Log.log(Log.INFO, "ClaimAction", "getRecoveryDetailData", "Entered");
		enteredMemberId = null;
		enterCgpan = null;
		dynaForm = form;
		try {
			enteredMemberId = request.getParameter("enterMember");
			enterCgpan = request.getParameter("enterCgpan").toUpperCase();
		} catch (Exception e) {
			throw new NoMemberFoundException("Enter CGPAN and  Member Id.");
		}
		if (enteredMemberId.equals(null) || enterCgpan.equals(null))
			;
		// break MISSING_BLOCK_LABEL_1210;
		connection = DBConnection.getConnection();
		try {
			// Statement str = connection.createStatement(1005, 1007);
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String one = enteredMemberId.substring(0, 4);
			String two = enteredMemberId.substring(4, 8);
			String three = enteredMemberId.substring(8, 12);
			String MemIdavailbel = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c \nwhere  c.mem_bnk_id='")
					.append(one).append("' and\n").append("c.mem_zne_id='")
					.append(two).append("' and\n").append("c.mem_brn_id='")
					.append(three).append("'").toString();
			ResultSet rsforavailmemid = str.executeQuery(MemIdavailbel);
			if (!rsforavailmemid.next()) {
				throw new NoMemberFoundException(" Member Id  Not Exsist : ");
			}
			rsforavailmemid.close();
			rsforavailmemid = null;
			String cgQury = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='")
					.append(enterCgpan).append("'").toString();
			ResultSet rsforvalid = str.executeQuery(cgQury);
			String memberid = "";
			if (!rsforvalid.next()) {
				throw new NoMemberFoundException(" CGPAN  Not Exsist : ");
			}
			rsforvalid.beforeFirst();
			while (rsforvalid.next()) {
				memberid = rsforvalid.getString(1);
			}
			if (!memberid.equals(enteredMemberId)) {
				throw new NoMemberFoundException(
						" Enterd Member Id and CGPAN  Not Associated. ");
			}
			String crefQuery = (new StringBuilder())
					.append("select clm_Ref_no from claim_detail  \nwhere bid in \n(select s.bid from ssi_detail s,application_detail a \nwhere a.ssi_Reference_number = s.ssi_Reference_number \nand a.cgpan = '")
					.append(enterCgpan).append("'\n")
					.append("and  a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id='")
					.append(enteredMemberId).append("')").toString();
			ResultSet rsforClaimRefNo;
			for (rsforClaimRefNo = str.executeQuery(crefQuery); !rsforClaimRefNo
					.next();) {
				throw new NoMemberFoundException(
						" Claim Application Does Not Exsist for Entred CGPAN and Member Id. ");
			}
			rsforClaimRefNo.beforeFirst();
			String retrivedClmRefNo;
			for (retrivedClmRefNo = ""; rsforClaimRefNo.next(); retrivedClmRefNo = rsforClaimRefNo
					.getString(1))
				;
			HttpSession session = request.getSession(true);
			session.setAttribute("clmReff", retrivedClmRefNo);
			session.setAttribute("cgpan", enterCgpan);
			String quryfortestapp = (new StringBuilder())
					.append("select clm_status,clm_installment_flag from claim_detail where clm_ref_no ='")
					.append(retrivedClmRefNo).append("'").toString();
			ResultSet rsforchkapp = str.executeQuery(quryfortestapp);
			String clmstatus = "";
			for (String clminstallmentgflag = ""; rsforchkapp.next(); clminstallmentgflag = rsforchkapp
					.getString(2))
				clmstatus = rsforchkapp.getString(1);

			if (!clmstatus.equals("AP")) {
				throw new NoMemberFoundException(
						" The Claim Application is Not Approved.");
			}
			String QuryForchcond = (new StringBuilder())
					.append("select * from claim_application_amount where cgpan='")
					.append(enterCgpan).append("'").toString();
			ResultSet rsforchkCond = str.executeQuery(QuryForchcond);
			if (!rsforchkCond.next()) {
				throw new NoMemberFoundException(
						" The Claim Applications for first Installment not Released.");
			}
			String QuryForRetriveInfo = (new StringBuilder())
					.append("select c.CLM_MLI_NAME,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id mem_id ,c.CLM_PLACE,c.clm_ref_no,ca.cgpan,ssi_unit_name unit,Ctd_TC_CLM_ELIG_AMT elg_amt,CTD_TC_FIRST_INST_PAY_AMT First_Inst\nfrom claim_detail c,ssi_detail s,claim_application_amount ca,CLAIM_TC_DETAIL T\nwhere ca.clm_ref_no=c.clm_ref_no\nand  c.clm_status='AP'\nand  c.clm_installment_flag='F'\nand  c.bid=s.bid\nAND t.CGPAN=ca.CGPAN\nand c.clm_ref_no in(select clm_ref_no from claim_application_amount where cgpan='")
					.append(enterCgpan)
					.append("')\n")
					.append("union\n")
					.append("select c.CLM_MLI_NAME,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id mem_id ,c.CLM_PLACE,c.clm_ref_no,ca.cgpan,ssi_unit_name unit,Cwd_wC_CLM_ELIG_AMT elg_amt,Cwd_wC_FIRST_INST_PAY_AMT First_Inst\n")
					.append("from claim_detail c,ssi_detail s,claim_application_amount ca,CLAIM_wC_DETAIL T\n")
					.append("where ca.clm_ref_no=c.clm_ref_no\n")
					.append("and  c.clm_status='AP'\n")
					.append("and  c.clm_installment_flag='F'\n")
					.append("and  c.bid=s.bid\n")
					.append("AND t.CGPAN=ca.CGPAN\n")
					.append("and c.clm_ref_no in(select clm_ref_no from claim_application_amount where cgpan='")
					.append(enterCgpan).append("')").toString();
			ResultSet rsforRetrirvedInfo = str.executeQuery(QuryForRetriveInfo);
			ClaimActionForm claimActionForm = new ClaimActionForm();
			if (rsforRetrirvedInfo.next()) {
				claimActionForm.setMliName(rsforRetrirvedInfo.getString(1));
				claimActionForm.setMliId(rsforRetrirvedInfo.getString(2));
				claimActionForm.setPlaceforClmRecovery(rsforRetrirvedInfo
						.getString(3));
				claimActionForm
						.setClmRefNumber(rsforRetrirvedInfo.getString(4));
				claimActionForm.setCgpanforclamRecovery(rsforRetrirvedInfo
						.getString(5));
				claimActionForm.setUnitName(rsforRetrirvedInfo.getString(6));
				claimActionForm.setClmEligibleAmt(rsforRetrirvedInfo
						.getDouble(7));
				claimActionForm.setFirstinstalmentrelease(rsforRetrirvedInfo
						.getDouble(8));
			}
			ArrayList viewArr = new ArrayList();
			String viewquery = (new StringBuilder())
					.append("select * from CLM_CGPAN_RECOVERY_DETAIL where clm_ref_no='")
					.append(retrivedClmRefNo).append("'").toString();
			ClaimActionForm claimActionForm2;
			for (ResultSet rsforViewData = str.executeQuery(viewquery); rsforViewData
					.next(); viewArr.add(claimActionForm2)) {
				claimActionForm2 = new ClaimActionForm();
				claimActionForm2.setDateOfreciept(rsforViewData.getDate(4));
				claimActionForm2.setAmtRecipt(rsforViewData.getDouble(5));
				claimActionForm2.setExpIncforRecovery(rsforViewData
						.getDouble(6));
				claimActionForm2.setExpDeducted(rsforViewData.getString(7));
				claimActionForm2.setNetRecovery(rsforViewData.getDouble(8));
				claimActionForm2.setDdNo(rsforViewData.getString(9));
				claimActionForm2.setDdDate(rsforViewData.getDate(10));
				claimActionForm2.setRemark(rsforViewData.getString(11));
			}

			String clmRefnoId = claimActionForm.getClmRefNumber();
			String selectRecovery = (new StringBuilder())
					.append("SELECT CCRD_FINAL_RECOVERY  FROM CLM_CGPAN_RECOVERY_DETAIL WHERE CLM_REF_NO='")
					.append(clmRefnoId).append("' ").toString();
			String finalrecovr = "";
			for (ResultSet rs = str.executeQuery(selectRecovery); rs.next();) {
				finalrecovr = rs.getString(1);
				if (finalrecovr.equals("Y")) {
					throw new NoMemberFoundException(
							"Final recovery Alreday Made for Claim . ");
				}
			}

			claimActionForm.setViewRecArr(viewArr);
			BeanUtils.copyProperties(dynaForm, claimActionForm);
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ClaimAction", "getRecoveryDetailData", "Exited");
		return mapping.findForward("ClaimRecoverysuccess");
	}

	public ActionForward SaveClaimrecovryData1(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String userid;
		double amtreciept;
		double expInForrecover;
		String booleanExp;
		double netrecover;
		String ddNo;
		String remark;
		String clmreff;
		String cgPan;
		String currentDate;
		String reciptDate;
		String dddbDate;
		String falgvalue;
		Connection connection;
		Log.log(Log.INFO, "ClaimAction", "SaveClaimrecovryData", "Entered");
		ClaimActionForm actionform = (ClaimActionForm) form;
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("USER");
		userid = user.getUserId();
		Date recDate1 = actionform.getDateOfreciept();
		String recDate = recDate1.toString();
		amtreciept = actionform.getAmtRecipt();
		expInForrecover = actionform.getExpIncforRecovery();
		boolean booleanExp1 = actionform.isBooleanExpInc();
		booleanExp = "";
		if (booleanExp1)
			booleanExp = "Y";
		else
			booleanExp = "N";
		netrecover = actionform.getNetRecovery();
		ddNo = actionform.getDdNo();
		Date ddDate1 = actionform.getDdDate();
		String ddDate = ddDate1.toString();
		remark = actionform.getRemark();
		clmreff = (String) session.getAttribute("clmReff");
		cgPan = (String) session.getAttribute("cgpan");
		Date date = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String toadsDate = formatter.format(date);
		currentDate = DateHelper.stringToDBDate(toadsDate);
		reciptDate = DateHelper.stringToDBDate(recDate);
		dddbDate = DateHelper.stringToDBDate(ddDate);
		boolean falgvalue1 = false;
		falgvalue = "";
		try {
			falgvalue1 = actionform.isBooleanFinalRecovery();
			if (falgvalue1)
				falgvalue = "Y";
			else
				falgvalue = "N";
		} catch (Exception e) {
			e.getMessage();
		}
		int rowCount = 1;
		connection = DBConnection.getConnection();
		int count = 0;
		try {
			Statement str = connection.createStatement();
			String insertRecovery = (new StringBuilder())
					.append("INSERT INTO CLM_CGPAN_RECOVERY_DETAIL( CCRD_SR_NO, CLM_REF_NO, CGPAN, CCRD_RECEIPT_DATE, CCRD_RECOVERY_AMOUNT, CCRD_RECOVERY_EXPENSES, CCRD_EXPENSES_DEDUCTED, CCRD_NET_RECOVERY, CCRD_REC_DD_NO,  CCRD_REC_DD_DT, CCRD_REMARKS,CCRD_CREATED_MODIFIED_BY,CCRD_CREATED_MODIFIED_DT,CCRD_FINAL_RECOVERY )\nVALUES (CLM_CGP_REC_SEQ.nextval,'")
					.append(clmreff).append("', '").append(cgPan)
					.append("', '").append(reciptDate).append("' ,'")
					.append(amtreciept).append("' ,'").append(expInForrecover)
					.append("' ,'").append(booleanExp).append("' ,'")
					.append(netrecover).append("' ,'").append(ddNo)
					.append("' ,'").append(dddbDate).append("' ,'")
					.append(remark).append("','").append(userid).append("','")
					.append(currentDate).append("' ,'").append(falgvalue)
					.append("')").toString();
			str.executeUpdate(insertRecovery);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ClaimAction", "SaveClaimrecovryData", "Exited");
		return mapping.findForward("recoverysuccess");
	}

	public ActionForward updateClaimSettledDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "updateClaimSettledDetailsNew",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetTheForm(mapping, request);
		Log.log(Log.INFO, "ClaimAction", "updateClaimSettledDetailsNew",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward afterUpdateClaimSettledDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "afterUpdateClaimSettledDetails",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Log.log(Log.INFO, "ClaimAction", "afterUpdateClaimSettledDetails",
				"Exited");
		return mapping.findForward("success");
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward forwardToNextPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);

		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_CLAIM_FOR"))
				&& session.getAttribute("subMenuItem").equals(
						MenuOptions.getMenu("CP_CLAIM_FOR_FIRST_INSTALLMENT"))) {
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage", "Entered");
			ClaimActionForm claimForm = (ClaimActionForm) form;
			StringTokenizer tokenizer = null;
			ClaimsProcessor processor = new ClaimsProcessor();
			String memberId = claimForm.getMemberId().toUpperCase().trim();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			String inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			claimForm = validateMemIdBidCgpan(memberId, borrowerId, inputcgpan,
					claimForm);
			memberId = claimForm.getMemberId();
			borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			HashMap whichClmApplicationsHasUserFiled = processor
					.getClmRefAndFlagDtls(bankId, zoneId, branchId, borrowerId);
			if (whichClmApplicationsHasUserFiled != null
					&& whichClmApplicationsHasUserFiled.size() > 0) {
				Set valuesSet = whichClmApplicationsHasUserFiled.keySet();
				for (Iterator valuesIterator = valuesSet.iterator(); valuesIterator
						.hasNext();) {
					String installmentFlag = null;
					String cgclan = null;
					String firstSettlementDate = null;
					String id = (String) valuesIterator.next();
					String val = (String) whichClmApplicationsHasUserFiled
							.get(id);
					if (id.equals("F")) {
						if (val == null || val.equals(""))
							throw new NoDataException(
									"Application for Claim First Installment has been filed and is pending Processing by CGTSI.");
						if (val.equals("$"))
							throw new NoDataException(
									"Application for Claim First Installment has already been filed and is rejected.");
						if (val.equals("^"))
							throw new NoDataException(
									"Claim Application has been completely settled.");
						if (!val.equals("")) {
							tokenizer = new StringTokenizer(val, "#");
							boolean isFlagRead = false;
							boolean isCGCLANRead = false;
							boolean isSettlementDateRead = false;
							while (tokenizer.hasMoreTokens()) {
								String token = tokenizer.nextToken();
								if (!isSettlementDateRead)
									if (!isCGCLANRead) {
										if (!isFlagRead) {
											installmentFlag = token;
											isFlagRead = true;
										} else {
											cgclan = token;
											isCGCLANRead = true;
										}
									} else {
										firstSettlementDate = token;
										isSettlementDateRead = true;
									}
							}
							if (firstSettlementDate.equals("-")) {
								throw new NoDataException(
										"Application for First Claim Installment has already been filed and approved and pending for Settlement by CGTSI");
							} else {
								throw new NoDataException(
										"Application for First Claim Installment has already been Filed, Approved and Settled by CGTSI");
							}
						}
					} else if (!id.equals("S"))
						;
				}

			}
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					"retriving the details to check if member has paid guarantee fee ......");
			Vector gfeeDtlsVector = processor.getLockInDetails(borrowerId);// cgpan,guarantee
																			// start
																			// date,last
																			// disbursement
																			// date
			Vector gfeePaidCGPANS = new Vector();
			boolean gfeepaid = false;
			for (int i = 0; i < gfeeDtlsVector.size(); i++) {
				HashMap gfeedtl = (HashMap) gfeeDtlsVector.elementAt(i);
				if (gfeedtl != null) {
					Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
							(new StringBuilder()).append("Printing gfeedtl :")
									.append(gfeedtl).toString());
					String cgpn = (String) gfeedtl.get("CGPAN");
					Date gfeedate = (Date) gfeedtl.get("GUARANTEESTARTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"forwardToNextPage",
							(new StringBuilder())
									.append("------> Printing gfeedate :")
									.append(gfeedate).toString());
					if (gfeedate != null) {
						gfeepaid = true;
						if (!gfeePaidCGPANS.contains(cgpn))
							gfeePaidCGPANS.addElement(cgpn);
					} else {
						gfeepaid = false;
					}
					gfeedtl = null;
				}
			}

			gfeeDtlsVector = null;
			if (gfeePaidCGPANS.size() == 0) {
				throw new NoDataException(
						" Kindly update the disbursement details online before proceeding for claim lodgment. The menu is Guarantee Maintenance-->Periodic Info--> Disbursement details. Please make sure Guarantee Fee has been paid and Disbursement Details, if any, are available for the CGPAN(s) of the Borrower.");
			}
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					"retrieving details to check if npa details exist for the borrower......");
			HashMap npadetails = processor.isNPADetailsAvailable(borrowerId);
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					(new StringBuilder()).append("Printing NPA Details :")
							.append(npadetails).toString());
			String willfulDefaulter = null;
			boolean npaDtlsAvl = false;
			if (npadetails == null) {
				session.setAttribute("MEMBERID", memberId);
				session.setAttribute("BORROWERID", borrowerId);
				throw new NoDataException(
						"NPA Details not available.Please go to GuaranteeMaintenance-->PeriodicInfo-->NPA Details.");
				// return
				// mapping.findForward("npaDetailsPage");//-------------from
				// here showNPADetails
			}
			if (npadetails.size() == 0) {
				session.setAttribute("MEMBERID", memberId);
				session.setAttribute("BORROWERID", borrowerId);
				throw new NoDataException(
						"NPA Details not available.Please go to GuaranteeMaintenance-->PeriodicInfo-->NPA Details.");
				// return mapping.findForward("npaDetailsPage");
			}
			HashMap npadtlMainTable = (HashMap) npadetails.get("MAIN");
			if (npadtlMainTable != null && npadtlMainTable.size() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				java.util.Date npaCreatedDate = (java.util.Date) npadtlMainTable
						.get("npaCreatedDate");
				if (npaCreatedDate == null) {
					throw new NoDataException(
							"Please Update Npa Details.Please go to GuaranteeMaintenance-->PeriodicInfo-->NPA Details.");
				}
				Date npaClassified = (Date) npadtlMainTable
						.get("NPAClassifiedDate");

				String npaClassifiedDateStr = sdf.format(npaClassified);
				claimForm.setDateOnWhichAccountClassifiedNPA(npaClassified);
				if (npaClassifiedDateStr != null)
					claimForm.setNpaDateNew(npaClassifiedDateStr);
				willfulDefaulter = (String) npadtlMainTable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null)
					npaDtlsAvl = npaDtlsAvl || true;
			}
			HashMap npadtltemptable = (HashMap) npadetails.get("TEMP");
			if (npadtltemptable != null && npadtltemptable.size() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				java.util.Date npaCreatedDate = (java.util.Date) npadtltemptable
						.get("npaCreatedDate");
				if (npaCreatedDate == null) {
					throw new NoDataException(
							"Please Update Npa Details.Please go to GuaranteeMaintenance-->PeriodicInfo-->NPA Details.");
				}
				Date npaClassified = (Date) npadtltemptable
						.get("NPAClassifiedDate");

				String npaClassifiedDateStr = sdf.format(npaClassified);
				claimForm.setDateOnWhichAccountClassifiedNPA(npaClassified);
				if (npaClassifiedDateStr != null)
					claimForm.setNpaDateNew(npaClassifiedDateStr);
				willfulDefaulter = (String) npadtltemptable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null)
					npaDtlsAvl = npaDtlsAvl || true;
			}
			if (!npaDtlsAvl) {
				Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
						"Error : npa details do not exist for the borrower.");
				throw new NoDataException(
						"NPA Details not available.Please go to GuaranteeMaintenance-->PeriodicInfo-->NPA Details.");
			}
			LegalProceedingsDetail legaldetails = null;
			Log.log(Log.INFO,
					"ClaimAction",
					"forwardToNextPage",
					"retrieving the details to check if there are legal proceeding details for the borrower.......");
			legaldetails = processor.isLegalProceedingsDetailAvl(borrowerId);
			boolean legalDtlsAvl = legaldetails
					.getAreLegalProceedingsDetailsAvl();
			if (!legalDtlsAvl)
				Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
						"Error: Legal Details are not available for the borrower.......");

			Log.log(4, "ClaimAction", "forwardToNextPage",
					"retrieving the details to check if disbursement details exist......");
			Hashtable disbursementdetails = processor
					.isDisbursementDetailsAvl(borrowerId);
			if (disbursementdetails == null)
				throw new NoDataException(
						"No Disbursement Details available.Please go to GuaranteeMaintenance-->PeriodicInfo-->Disbursement Details.");
			if (disbursementdetails.size() == 0)
				throw new NoDataException(
						"No Disbursement Details available.Please go to GuaranteeMaintenance-->PeriodicInfo-->Disbursement Details.");
			Enumeration cgpankeys = disbursementdetails.keys();
			String message = "";
			boolean isDisbursemntDtlsAvl = true;
			while (cgpankeys.hasMoreElements()) {
				String cgpan = (String) cgpankeys.nextElement();
				String gfeecgpan = null;
				for (int i = 0; i < gfeePaidCGPANS.size(); i++) {
					gfeecgpan = (String) gfeePaidCGPANS.elementAt(i);
					if (gfeecgpan != null && cgpan != null
							&& gfeecgpan.equals(cgpan)
							&& "TC".equals(cgpan.substring(cgpan.length() - 2))) {
						String status = (String) disbursementdetails.get(cgpan);
						if (!status.equals("Y"))
							isDisbursemntDtlsAvl = false;
					}
				}

			}
			if (!isDisbursemntDtlsAvl) {
				Log.log(4,
						"ClaimAction",
						"forwardToNextPage",
						"Error: Final Disbursement details not available for those CGPAN(s) whose Guarantee Fee has been paid.");
				throw new NoDataException(
						"Final Disbursement details not available for those CGPAN(s) whose Guarantee Fee has been paid. Please furnish Final Disbursement Details");
			}

			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					"retrieving the details to check if lock-in period is over......");
			boolean islockinperiodover = processor
					.isLockinPeriodOver(borrowerId);
			Vector lockindetails = processor.getLockInDetails(borrowerId);
			String lckdtl_cgpan = null;
			Date lckdtl_gstartdate = null;
			Date lckdtl_dtlastdsbrsmnt = null;
			Date lckdtl_lockin_start_date = null;
			Date tempdate = null;
			Date tdate = new Date();
			if (lockindetails.size() > 0) {
				for (int i = 0; i < lockindetails.size(); i++) {
					HashMap temp = (HashMap) lockindetails.elementAt(i);
					lckdtl_cgpan = (String) temp.get("CGPAN");
					lckdtl_gstartdate = (Date) temp.get("GUARANTEESTARTDT");
					lckdtl_dtlastdsbrsmnt = (Date) temp.get("LASTDSBRSMNTDT");
					// System.out.println("lckdtl_cgpan:"+lckdtl_cgpan+"--lckdtl_gstartdate:"+lckdtl_gstartdate+"--lckdtl_dtlastdsbrsmnt:"+lckdtl_dtlastdsbrsmnt);
					if (lckdtl_cgpan != null) {
						if (lckdtl_gstartdate != null
								&& lckdtl_dtlastdsbrsmnt != null) {
							if (lckdtl_gstartdate
									.compareTo(lckdtl_dtlastdsbrsmnt) < 0)
								tempdate = lckdtl_dtlastdsbrsmnt;
							if (lckdtl_gstartdate
									.compareTo(lckdtl_dtlastdsbrsmnt) > 0)
								tempdate = lckdtl_gstartdate;
							if (lckdtl_gstartdate
									.compareTo(lckdtl_dtlastdsbrsmnt) == 0)
								tempdate = lckdtl_gstartdate;
						} else if (lckdtl_gstartdate != null
								&& lckdtl_dtlastdsbrsmnt == null)
							tempdate = lckdtl_gstartdate;
						if (tdate.compareTo(tempdate) > 0)
							tdate = tempdate;
					}
					// System.out.println("tempdate:"+tempdate);
				}

			}
			tempdate = tdate;
			Administrator admin = new Administrator();
			ParameterMaster parameterMaster = admin.getParameter();
			int lockinperiodmonths = parameterMaster.getLockInPeriod();
			Date lockinperiodenddate = processor.getDate(tempdate,
					lockinperiodmonths);
			Date lockinperiodexpirydate = processor.getDate(
					lockinperiodenddate, 12);
			Date npaClassfied = claimForm.getDateOnWhichAccountClassifiedNPA();
			Date npaExpiryForLodgement = processor.getDate(npaClassfied, 12);
			// System.out.println("lockinperiodmonths:"+lockinperiodmonths+"--lockinperiodenddate:"+lockinperiodenddate+"--lockinperiodexpirydate:"+lockinperiodexpirydate);
			// System.out.println("npaClassfied:"+npaClassfied+"--npaExpiryForLodgement:"+npaExpiryForLodgement+"--npaExpiryForLodgement:"+npaExpiryForLodgement);
			if (!islockinperiodover) {
				Log.log(Log.INFO, "ClaimAction", "forwardToNextPage", "l......");
				throw new LockInPeriodNotCompletedException(
						"Lock In Period Not Completed for the Borrower.");
			}
			if (lockinperiodexpirydate.compareTo(npaExpiryForLodgement) < 0) {
				lockinperiodexpirydate = npaExpiryForLodgement;
				Date currentDate = new Date();

				// System.out.println((new
				// StringBuilder()).append("lockinperiodexpirydate").append(lockinperiodexpirydate).toString());
				// Calendar calendar1 = Calendar.getInstance();
				// calendar1.set(Calendar.DATE, 12);
				// calendar1.set(Calendar.MONTH, 7);
				// calendar1.set(Calendar.YEAR, 2013);
				// java.util.Date fromDt = calendar1.getTime();
				// System.out.println((new
				// StringBuilder()).append("fromDt").append(fromDt).toString());
				// Calendar calendar2 = Calendar.getInstance();
				// calendar2.set(Calendar.DATE, 13);
				// calendar2.set(Calendar.MONTH, 7);
				// calendar2.set(Calendar.YEAR, 2013);
				// java.util.Date toDate = calendar2.getTime();
				// Calendar calendar3 = Calendar.getInstance();
				// calendar3.set(Calendar.DATE, 14);
				// calendar3.set(Calendar.MONTH, 7);
				// calendar3.set(Calendar.YEAR, 2013);
				// java.util.Date toDate3 = calendar3.getTime();
				// Calendar calendar4 = Calendar.getInstance();
				// calendar4.set(Calendar.DATE, 15);
				// calendar4.set(Calendar.MONTH, 7);
				// calendar4.set(Calendar.YEAR, 2013);
				// java.util.Date toDate4 = calendar4.getTime();
				// System.out.println((new
				// StringBuilder()).append("toDate").append(toDate).toString());
				// if(lockinperiodexpirydate.compareTo(fromDt) == 1 ||
				// lockinperiodexpirydate.compareTo(toDate) == 1 ||
				// lockinperiodexpirydate.compareTo(toDate3) == 1 ||
				// lockinperiodexpirydate.compareTo(toDate4) == 1)
				// {
				// Calendar calendar5 = Calendar.getInstance();
				// calendar5.set(Calendar.DATE, 31);
				// calendar5.set(Calendar.MONTH, 9);
				// calendar5.set(Calendar.YEAR, 2013);
				// lockinperiodexpirydate = calendar5.getTime();
				// // System.out.println((new
				// StringBuilder()).append("lockinperiodexpirydate2").append(lockinperiodexpirydate).toString());
				// //System.out.println((new
				// StringBuilder()).append("ddddd").append(lockinperiodexpirydate.compareTo(currentDate)).toString());
				// }

				if (lockinperiodexpirydate.compareTo(currentDate) < 0) {
					throw new DatabaseException(
							"The lending institution may invoke the guarantee in respect of credit facility within a maximum period of one year from date of NPA, if NPA is after lock-in period or within one year of lock-in period, if NPA is within lock-in period ");
				}
			} else if (lockinperiodexpirydate.compareTo(npaExpiryForLodgement) > 0) {
				Date currentDate = new Date();
				if (lockinperiodexpirydate.compareTo(currentDate) < 0) {
					throw new DatabaseException(
							"The lending institution may invoke the guarantee in respect of credit facility within a maximum period of one year from date of NPA, if NPA is after lock-in period or within one year of lock-in period, if NPA is within lock-in period ");
				}
			}
			ParameterMaster pm = admin.getParameter();
			double minAmntForITPAN = pm.getMinAmtForMandatoryITPAN();
			Vector details = processor.getOTSRequestDetails(borrowerId);
			double amntSanctioned = 0.0D;
			boolean isITPANDtlAvl = false;
			for (int i = 0; i < details.size(); i++) {
				Hashtable dtl = (Hashtable) details.elementAt(i);
				if (dtl != null) {
					Log.log(Log.INFO,
							"ClaimAction",
							"forwardToNextPage",
							(new StringBuilder())
									.append("Printing the Hashmap to check ITPAN Details :")
									.append(dtl).toString());
					String amntSanctionedStr = (String) dtl
							.get("ApprovedAmount");
					if (amntSanctionedStr != null) {
						amntSanctioned = Double.parseDouble(amntSanctionedStr);
						Log.log(Log.INFO,
								"ClaimAction",
								"forwardToNextPage",
								(new StringBuilder())
										.append("Printing the Hashmap to check ITPAN Details amntSanctioned:")
										.append(amntSanctioned).toString());
						if (amntSanctioned > minAmntForITPAN) {
							isITPANDtlAvl = checkIfITPANAvailable(memberId,
									borrowerId);
							if (!isITPANDtlAvl)
								return mapping.findForward("itpanDtlsPage");
						}
					}
				}
			}

			claimForm.setMemberId(memberId);
			claimForm.setBorrowerID(borrowerId);
			npadetails = null;
			gfeePaidCGPANS = null;
			claimForm.setRecoveryFlag("N");
			claimForm.resetTheFirstClaimApplication(mapping, request);
			return mapping.findForward("claimpage");
		}

		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_CLAIM_FOR"))
				&& session.getAttribute("subMenuItem").equals(
						MenuOptions.getMenu("CP_CLAIM_FOR_SECOND_INSTALLMENT"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			ClaimsProcessor processor = new ClaimsProcessor();
			String memberId = claimForm.getMemberId().toUpperCase().trim();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			String inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			claimForm = validateMemIdBidCgpan(memberId, borrowerId, inputcgpan,
					claimForm);
			memberId = claimForm.getMemberId();
			borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			HashMap whichClmApplicationsHasUserFiled = processor
					.getClmRefAndFlagDtls(bankId, zoneId, branchId, borrowerId);
			if (whichClmApplicationsHasUserFiled != null) {
				if (whichClmApplicationsHasUserFiled.size() > 0) {
					StringTokenizer tokenizer = null;
					Set valuesSet = whichClmApplicationsHasUserFiled.keySet();
					for (Iterator valuesIterator = valuesSet.iterator(); valuesIterator
							.hasNext();) {
						String installmentFlag = null;
						String cgclan = null;
						String firstSettlementDate = null;
						String secondSettlementDate = null;
						String id = (String) valuesIterator.next();
						String val = (String) whichClmApplicationsHasUserFiled
								.get(id);
						if (id.equals("F")) {
							if (val == null)
								throw new NoDataException(
										"Application for Claim First Installment is pending for Approval. Please file Application for Second Claim Installment after the Approval and Settlement of Application of Claim First Installment.");
							if (val.equals("$"))
								throw new NoDataException(
										"Application for Claim First Installment has already been filed and is rejected. You cannot file application for Claim Second Installment.");
							if (val.equals(""))
								throw new NoDataException(
										"Please file Application for Claim First Installment before filing for Claim Second Installment.");
							if (val.equals("^"))
								throw new NoDataException(
										"Claim Application has been completely settled.");
							tokenizer = new StringTokenizer(val, "#");
							boolean isFlagRead = false;
							boolean isCGCLANRead = false;
							boolean isSettlementDateRead = false;
							while (tokenizer.hasMoreTokens()) {
								String token = tokenizer.nextToken();
								if (!isSettlementDateRead)
									if (!isCGCLANRead) {
										if (!isFlagRead) {
											installmentFlag = token;
											isFlagRead = true;
										} else {
											cgclan = token;
											isCGCLANRead = true;
										}
									} else {
										firstSettlementDate = token;
										isSettlementDateRead = true;
									}
							}
							if (firstSettlementDate.equals("-")) {
								throw new NoDataException(
										"Application for First Claim Installment has already been filed and approved and pending for Settlement by CGTSI. Please file Application for Second Claim Installment after the Settlement of First Claim Installment.");
							}
						} else if (id.equals("S")) {
							if (val == null)
								throw new NoDataException(
										"Application for Claim Second Installment is pending processing by CGTSI.");
							if (val.equals("$"))
								throw new NoDataException(
										"Application for Claim Second Installment has already been filed and is rejected.");
							if (!val.equals("")) {
								tokenizer = new StringTokenizer(val, "#");
								boolean isFlagRead = false;
								boolean isCGCLANRead = false;
								boolean isSettlementDateRead = false;
								while (tokenizer.hasMoreTokens()) {
									String token = tokenizer.nextToken();
									if (!isSettlementDateRead)
										if (!isCGCLANRead) {
											if (!isFlagRead) {
												installmentFlag = token;
												isFlagRead = true;
											} else {
												cgclan = token;
												isCGCLANRead = true;
											}
										} else {
											secondSettlementDate = token;
											isSettlementDateRead = true;
										}
								}
								if (secondSettlementDate.equals("-")) {
									throw new NoDataException(
											"Application for Second Claim Installment has already been filed and approved and is pending for Settlement by CGTSI.");
								} else {
									throw new NoDataException(
											"Application for Second Claim Installment has been filed, approved and settled by CGTSI.");
								}
							}
						}
					}

				} else {
					throw new NoDataException(
							"Please file Application for First Claim Installment before filing for Second Claim Installment.");
				}
			} else {
				throw new NoDataException(
						"Please file Application for First Claim Installment before filing for Second Claim Installment.");
			}
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					(new StringBuilder()).append("Printing Member Id :")
							.append(memberId).toString());
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					(new StringBuilder()).append("Printing Borrower Id :")
							.append(borrowerId).toString());
			claimForm.setMemberId(memberId);
			claimForm.setBorrowerID(borrowerId);
			claimForm.setRecoveryFlag(null);
			Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
					"Proceeding to Recovery Filter Page ......");
			claimForm.setRecoveryFlag("N");
			claimForm.resetTheSecondClaimApplication(mapping, request);
			return mapping.findForward("claimpage");
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_OTS"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			ClaimsProcessor processor = new ClaimsProcessor();
			String memberId = claimForm.getMemberId().toUpperCase().trim();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			String inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			claimForm = validateMemIdBidCgpan(memberId, borrowerId, inputcgpan,
					claimForm);
			memberId = claimForm.getMemberId();
			borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			Vector otsdetails = processor.getOTSDetails(borrowerId);
			for (int i = 0; i < otsdetails.size(); i++) {
				HashMap map = (HashMap) otsdetails.elementAt(i);
				if (map != null) {
					String willfuldefaulter = (String) map
							.get("WillfulDefaulter");
					if (willfuldefaulter != null)
						throw new ApplicationAlreadyFiledException(
								"OTS Details already exist for the Borrower");
				}
			}

			Vector otsReqDtls = processor.getOTSRequestDetails(borrowerId);
			claimForm.setOtsRequestDtls(otsReqDtls);
			claimForm.setWilfullDefaulter("N");
			otsdetails = null;
			otsReqDtls = null;
			claimForm.resetOTSRequestPage(mapping, request);
			return mapping.findForward("otsdetailspage");
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_EXPORT_CLAIM_FILE"))
				&& session
						.getAttribute("subMenuItem")
						.equals(MenuOptions
								.getMenu("CP_EXPORT_CLAIM_FILE_FIRST_INSTALLMNT"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			ClaimsProcessor processor = new ClaimsProcessor();
			StringTokenizer tokenizer = null;
			String memberId = claimForm.getMemberId().toUpperCase().trim();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			String inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			claimForm = validateMemIdBidCgpan(memberId, borrowerId, inputcgpan,
					claimForm);
			memberId = claimForm.getMemberId();
			borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			HashMap whichClmApplicationsHasUserFiled = processor
					.getClmRefAndFlagDtls(bankId, zoneId, branchId, borrowerId);
			if (whichClmApplicationsHasUserFiled != null
					&& whichClmApplicationsHasUserFiled.size() > 0) {
				Set valuesSet = whichClmApplicationsHasUserFiled.keySet();
				for (Iterator valuesIterator = valuesSet.iterator(); valuesIterator
						.hasNext();) {
					String installmentFlag = null;
					String cgclan = null;
					String firstSettlementDate = null;
					String id = (String) valuesIterator.next();
					String val = (String) whichClmApplicationsHasUserFiled
							.get(id);
					if (id.equals("F")) {
						if (val == null)
							throw new NoDataException(
									"Application for Claim First Installment has been filed and is pending Processing by CGTSI.");
						if (val.equals("$"))
							throw new NoDataException(
									"Application for Claim First Installment has already been filed and is rejected.");
						if (val.equals("^"))
							throw new NoDataException(
									"Claim Application has been completely settled.");
						if (!val.equals("")) {
							tokenizer = new StringTokenizer(val, "#");
							boolean isFlagRead = false;
							boolean isCGCLANRead = false;
							boolean isSettlementDateRead = false;
							while (tokenizer.hasMoreTokens()) {
								String token = tokenizer.nextToken();
								if (!isSettlementDateRead)
									if (!isCGCLANRead) {
										if (!isFlagRead) {
											installmentFlag = token;
											isFlagRead = true;
										} else {
											cgclan = token;
											isCGCLANRead = true;
										}
									} else {
										firstSettlementDate = token;
										isSettlementDateRead = true;
									}
							}
							if (firstSettlementDate.equals("-"))
								throw new NoDataException(
										"Application for First Claim Installment has already been filed and approved and pending for Settlement by CGTSI");
							else
								throw new NoDataException(
										"Application for First Claim Installment has already been Filed, Approved and Settled by CGTSI");
						}
					} else if (!id.equals("S"))
						;
				}

			}
			ClaimApplication claimapplication = new ClaimApplication();
			claimapplication.setBorrowerId(borrowerId);
			com.cgtsi.claim.MemberInfo memberinfo = processor
					.getMemberInfoDetails(memberId);
			claimapplication.setMemberDetails(memberinfo);
			BorrowerInfo borrowerinfo = processor
					.getBorrowerDetails(borrowerId);
			claimapplication.setBorrowerDetails(borrowerinfo);
			Date npaClassifiedDate = null;
			Date npaReportingDate = null;
			String reasonfornpa = null;
			String willfulDefaulter = null;
			HashMap npadetails = processor.isNPADetailsAvailable(borrowerId);
			boolean npaDtlsAvl = false;
			if (npadetails == null) {
				throw new NoDataException(
						"NPA Details are not available. Please go to GuaranteeMaintenance-->PeriodicInfo-->Npa Details.");
			}
			if (npadetails.size() == 0) {
				throw new NoDataException(
						"NPA Details are not available. Please go to GuaranteeMaintenance-->PeriodicInfo-->Npa Details.");
			}
			HashMap npadtlMainTable = (HashMap) npadetails.get("MAIN");
			if (npadtlMainTable != null && npadtlMainTable.size() > 0) {
				willfulDefaulter = (String) npadtlMainTable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaDtlsAvl = npaDtlsAvl || true;
					npaClassifiedDate = (Date) npadtlMainTable
							.get("NPAClassifiedDate");
					npaReportingDate = (Date) npadtlMainTable
							.get("NPAReportingDate");
					reasonfornpa = (String) npadtlMainTable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtlMainTable
							.get("WillFulDefaulter");
				}
			}
			HashMap npadtltemptable = (HashMap) npadetails.get("TEMP");
			if (npadtltemptable != null && npadtltemptable.size() > 0) {
				willfulDefaulter = (String) npadtltemptable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaDtlsAvl = npaDtlsAvl || true;
					npaClassifiedDate = (Date) npadtltemptable
							.get("NPAClassifiedDate");
					npaReportingDate = (Date) npadtltemptable
							.get("NPAReportingDate");
					reasonfornpa = (String) npadtltemptable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtltemptable
							.get("WillFulDefaulter");
				}
			}
			if (!npaDtlsAvl) {
				Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
						"Error : npa details do not exist for the borrower.");
				throw new NoDataException(
						"NPA Details are not available. Please go to GuaranteeMaintenance-->PeriodicInfo-->Npa Details.");
			}
			claimapplication
					.setDateOnWhichAccountClassifiedNPA(npaClassifiedDate);
			claimapplication.setDateOfReportingNpaToCgtsi(npaReportingDate);
			claimapplication.setReasonsForAccountTurningNPA(reasonfornpa);
			LegalProceedingsDetail legalproceedingsdetail = processor
					.isLegalProceedingsDetailAvl(borrowerId);
			if (legalproceedingsdetail == null) {
				throw new NoDataException(
						(new StringBuilder())
								.append("Legal Proceeding do not exit for the Borrower :")
								.append(borrowerId).toString());
			}
			claimapplication.setLegalProceedingsDetails(legalproceedingsdetail);
			Vector cgpanDetails = processor.getCGPANDetailsForBorrowerId(
					borrowerId, memberId);
			Vector cgpans = new Vector();
			Vector tccgpans = new Vector();
			Vector wccgpans = new Vector();
			HashMap hashmap = null;
			Date currentDate = new Date();
			Administrator admin = new Administrator();
			ParameterMaster pm = admin.getParameter();
			int periodTenureExpiryLodgementClaims = pm
					.getPeriodTenureExpiryLodgementClaims();
			for (int i = 0; i < cgpanDetails.size(); i++) {
				hashmap = (HashMap) cgpanDetails.elementAt(i);
				if (hashmap == null || !hashmap.containsKey("CGPAN"))
					continue;
				String cgpan = (String) hashmap.get("CGPAN");
				String cgpanStatus = (String) hashmap.get("APPLICATION_STATUS");
				if (cgpan == null || cgpan.equals(""))
					continue;
				if (hashmap.get("LoanType").equals("TC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						String applicationRefNum = processor
								.getAppRefNumber(cgpan);
						TermLoan termLoanObj = processor.getTermLoan(
								applicationRefNum, "TC");
						int applicationTenure = termLoanObj.getTenure();
						Date guarStartDt = termLoanObj
								.getAmountSanctionedDate();
						Date expiryDate = processor.getDate(guarStartDt,
								applicationTenure);
						Date tenureExpiryDate = processor.getDate(expiryDate,
								periodTenureExpiryLodgementClaims);
						if (currentDate.compareTo(tenureExpiryDate) > 0)
							continue;
					}
					HashMap mp = new HashMap();
					Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
					Double repaidAmnt = (Double) hashmap.get("AMNT_REPAID");
					mp.put("CGPAN", cgpan);
					mp.put("LASTDSBRSMNTDT", dsbrsDt);
					mp.put("AMNT_REPAID", repaidAmnt);
					if (!tccgpans.contains(mp))
						tccgpans.addElement(mp);
				}
				if (hashmap.get("LoanType").equals("WC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						HashMap workingCapitalDtl = processor
								.getWorkingCapital(cgpan);
						Integer applicationTenureObj = (Integer) workingCapitalDtl
								.get("WC_TENURE");
						if (applicationTenureObj != null) {
							int applicationTenure = applicationTenureObj
									.intValue();
							Date guarStartDt = (Date) workingCapitalDtl
									.get("GUARANTEESTARTDT");
							Date expiryDate = processor.getDate(guarStartDt,
									applicationTenure);
							Date tenureExpiryDate = processor.getDate(
									expiryDate,
									periodTenureExpiryLodgementClaims);
							if (currentDate.compareTo(tenureExpiryDate) > 0)
								continue;
						}
					}
					if (!wccgpans.contains(cgpan))
						wccgpans.addElement(cgpan);
				}
				if (hashmap.get("LoanType").equals("CC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						String applicationRefNum = processor
								.getAppRefNumber(cgpan);
						TermLoan termLoanObj = processor.getTermLoan(
								applicationRefNum, "CC");
						int applicationTenure = termLoanObj.getTenure();
						Date guarStartDt = termLoanObj
								.getAmountSanctionedDate();
						Date expiryDate = processor.getDate(guarStartDt,
								applicationTenure);
						Date tenureExpiryDate = processor.getDate(expiryDate,
								periodTenureExpiryLodgementClaims);
						if (currentDate.compareTo(tenureExpiryDate) > 0)
							continue;
					}
					HashMap mp = new HashMap();
					Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
					Double repaidAmnt = (Double) hashmap.get("AMNT_REPAID");
					mp.put("CGPAN", cgpan);
					mp.put("LASTDSBRSMNTDT", dsbrsDt);
					mp.put("AMNT_REPAID", repaidAmnt);
					if (!tccgpans.contains(mp))
						tccgpans.addElement(mp);
				}
				if (!cgpans.contains(cgpan))
					cgpans.addElement(cgpan);
			}

			Vector tlcldetails = new Vector();
			for (int i = 0; i < tccgpans.size(); i++) {
				double repaidAmt = 0.0D;
				HashMap map = (HashMap) tccgpans.elementAt(i);
				if (map != null) {
					TermLoanCapitalLoanDetail tcdetail = new TermLoanCapitalLoanDetail();
					String cgpan = (String) map.get("CGPAN");
					Date dsbrsDt = (Date) map.get("LASTDSBRSMNTDT");
					Double repaidAmnt = (Double) hashmap.get("AMNT_REPAID");
					if (repaidAmnt != null)
						repaidAmt = repaidAmnt.doubleValue();
					tcdetail.setCgpan(cgpan);
					tcdetail.setLastDisbursementDate(dsbrsDt);
					tcdetail.setPrincipalRepayment(repaidAmt);
					if (!tlcldetails.contains(tcdetail))
						tlcldetails.addElement(tcdetail);
				}
			}

			claimapplication.setTermCapitalDtls(tlcldetails);
			ArrayList wcdetails = new ArrayList();
			for (int i = 0; i < wccgpans.size(); i++) {
				WorkingCapitalDetail wcdetail = new WorkingCapitalDetail();
				String cgpan = (String) wccgpans.elementAt(i);
				wcdetail.setCgpan(cgpan);
				if (!wcdetails.contains(wcdetail))
					wcdetails.add(wcdetail);
			}

			claimapplication.setWorkingCapitalDtls(wcdetails);
			if (tccgpans != null && wccgpans != null && tccgpans.size() == 0
					&& wccgpans.size() == 0)
				throw new NoDataException(
						"For this Borrower, there are no Loan Account(s) or the Loan Account(s) have been closed.");
			Vector recoverydetails = new Vector();
			for (int i = 0; i < cgpans.size(); i++) {
				RecoveryDetails recdetail = new RecoveryDetails();
				String cgpan = (String) cgpans.elementAt(i);
				recdetail.setCgpan(cgpan);
				if (!recoverydetails.contains(recdetail))
					recoverydetails.addElement(recdetail);
			}

			claimapplication.setRecoveryDetails(recoverydetails);
			ArrayList claimsummarydetails = new ArrayList();
			for (int i = 0; i < cgpanDetails.size(); i++) {
				HashMap hashmp = (HashMap) cgpanDetails.elementAt(i);
				if (hashmp != null) {
					String cgpan = (String) hashmp.get("CGPAN");
					double approvedamnt = ((Double) hashmp
							.get("ApprovedAmount")).doubleValue();
					ClaimSummaryDtls clmsummarydtl = new ClaimSummaryDtls();
					clmsummarydtl.setCgpan(cgpan);
					clmsummarydtl.setLimitCoveredUnderCGFSI(String
							.valueOf(approvedamnt));
					if (!claimsummarydetails.contains(clmsummarydtl))
						claimsummarydetails.add(clmsummarydtl);
					hashmp = null;
				}
			}

			HashMap details = processor
					.getPrimarySecurityAndNetworthOfGuarantors(borrowerId,
							memberId);
			SecurityAndPersonalGuaranteeDtls sapg = new SecurityAndPersonalGuaranteeDtls();
			DtlsAsOnDateOfSanction asOnSnctn = new DtlsAsOnDateOfSanction();
			double totalNetworth = 0.0D;
			double totalLandVal = 0.0D;
			double totalMachineVal = 0.0D;
			double totalBldgVal = 0.0D;
			double totalOFMAVal = 0.0D;
			double totalCurrAssetsVal = 0.0D;
			double totalOthersVal = 0.0D;
			Double totalNetWorthDouble = (Double) details.get("networth");
			if (totalNetWorthDouble != null)
				totalNetworth = totalNetWorthDouble.doubleValue();
			Double totalLandValDouble = (Double) details.get("LAND");
			if (totalLandValDouble != null)
				totalLandVal = totalLandValDouble.doubleValue();
			Double totalMCValDouble = (Double) details.get("MACHINE");
			if (totalMCValDouble != null)
				totalMachineVal = totalMCValDouble.doubleValue();
			Double totalBldgValDouble = (Double) details.get("BUILDING");
			if (totalBldgValDouble != null)
				totalBldgVal = totalBldgValDouble.doubleValue();
			Double totalOFMAValDouble = (Double) details
					.get("OTHER FIXED MOVABLE ASSETS");
			if (totalOFMAValDouble != null)
				totalOFMAVal = totalOFMAValDouble.doubleValue();
			Double totalCurrAssetsDouble = (Double) details
					.get("CURRENT ASSETS");
			if (totalCurrAssetsDouble != null)
				totalCurrAssetsVal = totalCurrAssetsDouble.doubleValue();
			Double totalOthersValDouble = (Double) details.get("OTHERS");
			if (totalOthersValDouble != null)
				totalOthersVal = totalOthersValDouble.doubleValue();
			asOnSnctn.setNetworthOfGuarantors(totalNetworth);
			asOnSnctn.setValueOfLand(totalLandVal);
			asOnSnctn.setValueOfBuilding(totalBldgVal);
			asOnSnctn.setValueOfMachine(totalOFMAVal);
			asOnSnctn.setValueOfOtherFixedMovableAssets(totalOFMAVal);
			asOnSnctn.setValueOfCurrentAssets(totalCurrAssetsVal);
			asOnSnctn.setValueOfOthers(totalOthersVal);
			sapg.setDetailsAsOnDateOfSanction(asOnSnctn);
			claimapplication.setSecurityAndPersonalGuaranteeDtls(sapg);
			claimapplication.setClaimSummaryDtls(claimsummarydetails);
			claimapplication.setFirstInstallment(true);
			User user = getUserInformation(request);
			String userid = user.getUserId();
			String currentTime = (new java.sql.Date(System.currentTimeMillis()))
					.toString();
			Hashtable claimDetails = new Hashtable();
			claimDetails.put(borrowerId, claimapplication);
			boolean exportObjectStatus = false;
			try {
				String contextPath1 = request.getSession(false)
						.getServletContext().getRealPath("");
				String contextPath = PropertyLoader
						.changeToOSpath(contextPath1);
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objectOutputStream = null;
				String filename = (new StringBuilder()).append(contextPath)
						.append(File.separator).append("Download")
						.append(File.separator).append(userid)
						.append(currentTime).append("CLAIM").append("F")
						.append(".EXP").toString();
				String formattedToOSPath = (new StringBuilder())
						.append(request.getContextPath())
						.append(File.separator).append("Download")
						.append(File.separator).append(userid)
						.append(currentTime).append("CLAIM").append("F")
						.append(".EXP").toString();
				session.setAttribute("fileName", formattedToOSPath);
				File exportFlatFile = new File(filename);
				if (!exportFlatFile.exists())
					exportFlatFile.createNewFile();
				fileOutputStream = new FileOutputStream(exportFlatFile);
				objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(claimDetails);
				objectOutputStream.close();
				fileOutputStream.close();
				exportObjectStatus = true;
			} catch (IOException ioexception) {
				exportObjectStatus = false;
				Log.log(2, "ClaimAction", "Export Claim File",
						(new StringBuilder()).append("Export failed because ")
								.append(ioexception.getMessage()).toString());
				throw new ExportFailedException("Export Failed.");
			}
			wcdetails = null;
			claimsummarydetails = null;
			claimDetails = null;
			npadetails = null;
			hashmap = null;
			cgpanDetails = null;
			cgpans = null;
			tccgpans = null;
			wccgpans = null;
			return mapping.findForward("exportClaimFile");
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_EXPORT_CLAIM_FILE"))
				&& session
						.getAttribute("subMenuItem")
						.equals(MenuOptions
								.getMenu("CP_EXPORT_CLAIM_FILE_SECOND_INSTALLMNT"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			ClaimsProcessor processor = new ClaimsProcessor();
			String memberId = claimForm.getMemberId().toUpperCase().trim();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			String inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			claimForm = validateMemIdBidCgpan(memberId, borrowerId, inputcgpan,
					claimForm);
			memberId = claimForm.getMemberId();
			borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			inputcgpan = claimForm.getCgpan().toUpperCase().trim();
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			HashMap whichClmApplicationsHasUserFiled = processor
					.getClmRefAndFlagDtls(bankId, zoneId, branchId, borrowerId);
			if (whichClmApplicationsHasUserFiled != null) {
				if (whichClmApplicationsHasUserFiled.size() > 0) {
					StringTokenizer tokenizer = null;
					Set valuesSet = whichClmApplicationsHasUserFiled.keySet();
					for (Iterator valuesIterator = valuesSet.iterator(); valuesIterator
							.hasNext();) {
						String installmentFlag = null;
						String cgclan = null;
						String firstSettlementDate = null;
						String secondSettlementDate = null;
						String id = (String) valuesIterator.next();
						String val = (String) whichClmApplicationsHasUserFiled
								.get(id);
						if (id.equals("F")) {
							if (val == null)
								throw new NoDataException(
										"Application for Claim First Installment is pending for Approval. Please file Application for Second Claim Installment after the Approval and Settlement of Application of Claim First Installment.");
							if (val.equals("$"))
								throw new NoDataException(
										"Application for Claim First Installment has already been filed and is rejected. You cannot file application for Claim Second Installment.");
							if (val.equals(""))
								throw new NoDataException(
										"Please file Application for Claim First Installment before filing for Claim Second Installment.");
							if (val.equals("^"))
								throw new NoDataException(
										"Claim Application has been completely settled.");
							tokenizer = new StringTokenizer(val, "#");
							boolean isFlagRead = false;
							boolean isCGCLANRead = false;
							boolean isSettlementDateRead = false;
							while (tokenizer.hasMoreTokens()) {
								String token = tokenizer.nextToken();
								if (!isSettlementDateRead)
									if (!isCGCLANRead) {
										if (!isFlagRead) {
											installmentFlag = token;
											isFlagRead = true;
										} else {
											cgclan = token;
											isCGCLANRead = true;
										}
									} else {
										firstSettlementDate = token;
										isSettlementDateRead = true;
									}
							}
							if (firstSettlementDate.equals("-"))
								throw new NoDataException(
										"Application for First Claim Installment has already been filed and approved and pending for Settlement by CGTSI. Please file Application for Second Claim Installment after the Settlement of First Claim Installment.");
						} else if (id.equals("S")) {
							if (val == null)
								throw new NoDataException(
										"Application for Claim Second Installment is pending processing by CGTSI.");
							if (val.equals("$"))
								throw new NoDataException(
										"Application for Claim Second Installment has already been filed and is rejected.");
							if (!val.equals("")) {
								tokenizer = new StringTokenizer(val, "#");
								boolean isFlagRead = false;
								boolean isCGCLANRead = false;
								boolean isSettlementDateRead = false;
								while (tokenizer.hasMoreTokens()) {
									String token = tokenizer.nextToken();
									if (!isSettlementDateRead)
										if (!isCGCLANRead) {
											if (!isFlagRead) {
												installmentFlag = token;
												isFlagRead = true;
											} else {
												cgclan = token;
												isCGCLANRead = true;
											}
										} else {
											secondSettlementDate = token;
											isSettlementDateRead = true;
										}
								}
								if (secondSettlementDate.equals("-"))
									throw new NoDataException(
											"Application for Second Claim Installment has already been filed and approved and is pending for Settlement by CGTSI.");
								else
									throw new NoDataException(
											"Application for Second Claim Installment has been filed, approved and settled by CGTSI.");
							}
						}
					}

				} else {
					throw new NoDataException(
							"Please file Application for First Claim Installment before filing for Second Claim Installment.");
				}
			} else {
				throw new NoDataException(
						"Please file Application for First Claim Installment before filing for Second Claim Installment.");
			}
			ClaimApplication claimapplication = new ClaimApplication();
			HashMap firstClmDtl = processor.getFirstClmDtlForBid(bankId,
					zoneId, branchId, borrowerId);
			Date settDate = null;
			String claimRefNumber = null;
			String cgclan = null;
			if (firstClmDtl.containsKey("FirstSettlmntDt")) {
				settDate = (Date) firstClmDtl.get("FirstSettlmntDt");
				claimapplication.setClaimSettlementDate(settDate);
			}
			if (firstClmDtl.containsKey("ClaimRefNumber")) {
				claimRefNumber = (String) firstClmDtl.get("ClaimRefNumber");
				claimapplication.setClaimRefNumber(claimRefNumber);
			}
			if (firstClmDtl.containsKey("cgclan")) {
				cgclan = (String) firstClmDtl.get("cgclan");
				claimapplication.setClpan(cgclan);
			}
			Date recallNoticeDt = null;
			if (firstClmDtl.containsKey("RECALL-")) {
				recallNoticeDt = (Date) firstClmDtl.get("RECALL-");
				claimapplication.setDateOfIssueOfRecallNotice(recallNoticeDt);
			}
			claimapplication.setBorrowerId(borrowerId);
			com.cgtsi.claim.MemberInfo memberinfo = processor
					.getMemberInfoDetails(memberId);
			claimapplication.setMemberDetails(memberinfo);
			BorrowerInfo borrowerinfo = processor
					.getBorrowerDetails(borrowerId);
			claimapplication.setBorrowerDetails(borrowerinfo);
			Date npaClassifiedDate = null;
			Date npaReportingDate = null;
			String reasonfornpa = null;
			String willfulDefaulter = null;
			HashMap npadetails = processor.isNPADetailsAvailable(borrowerId);
			boolean npaDtlsAvl = false;
			if (npadetails == null)
				throw new NoDataException(
						"NPA Details are not available. Please enter through Guarantee Maintenance.");
			if (npadetails.size() == 0)
				throw new NoDataException(
						"NPA Details are not available. Please enter through Guarantee Maintenance.");
			HashMap npadtlMainTable = (HashMap) npadetails.get("MAIN");
			if (npadtlMainTable != null && npadtlMainTable.size() > 0) {
				willfulDefaulter = (String) npadtlMainTable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaDtlsAvl = npaDtlsAvl || true;
					npaClassifiedDate = (Date) npadtlMainTable
							.get("NPAClassifiedDate");
					npaReportingDate = (Date) npadtlMainTable
							.get("NPAReportingDate");
					reasonfornpa = (String) npadtlMainTable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtlMainTable
							.get("WillFulDefaulter");
				}
			}
			HashMap npadtltemptable = (HashMap) npadetails.get("TEMP");
			if (npadtltemptable != null && npadtltemptable.size() > 0) {
				willfulDefaulter = (String) npadtltemptable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaDtlsAvl = npaDtlsAvl || true;
					npaClassifiedDate = (Date) npadtltemptable
							.get("NPAClassifiedDate");
					npaReportingDate = (Date) npadtltemptable
							.get("NPAReportingDate");
					reasonfornpa = (String) npadtltemptable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtltemptable
							.get("WillFulDefaulter");
				}
			}
			if (!npaDtlsAvl) {
				Log.log(Log.INFO, "ClaimAction", "forwardToNextPage",
						"Error : npa details do not exist for the borrower.");
				throw new NoDataException(
						"NPA Details are not available. Please enter through Guarantee Maintenance.");
			}
			claimapplication
					.setDateOnWhichAccountClassifiedNPA(npaClassifiedDate);
			claimapplication.setDateOfReportingNpaToCgtsi(npaReportingDate);
			claimapplication.setReasonsForAccountTurningNPA(reasonfornpa);
			LegalProceedingsDetail legalproceedingsdetail = processor
					.isLegalProceedingsDetailAvl(borrowerId);
			if (legalproceedingsdetail == null)
				throw new NoDataException(
						(new StringBuilder())
								.append("Legal Proceeding do not exit for the Borrower :")
								.append(borrowerId).toString());
			claimapplication.setLegalProceedingsDetails(legalproceedingsdetail);
			Vector cgpanDetails = processor.getCGPANDetailsForBorrowerId(
					borrowerId, memberId);
			ArrayList cgpansForClmRefNum = processor
					.getAllCgpansForClmRefNum(claimRefNumber);
			Vector cgpans = new Vector();
			Vector tccgpans = new Vector();
			Vector wccgpans = new Vector();
			HashMap hashmap = null;
			for (int i = 0; i < cgpanDetails.size(); i++) {
				hashmap = (HashMap) cgpanDetails.elementAt(i);
				if (hashmap != null && hashmap.containsKey("CGPAN")) {
					String cgpan = (String) hashmap.get("CGPAN");
					if (cgpan != null && !cgpan.equals("")
							&& cgpansForClmRefNum.contains(cgpan)) {
						if (hashmap.get("LoanType").equals("TC")) {
							HashMap mp = new HashMap();
							Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
							Double repaidAmnt = (Double) hashmap
									.get("AMNT_REPAID");
							mp.put("CGPAN", cgpan);
							mp.put("LASTDSBRSMNTDT", dsbrsDt);
							mp.put("AMNT_REPAID", repaidAmnt);
							if (!tccgpans.contains(mp))
								tccgpans.addElement(mp);
						}
						if (hashmap.get("LoanType").equals("WC")
								&& !wccgpans.contains(cgpan))
							wccgpans.addElement(cgpan);
						if (hashmap.get("LoanType").equals("CC")) {
							HashMap mp = new HashMap();
							Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
							Double repaidAmnt = (Double) hashmap
									.get("AMNT_REPAID");
							mp.put("CGPAN", cgpan);
							mp.put("LASTDSBRSMNTDT", dsbrsDt);
							mp.put("AMNT_REPAID", repaidAmnt);
							if (!tccgpans.contains(mp))
								tccgpans.addElement(mp);
						}
						if (!cgpans.contains(cgpan))
							cgpans.addElement(cgpan);
					}
				}
			}

			Vector tlcldetails = new Vector();
			TermLoanCapitalLoanDetail tlclDtl = null;
			String clmRefNum = (String) firstClmDtl.get("ClaimRefNumber");
			ClaimDetail clmdtl = processor
					.getDetailsForClaimRefNumber(clmRefNum);
			Vector tcVec = clmdtl.getTcDetails();
			for (int i = 0; i < tccgpans.size(); i++) {
				HashMap outMap = (HashMap) tccgpans.elementAt(i);
				if (outMap != null) {
					String outCgpan = (String) outMap.get("CGPAN");
					for (int j = 0; j < tcVec.size(); j++) {
						HashMap innerMap = (HashMap) tcVec.elementAt(j);
						if (innerMap != null) {
							String innerCgpan = (String) innerMap.get("CGPAN");
							if (innerCgpan != null
									&& innerCgpan.equals(outCgpan)) {
								double osAsOnNPADate = ((Double) innerMap
										.get("OS as on NPA")).doubleValue();
								double osAsInCivilSuit = ((Double) innerMap
										.get("OS as in Civil Suit"))
										.doubleValue();
								double osAsInFirstClmLodgement = ((Double) innerMap
										.get("OS as in Clm Logdmnt"))
										.doubleValue();
								outMap.put("OS as on NPA", new Double(
										osAsOnNPADate));
								outMap.put("OS as in Civil Suit", new Double(
										osAsInCivilSuit));
								outMap.put("OS as in Clm Logdmnt", new Double(
										osAsInFirstClmLodgement));
							}
						}
					}

					String cgpan = (String) outMap.get("CGPAN");
					Date dsbrsDt = (Date) outMap.get("LASTDSBRSMNTDT");
					Double obj = (Double) outMap.get("AMNT_REPAID");
					double repaidAmnt = 0.0D;
					if (obj != null)
						repaidAmnt = obj.doubleValue();
					double osAsOnNPADate = ((Double) outMap.get("OS as on NPA"))
							.doubleValue();
					double osAsInCivilSuit = ((Double) outMap
							.get("OS as in Civil Suit")).doubleValue();
					double osAsInFirstClmLodgement = ((Double) outMap
							.get("OS as in Clm Logdmnt")).doubleValue();
					tlclDtl = new TermLoanCapitalLoanDetail();
					tlclDtl.setCgpan(cgpan);
					tlclDtl.setLastDisbursementDate(dsbrsDt);
					tlclDtl.setPrincipalRepayment(repaidAmnt);
					tlclDtl.setOutstandingAsOnDateOfNPA(osAsOnNPADate);
					tlclDtl.setOutstandingStatedInCivilSuit(osAsInCivilSuit);
					tlclDtl.setOutstandingAsOnDateOfLodgement(osAsInFirstClmLodgement);
					if (!tlcldetails.contains(tlclDtl))
						tlcldetails.addElement(tlclDtl);
				}
			}

			Vector wcVec = clmdtl.getWcDetails();
			ArrayList workingCapitalDtls = new ArrayList();
			WorkingCapitalDetail wcDetail = null;
			String outerCgpan = null;
			for (int i = 0; i < wccgpans.size(); i++) {
				outerCgpan = (String) wccgpans.elementAt(i);
				if (outerCgpan != null) {
					for (int j = 0; j < wcVec.size(); j++) {
						HashMap innerMap = (HashMap) wcVec.elementAt(j);
						if (innerMap != null) {
							String innerCgpan = (String) innerMap.get("CGPAN");
							if (innerCgpan != null
									&& innerCgpan.equals(outerCgpan)) {
								wcDetail = new WorkingCapitalDetail();
								String wccgpan = (String) innerMap.get("CGPAN");
								double osAsOnNPADate = ((Double) innerMap
										.get("OS as on NPA")).doubleValue();
								double osAsInCivilSuit = ((Double) innerMap
										.get("OS as in Civil Suit"))
										.doubleValue();
								double osAsInFirstClmLodgement = ((Double) innerMap
										.get("OS as in Clm Logdmnt"))
										.doubleValue();
								wcDetail.setCgpan(wccgpan);
								wcDetail.setOutstandingAsOnDateOfNPA(osAsOnNPADate);
								wcDetail.setOutstandingStatedInCivilSuit(osAsInCivilSuit);
								wcDetail.setOutstandingAsOnDateOfLodgement(osAsInFirstClmLodgement);
								if (!workingCapitalDtls.contains(wcDetail))
									workingCapitalDtls.add(wcDetail);
							}
						}
					}

				}
			}

			claimapplication.setTermCapitalDtls(tlcldetails);
			claimapplication.setWorkingCapitalDtls(workingCapitalDtls);
			if (tccgpans != null && wccgpans != null && tccgpans.size() == 0
					&& wccgpans.size() == 0)
				throw new NoDataException(
						"For this Borrower, there are no Loan Account(s) or the Loan Account(s) have been closed.");
			Vector recoverydetails = new Vector();
			for (int i = 0; i < cgpans.size(); i++) {
				RecoveryDetails recdetail = new RecoveryDetails();
				String cgpan = (String) cgpans.elementAt(i);
				recdetail.setCgpan(cgpan);
				if (!recoverydetails.contains(recdetail))
					recoverydetails.addElement(recdetail);
			}

			claimapplication.setRecoveryDetails(recoverydetails);
			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(
					borrowerId, memberId);
			Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(
					borrowerId, "F");
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
					.getClaimSettlementDetailForBorrower(borrowerId);
			double firstSettlementAmnt = 0.0D;
			Double firstSettlementAmntObj = (Double) settlmntDetails
					.get("FirstSettlmntAmnt");
			if (firstSettlementAmntObj != null)
				firstSettlementAmnt = firstSettlementAmntObj.doubleValue();
			Date firstSettlementDt = (Date) settlmntDetails
					.get("FirstSettlmntDt");
			HashMap dtl = null;
			Vector finalUpdatedDtls = new Vector();
			ArrayList clmSummryDtls = new ArrayList();
			ClaimSummaryDtls dtlSummary = null;
			for (int i = 0; i < updateClmDtls.size(); i++) {
				dtl = (HashMap) updateClmDtls.elementAt(i);
				if (dtl != null) {
					dtlSummary = new ClaimSummaryDtls();
					if (dtl != null) {
						dtl.put("FirstSettlmntAmnt", new Double(
								firstSettlementAmnt));
						dtl.put("FirstSettlmntDt", firstSettlementDt);
						if (!finalUpdatedDtls.contains(dtl))
							finalUpdatedDtls.addElement(dtl);
					}
					String pan = (String) dtl.get("CGPAN");
					dtlSummary.setCgpan(pan);
					String loanType = (String) dtl.get("LoanType");
					dtlSummary.setTypeOfFacility(loanType);
					double loanLimit = ((Double) dtl.get("ApprovedAmount"))
							.doubleValue();
					dtlSummary.setLimitCoveredUnderCGFSI(String
							.valueOf(loanLimit));
					double clmAppliedAmnt = ((Double) dtl
							.get("ClaimAppliedAmnt")).doubleValue();
					dtlSummary.setAmount(clmAppliedAmnt);
					dtlSummary.setAmntSettledByCGTSI(firstSettlementAmntObj
							.doubleValue());
					dtlSummary
							.setDtOfSettlemntOfFirstInstallmentOfClm(firstSettlementDt);
					if (!clmSummryDtls.contains(dtlSummary))
						clmSummryDtls.add(dtlSummary);
				}
			}

			ArrayList claimsummarydetails = new ArrayList();
			for (int i = 0; i < cgpanDetails.size(); i++) {
				HashMap hashmp = (HashMap) cgpanDetails.elementAt(i);
				if (hashmp != null) {
					String cgpan = (String) hashmp.get("CGPAN");
					double approvedamnt = ((Double) hashmp
							.get("ApprovedAmount")).doubleValue();
					ClaimSummaryDtls clmsummarydtl = new ClaimSummaryDtls();
					clmsummarydtl.setCgpan(cgpan);
					clmsummarydtl.setLimitCoveredUnderCGFSI(String
							.valueOf(approvedamnt));
					if (!claimsummarydetails.contains(clmsummarydtl))
						claimsummarydetails.add(clmsummarydtl);
					hashmp = null;
				}
			}

			HashMap details = processor
					.getPrimarySecurityAndNetworthOfGuarantors(borrowerId,
							memberId);
			SecurityAndPersonalGuaranteeDtls sapg = new SecurityAndPersonalGuaranteeDtls();
			DtlsAsOnDateOfSanction asOnSnctn = new DtlsAsOnDateOfSanction();
			double totalNetworth = 0.0D;
			double totalLandVal = 0.0D;
			double totalMachineVal = 0.0D;
			double totalBldgVal = 0.0D;
			double totalOFMAVal = 0.0D;
			double totalCurrAssetsVal = 0.0D;
			double totalOthersVal = 0.0D;
			Double totalNetWorthDouble = (Double) details.get("networth");
			if (totalNetWorthDouble != null)
				totalNetworth = totalNetWorthDouble.doubleValue();
			Double totalLandValDouble = (Double) details.get("LAND");
			if (totalLandValDouble != null)
				totalLandVal = totalLandValDouble.doubleValue();
			Double totalMCValDouble = (Double) details.get("MACHINE");
			if (totalMCValDouble != null)
				totalMachineVal = totalMCValDouble.doubleValue();
			Double totalBldgValDouble = (Double) details.get("BUILDING");
			if (totalBldgValDouble != null)
				totalBldgVal = totalBldgValDouble.doubleValue();
			Double totalOFMAValDouble = (Double) details
					.get("OTHER FIXED MOVABLE ASSETS");
			if (totalOFMAValDouble != null)
				totalOFMAVal = totalOFMAValDouble.doubleValue();
			Double totalCurrAssetsDouble = (Double) details
					.get("CURRENT ASSETS");
			if (totalCurrAssetsDouble != null)
				totalCurrAssetsVal = totalCurrAssetsDouble.doubleValue();
			Double totalOthersValDouble = (Double) details.get("OTHERS");
			if (totalOthersValDouble != null)
				totalOthersVal = totalOthersValDouble.doubleValue();
			asOnSnctn.setNetworthOfGuarantors(totalNetworth);
			asOnSnctn.setValueOfLand(totalLandVal);
			asOnSnctn.setValueOfBuilding(totalBldgVal);
			asOnSnctn.setValueOfMachine(totalOFMAVal);
			asOnSnctn.setValueOfOtherFixedMovableAssets(totalOFMAVal);
			asOnSnctn.setValueOfCurrentAssets(totalCurrAssetsVal);
			asOnSnctn.setValueOfOthers(totalOthersVal);
			sapg.setDetailsAsOnDateOfSanction(asOnSnctn);
			claimapplication.setSecurityAndPersonalGuaranteeDtls(sapg);
			String clmRefNumber = null;
			Vector clmsFiled = processor.getAllClaimsFiled();
			for (int k = 0; k < clmsFiled.size(); k++) {
				HashMap mp = (HashMap) clmsFiled.elementAt(k);
				if (mp != null) {
					String mpMemberId = (String) mp.get("MEMBERID");
					String mpbid = (String) mp.get("BORROWERID");
					if (mpMemberId != null && mpbid != null
							&& mpMemberId.equals(memberId)
							&& mpbid.equals(borrowerId))
						clmRefNumber = (String) mp.get("ClaimRefNumber");
				}
			}

			HashMap totalRecDtls = processor
					.isRecoveryDetailsAvailable(clmRefNumber);
			Vector recDetails = (Vector) totalRecDtls.get("MAIN");
			Vector recoveryDtls = new Vector();
			RecoveryDetails rd = null;
			for (int i = 0; i < recDetails.size(); i++) {
				HashMap recoveryDetail = (HashMap) recDetails.elementAt(i);
				if (recoveryDetail != null) {
					String pan = (String) recoveryDetail.get("CGPAN");
					double tcPrincipalAmt = 0.0D;
					Double tcPrincipalAmtObj = (Double) recoveryDetail
							.get("TCPRINCIPAL");
					if (tcPrincipalAmtObj != null)
						tcPrincipalAmt = tcPrincipalAmtObj.doubleValue();
					double tcInterestAmt = 0.0D;
					Double tcInterestAmtObj = (Double) recoveryDetail
							.get("TCINTEREST");
					if (tcInterestAmtObj != null)
						tcInterestAmt = tcInterestAmtObj.doubleValue();
					double wcAmount = 0.0D;
					Double wcAmountObj = (Double) recoveryDetail
							.get("WC_AMOUNT");
					if (wcAmountObj != null)
						wcAmount = wcAmountObj.doubleValue();
					double wcOtherAmount = 0.0D;
					Double wcOtherAmountObj = (Double) recoveryDetail
							.get("WC_OTHER");
					if (wcOtherAmountObj != null)
						wcOtherAmount = wcOtherAmountObj.doubleValue();
					String mode = (String) recoveryDetail.get("REC_MODE");
					rd = new RecoveryDetails();
					rd.setCgpan(pan);
					rd.setModeOfRecovery(mode);
					rd.setTcPrincipal(tcPrincipalAmt);
					rd.setTcInterestAndOtherCharges(tcInterestAmt);
					rd.setWcAmount(wcAmount);
					rd.setWcOtherCharges(wcOtherAmount);
					if (!recoveryDtls.contains(rd))
						recoveryDtls.addElement(rd);
				}
			}

			if (cgpans.size() != recoveryDtls.size()) {
				for (int i = 0; i < cgpans.size(); i++) {
					RecoveryDetails recdetail = new RecoveryDetails();
					String cgpan = (String) cgpans.elementAt(i);
					recdetail.setCgpan(cgpan);
					boolean dtlsThere = false;
					for (int j = 0; j < recoveryDtls.size(); j++) {
						RecoveryDetails tempRd = (RecoveryDetails) recoveryDtls
								.elementAt(j);
						if (tempRd == null
								|| !tempRd.getCgpan().equalsIgnoreCase(cgpan))
							continue;
						dtlsThere = true;
						break;
					}

					if (!dtlsThere)
						recoveryDtls.add(recdetail);
				}

			}
			claimapplication.setClaimSummaryDtls(clmSummryDtls);
			claimapplication.setRecoveryDetails(recoveryDtls);
			claimapplication.setSecondInstallment(true);
			User user = getUserInformation(request);
			String userid = user.getUserId();
			String currentTime = (new java.sql.Date(System.currentTimeMillis()))
					.toString();
			Hashtable claimDetails = new Hashtable();
			claimDetails.put(borrowerId, claimapplication);
			boolean exportObjectStatus = false;
			try {
				String contextPath1 = request.getSession(false)
						.getServletContext().getRealPath("");
				String contextPath = PropertyLoader
						.changeToOSpath(contextPath1);
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objectOutputStream = null;
				String filename = (new StringBuilder()).append(contextPath)
						.append(File.separator).append("Download")
						.append(File.separator).append(userid)
						.append(currentTime).append("CLAIM").append("S")
						.append(".EXP").toString();
				String formattedToOSPath = (new StringBuilder())
						.append(request.getContextPath())
						.append(File.separator).append("Download")
						.append(File.separator).append(userid)
						.append(currentTime).append("CLAIM").append("S")
						.append(".EXP").toString();
				session.setAttribute("fileName", formattedToOSPath);
				File exportFlatFile = new File(filename);
				session.setAttribute("fileName", formattedToOSPath);
				if (!exportFlatFile.exists())
					exportFlatFile.createNewFile();
				fileOutputStream = new FileOutputStream(exportFlatFile);
				objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(claimDetails);
				objectOutputStream.close();
				fileOutputStream.close();
				exportObjectStatus = true;
			} catch (IOException ioexception) {
				exportObjectStatus = false;
				Log.log(2, "ClaimAction", "Export Claim File",
						(new StringBuilder()).append("Export failed because ")
								.append(ioexception.getMessage()).toString());
				throw new ExportFailedException("Export Failed.");
			}
			claimsummarydetails = null;
			claimDetails = null;
			npadetails = null;
			hashmap = null;
			tlcldetails = null;
			recoverydetails = null;
			return mapping.findForward("exportClaimFile");
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("GM_PERIODIC_INFO"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			claimForm.setRecoveryFlag(null);
			claimForm.setOtsFlag(null);
			return mapping.findForward("recoveryfilter");
		} else {
			return null;
		}
	}

	public ActionForward proceedFromRecoveryFilterPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String recoveryflag = null;
		HttpSession session = request.getSession(false);
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_CLAIM_FOR"))
				&& ((String) session.getAttribute("subMenuItem"))
						.equals(MenuOptions
								.getMenu("CP_CLAIM_FOR_FIRST_INSTALLMENT"))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			ClaimActionForm claimForm = (ClaimActionForm) form;
			claimForm.setForumthrulegalinitiated("");
			String memberId = claimForm.getMemberId();
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			recoveryflag = claimForm.getRecoveryFlag();
			Date npaDateNew = null;
			if (recoveryflag.equals("Y")) {
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Redirecting to Recovery Details Page of Guarantee Maintenance");
				session.setAttribute("MEMBERID", memberId);
				session.setAttribute("BORROWERID", borrowerId);
				claimForm.setRecoveryFlag("N");
				return mapping.findForward("recoverydetails");
			}
			ClaimsProcessor processor = new ClaimsProcessor();
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving member details for a member id......");
			com.cgtsi.claim.MemberInfo memberDetails = processor
					.getMemberInfoDetails(memberId);
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Successfully retrieved member details for a member id......");
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving borrower details for a borrower id......");
			BorrowerInfo borrowerDetails = processor
					.getBorrowerDetails(borrowerId);
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Successfully retrieved details for a borrower id......");
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving NPA details for a borrower id......");
			Date npaClassifiedDate = null;
			Date npaReportingDate = null;
			String reasonfornpa = null;
			String willfulDefaulter = null;
			HashMap npadetails = processor.isNPADetailsAvailable(borrowerId);
			if (npadetails == null) {
				// throw new NoDataException("NPA Details not available.");
			}
			if (npadetails.size() == 0) {
				// throw new NoDataException("NPA Details not available.");
			}
			String npaId = null;
			// npaId = "12345";

			HashMap npadtlMainTable = (HashMap) npadetails.get("MAIN");
			if (npadtlMainTable != null && npadtlMainTable.size() > 0) {
				npaId = (String) npadtlMainTable.get("npaId");
				willfulDefaulter = (String) npadtlMainTable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaClassifiedDate = (Date) npadtlMainTable
							.get("NPAClassifiedDate");
					if (npaClassifiedDate != null) {
						npadtlMainTable.remove("NPAClassifiedDate");
						String npaClassifiedDateStr = sdf
								.format(npaClassifiedDate);
						npadtlMainTable.put("NPAClassifiedDate",
								npaClassifiedDateStr);
					}
					npaReportingDate = (Date) npadtlMainTable
							.get("NPAReportingDate");
					if (npaReportingDate != null) {
						npadtlMainTable.remove("NPAReportingDate");
						String npaReportingDateStr = sdf
								.format(npaReportingDate);
						npadtlMainTable.put("NPAReportingDate",
								npaReportingDateStr);
					}
					reasonfornpa = (String) npadtlMainTable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtlMainTable
							.get("WillFulDefaulter");
				}
			}
			HashMap npadtltemptable = (HashMap) npadetails.get("TEMP");
			if (npadtltemptable != null && npadtltemptable.size() > 0) {
				npaId = (String) npadtltemptable.get("npaId");
				willfulDefaulter = (String) npadtltemptable
						.get("WillFulDefaulter");
				if (willfulDefaulter != null) {
					npaClassifiedDate = (Date) npadtltemptable
							.get("NPAClassifiedDate");
					if (npaClassifiedDate != null) {
						npadtltemptable.remove("NPAClassifiedDate");
						String npaClassifiedDateStr = sdf
								.format(npaClassifiedDate);
						npadtltemptable.put("NPAClassifiedDate",
								npaClassifiedDateStr);
					}
					npaReportingDate = (Date) npadtltemptable
							.get("NPAReportingDate");
					if (npaReportingDate != null) {
						npadtltemptable.remove("NPAReportingDate");
						String npaReportingDateStr = sdf
								.format(npaReportingDate);
						npadtltemptable.put("NPAReportingDate",
								npaReportingDateStr);
					}
					reasonfornpa = (String) npadtltemptable
							.get("ReasonforturningNPA");
					willfulDefaulter = (String) npadtltemptable
							.get("WillFulDefaulter");
				}
			}
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Successfully retrieved npa details for a borrower id......");
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving legal details for a borrower id......");
			LegalProceedingsDetail legaldetails = processor
					.isLegalProceedingsDetailAvl(borrowerId);
			Log.log(Log.INFO,
					"ClaimAction",
					"proceedFromRecoveryFilterPage",
					(new StringBuilder())
							.append("Printing Legal Details Object :")
							.append(legaldetails).toString());
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Successfully retrieved legal details for a borrower id......");
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving the cgpan details for the borrower id......");

			Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(
					borrowerId, memberId);

			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Successfully retrieved the cgpan details for the borrower id......");
			Vector cgpans = new Vector();
			Vector tccgpans = new Vector();
			Vector wccgpans = new Vector();
			HashMap hashmap = null;
			Administrator admin = new Administrator();
			ParameterMaster pm = admin.getParameter();
			int periodTenureExpiryLodgementClaims = pm
					.getPeriodTenureExpiryLodgementClaims();
			Date currentDate = new Date();
			for (int i = 0; i < cgpnDetails.size(); i++) {
				hashmap = (HashMap) cgpnDetails.elementAt(i);
				if (hashmap == null || !hashmap.containsKey("CGPAN"))
					continue;
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage", (new StringBuilder())
								.append("Printing cgpnDetail :")
								.append(hashmap).toString());
				String cgpan = (String) hashmap.get("CGPAN");
				String cgpanStatus = (String) hashmap.get("APPLICATION_STATUS");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage", (new StringBuilder())
								.append("Printing cgpan :").append(cgpan)
								.toString());
				if (cgpan == null || cgpan.equals(""))
					continue;
				if (hashmap.get("LoanType").equals("TC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						String applicationRefNum = processor
								.getAppRefNumber(cgpan);
						TermLoan termLoanObj = processor.getTermLoan(
								applicationRefNum, "TC");
						int applicationTenure = termLoanObj.getTenure();
						Date guarStartDt = termLoanObj
								.getAmountSanctionedDate();
						Date expiryDate = processor.getDate(guarStartDt,
								applicationTenure);
						Date tenureExpiryDate = processor.getDate(expiryDate,
								periodTenureExpiryLodgementClaims);// ????????
																	// QQQQ
																	// ????????????????
						Calendar calendar2 = Calendar.getInstance();
						calendar2.set(Calendar.MONTH, 2);
						calendar2.set(Calendar.DATE, 31);
						calendar2.set(Calendar.YEAR, 2010);
						Date extendedtenureExpiryDate = calendar2.getTime();
						if (extendedtenureExpiryDate
								.compareTo(tenureExpiryDate) > 0)
							tenureExpiryDate = extendedtenureExpiryDate;
						if (currentDate.compareTo(tenureExpiryDate) > 0)
							continue;
					}
					HashMap mp = new HashMap();
					Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage",
							(new StringBuilder())
									.append("******* Printing dsbrsDt :")
									.append(dsbrsDt).toString());
					Double repaidAmnt = (Double) hashmap.get("AMNT_REPAID");
					Date guarStartDt = (Date) hashmap.get("GUARANTEESTARTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage",
							(new StringBuilder())
									.append("******* Printing guarStartDt :")
									.append(guarStartDt).toString());
					if (guarStartDt == null)
						continue;
					mp.put("CGPAN", cgpan);
					mp.put("LASTDSBRSMNTDT", dsbrsDt);
					mp.put("AMNT_REPAID", repaidAmnt);
					Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryPage",
							(new StringBuilder()).append("Printing HashMap :")
									.append(mp).toString());
					if (!tccgpans.contains(mp))
						tccgpans.addElement(mp);
				}
				if (hashmap.get("LoanType").equals("WC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						HashMap workingCapitalDtl = processor
								.getWorkingCapital(cgpan);
						Integer applicationTenureObj = (Integer) workingCapitalDtl
								.get("WC_TENURE");
						if (applicationTenureObj != null) {
							int applicationTenure = applicationTenureObj
									.intValue();
							Date guarStartDt = (Date) workingCapitalDtl
									.get("GUARANTEESTARTDT");
							Date expiryDate = processor.getDate(guarStartDt,
									applicationTenure);
							Date tenureExpiryDate = processor.getDate(
									expiryDate,
									periodTenureExpiryLodgementClaims);
							Calendar calendar2 = Calendar.getInstance();
							calendar2.set(Calendar.MONTH, 2);
							calendar2.set(Calendar.DATE, 31);
							calendar2.set(Calendar.YEAR, 2010);
							Date extendedtenureExpiryDate = calendar2.getTime();
							if (extendedtenureExpiryDate
									.compareTo(tenureExpiryDate) > 0)
								tenureExpiryDate = extendedtenureExpiryDate;
							if (currentDate.compareTo(tenureExpiryDate) > 0)
								continue;
						}
					}
					Date guarStartDt = (Date) hashmap.get("GUARANTEESTARTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage",
							(new StringBuilder())
									.append("******* Printing guarStartDt :")
									.append(guarStartDt).toString());
					if (guarStartDt == null)
						continue;
					if (!wccgpans.contains(cgpan))
						wccgpans.addElement(cgpan);
				}
				if (hashmap.get("LoanType").equals("CC")) {
					if (cgpanStatus != null
							&& cgpanStatus.equalsIgnoreCase("EX")) {
						String applicationRefNum = processor
								.getAppRefNumber(cgpan);
						TermLoan termLoanObj = processor.getTermLoan(
								applicationRefNum, "CC");
						int applicationTenure = termLoanObj.getTenure();
						Date guarStartDt = termLoanObj
								.getAmountSanctionedDate();
						Date expiryDate = processor.getDate(guarStartDt,
								applicationTenure);
						Date tenureExpiryDate = processor.getDate(expiryDate,
								periodTenureExpiryLodgementClaims);
						Calendar calendar2 = Calendar.getInstance();
						calendar2.set(Calendar.MONTH, 2);
						calendar2.set(Calendar.DATE, 31);
						calendar2.set(Calendar.YEAR, 2010);
						Date extendedtenureExpiryDate = calendar2.getTime();
						if (extendedtenureExpiryDate
								.compareTo(tenureExpiryDate) > 0)
							tenureExpiryDate = extendedtenureExpiryDate;
						if (currentDate.compareTo(tenureExpiryDate) > 0)
							continue;
					}
					HashMap mp = new HashMap();
					Date dsbrsDt = (Date) hashmap.get("LASTDSBRSMNTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage",
							(new StringBuilder())
									.append("******* Printing dsbrsDt :")
									.append(dsbrsDt).toString());
					Double repaidAmnt = (Double) hashmap.get("AMNT_REPAID");
					mp.put("CGPAN", cgpan);
					mp.put("LASTDSBRSMNTDT", dsbrsDt);
					mp.put("AMNT_REPAID", repaidAmnt);
					Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryPage",
							(new StringBuilder()).append("Printing HashMap :")
									.append(mp).toString());
					Date guarStartDt = (Date) hashmap.get("GUARANTEESTARTDT");
					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage",
							(new StringBuilder())
									.append("******* Printing guarStartDt :")
									.append(guarStartDt).toString());
					if (guarStartDt == null)
						continue;
					if (!tccgpans.contains(mp))
						tccgpans.addElement(mp);
				}
				if (!cgpans.contains(cgpan))
					cgpans.addElement(cgpan);
			}

			Vector cgs = new Vector();
			Vector tccgs = new Vector();
			Vector wccgs = new Vector();
			CPDAO dao = new CPDAO();
			Map mapnpa = dao.getCgpanFlagsForBid(borrowerId);
			Vector tccgpannpa = (Vector) mapnpa.get("tccgpans");
			Vector wccgpannpa = (Vector) mapnpa.get("wccgpans");
			// System.out.println("size of tccgpans:"+tccgpans);
			for (int i = 0; i < tccgpans.size(); i++) {
				Map m = (HashMap) tccgpans.get(i);
				String cgNo = (String) m.get("CGPAN");
				for (int j = 0; j < tccgpannpa.size(); j++) {
					Map m2 = (HashMap) tccgpannpa.get(j);
					String cg = (String) m2.get("CGPAN");
					if (cgNo.equals(cg)) {
						m2.put(ClaimConstants.CGPAN_APPRVD_AMNT,
								m.get(ClaimConstants.CGPAN_APPRVD_AMNT));
						m2.put("GUARANTEESTARTDT", m.get("GUARANTEESTARTDT"));

						tccgs.add(m2);
						break;
					}
				}
				if (!cgs.contains(cgNo)) {
					cgs.addElement(cgNo);
				}
			}
			// System.out.println("size of tccgpans:"+tccgpans);
			// System.out.println("size of wccgpans:"+wccgpans);

			for (int i = 0; i < wccgpans.size(); i++) {
				// Map m = (HashMap)wccgpans.get(i);
				// String cgNo = (String)m.get("CGPAN");
				String cgNo = (String) wccgpans.get(i);
				for (int j = 0; j < wccgpannpa.size(); j++) {
					Map m2 = (HashMap) wccgpannpa.get(j);
					String cg = (String) m2.get("CGPAN");
					if (cgNo.equals(cg)) {

						wccgs.add(m2);
						break;
					}
				}
				if (!cgs.contains(cgNo)) {
					cgs.addElement(cgNo);
				}
			}
			// System.out.println("size of wccgpans:"+wccgpans);
			/* getCgpanDetailsAsOnNpa(npaId) */

			Double totalLandValDouble = 0.0;
			Double totalNetworthDouble = 0.0;
			Double totalMachineValDouble = 0.0;
			Double totalBldgValueDouble = 0.0;
			Double totalOFMAValDouble = 0.0;
			Double totalCurrAssetsValDouble = 0.0;
			Double totalOthersValDouble = 0.0;
			String totalLandValueStr = null;
			String totalNetworthStr = null;
			String totalMachineValueStr = null;
			String totalBldgValueStr = null;
			String totalOFMAValueStr = null;
			String totalCurrAssetsValueStr = null;
			String totalOthersValueStr = null;
			String reasonReduction = "";
			Map securityMap = new HashMap();
			Map npaSecurityMap = new HashMap();

			// HashMap details =
			// processor.getPrimarySecurityAndNetworthOfGuarantors(borrowerId,
			// memberId);

			HashMap details = dao
					.getPrimarySecurityAndNetworthOfGuarantors(npaId);

			HashMap sancmap = (HashMap) details.get("SAN");
			HashMap npamap = (HashMap) details.get("NPA");

			if (sancmap != null) {
				totalLandValDouble = (Double) sancmap.get("LAND");
				if (totalLandValDouble != null)
					if (totalLandValDouble.doubleValue() > 0.0D)
						totalLandValueStr = totalLandValDouble.toString();
					else
						totalLandValueStr = "";
				totalNetworthDouble = (Double) sancmap.get("networth");
				if (totalNetworthDouble != null)
					if (totalNetworthDouble.doubleValue() > 0.0D)
						totalNetworthStr = totalNetworthDouble.toString();
					else
						totalNetworthStr = "";
				totalMachineValDouble = (Double) sancmap.get("MACHINE");
				if (totalMachineValDouble != null)
					if (totalMachineValDouble.doubleValue() > 0.0D)
						totalMachineValueStr = totalMachineValDouble.toString();
					else
						totalMachineValueStr = "";
				totalBldgValueDouble = (Double) sancmap.get("BUILDING");
				if (totalBldgValueDouble != null)
					if (totalBldgValueDouble.doubleValue() > 0.0D)
						totalBldgValueStr = totalBldgValueDouble.toString();
					else
						totalBldgValueStr = "";
				totalOFMAValDouble = (Double) sancmap
						.get("OTHER FIXED MOVABLE ASSETS");
				if (totalOFMAValDouble != null)
					if (totalOFMAValDouble.doubleValue() > 0.0D)
						totalOFMAValueStr = totalOFMAValDouble.toString();
					else
						totalOFMAValueStr = "";
				totalCurrAssetsValDouble = (Double) sancmap
						.get("CURRENT ASSETS");
				if (totalCurrAssetsValDouble != null)
					if (totalCurrAssetsValDouble.doubleValue() > 0.0D)
						totalCurrAssetsValueStr = totalCurrAssetsValDouble
								.toString();
					else
						totalCurrAssetsValueStr = "";
				totalOthersValDouble = (Double) sancmap.get("OTHERS");
				if (totalOthersValDouble != null)
					if (totalOthersValDouble.doubleValue() > 0.0D)
						totalOthersValueStr = totalOthersValDouble.toString();
					else
						totalOthersValueStr = "";

			}

			securityMap.put("LAND", totalLandValueStr);
			securityMap.put("networth", totalNetworthStr);
			securityMap.put("MACHINE", totalMachineValueStr);
			securityMap.put("BUILDING", totalBldgValueStr);
			securityMap.put("OTHER FIXED MOVABLE ASSETS", totalOFMAValueStr);
			securityMap.put("CURRENT ASSETS", totalCurrAssetsValueStr);
			securityMap.put("OTHERS", totalOthersValueStr);
			securityMap.put("reasonReduction", "NA");
			claimForm.setAsOnDtOfSanctionDtl(securityMap);

			if (npamap != null) {
				totalLandValDouble = (Double) npamap.get("LAND");
				if (totalLandValDouble != null)
					if (totalLandValDouble.doubleValue() > 0.0D)
						totalLandValueStr = totalLandValDouble.toString();
					else
						totalLandValueStr = "";
				totalNetworthDouble = (Double) npamap.get("networth");
				if (totalNetworthDouble != null)
					if (totalNetworthDouble.doubleValue() > 0.0D)
						totalNetworthStr = totalNetworthDouble.toString();
					else
						totalNetworthStr = "";
				totalMachineValDouble = (Double) npamap.get("MACHINE");
				if (totalMachineValDouble != null)
					if (totalMachineValDouble.doubleValue() > 0.0D)
						totalMachineValueStr = totalMachineValDouble.toString();
					else
						totalMachineValueStr = "";
				totalBldgValueDouble = (Double) npamap.get("BUILDING");
				if (totalBldgValueDouble != null)
					if (totalBldgValueDouble.doubleValue() > 0.0D)
						totalBldgValueStr = totalBldgValueDouble.toString();
					else
						totalBldgValueStr = "";
				totalOFMAValDouble = (Double) npamap
						.get("OTHER FIXED MOVABLE ASSETS");
				if (totalOFMAValDouble != null)
					if (totalOFMAValDouble.doubleValue() > 0.0D)
						totalOFMAValueStr = totalOFMAValDouble.toString();
					else
						totalOFMAValueStr = "";
				totalCurrAssetsValDouble = (Double) npamap
						.get("CURRENT ASSETS");
				if (totalCurrAssetsValDouble != null)
					if (totalCurrAssetsValDouble.doubleValue() > 0.0D)
						totalCurrAssetsValueStr = totalCurrAssetsValDouble
								.toString();
					else
						totalCurrAssetsValueStr = "";
				totalOthersValDouble = (Double) npamap.get("OTHERS");
				if (totalOthersValDouble != null)
					if (totalOthersValDouble.doubleValue() > 0.0D)
						totalOthersValueStr = totalOthersValDouble.toString();
					else
						totalOthersValueStr = "";
				reasonReduction = (String) npamap.get("reasonReduction");
			}
			npaSecurityMap.put("LAND", totalLandValueStr);
			npaSecurityMap.put("networth", totalNetworthStr);
			npaSecurityMap.put("MACHINE", totalMachineValueStr);
			npaSecurityMap.put("BUILDING", totalBldgValueStr);
			npaSecurityMap.put("OTHER FIXED MOVABLE ASSETS", totalOFMAValueStr);
			npaSecurityMap.put("CURRENT ASSETS", totalCurrAssetsValueStr);
			npaSecurityMap.put("OTHERS", totalOthersValueStr);
			npaSecurityMap.put("reasonReduction", reasonReduction);
			claimForm.setAsOnDtOfNPA(npaSecurityMap);

			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
					"Retrieving all the recovery modes from the database......");
			Vector recoveryModes = processor.getAllRecoveryModes();
			Vector otsDtls = processor.getOTSDetails(borrowerId);
			Date otsReqDate = null;
			String otsReqDateStr = null;
			for (int k = 0; k < otsDtls.size(); k++) {
				HashMap dtl = (HashMap) otsDtls.elementAt(k);
				if (dtl != null) {
					otsReqDate = (Date) dtl.get("OTSRequestDate");
					sdf = new SimpleDateFormat("dd/MM/yyyy");
					otsReqDateStr = sdf.format(otsReqDate);
				}
			}

			claimForm.setMemberDetails(memberDetails);
			claimForm.setBorrowerDetails(borrowerDetails);
			if (npadtltemptable != null) {
				if (npadtltemptable.size() > 0)
					claimForm.setNpaDetails(npadtltemptable);
			} else if (npadtlMainTable != null && npadtlMainTable.size() > 0)
				claimForm.setNpaDetails(npadtlMainTable);
			Log.log(Log.INFO,
					"ClaimAction",
					"proceedFromRecoveryFilterPage()",
					(new StringBuilder())
							.append("legaldetails.getForumRecoveryProceedingsInitiated() :")
							.append(legaldetails
									.getForumRecoveryProceedingsInitiated())
							.toString());
			String forumNameStr = legaldetails
					.getForumRecoveryProceedingsInitiated();
			if (forumNameStr != null)
				if (forumNameStr.equals("Civil Court")
						|| forumNameStr.equals("DRT")
						|| forumNameStr.equals("LokAdalat")
						|| forumNameStr.equals("Revenue Recovery Autority")
						|| forumNameStr.equals("Securitisation Act ")) {
					claimForm.setForumthrulegalinitiated(legaldetails
							.getForumRecoveryProceedingsInitiated());
				} else {
					claimForm.setForumthrulegalinitiated("Others");
					claimForm.setOtherforums(legaldetails
							.getForumRecoveryProceedingsInitiated());
				}
			claimForm.setCaseregnumber(legaldetails.getSuitCaseRegNumber());
			Date legaldt_utilformat = legaldetails.getFilingDate();
			if (legaldt_utilformat != null)
				claimForm.setLegaldate(sdf.format(legaldt_utilformat));
			else
				claimForm.setLegaldate("");
			claimForm.setNameofforum(legaldetails.getNameOfForum());
			claimForm.setLocation(legaldetails.getLocation());
			claimForm.setAmountclaimed(String.valueOf(legaldetails
					.getAmountClaimed()));
			claimForm.setCurrentstatusremarks(legaldetails
					.getCurrentStatusRemarks());
			claimForm.setProceedingsConcluded(legaldetails
					.getIsRecoveryProceedingsConcluded());

			// claimForm.setCgpansVector(cgpans);
			claimForm.setCgpansVector(cgs);
			int tcCounter = 0;
			// if(tccgpans != null)
			// tcCounter = tccgpans.size();
			if (tccgs != null) {
				tcCounter = tccgs.size();
			}

			claimForm.setTcCounter(tcCounter);
			// claimForm.setTcCgpansVector(tccgpans);
			claimForm.setTcCgpansVector(tccgs);
			int wcCounter = 0;
			// if(wccgpans != null)
			// wcCounter = wccgpans.size();
			if (wccgs != null) {
				wcCounter = wccgs.size();
			}

			claimForm.setWcCounter(wcCounter);
			// claimForm.setWcCgpansVector(wccgpans);
			claimForm.setWcCgpansVector(wccgs);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy");
			// for(int i = 0; i < tccgpans.size(); i++)
			for (int i = 0; i < tccgs.size(); i++) {
				int j = i + 1;
				// HashMap map = (HashMap)tccgpans.elementAt(i);
				HashMap map = (HashMap) tccgs.elementAt(i);
				if (map != null) {
					Date dsbrsDt = (Date) map.get("LASTDSBRSMNTDT");

					Log.log(Log.INFO,
							"ClaimAction",
							"proceedFromRecoveryFilterPage()",
							(new StringBuilder()).append("dsbrsDt :")
									.append(dsbrsDt).toString());

					String dateStr = "";
					if (dsbrsDt != null) {
						dateStr = simpleDateFormat.format(dsbrsDt);
					}
					// Double repaidAmnt = (Double)map.get("AMNT_REPAID");
					Double repaidAmnt = (Double) map.get("TCPRINREPAMT");
					Double repaidIntAmnt = (Double) map.get("TCINTREPAMT");
					Double tcosnpaAmnt = (Double) map.get("TCPRINNPAOSAMT");
					String repaidStr = "0.0";
					String repaidIntStr = "0.0";
					String tcosStr = "0.0";
					if (repaidAmnt != null) {
						repaidStr = repaidAmnt.toString();
					} else {
						repaidStr = "0.0";
					}
					if (repaidIntAmnt != null) {
						repaidIntStr = repaidIntAmnt.toString();
					} else {
						repaidIntStr = "0.0";
					}
					if (tcosnpaAmnt != null) {
						tcosStr = tcosnpaAmnt.toString();
					} else {
						tcosStr = "0.0";
					}

					Double approvedAmt = (Double) map.get("ApprovedAmount");
					String approvedAmtStr = "0.0";
					if (approvedAmt != null)
						approvedAmtStr = approvedAmt.toString();

					// Log.log(Log.INFO, "ClaimAction",
					// "proceedFromRecoveryFilterPage()", (new
					// StringBuilder()).append("dateStr :").append(dateStr).toString());
					// Log.log(Log.INFO, "ClaimAction",
					// "proceedFromRecoveryFilterPage()", "******************");
					claimForm.setLastDisbursementDate((new StringBuilder())
							.append("key-").append(j).toString(), dateStr);
					claimForm.setTcprincipal(
							(new StringBuilder()).append("key-").append(j)
									.toString(), repaidStr);
					claimForm.setTcInterestCharge(
							(new StringBuilder()).append("key-").append(j)
									.toString(), repaidIntStr);
					claimForm.setTcOsAsOnDateOfNPA((new StringBuilder())
							.append("key-").append(j).toString(), tcosStr);
				}
			}

			for (int i = 0; i < wccgs.size(); i++) {
				int j = i + 1;
				Map map = (HashMap) wccgs.elementAt(i);
				if (map != null) {
					Double wcosnpaAmnt = (Double) map.get("WCPRINNPAOSAMT");
					String wcosStr = "";
					if (wcosnpaAmnt != null) {
						wcosStr = wcosnpaAmnt.toString();
					} else {
						wcosStr = "0.0";
					}
					claimForm.setWcOsAsOnDateOfNPA((new StringBuilder())
							.append("key-").append(j).toString(), wcosStr);
				}
			}
			Map newCgMap = null;
			Vector newCgVector = new Vector();
			for (int i = 0; i < cgpnDetails.size(); i++) {
				Map m1 = (HashMap) cgpnDetails.elementAt(i);
				for (int j = 0; j < cgs.size(); j++) {
					if (!m1.get("CGPAN").equals(cgs.elementAt(j)))
						continue;
					newCgMap = new HashMap();
					newCgMap.put("CGPAN", m1.get("CGPAN"));
					newCgMap.put("ApprovedAmount", m1.get("ApprovedAmount"));
					newCgMap.put("EnhancedApprovedAmount",
							m1.get("EnhancedApprovedAmount"));
					newCgMap.put("LoanType", m1.get("LoanType"));
					newCgMap.put("GUARANTEESTARTDT", m1.get("GUARANTEESTARTDT"));
					newCgMap.put("APPLICATION_STATUS",
							m1.get("APPLICATION_STATUS"));
					newCgVector.add(newCgMap);
					break;
				}

			}

			// if(tccgpans != null && wccgpans != null && tccgpans.size() == 0
			// && wccgpans.size() == 0)
			if (tccgs != null && wccgs != null && tccgs.size() == 0
					&& wccgs.size() == 0) {
				session.setAttribute("CurrentPage",
						"getBorrowerId.do?method=setBankId");
				throw new NoDataException(
						"There are no Loan Account(s) for this Borrower or the existing Loan Account(s) may have been closed.");
			}
			claimForm.setRecoveryModes(recoveryModes);
			claimForm.setDateOfSeekingOTS(otsReqDateStr);

			// for(int i = 0; i < cgpnDetails.size(); i++)
			// {
			// hashmap = (HashMap)cgpnDetails.elementAt(i);
			// if(hashmap != null)
			// {
			// Date guarStartDt = (Date)hashmap.get("GUARANTEESTARTDT");
			// Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage",
			// (new
			// StringBuilder()).append("******* Printing guarStartDt :").append(guarStartDt).toString());
			// if(guarStartDt == null)
			// {
			// cgpnDetails.remove(i);
			// i--;
			// }
			// }
			// }

			// claimForm.setCgpnDetails(cgpnDetails);
			claimForm.setCgpnDetails(newCgVector);
			claimForm.setSecurityDetails(details);
			npadetails = null;
			hashmap = null;
			cgpnDetails = null;
			cgpans = null;
			tccgpans = null;
			wccgpans = null;
			recoveryModes = null;
			return mapping.findForward("claimdetails");
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("CP_CLAIM_FOR"))
				&& session.getAttribute("subMenuItem").equals(
						MenuOptions.getMenu("CP_CLAIM_FOR_SECOND_INSTALLMENT"))) {
			ClaimActionForm claimForm = (ClaimActionForm) form;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String memberId = claimForm.getMemberId();
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
			Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryFilterPage()",
					(new StringBuilder()).append("Printing the Borrower Id :")
							.append(borrowerId).toString());
			HashMap firstClmDtl = null;
			recoveryflag = claimForm.getRecoveryFlag();
			if (recoveryflag.equals("Y")) {
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Redirecting to Recovery Details Page of Guarantee Maintenance");
				session.setAttribute("MEMBERID", memberId);
				session.setAttribute("BORROWERID", borrowerId);
				claimForm.setRecoveryFlag("N");
				return mapping.findForward("recoverydetails");
			}
			if (recoveryflag.equals("N")) {
				ClaimsProcessor processor = new ClaimsProcessor();
				Date settDate = null;
				firstClmDtl = processor.getFirstClmDtlForBid(bankId, zoneId,
						branchId, borrowerId);
				if (firstClmDtl.containsKey("FirstSettlmntDt")) {
					settDate = (Date) firstClmDtl.get("FirstSettlmntDt");
					firstClmDtl.remove("FirstSettlmntDt");
					if (settDate != null)
						firstClmDtl
								.put("FirstSettlmntDt", sdf.format(settDate));
				}
				String clmRefNum = (String) firstClmDtl.get("ClaimRefNumber");
				Date recallNoticeDt = (Date) firstClmDtl.get("RECALL-");
				String recallNoticeDtStr = sdf.format(recallNoticeDt);
				claimForm.setDateOfRecallNotice(recallNoticeDtStr);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Retrieving member details for a member id......");
				com.cgtsi.claim.MemberInfo memberDetails = processor
						.getMemberInfoDetails(memberId);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Successfully retrieved member details for a member id......");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Retrieving borrower details for a borrower id......");
				BorrowerInfo borrowerDetails = processor
						.getBorrowerDetails(borrowerId);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Successfully retrieved details for a borrower id......");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Retrieving NPA details for a borrower id......");
				Date npaClassifiedDate = null;
				Date npaReportingDate = null;
				String reasonfornpa = null;
				String willfulDefaulter = null;
				ClaimDetail clmdtl = processor
						.getDetailsForClaimRefNumber(clmRefNum);
				Vector tcVec = clmdtl.getTcDetails();
				Vector wcVec = clmdtl.getWcDetails();
				HashMap npadetails = processor
						.isNPADetailsAvailable(borrowerId);
				if (npadetails == null)
					throw new NoDataException("NPA Details not available.");
				if (npadetails.size() == 0)
					throw new NoDataException("NPA Details not available.");
				HashMap npadtlMainTable = (HashMap) npadetails.get("MAIN");
				if (npadtlMainTable != null && npadtlMainTable.size() > 0) {
					willfulDefaulter = (String) npadtlMainTable
							.get("WillFulDefaulter");
					if (willfulDefaulter != null) {
						npaClassifiedDate = (Date) npadtlMainTable
								.get("NPAClassifiedDate");
						if (npaClassifiedDate != null) {
							npadtlMainTable.remove("NPAClassifiedDate");
							String npaClassifiedDateStr = sdf
									.format(npaClassifiedDate);
							npadtlMainTable.put("NPAClassifiedDate",
									npaClassifiedDateStr);
						}
						npaReportingDate = (Date) npadtlMainTable
								.get("NPAReportingDate");
						if (npaReportingDate != null) {
							npadtlMainTable.remove("NPAReportingDate");
							String npaReportingDateStr = sdf
									.format(npaReportingDate);
							npadtlMainTable.put("NPAReportingDate",
									npaReportingDateStr);
						}
						reasonfornpa = (String) npadtlMainTable
								.get("ReasonforturningNPA");
						willfulDefaulter = (String) npadtlMainTable
								.get("WillFulDefaulter");
					}
				}
				HashMap npadtltemptable = (HashMap) npadetails.get("TEMP");
				if (npadtltemptable != null && npadtltemptable.size() > 0) {
					willfulDefaulter = (String) npadtltemptable
							.get("WillFulDefaulter");
					if (willfulDefaulter != null) {
						npaClassifiedDate = (Date) npadtltemptable
								.get("NPAClassifiedDate");
						if (npaClassifiedDate != null) {
							npadtltemptable.remove("NPAClassifiedDate");
							String npaClassifiedDateStr = sdf
									.format(npaClassifiedDate);
							npadtltemptable.put("NPAClassifiedDate",
									npaClassifiedDateStr);
						}
						npaReportingDate = (Date) npadtltemptable
								.get("NPAReportingDate");
						if (npaReportingDate != null) {
							npadtltemptable.remove("NPAReportingDate");
							String npaReportingDateStr = sdf
									.format(npaReportingDate);
							npadtltemptable.put("NPAReportingDate",
									npaReportingDateStr);
						}
						reasonfornpa = (String) npadtltemptable
								.get("ReasonforturningNPA");
						willfulDefaulter = (String) npadtltemptable
								.get("WillFulDefaulter");
					}
				}
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Successfully retrieved npa details for a borrower id......");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Retrieving legal details for a borrower id......");
				LegalProceedingsDetail legaldetails = processor
						.isLegalProceedingsDetailAvl(borrowerId);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Successfully retrieved legal details for a borrower id......");
				Vector cgpnDetails = processor.getCGPANDetailsForBorrowerId(
						borrowerId, memberId);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Successfully retrieved the cgpan details for the borrower id......");
				Vector cgpans = new Vector();
				Vector tccgpans = new Vector();
				Vector wccgpans = new Vector();
				Vector clmsFiled = processor.getAllClaimsFiled();
				String clmRefNumber = null;
				for (int k = 0; k < clmsFiled.size(); k++) {
					HashMap mp = (HashMap) clmsFiled.elementAt(k);
					if (mp != null) {
						String mpMemberId = (String) mp.get("MEMBERID");
						String mpbid = (String) mp.get("BORROWERID");
						if (mpMemberId != null && mpbid != null
								&& mpMemberId.equals(memberId)
								&& mpbid.equals(borrowerId))
							clmRefNumber = (String) mp.get("ClaimRefNumber");
					}
				}

				HashMap totalRecDtls = processor
						.isRecoveryDetailsAvailable(clmRefNumber);
				Vector recDetails = (Vector) totalRecDtls.get("MAIN");
				ArrayList cgpansForClmRefNum = processor
						.getAllCgpansForClmRefNum(clmRefNum);
				HashMap hashmap = null;
				for (int i = 0; i < cgpnDetails.size(); i++) {
					hashmap = (HashMap) cgpnDetails.elementAt(i);
					Log.log(Log.INFO, "ClaimAction", "proceedFromRecoveryPage",
							(new StringBuilder()).append("Printing HashMap :")
									.append(hashmap).toString());
					if (hashmap == null)
						continue;
					if (hashmap.containsKey("CGPAN")) {
						String cgpan = (String) hashmap.get("CGPAN");
						if (cgpan != null && !cgpan.equals("")) {
							if (!cgpansForClmRefNum.contains(cgpan))
								continue;
							if (hashmap.get("LoanType").equals("TC")) {
								HashMap mp = new HashMap();
								Date dsbrsDt = (Date) hashmap
										.get("LASTDSBRSMNTDT");
								Log.log(Log.INFO,
										"ClaimAction",
										"proceedFromRecoveryFilterPage",
										(new StringBuilder())
												.append("******* Printing dsbrsDt :")
												.append(dsbrsDt).toString());
								Double repaidAmnt = (Double) hashmap
										.get("AMNT_REPAID");
								mp.put("CGPAN", cgpan);
								mp.put("LASTDSBRSMNTDT", dsbrsDt);
								mp.put("AMNT_REPAID", repaidAmnt);
								Date guarStartDt = (Date) hashmap
										.get("GUARANTEESTARTDT");
								if (guarStartDt == null)
									continue;
								if (!tccgpans.contains(mp))
									tccgpans.addElement(mp);
							}
							if (hashmap.get("LoanType").equals("WC")) {
								Date guarStartDt = (Date) hashmap
										.get("GUARANTEESTARTDT");
								if (guarStartDt == null)
									continue;
								if (!wccgpans.contains(cgpan))
									wccgpans.addElement(cgpan);
							}
							if (hashmap.get("LoanType").equals("CC")) {
								HashMap mp = new HashMap();
								Date dsbrsDt = (Date) hashmap
										.get("LASTDSBRSMNTDT");
								Log.log(Log.INFO,
										"ClaimAction",
										"proceedFromRecoveryFilterPage",
										(new StringBuilder())
												.append("******* Printing dsbrsDt :")
												.append(dsbrsDt).toString());
								Double repaidAmnt = (Double) hashmap
										.get("AMNT_REPAID");
								mp.put("CGPAN", cgpan);
								mp.put("LASTDSBRSMNTDT", dsbrsDt);
								mp.put("AMNT_REPAID", repaidAmnt);
								Date guarStartDt = (Date) hashmap
										.get("GUARANTEESTARTDT");
								if (guarStartDt == null)
									continue;
								if (!tccgpans.contains(mp))
									tccgpans.addElement(mp);
							}
							if (!cgpans.contains(cgpan))
								cgpans.addElement(cgpan);
							HashMap recMap = null;
							for (int m = 0; m < recDetails.size(); m++) {
								int count = m;
								recMap = (HashMap) recDetails.elementAt(m);
								if (recMap != null) {
									String pan = (String) recMap.get("CGPAN");
									if (pan != null && pan.equals(cgpan)) {
										count++;
										if (pan.indexOf("TC") > 0) {
											if (recMap
													.containsKey("TCPRINCIPAL")) {
												String principalAmntStr = ((Double) recMap
														.get("TCPRINCIPAL"))
														.toString();
												claimForm
														.setCgpandetails(
																(new StringBuilder())
																		.append("tcprincipal$recovery#key-")
																		.append(count)
																		.toString(),
																principalAmntStr);
											}
											if (recMap
													.containsKey("TCINTEREST")) {
												String interestChargesStr = ((Double) recMap
														.get("TCINTEREST"))
														.toString();
												claimForm
														.setCgpandetails(
																(new StringBuilder())
																		.append("tcInterestCharges$recovery#key-")
																		.append(count)
																		.toString(),
																interestChargesStr);
											}
										}
										if (pan.indexOf("WC") > 0) {
											if (recMap.containsKey("WC_AMOUNT")) {
												String wcAmountStr = ((Double) recMap
														.get("WC_AMOUNT"))
														.toString();
												claimForm
														.setCgpandetails(
																(new StringBuilder())
																		.append("wcAmount$recovery#key-")
																		.append(count)
																		.toString(),
																wcAmountStr);
											}
											if (recMap.containsKey("WC_OTHER")) {
												String wcOthersStr = ((Double) recMap
														.get("WC_OTHER"))
														.toString();
												claimForm
														.setCgpandetails(
																(new StringBuilder())
																		.append("wcOtherCharges$recovery#key-")
																		.append(count)
																		.toString(),
																wcOthersStr);
											}
										}
										if (recMap.containsKey("REC_MODE")) {
											String modeOfRec = (String) recMap
													.get("REC_MODE");
											claimForm
													.setCgpandetails(
															(new StringBuilder())
																	.append("recoveryMode$recovery#key-")
																	.append(count)
																	.toString(),
															modeOfRec);
										}
									}
								}
							}

						}
					}
					hashmap = null;
				}

				Vector perGaurDtls = processor
						.getFirstClmPerGaurSecDtls(clmRefNumber);
				String particularFlag = null;
				String landValue = "";
				String networthValue = "";
				String machineValue = "";
				String bldgValue = "";
				String ofmaValue = "";
				String currAssetsValue = "";
				String othersValue = "";
				String reasonForReduction = "";
				Double doubleObj = null;
				for (int i = 0; i < perGaurDtls.size(); i++) {
					HashMap mp = (HashMap) perGaurDtls.elementAt(i);
					if (mp != null) {
						particularFlag = (String) mp
								.get("SecurityParticularFlag");
						if (particularFlag != null
								&& particularFlag.equals("SAN")) {
							doubleObj = (Double) mp.get("LAND");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								landValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("LAND", landValue);
							doubleObj = (Double) mp.get("networth");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								networthValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("networth",
									networthValue);
							reasonForReduction = (String) mp
									.get("reasonReduction");
							claimForm.setAsOnDtOfSanctionDtl("reasonReduction",
									reasonForReduction);
							doubleObj = (Double) mp.get("BUILDING");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								bldgValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("BUILDING",
									bldgValue);
							doubleObj = (Double) mp.get("MACHINE");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								machineValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("MACHINE",
									machineValue);
							doubleObj = (Double) mp
									.get("OTHER FIXED MOVABLE ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								ofmaValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl(
									"OTHER FIXED MOVABLE ASSETS", ofmaValue);
							doubleObj = (Double) mp.get("CURRENT ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								currAssetsValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("CURRENT ASSETS",
									currAssetsValue);
							doubleObj = (Double) mp.get("OTHERS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								othersValue = doubleObj.toString();
							claimForm.setAsOnDtOfSanctionDtl("OTHERS",
									othersValue);
						}
						if (particularFlag != null
								&& particularFlag.equals("NPA")) {
							doubleObj = (Double) mp.get("LAND");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								landValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("LAND", landValue);
							doubleObj = (Double) mp.get("networth");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								networthValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("networth", networthValue);
							reasonForReduction = (String) mp
									.get("reasonReduction");
							claimForm.setAsOnDtOfNPA("reasonReduction",
									reasonForReduction);
							doubleObj = (Double) mp.get("BUILDING");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								bldgValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("BUILDING", bldgValue);
							doubleObj = (Double) mp.get("MACHINE");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								machineValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("MACHINE", machineValue);
							doubleObj = (Double) mp
									.get("OTHER FIXED MOVABLE ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								ofmaValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA(
									"OTHER FIXED MOVABLE ASSETS", ofmaValue);
							doubleObj = (Double) mp.get("CURRENT ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								currAssetsValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("CURRENT ASSETS",
									currAssetsValue);
							doubleObj = (Double) mp.get("OTHERS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								othersValue = doubleObj.toString();
							claimForm.setAsOnDtOfNPA("OTHERS", othersValue);
						}
						if (particularFlag != null
								&& particularFlag.equals("CLM")) {
							doubleObj = (Double) mp.get("LAND");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								landValue = doubleObj.toString();
							claimForm
									.setAsOnLodgemntOfCredit("LAND", landValue);
							doubleObj = (Double) mp.get("networth");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								networthValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit("networth",
									networthValue);
							reasonForReduction = (String) mp
									.get("reasonReduction");
							claimForm.setAsOnLodgemntOfCredit(
									"reasonReduction", reasonForReduction);
							doubleObj = (Double) mp.get("BUILDING");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								bldgValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit("BUILDING",
									bldgValue);
							doubleObj = (Double) mp.get("MACHINE");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								machineValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit("MACHINE",
									machineValue);
							doubleObj = (Double) mp
									.get("OTHER FIXED MOVABLE ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								ofmaValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit(
									"OTHER FIXED MOVABLE ASSETS", ofmaValue);
							doubleObj = (Double) mp.get("CURRENT ASSETS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								currAssetsValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit("CURRENT ASSETS",
									currAssetsValue);
							doubleObj = (Double) mp.get("OTHERS");
							if (doubleObj != null
									&& doubleObj.doubleValue() != 0.0D)
								othersValue = doubleObj.toString();
							claimForm.setAsOnLodgemntOfCredit("OTHERS",
									othersValue);
						}
					}
				}

				Vector clmAppliedAmnts = processor.getClaimAppliedAmounts(
						borrowerId, "F");
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
										|| !cgpnInAppliedAmntsVec
												.equals(thiscgpn))
									continue;
								double clmAppliedAmnt = 0.0D;
								Double clmAppAmntObj = (Double) clmAppliedDtl
										.get("ClaimAppliedAmnt");
								if (clmAppAmntObj != null)
									clmAppliedAmnt = clmAppAmntObj
											.doubleValue();
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
						.getClaimSettlementDetailForBorrower(borrowerId);
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
						dtl.put("FirstSettlmntAmnt", new Double(
								firstSettlementAmnt));
						dtl.put("FirstSettlmntDt", firstSettlementDt);
						if (!finalUpdatedDtls.contains(dtl))
							finalUpdatedDtls.addElement(dtl);
						dtl = null;
					}
				}

				Vector recoveryModes = processor.getAllRecoveryModes();
				Vector otsDtls = processor.getOTSDetails(borrowerId);
				Date otsReqDate = null;
				String otsReqDateStr = null;
				for (int k = 0; k < otsDtls.size(); k++) {
					HashMap dtlMap = (HashMap) otsDtls.elementAt(k);
					if (dtlMap != null) {
						otsReqDate = (Date) dtlMap.get("OTSRequestDate");
						sdf = new SimpleDateFormat("dd/MM/yyyy");
						otsReqDateStr = sdf.format(otsReqDate);
					}
				}

				claimForm.setMemberDetails(memberDetails);
				claimForm.setBorrowerDetails(borrowerDetails);
				if (npadtltemptable != null) {
					if (npadtltemptable.size() > 0)
						claimForm.setNpaDetails(npadtltemptable);
				} else if (npadtlMainTable != null
						&& npadtlMainTable.size() > 0)
					claimForm.setNpaDetails(npadtlMainTable);
				claimForm.setWilfullDefaulter(willfulDefaulter);
				int tcCounter = 0;
				if (tccgpans != null)
					tcCounter = tccgpans.size();
				claimForm.setTcCounter(tcCounter);
				for (int i = 0; i < tccgpans.size(); i++) {
					HashMap outMap = (HashMap) tccgpans.elementAt(i);
					if (outMap != null) {
						String outCgpan = (String) outMap.get("CGPAN");
						for (int j = 0; j < tcVec.size(); j++) {
							HashMap innerMap = (HashMap) tcVec.elementAt(j);
							if (innerMap != null) {
								String innerCgpan = (String) innerMap
										.get("CGPAN");
								if (innerCgpan != null
										&& innerCgpan.equals(outCgpan)) {
									double principalAmnt = ((Double) innerMap
											.get("TCPRINCIPAL")).doubleValue();
									double interestOtherCharges = ((Double) innerMap
											.get("TCINTEREST")).doubleValue();
									double osAsOnNPADate = ((Double) innerMap
											.get("OS as on NPA")).doubleValue();
									double osAsInCivilSuit = ((Double) innerMap
											.get("OS as in Civil Suit"))
											.doubleValue();
									double osAsInFirstClmLodgement = ((Double) innerMap
											.get("OS as in Clm Logdmnt"))
											.doubleValue();
									outMap.put("TCPRINCIPAL", new Double(
											principalAmnt));
									outMap.put("TCINTEREST", new Double(
											interestOtherCharges));
									outMap.put("OS as on NPA", new Double(
											osAsOnNPADate));
									outMap.put("OS as in Civil Suit",
											new Double(osAsInCivilSuit));
									outMap.put("OS as in Clm Logdmnt",
											new Double(osAsInFirstClmLodgement));
								}
							}
						}

						Log.log(Log.INFO,
								"ClaimAction",
								"proceedFromRecoveryPage",
								(new StringBuilder())
										.append("Printing OutMap :")
										.append(outMap).toString());
					}
				}

				claimForm.setTcCgpansVector(tccgpans);
				for (int i = 0; i < tccgpans.size(); i++) {
					int j = i + 1;
					HashMap map = (HashMap) tccgpans.elementAt(i);
					if (map != null) {
						Date dsbrsDt = (Date) map.get("LASTDSBRSMNTDT");
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");
						String dateStr = "";
						if (dsbrsDt != null)
							dateStr = simpleDateFormat.format(dsbrsDt);
						Double repaidAmnt = (Double) map.get("AMNT_REPAID");
						String repaidStr = "";
						if (repaidAmnt != null)
							repaidStr = repaidAmnt.toString();
						else
							repaidStr = "0.0";
						String principalAmntStr = ((Double) map
								.get("TCPRINCIPAL")).toString();
						String interestOtherChargesStr = ((Double) map
								.get("TCINTEREST")).toString();
						String osAsOnNPADate = ((Double) map
								.get("OS as on NPA")).toString();
						String osAsInCivilSuit = ((Double) map
								.get("OS as in Civil Suit")).toString();
						String osAsInFirstClmLodgement = ((Double) map
								.get("OS as in Clm Logdmnt")).toString();
						claimForm.setLastDisbursementDate((new StringBuilder())
								.append("key-").append(j).toString(), dateStr);
						claimForm.setTcprincipal(
								(new StringBuilder()).append("key-").append(j)
										.toString(), principalAmntStr);
						claimForm.setTcInterestCharge((new StringBuilder())
								.append("key-").append(j).toString(),
								interestOtherChargesStr);
						claimForm.setTcOsAsOnDateOfNPA((new StringBuilder())
								.append("key-").append(j).toString(),
								osAsOnNPADate);
						claimForm.setTcOsAsStatedInCivilSuit(
								(new StringBuilder()).append("key-").append(j)
										.toString(), osAsInCivilSuit);
						claimForm.setTcOsAsOnLodgementOfClaim(
								(new StringBuilder()).append("key-").append(j)
										.toString(), osAsInFirstClmLodgement);
					}
				}

				int wcCounter = 0;
				if (wccgpans != null)
					wcCounter = wccgpans.size();
				claimForm.setWcCounter(wcCounter);
				Vector updateWCDtls = new Vector();
				String outerCgpan = null;
				for (int i = 0; i < wccgpans.size(); i++) {
					outerCgpan = (String) wccgpans.elementAt(i);
					if (outerCgpan != null) {
						for (int j = 0; j < wcVec.size(); j++) {
							HashMap innerMap = (HashMap) wcVec.elementAt(j);
							if (innerMap != null) {
								String innerCgpan = (String) innerMap
										.get("CGPAN");
								if (innerCgpan != null
										&& innerCgpan.equals(outerCgpan))
									updateWCDtls.addElement(innerMap);
							}
						}

					}
				}

				claimForm.setWcCgpansVector(updateWCDtls);
				if (tccgpans != null && wccgpans != null
						&& tccgpans.size() == 0 && wccgpans.size() == 0) {
					session.setAttribute("CurrentPage",
							"getBorrowerId.do?method=setBankId");
					throw new NoDataException(
							"There are no Loan Account(s) for this Borrower or the existing Loan Account(s) may have been closed.");
				}
				claimForm.setCgpansVector(cgpans);
				claimForm.setRecoveryModes(recoveryModes);
				claimForm.setCgpnDetails(finalUpdatedDtls);
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Setting Legal Details in the claim form!");
				Log.log(Log.INFO,
						"ClaimAction",
						"proceedFromRecoveryFilterPage()",
						(new StringBuilder())
								.append("legaldetails.getForumRecoveryProceedingsInitiated() :")
								.append(legaldetails
										.getForumRecoveryProceedingsInitiated())
								.toString());
				String forumNameStr = legaldetails
						.getForumRecoveryProceedingsInitiated();
				if (forumNameStr != null)
					if (forumNameStr.equals("Civil Court")
							|| forumNameStr.equals("DRT")
							|| forumNameStr.equals("LokAdalat")
							|| forumNameStr.equals("Revenue Recovery Autority")
							|| forumNameStr.equals("Securitisation Act ")) {
						claimForm.setForumthrulegalinitiated(legaldetails
								.getForumRecoveryProceedingsInitiated());
					} else {
						claimForm.setForumthrulegalinitiated("Others");
						claimForm.setOtherforums(legaldetails
								.getForumRecoveryProceedingsInitiated());
					}
				claimForm.setCaseregnumber(legaldetails.getSuitCaseRegNumber());
				claimForm.setProceedingsConcluded(legaldetails
						.getIsRecoveryProceedingsConcluded());
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Setting Legal Filing Date in the claim form!");
				Date legaldt_utilformat = legaldetails.getFilingDate();
				if (legaldt_utilformat != null)
					claimForm.setLegaldate(sdf.format(legaldt_utilformat));
				else
					claimForm.setLegaldate("");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Setting Name of the Forum in the claim form!");
				claimForm.setNameofforum(legaldetails.getNameOfForum());
				claimForm.setLocation(legaldetails.getLocation());
				claimForm.setAmountclaimed(String.valueOf(legaldetails
						.getAmountClaimed()));
				claimForm.setCurrentstatusremarks(legaldetails
						.getCurrentStatusRemarks());
				claimForm.setClmDtlForFirstInstllmnt(firstClmDtl);
				claimForm.setDateOfSeekingOTS(otsReqDateStr);
				claimForm.setPerGaurDtls(perGaurDtls);
				claimForm.setWhetherAccntWasWrittenOffBooks("N");
				Log.log(Log.INFO, "ClaimAction",
						"proceedFromRecoveryFilterPage",
						"Proceeding to Second Claim Installment Page...");
				firstClmDtl = null;
				npadetails = null;
				settlmntDetails = null;
				cgpnDetails = null;
				cgpans = null;
				tccgpans = null;
				wccgpans = null;
				clmAppliedAmnts = null;
				updateClmDtls = null;
				finalUpdatedDtls = null;
				recoveryModes = null;
				return mapping.findForward("secondclaimdetails");
			}
		}
		if (((String) session.getAttribute("mainMenu")).equals(MenuOptions
				.getMenu("GM_PERIODIC_INFO"))) {
			GMProcessor gmProcessor = new GMProcessor();
			ClaimActionForm claimForm = (ClaimActionForm) form;
			Recovery recoveryobject = null;
			String otsFlag = claimForm.getOtsFlag();
			if (otsFlag.equals("Y")) {
				String memberId = claimForm.getMemberId();
				String borrowerId = claimForm.getBorrowerID().toUpperCase()
						.trim();
				ClaimsProcessor processor = new ClaimsProcessor();
				Vector otsReqDtls = processor.getOTSRequestDetails(borrowerId);
				recoveryobject = (Recovery) session
						.getAttribute("RECOVERYOBJECT");
				recoveryobject.setIsRecoveryByOTS("N");
				gmProcessor.addRecoveryDetails(recoveryobject);
				claimForm.setMemberId(memberId);
				claimForm.setBorrowerID(borrowerId);
				claimForm.setOtsRequestDtls(otsReqDtls);
				String srcSubMenu = (String) session
						.getAttribute("subMenuItem");
				if (srcSubMenu.equals(MenuOptions
						.getMenu("CP_CLAIM_FOR_FIRST_INSTALLMENT")))
					session.setAttribute("OTSFirstDtlFromRecovery",
							new Boolean(true));
				if (srcSubMenu.equals(MenuOptions
						.getMenu("CP_CLAIM_FOR_SECOND_INSTALLMENT")))
					session.setAttribute("OTSSecondDtlFromRecovery",
							new Boolean(true));
				otsReqDtls = null;
				srcSubMenu = null;
				return mapping.findForward("otsdetailspage");
			}
			if (otsFlag.equals("N")) {
				recoveryobject = (Recovery) session
						.getAttribute("RECOVERYOBJECT");
				recoveryobject.setIsRecoveryByOTS("N");
				session.setAttribute("RECOVERYOBJECT", null);
				request.setAttribute(
						"message",
						"Recovery Details is not being saved. Please re-submit Recovery Details with OTS Flag as No and then file Claim Details.");
				return mapping.findForward("success");
			}
		}
		return null;
	}

	public ActionForward addFirstClaimsPageDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ClaimApplication claimApplication = new ClaimApplication();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String mId = claimForm.getMemberId();// -----------------------------------------------------------------------------------memeber
												// id
		String bid = claimForm.getBorrowerID().toUpperCase().trim();// ------------------------------------------------------------borrower
																	// id
		MemberInfo info = claimForm.getMemberDetails();
		String phone = info.getTelephone();// --------------------------------------------------------------------------------------phone
		String email = info.getEmail();// ------------------------------------------------------------------------------------------email
		String microFlag = claimForm.getMicroCategory();
		String wilful = claimForm.getWilfullDefaulter();
		String fraudFlag = claimForm.getIsAcctFraud();
		String enquiryFlag = claimForm.getIsEnquiryConcluded();
		String mliInvolvementFlag = claimForm.getIsMLIInvolved();
		String reasonForRecall = claimForm.getReasonForIssueRecallNotice();
		String reasonForFilingSuit = claimForm.getReasonForFilingSuit();
		Date assetPossessionDt = claimForm.getAssetPossessionDate();
		String inclusionOfReceipt = claimForm.getInclusionOfReciept();
		String confirmRecoveryFlag = claimForm.getConfirmRecoveryValues();
		String subsidyFlag = claimForm.getSubsidyFlag();
		String subsidyRcvd = claimForm.getIsSubsidyRcvdAfterNpa();
		String subsidyAdjusted = claimForm.getIsSubsidyAdjustedOnDues();
		double subsidyAmt = claimForm.getSubsidyAmt();
		Date subsidyDate = claimForm.getSubsidyDate();
		String comments = claimForm.getMliCommentOnFinPosition();
		String finAssistance = claimForm.getDetailsOfFinAssistance();
		String creditSupport = claimForm.getCreditSupport();
		String bankFacility = claimForm.getBankFacilityDetail();
		String watchListPlace = claimForm.getPlaceUnderWatchList();
		String remarks = claimForm.getRemarksOnNpa();

		// System.out.println("..............1");
		String nameofforum = claimForm.getNameofforum();// -----------------------------------------------------------------------name
														// of forum
		String dtOfRecallNoticeStr = claimForm.getDateOfRecallNotice();// ---------------------------------------------------------date
																		// of
																		// recal
																		// of
																		// notice
		Date recallnoticedate = sdf.parse(dtOfRecallNoticeStr,
				new ParsePosition(0));
		String forumthruwhichinitiated = claimForm.getForumthrulegalinitiated();// -----------------------------------------------forum
																				// type
		if (forumthruwhichinitiated != null
				&& forumthruwhichinitiated.equals("Others"))
			forumthruwhichinitiated = claimForm.getOtherforums();
		String casesuitregnumber = claimForm.getCaseregnumber();// ---------------------------------------------------------------case
																// reg number
		// String nameofforum = claimForm.getNameofforum();
		String dtOfFilingStr = claimForm.getLegaldate();// -----------------------------------------------------------------------legal
														// date
		Date filingdate = sdf.parse(dtOfFilingStr, new ParsePosition(0));
		String location = claimForm.getLocation();// -----------------------------------------------------------------------------location
		String amntClaimed1 = claimForm.getAmountclaimed();// --------------------------------------------------------------------amount
															// claimed
		String currentstatusremarks = claimForm.getCurrentstatusremarks();// -----------------------------------------------------current
																			// status
																			// remarks
		String recoveryprocconcluded = claimForm.getProceedingsConcluded();
		String dtOfReleaseOfWCStr = claimForm.getDateOfReleaseOfWC();
		java.util.Date dateofreleasewc = null;
		if (dtOfReleaseOfWCStr != null && dtOfReleaseOfWCStr.equals(""))
			dateofreleasewc = sdf.parse(dtOfReleaseOfWCStr,
					new ParsePosition(0));

		// System.out.println("..............2");
		String dtofseekingofots = claimForm.getDateOfSeekingOTS();// --------------------------------------------------------------date
																	// seeking
																	// OTS
		Map tclastDisbursementDts = claimForm.getLastDisbursementDate();// --------------------------------------------------------last
																		// disbursement
																		// dates
		Set tclastDisbursementDtsSet = tclastDisbursementDts.keySet();
		Iterator tclastDisbursementDtsIterator = tclastDisbursementDtsSet
				.iterator();
		String contextPath = null;
		String nameOfFile = null;
		String currentTime = (new java.sql.Date(System.currentTimeMillis()))
				.toString();
		FormFile file = claimForm.getRecallnoticefilepath();// ------------------------------------------------------------------recall
															// notice file path
		if (file != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("RECALL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					"Uploading the Recall Notice attachment....");
			byte data[] = file.getFileData();
			claimApplication.setRecallNoticeFileData(data);
			claimApplication.setRecalNoticeFileName(file.getFileName());
			uploadFile(file, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		FormFile legalfile = claimForm.getLegalAttachmentPath();// ------------------------------------------------------------legal
																// attachment
		if (legalfile != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("LEGAL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(Log.INFO, "ClaimAction", "addFirstClaimsPageDetails()",
					"Uploading Legal Details the attachment....");
			byte data[] = legalfile.getFileData();
			claimApplication.setLegalDetailsFileData(data);
			claimApplication.setLegalDetailsFileName(legalfile.getFileName());
			uploadFile(legalfile, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		TermLoanCapitalLoanDetail tcLoanDetail = null;
		Vector termCapitalDtls = new Vector();
		Vector cgpans = claimForm.getCgpansVector();// --------------------------------------------------------------------------------------cgpans
													// vector
		Vector keys = new Vector();
		while (tclastDisbursementDtsIterator.hasNext()) {
			String key = (String) tclastDisbursementDtsIterator.next();
			if (!keys.contains(key))
				keys.addElement(key);
		}
		for (int i = 0; i < keys.size(); i++) {
			// System.out.println("..............3");
			tcLoanDetail = new TermLoanCapitalLoanDetail();
			String key = (String) keys.elementAt(i);
			if (key != null) {
				tcLoanDetail.setCgpan((String) claimForm.getCgpandetails(key));
				String date = (String) claimForm.getLastDisbursementDate(key);
				tcLoanDetail.setLastDisbursementDate(sdf.parse(date,
						new ParsePosition(0)));
				String principl = (String) claimForm.getTcprincipal(key);
				if (principl.equals("") || principl == null)
					principl = "0.0";
				tcLoanDetail
						.setPrincipalRepayment(Double.parseDouble(principl));
				String interestCharges = (String) claimForm
						.getTcInterestCharge(key);
				if (interestCharges.equals("") || interestCharges == null)
					interestCharges = "0.0";
				tcLoanDetail.setInterestAndOtherCharges(Double
						.parseDouble(interestCharges));
				String osnpa = (String) claimForm.getTcOsAsOnDateOfNPA(key);
				if (osnpa.equals("") || osnpa == null)
					osnpa = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfNPA(Double
						.parseDouble(osnpa));
				String oscivilsuit = (String) claimForm
						.getTcOsAsStatedInCivilSuit(key);
				if (oscivilsuit.equals("") || oscivilsuit == null)
					oscivilsuit = "0.0";
				tcLoanDetail.setOutstandingStatedInCivilSuit(Double
						.parseDouble(oscivilsuit));
				String oslodgement = (String) claimForm
						.getTcOsAsOnLodgementOfClaim(key);
				if (oslodgement.equals("") || oslodgement == null)
					oslodgement = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfLodgement(Double
						.parseDouble(oslodgement));
				if (!termCapitalDtls.contains(tcLoanDetail))
					termCapitalDtls.addElement(tcLoanDetail);
			}
		}
		// System.out.println("..............4");
		ArrayList workingCapitalDtls = new ArrayList();
		Map wcOsAsOnNPADtls = claimForm.getWcOsAsOnDateOfNPA();
		Set wcOsAsOnNPADtlsSet = wcOsAsOnNPADtls.keySet();
		Iterator wcOsAsOnNPADtlsIterator = wcOsAsOnNPADtlsSet.iterator();
		Vector wcKeys = new Vector();
		while (wcOsAsOnNPADtlsIterator.hasNext()) {
			String key = (String) wcOsAsOnNPADtlsIterator.next();
			if (!wcKeys.contains(key))
				wcKeys.addElement(key);
		}
		WorkingCapitalDetail wcDetail = null;
		for (int i = 0; i < wcKeys.size(); i++) {
			wcDetail = new WorkingCapitalDetail();
			wcDetail.setCgpan((String) claimForm.getWcCgpan((String) wcKeys
					.elementAt(i)));
			String osnpawc = (String) claimForm
					.getWcOsAsOnDateOfNPA((String) wcKeys.elementAt(i));
			if (osnpawc.equals("") || osnpawc == null)
				osnpawc = "0.0";
			wcDetail.setOutstandingAsOnDateOfNPA(Double.parseDouble(osnpawc));
			String oswccivilsuit = (String) claimForm
					.getWcOsAsStatedInCivilSuit((String) wcKeys.elementAt(i));
			if (oswccivilsuit.equals("") || oswccivilsuit == null)
				oswccivilsuit = "0.0";
			wcDetail.setOutstandingStatedInCivilSuit(Double
					.parseDouble(oswccivilsuit));
			String oswclodgement = (String) claimForm
					.getWcOsAsOnLodgementOfClaim((String) wcKeys.elementAt(i));
			if (oswclodgement.equals("") || oswclodgement == null)
				oswclodgement = "0.0";
			wcDetail.setOutstandingAsOnDateOfLodgement(Double
					.parseDouble(oswclodgement));
			if (!workingCapitalDtls.contains(wcDetail))
				workingCapitalDtls.add(wcDetail);
		}

		Map asOnDtOfSanctionDtls = claimForm.getAsOnDtOfSanctionDtl();
		Set asOnDtOfSanctionDtlsSet = asOnDtOfSanctionDtls.keySet();
		Iterator asOnDtOfSanctionDtlsIterator = asOnDtOfSanctionDtlsSet
				.iterator();
		DtlsAsOnDateOfSanction dtlsAsOnSanctionDt = new DtlsAsOnDateOfSanction();
		while (asOnDtOfSanctionDtlsIterator.hasNext()) {
			String key = (String) asOnDtOfSanctionDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnSanctionDt.setReasonsForReduction((String) claimForm
						.getAsOnDtOfSanctionDtl(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currntassetssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (currntassetssanction.equals("")
						|| currntassetssanction == null)
					currntassetssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfCurrentAssets(Double
						.parseDouble(currntassetssanction));
			}
			if (key.trim().equals("LAND")) {
				String landasonsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (landasonsanction.equals("") || landasonsanction == null)
					landasonsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfLand(Double
						.parseDouble(landasonsanction));
			}
			if (key.trim().equals("MACHINE")) {
				String machinesanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (machinesanction.equals("") || machinesanction == null)
					machinesanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfMachine(Double
						.parseDouble(machinesanction));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (bldgsanction.equals("") || bldgsanction == null)
					bldgsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfBuilding(Double
						.parseDouble(bldgsanction));
			}
			if (key.trim().equals("OTHERS")) {
				String otherssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherssanction.equals("") || otherssanction == null)
					otherssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfOthers(Double
						.parseDouble(otherssanction));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnSanctionDt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (networthsanction.equals("") || networthsanction == null)
					networthsanction = "0.0";
				dtlsAsOnSanctionDt.setNetworthOfGuarantors(Double
						.parseDouble(networthsanction));
			}
		}
		Map asOnNPADtls = claimForm.getAsOnDtOfNPA();
		Set asOnNPADtlsSet = asOnNPADtls.keySet();
		Iterator asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
		DtlsAsOnDateOfNPA dtlsAsOnNPA = new DtlsAsOnDateOfNPA();
		while (asOnNPADtlsIterator.hasNext()) {
			String key = (String) asOnNPADtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnNPA.setReasonsForReduction((String) claimForm
						.getAsOnDtOfNPA(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetsnpa = (String) claimForm
						.getAsOnDtOfNPA(key);
				if (currentassetsnpa.equals("") || currentassetsnpa == null)
					currentassetsnpa = "0.0";
				dtlsAsOnNPA.setValueOfCurrentAssets(Double
						.parseDouble(currentassetsnpa));
			}
			if (key.trim().equals("LAND")) {
				String landnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (landnpa.equals("") || landnpa == null)
					landnpa = "0.0";
				dtlsAsOnNPA.setValueOfLand(Double.parseDouble(landnpa));
			}
			if (key.trim().equals("MACHINE")) {
				String machinenpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (machinenpa.equals("") || machinenpa == null)
					machinenpa = "0.0";
				dtlsAsOnNPA.setValueOfMachine(Double.parseDouble(machinenpa));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (bldgnpa.equals("") || bldgnpa == null)
					bldgnpa = "0.0";
				dtlsAsOnNPA.setValueOfBuilding(Double.parseDouble(bldgnpa));
			}
			if (key.trim().equals("OTHERS")) {
				String othersnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (othersnpa.equals("") || othersnpa == null)
					othersnpa = "0.0";
				dtlsAsOnNPA.setValueOfOthers(Double.parseDouble(othersnpa));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm.getAsOnDtOfNPA(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnNPA.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (networthnpa.equals("") || networthnpa == null)
					networthnpa = "0.0";
				dtlsAsOnNPA.setNetworthOfGuarantors(Double
						.parseDouble(networthnpa));
			}
		}
		Map asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
		Set asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
		Iterator asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
		DtlsAsOnLogdementOfClaim dtlsAsOnLodgemnt = new DtlsAsOnLogdementOfClaim();
		while (asOnLodgemntDtlsIterator.hasNext()) {
			String key = (String) asOnLodgemntDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnLodgemnt.setReasonsForReduction((String) claimForm
						.getAsOnLodgemntOfCredit(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (currentassetslodgemnt.equals("")
						|| currentassetslodgemnt == null)
					currentassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfCurrentAssets(Double
						.parseDouble(currentassetslodgemnt));
			}
			if (key.trim().equals("LAND")) {
				String landlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (landlodgemnt.equals("") || landlodgemnt == null)
					landlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfLand(Double
						.parseDouble(landlodgemnt));
			}
			if (key.trim().equals("MACHINE")) {
				String machinelodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (machinelodgemnt.equals("") || machinelodgemnt == null)
					machinelodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfMachine(Double
						.parseDouble(machinelodgemnt));
			}
			if (key.trim().equals("BUILDING")) {
				String bldglodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (bldglodgemnt.equals("") || bldglodgemnt == null)
					bldglodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfBuilding(Double
						.parseDouble(bldglodgemnt));
			}
			if (key.trim().equals("OTHERS")) {
				String otherslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherslodgemnt.equals("") || otherslodgemnt == null)
					otherslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOthers(Double
						.parseDouble(otherslodgemnt));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherassetslodgemnt.equals("")
						|| otherassetslodgemnt == null)
					otherassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassetslodgemnt));
			}
			if (key.trim().equals("networth")) {
				String networthlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (networthlodgemnt.equals("") || networthlodgemnt == null)
					networthlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setNetworthOfGuarantors(Double
						.parseDouble(networthlodgemnt));
			}
		}
		Vector recoveryDetailsVector = new Vector();
		Map recoveryDetails = claimForm.getCgpandetails();// -------------------------------------------recovery
															// details
		Set recoveryDetailsSet = recoveryDetails.keySet();
		Iterator recoveryDetailsIterator = recoveryDetailsSet.iterator();
		StringTokenizer tokenzier = null;
		RecoveryDetails recDetails = null;
		Vector cgpanKeys = new Vector();
		Vector recoveryKeys = new Vector();
		String tcPrincipal = "tcprincipal".trim();
		String tcInterestCharges = "tcInterestCharges".trim();
		String wcAmount = "wcAmount".trim();
		String wcOtherCharges = "wcOtherCharges".trim();
		String recoveryMode = "recoveryMode".trim();
		String substring = "recovery#key-".trim();
		while (recoveryDetailsIterator.hasNext()) {
			String key = (String) recoveryDetailsIterator.next();
			if (key != null) {
				if (key.indexOf(substring) == 0 && !cgpanKeys.contains(key))
					cgpanKeys.addElement(key);
				if (key.indexOf(substring) > 0 && !recoveryKeys.contains(key))
					recoveryKeys.addElement(key);
			}
		}
		for (int j = 0; j < cgpanKeys.size(); j++) {
			recDetails = new RecoveryDetails();
			String cgpanKey = (String) cgpanKeys.elementAt(j);
			if (cgpanKey != null) {
				recDetails.setCgpan((String) claimForm
						.getCgpandetails(cgpanKey));
				for (int i = 0; i < recoveryKeys.size(); i++) {
					String recoveryKey = (String) recoveryKeys.elementAt(i);
					if (recoveryKey != null) {
						if (recoveryKey.indexOf(tcPrincipal) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recoverypricipaltc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recoverypricipaltc.equals("")
									|| recoverypricipaltc == null)
								recoverypricipaltc = "0.0";
							recDetails.setTcPrincipal(Double
									.parseDouble(recoverypricipaltc));
						}
						if (recoveryKey.indexOf(tcInterestCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recinterestchargestc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recinterestchargestc.equals("")
									|| recinterestchargestc == null)
								recinterestchargestc = "0.0";
							recDetails.setTcInterestAndOtherCharges(Double
									.parseDouble(recinterestchargestc));
						}
						if (recoveryKey.indexOf(wcAmount) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcamount = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcamount.equals("") || wcamount == null)
								wcamount = "0.0";
							recDetails
									.setWcAmount(Double.parseDouble(wcamount));
						}
						if (recoveryKey.indexOf(wcOtherCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcothercharges = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcothercharges.equals("")
									|| wcothercharges == null)
								wcothercharges = "0.0";
							recDetails.setWcOtherCharges(Double
									.parseDouble(wcothercharges));
						}
						if (recoveryKey.indexOf(recoveryMode) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String temp = (String) claimForm
									.getCgpandetails(recoveryKey);
							recDetails.setModeOfRecovery(temp);
						}
					}
				}

				String modeOfRec = recDetails.getModeOfRecovery();
				if (modeOfRec != null && !modeOfRec.equals("")
						&& !recoveryDetailsVector.contains(recDetails))
					recoveryDetailsVector.addElement(recDetails);
			}
		}

		ArrayList claimSummaryDtlsArrayList = new ArrayList();
		ClaimSummaryDtls summaryDtls = null;
		Map claimSummaryDtls = claimForm.getClaimSummaryDetails();
		Set claimSummaryDtlsSet = claimSummaryDtls.keySet();
		Iterator claimSummaryDtlsIterator;
		for (claimSummaryDtlsIterator = claimSummaryDtlsSet.iterator(); claimSummaryDtlsIterator
				.hasNext();) {
			summaryDtls = new ClaimSummaryDtls();
			String key = (String) claimSummaryDtlsIterator.next();
			summaryDtls.setCgpan(key);
			RpDAO rpDAO = new RpDAO();
			double approvedAmt = rpDAO.getTotalSanctionedAmount(key);
			String amntClaimed = (String) claimForm.getClaimSummaryDetails(key);
			double guaranteeApprovedAmt = rpDAO
					.getTotalSanctionedAmountforCgpan(key);
			if (amntClaimed.equals("") || amntClaimed == null)
				amntClaimed = "0.0";
			if (guaranteeApprovedAmt < Double.parseDouble(amntClaimed))
				throw new DatabaseException(
						(new StringBuilder())
								.append("Claim Applied Amount for ")
								.append(key)
								.append(" is ")
								.append(amntClaimed)
								.append(" which can not be more than Loan / Limit Covered under CGFSI : ")
								.append(guaranteeApprovedAmt).toString());
			summaryDtls.setAmount(Double.parseDouble(amntClaimed));
			if (!claimSummaryDtlsArrayList.contains(summaryDtls))
				claimSummaryDtlsArrayList.add(summaryDtls);
		}

		claimApplication.setMemberId(mId);
		claimApplication.setBorrowerId(bid);
		claimApplication.setDateOfIssueOfRecallNotice(recallnoticedate);
		LegalProceedingsDetail lpd = new LegalProceedingsDetail();
		lpd.setBorrowerId(bid);
		lpd.setForumRecoveryProceedingsInitiated(forumthruwhichinitiated);
		lpd.setSuitCaseRegNumber(casesuitregnumber);
		lpd.setNameOfForum(nameofforum);
		lpd.setFilingDate(filingdate);
		lpd.setLocation(location);
		if (amntClaimed1 != null && !amntClaimed1.equals(""))
			lpd.setAmountClaimed(Double.parseDouble(amntClaimed1));
		lpd.setCurrentStatusRemarks(currentstatusremarks);
		lpd.setIsRecoveryProceedingsConcluded(recoveryprocconcluded);
		claimApplication.setLegalProceedingsDetails(lpd);
		claimApplication.setDateOfReleaseOfWC(dateofreleasewc);
		SecurityAndPersonalGuaranteeDtls sapg = new SecurityAndPersonalGuaranteeDtls();
		sapg.setDetailsAsOnDateOfSanction(dtlsAsOnSanctionDt);
		sapg.setDetailsAsOnDateOfNPA(dtlsAsOnNPA);
		sapg.setDetailsAsOnDateOfLodgementOfClaim(dtlsAsOnLodgemnt);

		claimApplication.setSecurityAndPersonalGuaranteeDtls(sapg);

		java.util.Date dtofseekofotsutilformat = sdf.parse(dtofseekingofots,
				new ParsePosition(0));

		claimApplication.setDateOfSeekingOTS(dtofseekofotsutilformat);
		claimApplication.setRecoveryDetails(recoveryDetailsVector);
		claimApplication.setTermCapitalDtls(termCapitalDtls);
		claimApplication.setWorkingCapitalDtls(workingCapitalDtls);
		claimApplication.setClaimSummaryDtls(claimSummaryDtlsArrayList);

		claimApplication.setMemberDetails(info);
		claimApplication.setUnitAssistedMSE(microFlag);
		claimApplication.setWilful(wilful);
		claimApplication.setFraudFlag(fraudFlag);
		claimApplication.setEnquiryFlag(enquiryFlag);
		claimApplication.setMliInvolvementFlag(mliInvolvementFlag);
		claimApplication.setReasonForRecall(reasonForRecall);
		claimApplication.setReasonForFilingSuit(reasonForFilingSuit);
		claimApplication.setAssetPossessionDt(assetPossessionDt);
		claimApplication.setInclusionOfReceipt(inclusionOfReceipt);
		claimApplication.setConfirmRecoveryFlag(confirmRecoveryFlag);
		claimApplication.setSubsidyFlag(subsidyFlag);
		claimApplication.setIsSubsidyRcvdAfterNpa(subsidyRcvd);
		claimApplication.setIsSubsidyAdjustedOnDues(subsidyAdjusted);
		claimApplication.setSubsidyAmt(subsidyAmt);
		claimApplication.setSubsidyDate(subsidyDate);
		claimApplication.setMliCommentOnFinPosition(comments);
		claimApplication.setDetailsOfFinAssistance(finAssistance);
		claimApplication.setCreditSupport(creditSupport);
		claimApplication.setBankFacilityDetail(bankFacility);
		claimApplication.setPlaceUnderWatchList(watchListPlace);
		claimApplication.setRemarksOnNpa(remarks);

		claimForm.setClaimapplication(claimApplication);

		sdf = null;
		mId = null;
		bid = null;
		dtOfRecallNoticeStr = null;
		recallnoticedate = null;
		forumthruwhichinitiated = null;
		casesuitregnumber = null;
		nameofforum = null;
		dtOfFilingStr = null;
		filingdate = null;
		location = null;
		amntClaimed1 = null;
		currentstatusremarks = null;
		recoveryprocconcluded = null;
		dtOfReleaseOfWCStr = null;
		dateofreleasewc = null;
		dtofseekingofots = null;
		tclastDisbursementDts = null;
		tclastDisbursementDtsSet = null;
		tclastDisbursementDtsIterator = null;
		contextPath = null;
		nameOfFile = null;
		currentTime = null;
		file = null;
		legalfile = null;
		tcLoanDetail = null;
		termCapitalDtls = null;
		cgpans = null;
		keys = null;
		workingCapitalDtls = null;
		wcOsAsOnNPADtls = null;
		wcOsAsOnNPADtlsSet = null;
		wcOsAsOnNPADtlsIterator = null;
		wcKeys = null;
		wcDetail = null;
		asOnDtOfSanctionDtls = null;
		asOnDtOfSanctionDtlsSet = null;
		asOnDtOfSanctionDtlsIterator = null;
		dtlsAsOnSanctionDt = null;
		asOnNPADtls = null;
		asOnNPADtlsSet = null;
		asOnNPADtlsIterator = null;
		dtlsAsOnNPA = null;
		asOnLodgemntDtls = null;
		asOnLodgemntDtlsSet = null;
		asOnLodgemntDtlsIterator = null;
		dtlsAsOnLodgemnt = null;
		recoveryDetailsVector = null;
		recoveryDetails = null;
		recoveryDetailsSet = null;
		recoveryDetailsIterator = null;
		tokenzier = null;
		recDetails = null;
		cgpanKeys = null;
		recoveryKeys = null;
		tcPrincipal = null;
		tcInterestCharges = null;
		wcAmount = null;
		wcOtherCharges = null;
		recoveryMode = null;
		substring = null;
		claimSummaryDtlsArrayList = null;
		summaryDtls = null;
		claimSummaryDtls = null;
		claimSummaryDtlsSet = null;
		claimSummaryDtlsIterator = null;

		microFlag = "";
		wilful = "";
		fraudFlag = "";
		enquiryFlag = "";
		mliInvolvementFlag = "";
		reasonForRecall = "";
		reasonForFilingSuit = "";
		assetPossessionDt = null;
		inclusionOfReceipt = "";
		confirmRecoveryFlag = "";
		subsidyFlag = "";
		subsidyRcvd = "";
		subsidyAdjusted = "";
		comments = "";
		finAssistance = "";
		creditSupport = "";
		bankFacility = "";
		watchListPlace = "";
		remarks = "";
		// System.out.println("..............5");
		claimForm.resetDisclaimerPage(mapping, request);
		return mapping.findForward("nextclaimdetailspage");
	}

	public ActionForward addFirstClaimsPageDetailsOld(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ClaimApplication claimApplication = new ClaimApplication();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String mId = claimForm.getMemberId();// -----------------------------------------------------------------------------------memeber
												// id
		String bid = claimForm.getBorrowerID().toUpperCase().trim();// ------------------------------------------------------------borrower
																	// id
		String dtOfRecallNoticeStr = claimForm.getDateOfRecallNotice();// ---------------------------------------------------------date
																		// of
																		// recal
																		// of
																		// notice
		Date recallnoticedate = sdf.parse(dtOfRecallNoticeStr,
				new ParsePosition(0));
		String forumthruwhichinitiated = claimForm.getForumthrulegalinitiated();// -----------------------------------------------forum
																				// type
		if (forumthruwhichinitiated != null
				&& forumthruwhichinitiated.equals("Others"))
			forumthruwhichinitiated = claimForm.getOtherforums();
		String casesuitregnumber = claimForm.getCaseregnumber();// ---------------------------------------------------------------case
																// reg number
		String nameofforum = claimForm.getNameofforum();// -----------------------------------------------------------------------name
														// of forum
		String dtOfFilingStr = claimForm.getLegaldate();// -----------------------------------------------------------------------legal
														// date
		Date filingdate = sdf.parse(dtOfFilingStr, new ParsePosition(0));
		String location = claimForm.getLocation();// -----------------------------------------------------------------------------location
		String amntClaimed1 = claimForm.getAmountclaimed();// --------------------------------------------------------------------amount
															// claimed
		String currentstatusremarks = claimForm.getCurrentstatusremarks();// -----------------------------------------------------current
																			// status
																			// remarks
		String recoveryprocconcluded = claimForm.getProceedingsConcluded();// ----------------------------------------------------proceeding
																			// concluded
		String dtOfReleaseOfWCStr = claimForm.getDateOfReleaseOfWC();// ----------------------------------------------------------date
																		// of
																		// release
																		// of wc
		Date dateofreleasewc = null;
		if (dtOfReleaseOfWCStr != null && dtOfReleaseOfWCStr.equals(""))
			dateofreleasewc = sdf.parse(dtOfReleaseOfWCStr,
					new ParsePosition(0));
		String dtofseekingofots = claimForm.getDateOfSeekingOTS();// --------------------------------------------------------------date
																	// seeking
																	// OTS
		Map tclastDisbursementDts = claimForm.getLastDisbursementDate();// --------------------------------------------------------last
																		// disbursement
																		// date
		Set tclastDisbursementDtsSet = tclastDisbursementDts.keySet();
		Iterator tclastDisbursementDtsIterator = tclastDisbursementDtsSet
				.iterator();
		String contextPath = null;
		String nameOfFile = null;
		String currentTime = (new java.sql.Date(System.currentTimeMillis()))
				.toString();
		FormFile file = claimForm.getRecallnoticefilepath();// ------------------------------------------------------------------recall
															// notice file path
		if (file != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("RECALL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					"Uploading the Recall Notice attachment....");
			byte data[] = file.getFileData();
			claimApplication.setRecallNoticeFileData(data);
			claimApplication.setRecalNoticeFileName(file.getFileName());
			uploadFile(file, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		FormFile legalfile = claimForm.getLegalAttachmentPath();
		if (legalfile != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("LEGAL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(Log.INFO, "ClaimAction", "addFirstClaimsPageDetails()",
					"Uploading Legal Details the attachment....");
			byte data[] = legalfile.getFileData();
			claimApplication.setLegalDetailsFileData(data);
			claimApplication.setLegalDetailsFileName(legalfile.getFileName());
			uploadFile(legalfile, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction", "addFirstClaimsPageDetails()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		TermLoanCapitalLoanDetail tcLoanDetail = null;
		Vector termCapitalDtls = new Vector();
		Vector cgpans = claimForm.getCgpansVector();// --------------------------------------------------------------------------------------cgpans
													// vector
		Vector keys = new Vector();
		while (tclastDisbursementDtsIterator.hasNext()) {
			String key = (String) tclastDisbursementDtsIterator.next();
			if (!keys.contains(key))
				keys.addElement(key);
		}
		for (int i = 0; i < keys.size(); i++) {
			tcLoanDetail = new TermLoanCapitalLoanDetail();
			String key = (String) keys.elementAt(i);
			if (key != null) {
				tcLoanDetail.setCgpan((String) claimForm.getCgpandetails(key));
				String date = (String) claimForm.getLastDisbursementDate(key);
				tcLoanDetail.setLastDisbursementDate(sdf.parse(date,
						new ParsePosition(0)));
				String principl = (String) claimForm.getTcprincipal(key);
				if (principl.equals("") || principl == null)
					principl = "0.0";
				tcLoanDetail
						.setPrincipalRepayment(Double.parseDouble(principl));
				String interestCharges = (String) claimForm
						.getTcInterestCharge(key);
				if (interestCharges.equals("") || interestCharges == null)
					interestCharges = "0.0";
				tcLoanDetail.setInterestAndOtherCharges(Double
						.parseDouble(interestCharges));
				String osnpa = (String) claimForm.getTcOsAsOnDateOfNPA(key);
				if (osnpa.equals("") || osnpa == null)
					osnpa = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfNPA(Double
						.parseDouble(osnpa));
				String oscivilsuit = (String) claimForm
						.getTcOsAsStatedInCivilSuit(key);
				if (oscivilsuit.equals("") || oscivilsuit == null)
					oscivilsuit = "0.0";
				tcLoanDetail.setOutstandingStatedInCivilSuit(Double
						.parseDouble(oscivilsuit));
				String oslodgement = (String) claimForm
						.getTcOsAsOnLodgementOfClaim(key);
				if (oslodgement.equals("") || oslodgement == null)
					oslodgement = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfLodgement(Double
						.parseDouble(oslodgement));
				if (!termCapitalDtls.contains(tcLoanDetail))
					termCapitalDtls.addElement(tcLoanDetail);
			}
		}

		ArrayList workingCapitalDtls = new ArrayList();
		Map wcOsAsOnNPADtls = claimForm.getWcOsAsOnDateOfNPA();
		Set wcOsAsOnNPADtlsSet = wcOsAsOnNPADtls.keySet();
		Iterator wcOsAsOnNPADtlsIterator = wcOsAsOnNPADtlsSet.iterator();
		Vector wcKeys = new Vector();
		while (wcOsAsOnNPADtlsIterator.hasNext()) {
			String key = (String) wcOsAsOnNPADtlsIterator.next();
			if (!wcKeys.contains(key))
				wcKeys.addElement(key);
		}
		WorkingCapitalDetail wcDetail = null;
		for (int i = 0; i < wcKeys.size(); i++) {
			wcDetail = new WorkingCapitalDetail();
			wcDetail.setCgpan((String) claimForm.getWcCgpan((String) wcKeys
					.elementAt(i)));
			String osnpawc = (String) claimForm
					.getWcOsAsOnDateOfNPA((String) wcKeys.elementAt(i));
			if (osnpawc.equals("") || osnpawc == null)
				osnpawc = "0.0";
			wcDetail.setOutstandingAsOnDateOfNPA(Double.parseDouble(osnpawc));
			String oswccivilsuit = (String) claimForm
					.getWcOsAsStatedInCivilSuit((String) wcKeys.elementAt(i));
			if (oswccivilsuit.equals("") || oswccivilsuit == null)
				oswccivilsuit = "0.0";
			wcDetail.setOutstandingStatedInCivilSuit(Double
					.parseDouble(oswccivilsuit));
			String oswclodgement = (String) claimForm
					.getWcOsAsOnLodgementOfClaim((String) wcKeys.elementAt(i));
			if (oswclodgement.equals("") || oswclodgement == null)
				oswclodgement = "0.0";
			wcDetail.setOutstandingAsOnDateOfLodgement(Double
					.parseDouble(oswclodgement));
			if (!workingCapitalDtls.contains(wcDetail))
				workingCapitalDtls.add(wcDetail);
		}

		Map asOnDtOfSanctionDtls = claimForm.getAsOnDtOfSanctionDtl();
		Set asOnDtOfSanctionDtlsSet = asOnDtOfSanctionDtls.keySet();
		Iterator asOnDtOfSanctionDtlsIterator = asOnDtOfSanctionDtlsSet
				.iterator();
		DtlsAsOnDateOfSanction dtlsAsOnSanctionDt = new DtlsAsOnDateOfSanction();
		while (asOnDtOfSanctionDtlsIterator.hasNext()) {
			String key = (String) asOnDtOfSanctionDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnSanctionDt.setReasonsForReduction((String) claimForm
						.getAsOnDtOfSanctionDtl(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currntassetssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (currntassetssanction.equals("")
						|| currntassetssanction == null)
					currntassetssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfCurrentAssets(Double
						.parseDouble(currntassetssanction));
			}
			if (key.trim().equals("LAND")) {
				String landasonsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (landasonsanction.equals("") || landasonsanction == null)
					landasonsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfLand(Double
						.parseDouble(landasonsanction));
			}
			if (key.trim().equals("MACHINE")) {
				String machinesanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (machinesanction.equals("") || machinesanction == null)
					machinesanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfMachine(Double
						.parseDouble(machinesanction));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (bldgsanction.equals("") || bldgsanction == null)
					bldgsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfBuilding(Double
						.parseDouble(bldgsanction));
			}
			if (key.trim().equals("OTHERS")) {
				String otherssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherssanction.equals("") || otherssanction == null)
					otherssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfOthers(Double
						.parseDouble(otherssanction));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnSanctionDt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (networthsanction.equals("") || networthsanction == null)
					networthsanction = "0.0";
				dtlsAsOnSanctionDt.setNetworthOfGuarantors(Double
						.parseDouble(networthsanction));
			}
		}
		Map asOnNPADtls = claimForm.getAsOnDtOfNPA();
		Set asOnNPADtlsSet = asOnNPADtls.keySet();
		Iterator asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
		DtlsAsOnDateOfNPA dtlsAsOnNPA = new DtlsAsOnDateOfNPA();
		while (asOnNPADtlsIterator.hasNext()) {
			String key = (String) asOnNPADtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnNPA.setReasonsForReduction((String) claimForm
						.getAsOnDtOfNPA(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetsnpa = (String) claimForm
						.getAsOnDtOfNPA(key);
				if (currentassetsnpa.equals("") || currentassetsnpa == null)
					currentassetsnpa = "0.0";
				dtlsAsOnNPA.setValueOfCurrentAssets(Double
						.parseDouble(currentassetsnpa));
			}
			if (key.trim().equals("LAND")) {
				String landnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (landnpa.equals("") || landnpa == null)
					landnpa = "0.0";
				dtlsAsOnNPA.setValueOfLand(Double.parseDouble(landnpa));
			}
			if (key.trim().equals("MACHINE")) {
				String machinenpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (machinenpa.equals("") || machinenpa == null)
					machinenpa = "0.0";
				dtlsAsOnNPA.setValueOfMachine(Double.parseDouble(machinenpa));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (bldgnpa.equals("") || bldgnpa == null)
					bldgnpa = "0.0";
				dtlsAsOnNPA.setValueOfBuilding(Double.parseDouble(bldgnpa));
			}
			if (key.trim().equals("OTHERS")) {
				String othersnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (othersnpa.equals("") || othersnpa == null)
					othersnpa = "0.0";
				dtlsAsOnNPA.setValueOfOthers(Double.parseDouble(othersnpa));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm.getAsOnDtOfNPA(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnNPA.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (networthnpa.equals("") || networthnpa == null)
					networthnpa = "0.0";
				dtlsAsOnNPA.setNetworthOfGuarantors(Double
						.parseDouble(networthnpa));
			}
		}
		Map asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
		Set asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
		Iterator asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
		DtlsAsOnLogdementOfClaim dtlsAsOnLodgemnt = new DtlsAsOnLogdementOfClaim();
		while (asOnLodgemntDtlsIterator.hasNext()) {
			String key = (String) asOnLodgemntDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnLodgemnt.setReasonsForReduction((String) claimForm
						.getAsOnLodgemntOfCredit(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (currentassetslodgemnt.equals("")
						|| currentassetslodgemnt == null)
					currentassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfCurrentAssets(Double
						.parseDouble(currentassetslodgemnt));
			}
			if (key.trim().equals("LAND")) {
				String landlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (landlodgemnt.equals("") || landlodgemnt == null)
					landlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfLand(Double
						.parseDouble(landlodgemnt));
			}
			if (key.trim().equals("MACHINE")) {
				String machinelodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (machinelodgemnt.equals("") || machinelodgemnt == null)
					machinelodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfMachine(Double
						.parseDouble(machinelodgemnt));
			}
			if (key.trim().equals("BUILDING")) {
				String bldglodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (bldglodgemnt.equals("") || bldglodgemnt == null)
					bldglodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfBuilding(Double
						.parseDouble(bldglodgemnt));
			}
			if (key.trim().equals("OTHERS")) {
				String otherslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherslodgemnt.equals("") || otherslodgemnt == null)
					otherslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOthers(Double
						.parseDouble(otherslodgemnt));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherassetslodgemnt.equals("")
						|| otherassetslodgemnt == null)
					otherassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassetslodgemnt));
			}
			if (key.trim().equals("networth")) {
				String networthlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (networthlodgemnt.equals("") || networthlodgemnt == null)
					networthlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setNetworthOfGuarantors(Double
						.parseDouble(networthlodgemnt));
			}
		}
		Vector recoveryDetailsVector = new Vector();
		Map recoveryDetails = claimForm.getCgpandetails();
		Set recoveryDetailsSet = recoveryDetails.keySet();
		Iterator recoveryDetailsIterator = recoveryDetailsSet.iterator();
		StringTokenizer tokenzier = null;
		RecoveryDetails recDetails = null;
		Vector cgpanKeys = new Vector();
		Vector recoveryKeys = new Vector();
		String tcPrincipal = "tcprincipal".trim();
		String tcInterestCharges = "tcInterestCharges".trim();
		String wcAmount = "wcAmount".trim();
		String wcOtherCharges = "wcOtherCharges".trim();
		String recoveryMode = "recoveryMode".trim();
		String substring = "recovery#key-".trim();
		while (recoveryDetailsIterator.hasNext()) {
			String key = (String) recoveryDetailsIterator.next();
			if (key != null) {
				if (key.indexOf(substring) == 0 && !cgpanKeys.contains(key))
					cgpanKeys.addElement(key);
				if (key.indexOf(substring) > 0 && !recoveryKeys.contains(key))
					recoveryKeys.addElement(key);
			}
		}
		for (int j = 0; j < cgpanKeys.size(); j++) {
			recDetails = new RecoveryDetails();
			String cgpanKey = (String) cgpanKeys.elementAt(j);
			if (cgpanKey != null) {
				recDetails.setCgpan((String) claimForm
						.getCgpandetails(cgpanKey));
				for (int i = 0; i < recoveryKeys.size(); i++) {
					String recoveryKey = (String) recoveryKeys.elementAt(i);
					if (recoveryKey != null) {
						if (recoveryKey.indexOf(tcPrincipal) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recoverypricipaltc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recoverypricipaltc.equals("")
									|| recoverypricipaltc == null)
								recoverypricipaltc = "0.0";
							recDetails.setTcPrincipal(Double
									.parseDouble(recoverypricipaltc));
						}
						if (recoveryKey.indexOf(tcInterestCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recinterestchargestc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recinterestchargestc.equals("")
									|| recinterestchargestc == null)
								recinterestchargestc = "0.0";
							recDetails.setTcInterestAndOtherCharges(Double
									.parseDouble(recinterestchargestc));
						}
						if (recoveryKey.indexOf(wcAmount) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcamount = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcamount.equals("") || wcamount == null)
								wcamount = "0.0";
							recDetails
									.setWcAmount(Double.parseDouble(wcamount));
						}
						if (recoveryKey.indexOf(wcOtherCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcothercharges = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcothercharges.equals("")
									|| wcothercharges == null)
								wcothercharges = "0.0";
							recDetails.setWcOtherCharges(Double
									.parseDouble(wcothercharges));
						}
						if (recoveryKey.indexOf(recoveryMode) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String temp = (String) claimForm
									.getCgpandetails(recoveryKey);
							recDetails.setModeOfRecovery(temp);
						}
					}
				}

				String modeOfRec = recDetails.getModeOfRecovery();
				if (modeOfRec != null && !modeOfRec.equals("")
						&& !recoveryDetailsVector.contains(recDetails))
					recoveryDetailsVector.addElement(recDetails);
			}
		}

		ArrayList claimSummaryDtlsArrayList = new ArrayList();
		ClaimSummaryDtls summaryDtls = null;
		Map claimSummaryDtls = claimForm.getClaimSummaryDetails();
		Set claimSummaryDtlsSet = claimSummaryDtls.keySet();
		Iterator claimSummaryDtlsIterator;
		for (claimSummaryDtlsIterator = claimSummaryDtlsSet.iterator(); claimSummaryDtlsIterator
				.hasNext();) {
			summaryDtls = new ClaimSummaryDtls();
			String key = (String) claimSummaryDtlsIterator.next();
			summaryDtls.setCgpan(key);
			RpDAO rpDAO = new RpDAO();
			double approvedAmt = rpDAO.getTotalSanctionedAmount(key);
			String amntClaimed = (String) claimForm.getClaimSummaryDetails(key);
			double guaranteeApprovedAmt = rpDAO
					.getTotalSanctionedAmountforCgpan(key);
			if (amntClaimed.equals("") || amntClaimed == null)
				amntClaimed = "0.0";
			if (guaranteeApprovedAmt < Double.parseDouble(amntClaimed))
				throw new DatabaseException(
						(new StringBuilder())
								.append("Claim Applied Amount for ")
								.append(key)
								.append(" is ")
								.append(amntClaimed)
								.append(" which can not be more than Loan / Limit Covered under CGFSI : ")
								.append(guaranteeApprovedAmt).toString());
			summaryDtls.setAmount(Double.parseDouble(amntClaimed));
			if (!claimSummaryDtlsArrayList.contains(summaryDtls))
				claimSummaryDtlsArrayList.add(summaryDtls);
		}

		claimApplication.setMemberId(mId);
		claimApplication.setBorrowerId(bid);
		claimApplication.setDateOfIssueOfRecallNotice(recallnoticedate);
		LegalProceedingsDetail lpd = new LegalProceedingsDetail();
		lpd.setBorrowerId(bid);
		lpd.setForumRecoveryProceedingsInitiated(forumthruwhichinitiated);
		lpd.setSuitCaseRegNumber(casesuitregnumber);
		lpd.setNameOfForum(nameofforum);
		lpd.setFilingDate(filingdate);
		lpd.setLocation(location);
		if (amntClaimed1 != null && !amntClaimed1.equals(""))
			lpd.setAmountClaimed(Double.parseDouble(amntClaimed1));
		lpd.setCurrentStatusRemarks(currentstatusremarks);
		lpd.setIsRecoveryProceedingsConcluded(recoveryprocconcluded);
		claimApplication.setLegalProceedingsDetails(lpd);
		claimApplication.setDateOfReleaseOfWC(dateofreleasewc);
		SecurityAndPersonalGuaranteeDtls sapg = new SecurityAndPersonalGuaranteeDtls();
		sapg.setDetailsAsOnDateOfSanction(dtlsAsOnSanctionDt);
		sapg.setDetailsAsOnDateOfNPA(dtlsAsOnNPA);
		sapg.setDetailsAsOnDateOfLodgementOfClaim(dtlsAsOnLodgemnt);
		claimApplication.setSecurityAndPersonalGuaranteeDtls(sapg);
		Date dtofseekofotsutilformat = sdf.parse(dtofseekingofots,
				new ParsePosition(0));
		claimApplication.setDateOfSeekingOTS(dtofseekofotsutilformat);
		claimApplication.setRecoveryDetails(recoveryDetailsVector);
		claimApplication.setTermCapitalDtls(termCapitalDtls);
		claimApplication.setWorkingCapitalDtls(workingCapitalDtls);
		claimApplication.setClaimSummaryDtls(claimSummaryDtlsArrayList);
		claimForm.setClaimapplication(claimApplication);
		sdf = null;
		mId = null;
		bid = null;
		dtOfRecallNoticeStr = null;
		recallnoticedate = null;
		forumthruwhichinitiated = null;
		casesuitregnumber = null;
		nameofforum = null;
		dtOfFilingStr = null;
		filingdate = null;
		location = null;
		amntClaimed1 = null;
		currentstatusremarks = null;
		recoveryprocconcluded = null;
		dtOfReleaseOfWCStr = null;
		dateofreleasewc = null;
		dtofseekingofots = null;
		tclastDisbursementDts = null;
		tclastDisbursementDtsSet = null;
		tclastDisbursementDtsIterator = null;
		contextPath = null;
		nameOfFile = null;
		currentTime = null;
		file = null;
		legalfile = null;
		tcLoanDetail = null;
		termCapitalDtls = null;
		cgpans = null;
		keys = null;
		workingCapitalDtls = null;
		wcOsAsOnNPADtls = null;
		wcOsAsOnNPADtlsSet = null;
		wcOsAsOnNPADtlsIterator = null;
		wcKeys = null;
		wcDetail = null;
		asOnDtOfSanctionDtls = null;
		asOnDtOfSanctionDtlsSet = null;
		asOnDtOfSanctionDtlsIterator = null;
		dtlsAsOnSanctionDt = null;
		asOnNPADtls = null;
		asOnNPADtlsSet = null;
		asOnNPADtlsIterator = null;
		dtlsAsOnNPA = null;
		asOnLodgemntDtls = null;
		asOnLodgemntDtlsSet = null;
		asOnLodgemntDtlsIterator = null;
		dtlsAsOnLodgemnt = null;
		recoveryDetailsVector = null;
		recoveryDetails = null;
		recoveryDetailsSet = null;
		recoveryDetailsIterator = null;
		tokenzier = null;
		recDetails = null;
		cgpanKeys = null;
		recoveryKeys = null;
		tcPrincipal = null;
		tcInterestCharges = null;
		wcAmount = null;
		wcOtherCharges = null;
		recoveryMode = null;
		substring = null;
		claimSummaryDtlsArrayList = null;
		summaryDtls = null;
		claimSummaryDtls = null;
		claimSummaryDtlsSet = null;
		claimSummaryDtlsIterator = null;
		claimForm.resetDisclaimerPage(mapping, request);
		return mapping.findForward("nextclaimdetailspage");
	}

	public ActionForward saveClaimApplication(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimApplication claimapplication = claimForm.getClaimapplication();
		ClaimsProcessor processor = new ClaimsProcessor();
		String nameofofficial = claimForm.getNameOfOfficial();
		String designation = claimForm.getDesignationOfOfficial();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		HashMap itpanDetails = claimForm.getItpanDetails();
		String claimsubmitteddt = claimForm.getClaimSubmittedDate();
		String place = claimForm.getPlace();
		String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
		claimapplication.setNameOfOfficial(nameofofficial);
		claimapplication.setDesignationOfOfficial(designation);
		claimapplication.setClaimSubmittedDate(sdf.parse(claimsubmitteddt,
				new ParsePosition(0)));
		claimapplication.setPlace(place);
		claimapplication.setFirstInstallment(true);
		java.util.Date subsidyDate = claimForm.getSubsidyDate();
		double subsidyAmt = claimForm.getSubsidyAmt();
		String ifsCode = claimForm.getIfsCode();
		String neftCode = claimForm.getNeftCode();
		String rtgsBankName = claimForm.getRtgsBankName();
		String rtgsBranchName = claimForm.getRtgsBranchName();
		String rtgsBankNumber = claimForm.getRtgsBankNumber();
		String microCategory = claimForm.getMicroCategory();
		claimapplication.setSubsidyDate(subsidyDate);
		claimapplication.setSubsidyAmt(subsidyAmt);
		claimapplication.setIfsCode(ifsCode);
		claimapplication.setNeftCode(neftCode);
		claimapplication.setRtgsBankName(rtgsBankName);
		claimapplication.setRtgsBranchName(rtgsBranchName);
		claimapplication.setRtgsBankNumber(rtgsBankNumber);
		claimapplication.setMicroCategory(microCategory);
		String unitName = claimForm.getBorrowerDetails().getBorrowerName();
		boolean internetUser = true;
		User userInfo = getUserInformation(request);
		String userId = userInfo.getUserId();
		claimapplication.setCreatedModifiedy(userId);
		if ((new StringBuilder()).append(userInfo.getBankId())
				.append(userInfo.getZoneId()).append(userInfo.getBranchId())
				.toString().equals("000000000000")
				&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER")) {
			internetUser = false;
		}

		String clmRefNumber = processor.addClaimApplication(claimapplication,
				itpanDetails, internetUser);
		System.out.println("clm ref no:" + clmRefNumber);

		claimForm.setClmRefNumber(clmRefNumber);
		claimForm.resetTheFirstClaimApplication(mapping, request);
		claimForm.setNameOfOfficial("");
		claimForm.setDesignationOfOfficial("");
		claimForm.setPlace("");
		claimForm.setForumthrulegalinitiated("");
		Log.log(4, "ClaimAction", "saveClaimApplication", "Exited");
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Application for First Claim Installment for ")
						.append(borrowerId)
						.append("\n has been saved successfully. Claim Reference Number is : ")
						.append(clmRefNumber).toString());
		return mapping.findForward("submitClaim");
	}

	public ActionForward getMemIdClmRefNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("secondClaimInstallmentPage");
	}

	public ActionForward displayDisclaimerForSecInstallment(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimApplication claimApplication = new ClaimApplication();
		String mId = claimForm.getMemberId();
		String bid = claimForm.getBorrowerID().toUpperCase().trim();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String whetherBorrowerIsWilfulDefaulter = claimForm
				.getWilfullDefaulter();
		String dateOfIssueOfRecallNotice = claimForm.getDateOfRecallNotice();
		String forumRecoveryProceedingsInitiated = claimForm
				.getForumthrulegalinitiated();
		if (forumRecoveryProceedingsInitiated != null
				&& forumRecoveryProceedingsInitiated.equals("Others"))
			forumRecoveryProceedingsInitiated = claimForm.getOtherforums();
		String caseregnumber = claimForm.getCaseregnumber();
		String legaldate = claimForm.getLegaldate();
		String nameOfForum = claimForm.getNameofforum();
		String location = claimForm.getLocation();
		String amountClaimed = claimForm.getAmountclaimed();
		String currentStatusRemarks = claimForm.getCurrentstatusremarks();
		String proceedingsConcluded = claimForm.getProceedingsConcluded();
		String dtOfConclusionOfRecoveryProc = claimForm
				.getDtOfConclusionOfRecoveryProc();
		String whetherAccntWasWrittenOffBooks = claimForm
				.getWhetherAccntWasWrittenOffBooks();
		String dtOnWhichAccntWrittenOff = claimForm
				.getDtOnWhichAccntWrittenOff();
		String dateOfReleaseOfWC = claimForm.getDateOfReleaseOfWC();
		String dateOfSeekingOTS = claimForm.getDateOfSeekingOTS();
		String contextPath = null;
		String nameOfFile = null;
		String currentTime = (new java.sql.Date(System.currentTimeMillis()))
				.toString();
		FormFile file = claimForm.getRecallnoticefilepath();
		if (file != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("RECALL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(5, "ClaimAction", "displayDisclaimerForSecInstallment()",
					"Uploading the Recall Notice attachment....");
			byte data[] = file.getFileData();
			claimApplication.setRecallNoticeFileData(data);
			claimApplication.setRecalNoticeFileName(file.getFileName());
			uploadFile(file, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "displayDisclaimerForSecInstallment()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction",
						"displayDisclaimerForSecInstallment()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		FormFile legalfile = claimForm.getLegalAttachmentPath();
		if (legalfile != null) {
			contextPath = request.getSession().getServletContext()
					.getRealPath("");
			nameOfFile = (new StringBuilder()).append("LEGAL-")
					.append(currentTime).append("-").append(mId).append(bid)
					.toString();
			Log.log(Log.INFO, "ClaimAction",
					"displayDisclaimerForSecInstallment()",
					"Uploading Legal Details the attachment....");
			byte data[] = legalfile.getFileData();
			claimApplication.setLegalDetailsFileData(data);
			claimApplication.setLegalDetailsFileName(legalfile.getFileName());
			uploadFile(legalfile, contextPath, nameOfFile);
			Log.log(5, "ClaimAction", "displayDisclaimerForSecInstallment()",
					(new StringBuilder()).append("File Name, File data ")
							.append(file.getFileName()).append(",")
							.append(data).toString());
			if (data != null)
				Log.log(5, "ClaimAction",
						"displayDisclaimerForSecInstallment()",
						(new StringBuilder()).append("File data length ")
								.append(data.length).toString());
		}
		Map tclastDisbursementDts = claimForm.getLastDisbursementDate();
		Set tclastDisbursementDtsSet = tclastDisbursementDts.keySet();
		Iterator tclastDisbursementDtsIterator = tclastDisbursementDtsSet
				.iterator();
		TermLoanCapitalLoanDetail tcLoanDetail = null;
		Vector termCapitalDtls = new Vector();
		Vector keys = new Vector();
		while (tclastDisbursementDtsIterator.hasNext()) {
			String key = (String) tclastDisbursementDtsIterator.next();
			if (!keys.contains(key))
				keys.addElement(key);
		}
		for (int i = 0; i < keys.size(); i++) {
			tcLoanDetail = new TermLoanCapitalLoanDetail();
			String key = (String) keys.elementAt(i);
			if (key != null) {
				tcLoanDetail.setCgpan((String) claimForm.getCgpandetails(key));
				String date = (String) claimForm.getLastDisbursementDate(key);
				tcLoanDetail.setLastDisbursementDate(sdf.parse(date,
						new ParsePosition(0)));
				String principl = (String) claimForm.getTcprincipal(key);
				if (principl.equals("") || principl == null)
					principl = "0.0";
				tcLoanDetail
						.setPrincipalRepayment(Double.parseDouble(principl));
				String interestCharges = (String) claimForm
						.getTcInterestCharge(key);
				if (interestCharges.equals("") || interestCharges == null)
					interestCharges = "0.0";
				tcLoanDetail.setInterestAndOtherCharges(Double
						.parseDouble(interestCharges));
				String osnpa = (String) claimForm.getTcOsAsOnDateOfNPA(key);
				if (osnpa.equals("") || osnpa == null)
					osnpa = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfNPA(Double
						.parseDouble(osnpa));
				String oscivilsuit = (String) claimForm
						.getTcOsAsStatedInCivilSuit(key);
				if (oscivilsuit.equals("") || oscivilsuit == null)
					oscivilsuit = "0.0";
				tcLoanDetail.setOutstandingStatedInCivilSuit(Double
						.parseDouble(oscivilsuit));
				String oslodgement = (String) claimForm
						.getTcOsAsOnLodgementOfClaim(key);
				if (oslodgement.equals("") || oslodgement == null)
					oslodgement = "0.0";
				tcLoanDetail.setOutstandingAsOnDateOfLodgement(Double
						.parseDouble(oslodgement));
				String oslodgemntSecondClm = (String) claimForm
						.getTcOsAsOnLodgementOfSecondClaim(key);
				if (oslodgemntSecondClm.equals("")
						|| oslodgemntSecondClm == null)
					oslodgemntSecondClm = "0.0";
				double osAsOnDtOfSecClmInstmnt = Double
						.parseDouble(oslodgemntSecondClm);
				tcLoanDetail
						.setOsAsOnDateOfLodgementOfClmForSecInstllmnt(osAsOnDtOfSecClmInstmnt);
				if (!termCapitalDtls.contains(tcLoanDetail))
					termCapitalDtls.addElement(tcLoanDetail);
			}
		}

		ArrayList workingCapitalDtls = new ArrayList();
		Map wcOsAsOnNPADtls = claimForm.getWcOsAsOnDateOfNPA();
		Set wcOsAsOnNPADtlsSet = wcOsAsOnNPADtls.keySet();
		Iterator wcOsAsOnNPADtlsIterator = wcOsAsOnNPADtlsSet.iterator();
		Vector wcKeys = new Vector();
		while (wcOsAsOnNPADtlsIterator.hasNext()) {
			String key = (String) wcOsAsOnNPADtlsIterator.next();
			if (!wcKeys.contains(key))
				wcKeys.addElement(key);
		}
		WorkingCapitalDetail wcDetail = null;
		for (int i = 0; i < wcKeys.size(); i++) {
			wcDetail = new WorkingCapitalDetail();
			wcDetail.setCgpan((String) claimForm.getWcCgpan((String) wcKeys
					.elementAt(i)));
			String osnpawc = (String) claimForm
					.getWcOsAsOnDateOfNPA((String) wcKeys.elementAt(i));
			if (osnpawc.equals("") || osnpawc == null)
				osnpawc = "0.0";
			wcDetail.setOutstandingAsOnDateOfNPA(Double.parseDouble(osnpawc));
			String oswccivilsuit = (String) claimForm
					.getWcOsAsStatedInCivilSuit((String) wcKeys.elementAt(i));
			if (oswccivilsuit.equals("") || oswccivilsuit == null)
				oswccivilsuit = "0.0";
			wcDetail.setOutstandingStatedInCivilSuit(Double
					.parseDouble(oswccivilsuit));
			String oswclodgement = (String) claimForm
					.getWcOsAsOnLodgementOfClaim((String) wcKeys.elementAt(i));
			if (oswclodgement.equals("") || oswclodgement == null)
				oswclodgement = "0.0";
			wcDetail.setOutstandingAsOnDateOfLodgement(Double
					.parseDouble(oswclodgement));
			String oswclodgementOfSecondClm = (String) claimForm
					.getWcOsAsOnLodgementOfSecondClaim((String) wcKeys
							.elementAt(i));
			if (oswclodgementOfSecondClm.equals("")
					|| oswclodgementOfSecondClm == null)
				oswclodgementOfSecondClm = "0.0";
			wcDetail.setOsAsOnDateOfLodgementOfClmForSecInstllmnt(Double
					.parseDouble(oswclodgementOfSecondClm));
			if (!workingCapitalDtls.contains(wcDetail))
				workingCapitalDtls.add(wcDetail);
		}

		Vector recoveryDetailsVector = new Vector();
		Map recoveryDetails = claimForm.getCgpandetails();
		Set recoveryDetailsSet = recoveryDetails.keySet();
		Iterator recoveryDetailsIterator = recoveryDetailsSet.iterator();
		RecoveryDetails recDetails = null;
		Vector cgpanKeys = new Vector();
		Vector recoveryKeys = new Vector();
		String tcPrincipal = "tcprincipal".trim();
		String tcInterestCharges = "tcInterestCharges".trim();
		String wcAmount = "wcAmount".trim();
		String wcOtherCharges = "wcOtherCharges".trim();
		String recoveryMode = "recoveryMode".trim();
		String substring = "recovery#key-".trim();
		while (recoveryDetailsIterator.hasNext()) {
			String key = (String) recoveryDetailsIterator.next();
			if (key != null) {
				if (key.indexOf(substring) == 0 && !cgpanKeys.contains(key))
					cgpanKeys.addElement(key);
				if (key.indexOf(substring) > 0 && !recoveryKeys.contains(key))
					recoveryKeys.addElement(key);
			}
		}
		for (int j = 0; j < cgpanKeys.size(); j++) {
			recDetails = new RecoveryDetails();
			String cgpanKey = (String) cgpanKeys.elementAt(j);
			if (cgpanKey != null) {
				recDetails.setCgpan((String) claimForm
						.getCgpandetails(cgpanKey));
				for (int i = 0; i < recoveryKeys.size(); i++) {
					String recoveryKey = (String) recoveryKeys.elementAt(i);
					if (recoveryKey != null) {
						if (recoveryKey.indexOf(tcPrincipal) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recoverypricipaltc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recoverypricipaltc.equals("")
									|| recoverypricipaltc == null)
								recoverypricipaltc = "0.0";
							recDetails.setTcPrincipal(Double
									.parseDouble(recoverypricipaltc));
						}
						if (recoveryKey.indexOf(tcInterestCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String recinterestchargestc = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (recinterestchargestc.equals("")
									|| recinterestchargestc == null)
								recinterestchargestc = "0.0";
							recDetails.setTcInterestAndOtherCharges(Double
									.parseDouble(recinterestchargestc));
						}
						if (recoveryKey.indexOf(wcAmount) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcamount = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcamount.equals("") || wcamount == null)
								wcamount = "0.0";
							recDetails
									.setWcAmount(Double.parseDouble(wcamount));
						}
						if (recoveryKey.indexOf(wcOtherCharges) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0) {
							String wcothercharges = (String) claimForm
									.getCgpandetails(recoveryKey);
							if (wcothercharges.equals("")
									|| wcothercharges == null)
								wcothercharges = "0.0";
							recDetails.setWcOtherCharges(Double
									.parseDouble(wcothercharges));
						}
						if (recoveryKey.indexOf(recoveryMode) == 0
								&& recoveryKey.indexOf(cgpanKey) > 0)
							recDetails.setModeOfRecovery((String) claimForm
									.getCgpandetails(recoveryKey));
					}
				}

				String modeOfRec = recDetails.getModeOfRecovery();
				if (modeOfRec != null && !modeOfRec.equals("")
						&& !recoveryDetailsVector.contains(recDetails))
					recoveryDetailsVector.addElement(recDetails);
			}
		}

		Map asOnDtOfSanctionDtls = claimForm.getAsOnDtOfSanctionDtl();
		Set asOnDtOfSanctionDtlsSet = asOnDtOfSanctionDtls.keySet();
		Iterator asOnDtOfSanctionDtlsIterator = asOnDtOfSanctionDtlsSet
				.iterator();
		DtlsAsOnDateOfSanction dtlsAsOnSanctionDt = new DtlsAsOnDateOfSanction();
		while (asOnDtOfSanctionDtlsIterator.hasNext()) {
			String key = (String) asOnDtOfSanctionDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnSanctionDt.setReasonsForReduction((String) claimForm
						.getAsOnDtOfSanctionDtl(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currntassetssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (currntassetssanction.equals("")
						|| currntassetssanction == null)
					currntassetssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfCurrentAssets(Double
						.parseDouble(currntassetssanction));
			}
			if (key.trim().equals("LAND")) {
				String landasonsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (landasonsanction.equals("") || landasonsanction == null)
					landasonsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfLand(Double
						.parseDouble(landasonsanction));
			}
			if (key.trim().equals("MACHINE")) {
				String machinesanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (machinesanction.equals("") || machinesanction == null)
					machinesanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfMachine(Double
						.parseDouble(machinesanction));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (bldgsanction.equals("") || bldgsanction == null)
					bldgsanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfBuilding(Double
						.parseDouble(bldgsanction));
			}
			if (key.trim().equals("OTHERS")) {
				String otherssanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherssanction.equals("") || otherssanction == null)
					otherssanction = "0.0";
				dtlsAsOnSanctionDt.setValueOfOthers(Double
						.parseDouble(otherssanction));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnSanctionDt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthsanction = (String) claimForm
						.getAsOnDtOfSanctionDtl(key);
				if (networthsanction.equals("") || networthsanction == null)
					networthsanction = "0.0";
				dtlsAsOnSanctionDt.setNetworthOfGuarantors(Double
						.parseDouble(networthsanction));
			}
		}
		Map asOnNPADtls = claimForm.getAsOnDtOfNPA();
		Set asOnNPADtlsSet = asOnNPADtls.keySet();
		Iterator asOnNPADtlsIterator = asOnNPADtlsSet.iterator();
		DtlsAsOnDateOfNPA dtlsAsOnNPA = new DtlsAsOnDateOfNPA();
		while (asOnNPADtlsIterator.hasNext()) {
			String key = (String) asOnNPADtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnNPA.setReasonsForReduction((String) claimForm
						.getAsOnDtOfNPA(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetsnpa = (String) claimForm
						.getAsOnDtOfNPA(key);
				if (currentassetsnpa.equals("") || currentassetsnpa == null)
					currentassetsnpa = "0.0";
				dtlsAsOnNPA.setValueOfCurrentAssets(Double
						.parseDouble(currentassetsnpa));
			}
			if (key.trim().equals("LAND")) {
				String landnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (landnpa.equals("") || landnpa == null)
					landnpa = "0.0";
				dtlsAsOnNPA.setValueOfLand(Double.parseDouble(landnpa));
			}
			if (key.trim().equals("MACHINE")) {
				String machinenpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (machinenpa.equals("") || machinenpa == null)
					machinenpa = "0.0";
				dtlsAsOnNPA.setValueOfMachine(Double.parseDouble(machinenpa));
			}
			if (key.trim().equals("BUILDING")) {
				String bldgnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (bldgnpa.equals("") || bldgnpa == null)
					bldgnpa = "0.0";
				dtlsAsOnNPA.setValueOfBuilding(Double.parseDouble(bldgnpa));
			}
			if (key.trim().equals("OTHERS")) {
				String othersnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (othersnpa.equals("") || othersnpa == null)
					othersnpa = "0.0";
				dtlsAsOnNPA.setValueOfOthers(Double.parseDouble(othersnpa));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassets = (String) claimForm.getAsOnDtOfNPA(key);
				if (otherassets.equals("") || otherassets == null)
					otherassets = "0.0";
				dtlsAsOnNPA.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassets));
			}
			if (key.trim().equals("networth")) {
				String networthnpa = (String) claimForm.getAsOnDtOfNPA(key);
				if (networthnpa.equals("") || networthnpa == null)
					networthnpa = "0.0";
				dtlsAsOnNPA.setNetworthOfGuarantors(Double
						.parseDouble(networthnpa));
			}
		}
		Map asOnLodgemntDtls = claimForm.getAsOnLodgemntOfCredit();
		Set asOnLodgemntDtlsSet = asOnLodgemntDtls.keySet();
		Iterator asOnLodgemntDtlsIterator = asOnLodgemntDtlsSet.iterator();
		DtlsAsOnLogdementOfClaim dtlsAsOnLodgemnt = new DtlsAsOnLogdementOfClaim();
		while (asOnLodgemntDtlsIterator.hasNext()) {
			String key = (String) asOnLodgemntDtlsIterator.next();
			if (key.trim().equals("reasonReduction"))
				dtlsAsOnLodgemnt.setReasonsForReduction((String) claimForm
						.getAsOnLodgemntOfCredit(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (currentassetslodgemnt.equals("")
						|| currentassetslodgemnt == null)
					currentassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfCurrentAssets(Double
						.parseDouble(currentassetslodgemnt));
			}
			if (key.trim().equals("LAND")) {
				String landlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (landlodgemnt.equals("") || landlodgemnt == null)
					landlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfLand(Double
						.parseDouble(landlodgemnt));
			}
			if (key.trim().equals("MACHINE")) {
				String machinelodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (machinelodgemnt.equals("") || machinelodgemnt == null)
					machinelodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfMachine(Double
						.parseDouble(machinelodgemnt));
			}
			if (key.trim().equals("BUILDING")) {
				String bldglodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (bldglodgemnt.equals("") || bldglodgemnt == null)
					bldglodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfBuilding(Double
						.parseDouble(bldglodgemnt));
			}
			if (key.trim().equals("OTHERS")) {
				String otherslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherslodgemnt.equals("") || otherslodgemnt == null)
					otherslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOthers(Double
						.parseDouble(otherslodgemnt));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassetslodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (otherassetslodgemnt.equals("")
						|| otherassetslodgemnt == null)
					otherassetslodgemnt = "0.0";
				dtlsAsOnLodgemnt.setValueOfOtherFixedMovableAssets(Double
						.parseDouble(otherassetslodgemnt));
			}
			if (key.trim().equals("networth")) {
				String networthlodgemnt = (String) claimForm
						.getAsOnLodgemntOfCredit(key);
				if (networthlodgemnt.equals("") || networthlodgemnt == null)
					networthlodgemnt = "0.0";
				dtlsAsOnLodgemnt.setNetworthOfGuarantors(Double
						.parseDouble(networthlodgemnt));
			}
		}
		Map asOnLodgemntOfSecClmDtls = claimForm
				.getAsOnDateOfLodgemntOfSecondClm();
		Set asOnLodgemntOfSecClmDtlsSet = asOnLodgemntOfSecClmDtls.keySet();
		Iterator asOnLodgemntOfSecClmDtlsIterator = asOnLodgemntOfSecClmDtlsSet
				.iterator();
		DtlsAsOnLogdementOfSecondClaim dtlsAsOnLodgemntOfSecClm = new DtlsAsOnLogdementOfSecondClaim();
		String keyForLand = "reasonReduction#LAND";
		String keyForBldg = "reasonReduction#BUILDING";
		String keyForMachine = "reasonReduction#MACHINE";
		String keyForOFMA = "reasonReduction#OTHER FIXED MOVABLE ASSETS";
		String keyForCurrAssets = "reasonReduction#CURRENT ASSETS";
		String keyForOthers = "reasonReduction#OTHERS";
		String amtRealisedPersonalGuaranteeStr = null;
		while (asOnLodgemntOfSecClmDtlsIterator.hasNext()) {
			String key = (String) asOnLodgemntOfSecClmDtlsIterator.next();
			if (key.trim().equals(keyForLand))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionLand((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals(keyForBldg))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionBuilding((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals(keyForMachine))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionMachine((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals(keyForOFMA))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionFixed((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals(keyForCurrAssets))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionCurrent((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals(keyForOthers))
				dtlsAsOnLodgemntOfSecClm
						.setReasonsForReductionOthers((String) claimForm
								.getAsOnDateOfLodgemntOfSecondClm(key));
			if (key.trim().equals("CURRENT ASSETS")) {
				String currentassetslodgemntOfSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (currentassetslodgemntOfSecClm.equals("")
						|| currentassetslodgemntOfSecClm == null)
					currentassetslodgemntOfSecClm = "0.0";
				String amntRealizedForCurrThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForCurrThruSecurity.equals(""))
					amntRealizedForCurrThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm.setValueOfCurrentAssets(Double
						.parseDouble(currentassetslodgemntOfSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedCurrent(Double
						.parseDouble(amntRealizedForCurrThruSecurity));
			}
			if (key.trim().equals("LAND")) {
				String landlodgemntOfSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (landlodgemntOfSecClm.equals("")
						|| landlodgemntOfSecClm == null)
					landlodgemntOfSecClm = "0.0";
				String amntRealizedForLandThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForLandThruSecurity.equals(""))
					amntRealizedForLandThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm.setValueOfLand(Double
						.parseDouble(landlodgemntOfSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedLand(Double
						.parseDouble(amntRealizedForLandThruSecurity));
			}
			if (key.trim().equals("MACHINE")) {
				String machinelodgemntOfSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (machinelodgemntOfSecClm.equals("")
						|| machinelodgemntOfSecClm == null)
					machinelodgemntOfSecClm = "0.0";
				String amntRealizedForMachineThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForMachineThruSecurity.equals(""))
					amntRealizedForMachineThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm.setValueOfMachine(Double
						.parseDouble(machinelodgemntOfSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedMachine(Double
						.parseDouble(amntRealizedForMachineThruSecurity));
			}
			if (key.trim().equals("BUILDING")) {
				String bldglodgemntSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (bldglodgemntSecClm.equals("") || bldglodgemntSecClm == null)
					bldglodgemntSecClm = "0.0";
				String amntRealizedForBldgThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForBldgThruSecurity.equals(""))
					amntRealizedForBldgThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm.setValueOfBuilding(Double
						.parseDouble(bldglodgemntSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedBuilding(Double
						.parseDouble(amntRealizedForBldgThruSecurity));
			}
			if (key.trim().equals("OTHERS")) {
				String otherslodgemntSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (otherslodgemntSecClm.equals("")
						|| otherslodgemntSecClm == null)
					otherslodgemntSecClm = "0.0";
				String amntRealizedForOthersThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForOthersThruSecurity.equals(""))
					amntRealizedForOthersThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm.setValueOfOthers(Double
						.parseDouble(otherslodgemntSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedOthers(Double
						.parseDouble(amntRealizedForOthersThruSecurity));
			}
			if (key.trim().equals("OTHER FIXED MOVABLE ASSETS")) {
				String otherassetslodgemntSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (otherassetslodgemntSecClm.equals("")
						|| otherassetslodgemntSecClm == null)
					otherassetslodgemntSecClm = "0.0";
				String amntRealizedForOFMAThruSecurity = (String) claimForm
						.getAmntRealizedThruDisposalOfSecurity(key);
				if (amntRealizedForOFMAThruSecurity.equals(""))
					amntRealizedForOFMAThruSecurity = "0.0";
				dtlsAsOnLodgemntOfSecClm
						.setValueOfOtherFixedMovableAssets(Double
								.parseDouble(otherassetslodgemntSecClm));
				dtlsAsOnLodgemntOfSecClm.setAmtRealisedFixed(Double
						.parseDouble(amntRealizedForOFMAThruSecurity));
			}
			if (key.trim().equals("networth")) {
				String networthlodgemntSecClm = (String) claimForm
						.getAsOnDateOfLodgemntOfSecondClm(key);
				if (networthlodgemntSecClm.equals("")
						|| networthlodgemntSecClm == null)
					networthlodgemntSecClm = "0.0";
				dtlsAsOnLodgemntOfSecClm.setNetworthOfGuarantors(Double
						.parseDouble(networthlodgemntSecClm));
			}
		}
		amtRealisedPersonalGuaranteeStr = claimForm
				.getAmntRealizedThruInvocationOfPerGuarantees();
		if (amtRealisedPersonalGuaranteeStr.equals(""))
			amtRealisedPersonalGuaranteeStr = "0.0";
		double amtRealisedPersonalGuarantee = Double
				.parseDouble(amtRealisedPersonalGuaranteeStr);
		dtlsAsOnLodgemntOfSecClm
				.setAmtRealisedPersonalGuarantee(amtRealisedPersonalGuarantee);
		Map clmSummaryDtls = claimForm.getClaimSummaryDetails();
		Set clmSummaryDtlsSet = clmSummaryDtls.keySet();
		Iterator clmSummaryDtlsIterator = clmSummaryDtlsSet.iterator();
		ArrayList claimSummaryDtlsArrayList = new ArrayList();
		ClaimSummaryDtls summaryDtls = null;
		StringTokenizer tokenizer = null;
		String installmentFlag = null;
		String cgpan = null;
		while (clmSummaryDtlsIterator.hasNext()) {
			String key = (String) clmSummaryDtlsIterator.next();
			tokenizer = new StringTokenizer(key, "#");
			boolean isFlagRead = false;
			boolean isCGPANRead = false;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (!isCGPANRead)
					if (!isFlagRead) {
						installmentFlag = token;
						isFlagRead = true;
					} else {
						cgpan = token;
						isCGPANRead = true;
					}
			}
			if (installmentFlag.equals("S")) {
				summaryDtls = new ClaimSummaryDtls();
				summaryDtls.setCgpan(cgpan);
				String amntClaimed = (String) claimForm
						.getClaimSummaryDetails(key);
				if (amntClaimed.equals("") || amntClaimed == null)
					amntClaimed = "0.0";
				summaryDtls.setAmount(Double.parseDouble(amntClaimed));
				if (!claimSummaryDtlsArrayList.contains(summaryDtls))
					claimSummaryDtlsArrayList.add(summaryDtls);
			}
		}
		claimApplication.setMemberId(mId);
		claimApplication.setBorrowerId(bid);
		claimApplication.setDateOfIssueOfRecallNotice(sdf.parse(
				dateOfIssueOfRecallNotice, new ParsePosition(0)));
		LegalProceedingsDetail lpd = new LegalProceedingsDetail();
		lpd.setBorrowerId(bid);
		lpd.setForumRecoveryProceedingsInitiated(forumRecoveryProceedingsInitiated);
		lpd.setSuitCaseRegNumber(caseregnumber);
		lpd.setNameOfForum(nameOfForum);
		lpd.setFilingDate(sdf.parse(legaldate, new ParsePosition(0)));
		lpd.setLocation(location);
		if (amountClaimed != null && !amountClaimed.equals(""))
			lpd.setAmountClaimed(Double.parseDouble(amountClaimed));
		lpd.setCurrentStatusRemarks(currentStatusRemarks);
		lpd.setIsRecoveryProceedingsConcluded(proceedingsConcluded);
		claimApplication
				.setWhetherBorrowerIsWilfulDefaulter(whetherBorrowerIsWilfulDefaulter);
		claimApplication.setDtOfConclusionOfRecoveryProc(sdf.parse(
				dtOfConclusionOfRecoveryProc, new ParsePosition(0)));
		claimApplication
				.setWhetherAccntWrittenOffFromBooksOfMLI(whetherAccntWasWrittenOffBooks);
		claimApplication.setDtOnWhichAccntWrittenOff(sdf.parse(
				dtOnWhichAccntWrittenOff, new ParsePosition(0)));
		claimApplication.setLegalProceedingsDetails(lpd);
		if (dateOfReleaseOfWC != null && !dateOfReleaseOfWC.equals(""))
			claimApplication.setDateOfReleaseOfWC(sdf.parse(dateOfReleaseOfWC,
					new ParsePosition(0)));
		SecurityAndPersonalGuaranteeDtls sapg = new SecurityAndPersonalGuaranteeDtls();
		sapg.setDetailsAsOnDateOfSanction(dtlsAsOnSanctionDt);
		sapg.setDetailsAsOnDateOfNPA(dtlsAsOnNPA);
		sapg.setDetailsAsOnDateOfLodgementOfClaim(dtlsAsOnLodgemnt);
		sapg.setDetailsAsOnDateOfLodgementOfSecondClaim(dtlsAsOnLodgemntOfSecClm);
		claimApplication.setSecurityAndPersonalGuaranteeDtls(sapg);
		Date dtofseekofotsutilformat = sdf.parse(dateOfSeekingOTS,
				new ParsePosition(0));
		claimApplication.setDateOfSeekingOTS(dtofseekofotsutilformat);
		claimApplication.setRecoveryDetails(recoveryDetailsVector);
		claimApplication.setTermCapitalDtls(termCapitalDtls);
		claimApplication.setWorkingCapitalDtls(workingCapitalDtls);
		claimApplication.setClaimSummaryDtls(claimSummaryDtlsArrayList);
		claimForm.setClaimapplication(claimApplication);
		workingCapitalDtls = null;
		claimSummaryDtlsArrayList = null;
		wcKeys = null;
		recoveryDetailsVector = null;
		cgpanKeys = null;
		recoveryKeys = null;
		claimForm.resetDisclaimerPage(mapping, request);
		return mapping.findForward("disclaimerpage");
	}

	public ActionForward saveApplicationForSecInstlmnt(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimApplication claimapplication = claimForm.getClaimapplication();
		ClaimsProcessor processor = new ClaimsProcessor();
		String nameofofficial = claimForm.getNameOfOfficial();
		String designation = claimForm.getDesignationOfOfficial();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String claimsubmitteddt = claimForm.getClaimSubmittedDate();
		String place = claimForm.getPlace();
		String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
		claimapplication.setNameOfOfficial(nameofofficial);
		claimapplication.setDesignationOfOfficial(designation);
		claimapplication.setClaimSubmittedDate(sdf.parse(claimsubmitteddt,
				new ParsePosition(0)));
		claimapplication.setPlace(place);
		claimapplication.setSecondInstallment(true);
		boolean internetUser = true;
		User userInfo = getUserInformation(request);
		String userId = userInfo.getUserId();
		claimapplication.setCreatedModifiedy(userId);
		if ((new StringBuilder()).append(userInfo.getBankId())
				.append(userInfo.getZoneId()).append(userInfo.getBranchId())
				.toString().equals("000000000000")
				&& !userInfo.getUserId().equalsIgnoreCase("DEMOUSER"))
			internetUser = false;
		String clmRefNumber = processor.addClaimApplication(claimapplication,
				null, internetUser);
		claimForm.resetTheSecondClaimApplication(mapping, request);
		claimForm.setNameOfOfficial("");
		claimForm.setDesignationOfOfficial("");
		claimForm.setClaimSubmittedDate("");
		claimForm.setPlace("");
		Log.log(Log.INFO, "ClaimAction", "saveApplicationForSecInstlmnt",
				"Exited");
		request.setAttribute(
				"message",
				(new StringBuilder())
						.append("Application for Second Claim Installment for ")
						.append(borrowerId)
						.append("\n has been saved successfully. Claim Reference Number is : ")
						.append(clmRefNumber).toString());
		return mapping.findForward("detailsSaved");
	}

	public ActionForward saveOTSReqDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		HttpSession session = request.getSession(false);
		Boolean firstValue = (Boolean) session
				.getAttribute("OTSFirstDtlFromRecovery");
		Boolean secondValue = (Boolean) session
				.getAttribute("OTSSecondDtlFromRecovery");
		ClaimsProcessor processor = new ClaimsProcessor();
		String memberId = claimForm.getMemberId();
		String borrowerId = claimForm.getBorrowerID().toUpperCase().trim();
		Hashtable loanDetails = null;
		String reasonForOTS = claimForm.getReasonForOTS();
		String willfulDefaulter = claimForm.getWilfullDefaulter();
		Map propsedAmntPaidByBorrower = claimForm
				.getProposedAmntPaidByBorrower();
		Set propsedAmntPaidByBorrowerSet = propsedAmntPaidByBorrower.keySet();
		Iterator propsedAmntPaidByBorrowerIterator = propsedAmntPaidByBorrowerSet
				.iterator();
		String proposedAmntPaid = null;
		String amountSacrificed = null;
		String osAmntAsOnDate = null;
		Vector cgpandtls = new Vector();
		OTSRequestDetail reqDetail = new OTSRequestDetail();
		reqDetail.setMliId(memberId);
		reqDetail.setCgbid(borrowerId);
		reqDetail.setReasonForOTS(reasonForOTS);
		reqDetail.setWillfulDefaulter(willfulDefaulter);
		while (propsedAmntPaidByBorrowerIterator.hasNext()) {
			String key = (String) propsedAmntPaidByBorrowerIterator.next();
			proposedAmntPaid = (String) claimForm
					.getProposedAmntPaidByBorrower(key);
			if (proposedAmntPaid.equals(""))
				proposedAmntPaid = "0";
			amountSacrificed = (String) claimForm
					.getProposedAmntSacrificed(key);
			if (amountSacrificed.equals(""))
				amountSacrificed = "0";
			osAmntAsOnDate = (String) claimForm.getOsAmntOnDateForOTS(key);
			if (osAmntAsOnDate.equals(""))
				osAmntAsOnDate = "0";
			loanDetails = new Hashtable();
			loanDetails.put("CGPAN", key);
			loanDetails.put("ProposedAmntPaid", proposedAmntPaid);
			loanDetails.put("ProposedAmntSacrificed", amountSacrificed);
			loanDetails.put("OutstandingAmntAsOnDate", osAmntAsOnDate);
			if (!cgpandtls.contains(loanDetails))
				cgpandtls.addElement(loanDetails);
		}
		reqDetail.setLoanDetails(cgpandtls);
		User user = getUserInformation(request);
		String userid = user.getUserId();
		processor.saveOTSDetail(reqDetail, userid);
		claimForm.resetOTSRequestPage(mapping, request);
		loanDetails = null;
		cgpandtls = null;
		boolean val = false;
		if (firstValue != null) {
			val = firstValue.booleanValue();
			if (val) {
				session.setAttribute("mainMenu",
						MenuOptions.getMenu("CP_CLAIM_FOR"));
				session.setAttribute("subMenuItem",
						MenuOptions.getMenu("CP_CLAIM_FOR_FIRST_INSTALLMENT"));
				claimForm.setRecoveryFlag("N");
				firstValue = null;
				return mapping.findForward("firstclaimdetails");
			}
		}
		if (secondValue != null) {
			val = secondValue.booleanValue();
			if (val) {
				session.setAttribute("mainMenu",
						MenuOptions.getMenu("CP_CLAIM_FOR"));
				session.setAttribute("subMenuItem",
						MenuOptions.getMenu("CP_CLAIM_FOR_SECOND_INSTALLMENT"));
				claimForm.setRecoveryFlag("N");
				secondValue = null;
				return mapping.findForward("secondclaimdetails");
			}
		}
		val = false;
		request.setAttribute(
				"message",
				(new StringBuilder()).append("OTS Details for Member :")
						.append(memberId).append(" and Borrower :")
						.append(borrowerId)
						.append("\nhave been saved successfully.").toString());
		return mapping.findForward("success");
	}

	public ActionForward displayOTSProcessInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetOTSProcessPage(mapping, request);
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector otsprocessdetails = processor.getToBeApprovedOTSRequests();
		if (otsprocessdetails != null && otsprocessdetails.size() == 0) {
			request.setAttribute("message",
					"There are no OTS Request(s) to be processed.");
			return mapping.findForward("success");
		} else {
			claimForm.setOtsprocessdetails(otsprocessdetails);
			otsprocessdetails = null;
			return mapping.findForward("otsApproveDisplayPage");
		}
	}

	public ActionForward displayOTSReferenceDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String borrowerId = request.getParameter("BORROWERID");
		String willfulDefaulter = null;
		String otsReason = null;
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector otsReferenceDetails = processor
				.getOTSReferenceDetailsForBorrower(borrowerId);
		for (int i = 0; i < otsReferenceDetails.size(); i++) {
			HashMap temp = (HashMap) otsReferenceDetails.elementAt(i);
			if (temp != null) {
				willfulDefaulter = (String) temp.get("WillfulDefaulter");
				otsReason = (String) temp.get("ReasonForOTS");
				break;
			}
			temp = null;
		}

		claimForm.setBorrowerID(borrowerId);
		claimForm.setReasonForOTS(otsReason);
		claimForm.setWilfullDefaulter(willfulDefaulter);
		claimForm.setOtsReferenceDetails(otsReferenceDetails);
		otsReferenceDetails = null;
		return mapping.findForward("otsReferenceDetails");
	}

	public ActionForward saveOTSProcessDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		Map decisions = claimForm.getDecision();
		Set decisionset = decisions.keySet();
		Iterator decisionIterator = decisionset.iterator();
		OTSApprovalDetail approvaldetail = null;
		String key = null;
		Vector processedOTSDetails = new Vector();
		String mliId = null;
		String bid = null;
		String otsrequestdate = null;
		StringTokenizer tokenizer = null;
		boolean isMliRead = false;
		boolean isBidRead = false;
		boolean isOtsDateRead = false;
		while (decisionIterator.hasNext()) {
			isMliRead = false;
			isBidRead = false;
			isOtsDateRead = false;
			key = (String) decisionIterator.next();
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreTokens();) {
				String thistoken = tokenizer.nextToken();
				if (!isOtsDateRead)
					if (!isBidRead) {
						if (!isMliRead) {
							mliId = thistoken;
							isMliRead = true;
						} else {
							bid = thistoken;
							isBidRead = true;
						}
					} else {
						otsrequestdate = thistoken;
						isOtsDateRead = true;
					}
			}

			String decision = (String) claimForm.getDecision(key);
			String remrks = (String) claimForm.getRemarks(key);
			if (!decision.equals("") && !remrks.equals("")
					|| decision.equals("AP") && remrks.equals("")) {
				approvaldetail = new OTSApprovalDetail();
				approvaldetail.setMliId(mliId);
				approvaldetail.setCgbid(bid);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				approvaldetail.setOtsRequestDate(sdf.parse(otsrequestdate,
						new ParsePosition(0)));
				approvaldetail.setDecision(decision);
				approvaldetail.setRemarks(remrks);
				if (!processedOTSDetails.contains(approvaldetail))
					processedOTSDetails.addElement(approvaldetail);
			}
		}
		User user = getUserInformation(request);
		String userid = user.getUserId();
		if (processedOTSDetails.size() == 0) {
			request.setAttribute("message", "There are no OTS Details to save.");
			return mapping.findForward("success");
		} else {
			processor.saveOTSProcessingResults(processedOTSDetails, userid);
			processedOTSDetails = null;
			claimForm.resetOTSProcessPage(mapping, request);
			request.setAttribute("message",
					"OTS Processing Details have been saved.");
			return mapping.findForward("success");
		}
	}

	public ActionForward displayClaimApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String flagClmRefDtl = claimForm.getClmRefDtlSet();
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
				"*******************************");
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApproval()",
				(new StringBuilder()).append("flagClmRefDtl :")
						.append(flagClmRefDtl).toString());
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
				"*******************************");
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String payAmntNow = null;
		String userRemarks = null;
		String standardRemarks = null;
		double tcServiceFee = 0.0D;
		double wcServiceFee = 0.0D;
		double tcClaimEligibleAmt = 0.0D;
		double wcClaimEligibleAmt = 0.0D;
		double tcFirstInstallment = 0.0D;
		double wcFirstInstallment = 0.0D;
		double totalTCOSAmountAsOnNPA = 0.0D;
		double totalWCOSAmountAsOnNPA = 0.0D;
		double tcrecovery = 0.0D;
		double wcrecovery = 0.0D;
		double tcIssued = 0.0D;
		double wcIssued = 0.0D;
		Administrator admin = new Administrator();
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		User loggedUser = getUserInformation(request);
		String loggedUserId = loggedUser.getUserId();
		userIds.remove(loggedUserId);
		userIds.remove("ADMIN");
		if (userIds.contains("DEMOUSER"))
			userIds.remove("DEMOUSER");
		if (userIds.contains("AUDITOR"))
			userIds.remove("AUDITOR");
		claimForm.setUserIds(userIds);
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String loggedUsr = user.getUserId();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApproval()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApproval()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		Date dateofReceipt = null;
		if (flagClmRefDtl != null && flagClmRefDtl.equals("Y")) {
			clmdtl = claimForm.getClaimdetail();
			if (clmdtl != null) {
				clmRefNum = clmdtl.getClaimRefNum();
				dateofReceipt = claimForm.getDateofReceipt();
				tcIssued = claimForm.getTcIssued();
				wcIssued = claimForm.getWcIssued();
				userRemarks = clmdtl.getComments();
				totalTCOSAmountAsOnNPA = clmdtl.getTotalTCOSAmountAsOnNPA();
				totalWCOSAmountAsOnNPA = clmdtl.getTotalWCOSAmountAsOnNPA();
				tcrecovery = claimForm.getTcrecovery();
				wcrecovery = claimForm.getWcrecovery();
				if (Math.min(tcIssued, totalTCOSAmountAsOnNPA) - tcrecovery <= 500000D)
					tcClaimEligibleAmt = Math
							.round((Math.min(tcIssued, totalTCOSAmountAsOnNPA) - tcrecovery) * 0.80000000000000004D);
				else
					tcClaimEligibleAmt = Math.round((Math.min(tcIssued,
							totalTCOSAmountAsOnNPA) - tcrecovery) * 0.75D);
				if (Math.min(wcIssued, totalWCOSAmountAsOnNPA) - wcrecovery <= 500000D)
					wcClaimEligibleAmt = Math
							.round((Math.min(wcIssued, totalWCOSAmountAsOnNPA) - wcrecovery) * 0.80000000000000004D);
				else
					wcClaimEligibleAmt = Math.round((Math.min(wcIssued,
							totalWCOSAmountAsOnNPA) - wcrecovery) * 0.75D);
				tcFirstInstallment = Math.round(tcClaimEligibleAmt * 0.75D);
				wcFirstInstallment = Math.round(wcClaimEligibleAmt * 0.75D);
				tcServiceFee = claimForm.getAsfDeductableforTC();
				wcServiceFee = claimForm.getAsfDeductableforWC();
				payAmntNow = Double
						.toString((tcFirstInstallment + wcFirstInstallment)
								- tcServiceFee - wcServiceFee);
				clmdtl.setTotalAmtPayNow(payAmntNow);
				CPDAO cpdao = new CPDAO();
				// cpdao.insertClaimProcessDetails(clmRefNum, userRemarks,
				// tcServiceFee, wcServiceFee, tcClaimEligibleAmt,
				// wcClaimEligibleAmt, tcFirstInstallment, wcFirstInstallment,
				// totalTCOSAmountAsOnNPA, totalWCOSAmountAsOnNPA, tcrecovery,
				// wcrecovery, dateofReceipt);
				cpdao.insertClaimProcessDetailsNew(clmRefNum, userRemarks,
						tcServiceFee, wcServiceFee, tcClaimEligibleAmt,
						wcClaimEligibleAmt, tcFirstInstallment,
						wcFirstInstallment, totalTCOSAmountAsOnNPA,
						totalWCOSAmountAsOnNPA, tcrecovery, wcrecovery,
						dateofReceipt, null, 0, 0);
				if (payAmntNow != null && !payAmntNow.equals("")) {
					if (Double.parseDouble(payAmntNow) < 0.0D)
						payAmntNow = "0.0";
				} else {
					payAmntNow = "0.0";
				}
			}
		}
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String thiskey = null;
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor.getClaimProcessingDetails("F");
		if (firstinstllmntclaims != null) {
			for (int i = 0; i < firstinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"##################");
					String memId = clmDtl.getMliId();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("memId :")
									.append(memId).toString());
					String claimrefnumber = clmDtl.getClaimRefNum();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("claimrefnumber :")
									.append(claimrefnumber).toString());
					clmStatus = clmDtl.getClmStatus();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("clmStatus :")
									.append(clmStatus).toString());
					comments = clmDtl.getComments();
					forwardedToUser = clmDtl.getForwaredToUser();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("forwardedToUser :")
									.append(forwardedToUser).toString());
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"###################");
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (clmStatus != null
							&& (clmStatus.equals("FW") || clmStatus
									.equals("HO"))) {
						thiskey = (new StringBuilder()).append("F#")
								.append(memId).append("#")
								.append(claimrefnumber).toString();
						claimForm.setDecision(thiskey, clmStatus);
						claimForm.setRemarks(thiskey, comments);
						Log.log(Log.INFO, "ClaimAction",
								"displayClaimApproval()", (new StringBuilder())
										.append("loggedUsr :")
										.append(loggedUsr).toString());
						if (forwardedToUser != null
								&& !forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							firstinstllmntclaims.remove(i);
							i--;
						}
						if (forwardedToUser != null
								&& forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("thiskey :")
											.append(thiskey).toString());
							Log.log(Log.INFO,
									"ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder())
											.append("forwardedToUser :")
											.append(forwardedToUser).toString());
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							claimForm.setForwardedToIds(thiskey,
									forwardedToUser);
							if (userIds.contains(forwardedToUser))
								claimForm.setUserIds(userIds);
						}
					}
				}
			}

		}
		Vector secinstllmntclaims = processor.getClaimProcessingDetails("S");
		if (secinstllmntclaims != null) {
			for (int i = 0; i < secinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else {
						String memId = clmDtl.getMliId();
						String claimrefnumber = clmDtl.getClaimRefNum();
						String cgclan = clmDtl.getCGCLAN();
						clmStatus = clmDtl.getClmStatus();
						comments = clmDtl.getComments();
						forwardedToUser = clmDtl.getForwaredToUser();
						Log.log(Log.INFO,
								"ClaimAction",
								"displayClaimApproval()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						if (clmStatus != null
								&& (clmStatus.equals("FW") || clmStatus
										.equals("HO"))) {
							thiskey = (new StringBuilder()).append("S#")
									.append(memId).append("#")
									.append(claimrefnumber).append("#")
									.append(cgclan).toString();
							claimForm.setDecision(thiskey, clmStatus);
							claimForm.setRemarks(thiskey, comments);
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("loggedUsr :")
											.append(loggedUsr).toString());
							if (forwardedToUser != null
									&& !forwardedToUser
											.equalsIgnoreCase(loggedUsr)) {
								secinstllmntclaims.remove(i);
								i--;
							}
							if (forwardedToUser != null
									&& forwardedToUser
											.equalsIgnoreCase(loggedUsr))
								claimForm.setForwardedToIds(thiskey,
										forwardedToUser);
						}
					}
				}
			}

		}
		if (firstinstllmntclaims.size() == 0 && secinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to process.");
			return mapping.findForward("success");
		}
		for (int i = 0; i < firstinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) firstinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) firstinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				cd.setComments(userRemarks);
				firstinstllmntclaims.addElement(cd);
			}
		}

		for (int i = 0; i < secinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) secinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) secinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				secinstllmntclaims.addElement(cd);
			}
		}

		clmdtl = null;
		clmRefNum = null;
		payAmntNow = null;
		claimForm.setLimit(outOfLimit);
		claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
		claimForm.setFirstCounter(firstinstllmntclaims.size());
		claimForm.setSecondInstallmentClaims(secinstllmntclaims);
		claimForm.setSecondCounter(secinstllmntclaims.size());
		claimForm.setClmRefDtlSet("N");
		firstinstllmntclaims = null;
		secinstllmntclaims = null;
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()", "Exited");
		return mapping.findForward("displayClaimsApprovalPage");
	}

	public ActionForward displayClaimApprovalMod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ClaimAction", "displayClaimApprovalMod()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String flagClmRefDtl = claimForm.getClmRefDtlSet();
		String fromdate36 = claimForm.getDateOfTheDocument36();
		String toDate37 = claimForm.getDateOfTheDocument37();
		java.sql.Date stDt = null;
		java.sql.Date endDt = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date dt = formatter.parse(fromdate36);
			Date dt1 = formatter.parse(toDate37);
			stDt = new java.sql.Date(dt.getTime());
			endDt = new java.sql.Date(dt1.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.log(4, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		Log.log(4,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("flagClmRefDtl :")
						.append(flagClmRefDtl).toString());
		Log.log(4, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String payAmntNow = null;
		String userRemarks = null;
		String standardRemarks = null;
		String microFlag = null;
		String womenOwned = null;
		String nerFlag = "N";
		double tcServiceFee = 0.0D;
		double wcServiceFee = 0.0D;
		double tcClaimEligibleAmt = 0.0D;
		double wcClaimEligibleAmt = 0.0D;
		double tcFirstInstallment = 0.0D;
		double wcFirstInstallment = 0.0D;
		double totalTCOSAmountAsOnNPA = 0.0D;
		double totalWCOSAmountAsOnNPA = 0.0D;
		double tcrecovery = 0.0D;
		double wcrecovery = 0.0D;
		double tcIssued = 0.0D;
		double wcIssued = 0.0D;
		double tcClaimEligible1 = 0.0D;
		double tcClaimEligible2 = 0.0D;
		double wcClaimEligible1 = 0.0D;
		double wcClaimEligible2 = 0.0D;
		double tcClaimEligible = 0.0D;
		double wcClaimEligible = 0.0D;
		double tcNetOutstanding = 0.0D;
		double wcNetOutstanding = 0.0D;
		double r1 = 0.0D;
		double r2 = 0.0D;
		String schemeName = "";
		String falgforCasesafet = "N";
		Administrator admin = new Administrator();
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		User loggedUser = getUserInformation(request);
		String loggedUserId = loggedUser.getUserId();
		userIds.remove(loggedUserId);
		userIds.remove("ADMIN");
		if (userIds.contains("DEMOUSER"))
			userIds.remove("DEMOUSER");
		if (userIds.contains("AUDITOR"))
			userIds.remove("AUDITOR");
		claimForm.setUserIds(userIds);
		claimForm.setUserId(loggedUserId);
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		Date dateofReceipt = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String loggedUsr = user.getUserId();
		Log.log(4,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(4,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		if (flagClmRefDtl != null && flagClmRefDtl.equals("Y")) {
			clmdtl = claimForm.getClaimdetail();
			if (clmdtl != null) {
				clmRefNum = clmdtl.getClaimRefNum();
				dateofReceipt = claimForm.getDateofReceipt();
				tcIssued = claimForm.getTcIssued();
				wcIssued = claimForm.getWcIssued();
				userRemarks = clmdtl.getComments();
				standardRemarks = clmdtl.getStandardRemarks();
				userRemarks = standardRemarks.concat(userRemarks);
				totalTCOSAmountAsOnNPA = clmdtl.getTotalTCOSAmountAsOnNPA();
				totalWCOSAmountAsOnNPA = clmdtl.getTotalWCOSAmountAsOnNPA();
				tcrecovery = claimForm.getTcrecovery();
				wcrecovery = claimForm.getWcrecovery();
				microFlag = claimForm.getMicroCategory();
				falgforCasesafet = claimForm.getFalgforCasesafet();
				womenOwned = claimForm.getWomenOperated();
				nerFlag = claimForm.getNerFlag();
				schemeName = request.getParameter("schemeName");
				System.out.println((new StringBuilder())
						.append("scheme name from request:").append(schemeName)
						.toString());
				tcNetOutstanding = Math.min(tcIssued, totalTCOSAmountAsOnNPA)
						- tcrecovery;
				wcNetOutstanding = Math.min(wcIssued, totalWCOSAmountAsOnNPA)
						- wcrecovery;

				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
						"dd/MM/yyyy");
				Date maxApprDt = claimForm.getAppApproveDate();
				java.util.Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 16);
				cal.set(Calendar.MONTH, 11);
				cal.set(Calendar.YEAR, 2013);
				java.util.Date circular70Date = cal.getTime();

				String strdt = sdf.format(circular70Date);
				circular70Date = sdf.parse(strdt);
				strdt = sdf.format(maxApprDt);
				maxApprDt = sdf.parse(strdt);

				if ("RSF".equals(schemeName)) {
					tcClaimEligibleAmt = Math.round(tcNetOutstanding * 0.5D);
					wcClaimEligibleAmt = Math.round(wcNetOutstanding * 0.5D);
				} else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("Y")) {
					if (falgforCasesafet.equals("Y"))
						tcClaimEligibleAmt = Math
								.round((Math.min(tcIssued,
										totalTCOSAmountAsOnNPA) - tcrecovery) * 0.84999999999999998D);
					else if (falgforCasesafet.equals("N"))
						tcClaimEligibleAmt = Math
								.round((Math.min(tcIssued,
										totalTCOSAmountAsOnNPA) - tcrecovery) * 0.80000000000000004D);
				} else if (tcIssued + wcIssued <= 5000000D
						&& (womenOwned.equals("F") || nerFlag.equals("Y")))
					tcClaimEligibleAmt = Math
							.round((Math.min(tcIssued, totalTCOSAmountAsOnNPA) - tcrecovery) * 0.80000000000000004D);
				else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("N"))
					tcClaimEligibleAmt = Math.round((Math.min(tcIssued,
							totalTCOSAmountAsOnNPA) - tcrecovery) * 0.75D);
				else
					tcClaimEligibleAmt = Math.round((Math.min(tcIssued,
							totalTCOSAmountAsOnNPA) - tcrecovery) * 0.75D);
				if ("RSF".equals(schemeName))
					wcClaimEligibleAmt = Math.round(wcNetOutstanding * 0.5D);
				else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("Y")) {
					if (falgforCasesafet.equals("Y"))
						wcClaimEligibleAmt = Math
								.round((Math.min(wcIssued,
										totalWCOSAmountAsOnNPA) - wcrecovery) * 0.84999999999999998D);
					else if (falgforCasesafet.equals("N"))
						wcClaimEligibleAmt = Math
								.round((Math.min(wcIssued,
										totalWCOSAmountAsOnNPA) - wcrecovery) * 0.80000000000000004D);
				} else if (tcIssued + wcIssued <= 5000000D
						&& (womenOwned.equals("F") || nerFlag.equals("Y")))
					wcClaimEligibleAmt = Math
							.round((Math.min(wcIssued, totalWCOSAmountAsOnNPA) - wcrecovery) * 0.80000000000000004D);
				else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("N"))
					wcClaimEligibleAmt = Math.round((Math.min(wcIssued,
							totalWCOSAmountAsOnNPA) - wcrecovery) * 0.75D);
				else if ("CGFSI".equals(schemeName)
						&& tcIssued + wcIssued > 5000000D
						&& nerFlag.equals("Y") || "CGFSI".equals(schemeName)
						&& womenOwned.equals("F")
						&& tcIssued + wcIssued > 5000000D
						&& nerFlag.equals("N")) {
					long l = (int) Math
							.round((((tcIssued + wcIssued) - 5000000D) / (tcIssued + wcIssued)) * 1000000000D);
					r2 = (double) l / 1000000000D;
					long l2 = (int) Math
							.round((5000000D / (tcIssued + wcIssued)) * 1000000000D);
					r1 = (double) l2 / 1000000000D;
					tcClaimEligible1 = Math.round(tcNetOutstanding
							* 0.80000000000000004D * r1);
					tcClaimEligible2 = Math.round(tcNetOutstanding * 0.5D * r2);
					tcClaimEligibleAmt = tcClaimEligible1 + tcClaimEligible2;
					wcClaimEligible1 = Math.round(wcNetOutstanding
							* 0.80000000000000004D * r1);
					wcClaimEligible2 = Math.round(wcNetOutstanding * 0.5D * r2);
					wcClaimEligibleAmt = wcClaimEligible1 + wcClaimEligible2;

					if (maxApprDt.compareTo(circular70Date) > 0
							|| maxApprDt.compareTo(circular70Date) == 0) {
						wcClaimEligibleAmt = Math
								.round(wcNetOutstanding * 0.50000000000000004D);
						tcClaimEligibleAmt = Math
								.round(tcNetOutstanding * 0.50000000000000004D);
					}
				} else if ("CGFSI".equals(schemeName) && womenOwned.equals("M")
						&& tcIssued + wcIssued > 5000000D
						&& nerFlag.equals("N")) {
					long l = (int) Math
							.round((((tcIssued + wcIssued) - 5000000D) / (tcIssued + wcIssued)) * 1000000000D);
					r2 = (double) l / 1000000000D;
					long l2 = (int) Math
							.round((5000000D / (tcIssued + wcIssued)) * 1000000000D);
					r1 = (double) l2 / 1000000000D;
					tcClaimEligible1 = Math
							.round(tcNetOutstanding * 0.75D * r1);
					tcClaimEligible2 = Math.round(tcNetOutstanding * 0.5D * r2);
					tcClaimEligibleAmt = tcClaimEligible1 + tcClaimEligible2;
					wcClaimEligible1 = Math.round(wcNetOutstanding * 0.5D * r2);
					wcClaimEligible2 = Math
							.round(wcNetOutstanding * 0.75D * r1);
					wcClaimEligibleAmt = wcClaimEligible1 + wcClaimEligible2;
					if (maxApprDt.compareTo(circular70Date) > 0
							|| maxApprDt.compareTo(circular70Date) == 0) {
						wcClaimEligibleAmt = Math
								.round(wcNetOutstanding * 0.50000000000000004D);
						tcClaimEligibleAmt = Math
								.round(tcNetOutstanding * 0.50000000000000004D);
					}
				} else if (tcIssued + wcIssued > 500000D) {
					tcClaimEligibleAmt = Math.round(tcNetOutstanding * 0.75D);
					wcClaimEligibleAmt = Math.round(wcNetOutstanding * 0.75D);
				} else {
					wcClaimEligibleAmt = Math.round((Math.min(wcIssued,
							totalWCOSAmountAsOnNPA) - wcrecovery) * 0.75D);
				}
				if (tcClaimEligibleAmt < 0.0D)
					tcClaimEligibleAmt = 0.0D;
				if (wcClaimEligibleAmt < 0.0D)
					wcClaimEligibleAmt = 0.0D;
				tcFirstInstallment = Math.round(tcClaimEligibleAmt * 0.75D);
				wcFirstInstallment = Math.round(wcClaimEligibleAmt * 0.75D);
				tcServiceFee = claimForm.getAsfDeductableforTC();
				wcServiceFee = claimForm.getAsfDeductableforWC();
				double tcRF = 0.0D;
				double wcRF = 0.0D;
				String tcRefundFee = claimForm
						.getServiceFeeForOneYearDiffforTC();
				String wcRefundFee = claimForm
						.getServiceFeeForOneYearDiffforWC();
				if (tcRefundFee != null || tcRefundFee != "")
					tcRF = Double.parseDouble(tcRefundFee);
				if (wcRefundFee != null || wcRefundFee != "")
					wcRF = Double.parseDouble(wcRefundFee);
				String isRefundable = null;
				String refundFlag = claimForm.getRefundFlag();
				if (refundFlag != null) {
					isRefundable = "Y";
					payAmntNow = Double.toString(tcFirstInstallment
							+ wcFirstInstallment + tcRF + wcRF);
				} else {
					isRefundable = "N";
					payAmntNow = Double
							.toString((tcFirstInstallment + wcFirstInstallment)
									- tcServiceFee - wcServiceFee);
				}
				clmdtl.setTotalAmtPayNow(payAmntNow);
				CPDAO cpdao = new CPDAO();
				cpdao.insertClaimProcessDetailsNew(clmRefNum, userRemarks,
						tcServiceFee, wcServiceFee, tcClaimEligibleAmt,
						wcClaimEligibleAmt, tcFirstInstallment,
						wcFirstInstallment, totalTCOSAmountAsOnNPA,
						totalWCOSAmountAsOnNPA, tcrecovery, wcrecovery,
						dateofReceipt, isRefundable, tcRF, wcRF);
				if (payAmntNow != null && !payAmntNow.equals("")) {
					if (Double.parseDouble(payAmntNow) < 0.0D)
						payAmntNow = "0.0";
				} else {
					payAmntNow = "0.0";
				}
			}
		}
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String thiskey = null;
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor.getClaimProcessingDetailsMod(
				"F", stDt, endDt);
		if (firstinstllmntclaims != null) {
			for (int i = 0; i < firstinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					Log.log(4, "ClaimAction", "displayClaimApproval()",
							"##################");
					String memId = clmDtl.getMliId();
					Log.log(4,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("memId :")
									.append(memId).toString());
					String claimrefnumber = clmDtl.getClaimRefNum();
					Log.log(4, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("claimrefnumber :")
									.append(claimrefnumber).toString());
					clmStatus = clmDtl.getClmStatus();
					Log.log(4,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("clmStatus :")
									.append(clmStatus).toString());
					comments = clmDtl.getComments();
					forwardedToUser = clmDtl.getForwaredToUser();
					Log.log(4, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("forwardedToUser :")
									.append(forwardedToUser).toString());
					Log.log(4, "ClaimAction", "displayClaimApproval()",
							"###################");
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (clmStatus != null
							&& (clmStatus.equals("FW") || clmStatus
									.equals("HO"))) {
						thiskey = (new StringBuilder()).append("F#")
								.append(memId).append("#")
								.append(claimrefnumber).toString();
						claimForm.setDecision(thiskey, clmStatus);
						claimForm.setRemarks(thiskey, comments);
						Log.log(4, "ClaimAction", "displayClaimApproval()",
								(new StringBuilder()).append("loggedUsr :")
										.append(loggedUsr).toString());
						if (forwardedToUser != null
								&& !forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							firstinstllmntclaims.remove(i);
							i--;
						}
						if (forwardedToUser != null
								&& forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							Log.log(4, "ClaimAction", "displayClaimApproval()",
									"*******************");
							Log.log(4, "ClaimAction", "displayClaimApproval()",
									(new StringBuilder()).append("thiskey :")
											.append(thiskey).toString());
							Log.log(4,
									"ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder())
											.append("forwardedToUser :")
											.append(forwardedToUser).toString());
							Log.log(4, "ClaimAction", "displayClaimApproval()",
									"*******************");
							claimForm.setForwardedToIds(thiskey,
									forwardedToUser);
							if (userIds.contains(forwardedToUser))
								claimForm.setUserIds(userIds);
						}
					}
				}
			}

		}
		Vector secinstllmntclaims = processor.getClaimProcessingDetails("S");
		if (secinstllmntclaims != null) {
			for (int i = 0; i < secinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else {
						String memId = clmDtl.getMliId();
						String claimrefnumber = clmDtl.getClaimRefNum();
						String cgclan = clmDtl.getCGCLAN();
						clmStatus = clmDtl.getClmStatus();
						comments = clmDtl.getComments();
						forwardedToUser = clmDtl.getForwaredToUser();
						Log.log(4,
								"ClaimAction",
								"displayClaimApproval()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						if (clmStatus != null
								&& (clmStatus.equals("FW") || clmStatus
										.equals("HO"))) {
							thiskey = (new StringBuilder()).append("S#")
									.append(memId).append("#")
									.append(claimrefnumber).append("#")
									.append(cgclan).toString();
							claimForm.setDecision(thiskey, clmStatus);
							claimForm.setRemarks(thiskey, comments);
							Log.log(4, "ClaimAction", "displayClaimApproval()",
									(new StringBuilder()).append("loggedUsr :")
											.append(loggedUsr).toString());
							if (forwardedToUser != null
									&& !forwardedToUser
											.equalsIgnoreCase(loggedUsr)) {
								secinstllmntclaims.remove(i);
								i--;
							}
							if (forwardedToUser != null
									&& forwardedToUser
											.equalsIgnoreCase(loggedUsr))
								claimForm.setForwardedToIds(thiskey,
										forwardedToUser);
						}
					}
				}
			}

		}
		if (firstinstllmntclaims.size() == 0 && secinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to process.");
			return mapping.findForward("success");
		}
		for (int i = 0; i < firstinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) firstinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) firstinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				cd.setComments(standardRemarks.concat(userRemarks));
				firstinstllmntclaims.addElement(cd);
			}
		}

		for (int i = 0; i < secinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) secinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) secinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				secinstllmntclaims.addElement(cd);
			}
		}

		clmdtl = null;
		clmRefNum = null;
		payAmntNow = null;
		claimForm.setRefundFlag(null);
		claimForm.setLimit(outOfLimit);
		claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
		claimForm.setFirstCounter(firstinstllmntclaims.size());
		claimForm.setSecondInstallmentClaims(secinstllmntclaims);
		claimForm.setSecondCounter(secinstllmntclaims.size());
		claimForm.setClmRefDtlSet("N");
		firstinstllmntclaims = null;
		secinstllmntclaims = null;
		Log.log(4, "ClaimAction", "displayClaimApproval()", "Exited");
		return mapping.findForward("displayClaimsApprovalPage");
	}

	public ActionForward displayClaimApprovalNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalNew()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String payAmntNow = null;
		Administrator admin = new Administrator();
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String bankName = request.getParameter("Link");
		bankName = bankName.replaceAll("PATH", "&");
		String loggedUsr = user.getUserId();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalNew()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalNew()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor.getClaimApprovalDetails(
				loggedUsr, bankName);
		if (firstinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to Approve.");
			return mapping.findForward("success");
		} else {
			claimForm.setLimit(outOfLimit);
			claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
			claimForm.setFirstCounter(firstinstllmntclaims.size());
			claimForm.setClmRefDtlSet("N");
			firstinstllmntclaims = null;
			Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalNew()",
					"Exited");
			return mapping.findForward("displayClaimApprovalNew");
		}
	}

	public ActionForward displayClaimRefNumberDtls(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Vector firstClms = claimForm.getFirstInstallmentClaims();
		Vector secClms = claimForm.getSecondInstallmentClaims();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		double clmEligibleAmnt = 0.0D;
		if (firstClms != null) {
			for (int i = 0; i < firstClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		if (secClms != null) {
			for (int i = 0; i < secClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		ClaimsProcessor processor = new ClaimsProcessor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ClaimDetail claimdetail = processor
				.getDetailsForClaimRefNumber(claimRefNumber);
		claimdetail.setEligibleClaimAmt(clmEligibleAmnt);
		claimdetail.setClaimRefNum(claimRefNumber);
		Date dtOfRecallNotice = claimdetail.getDateOfIssueOfRecallNotice();
		if (dtOfRecallNotice != null)
			claimdetail.setDateOfIssueOfRecallNoticeStr(sdf
					.format(dtOfRecallNotice));
		Date npaDate = claimdetail.getNpaDate();
		if (npaDate != null)
			claimdetail.setNpaDateStr(sdf.format(npaDate));
		Date dtOfNPAReportedToCGTSI = claimdetail.getDtOfNPAReportedToCGTSI();
		if (dtOfNPAReportedToCGTSI != null)
			claimdetail.setDtOfNPAReportedToCGTSIStr(sdf
					.format(dtOfNPAReportedToCGTSI));
		double amtClaimed = claimdetail.getAppliedClaimAmt();
		clmEligibleAmnt = claimdetail.getEligibleClaimAmt();
		String amntClaimed = Double.toString(amtClaimed);
		claimForm.setAmountclaimed(amntClaimed);
		claimForm.setDanSummaryReportDetails(claimdetail
				.getDanSummaryReportDetails());
		claimForm.setClaimdetail(claimdetail);
		claimForm.setTcInterestChargeForThisBorrower("");
		claimForm.setWcOtherChargesAsOnNPA("");
		claimForm.setTcPrinRecoveriesAfterNPA("");
		claimForm.setTcInterestChargesRecovAfterNPA("");
		claimForm.setWcPrincipalRecoveAfterNPA("");
		claimForm.setWcothercgrgsRecAfterNPA("");
		return mapping.findForward("displayClaimRefNumDtlsPage");
	}

	public ActionForward displayClaimRefNumberDtlsMod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Vector firstClms = claimForm.getFirstInstallmentClaims();
		Vector secClms = claimForm.getSecondInstallmentClaims();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		double clmEligibleAmnt = 0.0D;
		User user = getUserInformation(request);
		String loggedUserId = user.getUserId();
		claimForm.setUserId(loggedUserId);
		String isClaimProceedings = claimForm.getIsClaimProceedings();
		if (firstClms != null) {
			for (int i = 0; i < firstClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		if (secClms != null) {
			for (int i = 0; i < secClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		ClaimsProcessor processor = new ClaimsProcessor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ClaimDetail claimdetail = processor
				.getDetailsForClaimRefNumber(claimRefNumber);

		claimdetail.setEligibleClaimAmt(clmEligibleAmnt);
		claimdetail.setClaimRefNum(claimRefNumber);
		Date dtOfRecallNotice = claimdetail.getDateOfIssueOfRecallNotice();
		if (dtOfRecallNotice != null)
			claimdetail.setDateOfIssueOfRecallNoticeStr(sdf
					.format(dtOfRecallNotice));
		Date npaDate = claimdetail.getNpaDate();
		if (npaDate != null)
			claimdetail.setNpaDateStr(sdf.format(npaDate));
		Date dtOfNPAReportedToCGTSI = claimdetail.getDtOfNPAReportedToCGTSI();
		if (dtOfNPAReportedToCGTSI != null)
			claimdetail.setDtOfNPAReportedToCGTSIStr(sdf
					.format(dtOfNPAReportedToCGTSI));
		double amtClaimed = claimdetail.getAppliedClaimAmt();
		clmEligibleAmnt = claimdetail.getEligibleClaimAmt();
		String amntClaimed = Double.toString(amtClaimed);
		claimForm.setAmountclaimed(amntClaimed);
		claimForm.setDanSummaryReportDetails(claimdetail
				.getDanSummaryReportDetails());
		claimForm.setClaimdetail(claimdetail);
		claimForm.setTcInterestChargeForThisBorrower("");
		claimForm.setWcOtherChargesAsOnNPA("");
		claimForm.setTcPrinRecoveriesAfterNPA("");
		claimForm.setTcInterestChargesRecovAfterNPA("");
		claimForm.setWcPrincipalRecoveAfterNPA("");
		claimForm.setWcothercgrgsRecAfterNPA("");
		claimForm.setDateofReceipt(null);
		claimForm.setTcrecovery(0.0D);
		claimForm.setWcrecovery(0.0D);
		claimForm.setTotalRecovery(0.0D);
		Vector arrylist = claimdetail.getCgpanDetails();
		String falgforCasesafet = "N";
		Connection connection = DBConnection.getConnection();
		for (Iterator interator = arrylist.iterator(); interator.hasNext();)
			try {
				Statement str = connection.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				HashMap hashmap = (HashMap) interator.next();
				String cgpanfordate = (String) hashmap.get("CGPAN");
				String queryNPAOutStanfAMt1 = (new StringBuilder())
						.append("SELECT TRM_AMOUNT_SANCTIONED_DT A, NULL  B FROM TERM_LOAN_DETAIL WHERE CGPAN='")
						.append(cgpanfordate)
						.append("' UNION ALL SELECT WCP_FB_LIMIT_SANCTIONED_DT A,WCP_NFB_LIMIT_SANCTIONED_DT B FROM WORKING_CAPITAL_DETAIL WHERE CGPAN='")
						.append(cgpanfordate).append("'").toString();
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DATE);
				int year = calendar.get(Calendar.YEAR);
				day = 2;
				month = 0;
				year = 2009;
				calendar.set(Calendar.MONTH, month);
				calendar.set(Calendar.DATE, day);
				calendar.set(Calendar.YEAR, year);
				Date caesesafter2009 = calendar.getTime();
				claimForm.setFalgforCasesafet(falgforCasesafet);
				ResultSet rs1 = str.executeQuery(queryNPAOutStanfAMt1);
				while (rs1.next()) {
					Date firstDate = null;
					firstDate = rs1.getDate(1);
					Date secondDate = null;
					secondDate = rs1.getDate(2);
					try {
						if (firstDate.equals(null))
							continue;
						if (firstDate.after(caesesafter2009)) {
							falgforCasesafet = "Y";
							claimForm.setFalgforCasesafet(falgforCasesafet);
							break;
						}
						if (!secondDate.equals(null)
								&& secondDate.after(caesesafter2009)) {
							falgforCasesafet = "Y";
							claimForm.setFalgforCasesafet(falgforCasesafet);
							break;
						}
						claimForm.setFalgforCasesafet(falgforCasesafet);
					} catch (Exception we) {
						we.getMessage();
					}
				}
				rs1.close();
				rs1 = null;
				str.close();
				str = null;
			} catch (Exception e) {
				e.getMessage();
			} finally {
				DBConnection.freeConnection(connection);
			}

		return mapping.findForward("displayClaimRefNumDtlsPage");
	}

	public ActionForward displayClaimProcessingInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimProcessingInput",
				"Entered");
		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		String startDate = dateFormat.format(prevDate);
		String endDate = dateFormat.format(date);
		claimForm.setDateOfTheDocument36(startDate);
		claimForm.setDateOfTheDocument37(endDate);
		Log.log(Log.INFO, "ClaimAction", "displayClaimProcessingInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward displayClaimRefNumberDtlsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Vector firstClms = claimForm.getFirstInstallmentClaims();
		Vector secClms = claimForm.getSecondInstallmentClaims();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		double clmEligibleAmnt = 0.0D;
		if (firstClms != null) {
			for (int i = 0; i < firstClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		if (secClms != null) {
			for (int i = 0; i < secClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		ClaimsProcessor processor = new ClaimsProcessor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ClaimDetail claimdetail = processor
				.getDetailsForClaimRefNumber(claimRefNumber);
		claimdetail.setEligibleClaimAmt(clmEligibleAmnt);
		claimdetail.setClaimRefNum(claimRefNumber);
		Date dtOfRecallNotice = claimdetail.getDateOfIssueOfRecallNotice();
		if (dtOfRecallNotice != null)
			claimdetail.setDateOfIssueOfRecallNoticeStr(sdf
					.format(dtOfRecallNotice));
		Date npaDate = claimdetail.getNpaDate();
		if (npaDate != null)
			claimdetail.setNpaDateStr(sdf.format(npaDate));
		Date dtOfNPAReportedToCGTSI = claimdetail.getDtOfNPAReportedToCGTSI();
		if (dtOfNPAReportedToCGTSI != null)
			claimdetail.setDtOfNPAReportedToCGTSIStr(sdf
					.format(dtOfNPAReportedToCGTSI));
		double amtClaimed = claimdetail.getAppliedClaimAmt();
		clmEligibleAmnt = claimdetail.getEligibleClaimAmt();
		String amntClaimed = Double.toString(amtClaimed);
		claimForm.setAmountclaimed(amntClaimed);
		claimForm.setDanSummaryReportDetails(claimdetail
				.getDanSummaryReportDetails());
		claimForm.setClaimdetail(claimdetail);
		claimForm.setTcInterestChargeForThisBorrower("");
		claimForm.setWcOtherChargesAsOnNPA("");
		claimForm.setTcPrinRecoveriesAfterNPA("");
		claimForm.setTcInterestChargesRecovAfterNPA("");
		claimForm.setWcPrincipalRecoveAfterNPA("");
		claimForm.setWcothercgrgsRecAfterNPA("");
		return mapping.findForward("displayClaimRefNumberDtlsNew");
	}

	public ActionForward saveTcClaimProcessDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "saveTcClaimProcessDetailsNew",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		CPDAO cpDAO = new CPDAO();
		User user = getUserInformation(request);
		String userId = user.getUserId();
		Date systemDate = new Date();
		int tempCount = 0;
		boolean insertedFlag = false;
		Map claimRefSetCases = claimForm.getClaimRefSet();
		Map q1Cases = claimForm.getQ1Flags();
		Map q2Cases = claimForm.getQ2Flags();
		Map q3Cases = claimForm.getQ3Flags();
		Map q4Cases = claimForm.getQ4Flags();
		Map q5Cases = claimForm.getQ5Flags();
		Map q6Cases = claimForm.getQ6Flags();
		Map q7Cases = claimForm.getQ7Flags();
		Map q8Cases = claimForm.getQ8Flags();
		Map q9Cases = claimForm.getQ9Flags();
		Map q10Cases = claimForm.getQ10Flags();
		Map q11Cases = claimForm.getQ11Flags();
		Map q12Cases = claimForm.getQ12Flags();
		Map q13Cases = claimForm.getQ13Flags();
		Map q14Cases = claimForm.getQ14Flags();
		Map ltrRefNoSet = claimForm.getLtrRefNoSet();
		Map ltrDtSet = claimForm.getLtrDtSet();
		Set claimRefSet = claimRefSetCases.keySet();
		Iterator claimRefSetIterator = claimRefSet.iterator();
		Set q1Set = q1Cases.keySet();
		Iterator q1Iterator = q1Set.iterator();
		while (claimRefSetIterator.hasNext()) {
			String claimRefNo = (String) claimRefSetIterator.next();
			String q1 = (String) q1Cases.get(claimRefNo);
			String q2 = (String) q2Cases.get(claimRefNo);
			String q3 = (String) q3Cases.get(claimRefNo);
			String q4 = (String) q4Cases.get(claimRefNo);
			String q5 = (String) q5Cases.get(claimRefNo);
			String q6 = (String) q6Cases.get(claimRefNo);
			String q7 = (String) q7Cases.get(claimRefNo);
			String q8 = (String) q8Cases.get(claimRefNo);
			String q9 = (String) q9Cases.get(claimRefNo);
			String q10 = (String) q10Cases.get(claimRefNo);
			String q11 = (String) q11Cases.get(claimRefNo);
			String q12 = (String) q12Cases.get(claimRefNo);
			String q13 = (String) q13Cases.get(claimRefNo);
			String q14 = (String) q14Cases.get(claimRefNo);
			String ltrRefNo = (String) ltrRefNoSet.get(claimRefNo);
			String ltrDate = (String) ltrDtSet.get(claimRefNo);
			tempCount += cpDAO.insertClaimTCProcessingDetails(claimRefNo, q1,
					q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14,
					userId, systemDate, ltrRefNo, ltrDate);
			insertedFlag = true;
		}
		if (!insertedFlag)
			throw new MessageException("No Queries Sent.");
		Vector claimDetails = new Vector();
		Vector aVector = new Vector();
		Vector tVector = new Vector();
		claimDetails = claimForm.getClaimProcessingDetails();
		if (claimDetails.size() > 0) {
			HashMap details = processor
					.saveClaimProcessingResults(claimDetails);
			if (details != null && details.size() > 0) {
				Set detailsset = details.keySet();
				for (Iterator detailsiterator = detailsset.iterator(); detailsiterator
						.hasNext();) {
					String key = (String) detailsiterator.next();
					String cgclan = (String) details.get(key);
					for (int i = 0; i < aVector.size(); i++) {
						HashMap map = (HashMap) aVector.elementAt(i);
						if (map != null) {
							String flag = (String) map.get("FLAG");
							String clmrefnum = (String) map
									.get("ClaimRefNumber");
							if (flag.equals("APF") && key.equals(clmrefnum)) {
								map = (HashMap) aVector.remove(i);
								map.put("cgclan", cgclan);
								aVector.add(i, map);
							}
						}
					}

				}

			}
		}
		claimDetails = null;
		aVector = null;
		tVector = null;
		claimForm.resetTCClaimsProcessPage(mapping, request);
		return mapping.findForward("summaryPage");
	}

	public ActionForward saveClaimProcessDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "saveClaimProcessDetails", "Entered");
		User user = getUserInformation(request);
		String userid = user.getUserId();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		ClaimDetail claimdetail = null;
		Map approvedClaimAmounts = claimForm.getApprovedClaimAmount();
		Log.log(Log.INFO, "ClaimAction", "saveClaimProcessDetails",
				(new StringBuilder()).append("Printing Approved Claim Amnts :")
						.append(approvedClaimAmounts).toString());
		Set approvedClaimAmountsSet = approvedClaimAmounts.keySet();
		Iterator approvedClaimAmountIterator = approvedClaimAmountsSet
				.iterator();
		StringTokenizer tokenizer = null;
		String memberId = null;
		String claimRefNumber = null;
		String cgtsidecision = null;
		String installmentFlag = null;
		double approvedclaimamount = 0.0D;
		String cgtsicomments = null;
		Vector claimDetails = new Vector();
		Vector claimTcProcessingDetails = new Vector();
		HashMap claimsummaries = new HashMap();
		HashMap individualDtl = null;
		Vector aVector = new Vector();
		Vector rVector = new Vector();
		Vector hVector = new Vector();
		Vector fVector = new Vector();
		Vector tVector = new Vector();
		Vector trVector = new Vector();
		Vector wdVector = new Vector();
		String hashmapKey = null;
		String cgclanS = null;
		String firstChar = null;
		int count = 0;
		String forwardedToUser = null;
		while (approvedClaimAmountIterator.hasNext()) {
			claimdetail = new ClaimDetail();
			String key = (String) approvedClaimAmountIterator.next();
			String token = null;
			if (key.length() != 0) {
				firstChar = key.substring(0, 1);
				if (firstChar.equals("F")) {
					tokenizer = new StringTokenizer(key, "#");
					boolean isMemIdRead = false;
					boolean isClaimRefNumRead = false;
					boolean isInstallmentFlagRead = false;
					while (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						if (!isClaimRefNumRead)
							if (!isMemIdRead) {
								if (!isInstallmentFlagRead) {
									installmentFlag = token;
									isInstallmentFlagRead = true;
								} else {
									memberId = token;
									isMemIdRead = true;
								}
							} else {
								claimRefNumber = token;
								isClaimRefNumRead = true;
							}
					}
				} else if (firstChar.equals("S")) {
					tokenizer = new StringTokenizer(key, "#");
					boolean isMemIdRead = false;
					boolean isClaimRefNumRead = false;
					boolean isInstallmentFlagRead = false;
					boolean isCGCLANRead = false;
					while (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						if (!isCGCLANRead)
							if (!isClaimRefNumRead) {
								if (!isMemIdRead) {
									if (!isInstallmentFlagRead) {
										installmentFlag = token;
										isInstallmentFlagRead = true;
									} else {
										memberId = token;
										isMemIdRead = true;
									}
								} else {
									claimRefNumber = token;
									isClaimRefNumRead = true;
								}
							} else {
								cgclanS = token;
								isCGCLANRead = true;
							}
					}
				}
				cgtsidecision = (String) claimForm.getDecision(key);
				String apprvdamnt = (String) claimForm
						.getApprovedClaimAmount(key);
				if (apprvdamnt != null && !apprvdamnt.equals(""))
					approvedclaimamount = Double.parseDouble(apprvdamnt);
				cgtsicomments = (String) claimForm.getRemarks(key);
				claimdetail.setMliId(memberId);
				claimdetail.setClaimRefNum(claimRefNumber);
				claimdetail.setDecision(cgtsidecision);
				claimdetail.setApprovedClaimAmount(approvedclaimamount);
				claimdetail.setComments(cgtsicomments);
				if (cgtsidecision != null && cgtsidecision.equals("AP")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "APF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!aVector.contains(individualDtl))
							aVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCGCLAN(cgclanS);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "APS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						individualDtl.put("cgclan", cgclanS);
						if (!aVector.contains(individualDtl))
							aVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("TC")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TCF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!tVector.contains(individualDtl))
							tVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TCS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!tVector.contains(individualDtl))
							tVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimTcProcessingDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("TR")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TRF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!trVector.contains(individualDtl))
							trVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TRS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!trVector.contains(individualDtl))
							trVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("WD")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "WDF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!wdVector.contains(individualDtl))
							wdVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "WDS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!wdVector.contains(individualDtl))
							wdVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("RE")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "REF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rVector.contains(individualDtl))
							rVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "RES";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rVector.contains(individualDtl))
							rVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("HO")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "HOF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!hVector.contains(individualDtl))
							hVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "HOS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!hVector.contains(individualDtl))
							hVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("FW")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						forwardedToUser = (String) claimForm
								.getForwardedToIds(key);
						Log.log(Log.INFO,
								"ClaimAction",
								"saveClaimProcessDetails()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "FWF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!fVector.contains(individualDtl))
							fVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						forwardedToUser = (String) claimForm
								.getForwardedToIds(key);
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "FWS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!fVector.contains(individualDtl))
							fVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				individualDtl = null;
				count++;
			}
		}
		if (aVector.size() == 0 && rVector.size() == 0 && hVector.size() == 0
				&& fVector.size() == 0 && tVector.size() == 0
				&& trVector.size() == 0 && wdVector.size() == 0) {
			request.setAttribute("message", "There are no Details to save.");
			return mapping.findForward("success");
		}
		claimForm.setClaimProcessingDetails(claimTcProcessingDetails);
		claimForm.setAVector(aVector);
		claimForm.setTVector(tVector);
		if (claimDetails.size() > 0) {
			HashMap details = processor
					.saveClaimProcessingResults(claimDetails);
			if (details != null && details.size() > 0) {
				Set detailsset = details.keySet();
				for (Iterator detailsiterator = detailsset.iterator(); detailsiterator
						.hasNext();) {
					String key = (String) detailsiterator.next();
					String cgclan = (String) details.get(key);
					for (int i = 0; i < aVector.size(); i++) {
						HashMap map = (HashMap) aVector.elementAt(i);
						if (map != null) {
							String flag = (String) map.get("FLAG");
							String clmrefnum = (String) map
									.get("ClaimRefNumber");
							if (flag.equals("APF") && key.equals(clmrefnum)) {
								map = (HashMap) aVector.remove(i);
								map.put("cgclan", cgclan);
								aVector.add(i, map);
							}
						}
					}

				}

			}
		}
		claimForm.setApprvdVectorSize(aVector.size());
		claimForm.setRejectdVectorSize(rVector.size());
		claimForm.setHeldVectorSize(hVector.size());
		claimForm.setForwardedVectorSize(fVector.size());
		claimForm.setTempclosedVectorSize(tVector.size());
		claimForm.setTemprejectedVectorSize(trVector.size());
		claimForm.setClaimwithdrawnVectorSize(wdVector.size());
		claimsummaries.put("AP", aVector);
		claimsummaries.put("RE", rVector);
		claimsummaries.put("HO", hVector);
		claimsummaries.put("FW", fVector);
		claimsummaries.put("TC", tVector);
		claimsummaries.put("TR", trVector);
		claimsummaries.put("WD", wdVector);
		claimForm.setClaimSummaries(claimsummaries);
		int test = 0;
		if (tVector.size() > 0)
			test = 1;
		claimsummaries = null;
		claimDetails = null;
		aVector = null;
		rVector = null;
		hVector = null;
		fVector = null;
		tVector = null;
		trVector = null;
		wdVector = null;
		claimForm.resetClaimsProcessPage(mapping, request);
		if (test == 0)
			return mapping.findForward("summaryPage");
		else
			return mapping.findForward("tcsummaryPage");
	}

	public ActionForward saveClaimApproval(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ClaimAction", "saveClaimApproval", "Entered");
		User user = getUserInformation(request);
		String userid = user.getUserId();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		ClaimDetail claimdetail = null;
		Map approveClaims = claimForm.getApproveClaims();
		Set approveClaimsSet = approveClaims.keySet();
		Iterator approveClaimsSetIterator = approveClaimsSet.iterator();
		Map approvedClaimAmounts = claimForm.getApprovedClaimAmount();
		Log.log(4, "ClaimAction", "saveClaimProcessDetailsNew",
				(new StringBuilder()).append("Printing Approved Claim Amnts :")
						.append(approvedClaimAmounts).toString());
		Map decisionmap = claimForm.getDecision();
		Set approvedClaimAmountsSet = approvedClaimAmounts.keySet();
		Iterator approvedClaimAmountIterator = approvedClaimAmountsSet
				.iterator();
		StringTokenizer tokenizer = null;
		String memberId = null;
		String claimRefNumber = null;
		String cgtsidecision = null;
		String installmentFlag = null;
		double approvedclaimamount = 0.0D;
		String cgtsicomments = null;
		Vector claimDetails = new Vector();
		HashMap claimsummaries = new HashMap();
		HashMap individualDtl = null;
		Vector aVector = new Vector();
		Vector rVector = new Vector();
		Vector tVector = new Vector();
		Vector hVector = new Vector();
		Vector fVector = new Vector();
		Vector nVector = new Vector();
		Vector rtVector = new Vector();
		String hashmapKey = null;
		String cgclanS = null;
		String firstChar = null;
		int count = 0;
		String forwardedToUser = null;
		while (approvedClaimAmountIterator.hasNext()) {
			claimdetail = new ClaimDetail();
			String key = (String) approvedClaimAmountIterator.next();
			String token = null;
			if (key.length() != 0) {
				firstChar = key.substring(0, 1);
				if (firstChar.equals("F")) {
					tokenizer = new StringTokenizer(key, "#");
					boolean isMemIdRead = false;
					boolean isClaimRefNumRead = false;
					boolean isInstallmentFlagRead = false;
					while (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						if (!isClaimRefNumRead)
							if (!isMemIdRead) {
								if (!isInstallmentFlagRead) {
									installmentFlag = token;
									isInstallmentFlagRead = true;
								} else {
									memberId = token;
									isMemIdRead = true;
								}
							} else {
								claimRefNumber = token;
								isClaimRefNumRead = true;
							}
					}
				} else if (firstChar.equals("S")) {
					tokenizer = new StringTokenizer(key, "#");
					boolean isMemIdRead = false;
					boolean isClaimRefNumRead = false;
					boolean isInstallmentFlagRead = false;
					boolean isCGCLANRead = false;
					while (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						if (!isCGCLANRead)
							if (!isClaimRefNumRead) {
								if (!isMemIdRead) {
									if (!isInstallmentFlagRead) {
										installmentFlag = token;
										isInstallmentFlagRead = true;
									} else {
										memberId = token;
										isMemIdRead = true;
									}
								} else {
									claimRefNumber = token;
									isClaimRefNumRead = true;
								}
							} else {
								cgclanS = token;
								isCGCLANRead = true;
							}
					}
				}
				Log.log(4, "ClaimAction", "saveClaimProcessDetailsNew",
						(new StringBuilder()).append("cgtsidecision key:")
								.append(key).toString());
				cgtsidecision = (String) claimForm.getDecision(key);
				Log.log(4,
						"ClaimAction",
						"saveClaimProcessDetailsNew",
						(new StringBuilder()).append("cgtsidecision :")
								.append(cgtsidecision).toString());
				String apprvdamnt = (String) claimForm
						.getApprovedClaimAmount(key);
				if (apprvdamnt != null && !apprvdamnt.equals(""))
					approvedclaimamount = Double.parseDouble(apprvdamnt);
				cgtsicomments = (String) claimForm.getRemarks(key);
				String recommendationData = "";
				recommendationData = (String) claimForm
						.getRecommendationData(key);
				String data = "";
				if (recommendationData != null
						&& !recommendationData.equals(""))
					data = recommendationData.replaceAll("&", "-");
				cgtsicomments = (new StringBuilder()).append(cgtsicomments)
						.append(data).toString();

				int index = key.indexOf("C");
				String tempkey = "F#";
				tempkey = (new StringBuilder()).append(tempkey)
						.append(key.substring(index)).toString();
				String rejectionReason = "";
				rejectionReason = (String) claimForm.getReasonData(tempkey);

				claimdetail.setMliId(memberId);
				claimdetail.setClaimRefNum(claimRefNumber);
				claimdetail.setDecision(cgtsidecision);
				claimdetail.setApprovedClaimAmount(approvedclaimamount);
				claimdetail.setComments(cgtsicomments);
				// System.out.println("cgtsicomments"+cgtsicomments);

				if (cgtsidecision.equals("RT")) {
					claimdetail.setRejectionReason(rejectionReason);
				} else {
					claimdetail.setRejectionReason("");
				}
				if (cgtsidecision != null && cgtsidecision.equals("AP")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "APF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!aVector.contains(individualDtl))
							aVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCGCLAN(cgclanS);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "APS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						individualDtl.put("cgclan", cgclanS);
						if (!aVector.contains(individualDtl))
							aVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				} else if (cgtsidecision != null && cgtsidecision.equals("TC")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TCF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!tVector.contains(individualDtl))
							tVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "TCS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!tVector.contains(individualDtl))
							tVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				} else if (cgtsidecision != null && cgtsidecision.equals("RE")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "REF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rVector.contains(individualDtl))
							rVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "RES";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rVector.contains(individualDtl))
							rVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				if (cgtsidecision != null && cgtsidecision.equals("RT")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "RTF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rtVector.contains(individualDtl))
							rtVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "RTS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!rtVector.contains(individualDtl))
							rtVector.addElement(individualDtl);
					}

					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				} else if (cgtsidecision != null && cgtsidecision.equals("HO")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "HOF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!hVector.contains(individualDtl))
							hVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "HOS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!hVector.contains(individualDtl))
							hVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				} else if (cgtsidecision != null && cgtsidecision.equals("NE")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "NEF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!nVector.contains(individualDtl))
							nVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "NES";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!nVector.contains(individualDtl))
							nVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				} else if (cgtsidecision != null && cgtsidecision.equals("FW")) {
					if (installmentFlag.equals("F")) {
						claimdetail.setWhichInstallemnt("F");
						forwardedToUser = (String) claimForm
								.getForwardedToIds(key);
						Log.log(4,
								"ClaimAction",
								"saveClaimProcessDetailsNew()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "FWF";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!fVector.contains(individualDtl))
							fVector.addElement(individualDtl);
					} else if (installmentFlag.equals("S")) {
						claimdetail.setWhichInstallemnt("S");
						forwardedToUser = (String) claimForm
								.getForwardedToIds(key);
						Log.log(2,
								"ClaimAction",
								"saveClaimProcessDetailsNew()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setCreatedModifiedBy(userid);
						individualDtl = new HashMap();
						hashmapKey = "FWS";
						individualDtl.put("FLAG", hashmapKey);
						individualDtl.put("ClaimRefNumber", claimRefNumber);
						if (!fVector.contains(individualDtl))
							fVector.addElement(individualDtl);
					}
					if (!claimDetails.contains(claimdetail))
						claimDetails.addElement(claimdetail);
				}
				individualDtl = null;
				count++;
			}
		}
		if (aVector.size() == 0 && rVector.size() == 0 && hVector.size() == 0
				&& fVector.size() == 0 && tVector.size() == 0
				&& nVector.size() == 0 && rtVector.size() == 0) {
			request.setAttribute("message",
					"There are no claim details to save.");
			return mapping.findForward("success");
		}
		if (claimDetails.size() > 0) {
			HashMap details = processor
					.saveClaimProcessingResults(claimDetails);
			if (details != null && details.size() > 0) {
				Set detailsset = details.keySet();
				for (Iterator detailsiterator = detailsset.iterator(); detailsiterator
						.hasNext();) {
					String key = (String) detailsiterator.next();
					String cgclan = (String) details.get(key);
					for (int i = 0; i < aVector.size(); i++) {
						HashMap map = (HashMap) aVector.elementAt(i);
						if (map != null) {
							String flag = (String) map.get("FLAG");
							String clmrefnum = (String) map
									.get("ClaimRefNumber");
							if (flag.equals("APF") && key.equals(clmrefnum)) {
								map = (HashMap) aVector.remove(i);
								map.put("cgclan", cgclan);
								aVector.add(i, map);
							}
						}
					}

				}

			}
		}
		claimForm.setApprvdVectorSize(aVector.size());
		claimForm.setRejectdVectorSize(rVector.size());
		claimForm.setHeldVectorSize(hVector.size());
		claimForm.setForwardedVectorSize(fVector.size());
		claimForm.setTempclosedVectorSize(tVector.size());
		claimForm.setClaimPendingVectorSize(nVector.size());
		claimForm.setReturnVectorSize(rtVector.size());
		claimsummaries.put("AP", aVector);
		claimsummaries.put("RE", rVector);
		claimsummaries.put("HO", hVector);
		claimsummaries.put("FW", fVector);
		claimsummaries.put("TC", tVector);
		claimsummaries.put("NE", nVector);
		claimsummaries.put("RT", rtVector);
		claimForm.setClaimSummaries(claimsummaries);
		claimsummaries = null;
		claimDetails = null;
		aVector = null;
		rVector = null;
		hVector = null;
		fVector = null;
		tVector = null;
		nVector = null;
		rtVector = null;
		claimForm.resetClaimsProcessPage(mapping, request);
		return mapping.findForward("summaryPage");
	}

	public ActionForward claimDeclarationView(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "npaReport", "Entered");
		ArrayList npaDetails = new ArrayList();

		HttpSession session = request.getSession();
		DynaActionForm dynaForm = (DynaActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		String id = (String) dynaForm.get("memberId");
		String clmRefNumber = (String) request.getParameter("clmrefnum");
		ArrayList claimViewArray = new ArrayList();
		ArrayList claimCheckListView = new ArrayList();
		ArrayList claimCheckListViewFinal = new ArrayList();
		Connection connection = DBConnection.getConnection();
		ResultSet claimviewResult;
		PreparedStatement claimviewStmt;
		ResultSet claimviewResult1;
		PreparedStatement claimviewStmt1;
		ResultSet claimviewResult2;
		PreparedStatement claimviewStmt2;
		try {

			String Query = "SELECT C.CLM_REF_NO,A.CGPAN,SSI_UNIT_NAME,decode(nvl(app_reapprove_amount,0),0,app_approved_amount,"
					+ "app_reapprove_amount) APPAMT FROM CLAIM_DETAIL_TEMP C,APPLICATION_DETAIL A,SSI_DETAIL S,claim_wc_detail_temp cw"
					+ " WHERE C.BID = S.BID AND S.SSI_REFERENCE_NUMBER = A.SSI_REFERENCE_NUMBER and c.clm_ref_no = cw.clm_ref_no"
					+ " and a.cgpan = cw.cgpan AND CLM_STATUS IN ('NE')and c.clm_ref_no=? union all SELECT C.CLM_REF_NO,"
					+ "A.CGPAN,SSI_UNIT_NAME,decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) APPAMT"
					+ " FROM CLAIM_DETAIL_TEMP C,APPLICATION_DETAIL A,SSI_DETAIL S,claim_Tc_detail_temp ct WHERE C.BID = S.BID AND "
					+ "S.SSI_REFERENCE_NUMBER = A.SSI_REFERENCE_NUMBER and c.clm_ref_no = ct.clm_ref_no and a.cgpan = ct.cgpan "
					+ "AND CLM_STATUS IN ('NE') and c.clm_ref_no=?";

			claimviewStmt = connection.prepareStatement(Query);
			claimviewStmt.setString(1, clmRefNumber);
			claimviewStmt.setString(2, clmRefNumber);
			claimviewResult = claimviewStmt.executeQuery();

			while (claimviewResult.next()) {

				ClaimDetail claimviewDetails = new ClaimDetail();
				claimviewDetails.setClaimRefNum((claimviewResult.getString(1)));
				claimviewDetails.setCgpan(claimviewResult.getString(2));
				claimviewDetails.setSsiUnitName(claimviewResult.getString(3));
				claimviewDetails.setApprovedClaimAmount(claimviewResult
						.getDouble(4));

				claimViewArray.add(claimviewDetails);
			}

			claimviewResult.close();
			claimviewStmt.close();

			/*
			 * String Query2=
			 * "select IS_ELIG_ACT,IS_ELIG_ACT_COMM,WHET_CIBIL_DONE,WHET_CIBIL_DONE_COMM,IS_RAT_AS_PER_CGS,IS_RAT_AS_PER_CGS_COMM,IS_THIRD_COLLAT_TAKEN,"
			 * +
			 * "IS_THIRD_COLLAT_TAKEN_COMM,IS_NPA_DT_AS_PER_GUID,IS_NPA_DT_AS_PER_GUID_COMM,IS_CLM_OS_WRT_NPA_DT,IS_CLM_OS_WRT_NPA_DT_COMM,WHET_SERIOUS_DEFIC_INVOL,"
			 * +
			 * "WHET_SERIOUS_DEFIC_INVOL_COMM,WHET_MAJOR_DEFIC_INVOLVD,WHET_MAJOR_DEFIC_INVOLVD_COMM,"
			 * +
			 * "WHET_DEFIC_INVOL_BY_STAFF,WHET_DEFIC_INVOL_BY_STAFF_COMM,IS_INTERN_RAT_INVEST_GRAD,"
			 * +
			 * "IS_INTERN_RAT_INVEST_GRAD_COMM,IS_ALL_REC_IN_CLM_FORM,IS_ALL_REC_IN_CLM_FORM_COMM "
			 * +
			 * "from claim_check_list a ,claim_detail_temp b where a.clm_ref_no=b.clm_ref_no and a.clm_ref_no=?"
			 * +"AND CLM_STATUS IN ('NE') and a.clm_ref_no=?";
			 */

			String Query2 = "select IS_ELIG_ACT,IS_ELIG_ACT_COMM,WHET_CIBIL_DONE,WHET_CIBIL_DONE_COMM,IS_RAT_AS_PER_CGS,IS_RAT_AS_PER_CGS_COMM,IS_THIRD_COLLAT_TAKEN,IS_THIRD_COLLAT_TAKEN_COMM,IS_NPA_DT_AS_PER_GUID,IS_NPA_DT_AS_PER_GUID_COMM,IS_CLM_OS_WRT_NPA_DT,IS_CLM_OS_WRT_NPA_DT_COMM,WHET_SERIOUS_DEFIC_INVOL,WHET_SERIOUS_DEFIC_INVOL_COMM,WHET_MAJOR_DEFIC_INVOLVD,WHET_MAJOR_DEFIC_INVOLVD_COMM,WHET_DEFIC_INVOL_BY_STAFF,WHET_DEFIC_INVOL_BY_STAFF_COMM,IS_INTERN_RAT_INVEST_GRAD,IS_INTERN_RAT_INVEST_GRAD_COMM,IS_ALL_REC_IN_CLM_FORM,IS_ALL_REC_IN_CLM_FORM_COMM from claim_check_list a ,claim_detail_temp b where a.clm_ref_no=b.clm_ref_no and a.clm_ref_no=?AND CLM_STATUS IN ('NE','RU') and a.clm_ref_no=?";
			claimviewStmt1 = connection.prepareStatement(Query2);
			claimviewStmt1.setString(1, clmRefNumber);
			claimviewStmt1.setString(2, clmRefNumber);
			claimviewResult1 = claimviewStmt1.executeQuery();

			while (claimviewResult1.next()) {

				ClaimDetail claimviewDetailss = new ClaimDetail();
				claimviewDetailss.setIseligact((claimviewResult1.getString(1)));
				claimviewDetailss.setIseligactcomm(claimviewResult1
						.getString(2));
				claimviewDetailss.setWhetcibildone(claimviewResult1
						.getString(3));
				claimviewDetailss.setWhetcibildonecomm(claimviewResult1
						.getString(4));
				claimviewDetailss.setIsrataspercgs((claimviewResult1
						.getString(5)));
				claimviewDetailss.setIsrataspercgscomm(claimviewResult1
						.getString(6));
				claimviewDetailss.setIsthirdcollattaken(claimviewResult1
						.getString(7));
				claimviewDetailss.setIsthirdcollattakencomm(claimviewResult1
						.getString(8));
				claimviewDetailss.setIsnpadtasperguid((claimviewResult1
						.getString(9)));
				claimviewDetailss.setIsnpadtasperguidcomm(claimviewResult1
						.getString(10));
				claimviewDetailss.setIsclmoswrtnpadt(claimviewResult1
						.getString(11));
				claimviewDetailss.setIsclmoswrtnpadtcomm(claimviewResult1
						.getString(12));
				claimviewDetailss.setWhetseriousdeficinvol((claimviewResult1
						.getString(13)));
				claimviewDetailss.setWhetseriousdeficinvolcomm(claimviewResult1
						.getString(14));
				claimviewDetailss.setWhetmajordeficinvolvd(claimviewResult1
						.getString(15));
				claimviewDetailss.setWhetmajordeficinvolvdcomm(claimviewResult1
						.getString(16));
				claimviewDetailss.setWhetdeficinvolbystaff((claimviewResult1
						.getString(17)));
				claimviewDetailss.setWhetdeficinvolbystaffcomm(claimviewResult1
						.getString(18));
				claimviewDetailss.setIsinternratinvestgrad(claimviewResult1
						.getString(19));
				claimviewDetailss.setIsinternratinvestgradcomm(claimviewResult1
						.getString(20));
				claimviewDetailss.setIsallrecinclmform((claimviewResult1
						.getString(21)));
				claimviewDetailss.setIsallrecinclmformcomm(claimviewResult1
						.getString(22));

				claimCheckListView.add(claimviewDetailss);
			}

			claimviewResult1.close();
			claimviewStmt1.close();

			String Query3 = " select CLM_CHECKER_DONE_BY,mem_bank_name,CLM_MLI_NAME,a.MEM_BNK_ID||a.MEM_ZNE_ID||a.MEM_BRN_ID memid,to_char(CLM_CHECKER_DONE_DT)"
					+ "from  "
					+ "claim_detail_temp a,member_info b where "
					+ "a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id=b.mem_bnk_id||b.mem_zne_id||b.mem_brn_id and "
					+ " clm_ref_no=? ";

			claimviewStmt2 = connection.prepareStatement(Query3);
			claimviewStmt2.setString(1, clmRefNumber);
			claimviewResult2 = claimviewStmt2.executeQuery();

			while (claimviewResult2.next()) {

				ClaimDetail claimviewDetailR = new ClaimDetail();
				claimviewDetailR.setUserid((claimviewResult2.getString(1)));
				claimviewDetailR.setLastname(claimviewResult2.getString(3));
				claimviewDetailR.setDesignation(claimviewResult2.getString(4));
				claimviewDetailR
						.setDateofClaim((claimviewResult2.getString(5)));
				claimCheckListViewFinal.add(claimviewDetailR);

			}

			claimviewResult1.close();
			claimviewStmt1.close();

		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		request.setAttribute("claimViewArray", claimViewArray);
		request.setAttribute("claimDeclArrayListsize", new Integer(
				claimViewArray.size()).toString());

		request.setAttribute("claimCheckListView", claimCheckListView);
		request.setAttribute("claimCheckListViewsize", new Integer(
				claimCheckListView.size()).toString());

		request.setAttribute("claimCheckListViewFinal", claimCheckListViewFinal);
		request.setAttribute("claimCheckListViewFinalSize", new Integer(
				claimCheckListViewFinal.size()).toString());

		return mapping.findForward("success");
	} // commented by anil on 8th Dec 2016
		// ended by rajuk

	public ActionForward displaySettlementDetailFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetTheMemberId(mapping, request);
		return mapping.findForward("settlementFilter");
	}

	public ActionForward getAllPendingSettlements(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		Registration registration = new Registration();
		Vector claimsFiled = processor.getAllClaimsFiled();
		Vector pendingSettlements = new Vector();
		HashMap pendingSettlmnt = null;
		String memberId = null;
		Vector dtls = null;
		if (claimsFiled != null) {
			for (int i = 0; i < claimsFiled.size(); i++) {
				HashMap map = (HashMap) claimsFiled.elementAt(i);
				if (map != null) {
					double totalPendingSettlmnt = 0.0D;
					memberId = (String) map.get("MEMBERID");
					boolean toBeAddedIntoVector = false;
					String bankId = memberId.substring(0, 4);
					String zoneId = memberId.substring(4, 8);
					String branchId = memberId.substring(8, 12);
					MLIInfo mliInfo = registration.getMemberDetails(bankId,
							zoneId, branchId);
					String memberName = mliInfo.getBankName();
					dtls = processor.getSettlementDetails(bankId, zoneId,
							branchId, "F", false);
					if (dtls != null) {
						for (int j = 0; j < dtls.size(); j++) {
							SettlementDetail settDtl = (SettlementDetail) dtls
									.elementAt(j);
							if (settDtl != null) {
								totalPendingSettlmnt += settDtl
										.getApprovedClaimAmt();
								toBeAddedIntoVector = true;
							}
						}

					}
					dtls = null;
					dtls = processor.getSettlementDetails(bankId, zoneId,
							branchId, "S", false);
					if (dtls != null) {
						for (int j = 0; j < dtls.size(); j++) {
							SettlementDetail settDtl = (SettlementDetail) dtls
									.elementAt(j);
							if (settDtl != null) {
								totalPendingSettlmnt += settDtl
										.getApprovedClaimAmt();
								toBeAddedIntoVector = true;
							}
						}

					}
					if (toBeAddedIntoVector) {
						pendingSettlmnt = new HashMap();
						pendingSettlmnt.put("MEMBERID", memberId);
						pendingSettlmnt.put("MEMBERNAME", memberName);
						pendingSettlmnt.put("TotalSettlementAmnt", new Double(
								totalPendingSettlmnt));
						if (!pendingSettlements.contains(pendingSettlmnt))
							pendingSettlements.addElement(pendingSettlmnt);
					}
				}
			}

		}
		claimForm.setSettlementdetail(pendingSettlements);
		return mapping.findForward("settlementDetails");
	}

	public ActionForward getSettlementMemId(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetSettlementProcessPage(mapping, request);
		ClaimsProcessor processor = new ClaimsProcessor();
		String memberId = request.getParameter("MEMBERID");
		if (memberId == null)
			memberId = claimForm.getMemberId();
		claimForm.setMemberId(memberId);
		HttpSession session = request.getSession(false);
		session.setAttribute("closureMemberId", memberId);
		String scr = request.getParameter("Src");
		if (scr != null && scr.equals("Reg")) {
			memberId = request.getParameter("MemberId");
			claimForm.setMemberId(memberId);
		}
		scr = "";
		Vector memberids = processor.getAllMemberIds();
		if (!memberids.contains(memberId))
			throw new NoMemberFoundException((new StringBuilder())
					.append("Member Id :").append(memberId)
					.append(" does not exist in the database.").toString());
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		Log.log(Log.INFO, "ClaimAction", "getSettlementMemId",
				(new StringBuilder()).append("Member Id :").append(bankId)
						.append(zoneId).append(branchId).toString());
		Vector settlementsOfFirstClaim = processor.getSettlementDetails(bankId,
				zoneId, branchId, "F", true);
		Vector settlementsOfSecondClaim = processor.getSettlementDetails(
				bankId, zoneId, branchId, "S", true);
		if (settlementsOfFirstClaim != null && settlementsOfSecondClaim != null
				&& settlementsOfFirstClaim.size() == 0
				&& settlementsOfSecondClaim.size() == 0) {
			request.setAttribute("message",
					"There are no Settlement Details to process.");
			return mapping.findForward("success");
		}
		SettlementDetail settlementDtl = null;
		String thiskey = null;
		String borrowerId = null;
		String cgclan = null;
		double clmApprvdAmnt = 0.0D;
		for (int i = 0; i < settlementsOfFirstClaim.size(); i++) {
			settlementDtl = (SettlementDetail) settlementsOfFirstClaim
					.elementAt(i);
			if (settlementDtl != null) {
				borrowerId = settlementDtl.getCgbid();
				cgclan = settlementDtl.getCgclan();
				clmApprvdAmnt = settlementDtl.getApprovedClaimAmt();
				thiskey = (new StringBuilder()).append("F#").append(borrowerId)
						.append("#").append(cgclan).toString();
				claimForm.setSettlementAmounts(thiskey, (new Double(
						clmApprvdAmnt)).toString());
				claimForm.setFinalSettlementFlags(thiskey, "N");
				double penaltyAmnt = settlementDtl.getPenaltyAmnt();
				double pendingReceivableFromMLI = settlementDtl
						.getPendingFromMLI();
				Log.log(2,
						"ClaimAction",
						"getSettlementMemId",
						(new StringBuilder())
								.append("pendingReceivableFromMLI :")
								.append(pendingReceivableFromMLI).toString());
				String key = (new StringBuilder()).append("F#")
						.append(borrowerId).append("#").append(cgclan)
						.toString();
				claimForm.setPenaltyFees(key, new Double(penaltyAmnt));
				Log.log(2, "ClaimAction", "getSettlementMemId",
						(new StringBuilder()).append("KEY :").append(key)
								.toString());
				claimForm.setPendingAmntsFromMLI(key, new Double(
						pendingReceivableFromMLI));
			}
		}

		settlementDtl = null;
		thiskey = null;
		borrowerId = null;
		cgclan = null;
		for (int i = 0; i < settlementsOfSecondClaim.size(); i++) {
			settlementDtl = (SettlementDetail) settlementsOfSecondClaim
					.elementAt(i);
			if (settlementDtl != null) {
				borrowerId = settlementDtl.getCgbid();
				cgclan = settlementDtl.getCgclan();
				thiskey = (new StringBuilder()).append("S#").append(borrowerId)
						.append("#").append(cgclan).toString();
				clmApprvdAmnt = settlementDtl.getApprovedClaimAmt();
				claimForm.setFinalSettlementFlags(thiskey, "Y");
				claimForm.setSettlementAmounts(thiskey, (new Double(
						clmApprvdAmnt)).toString());
				double penaltyAmnt = settlementDtl.getPenaltyAmnt();
				double pendingReceivableFromMLI = settlementDtl
						.getPendingFromMLI();
				String key = (new StringBuilder()).append("S#")
						.append(borrowerId).append("#").append(cgclan)
						.toString();
				claimForm.setPenaltyFees(key, new Double(penaltyAmnt));
				claimForm.setPendingAmntsFromMLI(key, new Double(
						pendingReceivableFromMLI));
			}
		}

		if (settlementsOfFirstClaim != null)
			claimForm.setFirstCounter(settlementsOfFirstClaim.size());
		claimForm.setSettlementsOfFirstClaim(settlementsOfFirstClaim);
		if (settlementsOfSecondClaim != null)
			claimForm.setSecondCounter(settlementsOfSecondClaim.size());
		claimForm.setSettlementsOfSecondClaim(settlementsOfSecondClaim);
		memberids = null;
		settlementsOfFirstClaim = null;
		settlementsOfSecondClaim = null;
		return mapping.findForward("settlementDetails");
	}

	public ActionForward saveSettlementDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "saveSettlementDetails", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Map settlementAmounts = claimForm.getSettlementAmounts();
		Vector settlementDetails = new Vector();
		SettlementDetail settlementDtl = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Set settlementAmountsSet = settlementAmounts.keySet();
		Iterator settlementAmountsIterator = settlementAmountsSet.iterator();
		String installmentFlag = null;
		double totalSettlementAmnt = 0.0D;
		String finalInstallmentFlag = null;
		StringTokenizer tokenizer = null;
		String cgclan = null;
		String bid = null;
		String firstSettlementAmntStr = null;
		String settlmntDtStrFormat = null;
		while (settlementAmountsIterator.hasNext()) {
			Date firstSettlementDt = null;
			Date secondSettlementDt = null;
			double firstSettlementAmnt = 0.0D;
			double secondSettlementAmnt = 0.0D;
			settlementDtl = new SettlementDetail();
			String key = (String) settlementAmountsIterator.next();
			boolean installmentFlagRead = false;
			boolean cgclanRead = false;
			boolean borrowerRead = false;
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreTokens();) {
				String token = tokenizer.nextToken();
				if (!cgclanRead)
					if (!borrowerRead) {
						if (!installmentFlagRead) {
							installmentFlag = token;
							installmentFlagRead = true;
						} else {
							bid = token;
							borrowerRead = true;
						}
					} else {
						cgclan = token;
						cgclanRead = true;
					}
			}

			if (installmentFlag.equals("F")) {
				firstSettlementAmntStr = (String) claimForm
						.getSettlementAmounts(key);
				if (firstSettlementAmntStr.equals(""))
					firstSettlementAmntStr = "0.0";
				firstSettlementAmnt = Double
						.parseDouble(firstSettlementAmntStr);
				settlmntDtStrFormat = (String) claimForm
						.getSettlementDates(key);
				if (!settlmntDtStrFormat.equals(""))
					firstSettlementDt = sdf.parse(settlmntDtStrFormat,
							new ParsePosition(0));
				finalInstallmentFlag = (String) claimForm
						.getFinalSettlementFlags(key);
				settlementDtl.setCgbid(bid);
				settlementDtl.setCgclan(cgclan);
				settlementDtl.setFinalSettlementFlag(finalInstallmentFlag);
				settlementDtl.setWhichInstallment("F");
				settlementDtl.setTierOneSettlement(firstSettlementAmnt);
				settlementDtl.setTierOneSettlementDt(firstSettlementDt);
				if (finalInstallmentFlag != null && firstSettlementDt != null
						&& !settlementDetails.contains(settlementDtl))
					settlementDetails.addElement(settlementDtl);
			}
			if (installmentFlag.equals("S")) {
				String secondSettlementAmntStr = (String) claimForm
						.getSettlementAmounts(key);
				if (secondSettlementAmntStr.equals(""))
					secondSettlementAmntStr = "0.0";
				secondSettlementAmnt = Double
						.parseDouble(secondSettlementAmntStr);
				String secondSettlementDtStr = (String) claimForm
						.getSettlementDates(key);
				if (!secondSettlementDtStr.equals(""))
					secondSettlementDt = sdf.parse(secondSettlementDtStr,
							new ParsePosition(0));
				finalInstallmentFlag = (String) claimForm
						.getFinalSettlementFlags(key);
				if (finalInstallmentFlag != null)
					;
				settlementDtl.setCgbid(bid);
				settlementDtl.setCgclan(cgclan);
				settlementDtl.setFinalSettlementFlag(finalInstallmentFlag);
				settlementDtl.setWhichInstallment("S");
				settlementDtl.setTierTwoSettlement(secondSettlementAmnt);
				settlementDtl.setTierTwoSettlementDt(secondSettlementDt);
				if (finalInstallmentFlag != null && secondSettlementDt != null
						&& !settlementDetails.contains(settlementDtl))
					settlementDetails.addElement(settlementDtl);
			}
			totalSettlementAmnt += firstSettlementAmnt;
			totalSettlementAmnt += secondSettlementAmnt;
		}
		User user = getUserInformation(request);
		String userid = user.getUserId();
		if (settlementDetails.size() == 0) {
			request.setAttribute("message", "There are no Details to save.");
			return mapping.findForward("success");
		}
		claimForm.setSettledClms(settlementDetails);
		claimForm.setInstrumenAmount(totalSettlementAmnt);
		claimForm.setBorrowerID(bid);
		claimForm.setUserId(userid);
		settlementDetails = null;
		Registration registration = new Registration();
		CollectingBank collectingBank = registration
				.getCollectingBank((new StringBuilder()).append("(")
						.append(claimForm.getMemberId().trim()).append(")")
						.toString());
		IFProcessor ifProcessor = new IFProcessor();
		ArrayList instruments = ifProcessor.getInstrumentTypes("G");
		ArrayList bankDetails = ifProcessor.getBankAccounts();
		ArrayList bankNames = new ArrayList();
		for (int i = 0; i < bankDetails.size(); i++) {
			BankAccountDetail bankAccountDetail = (BankAccountDetail) bankDetails
					.get(i);
			String bankName = bankAccountDetail.getBankName();
			String branchName = bankAccountDetail.getBankBranchName();
			String bankBranchName = (new StringBuilder()).append(bankName)
					.append(",").append(branchName).toString();
			bankNames.add(bankBranchName);
		}

		claimForm.setBanksList(bankNames);
		claimForm.setInstrumentTypes(instruments);
		claimForm.setCollectingBank(collectingBank);
		claimForm.setModeOfPayment("");
		claimForm.setPaymentDate(null);
		claimForm.setInstrumentNo("");
		claimForm.setInstrumentDate("");
		claimForm.setDrawnAtBank("");
		claimForm.setDrawnAtBranch("");
		claimForm.setPayableAt("");
		return mapping.findForward("paymentDetails");
	}

	public ActionForward saveSettlementAndPaymentDtls(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String collectingBranchName = null;
		String paidToString = null;
		String memberId = claimForm.getMemberId().trim();
		CollectingBank colBank = claimForm.getCollectingBank();
		if (colBank != null)
			collectingBranchName = colBank.getBranchName();
		Vector settlementDtls = claimForm.getSettledClms();
		String whichInstllmnt = null;
		String borrowerId = null;
		String cgclan = null;
		String whetherFinalInstallment = null;
		String modeOfPayment = claimForm.getModeOfPayment();
		Date paymentDate = claimForm.getPaymentDate();
		String instrumentNumber = claimForm.getInstrumentNo();
		String instrumentDate = claimForm.getInstrumentDate();
		Vector voucherPaymentDtls = new Vector();
		paidToString = (new StringBuilder()).append("PAID TO :")
				.append(memberId).toString();
		Properties accCodes = new Properties();
		String contextPath = request.getSession(false).getServletContext()
				.getRealPath("");
		Log.log(5, "ClaimAction", "saveSettlementAndPaymentDtls",
				(new StringBuilder()).append("path ").append(contextPath)
						.toString());
		File tempFile = new File((new StringBuilder()).append(contextPath)
				.append("\\WEB-INF\\classes").toString(),
				"AccountCodes.properties");
		Log.log(5, "ClaimAction", "saveSettlementAndPaymentDtls",
				"file opened ");
		File accCodeFile = new File(tempFile.getAbsolutePath());
		try {
			FileInputStream fin = new FileInputStream(accCodeFile);
			accCodes.load(fin);
		} catch (FileNotFoundException fe) {
			throw new MessageException("Could not load Account Codes.");
		} catch (IOException ie) {
			throw new MessageException("Could not load Account Codes.");
		}
		Log.log(5, "ClaimAction", "saveSettlementAndPaymentDtls",
				"props loaded ");
		Log.log(5,
				"ClaimAction",
				"saveSettlementAndPaymentDtls",
				(new StringBuilder()).append("code ")
						.append(accCodes.getProperty("bank_ac")).toString());
		for (int k = 0; k < settlementDtls.size(); k++) {
			SettlementDetail sd = (SettlementDetail) settlementDtls
					.elementAt(k);
			if (sd != null) {
				whichInstllmnt = sd.getWhichInstallment();
				borrowerId = sd.getCgbid();
				cgclan = sd.getCgclan();
				whetherFinalInstallment = sd.getFinalSettlementFlag();
				ArrayList vouchers = new ArrayList();
				VoucherDetail vd = new VoucherDetail();
				vd.setBankGLName("");
				vd.setBankGLCode(accCodes.getProperty("bank_ac"));
				vd.setDeptCode("CG");
				vd.setNarration((new StringBuilder()).append("cgclan :")
						.append(cgclan).append(", ").append("BORROWERID")
						.append(" :").append(borrowerId).toString());
				vd.setPaymentDt(paymentDate);
				vd.setVoucherType("PAYMENT VOUCHER");
				if (whichInstllmnt != null && whetherFinalInstallment != null) {
					if (whichInstllmnt.equals("F")
							&& whetherFinalInstallment.equals("N")) {
						vd.setAmount(0.0D - sd.getTierOneSettlement());
						Voucher debitVouchr = new Voucher();
						debitVouchr.setPaidTo(paidToString);
						debitVouchr.setInstrumentNo(instrumentNumber);
						debitVouchr.setInstrumentDate(instrumentDate);
						debitVouchr.setInstrumentType(modeOfPayment);
						debitVouchr.setAmountInRs((new Double(sd
								.getTierOneSettlement())).toString());
						debitVouchr.setDebitOrCredit("D");
						debitVouchr.setAcCode(accCodes
								.getProperty("clm_first_inst_ac"));
						vouchers.add(debitVouchr);
						vd.setVouchers(vouchers);
						voucherPaymentDtls.addElement(vd);
					}
					if (whichInstllmnt.equals("F")
							&& whetherFinalInstallment.equals("Y")) {
						vd.setAmount(0.0D - sd.getTierOneSettlement());
						Voucher debitVouchr1 = new Voucher();
						debitVouchr1.setPaidTo(paidToString);
						debitVouchr1.setInstrumentNo(instrumentNumber);
						debitVouchr1.setInstrumentDate(instrumentDate);
						debitVouchr1.setInstrumentType(modeOfPayment);
						debitVouchr1.setAmountInRs((new Double(sd
								.getTierOneSettlement())).toString());
						debitVouchr1.setDebitOrCredit("D");
						debitVouchr1.setAcCode(accCodes
								.getProperty("clm_first_inst_ac"));
						vouchers.add(debitVouchr1);
						Voucher debitVouchr2 = new Voucher();
						debitVouchr2.setPaidTo(paidToString);
						debitVouchr2.setInstrumentNo(instrumentNumber);
						debitVouchr2.setInstrumentDate(instrumentDate);
						debitVouchr2.setInstrumentType(modeOfPayment);
						String noAmnt = "0.0";
						debitVouchr2.setAmountInRs(noAmnt);
						debitVouchr2.setDebitOrCredit("D");
						debitVouchr2.setAcCode(accCodes
								.getProperty("clm_second_inst_ac"));
						vouchers.add(debitVouchr2);
						vd.setVouchers(vouchers);
						voucherPaymentDtls.addElement(vd);
					}
					if (whichInstllmnt.equals("S")) {
						vd.setAmount(0.0D - sd.getTierTwoSettlement());
						Voucher debitVouchr = new Voucher();
						debitVouchr.setPaidTo(paidToString);
						debitVouchr.setInstrumentNo(instrumentNumber);
						debitVouchr.setInstrumentDate(instrumentDate);
						debitVouchr.setInstrumentType(modeOfPayment);
						debitVouchr.setAmountInRs((new Double(sd
								.getTierTwoSettlement())).toString());
						debitVouchr.setDebitOrCredit("D");
						debitVouchr.setAcCode(accCodes
								.getProperty("clm_second_inst_ac"));
						vouchers.add(debitVouchr);
						vd.setVouchers(vouchers);
						voucherPaymentDtls.addElement(vd);
					}
				}
				sd = null;
			}
		}

		User user = getUserInformation(request);
		String userid = user.getUserId();
		IFProcessor ifProcessor = new IFProcessor();
		ChequeDetails chequeDetails = new ChequeDetails();
		if (claimForm.getModeOfPayment().equals("CHEQUE")) {
			if (claimForm.getBnkName() == null
					|| claimForm.getBnkName().equals(""))
				throw new MessageException(
						"Since no bank names are available,Cheque Details cannot be inserted");
			String bankBranchName = claimForm.getBnkName();
			int start = bankBranchName.indexOf(",");
			String bankName = bankBranchName.substring(0, start);
			String branchName = bankBranchName.substring(start + 1);
			chequeDetails.setUserId(userid);
			chequeDetails.setChequeAmount(claimForm.getInstrumenAmount());
			chequeDetails.setChequeDate(claimForm.getPaymentDate());
			chequeDetails.setChequeNumber(claimForm.getInstrumentNo());
			chequeDetails.setChequeIssuedTo("CG");
			chequeDetails.setBankName(bankName);
			chequeDetails.setBranchName(branchName);
			chequeDetails.setChequeRemarks(null);
			chequeDetails.setPayId(null);
		} else {
			chequeDetails = null;
		}
		processor.saveSettlementDetails(settlementDtls, voucherPaymentDtls,
				userid, chequeDetails, contextPath);
		request.setAttribute("message",
				"The Settlement Detail(s) and Payment Detail(s) are saved successfully.");
		return mapping.findForward("success");
	}

	public ActionForward generateSettAdviceLinkFromSuccessPage(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String memberId = claimForm.getMemberId();
		Vector settlementAdviceDtlsFirstSttlmnt = processor
				.getSettlementAdviceDetail(memberId, "F");
		Vector settlementAdviceDtlsSecSttlmnt = processor
				.getSettlementAdviceDetail(memberId, "S");
		claimForm
				.setSettlmntAdviceDtlsFirstSttlmnt(settlementAdviceDtlsFirstSttlmnt);
		claimForm
				.setSettlmntAdviceDtlsSecondSttlmnt(settlementAdviceDtlsSecSttlmnt);
		if (settlementAdviceDtlsFirstSttlmnt != null)
			claimForm.setFirstCounter(settlementAdviceDtlsFirstSttlmnt.size());
		if (settlementAdviceDtlsSecSttlmnt != null)
			claimForm.setSecondCounter(settlementAdviceDtlsSecSttlmnt.size());
		return mapping.findForward("displayCSAGenerateOptionPage");
	}

	public ActionForward displaySettlementAdviceFilter(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.resetTheMemberIdAndFlag(mapping, request);
		return mapping.findForward("displaysettlementadvicefilter");
	}

	public ActionForward getSettlementAdviceFilterDtl(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String memberIdFlag = claimForm.getMemberIdFlag();
		String memberId = null;
		ClaimsProcessor processor = new ClaimsProcessor();
		Vector allMemberIds = new Vector();
		Vector settlementAdviceDtlsFirstSttlmnt = null;
		Vector settlementAdviceDtlsSecSttlmnt = null;
		if (memberIdFlag.equals("All")) {
			settlementAdviceDtlsFirstSttlmnt = new Vector();
			settlementAdviceDtlsSecSttlmnt = new Vector();
			Vector allclaimsfiled = processor.getAllClaimsFiled();
			for (int i = 0; i < allclaimsfiled.size(); i++) {
				HashMap thisclaim = (HashMap) allclaimsfiled.elementAt(i);
				if (thisclaim != null) {
					String thismemberId = (String) thisclaim.get("MEMBERID");
					if (!allMemberIds.contains(thismemberId))
						allMemberIds.addElement(thismemberId);
					thisclaim = null;
				}
			}

			for (int i = 0; i < allMemberIds.size(); i++) {
				memberId = (String) allMemberIds.elementAt(i);
				if (memberId != null) {
					Vector first = processor.getSettlementAdviceDetail(
							memberId, "F");
					Vector second = processor.getSettlementAdviceDetail(
							memberId, "S");
					for (int j = 0; j < first.size(); j++) {
						HashMap thismap = (HashMap) first.elementAt(j);
						if (thismap != null
								&& !settlementAdviceDtlsFirstSttlmnt
										.contains(thismap))
							settlementAdviceDtlsFirstSttlmnt
									.addElement(thismap);
					}

					for (int j = 0; j < second.size(); j++) {
						HashMap anothermap = (HashMap) second.elementAt(j);
						if (anothermap != null
								&& !settlementAdviceDtlsSecSttlmnt
										.contains(anothermap))
							settlementAdviceDtlsSecSttlmnt
									.addElement(anothermap);
					}

				}
			}

			allclaimsfiled = null;
		} else if (memberIdFlag.equals("Specific")) {
			memberId = claimForm.getMemberId();
			settlementAdviceDtlsFirstSttlmnt = processor
					.getSettlementAdviceDetail(memberId, "F");
			settlementAdviceDtlsSecSttlmnt = processor
					.getSettlementAdviceDetail(memberId, "S");
		}
		claimForm
				.setSettlmntAdviceDtlsFirstSttlmnt(settlementAdviceDtlsFirstSttlmnt);
		claimForm
				.setSettlmntAdviceDtlsSecondSttlmnt(settlementAdviceDtlsSecSttlmnt);
		if (settlementAdviceDtlsFirstSttlmnt != null)
			claimForm.setFirstCounter(settlementAdviceDtlsFirstSttlmnt.size());
		if (settlementAdviceDtlsSecSttlmnt != null)
			claimForm.setSecondCounter(settlementAdviceDtlsSecSttlmnt.size());
		allMemberIds = null;
		settlementAdviceDtlsFirstSttlmnt = null;
		settlementAdviceDtlsSecSttlmnt = null;
		return mapping.findForward("displayCSAGenerateOptionPage");
	}

	public ActionForward saveGenerateCSAOption(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Map settlmntAdviceDtls = claimForm.getSettlementAdviceFlags();
		Set settlmntAdviceDtlsSet = settlmntAdviceDtls.keySet();
		Iterator settlmntAdviceDtlsIterator = settlmntAdviceDtlsSet.iterator();
		StringTokenizer tokenizer = null;
		String memberId = null;
		String cgclan = null;
		String installmentFlag = null;
		String settlmntAdviceFlag = null;
		String settlementDt = null;
		double settlementAmnt = 0.0D;
		HashMap checkedDtlsMap = null;
		Vector checkedFirstSettlmntDtls = new Vector();
		Vector checkedSecondSettlmntDtls = new Vector();
		String cgcsanumber = null;
		while (settlmntAdviceDtlsIterator.hasNext()) {
			boolean isMemIdRead = false;
			boolean isCGCLANRead = false;
			boolean instllmntRead = false;
			boolean isSettlmntDtRead = false;
			boolean isSettlmntAmntRead = false;
			String key = (String) settlmntAdviceDtlsIterator.next();
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreElements();) {
				String token = (String) tokenizer.nextElement();
				if (!isSettlmntDtRead)
					if (!isSettlmntAmntRead) {
						if (!instllmntRead) {
							if (!isCGCLANRead) {
								if (!isMemIdRead) {
									memberId = token;
									isMemIdRead = true;
								} else {
									cgclan = token;
									isCGCLANRead = true;
								}
							} else {
								installmentFlag = token;
								instllmntRead = true;
							}
						} else {
							settlementAmnt = Double.parseDouble(token);
							isSettlmntAmntRead = true;
						}
					} else {
						settlementDt = token;
						isSettlmntDtRead = true;
					}
			}

			settlmntAdviceFlag = (String) claimForm
					.getSettlementAdviceFlags(key);
			if (settlmntAdviceFlag.equals("Y") && installmentFlag.equals("F")) {
				checkedDtlsMap = new HashMap();
				checkedDtlsMap.put("MEMBERID", memberId);
				checkedDtlsMap.put("cgclan", cgclan);
				checkedDtlsMap.put("FirstSettlmntAmnt", new Double(
						settlementAmnt));
				checkedDtlsMap.put("FirstSettlmntDt", settlementDt);
				checkedDtlsMap.put("CGCSA", cgcsanumber);
				if (!checkedFirstSettlmntDtls.contains(checkedDtlsMap))
					checkedFirstSettlmntDtls.addElement(checkedDtlsMap);
				checkedDtlsMap = null;
			}
			if (settlmntAdviceFlag.equals("Y") && installmentFlag.equals("S")) {
				checkedDtlsMap = new HashMap();
				checkedDtlsMap.put("MEMBERID", memberId);
				checkedDtlsMap.put("cgclan", cgclan);
				checkedDtlsMap.put("SecondSettlmntAmnt", new Double(
						settlementAmnt));
				checkedDtlsMap.put("SecondSettlmntDt", settlementDt);
				if (!checkedSecondSettlmntDtls.contains(checkedDtlsMap))
					checkedSecondSettlmntDtls.addElement(checkedDtlsMap);
				checkedDtlsMap = null;
			}
		}
		if (checkedFirstSettlmntDtls.size() == 0
				&& checkedSecondSettlmntDtls.size() == 0) {
			request.setAttribute("message",
					"There are no CGCLAN(s) to generate Advice for.");
			return mapping.findForward("success");
		} else {
			claimForm
					.setCheckedFirstSettlmntAdviceDtls(checkedFirstSettlmntDtls);
			claimForm.setFirstCounter(checkedFirstSettlmntDtls.size());
			claimForm
					.setCheckedSecondSettlmntAdviceDtls(checkedSecondSettlmntDtls);
			claimForm.setSecondCounter(checkedSecondSettlmntDtls.size());
			checkedFirstSettlmntDtls = null;
			checkedSecondSettlmntDtls = null;
			return mapping.findForward("displayCSASummaryPage");
		}
	}

	public ActionForward savePaymentVoucher(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		Map paymentVoucherIds = claimForm.getPaymentVoucherIds();
		Set paymentVoucherIdsSet = paymentVoucherIds.keySet();
		Iterator paymentVoucherIdsIterator = paymentVoucherIdsSet.iterator();
		Vector details = new Vector();
		HashMap individualDtl = null;
		String cgclan = null;
		String paymentVoucherId = null;
		String whichInstallment = null;
		StringTokenizer tokenizer = null;
		while (paymentVoucherIdsIterator.hasNext()) {
			boolean isCGCLANRead = false;
			boolean isInstallmentFlag = false;
			String key = (String) paymentVoucherIdsIterator.next();
			for (tokenizer = new StringTokenizer(key, "#"); tokenizer
					.hasMoreTokens();) {
				String token = tokenizer.nextToken();
				if (!isInstallmentFlag && !isCGCLANRead) {
					cgclan = token;
					isCGCLANRead = true;
				} else {
					whichInstallment = token;
					isInstallmentFlag = true;
				}
			}

			if (whichInstallment.equals("F")) {
				individualDtl = new HashMap();
				individualDtl.put("cgclan", cgclan);
				individualDtl.put("INSTALLMENTFLAG", "F");
				paymentVoucherId = (String) claimForm.getPaymentVoucherIds(key);
				individualDtl.put("VOUCHERID", paymentVoucherId);
				if (!details.contains(individualDtl))
					details.addElement(individualDtl);
				individualDtl = null;
			}
			if (whichInstallment.equals("S")) {
				individualDtl = new HashMap();
				individualDtl.put("cgclan", cgclan);
				individualDtl.put("INSTALLMENTFLAG", "S");
				paymentVoucherId = (String) claimForm.getPaymentVoucherIds(key);
				individualDtl.put("VOUCHERID", paymentVoucherId);
				if (!details.contains(individualDtl))
					details.addElement(individualDtl);
				individualDtl = null;
			}
		}
		User user = getUserInformation(request);
		String userid = user.getUserId();
		processor.saveSettlementAdviceDetail(details, userid);
		claimForm.resetGenerateOptionFlag(mapping, request);
		details = null;
		return mapping.findForward("paymentvouchersaved");
	}

	public ActionForward saveITPANDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "saveITPANDetails", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String borrowerId = claimForm.getBorrowerID();
		String itpanOfChiefPromoter = claimForm.getItpanOfChiefPromoter();
		char array1[] = itpanOfChiefPromoter.toCharArray();
		if (array1.length == 10 && Character.isLetter(array1[0])
				&& Character.isLetter(array1[1])
				&& Character.isLetter(array1[2])
				&& Character.isLetter(array1[3])
				&& Character.isLetter(array1[4])
				&& Character.isDigit(array1[5]) && Character.isDigit(array1[6])
				&& Character.isDigit(array1[7]) && Character.isDigit(array1[8])
				&& Character.isLetter(array1[9])) {
			HashMap itpanDetails = new HashMap();
			itpanDetails.put("BORROWERID", borrowerId);
			itpanDetails.put("Clm_ITPAN_of_Chief_Promoter",
					itpanOfChiefPromoter);
			claimForm.setItpanDetails(itpanDetails);
			claimForm.setRecoveryFlag("N");
		} else {
			throw new InvalidDataException("ITPAN is not in the correct format");
		}
		Log.log(Log.INFO, "ClaimAction", "saveITPANDetails", "Exited");
		return mapping.findForward("claimdetails");
	}

	private ClaimActionForm validateMemIdBidCgpan(String memberId,
			String borrowerId, String inputcgpan, ClaimActionForm claimForm)
			throws Exception {
		ClaimsProcessor processor;
		Connection conn;
		Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan", "Entered");
		processor = new ClaimsProcessor();
		Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
				"Going to validate Member Id");
		Vector memberids = processor.getAllMemberIds();
		if (!memberids.contains(memberId)) {
			Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
					"Error validating the Member Id...");
			claimForm.setMemberId("");
			throw new NoMemberFoundException((new StringBuilder())
					.append("Member Id :").append(memberId)
					.append(" does not exist in the database.").toString());
		}
		memberids = null;
		if (!borrowerId.equals("")) {
			Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
					"validating the Borrower Id with the member id......");
			ArrayList borrowerids = processor.getAllBorrowerIDs(memberId);
			if (!borrowerids.contains(borrowerId)) {
				Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
						"The Borrower Id could not be validated for the Member Id");
				claimForm.setMemberId(memberId);
				claimForm.setBorrowerID("");
				throw new NoDataException(
						(new StringBuilder())
								.append(borrowerId)
								.append(" is not among the borrower Ids for the Member Id :")
								.append(memberId)
								.append(". Please enter correct Member Id and Borrower Id.")
								.toString());
			}
			borrowerids = null;
		}
		// if(inputcgpan.equals("") || memberId.equals(""))
		if (!inputcgpan.equals("") && !memberId.equals(""))
		// break MISSING_BLOCK_LABEL_441;
		{
			CPDAO cpdao = new CPDAO();
			CallableStatement callableStmt = null;
			conn = null;
			int status = -1;
			String errorCode = null;
			try {
				conn = DBConnection.getConnection();
				callableStmt = conn
						.prepareCall("{? = call funcCheckCgpanMemberID(?,?,?)}");
				callableStmt.registerOutParameter(1, 4);
				callableStmt.setString(2, inputcgpan);
				callableStmt.setString(3, memberId);
				callableStmt.registerOutParameter(4, 12);
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(4);
				if (status == 1) {
					Log.log(2,
							"CPDAO",
							"getMemberIDForCGPAN()",
							(new StringBuilder())
									.append("SP returns a 1. Error code is :")
									.append(errorCode).toString());
					callableStmt.close();
					throw new DatabaseException(errorCode);
				}
				if (status == 0)
					callableStmt.close();
			} catch (SQLException sqlexception) {
				Log.log(2, "CPDAO", "getMemberIDForCGPAN()",
						"Error retrieving MemberId for the CGPAN!");
				throw new DatabaseException(sqlexception.getMessage());
			} finally {
				DBConnection.freeConnection(conn);
			}
		}
		if (!inputcgpan.equals("") && borrowerId.equals("")) {
			Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
					"for the given cgpan, retrieving the borrower id......");
			borrowerId = processor.getBorowwerForCGPAN(inputcgpan).trim();
			claimForm.setBorrowerID(borrowerId);
			Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan",
					"for the given cgpan, successfully retrieved the borrower id......");
		} else if (!inputcgpan.equals("") && !borrowerId.equals("")) {
			String borrowerForThisCgpan = processor
					.getBorowwerForCGPAN(inputcgpan);
			if (!borrowerForThisCgpan.equals(borrowerId)) {
				Log.log(Log.INFO,
						"ClaimAction",
						"validateMemIdBidCgpan",
						"Error: Borrower Id for the cgpan and the Borrower Id input in the text field are not same.");
				claimForm.setMemberId(memberId);
				claimForm.setCgpan("");
				throw new NoDataException(
						"Please enter correct Borrower Id and CGPAN");
			}
		}
		Log.log(Log.INFO,
				"ClaimAction",
				"validateMemIdBidCgpan",
				(new StringBuilder()).append("Member Id is :")
						.append(claimForm.getMemberId()).toString());
		Log.log(Log.INFO,
				"ClaimAction",
				"validateMemIdBidCgpan",
				(new StringBuilder()).append("Borrower Id is :")
						.append(claimForm.getBorrowerID()).toString());
		Log.log(Log.INFO,
				"ClaimAction",
				"validateMemIdBidCgpan",
				(new StringBuilder()).append("CGPAN is :")
						.append(claimForm.getCgpan()).toString());
		Log.log(Log.INFO, "ClaimAction", "validateMemIdBidCgpan", "Exited");
		return claimForm;
	}

	private boolean checkIfITPANAvailable(String memberId, String borrowerId)
			throws Exception {
		Log.log(Log.INFO, "ClaimAction", "checkIfITPANAvailable", "Entered");
		GMProcessor processor = new GMProcessor();
		BorrowerDetails bidDtls = processor.getBorrowerDetailsForBID(memberId,
				borrowerId);
		SSIDetails ssiDtls = bidDtls.getSsiDetails();
		String cpITPAN = ssiDtls.getCpITPAN();
		Log.log(Log.INFO, "ClaimAction", "checkIfITPANAvailable",
				(new StringBuilder()).append("Chief Promoter ITPAN is :")
						.append(cpITPAN).toString());
		if (cpITPAN != null && !cpITPAN.equals("")) {
			return true;
		} else {
			Log.log(Log.INFO, "ClaimAction", "checkIfITPANAvailable", "Exited");
			return false;
		}
	}

	public ActionForward displayClaimRefNumberDtlsMod_27062011(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		Vector firstClms = claimForm.getFirstInstallmentClaims();
		Vector secClms = claimForm.getSecondInstallmentClaims();
		String claimRefNumber = request.getParameter("ClaimRefNumber");
		double clmEligibleAmnt = 0.0D;
		String isClaimProceedings = claimForm.getIsClaimProceedings();
		if (firstClms != null) {
			for (int i = 0; i < firstClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		if (secClms != null) {
			for (int i = 0; i < secClms.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secClms.elementAt(i);
				if (clmDtl != null) {
					String clmRefNum = clmDtl.getClaimRefNum();
					if (clmRefNum != null && clmRefNum.equals(claimRefNumber))
						clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
				}
			}

		}
		ClaimsProcessor processor = new ClaimsProcessor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ClaimDetail claimdetail = processor
				.getDetailsForClaimRefNumber(claimRefNumber);
		claimdetail.setEligibleClaimAmt(clmEligibleAmnt);
		claimdetail.setClaimRefNum(claimRefNumber);
		Date dtOfRecallNotice = claimdetail.getDateOfIssueOfRecallNotice();
		if (dtOfRecallNotice != null)
			claimdetail.setDateOfIssueOfRecallNoticeStr(sdf
					.format(dtOfRecallNotice));
		Date npaDate = claimdetail.getNpaDate();
		if (npaDate != null)
			claimdetail.setNpaDateStr(sdf.format(npaDate));
		Date dtOfNPAReportedToCGTSI = claimdetail.getDtOfNPAReportedToCGTSI();
		if (dtOfNPAReportedToCGTSI != null)
			claimdetail.setDtOfNPAReportedToCGTSIStr(sdf
					.format(dtOfNPAReportedToCGTSI));
		double amtClaimed = claimdetail.getAppliedClaimAmt();
		clmEligibleAmnt = claimdetail.getEligibleClaimAmt();
		String amntClaimed = Double.toString(amtClaimed);
		claimForm.setAmountclaimed(amntClaimed);
		claimForm.setDanSummaryReportDetails(claimdetail
				.getDanSummaryReportDetails());
		claimForm.setClaimdetail(claimdetail);
		claimForm.setTcInterestChargeForThisBorrower("");
		claimForm.setWcOtherChargesAsOnNPA("");
		claimForm.setTcPrinRecoveriesAfterNPA("");
		claimForm.setTcInterestChargesRecovAfterNPA("");
		claimForm.setWcPrincipalRecoveAfterNPA("");
		claimForm.setWcothercgrgsRecAfterNPA("");
		claimForm.setDateofReceipt(null);
		claimForm.setTcrecovery(0.0D);
		claimForm.setWcrecovery(0.0D);
		claimForm.setTotalRecovery(0.0D);
		// System.out.println("Before jai code.....!");
		Vector arrylist = claimdetail.getCgpanDetails();
		FunctionForCgpanWiseFirstClaim(claimdetail, claimForm);
		Connection connection = DBConnection.getConnection();
		String qryufordata = (new StringBuilder())
				.append(" SELECT  P.PMR_CHIEF_GENDER,S.SSI_STATE_NAME, S.SSI_TYPE_OF_ACTIVITY,CM.SCM_NAME FROM SSI_DETAIL S,PROMOTER_DETAIL P,SCHEME_MASTER CM,APPLICATION_DETAIL A \n  WHERE BID=(SELECT BID FROM CLAIM_DETAIL_TEMP WHERE CLM_REF_NO='")
				.append(claimRefNumber)
				.append("')  \n ")
				.append(" AND P.SSI_REFERENCE_NUMBER=S.SSI_REFERENCE_NUMBER \n ")
				.append(" AND A.SSI_REFERENCE_NUMBER=S.SSI_REFERENCE_NUMBER \n ")
				.append(" AND CM.SCM_ID=A.SCM_ID ").toString();
		try {
			Statement strQury = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			for (ResultSet rsdata = strQury.executeQuery(qryufordata); rsdata
					.next(); claimForm.setNewSchemName(rsdata.getString(4))) {
				claimForm.setNewChipParmoGender(rsdata.getString(1));
				claimForm.setNewborowerState(rsdata.getString(2));
				claimForm.setNewTypeActivity(rsdata.getString(3));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String sateforTest = claimForm.getNewborowerState();
		String nerState = (new StringBuilder())
				.append("SELECT * FROM STATE_GR_MAP WHERE SCR_STATE_NAME='")
				.append(sateforTest).append("'").toString();
		try {
			Statement strQury1 = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rsdata1 = strQury1.executeQuery(nerState);
			String nerFlag;
			for (nerFlag = "N"; rsdata1.next(); nerFlag = "Y")
				;
			claimForm.setNewNERFlag(nerFlag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double tc_total_amt = claimdetail.getTotalTCOSAmountAsOnNPA();
		String falgforCasesafet = "N";
		for (Iterator interator = arrylist.iterator(); interator.hasNext();)
			try {
				Statement str = connection.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				HashMap hashmap = (HashMap) interator.next();
				String cgpanfordate = (String) hashmap.get("CGPAN");
				String queryNPAOutStanfAMt1 = (new StringBuilder())
						.append("SELECT TRM_AMOUNT_SANCTIONED_DT A, NULL  B FROM TERM_LOAN_DETAIL WHERE CGPAN='")
						.append(cgpanfordate)
						.append("' UNION ALL SELECT WCP_FB_LIMIT_SANCTIONED_DT A,WCP_NFB_LIMIT_SANCTIONED_DT B FROM WORKING_CAPITAL_DETAIL WHERE CGPAN='")
						.append(cgpanfordate).append("'").toString();
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int month = calendar.get(2);
				int day = calendar.get(5);
				int year = calendar.get(1);
				day = 2;
				month = 1;
				year = 2009;
				calendar.set(Calendar.MONTH, month);
				calendar.set(Calendar.DATE, day);
				calendar.set(Calendar.YEAR, year);
				Date caesesafter2009 = calendar.getTime();
				claimForm.setFalgforCasesafet(falgforCasesafet);
				ResultSet rs1 = str.executeQuery(queryNPAOutStanfAMt1);
				while (rs1.next()) {
					Date firstDate = null;
					firstDate = rs1.getDate(1);
					Date secondDate = null;
					secondDate = rs1.getDate(2);
					try {
						if (!firstDate.equals(null)
								&& firstDate.after(caesesafter2009)) {
							falgforCasesafet = "Y";
							claimForm.setFalgforCasesafet(falgforCasesafet);
							break;
						}
						if (secondDate.equals(null)
								|| !secondDate.after(caesesafter2009))
							continue;
						falgforCasesafet = "Y";
						claimForm.setFalgforCasesafet(falgforCasesafet);
						break;
					} catch (Exception we) {
						we.getMessage();
					}
				}
			} catch (Exception e) {
				e.getMessage();
				System.out.println((new StringBuilder())
						.append("the Exception is rised.....!")
						.append(e.getMessage()).toString());
			}

		return mapping.findForward("displayClaimRefNumDtlsPage");
	}

	public void FunctionForCgpanWiseFirstClaim(ClaimDetail claimdetail,
			ClaimActionForm claimForm) throws Exception {
		Vector arrylist = claimdetail.getCgpanDetails();
		ArrayList forCGPANWiseData = new ArrayList();
		ClaimApplication claimApplication = claimForm.getClaimapplication();
		Vector tld = claimApplication.getTermCapitalDtls();
		ArrayList wcd = claimApplication.getWorkingCapitalDtls();
		Vector recoDtl = claimApplication.getRecoveryDetails();
		ArrayList claimSummeryDtl = claimApplication.getClaimSummaryDtls();
		for (int i = 0; i < arrylist.size(); i++) {
			ClaimActionForm clmForm = new ClaimActionForm();
			HashMap hashmap = (HashMap) arrylist.get(i);
			String cgpanfordate = (String) hashmap.get("CGPAN");
			clmForm.setNewCGPAN(cgpanfordate);
			double tcApprovedAmnt = 0.0D;
			double wcApprovedAmnt = 0.0D;
			String loanType = (String) hashmap.get("LoanType");
			if (loanType.equals("TC") || loanType.equals("CC")) {
				tcApprovedAmnt = ((Double) hashmap.get("ApprovedAmount"))
						.doubleValue();
				clmForm.setNewGuarnteeIssueAmt(tcApprovedAmnt);
			} else if (loanType.equals("WC")) {
				wcApprovedAmnt = ((Double) hashmap.get("ApprovedAmount"))
						.doubleValue();
				clmForm.setNewGuarnteeIssueAmt(wcApprovedAmnt);
			}
			if (loanType.equals("TC") || loanType.equals("CC")) {
				for (int j = 0; j < tld.size(); j++) {
					TermLoanCapitalLoanDetail termLD = (TermLoanCapitalLoanDetail) tld
							.get(j);
					String cgpanfromTCGurnteeIssue = termLD.getCgpan();
					if (cgpanfordate.equals(cgpanfromTCGurnteeIssue))
						clmForm.setNewAmtOutstandAsOnNPA(termLD
								.getOutstandingAsOnDateOfNPA());
				}

			} else {
				for (int k = 0; k < wcd.size(); k++) {
					WorkingCapitalDetail workingLD = (WorkingCapitalDetail) wcd
							.get(k);
					String cgpanfromWCGurnteeIssue = workingLD.getCgpan();
					if (cgpanfordate.equals(cgpanfromWCGurnteeIssue))
						clmForm.setNewAmtOutstandAsOnNPA(workingLD
								.getOutstandingAsOnDateOfNPA());
				}

			}
			double tcRecoveryMade = 0.0D;
			double wcRecoveryMade = 0.0D;
			for (int rec = 0; rec < recoDtl.size(); rec++) {
				RecoveryDetails recDtl = (RecoveryDetails) recoDtl.get(rec);
				String recovryCgpan = recDtl.getCgpan();
				if (loanType.equals("TC") || loanType.equals("CC")) {
					if (cgpanfordate.equals(recovryCgpan)) {
						double tcInterestAndOtherCharges = recDtl
								.getTcInterestAndOtherCharges();
						double tcPrincipal = recDtl.getTcPrincipal();
						tcRecoveryMade = tcInterestAndOtherCharges
								+ tcPrincipal;
						clmForm.setNewAmtRecoverAfterNPA(tcRecoveryMade);
					}
				} else if (cgpanfordate.equals(recovryCgpan)) {
					double wcAmount = recDtl.getWcAmount();
					double wcOtherCharges = recDtl.getWcOtherCharges();
					wcRecoveryMade = wcAmount + wcOtherCharges;
					clmForm.setNewAmtRecoverAfterNPA(wcRecoveryMade);
				}
			}

			double tcClaimApplied = 0.0D;
			double wcClaimApplied = 0.0D;
			for (int clmsum = 0; clmsum < claimSummeryDtl.size(); clmsum++) {
				ClaimSummaryDtls clmSummaryDtl = (ClaimSummaryDtls) claimSummeryDtl
						.get(clmsum);
				String clmSummryCgpan = clmSummaryDtl.getCgpan();
				String panType = clmSummryCgpan.substring(11, 13);
				if (cgpanfordate.equals(clmSummryCgpan)) {
					tcClaimApplied = clmSummaryDtl.getAmount();
					clmForm.setNewAmtClaimByMli(tcClaimApplied);
				}
			}

			double tcServiceFee = 0.0D;
			double wcServiceFee = 0.0D;
			if (loanType.equals("TC") || loanType.equals("CC")) {
				tcServiceFee = ((Double) hashmap.get("TOTALSERVICEFEE"))
						.doubleValue();
				clmForm.setNewAmtDeductedFromMli(tcServiceFee);
			} else {
				wcServiceFee = ((Double) hashmap.get("TOTALSERVICEFEE"))
						.doubleValue();
				clmForm.setNewAmtDeductedFromMli(wcServiceFee);
			}
			forCGPANWiseData.add(clmForm);
		}

		claimForm.setForCGPANWiseDataArray(forCGPANWiseData);
	}

	public ActionForward displayClaimApprovalMod_Jagadeesh(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String flagClmRefDtl = claimForm.getClmRefDtlSet();
		java.sql.Date stDt = java.sql.Date.valueOf(DateHelper
				.stringToSQLdate(claimForm.getDateOfTheDocument36()));
		java.sql.Date endDt = java.sql.Date.valueOf(DateHelper
				.stringToSQLdate(claimForm.getDateOfTheDocument37()));
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		System.out.println((new StringBuilder())
				.append("ClaimAction displayClaimApproval() flagClmRefDtl :")
				.append(flagClmRefDtl).toString());
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("flagClmRefDtl :")
						.append(flagClmRefDtl).toString());
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String payAmntNow = null;
		String userRemarks = null;
		String microFlag = null;
		double tcServiceFee = 0.0D;
		double wcServiceFee = 0.0D;
		double tcClaimEligibleAmt = 0.0D;
		double wcClaimEligibleAmt = 0.0D;
		double tcFirstInstallment = 0.0D;
		double wcFirstInstallment = 0.0D;
		double totalTCOSAmountAsOnNPA = 0.0D;
		double totalWCOSAmountAsOnNPA = 0.0D;
		double tcrecovery = 0.0D;
		double wcrecovery = 0.0D;
		double tcIssued = 0.0D;
		double wcIssued = 0.0D;
		Administrator admin = new Administrator();
		String falgforCasesafet = "N";
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		User loggedUser = getUserInformation(request);
		String loggedUserId = loggedUser.getUserId();
		userIds.remove(loggedUserId);
		userIds.remove("ADMIN");
		if (userIds.contains("DEMOUSER"))
			userIds.remove("DEMOUSER");
		if (userIds.contains("AUDITOR"))
			userIds.remove("AUDITOR");
		claimForm.setUserIds(userIds);
		claimForm.setUserId(loggedUserId);
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		Date dateofReceipt = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String loggedUsr = user.getUserId();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		if (flagClmRefDtl != null && flagClmRefDtl.equals("Y")) {
			clmdtl = claimForm.getClaimdetail();
			if (clmdtl != null) {
				clmRefNum = clmdtl.getClaimRefNum();
				dateofReceipt = claimForm.getDateofReceipt();
				userRemarks = clmdtl.getComments();
				microFlag = claimForm.getMicroCategory();
				String nerfalg = claimForm.getNewNERFlag();
				String womenOprator = claimForm.getNewChipParmoGender();
				falgforCasesafet = claimForm.getFalgforCasesafet();
				ArrayList mainArry = new ArrayList();
				String hidcgpan[] = request.getParameterValues("hidcgpan");
				String hidgaurIssue[] = request
						.getParameterValues("hidgaurIssue");
				String outstandingAsOnNPA[] = request
						.getParameterValues("outstandingAsOnNPA");
				String recoverafterNPA[] = request
						.getParameterValues("recoverafterNPA");
				String netOutsandingAmt[] = request
						.getParameterValues("netOutsandingAmt");
				String hidclaimbymliamt[] = request
						.getParameterValues("hidclaimbymliamt");
				String claimEligibleAmt[] = request
						.getParameterValues("claimEligibleAmt");
				String firstInstallAmt[] = request
						.getParameterValues("firstInstallAmt");
				String dedecutByMliIfAny[] = request
						.getParameterValues("dedecutByMliIfAny");
				String paybleAmt[] = request.getParameterValues("paybleAmt");
				mainArry.add(hidcgpan);
				mainArry.add(hidgaurIssue);
				mainArry.add(outstandingAsOnNPA);
				mainArry.add(recoverafterNPA);
				mainArry.add(netOutsandingAmt);
				mainArry.add(hidclaimbymliamt);
				mainArry.add(claimEligibleAmt);
				mainArry.add(firstInstallAmt);
				mainArry.add(dedecutByMliIfAny);
				mainArry.add(paybleAmt);
				CPDAO cpdao1 = new CPDAO();
				payAmntNow = cpdao1.insertClaimProcessDetails1(clmRefNum,
						userRemarks, mainArry, dateofReceipt, microFlag,
						falgforCasesafet, clmdtl, nerfalg, womenOprator);
				if (payAmntNow != null && !payAmntNow.equals("")) {
					if (Double.parseDouble(payAmntNow) < 0.0D)
						payAmntNow = "0.0";
				} else {
					payAmntNow = "0.0";
				}
			}
		}
		// System.out.println("AFTER LOOP :---------------------");
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String thiskey = null;
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor.getClaimProcessingDetailsMod(
				"F", stDt, endDt);
		if (firstinstllmntclaims != null) {
			// System.out.println("Inside First Claim .....!");
			for (int i = 0; i < firstinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"##################");
					String memId = clmDtl.getMliId();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("memId :")
									.append(memId).toString());
					String claimrefnumber = clmDtl.getClaimRefNum();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("claimrefnumber :")
									.append(claimrefnumber).toString());
					clmStatus = clmDtl.getClmStatus();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("clmStatus :")
									.append(clmStatus).toString());
					comments = clmDtl.getComments();
					forwardedToUser = clmDtl.getForwaredToUser();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("forwardedToUser :")
									.append(forwardedToUser).toString());
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"###################");
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (clmStatus != null
							&& (clmStatus.equals("FW") || clmStatus
									.equals("HO"))) {
						thiskey = (new StringBuilder()).append("F#")
								.append(memId).append("#")
								.append(claimrefnumber).toString();
						claimForm.setDecision(thiskey, clmStatus);
						claimForm.setRemarks(thiskey, comments);
						Log.log(Log.INFO, "ClaimAction",
								"displayClaimApproval()", (new StringBuilder())
										.append("loggedUsr :")
										.append(loggedUsr).toString());
						if (forwardedToUser != null
								&& !forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							firstinstllmntclaims.remove(i);
							i--;
						}
						if (forwardedToUser != null
								&& forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("thiskey :")
											.append(thiskey).toString());
							Log.log(Log.INFO,
									"ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder())
											.append("forwardedToUser :")
											.append(forwardedToUser).toString());
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							claimForm.setForwardedToIds(thiskey,
									forwardedToUser);
							if (userIds.contains(forwardedToUser))
								claimForm.setUserIds(userIds);
						}
					}
				}
			}

		}
		Vector secinstllmntclaims = processor.getClaimProcessingDetails("S");
		if (secinstllmntclaims != null) {
			for (int i = 0; i < secinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else {
						String memId = clmDtl.getMliId();
						String claimrefnumber = clmDtl.getClaimRefNum();
						String cgclan = clmDtl.getCGCLAN();
						clmStatus = clmDtl.getClmStatus();
						comments = clmDtl.getComments();
						forwardedToUser = clmDtl.getForwaredToUser();
						Log.log(Log.INFO,
								"ClaimAction",
								"displayClaimApproval()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						if (clmStatus != null
								&& (clmStatus.equals("FW") || clmStatus
										.equals("HO"))) {
							thiskey = (new StringBuilder()).append("S#")
									.append(memId).append("#")
									.append(claimrefnumber).append("#")
									.append(cgclan).toString();
							claimForm.setDecision(thiskey, clmStatus);
							claimForm.setRemarks(thiskey, comments);
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("loggedUsr :")
											.append(loggedUsr).toString());
							if (forwardedToUser != null
									&& !forwardedToUser
											.equalsIgnoreCase(loggedUsr)) {
								secinstllmntclaims.remove(i);
								i--;
							}
							if (forwardedToUser != null
									&& forwardedToUser
											.equalsIgnoreCase(loggedUsr))
								claimForm.setForwardedToIds(thiskey,
										forwardedToUser);
						}
					}
				}
			}

		}
		if (firstinstllmntclaims.size() == 0 && secinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to process.");
			return mapping.findForward("success");
		}
		for (int i = 0; i < firstinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) firstinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) firstinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				cd.setComments(userRemarks);
				firstinstllmntclaims.addElement(cd);
			}
		}

		for (int i = 0; i < secinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) secinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) secinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				secinstllmntclaims.addElement(cd);
			}
		}

		clmdtl = null;
		clmRefNum = null;
		payAmntNow = null;
		claimForm.setLimit(outOfLimit);
		claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
		claimForm.setFirstCounter(firstinstllmntclaims.size());
		claimForm.setSecondInstallmentClaims(secinstllmntclaims);
		claimForm.setSecondCounter(secinstllmntclaims.size());
		claimForm.setClmRefDtlSet("N");
		firstinstllmntclaims = null;
		secinstllmntclaims = null;
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()", "Exited");
		return mapping.findForward("displayClaimsApprovalPage");
	}

	public ActionForward getRecoveryAfterOTS(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "getRecoveryrots", "Entered");
		ClaimActionForm clmActionform = (ClaimActionForm) form;
		clmActionform.setCp_ots_enterMember("");
		clmActionform.setCp_ots_enterCgpan("");
		clmActionform.setCp_ots_appRefNo("");
		Log.log(Log.INFO, "ClaimAction", "getRecoveryrots", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward getRecoveryAfterOTSDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String memberId;
		String cgpan;
		String clmRefNo;
		ActionForm dynaForm;
		Connection connection;
		Log.log(Log.INFO, "ClaimAction", "getRecoveryAfterOTSDetail", "Entered");
		ClaimActionForm clmActionform = (ClaimActionForm) form;
		memberId = "";
		cgpan = "";
		clmRefNo = "";
		dynaForm = form;
		connection = DBConnection.getConnection();
		try {
			memberId = clmActionform.getCp_ots_enterMember();
			cgpan = clmActionform.getCp_ots_enterCgpan();
			clmRefNo = clmActionform.getCp_ots_appRefNo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!memberId.equals(""))
			try {
				validMemberId(memberId);
				String mainQuery = null;
				if (!cgpan.equals("")) {
					validCGPAN(cgpan);
					cgpanmemberAsso(memberId, cgpan);
					String retrclmRef = getClmRefByCgpan(memberId, cgpan);
					mainQuery = (new StringBuilder())
							.append("select clm_mli_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,c.clm_ref_no,ssi_unit_name, pmr_chief_gender  \n gender,app_mse_type ,npa_effective_dt npa_dt,a.cgpan, a.app_approved_date_time approved_dt,\n decode(a.app_reapprove_amount,null,a.app_approved_amount,a.app_reapprove_amount)\n approved_amt ,trunc(clm_approved_dt)\n from claim_detail c,ssi_detail s,npa_detail n,application_detail a,promoter_detail p,claim_application_amount CA \n where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("'                                                        \n")
							.append(" and s.bid=c.BID  and n.bid=c.bid \n")
							.append(" and a.ssi_reference_number=s.ssi_reference_number \n")
							.append(" and s.ssi_reference_number=p.ssi_reference_number  \n")
							.append(" AND A.cgpan=ca.cgpan \n")
							.append(" and app_status!='RE' \n")
							.append(" and c.clm_ref_no='")
							.append(retrclmRef)
							.append("' \n")
							.append(" union\n")
							.append(" select clm_mli_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,c.clm_ref_no,ssi_unit_name, pmr_chief_gender  \n")
							.append(" gender,app_mse_type ,npa_effective_dt npa_dt,a.cgpan, a.app_approved_date_time approved_dt,\n")
							.append(" decode(a.app_reapprove_amount,null,a.app_approved_amount,a.app_reapprove_amount)\n")
							.append(" approved_amt ,trunc(clm_approved_dt)\n")
							.append(" from claim_detail c,ssi_detail s,npa_detail_temp n,application_detail a,promoter_detail p,claim_application_amount CA \n")
							.append(" where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("'                                                        \n")
							.append(" and s.bid=c.BID  and n.bid=c.bid \n")
							.append(" and a.ssi_reference_number=s.ssi_reference_number \n")
							.append(" and s.ssi_reference_number=p.ssi_reference_number  \n")
							.append(" AND A.cgpan=ca.cgpan \n")
							.append(" and app_status!='RE' \n")
							.append(" and c.clm_ref_no='").append(retrclmRef)
							.append("'").toString();
				}
				if (!clmRefNo.equals("")) {
					validClmRefNo(clmRefNo, memberId);
					mainQuery = (new StringBuilder())
							.append("select clm_mli_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,c.clm_ref_no,ssi_unit_name, pmr_chief_gender  \n gender,app_mse_type ,npa_effective_dt npa_dt,a.cgpan, a.app_approved_date_time approved_dt,\n decode(a.app_reapprove_amount,null,a.app_approved_amount,a.app_reapprove_amount)\n approved_amt ,trunc(clm_approved_dt)\n from claim_detail c,ssi_detail s,npa_detail n,application_detail a,promoter_detail p,claim_application_amount CA \n where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("' \n")
							.append(" and s.bid=c.BID  and n.bid=c.bid \n")
							.append(" and a.ssi_reference_number=s.ssi_reference_number \n")
							.append(" and s.ssi_reference_number=p.ssi_reference_number  \n")
							.append(" AND A.cgpan=ca.cgpan \n")
							.append(" and app_status!='RE' \n")
							.append(" and c.clm_ref_no='")
							.append(clmRefNo)
							.append("' \n")
							.append(" union\n")
							.append(" select clm_mli_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id,c.clm_ref_no,ssi_unit_name, pmr_chief_gender  \n")
							.append(" gender,app_mse_type ,npa_effective_dt npa_dt,a.cgpan, a.app_approved_date_time approved_dt,\n")
							.append(" decode(a.app_reapprove_amount,null,a.app_approved_amount,a.app_reapprove_amount)\n")
							.append(" approved_amt ,trunc(clm_approved_dt)\n")
							.append(" from claim_detail c,ssi_detail s,npa_detail_temp n,application_detail a,promoter_detail p,claim_application_amount CA \n")
							.append(" where c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
							.append(memberId)
							.append("'\n")
							.append(" and s.bid=c.BID  and n.bid=c.bid \n")
							.append(" and a.ssi_reference_number=s.ssi_reference_number \n")
							.append(" and s.ssi_reference_number=p.ssi_reference_number  \n")
							.append(" AND A.cgpan=ca.cgpan \n")
							.append(" and app_status!='RE' \n")
							.append(" and c.clm_ref_no='").append(clmRefNo)
							.append("'").toString();
				}
				Statement str = connection.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rsForData = str.executeQuery(mainQuery);
				ArrayList claimOtsDataArr = new ArrayList();
				if (!rsForData.next())
					throw new NoMemberFoundException(
							"No data Found for Entred Details ");
				rsForData.beforeFirst();
				ClaimActionForm claimActionForm = new ClaimActionForm();
				ClaimActionForm claimActionFormmain;
				for (; rsForData.next(); claimOtsDataArr
						.add(claimActionFormmain)) {
					claimActionFormmain = new ClaimActionForm();
					claimActionForm.setCp_ots_mliName(rsForData.getString(1));
					claimActionForm.setCp_ots_enterMember(rsForData
							.getString(2));
					claimActionForm.setCp_ots_appRefNo(rsForData.getString(3));
					claimActionForm.setCp_ots_unitName(rsForData.getString(4));
					claimActionForm.setCp_ots_gender(rsForData.getString(5));
					String flagValu = rsForData.getString(6);
					if (flagValu == null)
						flagValu = "N";
					claimActionForm.setCp_ots_UnitAssitByMSE(flagValu);
					claimActionForm.setCp_ots_npaDate(rsForData.getDate(7));
					claimActionFormmain.setCp_ots_enterCgpan(rsForData
							.getString(8));
					claimActionForm.setCp_ots_firstInstallDate(rsForData
							.getDate(9));
					claimActionFormmain.setCp_ots_totAmount(rsForData
							.getDouble(10));
					claimActionForm.setCp_ots_clmappDate(rsForData.getDate(11));
				}

				claimActionForm.setClaimdeatilforOts(claimOtsDataArr);
				ClaimActionForm clmactionFm = FunctionExcelXYZ(claimActionForm,
						cgpan, request);
				BeanUtils.copyProperties(dynaForm, clmactionFm);
				HttpSession session = request.getSession(true);
				session.setAttribute("claimActionFormdetail", claimActionForm);
			} finally {
				DBConnection.freeConnection(connection);
			}
		Log.log(Log.INFO, "ClaimAction", "getRecoveryAfterOTSDetail", "Exited");
		return mapping.findForward("detailsuccess");
	}

	void validMemberId(String enteredMemberId) throws Exception {
		Connection connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String one = enteredMemberId.substring(0, 4);
			String two = enteredMemberId.substring(4, 8);
			String three = enteredMemberId.substring(8, 12);
			String MemIdavailbel = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from member_info c \nwhere  c.mem_bnk_id='")
					.append(one).append("' and\n").append("c.mem_zne_id='")
					.append(two).append("' and\n").append("c.mem_brn_id='")
					.append(three).append("'").toString();
			ResultSet rsforavailmemid = str.executeQuery(MemIdavailbel);
			if (!rsforavailmemid.next())
				throw new NoMemberFoundException(" Member Id  Not Exsist : ");
			rsforavailmemid.close();
			rsforavailmemid = null;
		} finally {
			DBConnection.freeConnection(connection);
			// System.out.println("Inside finally");
		}
		return;
	}

	void validCGPAN(String cgpan) throws Exception {
		Connection connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String cgQury = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='")
					.append(cgpan).append("'").toString();
			ResultSet rsforvalid = str.executeQuery(cgQury);
			String memberid = "";
			if (!rsforvalid.next())
				throw new NoMemberFoundException(" CGPAN  Not Exsist.  ");
		} finally {
			DBConnection.freeConnection(connection);
		}
		return;
	}

	void cgpanmemberAsso(String memberId, String cgpan) throws Exception {
		Connection connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String cgQury = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='")
					.append(cgpan).append("'").toString();
			ResultSet rsforvalid = str.executeQuery(cgQury);
			String memberid = "";
			if (!rsforvalid.next())
				throw new NoMemberFoundException(" CGPAN  Not Exsist.  ");
			rsforvalid.beforeFirst();
			while (rsforvalid.next())
				memberid = rsforvalid.getString(1);
			if (!memberid.equals(memberId))
				throw new NoMemberFoundException(
						" Enterd Member Id and CGPAN  Not Associated. ");
		} finally {
			DBConnection.freeConnection(connection);
		}
		return;
	}

	String getClmRefByCgpan(String memberId, String cgpan) throws Exception {
		String clmrefretrived;
		Connection connection;
		clmrefretrived = null;
		connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String qury1 = (new StringBuilder())
					.append("select clm_Ref_no from claim_detail\nwhere bid in\n(select s.bid from ssi_detail s,application_detail a\nwhere a.ssi_Reference_number = s.ssi_Reference_number\nand a.cgpan = '")
					.append(cgpan).append("'\n")
					.append("and  a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id='")
					.append(memberId).append("')").toString();
			ResultSet rs1 = str.executeQuery(qury1);
			if (!rs1.next())
				throw new NoMemberFoundException(
						"Claim Application Not Approved  ");
			rs1.beforeFirst();
			while (rs1.next())
				clmrefretrived = rs1.getString(1);
			rs1.close();
			rs1 = null;
		} finally {
			DBConnection.freeConnection(connection);
		}
		return clmrefretrived;
	}

	public void validClmRefNo(String clmrefNo, String memberId)
			throws Exception {
		ResultSet rsforvalid = null;
		Connection connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String cgQury = (new StringBuilder())
					.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from claim_detail c where clm_ref_no='")
					.append(clmrefNo).append("'").toString();
			rsforvalid = str.executeQuery(cgQury);
			String memberid = "";
			if (!rsforvalid.next())
				throw new NoMemberFoundException(
						"Claim Ref Number provided is not Approved.  ");
			rsforvalid.beforeFirst();
			while (rsforvalid.next())
				memberid = rsforvalid.getString(1);
			if (!memberid.equals(memberId))
				throw new NoMemberFoundException(
						"Claim Ref Number and Member ID not Associated.  ");
		} finally {
			rsforvalid.close();
			rsforvalid = null;

			DBConnection.freeConnection(connection);
		}
		return;
	}

	public ClaimActionForm FunctionExcelXYZ(ClaimActionForm claimActionform1,
			String cgpan, HttpServletRequest request) throws Exception {
		double total;
		double npaTotal;
		double recovryTotal;
		double recoveryAmt;
		double npaAmount;
		ArrayList claimActionObjList;
		ArrayList claimActionforstore;
		String clmrefNo;
		Connection connection;
		double totalforcal = 0.0D;
		total = 0.0D;
		npaTotal = 0.0D;
		recovryTotal = 0.0D;
		String NPACgpan = "";
		String recoveryCgpan = "";
		String cgpanRetrive = "";
		double cgpanGaurnteeAmt = 0.0D;
		double guarnteeAmt = 0.0D;
		recoveryAmt = 0.0D;
		npaAmount = 0.0D;
		double totNetOutstandingAmount = 0.0D;
		double netOutstandingAmt = 0.0D;
		ClaimActionForm claimActionformforaddobject = new ClaimActionForm();
		claimActionObjList = new ArrayList();
		claimActionforstore = new ArrayList();
		claimActionObjList = claimActionform1.getClaimdeatilforOts();
		for (int i = 0; i < claimActionObjList.size(); i++) {
			ClaimActionForm claimOBJ1 = (ClaimActionForm) claimActionObjList
					.get(i);
			totalforcal += claimOBJ1.getCp_ots_totAmount();
		}

		total = totalforcal;
		claimActionform1.setCp_ots_Total(total);
		clmrefNo = claimActionform1.getCp_ots_appRefNo();
		connection = DBConnection.getConnection();
		try {
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String queryNPAOutStanfAMt1 = (new StringBuilder())
					.append("select clm_ref_no,cgpan,ctd_npa_outstanding_amt from claim_tc_detail where clm_ref_no='")
					.append(clmrefNo)
					.append("'\n")
					.append("union\n")
					.append("select clm_ref_no,cgpan,cwd_npa_outstanding_amt  from claim_wc_detail where clm_ref_no='")
					.append(clmrefNo).append("'").toString();
			for (ResultSet rs1 = str.executeQuery(queryNPAOutStanfAMt1); rs1
					.next();) {
				NPACgpan = rs1.getString(2);
				npaTotal += rs1.getDouble(3);
			}

			claimActionform1.setCp_ots_npatotal(npaTotal);
			String qryrecbeforefirinstallrealse = (new StringBuilder())
					.append("select cgpan,crd_tc_principal_amt from claim_recovery_detail where clm_ref_no='")
					.append(clmrefNo).append("'").toString();
			for (ResultSet rsforRecov = str
					.executeQuery(qryrecbeforefirinstallrealse); rsforRecov
					.next();) {
				recoveryCgpan = rsforRecov.getString(1);
				recovryTotal += rsforRecov.getDouble(2);
			}

			claimActionform1.setCp_ots_cp_ots_totRecAMt(recovryTotal);
			double fornetoutst = Math.min(total, npaTotal);
			totNetOutstandingAmount = fornetoutst - recovryTotal;
			int arrysixe = claimActionObjList.size();
			for (int i = 0; i < arrysixe; i++) {
				ClaimActionForm claimActionform = new ClaimActionForm();
				ClaimActionForm claimOBJ = (ClaimActionForm) claimActionObjList
						.get(i);
				cgpanRetrive = claimOBJ.getCp_ots_enterCgpan();
				claimActionform.setCp_ots_enterCgpan(cgpanRetrive);
				cgpanGaurnteeAmt = claimOBJ.getCp_ots_totAmount();
				claimActionform.setCp_ots_cgpanGaurnteeAmt(cgpanGaurnteeAmt);
				String queryNPAOutStanfAMt = (new StringBuilder())
						.append("select clm_ref_no,cgpan,ctd_npa_outstanding_amt from claim_tc_detail where cgpan='")
						.append(cgpanRetrive)
						.append("'\n")
						.append("union\n")
						.append("select clm_ref_no,cgpan,cwd_npa_outstanding_amt  from claim_wc_detail where cgpan='")
						.append(cgpanRetrive).append("'").toString();
				for (ResultSet rs2 = str.executeQuery(queryNPAOutStanfAMt); rs2
						.next();)
					npaAmount = rs2.getDouble(3);

				claimActionform.setCp_ots_npaAmount(npaAmount);
				String qryrecbeforefirinstallrealse1 = (new StringBuilder())
						.append("select cgpan,crd_tc_principal_amt from claim_recovery_detail where cgpan='")
						.append(cgpanRetrive).append("'").toString();
				ResultSet rsforRecov1 = str
						.executeQuery(qryrecbeforefirinstallrealse1);
				rsforRecov1.beforeFirst();
				while (rsforRecov1.next()) {
					String cgpanforchek = rsforRecov1.getString(1);
					recoveryAmt = rsforRecov1.getDouble(2);
				}
				claimActionform.setCp_ots_recoveryAmt(recoveryAmt);
				double minofA_B = Math.min(cgpanGaurnteeAmt, npaAmount);
				netOutstandingAmt = minofA_B - recoveryAmt;
				claimActionform.setCp_ots_netOutstanding(netOutstandingAmt);
				String queryforguarnteeStartDate = (new StringBuilder())
						.append("select app_guar_start_date_time from application_detail where cgpan='")
						.append(cgpanRetrive).append("'").toString();
				ResultSet rs = str.executeQuery(queryforguarnteeStartDate);
				Date guarsatDtae;
				for (guarsatDtae = null; !rs.next();)
					throw new NoMemberFoundException(
							" Guarntee Start Date is Null. ");

				rs.beforeFirst();
				while (rs.next())
					guarsatDtae = rs.getDate(1);
				Date guranteeStartDate = guarsatDtae;
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int month = calendar.get(2);
				int day = calendar.get(5);
				int year = calendar.get(1);
				month = 1;
				day = 2;
				year = 2009;
				calendar.set(2, month);
				calendar.set(5, day);
				calendar.set(1, year);
				Date condtionalDate = calendar.getTime();
				String retGender = claimActionform1.getCp_ots_gender();
				String Gender = "";
				if (retGender.equals("M"))
					Gender = "male";
				else if (retGender.equals("F"))
					Gender = "female";
				boolean MicroFlag = false;
				boolean NERflag = false;
				String schemQery = (new StringBuilder())
						.append("select CLM_IS_MICRO_FLAG from claim_detail where clm_ref_no='")
						.append(clmrefNo).append("'").toString();
				ResultSet scmRs = str.executeQuery(schemQery);
				String schmId = "";
				if (!scmRs.next())
					MicroFlag = false;
				scmRs.beforeFirst();
				try {
					while (scmRs.next())
						schmId = scmRs.getString(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (schmId != null && schmId.equals("Y"))
					MicroFlag = true;
				claimActionform.setCp_ots_microfalg(MicroFlag);
				String stateQery = (new StringBuilder())
						.append("SELECT SSI_STATE_NAME  FROM SSI_DETAIL WHERE BID IN(SELECT BID FROM CLAIM_DETAIL WHERE CLM_REF_NO= '")
						.append(clmrefNo).append("')").toString();
				ResultSet stateRs = str.executeQuery(stateQery);
				String StateName = "";
				while (!stateRs.next())
					NERflag = false;
				stateRs.beforeFirst();
				while (stateRs.next())
					StateName = stateRs.getString(1);
				String compStateQry = (new StringBuilder())
						.append("SELECT SCR_STATE_NAME FROM STATE_GR_MAP WHERE SCR_STATE_NAME='")
						.append(StateName).append("'").toString();
				ResultSet compStateRs = str.executeQuery(compStateQry);
				if (!compStateRs.next())
					NERflag = false;
				compStateRs.beforeFirst();
				while (compStateRs.next())
					NERflag = true;
				double TotalGuarnteeAmt = total;
				double NetOutsatndingAmt = totNetOutstandingAmount;
				double TotalLiableAmt = 0.0D;
				double liableAmt = 0.0D;
				Date guaranteeSanctionedYear = guranteeStartDate;
				double liablepercent = 0.0D;
				if (guaranteeSanctionedYear.before(condtionalDate))
					if (MicroFlag) {
						if (TotalGuarnteeAmt <= 500000D) {
							liableAmt = (netOutstandingAmt * 80D) / 100D;
							liableAmt = Math.round(liableAmt);
							claimActionform.setCp_ots_liableamount(liableAmt);
							claimActionform
									.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
							liablepercent = 80D;
							claimActionform
									.setCp_ots_liablePercent(liablepercent);
						} else if (TotalGuarnteeAmt >= 500001D
								&& TotalGuarnteeAmt <= 5000000D)
							if (NERflag = true) {
								liableAmt = (netOutstandingAmt * 80D) / 100D;
								liableAmt = Math.round(liableAmt);
								TotalLiableAmt = Math.round(TotalLiableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							} else if (NERflag = false)
								if (Gender == "male") {
									liableAmt = (netOutstandingAmt * 75D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								} else if (Gender == "female") {
									liableAmt = (netOutstandingAmt * 80D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
						if (TotalGuarnteeAmt >= 5000001D
								&& TotalGuarnteeAmt <= 10000000D)
							if (NERflag = true) {
								double AmtForpercentTC = (netOutstandingAmt * 5000000D)
										/ TotalGuarnteeAmt;
								double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
								double amtforsecondpersentTC = NetOutsatndingAmt
										- AmtForpercentTC;
								double persecondhalfTC = amtforsecondpersentTC / 2D;
								liableAmt = peroffirsthalfTC + persecondhalfTC;
								liableAmt = Math.round(liableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							} else if (NERflag = false) {
								if (Gender == "male") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 75D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
					} else if (!MicroFlag) {
						if (TotalGuarnteeAmt <= 5000000D)
							if (NERflag) {
								liableAmt = (netOutstandingAmt * 80D) / 100D;
								liableAmt = Math.round(liableAmt);
								TotalLiableAmt = Math.round(TotalLiableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							} else if (!NERflag) {
								if (Gender == "male") {
									liableAmt = (netOutstandingAmt * 75D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									liableAmt = (netOutstandingAmt * 80D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
						if (TotalGuarnteeAmt >= 5000001D
								&& TotalGuarnteeAmt <= 10000000D) {
							if (NERflag) {
								double AmtForpercentTC = (netOutstandingAmt * 5000000D)
										/ TotalGuarnteeAmt;
								double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
								double amtforsecondpersentTC = NetOutsatndingAmt
										- AmtForpercentTC;
								double persecondhalfTC = amtforsecondpersentTC / 2D;
								liableAmt = peroffirsthalfTC + persecondhalfTC;
								liableAmt = Math.round(liableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							}
							if (!NERflag) {
								if (Gender == "male") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 75D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
						}
					}
				if (guaranteeSanctionedYear.after(condtionalDate))
					if (MicroFlag) {
						if (TotalGuarnteeAmt <= 500000D) {
							liableAmt = (netOutstandingAmt * 85D) / 100D;
							liableAmt = Math.round(liableAmt);
							TotalLiableAmt = Math.round(TotalLiableAmt);
							claimActionform.setCp_ots_liableamount(liableAmt);
							claimActionform
									.setCp_ots_liabtext("CGTMSE's Liability (85% * Net Outstanding )");
							liablepercent = 85D;
							claimActionform
									.setCp_ots_liablePercent(liablepercent);
						} else if (TotalGuarnteeAmt >= 500001D
								&& TotalGuarnteeAmt <= 5000000D) {
							if (NERflag) {
								liableAmt = (netOutstandingAmt * 80D) / 100D;
								liableAmt = Math.round(liableAmt);
								TotalLiableAmt = Math.round(TotalLiableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							}
							if (!NERflag)
								if (Gender == "male") {
									liableAmt = (netOutstandingAmt * 75D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								} else if (Gender == "female") {
									liableAmt = (netOutstandingAmt * 80D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
						} else if (TotalGuarnteeAmt >= 5000001D
								&& TotalGuarnteeAmt <= 10000000D)
							if (NERflag) {
								double AmtForpercentTC = (netOutstandingAmt * 5000000D)
										/ TotalGuarnteeAmt;
								double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
								double amtforsecondpersentTC = NetOutsatndingAmt
										- AmtForpercentTC;
								double persecondhalfTC = amtforsecondpersentTC / 2D;
								liableAmt = peroffirsthalfTC + persecondhalfTC;
								liableAmt = Math.round(liableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							} else if (!NERflag) {
								if (Gender == "male") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 75D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
					} else if (!MicroFlag) {
						if (TotalGuarnteeAmt <= 5000000D)
							if (NERflag) {
								liableAmt = (netOutstandingAmt * 80D) / 100D;
								liableAmt = Math.round(liableAmt);
								TotalLiableAmt = Math.round(TotalLiableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							} else if (!NERflag) {
								if (Gender == "male") {
									liableAmt = (netOutstandingAmt * 75D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									liableAmt = (netOutstandingAmt * 80D) / 100D;
									liableAmt = Math.round(liableAmt);
									TotalLiableAmt = Math.round(TotalLiableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
						if (TotalGuarnteeAmt >= 5000001D
								&& TotalGuarnteeAmt <= 10000000D) {
							if (NERflag) {
								double AmtForpercentTC = (netOutstandingAmt * 5000000D)
										/ TotalGuarnteeAmt;
								double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
								double amtforsecondpersentTC = NetOutsatndingAmt
										- AmtForpercentTC;
								double persecondhalfTC = amtforsecondpersentTC / 2D;
								liableAmt = peroffirsthalfTC + persecondhalfTC;
								liableAmt = Math.round(liableAmt);
								claimActionform
										.setCp_ots_liableamount(liableAmt);
								claimActionform
										.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
								liablepercent = 80D;
								claimActionform
										.setCp_ots_liablePercent(liablepercent);
							}
							if (!NERflag) {
								if (Gender == "male") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 75D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (75% * Net Outstanding )");
									liablepercent = 75D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
								if (Gender == "female") {
									double AmtForpercentTC = (netOutstandingAmt * 5000000D)
											/ TotalGuarnteeAmt;
									double peroffirsthalfTC = (AmtForpercentTC * 80D) / 100D;
									double amtforsecondpersentTC = NetOutsatndingAmt
											- AmtForpercentTC;
									double persecondhalfTC = amtforsecondpersentTC / 2D;
									liableAmt = peroffirsthalfTC
											+ persecondhalfTC;
									liableAmt = Math.round(liableAmt);
									claimActionform
											.setCp_ots_liableamount(liableAmt);
									claimActionform
											.setCp_ots_liabtext("CGTMSE's Liability (80% * Net Outstanding )");
									liablepercent = 80D;
									claimActionform
											.setCp_ots_liablePercent(liablepercent);
								}
							}
						}
					}
				double fistIntallpaid = (liableAmt * 75D) / 100D;
				fistIntallpaid = Math.round(fistIntallpaid);
				claimActionform.setCp_ots_firstIntalpaidAmount(fistIntallpaid);
				String quRecovdata = (new StringBuilder())
						.append("SELECT  CGPAN,CCRD_RECOVERY_AMOUNT,CCRD_EXPENSES_DEDUCTED,CCRD_RECOVERY_EXPENSES,CCRD_NET_RECOVERY FROM CLM_CGPAN_RECOVERY_DETAIL WHERE CGPAN='")
						.append(cgpanRetrive).append("'").toString();
				ResultSet rsForRecData = str.executeQuery(quRecovdata);
				double totalRecAmtAfterclaimlodge = 0.0D;
				double totDedctAmt = 0.0D;
				double totNotDedctAmt = 0.0D;
				String expeDeduct = "";
				double netRecovryAmtfordedct = 0.0D;
				double netRecovryAmt = 0.0D;
				while (rsForRecData.next()) {
					totalRecAmtAfterclaimlodge += rsForRecData.getDouble(2);
					expeDeduct = rsForRecData.getString(3);
					if (expeDeduct.equals("Y"))
						totDedctAmt += rsForRecData.getDouble(4);
					else if (expeDeduct.equals("N"))
						totNotDedctAmt += rsForRecData.getDouble(4);
					netRecovryAmtfordedct += rsForRecData.getDouble(5);
				}
				totalRecAmtAfterclaimlodge = Math
						.round(totalRecAmtAfterclaimlodge);
				claimActionform
						.setCp_ots_totRecAftFirInst(totalRecAmtAfterclaimlodge);
				totDedctAmt = Math.round(totDedctAmt);
				claimActionform.setCp_ots_totDedtctAmt(totDedctAmt);
				totNotDedctAmt = Math.round(totNotDedctAmt);
				claimActionform.setCp_ots_totNotDedtctAmt(totNotDedctAmt);
				netRecovryAmt = netRecovryAmtfordedct - totDedctAmt;
				netRecovryAmt = Math.round(netRecovryAmt);
				claimActionform.setCp_ots_netRecovAmt(netRecovryAmt);
				double amountinDefoult = netOutstandingAmt - netRecovryAmt;
				amountinDefoult = Math.round(amountinDefoult);
				claimActionform.setCp_ots_netAmountInDefoault(amountinDefoult);
				double secintTot = totNetOutstandingAmount - netRecovryAmt;
				double secintAmt = netOutstandingAmt - netRecovryAmt;
				double totSecInstal = 0.0D;
				double secintallmentAmt = 0.0D;
				if (secintTot >= 5000001D && secintTot <= 10000000D) {
					if (NERflag = true) {
						double secInstaTC = (secintAmt * 5000000D) / secintTot;
						double firstHalfInsatTc = (secInstaTC * 80D) / 100D;
						double forcalInsPerTc = secintAmt - secInstaTC;
						double secondhalfInsatTC = forcalInsPerTc / 2D;
						totSecInstal = firstHalfInsatTc + secondhalfInsatTC;
						totSecInstal = Math.round(totSecInstal);
						claimActionform.setCp_ots_secIntalAMt(totSecInstal);
						String secIntalhalf = (new StringBuilder())
								.append("Second installment payable by CGTMSE (")
								.append(liablepercent).toString();
						String secIntalString = secIntalhalf
								.concat("% Of final loss minus 1st insatl remitted by CGTMSE)");
						claimActionform
								.setCp_ots_seconInsatlText(secIntalString);
					} else if (NERflag = false) {
						if (Gender == "male") {
							double secInstaTC = (secintAmt * 5000000D)
									/ secintTot;
							double firstHalfInsatTc = (secInstaTC * 75D) / 100D;
							double forcalInsPerTc = secintAmt - secInstaTC;
							double secondhalfInsatTC = forcalInsPerTc / 2D;
							totSecInstal = firstHalfInsatTc + secondhalfInsatTC;
							totSecInstal = Math.round(totSecInstal);
							claimActionform.setCp_ots_secIntalAMt(totSecInstal);
							String secIntalhalf = (new StringBuilder())
									.append("Second installment payable by CGTMSE (")
									.append(liablepercent).toString();
							String secIntalString = secIntalhalf
									.concat("% Of final loss minus 1st insatl remitted by CGTMSE)");
							claimActionform
									.setCp_ots_seconInsatlText(secIntalString);
						}
						if (Gender == "female") {
							double secInstaTC = (secintAmt * 5000000D)
									/ secintTot;
							double firstHalfInsatTc = (secInstaTC * 80D) / 100D;
							double forcalInsPerTc = secintAmt - secInstaTC;
							double secondhalfInsatTC = forcalInsPerTc / 2D;
							totSecInstal = firstHalfInsatTc + secondhalfInsatTC;
							totSecInstal = Math.round(totSecInstal);
							claimActionform.setCp_ots_secIntalAMt(totSecInstal);
							String secIntalhalf = (new StringBuilder())
									.append("Second installment payable by CGTMSE (")
									.append(liablepercent).toString();
							String secIntalString = secIntalhalf
									.concat("% Of final loss minus 1st insatl remitted by CGTMSE)");
							claimActionform
									.setCp_ots_seconInsatlText(secIntalString);
						}
					}
				} else {
					secintallmentAmt = (secintAmt * liablepercent) / 100D
							- fistIntallpaid;
					secintallmentAmt = Math.round(secintallmentAmt);
					claimActionform.setCp_ots_secIntalAMt(secintallmentAmt);
					String secIntalhalf = (new StringBuilder())
							.append("Second installment payable by CGTMSE (")
							.append(liablepercent).toString();
					String secIntalString = secIntalhalf
							.concat(" % Of final loss minus 1st insatl remitted by CGTMSE)");
					claimActionform.setCp_ots_seconInsatlText(secIntalString);
				}
				double totpayout = recoveryAmt + secintallmentAmt + totDedctAmt;
				claimActionform.setCp_ots_finalPayout(totpayout);
				claimActionforstore.add(claimActionform);
				recoveryAmt = 0.0D;
				totDedctAmt = 0.0D;
				totNotDedctAmt = 0.0D;
			}

			claimActionform1.setLoopobjtData(claimActionforstore);
			ArrayList x = claimActionform1.getLoopobjtData();
			double guarnteetotal = 0.0D;
			double npatotalAmt = 0.0D;
			double recovTotal = 0.0D;
			double netOutstandingTotal = 0.0D;
			double liavleAmtTotal = 0.0D;
			double firstInstallmentPaidTotal = 0.0D;
			double recafterfirstinstallTotal = 0.0D;
			double totaDecdectAMt = 0.0D;
			double totNotdedctAmt = 0.0D;
			double totnetRecovAmt = 0.0D;
			double netAmtindefoultTotal = 0.0D;
			double secinstamentAmtTotal = 0.0D;
			double finalPayoutAmtTotal = 0.0D;
			for (int i = 0; i < x.size(); i++) {
				ClaimActionForm clm = (ClaimActionForm) x.get(i);
				guarnteetotal += clm.getCp_ots_cgpanGaurnteeAmt();
				npatotalAmt += clm.getCp_ots_npaAmount();
				recovTotal += clm.getCp_ots_recoveryAmt();
				netOutstandingTotal += clm.getCp_ots_netOutstanding();
				liavleAmtTotal += clm.getCp_ots_liableamount();
				firstInstallmentPaidTotal += clm
						.getCp_ots_firstIntalpaidAmount();
				recafterfirstinstallTotal += clm.getCp_ots_totRecAftFirInst();
				totaDecdectAMt += clm.getCp_ots_totDedtctAmt();
				totNotdedctAmt += clm.getCp_ots_totNotDedtctAmt();
				totnetRecovAmt += clm.getCp_ots_netRecovAmt();
				netAmtindefoultTotal += clm.getCp_ots_netAmountInDefoault();
				secinstamentAmtTotal += clm.getCp_ots_secIntalAMt();
				finalPayoutAmtTotal += clm.getCp_ots_finalPayout();
			}

			claimActionform1.setCp_ots_guarnteetotal(guarnteetotal);
			claimActionform1.setCp_ots_npatotalAmt(npatotalAmt);
			claimActionform1.setCp_ots_recovTotal(recovTotal);
			claimActionform1.setCp_ots_netOutstandingTotal(netOutstandingTotal);
			claimActionform1.setCp_ots_liavleAmtTotal(liavleAmtTotal);
			claimActionform1
					.setCp_ots_firstInstallmentPaidTotal(firstInstallmentPaidTotal);
			claimActionform1
					.setCp_ots_recafterfirstinstallTotal(recafterfirstinstallTotal);
			claimActionform1.setCp_ots_totaDecdectAMt(totaDecdectAMt);
			claimActionform1.setCp_ots_totNotdedctAmt(totNotdedctAmt);
			claimActionform1.setCp_ots_totnetRecovAmt(totnetRecovAmt);
			claimActionform1
					.setCp_ots_netAmtindefoultTotal(netAmtindefoultTotal);
			claimActionform1
					.setCp_ots_secinstamentAmtTotal(secinstamentAmtTotal);
			claimActionform1.setCp_ots_finalPayoutAmtTotal(finalPayoutAmtTotal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.freeConnection(connection);
		}
		return claimActionform1;
	}

	public ActionForward farwordData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClaimActionForm claimactionform = (ClaimActionForm) form;
		double val = 0.0D;
		ArrayList newValues = new ArrayList();
		String cgpanHidd[] = request.getParameterValues("cgpanHidd");
		String cgpanGaurnteeAmtHidd[] = request
				.getParameterValues("cgpanGaurnteeAmtHidd");
		String npaAmountHidd[] = request.getParameterValues("npaAmountHidd");
		String recoveryAmtHidd[] = request
				.getParameterValues("recoveryAmtHidd");
		String netOutstandAmt[] = request.getParameterValues("netOutstandAmt");
		String laibleAmount[] = request.getParameterValues("laibleAmount");
		String firstInstalPaidAmt[] = request
				.getParameterValues("firstInstalPaidAmt");
		String recAfterFirstInstall[] = request
				.getParameterValues("recAfterFirstInstall");
		String deductAmt[] = request.getParameterValues("deductAmt");
		String notDeductAmt[] = request.getParameterValues("notDeductAmt");
		String netRecovAmt[] = request.getParameterValues("netRecovAmt");
		String netAmtIndefaoult[] = request
				.getParameterValues("netAmtIndefaoult");
		String secInstallAmt[] = request.getParameterValues("secInstallAmt");
		String finalPayoutAmt[] = request.getParameterValues("finalPayoutAmt");
		double cgpanGaurnteeAmtHiddValTot = 0.0D;
		double npaAmountHiddValTot = 0.0D;
		double recoveryAmtHiddValTot = 0.0D;
		double netOutstandAmtValTot = 0.0D;
		double laibleAmountValTot = 0.0D;
		double firstInstalPaidAmtValTot = 0.0D;
		double recAfterFirstInstallValTot = 0.0D;
		double deductAmtValTot = 0.0D;
		double notDeductAmtValTot = 0.0D;
		double netRecovAmtValTot = 0.0D;
		double netAmtIndefaoultValTot = 0.0D;
		double secInstallAmtValTot = 0.0D;
		double finalPayoutAmtValTot = 0.0D;
		try {
			for (int i = 0; i < netOutstandAmt.length; i++) {
				ClaimActionForm Obj = new ClaimActionForm();
				String cgpanHiddVal = cgpanHidd[i];
				Obj.setCp_ots_enterCgpan(cgpanHiddVal);
				double cgpanGaurnteeAmtHiddVal = Double
						.parseDouble(cgpanGaurnteeAmtHidd[i]);
				Obj.setCp_ots_cgpanGaurnteeAmt(cgpanGaurnteeAmtHiddVal);
				double npaAmountHiddVal = Double.parseDouble(npaAmountHidd[i]);
				Obj.setCp_ots_npaAmount(npaAmountHiddVal);
				double recoveryAmtHiddVal = Double
						.parseDouble(recoveryAmtHidd[i]);
				Obj.setCp_ots_recoveryAmt(recoveryAmtHiddVal);
				double netOutstandAmtVal = Double
						.parseDouble(netOutstandAmt[i]);
				Obj.setCp_ots_netOutstanding(netOutstandAmtVal);
				double laibleAmountVal = Double.parseDouble(laibleAmount[i]);
				Obj.setCp_ots_liableamount(laibleAmountVal);
				double firstInstalPaidAmtVal = Double
						.parseDouble(firstInstalPaidAmt[i]);
				Obj.setCp_ots_firstIntalpaidAmount(firstInstalPaidAmtVal);
				double recAfterFirstInstallVal = Double
						.parseDouble(recAfterFirstInstall[i]);
				Obj.setCp_ots_totRecAftFirInst(recAfterFirstInstallVal);
				double deductAmtVal = Double.parseDouble(deductAmt[i]);
				Obj.setCp_ots_totDedtctAmt(deductAmtVal);
				double notDeductAmtVal = Double.parseDouble(notDeductAmt[i]);
				Obj.setCp_ots_totNotDedtctAmt(notDeductAmtVal);
				double netRecovAmtVal = Double.parseDouble(netRecovAmt[i]);
				Obj.setCp_ots_netRecovAmt(netRecovAmtVal);
				double netAmtIndefaoultVal = Double
						.parseDouble(netAmtIndefaoult[i]);
				Obj.setCp_ots_netAmountInDefoault(netAmtIndefaoultVal);
				double secInstallAmtVal = Double.parseDouble(secInstallAmt[i]);
				Obj.setCp_ots_secIntalAMt(secInstallAmtVal);
				double finalPayoutAmtVal = Double
						.parseDouble(finalPayoutAmt[i]);
				Obj.setCp_ots_finalPayout(finalPayoutAmtVal);
				newValues.add(Obj);
				cgpanGaurnteeAmtHiddValTot += cgpanGaurnteeAmtHiddVal;
				npaAmountHiddValTot += npaAmountHiddVal;
				recoveryAmtHiddValTot += recoveryAmtHiddVal;
				netOutstandAmtValTot += netOutstandAmtVal;
				laibleAmountValTot += laibleAmountVal;
				firstInstalPaidAmtValTot += firstInstalPaidAmtVal;
				recAfterFirstInstallValTot += recAfterFirstInstallVal;
				deductAmtValTot += deductAmtVal;
				notDeductAmtValTot += notDeductAmtVal;
				netRecovAmtValTot += netRecovAmtVal;
				netAmtIndefaoultValTot += netAmtIndefaoultVal;
				secInstallAmtValTot += secInstallAmtVal;
				finalPayoutAmtValTot += finalPayoutAmtVal;
			}

			claimactionform.setCp_ots_guarnteetotal(cgpanGaurnteeAmtHiddValTot);
			claimactionform.setCp_ots_npatotalAmt(npaAmountHiddValTot);
			claimactionform.setCp_ots_recovTotal(recoveryAmtHiddValTot);
			claimactionform.setCp_ots_netOutstandingTotal(netOutstandAmtValTot);
			claimactionform.setCp_ots_liavleAmtTotal(laibleAmountValTot);
			claimactionform
					.setCp_ots_firstInstallmentPaidTotal(firstInstalPaidAmtValTot);
			claimactionform
					.setCp_ots_recafterfirstinstallTotal(recAfterFirstInstallValTot);
			claimactionform.setCp_ots_totaDecdectAMt(deductAmtValTot);
			claimactionform.setCp_ots_totNotdedctAmt(notDeductAmtValTot);
			claimactionform.setCp_ots_totnetRecovAmt(netRecovAmtValTot);
			claimactionform
					.setCp_ots_netAmtindefoultTotal(netAmtIndefaoultValTot);
			claimactionform.setCp_ots_secinstamentAmtTotal(secInstallAmtValTot);
			claimactionform.setCp_ots_finalPayoutAmtTotal(finalPayoutAmtValTot);
			claimactionform.setLoopobjtData(newValues);
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		ClaimActionForm obj = new ClaimActionForm();
		obj.setCp_ots_netOutstanding(val);
		return mapping.findForward("datafarward");
	}

	public ActionForward saveOTSData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClaimActionForm claimActionformdetail;
		HttpSession session;
		String remrk;
		String memberId;
		String mliName;
		String unitName;
		String clmrefNo;
		String unitAssit;
		String gender;
		String converttoDBformnpaDate;
		String converttoDBformfirstInstall;
		ArrayList cgarry;
		Connection connection;
		claimActionformdetail = (ClaimActionForm) form;
		session = request.getSession(true);
		remrk = "";
		remrk = claimActionformdetail.getCp_ots_remarks2();
		memberId = "";
		double mem = 0.0D;
		mliName = "";
		unitName = "";
		clmrefNo = "";
		unitAssit = "";
		gender = "";
		Date npaDate = null;
		Date firstInstall = null;
		memberId = claimActionformdetail.getCp_ots_enterMember();
		mem = Double.parseDouble(memberId);
		mliName = claimActionformdetail.getCp_ots_mliName();
		unitName = claimActionformdetail.getCp_ots_unitName();
		clmrefNo = claimActionformdetail.getCp_ots_appRefNo();
		unitAssit = claimActionformdetail.getCp_ots_UnitAssitByMSE();
		gender = claimActionformdetail.getCp_ots_gender();
		npaDate = claimActionformdetail.getCp_ots_npaDate();
		firstInstall = claimActionformdetail.getCp_ots_clmappDate();
		String ab = npaDate.toString();
		String xy = firstInstall.toString();
		converttoDBformnpaDate = DateHelper.stringToDBDate(ab);
		converttoDBformfirstInstall = DateHelper.stringToDBDate(xy);
		cgarry = claimActionformdetail.getLoopobjtData();
		double guarnteetotal = 0.0D;
		double npatotalAmt = 0.0D;
		double recovTotal = 0.0D;
		double netOutstandingTotal = 0.0D;
		double liavleAmtTotal = 0.0D;
		double firstInstallmentPaidTotal = 0.0D;
		double recafterfirstinstallTotal = 0.0D;
		double totaDecdectAMt = 0.0D;
		double totNotdedctAmt = 0.0D;
		double totnetRecovAmt = 0.0D;
		double netAmtindefoultTotal = 0.0D;
		double secinstamentAmtTotal = 0.0D;
		double finalPayoutAmtTotal = 0.0D;
		String ctgpan = "";
		connection = DBConnection.getConnection();
		try {
			connection.setAutoCommit(false);
			User user = (User) session.getAttribute("USER");
			String userid = user.getUserId();
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String todysDate = formatter.format(date);
			String currentDate = DateHelper.stringToDBDate(todysDate);
			String userChoiceSattes = claimActionformdetail
					.getCp_ots_userChoice();
			String farwardtouser = claimActionformdetail.getCp_ots_user();
			userChoiceSattes = userChoiceSattes.trim();
			String satus = "";
			if (userChoiceSattes.equals("NE"))
				throw new NoMemberFoundException(
						"Please Select The farwrad Option. ");
			if (userChoiceSattes.equals("FW"))
				satus = "FW";
			farwardtouser = farwardtouser.trim();
			if (farwardtouser.equals("Select"))
				throw new NoMemberFoundException(
						"Please Select The farwrding User .");
			double totGaurantee = claimActionformdetail
					.getCp_ots_guarnteetotal();
			double totNPA = claimActionformdetail.getCp_ots_npatotalAmt();
			double totRec = claimActionformdetail.getCp_ots_recovTotal();
			double totNetOutstand = claimActionformdetail
					.getCp_ots_netOutstandingTotal();
			double totlaibel = claimActionformdetail.getCp_ots_liavleAmtTotal();
			double totfirstInstallpaid = claimActionformdetail
					.getCp_ots_firstInstallmentPaidTotal();
			double totRecoAfterFirstInstal = claimActionformdetail
					.getCp_ots_recafterfirstinstallTotal();
			double totDedc = claimActionformdetail.getCp_ots_totaDecdectAMt();
			double totNotDedect = claimActionformdetail
					.getCp_ots_totNotdedctAmt();
			double totNetrec = claimActionformdetail.getCp_ots_totnetRecovAmt();
			double totnetAmtInDefoult = claimActionformdetail
					.getCp_ots_netAmtindefoultTotal();
			double totSecondInt = claimActionformdetail
					.getCp_ots_secinstamentAmtTotal();
			double totPayout = claimActionformdetail
					.getCp_ots_finalPayoutAmtTotal();
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			for (ResultSet rs = str.executeQuery((new StringBuilder())
					.append("select * from OTS where clm_ref_no='")
					.append(clmrefNo).append("'").toString()); rs.next();)
				throw new NoDataException("Application Already Saved.");

			for (int i = 0; i < cgarry.size(); i++) {
				ClaimActionForm clm = (ClaimActionForm) cgarry.get(i);
				ctgpan = clm.getCp_ots_enterCgpan();
				guarnteetotal = clm.getCp_ots_cgpanGaurnteeAmt();
				npatotalAmt = clm.getCp_ots_npaAmount();
				recovTotal = clm.getCp_ots_recoveryAmt();
				netOutstandingTotal = clm.getCp_ots_netOutstanding();
				liavleAmtTotal = clm.getCp_ots_liableamount();
				firstInstallmentPaidTotal = clm
						.getCp_ots_firstIntalpaidAmount();
				recafterfirstinstallTotal = clm.getCp_ots_totRecAftFirInst();
				totNotdedctAmt = clm.getCp_ots_totNotDedtctAmt();
				totaDecdectAMt = clm.getCp_ots_totDedtctAmt();
				totnetRecovAmt = clm.getCp_ots_netRecovAmt();
				netAmtindefoultTotal = clm.getCp_ots_netAmountInDefoault();
				secinstamentAmtTotal = clm.getCp_ots_secIntalAMt();
				finalPayoutAmtTotal = clm.getCp_ots_finalPayout();
				str.executeUpdate((new StringBuilder())
						.append("INSERT INTO CGTSIINTRANETUSER.OTS ( CGPAN, GURANTEE_AMT, NPA_AMT, RECOVERY_AMT, NET_OUTSTANDING_AMT, LIABLE_AMT, FIRST_INSTALL_PAID_AMT, REC_AFTER_FIRSTiNSTALL_AMT, NOT_DEDUCTED_AMT, DEDEUCTED_AMT, NET_REC_AMT,NET_DEFOULT_AMT, SEC_INSTALL_AMT ,FINAL_PAY_AMT, MEMBERID, MLI_NAME, UNIT_NAME, CLM_REF_NO, UNIT_ASS_FLAG, GENDER, NPA_DATE, CLM_FIRST_INSTALL_DATE ,REMARKS) VALUES ('")
						.append(ctgpan).append("' , '").append(guarnteetotal)
						.append("' , '").append(npatotalAmt).append("' , '")
						.append(recovTotal).append("' , '")
						.append(netOutstandingTotal).append("' , '")
						.append(liavleAmtTotal).append("' , '")
						.append(firstInstallmentPaidTotal).append("' , '")
						.append(recafterfirstinstallTotal).append("' ,'")
						.append(totNotdedctAmt).append("' , '")
						.append(totaDecdectAMt).append("' , '")
						.append(totnetRecovAmt).append("' , '")
						.append(netAmtindefoultTotal).append("' , '")
						.append(secinstamentAmtTotal).append("' , '")
						.append(finalPayoutAmtTotal).append("' , '")
						.append(memberId).append("' , '").append(mliName)
						.append("', '").append(unitName).append("' , '")
						.append(clmrefNo).append("' , '").append(unitAssit)
						.append("' , '").append(gender).append("' , '")
						.append(converttoDBformnpaDate).append("' , '")
						.append(converttoDBformfirstInstall).append("' , '")
						.append(remrk).append("' )").toString());
			}

			String queryStr = (new StringBuilder())
					.append("INSERT INTO CGTSIINTRANETUSER.OTS_TOTAL (GURANTEE_AMT_TOTAL,NPA_AMT_TOTAL,RECOVERY_AMT_TOTAL,NET_OUTSTANDING_AMT_TOTAL,LIABLE_AMT,FIRST_INSTALL_PAID_AMT_TOTAL,REC_AFTER_FIRST_INST_AMT_TOTAL,NOT_DEDUCTED_AMT_TOTAL,DEDEUCTED_AMT_TOTAL,NET_REC_AMT_TOTAL,NET_DEFOULT_AMT_TOTAL,SEC_INSTALL_AMT_TOTAL,FINAL_PAY_AMT_TOTAL,MEMBERID,UNIT_NAME,CLM_REF_NO,FORWAREDER_REMARK,FORWAREDER_USR_NAME,FORWAREDED_DATE,STATUS,FARWORDED_TO_USER) VALUES ('")
					.append(totGaurantee).append("' , '").append(totNPA)
					.append("' , '").append(totRec).append("' , '")
					.append(totNetOutstand).append("' , '").append(totlaibel)
					.append("' , '").append(totfirstInstallpaid)
					.append("' , '").append(totRecoAfterFirstInstal)
					.append("' ,'").append(totDedc).append("' , '")
					.append(totNotDedect).append("' , '").append(totNetrec)
					.append("' , '").append(totnetAmtInDefoult).append("' , '")
					.append(totSecondInt).append("' , '").append(totPayout)
					.append("' , '").append(memberId).append("' , '")
					.append(unitName).append("', '").append(clmrefNo)
					.append("' , '").append(remrk).append("' , '")
					.append(userid).append("' , '").append(currentDate)
					.append("' , '").append(satus).append("','")
					.append(farwardtouser).append("')").toString();
			str.executeUpdate(queryStr);
			String modiRecoStatuc = (new StringBuilder())
					.append("UPDATE CLM_CGPAN_RECOVERY_DETAIL SET CCRD_FINAL_RECOVERY='Y' WHERE CLM_REF_NO='")
					.append(clmrefNo).append("'").toString();
			String viewprsentclminRecory = (new StringBuilder())
					.append("SELECT * FROM CLM_CGPAN_RECOVERY_DETAIL WHERE CLM_REF_NO='")
					.append(clmrefNo).append("'").toString();
			ResultSet viewClmInRecoRS = str.executeQuery(viewprsentclminRecory);
			if (viewClmInRecoRS.next())
				str.executeUpdate(modiRecoStatuc);
		} finally {
			connection.commit();

			DBConnection.freeConnection(connection);
			request.setAttribute("message", "Claim farwared successfully.");
		}
		return mapping.findForward("successclmsave");
	}

	public ActionForward getuserList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(Log.INFO, "ApplicationProcessingAction", "getDistricts",
				"Entered");
		Administrator admin = new Administrator();
		ClaimActionForm dynaForm = (ClaimActionForm) form;
		HttpSession session1 = request.getSession(false);
		String userChoice = dynaForm.getCp_ots_userChoice();
		Connection connection = DBConnection.getConnection();
		ArrayList userlist = new ArrayList();
		try {
			String var = "A";
			var = var.trim();
			String qury = "SELECT * FROM USER_INFO WHERE MEM_BNK_ID='0000' AND USR_STATUS='A' order by 1";
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			for (ResultSet rs = str.executeQuery(qury); rs.next(); userlist
					.add(rs.getString(1)))
				;
			dynaForm.setCp_ots_userList(userlist);
			PrintWriter out = response.getWriter();
			String outLine = makeOutputString(userlist);
			out.print(outLine);
			admin = null;
			userlist = null;
			userChoice = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.log(Log.INFO, "ApplicationProcessingAction", "getDistricts",
				"Exited");
		return null;
	}

	String makeOutputString(ArrayList districtList) {
		String disstring = "Select;Select";
		for (int i = 1; i < districtList.size(); i++)
			disstring = (new StringBuilder()).append(disstring).append("||")
					.append(districtList.get(i)).append(";")
					.append(districtList.get(i)).toString();

		return disstring;
	}

	public ActionForward getClaimProcessingOTSNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionForm actionForm;
		ArrayList forApp;
		HttpSession session;
		Connection connection;
		Log.log(Log.INFO, "ClaimAction", "getClaimProcessingOTSNew", "Entered");
		actionForm = form;
		forApp = new ArrayList();
		session = request.getSession(true);
		session.removeAttribute("clmForm");
		connection = DBConnection.getConnection();
		try {
			ClaimActionForm clmForm = new ClaimActionForm();
			String status = "FW";
			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = (new StringBuilder())
					.append("select GURANTEE_AMT_TOTAL,NPA_AMT_TOTAL,RECOVERY_AMT_TOTAL,NET_OUTSTANDING_AMT_TOTAL,LIABLE_AMT,FIRST_INSTALL_PAID_AMT_TOTAL,REC_AFTER_FIRST_INST_AMT_TOTAL,NOT_DEDUCTED_AMT_TOTAL,DEDEUCTED_AMT_TOTAL,NET_REC_AMT_TOTAL,NET_DEFOULT_AMT_TOTAL,SEC_INSTALL_AMT_TOTAL,FINAL_PAY_AMT_TOTAL,MEMBERID,UNIT_NAME,CLM_REF_NO FROM OTS_TOTAL WHERE STATUS='")
					.append(status).append("'").toString();
			ResultSet rs;
			for (rs = str.executeQuery(query); !rs.next();)
				throw new NoMemberFoundException("No Data For Approval. ");

			rs.beforeFirst();
			ClaimActionForm clm;
			for (; rs.next(); forApp.add(clm)) {
				clm = new ClaimActionForm();
				double guranteeAmt = rs.getDouble(1);
				double npaAmt = rs.getDouble(2);
				double recoveryAmt = rs.getDouble(3);
				double netoutstandAmt = rs.getDouble(4);
				double liableAmt = rs.getDouble(5);
				double firstInstallAmt = rs.getDouble(6);
				double recAfterfisrtAmt = rs.getDouble(7);
				double notDeductAmt = rs.getDouble(8);
				double decductedAmt = rs.getDouble(9);
				double netrecAmt = rs.getDouble(10);
				double netDefoultAmt = rs.getDouble(11);
				double secInstallAmt = rs.getDouble(12);
				double finalpayAmt = rs.getDouble(13);
				String memberId = rs.getString(14);
				String unitName = rs.getString(15);
				String clmRefno = rs.getString(16);
				clm.setOts_memberId(memberId);
				clm.setOts_clmRefNo(clmRefno);
				clm.setOts_unitName(unitName);
				clm.setOts_gaurnteeAmt(guranteeAmt);
				clm.setOts_amtInDefault(npaAmt);
				clm.setOts_recoveryPrimary(recoveryAmt);
				clm.setOts_netOutstanding(netoutstandAmt);
				clm.setOts_liableAmt(liableAmt);
				clm.setOts_firstInstallPaidAmt(firstInstallAmt);
				clm.setOts_recoveryAfterPrimary(recAfterfisrtAmt);
				clm.setOts_legalExpencesNotDeducted(notDeductAmt);
				clm.setOts_legalExpencesDeducted(decductedAmt);
				clm.setOts_netRecovery(netrecAmt);
				clm.setOts_netDefaultAmt(netDefoultAmt);
				clm.setOts_secondInstallmentAmt(secInstallAmt);
				clm.setOts_finalPayout(finalpayAmt);
			}

			for (int i = 0; i < forApp.size(); i++) {
				ClaimActionForm clm1 = (ClaimActionForm) forApp.get(i);
				double gaurn = clm1.getOts_gaurnteeAmt();
			}

			clmForm.setOtsClmProcess(forApp);
			BeanUtils.copyProperties(actionForm, clmForm);
			ArrayList ab = clmForm.getOtsClmProcess();
			for (int i = 0; i < ab.size(); i++) {
				ClaimActionForm clm1 = (ClaimActionForm) ab.get(i);
				double gaurn = clm1.getOts_gaurnteeAmt();
			}

			session.setAttribute("clmForm", clmForm);
		} finally {
			DBConnection.freeConnection(connection);
			Log.log(Log.INFO, "ClaimAction", "getClaimProcessingOTSNew",
					"Exited");
		}
		return mapping.findForward("success");
	}

	public ActionForward saveApproval(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClaimActionForm clmform;
		Connection connection;
		// System.out.println("Inside Save approval ..........!");
		clmform = (ClaimActionForm) form;
		// System.out.println("Inside Save Approval .........!");
		connection = DBConnection.getConnection();
		Statement str = null;
		try {
			HttpSession session = request.getSession(true);
			User user = (User) session.getAttribute("USER");
			String userid = user.getUserId();
			System.out
					.println((new StringBuilder())
							.append("The User Name is :--->").append(userid)
							.toString());
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String todysDate = formatter.format(date);
			System.out.println((new StringBuilder()).append("Today : ")
					.append(todysDate).toString());
			String currentDate = DateHelper.stringToDBDate(todysDate);
			System.out.println((new StringBuilder())
					.append("The Current Date is :--->").append(currentDate)
					.toString());
			ArrayList claimProcess = clmform.getOtsClmProcess();
			connection.setAutoCommit(false);
			str = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int len = claimProcess.size();
			String action[] = request.getParameterValues("ots_decisionTaken");
			String approvRemark[] = request.getParameterValues("ots_remark");
			System.out.println((new StringBuilder())
					.append("the length of the arrauList is :===>").append(len)
					.toString());
			String actFlg = "";
			for (int i = 0; i < claimProcess.size(); i++) {
				ClaimActionForm clm = (ClaimActionForm) claimProcess.get(i);
				String act = action[i];
				if (act.equals("reject"))
					actFlg = "RE";
				if (act.equals("approve"))
					actFlg = "AP";
				if (act.equals("hold"))
					actFlg = "HO";
				if (act.equals("select"))
					actFlg = "FW";
				String clm_ref = clm.getOts_clmRefNo();
				String remarks = approvRemark[i];
				System.out.println((new StringBuilder())
						.append("the unit name is:----->")
						.append(clm.getOts_unitName()).toString());
				String quryChk = (new StringBuilder())
						.append("SELECT STATUS FROM  OTS_TOTAL  WHERE CLM_REF_NO='")
						.append(clm_ref).append("'").toString();
				for (ResultSet rs = str.executeQuery(quryChk); rs.next();) {
					String sta = rs.getString(1);
					if (!sta.equals("FW"))
						throw new NoMemberFoundException("alreday processed. ");
				}

				String qury = (new StringBuilder())
						.append("UPDATE OTS_TOTAL SET APPROVER_USR_NAME='")
						.append(userid).append("',APPROVER_DATE='")
						.append(currentDate).append("',APPROVER_REMARK='")
						.append(remarks).append("',STATUS='").append(actFlg)
						.append("' WHERE CLM_REF_NO='").append(clm_ref)
						.append("'").toString();
				str.executeUpdate(qury);
			}
		} finally {
			str.close();
			str = null;
			connection.commit();

			DBConnection.freeConnection(connection);
		}
		return mapping.findForward("saved");
	}

	public ActionForward getRecoveryDetailData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String enteredMemberId;
		String enterCgpan;
		ClaimActionForm claimForm;
		Connection connection = null;
		Log.log(Log.INFO, "ClaimAction", "getRecoveryDetailData", "Entered");
		enteredMemberId = null;
		enterCgpan = null;
		claimForm = (ClaimActionForm) form;
		try {
			enteredMemberId = claimForm.getEnterMember().toUpperCase();
			enterCgpan = claimForm.getEnterCgpan().toUpperCase();
			System.out.println((new StringBuilder())
					.append("the member Id is :-->").append(enteredMemberId)
					.toString());
			System.out.println((new StringBuilder())
					.append("the CGPAN is :-->").append(enterCgpan).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!enteredMemberId.equals(null)) {
			if (!enterCgpan.equals(null)) {

				connection = DBConnection.getConnection();

				System.out.println((new StringBuilder())
						.append("The Connection URL is :--->")
						.append(connection.toString()).toString());
				// System.out.println("---->");
				try {
					Statement str = connection.createStatement(
							ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					String one = enteredMemberId.substring(0, 4);
					String two = enteredMemberId.substring(4, 8);
					String three = enteredMemberId.substring(8, 12);
					// System.out.println("---->1");
					String MemIdavailbel = (new StringBuilder())
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c \nwhere  c.mem_bnk_id='")
							.append(one).append("' and\n")
							.append("c.mem_zne_id='").append(two)
							.append("' and\n").append("c.mem_brn_id='")
							.append(three).append("'").toString();
					ResultSet rsforavailmemid = str.executeQuery(MemIdavailbel);
					if (!rsforavailmemid.next()) {
						throw new NoMemberFoundException(
								" Member Id  Not Exsist : ");
					}
					// System.out.println("---->2");
					String cgQury = (new StringBuilder())
							.append("select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='")
							.append(enterCgpan).append("'").toString();
					ResultSet rsforvalid = str.executeQuery(cgQury);
					String memberid = "";
					if (!rsforvalid.next()) {
						throw new NoMemberFoundException(
								" CGPAN  Not Exsist : ");
					}
					// System.out.println("---->3");
					// System.out.println("---->4");
					rsforvalid.beforeFirst();
					while (rsforvalid.next()) {
						memberid = rsforvalid.getString(1);
					}
					if (!memberid.equals(enteredMemberId)) {
						throw new NoMemberFoundException(
								" Enterd Member Id and CGPAN  Not Associated. ");
					}
					// System.out.println("---->5");
					String crefQuery = (new StringBuilder())
							.append("select clm_Ref_no from claim_detail  \nwhere bid in \n(select s.bid from ssi_detail s,application_detail a \nwhere a.ssi_Reference_number = s.ssi_Reference_number \nand a.cgpan = '")
							.append(enterCgpan)
							.append("'\n")
							.append("and  a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id='")
							.append(enteredMemberId).append("')").toString();
					// System.out.println("---->6");
					ResultSet rsforClaimRefNo;
					for (rsforClaimRefNo = str.executeQuery(crefQuery); !rsforClaimRefNo
							.next();)
						throw new NoMemberFoundException(
								" Claim Application Does Not Exsist for Entred CGPAN and Member Id. ");

					rsforClaimRefNo.beforeFirst();
					String retrivedClmRefNo;
					for (retrivedClmRefNo = ""; rsforClaimRefNo.next(); retrivedClmRefNo = rsforClaimRefNo
							.getString(1))
						;
					System.out.println((new StringBuilder())
							.append("the retived Claim Ref No is :-->")
							.append(retrivedClmRefNo).toString());
					HttpSession session = request.getSession(true);
					session.setAttribute("clmReff", retrivedClmRefNo);
					session.setAttribute("cgpan", enterCgpan);
					String quryfortestapp = (new StringBuilder())
							.append("select clm_status,clm_installment_flag from claim_detail where clm_ref_no ='")
							.append(retrivedClmRefNo).append("'").toString();
					ResultSet rsforchkapp = str.executeQuery(quryfortestapp);
					String clmstatus = "";
					for (String clminstallmentgflag = ""; rsforchkapp.next(); clminstallmentgflag = rsforchkapp
							.getString(2)) {
						clmstatus = rsforchkapp.getString(1);
						System.out.println((new StringBuilder())
								.append("the stautus of application is =====>")
								.append(clmstatus).toString());
					}

					if (!clmstatus.equals("AP"))
						throw new NoMemberFoundException(
								" The Claim Application is Not Approved. ");
					String QuryForchcond = (new StringBuilder())
							.append("select * from claim_application_amount where cgpan='")
							.append(enterCgpan).append("'").toString();
					ResultSet rsforchkCond = str.executeQuery(QuryForchcond);
					if (!rsforchkCond.next())
						throw new NoMemberFoundException(
								" The Claim Applications for first Installment not Release.");
					String QuryForRetriveInfo = (new StringBuilder())
							.append("select c.CLM_MLI_NAME,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id mem_id ,c.CLM_PLACE,c.clm_ref_no,ca.cgpan,ssi_unit_name unit,Ctd_TC_CLM_ELIG_AMT elg_amt,CTD_TC_FIRST_INST_PAY_AMT First_Inst,c.clm_approved_dt\nfrom claim_detail c,ssi_detail s,claim_application_amount ca,CLAIM_TC_DETAIL T\nwhere ca.clm_ref_no=c.clm_ref_no\nand  c.clm_status='AP'\nand  c.clm_installment_flag='F'\nand  c.bid=s.bid\nAND t.CGPAN=ca.CGPAN\nand c.clm_ref_no in(select clm_ref_no from claim_application_amount where cgpan='")
							.append(enterCgpan)
							.append("')\n")
							.append("union\n")
							.append("select c.CLM_MLI_NAME,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id mem_id ,c.CLM_PLACE,c.clm_ref_no,ca.cgpan,ssi_unit_name unit,Cwd_wC_CLM_ELIG_AMT elg_amt,Cwd_wC_FIRST_INST_PAY_AMT First_Inst,c.clm_approved_dt\n")
							.append("from claim_detail c,ssi_detail s,claim_application_amount ca,CLAIM_wC_DETAIL T\n")
							.append("where ca.clm_ref_no=c.clm_ref_no\n")
							.append("and  c.clm_status='AP'\n")
							.append("and  c.clm_installment_flag='F'\n")
							.append("and  c.bid=s.bid\n")
							.append("AND t.CGPAN=ca.CGPAN\n")
							.append("and c.clm_ref_no in(select clm_ref_no from claim_application_amount where cgpan='")
							.append(enterCgpan).append("')").toString();
					ResultSet rsforRetrirvedInfo = str
							.executeQuery(QuryForRetriveInfo);
					ClaimActionForm claimActionForm = new ClaimActionForm();
					if (rsforRetrirvedInfo.next()) {
						claimActionForm.setMliName(rsforRetrirvedInfo
								.getString(1));
						claimActionForm.setMliId(rsforRetrirvedInfo
								.getString(2));
						claimActionForm
								.setPlaceforClmRecovery(rsforRetrirvedInfo
										.getString(3));
						claimActionForm.setClmRefNumber(rsforRetrirvedInfo
								.getString(4));
						claimActionForm
								.setCgpanforclamRecovery(rsforRetrirvedInfo
										.getString(5));
						claimActionForm.setUnitName(rsforRetrirvedInfo
								.getString(6));
						claimActionForm.setClmEligibleAmt(rsforRetrirvedInfo
								.getDouble(7));
						claimActionForm
								.setFirstinstalmentrelease(rsforRetrirvedInfo
										.getDouble(8));
						claimActionForm.setFirstInsatllDate(rsforRetrirvedInfo
								.getDate(9));
					}
					ArrayList viewArr = new ArrayList();
					String viewquery = (new StringBuilder())
							.append("select * from CLM_CGPAN_RECOVERY_DETAIL where clm_ref_no='")
							.append(retrivedClmRefNo)
							.append("' order by ccrd_receipt_date desc")
							.toString();
					ResultSet rsforViewData;
					ClaimActionForm claimActionForm2;
					for (rsforViewData = str.executeQuery(viewquery); rsforViewData
							.next(); viewArr.add(claimActionForm2)) {
						claimActionForm2 = new ClaimActionForm();
						claimActionForm2.setEnterCgpan(rsforViewData
								.getString(3));
						claimActionForm2.setDateOfreciept(rsforViewData
								.getDate(4));
						claimActionForm2.setAmtRecipt(rsforViewData
								.getDouble(5));
						claimActionForm2.setExpIncforRecovery(rsforViewData
								.getDouble(6));
						claimActionForm2.setExpDeducted(rsforViewData
								.getString(7));
						claimActionForm2.setNetRecovery(rsforViewData
								.getDouble(8));
						claimActionForm2.setDdNo(rsforViewData.getString(9));
						claimActionForm2.setDdDate(rsforViewData.getDate(10));
						claimActionForm2.setRemark(rsforViewData.getString(11));
					}

					rsforViewData.beforeFirst();
					double netrecovrySum;
					for (netrecovrySum = 0.0D; rsforViewData.next(); netrecovrySum += rsforViewData
							.getDouble(8))
						;
					claimActionForm.setTotalNetRecovery(netrecovrySum);
					System.out.println((new StringBuilder())
							.append("The Total Net Recovery is :--->")
							.append(netrecovrySum).toString());
					String clmRefnoId = claimActionForm.getClmRefNumber();
					System.out.println((new StringBuilder())
							.append("the claim Ref No is :--->")
							.append(clmRefnoId).toString());
					String selectRecovery = (new StringBuilder())
							.append("SELECT CCRD_FINAL_RECOVERY  FROM CLM_CGPAN_RECOVERY_DETAIL WHERE CLM_REF_NO='")
							.append(clmRefnoId).append("' ").toString();
					String finalrecovr = "";
					for (ResultSet rs = str.executeQuery(selectRecovery); rs
							.next();) {
						finalrecovr = rs.getString(1);
						System.out.println((new StringBuilder())
								.append("the value retive is====>")
								.append(finalrecovr).toString());
						if (finalrecovr.equals("Y"))
							throw new NoMemberFoundException(
									"Final recovery Alreday Made for Claim . ");
					}

					claimActionForm.setViewRecArr(viewArr);
					BeanUtils.copyProperties(claimForm, claimActionForm);
				} finally {
					DBConnection.freeConnection(connection);
					// System.out.println("Inside finally");
					Log.log(Log.INFO, "ClaimAction", "getRecoveryDetailData",
							"Exited");
					// System.out.println("before redirecting");
					claimForm.setEnterMember(enteredMemberId);
					claimForm.setEnterCgpan(enterCgpan);
				}
			}
		}
		return mapping.findForward("ClaimRecoverysuccess");
	}

	public ActionForward forwardtoOTSAfterRecovery(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("inside forwardtoOTSAfterRecovery method...!");
		String key = "OTS";
		request.setAttribute("OTS", key);
		ClaimActionForm actionform = (ClaimActionForm) form;
		actionform.setBooleanFinalRecovery(false);
		SaveClaimrecovryData(mapping, form, request, response);
		System.out.println("after saving data into ");
		String enterCgpan = actionform.getEnterCgpan();
		String mliId = actionform.getMliId();
		actionform.setCp_ots_enterMember(mliId);
		actionform.setCp_ots_enterCgpan(enterCgpan);
		actionform.setCp_ots_appRefNo("");
		System.out
				.println("After Setting the MLI Id,CGPAN Number, Claim Reference Number. ");
		System.out.println((new StringBuilder())
				.append("The Member Id After Set ===>")
				.append(actionform.getCp_ots_enterMember()).toString());
		try {
			getRecoveryAfterOTSDetail(mapping, form, request, response);
			System.out.println("The path is :-->");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("detailsuccess");
	}

	public ActionForward SaveClaimrecovryData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String userid;
		double amtreciept;
		double expInForrecover;
		String booleanExp;
		double netrecover;
		String ddNo;
		String remark;
		String clmreff;
		String cgPan;
		String currentDate;
		String reciptDate;
		String dddbDate;
		String falgvalue;
		Connection connection;
		Log.log(Log.INFO, "ClaimAction", "SaveClaimrecovryData", "Entered");
		System.out.println("inside method.....!");
		ClaimActionForm actionform = (ClaimActionForm) form;
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("USER");
		userid = user.getUserId();
		Date recDate1 = actionform.getDateOfreciept();
		String recDate = recDate1.toString();
		System.out
				.println((new StringBuilder())
						.append("the recipt date is :--->").append(recDate1)
						.toString());
		amtreciept = actionform.getAmtRecipt();
		expInForrecover = actionform.getExpIncforRecovery();
		boolean booleanExp1 = actionform.isBooleanExpInc();
		System.out.println((new StringBuilder()).append("The booleanExp1:--->")
				.append(booleanExp1).toString());
		booleanExp = "";
		if (booleanExp1)
			booleanExp = "Y";
		else
			booleanExp = "N";
		netrecover = actionform.getNetRecovery();
		ddNo = actionform.getDdNo();
		Date ddDate1 = actionform.getDdDate();
		String ddDate = ddDate1.toString();
		remark = actionform.getRemark();
		System.out.println((new StringBuilder()).append("the DD date is :--->")
				.append(ddDate).toString());
		clmreff = (String) session.getAttribute("clmReff");
		cgPan = (String) session.getAttribute("cgpan");
		double totnetRecov = actionform.getTotalNetRecovery();
		double firstInstallRelease = actionform.getFirstinstalmentrelease();
		totnetRecov += netrecover;
		System.out.println((new StringBuilder())
				.append("current total net Recovery is :-->")
				.append(totnetRecov).toString());
		boolean finalrecovryFlag = actionform.isBooleanFinalRecovery();
		System.out.println((new StringBuilder())
				.append("the value of Final Recovery is :--->")
				.append(finalrecovryFlag).toString());
		if (finalrecovryFlag && totnetRecov >= firstInstallRelease) {
			String a = actionform.getCloserrequest();
			String b = actionform.getLtr_ref();
			System.out.println((new StringBuilder())
					.append("THe Clousrer request Status Flag is :--->")
					.append(a).toString());
			System.out
					.println((new StringBuilder())
							.append("The Lter ref number is :-->").append(b)
							.toString());
			if (a.equals("false") || b.equals("")) {
				System.out
						.println("The clouser request and ltr number are mandetory ...!");
				throw new NoMemberFoundException(
						"Must enter the clouser Request and LTR  Details.");
			}
		}
		System.out.println((new StringBuilder())
				.append("the value of CGPAN is:--->").append(cgPan).toString());
		System.out.println((new StringBuilder())
				.append("the value of claim Rff No  is:--->").append(clmreff)
				.toString());
		Date date = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String toadsDate = formatter.format(date);
		System.out.println((new StringBuilder()).append("Today : ")
				.append(toadsDate).toString());
		currentDate = DateHelper.stringToDBDate(toadsDate);
		System.out.println((new StringBuilder())
				.append("The Current Date is :--->").append(currentDate)
				.toString());
		reciptDate = DateHelper.stringToDBDate(recDate);
		dddbDate = DateHelper.stringToDBDate(ddDate);
		System.out.println((new StringBuilder())
				.append("after convertig dd date in format for DATA base:-->")
				.append(dddbDate).toString());
		boolean falgvalue1 = false;
		falgvalue = "";
		try {
			falgvalue1 = actionform.isBooleanFinalRecovery();
			System.out.println((new StringBuilder())
					.append("The Falg Value is :---->").append(falgvalue)
					.toString());
			if (falgvalue1) {
				falgvalue = "Y";
				System.out.println("here is control setting flag value Y");
			} else {
				falgvalue = "N";
				System.out.println("here is control setting flag value N");
			}
		} catch (Exception e) {
			e.getMessage();
		}
		int rowCount = 1;
		connection = DBConnection.getConnection();
		int count = 0;
		try {
			Statement str = connection.createStatement();
			System.out.println("Inside Try");
			String insertRecovery = (new StringBuilder())
					.append("INSERT INTO CLM_CGPAN_RECOVERY_DETAIL( CCRD_SR_NO, CLM_REF_NO, CGPAN, CCRD_RECEIPT_DATE, CCRD_RECOVERY_AMOUNT, CCRD_RECOVERY_EXPENSES, CCRD_EXPENSES_DEDUCTED, CCRD_NET_RECOVERY, CCRD_REC_DD_NO,  CCRD_REC_DD_DT, CCRD_REMARKS,CCRD_CREATED_MODIFIED_BY,CCRD_CREATED_MODIFIED_DT,CCRD_FINAL_RECOVERY )\nVALUES (CLM_CGP_REC_SEQ.nextval,'")
					.append(clmreff).append("', '").append(cgPan)
					.append("', '").append(reciptDate).append("' ,'")
					.append(amtreciept).append("' ,'").append(expInForrecover)
					.append("' ,'").append(booleanExp).append("' ,'")
					.append(netrecover).append("' ,'").append(ddNo)
					.append("' ,'").append(dddbDate).append("' ,'")
					.append(remark).append("','").append(userid).append("','")
					.append(currentDate).append("' ,'").append(falgvalue)
					.append("')").toString();
			str.executeUpdate(insertRecovery);
			System.out.println("after query fire ");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.freeConnection(connection);
			Log.log(Log.INFO, "ClaimAction", "SaveClaimrecovryData", "Exited");
		}
		return mapping.findForward("recoverysuccess");
	}

	public ActionForward displayClaimApprovalMod14102011_pradeep(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		String flagClmRefDtl = claimForm.getClmRefDtlSet();
		java.sql.Date stDt = java.sql.Date.valueOf(DateHelper
				.stringToSQLdate(claimForm.getDateOfTheDocument36()));
		java.sql.Date endDt = java.sql.Date.valueOf(DateHelper
				.stringToSQLdate(claimForm.getDateOfTheDocument37()));
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("flagClmRefDtl :")
						.append(flagClmRefDtl).toString());
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalMod()",
				"*******************************");
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String payAmntNow = null;
		String userRemarks = null;
		String standardRemarks = null;
		String microFlag = null;
		String womenOwned = null;
		String nerFlag = "N";
		double tcServiceFee = 0.0D;
		double wcServiceFee = 0.0D;
		double tcClaimEligibleAmt = 0.0D;
		double wcClaimEligibleAmt = 0.0D;
		double tcFirstInstallment = 0.0D;
		double wcFirstInstallment = 0.0D;
		double totalTCOSAmountAsOnNPA = 0.0D;
		double totalWCOSAmountAsOnNPA = 0.0D;
		double tcrecovery = 0.0D;
		double wcrecovery = 0.0D;
		double tcIssued = 0.0D;
		double wcIssued = 0.0D;
		String falgforCasesafet = "N";
		Administrator admin = new Administrator();
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		User loggedUser = getUserInformation(request);
		String loggedUserId = loggedUser.getUserId();
		userIds.remove(loggedUserId);
		userIds.remove("ADMIN");
		if (userIds.contains("DEMOUSER"))
			userIds.remove("DEMOUSER");
		if (userIds.contains("AUDITOR"))
			userIds.remove("AUDITOR");
		claimForm.setUserIds(userIds);
		claimForm.setUserId(loggedUserId);
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		Date dateofReceipt = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String loggedUsr = user.getUserId();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalMod()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		if (flagClmRefDtl != null && flagClmRefDtl.equals("Y")) {
			clmdtl = claimForm.getClaimdetail();
			if (clmdtl != null) {
				clmRefNum = clmdtl.getClaimRefNum();
				dateofReceipt = claimForm.getDateofReceipt();
				tcIssued = claimForm.getTcIssued();
				wcIssued = claimForm.getWcIssued();
				userRemarks = clmdtl.getComments();
				standardRemarks = clmdtl.getStandardRemarks();
				userRemarks = standardRemarks.concat(userRemarks);
				totalTCOSAmountAsOnNPA = clmdtl.getTotalTCOSAmountAsOnNPA();
				totalWCOSAmountAsOnNPA = clmdtl.getTotalWCOSAmountAsOnNPA();
				tcrecovery = claimForm.getTcrecovery();
				wcrecovery = claimForm.getWcrecovery();
				microFlag = claimForm.getMicroCategory();
				falgforCasesafet = claimForm.getFalgforCasesafet();
				womenOwned = claimForm.getWomenOperated();
				nerFlag = claimForm.getNerFlag();
				if (tcIssued + wcIssued <= 500000D && microFlag.equals("Y")) {
					if (falgforCasesafet.equals("Y"))
						tcClaimEligibleAmt = Math
								.round((Math.min(tcIssued,
										totalTCOSAmountAsOnNPA) - tcrecovery) * 0.84999999999999998D);
					else if (falgforCasesafet.equals("N"))
						tcClaimEligibleAmt = Math
								.round((Math.min(tcIssued,
										totalTCOSAmountAsOnNPA) - tcrecovery) * 0.80000000000000004D);
				} else if (tcIssued + wcIssued <= 5000000D
						&& (womenOwned.equals("F") || nerFlag.equals("Y")))
					tcClaimEligibleAmt = Math
							.round((Math.min(tcIssued, totalTCOSAmountAsOnNPA) - tcrecovery) * 0.80000000000000004D);
				else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("N"))
					tcClaimEligibleAmt = Math.round((Math.min(tcIssued,
							totalTCOSAmountAsOnNPA) - tcrecovery) * 0.75D);
				else
					tcClaimEligibleAmt = Math.round((Math.min(tcIssued,
							totalTCOSAmountAsOnNPA) - tcrecovery) * 0.75D);
				if (tcIssued + wcIssued <= 500000D && microFlag.equals("Y")) {
					if (falgforCasesafet.equals("Y"))
						wcClaimEligibleAmt = Math
								.round((Math.min(wcIssued,
										totalWCOSAmountAsOnNPA) - wcrecovery) * 0.84999999999999998D);
					else if (falgforCasesafet.equals("N"))
						wcClaimEligibleAmt = Math
								.round((Math.min(wcIssued,
										totalWCOSAmountAsOnNPA) - wcrecovery) * 0.80000000000000004D);
				} else if (tcIssued + wcIssued <= 5000000D
						&& (womenOwned.equals("F") || nerFlag.equals("Y")))
					wcClaimEligibleAmt = Math
							.round((Math.min(wcIssued, totalWCOSAmountAsOnNPA) - wcrecovery) * 0.80000000000000004D);
				else if (tcIssued + wcIssued <= 500000D
						&& microFlag.equals("N"))
					wcClaimEligibleAmt = Math.round((Math.min(wcIssued,
							totalWCOSAmountAsOnNPA) - wcrecovery) * 0.75D);
				else
					wcClaimEligibleAmt = Math.round((Math.min(wcIssued,
							totalWCOSAmountAsOnNPA) - wcrecovery) * 0.75D);
				if (tcClaimEligibleAmt < 0.0D)
					tcClaimEligibleAmt = 0.0D;
				if (wcClaimEligibleAmt < 0.0D)
					wcClaimEligibleAmt = 0.0D;
				tcFirstInstallment = Math.round(tcClaimEligibleAmt * 0.75D);
				wcFirstInstallment = Math.round(wcClaimEligibleAmt * 0.75D);
				tcServiceFee = claimForm.getAsfDeductableforTC();
				wcServiceFee = claimForm.getAsfDeductableforWC();
				payAmntNow = Double
						.toString((tcFirstInstallment + wcFirstInstallment)
								- tcServiceFee - wcServiceFee);
				clmdtl.setTotalAmtPayNow(payAmntNow);
				CPDAO cpdao = new CPDAO();
				cpdao.insertClaimProcessDetails(clmRefNum, userRemarks,
						tcServiceFee, wcServiceFee, tcClaimEligibleAmt,
						wcClaimEligibleAmt, tcFirstInstallment,
						wcFirstInstallment, totalTCOSAmountAsOnNPA,
						totalWCOSAmountAsOnNPA, tcrecovery, wcrecovery,
						dateofReceipt);
				if (payAmntNow != null && !payAmntNow.equals("")) {
					if (Double.parseDouble(payAmntNow) < 0.0D)
						payAmntNow = "0.0";
				} else {
					payAmntNow = "0.0";
				}
			}
		}
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String thiskey = null;
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor.getClaimProcessingDetailsMod(
				"F", stDt, endDt);
		if (firstinstllmntclaims != null) {
			for (int i = 0; i < firstinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) firstinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"##################");
					String memId = clmDtl.getMliId();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("memId :")
									.append(memId).toString());
					String claimrefnumber = clmDtl.getClaimRefNum();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("claimrefnumber :")
									.append(claimrefnumber).toString());
					clmStatus = clmDtl.getClmStatus();
					Log.log(Log.INFO,
							"ClaimAction",
							"displayClaimApproval()",
							(new StringBuilder()).append("clmStatus :")
									.append(clmStatus).toString());
					comments = clmDtl.getComments();
					forwardedToUser = clmDtl.getForwaredToUser();
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							(new StringBuilder()).append("forwardedToUser :")
									.append(forwardedToUser).toString());
					Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()",
							"###################");
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						firstinstllmntclaims.remove(i);
						i--;
					} else if (clmStatus != null
							&& (clmStatus.equals("FW") || clmStatus
									.equals("HO"))) {
						thiskey = (new StringBuilder()).append("F#")
								.append(memId).append("#")
								.append(claimrefnumber).toString();
						claimForm.setDecision(thiskey, clmStatus);
						claimForm.setRemarks(thiskey, comments);
						Log.log(Log.INFO, "ClaimAction",
								"displayClaimApproval()", (new StringBuilder())
										.append("loggedUsr :")
										.append(loggedUsr).toString());
						if (forwardedToUser != null
								&& !forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							firstinstllmntclaims.remove(i);
							i--;
						}
						if (forwardedToUser != null
								&& forwardedToUser.equalsIgnoreCase(loggedUsr)) {
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("thiskey :")
											.append(thiskey).toString());
							Log.log(Log.INFO,
									"ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder())
											.append("forwardedToUser :")
											.append(forwardedToUser).toString());
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									"*******************");
							claimForm.setForwardedToIds(thiskey,
									forwardedToUser);
							if (userIds.contains(forwardedToUser))
								claimForm.setUserIds(userIds);
						}
					}
				}
			}

		}
		Vector secinstllmntclaims = processor.getClaimProcessingDetails("S");
		if (secinstllmntclaims != null) {
			for (int i = 0; i < secinstllmntclaims.size(); i++) {
				ClaimDetail clmDtl = (ClaimDetail) secinstllmntclaims
						.elementAt(i);
				if (clmDtl != null) {
					clmEligibleAmnt = clmDtl.getEligibleClaimAmt();
					if (fromDate != null && !fromDate.equals("")
							&& toDate != null && !toDate.equals("")
							&& fromDate.compareTo(currentDate) <= 0
							&& currentDate.compareTo(toDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else if (fromDate != null && !fromDate.equals("")
							&& toDate == null
							&& fromDate.compareTo(currentDate) <= 0
							&& clmEligibleAmnt > maxClmApprvdAmnt) {
						outOfLimit++;
						secinstllmntclaims.remove(i);
						i--;
					} else {
						String memId = clmDtl.getMliId();
						String claimrefnumber = clmDtl.getClaimRefNum();
						String cgclan = clmDtl.getCGCLAN();
						clmStatus = clmDtl.getClmStatus();
						comments = clmDtl.getComments();
						forwardedToUser = clmDtl.getForwaredToUser();
						Log.log(Log.INFO,
								"ClaimAction",
								"displayClaimApproval()",
								(new StringBuilder())
										.append("forwardedToUser :")
										.append(forwardedToUser).toString());
						if (clmStatus != null
								&& (clmStatus.equals("FW") || clmStatus
										.equals("HO"))) {
							thiskey = (new StringBuilder()).append("S#")
									.append(memId).append("#")
									.append(claimrefnumber).append("#")
									.append(cgclan).toString();
							claimForm.setDecision(thiskey, clmStatus);
							claimForm.setRemarks(thiskey, comments);
							Log.log(Log.INFO, "ClaimAction",
									"displayClaimApproval()",
									(new StringBuilder()).append("loggedUsr :")
											.append(loggedUsr).toString());
							if (forwardedToUser != null
									&& !forwardedToUser
											.equalsIgnoreCase(loggedUsr)) {
								secinstllmntclaims.remove(i);
								i--;
							}
							if (forwardedToUser != null
									&& forwardedToUser
											.equalsIgnoreCase(loggedUsr))
								claimForm.setForwardedToIds(thiskey,
										forwardedToUser);
						}
					}
				}
			}

		}
		if (firstinstllmntclaims.size() == 0 && secinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to process.");
			return mapping.findForward("success");
		}
		for (int i = 0; i < firstinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) firstinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) firstinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				cd.setComments(standardRemarks.concat(userRemarks));
				firstinstllmntclaims.addElement(cd);
			}
		}

		for (int i = 0; i < secinstllmntclaims.size(); i++) {
			ClaimDetail cd = (ClaimDetail) secinstllmntclaims.elementAt(i);
			String crn = cd.getClaimRefNum();
			if (crn.equals(clmRefNum)) {
				cd = (ClaimDetail) secinstllmntclaims.remove(i);
				cd.setTotalAmtPayNow(payAmntNow);
				secinstllmntclaims.addElement(cd);
			}
		}

		clmdtl = null;
		clmRefNum = null;
		payAmntNow = null;
		claimForm.setLimit(outOfLimit);
		claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
		claimForm.setFirstCounter(firstinstllmntclaims.size());
		claimForm.setSecondInstallmentClaims(secinstllmntclaims);
		claimForm.setSecondCounter(secinstllmntclaims.size());
		claimForm.setClmRefDtlSet("N");
		firstinstllmntclaims = null;
		secinstllmntclaims = null;
		Log.log(Log.INFO, "ClaimAction", "displayClaimApproval()", "Exited");
		return mapping.findForward("displayClaimsApprovalPage");
	}

	/* added by upchar@path on 04-04-2013 */
	public ActionForward displayUpdatePaymentDetailInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayUpdatePaymentDetailInput",
				"Entered");
		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		String startDate = dateFormat.format(prevDate);
		String endDate = dateFormat.format(date);
		claimForm.setDateOfTheDocument36(startDate);
		claimForm.setDateOfTheDocument37(endDate);
		Log.log(Log.INFO, "ClaimAction", "displayClaimProcessingInput",
				"Exited");
		return mapping.findForward("success");
	}

	public ActionForward getPaymentDetailsForMemeberID(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "getPaymentDetailsForMemeberID",
				"Entered");
		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		String mliID = claimForm.getMemberID();
		String startDate = claimForm.getDateOfTheDocument36();
		String endDate = claimForm.getDateOfTheDocument37();
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		Date prevDate = calendar.getTime();
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		if (startDate.equals(null) || startDate.equals(""))
			throw new NoDataException(
					"From Date shold not be empty, Please Enter from Date ");
		if (startDate.length() > 10 || startDate.length() < 10)
			throw new NoDataException("date format should be dd/MM/yyyy");
		if (endDate.equals(null) || endDate.equals(""))
			throw new NoDataException(
					"To Date shold not be empty, Please Enter To Date ");
		if (endDate.length() > 10 || endDate.length() < 10)
			throw new NoDataException("date format should be dd/MM/yyyy");
		if (!mliID.equals("") && mliID != null) {
			if (mliID.length() < 12)
				throw new NoDataException(
						"Member ID can not be less than 12 characters, Please Enter 12 Digit Member ID ");
			if (!memberids.contains(mliID)) {
				Log.log(2, "CPDAO", "getAllMemberIds()",
						"No Member Ids in the database!");
				throw new DatabaseException("No Member Ids in the database");
			}
		}
		ArrayList paymentCaseDetails = claimsProcessor
				.getPaymentDetailsForMemeberID(mliID, startDate, endDate);
		System.out.println((new StringBuilder()).append("size of arraylist:")
				.append(paymentCaseDetails.size()).toString());
		String pendingCaseDetailsArray_size = new String((new Integer(
				paymentCaseDetails.size())).toString());
		request.setAttribute("pendingCaseDetailsArray_size",
				pendingCaseDetailsArray_size);
		request.setAttribute("pendingCaseDetailsArray", paymentCaseDetails);
		claimForm.setDateOfTheDocument36(startDate);
		claimForm.setDateOfTheDocument37(endDate);
		Log.log(Log.INFO, "ClaimAction", "getPaymentDetailsForMemeberID",
				"Exited");
		System.out.println("Exited from getPaymentDetailsForMemeberID-----");
		return mapping.findForward("success");
	}

	public ActionForward updateDayWisePaymentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String fromDate = claimForm.getDateOfTheDocument36();
		String toDate = claimForm.getDateOfTheDocument37();

		List paymentDetails = null;
		String paymentArray[] = null;
		String nextPage = "success";

		Date paymentDate = claimForm.getPaymentDate();
		Date outwardDate = claimForm.getOutwardDate();
		String pymtaccountNO = claimForm.getAccountNO();
		String outwardNO = claimForm.getOutwardNO();
		String UTRNO = claimForm.getUTRNO();
		Map depositedFlags = claimForm.getDepositedFlags();
		paymentDetails = new ArrayList();
		Set set = depositedFlags.keySet();
		Iterator itr = set.iterator();
		String clmrefno = null;

		while (itr.hasNext()) {
			clmrefno = (String) itr.next();
			paymentDetails.add(clmrefno);
		}

		// for(Iterator itr = set.iterator(); itr.hasNext();
		// paymentDetails.add(clmrefno)){
		// clmrefno = (String)itr.next();
		// }
		if (paymentDetails.size() != 0) {
			String paymentDateStr = null;
			String outwardDateStr = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (paymentDate != null)
				paymentDateStr = sdf.format(paymentDate);
			if (outwardDate != null)
				outwardDateStr = sdf.format(outwardDate);

			paymentArray = new String[5];
			paymentArray[0] = UTRNO;
			paymentArray[1] = paymentDateStr;
			paymentArray[2] = pymtaccountNO;
			paymentArray[3] = outwardNO;
			paymentArray[4] = outwardDateStr;
			ClaimsProcessor claimsProcessor = new ClaimsProcessor();

			try {
				System.out.println("---before updateDayWisePaymentDetails---");
				claimsProcessor.updateDayWisePaymentDetails(paymentDetails,
						paymentArray);
				System.out.println("---after updateDayWisePaymentDetails---");
			} catch (DatabaseException e) {
				e.printStackTrace();
				throw new DatabaseException();
			}

		} else {
			nextPage = "inputPage";
		}
		return mapping.findForward(nextPage);
	}

	// added by upchar@path on 02/06/2013
	public ActionForward claimReplyReceivedInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		// String mliID = claimForm.getMemberID();

		String userId = user.getUserId();
		claimForm.setUserId(userId);

		return mapping.findForward("success");
	}

	public ActionForward insertClaimReplyReceivedDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();
		ClaimActionForm claimForm = (ClaimActionForm) form;

		String userId = claimForm.getUserId();
		String inwardno = request.getParameter("inwardno");
		String inwarddtstr = request.getParameter("inwarddt");
		String clmrefno = claimForm.getClaimRefNum();

		Connection conn = null;
		CallableStatement callableStmt = null;
		Date inwarddt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (inwardno != null && !inwardno.equals(""))
			inwardno = inwardno.trim();
		if (clmrefno != null && !clmrefno.equals(""))
			clmrefno = clmrefno.trim();

		if (inwarddtstr != null || !inwarddtstr.equals(""))
			inwarddt = sdf.parse(inwarddtstr);
		try {
			conn = DBConnection.getConnection(false);

			callableStmt = conn
					.prepareCall("{? = call Funcinsclmreplydet(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, 4);
			callableStmt.setString(2, clmrefno);
			callableStmt.setString(3, inwardno);
			callableStmt.setDate(4, new java.sql.Date(inwarddt.getTime()));
			callableStmt.setString(5, userId);
			callableStmt.registerOutParameter(6, 12);
			callableStmt.execute();

			int error = callableStmt.getInt(1);
			String exception = callableStmt.getString(6);

			if (error == 1) {
				callableStmt.close();
				callableStmt = null;

				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}

				throw new DatabaseException(exception);
			}
		} catch (DatabaseException sqlexception) {

			try {
				if (callableStmt != null)
					callableStmt.close();
				conn.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} catch (SQLException sqlexception) {

			try {
				if (callableStmt != null)
					callableStmt.close();
				conn.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} finally {

			DBConnection.freeConnection(conn);
			request.setAttribute("message", "Information Saved Successfully");

		}

		return mapping.findForward("success");
	}

	public ActionForward claimStatusChangeForRR(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "claimStatusChange", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		claimForm.setClaimRefNum("");
		claimForm.setEnterCgpan("");
		return mapping.findForward("success");
	}

	public ActionForward afterRRClaimStatusChange(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "updateRRClaimApplicationStatus",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		String claimRefNum = claimForm.getClaimRefNum();
		String cgpan = claimForm.getEnterCgpan();
		CPDAO cpDAO = new CPDAO();
		String claimRefNo = null;
		boolean statusFlag = false;
		User user = getUserInformation(request);
		String userId = user.getUserId();

		if (claimRefNum != null && claimRefNum != "")
			claimRefNum = claimRefNum.trim();
		if (cgpan != null && !cgpan.equals(""))
			cgpan = cgpan.trim();

		if (claimRefNum != null && !claimRefNum.equals("") && cgpan != null
				&& !cgpan.equals("")) {
			claimRefNo = cpDAO.getClaimRefNo(cgpan);
			if (!claimRefNum.equals(claimRefNo))
				throw new DatabaseException(
						"CGPAN and Claim Ref no does not Match");
			updateRRClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		} else if (claimRefNum != null && !claimRefNum.equals("")) {
			updateRRClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		} else if (cgpan != null && !cgpan.equals("")) {
			updateRRClaimApplicationStatus(claimRefNum, cgpan, userId);
			statusFlag = true;
		}
		if (!statusFlag) {

			throw new DatabaseException(
					"Unable to Update Claim Application Status");
		} else {
			request.setAttribute("message",
					"Claim Application Status Changed Successfully");
			Log.log(Log.INFO, "ClaimAction", "afterclaimStatusChange", "Exited");
			return mapping.findForward("success");
		}
	}

	public void updateRRClaimApplicationStatus(String claimRefNo, String cgpan,
			String userId) throws DatabaseException {
		Connection connection = null;
		CallableStatement stmt = null;
		try {
			connection = DBConnection.getConnection(false);
			stmt = connection
					.prepareCall("{?=call Funcchangeclmstatusfromrr(?,?,?,?)}");
			stmt.registerOutParameter(1, 4);
			if (claimRefNo != null && !claimRefNo.equals(""))
				stmt.setString(2, claimRefNo);
			else
				stmt.setNull(2, 12);
			if (cgpan != null && !cgpan.equals(""))
				stmt.setString(3, cgpan);
			else
				stmt.setNull(3, 12);
			stmt.setString(4, userId);
			stmt.registerOutParameter(5, 12);
			stmt.execute();
			int status = stmt.getInt(1);
			String err = stmt.getString(5);

			if (status == 1) {
				stmt.close();
				stmt = null;

				try {
					connection.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}

				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;
			Log.log(Log.INFO, "CPDAO", "updateRRClaimApplicationStatus",
					"funcCancAllocation executed successfully");
		} catch (DatabaseException sqlexception) {

			try {
				if (stmt != null)
					stmt.close();
				connection.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} catch (SQLException sqlexception) {

			try {
				if (stmt != null)
					stmt.close();
				connection.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
	}

	public ActionForward displayClaimApprovalNewForNewCases(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalNew()", "Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimsProcessor processor = new ClaimsProcessor();
		ClaimDetail clmdtl = null;
		String clmRefNum = null;
		String claimRetRemarks = null;
		String payAmntNow = null;
		Administrator admin = new Administrator();
		String cgtsiBankId = "0000";
		String cgtsiZoneId = "0000";
		String cgtsiBrnId = "0000";
		ArrayList userIds = admin.getUsers((new StringBuilder())
				.append(cgtsiBankId).append(cgtsiZoneId).append(cgtsiBrnId)
				.toString());
		double maxClmApprvdAmnt = 0.0D;
		Date fromDate = null;
		Date toDate = null;
		User user = getUserInformation(request);
		String designation = user.getDesignation();
		String bankName = request.getParameter("Link");
		bankName = bankName.replaceAll("PATH", "&");
		
		String filetype = request.getParameter("fileType");
        System.out.println("filetype=="+filetype);
		String loggedUsr = user.getUserId();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalNew()",
				(new StringBuilder()).append("designation :")
						.append(designation).toString());
		HashMap clmLimitDetails = processor.getClaimLimitDtls(designation);
		maxClmApprvdAmnt = ((Double) clmLimitDetails
				.get("Max Claim Approved Amount")).doubleValue();
		Log.log(Log.INFO,
				"ClaimAction",
				"displayClaimApprovalNew()",
				(new StringBuilder()).append("maxClmApprvdAmnt :")
						.append(maxClmApprvdAmnt).toString());
		fromDate = (Date) clmLimitDetails.get("Claim Valid From Date");
		toDate = (Date) clmLimitDetails.get("Claim Valid To Date");
		int outOfLimit = 0;
		double clmEligibleAmnt = 0.0D;
		Date currentDate = new Date();
		Vector firstinstllmntclaims = processor
				.getClaimApprovalDetailsForNewCases(loggedUsr, bankName,filetype);
		if (firstinstllmntclaims.size() == 0) {
			request.setAttribute("message",
					"There are no Claim Application(s) to Approve.");
			return mapping.findForward("success");
		} else {
			claimForm.setLimit(outOfLimit);
			claimForm.setFirstInstallmentClaims(firstinstllmntclaims);
			claimForm.setFirstCounter(firstinstllmntclaims.size());
			claimForm.setClmRefDtlSet("N");
			firstinstllmntclaims = null;
			Log.log(Log.INFO, "ClaimAction", "displayClaimApprovalNew()",
					"Exited");
			return mapping.findForward("displayClaimApprovalNewForNewCases");
		}
	}

	public ActionForward updateConsolilidDeclnReportDetailed(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		GMDAO gmDAO = new GMDAO();
		RpDAO rpDAO = new RpDAO();
		// ApplicationProcessor appProcessor=new ApplicationProcessor();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		// Connection connection = DBConnection.getConnection(false);
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Map modifCgpan = claimForm.getClosureCgpan();

		// System.out.println("modifCgpan size is"+modifCgpan.size());

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		java.util.Date eDate = null;

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dateInString = "07/06/2013";

		try {

			eDate = formatter.parse(claimForm.getEffortsConclusionDate2());

			// java.util.Date eDate = (java.util.Date)
			// claimForm.getClaimConsDate();
			endDate = new java.sql.Date(eDate.getTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// System.out.println("date is"+endDate);

		java.util.Date todaydate = new Date();

		// System.out.println("todaydate is"+endDate);

		// System.out.println("compare"+(todaydate.compareTo(endDate)));

		ArrayList claimDeclArrayList = new ArrayList();

		int claimCount;

		String clm1 = null;

		int month = 7;
		int year = 1970;
		int hours = 0;
		int day = 0;
		int min = 0;
		String strDay = "";
		String strMonth = "";
		String rDate = "";
		Calendar cal = Calendar.getInstance();
		// System.out.println("into else"+dateP.get(dateP.DATE)+"year "+dateP.get(dateP.YEAR)+"month"+dateP.get(dateP.HOUR)+"amorp"+dateP.get(dateP.AM_PM));
		try {

			day = cal.get(Calendar.DATE);
			month = cal.get(Calendar.MONTH);
			year = cal.get(Calendar.YEAR);
			// hours = cal.get(cal. 22-02-2000
			month++;

			if (month == 0)
				month = 12;
			strDay = day + "";
			strMonth = month + "";
			if (strDay.length() == 1) {
				strDay = "0" + strDay;
			}
			if (strMonth.length() == 1) {
				strMonth = "0" + strMonth;
			}

			rDate = strDay + "/" + strMonth + "/" + year;

		} catch (Exception ert) {
			return null;
		}

		// System.out.println("strDay"+strDay+"strMonth"+strMonth+"year"+year);

		String edateStr = endDate.toString();

		// System.out.println("edateStr"+edateStr);

		String todaydateStr = todaydate.toString().trim();

		// System.out.println("todaydateStr"+todaydateStr);

		StringTokenizer st = new StringTokenizer(edateStr, dbDateSeparator);
		if (st.countTokens() != 3) {

		}

		String yr1 = st.nextToken();
		String mth1 = st.nextToken();
		String day1 = st.nextToken();
		if ((todaydate.compareTo(endDate)) < 0)

		{

			throw new NoDataException(
					"consolidated date should not be greater than  today's date");

		}

		if (modifCgpan.size() == 0)

		{

			throw new NoDataException("Please select Atleast One Chekbox");

		}

		Connection conn = null;
		CallableStatement callableStmt = null;
		java.sql.ResultSet rs = null;
		conn = DBConnection.getConnection();
		java.util.Date fromDate = null;
		java.util.Date toDate = null;

		Set modifCgpanSet = modifCgpan.keySet();
		Iterator modifCgpanIterator = modifCgpanSet.iterator();
		try {
			while (modifCgpanIterator.hasNext()) {
				String claimRefNumber = (String) modifCgpanIterator.next();
				String decision = (String) modifCgpan.get(claimRefNumber);
				if (!(decision.equals(""))) {

					callableStmt = conn
							.prepareCall("{? = call PackGetClmListForDeclUpd.FuncUpdClmDeclRecvdDt(?,?,?,?)}");

					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);// error
																				// code
					callableStmt.setString(2, claimRefNumber);
					callableStmt.setDate(3, new java.sql.Date(eDate.getTime()));
					callableStmt.setString(4, userId);

					callableStmt
							.registerOutParameter(5, java.sql.Types.VARCHAR);// for
																				// exception

					callableStmt.execute();

					int error = callableStmt.getInt(1);
					String exception = callableStmt.getString(5);

					// System.out.println("function return value:"+error);
					if (error == Constants.FUNCTION_FAILURE) {

						callableStmt.close();
						callableStmt = null;

						conn.rollback();

						// throw new DatabaseException(getDanDetailsErr);

						throw new DatabaseException(exception);

					}
				}
			}

			conn.commit();

		}

		catch (SQLException sqlException) {
			try {
				conn.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(conn);
		}

		modifCgpan.clear();

		request.setAttribute(
				"message",
				"<b>The Request for Consolidation Declaration has been successfully Updated.<b><br>");

		return mapping.findForward("success");
	}

	public ActionForward updateConsolilidDeclnInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "NewReportsAction", "claimDeclarationInput",
				"Entered");

		ClaimActionForm claimForm = (ClaimActionForm) form;

		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);

		java.util.Date date = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		month--;
		day++;
		calendar.set(2, month);
		calendar.set(5, day);
		java.util.Date prevDate = calendar.getTime();
		String startDate = dateFormat.format(prevDate);
		String endDate = dateFormat.format(date);

		// claimForm.setDateOfTheDocument36(startDate);
		claimForm.setClaimConsDate(endDate);

		return mapping.findForward("success");
	}

	public ActionForward updateConsolilidDeclnReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ReportsAction", "npaReport", "Entered");
		ArrayList npaDetails = new ArrayList();
		ClaimActionForm claimForm = (ClaimActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		// java.util.Date sDate = (java.util.Date)
		// claimForm.get("dateOfTheDocument26");
		// String stDate = String.valueOf(sDate);

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dateInString = "07/06/2013";

		try {

			Date eDate = formatter.parse(claimForm.getClaimConsDate());

			endDate = new java.sql.Date(eDate.getTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("date is" + endDate);

		java.util.Date todaydate = new Date();

		// System.out.println("todaydate is"+endDate);

		// System.out.println("compare"+(todaydate.compareTo(endDate)));

		ArrayList claimDeclArrayList = new ArrayList();

		int claimCount;

		String clm1 = null;

		int month = 7;
		int year = 1970;
		int hours = 0;
		int day = 0;
		int min = 0;
		String strDay = "";
		String strMonth = "";
		String rDate = "";
		Calendar cal = Calendar.getInstance();
		// System.out.println("into else"+dateP.get(dateP.DATE)+"year "+dateP.get(dateP.YEAR)+"month"+dateP.get(dateP.HOUR)+"amorp"+dateP.get(dateP.AM_PM));
		try {

			day = cal.get(Calendar.DATE);
			month = cal.get(Calendar.MONTH);
			year = cal.get(Calendar.YEAR);
			// hours = cal.get(cal. 22-02-2000
			month++;

			if (month == 0)
				month = 12;
			strDay = day + "";
			strMonth = month + "";
			if (strDay.length() == 1) {
				strDay = "0" + strDay;
			}
			if (strMonth.length() == 1) {
				strMonth = "0" + strMonth;
			}

			rDate = strDay + "/" + strMonth + "/" + year;

		} catch (Exception ert) {
			return null;
		}

		// System.out.println("strDay"+strDay+"strMonth"+strMonth+"year"+year);

		String edateStr = endDate.toString();

		// System.out.println("edateStr"+edateStr);

		String todaydateStr = todaydate.toString().trim();

		// System.out.println("todaydateStr"+todaydateStr);

		StringTokenizer st = new StringTokenizer(edateStr, dbDateSeparator);
		if (st.countTokens() != 3) {

		}

		String yr1 = st.nextToken();
		String mth1 = st.nextToken();
		String day1 = st.nextToken();

		if ((todaydate.compareTo(endDate)) < 0)

		{

			throw new NoDataException(
					"consolidated date should not be greater than  today's date");

		}

		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();

		String mliID = (String) claimForm.getMemberId();

		if ((mliID.equals("")) || (mliID.equals(null))) {
			throw new NoDataException("Please Enter 12 Digit Member ID");
			// throws
			// NewDateException("MEMBER ID LENGHT SHOULD NOT BE 12 DIGITS ")
		}

		if (!(mliID.equals("")) && (mliID != null)) {

			if (mliID.length() < 12) {
				throw new NoDataException(
						"Member ID can not be less than 12 characters,"
								+ " Please Enter 12 Digit Member ID ");
				// throws
				// NewDateException("MEMBER ID LENGHT SHOULD NOT BE 12 DIGITS ")
			}
			if (!memberids.contains(mliID)) {
				Log.log(Log.ERROR, "CPDAO", "getAllMemberIds()",
						"No Member Ids in the database!");
				throw new DatabaseException("No Member Ids in the database");
			}
		}

		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Connection conn = null;
		CallableStatement callableStmt = null;
		java.sql.ResultSet rs = null;
		conn = DBConnection.getConnection();
		java.util.Date fromDate = null;
		java.util.Date toDate = null;
		java.util.ArrayList clmListForDeclUpddationList = new java.util.ArrayList();
		String[] clmArrayForDeclUpddation = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			fromDate = sdf.parse(edateStr);
			System.out.println(fromDate.getTime());
			// toDate = sdf.parse(endDate);
		} catch (ParseException e) {
			// TODO
		}

		try {

			callableStmt = conn
					.prepareCall("{? = call PackGetClmListForDeclUpd.funcGetClmListForDeclUpd(?,?,?,?)}");

			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);// error
																			// code
			callableStmt.setString(2, mliID);
			callableStmt.setDate(3, new java.sql.Date(endDate.getTime()));
			// callableStmt.setDate(3,new java.sql.Date(toDate.getTime()));

			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);// for
																			// exception

			callableStmt.execute();

			int error = callableStmt.getInt(1);
			String exception = callableStmt.getString(5);

			// System.out.println("function return value:"+error);
			if (error == Constants.FUNCTION_FAILURE) {

				callableStmt.close();
				callableStmt = null;

				throw new DatabaseException(exception);

			} else {
				rs = (ResultSet) callableStmt.getObject(4);

				while (rs.next()) {
					clmArrayForDeclUpddation = new String[8];

					clmArrayForDeclUpddation[0] = rs.getString(1);
					clmArrayForDeclUpddation[1] = rs.getString(2);
					clmArrayForDeclUpddation[2] = rs.getString(3);
					clmArrayForDeclUpddation[3] = sdf.format(rs.getDate(4));

					clmArrayForDeclUpddation[4] = rs.getString(5);
					clmArrayForDeclUpddation[5] = sdf.format(rs.getDate(6));
					clmArrayForDeclUpddation[6] = rs.getString(7);

					clmListForDeclUpddationList.add(clmArrayForDeclUpddation);
				}
				rs.close();
				rs = null;
				callableStmt.close();
				callableStmt = null;

				java.util.Date date2 = new java.util.Date();
				Calendar calendar2 = Calendar.getInstance();
				SimpleDateFormat dateFormat2 = new SimpleDateFormat(
						"dd/MM/yyyy");
				calendar2.setTime(date2);
				int month2 = calendar2.get(2);
				int day2 = calendar2.get(5);
				month2--;
				day2++;
				calendar2.set(2, month2);
				calendar2.set(5, day2);
				java.util.Date prevDate = calendar2.getTime();
				// String startDate2 = dateFormat.format(prevDate);
				String endDate2 = dateFormat2.format(date2);

				// claimForm.setDateOfTheDocument36(startDate);
				claimForm.setEffortsConclusionDate2(endDate2);

				request.setAttribute("pendingCaseDetails",
						clmListForDeclUpddationList);
				request.setAttribute("pendingCaseDetailsArray",
						clmListForDeclUpddationList.size());
			}

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "CPDAO", "getPaymentDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

		}
		claimForm.setSelectAll("");
		return mapping.findForward("success");

	}

	// Diksha
	public ActionForward getMemidAndUnitName(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		Log.log(4, "ClaimAction", "getDeclartionDeatils", "Entered");
		PrintWriter out = response.getWriter();
		String memid = "";
		String unitName = "";
		String Mli_name = "";
		String Claim_RefNo = request.getParameter("clmRefNumber");
		String cgpan = "";
		String cgpan_new = "";
		ArrayList CGPAN = new ArrayList();
		StringBuilder query = new StringBuilder();
		Connection connection = DBConnection.getConnection();
		Statement str = connection.createStatement();
		ResultSet rs1 = null;
		String CLMCHECK = "";
		String l_strsetClmRef = request.getParameter("setRef");
		ClaimActionForm claimForm = (ClaimActionForm) form;
		if (Claim_RefNo != null && Claim_RefNo.length() > 0) {
			String Qury = "";
			int fistclaim_count = 0;
			Qury = " select sum(clmno) clmcheck from ( select count(clm_ref_no) clmno from claim_detail_temp where ";
			Qury = (new StringBuilder(String.valueOf(Qury)))
					.append(" clm_ref_no='").append(Claim_RefNo).append("'")
					.toString();
			Qury = (new StringBuilder(String.valueOf(Qury))).append(
					" union all ").toString();
			Qury = (new StringBuilder(String.valueOf(Qury))).append(
					" select count(clm_ref_no) clmno from claim_detail where ")
					.toString();
			Qury = (new StringBuilder(String.valueOf(Qury)))
					.append(" clm_ref_no='").append(Claim_RefNo).append("')")
					.toString();
			ResultSet rs = str.executeQuery(Qury);
			if (rs.next())
				CLMCHECK = rs.getString("clmcheck");
			if (CLMCHECK != null && CLMCHECK.length() > 0)
				fistclaim_count = Integer.parseInt(CLMCHECK);
			if (fistclaim_count == 0)
				throw new DatabaseException(
						"Entered Claim Reference number doesn't exist.");
			if (fistclaim_count > 0) {
				String clmRefNoExist = "";
				String Clm_ref_CHK_qry = (new StringBuilder(
						"select CLM_REF_NO from CLAIM_INSPECTION_DATA where CLM_REF_NO=UPPER('"))
						.append(Claim_RefNo).append("')").toString();
				rs1 = str.executeQuery(Clm_ref_CHK_qry);
				if (rs1.next()) {
					clmRefNoExist = rs1.getString("CLM_REF_NO");
					if (clmRefNoExist != null && clmRefNoExist.length() > 0)
						throw new NoMemberFoundException(
								(new StringBuilder(" Claim Ref Number '"))
										.append(clmRefNoExist)
										.append("' Already Exist. Please Enter Another Claim Ref No.")
										.toString());
				}
				query.append("SELECT a.clm_ref_no,ssi_unit_name unitname,B.cgpan,A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id memid,m.MEM_BANK_NAME ");
				query.append(" FROM claim_detail a,claim_tc_detail b,SSI_DETAIL S,APPLICATION_DETAIL D,promoter_detail p,member_info m");
				query.append(" WHERE a.clm_ref_no = b.clm_ref_no AND clm_status = 'AP' AND S.BID = A.BID AND B.CGPAN = D.CGPAN AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id=m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id ");
				query.append(" AND p.ssi_reference_number = s.ssi_reference_number AND APP_LOAN_TYPE = 'TC'");
				query.append((new StringBuilder(" AND a.clm_ref_no=UPPER ('"))
						.append(Claim_RefNo).append("')").toString());
				query.append(" UNION ALL ");
				query.append(" SELECT a.clm_ref_no,ssi_unit_name unitname,B.cgpan, A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id memid,m.MEM_BANK_NAME ");
				query.append(" FROM claim_detail a,claim_wc_detail b,SSI_DETAIL S,APPLICATION_DETAIL D,promoter_detail p,");
				query.append(" member_info m WHERE a.clm_ref_no = b.clm_ref_no AND clm_status = 'AP' AND S.BID = A.BID");
				query.append(" AND B.CGPAN = D.CGPAN AND p.ssi_reference_number = s.ssi_reference_number  AND APP_LOAN_TYPE='WC'");
				query.append(" AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id");
				query.append((new StringBuilder(" AND a.clm_ref_no=UPPER ('"))
						.append(Claim_RefNo).append("')").toString());
				rs = str.executeQuery(query.toString());
				String memberid = "";
				for (; rs.next(); CGPAN.add(rs.getString("cgpan"))) {
					memid = rs.getString("memid");
					unitName = rs.getString("unitname");
					Mli_name = rs.getString("MEM_BANK_NAME");
					cgpan = (new StringBuilder(String.valueOf(cgpan)))
							.append(rs.getString("cgpan")).append(",<br>")
							.toString();
				}

				session.setAttribute("CGPAN", CGPAN);
				session.setAttribute("unitName", unitName);
				out.print((new StringBuilder(
						"<br>Please check Details of below Claim Ref No.<br>CGPAN:"))
						.append(cgpan).append("  Member Id: ").append(memid)
						.append(" <br> Unit Name\n:  ").append(unitName)
						.append(" <br> MLI Name\n: ").append(Mli_name)
						.toString());
			}
		}
		Log.log(4, "ClaimAction", "showClmInspOption", "Exited");
		if (l_strsetClmRef != null && l_strsetClmRef.length() > 0
				&& l_strsetClmRef.equals("Y")) {
			claimForm.setMliid(memid);
			claimForm.setMliname(Mli_name);
			return mapping.findForward("success");
		} else {
			return null;
		}
	}

	public ActionForward getClaimRef(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Log.log(4, "ClaimAction", "getClaimRef", "Entered");
		PrintWriter out = response.getWriter();
		String clmRef = "";
		String cgpanNo = request.getParameter("cgpanNo");
		Connection connection = DBConnection.getConnection();
		Statement str = connection.createStatement();
		ResultSet rs = null;
		StringBuilder cgQury = new StringBuilder();
		if (cgpanNo != null && cgpanNo.length() > 0) {
			String Qury = (new StringBuilder(
					"select c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id member_id from application_detail c where cgpan='"))
					.append(cgpanNo).append("'").toString();
			ResultSet rsforvalid = str.executeQuery(Qury);
			if (!rsforvalid.next())
				throw new NoMemberFoundException(
						"Entered CGPAN doesn't Exist!!");
			cgQury.append("SELECT CLM_REF_NO FROM claim_detail WHERE bid IN (SELECT BID FROM ssi_detail  WHERE SSI_REFERENCE_NUMBER IN");
			cgQury.append((new StringBuilder(
					"(SELECT SSI_REFERENCE_NUMBER FROM application_detail   WHERE cgpan =UPPER ('"))
					.append(cgpanNo).append("')))").toString());
			cgQury.append("union all");
			cgQury.append(" SELECT CLM_REF_NO FROM claim_detail_temp WHERE bid IN (SELECT BID FROM ssi_detail WHERE SSI_REFERENCE_NUMBER IN");
			cgQury.append((new StringBuilder(
					" (SELECT SSI_REFERENCE_NUMBER FROM application_detail  WHERE cgpan =UPPER ('"))
					.append(cgpanNo).append("')))").toString());
			rs = str.executeQuery(cgQury.toString());
			if (rs.next()) {
				clmRef = rs.getString("CLM_REF_NO");
				out.print((new StringBuilder(
						"Please Find the Claim Ref No of below CGPAN<br>Claim Ref No: "))
						.append(clmRef).toString());
			} else {
				throw new NoMemberFoundException(
						"Claim has not been lodged of Entered CGPAN.");
			}
		}
		Log.log(4, "ClaimAction", "getClaimRef", "Exited");
		return null;
	}

	public ActionForward claiminspectionNewdata(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ClaimAction", "claiminspectionNewdata", "Entered");
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
		Log.log(4, "ClaimAction", "claiminspectionNewdata", "Exited");
		return mapping.findForward("success");
	}

	// rajuk

	public ActionForward showClmInspOption(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "ClaimAction", "getDeclartionDeatils", "Entered");
		// Diksha
		/*
		 * ClaimActionForm claimForm = (ClaimActionForm)form;
		 * claimForm.setInspectPerson(""); claimForm.setInspectRemarks("");
		 * claimForm.setCgpan(""); claimForm.setInspectstatus("");
		 * claimForm.setIsrecproposed(""); claimForm.setInsrecoveryamt("");
		 * claimForm.setAmnt_rec_descion("");
		 */

		Log.log(Log.INFO, "ClaimAction", "showClmInspOption", "Exited");
		return mapping.findForward("success");
	}

	// Diksha end
	
	//rajuk
	/*public ActionForward getClaimReviewReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"Entered");

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getStartDt :" + dynaForm.getStartDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getEndDt :" + dynaForm.getEndDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.setMemberId :" + dynaForm.getMemberId());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getAcctDtlAvlFlag() :" + dynaForm.getAcctDtlAvlFlag());

		// System.out.println("2-getAcctDtl:"+dynaForm.getAcctDtlAvlFlag());

		return mapping.findForward("ClaimSettleForPaymentInputPage");
	}

	
	
	
	// ###Claim Settlement for Payment MAKER
	public ActionForward getClaimReviewReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	
		Date fromDt;
		Date toDt;
		HttpSession session = request.getSession();
		String retVal="";
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());
		} catch (Exception err) {
			session.setAttribute("CurrentPage",
					"ClaimReviewReportInput.do?method=getClaimReviewReport");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");

		}
		String mliId = request.getParameter("memberId");
		//String AcctTypeStat = request.getParameter("status");
		String checkvalue = request.getParameter("checkValue");
		
		System.out.println("checkvalue==="+checkvalue);
		String checktype = request.getParameter("check");
		
		if (  checkvalue==null || checkvalue.equals("") ){
			throw new MessageException("Please select Radio button. ");
		}
		if (  checktype==null || checktype.equals("") ){
			throw new MessageException("Please select MLI WISE OR CGPAN WISE . ");
		}
		
		
		System.out.println("check==="+checktype);
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);

		//session.setAttribute("sqlfromdate", dynaForm.getStartDt());
		//session.setAttribute("sqltodate", dynaForm.getEndDt());
		//session.setAttribute("savedOnceFlag1", "true");
		//session.setAttribute("savedOnceFlag2", "true");

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		if (request.getParameter("startDt") == null
				&& request.getParameter("endDt") == null) {
			throw new MessageException("Please select Date.");
		} else {
			
		}

		if(checkvalue.equals("A") &&  checktype.equals("R") ){
			
			System.out.println("entered======");
		list = getClaimReviewData(connection, sqlfromdate, sqltodate,
				checkvalue, checktype);
		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettledDatalist", list);
		sess.setAttribute("MK", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		retVal="ClaimSettleForPaymentPage1";
		//return mapping.findForward("ClaimSettleForPaymentPage1");
		}
		else if(checkvalue.equals("A") &&  checktype.equals("S") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage2";
			
		}
		else if(checkvalue.equals("B") &&  checktype.equals("R") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage3";
			
		}
		else if(checkvalue.equals("B") &&  checktype.equals("S") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage4";
			
		}
		else if(checkvalue.equals("C") &&  checktype.equals("R") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage5";
			
		}
		else if(checkvalue.equals("C") &&  checktype.equals("S") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage6";
			
		}
		else if(checkvalue.equals("D") &&  checktype.equals("R") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage7";
			
		}
		else if(checkvalue.equals("D") &&  checktype.equals("S") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage8";
			
		}
		
		else if(checkvalue.equals("E") &&  checktype.equals("R") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage8";
			
		}
		else if(checkvalue.equals("E") &&  checktype.equals("S") ){
			list = getClaimReviewData(connection, sqlfromdate, sqltodate,
					checkvalue, checktype);
			HttpSession sess = request.getSession(false);
			// sess.setAttribute("ClaimSettledDatalist", list);
			sess.setAttribute("MK", list);

			dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
			dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		   // return mapping.findForward("ClaimSettleForPaymentPage2");
			retVal="ClaimSettleForPaymentPage8";
			
		}
		
		System.out.println("retVal====="+retVal);
		return mapping.findForward(retVal);
		
	}

	// ### MAKER
	private List getClaimReviewData(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String checkvalue,
			String checktype) throws DatabaseException {
		Log.log(Log.INFO, "ClaimAction", "getClaimSettledData()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		
		 //String mliId="002500010000";
		 //String AcctTypeStatus="Y";   
		//RAJUK
		
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList claimSettledData = new ArrayList();
		
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_ClaimReviewReport(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, checkvalue);
			callableStmt.setString(5, checktype);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "ClaimAction", "getClaimSettledData()",
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
				// System.out.println("list data " + nestData);
				claimSettledData.add(0, coulmName);
				claimSettledData.add(1, nestData);
			}

			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;

		} catch (SQLException sqlexception) {

			Log.log(Log.ERROR, "ClaimAction", "getClaimSettledData()",
					"Error retrieving all Claim settled data!");
			throw new DatabaseException(sqlexception.getMessage());

		} finally {

			DBConnection.freeConnection(conn);
		}
		return claimSettledData;
	}*/
	
	//end rajuk

	// Riyaz Payment Module 11-Dec-2018
	// ###Claim Settlement for Payment cgpanwise input page MAKER
	public ActionForward getClaimSettleForPaymentInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"Entered");

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getStartDt :" + dynaForm.getStartDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getEndDt :" + dynaForm.getEndDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.setMemberId :" + dynaForm.getMemberId());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getAcctDtlAvlFlag() :" + dynaForm.getAcctDtlAvlFlag());

		// System.out.println("2-getAcctDtl:"+dynaForm.getAcctDtlAvlFlag());

		return mapping.findForward("ClaimSettleForPaymentInputPage");
	}

	// ###

	// ###Claim Settlement for Payment mliwise input page MAKER
	public ActionForward getClaimSettleForPaymentPayIdWiseMKInput(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println(" ##getClaimSettleForPaymentPayIdWiseMKInput ## ");
		Log.log(Log.INFO, "ClaimAction",
				"getClaimSettleForPaymentPayIdWiseMKInput", "Entered");
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");

		// System.out.println("2-getAcctDtl:"+dynaForm.getAcctDtl());
		return mapping.findForward("ClaimSettleForPaymentPayIdWiseMKInputPage");
	}

	// ###

	/* mliwise data page MAKER */
	public ActionForward ClaimSettleForPaymentMliWise(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection connection = null;
		User user = getUserInformation(request);
		String userId = user.getUserId();
		// System.out.println("---ClaimSettleForPaymentProcess--Method---");
		HttpSession sess = request.getSession(false);
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		/**/

		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {
			sess.setAttribute(
					"CurrentPage",
					"ClaimSettleForPaymentPayIdWiseMKInput.do?method=getClaimSettleForPaymentPayIdWiseMKInput");
			throw new MessageException("Please Enter Start Date and End Date.");
		}
		Date fromDt;
		Date toDt;
		java.sql.Date sqlfromdate;
		java.sql.Date sqltodate;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());
			sqlfromdate = new java.sql.Date(fromDt.getYear(),
					fromDt.getMonth(), fromDt.getDate());
			sqltodate = new java.sql.Date(toDt.getYear(), toDt.getMonth(),
					toDt.getDate());

			// System.out.println("sqlfromdate :"+sqlfromdate);
			// System.out.println("sqltodate :"+sqltodate);

		} catch (Exception err) {
			sess.setAttribute(
					"CurrentPage",
					"ClaimSettleForPaymentPayIdWiseMKInput.do?method=getClaimSettleForPaymentPayIdWiseMKInput");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");

		}
		String mliId = request.getParameter("memberId");
		String all_selectedcgpan = "";
		/**/

		// %%%

		List list = new ArrayList();

		list = getClaimSettledPaymentProcessData(connection, all_selectedcgpan,
				sqlfromdate, sqltodate, mliId);

		// sess.setAttribute("ClaimSettledDatalist", list);
		sess.setAttribute("MKK", list);
		sess.setAttribute("savedOnceFlag2", "true");
		sess.setAttribute("sqlfromdate", dynaForm.getStartDt());
		sess.setAttribute("sqltodate", dynaForm.getEndDt());

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		return mapping.findForward("ClaimSettleForPaymentProcessPage");

	}

	/**/

	// ###Claim Settlement for Payment MAKER
	public ActionForward getClaimSettleForPayment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPayment", "Entered");
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPayment", "startDt"
				+ request.getParameter("startDt"));
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPayment", "end dt "
				+ request.getParameter("endDt"));
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPayment", "MLI ID"
				+ request.getParameter("memberId"));
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPayment",
				" Accout Details" + request.getParameter("status"));

		/*
		 * System.out.println("request: start dt : "+request.getParameter("startDt"
		 * ));
		 * System.out.println("request: end dt : "+request.getParameter("endDt"
		 * ));
		 * System.out.println("request: MLI ID : "+request.getParameter("memberId"
		 * ));
		 * System.out.println("request: Accout Details : "+request.getParameter
		 * ("status")); /*
		 */

		Date fromDt;
		Date toDt;
		HttpSession session = request.getSession();
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {
			session.setAttribute("CurrentPage",
					"ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput");
			throw new MessageException("Please Enter Start Date and End Date.");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());
		} catch (Exception err) {
			session.setAttribute("CurrentPage",
					"ClaimSettleForPaymentInput.do?method=getClaimSettleForPaymentInput");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");

		}
		String mliId = request.getParameter("memberId");
		String AcctTypeStat = request.getParameter("status");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);

		session.setAttribute("sqlfromdate", dynaForm.getStartDt());
		session.setAttribute("sqltodate", dynaForm.getEndDt());
		session.setAttribute("savedOnceFlag1", "true");
		session.setAttribute("savedOnceFlag2", "true");

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		if (request.getParameter("startDt") == null
				&& request.getParameter("endDt") == null) {
			throw new MessageException("Please select Date.");
		} else {
			list = getClaimSettledData(connection, sqlfromdate, sqltodate,
					mliId, AcctTypeStat);
		}

		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettledDatalist", list);
		sess.setAttribute("MK", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		return mapping.findForward("ClaimSettleForPaymentPage");
	}

	// ### MAKER
	private List getClaimSettledData(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String mliId,
			String AcctTypeStatus) throws DatabaseException {
		Log.log(Log.INFO, "ClaimAction", "getClaimSettledData()", "Entered!");
		CallableStatement callableStmt = null;
		// Connection conn = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList claimSettledData = new ArrayList();
		
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_ClaimSettledDataPayment(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliId);
			callableStmt.setString(5, AcctTypeStatus);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "ClaimAction", "getClaimSettledData()",
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
				// System.out.println("list data " + nestData);
				claimSettledData.add(0, coulmName);
				claimSettledData.add(1, nestData);
			}

			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;

		} catch (SQLException sqlexception) {

			Log.log(Log.ERROR, "ClaimAction", "getClaimSettledData()",
					"Error retrieving all Claim settled data!");
			throw new DatabaseException(sqlexception.getMessage());

		} finally {

			DBConnection.freeConnection(conn);
		}
		return claimSettledData;
	}

	// ##################################

	// ###Claim Settlement for Payment
	public ActionForward ExportToFile(ActionMapping mapping, ActionForm form,
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
					"attachment; filename=ClaimPaymentExcelData" + strDate
							+ ".csv");
			os.write(b);
			os.flush();
		}
		
		if (fileType.equals("EXLType")) {
			byte[] b = generateEXL(ClmDataList, NoColumn, contextPath);
			if (response != null)
				response.setContentType("APPLICATION/OCTET-STREAM");
			    response.setHeader("Content-Disposition","attachment; filename=ClaimPaymentExcelData" + strDate+".xls");
			    os.write(b);
			    os.flush();
		}

		if (fileType.equals("PDFType")) {

			byte[] b = generatePDF(ClmDataList, NoColumn, contextPath);
			if (response != null)

				response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					"attachment; filename=ClaimPaymentExcelData" + strDate
							+ ".pdf");
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
		int rownum = 1;
		for (ArrayList<String> RecordWiseLstObj : RecordWiseLst) {
			int colnum = 0;
			row = sheet.createRow(rownum);			
			for (String SingleRecordDataObj : RecordWiseLstObj) {			
				Cell cell = row.createCell(colnum);
				cell.setCellValue(SingleRecordDataObj);
				colnum++;			
				rowDataLst.add(SingleRecordDataObj);
			}
			rownum++;
		}
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

	public byte[] generatePDF(ArrayList<ArrayList> ParamDataList,
			int No_Column, String contextPath) throws DocumentException,
			IOException {
		// System.out.println("--generatePDF()--");

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String strDate = sdf.format(cal.getTime());
		// Document document = new Document();
		// Document document = new Document(PageSize.A4, 60, 60, 120, 80);
		Document document = new Document(PageSize.A4, 20, 20, 20, 20);
		com.itextpdf.text.Font cellDatafont = FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 6);
		com.itextpdf.text.Font cellHeaderfont = FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 7, Font.BOLD);
		// PdfWriter.getInstance(document, new
		// FileOutputStream("D:\\GenerateFiles\\SampleFile" + strDate +
		// ".pdf"));
		PdfWriter.getInstance(document, new FileOutputStream(contextPath
				+ "\\Download\\DataCSVFile" + strDate + ".pdf"));

		document.open();
		// ##
		// @@ArrayList<ArrayList> RecordWiseLst = (ArrayList)
		// ParamDataList.get(0);
		// @@ArrayList<String> rowDataLst = new ArrayList<String>();
		ArrayList<String> rowDataLst = new ArrayList<String>();
		ArrayList<String> HeaderLst = (ArrayList) ParamDataList.get(0);
		ArrayList<ArrayList> RecordWiseLst = (ArrayList) ParamDataList.get(1);

		// #### Header List
		for (String headerdata : HeaderLst) {
			rowDataLst.add(headerdata);
		}
		// #### Header List

		for (ArrayList<String> RecordWiseLstObj : RecordWiseLst) {

			for (String SingleRecordDataObj : RecordWiseLstObj) {
				// System.out.println("DataLstInnerObj :"+SingleRecordDataObj);
				if (SingleRecordDataObj == null) {
					rowDataLst.add(" ");
				} else {
					rowDataLst.add(SingleRecordDataObj);
				}
			}
			// System.out.println("DataLstObj :"+DataLstObj);
		}

		// ##
		// System.out.println("rowDataLst:" + rowDataLst);
		PdfPTable table = new PdfPTable(No_Column);

		float colwith[] = new float[No_Column];
		for (int n = 0; n < No_Column; n++) {
			colwith[n] = 30f;
		}

		table.setTotalWidth(colwith);
		table.setWidthPercentage(100);
		// System.out.println(" Row-Data : "+rowDataLst.get(m));

		for (int m = 0; m < rowDataLst.size(); m++) {
			// System.out.println(" Row-Data : "+rowDataLst.get(m));
			// table.addCell(rowDataLst.get(m).toString());
			if (m < No_Column) {
				table.addCell(new PdfPCell(new Phrase(rowDataLst.get(m)
						.toString(), cellHeaderfont)));
			} else {
				table.addCell(new PdfPCell(new Phrase(rowDataLst.get(m)
						.toString(), cellDatafont)));
			}
			// table.addCell(new PdfPCell(new
			// Phrase(rowDataLst.get(m).toString()));
		}
		document.add(table);
		document.close();

		// ##
		// FileInputStream fis = new
		// FileInputStream("D:\\GenerateFiles\\SampleFile" + strDate + ".pdf");
		FileInputStream fis = new FileInputStream(contextPath
				+ "\\Download\\DataCSVFile" + strDate + ".pdf");

		byte b[];
		int x;

		x = fis.available();
		b = new byte[x];
		// System.out.println(" b size"+b.length);
		fis.read(b);
		// ##
		return b;
	}

	// *****MAKER
	public ActionForward ClaimSettleForPaymentProcess(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection connection = null;

		User user = getUserInformation(request);
		String userId = user.getUserId();

		// System.out.println("---ClaimSettleForPaymentProcess--Method---");

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
		// System.out.println("checkbox value:"+dynaForm.getCheckboxfield());
		Map opSelectedmap = dynaForm.getCheckboxfield();

		// %%%

		HttpSession session = request.getSession(true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt = sdf.parse(session.getAttribute("sqlfromdate").toString());
		Date toDt = sdf.parse(session.getAttribute("sqltodate").toString());
		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);
		String mliID = "";
		// %%%
		// ##

		// System.out.println("savedOnceFlag1::"+session.getAttribute("savedOnceFlag1"));

		if (null != session.getAttribute("savedOnceFlag1")) {
			saveClaimSettledPaymentProcessData(opSelectedmap, userId);
			session.removeAttribute("savedOnceFlag1");
		}
		// / }
		// Saving cgpan wise data in a table -end
		// ##
		// /String all_selectedcgpan = keys.toString();

		String all_selectedcgpan = opSelectedmap.keySet().toString();
		all_selectedcgpan = all_selectedcgpan.substring(1,
				all_selectedcgpan.length() - 1).replace(" ", "");

		List list = new ArrayList();
		list = getClaimSettledPaymentProcessData(connection, all_selectedcgpan,
				sqlfromdate, sqltodate, mliID);

		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettledDatalist", list);
		sess.setAttribute("MKK", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
		return mapping.findForward("ClaimSettleForPaymentProcessPage");

	}

	// private int saveClaimSettledPaymentProcessData(Connection conn,String
	// cgpanPayment)
	// throws DatabaseException, ParseException, SQLException {
	private int saveClaimSettledPaymentProcessData(Map opSelectedmap,
			String userId) throws DatabaseException, ParseException,
			SQLException {

		Log.log(Log.INFO, "CPDAO", "saveClaimSettledPaymentProcessData()",
				"Entered!");
		Connection conn = null;
		conn = DBConnection.getConnection();

		CallableStatement callableStmt1 = null;
		CallableStatement callableStmt2 = null;
		int saveFlag = 0;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		// #################################################################################################

		ArrayList<Set> cgpan200SetsArrList = new ArrayList<Set>();
		Set<String> keys = opSelectedmap.keySet();

		// System.out.println("Key Size:"+keys.size());
		Set<String> cgpanSet = new HashSet<String>();
		ArrayList<Integer> IntValueList = new ArrayList<Integer>();
		for (int n = 151; n < keys.size(); n = n + 150) {
			IntValueList.add(n);
		}
		// System.out.println(" IntValueList : "+IntValueList);

		int count = 1;
		for (String item : keys) {
			// System.out.println("count:"+count);
			if (IntValueList.contains(count)) {
				Set cgSetTemp = new HashSet();
				cgSetTemp.addAll(cgpanSet);
				// System.out.println("####cgpanSet Size:"+cgpanSet.size());
				cgpan200SetsArrList.add(cgSetTemp);
				cgpanSet.clear();
				cgpanSet.add(item);

			} else {
				cgpanSet.add(item);
			}
			++count;
		}

		// System.out.println("@@count:"+count);
		// System.out.println("@@cgpanSet:"+cgpanSet);

		if (!cgpanSet.isEmpty()) {
			cgpan200SetsArrList.add(cgpanSet);
		}

		// System.out.println("cgpan200Sets :"+cgpan200SetsArrList);
		// System.out.println("cgpan200Sets Size:"+cgpan200SetsArrList.size());

		try {

			for (Set cgpanSetTemp : cgpan200SetsArrList) {

				// System.out.println("Total CGPAN SELECTED :"+keys.size());
				// System.out.println("##cgpanSetTemp Size:"+cgpanSetTemp.size());
				String cgpan = cgpanSetTemp.toString();
				// HttpSession sess = request.getSession(false);
				// dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
				cgpan = cgpan.substring(1, cgpan.length() - 1).replace(" ", "");
				// cgpan = cgpan.replace("[","").replace("]",
				// "").replace(" ","");
				// System.out.println("after substr cgpan:"+cgpan);
				// ##
				// / }
				// #################################################################################################

				callableStmt1 = conn
						.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_allocation(?,?,?)}");
				callableStmt1.setString(1, cgpan);
				callableStmt1.setString(2, userId);
				callableStmt1.registerOutParameter(3, java.sql.Types.VARCHAR);
				callableStmt1.execute();
				errorCode = callableStmt1.getString(3);
				// System.out.println("errorCode :"+errorCode);

				if (null != errorCode) {
					// Log.log(Log.ERROR, "ClaimAction",
					// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
					// + errorCode);
					callableStmt1.close();

					throw new DatabaseException(errorCode);

				} else {

					saveFlag = 0;
				}
				callableStmt1.close();
				callableStmt1 = null;
			}
			// /###
			// Payid Generation and update in table test_para_cgpan
			callableStmt2 = conn
					.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_pay_id_init(?,?)}");
			callableStmt2.setString(1, userId);
			callableStmt2.registerOutParameter(2, java.sql.Types.VARCHAR);
			callableStmt2.execute();
			errorCode = callableStmt2.getString(2);
			// System.out.println("errorCode :"+errorCode);

			if (null != errorCode) {
				// Log.log(Log.ERROR, "ClaimAction",
				// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
				// + errorCode);
				callableStmt1.close();
				throw new DatabaseException(errorCode);

			} else {

				saveFlag = 0;
			}
			callableStmt2.close();
			callableStmt2 = null;/**/
			// ###

		} catch (Exception ex) {

			conn.rollback();
			ex.printStackTrace();
			// System.out.println("Error:"+ex);
			DBConnection.freeConnection(conn);
			// Log.log(Log.ERROR, "ClaimAction",
			// "saveClaimSettledPaymentProcessData()","Error retrieving all Sacing Claim settled Payment Process Data!");
			throw new DatabaseException(ex.getMessage());
		} finally {
			conn.commit();
			DBConnection.freeConnection(conn);
		}
		return saveFlag;
	}

	// ##################################

	// ###MAKER
	private List getClaimSettledPaymentProcessData(Connection conn,
			String cgpanPayment, java.sql.Date sqlfromdate,
			java.sql.Date sqltodate, String mliID) throws DatabaseException,
			ParseException {
		Log.log(Log.INFO, "CPDAO", "getClaimSettledPaymentProcessData()",
				"Entered!");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList claimSettledPaymentProcessData = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);

		try {
			// ##
			/*
			 * SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy"); Date
			 * fromDt = sdf.parse("01/08/2018"); Date toDt =
			 * sdf.parse("15/08/2018"); java.sql.Date sqlfromdate = new
			 * java.sql.
			 * Date(fromDt.getYear(),fromDt.getMonth(),fromDt.getDate());
			 * java.sql.Date sqltodate = new
			 * java.sql.Date(toDt.getYear(),toDt.getMonth(),toDt.getDate());/*
			 */

			// System.out.println("sqlfromdate :"+sqlfromdate);
			// System.out.println("sqltodate :"+sqltodate);
			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_getMLIWisePayIDData(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliID);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"getClaimSettledPaymentProcessData()",
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
				// System.out.println("list data " + nestData);
				claimSettledPaymentProcessData.add(0, coulmName);
				claimSettledPaymentProcessData.add(1, nestData);
			}

			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;

		} catch (SQLException sqlexception) {

			DBConnection.freeConnection(conn);
			Log.log(Log.ERROR, "ClaimAction",
					"getClaimSettledPaymentProcessData()",
					"Error retrieving all Claim settled Payment Process Data!");
			throw new DatabaseException(sqlexception.getMessage());

		} finally {
			DBConnection.freeConnection(conn);
		}
		return claimSettledPaymentProcessData;
	}

	// MAKER
	public ActionForward ClaimSettleForPaymentInitiation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("---ClaimSettleForPaymentInitiation--Method---");
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Connection connection = null;
		// System.out.println("---ClaimSettleForPaymentInitiation--Method---");
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
		// System.out.println("checkbox value:"+dynaForm.getCheckboxfield());
		Map opSelectedmap = dynaForm.getCheckboxfield();
		String actionType = request.getParameter("actionType");
		// %%%

		HttpSession session = request.getSession(true);
		/*********************
		 * SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy"); Date fromDt
		 * = sdf.parse(session.getAttribute("sqlfromdate").toString()); Date
		 * toDt = sdf.parse(session.getAttribute("sqltodate").toString());
		 * String mliId = request.getParameter("memberId");
		 * 
		 * 
		 * java.sql.Date sqlfromdate = new
		 * java.sql.Date(fromDt.getYear(),fromDt.getMonth(),fromDt.getDate());
		 * java.sql.Date sqltodate = new
		 * java.sql.Date(toDt.getYear(),toDt.getMonth(),toDt.getDate());
		 * 
		 * System.out.println("sqlfromdate :"+sqlfromdate);
		 * System.out.println("sqltodate :"+sqltodate); String mliID="";/
		 ***********************/
		// %%%
		// ##

		// System.out.println("savedOnceFlag2::"+session.getAttribute("savedOnceFlag2"));

		if (null != session.getAttribute("savedOnceFlag2")) {
			saveClaimSettledPaymentInitiateData(opSelectedmap, actionType,
					userId);
			session.removeAttribute("savedOnceFlag2");
		}
		// / }
		// Saving cgpan wise data in a table -end
		// ##
		// /String all_selectedcgpan = keys.toString();

		String all_selectedmliId = opSelectedmap.keySet().toString();
		all_selectedmliId = all_selectedmliId.substring(1,
				all_selectedmliId.length() - 1).replace(" ", "");

		/*
		 * List list = new ArrayList(); list =
		 * getClaimSettledPaymentProcessData(
		 * connection,all_selectedmliId,sqlfromdate,sqltodate,mliID);
		 * 
		 * HttpSession sess = request.getSession(false);
		 * sess.setAttribute("ClaimSettledDatalist", list);
		 * 
		 * dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		 * dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));/*
		 */

		HttpSession sess = request.getSession(false);
		sess.setAttribute("actionType", actionType);

		return mapping.findForward("ClaimSettleForPaymentInitiationConfirm");
		// return null;
	}

	// MAKER
	private int saveClaimSettledPaymentInitiateData(Map opSelectedmap,
			String ActionType, String userId) throws DatabaseException,
			ParseException, SQLException {

		Log.log(Log.INFO, "ClaimAction",
				"saveClaimSettledPaymentInitiateData()", "Entered!");
		Connection conn = null;
		conn = DBConnection.getConnection();

		CallableStatement callableStmt1 = null;
		CallableStatement callableStmt2 = null;
		int saveFlag = 0;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		// #################################################################################################

		// ArrayList<Set> cgpan200SetsArrList = new ArrayList<Set>();
		ArrayList<Set> payId200SetsArrList = new ArrayList<Set>();

		Set<String> keys = opSelectedmap.keySet();

		// System.out.println("Key Size:"+keys.size());
		Set<String> payIdSet = new HashSet<String>();
		ArrayList<Integer> IntValueList = new ArrayList<Integer>();
		for (int n = 151; n < keys.size(); n = n + 150) {
			IntValueList.add(n);
		}
		// System.out.println(" IntValueList : " + IntValueList);

		int count = 1;
		for (String item : keys) {
			// System.out.println("count:"+count);
			if (IntValueList.contains(count)) {
				Set payIdSetTemp = new HashSet();
				payIdSetTemp.addAll(payIdSet);
				// System.out.println("####cgpanSet Size:"+cgpanSet.size());
				// cgpan200SetsArrList.add(cgSetTemp);
				payId200SetsArrList.add(payIdSetTemp);

				payIdSet.clear();
				payIdSet.add(item);

			} else {
				payIdSet.add(item);
			}
			++count;
		}

		// System.out.println("@@count:" + count);
		// System.out.println("@@payIdSet:" + payIdSet);

		if (!payIdSet.isEmpty()) {
			payId200SetsArrList.add(payIdSet);
		}

		// System.out.println("cgpan200Sets :"+cgpan200SetsArrList);
		// System.out.println("cgpan200Sets Size:"+cgpan200SetsArrList.size());

		try {

			for (Set payIdSetTemp : payId200SetsArrList) {

				// System.out.println("Total Mli ID SELECTED :" + keys.size());
				// System.out.println("##payIdSetTemp Size:" +
				// payIdSetTemp.size());
				String payId = payIdSetTemp.toString();
				// HttpSession sess = request.getSession(false);
				// dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
				payId = payId.substring(1, payId.length() - 1).replace(" ", "");
				// cgpan = cgpan.replace("[","").replace("]",
				// "").replace(" ","");
				// System.out.println("after substr cgpan:"+cgpan);
				// ##
				// / }
				// #################################################################################################

				// System.out.println("payid:="+payId);

				callableStmt1 = conn
						.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_initiation(?,?,?,?)}");
				callableStmt1.setString(1, payId);
				callableStmt1.setString(2, ActionType);
				callableStmt1.setString(3, userId);
				callableStmt1.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStmt1.execute();
				errorCode = callableStmt1.getString(4);
				// System.out.println("errorCode :" + errorCode);

				if (null != errorCode) {
					// Log.log(Log.ERROR, "ClaimAction",
					// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
					// + errorCode);
					callableStmt1.close();

					throw new DatabaseException(errorCode);

				} else {

					// throw new
					// DatabaseException(" !! Claim Payment Succefully Initiated and sent for Approval to Reviewer Officer !! ");

					saveFlag = 0;
				}

			}

		} catch (Exception ex) {

			conn.rollback();
			ex.printStackTrace();
			// System.out.println("Error:" + ex);
			DBConnection.freeConnection(conn);
			// Log.log(Log.ERROR, "ClaimAction",
			// "saveClaimSettledPaymentProcessData()","Error retrieving all Sacing Claim settled Payment Process Data!");
			throw new DatabaseException(ex.getMessage());
		} finally {
			conn.commit();
			DBConnection.freeConnection(conn);
			callableStmt1.close();
			callableStmt1 = null;
			/*****************/
		}
		return saveFlag;
	}

	// ##################################
	// ###Claim Settlement for Payment MLIWise Total Screen reviewer
	public ActionForward getClaimSettlePaymentSavedMLIWiseCK1Input(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");

		// System.out.println("2");
		return mapping
				.findForward("ClaimSettlePaymentSavedMLIWiseCK1InputPage");

	}

	// ###
	
	// ###Claim Settlement for Payment reviewer
	public ActionForward getClaimSettlePaymentSavedMLIWiseCK1(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		 //System.out.println("request: start dt"+request.getParameter("startDt"));
		 //System.out.println("request: end dt"+request.getParameter("endDt"));
		// System.out.println("request: MLI ID"+request.getParameter("memberId"));

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		HttpSession session = request.getSession();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt;
		Date toDt;

		// ##
		/**********************************************
		if (null != session.getAttribute("sqlfromdate")) {
			dynaForm.setStartDt(session.getAttribute("sqlfromdate").toString());
			dynaForm.setEndDt(session.getAttribute("sqltodate").toString());
		}/*****************/
		// ##

		// ##
		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input");
			throw new MessageException("Please Enter Start Date and End Date.");
		}

		try {

			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());

		} catch (Exception err) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");

		}

		// ##

		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);

		session.setAttribute("sqlfromdate", dynaForm.getStartDt());
		session.setAttribute("sqltodate", dynaForm.getEndDt());
		session.setAttribute("savedOnceFlag1", "true");

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		if ((request.getParameter("startDt") == null && request
				.getParameter("endDt") == null)
				&& (session.getAttribute("sqlfromdate") == null && session
						.getAttribute("sqltodate") == null)) {
			throw new MessageException("Please select Date.");
		} else {
			 System.out.println("@@@@@@@@@@sqlfromdate :"+sqlfromdate);
			 System.out.println("@@@@@@@@@@sqltodate :"+sqltodate);
			list = getClaimSettlePaymentSavedMLIWiseCK1Data(connection,
					sqlfromdate, sqltodate, mliId);
		}

		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettlePaymentSavedMLIWiseCK1List", list);
		sess.setAttribute("CK1", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("ClaimSettlePaymentSavedMLIWiseCK1");
	}

	// reviewer
	private List getClaimSettlePaymentSavedMLIWiseCK1Data(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String mliID)
			throws DatabaseException, ParseException {
		Log.log(Log.INFO, "CPDAO", "getClaimSettledPaymentProcessData()",
				"Entered!");

		// System.out.println("getClaimSettlePaymentSavedMLIWiseCK1Data Entered");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList ClaimSettlePaymentSavedMLIWiseCK1Data = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		try {
			// System.out.println("sqlfromdate :" + sqlfromdate);
			// System.out.println("sqltodate :" + sqltodate);
			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_getMLIWisePayIDForCK1App(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliID);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"getClaimSettlePaymentSavedMLIWiseCK1Data()",
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
				// System.out.println("list data " + nestData);
				ClaimSettlePaymentSavedMLIWiseCK1Data.add(0, coulmName);
				ClaimSettlePaymentSavedMLIWiseCK1Data.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "ClaimAction",
					"getClaimSettlePaymentSavedMLIWiseCK1Data()",
					"Error retrieving all Claim settled Payment Process Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return ClaimSettlePaymentSavedMLIWiseCK1Data;
	}

	// reviewer
	public ActionForward ClaimSettlePaymentSavedMLIWiseCK1Approval(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("---ClaimSettlePaymentSavedMLIWiseCK1Approval--Method---");
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Connection connection = null;
		// System.out.println("---ClaimSettlePaymentSavedMLIWiseCK1Approval--Method---");
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
		// System.out.println("checkbox value:"+dynaForm.getCheckboxfield());
		Map opSelectedmap = dynaForm.getCheckboxfield();
		String actionType = request.getParameter("actionType");
		// %%%

		HttpSession session = request.getSession(true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		
		Date fromDt = sdf.parse(session.getAttribute("sqlfromdate").toString());
		Date toDt = sdf.parse(session.getAttribute("sqltodate").toString());
		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);
		String mliID = "";
		// %%%
		// ##

		// System.out.println("savedOnceFlag2::"+session.getAttribute("savedOnceFlag2"));

		if (null != session.getAttribute("savedOnceFlag1")) {
			// System.out.println("Saving for Rev Approval");
			saveClaimSettlePaymentSavedMLIWiseCK1DataApproval(opSelectedmap,
					actionType, userId);
			session.removeAttribute("savedOnceFlag1");
		}
		// / }
		// Saving cgpan wise data in a table -end
		// ##
		// /String all_selectedcgpan = keys.toString();
		// System.out.println("opSelectedmap :"+opSelectedmap);
		String all_selectedmliId = opSelectedmap.keySet().toString();
		all_selectedmliId = all_selectedmliId.substring(1,
				all_selectedmliId.length() - 1).replace(" ", "");

		/*
		 * List list = new ArrayList(); list =
		 * getClaimSettledPaymentProcessData(
		 * connection,all_selectedmliId,sqlfromdate,sqltodate,mliID);
		 * 
		 * HttpSession sess = request.getSession(false);
		 * sess.setAttribute("ClaimSettledDatalist", list);
		 * 
		 * dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		 * dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));/*
		 */
		HttpSession sess = request.getSession(false);
		sess.setAttribute("actionType", actionType);

		return mapping
				.findForward("ClaimSettlePaymentSavedMLIWiseCK1ApprovalConfirm");
		// return null;
	}

	// reviewer
	private int saveClaimSettlePaymentSavedMLIWiseCK1DataApproval(
			Map opSelectedmap, String ActionType, String userId)
			throws DatabaseException, ParseException, SQLException {

		Log.log(Log.INFO, "ClaimAction",
				"saveClaimSettledPaymentInitiateData()", "Entered!");
		Connection conn = null;
		conn = DBConnection.getConnection();

		CallableStatement callableStmt1 = null;
		CallableStatement callableStmt2 = null;
		int saveFlag = 0;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		// #################################################################################################

		// ArrayList<Set> cgpan200SetsArrList = new ArrayList<Set>();
		ArrayList<Set> payId200SetsArrList = new ArrayList<Set>();

		Set<String> keys = opSelectedmap.keySet();

		// System.out.println("Key Size:"+keys.size());
		Set<String> payIdSet = new HashSet<String>();
		ArrayList<Integer> IntValueList = new ArrayList<Integer>();
		for (int n = 151; n < keys.size(); n = n + 150) {
			IntValueList.add(n);
		}
		System.out.println(" IntValueList : " + IntValueList);

		int count = 1;
		for (String item : keys) {
			// System.out.println("count:"+count);
			if (IntValueList.contains(count)) {
				Set payIdSetTemp = new HashSet();
				payIdSetTemp.addAll(payIdSet);
				// System.out.println("####cgpanSet Size:"+cgpanSet.size());
				// cgpan200SetsArrList.add(cgSetTemp);
				payId200SetsArrList.add(payIdSetTemp);

				payIdSet.clear();
				payIdSet.add(item);

			} else {
				payIdSet.add(item);
			}
			++count;
		}

		// System.out.println("@@count:" + count);
		// System.out.println("@@payIdSet:" + payIdSet);

		if (!payIdSet.isEmpty()) {
			payId200SetsArrList.add(payIdSet);
		}

		// System.out.println("cgpan200Sets :"+cgpan200SetsArrList);
		// System.out.println("cgpan200Sets Size:"+cgpan200SetsArrList.size());

		try {

			for (Set payIdSetTemp : payId200SetsArrList) {

				// System.out.println("Total Pay ID SELECTED :" + keys.size());
				// System.out.println("##payIdSetTemp Size:" +
				// payIdSetTemp.size());
				String payId = payIdSetTemp.toString();
				// HttpSession sess = request.getSession(false);
				// dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
				payId = payId.substring(1, payId.length() - 1).replace(" ", "");
				// cgpan = cgpan.replace("[","").replace("]",
				// "").replace(" ","");
				// System.out.println("after substr cgpan:"+cgpan);
				// ##
				// / }
				// #################################################################################################

				// System.out.println("payid:="+payId);

				callableStmt1 = conn
						.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_ck1_app(?,?,?,?)}");
				callableStmt1.setString(1, payId);
				callableStmt1.setString(2, ActionType);
				callableStmt1.setString(3, userId);
				callableStmt1.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStmt1.execute();
				errorCode = callableStmt1.getString(4);
				// System.out.println("errorCode :" + errorCode);

				if (null != errorCode) {
					// Log.log(Log.ERROR, "ClaimAction",
					// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
					// + errorCode);
					callableStmt1.close();

					throw new DatabaseException(errorCode);

				} else {

					// throw new
					// DatabaseException(" !! Claim Payment Succefully Initiated and sent for Approval to Reviewer Officer !! ");

					saveFlag = 0;
				}

			}

		} catch (Exception ex) {

			conn.rollback();
			ex.printStackTrace();
			// System.out.println("Error:" + ex);
			DBConnection.freeConnection(conn);
			// Log.log(Log.ERROR, "ClaimAction",
			// "saveClaimSettledPaymentProcessData()","Error retrieving all Sacing Claim settled Payment Process Data!");
			throw new DatabaseException(ex.getMessage());
		} finally {
			conn.commit();
			DBConnection.freeConnection(conn);
			callableStmt1.close();
			callableStmt1 = null;
			/*****************/
		}
		return saveFlag;
	}

	// ##################################
	// ###Claim Settlement for Payment MLIWise Total Screen checker
	public ActionForward getClaimSettlePaymentSavedMLIWiseCK2Input(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");
		// System.out.println("2");
		return mapping
				.findForward("ClaimSettlePaymentSavedMLIWiseCK2InputPage");

	}

	// ###

	// ###Claim Settlement for Payment checker
	public ActionForward getClaimSettlePaymentSavedMLIWiseCK2(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("request: start dt"+request.getParameter("startDt"));
		// System.out.println("request: end dt"+request.getParameter("endDt"));
		// System.out.println("request: MLI ID"+request.getParameter("memberId"));
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		HttpSession session = request.getSession();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt;
		Date toDt;

		// ##
		/***************************************************
		if (null != session.getAttribute("sqlfromdate")) {
			dynaForm.setStartDt(session.getAttribute("sqlfromdate").toString());
			dynaForm.setEndDt(session.getAttribute("sqltodate").toString());
		}
		/***************************************************/
		// ##

		// ##
		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK2Input.do?method=getClaimSettlePaymentSavedMLIWiseCK2Input");
			throw new MessageException("Please Enter Start Date and End Date.");
		}

		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());
		} catch (Exception err) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK2Input.do?method=getClaimSettlePaymentSavedMLIWiseCK2Input");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");
		}

		// ##

		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);

		session.setAttribute("sqlfromdate", dynaForm.getStartDt());
		session.setAttribute("sqltodate", dynaForm.getEndDt());
		session.setAttribute("savedOnceFlag1", "true");
		session.setAttribute("savedOnceFlag2", "true");

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		if ((request.getParameter("startDt") == null && request
				.getParameter("endDt") == null)
				&& (session.getAttribute("sqlfromdate") == null && session
						.getAttribute("sqltodate") == null)) {
			throw new MessageException("Please select Date.");
		} else {
			list = getClaimSettlePaymentSavedMLIWiseCK2Data(connection,
					sqlfromdate, sqltodate, mliId);
		}

		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettlePaymentSavedMLIWiseCK2List", list);
		sess.setAttribute("CK2", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("ClaimSettlePaymentSavedMLIWiseCK2");
	}

	// checker
	private List getClaimSettlePaymentSavedMLIWiseCK2Data(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String mliID)
			throws DatabaseException, ParseException {
		Log.log(Log.INFO, "CPDAO",
				"getClaimSettlePaymentSavedMLIWiseCK2Data()", "Entered!");

		// System.out.println("getClaimSettlePaymentSavedMLIWiseCK2Data Entered");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList ClaimSettlePaymentSavedMLIWiseCK2Data = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		try {
			// System.out.println("sqlfromdate :" + sqlfromdate);
			// System.out.println("sqltodate :" + sqltodate);
			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_getMLIWisePayIDForCK2App(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliID);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"getClaimSettlePaymentSavedMLIWiseCK2Data()",
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
				// System.out.println("list data " + nestData);
				ClaimSettlePaymentSavedMLIWiseCK2Data.add(0, coulmName);
				ClaimSettlePaymentSavedMLIWiseCK2Data.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "ClaimAction",
					"getClaimSettlePaymentSavedMLIWiseCK2Data()",
					"Error retrieving all Claim settled Payment Process Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return ClaimSettlePaymentSavedMLIWiseCK2Data;
	}

	// checker
	public ActionForward ClaimSettlePaymentSavedMLIWiseCK2Approval(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("---ClaimSettlePaymentSavedMLIWiseCK2Approval--Method---");
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Connection connection = null;
		// System.out.println("---ClaimSettlePaymentSavedMLIWiseCK2Approval--Method---");
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
		// System.out.println("checkbox value:"+dynaForm.getCheckboxfield());
		Map opSelectedmap = dynaForm.getCheckboxfield();
		String actionType = request.getParameter("actionType");
		// System.out.println("actionType: "+actionType);
		// %%%

		HttpSession session = request.getSession(true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt = sdf.parse(session.getAttribute("sqlfromdate").toString());
		Date toDt = sdf.parse(session.getAttribute("sqltodate").toString());
		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);
		String mliID = "";
		// %%%
		// ##

		// System.out.println("savedOnceFlag2::"+session.getAttribute("savedOnceFlag2"));

		if (null != session.getAttribute("savedOnceFlag1")) {
			// System.out.println("Saving for Rev Approval");
			saveClaimSettlePaymentSavedMLIWiseCK2DataApproval(opSelectedmap,
					actionType, userId);
			session.removeAttribute("savedOnceFlag1");
		}
		// / }
		// Saving cgpan wise data in a table -end
		// ##
		// /String all_selectedcgpan = keys.toString();
		// System.out.println("opSelectedmap :"+opSelectedmap);
		String all_selectedmliId = opSelectedmap.keySet().toString();
		all_selectedmliId = all_selectedmliId.substring(1,
				all_selectedmliId.length() - 1).replace(" ", "");

		/*
		 * List list = new ArrayList(); list =
		 * getClaimSettledPaymentProcessData(
		 * connection,all_selectedmliId,sqlfromdate,sqltodate,mliID);
		 * 
		 * HttpSession sess = request.getSession(false);
		 * sess.setAttribute("ClaimSettledDatalist", list);
		 * 
		 * dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		 * dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));/*
		 */
		HttpSession sess = request.getSession(false);
		sess.setAttribute("actionType", actionType);

		return mapping
				.findForward("ClaimSettlePaymentSavedMLIWiseCK2ApprovalConfirm");
		// return null;
	}

	// checker
	private int saveClaimSettlePaymentSavedMLIWiseCK2DataApproval(
			Map opSelectedmap, String ActionType, String userId)
			throws DatabaseException, ParseException, SQLException {

		Log.log(Log.INFO, "ClaimAction",
				"saveClaimSettlePaymentSavedMLIWiseCK2DataApproval()",
				"Entered!");
		Connection conn = null;
		conn = DBConnection.getConnection();

		CallableStatement callableStmt1 = null;
		CallableStatement callableStmt2 = null;
		int saveFlag = 0;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		// #################################################################################################

		// ArrayList<Set> cgpan200SetsArrList = new ArrayList<Set>();
		ArrayList<Set> payId200SetsArrList = new ArrayList<Set>();

		Set<String> keys = opSelectedmap.keySet();

		// System.out.println("Key Size:"+keys.size());
		Set<String> payIdSet = new HashSet<String>();
		ArrayList<Integer> IntValueList = new ArrayList<Integer>();
		for (int n = 151; n < keys.size(); n = n + 150) {
			IntValueList.add(n);
		}
		// System.out.println(" IntValueList : " + IntValueList);

		int count = 1;
		for (String item : keys) {
			// System.out.println("count:"+count);
			if (IntValueList.contains(count)) {
				Set payIdSetTemp = new HashSet();
				payIdSetTemp.addAll(payIdSet);
				// System.out.println("####cgpanSet Size:"+cgpanSet.size());
				// cgpan200SetsArrList.add(cgSetTemp);
				payId200SetsArrList.add(payIdSetTemp);

				payIdSet.clear();
				payIdSet.add(item);

			} else {
				payIdSet.add(item);
			}
			++count;
		}

		// /System.out.println("@@count:" + count);
		// System.out.println("@@payIdSet:" + payIdSet);

		if (!payIdSet.isEmpty()) {
			payId200SetsArrList.add(payIdSet);
		}

		// System.out.println("cgpan200Sets :"+cgpan200SetsArrList);
		// System.out.println("cgpan200Sets Size:"+cgpan200SetsArrList.size());

		try {

			for (Set payIdSetTemp : payId200SetsArrList) {

				// System.out.println("Total Pay ID SELECTED :" + keys.size());
				// System.out.println("##payIdSetTemp Size:" +
				// payIdSetTemp.size());
				String payId = payIdSetTemp.toString();
				// HttpSession sess = request.getSession(false);
				// dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
				payId = payId.substring(1, payId.length() - 1).replace(" ", "");
				// cgpan = cgpan.replace("[","").replace("]",
				// "").replace(" ","");
				// System.out.println("after substr cgpan:"+cgpan);
				// ##
				// / }
				// #################################################################################################

				// System.out.println("payid:="+payId);

				callableStmt1 = conn
						.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_ck2_app(?,?,?,?)}");
				callableStmt1.setString(1, payId);
				callableStmt1.setString(2, ActionType);
				callableStmt1.setString(3, userId);
				callableStmt1.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStmt1.execute();
				errorCode = callableStmt1.getString(4);
				// System.out.println("errorCode :" + errorCode);

				if (null != errorCode) {
					// Log.log(Log.ERROR, "ClaimAction",
					// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
					// + errorCode);
					callableStmt1.close();

					throw new DatabaseException(errorCode);

				} else {

					// throw new
					// DatabaseException(" !! Claim Payment Succefully Initiated and sent for Approval to Reviewer Officer !! ");

					saveFlag = 0;
				}

			}

		} catch (Exception ex) {

			conn.rollback();
			ex.printStackTrace();
			// System.out.println("Error:" + ex);
			DBConnection.freeConnection(conn);
			// Log.log(Log.ERROR, "ClaimAction",
			// "saveClaimSettledPaymentProcessData()","Error retrieving all Sacing Claim settled Payment Process Data!");
			throw new DatabaseException(ex.getMessage());
		} finally {
			conn.commit();
			DBConnection.freeConnection(conn);
			callableStmt1.close();
			callableStmt1 = null;
			/*****************/
		}
		return saveFlag;
	}

	// ###Claim Settlement for Payment checker
	public ActionForward ShowPaymentAllocatedMLIWiseData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		String PayId = request.getParameter("PayId");
		String approvalStatus = request.getParameter("approvalStatus");
		// System.out.println("approvalStatus : "+approvalStatus);

		// System.out.println("PayId:"+PayId);
		HttpSession session = request.getSession();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		if (request.getParameter("PayId") == null) {
			throw new MessageException("Please enter PayId Data.");
		} else {
			list = getPaymentAllocatedMLIWiseData(connection, PayId,
					approvalStatus);
		}

		HttpSession sess = request.getSession(false);
		sess.setAttribute("ClaimSettlePaymentSavedMLIWiseCK1List", list);
		sess.setAttribute("approvalStatus", approvalStatus);
		sess.setAttribute("ClickedPAYID_data", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("ShowPaymentAllocatedMLIWiseData");
	}

	// checker
	private List getPaymentAllocatedMLIWiseData(Connection conn, String PayId,
			String ApprovalStatus) throws DatabaseException, ParseException {
		Log.log(Log.INFO, "CPDAO", "getPaymentAllocatedMLIWiseData()",
				"Entered!");

		// System.out.println("getPaymentAllocatedMLIWiseData Entered");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList PaymentSavedMLIWiseData = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		try {

			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_PaymentAllocMLIWiseData(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, PayId);
			callableStmt.setString(3, ApprovalStatus);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"getPaymentAllocatedMLIWiseData()",
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
				PaymentSavedMLIWiseData.add(0, coulmName);
				PaymentSavedMLIWiseData.add(1, nestData);
			}

			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;

		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "ClaimAction",
					"getPaymentAllocatedMLIWiseData()",
					"Error retrieving all Claim settled Payment Process Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return PaymentSavedMLIWiseData;
	}

	// ##################################
	// ###Claim Settlement for Payment Report input page
	public ActionForward ClaimSettlePaymentReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");
		// System.out.println("2");
		return mapping.findForward("ClaimSettlePaymentReportInputPage");

	}

	public ActionForward ClaimSettlePaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// System.out.println("!! ClaimSettlePaymentReport !! ");
		// System.out.println("request: start dt"+request.getParameter("startDt"));
		// System.out.println("request: end dt"+request.getParameter("endDt"));
		// System.out.println("request: MLI ID"+request.getParameter("memberId"));

		HttpSession session = request.getSession();

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		// System.out.println("1");
		// ##
		// Back Request come ShowPaymentAllocatedMliWiseData jsp
		/*
		 * if(session.getAttribute("ssRepStartDt")!=null&&session.getAttribute(
		 * "ssRepToDt")!=null){
		 * 
		 * System.out.println("startDt:"+session.getAttribute("ssRepStartDt"));
		 * System.out.println("endDt:"+session.getAttribute("ssRepToDt"));
		 * 
		 * dynaForm.setStartDt(session.getAttribute("ssRepStartDt").toString());
		 * dynaForm.setEndDt(session.getAttribute("ssRepToDt").toString());
		 * 
		 * }else{ throw new
		 * MessageException("Please Enter Start Date and End Date."); }
		 * 
		 * /*
		 */
		// Back Request come ShowPaymentAllocatedMliWiseData jsp

		// ##

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt;
		Date toDt;
		// System.out.println("2");
		// ##
		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK2Input.do?method=getClaimSettlePaymentSavedMLIWiseCK2Input");
			throw new MessageException("Please Enter Start Date and End Date.");
		}

		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());
			// System.out.println("3");
			// ##
			session.setAttribute("ssRepStartDt", fromDt);
			session.setAttribute("ssRepToDt", toDt);
			// ##

		} catch (Exception err) {

			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK2Input.do?method=getClaimSettlePaymentSavedMLIWiseCK2Input");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");
		}

		// ##

		String mliId = request.getParameter("memberId");
		// System.out.println("status : "+dynaForm.getStatus());
		// System.out.println("4");

		String appLevel = dynaForm.getStatus();
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println(" 1 sqlfromdate :" + sqlfromdate);
		// System.out.println(" 2 sqltodate :" + sqltodate);

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();

		list = getClaimSettlePaymentReportData(connection, sqlfromdate,
				sqltodate, mliId, appLevel, request);

		HttpSession sess = request.getSession(false);
		sess.setAttribute("REPORT", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("ClaimSettlePaymentReport");
	}

	private List getClaimSettlePaymentReportData(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String mliID,
			String app_level,HttpServletRequest request) throws DatabaseException, ParseException {
		Log.log(Log.INFO, "ClaimAction", "getClaimSettlePaymentReportData()",
				"Entered!");
		Double totalNetAmt=0.0D;
		// System.out.println("getClaimSettlePaymentReportData Entered");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList ClaimSettlePaymentReportData = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		try {
			// System.out.println("sqlfromdate :" + sqlfromdate);
			// System.out.println("sqltodate :" + sqltodate);
			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call Fun_getClaimPaymentReport(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliID);
			callableStmt.setString(5, app_level);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"getClaimSettlePaymentReportData()",
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
						if(i==6) {
							totalNetAmt=totalNetAmt+Double.parseDouble(resultset.getString(i));
							///System.out.println("totalNetAmt==="+totalNetAmt);
						}
						columnValue.add(resultset.getString(i));
					}
					nestData.add(columnValue);
				}
				BigDecimal b=new BigDecimal(totalNetAmt);
				BigDecimal bg= BigDecimal.valueOf(totalNetAmt);
				HttpSession sess = request.getSession(false);
				sess.setAttribute("totNetPayableAmt", bg);
				// System.out.println("list data " + nestData);
				ClaimSettlePaymentReportData.add(0, coulmName);
				ClaimSettlePaymentReportData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "ClaimAction",
					"getClaimSettlePaymentSavedMLIWiseCK2Data()",
					"Error retrieving all Claim settled Payment Process Data!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return ClaimSettlePaymentReportData;
	}

	// ###

	// Riyaz Payment Module 11-Dec-2018
	
	public ActionForward getBulkUploadTemplate(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws MessageException, Exception {
		
			Connection conn = DBConnection.getConnection();
			try{
		    
		    String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
			String contextPath = PropertyLoader.changeToOSpath(contextPath1);
			System.out.println("####getBulkUploadTemplate#### 1");
			
		    String TableName="claim_payment_utr_detail_stag";
		    OutputStream os = response.getOutputStream();
		    BulkUpload bulkupload = new BulkUpload();
		    LinkedHashMap<String, TableDetailBean> headerMap = bulkupload.getTableHeaderData(conn, TableName,"UTR");
		    System.out.println("####getBulkUploadTemplate#### 2");
		    if (headerMap.size()>1) {
		    	System.out.println("####getBulkUploadTemplate#### 3 ");
				byte[] b = generateTemplateFile(headerMap,contextPath);

				if (response != null){
					response.setContentType("APPLICATION/OCTET-STREAM");
				    response.setHeader("Content-Disposition","attachment; filename=TemplateFile.xls");
				    os.write(b);
				    os.flush();
				    System.out.println("####getBulkUploadTemplate#### 4 ");
				}
			}    
		    System.out.println("####getBulkUploadTemplate#### 5");
			}catch(Exception err){
				
				System.out.println("Erorr @ ClaimAction@getBulkUploadTemplate::"+err);
				
				
			}finally{
				DBConnection.freeConnection(conn);
			}
			return null;
	}
	
	
	
	 //##
	public byte[] generateTemplateFile(LinkedHashMap<String, TableDetailBean> TableHeaderHashMap,String contextPath) throws DocumentException,
	IOException {
				    System.out.println("---generateTemplate()---");
					StringBuffer strbuff = new StringBuffer();
					//System.out.println("ParamDataList:" + ParamDataList);				 
				    HSSFWorkbook workbook = new HSSFWorkbook();
					HSSFSheet sheet = workbook.createSheet("Template");
					// #### Header List Wrinting
					HSSFCellStyle MandatoryCellstyle = workbook.createCellStyle();
					MandatoryCellstyle.setFillForegroundColor(IndexedColors.GOLD.index);
					MandatoryCellstyle.setFillPattern(MandatoryCellstyle.SOLID_FOREGROUND);
					MandatoryCellstyle.setBorderBottom(MandatoryCellstyle.BORDER_THIN);
					MandatoryCellstyle.setBorderTop(MandatoryCellstyle.BORDER_THIN);
					MandatoryCellstyle.setBorderLeft(MandatoryCellstyle.BORDER_THIN);
					MandatoryCellstyle.setBorderRight(MandatoryCellstyle.BORDER_THIN);
					
					
					Row row = sheet.createRow(0);
					int hdcolnum = 0;
					Iterator it = TableHeaderHashMap.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry<String,TableDetailBean> Headerpair = (Map.Entry)it.next();
				        System.out.println(Headerpair.getKey() + " = " + Headerpair.getValue());
				        Cell cell = row.createCell(hdcolnum);
				        TableDetailBean tabledetailbean =  Headerpair.getValue();
				        if(tabledetailbean.getColumnAllowNullFlag().equals("N")){
					         System.out.println("....Setting sytle");
					         cell.setCellValue(Headerpair.getKey().toString());
					         cell.setCellStyle(MandatoryCellstyle);
				        }else{				        
				        	 cell.setCellValue(Headerpair.getKey().toString());
				        }
						hdcolnum++;
				        //it.remove(); // avoids a ConcurrentModificationException
				    }
				    // #### Header List Wrinting
					
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
					String strDate = sdf.format(cal.getTime());
					try {			
						FileOutputStream out = new FileOutputStream(new File(contextPath+ "\\Download\\TemplateUTRUpdateFile.xls"));
						workbook.write(out);
						out.close();
						//System.out.println("Excel written successfully..");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}		
					FileInputStream fis = new FileInputStream(contextPath+ "\\Download\\TemplateUTRUpdateFile.xls");
					//System.out.println("9");
					byte b[];
					int x = fis.available();
					b = new byte[x];
					// System.out.println(" b size"+b.length);
					fis.read(b);		
					return b;
}
	//##
	 
	

	// Riyaz Payment Module 09-Jan-2019
	public ActionForward ClaimSettledPaymentUTRUpdateProcess(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws MessageException, Exception {

		System.out.println("#ClaimSettledPaymentUTRUpdateProcess#");
		User user = getUserInformation(request);

		HashMap UploadStatus = null;
		DynaValidatorActionForm appForm = (DynaValidatorActionForm) form;
		FormFile formFile = (FormFile) appForm.get("filePath");
		String filePath = request.getRealPath("");
		// System.out.println("File Path"+request.getRealPath(""));

		if (!formFile.getContentType().equalsIgnoreCase(
				"application/vnd.ms-excel")
				|| (formFile.equals("") || formFile == null)) {
			System.out.println("formFile.getContentType()  :"
					+ formFile.getContentType());
			throw new MessageException("Please attach an excel file with .xls");
		}

		Connection conn = DBConnection.getConnection();
		System.out.println("conn :" + conn);
		System.out.println("#ClaimSettledPaymentUTRUpdateProcess# 1 ");
		BulkUpload BlkUpdObj = new BulkUpload();
		String TableName = "claim_payment_utr_detail_stag";
		try {
			LinkedHashMap<String, TableDetailBean> headerMap = BlkUpdObj.getTableHeaderData(conn, TableName,"UTR");
			System.out.println("#ClaimSettledPaymentUTRUpdateProcess# 2 ");
			UploadStatus = BlkUpdObj.CheckExcelData(formFile, headerMap,TableName, conn, user,"UTR");
		     						 

		} catch (Exception err) {
			throw new MessageException("err" + err);
		}

		System.out.println("UploadStatus :" + UploadStatus);

		String fileName = formFile.getFileName();

		if (UploadStatus != null) {
			
			System.out.println("#ClaimSettledPaymentUTRUpdateProcess# 3 ");
			
			ArrayList SuccessDataList = (ArrayList) UploadStatus.get("successRecord");
			ArrayList UnSuccessDataList = (ArrayList) UploadStatus.get("unsuccessRecord");
			ArrayList allerrors = (ArrayList) UploadStatus.get("allerror");
			System.out.println("allerrors:" + allerrors);
			System.out.println("UnSuccessDataList:" + UnSuccessDataList);

			HttpSession sess = request.getSession(false);
			request.setAttribute("UploadedStatus", UploadStatus);
			sess.setAttribute("SuccessDataList", SuccessDataList);
			sess.setAttribute("UnSuccessDataList", UnSuccessDataList);
			sess.setAttribute("Allerrors", allerrors);
			
			System.out.println("#ClaimSettledPaymentUTRUpdateProcess# 4 ");
		}

		// ##

		return mapping.findForward("success");
	}

	

	// #########UTR Checker #############
	public ActionForward ClaimSettlePaymentUTRUpdateCKInput(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
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
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

		dynaForm.setStartDt(dt.format(prevdate));
		dynaForm.setEndDt(dt.format(date));
		dynaForm.setMemberId("");

		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getStartDt :" + dynaForm.getStartDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.getEndDt :" + dynaForm.getEndDt());
		Log.log(Log.INFO, "ClaimAction", "getClaimSettleForPaymentInput",
				"dynaForm.setMemberId :" + dynaForm.getMemberId());

		// System.out.println("2-getAcctDtl:"+dynaForm.getAcctDtlAvlFlag());

		return mapping.findForward("ClaimSettlePaymentUTRUpdateInputPage");
	}

	// UTR Update Checker Data Show Action
	public ActionForward ClaimSettlePaymentUTRUpdateCK(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// System.out.println("request: start dt"+request.getParameter("startDt"));
		// System.out.println("request: end dt"+request.getParameter("endDt"));
		// System.out.println("request: MLI ID"+request.getParameter("memberId"));

		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;

		HttpSession session = request.getSession();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt;
		Date toDt;

		// ##
		if (dynaForm.getStartDt().length() == 0
				|| dynaForm.getEndDt().length() == 0) {
			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input");
			throw new MessageException("Please Enter Start Date and End Date.");
		}

		try {
			fromDt = sdf.parse(dynaForm.getStartDt());
			toDt = sdf.parse(dynaForm.getEndDt());

		} catch (Exception err) {
			session.setAttribute(
					"CurrentPage",
					"ClaimSettlePaymentSavedMLIWiseCK1Input.do?method=getClaimSettlePaymentSavedMLIWiseCK1Input");
			throw new MessageException(
					"Please Enter Valid Start Date and End Date.");
		}

		// ##

		String mliId = request.getParameter("memberId");
		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());
		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);
		session.setAttribute("sqlfromdate", dynaForm.getStartDt());
		session.setAttribute("sqltodate", dynaForm.getEndDt());
		// session.setAttribute("savedOnceFlag1", "true");

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;

		List list = new ArrayList();
		list = getClaimSettledPaymentUTRUpdateCkData(connection, sqlfromdate,
				sqltodate, mliId);
		HttpSession sess = request.getSession(false);
		// sess.setAttribute("ClaimSettlePaymentSavedMLIWiseCK1List", list);
		sess.setAttribute("UTRUpdateCKData", list);

		dynaForm.setColletralCoulmnName((ArrayList) list.get(0));
		dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));

		return mapping.findForward("ClaimSettlePaymentUTRUpdateCK");
	}

	// UTR Update Checker Data Show Fetch Method

	private List getClaimSettledPaymentUTRUpdateCkData(Connection conn,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate, String mliID)
			throws DatabaseException, ParseException {
		Log.log(Log.INFO, "CPDAO", "getClaimSettledPaymentUTRUpdateChkData()",
				"Entered!");

		System.out.println("getClaimSettledPaymentUTRUpdateChkData Entered");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		ResultSetMetaData resultSetMetaData = null;
		ArrayList coulmName = new ArrayList();
		ArrayList nestData = new ArrayList();
		ArrayList ClaimSettledPaymentUTRUpdateChkData = new ArrayList();
		int status = -1;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		try {
			// System.out.println("sqlfromdate :" + sqlfromdate);
			// System.out.println("sqltodate :" + sqltodate);
			// ##
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call CGTSIINTRANETUSER.Fun_getPaymentUTRUpdateChkData(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setDate(2, sqlfromdate);
			callableStmt.setDate(3, sqltodate);
			callableStmt.setString(4, mliID);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "ClaimAction",
						"ClaimSettledPaymentUTRUpdateChkData()",
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
				// System.out.println("list data " + nestData);
				ClaimSettledPaymentUTRUpdateChkData.add(0, coulmName);
				ClaimSettledPaymentUTRUpdateChkData.add(1, nestData);
			}
			resultset.close();
			resultset = null;
			callableStmt.close();
			callableStmt = null;
			resultSetMetaData = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "ClaimAction",
					"ClaimSettledPaymentUTRUpdateChkData()",
					"Error retrieving all Claim settled Payment Process Data!");
			DBConnection.freeConnection(conn);
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return ClaimSettledPaymentUTRUpdateChkData;
	}

	// $$
	// UTR Update Checker Approval Action
	public ActionForward ClaimSettlePaymentUTRUpdateCKApproval(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		System.out
				.println("---ClaimSettlePaymentUTRUpdateCKApproval--Method---");
		User user = getUserInformation(request);
		String userId = user.getUserId();

		Connection connection = null;
		// System.out.println("---ClaimSettlePaymentSavedMLIWiseCK1Approval--Method---");
		ClaimSettleForPaymentActionForm dynaForm = (ClaimSettleForPaymentActionForm) form;
		// System.out.println("checkbox value:"+dynaForm.getCheckboxfield());
		Map opSelectedmap = dynaForm.getCheckboxfield();

		String actionType = request.getParameter("actionType");
		System.out.println("opSelectedmap :" + opSelectedmap);

		// %%%
		HttpSession session = request.getSession(true);
		session.setAttribute("savedOnceUTRFlag", "true");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date fromDt = sdf.parse(session.getAttribute("sqlfromdate").toString());
		Date toDt = sdf.parse(session.getAttribute("sqltodate").toString());
		String mliId = request.getParameter("memberId");

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getYear(),
				fromDt.getMonth(), fromDt.getDate());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getYear(),
				toDt.getMonth(), toDt.getDate());

		// System.out.println("sqlfromdate :"+sqlfromdate);
		// System.out.println("sqltodate :"+sqltodate);
		String mliID = "";
		// %%%
		// ##

		// System.out.println("savedOnceFlag2::"+session.getAttribute("savedOnceFlag2"));

		if (null != session.getAttribute("savedOnceUTRFlag")) {
			System.out.println("Saving for Rev Approval 1");
			saveClaimSettlePaymentUTRUpdateCKApproval(opSelectedmap,
					actionType, userId);
			System.out.println("Saving for Rev Approval 2");
			session.removeAttribute("savedOnceUTRFlag");
		}
		String all_selectedmliId = opSelectedmap.keySet().toString();
		System.out.println(" 1 all_selectedmliId  :" + all_selectedmliId);
		all_selectedmliId = all_selectedmliId.substring(1,
				all_selectedmliId.length() - 1).replace(" ", "");
		System.out.println(" 2 all_selectedmliId  :" + all_selectedmliId);

		HttpSession sess = request.getSession(false);
		sess.setAttribute("actionType", actionType);
		return mapping
				.findForward("ClaimSettlePaymentUTRUpdateCKApprovalConfirm");

	}

	// UTR Update Checker Save Mathod
	private int saveClaimSettlePaymentUTRUpdateCKApproval(Map opSelectedmap,
			String ActionType, String userId) throws DatabaseException,
			ParseException, SQLException {

		Log.log(Log.INFO, "ClaimAction",
				"saveClaimSettlePaymentUTRUpdateCKApproval()", "Entered!");
		Connection conn = null;
		conn = DBConnection.getConnection();

		CallableStatement callableStmt1 = null;
		CallableStatement callableStmt2 = null;
		int saveFlag = 0;
		String errorCode = null;
		// System.out.println("cgpanPayment :"+cgpanPayment);
		// #################################################################################################

		// ArrayList<Set> cgpan200SetsArrList = new ArrayList<Set>();
		ArrayList<Set> payId200SetsArrList = new ArrayList<Set>();

		Set<String> keys = opSelectedmap.keySet();

		System.out.println("Key Size:" + keys.size());
		Set<String> payIdSet = new HashSet<String>();
		ArrayList<Integer> IntValueList = new ArrayList<Integer>();
		for (int n = 151; n < keys.size(); n = n + 150) {
			IntValueList.add(n);
		}
		System.out.println(" IntValueList : " + IntValueList);

		int count = 1;
		for (String item : keys) {
			// System.out.println("count:"+count);
			if (IntValueList.contains(count)) {
				Set payIdSetTemp = new HashSet();
				payIdSetTemp.addAll(payIdSet);
				// System.out.println("####cgpanSet Size:"+cgpanSet.size());
				// cgpan200SetsArrList.add(cgSetTemp);
				payId200SetsArrList.add(payIdSetTemp);

				payIdSet.clear();
				payIdSet.add(item);

			} else {
				payIdSet.add(item);
			}
			++count;
		}
		// System.out.println("@@count:" + count);
		// System.out.println("@@payIdSet:" + payIdSet);
		if (!payIdSet.isEmpty()) {
			payId200SetsArrList.add(payIdSet);
		}
		// System.out.println("cgpan200Sets :"+cgpan200SetsArrList);
		// System.out.println("cgpan200Sets Size:"+cgpan200SetsArrList.size());
		try {

			for (Set payIdSetTemp : payId200SetsArrList) {

				// System.out.println("Total Pay ID SELECTED :" + keys.size());
				// System.out.println("##payIdSetTemp Size:" +
				// payIdSetTemp.size());
				String payId = payIdSetTemp.toString();
				// HttpSession sess = request.getSession(false);
				// dynaForm.setColletralCoulmnValue((ArrayList) list.get(1));
				payId = payId.substring(1, payId.length() - 1).replace(" ", "");
				// cgpan = cgpan.replace("[","").replace("]",
				// "").replace(" ","");
				// System.out.println("after substr cgpan:"+cgpan);
				// ##
				// / }
				// #################################################################################################

				System.out.println("payid:=" + payId);

				callableStmt1 = conn.prepareCall("{call CGTSIINTRANETUSER.proc_claim_payment_utr_upd_ck(?,?,?,?)}");
				callableStmt1.setString(1, payId);
				callableStmt1.setString(2, ActionType);
				callableStmt1.setString(3, userId);
				callableStmt1.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStmt1.execute();
				errorCode = callableStmt1.getString(4);
				// System.out.println("errorCode :" + errorCode);

				if (null != errorCode) {
					// Log.log(Log.ERROR, "ClaimAction",
					// "saveClaimSettledPaymentProcessData()","SP returns a 1. Error code is :"
					// + errorCode);
					callableStmt1.close();

					throw new DatabaseException(errorCode);

				} else {

					// throw new
					// DatabaseException(" !! Claim Payment Succefully Initiated and sent for Approval to Reviewer Officer !! ");

					saveFlag = 0;
				}

			}

		} catch (Exception ex) {

			conn.rollback();
			ex.printStackTrace();
			// System.out.println("Error:" + ex);
			DBConnection.freeConnection(conn);
			// Log.log(Log.ERROR, "ClaimAction",
			// "saveClaimSettledPaymentProcessData()","Error retrieving all Sacing Claim settled Payment Process Data!");
			throw new DatabaseException(ex.getMessage());
		} finally {
			conn.commit();
			DBConnection.freeConnection(conn);
			callableStmt1.close();
			callableStmt1 = null;
			/*****************/
		}
		return saveFlag;
	}
	
/* downloadClaimSettledPaymentReport method newly add by parmanand to implement the generating the claimSettledPaymement CSV file separatly. Change 1 */
	
	public ActionForward downloadClaimSettledPaymentReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("Manish1111111===========");
		OutputStream os = response.getOutputStream();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String strDate = sdf.format(cal.getTime());


		String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
		String contextPath = PropertyLoader.changeToOSpath(contextPath1);

		HttpSession sess = request.getSession(false);
		String fileType = request.getParameter("fileType");
		String FlowLevel = request.getParameter("FlowLevel");
		//Change 1 done by parmanand
	    String lavelValue = (String)request.getParameter("LabelValue");
	    System.out.println("lavelValue==="+lavelValue);
		ArrayList ClmDataList = (ArrayList) sess.getAttribute(FlowLevel);
		ArrayList HeaderArrLst = (ArrayList) ClmDataList.get(0);
		int NoColumn = HeaderArrLst.size();
		if (fileType.equals("CSVType")) {
			byte[] b = generateClaimSettledPaymentReportCSV(ClmDataList, NoColumn, contextPath, request,lavelValue);
			
			if (response != null)
				response.setContentType("APPLICATION/OCTET-STREAM");//text/plain
			response.setHeader("Content-Disposition",
					"attachment; filename=ClaimPaymentExcelData" + strDate
							+ ".csv");
			
			os.write(b);
			os.flush();
		}
		
		if (fileType.equals("EXLType")) {
			byte[] b = generateEXL(ClmDataList, NoColumn, contextPath);
			if (response != null)
				response.setContentType("APPLICATION/OCTET-STREAM");
			    response.setHeader("Content-Disposition","attachment; filename=ClaimPaymentExcelData" + strDate+".xls");
			    os.write(b);
			    os.flush();
		}

		if (fileType.equals("PDFType")) {

			byte[] b = generatePDF(ClmDataList, NoColumn, contextPath);
			if (response != null)

				response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					"attachment; filename=ClaimPaymentExcelData" + strDate
							+ ".pdf");
			os.write(b);
			os.flush();
		}
		return null;
	}
	
	
	/* generateClaimSettledPaymentReportCSV method newly add by parmanand to implement the generating the claimSettledPaymement CSV file separatly. Change 2 */
	
	public byte[] generateClaimSettledPaymentReportCSV(ArrayList<ArrayList> ParamDataList,int No_Column, String contextPath,HttpServletRequest request,String lavelValue) throws IOException {
StringBuffer strbuff = new StringBuffer();
		
		
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
		int rownum = 1;
		for (ArrayList<String> RecordWiseLstObj : RecordWiseLst) {
			int colnum = 0;
			row = sheet.createRow(rownum);			
			for (String SingleRecordDataObj : RecordWiseLstObj) {
				Cell cell = row.createCell(colnum);
				if(SingleRecordDataObj!=null && SingleRecordDataObj.length()>0 ){
					if(lavelValue.equals("Checker Approved Claim Settled Data for Payment Bank")) {
						if(colnum==5) {
							cell.setCellType(cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);	
						}else {
							cell.setCellType(cell.CELL_TYPE_STRING);
							cell.setCellValue(SingleRecordDataObj);
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);
						}
					}if(lavelValue.equals("Checker Approved")) {
							if(colnum==4||colnum==5||colnum==6) {
								cell.setCellType(cell.CELL_TYPE_NUMERIC);
								cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
								colnum++;			
								rowDataLst.add(SingleRecordDataObj);	
							}else {
								cell.setCellType(cell.CELL_TYPE_STRING);
								cell.setCellValue(SingleRecordDataObj);
								colnum++;			
								rowDataLst.add(SingleRecordDataObj);
							}
					}
					if(lavelValue.equals("Maker saved for Initiated")) {
						if(colnum==4||colnum==5||colnum==6) {
							cell.setCellType(cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);	
						}else {
							cell.setCellType(cell.CELL_TYPE_STRING);
							cell.setCellValue(SingleRecordDataObj);
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);
						}
				}
				if(lavelValue.equals("Maker Initiated")) {
						if(colnum==4||colnum==5||colnum==6) {
							cell.setCellType(cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);	
						}else {
							cell.setCellType(cell.CELL_TYPE_STRING);
							cell.setCellValue(SingleRecordDataObj);
							colnum++;			
							rowDataLst.add(SingleRecordDataObj);
						}
				}
				if(lavelValue.equals("Reviewer Approved")) {
					if(colnum==4||colnum==5||colnum==6) {
						cell.setCellType(cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(SingleRecordDataObj));
						colnum++;			
						rowDataLst.add(SingleRecordDataObj);	
					}else {
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue(SingleRecordDataObj);
						colnum++;			
						rowDataLst.add(SingleRecordDataObj);
					}
				}
				}else{
					double defaultDVal=0.00;
					cell.setCellValue(defaultDVal);
					colnum++;			
					rowDataLst.add(SingleRecordDataObj);
				}
			}
			rownum++;
			//System.out.println("rownum Anand==="+rownum);
		}

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
	

	// $$
	// ######################
	
	//@@@@@@@@@@@@@@@@@@@@@@@
	/*
	public ActionForward TestingMail(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("TriggerMail() 1");
		TriggerMail();		
		System.out.println("TriggerMail() 2");
		return mapping.findForward("TestingMail");
	}
	
 	
public void TriggerMail() throws AddressException, MessagingException{
		
		Properties props = System.getProperties();
	    //props.put("mail.host", "192.168.10.118");
	    //props.put("mail.host", "202.138.96.11");
	    
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.host", "202.138.96.11");
	    props.put("mail.from", "administrator@cgtmse.in");
		
	    Session session1= null;
	    session1 = Session.getDefaultInstance(props, null);
	    try{
	    javax.mail.internet.MimeMessage msg = new javax.mail.internet.MimeMessage(session1);
	    msg.setFrom(new javax.mail.internet.InternetAddress("administrator@cgtmse.in"));
	    javax.mail.internet.InternetAddress[] Toaddress = {new javax.mail.internet.InternetAddress("riyaz.sanghar@cgtmse.in")};     
	    msg.setRecipients(javax.mail.Message.RecipientType.TO, Toaddress);
	    msg.setSubject("TEST MAIL FROM POJO");
	    msg.setSentDate(new java.util.Date());
	    msg.setText("Hello from pojo App ");
	    Transport.send(msg);
	    }catch(Exception err){
	    	
	    	err.printStackTrace();
	    }
		
	}
	/**/
	//@@@@@@@@@@@@@@@@@@@@@@@
	 

	public ClaimAction() {
	}
}
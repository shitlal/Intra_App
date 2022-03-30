// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   NewReportsAction.java

package com.cgtsi.action;
import java.io.PrintWriter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hpsf.Util;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.validator.DynaValidatorActionForm;

import com.cgtsi.actionform.AdministrationActionForm;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.actionform.GMActionForm;
import com.cgtsi.actionform.ReportActionForm;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.User;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimDetail;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.RegistrationDAO;
import com.cgtsi.reports.DanReport;
import com.cgtsi.reports.GeneralReport;
import com.cgtsi.reports.NewReportsDAO;
import com.cgtsi.reports.ReportManager;
import com.cgtsi.util.CustomisedDate;
import com.cgtsi.util.DBConnection;

// Referenced classes of package com.cgtsi.action:
//            BaseAction

public class NewReportsAction extends BaseAction {

	public ActionForward slabWiseInput(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaValidatorActionForm Form = (DynaValidatorActionForm) form;
		HttpSession session = request.getSession();
		ArrayList stateList = new ArrayList();
		ArrayList districtList = new ArrayList();
		ArrayList sectorList = new ArrayList();
		ArrayList rangeList = new ArrayList();
		Log.log(4, "SlabWiseInputREportAction", "slabReport", "Entered");
		Statement stmt = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();
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
		generalReport.setDateOfTheDocument42(prevDate);
		generalReport.setDateOfTheDocument43(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		try {
			String query = " SELECT ste_code,ste_name FROM state_master order by ste_name";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String state[] = null;
			for (; result.next(); stateList.add(state)) {
				state = new String[2];
				state[0] = result.getString(1);
				state[1] = result.getString(2);
			}

			session.setAttribute("stateList", stateList);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		if (request.getParameter("hiddenvalue") != null) {
			String state = (String) Form.get("slabState");
			String stateCode = "";
			try {
				String query = (new StringBuilder())
						.append(" select ste_code FROM state_master where ste_name='")
						.append(state).append("'").toString();
				stmt = connection.createStatement();
				for (result = stmt.executeQuery(query); result.next();)
					stateCode = result.getString(1);

				result = null;
				stmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			}
			try {
				String query = (new StringBuilder())
						.append(" select dst_code,dst_name,ste_code  from district_master where ste_code='")
						.append(stateCode).append("' order by dst_name")
						.toString();
				stmt = connection.createStatement();
				result = stmt.executeQuery(query);
				String district[] = null;
				for (; result.next(); districtList.add(district)) {
					district = new String[2];
					district[0] = (new StringBuilder())
							.append(result.getInt(1)).append("").toString();
					district[1] = result.getString(2);
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
		try {
			String query = " SELECT isc_code, isc_name FROM INDUSTRY_SECTOR ORDER BY isc_name";
			stmt = connection.createStatement();
			result = stmt.executeQuery(query);
			String sector[] = null;
			for (; result.next(); sectorList.add(sector)) {
				sector = new String[2];
				sector[0] = (new StringBuilder()).append(result.getInt(1))
						.append("").toString();
				sector[1] = result.getString(2);
			}

			session.setAttribute("sectorList", sectorList);
			result = null;
			stmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		try {
			String query = "SELECT range_id,range_desc FROM range_master";
			Statement stmt2 = connection.createStatement();
			ResultSet result2 = stmt2.executeQuery(query);
			String range[] = null;
			for (; result2.next(); rangeList.add(range)) {
				range = new String[2];
				range[0] = result2.getString(1);
				range[1] = result2.getString(2);
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
	}

	public ActionForward slabWiseOutPut(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm Form = (DynaActionForm) form;
		Log.log(4, "SlabWiseOutPutReportAction", "slabReport", "Entered");
		Statement stmt = null;
		ResultSet result = null;
		Connection connection = DBConnection.getConnection();
		Vector list = new Vector();
		double amount = 0.0D;
		int proposals = 0;
		DecimalFormat decimalFormat = new DecimalFormat("##########0.00");
		Date resta = (Date) Form.get("dateOfTheDocument42");
		Date resend = (Date) Form.get("dateOfTheDocument43");
		String state = (String) Form.get("slabState");
		String district = (String) Form.get("slabDistrict");
		String sector = (String) Form.get("slabIndustrySector");
		String mliID = ((String) Form.get("mliID")).trim();
		String bankName = null;
		String zoneName = null;
		String branchName = null;
		MLIInfo mliInfo = null;
		RegistrationDAO registrationDAO = new RegistrationDAO();
		if (mliID != null && !mliID.equals("")) {
			mliInfo = registrationDAO.getMemberDetails(mliID.substring(0, 4),
					mliID.substring(4, 8), mliID.substring(8, 12));
			bankName = mliInfo.getBankName();
			zoneName = mliInfo.getZoneName();
			branchName = mliInfo.getBranchName();
		}
		Form.set("bank", bankName);
		Form.set("zone", zoneName);
		Form.set("branch", branchName);
		String rangeFrom = ((String) Form.get("rangeFrom")).trim();
		String rangeTo = ((String) Form.get("rangeTo")).trim();
		String range = "";
		Date toDate = null;
		Date fromDate = null;
		if (!resend.equals("") && resend != null)
			toDate = (Date) Form.get("dateOfTheDocument43");
		if (!resta.equals("") && resta != null)
			fromDate = (Date) Form.get("dateOfTheDocument42");
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		if (fromDate != null && !fromDate.equals(""))
			startDate = new java.sql.Date(fromDate.getTime());
		if (toDate != null && !toDate.equals(""))
			endDate = new java.sql.Date(toDate.getTime());
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
			range = (new StringBuilder()).append(rangeFrom).append("-")
					.append(rangeTo).toString();
		CallableStatement slab = null;
		try {
			slab = connection
					.prepareCall("call Packallslab.PROCSLABREPORT(?,?,?,?,?,?,?,?)");
			//System.out.println("rajukkkk====");
			slab.setDate(1, startDate);
			slab.setDate(2, endDate);
			slab.setString(3, state);
			slab.setString(4, district);
			slab.setString(5, range);
			slab.setString(6, sector);
			slab.setString(7, mliID);
			slab.registerOutParameter(8, Constants.CURSOR);// curser
			slab.execute();
			result = (ResultSet) slab.getObject(8);
			String slabList[] = null;
			for (; result.next(); list.add(slabList)) {
				slabList = new String[3];
				slabList[0] = result.getString(1);
				slabList[1] = (new StringBuilder()).append(result.getInt(2))
						.append("").toString();
				slabList[2] = (new StringBuilder())
						.append(decimalFormat.format(result.getDouble(3)))
						.append("").toString();
				amount += result.getDouble(3);
				proposals += result.getInt(2);
			}

			request.setAttribute("slabList", list);
			request.setAttribute("totalProposals", (new StringBuilder())
					.append(proposals).append("").toString());
			request.setAttribute("totalLoan", decimalFormat.format(amount));
			result = null;
			stmt = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		DBConnection.freeConnection(connection);
		return mapping.findForward("success");
	}

	public ActionForward listOfPendingCases(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "NewReportsAction", "listOfPendingCases", "Entered");
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
		generalReport.setDateOfTheDocument40(prevDate);
		generalReport.setDateOfTheDocument41(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("memberId", "");
		dynaForm.set("ssi_name", "");
		Log.log(4, "NewReportsAction", "listOfPendingCases", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward pendingDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "pendingDetails", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument40");
		Date todate = (Date) dynaForm.get("dateOfTheDocument41");
		String memberID = (String) dynaForm.get("memberId");
		String ssi_name = ((String) dynaForm.get("ssi_name")).trim();
		String PAStringArray[] = null;
		ArrayList PAArrayList = new ArrayList();
		String id = "";
		query = (new StringBuilder())
				.append(" Select A.App_ref_no, A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID, a.APP_LOAN_TYPE,a.APP_STATUS,  to_char(TRUNC(b.WCP_FB_LIMIT_SANCTIONED_DT),'DD/MM/YYYY') WCP_FB_LIMIT_SANCTIONED_DT,to_char(TRUNC(c.TRM_AMOUNT_SANCTIONED_DT),'DD/MM/YYYY') TRM_AMOUNT_SANCTIONED_DT,d.SSI_UNIT_NAME,d.SSI_TYPE_OF_ACTIVITY, ltrim(rtrim(d.SSI_CONSTITUTION)),e.PMR_CHIEF_IT_PAN, a.APP_REMARKS  from Application_detail_temp@CGINTER a, working_capital_detail_temp@CGINTER B,   TERM_LOAN_DETAIL_temp@CGINTER C,ssi_detail_temp@CGINTER d, promoter_detail_temp@CGINTER e  Where A.app_ref_no =  B.app_ref_no AND A.app_ref_no =  C.app_ref_no  AND B.app_ref_no = C.app_ref_no  AND A.APP_STATUS <> 'RE'   AND a.SSI_REFERENCE_NUMBER = d.SSI_REFERENCE_NUMBER(+)  AND d.SSI_REFERENCE_NUMBER = e.SSI_REFERENCE_NUMBER(+)  AND trunc(a.app_submitted_dt) between to_date('")
				.append(fromdate).append("','dd/mm/yyyy')")
				.append("  AND to_date('").append(todate)
				.append("','dd/mm/yyyy') AND d.SSI_UNIT_NAME LIKE '%")
				.append(ssi_name).append("%'").toString();
		if (memberID.equals(null) || memberID.equals(""))
			query = (new StringBuilder()).append(query)
					.append("AND MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID=NVL('")
					.append(memberID)
					.append("',MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID)").toString();
		else if (!memberID.equals(null)) {
			String bankId = memberID.substring(0, 4);
			String zoneId = memberID.substring(4, 8);
			String branchId = memberID.substring(8, 12);
			if (!branchId.equals("0000")) {
				id = (new StringBuilder()).append(bankId).append(zoneId)
						.append(branchId).toString();
				query = (new StringBuilder()).append(query)
						.append("AND MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID=NVL('")
						.append(id)
						.append("',MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID)")
						.toString();
			} else if (!zoneId.equals("0000")) {
				id = (new StringBuilder()).append(bankId).append(zoneId)
						.toString();
				query = (new StringBuilder()).append(query)
						.append("AND MEM_BNK_ID||MEM_ZNE_ID=NVL('").append(id)
						.append("',MEM_BNK_ID||MEM_ZNE_ID)").toString();
			} else {
				id = bankId;
				query = (new StringBuilder()).append(query)
						.append("AND MEM_BNK_ID=NVL('").append(id)
						.append("',MEM_BNK_ID)").toString();
			}
		}
		query = (new StringBuilder()).append(query)
				.append("ORDER BY a.app_submitted_dt").toString();
		try {
			Statement pendingCaseDetailsStmt = null;
			ResultSet pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt = connection.createStatement();
			for (pendingCaseDetailsResult = pendingCaseDetailsStmt
					.executeQuery(query); pendingCaseDetailsResult.next(); 
					PAArrayList.add(PAStringArray)) {
				PAStringArray = new String[11];
				PAStringArray[0] = pendingCaseDetailsResult.getString(1);
				PAStringArray[1] = pendingCaseDetailsResult.getString(2);
				PAStringArray[2] = pendingCaseDetailsResult.getString(3);
				PAStringArray[3] = pendingCaseDetailsResult.getString(4);
				PAStringArray[4] = pendingCaseDetailsResult.getString(5);
				PAStringArray[5] = pendingCaseDetailsResult.getString(6);
				PAStringArray[6] = pendingCaseDetailsResult.getString(7);
				PAStringArray[7] = pendingCaseDetailsResult.getString(8);
				PAStringArray[8] = pendingCaseDetailsResult.getString(9);
				PAStringArray[9] = pendingCaseDetailsResult.getString(10);
				PAStringArray[10] = pendingCaseDetailsResult.getString(11);
			}

			pendingCaseDetailsResult.close();
			pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt.close();
			pendingCaseDetailsStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("pendingCaseDetailsArray", PAArrayList);
		request.setAttribute("pendingCaseDetailsArray_size", (new Integer(
				PAArrayList.size())).toString());
		Log.log(4, "NewReportsAction", "pendingDetails", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward newMonthlyReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "NewReportsAction", "newMonthlyReport", "Entered");
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
		generalReport.setDateOfTheDocument38(prevDate);
		generalReport.setDateOfTheDocument39(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(4, "NewReportsAction", "newMonthlyReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward newMonthlyProgressReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "newMonthlyProgressReport", "Entered");
		Connection connection = DBConnection.getConnection();
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(2);
		int day = calendar.get(5);
		int year = calendar.get(5);
		month--;
		day++;
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument38");
		Date todate = (Date) dynaForm.get("dateOfTheDocument39");
		String MonthlyStringArray[] = null;
		PreparedStatement monthlyProgressStmt = null;
		ArrayList monthlyProgressReportArray = new ArrayList();
		try {
			query = (new StringBuilder())
					.append(" select count(b.SSI_REFERENCE_NUMBER), sum(SSI_NO_OF_EMPLOYEES) emp, round(sum(SSI_PROJECTED_SALES_TURNOVER)/100000,2) turn, round(sum(SSI_PROJECTED_EXPORTS)/100000,2) expo ,'current' from application_detail a , ssi_detail b where a.SSI_REFERENCE_NUMBER = b.SSI_REFERENCE_NUMBER and a.APP_STATUS not in ('RE') and trunc(a.APP_APPROVED_DATE_TIME) between to_date('")
					.append(fromdate)
					.append("','dd/mm/yyyy') and to_date('")
					.append(todate)
					.append("','dd/mm/yyyy')")
					.append(" union")
					.append(" select count(b.SSI_REFERENCE_NUMBER), sum(SSI_NO_OF_EMPLOYEES) emp,")
					.append(" round(sum(SSI_PROJECTED_SALES_TURNOVER)/100000,2) turn,")
					.append(" round(sum(SSI_PROJECTED_EXPORTS)/100000,2) expo,'prev' from application_detail a , ssi_detail b")
					.append(" where a.SSI_REFERENCE_NUMBER = b.SSI_REFERENCE_NUMBER and a.APP_STATUS not in ('RE')")
					.append(" and trunc(a.APP_APPROVED_DATE_TIME) between add_months(to_date('")
					.append(fromdate)
					.append("','dd/mm/yyyy'),-12) and add_months(to_date('")
					.append(todate).append("','dd/mm/yyyy'),-12) order by 5")
					.toString();
			monthlyProgressStmt = connection.prepareStatement(query);
			System.out.println("query==============RAJJ"+query);
			ResultSet monthlyProgressResult;
			for (monthlyProgressResult = monthlyProgressStmt.executeQuery(); monthlyProgressResult
					.next(); monthlyProgressReportArray.add(MonthlyStringArray)) {
				MonthlyStringArray = new String[5];
				MonthlyStringArray[0] = monthlyProgressResult.getString(1);
				MonthlyStringArray[1] = monthlyProgressResult.getString(2);
				MonthlyStringArray[2] = monthlyProgressResult.getString(3);
				MonthlyStringArray[3] = monthlyProgressResult.getString(4);
			}

			monthlyProgressResult.close();
			monthlyProgressResult = null;
			monthlyProgressStmt.close();
			monthlyProgressStmt = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("monthlyProgressReportArray",
				monthlyProgressReportArray);
		request.setAttribute("monthlyProgressReportArray_size", (new Integer(
				monthlyProgressReportArray.size())).toString());
		Log.log(4, "NewReportsAction", "newMonthlyProgressReport", "Exited");
		return mapping.findForward("success");
	}

	 public ActionForward npaAccountCount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
     Log.log(4, "ReportsAction", "npaAccountCount", "Entered");
     DynaActionForm dynaForm = (DynaActionForm)form;
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
     generalReport.setDateOfTheDocument12(prevDate);
     generalReport.setDateOfTheDocument13(date);
     BeanUtils.copyProperties(dynaForm, generalReport);
     Log.log(4, "NewReportsAction", "npaAccountCount", "Exited");
     return mapping.findForward("success");
 }

 public ActionForward npaAccountCountDetailsnew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
     ArrayList mliWiseGuarNpaNoAmtList = new ArrayList();
     Statement stmt = null;
     ResultSet result = null;
     Connection connection = DBConnection.getConnection();
     HttpSession session = request.getSession();
     DynaActionForm dynaForm = (DynaActionForm)form;
     Date fromdat = (Date)dynaForm.get("dateOfTheDocument12");
     Date toDate = (Date)dynaForm.get("dateOfTheDocument13");
     try
     {
         String query = (new StringBuilder("\tselect     bnk,sum(nos) as GuarNos,to_char(sum(amt),'99999999999999.99') as GuarAmt,sum(npanos)  as NPAMO,    decode( to_char(sum(npaamt),'9999999999.99') ,.00,0,to_char(sum(npaamt),'9999999999.99')) as NPAAmt,   decode( to_char(sum(npanos)/sum(nos)*100,'9999999999.99') ,.00,0,to_char(sum(npanos)/sum(nos)*100,'9999999999.99')) as npaprcnt,     decode( to_char(sum(npaamt)/sum(amt)*100,'9999999999.99') ,.00,0,to_char(sum(npaamt)/sum(amt)*100,'9999999999.99')) as prcntamt    from  (select mem_bank_name bnk,count(cgpan) nos, sum(decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount))/100000 amt,0 npanos,0 npaamt   from application_detail a,ssi_detail s,member_info m  where a.ssi_reference_number = s.ssi_reference_number      and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id     and trunc(APP_GUAR_START_DATE_TIME) between  to_date('")).append(fromdat).append("','dd/mm/yyyy')  and to_date('").append(toDate).append("','dd/mm/yyyy')  ").append("  and app_status not in ('RE') group by mem_bank_name union all select mem_bank_name bnk,0 nos,0 amt, ").append("  count(cgpan) npanos,sum(decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount))/100000 npaamt ").append("  from application_detail a,ssi_detail s,member_info m  where a.ssi_reference_number = s.ssi_reference_number   ").append("  and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id   ").append("  and trunc(APP_GUAR_START_DATE_TIME) between  to_date('").append(fromdat).append("','dd/mm/yyyy')  and to_date('").append(toDate).append("','dd/mm/yyyy')       ").append("  and app_status not in ('RE')  and bid in(select bid from npa_detail_Temp union all select bid from npa_detail)  group by mem_bank_name) ").append("  group by bnk  order by 1").toString();
         
         //System.out.println("query=== is ===="+query);
         stmt = connection.createStatement();
         result = stmt.executeQuery(query);
         String mliWiseGuarNpaNoAmt[] = (String[])null;
         for(; result.next(); mliWiseGuarNpaNoAmtList.add(mliWiseGuarNpaNoAmt))
         {
             mliWiseGuarNpaNoAmt = new String[7];
             mliWiseGuarNpaNoAmt[0] = result.getString(1);
             mliWiseGuarNpaNoAmt[1] = result.getString(2);
             mliWiseGuarNpaNoAmt[2] = result.getString(3);
             mliWiseGuarNpaNoAmt[3] = result.getString(4);
             mliWiseGuarNpaNoAmt[4] = result.getString(5);
             mliWiseGuarNpaNoAmt[5] = result.getString(6);
             mliWiseGuarNpaNoAmt[6] = result.getString(7);
         }

         session.setAttribute("mliWiseGuarNpaNoAmtList", mliWiseGuarNpaNoAmtList);
         session.setAttribute("mliWiseGuarNpaNoAmt", Integer.valueOf(mliWiseGuarNpaNoAmtList.size()));
         result = null;
         stmt = null;
     }
     catch(Exception exception)
     {
         Log.logException(exception);
         throw new DatabaseException(exception.getMessage());
     }
     return mapping.findForward("success");
 }

 public ActionForward npaAccountCountFinancialWiseInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
     Log.log(4, "ReportsAction", "npaAccountCountFinancialWiseInput", "Entered");
     DynaActionForm dynaForm = (DynaActionForm)form;
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
     generalReport.setDateOfTheDocument12(prevDate);
     generalReport.setDateOfTheDocument13(date);
     BeanUtils.copyProperties(dynaForm, generalReport);
     Log.log(4, "NewReportsAction", "npaAccountCountFinancialWiseInput", "Exited");
     return mapping.findForward("success");
 }

 public ActionForward npaAccountCountFinancialYrWiseDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
     ArrayList mliWiseGuarNpaNoAmtList = new ArrayList();
     Statement stmt = null;
     ResultSet result = null;
     Connection connection = DBConnection.getConnection();
     HttpSession session = request.getSession();
     DynaActionForm dynaForm = (DynaActionForm)form;
     Date fromdat = (Date)dynaForm.get("dateOfTheDocument12");
     Date toDate = (Date)dynaForm.get("dateOfTheDocument13");
     try
     {
         String query = (new StringBuilder("select to_char(FinancialYear,'YYYY')||'-'||to_number(to_char(FinancialYear,'YYYY')+1) as FinancialYear,sum(GuarNo), decode( to_char(sum(GuarAmt),'9999999999.99') ,.00,0,to_char(sum(GuarAmt),'9999999999.99')) guaramt, sum(NPANO), decode( to_char(sum(NPAAMT),'9999999999.99') ,.00,0,to_char(sum(NPAAMT),'9999999999.99')) NPAMT , decode( to_char((sum(NPANO)/sum(GuarNo)*100),'99999999999999.99') ,.00,0,to_char((sum(NPANO)/sum(GuarNo)*100),'99999999999999.99')) NPAGuarPrcnt,  decode( to_char((sum(NPAAMT)/sum(GuarAmt)*100),'99999999999999.99') ,.00,0,to_char((sum(NPAAMT)/sum(GuarAmt)*100),'99999999999999.99')) NPAGUARPRAMT from ( select trunc(fy) as FinancialYear, sum(nos) as GuarNo,(sum(appamt)) as GuarAmt ,0 as NPANO, 0 as NPAAMT from ( select mem_bank_name,fystartdate(app_guar_start_date_time) fy ,count(cgpan) nos, sum(decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount))/100000 appamt from application_detail a,ssi_detail s,member_info m where a.ssi_reference_number = s.ssi_reference_number and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id and app_status not in ('RE') and trunc(APP_GUAR_START_DATE_TIME) between to_date('")).append(fromdat).append("','dd/mm/yyyy')  and to_date('").append(toDate).append("','dd/mm/yyyy') ").append("group by mem_bank_name,fystartdate(app_guar_start_date_time) order by fy asc   )    group by fy ").append("union all ").append("select trunc(fy) as FinancialYear,0 as GuarNo,0 as GuarAmt, sum(nos) as NPANO,sum(appamt) as NPAAMT from ( ").append("select mem_bank_name,fystartdate(a.app_guar_start_date_time) fy,count(cgpan) nos, ").append("sum(decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount))/100000 appamt ").append("from application_detail a,ssi_detail s,member_info m ").append("where a.ssi_reference_number = s.ssi_reference_number ").append("and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id ").append("and app_status not in ('RE') ").append("and trunc(APP_GUAR_START_DATE_TIME) between to_date('").append(fromdat).append("','dd/mm/yyyy')  and to_date('").append(toDate).append("','dd/mm/yyyy') ").append("and bid in ").append("(select bid from npa_detail_Temp ").append("union all ").append("select bid from npa_detail) ").append("group by mem_bank_name,fystartdate(a.app_guar_start_date_time)  order by fy asc   )    group by fy ").append(") ").append("group by FinancialYear   ").append("order by FinancialYear asc ").toString();
         //System.out.println("query"+query);
         stmt = connection.createStatement();
         result = stmt.executeQuery(query);
         String mliWiseGuarNpaNoAmt[] = (String[])null;
         for(; result.next(); mliWiseGuarNpaNoAmtList.add(mliWiseGuarNpaNoAmt))
         {
             mliWiseGuarNpaNoAmt = new String[7];
             mliWiseGuarNpaNoAmt[0] = result.getString(1);
             mliWiseGuarNpaNoAmt[1] = result.getString(2);
             mliWiseGuarNpaNoAmt[2] = result.getString(3);
             mliWiseGuarNpaNoAmt[3] = result.getString(4);
             mliWiseGuarNpaNoAmt[4] = result.getString(5);
             mliWiseGuarNpaNoAmt[5] = result.getString(6);
             mliWiseGuarNpaNoAmt[6] = result.getString(7);
         }

         session.setAttribute("mliWiseGuarNpaNoAmtList", mliWiseGuarNpaNoAmtList);
         session.setAttribute("mliWiseGuarNpaNoAmt", Integer.valueOf(mliWiseGuarNpaNoAmtList.size()));
         result = null;
         stmt = null;
     }
     catch(Exception exception)
     {
         Log.logException(exception);
         throw new DatabaseException(exception.getMessage());
     }
     return mapping.findForward("success");
 }

	
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
			//System.out.println("query1--"+query);
			if (!state.equals(""))
				query = (new StringBuilder())
						.append(query)
						.append("and a.app_status not in ('RE') and m.MEM_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
						.append(state.trim()).append("')").toString();
			//System.out.println("query2--"+query);
			if (!District.equals(""))
				query = (new StringBuilder())
						.append(query)
						.append("and a.app_status not in ('RE') and m.MEM_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
						.append(District.trim()).append("')").toString();
			//System.out.println("query3--"+query);
			query = (new StringBuilder()).append(query)
					.append("group by PMR_chief_gender").toString();
			//System.out.println("query4--"+query);
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
				////System.out.println("query1=="+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and a.app_status not in ('RE') and M.MEM_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				////System.out.println("query2=="+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and a.app_status not in ('RE') and M.MEM_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				////System.out.println("query3=="+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				////System.out.println("query4=="+query);
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
				////System.out.println("query5=="+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				////System.out.println("query6"+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				////System.out.println("query7"+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				////System.out.println("query8"+query);
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
				////System.out.println("query9"+query);
				if (!state.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_STATE_NAME =(select STE_NAME from  state_master where  STE_code='")
							.append(state.trim()).append("')").toString();
				////System.out.println("query10"+query);
				if (!District.equals(""))
					query = (new StringBuilder())
							.append(query)
							.append("and C.SSI_DISTRICT_NAME =(SELECT DST_NAME FROM DISTRICT_MASTER WHERE DST_CODE='")
							.append(District.trim()).append("')").toString();
				////System.out.println("query11"+query);
				query = (new StringBuilder()).append(query)
						.append("group by PMR_chief_gender").toString();
				////System.out.println("query12"+query);
			}
		}
		try {
			Statement womenEntrepreneurDetailsStmt = null;
			ResultSet womenEntrepreneurDetailsResult = null;
			womenEntrepreneurDetailsStmt = connection.createStatement();
			for (womenEntrepreneurDetailsResult = womenEntrepreneurDetailsStmt
					.executeQuery(query); womenEntrepreneurDetailsResult.next(); womenEntrepreneurDetailsArray.add(WomenStringArray)) {
				WomenStringArray = new String[2];
				WomenStringArray[0] = womenEntrepreneurDetailsResult
						.getString(1);
				WomenStringArray[1] = womenEntrepreneurDetailsResult
						.getString(2);
			}

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

	public ActionForward fetchDistrict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String state = "";
		state = (String) dynaForm.get("State");
		HttpSession session = request.getSession();
		String District_Array[] = null;
		ArrayList district_arraylist_data = new ArrayList();
		String sql_district = (new StringBuilder())
				.append("SELECT DST_CODE,DST_NAME FROM DISTRICT_MASTER WHERE ste_code='")
				.append(state).append("'").toString();
		try {
			con = DBConnection.getConnection(false);
			st = con.createStatement();
			for (rs = st.executeQuery(sql_district); rs.next(); district_arraylist_data
					.add(District_Array)) {
				District_Array = new String[2];
				District_Array[0] = rs.getString(1);
				District_Array[1] = rs.getString(2);
			}

		} catch (Exception e) {
		
		} finally {
			if (con != null)
				con.close();
		}
		request.setAttribute("district_arraylist_data", district_arraylist_data);
		request.setAttribute("district_arraylist_data_size", (new Integer(
				district_arraylist_data.size())).toString());
		return mapping.findForward("district");
	}
	
	
	//koteswar for new report
	
	public ActionForward claimPaymentReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String condition = request.getParameter("condition");
		DynaActionForm dynaForm = (DynaActionForm) form;
		ArrayList cgpanList = null;
		ArrayList memberIdList = null;
		ArrayList otherList = null;

		String totalEntries = (String) request.getParameter("rowcount");

		String cgpans[] = (String[]) request.getParameterValues("enterCgpan");
		int totalEntry = 0;
		if (totalEntries != null && !"".equals(totalEntries)) {
			totalEntry = Integer.parseInt(totalEntries);
		}
		Log.log(4, "NewReportsAction", "Claim settled", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		String excQry = "";//v
		ArrayList cgmemList = new ArrayList(); //v
		String cgmemStringList[] = null;//v
		
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument26");
		Date todate = (Date) dynaForm.get("dateOfTheDocument27");
		String memberID = (String) dynaForm.get("memberId");
		String ssi_name = ((String) dynaForm.get("ssi_name")).trim();
		String clm_date_for_report = null;
		if ("approvedDateWiseReport".equals(condition))
			clm_date_for_report = "clm_approved_dt";
		else if ("paymentDateWiseReport".equals(condition))
			clm_date_for_report = "clm_payment_dt";
		else
			return mapping.findForward("nocondition");
		String PAStringArray[] = null;
		ArrayList PAArrayList = new ArrayList();
		String id = "";
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		request.setAttribute("date1", fromdate);
		request.setAttribute("date2", todate);
		String stDate = String.valueOf(fromdate);
		String todateNew = String.valueOf(todate);
		if (stDate.equals(null) || stDate.equals(""))
			throw new NoDataException(
					"From Date shold not be empty, Please Enter from Date ");
		if (todateNew.equals(null) || todateNew.equals(""))
			throw new NoDataException(
					"To Date shold not be empty, Please Enter To Date ");
		if (memberID.length() == 12 && !memberids.contains(memberID)) {
			Log.log(2, "CPDAO", "getAllMemberIds()",
					"No Member Ids in the database!");
			throw new DatabaseException("No Member Ids in the database");
		}

		if ("approvedDateWiseReport".equals(condition)) {

			query = " SELECT memberid,\n"
					+ "         accno,\n"
					+ "         ifsccode,\n"
					+ "         SUM (netpaid),\n"
					+ "         BENEFICARY_NAME,\n"
					+ "         BENEFICARY_BANKNAME,\n"
					+ "         email\n"
					+ "    FROM (  SELECT c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid,\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO) accno,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE) ifsccode,\n"
					+ "                   SUM (\n"
					+ "                        NVL (ctd_tc_first_inst_pay_amt, 0)\n"
					+ "                      - NVL (ctd_tc_asf_deductable, 0))\n"
					+ "                      netpaid,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL) email\n"
					+ "              FROM APPLICATION_DETAIL A,\n"
					+ "                   SSI_DETAIL s,\n"
					+ "                   CLAIM_DETAIL c,\n"
					+ "                   CLAIM_TC_DETAIL ctd,\n"
					+ "                   member_info_include_bank_det m,\n"
					+ "                   CLAIM_APPLICATION_AMOUNT caa\n"
					+ "             WHERE     A.ssi_reference_number = s.ssi_Reference_number\n"
					+ "                   AND s.bid = c.bid\n"
					+ "                   AND C.CLM_REF_NO = ctd.clm_ref_no\n"
					+ "                   AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id =\n"
					+ "                          m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id\n"
					+ "                   AND A.cgpan = caa.cgpan\n"
					+ "                   AND A.cgpan = ctd.cgpan\n"
					+ "                   AND app_loan_type IN ('TC')\n"
					+ "                   AND clm_status IN ('AP')\n"
					+ "                   AND TRUNC (clm_approved_dt) BETWEEN TO_DATE ('"+fromdate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                                                   AND TO_DATE ('"+todate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                   AND ctd.cgpan NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "                   and  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id  NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "          GROUP BY c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE),\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL),\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO)\n"
					+ "          UNION ALL\n"
					+ "            SELECT c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid,\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO) accno,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE) ifsccode,\n"
					+ "                   SUM (\n"
					+ "                        NVL (cwd_wc_first_inst_pay_amt, 0)\n"
					+ "                      - NVL (cwd_wc_asf_deductable, 0))\n"
					+ "                      netpaid,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL) email\n"
					+ "              FROM APPLICATION_DETAIL A,\n"
					+ "                   SSI_DETAIL s,\n"
					+ "                   CLAIM_DETAIL c,\n"
					+ "                   CLAIM_WC_DETAIL cwd,\n"
					+ "                   member_info_include_bank_det m,\n"
					+ "                   CLAIM_APPLICATION_AMOUNT caa\n"
					+ "             WHERE     A.ssi_reference_number = s.ssi_Reference_number\n"
					+ "                   AND s.bid = c.bid\n"
					+ "                   AND C.CLM_REF_NO = cwd.clm_ref_no\n"
					+ "                   AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id =\n"
					+ "                          m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id\n"
					+ "                   AND A.cgpan = caa.cgpan\n"
					+ "                   AND A.cgpan = cwd.cgpan\n"
					+ "                   AND app_loan_type IN ('WC')\n"
					+ "                   AND clm_status IN ('AP')\n"
					+ "                   AND TRUNC (clm_approved_dt) BETWEEN TO_DATE ('"+fromdate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                                                   AND TO_DATE ('"+todate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                   AND cwd.cgpan NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "                     and  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id  NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "          GROUP BY c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE),\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL),\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO)\n"
					+ "          UNION ALL\n"
					+ "            SELECT c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid,\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO) accno,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE) ifsccode,\n"
					+ "                   SUM (\n"
					+ "                        NVL (ctd_tc_first_inst_pay_amt, 0)\n"
					+ "                      - NVL (ctd_tc_asf_deductable, 0)\n"
					+ "                      + NVL (cwd_wc_first_inst_pay_amt, 0)\n"
					+ "                      - NVL (cwd_wc_asf_deductable, 0))\n"
					+ "                      netpaid,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL) email\n"
					+ "              FROM APPLICATION_DETAIL A,\n"
					+ "                   SSI_DETAIL s,\n"
					+ "                   CLAIM_DETAIL c,\n"
					+ "                   CLAIM_WC_DETAIL cwd,\n"
					+ "                   member_info_include_bank_det m,\n"
					+ "                   CLAIM_APPLICATION_AMOUNT caa,\n"
					+ "                   CLAIM_TC_DETAIL ctd\n"
					+ "             WHERE     A.ssi_reference_number = s.ssi_Reference_number\n"
					+ "                   AND s.bid = c.bid\n"
					+ "                   AND C.CLM_REF_NO = cwd.clm_ref_no\n"
					+ "                   AND C.CLM_REF_NO = ctd.clm_ref_no\n"
					+ "                   AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id =\n"
					+ "                          m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id\n"
					+ "                   AND A.cgpan = caa.cgpan\n"
					+ "                   AND A.cgpan = ctd.cgpan\n"
					+ "                   AND A.cgpan = cwd.cgpan\n"
					+ "                   AND app_loan_type IN ('CC')\n"
					+ "                   AND clm_status IN ('AP')\n"
					+ "                   AND TRUNC (clm_approved_dt) BETWEEN TO_DATE ('"+fromdate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                                                   AND TO_DATE ('"+todate+"',\n"
					+ "                                                                'DD/MM/YYYY')\n"
					+ "                   AND cwd.cgpan NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "                     and  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id  NOT IN (SELECT cgpan FROM EXCLUDED_PAYNENT_DETAIL_INFO)\n"
					+ "          GROUP BY c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id,\n"
					+ "                   BENEFICARY_NAME,\n"
					+ "                   BENEFICARY_BANKNAME,\n"
					+ "                   DECODE (MEM_IFSC_CODE, '', '-', MEM_IFSC_CODE),\n"
					+ "                   DECODE (MEM_EMAIL, '', '-', MEM_EMAIL),\n"
					+ "                   DECODE (MEM_ACC_NO, '', '-', MEM_ACC_NO))\n"
					+ "GROUP BY memberid,\n" + "         accno,\n"
					+ "         ifsccode,\n" + "         BENEFICARY_NAME,\n"
					+ "         BENEFICARY_BANKNAME,\n" + "         email ";
			
			excQry = "SELECT distinct CGPAN, MEMBERID FROM excluded_paynent_detail_info WHERE CREATED_DATE = TRUNC (SYSDATE)";
			
			////System.out.println("from date"+fromdate);
			////System.out.println("to date "+todate);

			/*
			 * query =
			 * " select  memberid,accno,ifsccode,sum(netpaid),BENEFICARY_NAME,BENEFICARY_BANKNAME, email from (  \n"
			 * +
			 * "                SELECT   c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO) accno,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE) ifsccode,\n"
			 * +
			 * "                sum(nvl(ctd_tc_first_inst_pay_amt,0) -nvl(ctd_tc_asf_deductable,0)) netpaid,BENEFICARY_NAME,BENEFICARY_BANKNAME,decode(MEM_EMAIL,'','-',MEM_EMAIL) email \n"
			 * +
			 * "                   FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,member_info_include_bank_det m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number \n"
			 * +
			 * "                   AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n"
			 * +
			 * "               app_loan_type IN ('TC') AND clm_status IN ('AP') \n"
			 * +
			 * " AND TRUNC("+clm_date_for_report+") between  to_date('"+fromdate
			 * +"','DD/MM/YYYY') and  to_date('"+todate+"','DD/MM/YYYY')  \n" +
			 * "               and A.cgpan not in ( select cgpan from  excl_cgpan_payment)  group by   c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id,BENEFICARY_NAME,BENEFICARY_BANKNAME,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),\n"
			 * +
			 * "               decode(MEM_EMAIL,'','-',MEM_EMAIL),decode(MEM_ACC_NO,'','-',MEM_ACC_NO) \n"
			 * + "               UNION ALL \n" +
			 * "               SELECT  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO) accno,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE) ifsccode,\n"
			 * +
			 * "               sum(nvl(cwd_wc_first_inst_pay_amt,0) - nvl(cwd_wc_asf_deductable,0) ) netpaid,BENEFICARY_NAME,BENEFICARY_BANKNAME,decode(MEM_EMAIL,'','-',MEM_EMAIL) email\n"
			 * +
			 * "               FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,member_info_include_bank_det m,CLAIM_APPLICATION_AMOUNT caa \n"
			 * +
			 * "               WHERE A.ssi_reference_number = s.ssi_Reference_number \n"
			 * + "               AND s.bid = c.bid \n" +
			 * "               AND C.CLM_REF_NO = cwd.clm_ref_no \n" +
			 * "               AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n"
			 * + "               AND A.cgpan = caa.cgpan \n" +
			 * "               AND A.cgpan = cwd.cgpan \n" +
			 * "               AND app_loan_type IN ('WC') \n" +
			 * "               AND clm_status IN ('AP') \n" +
			 * "   AND TRUNC("+clm_date_for_report
			 * +") between  to_date('"+fromdate
			 * +"','DD/MM/YYYY') and  to_date('"+todate+"','DD/MM/YYYY')  \n" +
			 * "               and  A.cgpan not in ( select cgpan from  excl_cgpan_payment) group by  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id,BENEFICARY_NAME,BENEFICARY_BANKNAME,\n"
			 * +
			 * "               decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL),decode(MEM_ACC_NO,'','-',MEM_ACC_NO) \n"
			 * + "               UNION ALL \n" +
			 * "               SELECT  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO) accno,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE) ifsccode,\n"
			 * +
			 * "               sum(nvl(ctd_tc_first_inst_pay_amt,0)-nvl(ctd_tc_asf_deductable,0)+nvl(cwd_wc_first_inst_pay_amt,0) - nvl(cwd_wc_asf_deductable,0) ) netpaid,BENEFICARY_NAME,BENEFICARY_BANKNAME,\n"
			 * + "               decode(MEM_EMAIL,'','-',MEM_EMAIL) email \n" +
			 * "                            FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,member_info_include_bank_det m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n"
			 * +
			 * "               WHERE A.ssi_reference_number = s.ssi_Reference_number \n"
			 * + "               AND s.bid = c.bid \n" +
			 * "               AND C.CLM_REF_NO = cwd.clm_ref_no \n" +
			 * "               AND C.CLM_REF_NO = ctd.clm_ref_no \n" +
			 * "               AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n"
			 * + "               AND A.cgpan = caa.cgpan \n" +
			 * "               AND A.cgpan = ctd.cgpan \n" +
			 * "               AND A.cgpan = cwd.cgpan \n" +
			 * "               AND app_loan_type IN ('CC') \n" +
			 * "               AND clm_status IN ('AP') \n" +
			 * "    AND TRUNC("+clm_date_for_report
			 * +") between  to_date('"+fromdate
			 * +"','DD/MM/YYYY') and  to_date('"+todate+"','DD/MM/YYYY')  \n" +
			 * "               and A.cgpan not in ( select cgpan from  excl_cgpan_payment) group by  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id,BENEFICARY_NAME,BENEFICARY_BANKNAME,\n"
			 * +
			 * "               decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL),decode(MEM_ACC_NO,'','-',MEM_ACC_NO) \n"
			 * +
			 * "                )  group by memberid,accno,ifsccode,BENEFICARY_NAME,BENEFICARY_BANKNAME,email "
			 * ;
			 */

		}
	//	//System.out.println("NRA query : " + query);
	//	//System.out.println("NRA excQry : "+excQry);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date clm_approved_date = null;
		Date clm_payment_date = null;
		String numberformat = "-?\\d+(\\.\\d+)?";
		String firstInstAmt = "0.0";
		String asfDeductedAmt = "0.0";
		String netPaidAmt = "0.0";
		Date clm_outward_date = null;
		String refundableAmt = "0.0";

		try {
			Statement pendingCaseDetailsStmt = null;
			ResultSet pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt = connection.createStatement();
			for (pendingCaseDetailsResult = pendingCaseDetailsStmt
					.executeQuery(query); pendingCaseDetailsResult.next(); PAArrayList
					.add(PAStringArray)) {

				// //System.out.println(pendingCaseDetailsResult.getFetchSize());

				// kot PAStringArray = new String[22];
				PAStringArray = new String[9];
				PAStringArray[0] = pendingCaseDetailsResult.getString(1);
				PAStringArray[1] = pendingCaseDetailsResult.getString(2);
				PAStringArray[2] = pendingCaseDetailsResult.getString(3);
				PAStringArray[3] = pendingCaseDetailsResult.getString(4);
				PAStringArray[4] = pendingCaseDetailsResult.getString(5);
				PAStringArray[5] = pendingCaseDetailsResult.getString(6);
				PAStringArray[6] = pendingCaseDetailsResult.getString(7);
				// PAStringArray[7] = pendingCaseDetailsResult.getString(8);

			}
			
			//v s
			for(pendingCaseDetailsResult = pendingCaseDetailsStmt.executeQuery(excQry); pendingCaseDetailsResult.next(); 
				cgmemList.add(cgmemStringList))
			{
				cgmemStringList = new String[2];
				cgmemStringList[0] = pendingCaseDetailsResult.getString(1);
				cgmemStringList[1] = pendingCaseDetailsResult.getString(2);
				////System.out.println("NRA cgmemStringList[0] : "+cgmemStringList[0]+ "\t cgmemStringList[1] : "+cgmemStringList[1]);
			}
			//v e
			pendingCaseDetailsResult.close();
			pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt.close();
			pendingCaseDetailsStmt = null;
			
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		
		if (PAArrayList.size() == 0) {
			throw new NoDataException(
					"There are no Claim Ref Numbers that match the query.");
		}
		//v s
		if(cgmemList.size() == 0)
		{
			////System.out.println("NRA if(cgmemList.size() == 0)");
			//throw new NoDataException("There Are No Excluded CGPAN/MEMBER ID.");
		}
		//v e
		request.setAttribute("reportCondition", condition);
		request.setAttribute("pendingCaseDetailsArray", PAArrayList);
		request.setAttribute("pendingCaseDetailsArray_size", (new Integer(
				PAArrayList.size())).toString());
		
		request.setAttribute("ExcludedCgpanMemberId", cgmemList);//v
		request.setAttribute("ExcludedCgpanMemberIdSize", (new Integer(cgmemList.size())).toString());//v
		Log.log(4, "NewReportsAction", "Claim settled", "Exited");
		return mapping.findForward("success");
	}
	
	//KOTESWAR FOR NEW ADDED ACC NO AND IFSC CODE AND EMAIL IDS
	
	public ActionForward claimSettledReport27072015(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String condition = request.getParameter("condition");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "Claim settled", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument26");
		Date todate = (Date) dynaForm.get("dateOfTheDocument27");
		String memberID = (String) dynaForm.get("memberId");
		String ssi_name = ((String) dynaForm.get("ssi_name")).trim();
		String clm_date_for_report = null;
		if ("approvedDateWiseReport".equals(condition))
			clm_date_for_report = "clm_approved_dt";
		else if ("paymentDateWiseReport".equals(condition))
			clm_date_for_report = "clm_payment_dt";
		else
			return mapping.findForward("nocondition");
		String PAStringArray[] = null;
		ArrayList PAArrayList = new ArrayList();
		String id = "";
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		request.setAttribute("date1", fromdate);
		request.setAttribute("date2", todate);
		String stDate = String.valueOf(fromdate);
		String todateNew = String.valueOf(todate);
		if (stDate.equals(null) || stDate.equals(""))
			throw new NoDataException(
					"From Date shold not be empty, Please Enter from Date ");
		if (todateNew.equals(null) || todateNew.equals(""))
			throw new NoDataException(
					"To Date shold not be empty, Please Enter To Date ");
		if (memberID.length() == 12 && !memberids.contains(memberID)) {
			Log.log(2, "CPDAO", "getAllMemberIds()",
					"No Member Ids in the database!");
			throw new DatabaseException("No Member Ids in the database");
		}
		if ("approvedDateWiseReport".equals(condition))
		{
		
		if (fromdate == null && memberID.equals("")) {
			//kot1
			 query = " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)  FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
		      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" +
		      //" AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +		      	      
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('WC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		     // " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +  		      
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)     \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = ctd.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('CC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		     // " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY')\n"+
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" + 
		      " ORDER BY 1,2,5 "; 
			 
		} else if (memberID.equals(null) || memberID.equals("")) {
			
			//kot2
			
			 query = "SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
		      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" + 
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('WC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY')\n" + 
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +		      		       
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = ctd.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('CC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" +
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY') \n" +		      		       
		      " ORDER BY 1,2,5 ";
		    						 
		} else {
			
			
			//kot 3
			
			  query = " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
		      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+		       		       
		      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('WC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+ 		      		       
		      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
		      " UNION ALL \n" + 
		      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
		      " A.cgpan,ssi_unit_name, \n" + 
		      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
		      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
		      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
		      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
		      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),decode(MEM_ACC_NO,'','-',MEM_ACC_NO),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
		      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
		      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
		      " AND s.bid = c.bid \n" + 
		      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
		      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
		      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
		      " AND A.cgpan = caa.cgpan \n" + 
		      " AND A.cgpan = ctd.cgpan \n" + 
		      " AND A.cgpan = cwd.cgpan \n" + 
		      " AND app_loan_type IN ('CC') \n" + 
		      " AND clm_status IN ('AP') \n" + 
		      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
		      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+ 		      
		      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
		      " ORDER BY 1,2,5 ";		    		
		}}
		
		else
		{
			if (fromdate == null && memberID.equals("")) {
				//kot1
				 query = " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
			      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" +
			      //" AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +		      	      
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('WC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			     // " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +  		      
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = ctd.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('CC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			     // " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY')\n"+
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" + 
			      " ORDER BY 1,2,5 "; 
				 
			} else if (memberID.equals(null) || memberID.equals("")) {
				
				//kot2
				
				 query = "SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
			      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n"+
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" + 
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('WC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY')\n" + 
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')\n" +		      		       
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = ctd.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('CC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" +
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY') \n" +		      		       
			      " ORDER BY 1,2,5 ";
			    						 
			} else {
				
				
				//kot 3
				
				  query = " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, A.cgpan,ssi_unit_name, DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, ctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, ctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa WHERE A.ssi_reference_number = s.ssi_Reference_number AND s.bid = c.bid AND C.CLM_REF_NO = ctd.clm_ref_no AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id AND A.cgpan = caa.cgpan AND  A.cgpan=ctd.cgpan AND\n" + 
			      " app_loan_type IN ('TC') AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+		       		       
			      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('WC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+ 		      		       
			      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
			      " UNION ALL \n" + 
			      " SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n" + 
			      " A.cgpan,ssi_unit_name, \n" + 
			      " DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n" + 
			      " cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n" + 
			      " LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n" + 
			      " caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n" + 
			      " cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,decode(MEM_IFSC_CODE,'','-',MEM_IFSC_CODE),decode(MEM_EMAIL,'','-',MEM_EMAIL)    \n" + 
			      " FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n" + 
			      " WHERE A.ssi_reference_number = s.ssi_Reference_number \n" + 
			      " AND s.bid = c.bid \n" + 
			      " AND C.CLM_REF_NO = cwd.clm_ref_no \n" + 
			      " AND C.CLM_REF_NO = ctd.clm_ref_no \n" + 
			      " AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n" + 
			      " AND A.cgpan = caa.cgpan \n" + 
			      " AND A.cgpan = ctd.cgpan \n" + 
			      " AND A.cgpan = cwd.cgpan \n" + 
			      " AND app_loan_type IN ('CC') \n" + 
			      " AND clm_status IN ('AP') \n" + 
			      " AND TRUNC("+clm_date_for_report+") >= to_date('"+fromdate+"','DD/MM/YYYY') \n" + 
			      " AND TRUNC("+clm_date_for_report+") <= to_date('"+todate+"','DD/MM/YYYY')"+ 		      
			      " AND c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id= '"+memberID+"'  \n" + 
			      " ORDER BY 1,2,5 ";		    		
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date clm_approved_date = null;
		Date clm_payment_date = null;
		String numberformat = "-?\\d+(\\.\\d+)?";
		String firstInstAmt = "0.0";
		String asfDeductedAmt = "0.0";
		String netPaidAmt = "0.0";
		Date clm_outward_date = null;
		String refundableAmt = "0.0";

		try {
			Statement pendingCaseDetailsStmt = null;
			ResultSet pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt = connection.createStatement();
			for (pendingCaseDetailsResult = pendingCaseDetailsStmt
					.executeQuery(query); pendingCaseDetailsResult.next(); PAArrayList.add(PAStringArray)) {
				//kot  PAStringArray = new String[22];
				PAStringArray = new String[24];
				PAStringArray[0] = pendingCaseDetailsResult.getString(1);
				PAStringArray[1] = pendingCaseDetailsResult.getString(2);
				PAStringArray[2] = pendingCaseDetailsResult.getString(3);
				PAStringArray[3] = pendingCaseDetailsResult.getString(4);
				PAStringArray[4] = pendingCaseDetailsResult.getString(5);
				PAStringArray[5] = pendingCaseDetailsResult.getString(6);
				PAStringArray[6] = pendingCaseDetailsResult.getString(7);
				PAStringArray[7] = pendingCaseDetailsResult.getString(8);
				PAStringArray[8] = pendingCaseDetailsResult.getString(9);
				PAStringArray[9] = pendingCaseDetailsResult.getString(10);
				PAStringArray[10] = pendingCaseDetailsResult.getString(11);

				firstInstAmt = String.valueOf(pendingCaseDetailsResult
						.getString(12));
				asfDeductedAmt = String.valueOf(pendingCaseDetailsResult
						.getString(13));
				netPaidAmt = String.valueOf(pendingCaseDetailsResult
						.getString(14));

				PAStringArray[14] = pendingCaseDetailsResult.getString(15);
				PAStringArray[15] = pendingCaseDetailsResult.getString(16);
				PAStringArray[16] = pendingCaseDetailsResult.getString(17);
				clm_outward_date = pendingCaseDetailsResult.getDate(18);
				clm_approved_date = pendingCaseDetailsResult.getDate(19);
				clm_payment_date = pendingCaseDetailsResult.getDate(20);

				if (firstInstAmt.matches(numberformat))
					PAStringArray[11] = firstInstAmt;
				else
					PAStringArray[11] = "0.0";
				if (asfDeductedAmt.matches(numberformat))
					PAStringArray[12] = asfDeductedAmt;
				else
					PAStringArray[12] = "0.0";
				if (netPaidAmt.matches(numberformat))
					PAStringArray[13] = netPaidAmt;
				else
					PAStringArray[13] = "0.0";

				if (clm_outward_date != null)
					PAStringArray[17] = sdf.format(clm_outward_date);
				else
					PAStringArray[17] = "";

				if (clm_approved_date != null)
					PAStringArray[18] = sdf.format(clm_approved_date);
				else
					PAStringArray[18] = "";
				if (clm_payment_date != null)
					PAStringArray[19] = sdf.format(clm_payment_date);
				else
					PAStringArray[19] = "";

				refundableAmt = String.valueOf(pendingCaseDetailsResult
						.getString(21));

				if (refundableAmt.matches(numberformat))
					PAStringArray[20] = refundableAmt;
				else
					PAStringArray[20] = "0.0";

				PAStringArray[21] = pendingCaseDetailsResult.getString(22);
				//kot
				PAStringArray[22] = pendingCaseDetailsResult.getString(23);//pendingCaseDetailsResult.getString(23);
				PAStringArray[23] = pendingCaseDetailsResult.getString(24);;//pendingCaseDetailsResult.getString(24);
			}

			pendingCaseDetailsResult.close();
			pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt.close();
			pendingCaseDetailsStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		if (PAArrayList.size() == 0) {
			throw new NoDataException(
					"There are no Claim Ref Numbers that match the query.");
		}
		request.setAttribute("reportCondition", condition);
		request.setAttribute("pendingCaseDetailsArray", PAArrayList);
		request.setAttribute("pendingCaseDetailsArray_size", (new Integer(
				PAArrayList.size())).toString());
		Log.log(4, "NewReportsAction", "Claim settled", "Exited");
		return mapping.findForward("success");
	}
	
//KULDEEP
	public ActionForward claimSettledReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String condition = request.getParameter("condition");
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "Claim settled", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument26");
		Date todate = (Date) dynaForm.get("dateOfTheDocument27");
		String memberID = (String) dynaForm.get("memberId");
		String ssi_name = ((String) dynaForm.get("ssi_name")).trim();
		String clm_date_for_report = null;
		if ("approvedDateWiseReport".equals(condition))
			clm_date_for_report = "clm_approved_dt";
		else if ("paymentDateWiseReport".equals(condition))
			clm_date_for_report = "clm_payment_dt";
		else
			return mapping.findForward("nocondition");
		
	
		String PAStringArray[] = null;
		ArrayList PAArrayList = new ArrayList();
		String id = "";
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		request.setAttribute("date1", fromdate);
		request.setAttribute("date2", todate);
		
		String stDate = String.valueOf(fromdate);
		String todateNew = String.valueOf(todate);
		
		if (stDate.equals(null) || stDate.equals(""))
			throw new NoDataException(
					"From Date should not be empty, Please Enter from Date ");
		if (todateNew.equals(null) || todateNew.equals(""))
			throw new NoDataException(
					"To Date should not be empty, Please Enter To Date ");
		if (memberID.length() == 12 && !memberids.contains(memberID)) {
			Log.log(2, "CPDAO", "getAllMemberIds()",
					"No Member Ids in the database!");
			throw new DatabaseException("No Member Ids in the database");
		}
		if (fromdate == null && memberID.equals("")) 
			/*query = (new StringBuilder())
					.append(" SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \nA.cgpan,ssi_unit_name, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \nctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, \nLEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \ncaa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, \nctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \nWHERE A.ssi_reference_number = s.ssi_Reference_number \nAND s.bid = c.bid \nAND C.CLM_REF_NO = ctd.clm_ref_no \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND A.cgpan = caa.cgpan \nAND  A.cgpan=ctd.cgpan \nAND     app_loan_type IN ('TC') \nAND clm_status IN ('AP') \nAND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')     \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('WC') \n")
					.append("AND clm_status IN ('AP') \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')    \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND C.CLM_REF_NO = ctd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = ctd.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('CC') \n")
					.append("AND clm_status IN ('AP') \n").append("AND TRUNC(")
					.append(clm_date_for_report).append(") <= to_date('")
					.append(todate).append("','DD/MM/YYYY')   \n")
					.append("ORDER BY 1,2,5 \n").append("\n").toString();*/
			
			
			 query =  "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       ctd_npa_outstanding_amt_revise nps_os, "
				+ "       ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            ctd_npa_outstanding_amt_revise) "
				+ "       - ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       ctd_tc_clm_elig_amt, "
				+ "       ctd_tc_first_inst_pay_amt, "
				+ "       ctd_tc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - NVL (ctd_tc_asf_deductable, 0) "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + NVL (ctd_tc_sf_refundable, 0) "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y', ctd_tc_sf_refundable, 0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_TC_DETAIL ctd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND app_loan_type IN ('TC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (cwd_sf_refund_flag, 'N'), "
				+ "          'N', (  cwd_wc_first_inst_pay_amt "
				+ "                - NVL (cwd_wc_asf_deductable, 0) "
				+ "                - NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CWD_SERV_TAX_DED, 0) "
				+ "                - NVL (CWD_KK_CESS_TAX_DED, 0)), "
				+ "          (  cwd_wc_first_inst_pay_amt "
				+ "           + NVL (cwd_wc_sf_refundable, 0) "
				+ "           + NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CWD_SERV_TAX_DED, 0) "
				+ "           + NVL (CWD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y', cwd_wc_sf_refundable, 0), "
				+ "       cwd_sf_refund_flag, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "        DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('WC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "       + ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable + cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - ctd_tc_asf_deductable "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + ctd_tc_asf_deductable "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, "
				+ "               'Y', ctd_tc_sf_refundable + cwd_wc_sf_refundable, "
				+ "               0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa, "
				+ "       CLAIM_TC_DETAIL ctd "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('CC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "ORDER BY 1, 2, 5";
			
			
			
			//=//System.out.println("query1"+query);
				 /*(new StringBuilder())
             .append(" SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \nA.cgpan,ssi_unit_name, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \nctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, \nLEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \ncaa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, \nctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable - nvl(CTD_SWBH_CESS_TAX_DED,0) - nvl(CTD_SERV_TAX_DED,0)-NVL(CTD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,''),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,nvl(CTD_SWBH_CESS_TAX_DED,0),nvl(CTD_SERV_TAX_DED,0),NVL(CTD_KK_CESS_TAX_DED,0) \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \nWHERE A.ssi_reference_number = s.ssi_Reference_number \nAND s.bid = c.bid \nAND C.CLM_REF_NO = ctd.clm_ref_no \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND A.cgpan = caa.cgpan \nAND  A.cgpan=ctd.cgpan \nAND     app_loan_type IN ('TC') \nAND clm_status IN ('AP') \nAND TRUNC(")
             .append(clm_date_for_report)
             .append(") <= to_date('")
             .append(todate)
             .append("','DD/MM/YYYY')     \n")
             .append("UNION ALL \n")
             .append("SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
             .append("A.cgpan,ssi_unit_name, \n")
             .append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
             .append("cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n")
             .append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n")
             .append("caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable \n")
             .append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable - nvl(CWD_SWBH_CESS_TAX_DED,0) - nvl(CWD_SERV_TAX_DED,0) -NVL(CWD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,nvl(CWD_SWBH_CESS_TAX_DED,0),nvl(CWD_SERV_TAX_DED,0),NVL(CWD_KK_CESS_TAX_DED,0) \n")
             .append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n")
             .append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
             .append("AND s.bid = c.bid \n")
             .append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
             .append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
             .append("AND A.cgpan = caa.cgpan \n")
             .append("AND A.cgpan = cwd.cgpan \n")
             .append("AND app_loan_type IN ('WC') \n")
             .append("AND clm_status IN ('AP') \n")
             .append("AND TRUNC(")
             .append(clm_date_for_report)
             .append(") <= to_date('")
             .append(todate)
             .append("','DD/MM/YYYY')    \n")
             .append("UNION ALL \n")
             .append("SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
             .append("A.cgpan,ssi_unit_name, \n")
             .append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
             .append("cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n")
             .append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n")
             .append("caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n")
             .append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable - nvl(CWD_SWBH_CESS_TAX_DED,0) - nvl(CWD_SERV_TAX_DED,0)-NVL(CWD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,''),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,nvl(CWD_SWBH_CESS_TAX_DED,0),nvl(CWD_SERV_TAX_DED,0),NVL(CWD_KK_CESS_TAX_DED,0)  \n")
             .append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n")
             .append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
             .append("AND s.bid = c.bid \n")
             .append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
             .append("AND C.CLM_REF_NO = ctd.clm_ref_no \n")
             .append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
             .append("AND A.cgpan = caa.cgpan \n")
             .append("AND A.cgpan = ctd.cgpan \n")
             .append("AND A.cgpan = cwd.cgpan \n")
             .append("AND app_loan_type IN ('CC') \n")
             .append("AND clm_status IN ('AP') \n").append("AND TRUNC(")
             .append(clm_date_for_report).append(") <= to_date('")
             .append(todate).append("','DD/MM/YYYY')   \n")
             .append("ORDER BY 1,2,5 \n").append("\n").toString();*/
		 ////System.out.println("query1"+query);
	  else if (memberID.equals(null) || memberID.equals("")) 
			/*query = (new StringBuilder())
					.append(" SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \nA.cgpan,ssi_unit_name, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \nctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, \nLEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \ncaa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, \nctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \nWHERE A.ssi_reference_number = s.ssi_Reference_number \nAND s.bid = c.bid \nAND C.CLM_REF_NO = ctd.clm_ref_no \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND A.cgpan = caa.cgpan  \nAND   A.cgpan=ctd.cgpan  \nAND  app_loan_type IN ('TC') \nAND clm_status IN ('AP') \nAND TRUNC(")
					.append(clm_date_for_report)
					.append(") >=  to_date('")
					.append(fromdate)
					.append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')     \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('WC') \n")
					.append("AND clm_status IN ('AP') \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") >= to_date('")
					.append(fromdate)
					.append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')    \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND C.CLM_REF_NO = ctd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = ctd.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('CC') \n")
					.append("AND clm_status IN ('AP') \n").append("AND TRUNC(")
					.append(clm_date_for_report).append(") >= to_date('")
					.append(fromdate).append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(").append(clm_date_for_report)
					.append(") <= to_date('").append(todate)
					.append("','DD/MM/YYYY')   \n").append("ORDER BY 1,2,5 \n")
					.append("\n").toString();*/
			
// New modified for swach bharat start from here
		
			
			 query =  "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       ctd_npa_outstanding_amt_revise nps_os, "
				+ "       ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            ctd_npa_outstanding_amt_revise) "
				+ "       - ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       ctd_tc_clm_elig_amt, "
				+ "       ctd_tc_first_inst_pay_amt, "
				+ "       ctd_tc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - NVL (ctd_tc_asf_deductable, 0) "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + NVL (ctd_tc_sf_refundable, 0) "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y', ctd_tc_sf_refundable, 0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_TC_DETAIL ctd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND app_loan_type IN ('TC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"				
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (cwd_sf_refund_flag, 'N'), "
				+ "          'N', (  cwd_wc_first_inst_pay_amt "
				+ "                - NVL (cwd_wc_asf_deductable, 0) "
				+ "                - NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CWD_SERV_TAX_DED, 0) "
				+ "                - NVL (CWD_KK_CESS_TAX_DED, 0)), "
				+ "          (  cwd_wc_first_inst_pay_amt "
				+ "           + NVL (cwd_wc_sf_refundable, 0) "
				+ "           + NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CWD_SERV_TAX_DED, 0) "
				+ "           + NVL (CWD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y', cwd_wc_sf_refundable, 0), "
				+ "       cwd_sf_refund_flag, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "        DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('WC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"		
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "       + ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable + cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - ctd_tc_asf_deductable "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + ctd_tc_asf_deductable "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, "
				+ "               'Y', ctd_tc_sf_refundable + cwd_wc_sf_refundable, "
				+ "               0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa, "
				+ "       CLAIM_TC_DETAIL ctd "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('CC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"		
				+ "ORDER BY 1, 2, 5";
			
		 /*koteswar   query ="SELECT mem_bank_name, "
				 + "       mem_zone_name, "
				 + "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				 + "       A.cgpan, "
				 + "       ssi_unit_name, "
				 + "       DECODE (NVL (app_reapprove_amount, 0), "
				 + "               0, app_approved_amount, "
				 + "               app_reapprove_amount) "
				 + "          appamt, "
				 + "       ctd_npa_outstanding_amt_revise nps_os, "
				 + "       ctd_npa_recovered_revise reco, "
				 + "         LEAST ( "
				 + "            DECODE (NVL (app_reapprove_amount, 0), "
				 + "                    0, app_approved_amount, "
				 + "                    app_reapprove_amount), "
				 + "            ctd_npa_outstanding_amt_revise) "
				 + "       - ctd_npa_recovered_revise "
				 + "          netos, "
				 + "       caa_applied_amount, "
				 + "       ctd_tc_clm_elig_amt, "
				 + "       ctd_tc_first_inst_pay_amt, "
				 + "       ctd_tc_asf_deductable, "
				 + "       DECODE ( "
				 + "          NVL (ctd_sf_refund_flag, 'N'), "
				 + "          'N', (  ctd_tc_first_inst_pay_amt "
				 + "                - NVL (ctd_tc_asf_deductable, 0) "
				 + "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				 + "                - NVL (CTD_SERV_TAX_DED, 0) "
				 + "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				 + "          (  ctd_tc_first_inst_pay_amt "
				 + "           + NVL (ctd_tc_sf_refundable, 0) "
				 + "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				 + "           + NVL (CTD_SERV_TAX_DED, 0) "
				 + "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				 + "          netpaid, "
				 + "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				 + "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				 + "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				 + "       CLM_PYMT_OUTWARD_DT, "
				 + "       clm_approved_dt, "
				 + "       clm_payment_dt, "
				 + "       DECODE (ctd_sf_refund_flag, 'Y', ctd_tc_sf_refundable, 0), "
				 + "       ctd_sf_refund_flag, "
				 + "       NVL (CTD_SWBH_CESS_TAX_DED, 0), "
				 + "       NVL (CTD_SERV_TAX_DED, 0), "
				 + "       NVL (CTD_KK_CESS_TAX_DED, 0) "
				 + "  FROM APPLICATION_DETAIL A, "
				 + "       SSI_DETAIL s, "
				 + "       CLAIM_DETAIL c, "
				 + "       CLAIM_TC_DETAIL ctd, "
				 + "       MEMBER_INFO m, "
				 + "       CLAIM_APPLICATION_AMOUNT caa "
				 + " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				 + "       AND s.bid = c.bid "
				 + "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				 + "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				 + "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				 + "       AND A.cgpan = caa.cgpan "
				 + "       AND A.cgpan = ctd.cgpan "
				 + "       AND app_loan_type IN ('TC') "
				 + "       AND clm_status IN ('AP') "
				 + "       AND TRUNC (clm_approved_dt) >= TO_DATE ('"+fromdate+"', 'DD/MM/YYYY') "
				 + "       AND TRUNC (clm_approved_dt) <= TO_DATE ('"+todate +"', 'DD/MM/YYYY') "
				 
				 + "UNION "
				 + "SELECT mem_bank_name, "
				 + "       mem_zone_name, "
				 + "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				 + "       A.cgpan, "
				 + "       ssi_unit_name, "
				 + "       DECODE (NVL (app_reapprove_amount, 0), "
				 + "               0, app_approved_amount, "
				 + "               app_reapprove_amount) "
				 + "          appamt, "
				 + "       cwd_npa_outstanding_amt_revise nps_os, "
				 + "       cwd_npa_recovered_revise reco, "
				 + "         LEAST ( "
				 + "            DECODE (NVL (app_reapprove_amount, 0), "
				 + "                    0, app_approved_amount, "
				 + "                    app_reapprove_amount), "
				 + "            cwd_npa_outstanding_amt_revise) "
				 + "       - cwd_npa_recovered_revise "
				 + "          netos, "
				 + "       caa_applied_amount, "
				 + "       cwd_wc_clm_elig_amt, "
				 + "       cwd_wc_first_inst_pay_amt, "
				 + "       cwd_wc_asf_deductable, "
				 + "       DECODE ( "
				 + "          NVL (cwd_sf_refund_flag, 'N'), "
				 + "          'N', (  cwd_wc_first_inst_pay_amt "
				 + "                - NVL (cwd_wc_asf_deductable, 0) "
				 + "                - NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				 + "                - NVL (CWD_SERV_TAX_DED, 0) "
				 + "                - NVL (CWD_KK_CESS_TAX_DED, 0)), "
				 + "          (  cwd_wc_first_inst_pay_amt "
				 + "           + NVL (cwd_wc_sf_refundable, 0) "
				 + "           + NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				 + "           + NVL (CWD_SERV_TAX_DED, 0) "
				 + "           + NVL (CWD_KK_CESS_TAX_DED, 0))) "
				 + "          netpaid, "
				 + "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				 + "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				 + "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				 + "       CLM_PYMT_OUTWARD_DT, "
				 + "       clm_approved_dt, "
				 + "       clm_payment_dt, "
				 + "       DECODE (cwd_sf_refund_flag, 'Y', cwd_wc_sf_refundable, 0), "
				 + "       cwd_sf_refund_flag, "
				 + "       NVL (CWD_SWBH_CESS_TAX_DED, 0), "
				 + "       NVL (CWD_SERV_TAX_DED, 0), "
				 + "       NVL (CWD_KK_CESS_TAX_DED, 0) "
				 + "  FROM APPLICATION_DETAIL A, "
				 + "       SSI_DETAIL s, "
				 + "       CLAIM_DETAIL c, "
				 + "       CLAIM_WC_DETAIL cwd, "
				 + "       MEMBER_INFO m, "
				 + "       CLAIM_APPLICATION_AMOUNT caa "
				 + " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				 + "       AND s.bid = c.bid "
				 + "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				 + "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				 + "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				 + "       AND A.cgpan = caa.cgpan "
				 + "       AND A.cgpan = cwd.cgpan "
				 + "       AND app_loan_type IN ('WC') "
				 + "       AND clm_status IN ('AP') "
				 + "       AND TRUNC (clm_approved_dt) >= TO_DATE ('"+fromdate+"', 'DD/MM/YYYY') "
				 + "       AND TRUNC (clm_approved_dt) <= TO_DATE ('"+todate+"', 'DD/MM/YYYY') "
				 
				 + "UNION "
				 + "SELECT mem_bank_name, "
				 + "       mem_zone_name, "
				 + "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				 + "       A.cgpan, "
				 + "       ssi_unit_name, "
				 + "       DECODE (NVL (app_reapprove_amount, 0), "
				 + "               0, app_approved_amount, "
				 + "               app_reapprove_amount) "
				 + "          appamt, "
				 + "       cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os, "
				 + "       cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, "
				 + "         LEAST ( "
				 + "            DECODE (NVL (app_reapprove_amount, 0), "
				 + "                    0, app_approved_amount, "
				 + "                    app_reapprove_amount), "
				 + "            cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) "
				 + "       - cwd_npa_recovered_revise "
				 + "       + ctd_npa_recovered_revise "
				 + "          netos, "
				 + "       caa_applied_amount, "
				 + "       cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt, "
				 + "       cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt, "
				 + "       cwd_wc_asf_deductable + cwd_wc_asf_deductable, "
				 + "       DECODE ( "
				 + "          NVL (ctd_sf_refund_flag, 'N'), "
				 + "          'N', (  cwd_wc_first_inst_pay_amt "
				 + "                + ctd_tc_first_inst_pay_amt "
				 + "                - cwd_wc_asf_deductable "
				 + "                - ctd_tc_asf_deductable "
				 + "                - NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				 + "                - NVL (CWD_SERV_TAX_DED, 0) "
				 + "                - NVL (CWD_KK_CESS_TAX_DED, 0) "
				 + "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				 + "                - NVL (CTD_SERV_TAX_DED, 0) "
				 + "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				 + "          (  cwd_wc_first_inst_pay_amt "
				 + "           + ctd_tc_first_inst_pay_amt "
				 + "           + cwd_wc_asf_deductable "
				 + "           + ctd_tc_asf_deductable "
				 + "           + NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				 + "           + NVL (CWD_SERV_TAX_DED, 0) "
				 + "           + NVL (CWD_KK_CESS_TAX_DED, 0) "
				 + "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				 + "           + NVL (CTD_SERV_TAX_DED, 0) "
				 + "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				 + "          netpaid, "
				 + "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				 + "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				 + "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				 + "       CLM_PYMT_OUTWARD_DT, "
				 + "       clm_approved_dt, "
				 + "       clm_payment_dt, "
				 + "       DECODE (ctd_sf_refund_flag, "
				 + "               'Y', ctd_tc_sf_refundable + cwd_wc_sf_refundable, "
				 + "               0), "
				 + "       ctd_sf_refund_flag, "
				 + "       NVL (CTD_SWBH_CESS_TAX_DED, 0), "
				 + "       NVL (CTD_SERV_TAX_DED, 0), "
				 + "       NVL (CTD_KK_CESS_TAX_DED, 0) "
				 + "  FROM APPLICATION_DETAIL A, "
				 + "       SSI_DETAIL s, "
				 + "       CLAIM_DETAIL c, "
				 + "       CLAIM_WC_DETAIL cwd, "
				 + "       MEMBER_INFO m, "
				 + "       CLAIM_APPLICATION_AMOUNT caa, "
				 + "       CLAIM_TC_DETAIL ctd "
				 + " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				 + "       AND s.bid = c.bid "
				 + "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				 + "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				 + "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				 + "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				 + "       AND A.cgpan = caa.cgpan "
				 + "       AND A.cgpan = ctd.cgpan "
				 + "       AND A.cgpan = cwd.cgpan "
				 + "       AND app_loan_type IN ('CC') "
				 + "       AND clm_status IN ('AP') "
				 + "       AND TRUNC (clm_approved_dt) >= TO_DATE ('"+fromdate+"', 'DD/MM/YYYY') "
				 + "       AND TRUNC (clm_approved_dt) <= TO_DATE ('"+todate+"', 'DD/MM/YYYY') "
				 + "ORDER BY 1, 2, 5";*/
		    
		   // //System.out.println("query2"+query);
/* (new StringBuilder())
            .append(" SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \nA.cgpan,ssi_unit_name, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \nctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, \nLEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \ncaa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, \nctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable - nvl(CTD_SWBH_CESS_TAX_DED,0) - nvl(CTD_SERV_TAX_DED,0)-NVL(CTD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag,nvl(CTD_SWBH_CESS_TAX_DED,0),nvl(CTD_SERV_TAX_DED,0),NVL(CTD_KK_CESS_TAX_DED,0) \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \nWHERE A.ssi_reference_number = s.ssi_Reference_number \nAND s.bid = c.bid \nAND C.CLM_REF_NO = ctd.clm_ref_no \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND A.cgpan = caa.cgpan  \nAND   A.cgpan=ctd.cgpan  \nAND  app_loan_type IN ('TC') \nAND clm_status IN ('AP') \nAND TRUNC(")
            .append(clm_date_for_report)
            .append(") >=  to_date('")
            .append(fromdate)
            .append("','DD/MM/YYYY')    \n")
            .append("AND TRUNC(")
            .append(clm_date_for_report)
            .append(") <= to_date('")
            .append(todate)
            .append("','DD/MM/YYYY')     \n")
            .append("UNION ALL \n")
            .append("SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
            .append("A.cgpan,ssi_unit_name, \n")
            .append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
            .append("cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n")
            .append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n")
            .append("caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n")
            .append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable - nvl(CWD_SWBH_CESS_TAX_DED,0) - nvl(CWD_SERV_TAX_DED,0)-NVL(CWD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag,nvl(CWD_SWBH_CESS_TAX_DED,0),nvl(CWD_SERV_TAX_DED,0),NVL(CWD_KK_CESS_TAX_DED,0) \n")
            .append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n")
            .append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
            .append("AND s.bid = c.bid \n")
            .append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
            .append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
            .append("AND A.cgpan = caa.cgpan \n")
            .append("AND A.cgpan = cwd.cgpan \n")
            .append("AND app_loan_type IN ('WC') \n")
            .append("AND clm_status IN ('AP') \n")
            .append("AND TRUNC(")
            .append(clm_date_for_report)
            .append(") >= to_date('")
            .append(fromdate)
            .append("','DD/MM/YYYY')    \n")
            .append("AND TRUNC(")
            .append(clm_date_for_report)
            .append(") <= to_date('")
            .append(todate)
            .append("','DD/MM/YYYY')    \n")
            .append("UNION ALL \n")
            .append("SELECT distinct mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
            .append("A.cgpan,ssi_unit_name, \n")
            .append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
            .append("cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n")
            .append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n")
            .append("caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n")
            .append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable - nvl(CWD_SWBH_CESS_TAX_DED,0) - nvl(CWD_SERV_TAX_DED,0)-NVL(CWD_KK_CESS_TAX_DED,0) netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag,nvl(CTD_SWBH_CESS_TAX_DED,0),nvl(CTD_SERV_TAX_DED,0),NVL(CTD_KK_CESS_TAX_DED,0) \n")
            .append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n")
            .append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
            .append("AND s.bid = c.bid \n")
            .append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
            .append("AND C.CLM_REF_NO = ctd.clm_ref_no \n")
            .append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
            .append("AND A.cgpan = caa.cgpan \n")
            .append("AND A.cgpan = ctd.cgpan \n")
            .append("AND A.cgpan = cwd.cgpan \n")
            .append("AND app_loan_type IN ('CC') \n")
            .append("AND clm_status IN ('AP') \n").append("AND TRUNC(")
            .append(clm_date_for_report).append(") >= to_date('")
            .append(fromdate).append("','DD/MM/YYYY')    \n")
            .append("AND TRUNC(").append(clm_date_for_report)
            .append(") <= to_date('").append(todate)
            .append("','DD/MM/YYYY')   \n").append("ORDER BY 1,2,5 \n")
            .append("\n").toString();*/
			// New modified for swach bharat end here
		 //   //System.out.println("query2"+query);
	else 
			/*query = (new StringBuilder())
					.append(" SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \nA.cgpan,ssi_unit_name, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \nctd_npa_outstanding_amt_revise nps_os,ctd_npa_recovered_revise reco, \nLEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),ctd_npa_outstanding_amt_revise) - ctd_npa_recovered_revise netos, \ncaa_applied_amount,ctd_tc_clm_elig_amt,ctd_tc_first_inst_pay_amt,ctd_tc_asf_deductable, \nctd_tc_first_inst_pay_amt - ctd_tc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable,0),ctd_sf_refund_flag \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_TC_DETAIL ctd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \nWHERE A.ssi_reference_number = s.ssi_Reference_number \nAND s.bid = c.bid \nAND C.CLM_REF_NO = ctd.clm_ref_no \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND A.cgpan = caa.cgpan    \nAND  A.cgpan=ctd.cgpan \nAND app_loan_type IN ('TC') \nAND clm_status IN ('AP') \nAND TRUNC(")
					.append(clm_date_for_report)
					.append(") >=  to_date('")
					.append(fromdate)
					.append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')   and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
					.append(memberID)
					.append("'  \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt,cwd_wc_first_inst_pay_amt,cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(cwd_sf_refund_flag,'Y',cwd_wc_sf_refundable,0),cwd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('WC') \n")
					.append("AND clm_status IN ('AP') \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") >= to_date('")
					.append(fromdate)
					.append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')  and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
					.append(memberID)
					.append("'  \n")
					.append("UNION ALL \n")
					.append("SELECT mem_bank_name,mem_zone_name,c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id memberid, \n")
					.append("A.cgpan,ssi_unit_name, \n")
					.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) appamt, \n")
					.append("cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os,cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, \n")
					.append("LEAST(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount),cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) - cwd_npa_recovered_revise + ctd_npa_recovered_revise netos, \n")
					.append("caa_applied_amount,cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt,cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt,cwd_wc_asf_deductable + cwd_wc_asf_deductable, \n")
					.append("cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable + cwd_wc_first_inst_pay_amt - cwd_wc_asf_deductable netpaid,NVL(CLM_PAYMENT_UTR_NO,'-'),NVL(CLM_PAYMENT_AC_NO,'-'),NVL(CLM_PYMT_OUTWARD_NO,'-'),CLM_PYMT_OUTWARD_DT,clm_approved_dt,clm_payment_dt,decode(ctd_sf_refund_flag,'Y',ctd_tc_sf_refundable + cwd_wc_sf_refundable,0),ctd_sf_refund_flag \n")
					.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,CLAIM_DETAIL c,CLAIM_WC_DETAIL cwd,MEMBER_INFO m,CLAIM_APPLICATION_AMOUNT caa,CLAIM_TC_DETAIL ctd \n")
					.append("WHERE A.ssi_reference_number = s.ssi_Reference_number \n")
					.append("AND s.bid = c.bid \n")
					.append("AND C.CLM_REF_NO = cwd.clm_ref_no \n")
					.append("AND C.CLM_REF_NO = ctd.clm_ref_no \n")
					.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
					.append("AND A.cgpan = caa.cgpan \n")
					.append("AND A.cgpan = ctd.cgpan \n")
					.append("AND A.cgpan = cwd.cgpan \n")
					.append("AND app_loan_type IN ('CC') \n")
					.append("AND clm_status IN ('AP') \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") >= to_date('")
					.append(fromdate)
					.append("','DD/MM/YYYY')    \n")
					.append("AND TRUNC(")
					.append(clm_date_for_report)
					.append(") <= to_date('")
					.append(todate)
					.append("','DD/MM/YYYY')  and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='")
					.append(memberID).append("'  \n")
					.append("ORDER BY 1,2,5 \n").append("\n").toString();*/
			
			 query = "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       ctd_npa_outstanding_amt_revise nps_os, "
				+ "       ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            ctd_npa_outstanding_amt_revise) "
				+ "       - ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       ctd_tc_clm_elig_amt, "
				+ "       ctd_tc_first_inst_pay_amt, "
				+ "       ctd_tc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - NVL (ctd_tc_asf_deductable, 0) "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + NVL (ctd_tc_sf_refundable, 0) "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y', ctd_tc_sf_refundable, 0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_TC_DETAIL ctd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND app_loan_type IN ('TC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "   and  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='"+memberID+"'	"
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (cwd_sf_refund_flag, 'N'), "
				+ "          'N', (  cwd_wc_first_inst_pay_amt "
				+ "                - NVL (cwd_wc_asf_deductable, 0) "
				+ "                - NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CWD_SERV_TAX_DED, 0) "
				+ "                - NVL (CWD_KK_CESS_TAX_DED, 0)), "
				+ "          (  cwd_wc_first_inst_pay_amt "
				+ "           + NVL (cwd_wc_sf_refundable, 0) "
				+ "           + NVL (CWD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CWD_SERV_TAX_DED, 0) "
				+ "           + NVL (CWD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y', cwd_wc_sf_refundable, 0), "
				+ "       cwd_sf_refund_flag, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (cwd_sf_refund_flag, 'Y',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "        DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (cwd_sf_refund_flag, 'N',NVL (CWD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('WC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "   and  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='"+memberID+"'	"
				+ "UNION "
				+ "SELECT mem_bank_name, "
				+ "       mem_zone_name, "
				+ "       c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id memberid, "
				+ "       A.cgpan, "
				+ "       ssi_unit_name, "
				+ "       DECODE (NVL (app_reapprove_amount, 0), "
				+ "               0, app_approved_amount, "
				+ "               app_reapprove_amount) "
				+ "          appamt, "
				+ "       cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise nps_os, "
				+ "       cwd_npa_recovered_revise + ctd_npa_recovered_revise reco, "
				+ "         LEAST ( "
				+ "            DECODE (NVL (app_reapprove_amount, 0), "
				+ "                    0, app_approved_amount, "
				+ "                    app_reapprove_amount), "
				+ "            cwd_npa_outstanding_amt_revise + ctd_npa_outstanding_amt_revise) "
				+ "       - cwd_npa_recovered_revise "
				+ "       + ctd_npa_recovered_revise "
				+ "          netos, "
				+ "       caa_applied_amount, "
				+ "       cwd_wc_clm_elig_amt + ctd_tc_clm_elig_amt, "
				+ "       cwd_wc_first_inst_pay_amt + ctd_tc_first_inst_pay_amt, "
				+ "       cwd_wc_asf_deductable + cwd_wc_asf_deductable, "
				+ "       DECODE ( "
				+ "          NVL (ctd_sf_refund_flag, 'N'), "
				+ "          'N', (  ctd_tc_first_inst_pay_amt "
				+ "                - ctd_tc_asf_deductable "
				+ "                - NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "                - NVL (CTD_SERV_TAX_DED, 0) "
				+ "                - NVL (CTD_KK_CESS_TAX_DED, 0)), "
				+ "          (  ctd_tc_first_inst_pay_amt "
				+ "           + ctd_tc_asf_deductable "
				+ "           + NVL (CTD_SWBH_CESS_TAX_DED, 0) "
				+ "           + NVL (CTD_SERV_TAX_DED, 0) "
				+ "           + NVL (CTD_KK_CESS_TAX_DED, 0))) "
				+ "          netpaid, "
				+ "       NVL (CLM_PAYMENT_UTR_NO, '-'), "
				+ "       NVL (CLM_PAYMENT_AC_NO, '-'), "
				+ "       NVL (CLM_PYMT_OUTWARD_NO, '-'), "
				+ "       CLM_PYMT_OUTWARD_DT, "
				+ "       clm_approved_dt, "
				+ "       clm_payment_dt, "
				+ "       DECODE (ctd_sf_refund_flag, "
				+ "               'Y', ctd_tc_sf_refundable + cwd_wc_sf_refundable, "
				+ "               0), "
				+ "       ctd_sf_refund_flag, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'Y',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_REFUND, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SWBH_CESS_TAX_DED, 0), 0) SWBH_CESS_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_SERV_TAX_DED, 0), 0) SERV_TAX_DED, "
				+ "       DECODE (ctd_sf_refund_flag, 'N',NVL (CTD_KK_CESS_TAX_DED, 0), 0) KK_CESS_DED "
				+ "  FROM APPLICATION_DETAIL A, "
				+ "       SSI_DETAIL s, "
				+ "       CLAIM_DETAIL c, "
				+ "       CLAIM_WC_DETAIL cwd, "
				+ "       MEMBER_INFO m, "
				+ "       CLAIM_APPLICATION_AMOUNT caa, "
				+ "       CLAIM_TC_DETAIL ctd "
				+ " WHERE     A.ssi_reference_number = s.ssi_Reference_number "
				+ "       AND s.bid = c.bid "
				+ "       AND C.CLM_REF_NO = cwd.clm_ref_no "
				+ "       AND C.CLM_REF_NO = ctd.clm_ref_no "
				+ "       AND A.mem_bnk_id || A.mem_zne_id || A.mem_brn_id = "
				+ "              m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id "
				+ "       AND A.cgpan = caa.cgpan "
				+ "       AND A.cgpan = ctd.cgpan "
				+ "       AND A.cgpan = cwd.cgpan "
				+ "       AND app_loan_type IN ('CC') "
				+ "       AND clm_status IN ('AP') "
				+ "   and trunc("+clm_date_for_report+")  >=  to_date('"+fromdate+"','DD/MM/YYYY')"
				+ "   and trunc("+clm_date_for_report+")  <=  to_date('"+todate+"','DD/MM/YYYY')"
				+ "   and  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='"+memberID+"'	"
				+ "ORDER BY 1, 2, 5";
		//	//System.out.println("query3"+query);
			
		//System.out.println("query2"+query);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date clm_approved_date = null;
		Date clm_payment_date = null;
		String numberformat = "-?\\d+(\\.\\d+)?";
		String firstInstAmt = "0.0";
		String asfDeductedAmt = "0.0";
		String netPaidAmt = "0.0";
		Date clm_outward_date = null;
		String refundableAmt = "0.0";
		
		String swbhTaxDed = "0.0";
		String serTaxDed = "0.0";
		
		String serKrishiKalyan = "0.0";
		
		
		String serDedKrishiKalyan = "0.0";
		
		String swDedbhTaxDed = "0.0";
		String serDedTaxDed = "0.0";
		

		try {
			Statement pendingCaseDetailsStmt = null;
			ResultSet pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt = connection.createStatement();
			for (pendingCaseDetailsResult = pendingCaseDetailsStmt
					.executeQuery(query); pendingCaseDetailsResult.next(); PAArrayList
					.add(PAStringArray)) {
				//PAStringArray = new String[24];
				PAStringArray = new String[28];
				PAStringArray[0] = pendingCaseDetailsResult.getString(1);
				PAStringArray[1] = pendingCaseDetailsResult.getString(2);
				PAStringArray[2] = pendingCaseDetailsResult.getString(3);
				PAStringArray[3] = pendingCaseDetailsResult.getString(4);
				PAStringArray[4] = pendingCaseDetailsResult.getString(5);
				PAStringArray[5] = pendingCaseDetailsResult.getString(6);
				PAStringArray[6] = pendingCaseDetailsResult.getString(7);
				PAStringArray[7] = pendingCaseDetailsResult.getString(8);
				PAStringArray[8] = pendingCaseDetailsResult.getString(9);
				PAStringArray[9] = pendingCaseDetailsResult.getString(10);
				PAStringArray[10] = pendingCaseDetailsResult.getString(11);

				firstInstAmt = String.valueOf(pendingCaseDetailsResult
						.getString(12));
				asfDeductedAmt = String.valueOf(pendingCaseDetailsResult
						.getString(13));
				netPaidAmt = String.valueOf(pendingCaseDetailsResult
						.getString(14));

				PAStringArray[14] = pendingCaseDetailsResult.getString(15);
				PAStringArray[15] = pendingCaseDetailsResult.getString(16);
				PAStringArray[16] = pendingCaseDetailsResult.getString(17);
				clm_outward_date = pendingCaseDetailsResult.getDate(18);
				clm_approved_date = pendingCaseDetailsResult.getDate(19);
				clm_payment_date = pendingCaseDetailsResult.getDate(20);

				if (firstInstAmt.matches(numberformat))
					PAStringArray[11] = firstInstAmt;
				else
					PAStringArray[11] = "0.0";
				if (asfDeductedAmt.matches(numberformat))
					PAStringArray[12] = asfDeductedAmt;
				else
					PAStringArray[12] = "0.0";
				if (netPaidAmt.matches(numberformat))
					PAStringArray[13] = netPaidAmt;
				else
					PAStringArray[13] = "0.0";

				if (clm_outward_date != null)
					PAStringArray[17] = sdf.format(clm_outward_date);
				else
					PAStringArray[17] = "";

				if (clm_approved_date != null)
					PAStringArray[18] = sdf.format(clm_approved_date);
				else
					PAStringArray[18] = "";
				if (clm_payment_date != null)
					PAStringArray[19] = sdf.format(clm_payment_date);
				else
					PAStringArray[19] = "";

				refundableAmt = String.valueOf(pendingCaseDetailsResult
						.getString(21));

				if (refundableAmt.matches(numberformat))
					PAStringArray[20] = refundableAmt;
				else
					PAStringArray[20] = "0.0";

				PAStringArray[21] = pendingCaseDetailsResult.getString(22);
				
				swbhTaxDed = String.valueOf(pendingCaseDetailsResult
						.getString(23));
				PAStringArray[22]=swbhTaxDed;
				
				//if (swbhTaxDed.matches(numberformat))  
					
				//else
					//PAStringArray[22] = "0.0";
				
				serTaxDed = String.valueOf(pendingCaseDetailsResult
						.getString(24));
				PAStringArray[23]=serTaxDed;
				//if (serTaxDed.matches(numberformat))
				
			//else
				//PAStringArray[23] = "0.0";
				 
				//PAStringArray[23]= String.valueOf(pendingCaseDetailsResult
						//.getString(24));
				
				serKrishiKalyan= String.valueOf(pendingCaseDetailsResult
						.getString(25));
				PAStringArray[24]=serKrishiKalyan;
				
				
				serDedTaxDed= String.valueOf(pendingCaseDetailsResult
						.getString(26));
				PAStringArray[25]=serDedTaxDed;
				
				swDedbhTaxDed= String.valueOf(pendingCaseDetailsResult
						.getString(27));
				PAStringArray[26]=swDedbhTaxDed;

				serDedKrishiKalyan= String.valueOf(pendingCaseDetailsResult
						.getString(28));
				PAStringArray[27]=serDedKrishiKalyan;
			
			//	//System.out.println("serKrishiKalyan"+serKrishiKalyan);	 
			}
			pendingCaseDetailsResult.close();
			pendingCaseDetailsResult = null;
			pendingCaseDetailsStmt.close();
			pendingCaseDetailsStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			////System.out.println("e.getMessage()"+e.getMessage());
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
////System.out.println("arrayList"+PAArrayList.size());
		if (PAArrayList.size() == 0) {
			throw new NoDataException(
					"There are no Claim Ref Numbers that match the query.");
		}
		request.setAttribute("reportCondition", condition);
		request.setAttribute("pendingCaseDetailsArray", PAArrayList);
		request.setAttribute("pendingCaseDetailsArray_size", (new Integer(
				PAArrayList.size())).toString());
		Log.log(4, "NewReportsAction", "Claim settled", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward clmSettldReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "clmSettldReport", "Entered");
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
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("memberId", memberId);
		Log.log(4, "ReportsAction", "clmSettldReport", "Exited");
		return mapping.findForward("success");
	}
	
	///ADDED BY KOTESWAR FOR CLAIM PAYMENT REPORT
	
	public ActionForward clmPaymentReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "clmSettldReport", "Entered");
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
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		dynaForm.set("bankId", bankId);
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("memberId", memberId);
		Log.log(4, "ReportsAction", "clmSettldReport", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward approvalDateReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "disbursementReportInput", "Entered");
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
		generalReport.setDateOfTheDocument18(prevDate);
		generalReport.setDateOfTheDocument19(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		dynaForm.set("bankId", bankId);
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("memberId", memberId);
		Log.log(4, "ReportsAction", "disbursementReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward dateWiseSanctionList(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "disbursementReportInput", "Entered");
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
		generalReport.setDateOfTheDocument18(prevDate);
		generalReport.setDateOfTheDocument19(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		User user = getUserInformation(request);
		String bankId = user.getBankId();
		String zoneId = user.getZoneId();
		String branchId = user.getBranchId();
		String memberId = bankId.concat(zoneId).concat(branchId);
		dynaForm.set("bankId", bankId);
		if (bankId.equals("0000"))
			memberId = "";
		dynaForm.set("memberId", memberId);
		Log.log(4, "ReportsAction", "disbursementReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward approvalDateWise(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", "Claim settled", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		String PAStringArray[] = null;
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument18");
		Date todate = (Date) dynaForm.get("dateOfTheDocument19");
		String memberID = (String) dynaForm.get("memberId");
		request.setAttribute("fromdate", fromdate);
		request.setAttribute("todate", todate);
		String stDate = String.valueOf(fromdate);
		String todateNew = String.valueOf(todate);
		if (memberID.equals(null) || memberID.equals(""))
			throw new NoDataException(
					"Member Id shold not be empty, Please Enter Member Id ");
		if (stDate.equals(null) || stDate.equals(""))
			throw new NoDataException(
					"From Date shold not be empty, Please Enter from Date ");
		if (todateNew.equals(null) || todateNew.equals(""))
			throw new NoDataException(
					"To Date shold not be empty, Please Enter To Date ");
		if (!memberID.equals("")) {
			if (memberID.length() < 12)
				throw new NoDataException(
						"Member ID can not be less than 12 characters, Please Enter 12 Digit Member ID ");
			if (!memberids.contains(memberID)) {
				Log.log(2, "CPDAO", "getAllMemberIds()",
						"No Member Ids in the database!");
				throw new DatabaseException("Please Enter Valid Member Id");
			}
		}
		String id = "";
		if (memberID != null && !memberID.equals("")) {
			String bankId = memberID.substring(0, 4);
			String zoneId = memberID.substring(4, 8);
			String bnkZneId = bankId.concat(zoneId);
			String branchId = memberID.substring(8, 12);
			if (!bankId.equals("0000") && zoneId.equals("0000")
					&& branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,ssi_unit_name, \n  DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n  to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm \n WHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.scm_id = sm.scm_id \n AND A.MEM_BNK_ID='")
						.append(bankId)
						.append("'  \n")
						.append("AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) <= TO_DATE('")
						.append(todate).append("','DD/MM/YYYY')\n")
						.append("AND APP_STATUS NOT IN ('RE') \n")
						.append(" ORDER BY 1,2 \n").toString();
			else if (!bankId.equals("0000") && !zoneId.equals("0000")
					&& branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,ssi_unit_name, \n  DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n  to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm \n WHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.scm_id = sm.scm_id \n AND A.MEM_BNK_ID||A.MEM_ZNE_ID='")
						.append(bnkZneId)
						.append("'  \n")
						.append("AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) <= TO_DATE('")
						.append(todate).append("','DD/MM/YYYY')\n")
						.append("AND APP_STATUS NOT IN ('RE') \n")
						.append(" ORDER BY 1,2 \n").toString();
			else if (!bankId.equals("0000") && !zoneId.equals("0000")
					&& !branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,ssi_unit_name, \n  DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n  to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm \n WHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.scm_id = sm.scm_id \n AND A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID='")
						.append(memberID)
						.append("'  \n")
						.append("AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) <= TO_DATE('")
						.append(todate).append("','DD/MM/YYYY')\n")
						.append("AND APP_STATUS NOT IN ('RE') \n")
						.append(" ORDER BY 1,2 \n").toString();
		} else {
			query = (new StringBuilder())
					.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,ssi_unit_name, \n  DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n  to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm \n WHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.scm_id = sm.scm_id \nAND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) >= TO_DATE('")
					.append(fromdate)
					.append("','DD/MM/YYYY')\n")
					.append(" AND TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)) <= TO_DATE('")
					.append(todate).append("','DD/MM/YYYY')\n")
					.append("AND APP_STATUS NOT IN ('RE') \n")
					.append(" ORDER BY 1,2 \n").toString();
		}
		String DateWiseAppArray[] = null;
		ArrayList DateWiseAppArrayList = new ArrayList();
		java.sql.Date startDate = new java.sql.Date(fromdate.getTime());
		java.sql.Date endDate = new java.sql.Date(todate.getTime());
		try {
			Statement dateWiseappStmt = null;
			ResultSet DateWiseResult = null;
			dateWiseappStmt = connection.createStatement();
			for (DateWiseResult = dateWiseappStmt.executeQuery(query); DateWiseResult
					.next(); DateWiseAppArrayList.add(DateWiseAppArray)) {
				DateWiseAppArray = new String[10];
				DateWiseAppArray[0] = DateWiseResult.getString(1);
				DateWiseAppArray[1] = DateWiseResult.getString(2);
				DateWiseAppArray[2] = DateWiseResult.getString(3);
				DateWiseAppArray[3] = DateWiseResult.getString(4);
				DateWiseAppArray[4] = DateWiseResult.getString(5);
				DateWiseAppArray[5] = DateWiseResult.getString(6);
				DateWiseAppArray[6] = DateWiseResult.getString(7);
				DateWiseAppArray[7] = DateWiseResult.getString(8);
				DateWiseAppArray[8] = DateWiseResult.getString(9);
			}

			DateWiseResult.close();
			DateWiseResult = null;
			dateWiseappStmt.close();
			dateWiseappStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("DateWiseAppArrayList", DateWiseAppArrayList);
		request.setAttribute("DateWiseAppArrayListSize", (new Integer(
				DateWiseAppArrayList.size())).toString());
		Log.log(4, "NewReportsAction", "Claim settled", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward sanctionDateWise(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		Log.log(4, "NewReportsAction", " sanctionDateWise", "Entered");
		HttpSession session = request.getSession();
		Connection connection = DBConnection.getConnection();
		String query = "";
		String PAStringArray[] = null;
		ClaimsProcessor claimsProcessor = new ClaimsProcessor();
		Vector memberids = new Vector();
		memberids = claimsProcessor.getAllMemberIds();
		Date fromdate = (Date) dynaForm.get("dateOfTheDocument18");
		Date todate = (Date) dynaForm.get("dateOfTheDocument19");
		String memberID = (String) dynaForm.get("memberId");
		request.setAttribute("fromdate", fromdate);
		request.setAttribute("todate", todate);
		String stDate = String.valueOf(fromdate);
		String todateNew = String.valueOf(todate);
		if (memberID.equals(null) || memberID.equals(""))
			throw new NoDataException(
					"Member Id shold not be empty, Please Enter Member Id ");
		if (stDate.equals(null) || stDate.equals(""))
			throw new NoDataException(
					"From Date shold not be empty, Please Enter from Date ");
		if (todateNew.equals(null) || todateNew.equals(""))
			throw new NoDataException(
					"To Date shold not be empty, Please Enter To Date ");
		if (!memberID.equals("")) {
			if (memberID.length() < 12)
				throw new NoDataException(
						"Member ID can not be less than 12 characters, Please Enter 12 Digit Member ID ");
			if (!memberids.contains(memberID)) {
				Log.log(2, "CPDAO", "getAllMemberIds()",
						"No Member Ids in the database!");
				throw new DatabaseException("Please Enter Valid Member Id");
			}
		}
		String id = "";
		if (memberID != null && !memberID.equals("")) {
			String bankId = memberID.substring(0, 4);
			String zoneId = memberID.substring(4, 8);
			String bnkZneId = bankId.concat(zoneId);
			String branchId = memberID.substring(8, 12);
			if (!bankId.equals("0000") && zoneId.equals("0000")
					&& branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp, \nssi_unit_name,to_char(trm_amount_sanctioned_dt,'dd/mm/yyyy') sancdt, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \nto_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,TERM_LOAN_DETAIL t,MEMBER_INFO m \nWHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.app_ref_no = t.app_ref_no \nAND A.scm_id = sm.scm_id \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND trm_amount_sanctioned_dt >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND trm_amount_sanctioned_dt <=  TO_DATE('")
						.append(todate)
						.append("','DD/MM/YYYY')\n")
						.append("and a.mem_bnk_id = '")
						.append(bankId)
						.append("'  \n")
						.append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('TC','CC') \n")
						.append("UNION ALL \n")
						.append("SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp,ssi_unit_name, \n")
						.append("to_char(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt),'dd/mm/yyyy') sancdt, \n")
						.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n")
						.append("to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n")
						.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,WORKING_CAPITAL_DETAIL w,MEMBER_INFO m \n")
						.append("WHERE A.ssi_reference_number = s.ssi_reference_number \n")
						.append("AND A.app_ref_no = w.app_ref_no \n")
						.append("AND A.scm_id = sm.scm_id \n")
						.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) <= TO_DATE('")
						.append(todate).append("','DD/MM/YYYY')\n")
						.append("and a.mem_bnk_id = '").append(bankId)
						.append("'  \n").append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('WC') \n")
						.append("ORDER BY 3,4,5,8 \n").append("\n  ")
						.toString();
			else if (!bankId.equals("0000") && !zoneId.equals("0000")
					&& branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp, \nssi_unit_name,to_char(trm_amount_sanctioned_dt,'dd/mm/yyyy') sancdt, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \nto_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,TERM_LOAN_DETAIL t,MEMBER_INFO m \nWHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.app_ref_no = t.app_ref_no \nAND A.scm_id = sm.scm_id \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND trm_amount_sanctioned_dt >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND trm_amount_sanctioned_dt <=  TO_DATE('")
						.append(todate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND A.MEM_BNK_ID||A.MEM_ZNE_ID='")
						.append(bnkZneId)
						.append("'  \n")
						.append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('TC','CC') \n")
						.append("UNION ALL \n")
						.append("SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp,ssi_unit_name, \n")
						.append("to_char(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt),'dd/mm/yyyy') sancdt, \n")
						.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n")
						.append("to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n")
						.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,WORKING_CAPITAL_DETAIL w,MEMBER_INFO m \n")
						.append("WHERE A.ssi_reference_number = s.ssi_reference_number \n")
						.append("AND A.app_ref_no = w.app_ref_no \n")
						.append("AND A.scm_id = sm.scm_id \n")
						.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) <= TO_DATE('")
						.append(todate).append("','DD/MM/YYYY')\n")
						.append(" AND A.MEM_BNK_ID||A.MEM_ZNE_ID='")
						.append(bnkZneId).append("'  \n")
						.append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('WC') \n")
						.append("ORDER BY 3,4,5,8 \n").append("\n  ")
						.toString();
			else if (!bankId.equals("0000") && !zoneId.equals("0000")
					&& !branchId.equals("0000"))
				query = (new StringBuilder())
						.append(" SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp, \nssi_unit_name,to_char(trm_amount_sanctioned_dt,'dd/mm/yyyy') sancdt, \nDECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \nto_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \nFROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,TERM_LOAN_DETAIL t,MEMBER_INFO m \nWHERE A.ssi_reference_number = s.ssi_reference_number \nAND A.app_ref_no = t.app_ref_no \nAND A.scm_id = sm.scm_id \nAND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \nAND trm_amount_sanctioned_dt >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND trm_amount_sanctioned_dt <=  TO_DATE('")
						.append(todate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID='")
						.append(memberID)
						.append("'  \n")
						.append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('TC','CC') \n")
						.append("UNION ALL \n")
						.append("SELECT A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id memberid,scm_name,mem_bank_name,mem_zone_name,app_mli_branch_name branch,app_bank_app_ref_no refnumber,A.cgpan,app_loan_type ltyp,ssi_unit_name, \n")
						.append("to_char(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt),'dd/mm/yyyy') sancdt, \n")
						.append("DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) approvedamount, \n")
						.append("to_char(TRUNC(DECODE(app_reapprove_dt,NULL,app_approved_date_time,app_reapprove_dt)),'dd/mm/yyyy') approveddate,app_status st \n")
						.append("FROM APPLICATION_DETAIL A,SSI_DETAIL s,SCHEME_MASTER sm,WORKING_CAPITAL_DETAIL w,MEMBER_INFO m \n")
						.append("WHERE A.ssi_reference_number = s.ssi_reference_number \n")
						.append("AND A.app_ref_no = w.app_ref_no \n")
						.append("AND A.scm_id = sm.scm_id \n")
						.append("AND A.mem_bnk_id||A.mem_zne_id||A.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id \n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) >= TO_DATE('")
						.append(fromdate)
						.append("','DD/MM/YYYY')\n")
						.append("AND TRUNC(DECODE(wcp_fb_limit_sanctioned_dt,NULL,wcp_nfb_limit_sanctioned_dt,wcp_fb_limit_sanctioned_dt)) <= TO_DATE('")
						.append(todate)
						.append("','DD/MM/YYYY')\n")
						.append(" AND A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID='")
						.append(memberID).append("'  \n")
						.append("AND APP_STATUS IN ('AP') \n")
						.append("AND app_loan_type IN ('WC') \n")
						.append("ORDER BY 3,4,5,8 \n").append("\n  ")
						.toString();
		}
		String DateWiseSanArray[] = null;
		ArrayList DateWiseSanArrayList = new ArrayList();
		java.sql.Date startDate = new java.sql.Date(fromdate.getTime());
		java.sql.Date endDate = new java.sql.Date(todate.getTime());
		try {
			Statement dateWiseappStmt = null;
			ResultSet DateWiseResult = null;
			dateWiseappStmt = connection.createStatement();
			for (DateWiseResult = dateWiseappStmt.executeQuery(query); DateWiseResult
					.next(); DateWiseSanArrayList.add(DateWiseSanArray)) {
				DateWiseSanArray = new String[13];
				DateWiseSanArray[0] = DateWiseResult.getString(1);
				DateWiseSanArray[1] = DateWiseResult.getString(2);
				DateWiseSanArray[2] = DateWiseResult.getString(3);
				DateWiseSanArray[3] = DateWiseResult.getString(4);
				DateWiseSanArray[4] = DateWiseResult.getString(5);
				DateWiseSanArray[5] = DateWiseResult.getString(6);
				DateWiseSanArray[6] = DateWiseResult.getString(7);
				DateWiseSanArray[7] = DateWiseResult.getString(8);
				DateWiseSanArray[8] = DateWiseResult.getString(9);
				DateWiseSanArray[9] = DateWiseResult.getString(10);
				DateWiseSanArray[10] = DateWiseResult.getString(11);
				DateWiseSanArray[11] = DateWiseResult.getString(12);
				DateWiseSanArray[12] = DateWiseResult.getString(13);
			}

			DateWiseResult.close();
			DateWiseResult = null;
			dateWiseappStmt.close();
			dateWiseappStmt = null;
		} catch (Exception e) {
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		DBConnection.freeConnection(connection);
		request.setAttribute("DateWiseSanArrayList", DateWiseSanArrayList);
		request.setAttribute("DateWiseSanArrayListSize", (new Integer(
				DateWiseSanArrayList.size())).toString());
		Log.log(4, "NewReportsAction", "Claim settled", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward inwardReportNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
	//	//System.out.println("inwardReportNew");
		Log.log(4, "ReportsAction", "inwardReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		 java.sql.Date startDate = null;
	      java.sql.Date endDate = null;
		
		 
		Date sDate = (Date) dynaForm.get("dateOfTheDocument26");
		 
		 SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	        Date parsedStartDate = format.parse(dynaForm.get("dateOfTheDocument26").toString());
	        java.sql.Date sqlStartDate = new java.sql.Date(parsedStartDate.getTime());
	        
	        Date parsedendDate = format.parse(dynaForm.get("dateOfTheDocument27").toString());
	        java.sql.Date sqlEndDate = new java.sql.Date(parsedendDate.getTime());
	        
	      //  //System.out.println("sql date "+sqlStartDate);  
		String stDate = String.valueOf(sDate);
		
		PreparedStatement danRaisedStmt = null;
		
		ArrayList inwardRaisedArray = new ArrayList();
		ResultSet danRaisedResult = null;
		Connection connection = DBConnection.getConnection();
		String condition = request.getParameter("condition");
		String dateForReport = null;
	 
		 
		if ("inwardDateWiseReport".equals(condition))
			dateForReport = "INW_DT";
		else if ("ltrDateWiseReport".equals(condition))
			dateForReport = "LTR_DT";
		if (stDate == null || stDate.equals(""))
			startDate = null;
		else if (stDate != null)        
		startDate = new java.sql.Date(sDate.getTime());
		startDate=sqlStartDate;
		Date eDate = (Date) dynaForm.get("dateOfTheDocument27");
	    endDate = new java.sql.Date(eDate.getTime());
		endDate =sqlEndDate;
		if (startDate != null)
			try {
				String query = " SELECT INW_DT,INW_ID,INWARD_SEC,BANK_NAME,PLACE,LTR_REF_NO,LTR_DT,SUBJECT,INSTRUMENT_NO,"
						+ " INSTRUMENT_DT,INSTRUMENT_AMT,DRAWN_ON_BANK,OUTWARD_ID,OUTWARD_DT,ASSIGNED_USER,INW_CTS_TYPE FROM INWARD_NEW   "
						+ " WHERE trunc("
						+ dateForReport
						+ ")  between ? and ? ORDER BY 1,2,3,INW_SEQ ";
				// //System.out.println(" WHERE trunc(" + dateForReport);
				danRaisedStmt = connection.prepareStatement(query);
				danRaisedStmt.setDate(1, startDate);
				danRaisedStmt.setDate(2, endDate);
				GeneralReport generalReport;
				for (danRaisedResult = danRaisedStmt.executeQuery(); danRaisedResult
						.next(); inwardRaisedArray.add(generalReport)) {
					generalReport = new GeneralReport();
					generalReport.setDateOfTheDocument1(danRaisedResult
							.getDate(1));
					generalReport.setInwardNum(danRaisedResult.getInt(2));
					generalReport
							.setInwardSection(danRaisedResult.getString(3));
					generalReport.setBankName(danRaisedResult.getString(4));
					generalReport.setPlace(danRaisedResult.getString(5));
					generalReport.setLtrRefNo(danRaisedResult.getString(6));
					generalReport.setDateOfTheDocument10(danRaisedResult
							.getDate(7));
					generalReport.setSubject(danRaisedResult.getString(8));
					generalReport.setInstrumentNum(danRaisedResult.getInt(9));
					generalReport.setDateOfTheDocument11(danRaisedResult
							.getDate(10));
					generalReport.setInstrumentAmt(danRaisedResult
							.getDouble(11));
					generalReport.setDrawnonBank(danRaisedResult.getString(12));
					generalReport.setAppRefNo(danRaisedResult.getString(13));
					generalReport.setDateOfTheDocument12(danRaisedResult
							.getDate(14));
					generalReport.setName(danRaisedResult.getString(15));
					generalReport.setTxnType(danRaisedResult.getString(16));
				}

				danRaisedResult.close();
				danRaisedResult = null;
				danRaisedStmt.close();
				danRaisedStmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		else if (startDate == null)
			try {
				String query = " SELECT INW_DT,INW_ID,INWARD_SEC,BANK_NAME,PLACE,LTR_REF_NO,LTR_DT,SUBJECT,INSTRUMENT_NO,"
						+ " INSTRUMENT_DT,INSTRUMENT_AMT,DRAWN_ON_BANK,OUTWARD_ID,OUTWARD_DT,ASSIGNED_USER,INW_CTS_TYPE FROM INWARD_NEW  "
						+ " WHERE trunc("
						+ dateForReport
						+ ")  <= ? ORDER BY 1,2,3,INW_SEQ ";
				// //System.out.println(" WHERE trunc(" + dateForReport);
				danRaisedStmt = connection.prepareStatement(query);
				danRaisedStmt.setDate(1, endDate);
				GeneralReport generalReport;
				for (danRaisedResult = danRaisedStmt.executeQuery(); danRaisedResult
						.next(); inwardRaisedArray.add(generalReport)) {
					generalReport = new GeneralReport();
					generalReport.setDateOfTheDocument1(danRaisedResult
							.getDate(1));
					generalReport.setInwardNum(danRaisedResult.getInt(2));
					generalReport
							.setInwardSection(danRaisedResult.getString(3));
					generalReport.setBankName(danRaisedResult.getString(4));
					generalReport.setPlace(danRaisedResult.getString(5));
					generalReport.setLtrRefNo(danRaisedResult.getString(6));
					generalReport.setDateOfTheDocument10(danRaisedResult
							.getDate(7));
					generalReport.setSubject(danRaisedResult.getString(8));
					generalReport.setInstrumentNum(danRaisedResult.getInt(9));
					generalReport.setDateOfTheDocument11(danRaisedResult
							.getDate(10));
					generalReport.setInstrumentAmt(danRaisedResult
							.getDouble(11));
					generalReport.setDrawnonBank(danRaisedResult.getString(12));
					generalReport.setAppRefNo(danRaisedResult.getString(13));
					generalReport.setDateOfTheDocument12(danRaisedResult
							.getDate(14));
					generalReport.setName(danRaisedResult.getString(15));
					generalReport.setTxnType(danRaisedResult.getString(16));
				}

				danRaisedResult.close();
				danRaisedResult = null;
				danRaisedStmt.close();
				danRaisedStmt = null;
			} catch (Exception exception) {
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		dynaForm.set("danRaised", inwardRaisedArray);
		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", inwardRaisedArray);
		if (inwardRaisedArray == null || inwardRaisedArray.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered, Please Enter Any Other Value ");
		} else {
			inwardRaisedArray = null;
			return mapping.findForward("success");
		}
	}

	public ActionForward correspondenceReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "correspondenceReportInput", "Entered");
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
		generalReport.setDateOfTheDocument26(prevDate);
		generalReport.setDateOfTheDocument27(date);
		BeanUtils.copyProperties(dynaForm, generalReport);
		Log.log(4, "ReportsAction", "correspondenceReportInput", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward correspondenceReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(4, "ReportsAction", "inwardReport", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		java.sql.Date startDate = null;
		java.sql.Date endDate = null;
		Date sDate = (Date) dynaForm.get("dateOfTheDocument26");
		String stDate = String.valueOf(sDate);
		PreparedStatement danRaisedStmt = null;
		ArrayList inwardRaisedArray = new ArrayList();
		ResultSet danRaisedResult = null;
		Connection connection = DBConnection.getConnection();
		String condition = request.getParameter("condition");
		String dateForReport = null;
		if ("inwardDateWiseReport".equals(condition))
			dateForReport = "INW_DT";
		else if ("ltrDateWiseReport".equals(condition))
			dateForReport = "LTR_DT";
		if (stDate == null || stDate.equals(""))
			startDate = null;
		else if (stDate != null)
			startDate = new java.sql.Date(sDate.getTime());
		Date eDate = (Date) dynaForm.get("dateOfTheDocument27");
		endDate = new java.sql.Date(eDate.getTime());
		if (startDate != null)
			try {
				String query = " SELECT INW_DT,INW_ID,INWARD_SEC,BANK_NAME,   PLACE,LTR_REF_NO,LTR_DT,SUBJECT,INSTRUMENT_NO,    INSTRUMENT_DT,INSTRUMENT_AMT,DRAWN_ON_BANK,OUTWARD_ID,OUTWARD_DT,ASSIGNED_USER FROM INWARD_CORR   WHERE trunc("
						+ dateForReport
						+ ")  between ? and ? ORDER BY 1,2,3,INW_SEQ ";
				// //System.out.println(" WHERE trunc(" + dateForReport);
				danRaisedStmt = connection.prepareStatement(query);
				danRaisedStmt.setDate(1, startDate);
				danRaisedStmt.setDate(2, endDate);
				GeneralReport generalReport;
				for (danRaisedResult = danRaisedStmt.executeQuery(); danRaisedResult
						.next(); inwardRaisedArray.add(generalReport)) {
					generalReport = new GeneralReport();
					generalReport.setDateOfTheDocument1(danRaisedResult
							.getDate(1));
					generalReport
							.setInwardNumCorr(danRaisedResult.getString(2));
					generalReport
							.setInwardSection(danRaisedResult.getString(3));
					generalReport.setBankName(danRaisedResult.getString(4));
					generalReport.setPlace(danRaisedResult.getString(5));
					generalReport.setLtrRefNo(danRaisedResult.getString(6));
					generalReport.setDateOfTheDocument10(danRaisedResult
							.getDate(7));
					generalReport.setSubject(danRaisedResult.getString(8));
					generalReport.setInstrumentNum(danRaisedResult.getInt(9));
					generalReport.setDateOfTheDocument11(danRaisedResult
							.getDate(10));
					generalReport.setInstrumentAmt(danRaisedResult
							.getDouble(11));
					generalReport.setDrawnonBank(danRaisedResult.getString(12));
					generalReport.setAppRefNo(danRaisedResult.getString(13));
					generalReport.setDateOfTheDocument12(danRaisedResult
							.getDate(14));
					generalReport.setName(danRaisedResult.getString(15));
				}

				danRaisedResult.close();
				danRaisedResult = null;
				danRaisedStmt.close();
				danRaisedStmt = null;
			} catch (Exception exception) {
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		else if (startDate == null)
			try {
				String query = " SELECT INW_DT,INW_ID,INWARD_SEC,BANK_NAME,    PLACE,LTR_REF_NO,LTR_DT,SUBJECT,INSTRUMENT_NO,   INSTRUMENT_DT,INSTRUMENT_AMT,DRAWN_ON_BANK,OUTWARD_ID,OUTWARD_DT,ASSIGNED_USER FROM INWARD_CORR  WHERE trunc("
						+ dateForReport + ")  <= ? ORDER BY 1,2,3,INW_SEQ ";
				// //System.out.println(" WHERE trunc(" + dateForReport);
				danRaisedStmt = connection.prepareStatement(query);
				danRaisedStmt.setDate(1, endDate);
				GeneralReport generalReport;
				for (danRaisedResult = danRaisedStmt.executeQuery(); danRaisedResult
						.next(); inwardRaisedArray.add(generalReport)) {
					generalReport = new GeneralReport();
					generalReport.setDateOfTheDocument1(danRaisedResult
							.getDate(1));
					generalReport
							.setInwardNumCorr(danRaisedResult.getString(2));
					generalReport
							.setInwardSection(danRaisedResult.getString(3));
					generalReport.setBankName(danRaisedResult.getString(4));
					generalReport.setPlace(danRaisedResult.getString(5));
					generalReport.setLtrRefNo(danRaisedResult.getString(6));
					generalReport.setDateOfTheDocument10(danRaisedResult
							.getDate(7));
					generalReport.setSubject(danRaisedResult.getString(8));
					generalReport.setInstrumentNum(danRaisedResult.getInt(9));
					generalReport.setDateOfTheDocument11(danRaisedResult
							.getDate(10));
					generalReport.setInstrumentAmt(danRaisedResult
							.getDouble(11));
					generalReport.setDrawnonBank(danRaisedResult.getString(12));
					generalReport.setAppRefNo(danRaisedResult.getString(13));
					generalReport.setDateOfTheDocument12(danRaisedResult
							.getDate(14));
					generalReport.setName(danRaisedResult.getString(15));
				}

				danRaisedResult.close();
				danRaisedResult = null;
				danRaisedStmt.close();
				danRaisedStmt = null;
			} catch (Exception exception) {
				throw new DatabaseException(exception.getMessage());
			} finally {
				DBConnection.freeConnection(connection);
			}
		dynaForm.set("danRaised", inwardRaisedArray);
		HttpSession session = request.getSession(true);
		session.setAttribute("danRaised", inwardRaisedArray);
		if (inwardRaisedArray == null || inwardRaisedArray.size() == 0) {
			throw new NoDataException(
					"No Data is available for the values entered, Please Enter Any Other Value ");
		} else {
			inwardRaisedArray = null;
			Log.log(4, "ReportsAction", "inwardReport", "Exited");
			return mapping.findForward("success");
		}
	}

	// added by upchar@path on 03/07/2013

	public ActionForward rrQueryClaimDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "NewReportsAction", "rrQueryClaimDetail", "Entered");
		DynaActionForm dynaForm = (DynaActionForm) form;
		dynaForm.initialize(mapping);
		Log.log(Log.INFO, "NewReportsAction", "rrQueryClaimDetail", "Exited");
		return mapping.findForward("success");
	}

	public ActionForward displayRRQueryDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Log.log(Log.INFO, "NewReportsAction", "displayRRQueryDetail", "Entered");
		ReportManager manager = new ReportManager();
		ClaimActionForm claimForm = (ClaimActionForm) form;

		// String clmApplicationStatus = claimForm.getClmApplicationStatus();
		String clmApplicationStatus = "";
		Log.log(Log.INFO, "NewReportsAction", "displayRRQueryDetail",
				"Claim Application Status being queried ");

		// Retrieving the Member Id from the User Object
		User user = getUserInformation(request);
		String bankid = (user.getBankId()).trim();
		String zoneid = (user.getZoneId()).trim();
		String branchid = (user.getBranchId()).trim();
		String memberId = bankid + zoneid + branchid;
		String cgpanno = null;
		String claimRefNumber = null;
		cgpanno = (String) request.getParameter("cgpan");
		claimRefNumber = (String) request.getParameter("clmRefNumber");

		if (claimRefNumber != null && claimRefNumber != "") {
			claimRefNumber = claimRefNumber.trim();
			claimRefNumber = claimRefNumber.toUpperCase();
		}
		if (cgpanno != null && !cgpanno.equals("")) {
			cgpanno = cgpanno.trim();
			cgpanno = cgpanno.toUpperCase();
		}

		if (cgpanno != null && !cgpanno.equals("")) {
			PreparedStatement pstmt = null;
			ResultSet rst = null;
			String clmRefNo = "";
			Connection connection = DBConnection.getConnection();
			try {

				String query = "select distinct clm_ref_no,clm_status \n"
						+ "from claim_detail_temp ct,application_detail a,ssi_detail s\n"
						+ "where ct.bid = s.bid\n"
						+ "and s.ssi_reference_number = a.ssi_reference_number\n"
						+ "and a.cgpan = ?\n"
						+ "union all\n"
						+ "select distinct clm_ref_no,clm_status \n"
						+ "from claim_detail c,application_detail a,ssi_detail s\n"
						+ "where c.bid = s.bid\n"
						+ "and s.ssi_reference_number = a.ssi_reference_number\n"
						+ "and a.cgpan = ? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, cgpanno);
				pstmt.setString(2, cgpanno);
				rst = pstmt.executeQuery();
				while (rst.next()) {
					claimRefNumber = rst.getString(1);
					clmApplicationStatus = rst.getString(2);
				}
				if (clmApplicationStatus.equals("")) {
					throw new DatabaseException("Enter a valid cgpan.");
				}
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
			// claimRefNumber = (String)request.getParameter("clmRefNumber");

			PreparedStatement pstmt = null;
			ResultSet rst = null;
			String clmRefNo = "";
			Connection connection = DBConnection.getConnection();
			try {

				String query = "select distinct clm_status \n"
						+ "from claim_detail_temp ct,application_detail a,ssi_detail s\n"
						+ "where ct.bid = s.bid\n"
						+ "and s.ssi_reference_number = a.ssi_reference_number\n"
						+ "and ct.clm_ref_no = ?\n"
						+ "union all\n"
						+ "select distinct clm_status \n"
						+ "from claim_detail c,application_detail a,ssi_detail s\n"
						+ "where c.bid = s.bid\n"
						+ "and s.ssi_reference_number = a.ssi_reference_number\n"
						+ "and c.clm_ref_no = ? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, claimRefNumber);
				pstmt.setString(2, claimRefNumber);
				rst = pstmt.executeQuery();
				while (rst.next()) {

					clmApplicationStatus = rst.getString(1);

				}

				if (clmApplicationStatus.equals("")) {
					throw new DatabaseException("Enter a valid Claim Ref No.");
				}
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

		if (!clmApplicationStatus.equals("RR")
				&& !(clmApplicationStatus.equals(""))) {

			throw new DatabaseException(
					"Please enter correct details. Claim should in RR Status");
		}
		Connection connection = DBConnection.getConnection(false);
		ArrayList rrList = new ArrayList();
		ArrayList rrQueryList = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ResultSet result = null;
		try {
			CallableStatement callable = connection
					.prepareCall("{?=call Packgetclmreplydetails.funcGetClmReplyDetails(?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER); // error status
			callable.setString(2, cgpanno);
			callable.setString(3, claimRefNumber);
			callable.registerOutParameter(4, Constants.CURSOR);
			callable.registerOutParameter(5, Types.VARCHAR); // error message
			callable.execute();
			int errorCode = callable.getInt(1);
			String error = callable.getString(5);
			Log.log(Log.INFO, "ReportAction", "displayRRQueryDetail",
					"Error code and error are " + errorCode + " " + error);

			// If error status is 1 throw database exception.
			if (errorCode == Constants.FUNCTION_FAILURE) {

				// connection.rollback();
				callable.close();
				callable = null;
				throw new DatabaseException(error);
			} else {
				Date inwdt = null;
				;

				result = (ResultSet) callable.getObject(4);
				while (result.next()) {

					String str[] = new String[5];
					str[0] = result.getString(1);// cgpan
					str[1] = result.getString(2);// claim ref no
					str[2] = result.getString(3);// inward no
					inwdt = result.getDate(4);// inward date
					if (inwdt != null) {
						str[3] = sdf.format(inwdt);
					} else {
						str[3] = "";
					}

					rrList.add(str);
				}
				claimForm.setDanSummary(rrList);
				result.close();
				result = null;
			}
			callable.close();
			callable = null;
			// connection.commit();

			callable = connection
					.prepareCall("{?=call Packgetclmreplydetails.funcgetclmqrydetails(?,?,?)}");
			callable.registerOutParameter(1, Types.INTEGER);// error status
			callable.setString(2, claimRefNumber);
			callable.registerOutParameter(3, Constants.CURSOR);
			callable.registerOutParameter(4, Types.VARCHAR);// errer message
			callable.execute();

			errorCode = callable.getInt(1);
			error = callable.getString(4);
			if (errorCode == Constants.FUNCTION_FAILURE) {
				callable.close();
				callable = null;
				throw new DatabaseException(error);
			} else {
				Date ltrdt = null;
				;
				result = (ResultSet) callable.getObject(3);
				while (result.next()) {
					String str[] = new String[18];
					str[0] = result.getString(1);// cgpan
					str[1] = result.getString(2);// claimreference
					str[2] = result.getString(3);// q1
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
					str[15] = result.getString(16);// q14
					str[16] = result.getString(17);
					/*
					 * ltrdt = result.getDate(18); if(ltrdt != null){ str[17] =
					 * sdf.format(ltrdt); }else{ str[17] = ""; }
					 */
					str[17] = result.getString(18);

					rrQueryList.add(str);
				}
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ReportAction", "displayRRQueryDetail",
						ignore.getMessage());
			}

			Log.log(Log.ERROR, "ReportAction", "displayRRQueryDetail",
					e.getMessage());
			Log.logException(e);
		} finally {
			DBConnection.freeConnection(connection);
			request.setAttribute("danSummary", rrList);
			request.setAttribute("rrQuerySummary", rrQueryList);
		}
		claimForm.setCgpan("");
		claimForm.setClmRefNumber("");
		return mapping.findForward("success");
	}

	// added on 09-07-2013.Methods are taken from ReportsAction ,ReportManager
	// and Reportdao and renamed these methods
	public ActionForward showFilterForClaimDetailsNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		java.util.Date date = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		month = month - 1;
		day = day + 1;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		java.util.Date prevDate = calendar.getTime();
		CustomisedDate customDate = new CustomisedDate();
		customDate.setDate(prevDate);

		CustomisedDate customToDate = new CustomisedDate();
		customToDate.setDate(date);

		claimForm.setFromDate(customDate);
		claimForm.setToDate(customToDate);
		claimForm.setClmApplicationStatus(ClaimConstants.CLM_APPROVAL_STATUS);
		return mapping.findForward("displayFilterForClaimDtls");
	}

	public ActionForward displayListOfClaimRefNumbersNew(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log.log(Log.INFO, "NewReportsAction", "displayListOfClaimRefNumbers",
				"Entered");
		ClaimActionForm claimForm = (ClaimActionForm) form;

		User user = getUserInformation(request);
		String bankid = (user.getBankId()).trim();
		String zoneid = (user.getZoneId()).trim();
		String branchid = (user.getBranchId()).trim();
		String memberId = bankid + zoneid + branchid;

		java.util.Date fromDate = (java.util.Date) claimForm.getFromDate();
		java.util.Date toDate = (java.util.Date) claimForm.getToDate();
		String clmApplicationStatus = (String) claimForm
				.getClmApplicationStatus();

		claimForm.setstatusFlag(clmApplicationStatus);
		ReportManager manager = new ReportManager();
		Vector listOfClmRefNumbers = null;
		java.sql.Date sqlFromDate = null;
		NewReportsAction ra = new NewReportsAction();

		if (fromDate.toString().equals("")) {
			if (bankid.equals(Constants.CGTSI_USER_BANK_ID)) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbersNew(null,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus);
			} else if ((zoneid.equals("0000")) && (branchid.equals("0000"))) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(null,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid);
			} else if (branchid.equals("0000")) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(null,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid, zoneid);
			} else {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(null,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid, zoneid, branchid);
			}
		} else {
			sqlFromDate = new java.sql.Date(fromDate.getTime());
			// listOfClmRefNumbers =
			// manager.getListOfClaimRefNumbersNew(sqlFromDate, new
			// java.sql.Date(toDate.getTime()),clmApplicationStatus, memberId);
			if (bankid.equals(Constants.CGTSI_USER_BANK_ID)) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbersNew(
						sqlFromDate, new java.sql.Date(toDate.getTime()),
						clmApplicationStatus);
			} else if ((zoneid.equals("0000")) && (branchid.equals("0000"))) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(sqlFromDate,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid);
			} else if (branchid.equals("0000")) {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(sqlFromDate,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid, zoneid);
			} else {
				listOfClmRefNumbers = ra.getListOfClaimRefNumbers(sqlFromDate,
						new java.sql.Date(toDate.getTime()),
						clmApplicationStatus, bankid, zoneid, branchid);
			}
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
		
        else if(clmApplicationStatus.equals("RTD"))
            request.setAttribute("radioValue","TempororyRejected/Rejcted");
            
            
            else if(clmApplicationStatus.equals("AS"))
            request.setAttribute("radioValue","All Statuses Report");
   
		
		claimForm.setListOfClmRefNumbers(listOfClmRefNumbers);
		if (listOfClmRefNumbers != null) {
			if (listOfClmRefNumbers.size() == 0) {
				throw new NoDataException("There are no Claim Ref Numbers "
						+ "that match the query.");
			}
		}
		Log.log(Log.INFO, "NewReportsAction", "displayListOfClaimRefNumbers",
				"Exited");
		return mapping.findForward("getListOfClmRefNumbers");
	}

	
	
	public Vector getListOfClaimRefNumbersNewOLD(java.sql.Date fromDate,
			java.sql.Date toDate, String clmApplicationStatusFlag)
			throws DatabaseException {

		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbersNew()",
				"Entered");
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbersNew()",
				"From Date :" + fromDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbersNew()",
				"To Date :" + toDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbersNew()",
				"clmApplicationStatusFlag :" + clmApplicationStatusFlag);
		Connection conn = null;
		PreparedStatement prepStatement = null;
		ResultSet rs = null;
		Vector clmRefNumbersList = new Vector();
		String query = null;

		try {
			conn = DBConnection.getConnection();

			if ((fromDate != null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_HOLD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan,S.SSI_UNIT_NAME,TRUNC(C.CLM_APPROVED_DT),CLM_APPROVED_AMT "
							+ " from claim_detail c, member_info m,SSI_DETAIL S"
							+
							// " where c.clm_date between ? and ? AND C.BID=S.BID "
							// +
							// code changed clm_date to clm_approved_dt by
							// sukumar@path on 11-09-2009
							" where TRUNC(c.clm_approved_dt) between ? and ? AND C.BID=S.BID "
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,"
							+ " c.cgclan,S.SSI_UNIT_NAME,TRUNC(c.clm_approved_dt),CLM_APPROVED_AMT order by TRUNC(c.clm_approved_dt),bnkname";*/
					
					//kot
					
					
					
					
					
					// //System.out.println("Query:"+query);
					/*prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);*/
					
			           query="SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
			           "         DECODE (m.MEM_BRANCH_NAME,\n" + 
			           "                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
			           "                 m.MEM_BRANCH_NAME)\n" + 
			           "            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
			           "                 NULL, a.APP_approved_amount,\n" + 
			           "                 a.app_reapprove_amount)\n" + 
			           "            GuarntAmt, c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,ctd_tc_first_inst_pay_amt clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
			           "                   FROM claim_detail c,\n" + 
			           "         member_info m,\n" + 
			           "         SSI_DETAIL S,\n" + 
			           "         application_detail a,\n" + 
			           "         claim_tc_detail ct\n" + 
			           "   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ?  \n" + 
			           "         AND C.BID = S.BID\n" + 
			           "         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
			           "         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
			           "                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
			           "         AND LTRIM (\n" + 
			           "                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
			           "                LTRIM (\n" + 
			           "                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
			           "         AND c.clm_status = ? \n" + 
			           "         and ct.clm_ref_no = c.clm_ref_no\n" + 
			           "         and ct.cgpan = a.cgpan\n" + 
			           "         and A.APP_LOAN_TYPE in ('TC')\n" + 
			           "         union all\n" + 
			           "          SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
			           "         DECODE (m.MEM_BRANCH_NAME,\n" + 
			           "                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
			           "                 m.MEM_BRANCH_NAME)\n" + 
			           "            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
			           "                 NULL, a.APP_approved_amount,\n" + 
			           "                 a.app_reapprove_amount)\n" + 
			           "            GuarntAmt,c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,cwd_wc_first_inst_pay_amt clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
			           "    FROM claim_detail c,\n" + 
			           "         member_info m,\n" + 
			           "         SSI_DETAIL S,\n" + 
			           "         application_detail a,\n" + 
			           "         claim_wc_detail cw\n" + 
			           "   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ? \n" + 
			           "         AND C.BID = S.BID\n" + 
			           "         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
			           "         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
			           "                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
			           "         AND LTRIM (\n" + 
			           "                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
			           "                LTRIM (\n" + 
			           "                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
			           "         AND c.clm_status = ? \n" + 
			           "         and cw.clm_ref_no = c.clm_ref_no\n" + 
			           "         and cw.cgpan = a.cgpan\n" + 
			           "         and app_loan_type in ('WC')\n" + 
			           "union all\n" + 
			           " SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
			           "         DECODE (m.MEM_BRANCH_NAME,\n" + 
			           "                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
			           "                 m.MEM_BRANCH_NAME)\n" + 
			           "            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
			           "                 NULL, a.APP_approved_amount,\n" + 
			           "                 a.app_reapprove_amount)\n" + 
			           "            GuarntAmt,c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,  nvl(cwd_wc_first_inst_pay_amt,0)+nvl(ctd_tc_first_inst_pay_amt,0) clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
			           "    FROM claim_detail c,\n" + 
			           "         member_info m,\n" + 
			           "         SSI_DETAIL S,\n" + 
			           "         application_detail a,\n" + 
			           "         claim_wc_detail cw,\n" + 
			           "         claim_tc_detail ct\n" + 
			           "   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ?\n" + 
			           "         AND C.BID = S.BID\n" + 
			           "         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
			           "         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
			           "                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
			           "         AND LTRIM (\n" + 
			           "                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
			           "                LTRIM (\n" + 
			           "                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
			           "         AND c.clm_status = ? \n" + 
			           "         and cw.clm_ref_no = c.clm_ref_no\n" + 
			           "         and cw.cgpan = a.cgpan\n" + 
			           "         and ct.clm_ref_no = c.clm_ref_no\n" + 
			           "         and ct.cgpan = a.cgpan\n" + 
			           "         and app_loan_type in ('CC')";
			           
			                   prepStatement = conn.prepareStatement(query);
			                   prepStatement.setDate(1,fromDate);
			                   prepStatement.setDate(2,toDate);
			                   prepStatement.setString(3,clmApplicationStatusFlag);
			                     prepStatement.setDate(4,fromDate);
			                     prepStatement.setDate(5,toDate);
			                     prepStatement.setString(6,clmApplicationStatusFlag);
			                     prepStatement.setDate(7,fromDate);
			                     prepStatement.setDate(8,toDate);
			                     prepStatement.setString(9,clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				}
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail c, member_info m,SSI_DETAIL S"
							+
							// code changed clm_date to clm_approved_dt by
							// sukumar@path on 11-09-2009
							// " where c.clm_date between ? and ?" +
							" where TRUNC(c.clm_approved_dt) between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ? AND C.BID=S.BID"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";*/
					//kot
                    query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,d.mem_bank_name,d.mem_zone_name, DECODE (d.MEM_BRANCH_NAME,\n" + 
                    "                 NULL, e.APP_MLI_BRANCH_NAME,\n" + 
                    "                 d.MEM_BRANCH_NAME)\n" + 
                    "            branch,e.cgpan,c.ssi_unit_name, DECODE (e.app_reapprove_amount,\n" + 
                    "                 NULL, e.APP_approved_amount,\n" + 
                    "                 e.app_reapprove_amount)\n" + 
                    "            guarantamt,A.clm_ref_no, a.clm_date,b.caa_applied_amount clmAppdAmt,a.clm_remarks\n" + 
                    "                      FROM claim_detail a,\n" + 
                    "         claim_application_amount b,\n" + 
                    "         ssi_detail c,\n" + 
                    "         member_info d,\n" + 
                    "         application_detail e\n" + 
                    "   WHERE   \n" + 
                    "        b.cgpan = e.cgpan\n" + 
                    "         AND a.clm_ref_no = b.clm_ref_no\n" + 
                    "         AND a.bid = c.bid\n" + 
                    "         AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
                    "                d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
                    "         AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ? \n" + 
                    "         AND a.clm_status = ? ";
                    
              prepStatement = conn.prepareStatement(query);
                prepStatement.setDate(1,fromDate);
                prepStatement.setDate(2,toDate);
                prepStatement.setString(3,clmApplicationStatusFlag);
            
              rs = (ResultSet)prepStatement.executeQuery();
					
					/*prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();*/
				}
				// ADDED FORWARD REPORT BY SUKUMAR@PATH ON 12-09-2009
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_FORWARD_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is  Forward");
					/*query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S "
							+ " where TRUNC(CLM_CREATED_MODIFIED_DT) between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ? AND C.BID=S.BID"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();*/
	                   query="SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
	                   "       m.mem_bank_name bnkname,\n" + 
	                   "       m.mem_zone_name,\n" + 
	                   "       DECODE (m.MEM_BRANCH_NAME,\n" + 
	                   "               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
	                   "               m.MEM_BRANCH_NAME)\n" + 
	                   "          BRANCH,\n" + 
	                   "       d.cgpan,\n" + 
	                   "       S.SSI_UNIT_NAME,\n" + 
	                   "       DECODE (a.app_reapprove_amount,\n" + 
	                   "               NULL, a.APP_approved_amount,\n" + 
	                   "               a.app_reapprove_amount)\n" + 
	                   "          guartAmt,\n" + 
	                   "       c.clm_ref_no,\n" + 
	                   "       C.CLM_DATE clmFilgDt,\n" + 
	                   "       d.ctd_tc_first_inst_pay_amt clmForwrdAmt,\n" + 
	                   "       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt\n" + 
	                   "  FROM claim_detail_temp c,\n" + 
	                   "       member_info m,\n" + 
	                   "       SSI_DETAIL S,\n" + 
	                   "       application_detail a,\n" + 
	                   "       CLAIM_TC_DETAIL_TEMP D\n" + 
	                   " WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
	                   "                                                 AND  ? \n" + 
	                   "       AND C.BID = S.BID\n" + 
	                   "       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
	                   "       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
	                   "              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
	                   "       AND LTRIM (\n" + 
	                   "              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
	                   "              LTRIM (\n" + 
	                   "                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
	                   "       AND c.clm_status = ? \n" + 
	                   "       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
	                   "       AND d.cgpan = a.cgpan\n" + 
	                   "       AND app_loan_type IN ('TC')\n" + 
	                   "UNION ALL\n" + 
	                   "SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
	                   "       m.mem_bank_name bnkname,\n" + 
	                   "       m.mem_zone_name,\n" + 
	                   "       DECODE (m.MEM_BRANCH_NAME,\n" + 
	                   "               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
	                   "               m.MEM_BRANCH_NAME)\n" + 
	                   "          BRANCH,\n" + 
	                   "       d.cgpan,\n" + 
	                   "       S.SSI_UNIT_NAME,\n" + 
	                   "       DECODE (a.app_reapprove_amount,\n" + 
	                   "               NULL, a.APP_approved_amount,\n" + 
	                   "               a.app_reapprove_amount)\n" + 
	                   "          guartAmt,\n" + 
	                   "       c.clm_ref_no,\n" + 
	                   "       C.CLM_DATE clmFilgDt,\n" + 
	                   "       d.cwd_wc_first_inst_pay_amt clmForwrdAmt,\n" + 
	                   "       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt\n" + 
	                   "  FROM claim_detail_temp c,\n" + 
	                   "       member_info m,\n" + 
	                   "       SSI_DETAIL S,\n" + 
	                   "       application_detail a,\n" + 
	                   "       CLAIM_WC_DETAIL_TEMP D\n" + 
	                   " WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
	                   "                                                 AND ?  \n" + 
	                   "       AND C.BID = S.BID\n" + 
	                   "       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
	                   "       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
	                   "              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
	                   "       AND LTRIM (\n" + 
	                   "              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
	                   "              LTRIM (\n" + 
	                   "                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
	                   "       AND c.clm_status = ? \n" + 
	                   "       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
	                   "       AND d.cgpan = a.cgpan\n" + 
	                   "       AND app_loan_type IN ('WC')\n" + 
	                   "UNION ALL\n" + 
	                   "SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
	                   "       m.mem_bank_name bnkname,\n" + 
	                   "       m.mem_zone_name,\n" + 
	                   "       DECODE (m.MEM_BRANCH_NAME,\n" + 
	                   "               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
	                   "               m.MEM_BRANCH_NAME)\n" + 
	                   "          BRANCH,\n" + 
	                   "       d.cgpan,\n" + 
	                   "       S.SSI_UNIT_NAME,\n" + 
	                   "       DECODE (a.app_reapprove_amount,\n" + 
	                   "               NULL, a.APP_approved_amount,\n" + 
	                   "               a.app_reapprove_amount)\n" + 
	                   "          guartAmt,\n" + 
	                   "       c.clm_ref_no,\n" + 
	                   "       C.CLM_DATE clmFilgDt,\n" + 
	                   "         NVL (d.cwd_wc_first_inst_pay_amt, 0)\n" + 
	                   "       + NVL (CWD_WC_FIRST_INST_PAY_AMT, 0)\n" + 
	                   "          clmForwrdAmt,\n" + 
	                   "       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt\n" + 
	                   "  FROM claim_detail_temp c,\n" + 
	                   "       member_info m,\n" + 
	                   "       SSI_DETAIL S,\n" + 
	                   "       application_detail a,\n" + 
	                   "       CLAIM_WC_DETAIL_TEMP D,\n" + 
	                   "       CLAIM_TC_DETAIL_TEMP E\n" + 
	                   " WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
	                   "                                                 AND ? \n" + 
	                   "       AND C.BID = S.BID\n" + 
	                   "       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
	                   "       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
	                   "              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
	                   "       AND LTRIM (\n" + 
	                   "              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
	                   "              LTRIM (\n" + 
	                   "                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
	                   "       AND c.clm_status = ? \n" + 
	                   "       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
	                   "       AND D.cgpan = a.cgpan\n" + 
	                   "       AND C.CLM_REF_NO = E.CLM_REF_NO\n" + 
	                   "       AND E.cgpan = a.cgpan\n" + 
	                   "       AND app_loan_type IN ('CC')";
	                   
	                    prepStatement = conn.prepareStatement(query);
	                    prepStatement.setDate(1,fromDate);
	                    prepStatement.setDate(2,toDate);
	                     prepStatement.setString(3,clmApplicationStatusFlag);
	                     prepStatement.setDate(4,fromDate);
	                     prepStatement.setDate(5,toDate);
	                     prepStatement.setString(6,clmApplicationStatusFlag);
	                     prepStatement.setDate(7,fromDate);
	                     prepStatement.setDate(8,toDate);
	                     prepStatement.setString(9,clmApplicationStatusFlag);
	                 rs = (ResultSet)prepStatement.executeQuery();
					
					
					
				}
				/*
				 * ADDED BY SUKUMAR@PATH ON 20-FEB-2010 FOR DISPLAY TEMPORARY
				 * CLOSE & TEMPORARY REJECT APPLICATION
				 */
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)
						|| clmApplicationStatusFlag
								.equals(ClaimConstants.CLM_TEMPORARY_REJECT)
						|| clmApplicationStatusFlag
								.equals(ClaimConstants.CLM_WITHDRAWN)
						|| "RT".equals(clmApplicationStatusFlag)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is  TEMPORARY CLOSED");
					/*query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT),clm_return_remarks "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S "
							+ " where TRUNC(CLM_CREATED_MODIFIED_DT) between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ? AND C.BID=S.BID"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT),clm_return_remarks order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();*/
					
					
					//kot
					
                    query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,\n" + 
                    "       d.mem_bank_name,\n" + 
                    "       d.mem_zone_name,\n" + 
                    "       DECODE (d.MEM_BRANCH_NAME,\n" + 
                    "               NULL, e.APP_MLI_BRANCH_NAME,\n" + 
                    "               d.MEM_BRANCH_NAME)\n" + 
                    "          branch,\n" + 
                    "       e.cgpan,\n" + 
                    "       c.ssi_unit_name,\n" + 
                    "       DECODE (e.app_reapprove_amount,\n" + 
                    "               NULL, e.APP_approved_amount,\n" + 
                    "               e.app_reapprove_amount)\n" + 
                    "          guarantamt,\n" + 
                    "       A.clm_ref_no,\n" + 
                    "       a.clm_date,\n" + 
                    "       b.caa_applied_amount clmAppdAmt\n" + 
                    "  FROM claim_detail_temp a,\n" + 
                    "       claim_application_amount_temp b,\n" + 
                    "       ssi_detail c,\n" + 
                    "       member_info d,\n" + 
                    "       application_detail e\n" + 
                    " WHERE     b.cgpan = e.cgpan\n" + 
                    "       AND a.clm_ref_no = b.clm_ref_no\n" + 
                    "       AND a.bid = c.bid\n" + 
                    "       AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
                    "              d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
                    "       AND TRUNC (CLM_DATE) BETWEEN ?  AND ? \n" + 
                    "       AND a.clm_status = ? \n";
                    
              prepStatement = conn.prepareStatement(query);
              prepStatement.setDate(1,fromDate);
              prepStatement.setDate(2,toDate);
              prepStatement.setString(3,clmApplicationStatusFlag);
             
              rs = (ResultSet)prepStatement.executeQuery();
				}
				// added by upchar@path on 03/07/2013 to display reply received
				// claim applications
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REPLY_RECEIVED)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is  TEMPORARY CLOSED");
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S "
							+ " where TRUNC(CLM_CREATED_MODIFIED_DT) between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ? AND C.BID=S.BID"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate != null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_HOLD_STATUS)
					// clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
					))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
			/*	query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE "
						+ " from claim_detail_temp c, member_info m,SSI_DETAIL S "
						+ " where c.clm_date between ? and ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ? AND C.BID=S.BID"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE order by CLM_DATE,bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, fromDate);
				prepStatement.setDate(2, toDate);
				prepStatement.setString(3, clmApplicationStatusFlag);
				rs = (ResultSet) prepStatement.executeQuery();*/
				
                //new kot
                query="SELECT  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,m.mem_bank_name bnkname,m.mem_zone_name, DECODE (m.MEM_BRANCH_NAME,\n" + 
                "                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
                "                 m.MEM_BRANCH_NAME)\n" + 
                "            BRANCH,a.cgpan,S.SSI_UNIT_NAME,DECODE (a.app_reapprove_amount,\n" + 
                "                 NULL, a.APP_approved_amount,\n" + 
                "                 a.app_reapprove_amount)\n" + 
                "            GuarAmt,c.clm_ref_no, C.CLM_DATE, b.caa_applied_amount clmAppliedAmt,C.CLM_DECLARATION_RECVD clmDeclRcvdFlg, C.CLM_DECL_RECVD_DT clmDeclRecvdDt\n" + 
                "                                      \n" + 
                "    FROM claim_detail_temp c,\n" + 
                "    claim_application_amount_temp b,\n" + 
                "         member_info m,\n" + 
                "         SSI_DETAIL S,\n" + 
                "         application_detail a\n" + 
                "         \n" + 
                "   WHERE     TRUNC (c.clm_date) BETWEEN ?  AND ? \n" + 
                "         AND C.BID = S.BID\n" + 
                "         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
                "         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
                "                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
                "         AND LTRIM (\n" + 
                "                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
                "                LTRIM (\n" + 
                "                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
                "         AND c.clm_status = ? \n" + 
                "         AND C.CLM_REF_NO = b.CLM_REF_NO\n" + 
                "         AND b.CGPAN = a.CGPAN ";
                         
                
        prepStatement = conn.prepareStatement(query);
          prepStatement.setDate(1,fromDate);
          prepStatement.setDate(2,toDate);
          prepStatement.setString(3,clmApplicationStatusFlag);
          
        rs = (ResultSet)prepStatement.executeQuery();
			}
			if ((fromDate == null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_HOLD_STATUS)
					// clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
					))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan,S.SSI_UNIT_NAME,TRUNC(CLM_APPROVED_DT),CLM_APPROVED_AMT "
							+ " from claim_detail c, member_info m,SSI_DETAIL S "
							+
							// code changed clm_date to clm_approved_dt by
							// sukumar@path on 11-09-2009
							// " where c.clm_date <= ? AND C.BID=S.BID " +
							" where TRUNC(c.clm_approved_dt) <= ? AND C.BID=S.BID "
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,"
							+ " c.cgclan,S.SSI_UNIT_NAME,TRUNC(CLM_APPROVED_DT),CLM_APPROVED_AMT order by Trunc(c.clm_approved_dt),bnkname";
					prepStatement = conn.prepareStatement(query);
					// prepStatement.setDate(1,fromDate);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail c, member_info m,SSI_DETAIL S "
							+
							// condition changed clm_date to clm_approved_dt by
							// sukumar@path on 11-09-2009
							// " where c.clm_date <= ?" +
							" where TRUNC(c.clm_created_modified_dt) <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ? AND C.BID=S.BID "
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();*/
                    query="  SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,d.mem_bank_name,d.mem_zone_name, DECODE (d.MEM_BRANCH_NAME,\n" + 
                    "                 NULL, e.APP_MLI_BRANCH_NAME,\n" + 
                    "                 d.MEM_BRANCH_NAME)\n" + 
                    "            branch,e.cgpan,c.ssi_unit_name, DECODE (e.app_reapprove_amount,\n" + 
                    "                 NULL, e.APP_approved_amount,\n" + 
                    "                 e.app_reapprove_amount)\n" + 
                    "            guarantamt,A.clm_ref_no, a.clm_date,b.caa_applied_amount clmAppdAmt,a.clm_remarks\n" + 
                    "                      FROM claim_detail a,\n" + 
                    "         claim_application_amount b,\n" + 
                    "         ssi_detail c,\n" + 
                    "         member_info d,\n" + 
                    "         application_detail e\n" + 
                    "   WHERE   \n" + 
                    "        b.cgpan = e.cgpan\n" + 
                    "         AND a.clm_ref_no = b.clm_ref_no\n" + 
                    "         AND a.bid = c.bid\n" + 
                    "         AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
                    "                d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
                    "         AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ? \n" + 
                    "         AND a.clm_status = ? ";
                    
                    
              prepStatement = conn.prepareStatement(query);
                prepStatement.setDate(1,fromDate);
                prepStatement.setDate(2,toDate);
                prepStatement.setString(3,clmApplicationStatusFlag);
               
              rs = (ResultSet)prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_FORWARD_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Forward");
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S"
							+ " where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID "
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)
						|| clmApplicationStatusFlag
								.equals(ClaimConstants.CLM_TEMPORARY_REJECT)
						|| clmApplicationStatusFlag
								.equals(ClaimConstants.CLM_WITHDRAWN)
						|| "RT".equals(clmApplicationStatusFlag)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Forward");
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT),clm_return_remarks "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S"
							+ " where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID "
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT),clm_return_remarks order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REPLY_RECEIVED)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is  TEMPORARY CLOSED");
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) "
							+ " from claim_detail_temp c, member_info m,SSI_DETAIL S"
							+ " where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID "
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate == null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_HOLD_STATUS)
					// clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
					))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE "
						+ " from claim_detail_temp c, member_info m,SSI_DETAIL S"
						+ " where c.clm_date <= ? AND C.BID=S.BID "
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE order by CLM_DATE,bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, toDate);
				prepStatement.setString(2, clmApplicationStatusFlag);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			
			
			
			
            String memberBankName = null;
            String memberId       = null;
            String clmRefNumber   = null;
            String cgclan         = "";
            ClaimDetail clmDtl    = null;
        	String returnRemark = "";
            String unitName = "";
            java.util.Date submittedDt = null;
              java.util.Date clmLodgmentdDt = null;
              java.util.Date claimForwardedDt = null;
              
              Double claimForwdAmt=null;
              
              java.util.Date claimRejectedDt = null;
              
          double claimAppliedAmt=0.0;
              String clmDeclRecvdFlag=null;
             java.util.Date  clmDeclRecvdDt=null;
              
              
              
            double claimApprovedAmt = 0.0;
            String zoneName=null;
            String branchName=null;
            double guarApprdAmount=0.0;
            
              double claimDecRecvdAmt=0.0;
              String claimDecRecvdflag=null;
              java.util.Date claimDecRecvdDt=null;
              String claimRemarks=null;
            String cgpan=null;
            java.util.Date clmApprovedDt=null;
            
 
            if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_APPROVAL_STATUS))
            {
              while(rs.next())
              {
              
              
              
                  memberId        = (String)rs.getString(1);
                  memberBankName  = (String)rs.getString(2);
                  zoneName=(String)rs.getString(3);
                  branchName=(String)rs.getString(4);
                  cgpan=(String)rs.getString(5);
                  unitName        = (String)rs.getString(6);
                  guarApprdAmount=(Double)rs.getDouble(7);
                  clmRefNumber    = (String)rs.getString(8);
                  cgclan          = (String)rs.getString(9);
                  clmLodgmentdDt=(Date)rs.getDate(10);
                  claimApprovedAmt = (double)rs.getDouble(11);
                  clmApprovedDt=(Date)rs.getDate(12);
                  
             
                  
                
                
                
                clmDtl          = new ClaimDetail();
                
                  clmDtl.setMliId(memberId);
                  clmDtl.setMliName(memberBankName);
                  clmDtl.setBranchName(branchName);
                  clmDtl.setZoneName(zoneName);
                  clmDtl.setCgpan(cgpan);
                  clmDtl.setSsiUnitName(unitName);
                  clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                  clmDtl.setClaimRefNum(clmRefNumber);
                  clmDtl.setCGCLAN(cgclan);
                  clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                  clmDtl.setClaimApprovedAmount(claimApprovedAmt);
                  clmDtl.setClaimApprovedDt(clmApprovedDt);
                  
                
 
                // Adding the ClaimDetail object to the vector
                clmRefNumbersList.addElement(clmDtl);
              }
              rs.close();
              rs = null;
            } else if
            
            
                (clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS))
                {
                  while(rs.next())
                  {
                  
                      memberId        = (String)rs.getString(1);
                      memberBankName  = (String)rs.getString(2);
                      zoneName=(String)rs.getString(3);
                      branchName=(String)rs.getString(4);
                      cgpan=(String)rs.getString(5);
                      unitName        = (String)rs.getString(6);
                      guarApprdAmount=(Double)rs.getDouble(7);
                      clmRefNumber    = (String)rs.getString(8);
                      clmLodgmentdDt=(Date)rs.getDate(9);
                      claimAppliedAmt=(Double)rs.getDouble(10);
                      clmDeclRecvdFlag=(String)rs.getString(11);
                      clmDeclRecvdDt=(Date)rs.getDate(12);
                      
                      
                     
                          
                      
                  
                     
                      
                      
                      
                      
                      
                      
                      
                      
                      clmDtl          = new ClaimDetail();
                      
                      clmDtl.setMliId(memberId);
                      clmDtl.setMliName(memberBankName);
                      clmDtl.setBranchName(branchName);
                      clmDtl.setZoneName(zoneName);
                      clmDtl.setCgpan(cgpan);
                      clmDtl.setSsiUnitName(unitName);
                      clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                      clmDtl.setClaimRefNum(clmRefNumber);
                      clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                      clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                       clmDtl.setClmDeclRecvdFlag(clmDeclRecvdFlag);
                     // clmDtl.setClmDeclRecvdDt(clmDeclRecvdDt);
                  
                  
                  
                  
                  
                  
                /*    memberBankName  = (String)rs.getString(1);
                    memberId        = (String)rs.getString(2);
                    clmRefNumber    = (String)rs.getString(3);
                   // cgclan          = (String)rs.getString(4);
                    unitName        = (String)rs.getString(4);
                      clmLodgmentdDt=(Date)rs.getDate(5);
                    //submittedDt     = (Date)rs.getDate(6);
                   // claimApprovedAmt = (double)rs.getDouble(7);
                      zoneName=(String)rs.getString(6);
                      branchName=(String)rs.getString(7);
                      guarApprdAmount=(Double)rs.getDouble(8);
                      claimDecRecvdAmt=(Double)rs.getDouble(9);
                      claimDecRecvdflag    = (String)rs.getString(10);
                      claimDecRecvdDt=(Date)rs.getDate(11);
                      
                                            
                                          
                    
                    clmDtl          = new ClaimDetail();
                    clmDtl.setMliName(memberBankName);
                    clmDtl.setMliId(memberId);
                    clmDtl.setClaimRefNum(clmRefNumber);
                   // clmDtl.setCGCLAN(cgclan);
                    clmDtl.setSsiUnitName(unitName);
                    clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                  //  clmDtl.setEligibleClaimAmt(claimApprovedAmt);
                      clmDtl.setZoneName(zoneName);
                      clmDtl.setBranchName(branchName);
                      clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                      
                      clmDtl.setClaimDecRecvdAmt(claimDecRecvdAmt);
                      clmDtl.setClaimDecRecvdflag(claimDecRecvdflag);                       
                      clmDtl.setClaimDecRecvdDt(claimDecRecvdDt);
                     // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                    
                    */
                    
                    
                
                    // Adding the ClaimDetail object to the vector
                    clmRefNumbersList.addElement(clmDtl);
                  }
                  rs.close();
                  rs = null;
                } else if
                
                
                    
                        (clmApplicationStatusFlag.equals(ClaimConstants.CLM_WITHDRAWN))
                        {
                          while(rs.next())
                          {
                          
                          
                          
                          
                              memberId        = (String)rs.getString(1);
                              memberBankName  = (String)rs.getString(2);
                              zoneName=(String)rs.getString(3);
                              branchName=(String)rs.getString(4);
                              cgpan=(String)rs.getString(5);
                              unitName        = (String)rs.getString(6);
                              guarApprdAmount=(Double)rs.getDouble(7);
                              clmRefNumber    = (String)rs.getString(8);
                              clmLodgmentdDt=(Date)rs.getDate(9);
                              claimAppliedAmt=(Double)rs.getDouble(10);
                            
                              
                              
                              
                              
                              
                              clmDtl          = new ClaimDetail();
                              
                              clmDtl.setMliId(memberId);
                              clmDtl.setMliName(memberBankName);
                              clmDtl.setBranchName(branchName);
                              clmDtl.setZoneName(zoneName);
                              clmDtl.setCgpan(cgpan);
                              clmDtl.setSsiUnitName(unitName);
                              clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                              clmDtl.setClaimRefNum(clmRefNumber);
                              clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                              clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                              
                              
                               // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                               
                               
                                // Adding the ClaimDetail object to the vector
                                clmRefNumbersList.addElement(clmDtl);
                              
                          }
                          rs.close();
                          rs = null;
                        }
              else if
                                 
                                 
                                     
                                         (clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_CLOSE))
                                         {
                                           while(rs.next())
                                           {
                                           
                                           
                                           
                                           
                                               memberId        = (String)rs.getString(1);
                                               memberBankName  = (String)rs.getString(2);
                                               zoneName=(String)rs.getString(3);
                                               branchName=(String)rs.getString(4);
                                               cgpan=(String)rs.getString(5);
                                               unitName        = (String)rs.getString(6);
                                               guarApprdAmount=(Double)rs.getDouble(7);
                                               clmRefNumber    = (String)rs.getString(8);
                                               clmLodgmentdDt=(Date)rs.getDate(9);
                                               claimAppliedAmt=(Double)rs.getDouble(10);
                                             
                                               
                                               
                                               
                                               
                                               
                                               clmDtl          = new ClaimDetail();
                                               
                                               clmDtl.setMliId(memberId);
                                               clmDtl.setMliName(memberBankName);
                                               clmDtl.setBranchName(branchName);
                                               clmDtl.setZoneName(zoneName);
                                               clmDtl.setCgpan(cgpan);
                                               clmDtl.setSsiUnitName(unitName);
                                               clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                                               clmDtl.setClaimRefNum(clmRefNumber);
                                               clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                                               clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                                               
                                               
                                                // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                                                
                                                
                                                 // Adding the ClaimDetail object to the vector
                                                 clmRefNumbersList.addElement(clmDtl);
                                               
                                           }
                                           rs.close();
                                           rs = null;
                                         }
                                         
              else if
                                 
                                 
                                     
                                         (clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_REJECT))
                                         {
                                           while(rs.next())
                                           {
                                           
                                           
                                           
                                           
                                               memberId        = (String)rs.getString(1);
                                               memberBankName  = (String)rs.getString(2);
                                               zoneName=(String)rs.getString(3);
                                               branchName=(String)rs.getString(4);
                                               cgpan=(String)rs.getString(5);
                                               unitName        = (String)rs.getString(6);
                                               guarApprdAmount=(Double)rs.getDouble(7);
                                               clmRefNumber    = (String)rs.getString(8);
                                               clmLodgmentdDt=(Date)rs.getDate(9);
                                               claimAppliedAmt=(Double)rs.getDouble(10);
                                             
                                               
                                               
                                               
                                               
                                               
                                               clmDtl          = new ClaimDetail();
                                               
                                               clmDtl.setMliId(memberId);
                                               clmDtl.setMliName(memberBankName);
                                               clmDtl.setBranchName(branchName);
                                               clmDtl.setZoneName(zoneName);
                                               clmDtl.setCgpan(cgpan);
                                               clmDtl.setSsiUnitName(unitName);
                                               clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                                               clmDtl.setClaimRefNum(clmRefNumber);
                                               clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                                               clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                                               
                                               
                                                // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                                                
                                                
                                                 // Adding the ClaimDetail object to the vector
                                                 clmRefNumbersList.addElement(clmDtl);
                                               
                                           }
                                           rs.close();
                                           rs = null;
                                         }
                 
                        else if
                
                
                    (clmApplicationStatusFlag.equals(ClaimConstants.CLM_REJECT_STATUS))
                    {
                      while(rs.next())
                      {
                      
                          memberId        = (String)rs.getString(1);
                          memberBankName  = (String)rs.getString(2);
                          zoneName=(String)rs.getString(3);
                          branchName=(String)rs.getString(4);
                          cgpan=(String)rs.getString(5);
                          unitName        = (String)rs.getString(6);
                          guarApprdAmount=(Double)rs.getDouble(7);
                          clmRefNumber    = (String)rs.getString(8);
                          clmLodgmentdDt=(Date)rs.getDate(9);
                          claimAppliedAmt=(Double)rs.getDouble(10);
                          claimRemarks=(String)rs.getString(11);
                          
                          
                          
                          
                          
                          
                          clmDtl          = new ClaimDetail();
                          
                          clmDtl.setMliId(memberId);
                          clmDtl.setMliName(memberBankName);
                          clmDtl.setBranchName(branchName);
                          clmDtl.setZoneName(zoneName);
                          clmDtl.setCgpan(cgpan);
                          clmDtl.setSsiUnitName(unitName);
                          clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                          clmDtl.setClaimRefNum(clmRefNumber);
                          clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                          clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                          clmDtl.setClaimRemarks(claimRemarks);
                        
                      
                      
                     /*     memberBankName  = (String)rs.getString(1);
                          memberId        = (String)rs.getString(2);
                          clmRefNumber    = (String)rs.getString(3);
                      
                          unitName        = (String)rs.getString(4);
                          clmLodgmentdDt     = (Date)rs.getDate(6);                       
                            claimRejectedDt     = (Date)rs.getDate(5);                       
                            zoneName=(String)rs.getString(7);
                            branchName=(String)rs.getString(8);
                            guarApprdAmount=(Double)rs.getDouble(9);
                            claimApprovedAmt = (double)rs.getDouble(10);
                          claimRemarks=(String)rs.getString(11);
                            
                          
                            
                          
                          
                          
                          clmDtl          = new ClaimDetail();
                          clmDtl.setMliName(memberBankName);
                          clmDtl.setMliId(memberId);
                          clmDtl.setClaimRefNum(clmRefNumber);
                          //  clmDtl.setCGCLAN(cgclan);
                          clmDtl.setSsiUnitName(unitName);
                          //  clmDtl.setClmApprvdDt(submittedDt);
                          clmDtl.setEligibleClaimAmt(claimApprovedAmt);
                            clmDtl.setZoneName(zoneName);
                            clmDtl.setBranchName(branchName);
                            clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                            clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                            clmDtl.setClaimRejectedDt(claimRejectedDt);
                          clmDtl.setClaimRemarks(claimRemarks);
                           // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);*/
                           
                           
                            // Adding the ClaimDetail object to the vector
                            clmRefNumbersList.addElement(clmDtl);
                          
                          
                      }
                      rs.close();
                      rs = null;
                    }
                
                else if
                
                
                
                ("RT".equals(clmApplicationStatusFlag)) {
    				while (rs.next()) {
    					memberBankName = (String) rs.getString(1);
    					memberId = (String) rs.getString(2);
    					clmRefNumber = (String) rs.getString(3);
    					unitName = (String) rs.getString(4);
    					submittedDt = (Date) rs.getDate(5);
    					returnRemark = (String) rs.getString(6);

    					clmDtl = new ClaimDetail();
    					clmDtl.setMliName(memberBankName);
    					clmDtl.setMliId(memberId);
    					clmDtl.setClaimRefNum(clmRefNumber);
    					clmDtl.setCGCLAN(cgclan);
    					clmDtl.setSsiUnitName(unitName);
    					clmDtl.setClmSubmittedDt(submittedDt);
    					clmDtl.setReturnRemark(returnRemark);
    					// Adding the ClaimDetail object to the vector
    					clmRefNumbersList.addElement(clmDtl);
    				}
    				rs.close();
    				rs = null;
                
                
                }
                
                
    				else if
                
                
                
                (clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS))
                {
                  while(rs.next())
                  {
                  
                  
                      memberId        = (String)rs.getString(1);
                      memberBankName  = (String)rs.getString(2);
                      zoneName=(String)rs.getString(3);
                      branchName=(String)rs.getString(4);
                      cgpan=(String)rs.getString(5);
                      unitName        = (String)rs.getString(6);
                      guarApprdAmount=(Double)rs.getDouble(7);
                      clmRefNumber    = (String)rs.getString(8);
                      clmLodgmentdDt=(Date)rs.getDate(9);
                      
                      claimForwdAmt = (double)rs.getDouble(10);
                      claimForwardedDt=(Date)rs.getDate(11);
                      
                      
                      
                      
                      
                      
                      clmDtl          = new ClaimDetail();
                      
                      clmDtl.setMliId(memberId);
                      clmDtl.setMliName(memberBankName);
                      clmDtl.setBranchName(branchName);
                      clmDtl.setZoneName(zoneName);
                      clmDtl.setCgpan(cgpan);
                      clmDtl.setSsiUnitName(unitName);
                      clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                      clmDtl.setClaimRefNum(clmRefNumber);
                      clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                      clmDtl.setClaimForwdAmt(claimForwdAmt);
                      clmDtl.setClaimForwardedDt(claimForwardedDt);
                      
                      
                      
                      
                      
                    
                     /* clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                  
                  
                  
                  
                    memberBankName  = (String)rs.getString(1);
                    memberId        = (String)rs.getString(2);
                    clmRefNumber    = (String)rs.getString(3);
                    //cgclan          = (String)rs.getString(4);
                    unitName        = (String)rs.getString(4);
                    clmLodgmentdDt     = (Date)rs.getDate(5);                       
                      claimForwardedDt     = (Date)rs.getDate(6);                       
                      zoneName=(String)rs.getString(7);
                      branchName=(String)rs.getString(8);
                      guarApprdAmount=(Double)rs.getDouble(9);
                      claimApprovedAmt = (double)rs.getDouble(10);
                    
                      
                    
                    
                    
                    clmDtl          = new ClaimDetail();
                    clmDtl.setMliName(memberBankName);
                    clmDtl.setMliId(memberId);
                    clmDtl.setClaimRefNum(clmRefNumber);
                  //  clmDtl.setCGCLAN(cgclan);
                    clmDtl.setSsiUnitName(unitName);
                  //  clmDtl.setClmApprvdDt(submittedDt);
                    clmDtl.setEligibleClaimAmt(claimApprovedAmt);
                      clmDtl.setZoneName(zoneName);
                      clmDtl.setBranchName(branchName);
                      clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                      clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                      clmDtl.setClaimForwardedDt(claimForwardedDt);
                     // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                    
                    
                    */
                    
                
                    // Adding the ClaimDetail object to the vector
                    clmRefNumbersList.addElement(clmDtl);
                  }
                  rs.close();
                  rs = null;
                }
            else 
            {
              while(rs.next())
              {
                memberBankName  = (String)rs.getString(1);
                memberId        = (String)rs.getString(2);
                clmRefNumber    = (String)rs.getString(3);
                unitName        = (String)rs.getString(4);
                submittedDt     = (Date)rs.getDate(5);
                
                  zoneName=(String)rs.getString(6);
                  branchName=(String)rs.getString(7);
                  guarApprdAmount=(Double)rs.getDouble(8);
                
 
                clmDtl          = new ClaimDetail();
                clmDtl.setMliName(memberBankName);
                clmDtl.setMliId(memberId);
                clmDtl.setClaimRefNum(clmRefNumber);
                clmDtl.setCGCLAN(cgclan);
                clmDtl.setSsiUnitName(unitName);
                clmDtl.setClmSubmittedDt(submittedDt);
                
                  clmDtl.setZoneName(zoneName);
                  clmDtl.setBranchName(branchName);
                  clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                
                
                
 
               // Adding the ClaimDetail object to the vector
                clmRefNumbersList.addElement(clmDtl);
              }
              rs.close();
              rs = null;
            }
            prepStatement.close();
            prepStatement=null;
          }
          catch(SQLException sqlexception)
          {
            // sqlexception.printStackTrace();
            throw new DatabaseException(sqlexception.getMessage());
          }
          finally{
            DBConnection.freeConnection(conn);
          }
          return clmRefNumbersList;
      }
			
			
			
			
			
			
			/*
			String memberBankName = null;
			String memberId = null;
			String clmRefNumber = null;
			String cgclan = "";
			ClaimDetail clmDtl = null;
			String unitName = "";
			java.util.Date submittedDt = null;
			double claimApprovedAmt = 0.0;
			String returnRemark = "";

			if (clmApplicationStatusFlag
					.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					cgclan = (String) rs.getString(4);
					unitName = (String) rs.getString(5);
					submittedDt = (Date) rs.getDate(6);
					claimApprovedAmt = (double) rs.getDouble(7);
					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					clmDtl.setSsiUnitName(unitName);
					clmDtl.setClmSubmittedDt(submittedDt);
					clmDtl.setEligibleClaimAmt(claimApprovedAmt);

					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			} else if ("RT".equals(clmApplicationStatusFlag)) {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					unitName = (String) rs.getString(4);
					submittedDt = (Date) rs.getDate(5);
					returnRemark = (String) rs.getString(6);

					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					clmDtl.setSsiUnitName(unitName);
					clmDtl.setClmSubmittedDt(submittedDt);
					clmDtl.setReturnRemarks(returnRemark);
					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			} else {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					unitName = (String) rs.getString(4);
					submittedDt = (Date) rs.getDate(5);

					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					clmDtl.setSsiUnitName(unitName);
					clmDtl.setClmSubmittedDt(submittedDt);

					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			}
			prepStatement.close();
			prepStatement = null;
		} catch (SQLException sqlexception) {
			// sqlexception.printStackTrace();
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return clmRefNumbersList;
	}*/

	public Vector getListOfClaimRefNumbers(java.sql.Date fromDate,
			java.sql.Date toDate, String clmApplicationStatusFlag, String bankId)
			throws DatabaseException {

		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()", "Entered");
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"From Date :" + fromDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"To Date :" + toDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"clmApplicationStatusFlag :" + clmApplicationStatusFlag);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Bank Id :" + bankId);
		Connection conn = null;
		PreparedStatement prepStatement = null;
		ResultSet rs = null;
		Vector clmRefNumbersList = new Vector();
		String query = null;
		try {
			conn = DBConnection.getConnection();
			if ((fromDate != null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					/*
					 * capture borrower name and application submitted date by
					 * sukumar@path 0n 04-08-2009
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					/*
					 * capture borrower name and application submitted date by
					 * sukumar@path 0n 04-08-2009
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate != null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				/*
				 * capture borrower name and application submitted date by
				 * sukumar@path 0n 04-08-2009
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date between ? and ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and c.mem_bnk_id = ?"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, fromDate);
				prepStatement.setDate(2, toDate);
				prepStatement.setString(3, clmApplicationStatusFlag);
				prepStatement.setString(4, bankId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			if ((fromDate == null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					/*
					 * capture borrower name and application submitted date by
					 * sukumar@path 0n 04-08-2009
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					// prepStatement.setDate(1,fromDate);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					/*
					 * capture borrower name and application submitted date by
					 * sukumar@path 0n 04-08-2009
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate == null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				/*
				 * capture borrower name and application submitted date by
				 * sukumar@path 0n 04-08-2009
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date <= ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and c.mem_bnk_id = ?"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, toDate);
				prepStatement.setString(2, clmApplicationStatusFlag);
				prepStatement.setString(3, bankId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			String memberBankName = null;
			String memberId = null;
			String clmRefNumber = null;
			String cgclan = "";
			ClaimDetail clmDtl = null;

			if (clmApplicationStatusFlag
					.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					cgclan = (String) rs.getString(4);

					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);

					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			} else {
				while (rs.next()) {

					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);

					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);

					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			}
			prepStatement.close();
			prepStatement = null;
		} catch (SQLException sqlexception) {
			// sqlexception.printStackTrace();
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return clmRefNumbersList;
	}
	
	
	
	
	
	
    public Vector getListOfClaimRefNumbersNew(java.sql.Date fromDate,
            java.sql.Date toDate,
            String clmApplicationStatusFlag
            ) throws DatabaseException   {

Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbersNew()","Entered");
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbersNew()","From Date :" + fromDate);
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbersNew()","To Date :" + toDate);
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbersNew()","clmApplicationStatusFlag :" + clmApplicationStatusFlag);
Connection conn                 = null;
PreparedStatement prepStatement = null;
ResultSet rs                    = null;
Vector clmRefNumbersList        = new Vector();
String query                    = null;

try
{
conn = DBConnection.getConnection();

if((fromDate != null) && (!(clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS) ||
           clmApplicationStatusFlag.equals(ClaimConstants.CLM_HOLD_STATUS)
          )))
{
if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_APPROVAL_STATUS))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Approval");


//new mod query kot1
query="SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
"         DECODE (m.MEM_BRANCH_NAME,\n" + 
"                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"                 m.MEM_BRANCH_NAME)\n" + 
"            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
"                 NULL, a.APP_approved_amount,\n" + 
"                 a.app_reapprove_amount)\n" + 
"            GuarntAmt, c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,ctd_tc_first_inst_pay_amt clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
"                   FROM claim_detail c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         application_detail a,\n" + 
"         claim_tc_detail ct\n" + 
"   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ?  \n" + 
"         AND C.BID = S.BID\n" + 
"         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"         AND c.clm_status = ? \n" + 
"         and ct.clm_ref_no = c.clm_ref_no\n" + 
"         and ct.cgpan = a.cgpan\n" + 
"         and A.APP_LOAN_TYPE in ('TC')   \n" + 
"         union all\n" + 
"          SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
"         DECODE (m.MEM_BRANCH_NAME,\n" + 
"                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"                 m.MEM_BRANCH_NAME)\n" + 
"            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
"                 NULL, a.APP_approved_amount,\n" + 
"                 a.app_reapprove_amount)\n" + 
"            GuarntAmt,c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,cwd_wc_first_inst_pay_amt clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
"    FROM claim_detail c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         application_detail a,\n" + 
"         claim_wc_detail cw\n" + 
"   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ? \n" + 
"         AND C.BID = S.BID\n" + 
"         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"         AND c.clm_status = ? \n" + 
"         and cw.clm_ref_no = c.clm_ref_no\n" + 
"         and cw.cgpan = a.cgpan\n" + 
"         and app_loan_type in ('WC')  \n" + 
"union all\n" + 
" SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bankname, m.mem_zone_name,\n" + 
"         DECODE (m.MEM_BRANCH_NAME,\n" + 
"                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"                 m.MEM_BRANCH_NAME)\n" + 
"            BRANCH,a.cgpan,S.SSI_UNIT_NAME, DECODE (a.app_reapprove_amount,\n" + 
"                 NULL, a.APP_approved_amount,\n" + 
"                 a.app_reapprove_amount)\n" + 
"            GuarntAmt,c.clm_ref_no, c.cgclan,  c.CLM_DATE claimFilDt,  nvl(cwd_wc_first_inst_pay_amt,0)+nvl(ctd_tc_first_inst_pay_amt,0) clmApprvdAmt,TRUNC (C.CLM_APPROVED_DT) clmApprovdDt\n" + 
"    FROM claim_detail c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         application_detail a,\n" + 
"         claim_wc_detail cw,\n" + 
"         claim_tc_detail ct\n" + 
"   WHERE     TRUNC (c.clm_approved_dt) BETWEEN ? AND ?\n" + 
"         AND C.BID = S.BID\n" + 
"         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"         AND c.clm_status = ? \n" + 
"         and cw.clm_ref_no = c.clm_ref_no\n" + 
"         and cw.cgpan = a.cgpan\n" + 
"         and ct.clm_ref_no = c.clm_ref_no\n" + 
"         and ct.cgpan = a.cgpan\n" + 
"         and app_loan_type in ('CC')   order by clmApprovdDt,bankname ";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);
prepStatement.setDate(4,fromDate);
prepStatement.setDate(5,toDate);
prepStatement.setString(6,clmApplicationStatusFlag);
prepStatement.setDate(7,fromDate);
prepStatement.setDate(8,toDate);
prepStatement.setString(9,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}


//kotttttt

if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_CLOSE))
{
//kot5
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Reject");

query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,  \n" + 
"                                d.mem_bank_name,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,  \n" + 
"                                e.cgpan,  \n" + 
"                                c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmAppdAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT), \n" + 
"                                (select distinct max(trim(upper(replace(clm_ltr_ref_no,' ',''))))\n" + 
"                                from claim_query_detail cqd\n" + 
"                                where a.clm_ref_no = cqd.clm_ref_no\n" + 
"                                and clm_ltr_dt in \n" + 
"                                (select max(clm_ltr_dt) from claim_query_detail cd where cd.clm_ref_no = cqd.clm_ref_no)\n" + 
"                                ) clmqryrefno,\n" + 
"                                (select distinct clm_ltr_dt\n" + 
"                                from claim_query_detail cq\n" + 
"                                where a.clm_ref_no = cq.clm_ref_no\n" + 
"                                and clm_ltr_dt in \n" + 
"                                (select max(clm_ltr_dt) from claim_query_detail cd where cd.clm_ref_no = cq.clm_ref_no)\n" + 
"                                ) clmqrydt  \n" + 
"                           FROM claim_detail_temp a,  \n" + 
"                                claim_application_amount_temp b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                               AND TRUNC (CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status = ?  order by TRUNC(CLM_CREATED_MODIFIED_DT),d.mem_bank_name ";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}



if(clmApplicationStatusFlag.equals("RT"))
{
//kot9    
query=" SELECT  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bnkname,mem_zone_name zone,\n" + 
"         decode(mem_branch_name,null,app_mli_branch_name,mem_branch_name) branch,\n" + 
"                 a.cgpan, S.SSI_UNIT_NAME,\n" + 
"         decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_Reapprove_amount) appamt,\n" + 
"         c.clm_ref_no,\n" + 
"                 caa_applied_amount clmedamt,\n" + 
"         TRUNC (CLM_CREATED_MODIFIED_DT) rtdate,\n" + 
"         clm_return_remarks\n" + 
"    FROM claim_detail_temp c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         claim_application_amount_temp caa,\n" + 
"         application_detail a\n" + 
"   WHERE     TRUNC(C.CLM_CREATED_MODIFIED_DT) between ?  and ?  AND\n" + 
"             C.BID = S.BID\n" + 
"         AND c.clm_ref_no = caa.clm_ref_no\n" + 
"         AND caa.cgpan = a.cgpan\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"         AND c.clm_status = ?  \n" + 

"ORDER BY TRUNC (CLM_CREATED_MODIFIED_DT), bnkname";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}



if(clmApplicationStatusFlag.equals("AS"))
{
//kot11

query=" SELECT  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bnkname,mem_zone_name zone,\n" + 
"         decode(mem_branch_name,null,app_mli_branch_name,mem_branch_name) branch,\n" + 
"                 a.cgpan, S.SSI_UNIT_NAME,\n" + 
"         decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_Reapprove_amount) appamt,\n" + 
"         c.clm_ref_no,\n" + 
"                 caa_applied_amount clmApplAmt,CLM_DATE,\n" + 
"         clm_status,\n" + 
"clm_decl_recvd_dt,TRUNC(c.CLM_CREATED_MODIFIED_DT)  withdrawndt  \n" + 
"         \n" + 
"    FROM claim_detail_temp c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         claim_application_amount_temp caa,\n" + 
"         application_detail a\n" + 
"   WHERE     TRUNC(C.CLM_date) between ?  and ?   AND\n" + 
"             C.BID = S.BID\n" + 
"         AND c.clm_ref_no = caa.clm_ref_no\n" + 
"         AND caa.cgpan = a.cgpan\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))  \n" + 
"        \n" + 
" union  all \n" + 
" \n" + 
"  SELECT  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id memberid, m.mem_bank_name bnkname,mem_zone_name zone,\n" + 
"         decode(mem_branch_name,null,app_mli_branch_name,mem_branch_name) branch,\n" + 
"                 a.cgpan, S.SSI_UNIT_NAME,\n" + 
"         decode(nvl(app_reapprove_amount,0),0,app_approved_amount,app_Reapprove_amount) appamt,\n" + 
"         c.clm_ref_no,\n" + 
"                 caa_applied_amount clmApplAmt,CLM_DATE,\n" + 
"         clm_status,\n" + 
"clm_decl_recvd_dt,TRUNC(c.CLM_CREATED_MODIFIED_DT) withdrawndt \n" + 
"        \n" + 
"    FROM claim_detail c,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         claim_application_amount caa,\n" + 
"         application_detail a\n" + 
"   WHERE     TRUNC(C.CLM_date) between ?  and ?  AND\n" + 
"             C.BID = S.BID\n" + 
"         AND c.clm_ref_no = caa.clm_ref_no\n" + 
"         AND caa.cgpan = a.cgpan\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))  order by withdrawndt, bnkname \n" + 
"        " ;

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setDate(3,fromDate);
prepStatement.setDate(4,toDate);
// prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}




if(clmApplicationStatusFlag.equals("RTD"))
{
//kot10
query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,  \n" + 
"                                d.mem_bank_name bnkname,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,                                  e.cgpan,                                  c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmApplAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT) dt \n" + 
"                           FROM claim_detail_temp a,  \n" + 
"                                claim_application_amount_temp b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                            AND TRUNC (a.CLM_CREATED_MODIFIED_DT) bETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status in ('RE','TR')    union all \n" + 
" SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,d.mem_bank_name bnkname,d.mem_zone_name, DECODE (d.MEM_BRANCH_NAME,\n" + 
"                                           NULL, e.APP_MLI_BRANCH_NAME, \n" + 
"                                            d.MEM_BRANCH_NAME)\n" + 
"                                       branch,e.cgpan,c.ssi_unit_name, DECODE (e.app_reapprove_amount,\n" + 
"                                            NULL, e.APP_approved_amount,\n" + 
"                                            e.app_reapprove_amount)\n" + 
"                                       guarantamt,A.clm_ref_no, a.clm_date,b.caa_applied_amount clmApplAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT) dt \n" + 
"                                                 FROM claim_detail a,\n" + 
"                                    claim_application_amount b,\n" + 
"                                    ssi_detail c,\n" + 
"                                    member_info d,\n" + 
"                                    application_detail e\n" + 
"                              WHERE   \n" + 
"                                   b.cgpan = e.cgpan\n" + 
"                                    AND a.clm_ref_no = b.clm_ref_no\n" + 
"                                    AND a.bid = c.bid\n" + 
"                                    AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
"                                           d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
"                                    AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ? \n" + 
"                                    AND a.clm_status in ('RE','TR')  order by dt,bnkname ";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);                          
prepStatement.setDate(3,fromDate);
prepStatement.setDate(4,toDate);
//  prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}




if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_REJECT))
{
//kot6
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Reject");

query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,  \n" + 
"                                d.mem_bank_name,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,  \n" + 
"                                e.cgpan,  \n" + 
"                                c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmRejAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT) \n" + 
"                           FROM claim_detail_temp a,  \n" + 
"                                claim_application_amount_temp b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                               AND TRUNC (CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status = ?  order by TRUNC(CLM_CREATED_MODIFIED_DT), d.mem_bank_name ";
prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}
if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_WITHDRAWN))
{
//kot7
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Reject");

query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,  \n" + 
"                                d.mem_bank_name bnkname,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,  \n" + 
"                                e.cgpan,  \n" + 
"                                c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmAPPAmt , TRUNC(a.CLM_CREATED_MODIFIED_DT) withdrawndt \n" + 
"                           FROM claim_detail_temp a,  \n" + 
"                                claim_application_amount_temp b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                               AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status = ?     \n" + 
"union all \n" + 
"SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,  \n" + 
"                                d.mem_bank_name bnkname,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,  \n" + 
"                                e.cgpan,  \n" + 
"                                c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmappAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT) withdrawndt  \n" + 
"                           FROM claim_detail a,  \n" + 
"                                claim_application_amount b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                                AND TRUNC (CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status = ?  order by withdrawndt, bnkname  ";
prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);
prepStatement.setDate(4,fromDate);
prepStatement.setDate(5,toDate);
prepStatement.setString(6,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}



if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_REJECT_STATUS))
{
//kot3
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Reject");

query="  SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,d.mem_bank_name,d.mem_zone_name, DECODE (d.MEM_BRANCH_NAME,\n" + 
"                 NULL, e.APP_MLI_BRANCH_NAME,\n" + 
"                 d.MEM_BRANCH_NAME)\n" + 
"            branch,e.cgpan,c.ssi_unit_name, DECODE (e.app_reapprove_amount,\n" + 
"                 NULL, e.APP_approved_amount,\n" + 
"                 e.app_reapprove_amount)\n" + 
"            guarantamt,A.clm_ref_no, a.clm_date,b.caa_applied_amount clmAppdAmt, TRUNC(a.CLM_CREATED_MODIFIED_DT) \n" + 
"                      FROM claim_detail a,\n" + 
"         claim_application_amount b,\n" + 
"         ssi_detail c,\n" + 
"         member_info d,\n" + 
"         application_detail e\n" + 
"   WHERE   \n" + 
"        b.cgpan = e.cgpan\n" + 
"         AND a.clm_ref_no = b.clm_ref_no\n" + 
"         AND a.bid = c.bid\n" + 
"         AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
"                d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
"         AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ? \n" + 
"         AND a.clm_status = ?   order by TRUNC(CLM_CREATED_MODIFIED_DT), d.mem_bank_name  ";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}
//ADDED FORWARD REPORT BY SUKUMAR@PATH ON 12-09-2009
if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is  Forward");
//kot4
query="SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
"       m.mem_bank_name bnkname,\n" + 
"       m.mem_zone_name,\n" + 
"       DECODE (m.MEM_BRANCH_NAME,\n" + 
"               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"               m.MEM_BRANCH_NAME)\n" + 
"          BRANCH,\n" + 
"       d.cgpan,\n" + 
"       S.SSI_UNIT_NAME,\n" + 
"       DECODE (a.app_reapprove_amount,\n" + 
"               NULL, a.APP_approved_amount,\n" + 
"               a.app_reapprove_amount)\n" + 
"          guartAmt,\n" + 
"       c.clm_ref_no,\n" + 
"       C.CLM_DATE clmFilgDt,\n" + 
"       d.ctd_tc_first_inst_pay_amt clmForwrdAmt, \n" + 
"       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt \n" + 
"  FROM claim_detail_temp c,\n" + 
"       member_info m,\n" + 
"       SSI_DETAIL S,\n" + 
"       application_detail a,\n" + 
"       CLAIM_TC_DETAIL_TEMP D\n" + 
" WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
"                                                 AND  ? \n" + 
"       AND C.BID = S.BID\n" + 
"       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"       AND LTRIM (\n" + 
"              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"              LTRIM (\n" + 
"                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"       AND c.clm_status = ? \n" + 
"       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
"       AND d.cgpan = a.cgpan\n" + 
"       AND app_loan_type IN ('TC')   \n" + 
"UNION ALL\n" + 
"SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
"       m.mem_bank_name bnkname,\n" + 
"       m.mem_zone_name,\n" + 
"       DECODE (m.MEM_BRANCH_NAME,\n" + 
"               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"               m.MEM_BRANCH_NAME)\n" + 
"          BRANCH,\n" + 
"       d.cgpan,\n" + 
"       S.SSI_UNIT_NAME,\n" + 
"       DECODE (a.app_reapprove_amount,\n" + 
"               NULL, a.APP_approved_amount,\n" + 
"               a.app_reapprove_amount)\n" + 
"          guartAmt,\n" + 
"       c.clm_ref_no,\n" + 
"       C.CLM_DATE clmFilgDt,\n" + 
"       d.cwd_wc_first_inst_pay_amt clmForwrdAmt,\n" + 
"       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt \n" + 
"  FROM claim_detail_temp c,\n" + 
"       member_info m,\n" + 
"       SSI_DETAIL S,\n" + 
"       application_detail a,\n" + 
"       CLAIM_WC_DETAIL_TEMP D\n" + 
" WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
"                                                 AND ?  \n" + 
"       AND C.BID = S.BID\n" + 
"       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"       AND LTRIM (\n" + 
"              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"              LTRIM (\n" + 
"                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"       AND c.clm_status = ? \n" + 
"       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
"       AND d.cgpan = a.cgpan\n" + 
"       AND app_loan_type IN ('WC')   \n" + 
"UNION ALL\n" + 
"SELECT m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,\n" + 
"       m.mem_bank_name bnkname,\n" + 
"       m.mem_zone_name,\n" + 
"       DECODE (m.MEM_BRANCH_NAME,\n" + 
"               NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"               m.MEM_BRANCH_NAME)\n" + 
"          BRANCH,\n" + 
"       d.cgpan,\n" + 
"       S.SSI_UNIT_NAME,\n" + 
"       DECODE (a.app_reapprove_amount,\n" + 
"               NULL, a.APP_approved_amount,\n" + 
"               a.app_reapprove_amount)\n" + 
"          guartAmt,\n" + 
"       c.clm_ref_no,\n" + 
"       C.CLM_DATE clmFilgDt,\n" + 
"         NVL (d.cwd_wc_first_inst_pay_amt, 0)\n" + 
"       + NVL (CWD_WC_FIRST_INST_PAY_AMT, 0)\n" + 
"          clmForwrdAmt,\n" + 
"       TRUNC (CLM_CREATED_MODIFIED_DT) clmForwdDt \n" + 
"  FROM claim_detail_temp c,\n" + 
"       member_info m,\n" + 
"       SSI_DETAIL S,\n" + 
"       application_detail a,\n" + 
"       CLAIM_WC_DETAIL_TEMP D,\n" + 
"       CLAIM_TC_DETAIL_TEMP E\n" + 
" WHERE     TRUNC (C.CLM_CREATED_MODIFIED_DT) BETWEEN ? \n" + 
"                                                 AND ? \n" + 
"       AND C.BID = S.BID\n" + 
"       AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"       AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"              a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"       AND LTRIM (\n" + 
"              RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"              LTRIM (\n" + 
"                 RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"       AND c.clm_status = ? \n" + 
"       AND C.CLM_REF_NO = D.CLM_REF_NO\n" + 
"       AND D.cgpan = a.cgpan\n" + 
"       AND C.CLM_REF_NO = E.CLM_REF_NO\n" + 
"       AND E.cgpan = a.cgpan\n" + 
"       AND app_loan_type IN ('CC') order by clmForwdDt, bnkname ";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);
prepStatement.setDate(4,fromDate);
prepStatement.setDate(5,toDate);
prepStatement.setString(6,clmApplicationStatusFlag);
prepStatement.setDate(7,fromDate);
prepStatement.setDate(8,toDate);
prepStatement.setString(9,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
/* ADDED BY SUKUMAR@PATH ON 20-FEB-2010 FOR DISPLAY TEMPORARY CLOSE & TEMPORARY REJECT APPLICATION */
if(clmApplicationStatusFlag.equals("KTC")||clmApplicationStatusFlag.equals("KWC") || clmApplicationStatusFlag.equals("KWD"))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is  TEMPORARY CLOSED");
//new kot
query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,\n" + 
"       d.mem_bank_name,\n" + 
"       d.mem_zone_name,\n" + 
"       DECODE (d.MEM_BRANCH_NAME,\n" + 
"               NULL, e.APP_MLI_BRANCH_NAME,\n" + 
"               d.MEM_BRANCH_NAME)\n" + 
"          branch,\n" + 
"       e.cgpan,\n" + 
"       c.ssi_unit_name,\n" + 
"       DECODE (e.app_reapprove_amount,\n" + 
"               NULL, e.APP_approved_amount,\n" + 
"               e.app_reapprove_amount)\n" + 
"          guarantamt,\n" + 
"       A.clm_ref_no,\n" + 
"       a.clm_date,\n" + 
"       b.caa_applied_amount clmAppdAmt\n" + 
"  FROM claim_detail_temp a,\n" + 
"       claim_application_amount_temp b,\n" + 
"       ssi_detail c,\n" + 
"       member_info d,\n" + 
"       application_detail e\n" + 
" WHERE     b.cgpan = e.cgpan\n" + 
"       AND a.clm_ref_no = b.clm_ref_no\n" + 
"       AND a.bid = c.bid\n" + 
"       AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
"              d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
"       AND TRUNC (CLM_DATE) BETWEEN ?  AND ? \n" + 
"       AND a.clm_status = ? \n";

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}
//added by upchar@path on 03/07/2013 to display reply received claim applications
if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_REPLY_RECEIVED))
{
//kot8
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is  TEMPORARY CLOSED");

query="SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id memberid,  \n" + 
"                                d.mem_bank_name,  \n" + 
"                                d.mem_zone_name,  \n" + 
"                                DECODE (d.MEM_BRANCH_NAME,  \n" + 
"                                        NULL, e.APP_MLI_BRANCH_NAME,  \n" + 
"                                        d.MEM_BRANCH_NAME)  \n" + 
"                                   branch,  \n" + 
"                                e.cgpan,  \n" + 
"                                c.ssi_unit_name,  \n" + 
"                                DECODE (e.app_reapprove_amount,  \n" + 
"                                        NULL, e.APP_approved_amount,  \n" + 
"                                        e.app_reapprove_amount)  \n" + 
"                                   guarantamt,  \n" + 
"                                A.clm_ref_no,  \n" + 
"                                a.clm_date,  \n" + 
"                                b.caa_applied_amount clmAppdAmt,TRUNC(a.CLM_CREATED_MODIFIED_DT), \n" + 
"                                (select distinct max(trim(upper(replace(clm_ltr_ref_no,' ',''))))\n" + 
"                                from claim_query_detail cqd\n" + 
"                                where a.clm_ref_no = cqd.clm_ref_no\n" + 
"                                and clm_ltr_dt in \n" + 
"                                (select max(clm_ltr_dt) from claim_query_detail cd where cd.clm_ref_no = cqd.clm_ref_no)\n" + 
"                                ) clmqryrefno,\n" + 
"                                (select distinct clm_ltr_dt\n" + 
"                                from claim_query_detail cq\n" + 
"                                where a.clm_ref_no = cq.clm_ref_no\n" + 
"                                and clm_ltr_dt in \n" + 
"                                (select max(clm_ltr_dt) from claim_query_detail cd where cd.clm_ref_no = cq.clm_ref_no)\n" + 
"                                ) clmqrydt,\n" + 
"                                (select inward_id from claim_reply_detail crd\n" + 
"                                where crd.clm_ref_no = a.clm_ref_no\n" + 
"                                and inward_dt in \n" + 
"                                (select max(inward_dt) from claim_reply_detail cd where cd.clm_ref_no = crd.clm_ref_no)\n" + 
"                                ) rplyinwid,\n" + 
"                                (select inward_dt from claim_reply_detail crd\n" + 
"                                where crd.clm_ref_no = a.clm_ref_no\n" + 
"                                and inward_dt in \n" + 
"                                (select max(inward_dt) from claim_reply_detail cd where cd.clm_ref_no = crd.clm_ref_no)\n" + 
"                                ) rplyinwdt ,TRUNC(a.CLM_CREATED_MODIFIED_DT) \n" + 
"                           FROM claim_detail_temp a,  \n" + 
"                                claim_application_amount_temp b,  \n" + 
"                                ssi_detail c,  \n" + 
"                                member_info d,  \n" + 
"                                application_detail e  \n" + 
"                          WHERE     b.cgpan = e.cgpan  \n" + 
"                                AND a.clm_ref_no = b.clm_ref_no  \n" + 
"                                AND a.bid = c.bid  \n" + 
"                                AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  \n" + 
"                                       d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id  \n" + 
"                               AND TRUNC (CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ?   \n" + 
"                                AND a.clm_status = ?   order by TRUNC(CLM_CREATED_MODIFIED_DT),d.mem_bank_name ";


prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
}
if((fromDate != null) && ((clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS) ||
           clmApplicationStatusFlag.equals(ClaimConstants.CLM_HOLD_STATUS)
         //  clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
          )))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Pending or Forward or Hold");
//new kot2
query="SELECT  m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,m.mem_bank_name bnkname,m.mem_zone_name, DECODE (m.MEM_BRANCH_NAME,\n" + 
"                 NULL, a.APP_MLI_BRANCH_NAME,\n" + 
"                 m.MEM_BRANCH_NAME)\n" + 
"            BRANCH,a.cgpan,S.SSI_UNIT_NAME,DECODE (a.app_reapprove_amount,\n" + 
"                 NULL, a.APP_approved_amount,\n" + 
"                 a.app_reapprove_amount)\n" + 
"            GuarAmt,c.clm_ref_no, C.CLM_DATE, b.caa_applied_amount clmAppliedAmt,C.CLM_DECLARATION_RECVD clmDeclRcvdFlg, C.CLM_DECL_RECVD_DT clmDeclRecvdDt\n" + 
"                                      \n" + 
"    FROM claim_detail_temp c,\n" + 
"    claim_application_amount_temp b,\n" + 
"         member_info m,\n" + 
"         SSI_DETAIL S,\n" + 
"         application_detail a\n" + 
"         \n" + 
"   WHERE     TRUNC (c.clm_date) BETWEEN ?  AND ? \n" + 
"         AND C.BID = S.BID\n" + 
"         AND s.ssi_reference_number = a.ssi_reference_number\n" + 
"         AND c.mem_bnk_id || c.mem_brn_id || c.mem_zne_id =\n" + 
"                a.mem_bnk_id || a.mem_brn_id || a.mem_zne_id\n" + 
"         AND LTRIM (\n" + 
"                RTRIM (UPPER (c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) =\n" + 
"                LTRIM (\n" + 
"                   RTRIM (UPPER (m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id)))\n" + 
"         AND c.clm_status = ? \n" + 
"         AND C.CLM_REF_NO = b.CLM_REF_NO\n" + 
"         AND b.CGPAN = a.CGPAN  order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname ";
  

prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}
if((fromDate == null) && (!(clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS) ||
           clmApplicationStatusFlag.equals(ClaimConstants.CLM_HOLD_STATUS)
         //  clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
          )))
{
if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_APPROVAL_STATUS))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Approval");
query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan,S.SSI_UNIT_NAME,TRUNC(CLM_APPROVED_DT),CLM_APPROVED_AMT " +
" from claim_detail c, member_info m,SSI_DETAIL S " +
//code changed clm_date to clm_approved_dt by sukumar@path on 11-09-2009
//    " where c.clm_date <= ? AND C.BID=S.BID " +
" where TRUNC(c.clm_approved_dt) <= ? AND C.BID=S.BID " +
" and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) " +
" and c.clm_status = ?" +
" group by m.mem_bank_name," +
" m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id," +
" c.clm_ref_no," +
" c.cgclan,S.SSI_UNIT_NAME,TRUNC(CLM_APPROVED_DT),CLM_APPROVED_AMT order by Trunc(c.clm_approved_dt),bnkname";
prepStatement = conn.prepareStatement(query);
// prepStatement.setDate(1,fromDate);
prepStatement.setDate(1,toDate);
prepStatement.setString(2,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
else if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_REJECT_STATUS))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Reject");

query="  SELECT a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,d.mem_bank_name,d.mem_zone_name, DECODE (d.MEM_BRANCH_NAME,\n" + 
"                 NULL, e.APP_MLI_BRANCH_NAME,\n" + 
"                 d.MEM_BRANCH_NAME)\n" + 
"            branch,e.cgpan,c.ssi_unit_name, DECODE (e.app_reapprove_amount,\n" + 
"                 NULL, e.APP_approved_amount,\n" + 
"                 e.app_reapprove_amount)\n" + 
"            guarantamt,A.clm_ref_no, a.clm_date,b.caa_applied_amount clmAppdAmt \n" + 
"                      FROM claim_detail a,\n" + 
"         claim_application_amount b,\n" + 
"         ssi_detail c,\n" + 
"         member_info d,\n" + 
"         application_detail e\n" + 
"   WHERE   \n" + 
"        b.cgpan = e.cgpan\n" + 
"         AND a.clm_ref_no = b.clm_ref_no\n" + 
"         AND a.bid = c.bid\n" + 
"         AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =\n" + 
"                d.mem_bnk_id || d.mem_zne_id || d.mem_brn_id\n" + 
"         AND TRUNC (a.CLM_CREATED_MODIFIED_DT) BETWEEN ?  AND ? \n" + 
"         AND a.clm_status = ? ";


prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,fromDate);
prepStatement.setDate(2,toDate);
prepStatement.setString(3,clmApplicationStatusFlag);

rs = (ResultSet)prepStatement.executeQuery();
}
else if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Forward");
query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) " +
" from claim_detail_temp c, member_info m,SSI_DETAIL S" +
" where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID " +
" and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) " +
" and c.clm_status = ?" +
" group by m.mem_bank_name," +
" m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id," +
" c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";


prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,toDate);
prepStatement.setString(2,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
else if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)||clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_REJECT) || clmApplicationStatusFlag.equals(ClaimConstants.CLM_WITHDRAWN) )
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Forward");
query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) " +
" from claim_detail_temp c, member_info m,SSI_DETAIL S" +
" where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID " +
" and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) " +
" and c.clm_status = ?" +
" group by m.mem_bank_name," +
" m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id," +
" c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,toDate);
prepStatement.setString(2,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
else if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_REPLY_RECEIVED))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is  TEMPORARY CLOSED");
query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) " +
" from claim_detail_temp c, member_info m,SSI_DETAIL S" +
" where TRUNC(C.CLM_CREATED_MODIFIED_DT) <= ? AND C.BID=S.BID " +
" and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) " +
" and c.clm_status = ?" +
" group by m.mem_bank_name," +
" m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id," +
" c.clm_ref_no,S.SSI_UNIT_NAME,TRUNC(CLM_CREATED_MODIFIED_DT) order by TRUNC(CLM_CREATED_MODIFIED_DT),bnkname";
prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,toDate);
prepStatement.setString(2,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
}
if((fromDate == null) && ((clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS) ||
           clmApplicationStatusFlag.equals(ClaimConstants.CLM_HOLD_STATUS)
       //    clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS)
          )))
{
Log.log(Log.INFO,"ReportDAO","getListOfClaimRefNumbers()","From Date is NULL, Status is Pending or Forward or Hold");
query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE " +
" from claim_detail_temp c, member_info m,SSI_DETAIL S" +
" where c.clm_date <= ? AND C.BID=S.BID " +
" and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) " +
" and c.clm_status = ?" +
" group by m.mem_bank_name," +
" m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id," +
" c.clm_ref_no,S.SSI_UNIT_NAME,CLM_DATE order by CLM_DATE,bnkname";
prepStatement = conn.prepareStatement(query);
prepStatement.setDate(1,toDate);
prepStatement.setString(2,clmApplicationStatusFlag);
rs = (ResultSet)prepStatement.executeQuery();
}
String memberBankName = null;
String memberId       = null;
String clmRefNumber   = null;
String cgclan         = "";
ClaimDetail clmDtl    = null;
String unitName = "";
java.util.Date submittedDt = null;
java.util.Date clmLodgmentdDt = null;
java.util.Date claimForwardedDt = null;

String claimRetRemarks = "";
java.util.Date claimReturnDate = null;


Double claimForwdAmt=null;

java.util.Date claimRejectedDt = null;

java.util.Date tempRejOrRejDt = null;


java.util.Date claimReplyRecvdDate = null;

double claimAppliedAmt=0.0;


double claimRejAmount=0.0;

String clmQryRefNumber="";

java.util.Date  clamQryDate=null;

String clmDeclRecvdFlag=null;
java.util.Date  clmDeclRecvdDt=null;

java.util.Date  lastActionTakenDt=null;

String claimStatus="";

String replyInwardId="";

java.util.Date  replyInwardDt=null;

java.util.Date  tempClosedDate=null;

java.util.Date  tempRejectedDate=null;





double claimApprovedAmt = 0.0;

double totalClmApprovedAmt=0.0;

double totGrandClmApprovedAmtTemp=0.0;

double totGrandClmApprovedAmt=0.0;



String zoneName=null;
String branchName=null;
double guarApprdAmount=0.0;

double totalGuarnteAmt=0.0;

double totgrandTotal=0.0;

double totgrandTotalTemp=0.0;


double claimDecRecvdAmt=0.0;
String claimDecRecvdflag=null;
java.util.Date claimDecRecvdDt=null;
java.util.Date claimWithDrawnDt=null;
String claimRemarks=null;
String cgpan=null;
java.util.Date clmApprovedDt=null;


if(clmApplicationStatusFlag.equals(ClaimConstants.CLM_APPROVAL_STATUS))
{
while(rs.next())
{



memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
cgclan          = (String)rs.getString(9);
clmLodgmentdDt=(Date)rs.getDate(10);
claimApprovedAmt = (double)rs.getDouble(11);
clmApprovedDt=(Date)rs.getDate(12);

totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimApprovedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 



clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setCGCLAN(cgclan);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClaimApprovedAmount(claimApprovedAmt);
clmDtl.setClaimApprovedDt(clmApprovedDt);
clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);

// Adding the ClaimDetail object to the vector
clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
} else if


(clmApplicationStatusFlag.equals(ClaimConstants.CLM_PENDING_STATUS))
{
while(rs.next())
{

memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
clmLodgmentdDt=(Date)rs.getDate(9);
claimAppliedAmt=(Double)rs.getDouble(10);
clmDeclRecvdFlag=(String)rs.getString(11);
clmDeclRecvdDt=(Date)rs.getDate(12);

totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 




clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClaimAppliedAmt(claimAppliedAmt);
clmDtl.setClmDeclRecvdFlag(clmDeclRecvdFlag);
clmDtl.setClmDeclRecvdDt(clmDeclRecvdDt);

clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);


// Adding the ClaimDetail object to the vector
clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
} else if



(clmApplicationStatusFlag.equals(ClaimConstants.CLM_WITHDRAWN))
{
while(rs.next())
{
 
 memberId        = (String)rs.getString(1);
 memberBankName  = (String)rs.getString(2);
 zoneName=(String)rs.getString(3);
 branchName=(String)rs.getString(4);
 cgpan=(String)rs.getString(5);
 unitName        = (String)rs.getString(6);
 guarApprdAmount=(Double)rs.getDouble(7);
 clmRefNumber    = (String)rs.getString(8);
 clmLodgmentdDt=(Date)rs.getDate(9);
 claimAppliedAmt=(Double)rs.getDouble(10);
 claimWithDrawnDt=(Date)rs.getDate(11);

 totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
 totgrandTotalTemp=totalGuarnteAmt/100000;                     
 totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
  totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
 DecimalFormat decFormat = new DecimalFormat("#.00");  
 totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
 totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 
 
 
 clmDtl          = new ClaimDetail();
 
 clmDtl.setMliId(memberId);
 clmDtl.setMliName(memberBankName);
 clmDtl.setBranchName(branchName);
 clmDtl.setZoneName(zoneName);
 clmDtl.setCgpan(cgpan);
 clmDtl.setSsiUnitName(unitName);
 clmDtl.setApplicationApprovedAmount(guarApprdAmount);
 clmDtl.setClaimRefNum(clmRefNumber);
 clmDtl.setClmSubmittedDt(clmLodgmentdDt);
 clmDtl.setClaimAppliedAmt(claimAppliedAmt);
 clmDtl.setClaimWithDrawnDate(claimWithDrawnDt);
 
 clmDtl.setTotgrandTotal(totgrandTotal);                       
 clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);
     
  // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
  
  
   // Adding the ClaimDetail object to the vector
   clmRefNumbersList.addElement(clmDtl);
 
}
rs.close();
rs = null;
}
else if
    
    
        
            (clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_CLOSE))
            {
              while(rs.next())
              {
              
                  
              
                  memberId        = (String)rs.getString(1);
                  memberBankName  = (String)rs.getString(2);
                  zoneName=(String)rs.getString(3);
                  branchName=(String)rs.getString(4);
                  cgpan=(String)rs.getString(5);
                  unitName        = (String)rs.getString(6);
                  guarApprdAmount=(Double)rs.getDouble(7);
                  clmRefNumber    = (String)rs.getString(8);
                  clmLodgmentdDt=(Date)rs.getDate(9);
                  claimAppliedAmt=(Double)rs.getDouble(10);
                  tempClosedDate=(Date)rs.getDate(11);
                  
                  clmQryRefNumber    = (String)rs.getString(12);
                  
                  clamQryDate=(Date)rs.getDate(13);
                  
                  totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
                  totgrandTotalTemp=totalGuarnteAmt/100000;                     
                  totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
                   totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
                  DecimalFormat decFormat = new DecimalFormat("#.00");  
                  totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
                  totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 
                  
                   
                  clmDtl          = new ClaimDetail();
                  
                  clmDtl.setMliId(memberId);
                  clmDtl.setMliName(memberBankName);
                  clmDtl.setBranchName(branchName);
                  clmDtl.setZoneName(zoneName);
                  clmDtl.setCgpan(cgpan);
                  clmDtl.setSsiUnitName(unitName);
                  clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                  clmDtl.setClaimRefNum(clmRefNumber);
                  clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                  clmDtl.setClaimAppliedAmt(claimAppliedAmt);
                  clmDtl.setTempClosedDate(tempClosedDate);
                  
                  clmDtl.setClmQryRefNumber(clmQryRefNumber);
                  clmDtl.setClamQryDate(clamQryDate);
                  clmDtl.setTotgrandTotal(totgrandTotal);                       
                  clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);
                      
                       
                    // Adding the ClaimDetail object to the vector
                    clmRefNumbersList.addElement(clmDtl);
                  
              }
              rs.close();
              rs = null;
            }
            
else if
    
    
        
            (clmApplicationStatusFlag.equals(ClaimConstants.CLM_TEMPORARY_REJECT))
            {
              while(rs.next())
              {
              
                   memberId        = (String)rs.getString(1);
                  memberBankName  = (String)rs.getString(2);
                  zoneName=(String)rs.getString(3);
                  branchName=(String)rs.getString(4);
                  cgpan=(String)rs.getString(5);
                  unitName        = (String)rs.getString(6);
                  guarApprdAmount=(Double)rs.getDouble(7);
                  clmRefNumber    = (String)rs.getString(8);
                  clmLodgmentdDt=(Date)rs.getDate(9);
                  claimRejAmount=(Double)rs.getDouble(10);
                  tempRejectedDate=(Date)rs.getDate(11);
                  
                  totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
                  totgrandTotalTemp=totalGuarnteAmt/100000;                     
                  totalClmApprovedAmt=totalClmApprovedAmt+claimRejAmount;                       
                   totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
                  DecimalFormat decFormat = new DecimalFormat("#.00");  
                  totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
                  totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 
                
                    
                  clmDtl          = new ClaimDetail();
                  
                  clmDtl.setMliId(memberId);
                  clmDtl.setMliName(memberBankName);
                  clmDtl.setBranchName(branchName);
                  clmDtl.setZoneName(zoneName);
                  clmDtl.setCgpan(cgpan);
                  clmDtl.setSsiUnitName(unitName);
                  clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                  clmDtl.setClaimRefNum(clmRefNumber);
                  clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                  clmDtl.setClaimAppliedAmt(claimRejAmount);
                  clmDtl.setTempRejectedDate(tempRejectedDate);
                  
                  
                  clmDtl.setTotgrandTotal(totgrandTotal);                       
                  clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);
                      
                   // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                   
                   
                    // Adding the ClaimDetail object to the vector
                    clmRefNumbersList.addElement(clmDtl);
                  
              }
              rs.close();
              rs = null;
            }
            
            
             
             else if
                                
                                
                                    
                                        (clmApplicationStatusFlag.equals(ClaimConstants.CLM_REPLY_RECEIVED))
                                        {
                                          while(rs.next())
                                          {
                                          
                                           
                                              memberId        = (String)rs.getString(1);
                                              memberBankName  = (String)rs.getString(2);
                                              zoneName=(String)rs.getString(3);
                                              branchName=(String)rs.getString(4);
                                              cgpan=(String)rs.getString(5);
                                              unitName        = (String)rs.getString(6);
                                              guarApprdAmount=(Double)rs.getDouble(7);
                                              clmRefNumber    = (String)rs.getString(8);
                                              clmLodgmentdDt=(Date)rs.getDate(9);
                                              claimApprovedAmt=(Double)rs.getDouble(10);    
                                              claimReplyRecvdDate=(Date)rs.getDate(11);
                                              clmQryRefNumber    = (String)rs.getString(12);                                                                              
                                              clamQryDate=(Date)rs.getDate(13);                                                                              
                                              replyInwardId    = (String)rs.getString(14);                                                                              
                                              replyInwardDt=(Date)rs.getDate(15);
                                              
                                              totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
                                              totgrandTotalTemp=totalGuarnteAmt/100000;                     
                                              totalClmApprovedAmt=totalClmApprovedAmt+claimApprovedAmt;                       
                                               totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
                                              DecimalFormat decFormat = new DecimalFormat("#.00");  
                                              totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
                                              totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 
                                                 
                                              
                                              
                                              clmDtl          = new ClaimDetail();
                                              
                                              clmDtl.setMliId(memberId);
                                              clmDtl.setMliName(memberBankName);
                                              clmDtl.setBranchName(branchName);
                                              clmDtl.setZoneName(zoneName);
                                              clmDtl.setCgpan(cgpan);
                                              clmDtl.setSsiUnitName(unitName);
                                              clmDtl.setApplicationApprovedAmount(guarApprdAmount);
                                              clmDtl.setClaimRefNum(clmRefNumber);
                                              clmDtl.setClmSubmittedDt(clmLodgmentdDt);
                                              clmDtl.setClaimApprovedAmount(claimApprovedAmt);
                                              clmDtl.setClaimReplyRecvdDate(claimReplyRecvdDate);
                                              clmDtl.setClmQryRefNumber(clmQryRefNumber);
                                              clmDtl.setClamQryDate(clamQryDate);
                                              
                                              
                                              
                                              clmDtl.setReplyInwardId(replyInwardId);
                                              clmDtl.setReplyInwardDt(replyInwardDt);
                                              clmDtl.setTotgrandTotal(totgrandTotal);                       
                                              clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);
                                                  
                                               // clmDtl.setClaimLodgmentDate(clmLodgmentdDt);
                                               
                                               
                                                // Adding the ClaimDetail object to the vector
                                                clmRefNumbersList.addElement(clmDtl);
                                              
                                          }
                                          rs.close();
                                          rs = null;
                                        }
                

else if


(clmApplicationStatusFlag.equals(ClaimConstants.CLM_REJECT_STATUS))
{
while(rs.next())
{

memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
clmLodgmentdDt=(Date)rs.getDate(9);
claimAppliedAmt=(Double)rs.getDouble(10);
claimRejectedDt=(Date)rs.getDate(11);

totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 



clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClaimAppliedAmt(claimAppliedAmt);
clmDtl.setClaimRejectedDt(claimRejectedDt);

clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);
 
 // Adding the ClaimDetail object to the vector
clmRefNumbersList.addElement(clmDtl);


}
rs.close();
rs = null;
}

else if


(clmApplicationStatusFlag.equals("RT"))
{
while(rs.next())
{                     


memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
claimAppliedAmt=(Double)rs.getDouble(9);
claimReturnDate=(Date)rs.getDate(10);
claimRetRemarks    = (String)rs.getString(11);

totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 




clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClaimAppliedAmt(claimAppliedAmt);
clmDtl.setClaimReturnDate(claimReturnDate);
clmDtl.setClaimRetRemarks(claimRetRemarks);
clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);



clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
}




else if


(clmApplicationStatusFlag.equals("RTD"))
{
while(rs.next())
{                     


memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
clmLodgmentdDt=(Date)rs.getDate(9);
claimAppliedAmt=(Double)rs.getDouble(10);
tempRejOrRejDt=(Date)rs.getDate(11);


totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 



clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClaimAppliedAmt(claimAppliedAmt);

clmDtl.setTempRejOrRejDt(tempRejOrRejDt);
clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);





clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
}



else if


(clmApplicationStatusFlag.equals("AS"))
{
while(rs.next())
{                     



memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
claimAppliedAmt=(Double)rs.getDouble(9);
clmLodgmentdDt=(Date)rs.getDate(10);
claimStatus=(String)rs.getString(11);

clmDeclRecvdDt=(Date)rs.getDate(12);

lastActionTakenDt=(Date)rs.getDate(13);

totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimAppliedAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 




clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClaimAppliedAmt(claimAppliedAmt);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClmStatus(claimStatus);
clmDtl.setClmDeclRecvdDt(clmDeclRecvdDt);

clmDtl.setLastActionTakenDt(lastActionTakenDt);


clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);




clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
}


else if


(clmApplicationStatusFlag.equals(ClaimConstants.CLM_FORWARD_STATUS))
{
while(rs.next())
{


memberId        = (String)rs.getString(1);
memberBankName  = (String)rs.getString(2);
zoneName=(String)rs.getString(3);
branchName=(String)rs.getString(4);
cgpan=(String)rs.getString(5);
unitName        = (String)rs.getString(6);
guarApprdAmount=(Double)rs.getDouble(7);
clmRefNumber    = (String)rs.getString(8);
clmLodgmentdDt=(Date)rs.getDate(9);
claimForwdAmt = (double)rs.getDouble(10);
claimForwardedDt=(Date)rs.getDate(11);

              
totalGuarnteAmt=totalGuarnteAmt+guarApprdAmount;                       
totgrandTotalTemp=totalGuarnteAmt/100000;                     
totalClmApprovedAmt=totalClmApprovedAmt+claimForwdAmt;                       
totGrandClmApprovedAmtTemp=totalClmApprovedAmt/100000;                         
DecimalFormat decFormat = new DecimalFormat("#.00");  
totGrandClmApprovedAmt = new Double(decFormat.format(totGrandClmApprovedAmtTemp)).doubleValue();                         
totgrandTotal = new Double(decFormat.format(totgrandTotalTemp)).doubleValue(); 



clmDtl          = new ClaimDetail();

clmDtl.setMliId(memberId);
clmDtl.setMliName(memberBankName);
clmDtl.setBranchName(branchName);
clmDtl.setZoneName(zoneName);
clmDtl.setCgpan(cgpan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setClmSubmittedDt(clmLodgmentdDt);
clmDtl.setClaimForwdAmt(claimForwdAmt);
clmDtl.setClaimForwardedDt(claimForwardedDt);
clmDtl.setTotgrandTotal(totgrandTotal);                       
clmDtl.setTotGrandClmApprovedAmt(totGrandClmApprovedAmt);


// Adding the ClaimDetail object to the vector
clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
}


else 
{
while(rs.next())
{
memberBankName  = (String)rs.getString(1);
memberId        = (String)rs.getString(2);
clmRefNumber    = (String)rs.getString(3);
unitName        = (String)rs.getString(4);
submittedDt     = (Date)rs.getDate(5);

zoneName=(String)rs.getString(6);
branchName=(String)rs.getString(7);
guarApprdAmount=(Double)rs.getDouble(8);




clmDtl          = new ClaimDetail();
clmDtl.setMliName(memberBankName);
clmDtl.setMliId(memberId);
clmDtl.setClaimRefNum(clmRefNumber);
clmDtl.setCGCLAN(cgclan);
clmDtl.setSsiUnitName(unitName);
clmDtl.setClmSubmittedDt(submittedDt);

clmDtl.setZoneName(zoneName);
clmDtl.setBranchName(branchName);
clmDtl.setApplicationApprovedAmount(guarApprdAmount);




// Adding the ClaimDetail object to the vector
clmRefNumbersList.addElement(clmDtl);
}
rs.close();
rs = null;
}
prepStatement.close();
prepStatement=null;
}
catch(SQLException sqlexception)
{
// sqlexception.printStackTrace();
throw new DatabaseException(sqlexception.getMessage());
}
finally{
DBConnection.freeConnection(conn);
}
return clmRefNumbersList;
    }

	public Vector getListOfClaimRefNumbers(java.sql.Date fromDate,
			java.sql.Date toDate, String clmApplicationStatusFlag,
			String bankId, String zoneId) throws DatabaseException {

		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()", "Entered");
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"From Date :" + fromDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"To Date :" + toDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"clmApplicationStatusFlag :" + clmApplicationStatusFlag);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Bank Id :" + bankId);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Zone Id :" + zoneId);
		Connection conn = null;
		PreparedStatement prepStatement = null;
		ResultSet rs = null;
		Vector clmRefNumbersList = new Vector();
		String query = null;

		try {
			conn = DBConnection.getConnection();
			if ((fromDate != null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and m.mem_bnk_id = ?"
							+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					prepStatement.setString(5, zoneId);
					prepStatement.setString(6, zoneId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and m.mem_bnk_id = ?"
							+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					prepStatement.setString(5, zoneId);
					prepStatement.setString(6, zoneId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate != null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date between ? and ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and m.mem_bnk_id = ?"
						+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, fromDate);
				prepStatement.setDate(2, toDate);
				prepStatement.setString(3, clmApplicationStatusFlag);
				prepStatement.setString(4, bankId);
				prepStatement.setString(5, zoneId);
				prepStatement.setString(6, zoneId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			if ((fromDate == null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and m.mem_bnk_id = ?"
							+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					// prepStatement.setDate(1,fromDate);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					prepStatement.setString(4, zoneId);
					prepStatement.setString(5, zoneId);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and m.mem_bnk_id = ?"
							+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					prepStatement.setString(4, zoneId);
					prepStatement.setString(5, zoneId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate == null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date <= ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and m.mem_bnk_id = ?"
						+ " and (m.mem_reporting_zone_id = ? or m.mem_zne_id = ?) "
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, toDate);
				prepStatement.setString(2, clmApplicationStatusFlag);
				prepStatement.setString(3, bankId);
				prepStatement.setString(4, zoneId);
				prepStatement.setString(5, zoneId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			String memberBankName = null;
			String memberId = null;
			String clmRefNumber = null;
			String cgclan = "";
			ClaimDetail clmDtl = null;

			if (clmApplicationStatusFlag
					.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					cgclan = (String) rs.getString(4);
					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			} else {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			}
			prepStatement.close();
			prepStatement = null;
		} catch (SQLException sqlexception) {
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return clmRefNumbersList;
	}

	public Vector getListOfClaimRefNumbers(java.sql.Date fromDate,
			java.sql.Date toDate, String clmApplicationStatusFlag,
			String bankId, String zoneId, String brnId)
			throws DatabaseException {

		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()", "Entered");
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"From Date :" + fromDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"To Date :" + toDate);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"clmApplicationStatusFlag :" + clmApplicationStatusFlag);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Bank Id :" + bankId);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Zone Id :" + zoneId);
		Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
				"Branch Id :" + brnId);
		Connection conn = null;
		PreparedStatement prepStatement = null;
		ResultSet rs = null;
		Vector clmRefNumbersList = new Vector();
		String query = null;

		try {
			conn = DBConnection.getConnection();
			if ((fromDate != null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " and c.mem_zne_id = ?"
							+ " and c.mem_brn_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					prepStatement.setString(5, zoneId);
					prepStatement.setString(6, brnId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date between ? and ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " and c.mem_zne_id = ?"
							+ " and c.mem_brn_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, fromDate);
					prepStatement.setDate(2, toDate);
					prepStatement.setString(3, clmApplicationStatusFlag);
					prepStatement.setString(4, bankId);
					prepStatement.setString(5, zoneId);
					prepStatement.setString(6, brnId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate != null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date between ? and ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and c.mem_bnk_id = ?"
						+ " and c.mem_zne_id = ?"
						+ " and c.mem_brn_id = ?"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, fromDate);
				prepStatement.setDate(2, toDate);
				prepStatement.setString(3, clmApplicationStatusFlag);
				prepStatement.setString(4, bankId);
				prepStatement.setString(5, zoneId);
				prepStatement.setString(6, brnId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			if ((fromDate == null)
					&& (!(clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Approval");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no, c.cgclan"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " and c.mem_zne_id = ?"
							+ " and c.mem_brn_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no," + " c.cgclan order by bnkname";
					prepStatement = conn.prepareStatement(query);
					// prepStatement.setDate(1,fromDate);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					prepStatement.setString(4, zoneId);
					prepStatement.setString(5, brnId);
					rs = (ResultSet) prepStatement.executeQuery();
				} else if (clmApplicationStatusFlag
						.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					Log.log(Log.INFO, "ReportDAO",
							"getListOfClaimRefNumbers()",
							"From Date is NULL, Status is Reject");
					/*
					 * query modification@sudeep.dhiman to get resultset in
					 * sorted order
					 */
					query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
							+ " from claim_detail c, member_info m"
							+ " where c.clm_date <= ?"
							+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
							+ " and c.clm_status = ?"
							+ " and c.mem_bnk_id = ?"
							+ " and c.mem_zne_id = ?"
							+ " and c.mem_brn_id = ?"
							+ " group by m.mem_bank_name,"
							+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
							+ " c.clm_ref_no order by bnkname";
					prepStatement = conn.prepareStatement(query);
					prepStatement.setDate(1, toDate);
					prepStatement.setString(2, clmApplicationStatusFlag);
					prepStatement.setString(3, bankId);
					prepStatement.setString(4, zoneId);
					prepStatement.setString(5, brnId);
					rs = (ResultSet) prepStatement.executeQuery();
				}
			}
			if ((fromDate == null)
					&& ((clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_PENDING_STATUS)
							|| clmApplicationStatusFlag
									.equals(ClaimConstants.CLM_HOLD_STATUS) || clmApplicationStatusFlag
							.equals(ClaimConstants.CLM_FORWARD_STATUS)))) {
				Log.log(Log.INFO, "ReportDAO", "getListOfClaimRefNumbers()",
						"From Date is NULL, Status is Pending or Forward or Hold");
				/*
				 * query modification@sudeep.dhiman to get resultset in sorted
				 * order
				 */
				query = "select m.mem_bank_name bnkname, m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id, c.clm_ref_no"
						+ " from claim_detail_temp c, member_info m"
						+ " where c.clm_date <= ?"
						+ " and LTRIM(RTRIM(UPPER(c.mem_bnk_id || c.mem_zne_id || c.mem_brn_id))) = LTRIM(RTRIM(UPPER(m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id))) "
						+ " and c.clm_status = ?"
						+ " and c.mem_bnk_id = ?"
						+ " and c.mem_zne_id = ?"
						+ " and c.mem_brn_id = ?"
						+ " group by m.mem_bank_name,"
						+ " m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id,"
						+ " c.clm_ref_no order by bnkname";
				prepStatement = conn.prepareStatement(query);
				prepStatement.setDate(1, toDate);
				prepStatement.setString(2, clmApplicationStatusFlag);
				prepStatement.setString(3, bankId);
				prepStatement.setString(4, zoneId);
				prepStatement.setString(5, brnId);
				rs = (ResultSet) prepStatement.executeQuery();
			}
			String memberBankName = null;
			String memberId = null;
			String clmRefNumber = null;
			String cgclan = "";
			ClaimDetail clmDtl = null;

			if (clmApplicationStatusFlag
					.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					cgclan = (String) rs.getString(4);
					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);

					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			} else {
				while (rs.next()) {
					memberBankName = (String) rs.getString(1);
					memberId = (String) rs.getString(2);
					clmRefNumber = (String) rs.getString(3);
					clmDtl = new ClaimDetail();
					clmDtl.setMliName(memberBankName);
					clmDtl.setMliId(memberId);
					clmDtl.setClaimRefNum(clmRefNumber);
					clmDtl.setCGCLAN(cgclan);
					// Adding the ClaimDetail object to the vector
					clmRefNumbersList.addElement(clmDtl);
				}
				rs.close();
				rs = null;
			}
			prepStatement.close();
			prepStatement = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		return clmRefNumbersList;
	}
	// Added for colletral Hybrid Retail
	/*public ActionForward coletralHybridRetailReportInput(ActionMapping mapping,
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

	private ArrayList getReportType() throws DatabaseException {
		Connection conn = null;
		Statement stmt=null;
		ArrayList reportType=new ArrayList();
		try {
			
			conn = DBConnection.getConnection();
			Statement pstmt = null;
			ResultSet rs = null;
			ArrayList list = new ArrayList();
						// stmt = connection.createStatement();
			stmt = conn.createStatement();
			String query="select COLUMN_TAB from report_table_tab";
			
			//stmt = conn.createStatement();
			
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if(rs.getString("COLUMN_TAB")!=null){
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
	}*/

	// Ended for colletral Hybrid Retail
	public ActionForward consolidatedDanReportInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportActionForm dynaForm = (ReportActionForm) form;
		//dynaForm.set("dateOfTheDocument20", null);
		//dynaForm.set("dateOfTheDocument21", null);
		dynaForm.setReportType("");
		
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
		return mapping.findForward("inputPage");
	}


	public ActionForward consolidatedDanReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, Exception {

		ReportActionForm dynaForm = (ReportActionForm) form;
		Date fromDt = (Date) dynaForm.getDateOfTheDocument20();
		Date toDt = (Date) dynaForm.getDateOfTheDocument21();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		List list = new ArrayList();
		String flag = (String) dynaForm.getReportType();

		if (flag == null || flag.equals("")) {
			throw new MessageException("Please choose Report type.");
		}

		java.sql.Date sqlfromdate = new java.sql.Date(fromDt.getTime());
		java.sql.Date sqltodate = new java.sql.Date(toDt.getTime());
		String forwardflag = "";
		try {
			// connection = DBConnection.getConnection();
			// stmt = connection.createStatement();
			 
			if ("WITHOUTSTF".equals(flag)) {

				query = " select dan_type,mth,stclass,sum(gencnt) gencnt,sum(genamt) genamt,sum(notpaidcnt) notpaidcnt,sum(notpaidamt) notpaidamt, "
						+ " sum(paidcnt) paidcnt,sum(paidamt) paidamt from( "
						+ " select dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1 mth,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
						+ " count(*) gencnt,sum(dci_amount_raised) genamt,0 notpaidcnt,0 notpaidamt,0 paidcnt,0 paidamt, "
						+ " sum(dci_stax_amt) genstax,sum(dci_ecess_amt) genecamt,sum(dci_hecess_amt) genhecamt,0 paidstax,0 paidecamt,0 paidhecamt,0 notpaidstax,0 notpaidecamt,0 notpaidhecamt "
						+ " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
						+ " where dc.dan_id = da.dan_id and dc.cgpan = a.cgpan and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
						+ " and trunc(dan_generated_dt) >= '01-jul-2012' "
						+ " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
						+ " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
						+ " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
						+ " union all "
						+ " select dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1 mth,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
						+ " 0 gencnt,0 genamt,count(*) notpaidcnt,sum(dci_amount_raised)  notpaidamt,0 paidcnt,0 paidamt, "
						+ " 0 genstax,0 genecamt,0 genhecamt,0 paidstax,0 paidecamt,0 paidhecamt,sum(dci_stax_amt) notpaidstax,sum(dci_ecess_amt) notpaidecamt,sum(dci_hecess_amt) notpaidhecamt "
						+ " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
						+ " where dc.dan_id = da.dan_id and dc.cgpan = a.cgpan and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
						+ " and trunc(dan_generated_dt) >= '01-jul-2012' "
						+ " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 and dci_appropriation_flag = 'N' "
						+ " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
						+ " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
						+ " union all "
						+ " select dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
						+ " 0 gencnt,0 genamt,0 notpaidcnt,0 notpaidamt,count(*) paidcnt,sum(dci_amount_raised)  paidamt, "
						+ " 0 genstax,0 genecamt,0 genhecamt,sum(dci_stax_amt) paidstax,sum(dci_ecess_amt) paidecamt,sum(dci_hecess_amt) paidhecamt,0 notpaidstax,0 notpaidecamt,0 notpaidhecamt "
						+ " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
						+ " where dc.dan_id = da.dan_id and dc.cgpan = a.cgpan and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
						+ " and trunc(dan_generated_dt) >= '01-jul-2012' "
						+ " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 and dci_appropriation_flag = 'Y' "
						+ " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
						+ " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1,decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
						+ " )"
						+ " group by dan_type,mth,stclass "
						+ " ORDER BY 3";
				 
				forwardflag = "withoutstf";

				list = consolidatedDanReport(connection, query, sqlfromdate,
						sqltodate);
			} else if ("WITHSTF".equals(flag)) {

				/*
				 * query = "select tflg,dan_type,mth,stclass, " +
				 * " sum(gencnt) gencnt,sum(genamt) genamt,sum(genstax) genstax,sum(genecamt) genecamt,sum(genhecamt) genhecamt, "
				 * +
				 * " sum(notpaidcnt) notpaidcnt,sum(notpaidamt) notpaidamt,sum(notpaidstax) notpaidstax,sum(notpaidecamt) notpaidecamt,sum(notpaidhecamt) notpaidhecamt, "
				 * +
				 * " sum(paidcnt) paidcnt,sum(paidamt) paidamt,sum(paidstax) paidstax,sum(paidecamt) paidecamt,sum(paidhecamt) paidhecamt "
				 * + " from " + " ( " +
				 * " select 'STI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1 mth, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * +
				 * " count(*) gencnt,sum(dci_amount_raised) genamt,0 notpaidcnt,0 notpaidamt,0 paidcnt,0 paidamt, "
				 * +
				 * " sum(dci_stax_amt) genstax,sum(dci_ecess_amt) genecamt,sum(dci_hecess_amt) genhecamt, "
				 * +
				 * " 0 paidstax,0 paidecamt,0 paidhecamt,0 notpaidstax,0 notpaidecamt,0 notpaidhecamt  "
				 * +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_stax_amt is not null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " union all " +
				 * " select 'STI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * + " 0 gencnt,0 genamt, " +
				 * " count(*) notpaidcnt,sum(dci_amount_raised)  notpaidamt,0 paidcnt,0 paidamt, "
				 * + " 0 genstax,0 genecamt,0 genhecamt, " +
				 * " 0 paidstax,0 paidecamt,0 paidhecamt, " +
				 * " sum(dci_stax_amt) notpaidstax,sum(dci_ecess_amt) notpaidecamt,sum(dci_hecess_amt) notpaidhecamt "
				 * +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id  " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_appropriation_flag = 'N' " +
				 * " and dci_stax_amt is not null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " union all " +
				 * " select 'STI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * + " 0 gencnt,0 genamt,0 notpaidcnt,0 notpaidamt, " +
				 * " count(*) paidcnt,sum(dci_amount_raised)  paidamt, " +
				 * " 0 genstax,0 genecamt,0 genhecamt, " +
				 * " sum(dci_stax_amt) paidstax,sum(dci_ecess_amt) paidecamt,sum(dci_hecess_amt) paidhecamt, "
				 * + " 0 notpaidstax,0 notpaidecamt,0 notpaidhecamt " +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_appropriation_flag = 'Y' " +
				 * " and dci_stax_amt is not null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " union all " +
				 * " select 'STNI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1 mth, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * +
				 * " count(*) gencnt,sum(dci_amount_raised) genamt,0 notpaidcnt,0 notpaidamt,0 paidcnt,0 paidamt, "
				 * +
				 * " sum(dci_stax_amt) genstax,sum(dci_ecess_amt) genecamt,sum(dci_hecess_amt) genhecamt, "
				 * +
				 * " 0 paidstax,0 paidecamt,0 paidhecamt,0 notpaidstax,0 notpaidecamt,0 notpaidhecamt  "
				 * +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_stax_amt is null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " union all " +
				 * " select 'STNI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * + " 0 gencnt,0 genamt, " +
				 * " count(*) notpaidcnt,sum(dci_amount_raised)  notpaidamt,0 paidcnt,0 paidamt, "
				 * + " 0 genstax,0 genecamt,0 genhecamt, " +
				 * " 0 paidstax,0 paidecamt,0 paidhecamt, " +
				 * " sum(dci_stax_amt) notpaidstax,sum(dci_ecess_amt) notpaidecamt,sum(dci_hecess_amt) notpaidhecamt "
				 * +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_appropriation_flag = 'N' " +
				 * " and dci_stax_amt is null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " union all " +
				 * " select 'STNI' Tflg,dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') stclass, "
				 * + " 0 gencnt,0 genamt,0 notpaidcnt,0 notpaidamt, " +
				 * " count(*) paidcnt,sum(dci_amount_raised)  paidamt, " +
				 * " 0 genstax,0 genecamt,0 genhecamt, " +
				 * " sum(dci_stax_amt) paidstax,sum(dci_ecess_amt) paidecamt,sum(dci_hecess_amt) paidhecamt, "
				 * + " 0 notpaidstax,0 notpaidecamt,0 notpaidhecamt " +
				 * " from dan_cgpan_info dc,demand_advice_info da,application_detail a,member_info m "
				 * + " where dc.dan_id = da.dan_id " +
				 * " and dc.cgpan = a.cgpan " +
				 * " and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id = m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id "
				 * + " and trunc(dan_generated_dt) >= '01-jul-2012' " //+
				 * " and trunc(dan_generated_dt) <= '31-aug-2014' "
				 */
				/*
				 * +
				 * " and trunc(dan_generated_dt) >= ? and trunc(dan_generated_dt) <= ? "
				 * + " and dci_amount_raised - nvl(dci_amount_cancelled,0) > 0 "
				 * + " and dci_appropriation_flag = 'Y' " +
				 * " and dci_stax_amt is null " +
				 * " group by dan_type,last_day(add_months(trunc(dan_generated_dt),-1))+1, "
				 * +
				 * " decode(replace(mem_state_name,'&',''),'JAMMU  KASHMIR','JK','OTHERS') "
				 * + " ) " + " group by tflg,dan_type,mth,stclass " +
				 * " ORDER BY 3 ";
				 */
            
				query = "SELECT tflg, dan_type, mth, stclass, SUM (gencnt) gencnt, SUM (genamt) genamt, SUM (genstax) genstax, SUM (genecamt) genecamt, SUM (genhecamt) genhecamt, SUM (notpaidcnt) notpaidcnt, \n SUM (notpaidamt) notpaidamt, SUM (notpaidstax) notpaidstax, SUM (notpaidecamt) notpaidecamt, SUM (notpaidhecamt) notpaidhecamt, SUM (paidcnt) paidcnt, SUM (paidamt) paidamt, \n SUM (paidstax) paidstax, SUM (paidecamt) paidecamt, SUM (paidhecamt) paidhecamt, SUM (TOTAL_SWBHCESS_AMT) TOTAL_SWBHCESS_AMT, SUM (paidswhcess) paidswhcess, SUM (notpaidswhcess) notpaidswhcess ,SUM (genkkcess) genkkcess ,SUM (paidkkcess) paidkkcess , SUM (notpaidkkhcess) notpaidkkhcess \n FROM (  SELECT 'STI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1 mth, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, \n COUNT (*) gencnt, SUM (dci_amount_raised) genamt, 0 notpaidcnt, 0 notpaidamt, 0 paidcnt, 0 paidamt, SUM (dci_stax_amt) genstax, SUM (dci_ecess_amt) genecamt, SUM (dci_hecess_amt) genhecamt, \n 0 paidstax, 0 paidecamt, 0 paidhecamt, 0 notpaidstax, 0 notpaidecamt, 0 notpaidhecamt, SUM (dc.DCI_SWBHCESS_AMT) TOTAL_SWBHCESS_AMT, 0 paidswhcess, 0 notpaidswhcess , sum(DCI_KKALYANCESS_AMT)  genkkcess ,0 paidkkcess , 0  notpaidkkhcess \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id AND dc.cgpan = a.cgpan AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_stax_amt IS NOT NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') \n UNION ALL \n SELECT 'STI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, 0 gencnt, 0 genamt, \n COUNT (*) notpaidcnt, SUM (dci_amount_raised) notpaidamt, 0 paidcnt, 0 paidamt, 0 genstax, 0 genecamt, 0 genhecamt, 0 paidstax, 0 paidecamt, 0 paidhecamt, SUM (dci_stax_amt) notpaidstax, \n SUM (dci_ecess_amt) notpaidecamt, SUM (dci_hecess_amt) notpaidhecamt, 0 TOTAL_SWBHCESS_AMT, 0 paidswhcess, SUM (dc.DCI_SWBHCESS_AMT) notpaidswhcess ,0  genkkcess, 0   paidkkcess , sum(DCI_KKALYANCESS_AMT) notpaidkkhcess \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id AND dc.cgpan = a.cgpan AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_appropriation_flag = 'N' AND dci_stax_amt IS NOT NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') \n UNION ALL \n SELECT 'STI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, 0 gencnt, \n 0 genamt, 0 notpaidcnt, 0 notpaidamt, COUNT (*) paidcnt, SUM (dci_amount_raised) paidamt, 0 genstax, 0 genecamt, 0 genhecamt, SUM (dci_stax_amt) paidstax, SUM (dci_ecess_amt) paidecamt, \n SUM (dci_hecess_amt) paidhecamt, 0 notpaidstax, 0 notpaidecamt, 0 notpaidhecamt, 0 TOTAL_SWBHCESS_AMT, SUM (dc.DCI_SWBHCESS_AMT) paidswhcess, 0 notpaidswhcess, 0  genkkcess ,sum(DCI_KKALYANCESS_AMT)  paidkkcess, 0  notpaidkkhcess  \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id AND dc.cgpan = a.cgpan AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_appropriation_flag = 'Y' AND dci_stax_amt IS NOT NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') \n UNION ALL \n SELECT 'STNI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1 mth, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, \n COUNT (*) gencnt, SUM (dci_amount_raised) genamt, 0 notpaidcnt, 0 notpaidamt, 0 paidcnt, 0 paidamt, SUM (dci_stax_amt) genstax, SUM (dci_ecess_amt) genecamt, SUM (dci_hecess_amt) genhecamt, \n 0 paidstax, 0 paidecamt, 0 paidhecamt, 0 notpaidstax, 0 notpaidecamt, 0 notpaidhecamt, SUM (dc.DCI_SWBHCESS_AMT) TOTAL_SWBHCESS_AMT, 0 paidswhcess, 0 notpaidswhcess, sum(DCI_KKALYANCESS_AMT)  genkkcess , 0 paidkkcess ,0  notpaidkkhcess \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id AND dc.cgpan = a.cgpan AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_stax_amt IS NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') \n UNION ALL \n SELECT 'STNI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, 0 gencnt, \n 0 genamt, COUNT (*) notpaidcnt, SUM (dci_amount_raised) notpaidamt, 0 paidcnt, 0 paidamt, 0 genstax, 0 genecamt, 0 genhecamt, 0 paidstax, 0 paidecamt, 0 paidhecamt,\n SUM (dci_stax_amt) notpaidstax, SUM (dci_ecess_amt) notpaidecamt, SUM (dci_hecess_amt) notpaidhecamt, 0 TOTAL_SWBHCESS_AMT, 0 paidswhcess, SUM (dc.DCI_SWBHCESS_AMT) notpaidswhcess, 0 genkkcess,  0 paidkkcess  , sum(DCI_KKALYANCESS_AMT) notpaidkkhcess \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id  AND dc.cgpan = a.cgpan  AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_appropriation_flag = 'N' AND dci_stax_amt IS NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') \n UNION ALL \n SELECT 'STNI' Tflg, dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS') stclass, 0 gencnt, \n 0 genamt, 0 notpaidcnt, 0 notpaidamt, COUNT (*) paidcnt, SUM (dci_amount_raised) paidamt, 0 genstax, 0 genecamt, 0 genhecamt, SUM (dci_stax_amt) paidstax, SUM (dci_ecess_amt) paidecamt, \n SUM (dci_hecess_amt) paidhecamt, 0 notpaidstax, 0 notpaidecamt, 0 notpaidhecamt, 0 TOTAL_SWBHCESS_AMT, SUM (dc.DCI_SWBHCESS_AMT) paidswhcess, 0 notpaidswhcess ,   0  genkkcess  ,  sum(DCI_KKALYANCESS_AMT)  paidkkcess ,   0  notpaidkkhcess  \n FROM dan_cgpan_info dc, demand_advice_info da, application_detail a, member_info m \n WHERE     dc.dan_id = da.dan_id AND dc.cgpan = a.cgpan AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id = m.mem_bnk_id || m.mem_zne_id || m.mem_brn_id \n AND TRUNC (dan_generated_dt) >= '01-jul-2012' AND TRUNC (dan_generated_dt) >= ? AND TRUNC (dan_generated_dt) <= ? \n AND dci_amount_raised - NVL (dci_amount_cancelled, 0) > 0 AND dci_appropriation_flag = 'Y' AND dci_stax_amt IS NULL \n GROUP BY dan_type, LAST_DAY (ADD_MONTHS (TRUNC (dan_generated_dt), -1)) + 1, DECODE (REPLACE (mem_state_name, '&', ''), 'JAMMU  KASHMIR', 'JK', 'OTHERS')) \n GROUP BY tflg, dan_type, mth, stclass ORDER BY 3";
				forwardflag = "withstf";

			////System.out.println("withstf query "+query);
				  
				list = consolidatedDanReportWithTaxComponent(connection, query,
						sqlfromdate, sqltodate);
			}
			dynaForm.setDanDetails(list);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return mapping.findForward(forwardflag);
	}

	public ArrayList consolidatedDanReport(Connection connection, String query,
			java.sql.Date sqlfromdate, java.sql.Date sqltodate)
			throws DatabaseException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		ArrayList list = new ArrayList();
		connection = DBConnection.getConnection();
		// stmt = connection.createStatement();
		pstmt = connection.prepareStatement(query);
		pstmt.setDate(1, sqlfromdate);
		pstmt.setDate(2, sqltodate);
		pstmt.setDate(3, sqlfromdate);
		pstmt.setDate(4, sqltodate);
		pstmt.setDate(5, sqlfromdate);
		pstmt.setDate(6, sqltodate);
		try {
			// rs = stmt.executeQuery(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				DanReport report = new DanReport();
				report.setDanType(rs.getString("dan_type"));
				report.setLastDate(rs.getDate("mth"));
				report.setStclass(rs.getString("stclass"));

				report.setTotalcnt(rs.getLong("gencnt"));
				report.setTotalamt(rs.getDouble("genamt"));

				report.setNotpaidcnt(rs.getLong("notpaidcnt"));
				report.setNotpaidamt(rs.getDouble("notpaidamt"));

				report.setPaidcnt(rs.getLong("paidcnt"));
				report.setPaidamt(rs.getDouble("paidamt"));

				list.add(report);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return list;
	}

	public ArrayList consolidatedDanReportWithTaxComponent(
			Connection connection, String query, java.sql.Date sqlfromdate,
			java.sql.Date sqltodate) throws DatabaseException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		PreparedStatement pstmt = null;
		pstmt = connection.prepareStatement(query);
		pstmt.setDate(1, sqlfromdate);
		pstmt.setDate(2, sqltodate);
		pstmt.setDate(3, sqlfromdate);
		pstmt.setDate(4, sqltodate);
		pstmt.setDate(5, sqlfromdate);
		pstmt.setDate(6, sqltodate);
		pstmt.setDate(7, sqlfromdate);
		pstmt.setDate(8, sqltodate);
		pstmt.setDate(9, sqlfromdate);
		pstmt.setDate(10, sqltodate);
		pstmt.setDate(11, sqlfromdate);
		pstmt.setDate(12, sqltodate);
		try {
			// rs = stmt.executeQuery(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				DanReport report = new DanReport();
				report.setStFlag(rs.getString("tflg"));
				report.setDanType(rs.getString("dan_type"));
				report.setLastDate(rs.getDate("mth"));
				report.setStclass(rs.getString("stclass"));

				report.setTotalcnt(rs.getLong("gencnt"));
				report.setTotalamt(rs.getDouble("genamt"));

				report.setNotpaidcnt(rs.getLong("notpaidcnt"));
				report.setNotpaidamt(rs.getDouble("notpaidamt"));

				report.setPaidcnt(rs.getLong("paidcnt"));
				report.setPaidamt(rs.getDouble("paidamt"));

				report.setTotalstamt(rs.getDouble("genstax"));
				report.setNotpaidstamt(rs.getDouble("notpaidstax"));
				report.setPaidstamt(rs.getDouble("paidstax"));

				report.setTotalecamt(rs.getDouble("genecamt"));
				report.setNotpaidecamt(rs.getDouble("notpaidecamt"));
				report.setPaidecamt(rs.getDouble("paidecamt"));

				report.setTotalhecamt(rs.getDouble("genhecamt"));
				report.setNotpaidhecamt(rs.getDouble("notpaidhecamt"));
				report.setPaidhecamt(rs.getDouble("paidhecamt"));
				report.setTotalsbamt(rs.getDouble("TOTAL_SWBHCESS_AMT"));
				report.setNotpaidsbamt(rs.getDouble("notpaidswhcess"));
				report.setPaidsbamt(rs.getDouble("paidswhcess"));
					//ADDED BY KULDEEP
				report.setTotalkrishikamt(rs.getDouble("genkkcess"));
				report.setNotpaidkrishikamt(rs.getDouble("notpaidkkhcess"));
				report.setPaidkrishikamt(rs.getDouble("paidkkcess"));
				
				
				
				list.add(report);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

		return list;
	}

	
    public ActionForward StateWiseCategory(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response
            )throws Exception {

Log.log(Log.INFO,"ReportsAction","minorityStateReport","Entered");
DynaActionForm dynaForm = (DynaActionForm) form;
Date date               = new Date();
Calendar calendar       = Calendar.getInstance();
calendar.setTime(date);
int month   = calendar.get(Calendar.MONTH);
int day     = calendar.get(Calendar.DATE);
month       = month - 1;
day         = day + 1;
calendar.set(Calendar.MONTH, month);
calendar.set(Calendar.DATE,day);
Date prevDate = calendar.getTime();
GeneralReport generalReport = new GeneralReport();
generalReport.setDateOfTheDocument(prevDate);
generalReport.setDateOfTheDocument1(date);
BeanUtils.copyProperties(dynaForm, generalReport);

Log.log(Log.INFO,"ReportsAction","minorityStateReport","Exited");
return mapping.findForward("success");
}



public ActionForward industryWiseReport(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response
            )throws Exception {

Log.log(Log.INFO,"ReportsAction","minorityStateReport","Entered");
DynaActionForm dynaForm = (DynaActionForm) form;
Date date               = new Date();
Calendar calendar       = Calendar.getInstance();
calendar.setTime(date);
int month   = calendar.get(Calendar.MONTH);
int day     = calendar.get(Calendar.DATE);
month       = month - 1;
day         = day + 1;
calendar.set(Calendar.MONTH, month);
calendar.set(Calendar.DATE,day);
Date prevDate = calendar.getTime();
GeneralReport generalReport = new GeneralReport();
generalReport.setDateOfTheDocument(prevDate);
generalReport.setDateOfTheDocument1(date);
BeanUtils.copyProperties(dynaForm, generalReport);

Log.log(Log.INFO,"ReportsAction","minorityStateReport","Exited");
return mapping.findForward("success");
}


public ActionForward categorystateprogressReport(ActionMapping mapping,
ActionForm form,
HttpServletRequest request,
HttpServletResponse response
)throws Exception {

Log.log(Log.INFO,"ReportsAction","minoritystateprogressReport","Entered");
ArrayList categorystateprogressReport  = new ArrayList();
DynaActionForm dynaForm   = (DynaActionForm)form;
java.sql.Date startDate   = null;
java.sql.Date endDate     = null;
java.util.Date sDate      = (java.util.Date) dynaForm.get("dateOfTheDocument");
String stDate             = String.valueOf(sDate);

if((stDate == null) ||(stDate.equals("")))
startDate = null;      
else if(stDate != null)
startDate = new java.sql.Date (sDate.getTime());

//    //System.out.println("sukumar:shyam");   
java.util.Date eDate  =  (java.util.Date) dynaForm.get("dateOfTheDocument1");
endDate               = new java.sql.Date (eDate.getTime());
//  //System.out.println(" startDate:"+ startDate);
//  //System.out.println("endDate :"+endDate );
categorystateprogressReport =getcategoryStateProgressReport(startDate,endDate);
dynaForm.set("categorystateprogressReport",categorystateprogressReport); 
if(categorystateprogressReport == null ||  categorystateprogressReport.size()==0)
{
throw new NoDataException("No Data is available for the values entered," +
" Please Enter Any Other Value ");
}
else{
Log.log(Log.INFO,"ReportsAction","minoritystateprogressReport","Exited");
return mapping.findForward("success");
}
/*  progressReport        = reportManager.getMonthlyProgressReport(startDate,endDate);
dynaForm.set("progressReport",progressReport); 
if(progressReport == null || progressReport.size()==0)
{
throw new NoDataException("No Data is available for the values entered," +
" Please Enter Any Other Value ");
}
else
{
Log.log(Log.INFO,"ReportsAction","minorityprogressReport","Exited");
return mapping.findForward("success");
} */

}

public ArrayList getcategoryStateProgressReport(java.sql.Date startDate, 
             java.sql.Date endDate) throws DatabaseException {

Log.log(Log.INFO, "ReportDAO", " minorityStateProgressReport", 
"Entered");
PreparedStatement allStatesStmt1 = null;
ArrayList allStatesArray1 = new ArrayList();
ResultSet allStatesResult1 = null;
Connection connection = DBConnection.getConnection();

int totalCount=0;

double totalAmount=0.0;

if (startDate == null) {
try { /*query modification@sudeep.dhiman to get resultset in sorted order*/
/*
String query = "select ssi.SSI_STATE_NAME, count(vw.CGPAN), " +
" SUM(decode(app.APP_REAPPROVE_AMOUNT,null,app.APP_APPROVED_AMOUNT,app.APP_REAPPROVE_AMOUNT)) " +
" from ssi_detail ssi, application_detail app, view_appl_amounts vw, member_info m " +
" where app.CGPAN = vw.CGPAN and ssi.SSI_REFERENCE_NUMBER = app.SSI_REFERENCE_NUMBER " +
" and app.MEM_BNK_ID||app.MEM_ZNE_ID||app.MEM_BRN_ID =    m.MEM_BNK_ID||m.MEM_ZNE_ID||m.MEM_BRN_ID " +
" and trunc(app.APP_APPROVED_DATE_TIME) <= ? group by ssi.SSI_state_name "+
" order by ssi.SSI_STATE_NAME";
*/
String query ="SELECT ssi_state_name state,pmr_chief_social_cat cat,COUNT(cgpan) CMCNT,\n" + 
"    ROUND(SUM(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount)/100000),3) CMAMT    \n" + 
"    FROM APPLICATION_DETAIL A,PROMOTER_DETAIL p,SSI_DETAIL s \n" + 
"    WHERE A.ssi_reference_number = s.ssi_reference_number\n" + 
"    AND s.ssi_reference_number = p.ssi_reference_number\n" + 
"     AND TRUNC(app_approved_date_time) BETWEEN  ?  AND  ? \n" + 
"    AND app_status NOT IN ('RE')\n" + 
"    GROUP BY ssi_state_name,pmr_chief_social_cat  order by ssi_state_name "; 

allStatesStmt1 = connection.prepareStatement(query);
allStatesStmt1.setDate(1, endDate); //Set endDate parameter
allStatesResult1 = allStatesStmt1.executeQuery();
  //System.out.println("query0:"+query);
while (allStatesResult1.next()) {
//                                  Instantiate a GeneralReport value object
GeneralReport allStatesList1 = new GeneralReport();
allStatesList1.setStateName(allStatesResult1.getString(1));
allStatesList1.setCategory(allStatesResult1.getString(2));
allStatesList1.setNumber(allStatesResult1.getInt(3));                     
allStatesList1.setAmount(allStatesResult1.getDouble(4));


totalCount=totalCount+allStatesResult1.getInt(3);

totalAmount=totalAmount+allStatesResult1.getDouble(4);


allStatesList1.setTotalCount(totalCount);                     
allStatesList1.setTotalAmount(totalAmount);





allStatesArray1.add(allStatesList1);
}
allStatesResult1.close();
allStatesResult1 = null;
allStatesStmt1.close();
allStatesStmt1 = null;

} catch (Exception exception) {
Log.logException(exception);
throw new DatabaseException(exception.getMessage());
} finally {
DBConnection.freeConnection(connection);
}
}

else if (startDate != null) {
try { /*query modification@sudeep.dhiman to get resultset in sorted order*/
String query ="   SELECT MEM_STATE_NAME state,pmr_chief_social_cat cat,COUNT(cgpan) CMCNT,\n" + 
"    ROUND(SUM(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount)/100000),3) CMAMT    \n" + 
"    FROM APPLICATION_DETAIL A,PROMOTER_DETAIL p,member_info m,SSI_DETAIL s \n" + 
"    WHERE A.ssi_reference_number = s.ssi_reference_number\n" + 
"   and A.MEM_BNK_ID||A.MEM_ZNE_ID||A.MEM_BRN_ID=M.MEM_BNK_ID||M.MEM_ZNE_ID||M.MEM_BRN_ID" +
"    AND s.ssi_reference_number = p.ssi_reference_number\n" + 
"     AND TRUNC(app_approved_date_time) BETWEEN  ?  AND  ? \n" + 
"    AND app_status NOT IN ('RE')\n" + 
"    GROUP BY MEM_STATE_NAME,pmr_chief_social_cat order by MEM_STATE_NAME "; 
allStatesStmt1 = connection.prepareStatement(query);
allStatesStmt1.setDate(1, startDate); //Set startDate parameter
allStatesStmt1.setDate(2, endDate); //Set endDate parameter
allStatesResult1 = allStatesStmt1.executeQuery();
   //System.out.println("query:1"+query);
while (allStatesResult1.next()) {
//                                  Instantiate a GeneralReport value object
GeneralReport allStatesList2 = new GeneralReport();
allStatesList2.setStateName(allStatesResult1.getString(1));
allStatesList2.setCategory(allStatesResult1.getString(2));
allStatesList2.setNumber(allStatesResult1.getInt(3));                     
allStatesList2.setAmount(allStatesResult1.getDouble(4));

totalCount=totalCount+allStatesResult1.getInt(3);                     
double  totalAmounttemp=totalAmount+allStatesResult1.getDouble(4);       
DecimalFormat decFormat = new DecimalFormat("#.00");  
totalAmount = new Double(decFormat.format(totalAmounttemp)).doubleValue(); 
allStatesList2.setTotalCount(totalCount);
allStatesList2.setTotalAmount(totalAmount);



allStatesArray1.add(allStatesList2);
}
allStatesResult1.close();
allStatesResult1 = null;
allStatesStmt1.close();
allStatesStmt1 = null;

} catch (Exception exception) {
Log.logException(exception);
throw new DatabaseException(exception.getMessage());
} finally {
DBConnection.freeConnection(connection);
}
}

Log.log(Log.INFO, "ReportDAO", " minorityStateProgressReport", 
"Exited");
return allStatesArray1;
}




public ActionForward industryReportDetails(ActionMapping mapping,
     ActionForm form,
     HttpServletRequest request,
     HttpServletResponse response
     )throws Exception {

Log.log(Log.INFO,"ReportsAction","stateWiseReportDetails","Entered");
DynaActionForm dynaForm = (DynaActionForm)form;
ArrayList mliReport     = new ArrayList();
java.sql.Date startDate = null;
java.sql.Date endDate   = null;
java.util.Date sDate    = (java.util.Date) dynaForm.get("dateOfTheDocument");
String stDate           = String.valueOf(sDate);

if((stDate == null) ||(stDate.equals("")))
startDate = null;
else if(stDate != null)
startDate = new java.sql.Date (sDate.getTime());

java.util.Date eDate  = (java.util.Date) dynaForm.get("dateOfTheDocument1");
endDate               = new java.sql.Date (eDate.getTime());
//    //System.out.println("startDate:"+startDate);
//     //System.out.println("endDate:"+endDate);
String id             = (String) dynaForm.get("checkValue");
//    //System.out.println("id:"+id);
BaseAction baseAction = new BaseAction();
MLIInfo mliInfo       = baseAction.getMemberInfo(request);
String bankId         = mliInfo.getBankId();
String zoneId         = mliInfo.getZoneId();
String branchId       = mliInfo.getBranchId();

if(id.equals("yes"))
request.setAttribute("radioValue","Guarantee Approved");
else if(id.equals("no"))
request.setAttribute("radioValue","Guarantee Issued");



String memberId = bankId+zoneId+branchId;
//   //System.out.println("memberId:"+memberId);

if(id.equals("yes")){
mliReport       =industryReportDetailsLessthanEqualto10LakhsArrayList(startDate,endDate,id);
}
         else if(id.equals("no"))
{
mliReport       =industryReportDetailsGreaterthan10LakhsArrayList(startDate,endDate,id);
}

else
{
         
mliReport       =industryReportDetailsArrayList(startDate,endDate,id);

}
dynaForm.set("mliWiseReport",mliReport);
if(mliReport == null || mliReport.size()==0)
{
throw new NoDataException("No Data is available for the values entered," +
" Please Enter Any Other Value ");
}
else
{
mliReport = null;
Log.log(Log.INFO,"ReportsAction","stateWiseReportDetails","Exited");






return mapping.findForward("success");

}
     }



public ArrayList industryReportDetailsLessthanEqualto10LakhsArrayList(java.sql.Date startDate, 
              java.sql.Date endDate, 
              String id
              ) throws DatabaseException {

Log.log(Log.INFO, "ReportDAO", "StateBranchApplicationDetails", 
"Entered");
PreparedStatement mliApplicationStmt = null;
ResultSet mliApplicationResult;
Connection connection = DBConnection.getConnection();
ArrayList mliApplicationArray = new ArrayList();

                  int totalCount=0;
                  
                  double totalAmount=0.0;


//  //System.out.println("@@@@@@@@@@@@@@@2 Hello Calling from hell @@@@@@@@@@2");

try { /*query modification@sudeep.dhiman to get resultset in sorted order*/
String query ="SELECT ssi_industry_nature industry,COUNT(cgpan) CUMCNT,\n" + 
"ROUND(SUM(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount)/100000),3) CUMAMT\n" + 
"FROM APPLICATION_DETAIL A,SSI_DETAIL S\n" + 
"WHERE A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER\n" + 
"AND TRUNC(app_approved_date_time)  BETWEEN ?  AND  ? \n" + 
"AND app_status NOT IN ('RE')\n" + 
"AND DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) <= 1000000\n" + 
"AND ssi_industry_nature IS NULL\n" + 
"GROUP BY ssi_industry_nature";
mliApplicationStmt = connection.prepareStatement(query);
mliApplicationStmt.setDate(1, 
      startDate); //Set startDate parameter
mliApplicationStmt.setDate(2, endDate); //Set endDate parameter
// mliApplicationStmt.setString(3, 
       // id); //Set memberId parameter
mliApplicationResult = mliApplicationStmt.executeQuery();

while (mliApplicationResult.next()) {
//Instantiate a GeneralReport value object
GeneralReport mliApplicationReport = new GeneralReport();
mliApplicationReport.setIndustry(mliApplicationResult.getString(1));
mliApplicationReport.setProposals(mliApplicationResult.getInt(2));
mliApplicationReport.setAmount(mliApplicationResult.getDouble(3));

totalCount=totalCount+mliApplicationResult.getInt(2);                     
double  totalAmounttemp=totalAmount+mliApplicationResult.getDouble(3);
DecimalFormat decFormat = new DecimalFormat("#.00");  
totalAmount = new Double(decFormat.format(totalAmounttemp)).doubleValue(); 
mliApplicationReport.setTotalCount(totalCount);
mliApplicationReport.setTotalAmount(totalAmount);



mliApplicationArray.add(mliApplicationReport);
}
mliApplicationResult.close();
mliApplicationResult = null;
mliApplicationStmt.close();
mliApplicationStmt = null;

} catch (Exception exception) {
Log.logException(exception);
throw new DatabaseException(exception.getMessage());
} finally {
DBConnection.freeConnection(connection);
}

return mliApplicationArray;
              }

public ArrayList industryReportDetailsGreaterthan10LakhsArrayList(java.sql.Date startDate, 
              java.sql.Date endDate, 
              String id
              ) throws DatabaseException {

Log.log(Log.INFO, "ReportDAO", "StateBranchApplicationDetails", 
"Entered");
PreparedStatement mliApplicationStmt = null;
ResultSet mliApplicationResult;
Connection connection = DBConnection.getConnection();
ArrayList mliApplicationArray = new ArrayList();

                  int totalCount=0;
                  
                  double totalAmount=0.0;
//  //System.out.println("@@@@@@@@@@@@@@@2 Hello Calling from hell @@@@@@@@@@2");

try { /*query modification@sudeep.dhiman to get resultset in sorted order*/
String query ="SELECT ssi_industry_nature industry,COUNT(cgpan) CUMCNT,\n" + 
"ROUND(SUM(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount)/100000),3) CUMAMT\n" + 
"FROM APPLICATION_DETAIL A,SSI_DETAIL S\n" + 
"WHERE A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER\n" + 
"AND TRUNC(app_approved_date_time) BETWEEN ?  AND  ? \n" + 
"AND app_status NOT IN ('RE')\n" + 
"AND DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount) > 1000000\n" + 
"AND ssi_industry_nature IS NULL\n" + 
"GROUP BY ssi_industry_nature";
//  //System.out.println("the query is =========="+query);
mliApplicationStmt = connection.prepareStatement(query);
mliApplicationStmt.setDate(1, 
       startDate); //Set startDate parameter
mliApplicationStmt.setDate(2, endDate); //Set endDate parameter
//   mliApplicationStmt.setString(3, 
  //       id); //Set memberId parameter
mliApplicationResult = mliApplicationStmt.executeQuery();

while (mliApplicationResult.next()) {
//Instantiate a GeneralReport value object
GeneralReport mliApplicationReport = new GeneralReport();
mliApplicationReport.setIndustry(mliApplicationResult.getString(1));
mliApplicationReport.setProposals(mliApplicationResult.getInt(2));
mliApplicationReport.setAmount(mliApplicationResult.getDouble(3));

totalCount=totalCount+mliApplicationResult.getInt(2);                     
double  totalAmounttemp=totalAmount+mliApplicationResult.getDouble(3);
DecimalFormat decFormat = new DecimalFormat("#.00");  
totalAmount = new Double(decFormat.format(totalAmounttemp)).doubleValue(); 
mliApplicationReport.setTotalCount(totalCount);
mliApplicationReport.setTotalAmount(totalAmount);

mliApplicationArray.add(mliApplicationReport);
}
mliApplicationResult.close();
mliApplicationResult = null;
mliApplicationStmt.close();
mliApplicationStmt = null;

} catch (Exception exception) {
Log.logException(exception);
throw new DatabaseException(exception.getMessage());
} finally {
DBConnection.freeConnection(connection);
}

return mliApplicationArray;


              }
public ArrayList industryReportDetailsArrayList(java.sql.Date startDate, 
              java.sql.Date endDate, 
              String id
              ) throws DatabaseException {

Log.log(Log.INFO, "ReportDAO", "StateBranchApplicationDetails", 
"Entered");
PreparedStatement mliApplicationStmt = null;
ResultSet mliApplicationResult;
Connection connection = DBConnection.getConnection();
ArrayList mliApplicationArray = new ArrayList();
int totalCount=0;

double totalAmount=0.0;

//  //System.out.println("@@@@@@@@@@@@@@@2 Hello Calling from hell @@@@@@@@@@2");

try { /*query modification@sudeep.dhiman to get resultset in sorted order*/
String query ="SELECT ssi_industry_nature industry,COUNT(cgpan) CUMCNT,\n" + 
"ROUND(SUM(DECODE(NVL(app_reapprove_amount,0),0,app_approved_amount,app_reapprove_amount)/100000),3) CUMAMT\n" + 
"FROM APPLICATION_DETAIL A,SSI_DETAIL S\n" + 
"WHERE A.SSI_REFERENCE_NUMBER = S.SSI_REFERENCE_NUMBER\n" + 
"AND TRUNC(app_approved_date_time) BETWEEN ?  AND  ? \n" + 
"AND app_status NOT IN ('RE')\n" + 
"GROUP BY ssi_industry_nature";
//  //System.out.println("the query is =========="+query);

mliApplicationStmt = connection.prepareStatement(query);
mliApplicationStmt.setDate(1, 
      startDate); //Set startDate parameter
mliApplicationStmt.setDate(2, endDate); //Set endDate parameter
//  mliApplicationStmt.setString(3, 
//        id); //Set memberId parameter
mliApplicationResult = mliApplicationStmt.executeQuery();

while (mliApplicationResult.next()) {
//Instantiate a GeneralReport value object
GeneralReport mliApplicationReport = new GeneralReport();
mliApplicationReport.setIndustry(mliApplicationResult.getString(1));
mliApplicationReport.setProposals(mliApplicationResult.getInt(2));
mliApplicationReport.setAmount(mliApplicationResult.getDouble(3));

totalCount=totalCount+mliApplicationResult.getInt(2);                     
double  totalAmounttemp=totalAmount+mliApplicationResult.getDouble(3);
DecimalFormat decFormat = new DecimalFormat("#.00");  
totalAmount = new Double(decFormat.format(totalAmounttemp)).doubleValue(); 
mliApplicationReport.setTotalCount(totalCount);
mliApplicationReport.setTotalAmount(totalAmount);

mliApplicationArray.add(mliApplicationReport);
}
mliApplicationResult.close();
mliApplicationResult = null;
mliApplicationStmt.close();
mliApplicationStmt = null;

} catch (Exception exception) {
Log.logException(exception);
throw new DatabaseException(exception.getMessage());
} finally {
DBConnection.freeConnection(connection);
}

return mliApplicationArray;

}

//kot


public ActionForward calenderAdd(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(4, "NewReportsAction", "listOfPendingCases", "Entered");
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
	generalReport.setDateOfTheDocument40(prevDate);
	generalReport.setDateOfTheDocument41(date);
	BeanUtils.copyProperties(dynaForm, generalReport);
	dynaForm.set("memberId", "");
	dynaForm.set("ssi_name", "");
	Log.log(4, "NewReportsAction", "listOfPendingCases", "Exited");
	return mapping.findForward("success");
}


public ActionForward holidayListReportInput(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(4, "NewReportsAction", "listOfPendingCases", "Entered");
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
	generalReport.setDateOfTheDocument40(prevDate);
	generalReport.setDateOfTheDocument41(date);
	BeanUtils.copyProperties(dynaForm, generalReport);
	dynaForm.set("memberId", "");
	dynaForm.set("ssi_name", "");
	Log.log(4, "NewReportsAction", "listOfPendingCases", "Exited");
	return mapping.findForward("success");
}

public ActionForward InsertHolidayList(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(4, "NewReportsAction", "listOfPendingCases", "Entered");
	HttpSession session = request.getSession();
	
	Statement stmt = null;
	ResultSet result = null;
	Connection connection = DBConnection.getConnection();
	DynaActionForm dynaForm = (DynaActionForm) form;
	 String wk="";
	// ResultSet result = null;

	Date fromdate = (Date) dynaForm.get("dateOfTheDocument13");
	
	 String id             = (String) dynaForm.get("checkValue");
	

	 
	//java.sql.Date d=new java.sql.Date(fromdate.getDate());
	////System.out.println(d);
	
	
	
	
	 int day=fromdate.getDate();
	
	 Calendar calendar       = Calendar.getInstance();
	 calendar.setTime(fromdate);
	 int week=calendar.get(Calendar.DAY_OF_WEEK);
	
	 if(id.equals("no"))
	 {

if(week==1)
{
  wk="SUNDAY";
  throw new NoDataException("YOU CAN NOT CHANGE SUNDAY AS WORKINGDAY");
}

 else 
	 if(week==2)
	 {
		  wk="MONDAY";
	 }
	 else 
		 if(week==3)
		 {
			  wk="TUESDAY";
		 }
		 else 
			 if(week==4)
			 {
				  wk="WEDNESDAY";
			 }
			 else 
				 if(week==5)
				 {
					  wk="THURSDAY";
				 }
				 else 
					 if(week==6)
					 {
						  wk="FRIDAY";
					 }
					 else 
						 if(week==7)
						 {
							  wk="SATURDAY";
							  throw new NoDataException("YOU CAN NOT CHANGE SATURDAY AS WORKINGDAY");
						 }

try {
	
	stmt = connection.createStatement();
	String query = "SELECT * from  CALENDAR_WORKING_HOLIDAY  WHERE CWH_WORKING_HOLIDAY='H'  and  cwh_date = to_date('" +fromdate + "','dd/mm/yyyy') ";
	
	result = stmt.executeQuery(query);
	while (!result.next()) {
		throw new NoDataException(" PLEASE CHECK YOUR DATE . DATE IS NOT IN HOLIDAYS LIST");
	
	}
	//stmt = connection.createStatement();
	//String query1 = "delete   CALENDAR_WORKING_HOLIDAY  WHERE cwh_date = to_date('" +fromdate + "','dd/mm/yyyy') ";
	String query1 = "UPDATE  CALENDAR_WORKING_HOLIDAY SET  CWH_WORKING_HOLIDAY='W'  WHERE cwh_date = to_date('" +fromdate + "','dd/mm/yyyy') ";
	
	
	
	int i = stmt.executeUpdate(query1);
	
	
	result = null;
	stmt = null;
} catch (Exception exception) {
	Log.logException(exception);
	throw new DatabaseException(exception.getMessage());
}



	 }

	 
	 else {
	 
	 
	 if(id.equals("yes"))
			 {
	 
	 if(week==1)
	 {
		  wk="SUNDAY";
		  throw new NoDataException("YOU CAN NOT CHANGE SUNDAY AS HOLIDAY");
	 }
	 
		 else 
			 if(week==2)
			 {
				  wk="MONDAY";
			 }
			 else 
				 if(week==3)
				 {
					  wk="TUESDAY";
				 }
				 else 
					 if(week==4)
					 {
						  wk="WEDNESDAY";
					 }
					 else 
						 if(week==5)
						 {
							  wk="THURSDAY";
						 }
						 else 
							 if(week==6)
							 {
								  wk="FRIDAY";
							 }
							 else 
								 if(week==7)
								 {
									  wk="SATURDAY";
									  throw new NoDataException("YOU CAN NOT CHANGE SATURDAY AS HOLIDAY");
								 }
	 
			 }
	 
	
	
	 
	try {
		stmt = connection.createStatement();
		String query1 = "SELECT * from  CALENDAR_WORKING_HOLIDAY  WHERE  CWH_WORKING_HOLIDAY='H'  and  cwh_date = to_date('" +fromdate + "','dd/mm/yyyy') ";
		
		result = stmt.executeQuery(query1);
		while (result.next()) {
			throw new NoDataException("PLEASE CHECK YOUR DATE . ALREADY DATE IS ADDED IN HOLIDAYS LIST");
			
			//Date ddd = result.getDate(1);
		}         
		//String query = "INSERT INTO CALENDAR_WORKING_HOLIDAY  VALUES ((to_date(fromdate,'dd/mm/yyyy'),'','')";
		//String query = "INSERT INTO CALENDAR_WORKING_HOLIDAY  VALUES (to_date('" +fromdate + "','dd/mm/yyyy'),'"+wk+"','H')";
		String query = "UPDATE  CALENDAR_WORKING_HOLIDAY SET  CWH_WORKING_HOLIDAY='H'  WHERE cwh_date = to_date('" +fromdate + "','dd/mm/yyyy') ";
		int i = stmt.executeUpdate(query);
		
		
		result = null;
		stmt = null;
	} catch (Exception exception) {
		Log.logException(exception);
		throw new DatabaseException(exception.getMessage());
	}
	 }
	
	
	
	return mapping.findForward("success");
}


public ActionForward holidayListReport(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {

	 ArrayList datesList    = new ArrayList();
	 Statement stmt        = null;
     ResultSet result      = null;
     
     Connection connection = DBConnection.getConnection();
     HttpSession session    = request.getSession();
     
 	DynaActionForm dynaForm = (DynaActionForm) form;
     Date fromdat = (Date) dynaForm.get("dateOfTheDocument14");
     
     Date toDate = (Date) dynaForm.get("dateOfTheDocument15");
     
    
try
{
   String query = " SELECT to_char(CWH_DATE),CWH_DAY,CWH_WORKING_HOLIDAY from  CALENDAR_WORKING_HOLIDAY  WHERE cwh_date   between  to_date('" +fromdat + "','dd/mm/yyyy')  and to_date('" +toDate + "','dd/mm/yyyy')  and CWH_WORKING_HOLIDAY='H' " ;
   stmt   = connection.createStatement();
   result = stmt.executeQuery(query);
   String dates[] = null;
   while(result.next())
   {
	   dates = new String[3];
	   
	   /*//System.out.println(result.getString(1));
	   //System.out.println(result.getString(2));	   
	   //System.out.println(result.getString(3));*/
	   dates[0] = result.getString(1);
	   dates[1] = result.getString(2);
	   dates[2] = result.getString(3);
      datesList.add(dates);
    }
   session.setAttribute("fromdat",fromdat);
   session.setAttribute("toDate",toDate);
    session.setAttribute("datesList",datesList);
    session.setAttribute("datesListSize",datesList.size());
    result = null;
    stmt = null;


}
catch(Exception exception)
{
  Log.logException(exception);
  throw new DatabaseException(exception.getMessage());
}
return mapping.findForward("success");
}
/*public ActionForward calenderAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
throws Exception
{

// RPActionForm rpActionForm = (RPActionForm)form;

DynaActionForm dynaForm=(DynaActionForm)form;
Date date               = new Date();
Calendar calendar       = Calendar.getInstance();
calendar.setTime(date);
int month = calendar.get(Calendar.MONTH);
int day   = calendar.get(Calendar.DATE);
month     = month - 1;
day  = day + 1;
calendar.set(Calendar.MONTH, month);
calendar.set(Calendar.DATE,day);
Date prevDate = calendar.getTime();

GeneralReport generalReport = new GeneralReport();
//dynaForm.setd(prevDate);
//dynaForm.setDateOfTheDocument25(date);

generalReport.setDateOfTheDocument42(prevDate);
generalReport.setDateOfTheDocument43(date);
// dynaForm.set("roleNames",roleNames);
//dynaForm.set("roleName",roleName);
//dynaForm.set("roleDescription",roleDescription);


// BeanUtils.copyProperties(dynaForm, generalReport);
return mapping.findForward("success");


}
*/

/* THIS METHOD ADDED BY VINOD@PATH ON 30-01-2015 */
/* CODE GOES HERE */
public ActionForward categoryWiseReportInput(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(4, "NewReportsAction", "categoryWiseReportInput", "Entered");
	DynaActionForm dynaForm = (DynaActionForm) form;
	User user = getUserInformation(request);
	if (user == null) {
		throw new MessageException("Please re-login.");
	}
	dynaForm.initialize(mapping);
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

	Log.log(4, "NewReportsAction", "categoryWiseReportInput", "Exited");
	return mapping.findForward(Constants.SUCCESS);
}

public ActionForward categoryWiseReport(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(Log.INFO, "NewReportsAction", "categoryWiseReport", "Entered");
	DynaActionForm dynaForm = (DynaActionForm) form;

	ArrayList categoryReport = new ArrayList();
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
	String[] ids = (String[]) dynaForm.get("checkValues");

	String memid = (String) dynaForm.get("memberId");
	String yearFlag = (String) dynaForm.get("year");
	// //System.out.println(yearFlag);
	// //System.out.println(ids);
	StringBuffer clm_status = new StringBuffer();
	if ("TR".equalsIgnoreCase(ids[0])) {
		clm_status.append(" 'TR'");
	}
	if ("WD".equalsIgnoreCase(ids[1])) {

		clm_status.append(" 'WD'");

	}
	if ("RE".equalsIgnoreCase(ids[2])) {
		clm_status.append(" 'RE'");
	}
	String st = clm_status.toString();
	// //System.out.println(st.toString());
	st = (String) st.trim();
	// //System.out.println(st.toString());
	st = st.replace(" ", ",");
	// //System.out.println(st.toString());

	// String query = " where clm_status in(" + clm_status.toString() + ")";
	request.setAttribute("STATUS", clm_status.toString());
	request.setAttribute("MEMID", memid);
	request.setAttribute("YEARFLAG", yearFlag);
	NewReportsDAO dao = new NewReportsDAO();
	categoryReport = dao.getCategoryWise(sDate, eDate, memid, st, yearFlag);
	request.setAttribute("categoryReport", categoryReport);
	// dynaForm.set("categoryReport",categoryReport);
	if (categoryReport == null || categoryReport.size() == 0) {
		throw new NoDataException("No Data Found");
	} else {
		categoryReport = null;
		Log.log(Log.INFO, "NewReportsAction", "categoryWiseReport",
				"Exited");
		return mapping.findForward(Constants.SUCCESS);
	}
}

public ActionForward slabStateCategoryReportInput(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(4, "NewReportsAction", "slabStateCategoryReportInput",
			"Entered");
	DynaActionForm dynaForm = (DynaActionForm) form;
	dynaForm.initialize(mapping);
	User user = getUserInformation(request);
	if (user == null) {
		throw new MessageException("Please Login Again");
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
	generalReport.setDateOfTheDocument16(prevDate);
	generalReport.setDateOfTheDocument17(date);
	BeanUtils.copyProperties(dynaForm, generalReport);

	Administrator admin = new Administrator();
	ArrayList states = admin.getAllStates();
	ArrayList industrySectors = admin.getAllIndustrySectors();
	dynaForm.set("industryList", industrySectors);
	dynaForm.set("stateList", states);

	Log.log(4, "NewReportsAction", "slabStateCategoryReportInput", "Exited");
	return mapping.findForward(Constants.SUCCESS);
}

public ActionForward slabStateCategoryReportDetails(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	Log.log(Log.INFO, "NewReportsAction", "slabStateCategoryReportDetails",
			"Entered");
	DynaActionForm dynaForm = (DynaActionForm) form;
	ArrayList SSCReport = new ArrayList();
	java.sql.Date startDate = null;
	java.sql.Date endDate = null;
	java.util.Date sDate = (java.util.Date) dynaForm
			.get("dateOfTheDocument16");
	String stDate = String.valueOf(sDate);
	if (stDate == null || stDate.equals("")) {
		startDate = null;
	} else if (stDate != null) {
		startDate = new java.sql.Date(sDate.getTime());
	}

	java.util.Date eDate = (java.util.Date) dynaForm
			.get("dateOfTheDocument17");
	endDate = new java.sql.Date(eDate.getTime());		

	String state = (String) dynaForm.get("stateName");
	String Dfor = (String) dynaForm.get("year");
	String industry = (String) dynaForm.get("industry");
	String[] categories = (String[]) dynaForm.get("categories");
	String memberId = (String)dynaForm.get("memberId");
	StringBuffer catList = new StringBuffer();
	String cat = null;
	if(state.equals("")){
		state = null;
	}
	/*if(industry.equals("")){
		industry = null;
	}*/
	if(memberId.equals("")){
		memberId = null;
	}

	if ("ALL".equals(categories[3])) {
		catList.append("ALL");
	} else {
		
		if ("SC".equals(categories[0])) {
			if(catList.length() == 0){
				catList.append("('Schedule Caste'");
			}
		}
		if ("ST".equalsIgnoreCase(categories[1])) {
			//catList.append(" 'Schedule Tribe'");
			if(catList.length() == 0){
				catList.append("('Schedule Tribe'");
			}else{
				catList.append(",'Schedule Tribe'");
			}
		}
		if ("OBC".equalsIgnoreCase(categories[2])) {
			if(catList.length() == 0){
				catList.append("('Other Backward Caste'");
			}else{
				catList.append(",'Other Backward Caste'");
			}
		}
		catList.append(")");
	}

	String catStatus = catList.toString();
	////System.out.println(catStatus);
	NewReportsDAO sscReport=new NewReportsDAO();
	SSCReport=sscReport.getSlabStateCategoryReport(sDate, eDate, state, Dfor, industry, catStatus, memberId);
	//dynaForm.set("", sscReport);
	request.setAttribute("StateName", state);
	request.setAttribute("DataFor", Dfor);
	//request.setAttribute("IndustrySector", industry);
	request.setAttribute("SSCReport", SSCReport);
	if(SSCReport==null || SSCReport.size()==0){
		throw new NoDataException("No Data Found");
	}
	else{
		SSCReport = null;
		Log.log(Log.INFO, "NewReportsAction", "slabStateCategoryReportDetails","Exited");
	}
	return mapping.findForward(Constants.SUCCESS);
}

public ActionForward openServiceFeeDanInput(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception{
	Log.log(Log.INFO, "NewReportsAction", "openServiceFeeDanInput","Entered");
	DynaActionForm dynaForm=(DynaActionForm)form;
	String memberId=(String)dynaForm.get("memberId");	
	////System.out.println(memberId);
	return mapping.findForward(Constants.SUCCESS);
}
//bhu 02-mar-2015

public ActionForward showAuditFilesForClaim(ActionMapping mapping,
		ActionForm form, HttpServletRequest request,
		HttpServletResponse response) {
	/* Claim File Attachments */
	ClaimActionForm claimForm = (ClaimActionForm) form;
	NewReportsDAO reportDao = new NewReportsDAO();

	String claimRefNumber = (String) claimForm.getClmRefNumber();
	//String clmRefNo = (String) request.getAttribute("CLAIMREFNO");
	
	Map files = reportDao.getClaimFiles(claimRefNumber, request);		
	
	ArrayList singlefiles = new ArrayList();
	ArrayList multiplefiles = new ArrayList();
	ArrayList tcwcclaimfiles = new ArrayList();
	singlefiles = (ArrayList) files.get("SINGLE");
	multiplefiles = (ArrayList) files.get("MULTIPLE");
	tcwcclaimfiles = (ArrayList) files.get("TCWCCLAIMFILES");
	
	request.setAttribute("SINGLECLAIMFILES", singlefiles);
	request.setAttribute("MULTIPLECLAIMFILES", multiplefiles);
	
	//Map tcfiles = reportDao.getClaimTCFiles(claimRefNumber, request);
	//Map wcfiles = reportDao.getClaimWCFiles(claimRefNumber, request);
	
	
	//claimForm.setTcCounter(tcwcclaimfiles.size());
	request.setAttribute("TCWCFILES", tcwcclaimfiles);
	
	HashMap tcwcamounts = reportDao.getTcClaimAmounts(claimRefNumber);
	
	ArrayList newList = new ArrayList();
	ArrayList singleList = new ArrayList();
	
	newList = (ArrayList) tcwcamounts.get("TCWCAMOUNTS");
	singleList = (ArrayList) tcwcamounts.get("SINGLE");		
	
	request.setAttribute("TCWCAMOUNTS", newList);
	
	String bankRateType = "";
	   if(singleList != null)
           if(singleList.size() != 0){

        		claimForm.setInsuranceFileFlag((String)singleList.get(0));
        		claimForm.setInsuranceReason((String)singleList.get(1));
		claimForm.setBankRateType((String)singleList.get(2));
	claimForm.setSecurityRemarks((String)singleList.get(3));
	claimForm.setRecoveryEffortsTaken((String)singleList.get(4));
	claimForm.setRating((String)singleList.get(5));
	claimForm.setBranchAddress((String)singleList.get(6));
	claimForm.setInvestmentGradeFlag((String)singleList.get(7));
	claimForm.setPlr((Double)singleList.get(8));
	claimForm.setRate((Double)singleList.get(9));
} else
{
    claimForm.setInsuranceFileFlag("No File");
}
	return mapping.findForward(Constants.SUCCESS);
}



public ActionForward closerCgpanInput(ActionMapping mapping,
		ActionForm form,HttpServletRequest request,
		HttpServletResponse response)throws Exception {
	
	Log.log(4, "NewReportsAction", "closerCgpanInput", "Entered");
	HttpSession session = request.getSession();
	DynaActionForm dynaForm = (DynaActionForm)form;
	User user = getUserInformation(request);
	String bankId = user.getBankId();
	String zoneId = user.getZoneId();
	String branchId = user.getBranchId();
	String memberId = bankId.concat(zoneId).concat(branchId);
	/*if(bankId.equals("0000"))
	{
		memberId = "";
		dynaForm.set("memberId", memberId);
	}
	else
	{
		dynaForm.set("MLI_ID", memberId);
	}*/
	
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
	Log.log(4, "NewReportsAction", "closerCgpanInput", "Exited");
	return mapping.findForward("success");
}

public ActionForward closerCgpanDetail(ActionMapping mapping,
		ActionForm form,HttpServletRequest request,
		HttpServletResponse response)throws Exception {
	
	Log.log(Log.INFO, "NewReportsAction", "closerCgpanDetail", "Entered");
	ReportManager reportManager = new ReportManager();
	DynaActionForm dynaForm = (DynaActionForm) form;
	ArrayList closerList = new ArrayList();
	java.sql.Date startDate = null;
	java.sql.Date endDate = null;
	java.util.Date sDate = (java.util.Date) dynaForm
			.get("dateOfTheDocument36");
	
	String stDate = String.valueOf(sDate);
	if ((stDate == null) || (stDate.equals("")))
		startDate = null;
	else if (stDate != null)
		startDate = new java.sql.Date(sDate.getTime());

	java.util.Date eDate = (java.util.Date) dynaForm
			.get("dateOfTheDocument37");
	endDate = new java.sql.Date(eDate.getTime());
	
	BaseAction baseAction = new BaseAction();
	MLIInfo mliInfo = baseAction.getMemberInfo(request);
	String bankId = mliInfo.getBankId();
	String zoneId = mliInfo.getZoneId();
	String branchId = mliInfo.getBranchId();
	
	if(startDate !=null && endDate!=null)
	{
		closerList =getCloserCgpanList(startDate, endDate);
		dynaForm.set("closerCgpanList",closerList);
		if(closerList == null || closerList.size() == 0)
		{
			throw new NoDataException("No Data is available for the values entered,"
							+ " Please Enter Any Other Value ");
		}
		else 
		{
			closerList = null;
			Log.log(Log.INFO, "ReportsAction", "closerCgpanDetail","Exited");
			//return mapping.findForward("success");
		}
	}		
	return mapping.findForward("closeCgpan");
}

public ArrayList getCloserCgpanList(java.sql.Date startDate,
		java.sql.Date endDate) throws DatabaseException
{
	Log.log(Log.INFO, "ReportsManager", "getCloserCgpanList","Entered");
	ArrayList cgpanArray = new ArrayList();
	
	try {
		cgpanArray =closerCgpanDetail(startDate,endDate);
	} catch (Exception exception) {
		throw new DatabaseException(exception.getMessage());
	}
	Log.log(Log.INFO, "ReportsManager", "getCloserCgpanList","Exited");
	return cgpanArray;
}

public ArrayList closerCgpanDetail(java.sql.Date startDate, 
        java.sql.Date endDate) throws DatabaseException
	{
		Log.log(Log.INFO, "ReportDAO", "closerCgpanDetail", "Entered");
    PreparedStatement allCgpanStmt = null;
    ArrayList allCgpanArray = new ArrayList();
    ResultSet allCgpanResult = null;
    Connection connection = DBConnection.getConnection();
    
    if(startDate !=null && endDate !=null)
    {
    	try
    	{
    		String query = " select cl.mem_bnk_id||cl.mem_zne_id||cl.mem_brn_id,cl.cgpan,closure_date," +
    					   " closure_remarks,app_status,request_date" + 
    					   " from application_detail a,app_closure_request cl  where a.cgpan=cl.cgpan" +
    					   " and trunc(cl.closure_date) between ? and ? ";
    		allCgpanStmt = connection.prepareStatement(query);
    		allCgpanStmt.setDate(1, startDate); //Set startDate parameter
    		allCgpanStmt.setDate(2, endDate); //Set endDate parameter
            allCgpanResult = allCgpanStmt.executeQuery();
            
            while (allCgpanResult.next()) {
                //Instantiate a GeneralReport value object
                GeneralReport allCgpanList = new GeneralReport();
                allCgpanList.setMemberId(allCgpanResult.getString(1));
                allCgpanList.setCgpan(allCgpanResult.getString(2));
                allCgpanList.setCloserDate(allCgpanResult.getDate(3));
                allCgpanList.setCloserRemark(allCgpanResult.getString(4));
                allCgpanList.setStatus(allCgpanResult.getString(5));
                allCgpanList.setRequestDate(allCgpanResult.getDate(6));
                allCgpanArray.add(allCgpanList);
            }
            allCgpanResult.close();
            allCgpanResult = null;
            allCgpanStmt.close();
            allCgpanStmt = null;
    	}
    	catch(Exception exception)
    	{
    		Log.logException(exception);
            throw new DatabaseException(exception.getMessage());
    	}
    	finally 
    	{
            DBConnection.freeConnection(connection);
    	}
    }
    Log.log(Log.INFO, "ReportDAO", "closerCgpanDetail", "Exited");
		return allCgpanArray;
	}

//koteswar approve approve divide mli wise

public ActionForward showMliListPathForApproveApprove(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception 
    {
  
        Log.log(Log.INFO,"ReportsAction","showMliListPath","Entered");
        ArrayList mli = new ArrayList();
        DynaActionForm dynaForm = (DynaActionForm)form;
        mli = getAppListMliWiseForApprovApprvnewMLIWise();
        dynaForm.set("mli",mli);
        String forward = "";
        if(mli == null || mli.size()==0)
        {
          Log.log(Log.INFO,"ReportsAction","showMliListPath","Exited");
          request.setAttribute("message","No Applications for Approval Available");
          forward = "success";
          //throw new NoDataException("No Applications for Approval Available");
        }
        else
        {
          mli = null;
          Log.log(Log.INFO,"ReportsAction","showMliListPath","Exited");
          forward = "approvalPageList";
        }
        return mapping.findForward(forward);
  	}

//koteswar end

//koteswar approve approve new


public ArrayList getAppListMliWiseForApprovApprvnewMLIWise() throws DatabaseException
{
	Log.log(Log.INFO,"ReportsManager","getAppListMliWise","Entered");
	ArrayList mliArray = new ArrayList();
	try
	{
		mliArray =getAppListMliWiseForApprovApprvnewMLI();
	}
	catch(Exception exception)
	{
		throw new DatabaseException(exception.getMessage());
	}
	Log.log(Log.INFO,"ReportsManager","getAppListMliWise","Exited");
	return mliArray;
}

//koteswar approve approve new

public ArrayList getAppListMliWiseForApprovApprvnewMLI() throws DatabaseException {


    ////System.out.println("rep1");
    Log.log(Log.INFO, "ReportDAO", "AppListMliWise", "Entered");
    ArrayList mliArray = new ArrayList();
    Connection connection = DBConnection.getConnection();
    GeneralReport mliList = null;
    String status = "A";

    try {
        ////System.out.println("rep2");
        CallableStatement pendingApps = 
            connection.prepareCall("{?=call packGetPackagePath1.funcGetAppCountForApprvApprv(?,?)}");
        pendingApps.registerOutParameter(1, Types.INTEGER);
        pendingApps.registerOutParameter(2, Constants.CURSOR);
        pendingApps.registerOutParameter(3, Types.VARCHAR);
        pendingApps.execute();
        int functionReturnValues = pendingApps.getInt(1);
        if (functionReturnValues == Constants.FUNCTION_FAILURE) {
            ////System.out.println("rep3");
            String error = pendingApps.getString(3);
            pendingApps.close();
            pendingApps = null;
            connection.rollback();
            throw new DatabaseException(error);
        } else {
            ////System.out.println("rep4");
            ResultSet pendingAppsResults = 
                (ResultSet)pendingApps.getObject(2);
            while (pendingAppsResults.next()) {
                mliList = new GeneralReport();
                mliList.setType(pendingAppsResults.getString(1));
//                //System.out.println("Bank Name:" + 
//                                   pendingAppsResults.getString(1) + 
//                                   " count:" + 
//                                   pendingAppsResults.getString(2));

                mliList.setProposals(pendingAppsResults.getInt(2));
                ////System.out.println(""+pendingAppsResults.getString(2));
                mliArray.add(mliList);
            }
            pendingAppsResults.close();
            pendingAppsResults = null;
        }
    }
    // //System.out.println(mliArray.size());
    catch (Exception exception) {
        Log.logException(exception);
        throw new DatabaseException(exception.getMessage());
    } finally {
        DBConnection.freeConnection(connection);
    }
    Log.log(Log.INFO, "ReportDAO", "AppListMliWise", "Exited");
    
    return mliArray;
}


//kuldeep 
public ActionForward mliCheckerReport(ActionMapping mapping,
        ActionForm form,
        HttpServletRequest request,
        HttpServletResponse response
        )	throws Exception {

Log.log(Log.INFO,"ReportsAction","npaReport","Entered");
ArrayList checkersList= new  ArrayList();
DynaActionForm dynaForm   = (DynaActionForm)form;
java.sql.Date startDate   = null;
java.sql.Date endDate     = null;
java.util.Date sDate      = (java.util.Date) dynaForm.get("dateOfTheDocument26");
String stDate             = String.valueOf(sDate);
Connection connection = null;
Statement stmt = null,stmt1;
ResultSet rs = null , rs1=null;

GeneralReport generalReport=null;

connection = DBConnection.getConnection(false);


String modificationQuery = "  SELECT mi.mem_bank_name, MI.MEM_BNK_ID || MI.MEM_ZNE_ID || MI.MEM_BRN_ID, MEM_ZONE_NAME, USR_FIRST_NAME, USR_MIDDLE_NAME, USR_LAST_NAME," +
		" USR_EMP_ID " +
     "  , USR_DSG_NAME, USR_PHONE_NO, USR_EMAIL_ID ,MLI_CHECKER_ID,USR_CREATED_MODIFIED_DT " +
            "            FROM member_info mi, mli_checker_info mci " +
              "   WHERE mi.mem_bnk_id || mi.mem_zne_id || mi.mem_brn_id = mci.mem_bnk_id || mci.mem_zne_id || mci.mem_brn_id " +
              "   AND MCI.USR_STATUS = 'CA' AND mi.MEM_STATUS = 'A' ORDER BY mi.MEM_BANK_NAME,MI.MEM_BNK_ID || MI.MEM_ZNE_ID || MI.MEM_BRN_ID  " ;
//System.out.println("GMA query : "+modificationQuery);

stmt = connection.createStatement();
rs = stmt.executeQuery(modificationQuery);



String forward="";

while(rs.next())
{

	 generalReport=new  GeneralReport();

generalReport.setBankName(rs.getString(1)); //bank name

generalReport.setMemberId(rs.getString(2)); //memberId

generalReport.setZoneName(rs.getString(3)); // 

generalReport.setEmpFName(rs.getString(4)); // 

////System.out.println(rs.getString(4));


generalReport.setEmpMName(rs.getString(5)); // 

generalReport.setEmpLName(rs.getString(6)); // 

generalReport.setEmpId(rs.getString(7)); 

generalReport.setDesignation(rs.getString(8));
//System.out.println("(rs.getString(8)--"+(rs.getString(8)));

generalReport.setPhoneNo(rs.getString(9)); 
//System.out.println("(rs.getString(9)--"+(rs.getString(9)));
generalReport.setEmailId(rs.getString(10)); 
//System.out.println("(rs.getString(10)--"+(rs.getString(10)));
//generalReport.setCheckerId(rs.getString(11));
//System.out.println("(rs.getString(11)--"+(rs.getString(11)));

//generalReport.setDateModify(rs.getString(12));
//System.out.println("(rs.getString(12)--"+(rs.getString(12)));

checkersList.add(generalReport);
}			
	
//	connection.commit();
rs.close();
rs = null;
stmt.close();
stmt = null;

////System.out.println("checkersList.size()"+checkersList.size());
if (checkersList == null || checkersList.size() == 0){				
Log.log(Log.INFO, "GMAction", "showTenureApproval",
	"Emty NPA Updation Approval list");
request.setAttribute("message","No NPA Upgradation details available");
//forward = "success";
throw new MessageException("No NPA Upgradation details available");
}
else

{
//	gmActionForm1.setNpaUpgraDetailReq(npaUpdationList);	
dynaForm.set("danRaised", checkersList);
forward = "listofaccounts";
}
return mapping.findForward(forward);		
}






	public NewReportsAction() {
	}
	
	//added by vinod@path 17-FEB-2016 for Ajax Call
	public ActionForward insertExcludedData(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		
		Log.log(Log.INFO, "NewReportsAction", "insertAjaxData", "Entered");
		PrintWriter printWriter = response.getWriter();
		////System.out.println("NRA insertAjaxData S");
		String receiveData = request.getParameter("item");
		////System.out.println("NRA message : "+receiveData);
		String cgpan = null;
		String memberId = null;
		String checkCgpanMember = null;
		String responseData = receiveData;
		String QueryretrieveDate = null;
		if(receiveData.startsWith("CG") || receiveData.startsWith("cg"))
		{
			////System.out.println("NRA if(receiveData.toUpperCase().startsWith(CG))");
			cgpan = receiveData.toUpperCase();
			////System.out.println("NRA cgpan : "+cgpan);
			checkCgpanMember = "SELECT CGPAN FROM application_detail WHERE CGPAN = '"+cgpan+"'";
			////System.out.println("NRA checkCgpanMember : "+checkCgpanMember);
		}
		else if(receiveData.startsWith("00"))
		{
			////System.out.println("NRA else if(receiveData.startsWith(00))");
			memberId = receiveData;
			////System.out.println("NRA memberId : "+memberId);
			checkCgpanMember = "SELECT MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID FROM application_detail WHERE MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID = '"+memberId+"'";
			////System.out.println("NRA checkCgpanMember : "+checkCgpanMember);
		}	
		Connection connection = DBConnection.getConnection(false);
		PreparedStatement pmst = null;
		Statement statement = null;
		ResultSet rs = null;
		
		String cgpanQuery = "INSERT INTO excluded_paynent_detail_info (CGPAN, MEMBERID, CREATED_DATE) VALUES (?,?,TO_CHAR(SYSDATE))";
		//String memberIdQuery = "INSERT INTO excluded_paynent_detail_info (CGPAN, MEMBERID, CREATED_DATE) VALUES (?, ?, ?)";
		try
		{
			statement = connection.createStatement();
			rs = statement.executeQuery(checkCgpanMember);			
			if(rs.next())
			{			
					////System.out.println("NRA 2");
					QueryretrieveDate = rs.getString(1);
					////System.out.println("NRA QueryretrieveDate : "+QueryretrieveDate);			
			}
			else
			{
				////System.out.println("does not exist.");
				responseData = "Entered CGPAN/MEMBERID Does Not Exist";
				printWriter.print(responseData);
				return null;
			}
						
			
			
			pmst = connection.prepareStatement(cgpanQuery);
			if(cgpan!=null)
			{
			pmst.setString(1, cgpan);
			}
			else
			{
				pmst.setString(1, "null");
			}
			if(memberId!=null)
			{
			pmst.setString(2, memberId);
			}
			else
			{
				pmst.setString(2, "null");
			}
				
						
			pmst.executeUpdate();			
						
			connection.commit();
		}
		catch(Exception sql)
		{
			connection.rollback();
			sql.printStackTrace();
		}
		finally
		{
			if(pmst != null)
			{
				try{ pmst.close();}
				catch (Exception e) { }
			}
			if(statement != null)
			{
				try{ statement.close();}
				catch (Exception e) { }
			}
			DBConnection.freeConnection(connection);
		}				
		//printWriter.print(responseData);
		////System.out.println("NRA insertAjaxData E");
		Log.log(Log.INFO, "NewReportsAction", "insertAjaxData", "Exited");
		return null;
	}
	
	public ActionForward deleteExcludedData(ActionMapping mapping,ActionForm form,
			HttpServletRequest request ,HttpServletResponse response)throws Exception
	{
		Log.log(Log.INFO, "NewReportsAction", "deleteAjaxData", "Entered");
		////System.out.println("NRA deleteAjaxData S");
		DynaActionForm dynaForm = (DynaActionForm)form;
		StringBuffer stringBuffer = new StringBuffer();
		String recStr2 = "";
		String recStr3 = "";
		PrintWriter out = response.getWriter();
		
		Connection connection = DBConnection.getConnection(false);		
		Statement statement = null;
		
		String recStr1 = request.getParameter("listItems");
		////System.out.println("NRA recStr1 : "+recStr1);
		
		String recStrArr[] = recStr1.split(",");
		////System.out.println("NRA recStrArr : "+recStrArr.length);
		
		for(int i = 0; i < recStrArr.length; i++)
		{
			recStr2 = "'"+recStrArr[i]+"'"+",";
			////System.out.println("NRA recStr2 : "+recStr2);
			stringBuffer.append(recStr2);
		}
	//	//System.out.println("NRA stringBuffer : "+stringBuffer);
		//recStr3 = stringBuffer.substring(0, stringBuffer.length()-1);
		////System.out.println("NRA recStr3 : "+recStr3);
		
		String cgpanDel = "DELETE excluded_paynent_detail_info WHERE CGPAN IN("+recStr3+")";
		////System.out.println("NRA cgpanDel : "+cgpanDel);
		String memberDel = "DELETE excluded_paynent_detail_info WHERE MEMBERID IN("+recStr3+")";
		////System.out.println("NRA memberDel : "+memberDel);
		try
		{
			statement = connection.createStatement();
			int counter = statement.executeUpdate(cgpanDel);
		//	//System.out.println("NRA counter : "+counter);
		//	//System.out.println("NRA member Id");
			int memC = statement.executeUpdate(memberDel);
		//	//System.out.println("NRA memC : "+memC);
			/*if(counter > 0)
			{
				connection.commit();
				//System.out.println("NRA connection.commit()");
			}
			else
			{
				connection.rollback();
				//System.out.println("NRA connection.rollback()");
			}*/
			connection.commit();
		//	//System.out.println("NRA connection.commit()");			
		}
		catch(Exception sqldel)
		{
			connection.rollback();
			sqldel.printStackTrace();
		}
		finally
		{
			if(statement != null)
			{
				try { statement.close(); }
				catch (Exception e) { }
			}
			DBConnection.freeConnection(connection);
		}
		////System.out.println("NRA deleteAjaxData E");
		Log.log(Log.INFO, "NewReportsAction", "deleteAjaxData", "Exited");
		return null;
	}
	
	
	
//added by kuldeep@path 25 july 2016
	
	public ActionForward showNpaUpgradationEnter(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

			Log.log(Log.INFO,"NewReportsAction","showNpaUpgradationEnter","Entered");

			DynaActionForm dynaForm = (DynaActionForm)form;
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			month = month - 1;
			day= day + 1;
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE,day);
			Date prevDate = calendar.getTime();
           
			GeneralReport generalReport = new GeneralReport();
			
			generalReport.setDateOfTheDocument45(prevDate);
			generalReport.setDateOfTheDocument46(date);
			
			dynaForm.set("memberId","");
			
			
			BeanUtils.copyProperties(dynaForm, generalReport);
		
			User user = getUserInformation(request);
			String bankId = user.getBankId();
			String zoneId = user.getZoneId();
			String branchId = user.getBranchId();
			String memberId = bankId.concat(zoneId).concat(branchId);

			dynaForm.set("bankId",bankId);
			////System.out.println("bankId:"+bankId);
			if(bankId.equals(Constants.CGTSI_USER_BANK_ID))
			{
				memberId = "";
			}
			dynaForm.set("memberId",memberId);

			Log.log(Log.INFO,"NewReportsAction","showNpaUpgradationEnter","Exited");
			return mapping.findForward("success");
			}




 public ActionForward showNpaUpgradation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     throws Exception
 {
    
     ArrayList npaUpdationList = new ArrayList();
     GeneralReport generalReport = null;
     Connection connection = null;
     Statement stmt = null;
     ResultSet rs = null;
     User user = getUserInformation(request);
     String bankId = user.getBankId();
     String zoneId = user.getZoneId();
     String branchId = user.getBranchId();
     String memberId = (new StringBuilder(String.valueOf(bankId))).append(zoneId).append(branchId).toString();
     String forward = "";
     DynaActionForm dynaForm = (DynaActionForm)form;
     connection = DBConnection.getConnection(false);
    
     String query = "";
     try{
    	 java.sql.Date fromdate   = null;
         java.sql.Date todate     = null;
         java.util.Date sDate      = (java.util.Date) dynaForm.get("dateOfTheDocument45");
        
        	 fromdate = new java.sql.Date (sDate.getTime());
			String memberID = (String) dynaForm.get("memberId");
		
            java.util.Date eDate  =  (java.util.Date) dynaForm.get("dateOfTheDocument46");
             todate               = new java.sql.Date (eDate.getTime());
   
     if (memberID.equals(null) || memberID.equals("")) {
    
    	 /*
    	 (new StringBuilder("select  mem_bank_name,mem_zone_name, a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id,cgpan,ssi_unit_name,a.NPA_EFFECTIVE_DT,NPA_UPGRADE_DT,USR_ID,USR_DSG_NAME,USR_EMP_ID from  npa_upgradation_detail a,   member_info b,mli_checker_info c,ssi_detail s,npa_detail_temp_canc d  where s.bid=d.bid and a.npa_id=d.npa_id and   NUD_MLI_LWR_LEV_AP_BY=USR_ID  and a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id=b.mem_bnk_id||b.mem_zne_id||b.mem_brn_id   and NUD_UPGRADE_CHANG_FLAG='LA'  and  a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id ='")).append(memberId).append("'").toString();
     */
    	 query = " SELECT mem_bank_name, "
    	 + "  mem_zone_name,  "
    	 + "    a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id, "
    	 + "  cgpan, "
    	 + "   ssi_unit_name, "
    	 + "   a.NPA_EFFECTIVE_DT,  "
    	 + "   NPA_UPGRADE_DT,  "
    	 + "  USR_ID,  "
    	 + "   USR_DSG_NAME,  "
    	 + "   USR_EMP_ID  "
    	 + " FROM npa_upgradation_detail a,  "
    	 + "    member_info b,  "
    	 + "    mli_checker_info c,  "
    	 + "   ssi_detail s,  "
    	 + "   npa_detail_temp_canc d  "
    	 + " WHERE     s.bid = d.bid  "
    	 + "   AND a.npa_id = d.npa_id "
    	 + "   AND NUD_MLI_LWR_LEV_AP_BY = USR_ID  "
    	 + "   AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  "
    	 + "        b.mem_bnk_id || b.mem_zne_id || b.mem_brn_id  "
    	 + " AND NUD_UPGRADE_CHANG_FLAG = 'LA'   "
    	    + "   and trunc(NPA_UPGRADE_DT)  >=  to_date('"+sDate+"','DD/MM/YYYY')  "
			+ "   and trunc(NPA_UPGRADE_DT)  <=  to_date('"+eDate+"','DD/MM/YYYY')  ";
    	 
    	// //System.out.println("query1"+query);
     }else{
    	 
    	 query = " SELECT mem_bank_name, "
    		 + "  mem_zone_name, "
    		 + "  a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id,  "
    		 + "  cgpan, " 
    		 + "  ssi_unit_name, "
    		 + "  a.NPA_EFFECTIVE_DT, "
    		 + "  NPA_UPGRADE_DT, "
    		 + "  USR_ID, "
    		 + "  USR_DSG_NAME,  "
    		 + "  USR_EMP_ID  "
    		 + "  FROM npa_upgradation_detail a, "
    		 + "      member_info b, "
    		 + "     mli_checker_info c, "
    		 + "     ssi_detail s, "
    		 + "     npa_detail_temp_canc d "
    		 + "  WHERE     s.bid = d.bid "
    		 + "   AND a.npa_id = d.npa_id "
    		 + "  AND NUD_MLI_LWR_LEV_AP_BY = USR_ID "
    		 + "  AND a.mem_bnk_id || a.mem_zne_id || a.mem_brn_id =  "
    		 + "        b.mem_bnk_id || b.mem_zne_id || b.mem_brn_id  "
    		 + " AND NUD_UPGRADE_CHANG_FLAG = 'LA'  "
        	 + "   and trunc(NPA_UPGRADE_DT)  >=  to_date('"+sDate+"','DD/MM/YYYY') "
				+ "   and trunc(NPA_UPGRADE_DT)  <=  to_date('"+eDate+"','DD/MM/YYYY') "
				+ "   and  c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id='"+memberID+"' " ;
    	 
    	 ////System.out.println("query2"+query);
     }
     stmt = connection.createStatement();
     for(rs = stmt.executeQuery(query); rs.next(); npaUpdationList.add(generalReport))
     {
         generalReport = new GeneralReport();
         generalReport.setBankName(rs.getString(1));
         generalReport.setMemberId(rs.getString(2));
         generalReport.setZoneName(rs.getString(3));
         generalReport.setCgpan(rs.getString(4));
         generalReport.setUnitName(rs.getString(5));
         generalReport.setNpaEffDt(rs.getDate(6));
         generalReport.setNpaUpgraDt(rs.getDate(7));
         generalReport.setUserId(rs.getString(8));
         generalReport.setDesignation(rs.getString(9));
         generalReport.setEmployeeId(rs.getString(10));
     }

     rs.close();
     rs = null;
     stmt.close();
     stmt = null;
      if(npaUpdationList == null || npaUpdationList.size() == 0)
     {
         Log.log(4, "NewReportsAction", "showNpaUpgradation", "Emty NPA Updation Approval list");
         request.setAttribute("message", "No NPA Upgradation details available");
         throw new MessageException("No NPA Upgradation details available");
     } else
     {
    	 
         dynaForm.set("danRaised", npaUpdationList);
         forward = "listofaccounts";
               return mapping.findForward(forward);
     }
 
     }catch(Exception e){
    	 
    	// e.printStackTrace();
    	  throw new DatabaseException(e.getMessage());
    	 
     }
 }

 


	 

 	public ActionForward showQueryStatusReport(ActionMapping mapping, ActionForm form,
 			HttpServletRequest request, HttpServletResponse response)
 			throws Exception {
 		  Log.log(5, "AdministrationAction", "displayNpaRegistrationFormList", "Entered");
 		
 		  AdministrationActionForm adminForm = (AdministrationActionForm)form;
 		  
 		  ArrayList queryDetailsList = new ArrayList();
 		  AdministrationActionForm queryDetails = null;
 		  Connection connection = DBConnection.getConnection(false);
 		  ResultSet rs = null;
 		  Statement stmt = null;
 		  
 		  
 		  java.sql.Date startDate = null;
	        java.sql.Date endDate = null;

	        java.util.Date sDate = 
	            (java.util.Date)adminForm.getDateOfTheDocument();
	        String stDate = String.valueOf(sDate);

	        if ((stDate == null) || (stDate.equals("")))
	            startDate = null;
	        else if (stDate != null) {
	            startDate = new java.sql.Date(sDate.getTime());
	        }
	        String status = (String)adminForm.getCheck1();

	      //  if (guarantee.equals("yes"))
	         //   request.setAttribute("radioValue", "CGPAN");
	      //  else if (guarantee.equals("no")) {
	         //   request.setAttribute("radioValue", "BORROWER/Unit Name");
	      //  }
	        java.util.Date eDate = 
	        	 (java.util.Date)adminForm.getDateOfTheDocument1();
	        endDate = new java.sql.Date(eDate.getTime());
		  
 		  
 		  
 		  
 		  
 		  String queryInfo = "SELECT QRY_ID,MEM_BNK_ID , MEM_ZNE_ID, MEM_BRN_ID, MEM_PHONE_NUMBER, MEM_EMAIL, QUERY_DESC, QUERY_RAISED_BY, to_char(QUERY_RAISED_DT),QUERY_STATUS,DECODE(QUERY_RESOLVR_RESPONS,'','your query is under process',QUERY_RESOLVR_RESPONS) " +
 			"  FROM QUERY_MASTER where     QUERY_STATUS='"+ status + "'   and    trunc(QUERY_RAISED_DT) between to_date('" + sDate + 
                "','dd/mm/yyyy')  and  to_date('" + eDate + 
                "','dd/mm/yyyy') ";
 			  
 			  stmt = connection.createStatement();			
 			 // //System.out.println("ÄA npaRegistQuery : "+queryInfo);
 			  rs = stmt.executeQuery(queryInfo);
 			  
 			  while(rs.next())
 			  {
 				  queryDetails = new AdministrationActionForm();
 				  
 				  queryDetails.setQueryId(rs.getString(1));
 				  
 				  queryDetails.setBankId(rs.getString(2));
 				  ////System.out.println("AA MLI Id : "+rs.getString(1));
 				  
 				  queryDetails.setZoneId(rs.getString(3));
 				  ////System.out.println("AA Zone Name : "+rs.getString(2));
 				  
 				  queryDetails.setBranchId(rs.getString(4));
 				  ////System.out.println("AA Fname : "+rs.getString(3));
 				  
 				  queryDetails.setContPerson(rs.getString(5));
 				  ////System.out.println("AA Mname : "+rs.getString(4));
 				  
 				  queryDetails.setPhoneNo(rs.getString(6));
 				  ////System.out.println("AA Lname : "+rs.getString(5));
 				  
 				  queryDetails.setEmailId(rs.getString(7));
 				  ////System.out.println("AA EID : "+rs.getString(6));
 				  
 				  queryDetails.setDepartments(rs.getString(8));
 				  ////System.out.println("AA Designation : "+rs.getString(7));
 				  
 				  queryDetails.setQueryDescription(rs.getString(9));
 				  
 				  queryDetails.setQueryStatus(rs.getString(10));
 				  ////System.out.println("AA Phone No : "+rs.getString(8));
 				 queryDetails.setQueryResponse(rs.getString(11));
 				  
 				  
 				  ////System.out.println("AA checker ID : "+rs.getString(10));
 				  
 				  queryDetailsList.add(queryDetails);
			  }
				  
 			  
					String forward = "";
					if (queryDetailsList == null || queryDetailsList.size() == 0) {
						request.setAttribute("message",
								"No Applications Available For Approval");
						forward = "success";
					} else {
						adminForm.setMliQueryList(queryDetailsList);
						forward = "queryList";
					}
					rs.close();
					rs = null;
					stmt.close();
					stmt = null;
					connection.close();
		  return mapping.findForward(forward);
 	  }
 	
 	
 	
 	 public ActionForward showQueryStatusReportInput(ActionMapping mapping, ActionForm form, 
             HttpServletRequest request, 
             HttpServletResponse response) throws Exception {
Log.log(4, "ReportsAction", "guaranteeCover", "Entered");

//DynaActionForm dynaForm = (DynaActionForm)form;

AdministrationActionForm adminForm = (AdministrationActionForm)form;
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
BeanUtils.copyProperties(adminForm, generalReport);
Log.log(4, "ReportsAction", "guaranteeCover", "Exited");

return mapping.findForward("success");
}


	
}

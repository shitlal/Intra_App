/**
 * 
 */
package com.cgtsi.reports;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

import com.cgtsi.claim.UploadFileProperties;
import com.cgtsi.common.Constants;
import com.cgtsi.common.Log;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.PropertyLoader;

/**
 * @author path
 * 
 */
public class NewReportsDAO {

	public ArrayList getCategoryWise(Date stDate, Date eDate, String memid,
			String status, String yearFlag) throws Exception {
		Connection con = DBConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		String q1 = "";
		// try {
		if ("FY".equals(yearFlag)) {
			if (memid == null || memid.equals("")) {
				q1 = "select mem_bank_name,clm_status,year,count(tot),sum(amt) from("
						+ " select distinct(no) tot,mem_bank_name,clm_status,year,sum(GUARANTEE) amt from( "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_tc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ // and mem_bank_name='ALLAHABAD BANK'
							// group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_wc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "// and
															// mem_bank_name='ALLAHABAD
															// BANK'
						+
						// group by
						// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_tc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "// -- and
															// mem_bank_name='ALLAHABAD
															// BANK' "
						+
						// group by
						// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_wc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+
						// and mem_bank_name='ALLAHABAD BANK'
						// group by
						// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						")group by mem_bank_name,clm_status,year,no "
						+ ")group by mem_bank_name,clm_status,year "
						+ "order by 1,3 desc ";
				ps = con.prepareStatement(q1);
				// ps.setString(1, status);
				ps.setDate(1, new java.sql.Date(stDate.getTime()));
				ps.setDate(2, new java.sql.Date(eDate.getTime()));

				// ps.setString(4, status);
				ps.setDate(3, new java.sql.Date(stDate.getTime()));
				ps.setDate(4, new java.sql.Date(eDate.getTime()));

				// ps.setString(7, status);
				ps.setDate(5, new java.sql.Date(stDate.getTime()));
				ps.setDate(6, new java.sql.Date(eDate.getTime()));

				// ps.setString(10, status);
				ps.setDate(7, new java.sql.Date(stDate.getTime()));
				ps.setDate(8, new java.sql.Date(eDate.getTime()));

			} else {
				q1 = " select mem_bank_name,clm_status,year,count(tot),sum(amt) from("
						+ " select distinct(no) tot,mem_bank_name,clm_status,year,sum(GUARANTEE) amt from("
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_tc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no  "// --and
																// mem_bank_name='ALLAHABAD
																// BANK' "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						// group by
						// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						+ " union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_wc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // and mem_bank_name='ALLAHABAD BANK'
							// group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_tc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // and mem_bank_name='ALLAHABAD BANK'
							// group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status,FYSTARTDATE(CLM_CREATED_MODIFIED_DT) year, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_wc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						") group by mem_bank_name,clm_status,year,no "
						+ ")group by mem_bank_name,clm_status,year "
						+ "order by 1,3 desc ";
				ps = con.prepareStatement(q1);
				// ps.setString(1, status);
				ps.setDate(1, new java.sql.Date(stDate.getTime()));
				ps.setDate(2, new java.sql.Date(eDate.getTime()));
				ps.setString(3, memid);

				// ps.setString(5, status);
				ps.setDate(4, new java.sql.Date(stDate.getTime()));
				ps.setDate(5, new java.sql.Date(eDate.getTime()));
				ps.setString(6, memid);

				// ps.setString(9, status);
				ps.setDate(7, new java.sql.Date(stDate.getTime()));
				ps.setDate(8, new java.sql.Date(eDate.getTime()));
				ps.setString(9, memid);

				// ps.setString(13, status);
				ps.setDate(10, new java.sql.Date(stDate.getTime()));
				ps.setDate(11, new java.sql.Date(eDate.getTime()));
				ps.setString(12, memid);
			}
		} else {
			if (memid == null || memid.equals("")) {
				q1 = " select mem_bank_name,clm_status,count(tot),sum(amt) from("
						+ " select distinct(no) tot,mem_bank_name,clm_status,sum(GUARANTEE) amt from("
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_tc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_wc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_tc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ // -- and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_wc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						") group by mem_bank_name,clm_status,no "
						+ ")group by mem_bank_name,clm_status "
						+ " order by 1,3 desc ";
				ps = con.prepareStatement(q1);
				// ps.setString(1, status);
				ps.setDate(1, new java.sql.Date(stDate.getTime()));
				ps.setDate(2, new java.sql.Date(eDate.getTime()));

				// ps.setString(4, status);
				ps.setDate(3, new java.sql.Date(stDate.getTime()));
				ps.setDate(4, new java.sql.Date(eDate.getTime()));

				// ps.setString(7, status);
				ps.setDate(5, new java.sql.Date(stDate.getTime()));
				ps.setDate(6, new java.sql.Date(eDate.getTime()));

				// ps.setString(10, status);
				ps.setDate(7, new java.sql.Date(stDate.getTime()));
				ps.setDate(8, new java.sql.Date(eDate.getTime()));

			} else {
				q1 = " select mem_bank_name,clm_status,count(tot),sum(amt) from("
						+ " select distinct(no) tot,mem_bank_name,clm_status,sum(GUARANTEE) amt from("
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_tc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail_temp c,claim_wc_detail_temp c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_tc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // -- and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						" union all "
						+ " select mem_bank_name,clm_status, c.clm_ref_no no,DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) GUARANTEE "
						+ " from member_info m ,claim_detail c,claim_wc_detail c2 ,application_detail a "
						+ " where clm_status in("
						+ status
						+ ") "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)>=? "
						+ " and trunc(CLM_CREATED_MODIFIED_DT)<=? "
						+ // --and trunc(CLM_CREATED_MODIFIED_DT)>='01-apr-2014'
							// and trunc(CLM_CREATED_MODIFIED_DT)<='31-dec-2014'
						" and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id "
						+ " and c.mem_bnk_id||c.mem_zne_id||c.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						// +
						// " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=a.mem_bnk_id||a.mem_zne_id||a.mem_brn_id "
						+ " and a.cgpan=c2.cgpan "
						+ " and c.clm_ref_no=c2.clm_ref_no "
						+ " and m.mem_bnk_id||m.mem_zne_id||m.mem_brn_id=? "
						+ // --and mem_bank_name='ALLAHABAD BANK'
							// --group by
							// mem_bank_name,clm_status,CLM_CREATED_MODIFIED_DT,c.clm_ref_no,APP_REAPPROVE_AMOUNT,APP_APPROVED_AMOUNT
						") group by mem_bank_name,clm_status,no "
						+ ")group by mem_bank_name,clm_status "
						+ "order by 1,3 desc ";
				ps = con.prepareStatement(q1);
				// ps.setString(1, status);
				ps.setDate(1, new java.sql.Date(stDate.getTime()));
				ps.setDate(2, new java.sql.Date(eDate.getTime()));
				ps.setString(3, memid);

				// ps.setString(5, status);
				ps.setDate(4, new java.sql.Date(stDate.getTime()));
				ps.setDate(5, new java.sql.Date(eDate.getTime()));
				ps.setString(6, memid);

				// ps.setString(9, status);
				ps.setDate(7, new java.sql.Date(stDate.getTime()));
				ps.setDate(8, new java.sql.Date(eDate.getTime()));
				ps.setString(9, memid);

				// ps.setString(13, status);
				ps.setDate(10, new java.sql.Date(stDate.getTime()));
				ps.setDate(11, new java.sql.Date(eDate.getTime()));
				ps.setString(12, memid);
			}
		}
		try {
			rs = ps.executeQuery();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if ("FY".equals(yearFlag)) {

				while (rs.next()) {
					String[] str = new String[5];
					str[0] = rs.getString(1);
					str[1] = rs.getString(2);
					Date dt = rs.getDate(3);
					str[4] = sdf.format(dt);
					str[2] = rs.getString(4);
					str[3] = rs.getString(5);
					list.add(str);
				}
			} else {
				while (rs.next()) {
					String[] str = new String[4];
					str[0] = rs.getString(1);
					str[1] = rs.getString(2);
					str[2] = rs.getString(3);
					str[3] = rs.getString(4);
					list.add(str);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				DBConnection.freeConnection(con);
		}
		return list;
	}

	public ArrayList getSlabStateCategoryReport(Date fromDate, Date toDate,
			String state, String dataFlag, String industry, String category,
			String memeberId) throws SQLException, Exception {
		Connection con = DBConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		// java.sql.Date fromDt=new java.sql.Date(fromDate.getTime());
		// java.sql.Date toDt=new java.sql.Date(toDate.getTime());
		ArrayList list = new ArrayList();
		String q1 = "";
		String bankId = null;
		String zoneId = null;
		String branchId = null;
		if (memeberId != null) {
			bankId = memeberId.substring(0, 4);
			zoneId = memeberId.substring(4, 8);
			branchId = memeberId.substring(8, 12);
		}
		try {
			if ("FY".equals(dataFlag)) {
				if ("ALL".equals(category)) {
					q1 = "select  text,cat, sum(num),sum(amt),FYDT, id from("
							+ " SELECT to_number(SUBSTR(range_id,INSTR(range_id,'-')+1)) id,RANGE_DESC||'/-' text,PMR_CHIEF_SOCIAL_CAT cat, COUNT(*) NUM , fystartdate(app_approved_date_time) fydt, "
							+ " ROUND(sum(decode(APP_REAPPROVE_AMOUNT,null,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT)/100000),4) Amt "
							+ " FROM APPLICATION_DETAIL A,SSI_DETAIL s,RANGE_MASTER,promoter_detail p "
							+ " WHERE a.ssi_reference_number = s.ssi_reference_number "
							+ " and p.ssi_reference_number = s.ssi_reference_number "
							+ " AND A.APP_STATUS NOT IN ('RE') "
							+ " AND A.CGPAN IS NOT NULL "
							+ " and trunc(app_approved_date_time) >= nvl(?,'01-jan-2000') "
							+ " and trunc(app_approved_date_time) <= nvl(?,sysdate) "
							+ " and DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) BETWEEN SUBSTR(range_id,1,INSTR(range_id,'-')-1) "
							+ " AND SUBSTR(range_id,INSTR(range_id,'-')+1) "
							+ " and a.mem_bnk_id=NVL(decode("
							+ bankId
							+ ",null,A.MEM_bnk_ID,"
							+ bankId
							+ "),A.MEM_bnk_ID) "
							+ " and a.mem_zne_id=NVL(decode("
							+ zoneId
							+ ",null,A.MEM_zne_ID,"
							+ zoneId
							+ "),A.MEM_zne_ID) "
							+ " and a.mem_brn_id=NVL(decode("
							+ branchId
							+ ",null,A.MEM_brn_ID,"
							+ branchId
							+ "),A.MEM_brn_ID) "
							+ " and s.ssi_state_name = NVL(?, s.ssi_state_name) "
							// + " and s.ssi_industry_sector =  NVL("+
							// industrySector+ ", s.ssi_industry_sector) "
							+ " GROUP BY fystartdate(app_approved_date_time) ,RANGE_DESC,range_id,PMR_CHIEF_SOCIAL_CAT "
							+ ") RANGE_MASTER "
							+ " group by text,cat,FYDT,id order by fydt desc,cat,id";
				} else {
					q1 = "select  text,cat, sum(num),sum(amt),FYDT, id from("
							+ " SELECT to_number(SUBSTR(range_id,INSTR(range_id,'-')+1)) id,RANGE_DESC||'/-' text,PMR_CHIEF_SOCIAL_CAT cat, COUNT(*) NUM , fystartdate(app_approved_date_time) fydt, "
							+ " ROUND(sum(decode(APP_REAPPROVE_AMOUNT,null,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT)/100000),4) Amt "
							+ " FROM APPLICATION_DETAIL A,SSI_DETAIL s,RANGE_MASTER,promoter_detail p "
							+ " WHERE a.ssi_reference_number = s.ssi_reference_number "
							+ " and p.ssi_reference_number = s.ssi_reference_number "
							+ " and PMR_CHIEF_SOCIAL_CAT  in "
							+ category
							+ " AND A.APP_STATUS NOT IN ('RE') "
							+ " AND A.CGPAN IS NOT NULL "
							+ " and trunc(app_approved_date_time) >= nvl(?,'01-jan-2000') "
							+ " and trunc(app_approved_date_time) <= nvl(?,sysdate) "
							+ " and DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) BETWEEN SUBSTR(range_id,1,INSTR(range_id,'-')-1) "
							+ " AND SUBSTR(range_id,INSTR(range_id,'-')+1) "
							+ " and a.mem_bnk_id=NVL(decode("
							+ bankId
							+ ",null,A.MEM_bnk_ID,"
							+ bankId
							+ "),A.MEM_bnk_ID) "
							+ " and a.mem_zne_id=NVL(decode("
							+ zoneId
							+ ",null,A.MEM_zne_ID,"
							+ zoneId
							+ "),A.MEM_zne_ID) "
							+ " and a.mem_brn_id=NVL(decode("
							+ branchId
							+ ",null,A.MEM_brn_ID,"
							+ branchId
							+ "),A.MEM_brn_ID) "
							+ " and s.ssi_state_name = NVL(?, s.ssi_state_name) "
							// + " and s.ssi_industry_sector =  NVL("+
							// industrySector+ ", s.ssi_industry_sector) "
							+ " GROUP BY fystartdate(app_approved_date_time) ,RANGE_DESC,range_id,PMR_CHIEF_SOCIAL_CAT "
							+ ") RANGE_MASTER "
							+ " group by text,cat,FYDT,id order by fydt desc,cat,id";
				}
				ps = con.prepareStatement(q1);
				ps.setDate(1, new java.sql.Date(fromDate.getTime()));
				ps.setDate(2, new java.sql.Date(toDate.getTime()));

				ps.setString(3, state);

			} else {
				if ("ALL".equals(category)) {
					q1 = "select  text,cat, sum(num),sum(amt), id from("
							+ " SELECT to_number(SUBSTR(range_id,INSTR(range_id,'-')+1)) id,RANGE_DESC||'/-' text,PMR_CHIEF_SOCIAL_CAT cat, COUNT(*) NUM, "
							+ " ROUND(sum(decode(APP_REAPPROVE_AMOUNT,null,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT)/100000),4) Amt "
							+ " FROM APPLICATION_DETAIL A,SSI_DETAIL s,RANGE_MASTER,promoter_detail p "
							+ " WHERE a.ssi_reference_number = s.ssi_reference_number "
							+ " and p.ssi_reference_number = s.ssi_reference_number "
							+ " AND A.APP_STATUS NOT IN ('RE') "
							+ " AND A.CGPAN IS NOT NULL "
							+ " and trunc(app_approved_date_time) >= nvl(?,'01-jan-2000') "
							+ " and trunc(app_approved_date_time) <= nvl(?,sysdate) "
							+ " and DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) BETWEEN SUBSTR(range_id,1,INSTR(range_id,'-')-1) "
							+ " AND SUBSTR(range_id,INSTR(range_id,'-')+1) "
							+ " and a.mem_bnk_id=NVL(decode("
							+ bankId
							+ ",null,A.MEM_bnk_ID,"
							+ bankId
							+ "),A.MEM_bnk_ID) "
							+ " and a.mem_zne_id=NVL(decode("
							+ zoneId
							+ ",null,A.MEM_zne_ID,"
							+ zoneId
							+ "),A.MEM_zne_ID) "
							+ " and a.mem_brn_id=NVL(decode("
							+ branchId
							+ ",null,A.MEM_brn_ID,"
							+ branchId
							+ "),A.MEM_brn_ID) "
							+ " and s.ssi_state_name = NVL(?, s.ssi_state_name) "
							// + " and s.ssi_industry_sector =  NVL(" +
							// industrySector+ ", s.ssi_industry_sector) "
							+ " GROUP BY RANGE_DESC,range_id,PMR_CHIEF_SOCIAL_CAT "
							+ ") RANGE_MASTER "
							+ " group by text,cat,id order by cat,id";
				} else {
					q1 = "select  text,cat, sum(num),sum(amt), id from("
							+ " SELECT to_number(SUBSTR(range_id,INSTR(range_id,'-')+1)) id,RANGE_DESC||'/-' text,PMR_CHIEF_SOCIAL_CAT cat, COUNT(*) NUM, "
							+ " ROUND(sum(decode(APP_REAPPROVE_AMOUNT,null,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT)/100000),4) Amt "
							+ " FROM APPLICATION_DETAIL A,SSI_DETAIL s,RANGE_MASTER,promoter_detail p "
							+ " WHERE a.ssi_reference_number = s.ssi_reference_number "
							+ " and p.ssi_reference_number = s.ssi_reference_number "
							+ " and PMR_CHIEF_SOCIAL_CAT  in "
							+ category
							+ " AND A.APP_STATUS NOT IN ('RE') "
							+ " AND A.CGPAN IS NOT NULL "
							+ " and trunc(app_approved_date_time) >= nvl(?,'01-jan-2000') "
							+ " and trunc(app_approved_date_time) <= nvl(?,sysdate) "
							+ " and DECODE(APP_REAPPROVE_AMOUNT,NULL,APP_APPROVED_AMOUNT,APP_REAPPROVE_AMOUNT) BETWEEN SUBSTR(range_id,1,INSTR(range_id,'-')-1) "
							+ " AND SUBSTR(range_id,INSTR(range_id,'-')+1) "
							+ " and a.mem_bnk_id=NVL(decode("
							+ bankId
							+ ",null,A.MEM_bnk_ID,"
							+ bankId
							+ "),A.MEM_bnk_ID) "
							+ " and a.mem_zne_id=NVL(decode("
							+ zoneId
							+ ",null,A.MEM_zne_ID,"
							+ zoneId
							+ "),A.MEM_zne_ID) "
							+ " and a.mem_brn_id=NVL(decode("
							+ branchId
							+ ",null,A.MEM_brn_ID,"
							+ branchId
							+ "),A.MEM_brn_ID) "
							+ " and s.ssi_state_name = NVL(?, s.ssi_state_name) "
							// + " and s.ssi_industry_sector =  NVL(" +
							// industrySector+ ", s.ssi_industry_sector) "
							+ " GROUP BY RANGE_DESC,range_id,PMR_CHIEF_SOCIAL_CAT "
							+ ") RANGE_MASTER "
							+ " group by text,cat,id order by cat,id";
				}
				ps = con.prepareStatement(q1);
				ps.setDate(1, new java.sql.Date(fromDate.getTime()));
				ps.setDate(2, new java.sql.Date(toDate.getTime()));
				ps.setString(3, state);

			}

			rs = ps.executeQuery();

			SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy");
			if ("FY".equals(dataFlag)) {
				while (rs.next()) {
					String[] arr = new String[5];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
					Date dt = rs.getDate(5);
					arr[4] = sdt.format(dt);
					list.add(arr);
				}
			} else {
				while (rs.next()) {
					String[] arrs = new String[4];
					arrs[0] = rs.getString(1);
					arrs[1] = rs.getString(2);
					arrs[2] = rs.getString(3);
					arrs[3] = rs.getString(4);
					list.add(arrs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				DBConnection.freeConnection(con);
			}
		}
		return list;
	}

	public HashMap getClaimFiles(String claimReferenceNumber,
			HttpServletRequest request) {
		/* get fields from claim_files_new_temp */
		/* get fields from claim_tc_files_temp and claim_wc_files_temp */
         //System.out.println("NKS..............."+claimReferenceNumber);
		ArrayList singlefiles = new ArrayList();
		ArrayList multiplefiles = new ArrayList();
		HashMap files = new HashMap();
		/* run a loop to set files */
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String sql = "INSERT INTO CLAIM_FILES_GTT_NEW_TEMP SELECT * FROM CLAIM_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?";
			//System.out.println("NRDAO sql : "+sql);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			int retVal = stmt.executeUpdate();
			//System.out.println("NRDAO retVal : "+retVal);

			String clmsql = " SELECT NPAREPORT_NAME,NPAREPORT_FILE,DILIGENCEREPORT_NAME,DILIGENCEREPORT_FILE, "
					+ " DILIGENCEREPORT_NAME1,DILIGENCEREPORT_FILE1,DILIGENCEREPORT_NAME2,DILIGENCEREPORT_FILE2, "
					+ " POSTINSPECTIONREPORT_NAME,POSTINSPECTIONREPORT_FILE,POSTINSPECTIONREPORT_NAME1, "
					+ " POSTINSPECTIONREPORT_FILE1,POSTINSPECTIONREPORT_NAME2,POSTINSPECTIONREPORT_FILE2, "
					+ " POSTNPAREPORT_NAME,POSTNPAREPORT_FILE,POSTNPAREPORT_NAME1,POSTNPAREPORT_FILE1, "
					+ " POSTNPAREPORT_NAME2,POSTNPAREPORT_FILE2,"
					+ " SUITFILE_NAME,SUITFILE_FILE,FINALVERDICTFILE_NAME,FINALVERDICTFILE_FILE, "
					+ " IDPROOFFILE_NAME,IDPROOFFILE_FILE,IDPROOFFILE_NAME1,IDPROOFFILE_FILE1, "
					+ " IDPROOFFILE_NAME2,IDPROOFFILE_FILE2,OTHERFILE_NAME,OTHERFILE_FILE,OTHERFILE_NAME1, "
					+ " OTHERFILE_FILE1,OTHERFILE_NAME2,OTHERFILE_FILE2,STAFFREPORT_NAME,STAFFREPORT_FILE, "
					+ " INTERNAL_RATING_FILE_NAME,INTERNAL_RATING_FILE "
					+ " FROM CLAIM_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";

			//System.out.println("NRDAO clmsql : "+clmsql);
			stmt = conn.prepareStatement(clmsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();

			if (rs.next()) {
				String fileName = (String) rs.getString(1);
				System.out.println(" fileName "+fileName);
				String filePath = "#";
				System.out.println(" filePath "+filePath);
				Blob blob = (Blob) rs.getBlob(2);
				System.out.println("blob  "+blob);
				if (fileName != null && !"".equals(fileName)) {
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					
					singlefiles.add(map);
				} else {
					singlefiles.add(null);
				}

				HashMap[] maparr = new HashMap[3];
				fileName = (String) rs.getString(3);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(4);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[0] = map;
				} else {
					maparr[0] = null;
				}
				fileName = (String) rs.getString(5);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(6);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				} else {
					maparr[1] = null;
				}

				fileName = (String) rs.getString(7);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(8);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				} else {
					maparr[2] = null;
				}
				multiplefiles.add(maparr);

				maparr = new HashMap[3];
				fileName = (String) rs.getString(9);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(10);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[0] = map;
				} else {
					maparr[0] = null;
				}
				fileName = (String) rs.getString(11);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(12);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				} else {
					maparr[1] = null;
				}
				fileName = (String) rs.getString(13);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(14);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				} else {
					maparr[2] = null;
				}
				multiplefiles.add(maparr);

				maparr = new HashMap[3];
				fileName = (String) rs.getString(15);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(16);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[0] = map;
				} else {
					maparr[0] = null;
				}
				fileName = (String) rs.getString(17);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(18);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				} else {
					maparr[1] = null;
				}
				fileName = (String) rs.getString(19);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(20);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				} else {
					maparr[2] = null;
				}
				multiplefiles.add(maparr);

				fileName = (String) rs.getString(21);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(22);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					singlefiles.add(map);
				} else {
					singlefiles.add(null);
				}
				fileName = (String) rs.getString(23);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(24);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					singlefiles.add(map);
				} else {
					singlefiles.add(null);
				}

				maparr = new HashMap[3];
				fileName = (String) rs.getString(25);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(26);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[0] = map;
				} else {
					maparr[0] = null;
				}

				fileName = (String) rs.getString(27);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(28);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				} else {
					maparr[1] = null;
				}
				fileName = (String) rs.getString(29);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(30);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				} else {
					maparr[2] = null;
				}
				multiplefiles.add(maparr);

				maparr = new HashMap[3];
				fileName = (String) rs.getString(31);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(32);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[0] = map;
				} else {
					maparr[0] = null;
				}
				fileName = (String) rs.getString(33);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(34);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				} else {
					maparr[1] = null;
				}
				fileName = (String) rs.getString(35);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(36);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				} else {
					maparr[2] = null;
				}
				multiplefiles.add(maparr);

				fileName = (String) rs.getString(37);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(38);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					singlefiles.add(map);
				} else {
					singlefiles.add(null);
				}
				fileName = (String) rs.getString(39);

				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(40);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					singlefiles.add(map);
				} else {
					singlefiles.add(null);
				}

			}
			files.put("SINGLE", singlefiles);
			files.put("MULTIPLE", multiplefiles);

			if (rs != null)
				rs.close();
			rs = null;

			sql = "INSERT INTO CLAIM_TC_FILES_GTT_NEW_TEMP (SELECT * FROM CLAIM_TC_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			retVal = stmt.executeUpdate();
			String clmtcsql = " SELECT CGPAN,STATEMENT_NAME,STATEMENT_FILE,STATEMENT_NAME1,STATEMENT_FILE1,STATEMENT_NAME2,STATEMENT_FILE2, "
					+ " ISSAMEAPPRFILE,APPRAISALREPORT_NAME,APPRAISALREPORT_FILE,APPRAISALREPORT_NAME1,APPRAISALREPORT_FILE1, "
					+ " APPRAISALREPORT_NAME2,APPRAISALREPORT_FILE2,ISSAMESANCFILE,SANCTIONLETTER_NAME,SANCTIONLETTER_FILE, "
					+ " SANCTIONLETTER_NAME1,SANCTIONLETTER_FILE1,SANCTIONLETTER_NAME2,SANCTIONLETTER_FILE2,ISSAMECOMPFILE, "
					+ " COMPLIANCEREPORT_NAME,COMPLIANCEREPORT_FILE,COMPLIANCEREPORT_NAME1,COMPLIANCEREPORT_FILE1, "
					+ " COMPLIANCEREPORT_NAME2,COMPLIANCEREPORT_FILE2,ISSAMEPREINSPECFILE,PREINSPECTIONREPORT_NAME, "
					+ " PREINSPECTIONREPORT_FILE,PREINSPECTIONREPORT_NAME1,PREINSPECTIONREPORT_FILE1,PREINSPECTIONREPORT_NAME2, "
					+ " PREINSPECTIONREPORT_FILE2,ISSAMEINSURANCEFILE,INSURANCECOPY_NAME,INSURANCECOPY_FILE,INSURANCECOPY_NAME1, "
					+ " INSURANCECOPY_FILE1,INSURANCECOPY_NAME2,INSURANCECOPY_FILE2 "
					+ " FROM CLAIM_TC_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(clmtcsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			/*
			 * here i need to add files into list and that list would be cgpan
			 * wise. list i am creating by adding elements into maps and map
			 * array. if i am creating list of list then on retrieval i will
			 * have to fetch it in same order to display.
			 * 
			 * -----CGPAN CGPAN 10 F,F,F F 11 F F,F
			 */
			ArrayList newList = new ArrayList();
			while (rs.next()) {
				ArrayList tcfiles = new ArrayList();
				String filePath = "#";
				String cgpan = (String) rs.getString(1);
				HashMap[] maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				String fileName = (String) rs.getString(2);
				Blob blob = (Blob) rs.getBlob(3);
				if (fileName != null && !"".equals(fileName)) {
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				}
				fileName = (String) rs.getString(4);
				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(5);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				}
				fileName = (String) rs.getString(6);
				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(7);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[3] = map;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				String isSameAsPrevious = (String) rs.getString(8);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(9);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(10);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(11);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(12);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(13);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(14);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}
				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(15);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(16);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(17);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(18);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(19);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(20);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(21);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(22);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(23);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(24);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(25);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(26);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(27);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(28);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(29);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(30);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(31);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(32);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(33);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(34);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(35);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(36);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(37);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(38);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(39);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(40);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(41);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(42);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				newList.add(tcfiles);
			}

			sql = "INSERT INTO CLAIM_WC_FILES_GTT_NEW_TEMP SELECT * FROM CLAIM_WC_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			retVal = stmt.executeUpdate();
			String clmwcsql = " SELECT CGPAN,STATEMENT_NAME,STATEMENT_FILE,STATEMENT_NAME1,STATEMENT_FILE1,STATEMENT_NAME2,STATEMENT_FILE2, "
					+ " ISSAMEAPPRFILE,APPRAISALREPORT_NAME,APPRAISALREPORT_FILE,APPRAISALREPORT_NAME1,APPRAISALREPORT_FILE1, "
					+ " APPRAISALREPORT_NAME2,APPRAISALREPORT_FILE2,ISSAMESANCFILE,SANCTIONLETTER_NAME,SANCTIONLETTER_FILE, "
					+ " SANCTIONLETTER_NAME1,SANCTIONLETTER_FILE1,SANCTIONLETTER_NAME2,SANCTIONLETTER_FILE2,ISSAMECOMPFILE, "
					+ " COMPLIANCEREPORT_NAME,COMPLIANCEREPORT_FILE,COMPLIANCEREPORT_NAME1,COMPLIANCEREPORT_FILE1, "
					+ " COMPLIANCEREPORT_NAME2,COMPLIANCEREPORT_FILE2,ISSAMEPREINSPECFILE,PREINSPECTIONREPORT_NAME, "
					+ " PREINSPECTIONREPORT_FILE,PREINSPECTIONREPORT_NAME1,PREINSPECTIONREPORT_FILE1,PREINSPECTIONREPORT_NAME2, "
					+ " PREINSPECTIONREPORT_FILE2,ISSAMEINSURANCEFILE,INSURANCECOPY_NAME,INSURANCECOPY_FILE,INSURANCECOPY_NAME1, "
					+ " INSURANCECOPY_FILE1,INSURANCECOPY_NAME2,INSURANCECOPY_FILE2 "
					+ " FROM CLAIM_WC_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(clmwcsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			
			
			while (rs.next()) {
				ArrayList tcfiles = new ArrayList();
				String filePath = "#";
				String cgpan = (String) rs.getString(1);
				HashMap[] maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				String fileName = (String) rs.getString(2);
				Blob blob = (Blob) rs.getBlob(3);
				if (fileName != null && !"".equals(fileName)) {
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[1] = map;
				}
				fileName = (String) rs.getString(4);
				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(5);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[2] = map;
				}
				fileName = (String) rs.getString(6);
				if (fileName != null && !"".equals(fileName)) {
					blob = (Blob) rs.getBlob(7);
					filePath = getFilePath(blob, fileName, request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					maparr[3] = map;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				String isSameAsPrevious = (String) rs.getString(8);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(9);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(10);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(11);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(12);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(13);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(14);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}
				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(15);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(16);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(17);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(18);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(19);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(20);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(21);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(22);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(23);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(24);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(25);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(26);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(27);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(28);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(29);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(30);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(31);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(32);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(33);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(34);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(35);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				maparr = new HashMap[4];
				if (cgpan != null) {
					HashMap map = new HashMap();
					map.put("CGPAN", cgpan);
					maparr[0] = map;
				}
				isSameAsPrevious = (String) rs.getString(36);

				if ("N".equalsIgnoreCase(isSameAsPrevious)) {
					fileName = (String) rs.getString(37);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(38);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[1] = map;
					}
					fileName = (String) rs.getString(39);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(40);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[2] = map;
					}
					fileName = (String) rs.getString(41);
					if (fileName != null && !"".equals(fileName)) {
						blob = (Blob) rs.getBlob(42);
						filePath = getFilePath(blob, fileName, request);
						HashMap map = new HashMap();
						map.put("FILENAME", fileName);
						map.put("FILEPATH", filePath);
						maparr[3] = map;
					}

				} else {
					maparr[1] = null;
					maparr[2] = null;
					maparr[3] = null;
				}
				tcfiles.add(maparr);

				newList.add(tcfiles);
			}
			files.put("TCWCCLAIMFILES", newList);
			/*
			 * added by vinod@path for legal doc*/
			System.out.println("NPDAO vinod start");
			System.out.println("NRDAO claimReferenceNumber 1: "+claimReferenceNumber);
			ArrayList legalFile = new ArrayList();
			int count_clm_ref_no = 0;
			String ins_cf_tab = null;
			Statement ss = null;			
			String check_clm_ref_no = "SELECT COUNT(*) AS ROWCOUNT FROM CLAIM_FILES WHERE CLM_REF_NO = '"+claimReferenceNumber+"'";
			ss = conn.createStatement();
			rs = ss.executeQuery(check_clm_ref_no);
			while(rs.next())
			{
				count_clm_ref_no = rs.getInt("ROWCOUNT");
				System.out.println("NRDAO count_clm_ref_no : "+count_clm_ref_no);
			}
			
			if(count_clm_ref_no == 0)
			{						
				ins_cf_tab =" INSERT INTO CLAIM_FILES (CLM_REF_NO, CFT_RECALL_NOTICE_FILE, CFT_RECALL_FILE_NAME, CFT_LEGAL_PROCEED_FILE, CFT_LEGAL_FILE_NAME, " +
                 " STAFF_AC_FILE, STAFF_AC_FILE_NAME, SANCTION_LETTER_FILE, SANCTION_LETTER_NAME, SANCTION_LETTER_F1, SANCTION_LETTER_N1, " +
                 " SANCTION_LETTER_F2, SANCTION_LETTER_N2) " +
                 " SELECT CLM_REF_NO, CFT_RECALL_NOTICE_FILE, CFT_RECALL_FILE_NAME, CFT_LEGAL_PROCEED_FILE, CFT_LEGAL_FILE_NAME, STAFF_AC_FILE, " +
                 " STAFF_AC_FILE_NAME, SANCTION_LETTER_FILE, SANCTION_LETTER_NAME, SANCTION_LETTER_F1, SANCTION_LETTER_N1, SANCTION_LETTER_F2," +
                 " SANCTION_LETTER_N2 " +
                 " FROM CLAIM_FILES_TEMP " +
                 " WHERE CLM_REF_NO ='"+ claimReferenceNumber+"'";

				System.out.println("NRDAO ins_cf_tab : "+ins_cf_tab);
				ss = conn.createStatement();
				retVal = ss.executeUpdate(ins_cf_tab);
				System.out.println("NPDAO retVal : "+retVal);
			}
			
			ss = conn.createStatement();
			String sel_legal_file = "SELECT CFT_LEGAL_FILE_NAME, CFT_LEGAL_PROCEED_FILE FROM CLAIM_FILES WHERE CLM_REF_NO = '"+claimReferenceNumber+"'";
			System.out.println("NPDAO sel_legal_file : "+sel_legal_file);
			rs = ss.executeQuery(sel_legal_file);			
			while (rs.next())
			{
				String filePath = "#";
				String fileName =  (String)rs.getString(1);
				Blob blob = (Blob)rs.getBlob(2);
				if(fileName != null && !"".equals(fileName))
				{
					filePath = getFilePath(blob, fileName, request);
					HashMap legalMap = new HashMap();
					legalMap.put("LEGFILENAME", fileName);
					legalMap.put("LEGFILEPATH", filePath);
					System.out.println("LEGFILENAME : "+fileName);
					System.out.println("LEGFILEPATH : "+filePath);
					legalFile.add(legalMap);
				}
				else
				{
					legalFile.add(null);
				}
			}					
			System.out.println("NPDAO vinod end");
			files.put("LEAGEFILE", legalFile);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBConnection.freeConnection(conn);
		}

		return files;
	}

	public HashMap getTcClaimAmounts(String claimReferenceNumber) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap hashMap = new HashMap();
		ArrayList newList = new ArrayList();
		ArrayList singleList = new ArrayList(); 
		try {
			conn = DBConnection.getConnection(false);
			String sql = "INSERT INTO CLAIM_TC_FILES_GTT_NEW_TEMP (SELECT * FROM CLAIM_TC_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			int retVal = stmt.executeUpdate();
			String clmtcsql = " SELECT CGPAN,PRINCIPALREPAYBEFORENPA_AMT,INTERESTREPAYBEFORENPA_AMT,"
					+ "	PRINCIPALRECOAFTERNPA_AMT,INTERESTRECOAFTERNPA_AMT,INTERESTRATE "
					+ " FROM CLAIM_TC_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(clmtcsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			while (rs.next()) {
				HashMap m = new HashMap();
				String cgpan = (String) rs.getString(1);
				double prepayamt = rs.getDouble(2);
				double irepayamt = rs.getDouble(3);
				double prerecoamt = rs.getDouble(4);
				double irerecoamt = rs.getDouble(5);
				double interest = rs.getDouble(6);
				m.put("CGPAN", cgpan);
				m.put("PREPAYAMT", prepayamt);
				m.put("IREPAYAMT", irepayamt);
				m.put("PRECOAMT", prerecoamt);
				m.put("IRECOAMT", irerecoamt);
				m.put("INTEREST", interest);
				newList.add(m);
			}
			sql = "INSERT INTO CLAIM_WC_FILES_GTT_NEW_TEMP (SELECT * FROM CLAIM_WC_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			retVal = stmt.executeUpdate();
			 clmtcsql = " SELECT CGPAN,"
					+ "	PRINCIPALRECOAFTERNPA_AMT,INTERESTRECOAFTERNPA_AMT,INTERESTRATE "
					+ " FROM CLAIM_WC_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(clmtcsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			while (rs.next()) {
				HashMap m = new HashMap();
				String cgpan = (String) rs.getString(1);
				double prerecoamt = rs.getDouble(2);
				double irerecoamt = rs.getDouble(3);
				double interest = rs.getDouble(4);
				m.put("CGPAN", cgpan);
				m.put("PREPAYAMT", 0.0);
				m.put("IREPAYAMT", 0.0);
				m.put("PRECOAMT", prerecoamt);
				m.put("IRECOAMT", irerecoamt);
				m.put("INTEREST", interest);
				newList.add(m);
			}
			hashMap.put("TCWCAMOUNTS", newList);
			sql = "INSERT INTO CLAIM_FILES_GTT_NEW_TEMP "
					+ "(SELECT * FROM CLAIM_FILES_NEW_TEMP@CGINTER WHERE CLM_REF_NO=?)";

			stmt = conn.prepareStatement(sql);

			stmt.setString(1, claimReferenceNumber);
			retVal = stmt.executeUpdate();
			clmtcsql = " SELECT INSURANCE_FLAG,INSURANCE_REASON,BANKRATE_TYPE,SECURITYREMARKS,"
					+ "	RECOVERYEFFORTSTAKEN,RATING,BRANCHADDRESS,INVESTMENTGRADE,PLR,RATE "
					+ " FROM CLAIM_FILES_GTT_NEW_TEMP WHERE CLM_REF_NO=?";
			stmt = conn.prepareStatement(clmtcsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			while (rs.next()) 
			{
				singleList.add(rs.getString(1));
				singleList.add(rs.getString(2));
				singleList.add(rs.getString(3));
				singleList.add(rs.getString(4));
				singleList.add(rs.getString(5));
				singleList.add(rs.getString(6));
				singleList.add(rs.getString(7));
				singleList.add(rs.getString(8));
				singleList.add(rs.getDouble(9));
				singleList.add(rs.getDouble(10));
			}
			System.out.println(singleList);
			hashMap.put("SINGLE", singleList);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBConnection.freeConnection(conn);
		}
		return hashMap;
	}

	public String getFilePath(Blob blob, String fileName,
			HttpServletRequest request) {
		System.out.println("NRDAO getFilePath start");
		Map attachments = new HashMap();		
		byte[] bytesArray = null;
		String formattedToOSPath = null;
		String filePath = null;
		
		try {			
			BufferedInputStream inputStream = null;
					 
			if (blob != null) {
				 
				
				 	inputStream = new BufferedInputStream(blob.getBinaryStream());
				 
				
				// gets the bytes array from the input stream
				bytesArray = readBytes(inputStream);
			}
			if (bytesArray != null) {
				System.out.println("data available");
			}
			// If any file is attached, Add it to the Map
			/*
			 * if (bytesArray != null && bytesArray.length != 0) {
			 * UploadFileProperties uploadFile = new UploadFileProperties();
			 * uploadFile.setFileSize(bytesArray);
			 * uploadFile.setFileName(fileName); attachments.put("recallNotice",
			 * uploadFile); }
			 */
			if (inputStream != null) {
				inputStream.close();
			}

			formattedToOSPath = request.getContextPath() + File.separator
					+ Constants.FILE_DOWNLOAD_DIRECTORY + File.separator
					+ fileName;
			System.out.println("formattedToOSPath:" + formattedToOSPath);

			String realPath = request.getSession(false).getServletContext()
					.getRealPath("");
			System.out.println("realPath:" + realPath);

			String contextPath = PropertyLoader.changeToOSpath(realPath);
			System.out.println("changed to OS path contextPath:" + contextPath);

			filePath = contextPath + File.separator
					+ Constants.FILE_DOWNLOAD_DIRECTORY + File.separator
					+ fileName;
			System.out.println("filePath:" + filePath);

			// FileOutputStream fileOutputStream = new
			// FileOutputStream(filePath);
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			fileOutputStream.write(bytesArray);
			fileOutputStream.flush();
			fileOutputStream.close();
			bytesArray = null;
			blob = null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (SQLException e) {
		  e.printStackTrace();

		} catch (IOException e) {
			
	   	e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("NRDAO getFilePath end");
		return formattedToOSPath;
	}

	private byte[] readBytes(InputStream inputStream) {
		Log.log(Log.DEBUG, "ReportsDAO", "readBytes", "Entered");

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] byteArray = null;
		try {
			byte[] readBlocks = new byte[1024];

			while (inputStream.read(readBlocks) != -1) {
				byteOut.write(readBlocks, 0, readBlocks.length);
			}
			byteArray = byteOut.toByteArray();
			byteOut.close();

		} catch (IOException e) {
			// ioException.printStackTrace();
			Log.log(Log.ERROR, "ReportsDAO", "readBytes",
					"Error is " + e.getMessage());
			Log.logException(e);
		}

		Log.log(Log.DEBUG, "ReportsDAO", "readBytes", "Exited");

		return byteArray;
	}
	//bhu start  26062015
	
	public HashMap getClaimFiles1(String claimReferenceNumber,
			HttpServletRequest request) 
	{
		/* get fields from claim_files_new_temp */
		/* get fields from claim_tc_files_temp and claim_wc_files_temp */

		ArrayList singlefiles = new ArrayList();
		//ArrayList multiplefiles = new ArrayList();
		HashMap files = new HashMap();
		/* run a loop to set files */
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String sql = "INSERT INTO CLAIM_FILES_gtt_test_temp (SELECT * FROM CLAIM_FILES_TEMP@CGINTER WHERE CLM_REF_NO=?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, claimReferenceNumber);
			System.out.println(" before excute  ");
			int retVal = stmt.executeUpdate();
			System.out.println("  after excute "+retVal);
			String clmsql = " SELECT STAFF_AC_FILE_NAME,STAFF_AC_FILE,SANCTION_LETTER_NAME,SANCTION_LETTER_N1,SANCTION_LETTER_F1,SANCTION_LETTER_N2,SANCTION_LETTER_F2"
							+ " FROM CLAIM_FILES_gtt_test_temp WHERE CLM_REF_NO=?";
			System.out.println(" clmsql "+clmsql);
			
			stmt = conn.prepareStatement(clmsql);
			stmt.setString(1, claimReferenceNumber);
			rs = stmt.executeQuery();
			System.out.println("rs :"+rs);
			 if (rs.next()) 
			{
			
				
				String fileName = (String) rs.getString(1);
				System.out.println("fileName "+fileName);
				String filePath = "#";
				System.out.println("filePath "+filePath);
				Blob blob = (Blob) rs.getBlob(2);
				if (fileName != null && !"".equals(fileName)) 
				{
					filePath = getFilePath(blob, fileName, request);
					System.out.println("filePath :"+filePath);

					System.out.println("blob :"+blob+" fileName : "+fileName+" request "+request);
					HashMap map = new HashMap();
					map.put("FILENAME", fileName);
					map.put("FILEPATH", filePath);
					singlefiles.add(map);
				} else
				{
					singlefiles.add(null);
				}
				files.put("SINGLE", singlefiles);
			} 
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				DBConnection.freeConnection(conn);
			}
			return files;
		}
	//bhu end 26062015
	
}
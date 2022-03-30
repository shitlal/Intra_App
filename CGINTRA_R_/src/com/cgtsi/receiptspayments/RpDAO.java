//Source file: D:\\com\\cgtsi\\receiptspayments\\RpDAO.java

package com.cgtsi.receiptspayments;

import com.cgtsi.actionform.RPActionForm;
import com.cgtsi.admin.AdminDAO;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.Message;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.NoApplicationFoundException;
import com.cgtsi.application.SSIDetails;
import com.cgtsi.application.SubScheme;
import com.cgtsi.claim.SettlementDetail;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.Mailer;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoUserFoundException;
import com.cgtsi.guaranteemaintenance.Recovery;
import com.cgtsi.investmentfund.ChequeDetails;
import com.cgtsi.investmentfund.IFProcessor;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.RegistrationDAO;
import com.cgtsi.reports.ApplicationReport;
import com.cgtsi.reports.ReportDAO;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

//import java.util.Collection;
/**
 * Any DB related activities in receipts and payments module is handled here.
 */
public class RpDAO {
	private final String className = "RpDAO";

	/**
	 * @roseuid 39BA05F20177
	 */
	public RpDAO() {

	}

	public String insertVoucherDetails(VoucherDetail voucherDetail,
			String userId, Connection connection) throws DatabaseException,
			MessageException {

		Log.log(Log.INFO, "RpDAO", "insertVoucherDetails", "Entered");

		boolean newConn = false;

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newConn = true;
		}

		String voucherId = "";

		try {

			CallableStatement callable = connection
					.prepareCall("{?=call funcPaymentVoucher(?,?,?,?,?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.setString(2, null);
			callable.setString(3, voucherDetail.getVoucherType());
			// System.out.println("voucherDetail.getVoucherType():"+voucherDetail.getVoucherType());
			callable.setString(4, voucherDetail.getBankGLCode());
			// System.out.println("voucherDetail.getBankGLCode():"+voucherDetail.getBankGLCode());
			callable.setString(5, voucherDetail.getBankGLName());
			// System.out.println("voucherDetail.getBankGLName():"+voucherDetail.getBankGLName());
			callable.setString(6, voucherDetail.getDeptCode());
			// System.out.println("voucherDetail.getDeptCode():"+voucherDetail.getDeptCode());
			callable.setDouble(7, voucherDetail.getAmount());
			// System.out.println("voucherDetail.getAmount():"+voucherDetail.getAmount());
			callable.setString(8, voucherDetail.getNarration());
			// System.out.println("voucherDetail.getNarration():"+voucherDetail.getNarration());

			callable.setString(9, voucherDetail.getManager());
			// System.out.println("voucherDetail.getManager():"+voucherDetail.getManager());
			callable.setString(10, voucherDetail.getAsstManager());
			// System.out.println("voucherDetail.getAsstManager():"+voucherDetail.getAsstManager());
			callable.setString(11, userId);
			// System.out.println("userId:"+userId);
			callable.registerOutParameter(12, Types.VARCHAR);
			callable.registerOutParameter(13, Types.VARCHAR);

			callable.execute();

			int errorCode = callable.getInt(1);
			  System.out.println("errorCode 128:"+errorCode);

			String error = callable.getString(13);
		    System.out.println("error:"+error);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
					"errorCode,error " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "insertVoucherDetails", error);

				connection.rollback();

				callable.close();
				callable = null;
				  System.out.println("errorCode 143:"+errorCode);
				throw new DatabaseException(error);
				 
			}
			voucherId = callable.getString(12);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "voucherId "
					+ voucherId);

			callable.close();
			callable = null;

			callable = connection
					.prepareCall("{?=call funcVoucherTransactionDtl(?,?,?,?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.setString(2, voucherId);

			callable.registerOutParameter(12, Types.VARCHAR);

			ArrayList vouchers = voucherDetail.getVouchers();

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (int i = 0; i < vouchers.size(); i++) {
				Voucher voucher = (Voucher) vouchers.get(i);

				callable.setString(3, voucher.getAcCode());
				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "ac cd "
						+ voucher.getAcCode());
				// System.out.println("insertVoucherDetails"+"ac cd "+voucher.getAcCode());
				callable.setString(4, voucher.getPaidTo());
				// System.out.println("voucher.getPaidTo()"+voucher.getPaidTo());
				/*
				 * if (voucher.getDebitOrCredit().equalsIgnoreCase("D")) {
				 * callable.setString(5,voucher.getAmountInRs()); } else if
				 * (voucher.getDebitOrCredit().equalsIgnoreCase("C")) {
				 * callable.setString(5,"-"+voucher.getAmountInRs()); }
				 */
				callable.setString(5, voucher.getAmountInRs());
				// System.out.println("voucher.getAmountInRs():"+voucher.getAmountInRs());
				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "Amt "
						+ voucher.getAmountInRs());
				callable.setString(6, voucher.getDebitOrCredit());
				// System.out.println("voucher.getDebitOrCredit():"+voucher.getDebitOrCredit());
				callable.setString(7, voucher.getAdvNo());
				// System.out.println("voucher.getAdvNo()"+voucher.getAdvNo());
				Date parsedDate = null;
				if (voucher.getAdvDate() != null) {
					parsedDate = dateFormat.parse(voucher.getAdvDate(),
							new ParsePosition(0));

					Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
							"Adv. Date " + parsedDate);
				}

				if (parsedDate == null) {
					callable.setDate(8, null);
				} else {
					callable.setDate(8, new java.sql.Date(parsedDate.getTime()));
				}

				callable.setString(9, voucher.getInstrumentType());
				// System.out.println("voucher.getInstrumentType():"+voucher.getInstrumentType());
				callable.setString(10, voucher.getInstrumentNo());
				// System.out.println("voucher.getInstrumentNo():"+voucher.getInstrumentNo());

				if (voucher.getInstrumentDate() != null) {
					parsedDate = dateFormat.parse(voucher.getInstrumentDate(),
							new ParsePosition(0));
				}

				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"Instrument Date " + parsedDate);
				// System.out.println("RpDAO"+"insertVoucherDetails"+"Instrument Date "+parsedDate);

				if (parsedDate == null) {
					callable.setDate(11, null);
				} else {
					callable.setDate(11,
							new java.sql.Date(parsedDate.getTime()));
				}

				callable.execute();

				errorCode = callable.getInt(1);
                  System.out.println("errorCode "+errorCode);
				error = callable.getString(12);
				System.out.println("errorCode "+error);
				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"errorCode,error " + errorCode + "," + error);

				if (errorCode == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "RpDAO", "insertVoucherDetails", error);

					connection.rollback();

					callable.close();
					callable = null;
                 System.out.println(" error "+error);
					throw new DatabaseException(error);
				}

				/**
				 * inserting into cheque_issued_detail
				 */
				Log.log(Log.INFO,
						"RpDAO",
						"insertVoucherDetails",
						"voucher.getInstrumentType() : "
								+ voucher.getInstrumentType());
				if (voucher.getInstrumentType() != null
						&& !voucher.getInstrumentType().equals("")
						&& voucher.getDebitOrCredit() != null
						&& !voucher.getDebitOrCredit().equals("")) {
					if (voucher.getDebitOrCredit().equals("C")
							&& voucher.getInstrumentType().equals("CHEQUE")) {
						Log.log(Log.INFO, "RpDAO", "insertVoucherDetails",
								"Inserting cheque issued information ");
						ChequeDetails chequeDetails = new ChequeDetails();
						chequeDetails.setUserId(userId);
						chequeDetails.setChequeAmount(new Double(voucher
								.getAmountInRs()).doubleValue());
						if (voucher.getInstrumentDate() != null) {
							parsedDate = dateFormat.parse(
									voucher.getInstrumentDate(),
									new ParsePosition(0));
						}
						if (parsedDate == null) {
							parsedDate = null;
						} else {
							parsedDate = new java.sql.Date(parsedDate.getTime());
						}
						chequeDetails.setChequeDate(parsedDate);
						chequeDetails
								.setChequeNumber(voucher.getInstrumentNo());
						chequeDetails.setChequeIssuedTo(voucher.getPaidTo());
						chequeDetails.setBankName("IDBI Bank".toUpperCase());

						IFProcessor ifProcessor = new IFProcessor();
						String branchName = ifProcessor
								.getBranchForBank("IDBI Bank");
						chequeDetails.setBranchName(branchName);
						chequeDetailsInsertSuccess(chequeDetails, connection,
								userId);
					}
				}

			}
			callable.close();
			callable = null;

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
					"Inserting BAS information ");
			callable = connection
					.prepareCall("{?= call funcInsertBASDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callable.registerOutParameter(1, Types.INTEGER);

			callable.setDate(2,
					new java.sql.Date(new java.util.Date().getTime()));

			callable.setString(3, voucherDetail.getVoucherType());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getVoucherType()  "
							+ voucherDetail.getVoucherType());

			callable.setString(4, voucherId);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "voucherId  "
					+ voucherId);

			callable.setString(5, null);
			callable.setString(6, voucherDetail.getDeptCode());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getDeptCode()  "
							+ voucherDetail.getDeptCode());

			callable.setString(7, voucherDetail.getBankGLCode());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getBankGLCode()  "
							+ voucherDetail.getBankGLCode());

			callable.setDate(8, null);
			callable.setString(9, null);
			callable.setString(10, null);
			callable.setDouble(11, voucherDetail.getAmount());

			callable.setString(12, userId);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "userId  "
					+ userId);

			callable.setDate(13, null);
			callable.setString(14, null);

			callable.setString(15, voucherDetail.getNarration());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getNarration()  "
							+ voucherDetail.getNarration());

			callable.setDate(16, null);

			callable.registerOutParameter(17, Types.VARCHAR);

			callable.execute();

			errorCode = callable.getInt(1);
         System.out.println(" errorCode"+errorCode);
			error = callable.getString(17);
     System.out.println("error "+error);
			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
					"errorCode,error " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "insertVoucherDetails", error);

				connection.rollback();

				callable.close();
				callable = null;
                 System.out.println("error "+error);
				throw new DatabaseException(error);
			}
			callable.close();
			callable = null;

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
					"Inserting BAS information ");
			callable = connection
					.prepareCall("{?= call funcInsertBASDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callable.registerOutParameter(1, Types.INTEGER);

			callable.setDate(2,
					new java.sql.Date(new java.util.Date().getTime()));

			callable.setString(3, voucherDetail.getVoucherType());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getVoucherType()  "
							+ voucherDetail.getVoucherType());

			callable.setString(4, voucherId);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "voucherId  "
					+ voucherId);

			callable.setString(5, null);
			callable.setString(6, voucherDetail.getDeptCode());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getDeptCode()  "
							+ voucherDetail.getDeptCode());

			callable.setString(12, userId);

			Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails", "userId  "
					+ userId);

			callable.setString(15, voucherDetail.getNarration());

			Log.log(Log.DEBUG,
					"RpDAO",
					"insertVoucherDetails",
					"voucherDetail.getNarration()  "
							+ voucherDetail.getNarration());

			callable.registerOutParameter(17, Types.VARCHAR);

			// Insert into BAS related table.
			Date parsedDate = null;

			for (int i = 0; i < vouchers.size(); i++) {
				Voucher voucher = (Voucher) vouchers.get(i);

				callable.setString(7, voucher.getAcCode());

				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"voucher.getAcCode()  " + voucher.getAcCode());

				if (voucher.getInstrumentDate() != null) {
					parsedDate = dateFormat.parse(voucher.getInstrumentDate(),
							new ParsePosition(0));
				}

				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"Instrument Date " + parsedDate);

				if (parsedDate == null) {
					callable.setDate(8, null);
				} else {
					callable.setDate(8, new java.sql.Date(parsedDate.getTime()));
				}

				callable.setString(9, voucher.getInstrumentType());

				Log.log(Log.DEBUG,
						"RpDAO",
						"insertVoucherDetails",
						"voucherDetail.getInstrumentType()  "
								+ voucher.getInstrumentType());

				callable.setString(10, voucher.getInstrumentNo());
				Log.log(Log.DEBUG,
						"RpDAO",
						"insertVoucherDetails",
						"voucherDetail.getInstrumentNo()  "
								+ voucher.getInstrumentNo());

				callable.setString(11, voucher.getAmountInRs());

				Log.log(Log.DEBUG,
						"RpDAO",
						"insertVoucherDetails",
						"voucherDetail.getAmountInRs()  "
								+ voucher.getAmountInRs());

				parsedDate = null;
				if (voucher.getAdvDate() != null) {
					parsedDate = dateFormat.parse(voucher.getAdvDate(),
							new ParsePosition(0));

					Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
							"Adv. Date " + parsedDate);
				}
				if (parsedDate == null) {
					callable.setDate(13, null);
				} else {
					callable.setDate(13,
							new java.sql.Date(parsedDate.getTime()));
				}

				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"voucherDetail.getAdvNo()  " + voucher.getAdvNo());

				callable.setString(14, voucher.getAdvNo());

				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"voucherDetail.getPaidTo()  " + voucher.getPaidTo());

				callable.setString(16, voucher.getPaidTo());

				callable.execute();

				errorCode = callable.getInt(1);
          System.out.println(" errorCode 503 "+errorCode);
				error = callable.getString(17);
				 System.out.println(" errorCode 505 "+error);
				Log.log(Log.DEBUG, "RpDAO", "insertVoucherDetails",
						"errorCode,error " + errorCode + "," + error);

				if (errorCode == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "RpDAO", "insertVoucherDetails", error);

					connection.rollback();

					callable.close();
					callable = null;
                    System.out.println("error 517 "+error);
					throw new DatabaseException(error);
				}
			}

			callable.close();
			callable = null;

			if (newConn) {
				connection.commit();
			}
		} catch (SQLException e) {
			
			System.out.println(" e.getMessage 529 "+e.getMessage());
			Log.log(Log.ERROR, "RpDAO", "insertVoucherDetails", e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				
				System.out.println(" e.getMessage 529 "+e1.getMessage());
				e1.printStackTrace();
			}

			throw new DatabaseException("Unable to insert Voucher Details");
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "RpDAO", "insertVoucherDetails", "Exited");

		return voucherId;

	}

	public void chequeDetailsInsertSuccess(ChequeDetails chequeDetails,
			Connection connection, String user) throws DatabaseException,
			MessageException {
		Log.log(Log.INFO, "RpDAO", "chequeDetailsInsertSuccess", "Entered");
		boolean newconn = false;
		RpDAO rpDAO = new RpDAO();

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newconn = true;
		}
		CallableStatement chequeStmt = null;

		try {
			chequeStmt = connection
					.prepareCall("{? = call funcAddChequeDetails(?,?,?,?,?,?,?,?,?,?,?)}");
			chequeStmt.registerOutParameter(1, Types.INTEGER);
			chequeStmt.registerOutParameter(12, Types.VARCHAR);
			chequeStmt.setString(2, chequeDetails.getUserId());
			chequeStmt.setDouble(3, chequeDetails.getChequeAmount());
			java.util.Date utilDate1 = chequeDetails.getChequeDate();
			java.sql.Date sqlDate1 = new java.sql.Date(utilDate1.getTime());
			chequeStmt.setDate(4, sqlDate1);
			chequeStmt.setString(5, chequeDetails.getChequeNumber());
			chequeStmt.setString(6, chequeDetails.getChequeIssuedTo());
			chequeStmt.setString(7, chequeDetails.getBankName());
			chequeStmt.setString(8, chequeDetails.getBranchName());
			chequeStmt.setString(9, chequeDetails.getChequeRemarks());
			chequeStmt.setString(10, chequeDetails.getPayId());
			chequeStmt.setString(11, "I");

			chequeStmt.executeQuery();
			int status = chequeStmt.getInt(1);

			if (status == Constants.FUNCTION_FAILURE) {
				String errorCode = chequeStmt.getString(12);
				System.out.println(" errorCode :"+errorCode);
				Log.log(Log.ERROR, "RpDAO", "chequeDetailsInsertSuccess",
						"SP returns a 1." + " Error code is :" + errorCode);
				connection.rollback();
				chequeStmt.close();
				chequeStmt = null;
                System.out.println(" 594 errorCode"+errorCode);
				throw new DatabaseException(errorCode);
			}
			if (newconn) {
				connection.commit();
			}
		} catch (SQLException e) {
			
			System.out.println(" e 602 "+e.getMessage());
			Log.log(Log.ERROR, "RpDAO", "insertChequeDetails", e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("  e1"+e1.getMessage());
			}
    System.out.println("e.getMessage()  "+e.getMessage());
			throw new DatabaseException(e.getMessage());
		} finally {
			if (newconn) {
				DBConnection.freeConnection(connection);
			}

		}
		Log.log(Log.INFO, "RpDAO", "chequeDetailsInsertSuccess", "Exited");
	}

//niteen start
	
	
	public void  insertDeallocationData(String danCgpan ,String danCgpan1 ,String pay_id
			 ,String user) throws DatabaseException,
			MessageException {
		
		Log.log(Log.INFO, "RpDAO", "insertDeallocationData", "Entered");
		
		
		System.out.println("insertDeallocationData 622");
		boolean newconn = false;
		RpDAO rpDAO = new RpDAO();
		Connection connection = DBConnection.getConnection();
		CallableStatement cStmt = null;
		String errorCode="";

		try {
			cStmt = connection
					.prepareCall("{ call proc_dan_deallocation(?,?,?,?,?)}");
			
			System.out.println("proc_dan_deallocation  633");
			 
			//cStmt.registerOutParameter(1, Types.VARCHAR);
			
		    
			cStmt.setString(1, danCgpan);
			cStmt.setString(2, danCgpan1);
			cStmt.setString(3, pay_id);
			cStmt.setString(4, user);
			cStmt.registerOutParameter(5, Types.VARCHAR); 
			System.out.println("danCgpan :"+danCgpan);
			System.out.println("danCgpan :"+danCgpan1);
			System.out.println("danCgpan :"+pay_id);
			System.out.println("danCgpan :"+user);

			cStmt.execute();
			
			 
			  errorCode = cStmt.getString(5);
			  
			  System.out.println("errorCode 647:"+errorCode);
			if (errorCode == null) {
				 
			 
				connection.rollback();
				cStmt.close();
				cStmt = null;

				throw new DatabaseException(errorCode);
			}
			if (newconn) {
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Log.log(Log.ERROR, "RpDAO", "generateASFDANforClaimSettled",
					e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
           System.out.println("getMessage :"+e.getMessage());
			throw new DatabaseException(e.getMessage());
		} finally {
			if (newconn) {
				DBConnection.freeConnection(connection);
			}

		}
		Log.log(Log.INFO, "RpDAO", "generateASFDANforClaimSettled", "Exited");
	}

	
	
	
	
	/**
	 * 
	 * @param cgpan
	 * @param danAmt
	 * @param remarks
	 * @param danType
	 * @throws com.cgtsi.common.DatabaseException
	 * @throws com.cgtsi.common.MessageException
	 */
	public void generateASFDANforClaimSettled(String cgpan, int danAmt,
			String remarks, String danType) throws DatabaseException,
			MessageException {
		Log.log(Log.INFO, "RpDAO", "chequeDetailsInsertSuccess", "Entered");
		boolean newconn = false;
		RpDAO rpDAO = new RpDAO();
		Connection connection = DBConnection.getConnection();
		CallableStatement cStmt = null;

		try {
			cStmt = connection
					.prepareCall("{? = call funcinsadhocdan(?,?,?,?,?,?)}");
			cStmt.registerOutParameter(1, Types.INTEGER);
			cStmt.registerOutParameter(7, Types.VARCHAR);
			cStmt.setString(2, cgpan);
			cStmt.setString(3, danType);
			cStmt.setDouble(4, danAmt);
			cStmt.setString(5, remarks);
			cStmt.setString(6, "CL");

			cStmt.executeQuery();
			int status = cStmt.getInt(1);

			if (status == Constants.FUNCTION_FAILURE) {
				String errorCode = cStmt.getString(7);
				Log.log(Log.ERROR, "RpDAO", "generateASFDANforClaimSettled",
						"SP returns a 1." + " Error code is :" + errorCode);
				connection.rollback();
				cStmt.close();
				cStmt = null;

				throw new DatabaseException(errorCode);
			}
			if (newconn) {
				connection.commit();
			}
		} catch (SQLException e) {
			Log.log(Log.ERROR, "RpDAO", "generateASFDANforClaimSettled",
					e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			throw new DatabaseException(e.getMessage());
		} finally {
			if (newconn) {
				DBConnection.freeConnection(connection);
			}

		}
		Log.log(Log.INFO, "RpDAO", "generateASFDANforClaimSettled", "Exited");
	}

	// / FUNCTION OVERLOADED BY PATH (Ritesh) ON 9NOV2006 TO GENERATE
	// getGuaranteeFeeMemberWise
	/****** Generate CGDAN ***************/

	/**
	 * This method returns an arraylist of DemandAdvice objects for those
	 * applications which are approved on the current system date. The
	 * DemandAdvice will have the following values picked up from the database.
	 * 1. CGPAN 2. Bank Id 3. Zone Id 4. Branch Id 5. Borrower Id 6. Amount
	 * Raised
	 * 
	 * The DemandAdvice object returned will not contain DAN no.
	 * 
	 * OVERLOADED BY RITESH PATH ON 9NOV2006
	 */
	public ArrayList getGuaranteeFeeMemberWise(String memberId,
			java.sql.Date fromDt, java.sql.Date toDt) throws DatabaseException {
		// System.out.println("PATH getGuaranteeFeeMemberWisePath  memberId = "+memberId);
		// System.out.println("RITESH PATH FROMDATE todate in getGuaranteeFeeMemberWise RPDAO = "+fromDt+"   TODATE = "+toDt);
		String methodName = "getGuaranteeFeeMemberWisePath";
		Connection connection = null;
		CallableStatement getGuaranteeFeeMemberWiseStmt = null;
		ArrayList guaranteeFeeMemberWise = null;
		ResultSet resultSet = null;
		DemandAdvice demandAdvice = null;
		int status = 0;
		Log.log(Log.INFO, className, methodName, "Entering");

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.DEBUG, className, methodName, "Member id  " + memberId);
			String exception = "";

			getGuaranteeFeeMemberWiseStmt = connection
					.prepareCall("{?=call packGetAppPendingCGDANGenPath.funcGetAppPendingCGDANGenDt(?,?,?,?)}");
			getGuaranteeFeeMemberWiseStmt
					.registerOutParameter(1, Types.INTEGER);

			getGuaranteeFeeMemberWiseStmt.registerOutParameter(2,
					Constants.CURSOR);

			getGuaranteeFeeMemberWiseStmt
					.registerOutParameter(3, Types.VARCHAR);
			getGuaranteeFeeMemberWiseStmt.setDate(4, fromDt);
			getGuaranteeFeeMemberWiseStmt.setDate(5, toDt);

			Log.log(Log.DEBUG, className, methodName,
					"Before executing Stored Procedure");

			getGuaranteeFeeMemberWiseStmt.execute();
			Log.log(Log.DEBUG, className, methodName,
					"After executing Stored Procedure");
			resultSet = (ResultSet) getGuaranteeFeeMemberWiseStmt.getObject(2);

			Log.log(Log.DEBUG, className, methodName, "ResultSet = "
					+ resultSet.toString());

			status = getGuaranteeFeeMemberWiseStmt.getInt(1);
			// System.out.println("status:"+status);
			Log.log(Log.DEBUG, className, methodName, "Status = " + status);
			if (status != 0) {
				String error = getGuaranteeFeeMemberWiseStmt.getString(3);
				// System.out.println("error:"+error);
				getGuaranteeFeeMemberWiseStmt.close();
				getGuaranteeFeeMemberWiseStmt = null;
				connection.rollback();

				throw new DatabaseException(error);
			}

			int recordCount = 0;
			String bankId = null;
			String zoneId = null;
			String branchId = null;

			while (resultSet.next()) {
				if (recordCount == 0) {
					guaranteeFeeMemberWise = new ArrayList();
				}
				bankId = resultSet.getString(2);
				zoneId = resultSet.getString(3);
				branchId = resultSet.getString(4);

				Log.log(Log.DEBUG, className, methodName, "Member id From DB "
						+ bankId + zoneId + branchId);

				if (!memberId.equals(Constants.ALL)
						&& !memberId.equals(bankId + zoneId + branchId)) {
					Log.log(Log.DEBUG, className, methodName,
							"Skipped Member id is " + bankId + zoneId
									+ branchId);

					continue;
				}
				demandAdvice = new DemandAdvice();

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				demandAdvice.setCgpan(resultSet.getString(1));

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				demandAdvice.setBankId(bankId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				demandAdvice.setZoneId(zoneId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				demandAdvice.setBranchId(branchId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Borrower Id=" + resultSet.getString(5));
				demandAdvice.setBorrowerId(resultSet.getString(5));
				// added by sukumar@path for getting the ssi reference number
				demandAdvice.setSsiRef(resultSet.getString(6));

				guaranteeFeeMemberWise.add(demandAdvice);
				++recordCount;
			}
			// System.out.println("PATH  recordCount "+recordCount);
			resultSet.close();
			resultSet = null;
			getGuaranteeFeeMemberWiseStmt.close();
			getGuaranteeFeeMemberWiseStmt = null;

			connection.commit();

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return guaranteeFeeMemberWise;
	}

	// /////////////////////////////////////////////////////////

	/****** Generate CGDAN ***************/

	/**
	 * This method returns an arraylist of DemandAdvice objects for those
	 * applications which are approved on the current system date. The
	 * DemandAdvice will have the following values picked up from the database.
	 * 1. CGPAN 2. Bank Id 3. Zone Id 4. Branch Id 5. Borrower Id 6. Amount
	 * Raised
	 * 
	 * The DemandAdvice object returned will not contain DAN no.
	 * 
	 * @param null
	 */
	public ArrayList getGuaranteeFeeMemberWise(String memberId)
			throws DatabaseException {
		// System.out.println("PATH getGuaranteeFeeMemberWise  memberId = "+memberId);
		String methodName = "getGuaranteeFeeMemberWise";
		Connection connection = null;
		CallableStatement getGuaranteeFeeMemberWiseStmt = null;
		ArrayList guaranteeFeeMemberWise = null;
		ResultSet resultSet = null;
		DemandAdvice demandAdvice = null;
		int status = 0;
		Log.log(Log.INFO, className, methodName, "Entering");

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.DEBUG, className, methodName, "Member id  " + memberId);
			String exception = "";

			getGuaranteeFeeMemberWiseStmt = connection
					.prepareCall("{?=call packGetAppPendingCGDANGen.funcGetAppPendingCGDANGen(?,?)}");
			getGuaranteeFeeMemberWiseStmt
					.registerOutParameter(1, Types.INTEGER);

			getGuaranteeFeeMemberWiseStmt.registerOutParameter(2,
					Constants.CURSOR);

			getGuaranteeFeeMemberWiseStmt
					.registerOutParameter(3, Types.VARCHAR);

			Log.log(Log.DEBUG, className, methodName,
					"Before executing Stored Procedure");
			getGuaranteeFeeMemberWiseStmt.execute();
			Log.log(Log.DEBUG, className, methodName,
					"After executing Stored Procedure");
			resultSet = (ResultSet) getGuaranteeFeeMemberWiseStmt.getObject(2);

			Log.log(Log.DEBUG, className, methodName, "ResultSet = "
					+ resultSet.toString());

			status = getGuaranteeFeeMemberWiseStmt.getInt(1);
			Log.log(Log.DEBUG, className, methodName, "Status = " + status);
			if (status != 0) {
				String error = getGuaranteeFeeMemberWiseStmt.getString(3);
				getGuaranteeFeeMemberWiseStmt.close();
				getGuaranteeFeeMemberWiseStmt = null;
				connection.rollback();

				throw new DatabaseException(error);
			}

			int recordCount = 0;
			String bankId = null;
			String zoneId = null;
			String branchId = null;

			while (resultSet.next()) {
				if (recordCount == 0) {
					guaranteeFeeMemberWise = new ArrayList();
				}
				bankId = resultSet.getString(2);
				zoneId = resultSet.getString(3);
				branchId = resultSet.getString(4);

				Log.log(Log.DEBUG, className, methodName, "Member id From DB "
						+ bankId + zoneId + branchId);

				if (!memberId.equals(Constants.ALL)
						&& !memberId.equals(bankId + zoneId + branchId)) {
					Log.log(Log.DEBUG, className, methodName,
							"Skipped Member id is " + bankId + zoneId
									+ branchId);

					continue;
				}
				demandAdvice = new DemandAdvice();

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				demandAdvice.setCgpan(resultSet.getString(1));

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				demandAdvice.setBankId(bankId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				demandAdvice.setZoneId(zoneId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				demandAdvice.setBranchId(branchId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Borrower Id=" + resultSet.getString(5));
				demandAdvice.setBorrowerId(resultSet.getString(5));

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : SSI Ref No=" + resultSet.getString(6));

				demandAdvice.setSsiRef(resultSet.getString(6));
				guaranteeFeeMemberWise.add(demandAdvice);
				++recordCount;
			}
			// System.out.println("PATH  recordCount "+recordCount);
			resultSet.close();
			resultSet = null;
			getGuaranteeFeeMemberWiseStmt.close();
			getGuaranteeFeeMemberWiseStmt = null;

			connection.commit();

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return guaranteeFeeMemberWise;
	}

	/**
	 * This method updates the penalty for the service fee in the database. The
	 * method accepts the ServiceFee object as an argument.
	 * 
	 * @param penalty
	 * @param sfdan
	 * @return Boolean
	 * @roseuid 398278B900AB
	 */
	public// **************param sfdan changed to cgpan*************
	void updatePenalty(DemandAdvice demandAdvice, User user)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement updatePenaltyStmt = null;

		int updateStatus = 0;
		boolean updatePenaltyStatus = false;

		double penalty = demandAdvice.getPenalty();
		String cgpan = demandAdvice.getCgpan();

		connection = DBConnection.getConnection(false);

		try {

			updatePenaltyStmt = connection
					.prepareCall("{?=call funcUpdatePenalty(?,?,?,?,?)}");
			updatePenaltyStmt.registerOutParameter(1, Types.INTEGER);
			updatePenaltyStmt.setString(2, demandAdvice.getDanNo());
			updatePenaltyStmt.setString(3, demandAdvice.getCgpan());
			updatePenaltyStmt.setDouble(4, demandAdvice.getPenalty());
			updatePenaltyStmt.setString(5, user.getUserId());
			updatePenaltyStmt.registerOutParameter(6, Types.VARCHAR);
			updatePenaltyStmt.executeQuery();

			int updatePenaltyStmtValue = updatePenaltyStmt.getInt(1);

			if (updatePenaltyStmtValue == Constants.FUNCTION_FAILURE) {

				String error = updatePenaltyStmt.getString(6);

				updatePenaltyStmt.close();
				updatePenaltyStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updatePenaltyStmt.close();
			updatePenaltyStmt = null;

			connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	/**
	 * This method gets all the service fee details that has crossed due date.
	 * The method returns an arraylist of ServiceFee object.
	 * 
	 * @return java.util.ArrayList
	 * @roseuid 3983E45D000F
	 */
	public ArrayList getServiceFeeBeyondDueDate() throws DatabaseException {
		Connection connection = null;
		CallableStatement getServiceFeeBeyondDueDateStmt = null;
		ResultSet resultSet = null;
		ArrayList serviceFeeList = null;
		ServiceFee serviceFee = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getServiceFeeBeyondDueDateStmt = connection
					.prepareCall("{?=call packGetServiceFee.funcGetBorrowerDetails(?,?)}");
			getServiceFeeBeyondDueDateStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getServiceFeeBeyondDueDateStmt.registerOutParameter(2,
					Constants.CURSOR);
			getServiceFeeBeyondDueDateStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);

			getServiceFeeBeyondDueDateStmt.execute();

			int getServiceFeeBeyondDueDateStmtValue = getServiceFeeBeyondDueDateStmt
					.getInt(1);

			if (getServiceFeeBeyondDueDateStmtValue == Constants.FUNCTION_FAILURE) {

				String error = getServiceFeeBeyondDueDateStmt.getString(3);

				getServiceFeeBeyondDueDateStmt.close();
				getServiceFeeBeyondDueDateStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getServiceFeeBeyondDueDateStmt
						.getObject(2);
				exception = getServiceFeeBeyondDueDateStmt.getString(3);

				// condition has to be checked.

				while (resultSet.next()) {
					serviceFee = (ServiceFee) resultSet.getObject(1);
					serviceFeeList.add(serviceFee);
				}
			}

			resultSet.close();
			resultSet = null;
			getServiceFeeBeyondDueDateStmt.close();
			getServiceFeeBeyondDueDateStmt = null;

			connection.commit();

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return serviceFeeList;

	}

	/**
	 * This method updates the guarantee fee details passed as an argument to
	 * the database.
	 * 
	 * @param GuaranteeFee
	 * @return boolean
	 * @roseuid 398CE36E003E
	 */
	public void updateGuaranteeFee(GuaranteeFee guaranteeFee, String userId,
			Connection connection) throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "updateGuaranteeFee", "Entered");

		// Connection connection = null ;
		CallableStatement updateGuaranteeFeeStmt = null;

		int updateStatus = 0;
		boolean newConn = false;

		if (connection == null) {
			connection = DBConnection.getConnection();
			newConn = true;
		}

		try {
			// System.out.println("coming here to update gfee FUNCUPDATEGUARANTEEFEE");
			updateGuaranteeFeeStmt = connection
					.prepareCall("{?=call FUNCUPDATEGUARANTEEFEE(?,?,?,?)}");

			updateGuaranteeFeeStmt.registerOutParameter(1, Types.INTEGER);
			updateGuaranteeFeeStmt.setString(2, guaranteeFee.getCgpan());
			updateGuaranteeFeeStmt.setDouble(3,
					guaranteeFee.getGuaranteeAmount());
			updateGuaranteeFeeStmt.setString(4, userId);
			updateGuaranteeFeeStmt.registerOutParameter(5, Types.VARCHAR);

			updateGuaranteeFeeStmt.executeQuery();
			updateStatus = updateGuaranteeFeeStmt.getInt(1);
			String error = updateGuaranteeFeeStmt.getString(5);

			Log.log(Log.DEBUG, "RpDAO", "updateGuaranteeFee",
					"updateStatus,error " + updateStatus + ", " + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				updateGuaranteeFeeStmt.close();
				updateGuaranteeFeeStmt = null;

				Log.log(Log.ERROR, "RpDAO", "updateGuaranteeFee", "error "
						+ error);

				throw new DatabaseException(error);
			}

			updateGuaranteeFeeStmt.close();
			updateGuaranteeFeeStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "updateGuaranteeFee", "error "
					+ exception.getMessage());
			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "RpDAO", "updateGuaranteeFee", "Exited");
	}

	/**
	 * This method retrieves all the short amount DAN details for the given
	 * member id and returns an arraylist of DemandAdvice object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CBA05001F
	 */
	public ArrayList getShortAmountDANs(String mliId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getShortAmountDANsStmt = null;
		ArrayList ShortAmountDANs = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getShortAmountDANsStmt = connection
					.prepareCall("{?=call packGetBorrowerDetails.funcGetBorrowerDetails(?,?)}");
			getShortAmountDANsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getShortAmountDANsStmt.setString(1, mliId);

			getShortAmountDANsStmt.registerOutParameter(2, Constants.CURSOR);
			getShortAmountDANsStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);

			getShortAmountDANsStmt.execute();

			int getShortAmountDANsStmtValue = getShortAmountDANsStmt.getInt(1);

			if (getShortAmountDANsStmtValue == Constants.FUNCTION_FAILURE) {

				String error = getShortAmountDANsStmt.getString(3);

				getShortAmountDANsStmt.close();
				getShortAmountDANsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				resultSet = (ResultSet) getShortAmountDANsStmt.getObject(2);
				exception = getShortAmountDANsStmt.getString(3);

				// doubt dd whether each element has to be set.

				while (resultSet.next()) {
					demandAdvice = (DemandAdvice) resultSet.getObject(1);
					ShortAmountDANs.add(demandAdvice);
				}
				resultSet.close();

				getShortAmountDANsStmt.close();
				getShortAmountDANsStmt = null;
			}
			connection.commit();
		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return ShortAmountDANs;
	}

	/**
	 * This method retrieves all the CGDAN details for the given member id and
	 * returns an arraylist of DemandAdvice object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CFF130290
	 */
	public ArrayList getDANSummaryForMember(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			getDanDetailsStmt = connection
					.prepareCall("{?= call packGetDanDetails.funcGetDanDetails(?,?,?,?,?,?)}");
			// getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			// System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					// danSummary.setDanId(rsDanDetails.getString(1));
					// danSummary.setNoOfCGPANs(rsDanDetails.getInt(2));
					// danSummary.setAmountDue(rsDanDetails.getDouble(3));
					// danSummary.setDanDate(rsDanDetails.getDate(4));
					danSummary.setCgpan(rsDanDetails.getString(1));
					danSummary.setUnitname(rsDanDetails.getString(2));
					danSummary.setDanId(rsDanDetails.getString(3));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(4));
					danSummary.setAmountDue(rsDanDetails.getDouble(5));
					danSummary.setDanDate(rsDanDetails.getDate(6));

					// added @path on 07-09-2013
					danSummary.setInclSTaxAmnt(rsDanDetails.getDouble(7));
					danSummary.setInclECESSAmnt(rsDanDetails.getDouble(8));
					danSummary.setInclHECESSAmnt(rsDanDetails.getDouble(9));
					danSummary.setSwBhaCessDed(rsDanDetails.getDouble(10));		//Diksha
					danDetails.add(danSummary);
					danSummary = null;
				}
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next()) {
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i = 0; i < danDetails.size(); i++) {
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId)) {
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary = null;
					}
				}

				rsDanDetails.close();
				rsDanDetails = null;
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/* added by sukumar@path for getting Current Year ASF DAN Summary */

	/**
	 * 
	 * @param bankId
	 * @param zoneId
	 * @param branchId
	 * @return ArrayList
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getASFDANSummaryForMember(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			getDanDetailsStmt = connection
					.prepareCall("{?= call PackgetAsfdandetailsModified.funcGetASFDanDetailsModified(?,?,?,?,?,?)}");
			// getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			// System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					danSummary.setDanId(rsDanDetails.getString(1));
					danSummary.setCgpan(rsDanDetails.getString(2));
					danSummary.setUnitname(rsDanDetails.getString(3));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(4));
					danSummary.setAmountDue(rsDanDetails.getDouble(5));
					danSummary.setDanDate(rsDanDetails.getDate(6));
					danSummary.setBranchName(rsDanDetails.getString(7));

					// added @path on 07-09-2013
					danSummary.setInclSTaxAmnt(rsDanDetails.getDouble(8));
					danSummary.setInclECESSAmnt(rsDanDetails.getDouble(9));
					danSummary.setInclHECESSAmnt(rsDanDetails.getDouble(10));
					//Diksha 
					danSummary.setSwBhaCessDed(rsDanDetails.getDouble(11));
					//danSummary.setKrishiKalCess(rsDanDetails.getDouble(12));
                    //Diksha end
					danDetails.add(danSummary);
					danSummary = null;
				}
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next()) {
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i = 0; i < danDetails.size(); i++) {
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId)) {
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary = null;
					}
				}

				rsDanDetails.close();
				rsDanDetails = null;
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/* -------------------------------------------------------------- */

	/**
	 * 
	 * @param bankId
	 * @param zoneId
	 * @param branchId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getExpASFDANSummaryForMember(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			getDanDetailsStmt = connection
					.prepareCall("{?= call PackgetAsfdandetailsExp.funcGetASFDanDetailsExp(?,?,?,?,?,?)}");
			// getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			// System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					danSummary.setDanId(rsDanDetails.getString(1));
					danSummary.setCgpan(rsDanDetails.getString(2));
					danSummary.setUnitname(rsDanDetails.getString(3));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(4));
					danSummary.setAmountDue(rsDanDetails.getDouble(5));
					danSummary.setDanDate(rsDanDetails.getDate(6));
					danSummary.setBranchName(rsDanDetails.getString(7));

					// added @path on 07-09-2013
					danSummary.setInclSTaxAmnt(rsDanDetails.getDouble(8));
					danSummary.setInclECESSAmnt(rsDanDetails.getDouble(9));
					danSummary.setInclHECESSAmnt(rsDanDetails.getDouble(10));

					danDetails.add(danSummary);
					danSummary = null;
				}
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next()) {
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i = 0; i < danDetails.size(); i++) {
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId)) {
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary = null;
					}
				}

				rsDanDetails.close();
				rsDanDetails = null;
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/**
	 * 
	 * @param appRefNo
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getInstrumentSeq() throws DatabaseException {
		Log.log(Log.INFO, "RPDAO", "getInstrumentSeq", "Entered");

		String instrumentSeq = null;
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT TEXMINFUND.NEXTVAL FROM DUAL";
			pStmt = connection.prepareStatement(query);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				instrumentSeq = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			// System.out.println("instrumentSeq-"+instrumentSeq);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return instrumentSeq;
	}

	/**
	 * 
	 * @param paymentId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getMemberId(String paymentId) throws DatabaseException {
		Log.log(Log.INFO, "RPDAO", "getMemberId", "Entered");
		String memberId = null;

		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		PreparedStatement pStmt = null;

		try {
			String query = " SELECT UNIQUE MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID  MLIID FROM DAN_CGPAN_INFO_TEMP D,APPLICATION_DETAIL A "
					+ " WHERE A.CGPAN=D.CGPAN AND DCI_ALLOCATION_FLAG='Y' AND D.PAY_ID IS NOT NULL AND D.PAY_ID="
					+ "'" + paymentId + "'";
			// System.out.println("Q1:"+query);
			pStmt = connection.prepareStatement(query);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				memberId = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			// System.out.println("memberId-"+memberId);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return memberId;
	}

	/**
	 * 
	 * @param bankId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getBalancePaymentFromOtherFacility(String modeOfPayment)
			throws DatabaseException {
		Log.log(Log.INFO, "RPDAO", "getBalancePaymentFromOtherFacility",
				"Entered");

		double balancePayment = 0;
		PreparedStatement pStmt = null;
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT TOTAL_AMOUNT-(TOTAL_UTIL+TEMP_UTIL) FROM ALLOC_PYMT_OTH_FACILITY WHERE INS_TYPE=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, modeOfPayment);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				balancePayment = rsSet.getDouble(1);
			}
			rsSet.close();
			pStmt.close();
			// System.out.println("getBalancePaymentFromOtherFacility:"+balancePayment);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "RPDAO", "getBalancePaymentFromOtherFacility",
				"Exited");
		return balancePayment;
	}

	/**
	 * 
	 * @param modeOfPayment
	 * @param instrumentAmount
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void updateTempUtilForOtherFacility(String modeOfPayment,
			double instrumentAmount) throws DatabaseException {
		Log.log(Log.INFO, "RPDAO", "updateTempUtilForOtherFacility", "Entered");

		PreparedStatement pStmt = null;
		// ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "UPDATE ALLOC_PYMT_OTH_FACILITY SET TEMP_UTIL = ? WHERE INS_TYPE=?";
			pStmt = connection.prepareStatement(query);

			pStmt.setDouble(1, instrumentAmount);
			pStmt.setString(2, modeOfPayment);

			int i = pStmt.executeUpdate(query);
			pStmt.close();
			if (i == Constants.FUNCTION_FAILURE) {
				connection.rollback();
				throw new DatabaseException(
						"UNABLE TO UPDATE THE TEMP_UTIL AMOUNT IN ALLOC_PYMT_OTH_FACILITY");
			}
			connection.commit();
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "RPDAO", "getBalancePaymentFromOtherFacility",
				"Exited");
	}

	/**
	 * 
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getCGDANSummaryForTextiles() throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		// ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			// getDanDetailsStmt=connection.prepareCall("{?= call packGetGFDanDetailsNew.funcGetGFDanDetailsNew(?,?,?,?,?,?)}");
			getDanDetailsStmt = connection
					.prepareCall("{?= call packGetGFDanDetForHandicrft.funcGetGFDanDetForHandicrft(?,?)}");

			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.registerOutParameter(2, Constants.CURSOR);
			// getDanDetailsStmt.registerOutParameter(3, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(2);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					danSummary.setCgpan(rsDanDetails.getString(1));
					danSummary.setUnitname(rsDanDetails.getString(2));
					danSummary.setDanId(rsDanDetails.getString(3));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(4));
					danSummary.setAmountDue(rsDanDetails.getDouble(5));
					danSummary.setDanDate(rsDanDetails.getDate(6));
					danSummary.setBranchName(rsDanDetails.getString(7));

					danDetails.add(danSummary);
					danSummary = null;
				}

				rsDanDetails.close();
				rsDanDetails = null;
				// rsPaidDetails.close();
				// rsPaidDetails=null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(3);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/* added by sukumar@path for getting CGDAN Summary for Member */

	/**
	 * 
	 * This method retrieves all the CGDAN details for the given member id and
	 * returns an arraylist of DemandAdvice object.
	 * 
	 * @param bankId
	 * @param zoneId
	 * @param branchId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getCGDANSummaryForMember(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			// getDanDetailsStmt=connection.prepareCall("{?= call packGetGFDanDetailsNew.funcGetGFDanDetailsNew(?,?,?,?,?,?)}");
			getDanDetailsStmt = connection
					.prepareCall("{?= call packGetGFDanDetailsModified.funcGetGFDanDetailsModified(?,?,?,?,?,?)}");

			// getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			// System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					danSummary.setCgpan(rsDanDetails.getString(1));
					danSummary.setUnitname(rsDanDetails.getString(2));
					danSummary.setDanId(rsDanDetails.getString(3));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(4));
					danSummary.setAmountDue(rsDanDetails.getDouble(5));
					danSummary.setDanDate(rsDanDetails.getDate(6));
					danSummary.setBranchName(rsDanDetails.getString(7));

					// added @path on 07-09-2013
					danSummary.setInclSTaxAmnt(rsDanDetails.getDouble(8));
					danSummary.setInclECESSAmnt(rsDanDetails.getDouble(9));
					danSummary.setInclHECESSAmnt(rsDanDetails.getDouble(10));
					danSummary.setSwBhaCessDed(rsDanDetails.getDouble(11));	//diksha

					danDetails.add(danSummary);
					danSummary = null;
				}
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next()) {
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i = 0; i < danDetails.size(); i++) {
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId)) {
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary = null;
					}
				}

				rsDanDetails.close();
				rsDanDetails = null;
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/* ---------------------- */
	/* ADDED BY SHYAM */

	/**
	 * This method retrieves all the CGDAN details for the given member id and
	 * returns an arraylist of DemandAdvice object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CFF130290
	 */
	public ArrayList getSFDANSummaryForMember(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus = 0;
		String getDanDetailsErr = "";

		ArrayList danDetails = null;

		connection = DBConnection.getConnection(false);

		try {
			danDetails = new ArrayList();

			// connection=DBConnection.getConnection();
			// System.out.println("Connection established");
			getDanDetailsStmt = connection
					.prepareCall("{?= call packgetsfdandetails.funcGetSFDanDetails(?,?,?,?,?,?)}");
			// getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			// System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus = getDanDetailsStmt.getInt(1);
			if (getSchemesStatus == 0) {
				rsDanDetails = (ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next()) {
					danSummary = new DANSummary();
					danSummary.setDanId(rsDanDetails.getString(1));
					danSummary.setNoOfCGPANs(rsDanDetails.getInt(2));
					danSummary.setAmountDue(rsDanDetails.getDouble(3));
					danSummary.setDanDate(rsDanDetails.getDate(4));
					danSummary.setCgpan(rsDanDetails.getString(5));
					danSummary.setBranchName(rsDanDetails.getString(6));
					danSummary.setUnitname(rsDanDetails.getString(7));

					danDetails.add(danSummary);
					danSummary = null;
				}
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next()) {
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i = 0; i < danDetails.size(); i++) {
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId)) {
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary = null;
					}
				}

				rsDanDetails.close();
				rsDanDetails = null;
				rsPaidDetails.close();
				rsPaidDetails = null;
				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

			} else {
				getDanDetailsErr = getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
			}

			connection.commit();

		} catch (Exception exception) {
			// System.out.println(exception.getMessage());

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

	/* ----------------- */

	/**
	 * This method retrieves all the SFDAN details for the given member id and
	 * returns an arraylist of DemandAdvice object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CFF470128
	 */
	public ArrayList getSFDANDetails(String mliId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getSFDANDetailsStmt = null;
		ArrayList SFDANDetails = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getSFDANDetailsStmt = connection
					.prepareCall("{?=call packGetBorrowerDetails.funcGetBorrowerDetails(?,?)}");
			getSFDANDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getSFDANDetailsStmt.setString(1, mliId);
			getSFDANDetailsStmt.registerOutParameter(2, Constants.CURSOR);
			getSFDANDetailsStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			getSFDANDetailsStmt.execute();

			int getSFDANDetailsStmtValue = getSFDANDetailsStmt.getInt(1);

			if (getSFDANDetailsStmtValue == Constants.FUNCTION_FAILURE) {

				String error = getSFDANDetailsStmt.getString(3);

				getSFDANDetailsStmt.close();
				getSFDANDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				resultSet = (ResultSet) getSFDANDetailsStmt.getObject(2);
				exception = getSFDANDetailsStmt.getString(3);

				// doubt dd whether each element has to be set.

				while (resultSet.next()) {
					demandAdvice = (DemandAdvice) resultSet.getObject(1);
					SFDANDetails.add(demandAdvice);
				}

				resultSet.close();
				resultSet = null;
				getSFDANDetailsStmt.close();
				getSFDANDetailsStmt = null;

			}

			connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return SFDANDetails;
	}

	/**
	 * This method retrieves all the CLDAN details for the given member id and
	 * returns an arraylist of DemandAdvice object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CFF89000F
	 */
	public ArrayList getCLDANDetails(String MLIId) throws DatabaseException {

		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getCLDANDetailsStmt = null;
		ArrayList CLDANDetails = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getCLDANDetailsStmt = connection
					.prepareCall("{?=call packGetBorrowerDetails.funcGetBorrowerDetails(?,?)}");
			getCLDANDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getCLDANDetailsStmt.setString(1, MLIId);
			getCLDANDetailsStmt.registerOutParameter(2, Constants.CURSOR);
			getCLDANDetailsStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			getCLDANDetailsStmt.execute();

			int functionReturnValue = getCLDANDetailsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getCLDANDetailsStmt.getString(3);

				getCLDANDetailsStmt.close();
				getCLDANDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				resultSet = (ResultSet) getCLDANDetailsStmt.getObject(2);
				exception = getCLDANDetailsStmt.getString(3);

				// doubt dd whether each element has to be set.

				while (resultSet.next()) {
					demandAdvice = (DemandAdvice) resultSet.getObject(1);
					CLDANDetails.add(demandAdvice);
				}

				resultSet.close();
				getCLDANDetailsStmt.close();
				getCLDANDetailsStmt = null;

			}

			connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return CLDANDetails;
	}

	/**
	 * This method fetches all short DAN details and returns a DemandAdvice
	 * object.
	 * 
	 * @param MLIId
	 * @return java.util.ArrayList
	 * @roseuid 399CFFD801F4
	 */
	public ArrayList getShortDANs(String MLIId) throws DatabaseException {
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getShortDANsStmt = null;
		ArrayList shortDANs = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getShortDANsStmt = connection
					.prepareCall("{?=call packGetBorrowerDetails.funcGetBorrowerDetails(?,?)}");
			getShortDANsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getShortDANsStmt.setString(1, MLIId);
			getShortDANsStmt.registerOutParameter(2, Constants.CURSOR);
			getShortDANsStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			getShortDANsStmt.execute();

			int functionReturnValue = getShortDANsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getShortDANsStmt.getString(3);

				getShortDANsStmt.close();
				getShortDANsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getShortDANsStmt.getObject(2);
				exception = getShortDANsStmt.getString(3);

				// doubt dd whether each element has to be set.

				while (resultSet.next()) {
					demandAdvice = (DemandAdvice) resultSet.getObject(1);
					shortDANs.add(demandAdvice);
				}

				resultSet.close();
				getShortDANsStmt.close();
				getShortDANsStmt = null;
			}

			connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return shortDANs;
	}

	/**
	 * This method updates the CGDAN details to the database. The method takes
	 * the GuaranteeFee object and the CGDAN Id as parameters.
	 * 
	 * @param GuaranteeFee
	 * @param CGDANId
	 * @return boolean
	 * @roseuid 399E7955003E
	 */
	public// ************params changed to DemandAdvice *************
	boolean insertDANDetails(DemandAdvice demandAdvice, Connection connection)
			throws DatabaseException {
		System.out.println("entered in insert dan setails");
		String methodName = "insertDANDetails";
		// Connection connection = null ;
		CallableStatement insertCGDANDetailsStmt = null;
		CallableStatement insertCGPANDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String formatedDate = "";
		int updateStatus = 0;
		boolean updateCGDANDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		Log.log(Log.INFO, className, methodName, "Entering");
		try {
			// connection = DBConnection.getConnection() ;
			insertCGDANDetailsStmt = connection
					.prepareCall("{?=call funcInsertDANDetails(?,?,?,?,?,?,?,?,?,?)}");
			insertCGDANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.INFO, className, methodName,
					"DAN No : " + demandAdvice.getDanNo());
			insertCGDANDetailsStmt.setString(2, demandAdvice.getDanNo());

			Log.log(Log.INFO, className, methodName, "DAN Type : "
					+ demandAdvice.getDanType());
			insertCGDANDetailsStmt.setString(3, demandAdvice.getDanType());

			utilDate = demandAdvice.getDanGeneratedDate();
			Log.log(Log.INFO, className, methodName, "Generated utilDate: "
					+ utilDate);
			// formatedDate=dateFormat.format(utilDate);Modified by pradeep for
			// newserver on 16.07.2012
			// Log.log(Log.INFO,className,methodName,"Generated Date : "+formatedDate)
			// ;Modified by pradeep for newserver on 16.07.2012
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));Modified
			// by pradeep for newserver on 16.07.2012

			// Log.log(Log.INFO,className,methodName,"Generated Date : "+sqlDate)
			// ;Modified by pradeep for newserver on 16.07.2012
			insertCGDANDetailsStmt.setDate(4,
					new java.sql.Date(utilDate.getTime()));

			Log.log(Log.INFO, className, methodName, "Bank Id : "
					+ demandAdvice.getBankId());
			insertCGDANDetailsStmt.setString(5, demandAdvice.getBankId());

			Log.log(Log.INFO, className, methodName, "Zone Id : "
					+ demandAdvice.getZoneId());
			insertCGDANDetailsStmt.setString(6, demandAdvice.getZoneId());

			Log.log(Log.INFO, className, methodName, "Branch Id : "
					+ demandAdvice.getBranchId());
			insertCGDANDetailsStmt.setString(7, demandAdvice.getBranchId());

			utilDate = demandAdvice.getDanDueDate();
			// formatedDate=dateFormat.format(utilDate);Modified by pradeep for
			// newserver on 16.07.2012
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));Modified
			// by pradeep for newserver on 16.07.2012

			Log.log(Log.INFO, className, methodName, "Due Date : " + utilDate);
			insertCGDANDetailsStmt.setDate(8,
					new java.sql.Date(utilDate.getTime()));

			// utilDate = demandAdvice.getDanExpiryDate() ;
			// sqlDate=java.sql.Date.valueOf(utilDate.toString());
			utilDate = demandAdvice.getDanDueDate();
			// formatedDate=dateFormat.format(utilDate);Modified by pradeep for
			// newserver on 16.07.2012
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));Modified
			// by pradeep for newserver on 16.07.2012

			Log.log(Log.INFO, className, methodName, "Expiry Date : "
					+ utilDate);
			insertCGDANDetailsStmt.setDate(9,
					new java.sql.Date(utilDate.getTime()));

			Log.log(Log.INFO, className, methodName, "User Id : "
					+ demandAdvice.getUserId());
			insertCGDANDetailsStmt.setString(10, demandAdvice.getUserId());
			insertCGDANDetailsStmt.registerOutParameter(11, Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"Before Inserting CGDAN details");
			insertCGDANDetailsStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"After Inserting CGDAN details");

			updateStatus = insertCGDANDetailsStmt.getInt(1);

			String error = insertCGDANDetailsStmt.getString(11);

			Log.log(Log.DEBUG, className, methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, className, methodName, error);

				insertCGDANDetailsStmt.close();
				insertCGDANDetailsStmt = null;

				throw new DatabaseException(error);
			}

			insertCGDANDetailsStmt.close();
			insertCGDANDetailsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, className, methodName,
					"Exception raised when inserting CGDAN details, exception = "
							+ exception.getMessage());
			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */

		Log.log(Log.INFO, className, methodName, "Exited");

		return updateCGDANDetailsStatus;
	}

	/**
	 * This method returns all the service fee due grouped by the member
	 * followed by the borrower id
	 * 
	 * @return java.util.ArrayList
	 * @roseuid 399E8FD403B9
	 */
	public// method not used instead uses get approved applications
	ArrayList getServiceFeeMemberWise(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		String methodName = "getServiceFeeMemberWise";
		Connection connection = null;
		CallableStatement getServiceFeeMemberWiseStmt = null;
		ArrayList serviceFeeMemberWise = null;
		ResultSet resultSet = null;
		ServiceFee serviceFee = null;
		int status = 0;
		Log.log(Log.INFO, className, methodName, "Entering");

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			if (bankId == null) {
				getServiceFeeMemberWiseStmt = connection
						.prepareCall("{?=call packGetServiceFeeDetails.funcGetServiceFeeDetails(?,?)}");
			} else {

				Log.log(Log.INFO, className, methodName,
						"entering for packGetMemberServiceFeeDetails.funcGetMemberServiceFeeDetails");

				getServiceFeeMemberWiseStmt = connection
						.prepareCall("{?=call packGetMemberServiceFeeDetails.funcGetMemberServiceFeeDetails(?,?,?,?,?)}");
				getServiceFeeMemberWiseStmt.setString(4, bankId);
				getServiceFeeMemberWiseStmt.setString(5, zoneId);
				getServiceFeeMemberWiseStmt.setString(6, branchId);
			}

			getServiceFeeMemberWiseStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getServiceFeeMemberWiseStmt.registerOutParameter(2,
					Constants.CURSOR);

			getServiceFeeMemberWiseStmt.registerOutParameter(3, Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"Before executing Stored Procedure");
			getServiceFeeMemberWiseStmt.execute();
			Log.log(Log.INFO, className, methodName,
					"After executing Stored Procedure");
			resultSet = (ResultSet) getServiceFeeMemberWiseStmt.getObject(2);

			Log.log(Log.INFO, className, methodName,
					"ResultSet = " + resultSet.toString());

			status = getServiceFeeMemberWiseStmt.getInt(1);
			Log.log(Log.INFO, className, methodName, "Status = " + status);
			if (status != 0) {

				String error = getServiceFeeMemberWiseStmt.getString(3);

				getServiceFeeMemberWiseStmt.close();
				getServiceFeeMemberWiseStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			int recordCount = 0;

			Log.log(Log.INFO, className, methodName, "result Set Size :"
					+ resultSet.getFetchSize());
			while (resultSet.next()) {

				Log.log(Log.INFO, className, methodName, "entering result Set");
				if (recordCount == 0) {
					serviceFeeMemberWise = new ArrayList();
				}
				serviceFee = new ServiceFee();

				Log.log(Log.INFO, className, methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				serviceFee.setCgpan(resultSet.getString(1));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				serviceFee.setBankId(resultSet.getString(2));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				serviceFee.setZoneId(resultSet.getString(3));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				serviceFee.setBranchId(resultSet.getString(4));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Service Fee=" + resultSet.getDouble(5));
				serviceFee.setServiceAmount(resultSet.getDouble(5));

				/*
				 * java.sql.Date sqlDate = resultSet.getDate(6);
				 * Log.log(Log.INFO, className, methodName,
				 * "Inside While : Service Fee To Date="+sqlDate) ; if
				 * (sqlDate!=null) {
				 * serviceFee.setToDate(DateHelper.sqlToUtilDate
				 * (resultSet.getDate(6))); }
				 */

				Log.log(Log.INFO, className, methodName,
						"Inside While : Borrower Id=" + resultSet.getString(6));
				serviceFee.setBorrowerId(resultSet.getString(6));

				Log.log(Log.INFO,
						className,
						methodName,
						"Inside While : Service Fee Id="
								+ resultSet.getDouble(7));
				serviceFee.setServiceFeeId(resultSet.getDouble(7));

				serviceFeeMemberWise.add(serviceFee);
				++recordCount;
			}

			resultSet.close();
			getServiceFeeMemberWiseStmt.close();
			getServiceFeeMemberWiseStmt = null;

			// connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return serviceFeeMemberWise;

	}

	/**
	 * This method updates the SFDAN details to the database. The method takes
	 * the ServiceFee object and the SFDAN Id as parameters.
	 * 
	 * @param ServiceFee
	 * @param SFDAN
	 * @return boolean
	 * @roseuid 399E98B00177
	 */
	public// Params changed to DemandAdvice obj..*****method overloading must be
	// done
	boolean updateSFDANDetails(DemandAdvice demandAdvice) {
		Connection connection = null;
		CallableStatement updateSFDANDetailsStmt = null;

		int updateStatus = 0;
		boolean updateSFDANDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		connection = DBConnection.getConnection(false);

		try {

			updateSFDANDetailsStmt = connection
					.prepareCall("{call <<updateSFDANDetails>>(?,?,?)}");
			updateSFDANDetailsStmt.setString(1, demandAdvice.getDanNo());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getDanType());
			updateSFDANDetailsStmt.setString(3, demandAdvice.getCgpan());
			updateSFDANDetailsStmt.setString(4, demandAdvice.getBankId());
			updateSFDANDetailsStmt.setString(4, demandAdvice.getZoneId());
			updateSFDANDetailsStmt.setString(4, demandAdvice.getBranchId());
			updateSFDANDetailsStmt.setDouble(4, demandAdvice.getAmountRaised());
			updateSFDANDetailsStmt.setDouble(4, demandAdvice.getPenalty());
			// updateSFDANDetailsStmt.setDouble(4,demandAdvice.getCreatedBy()) ;

			utilDate = demandAdvice.getDanGeneratedDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanDueDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanExpiryDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			updateSFDANDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updateSFDANDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateSFDANDetailsStmt.getObject(6)
					.toString());

			if (updateStatus == 0) {
				updateSFDANDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateSFDANDetailsStatus = false;
			}

			connection.commit();
		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			// throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateSFDANDetailsStatus;

	}

	/**
	 * This method retrieves the pay in slip details for the given payment id
	 * and returns a PayInSlip object with pay in slip details.
	 * 
	 * @param PaymentId
	 * @return com.cgtsi.receiptspayments.PayInSlip
	 * @roseuid 399E9D8802EE
	 */
	public PaymentDetails getPayInSlipDetails(String paymentId)
			throws DatabaseException {
		PaymentDetails paymentDetails = new PaymentDetails();
		Connection connection = null;
		CallableStatement getPayInSlipDetailsStmt = null;
		// ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.INFO, "RpDAO", "getPayInSlipDetails", "Entered");

			String exception = "";

			getPayInSlipDetailsStmt = connection
					.prepareCall("{?=call funcGetPaymentDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			getPayInSlipDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getPayInSlipDetailsStmt.setString(2, paymentId);
			getPayInSlipDetailsStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(4,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(5,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(6,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt
					.registerOutParameter(7, java.sql.Types.DATE);
			getPayInSlipDetailsStmt.registerOutParameter(8,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(9,
					java.sql.Types.DOUBLE);
			getPayInSlipDetailsStmt.registerOutParameter(10,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(11,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(12,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.registerOutParameter(13,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(14,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.registerOutParameter(15,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(16,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(17,
					java.sql.Types.DATE);
			getPayInSlipDetailsStmt.registerOutParameter(18,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(19,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.execute();

			int errorCode = getPayInSlipDetailsStmt.getInt(1);
			String error = getPayInSlipDetailsStmt.getString(19);

			Log.log(Log.DEBUG, "RpDAO", "getPayInSlipDetails",
					"errorCode,error are " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails", error);

				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			paymentDetails.setPaymentId(getPayInSlipDetailsStmt.getString(3));
			paymentDetails.setModeOfDelivery(getPayInSlipDetailsStmt
					.getString(4));
			paymentDetails.setModeOfPayment(getPayInSlipDetailsStmt
					.getString(5));
			paymentDetails
					.setInstrumentNo(getPayInSlipDetailsStmt.getString(6));
			paymentDetails
					.setInstrumentDate(getPayInSlipDetailsStmt.getDate(7));
			paymentDetails.setInstrumentType(getPayInSlipDetailsStmt
					.getString(8));

			paymentDetails.setInstrumentAmount(getPayInSlipDetailsStmt
					.getDouble(9));
			paymentDetails.setPayableAt(getPayInSlipDetailsStmt.getString(11));
			paymentDetails
					.setDrawnAtBank(getPayInSlipDetailsStmt.getString(12));
			paymentDetails.setDrawnAtBranch(getPayInSlipDetailsStmt
					.getString(13));
			paymentDetails.setCollectingBank(getPayInSlipDetailsStmt
					.getString(14));
			paymentDetails.setCollectingBankBranch(getPayInSlipDetailsStmt
					.getString(15));
			paymentDetails.setCgtsiAccNumber(getPayInSlipDetailsStmt
					.getString(16));
			paymentDetails.setPaymentDate(getPayInSlipDetailsStmt.getDate(17));
			paymentDetails.setCgtsiAccNumber(getPayInSlipDetailsStmt
					.getString(18));
			getPayInSlipDetailsStmt.close();
			getPayInSlipDetailsStmt = null;
			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Payment details.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getPayInSlipDetails", "Exited");

		return paymentDetails;
	}

	/**
	 * 
	 * @param rpActionForm
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void afterModifyAllocatePaymentDetail(RPActionForm rpActionForm,
			String userId) throws DatabaseException {
		String methodName = "afterModifyAllocatePaymentDetail";

		Log.log(Log.INFO, "RpDAO", methodName, "Entered");

		boolean newConn = false;
		Connection connection = DBConnection.getConnection(false);
		java.sql.Date instrumentDt = null;
		java.util.Date instrumentDate = null;

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newConn = true;
		}
		try {
			CallableStatement callable = null;
			int errorCode;
			String error;

			callable = connection
					.prepareCall("{?=call Funcinsertpaydetlog (?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.setString(2, rpActionForm.getPaymentId());
			String instrumentNo = rpActionForm.getInstrumentNo(); // OLD NUMBER
			if (instrumentNo == null || instrumentNo.equals("")) {
				callable.setNull(3, Types.VARCHAR);
			} else {
				callable.setString(3, instrumentNo);
			}

			instrumentDate = rpActionForm.getInstrumentDate(); // OLD DATE

			// callable.setDate(4,instrumentDt);

			if (instrumentDate == null) {
				callable.setNull(4, Types.DATE);
			} else {
				instrumentDt = new java.sql.Date(instrumentDate.getTime());
				callable.setDate(4, instrumentDt);
			}

			callable.setString(5, rpActionForm.getPayableAt());

			String newInstrumentNo = rpActionForm.getNewInstrumentNo();
			if (newInstrumentNo == null || newInstrumentNo.equals("")) {
				callable.setNull(6, Types.VARCHAR);
			} else {
				callable.setString(6, newInstrumentNo);
			}
			java.util.Date newInstrumentDt = rpActionForm.getNewInstrumentDt();

			if (newInstrumentDt == null) {
				callable.setNull(7, Types.DATE);
			} else {
				callable.setDate(7,
						new java.sql.Date(newInstrumentDt.getTime()));
			}

			callable.setString(8, userId);

			callable.registerOutParameter(9, Types.VARCHAR);

			callable.execute();

			errorCode = callable.getInt(1);

			error = callable.getString(9);

			Log.log(Log.DEBUG, "RpDAO", methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", methodName, error);

				callable.close();
				callable = null;
				connection.rollback();

				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
			connection.commit();

		} catch (SQLException e) {
			Log.log(Log.ERROR, "RpDAO", methodName, e.getMessage());

			Log.logException(e);

			if (newConn) {
				try {
					connection.rollback();
				} catch (SQLException ignore) {
				}
			}

			throw new DatabaseException(
					"Unable to update Allocated Payment Id Details.");

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO, "RpDAO", methodName, "Exited");
	}

	/* Added by shyam */

	/**
	 * This method updates the payment instrument details along with the
	 * generated payment id.
	 * 
	 * @param Instrument
	 * @return boolean
	 * @roseuid 399EABCF0109
	 */
	public String appropriateallocateDAN(PaymentDetails paymentDetails,
			ArrayList danSummaries, Map allocationFlags, Map cgpans,
			Map danCgpanDetails, User user) throws DatabaseException,
			MissingCGPANsException {
		String methodName = "appropriateallocateDAN";
		// System.out.println(methodName);
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		// System.out.println("Realisationdt1:"+paymentDetails.getRealisationDate());
		// System.out.println("ReceivedAmount1:"+paymentDetails.getReceivedAmount());
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			// System.out.println("payment details  " +
			// paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertSFInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// System.out.println("payment details inserted " + paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			Set cgpansSet = cgpans.keySet();

			Iterator cgpansIterator = cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);

				String danId = danSummary.getDanId();

				Log.log(Log.DEBUG, className, methodName, "danId " + danId);
				String shiftDanId = danId.replace('.', '_');

				if (allocationFlags.containsKey(shiftDanId)) {
					Log.log(Log.DEBUG, className, methodName,
							"dan is allocated");

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(shiftDanId);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor
								.displaySFCGPANs(danId);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());
						// System.out.println("test1:"+paymentDetails.getRealisationDate());
						// System.out.println("ReceivedAmount:"+paymentDetails.getReceivedAmount());
						// System.out.println("Realisationdt1:"+paymentDetails.getRealisationDate());
						insertAllocationDetails1(danId, paymentDetails,
								allocationDetail, user, paymentId, connection);
					}
				} else {
					Log.log(Log.DEBUG, className, methodName,
							"CGPAN wise allocation ");
					// System.out.println("CGPAN wise allocation ");

					while (cgpansIterator.hasNext()) {
						String key = (String) cgpansIterator.next();

						Log.log(Log.DEBUG, className, methodName, "key " + key);
						// System.out.println( "key "+key);

						if (key.startsWith(shiftDanId)) {
							Log.log(Log.DEBUG, className, methodName,
									"Allocated");
							// System.out.println("Allocated");
							ArrayList panDetails = (ArrayList) danCgpanDetails
									.get(danId);
							// String
							// cgpanPart=key.substring(key.indexOf("-")+1,key.length());

							// Log.log(Log.DEBUG, className, methodName,
							// "cgpanPart "+cgpanPart);
							if (panDetails != null) {
								for (int j = 0; j < panDetails.size(); j++) {
									AllocationDetail allocationDetail = (AllocationDetail) panDetails
											.get(j);

									Log.log(Log.DEBUG,
											className,
											methodName,
											"allocation cgpan "
													+ allocationDetail
															.getCgpan());

									// if(cgpanPart.equals(allocationDetail.getCgpan()))
									// {
									Log.log(Log.DEBUG,
											className,
											methodName,
											"CGPAN : "
													+ allocationDetail
															.getCgpan());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Name Of Unit  "
													+ allocationDetail
															.getNameOfUnit());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Facility Covered  "
													+ allocationDetail
															.getFacilityCovered());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Amount Due "
													+ allocationDetail
															.getAmountDue());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Penalty  "
													+ allocationDetail
															.getPenalty());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"First Disbursement Date "
													+ allocationDetail
															.getFirstDisbursementDate());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Allocated Flag  "
													+ allocationDetail
															.getAllocatedFlag());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Not allocated Reason "
													+ allocationDetail
															.getNotAllocatedReason());
									// System.out.println("test2:"+paymentDetails.getRealisationDate());
									// System.out.println("ReceivedAmount:"+paymentDetails.getReceivedAmount());
									// System.out.println("Realisationdt1:"+paymentDetails.getRealisationDate());
									insertAllocationDetails1(danId,
											paymentDetails, allocationDetail,
											user, paymentId, connection);
									isAllocated = true;
									// }
								}
							}
						}
					}
					cgpansIterator = cgpansSet.iterator();
				}

			}
			Log.log(Log.INFO, "RPDAO", methodName,
					"allocation details inserted");
			connection.commit();
			// System.out.println("commited");
			Log.log(Log.INFO, "RPDAO", methodName, " commited");
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return paymentId;
	}

	/* --------------- */
	/* ADDED BY SUKUMAR@PATH FOR ALLOCATING CURRENT YEAR ASF DANS */

	/**
	 * 
	 * @param paymentDetails
	 * @param danSummaries
	 * @param allocationFlags
	 * @param cgpans
	 * @param danCgpanDetails
	 * @param user
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 * @throws com.cgtsi.receiptspayments.MissingCGPANsException
	 */
	public String allocateASFDAN(PaymentDetails paymentDetails,
			ArrayList danSummaries, Map allocationFlags, Map cgpans,
			Map danCgpanDetails, User user) throws DatabaseException,
			MissingCGPANsException {
		String methodName = "allocateASFDAN";
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			Set cgpansSet = cgpans.keySet();

			Iterator cgpansIterator = cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);

				String danId = danSummary.getDanId();

				Log.log(Log.DEBUG, className, methodName, "danId " + danId);
				String shiftDanId = danId.replace('.', '_');

				if (allocationFlags.containsKey(shiftDanId)) {
					Log.log(Log.DEBUG, className, methodName,
							"dan is allocated");

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(shiftDanId);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor.displayCGPANs(danId);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());

						insertAllocationDetails(danId, allocationDetail, user,
								paymentId, connection);
					}
				} else {
					Log.log(Log.DEBUG, className, methodName,
							"CGPAN wise allocation ");

					while (cgpansIterator.hasNext()) {
						String key = (String) cgpansIterator.next();

						Log.log(Log.DEBUG, className, methodName, "key " + key);

						if (key.startsWith(shiftDanId)) {
							Log.log(Log.DEBUG, className, methodName,
									"Allocated");

							ArrayList panDetails = (ArrayList) danCgpanDetails
									.get(danId);
							// String
							// cgpanPart=key.substring(key.indexOf("-")+1,key.length());

							// Log.log(Log.DEBUG, className, methodName,
							// "cgpanPart "+cgpanPart);
							if (panDetails != null) {
								for (int j = 0; j < panDetails.size(); j++) {
									AllocationDetail allocationDetail = (AllocationDetail) panDetails
											.get(j);

									Log.log(Log.DEBUG,
											className,
											methodName,
											"allocation cgpan "
													+ allocationDetail
															.getCgpan());

									// if(cgpanPart.equals(allocationDetail.getCgpan()))
									// {
									Log.log(Log.DEBUG,
											className,
											methodName,
											"CGPAN : "
													+ allocationDetail
															.getCgpan());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Name Of Unit  "
													+ allocationDetail
															.getNameOfUnit());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Facility Covered  "
													+ allocationDetail
															.getFacilityCovered());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Amount Due "
													+ allocationDetail
															.getAmountDue());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Penalty  "
													+ allocationDetail
															.getPenalty());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"First Disbursement Date "
													+ allocationDetail
															.getFirstDisbursementDate());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Allocated Flag  "
													+ allocationDetail
															.getAllocatedFlag());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Not allocated Reason "
													+ allocationDetail
															.getNotAllocatedReason());

									insertAllocationDetails(danId,
											allocationDetail, user, paymentId,
											connection);
									isAllocated = true;
									// }
								}
							}
						}
					}
					cgpansIterator = cgpansSet.iterator();
				}

			}
			Log.log(Log.INFO, "RPDAO", methodName,
					"allocation details inserted");
			connection.commit();
			Log.log(Log.INFO, "RPDAO", methodName, " commited");
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	/* ----------------------------------------------------------- */
	/* added by sukumar@path for GF Allocation */

	/**
	 * 
	 * @param paymentDetails
	 * @param danSummaries
	 * @param allocationFlags
	 * @param cgpans
	 * @param danCgpanDetails
	 * @param user
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 * @throws com.cgtsi.receiptspayments.MissingCGPANsException
	 */
	public String allocateCGDAN(PaymentDetails paymentDetails,
			Map appropriatedFlags, Map cgpans, Map danCgpanDetails, User user)
			throws DatabaseException, MissingCGPANsException {
		String methodName = "allocateCGDAN";
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			// Set cgpansSet=cgpans.keySet();

			// Iterator cgpansIterator=cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			// kot

			StringTokenizer tokenizer = null;
			// Map appropriatedFlags=actionForm.getAppropriatedFlags();
			Set appropriatedCasesSet = appropriatedFlags.keySet();
			Iterator appropriatedCasesIterator = appropriatedCasesSet
					.iterator();
			String token = null;
			String token1 = null;
			int total = 0;

			int total2 = 0;
			// boolean appropriatedFlag = false;
			while (appropriatedCasesIterator.hasNext()) {
				String key = (String) appropriatedCasesIterator.next();

				// String d= key1.;

				tokenizer = new StringTokenizer(key, "#");

				while (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();

					token1 = tokenizer.nextToken();

					String danNoKey = token.replace('_', '.');

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(danNoKey);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor
								.displayCGPANs(danNoKey);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());

						insertAllocationDetails(danNoKey, allocationDetail,
								user, paymentId, connection);

					}

					// kot end

				}

			}

			connection.commit();

		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	public String allocateCGDANold(PaymentDetails paymentDetails,
			ArrayList danSummaries, Map allocationFlags, Map cgpans,
			Map danCgpanDetails, User user) throws DatabaseException,
			MissingCGPANsException {
		String methodName = "allocateCGDAN";
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			Set cgpansSet = cgpans.keySet();

			Iterator cgpansIterator = cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);

				String danId = danSummary.getDanId();

				Log.log(Log.DEBUG, className, methodName, "danId " + danId);
				String shiftDanId = danId.replace('.', '_');

				if (allocationFlags.containsKey(shiftDanId)) {
					Log.log(Log.DEBUG, className, methodName,
							"dan is allocated");

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(shiftDanId);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor.displayCGPANs(danId);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());

						insertAllocationDetails(danId, allocationDetail, user,
								paymentId, connection);
					}
				} else {
					Log.log(Log.DEBUG, className, methodName,
							"CGPAN wise allocation ");

					while (cgpansIterator.hasNext()) {
						String key = (String) cgpansIterator.next();

						Log.log(Log.DEBUG, className, methodName, "key " + key);

						if (key.startsWith(shiftDanId)) {
							Log.log(Log.DEBUG, className, methodName,
									"Allocated");

							ArrayList panDetails = (ArrayList) danCgpanDetails
									.get(danId);
							// String
							// cgpanPart=key.substring(key.indexOf("-")+1,key.length());

							// Log.log(Log.DEBUG, className, methodName,
							// "cgpanPart "+cgpanPart);
							if (panDetails != null) {
								for (int j = 0; j < panDetails.size(); j++) {
									AllocationDetail allocationDetail = (AllocationDetail) panDetails
											.get(j);

									Log.log(Log.DEBUG,
											className,
											methodName,
											"allocation cgpan "
													+ allocationDetail
															.getCgpan());

									// if(cgpanPart.equals(allocationDetail.getCgpan()))
									// {
									Log.log(Log.DEBUG,
											className,
											methodName,
											"CGPAN : "
													+ allocationDetail
															.getCgpan());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Name Of Unit  "
													+ allocationDetail
															.getNameOfUnit());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Facility Covered  "
													+ allocationDetail
															.getFacilityCovered());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Amount Due "
													+ allocationDetail
															.getAmountDue());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Penalty  "
													+ allocationDetail
															.getPenalty());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"First Disbursement Date "
													+ allocationDetail
															.getFirstDisbursementDate());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Allocated Flag  "
													+ allocationDetail
															.getAllocatedFlag());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Not allocated Reason "
													+ allocationDetail
															.getNotAllocatedReason());

									insertAllocationDetails(danId,
											allocationDetail, user, paymentId,
											connection);
									isAllocated = true;
									// }
								}
							}
						}
					}
					cgpansIterator = cgpansSet.iterator();
				}

			}
			Log.log(Log.INFO, "RPDAO", methodName,
					"allocation details inserted");
			connection.commit();
			Log.log(Log.INFO, "RPDAO", methodName, " commited");
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	/**
	 * 
	 * @param paymentDetails
	 * @param danSummaries
	 * @param allocationFlags
	 * @param cgpans
	 * @param danCgpanDetails
	 * @param user
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 * @throws com.cgtsi.receiptspayments.MissingCGPANsException
	 */
	public String allocateNEFTCGDAN(PaymentDetails paymentDetails,
			ArrayList danSummaries, Map allocationFlags, Map cgpans,
			Map danCgpanDetails, User user) throws DatabaseException,
			MissingCGPANsException {
		String methodName = "allocateCGDAN";
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertNEFTInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			Set cgpansSet = cgpans.keySet();

			Iterator cgpansIterator = cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);

				String danId = danSummary.getDanId();

				Log.log(Log.DEBUG, className, methodName, "danId " + danId);
				String shiftDanId = danId.replace('.', '_');

				if (allocationFlags.containsKey(shiftDanId)) {
					Log.log(Log.DEBUG, className, methodName,
							"dan is allocated");

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(shiftDanId);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor.displayCGPANs(danId);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());

						insertAllocationDetails(danId, allocationDetail, user,
								paymentId, connection);
					}
				} else {
					Log.log(Log.DEBUG, className, methodName,
							"CGPAN wise allocation ");

					while (cgpansIterator.hasNext()) {
						String key = (String) cgpansIterator.next();

						Log.log(Log.DEBUG, className, methodName, "key " + key);

						if (key.startsWith(shiftDanId)) {
							Log.log(Log.DEBUG, className, methodName,
									"Allocated");

							ArrayList panDetails = (ArrayList) danCgpanDetails
									.get(danId);
							// String
							// cgpanPart=key.substring(key.indexOf("-")+1,key.length());

							// Log.log(Log.DEBUG, className, methodName,
							// "cgpanPart "+cgpanPart);
							if (panDetails != null) {
								for (int j = 0; j < panDetails.size(); j++) {
									AllocationDetail allocationDetail = (AllocationDetail) panDetails
											.get(j);

									Log.log(Log.DEBUG,
											className,
											methodName,
											"allocation cgpan "
													+ allocationDetail
															.getCgpan());

									// if(cgpanPart.equals(allocationDetail.getCgpan()))
									// {
									Log.log(Log.DEBUG,
											className,
											methodName,
											"CGPAN : "
													+ allocationDetail
															.getCgpan());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Name Of Unit  "
													+ allocationDetail
															.getNameOfUnit());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Facility Covered  "
													+ allocationDetail
															.getFacilityCovered());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Amount Due "
													+ allocationDetail
															.getAmountDue());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Penalty  "
													+ allocationDetail
															.getPenalty());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"First Disbursement Date "
													+ allocationDetail
															.getFirstDisbursementDate());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Allocated Flag  "
													+ allocationDetail
															.getAllocatedFlag());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Not allocated Reason "
													+ allocationDetail
															.getNotAllocatedReason());

									insertAllocationDetails(danId,
											allocationDetail, user, paymentId,
											connection);
									isAllocated = true;
									// }
								}
							}
						}
					}
					cgpansIterator = cgpansSet.iterator();
				}

			}
			Log.log(Log.INFO, "RPDAO", methodName,
					"allocation details inserted");
			connection.commit();
			Log.log(Log.INFO, "RPDAO", methodName, " commited");
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	/* --------------------------------------- */

	/**
	 * This method updates the payment instrument details along with the
	 * generated payment id.
	 * 
	 * @param Instrument
	 * @return boolean
	 * @roseuid 399EABCF0109
	 */
	public String allocateDAN(PaymentDetails paymentDetails,
			ArrayList danSummaries, Map allocationFlags, Map cgpans,
			Map danCgpanDetails, User user) throws DatabaseException,
			MissingCGPANsException {
		String methodName = "allocateDAN";
		Log.log(Log.INFO, "RPDAO", methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		RpProcessor rpProcessor = new RpProcessor();
		String paymentId = "";
		try {
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getModeOfPayment());
			Log.log(Log.INFO, "RPDAO", methodName, "payment details  "
					+ paymentDetails.getInstrumentAmount());
			paymentId = insertInstrumentDetails(paymentDetails, connection);
			Log.log(Log.INFO, "RPDAO", methodName, "payment details inserted "
					+ paymentId);
			// Map allocationFlags=actionForm.getAllocatedFlags();

			// Set allocationSet=allocationFlags.keySet();

			// Iterator allocationIterator=allocationSet.iterator();

			// ArrayList danSummaries=actionForm.getDanSummaries();

			// Map cgpans=actionForm.getCgpans();

			Set cgpansSet = cgpans.keySet();

			Iterator cgpansIterator = cgpansSet.iterator();

			// Map danCgpanDetails=actionForm.getDanPanDetails();

			boolean isAllocated = false;

			for (int i = 0; i < danSummaries.size(); i++) {
				DANSummary danSummary = (DANSummary) danSummaries.get(i);

				String danId = danSummary.getDanId();

				Log.log(Log.DEBUG, className, methodName, "danId " + danId);
				String shiftDanId = danId.replace('.', '_');

				if (allocationFlags.containsKey(shiftDanId)) {
					Log.log(Log.DEBUG, className, methodName,
							"dan is allocated");

					ArrayList panDetails = (ArrayList) danCgpanDetails
							.get(shiftDanId);

					if (panDetails == null) {
						Log.log(Log.DEBUG, className, methodName,
								"CGPAN details are not available. get them.");
						ArrayList totalList = rpProcessor.displayCGPANs(danId);
						panDetails = (ArrayList) totalList.get(0);
					}
					if (panDetails.size() == 0) {
						throw new DatabaseException("Allocation already done.");
					}
					for (int j = 0; j < panDetails.size(); j++) {
						AllocationDetail allocationDetail = (AllocationDetail) panDetails
								.get(j);

						allocationDetail.setAllocatedFlag(Constants.YES);

						Log.log(Log.DEBUG, className, methodName, "CGPAN : "
								+ allocationDetail.getCgpan());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Name Of Unit  "
										+ allocationDetail.getNameOfUnit());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Facility Covered  "
										+ allocationDetail.getFacilityCovered());

						Log.log(Log.DEBUG, className, methodName, "Amount Due "
								+ allocationDetail.getAmountDue());

						Log.log(Log.DEBUG, className, methodName, "Penalty  "
								+ allocationDetail.getPenalty());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"First Disbursement Date  "
										+ allocationDetail
												.getFirstDisbursementDate());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Allocated Flag  "
										+ allocationDetail.getAllocatedFlag());

						Log.log(Log.DEBUG,
								className,
								methodName,
								"Not allocated Reason  "
										+ allocationDetail
												.getNotAllocatedReason());

						insertAllocationDetails(danId, allocationDetail, user,
								paymentId, connection);
					}
				} else {
					Log.log(Log.DEBUG, className, methodName,
							"CGPAN wise allocation ");

					while (cgpansIterator.hasNext()) {
						String key = (String) cgpansIterator.next();

						Log.log(Log.DEBUG, className, methodName, "key " + key);

						if (key.startsWith(shiftDanId)) {
							Log.log(Log.DEBUG, className, methodName,
									"Allocated");

							ArrayList panDetails = (ArrayList) danCgpanDetails
									.get(danId);
							// String
							// cgpanPart=key.substring(key.indexOf("-")+1,key.length());

							// Log.log(Log.DEBUG, className, methodName,
							// "cgpanPart "+cgpanPart);
							if (panDetails != null) {
								for (int j = 0; j < panDetails.size(); j++) {
									AllocationDetail allocationDetail = (AllocationDetail) panDetails
											.get(j);

									Log.log(Log.DEBUG,
											className,
											methodName,
											"allocation cgpan "
													+ allocationDetail
															.getCgpan());

									// if(cgpanPart.equals(allocationDetail.getCgpan()))
									// {
									Log.log(Log.DEBUG,
											className,
											methodName,
											"CGPAN : "
													+ allocationDetail
															.getCgpan());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Name Of Unit  "
													+ allocationDetail
															.getNameOfUnit());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Facility Covered  "
													+ allocationDetail
															.getFacilityCovered());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Amount Due "
													+ allocationDetail
															.getAmountDue());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Penalty  "
													+ allocationDetail
															.getPenalty());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"First Disbursement Date "
													+ allocationDetail
															.getFirstDisbursementDate());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Allocated Flag  "
													+ allocationDetail
															.getAllocatedFlag());

									Log.log(Log.DEBUG,
											className,
											methodName,
											"Not allocated Reason "
													+ allocationDetail
															.getNotAllocatedReason());

									insertAllocationDetails(danId,
											allocationDetail, user, paymentId,
											connection);
									isAllocated = true;
									// }
								}
							}
						}
					}
					cgpansIterator = cgpansSet.iterator();
				}

			}
			Log.log(Log.INFO, "RPDAO", methodName,
					"allocation details inserted");
			connection.commit();
			Log.log(Log.INFO, "RPDAO", methodName, " commited");
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlExp) {
				Log.log(Log.INFO, "RPDAO", methodName, "error rolling back");
				throw new DatabaseException(sqlExp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlExp) {
			Log.log(Log.INFO, "RPDAO", methodName, "error while commit");
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	/* Added by shyam */

	/**
	 * This method updates the payment instrument details along with the
	 * generated payment id.
	 * 
	 * @param Instrument
	 * @return boolean
	 * @roseuid 399EABCF0109
	 */
	public String insertSFInstrumentDetails(PaymentDetails instrument,
			Connection connection) throws DatabaseException {
		String methodName = "insertSFInstrumentDetails";
		Log.log(Log.INFO, "RPDAO", methodName, "entered");
		// Connection connection = null ;
		CallableStatement updateInstrumentDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		int updateStatus = 0;
		boolean updateInstrumentDetailsStatus = false;
		String paymentId = "";
		String formatedDate = "";
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		boolean newConn = false;

		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			updateInstrumentDetailsStmt = connection
					.prepareCall("{?=call funcInsertSFPaymentDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			/* ---------------------- */

			/* ----------------------- */
			updateInstrumentDetailsStmt.registerOutParameter(1, Types.INTEGER);
			updateInstrumentDetailsStmt.registerOutParameter(2, Types.VARCHAR);
			updateInstrumentDetailsStmt.setString(3,
					instrument.getModeOfPayment());
			// System.out.println(instrument.getModeOfPayment());
			updateInstrumentDetailsStmt.setString(4,
					instrument.getCollectingBank());
			// System.out.println(instrument.getCollectingBank());
			updateInstrumentDetailsStmt.setString(5,
					instrument.getCollectingBankBranch());
			// System.out.println( instrument.getCollectingBankBranch());
			// updateInstrumentDetailsStmt.setString(6,instrument.getPaymentDate().toString());
			// updateInstrumentDetailsStmt.setDate(6,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(dateFormat.format(instrument.getPaymentDate()))))
			// ;
			updateInstrumentDetailsStmt.setDate(6, new java.sql.Date(instrument
					.getPaymentDate().getTime()));
			// updateInstrumentDetailsStmt.setDate(6,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate((instrument.getPaymentDate().toString()))))
			// ;//modified by pradeep for new server on 16.07.2012
			// System.out.println(java.sql.Date.valueOf(DateHelper.stringToSQLdate((instrument.getPaymentDate().toString()))));
			updateInstrumentDetailsStmt.setString(7,
					instrument.getInstrumentNo());
			// System.out.println(instrument.getInstrumentNo());
			// System.out.println("instrument.getPaymentDate().toString():"+instrument.getPaymentDate().toString());
			// updateInstrumentDetailsStmt.setDate(8,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(dateFormat.format(instrument.getInstrumentDate()))))
			// ;modified by pradeep for new server on 16.07.2012
			updateInstrumentDetailsStmt.setDate(8, new java.sql.Date(instrument
					.getInstrumentDate().getTime()));
			// updateInstrumentDetailsStmt.setString(8,instrument.getInstrumentDate().toString());
			// System.out.println("instrument.getInstrumentDate().toString()"+instrument.getInstrumentDate().toString());
			// System.out.println(java.sql.Date.valueOf(DateHelper.stringToSQLdate((instrument.getInstrumentDate().toString()))));
			// updateInstrumentDetailsStmt.setString(9,
			// instrument.getModeOfDelivery()) ;
			// System.out.println(instrument.getModeOfDelivery());
			updateInstrumentDetailsStmt.setDouble(9,
					instrument.getInstrumentAmount());
			// System.out.println(instrument.getInstrumentAmount());
			updateInstrumentDetailsStmt.setString(10,
					instrument.getDrawnAtBank());
			// System.out.println(instrument.getDrawnAtBank());
			updateInstrumentDetailsStmt.setString(11,
					instrument.getDrawnAtBranch());
			// System.out.println(instrument.getDrawnAtBranch());
			updateInstrumentDetailsStmt
					.setString(12, instrument.getPayableAt());
			// System.out.println(instrument.getPayableAt());
			// updateInstrumentDetailsStmt.setString(14,
			// instrument.getCgtsiAccNumber()) ;
			// System.out.println(instrument.getCgtsiAccNumber());
			// updateInstrumentDetailsStmt.setDate(13,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate((instrument.getRealisationDate().toString()))))
			// ;
			// updateInstrumentDetailsStmt.setDate(13,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(dateFormat.format(instrument.getRealisationDate()))));
			updateInstrumentDetailsStmt.setDate(13, new java.sql.Date(
					instrument.getRealisationDate().getTime()));
			// updateInstrumentDetailsStmt.setString(13,instrument.getRealisationDate().toString());
			// System.out.println("instrument.getRealisationDate().toString()"+instrument.getRealisationDate().toString());
			// System.out.println(java.sql.Date.valueOf(DateHelper.stringToSQLdate((instrument.getRealisationDate().toString()))));
			updateInstrumentDetailsStmt.setDouble(14,
					instrument.getReceivedAmount());
			// System.out.println(instrument.getReceivedAmount());
			updateInstrumentDetailsStmt.registerOutParameter(15, Types.VARCHAR);
			updateInstrumentDetailsStmt.executeQuery();
			updateStatus = updateInstrumentDetailsStmt.getInt(1);
			// System.out.println("updateStatus:"+updateStatus);
			Log.log(Log.INFO, "RPDAO", methodName, "status " + updateStatus);

			if (updateStatus == 0) {
				paymentId = updateInstrumentDetailsStmt.getString(2);
				// System.out.println("paymentId:"+paymentId);
				Log.log(Log.INFO, "RPDAO", methodName, "id " + paymentId);
			} else if (updateStatus == 1) {
				Log.log(Log.INFO, "RPDAO", methodName, "status here");
				updateInstrumentDetailsStatus = false;
				String err = updateInstrumentDetailsStmt.getString(15);
				Log.log(Log.INFO, "RPDAO", methodName, "status err " + err);
				// System.out.println("err:"+err);
				updateInstrumentDetailsStmt.close();
				updateInstrumentDetailsStmt = null;

				throw new DatabaseException(err);
			}
			updateInstrumentDetailsStmt.close();
			updateInstrumentDetailsStmt = null;
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		return paymentId;
	}

	/* ------------- */

	/**
	 * This method updates the payment instrument details along with the
	 * generated payment id.
	 * 
	 * @param Instrument
	 * @return boolean
	 * @roseuid 399EABCF0109
	 * @param instrument
	 * @param connection
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String insertInstrumentDetails(PaymentDetails instrument,
			Connection connection) throws DatabaseException {
		String methodName = "insertInstrumentDetails";
		Log.log(Log.INFO, "RPDAO", methodName, "entered");
		// Connection connection = null ;
		CallableStatement updateInstrumentDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		int updateStatus = 0;
		boolean updateInstrumentDetailsStatus = false;
		String paymentId = "";
		String formatedDate = "";
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		boolean newConn = false;
		String drawnBank;

		String drawnBranch;

		java.sql.Date payDate;

		java.sql.Date instrDate;
		String instrmentNo;

		try {

			System.out.println("test1");

			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}

			if (instrument.getDrawnAtBank() != null
					&& instrument.getDrawnAtBank() != "") {
				drawnBank = instrument.getDrawnAtBank();

			} else {

				drawnBank = "";
			}
			if (instrument.getDrawnAtBranch() != null
					&& instrument.getDrawnAtBank() != "") {
				drawnBranch = instrument.getDrawnAtBranch();

			} else {

				drawnBranch = "";
			}
			if (instrument.getInstrumentNo() != null
					&& instrument.getInstrumentNo() != "") {
				instrmentNo = instrument.getInstrumentNo();

			} else {

				instrmentNo = "";
			}
			if (instrument.getPaymentDate() != null) {
				payDate = new java.sql.Date(instrument.getPaymentDate()
						.getTime());

			} else {

				payDate = null;
			}

			if (instrument.getInstrumentDate() != null) {
				instrDate = new java.sql.Date(instrument.getInstrumentDate()
						.getTime());

			} else {

				instrDate = null;
			}
			System.out.println("test3");
			updateInstrumentDetailsStmt = connection
					.prepareCall("{?=call funcInsertPaymentDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			updateInstrumentDetailsStmt.registerOutParameter(1, Types.INTEGER);
			updateInstrumentDetailsStmt.registerOutParameter(2, Types.VARCHAR);
			updateInstrumentDetailsStmt.setString(3,
					instrument.getModeOfPayment());
			updateInstrumentDetailsStmt.setString(4,
					instrument.getCollectingBank());
			updateInstrumentDetailsStmt.setString(5,
					instrument.getCollectingBankBranch());
			// updateInstrumentDetailsStmt.setDate(6,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (instrument.getPaymentDate().toString())))) ;
			updateInstrumentDetailsStmt.setDate(6, payDate);
			updateInstrumentDetailsStmt.setString(7, instrmentNo);
			// updateInstrumentDetailsStmt.setDate(8,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (instrument.getInstrumentDate().toString())))) ;
			updateInstrumentDetailsStmt.setDate(8, instrDate);
			updateInstrumentDetailsStmt.setString(9,
					instrument.getModeOfDelivery());
			updateInstrumentDetailsStmt.setDouble(10,
					instrument.getInstrumentAmount());
			updateInstrumentDetailsStmt.setString(11, drawnBank);
			updateInstrumentDetailsStmt.setString(12, drawnBranch);
			updateInstrumentDetailsStmt
					.setString(13, instrument.getPayableAt());
			updateInstrumentDetailsStmt.setString(14,
					instrument.getCgtsiAccNumber());
			updateInstrumentDetailsStmt.setString(15,
					instrument.getAllocationType1());
			updateInstrumentDetailsStmt.registerOutParameter(16, Types.VARCHAR);
			updateInstrumentDetailsStmt.executeQuery();
			updateStatus = updateInstrumentDetailsStmt.getInt(1);

			// System.out.println("test5");

			Log.log(Log.INFO, "RPDAO", methodName, "status " + updateStatus);

			if (updateStatus == 0) {
				paymentId = updateInstrumentDetailsStmt.getString(2);
				Log.log(Log.INFO, "RPDAO", methodName, "id " + paymentId);
			} else if (updateStatus == 1) {
				Log.log(Log.INFO, "RPDAO", methodName, "status here");
				updateInstrumentDetailsStatus = false;
				String err = updateInstrumentDetailsStmt.getString(15);
				Log.log(Log.INFO, "RPDAO", methodName, "status err " + err);

				updateInstrumentDetailsStmt.close();
				updateInstrumentDetailsStmt = null;

				throw new DatabaseException(err);
			}
			updateInstrumentDetailsStmt.close();
			updateInstrumentDetailsStmt = null;
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		return paymentId;
	}

	/**
	 * 
	 * @param instrument
	 * @param connection
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String insertNEFTInstrumentDetails(PaymentDetails instrument,
			Connection connection) throws DatabaseException {
		String methodName = "insertNEFTInstrumentDetails";
		Log.log(Log.INFO, "RPDAO", methodName, "entered");
		// Connection connection = null ;
		CallableStatement updateInstrumentDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		int updateStatus = 0;
		boolean updateInstrumentDetailsStatus = false;
		String paymentId = "";
		String formatedDate = "";
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		boolean newConn = false;

		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			updateInstrumentDetailsStmt = connection
					.prepareCall("{?=call funcInsertPaymentDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			updateInstrumentDetailsStmt.registerOutParameter(1, Types.INTEGER);
			updateInstrumentDetailsStmt.registerOutParameter(2, Types.VARCHAR);
			updateInstrumentDetailsStmt.setString(3,
					instrument.getModeOfPayment());
			updateInstrumentDetailsStmt.setString(4,
					instrument.getCollectingBank());
			updateInstrumentDetailsStmt.setString(5,
					instrument.getCollectingBankBranch());
			// updateInstrumentDetailsStmt.setDate(6,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (instrument.getPaymentDate().toString())))) ;
			updateInstrumentDetailsStmt.setDate(6, new java.sql.Date(instrument
					.getPaymentDate().getTime()));
			updateInstrumentDetailsStmt.setString(7,
					instrument.getInstrumentNo());
			if (!((instrument.getInstrumentDate() == null) || (instrument
					.getInstrumentDate().equals("")))) {
				// updateInstrumentDetailsStmt.setDate(8,
				// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
				// (instrument.getInstrumentDate().toString())))) ;
				updateInstrumentDetailsStmt.setDate(8, new java.sql.Date(
						instrument.getInstrumentDate().getTime()));
			} else {
				// updateInstrumentDetailsStmt.setDate(8,
				// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
				// (instrument.getPaymentDate().toString())))) ;
				updateInstrumentDetailsStmt.setDate(8, new java.sql.Date(
						instrument.getPaymentDate().getTime()));

				// updateInstrumentDetailsStmt.setNull(8,Types.DATE);
			}
			updateInstrumentDetailsStmt.setString(9,
					instrument.getModeOfDelivery());
			updateInstrumentDetailsStmt.setDouble(10,
					instrument.getInstrumentAmount());
			updateInstrumentDetailsStmt.setString(11,
					instrument.getDrawnAtBank());
			updateInstrumentDetailsStmt.setString(12,
					instrument.getDrawnAtBranch());
			updateInstrumentDetailsStmt
					.setString(13, instrument.getPayableAt());
			updateInstrumentDetailsStmt.setString(14,
					instrument.getCgtsiAccNumber());
			updateInstrumentDetailsStmt.setString(15,
					instrument.getAllocationType1());
			updateInstrumentDetailsStmt.registerOutParameter(16, Types.VARCHAR);
			updateInstrumentDetailsStmt.executeQuery();
			updateStatus = updateInstrumentDetailsStmt.getInt(1);

			Log.log(Log.INFO, "RPDAO", methodName, "status " + updateStatus);

			if (updateStatus == 0) {
				paymentId = updateInstrumentDetailsStmt.getString(2);
				Log.log(Log.INFO, "RPDAO", methodName, "id " + paymentId);
			} else if (updateStatus == 1) {
				Log.log(Log.INFO, "RPDAO", methodName, "status here");
				updateInstrumentDetailsStatus = false;
				String err = updateInstrumentDetailsStmt.getString(15);
				Log.log(Log.INFO, "RPDAO", methodName, "status err " + err);

				updateInstrumentDetailsStmt.close();
				updateInstrumentDetailsStmt = null;

				throw new DatabaseException(err);
			}
			updateInstrumentDetailsStmt.close();
			updateInstrumentDetailsStmt = null;
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		return paymentId;
	}

	/**
	 * This method updates the payment instrument details along with the
	 * generated payment id.
	 * 
	 * @param Instrument
	 * @return boolean
	 * @roseuid 399EABCF0109
	 */
	public String insertPaymentByCGTSI(PaymentDetails instrument)
			throws DatabaseException {
		String methodName = "insertInstrumentDetails";
		Connection connection = null;
		CallableStatement updateInstrumentDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		int updateStatus = 0;
		boolean updateInstrumentDetailsStatus = false;
		String paymentId = "";
		String formatedDate = "";
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();

			updateInstrumentDetailsStmt = connection
					.prepareCall("{?=call funcInsertPaymentByCGTSI(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			updateInstrumentDetailsStmt.registerOutParameter(1, Types.INTEGER);
			updateInstrumentDetailsStmt.registerOutParameter(2, Types.VARCHAR);
			updateInstrumentDetailsStmt.setString(3,
					instrument.getModeOfPayment());
			updateInstrumentDetailsStmt.setString(4,
					instrument.getCollectingBank());
			updateInstrumentDetailsStmt.setString(5,
					instrument.getCollectingBankBranch());

			// updateInstrumentDetailsStmt.setDate(6,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (instrument.getPaymentDate().toString())))) ;//mpdified by
			// pradeep for new server on 16.07.2012
			updateInstrumentDetailsStmt.setDate(6, new java.sql.Date(instrument
					.getPaymentDate().getTime()));
			updateInstrumentDetailsStmt.setString(7,
					instrument.getInstrumentNo());

			// updateInstrumentDetailsStmt.setDate(8,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (instrument.getInstrumentDate().toString())))) ;
			updateInstrumentDetailsStmt.setDate(8, new java.sql.Date(instrument
					.getInstrumentDate().getTime()));
			updateInstrumentDetailsStmt.setString(9,
					instrument.getModeOfDelivery());
			updateInstrumentDetailsStmt.setDouble(10,
					instrument.getInstrumentAmount());
			updateInstrumentDetailsStmt.setString(11,
					instrument.getDrawnAtBank());
			updateInstrumentDetailsStmt.setString(12,
					instrument.getDrawnAtBranch());
			updateInstrumentDetailsStmt
					.setString(13, instrument.getPayableAt());

			updateInstrumentDetailsStmt.registerOutParameter(14, Types.VARCHAR);
			updateInstrumentDetailsStmt.executeQuery();
			updateStatus = updateInstrumentDetailsStmt.getInt(1);

			if (updateStatus == 0) {
				paymentId = updateInstrumentDetailsStmt.getString(2);
			} else if (updateStatus == 1) {
				updateInstrumentDetailsStatus = false;
				String err = updateInstrumentDetailsStmt.getString(14);

				updateInstrumentDetailsStmt.close();
				updateInstrumentDetailsStmt = null;

				throw new DatabaseException(err);

			}

			updateInstrumentDetailsStmt.close();
			updateInstrumentDetailsStmt = null;

		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentId;
	}

	/**
	 * 
	 * @param danNo
	 * @param allocationDetail
	 * @param user
	 * @param paymentId
	 * @param connection
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void insertAllocationDetails(String danNo,
			AllocationDetail allocationDetail, User user, String paymentId,
			Connection connection) throws DatabaseException {
		String methodName = "insertAllocationDetails";
		// Connection connection = null ;
		CallableStatement insertAllocationDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Log.log(Log.INFO, "RPDAO", methodName, "Entered");

		int updateStatus = 0;
		// String paymentId = "";
		String formatedDate = "";
		String userId = "";

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		boolean newConn = false;

		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			insertAllocationDetailsStmt = connection
					.prepareCall("{?= call funcInsertAllocationDetail(?,?,?,?,?,?,?,?,?,?)}");
			insertAllocationDetailsStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.DEBUG, "RPDAO", methodName, "danNo " + danNo);
			Log.log(Log.DEBUG, "RPDAO", methodName,
					"cgpan " + allocationDetail.getCgpan());
			Log.log(Log.DEBUG, "RPDAO", methodName, "amount due "
					+ allocationDetail.getAmountDue());
			Log.log(Log.DEBUG, "RPDAO", methodName, "penalty "
					+ allocationDetail.getPenalty());
			Log.log(Log.DEBUG, "RPDAO", methodName, "allocation flag "
					+ allocationDetail.getAllocatedFlag());
			Log.log(Log.DEBUG, "RPDAO", methodName, "payment id " + paymentId);

			insertAllocationDetailsStmt.setString(2, danNo);
			insertAllocationDetailsStmt.setString(3,
					allocationDetail.getCgpan());
			insertAllocationDetailsStmt.setString(4, paymentId);
			insertAllocationDetailsStmt.setDouble(5,
					allocationDetail.getAmountDue());
			insertAllocationDetailsStmt.setDouble(6,
					allocationDetail.getPenalty());
			insertAllocationDetailsStmt.setString(7,
					allocationDetail.getAllocatedFlag());
			userId = user.getUserId();
			insertAllocationDetailsStmt.setString(8, userId);
			utilDate = new Date();
			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate))
			// ;//modified by pradeep for new server on 16.07.2012
			// insertAllocationDetailsStmt.setDate(9, sqlDate) ;
			insertAllocationDetailsStmt.setDate(9,
					new java.sql.Date(utilDate.getTime()));
			insertAllocationDetailsStmt.setString(
					10,
					RpConstants.SMILE_DELIMITER
							+ allocationDetail.getNotAllocatedReason()
							+ RpConstants.SMILE_DELIMITER);
			// System.out.println("SMILE_DELIMITER"+RpConstants.SMILE_DELIMITER+allocationDetail.getNotAllocatedReason()+RpConstants.SMILE_DELIMITER);
			insertAllocationDetailsStmt.registerOutParameter(11, Types.VARCHAR);
			insertAllocationDetailsStmt.executeQuery();

			updateStatus = insertAllocationDetailsStmt.getInt(1);
			String error = insertAllocationDetailsStmt.getString(11);

			Log.log(Log.DEBUG, "RPDAO", methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				insertAllocationDetailsStmt.close();
				insertAllocationDetailsStmt = null;

				Log.log(Log.ERROR, "RPDAO", methodName, error);

				throw new DatabaseException(error);
			} else if (updateStatus == 2) {
				insertAllocationDetailsStmt.close();
				insertAllocationDetailsStmt = null;

				Log.log(Log.ERROR, "RPDAO", methodName, error);

				throw new DatabaseException("Allocation already done.");
			}

			insertAllocationDetailsStmt.close();
			insertAllocationDetailsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RPDAO", methodName, exception.getMessage());
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "RPDAO", methodName, "Exited");
	}

	/* Added by Sukumar on 15-03-2008 */

	/**
	 * 
	 * @param danNo
	 * @param allocationDetail
	 * @param user
	 * @param paymentId
	 * @param connection
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void insertAllocationDetails1(String danNo,
			PaymentDetails paymentDetails, AllocationDetail allocationDetail,
			User user, String paymentId, Connection connection)
			throws DatabaseException {
		String methodName = "insertAllocationDetails";
		// Connection connection = null ;
		CallableStatement insertAllocationDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Log.log(Log.INFO, "RPDAO", methodName, "Entered");

		int updateStatus = 0;
		// String paymentId = "";
		String formatedDate = "";
		String userId = "";

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		boolean newConn = false;

		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			insertAllocationDetailsStmt = connection
					.prepareCall("{?= call funcInsertAllocationDetail1(?,?,?,?,?,?,?,?,?,?,?)}");
			insertAllocationDetailsStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.DEBUG, "RPDAO", methodName, "danNo " + danNo);
			Log.log(Log.DEBUG, "RPDAO", methodName,
					"cgpan " + allocationDetail.getCgpan());
			Log.log(Log.DEBUG, "RPDAO", methodName, "amount due "
					+ allocationDetail.getAmountDue());
			Log.log(Log.DEBUG, "RPDAO", methodName, "penalty "
					+ allocationDetail.getPenalty());
			Log.log(Log.DEBUG, "RPDAO", methodName, "allocation flag "
					+ allocationDetail.getAllocatedFlag());
			Log.log(Log.DEBUG, "RPDAO", methodName, "payment id " + paymentId);

			insertAllocationDetailsStmt.setString(2, danNo);
			insertAllocationDetailsStmt.setString(3,
					allocationDetail.getCgpan());
			insertAllocationDetailsStmt.setString(4, paymentId);
			insertAllocationDetailsStmt.setDouble(5,
					allocationDetail.getAmountDue());
			insertAllocationDetailsStmt.setDouble(6,
					allocationDetail.getPenalty());
			insertAllocationDetailsStmt.setString(7,
					allocationDetail.getAllocatedFlag());
			userId = user.getUserId();
			insertAllocationDetailsStmt.setString(8, userId);
			utilDate = new Date();
			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate))
			// ;//Modified by pradeep for new server on 16.07.2012
			// insertAllocationDetailsStmt.setDate(9, sqlDate) ;
			insertAllocationDetailsStmt.setDate(9,
					new java.sql.Date(utilDate.getTime()));
			// insertAllocationDetailsStmt.setString(10,
			// RpConstants.SMILE_DELIMITER+allocationDetail.getNotAllocatedReason()+RpConstants.SMILE_DELIMITER)
			// ;
			insertAllocationDetailsStmt.setString(10,
					paymentDetails.getremarksforAppropriation());
			// insertAllocationDetailsStmt.setDate(11,
			// java.sql.Date.valueOf(DateHelper.stringToSQLdate(dateFormat.format(paymentDetails.getRealisationDate()))));Modified
			// by pradeep for new server on 16.07.2012
			insertAllocationDetailsStmt.setDate(11, new java.sql.Date(
					paymentDetails.getRealisationDate().getTime()));
			insertAllocationDetailsStmt.registerOutParameter(12, Types.VARCHAR);
			insertAllocationDetailsStmt.executeQuery();

			updateStatus = insertAllocationDetailsStmt.getInt(1);
			String error = insertAllocationDetailsStmt.getString(12);

			Log.log(Log.DEBUG, "RPDAO", methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				insertAllocationDetailsStmt.close();
				insertAllocationDetailsStmt = null;

				Log.log(Log.ERROR, "RPDAO", methodName, error);

				throw new DatabaseException(error);
			} else if (updateStatus == 2) {
				insertAllocationDetailsStmt.close();
				insertAllocationDetailsStmt = null;

				Log.log(Log.ERROR, "RPDAO", methodName, error);

				throw new DatabaseException("Allocation already done.");
			}

			insertAllocationDetailsStmt.close();
			insertAllocationDetailsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RPDAO", methodName, exception.getMessage());
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "RPDAO", methodName, "Exited");
	}

	/* test end here */

	public void submitReAllocationDetails(RPActionForm actionForm,
			HttpServletRequest request, User user, String payId)
			throws DatabaseException, ExcessExceedsLimitException,
			ShortExceedsLimitException, NoUserFoundException,
			NoApplicationFoundException, MessageException {
		String methodName = "submitReAllocationDetails";
		Connection connection = null;
		Map cgpansAllocated = actionForm.getCgpans();
		Map danCgpanInfo = actionForm.getDanPanDetails();
		Map amtsRaised = actionForm.getAmountsRaised();
		Map penalties = actionForm.getPenalties();
		Set keys = cgpansAllocated.keySet();

		Iterator iterator = keys.iterator();

		Set danCgpanSet = danCgpanInfo.keySet();
		Iterator danCgpanIterator = danCgpanSet.iterator();

		ArrayList removedCgpans = new ArrayList();
		String memberId = actionForm.getMemberId();
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		boolean isRemoved = true;

		CallableStatement stmt = null;

		connection = DBConnection.getConnection(false);

		ArrayList danDetails = new ArrayList();

		try {
			double realisedAmt = 0;
			while (danCgpanIterator.hasNext()) {
				String danIdAvl = (String) danCgpanIterator.next();
				Log.log(Log.DEBUG, "RPDAO", methodName, "danIdAvl " + danIdAvl);
				ArrayList panDetails = (ArrayList) danCgpanInfo.get(danIdAvl);
				danDetails = new ArrayList();
				realisedAmt = 0;

				for (int i = 0; i < panDetails.size(); i++) {
					isRemoved = true;
					AllocationDetail allocationDetail = (AllocationDetail) panDetails
							.get(i);

					while (iterator.hasNext()) {
						String key = (String) iterator.next();

						Log.log(Log.DEBUG, "RPDAO", methodName, "key " + key);
						Log.log(Log.DEBUG, "RPDAO", methodName, "Request key "
								+ request.getParameter("cgpan(" + key + ")"));

						String danId = key.substring(0, key.indexOf("-"));
						key = key.replace('.', '_');
						if (!danId.equals(danIdAvl)
								|| request.getParameter("cgpan(" + key + ")") == null) {
							Log.log(Log.DEBUG, "RPDAO", methodName, "here");
							continue;
						}

						String cgpan = key.substring(key.indexOf("-") + 1,
								key.length());
						Log.log(Log.DEBUG, "RPDAO",
								"submitReallocationPayments", "danId,cgpan "
										+ danId + "," + cgpan);

						if (allocationDetail.getCgpan().equals(cgpan)) {
							isRemoved = false;
							Log.log(Log.DEBUG, "RPDAO", methodName,
									"cgpans same. update the data base ");

							allocationDetail.setAllocatedFlag(Constants.YES);
							DemandAdvice demandAdvice = new DemandAdvice();
							demandAdvice.setDanNo(danIdAvl);
							demandAdvice.setCgpan(cgpan);
							demandAdvice.setPaymentId(payId);
							// danIdAvl = danIdAvl.replace('.', '_');
							double amt = Double.parseDouble((String) amtsRaised
									.get(danIdAvl.replace('.', '_') + "-"
											+ cgpan));
							realisedAmt += amt;
							demandAdvice.setAmountRaised(amt);
							demandAdvice.setPenalty(Double
									.parseDouble((String) penalties
											.get(danIdAvl.replace('.', '_')
													+ "-" + cgpan)));
							demandAdvice.setAllocated(Constants.YES);
							demandAdvice.setAppropriated(Constants.YES);
							demandAdvice.setUserId(user.getUserId());
							demandAdvice.setReason("");
							demandAdvice.setBankId(bankId);
							demandAdvice.setZoneId(zoneId);
							demandAdvice.setBranchId(branchId);
							danDetails.add(demandAdvice);
							demandAdvice = null;
							// insertAllocationDetails(danIdAvl,
							// allocationDetail, user, payId, connection) ;

							break;
						}
					}

					if (isRemoved) {
						Log.log(Log.DEBUG, "RPDAO", methodName,
								"Removed from the data base "
										+ allocationDetail.getCgpan());

						DemandAdvice demandAdvice = new DemandAdvice();
						demandAdvice.setDanNo(danIdAvl);
						demandAdvice.setCgpan(allocationDetail.getCgpan());
						demandAdvice.setPaymentId(payId);
						// danIdAvl = danIdAvl.replace('.', '_');
						double amt = Double.parseDouble((String) amtsRaised
								.get(danIdAvl.replace('.', '_') + "-"
										+ allocationDetail.getCgpan()));
						demandAdvice.setAmountRaised(amt);
						demandAdvice.setPenalty(Double
								.parseDouble((String) penalties.get(danIdAvl
										.replace('.', '_')
										+ "-"
										+ allocationDetail.getCgpan())));
						demandAdvice.setAllocated(Constants.NO);
						demandAdvice.setAppropriated(Constants.NO);
						demandAdvice.setUserId(user.getUserId());
						demandAdvice.setReason("");
						danDetails.add(demandAdvice);
						demandAdvice = null;
						Log.log(Log.DEBUG, "RPDAO", methodName,
								"added to dan details");
					}

					iterator = keys.iterator();
				}

				RealisationDetail realisationDetail = new RealisationDetail();
				realisationDetail.setPaymentId(payId);
				realisationDetail.setRealisationAmount(realisedAmt);
				realisationDetail.setRealisationDate(new java.util.Date());

				appropriatePayment(danDetails, realisationDetail, true, request
						.getSession(false).getServletContext().getRealPath(""));
			}

			connection.commit();
			Log.log(Log.DEBUG, "RPDAO", methodName,
					"submit reallocation commited");
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
				Log.log(Log.DEBUG, "RPDAO", methodName,
						"submit reallocation rollback");
			} catch (SQLException sqlExp) {
			}
			throw dbExp;
		} catch (SQLException sqlExp) {
			throw new DatabaseException(sqlExp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "RPDAO", methodName, "Exited");
	}

	/**
	 * This method performs the following actions for each DAN. If the DAN type
	 * is CGDAN, then the CGPAN details will be transferred from the temporary
	 * table to GUARANTEE_FEE table along with the payment id.
	 * 
	 * If the DAN type is SFDAN, then the CGPAN details will be transferred from
	 * the temporary table to SERVICE_FEE table along with the payment id.
	 * 
	 * If the DAN type is CLDAN, then the DAN Amount details will be updated in
	 * the CLAIM_FEE table along with the payment id.
	 * 
	 * If the DAN type is SHDAN, then the Short Amount details will be updated
	 * in the SHORT_EXCESS_INFO table along with the payment id.
	 * 
	 * @param DANNo
	 * @param DANType
	 * @param PaymentId
	 * @return boolean
	 * @roseuid 399F62560196
	 */
	public boolean updatePaymentAllocationDetails(String danId, String danType,
			String paymentId) throws DatabaseException {
		Connection connection = null;
		CallableStatement updatePaymentAllocationDetailsStmt = null;

		int updateStatus = 0;
		boolean updatePaymentAllocationDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updatePaymentAllocationDetailsStmt = connection
					.prepareCall("{call <<updatepymtallocDetails>>(?,?,?)}");
			updatePaymentAllocationDetailsStmt.setString(1, danId);
			updatePaymentAllocationDetailsStmt.setString(2, danType);
			updatePaymentAllocationDetailsStmt.setString(3, paymentId);

			updatePaymentAllocationDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updatePaymentAllocationDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updatePaymentAllocationDetailsStmt
					.getObject(6).toString());

			if (updateStatus == 0) {
				updatePaymentAllocationDetailsStatus = true;
			} else if (updateStatus == 1) {
				updatePaymentAllocationDetailsStatus = false;
			}
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updatePaymentAllocationDetailsStatus;
	}

	/**
	 * method overriding done.
	 * 
	 * @param PaymentId
	 * @return
	 */
	public boolean updatePaymentAllocationDetails(DANSummary danSummary,
			String paymentId) throws DatabaseException {
		Connection connection = null;
		CallableStatement updatePaymentAllocationDetailsStmt = null;

		int updateStatus = 0;
		boolean updatePaymentAllocationDetailsStatus = false;
		String userId = "";

		try {
			connection = DBConnection.getConnection();
			updatePaymentAllocationDetailsStmt = connection
					.prepareCall("{?=call funcUpdateDanCGPAN(?,?,?,?)}");
			updatePaymentAllocationDetailsStmt.registerOutParameter(1,
					Types.INTEGER);
			updatePaymentAllocationDetailsStmt.setString(2,
					danSummary.getDanId());
			updatePaymentAllocationDetailsStmt.setString(3, paymentId);
			updatePaymentAllocationDetailsStmt.setString(4, userId);
			updatePaymentAllocationDetailsStmt.registerOutParameter(5,
					Types.VARCHAR);
			updatePaymentAllocationDetailsStmt.executeQuery();

			updateStatus = Integer.parseInt(updatePaymentAllocationDetailsStmt
					.getObject(1).toString());

			if (updateStatus == 0) {
				updatePaymentAllocationDetailsStatus = true;
			} else if (updateStatus == 1) {
				updatePaymentAllocationDetailsStatus = false;
				String err = updatePaymentAllocationDetailsStmt.getString(5);

				updatePaymentAllocationDetailsStmt.close();
				updatePaymentAllocationDetailsStmt = null;

				throw new DatabaseException(err);
			}

			updatePaymentAllocationDetailsStmt.close();
			updatePaymentAllocationDetailsStmt = null;
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return updatePaymentAllocationDetailsStatus;
	}

	/**
	 * This method returns an arraylist of PaymentList objects containing the
	 * payment details made by the members for any DAN.
	 * 
	 * @return java.util.ArrayList
	 * @roseuid 39A0CE220261
	 */
	public ArrayList getReceivedPayments(Date fromDate, Date toDate)
			throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			/*
			 * getReceivedPaymentsStmt = connection.prepareCall(
			 * "{?=call packGetPaymentDetails.funcGetPaymentDetails(?,?)}");
			 */
			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetails(?,?,?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.setDate(2,
					new java.sql.Date(fromDate.getTime()));
			getReceivedPaymentsStmt.setDate(3,
					new java.sql.Date(toDate.getTime()));
			getReceivedPaymentsStmt.registerOutParameter(4, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(5, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(5);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(4);
				exception = getReceivedPaymentsStmt.getString(5);

				// doubt dd whether each element has to be set.
				// Two parameters paymentId, instrumentNo added by sukant
				// PathInfotech on 17/01/2007
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int noOfRecords = 0;
				int payAmount = 0;
				int allocatedAmt = 0;
				int cases = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);

					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * 
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getReceivedPaymentsForGF(Date fromDate, Date toDate)
			throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForGF(?,?,?,?)}");
			System.out.println(" packGetPaymentDetails.funcGetPaymentDetailsForGF  ");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			// getReceivedPaymentsStmt.setString(2, memId);
			if (fromDate != null || !fromDate.equals("")) {
				getReceivedPaymentsStmt.setDate(2,
						new java.sql.Date(fromDate.getTime()));
			} else {
				getReceivedPaymentsStmt.setNull(2, Types.DATE);
			}
			if (toDate != null || toDate.equals("")) {
				getReceivedPaymentsStmt.setDate(3,
						new java.sql.Date(toDate.getTime()));
			} else {
				getReceivedPaymentsStmt.setNull(3, Types.DATE);
			}
			getReceivedPaymentsStmt.registerOutParameter(4, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(5, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(5);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(4);
				exception = getReceivedPaymentsStmt.getString(5);

				// doubt dd whether each element has to be set.
				// Two parameters paymentId, instrumentNo added by sukant
				// PathInfotech on 17/01/2007
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}
			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

			// connection.commit();

		} catch (Exception exception) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				ignore.printStackTrace();
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return receivedPayments;
	}

	/**
	 * 
	 * added by sukumar@path for getting batch appropriation for GF
	 * 
	 * @return receivedPayments
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getBatchPaymentReceivedForGF() throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";
			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetBatchPayDtlsForGF(?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.registerOutParameter(2, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(3, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(3);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(2);
				exception = getReceivedPaymentsStmt.getString(3);

				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				int inwardAmount = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					inwardAmount = resultSet.getInt(9);
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases, inwardAmount);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * 
	 * @param dateofRealisation
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList daywiseBatchPaymentsReceivedForGF(Date dateofRealisation)
			throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";
			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call PackGetGFListforBatAppr.FuncGetGFListforBatAppr(?,?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.setDate(2, new java.sql.Date(
					dateofRealisation.getTime()));
			getReceivedPaymentsStmt.registerOutParameter(3, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(4, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);
			// System.out.println("functionReturnValue:"+functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(4);
				System.out.println("Error:" + error);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(3);
				exception = getReceivedPaymentsStmt.getString(4);

				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				int inwardAmount = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					inwardAmount = resultSet.getInt(9);
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases, inwardAmount);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * 
	 * @param inwardDate
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList daywiseBatchPaymentsInwardedForGF(Date inwardDate)
			throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";
			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call PackGetGFListforBatApprNew.FuncGetGFListforBatApprNew(?,?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.setDate(2,
					new java.sql.Date(inwardDate.getTime()));
			getReceivedPaymentsStmt.registerOutParameter(3, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(4, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);
			// System.out.println("functionReturnValue:"+functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(4);
				System.out.println("Error:" + error);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(3);
				exception = getReceivedPaymentsStmt.getString(4);

				String inwardId = "";
				String memberName = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int inwardAmount = 0;
				int noOfRecords = 0;

				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					inwardId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					instrumentDt = resultSet.getDate(3);
					instrumentNo = resultSet.getString(4);
					inwardAmount = resultSet.getInt(5);
					paymentList = new PaymentList(inwardId, memberName,
							instrumentDt, instrumentNo, inwardAmount);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * 
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getReceivedPaymentsForCLAIM() throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForCLAIM(?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.registerOutParameter(2, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(3, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(3);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(2);
				exception = getReceivedPaymentsStmt.getString(3);

				// doubt dd whether each element has to be set.
				// Two parameters paymentId, instrumentNo added by sukant
				// PathInfotech on 17/01/2007
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * 
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getReceivedPaymentsForASF() throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForASF(?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.registerOutParameter(2, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(3, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(3);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(2);
				exception = getReceivedPaymentsStmt.getString(3);

				// doubt dd whether each element has to be set.
				// Two parameters paymentId, instrumentNo added by sukant
				// PathInfotech on 17/01/2007
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	/**
	 * This method returns an arraylist of PaymentList objects containing the
	 * payment allocated / appropriated by the members for any DAN.
	 * 
	 * @return java.util.ArrayList
	 * @roseuid 39A0CE220261
	 */
	public ArrayList getPaymentsForReallocation() throws DatabaseException {
		Log.log(Log.INFO, className, "getPaymentsForReallocation", "Entered");

		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = new ArrayList();
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetReceivedPayments.funcGetReceivedPayments(?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
			getReceivedPaymentsStmt.registerOutParameter(2, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(3, Types.VARCHAR);

			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(3);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(2);
				exception = getReceivedPaymentsStmt.getString(3);

				// doubt dd whether each element has to be set.
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				// Date instrumentDt = null;
				// String instrumentNo = "";
				int noOfRecords = 0;
				Map memberDetails = new HashMap();
				while (resultSet.next()) {
					memberId = resultSet.getString(3);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(1);
					// instrumentNo = resultSet.getString(4);
					// instrumentDt = resultSet.getDate(5) ;
					paymentList = new PaymentList(memberId, memberName,
							paymentId);

					// if(memberDetails.get(memberId)==null)
					// {
					memberDetails.put(memberId, paymentList);
					receivedPayments.add(paymentList);
					// }
					// receivedPayments.add(paymentList);
					++noOfRecords;
				}

				/*
				 * Set keys=memberDetails.keySet();
				 * 
				 * Iterator iterator=keys.iterator();
				 * 
				 * while(iterator.hasNext()) { Object object=iterator.next();
				 * 
				 * receivedPayments.add(memberDetails.get(object)); }
				 */

				resultSet.close();
				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;
			}

			connection.commit();
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, "getPaymentsForReallocation", "Exited");

		return receivedPayments;
	}

	/**
	 * Gets the details of the instrument(cheque etc.) paid by the member. The
	 * Return value is a PaymentDetail object.
	 * 
	 * @param PaymentId
	 * @return com.cgtsi.receiptspayments.PaymentDetails
	 * @roseuid 39A0D05703D8
	 */
	public PaymentDetails getInstrumentDetails(String paymentId)
			throws DatabaseException {
		PaymentDetails paymentDetails = new PaymentDetails();
		Connection connection = null;
		CallableStatement getInstrumentDetailsStmt = null;

		String modeOfDelivery = "";
		String modeOfPayment = "";
		String instrumentNumber = "";
		Date instrumentDate = null;
		double instrumentAmount = 0;
		String drawnAtBank = "";
		String drawnAtBranch = "";
		String payeeBankOrBranch = "";
		String payableAt = "";
		String collectingBank = "";
		String collectingBranch = "";

		double allocatedAmount = 0;

		String methodName = "getInstrumentDetails";

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getInstrumentDetailsStmt = connection
					.prepareCall("{?=call funcGetPaymentDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			getInstrumentDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getInstrumentDetailsStmt.setString(2, paymentId);

			getInstrumentDetailsStmt.registerOutParameter(3, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(4, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(5, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(6, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(7, Types.DATE);
			getInstrumentDetailsStmt.registerOutParameter(8, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(9, Types.DOUBLE);
			getInstrumentDetailsStmt.registerOutParameter(10, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(11, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(12, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(13, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(14, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(15, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(16, Types.DOUBLE);
			getInstrumentDetailsStmt.registerOutParameter(17, Types.DATE);
			getInstrumentDetailsStmt.registerOutParameter(18, Types.VARCHAR);
			getInstrumentDetailsStmt.registerOutParameter(19, Types.VARCHAR);

			getInstrumentDetailsStmt.execute();

			int functionReturnValue = getInstrumentDetailsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getInstrumentDetailsStmt.getString(19);

				getInstrumentDetailsStmt.close();
				getInstrumentDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				modeOfDelivery = getInstrumentDetailsStmt.getString(4);
				modeOfPayment = getInstrumentDetailsStmt.getString(5);
				instrumentNumber = getInstrumentDetailsStmt.getString(6);
				instrumentDate = getInstrumentDetailsStmt.getDate(7);
				instrumentAmount = getInstrumentDetailsStmt.getDouble(9);
				payableAt = getInstrumentDetailsStmt.getString(11);
				drawnAtBank = getInstrumentDetailsStmt.getString(12);
				drawnAtBranch = getInstrumentDetailsStmt.getString(13);
				collectingBank = getInstrumentDetailsStmt.getString(14);
				collectingBranch = getInstrumentDetailsStmt.getString(15);
				allocatedAmount = getInstrumentDetailsStmt.getDouble(16);

				paymentDetails.setModeOfDelivery(modeOfDelivery);
				paymentDetails.setModeOfPayment(modeOfPayment);
				paymentDetails.setInstrumentNo(instrumentNumber);
				paymentDetails.setInstrumentDate(instrumentDate);
				paymentDetails.setInstrumentAmount(instrumentAmount);
				paymentDetails.setPayableAt(payableAt);
				paymentDetails.setDrawnAtBank(drawnAtBank);
				paymentDetails.setDrawnAtBranch(drawnAtBranch);
				paymentDetails.setCollectingBank(collectingBank);
				paymentDetails.setCollectingBankBranch(collectingBranch);
				paymentDetails.setAllocatedAmount(allocatedAmount);
				paymentDetails.setPaymentDate(getInstrumentDetailsStmt
						.getDate(17));
				paymentDetails.setCgtsiAccNumber(getInstrumentDetailsStmt
						.getString(18));

				Log.log(Log.INFO, className, methodName, "Done inside RpDAO");

				getInstrumentDetailsStmt.close();
				getInstrumentDetailsStmt = null;

			}

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return paymentDetails;
	}

	/**
	 * This method returns all the CGPANs belonging to the DAN for which
	 * payments have been made. The DANs include CGDANs, SFDANs, CLDANs and
	 * Short DANs. The output returned by this method is an ArrayList of
	 * DemandAdvice Object.
	 * 
	 * @param PaymentId
	 * @return java.util.ArrayList
	 * @roseuid 39A0D4990148
	 */
	public ArrayList getDANDetails(String paymentId) throws DatabaseException {
		String methodName = "getDANDetails";
		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getDANDetailsStmt = null;
		ArrayList danDetails = new ArrayList();
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		Log.log(Log.INFO, className, "getDANDetails", "Entering getDANDetails");
		try {
			String exception = "";

			Log.log(Log.INFO, className, methodName, "Before preparing call");
			getDANDetailsStmt = connection
					.prepareCall("{?=call packGetDansforAppropriation.funcGetDansforAppropriation(?,?,?)}");

			Log.log(Log.INFO, className, methodName,
					"Before registering parameters");
			getDANDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDANDetailsStmt.setString(2, paymentId);

			getDANDetailsStmt.registerOutParameter(3, Constants.CURSOR);
			getDANDetailsStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"After registering parameters");

			Log.log(Log.INFO, className, methodName, "Before execute");
			getDANDetailsStmt.execute();
			Log.log(Log.INFO, className, methodName, "After execute");

			int functionReturnValue = getDANDetailsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getDANDetailsStmt.getString(4);

				getDANDetailsStmt.close();
				getDANDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			}

			resultSet = (ResultSet) getDANDetailsStmt.getObject(3);
			exception = getDANDetailsStmt.getString(4);

			// doubt dd whether each element has to be set.

			Log.log(Log.INFO, className, methodName, "Before running resultset");
			while (resultSet.next()) {
				demandAdvice = new DemandAdvice();

				Log.log(Log.INFO, className, methodName, "Getting dan id");
				// Start Code By Path on 9Oct06
				// System.out.println("Getting dan id = "+resultSet.getString(1));
				demandAdvice.setDanNo(resultSet.getString(1));
				// System.out.println(resultSet.getString(1));
				Log.log(Log.INFO, className, methodName, "Getting cgpan");
				demandAdvice.setCgpan(resultSet.getString(2));
				// System.out.println(resultSet.getString(2));
				Log.log(Log.INFO, className, methodName,
						"Getting amount raised");
				demandAdvice.setAmountRaised(resultSet.getDouble(3));
				// System.out.println(resultSet.getDouble(3));
				Log.log(Log.INFO, className, methodName, "Getting penalty");
				demandAdvice.setPenalty(resultSet.getDouble(4));
				// System.out.println(resultSet.getDouble(4));
				Log.log(Log.INFO, className, methodName, "Getting allocated");
				demandAdvice.setAllocated(resultSet.getString(5));
				// System.out.println(resultSet.getString(5));
				String reasons = resultSet.getString(6);
				 System.out.println("reasons:"+resultSet.getString(6));
				Log.log(Log.INFO, className, methodName, "Getting reason:"
						+ reasons);
				if (reasons != null) {
					reasons = reasons.substring(
							reasons.indexOf(RpConstants.SMILE_DELIMITER) + 1,
							reasons.lastIndexOf(RpConstants.SMILE_DELIMITER));
				}
				demandAdvice.setReason(reasons);
				// System.out.println(demandAdvice.getReason());
				Log.log(Log.INFO, className, methodName, "Getting appropriated");
				demandAdvice.setAppropriated(resultSet.getString(8));

				Log.log(Log.INFO, className, methodName, "Getting appropriated");
				demandAdvice.setDanType(resultSet.getString(9));

				Log.log(Log.INFO, className, methodName, "Getting Bank Id");
				demandAdvice.setBankId(resultSet.getString(10));

				Log.log(Log.INFO, className, methodName, "Getting Zone Id");
				demandAdvice.setZoneId(resultSet.getString(11));

				Log.log(Log.INFO, className, methodName, "Getting Branch Id");
				demandAdvice.setBranchId(resultSet.getString(12));

				// added by sukumar@path on 15-Dec-2010 for providing allocated
				// cgpan application status
				Log.log(Log.INFO, className, methodName,
						"Getting Application Status");
				demandAdvice.setStatus(resultSet.getString(13));

				Log.log(Log.INFO, className, methodName, "adding demand advice");
				danDetails.add(demandAdvice);

				Log.log(Log.INFO, className, methodName,
						"After adding demand advice");
			}

			resultSet.close();
			getDANDetailsStmt.close();
			getDANDetailsStmt = null;

			Log.log(Log.INFO, className, methodName, "After running resultset");

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
		Log.log(Log.INFO, className, methodName, "Done inside getDANDetails");
		return danDetails;
	}

	public ArrayList getDANDetailsForReallocation(String paymentId,
			String memberId) throws DatabaseException {
		String methodName = "getDANDetails";
		DemandAdvice demandAdvice = null;
		AllocationDetail allocationDtl = null;
		Connection connection = null;
		CallableStatement getDANDetailsStmt = null;
		ArrayList danDetails = new ArrayList();
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		Log.log(Log.INFO, className, "getDANDetails", "Entering getDANDetails");
		try {
			String exception = "";

			String bnkId = memberId.substring(0, 4);
			String zneId = memberId.substring(4, 8);
			String brnId = memberId.substring(8, 12);
			Log.log(Log.INFO, className, "getDANDetails", "bank id " + bnkId);
			Log.log(Log.INFO, className, "getDANDetails", "zone id " + zneId);
			Log.log(Log.INFO, className, "getDANDetails", "branch id " + brnId);
			Log.log(Log.INFO, className, methodName, "Before preparing call");
			getDANDetailsStmt = connection
					.prepareCall("{?=call packGetDansforReallocation.funcGetDansforReallocation(?,?,?,?,?,?)}");

			Log.log(Log.INFO, className, methodName,
					"Before registering parameters");
			getDANDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDANDetailsStmt.setString(2, paymentId);
			getDANDetailsStmt.setString(3, bnkId);
			getDANDetailsStmt.setString(4, zneId);
			getDANDetailsStmt.setString(5, brnId);

			getDANDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDANDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"After registering parameters");

			Log.log(Log.INFO, className, methodName, "Before execute");
			getDANDetailsStmt.execute();
			Log.log(Log.INFO, className, methodName, "After execute");

			int functionReturnValue = getDANDetailsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getDANDetailsStmt.getString(7);

				getDANDetailsStmt.close();
				getDANDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			}

			resultSet = (ResultSet) getDANDetailsStmt.getObject(6);
			exception = getDANDetailsStmt.getString(7);

			// doubt dd whether each element has to be set.

			Log.log(Log.INFO, className, methodName, "Before running resultset");
			while (resultSet.next()) {
				allocationDtl = new AllocationDetail();

				Log.log(Log.INFO, className, methodName, "Getting dan id");
				allocationDtl.setDanNo(resultSet.getString(1));

				allocationDtl.setPaymentId(paymentId);

				Log.log(Log.INFO, className, methodName, "Getting cgpan");
				allocationDtl.setCgpan(resultSet.getString(2));

				Log.log(Log.INFO, className, methodName,
						"Getting amount raised");
				allocationDtl.setAmountDue(resultSet.getDouble(3));

				Log.log(Log.INFO, className, methodName, "Getting penalty");
				allocationDtl.setPenalty(resultSet.getDouble(4));

				Log.log(Log.INFO, className, methodName, "Getting allocated");
				allocationDtl.setAllocatedFlag(resultSet.getString(5));

				Log.log(Log.INFO, className, methodName, "Getting reason:"
						+ resultSet.getString(6));
				allocationDtl.setNotAllocatedReason(resultSet.getString(6));

				Log.log(Log.INFO, className, methodName, "Getting appropriated");
				allocationDtl.setAppropriatedFlag(resultSet.getString(8));

				Log.log(Log.INFO, className, methodName, "Getting appropriated");
				allocationDtl.setDanType(resultSet.getString(9));

				Log.log(Log.INFO, className, methodName, "Getting Bank Id");
				allocationDtl.setBankId(resultSet.getString(10));

				Log.log(Log.INFO, className, methodName, "Getting Zone Id");
				allocationDtl.setZoneId(resultSet.getString(11));

				Log.log(Log.INFO, className, methodName, "Getting Branch Id");
				allocationDtl.setBranchId(resultSet.getString(12));

				Log.log(Log.INFO, className, methodName, "Getting Branch Id");
				allocationDtl.setNameOfUnit(resultSet.getString(13));

				Log.log(Log.INFO, className, methodName, "Getting Branch Id");
				allocationDtl.setFacilityCovered(resultSet.getString(14));

				Log.log(Log.INFO, className, methodName, "Getting Branch Id");
				allocationDtl.setFirstDisbursementDate(resultSet.getDate(15));

				Log.log(Log.INFO, className, methodName, "adding demand advice");
				danDetails.add(allocationDtl);

				allocationDtl = null;

				Log.log(Log.INFO, className, methodName,
						"After adding demand advice");
			}

			resultSet.close();
			resultSet = null;
			getDANDetailsStmt.close();
			getDANDetailsStmt = null;

			connection.commit();
			Log.log(Log.INFO, className, methodName, "After running resultset");

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, className, methodName, "Done inside getDANDetails");
		return danDetails;
	}

	/**
	 * 
	 * @param payId
	 * @param userId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int aftergfbatchappropriatePayments(String paymentId, String userId)
			throws DatabaseException {
		Connection connection = null;
		int tempcount = 0;
		CallableStatement batchAppropriatedStmt = null;

		boolean status = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			batchAppropriatedStmt = connection
					.prepareCall("{?=call FuncApprPayID(?,?,?)}");

			batchAppropriatedStmt.registerOutParameter(1, Types.INTEGER);
			batchAppropriatedStmt.setString(2, paymentId);
			batchAppropriatedStmt.setString(3, userId);
			batchAppropriatedStmt.registerOutParameter(4, Types.VARCHAR);

			batchAppropriatedStmt.executeQuery();
			int returnValue = batchAppropriatedStmt.getInt(1);
			// System.out.println("Payment Id:"+paymentId+" Return Value:"+returnValue);
			if (returnValue == Constants.FUNCTION_FAILURE) {

				String error = batchAppropriatedStmt.getString(4);
				System.out.println("Error:" + error);
				status = false;
				tempcount = 0;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;

				Log.log(Log.ERROR, "RpDAO", "aftergfbatchappropriatePayments",
						error);

				connection.rollback();

				throw new DatabaseException(batchAppropriatedStmt.getString(4));

			} else if (returnValue == Constants.FUNCTION_SUCCESS) {
				status = true;
				tempcount = 1;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;
			}
			// System.out.println("Temp Count: "+tempcount);
			connection.commit();
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return tempcount;

	}

	/**
	 * 
	 * @param memberId
	 * @param paymentId
	 * @param allocatedAmt
	 * @param neftCode
	 * @param bankName
	 * @param zoneName
	 * @param branchName
	 * @param ifscCode
	 * @param paymentDt
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void afterMapRPwithNEFTDtls(String memberId, String paymentId,
			double allocatedAmt, String neftCode, String bankName,
			String zoneName, String branchName, String ifscCode,
			Date paymentDt, String userId) throws DatabaseException {
		String methodName = "afterMapRPwithNEFTDtls";

		Log.log(Log.INFO, "RpDAO", methodName, "Entered");

		boolean newConn = false;
		Connection connection = DBConnection.getConnection(false);

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newConn = true;
		}
		try {
			CallableStatement callable = null;
			int errorCode;
			String error;

			callable = connection
					.prepareCall("{?=call Funcinsnefttransdet (?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.setString(2, memberId.substring(0, 4));
			callable.setString(3, memberId.substring(4, 8));
			callable.setString(4, memberId.substring(8, 12));
			callable.setString(5, paymentId);
			callable.setDouble(6, allocatedAmt);
			callable.setString(7, neftCode);
			callable.setString(8, bankName);
			callable.setString(9, zoneName);
			callable.setString(10, branchName);
			callable.setString(11, ifscCode);
			if (!(paymentDt == null)) {
				callable.setDate(12, new java.sql.Date(paymentDt.getTime()));
			} else {
				callable.setNull(12, Types.DATE);
			}
			callable.setString(13, userId);

			// java.util.Date currentDate=new java.util.Date();
			// callable.setDate(14,new java.sql.Date (currentDate.getTime()));

			callable.registerOutParameter(14, Types.VARCHAR);

			callable.execute();

			errorCode = callable.getInt(1);
			// System.out.println("errorCode:"+errorCode);
			error = callable.getString(14);
			// System.out.println("error:"+error);

			Log.log(Log.DEBUG, "RpDAO", methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", methodName, error);

				callable.close();
				callable = null;
				connection.rollback();

				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
			connection.commit();

		} catch (SQLException e) {
			Log.log(Log.ERROR, "RpDAO", methodName, e.getMessage());

			Log.logException(e);

			if (newConn) {
				try {
					connection.rollback();
				} catch (SQLException ignore) {
				}
			}

			throw new DatabaseException(
					"Unable to Insert NEFT Payment Id Details.");

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO, "RpDAO", methodName, "Exited");
	}

	public int aftergfdaywisebatchappropriatePayments(String paymentId,
			String userId, Date dateofRealisation)
			throws DatabaseException {
		
		System.out.println(" aftergfdaywisebatchappropriatePayments S");
		Connection connection = null;
		int tempcount = 0;
		CallableStatement batchAppropriatedStmt = null;

		boolean status = false;

		//java.util.Date utilDate;
		//java.sql.Date sqlDate;
		
	   
		 
	        
		 
		//System.out.println(" dateofRealisation.getTime()"+dateofRealisation.getTime());

		try {
			
			 
			//java.sql.Date sqlDate = new java.sql.Date(dateofRealisation.getTime());
		    
		    
		    
		    //System.out.println("sqlDate "+sqlDate);
	          
	       
	             
			connection = DBConnection.getConnection();
			batchAppropriatedStmt = connection
					.prepareCall("{?=call funcapprpayidNew(?,?,?,?)}");

			batchAppropriatedStmt.registerOutParameter(1, Types.INTEGER);
			batchAppropriatedStmt.setString(2, paymentId);
			batchAppropriatedStmt.setString(3, userId);
			
			//System.out.println("new java.sql.Date(dateofRealisation.getTime())"+new java.sql.Date(dateofRealisation.getTime()));
					 
		//	batchAppropriatedStmt.setDate(4, new java.sql.Date(
	                //  	dateofRealisation.getTime()));
			System.out.println("Reliazation date====RP DAO==6820=== "+dateofRealisation);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//String startDate = "2013-09-25";
			//Date frmDate = sdf.parse(startDate); 
	//================added from here 2nd Dec 2016======================//		
			
			batchAppropriatedStmt.setDate(4, (java.sql.Date) dateofRealisation);

			batchAppropriatedStmt.registerOutParameter(5, Types.VARCHAR);

			batchAppropriatedStmt.executeQuery();
			int returnValue = batchAppropriatedStmt.getInt(1);
			// System.out.println("Payment Id:"+paymentId+" Return Value:"+returnValue);
			if (returnValue == Constants.FUNCTION_FAILURE) {
				System.out.println("Payment Id:" + paymentId);
				String error = batchAppropriatedStmt.getString(5);
				System.out.println("Error:" + error);
				status = false;
				tempcount = 0;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;

				Log.log(Log.ERROR, "RpDAO", "aftergfbatchappropriatePayments",
						error);

				connection.rollback();

				throw new DatabaseException(batchAppropriatedStmt.getString(5));

			} else if (returnValue == Constants.FUNCTION_SUCCESS) {
				status = true;
				tempcount = 1;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;
			}
			// System.out.println("Temp Count: "+tempcount);
			connection.commit();
		} catch (Exception exception) {
			System.out.println(" exception"+exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return tempcount;

	}

	/**
	 * 
	 * @param inwardId
	 * @param instrumentNo
	 * @param userId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int dayWiseddMarkedForDepositedSummary(String inwardId,
			String instrumentNo, String userId, Date depositDate)
			throws DatabaseException {
		Connection connection = null;
		int tempcount = 0;
		CallableStatement batchAppropriatedStmt = null;

		boolean status = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			batchAppropriatedStmt = connection
					.prepareCall("{?=call FuncUpdChqDeposit(?,?,?,?,?)}");

			batchAppropriatedStmt.registerOutParameter(1, Types.INTEGER);
			batchAppropriatedStmt.setString(2, inwardId);
			batchAppropriatedStmt.setString(3, instrumentNo);
			batchAppropriatedStmt.setString(4, userId);
			batchAppropriatedStmt.setDate(5,
					new java.sql.Date(depositDate.getTime()));
			// System.out.println("Inward Id:"+inwardId+" Instrument No:"+instrumentNo+" User Id:"+userId);
			batchAppropriatedStmt.registerOutParameter(6, Types.VARCHAR);

			batchAppropriatedStmt.executeQuery();
			int returnValue = batchAppropriatedStmt.getInt(1);
			// System.out.println("Payment Id:"+paymentId+" Return Value:"+returnValue);
			if (returnValue == Constants.FUNCTION_FAILURE) {
				System.out.println("Inward Id:" + inwardId + " Instrument No:"
						+ instrumentNo + " User Id:" + userId);
				String error = batchAppropriatedStmt.getString(6);
				System.out.println("Error:" + error);
				status = false;
				tempcount = 0;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;

				Log.log(Log.ERROR, "RpDAO",
						"dayWiseddMarkedForDepositedSummary", error);

				connection.rollback();

				throw new DatabaseException(batchAppropriatedStmt.getString(6));

			} else if (returnValue == Constants.FUNCTION_SUCCESS) {
				status = true;
				tempcount = 1;
				batchAppropriatedStmt.close();
				batchAppropriatedStmt = null;
			}
			// System.out.println("Temp Count: "+tempcount);
			connection.commit();
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return tempcount;

	}

	public double appropriatePayment(ArrayList demandAdvices,
			RealisationDetail realisationDetail, boolean reallocation,
			String contextPath) throws DatabaseException,
			ShortExceedsLimitException, ExcessExceedsLimitException,
			NoUserFoundException, NoApplicationFoundException, MessageException {
		DemandAdvice demandAdvice = null;
		AdminDAO adminDAO = new AdminDAO();

		ParameterMaster parameterMaster;
		
		
		int sizeOfDemandAdvices = 0;
		int noOfCGPANs = 0;
		double amount = 0.0;
		double penalty = 0.0;
		double appropriatedAmount = 0.0;
		double realisationAmount = 0.0;
		double shortAmount = 0.0;
		double shortLimit = 0.0;
		double excessAmount = 0.0;
		double excessLimit = 0.0;
		String appropriated = "";
		String cgpan = "";
		boolean atleastOneAppropriated = false;

		Administrator administrator = new Administrator();
		parameterMaster = administrator.getParameter();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		sizeOfDemandAdvices = demandAdvices.size();
		double gfAmt = 0;
		double sfAmt = 0;
		double shAmt = 0;
		double clAmt = 0;
		for (int i = 0; i < sizeOfDemandAdvices; ++i) 
		{
			demandAdvice = (DemandAdvice) demandAdvices.get(i);
			amount = demandAdvice.getAmountRaised();
			penalty = demandAdvice.getPenalty();
			appropriated = demandAdvice.getAppropriated();

			if (appropriated.equalsIgnoreCase("Y")) {
				appropriatedAmount = appropriatedAmount + (amount + penalty);
				++noOfCGPANs;
				atleastOneAppropriated = true;
				String danType = demandAdvice.getDanNo().substring(0, 2);
				// Log.log(Log.DEBUG, className, "appropriatePayment", "type " +
				// danType);
				if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_CGDAN)
						|| "GF".equalsIgnoreCase(danType)) {// add GF
					gfAmt += amount + penalty;
				} else if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_SFDAN)) {
					sfAmt += amount + penalty;
				} else if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_SHDAN)) {
					shAmt += amount + penalty;
				} else if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_CLDAN)) {
					clAmt += amount + penalty;
				}
			}// if end
		}// for end

		//Diksha
		
		Properties accCodes = new Properties();
		File tempFile = new File(contextPath + "\\WEB-INF\\classes",
				RpConstants.AC_CODE_FILE_NAME);
		Log.log(Log.DEBUG,"RPAction","getPaymentsMade","file opened ");
		File accCodeFile = new File(tempFile.getAbsolutePath());
		try {
			FileInputStream fin = new FileInputStream(accCodeFile);
			accCodes.load(fin);
		} catch (FileNotFoundException fe) {
			throw new MessageException("Could not load Account Codes.");
		} catch (IOException ie) {
			throw new MessageException("Could not load Account Codes.");
		}
		
		//Diksha end
		
		Connection connection = DBConnection.getConnection(false);
		/**
		 * Only if atleast one of the cgpans have been appropriated the payment
		 * realisation details will be updated in the database. The payment
		 * details are first moved from temp to the intranet and internet. Then
		 * the realisation details are updated and the short and excess details
		 * if any are also updated. Then the dan cgpan details are updated in
		 * the intranet and internet.
		 */

		try {
			PaymentDetails paymentDetails = null;
			String paymentId = realisationDetail.getPaymentId();
			paymentDetails = getPaymentDetails(paymentId);
			// System.out.println("paymentDetails.getInstrumentNo():"+
			// paymentDetails.getInstrumentNo());
			// System.out.println("paymentDetails.getInstrumentDate()"+paymentDetails.getInstrumentDate());
			// System.out.println("paymentDetails.getInstrumentAmount()"+paymentDetails.getInstrumentAmount());
			if (atleastOneAppropriated) {
				// Log.log(Log.INFO, className, "appropriatePayment",
				// "realisation amount " + realisationAmount);
				// Log.log(Log.INFO, className, "appropriatePayment",
				// "appropriated amount " + appropriatedAmount);
				realisationAmount = realisationDetail.getRealisationAmount();
				if (reallocation) {
					realisationAmount = paymentDetails.getInstrumentAmount();
				}
				// Log.log(Log.INFO, className, "appropriatePayment",
				// "realisation amount " + realisationAmount);

				String narration = "Payment Id: " + paymentId;
				narration = narration + " Member Id: "
						+ demandAdvice.getBankId() + demandAdvice.getZoneId()
						+ demandAdvice.getBranchId();
				narration = narration + " Dan Nos: ";
				String oldDanId = "";
				int added = 1;
				for (int i = 0; i < sizeOfDemandAdvices; ++i) {
					demandAdvice = (DemandAdvice) demandAdvices.get(i);
					appropriated = demandAdvice.getAppropriated();
					if (appropriated.equalsIgnoreCase("Y")) {
						if (oldDanId != demandAdvice.getDanNo()) {
							if (added <= 5) {
								narration = narration + demandAdvice.getDanNo()
										+ ", ";
								oldDanId = demandAdvice.getDanNo();
								added++;
							}
						}
					}
				}
				narration = narration.substring(0, narration.length() - 2);
				if (added > 5) {
					narration = narration + " etc.";
				}

				sizeOfDemandAdvices = demandAdvices.size();

				if (realisationAmount < appropriatedAmount) {
					shortAmount = appropriatedAmount - realisationAmount;
					shortLimit = parameterMaster.getShortLimit();
					if (shortAmount > (shortLimit * noOfCGPANs)) {
						throw new ShortExceedsLimitException(
								"Short Exceeds Limit");
					}
				} else if (realisationAmount > appropriatedAmount) {
					excessAmount = realisationAmount - appropriatedAmount;
					excessLimit = parameterMaster.getExcessLimit();
					// Log.log(Log.INFO, className, "appropriatePayment",
					// "excess limit " + excessLimit);
					if (excessAmount > excessLimit) {
						// throw new
						// ExcessExceedsLimitException("Excess Exceeds Limit");
						ArrayList vouchers = new ArrayList();
						VoucherDetail voucherDetail = new VoucherDetail();
						voucherDetail.setBankGLCode(accCodes
								.getProperty(RpConstants.BANK_AC));	//Diksha
						voucherDetail.setBankGLName("");
						voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
						voucherDetail.setAmount(0 - excessAmount);
						voucherDetail
								.setVoucherType(RpConstants.PAYMENT_VOUCHER);
						String danId = "";
						Voucher voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.EXCESS_AC));	//Diksha 
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("D");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("" + excessAmount);
						vouchers.add(voucher);
						voucher = null;
						voucherDetail.setNarration(narration);
						voucherDetail.setVouchers(vouchers);

						insertVoucherDetails(voucherDetail,
								demandAdvice.getUserId(), connection);
						vouchers.clear();
						// Log.log(Log.DEBUG,"RpProcessor","reapproveLoanAmount","inserted payment voucher");
					}
				}

				if (!reallocation) {
					// to move the payment details from the temp to intranet and
					// internet.
					Log.log(Log.INFO, className, "appropriatePayment Sukumar1",
							"Moving payment Details from temp "
									+ realisationDetail.getPaymentId());
					updatePaymentDetails(realisationDetail.getPaymentId(),
							connection);

					Log.log(Log.INFO, className, "appropriatePayment Sukumar2",
							"Executing updateRealisationDetails");
					updateRealisationDetails(realisationDetail, connection);
					Log.log(Log.INFO, className, "appropriatePayment Sukumar3",
							"After Executing updateRealisationDetails");
				}
				String danId = "";
				ArrayList vouchers = new ArrayList();
				String shortId = "";
				int excessId = 0;
				if (realisationAmount < appropriatedAmount) {
					// Log.log(Log.INFO,className, "appropriatePayment",
					// "Short within limit");
					for (int i = 0; i < sizeOfDemandAdvices; ++i) {
						demandAdvice = (DemandAdvice) demandAdvices.get(i);
						appropriated = demandAdvice.getAppropriated();
						// Log.log(Log.INFO,className, "appropriatePayment",
						// "appropriate  " + appropriated);
						if (appropriated.equalsIgnoreCase("Y")) {
							if (shortAmount > shortLimit) {
								// Log.log(Log.INFO,className,
								// "appropriatePayment",
								// "Executing rpDAO.updateShortAmountDetails(demandAdvice, shortLimit)");
								demandAdvice.setPaymentId(paymentId);
								shortId = shortId
										+ updateShortAmountDetails(
												demandAdvice, shortLimit,
												connection) + ", ";
								shortAmount = shortAmount - shortLimit;
							} else {
								// Log.log(Log.INFO,className,
								// "appropriatePayment",
								// "Executing rpDAO.updateShortAmountDetails(demandAdvice, shortAmount)");
								demandAdvice.setPaymentId(paymentId);
								shortId = shortId
										+ updateShortAmountDetails(
												demandAdvice, shortAmount,
												connection);
								break;
							}
						}
					}
				} else if (realisationAmount > appropriatedAmount
						&& !(excessAmount > excessLimit)) {
					demandAdvice = (DemandAdvice) demandAdvices.get(0);
					// Log.log(Log.INFO,className, "appropriatePayment",
					// "Executing rpDAO.updateExcessAmountDetails(demandAdvice, excessAmount)");
					demandAdvice.setPaymentId(paymentId);
					excessId = updateExcessAmountDetails(demandAdvice,
							excessAmount, connection);
					Log.log(Log.INFO, className, "appropriatePayment Sukumar4",
							"Executing rpDAO.updateExcessAmountDetails(demandAdvice, excessAmount)"
									+ excessId);
				}

				if (!reallocation) {
					Log.log(Log.INFO, className, "appropriatePayment Sukumar5",
							"Executing rpDAO.updateExcessAmountDetails(demandAdvice, excessAmount)"
									+ excessId);

					VoucherDetail voucherDetail = new VoucherDetail();
					vouchers.clear();
					voucherDetail.setBankGLCode(accCodes
							.getProperty(RpConstants.BANK_AC));
					voucherDetail.setBankGLName("");
					voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
					voucherDetail.setAmount(realisationAmount);
					voucherDetail.setVoucherType(RpConstants.RECEIPT_VOUCHER);

					Voucher voucher = new Voucher();

					if (gfAmt > 0) {
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.GF_AC));	//diksha
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("C");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-" + gfAmt);
						vouchers.add(voucher);
						voucher = null;
					}
					if (sfAmt > 0) {
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.SF_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("C");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-" + sfAmt);
						vouchers.add(voucher);
						voucher = null;
					}
					if (shAmt > 0) {
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.SHORT_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("C");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-" + shAmt);
						vouchers.add(voucher);
						voucher = null;
					}
					if (clAmt > 0) {
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.RECOVERY_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("C");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-" + clAmt);
						vouchers.add(voucher);
						voucher = null;
					}

					if (realisationAmount < appropriatedAmount) { // short
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.SHORT_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("D");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs((appropriatedAmount - realisationAmount)
								+ "");
						vouchers.add(voucher);
						voucher = null;
					} else if (realisationAmount > appropriatedAmount) { // excess
						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.EXCESS_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("D");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-"
								+ (realisationAmount - appropriatedAmount));
						vouchers.add(voucher);
						voucher = null;
					}

					if (!shortId.equals("")) {
						narration = narration + " Short Id: " + shortId;
					}
					if (excessId != 0) {
						narration = narration + " Excess Id: " + excessId;
					}
					voucherDetail.setNarration(narration);
					voucherDetail.setVouchers(vouchers);

					insertVoucherDetails(voucherDetail,
							demandAdvice.getUserId(), connection);
					vouchers.clear();
				} else if (reallocation) {
					VoucherDetail voucherDetail = new VoucherDetail();
					vouchers.clear();

					Voucher voucher = new Voucher();
					if (realisationAmount < appropriatedAmount) { // short
						voucherDetail.setBankGLCode(accCodes
								.getProperty(RpConstants.GF_AC));
						voucherDetail.setBankGLName("");
						voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
						voucherDetail
								.setAmount(0 - (appropriatedAmount - realisationAmount));
						voucherDetail
								.setVoucherType(RpConstants.JOURNAL_VOUCHER);

						/*
						 * voucher = new Voucher(); voucher.setAcCode("GF A/c");
						 * voucher.setPaidTo("CGTSI");
						 * voucher.setDebitOrCredit("C");
						 * voucher.setInstrumentDate
						 * (dateFormat.format(paymentDetails
						 * .getInstrumentDate()));
						 * voucher.setInstrumentNo(paymentDetails
						 * .getInstrumentNo());
						 * voucher.setInstrumentType(paymentDetails
						 * .getInstrumentType());
						 * voucher.setAmountInRs("-"+(appropriatedAmount
						 * -realisationAmount)); vouchers.add(voucher);
						 * voucher=null;
						 */

						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.SHORT_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("D");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs((appropriatedAmount - realisationAmount)
								+ "");
						vouchers.add(voucher);
						voucher = null;
					} else if (realisationAmount > appropriatedAmount) { // excess
						voucherDetail.setBankGLCode(accCodes
								.getProperty(RpConstants.GF_AC));
						voucherDetail.setBankGLName("");
						voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
						voucherDetail
								.setAmount((realisationAmount - appropriatedAmount));
						voucherDetail
								.setVoucherType(RpConstants.JOURNAL_VOUCHER);

						/*
						 * voucher = new Voucher(); voucher.setAcCode("GF A/c");
						 * voucher.setPaidTo("CGTSI");
						 * voucher.setDebitOrCredit("D");
						 * voucher.setInstrumentDate
						 * (dateFormat.format(paymentDetails
						 * .getInstrumentDate()));
						 * voucher.setInstrumentNo(paymentDetails
						 * .getInstrumentNo());
						 * voucher.setInstrumentType(paymentDetails
						 * .getInstrumentType());
						 * voucher.setAmountInRs((realisationAmount
						 * -appropriatedAmount)+""); vouchers.add(voucher);
						 * voucher=null;
						 */

						voucher = new Voucher();
						voucher.setAcCode(accCodes
								.getProperty(RpConstants.EXCESS_AC));
						voucher.setPaidTo("CGTSI");
						voucher.setDebitOrCredit("C");
						voucher.setInstrumentDate(dateFormat
								.format(paymentDetails.getInstrumentDate()));
						voucher.setInstrumentNo(paymentDetails
								.getInstrumentNo());
						voucher.setInstrumentType(paymentDetails
								.getInstrumentType());
						voucher.setAmountInRs("-"
								+ (realisationAmount - appropriatedAmount));
						vouchers.add(voucher);
						voucher = null;
					} else {
						voucherDetail.setBankGLCode(accCodes
								.getProperty(RpConstants.GF_AC));
						voucherDetail.setBankGLName("");
						voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
						voucherDetail.setAmount(realisationAmount);
						voucherDetail
								.setVoucherType(RpConstants.JOURNAL_VOUCHER);
					}

					if (!shortId.equals("")) {
						narration = narration + " Short Id: " + shortId;
					}
					if (excessId != 0) {
						narration = narration + " Excess Id: " + excessId;
					}

					voucherDetail.setNarration(narration);
					voucherDetail.setVouchers(vouchers);

					insertVoucherDetails(voucherDetail,
							demandAdvice.getUserId(), connection);
					vouchers.clear();
				}
			}

			String emailMessage = "";
			String bankId = "";
			String zoneId = "";
			String branchId = "";
			String mliId = "";
			int slNo = 1;
			for (int i = 0; i < sizeOfDemandAdvices; ++i) {
				Log.log(Log.DEBUG, className, "appropriatePayment sukumar 6",
						"Retrieving Demand Advice object");
				demandAdvice = (DemandAdvice) demandAdvices.get(i);
				Log.log(Log.DEBUG, className, "appropriatePayment sukumar7",
						"Executing rpDAO.appropriateAmount");
				appropriateAmount(demandAdvice, connection);
				demandAdvice.setAppropriatedDate(new java.util.Date());

				String danType = demandAdvice.getDanNo().substring(0, 2);
				if (demandAdvice.getAllocated().equals("Y")) {
					if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_CGDAN)
							|| "GF".equalsIgnoreCase(danType)) {// ADD GF ALSO
						ApplicationDAO applicationDAO = new ApplicationDAO();
						RpProcessor rpProcessor = new RpProcessor();
						String tempMLIId = demandAdvice.getBankId()
								+ demandAdvice.getZoneId()
								+ demandAdvice.getBranchId();
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar 8", "mli id "
										+ tempMLIId);
						System.out
								.println("PATH in RpDAOappropriate tempMLIId = "
										+ tempMLIId
										+ "  demandAdvice.getCgpan() = "
										+ demandAdvice.getCgpan());
						Application application = applicationDAO
								.getAppForCgpan(tempMLIId,
										demandAdvice.getCgpan());
						// System.out.println("tempMLIId:"+tempMLIId);
						// double guaranteeAmount = 0.0;
						  System.out.println(" guaranteeAmount S " );
						double guaranteeAmount[] = rpProcessor
								.calculateGuaranteeFee(application);
                         System.out.println(" guaranteeAmount E "+guaranteeAmount[4]);
						Log.log(Log.DEBUG, className,
								"appropriatePayment Sukumar 9",
								"Guarantee Amount = " + guaranteeAmount[4]);
						GuaranteeFee gFee = new GuaranteeFee();
						gFee.setCgpan(demandAdvice.getCgpan());
						System.out.println("demandAdvice.getCgpan():"
								+ demandAdvice.getCgpan());
						gFee.setGuaranteeAmount(guaranteeAmount[4]);
						System.out
								.println("guaranteeAmount:" + guaranteeAmount[4]);
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar 10",
								"Executing updateGuaranteeFee");
						// System.out.println("Executing updateGuaranteeFee");
						updateGFeeForAppropriation(demandAdvice,
								realisationDetail.getRealisationDate(),
								connection);
						Log.log(Log.DEBUG, className, "appropriatePayment",
								"after Executing updateGuaranteeFee");
						// System.out.println("after Executing updateGuaranteeFee");
					}// end inner if
					Log.log(Log.DEBUG, className, "appropriatePayment ",
							"Executing updateRealizationDate");
					// System.out.println("Executing updateRealizationDate");
					updateRealizationDate(demandAdvice,
							realisationDetail.getRealisationDate(), connection);
					// System.out.println("after Executing updateRealizationDate");
					Log.log(Log.DEBUG, className,
							"appropriatePayment sukumar12",
							"after Executing updateRealizationDate");
				}// end outer if
				Log.log(Log.DEBUG, className,
						"appropriatePayment sukumar test1",
						"after Executing updateRealizationDate");

				Application application = new ApplicationProcessor()
						.getPartApplication(null, demandAdvice.getCgpan(), "");
				Log.log(Log.DEBUG, className,
						"appropriatePayment sukumar test2",
						"after Executing updateRealizationDate");

				mliId = application.getMliID();
				if (demandAdvice.getAllocated().equals("Y")) {

					Log.log(Log.DEBUG, className,
							"appropriatePayment sukumar test4",
							"after Executing updateRealizationDate");

					if (demandAdvice.getDanNo().substring(0, 2)
							.equalsIgnoreCase(RpConstants.DAN_TYPE_CGDAN)) {
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar test5",
								"after Executing updateRealizationDate");

						// Start Code by Path on 9Oct06 to send CGDAN in message
						emailMessage = emailMessage + "<BR>" + slNo + ". "
								+ "CGPAN: " + demandAdvice.getCgpan();

						// emailMessage = emailMessage + "<BR>" + slNo + ". " +
						// " Please Check Dan No: " + demandAdvice.getDanNo()
						// +" For Payment Detail.";
						// emailMessage = emailMessage + "<BR>" + slNo + ". " +
						// " CGPAN: " + demandAdvice.getCgpan() +
						// " Guarantee Cover Amount: " +
						// application.getApprovedAmount();
						// End Code by Path on 9Oct06 to send CGDAN in message
					} else {
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar test6",
								"after Executing updateRealizationDate");

						// Commented code by path on 3jan2007 for appropriation
						// emailMessage = emailMessage + "<BR>" + slNo + ". " +
						// "Dan No: " + demandAdvice.getDanNo() +
						// " has been appropriated.";
						emailMessage = emailMessage + "<BR>" + slNo;
					}
					slNo++;
				}
				if (!reallocation) {
					Log.log(Log.DEBUG, className, "test7",
							"after Executing updateRealizationDate");
					Log.log(Log.DEBUG, className, "test7a",
							"after Executing updateRealizationDate"
									+ demandAdvice.getAllocated());
					Log.log(Log.DEBUG, className, "test7b",
							"after Executing updateRealizationDate"
									+ demandAdvice.getDanNo());
					Log.log(Log.DEBUG, className, "test7c",
							"after Executing updateRealizationDate"
									+ RpConstants.DAN_TYPE_CGDAN);

					if (demandAdvice.getAllocated().equals("Y")
							&& demandAdvice
									.getDanNo()
									.substring(0, 2)
									.equalsIgnoreCase(
											RpConstants.DAN_TYPE_CGDAN)) {
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar test8",
								"after Executing updateRealizationDate");

						// Commented and Added code by Path on 9Oct06
						emailMessage = emailMessage + " GI Dt: "
								+ realisationDetail.getRealisationDate();

						// emailMessage = emailMessage +
						// " Guarantee Issue Date: " +
						// realisationDetail.getRealisationDate() +
						// " Payment ID: " + demandAdvice.getPaymentId() +
						// " Instrument No: " + paymentDetails.getInstrumentNo()
						// +
						// " Instrument Date: " +
						// dateFormat.format(paymentDetails.getInstrumentDate());
					}
				} else if (reallocation
						&& demandAdvice.getAllocated().equals("N")) {
					Log.log(Log.DEBUG, className,
							"appropriatePayment sukumar test9",
							"after Executing updateRealizationDate");

					if (demandAdvice.getDanNo().substring(0, 2)
							.equalsIgnoreCase(RpConstants.DAN_TYPE_CGDAN)) {
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar test10",
								"after Executing updateRealizationDate");

						emailMessage = emailMessage
								+ " Guarantee Issue Revoke Date: "
								+ dateFormat.format(new java.util.Date());

					} else {
						Log.log(Log.DEBUG, className,
								"appropriatePayment sukumar test11",
								"after Executing updateRealizationDate");

						emailMessage = emailMessage + " Payment for "
								+ demandAdvice.getDanNo()
								+ " has been unallocated";

					}
				}

			}// end for

			Log.log(Log.DEBUG, className, "appropriatePayment test3",
					"Exception getting user details for the MLI. Error=");

			// System.out.println("emailMessage =>>> "+emailMessage);
			ArrayList users = new ArrayList();
			RegistrationDAO registrationDAO = new RegistrationDAO();
			MLIInfo mliInfo = new MLIInfo();
			User user = adminDAO.getUserInfo(demandAdvice.getUserId());
			try {
				// users = rpDAO.getActiveBankUsers(mliId) ;
				users = administrator.getAllUsers(mliId);
			} catch (NoUserFoundException exception) {
				
				System.out.println("+ exception.getMessage() 7627"+ exception.getMessage());
				Log.log(Log.DEBUG, className, "appropriatePayment",
						"Exception getting user details for the MLI. Error="
								+ exception.getMessage());
			} catch (DatabaseException exception) {
				 
				System.out.println("+ exception.getMessage() 7633 :"+  exception.getMessage());
				Log.log(Log.DEBUG, className, "appropriatePayment",
						"Exception getting user details for the MLI. Error="
								+ exception.getMessage());
			}

			Log.log(Log.DEBUG, className, "appropriatePayment Sukumar 13",
					"Before getting member details, bankId = " + bankId
							+ ", zoneId = " + zoneId + ", branchId = "
							+ branchId);
			bankId = mliId.substring(0, 4);
			zoneId = mliId.substring(4, 8);
			branchId = mliId.substring(8, 12);
			mliInfo = registrationDAO
					.getMemberDetails(bankId, zoneId, branchId);
			Log.log(Log.DEBUG, className, "appropriatePayment Sukumar14",
					"After getting member details");

			String mailPrivelege = mliInfo.getMail();
			String emailPrivelege = mliInfo.getEmail();
			String hardCopyPrivelege = mliInfo.getHardCopy();

			// Log.log(Log.DEBUG,
			// className,"appropriatePayment","Getting Email Id for MLI id completed")
			// ;
			int userSize = users.size();
			ArrayList emailIds = new ArrayList();
			ArrayList mailIds = new ArrayList();

			for (int j = 0; j < userSize; j++) {
				User mailUser = (User) users.get(j);
				emailIds.add(mailUser.getUserId()); // mail Ids
				mailIds.add(mailUser.getEmailId()); // e-mail Ids
				// emailIds.add(mailUser.getEmailId()) ;
				// Log.log(Log.DEBUG,
				// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
				// ;
				// Log.log(Log.DEBUG,
				// className,"appropriatePayment","Member Id"+mliId+", User mail "+mailUser.getUserId())
				// ;
				// Log.log(Log.DEBUG,
				// className,"appropriatePayment","Member Id"+mliId+", User email "+mailUser.getEmailId())
				// ;
			}

			// sending mail
			if (emailIds != null) {
				// Log.log(Log.DEBUG,
				// className,"appropriatePayment","Before instantiating message")
				// ;
				String subject = "Guarantee Cover Details";
				// Log.log(Log.DEBUG,
				// className,"appropriatePayment","Subject = "+subject) ;
				// Log.log(Log.DEBUG,
				// className,"appropriatePayment","Email Message = "+emailMessage)
				// ;
				Message message = new Message(emailIds, null, null, subject,
						emailMessage);
				message.setFrom(user.getUserId());
				// try
				// {
				// mailer.sendEmail(message);
				// Commented by path on 5jan2007 due to performence issue
				// administrator.sendMail(message);
				// ///////////////////////////////////////////////////////
				/*
				 * }catch(MailerException mailerException) {
				 * Log.log(Log.WARNING,
				 * className,methodName,"Exception sending Mail. Error="
				 * +mailerException.getMessage()) ; }
				 */// administrator(message) ;
					// Log.log(Log.DEBUG, className,
					// "appropriatePayment","After instantiating message") ;
					// mailStatus = mailer.sendEmail(message) ;
			}
			connection.commit();
		} catch (DatabaseException exp) {
			try {
				connection.rollback();
			} catch (SQLException sqlexp) {
				
				System.out.println("sqlexp.getMessage()  7710 "+sqlexp.getMessage());
				throw new DatabaseException(sqlexp.getMessage());
			}
			throw exp;
		} catch (SQLException sqlexp) {
			
			
			System.out.println("sqlexp.getMessage()  7715 "+sqlexp.getMessage());
			
			sqlexp.printStackTrace();
	
			throw new DatabaseException(sqlexp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return (realisationAmount - appropriatedAmount);
	}

	/**
	 * This method updates the realisation date and realisation amount for the
	 * payment made by the member.
	 */
	public void updatePaymentDetails(String paymentId, Connection connection)
			throws DatabaseException {

		CallableStatement stmt = null;
		try {
			stmt = connection.prepareCall("{?=call funcDEInsPaymentDtl(?,?)}");
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.setString(2, paymentId);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			stmt.execute();

			int status = stmt.getInt(1);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(3);
				System.out.println( "err 7751"+ err);
				Log.log(Log.INFO, className, "updatePaymentDetails",
						"error from funcDEInsPaymentDtl " + err);

				stmt.close();
				stmt = null;
				System.out.println( "err 7757 "+ err);
				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;

			Log.log(Log.INFO, className, "updatePaymentDetails",
					"funcDEInsPaymentDtl executed successfully");
		} catch (SQLException exp) {
			
			System.out.println(" exp "+ exp.getMessage());
			Log.log(Log.INFO, className, "updatePaymentDetails",
					"sql exception " + exp.getMessage());
			throw new DatabaseException(exp.getMessage());
		}
	}

	/* added by sukumar@path on 14-05-2008 */

	/**
	 * 
	 * @param paymentId
	 * @param connection
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void deAllocatePayments(String paymentId, String userId)
			throws DatabaseException {

		Connection connection = null;
		CallableStatement stmt = null;
		// System.out.println("deAllocatePayments entered");
		try {
			connection = DBConnection.getConnection();
			// System.out.println("paymentId:"+paymentId);
			stmt = connection.prepareCall("{?=call funcCancAllocation(?,?,?)}");
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);

			stmt.setString(2, paymentId);
			stmt.setString(3, userId);
			stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			stmt.execute();

			int status = stmt.getInt(1);
			// System.out.println("status:"+status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(4);
				// System.out.println("error:"+err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;

			Log.log(Log.INFO, className, "deAllocatePayments",
					"funcCancAllocation executed successfully");
		} catch (SQLException exp) {
			// System.out.println("sql exception " +exp.getMessage());
			Log.log(Log.INFO, className, "deAllocatePayments", "sql exception "
					+ exp.getMessage());
			throw new DatabaseException(exp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
	}

	/* ------------------------------------- */

	/**
	 * This method updates the realisation date and realisation amount for the
	 * payment made by the member.
	 * 
	 * @param PaymentId
	 * @param RealisationDate
	 * @param RealisationAmount
	 * @return boolean
	 * @roseuid 39A0E22902FD
	 */
	public boolean updateRealisationDetails(String paymentId,
			Date realisationDate, double realisationAmount)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement updateRealisationDetailsStmt = null;

		int updateStatus = 0;
		boolean updateRealisationDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updateRealisationDetailsStmt = connection
					.prepareCall("{call <<updatRelsnDetails>>(?,?,?)}");
			updateRealisationDetailsStmt.setString(1, paymentId);
			updateRealisationDetailsStmt.setDouble(2, realisationAmount);

			utilDate = realisationDate;
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateRealisationDetailsStmt.setDate(5, sqlDate);

			updateRealisationDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updateRealisationDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateRealisationDetailsStmt
					.getObject(6).toString());

			if (updateStatus == 0) {
				updateRealisationDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateRealisationDetailsStatus = false;
			}
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateRealisationDetailsStatus;
	}

	// method overriding done for appropriatepayments

	public boolean updateRealisationDetails(
			RealisationDetail realisationDetail, Connection connection)
			throws DatabaseException {
		// Connection connection = null ;
		CallableStatement updateRealisationDetailsStmt = null;

		int updateStatus = 0;
		boolean updateRealisationDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			// System.out.println("ritesh in rpdao updateRealisationDetails ");
			// connection = DBConnection.getConnection() ;
			updateRealisationDetailsStmt = connection
					.prepareCall("{?=call funcUpdatePaymentForAppr(?,?,?,?)}");
			updateRealisationDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			Log.log(Log.INFO, className, "updateRealisationDetails",
					"PaymentId : " + realisationDetail.getPaymentId());
			updateRealisationDetailsStmt.setString(2,
					realisationDetail.getPaymentId());

			Log.log(Log.INFO,
					className,
					"updateRealisationDetails",
					"Realisation Amount : "
							+ realisationDetail.getRealisationAmount());
			updateRealisationDetailsStmt.setDouble(3,
					realisationDetail.getRealisationAmount());

			Log.log(Log.INFO,
					className,
					"updateRealisationDetails",
					"Realisation Date : "
							+ realisationDetail.getRealisationDate());
			// updateRealisationDetailsStmt.setDate(4,java.sql.Date.valueOf(DateHelper.stringToSQLdate(
			// (realisationDetail.getRealisationDate().toString()))));
			updateRealisationDetailsStmt.setDate(4, new java.sql.Date(
					realisationDetail.getRealisationDate().getTime()));
			updateRealisationDetailsStmt.registerOutParameter(5,
					java.sql.Types.VARCHAR);
			updateRealisationDetailsStmt.executeQuery();
			updateStatus = updateRealisationDetailsStmt.getInt(1);

			if (updateStatus == 0) {
				updateRealisationDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateRealisationDetailsStatus = false;
				String err = updateRealisationDetailsStmt.getString(5);
				
				System.out.println("err "+err);

				updateRealisationDetailsStmt.close();
				updateRealisationDetailsStmt = null;

				
				System.out.println("err "+err);
				throw new DatabaseException(err);
			}

			updateRealisationDetailsStmt.close();
			updateRealisationDetailsStmt = null;

		} catch (Exception exception) {
			
			System.out.println(" exception.getMessage()"+exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */
		return updateRealisationDetailsStatus;
	}

	/**
	 * This method is used to appropriate the payments received to the
	 * appropriate CGPAN. The method passes the payment id, DAN id, CGPAN, flag
	 * to indicate whether payment is appropriated and the reason if the payment
	 * is not appropriated.
	 * 
	 * @param DANNo
	 * @param CGPAN
	 * @param PaymentId
	 * @param AppropriatedFlag
	 * @param Reason
	 * @return boolean
	 * @roseuid 39A0E45F007D
	 */
	public// changed to demand advice parameter from dan,payid,appropflag,
	// reason..
	boolean appropriateAmount(DemandAdvice demandAdvice, Connection connection)
			throws DatabaseException {
		// Connection connection = null ;
		CallableStatement appropriateAmountStmt = null;

		int updateStatus = 0;
		boolean appropriateAmountStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			// connection = DBConnection.getConnection() ;
			appropriateAmountStmt = connection
					.prepareCall("{?=call funcUpdateAppropriationDetail(?,?,?,?,?,?,?,?,?)}");
			appropriateAmountStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			appropriateAmountStmt.setString(2, demandAdvice.getPaymentId());
			// System.out.println("demandAdvice.getPaymentId():"+demandAdvice.getPaymentId());
			Log.log(Log.INFO, className, "appropriateAmount", "dan no "
					+ demandAdvice.getDanNo());
			// System.out.println("appropriateAmount"+ "dan no " +
			// demandAdvice.getDanNo());
			appropriateAmountStmt.setString(3, demandAdvice.getDanNo());
			Log.log(Log.INFO, className, "appropriateAmount", "cgpan "
					+ demandAdvice.getCgpan());
			// System.out.println("appropriateAmount"+ "cgpan " +
			// demandAdvice.getCgpan());
			appropriateAmountStmt.setString(4, demandAdvice.getCgpan());
			Log.log(Log.INFO, className, "appropriateAmount", "allocated flag "
					+ demandAdvice.getAllocated());
			// System.out.println("appropriateAmount"+ "allocated flag " +
			// demandAdvice.getAllocated());
			appropriateAmountStmt.setString(5, demandAdvice.getAllocated());
			// System.out.println("appropriateAmount"+ "appropriated flag " +
			// demandAdvice.getAppropriated());
			Log.log(Log.INFO, className, "appropriateAmount",
					"appropriated flag " + demandAdvice.getAppropriated());
			appropriateAmountStmt.setString(6, demandAdvice.getAppropriated());
			appropriateAmountStmt.setString(7, demandAdvice.getUserId());
			// System.out.println(demandAdvice.getUserId());
			appropriateAmountStmt.setString(8, demandAdvice.getReason());
			// System.out.println("demandAdvice.getReason():"+demandAdvice.getReason());
			appropriateAmountStmt.setString(9, demandAdvice.getUserId());
			appropriateAmountStmt.registerOutParameter(10, Types.VARCHAR);

			appropriateAmountStmt.executeQuery();

			updateStatus = appropriateAmountStmt.getInt(1);
			// System.out.println("appropriateAmount"+ "status " +
			// updateStatus);
			Log.log(Log.INFO, className, "appropriateAmount", "status "
					+ updateStatus);
			if (updateStatus == 0) {
				appropriateAmountStatus = true;
			} else if (updateStatus == 1) {
				appropriateAmountStatus = false;
				String error = appropriateAmountStmt.getString(10);
			System.out.println("error:"+error);
				appropriateAmountStmt.close();
				appropriateAmountStmt = null;
				System.out.println("error 8044:"+error);
				throw new DatabaseException(error);
			}

			appropriateAmountStmt.close();
			appropriateAmountStmt = null;

		} catch (Exception exception) {
			System.out.println(" exception.getMessage() "+exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */
		return appropriateAmountStatus;
	}

	/**
	 * This method updates the short amount details to the database. The
	 * parameters passed to this method are payment id and short amount.
	 * 
	 * @param PaymentId
	 * @param ShortAmount
	 * @return boolean
	 * @roseuid 39A13A9900CB
	 */
	public boolean updateShortAmountDetails(String paymentId, double shortAmount)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement updateShortAmountDetailsStmt = null;

		int updateStatus = 0;
		boolean updateShortAmountDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updateShortAmountDetailsStmt = connection
					.prepareCall("{?=call funcUpdateShortAmount(?,?,?)}");
			updateShortAmountDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			updateShortAmountDetailsStmt.setString(1, paymentId);
			updateShortAmountDetailsStmt.setDouble(2, shortAmount);

			updateShortAmountDetailsStmt.registerOutParameter(6,
					java.sql.Types.VARCHAR);
			updateShortAmountDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateShortAmountDetailsStmt
					.getObject(6).toString());

			if (updateStatus == 0) {
				updateShortAmountDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateShortAmountDetailsStatus = false;
				String err = updateShortAmountDetailsStmt.getString(6);

				updateShortAmountDetailsStmt.close();
				updateShortAmountDetailsStmt = null;

				throw new DatabaseException(err);
			}
			updateShortAmountDetailsStmt.close();
			updateShortAmountDetailsStmt = null;
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateShortAmountDetailsStatus;
	}

	/**
	 * This method updates the excess amount details to the database along with
	 * the payment id and the excess amount.
	 * 
	 * @param PaymentId
	 * @param ExcessAmount
	 *            *****Method name also changed****************
	 * @return boolean *****to updateExcessAmountDetails()************
	 * @roseuid 39A13C810000
	 */
	public// **********Method params changed from pay Id and Excess
	// Amount**********
	// to DemandAdvice and Excess amount..
	int updateExcessAmountDetails(DemandAdvice demandAdvice,
			double excessAmount, Connection connection)
			throws DatabaseException {
		// Connection connection = null ;
		CallableStatement updateExcessAmountStmt = null;

		int updateStatus = 0;
		boolean updateExcessAmountStatus = false;
		int excessId = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		boolean newConn = false;

		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			updateExcessAmountStmt = connection
					.prepareCall("{?=call funcUpdateExcessAmount(?,?,?,?,?,?,?)}");

			updateExcessAmountStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			updateExcessAmountStmt.setString(2, demandAdvice.getBankId());
			updateExcessAmountStmt.setString(3, demandAdvice.getZoneId());
			updateExcessAmountStmt.setString(4, demandAdvice.getBranchId());
			updateExcessAmountStmt.setString(5, demandAdvice.getPaymentId());
			updateExcessAmountStmt.setDouble(6, excessAmount);
			updateExcessAmountStmt.registerOutParameter(7,
					java.sql.Types.INTEGER);
			updateExcessAmountStmt.registerOutParameter(8,
					java.sql.Types.VARCHAR);
			updateExcessAmountStmt.executeQuery();
			updateStatus = updateExcessAmountStmt.getInt(1);

			if (updateStatus == 0) {
				updateExcessAmountStatus = true;
				excessId = updateExcessAmountStmt.getInt(7);
			} else if (updateStatus == 1) {
				updateExcessAmountStatus = false;
				String err = updateExcessAmountStmt.getString(8);
				updateExcessAmountStmt.close();
				updateExcessAmountStmt = null;
				System.out.println(" err "+err);
				throw new DatabaseException(err);
			}
			updateExcessAmountStmt.close();
			updateExcessAmountStmt = null;
		} catch (Exception exception) {
			
			System.out.println("8180 "+exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		return excessId;
	}

	/**
	 * This method Returns an arraylist of DemandAdvice object for all the DAN
	 * details for the specified CGPAN which has been generated.
	 * 
	 * @param CGPAN
	 * @return java.util.ArrayList
	 * @roseuid 39A5295503B9
	 */
	public ArrayList getGeneratedDANs(Application application)
			throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "getGeneratedDANs", "Entered");

		DemandAdvice demandAdvice = null;
		Connection connection = null;
		CallableStatement getGeneratedDANsStmt = null;
		ArrayList generatedDANs = new ArrayList();
		ResultSet resultSet = null;
		try {
			String exception = "";

			connection = DBConnection.getConnection();

			Log.log(Log.DEBUG, "RpDAO", "getGeneratedDANs", "CGPAN "
					+ application.getCgpan());

			getGeneratedDANsStmt = connection
					.prepareCall("{?=call packGetGeneratedDans.funcGetGeneratedDans(?,?,?,?)}");

			getGeneratedDANsStmt.registerOutParameter(1, Types.INTEGER);

			getGeneratedDANsStmt.setString(2, application.getCgpan());
			getGeneratedDANsStmt.registerOutParameter(3, Constants.CURSOR);
			getGeneratedDANsStmt.registerOutParameter(4, Constants.CURSOR);
			getGeneratedDANsStmt.registerOutParameter(5, Types.VARCHAR);

			getGeneratedDANsStmt.execute();

			int errorCode = getGeneratedDANsStmt.getInt(1);
			String error = getGeneratedDANsStmt.getString(5);

			Log.log(Log.DEBUG, "RpDAO", "getGeneratedDANs",
					"Error Code and Error " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {

				Log.log(Log.ERROR, "RpDAO", "getGeneratedDANs", error);

				getGeneratedDANsStmt.close();
				getGeneratedDANsStmt = null;

				throw new DatabaseException(error);
			}

			resultSet = (ResultSet) getGeneratedDANsStmt.getObject(3);
			// doubt dd whether each element has to be set.

			while (resultSet.next()) {

				demandAdvice = new DemandAdvice();

				demandAdvice.setDanNo(resultSet.getString(1));
				demandAdvice.setCgpan(resultSet.getString(2));
				demandAdvice.setAmountRaised(resultSet.getDouble(3));
				demandAdvice.setPenalty(resultSet.getDouble(4));
				demandAdvice.setAllocated(resultSet.getString(5));
				demandAdvice.setReason(resultSet.getString(6));
				demandAdvice.setAppropriated(resultSet.getString(8));
				demandAdvice.setDanType(resultSet.getString(9));
				demandAdvice.setBankId(resultSet.getString(10));
				demandAdvice.setZoneId(resultSet.getString(11));
				demandAdvice.setBranchId(resultSet.getString(12));
				demandAdvice.setPaymentId(resultSet.getString(13));

				generatedDANs.add(demandAdvice);
			}

			resultSet.close();
			resultSet = null;

			resultSet = (ResultSet) getGeneratedDANsStmt.getObject(4);
			// doubt dd whether each element has to be set.

			while (resultSet.next()) {

				demandAdvice = new DemandAdvice();

				demandAdvice.setDanNo(resultSet.getString(1));
				demandAdvice.setCgpan(resultSet.getString(2));
				demandAdvice.setAmountRaised(resultSet.getDouble(3));
				demandAdvice.setPenalty(resultSet.getDouble(4));
				demandAdvice.setAllocated(resultSet.getString(5));
				demandAdvice.setReason(resultSet.getString(6));
				demandAdvice.setAppropriated(resultSet.getString(8));
				demandAdvice.setDanType(resultSet.getString(9));
				demandAdvice.setBankId(resultSet.getString(10));
				demandAdvice.setZoneId(resultSet.getString(11));
				demandAdvice.setBranchId(resultSet.getString(12));
				demandAdvice.setPaymentId(resultSet.getString(13));

				for (int x = 0; x < generatedDANs.size(); x++) {
					DemandAdvice temp = (DemandAdvice) generatedDANs.get(x);
					if (temp.getDanNo().equals(demandAdvice.getDanNo())) {
						generatedDANs.set(x, demandAdvice);
					}
					temp = null;
				}
			}

			resultSet.close();
			resultSet = null;

			getGeneratedDANsStmt.close();
			getGeneratedDANsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getGeneratedDANs",
					exception.getMessage());
			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getGeneratedDANs", "Exited");
		return generatedDANs;
	}

	/**
	 * This method updates modified CGDAN details to the database. The
	 * parameters passed to this method are DAN id, old DAN id and the
	 * GuaranteeFee object.
	 * 
	 * @param DANNo
	 * @param oldDANId
	 * @param GuaranteeFee
	 * @return boolean
	 * @roseuid 39A536280213
	 */
	public// method changed to demandAdvice, oldcgdanid*************
	boolean updateCGDANDetails(String cgpan, User user)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement updateCGDANDetailsStmt = null;

		int updateStatus = 0;
		boolean updateCGDANDetailsStatus = false;

		try {
			connection = DBConnection.getConnection();
			updateCGDANDetailsStmt = connection
					.prepareCall("{?=call funcSetNullAppGuaranteeFee(?,?,?)}");
			updateCGDANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			updateCGDANDetailsStmt.setString(2, cgpan);
			updateCGDANDetailsStmt.setString(3, user.getUserId());
			updateCGDANDetailsStmt.registerOutParameter(4, Types.VARCHAR);
			updateCGDANDetailsStmt.executeQuery();
			updateStatus = updateCGDANDetailsStmt.getInt(1);

			if (updateStatus == 0) {
				updateCGDANDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateCGDANDetailsStatus = false;
				String err = updateCGDANDetailsStmt.getString(4);
				updateCGDANDetailsStmt.close();
				updateCGDANDetailsStmt = null;
				throw new DatabaseException(err);
			}
			updateCGDANDetailsStmt.close();
			updateCGDANDetailsStmt = null;
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateCGDANDetailsStatus;
	}

	/**
	 * Gets the Cumulative guarantee cover for the MLI as of date. The
	 * cumulative guarantee cover for the MLI can be obtained from the given
	 * CGPAN value.
	 * 
	 * @param CGPAN
	 * @return Double
	 * @roseuid 39A53A8901F4
	 */
	public double getCumulativeGuaranteeCover(String cgpan)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement getCumulativeGuaranteeCoverStmt = null;
		ResultSet resultSet = null;
		double cumulativeGuaranteeCover = 0;

		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getCumulativeGuaranteeCoverStmt = connection
					.prepareCall("{?=call <<get cum claims>>(?,?)}");
			getCumulativeGuaranteeCoverStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getCumulativeGuaranteeCoverStmt.setString(1, cgpan);

			getCumulativeGuaranteeCoverStmt.registerOutParameter(2,
					Constants.CURSOR);
			getCumulativeGuaranteeCoverStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);

			getCumulativeGuaranteeCoverStmt.execute();
			resultSet = (ResultSet) getCumulativeGuaranteeCoverStmt
					.getObject(2);
			exception = getCumulativeGuaranteeCoverStmt.getString(3);

			// dd doubt whether list of gf obj or ....

			while (resultSet.next()) {
				cumulativeGuaranteeCover = resultSet.getDouble(1);
			}

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return cumulativeGuaranteeCover;

	}

	/**
	 * This method will get the guarantee fee rate from the sub-scheme table,
	 * specific to the MLI.
	 * 
	 * @param CGPAN
	 * @return Double
	 * @roseuid 39A53AFF02EE
	 */
	public double getModerationFactor(String cgpan) throws DatabaseException {
		double moderationFactor = 0;
		Connection connection = null;
		CallableStatement getModerationFactorStmt = null;
		ResultSet resultSet = null;

		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getModerationFactorStmt = connection
					.prepareCall("{?=call <<get cum claims>>(?,?)}");
			getModerationFactorStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getModerationFactorStmt.setString(1, cgpan);

			getModerationFactorStmt.registerOutParameter(2, Constants.CURSOR);
			getModerationFactorStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);

			getModerationFactorStmt.execute();
			resultSet = (ResultSet) getModerationFactorStmt.getObject(2);
			exception = getModerationFactorStmt.getString(3);

			// dd doubt ....

			while (resultSet.next()) {
				moderationFactor = resultSet.getDouble(1);
			}

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return moderationFactor;
	}

	/**
	 * This method updates modified SFDAN details to the database. The
	 * parameters passed to this method are DAN id, old DAN id and the
	 * ServiceFee object.
	 * 
	 * @param DANNo
	 * @param oldDANId
	 * @param ServiceFee
	 * @return boolean
	 * @roseuid 39A554BB036B
	 */
	public boolean updateSFDANDetails(DemandAdvice demandAdvice, String oldDANId) {
		Connection connection = null;
		CallableStatement updateSFDANDetailsStmt = null;

		int updateStatus = 0;
		boolean updateSFDANDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updateSFDANDetailsStmt = connection
					.prepareCall("{call <<GuaranteeFee>>(?,?,?)}");

			updateSFDANDetailsStmt.setString(1, demandAdvice.getDanNo());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getDanType());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getBankId());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getZoneId());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getBranchId());
			updateSFDANDetailsStmt.setString(2, demandAdvice.getCgpan());

			// the oldcgdan id linked with new dan.
			updateSFDANDetailsStmt.setString(2, oldDANId);

			/* dd amount doubt */
			updateSFDANDetailsStmt.setDouble(2, demandAdvice.getAmountRaised());
			// updateSFDANDetailsStmt.setString(2,demandAdvice.CreatedBy()) ;
			updateSFDANDetailsStmt.setDouble(2, demandAdvice.getPenalty());
			/* *** */
			utilDate = demandAdvice.getDanGeneratedDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanDueDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanExpiryDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateSFDANDetailsStmt.setDate(5, sqlDate);

			updateSFDANDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updateSFDANDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateSFDANDetailsStmt.getObject(6)
					.toString());

			if (updateStatus == 0) {
				updateSFDANDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateSFDANDetailsStatus = false;
			}
		} catch (Exception exception) {
			// throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateSFDANDetailsStatus;
	}

	/**
	 * This method updates the recovery details made to a particular borrower to
	 * the database.
	 * 
	 * @param Recovery
	 * @return boolean
	 * @roseuid 39A605D602FD
	 */
	public boolean updateRecoveryDetails(Recovery recovery)
			throws DatabaseException {
		Connection connection = null;
		CallableStatement updateRecoveryDetailsStmt = null;

		int updateStatus = 0;
		boolean updateRecoveryDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updateRecoveryDetailsStmt = connection
					.prepareCall("{call <<updateSFDANDetails>>(?,?,?)}");
			updateRecoveryDetailsStmt.setString(1, recovery.getCgbid());
			updateRecoveryDetailsStmt.setString(2, recovery.getCgpan());
			updateRecoveryDetailsStmt.setString(3,
					recovery.getDetailsOfAssetSold());
			updateRecoveryDetailsStmt.setString(4,
					recovery.getDetailsOfAssetSold());
			updateRecoveryDetailsStmt.setString(4,
					recovery.getIsRecoveryByOTS());
			updateRecoveryDetailsStmt.setString(4, recovery.getRemarks());
			updateRecoveryDetailsStmt.setString(4, recovery.getRecoveryNo());
			updateRecoveryDetailsStmt.setDouble(4,
					recovery.getAmountRecovered());
			// updateRecoveryDetailsStmt.setDate(4,recovery.getDateOfRecovery())
			// ;

			utilDate = recovery.getDateOfRecovery();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateRecoveryDetailsStmt.setDate(5, sqlDate);

			updateRecoveryDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updateRecoveryDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateRecoveryDetailsStmt
					.getObject(6).toString());

			if (updateStatus == 0) {
				updateRecoveryDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateRecoveryDetailsStatus = false;
			}
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateRecoveryDetailsStatus;
	}

	/**
	 * This method updates the CLDAN details to the database. the DemandAdvice
	 * object is passed as an argument to this method.
	 * 
	 * @param DemandAdvice
	 * @return boolean
	 * @roseuid 39A608D003B9
	 */
	public boolean updateCLDANDetails(DemandAdvice demandAdvice) {
		Connection connection = null;
		CallableStatement updateCLDANDetailsStmt = null;

		int updateStatus = 0;
		boolean updateCLDANDetailsStatus = false;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			updateCLDANDetailsStmt = connection
					.prepareCall("{call <<updatExcessAmt>>(?,?,?)}");
			updateCLDANDetailsStmt.setString(1, demandAdvice.getDanNo());
			updateCLDANDetailsStmt.setString(2, demandAdvice.getDanType());
			updateCLDANDetailsStmt.setString(2, demandAdvice.getBankId());
			updateCLDANDetailsStmt.setString(2, demandAdvice.getZoneId());
			updateCLDANDetailsStmt.setString(2, demandAdvice.getBranchId());
			updateCLDANDetailsStmt.setString(2, demandAdvice.getCgpan());

			// dd doubt..
			updateCLDANDetailsStmt.setDouble(2, demandAdvice.getAmountRaised());
			// updateCLDANDetailsStmt.setString(2,demandAdvice.CreatedBy()) ;
			updateCLDANDetailsStmt.setDouble(2, demandAdvice.getPenalty());
			// **
			utilDate = demandAdvice.getDanGeneratedDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateCLDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanDueDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateCLDANDetailsStmt.setDate(5, sqlDate);

			utilDate = demandAdvice.getDanExpiryDate();
			sqlDate = java.sql.Date.valueOf(utilDate.toString());
			updateCLDANDetailsStmt.setDate(5, sqlDate);

			updateCLDANDetailsStmt.registerOutParameter(6,
					java.sql.Types.INTEGER);
			updateCLDANDetailsStmt.executeQuery();
			updateStatus = Integer.parseInt(updateCLDANDetailsStmt.getObject(6)
					.toString());

			updateCLDANDetailsStmt.close();
			updateCLDANDetailsStmt = null;

			if (updateStatus == 0) {
				updateCLDANDetailsStatus = true;
			} else if (updateStatus == 1) {
				updateCLDANDetailsStatus = false;
			}
		} catch (Exception exception) {
			// throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return updateCLDANDetailsStatus;
	}

	/**
	 * This method returns all CGPAN details that belongs to the particular
	 * CGDAN. The output will be an ArrayList of DemandAdvice object.
	 * 
	 * @param CGDAN
	 * @return java.util.ArrayList
	 * @roseuid 39AF57080157
	 */
	public ArrayList getCGPANsForDAN(String danNo) throws DatabaseException {
		AllocationDetail danApplication = null;
		Connection connection = null;
		ResultSet rsDemandAdvice = null;
		CallableStatement demandAdviceStmt;

		int getCgpanStatus = 0;
		String getDemandAdviceErr = "";

		ArrayList returnList = new ArrayList();
		ArrayList danDetails = new ArrayList();
		ArrayList allocatedDanDetails = new ArrayList();

		try {
			connection = DBConnection.getConnection();

			demandAdviceStmt = connection
					.prepareCall("{?= call packGetPANDtlforDAN.funcGetPANDtlforDAN(?,?,?,?)}");
			demandAdviceStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			demandAdviceStmt.setString(2, danNo);
			demandAdviceStmt.registerOutParameter(3, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(4, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			demandAdviceStmt.execute();

			getCgpanStatus = demandAdviceStmt.getInt(1);

			if (getCgpanStatus == 0) {
				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(3);

				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));
					danApplication.setFirstDisbursementDate(rsDemandAdvice
							.getDate(9));
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setGuarCoverAmount(rsDemandAdvice
							.getDouble(10));
					danApplication.setNewDanId(rsDemandAdvice.getString(11));

					danDetails.add(danApplication);
					danApplication = null;
				}
				rsDemandAdvice.close();
				rsDemandAdvice = null;

				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(4);
				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));
					danApplication.setFirstDisbursementDate(DateHelper
							.sqlToUtilDate(rsDemandAdvice.getDate(9)));
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setGuarCoverAmount(rsDemandAdvice
							.getDouble(10));
					danApplication.setNewDanId(rsDemandAdvice.getString(11));

					allocatedDanDetails.add(danApplication);
					danApplication = null;
				}

				rsDemandAdvice.close();
				rsDemandAdvice = null;
				demandAdviceStmt.close();
				demandAdviceStmt = null;

				returnList.add(danDetails);
				returnList.add(allocatedDanDetails);
			} else {
				getDemandAdviceErr = demandAdviceStmt.getString(5);
				throw new DatabaseException(getDemandAdviceErr);
			}
		} catch (Exception exception) {
			// System.out.println(exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return returnList;
	}

	/* Added by shyam */

	/**
	 * This method returns all CGPAN details that belongs to the particular
	 * CGDAN. The output will be an ArrayList of DemandAdvice object.
	 * 
	 * @param CGDAN
	 * @return java.util.ArrayList
	 * @roseuid 39AF57080157
	 */
	public ArrayList getSFCGPANsForDAN(String danNo) throws DatabaseException {
		AllocationDetail danApplication = null;
		Connection connection = null;
		ResultSet rsDemandAdvice = null;
		CallableStatement demandAdviceStmt;

		int getCgpanStatus = 0;
		String getDemandAdviceErr = "";

		ArrayList returnList = new ArrayList();
		ArrayList danDetails = new ArrayList();
		ArrayList allocatedDanDetails = new ArrayList();

		try {
			connection = DBConnection.getConnection();

			demandAdviceStmt = connection
					.prepareCall("{?= call packGetPANDtlforDAN.funcGetPANDtlforDAN(?,?,?,?)}");
			demandAdviceStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			demandAdviceStmt.setString(2, danNo);
			demandAdviceStmt.registerOutParameter(3, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(4, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			demandAdviceStmt.execute();

			getCgpanStatus = demandAdviceStmt.getInt(1);

			if (getCgpanStatus == 0) {
				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(3);

				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));
					danApplication.setFirstDisbursementDate(DateHelper
							.sqlToUtilDate(rsDemandAdvice.getDate(9)));
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setGuarCoverAmount(rsDemandAdvice
							.getDouble(10));
					danApplication.setNewDanId(rsDemandAdvice.getString(11));

					danDetails.add(danApplication);
					danApplication = null;
				}
				rsDemandAdvice.close();
				rsDemandAdvice = null;

				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(4);
				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));
					danApplication.setFirstDisbursementDate(DateHelper
							.sqlToUtilDate(rsDemandAdvice.getDate(9)));
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setGuarCoverAmount(rsDemandAdvice
							.getDouble(10));
					danApplication.setNewDanId(rsDemandAdvice.getString(11));

					allocatedDanDetails.add(danApplication);
					danApplication = null;
				}

				rsDemandAdvice.close();
				rsDemandAdvice = null;
				demandAdviceStmt.close();
				demandAdviceStmt = null;

				returnList.add(danDetails);
				returnList.add(allocatedDanDetails);
			} else {
				getDemandAdviceErr = demandAdviceStmt.getString(5);
				throw new DatabaseException(getDemandAdviceErr);
			}
		} catch (Exception exception) {
			// System.out.println(exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return returnList;
	}

	/**
	 * This method will update the DAN_WAIVED_FLAG of the DEMAND_ADVICE_INFO
	 * table to 'Y' for the given SHDAN.
	 * 
	 * @param SHDAN
	 * @return boolean
	 * @roseuid 39B222F2009C
	 */
	public void waiveShortDANs(String shdan) throws DatabaseException {
		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement waiveShortInfo = connection
					.prepareCall("{?=call funcinsertWaiveDetail(?,?)}");

			waiveShortInfo.registerOutParameter(1, Types.INTEGER);
			waiveShortInfo.registerOutParameter(3, Types.VARCHAR);
			waiveShortInfo.setString(2, shdan);
			Log.log(Log.DEBUG, "RpDAO", "waiveShortDANs", "waiveShortDAN :"
					+ shdan);

			waiveShortInfo.executeQuery();

			int waiveShortInfoValue = waiveShortInfo.getInt(1);
			Log.log(Log.DEBUG, "RpDAO", "waiveShortDANs",
					"waiveShortDANs result :" + waiveShortInfoValue);

			if (waiveShortInfoValue == Constants.FUNCTION_FAILURE) {

				String error = waiveShortInfo.getString(3);

				waiveShortInfo.close();
				waiveShortInfo = null;

				connection.rollback();

				Log.log(Log.ERROR, "RpDAO", "waiveShortDANs",
						"waiveShortDANs Exception :" + error);

				throw new DatabaseException(error);
			}
			waiveShortInfo.close();
			waiveShortInfo = null;
		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "RpDAO", "waiveShortDANs",
					sqlException.getMessage());
			Log.logException(sqlException);

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	/**
	 * This method updates the service fee values available in the ServiceFee
	 * object that is passed to this method as argument.
	 * 
	 * @param serviceFee
	 * @return boolean
	 * @roseuid 39B4A0A903A0
	 */
	public double insertServiceFee(ServiceFee serviceFee, Connection connection)
			throws DatabaseException {
		String methodName = "insertServiceFee";
		// Connection connection = null ;
		CallableStatement insertServiceFeeStmt = null;
		double serviceFeeId = 0;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = "";

		int updateStatus = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			// connection = DBConnection.getConnection() ;
			insertServiceFeeStmt = connection
					.prepareCall("{?=call funcInsertServiceFee(?,?,?,?,?,?)}");
			insertServiceFeeStmt.registerOutParameter(1, Types.INTEGER);

			insertServiceFeeStmt.setString(2, serviceFee.getCgpan());
			insertServiceFeeStmt.setDouble(3, serviceFee.getServiceAmount());

			utilDate = serviceFee.getFromDate();

			Log.log(Log.DEBUG, "RpDAO", "insertServiceFee", "from date :"
					+ utilDate);
			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));//
			// utilDate.toString());
			// Modified by pradeep for new server on 16.07.2012
			insertServiceFeeStmt.setDate(4,
					new java.sql.Date(utilDate.getTime()));
			// insertServiceFeeStmt.setDate(4, sqlDate) ;
			utilDate = serviceFee.getToDate();
			Log.log(Log.DEBUG, "RpDAO", "insertServiceFee", "to date :"
					+ utilDate);
			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));//
			// utilDate.toString());
			// insertServiceFeeStmt.setDate(5, sqlDate) ;
			insertServiceFeeStmt.setDate(5,
					new java.sql.Date(utilDate.getTime()));

			insertServiceFeeStmt.registerOutParameter(6, Types.DOUBLE);
			insertServiceFeeStmt.registerOutParameter(7, Types.VARCHAR);

			insertServiceFeeStmt.executeQuery();

			int status = insertServiceFeeStmt.getInt(1);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = insertServiceFeeStmt.getString(7);
				insertServiceFeeStmt.close();
				insertServiceFeeStmt = null;

				connection.rollback();
				throw new DatabaseException(err);
			}

			serviceFeeId = insertServiceFeeStmt.getDouble(6);
			insertServiceFeeStmt.close();
			insertServiceFeeStmt = null;

			// connection.commit();

		}
		/*
		 * catch (Exception exception) { throw new
		 * DatabaseException(exception.getMessage()); }
		 */catch (SQLException sqlException) {

			Log.log(Log.ERROR, "RpDAO", "insertServiceFee",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */

		return serviceFeeId;
	}

	/**
	 * This method updates the service fee values available in the ServiceFee
	 * object that is passed to this method as argument.
	 * 
	 * @param serviceFee
	 * @return boolean
	 * @roseuid 39B4A0A903A0
	 */
	public int getServiceFeeCount(String cgpan, Date asOnDate)
			throws DatabaseException {
		String methodName = "getServiceFeeCount";
		Connection connection = null;
		CallableStatement getServiceFeeCountStmt = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = "";

		int updateStatus = 0;
		int serviceFeeCount = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		Log.log(Log.INFO, className, methodName, "Entering");

		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);
		Log.log(Log.INFO, className, methodName, "As on date = " + asOnDate);

		try {
			Log.log(Log.INFO, className, methodName, "Establishing connection");
			connection = DBConnection.getConnection();

			getServiceFeeCountStmt = connection
					.prepareCall("{?=call funcGetServiceFeeCount(?,?,?,?)}");

			Log.log(Log.INFO, className, methodName,
					"Registering output parameter");
			getServiceFeeCountStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.INFO, className, methodName, "setting CGPAN = " + cgpan);
			getServiceFeeCountStmt.setString(2, cgpan);

			utilDate = asOnDate;
			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));

			Log.log(Log.INFO, className, methodName, "setting as on date = "
					+ utilDate);
			// getServiceFeeCountStmt.setDate(3, sqlDate) ;
			getServiceFeeCountStmt.setDate(3,
					new java.sql.Date(utilDate.getTime()));
			Log.log(Log.INFO, className, methodName,
					"Registering output parameter for service fee count");
			getServiceFeeCountStmt.registerOutParameter(4, Types.INTEGER);

			Log.log(Log.INFO, className, methodName,
					"Registering output parameter for error messages");
			getServiceFeeCountStmt.registerOutParameter(5, Types.VARCHAR);

			Log.log(Log.INFO, className, methodName,
					"Before executing Service Fee Count");
			getServiceFeeCountStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"Before executing Service Fee Count");

			int status = getServiceFeeCountStmt.getInt(1);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = getServiceFeeCountStmt.getString(5);
				getServiceFeeCountStmt.close();
				getServiceFeeCountStmt = null;
				throw new DatabaseException(err);
			}

			serviceFeeCount = getServiceFeeCountStmt.getInt(4);
			getServiceFeeCountStmt.close();
			getServiceFeeCountStmt = null;
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, methodName, "Service Fee Count = "
				+ serviceFeeCount);
		return serviceFeeCount;
	}

	/**
	 * This method is used to reallocate the amount received to the appropriate
	 * CGPAN amounts.
	 * 
	 * @param PaymentId
	 * @param DANNo
	 * @param DANType
	 * @param CGPAN
	 * @param Reason
	 * @param Allocated
	 * @return boolean
	 * @roseuid 39B8D614007D
	 */
	public// payid. danno, pan, reason, dantype, allocated changed to demand
	// advive obj..dd
	boolean reallocateAmount(DemandAdvice demandAdvice) {
		return true;
	}

	/**
	 * This method will generate Voucher for the specified member and the
	 * voucher type.
	 * 
	 * @param MemberId
	 * @param VoucherType
	 * @return boolean
	 * @roseuid 39B8E610035B
	 */
	public boolean generateVoucher(String memberId, String voucherType) {
		return true;
	}

	/**
	 * This method is retrieves the card rate for the given loan amount.
	 * 
	 * @param application
	 * @return Double
	 * @roseuid 39BA17C90354
	 */
	public double getCardRate(Application application) {
		double cardRate = 0;
		return cardRate;
	}

	/**
	 * 
	 * @author GU14477 to get the recieved amount ..
	 * 
	 */
	public double getReceivedAmount(String paymentId) {
		return 0;
	}

	/*
	 * This method gets the cumulative guarantee cover for the corresponding
	 * application.
	 */

	public double getCumulativeGuaranteeCover(Application application)
			throws DatabaseException {

		Connection connection = null;
		CallableStatement getCumulativeGuaranteeCoverStmt = null;
		ArrayList danDetailsDateWise = null;
		ResultSet resultSet = null;

		String cgpan = application.getCgpan();
		double cumulativeGuaranteeCover = 0;
		double enhancedApplicationAmount = 0;

		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getCumulativeGuaranteeCoverStmt = connection
					.prepareCall("{?=call funcGetCumulativeGuar(?,?,?,?,?,?)}");
			getCumulativeGuaranteeCoverStmt.registerOutParameter(1,
					Types.INTEGER);
			getCumulativeGuaranteeCoverStmt.setString(2,
					application.getBankId());
			getCumulativeGuaranteeCoverStmt.setString(3,
					application.getZoneId());
			getCumulativeGuaranteeCoverStmt.setString(4,
					application.getBranchId());
			getCumulativeGuaranteeCoverStmt.registerOutParameter(5,
					Types.DOUBLE);
			getCumulativeGuaranteeCoverStmt.registerOutParameter(6,
					Types.DOUBLE);
			getCumulativeGuaranteeCoverStmt.registerOutParameter(7,
					Types.VARCHAR);
			getCumulativeGuaranteeCoverStmt.execute();

			int status = getCumulativeGuaranteeCoverStmt.getInt(1);

			if (status == Constants.FUNCTION_FAILURE) {
				exception = getCumulativeGuaranteeCoverStmt.getString(7);
				getCumulativeGuaranteeCoverStmt.close();
				getCumulativeGuaranteeCoverStmt = null;
				throw new DatabaseException(exception);
			} else {
				cumulativeGuaranteeCover = getCumulativeGuaranteeCoverStmt
						.getDouble(5);
				enhancedApplicationAmount = getCumulativeGuaranteeCoverStmt
						.getDouble(6);
			}

			getCumulativeGuaranteeCoverStmt.close();
			getCumulativeGuaranteeCoverStmt = null;

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return cumulativeGuaranteeCover;
	}

	/*
	 * For updating short amount details
	 */

	public int updateShortAmountDetails(DemandAdvice demandAdvice,
			double shortAmount, Connection connection) throws DatabaseException {
		String methodName = "updateShortAmountDetails";
		// Connection connection = null ;
		CallableStatement updateShortAmountDetailsStmt = null;

		int updateStatus = 0;
		boolean updateShortAmountDetailsStatus = false;
		int shortId = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		Log.log(Log.INFO, className, methodName, "Entered");

		boolean newConn = false;
		try {
			if (newConn) {
				connection = DBConnection.getConnection();
			}
			updateShortAmountDetailsStmt = connection
					.prepareCall("{?=call funcUpdateShortAmount(?,?,?,?,?,?,?,?)}");
			updateShortAmountDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			updateShortAmountDetailsStmt.setString(2, demandAdvice.getCgpan());
			updateShortAmountDetailsStmt.setString(3, demandAdvice.getBankId());
			updateShortAmountDetailsStmt.setString(4, demandAdvice.getZoneId());
			updateShortAmountDetailsStmt.setString(5,
					demandAdvice.getBranchId());
			updateShortAmountDetailsStmt.setString(6,
					demandAdvice.getPaymentId());
			updateShortAmountDetailsStmt.setDouble(7, shortAmount);
			updateShortAmountDetailsStmt.registerOutParameter(8,
					java.sql.Types.INTEGER);
			updateShortAmountDetailsStmt.registerOutParameter(9,
					java.sql.Types.VARCHAR);

			updateShortAmountDetailsStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"Executed Function funcUpdateShortAmount");
			updateStatus = updateShortAmountDetailsStmt.getInt(1);

			if (updateStatus == 0) {
				updateShortAmountDetailsStatus = true;
				shortId = updateShortAmountDetailsStmt.getInt(8);
			} else if (updateStatus == 1) {
				updateShortAmountDetailsStatus = false;
				String err = updateShortAmountDetailsStmt.getString(9);
				
				System.out.println("err  "+err);
				updateShortAmountDetailsStmt.close();
				updateShortAmountDetailsStmt = null;
				System.out.println("err  "+err);
				throw new DatabaseException(err);
			}
			updateShortAmountDetailsStmt.close();
			updateShortAmountDetailsStmt = null;
		} catch (SQLException exception) {
			
			System.out.println(" exception "+exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		return shortId;
	}

	/*
	 * gets the approved applications member wise
	 */

	/*
	 * gets the short amount while generating short dan method generate shdan in
	 * ro processor
	 */

	public double getShortAmount(MLIInfo mliId) throws DatabaseException {
		return 0;
	}

	/*
	 * gets the excess amount while generating short dan method generate shdan
	 * in ro processor
	 */

	public double getExcessAmount(MLIInfo mliId) throws DatabaseException {
		return 0;
	}

	public String getDANId(String danType, String bankId, Connection connection)
			throws DatabaseException {
		String danNo = "";
		// Connection connection = null ;
		CallableStatement danNoStmt = null;
		int updateStatus;
		Log.log(Log.INFO, className, "getDANId", "Entered");
		try {
			// connection = DBConnection.getConnection() ;
			danNoStmt = connection
					.prepareCall("{?=call funcGenerateDANId(?,?,?,?)}");

			danNoStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			danNoStmt.setString(2, danType);
			danNoStmt.setString(3, bankId);
			danNoStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			danNoStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			Log.log(Log.INFO, className, "getDANId",
					"Before Executing stored procedure");
			danNoStmt.executeQuery();
			Log.log(Log.INFO, className, "getDANId",
					"After Executing stored procedure");
			updateStatus = danNoStmt.getInt(1);
			Log.log(Log.INFO, className, "getDANId", "sp result "
					+ updateStatus);
			if (updateStatus == Constants.FUNCTION_FAILURE) {
				String err = danNoStmt.getString(4);
				Log.log(Log.INFO, className, "getDANId", "sp error " + err);
				danNoStmt.close();
				danNoStmt = null;
				throw new DatabaseException(err);
			}
			danNo = danNoStmt.getString(5);
			Log.log(Log.INFO, className, "getDANId", "DAN No = " + danNo);
			danNoStmt.close();
			danNoStmt = null;
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// DBConnection.freeConnection(connection) ;
		}
		return danNo;
	}

	/**
	 * This method inserts CGPAN details for the newly created DAN.
	 * 
	 * @param demandAdvice
	 * @return void
	 **/
	public void insertPANDetailsForDAN(DemandAdvice demandAdvice,
			Connection connection) throws DatabaseException {
		String methodName = "insertPANDetailsForDAN";

		Log.log(Log.INFO, className, methodName, "Entered");

		// Connection connection = null ;
		CallableStatement insertCGPANDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String formatedDate = "";

		int updateStatus = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		double amountRaised = demandAdvice.getAmountRaised();
		if (amountRaised == 0 && !demandAdvice.getDanType().equals("CL")) {
			Log.log(Log.DEBUG, className, methodName, "Amount is zero");
			return;
		}

		try {
			// connection = DBConnection.getConnection() ;
			/*insertCGPANDetailsStmt = connection
					.prepareCall("{?=call funcInsertDanCGPANDtl(?,?,?,?,?,?,?,?)}");*/  //Diksha
			
			 insertCGPANDetailsStmt = connection.prepareCall("{?=call funcInsertDanCGPANDtl(?,?,?,?,?,?,?,?,?,?,?,?)}");
			insertCGPANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.DEBUG, className, methodName, "DAN No = "
					+ demandAdvice.getDanNo());

			insertCGPANDetailsStmt.setString(2, demandAdvice.getDanNo());

			Log.log(Log.DEBUG, className, methodName,
					"CGPAN = " + demandAdvice.getCgpan());

			insertCGPANDetailsStmt.setString(3, demandAdvice.getCgpan());

			Log.log(Log.DEBUG, className, methodName, "new dan no = "
					+ demandAdvice.getNewDanNo());

			insertCGPANDetailsStmt.setString(4, demandAdvice.getNewDanNo());

			Log.log(Log.DEBUG, className, methodName, "Amount Raised = "
					+ demandAdvice.getAmountRaised());

			insertCGPANDetailsStmt.setDouble(5, demandAdvice.getAmountRaised());

			Log.log(Log.DEBUG, className, methodName, "Penalty = "
					+ demandAdvice.getPenalty());

			insertCGPANDetailsStmt.setDouble(6, demandAdvice.getPenalty());

			utilDate = demandAdvice.getDanDueDate();

			Log.log(Log.DEBUG, className, methodName, "Due date(util) = "
					+ utilDate);

			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));

			// Log.log(Log.DEBUG, className, methodName,
			// "Due date(sql) = "+utilDate);

			// insertCGPANDetailsStmt.setDate(7,sqlDate) ;
			insertCGPANDetailsStmt.setDate(7,
					new java.sql.Date(utilDate.getTime()));

			Log.log(Log.DEBUG, className, methodName, "Remarks");

			insertCGPANDetailsStmt.setString(8, null);

			Log.log(Log.DEBUG, className, methodName, "Registering error code");
		
			//Diksha
			insertCGPANDetailsStmt.setDouble(9, demandAdvice.getStandardRate());
			
            insertCGPANDetailsStmt.setDouble(10, demandAdvice.getNpaRiskRate());
            
            insertCGPANDetailsStmt.setDouble(11, demandAdvice.getClaimRiskrate());
            
            insertCGPANDetailsStmt.setDouble(12, demandAdvice.getFinalRate());

			insertCGPANDetailsStmt.registerOutParameter(13, Types.VARCHAR);

			Log.log(Log.DEBUG, className, methodName,
					"Before Inserting CGPAN details");

			insertCGPANDetailsStmt.executeQuery();

			Log.log(Log.DEBUG, className, methodName,
					"After Inserting CGPAN details");

			updateStatus = insertCGPANDetailsStmt.getInt(1);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				String err = insertCGPANDetailsStmt.getString(13);
				insertCGPANDetailsStmt.close();
				insertCGPANDetailsStmt = null;
				throw new DatabaseException("Failed inserting CGPAN details "
						+ err);
			}

			insertCGPANDetailsStmt.close();
			insertCGPANDetailsStmt = null;

			String danType = demandAdvice.getDanType();
			String functionName = "";

			if (danType.equals(RpConstants.DAN_TYPE_SFDAN)) {
				functionName = "funcUpdateServiceFeeDanId";
			} else if (danType.equals(RpConstants.DAN_TYPE_SHDAN)) {
				functionName = "funcUpdateShortInfoDanId";
			} else {
				Log.log(Log.DEBUG, className, methodName,
						"CGDAN generation. skip.");
				Log.log(Log.INFO, className, methodName, "Exited");
				return;
			}

			// Log.log(Log.DEBUG, className, methodName, "function name " +
			// functionName);

			insertCGPANDetailsStmt = connection.prepareCall("{?=call "
					+ functionName + "(?,?,?)}");
			insertCGPANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			if (danType.equals(RpConstants.DAN_TYPE_SFDAN)) {
				insertCGPANDetailsStmt.setDouble(2, demandAdvice.getFeeId());

				Log.log(Log.DEBUG, className, methodName, "service fee id "
						+ demandAdvice.getFeeId());
			} else if (danType.equals(RpConstants.DAN_TYPE_SHDAN)) {
				insertCGPANDetailsStmt.setString(2, demandAdvice.getCgpan());

				Log.log(Log.DEBUG, className, methodName, "cgpan "
						+ demandAdvice.getCgpan());
			}

			insertCGPANDetailsStmt.setString(3, demandAdvice.getDanNo());
			// Log.log(Log.DEBUG, className, methodName, "dan id " +
			// demandAdvice.getDanNo());
			insertCGPANDetailsStmt.registerOutParameter(4, Types.VARCHAR);

			// Log.log(Log.DEBUG, className, methodName,
			// "Before updating with Dan Id");

			insertCGPANDetailsStmt.executeQuery();

			// Log.log(Log.DEBUG, className, methodName,
			// "After updating with Dan Id");

			updateStatus = insertCGPANDetailsStmt.getInt(1);

			if (updateStatus != 0) {
				String err = insertCGPANDetailsStmt.getString(4);
				insertCGPANDetailsStmt.close();
				insertCGPANDetailsStmt = null;
				throw new DatabaseException("Failed updating dan id details "
						+ err);
			}
			insertCGPANDetailsStmt.close();
			insertCGPANDetailsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, className, methodName,
					"Exception occured while inserting CGPAN details for a DAN. CGPAN="
							+ demandAdvice.getDanNo() + ", error = "
							+ exception.getMessage());

			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */

		Log.log(Log.INFO, className, methodName, "Exited");
	}

	/**
	 * insert the Expired cgpan details for DAN
	 * 
	 * @param demandAdvice
	 * @param connection
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void insertPANDetailsForDANExp(DemandAdvice demandAdvice,
			Connection connection) throws DatabaseException {
		String methodName = "insertPANDetailsForDANExp";

		Log.log(Log.INFO, className, methodName, "Entered");

		// Connection connection = null ;
		CallableStatement insertCGPANDetailsStmt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String formatedDate = "";

		int updateStatus = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		double amountRaised = demandAdvice.getAmountRaised();
		if (amountRaised == 0 && !demandAdvice.getDanType().equals("CL")) {
			Log.log(Log.DEBUG, className, methodName, "Amount is zero");
			return;
		}

		try {
			// connection = DBConnection.getConnection() ;
			insertCGPANDetailsStmt = connection
					.prepareCall("{?=call funcInsertDanCGPANDtl(?,?,?,?,?,?,?,?)}");
			insertCGPANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			Log.log(Log.DEBUG, className, methodName, "DAN No = "
					+ demandAdvice.getDanNo());

			insertCGPANDetailsStmt.setString(2, demandAdvice.getDanNo());

			Log.log(Log.DEBUG, className, methodName,
					"CGPAN = " + demandAdvice.getCgpan());

			insertCGPANDetailsStmt.setString(3, demandAdvice.getCgpan());

			Log.log(Log.DEBUG, className, methodName, "new dan no = "
					+ demandAdvice.getNewDanNo());

			insertCGPANDetailsStmt.setString(4, demandAdvice.getNewDanNo());

			Log.log(Log.DEBUG, className, methodName, "Amount Raised = "
					+ demandAdvice.getAmountRaised());

			insertCGPANDetailsStmt.setDouble(5, demandAdvice.getAmountRaised());

			Log.log(Log.DEBUG, className, methodName, "Penalty = "
					+ demandAdvice.getPenalty());

			insertCGPANDetailsStmt.setDouble(6, demandAdvice.getPenalty());

			utilDate = demandAdvice.getDanDueDate();

			Log.log(Log.DEBUG, className, methodName, "Due date(util) = "
					+ utilDate);

			// formatedDate=dateFormat.format(utilDate);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));

			// Log.log(Log.DEBUG, className, methodName,
			// "Due date(sql) = "+sqlDate);

			// insertCGPANDetailsStmt.setDate(7,sqlDate) ;
			insertCGPANDetailsStmt.setDate(7,
					new java.sql.Date(utilDate.getTime()));

			Date expiryDate = getExpiryDateofSpecifiedCgpan(demandAdvice
					.getCgpan());
			java.text.DateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

			Log.log(Log.DEBUG, className, methodName, "Remarks");

			insertCGPANDetailsStmt.setString(
					8,
					"DAN Generated upto " + format1.format(expiryDate)
							+ " as case expired on "
							+ format1.format(expiryDate));
			// System.out.println("Remarks:"+"Dan Generated upto"+format1.format(
			// expiryDate )+ " as case expired on "+ format1.format( expiryDate
			// ));
			Log.log(Log.DEBUG, className, methodName, "Registering error code");

			insertCGPANDetailsStmt.registerOutParameter(9, Types.VARCHAR);

			Log.log(Log.DEBUG, className, methodName,
					"Before Inserting CGPAN details");

			insertCGPANDetailsStmt.executeQuery();

			Log.log(Log.DEBUG, className, methodName,
					"After Inserting CGPAN details");

			updateStatus = insertCGPANDetailsStmt.getInt(1);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				String err = insertCGPANDetailsStmt.getString(9);
				insertCGPANDetailsStmt.close();
				insertCGPANDetailsStmt = null;
				throw new DatabaseException("Failed inserting CGPAN details "
						+ err);
			}

			insertCGPANDetailsStmt.close();
			insertCGPANDetailsStmt = null;

			String danType = demandAdvice.getDanType();
			String functionName = "";

			if (danType.equals(RpConstants.DAN_TYPE_SFDAN)) {
				functionName = "funcUpdateServiceFeeDanId";
			} else if (danType.equals(RpConstants.DAN_TYPE_SHDAN)) {
				functionName = "funcUpdateShortInfoDanId";
			} else {
				Log.log(Log.DEBUG, className, methodName,
						"CGDAN generation. skip.");
				Log.log(Log.INFO, className, methodName, "Exited");
				return;
			}

			// Log.log(Log.DEBUG, className, methodName, "function name " +
			// functionName);

			insertCGPANDetailsStmt = connection.prepareCall("{?=call "
					+ functionName + "(?,?,?)}");
			insertCGPANDetailsStmt.registerOutParameter(1, Types.INTEGER);

			if (danType.equals(RpConstants.DAN_TYPE_SFDAN)) {
				insertCGPANDetailsStmt.setDouble(2, demandAdvice.getFeeId());

				Log.log(Log.DEBUG, className, methodName, "service fee id "
						+ demandAdvice.getFeeId());
			} else if (danType.equals(RpConstants.DAN_TYPE_SHDAN)) {
				insertCGPANDetailsStmt.setString(2, demandAdvice.getCgpan());

				Log.log(Log.DEBUG, className, methodName, "cgpan "
						+ demandAdvice.getCgpan());
			}

			insertCGPANDetailsStmt.setString(3, demandAdvice.getDanNo());
			// Log.log(Log.DEBUG, className, methodName, "dan id " +
			// demandAdvice.getDanNo());
			insertCGPANDetailsStmt.registerOutParameter(4, Types.VARCHAR);

			// Log.log(Log.DEBUG, className, methodName,
			// "Before updating with Dan Id");

			insertCGPANDetailsStmt.executeQuery();

			// Log.log(Log.DEBUG, className, methodName,
			// "After updating with Dan Id");

			updateStatus = insertCGPANDetailsStmt.getInt(1);

			if (updateStatus != 0) {
				String err = insertCGPANDetailsStmt.getString(4);
				insertCGPANDetailsStmt.close();
				insertCGPANDetailsStmt = null;
				throw new DatabaseException("Failed updating dan id details "
						+ err);
			}
			insertCGPANDetailsStmt.close();
			insertCGPANDetailsStmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, className, methodName,
					"Exception occured while inserting CGPAN details for a DAN. CGPAN="
							+ demandAdvice.getDanNo() + ", error = "
							+ exception.getMessage());

			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		}
		/*
		 * finally { DBConnection.freeConnection(connection) ; }
		 */

		Log.log(Log.INFO, className, methodName, "Exited");
	}

	/* ------------------------- */

	public Date getFirstDisbursementDate(String cgpan) throws DatabaseException {
		String methodName = "getFirstDisbursementDate";
		Date firstDisbursementDate = null;
		Connection connection = null;
		CallableStatement firstDisbursementDateStmt = null;
		ResultSet resultSet = null;

		int updateStatus = 0;

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			firstDisbursementDateStmt = connection
					.prepareCall("{?=call packGetPIDBRDtlsCGPAN.funcDBRDetailsForCGPAN(?,?,?)}");
			firstDisbursementDateStmt.registerOutParameter(1, Types.INTEGER);
			firstDisbursementDateStmt.setString(2, cgpan);
			firstDisbursementDateStmt.registerOutParameter(3, Constants.CURSOR);
			firstDisbursementDateStmt.registerOutParameter(4, Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"Before executing first disbursement date");
			firstDisbursementDateStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"After executing first disbursement date");

			updateStatus = firstDisbursementDateStmt.getInt(1);
			Log.log(Log.INFO, className, methodName, "sp result "
					+ updateStatus);
			if (updateStatus != 0) {
				String err = firstDisbursementDateStmt.getString(4);
				Log.log(Log.INFO, className, methodName, "sp error " + err);
				firstDisbursementDateStmt.close();
				firstDisbursementDateStmt = null;
				throw new DatabaseException(err);
			}
			resultSet = (ResultSet) firstDisbursementDateStmt.getObject(3);
			if (resultSet.next()) {
				firstDisbursementDate = new java.util.Date(resultSet.getDate(3)
						.getTime());
			}
			resultSet.close();
			resultSet = null;
			firstDisbursementDateStmt.close();
			firstDisbursementDateStmt = null;
		} catch (Exception exception) {
			Log.log(Log.INFO, className, methodName,
					"Exception occured while fetching disbursement date. Error = "
							+ exception.getMessage());
			// throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return firstDisbursementDate;
	}

	public double getAmountOutstanding(Application application, Date date) {
		String methodName = "getAmountOutstanding";

		Log.log(Log.INFO, className, methodName, "Entering");

		Connection connection = null;
		CallableStatement amountOutstandingStmt = null;
		ResultSet resultSet = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = "";

		String cgpan = application.getCgpan();
		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);

		String errorMessage = "";
		int updateStatus;
		double amountOutstanding = 0;
		java.sql.Date sqlDate = null;

		try {
			connection = DBConnection.getConnection();
			amountOutstandingStmt = connection
					.prepareCall("{?=call funcGetOutstanding(?,?,?,?)}");
			amountOutstandingStmt.registerOutParameter(1, Types.INTEGER);
			amountOutstandingStmt.setString(2, cgpan);

			// formatedDate=dateFormat.format(date);
			// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));
			// amountOutstandingStmt.setDate(3, sqlDate) ;
			amountOutstandingStmt.setDate(3, new java.sql.Date(date.getTime()));

			amountOutstandingStmt.registerOutParameter(4, Types.DOUBLE);
			amountOutstandingStmt.registerOutParameter(5, Types.VARCHAR);

			Log.log(Log.INFO, className, methodName,
					"Before executing outstanding details");
			amountOutstandingStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"After executing outstanding details");

			updateStatus = amountOutstandingStmt.getInt(1);
			if (updateStatus != 0) {
				errorMessage = amountOutstandingStmt.getString(5);
				Log.log(Log.INFO, className, methodName,
						"Exception occured while fetching outstanding details: "
								+ errorMessage);
				amountOutstandingStmt.close();
				amountOutstandingStmt = null;
				throw new DatabaseException(
						"Failed fetching outstanding details");
			}
			amountOutstanding = amountOutstandingStmt.getDouble(4);
			Log.log(Log.INFO, className, methodName, "Outstanding amount = "
					+ amountOutstanding);
			amountOutstandingStmt.close();
			amountOutstandingStmt = null;
		} catch (Exception exception) {
			Log.log(Log.INFO, className, methodName,
					"Exception occured while fetching outstanding details. Error = "
							+ exception.getMessage());
			// throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return amountOutstanding;
	}

	public int getAmountOutstandingCount(Application application) {
		String methodName = "getAmountOutstandingCount";

		Connection connection = null;
		CallableStatement amountOutstandingStmt = null;
		ResultSet resultSet = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = "";

		String cgpan = application.getCgpan();
		String errorMessage = "";
		int updateStatus;
		int amountOutstandingCount = 0;
		java.sql.Date sqlDate = null;

		// Date currentDate = new Date() ;
		// Date asOnDate = new Date() ;

		try {
			connection = DBConnection.getConnection();
			amountOutstandingStmt = connection
					.prepareCall("{?=call funcGetOutstandingCount(?,?,?)}");
			amountOutstandingStmt.registerOutParameter(1, Types.INTEGER);
			amountOutstandingStmt.setString(2, cgpan);

			amountOutstandingStmt.registerOutParameter(3, Types.INTEGER);
			amountOutstandingStmt.registerOutParameter(4, Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"Before executing outstanding details");
			amountOutstandingStmt.executeQuery();
			Log.log(Log.INFO, className, methodName,
					"After executing outstanding details");

			updateStatus = amountOutstandingStmt.getInt(1);
			if (updateStatus != 0) {
				errorMessage = amountOutstandingStmt.getString(4);
				Log.log(Log.INFO, className, methodName,
						"Exception occured while fetching outstanding details: "
								+ errorMessage);
				amountOutstandingStmt.close();
				amountOutstandingStmt = null;
				throw new DatabaseException(
						"Failed fetching outstanding details");
			}
			amountOutstandingCount = amountOutstandingStmt.getInt(3);
			amountOutstandingStmt.close();
			amountOutstandingStmt = null;
		} catch (Exception exception) {
			Log.log(Log.INFO, className, methodName,
					"Exception occured while fetching disbursement date. Error = "
							+ exception.getMessage());
			// throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return amountOutstandingCount;
	}

	public ArrayList getShortAmountMemberWise(String mliId)
			throws DatabaseException {

		String methodName = "getShortAmountMemberWise";
		Connection connection = null;
		CallableStatement getShortAmountMemberWiseStmt = null;
		ArrayList shortAmountMemberWiseStmt = null;
		ResultSet resultSet = null;
		ShortAmountInfo shortAmountInfo = null;

		String memberId = "";

		if (!mliId.equals(Constants.ALL)) {
			mliId = mliId.substring(1, 13);
		}

		Log.log(Log.INFO, className, methodName, "member Id input :" + mliId);

		int status = 0;
		Log.log(Log.INFO, className, methodName, "Entered");

		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getShortAmountMemberWiseStmt = connection
					.prepareCall("{?=call packGetShortAmounts.funcGetShortAmounts(?,?)}");
			getShortAmountMemberWiseStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getShortAmountMemberWiseStmt.registerOutParameter(2,
					Constants.CURSOR);

			getShortAmountMemberWiseStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);
			Log.log(Log.DEBUG, className, methodName,
					"Before executing Stored Procedure");
			getShortAmountMemberWiseStmt.execute();
			Log.log(Log.DEBUG, className, methodName,
					"After executing Stored Procedure");
			resultSet = (ResultSet) getShortAmountMemberWiseStmt.getObject(2);

			Log.log(Log.DEBUG, className, methodName, "ResultSet = "
					+ resultSet.toString());

			status = getShortAmountMemberWiseStmt.getInt(1);
			Log.log(Log.DEBUG, className, methodName, "Status = " + status);

			if (status == Constants.FUNCTION_FAILURE) {
				String error = getShortAmountMemberWiseStmt.getString(3);
				getShortAmountMemberWiseStmt.close();
				getShortAmountMemberWiseStmt = null;
				throw new DatabaseException(error);
			}

			int recordCount = 0;

			String bankId = null;
			String zoneId = null;
			String branchId = null;

			while (resultSet.next()) {
				if (recordCount == 0) {
					shortAmountMemberWiseStmt = new ArrayList();
				}

				bankId = resultSet.getString(2);

				zoneId = resultSet.getString(3);

				branchId = resultSet.getString(4);

				Log.log(Log.DEBUG, className, methodName,
						"bankId,zoneId,branchId" + bankId + "," + zoneId + ","
								+ branchId);

				if (!mliId.equals(Constants.ALL)
						&& !mliId.equals(bankId + zoneId + branchId)) {
					Log.log(Log.DEBUG, className, methodName,
							"Member id is not same. Skip.");
					continue;
				}

				shortAmountInfo = new ShortAmountInfo();

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				shortAmountInfo.setCgpan(resultSet.getString(1));

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				shortAmountInfo.setBankId(bankId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				shortAmountInfo.setZoneId(zoneId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				shortAmountInfo.setBranchId(branchId);

				/*
				 * Log.log(Log.DEBUG, className, methodName,
				 * "Inside While : Service Fee="+resultSet.getDouble(5)) ;
				 * shortAmountInfo.setShortAmount(resultSet.getDouble(5));
				 */

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Short Amount=" + resultSet.getDouble(5));
				shortAmountInfo.setShortAmount(resultSet.getDouble(5));

				Log.log(Log.DEBUG, className, methodName,
						"Inside While : Borrower Id=" + resultSet.getString(6));
				shortAmountInfo.setBorrowerId(resultSet.getString(6));

				shortAmountMemberWiseStmt.add(shortAmountInfo);
				++recordCount;
			}
			resultSet.close();
			resultSet = null;
			getShortAmountMemberWiseStmt.close();
			getShortAmountMemberWiseStmt = null;
		} catch (Exception exception) {
			Log.log(Log.ERROR, className, methodName, exception.getMessage());
			Log.logException(exception);

			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, methodName, "Exited");

		return shortAmountMemberWiseStmt;
	}

	public ArrayList getDANsBeyondDueDate() throws DatabaseException {
		String methodName = "getDANsBeyondDueDate";
		Connection connection = null;
		CallableStatement getDANsBeyondDueDateStmt = null;
		ArrayList dansBeyondDueDate = null;
		ResultSet resultSet = null;
		DemandAdvice demandAdvice = null;

		int status = 0;
		Log.log(Log.INFO, className, methodName, "Entering");
		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getDANsBeyondDueDateStmt = connection
					.prepareCall("{?=call packGetDANsBeyondDueDate.funcGetDANsBeyondDueDate(?,?)}");
			getDANsBeyondDueDateStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);

			getDANsBeyondDueDateStmt.registerOutParameter(2, Constants.CURSOR);

			getDANsBeyondDueDateStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);
			Log.log(Log.INFO, className, methodName,
					"Before executing Stored Procedure");
			getDANsBeyondDueDateStmt.execute();
			Log.log(Log.INFO, className, methodName,
					"After executing Stored Procedure");
			resultSet = (ResultSet) getDANsBeyondDueDateStmt.getObject(2);

			Log.log(Log.INFO, className, methodName,
					"ResultSet = " + resultSet.toString());

			status = getDANsBeyondDueDateStmt.getInt(1);
			Log.log(Log.INFO, className, methodName, "Status = " + status);
			if (status != 0) {
				String error = getDANsBeyondDueDateStmt.getString(3);
				getDANsBeyondDueDateStmt.close();
				getDANsBeyondDueDateStmt = null;
				throw new DatabaseException(error);
			}

			int recordCount = 0;

			while (resultSet.next()) {
				if (recordCount == 0) {
					dansBeyondDueDate = new ArrayList();
				}

				demandAdvice = new DemandAdvice();

				Log.log(Log.INFO, className, methodName,
						"Inside While : DAN No.=" + resultSet.getString(1));
				demandAdvice.setDanNo(resultSet.getString(1));

				Log.log(Log.INFO, className, methodName,
						"Inside While : CGPAN=" + resultSet.getString(2));
				demandAdvice.setCgpan(resultSet.getString(2));

				Log.log(Log.INFO,
						className,
						methodName,
						"Inside While : Amount Raised="
								+ resultSet.getDouble(3));
				demandAdvice.setAmountRaised(resultSet.getDouble(3));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Due Date=" + resultSet.getDate(4));
				demandAdvice.setDanDueDate(resultSet.getDate(4));

				Log.log(Log.INFO, className, methodName,
						"Inside While : Penalty=" + resultSet.getDouble(5));
				demandAdvice.setPenalty(resultSet.getDouble(5));

				demandAdvice.setDanType(resultSet.getString(6));

				dansBeyondDueDate.add(demandAdvice);
				++recordCount;
			}
			resultSet.close();
			resultSet = null;
			getDANsBeyondDueDateStmt.close();
			getDANsBeyondDueDateStmt = null;

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return dansBeyondDueDate;
	}

	public void cancelAndUpdateDANDetails(DemandAdvice demandAdvice,
			Connection connection) throws DatabaseException {
		String methodName = "cancelAndUpdateDANDetails";

		Log.log(Log.INFO, className, methodName, "Entered");

		// Connection connection=DBConnection.getConnection();
		try {
			CallableStatement callable = connection
					.prepareCall("{?=call funcUpdDanReAllocate(?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.setString(2, demandAdvice.getDanNo());
			callable.setString(3, demandAdvice.getCgpan());
			callable.setString(4, demandAdvice.getNewDanNo());
			callable.setDouble(5, demandAdvice.getAmountRaised());
			callable.setDate(6, new java.sql.Date(demandAdvice.getDanDueDate()
					.getTime()));
			callable.setString(7, "Due to Reapproval");

			callable.registerOutParameter(8, Types.VARCHAR);

			callable.execute();

			int errorCode = callable.getInt(1);

			String error = callable.getString(8);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to update dan info.");

		}
		/*
		 * finally { DBConnection.freeConnection(connection); }
		 */
		Log.log(Log.INFO, className, methodName, "Exited");

	}

	public void updateRealizationDate(DemandAdvice demandAdvice,
			Date realisationDate, Connection connection)
			throws DatabaseException {
		String methodName = "updateRealizationDate";

		Log.log(Log.INFO, className, methodName, "Entered");

		boolean newConn = false;

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newConn = true;
		}
		try {
			CallableStatement callable = null;
			// System.out.println("dan:"+demandAdvice.getDanNo());
			// System.out.println("cgpan:"+demandAdvice.getCgpan());
			// System.out.println("Date:"+realisationDate);
			int errorCode;
			String error;
			String danType = demandAdvice.getDanNo().substring(0, 2);

			callable = connection
					.prepareCall("{?=call funcUpdStartDtDan (?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.setString(2, demandAdvice.getDanNo());

			callable.setString(3, demandAdvice.getCgpan());

			callable.setDate(4, new java.sql.Date(realisationDate.getTime()));

			callable.registerOutParameter(5, Types.VARCHAR);

			callable.execute();

			errorCode = callable.getInt(1);
              System.out.println(" errorCode "+errorCode);
			error = callable.getString(5);
			
			System.out.println(" error 10270 "+error);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				connection.rollback();
				System.out.println(" error 10270 "+error);
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;

			if (newConn) {
				connection.commit();
			}
		} catch (SQLException e) {
			
			System.out.println(" error 10270 "+e.getMessage());
			
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			if (newConn) {
				try {
					connection.rollback();
				} catch (SQLException ignore) {
					
					System.out.println("SQLException ignore "+ignore );
				}
			}

			throw new DatabaseException("Unable to update realization date.");

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO, className, methodName, "Exited");
	}

	public void insertRefundAdviceDetails(RefundAdvice refundAdvice,
			String userId) throws DatabaseException {
		String methodName = "insertRefundAdviceDetails";

		Log.log(Log.INFO, className, methodName, "Entered");

		Connection connection = DBConnection.getConnection();
		try {
			CallableStatement callable = connection.prepareCall("{?=call "
					+ "funcInsertRefundAdv(?,?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.setString(2, refundAdvice.getDanId());
			callable.setString(3, refundAdvice.getCgpan());

			callable.setString(4, refundAdvice.getBankId());
			callable.setString(5, refundAdvice.getZoneId());
			callable.setString(6, refundAdvice.getBranchId());
			callable.setDouble(7, refundAdvice.getAmount());
			callable.setString(8, "System generated");
			callable.setString(9, userId);

			callable.registerOutParameter(10, Types.VARCHAR);
			callable.execute();
			int errorCode = callable.getInt(1);

			String error = callable.getString(10);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException(
					"Unable to insert refund advice details.");

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, className, methodName, "Exited");
	}

	public ArrayList getAllRefundAdvices() throws DatabaseException {
		String methodName = "getAllRefundAdvices";

		Log.log(Log.INFO, className, methodName, "Entered");
		ArrayList refundAdvices = new ArrayList();

		Connection connection = DBConnection.getConnection();
		try {
			CallableStatement callable = connection.prepareCall("{?=call ()}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.registerOutParameter(2, Constants.CURSOR);

			callable.registerOutParameter(3, Types.VARCHAR);

			int errorCode = callable.getInt(1);

			String error = callable.getString(3);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}
			ResultSet results = (ResultSet) callable.getObject(2);

			while (results.next()) {
				RefundAdvice refundAdvice = new RefundAdvice();

				refundAdvice.setBankId(results.getString(1));
				refundAdvice.setZoneId(results.getString(2));
				refundAdvice.setBranchId(results.getString(3));

				refundAdvice.setDanId(results.getString(4));
				refundAdvice.setCgpan(results.getString(5));
				refundAdvice.setAmount(results.getDouble(6));

				refundAdvices.add(refundAdvice);

			}

			results.close();
			results = null;

			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to update dan info.");

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, methodName, "Exited");

		return refundAdvices;
	}

	public void dummy(DANSummary dansummary) throws DatabaseException {
		String methodName = "getDANsBeyondDueDate";

		Log.log(Log.INFO, className, methodName, "Entered");

		Connection connection = DBConnection.getConnection();
		try {
			CallableStatement callable = connection.prepareCall("{?=call ()}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.registerOutParameter(2, Types.VARCHAR);

			int errorCode = callable.getInt(1);

			String error = callable.getString(2);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to update dan info.");

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, className, methodName, "Exited");
	}

	/**
	 * This method returns all CGPAN details that belongs to the particular
	 * CGDAN. The output will be an ArrayList of DemandAdvice object.
	 * 
	 * @param CGDAN
	 * @return java.util.ArrayList
	 * @roseuid 39AF57080157
	 */
	public ArrayList getAllocatedCGPANsForDAN(String danNo)
			throws DatabaseException {
		AllocationDetail danApplication = null;
		Connection connection = null;
		ResultSet rsDemandAdvice = null;
		CallableStatement demandAdviceStmt;

		int getCgpanStatus = 0;
		String getDemandAdviceErr = "";

		ArrayList danDetails = new ArrayList();

		try {
			connection = DBConnection.getConnection();

			demandAdviceStmt = connection
					.prepareCall("{?= call packGetPANDtlforDAN.funcGetPANDtlforDAN(?,?,?)}");
			demandAdviceStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			demandAdviceStmt.setString(2, danNo);
			demandAdviceStmt.registerOutParameter(3, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			demandAdviceStmt.execute();

			getCgpanStatus = demandAdviceStmt.getInt(1);

			if (getCgpanStatus == 0) {
				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(3);

				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));

					String appropriatedFlag = rsDemandAdvice.getString(7);
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setFirstDisbursementDate(DateHelper
							.sqlToUtilDate(rsDemandAdvice.getDate(9)));

					if (appropriatedFlag.equals(Constants.NO)) {
						danDetails.add(danApplication);
					}

					danApplication = null;
				}
				rsDemandAdvice.close();
				rsDemandAdvice = null;
			} else {
				getDemandAdviceErr = demandAdviceStmt.getString(4);
				demandAdviceStmt.close();
				demandAdviceStmt = null;
				throw new DatabaseException(getDemandAdviceErr);
			}
			demandAdviceStmt.close();
			demandAdviceStmt = null;
		} catch (Exception exception) {
			// System.out.println(exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return danDetails;
	}

	public ArrayList getPANDetailsForReallocation(DemandAdvice demandAdvice)
			throws DatabaseException {
		AllocationDetail danApplication = null;
		Connection connection = null;
		ResultSet rsDemandAdvice = null;
		CallableStatement demandAdviceStmt;

		int getCgpanStatus = 0;
		String getDemandAdviceErr = "";

		ArrayList danDetails = new ArrayList();

		String danNo = demandAdvice.getDanNo();
		String paymentId = demandAdvice.getPaymentId();

		try {
			connection = DBConnection.getConnection();

			demandAdviceStmt = connection
					.prepareCall("{?= call packGetPANDtlforReallocation.funcGetPANDtlforReallocation(?,?,?,?)}");
			demandAdviceStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			demandAdviceStmt.setString(2, danNo);
			demandAdviceStmt.setString(3, paymentId);
			demandAdviceStmt.registerOutParameter(4, Constants.CURSOR);
			demandAdviceStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			demandAdviceStmt.execute();

			getCgpanStatus = demandAdviceStmt.getInt(1);

			if (getCgpanStatus == 0) {
				rsDemandAdvice = (ResultSet) demandAdviceStmt.getObject(4);

				while (rsDemandAdvice.next()) {
					danApplication = new AllocationDetail();

					danApplication.setCgpan(rsDemandAdvice.getString(1));
					Log.log(Log.INFO, className,
							"getPANDetailsForReallocation", "cgpan "
									+ danApplication.getCgpan());
					danApplication.setNameOfUnit(rsDemandAdvice.getString(2));
					danApplication.setFacilityCovered(rsDemandAdvice
							.getString(3));
					danApplication.setAmountDue(rsDemandAdvice.getDouble(4));
					danApplication.setPenalty(rsDemandAdvice.getDouble(6));

					String appropriatedFlag = rsDemandAdvice.getString(7);
					danApplication
							.setAllocatedFlag(rsDemandAdvice.getString(8));
					danApplication.setFirstDisbursementDate(DateHelper
							.sqlToUtilDate(rsDemandAdvice.getDate(9)));

					// if(appropriatedFlag.equals(Constants.NO))
					// {
					danDetails.add(danApplication);
					// }

					danApplication = null;
				}
				rsDemandAdvice.close();
				rsDemandAdvice = null;
			} else {
				getDemandAdviceErr = demandAdviceStmt.getString(5);
				demandAdviceStmt.close();
				demandAdviceStmt = null;
				throw new DatabaseException(getDemandAdviceErr);
			}
			demandAdviceStmt.close();
			demandAdviceStmt = null;
		} catch (Exception exception) {
			// System.out.println(exception.getMessage());
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return danDetails;
	}

	/**
	 * Retreives the details for CLDAN Generation
	 * 
	 * @author SS14485
	 */
	public ArrayList getDetailsForCLDANGen(User user, String bankId,
			String zoneId, String branchId) throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		ArrayList settlementDetailsList = new ArrayList();
		ArrayList countList = new ArrayList();
		HashMap settlementMap = new HashMap();
		HashMap countMap = new HashMap();

		try {
			// If 'ALL' is selected
			if (bankId == null) {

				// SP for Application Details************
				CallableStatement allMemberDtls = connection
						.prepareCall("{?=call packGetDtlsForCLDANGen.funcGetDtlsForCLDANGen(?,?)}");

				allMemberDtls.registerOutParameter(1, Types.INTEGER);
				allMemberDtls.registerOutParameter(3, Types.VARCHAR); // Generate
																		// App
																		// Ref
																		// No

				allMemberDtls.registerOutParameter(2, Constants.CURSOR);

				allMemberDtls.execute();

				int functionReturnValue = allMemberDtls.getInt(1);
				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = allMemberDtls.getString(3);

					allMemberDtls.close();
					allMemberDtls = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ResultSet allMemberDtlsResults = (ResultSet) allMemberDtls
							.getObject(2);
					while (allMemberDtlsResults.next()) {

						SettlementDetail settlementDetail = new SettlementDetail();
						// SettlementDetail tempSettlementDetail=new
						// SettlementDetail();

						settlementDetail.setCgbid(allMemberDtlsResults
								.getString(2));
						settlementDetail
								.setTierOneSettlement(allMemberDtlsResults
										.getDouble(3));
						settlementDetail
								.setTierTwoSettlement(allMemberDtlsResults
										.getDouble(4));
						settlementDetail.setOsAmtAsonNPA(allMemberDtlsResults
								.getDouble(5));
						// settlementDetail.setApprovedClaimAmt(allMemberDtlsResults.getString(6));

						bankId = allMemberDtlsResults.getString(7);
						zoneId = allMemberDtlsResults.getString(8);
						branchId = allMemberDtlsResults.getString(9);
						String mliId = bankId + zoneId + branchId;
						settlementDetail.setMliId(mliId);

						if (allMemberDtlsResults.getInt(1) == 0) {
							settlementDetail
									.setApprovedClaimAmt(allMemberDtlsResults
											.getDouble(6));

							int cgpanCount = allMemberDtlsResults.getInt(10);
							Integer intCount = new Integer(cgpanCount);
							countMap.put(settlementDetail.getCgbid(), intCount);

						}
						if (allMemberDtlsResults.getInt(1) == 1) {
							if (settlementMap.containsKey(settlementDetail
									.getCgbid())) {
								settlementDetail = (SettlementDetail) settlementMap
										.get(settlementDetail.getCgbid());
								settlementDetail
										.setRecoveryAmt(allMemberDtlsResults
												.getDouble(6));
								// settlementMap.put(settlementDetail.getCgbid(),settlementDetail);
							}

						}
						// System.out.println("recovery Amount 3:" +
						// settlementDetail.getRecoveryAmt());
						// settlementDetailsList.add(settlementDetail);
						// System.out.println("recovery Amount 4:" +
						// settlementDetail.getRecoveryAmt());

						settlementMap.put(settlementDetail.getCgbid(),
								settlementDetail);

					}
					allMemberDtlsResults.close();
					allMemberDtlsResults = null;
					allMemberDtls.close();
					allMemberDtls = null;
				}
			} else {

				// SP for Application Details************
				CallableStatement allMemberDtls = connection
						.prepareCall("{?=call packGetDtlsForCLDANGenMemWise.funcGetDtlsForCLDANGenMemWise(?,?,?,?,?)}");

				allMemberDtls.registerOutParameter(1, Types.INTEGER);
				allMemberDtls.registerOutParameter(6, Types.VARCHAR); // Generate
																		// App
																		// Ref
																		// No
				allMemberDtls.registerOutParameter(5, Constants.CURSOR);

				allMemberDtls.setString(2, bankId);
				allMemberDtls.setString(3, zoneId);
				allMemberDtls.setString(4, branchId);

				allMemberDtls.execute();

				int functionReturnValue = allMemberDtls.getInt(1);
				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = allMemberDtls.getString(6);

					allMemberDtls.close();
					allMemberDtls = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ResultSet allMemberDtlsResults = (ResultSet) allMemberDtls
							.getObject(5);
					while (allMemberDtlsResults.next()) {
						SettlementDetail settlementDetail = new SettlementDetail();
						settlementDetail.setCgbid(allMemberDtlsResults
								.getString(2));
						settlementDetail
								.setTierOneSettlement(allMemberDtlsResults
										.getDouble(3));
						settlementDetail
								.setTierTwoSettlement(allMemberDtlsResults
										.getDouble(4));
						settlementDetail.setOsAmtAsonNPA(allMemberDtlsResults
								.getDouble(5));
						// settlementDetail.setApprovedClaimAmt(allMemberDtlsResults.getString(6));

						if (allMemberDtlsResults.getInt(1) == 0) {
							settlementDetail
									.setApprovedClaimAmt(allMemberDtlsResults
											.getDouble(6));
							int cgpanCount = allMemberDtlsResults.getInt(7);
							Integer intCount = new Integer(cgpanCount);
							countMap.put(settlementDetail.getCgbid(), intCount);

						}
						if (allMemberDtlsResults.getInt(1) == 1) {
							if (settlementMap.containsKey(settlementDetail
									.getCgbid())) {
								settlementDetail = (SettlementDetail) settlementMap
										.get(settlementDetail.getCgbid());
								settlementDetail
										.setRecoveryAmt(allMemberDtlsResults
												.getDouble(6));
								settlementMap.put(settlementDetail.getCgbid(),
										settlementDetail);
							}

						}
						settlementMap.put(settlementDetail.getCgbid(),
								settlementDetail);

					}
					allMemberDtlsResults.close();
					allMemberDtlsResults = null;
					allMemberDtls.close();
					allMemberDtls = null;
				}

			}
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Set settlementSet = settlementMap.keySet();
		Iterator settlementIterator = settlementSet.iterator();
		Set countSet = countMap.keySet();
		Iterator countIterator = countSet.iterator();

		while (settlementIterator.hasNext()) {
			SettlementDetail settlementDetail = new SettlementDetail();
			String cgbid = (String) settlementIterator.next();
			settlementDetail = (SettlementDetail) settlementMap.get(cgbid);
			settlementDetailsList.add(settlementDetail);
			countList.add(countMap.get(cgbid));
		}

		/*
		 * while(countIterator.hasNext()) { String
		 * cgbid=(String)settlementIterator.next(); //int
		 * count=((Integer)countMap.get(cgbid)).intValue();
		 * countList.add(countMap.get(cgbid)); }
		 */ArrayList settlementList = new ArrayList();
		settlementList.add(settlementDetailsList);
		settlementList.add(countList);

		return settlementList;

	}

	/**
	 * This method returns all the cgpans for a bid that's passed
	 * 
	 * @author SS14485
	 */
	public ArrayList getCgansForBID(String cgbid) throws DatabaseException {
		ArrayList cgpansForBidList = new ArrayList();
		// boolean cgpanAvailable=false;
		String cgpan = "";

		Connection connection = DBConnection.getConnection(false);

		try {
			CallableStatement cgpansForBid = connection
					.prepareCall("{?=call packGetCgpansForNpaBID.funcGetCgpansForNpaBID(?,?,?)}");

			cgpansForBid.registerOutParameter(1, Types.INTEGER);
			cgpansForBid.registerOutParameter(4, Types.VARCHAR);
			cgpansForBid.registerOutParameter(3, Constants.CURSOR);

			cgpansForBid.setString(2, cgbid);

			cgpansForBid.execute();

			int functionReturnValue = cgpansForBid.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = cgpansForBid.getString(4);

				cgpansForBid.close();
				cgpansForBid = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet cgpansForBidResults = (ResultSet) cgpansForBid
						.getObject(3);
				while (cgpansForBidResults.next()) {
					cgpan = cgpansForBidResults.getString(1);

					cgpansForBidList.add(cgpan);
				}
				cgpansForBidResults.close();
				cgpansForBidResults = null;
				cgpansForBid.close();
				cgpansForBid = null;
			}

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return cgpansForBidList;

	}

	/*
	 * This method returns a Vector of PendingDANDetail(s). Each
	 * PendingDANDetail object cotains DAN Details and CGPAN Details.
	 */

	public HashMap getMLIWiseDANDetails(String memberId)
			throws DatabaseException, MessageException {
		Log.log(Log.INFO, className, "getMLIWiseDANDetails", "Entered");
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		Connection conn = null;
		CallableStatement callableStmt = null;
		ResultSet rs = null;

		int status = -1;
		String errorCode = null;

		// Vector mliWiseDANDetails = new Vector();
		Vector nestedVector = new Vector();
		Vector pendingSfeeDtls = new Vector();
		HashMap details = null;
		HashMap sfeeDetail = null;
		HashMap completeDtls = new HashMap();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetPendingMLIWiseDANDtls.funcGetPendingMLIWiseDANDtls(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			// Executing the Callable Statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.ERROR, "RPDAO", "getMLIWiseDANDetails",
						"SP returns a 1 for funcGetPendingMLIWiseDANDtls SP. Error is :"
								+ errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				String danId = null;
				java.util.Date danGeneratedDate = null;
				String cgpan = null;
				double amountRaised = 0.0;
				double penalty = 0.0;
				double totalAmountRaised = 0.0;
				double totalPenalty = 0.0;

				// Retrieving the resultset
				rs = (ResultSet) callableStmt.getObject(5);
				String lastDANID = null;
				int counter = 0;
				while (rs.next()) {
					// Retrieving the values from the ResultSet
					danId = (String) rs.getString(1);
					danGeneratedDate = (java.util.Date) rs.getDate(2);
					cgpan = (String) rs.getString(3);
					amountRaised = (double) rs.getDouble(4);
					penalty = (double) rs.getDouble(5);

					if (danId != null) {
						if ((danId.indexOf(RpConstants.DAN_TYPE_SFDAN)) == 0) {
							sfeeDetail = new HashMap();
							sfeeDetail.put(RpConstants.RP_CGPAN, cgpan);
							// Log.log(Log.ERROR,"RPDAO","getMLIWiseDANDetails","cgpan :"
							// + cgpan);
							// Log.log(Log.ERROR,"RPDAO","getMLIWiseDANDetails","amountRaised + penalty :"
							// + amountRaised + penalty);
							sfeeDetail.put(RpConstants.RP_TOTAL_PENDING_AMNT,
									new Double(amountRaised + penalty));
							pendingSfeeDtls.addElement(sfeeDetail);
						}
					}

					if (counter == 0) {
						details = new HashMap();
						details.put(RpConstants.RP_DAN_ID, danId);
						details.put(RpConstants.RP_DAN_GEN_DATE,
								danGeneratedDate);
						details.put(RpConstants.RP_PENALTY, null);
						details.put(RpConstants.RP_CGPAN, null);
						details.put(RpConstants.RP_AMOUNT_RAISED, null);
						details.put(RpConstants.RP_TOTAL_AMOUNT_RAISED, null);

						// Add the DAN Detail to Nested Vector
						nestedVector.addElement(details);
						details = null;

						// Add the CGPAN Detail to Nested Vector
						details = new HashMap();
						details.put(RpConstants.RP_DAN_ID, null);
						details.put(RpConstants.RP_DAN_GEN_DATE, null);
						details.put(RpConstants.RP_PENALTY, null);
						details.put(RpConstants.RP_CGPAN, cgpan);
						details.put(RpConstants.RP_AMOUNT_RAISED, new Double(
								amountRaised));
						details.put(RpConstants.RP_TOTAL_AMOUNT_RAISED, null);
						nestedVector.addElement(details);
						totalAmountRaised = totalAmountRaised + amountRaised;
						totalPenalty = totalPenalty + penalty;
						details = null;
					} else if (counter > 0) {
						if (lastDANID.equals(danId)) {
							details = new HashMap();
							details.put(RpConstants.RP_DAN_ID, null);
							details.put(RpConstants.RP_DAN_GEN_DATE, null);
							details.put(RpConstants.RP_PENALTY, null);
							details.put(RpConstants.RP_CGPAN, cgpan);
							details.put(RpConstants.RP_AMOUNT_RAISED,
									new Double(amountRaised));
							details.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
									null);
							nestedVector.addElement(details);
							totalAmountRaised = totalAmountRaised
									+ amountRaised;
							totalPenalty = totalPenalty + penalty;
							details = null;
						} else if (!lastDANID.equals(danId)) {
							for (int i = 0; i < nestedVector.size(); i++) {
								HashMap temp = (HashMap) nestedVector
										.elementAt(i);
								if (temp == null) {
									continue;
								}
								String tempdanId = (String) temp
										.get(RpConstants.RP_DAN_ID);
								if (tempdanId != null) {
									if (tempdanId.equals(lastDANID)) {
										temp = (HashMap) nestedVector.remove(i);
										if (temp.containsKey(RpConstants.RP_TOTAL_AMOUNT_RAISED)) {
											temp.remove(RpConstants.RP_TOTAL_AMOUNT_RAISED);
											temp.put(
													RpConstants.RP_TOTAL_AMOUNT_RAISED,
													new Double(
															totalAmountRaised));
											temp.put(RpConstants.RP_PENALTY,
													new Double(totalPenalty));
											nestedVector.add(i, temp);
											totalAmountRaised = 0.0;
											totalPenalty = 0.0;
											break;
										}
									}
								}
							}

							// mliWiseDANDetails.addElement(nestedVector);
							// nestedVector = new Vector();

							details = new HashMap();
							details.put(RpConstants.RP_DAN_ID, danId);
							details.put(RpConstants.RP_DAN_GEN_DATE,
									danGeneratedDate);
							details.put(RpConstants.RP_PENALTY, null);
							details.put(RpConstants.RP_CGPAN, null);
							details.put(RpConstants.RP_AMOUNT_RAISED, null);
							details.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
									null);

							// Add the DAN Detail to Nested Vector
							nestedVector.addElement(details);
							details = null;

							// Add the CGPAN Detail to Nested Vector
							details = new HashMap();
							details.put(RpConstants.RP_DAN_ID, null);
							details.put(RpConstants.RP_DAN_GEN_DATE, null);
							details.put(RpConstants.RP_PENALTY, null);
							details.put(RpConstants.RP_CGPAN, cgpan);
							details.put(RpConstants.RP_AMOUNT_RAISED,
									new Double(amountRaised));
							details.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
									null);
							nestedVector.addElement(details);
							details = null;
							totalAmountRaised = totalAmountRaised
									+ amountRaised;
							totalPenalty = totalPenalty + penalty;
						}
					}
					lastDANID = danId;
					counter++;
				}
				rs.close();
				rs = null;
				callableStmt.close();
				callableStmt = null;
				// For storing the total amount raised for the last dan id
				for (int i = 0; i < nestedVector.size(); i++) {
					HashMap temp = (HashMap) nestedVector.elementAt(i);
					String tempdanId = (String) temp.get(RpConstants.RP_DAN_ID);
					if (tempdanId != null) {
						if (tempdanId.equals(lastDANID)) {
							temp = (HashMap) nestedVector.remove(i);
							if (temp.containsKey(RpConstants.RP_TOTAL_AMOUNT_RAISED)) {
								temp.remove(RpConstants.RP_TOTAL_AMOUNT_RAISED);
								temp.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
										new Double(totalAmountRaised));
								temp.put(RpConstants.RP_PENALTY, new Double(
										totalPenalty));
								nestedVector.add(i, temp);
								totalAmountRaised = 0.0;
								totalPenalty = 0.0;
								break;
							}
						}
					}
				}
				if (nestedVector.isEmpty()) {
					throw new MessageException("No DANs pending for Payment.");
				}
			}
		} catch (SQLException sqlexception) {
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		completeDtls.put(RpConstants.PENDING_DETAILS, nestedVector);
		completeDtls.put(RpConstants.PENDINGSFEE_DETAILS, pendingSfeeDtls);
		Log.log(Log.INFO, className, "getMLIWiseDANDetails", "Exited");
		return completeDtls;
	}

	/*
	 * This method returns a Vector of HashMap(s). Each HashMap contains Date
	 * Wise DAN Details.
	 */

	public Vector getDateWiseDANDetails(java.sql.Date fromDate,
			java.sql.Date toDate) throws DatabaseException, MessageException {
		Log.log(Log.INFO, className, "getDateWiseDANDetails", "Entered");
		Connection conn = null;
		CallableStatement callableStmt = null;
		ResultSet rs = null;

		int status = -1;
		String errorCode = null;

		Vector dateWiseDANDetails = new Vector();
		HashMap danDetail = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetPendingDateWiseDANDtls.funcGetPendingDateWiseDANDtls(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			if (fromDate == null) {
				callableStmt.setNull(2, java.sql.Types.DATE);
			} else {
				callableStmt.setDate(2, fromDate);
			}
			callableStmt.setDate(3, toDate);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			// Executing the Callable Statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.ERROR, "CPDAO", "getDateWiseDANDetails",
						"SP returns a 1 for funcGetPendingDateWiseDANDtls SP. Error is :"
								+ errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				String danId = null;
				String memberId = null;
				java.util.Date danGeneratedDt = null;
				String cgpan = null;
				double amountRaised = 0.0;
				double penalty = 0.0;
				String lastMemberId = null;
				String lastDanId = null;
				double totalAmountRaised = 0.0;
				double totalPenalty = 0.0;

				// Retrieving the resultset
				rs = (ResultSet) callableStmt.getObject(4);
				int counter = 0;

				while (rs.next()) {
					// Retrieving the values from the ResultSet
					memberId = (String) rs.getString(1);
					danId = (String) rs.getString(2);
					danGeneratedDt = (java.util.Date) rs.getDate(3);
					cgpan = (String) rs.getString(4);
					amountRaised = (double) rs.getDouble(5);
					penalty = (double) rs.getDouble(6);

					if (counter == 0) {
						// Populating the values in the HashMap
						danDetail = new HashMap();
						danDetail.put(RpConstants.RP_MEMBER_ID, memberId);
						danDetail.put(RpConstants.RP_DAN_ID, danId);
						danDetail.put(RpConstants.RP_DAN_GEN_DATE,
								danGeneratedDt);
						danDetail.put(RpConstants.RP_CGPAN, null);
						danDetail.put(RpConstants.RP_AMOUNT_RAISED, null);
						danDetail.put(RpConstants.RP_PENALTY, null);
						danDetail.put(RpConstants.RP_TOTAL_AMOUNT_RAISED, null);

						// Adding the HashMap
						dateWiseDANDetails.addElement(danDetail);
						danDetail = null;

						// Populating the values in the HashMap
						danDetail = new HashMap();
						danDetail.put(RpConstants.RP_MEMBER_ID, null);
						danDetail.put(RpConstants.RP_DAN_ID, null);
						danDetail.put(RpConstants.RP_DAN_GEN_DATE, null);
						danDetail.put(RpConstants.RP_CGPAN, cgpan);
						danDetail.put(RpConstants.RP_AMOUNT_RAISED, new Double(
								amountRaised));
						danDetail.put(RpConstants.RP_PENALTY, null);
						danDetail.put(RpConstants.RP_TOTAL_AMOUNT_RAISED, null);

						// Adding the HashMap
						dateWiseDANDetails.addElement(danDetail);
						danDetail = null;
						totalAmountRaised = totalAmountRaised + amountRaised;
						totalPenalty = totalPenalty + penalty;
					} else if (counter > 0) {
						if (memberId.equals(lastMemberId)) {
							if (danId.equals(lastDanId)) {
								// Populating the values in the HashMap
								danDetail = new HashMap();
								danDetail.put(RpConstants.RP_MEMBER_ID, null);
								danDetail.put(RpConstants.RP_DAN_ID, null);
								danDetail.put(RpConstants.RP_CGPAN, cgpan);
								danDetail.put(RpConstants.RP_AMOUNT_RAISED,
										new Double(amountRaised));
								danDetail.put(RpConstants.RP_PENALTY, null);
								danDetail.put(
										RpConstants.RP_TOTAL_AMOUNT_RAISED,
										null);

								// Adding the HashMap
								dateWiseDANDetails.addElement(danDetail);
								danDetail = null;
								totalAmountRaised = totalAmountRaised
										+ amountRaised;
								totalPenalty = totalPenalty + penalty;
							} else if (!danId.equals(lastDanId)) {
								for (int i = 0; i < dateWiseDANDetails.size(); i++) {
									HashMap temp = (HashMap) dateWiseDANDetails
											.elementAt(i);
									if (temp == null) {
										continue;
									}
									String tempdanId = (String) temp
											.get(RpConstants.RP_DAN_ID);
									if (tempdanId != null) {
										if (tempdanId.equals(lastDanId)) {
											temp = (HashMap) dateWiseDANDetails
													.remove(i);
											if (temp.containsKey(RpConstants.RP_TOTAL_AMOUNT_RAISED)) {
												temp.remove(RpConstants.RP_TOTAL_AMOUNT_RAISED);
												temp.put(
														RpConstants.RP_TOTAL_AMOUNT_RAISED,
														new Double(
																totalAmountRaised));
												temp.put(
														RpConstants.RP_PENALTY,
														new Double(totalPenalty));
												dateWiseDANDetails.add(i, temp);
												totalAmountRaised = 0.0;
												totalPenalty = 0.0;
												break;
											}
										}
									}
								}

								// Populating the values in the HashMap
								danDetail = new HashMap();
								danDetail.put(RpConstants.RP_MEMBER_ID, null);
								danDetail.put(RpConstants.RP_DAN_ID, danId);
								danDetail.put(RpConstants.RP_DAN_GEN_DATE,
										danGeneratedDt);
								danDetail.put(RpConstants.RP_CGPAN, null);
								danDetail.put(RpConstants.RP_AMOUNT_RAISED,
										null);
								danDetail.put(RpConstants.RP_PENALTY, null);
								danDetail.put(
										RpConstants.RP_TOTAL_AMOUNT_RAISED,
										null);

								// Adding the HashMap
								dateWiseDANDetails.addElement(danDetail);
								danDetail = null;

								// Populating the values in the HashMap
								danDetail = new HashMap();
								danDetail.put(RpConstants.RP_MEMBER_ID, null);
								danDetail.put(RpConstants.RP_DAN_ID, null);
								danDetail
										.put(RpConstants.RP_DAN_GEN_DATE, null);
								danDetail.put(RpConstants.RP_CGPAN, cgpan);
								danDetail.put(RpConstants.RP_AMOUNT_RAISED,
										new Double(amountRaised));
								danDetail.put(RpConstants.RP_PENALTY, null);
								danDetail.put(
										RpConstants.RP_TOTAL_AMOUNT_RAISED,
										null);

								// Adding the HashMap
								dateWiseDANDetails.addElement(danDetail);
								danDetail = null;
								totalAmountRaised = totalAmountRaised
										+ amountRaised;
								totalPenalty = totalPenalty + penalty;
							}
						} else if (!memberId.equals(lastMemberId)) {

							// Adding the Total Amount for the last Member Id
							for (int i = 0; i < dateWiseDANDetails.size(); i++) {
								HashMap temp = (HashMap) dateWiseDANDetails
										.elementAt(i);
								if (temp == null) {
									continue;
								}
								// String tempMemId =
								// (String)temp.get(RpConstants.RP_MEMBER_ID);
								String tempDanId = (String) temp
										.get(RpConstants.RP_DAN_ID);
								if (tempDanId != null) {
									if (tempDanId.equals(lastDanId)) {
										temp = (HashMap) dateWiseDANDetails
												.remove(i);
										if (temp.containsKey(RpConstants.RP_TOTAL_AMOUNT_RAISED)) {
											temp.remove(RpConstants.RP_TOTAL_AMOUNT_RAISED);
											temp.put(
													RpConstants.RP_TOTAL_AMOUNT_RAISED,
													new Double(
															totalAmountRaised));
											temp.put(RpConstants.RP_PENALTY,
													new Double(totalPenalty));
											dateWiseDANDetails.add(i, temp);
											totalAmountRaised = 0.0;
											totalPenalty = 0.0;
											break;
										}
									}
								}
							}

							// Populating the values in the HashMap
							danDetail = new HashMap();
							danDetail.put(RpConstants.RP_MEMBER_ID, memberId);
							danDetail.put(RpConstants.RP_DAN_ID, danId);
							danDetail.put(RpConstants.RP_DAN_GEN_DATE,
									danGeneratedDt);
							danDetail.put(RpConstants.RP_CGPAN, null);
							danDetail.put(RpConstants.RP_AMOUNT_RAISED, null);
							danDetail.put(RpConstants.RP_PENALTY, null);
							danDetail.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
									null);

							// Adding the HashMap
							dateWiseDANDetails.addElement(danDetail);
							danDetail = null;

							// Populating the values in the HashMap
							danDetail = new HashMap();
							danDetail.put(RpConstants.RP_MEMBER_ID, null);
							danDetail.put(RpConstants.RP_DAN_ID, null);
							danDetail.put(RpConstants.RP_DAN_GEN_DATE, null);
							danDetail.put(RpConstants.RP_CGPAN, cgpan);
							danDetail.put(RpConstants.RP_AMOUNT_RAISED,
									new Double(amountRaised));
							danDetail.put(RpConstants.RP_PENALTY, null);
							danDetail.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
									null);

							// Adding the HashMap
							dateWiseDANDetails.addElement(danDetail);
							danDetail = null;
							totalAmountRaised = totalAmountRaised
									+ amountRaised;
							totalPenalty = totalPenalty + penalty;
						}
					}
					counter++;
					lastMemberId = memberId;
					lastDanId = danId;
				}
				rs.close();
				rs = null;
				callableStmt.close();
				callableStmt = null;

				// For storing the total amount raised for the last dan id
				for (int i = 0; i < dateWiseDANDetails.size(); i++) {
					HashMap temp = (HashMap) dateWiseDANDetails.elementAt(i);
					String tempdanId = (String) temp.get(RpConstants.RP_DAN_ID);
					if (tempdanId != null) {
						if (tempdanId.equals(lastDanId)) {
							temp = (HashMap) dateWiseDANDetails.remove(i);
							if (temp.containsKey(RpConstants.RP_TOTAL_AMOUNT_RAISED)) {
								temp.remove(RpConstants.RP_TOTAL_AMOUNT_RAISED);
								temp.put(RpConstants.RP_TOTAL_AMOUNT_RAISED,
										new Double(totalAmountRaised));
								temp.put(RpConstants.RP_PENALTY, new Double(
										totalPenalty));
								dateWiseDANDetails.add(i, temp);
								totalAmountRaised = 0.0;
								totalPenalty = 0.0;
								break;
							}
						}
					}
				}
				if (dateWiseDANDetails.isEmpty()) {
					throw new MessageException("No DANs pending for Payment.");
				}
			}
		} catch (SQLException sqlexception) {
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, className, "getDateWiseDANDetails", "Exited");
		return dateWiseDANDetails;
	}

	public ArrayList getGLHeads() throws DatabaseException {
		String methodName = "getGLHeads";

		ArrayList glHeads = new ArrayList();

		Log.log(Log.INFO, className, methodName, "Entered");

		Connection connection = DBConnection.getConnection();
		try {
			CallableStatement callable = connection
					.prepareCall("{?=call packGetAllBasHeads.FUNCGETALLHEADS (?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.registerOutParameter(2, Constants.CURSOR);
			callable.registerOutParameter(3, Types.VARCHAR);

			callable.execute();

			int errorCode = callable.getInt(1);

			String error = callable.getString(3);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			ResultSet results = (ResultSet) callable.getObject(2);

			while (results.next()) {
				glHeads.add(results.getString(1));
			}

			results.close();
			results = null;
			callable.close();
			callable = null;
		} catch (SQLException e) {
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException("Unable to get GL Heads ");

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, className, methodName, "Exited");

		return glHeads;
	}

	/**
	 * This method displays all the details for Waive off Short DANs
	 * 
	 * @author SS14485
	 */
	public ArrayList showShortDansForWaive(String bankId, String zoneId,
			String branchId) throws DatabaseException

	{
		ArrayList shortDansList = new ArrayList();
		DemandAdvice demandAdvice = new DemandAdvice();

		Connection connection = DBConnection.getConnection(false);

		try {
			CallableStatement shortDansDtls = connection
					.prepareCall("{?=call packGetDtlsWaiveShortAmounts.funcGetDtlsWaiveShortAmounts(?,?,?,?,?)}");

			shortDansDtls.registerOutParameter(1, Types.INTEGER);
			shortDansDtls.registerOutParameter(6, Types.VARCHAR);
			shortDansDtls.registerOutParameter(5, Constants.CURSOR);

			shortDansDtls.setString(2, bankId);
			shortDansDtls.setString(3, zoneId);
			shortDansDtls.setString(4, branchId);
			shortDansDtls.execute();

			int functionReturnValue = shortDansDtls.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = shortDansDtls.getString(6);

				shortDansDtls.close();
				shortDansDtls = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet shortDansDtlsResults = (ResultSet) shortDansDtls
						.getObject(5);

				while (shortDansDtlsResults.next()) {
					demandAdvice = new DemandAdvice();

					demandAdvice.setDanNo(shortDansDtlsResults.getString(1));
					demandAdvice.setAmountRaised(shortDansDtlsResults
							.getDouble(2));
					demandAdvice.setDanGeneratedDate(DateHelper
							.sqlToUtilDate(shortDansDtlsResults.getDate(3)));
					demandAdvice.setDanDueDate(DateHelper
							.sqlToUtilDate(shortDansDtlsResults.getDate(4)));

					shortDansList.add(demandAdvice);

				}
				shortDansDtlsResults.close();
				shortDansDtlsResults = null;
				shortDansDtls.close();
				shortDansDtls = null;
			}

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return shortDansList;

	}

	public double getRefundAmountForMember(String mliId)
			throws DatabaseException {
		Log.log(Log.INFO, className, "getRefundAmountForMember", "Entered");
		Connection connection = null;
		CallableStatement stmt;
		double refAmt;

		try {
			String bnkId = mliId.substring(0, 4);
			String zneId = mliId.substring(4, 8);
			String brnId = mliId.substring(8, 12);
			Log.log(Log.INFO, className, "getRefundAmountForMember", "bank id "
					+ bnkId);
			Log.log(Log.INFO, className, "getRefundAmountForMember", "zone id "
					+ zneId);
			Log.log(Log.INFO, className, "getRefundAmountForMember",
					"branch id " + brnId);

			connection = DBConnection.getConnection();
			stmt = connection
					.prepareCall("{?=call packGetAllRefundAdvice.funcGetRefundAdvice(?,?,?,?,?)}");

			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(5, java.sql.Types.DOUBLE);
			stmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			stmt.setString(2, bnkId);
			stmt.setString(3, zneId);
			stmt.setString(4, brnId);

			stmt.execute();

			int status = stmt.getInt(1);
			Log.log(Log.INFO, className, "getRefundAmountForMember",
					"sp result " + status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(6);
				Log.log(Log.INFO, className, "getRefundAmountForMember",
						"err frm sp " + err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}

			refAmt = stmt.getDouble(5);
			Log.log(Log.INFO, className, "getRefundAmountForMember",
					"refund amt from sp " + refAmt);
			stmt.close();
			stmt = null;
			if (refAmt == 0.0) {
				throw new DatabaseException("No Refund Amount for member "
						+ mliId);
			}
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, "getRefundAmountForMember", "Exited");
		return refAmt;
	}

	public String generateRefundAdvice(String mliId, String userId)
			throws DatabaseException {
		Log.log(Log.INFO, className, "generateRefundAdvice", "Entered");
		Connection connection = null;
		CallableStatement stmt;
		String refAdvNumber;

		try {
			String bnkId = mliId.substring(0, 4);
			String zneId = mliId.substring(4, 8);
			String brnId = mliId.substring(8, 12);
			Log.log(Log.INFO, className, "generateRefundAdvice", "bank id "
					+ bnkId);
			Log.log(Log.INFO, className, "generateRefundAdvice", "zone id "
					+ zneId);
			Log.log(Log.INFO, className, "generateRefundAdvice", "branch id "
					+ brnId);

			connection = DBConnection.getConnection();
			stmt = connection.prepareCall("{?=call funcGenCGRFA(?,?)}");

			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();

			int status = stmt.getInt(1);
			Log.log(Log.INFO, className, "generateRefundAdvice", "sp result "
					+ status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(3);
				Log.log(Log.INFO, className, "generateRefundAdvice",
						"err frm sp " + err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}

			refAdvNumber = stmt.getString(2);
			Log.log(Log.INFO, className, "generateRefundAdvice",
					"refund number generated from sp " + refAdvNumber);
			stmt.close();
			stmt = null;

			stmt = connection
					.prepareCall("{?=call funcUpdRefundAdvice(?,?,?,?,?,?)}");

			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			stmt.setString(2, bnkId);
			stmt.setString(3, zneId);
			stmt.setString(4, brnId);
			stmt.setString(5, refAdvNumber);
			stmt.setString(6, userId);

			stmt.execute();

			status = stmt.getInt(1);
			Log.log(Log.INFO, className, "generateRefundAdvice",
					"update sp result " + status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(7);
				Log.log(Log.INFO, className, "generateRefundAdvice",
						"err frm update sp " + err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, "generateRefundAdvice", "Exited");
		return refAdvNumber;
	}

	public ArrayList calculateAllServiceFee(String bankId, String zoneId,
			String branchId, Connection connection) throws DatabaseException {
		String methodName = "calculateAllServiceFee";
		ArrayList liveApplications = null;
		ArrayList expiredApplications = null;
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Application application = null;
		ServiceFee serviceFee = null;

		int noOfApplications = 0;
		double serviceFeeRate = 0;
		double outstandingAmount = 0;
		double approvedAmount = 0;
		// double serviceFeeAmount = 0;//commented By sudeep.dhiman@pathinfotech
		// 12 dec 2006
		double serviceFeeId = 0;
		String cgpan;

		ArrayList serviceFeeList = new ArrayList();
		ArrayList sfCgpanList = new ArrayList();

		Log.log(Log.INFO, className, methodName, "Entering");
		// System.out.println(className+methodName+"Entered");

		Log.log(Log.DEBUG, className, methodName,
				" Before getting applications bankId,zoneId,branchId " + bankId
						+ "," + zoneId + "," + branchId);

		liveApplications = applicationDAO.getLiveApplications(bankId, zoneId,
				branchId);
		if (liveApplications == null) {

			Log.log(Log.INFO, className, methodName,
					"No live applications available");
			return null;
		}
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();
		serviceFeeRate = parameterMaster.getServiceFeeRate();
		// System.out.println("serviceFeeRate:"+serviceFeeRate);
		Log.log(Log.INFO, className, methodName, "Service fee rate = "
				+ serviceFeeRate);

		if (serviceFeeRate == 0) {
			Log.log(Log.INFO, className, methodName,
					"Service Fee Rate not available");
			return null;
		}

		noOfApplications = liveApplications.size();
		// System.out.println("No of Live Applications:"+noOfApplications);
		if (noOfApplications == 0) {
			// System.out.println(className+methodName+" No live applications available");
			Log.log(Log.INFO, className, methodName,
					"No live applications available");
			return null;
		}

		for (int i = 0; i < noOfApplications; ++i) {
			application = (Application) liveApplications.get(i);
			// System.out.println("Application:"+application);
			Log.log(Log.INFO, className, methodName,
					"Before calculating service fee for application:"
							+ application.getCgpan());
			if (application.getGuaranteeAmount() != 0) {
				// System.out.println("_________calling calculateServiceFee(application)in RPDAO");
				serviceFee = calculateServiceFee(application); // dd
				// System.out.println("serviceFee:"+serviceFee);
				Log.log(Log.INFO, className, methodName,
						"After calculating service fee for application:"
								+ application.getCgpan());
				if (serviceFee != null) {
					if (serviceFee.getServiceAmount() > 0) {
						// System.out.println("Insert Service Fee");
						serviceFeeId = insertServiceFee(serviceFee, connection);
						// String sfCgpan=serviceFee.getCgpan();
						sfCgpanList.add(serviceFee);
					}
				}

			}
		}

		return sfCgpanList;

	}

	/**
	 * Calculate service fee for expired cases --- added by sukumar@path on
	 * 21-08-2008
	 * 
	 * @param bankId
	 * @param zoneId
	 * @param branchId
	 * @param connection
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList calculateAllServiceFeeForExpiryCases(String bankId,
			String zoneId, String branchId, Connection connection)
			throws DatabaseException {
		String methodName = "calculateAllServiceFeeForExpiryCases";
		// System.out.println("Rpdao.java----calculateAllServiceFeeForExpiryCases entered");
		ArrayList expiredliveApplications = null;
		ArrayList expiredApplications = null;
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Application application = null;
		ServiceFee serviceFee = null;

		int noOfApplications = 0;
		double serviceFeeRate = 0;
		double outstandingAmount = 0;
		double approvedAmount = 0;
		double serviceFeeId = 0;
		String cgpan;

		ArrayList serviceFeeList = new ArrayList();
		ArrayList sfCgpanList = new ArrayList();

		Log.log(Log.INFO, className, methodName, "Entering");
		Log.log(Log.DEBUG, className, methodName,
				" Before getting applications bankId,zoneId,branchId " + bankId
						+ "," + zoneId + "," + branchId);

		expiredliveApplications = applicationDAO.getExpApplications(bankId,
				zoneId, branchId);
		if (expiredliveApplications == null) {
			// System.out.println("No live applications available- line number 10022");
			Log.log(Log.INFO, className, methodName,
					"No live applications available");
			return null;
		}
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();
		serviceFeeRate = parameterMaster.getServiceFeeRate();
		// System.out.println("serviceFeeRate:"+serviceFeeRate);
		Log.log(Log.INFO, className, methodName, "Service fee rate = "
				+ serviceFeeRate);

		if (serviceFeeRate == 0) {
			Log.log(Log.INFO, className, methodName,
					"Service Fee Rate not available");
			return null;
		}

		noOfApplications = expiredliveApplications.size();
		// System.out.println("No.of Expired Applications:"+noOfApplications);
		if (noOfApplications == 0) {
			// System.out.println(className+methodName+" No of Expired applications available");
			Log.log(Log.INFO, className, methodName,
					"No of Expired applications available");
			return null;
		}

		for (int i = 0; i < noOfApplications; ++i) {
			application = (Application) expiredliveApplications.get(i);
			// System.out.println("RPDAO.java"+"calculateAllServiceFeeForExpiryCases"+"Application CGPANs:"+application.getCgpan());
			Log.log(Log.INFO, className, methodName,
					"Before calculating service fee for application:"
							+ application.getCgpan());
			if (application.getGuaranteeAmount() != 0) {
				// System.out.println("_________calling calculateServiceFee(application)in RPDAO");
				// System.out.println("CGPAN---"+application.getCgpan());
				serviceFee = calculateServiceFeeForExpiry(application); // dd
				// System.out.println("serviceFee:"+serviceFee);
				Log.log(Log.INFO, className, methodName,
						"After calculating service fee for application:"
								+ application.getCgpan());
				if (serviceFee != null) {
					if (serviceFee.getServiceAmount() > 0) {
						// System.out.println("Insert Service Fee");
						serviceFeeId = insertServiceFee(serviceFee, connection);
						// String sfCgpan=serviceFee.getCgpan();
						sfCgpanList.add(serviceFee);
					}
				}

			}
		}

		return sfCgpanList;

	}

	/* -------------------- */

	/**
	 * This method calculates the service fee from the outstanding amount and
	 * the approved amount whichever is less. The method instanciates the
	 * ServiceFee object with the service fee amount and CGPAN as arguments and
	 * returns this ServiceFee object.
	 * 
	 * @return com.cgtsi.receiptspayments.ServiceFee
	 * @roseuid 394C55910249
	 */
	public ServiceFee calculateServiceFee(Application application)
			throws DatabaseException {
		String methodName = "calculateServiceFee";
		// System.out.println("_________Inside calculateServiceFee(Application application)"
		// );
		ServiceFee serviceFee = null;
		Administrator administrator = new Administrator();
		RpProcessor rpProcessor = new RpProcessor();
		ParameterMaster parameterMaster = administrator.getParameter();
		BorrowerDetails borrowerDetails = application.getBorrowerDetails();
		SSIDetails ssiDetails = borrowerDetails.getSsiDetails();

		RpDAO rpDAO = new RpDAO();
		// SubSchemeMaster subSchemeMaster = null ;
		Log.log(Log.INFO, className, methodName, "Entering");
		String cgpan = "";
		String ssiRef = "";
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8496"
		// );
		double outstandingAmount = 0;
		double approvedAmount = 0;
		double serviceFeeAmount = 0;
		double serviceFeeRate = 0;
		double serviceFeeCardRate = 0;

		int outstandingAmountCount = 0;
		/* ----------------- */
		// following parameter are added by sudeep.dhiman@pathinfotech.com,
		// for parametrization of calculation of serviceFeeRate
		/*
		 * String socialStatus =
		 * application.getBorrowerDetails().getSsiDetails().getSocialCategory();
		 * String sex =
		 * application.getBorrowerDetails().getSsiDetails().getCpGender();
		 * String district =
		 * application.getBorrowerDetails().getSsiDetails().getDistrict();
		 * String state =
		 * application.getBorrowerDetails().getSsiDetails().getState(); /*
		 * ----------------- following aatribute recalculated by
		 * sudeep.dhiman@pathinfotech.com on dec 22
		 */
		String socialStatus = application.getSocialCategory();
		String sex = application.getSex();
		String district = application.getDistrict();
		String state = application.getState();
		String schemeId = application.getScheme();

		// System.out.println("Line number 10418 SchemeId:"+schemeId);
		boolean serviceFeeCalculated = false;

		if (application == null) {
			return null;
		}
		// System.out.println("_________Inside calculateServiceFee(Application application) line 10124"
		// );
		String mcgfFlag = "";

		Log.log(Log.INFO, className, methodName, "application.getScheme() "
				+ application.getScheme());
		if (application.getScheme() != null
				&& application.getScheme().equals("MCGS")) {
			mcgfFlag = parameterMaster.getMcgfServiceFee();

			Log.log(Log.INFO, className, methodName, "mcgfFlag : " + mcgfFlag);
			if (mcgfFlag.equals("NO")) {
				return null;
			}
		}

		// System.out.println("_________Inside calculateServiceFee(Application application) line 8533"
		// );

		Calendar calendar = Calendar.getInstance();
		Date fromDate = null;
		Date toDate = null;
		Date asOnDate = null;
		// calendar.setTime(currentDate) ;

		int year = calendar.get(Calendar.YEAR);
		Log.log(Log.INFO, className, methodName, "Current Year = " + year);

		int month = calendar.get(Calendar.MONTH);
		Log.log(Log.INFO, className, methodName, "Current Month = " + month);

		int day = calendar.get(Calendar.DATE);
		Log.log(Log.INFO, className, methodName, "Current Date = " + day);

		String calendarMonth = parameterMaster.getServiceFeeCalculationMonth()
				.toUpperCase();

		calendar.set(Calendar.DATE,
				parameterMaster.getServiceFeeCalculationDay());
		// calendar.set(Calendar.MONTH, Calendar.MARCH) ;

		// System.out.println("_________Inside calculateServiceFee(Application application) line 10161"
		// );
		if (calendarMonth.equalsIgnoreCase("JANUARY")) {
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		} else if (calendarMonth.equalsIgnoreCase("FEBRUARY")) {
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		} else if (calendarMonth.equalsIgnoreCase("MARCH")) {
			calendar.set(Calendar.MONTH, Calendar.MARCH);
		} else if (calendarMonth.equalsIgnoreCase("APRIL")) {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
		} else if (calendarMonth.equalsIgnoreCase("MAY")) {
			calendar.set(Calendar.MONTH, Calendar.MAY);
		} else if (calendarMonth.equalsIgnoreCase("JUNE")) {
			calendar.set(Calendar.MONTH, Calendar.JUNE);
		} else if (calendarMonth.equalsIgnoreCase("JULY")) {
			calendar.set(Calendar.MONTH, Calendar.JULY);
		} else if (calendarMonth.equalsIgnoreCase("AUGUST")) {
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
		} else if (calendarMonth.equalsIgnoreCase("SEPTEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		} else if (calendarMonth.equalsIgnoreCase("OCTOBER")) {
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		} else if (calendarMonth.equalsIgnoreCase("NOVEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		} else if (calendarMonth.equalsIgnoreCase("DECEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		}

		if (month < calendar.get(Calendar.MONTH)) {
			year--;
		}

		calendar.set(Calendar.YEAR, year);

		toDate = calendar.getTime();
		asOnDate = calendar.getTime();
		Log.log(Log.INFO, className, methodName, "As On Date = " + asOnDate);

		// Process done only for applications != null.
		cgpan = application.getCgpan();
		// System.out.println("CGPAN:"+cgpan);
		ssiRef = application.getSsiRef();
		// System.out.println("SSI Reference Number:"+ssiRef);
		double tempApprovedAmount = getTotalSanctionedAmountNew(ssiRef);
		// System.out.println("tempApprovedAmount:"+tempApprovedAmount);
		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8621"
		// );
		// System.out.println("________CGPAN IS "+cgpan);
		// System.out.println("_________asOnDate "+asOnDate );
		// System.out.println("_________toDate "+toDate);
		if (rpProcessor.isServiceFeeCalculated(cgpan, asOnDate)) {
			return null;
		}
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8625"
		// );
		/*
		 * following line commented by sudeep.dhiman@pathinfotech.com this
		 * serviceFeeRate will be calculated a little later as we need
		 * approvedAmount to calculate serviceFeeRate, and approvedAmount
		 * calculated after few lines
		 */
		// serviceFeeRate = parameterMaster.getServiceFeeRate() ;

		Log.log(Log.INFO, className, methodName, "Service Fee Rate = "
				+ serviceFeeRate);

		Log.log(Log.INFO, className, methodName,
				"Before calling procedure to get outstanding amount");
		outstandingAmount = getAmountOutstanding(application, toDate);
		Log.log(Log.INFO, className, methodName,
				"After calling procedure to get outstanding amount");
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8636-OUTSTANDING AMOUNT"+outstandingAmount);
		if (outstandingAmount == -1) {
			outstandingAmountCount = getAmountOutstandingCount(application);
			Log.log(Log.INFO, className, methodName,
					"Outstanding Amount Count = " + outstandingAmountCount);

			if (outstandingAmountCount > 0) {
				for (int i = 1; i < outstandingAmountCount; ++i) {
					calendar.roll(Calendar.YEAR, false);
					outstandingAmount = getAmountOutstanding(application,
							calendar.getTime()); // application.getOutstandingAmount()
													// ;
					// System.out.println("outstandingAmount:"+outstandingAmount);
					if (outstandingAmount != -1) {
						break;
					}
				}
			}
		}
		Log.log(Log.INFO, className, methodName, "Amount Outstanding = "
				+ outstandingAmount);
		// System.out.println("Amount Outstanding = "+outstandingAmount);
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8652"
		// );

		Log.log(Log.INFO, className, methodName,
				"After getting approved amount");
		if (application.getReapprovedAmount() == 0) {
			approvedAmount = application.getApprovedAmount();
		} else {
			approvedAmount = application.getReapprovedAmount();
		}
		Log.log(Log.INFO, className, methodName,
				"After getting approved amount:" + approvedAmount);

		if (outstandingAmount == -1) {
			outstandingAmount = approvedAmount;
		}
		/*
		 * ================================ Calculation of serviceFeeRate has
		 * been changed by sudeep.dhiman@pathinfotech.com on 12 dec 2006
		 * previously the calculation is as serviceFeeRate =
		 * parameterMaster.getServiceFeeRate(),few line above the method
		 * getServiceRate(String,String,String,String,double) used below has
		 * been created newly. for calculating serviceFeeRate moreAccurately.
		 */
		// System.out.println("....... The value of Parameter are :"+socialStatus+"__"+sex+"___"+district+"__ "+state+"__"+approvedAmount+"------"+schemeId);

		if (application.getScheme() != null
				&& application.getScheme().equals("RSF")) {
			// System.out.println("getServiceRateForRSF");
			// serviceFeeRate = getServiceRateForRSF(socialStatus, sex,
			// district, state, approvedAmount, schemeId) ;
			serviceFeeRate = getServiceRateForRSF(socialStatus, sex, district,
					state, tempApprovedAmount, schemeId);

		} else {
			// serviceFeeRate = getServiceRate(socialStatus, sex, district,
			// state, approvedAmount) ;
			serviceFeeRate = getServiceRate(socialStatus, sex, district, state,
					tempApprovedAmount);

		}
		// ================================
		// System.out.println("................ servicerate is = "+serviceFeeRate);
		serviceFeeAmount = serviceFeeRate
				* Math.min(outstandingAmount, approvedAmount) / 100;

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();

		/**
		 * added this method for capturing the guarantee start date of the
		 * application Calculating Service Fee on ProRata Basis
		 * 
		 * @author sukumar@path
		 */
		Date guarStartDate = getGuarStartDate(application);

		date1.setTime(guarStartDate);
		date2.setTime(toDate);

		// Modifyed by bhupendra.jat@pathinfotech.com 12/10/2006
		long days = 0;

		// days=DateHelper.getDays(date1,date2);
		/**
		 * added by sukumar@path for calculating the difference between
		 * guarantee start date and toDate
		 */
		days = getDifferenceDays(guarStartDate, toDate);

		if (days <= 365) {
			serviceFeeAmount = serviceFeeRate
					* Math.min(outstandingAmount, approvedAmount) * days
					/ (100 * 365);
		}

		Log.log(Log.INFO, className, methodName, "Days = " + days);

		// Commented by bhooppandra.jat@pathinfotech.com on 12-10-2006

		/*
		 * long monthDiff = DateHelper.getMonthDifference(date1,date2);
		 * 
		 * Log.log(Log.INFO, className, methodName, "monthDiff = "+ monthDiff) ;
		 * 
		 * if(monthDiff<12) { serviceFeeAmount = serviceFeeRate *
		 * Math.min(outstandingAmount, approvedAmount)* monthDiff / (100 * 12);
		 * }
		 */
		// End of calculation of SFee on pro rata basis

		// End code by path on 12/10/2006

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		// servicefee instantiated with calculated serviceamount and cgpan

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, --year);
		fromDate = calendar.getTime();

		serviceFee = new ServiceFee(cgpan, Math.round(serviceFeeAmount));
		// System.out.println("CGPAN:"+cgpan+"Approved Amount:"+approvedAmount+"Days="+days+" Service Fee:"+serviceFeeAmount);
		serviceFee.setBankId(application.getBankId());
		serviceFee.setZoneId(application.getZoneId());
		serviceFee.setBranchId(application.getBranchId());
		serviceFee.setBorrowerId(ssiDetails.getCgbid());

		serviceFee.setFromDate(fromDate);
		Log.log(Log.INFO, className, methodName,
				"From Date = " + serviceFee.getFromDate());
		// System.out.println("serviceFee.getFromDate()"+serviceFee.getFromDate());
		serviceFee.setToDate(toDate);
		Log.log(Log.INFO, className, methodName,
				"To Date = " + serviceFee.getToDate());
		// System.out.println("To Date = "+serviceFee.getToDate());
		return serviceFee;

	}

	/*------------------------------------ */

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double calculateServiceFeeforCGPAN(String cgpan)
			throws DatabaseException {

		String methodName = "calculateServiceFeeforCGPAN";
		// System.out.println("_________Inside calculateServiceFee(Application application)"
		// );
		ServiceFee serviceFee = null;
		Administrator administrator = new Administrator();
		RpProcessor rpProcessor = new RpProcessor();
		ReportDAO reportDAO = new ReportDAO();
		Application application = new Application();
		application.setCgpan(cgpan);
		ParameterMaster parameterMaster = administrator.getParameter();
		ApplicationReport applicationReport = reportDAO
				.applicationReportNew(cgpan);
		RpDAO rpDAO = new RpDAO();
		// SubSchemeMaster subSchemeMaster = null ;
		Log.log(Log.INFO, className, methodName, "Entering");
		String ssiRef = "";
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8496"
		// );
		double outstandingAmount = 0;
		double approvedAmount = 0;
		double serviceFeeAmount = 0;
		double serviceFeeRate = 0;
		double serviceFeeCardRate = 0;

		int outstandingAmountCount = 0;
		/* ----------------- */
		String socialStatus = null;
		String sex = applicationReport.getGender();
		String district = applicationReport.getDistrict();
		String state = applicationReport.getState();
		String schemeId = applicationReport.getAppSubsidySchemeName();

		// System.out.println("Line number 10418 SchemeId:"+schemeId);
		boolean serviceFeeCalculated = false;

		Calendar calendar = Calendar.getInstance();
		Date fromDate = null;
		Date toDate = null;
		Date asOnDate = null;
		Date endDate = null;
		// calendar.setTime(currentDate) ;

		int year = calendar.get(Calendar.YEAR);
		Log.log(Log.INFO, className, methodName, "Current Year = " + year);

		int month = calendar.get(Calendar.MONTH);
		Log.log(Log.INFO, className, methodName, "Current Month = " + month);

		int day = calendar.get(Calendar.DATE);
		Log.log(Log.INFO, className, methodName, "Current Date = " + day);

		endDate = calendar.getTime();

		final int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);

		// endDate.e

		/*
		 * int month1=month+1;
		 * 
		 * 
		 * String i=year+""+month1+""+day;
		 * 
		 * 
		 * 
		 * System.out.println("End Date:"+endDate);
		 * 
		 * 
		 * 
		 * if(i.equals("2013820")||(i.equals("2013821"))||(i.equals("2013822"))||
		 * (i.equals("2013824"))||(i.equals("2013827"))||(i.equals("2013829")))
		 * 
		 * 
		 * {
		 * 
		 * calendar.set(Calendar.DATE, 29) ; calendar.set(Calendar.MONTH,
		 * Calendar.AUGUST) ; calendar.set(Calendar.YEAR, 2013) ; fromDate =
		 * calendar.getTime() ;
		 * 
		 * 
		 * endDate = calendar.getTime();
		 * 
		 * 
		 * System.out.println(endDate); }
		 * 
		 * 
		 * else {
		 * 
		 * int year1 = calendar.get(Calendar.YEAR) ;
		 * 
		 * System.out.println(year);
		 * 
		 * int month2 = calendar.get(Calendar.MONTH)+2 ;
		 * 
		 * System.out.println(month);
		 * 
		 * int day3 = calendar.get(Calendar.DATE) ;
		 * 
		 * System.out.println(day);
		 * 
		 * // String i=year1+""+month2+""+day3;
		 * 
		 * System.out.println(i); endDate = calendar.getTime();
		 * 
		 * 
		 * }
		 */

		String calendarMonth = parameterMaster.getServiceFeeCalculationMonth()
				.toUpperCase();

		calendar.set(Calendar.DATE,
				parameterMaster.getServiceFeeCalculationDay());
		// calendar.set(Calendar.MONTH, Calendar.MARCH) ;

		// System.out.println("_________Inside calculateServiceFee(Application application) line 10161"
		// );
		if (calendarMonth.equalsIgnoreCase("JANUARY")) {
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		} else if (calendarMonth.equalsIgnoreCase("FEBRUARY")) {
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		} else if (calendarMonth.equalsIgnoreCase("MARCH")) {
			calendar.set(Calendar.MONTH, Calendar.MARCH);
		} else if (calendarMonth.equalsIgnoreCase("APRIL")) {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
		} else if (calendarMonth.equalsIgnoreCase("MAY")) {
			calendar.set(Calendar.MONTH, Calendar.MAY);
		} else if (calendarMonth.equalsIgnoreCase("JUNE")) {
			calendar.set(Calendar.MONTH, Calendar.JUNE);
		} else if (calendarMonth.equalsIgnoreCase("JULY")) {
			calendar.set(Calendar.MONTH, Calendar.JULY);
		} else if (calendarMonth.equalsIgnoreCase("AUGUST")) {
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
		} else if (calendarMonth.equalsIgnoreCase("SEPTEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		} else if (calendarMonth.equalsIgnoreCase("OCTOBER")) {
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		} else if (calendarMonth.equalsIgnoreCase("NOVEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		} else if (calendarMonth.equalsIgnoreCase("DECEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		}

		if (month <= calendar.get(Calendar.MONTH)) {
			year--;
		}

		calendar.set(Calendar.YEAR, year);

		toDate = calendar.getTime();
		asOnDate = calendar.getTime();
		Log.log(Log.INFO, className, methodName, "As On Date = " + asOnDate);

		// Process done only for applications != null.
		ssiRef = applicationReport.getSsiReferenceNumber();
		// System.out.println("SSI Reference Number:"+ssiRef);
		double tempApprovedAmount = getTotalSanctionedAmountNew(ssiRef);
		// System.out.println("tempApprovedAmount:"+tempApprovedAmount);
		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);

		/*
		 * if(rpProcessor.isServiceFeeCalculated(cgpan, asOnDate)) { return 0; }
		 */

		Log.log(Log.INFO, className, methodName, "Service Fee Rate = "
				+ serviceFeeRate);

		Log.log(Log.INFO, className, methodName,
				"Before calling procedure to get outstanding amount");
		// outstandingAmount = getAmountOutstanding(application, toDate) ;
		Log.log(Log.INFO, className, methodName,
				"After calling procedure to get outstanding amount");
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8636-OUTSTANDING AMOUNT"+outstandingAmount);

		approvedAmount = Double.parseDouble(applicationReport
				.getAppApprovedAmount());

		Log.log(Log.INFO, className, methodName,
				"After getting approved amount:" + approvedAmount);

		if (outstandingAmount == -1) {
			outstandingAmount = approvedAmount;
		}

		if (applicationReport.getAppSubsidySchemeName() != null
				&& applicationReport.getAppSubsidySchemeName().equals("RSF")) {
			// serviceFeeRate = getServiceRateForRSF(socialStatus, sex,
			// district, state, approvedAmount, schemeId) ;
			serviceFeeRate = getServiceRateForRSF(socialStatus, sex, district,
					state, tempApprovedAmount, schemeId);

		} else {
			// serviceFeeRate = getServiceRate(socialStatus, sex, district,
			// state, approvedAmount) ;
			serviceFeeRate = getServiceRate(socialStatus, sex, district, state,
					tempApprovedAmount);

		}
		// ================================

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();

		/**
		 * added this method for capturing the guarantee start date of the
		 * application Calculating Service Fee on ProRata Basis
		 * 
		 * @author sukumar@path
		 */
		Date guarStartDate = getGuarStartDate(application);

		System.out.println("guarStartDate" + guarStartDate);

		date1.setTime(guarStartDate);
		date2.setTime(endDate);

		// Modifyed by bhupendra.jat@pathinfotech.com 12/10/2006
		long days = 0;

		// days=DateHelper.getDays(date1,date2);
		/**
		 * added by sukumar@path for calculating the difference between
		 * guarantee start date and toDate
		 */
		days = getDifferenceDays(guarStartDate, endDate);

		// added by koty
		// long noofdays = days;

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, year);
		fromDate = calendar.getTime();
		System.out.println("fromDate:" + fromDate + "endDate:" + endDate
				+ "days:" + days);

		if (days <= 365) {
			switch (dayofweek) {

			case 1:
				days = days + 4;
				break;
			case 2:
				days = days + 3;
				break;
			case 3:
				days = days + 2;
				break;
			case 4:
				days = days + 1;
				break;
			case 5:
				days = days + 7;
				break;
			case 6:
				days = days + 6;
				break;
			case 7:
				days = days + 5;
				break;

			}
			serviceFeeAmount = serviceFeeRate * approvedAmount * days
					/ (100 * 365);
		} else if (days > 365) {
			days = getDifferenceDays(fromDate, endDate);
			if ((days <= 365)) {
				switch (dayofweek) {

				case 1:
					days = days + 4;
					break;
				case 2:
					days = days + 3;
					break;
				case 3:
					days = days + 2;
					break;
				case 4:
					days = days + 1;
					break;
				case 5:
					days = days + 7;
					break;
				case 6:
					days = days + 6;
					break;
				case 7:
					days = days + 5;
					break;

				}
				serviceFeeAmount = serviceFeeRate * approvedAmount * days
						/ (100 * 365);
			} else {
				serviceFeeAmount = serviceFeeRate * approvedAmount / 100;
			}
		}

		Log.log(Log.INFO, className, methodName, "Days = " + days);

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		// servicefee instantiated with calculated serviceamount and cgpan

		serviceFee = new ServiceFee(cgpan, Math.round(serviceFeeAmount));
		String memberId = applicationReport.getMemberId();

		// System.out.println("CGPAN:"+cgpan+"Approved Amount:"+approvedAmount+"Days="+days+" Service Fee:"+Math.round(serviceFeeAmount));
		serviceFee.setBankId(memberId.substring(0, 4));
		serviceFee.setZoneId(memberId.substring(4, 8));
		serviceFee.setBranchId(memberId.substring(8, 12));
		serviceFee.setBorrowerId(applicationReport.getBid());

		serviceFee.setFromDate(fromDate);
		Log.log(Log.INFO, className, methodName,
				"From Date = " + serviceFee.getFromDate());
		// System.out.println("serviceFee.getFromDate()"+serviceFee.getFromDate());
		serviceFee.setToDate(toDate);
		Log.log(Log.INFO, className, methodName,
				"To Date = " + serviceFee.getToDate());
		// System.out.println("To Date = "+serviceFee.getToDate());
		return Math.round(serviceFeeAmount);

	}

	public double calculateserviceFeeForAsfDeduct(String cgpan)
			throws DatabaseException {
		String methodName = "calculateServiceFeeforCGPAN";
		// System.out.println("_________Inside calculateServiceFee(Application application)"
		// );
		ServiceFee serviceFee = null;
		Administrator administrator = new Administrator();
		RpProcessor rpProcessor = new RpProcessor();
		ReportDAO reportDAO = new ReportDAO();
		Application application = new Application();
		application.setCgpan(cgpan);
		ParameterMaster parameterMaster = administrator.getParameter();
		ApplicationReport applicationReport = reportDAO
				.applicationReportNew(cgpan);
		RpDAO rpDAO = new RpDAO();
		// SubSchemeMaster subSchemeMaster = null ;
		Log.log(Log.INFO, className, methodName, "Entering");
		String ssiRef = "";
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8496"
		// );
		double outstandingAmount = 0;
		double approvedAmount = 0;
		double serviceFeeAmount = 0;
		double serviceFeeRate = 0;
		double serviceFeeCardRate = 0;

		int outstandingAmountCount = 0;
		/* ----------------- */
		String socialStatus = null;
		String sex = applicationReport.getGender();
		String district = applicationReport.getDistrict();
		String state = applicationReport.getState();
		String schemeId = applicationReport.getAppSubsidySchemeName();

		// System.out.println("Line number 10418 SchemeId:"+schemeId);
		boolean serviceFeeCalculated = false;

		Calendar calendar = Calendar.getInstance();
		Date fromDate = null;
		Date toDate = null;
		Date asOnDate = null;
		Date endDate = null;
		// calendar.setTime(currentDate) ;

		int year = calendar.get(Calendar.YEAR);
		Log.log(Log.INFO, className, methodName, "Current Year = " + year);

		int month = calendar.get(Calendar.MONTH);
		Log.log(Log.INFO, className, methodName, "Current Month = " + month);

		int day = calendar.get(Calendar.DATE);
		Log.log(Log.INFO, className, methodName, "Current Date = " + day);

		endDate = calendar.getTime();
		System.out.println("End Date:" + endDate);

		final int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);

		String calendarMonth = parameterMaster.getServiceFeeCalculationMonth()
				.toUpperCase();

		calendar.set(Calendar.DATE,
				parameterMaster.getServiceFeeCalculationDay());
		// calendar.set(Calendar.MONTH, Calendar.MARCH) ;

		// System.out.println("_________Inside calculateServiceFee(Application application) line 10161"
		// );
		if (calendarMonth.equalsIgnoreCase("JANUARY")) {
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		} else if (calendarMonth.equalsIgnoreCase("FEBRUARY")) {
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		} else if (calendarMonth.equalsIgnoreCase("MARCH")) {
			calendar.set(Calendar.MONTH, Calendar.MARCH);
		} else if (calendarMonth.equalsIgnoreCase("APRIL")) {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
		} else if (calendarMonth.equalsIgnoreCase("MAY")) {
			calendar.set(Calendar.MONTH, Calendar.MAY);
		} else if (calendarMonth.equalsIgnoreCase("JUNE")) {
			calendar.set(Calendar.MONTH, Calendar.JUNE);
		} else if (calendarMonth.equalsIgnoreCase("JULY")) {
			calendar.set(Calendar.MONTH, Calendar.JULY);
		} else if (calendarMonth.equalsIgnoreCase("AUGUST")) {
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
		} else if (calendarMonth.equalsIgnoreCase("SEPTEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		} else if (calendarMonth.equalsIgnoreCase("OCTOBER")) {
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		} else if (calendarMonth.equalsIgnoreCase("NOVEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		} else if (calendarMonth.equalsIgnoreCase("DECEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		}

		if (month <= calendar.get(Calendar.MONTH)) {
			year--;
		}

		calendar.set(Calendar.YEAR, year);

		toDate = calendar.getTime();
		asOnDate = calendar.getTime();
		Log.log(Log.INFO, className, methodName, "As On Date = " + asOnDate);

		// Process done only for applications != null.
		ssiRef = applicationReport.getSsiReferenceNumber();
		// System.out.println("SSI Reference Number:"+ssiRef);
		double tempApprovedAmount = getTotalSanctionedAmountNew(ssiRef);
		// System.out.println("tempApprovedAmount:"+tempApprovedAmount);
		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);

		/*
		 * if(rpProcessor.isServiceFeeCalculated(cgpan, asOnDate)) { return 0; }
		 */

		Log.log(Log.INFO, className, methodName, "Service Fee Rate = "
				+ serviceFeeRate);

		Log.log(Log.INFO, className, methodName,
				"Before calling procedure to get outstanding amount");
		// outstandingAmount = getAmountOutstanding(application, toDate) ;
		Log.log(Log.INFO, className, methodName,
				"After calling procedure to get outstanding amount");
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8636-OUTSTANDING AMOUNT"+outstandingAmount);

		approvedAmount = Double.parseDouble(applicationReport
				.getAppApprovedAmount());

		Log.log(Log.INFO, className, methodName,
				"After getting approved amount:" + approvedAmount);

		if (outstandingAmount == -1) {
			outstandingAmount = approvedAmount;
		}

		if (applicationReport.getAppSubsidySchemeName() != null
				&& applicationReport.getAppSubsidySchemeName().equals("RSF")) {
			// serviceFeeRate = getServiceRateForRSF(socialStatus, sex,
			// district, state, approvedAmount, schemeId) ;
			serviceFeeRate = getServiceRateForRSF(socialStatus, sex, district,
					state, tempApprovedAmount, schemeId);

		} else {
			// serviceFeeRate = getServiceRate(socialStatus, sex, district,
			// state, approvedAmount) ;
			serviceFeeRate = getServiceRate(socialStatus, sex, district, state,
					tempApprovedAmount);

		}
		// ================================

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();

		/**
		 * added this method for capturing the guarantee start date of the
		 * application Calculating Service Fee on ProRata Basis
		 * 
		 * @author sukumar@path
		 */
		Date guarStartDate = getGuarStartDate(application);

		System.out.println("guarStartDate" + guarStartDate);

		date1.setTime(guarStartDate);
		date2.setTime(endDate);

		// Modifyed by bhupendra.jat@pathinfotech.com 12/10/2006
		long days = 0;

		// days=DateHelper.getDays(date1,date2);
		/**
		 * added by sukumar@path for calculating the difference between
		 * guarantee start date and toDate
		 */
		days = getDifferenceDays(guarStartDate, endDate);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, year);
		fromDate = calendar.getTime();
		System.out.println("fromDate:" + fromDate + "endDate:" + endDate
				+ "days:" + days);

		// if(days<=365)
		// {
		// serviceFeeAmount = serviceFeeRate * approvedAmount* days / (100 *
		// 365);
		// }
		// else if(days>365)
		// {
		// days = getDifferenceDays(fromDate,endDate);
		// if((days<=365)){
		// serviceFeeAmount = serviceFeeRate * approvedAmount* days / (100 *
		// 365);
		// }
		// else{
		serviceFeeAmount = serviceFeeRate * approvedAmount / 100;
		// }
		// }

		Log.log(Log.INFO, className, methodName, "Days = " + days);

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		// servicefee instantiated with calculated serviceamount and cgpan

		serviceFee = new ServiceFee(cgpan, Math.round(serviceFeeAmount));
		String memberId = applicationReport.getMemberId();

		// System.out.println("CGPAN:"+cgpan+"Approved Amount:"+approvedAmount+"Days="+days+" Service Fee:"+Math.round(serviceFeeAmount));
		serviceFee.setBankId(memberId.substring(0, 4));
		serviceFee.setZoneId(memberId.substring(4, 8));
		serviceFee.setBranchId(memberId.substring(8, 12));
		serviceFee.setBorrowerId(applicationReport.getBid());

		serviceFee.setFromDate(fromDate);
		Log.log(Log.INFO, className, methodName,
				"From Date = " + serviceFee.getFromDate());
		// System.out.println("serviceFee.getFromDate()"+serviceFee.getFromDate());
		serviceFee.setToDate(toDate);
		Log.log(Log.INFO, className, methodName,
				"To Date = " + serviceFee.getToDate());
		// System.out.println("To Date = "+serviceFee.getToDate());
		return Math.round(serviceFeeAmount);

	}

	/* --------------------------------- */

	/**
	 * Calculate Service Fee For Expired Cases
	 * 
	 * @param application
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ServiceFee calculateServiceFeeForExpiry(Application application)
			throws DatabaseException {
		String methodName = "calculateServiceFeeForExpiry";
		// System.out.println("_________Inside calculateServiceFeeForExpiry(Application application)"
		// );
		ServiceFee serviceFee = null;
		Administrator administrator = new Administrator();
		RpProcessor rpProcessor = new RpProcessor();
		ParameterMaster parameterMaster = administrator.getParameter();
		BorrowerDetails borrowerDetails = application.getBorrowerDetails();
		SSIDetails ssiDetails = borrowerDetails.getSsiDetails();

		RpDAO rpDAO = new RpDAO();
		Log.log(Log.INFO, className, methodName, "Entering");
		String cgpan = "";
		String ssiRef = "";
		// System.out.println("_________Inside calculateServiceFee(Application application) line 8496"
		// );
		double outstandingAmount = 0;
		double approvedAmount = 0;
		double serviceFeeAmount = 0;
		double serviceFeeRate = 0;
		double serviceFeeCardRate = 0;

		int outstandingAmountCount = 0;

		String socialStatus = application.getSocialCategory();
		String sex = application.getSex();
		String district = application.getDistrict();
		String state = application.getState();

		// System.out.println("application.getSocialCategory():"+application.getSocialCategory());
		// System.out.println("application.getSex()"+application.getSex());
		// System.out.println("application.getDistrict():"+application.getDistrict());
		// System.out.println("application.getState():"+application.getState());
		boolean serviceFeeCalculated = false;

		if (application == null) {
			return null;
		}
		String mcgfFlag = "";

		Log.log(Log.INFO, className, methodName, "application.getScheme() "
				+ application.getScheme());
		// System.out.println("application.getScheme():"+application.getScheme());
		if (application.getScheme() != null
				&& application.getScheme().equals("MCGS")) {
			mcgfFlag = parameterMaster.getMcgfServiceFee();
			// System.out.println("mcgfFlag:"+mcgfFlag);
			Log.log(Log.INFO, className, methodName, "mcgfFlag : " + mcgfFlag);
			if (mcgfFlag.equals("NO")) {
				return null;
			}
		}
		Calendar calendar = Calendar.getInstance();
		Date fromDate = null;
		Date toDate = null;
		Date asOnDate = null;
		// calendar.setTime(currentDate) ;

		int year = calendar.get(Calendar.YEAR);
		Log.log(Log.INFO, className, methodName, "Current Year = " + year);

		int month = calendar.get(Calendar.MONTH);
		Log.log(Log.INFO, className, methodName, "Current Month = " + month);

		int day = calendar.get(Calendar.DATE);
		Log.log(Log.INFO, className, methodName, "Current Date = " + day);

		String calendarMonth = parameterMaster.getServiceFeeCalculationMonth()
				.toUpperCase();

		calendar.set(Calendar.DATE,
				parameterMaster.getServiceFeeCalculationDay());
		// calendar.set(Calendar.MONTH, Calendar.MARCH) ;

		if (calendarMonth.equalsIgnoreCase("JANUARY")) {
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		} else if (calendarMonth.equalsIgnoreCase("FEBRUARY")) {
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		} else if (calendarMonth.equalsIgnoreCase("MARCH")) {
			calendar.set(Calendar.MONTH, Calendar.MARCH);
		} else if (calendarMonth.equalsIgnoreCase("APRIL")) {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
		} else if (calendarMonth.equalsIgnoreCase("MAY")) {
			calendar.set(Calendar.MONTH, Calendar.MAY);
		} else if (calendarMonth.equalsIgnoreCase("JUNE")) {
			calendar.set(Calendar.MONTH, Calendar.JUNE);
		} else if (calendarMonth.equalsIgnoreCase("JULY")) {
			calendar.set(Calendar.MONTH, Calendar.JULY);
		} else if (calendarMonth.equalsIgnoreCase("AUGUST")) {
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
		} else if (calendarMonth.equalsIgnoreCase("SEPTEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		} else if (calendarMonth.equalsIgnoreCase("OCTOBER")) {
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		} else if (calendarMonth.equalsIgnoreCase("NOVEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		} else if (calendarMonth.equalsIgnoreCase("DECEMBER")) {
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		}

		if (month < calendar.get(Calendar.MONTH)) {
			year--;
			// System.out.println("Line number-Year--:"+year);
		}

		calendar.set(Calendar.YEAR, year);

		toDate = calendar.getTime();
		asOnDate = calendar.getTime();
		Log.log(Log.INFO, className, methodName, "As On Date = " + asOnDate);

		// Process done only for applications != null.
		cgpan = application.getCgpan();
		// System.out.println("CGPAN:"+application.getCgpan());

		ssiRef = application.getSsiRef();
		// System.out.println("SSI Reference Number:"+ssiRef);
		double tempApprovedAmount = getTotalSanctionedAmountNew(ssiRef);

		Log.log(Log.INFO, className, methodName, "CGPAN = " + cgpan);
		// System.out.println("_________Inside calculateServiceFee(Application application) line 10504"
		// );
		// System.out.println("________CGPAN IS "+cgpan);
		// System.out.println("_________asOnDate "+asOnDate );
		if (rpProcessor.isServiceFeeCalculated(cgpan, asOnDate)) {
			return null;
		}
		// System.out.println("_________Inside calculateServiceFee(Application application) line 10511"
		// );

		Log.log(Log.INFO, className, methodName, "Service Fee Rate = "
				+ serviceFeeRate);
		Log.log(Log.INFO, className, methodName,
				"Before calling procedure to get outstanding amount");
		outstandingAmount = getAmountOutstanding(application, toDate);
		Log.log(Log.INFO, className, methodName,
				"After calling procedure to get outstanding amount");
		// System.out.println("_________Inside calculateServiceFee(Application application) line 10514-OUTSTANDING AMOUNT"+outstandingAmount);
		if (outstandingAmount == -1) {
			outstandingAmountCount = getAmountOutstandingCount(application);
			Log.log(Log.INFO, className, methodName,
					"Outstanding Amount Count = " + outstandingAmountCount);

			if (outstandingAmountCount > 0) {
				for (int i = 1; i < outstandingAmountCount; ++i) {
					calendar.roll(Calendar.YEAR, false);
					outstandingAmount = getAmountOutstanding(application,
							calendar.getTime()); // application.getOutstandingAmount()
													// ;
					// System.out.println("outstandingAmount:"+outstandingAmount);
					if (outstandingAmount != -1) {
						break;
					}
				}
			}
		}
		Log.log(Log.INFO, className, methodName, "Amount Outstanding = "
				+ outstandingAmount);
		// System.out.println("Amount Outstanding = "+outstandingAmount);
		// System.out.println("_________Inside calculateServiceFee(Application application) line 10532"
		// );

		Log.log(Log.INFO, className, methodName,
				"After getting approved amount");
		if (application.getReapprovedAmount() == 0) {
			approvedAmount = application.getApprovedAmount();
		} else {
			approvedAmount = application.getReapprovedAmount();
		}
		Log.log(Log.INFO, className, methodName,
				"After getting approved amount:" + approvedAmount);

		if (outstandingAmount == -1) {
			outstandingAmount = approvedAmount;
		}
		/*
		 * ================================ Calculation of serviceFeeRate has
		 * been changed by sudeep.dhiman@pathinfotech.com on 12 dec 2006
		 * previously the calculation is as serviceFeeRate =
		 * parameterMaster.getServiceFeeRate(),few line above the method
		 * getServiceRate(String,String,String,String,double) used below has
		 * been created newly. for calculating serviceFeeRate moreAccurately.
		 */
		// System.out.println("....... The value of Parameter are :"+socialStatus+"__"+sex+"___"+district+"__ "+state+"__"+approvedAmount);
		// serviceFeeRate = getServiceRate(socialStatus, sex, district, state,
		// approvedAmount) ;
		serviceFeeRate = getServiceRate(socialStatus, sex, district, state,
				tempApprovedAmount);
		// ================================
		// System.out.println("................ servicerate is = "+serviceFeeRate);
		serviceFeeAmount = serviceFeeRate
				* Math.min(outstandingAmount, approvedAmount) / 100;

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();

		/**
		 * Calculating Service Fee on ProRata Basis
		 */
		Date guarStartDate = getGuarStartDate(application);
		Date expiryDate = getExpiryDate(application);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.MONTH, Calendar.APRIL);
		calendar2.set(Calendar.DATE, 1);
		calendar2.set(Calendar.YEAR, 2008);
		Date tempDate = calendar2.getTime();
		// System.out.println("Temporary Date:"+tempDate);
		if (tempDate.compareTo(guarStartDate) > 0) {
			guarStartDate = tempDate;

			// System.out.println("Temp date2:"+tempDate);
		}

		date1.setTime(guarStartDate);
		// System.out.println("guarStartDate:"+guarStartDate);
		date2.setTime(expiryDate);
		// System.out.println("expiryDate:"+expiryDate);

		// Modifyed by bhupendra.jat@pathinfotech.com 12/10/2006
		long days = 0;
		// days=DateHelper.getDays(date1,date2);
		/**
		 * added by sukumar@path for calculating the difference between
		 * guarantee start date and toDate
		 */
		days = getDifferenceDays(guarStartDate, expiryDate);

		// System.out.println("Days Difference between two dates="+days);

		if (days <= 365) {
			serviceFeeAmount = serviceFeeRate
					* Math.min(outstandingAmount, approvedAmount) * days
					/ (100 * 365);
		}

		Log.log(Log.INFO, className, methodName, "Days = " + days);

		Log.log(Log.INFO, className, methodName, "Service Fee Amount = "
				+ serviceFeeAmount);

		// servicefee instantiated with calculated serviceamount and cgpan

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, --year);
		fromDate = calendar.getTime();

		serviceFee = new ServiceFee(cgpan, Math.round(serviceFeeAmount));
		// System.out.println("CGPAN:"+application.getCgpan()+"  Days = "+
		// days+" Service Fee Amount = "+Math.round(serviceFeeAmount));
		serviceFee.setBankId(application.getBankId());
		serviceFee.setZoneId(application.getZoneId());
		serviceFee.setBranchId(application.getBranchId());
		serviceFee.setBorrowerId(ssiDetails.getCgbid());

		serviceFee.setFromDate(fromDate);
		Log.log(Log.INFO, className, methodName,
				"From Date = " + serviceFee.getFromDate());
		// System.out.println("serviceFee.getFromDate()"+serviceFee.getFromDate());
		serviceFee.setToDate(toDate);
		Log.log(Log.INFO, className, methodName,
				"To Date = " + serviceFee.getToDate());
		// System.out.println("To Date = "+serviceFee.getToDate());
		return serviceFee;

	}

	/**
	 * 
	 * @param user
	 * @param bnkId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void generateBatchSFDAN(User user, String bnkId)
			throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "generateBatchSFDAN", "Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement generateBatchASFStmt = null;
		int updateStatus = 0;
		String status = "EX";
		String userId = user.getUserId();
		;

		try {
			/*
			 * Creates a CallableStatement object for calling database stored
			 * procedures
			 */

			generateBatchASFStmt = connection
					.prepareCall("{?=call FuncGenASFBatch(?,?,?)}");
			generateBatchASFStmt
					.registerOutParameter(1, java.sql.Types.INTEGER);
			generateBatchASFStmt.setString(2, bnkId);
			generateBatchASFStmt.setString(3, status);
			generateBatchASFStmt.setString(4, userId);
			// generateBatchASFStmt.registerOutParameter(5,
			// java.sql.Types.VARCHAR);
			generateBatchASFStmt.executeQuery();
			updateStatus = Integer.parseInt(generateBatchASFStmt.getObject(1)
					.toString());

			// String error = generateBatchASFStmt.getString(5);
			System.out.println("bnkId:" + bnkId);
			System.out.println("updateStatus:" + updateStatus);
			if (updateStatus == Constants.FUNCTION_SUCCESS) {
				generateBatchASFStmt.close();
				generateBatchASFStmt = null;
				connection.commit();
				Log.log(Log.DEBUG, "RpDAO", "generateBatchSFDAN", "success-SP");
			} else if (updateStatus == Constants.FUNCTION_FAILURE) {
				generateBatchASFStmt.close();
				generateBatchASFStmt = null;
				// Log.log(Log.ERROR,"RpDAO","generateBatchSFDAN","Error : "+error);
				connection.rollback();
				throw new DatabaseException(
						"No Applications Available For SFDAN Generation");
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

		Log.log(Log.INFO, "RpDAO", "generateBatchSFDAN", "Exited");

	}

	public void generateSFDAN(User user, String bnkId, String zneId,
			String brnId) throws DatabaseException {
		// System.out.println("____________Inside RpDAO().generateSFDAN.java");
		RpHelper rpHelper = new RpHelper();
		RpProcessor rpProcessor = new RpProcessor();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();

		String methodName = "generateSFDAN";

		Log.log(Log.INFO, className, methodName, "Entered");

		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		String memberId = "";
		/**
		 * MLI details captured inside application loop for each application. If
		 * this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";
		// Added by sukant
		String sfmemberid = null;
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		String subject = "";
		String emailMessage = "";
		boolean mailStatus = false;
		ArrayList users = null;
		ArrayList emailIds = null;
		ArrayList mailIds = null;
		MLIInfo mliInfo = null;
		String mailPrivelege = "";
		String emailPrivelege = "";
		String hardCopyPrivelege = "";
		String userId = user.getUserId();
		String fromEmail = user.getUserId();
		User mailUser = null;
		/** End of variables used for mailing **/

		String sfdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated
		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN
		ArrayList serviceFeeValues = null; // Arraylist used to get the approved
											// application details that are
											// pending for generation of DAN.
		ServiceFee serviceFee = null;
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop
		Connection connection = DBConnection.getConnection(false);
		try {
			// Calculates Service Fee payable by all the members and inserts
			// them into service_fee table.
			Log.log(Log.DEBUG, className, methodName,
					"Before calculating service fee");
			// System.out.println("Before calculating service fee");
			// /////////////////////////////RITESH HAS ADDED THIS CODE FOR ONE
			// MLI/
			// Added By Path (Ritesh) on 1NOV2006
			// GET ALL BRANCH OFFICESS OF ONE MLI AND START LOOP TO GET
			// GUARANTEE FEE AND GENERATE CGPAN
			// AS IT WAS CREATING EARLIER
			// LOOP STARTS HERE BY PATH RITESH

			CallableStatement allMembersPath = null;
			memberId = bnkId + zneId + brnId;
			// System.out.println("memberId1:"+memberId);
			String bankIdPath = "";
			// Added this code by Ritesh Path on 9Nov2006
			String restId = "";
			// //////////////////////////////////////////
			if (!memberId.equals(Constants.ALL)) {
				// bankIdPath = sfmemberid.substring(0,4);
				// restId = sfmemberid.substring(4,12);
				bankIdPath = memberId.substring(0, 4);
				restId = memberId.substring(4, 12);
				// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);
			} else {
				// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);
				bankIdPath = Constants.ALL;
			}
			// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);
			if (memberId.equals(Constants.ALL) || restId.equals("00000000")) {
				allMembersPath = connection
						.prepareCall("{?=call packGetMemberLiveAppPath.funcGetMemberLiveAppForMli1(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
			} else {
				allMembersPath = connection
						.prepareCall("{?=call packGetMemberLiveAppPath.funcGetMemberLiveAppForMli1(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
			}
			allMembersPath.execute();

			int functionReturn = allMembersPath.getInt(1);
			String error = allMembersPath.getString(3);
			// System.out.println("error:"+error);

			if (functionReturn == Constants.FUNCTION_FAILURE) {
				allMembersPath.close();
				allMembersPath = null;
				throw new DatabaseException(error);
			} else {
				int tmpreccount = 0;
				ResultSet membersResult = (ResultSet) allMembersPath
						.getObject(2);
				// //// code added by ritesh path on 23nov2006
				// if(membersResult.next())
				// {
				// Modified by sukant@pathinfotech 17/05/2007
				while (membersResult.next()) {
					sfmemberid = membersResult.getString(1);
					// System.out.println("sfmemberid:"+sfmemberid);
					// System.out.println("tmpreccount PATH >>> = "+tmpreccount);
					if ((sfmemberid.equals(null)) || (sfmemberid.equals(""))) {
						throw new DatabaseException(
								"No Applications Available For SFDAN Generation");
					}
					// System.out.println("memberId = "+sfmemberid);
					tmpreccount++;
					// System.out.println("tmpreccount PATH IN SFDAN >>> = "+tmpreccount);

					// System.out.println("memberId = "+memberId);
					bnkId = sfmemberid.substring(0, 4);
					zneId = sfmemberid.substring(4, 8);
					brnId = sfmemberid.substring(8, 12);
					// System.out.println("RITESH PATH IN RPDAO SFDAN bnkId = "+bnkId+"  zneId = "+zneId+"   brnId = "+brnId);
					// /////////////////////////CODE END
					// HERE////////////////////////
					// System.out.println("________ calling calculateAllServiceFee()");
					ArrayList sfCgpanList = calculateAllServiceFee(bnkId,
							zneId, brnId, connection);
					// System.out.println("RPDAO RITESH PATH IS COMING OUT AFTER GETTING sfCgpanList >>");
					// System.out.println("sfCgpanList.size():"+sfCgpanList.size());
					// connection.commit();
					if (sfCgpanList != null && sfCgpanList.size() != 0) {
						Log.log(Log.DEBUG, className, methodName,
								"sfCgpanList :" + sfCgpanList.size());
						// serviceFeeValues = getServiceFeeMemberWise(bnkId,
						// zneId, brnId) ;
						serviceFeeValues = sfCgpanList;
						Log.log(Log.DEBUG, className, methodName,
								"After Getting Approved Applications");
						// System.out.println("After Getting Approved Applications");
						if (serviceFeeValues == null) {
							Log.log(Log.DEBUG, className, methodName,
									"No applications fetched");
							// System.out.println("No applications fetched");
							throw new DatabaseException(
									"No Applications Available For SFDAN Generation");
							// return;
						}
						size = serviceFeeValues.size();
						if (size == 0) {
							Log.log(Log.DEBUG, className, methodName,
									"No. of applications fetched = 0");
							// System.out.println("No. of applications fetched = 0");
							throw new DatabaseException(
									"No Applications Available For SFDAN Generation");
							// return ;
						}
						/**
						 * Following lines of code does the following sequence.
						 * 1. Picks up the first DemandAdvice object from the
						 * arraylist. 2. Initialises mliId with the first MLI
						 * retrieved 3. As the DemandAdvice object also has
						 * CGPAN information, new DAN is generated and the PAN
						 * details are inserted in the database
						 * **/
						serviceFee = (ServiceFee) serviceFeeValues.get(0);
						demandAdvice = new DemandAdvice(serviceFee);

						bankId = demandAdvice.getBankId();
						zoneId = demandAdvice.getZoneId();
						branchId = demandAdvice.getBranchId();
						// Start code by Path on 18Oct2006

						sfdanNo = rpHelper.generateSFDANId(demandAdvice,
								connection);
						Log.log(Log.DEBUG, className, methodName,
								"Generating SFDAN for first application, SFDAN = "
										+ sfdanNo);

						// System.out.println("PATH  bankId = "+bankId+"  zoneId = "+zoneId+"  branchId = "+branchId+"  sfdanNo"+sfdanNo);

						demandAdvice.setDanNo(sfdanNo);

						Log.log(Log.DEBUG, className, methodName,
								"After Setting DAN Type for the first SFDAN, Dan Type = "
										+ demandAdvice.getDanType());
						demandAdvice.setDanGeneratedDate(danGeneratedDate);

						dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
						demandAdvice.setDanDueDate(dueDate);
						// Set expiry date for DAN
						demandAdvice.setUserId(userId);

						Log.log(Log.DEBUG, className, methodName,
								"After Generating SFDAN");
						// System.out.println("After Generating SFDAN");
						insertDANDetails(demandAdvice, connection);
						mliId = bankId.concat(zoneId).concat(branchId);
						/**
						 * End of generating DAN for the first application
						 * retrieved.
						 **/
						ParameterMaster parameterMaster = administrator
								.getParameter();
						// System.out.println("parameterMaster"+parameterMaster);
						// System.out.println("PATH  mliId = "+mliId);
						noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																				// exceeds
																				// 20
						// System.out.println("noOfCGPANsLimit:"+noOfCGPANsLimit);
						Log.log(Log.DEBUG, className, methodName,
								"Starting Application Loop");
						if (size == 1) {
							insertPANDetailsForDAN(demandAdvice, connection);

							emailMessage += "DAN No :" + sfdanNo + "CGPAN - "
									+ demandAdvice.getCgpan() + ", serviceFee"
									+ demandAdvice.getAmountRaised() + "\n";
							try {
								users = administrator.getAllUsers(mliId);
							} catch (NoUserFoundException exception) {
								Log.log(Log.WARNING, className, methodName,
										"Exception getting user details for the MLI. Error="
												+ exception.getMessage());
							} catch (DatabaseException exception) {
								Log.log(Log.WARNING, className, methodName,
										"Exception getting user details for the MLI. Error="
												+ exception.getMessage());
							}
							mailer = new Mailer();
							mliInfo = registrationDAO.getMemberDetails(bankId,
									zoneId, branchId);
							mailPrivelege = mliInfo.getMail();
							emailPrivelege = mliInfo.getEmail();
							hardCopyPrivelege = mliInfo.getHardCopy();
							Log.log(Log.DEBUG, className, methodName,
									"Getting Email Id for MLI id completed");
							userSize = users.size();
							emailIds = new ArrayList();
							mailIds = new ArrayList();
							// System.out.println("userSize:"+userSize);
							for (int j = 0; j < userSize; j++) {
								mailUser = (User) users.get(j);
								emailIds.add(mailUser.getUserId()); // mail Ids
								mailIds.add(mailUser.getEmailId()); // e-mail
																	// Ids
								// System.out.println("mailUser.getUserId()"+mailUser.getUserId());
								// System.out.println("mailUser.getEmailId()"+mailUser.getEmailId());
								// emailIds.add(mailUser.getEmailId()) ;
								// Log.log(Log.DEBUG,
								// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
								// ;
								Log.log(Log.DEBUG, className, methodName,
										"Member Id" + mliId + ", User mail "
												+ mailUser.getUserId());
								Log.log(Log.DEBUG, className, methodName,
										"Member Id" + mliId + ", User email "
												+ mailUser.getEmailId());
							}
							// sending mail
							if (emailIds != null) {
								Log.log(Log.DEBUG, className, methodName,
										"Before instantiating message");
								subject = "New Demand Advice(" + sfdanNo
										+ ") generated";
								// System.out.println("subject:"+subject);
								Log.log(Log.DEBUG, className, methodName,
										"Subject = " + subject);
								Log.log(Log.DEBUG, className, methodName,
										"Email Message = " + emailMessage);
								Message message = new Message(emailIds, null,
										null, subject, emailMessage);
								message.setFrom(fromEmail);
								// try
								// {
								// mailer.sendEmail(message);
								// Commented by path on 5jan2007 due to
								// performence issue
								// administrator.sendMail(message);
								// ///////////////////////////////////////////////////////
								/*
								 * }catch(MailerException mailerException) {
								 * Log.log(Log.WARNING, className,methodName,
								 * "Exception sending Mail. Error="
								 * +mailerException.getMessage()) ; }
								 */// administrator(message) ;
								Log.log(Log.DEBUG, className, methodName,
										"After instantiating message");
								// mailStatus = mailer.sendEmail(message) ;
							}

							// sending e-mail

							if (mailIds != null) {
								Log.log(Log.DEBUG, className, methodName,
										"Before instantiating message");
								subject = "New Demand Advice(" + sfdanNo
										+ ") generated";
								Log.log(Log.DEBUG, className, methodName,
										"Subject = " + subject);
								Log.log(Log.DEBUG, className, methodName,
										"Email Message = " + emailMessage);
								Message message = new Message(mailIds, null,
										null, subject, emailMessage);
								message.setFrom(fromEmail);
								// try
								// {
								// Commented by path on 5jan2007 due to
								// performence issue
								// mailer.sendEmail(message);
								// ///////////////////////////////////////////////////////

								// administrator.sendMail(message);
								// }
								// catch(MailerException mailerException)
								// {
								// Log.log(Log.WARNING,
								// className,methodName,"Exception sending Mail. Error="+mailerException.getMessage())
								// ;
								// }

								// administrator(message) ;
								Log.log(Log.DEBUG, className, methodName,
										"After instantiating message");
								// mailStatus = mailer.sendEmail(message) ;
							}
							emailMessage = ""; // resetting the message.
							noOfCGPANs = 0;
							connection.commit();
						} else {
							// System.out.println("size1:"+size);
							/** Starting application loop **/
							for (int i = 0; i < size; i++) {
								serviceFee = (ServiceFee) serviceFeeValues
										.get(i);
								demandAdvice = new DemandAdvice(serviceFee);
								/**
								 * Code inserted By Sowmya on 12-02-2004
								 * 
								 * demandAdvice = new DemandAdvice(serviceFee);
								 * 
								 * double
								 * sfAmount=serviceFee.getServiceAmount();
								 * bankId = demandAdvice.getBankId() ; zoneId =
								 * demandAdvice.getZoneId() ; branchId =
								 * demandAdvice.getBranchId() ;
								 * 
								 * sfdanNo =
								 * rpHelper.generateSFDANId(demandAdvice) ;
								 * Log.log(Log.DEBUG, className,methodName,
								 * "Generating SFDAN for first application, SFDAN = "
								 * +sfdanNo) ; demandAdvice.setDanNo(sfdanNo) ;
								 * 
								 * Log.log(Log.DEBUG, className,methodName,
								 * "After Setting DAN Type for the first SFDAN, Dan Type = "
								 * +demandAdvice.getDanType()) ;
								 * demandAdvice.setDanGeneratedDate
								 * (danGeneratedDate) ;
								 * 
								 * dueDate = getPANDueDate(demandAdvice, null) ;
								 * demandAdvice.setDanDueDate(dueDate) ;
								 * 
								 * demandAdvice.setUserId(userId) ;
								 * 
								 * Log.log(Log.DEBUG, className,methodName,
								 * "After Generating SFDAN") ;
								 * rpDAO.insertDANDetails(demandAdvice) ; mliId
								 * = bankId.concat(zoneId).concat(branchId) ;
								 * 
								 * demandAdvice.setDanNo(sfdanNo) ;
								 * demandAdvice.
								 * setDanGeneratedDate(danGeneratedDate) ;
								 * dueDate = getPANDueDate(demandAdvice, null) ;
								 * demandAdvice.setDanDueDate(dueDate) ;
								 * 
								 * demandAdvice.setUserId(userId) ;
								 */
								bankId = demandAdvice.getBankId();
								zoneId = demandAdvice.getZoneId();
								branchId = demandAdvice.getBranchId();
								// System.out.println("PATH Inside Loop: SFDAN = "+sfdanNo+", CGPAN = "+demandAdvice.getCgpan()+", Appln. Number = "+noOfCGPANs);
								Log.log(Log.DEBUG,
										className,
										methodName,
										"Inside Loop: SFDAN = " + sfdanNo
												+ ", CGPAN = "
												+ demandAdvice.getCgpan()
												+ ", Appln. Number = "
												+ noOfCGPANs);
								tempBankId = demandAdvice.getBankId();
								tempZoneId = demandAdvice.getZoneId();
								tempBranchId = demandAdvice.getBranchId();

								tempMLIId = tempBankId.concat(tempZoneId)
										.concat(tempBranchId);
								Log.log(Log.DEBUG, className, methodName,
										"Temp MLI Id = " + tempMLIId);
								// System.out.println("tempMLIID:"+tempMLIId);
								boolean sendMailValue = true;

								/**
								 * Checks if the new MLI Id is the same as the
								 * previous MLI id or not
								 **/
								// System.out.println("RITESH PATH MLID IS "+mliId);
								if (mliId.equalsIgnoreCase(tempMLIId)) { // If
																			// the
																			// newly
																			// obtained
																			// mliId
																			// is
																			// equal
																			// to
																			// the
																			// previous
																			// MliId
																			// value
									// System.out.println("MLID IS "+mliId);
									// System.out.println("tempMLIID is:"+tempMLIId);
									// System.out.println("noOfCGPANs:"+noOfCGPANs);
									// System.out.println("noOfCGPANsLimit:"+noOfCGPANsLimit);
									if (noOfCGPANs >= noOfCGPANsLimit) {
										// If no. of CGPANs for the same MLI id
										// exceeds the limit, new DAN is
										// generated for the remaining CGPANs of
										// the same MLI.
										emailMessage += "DAN No :"
												+ sfdanNo
												+ "CGPAN - "
												+ demandAdvice.getCgpan()
												+ ", serviceFee"
												+ demandAdvice
														.getAmountRaised()
												+ "\n";
										Log.log(Log.INFO, className,
												methodName, "above cgpan limit");
										try {
											users = administrator
													.getAllUsers(mliId);
										} catch (NoUserFoundException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										} catch (DatabaseException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										}
										mailer = new Mailer();
										mliInfo = registrationDAO
												.getMemberDetails(bankId,
														zoneId, branchId);
										mailPrivelege = mliInfo.getMail();
										emailPrivelege = mliInfo.getEmail();
										hardCopyPrivelege = mliInfo
												.getHardCopy();
										Log.log(Log.DEBUG, className,
												methodName,
												"Getting Email Id for MLI id completed");
										userSize = users.size();
										emailIds = new ArrayList();
										mailIds = new ArrayList();
										// System.out.println("userSize is:"+userSize);
										for (int j = 0; j < userSize; j++) {
											mailUser = (User) users.get(j);
											emailIds.add(mailUser.getUserId()); // mail
																				// Ids
											mailIds.add(mailUser.getEmailId()); // e-mail
																				// Ids
											// emailIds.add(mailUser.getEmailId())
											// ;
											// Log.log(Log.DEBUG,
											// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
											// ;
											Log.log(Log.DEBUG,
													className,
													methodName,
													"Member Id"
															+ mliId
															+ ", User mail "
															+ mailUser
																	.getUserId());
											// System.out.println("mailUser.getUserId():"+mailUser.getUserId());
											Log.log(Log.DEBUG,
													className,
													methodName,
													"Member Id"
															+ mliId
															+ ", User email "
															+ mailUser
																	.getEmailId());
											// System.out.println("mailUser.getEmailId():"+mailUser.getEmailId());
										}
										// sending mail
										if (emailIds != null) {
											Log.log(Log.DEBUG, className,
													methodName,
													"Before instantiating message");
											subject = "New Demand Advice("
													+ sfdanNo + ") generated";
											// System.out.println(subject);
											Log.log(Log.DEBUG, className,
													methodName, "Subject = "
															+ subject);
											Log.log(Log.DEBUG, className,
													methodName,
													"Email Message = "
															+ emailMessage);
											Message message = new Message(
													emailIds, null, null,
													subject, emailMessage);
											message.setFrom(fromEmail);
											// try
											// {
											// mailer.sendEmail(message);
											// Commented by path on 5jan2007 due
											// to performence issue
											// administrator.sendMail(message);
											// ///////////////////////////////////////////////////////

											/*
											 * }catch(MailerException
											 * mailerException) {
											 * Log.log(Log.WARNING,
											 * className,methodName
											 * ,"Exception sending Mail. Error="
											 * +mailerException.getMessage()) ;
											 * }
											 */// administrator(message) ;
											Log.log(Log.DEBUG, className,
													methodName,
													"After instantiating message");
											// mailStatus =
											// mailer.sendEmail(message) ;
										}
										// sending e-mail
										if (mailIds != null) {
											Log.log(Log.DEBUG, className,
													methodName,
													"Before instantiating message");
											subject = "New Demand Advice("
													+ sfdanNo + ") generated";
											Log.log(Log.DEBUG, className,
													methodName, "Subject = "
															+ subject);
											Log.log(Log.DEBUG, className,
													methodName,
													"Email Message = "
															+ emailMessage);
											Message message = new Message(
													mailIds, null, null,
													subject, emailMessage);
											message.setFrom(fromEmail);
											// try
											// {

											// Commented by path on 5jan2007 due
											// to performence issue
											// mailer.sendEmail(message);
											// ///////////////////////////////////////////////////////

											// administrator.sendMail(message);
											// }catch(MailerException
											// mailerException)
											// {
											// Log.log(Log.WARNING,
											// className,methodName,"Exception sending Mail. Error="+mailerException.getMessage())
											// ;
											// }

											// administrator(message) ;
											Log.log(Log.DEBUG, className,
													methodName,
													"After instantiating message");
											// mailStatus =
											// mailer.sendEmail(message) ;
										}
										emailMessage = ""; // resetting the
															// message.
										noOfCGPANs = 0;
										sfdanNo = rpHelper.generateSFDANId(
												bankId, zoneId, branchId,
												connection);
										demandAdvice.setDanNo(sfdanNo);
										// System.out.println(sfdanNo);
										// System.out.println("Dan Number:"+demandAdvice.getDanNo());
										demandAdvice
												.setDanGeneratedDate(danGeneratedDate);
										dueDate = rpProcessor.getPANDueDate(
												demandAdvice, null);
										demandAdvice.setDanDueDate(dueDate);
										// Set expiry date for DAN
										demandAdvice.setUserId(userId);
										insertDANDetails(demandAdvice,
												connection);
										emailMessage += "DAN No : "
												+ demandAdvice.getDanNo()
												+ "\n";
										// COMMENTED BY RITESH PATH BECAUSE
										// ALREADY COMMENTED ABOVE.
										// noOfCGPANs = 0 ;
										sendMailValue = false;
									} else {
										// System.out.println("mli id"+mliId);
										// System.out.println("temp mli id:"+tempMLIId);
										// System.out.println("sfDAN Number:"+sfdanNo);
										demandAdvice.setDanNo(sfdanNo);
										demandAdvice
												.setDanGeneratedDate(danGeneratedDate);
										dueDate = rpProcessor.getPANDueDate(
												demandAdvice, null);
										demandAdvice.setDanDueDate(dueDate);
										// Set expiry date for DAN
										demandAdvice.setUserId(userId);
										sendMailValue = true;
										// rpDAO.insertDANDetails(demandAdvice)
										// ;
									}
									// System.out.println("Before Inserting PAN details for SFDAN");
									Log.log(Log.DEBUG, className, methodName,
											"Before Inserting PAN details for SFDAN"
													+ demandAdvice.getDanNo());
									insertPANDetailsForDAN(demandAdvice,
											connection);
									// System.out.println("After instantiating message");
									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
									Log.log(Log.DEBUG, className, methodName,
											"After Insert from " + methodName);
									++noOfCGPANs;
									emailMessage += "CGPAN - "
											+ demandAdvice.getCgpan()
											+ ", serviceFee"
											+ demandAdvice.getAmountRaised()
											+ "\n";
								} else { // This means MLI id is changed. Mail
											// will be sent to the previous MLI
											// with the generated DAN details.
											// Then, a new DAN is generated for
											// the new MLI id.
									// System.out.println("Entering else part since MLI id changed");
									Log.log(Log.DEBUG, className, methodName,
											"Entering else part since MLI id changed");
									// mliIds.add(mliId) ;
									// administrator.getAllUsers(mliId) ;
									Log.log(Log.DEBUG, className, methodName,
											"Addition of MliId to arraylist completed");
									if (emailMessage != null
											&& !emailMessage.equals("")) {
										try {
											// users =
											// rpDAO.getActiveBankUsers(mliId) ;
											users = administrator
													.getAllUsers(mliId);
										} catch (NoUserFoundException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										} catch (DatabaseException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										}
										Log.log(Log.DEBUG, className,
												methodName,
												"Before getting member details, bankId = "
														+ bankId
														+ ", zoneId = "
														+ zoneId
														+ ", branchId = "
														+ branchId);
										mliInfo = registrationDAO
												.getMemberDetails(bankId,
														zoneId, branchId);
										Log.log(Log.DEBUG, className,
												methodName,
												"After getting member details");
										mailPrivelege = mliInfo.getMail();
										emailPrivelege = mliInfo.getEmail();
										hardCopyPrivelege = mliInfo
												.getHardCopy();
										Log.log(Log.DEBUG, className,
												methodName,
												"Getting Email Id for MLI id completed");
										userSize = users.size();
										emailIds = new ArrayList();
										mailIds = new ArrayList();
										for (int j = 0; j < userSize; j++) {
											mailUser = (User) users.get(j);
											emailIds.add(mailUser.getUserId()); // mail
																				// Ids
											mailIds.add(mailUser.getEmailId()); // e-mail
																				// Ids
											// emailIds.add(mailUser.getEmailId())
											// ;
											// Log.log(Log.DEBUG,
											// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
											// ;
											Log.log(Log.DEBUG,
													className,
													methodName,
													"Member Id"
															+ mliId
															+ ", User mail "
															+ mailUser
																	.getUserId());
											Log.log(Log.DEBUG,
													className,
													methodName,
													"Member Id"
															+ mliId
															+ ", User email "
															+ mailUser
																	.getEmailId());
										}
										// sending mail
										if (emailIds != null) {
											Log.log(Log.DEBUG, className,
													methodName,
													"Before instantiating message");
											subject = "New Demand Advice("
													+ sfdanNo + ") generated";
											Log.log(Log.DEBUG, className,
													methodName, "Subject = "
															+ subject);
											Log.log(Log.DEBUG, className,
													methodName,
													"Email Message = "
															+ emailMessage);
											Message message = new Message(
													emailIds, null, null,
													subject, emailMessage);
											message.setFrom(fromEmail);
											// try
											// {
											// mailer.sendEmail(message);
											// Commented by path on 5jan2007 due
											// to performence issue
											// administrator.sendMail(message);
											// ///////////////////////////////////////////////////////

											/*
											 * }catch(MailerException
											 * mailerException) {
											 * Log.log(Log.WARNING,
											 * className,methodName
											 * ,"Exception sending Mail. Error="
											 * +mailerException.getMessage()) ;
											 * }
											 */// administrator(message) ;
											Log.log(Log.DEBUG, className,
													methodName,
													"After instantiating message");
											// mailStatus =
											// mailer.sendEmail(message) ;
										}
										// sending e-mail
										if (mailIds != null) {
											Log.log(Log.DEBUG, className,
													methodName,
													"Before instantiating message");
											subject = "New Demand Advice("
													+ sfdanNo + ") generated";
											Log.log(Log.DEBUG, className,
													methodName, "Subject = "
															+ subject);
											Log.log(Log.DEBUG, className,
													methodName,
													"Email Message = "
															+ emailMessage);
											Message message = new Message(
													mailIds, null, null,
													subject, emailMessage);
											message.setFrom(fromEmail);
											// try
											// {
											// Commented by path on 5jan2007 due
											// to performence issue
											// mailer.sendEmail(message);
											// ///////////////////////////////////////////////////////

											// administrator.sendMail(message);
											// }catch(MailerException
											// mailerException)
											// {
											// Log.log(Log.WARNING,
											// className,methodName,"Exception sending Mail. Error="+mailerException.getMessage())
											// ;
											// }
											// administrator(message) ;
											Log.log(Log.DEBUG, className,
													methodName,
													"After instantiating message");
											// mailStatus =
											// mailer.sendEmail(message) ;
										}
										emailMessage = ""; // resetting the
															// message.
										noOfCGPANs = 0;
										sfdanNo = rpHelper.generateSFDANId(
												tempBankId, tempZoneId,
												tempBranchId, connection);
										demandAdvice.setDanNo(sfdanNo);
										demandAdvice
												.setDanGeneratedDate(danGeneratedDate);
										dueDate = rpProcessor.getPANDueDate(
												demandAdvice, null);
										demandAdvice.setDanDueDate(dueDate);
										// Set expiry date for DAN
										demandAdvice.setUserId(userId);
										insertDANDetails(demandAdvice,
												connection);
										insertPANDetailsForDAN(demandAdvice,
												connection);
										emailMessage += "DAN No : "
												+ demandAdvice.getDanNo()
												+ "\n";
										++noOfCGPANs;
									}
									/** New DAN generation ends here **/
									/**
									 * mliId is updated with the new Mli
									 * details. Further applications will be
									 * checked with this MLI id
									 **/
									mliId = tempBankId.concat(tempZoneId)
											.concat(tempBranchId);
									// System.out.println("MLIID:"+mliId);
									sendMailValue = false;
								}
								if (sendMailValue) {
									try {
										users = administrator
												.getAllUsers(mliId);
									} catch (NoUserFoundException exception) {
										Log.log(Log.WARNING,
												className,
												methodName,
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									} catch (DatabaseException exception) {
										Log.log(Log.WARNING,
												className,
												methodName,
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									}
									mailer = new Mailer();
									mliInfo = registrationDAO.getMemberDetails(
											tempBankId, tempZoneId,
											tempBranchId);
									mailPrivelege = mliInfo.getMail();
									emailPrivelege = mliInfo.getEmail();
									hardCopyPrivelege = mliInfo.getHardCopy();
									Log.log(Log.DEBUG, className, methodName,
											"Getting Email Id for MLI id completed");
									userSize = users.size();
									emailIds = new ArrayList();
									for (int j = 0; j < userSize; j++) {
										mailUser = (User) users.get(j);
										emailIds.add(mailUser.getUserId());
										// emailIds.add(mailUser.getEmailId()) ;
										// Log.log(Log.DEBUG,
										// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
										// ;
										Log.log(Log.DEBUG, className,
												methodName, "Member Id" + mliId
														+ ", User email "
														+ mailUser.getUserId());
									}
									if (emailIds != null) {
										Log.log(Log.DEBUG, className,
												methodName,
												"Before instantiating message");
										subject = "New Demand Advice("
												+ sfdanNo + ") generated";
										Log.log(Log.DEBUG, className,
												methodName, "Subject = "
														+ subject);
										Log.log(Log.DEBUG, className,
												methodName, "Email Message = "
														+ emailMessage);
										Message message = new Message(emailIds,
												null, null, subject,
												emailMessage);
										message.setFrom(fromEmail);
										// try
										// {
										// mailer.sendEmail(message);
										// Commented by path on 5jan2007 due to
										// performence issue
										// administrator.sendMail(message);
										// ///////////////////////////////////////////////////////

										/*
										 * }catch(MailerException
										 * mailerException) {
										 * Log.log(Log.WARNING,
										 * className,methodName
										 * ,"Exception sending Mail. Error="
										 * +mailerException.getMessage()) ; }
										 */// administrator(message) ;
										Log.log(Log.DEBUG, className,
												methodName,
												"After instantiating message");
										// mailStatus =
										// mailer.sendEmail(message) ;
									}
									emailMessage = ""; // resetting the message.
								}
							}
							// }
							connection.commit();
						}
					} else {
						Log.log(Log.DEBUG, className, methodName,
								"No. of applications fetched = 0");
						throw new DatabaseException(
								"No Applications Available For SFDAN Generation");
					}
					// LOOP ENDS HERE BY PATH RITESH
				}
				// Added by sukant@pathinfotech 02/Feb/2007
				if ((sfmemberid.equals(null)) || (sfmemberid.equals(""))) {
					throw new DatabaseException(
							"No Applications Available For SFDAN Generation");
				}

				// System.out.println("memberId = "+sfmemberid);
				// }
				// else
				// {
				// throw new
				// DatabaseException("No Applications Available For SFDAN Generation");
				// }
			}
			// //////////////////////////////////
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, className, methodName, "Exited");
		/** End of Application Loop **/
	}

	/* ----------------- */

	/**
	 * generate SFDAN for Expired cases added by sukumar@path on 20-08-2008
	 * 
	 * @param user
	 * @param bnkId
	 * @param zneId
	 * @param brnId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void generateSFDANEXP(User user, String bnkId, String zneId,
			String brnId) throws DatabaseException {
		// System.out.println("____________Inside RpDAO().generateSFDANEXP.java Entered");
		RpHelper rpHelper = new RpHelper();
		RpProcessor rpProcessor = new RpProcessor();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		String methodName = "generateSFDANEXP";

		Log.log(Log.INFO, className, methodName, "Entered");

		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		String memberId = "";
		/**
		 * MLI details captured inside application loop for each application. If
		 * this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";

		String sfmemberid = null;
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		ArrayList users = null;
		MLIInfo mliInfo = null;
		String userId = user.getUserId();
		/** End of variables used for mailing **/

		String sfdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated
		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN
		ArrayList serviceFeeValues = null; // Arraylist used to get the approved
											// application details that are
											// pending for generation of DAN.
		ServiceFee serviceFee = null;
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop
		Connection connection = DBConnection.getConnection(false);
		try {
			// Calculates Service Fee payable by all the members and inserts
			// them into service_fee table.
			Log.log(Log.DEBUG, className, methodName,
					"Before calculating service fee");
			// System.out.println("Before calculating service fee");
			CallableStatement allMembersPath = null;
			memberId = bnkId + zneId + brnId;
			// System.out.println("memberId1:"+memberId);
			String bankIdPath = "";
			String restId = "";
			// //////////////////////////////////////////
			if (!memberId.equals(Constants.ALL)) {
				bankIdPath = memberId.substring(0, 4);
				restId = memberId.substring(4, 12);
				// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);
			} else {
				// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);
				bankIdPath = Constants.ALL;
			}

			if (memberId.equals(Constants.ALL) || restId.equals("00000000")) {
				// System.out.println("memberId equals(Constants.ALL)");
				allMembersPath = connection
						.prepareCall("{?=call packGetMemberExpApp.funcGetMemberExpAppForMli1(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
			} else {
				// System.out.println("memberId is not equals(Constants.ALL)");
				allMembersPath = connection
						.prepareCall("{?=call packGetMemberExpApp.funcGetMemberExpAppForMli1(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
			}
			allMembersPath.execute();

			int functionReturn = allMembersPath.getInt(1);
			String error = allMembersPath.getString(3);
			// System.out.println("error:"+error);

			if (functionReturn == Constants.FUNCTION_FAILURE) {
				allMembersPath.close();
				allMembersPath = null;
				throw new DatabaseException(error);
			} else {
				int tmpreccount = 0;
				ResultSet membersResult = (ResultSet) allMembersPath
						.getObject(2);

				while (membersResult.next()) {
					sfmemberid = membersResult.getString(1);
					// System.out.println("sfmemberid:"+sfmemberid);
					// System.out.println("tmpreccount PATH >>> = "+tmpreccount);
					if ((sfmemberid.equals(null)) || (sfmemberid.equals(""))) {
						// System.out.println("sfmemberid is equals null");
						throw new DatabaseException(
								"No Applications Available For SFDAN Generation");
					}
					// System.out.println("memberId = "+sfmemberid);
					tmpreccount++;
					// System.out.println("tmpreccount PATH IN SFDAN >>> = "+tmpreccount);

					// System.out.println("memberId = "+memberId);
					bnkId = sfmemberid.substring(0, 4);
					zneId = sfmemberid.substring(4, 8);
					brnId = sfmemberid.substring(8, 12);
					// System.out.println("RITESH PATH IN RPDAO SFDAN bnkId = "+bnkId+"  zneId = "+zneId+"   brnId = "+brnId);
					// /////////////////////////CODE END
					// HERE////////////////////////
					// System.out.println("________ calling calculateAllServiceFee()");

					ArrayList sfCgpanList = calculateAllServiceFeeForExpiryCases(
							bnkId, zneId, brnId, connection);
					// System.out.println("RPDAO RITESH PATH IS COMING OUT AFTER GETTING sfCgpanList >>");
					// System.out.println("sfCgpanList.size():"+sfCgpanList.size());
					// connection.commit();

					if (sfCgpanList != null && sfCgpanList.size() != 0) {
						Log.log(Log.DEBUG, className, methodName,
								"sfCgpanList :" + sfCgpanList.size());
						// serviceFeeValues = getServiceFeeMemberWise(bnkId,
						// zneId, brnId) ;
						serviceFeeValues = sfCgpanList;
						Log.log(Log.DEBUG, className, methodName,
								"After Getting Approved Applications");
						// System.out.println("After Getting Approved Applications");
						if (serviceFeeValues == null) {
							Log.log(Log.DEBUG, className, methodName,
									"No applications fetched");
							// System.out.println("No applications fetched");
							throw new DatabaseException(
									"No Applications Available For SFDAN Generation");
							// return;
						}
						size = serviceFeeValues.size();
						if (size == 0) {
							Log.log(Log.DEBUG, className, methodName,
									"No. of applications fetched = 0");
							// System.out.println("No. of applications fetched = 0");
							throw new DatabaseException(
									"No Applications Available For SFDAN Generation");
							// return ;
						}

						serviceFee = (ServiceFee) serviceFeeValues.get(0);
						demandAdvice = new DemandAdvice(serviceFee);

						bankId = demandAdvice.getBankId();
						zoneId = demandAdvice.getZoneId();
						branchId = demandAdvice.getBranchId();
						// Start code by Path on 18Oct2006

						sfdanNo = rpHelper.generateSFDANId(demandAdvice,
								connection);
						Log.log(Log.DEBUG, className, methodName,
								"Generating SFDAN for first application, SFDAN = "
										+ sfdanNo);

						// System.out.println("PATH  bankId = "+bankId+"  zoneId = "+zoneId+"  branchId = "+branchId+"  sfdanNo"+sfdanNo);

						demandAdvice.setDanNo(sfdanNo);

						Log.log(Log.DEBUG, className, methodName,
								"After Setting DAN Type for the first SFDAN, Dan Type = "
										+ demandAdvice.getDanType());
						demandAdvice.setDanGeneratedDate(danGeneratedDate);
						// System.out.println("danGeneratedDate:"+danGeneratedDate);
						dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
						// System.out.println("dueDate:"+dueDate);
						demandAdvice.setDanDueDate(dueDate);
						// Set expiry date for DAN
						demandAdvice.setUserId(userId);

						Log.log(Log.DEBUG, className, methodName,
								"After Generating SFDAN");
						// System.out.println("After Generating SFDAN");
						insertDANDetails(demandAdvice, connection);
						mliId = bankId.concat(zoneId).concat(branchId);
						ParameterMaster parameterMaster = administrator
								.getParameter();
						// System.out.println("parameterMaster"+parameterMaster);
						// System.out.println("PATH  mliId = "+mliId);
						noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																				// exceeds
																				// 20
						// System.out.println("noOfCGPANsLimit getting from Parameter Master Table:"+noOfCGPANsLimit);
						Log.log(Log.DEBUG, className, methodName,
								"Starting Application Loop");
						// System.out.println("size:"+size);
						if (size == 1) {
							insertPANDetailsForDANExp(demandAdvice, connection);

							try {
								users = administrator.getAllUsers(mliId);
							} catch (NoUserFoundException exception) {
								Log.log(Log.WARNING, className, methodName,
										"Exception getting user details for the MLI. Error="
												+ exception.getMessage());
							} catch (DatabaseException exception) {
								Log.log(Log.WARNING, className, methodName,
										"Exception getting user details for the MLI. Error="
												+ exception.getMessage());
							}
							mailer = new Mailer();
							mliInfo = registrationDAO.getMemberDetails(bankId,
									zoneId, branchId);
							userSize = users.size();
							// System.out.println("userSize:"+userSize);
							noOfCGPANs = 0;
							connection.commit();
						} else {
							// System.out.println("size1:"+size);

							for (int i = 0; i < size; i++) {
								serviceFee = (ServiceFee) serviceFeeValues
										.get(i);
								// System.out.println("serviceFee:"+serviceFee);
								demandAdvice = new DemandAdvice(serviceFee);
								// System.out.println("demandAdvice:"+demandAdvice);

								bankId = demandAdvice.getBankId();
								zoneId = demandAdvice.getZoneId();
								branchId = demandAdvice.getBranchId();
								// System.out.println("PATH Inside Loop: SFDAN = "+sfdanNo+", CGPAN = "+demandAdvice.getCgpan()+", Appln. Number = "+noOfCGPANs);
								Log.log(Log.DEBUG,
										className,
										methodName,
										"Inside Loop: SFDAN = " + sfdanNo
												+ ", CGPAN = "
												+ demandAdvice.getCgpan()
												+ ", Appln. Number = "
												+ noOfCGPANs);
								tempBankId = demandAdvice.getBankId();
								tempZoneId = demandAdvice.getZoneId();
								tempBranchId = demandAdvice.getBranchId();

								tempMLIId = tempBankId.concat(tempZoneId)
										.concat(tempBranchId);
								Log.log(Log.DEBUG, className, methodName,
										"Temp MLI Id = " + tempMLIId);
								// System.out.println("tempMLIID:"+tempMLIId);
								boolean sendMailValue = true;

								// System.out.println("RITESH PATH MLID IS "+mliId);
								if (mliId.equalsIgnoreCase(tempMLIId)) { // If
																			// the
																			// newly
																			// obtained
																			// mliId
																			// is
																			// equal
																			// to
																			// the
																			// previous
																			// MliId
																			// value
									// System.out.println("MLID IS "+mliId);
									// System.out.println("tempMLIID is:"+tempMLIId);
									// System.out.println("noOfCGPANs:"+noOfCGPANs);
									// System.out.println("noOfCGPANsLimit:"+noOfCGPANsLimit);
									if (noOfCGPANs >= noOfCGPANsLimit) {
										// If no. of CGPANs for the same MLI id
										// exceeds the limit, new DAN is
										// generated for the remaining CGPANs of
										// the same MLI.
										try {
											users = administrator
													.getAllUsers(mliId);
										} catch (NoUserFoundException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										} catch (DatabaseException exception) {
											Log.log(Log.WARNING,
													className,
													methodName,
													"Exception getting user details for the MLI. Error="
															+ exception
																	.getMessage());
										}
										mailer = new Mailer();
										mliInfo = registrationDAO
												.getMemberDetails(bankId,
														zoneId, branchId);
										userSize = users.size();
										// System.out.println("userSize is:"+userSize);

										noOfCGPANs = 0;
										sfdanNo = rpHelper.generateSFDANId(
												bankId, zoneId, branchId,
												connection);
										demandAdvice.setDanNo(sfdanNo);
										// System.out.println(sfdanNo);
										// System.out.println("Dan Number:"+demandAdvice.getDanNo());
										demandAdvice
												.setDanGeneratedDate(danGeneratedDate);
										dueDate = rpProcessor.getPANDueDate(
												demandAdvice, null);
										demandAdvice.setDanDueDate(dueDate);
										// Set expiry date for DAN
										demandAdvice.setUserId(userId);
										insertDANDetails(demandAdvice,
												connection);
									} else {
										// System.out.println("mli id"+mliId);
										// System.out.println("temp mli id:"+tempMLIId);
										// System.out.println("sfDAN Number:"+sfdanNo);
										demandAdvice.setDanNo(sfdanNo);
										demandAdvice
												.setDanGeneratedDate(danGeneratedDate);
										dueDate = rpProcessor.getPANDueDate(
												demandAdvice, null);
										demandAdvice.setDanDueDate(dueDate);
										// Set expiry date for DAN
										demandAdvice.setUserId(userId);
										sendMailValue = true;
										// rpDAO.insertDANDetails(demandAdvice)
										// ;
									}
									// System.out.println("Before Inserting PAN details for SFDAN");
									Log.log(Log.DEBUG, className, methodName,
											"Before Inserting PAN details for SFDAN"
													+ demandAdvice.getDanNo());
									insertPANDetailsForDANExp(demandAdvice,
											connection);
									// System.out.println("After instantiating message");
									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
									Log.log(Log.DEBUG, className, methodName,
											"After Insert from " + methodName);
									++noOfCGPANs;
								} else { // This means MLI id is changed. Mail
											// will be sent to the previous MLI
											// with the generated DAN details.
											// Then, a new DAN is generated for
											// the new MLI id.
									// System.out.println("Entering else part since MLI id changed");
									Log.log(Log.DEBUG, className, methodName,
											"Entering else part since MLI id changed");
									// mliIds.add(mliId) ;
									// administrator.getAllUsers(mliId) ;
									Log.log(Log.DEBUG, className, methodName,
											"Addition of MliId to arraylist completed");
									mliId = tempBankId.concat(tempZoneId)
											.concat(tempBranchId);
									// System.out.println("MLIID:"+mliId);
									sendMailValue = false;
								}
								if (sendMailValue) {
									try {
										users = administrator
												.getAllUsers(mliId);
									} catch (NoUserFoundException exception) {
										Log.log(Log.WARNING,
												className,
												methodName,
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									} catch (DatabaseException exception) {
										Log.log(Log.WARNING,
												className,
												methodName,
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									}
									mailer = new Mailer();
									mliInfo = registrationDAO.getMemberDetails(
											tempBankId, tempZoneId,
											tempBranchId);
									userSize = users.size();
								}
							}
							// }
							connection.commit();
						}
					} else {
						Log.log(Log.DEBUG, className, methodName,
								"No. of applications fetched = 0");
						throw new DatabaseException(
								"No Applications Available For SFDAN Generation");
					}

					// LOOP ENDS HERE BY PATH RITESH
				}
				// Added by sukant@pathinfotech 02/Feb/2007
				if ((sfmemberid.equals(null)) || (sfmemberid.equals(""))) {
					throw new DatabaseException(
							"No Applications Available For SFDAN Generation");
				}

				// System.out.println("memberId = "+sfmemberid);
			}

			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, className, methodName, "Exited");
		/** End of Application Loop **/
	}

	/*-------------------- */
	// ADDED BY RITESH PATH ON 9NOV2006 TO USE THE DATES AS A FILTER IN THIS
	// FUNCTION.
	// rpDAO.generateCGDAN(user, memberId,fromDt,toDt);

	/**
	 * This method picks up all approved applications and generates DAN for the
	 * same. APPLICATION
	 * 
	 * @author OVERLOADED BY RITESH PATH As per new
	 */
	public void generateCGDAN(User user, String memberId, java.sql.Date fromDt,
			java.sql.Date toDt) throws DatabaseException,
			NoApplicationFoundException {
		RpProcessor rpProcessor = new RpProcessor();
		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();

		String methodName = "generateCGDAN";
		Log.log(Log.INFO, className, methodName, "Entered");
		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;
		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		double guaranteeAmount[];
		/**
		 * MLI details captured inside application loop for each application.
		 * 
		 * If this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		String subject = "";
		String emailMessage = "";
		boolean mailStatus = false;
		ArrayList users = null;
		ArrayList emailIds = null;
		MLIInfo mliInfo = null;
		String mailPrivelege = "";
		String emailPrivelege = "";
		String hardCopyPrivelege = "";
		String userId = user.getUserId();
		String fromEmail = user.getUserId();
		User mailUser = null;
		// Added by sukant@pathinfotech 17/05/2007
		String dtmemberid = null;
		/** End of variables used for mailing **/

		String cgdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated
		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN
		ArrayList guaranteeValues = null; // Arraylist used to get the approved
											// application details that are
											// pending for generation of DAN.
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop
		Application application = null;

		Connection connection = DBConnection.getConnection(false);
		try {
			// returns an arraylist of approved applications for the day
			// memberwise.
			Log.log(Log.DEBUG, className, methodName,
					"Before getting Approved Applications Member Wise");
			CallableStatement allMembersPath = null;
			String bankIdPath = "";

			String restId = "";

			if (!memberId.equals(Constants.ALL)) {
				bankIdPath = memberId.substring(0, 4);
				restId = memberId.substring(4, 12);
			} else {
				bankIdPath = Constants.ALL;
			}
			// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);

			if (memberId.equals(Constants.ALL) || restId.equals("00000000")) {
				allMembersPath = connection
						.prepareCall("{?=call packGetAllMembersPath.funcGetAllMembersForOneMliDt(?,?,?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath);
				allMembersPath.setDate(5, fromDt);
				allMembersPath.setDate(6, toDt);

			} else {
				allMembersPath = connection
						.prepareCall("{?=call packGetAllMembersPath.funcGetAllMembersForOneMli1Dt(?,?,?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
				allMembersPath.setDate(5, fromDt);
				allMembersPath.setDate(6, toDt);

			}

			allMembersPath.execute();

			int functionReturn = allMembersPath.getInt(1);
			String error = allMembersPath.getString(3);
			// System.out.println("error:"+error);
			if (functionReturn == Constants.FUNCTION_FAILURE) {
				allMembersPath.close();
				allMembersPath = null;
				throw new DatabaseException(error);
			} else {
				int tmpreccount = 0;
				ResultSet membersResult = (ResultSet) allMembersPath
						.getObject(2);
				while (membersResult.next()) {
					tmpreccount++;
					// Modified by sukant@pathinfotech 17/05/2007
					dtmemberid = membersResult.getString(1);
					// System.out.println("dtmemberid:"+dtmemberid);
					// System.out.println("tmpreccount PATH >>> = "+tmpreccount);
					if ((dtmemberid.equals(null)) || (dtmemberid.equals(""))) {
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
					}
					// System.out.println("memberId = "+dtmemberid);

					guaranteeValues = getGuaranteeFeeMemberWise(dtmemberid,
							fromDt, toDt);

					Log.log(Log.DEBUG, className, methodName,
							"After Getting Approved Applications");

					if (guaranteeValues == null) {
						Log.log(Log.DEBUG, className, methodName,
								"No applications fetched");
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
						// return;
					}
					size = guaranteeValues.size();
					// System.out.println("size:"+size);
					if (size == 0) {
						Log.log(Log.DEBUG, className, methodName,
								"No. of applications fetched = 0");
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
						// return ;
					}

					/**
					 * Following lines of code does the following sequence. 1.
					 * Picks up the first DemandAdvice object from the
					 * arraylist. 2. Initialises mliId with the first MLI
					 * retrieved 3. As the DemandAdvice object also has CGPAN
					 * information, new DAN is generated and the PAN details are
					 * inserted in the database
					 * **/
					demandAdvice = (DemandAdvice) guaranteeValues.get(0);
					// System.out.println("demandAdvice:"+demandAdvice);
					bankId = demandAdvice.getBankId();
					zoneId = demandAdvice.getZoneId();
					branchId = demandAdvice.getBranchId();
					mliId = (bankId.concat(zoneId)).concat(branchId);
					// System.out.println("mliId-12232:"+mliId);
					Log.log(Log.DEBUG, className, methodName, "Member Id = "
							+ mliId + ", CGPAN = " + demandAdvice.getCgpan());

					application = applicationDAO.getAppForCgpan(mliId,
							demandAdvice.getCgpan());

					Log.log(Log.DEBUG, className, methodName,
							"Application object = " + application);

					cgdanNo = rpHelper.generateCGDANId(bankId, zoneId,
							branchId, connection);

					Log.log(Log.DEBUG, className, methodName,
							"Generating CGDAN for first application, CGDAN = "
									+ cgdanNo);

					demandAdvice.setDanNo(cgdanNo);

					Log.log(Log.DEBUG, className, methodName,
							"Setting DAN Type for the first CGDAN, Dan Type = "
									+ RpConstants.DAN_TYPE_CGDAN);

					demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);

					Log.log(Log.DEBUG, className, methodName,
							"After Setting DAN Type for the first CGDAN, Dan Type = "
									+ demandAdvice.getDanType());

					demandAdvice.setDanGeneratedDate(danGeneratedDate);
					dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
					demandAdvice.setDanDueDate(dueDate);
					demandAdvice.setUserId(userId);

					Log.log(Log.DEBUG, className, methodName,
							"After Generating CGDAN");
					insertDANDetails(demandAdvice, connection);
					mliId = bankId.concat(zoneId).concat(branchId);
					/**
					 * End of generating DAN for the first application
					 * retrieved.
					 **/
					noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																			// exceeds
																			// 20
					// System.out.println("PATH in generateCGDAN noOfCGPANsLimit = "+noOfCGPANsLimit);
					Log.log(Log.DEBUG, className, methodName,
							"Starting Application Loop");
					boolean sendMailValue = false;
					/** Starting application loop **/
					for (int i = 0; i < size; ++i) {
						demandAdvice = (DemandAdvice) guaranteeValues.get(i);
						demandAdvice.setDanNo(cgdanNo);
						demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);
						demandAdvice.setDanGeneratedDate(danGeneratedDate);
						dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
						demandAdvice.setDanDueDate(dueDate);
						// Set expiry date for DAN
						demandAdvice.setUserId(userId);
						Log.log(Log.DEBUG,
								className,
								methodName,
								"Inside Loop: CGDAN = " + cgdanNo
										+ ", CGPAN = "
										+ demandAdvice.getCgpan());
						tempBankId = demandAdvice.getBankId();
						tempZoneId = demandAdvice.getZoneId();
						tempBranchId = demandAdvice.getBranchId();
						tempMLIId = tempBankId.concat(tempZoneId).concat(
								tempBranchId);
						application = applicationDAO.getApplication(tempMLIId,
								demandAdvice.getCgpan(), "");
						guaranteeAmount = rpProcessor
								.calculateGuaranteeFee(application);
						Log.log(Log.DEBUG, className, methodName,
								"Guarantee Amount = " + guaranteeAmount);
						demandAdvice.setAmountRaised(Math
								.round(guaranteeAmount[4]));
						Log.log(Log.DEBUG, className, methodName,
								"Temp MLI Id = " + tempMLIId);
						sendMailValue = true;
						/**
						 * Checks if the new MLI Id is the same as the previous
						 * MLI id or not
						 **/
						if (mliId.equalsIgnoreCase(tempMLIId)) { // If the newly
																	// obtained
																	// mliId is
																	// equal to
																	// the
																	// previous
																	// MliId
																	// value
							Log.log(Log.DEBUG, className, methodName,
									"MLI id equal temp mli id");
							if (noOfCGPANs >= noOfCGPANsLimit) { // If no. of
																	// CGPANs
																	// for the
																	// same MLI
																	// id
																	// exceeds
																	// the
																	// limit,
																	// new DAN
																	// is
																	// generated
																	// for the
																	// remaining
																	// CGPANs of
																	// the same
																	// MLI.
								Log.log(Log.DEBUG, className, methodName,
										"No of cgpans exceeded limit");
								try {
									users = administrator.getAllUsers(mliId);
								} catch (NoUserFoundException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								} catch (DatabaseException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								}
								mailer = new Mailer();
								mliInfo = registrationDAO.getMemberDetails(
										bankId, zoneId, branchId);
								mailPrivelege = mliInfo.getMail();
								emailPrivelege = mliInfo.getEmail();
								hardCopyPrivelege = mliInfo.getHardCopy();
								Log.log(Log.DEBUG, className, methodName,
										"Getting Email Id for MLI id completed");
								userSize = users.size();
								emailIds = new ArrayList();
								// System.out.println("PATH in generateCGDAN userSize = "+userSize);
								for (int j = 0; j < userSize; j++) {
									mailUser = (User) users.get(j);
									emailIds.add(mailUser.getUserId());
									Log.log(Log.DEBUG, className, methodName,
											"Member Id" + mliId
													+ ", User email "
													+ mailUser.getUserId());
								}
								if (emailIds != null) {
									Log.log(Log.DEBUG, className, methodName,
											"Before instantiating message");
									subject = "New Demand Advice(" + cgdanNo
											+ ") generated";
									Log.log(Log.DEBUG, className, methodName,
											"Subject = " + subject);
									Log.log(Log.DEBUG, className, methodName,
											"Email Message = " + emailMessage);
									Message message = new Message(emailIds,
											null, null, subject, emailMessage);
									message.setFrom(fromEmail);
									// Commented by path on 5jan2007 due to
									// performence issue
									// administrator.sendMail(message);
									// ///////////////////////////////////////////////////////

									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
								}
								emailMessage = ""; // resetting the message.
								noOfCGPANs = 0;
								cgdanNo = rpHelper.generateCGDANId(bankId,
										zoneId, branchId, connection);
								demandAdvice.setDanNo(cgdanNo);
								insertDANDetails(demandAdvice, connection);
								emailMessage += "DAN No : "
										+ demandAdvice.getDanNo() + "\n";
								noOfCGPANs = 0;
								sendMailValue = false;
							} else {
								sendMailValue = true;
							}
							insertPANDetailsForDAN(demandAdvice, connection);
							// update the guarantee fee for the CGPAN.
							GuaranteeFee guaranteeFee = new GuaranteeFee();
							guaranteeFee.setGuaranteeAmount(demandAdvice
									.getAmountRaised());
							guaranteeFee.setCgpan(demandAdvice.getCgpan());
							updateGuaranteeFee(guaranteeFee, user.getUserId(),
									connection);
							Log.log(Log.DEBUG, className, methodName,
									"After Insert from " + methodName);
							++noOfCGPANs;
							emailMessage += "CGPAN - "
									+ demandAdvice.getCgpan()
									+ ", GuaranteeAmount - "
									+ demandAdvice.getAmountRaised() + "\n";
							// System.out.println("emailMessage = "+emailMessage);
						} else { // This means MLI id is changed. Mail will be
									// sent to the previous MLI with the
									// generated DAN details. Then, a new DAN is
									// generated for the new MLI id.
							Log.log(Log.DEBUG, className, methodName,
									"Entering else part since MLI id changed");
							Log.log(Log.DEBUG, className, methodName,
									"Addition of MliId to arraylist completed");
							if (emailMessage != null
									&& !emailMessage.equals("")) {
								try {
									users = administrator.getAllUsers(mliId);
								} catch (NoUserFoundException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								} catch (DatabaseException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								}
								mliInfo = registrationDAO.getMemberDetails(
										bankId, zoneId, branchId);
								mailPrivelege = mliInfo.getMail();
								emailPrivelege = mliInfo.getEmail();
								hardCopyPrivelege = mliInfo.getHardCopy();
								Log.log(Log.DEBUG, className, methodName,
										"Getting Email Id for MLI id completed");
								userSize = users.size();
								emailIds = new ArrayList();
								for (int j = 0; j < userSize; ++j) {
									mailUser = (User) users.get(j);
									emailIds.add(mailUser.getUserId());
									Log.log(Log.DEBUG, className, methodName,
											"Member Id" + mliId
													+ ", User email "
													+ mailUser.getEmailId());
								}
								if (emailIds != null) {
									Log.log(Log.DEBUG, className, methodName,
											"Before instantiating message");
									subject = "New Demand Advice(" + cgdanNo
											+ ") generated";
									Log.log(Log.DEBUG, className, methodName,
											"Subject = " + subject);
									Log.log(Log.DEBUG, className, methodName,
											"Email Message = " + emailMessage);
									Message message = new Message(emailIds,
											null, null, subject, emailMessage);
									message.setFrom(fromEmail);
									// Commented by path on 5jan2007 due to
									// performence issue
									// administrator.sendMail(message);
									// ///////////////////////////////////////////////////////

									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
									// mailStatus = mailer.sendEmail(message) ;
								}
								emailMessage = ""; // resetting the message.
								noOfCGPANs = 0;
								/** New DAN generation begins here **/
								Log.log(Log.DEBUG, className, methodName,
										"Before generating New CGDAN: ");
								cgdanNo = rpHelper.generateCGDANId(tempBankId,
										tempZoneId, tempBranchId, connection);
								Log.log(Log.DEBUG, className, methodName,
										"New CGDAN: " + cgdanNo);

								demandAdvice.setDanNo(cgdanNo);

								insertDANDetails(demandAdvice, connection);

								guaranteeAmount = rpProcessor
										.calculateGuaranteeFee(application);

								Log.log(Log.DEBUG, className, methodName,
										"Guarantee Amount = " + guaranteeAmount[4]);

								demandAdvice.setAmountRaised(guaranteeAmount[4]);

								insertPANDetailsForDAN(demandAdvice, connection);

								// update the guarantee fee for the CGPAN.
								GuaranteeFee guaranteeFee = new GuaranteeFee();
								guaranteeFee.setGuaranteeAmount(demandAdvice
										.getAmountRaised());
								guaranteeFee.setCgpan(demandAdvice.getCgpan());

								updateGuaranteeFee(guaranteeFee,
										user.getUserId(), connection);

								emailMessage += "DAN No : "
										+ demandAdvice.getDanNo() + "\n";
								++noOfCGPANs;
								/** New DAN generation ends here **/

							}

							/**
							 * mliId is updated with the new Mli details.
							 * Further applications will be checked with this
							 * MLI id
							 **/
							mliId = tempBankId.concat(tempZoneId).concat(
									tempBranchId);
							if (emailMessage == "" || emailMessage == null) {
								sendMailValue = false;
							} else {
								sendMailValue = true;
							}

						}
						application = null;
					}
					/** End of Application Loop **/
					if (sendMailValue) {
						try {
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						mailer = new Mailer();

						mliInfo = registrationDAO.getMemberDetails(tempBankId,
								tempZoneId, tempBranchId);

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.DEBUG, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						for (int j = 0; j < userSize; j++) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId());
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getUserId());
						}

						if (emailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + cgdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);

							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////

							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}
						emailMessage = ""; // resetting the message.
					}
				}
				// Added by sukant@pathinfotech 02/Feb/2007
				if ((dtmemberid.equals(null)) || (dtmemberid.equals(""))) {
					throw new DatabaseException(
							"No Applications Available For CGDAN Generation");
				}
			}

			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		}

		Log.log(Log.INFO, className, methodName, "Exited");
	}

	// ///////////////////////ORIGINAL FUNCTION OF GENERATE CGDAN
	// /////////////////////////////////////////////////////////

	/**
	 * This method picks up all approved applications and generates DAN for the
	 * same. APPLICATION
	 * 
	 * @author GU14477 As per new
	 */
	public void generateCGDAN_ORG(User user, String memberId)
			throws DatabaseException, NoApplicationFoundException {

		RpProcessor rpProcessor = new RpProcessor();
		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();

		String methodName = "generateCGDAN";

		Log.log(Log.INFO, className, methodName, "Entered");

		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";

		double guaranteeAmount[];
		/**
		 * MLI details captured inside application loop for each application. If
		 * this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		String subject = "";
		String emailMessage = "";
		boolean mailStatus = false;
		ArrayList users = null;
		ArrayList emailIds = null;
		MLIInfo mliInfo = null;
		String mailPrivelege = "";
		String emailPrivelege = "";
		String hardCopyPrivelege = "";
		String userId = user.getUserId();
		String fromEmail = user.getUserId();
		User mailUser = null;
		/** End of variables used for mailing **/

		String cgdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated

		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN

		ArrayList guaranteeValues = null; // Arraylist used to get the approved
											// application details that are
											// pending for generation of DAN.
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop
		Application application = null;

		Connection connection = DBConnection.getConnection(false);

		try {
			// returns an arraylist of approved applications for the day
			// memberwise.
			Log.log(Log.DEBUG, className, methodName,
					"Before getting Approved Applications Member Wise");

			guaranteeValues = getGuaranteeFeeMemberWise(memberId);

			Log.log(Log.DEBUG, className, methodName,
					"After Getting Approved Applications");

			if (guaranteeValues == null) {
				Log.log(Log.DEBUG, className, methodName,
						"No applications fetched");
				return;
			}

			size = guaranteeValues.size();
			if (size == 0) {
				Log.log(Log.DEBUG, className, methodName,
						"No. of applications fetched = 0");
				return;
			}

			/**
			 * Following lines of code does the following sequence. 1. Picks up
			 * the first DemandAdvice object from the arraylist. 2. Initialises
			 * mliId with the first MLI retrieved 3. As the DemandAdvice object
			 * also has CGPAN information, new DAN is generated and the PAN
			 * details are inserted in the database
			 * **/

			demandAdvice = (DemandAdvice) guaranteeValues.get(0);
			bankId = demandAdvice.getBankId();
			zoneId = demandAdvice.getZoneId();
			branchId = demandAdvice.getBranchId();
			mliId = (bankId.concat(zoneId)).concat(branchId);

			Log.log(Log.DEBUG, className, methodName, "Member Id = " + mliId
					+ ", CGPAN = " + demandAdvice.getCgpan());

			application = applicationDAO.getAppForCgpan(mliId,
					demandAdvice.getCgpan());

			Log.log(Log.DEBUG, className, methodName, "Application object = "
					+ application);

			// guaranteeAmount = calculateGuaranteeFee(application) ;

			// Log.log(Log.DEBUG, className, methodName,
			// "Guarantee Amount = "+guaranteeAmount) ;

			// demandAdvice.setAmountRaised(guaranteeAmount) ;

			cgdanNo = rpHelper.generateCGDANId(bankId, zoneId, branchId,
					connection);

			// Log.log(Log.DEBUG,
			// className,methodName,"Generating CGDAN for first application, CGDAN = "+cgdanNo)
			// ;

			demandAdvice.setDanNo(cgdanNo);

			// Log.log(Log.DEBUG,
			// className,methodName,"Setting DAN Type for the first CGDAN, Dan Type = "+RpConstants.DAN_TYPE_CGDAN)
			// ;

			demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);

			// Log.log(Log.DEBUG,
			// className,methodName,"After Setting DAN Type for the first CGDAN, Dan Type = "+demandAdvice.getDanType())
			// ;

			demandAdvice.setDanGeneratedDate(danGeneratedDate);

			dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
			demandAdvice.setDanDueDate(dueDate);
			// Set expiry date for DAN
			demandAdvice.setUserId(userId);

			Log.log(Log.DEBUG, className, methodName, "After Generating CGDAN");
			insertDANDetails(demandAdvice, connection);
			mliId = bankId.concat(zoneId).concat(branchId);
			/** End of generating DAN for the first application retrieved. **/

			noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																	// exceeds
																	// 20
			Log.log(Log.DEBUG, className, methodName,
					"Starting Application Loop");
			boolean sendMailValue = false;
			/** Starting application loop **/
			for (int i = 0; i < size; ++i) {
				demandAdvice = (DemandAdvice) guaranteeValues.get(i);
				demandAdvice.setDanNo(cgdanNo);
				demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);
				demandAdvice.setDanGeneratedDate(danGeneratedDate);
				dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
				demandAdvice.setDanDueDate(dueDate);
				// Set expiry date for DAN
				demandAdvice.setUserId(userId);

				Log.log(Log.DEBUG, className, methodName,
						"Inside Loop: CGDAN = " + cgdanNo + ", CGPAN = "
								+ demandAdvice.getCgpan());
				tempBankId = demandAdvice.getBankId();
				tempZoneId = demandAdvice.getZoneId();
				tempBranchId = demandAdvice.getBranchId();

				tempMLIId = tempBankId.concat(tempZoneId).concat(tempBranchId);

				application = applicationDAO.getApplication(tempMLIId,
						demandAdvice.getCgpan(), "");

				guaranteeAmount = rpProcessor
						.calculateGuaranteeFee(application);

				Log.log(Log.DEBUG, className, methodName, "Guarantee Amount = "
						+ guaranteeAmount[4]);

				demandAdvice.setAmountRaised(Math.round(guaranteeAmount[4]));

				Log.log(Log.DEBUG, className, methodName, "Temp MLI Id = "
						+ tempMLIId);

				sendMailValue = true;

				/**
				 * Checks if the new MLI Id is the same as the previous MLI id
				 * or not
				 **/
				if (mliId.equalsIgnoreCase(tempMLIId)) { // If the newly
															// obtained mliId is
															// equal to the
															// previous MliId
															// value
					Log.log(Log.DEBUG, className, methodName,
							"MLI id equal temp mli id");
					if (noOfCGPANs >= noOfCGPANsLimit) { // If no. of CGPANs for
															// the same MLI id
															// exceeds the
															// limit, new DAN is
															// generated for the
															// remaining CGPANs
															// of the same MLI.
						Log.log(Log.DEBUG, className, methodName,
								"No of cgpans exceeded limit");

						try {
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						mailer = new Mailer();

						mliInfo = registrationDAO.getMemberDetails(bankId,
								zoneId, branchId);

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.DEBUG, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						for (int j = 0; j < userSize; j++) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId());
							// emailIds.add(mailUser.getEmailId()) ;
							// Log.log(Log.DEBUG,
							// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
							// ;
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getUserId());
						}

						if (emailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + cgdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// try
							// {
							// mailer.sendEmail(message);
							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////

							/*
							 * }catch(MailerException mailerException) {
							 * Log.log(Log.WARNING,
							 * className,methodName,"Exception sending Mail. Error="
							 * +mailerException.getMessage()) ; }
							 */// administrator(message) ;
							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}

						emailMessage = ""; // resetting the message.
						noOfCGPANs = 0;

						cgdanNo = rpHelper.generateCGDANId(bankId, zoneId,
								branchId, connection);
						demandAdvice.setDanNo(cgdanNo);

						insertDANDetails(demandAdvice, connection);

						emailMessage += "DAN No : " + demandAdvice.getDanNo()
								+ "\n";
						noOfCGPANs = 0;

						sendMailValue = false;
					} else {

						sendMailValue = true;
					}

					insertPANDetailsForDAN(demandAdvice, connection);

					// update the guarantee fee for the CGPAN.
					GuaranteeFee guaranteeFee = new GuaranteeFee();
					guaranteeFee.setGuaranteeAmount(demandAdvice
							.getAmountRaised());
					guaranteeFee.setCgpan(demandAdvice.getCgpan());
					updateGuaranteeFee(guaranteeFee, user.getUserId(),
							connection);

					Log.log(Log.DEBUG, className, methodName,
							"After Insert from " + methodName);

					++noOfCGPANs;
					emailMessage += "CGPAN - " + demandAdvice.getCgpan()
							+ ", GuaranteeAmount - "
							+ demandAdvice.getAmountRaised() + "\n";
				} else { // This means MLI id is changed. Mail will be sent to
							// the previous MLI with the generated DAN details.
							// Then, a new DAN is generated for the new MLI id.
					Log.log(Log.DEBUG, className, methodName,
							"Entering else part since MLI id changed");
					// mliIds.add(mliId) ;

					// administrator.getAllUsers(mliId) ;
					Log.log(Log.DEBUG, className, methodName,
							"Addition of MliId to arraylist completed");
					if (emailMessage != null && !emailMessage.equals("")) {
						try {
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						mliInfo = registrationDAO.getMemberDetails(bankId,
								zoneId, branchId);

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.DEBUG, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						for (int j = 0; j < userSize; ++j) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId());
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getEmailId());
						}

						if (emailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + cgdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////
							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}

						emailMessage = ""; // resetting the message.
						noOfCGPANs = 0;

						/** New DAN generation begins here **/
						Log.log(Log.DEBUG, className, methodName,
								"Before generating New CGDAN: ");
						cgdanNo = rpHelper.generateCGDANId(tempBankId,
								tempZoneId, tempBranchId, connection);
						Log.log(Log.DEBUG, className, methodName, "New CGDAN: "
								+ cgdanNo);

						demandAdvice.setDanNo(cgdanNo);

						insertDANDetails(demandAdvice, connection);

						guaranteeAmount = rpProcessor
								.calculateGuaranteeFee(application);

						Log.log(Log.DEBUG, className, methodName,
								"Guarantee Amount = " + guaranteeAmount[4]);

						demandAdvice.setAmountRaised(guaranteeAmount[4]);

						insertPANDetailsForDAN(demandAdvice, connection);

						// update the guarantee fee for the CGPAN.
						GuaranteeFee guaranteeFee = new GuaranteeFee();
						guaranteeFee.setGuaranteeAmount(demandAdvice
								.getAmountRaised());
						guaranteeFee.setCgpan(demandAdvice.getCgpan());

						updateGuaranteeFee(guaranteeFee, user.getUserId(),
								connection);

						emailMessage += "DAN No : " + demandAdvice.getDanNo()
								+ "\n";
						++noOfCGPANs;
						/** New DAN generation ends here **/

					}

					/**
					 * mliId is updated with the new Mli details. Further
					 * applications will be checked with this MLI id
					 **/
					mliId = tempBankId.concat(tempZoneId).concat(tempBranchId);

					if (emailMessage == "" || emailMessage == null) {
						sendMailValue = false;
					} else {
						sendMailValue = true;
					}

				}

				application = null;
			}
			/** End of Application Loop **/

			if (sendMailValue) {
				try {
					users = administrator.getAllUsers(mliId);
				} catch (NoUserFoundException exception) {
					Log.log(Log.WARNING, className, methodName,
							"Exception getting user details for the MLI. Error="
									+ exception.getMessage());
				} catch (DatabaseException exception) {
					Log.log(Log.WARNING, className, methodName,
							"Exception getting user details for the MLI. Error="
									+ exception.getMessage());
				}

				mailer = new Mailer();

				mliInfo = registrationDAO.getMemberDetails(tempBankId,
						tempZoneId, tempBranchId);

				mailPrivelege = mliInfo.getMail();
				emailPrivelege = mliInfo.getEmail();
				hardCopyPrivelege = mliInfo.getHardCopy();

				Log.log(Log.DEBUG, className, methodName,
						"Getting Email Id for MLI id completed");
				userSize = users.size();
				emailIds = new ArrayList();
				for (int j = 0; j < userSize; j++) {
					mailUser = (User) users.get(j);
					emailIds.add(mailUser.getUserId());
					// emailIds.add(mailUser.getEmailId()) ;
					// Log.log(Log.DEBUG,
					// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
					// ;
					Log.log(Log.DEBUG, className, methodName, "Member Id"
							+ mliId + ", User email " + mailUser.getUserId());
				}

				if (emailIds != null) {
					Log.log(Log.DEBUG, className, methodName,
							"Before instantiating message");
					subject = "New Demand Advice(" + cgdanNo + ") generated";
					Log.log(Log.DEBUG, className, methodName, "Subject = "
							+ subject);
					Log.log(Log.DEBUG, className, methodName,
							"Email Message = " + emailMessage);
					Message message = new Message(emailIds, null, null,
							subject, emailMessage);
					message.setFrom(fromEmail);
					// try
					// {
					// mailer.sendEmail(message);
					// Commented by path on 5jan2007 due to performence issue
					// administrator.sendMail(message);
					// ///////////////////////////////////////////////////////

					/*
					 * }catch(MailerException mailerException) {
					 * Log.log(Log.WARNING,
					 * className,methodName,"Exception sending Mail. Error="
					 * +mailerException.getMessage()) ; }
					 */// administrator(message) ;
					Log.log(Log.DEBUG, className, methodName,
							"After instantiating message");
					// mailStatus = mailer.sendEmail(message) ;
				}

				emailMessage = ""; // resetting the message.

			}
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		}

		Log.log(Log.INFO, className, methodName, "Exited");
	}

	// //////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method picks up all approved applications and generates DAN for the
	 * same. APPLICATION
	 * 
	 * @author GU14477 As per new
	 */
	public void generateCGDAN(User user, String memberId)
			throws DatabaseException, NoApplicationFoundException {
		RpProcessor rpProcessor = new RpProcessor();
		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();

		String methodName = "generateCGDAN";
		Log.log(Log.INFO, className, methodName, "Entered");
		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;
		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";
		double guaranteeAmount[];
		/**
		 * MLI details captured inside application loop for each application.
		 * 
		 * If this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		String subject = "";
		String emailMessage = "";
		boolean mailStatus = false;
		ArrayList users = null;
		ArrayList emailIds = null;
		MLIInfo mliInfo = null;
		String mailPrivelege = "";
		String emailPrivelege = "";
		String hardCopyPrivelege = "";
		String userId = user.getUserId();
		String fromEmail = user.getUserId();
		User mailUser = null;
		/** End of variables used for mailing **/

		String cgdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated
		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN
		ArrayList guaranteeValues = null; // Arraylist used to get the approved
											// application details that are
											// pending for generation of DAN.
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop
		Application application = null;
		// Added by sukant@PathInfotech on 05/02/2007
		String newMemberId = null;

		Connection connection = DBConnection.getConnection(false);
		try {
			// returns an arraylist of approved applications for the day
			// memberwise.
			Log.log(Log.DEBUG, className, methodName,
					"Before getting Approved Applications Member Wise");
			// Added By Path (Ritesh) on 1NOV2006
			// GET ALL BRANCH OFFICESS OF ONE MLI AND START LOOP TO GET
			// GUARANTEE FEE AND GENERATE CGPAN
			// AS IT WAS CREATING EARLIER
			// LOOP STARTS HERE BY PATH RITESH

			CallableStatement allMembersPath = null;
			String bankIdPath = "";
			// Added this code by Ritesh Path on 9Nov2006
			String restId = "";
			// //////////////////////////////////////////
			// System.out.println("MemberId:"+memberId);
			if (!memberId.equals(Constants.ALL)) {
				bankIdPath = memberId.substring(0, 4);
				restId = memberId.substring(4, 12);
			} else {
				bankIdPath = Constants.ALL;
			}
			// System.out.println("bankIdPath  = "+bankIdPath+"-"+restId);

			if (memberId.equals(Constants.ALL) || restId.equals("00000000")) {
				allMembersPath = connection
						.prepareCall("{?=call packGetAllMembersPath.funcGetAllMembersForOneMli(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath);
			} else {
				allMembersPath = connection
						.prepareCall("{?=call packGetAllMembersPath.funcGetAllMembersForOneMli1(?,?,?)}");
				allMembersPath.registerOutParameter(1, Types.INTEGER);
				allMembersPath.registerOutParameter(2, Constants.CURSOR);
				allMembersPath.registerOutParameter(3, Types.VARCHAR);
				allMembersPath.setString(4, bankIdPath + restId);
			}

			allMembersPath.execute();

			int functionReturn = allMembersPath.getInt(1);
			String error = allMembersPath.getString(3);
			if (functionReturn == Constants.FUNCTION_FAILURE) {
				allMembersPath.close();
				allMembersPath = null;
				throw new DatabaseException(error);
			} else {
				int tmpreccount = 0;
				ResultSet membersResult = (ResultSet) allMembersPath
						.getObject(2);
				// if(membersResult.next())
				// {
				// Modified by Sukant@PathInfotech.com on 02/Feb/2007
				while (membersResult.next()) {
					tmpreccount++;
					// System.out.println("tmpreccount PATH >>> = "+tmpreccount);

					newMemberId = membersResult.getString(1);

					if ((newMemberId.equals(null)) || (newMemberId.equals(""))) {
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
					}

					// System.out.println("memberId = "+newMemberId);

					guaranteeValues = getGuaranteeFeeMemberWise(newMemberId);
					Log.log(Log.DEBUG, className, methodName,
							"After Getting Approved Applications");

					if (guaranteeValues == null) {
						Log.log(Log.DEBUG, className, methodName,
								"No applications fetched");
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
						// return;
					}
					size = guaranteeValues.size();
					if (size == 0) {
						Log.log(Log.DEBUG, className, methodName,
								"No. of applications fetched = 0");
						throw new DatabaseException(
								"No Applications Available For CGDAN Generation");
						// return ;
					}

					/**
					 * Following lines of code does the following sequence. 1.
					 * Picks up the first DemandAdvice object from the
					 * arraylist. 2. Initialises mliId with the first MLI
					 * retrieved 3. As the DemandAdvice object also has CGPAN
					 * information, new DAN is generated and the PAN details are
					 * inserted in the database
					 * **/
					demandAdvice = (DemandAdvice) guaranteeValues.get(0);
					bankId = demandAdvice.getBankId();
					zoneId = demandAdvice.getZoneId();
					branchId = demandAdvice.getBranchId();
					mliId = (bankId.concat(zoneId)).concat(branchId);
					Log.log(Log.DEBUG, className, methodName, "Member Id = "
							+ mliId + ", CGPAN = " + demandAdvice.getCgpan());

					application = applicationDAO.getAppForCgpan(mliId,
							demandAdvice.getCgpan());

					Log.log(Log.DEBUG, className, methodName,
							"Application object = " + application);

					cgdanNo = rpHelper.generateCGDANId(bankId, zoneId,
							branchId, connection);

					Log.log(Log.DEBUG, className, methodName,
							"Generating CGDAN for first application, CGDAN = "
									+ cgdanNo);

					demandAdvice.setDanNo(cgdanNo);

					// Log.log(Log.DEBUG,
					// className,methodName,"Setting DAN Type for the first CGDAN, Dan Type = "+RpConstants.DAN_TYPE_CGDAN)
					// ;

					demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);

					// Log.log(Log.DEBUG,
					// className,methodName,"After Setting DAN Type for the first CGDAN, Dan Type = "+demandAdvice.getDanType())
					// ;

					demandAdvice.setDanGeneratedDate(danGeneratedDate);
					dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
					demandAdvice.setDanDueDate(dueDate);
					demandAdvice.setUserId(userId);

					Log.log(Log.DEBUG, className, methodName,
							"After Generating CGDAN");
					insertDANDetails(demandAdvice, connection);
					mliId = bankId.concat(zoneId).concat(branchId);
					/**
					 * End of generating DAN for the first application
					 * retrieved.
					 **/
					noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																			// exceeds
																			// 20
					// System.out.println("PATH in generateCGDAN noOfCGPANsLimit = "+noOfCGPANsLimit);
					Log.log(Log.DEBUG, className, methodName,
							"Starting Application Loop");
					boolean sendMailValue = false;
					/** Starting application loop **/
					for (int i = 0; i < size; ++i) {
						demandAdvice = (DemandAdvice) guaranteeValues.get(i);
						demandAdvice.setDanNo(cgdanNo);
						demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);
						demandAdvice.setDanGeneratedDate(danGeneratedDate);
						dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
						demandAdvice.setDanDueDate(dueDate);
						// Set expiry date for DAN
						demandAdvice.setUserId(userId);
						Log.log(Log.DEBUG,
								className,
								methodName,
								"Inside Loop: CGDAN = " + cgdanNo
										+ ", CGPAN = "
										+ demandAdvice.getCgpan());
						tempBankId = demandAdvice.getBankId();
						tempZoneId = demandAdvice.getZoneId();
						tempBranchId = demandAdvice.getBranchId();
						tempMLIId = tempBankId.concat(tempZoneId).concat(
								tempBranchId);
						// System.out.println("demandAdvice.getCgpan() = "+demandAdvice.getCgpan());
						application = applicationDAO.getApplication(tempMLIId,
								demandAdvice.getCgpan(), "");
						guaranteeAmount = rpProcessor
								.calculateGuaranteeFee(application);
						Log.log(Log.DEBUG, className, methodName,
								"Guarantee Amount = " + guaranteeAmount[4]);
						demandAdvice.setAmountRaised(Math
								.round(guaranteeAmount[4]));
						Log.log(Log.DEBUG, className, methodName,
								"Temp MLI Id = " + tempMLIId);
						sendMailValue = true;
						/**
						 * Checks if the new MLI Id is the same as the previous
						 * MLI id or not
						 **/
						if (mliId.equalsIgnoreCase(tempMLIId)) { // If the newly
																	// obtained
																	// mliId is
																	// equal to
																	// the
																	// previous
																	// MliId
																	// value
							Log.log(Log.DEBUG, className, methodName,
									"MLI id equal temp mli id");
							if (noOfCGPANs >= noOfCGPANsLimit) { // If no. of
																	// CGPANs
																	// for the
																	// same MLI
																	// id
																	// exceeds
																	// the
																	// limit,
																	// new DAN
																	// is
																	// generated
																	// for the
																	// remaining
																	// CGPANs of
																	// the same
																	// MLI.
								Log.log(Log.DEBUG, className, methodName,
										"No of cgpans exceeded limit");
								try {
									users = administrator.getAllUsers(mliId);
								} catch (NoUserFoundException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								} catch (DatabaseException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								}
								mailer = new Mailer();
								mliInfo = registrationDAO.getMemberDetails(
										bankId, zoneId, branchId);
								mailPrivelege = mliInfo.getMail();
								emailPrivelege = mliInfo.getEmail();
								hardCopyPrivelege = mliInfo.getHardCopy();
								Log.log(Log.DEBUG, className, methodName,
										"Getting Email Id for MLI id completed");
								userSize = users.size();
								emailIds = new ArrayList();
								// System.out.println("PATH in generateCGDAN userSize = "+userSize);
								for (int j = 0; j < userSize; j++) {
									mailUser = (User) users.get(j);
									emailIds.add(mailUser.getUserId());
									Log.log(Log.DEBUG, className, methodName,
											"Member Id" + mliId
													+ ", User email "
													+ mailUser.getUserId());
								}
								if (emailIds != null) {
									Log.log(Log.DEBUG, className, methodName,
											"Before instantiating message");
									subject = "New Demand Advice(" + cgdanNo
											+ ") generated";
									Log.log(Log.DEBUG, className, methodName,
											"Subject = " + subject);
									Log.log(Log.DEBUG, className, methodName,
											"Email Message = " + emailMessage);
									Message message = new Message(emailIds,
											null, null, subject, emailMessage);
									message.setFrom(fromEmail);
									// Commented by path on 5jan2007 due to
									// performence issue
									// administrator.sendMail(message);
									// ///////////////////////////////////////////////////////

									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
								}
								emailMessage = ""; // resetting the message.
								noOfCGPANs = 0;
								cgdanNo = rpHelper.generateCGDANId(bankId,
										zoneId, branchId, connection);
								demandAdvice.setDanNo(cgdanNo);
								insertDANDetails(demandAdvice, connection);
								emailMessage += "DAN No : "
										+ demandAdvice.getDanNo() + "\n";
								noOfCGPANs = 0;
								sendMailValue = false;
							} else {
								sendMailValue = true;
							}
							insertPANDetailsForDAN(demandAdvice, connection);
							// update the guarantee fee for the CGPAN.
							GuaranteeFee guaranteeFee = new GuaranteeFee();
							guaranteeFee.setGuaranteeAmount(demandAdvice
									.getAmountRaised());
							guaranteeFee.setCgpan(demandAdvice.getCgpan());
							// System.out.println("before updating gfee11111111");
							updateGuaranteeFee(guaranteeFee, user.getUserId(),
									connection);
							// System.out.println("after updating gfee111111111");
							Log.log(Log.DEBUG, className, methodName,
									"After Insert from " + methodName);
							++noOfCGPANs;
							emailMessage += "CGPAN - "
									+ demandAdvice.getCgpan()
									+ ", GuaranteeAmount - "
									+ demandAdvice.getAmountRaised() + "\n";
							// System.out.println("emailMessage = "+emailMessage);
						} else { // This means MLI id is changed. Mail will be
									// sent to the previous MLI with the
									// generated DAN details. Then, a new DAN is
									// generated for the new MLI id.
							Log.log(Log.DEBUG, className, methodName,
									"Entering else part since MLI id changed");
							Log.log(Log.DEBUG, className, methodName,
									"Addition of MliId to arraylist completed");
							if (emailMessage != null
									&& !emailMessage.equals("")) {
								try {
									users = administrator.getAllUsers(mliId);
								} catch (NoUserFoundException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								} catch (DatabaseException exception) {
									Log.log(Log.WARNING, className, methodName,
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								}
								mliInfo = registrationDAO.getMemberDetails(
										bankId, zoneId, branchId);
								mailPrivelege = mliInfo.getMail();
								emailPrivelege = mliInfo.getEmail();
								hardCopyPrivelege = mliInfo.getHardCopy();
								Log.log(Log.DEBUG, className, methodName,
										"Getting Email Id for MLI id completed");
								userSize = users.size();
								emailIds = new ArrayList();
								for (int j = 0; j < userSize; ++j) {
									mailUser = (User) users.get(j);
									emailIds.add(mailUser.getUserId());
									Log.log(Log.DEBUG, className, methodName,
											"Member Id" + mliId
													+ ", User email "
													+ mailUser.getEmailId());
								}
								if (emailIds != null) {
									Log.log(Log.DEBUG, className, methodName,
											"Before instantiating message");
									subject = "New Demand Advice(" + cgdanNo
											+ ") generated";
									Log.log(Log.DEBUG, className, methodName,
											"Subject = " + subject);
									Log.log(Log.DEBUG, className, methodName,
											"Email Message = " + emailMessage);
									Message message = new Message(emailIds,
											null, null, subject, emailMessage);
									message.setFrom(fromEmail);
									// Commented by path on 5jan2007 due to
									// performence issue
									// administrator.sendMail(message);
									// ///////////////////////////////////////////////////////

									Log.log(Log.DEBUG, className, methodName,
											"After instantiating message");
									// mailStatus = mailer.sendEmail(message) ;
								}
								emailMessage = ""; // resetting the message.
								noOfCGPANs = 0;
								/** New DAN generation begins here **/
								Log.log(Log.DEBUG, className, methodName,
										"Before generating New CGDAN: ");
								cgdanNo = rpHelper.generateCGDANId(tempBankId,
										tempZoneId, tempBranchId, connection);
								Log.log(Log.DEBUG, className, methodName,
										"New CGDAN: " + cgdanNo);

								demandAdvice.setDanNo(cgdanNo);

								insertDANDetails(demandAdvice, connection);

								guaranteeAmount = rpProcessor
										.calculateGuaranteeFee(application);

								Log.log(Log.DEBUG, className, methodName,
										"Guarantee Amount = " + guaranteeAmount[4]);

								demandAdvice.setAmountRaised(guaranteeAmount[4]);

								insertPANDetailsForDAN(demandAdvice, connection);

								// update the guarantee fee for the CGPAN.
								GuaranteeFee guaranteeFee = new GuaranteeFee();
								guaranteeFee.setGuaranteeAmount(demandAdvice
										.getAmountRaised());
								guaranteeFee.setCgpan(demandAdvice.getCgpan());
								// System.out.println("before updating gfee222222");
								updateGuaranteeFee(guaranteeFee,
										user.getUserId(), connection);
								// System.out.println("after updating gfee222222");
								emailMessage += "DAN No : "
										+ demandAdvice.getDanNo() + "\n";
								++noOfCGPANs;
								/** New DAN generation ends here **/

							}

							/**
							 * mliId is updated with the new Mli details.
							 * Further applications will be checked with this
							 * MLI id
							 **/
							mliId = tempBankId.concat(tempZoneId).concat(
									tempBranchId);
							if (emailMessage == "" || emailMessage == null) {
								sendMailValue = false;
							} else {
								sendMailValue = true;
							}

						}
						application = null;
					}
					/** End of Application Loop **/
					if (sendMailValue) {
						try {
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						mailer = new Mailer();

						mliInfo = registrationDAO.getMemberDetails(tempBankId,
								tempZoneId, tempBranchId);

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.DEBUG, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						for (int j = 0; j < userSize; j++) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId());
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getUserId());
						}

						if (emailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + cgdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////

							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}
						emailMessage = ""; // resetting the message.
					}
				}
				// Added by sukant@pathinfotech 02/Feb/2007
				if ((newMemberId.equals(null)) || (newMemberId.equals(""))) {
					throw new DatabaseException(
							"No Applications Available For CGDAN Generation");
				}

				// System.out.println("memberId = "+newMemberId);

				// }
				// else
				// {
				// throw new
				// DatabaseException("No Applications Available For CGDAN Generation");
				// }

			}

			// LOOP ENDS HERE BY PATH RITESH
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		}

		Log.log(Log.INFO, className, methodName, "Exited");
	}

	/**
	 * This method will be invoked whenever loan amount has to be reapproved.
	 * The method will pass CGPAN, approved amount and reapproval comments as
	 * parameters.
	 * 
	 * @param application
	 * @return Arraylist
	 * @roseuid 39A5207C02AF
	 */
	public ArrayList reapproveLoanAmount(Application application,
			double reapprovedAmt, User user, String contextPath)
			throws DatabaseException, MissingCardRateException,
			MissingServiceFeeRateException, DANNotGeneratedException,
			NoApplicationFoundException, MessageException {

		Log.log(Log.INFO, "RpProcessor", "reapproveLoanAmount", "Entered");

		RpProcessor rpProcessor = new RpProcessor();
		DemandAdvice demandAdvice = null;
		RpHelper rpHelper = null;
		ServiceFee serviceFee = null;

		ArrayList shortExcessAmounts = null;
		ArrayList demandAdvices = null;

		int sizeOfDemandAdvices = 0;

		/*double oldAmount[];
		double newAmount[];*/
		//double guaranteeAmount[] = 0;
		double serviceFeeAmount = 0;

		String danType = "";
		String allocated = "";
		String appropriated = "";
		String cgpan = "";
		String bankId = "";
		String branchId = "";
		String zoneId = "";
		String mliId = "";

		Date danGeneratedDate;
		Date danDueDate;
		Date danExpiryDate;
		Date fDisbursementDate;

		//Diksha
		double oldAmount = 0.0D;
        double newAmount[] = new double[4];
        double guaranteeAmount = 0.0D;
        //Diksha end
		if (application == null) {
			Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
					"Application is null ");
			return null;
		}
		demandAdvices = getGeneratedDANs(application);

		if (demandAdvices == null || demandAdvices.size() == 0) {
			Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
					"No demand advices generated ");

			return null;
		}
		sizeOfDemandAdvices = demandAdvices.size();

		Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
				"sizeOfDemandAdvices " + sizeOfDemandAdvices);

		Properties accCodes = new Properties();
		Log.log(Log.DEBUG, "RPDAO", "reapproveLoanAmount", "path "
				+ contextPath);
		File tempFile = new File(contextPath + "\\WEB-INF\\classes",
				RpConstants.AC_CODE_FILE_NAME);
		Log.log(Log.DEBUG, "RPDAO", "reapproveLoanAmount", "file opened "
				+ tempFile.getAbsolutePath());
		File accCodeFile = new File(tempFile.getAbsolutePath());
		try {
			FileInputStream fin = new FileInputStream(accCodeFile);
			accCodes.load(fin);
		} catch (FileNotFoundException fe) {
			// throw new MessageException("Could not load Account Codes.");
		} catch (IOException ie) {
			// throw new MessageException("Could not load Account Codes.");
		}
		Log.log(Log.DEBUG, "RPAction", "getPaymentsMade", "props loaded ");

		Log.log(Log.DEBUG, "RPAction", "getPaymentsMade",
				"code " + accCodes.getProperty(RpConstants.BANK_AC));

		ParameterMaster parameters = new Administrator().getParameter();

		Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
				"Excess and short limits " + parameters.getExcessLimit() + ","
						+ parameters.getShortLimit());

		double differentialAmount = 0;
		double originalApprovedAmount = 0;

		ApplicationProcessor appProcessor = new ApplicationProcessor();

		Application applicationTemp = appProcessor.getPartApplication(null,
				application.getCgpan(), "");

		if (applicationTemp.getReapprovedAmount() == 0) {
			Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
					"Reapproved amount is zero");
			originalApprovedAmount = applicationTemp.getApprovedAmount();
		} else {
			originalApprovedAmount = applicationTemp.getReapprovedAmount();
			System.out.println("Re Approved-originalApprovedAmount:"
					+ originalApprovedAmount);
		}

		// originalApprovedAmount=10000;

		rpHelper = new RpHelper();

		Log.log(Log.DEBUG,
				"RpProcessor",
				"reapproveLoanAmount",
				"approved and reapproved amts "
						+ applicationTemp.getApprovedAmount() + ","
						+ applicationTemp.getReapprovedAmount());

		Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
				"originalApprovedAmount " + originalApprovedAmount);

		Connection connection = DBConnection.getConnection(false);

		try {

			for (int i = 0; i < sizeOfDemandAdvices; ++i) {
				Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
						"entering demand advice");
				demandAdvice = (DemandAdvice) demandAdvices.get(i);
				danType = demandAdvice.getDanType();

				Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
						"danType " + danType);

				String paymentId = demandAdvice.getPaymentId();
				PaymentDetails paymentDetails = null;
				if (paymentId != null && !paymentId.equals("")) {
					paymentDetails = getPaymentDetails(paymentId);
				}

				String danId = "";
				String narration = "";

				ArrayList vouchers = new ArrayList();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String newDanId = "";

				boolean voucherGen = false;
				boolean newDanGenerated = false;

				String subject = "";
				String emailMessage = "";
				boolean mailStatus = false;
				ArrayList users = null;
				ArrayList emailIds = null;
				MLIInfo mliInfo = null;
				String mailPrivelege = "";
				String emailPrivelege = "";
				String hardCopyPrivelege = "";
				String userId = user.getUserId();
				String fromEmail = user.getUserId();
				User mailUser = null;

				if (danType.equalsIgnoreCase(RpConstants.DAN_TYPE_CGDAN)) {
					allocated = demandAdvice.getAllocated();
					// System.out.println("Allocated:"+allocated);
					appropriated = demandAdvice.getAppropriated();
					// System.out.println("Appropriated:"+appropriated);

					Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
							"CGPAN " + demandAdvice.getCgpan());

					Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
							"allocated " + allocated);

					double oldGFeeAmount[] = rpProcessor
							.calculateGuaranteeFee(applicationTemp);
					// System.out.println("Old G.Fee Amount:"+oldGFeeAmount);
					applicationTemp.setReapprovedAmount(reapprovedAmt);
					newAmount = rpProcessor
							.calculateGuaranteeFee(applicationTemp);
					// System.out.println("New Amount:"+newAmount);
					Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount",
							"new dan amount " + newAmount);

					  differentialAmount = 0.0D;
					/*differentialAmount = oldGFeeAmount - newAmount;*/
					
					// System.out.println("differentialAmount-Guarantee Fee Amount Difference:"+differentialAmount);

					Log.log(Log.DEBUG, "RpProcessor", "reapproveLoanAmount", ""
							+ "differentialAmount " + differentialAmount);
					int excessId = 0;

					if (differentialAmount > 0) {
						Log.log(Log.DEBUG, "RpProcessor",
								"reapproveLoanAmount",
								"Differential amount is greater than zero.");

						if (parameters.getExcessLimit() > differentialAmount) {
							Log.log(Log.DEBUG, "RpProcessor",
									"reapproveLoanAmount",
									"Within Excess limits ");
							// adjust within the excess limits.
							if (appropriated.equals("Y")) {
								demandAdvice.setPaymentId(paymentId);
							} else {
								demandAdvice.setPaymentId("");
							}

							// excessId=updateExcessAmountDetails(demandAdvice,differentialAmount,
							// connection);
							voucherGen = true;
						} else {
							if (allocated.equals("Y")) {
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"Beyond Excess limits. Generate Refund advices ");
								// Excess amount is beyond the limit. Can not be
								// approved.
								// Generate refund advice.
								/*
								 * RefundAdvice refundAdvice=new RefundAdvice();
								 * 
								 * refundAdvice.setDanId(demandAdvice.getDanNo())
								 * ;
								 * refundAdvice.setCgpan(demandAdvice.getCgpan(
								 * ));
								 * refundAdvice.setAmount(differentialAmount);
								 * refundAdvice
								 * .setBankId(demandAdvice.getBankId());
								 * refundAdvice
								 * .setBranchId(demandAdvice.getBranchId());
								 * refundAdvice
								 * .setZoneId(demandAdvice.getZoneId());
								 * 
								 * insertRefundAdviceDetails(refundAdvice,user.
								 * getUserId());
								 */
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"b4 insert payment voucher");

								VoucherDetail voucherDetail = new VoucherDetail();
								vouchers.clear();
								voucherDetail.setBankGLCode(accCodes
										.getProperty(RpConstants.BANK_AC));
								voucherDetail.setBankGLName("");
								voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
								voucherDetail.setAmount(0 - differentialAmount);
								voucherDetail
										.setVoucherType(RpConstants.PAYMENT_VOUCHER);
								danId = "";

								Voucher voucher = new Voucher();
								/*
								 * voucher.setAcCode("Bank A/c");
								 * voucher.setPaidTo("CGTSI");
								 * voucher.setDebitOrCredit("C");
								 * voucher.setInstrumentDate
								 * (dateFormat.format(paymentDetails
								 * .getInstrumentDate()));
								 * voucher.setInstrumentNo
								 * (paymentDetails.getInstrumentNo());
								 * voucher.setInstrumentType
								 * (paymentDetails.getInstrumentType());
								 * voucher.
								 * setAmountInRs("-"+differentialAmount);
								 * vouchers.add(voucher); voucher=null;
								 */

								voucher = new Voucher();
								voucher.setAcCode(accCodes
										.getProperty(RpConstants.EXCESS_AC));
								voucher.setPaidTo("CGTSI");
								voucher.setDebitOrCredit("D");
								voucher.setInstrumentDate(dateFormat
										.format(paymentDetails
												.getInstrumentDate()));
								voucher.setInstrumentNo(paymentDetails
										.getInstrumentNo());
								voucher.setInstrumentType(paymentDetails
										.getInstrumentType());
								voucher.setAmountInRs(differentialAmount + "");
								vouchers.add(voucher);
								voucher = null;

								narration = narration + "Payment Id: "
										+ paymentId;
								narration = narration + " Member Id: "
										+ demandAdvice.getBankId()
										+ demandAdvice.getZoneId()
										+ demandAdvice.getBranchId();
								narration = narration + " Dan No: "
										+ demandAdvice.getDanNo();
								narration = narration + " CGPAN: "
										+ demandAdvice.getCgpan();

								voucherDetail.setNarration(narration);
								voucherDetail.setVouchers(vouchers);

								// insertVoucherDetails(voucherDetail,
								// user.getUserId(), connection);
								vouchers.clear();
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"inserted payment voucher");

								voucherGen = true;

							} else {
								double oldAmt = Math.abs(demandAdvice
										.getCancelledAmount()
										- demandAdvice.getAmountRaised());
								/*
								 * if (oldAmt - differentialAmount < 0 ) {
								 * Log.log
								 * (Log.DEBUG,"RpProcessor","reapproveLoanAmount"
								 * ,"b4 insert payment voucher");
								 * 
								 * VoucherDetail voucherDetail = new
								 * VoucherDetail();
								 * voucherDetail.setBankGLCode("");
								 * voucherDetail
								 * .setBankGLName(paymentDetails.getCollectingBank
								 * ());
								 * voucherDetail.setDeptCode(RpConstants.RP_CGTSI
								 * ); voucherDetail.setAmount(oldAmt -
								 * differentialAmount);
								 * voucherDetail.setVoucherType
								 * (RpConstants.PAYMENT_VOUCHER); danId="";
								 * 
								 * Voucher voucher = new Voucher();
								 * voucher.setAcCode(demandAdvice.getDanType());
								 * voucher.setPaidTo("CGTSI");
								 * voucher.setDebitOrCredit("C");
								 * voucher.setInstrumentDate
								 * (dateFormat.format(paymentDetails
								 * .getInstrumentDate()));
								 * voucher.setInstrumentNo
								 * (paymentDetails.getInstrumentNo());
								 * voucher.setInstrumentType
								 * (paymentDetails.getInstrumentType());
								 * voucher.setAmountInRs((oldAmt -
								 * differentialAmount)+"");
								 * vouchers.add(voucher); voucher=null;
								 * 
								 * narration = narration + "Payment Id: " +
								 * paymentId; narration = narration +
								 * " Member Id: " +
								 * demandAdvice.getBankId()+demandAdvice
								 * .getZoneId()+demandAdvice.getBranchId();
								 * narration = narration + " Dan No: " +
								 * demandAdvice.getDanNo(); narration =
								 * narration + " CGPAN: " +
								 * demandAdvice.getCgpan();
								 * 
								 * voucherDetail.setNarration(narration);
								 * voucherDetail.setVouchers(vouchers);
								 * 
								 * insertVoucherDetails(voucherDetail,
								 * user.getUserId(), connection);
								 * vouchers.clear();
								 * Log.log(Log.DEBUG,"RpProcessor"
								 * ,"reapproveLoanAmount"
								 * ,"inserted payment voucher");
								 * 
								 * voucherGen=true; }
								 */

								DemandAdvice newDemandAdvice = new DemandAdvice();

								// insert new dan for differential amount
								newDemandAdvice.setBankId(demandAdvice
										.getBankId());
								newDemandAdvice.setZoneId(demandAdvice
										.getZoneId());
								newDemandAdvice.setBranchId(demandAdvice
										.getBranchId());

								newDanId = rpHelper.generateCGDANId(
										demandAdvice.getBankId(),
										demandAdvice.getZoneId(),
										demandAdvice.getBranchId(), connection);
								newDemandAdvice.setDanNo(newDanId);
								newDemandAdvice.setCgpan(demandAdvice
										.getCgpan());
								newDemandAdvice.setDanType(demandAdvice
										.getDanType());
								newDemandAdvice
										.setDanGeneratedDate(new java.util.Date());
								newDemandAdvice.setUserId(user.getUserId());
								// cancel the generated amount and update with
								// the new amount.
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										" excess Not allocated.");
								// System.out.println("newDanId:"+newDanId);
								Date dueDate = rpProcessor.getPANDueDate(
										newDemandAdvice, null);

								newDemandAdvice.setDanDueDate(dueDate);
								newDemandAdvice.setAmountRaised(Math
										.round(newAmount[4]));

								insertDANDetails(newDemandAdvice, connection);
								insertPANDetailsForDAN(newDemandAdvice,
										connection);

								Administrator administrator = new Administrator();
								try {
									mliId = demandAdvice.getBankId()
											+ demandAdvice.getZoneId()
											+ demandAdvice.getBranchId();
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount",
											"getting users for " + mliId);
									users = administrator.getAllUsers(mliId);
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount", "users got");
								} catch (NoUserFoundException exception) {
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount",
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								} catch (DatabaseException exception) {
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount",
											"Exception getting user details for the MLI. Error="
													+ exception.getMessage());
								}

								Mailer mailer = new Mailer();
								RegistrationDAO registrationDAO = new RegistrationDAO();

								mliInfo = registrationDAO.getMemberDetails(
										demandAdvice.getBankId(),
										demandAdvice.getZoneId(),
										demandAdvice.getBranchId());
								Log.log(Log.WARNING, className,
										"reapproveLoanAmount", "users got");

								mailPrivelege = mliInfo.getMail();
								emailPrivelege = mliInfo.getEmail();
								hardCopyPrivelege = mliInfo.getHardCopy();

								Log.log(Log.DEBUG, className,
										"reapproveLoanAmount",
										"Getting Email Id for MLI id completed");
								int userSize = users.size();
								emailIds = new ArrayList();
								for (int j = 0; j < userSize; j++) {
									mailUser = (User) users.get(j);
									emailIds.add(mailUser.getUserId());
									// emailIds.add(mailUser.getEmailId()) ;
									// Log.log(Log.DEBUG,
									// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
									// ;
									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount", "Member Id"
													+ mliId + ", User email "
													+ mailUser.getUserId());
								}

								if (emailIds != null) {
									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount",
											"Before instantiating message");
									subject = "New Demand Advice(" + newDanId
											+ ") generated";
									emailMessage = "CGPAN - "
											+ newDemandAdvice.getCgpan()
											+ ", GuaranteeAmount - "
											+ newDemandAdvice.getAmountRaised()
											+ "\n";
									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount", "Subject = "
													+ subject);
									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount",
											"Email Message = " + emailMessage);
									Message message = new Message(emailIds,
											null, null, subject, emailMessage);
									message.setFrom(fromEmail);
									// try
									// {
									// mailer.sendEmail(message);
									// Commented by path on 5jan2007 due to
									// performence issue
									// administrator.sendMail(message);
									// ///////////////////////////////////////////////////////

									/*
									 * }catch(MailerException mailerException) {
									 * Log.log(Log.WARNING,
									 * className,methodName,
									 * "Exception sending Mail. Error="
									 * +mailerException.getMessage()) ; }
									 */// administrator(message) ;
									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount",
											"After instantiating message");
									// mailStatus = mailer.sendEmail(message) ;
								}

								// cancel the previous dan-cgpan by setting the
								// amount cancelled = amount raised
								newDemandAdvice = new DemandAdvice();
								newDemandAdvice.setBankId(demandAdvice
										.getBankId());
								newDemandAdvice.setZoneId(demandAdvice
										.getZoneId());
								newDemandAdvice.setBranchId(demandAdvice
										.getBranchId());

								newDemandAdvice.setDanNo(demandAdvice
										.getDanNo());
								newDemandAdvice.setCgpan(demandAdvice
										.getCgpan());
								newDemandAdvice.setDanType(demandAdvice
										.getDanType());
								newDemandAdvice
										.setDanGeneratedDate(new java.util.Date());
								newDemandAdvice.setUserId(user.getUserId());
								dueDate = rpProcessor.getPANDueDate(
										newDemandAdvice, null);

								newDemandAdvice.setDanDueDate(dueDate);
								// cancel the generated amount and update with
								// the new amount.
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										" excess Not allocated.");

								double cancelledAmount = demandAdvice
										.getAmountRaised();
								double amountRaised = demandAdvice
										.getAmountRaised();

								// double
								// amountRaised=oldAmt+demandAdvice.getAmountRaised()-differentialAmount;

								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"cancelledAmount,amountRaised "
												+ cancelledAmount + ", "
												+ amountRaised);

								newDemandAdvice
										.setCancelledAmount(cancelledAmount);
								newDemandAdvice.setAmountRaised(amountRaised);
								newDemandAdvice.setNewDanNo(newDanId);

								// cancelAndUpdateDANDetails(newDemandAdvice,
								// connection);
								voucherGen = false;

							}
						}
						/*
						 * if (allocated.equalsIgnoreCase("Y")) {
						 * voucherGen=true; }
						 */
					} else {
						Log.log(Log.DEBUG, "RpProcessor",
								"reapproveLoanAmount",
								"Differential amount is lesser than zero.");

						if (differentialAmount != 0) {
							differentialAmount = Math.abs(differentialAmount);
							if (parameters.getShortLimit() > Math
									.abs(differentialAmount)) {
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"Within Short limits ");
								// Adjust within short limits.

								if (appropriated.equals("Y")) {
									demandAdvice.setPaymentId(paymentId);
								} else {
									demandAdvice.setPaymentId("");
								}

								// updateShortAmountDetails(demandAdvice,differentialAmount,
								// connection);
								voucherGen = true;

							} else {
								Log.log(Log.DEBUG, "RpProcessor",
										"reapproveLoanAmount",
										"Beyond Short limits. Generate demand advices ");
								// Generate new dan for the differential amount.
								// application.setApprovedAmount(Math.abs(differentialAmount));

								// applicationTemp.setApprovedAmount(Math.abs(differentialAmount));
								// newAmount =
								// rpProcessor.calculateGuaranteeFee(applicationTemp)
								// ;

								// payment is allocated.
								// generate a new dan.
								/*
								 * String danNo=demandAdvice.getDanNo();
								 * 
								 * 
								 * int index=danNo.indexOf("-");
								 * Log.log(Log.DEBUG
								 * ,"RpProcessor","reapproveLoanAmount"
								 * ,"index "+index);
								 * 
								 * String newDanId=null; if(index==-1) {
								 * Log.log(
								 * Log.DEBUG,"RpProcessor","reapproveLoanAmount"
								 * ," New Dan "); newDanId=danNo+"-1"; } else {
								 * String
								 * suffix=danNo.substring(index+1,danNo.length
								 * ());
								 * 
								 * Log.log(Log.DEBUG,"RpProcessor",
								 * "reapproveLoanAmount"," suffix "+suffix);
								 * 
								 * int suffixValue=Integer.parseInt(suffix);
								 * 
								 * Log.log(Log.DEBUG,"RpProcessor",
								 * "reapproveLoanAmount"
								 * ," suffixValue "+suffixValue);
								 * 
								 * suffixValue++;
								 * 
								 * String
								 * toString=Integer.toString(suffixValue);
								 * 
								 * Log.log(Log.DEBUG,"RpProcessor",
								 * "reapproveLoanAmount"," toString "+toString);
								 * 
								 * toString+="00";
								 * 
								 * toString=toString.substring(toString.length()-
								 * 3,toString.length());
								 * 
								 * Log.log(Log.DEBUG,"RpProcessor",
								 * "reapproveLoanAmount"
								 * ," toString After "+toString);
								 * 
								 * newDanId="-"+toString;
								 * 
								 * Log.log(Log.DEBUG,"RpProcessor",
								 * "reapproveLoanAmount"," newDanId "+newDanId);
								 * }
								 */
								// insert the new dan details.

								if (allocated.equalsIgnoreCase("Y")) {
									Log.log(Log.DEBUG, "RpProcessor",
											"reapproveLoanAmount", "Allocated ");

									// if(appropriated.equalsIgnoreCase("Y"))
									// {
									Log.log(Log.DEBUG, "RpProcessor",
											"reapproveLoanAmount",
											"Appropriated ");
									// method to change the realisation date and
									// approved date whichever is later..dd

									DemandAdvice newDemandAdvice = new DemandAdvice();
									newDemandAdvice.setBankId(demandAdvice
											.getBankId());
									newDemandAdvice.setZoneId(demandAdvice
											.getZoneId());
									newDemandAdvice.setBranchId(demandAdvice
											.getBranchId());

									newDemandAdvice.setDanNo(demandAdvice
											.getDanNo());
									newDemandAdvice.setCgpan(demandAdvice
											.getCgpan());
									newDemandAdvice.setDanType(demandAdvice
											.getDanType());
									newDemandAdvice
											.setDanGeneratedDate(new java.util.Date());
									newDemandAdvice.setAmountRaised(Math
											.round(differentialAmount));
									newDemandAdvice.setUserId(user.getUserId());

									newDanId = rpHelper.generateCGDANId(
											demandAdvice.getBankId(),
											demandAdvice.getZoneId(),
											demandAdvice.getBranchId(),
											connection);
									Date dueDate = rpProcessor.getPANDueDate(
											newDemandAdvice, null);
									newDemandAdvice.setDanNo(newDanId);
									newDemandAdvice.setDanDueDate(dueDate);

									insertDANDetails(newDemandAdvice,
											connection);
									insertPANDetailsForDAN(newDemandAdvice,
											connection);

									Administrator administrator = new Administrator();
									try {
										mliId = demandAdvice.getBankId()
												+ demandAdvice.getZoneId()
												+ demandAdvice.getBranchId();
										Log.log(Log.WARNING, className,
												"reapproveLoanAmount",
												"getting users for " + mliId);
										users = administrator
												.getAllUsers(mliId);
										Log.log(Log.WARNING, className,
												"reapproveLoanAmount",
												"users got");
									} catch (NoUserFoundException exception) {
										Log.log(Log.WARNING,
												className,
												"reapproveLoanAmount",
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									} catch (DatabaseException exception) {
										Log.log(Log.WARNING,
												className,
												"reapproveLoanAmount",
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									}

									Mailer mailer = new Mailer();
									RegistrationDAO registrationDAO = new RegistrationDAO();

									mliInfo = registrationDAO.getMemberDetails(
											demandAdvice.getBankId(),
											demandAdvice.getZoneId(),
											demandAdvice.getBranchId());
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount", "users got");

									mailPrivelege = mliInfo.getMail();
									emailPrivelege = mliInfo.getEmail();
									hardCopyPrivelege = mliInfo.getHardCopy();

									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount",
											"Getting Email Id for MLI id completed");
									int userSize = users.size();
									emailIds = new ArrayList();
									for (int j = 0; j < userSize; j++) {
										mailUser = (User) users.get(j);
										emailIds.add(mailUser.getUserId());
										// emailIds.add(mailUser.getEmailId()) ;
										// Log.log(Log.DEBUG,
										// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
										// ;
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Member Id" + mliId
														+ ", User email "
														+ mailUser.getUserId());
									}

									if (emailIds != null) {
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Before instantiating message");
										subject = "New Demand Advice("
												+ newDanId + ") generated";
										emailMessage = "CGPAN - "
												+ newDemandAdvice.getCgpan()
												+ ", GuaranteeAmount - "
												+ newDemandAdvice
														.getAmountRaised()
												+ "\n";
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Subject = " + subject);
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Email Message = "
														+ emailMessage);
										Message message = new Message(emailIds,
												null, null, subject,
												emailMessage);
										message.setFrom(fromEmail);
										// try
										// {
										// mailer.sendEmail(message);
										// Commented by path on 5jan2007 due to
										// performence issue
										// administrator.sendMail(message);
										// ///////////////////////////////////////////////////////

										/*
										 * }catch(MailerException
										 * mailerException) {
										 * Log.log(Log.WARNING,
										 * className,methodName
										 * ,"Exception sending Mail. Error="
										 * +mailerException.getMessage()) ; }
										 */// administrator(message) ;
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"After instantiating message");
										// mailStatus =
										// mailer.sendEmail(message) ;
									}

									voucherGen = false;

									// updateRealizationDate(newDemandAdvice,
									// newDemandAdvice.getAppropriatedDate(),
									// connection);

									// }

								} else {
									// cancel the generated amount and update
									// with the new amount.
									Log.log(Log.DEBUG, "RpProcessor",
											"reapproveLoanAmount",
											"Not allocated.");

									DemandAdvice newDemandAdvice = new DemandAdvice();
									newDemandAdvice.setBankId(demandAdvice
											.getBankId());
									newDemandAdvice.setZoneId(demandAdvice
											.getZoneId());
									newDemandAdvice.setBranchId(demandAdvice
											.getBranchId());

									newDemandAdvice.setDanNo(demandAdvice
											.getDanNo());
									newDemandAdvice.setCgpan(demandAdvice
											.getCgpan());
									newDemandAdvice.setDanType(demandAdvice
											.getDanType());
									newDemandAdvice
											.setDanGeneratedDate(new java.util.Date());
									newDemandAdvice.setAmountRaised(Math
											.round(newAmount[4]));
									newDemandAdvice.setUserId(user.getUserId());

									newDanId = rpHelper.generateCGDANId(
											demandAdvice.getBankId(),
											demandAdvice.getZoneId(),
											demandAdvice.getBranchId(),
											connection);
									Date dueDate = rpProcessor.getPANDueDate(
											newDemandAdvice, null);
									newDemandAdvice.setDanNo(newDanId);
									newDemandAdvice.setDanDueDate(dueDate);

									insertDANDetails(newDemandAdvice,
											connection);
									insertPANDetailsForDAN(newDemandAdvice,
											connection);

									Administrator administrator = new Administrator();
									try {
										mliId = demandAdvice.getBankId()
												+ demandAdvice.getZoneId()
												+ demandAdvice.getBranchId();
										Log.log(Log.WARNING, className,
												"reapproveLoanAmount",
												"getting users for " + mliId);
										users = administrator
												.getAllUsers(mliId);
										Log.log(Log.WARNING, className,
												"reapproveLoanAmount",
												"users got");
									} catch (NoUserFoundException exception) {
										Log.log(Log.WARNING,
												className,
												"reapproveLoanAmount",
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									} catch (DatabaseException exception) {
										Log.log(Log.WARNING,
												className,
												"reapproveLoanAmount",
												"Exception getting user details for the MLI. Error="
														+ exception
																.getMessage());
									}

									Mailer mailer = new Mailer();
									RegistrationDAO registrationDAO = new RegistrationDAO();

									mliInfo = registrationDAO.getMemberDetails(
											demandAdvice.getBankId(),
											demandAdvice.getZoneId(),
											demandAdvice.getBranchId());
									Log.log(Log.WARNING, className,
											"reapproveLoanAmount", "users got");

									mailPrivelege = mliInfo.getMail();
									emailPrivelege = mliInfo.getEmail();
									hardCopyPrivelege = mliInfo.getHardCopy();

									Log.log(Log.DEBUG, className,
											"reapproveLoanAmount",
											"Getting Email Id for MLI id completed");
									int userSize = users.size();
									emailIds = new ArrayList();
									for (int j = 0; j < userSize; j++) {
										mailUser = (User) users.get(j);
										emailIds.add(mailUser.getUserId());
										// emailIds.add(mailUser.getEmailId()) ;
										// Log.log(Log.DEBUG,
										// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
										// ;
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Member Id" + mliId
														+ ", User email "
														+ mailUser.getUserId());
									}

									if (emailIds != null) {
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Before instantiating message");
										subject = "New Demand Advice("
												+ newDanId + ") generated";
										emailMessage = "CGPAN - "
												+ newDemandAdvice.getCgpan()
												+ ", GuaranteeAmount - "
												+ newDemandAdvice
														.getAmountRaised()
												+ "\n";
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Subject = " + subject);
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"Email Message = "
														+ emailMessage);
										Message message = new Message(emailIds,
												null, null, subject,
												emailMessage);
										message.setFrom(fromEmail);
										// try
										// {
										// mailer.sendEmail(message);
										// Commented by path on 5jan2007 due to
										// performence issue
										// administrator.sendMail(message);
										// ///////////////////////////////////////////////////////

										/*
										 * }catch(MailerException
										 * mailerException) {
										 * Log.log(Log.WARNING,
										 * className,methodName
										 * ,"Exception sending Mail. Error="
										 * +mailerException.getMessage()) ; }
										 */// administrator(message) ;
										Log.log(Log.DEBUG, className,
												"reapproveLoanAmount",
												"After instantiating message");
										// mailStatus =
										// mailer.sendEmail(message) ;
									}

									voucherGen = false;

									double oldAmt = Math.abs(demandAdvice
											.getCancelledAmount()
											- demandAdvice.getAmountRaised());
									double cancelledAmount = demandAdvice
											.getAmountRaised();

									// double
									// amountRaised=oldAmt+demandAdvice.getAmountRaised()+differentialAmount;

									Log.log(Log.DEBUG, "RpProcessor",
											"reapproveLoanAmount",
											"cancelledAmount,amountRaised "
													+ cancelledAmount);

									newDemandAdvice.setBankId(demandAdvice
											.getBankId());
									newDemandAdvice.setZoneId(demandAdvice
											.getZoneId());
									newDemandAdvice.setBranchId(demandAdvice
											.getBranchId());

									newDemandAdvice.setDanNo(demandAdvice
											.getDanNo());
									// System.out.println("New DanId:"+demandAdvice.getDanNo());
									newDemandAdvice.setCgpan(demandAdvice
											.getCgpan());
									newDemandAdvice.setDanType(demandAdvice
											.getDanType());
									newDemandAdvice
											.setDanGeneratedDate(new java.util.Date());
									newDemandAdvice.setUserId(user.getUserId());

									newDemandAdvice
											.setCancelledAmount(cancelledAmount);
									dueDate = rpProcessor.getPANDueDate(
											newDemandAdvice, null);

									newDemandAdvice.setDanDueDate(dueDate);
									newDemandAdvice
											.setAmountRaised(cancelledAmount);
									newDemandAdvice.setNewDanNo(newDanId);

									// cancelAndUpdateDANDetails(newDemandAdvice,
									// connection);
									voucherGen = false;

								}
							}

						} else {

							voucherGen = false;
						}

						/*
						 * if(allocated.equals("N")) { voucherGen=false; }
						 */
					}

					if (voucherGen) {
						Log.log(Log.DEBUG, "RpProcessor",
								"reapproveLoanAmount",
								"b4 insert journal voucher");

						VoucherDetail voucherDetail = new VoucherDetail();
						vouchers.clear();
						/*
						 * voucherDetail.setBankGLCode("");
						 * voucherDetail.setBankGLName
						 * (paymentDetails.getCollectingBank());
						 * voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
						 * voucherDetail.setAmount(differentialAmount);
						 * voucherDetail
						 * .setVoucherType(RpConstants.JOURNAL_VOUCHER);
						 */
						danId = "";

						Voucher voucher = new Voucher();
						differentialAmount = 0.0D;;
						if (differentialAmount > 0) // excess
						{
							voucherDetail.setBankGLCode(accCodes
									.getProperty(RpConstants.GF_AC));
							voucherDetail.setBankGLName("");
							voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
							voucherDetail.setAmount(differentialAmount);
							voucherDetail
									.setVoucherType(RpConstants.JOURNAL_VOUCHER);

							/*
							 * voucher.setAcCode("GF A/c");
							 * voucher.setPaidTo("CGTSI");
							 * voucher.setDebitOrCredit("D");
							 * voucher.setInstrumentDate
							 * (dateFormat.format(paymentDetails
							 * .getInstrumentDate()));
							 * voucher.setInstrumentNo(paymentDetails
							 * .getInstrumentNo());
							 * voucher.setInstrumentType(paymentDetails
							 * .getInstrumentType());
							 * voucher.setAmountInRs(differentialAmount+"");
							 * vouchers.add(voucher); voucher=null;
							 */

							voucher = new Voucher();
							voucher.setAcCode(accCodes
									.getProperty(RpConstants.EXCESS_AC));
							voucher.setPaidTo("CGTSI");
							voucher.setDebitOrCredit("C");
							if (paymentDetails != null) {
								voucher.setInstrumentDate(dateFormat
										.format(paymentDetails
												.getInstrumentDate()));
								voucher.setInstrumentNo(paymentDetails
										.getInstrumentNo());
								voucher.setInstrumentType(paymentDetails
										.getInstrumentType());
							} else {
								voucher.setInstrumentDate(null);
								voucher.setInstrumentNo(null);
								voucher.setInstrumentType(null);
							}
							voucher.setAmountInRs("-" + differentialAmount);
							vouchers.add(voucher);
							voucher = null;
						} else // short
						{
							voucherDetail.setBankGLCode(accCodes
									.getProperty(RpConstants.GF_AC));
							voucherDetail.setBankGLName("");
							voucherDetail.setDeptCode(RpConstants.RP_CGTSI);
							voucherDetail.setAmount(0 - Math
									.abs(differentialAmount));
							voucherDetail
									.setVoucherType(RpConstants.JOURNAL_VOUCHER);

							/*
							 * voucher.setAcCode("GF A/c");
							 * voucher.setPaidTo("CGTSI");
							 * voucher.setDebitOrCredit("C");
							 * voucher.setInstrumentDate
							 * (dateFormat.format(paymentDetails
							 * .getInstrumentDate()));
							 * voucher.setInstrumentNo(paymentDetails
							 * .getInstrumentNo());
							 * voucher.setInstrumentType(paymentDetails
							 * .getInstrumentType());
							 * voucher.setAmountInRs("-"+Math
							 * .abs(differentialAmount)); vouchers.add(voucher);
							 * voucher=null;
							 */

							voucher = new Voucher();
							voucher.setAcCode(accCodes
									.getProperty(RpConstants.SHORT_AC));
							voucher.setPaidTo("CGTSI");
							voucher.setDebitOrCredit("D");
							if (paymentDetails != null) {
								voucher.setInstrumentDate(dateFormat
										.format(paymentDetails
												.getInstrumentDate()));
								voucher.setInstrumentNo(paymentDetails
										.getInstrumentNo());
								voucher.setInstrumentType(paymentDetails
										.getInstrumentType());
							} else {
								voucher.setInstrumentDate(null);
								voucher.setInstrumentNo(null);
								voucher.setInstrumentType(null);
							}
							voucher.setAmountInRs(Math.abs(differentialAmount)
									+ "");
							vouchers.add(voucher);
							voucher = null;
						}
						if (paymentId == null) {
							narration = narration + "Payment Id: "
									+ "not available";
						} else {

							narration = narration + "Payment Id: " + paymentId;
						}

						narration = narration + " Member Id: "
								+ demandAdvice.getBankId()
								+ demandAdvice.getZoneId()
								+ demandAdvice.getBranchId();
						narration = narration + " Dan No: "
								+ demandAdvice.getDanNo();
						narration = narration + " CGPAN: "
								+ demandAdvice.getCgpan();
						if (excessId != 0) {
							narration = narration + " Excess Id: " + excessId;
						}

						voucherDetail.setNarration(narration);
						voucherDetail.setVouchers(vouchers);

						// insertVoucherDetails(voucherDetail, user.getUserId(),
						// connection);
						vouchers.clear();

						Log.log(Log.DEBUG, "RpProcessor",
								"reapproveLoanAmount",
								"inserted journal voucher");
					}

					// rpDAO.updateCGDANDetails(demandAdvice.getCgpan(), user) ;
					// generate new dan id for the old dan id.

				}
				/*
				 * if(danType.equalsIgnoreCase(RpConstants.DAN_TYPE_SFDAN)) {
				 * allocated = demandAdvice.getAllocated() ; serviceFee =
				 * calculateServiceFee(application) ; serviceFeeAmount =
				 * serviceFee.getServiceAmount() ;
				 * 
				 * // param newdan,olddan,guaranteefee changed..dd
				 * rpDAO.insertServiceFee(serviceFee) ;
				 * 
				 * if (allocated.equalsIgnoreCase("Y")) { oldAmount =
				 * demandAdvice.getAmountRaised() ; newAmount =
				 * serviceFee.getServiceAmount() ; shortExcessAmounts.add(new
				 * Double(oldAmount - newAmount)) ;
				 * 
				 * if(appropriated.equalsIgnoreCase("Y")) { //method to change
				 * the realisation date and //approved date whichever is
				 * later..dd } } }
				 */
			}
			/*
			 * GuaranteeFee gFee = new GuaranteeFee();
			 * gFee.setCgpan(application.getCgpan());
			 * gFee.setGuaranteeAmount(newAmount); updateGuaranteeFee(gFee,
			 * user.getUserId(), connection);
			 */
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		} finally {
			DBConnection.freeConnection(connection);
		}
		// reset the approved amount.

		// application.setReapprovedAmount(originalApprovedAmount);

		Log.log(Log.INFO, "RpProcessor", "reapproveLoanAmount", "Exited");

		return shortExcessAmounts;
	}

	/**
	 * This method picks up all short information from SHORT_INFO table and
	 * generates short DAN for the short details.
	 * 
	 * @author GU14477
	 * 
	 *         To change the template for this generated type comment go to
	 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
	 *         Comments
	 */
	public void generateSHDAN(User user, String memberId)
			throws DatabaseException {

		RpProcessor rpProcessor = new RpProcessor();
		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		ParameterMaster parameterMaster = administrator.getParameter();

		String methodName = "generateSHDAN";
		int noOfCGPANs = 0; // Temporary variable used inside the DAN generation
							// loop to compare with the max. number of CGPANs
							// allowed.
		int size = 0; // Used to identify no. of Applications approved for the
						// day
		int noOfCGPANsLimit = 0; // Max. no. of CGPANS that a DAN can hold.
									// Picked up from parameter master
		int userSize = 0;

		String mliId = "";
		String bankId = "";
		String zoneId = "";
		String branchId = "";

		/**
		 * MLI details captured inside application loop for each application. If
		 * this differs from mliId mentioned above, new DAN will be generated
		 **/
		String tempMLIId = "";
		String tempBankId = "";
		String tempZoneId = "";
		String tempBranchId = "";
		/** End of temporary MLI variables **/

		/** Following variables are used for mailing purposes **/
		String subject = "";
		String emailMessage = "";
		boolean mailStatus = false;
		ArrayList users = null;
		ArrayList emailIds = null;
		MLIInfo mliInfo = null;
		String mailPrivelege = "";
		String emailPrivelege = "";
		String hardCopyPrivelege = "";
		String userId = user.getUserId();
		String fromEmail = user.getUserId();
		User mailUser = null;
		/** End of variables used for mailing **/

		String shdanNo = ""; // Always stores the recently generated Dan number.
								// Should be updated whenever new DAN is
								// generated

		Date danGeneratedDate = new Date(); // Date of generation of DAN
		Date dueDate = null; // Due date for the DAN

		ArrayList shortAmountDetails = null; // Arraylist used to get the
												// approved application details
												// that are pending for
												// generation of DAN.
		ShortAmountInfo shortAmountInfo = null;
		DemandAdvice demandAdvice = null; // DemandAdvice object retrieved from
											// the guaranteeValues arraylist.
											// This variable will be used inside
											// the application loop

		Connection connection = DBConnection.getConnection(false);

		try {
			// returns an arraylist of approved applications for the day
			// memberwise.
			Log.log(Log.INFO, className, methodName,
					"Before getting Approved Applications Member Wise");
			shortAmountDetails = getShortAmountMemberWise(memberId);
			Log.log(Log.INFO, className, methodName,
					"After Getting Approved Applications");

			if (shortAmountDetails == null) {
				Log.log(Log.INFO, className, methodName,
						"No applications fetched");
				return;
			}

			size = shortAmountDetails.size();
			if (size == 0) {
				Log.log(Log.INFO, className, methodName,
						"No. of applications fetched = 0");
				return;
			}

			/**
			 * Following lines of code does the following sequence. 1. Picks up
			 * the first DemandAdvice object from the arraylist. 2. Initialises
			 * mliId with the first MLI retrieved 3. As the DemandAdvice object
			 * also has CGPAN information, new DAN is generated and the PAN
			 * details are inserted in the database
			 * **/
			shortAmountInfo = (ShortAmountInfo) shortAmountDetails.get(0);
			demandAdvice = new DemandAdvice(shortAmountInfo);

			bankId = demandAdvice.getBankId();
			zoneId = demandAdvice.getZoneId();
			branchId = demandAdvice.getBranchId();

			shdanNo = rpHelper.generateSHDANId(demandAdvice, connection);
			Log.log(Log.INFO, className, methodName,
					"Generating SFDAN for first application, SFDAN = "
							+ shdanNo);
			demandAdvice.setDanNo(shdanNo);

			Log.log(Log.INFO, className, methodName,
					"After Setting DAN Type for the first SFDAN, Dan Type = "
							+ demandAdvice.getDanType());
			demandAdvice.setDanGeneratedDate(danGeneratedDate);

			dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
			demandAdvice.setDanDueDate(dueDate);
			// Set expiry date for DAN
			demandAdvice.setUserId(userId);

			Log.log(Log.INFO, className, methodName, "After Generating SFDAN");
			insertDANDetails(demandAdvice, connection);
			mliId = bankId.concat(zoneId).concat(branchId);
			/** End of generating DAN for the first application retrieved. **/

			noOfCGPANsLimit = parameterMaster.getNoOfCGPANsLimit(); // if
																	// exceeds
																	// 20
			Log.log(Log.INFO, className, methodName,
					"Starting Application Loop");

			/** Starting application loop **/
			for (int i = 0; i < size; ++i) {
				shortAmountInfo = (ShortAmountInfo) shortAmountDetails.get(i);
				demandAdvice = new DemandAdvice(shortAmountInfo);
				demandAdvice.setDanNo(shdanNo);
				demandAdvice.setDanGeneratedDate(danGeneratedDate);
				dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
				demandAdvice.setDanDueDate(dueDate);
				// Set expiry date for DAN
				demandAdvice.setUserId(userId);

				Log.log(Log.INFO, className, methodName,
						"Inside Loop: SHDAN = " + shdanNo + ", CGPAN = "
								+ demandAdvice.getCgpan());
				tempBankId = demandAdvice.getBankId();
				tempZoneId = demandAdvice.getZoneId();
				tempBranchId = demandAdvice.getBranchId();

				tempMLIId = tempBankId.concat(tempZoneId).concat(tempBranchId);
				Log.log(Log.INFO, className, methodName, "Temp MLI Id = "
						+ tempMLIId);

				boolean sendMailValue = true;

				/**
				 * Checks if the new MLI Id is the same as the previous MLI id
				 * or not
				 **/
				if (mliId.equalsIgnoreCase(tempMLIId)) { // If the newly
															// obtained mliId is
															// equal to the
															// previous MliId
															// value
					if (noOfCGPANs >= noOfCGPANsLimit) { // If no. of CGPANs for
															// the same MLI id
															// exceeds the
															// limit, new DAN is
															// generated for the
															// remaining CGPANs
															// of the same MLI.

						try {
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						mailer = new Mailer();

						mliInfo = registrationDAO.getMemberDetails(bankId,
								zoneId, branchId);

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.DEBUG, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						ArrayList mailIds = new ArrayList();

						for (int j = 0; j < userSize; j++) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId()); // mail Ids
							mailIds.add(mailUser.getEmailId()); // e-mail Ids
							// emailIds.add(mailUser.getEmailId()) ;
							// Log.log(Log.DEBUG,
							// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
							// ;
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User mail "
											+ mailUser.getUserId());
							Log.log(Log.DEBUG, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getEmailId());
						}

						// sending mail
						if (emailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + shdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// try
							// {
							// mailer.sendEmail(message);
							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////

							/*
							 * }catch(MailerException mailerException) {
							 * Log.log(Log.WARNING,
							 * className,methodName,"Exception sending Mail. Error="
							 * +mailerException.getMessage()) ; }
							 */// administrator(message) ;
							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}

						// sending e-mail

						if (mailIds != null) {
							Log.log(Log.DEBUG, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + shdanNo
									+ ") generated";
							Log.log(Log.DEBUG, className, methodName,
									"Subject = " + subject);
							Log.log(Log.DEBUG, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(mailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// try
							// {
							// Commented by path on 5jan2007 due to performence
							// issue
							// mailer.sendEmail(message);
							// ///////////////////////////////////////////////////////

							// administrator.sendMail(message);
							// }catch(MailerException mailerException)
							// {
							// Log.log(Log.WARNING,
							// className,methodName,"Exception sending Mail. Error="+mailerException.getMessage())
							// ;
							// }

							// administrator(message) ;
							Log.log(Log.DEBUG, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}

						emailMessage = ""; // resetting the message.
						noOfCGPANs = 0;

						shdanNo = rpHelper.generateSHDANId(bankId, zoneId,
								branchId, connection);
						demandAdvice.setDanNo(shdanNo);

						insertDANDetails(demandAdvice, connection);

						emailMessage += "DAN No : " + demandAdvice.getDanNo()
								+ "\n";
						noOfCGPANs = 0;

						sendMailValue = false;
					} else {

						sendMailValue = true;
					}

					Log.log(Log.INFO, className, methodName,
							"Before Inserting PAN details for SFDAN"
									+ demandAdvice.getDanNo());
					insertPANDetailsForDAN(demandAdvice, connection);

					Log.log(Log.INFO, className, methodName,
							"After instantiating message");
					Log.log(Log.INFO, className, methodName,
							"After Insert from " + methodName);
					++noOfCGPANs;
					emailMessage += "CGPAN - " + demandAdvice.getCgpan()
							+ ", amount raised"
							+ demandAdvice.getAmountRaised() + "\n";
				} else { // This means MLI id is changed. Mail will be sent to
							// the previous MLI with the generated DAN details.
							// Then, a new DAN is generated for the new MLI id.
					Log.log(Log.INFO, className, methodName,
							"Entering else part since MLI id changed");
					// mliIds.add(mliId) ;

					// administrator.getAllUsers(mliId) ;
					Log.log(Log.INFO, className, methodName,
							"Addition of MliId to arraylist completed");
					if (emailMessage != null && !emailMessage.equals("")) {
						try {
							// users = rpDAO.getActiveBankUsers(mliId) ;
							users = administrator.getAllUsers(mliId);
						} catch (NoUserFoundException exception) {
							Log.log(Log.WARNING, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						} catch (DatabaseException exception) {
							Log.log(Log.INFO, className, methodName,
									"Exception getting user details for the MLI. Error="
											+ exception.getMessage());
						}

						Log.log(Log.INFO, className, methodName,
								"Before getting member details, bankId = "
										+ bankId + ", zoneId = " + zoneId
										+ ", branchId = " + branchId);
						mliInfo = registrationDAO.getMemberDetails(bankId,
								zoneId, branchId);
						Log.log(Log.INFO, className, methodName,
								"After getting member details");

						mailPrivelege = mliInfo.getMail();
						emailPrivelege = mliInfo.getEmail();
						hardCopyPrivelege = mliInfo.getHardCopy();

						Log.log(Log.INFO, className, methodName,
								"Getting Email Id for MLI id completed");
						userSize = users.size();
						emailIds = new ArrayList();
						for (int j = 0; j < userSize; ++j) {
							mailUser = (User) users.get(j);
							emailIds.add(mailUser.getUserId());
							Log.log(Log.INFO, className, methodName,
									"Member Id" + mliId + ", User email "
											+ mailUser.getEmailId());
						}
						if (emailIds != null) {
							Log.log(Log.INFO, className, methodName,
									"Before instantiating message");
							subject = "New Demand Advice(" + shdanNo
									+ ") generated";
							Log.log(Log.INFO, className, methodName,
									"Subject = " + subject);
							Log.log(Log.INFO, className, methodName,
									"Email Message = " + emailMessage);
							Message message = new Message(emailIds, null, null,
									subject, emailMessage);
							message.setFrom(fromEmail);
							// Commented by path on 5jan2007 due to performence
							// issue
							// administrator.sendMail(message);
							// ///////////////////////////////////////////////////////

							Log.log(Log.INFO, className, methodName,
									"After instantiating message");
							// mailStatus = mailer.sendEmail(message) ;
						}

						emailMessage = ""; // resetting the message.
						noOfCGPANs = 0;

						/** New DAN generation begins here **/
						Log.log(Log.INFO, className, methodName,
								"Before generating New SFDAN: ");
						shdanNo = rpHelper.generateSHDANId(tempBankId,
								tempZoneId, tempBranchId, connection);
						Log.log(Log.INFO, className, methodName, "New SHDAN: "
								+ shdanNo);

						demandAdvice.setDanNo(shdanNo);

						insertDANDetails(demandAdvice, connection);

						Log.log(Log.INFO, className, methodName,
								"Before Inserting PAN details for SFDAN"
										+ demandAdvice.getDanNo());
						insertPANDetailsForDAN(demandAdvice, connection);

						emailMessage += "DAN No : " + demandAdvice.getDanNo()
								+ "\n";
						++noOfCGPANs;
						/** New DAN generation ends here **/

					}
					/**
					 * mliId is updated with the new Mli details. Further
					 * applications will be checked with this MLI id
					 **/
					mliId = tempBankId.concat(tempZoneId).concat(tempBranchId);

					sendMailValue = false;
				}

				if (sendMailValue) {
					try {
						users = administrator.getAllUsers(mliId);
					} catch (NoUserFoundException exception) {
						Log.log(Log.WARNING, className, methodName,
								"Exception getting user details for the MLI. Error="
										+ exception.getMessage());
					} catch (DatabaseException exception) {
						Log.log(Log.WARNING, className, methodName,
								"Exception getting user details for the MLI. Error="
										+ exception.getMessage());
					}

					mailer = new Mailer();

					mliInfo = registrationDAO.getMemberDetails(tempBankId,
							tempZoneId, tempBranchId);

					mailPrivelege = mliInfo.getMail();
					emailPrivelege = mliInfo.getEmail();
					hardCopyPrivelege = mliInfo.getHardCopy();

					Log.log(Log.DEBUG, className, methodName,
							"Getting Email Id for MLI id completed");
					userSize = users.size();
					emailIds = new ArrayList();
					for (int j = 0; j < userSize; j++) {
						mailUser = (User) users.get(j);
						emailIds.add(mailUser.getUserId());
						// emailIds.add(mailUser.getEmailId()) ;
						// Log.log(Log.DEBUG,
						// className,methodName,"Member Id"+mliId+", User email "+mailUser.getEmailId())
						// ;
						Log.log(Log.DEBUG, className, methodName,
								"Member Id" + mliId + ", User email "
										+ mailUser.getUserId());
					}

					if (emailIds != null) {
						Log.log(Log.DEBUG, className, methodName,
								"Before instantiating message");
						subject = "New Demand Advice(" + shdanNo
								+ ") generated";
						Log.log(Log.DEBUG, className, methodName, "Subject = "
								+ subject);
						Log.log(Log.DEBUG, className, methodName,
								"Email Message = " + emailMessage);
						Message message = new Message(emailIds, null, null,
								subject, emailMessage);
						message.setFrom(fromEmail);
						// try
						// {
						// mailer.sendEmail(message);
						// Commented by path on 5jan2007 due to performence
						// issue
						// administrator.sendMail(message);
						// ///////////////////////////////////////////////////////

						/*
						 * }catch(MailerException mailerException) {
						 * Log.log(Log.WARNING,
						 * className,methodName,"Exception sending Mail. Error="
						 * +mailerException.getMessage()) ; }
						 */// administrator(message) ;
						Log.log(Log.DEBUG, className, methodName,
								"After instantiating message");
						// mailStatus = mailer.sendEmail(message) ;
					}

					emailMessage = ""; // resetting the message.

				}

			}
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		} finally {
			DBConnection.freeConnection(connection);
		}
		/** End of Application Loop **/

	}

	/**
	 * This method is to calculate the difference in the approved Amount and the
	 * charges incurred And hence depending on the amount CLDAN is generated
	 * 
	 * @author SS14485
	 */
	public void generateCLDAN(User user, String bankId, String zoneId,
			String branchId) throws DatabaseException {

		RpProcessor rpProcessor = new RpProcessor();
		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Mailer mailer = new Mailer();
		Administrator administrator = new Administrator();
		String methodName = "generateCLDAN";
		String cldanNo = "";
		Log.log(Log.INFO, className, methodName, "Entered");
		Connection connection = DBConnection.getConnection(false);
		try {
			ArrayList cldanDetails = getDetailsForCLDANGen(user, bankId,
					zoneId, branchId);
			Log.log(Log.DEBUG, className, methodName, "CLDAN Details fetched :"
					+ cldanDetails.size());
			ArrayList settlementDetails = (ArrayList) cldanDetails.get(0);
			if (settlementDetails == null || settlementDetails.size() == 0) {
				Log.log(Log.DEBUG, className, methodName, "No BIDs fetched");
				throw new DatabaseException(
						"No BIDs Available For CLDAN Generation");
				// Commented by ritesh path on 5dec to show the proper error
				// message to users
				// return;
			}
			ArrayList cgpanCountList = (ArrayList) cldanDetails.get(1);
			int size = settlementDetails.size();
			double defaultAmnt = 0;
			double cgtsiLiability = 0.0;
			double firstInstllmntAmnt = 0.0;
			DemandAdvice demandAdvice = new DemandAdvice();
			for (int i = 0; i < size; i++) {
				SettlementDetail settlementDetail = (SettlementDetail) settlementDetails
						.get(i);
				double appApprovedAmount = settlementDetail
						.getApprovedClaimAmt();
				Log.log(Log.DEBUG, className, methodName,
						"outstanding amount : " + appApprovedAmount);
				double outstandingAmtAsOnNpa = settlementDetail
						.getOsAmtAsonNPA();
				Log.log(Log.DEBUG, className, methodName,
						"outstanding amount as on NPA: "
								+ outstandingAmtAsOnNpa);
				double recoveryAmount = settlementDetail.getRecoveryAmt();
				Log.log(Log.DEBUG, className, methodName, "recovery Amount: "
						+ recoveryAmount);
				String mliId = settlementDetail.getMliId();
				Log.log(Log.DEBUG, className, methodName, "MLI ID: " + mliId);
				if (outstandingAmtAsOnNpa < appApprovedAmount) {
					defaultAmnt = outstandingAmtAsOnNpa;
				} else if (outstandingAmtAsOnNpa > appApprovedAmount) {
					defaultAmnt = appApprovedAmount;
				} else if (outstandingAmtAsOnNpa == appApprovedAmount) {
					defaultAmnt = outstandingAmtAsOnNpa;
				}
				Log.log(Log.DEBUG, className, methodName, "default Amount: "
						+ defaultAmnt);
				/*
				 * Calculating CGTSI Liability CGTSI Liability is 75% of the
				 * Default Amount. CGTSI will pay the amount in two
				 * installment(s) First Installment will be 75% of the Default
				 * Amount
				 */
				cgtsiLiability = 0.75 * defaultAmnt;
				Log.log(Log.DEBUG, className, methodName, "cgtsi liability: "
						+ cgtsiLiability);
				firstInstllmntAmnt = 0.75 * cgtsiLiability; // already paid
															// amount
				Log.log(Log.DEBUG, className, methodName, "first Installment: "
						+ firstInstllmntAmnt);
				/**
				 * Net Loss to the MLI
				 */
				double netLossAmount = 0;
				if (defaultAmnt < recoveryAmount) {
					throw new DatabaseException(
							"Error in CLDAN Generation: DefaultAmnt < RecoveryAmount");
					// Commented by ritesh path on 5dec to show the proper error
					// message to users
					// return;
				}
				netLossAmount = defaultAmnt - recoveryAmount;
				Log.log(Log.DEBUG, className, methodName, "net loss Amount: "
						+ netLossAmount);
				/**
				 * CGTSI's Actual Liability
				 */
				double cgtsiActualLiability = 0.75 * netLossAmount;
				Log.log(Log.DEBUG, className, methodName,
						"cgtsi actual liability: " + cgtsiActualLiability);
				if (cgtsiActualLiability > firstInstllmntAmnt) {
					throw new DatabaseException(
							"Error in CLDAN Generation: CGTSI Actual Liability > First Installment Amnt");
					// Commented by ritesh path on 5dec to show the proper error
					// message to users
					// return;
				} else if (cgtsiActualLiability < firstInstllmntAmnt) {
					String danType = RpConstants.DAN_TYPE_CLDAN;

					if (settlementDetail.getMliId() != null) {
						bankId = (settlementDetail.getMliId()).substring(0, 4);
					}
					cldanNo = getDANId(danType, bankId, connection);
					Log.log(Log.DEBUG, className, methodName, "cldan: "
							+ cldanNo);
					int countCgpan = ((Integer) cgpanCountList.get(i))
							.intValue();
					double remAmount = firstInstllmntAmnt
							- cgtsiActualLiability;
					ArrayList cgpanList = getCgansForBID(settlementDetail
							.getCgbid());
					int cgpanSize = cgpanList.size();
					demandAdvice.setDanNo(cldanNo);
					demandAdvice.setDanType(danType);
					demandAdvice.setDanGeneratedDate(new java.util.Date());
					if (settlementDetail.getMliId() == null) {
						demandAdvice.setBankId(bankId);
						demandAdvice.setZoneId(zoneId);
						demandAdvice.setBranchId(branchId);

					} else {
						demandAdvice.setBankId((settlementDetail.getMliId())
								.substring(0, 4));
						demandAdvice.setZoneId((settlementDetail.getMliId())
								.substring(4, 8));
						demandAdvice.setBranchId((settlementDetail.getMliId())
								.substring(8, 12));
					}
					// java.util.Date
					// cldanDueDate=getPANDueDate(demandAdvice,null);
					Calendar calendar = Calendar.getInstance();
					java.util.Date generatedDate = demandAdvice
							.getDanGeneratedDate();
					calendar.setTime(generatedDate);
					calendar.add(Calendar.DATE, 30);

					java.util.Date cldanDueDate = calendar.getTime();
					demandAdvice.setDanDueDate(cldanDueDate);
					demandAdvice.setUserId(user.getUserId());
					insertDANDetails(demandAdvice, connection);
					for (int j = 0; j < cgpanSize; j++) {
						Log.log(Log.DEBUG, className, methodName, "cldan: "
								+ cldanNo + "for CGPAN " + cgpanList.get(j));
						demandAdvice = new DemandAdvice();
						String cgpan = (String) cgpanList.get(j);
						demandAdvice.setCgpan(cgpan);
						demandAdvice.setDanNo(cldanNo);
						demandAdvice.setDanType(danType);
						// demandAdvice.setBankId(bankId);
						// demandAdvice.setZoneId(zoneId);
						// demandAdvice.setBranchId(branchId);
						// demandAdvice.setUserId(user.getUserId());
						if (j == 0) {
							demandAdvice.setAmountRaised(Math.round(remAmount));
						} else {
							demandAdvice.setAmountRaised(0);
						}
						demandAdvice.setPenalty(0);
						demandAdvice.setDanGeneratedDate(new java.util.Date());
						cldanDueDate = rpProcessor.getPANDueDate(demandAdvice,
								null);
						demandAdvice.setDanDueDate(cldanDueDate);
						insertPANDetailsForDAN(demandAdvice, connection);
						// rpDAO.insertDANDetails(demandAdvice);
					}
					/*
					 * demandAdvice.setDanType(danType);
					 * demandAdvice.setDanGeneratedDate(new java.util.Date());
					 * demandAdvice.setBankId(bankId);
					 * demandAdvice.setZoneId(zoneId);
					 * demandAdvice.setBranchId(branchId); java.util.Date
					 * cldanDueDate=getPANDueDate(demandAdvice,null);
					 * demandAdvice.setDanDueDate(cldanDueDate);
					 * demandAdvice.setUserId(user.getUserId());
					 * rpDAO.insertDANDetails(demandAdvice);
					 */
				}
			}
			connection.commit();
		} catch (SQLException exp) {
			throw new DatabaseException(exp.getMessage());
		} catch (DatabaseException dbExp) {
			try {
				connection.rollback();
			} catch (SQLException exp) {
				throw new DatabaseException(exp.getMessage());
			}
			throw dbExp;
		} finally {
			DBConnection.freeConnection(connection);
		}
	}

	private PaymentDetails getPaymentDetails(String paymentId)
			throws DatabaseException {
		PaymentDetails paymentDetails = new PaymentDetails();
		Connection connection = null;
		CallableStatement getPayInSlipDetailsStmt = null;
		// ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.INFO, "RpDAO", "getPayInSlipDetails", "Entered");

			String exception = "";

			getPayInSlipDetailsStmt = connection
					.prepareCall("{?=call funcGetPaymentDetails(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			getPayInSlipDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getPayInSlipDetailsStmt.setString(2, paymentId);
			getPayInSlipDetailsStmt.registerOutParameter(3,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(4,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(5,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(6,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt
					.registerOutParameter(7, java.sql.Types.DATE);
			getPayInSlipDetailsStmt.registerOutParameter(8,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(9,
					java.sql.Types.DOUBLE);
			getPayInSlipDetailsStmt.registerOutParameter(10,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(11,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(12,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.registerOutParameter(13,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(14,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.registerOutParameter(15,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(16,
					java.sql.Types.DOUBLE);

			getPayInSlipDetailsStmt.execute();

			int errorCode = getPayInSlipDetailsStmt.getInt(1);
			
			System.out.println("  errorCode 18263 "+errorCode);
			String error = getPayInSlipDetailsStmt.getString(16);
			System.out.println("  errorCode 18265 "+error);
			Log.log(Log.DEBUG, "RpDAO", "getPayInSlipDetails",
					"errorCode,error are " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails", error);

				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			paymentDetails.setPaymentId(getPayInSlipDetailsStmt.getString(3));
			paymentDetails.setModeOfDelivery(getPayInSlipDetailsStmt
					.getString(4));
			paymentDetails.setModeOfPayment(getPayInSlipDetailsStmt
					.getString(5));
			paymentDetails
					.setInstrumentNo(getPayInSlipDetailsStmt.getString(6));
			paymentDetails
					.setInstrumentDate(getPayInSlipDetailsStmt.getDate(7));
			paymentDetails.setInstrumentType(getPayInSlipDetailsStmt
					.getString(8));

			paymentDetails.setInstrumentAmount(getPayInSlipDetailsStmt
					.getDouble(9));
			paymentDetails.setPayableAt(getPayInSlipDetailsStmt.getString(11));
			paymentDetails
					.setDrawnAtBank(getPayInSlipDetailsStmt.getString(12));
			paymentDetails.setDrawnAtBranch(getPayInSlipDetailsStmt
					.getString(13));
			paymentDetails.setCollectingBank(getPayInSlipDetailsStmt
					.getString(14));
			paymentDetails.setCollectingBankBranch(getPayInSlipDetailsStmt
					.getString(15));

			getPayInSlipDetailsStmt.close();
			getPayInSlipDetailsStmt = null;
			connection.commit();

		} catch (SQLException exception) {
			
         System.out.println("  exception :"+exception);
			Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				
				System.out.println("  ignore "+ignore);
			}

			throw new DatabaseException("Unable to get Payment details.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getPayInSlipDetails", "Exited");

		return paymentDetails;
	}

	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int getDifferenceDays(Date fromDate, Date toDate)
			throws DatabaseException {

		Connection connection = null;
		CallableStatement getPayInSlipDetailsStmt = null;
		// ResultSet resultSet = null;

		int paymentList;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		connection = DBConnection.getConnection(false);
		try {
			Log.log(Log.INFO, "RpDAO", "getDifferenceDays", "Entered");

			String exception = "";
			java.sql.Date sqlFromDate = null;
			if (fromDate != null && !fromDate.toString().equals("")) {
				String strFromDate = dateFormat.format(fromDate);
				// sqlFromDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(strFromDate));Modified
				// by pradeep for new server on 16.07.2012
				sqlFromDate = new java.sql.Date(fromDate.getTime());
			}
			java.sql.Date sqlToDate = null;
			if (toDate != null && !toDate.toString().equals("")) {
				String strToDate = dateFormat.format(toDate);
				// sqlToDate =
				// java.sql.Date.valueOf(DateHelper.stringToSQLdate(strToDate));Modified
				// by pradeep for new server on 16.07.2012
				sqlToDate = new java.sql.Date(toDate.getTime());
			}

			getPayInSlipDetailsStmt = connection
					.prepareCall("{?=call daysbetween(?,?,?,?)}");
			getPayInSlipDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			if (sqlFromDate != null) {
				getPayInSlipDetailsStmt.setDate(2, sqlFromDate);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " from date "
						+ sqlFromDate);
			}

			if (sqlToDate != null) {
				getPayInSlipDetailsStmt.setDate(3, sqlToDate);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " to date "
						+ sqlToDate);
			}
			getPayInSlipDetailsStmt.registerOutParameter(4,
					java.sql.Types.INTEGER);
			getPayInSlipDetailsStmt.registerOutParameter(5,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.execute();

			paymentList = getPayInSlipDetailsStmt.getInt(4);
			int errorCode = getPayInSlipDetailsStmt.getInt(1);
			String error = getPayInSlipDetailsStmt.getString(5);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			getPayInSlipDetailsStmt.close();
			getPayInSlipDetailsStmt = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Payment list.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		return paymentList;

	}

	public ArrayList getPaymentList(Date fromDate, Date toDate,
			String dateType, String memberId) throws DatabaseException,
			MessageException {
		Connection connection = null;
		CallableStatement getPayInSlipDetailsStmt = null;
		// ResultSet resultSet = null;

		ArrayList paymentList = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.INFO, "RpDAO", "getPaymentList", "Entered");

			String exception = "";
			java.sql.Date sqlFromDate = null;
			String bankId = memberId.substring(0, 4);
			String zoneId = memberId.substring(4, 8);
			String branchId = memberId.substring(8, 12);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " from date "
					+ fromDate);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " to date " + toDate);

			if (fromDate != null && !fromDate.toString().equals("")) {
				// String strFromDate=dateFormat.format(fromDate);
				// sqlFromDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(strFromDate));//Modified
				// by pradeep for new server on 16.07.2012
				sqlFromDate = new java.sql.Date(fromDate.getTime());
			}

			java.sql.Date sqlToDate = null;
			if (toDate != null && !toDate.toString().equals("")) {
				// String strToDate = dateFormat.format(toDate);
				// sqlToDate =
				// java.sql.Date.valueOf(DateHelper.stringToSQLdate(strToDate));Modified
				// by pradeep for new server on 16.07.2012
				sqlToDate = new java.sql.Date(toDate.getTime());
			}

			getPayInSlipDetailsStmt = connection
					.prepareCall("{?=call packGetAllPaymentIds.funcGetPaymentIds(?,?,?,?,?,?,?,?)}");
			getPayInSlipDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			if (sqlFromDate != null) {
				getPayInSlipDetailsStmt.setDate(2, sqlFromDate);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " from date "
						+ sqlFromDate);
			} else {
				getPayInSlipDetailsStmt.setNull(2, java.sql.Types.DATE);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " from date "
						+ sqlFromDate);
			}

			if (sqlToDate != null) {
				getPayInSlipDetailsStmt.setDate(3, sqlToDate);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " to date "
						+ sqlToDate);
			} else {
				getPayInSlipDetailsStmt.setNull(3, java.sql.Types.DATE);
				Log.log(Log.INFO, "RpDAO", "getPaymentList", " to date "
						+ sqlToDate);
			}
			getPayInSlipDetailsStmt.setString(4, dateType);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " date type "
					+ dateType);
			getPayInSlipDetailsStmt.setString(5, bankId);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " bank id " + bankId);
			getPayInSlipDetailsStmt.setString(6, zoneId);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " zone id " + zoneId);
			getPayInSlipDetailsStmt.setString(7, branchId);
			Log.log(Log.INFO, "RpDAO", "getPaymentList", " branch id "
					+ branchId);
			getPayInSlipDetailsStmt.registerOutParameter(8, Constants.CURSOR);
			getPayInSlipDetailsStmt.registerOutParameter(9,
					java.sql.Types.VARCHAR);

			getPayInSlipDetailsStmt.execute();

			int errorCode = getPayInSlipDetailsStmt.getInt(1);
			String error = getPayInSlipDetailsStmt.getString(9);

			Log.log(Log.DEBUG, "RpDAO", "getPaymentList",
					"errorCode,error are " + errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "getPaymentList", error);

				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			ResultSet paymentIds = (ResultSet) getPayInSlipDetailsStmt
					.getObject(8);
			while (paymentIds.next()) {
				String id = paymentIds.getString(1);
				Log.log(Log.ERROR, "RpDAO", "getPaymentList", "payment id - "
						+ id);
				paymentList.add(id);
			}
			paymentIds.close();
			paymentIds = null;
			getPayInSlipDetailsStmt.close();
			getPayInSlipDetailsStmt = null;

			if (paymentList.size() == 0) {
				throw new MessageException("No Payments made");
			}

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getPaymentList",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Payment list.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getPaymentList", "Exited");

		return paymentList;
	}

	public String getGLName(String glCode) throws DatabaseException,
			MessageException {
		Connection connection = null;
		CallableStatement getGLNameStmt = null;
		String glName = "";

		connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.INFO, "RpDAO", "getGLName", "Entered");

			String exception = "";

			getGLNameStmt = connection
					.prepareCall("{?=call funcGetGlName(?,?,?)}");
			getGLNameStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getGLNameStmt.setString(2, glCode);

			getGLNameStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			getGLNameStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			getGLNameStmt.execute();

			int errorCode = getGLNameStmt.getInt(1);
			String error = getGLNameStmt.getString(4);

			Log.log(Log.DEBUG, "RpDAO", "getGLName", "errorCode,error are "
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "getGLName", error);

				getGLNameStmt.close();
				getGLNameStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			glName = getGLNameStmt.getString(3);
			getGLNameStmt.close();
			getGLNameStmt = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getGLName", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get GL Description.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getPaymentList", "Exited");

		return glName;
	}

	/**
	 * This method the memberIds for which SHDAN has to be generated
	 * 
	 * @author SS14485
	 * 
	 *         To change the template for this generated type comment go to
	 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
	 *         Comments
	 */
	public ArrayList getMemberIdsForSHDAN() throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getMemberIdsForSHDAN", "Entered");
		ArrayList memberIds = new ArrayList();

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement memberIdsList = connection
					.prepareCall("{?=call packGetSHDANMemberIds.funcGetSHDANMemberIds(?,?)}");

			memberIdsList.registerOutParameter(1, java.sql.Types.INTEGER);
			memberIdsList.registerOutParameter(2, Constants.CURSOR);
			memberIdsList.registerOutParameter(3, java.sql.Types.VARCHAR);

			memberIdsList.execute();
			int memberIdValue = memberIdsList.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getMemberIdsForSHDAN",
					"memberIdValue :" + memberIdValue);

			if (memberIdValue == Constants.FUNCTION_FAILURE) {

				String error = memberIdsList.getString(3);

				memberIdsList.close();
				memberIdsList = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getMemberIdsForSHDAN", "error:"
						+ error);

				throw new DatabaseException(error);
			} else {

				ResultSet memberIdResult = (ResultSet) memberIdsList
						.getObject(2);
				while (memberIdResult.next()) {
					String memberId = memberIdResult.getString(1);
					if (memberId != null && !memberId.equals("")) {
						memberIds.add(memberId);
					}
				}
				memberIdResult.close();
				memberIdResult = null;
				memberIdsList.close();
				memberIdsList = null;
			}
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getMemberIdsForSHDAN",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Member Ids.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getMemberIdsForSHDAN", "Exited");
		return memberIds;
	}

	/**
	 * This method the memberIds for which Excess voucher has to be generated
	 * 
	 * @author SS14485
	 * 
	 *         To change the template for this generated type comment go to
	 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
	 *         Comments
	 */
	public ArrayList getMemberIdsForExcess() throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getMemberIdsForExcess", "Entered");
		ArrayList memberIds = new ArrayList();

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement memberIdsList = connection
					.prepareCall("{?=call packGetMemberIdsForExcess.funcGetMemberIdsForExcess(?,?)}");

			memberIdsList.registerOutParameter(1, java.sql.Types.INTEGER);
			memberIdsList.registerOutParameter(2, Constants.CURSOR);
			memberIdsList.registerOutParameter(3, java.sql.Types.VARCHAR);

			memberIdsList.execute();
			int memberIdValue = memberIdsList.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getMemberIdsForExcess",
					"memberIdValue :" + memberIdValue);

			if (memberIdValue == Constants.FUNCTION_FAILURE) {

				String error = memberIdsList.getString(3);

				memberIdsList.close();
				memberIdsList = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getMemberIdsForExcess", "error:"
						+ error);

				throw new DatabaseException(error);
			} else {

				ResultSet memberIdResult = (ResultSet) memberIdsList
						.getObject(2);
				while (memberIdResult.next()) {
					String memberId = memberIdResult.getString(1);
					if (memberId != null && !memberId.equals("")) {
						memberIds.add(memberId);
					}
				}
				memberIdResult.close();
				memberIdResult = null;
				memberIdsList.close();
				memberIdsList = null;
			}
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getMemberIdsForExcess",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Member Ids.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getMemberIdsForExcess", "Exited");
		return memberIds;
	}

	/**
	 * This method the returns the excess amount for the member passed
	 * 
	 * @author SS14485
	 * 
	 *         To change the template for this generated type comment go to
	 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
	 *         Comments
	 */
	public double getAmountForExcess(String memberId) throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getAmountForExcess", "Entered");
		ArrayList memberIds = new ArrayList();
		double excessAmount;

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement excessAmt = connection
					.prepareCall("{?=call funcGetAmountForExcess(?,?,?,?,?)}");

			excessAmt.setString(2, memberId.substring(0, 4));
			excessAmt.setString(3, memberId.substring(4, 8));
			excessAmt.setString(4, memberId.substring(8, 12));

			excessAmt.registerOutParameter(1, java.sql.Types.INTEGER);
			excessAmt.registerOutParameter(5, java.sql.Types.DOUBLE);
			excessAmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			excessAmt.execute();
			int excessAmountValue = excessAmt.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getAmountForExcess", "memberIdValue :"
					+ excessAmountValue);

			if (excessAmountValue == Constants.FUNCTION_FAILURE) {

				String error = excessAmt.getString(6);

				excessAmt.close();
				excessAmt = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getAmountForExcess", "error:"
						+ error);

				throw new DatabaseException(error);
			} else {

				excessAmount = excessAmt.getDouble(5);

			}
			excessAmt.close();
			excessAmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getAmountForExcess",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to get Member Ids.");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getAmountForExcess", "Exited");
		return excessAmount;
	}

	/**
	 * This method updates the voucher Id for the mli Id passed
	 * 
	 * @author SS14485
	 * 
	 *         To change the template for this generated type comment go to
	 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
	 *         Comments
	 */
	public void updateIdForExcess(String memberId, String voucherId)
			throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "updateIdForExcess", "Entered");
		ArrayList memberIds = new ArrayList();
		double excessAmount;

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement excessAmt = connection
					.prepareCall("{?=call funcUpdateIdForExcess(?,?,?,?,?)}");

			excessAmt.setString(2, memberId.substring(0, 4));
			excessAmt.setString(3, memberId.substring(4, 8));
			excessAmt.setString(4, memberId.substring(8, 12));
			excessAmt.setString(5, voucherId);

			excessAmt.registerOutParameter(1, java.sql.Types.INTEGER);

			excessAmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			excessAmt.execute();
			int excessAmountValue = excessAmt.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "updateIdForExcess", "memberIdValue :"
					+ excessAmountValue);

			if (excessAmountValue == Constants.FUNCTION_FAILURE) {

				String error = excessAmt.getString(6);

				excessAmt.close();
				excessAmt = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "updateIdForExcess", "error:"
						+ error);

				throw new DatabaseException(error);
			}
			excessAmt.close();
			excessAmt = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "updateIdForExcess",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to insert voucher Id");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "updateIdForExcess", "Exited");

	}

	public Date getGuarStartDate(Application application)
			throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getGuarStartDate", "Exited");

		Date guaranteeStartDate = null;

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement guarStartDt = connection
					.prepareCall("{?=call funcGetGuarStartDate(?,?,?)}");

			guarStartDt.setString(2, application.getCgpan());

			Log.log(Log.INFO, "RpDAO", "getGuarStartDate",
					"application.getAppRefNo() :" + application.getAppRefNo());
			Log.log(Log.INFO, "RpDAO", "getGuarStartDate",
					"application.getCgpan() :" + application.getCgpan());

			guarStartDt.registerOutParameter(1, java.sql.Types.INTEGER);
			guarStartDt.registerOutParameter(3, java.sql.Types.DATE);
			guarStartDt.registerOutParameter(4, java.sql.Types.VARCHAR);

			guarStartDt.execute();
			int guarStartDtValue = guarStartDt.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getGuarStartDate",
					"guarStartDtValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = guarStartDt.getString(4);

				guarStartDt.close();
				guarStartDt = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getGuarStartDate", "error:"
						+ error);

				throw new DatabaseException(error);
			} else {

				guaranteeStartDate = guarStartDt.getDate(3);
			}
			guarStartDt.close();
			guarStartDt = null;

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getGuarStartDate",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get guarantee Start Date");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "updateIdForExcess", "Exited");

		return guaranteeStartDate;
	}

	/* ------------------- */

	/**
	 * capture expiry date of application
	 * 
	 * @param application
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public Date getExpiryDate(Application application) throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getExpiryDate", "Exited");

		Date expiryDate = null;

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement expiryDt = connection
					.prepareCall("{?=call funcGetExpiryDate(?,?,?)}");

			expiryDt.setString(2, application.getCgpan());

			Log.log(Log.INFO, "RpDAO", "getExpiryDate",
					"application.getAppRefNo() :" + application.getAppRefNo());
			Log.log(Log.INFO, "RpDAO", "getExpiryDate",
					"application.getCgpan() :" + application.getCgpan());

			expiryDt.registerOutParameter(1, java.sql.Types.INTEGER);
			expiryDt.registerOutParameter(3, java.sql.Types.DATE);
			expiryDt.registerOutParameter(4, java.sql.Types.VARCHAR);

			expiryDt.execute();
			int getExpDtValue = expiryDt.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getExpiryDate", "getExpiryDate :"
					+ getExpDtValue);

			if (getExpDtValue == Constants.FUNCTION_FAILURE) {

				String error = expiryDt.getString(4);

				expiryDt.close();
				expiryDt = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getExpiryDate", "error:" + error);

				throw new DatabaseException(error);
			} else {

				expiryDate = expiryDt.getDate(3);
			}
			expiryDt.close();
			expiryDt = null;

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getExpiryDate", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Expiry Date");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "updateIdForExcess", "Exited");

		return expiryDate;
	}

	/* --------------------- */

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public Date getExpiryDateofSpecifiedCgpan(String cgpan)
			throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getExpiryDateofSpecifiedCgpan", "Exited");

		Date expiryDate = null;

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement expiryDt = connection
					.prepareCall("{?=call funcGetExpiryDate(?,?,?)}");

			expiryDt.setString(2, cgpan);

			Log.log(Log.INFO, "RpDAO", "getExpiryDate",
					"application.getCgpan() :" + cgpan);

			expiryDt.registerOutParameter(1, java.sql.Types.INTEGER);
			expiryDt.registerOutParameter(3, java.sql.Types.DATE);
			expiryDt.registerOutParameter(4, java.sql.Types.VARCHAR);

			expiryDt.execute();
			int getExpDtValue = expiryDt.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getExpiryDate", "getExpiryDate :"
					+ getExpDtValue);

			if (getExpDtValue == Constants.FUNCTION_FAILURE) {

				String error = expiryDt.getString(4);

				expiryDt.close();
				expiryDt = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getExpiryDate", "error:" + error);

				throw new DatabaseException(error);
			} else {

				expiryDate = expiryDt.getDate(3);
			}
			expiryDt.close();
			expiryDt = null;

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getExpiryDateofSpecifiedCgpan",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Expiry Date");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "updateIdForExcess", "Exited");

		return expiryDate;
	}

	public ArrayList getAllCardRates() throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getAllCardRates", "Exited");

		Connection connection = DBConnection.getConnection(false);
		GFCardRate cardRate = new GFCardRate();
		ArrayList cardRateList = new ArrayList();

		try {

			CallableStatement gfCardRate = connection
					.prepareCall("{?=call packGetAllGFCardRate.funcGetGFCardRates(?,?)}");

			gfCardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			gfCardRate.registerOutParameter(2, Constants.CURSOR);
			gfCardRate.registerOutParameter(3, java.sql.Types.VARCHAR);

			gfCardRate.execute();
			int guarStartDtValue = gfCardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getAllCardRates",
					"getAllCardRatesValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = gfCardRate.getString(3);

				gfCardRate.close();
				gfCardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getAllCardRates", "error:" + error);

				throw new DatabaseException(error);
			} else {
				ResultSet cardRateResults = (ResultSet) gfCardRate.getObject(2);
				while (cardRateResults.next()) {
					cardRate = new GFCardRate();
					cardRate.setCardRateId(cardRateResults.getInt(1));
					cardRate.setLowRangeAmount(cardRateResults.getDouble(2));
					cardRate.setHighRangeAmount(cardRateResults.getDouble(3));
					cardRate.setGfCardRate(cardRateResults.getDouble(4));

					cardRateList.add(cardRate);
				}

				cardRateResults.close();
				cardRateResults = null;

			}

			gfCardRate.close();
			gfCardRate = null;

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getAllCardRates",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get guarantee Start Date");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getAllCardRates", "Exited");

		return cardRateList;
	}

	public void updateCardRate(int cardId, double cardRate, String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "updateCardRate", "Exited");

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement updateRate = connection
					.prepareCall("{?=call funcUpdateCardRate(?,?,?,?)}");

			updateRate.setInt(2, cardId);
			updateRate.setDouble(3, cardRate);
			updateRate.setString(4, userId);

			updateRate.registerOutParameter(1, java.sql.Types.INTEGER);
			updateRate.registerOutParameter(5, java.sql.Types.VARCHAR);

			updateRate.execute();
			int guarStartDtValue = updateRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "updateCardRate",
					"getAllCardRatesValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = updateRate.getString(5);

				updateRate.close();
				updateRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "updateCardRate", "error:" + error);

				throw new DatabaseException(error);
			}
			updateRate.close();
			updateRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "updateCardRate",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Update Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getAllCardRates", "Exited");

	}

	public double getCardRate(double sanctionedAmount) throws DatabaseException {
		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;

		try {

			CallableStatement cardRate = connection
					.prepareCall("{?=call funcGetCardRate(?,?,?)}");

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "sanc amt :"
					+ sanctionedAmount);
			cardRate.setDouble(2, sanctionedAmount);

			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			cardRate.registerOutParameter(3, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(4, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "getAllCardRatesValue :"
					+ guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = cardRate.getString(4);

				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate", "error:" + error);

				throw new DatabaseException(error);
			} else {
				gfCardRate = cardRate.getDouble(3);
				Log.log(Log.INFO, "RpDAO", "getCardRate", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getCardRate", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		return gfCardRate;

	}

	// Method overLoaded and Added by sudeep.dhiman@pathinfotech.com, on 12 dec
	// 2006
	// method, now have more parameters required to calculate the guarentee fee
	// rate.
	// before above method is used which has only singal parameter double
	// sanctionedAmount.

//Diksha...................................................................
	
	
	/*public double getCardRate(String socialStatus, String sex, String district,
			String state, double sanctionedAmount) throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;

		// System.out.println("the social status is "+socialStatus);
		// System.out.println("the sex is "+sex);
		// System.out.println("the DISTRICT  is "+district);
		// System.out.println("the state is "+state);
		// System.out.println("the amount is "+sanctionedAmount);

		try {

			// code used before
			// CallableStatement cardRate =
			// connection.prepareCall("{?=call funcGetCardRate(?,?,?)}");

			// code used now
			CallableStatement cardRate = connection
					.prepareCall("{?=call funcGuaranteeCardRate(?,?,?,?,?,?,?)}");

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "sanc amt :"
					+ sanctionedAmount);
			// following new setter method added
			cardRate.setString(2, socialStatus);
			cardRate.setString(3, sex);
			cardRate.setString(4, district);
			cardRate.setString(5, state);
			// ////////////

			cardRate.setDouble(6, sanctionedAmount);

			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			cardRate.registerOutParameter(7, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(8, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "getAllCardRatesValue :"
					+ guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = cardRate.getString(8);

				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate", "error:" + error);

				throw new DatabaseException(error);
			} else {
				gfCardRate = cardRate.getDouble(7);
				// System.out.println("The card rate found is : "+gfCardRate);
				Log.log(Log.INFO, "RpDAO", "getCardRate", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getCardRate", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		return gfCardRate;

	}*/

	
	
	//Diskha end
	
	//Diskha 
	
	public double[] getCardRate(String socialStatus, String sex, String district, String state, double sanctionedAmount,String scheme,Date sancdt,String microflag,String  memId
    )throws DatabaseException, SQLException{
    
Log.log(Log.INFO,"RpDAO","getCardRate","Exited");

Connection connection = DBConnection.getConnection(false) ;
double gfStandardCardRate;

double gfNpaRiskCardRate;

double gfClaimRiskCardRate;

double gfFinalCardRate;
double guaranteeParam[] =new double[5] ;

System.out.println("the social status is "+socialStatus);
System.out.println("the sex is "+sex);
System.out.println("the DISTRICT  is "+district);
System.out.println("the state is "+state);
System.out.println("the amount is "+sanctionedAmount);

System.out.println("the scheme status is "+scheme);
System.out.println("the sancdt is "+sancdt);
System.out.println("the microflag  is "+microflag);
System.out.println("the memId is "+memId);
//System.out.println("the amount is "+sanctionedAmount);

CallableStatement cardRate=null;

Statement cardRatestmt=connection.createStatement();

ResultSet cardrs=null;

String bankName="";




try
{

//code used before
//CallableStatement cardRate = connection.prepareCall("{?=call funcGetCardRate(?,?,?)}");

//code used now
//cardRate = connection.prepareCall("{?=call funcGuaranteeCardRate(?,?,?,?,?,?,?,?,?,?,?)}");

cardRate = connection.prepareCall("{?=call FUNCGUARANTEECARDRATEMODDIF(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");


Log.log(Log.DEBUG,"RpDAO","getCardRate","sanc amt :" + sanctionedAmount);
//following new setter method added 
cardRate.setString(2,socialStatus);
cardRate.setString(3,sex);
cardRate.setString(4,district);
cardRate.setString(5,state);    

cardRate.setDouble(6,sanctionedAmount);

//System.out.println(sanctionedAmount);


cardRate.setString(7,scheme);

cardRate.setDate(8,new java.sql.Date(sancdt.getTime()));

//System.out.println("(java.sql.Date) sancdt: "+new java.sql.Date(sancdt.getTime()));

cardRate.setString(9,microflag);
cardRate.setString(10,memId);

cardRate.registerOutParameter(1, java.sql.Types.INTEGER) ;				
cardRate.registerOutParameter(11, java.sql.Types.DOUBLE) ;
cardRate.registerOutParameter(12, java.sql.Types.DOUBLE) ;
cardRate.registerOutParameter(13, java.sql.Types.DOUBLE) ;
cardRate.registerOutParameter(14, java.sql.Types.DOUBLE) ;

cardRate.registerOutParameter(15, java.sql.Types.VARCHAR) ;

cardRate.execute();
int guarStartDtValue=cardRate.getInt(1);

Log.log(Log.DEBUG,"RpDAO","getCardRate","getAllCardRatesValue :" + guarStartDtValue);

if(guarStartDtValue==Constants.FUNCTION_FAILURE){

String error = cardRate.getString(15);

cardRate.close();
cardRate=null;

connection.rollback();

Log.log(Log.DEBUG,"RpDAO","getCardRate","error:" + error);

throw new DatabaseException(error);
}
else{


gfStandardCardRate = cardRate.getDouble(11);

gfNpaRiskCardRate = cardRate.getDouble(12);

gfClaimRiskCardRate = cardRate.getDouble(13);

gfFinalCardRate = cardRate.getDouble(14);

guaranteeParam[0]=gfStandardCardRate;
guaranteeParam[1]=gfNpaRiskCardRate;
guaranteeParam[2]=gfClaimRiskCardRate;
guaranteeParam[3]=gfFinalCardRate;
System.out.println("The card rate found is enhance : "+gfStandardCardRate);
System.out.println("The card rate found is enhance : "+gfNpaRiskCardRate);

System.out.println("The card rate found is enhance : "+gfClaimRiskCardRate);

System.out.println("The card rate found is enhance : "+gfFinalCardRate);

//	Log.log(Log.INFO,"RpDAO","getCardRate","card rate :" + gfCardRate);
}
cardRate.close();
cardRate=null;

connection.commit();

}
catch (SQLException exception)
{
Log.log(Log.ERROR,"RpDAO","getCardRate",exception.getMessage());
Log.logException(exception);

try
{
connection.rollback();
}
catch (SQLException ignore){}


throw new DatabaseException("Unable to Get Card Rate");
}
finally
{
DBConnection.freeConnection(connection);
}

Log.log(Log.INFO,"RpDAO","getCardRate","Exited");

return guaranteeParam;


}
	//Diksabh end
	
	// added@path on 16092013

	public double getCardRate(String socialStatus, String sex, String district,
			String state, double sanctionedAmount, String scheme,
			Date sanction_dt, String microFlag) throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;

		// System.out.println("the social status is "+socialStatus);
		// System.out.println("the sex is "+sex);
		// System.out.println("the DISTRICT  is "+district);
		// System.out.println("the state is "+state);
		// System.out.println("the amount is "+sanctionedAmount);

		try {

			// code used before
			// CallableStatement cardRate =
			// connection.prepareCall("{?=call funcGetCardRate(?,?,?)}");

			// code used now
			// CallableStatement cardRate =
			// connection.prepareCall("{?=call funcGuaranteeCardRate(?,?,?,?,?,?,?)}");
			CallableStatement cardRate = connection
					.prepareCall("{?=call funcGuaranteeCardRateMod(?,?,?,?,?,?,?,?,?,?)}");
			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "sanc amt :"
					+ sanctionedAmount);
			// following new setter method added
			cardRate.setString(2, socialStatus);
			cardRate.setString(3, sex);
			cardRate.setString(4, district);
			cardRate.setString(5, state);
			cardRate.setDouble(6, sanctionedAmount);
			// ////////////
			cardRate.setString(7, scheme);

			if (sanction_dt != null) {
				cardRate.setDate(8, new java.sql.Date(sanction_dt.getTime()));
			} else {
				cardRate.setNull(8, Types.DATE);
			}
			cardRate.setString(9, microFlag);

			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			// cardRate.registerOutParameter(7, java.sql.Types.DOUBLE) ;
			// cardRate.registerOutParameter(8, java.sql.Types.VARCHAR) ;

			cardRate.registerOutParameter(10, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(11, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "getAllCardRatesValue :"
					+ guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				// String error = cardRate.getString(8);
				String error = cardRate.getString(11);
				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate", "error:" + error);

				throw new DatabaseException(error);
			} else {
				// gfCardRate = cardRate.getDouble(7);
				gfCardRate = cardRate.getDouble(10);

				// System.out.println("The card rate found is : "+gfCardRate);
				Log.log(Log.INFO, "RpDAO", "getCardRate", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getCardRate", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getCardRate", "Exited");

		return gfCardRate;

	}

	/**
	 * 
	 * @param socialStatus
	 * @param sex
	 * @param district
	 * @param state
	 * @param sanctionedAmount
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getCardRate1(String socialStatus, String sex,
			String district, String state, double sanctionedAmount,
			String scheme) throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "getCardRate1", "Exited");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;

		// System.out.println("the social status is "+socialStatus);
		// System.out.println("the sex is "+sex);
		// System.out.println("the DISTRICT  is "+district);
		// System.out.println("the state is "+state);
		// System.out.println("the amount is "+sanctionedAmount);
		// System.out.println("Scheme-"+scheme);

		try {

			// code used before
			// CallableStatement cardRate =
			// connection.prepareCall("{?=call funcGetCardRate(?,?,?)}");

			// code used now
			CallableStatement cardRate = connection
					.prepareCall("{?=call FuncguaranteecardrateNew(?,?,?,?,?,?,?,?)}");

			Log.log(Log.DEBUG, "RpDAO", "getCardRate1", "sanc amt :"
					+ sanctionedAmount);
			// following new setter method added
			cardRate.setString(2, socialStatus);
			cardRate.setString(3, sex);
			cardRate.setString(4, district);
			cardRate.setString(5, state);
			// ////////////

			cardRate.setDouble(6, sanctionedAmount);
			cardRate.setString(7, scheme);

			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			cardRate.registerOutParameter(8, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(9, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getCardRate1",
					"getAllCardRatesValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = cardRate.getString(9);

				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate1", "error:" + error);

				throw new DatabaseException(error);
			} else {
				gfCardRate = cardRate.getDouble(8);
				// System.out.println("The card rate found is : "+gfCardRate);
				Log.log(Log.INFO, "RpDAO", "getCardRate1", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getCardRate1", exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getCardRate1", "Exited");

		return gfCardRate;

	}

	public void updateGFeeForAppropriation(DemandAdvice demandAdvice,
			Date realisationDate, Connection connection)
			throws DatabaseException {
		String methodName = "updateGFeeForAppropriation";

		Log.log(Log.INFO, className, methodName, "Entered");

		boolean newConn = false;

		if (connection == null) {
			connection = DBConnection.getConnection(false);
			newConn = true;
		}
		try {
			CallableStatement callable = null;
			int errorCode;
			String error;
			String danType = demandAdvice.getDanNo().substring(0, 2);

			callable = connection
					.prepareCall("{?=call funcUpdGFeeAppropriation (?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);

			callable.setString(2, demandAdvice.getDanNo());

			callable.setString(3, demandAdvice.getCgpan());

			callable.setDouble(4, demandAdvice.getAmountRaised());

			callable.setDate(5, new java.sql.Date(realisationDate.getTime()));

			callable.setString(6, demandAdvice.getUserId());

			callable.registerOutParameter(7, Types.VARCHAR);

			callable.execute();

			errorCode = callable.getInt(1);
 
			System.out.println(" errorCode +"+errorCode);
			error = callable.getString(7);
			
			System.out.println(" error 19715 +"+error);

			Log.log(Log.DEBUG, className, methodName, "error code and error"
					+ errorCode + "," + error);

			if (errorCode == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, className, methodName, error);

				callable.close();
				callable = null;
				connection.rollback();
				System.out.println(" error 19726 +"+error);
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;

			if (newConn) {
				connection.commit();
			}
		} catch (SQLException e) {
			
			System.out.println("e SQLException 19738 " +e.getMessage());
			Log.log(Log.ERROR, className, methodName, e.getMessage());

			Log.logException(e);

			if (newConn) {
				try {
					connection.rollback();
				} catch (SQLException ignore) {
					
					System.out.println("SQLException "+ignore);
				}
			}

			throw new DatabaseException("Unable to update realization date.");

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO, className, methodName, "Exited");
	}

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getTotalSanctionedAmount(String cgpan)
			throws DatabaseException {
		Log.log(Log.INFO, "RpProcessor", "getTotalSanctionedAmount", "Entered");
		PreparedStatement sanctiondAmountStatement = null;
		ResultSet sanctionedAmountResult = null;
		double tempTotalAmount = 0.0;
		Connection connection = DBConnection.getConnection();
		try {
			/*
			 * String query =
			 * " select Sum(nvl(tc_guarantee_amt,0) + nvl(wc_fb_credit_to_guarantee,0) + nvl(wc_nfb_credit_to_guarantee,0)) TEMPAMT "
			 * + " from view_appl_amounts_modified "+
			 * " where cgpan like substr(?,0,length(?)-2)||'%' ";
			 */
			String query = " select Sum(nvl(APPROVED_AMT,0)) TEMPAMT "
					+ " from view_appl_amounts_modified "
					+ " where cgpan like substr(?,0,length(?)-2)||'%' ";
			sanctiondAmountStatement = connection.prepareStatement(query);
			sanctiondAmountStatement.setString(1, cgpan);
			sanctiondAmountStatement.setString(2, cgpan);
			sanctionedAmountResult = sanctiondAmountStatement.executeQuery();
			while (sanctionedAmountResult.next()) {
				tempTotalAmount = sanctionedAmountResult.getDouble(1);

			}
			sanctionedAmountResult.close();
			sanctionedAmountResult = null;
			sanctiondAmountStatement.close();
			sanctiondAmountStatement = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return tempTotalAmount;

	}

	/**
	 * bug fix on 19-Nov-2010 added by sukumar@path on 19-Nov-2010 for fixing
	 * claim applied amount should not be more than Application Guarantee
	 * Approved Amt.
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getTotalSanctionedAmountforCgpan(String cgpan)
			throws DatabaseException {
		Log.log(Log.INFO, "RpProcessor", "getTotalSanctionedAmountforCgpan",
				"Entered");
		PreparedStatement sanctiondAmountStatement = null;
		ResultSet sanctionedAmountResult = null;
		double tempTotalAmount = 0.0;
		Connection connection = DBConnection.getConnection();
		try {
			/*
			 * String query =
			 * " select Sum(nvl(tc_guarantee_amt,0) + nvl(wc_fb_credit_to_guarantee,0) + nvl(wc_nfb_credit_to_guarantee,0)) TEMPAMT "
			 * + " from view_appl_amounts_modified "+
			 * " where cgpan like substr(?,0,length(?)-2)||'%' ";
			 */
			String query = " select Sum(nvl(APPROVED_AMT,0)) TEMPAMT "
					+ " from view_appl_amounts_modified " + " where cgpan= ? ";
			sanctiondAmountStatement = connection.prepareStatement(query);
			sanctiondAmountStatement.setString(1, cgpan);
			// sanctiondAmountStatement.setString(2,cgpan);
			sanctionedAmountResult = sanctiondAmountStatement.executeQuery();
			while (sanctionedAmountResult.next()) {
				tempTotalAmount = sanctionedAmountResult.getDouble(1);

			}
			sanctionedAmountResult.close();
			sanctionedAmountResult = null;
			sanctiondAmountStatement.close();
			sanctiondAmountStatement = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return tempTotalAmount;

	}

	/**
	 * 
	 * @param ssiRef
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getTotalSanctionedAmountNew(String ssiRef)
			throws DatabaseException {
		Log.log(Log.INFO, "RpProcessor", "getTotalSanctionedAmountNew",
				"Entered");
		PreparedStatement sanctiondAmountStatement = null;
		ResultSet sanctionedAmountResult = null;
		double tempTotalAmount = 0.0;
		Connection connection = DBConnection.getConnection();
		try {
			/*
			 * String query =
			 * " select Sum(nvl(tc_guarantee_amt,0) + nvl(wc_fb_credit_to_guarantee,0) + nvl(wc_nfb_credit_to_guarantee,0)) TEMPAMT "
			 * + " from view_appl_amounts_modified "+
			 * " where cgpan like substr(?,0,length(?)-2)||'%' ";
			 */
			String query = " select Sum(nvl(APPROVED_AMT,0)) TEMPAMT "
					+ " from VIEW_APPL_AMOUNT_MOD_NEW "
					+ " where SSI_REFERENCE_NUMBER = ? ";
			sanctiondAmountStatement = connection.prepareStatement(query);
			sanctiondAmountStatement.setString(1, ssiRef);

			sanctionedAmountResult = sanctiondAmountStatement.executeQuery();
			while (sanctionedAmountResult.next()) {
				tempTotalAmount = sanctionedAmountResult.getDouble(1);

			}
			sanctionedAmountResult.close();
			sanctionedAmountResult = null;
			sanctiondAmountStatement.close();
			sanctiondAmountStatement = null;
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return tempTotalAmount;

	}

	//Diksha ...............................
	
	public double getEnhanceamt(String ssiRef)
	throws DatabaseException {
Log.log(Log.INFO, "RpProcessor", "getTotalSanctionedAmountNew",
		"Entered");
PreparedStatement sanctiondAmountStatement = null;
ResultSet sanctionedAmountResult = null;
double tempTotalAmount = 0.0;

double enhanceamt = 0.0;
Connection connection = DBConnection.getConnection();
try {
	/*
	 * String query =
	 * " select Sum(nvl(tc_guarantee_amt,0) + nvl(wc_fb_credit_to_guarantee,0) + nvl(wc_nfb_credit_to_guarantee,0)) TEMPAMT "
	 * + " from view_appl_amounts_modified "+
	 * " where cgpan like substr(?,0,length(?)-2)||'%' ";
	 */
	String query = " select ENHANCAMT "
			+ " from VIEW_APPL_GUAR_AMT "
			+ " where SSI_REFERENCE_NUMBER = ? ";
	sanctiondAmountStatement = connection.prepareStatement(query);
	sanctiondAmountStatement.setString(1, ssiRef);

	sanctionedAmountResult = sanctiondAmountStatement.executeQuery();
	while (sanctionedAmountResult.next()) {
		
		
		enhanceamt = sanctionedAmountResult.getDouble(1);

	}
	sanctionedAmountResult.close();
	sanctionedAmountResult = null;
	sanctiondAmountStatement.close();
	sanctiondAmountStatement = null;
} catch (Exception exception) {
	Log.logException(exception);
	throw new DatabaseException(exception.getMessage());
} finally {
	DBConnection.freeConnection(connection);
}
return enhanceamt;

}
	
	
	public double getTotalGuarantedAmount(String ssiRef)
	throws DatabaseException {
Log.log(Log.INFO, "RpProcessor", "getTotalSanctionedAmountNew",
		"Entered");
PreparedStatement sanctiondAmountStatement = null;
ResultSet sanctionedAmountResult = null;
double tempTotalAmount = 0.0;
Connection connection = DBConnection.getConnection();
try {
	/*
	 * String query =
	 * " select Sum(nvl(tc_guarantee_amt,0) + nvl(wc_fb_credit_to_guarantee,0) + nvl(wc_nfb_credit_to_guarantee,0)) TEMPAMT "
	 * + " from view_appl_amounts_modified "+
	 * " where cgpan like substr(?,0,length(?)-2)||'%' ";
	 */
	String query = " select net_guar_amt TEMPAMT "
			+ " from VIEW_APPL_GUAR_AMT "
			+ " where SSI_REFERENCE_NUMBER = ? ";
	sanctiondAmountStatement = connection.prepareStatement(query);
	sanctiondAmountStatement.setString(1, ssiRef);

	sanctionedAmountResult = sanctiondAmountStatement.executeQuery();
	while (sanctionedAmountResult.next()) {
		tempTotalAmount = sanctionedAmountResult.getDouble(1);

	}
	sanctionedAmountResult.close();
	sanctionedAmountResult = null;
	sanctiondAmountStatement.close();
	sanctiondAmountStatement = null;
} catch (Exception exception) {
	Log.logException(exception);
	throw new DatabaseException(exception.getMessage());
} finally {
	DBConnection.freeConnection(connection);
}
return tempTotalAmount;

}
	
	
	
	//Diksha end.............................
	
	
	
	
	// Method Added by sudeep.dhiman@pathinfotech.com, on 12 dec 2006
	// to calculate the Service fee rate.
	// before this service fee rate is directly picked up from ParameterMaster
	// Table.

	public double getServiceRate(String socialStatus, String sex,
			String district, String state, double approvedAmount)
			throws DatabaseException {

		// Log.log(Log.INFO,"RpDAO","getServiceRate","Exited");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;
		// System.out.println("+++++++++++++=the value passed to function are :"+socialStatus+"___"+sex+"___ "+district+"___ "+state+"___ "+approvedAmount);

		try {
			// System.out.println("Inside ServiceRAteCalculation methods");
			CallableStatement cardRate = connection
					.prepareCall("{?=call funcServiceCardRate(?,?,?,?,?,?,?)}");

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "sanc amt :"
					+ approvedAmount);
			cardRate.setString(2, socialStatus);
			cardRate.setString(3, sex);
			cardRate.setString(4, district);
			cardRate.setString(5, state);
			cardRate.setDouble(6, approvedAmount);

			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			cardRate.registerOutParameter(7, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(8, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getServiceRate",
					"getAllCardRatesValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = cardRate.getString(8);

				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate", "error:" + error);

				throw new DatabaseException(error);
			} else {
				gfCardRate = cardRate.getDouble(7);
				// System.out.println("The value of service rate is "+gfCardRate);
				Log.log(Log.INFO, "RpDAO", "getCardRate", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getServiceCardRate",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Service Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getServiceCardRate", "Exited");

		return gfCardRate;

	}

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public SubScheme getSchemeDetailsForCgpan(String cgpan)
			throws DatabaseException {

		// Log.log(Log.INFO,"RpDAO","getSchemeDetailsForCgpan","Exited");

		Connection connection = DBConnection.getConnection(false);

		SubScheme scheme = new SubScheme();

		try {

			CallableStatement schemeDtls = connection
					.prepareCall("{?=call FUNCGETSCHEMENAMEFORCGPAN(?,?,?,?)}");

			schemeDtls.setString(2, cgpan);

			schemeDtls.registerOutParameter(1, java.sql.Types.INTEGER);

			schemeDtls.registerOutParameter(3, java.sql.Types.VARCHAR);
			schemeDtls.registerOutParameter(4, java.sql.Types.VARCHAR);
			schemeDtls.registerOutParameter(5, java.sql.Types.VARCHAR);

			schemeDtls.execute();

			int retrunvalue = schemeDtls.getInt(1);

			Log.log(Log.DEBUG, "RpDAO", "getSchemeDetailsForCgpan",
					"retrunvalue :" + retrunvalue);

			if (retrunvalue == Constants.FUNCTION_FAILURE) {

				String error = schemeDtls.getString(5);

				schemeDtls.close();
				schemeDtls = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getSchemeDetailsForCgpan",
						"error:" + error);

				throw new DatabaseException(error);
			} else {
				scheme.setScmId(schemeDtls.getInt(3));
				scheme.setSchemeName(schemeDtls.getString(4));

			}

			schemeDtls.close();
			schemeDtls = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getSchemeDetailsForCgpan",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Scheme Details");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getSchemeDetailsForCgpan", "Exited");

		return scheme;

	}

	/**
	 * 
	 * @param socialStatus
	 * @param sex
	 * @param district
	 * @param state
	 * @param approvedAmount
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getServiceRateForRSF(String socialStatus, String sex,
			String district, String state, double approvedAmount,
			String schemeId) throws DatabaseException {

		Log.log(Log.INFO, "RpDAO", "getServiceRateForRSF", "Entered");

		// System.out.println("getServiceRateForRSF Entered");

		Connection connection = DBConnection.getConnection(false);
		double gfCardRate;
		// System.out.println("+++++++++++++=the value passed to function are :"+socialStatus+"___"+sex+"___ "+district+"___ "+state+"___ "+approvedAmount+"----"+schemeId);

		try {
			// System.out.println("Inside ServiceRAteCalculation methods");
			CallableStatement cardRate = connection
					.prepareCall("{?=call FuncservicecardrateNew(?,?,?,?,?,?,?,?)}");

			Log.log(Log.DEBUG, "RpDAO", "getCardRate", "sanc amt :"
					+ approvedAmount);
			cardRate.setString(2, socialStatus);
			cardRate.setString(3, sex);
			cardRate.setString(4, district);
			cardRate.setString(5, state);
			cardRate.setDouble(6, approvedAmount);
			cardRate.setString(7, schemeId);
			cardRate.registerOutParameter(1, java.sql.Types.INTEGER);
			cardRate.registerOutParameter(8, java.sql.Types.DOUBLE);
			cardRate.registerOutParameter(9, java.sql.Types.VARCHAR);

			cardRate.execute();
			int guarStartDtValue = cardRate.getInt(1);
			// System.out.println("guarStartDtValue:"+guarStartDtValue);
			Log.log(Log.DEBUG, "RpDAO", "getServiceRateForRSF",
					"getAllCardRatesValue :" + guarStartDtValue);

			if (guarStartDtValue == Constants.FUNCTION_FAILURE) {

				String error = cardRate.getString(9);
				// System.out.println("Error:"+error);
				cardRate.close();
				cardRate = null;

				connection.rollback();

				Log.log(Log.DEBUG, "RpDAO", "getCardRate", "error:" + error);

				throw new DatabaseException(error);
			} else {
				gfCardRate = cardRate.getDouble(8);
				// System.out.println("The value of service rate is "+gfCardRate);
				Log.log(Log.INFO, "RpDAO", "getCardRate", "card rate :"
						+ gfCardRate);
			}
			cardRate.close();
			cardRate = null;

			connection.commit();

		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getServiceRateForRSF",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to Get Service Card Rate");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getServiceRateForRSF", "Exited");

		return gfCardRate;

	}

	public PaymentDetails getPayInSlipDetailsForEPAY(String paymentId,
			String memberId) throws DatabaseException {
		PaymentDetails paymentDetails = new PaymentDetails();
		Connection connection = null;
		CallableStatement getPayInSlipDetailsStmt = null;
		// ResultSet resultSet = null;

		connection = DBConnection.getConnection();

		try {
			Log.log(Log.INFO, "RpDAO", "getPayInSlipDetailsForEPAY", "Entered");

			String exception = "";

			getPayInSlipDetailsStmt = connection
					.prepareCall("{?=call funcGetPaydetforepay(?,?,?,?,?,?,?)}");
			getPayInSlipDetailsStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getPayInSlipDetailsStmt.setString(2, paymentId);
			getPayInSlipDetailsStmt.setString(3, memberId);
			getPayInSlipDetailsStmt.registerOutParameter(4,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(5,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(6,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(7,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.registerOutParameter(8,
					java.sql.Types.VARCHAR);
			getPayInSlipDetailsStmt.execute();

			int result = getPayInSlipDetailsStmt.getInt(1);
			exception = getPayInSlipDetailsStmt.getString(8);

			Log.log(Log.DEBUG, "RpDAO", "getPayInSlipDetails",
					"errorCode,error are " + result + "," + exception);

			if (result == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails", exception);

				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;

				// connection.rollback();

				throw new DatabaseException(exception);
			} else {
				paymentDetails.setPaymentId(paymentId);
				paymentDetails.setDrawnAtBank(getPayInSlipDetailsStmt
						.getString(4));
				paymentDetails.setPayableAt(getPayInSlipDetailsStmt
						.getString(5));
				paymentDetails.setDrawnAtBranch(getPayInSlipDetailsStmt
						.getString(6));
				paymentDetails.setInstrumentAmount(getPayInSlipDetailsStmt
						.getDouble(7));
				getPayInSlipDetailsStmt.close();
				getPayInSlipDetailsStmt = null;
			}
			// connection.commit();
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "RpDAO", "getPayInSlipDetails",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				ignore.printStackTrace();
			}
			throw new DatabaseException("Unable to get Payment details for RP:"
					+ paymentId);
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "RpDAO", "getPayInSlipDetailsForEPAY", "Exited");

		return paymentDetails;
	}

	public ArrayList getReceivedPaymentsForCLAIM1(Date toDate, Date fromDate)
			throws DatabaseException {
		PaymentList paymentList = null;
		Connection connection = null;
		CallableStatement getReceivedPaymentsStmt = null;
		ArrayList receivedPayments = null;
		ResultSet resultSet = null;

		connection = DBConnection.getConnection(false);

		try {
			String exception = "";

			getReceivedPaymentsStmt = connection
					.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForCLAIM1(?,?,?,?)}");
			getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);

			getReceivedPaymentsStmt.setDate(2,
					new java.sql.Date(toDate.getTime()));

			getReceivedPaymentsStmt.setDate(3,
					new java.sql.Date(fromDate.getTime()));

			getReceivedPaymentsStmt.registerOutParameter(4, Constants.CURSOR);
			getReceivedPaymentsStmt.registerOutParameter(5, Types.VARCHAR);
			getReceivedPaymentsStmt.execute();

			int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = getReceivedPaymentsStmt.getString(5);

				getReceivedPaymentsStmt.close();
				getReceivedPaymentsStmt = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(4);
				exception = getReceivedPaymentsStmt.getString(5);

				// doubt dd whether each element has to be set.
				// Two parameters paymentId, instrumentNo added by sukant
				// PathInfotech on 17/01/2007
				String memberId = "";
				String memberName = "";
				String paymentId = "";
				Date instrumentDt = null;
				String instrumentNo = "";
				int payAmount = 0;
				int noOfRecords = 0;
				int allocatedAmt = 0;
				int cases = 0;
				int shortorExcessAmt = 0;
				while (resultSet.next()) {
					if (noOfRecords == 0) {
						receivedPayments = new ArrayList();
					}
					memberId = resultSet.getString(1);
					memberName = resultSet.getString(2);
					paymentId = resultSet.getString(3);
					instrumentNo = resultSet.getString(4);
					instrumentDt = resultSet.getDate(5);
					payAmount = resultSet.getInt(6);
					allocatedAmt = resultSet.getInt(7);
					cases = resultSet.getInt(8);
					shortorExcessAmt = payAmount - allocatedAmt;
					paymentList = new PaymentList(memberId, memberName,
							paymentId, instrumentNo, instrumentDt, payAmount,
							allocatedAmt, cases, shortorExcessAmt);

					receivedPayments.add(paymentList);
					++noOfRecords;
				}

			}

			resultSet.close();
			getReceivedPaymentsStmt.close();
			getReceivedPaymentsStmt = null;

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

		return receivedPayments;
	}

	 public ArrayList getReceivedPaymentsForASFNew(Date toDate,Date fromDate,String bank) throws DatabaseException {
	        PaymentList paymentList = null;
	        Connection connection = null;
	        CallableStatement getReceivedPaymentsStmt = null;
	        ArrayList receivedPayments = null;
	        ResultSet resultSet = null;

	        connection = DBConnection.getConnection(false);

	       // int  bankId=Integer.parseInt(bank);
	        try {
	            String exception = "";


	            
	            
	            getReceivedPaymentsStmt = 
	                    connection.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForASFNew(?,?,?,?,?)}");
	            
	            System.out.println("packGetPaymentDetails.funcGetPaymentDetailsForASFNew ");
	            getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
	            
	            getReceivedPaymentsStmt.setDate(2, new java.sql.Date(toDate.getTime()));
	            
	            getReceivedPaymentsStmt.setDate(3, new java.sql.Date(fromDate.getTime()));
	            getReceivedPaymentsStmt.setString(4, bank);
	            
	            getReceivedPaymentsStmt.registerOutParameter(5, Constants.CURSOR);
	            getReceivedPaymentsStmt.registerOutParameter(6, Types.VARCHAR);
	            getReceivedPaymentsStmt.execute();

	            int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

	            if (functionReturnValue == Constants.FUNCTION_FAILURE) {

	                String error = getReceivedPaymentsStmt.getString(6);
	                
	                System.out.println("error :"+error);

	                getReceivedPaymentsStmt.close();
	                getReceivedPaymentsStmt = null;

	                connection.rollback();

	                throw new DatabaseException(error);

	            } else {

	                resultSet = (ResultSet)getReceivedPaymentsStmt.getObject(5);
	                exception = getReceivedPaymentsStmt.getString(6);

	                // doubt dd whether each element has to be set.
	                //Two parameters paymentId, instrumentNo added by sukant PathInfotech on 17/01/2007
	                String memberId = "";
	                String memberName = "";
	                String paymentId = "";
	                Date instrumentDt = null;
	                String instrumentNo = "";
	                int payAmount = 0;
	                int noOfRecords = 0;
	                int allocatedAmt = 0;
	                int cases = 0;
	                int shortorExcessAmt=0;
	                while (resultSet.next()) {
	                    if (noOfRecords == 0) {
	                        receivedPayments = new ArrayList();
	                    }
	                    memberId = resultSet.getString(1);
	                    memberName = resultSet.getString(2);
	                    paymentId = resultSet.getString(3);
	                    instrumentNo = resultSet.getString(4);
	                    instrumentDt = resultSet.getDate(5);
	                    payAmount = resultSet.getInt(6);
	                    allocatedAmt = resultSet.getInt(7);
	                    cases = resultSet.getInt(8);
	                    shortorExcessAmt=payAmount-allocatedAmt;
	                    paymentList = 
	                            new PaymentList(memberId, memberName, paymentId, 
	                                            instrumentNo, instrumentDt, 
	                                            payAmount, allocatedAmt, cases,shortorExcessAmt);

	                    receivedPayments.add(paymentList);
	                    ++noOfRecords;
	                    if(noOfRecords==100)
	                    {
	                    	break;
	                    }
	                }

	            }

	            resultSet.close();
	            getReceivedPaymentsStmt.close();
	            getReceivedPaymentsStmt = null;

	            connection.commit();

	        } catch (Exception exception) {
	        	
	        	System.out.println("exception "+exception);

	            try {
	                connection.rollback();
	                
	            } catch (SQLException ignore) {
	            	System.out.println("SQLException"+ignore);
	            }

	            throw new DatabaseException(exception.getMessage());
	        } finally {
	            DBConnection.freeConnection(connection);
	        }

	        return receivedPayments;
	    }

    
    public int openServiceFeeDans(String memberId) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int max_count = 0;
		int count_expiry_date = 0;
		int count_dan_status = 0;
		int count_dan_flag = 0;
		String memIdField = "";
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		if ("0000".equals(zoneId)) {
			memIdField = "mem_bnk_id";
			memberId = bankId;
		} else if ("0000".equals(branchId)) {
			memIdField = "mem_bnk_id||mem_zne_id";
			memberId = bankId + zoneId;
		}

		StringBuilder sb1 = new StringBuilder();
		sb1.append(" UPDATE DAN_CGPAN_INFO SET DCI_ALLOCATION_VALID_FLAG='Y' WHERE DAN_ID IN(");
		sb1.append(" SELECT D.DAN_ID FROM DAN_CGPAN_INFO D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb1.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014')");
		sb1.append(" AND DAN_TYPE IN('AF','SF') AND D.DAN_ID NOT IN(SELECT D.DAN_ID FROM DAN_CGPAN_INFO_TEMP D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb1.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014') ");
		sb1.append(" AND DAN_TYPE IN('AF','SF') AND "
				+ memIdField
				+ "='"
				+ memberId
				+ "')  AND (DCI_AMOUNT_RAISED - DCI_AMOUNT_CANCELLED) > 0 AND PAY_ID IS NULL  AND "
				+ memIdField + "='" + memberId + "' ");

		StringBuilder sb1rep = new StringBuilder();
		sb1rep.append(" UPDATE DAN_CGPAN_INFO@repuser SET DCI_ALLOCATION_VALID_FLAG='Y' WHERE DAN_ID IN(");
		sb1rep.append(" SELECT D.DAN_ID FROM DAN_CGPAN_INFO D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb1rep.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014')");
		sb1rep.append(" AND DAN_TYPE IN('AF','SF') AND D.DAN_ID NOT IN(SELECT D.DAN_ID FROM DAN_CGPAN_INFO_TEMP D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb1rep.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014') ");
		sb1rep.append(" AND DAN_TYPE IN('AF','SF') AND "
				+ memIdField
				+ "='"
				+ memberId
				+ "') AND (DCI_AMOUNT_RAISED - DCI_AMOUNT_CANCELLED) > 0 AND PAY_ID IS NULL AND "
				+ memIdField + "='" + memberId + "' ");

		StringBuilder sb2 = new StringBuilder();
		sb2.append(" UPDATE DEMAND_ADVICE_INFO SET DAN_STATUS='O',DAN_EXPIRY_DT=ADD_MONTHS(SYSDATE, 1) WHERE DAN_ID IN(");
		sb2.append(" SELECT D.DAN_ID FROM DAN_CGPAN_INFO D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb2.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014')");
		sb2.append(" AND DAN_TYPE IN('AF','SF') AND D.DAN_ID NOT IN(SELECT D.DAN_ID FROM DAN_CGPAN_INFO_TEMP D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb2.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014') ");
		sb2.append(" AND DAN_TYPE IN('AF','SF') AND "
				+ memIdField
				+ "='"
				+ memberId
				+ "') AND (DCI_AMOUNT_RAISED - DCI_AMOUNT_CANCELLED) > 0 AND PAY_ID IS NULL AND "
				+ memIdField + "='" + memberId + "' ");

		StringBuilder sb2rep = new StringBuilder();
		sb2rep.append(" UPDATE DEMAND_ADVICE_INFO@repuser SET DAN_STATUS='O',DAN_EXPIRY_DT=ADD_MONTHS(SYSDATE, 1) WHERE DAN_ID IN(");
		sb2rep.append(" SELECT D.DAN_ID FROM DAN_CGPAN_INFO D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb2rep.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014')");
		sb2rep.append(" AND DAN_TYPE IN('AF','SF') AND D.DAN_ID NOT IN(SELECT D.DAN_ID FROM DAN_CGPAN_INFO_TEMP D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb2rep.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014') ");
		sb2rep.append(" AND DAN_TYPE IN('AF','SF') AND "
				+ memIdField
				+ "='"
				+ memberId
				+ "') AND (DCI_AMOUNT_RAISED - DCI_AMOUNT_CANCELLED) > 0 AND PAY_ID IS NULL AND "
				+ memIdField + "='" + memberId + "' ");

		StringBuilder sb3 = new StringBuilder();
		sb3.append(" SELECT count(*) FROM DAN_CGPAN_INFO D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb3.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014')");
		sb3.append(" AND DAN_TYPE IN('AF','SF') AND D.DAN_ID NOT IN(SELECT D.DAN_ID FROM DAN_CGPAN_INFO_TEMP D,DEMAND_ADVICE_INFO DA WHERE D.DAN_ID=DA.DAN_ID ");
		sb3.append(" AND TRUNC(DAN_GENERATED_DT) IN('31-MAR-2014','07-MAY-2014','09-MAY-2014','10-MAY-2014','12-NOV-2014') ");
		sb3.append(" AND DAN_TYPE IN('AF','SF') AND "
				+ memIdField
				+ "='"
				+ memberId
				+ "') AND (DCI_AMOUNT_RAISED - DCI_AMOUNT_CANCELLED) > 0 AND PAY_ID IS NULL AND "
				+ memIdField + "='" + memberId + "' ");

		try {
			con = DBConnection.getConnection();
			stmt = con.createStatement();
			String query1 = sb1.toString();
			String query1rep = sb1rep.toString();
			String query2 = sb2.toString();
			String query2rep = sb2rep.toString();
			String query3 = sb3.toString()
					+ " AND TRUNC(DAN_EXPIRY_DT)<=SYSDATE ";
			rs = stmt.executeQuery(query3);
			if (rs.next()) {
				count_expiry_date = rs.getInt(1);
				max_count = count_expiry_date;
			}
			rs = null;
			stmt = null;
			stmt = con.createStatement();
			query3 = sb3.toString() + " AND DCI_ALLOCATION_VALID_FLAG='N' ";
			rs = stmt.executeQuery(query3);
			if (rs.next()) {
				count_dan_flag = rs.getInt(1);
				if (count_dan_flag > max_count)
					max_count = count_dan_flag;
			}
			rs = null;
			stmt = null;
			stmt = con.createStatement();
			query3 = sb3.toString() + " AND DAN_STATUS='C' ";
			rs = stmt.executeQuery(query3);
			if (rs.next()) {
				count_dan_status = rs.getInt(1);
				if (count_dan_status > max_count)
					max_count = count_dan_status;
			}
			rs = null;
			stmt = null;

			if (max_count == count_expiry_date || max_count == count_dan_status) {
				if (count_expiry_date > count_dan_status) {
					query1 = query1 + " AND TRUNC(DAN_EXPIRY_DT)<=SYSDATE )";
					query1rep = query1rep
							+ " AND TRUNC(DAN_EXPIRY_DT)<=SYSDATE )";
					query2 = query2 + " AND TRUNC(DAN_EXPIRY_DT)<=SYSDATE )";
					query2rep = query2rep
							+ " AND TRUNC(DAN_EXPIRY_DT)<=SYSDATE )";
				} else {
					query1 = query1 + " AND DAN_STATUS='C' )";
					query1rep = query1rep + " AND DAN_STATUS='C' )";
					query2 = query2 + " AND DAN_STATUS='C' )";
					query2rep = query2rep + " AND DAN_STATUS='C' )";
				}
				//System.out.println(query1);
				//System.out.println(query2);
				stmt = con.createStatement();
				stmt.execute(query1rep);
				stmt.execute(query1);
				stmt.execute(query2rep);
				stmt.execute(query2);
			} else {
				query1 = query1 + " AND DCI_ALLOCATION_VALID_FLAG='N' ";
				query1rep = query1rep + " AND DCI_ALLOCATION_VALID_FLAG='N' ";
				query2 = query2 + " AND DCI_ALLOCATION_VALID_FLAG='N' ";
				query2rep = query2rep + " AND DCI_ALLOCATION_VALID_FLAG='N' ";
				//System.out.println(query1);
				//System.out.println(query2);
				stmt = con.createStatement();
				stmt.execute(query2rep);
				stmt.execute(query2);
				stmt.execute(query1rep);
				stmt.execute(query1);
			}

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (Exception e) {
			try {
				con.rollback();
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
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return max_count;
	}
    
    
  public ArrayList getReceivedPaymentsForTenure(Date fromDate, Date toDate)
	throws DatabaseException {
PaymentList paymentList = null;
Connection connection = null;
CallableStatement getReceivedPaymentsStmt = null;
ArrayList receivedPayments = null;
ResultSet resultSet = null;

connection = DBConnection.getConnection(false);

try {
	String exception = "";

	getReceivedPaymentsStmt = connection
			.prepareCall("{?=call packGetPaymentDetails.funcGetPaymentDetailsForGF(?,?,?,?)}");
	System.out.println(" packGetPaymentDetails.funcGetPaymentDetailsForGF  ");
	getReceivedPaymentsStmt.registerOutParameter(1, Types.INTEGER);
	// getReceivedPaymentsStmt.setString(2, memId);
	if (fromDate != null || !fromDate.equals("")) {
		getReceivedPaymentsStmt.setDate(2,
				new java.sql.Date(fromDate.getTime()));
	} else {
		getReceivedPaymentsStmt.setNull(2, Types.DATE);
	}
	if (toDate != null || toDate.equals("")) {
		getReceivedPaymentsStmt.setDate(3,
				new java.sql.Date(toDate.getTime()));
	} else {
		getReceivedPaymentsStmt.setNull(3, Types.DATE);
	}
	getReceivedPaymentsStmt.registerOutParameter(4, Constants.CURSOR);
	getReceivedPaymentsStmt.registerOutParameter(5, Types.VARCHAR);
	getReceivedPaymentsStmt.execute();

	int functionReturnValue = getReceivedPaymentsStmt.getInt(1);

	if (functionReturnValue == Constants.FUNCTION_FAILURE) {

		String error = getReceivedPaymentsStmt.getString(5);

		getReceivedPaymentsStmt.close();
		getReceivedPaymentsStmt = null;

		connection.rollback();

		throw new DatabaseException(error);

	} else {

		resultSet = (ResultSet) getReceivedPaymentsStmt.getObject(4);
		exception = getReceivedPaymentsStmt.getString(5);

		// doubt dd whether each element has to be set.
		// Two parameters paymentId, instrumentNo added by sukant
		// PathInfotech on 17/01/2007
		String memberId = "";
		String memberName = "";
		String paymentId = "";
		Date instrumentDt = null;
		String instrumentNo = "";
		int payAmount = 0;
		int noOfRecords = 0;
		int allocatedAmt = 0;
		int cases = 0;
		while (resultSet.next()) {
			if (noOfRecords == 0) {
				receivedPayments = new ArrayList();
			}
			memberId = resultSet.getString(1);
			memberName = resultSet.getString(2);
			paymentId = resultSet.getString(3);
			instrumentNo = resultSet.getString(4);
			instrumentDt = resultSet.getDate(5);
			payAmount = resultSet.getInt(6);
			allocatedAmt = resultSet.getInt(7);
			cases = resultSet.getInt(8);
			paymentList = new PaymentList(memberId, memberName,
					paymentId, instrumentNo, instrumentDt, payAmount,
					allocatedAmt, cases);

			receivedPayments.add(paymentList);
			++noOfRecords;
		}

	}
	resultSet.close();
	getReceivedPaymentsStmt.close();
	getReceivedPaymentsStmt = null;

	// connection.commit();

} catch (Exception exception) {

	try {
		connection.rollback();
	} catch (SQLException ignore) {
		ignore.printStackTrace();
	}

	throw new DatabaseException(exception.getMessage());
} finally {
	DBConnection.freeConnection(connection);
}

return receivedPayments;
}
}

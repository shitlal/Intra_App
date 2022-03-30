package com.cgtsi.claim;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oracle.jdbc.OracleResultSet;

import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.Message;
import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.Mailer;
import com.cgtsi.common.MessageException;
import com.cgtsi.guaranteemaintenance.Disbursement;
import com.cgtsi.guaranteemaintenance.DisbursementAmount;
import com.cgtsi.guaranteemaintenance.GMConstants;
import com.cgtsi.guaranteemaintenance.GMProcessor;
import com.cgtsi.guaranteemaintenance.NPADetails;
import com.cgtsi.guaranteemaintenance.PeriodicInfo;
import com.cgtsi.guaranteemaintenance.Repayment;
import com.cgtsi.guaranteemaintenance.RepaymentAmount;
import com.cgtsi.investmentfund.ChequeDetails;
import com.cgtsi.investmentfund.IFDAO;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.receiptspayments.RpProcessor;
import com.cgtsi.receiptspayments.VoucherDetail;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.RegistrationDAO;
import com.cgtsi.reports.ReportDAO;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;

/**
 * This class is used to access database. All claims processing classes will
 * access this class to access database. All database related information are
 * hidden from other business processing elements.
 */
public class CPDAO {
	/**
	 * @roseuid 39B87D98005D
	 */
	public CPDAO() {

	}

	/**
	 * This method retrieves all the Member Ids from the Database
	 */
	public Vector getAllMemberIds() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllMemberIds()", "Entered!");
		Vector memberIds = new Vector();
		RegistrationDAO regDAO = new RegistrationDAO();
		ArrayList memberInfoDetails = regDAO.getAllMembers();
		if (memberInfoDetails != null) {
			for (int i = 0; i < memberInfoDetails.size(); i++) {
				MLIInfo mliInfo = (MLIInfo) memberInfoDetails.get(i);
				String bankId = mliInfo.getBankId();
				String zoneId = mliInfo.getZoneId();
				String branchId = mliInfo.getBranchId();
				String memberId = bankId + zoneId + branchId;
				if (memberId != null) {
					if (!(memberIds.contains(memberId))) {
						memberIds.add(memberId);
					}
				}
			}
		}
		if (memberIds.size() == 0) {
			Log.log(Log.ERROR, "CPDAO", "getAllMemberIds()",
					"No Member Ids in the database!");
			throw new DatabaseException("No Member Ids in the database");
		}
		Log.log(Log.INFO, "CPDAO", "getAllMemberIds()", "Exited!");
		return memberIds;
	}

	/**
	 * This method retrieves the Borrower Id for the given CGPAN.
	 */
	public String getBorowwerForCGPAN(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getBorowwerForCGPAN()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		String borrowerid = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call funcGetBIDforCGPAN(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, cgpan);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			// System.out.println("errorCode:"+errorCode);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getBorowwerForCGPAN()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the borrower id
				borrowerid = callableStmt.getString(3);
				// System.out.println("borrowerid:"+borrowerid);
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getBorowwerForCGPAN()",
					"Error retrieving Borrower for the CGPAN!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}

		if (borrowerid == null) {
			Log.log(Log.INFO, "CPDAO", "getBorowwerForCGPAN()",
					"There is no Borrower Id for the given CGPAN.");
			throw new DatabaseException(
					"Please enter correct Borrower Id and(or) CGPAN");
		}
		Log.log(Log.INFO, "CPDAO", "getBorowwerForCGPAN()", "Exited!");
		return borrowerid;
	}

	/**
	 * This method returns an ArrayList of Borrower Ids for a given member id
	 */
	public ArrayList getAllBorrowerIDs(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllBorrowerIDs()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ArrayList borrowerIds = new ArrayList();

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call packGetBIDForMember.funcGetBIDForMember(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllBorrowerIDs()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(5);

				// iterate over the resultset and extract the borrower ids.
				while (resultset.next()) {
					String borrowerid = resultset.getString(1);
					if (borrowerid != null) {
						if (!(borrowerIds.contains(borrowerid))) {
							borrowerIds.add(borrowerid);
						}
					}
				}
				resultset.close();
				resultset = null;
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getAllBorrowerIDs()",
					"Error retrieving all Borrower Ids!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		if (borrowerIds.size() == 0) {
			Log.log(Log.INFO, "CPDAO", "getAllBorrowerIDs()",
					"There are no Borrower Ids for this Member Id!");
			throw new DatabaseException(
					"There are no Borrower Ids for this Member Id!");
		}
		Log.log(Log.INFO, "CPDAO", "getAllBorrowerIDs()", "Exited!");
		return borrowerIds;
	}

	/**
	 * This method gets the Member Info Details for a given Member Id. This
	 * method returns an object of MemberInfo
	 */
	public MemberInfo getMemberInfoDetails(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getMemberInfoDetails()", "Entered!");

		// Out Parameters
		String bankName = null;
		String branchName = null;
		String zoneName = null;
		String shortName = null;
		String city = null;
		String district = null;
		String stateName = null;
		String phoneCode = null;
		String phoneNumber = null;
		String email = null;
		String mcgf = null;
		String dandelivery = null;
		String memberStatus = null;
		String memberAddress = null;

		CallableStatement callableStmt = null;
		int status = -1;
		String errorCode = null;

		Connection conn = null;
		MemberInfo memberInfo = new MemberInfo();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call funcGetDetailsForMember(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(10, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(11, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(12, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(13, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(14, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(15, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(16, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(17, java.sql.Types.VARCHAR);

			// Incorporating the changes to SP funcGetDetailsForMember
			callableStmt.registerOutParameter(18, Types.VARCHAR); // Address
			callableStmt.registerOutParameter(19, Types.VARCHAR); // Pincode
			callableStmt.registerOutParameter(20, Types.VARCHAR); // Fax code
			callableStmt.registerOutParameter(21, Types.VARCHAR); // Fax number
			callableStmt.registerOutParameter(22, Types.VARCHAR); // ReportingZone

			callableStmt.registerOutParameter(23, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(23);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllBorrowerIDs()",
						"SP returns a 1. Error code is :" + errorCode);

				// Closing the callable statement
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the values from the Stored Procedure
				bankName = callableStmt.getString(5);
				branchName = callableStmt.getString(6);
				zoneName = callableStmt.getString(7);
				shortName = callableStmt.getString(8);
				city = callableStmt.getString(9);
				district = callableStmt.getString(10);
				stateName = callableStmt.getString(11);
				phoneCode = callableStmt.getString(12);
				phoneNumber = callableStmt.getString(13);
				email = callableStmt.getString(14);
				mcgf = callableStmt.getString(15);
				dandelivery = callableStmt.getString(16);
				memberStatus = callableStmt.getString(17);
				memberAddress = callableStmt.getString(18);

				memberInfo.setMemberBankName(bankName);
				memberInfo.setMemberBranchName(branchName);
				memberInfo.setCity(city);
				memberInfo.setDistrict(district);
				memberInfo.setState(stateName);
				memberInfo.setPhoneCode(phoneCode);
				memberInfo.setTelephone(phoneNumber);
				memberInfo.setMemberId(bankId + zoneId + branchId);
				memberInfo.setEmail(email);
				memberInfo.setMemberAddress(memberAddress);
				// Closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getMemberInfoDetails()",
					"Error retrieving member info details for the member Id!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getMemberInfoDetails()", "Exited!");
		return memberInfo;
	}

	/*
	 * This method retrieves the Borrower Details for a given Borrower Id. The
	 * method returns an object of BorrowerInfo
	 */

	public BorrowerInfo getBorrowerDetails(String borrowerId)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getBorrowerDetails()", "Entered!");
		String borrowerName = null;
		String address = null;
		String cityName = null;
		String districtName = null;
		String stateName = null;
		String pinCode = null;
		String activity = null;
		CallableStatement callableStmt = null;
		int status = -1;
		String errorCode = null;

		Connection conn = null;
		BorrowerInfo borrowerInfo = new BorrowerInfo();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call funcGetDetailsForBorrower(?,?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.CHAR);
			callableStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(10, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(10);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getBorrowerDetails()",
						"SP returns a 1. Error code is :" + errorCode);

				// Closing the callable statement
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the values from the Stored Procedure
				borrowerName = callableStmt.getString(3);
				address = callableStmt.getString(4);
				cityName = callableStmt.getString(5);
				districtName = callableStmt.getString(6);
				stateName = callableStmt.getString(7);
				pinCode = callableStmt.getString(8);
				activity = callableStmt.getString(9);
				// Setting the values in the borrower info object
				borrowerInfo.setBorrowerName(borrowerName);
				borrowerInfo.setAddress(address);
				borrowerInfo.setCity(cityName);
				borrowerInfo.setDistrict(districtName);
				borrowerInfo.setState(stateName);
				borrowerInfo.setPinCode(pinCode);
				borrowerInfo.setActivity(activity);
				// Closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getBorrowerDetails()",
					"Error retrieving borrower details for the borrower Id!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getBorrowerDetails()", "Exited!");
		return borrowerInfo;
	}

	/**
	 * This method is used to check whether NPA details are available in the
	 * database or not. The method stores the NPA Details in a HashMap.
	 */
	public HashMap isNPADetailsAvailable(String cgbid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "isNPADetailsAvailable()", "Entered!");
		Log.log(Log.INFO, "CPDAO", "isNPADetailsAvailable()", "Borrower Id :"
				+ cgbid);
		CallableStatement callableStmt = null;
		Connection conn = null;
		HashMap npadetails = new HashMap();
		ResultSet resultset = null;
		HashMap npadetail = null;

		int status = -1;
		String errorCode = null;
		
		
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetNPADetails.funcGetNPADetailForBID(?,?,?,?)}");		
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, cgbid);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);			
			callableStmt.execute();			
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);			
			
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "isNPADetailsAvailable()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {				
				// Retrieving the resultset from the intranet db				
				resultset = (ResultSet) callableStmt.getObject(3);				
				
				java.util.Date npaEffectiveDt = null;
				java.util.Date cgtsiReportingDt = null;
				String reasonForNPA = null;
				String willfulDefaulter = null;
				String whetherWrittenOff = null;
				java.util.Date writtenOffDate = null;
				java.util.Date recConclusionDt = null;
				String npaId = null;
				java.util.Date npaCreatedDate = null;
				// Reading the resultset
				while (resultset.next()) {
					npadetail = new HashMap();
					npaEffectiveDt = resultset.getDate(1);
					
					cgtsiReportingDt = resultset.getDate(2);
					
					reasonForNPA = resultset.getString(3);
					
					willfulDefaulter = resultset.getString(4);
					
					whetherWrittenOff = resultset.getString(5);
					
					writtenOffDate = resultset.getDate(6);
					
					recConclusionDt = resultset.getDate(7);
					
					npaId = String.valueOf(resultset.getInt(8));
					
					npaCreatedDate = resultset.getDate(9);
					

					// Populating the npadetails HashMap
					npadetail.put(ClaimConstants.NPA_CLASSIFIED_DT,	npaEffectiveDt);
					npadetail.put(ClaimConstants.NPA_REPORTING_DT, cgtsiReportingDt);
					npadetail.put(ClaimConstants.REASONS_FOR_TURNING_NPA, reasonForNPA);
					npadetail.put(ClaimConstants.WILLFUL_DEFAULTER, willfulDefaulter);
					npadetail.put(ClaimConstants.WHETHER_NPA_WRITTEN_OFF, whetherWrittenOff);
					npadetail.put(ClaimConstants.NPA_WRITTEN_OFF_DATE, writtenOffDate);
					npadetail.put(ClaimConstants.NPA_REC_CONCLUSION_DT, recConclusionDt);
					npadetail.put("npaId", npaId);
					npadetail.put("npaCreatedDate", npaCreatedDate);
					
					// Adding the hashmap to the vector
					if (npadetail != null) {
						npadetails.put(ClaimConstants.CLM_MAIN_TABLE, npadetail);
					
					}
				}

				resultset.close();
				resultset = null;
				// Intializing the variables
				npadetail = null;
				npaEffectiveDt = null;
				cgtsiReportingDt = null;
				reasonForNPA = null;
				willfulDefaulter = null;
				whetherWrittenOff = null;
				writtenOffDate = null;

				// Retrieving the resultset from the temp db
				
				resultset = (ResultSet) callableStmt.getObject(4);
				

				// Reading the resultset
				while (resultset.next()) {
					npadetail = new HashMap();
					npaEffectiveDt = resultset.getDate(1);
				
					cgtsiReportingDt = resultset.getDate(2);
				
					reasonForNPA = resultset.getString(3);
				
					willfulDefaulter = resultset.getString(4);
				
					whetherWrittenOff = resultset.getString(5);
				
					writtenOffDate = resultset.getDate(6);
				
					recConclusionDt = resultset.getDate(7);
				
					npaId = String.valueOf(resultset.getInt(8));
				
					npaCreatedDate = resultset.getDate(9);
				
					
					// Populating the npadetails HashMap
					npadetail.put(ClaimConstants.NPA_CLASSIFIED_DT, npaEffectiveDt);
					npadetail.put(ClaimConstants.NPA_REPORTING_DT, cgtsiReportingDt);
					npadetail.put(ClaimConstants.REASONS_FOR_TURNING_NPA, reasonForNPA);
					npadetail.put(ClaimConstants.WILLFUL_DEFAULTER, willfulDefaulter);
					npadetail.put(ClaimConstants.WHETHER_NPA_WRITTEN_OFF, whetherWrittenOff);
					npadetail.put(ClaimConstants.NPA_WRITTEN_OFF_DATE, writtenOffDate);
					npadetail.put(ClaimConstants.NPA_REC_CONCLUSION_DT, recConclusionDt);
					npadetail.put("npaId", npaId);
					npadetail.put("npaCreatedDate", npaCreatedDate);
				
					// Adding the hashmap to the vector
					if (npadetail != null) {
						npadetails.put(ClaimConstants.CLM_TEMP_TABLE, npadetail);
				
					}
				}
				
				resultset.close();
				resultset = null;
				callableStmt.close();
				callableStmt = null;
				
			}
		} catch (SQLException sqlexception) {
			
			Log.log(Log.ERROR, "CPDAO", "isNPADetailsAvailable()",
					"Error retrieving NPA details for the borrower Id!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "isNPADetailsAvailable()", "Exited!");		
		return npadetails;
	}

	/*
	 * This method retrieves the OTS Details for a given Borrower Id. The OTS
	 * Details are stored * in a HashMap and the HashMap is returned.
	 */

	public Vector getOTSDetails(String bid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getOTSDetails()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		HashMap otsdetails = new HashMap();
		ResultSet resultset = null;
		Vector otsdetailsvec = new Vector();

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetOTSDetail.funcGetOTSDetail(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bid);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getOTSDetails()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Retrieving the resultset from the intranet db
				resultset = (ResultSet) callableStmt.getObject(3);

				String reasonforots = null;
				String willfuldefaulter = null;
				double totalBorrowerProposedAmnt = 0.0;
				double totalProposedSacrificeAmnt = 0.0;
				double totalOSAmnt = 0.0;
				java.util.Date otsReqDate = null;

				// Reading the resultset
				while (resultset.next()) {
					reasonforots = resultset.getString(2);
					willfuldefaulter = resultset.getString(3);
					otsReqDate = resultset.getDate(4);
					totalBorrowerProposedAmnt = resultset.getDouble(5);
					totalProposedSacrificeAmnt = resultset.getDouble(6);
					totalOSAmnt = resultset.getDouble(7);

					// Populating the otsdetails HashMap
					otsdetails.put(ClaimConstants.CLM_OTS_REASON_FOR_OTS,
							reasonforots);
					otsdetails.put(ClaimConstants.CLM_OTS_WILLFUL_DEFAULTER,
							willfuldefaulter);
					otsdetails.put(ClaimConstants.CLM_OTS_REQUEST_DATE,
							otsReqDate);
					otsdetails
							.put(ClaimConstants.CLM_OTS_TOTAL_BORROWER_PROPOSED_AMNT,
									new Double(totalBorrowerProposedAmnt));
					otsdetails.put(
							ClaimConstants.CLM_OTS_TOTAL_PROPOSED_SCRFCE_AMNT,
							new Double(totalProposedSacrificeAmnt));
					otsdetails.put(ClaimConstants.CLM_OTS_TOTAL_OS_AMNT,
							new Double(totalOSAmnt));

					// Adding the hashmap to the vector
					if (otsdetails != null) {
						otsdetailsvec.addElement(otsdetails);
					}
				}

				resultset.close();
				resultset = null;

				// Intializing the variables
				reasonforots = null;
				willfuldefaulter = null;
				otsReqDate = null;
				totalBorrowerProposedAmnt = 0.0;
				totalProposedSacrificeAmnt = 0.0;
				totalOSAmnt = 0.0;

				// Retrieving the resultset from the temp db
				resultset = (ResultSet) callableStmt.getObject(4);

				// Reading the resultset
				while (resultset.next()) {
					reasonforots = resultset.getString(2);
					willfuldefaulter = resultset.getString(3);
					otsReqDate = resultset.getDate(4);
					totalBorrowerProposedAmnt = resultset.getDouble(5);
					totalProposedSacrificeAmnt = resultset.getDouble(6);
					totalOSAmnt = resultset.getDouble(7);

					// Populating the otsdetails HashMap
					otsdetails.put(ClaimConstants.CLM_OTS_REASON_FOR_OTS,
							reasonforots);
					otsdetails.put(ClaimConstants.CLM_OTS_WILLFUL_DEFAULTER,
							willfuldefaulter);
					otsdetails.put(ClaimConstants.CLM_OTS_REQUEST_DATE,
							otsReqDate);
					otsdetails
							.put(ClaimConstants.CLM_OTS_TOTAL_BORROWER_PROPOSED_AMNT,
									new Double(totalBorrowerProposedAmnt));
					otsdetails.put(
							ClaimConstants.CLM_OTS_TOTAL_PROPOSED_SCRFCE_AMNT,
							new Double(totalProposedSacrificeAmnt));
					otsdetails.put(ClaimConstants.CLM_OTS_TOTAL_OS_AMNT,
							new Double(totalOSAmnt));

					// Adding the hashmap to the vector
					if (otsdetails != null) {
						if (!otsdetailsvec.contains(otsdetails)) {
							otsdetailsvec.addElement(otsdetails);
						}
					}
				}
				resultset.close();
				resultset = null;
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getOTSDetails()",
					"Error retrieving OTS details for the borrower Id!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getOTSDetails()", "Exited!");
		return otsdetailsvec;
	}

	/*
	 * This method returns a vector of hashmaps. Each hashtable contains
	 * recovery details pertaining to the borrower.
	 */

	public HashMap isRecoveryDetailsAvailable(String clmRefNum)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "isRecoveryDetailsAvailable()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		HashMap recoveryDetail = null;
		Vector recoveryDetails = null;
		HashMap totalRecDetails = new HashMap();

		/*
		 * double recoveredAmount = 0.0; java.util.Date dateOfRecovery = null;
		 * double legalCharges = 0.0; String isRecoveryThruOTS = null; String
		 * isRecoveryThruSaleOfAssets = null; String dtlsOfAssetsSold = null;
		 * String remarks = null; String recoveryId = null;
		 */
		String cgpan = null;
		double tcPrincipalAmt = 0.0;
		double tcInterestAmt = 0.0;
		double wcAmount = 0.0;
		double wcOtherAmount = 0.0;

		int status = -1;
		String errorCode = null;
		String modeOfRecovery = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClaimRecoveryDtls.funcGetClaimRecoveryDtls(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, clmRefNum);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "isRecoveryDetailsAvailable()",
						"SP returns a 1. Error code is :" + errorCode);

				// closing the callable statement
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(3);
				recoveryDetails = new Vector();
				while (resultset.next()) {
					recoveryDetail = new HashMap();
					cgpan = resultset.getString(1);
					tcPrincipalAmt = resultset.getDouble(2);
					tcInterestAmt = resultset.getDouble(3);
					wcAmount = resultset.getDouble(4);
					wcOtherAmount = resultset.getDouble(5);
					modeOfRecovery = resultset.getString(6);

					// Storing the recovery details in a hashmap
					recoveryDetail.put(ClaimConstants.CLM_CGPAN, cgpan);
					recoveryDetail.put(ClaimConstants.CLM_REC_TC_PRINCIPAL,
							new Double(tcPrincipalAmt));
					// System.out.println("Line number:773-clmRefNum:"+clmRefNum);
					// System.out.println("cgpan:"+cgpan);
					// System.out.println("Line number:-775 tcPrincipalAmt:"+tcPrincipalAmt);
					// System.out.println("Line number:-776 tcInterestAmt:"+tcInterestAmt);
					// System.out.println("Line number:-777 wcAmount:"+wcAmount);
					// System.out.println("Line number:-778 wcOtherAmount:"+wcOtherAmount);
					recoveryDetail.put(ClaimConstants.CLM_REC_TC_INTEREST,
							new Double(tcInterestAmt));
					recoveryDetail.put(ClaimConstants.CLM_REC_WC_AMOUNT,
							new Double(wcAmount));
					recoveryDetail.put(ClaimConstants.CLM_REC_WC_OTHER,
							new Double(wcOtherAmount));
					recoveryDetail.put(ClaimConstants.CLM_REC_MODE,
							modeOfRecovery);

					// Storing the hashmap in a vector
					if (!recoveryDetails.contains(recoveryDetail)) {
						recoveryDetails.addElement(recoveryDetail);
					}
				}
				totalRecDetails.put(ClaimConstants.CLM_TEMP_TABLE,
						recoveryDetails);
				resultset.close();
				resultset = null;

				// Retrieving the recovery details from the main table
				resultset = (ResultSet) callableStmt.getObject(4);
				recoveryDetails = new Vector();
				while (resultset.next()) {
					recoveryDetail = new HashMap();
					cgpan = resultset.getString(1);
					tcPrincipalAmt = resultset.getDouble(2);
					tcInterestAmt = resultset.getDouble(3);
					wcAmount = resultset.getDouble(4);
					wcOtherAmount = resultset.getDouble(5);
					modeOfRecovery = resultset.getString(6);

					// Storing the recovery details in a hashmap
					recoveryDetail.put(ClaimConstants.CLM_CGPAN, cgpan);
					recoveryDetail.put(ClaimConstants.CLM_REC_TC_PRINCIPAL,
							new Double(tcPrincipalAmt));
					recoveryDetail.put(ClaimConstants.CLM_REC_TC_INTEREST,
							new Double(tcInterestAmt));
					recoveryDetail.put(ClaimConstants.CLM_REC_WC_AMOUNT,
							new Double(wcAmount));
					recoveryDetail.put(ClaimConstants.CLM_REC_WC_OTHER,
							new Double(wcOtherAmount));
					recoveryDetail.put(ClaimConstants.CLM_REC_MODE,
							modeOfRecovery);

					// Storing the hashmap in a vector
					if (!recoveryDetails.contains(recoveryDetail)) {
						recoveryDetails.addElement(recoveryDetail);
					}
				}
				totalRecDetails.put(ClaimConstants.CLM_MAIN_TABLE,
						recoveryDetails);

				// closing the resultset
				resultset.close();
				resultset = null;

				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "isRecoveryDetailsAvailable()",
					"Error retrieving Recovery details for the borrower Id!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "isRecoveryDetailsAvailable()", "Exited!");
		return totalRecDetails;
	}

	public Hashtable isDisbursementDetailsAvl(String borrowerId)
			throws DatabaseException {
		Connection conn;
		Vector cgpans;
		Log.log(4, "CPDAO", "isDisbursementDetailsAvl()", "Entered!");
		CallableStatement callableStmt = null;
		ResultSet resultset = null;
		conn = null;
		cgpans = new Vector();
		int status = -1;
		String errorCode = null;
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetDtlsforDBR.funcGetDtlsForBid(?,?,?)}");
			callableStmt.registerOutParameter(1, 4);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, -10);
			callableStmt.registerOutParameter(4, 12);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == 1) {
				Log.log(2,
						"CPDAO",
						"isDisbursementDetailsAvl()",
						(new StringBuilder())
								.append("SP returns a 1. Error code is :")
								.append(errorCode).toString());
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			if (status == 0) {
				// ResultSet resultset;
				for (resultset = (ResultSet) callableStmt.getObject(3); resultset
						.next();) {
					String bid = null;
					String cgpan = null;
					String schemeName = null;
					String unitName = null;
					double tcSanctionedAmnt = 0.0D;
					bid = resultset.getString(1);
					cgpan = resultset.getString(2);
					schemeName = resultset.getString(3);
					unitName = resultset.getString(4);
					tcSanctionedAmnt = resultset.getDouble(5);
					if (cgpan != null && !cgpans.contains(cgpan))
						cgpans.addElement(cgpan);
				}

				resultset.close();
				resultset = null;
				callableStmt.close();
			}
		} catch (SQLException sqlexception) {
			Log.log(2, "CPDAO", "isDisbursementDetailsAvl()",
					"Error retrieving Disbursement Details for Borrower!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {

			DBConnection.freeConnection(conn);
		}
		Hashtable disbursementStatuses = new Hashtable();
		try {
			conn = DBConnection.getConnection();
			boolean isFinal = false;
			String disbursementFlag = null;
			if (cgpans != null) {
				for (int i = 0; i < cgpans.size(); i++) {
					String cgpan = (String) cgpans.elementAt(i);
					if (cgpan != null)
						if ("TC".equals(cgpan.substring(cgpan.length() - 2))) {
							callableStmt = conn
									.prepareCall("{? = call packGetPIDBRDtlsCGPAN.funcDBRDetailsForCGPAN(?,?,?)}");
							callableStmt.registerOutParameter(1, 4);
							callableStmt.setString(2, cgpan);
							callableStmt.registerOutParameter(3, -10);
							callableStmt.registerOutParameter(4, 12);
							callableStmt.execute();
							status = callableStmt.getInt(1);
							errorCode = callableStmt.getString(4);
							if (status == 1) {
								Log.log(2,
										"CPDAO",
										"isDisbursementDetailsAvl()",
										(new StringBuilder())
												.append("SP returns a 1. Error code is :")
												.append(errorCode).toString());
								callableStmt.close();
								throw new DatabaseException(errorCode);
							}
							if (status == 0) {
								// ResultSet resultset;
								for (resultset = (ResultSet) callableStmt
										.getObject(3); resultset.next();) {
									disbursementFlag = resultset.getString(4);
									if (disbursementFlag != null)
										disbursementFlag = disbursementFlag
												.trim();
									if ("Y".equals(disbursementFlag))
										isFinal = true;
								}

								if (isFinal)
									disbursementStatuses.put(cgpan, "Y");
								else
									disbursementStatuses.put(cgpan, "N");
								resultset.close();
								resultset = null;
								callableStmt.close();
							}
						} else {
							disbursementStatuses.put(cgpan, "N");
						}
				}

			}
		} catch (SQLException sqlexception) {
			Log.log(2, "CPDAO", "isDisbursementDetailsAvl()",
					"Error retrieving Disbursement Details for each cgpan for the Borrower!");
			throw new DatabaseException(sqlexception.getMessage());
		}

		DBConnection.freeConnection(conn);
		Log.log(4, "CPDAO", "isDisbursementDetailsAvl()", (new StringBuilder())
				.append("Disbursement Details :").append(disbursementStatuses)
				.toString());
		Log.log(4, "CPDAO", "isDisbursementDetailsAvl()", "Exited!");
		return disbursementStatuses;
	}

	public LegalProceedingsDetail isLegalProceedingsDetailAvl(String borrowerId)
	throws DatabaseException {
Log.log(Log.INFO, "CPDAO", "isLegalProceedingsDetailAvl()", "Entered!");
CallableStatement callableStmt = null;
Connection conn = null;
LegalProceedingsDetail legalProceedingsDetail = new LegalProceedingsDetail();

String legalForum = null;
String registrationSuitNumber = null;
java.util.Date filingDate = null;
String forumName = null;
String location = null;
double amount = 0.0;
String statusRemarks = null;
String recvryProceedingsCompleteFlag = null;

String commHeadedByOfficerOrAboveDb = null;

int status = -1;
String errorCode = null;
DecimalFormat decimalFormat = new DecimalFormat("##########.00##");
try {
	conn = DBConnection.getConnection();
	callableStmt = conn
			.prepareCall("{? = call packGetInsUpdLegalDetail.funcGetLegalDtlForBID(?,?,?,?,?,?,?,?,?,?,?)}");
	callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
	callableStmt.setString(2, borrowerId);
	callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(5, java.sql.Types.DATE);
	callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(8, java.sql.Types.DOUBLE);
	callableStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(10, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(11, java.sql.Types.VARCHAR);
	callableStmt.registerOutParameter(12, java.sql.Types.VARCHAR);

	callableStmt.execute();
	status = callableStmt.getInt(1);
	errorCode = callableStmt.getString(12);

	if (status == Constants.FUNCTION_FAILURE) {
		Log.log(Log.ERROR, "CPDAO", "isLegalProceedingsDetailAvl()",
				"SP returns a 1. Error code is :" + errorCode);

		// closing the callable statement
		callableStmt.close();
		throw new DatabaseException(errorCode);
	} else if (status == Constants.FUNCTION_SUCCESS) {
		// Retrieving the details from the callable statement
		legalForum = callableStmt.getString(3);
		registrationSuitNumber = (String) callableStmt.getString(4);
		filingDate = callableStmt.getDate(5);

		forumName = callableStmt.getString(6);
		location = callableStmt.getString(7);
		amount = callableStmt.getDouble(8);
		statusRemarks = callableStmt.getString(9);
		recvryProceedingsCompleteFlag = callableStmt.getString(10);
		
		commHeadedByOfficerOrAboveDb = callableStmt.getString(11);
		// System.out.println("recvryProceedingsCompleteFlag :" +
		// recvryProceedingsCompleteFlag);
		errorCode = callableStmt.getString(12);

		// Setting the values in the LegalProceedingsDetail
		legalProceedingsDetail
				.setForumRecoveryProceedingsInitiated(legalForum);
		legalProceedingsDetail
				.setSuitCaseRegNumber(registrationSuitNumber);
		legalProceedingsDetail.setFilingDate(filingDate);
		legalProceedingsDetail.setNameOfForum(forumName);
		legalProceedingsDetail.setLocation(location);
		legalProceedingsDetail.setAmountClaimed(Double
				.parseDouble(decimalFormat.format(amount)));
		legalProceedingsDetail.setCurrentStatusRemarks(statusRemarks);
		legalProceedingsDetail
				.setIsRecoveryProceedingsConcluded(recvryProceedingsCompleteFlag);
		legalProceedingsDetail.setCommHeadedByOfficerOrAbove(commHeadedByOfficerOrAboveDb);

		// closing the callable statement
		callableStmt.close();
		callableStmt = null;
	}
} catch (SQLException sqlexception) {
	Log.log(Log.ERROR, "CPDAO", "isLegalProceedingsDetailAvl()",
			"Error retrieving Legal Details for the Borrower!");
	throw new DatabaseException(sqlexception.getMessage());
} finally {
	DBConnection.freeConnection(conn);
}
Log.log(Log.INFO, "CPDAO", "isLegalProceedingsDetailAvl()", "Exited!");
return legalProceedingsDetail;
}
	/*
	 * The vector is a collection of HashMaps. Each hashmap contains details
	 * about each CGPAN as key-value pairs. This method will encapsulate all the
	 * cgpans for a borrower. The hashmap also contains approved amount and
	 * enhanced approved amount. Effectively, for each cgpan, we can get the
	 * approved amount and enhanced approved amount.
	 */

	public Vector getCGPANDetailsForBorrowerId(String borrowerId,
			String memberId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getCGPANDetailsForBorrowerId()", "Entered!");
		ResultSet rs = null;
		HashMap cgpandetails = null;
		Vector allcgpandetails = new Vector();
		ApplicationDAO appDAO = new ApplicationDAO();
		Application application = null;

		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetCGPANForBorrower.funcGetCGPANForBorrower(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getCGPANDetailsForBorrowerId()",
						"SP returns a 1. Error code is :" + errorCode);

				// closing the callable statement
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the resultset from the callable statement
				rs = (ResultSet) callableStmt.getObject(3);
				while (rs.next()) {
					String cgpan = null;
					double approvedAmount = 0.0;
					double enhancedApprovedAmount = 0.0;
					double reapproveAmount = 0.0;
					String appRefNum = null;
					String loantype = null;
					java.util.Date guarStartDt = null;
					String applicationStatus = null;

					// reading from the resultset
					cgpan = rs.getString(1);
					if (cgpan == null) {
						continue;
					}
					appRefNum = getAppRefNumber(cgpan);
					application = appDAO.getAppForAppRef(null, appRefNum);
					reapproveAmount = application.getReapprovedAmount();
					approvedAmount = rs.getDouble(2);
					enhancedApprovedAmount = rs.getDouble(3);
					loantype = rs.getString(4);
					guarStartDt = (java.util.Date) rs.getDate(5);
					applicationStatus = (String) rs.getString(6);

					// Populating the hashmap. Each hashmap will have info about
					// one cgpan.
					if (cgpan != null) {
						cgpandetails = new HashMap();
						cgpandetails.put(ClaimConstants.CLM_CGPAN, cgpan);
						if (reapproveAmount == 0.0) {
							cgpandetails.put(ClaimConstants.CGPAN_APPRVD_AMNT,
									new Double(approvedAmount));
						}
						if (reapproveAmount > 0.0) {
							cgpandetails.put(ClaimConstants.CGPAN_APPRVD_AMNT,
									new Double(reapproveAmount));
						}
						cgpandetails.put(
								ClaimConstants.CGPAN_ENHANCED_APPRVD_AMNT,
								new Double(enhancedApprovedAmount));
						cgpandetails.put(ClaimConstants.CGPAN_LOAN_TYPE,
								loantype);
						cgpandetails.put(ClaimConstants.CLM_GUARANTEE_START_DT,
								guarStartDt);
						cgpandetails.put(ClaimConstants.APPLICATION_STATUS,
								applicationStatus);
						allcgpandetails.add(cgpandetails);
					}
				}

				// closing the resultset
				rs.close();
				rs = null;

				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getCGPANDetailsForBorrowerId()",
					"Error retrieving CGPAN Details for the Borrower!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}

		Vector clmsFiled = getAllClaimsFiled();
		String clmRefNumber = null;
		for (int k = 0; k < clmsFiled.size(); k++) {
			HashMap mp = (HashMap) clmsFiled.elementAt(k);
			if (mp == null) {
				continue;
			}
			String mpMemberId = (String) mp.get(ClaimConstants.CLM_MEMBER_ID);
			String mpbid = (String) mp.get(ClaimConstants.CLM_BORROWER_ID);
			if (mpMemberId == null) {
				continue;
			}
			if (mpbid == null) {
				continue;
			}
			if ((mpMemberId.equals(memberId)) && (mpbid.equals(borrowerId))) {
				clmRefNumber = (String) mp
						.get(ClaimConstants.CLM_CLAIM_REF_NUMBER);
			}
		}

		// GMProcessor gmProcessor = new GMProcessor();
		int type = 0;
		ArrayList disbursementDetails = null;
		// ArrayList repaymentDetails =
		// gmProcessor.viewRepaymentDetails(borrowerId,type);;
		ArrayList repaymentDetails = null;
		ArrayList disbDtls = null;
		ArrayList nestedDtls = null;
		PeriodicInfo pi = null;
		if (clmRefNumber == null) {
			disbursementDetails = getDisbursementDetails(borrowerId, type);
			if (disbursementDetails != null) {
				if (disbursementDetails.size() > 0) {
					Log.log(Log.INFO, "CPDAO",
							"getCGPANDetailsForBorrowerId()",
							"Size of disbursementDetails Dtls :"
									+ disbursementDetails.size());
					for (int i = 0; i < disbursementDetails.size(); i++) {
						pi = (PeriodicInfo) disbursementDetails.get(i);
						if (pi != null) {
							disbDtls = pi.getDisbursementDetails();
							if (disbDtls != null) {
								Log.log(Log.INFO,
										"CPDAO",
										"getCGPANDetailsForBorrowerId()",
										"Size of disbDtls Dtls :"
												+ disbDtls.size());
								if (disbDtls.size() > 0) {
									for (int j = 0; j < disbDtls.size(); j++) {
										Disbursement dsbrsmnt = (Disbursement) disbDtls
												.get(j);
										if (dsbrsmnt != null) {
											nestedDtls = dsbrsmnt
													.getDisbursementAmounts();
											if ((nestedDtls == null)
													|| (nestedDtls.size() == 0)) {
												continue;
											}
											String lastcgpan = null;
											java.util.Date lastDsbrsmntDt = null;
											java.util.Date presentDsbrsmntDt = null;
											Log.log(Log.INFO,
													"CPDAO",
													"getCGPANDetailsForBorrowerId()",
													"Size of Nested Dtls :"
															+ nestedDtls.size());
											for (int k = 0; k < nestedDtls
													.size(); k++) {
												DisbursementAmount dbamnt = (DisbursementAmount) nestedDtls
														.get(k);
												if (dbamnt == null) {
													continue;
												}

												String cgpan = dbamnt
														.getCgpan();
												Log.log(Log.INFO,
														"CPDAO",
														"getCGPANDetailsForBorrowerId()",
														"cgpan :" + cgpan);
												// System.out.println("CGPAN :"
												// + cgpan);
												presentDsbrsmntDt = dbamnt
														.getDisbursementDate();
												Log.log(Log.INFO,
														"CPDAO",
														"getCGPANDetailsForBorrowerId()",
														"presentDsbrsmntDt :"
																+ presentDsbrsmntDt);
												if (k == 0) {
													lastcgpan = cgpan;
													lastDsbrsmntDt = presentDsbrsmntDt;
												}
												if (k > 0) {
													if (cgpan.equals(lastcgpan)) {
														if (presentDsbrsmntDt != null) {
															// System.out.println("presentDsbrsmntDt :"
															// +
															// presentDsbrsmntDt);
															// System.out.println("lastDsbrsmntDt :"
															// +
															// lastDsbrsmntDt);
															if ((presentDsbrsmntDt
																	.compareTo(lastDsbrsmntDt)) > 0) {
																lastDsbrsmntDt = presentDsbrsmntDt;
															}
															if ((presentDsbrsmntDt
																	.compareTo(lastDsbrsmntDt)) == 0) {
																lastDsbrsmntDt = presentDsbrsmntDt;
															}
															// lastcgpan =
															// cgpan;
															// continue;
														}
													} else {
														// System.out.println("CGPAN :"
														// + dbamnt.getCgpan());
														// System.out.println("Disbursement Date :"
														// + lastDsbrsmntDt);
														// lastDsbrsmntDt =
														// presentDsbrsmntDt;
														// lastcgpan = cgpan;
														continue;
													}
												}

												// System.out.println("1 - CGPAN :"
												// + lastcgpan);
												// System.out.println("Disbursement Date :"
												// + lastDsbrsmntDt);
												HashMap hashmap = null;
												Log.log(Log.INFO,
														"CPDAO",
														"getCGPANDetailsForBorrowerId()",
														"allcgpandetails size :"
																+ allcgpandetails
																		.size());
												for (int m = 0; m < allcgpandetails
														.size(); m++) {
													hashmap = (HashMap) allcgpandetails
															.elementAt(m);
													Log.log(Log.INFO,
															"CPDAO",
															"getCGPANDetailsForBorrowerId()",
															"priting hasmap in allcgpandetails vector :"
																	+ hashmap);
													if (hashmap != null) {
														if (hashmap
																.containsKey(ClaimConstants.CLM_CGPAN)) {
															String cgpn = (String) hashmap
																	.get(ClaimConstants.CLM_CGPAN);
															// System.out.println("CGPAN from Inner FOR Loop :"
															// + cgpn);
															if ((cgpn != null)
																	&& (!(cgpn
																			.equals("")))) {
																if (cgpn.equals(lastcgpan)) {
																	hashmap = (HashMap) allcgpandetails
																			.remove(m);
																	hashmap.put(
																			ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
																			lastDsbrsmntDt);
																	allcgpandetails
																			.add(m,
																					hashmap);
																}
															}
														}
													}
													// System.out.println("Printing HashMap :"
													// + hashmap);
												}

											}
										}
									}
								}
							}
						}
					}
				}
			}
			disbDtls = null;
			nestedDtls = null;
		}
		if (clmRefNumber != null) {
			Vector tcDetails = null;
			ClaimDetail cd = getDetailsForClaimRefNumber(clmRefNumber);
			if (cd != null) {
				tcDetails = cd.getTcDetails();

				if (tcDetails != null) {
					for (int i = 0; i < tcDetails.size(); i++) {
						HashMap map = (HashMap) tcDetails.elementAt(i);
						if (map == null) {
							continue;
						}

						String pan = (String) map.get(ClaimConstants.CLM_CGPAN);
						java.util.Date dsbrsDate = (java.util.Date) map
								.get(ClaimConstants.CLM_LAST_DISBURSEMENT_DT);
						HashMap hashmap = null;
						for (int m = 0; m < allcgpandetails.size(); m++) {
							hashmap = (HashMap) allcgpandetails.elementAt(m);
							Log.log(Log.INFO, "CPDAO",
									"getCGPANDetailsForBorrowerId()",
									"priting hasmap in allcgpandetails vector :"
											+ hashmap);
							if (hashmap == null) {
								continue;
							}
							if (hashmap.containsKey(ClaimConstants.CLM_CGPAN)) {
								String cgpn = (String) hashmap
										.get(ClaimConstants.CLM_CGPAN);
								// System.out.println("CGPAN from Inner FOR Loop :"
								// + cgpn);
								if ((cgpn != null) && (!(cgpn.equals("")))) {
									if (cgpn.equals(pan)) {
										hashmap = (HashMap) allcgpandetails
												.remove(m);
										hashmap.put(
												ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
												dsbrsDate);
										allcgpandetails.add(m, hashmap);
									}
								}
							}
						}
					}
				}
			}
		}

		// Fetching the Repayment Details for the CGPAN(s)
		type = 1;
		HashMap hashmap = null;
		ArrayList repayments = null;
		ArrayList repayAmounts = null;
		Repayment repayment = null;
		RepaymentAmount repayamntAmnt = null;
		for (int m = 0; m < allcgpandetails.size(); m++) {
			hashmap = (HashMap) allcgpandetails.elementAt(m);
			if (hashmap != null) {
				if (hashmap.containsKey(ClaimConstants.CLM_CGPAN)) {
					String cgpn = (String) hashmap
							.get(ClaimConstants.CLM_CGPAN);
					if ((cgpn != null) && (!(cgpn.equals("")))) {
						repaymentDetails = getCPRepaymentDetails(cgpn, type);
						for (int i = 0; i < repaymentDetails.size(); i++) {
							pi = (PeriodicInfo) repaymentDetails.get(i);
							if (pi == null) {
								continue;
							}
							repayments = pi.getRepaymentDetails();
							if ((repayments == null)
									|| (repayments.size() == 0)) {
								continue;
							}
							double totalRepaidAmnt = 0.0;
							for (int j = 0; j < repayments.size(); j++) {
								repayment = (Repayment) repayments.get(j);
								if (repayment == null) {
									continue;
								}
								repayAmounts = repayment.getRepaymentAmounts();
								if ((repayAmounts == null)
										|| (repayAmounts.size() == 0)) {
									continue;
								}
								String lastPan = null;
								// System.out.println("Size of repayment vector :"
								// + repayAmounts.size());
								for (int k = 0; k < repayAmounts.size(); k++) {
									repayamntAmnt = (RepaymentAmount) repayAmounts
											.get(k);
									String pan = repayamntAmnt.getCgpan();
									// System.out.println("PAN :" + pan);
									if (pan == null) {
										continue;
									}
									double repaidAmnt = repayamntAmnt
											.getRepaymentAmount();
									// System.out.println("REPAID AMNT :" +
									// repaidAmnt);
									if (k == 0) {
										lastPan = pan;
										totalRepaidAmnt = repaidAmnt
												+ totalRepaidAmnt;
									} else {
										if (pan.equals(lastPan)) {
											lastPan = pan;
											totalRepaidAmnt = repaidAmnt
													+ totalRepaidAmnt;
											continue;
										}
									}
									hashmap = (HashMap) allcgpandetails
											.remove(m);
									hashmap.put(
											ClaimConstants.TOTAL_AMNT_REPAID,
											new Double(totalRepaidAmnt));
									allcgpandetails.add(m, hashmap);
								}
							}
						}
					}
				}
			}
			// System.out.println("Printing HashMap :" + hashmap);
		}
		Log.log(Log.INFO, "CPDAO", "getCGPANDetailsForBorrowerId()", "Exited!");
		for (int i = 0; i < allcgpandetails.size(); i++) {
			HashMap m = (HashMap) allcgpandetails.elementAt(i);
			if (m == null) {
				continue;
			}
			Log.log(Log.INFO, "CPDAO", "getCGPANDetailsForBorrowerId()",
					"------> Printing the hashmap from CPDAO:" + m);
		}
		return allcgpandetails;
	}

	/*
	 * This method returns all the recovery modes from the database
	 */

	public Vector getAllRecoveryModes() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllRecoveryModes()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		Vector recoveryModes = new Vector();
		String recoverymode = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetAllRecoveryModes.funcGetAllRecoveryModes(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, Constants.CURSOR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllRecoveryModes()",
						"SP returns a 1. Error code is :" + errorCode);
				// closing the callable statement
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(2);

				// Extracting the recovery modes from the resultset
				while (resultset.next()) {
					recoverymode = resultset.getString(1);
					if (!recoveryModes.contains(recoverymode)) {
						recoveryModes.addElement(recoverymode);
					}
				}
				// closing the resultset
				resultset.close();
				resultset = null;

				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getAllRecoveryModes()",
					"Error retrieving Recovery Modes from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getAllRecoveryModes()", "Exited!");
		return recoveryModes;
	}

	/**
	 * This method saves the submitted claim application to the database.
	 */
	public void saveClaimApplication(ClaimApplication claimApplication,
			HashMap map, boolean flag) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()", "Entered!");
		String claimRefNumber = claimApplication.getClaimRefNumber();

		String borrowerId = claimApplication.getBorrowerId();
		// BorrowerInfo borrowerInfo = getBorrowerDetails(borrowerId);

		String memberId = claimApplication.getMemberId();

		// Parse the member Id to get the bank Id, zone Id and branch Id
		memberId = memberId.trim();
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);

		MemberInfo memberInfo = getMemberInfoDetails(bankId, zoneId, branchId);
		String bankName = memberInfo.getMemberBankName();

		String participatingBank = claimApplication.getParticipatingBank();

		// java.util.Date recallNoticeDate =
		// claimApplication.getDateOfIssueOfRecallNotice();
		GMProcessor gmprocessor = new GMProcessor();

		String whethereWillFulDfaulter = claimApplication
				.getWhetherBorrowerIsWilfulDefaulter();
		java.util.Date dtOfConclusionOfRecProc = claimApplication
				.getDtOfConclusionOfRecoveryProc();
		String whetherAccntWrittenOffBooks = claimApplication
				.getWhetherAccntWrittenOffFromBooksOfMLI();
		java.util.Date dtOfWrittenOffBooks = claimApplication
				.getDtOnWhichAccntWrittenOff();

		/*
		 * if(claimApplication.getSecondInstallment()) { //
		 * System.out.println("Borrower Id passed to GMProcessor :" +
		 * borrowerId); NPADetails npaDtls =
		 * gmprocessor.getNPADetails(borrowerId); if(npaDtls != null) {
		 * npaDtls.setWillfulDefaulter(whethereWillFulDfaulter);
		 * npaDtls.setDtOfConclusionOfRecProc(dtOfConclusionOfRecProc);
		 * npaDtls.setWhetherWrittenOff(whetherAccntWrittenOffBooks);
		 * npaDtls.setDtOnWhichAccntWrittenOff(dtOfWrittenOffBooks);
		 * 
		 * // Updating the NPA Details // updateNPADetails(npaDtls); } }
		 */

		LegalProceedingsDetail legalProceedingsDetail = claimApplication
				.getLegalProceedingsDetails();

		// Updating the Legal Proceedings Details
		// updateLegalProceedingDetails(legalProceedingsDetail);

		java.util.Date filingdate = legalProceedingsDetail.getFilingDate();
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()", "Filing Date is :"
				+ filingdate);
		double amountClaimed = legalProceedingsDetail.getAmountClaimed();
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
				"Amount Claimed is :" + amountClaimed);
		String currentStatusRemarks = legalProceedingsDetail
				.getCurrentStatusRemarks();
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
				"Current Status Remarks is :" + currentStatusRemarks);
		String isRecoveryProceedingsComplete = legalProceedingsDetail
				.getIsRecoveryProceedingsConcluded();
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
				"Recovery Proceedings Complete is :"
						+ isRecoveryProceedingsComplete);

		java.util.Date claimSubmittedDate = claimApplication
				.getClaimSubmittedDate();

		java.util.Date dateOfReleaseOfWC = claimApplication
				.getDateOfReleaseOfWC();

		java.util.Date dateOfIssueOfRecallNotice = claimApplication
				.getDateOfIssueOfRecallNotice();

		String nameOfOfficial = claimApplication.getNameOfOfficial();
		String designationOfOfficial = claimApplication
				.getDesignationOfOfficial();
		String place = claimApplication.getPlace();
		String createdModifiedBy = claimApplication.getCreatedModifiedy();
		String installmentFlag = null;
		if (claimApplication.getFirstInstallment()) {
			installmentFlag = ClaimConstants.FIRST_INSTALLMENT;
		} else if (claimApplication.getSecondInstallment()) {
			installmentFlag = ClaimConstants.SECOND_INSTALLMENT;
		}

		String cgpan = null;
		java.util.Date disbursementDate = null;
		double principalAmount = 0.0;
		double interestAmnt = 0.0;
		double npaAmount = 0.0;
		double legalAmount = 0.0;
		double claimAmount = 0.0;

		CallableStatement callableStmt = null;
		Connection conn = null;
		boolean hasExceptionOccured = false;
		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection(false);
			// conn.setAutoCommit(false);
			// callableStmt =
			// conn.prepareCall("{?=call funcInsertClaimDetails(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimDetailsNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.setString(3, borrowerId);
			callableStmt.setString(4, bankId);
			callableStmt.setString(5, zoneId);
			callableStmt.setString(6, branchId);
			callableStmt.setString(7, participatingBank);
			if (dateOfIssueOfRecallNotice != null) {
				callableStmt.setDate(8, new java.sql.Date(
						dateOfIssueOfRecallNotice.getTime()));
			} else {
				callableStmt.setNull(8, java.sql.Types.DATE);
			}
			if (filingdate != null) {
				callableStmt
						.setDate(9, new java.sql.Date(filingdate.getTime()));
			} else {
				callableStmt.setNull(9, java.sql.Types.DATE);
			}
			if (dateOfReleaseOfWC != null) {
				callableStmt.setDate(10,
						new java.sql.Date(dateOfReleaseOfWC.getTime()));
			} else {
				callableStmt.setNull(10, java.sql.Types.DATE);
			}
			callableStmt.setString(11, nameOfOfficial);
			callableStmt.setString(12, designationOfOfficial);
			callableStmt.setString(13, bankName);
			callableStmt.setString(14, place);
			if (claimSubmittedDate != null) {
				callableStmt.setDate(15,
						new java.sql.Date(claimSubmittedDate.getTime()));
			} else {
				callableStmt.setNull(15, java.sql.Types.DATE);
			}

			callableStmt.setString(16, installmentFlag);
			callableStmt.setString(17, createdModifiedBy);

			java.util.Date subsidyDate = (java.util.Date) claimApplication
					.getSubsidyDate();
			if (subsidyDate != null) {
				callableStmt.setDate(18,
						new java.sql.Date(subsidyDate.getTime()));
			} else {
				callableStmt.setNull(18, java.sql.Types.DATE);
			}
			callableStmt.setDouble(19, claimApplication.getSubsidyAmt());
			callableStmt.setString(20, claimApplication.getIfsCode());
			callableStmt.setString(21, claimApplication.getNeftCode());
			callableStmt.setString(22, claimApplication.getRtgsBankName());
			callableStmt.setString(23, claimApplication.getRtgsBranchName());
			callableStmt.setString(24, claimApplication.getRtgsBankNumber());
			callableStmt.setString(25, claimApplication.getMicroCategory());

			callableStmt.setString(26, claimApplication.getWilful());
			callableStmt.setString(27, claimApplication.getFraudFlag());
			callableStmt.setString(28, claimApplication.getEnquiryFlag());
			callableStmt
					.setString(29, claimApplication.getMliInvolvementFlag());
			callableStmt.setString(30, claimApplication.getReasonForRecall());
			callableStmt.setString(31,
					claimApplication.getReasonForFilingSuit());

			java.util.Date possessionDt = claimApplication
					.getAssetPossessionDt();
			if (possessionDt != null) {
				callableStmt.setDate(32,
						new java.sql.Date(possessionDt.getTime()));
			} else {
				callableStmt.setNull(32, java.sql.Types.DATE);
			}
			callableStmt
					.setString(33, claimApplication.getInclusionOfReceipt());
			callableStmt.setString(34,
					claimApplication.getConfirmRecoveryFlag());
			callableStmt.setString(35, claimApplication.getSubsidyFlag());
			callableStmt.setString(36,
					claimApplication.getIsSubsidyRcvdAfterNpa());
			callableStmt.setString(37,
					claimApplication.getIsSubsidyAdjustedOnDues());
			callableStmt.setString(38,
					claimApplication.getMliCommentOnFinPosition());
			callableStmt.setString(39,
					claimApplication.getDetailsOfFinAssistance());
			callableStmt.setString(40, claimApplication.getCreditSupport());
			callableStmt
					.setString(41, claimApplication.getBankFacilityDetail());
			callableStmt.setString(42,
					claimApplication.getPlaceUnderWatchList());
			callableStmt.setString(43, claimApplication.getRemarksOnNpa());

			// callableStmt.registerOutParameter(26,java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(44, java.sql.Types.VARCHAR);
			// callableStmt.execute();//commented for testing
			status = callableStmt.getInt(1);
			// errorCode = callableStmt.getString(26);
			errorCode = callableStmt.getString(44);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1. Error code is :" + errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;

			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Done with saving Generic Info of Claim Application");
			// /////////////////////////////////////////////////////////////
			// /////////////// Storing Term Loan/Capital Loan Details //////
			// /////////////////////////////////////////////////////////////
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Saving Term Loan Dtls of Claim Application......");
			// tcDetails is a collection of TermLoanCapitalLoanDetail objects
			Vector tcDetails = claimApplication.getTermCapitalDtls();
			double osAsOnSecondClmLodgemnt = 0.0;
			String tcClaimFlag = "";
			double totalDisbAmnt = 0.0D;
			if (tcDetails != null) {
				for (int i = 0; i < tcDetails.size(); i++) {
					TermLoanCapitalLoanDetail tcLoanDetail = (TermLoanCapitalLoanDetail) tcDetails
							.elementAt(i);
					if (tcLoanDetail == null) {
						continue;
					}
					cgpan = tcLoanDetail.getCgpan();
					disbursementDate = tcLoanDetail.getLastDisbursementDate();
					principalAmount = tcLoanDetail.getPrincipalRepayment();
					interestAmnt = tcLoanDetail.getInterestAndOtherCharges();
					npaAmount = tcLoanDetail.getOutstandingAsOnDateOfNPA();
					legalAmount = tcLoanDetail
							.getOutstandingStatedInCivilSuit();
					claimAmount = tcLoanDetail
							.getOutstandingAsOnDateOfLodgement();
					osAsOnSecondClmLodgemnt = tcLoanDetail
							.getOsAsOnDateOfLodgementOfClmForSecInstllmnt();
					tcClaimFlag = tcLoanDetail.getTcClaimFlag();
					totalDisbAmnt = tcLoanDetail.getTotaDisbAmnt();
					// Calling the stored procedure
					callableStmt = conn
							.prepareCall("{?=call funcInsertClaimTLDetails(?,?,?,?,?,?,?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, claimRefNumber);
					callableStmt.setString(3, cgpan);
					if (disbursementDate != null) {
						callableStmt.setDate(4, new java.sql.Date(
								disbursementDate.getTime()));
					} else {
						callableStmt.setNull(4, java.sql.Types.DATE);
					}
					callableStmt.setDouble(5, principalAmount);
					callableStmt.setDouble(6, interestAmnt);
					callableStmt.setDouble(7, npaAmount);
					callableStmt.setDouble(8, legalAmount);
					callableStmt.setDouble(9, claimAmount);
					callableStmt.setDouble(10, osAsOnSecondClmLodgemnt);
					callableStmt.registerOutParameter(11,
							java.sql.Types.VARCHAR);

					callableStmt.execute(); // commented for testing
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(11);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
								"SP returns a 1. Error code is :" + errorCode);
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							throw new DatabaseException(sqlex.getMessage());
						}
						throw new DatabaseException(errorCode);
					}
					callableStmt.close();
					callableStmt = null;
				}
			}
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Done with saving Term Loan Details of Claim Application");

			// //////////////////////////////////////////////////////////////////////////
			// ///////////////Storing the Working Capital
			// Details////////////////////////
			// //////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Saving Working Capital Dtls of Claim Application......");
			ArrayList wcDetails = claimApplication.getWorkingCapitalDtls();
			double osWCAsOnSecWCClmt = 0.0;
			String wcClaimFlag = "";
			if (wcDetails != null) {
				for (int i = 0; i < wcDetails.size(); i++) {
					WorkingCapitalDetail wcDetail = (WorkingCapitalDetail) wcDetails
							.get(i);
					cgpan = wcDetail.getCgpan();
					npaAmount = wcDetail.getOutstandingAsOnDateOfNPA();
					legalAmount = wcDetail.getOutstandingStatedInCivilSuit();
					claimAmount = wcDetail.getOutstandingAsOnDateOfLodgement();
					osWCAsOnSecWCClmt = wcDetail
							.getOsAsOnDateOfLodgementOfClmForSecInstllmnt();
					wcClaimFlag = wcDetail.getWcClaimFlag();
					callableStmt = conn
							.prepareCall("{? = call funcInsertClaimWCDetails(?,?,?,?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, claimRefNumber);
					callableStmt.setString(3, cgpan);
					callableStmt.setDouble(4, npaAmount);
					callableStmt.setDouble(5, legalAmount);
					callableStmt.setDouble(6, claimAmount);
					callableStmt.setDouble(7, osWCAsOnSecWCClmt);
					callableStmt
							.registerOutParameter(8, java.sql.Types.VARCHAR);

					callableStmt.execute(); // commentted for testing
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(8);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
								"SP returns a 1. Error code is :" + errorCode);
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							throw new DatabaseException(sqlex.getMessage());
						}
						throw new DatabaseException(errorCode);
					}
					// closing the callable statement
					callableStmt.close();
					callableStmt = null;
				}
			}
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Done with saving Working Capital Details of Claim Application");

			// /////////////////////////////////////////////////////////////////////////////
			// //////////////////// Storing the Recovery Details
			// ///////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Saving Recovery Details of Claim Application");
			// vecRecoveryDetails is a collection of RecoveryDetails objects
			Vector vecRecoveryDetails = claimApplication.getRecoveryDetails();
			String recoveryMode = null;
			double tcPrincipal = 0.0;
			double tcInterestAndOtherCharges = 0.0;
			double wcAmount = 0.0;
			double wcOtherCharges = 0.0;

			if (vecRecoveryDetails != null) {
				for (int i = 0; i < vecRecoveryDetails.size(); i++) {
					RecoveryDetails recoveryDetail = (RecoveryDetails) vecRecoveryDetails
							.elementAt(i);
					if (recoveryDetail == null) {
						continue;
					}
					cgpan = recoveryDetail.getCgpan();
					recoveryMode = recoveryDetail.getModeOfRecovery();
					tcPrincipal = recoveryDetail.getTcPrincipal();
					tcInterestAndOtherCharges = recoveryDetail
							.getTcInterestAndOtherCharges();
					wcAmount = recoveryDetail.getWcAmount();
					wcOtherCharges = recoveryDetail.getWcOtherCharges();
					if (tcPrincipal == 0.0 && tcInterestAndOtherCharges == 0.0
							&& wcAmount == 0.0 && wcOtherCharges == 0.0) {
						continue;
					}

					callableStmt = conn
							.prepareCall("{?=call funcInsertClaimRecoveryDetails(?,?,?,?,?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, claimRefNumber);
					callableStmt.setString(3, cgpan);
					callableStmt.setString(4, recoveryMode);
					callableStmt.setDouble(5, tcPrincipal);
					callableStmt.setDouble(6, tcInterestAndOtherCharges);
					callableStmt.setDouble(7, wcAmount);
					callableStmt.setDouble(8, wcOtherCharges);
					callableStmt
							.registerOutParameter(9, java.sql.Types.VARCHAR);

					// callableStmt.execute();//commented for testing
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(9);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
								"SP returns a 1. Error code is :" + errorCode);
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							throw new DatabaseException(sqlex.getMessage());
						}
						throw new DatabaseException(errorCode);
					}
					// closing the callable statement
					callableStmt.close();
					callableStmt = null;
				}
			}
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Done with saving Recovery Details of Claim Application");

			// //////////////////////////////////////////////////////////////////////////////
			// //Storing Security and Personal Guarantee Details as on date of
			// Sanction ////
			// //////////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Saving Security and Personal Guarantee Dtls of Claim Application for as on Sanction......");
			SecurityAndPersonalGuaranteeDtls secAndPerGuaranteeDtl = claimApplication
					.getSecurityAndPersonalGuaranteeDtls();

			DtlsAsOnDateOfNPA dtlsAsOnNPA = secAndPerGuaranteeDtl
					.getDetailsAsOnDateOfNPA();
			DtlsAsOnDateOfSanction dtlsAsOnSanction = secAndPerGuaranteeDtl
					.getDetailsAsOnDateOfSanction();
			DtlsAsOnLogdementOfClaim dtlsAsOnLodgeOfClaim = secAndPerGuaranteeDtl
					.getDetailsAsOnDateOfLodgementOfClaim();
			DtlsAsOnLogdementOfSecondClaim dtlsAsOnLodgemntOfSecClm = secAndPerGuaranteeDtl
					.getDetailsAsOnDateOfLodgementOfSecondClaim();
			// Hashtable claimSecurityIds = new Hashtable();

			double networthAsOnSanction = dtlsAsOnSanction
					.getNetworthOfGuarantors();
			double amntRealizedThruInvocationPerGuar = 0.0;
			double amntRealizedThruSecurity = 0.0;
			String reasonForReduction = null;
			String reasonForReductionAsOnSanction = dtlsAsOnSanction
					.getReasonsForReduction();
			String claimSecurityIdAsOnSanction = null;

			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimSecurityDetail(?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_AS_ON_SANCTION_CODE);
			callableStmt.setDouble(4, networthAsOnSanction);
			callableStmt.setString(5, reasonForReductionAsOnSanction);
			callableStmt.setDouble(6, amntRealizedThruInvocationPerGuar);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

			// callableStmt.execute();//commneted for testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(8);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing As on Dt of Sanction Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Getting the claim security detail id for "As on Sanction"
				claimSecurityIdAsOnSanction = callableStmt.getString(7);
				// claimSecurityIds.put(ClaimConstants.CLM_SAPGD_AS_ON_SANCTION,
				// claimSecurityIdAsOnSanction);
				callableStmt.close();
				callableStmt = null;
			}

			// Calling the next SP to save security and personal guarantee
			// particulars
			// For Land
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_LAND);
			double vallandsanction = dtlsAsOnSanction.getValueOfLand();
			callableStmt.setDouble(4, vallandsanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commneted for testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Land as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Land for As on Dt of Sanction");

			// For Machine
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_MC);
			double valmachinesanction = dtlsAsOnSanction.getValueOfMachine();
			callableStmt.setDouble(4, valmachinesanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commented for testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Machine as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Machine for As on Dt of Sanction");

			// For Building
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG);
			double valbldgesanction = dtlsAsOnSanction.getValueOfBuilding();
			callableStmt.setDouble(4, valbldgesanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commeneted for testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Building as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Building for As on Dt of Sanction");

			// For Other Fixed/Movable Assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS);
			double valfixedmovableassetssanction = dtlsAsOnSanction
					.getValueOfOtherFixedMovableAssets();
			callableStmt.setDouble(4, valfixedmovableassetssanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commented for testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Other Fixed/Movable Assets as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Fixed and Movable Assets for As on Dt of Sanction");

			// For Current Assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS);
			double valcurrassetssanction = dtlsAsOnSanction
					.getValueOfCurrentAssets();
			callableStmt.setDouble(4, valcurrassetssanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commneted for testig
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Current Assets as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Current Assets for As on Dt of Sanction");

			// For other assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnSanction != null)
					&& (!(claimSecurityIdAsOnSanction.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnSanction));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS);
			double valotherssanction = dtlsAsOnSanction.getValueOfOthers();
			callableStmt.setDouble(4, valotherssanction);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//commneted for testg
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Other Assets as on Dt of Sanction. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Other Assets for As on Dt of Sanction");
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Done with Security and Personal Guarantee Dtls of Claim Application for as on Sanction");

			// //////////////////////////////////////////////////////////////////////////////
			// //Storing Security and Personal Guarantee Details as on date of
			// NPA//////////
			// //////////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Saving Security and Personal Guarantee Dtls of Claim Application for as on date of NPA");
			double networthAsOnNPA = dtlsAsOnNPA.getNetworthOfGuarantors();
			String reasonForReductionAsOnNPA = dtlsAsOnNPA
					.getReasonsForReduction();
			String claimSecurityIdAsOnNPA = null;

			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimSecurityDetail(?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_AS_ON_NPA_CODE);
			callableStmt.setDouble(4, networthAsOnNPA);
			callableStmt.setString(5, reasonForReductionAsOnNPA);
			callableStmt.setDouble(6, amntRealizedThruInvocationPerGuar);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

			// callableStmt.execute();//testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(8);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing NPA Dtls. Error code is :"
								+ errorCode);
				// closing the callable statement
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Getting the claim security detail id for "As on NPA"
				claimSecurityIdAsOnNPA = callableStmt.getString(7);
				// claimSecurityIds.put(ClaimConstants.CLM_SAPGD_AS_ON_NPA,
				// claimSecurityIdAsOnNPA);
				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}

			// Calling the next SP to save security and personal guarantee
			// particulars
			// For Land
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_LAND);
			double vallandnpa = dtlsAsOnNPA.getValueOfLand();
			callableStmt.setDouble(4, vallandnpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testin g
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Land as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Land for As on Dt of NPA");

			// For Machine
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_MC);
			double valmachinenpa = dtlsAsOnNPA.getValueOfMachine();
			callableStmt.setDouble(4, valmachinenpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testin g
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Machine as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Machine for As on Dt of NPA");

			// For Building
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG);
			double valbldgnpa = dtlsAsOnNPA.getValueOfBuilding();
			callableStmt.setDouble(4, valbldgnpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testig
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Building as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();

				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Building for As on Dt of NPA");

			// For Fixed/Movable Assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS);
			double valfixedmovassetsnpa = dtlsAsOnNPA
					.getValueOfOtherFixedMovableAssets();
			callableStmt.setDouble(4, valfixedmovassetsnpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testinmg
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Fixed/Movable Assets as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Fixed and Movable Assets for As on Dt of NPA");

			// For CurrentsAssets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS);
			double valcurrassetsnpa = dtlsAsOnNPA.getValueOfCurrentAssets();
			callableStmt.setDouble(4, valcurrassetsnpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Current Assets as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Current Assets for As on Dt of NPA");

			// For Others
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnNPA != null)
					&& (!(claimSecurityIdAsOnNPA.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnNPA));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS);
			double valothersnpa = dtlsAsOnNPA.getValueOfOthers();
			callableStmt.setDouble(4, valothersnpa);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			// callableStmt.execute();//testin g
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing Others as on NPA. Error code is :"
								+ errorCode);
				callableStmt.close();
				callableStmt = null;
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Other Assets for As on Dt of NPA");
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Done with saving Security and Personal Guarantee Dtls of Claim Application for as on dt of npa");
			// //////////////////////////////////////////////////////////////////////////////
			// Storing Security and Personal Guarantee Details as date of Claim
			// Lodgement ///
			// //////////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Saving Security and Personal Guarantee Details of Claim Application for as on date of lodgement of claim");
			double networthAsOnLodgement = dtlsAsOnLodgeOfClaim
					.getNetworthOfGuarantors();
			String reasonForReductionAsOnLodgeOfClaim = dtlsAsOnLodgeOfClaim
					.getReasonsForReduction();
			String claimSecurityIdAsOnLodgeOfClaim = null;

			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimSecurityDetail(?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_AS_ON_LODGE_OF_CLM);
			callableStmt.setDouble(4, networthAsOnLodgement);
			callableStmt.setString(5, reasonForReductionAsOnLodgeOfClaim);
			callableStmt.setDouble(6, amntRealizedThruInvocationPerGuar);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

			// callableStmt.execute();//testing
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(8);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
						"SP returns a 1 in storing As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Getting the claim security detail id for "As on NPA"
				claimSecurityIdAsOnLodgeOfClaim = callableStmt.getString(7);
				// claimSecurityIds.put(ClaimConstants.CLM_SAPGD_AS_ON_LODGEMNT_OF_CLM,
				// claimSecurityIdAsOnLodgeOfClaim);
				callableStmt.close();
			}

			// Calling the next SP to save security and personal guarantee
			// particulars
			// For Land
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_LAND);
			double vallandlodgeclm = dtlsAsOnLodgeOfClaim.getValueOfLand();
			callableStmt.setDouble(4, vallandlodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Land As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();

				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Land for As on Dt of Lodgement of First Claim");

			// For Machine
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_MC);
			double valmclodgeclm = dtlsAsOnLodgeOfClaim.getValueOfMachine();
			callableStmt.setDouble(4, valmclodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Machine As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				callableStmt = null;
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Machine for As on Dt of Lodgement of First Claim");

			// For Building
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3, ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG);
			double valbldglodgeclm = dtlsAsOnLodgeOfClaim.getValueOfBuilding();
			callableStmt.setDouble(4, valbldglodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Building As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Building for As on Dt of Lodgement of First Claim");

			// For Fixed/Movable Assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS);
			double valfixedmovassetslodgeclm = dtlsAsOnLodgeOfClaim
					.getValueOfOtherFixedMovableAssets();
			callableStmt.setDouble(4, valfixedmovassetslodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Fixed Movable Assets As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Fixed Assets for As on Dt of Lodgement of First Claim");

			// For Current Assets
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS);
			double valcurrassetslodgeclm = dtlsAsOnLodgeOfClaim
					.getValueOfCurrentAssets();
			callableStmt.setDouble(4, valcurrassetslodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Current Assets As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Current Assets for As on Dt of Lodgement of First Claim");

			// For Others
			callableStmt = conn
					.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if ((claimSecurityIdAsOnLodgeOfClaim != null)
					&& (!(claimSecurityIdAsOnLodgeOfClaim.equals("")))) {
				callableStmt.setDouble(2,
						Double.parseDouble(claimSecurityIdAsOnLodgeOfClaim));
			}
			callableStmt.setString(3,
					ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS);
			double valotherslodgeclm = dtlsAsOnLodgeOfClaim.getValueOfOthers();
			callableStmt.setDouble(4, valotherslodgeclm);
			callableStmt.setDouble(5, amntRealizedThruSecurity);
			callableStmt.setString(6, reasonForReduction);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// Calling the Stored Procedure
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveClaimApplication()",
						"SP returns a 1 in storing Others As on Dt of Lodgemnt of Claim Dtl. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Successfully saved - Other Assets for As on Dt of Lodgement of First Claim");
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Done with saving Security and Personal Guarantee Details of Claim Application for as on dt of lodgement of claim");
			// /////////////////////////////////////////////////////////////////////////////////
			// Storing Security and Personal Guarantee Details On date of Second
			// Clm Lodgement/
			// /////////////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Saving Security and Personal Guarantee Details of Claim Application for as on dt of lodgement of second claim.....");
			if (claimApplication.getSecondInstallment()) {
				double networthAsOnLodgmntOfSecClm = dtlsAsOnLodgemntOfSecClm
						.getNetworthOfGuarantors();
				amntRealizedThruInvocationPerGuar = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedPersonalGuarantee();

				// String reasonForReductionAsOnLodgeOfSecClaim =
				// dtlsAsOnLodgemntOfSecClm.getReasonsForReduction();
				String reasonForReductionAsOnLodgeOfSecClaim = "";
				String claimSecurityIdAsOnLodgeOfSecClaim = null;

				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimSecurityDetail(?,?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				callableStmt.setString(2, claimRefNumber);
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_AS_ON_LODGE_OF_SEC_CLM);
				callableStmt.setDouble(4, networthAsOnLodgmntOfSecClm);
				callableStmt
						.setString(5, reasonForReductionAsOnLodgeOfSecClaim);
				callableStmt.setDouble(6, amntRealizedThruInvocationPerGuar);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
				callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(8);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				} else if (status == Constants.FUNCTION_SUCCESS) {
					// Getting the claim security detail id for "As on NPA"
					claimSecurityIdAsOnLodgeOfSecClaim = callableStmt
							.getString(7);
					// claimSecurityIds.put(ClaimConstants.CLM_SAPGD_AS_ON_LODGEMNT_OF_CLM,
					// claimSecurityIdAsOnLodgeOfClaim);
					callableStmt.close();
				}

				// Calling the next SP to save security and personal guarantee
				// particulars
				// For Land
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedLand();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionLand();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_PARTICULAR_LAND);
				double vallandlodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfLand();
				callableStmt.setDouble(4, vallandlodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Land As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Land for As on Dt of Lodgement of Second Claim");

				// For Machine
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedMachine();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionMachine();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_PARTICULAR_MC);
				double valmclodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfMachine();
				callableStmt.setDouble(4, valmclodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Machine As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Machine for As on Dt of Lodgement of Second Claim");

				// For Building
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedBuilding();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionBuilding();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG);
				double valbldglodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfBuilding();
				callableStmt.setDouble(4, valbldglodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Building As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Building for As on Dt of Lodgement of Second Claim");

				// For Fixed/Movable Assets
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedFixed();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionFixed();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt
						.setString(
								3,
								ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS);
				double valfixedmovassetslodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfOtherFixedMovableAssets();
				callableStmt.setDouble(4, valfixedmovassetslodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Fixed/Movable Assets As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Fixed Assets for As on Dt of Lodgement of Second Claim");

				// For Current Assets
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedCurrent();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionCurrent();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS);
				double valcurrassetslodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfCurrentAssets();
				callableStmt.setDouble(4, valcurrassetslodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Current Assets As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Current Assets for As on Dt of Lodgement of Second Claim");

				// For Others
				amntRealizedThruSecurity = dtlsAsOnLodgemntOfSecClm
						.getAmtRealisedOthers();
				reasonForReduction = dtlsAsOnLodgemntOfSecClm
						.getReasonsForReductionOthers();
				callableStmt = conn
						.prepareCall("{?=call funcInsertClaimPerGuarDetail(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				if ((claimSecurityIdAsOnLodgeOfSecClaim != null)
						&& (!(claimSecurityIdAsOnLodgeOfSecClaim.equals("")))) {
					callableStmt.setDouble(2, Double
							.parseDouble(claimSecurityIdAsOnLodgeOfSecClaim));
				}
				callableStmt.setString(3,
						ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS);
				double valotherslodgeOfSecclm = dtlsAsOnLodgemntOfSecClm
						.getValueOfOthers();
				callableStmt.setDouble(4, valotherslodgeOfSecclm);
				callableStmt.setDouble(5, amntRealizedThruSecurity);
				callableStmt.setString(6, reasonForReduction);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// Calling the Stored Procedure
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR,
							"CPDAO",
							"saveClaimApplication()",
							"SP returns a 1 in storing Others As on Dt of Lodgemnt of Second Claim Dtl. Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						throw new DatabaseException(sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				callableStmt.close();
				callableStmt = null;
				Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
						"Successfully saved - Other Assets for As on Dt of Lodgement of Second Claim");
			}
			Log.log(Log.INFO,
					"CPDAO",
					"saveClaimApplication()",
					"Done with saving Security and Personal Guarantee Details of Claim Application for as on dt of lodgement of second claim");

			// ///////////////////////////////////////////////////////////////////////////
			// ///////////////////Capturing Claim Summry Details
			// /////////////////////////
			// ///////////////////////////////////////////////////////////////////////////
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Saving Claim Summary Details of Claim Application");
			ArrayList claimsummarydtls = claimApplication.getClaimSummaryDtls();
			double appliedamnt = 0.0;
			ClaimSummaryDtls dtls = null;
			if (claimsummarydtls != null) {
				for (int i = 0; i < claimsummarydtls.size(); i++) {
					dtls = (ClaimSummaryDtls) claimsummarydtls.get(i);
					cgpan = dtls.getCgpan();
					appliedamnt = dtls.getAmount();

					callableStmt = conn
							.prepareCall("{? = call funcInsertClaimAmount(?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, claimRefNumber);
					callableStmt.setString(3, cgpan);
					callableStmt.setDouble(4, appliedamnt);
					callableStmt
							.registerOutParameter(5, java.sql.Types.VARCHAR);

					// Calling the Stored Procedure
					callableStmt.execute();
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(5);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveClaimApplication()",
								"SP returns a 1 in storing Claim Summary Details. Error code is :"
										+ errorCode);
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							throw new DatabaseException(sqlex.getMessage());
						}
						throw new DatabaseException(errorCode);
					}
					callableStmt.close();
				}
			}

			NPADetails npaDtls = gmprocessor.getNPADetails(borrowerId);
			if (claimApplication.getFirstInstallment()) {
				// npaDtls = gmprocessor.getNPADetails(borrowerId);
				updateNPADetails(npaDtls);
				updateLegalProceedingDetails(legalProceedingsDetail);
				if (map != null) {
					String borowrId = (String) map
							.get(ClaimConstants.CLM_BORROWER_ID);
					String itpanOfChiefPromoter = (String) map
							.get(ClaimConstants.CLM_ITPAN_OF_CHIEF_PROMOTER);
					saveITPANDetail(borowrId, itpanOfChiefPromoter);
				}
			}
			if (claimApplication.getSecondInstallment()) {
				// System.out.println("Borrower Id passed to GMProcessor :" +
				// borrowerId);
				// npaDtls = gmprocessor.getNPADetails(borrowerId);
				if (npaDtls != null) {
					npaDtls.setWillfulDefaulter(whethereWillFulDfaulter);
					npaDtls.setDtOfConclusionOfRecProc(dtOfConclusionOfRecProc);
					npaDtls.setWhetherWrittenOff(whetherAccntWrittenOffBooks);
					npaDtls.setDtOnWhichAccntWrittenOff(dtOfWrittenOffBooks);

					// Updating the NPA Details
					updateNPADetails(npaDtls);

					// Updating the Legal Proceedings Details
					updateLegalProceedingDetails(legalProceedingsDetail);
				}
			}

			insertRecallAndLegalAttachments(claimApplication, claimRefNumber,
					flag);

			// updateNPADetails(npaDtls);

			// Updating the Legal Proceedings Details
			// updateLegalProceedingDetails(legalProceedingsDetail);

			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Done with saving Claim Summary Details of Claim Application");
			conn.commit();
		} catch (DatabaseException sqlexception) {
			hasExceptionOccured = true;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Error in saving the claim application.");
			try {
				callableStmt.close();
				conn.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} catch (SQLException sqlexception) {
			hasExceptionOccured = true;
			Log.log(Log.INFO, "CPDAO", "saveClaimApplication()",
					"Error in saving the claim application.");
			try {
				callableStmt.close();
				conn.rollback();
			} catch (SQLException sqlex) {
				throw new DatabaseException(sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveClaimApplication()", "Exited!");
	}

	/**
	 * This method is used to retrieve all claim reference numbers. These
	 * reference numbers are used to file second installment.
	 */
	public ArrayList getAllClaimRefNums(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllClaimRefNums()", "Entered!");
		String claimRefNum = null;
		String cgclan = null;

		CallableStatement callableStmt = null;
		ResultSet rs = null;
		Connection conn = null;
		Hashtable claimRefNumDetail = new Hashtable();
		ArrayList claimRefNumbers = new ArrayList();

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call packGetClmRefNoForMember.funcGetClmRefNoForMember(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllClaimRefNums()",
						"SP returns a 1.Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the cursor object from the callable statement
				rs = (ResultSet) callableStmt.getObject(5);
				while (rs.next()) {
					claimRefNum = rs.getString(1);
					cgclan = rs.getString(2);
					claimRefNumDetail.put(ClaimConstants.CLM_CLAIM_REF_NUMBER,
							claimRefNum);
					claimRefNumDetail.put(ClaimConstants.CLM_CGCLAN, cgclan);
					claimRefNumbers.add(claimRefNumDetail);
				}
				// closing the resultset
				rs.close();
				rs = null;
			}
			// closing the callable statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getAllClaimRefNums()",
					"Error retrieving all Claim Reference Numbers from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getAllClaimRefNums()", "Exited!");
		return claimRefNumbers;
	}

	/**
	 * This method retrives the cgpan loan details. The details are for
	 * capturing OTS Request details!
	 */
	public Vector getOTSRequestDetails(String borrowerId)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getOTSRequestDetails()", "Entered!");
		Hashtable cgpanloandetail = null;
		String cgpan = null;
		String tcSanctionedAmnt = null;
		String wcFBSanctionedAmnt = null;
		String wcNFBSanctionedAmnt = null;
		String approvedAmnt = null;
		String enhancedApprovedAmnt = null;
		ResultSet rs = null;
		Vector cgpanloandetails = new Vector();
		String reapproveAmount = null;
		String appRefNumber = null;
		ApplicationDAO appDAO = new ApplicationDAO();
		Application application = null;

		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetCGPANLoanDtlForBid.funcGetCGPANLoanDtlForBid(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getOTSRequestDetails()",
						"SP returns a 1.Error code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting resultset from the callable statement
				rs = (ResultSet) callableStmt.getObject(3);
				while (rs.next()) {
					cgpanloandetail = new Hashtable();
					cgpan = rs.getString(1);
					if (cgpan == null) {
						continue;
					}
					approvedAmnt = Double.toString(rs.getDouble(2));
					enhancedApprovedAmnt = Double.toString(rs.getDouble(3));
					tcSanctionedAmnt = Double.toString(rs.getDouble(4));
					wcFBSanctionedAmnt = Double.toString(rs.getDouble(5));
					wcNFBSanctionedAmnt = Double.toString(rs.getDouble(6));
					appRefNumber = getAppRefNumber(cgpan);
					if (appRefNumber != null) {
						application = appDAO
								.getAppForAppRef(null, appRefNumber);
					}
					if (application == null) {
						continue;
					}
					reapproveAmount = Double.toString(application
							.getReapprovedAmount());

					// Populating the hashtable with cgpan loan details!
					cgpanloandetail.put(ClaimConstants.CLM_CGPAN, cgpan);
					if ((application.getReapprovedAmount()) == 0.0) {
						cgpanloandetail.put(
								ClaimConstants.CLM_APPLICATION_APPRVD_AMNT,
								approvedAmnt);
					}
					cgpanloandetail.put(
							ClaimConstants.CLM_ENHANCED_APPRVD_AMNT,
							enhancedApprovedAmnt);
					cgpanloandetail.put(ClaimConstants.CLM_TC_SANCTIONED_AMNT,
							tcSanctionedAmnt);
					cgpanloandetail.put(
							ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT,
							wcFBSanctionedAmnt);
					cgpanloandetail.put(
							ClaimConstants.CLM_WC_NFB_SANCTIONED_AMNT,
							wcNFBSanctionedAmnt);
					if ((application.getReapprovedAmount()) > 0.0) {
						cgpanloandetail.put(
								ClaimConstants.CLM_APPLICATION_APPRVD_AMNT,
								reapproveAmount);
					}

					// Adding the populated hashtable to the vector
					cgpanloandetails.addElement(cgpanloandetail);
				}
				// closing the resultset
				rs.close();
				rs = null;
			}
			// closing the callable statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getOTSRequestDetails()",
					"Error retrieving OTS Details for a Borrower from the database");
			// System.out.println("Error retrieving OTS Details for a Borrower from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getOTSRequestDetails()", "Exited!");
		return cgpanloandetails;
	}

	/**
	 * This method saves the OTS details to the database.
	 */
	public void saveOTSDetail(OTSRequestDetail otsDetail, String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveOTSDetail()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		String borrowerId = otsDetail.getCgbid();
		String reasonForOTS = otsDetail.getReasonForOTS();
		String willfulDefaulter = otsDetail.getWillfulDefaulter();

		/*
		 * if(willfulDefaulter.equals(ClaimConstants.CLM_CHECKBOX_YES))
		 * willfulDefaulter = ClaimConstants.DISBRSMNT_YES_FLAG;
		 * if(willfulDefaulter.equals(ClaimConstants.CLM_CHECKBOX_NO))
		 * willfulDefaulter =ClaimConstants.DISBRSMNT_NO_FLAG;
		 */

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call funcInsertOTSRequest(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.setString(3, reasonForOTS);
			callableStmt.setString(4, willfulDefaulter);
			callableStmt.setString(5, userId);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveOTSDetail()",
						"SP returns a 1 in saving OTS Request Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			// closing the callable statement
			callableStmt.close();

			// Storing cgpan wise details
			String cgpan = null;
			String proposedAmntPaid = null;
			String amountSacrificed = null;
			String osAmntAsOnDate = null;
			Vector details = otsDetail.getLoanDetails();

			// conn = DBConnection.getConnection();
			for (int i = 0; i < details.size(); i++) {
				Hashtable ht = (Hashtable) details.elementAt(i);
				if (ht == null) {
					continue;
				}

				if (ht.containsKey(ClaimConstants.CLM_CGPAN)) {
					cgpan = (String) ht.get(ClaimConstants.CLM_CGPAN);
				}
				if (ht.containsKey(ClaimConstants.CLM_OTS_PROPOSED_AMNT_TOBEPAID_BY_BORROWER)) {
					proposedAmntPaid = (String) ht
							.get(ClaimConstants.CLM_OTS_PROPOSED_AMNT_TOBEPAID_BY_BORROWER);
				}
				if (ht.containsKey(ClaimConstants.CLM_OTS_PROPOSED_AMNT_TOBESACRIFICED)) {
					amountSacrificed = (String) ht
							.get(ClaimConstants.CLM_OTS_PROPOSED_AMNT_TOBESACRIFICED);
				}
				if (ht.containsKey(ClaimConstants.CLM_OTS_OS_AMNT_AS_ON_DT)) {
					osAmntAsOnDate = (String) ht
							.get(ClaimConstants.CLM_OTS_OS_AMNT_AS_ON_DT);
				}

				callableStmt = conn
						.prepareCall("{? = call funcInsertOTSAmount(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				callableStmt.setString(2, borrowerId);
				callableStmt.setString(3, cgpan);
				if ((proposedAmntPaid != null)
						&& (!(proposedAmntPaid.equals("")))) {
					callableStmt.setDouble(4,
							Double.parseDouble(proposedAmntPaid));
				}
				if ((amountSacrificed != null)
						&& (!(amountSacrificed.equals("")))) {
					callableStmt.setDouble(5,
							Double.parseDouble(amountSacrificed));
				}
				if ((osAmntAsOnDate != null) && (!(osAmntAsOnDate.equals("")))) {
					callableStmt.setDouble(6,
							Double.parseDouble(osAmntAsOnDate));
				}
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "CPDAO", "saveOTSDetail()",
							"SP returns a 1 in saving CGPAN wise OTS Request Details.Error code is :"
									+ errorCode);
					throw new DatabaseException(
							"OTS Details already exist for the Borrower");
				}
				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "saveOTSDetail()",
					"Error saving CGPAN-wise OTS Details into the database");
			try {
				conn.rollback();
			} catch (SQLException sqlex) {
				Log.log(Log.ERROR,
						"CPDAO",
						"saveOTSDetail()",
						"Error rolling back the transaction :"
								+ sqlex.getMessage());
			}
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveOTSDetail()", "Exited!");
	}

	public Vector getToBeApprovedOTSRequests() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getToBeApprovedOTSRequests()", "Entered!");
		String memberId = null;
		String bankId = null;
		String zoneId = null;
		String branchId = null;
		String borrowerId = null;
		java.util.Date otsRequestDate = null;
		// String decision = null;
		// String remarks = null;
		OTSApprovalDetail otsApprovalDetail = null;
		Vector otsRequestDetails = new Vector();

		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetPendingOTSRequests.funcGetPendingOTSRequests(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, Constants.CURSOR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getToBeApprovedOTSRequests()",
						"SP returns a 1 in saving CGPAN wise OTS Request Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(2);

				// Extracting the values from the resultset
				while (resultset.next()) {
					otsApprovalDetail = new OTSApprovalDetail();
					borrowerId = resultset.getString(1);
					otsRequestDate = resultset.getDate(2);
					bankId = resultset.getString(3);
					zoneId = resultset.getString(4);
					branchId = resultset.getString(5);
					memberId = bankId + zoneId + branchId;

					// setting the values in the OTSApprovalDetail object
					otsApprovalDetail.setCgbid(borrowerId);
					otsApprovalDetail.setMliId(memberId);
					otsApprovalDetail.setOtsRequestDate(otsRequestDate);

					// adding the otsRequestDetail object to the vector
					boolean duplicateobjectexists = false;
					for (int i = 0; i < otsRequestDetails.size(); i++) {
						OTSApprovalDetail temp = (OTSApprovalDetail) otsRequestDetails
								.elementAt(i);
						if (temp == null) {
							continue;
						}
						String tempmliid = temp.getMliId();
						String tempbid = temp.getCgbid();
						java.util.Date tempOtsReqDate = temp
								.getOtsRequestDate();
						if (tempmliid.equals(memberId)
								&& tempbid.equals(borrowerId)
								&& tempOtsReqDate.equals(otsRequestDate)) {
							duplicateobjectexists = true;
							break;
						}
					}
					if (!duplicateobjectexists) {
						otsRequestDetails.addElement(otsApprovalDetail);
					}
				}
				// closing the resultset
				resultset.close();
				resultset = null;
			}
			// closing the callable statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getToBeApprovedOTSRequests()",
					"Error retrieving to be approved OTS Requests from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getToBeApprovedOTSRequests()", "Exited!");
		return otsRequestDetails;
	}

	/**
	 * This method is used to update the decision taken by the CGTSI user on the
	 * pending OTS requests
	 */
	public void saveOTSProcessingResults(Vector otsApprovalDetails,
			String userId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveOTSProcessingResults()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		OTSApprovalDetail otsApprovalDtl = null;

		int status = -1;
		String errorCode = null;
		boolean isThereException = false;

		try {
			conn = DBConnection.getConnection();

			for (int i = 0; i < otsApprovalDetails.size(); i++) {
				otsApprovalDtl = (OTSApprovalDetail) otsApprovalDetails
						.elementAt(i);
				if (otsApprovalDtl == null) {
					continue;
				}
				String borrowerId = otsApprovalDtl.getCgbid();
				String decision = otsApprovalDtl.getDecision();
				String remarks = otsApprovalDtl.getRemarks();

				callableStmt = conn
						.prepareCall("{? = call funcUpdateOTSRequest(?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				callableStmt.setString(2, borrowerId);
				callableStmt.setString(3, decision);
				callableStmt.setString(4, remarks);
				callableStmt.setString(5, userId);
				callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(6);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "CPDAO", "saveOTSProcessingResults()",
							"SP returns a 1 in saving OTS Processing Results.Error code is :"
									+ errorCode);
					callableStmt.close();
					throw new DatabaseException(errorCode);
				}
				// closing the callable statement
				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			isThereException = true;
			Log.log(Log.ERROR, "CPDAO", "saveOTSProcessingResults()",
					"Error saving OTS Processing Results into the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveOTSProcessingResults()", "Exited!");
	}

	/* For claim approval details begin part here */

	/**
	 * 
	 * @param flag
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public Vector getClaimApprovalDetails(String loggedUsr, String bankName)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimApprovalDetails()", "Entered!");
		Vector claimDetails = new Vector();
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ClaimDetail claimdetail = null;
		String flag = ClaimConstants.FIRST_INSTALLMENT;
		String cgclan = null;
		String bid = null;
		String memberId = null;
		String claimRefNumber = null;
		double claimApprovedAmnt = 0.0;
		double applicationApprovedAmnt = 0.0;
		double tcApprovedAmt = 0.0;
		double wcApprovedAmt = 0.0;
		double tcOutstanding = 0.0;
		double wcOutstanding = 0.0;
		double tcrecovery = 0.0;
		double wcrecovery = 0.0;
		double tcEligibleAmt = 0.0;
		double wcEligibleAmt = 0.0;
		double tcDeduction = 0.0;
		double wcDeduction = 0.0;
		double tcFirstInstallment = 0.0;
		double wcFirstInstallment = 0.0;
		java.util.Date clmApprvdDate = null;
		java.util.Date npaEffectiveDate = null;
		double outstandingAmntAsOnNPA = 0.0;
		double appliedClaimAmnt = 0.0;
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String unitName = null;
		int status = -1;
		String errorCode = null;

		double asfRefundableForTC = 0.0;
		double asfRefundableForWC = 0.0;
		String refundFlag = "N";
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClmAppForUser.funcGetClmAppForUser(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, loggedUsr);
			callableStmt.setString(3, bankName);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimApprovalDetails()",
						"SP returns a 1 in retrieving Claim Processing Results.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(4);

				while (resultset.next()) {
					// Extracting the values from the resultset
					memberId = resultset.getString(1);
					// System.out.println("Member Id:"+memberId);
					claimRefNumber = resultset.getString(2);
					// System.out.println("claimRefNumber:"+claimRefNumber);
					unitName = resultset.getString(3);
					bid = resultset.getString(4);
					tcApprovedAmt = resultset.getDouble(5);
					wcApprovedAmt = resultset.getDouble(6);
					tcOutstanding = resultset.getDouble(7);
					wcOutstanding = resultset.getDouble(8);
					tcrecovery = resultset.getDouble(9);
					wcrecovery = resultset.getDouble(10);
					tcEligibleAmt = resultset.getDouble(11);
					wcEligibleAmt = resultset.getDouble(12);
					tcDeduction = resultset.getDouble(13);
					wcDeduction = resultset.getDouble(14);
					tcFirstInstallment = resultset.getDouble(15);
					wcFirstInstallment = resultset.getDouble(16);
					comments = resultset.getString(17);

					asfRefundableForTC = resultset.getDouble(18);
					asfRefundableForWC = resultset.getDouble(19);
					refundFlag = resultset.getString(20);

					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						// System.out.println("In CPDAO.java Flag is equals to Claim First Installment");
						// Setting the values in ClaimDetail object
						claimdetail = new ClaimDetail();
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						claimdetail.setSsiUnitName(unitName);
						claimdetail.setBorrowerId(bid);
						claimdetail.setTcApprovedAmt(tcApprovedAmt);
						claimdetail.setWcApprovedAmt(wcApprovedAmt);
						claimdetail.setTcOutstanding(tcOutstanding);
						claimdetail.setWcOutstanding(wcOutstanding);
						claimdetail.setTcrecovery(tcrecovery);
						claimdetail.setWcrecovery(wcrecovery);
						claimdetail.setTcClaimEligibleAmt(tcEligibleAmt);
						claimdetail.setWcClaimEligibleAmt(wcEligibleAmt);
						claimdetail.setAsfDeductableforTC(tcDeduction);
						claimdetail.setAsfDeductableforWC(wcDeduction);
						claimdetail.setTcFirstInstallment(tcFirstInstallment);
						claimdetail.setWcFirstInstallment(wcFirstInstallment);
						claimdetail.setComments(comments);

						claimdetail.setAsfRefundableForTC(asfRefundableForTC);
						claimdetail.setAsfRefundableForWC(asfRefundableForWC);
						claimdetail.setRefundFlag(refundFlag);

					}
					if (claimdetail != null) {
						// adding the claimdetail object to the vector
						claimDetails.addElement(claimdetail);
					}
				}
				resultset.close();
				resultset = null;
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getClaimApprovalDetails()",
					"Error retrieving Details for Processing Claims from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimApprovalDetails()", "Exited!");
		return claimDetails;
	}

	/* For claim approval details end part here */

	/**
	 * This method retrieves the claim processing details from the database.
	 */
	public Vector getClaimProcessingDetails(String flag)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimProcessingDetails()", "Entered!");
		Vector claimDetails = new Vector();
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ClaimDetail claimdetail = null;
		ClaimDetail cd = null;

		String cgclan = null;
		String bid = null;
		String memberId = null;
		String bankId = null;
		String zoneId = null;
		String branchId = null;
		String claimRefNumber = null;
		double claimApprovedAmnt = 0.0;
		double applicationApprovedAmnt = 0.0;
		java.util.Date clmApprvdDate = null;
		java.util.Date npaEffectiveDate = null;
		double outstandingAmntAsOnNPA = 0.0;
		double appliedClaimAmnt = 0.0;
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String unitName = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClaimstoAuthorizeDtl.funcGetClaimstoAuthorizeDtl(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, flag);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimProcessingDetails()",
						"SP returns a 1 in retrieving Claim Processing Results.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(3);

				while (resultset.next()) {
					// Extracting the values from the resultset
					cgclan = resultset.getString(1);
					bid = resultset.getString(2);
					bankId = resultset.getString(3);
					zoneId = resultset.getString(4);
					branchId = resultset.getString(5);
					memberId = bankId + zoneId + branchId;
					// System.out.println("CPDAO memberId:"+memberId);
					claimRefNumber = resultset.getString(6);
					// System.out.println("1 - CLAIM REF NUMBER :" +
					// claimRefNumber);
					clmStatus = resultset.getString(7);
					// System.out.println("Claim Status :" + clmStatus);
					claimApprovedAmnt = resultset.getDouble(8);
					// System.out.println("claimApprovedAmnt:"+claimApprovedAmnt);
					clmApprvdDate = resultset.getDate(9);
					// System.out.println("clmApprvdDate:"+clmApprvdDate);
					comments = (String) resultset.getString(10);
					// System.out.println("Comments:"+comments);
					forwardedToUser = (String) resultset.getString(11);
					npaEffectiveDate = resultset.getDate(12);
					// System.out.println("npaEffectiveDate:"+npaEffectiveDate);
					outstandingAmntAsOnNPA = resultset.getDouble(13);
					// System.out.println("outstandingAmntAsOnNPA:"+outstandingAmntAsOnNPA);
					appliedClaimAmnt = resultset.getDouble(14);
					// System.out.println("appliedClaimAmnt:"+appliedClaimAmnt);
					applicationApprovedAmnt = resultset.getDouble(15);
					// System.out.println("applicationApprovedAmnt:"+applicationApprovedAmnt);
					unitName = resultset.getString(16);
					// System.out.println("Unit Name:"+unitName);
					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						// System.out.println("In CPDAO.java Flag is equals to Claim First Installment");
						// Setting the values in ClaimDetail object
						claimdetail = new ClaimDetail();
						claimdetail.setCGCLAN(cgclan);
						// System.out.println("cgclan:"+cgclan);
						claimdetail.setBorrowerId(bid);
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						// System.out.println("claimRefNumber:"+claimRefNumber);
						claimdetail.setClmStatus(clmStatus);
						claimdetail.setApprovedClaimAmount(claimApprovedAmnt);
						claimdetail
								.setApplicationApprovedAmount(applicationApprovedAmnt);
						claimdetail.setClmApprvdDt(clmApprvdDate);
						claimdetail.setNpaDate(npaEffectiveDate);
						claimdetail
								.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
						claimdetail.setAppliedClaimAmt(appliedClaimAmnt);
						claimdetail.setComments(comments);
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setSsiUnitName(unitName);
					} else if (flag.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						HashMap details = getFirstClmDtlForBid(bankId, zoneId,
								branchId, bid);
						if (details != null) {
							if (details.containsKey(ClaimConstants.CLM_CGCLAN)) {
								String cgcln = (String) details
										.get(ClaimConstants.CLM_CGCLAN);
								// Setting the values in ClaimDetail object
								claimdetail = new ClaimDetail();
								claimdetail.setCGCLAN(cgcln);
								claimdetail.setBorrowerId(bid);
								claimdetail.setMliId(memberId);
								claimdetail.setClaimRefNum(claimRefNumber);
								claimdetail.setClmStatus(clmStatus);
								claimdetail
										.setApprovedClaimAmount(claimApprovedAmnt);
								claimdetail
										.setApplicationApprovedAmount(applicationApprovedAmnt);
								claimdetail.setClmApprvdDt(clmApprvdDate);
								claimdetail.setNpaDate(npaEffectiveDate);
								claimdetail
										.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
								claimdetail
										.setAppliedClaimAmt(appliedClaimAmnt);
								claimdetail.setComments(comments);
								claimdetail.setForwaredToUser(forwardedToUser);
								claimdetail.setSsiUnitName(unitName);
							}
						}

					}
					if (claimdetail != null) {

						// adding the claimdetail object to the vector
						claimDetails.addElement(claimdetail);
					}
				}
				// System.out.println("***11 -> Size of the Claim Details Vector :"
				// + claimDetails.size());
				// closing the resultset
				resultset.close();
				resultset = null;

				/*
				 * Retrieving the resultset querying the temp npa_detail table
				 */
				resultset = (ResultSet) callableStmt.getObject(4);
				int count = 0;
				while (resultset.next()) {
					// Extracting the values from the resultset
					cgclan = resultset.getString(1);
					bid = resultset.getString(2);
					bankId = resultset.getString(3);
					zoneId = resultset.getString(4);
					branchId = resultset.getString(5);
					memberId = bankId + zoneId + branchId;
					claimRefNumber = resultset.getString(6);
					clmStatus = resultset.getString(7);
					// System.out.println("Claim Status :" + clmStatus);
					// System.out.println("2 - CLAIM REF NUMBER :" +
					// claimRefNumber);
					claimApprovedAmnt = resultset.getDouble(8);
					clmApprvdDate = resultset.getDate(9);
					comments = (String) resultset.getString(10);
					forwardedToUser = (String) resultset.getString(11);
					npaEffectiveDate = resultset.getDate(12);
					outstandingAmntAsOnNPA = resultset.getDouble(13);
					appliedClaimAmnt = resultset.getDouble(14);
					applicationApprovedAmnt = resultset.getDouble(15);
					unitName = resultset.getString(16);
					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						// Setting the values in ClaimDetail object
						claimdetail = new ClaimDetail();
						claimdetail.setCGCLAN(cgclan);
						claimdetail.setBorrowerId(bid);
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						claimdetail.setClmStatus(clmStatus);
						claimdetail.setApprovedClaimAmount(claimApprovedAmnt);
						claimdetail
								.setApplicationApprovedAmount(applicationApprovedAmnt);
						claimdetail.setClmApprvdDt(clmApprvdDate);
						claimdetail.setNpaDate(npaEffectiveDate);
						claimdetail
								.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
						claimdetail.setAppliedClaimAmt(appliedClaimAmnt);
						claimdetail.setComments(comments);
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setSsiUnitName(unitName);
					} else if (flag.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						HashMap details = getFirstClmDtlForBid(bankId, zoneId,
								branchId, bid);
						if (details != null) {
							if (details.containsKey(ClaimConstants.CLM_CGCLAN)) {
								String cgcln = (String) details
										.get(ClaimConstants.CLM_CGCLAN);
								// Setting the values in ClaimDetail object
								claimdetail = new ClaimDetail();
								claimdetail.setCGCLAN(cgcln);
								claimdetail.setBorrowerId(bid);
								claimdetail.setMliId(memberId);
								claimdetail.setClaimRefNum(claimRefNumber);
								claimdetail.setClmStatus(clmStatus);
								claimdetail
										.setApprovedClaimAmount(claimApprovedAmnt);
								claimdetail
										.setApplicationApprovedAmount(applicationApprovedAmnt);
								claimdetail.setClmApprvdDt(clmApprvdDate);
								claimdetail.setNpaDate(npaEffectiveDate);
								claimdetail
										.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
								claimdetail
										.setAppliedClaimAmt(appliedClaimAmnt);
								claimdetail.setComments(comments);
								claimdetail.setForwaredToUser(forwardedToUser);
								claimdetail.setSsiUnitName(unitName);
							}
						}

					}
					// System.out.println("*** 12 -> Size of the Claim Details Vector :"
					// + claimDetails.size());

					if (claimDetails.size() == 0) {
						claimDetails.addElement(claimdetail);
						// continue;
					}

					String clmRefNum = null;

					for (int j = 0; j < claimDetails.size(); j++) {
						cd = (ClaimDetail) claimDetails.elementAt(j);
						clmRefNum = cd.getClaimRefNum();
						// System.out.println("1. Claim Ref Number :" +
						// clmRefNum);
						// System.out.println("2. Claim Ref Number :" +
						// claimRefNumber);
						if (clmRefNum != null) {
							if (!clmRefNum.equals(claimRefNumber)) {
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the Vector :" +
								// clmRefNum);
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the ResultSet :" +
								// claimRefNumber);
								if (!claimDetails.contains(claimdetail)) {
									claimDetails.addElement(claimdetail);
								}
							}
							if (clmRefNum.equals(claimRefNumber)) {
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the Vector :" +
								// clmRefNum);
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the ResultSet:" +
								// claimRefNumber);
								cd = (ClaimDetail) claimDetails.remove(j);
								claimDetails.add(j, claimdetail);
							}
						}
					}
					count++;
				}
				resultset.close();
				resultset = null;
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getClaimProcessingDetails()",
					"Error retrieving Details for Processing Claims from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimProcessingDetails()", "Exited!");
		// System.out.println("*** 2 -> Size of the Claim Details Vector :" +
		// claimDetails.size());
		/*
		 * for(int i=0; i<claimDetails.size(); i++) { ClaimDetail clmDtl =
		 * (ClaimDetail)claimDetails.elementAt(i); if(clmDtl == null) {
		 * continue; } String memId = (String)clmDtl.getMliId();
		 * Log.log(Log.INFO
		 * ,"CPDAO","getClaimProcessingDetails()","******************************"
		 * ); Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()","memId :" +
		 * memId); String claimrefnumber = (String)clmDtl.getClaimRefNum();
		 * Log.log
		 * (Log.INFO,"CPDAO","getClaimProcessingDetails()","claimrefnumber :" +
		 * claimrefnumber); String claimStatus = (String)clmDtl.getClmStatus();
		 * Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()","clmStatus :"
		 * + claimStatus); String commnts = (String)clmDtl.getComments(); String
		 * forwardedToUsr = (String)clmDtl.getForwaredToUser();
		 * Log.log(Log.INFO,
		 * "CPDAO","getClaimProcessingDetails()","forwardedToUser :" +
		 * forwardedToUsr);
		 * Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()"
		 * ,"******************************"); }
		 */
		return claimDetails;
	}

	/**
	 * 
	 * @param flag
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public Vector getClaimProcessingDetailsMod(String flag,
			java.sql.Date fromDate, java.sql.Date toDate)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimProcessingDetailsMod()", "Entered!");
		Vector claimDetails = new Vector();
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ClaimDetail claimdetail = null;
		ClaimDetail cd = null;

		String cgclan = null;
		String bid = null;
		String memberId = null;
		String bankId = null;
		String zoneId = null;
		String branchId = null;
		String claimRefNumber = null;
		double claimApprovedAmnt = 0.0;
		double applicationApprovedAmnt = 0.0;
		java.util.Date clmApprvdDate = null;
		java.util.Date npaEffectiveDate = null;
		double outstandingAmntAsOnNPA = 0.0;
		double appliedClaimAmnt = 0.0;
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String unitName = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call PackgetclaimstoauthorizedtlNew.funcGetClaimstoAuthorizeDtlNew(?,?,?,?,?,?)}");
			
			System.out.println(" 4308 PackgetclaimstoauthorizedtlNew.funcGetClaimstoAuthorizeDtlNew ");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, flag);
			callableStmt.setDate(3, fromDate);
			callableStmt.setDate(4, toDate);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimProcessingDetails()",
						"SP returns a 1 in retrieving Claim Processing Results.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(5);

				while (resultset.next()) {
					// Extracting the values from the resultset
					cgclan = resultset.getString(1);
					bid = resultset.getString(2);
					bankId = resultset.getString(3);
					zoneId = resultset.getString(4);
					branchId = resultset.getString(5);
					memberId = bankId + zoneId + branchId;
					// System.out.println("CPDAO memberId:"+memberId);
					claimRefNumber = resultset.getString(6);
					// System.out.println("1 - CLAIM REF NUMBER :" +
					// claimRefNumber);
					clmStatus = resultset.getString(7);
					// System.out.println("Claim Status :" + clmStatus);
					claimApprovedAmnt = resultset.getDouble(8);
					// System.out.println("claimApprovedAmnt:"+claimApprovedAmnt);
					clmApprvdDate = resultset.getDate(9);
					// System.out.println("clmApprvdDate:"+clmApprvdDate);
					comments = (String) resultset.getString(10);
					// System.out.println("Comments:"+comments);
					forwardedToUser = (String) resultset.getString(11);
					npaEffectiveDate = resultset.getDate(12);
					// System.out.println("npaEffectiveDate:"+npaEffectiveDate);
					outstandingAmntAsOnNPA = resultset.getDouble(13);
					// System.out.println("outstandingAmntAsOnNPA:"+outstandingAmntAsOnNPA);
					appliedClaimAmnt = resultset.getDouble(14);
					// System.out.println("appliedClaimAmnt:"+appliedClaimAmnt);
					applicationApprovedAmnt = resultset.getDouble(15);
					// System.out.println("applicationApprovedAmnt:"+applicationApprovedAmnt);
					unitName = resultset.getString(16);
					// System.out.println("Unit Name:"+unitName);
					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						// System.out.println("In CPDAO.java Flag is equals to Claim First Installment");
						// Setting the values in ClaimDetail object
						claimdetail = new ClaimDetail();
						claimdetail.setCGCLAN(cgclan);
						// System.out.println("cgclan:"+cgclan);
						claimdetail.setBorrowerId(bid);
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						// System.out.println("claimRefNumber:"+claimRefNumber);
						claimdetail.setClmStatus(clmStatus);
						claimdetail.setApprovedClaimAmount(claimApprovedAmnt);
						claimdetail
								.setApplicationApprovedAmount(applicationApprovedAmnt);
						claimdetail.setClmApprvdDt(clmApprvdDate);
						claimdetail.setNpaDate(npaEffectiveDate);
						claimdetail
								.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
						claimdetail.setAppliedClaimAmt(appliedClaimAmnt);
						claimdetail.setComments(comments);
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setSsiUnitName(unitName);
					} else if (flag.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						HashMap details = getFirstClmDtlForBid(bankId, zoneId,
								branchId, bid);
						if (details != null) {
							if (details.containsKey(ClaimConstants.CLM_CGCLAN)) {
								String cgcln = (String) details
										.get(ClaimConstants.CLM_CGCLAN);
								// Setting the values in ClaimDetail object
								claimdetail = new ClaimDetail();
								claimdetail.setCGCLAN(cgcln);
								claimdetail.setBorrowerId(bid);
								claimdetail.setMliId(memberId);
								claimdetail.setClaimRefNum(claimRefNumber);
								claimdetail.setClmStatus(clmStatus);
								claimdetail
										.setApprovedClaimAmount(claimApprovedAmnt);
								claimdetail
										.setApplicationApprovedAmount(applicationApprovedAmnt);
								claimdetail.setClmApprvdDt(clmApprvdDate);
								claimdetail.setNpaDate(npaEffectiveDate);
								claimdetail
										.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
								claimdetail
										.setAppliedClaimAmt(appliedClaimAmnt);
								claimdetail.setComments(comments);
								claimdetail.setForwaredToUser(forwardedToUser);
								claimdetail.setSsiUnitName(unitName);
							}
						}

					}
					if (claimdetail != null) {

						// adding the claimdetail object to the vector
						claimDetails.addElement(claimdetail);
					}
				}
				// System.out.println("***11 -> Size of the Claim Details Vector :"
				// + claimDetails.size());
				// closing the resultset
				resultset.close();
				resultset = null;

				/*
				 * Retrieving the resultset querying the temp npa_detail table
				 */
				resultset = (ResultSet) callableStmt.getObject(6);
				int count = 0;
				while (resultset.next()) {
					// Extracting the values from the resultset
					cgclan = resultset.getString(1);
					bid = resultset.getString(2);
					bankId = resultset.getString(3);
					zoneId = resultset.getString(4);
					branchId = resultset.getString(5);
					memberId = bankId + zoneId + branchId;
					claimRefNumber = resultset.getString(6);
					clmStatus = resultset.getString(7);
					// System.out.println("Claim Status :" + clmStatus);
					// System.out.println("2 - CLAIM REF NUMBER :" +
					// claimRefNumber);
					claimApprovedAmnt = resultset.getDouble(8);
					clmApprvdDate = resultset.getDate(9);
					comments = (String) resultset.getString(10);
					forwardedToUser = (String) resultset.getString(11);
					npaEffectiveDate = resultset.getDate(12);
					outstandingAmntAsOnNPA = resultset.getDouble(13);
					appliedClaimAmnt = resultset.getDouble(14);
					applicationApprovedAmnt = resultset.getDouble(15);
					unitName = resultset.getString(16);
					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						// Setting the values in ClaimDetail object
						claimdetail = new ClaimDetail();
						claimdetail.setCGCLAN(cgclan);
						claimdetail.setBorrowerId(bid);
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						claimdetail.setClmStatus(clmStatus);
						claimdetail.setApprovedClaimAmount(claimApprovedAmnt);
						claimdetail
								.setApplicationApprovedAmount(applicationApprovedAmnt);
						claimdetail.setClmApprvdDt(clmApprvdDate);
						claimdetail.setNpaDate(npaEffectiveDate);
						claimdetail
								.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
						claimdetail.setAppliedClaimAmt(appliedClaimAmnt);
						claimdetail.setComments(comments);
						claimdetail.setForwaredToUser(forwardedToUser);
						claimdetail.setSsiUnitName(unitName);
					} else if (flag.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						HashMap details = getFirstClmDtlForBid(bankId, zoneId,
								branchId, bid);
						if (details != null) {
							if (details.containsKey(ClaimConstants.CLM_CGCLAN)) {
								String cgcln = (String) details
										.get(ClaimConstants.CLM_CGCLAN);
								// Setting the values in ClaimDetail object
								claimdetail = new ClaimDetail();
								claimdetail.setCGCLAN(cgcln);
								claimdetail.setBorrowerId(bid);
								claimdetail.setMliId(memberId);
								claimdetail.setClaimRefNum(claimRefNumber);
								claimdetail.setClmStatus(clmStatus);
								claimdetail
										.setApprovedClaimAmount(claimApprovedAmnt);
								claimdetail
										.setApplicationApprovedAmount(applicationApprovedAmnt);
								claimdetail.setClmApprvdDt(clmApprvdDate);
								claimdetail.setNpaDate(npaEffectiveDate);
								claimdetail
										.setOutstandingAmntAsOnNPADate(outstandingAmntAsOnNPA);
								claimdetail
										.setAppliedClaimAmt(appliedClaimAmnt);
								claimdetail.setComments(comments);
								claimdetail.setForwaredToUser(forwardedToUser);
								claimdetail.setSsiUnitName(unitName);
							}
						}

					}
					// System.out.println("*** 12 -> Size of the Claim Details Vector :"
					// + claimDetails.size());

					if (claimDetails.size() == 0) {
						claimDetails.addElement(claimdetail);
						// continue;
					}

					String clmRefNum = null;

					for (int j = 0; j < claimDetails.size(); j++) {
						cd = (ClaimDetail) claimDetails.elementAt(j);
						clmRefNum = cd.getClaimRefNum();
						// System.out.println("1. Claim Ref Number :" +
						// clmRefNum);
						// System.out.println("2. Claim Ref Number :" +
						// claimRefNumber);
						if (clmRefNum != null) {
							if (!clmRefNum.equals(claimRefNumber)) {
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the Vector :" +
								// clmRefNum);
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the ResultSet :" +
								// claimRefNumber);
								if (!claimDetails.contains(claimdetail)) {
									claimDetails.addElement(claimdetail);
								}
							}
							if (clmRefNum.equals(claimRefNumber)) {
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the Vector :" +
								// clmRefNum);
								// System.out.println("Count :" + count
								// +"Claim Ref Number from the ResultSet:" +
								// claimRefNumber);
								cd = (ClaimDetail) claimDetails.remove(j);
								claimDetails.add(j, claimdetail);
							}
						}
					}
					count++;
				}
				resultset.close();
				resultset = null;
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getClaimProcessingDetails()",
					"Error retrieving Details for Processing Claims from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimProcessingDetails()", "Exited!");
		// System.out.println("*** 2 -> Size of the Claim Details Vector :" +
		// claimDetails.size());
		/*
		 * for(int i=0; i<claimDetails.size(); i++) { ClaimDetail clmDtl =
		 * (ClaimDetail)claimDetails.elementAt(i); if(clmDtl == null) {
		 * continue; } String memId = (String)clmDtl.getMliId();
		 * Log.log(Log.INFO
		 * ,"CPDAO","getClaimProcessingDetails()","******************************"
		 * ); Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()","memId :" +
		 * memId); String claimrefnumber = (String)clmDtl.getClaimRefNum();
		 * Log.log
		 * (Log.INFO,"CPDAO","getClaimProcessingDetails()","claimrefnumber :" +
		 * claimrefnumber); String claimStatus = (String)clmDtl.getClmStatus();
		 * Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()","clmStatus :"
		 * + claimStatus); String commnts = (String)clmDtl.getComments(); String
		 * forwardedToUsr = (String)clmDtl.getForwaredToUser();
		 * Log.log(Log.INFO,
		 * "CPDAO","getClaimProcessingDetails()","forwardedToUser :" +
		 * forwardedToUsr);
		 * Log.log(Log.INFO,"CPDAO","getClaimProcessingDetails()"
		 * ,"******************************"); }
		 */
		return claimDetails;
	}

	public String generateClaimRefNumber(String bid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "generateClaimRefNumber()", "Entered!");
		String claimRefNumber = null;

		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call funcGenClmRefNum(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bid);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "generateClaimRefNumber()",
						"SP returns a 1 in generating Claim Ref Number.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				claimRefNumber = callableStmt.getString(3);
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "generateClaimRefNumber()",
					"Error generating Claim Reference Number");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "generateClaimRefNumber()", "Exited!");
		return claimRefNumber;
	}

	public String generateCGCLAN() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "generateCGCLAN()", "Entered!");
		String cgclan = null;

		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn.prepareCall("{? = call funcGenCGCLAN(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "generateCGCLAN()",
						"SP returns a 1 in generating CGCLAN.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// extracting the cgclan number from the callable statement
				cgclan = callableStmt.getString(2);
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "generateCGCLAN()",
					"Error generating CGCLAN");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "generateCGCLAN()", "Exited!");
		return cgclan;
	}

	/**
	 * This method updates the decision taken by CGTSI user while approving a
	 * claim application. The method returns HashMap containing Claim Ref Number
	 * and CGCLAN as key-value pairs.
	 */
	public HashMap saveClaimProcessingResults(Vector claimdetails)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveClaimProcessingResults()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		HashMap details = new HashMap();

		int status = -1;
		String errorCode = null;
		ClaimDetail claimdetail = null;

		try {
			// System.out.println("Size of vector :" + claimdetails.size());
			/*
			 * for(int i=0; i<claimdetails.size();i++) {
			 * System.out.println("Object is :" +
			 * (ClaimDetail)claimdetails.elementAt(i)); }
			 */
			conn = DBConnection.getConnection();

			for (int i = 0; i < claimdetails.size(); i++) {
				String cgclan = null;
				// conn = DBConnection.getConnection();
				// System.out.println("claimdetails.size"+claimdetails.size());

				claimdetail = (ClaimDetail) claimdetails.elementAt(i);
				if (claimdetail != null) {
					if (claimdetail.getWhichInstallemnt() == null) {
						continue;
					}
				} else {
					continue;
				}

				double claimApprovedAmnt = claimdetail.getApprovedClaimAmount();
				String decision = claimdetail.getDecision();
				String remarks = claimdetail.getComments();

				String rejectionReason = claimdetail.getRejectionReason();

				if (decision.equals(ClaimConstants.CLM_APPROVAL_STATUS)) {
					// System.out.println("claimRefNumber :" +
					// claimdetail.getClaimRefNum());
					// System.out.println("Member Id:"+claimdetail.getMliId());
					// System.out.println("Approved By:"+claimdetail.getCreatedModifiedBy());
					// System.out.println("cgclan :" + cgclan);
					// System.out.println("claimApprovedAmnt :" +
					// claimApprovedAmnt);
					// System.out.println("decision :" + decision);
					// System.out.println("remarks :" + remarks);
					if ((claimdetail.getWhichInstallemnt())
							.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						cgclan = generateCGCLAN();
						// calling this method for generate ASF DANs for claim
						// settled applications
						//generateSFDANforClaimSettledCases(claimdetail.getClaimRefNum(),claimdetail.getMliId(),claimdetail.getCreatedModifiedBy());
						// System.out.println("CG CLAN Number:"+cgclan);
						// System.out.println("CLM Ref Number:"+claimdetail.getClaimRefNum());
					} else if ((claimdetail.getWhichInstallemnt())
							.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						cgclan = claimdetail.getCGCLAN();
					}
				} else if (decision.equals(ClaimConstants.CLM_TEMPORARY_CLOSE)) {
					cgclan = null;
				} else if (decision.equals(ClaimConstants.CLM_TEMPORARY_REJECT)) {
					cgclan = null;
				} else if (decision.equals(ClaimConstants.CLM_PENDING_STATUS)) {
					cgclan = null;
				} else if (decision.equals(ClaimConstants.CLM_REJECT_STATUS)) {
					cgclan = null;
				} else if (decision.equals(ClaimConstants.CLM_WITHDRAWN)) {
					cgclan = null;
				} else if (decision.equals(ClaimConstants.CLM_FORWARD_STATUS)) {
					Mailer mailer = new Mailer();
					String subject = "Claim Application Forwarded by";
					String messageBody = "Claim Application for Member Id :"
							+ claimdetail.getMliId()
							+ " and Claim Ref Number :"
							+ claimdetail.getClaimRefNum();
					String forwardedToUser = claimdetail.getForwaredToUser();
					String processedBy = claimdetail.getCreatedModifiedBy();
					Administrator admin = new Administrator();
					User userinfo = null;
					try {
						userinfo = admin.getUserInfo(forwardedToUser);
					} catch (Exception ex) {
						Log.log(Log.INFO, "CPDAO",
								"saveClaimProcessingResults",
								"Error fetching the details for the User");
					}
					String emailId = userinfo.getEmailId();
					ArrayList emailIds = new ArrayList();
					emailIds.add(emailId);

					Message message = new Message(emailIds, null, null,
							subject, messageBody);
					message.setFrom(processedBy);
					ArrayList idsToBeSent = new ArrayList();
					idsToBeSent.add(forwardedToUser);
					Message emailMsg = new Message(idsToBeSent, null, null,	subject, messageBody);
					Log.log(Log.INFO, "CPDAO", "saveClaimProcessingResults","processedBy :" + processedBy);
					emailMsg.setFrom(processedBy);
					// admin.sendMail(emailMsg);

					/*
					 * try{ //Email is sent. mailer.sendEmail(message); }
					 * catch(MailerException mailerException ){
					 * 
					 * // throw new MailerException(
					 * "Email to the id provided during User Registration could not be sent."
					 * ); //
					 * request.setAttribute("message","Email could not be sent."
					 * ); Log.log(Log.INFO,"CPDAO","saveClaimProcessingResults",
					 * "Email could not be sent."); }
					 */
				} else if (decision.equals("RT")) {
					cgclan = null;
				}
				String claimRefNumber = claimdetail.getClaimRefNum();
				// System.out.println("TC-claimRefNumber:"+claimRefNumber);
				String forwardedToUser = claimdetail.getForwaredToUser();
				// System.out.println("TC-forwardedToUser:"+forwardedToUser);
				String createdModifiedBy = claimdetail.getCreatedModifiedBy();
				// System.out.println("TC-createdModifiedBy:"+createdModifiedBy);
				System.out.println("CLaim Ref Number :" + claimRefNumber);
				System.out.println("CGCLAN :" + cgclan);
				callableStmt = conn
						.prepareCall("{? = call funcApproveClaims2(?,?,?,?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				callableStmt.setString(2, claimRefNumber);
				if (cgclan == null) {
					callableStmt.setNull(3, java.sql.Types.VARCHAR);
				} else {
					callableStmt.setString(3, cgclan);
				}
				callableStmt.setDouble(4, claimApprovedAmnt);
				// System.out.println("claimApprovedAmnt:"+claimApprovedAmnt);
				callableStmt.setString(5, decision);
				// System.out.println("decision:"+decision);
				callableStmt.setString(6, remarks);
				// Log.log(Log.INFO,"CPDAO","saveClaimProcessingResults()","forwardedToUser :"
				// + forwardedToUser);
				if ((forwardedToUser == null)
						|| (forwardedToUser != null && (forwardedToUser
								.equals("")))) {
					callableStmt.setNull(7, java.sql.Types.VARCHAR);
				} else {
					callableStmt.setString(7, forwardedToUser);
				}
				callableStmt.setString(8, createdModifiedBy);
				callableStmt.setString(9, rejectionReason);

				// callableStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
				callableStmt.registerOutParameter(10, java.sql.Types.VARCHAR);

				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(10);
				// System.out.println("errorCode:"+errorCode);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "CPDAO", "saveClaimProcessingResults()",
							"SP returns a 1 in saving Claim Processing Results.Error code is :"
									+ errorCode);
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						Log.log(Log.ERROR, "CPDAO",
								"saveClaimProcessingResults",
								sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
				details.put(claimRefNumber, cgclan);
			}
			conn.commit();
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			// sqlexception.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException sqlex) {
				Log.log(Log.ERROR, "CPDAO", "saveClaimProcessingResults",
						sqlex.getMessage());
			}
			Log.log(Log.ERROR, "CPDAO", "saveClaimProcessingResults()",
					"Error saving Claims Processing Results into the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveClaimProcessingResults()", "Exited!");
		return details;
	}

	/*
	 * This method returns a vector of SettlementDetail objects
	 */

	public Vector getSettlementDetails(String bankId, String zoneId,
			String branchId, String flag, boolean anotherFlag)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getSettlementDetails()", "Entered!");
		String cgclan = null;
		double claimApprovedAmount = 0.0;
		double outstandingAmntAsOnNPA = 0.0;
		double recoveredAmnt = 0.0;
		// double otsAmnt = 0.0;
		double firstTierSettlementAmnt = 0.0;
		java.util.Date firstSettlmntDt = null;
		// double secTierSettlementAmnt = 0.0;
		// java.util.Date secSettlmntDt = null;
		String borrowerId = null;
		String lastBorrowerId = null;
		double totalRecoveredAmnt = 0.0;
		java.util.Date clmApprvdDate = null;

		CallableStatement callableStmt = null;
		Connection conn = null;
		int status = -1;
		String errorCode = null;
		ResultSet resultset = null;
		SettlementDetail settlementdetail = null;
		Vector settlementdetails = new Vector();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClaimSettlementDtl.funcGetClaimSettlementDtl(?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.setString(5, flag);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, Constants.CURSOR);
			callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(8);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getSettlementDetails()",
						"SP returns a 1 in getting Settlement Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
					/*
					 * Retrieving the resultset from the callable statement This
					 * resultset contains results querying npa_detail in the
					 * intranet database.
					 */
					resultset = (ResultSet) callableStmt.getObject(6);
					while (resultset.next()) {
						borrowerId = resultset.getString(1);
						settlementdetail = new SettlementDetail();
						cgclan = resultset.getString(2);
						claimApprovedAmount = resultset.getDouble(3);
						clmApprvdDate = resultset.getDate(4);
						outstandingAmntAsOnNPA = resultset.getDouble(5);
						// System.out.println("OS Amount :" +
						// outstandingAmntAsOnNPA);
						recoveredAmnt = resultset.getDouble(6);
						/*
						 * totalRecoveredAmnt = recoveredAmnt; if(borrowerId !=
						 * null) { if(borrowerId.equals(lastBorrowerId)) {
						 * totalRecoveredAmnt = totalRecoveredAmnt +
						 * recoveredAmnt; continue; } } lastBorrowerId =
						 * borrowerId;
						 */
						// Setting the values in SettlementDetail object
						settlementdetail.setCgbid(borrowerId);
						settlementdetail.setCgclan(cgclan);
						settlementdetail
								.setApprovedClaimAmt(claimApprovedAmount);
						settlementdetail.setClmApprvdDate(clmApprvdDate);
						settlementdetail
								.setOsAmtAsonNPA(outstandingAmntAsOnNPA);
						settlementdetail.setRecoveryAmt(recoveredAmnt);
						settlementdetail
								.setTypeOfSettlement(ClaimConstants.FIRST_INSTALLMENT);
						// settlementdetail.setFinalSettlementFlag(ClaimConstants.DISBRSMNT_NO_FLAG);

						// Adding the SettlementDetail object to the vector
						settlementdetails.add(settlementdetail);
					}
					// System.out.println("1 -> Size of Vector :" +
					// settlementdetails.size());
					// Closing the resultset
					resultset.close();
					resultset = null;

					/*
					 * Retrieving the resultset from the callable statement This
					 * resultset contains results querying npa_detail in the
					 * temp database.
					 */
					resultset = (ResultSet) callableStmt.getObject(7);
					while (resultset.next()) {
						borrowerId = resultset.getString(1);
						settlementdetail = new SettlementDetail();
						cgclan = resultset.getString(2);
						claimApprovedAmount = resultset.getDouble(3);
						clmApprvdDate = resultset.getDate(4);
						outstandingAmntAsOnNPA = resultset.getDouble(5);
						recoveredAmnt = resultset.getDouble(6);
						// System.out.println("Recovered Amount :" +
						// recoveredAmnt);
						/*
						 * totalRecoveredAmnt = recoveredAmnt; if(borrowerId !=
						 * null) { if(borrowerId.equals(lastBorrowerId)) {
						 * totalRecoveredAmnt = totalRecoveredAmnt +
						 * recoveredAmnt; continue; } } lastBorrowerId =
						 * borrowerId;
						 */
						// Setting the values in SettlementDetail object
						settlementdetail.setCgbid(borrowerId);
						settlementdetail.setCgclan(cgclan);
						settlementdetail
								.setApprovedClaimAmt(claimApprovedAmount);
						settlementdetail.setClmApprvdDate(clmApprvdDate);
						settlementdetail
								.setOsAmtAsonNPA(outstandingAmntAsOnNPA);
						settlementdetail.setRecoveryAmt(recoveredAmnt);
						settlementdetail
								.setTypeOfSettlement(ClaimConstants.FIRST_INSTALLMENT);
						// settlementdetail.setFinalSettlementFlag(ClaimConstants.DISBRSMNT_NO_FLAG);

						// Adding the SettlementDetail object to the vector
						// System.out.println("2 -> Size of Vector :" +
						// settlementdetails.size());

						if (settlementdetails.size() == 0) {
							settlementdetails.addElement(settlementdetail);
							continue;
						}
						/*
						 * else {
						 * settlementdetails.addElement(settlementdetail); }
						 */
						boolean toAdd = false;
						for (int i = 0; i < settlementdetails.size(); i++) {
							SettlementDetail sd = (SettlementDetail) settlementdetails
									.elementAt(i);
							if (sd != null) {
								String bd = sd.getCgbid();
								if (bd.equals(borrowerId)) {
									sd = (SettlementDetail) settlementdetails
											.remove(i);
									// settlementdetails.add(i,settlementdetail);
									// break;
									toAdd = true;
								}
							}
						}
						if (toAdd) {
							settlementdetails.addElement(settlementdetail);
							toAdd = false;
							continue;
						}
						settlementdetails.addElement(settlementdetail);
					}
					resultset.close();
					resultset = null;
				} else if (flag.equals(ClaimConstants.SECOND_INSTALLMENT)) {
					/*
					 * Retrieving the resultset from the callable statement This
					 * resultset contains results querying npa_detail in the
					 * intranet database.
					 */
					resultset = (ResultSet) callableStmt.getObject(6);
					while (resultset.next()) {
						borrowerId = resultset.getString(1);
						cgclan = resultset.getString(2);
						claimApprovedAmount = resultset.getDouble(3);
						clmApprvdDate = resultset.getDate(4);
						outstandingAmntAsOnNPA = resultset.getDouble(5);
						recoveredAmnt = resultset.getDouble(6);
						/*
						 * if(borrowerId.equals(lastBorrowerId)) {
						 * totalRecoveredAmnt = totalRecoveredAmnt +
						 * recoveredAmnt; }
						 */
						firstTierSettlementAmnt = resultset.getDouble(7);
						firstSettlmntDt = resultset.getDate(8);
						lastBorrowerId = borrowerId;

						// Setting the values in SettlementDetail object
						settlementdetail = new SettlementDetail();
						settlementdetail.setCgbid(borrowerId);
						settlementdetail.setCgclan(cgclan);
						settlementdetail
								.setApprovedClaimAmt(claimApprovedAmount);
						settlementdetail.setClmApprvdDate(clmApprvdDate);
						settlementdetail
								.setOsAmtAsonNPA(outstandingAmntAsOnNPA);
						settlementdetail.setRecoveryAmt(recoveredAmnt);
						settlementdetail
								.setTierOneSettlement(firstTierSettlementAmnt);
						settlementdetail
								.setTierOneSettlementDt(firstSettlmntDt);
						settlementdetail
								.setTypeOfSettlement(ClaimConstants.SECOND_INSTALLMENT);
						// settlementdetail.setFinalSettlementFlag(ClaimConstants.DISBRSMNT_NO_FLAG);

						// Adding the SettlementDetail object to the vector
						settlementdetails.add(settlementdetail);
					}
					// System.out.println("First Loop : Size of the Settlement Dtls Vector :"
					// + settlementdetails.size());

					// closing the resultset
					resultset.close();
					resultset = null;
					/*
					 * Retrieving the resultset from the callable statement This
					 * resultset contains results querying npa_detail in the
					 * temp database.
					 */
					resultset = (ResultSet) callableStmt.getObject(7);
					while (resultset.next()) {
						borrowerId = resultset.getString(1);
						cgclan = resultset.getString(2);
						claimApprovedAmount = resultset.getDouble(3);
						clmApprvdDate = resultset.getDate(4);
						outstandingAmntAsOnNPA = resultset.getDouble(5);
						recoveredAmnt = resultset.getDouble(6);
						if (borrowerId.equals(lastBorrowerId)) {
							totalRecoveredAmnt = totalRecoveredAmnt
									+ recoveredAmnt;
						}
						firstTierSettlementAmnt = resultset.getDouble(7);
						firstSettlmntDt = resultset.getDate(8);
						lastBorrowerId = borrowerId;

						// Setting the values in SettlementDetail object
						settlementdetail = new SettlementDetail();
						settlementdetail.setCgbid(borrowerId);
						settlementdetail.setCgclan(cgclan);
						settlementdetail
								.setApprovedClaimAmt(claimApprovedAmount);
						settlementdetail.setClmApprvdDate(clmApprvdDate);
						settlementdetail
								.setOsAmtAsonNPA(outstandingAmntAsOnNPA);
						settlementdetail.setRecoveryAmt(recoveredAmnt);
						settlementdetail
								.setTierOneSettlement(firstTierSettlementAmnt);
						settlementdetail
								.setTierOneSettlementDt(firstSettlmntDt);
						settlementdetail
								.setTypeOfSettlement(ClaimConstants.SECOND_INSTALLMENT);
						// settlementdetail.setFinalSettlementFlag(ClaimConstants.DISBRSMNT_NO_FLAG);

						// Adding the SettlementDetail object to the vector
						// settlementdetails.addElement(settlementdetail);

						if (settlementdetails.size() == 0) {
							settlementdetails.addElement(settlementdetail);
							continue;
						}
						/*
						 * else {
						 */
						// settlementdetails.add(settlementdetail);
						boolean toBeAdd = false;
						for (int i = 0; i < settlementdetails.size(); i++) {
							SettlementDetail sd = (SettlementDetail) settlementdetails
									.elementAt(i);
							if (sd != null) {
								String bd = sd.getCgbid();
								if (bd.equals(borrowerId)) {
									sd = (SettlementDetail) settlementdetails
											.remove(i);
									// settlementdetails.add(i,settlementdetail);
									toBeAdd = true;
								}
							}
							// settlementdetails.add(settlementdetail);
						}
						if (toBeAdd) {
							settlementdetails.addElement(settlementdetail);
							toBeAdd = false;
							continue;
						}
						settlementdetails.addElement(settlementdetail);
						// }

					}
					// System.out.println("Second Loop : Size of the Settlement Dtls Vector :"
					// + settlementdetails.size());
					// closing the resultset
					resultset.close();
					resultset = null;
				}
			}
			// closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getSettlementDetails()",
					"Error retrieving Settlement Details from the database.");
			// sqlexception.printStackTrace();
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getSettlementDetails()", "Exited!");
		return settlementdetails;
	}

	/*
	 * added by sukumar@path on 08/09/2009 for capturing the claim processing
	 * details
	 */

	/**
	 * 
	 * added this method by sukumar@path for capturing Claim process details
	 * 
	 * @param clmRefNum
	 * @param userRemarks
	 * @param tcServiceFee
	 * @param wcServiceFee
	 * @param tcClaimEligibleAmt
	 * @param wcClaimEligibleAmt
	 * @param tcFirstInstallment
	 * @param wcFirstInstallment
	 * @param totalTCOSAmountAsOnNPA
	 * @param totalWCOSAmountAsOnNPA
	 * @param tcrecovery
	 * @param wcrecovery
	 * @param dateofReceipt
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void insertClaimProcessDetails(String clmRefNum, String userRemarks,
			double tcServiceFee, double wcServiceFee,
			double tcClaimEligibleAmt, double wcClaimEligibleAmt,
			double tcFirstInstallment, double wcFirstInstallment,
			double totalTCOSAmountAsOnNPA, double totalWCOSAmountAsOnNPA,
			double tcrecovery, double wcrecovery, java.util.Date dateofReceipt)
			throws DatabaseException {
		String methodName = "insertClaimProcessDetails";
		Connection connection = null;
		CallableStatement insertClaimProcessDtls = null;
		Log.log(Log.INFO, "CPDAO", methodName, "Entered");
		int updateStatus = 0;
		boolean newConn = false;
		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			insertClaimProcessDtls = connection
					.prepareCall("{?= call FuncInsClaimSetDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			insertClaimProcessDtls.registerOutParameter(1, Types.INTEGER);
			insertClaimProcessDtls.setString(2, clmRefNum);
			// System.out.println("clmRefNum:"+clmRefNum);
			insertClaimProcessDtls.setString(3, userRemarks);
			// System.out.println("userRemarks:"+userRemarks);
			insertClaimProcessDtls.setDouble(4, tcServiceFee);
			// System.out.println("tcServiceFee:"+tcServiceFee);
			insertClaimProcessDtls.setDouble(5, wcServiceFee);
			// System.out.println("wcServiceFee:"+wcServiceFee);
			insertClaimProcessDtls.setDouble(6, tcClaimEligibleAmt);
			// System.out.println("tcClaimEligibleAmt:"+tcClaimEligibleAmt);
			insertClaimProcessDtls.setDouble(7, wcClaimEligibleAmt);
			// System.out.println("wcClaimEligibleAmt:"+wcClaimEligibleAmt);
			insertClaimProcessDtls.setDouble(8, tcFirstInstallment);
			// System.out.println("tcFirstInstallment:"+tcFirstInstallment);
			insertClaimProcessDtls.setDouble(9, wcFirstInstallment);
			// System.out.println("wcFirstInstallment:"+wcFirstInstallment);
			insertClaimProcessDtls.setDouble(10, totalTCOSAmountAsOnNPA);
			insertClaimProcessDtls.setDouble(11, tcrecovery);
			insertClaimProcessDtls.setDouble(12, totalWCOSAmountAsOnNPA);
			insertClaimProcessDtls.setDouble(13, wcrecovery);
			insertClaimProcessDtls.setDate(14,
					new java.sql.Date(dateofReceipt.getTime()));

			insertClaimProcessDtls.registerOutParameter(15, Types.VARCHAR);
			insertClaimProcessDtls.executeQuery();

			updateStatus = insertClaimProcessDtls.getInt(1);
			String error = insertClaimProcessDtls.getString(15);

			Log.log(Log.DEBUG, "CPDAO", methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				insertClaimProcessDtls.close();
				insertClaimProcessDtls = null;

				Log.log(Log.ERROR, "CPDAO", methodName, error);

				throw new DatabaseException(error);
			}
			insertClaimProcessDtls.close();
			insertClaimProcessDtls = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "CPDAO", methodName, exception.getMessage());
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "CPDAO", methodName, "Exited");
	}

	public void insertClaimProcessDetailsNew(String clmRefNum,
			String userRemarks, double tcServiceFee, double wcServiceFee,
			double tcClaimEligibleAmt, double wcClaimEligibleAmt,
			double tcFirstInstallment, double wcFirstInstallment,
			double totalTCOSAmountAsOnNPA, double totalWCOSAmountAsOnNPA,
			double tcrecovery, double wcrecovery, java.util.Date dateofReceipt,
			String isRefundable, double tcRF, double wcRF)
			throws DatabaseException {
		String methodName = "insertClaimProcessDetails";
		Connection connection = null;
		CallableStatement insertClaimProcessDtls = null;
		Log.log(Log.INFO, "CPDAO", methodName, "Entered");
		int updateStatus = 0;
		boolean newConn = false;
		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			insertClaimProcessDtls = connection
					.prepareCall("{?= call FuncInsClaimSetDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			
			System.out.println(" FuncInsClaimSetDetail ");
			insertClaimProcessDtls.registerOutParameter(1, Types.INTEGER);
			insertClaimProcessDtls.setString(2, clmRefNum);
			// System.out.println("clmRefNum:"+clmRefNum);
			insertClaimProcessDtls.setString(3, userRemarks);
			// System.out.println("userRemarks:"+userRemarks);
			insertClaimProcessDtls.setDouble(4, tcServiceFee);
			// System.out.println("tcServiceFee:"+tcServiceFee);
			insertClaimProcessDtls.setDouble(5, wcServiceFee);
			// System.out.println("wcServiceFee:"+wcServiceFee);
			insertClaimProcessDtls.setDouble(6, tcClaimEligibleAmt);
			// System.out.println("tcClaimEligibleAmt:"+tcClaimEligibleAmt);
			insertClaimProcessDtls.setDouble(7, wcClaimEligibleAmt);
			// System.out.println("wcClaimEligibleAmt:"+wcClaimEligibleAmt);
			insertClaimProcessDtls.setDouble(8, tcFirstInstallment);
			// System.out.println("tcFirstInstallment:"+tcFirstInstallment);
			insertClaimProcessDtls.setDouble(9, wcFirstInstallment);
			// System.out.println("wcFirstInstallment:"+wcFirstInstallment);
			insertClaimProcessDtls.setDouble(10, totalTCOSAmountAsOnNPA);
			insertClaimProcessDtls.setDouble(11, tcrecovery);
			insertClaimProcessDtls.setDouble(12, totalWCOSAmountAsOnNPA);
			insertClaimProcessDtls.setDouble(13, wcrecovery);
			insertClaimProcessDtls.setDate(14,
					new java.sql.Date(dateofReceipt.getTime()));
			if ("N".equals(isRefundable) || "Y".equals(isRefundable)) {
				insertClaimProcessDtls.setString(15, isRefundable);
			} else {
				insertClaimProcessDtls.setNull(15, java.sql.Types.VARCHAR);
			}
			insertClaimProcessDtls.setDouble(16, tcRF);
			insertClaimProcessDtls.setDouble(17, wcRF);
			insertClaimProcessDtls.registerOutParameter(18, Types.VARCHAR);
			insertClaimProcessDtls.executeQuery();

			updateStatus = insertClaimProcessDtls.getInt(1);
			String error = insertClaimProcessDtls.getString(18);

			Log.log(Log.DEBUG, "CPDAO", methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				insertClaimProcessDtls.close();
				insertClaimProcessDtls = null;

				Log.log(Log.ERROR, "CPDAO", methodName, error);

				throw new DatabaseException(error);
			}
			insertClaimProcessDtls.close();
			insertClaimProcessDtls = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "CPDAO", methodName, exception.getMessage());
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "CPDAO", methodName, "Exited");
	}

	/**
	 * 
	 * added this method sukumar@pathinfotech on 27-Feb-2010 for automatic SF
	 * dan generated for claim settled applications
	 * 
	 * @param clmRefNum
	 * @param mliId
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void generateSFDANforClaimSettledCases(String clmRefNum,
			String mliId, String userId) throws DatabaseException {
		String methodName = "generateSFDANforClaimSettledCases";
		Connection connection = null;
		CallableStatement generateSFDANforClaimSettledCase = null;
		Log.log(Log.INFO, "CPDAO", methodName, "Entered");
		int updateStatus = 0;
		boolean newConn = false;
		try {
			if (connection == null) {
				connection = DBConnection.getConnection();
				newConn = true;
			}
			// System.out.println(" Call funcGenSFForClaimApp- "+"Claim ref NO: "+clmRefNum+"userId: "+userId+" Member Id: "+mliId);
			generateSFDANforClaimSettledCase = connection
					.prepareCall("{?= call funcGenSFForClaimApp(?,?,?)}");
			generateSFDANforClaimSettledCase.registerOutParameter(1,
					Types.INTEGER);
			generateSFDANforClaimSettledCase.setString(2, clmRefNum);
			System.out.println("clmRefNum:" + clmRefNum);
			generateSFDANforClaimSettledCase.setString(3, userId);
			generateSFDANforClaimSettledCase.registerOutParameter(4,
					Types.VARCHAR);
			generateSFDANforClaimSettledCase.executeQuery();

			updateStatus = generateSFDANforClaimSettledCase.getInt(1);
			// System.out.println("updateStatus:"+updateStatus);
			String error = generateSFDANforClaimSettledCase.getString(4);

			Log.log(Log.DEBUG, "CPDAO", methodName, "updateStatus,error "
					+ updateStatus + "," + error);

			if (updateStatus == Constants.FUNCTION_FAILURE) {
				System.out.println("Error:" + error);
				generateSFDANforClaimSettledCase.close();
				generateSFDANforClaimSettledCase = null;

				Log.log(Log.ERROR, "CPDAO", methodName, error);

				throw new DatabaseException(error);
			}
			generateSFDANforClaimSettledCase.close();
			generateSFDANforClaimSettledCase = null;
		} catch (SQLException exception) {
			Log.log(Log.ERROR, "CPDAO", methodName, exception.getMessage());
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());

		} finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "CPDAO", methodName, "Exited");
	}

	/* capturing claim processing details part end here */

	public ClaimDetail getDetailsForClaimRefNumber(String claimRefNumber)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getDetailsForClaimRefNumber()", "Entered!");
		// System.out.println("Claim Ref Number :" + claimRefNumber);
		String mliname = null;
		String memberId = null;
		String ssiunitname = null;
		String borrowerId = null;
		java.util.Date dateAccntClassifiedAsNPA = null;
		java.util.Date dateOfReportingOfNPAtoCGTSI = null;
		String reasonsForAccntNPA = null;
		double osAmountAsOnNPA = 0.0;
		java.util.Date dateOfIssueOfRecallNotice = null;
		double totalTCOSAmount = 0.0;
		double totalWCOSAmount = 0.0;
		double totalOSAmntAsOnNPA = 0.0;
		
		// CGPAN Details
		// String cgpan = null;
		java.util.Date appApprvdDate = null;
		java.util.Date guaranteeStartDate = null;
		double tcSanctionedAmnt = 0.0;
		double wcFBSanctionedAmnt = 0.0;
		double wcNFBSanctionedAmnt = 0.0;
		double applicationApprvdAmnt = 0.0;
		double enhancedApprvdAmount = 0.0;
		java.lang.String applicationType = null;
		double claimAppliedAmount = 0.0;
		double totalClaimAppliedAmount = 0.0;
	

		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ClaimDetail claimdetail = new ClaimDetail();
		int status = -1;
		String errorCode = null;
		String legalForum = null;
		String legalForumName = null;
		String legalSuitNumber = null;
		String legalLocation = null;
		java.util.Date legalFilingDate = null;

		String womenOperated = null;

		String typeofActivity = null;
		String schemeName = null;
		// added by sukumar@path for capturing borrower state
		String stateName = null;
		String nerFlag = "N";

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClaimDetails.funcGetClaimDtl(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getDetailsForClaimRefNumber()",
						"SP returns a 1 in getting details for a Claim Ref Number.Error code is :"
								+ errorCode);
				callableStmt.close();
				System.out.println("Error Code:" + errorCode);
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				Vector cgpandtls = new Vector();
				HashMap details = null;
				resultset = (ResultSet) callableStmt.getObject(3);

				boolean detailsread = false;

				// reading the resultset
				while (resultset.next()) {
					// Extracting the values from the resultset
					mliname = resultset.getString(1);
					// System.out.println("Memeber Name :" + mliname);
					memberId = resultset.getString(2);
					// System.out.println("Memeber Id :" + memberId);
					borrowerId = resultset.getString(3);
					// System.out.println("Borrower Id :" + borrowerId);
					dateOfIssueOfRecallNotice = resultset.getDate(4);

					ssiunitname = resultset.getString(5);
					dateAccntClassifiedAsNPA = resultset.getDate(6);
					dateOfReportingOfNPAtoCGTSI = resultset.getDate(7);
					reasonsForAccntNPA = resultset.getString(8);
					osAmountAsOnNPA = resultset.getDouble(9);

					// CGPAN Wise Details
					String cgpan = resultset.getString(10);
					// added by sukumar@path on 29-08-2009 for capturing the
					// danSummary Report details for CGPAN
					ReportDAO rpDao = new ReportDAO();
					RpDAO rcDao = new RpDAO();
					double serviceFee = rcDao
							.calculateServiceFeeforCGPAN(cgpan);
					// System.out.println("Service Fee:"+serviceFee);
					ArrayList danDetails = rpDao
							.getCgpanHistoryReportDetails(cgpan);
					// danDetails.add(new Double(serviceFee));
					// System.out.println("CGPAN:"+cgpan);
					claimdetail.setDanSummaryReportDetails(cgpan, danDetails);
					// claimForm.setDanSummary(danDetails);

					applicationApprvdAmnt = (double) resultset.getDouble(11);
					// System.out.println("applicationApprvdAmnt1:"+applicationApprvdAmnt);
					// enhancedApprvdAmount = (double)resultset.getDouble(12);
					tcSanctionedAmnt = (double) resultset.getDouble(12);
					wcFBSanctionedAmnt = (double) resultset.getDouble(13);
					wcNFBSanctionedAmnt = (double) resultset.getDouble(14);
					applicationType = (String) resultset.getString(15);
					guaranteeStartDate = (java.util.Date) resultset.getDate(16);
					claimAppliedAmount = (double) resultset.getDouble(17);
					legalSuitNumber = (String) resultset.getString(18);
					legalForum = (String) resultset.getString(19);
					legalForumName = (String) resultset.getString(20);
					legalLocation = (String) resultset.getString(21);
					legalFilingDate = resultset.getDate(22);
					womenOperated = resultset.getString(23);
					typeofActivity = resultset.getString(24);
					schemeName = resultset.getString(25);
					stateName = resultset.getString(26);
					String handiCraft = resultset.getString(27);
					String dcHandiCraft = resultset.getString(28);
					nerFlag = checkBorrowerStateRegion(stateName);

					totalClaimAppliedAmount = totalClaimAppliedAmount
							+ claimAppliedAmount;

					appApprvdDate = (java.util.Date)resultset.getDate(29);
					
					// Populating the CGPAN wise details in the HashMap
					details = new HashMap();
					details.put(ClaimConstants.CLM_CGPAN, cgpan);
					details.put(ClaimConstants.CLM_APPLICATION_APPRVD_AMNT,
							new Double(applicationApprvdAmnt));
					// details.put(ClaimConstants.CLM_ENHANCED_APPRVD_AMNT,new
					// Double(enhancedApprvdAmount));
					details.put(ClaimConstants.CLM_TC_SANCTIONED_AMNT,
							new Double(tcSanctionedAmnt));
					details.put(ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT,
							new Double(wcFBSanctionedAmnt));
					details.put(ClaimConstants.CLM_WC_NFB_SANCTIONED_AMNT,
							new Double(wcNFBSanctionedAmnt));
					details.put(ClaimConstants.CGPAN_LOAN_TYPE, applicationType);
					details.put(ClaimConstants.CLM_GUARANTEE_START_DT,
							guaranteeStartDate);
					details.put(ClaimConstants.CLM_TOTAL_SERVICE_FEE,
							new Double(serviceFee));
					
					
					details.put(ClaimConstants.CLM_APPLICATION_APPRVD_DT, appApprvdDate);

					if (!cgpandtls.contains(details)) {
						cgpandtls.addElement(details);
					}

					if (detailsread) {
						continue;
					} else {
						// Populating the ClaimDetail object
						claimdetail.setMliName(mliname);
						claimdetail.setMliId(memberId);
						claimdetail.setBorrowerId(borrowerId);
						claimdetail
								.setDateOfIssueOfRecallNotice(dateOfIssueOfRecallNotice);
						claimdetail.setSsiUnitName(ssiunitname);
						claimdetail.setNpaDate(dateAccntClassifiedAsNPA);
						claimdetail
								.setDtOfNPAReportedToCGTSI(dateOfReportingOfNPAtoCGTSI);
						claimdetail.setReasonForTurningNPA(reasonsForAccntNPA);
						claimdetail
								.setOutstandingAmntAsOnNPADate(osAmountAsOnNPA);
						claimdetail.setLegalSuitNumber(legalSuitNumber);
						claimdetail.setLegalForum(legalForum);
						claimdetail.setLegalForumName(legalForumName);
						claimdetail.setLegalLocation(legalLocation);
						claimdetail.setLegalFilingDate(legalFilingDate);
						// added womenoperated and type of activity of borrower
						// by sukumar@path on 30-Dec-2010
						claimdetail.setWomenOperated(womenOperated);
						claimdetail.setTypeofActivity(typeofActivity);
						claimdetail.setSchemeName(schemeName);
						// claimdetail.setServiceFee(serviceFee);
						claimdetail.setStateName(stateName);
						claimdetail.setNerFlag(nerFlag);
						detailsread = true;
					}
				}
				claimdetail.setAppliedClaimAmt(totalClaimAppliedAmount);
				claimdetail.setCgpanDetails(cgpandtls);

				// Closing the ResultSet
				resultset.close();
				resultset = null;

				resultset = (ResultSet) callableStmt.getObject(4);

				detailsread = false;

				// reading the resultset
				while (resultset.next()) {
					// Extracting the values from the resultset
					mliname = resultset.getString(1);
					// System.out.println("Memeber Name :" + mliname);
					memberId = resultset.getString(2);
					// System.out.println("Memeber Id :" + memberId);
					borrowerId = resultset.getString(3);
					// System.out.println("Borrower Id :" + borrowerId);
					dateOfIssueOfRecallNotice = resultset.getDate(4);

					ssiunitname = resultset.getString(5);
					dateAccntClassifiedAsNPA = resultset.getDate(6);
					dateOfReportingOfNPAtoCGTSI = resultset.getDate(7);
					reasonsForAccntNPA = resultset.getString(8);
					osAmountAsOnNPA = resultset.getDouble(9);

					// CGPAN Wise Details
					String cgpan = resultset.getString(10);
					ReportDAO rpDao = new ReportDAO();
					RpDAO rcDao = new RpDAO();
					double serviceFee = rcDao
							.calculateServiceFeeforCGPAN(cgpan); // ------------------------

					System.out.println("serviceFee is " + serviceFee);

					double serviceFeeoneYear = rcDao
							.calculateserviceFeeForAsfDeduct(cgpan); // ----------------

					System.out.println("serviceFeeoneYear is "
							+ serviceFeeoneYear);

					double serviceFeeForOneYearDiff = serviceFeeoneYear
							- serviceFee;

					System.out.println("serviceFeeForOneYearDiff Fee:"
							+ serviceFeeForOneYearDiff);
					ArrayList danDetails = rpDao
							.getCgpanHistoryReportDetails(cgpan);
					// danDetails.add(new Double(serviceFee));
					claimdetail.setDanSummaryReportDetails(cgpan, danDetails);
					// System.out.println("CGPAN:"+cgpan);

					applicationApprvdAmnt = (double) resultset.getDouble(11);
					// System.out.println("applicationApprvdAmnt:"+applicationApprvdAmnt);
					// enhancedApprvdAmount = (double)resultset.getDouble(12);
					tcSanctionedAmnt = (double) resultset.getDouble(12);
					wcFBSanctionedAmnt = (double) resultset.getDouble(13);
					wcNFBSanctionedAmnt = (double) resultset.getDouble(14);
					applicationType = (String) resultset.getString(15);
					guaranteeStartDate = (java.util.Date) resultset.getDate(16);
					claimAppliedAmount = (double) resultset.getDouble(17);
					legalSuitNumber = (String) resultset.getString(18);
					legalForum = (String) resultset.getString(19);
					legalForumName = (String) resultset.getString(20);
					legalLocation = (String) resultset.getString(21);
					legalFilingDate = resultset.getDate(22);
					womenOperated = resultset.getString(23);
					typeofActivity = resultset.getString(24);
					schemeName = resultset.getString(25);
					stateName = resultset.getString(26);
					String handiCraft = resultset.getString(27);
					String dcHandiCraft = resultset.getString(28);
					nerFlag = checkBorrowerStateRegion(stateName);

					// System.out.println("totalClaimAppliedAmount:"+totalClaimAppliedAmount+" claimAppliedAmount:"+claimAppliedAmount
					// );
					totalClaimAppliedAmount = totalClaimAppliedAmount
							+ claimAppliedAmount;
					
					appApprvdDate = (java.util.Date)resultset.getDate(29);

					// Populating the CGPAN wise details in the HashMap
					details = new HashMap();
					details.put(ClaimConstants.CLM_CGPAN, cgpan);
					details.put(ClaimConstants.CLM_APPLICATION_APPRVD_AMNT,
							new Double(applicationApprvdAmnt));
					// details.put(ClaimConstants.CLM_ENHANCED_APPRVD_AMNT,new
					// Double(enhancedApprvdAmount));
					details.put(ClaimConstants.CLM_TC_SANCTIONED_AMNT,
							new Double(tcSanctionedAmnt));
					details.put(ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT,
							new Double(wcFBSanctionedAmnt));
					details.put(ClaimConstants.CLM_WC_NFB_SANCTIONED_AMNT,
							new Double(wcNFBSanctionedAmnt));
					details.put(ClaimConstants.CGPAN_LOAN_TYPE, applicationType);
					details.put(ClaimConstants.CLM_GUARANTEE_START_DT,
							guaranteeStartDate);
					details.put(ClaimConstants.CLM_TOTAL_SERVICE_FEE,
							new Double(serviceFee));
					details.put(
							ClaimConstants.CLM_TOTAL_SERVICE_FEE_FOR_ONE_YEAR,
							new Double(serviceFeeForOneYearDiff));
					// details.put(ClaimConstants.CLM_TOTAL_SERVICE_FEE_FOR_ONE_YEAR_WC,new
					// Double(serviceFeeForOneYearDiff));
					
					details.put(ClaimConstants.CLM_APPLICATION_APPRVD_DT, appApprvdDate);

					if (!cgpandtls.contains(details)) {
						cgpandtls.addElement(details);
					}

					if (detailsread) {
						continue;
					} else {
						// Populating the ClaimDetail object
						claimdetail.setMliName(mliname);
						claimdetail.setMliId(memberId);
						claimdetail.setBorrowerId(borrowerId);
						claimdetail
								.setDateOfIssueOfRecallNotice(dateOfIssueOfRecallNotice);
						claimdetail.setSsiUnitName(ssiunitname);
						if (dateAccntClassifiedAsNPA != null) {
							claimdetail.setNpaDate(dateAccntClassifiedAsNPA);
							claimdetail
									.setDtOfNPAReportedToCGTSI(dateOfReportingOfNPAtoCGTSI);
							claimdetail
									.setReasonForTurningNPA(reasonsForAccntNPA);
							claimdetail
									.setOutstandingAmntAsOnNPADate(osAmountAsOnNPA);
							claimdetail.setLegalSuitNumber(legalSuitNumber);
							claimdetail.setLegalForum(legalForum);
							claimdetail.setLegalForumName(legalForumName);
							claimdetail.setLegalLocation(legalLocation);
							claimdetail.setLegalFilingDate(legalFilingDate);
							claimdetail.setWomenOperated(womenOperated);
							claimdetail.setTypeofActivity(typeofActivity);
							claimdetail.setSchemeName(schemeName);
							// claimdetail.setServiceFee(serviceFee);
							claimdetail.setStateName(stateName);
							claimdetail.setNerFlag(nerFlag);
							claimdetail.setHandicraft(handiCraft);
							claimdetail.setDcHandicraft(dcHandiCraft);
						}

						detailsread = true;
					}
				}
				claimdetail.setAppliedClaimAmt(totalClaimAppliedAmount);
				claimdetail.setCgpanDetails(cgpandtls);

				// Closing the ResultSet
				resultset.close();
				resultset = null;

				// Closing the callable statement
				callableStmt.close();
			}

			// Extracting the Disbursement Details for the CGPAN(s) of the
			// Borrower
			Vector cgpandtls = claimdetail.getCgpanDetails();
			Vector updatedCgpanDetails = new Vector();

			for (int i = 0; i < cgpandtls.size(); i++) {
				HashMap hashmap = (HashMap) cgpandtls.elementAt(i);
				if (hashmap == null) {
					continue;
				}
				String cgpan = (String) hashmap.get(ClaimConstants.CLM_CGPAN);
				double disbursementAmnt = 0.0;
				java.util.Date disbursementDate = null;
				java.lang.String finalDisbursementFlag = null;
				double totalDisbursement = 0.0;
				java.util.Date lastDisbursementDt = null;

				if (cgpan != null) {
					callableStmt = conn
							.prepareCall("{? = call packGetPIDBRDtlsCGPAN.funcDBRDetailsForCGPAN(?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, cgpan);
					callableStmt.registerOutParameter(3, Constants.CURSOR);
					callableStmt
							.registerOutParameter(4, java.sql.Types.VARCHAR);

					// executing the callable statement
					callableStmt.execute();
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(4);

					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO",
								"getDetailsForClaimRefNumber()",
								"SP returns a 1 in getting Disbursement details for a CGPAN.Error code is :"
										+ errorCode);
						callableStmt.close();
						throw new DatabaseException(errorCode);
					} else if (status == Constants.FUNCTION_SUCCESS) {
						resultset = (ResultSet) callableStmt.getObject(3);
						int counter = 0;

						// Retrieving the values from the ResultSet
						while (resultset.next()) {
							disbursementAmnt = (double) resultset.getDouble(2);
							disbursementDate = (java.util.Date) resultset
									.getDate(3);
							finalDisbursementFlag = (String) resultset
									.getString(4);

							totalDisbursement = totalDisbursement
									+ disbursementAmnt;

							if (counter == 0) {
								if (disbursementDate != null) {
									lastDisbursementDt = disbursementDate;
								} else {
									continue;
								}
							} else if (counter > 0) {
								if (disbursementDate != null) {
									if ((disbursementDate
											.compareTo(lastDisbursementDt)) > 0) {
										lastDisbursementDt = disbursementDate;
									}
								} else {
									continue;
								}
							}
							counter++;
						}
						resultset.close();
						resultset = null;

						hashmap.put(ClaimConstants.CLM_TOTAL_DISBURSEMENT_AMNT,
								new Double(totalDisbursement));
						hashmap.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
								lastDisbursementDt);
						if (!updatedCgpanDetails.contains(hashmap)) {
							updatedCgpanDetails.addElement(hashmap);
						}
					}
				} else {
					continue;
				}
			}
			claimdetail.setCgpanDetails(updatedCgpanDetails);

			cgpandtls = null;
			updatedCgpanDetails = null;

			// Extracting the Service Fee Details for the CGPAN(s) of the
			// Borrower
			cgpandtls = claimdetail.getCgpanDetails();
			updatedCgpanDetails = new Vector();
			// amountRaised = 0.0;
			// java.util.Date dtOfServiceFeePaid = null;
			String previousCGPAN = null;
			java.util.Date lastDtOfServiceFeePaid = null;
			double lastServiceFeePaid = 0.0;
			Vector sfeedtls = new Vector();
			HashMap sfeedtl = null;

			callableStmt = conn
					.prepareCall("{? = call packGetSFeeDetail.funcGetServiceFeeDetail(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getDetailsForClaimRefNumber()",
						"SP returns a 1 in getting Service Fee details for a CGPAN.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Getting the ResultSet
				resultset = (ResultSet) callableStmt.getObject(3);
				int counter = 0;

				// Retrieving the values from the resultset
				while (resultset.next()) {
					String cgpan = (String) resultset.getString(1);
					double amountRaised = (double) resultset.getDouble(2);
					java.util.Date dtOfServiceFeePaid = (java.util.Date) resultset
							.getDate(3);

					if (counter == 0) {
						if (cgpan != null) {
							previousCGPAN = cgpan;
							lastDtOfServiceFeePaid = dtOfServiceFeePaid;
							lastServiceFeePaid = amountRaised;
						} else {
							continue;
						}
					} else if (counter > 0) {
						if (cgpan != null) {
							if (cgpan.equals(previousCGPAN)) {
								if (dtOfServiceFeePaid != null) {
									if (lastDtOfServiceFeePaid != null) {
										if ((dtOfServiceFeePaid
												.compareTo(lastDtOfServiceFeePaid)) > 0) {
											lastDtOfServiceFeePaid = dtOfServiceFeePaid;
											lastServiceFeePaid = amountRaised;
										}
									}
								}
							} else if (!cgpan.equals(previousCGPAN)) {
								sfeedtl = new HashMap();
								sfeedtl.put(ClaimConstants.CLM_CGPAN,
										previousCGPAN);
								sfeedtl.put(
										ClaimConstants.CLM_RP_AMOUNT_RAISED,
										new Double(lastServiceFeePaid));
								sfeedtl.put(
										ClaimConstants.CLM_RP_APPROPRIATION_DT,
										lastDtOfServiceFeePaid);
								if (!sfeedtls.contains(sfeedtl)) {
									sfeedtls.addElement(sfeedtl);
								}

								previousCGPAN = cgpan;
								lastDtOfServiceFeePaid = dtOfServiceFeePaid;
								lastServiceFeePaid = amountRaised;

							}
						} else {
							continue;
						}
					}
					counter++;
				}
				resultset.close();
				resultset = null;

				// System.out.println("Counter is :" + counter);

				if (counter > 0) {
					// Adding the last row in the resultset to the Vector
					sfeedtl = new HashMap();
					sfeedtl.put(ClaimConstants.CLM_CGPAN, previousCGPAN);
					sfeedtl.put(ClaimConstants.CLM_RP_AMOUNT_RAISED,
							new Double(lastServiceFeePaid));
					sfeedtl.put(ClaimConstants.CLM_RP_APPROPRIATION_DT,
							lastDtOfServiceFeePaid);
					if (!sfeedtls.contains(sfeedtl)) {
						sfeedtls.addElement(sfeedtl);
					}

					for (int i = 0; i < cgpandtls.size(); i++) {
						HashMap tempi = (HashMap) cgpandtls.elementAt(i);
						if (tempi == null) {
							continue;
						}
						String cgpani = (String) tempi
								.get(ClaimConstants.CLM_CGPAN);
						for (int j = 0; j < sfeedtls.size(); j++) {
							HashMap tempj = (HashMap) sfeedtls.elementAt(j);
							if (tempj == null) {
								continue;
							}
							String cgpanj = (String) tempj
									.get(ClaimConstants.CLM_CGPAN);

							if (cgpanj.equals(cgpani)) {
								double amountRaised = ((Double) tempj
										.get(ClaimConstants.CLM_RP_AMOUNT_RAISED))
										.doubleValue();
								lastDtOfServiceFeePaid = (java.util.Date) tempj
										.get(ClaimConstants.CLM_RP_APPROPRIATION_DT);
								tempi.put(ClaimConstants.CLM_RP_AMOUNT_RAISED,
										new Double(amountRaised));
								tempi.put(
										ClaimConstants.CLM_RP_APPROPRIATION_DT,
										lastDtOfServiceFeePaid);
							}
						}
						if (!updatedCgpanDetails.contains(tempi)) {
							updatedCgpanDetails.addElement(tempi);
						}
					}
					// Setting the Updated CGPAN Details Vector in ClaimDetail
					// object
					claimdetail.setCgpanDetails(updatedCgpanDetails);
				}
			}

			// Extracting the Term Credit Details
			double tcOSAmtForCGPAN = 0.0;
			// double totalTCOSAmtForCGPAN = 0.0;
			String cgpn = null;
			double principalAmnt = 0.0;
			double interestOtherCharges = 0.0;
			double osAsOnNPADate = 0.0;
			double osAsInCivilSuit = 0.0;
			double osAsInFirstClmLodgement = 0.0;
			callableStmt = conn
					.prepareCall("{? = call packGetTCDetails.funcGetTermCreditDtls(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"getDetailsForClaimRefNumber()",
						"SP returns a 1 in getting Term Credit details for a Claim Ref Number.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(3);

				// Retrieving the values from the ResultSet
				while (resultset.next()) {
					String cgpan = (String) resultset.getString(1);
					tcOSAmtForCGPAN = (double) resultset.getDouble(2);
					totalTCOSAmount = totalTCOSAmount + tcOSAmtForCGPAN;
				}
				resultset.close();
				resultset = null;
				// Retrieving the TC Outstanding details for the given claim ref
				// num
				java.util.Date dsbrsDate = null;
				resultset = (ResultSet) callableStmt.getObject(4);
				Vector tcOSDetails = new Vector();
				HashMap mp = null;
				while (resultset.next()) {
					mp = new HashMap();
					cgpn = (String) resultset.getString(1);
					dsbrsDate = (java.util.Date) resultset.getDate(2);
					principalAmnt = (double) resultset.getDouble(3);
					interestOtherCharges = (double) resultset.getDouble(4);
					osAsOnNPADate = (double) resultset.getDouble(5);
					osAsInCivilSuit = (double) resultset.getDouble(6);
					osAsInFirstClmLodgement = (double) resultset.getDouble(7);

					// Populating the values in the HashMap
					mp.put(ClaimConstants.CLM_CGPAN, cgpn);
					mp.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT, dsbrsDate);
					mp.put(ClaimConstants.CLM_REC_TC_PRINCIPAL, new Double(
							principalAmnt));
					mp.put(ClaimConstants.CLM_REC_TC_INTEREST, new Double(
							interestOtherCharges));
					mp.put(ClaimConstants.CLM_OS_AS_ON_NPA, new Double(
							osAsOnNPADate));
					mp.put(ClaimConstants.CLM_OS_AS_ON_CIVIL_SUIT, new Double(
							osAsInCivilSuit));
					mp.put(ClaimConstants.CLM_OS_AS_IN_CLM_LODGMNT, new Double(
							osAsInFirstClmLodgement));

					// Adding the HashMap to the Vector
					if (!tcOSDetails.contains(mp)) {
						tcOSDetails.addElement(mp);
					}
				}
				resultset.close();
				resultset = null;

				claimdetail.setTcDetails(tcOSDetails);
			}
			claimdetail.setTotalTCOSAmountAsOnNPA(totalTCOSAmount);

			// Extracting the Working Capital Details
			cgpandtls = claimdetail.getCgpanDetails();
			Vector wcDetails = new Vector();
			Vector wcOSDetails = new Vector();
			updatedCgpanDetails = new Vector();
			double wcOSAmtForCGPAN = 0.0;
			callableStmt = conn
					.prepareCall("{? = call packGetWCDetails.funcGetWorkingCapitalDtl(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"getDetailsForClaimRefNumber()",
						"SP returns a 1 in getting Working Capital details for a Claim Ref Number.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				resultset = (ResultSet) callableStmt.getObject(3);

				// Retrieving the values from the ResultSet
				while (resultset.next()) {
					String cgpan = (String) resultset.getString(1);
					java.util.Date dateOfReleaseOfWC = (java.util.Date) resultset
							.getDate(2);
					wcOSAmtForCGPAN = (double) resultset.getDouble(3);
					totalWCOSAmount = totalWCOSAmount + wcOSAmtForCGPAN;

					HashMap wcHashMap = new HashMap();
					wcHashMap.put(ClaimConstants.CLM_CGPAN, cgpan);
					wcHashMap.put(ClaimConstants.CLM_DT_OF_RELEASE_OF_WC,
							dateOfReleaseOfWC);

					// Adding the HashMap to the Vector
					if (!wcDetails.contains(wcHashMap)) {
						wcDetails.addElement(wcHashMap);
					}
				}
				resultset.close();
				resultset = null;

				// Retrieving the TC Outstanding details for the given claim ref
				// num
				resultset = (ResultSet) callableStmt.getObject(4);
				HashMap mp = null;
				while (resultset.next()) {
					mp = new HashMap();
					cgpn = (String) resultset.getString(1);
					osAsOnNPADate = (double) resultset.getDouble(2);
					osAsInCivilSuit = (double) resultset.getDouble(3);
					osAsInFirstClmLodgement = (double) resultset.getDouble(4);

					// Populating the values in the HashMap
					mp.put(ClaimConstants.CLM_CGPAN, cgpn);
					mp.put(ClaimConstants.CLM_OS_AS_ON_NPA, new Double(
							osAsOnNPADate));
					mp.put(ClaimConstants.CLM_OS_AS_ON_CIVIL_SUIT, new Double(
							osAsInCivilSuit));
					mp.put(ClaimConstants.CLM_OS_AS_IN_CLM_LODGMNT, new Double(
							osAsInFirstClmLodgement));

					// Adding the HashMap to the Vector
					if (!wcOSDetails.contains(mp)) {
						wcOSDetails.addElement(mp);
					}
				}
				resultset.close();
				resultset = null;
				claimdetail.setWcDetails(wcOSDetails);

				for (int i = 0; i < cgpandtls.size(); i++) {
					HashMap cgpandtlsHashMap = (HashMap) cgpandtls.elementAt(i);
					if (cgpandtlsHashMap == null) {
						continue;
					}
					String cgpanCgpandtls = (String) cgpandtlsHashMap
							.get(ClaimConstants.CLM_CGPAN);
					for (int j = 0; j < wcDetails.size(); j++) {
						HashMap wcDetailsHashMap = (HashMap) wcDetails
								.elementAt(j);
						if (wcDetailsHashMap == null) {
							continue;
						}
						String cgpanWcDetails = (String) wcDetailsHashMap
								.get(ClaimConstants.CLM_CGPAN);
						java.util.Date wcReleaseDate = (java.util.Date) wcDetailsHashMap
								.get(ClaimConstants.CLM_DT_OF_RELEASE_OF_WC);
						if (cgpanWcDetails.equals(cgpanCgpandtls)) {
							cgpandtlsHashMap.put(
									ClaimConstants.CLM_DT_OF_RELEASE_OF_WC,
									wcReleaseDate);
						}
					}
					if (!updatedCgpanDetails.contains(cgpandtlsHashMap)) {
						updatedCgpanDetails.addElement(cgpandtlsHashMap);
					}
				}

				claimdetail.setCgpanDetails(updatedCgpanDetails);
			}
			claimdetail.setTotalWCOSAmountAsOnNPA(totalWCOSAmount);
		} catch (Exception sqlexception) {
			// sqlexception.printStackTrace();
			Log.log(Log.ERROR, "CPDAO", "getDetailsForClaimRefNumber()",
					"Error retrieving Details for Claim Ref Number from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
			Log.log(Log.INFO, "CPDAO", "getDetailsForClaimRefNumber()",
					"Exited!");
		}
		return claimdetail;
	}

	public Vector getAllClaimsFiled() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllClaimsFiled()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		Vector allclaims = new Vector();

		int status = -1;
		String errorCode = null;

		String bid = null;
		String claimrefnumber = null;
		String memberId = null;
		HashMap claims = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetAllClaims.funcGetAllClaims(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, Constants.CURSOR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllClaimsFiled()",
						"SP returns a 1 in getting all claims filed.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Reading the resultset
				resultset = (ResultSet) callableStmt.getObject(2);
				while (resultset.next()) {
					claimrefnumber = resultset.getString(1);
					bid = resultset.getString(2);
					memberId = resultset.getString(3);

					// populating the hashmap
					claims = new HashMap();
					claims.put(ClaimConstants.CLM_CLAIM_REF_NUMBER,
							claimrefnumber);
					claims.put(ClaimConstants.CLM_BORROWER_ID, bid);
					claims.put(ClaimConstants.CLM_MEMBER_ID, memberId);

					// populating the vector with the hashmap
					if (!allclaims.contains(claims)) {
						allclaims.addElement(claims);
					}
				}
				// closing the resultset
				resultset.close();
				resultset = null;
			}
			// Closing the callable statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getAllClaimsFiled()",
					"Error retrieving all Claims Filed so far from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getAllClaimsFiled()", "Exited!");
		return allclaims;
	}

	/**
	 * 
	 * @param borrowerid
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public Vector getLockInDetails(String borrowerid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getLockInDetails()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		Vector lockindtlsvector = new Vector();

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetDtlsforLock.funcGetDtlsforLock(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerid);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getLockInDetails()",
						"SP returns a 1 in getting Lock-in details for a borrower.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Reading the cursor containing details from the main db
				resultset = (ResultSet) callableStmt.getObject(3);

				String cgpan = null;
				java.util.Date guaranteeStartDt = null;
				java.util.Date lastDisbursemntDt = null;
				String finalDisbursementFlag = null;
				HashMap details = null;

				// Reading the resultset
				while (resultset.next()) {
					details = new HashMap();
					cgpan = resultset.getString(1);
					java.sql.Date guaranteeStartDtSqlDt = resultset.getDate(2);
					java.sql.Date lastDisbursemntDtSqlDt = resultset.getDate(3);
					finalDisbursementFlag = resultset.getString(4);
					if (cgpan != null) {
						if (guaranteeStartDtSqlDt != null) {
							guaranteeStartDt = new java.util.Date(
									guaranteeStartDtSqlDt.getTime());
						} else {
							continue;
						}
						/*
						 * if(finalDisbursementFlag.equals(ClaimConstants.
						 * DISBRSMNT_NO_FLAG)) { throw new DatabaseException(
						 * "Final Disbursement Details not available for CGPAN :"
						 * + cgpan +
						 * ". \nPlease furnish the Disbursement Details."); }
						 */
						if (lastDisbursemntDtSqlDt != null) {
							lastDisbursemntDt = new java.util.Date(
									lastDisbursemntDtSqlDt.getTime());
						} else {
							// throw new
							// DatabaseException("Last Disbursement Details not available for CGPAN :"
							// + cgpan +
							// "/nPlease furnish the Disbursement Details.");
							continue;
						}
						details.put(ClaimConstants.CLM_CGPAN, cgpan);
						details.put(ClaimConstants.CLM_GUARANTEE_START_DT,
								guaranteeStartDt);
						details.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
								lastDisbursemntDt);
					} else {
						continue;
					}
					lockindtlsvector.addElement(details);
					details = null;
				}
				// Closing the resultset
				resultset.close();
				resultset = null;

				// Reading the WC cursor
				resultset = (ResultSet) callableStmt.getObject(4);
				while (resultset.next()) {
					details = new HashMap();
					cgpan = (String) resultset.getString(1);
					java.sql.Date guaranteeStartDtSqlDt = (java.sql.Date) resultset
							.getDate(2);
					if (cgpan != null) {
						details.put(ClaimConstants.CLM_CGPAN, cgpan);
						if (guaranteeStartDtSqlDt != null) {
							guaranteeStartDt = new java.util.Date(
									guaranteeStartDtSqlDt.getTime());
							details.put(ClaimConstants.CLM_GUARANTEE_START_DT,
									guaranteeStartDt);
						} else {
							continue;
						}
					}
					lockindtlsvector.addElement(details);
					details = null;
				}
				// Closing the resultset
				resultset.close();
				resultset = null;

				// Retrieving the Cursor containing details from the temp db
				resultset = (ResultSet) callableStmt.getObject(5);

				String ccCgpan = null;
				java.util.Date ccGuaranteeStartDt = null;
				java.util.Date ccLastDisbursemntDt = null;
				String ccFinalDisbursementFlag = null;
				HashMap ccDetails = null;

				// Reading the resultset
				while (resultset.next()) {
					ccDetails = new HashMap();
					ccCgpan = resultset.getString(1);
					java.sql.Date ccGuaranteeStartDtSqlDt = resultset
							.getDate(2);
					java.sql.Date ccLastDisbursemntDtSqlDt = resultset
							.getDate(3);
					ccFinalDisbursementFlag = resultset.getString(4);
					if (ccCgpan != null) {
						boolean jumpOut = false;
						for (int k = 0; k < lockindtlsvector.size(); k++) {
							HashMap map = (HashMap) lockindtlsvector
									.elementAt(k);
							if (map == null) {
								continue;
							}
							String thispan = (String) map
									.get(ClaimConstants.CLM_CGPAN);
							if (thispan == null) {
								continue;
							}
							if (thispan.equals(ccCgpan)) {
								java.util.Date tempDate = (java.util.Date) map
										.get(ClaimConstants.CLM_LAST_DISBURSEMENT_DT);
								map = (HashMap) lockindtlsvector.remove(k);
								map = null;
								if (tempDate == null) {
									continue;
								}
								map = new HashMap();
								map.put(ClaimConstants.CLM_CGPAN, thispan);
								map.put(ClaimConstants.CLM_GUARANTEE_START_DT,
										ccGuaranteeStartDtSqlDt);
								if ((tempDate
										.compareTo(ccLastDisbursemntDtSqlDt)) > 0) {
									map.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
											tempDate);
								}
								if ((tempDate
										.compareTo(ccLastDisbursemntDtSqlDt)) < 0) {
									map.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
											ccLastDisbursemntDtSqlDt);
								}
								if ((tempDate
										.compareTo(ccLastDisbursemntDtSqlDt)) == 0) {
									map.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
											ccLastDisbursemntDtSqlDt);
								}
								lockindtlsvector.addElement(map);
								jumpOut = true;
								break;
							}
						}
						if (jumpOut) {
							continue;
						}
						if (ccGuaranteeStartDtSqlDt != null) {
							ccGuaranteeStartDt = new java.util.Date(
									ccGuaranteeStartDtSqlDt.getTime());
						} else {
							continue;
						}
						/*
						 * if(finalDisbursementFlag.equals(ClaimConstants.
						 * DISBRSMNT_NO_FLAG)) { throw new DatabaseException(
						 * "Final Disbursement Details not available for CGPAN :"
						 * + cgpan +
						 * ". \nPlease furnish the Disbursement Details."); }
						 */
						if (ccLastDisbursemntDtSqlDt != null) {
							ccLastDisbursemntDt = new java.util.Date(
									ccLastDisbursemntDtSqlDt.getTime());
						} else {
							// throw new
							// DatabaseException("Last Disbursement Details not available for CGPAN :"
							// + cgpan +
							// "/nPlease furnish the Disbursement Details.");
							continue;
						}
						ccDetails.put(ClaimConstants.CLM_CGPAN, ccCgpan);
						ccDetails.put(ClaimConstants.CLM_GUARANTEE_START_DT,
								ccGuaranteeStartDt);
						ccDetails.put(ClaimConstants.CLM_LAST_DISBURSEMENT_DT,
								ccLastDisbursemntDt);
					} else {
						continue;
					}
					lockindtlsvector.addElement(ccDetails);
					ccDetails = null;
				}
				// Closing the resultset
				resultset.close();
				resultset = null;
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getLockInDetails()",
					"Error retrieving Lock-in details for a borrower from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}

		Log.log(Log.INFO, "CPDAO", "getLockInDetails()", "Exited!");
		for (int i = 0; i < lockindtlsvector.size(); i++) {
			HashMap h = (HashMap) lockindtlsvector.elementAt(i);
			// System.out.println("Printing the map :" + h);
			Log.log(Log.INFO, "CPDAO", "getLockInDetails()",
					"Printing Hashmap from CPDAO :" + h);
		}
		return lockindtlsvector;
	}

	/**
	 * This method is used to save the settlement details.
	 */
	public void saveSettlementDetails(Vector details, Vector voucherdtls,
			String createdBy, ChequeDetails chequeDetails, String contextPath)
			throws DatabaseException, MessageException {
		Log.log(Log.INFO, "CPDAO", "saveSettlementDetails()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		RpProcessor rpprocessor = new RpProcessor();
		RpDAO rpDAO = new RpDAO();

		IFDAO ifDAO = new IFDAO();

		String errorCode = null;
		int status = -1;

		SettlementDetail settlmntDtl = null;
		String cgclan = null;
		String finalSettlemntFlag = null;
		double tierOneSettlement = 0.0;
		java.util.Date tierOneSettlmntDt = null;
		double tierTwoSettlement = 0.0;
		java.util.Date tierTwoSettlmntDt = null;
		String whichInstallment;

		try {
			conn = DBConnection.getConnection(false);

			for (int i = 0; i < details.size(); i++) {
				settlmntDtl = (SettlementDetail) details.elementAt(i);
				if (settlmntDtl == null) {
					continue;
				}

				whichInstallment = settlmntDtl.getWhichInstallment();

				if (whichInstallment.equals(ClaimConstants.FIRST_INSTALLMENT)) {
					cgclan = settlmntDtl.getCgclan();
					// System.out.println("CGCLAN :" + cgclan);
					finalSettlemntFlag = settlmntDtl.getFinalSettlementFlag();
					// System.out.println("FINAL SettlementFlag :" +
					// finalSettlemntFlag);
					tierOneSettlement = settlmntDtl.getTierOneSettlement();
					// System.out.println("tierOneSettlement :" +
					// tierOneSettlement);
					tierOneSettlmntDt = settlmntDtl.getTierOneSettlementDt();
					// System.out.println("tierOneSettlmntDt :" +
					// tierOneSettlmntDt);
					tierTwoSettlement = settlmntDtl.getTierTwoSettlement();
					// System.out.println("tierTwoSettlement :" +
					// tierTwoSettlement);
					tierTwoSettlmntDt = settlmntDtl.getTierTwoSettlementDt();
					// System.out.println("tierTwoSettlmntDt :" +
					// tierTwoSettlmntDt);
					// conn = DBConnection.getConnection();
					callableStmt = conn
							.prepareCall("{? = call funcInsertSettDtl(?,?,?,?,?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, cgclan);
					callableStmt.setDouble(3, tierOneSettlement);
					if (tierOneSettlmntDt != null) {
						callableStmt.setDate(4, new java.sql.Date(
								tierOneSettlmntDt.getTime()));
					} else {
						callableStmt.setNull(4, java.sql.Types.DATE);
					}

					callableStmt.setDouble(5, tierTwoSettlement);
					if (tierTwoSettlmntDt != null) {
						callableStmt.setDate(6, new java.sql.Date(
								tierTwoSettlmntDt.getTime()));
					} else {
						callableStmt.setNull(6, java.sql.Types.DATE);
					}

					callableStmt.setString(7, finalSettlemntFlag);
					callableStmt.setString(8, createdBy);
					callableStmt
							.registerOutParameter(9, java.sql.Types.VARCHAR);

					// executing the callable statement
					callableStmt.execute();
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(9);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
								"SP returns a 1 in saving Settlement details.Error code is :"
										+ errorCode);

						// Closing the Callable Statement
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							//
						}
						throw new DatabaseException(errorCode);
					}
				} else if (whichInstallment
						.equals(ClaimConstants.SECOND_INSTALLMENT)) {
					cgclan = settlmntDtl.getCgclan();

					finalSettlemntFlag = settlmntDtl.getFinalSettlementFlag();

					tierTwoSettlement = settlmntDtl.getTierTwoSettlement();

					tierTwoSettlmntDt = settlmntDtl.getTierTwoSettlementDt();

					// conn = DBConnection.getConnection();
					callableStmt = conn
							.prepareCall("{? = call funcUpdateSettDtl(?,?,?,?,?,?)}");
					callableStmt
							.registerOutParameter(1, java.sql.Types.INTEGER);
					callableStmt.setString(2, cgclan);
					callableStmt.setDouble(3, tierTwoSettlement);
					if (tierTwoSettlmntDt != null) {
						callableStmt.setDate(4, new java.sql.Date(
								tierTwoSettlmntDt.getTime()));
					} else {
						callableStmt.setNull(4, java.sql.Types.DATE);
					}
					callableStmt.setString(5, finalSettlemntFlag);
					callableStmt.setString(6, createdBy);
					callableStmt
							.registerOutParameter(7, java.sql.Types.VARCHAR);

					// executing the callable statement
					callableStmt.execute();
					status = callableStmt.getInt(1);
					errorCode = callableStmt.getString(7);
					if (status == Constants.FUNCTION_FAILURE) {
						Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
								"SP returns a 1 in saving Settlement details.Error code is :"
										+ errorCode);

						// Closing the Callable Statement
						callableStmt.close();
						try {
							conn.rollback();
						} catch (SQLException sqlex) {
							Log.log(Log.ERROR, "CPDAO",
									"saveSettlementDetails()",
									"Error rolling back transaction. Error code is :"
											+ sqlex.getMessage());
						}
						throw new DatabaseException(errorCode);
					}
				}

			}
			for (int i = 0; i < voucherdtls.size(); i++) {
				VoucherDetail vd = (VoucherDetail) voucherdtls.elementAt(i);
				if (vd == null) {
					continue;
				}
				rpDAO.insertVoucherDetails(vd, createdBy, conn);

			}
			if (chequeDetails != null) {
				ifDAO.chequeDetailsInsertSuccess(chequeDetails, conn,
						contextPath, createdBy);
			}

			conn.commit();
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
					"Error saving Settlement Details into the database.");
			try {
				conn.rollback();
			} catch (SQLException sqlex) {
				Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
						"Error rolling back the SQL Changes.");
			}
			throw new DatabaseException(sqlexception.getMessage());
		} catch (DatabaseException dbexception) {
			Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
					"Error saving Settlement Details into the database.");
			try {
				conn.rollback();
			} catch (SQLException sqlex) {
				Log.log(Log.ERROR, "CPDAO", "saveSettlementDetails()",
						"Error rolling back the SQL Changes.");
			}
			throw dbexception;
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveSettlementDetails()", "Exited!");
	}

	public double getCumulativeClaims(Application application)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getCumulativeClaims()", "Entered!");
		Connection connection = null;
		CallableStatement getCumulativeClaimsStmt = null;
		ResultSet resultSet = null;

		int status = -1;
		double cumulativeClaims = 0;

		try {
			String exception = "";

			connection = DBConnection.getConnection();
			getCumulativeClaimsStmt = connection
					.prepareCall("{?=call funcGetCumulativeClaim(?,?,?,?,?,?)}");
			getCumulativeClaimsStmt.registerOutParameter(1, Types.INTEGER);
			getCumulativeClaimsStmt.setString(2, application.getBankId());
			getCumulativeClaimsStmt.setString(3, application.getZoneId());
			getCumulativeClaimsStmt.setString(4, application.getBranchId());
			getCumulativeClaimsStmt.registerOutParameter(5, Types.DOUBLE);
			getCumulativeClaimsStmt.registerOutParameter(6, Types.VARCHAR);
			getCumulativeClaimsStmt.execute();

			cumulativeClaims = getCumulativeClaimsStmt.getDouble(5);
			status = getCumulativeClaimsStmt.getInt(1);
			exception = getCumulativeClaimsStmt.getString(6);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getCumulativeClaims()",
						"SP returns a 1 in getting Cumulative Claims details.Error code is :"
								+ exception);
				getCumulativeClaimsStmt.close();
				throw new DatabaseException(exception);
			}
			getCumulativeClaimsStmt.close();
		} catch (Exception exception) {
			Log.log(Log.ERROR, "CPDAO", "getCumulativeClaims()",
					"Error retrieving Cumulative Claim Details from the database.");
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "CPDAO", "getCumulativeClaims()", "Exited!");
		return cumulativeClaims;
	}

	// The method return installment flag. If the user has already filed claim
	// first
	// installment application, it returns 'F'. If it already has filed second
	// claim
	// installment application, it files 'S'.

	public HashMap getClmRefAndFlagDtls(String bankId, String zoneId,
			String branchId, String borrowerId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClmRefAndFlagDtls()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;
		HashMap results = new HashMap();

		String clmRefNumber = null;
		String clmStatus = null;
		String cgclan = null;
		String installmentFlag = null;
		String whetherFinalInstallment;
		String firstSettlementDate;
		String secondSettlementDate;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClmRefAndFlagDtl.funcGetGetClmRefAndFlagDtl(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.setString(5, borrowerId);
			callableStmt.registerOutParameter(6, Constants.CURSOR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR,
						"CPDAO",
						"getClmRefAndFlagDtls()",
						"SP returns a 1 in getting Claim Ref Number and Installment Flag.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Retrieving the resultset
				rs = (ResultSet) callableStmt.getObject(6);
				String formattedFirstValue = "";
				String formattedSecondValue = "";
				boolean isFirstSettlementDtRead = false;
				boolean isSecondSettlementDtRead = false;

				// retrieving the values from the resulset
				while (rs.next()) {
					clmRefNumber = rs.getString(2);
					clmStatus = rs.getString(3);
					cgclan = rs.getString(4);
					// System.out.println("CGCLAN :" + cgclan);

					installmentFlag = rs.getString(5);
					firstSettlementDate = rs.getString(6);
					if (firstSettlementDate == null) {
						firstSettlementDate = "0";
					}
					secondSettlementDate = rs.getString(7);
					if (secondSettlementDate == null) {
						secondSettlementDate = "0";
					}
					whetherFinalInstallment = rs.getString(8);

					if (installmentFlag
							.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						if (cgclan != null) {
							if (!firstSettlementDate.equals("0")) {
								formattedFirstValue = installmentFlag
										+ ClaimConstants.CLM_DELIMITER1
										+ cgclan
										+ ClaimConstants.CLM_DELIMITER1
										+ firstSettlementDate;
								isFirstSettlementDtRead = true;
							} else {
								if (!isFirstSettlementDtRead) {
									firstSettlementDate = ClaimConstants.CLM_NO_VALUE;
									formattedFirstValue = installmentFlag
											+ ClaimConstants.CLM_DELIMITER1
											+ cgclan
											+ ClaimConstants.CLM_DELIMITER1
											+ firstSettlementDate;
								}
							}
						} else if ((cgclan == null)
								&& ((clmStatus
										.equals(ClaimConstants.CLM_PENDING_STATUS))
										|| (clmStatus
												.equals(ClaimConstants.CLM_FORWARD_STATUS)) || (clmStatus
										.equals(ClaimConstants.CLM_HOLD_STATUS)))) {
							formattedFirstValue = null;
						} else if ((cgclan == null)
								&& (clmStatus
										.equals(ClaimConstants.CLM_REJECT_STATUS))) {
							formattedFirstValue = ClaimConstants.CLM_DELIMITER4;
						}
						if ((whetherFinalInstallment != null)
								&& (whetherFinalInstallment.equals("Y"))) {
							formattedFirstValue = ClaimConstants.CLM_DELIMITER5;
						}
					} else if (installmentFlag
							.equals(ClaimConstants.SECOND_INSTALLMENT)) {
						if (cgclan != null) {
							if (!secondSettlementDate.equals("0")) {
								formattedSecondValue = installmentFlag
										+ ClaimConstants.CLM_DELIMITER1
										+ cgclan
										+ ClaimConstants.CLM_DELIMITER1
										+ secondSettlementDate;
								isSecondSettlementDtRead = true;
							} else {
								if (!isSecondSettlementDtRead) {
									secondSettlementDate = ClaimConstants.CLM_NO_VALUE;
									formattedSecondValue = installmentFlag
											+ ClaimConstants.CLM_DELIMITER1
											+ cgclan
											+ ClaimConstants.CLM_DELIMITER1
											+ secondSettlementDate;
								}
							}
						} else if ((cgclan == null)
								&& ((clmStatus
										.equals(ClaimConstants.CLM_PENDING_STATUS))
										|| (clmStatus
												.equals(ClaimConstants.CLM_FORWARD_STATUS)) || (clmStatus
										.equals(ClaimConstants.CLM_HOLD_STATUS)))) {
							formattedSecondValue = null;
						} else if ((cgclan == null)
								&& (clmStatus
										.equals(ClaimConstants.CLM_REJECT_STATUS))) {
							formattedSecondValue = ClaimConstants.CLM_DELIMITER4;
						}
					}
					results.put(ClaimConstants.FIRST_INSTALLMENT,
							formattedFirstValue);
					results.put(ClaimConstants.SECOND_INSTALLMENT,
							formattedSecondValue);
				}
				rs.close();
				rs = null;
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR,
					"CPDAO",
					"getClmRefAndFlagDtls()",
					"Error retrieving Claim Ref Number and the corresponding flag from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClmRefAndFlagDtls()", "Exited!");
		return results;
	}

	public Vector getSettlementAdviceDetail(String memberId, String flag)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getSettlementAdviceDetail()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;
		HashMap settlmntAdviceDtl = null;
		Vector settlementAdviceDtls = new Vector();

		String bankId = null;
		String zoneId = null;
		String branchId = null;
		String cgclan = null;
		double firstSettlmntTierAmnt = 0.0;
		java.util.Date firstSettlmntTierDt = null;
		double secondSettlmntTierAmnt = 0.0;
		java.util.Date secondSettlmntTierDt = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetSettlementDtl.funcGetSettlementDtl(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, memberId);
			callableStmt.setString(3, flag);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getSettlementAdviceDetail()",
						"SP returns a 1 in getting Settlement Advice Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Retrieving the Resultset
				rs = (ResultSet) callableStmt.getObject(4);
				while (rs.next()) {
					bankId = rs.getString(1);
					zoneId = rs.getString(2);
					branchId = rs.getString(3);
					cgclan = rs.getString(4);
					firstSettlmntTierAmnt = rs.getDouble(6);
					firstSettlmntTierDt = rs.getDate(7);
					secondSettlmntTierAmnt = rs.getDouble(8);
					secondSettlmntTierDt = rs.getDate(9);

					settlmntAdviceDtl = new HashMap();

					// Populating the HashMap
					settlmntAdviceDtl.put(ClaimConstants.CLM_MEMBER_ID,
							memberId);
					settlmntAdviceDtl.put(ClaimConstants.CLM_CGCLAN, cgclan);
					settlmntAdviceDtl.put(
							ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
							new Double(firstSettlmntTierAmnt));
					settlmntAdviceDtl.put(
							ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
							firstSettlmntTierDt);
					settlmntAdviceDtl.put(
							ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_AMNT,
							new Double(secondSettlmntTierAmnt));
					settlmntAdviceDtl.put(
							ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_DT,
							secondSettlmntTierDt);

					// Adding the HashMap to the Vector
					settlementAdviceDtls.addElement(settlmntAdviceDtl);
				}

				// Closing the ResultSet
				rs.close();
				rs = null;
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getSettlementAdviceDetail()",
					"Error retrieving Settlement Advice Details from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getSettlementAdviceDetail()", "Exited!");
		return settlementAdviceDtls;
	}

	public void saveSettlementAdviceDetail(Vector settlemntAdviceDtls,
			String userid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveSettlementAdviceDetail()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		String cgclan = null;
		String cgcsa = null;
		String voucherid = null;
		String flag = null;

		try {
			conn = DBConnection.getConnection();
			for (int i = 0; i < settlemntAdviceDtls.size(); i++) {

				HashMap dtl = (HashMap) settlemntAdviceDtls.elementAt(i);
				if (dtl == null) {
					continue;
				}
				cgclan = (String) dtl.get(ClaimConstants.CLM_CGCLAN);
				// System.out.println("Printing CGClan :" + cgclan);
				cgcsa = generateCGCSANumber();
				// System.out.println("Printing CGCSA :" + cgcsa);
				voucherid = (String) dtl.get(ClaimConstants.CLM_VOUCHER_ID);
				// System.out.println("Printing Voucher Id :" + voucherid);
				flag = (String) dtl.get(ClaimConstants.INSTALLMENT_FLAG);
				// System.out.println("Printing Flag :" + flag);

				callableStmt = conn
						.prepareCall("{? = call funcInsertSettAdivceDtl(?,?,?,?,?,?)}");
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				callableStmt.setString(2, cgclan);
				callableStmt.setString(3, cgcsa);
				callableStmt.setString(4, voucherid);
				callableStmt.setString(5, flag);
				callableStmt.setString(6, userid);
				callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				// executing the callable statement
				callableStmt.execute();
				status = callableStmt.getInt(1);
				errorCode = callableStmt.getString(7);
				if (status == Constants.FUNCTION_FAILURE) {
					Log.log(Log.ERROR, "CPDAO", "saveSettlementAdviceDetail()",
							"SP returns a 1 in saving Settlement Advice Details.Error code is :"
									+ errorCode);
					// Closing the Callable Statement
					callableStmt.close();
					try {
						conn.rollback();
					} catch (SQLException sqlex) {
						Log.log(Log.ERROR, "CPDAO",
								"saveSettlementAdviceDetail()", "Error :"
										+ sqlex.getMessage());
					}
					throw new DatabaseException(errorCode);
				}
			}

		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "saveSettlementAdviceDetail()",
					"Error saving Settlement Advice Details into the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveSettlementAdviceDetail()", "Exited!");
	}

	public String generateCGCSANumber() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "generateCGCSANumber()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		String cgcsanumber = null;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn.prepareCall("{? = call funcGenCGCSA(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "generateCGCSANumber()",
						"SP returns a 1 in generating CGCSA Number.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				cgcsanumber = callableStmt.getString(2);
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "generateCGCSANumber()",
					"Error generating CGCSA Number.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "generateCGCSANumber()", "Exited!");
		return cgcsanumber;
	}

	public void updateSettlementDetail(HashMap settlementDtls, String flag,
			String userid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "updateSettlementDetail()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;
		String cgclan = null;
		double settlementAmnt = 0.0;
		java.util.Date settlementDt = null;

		if (settlementDtls.containsKey(ClaimConstants.CLM_CGCLAN)) {
			cgclan = (String) settlementDtls.get(ClaimConstants.CLM_CGCLAN);
		}
		if (settlementDtls
				.containsKey(ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_AMNT)) {
			settlementAmnt = ((Double) settlementDtls
					.get(ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_AMNT))
					.doubleValue();
		}
		if (settlementDtls
				.containsKey(ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_DT)) {
			settlementDt = (java.util.Date) settlementDtls
					.get(ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_DT);
		}

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call funcUpdateSettDtl(?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, cgclan);
			callableStmt.setDouble(3, settlementAmnt);
			if (settlementDt != null) {
				callableStmt.setDate(4,
						new java.sql.Date(settlementDt.getTime()));
			} else {
				callableStmt.setNull(4, java.sql.Types.DATE);
			}
			callableStmt.setString(5, flag);
			callableStmt.setString(6, userid);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(7);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "updateSettlementDetail()",
						"SP returns a 1 in updating Settlement Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "updateSettlementDetail()",
					"Error updating Settlement Details.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "updateSettlementDetail()", "Exited!");
	}

	/**
	 * This method updates Legal Proceedings Details for the Borrower
	 */
	public void updateLegalProceedingDetails(LegalProceedingsDetail legaldtls)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "updateLegalProceedingDetails()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		String borrowerId = legaldtls.getBorrowerId();
		String forumThruWhichInitiated = legaldtls
				.getForumRecoveryProceedingsInitiated();
		String suitCaseNumber = legaldtls.getSuitCaseRegNumber();
		java.util.Date filingDt = legaldtls.getFilingDate();
		String forumName = legaldtls.getNameOfForum();
		String location = legaldtls.getLocation();
		double amntClaimed = legaldtls.getAmountClaimed();
		String currentStatus = legaldtls.getCurrentStatusRemarks();
		String recoveryFlag = legaldtls.getIsRecoveryProceedingsConcluded();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetInsUpdLegalDetail.funcUpdateLegalDetail(?,?,?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.setString(3, forumThruWhichInitiated);
			callableStmt.setString(4, suitCaseNumber);
			if (filingDt != null) {
				callableStmt.setDate(5, new java.sql.Date(filingDt.getTime()));
			} else {
				callableStmt.setNull(5, java.sql.Types.DATE);
			}
			callableStmt.setString(6, forumName);
			callableStmt.setString(7, location);
			callableStmt.setDouble(8, amntClaimed);
			callableStmt.setString(9, currentStatus);
			callableStmt.setString(10, recoveryFlag);
			callableStmt.registerOutParameter(11, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(11);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "updateLegalProceedingDetails()",
						"SP returns a 1 in updating Legal Proceedings Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "updateLegalProceedingDetails()",
					"Error updating Legal Proceeding Details.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "updateLegalProceedingDetails()", "Exited!");
	}

	public HashMap getFirstClmDtlForBid(String bankId, String zoneId,
			String branchId, String borrowerId) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getFirstClmDtlForBid()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		String clmRefNumber = null;
		String cgclan = null;
		java.util.Date tierOneSettlmntDt = null;
		double firstClmApprvdAmt = 0.0;
		java.util.Date firstClmApprvdDt = null;
		java.util.Date clmRecallNoticeDt = null;

		HashMap clmDtl = new HashMap();
		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetFirstClmDtlForBid.funcGetFirstClmDtlForBid(?,?,?,?,?,?,?,?,?,?,?)}");
			
			System.out.println("7343   packGetFirstClmDtlForBid.funcGetFirstClmDtlForBid");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bankId);
			callableStmt.setString(3, zoneId);
			callableStmt.setString(4, branchId);
			callableStmt.setString(5, borrowerId);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStmt.registerOutParameter(8, java.sql.Types.DATE);
			callableStmt.registerOutParameter(9, java.sql.Types.DOUBLE);
			callableStmt.registerOutParameter(10, java.sql.Types.DATE);
			callableStmt.registerOutParameter(11, java.sql.Types.DATE);
			callableStmt.registerOutParameter(12, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(12);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getFirstClmDtlForBid()",
						"SP returns a 1 in getting Claim Details for First Installment.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				clmRefNumber = callableStmt.getString(6);
				cgclan = callableStmt.getString(7);
				tierOneSettlmntDt = callableStmt.getDate(8);
				firstClmApprvdAmt = callableStmt.getDouble(9);
				firstClmApprvdDt = callableStmt.getDate(10);
				clmRecallNoticeDt = callableStmt.getDate(11);

				clmDtl.put(ClaimConstants.CLM_CLAIM_REF_NUMBER, clmRefNumber);
				clmDtl.put(ClaimConstants.CLM_CGCLAN, cgclan);
				clmDtl.put(ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
						tierOneSettlmntDt);
				clmDtl.put(ClaimConstants.CLM_FIRST_CLM_APPRVD_AMT, new Double(
						firstClmApprvdAmt));
				clmDtl.put(ClaimConstants.CLM_FIRST_CLM_APPRVD_DT,
						firstClmApprvdDt);
				clmDtl.put(ClaimConstants.CLM_RECALL_NOTICE, clmRecallNoticeDt);
			}
			// Closing the Callable Statement
			callableStmt.close();
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getFirstClmDtlForBid()",
					"Error retrieving First Clm Dtls for the Borrower.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getFirstClmDtlForBid()", "Exited!");
		return clmDtl;
	}

	public boolean isClaimSettlementMade(String borrowerId) {
		return true;
	}

	public Vector getClaimAppliedAmounts(String borrowerid, String flag)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimAppliedAmounts", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		Vector clmappliedamnts = new Vector();
		int status = -1;
		String cgpan = null;
		double clmAppliedAmnt = 0.0;
		String errorCode = null;
		ResultSet rs = null;
		HashMap dtl = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClmAppliedAmnts.funcGetClmAppliedAmnts(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerid);
			callableStmt.setString(3, flag);
			callableStmt.registerOutParameter(4, Constants.CURSOR);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimAppliedAmounts()",
						"SP returns a 1 in getting Claim Applied Amounts. Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				rs = (ResultSet) callableStmt.getObject(4);
				while (rs.next()) {
					dtl = new HashMap();
					cgpan = rs.getString(1);
					clmAppliedAmnt = rs.getDouble(2);
					dtl.put(ClaimConstants.CLM_CGPAN, cgpan);
					dtl.put(ClaimConstants.CGPAN_CLM_APPLIED_AMNT, new Double(
							clmAppliedAmnt));
					clmappliedamnts.addElement(dtl);
				}
				rs.close();
				rs = null;
			}
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getClaimAppliedAmounts()",
					"Error retrieving claim applied amounts. Error is :"
							+ sqlexception.getMessage());
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimAppliedAmounts", "Exited");
		return clmappliedamnts;
	}

	/*
	 * This method returns a vector of Hashmaps. Each HashMap in the Vector
	 * contains CGPAN-wise OTS Request Details.
	 */

	public Vector getOTSReferenceDetailsForBorrower(String borrowerId)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getOTSReferenceDetailsForBorrower()",
				"Entered");
		Vector otsreqdetails = new Vector();
		Connection conn = null;
		CallableStatement callableStmt = null;
		ResultSet rs = null;
		HashMap otsreqdetail = null;

		// Properties being retrieved from the Cursor
		String cgpan = null;
		double approvedAmnt = 0.0;
		double enhancedApprvdAmnt = 0.0;
		double tcSantionedAmnt = 0.0;
		double wcFBLimit = 0.0;
		double wcNFBLimit = 0.0;
		String bid = null;
		String otsreason = null;
		String willfulDefaulter = null;
		java.util.Date otsReqDt = null;
		double borrowerProposedAmnt = 0.0;
		double proposedScrificedAmnt = 0.0;
		double outstandingAmnt = 0.0;

		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{?=call packGetOTSReqDetails.funcGetOTSRequestDetails(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO",
						"getOTSReferenceDetailsForBorrower()",
						"SP returns a 1 in retrieving OTS Request Details. Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				rs = (ResultSet) callableStmt.getObject(3);
				while (rs.next()) {
					cgpan = rs.getString(1);
					approvedAmnt = rs.getDouble(2);
					enhancedApprvdAmnt = rs.getDouble(3);
					tcSantionedAmnt = rs.getDouble(4);
					wcFBLimit = rs.getDouble(5);
					wcNFBLimit = rs.getDouble(6);
					bid = rs.getString(7);
					otsreason = rs.getString(8);
					willfulDefaulter = rs.getString(9);
					otsReqDt = rs.getDate(10);
					borrowerProposedAmnt = rs.getDouble(11);
					proposedScrificedAmnt = rs.getDouble(12);
					outstandingAmnt = rs.getDouble(13);

					// Storing the values in a HashMap
					otsreqdetail = new HashMap();
					otsreqdetail.put(ClaimConstants.CLM_CGPAN, cgpan);
					otsreqdetail.put(
							ClaimConstants.CLM_APPLICATION_APPRVD_AMNT,
							new Double(approvedAmnt));
					otsreqdetail.put(ClaimConstants.CLM_ENHANCED_APPRVD_AMNT,
							new Double(enhancedApprvdAmnt));
					otsreqdetail.put(ClaimConstants.CLM_TC_SANCTIONED_AMNT,
							new Double(tcSantionedAmnt));
					otsreqdetail.put(ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT,
							new Double(wcFBLimit));
					otsreqdetail.put(ClaimConstants.CLM_WC_NFB_SANCTIONED_AMNT,
							new Double(wcNFBLimit));
					otsreqdetail.put(ClaimConstants.CLM_BORROWER_ID, bid);
					otsreqdetail.put(ClaimConstants.CLM_OTS_REASON_FOR_OTS,
							otsreason);
					otsreqdetail.put(ClaimConstants.CLM_OTS_WILLFUL_DEFAULTER,
							willfulDefaulter);
					otsreqdetail.put(ClaimConstants.CLM_OTS_REQUEST_DATE,
							otsReqDt);
					otsreqdetail
							.put(ClaimConstants.CLM_OTS_TOTAL_BORROWER_PROPOSED_AMNT,
									new Double(borrowerProposedAmnt));
					otsreqdetail.put(
							ClaimConstants.CLM_OTS_TOTAL_PROPOSED_SCRFCE_AMNT,
							new Double(proposedScrificedAmnt));
					otsreqdetail.put(ClaimConstants.CLM_OTS_TOTAL_OS_AMNT,
							new Double(outstandingAmnt));

					// Adding the HashMap into the Vector
					otsreqdetails.addElement(otsreqdetail);
				}
				rs.close();
				rs = null;
			}
			// Closing the Callable Statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.INFO, "CPDAO", "getOTSReferenceDetailsForBorrower()",
					"There is an error. Error is :" + sqlexception.getMessage());
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getOTSReferenceDetailsForBorrower()",
				"Exited");
		return otsreqdetails;
	}

	/*
	 * This method gets the Claim Settlement Details for a given Borrower Id
	 */

	public HashMap getClaimSettlementDetailForBorrower(String borrowerId)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimSettlementDetailForBorrower()",
				"Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;
		HashMap settlementDetails = new HashMap();

		int status = -1;
		String errorCode = null;

		double firstSettlementAmnt = 0.0;
		java.util.Date firstSettlementDt = null;
		double secSettlementAmnt = 0.0;
		java.util.Date secSettlementDt = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetClaimSettlementForBid.funcGetClaimSettlementForBid(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO",
						"getClaimSettlementDetailForBorrower()",
						"SP returns a 1 in retrieving Claim Settlement Details. Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				rs = (ResultSet) callableStmt.getObject(3);
				while (rs.next()) {
					firstSettlementAmnt = rs.getDouble(1);
					firstSettlementDt = (java.util.Date) rs.getDate(2);
					secSettlementAmnt = rs.getDouble(3);
					secSettlementDt = (java.util.Date) rs.getDate(4);
				}
				settlementDetails.put(
						ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_AMNT,
						new Double(firstSettlementAmnt));
				settlementDetails.put(
						ClaimConstants.CLM_SETTLMNT_FIRST_SETTLMNT_DT,
						firstSettlementDt);
				settlementDetails.put(
						ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_AMNT,
						new Double(secSettlementAmnt));
				settlementDetails.put(
						ClaimConstants.CLM_SETTLMNT_SECOND_SETTLMNT_DT,
						secSettlementDt);

				// Closing the resultset
				rs.close();
				rs = null;
			}
			// Closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimSettlementDetailForBorrower()",
				"Exited");
		return settlementDetails;
	}

	/*
	 * This method gets Service Fee Details for a given Claim Reference Number.
	 * The return type for the method is Vector of HashMap(s). Each HashMap will
	 * be containing service fee details.
	 */

	public Vector getServiceFeeDetails(String claimRefNumber)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getServiceFeeDetails", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;

		int status = -1;
		String errorCode = null;

		Vector serviceFeeDetails = new Vector();
		HashMap serviceFeeDtl = null;
		String cgpan = null;
		double amountRaised = 0.0;
		java.util.Date appropriationDate = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = packGetServiceFeeDetail.funcGetServiceFeeDetail(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			// Executing the Callable Statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getServiceFeeDetails",
						"SP returns a 1 for funcGetServiceFeeDetail SP. Error is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else {
				// Retrieving the ResultSet object
				rs = (ResultSet) callableStmt.getObject(3);

				// Reading the values from the ResultSet
				while (rs.next()) {
					cgpan = (String) rs.getString(1);
					amountRaised = (double) rs.getDouble(2);
					appropriationDate = (java.util.Date) rs.getDate(3);

					// Populating the HashMap
					serviceFeeDtl = new HashMap();
					serviceFeeDtl.put(ClaimConstants.CLM_CGPAN, cgpan);
					serviceFeeDtl.put(ClaimConstants.CLM_RP_AMOUNT_RAISED,
							new Double(amountRaised));
					serviceFeeDtl.put(ClaimConstants.CLM_RP_APPROPRIATION_DT,
							appropriationDate);

					// Adding the HashMap to the Vector
					serviceFeeDetails.addElement(serviceFeeDtl);
				}
				rs.close();
				rs = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getServiceFeeDetails",
					"Exception in getServiceFeeDetails method. Exception is :"
							+ sqlexception.getMessage());
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.ERROR, "CPDAO", "getServiceFeeDetails", "Exited");
		return serviceFeeDetails;
	}

	/*
	 * This method gets the details for the Working Capital Application(s). The
	 * return type for the method is Vector of HashMaps.
	 */

	public Vector getWorkingCapitalDetails(String claimRefNumber)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getWorkingCapitalDetails", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;

		int status = -1;
		String errorCode = null;

		Vector workingCapitalDetails = new Vector();
		HashMap workingCapitalDtl = null;
		String cgpan = null;
		double sanctionedAmnt = 0.0;
		double outstandingAmntAsOnNPA = 0.0;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = packGetWCDetails.funcGetWorkingCapitalDtl(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			// Executing the Callable Statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getWorkingCapitalDetails",
						"SP returns a 1 for funcGetWorkingCapitalDtl SP. Error is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else {
				// Retrieving the ResultSet object
				rs = (ResultSet) callableStmt.getObject(3);

				// Reading the values from the ResultSet
				while (rs.next()) {
					cgpan = (String) rs.getString(1);
					sanctionedAmnt = (double) rs.getDouble(2);
					outstandingAmntAsOnNPA = (double) rs.getDouble(3);

					// Populating the HashMap
					workingCapitalDtl = new HashMap();
					workingCapitalDtl.put(ClaimConstants.CLM_CGPAN, cgpan);
					workingCapitalDtl.put(
							ClaimConstants.CLM_WC_FB_SANCTIONED_AMNT,
							new Double(sanctionedAmnt));
					workingCapitalDtl.put(
							ClaimConstants.CLM_OS_AMT_AS_ON_NPA_DATE,
							new Double(outstandingAmntAsOnNPA));

					// Adding the HashMap to the Vector
					workingCapitalDetails.addElement(workingCapitalDtl);
				}
				rs.close();
				rs = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getWorkingCapitalDetails()",
					"Exception in getWorkingCapitalDetails method. Exception is :"
							+ sqlexception.getMessage());
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getWorkingCapitalDetails()", "Exited");
		return workingCapitalDetails;
	}

	/*
	 * This method gets the details for the Term Credit Application(s). The
	 * return type for the method is Vector of HashMaps.
	 */

	public Vector getTermCreditDetails(String claimRefNumber)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getTermCreditDetails", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet rs = null;

		int status = -1;
		String errorCode = null;

		Vector termCreditDetails = new Vector();
		HashMap termCreditDtl = null;
		String cgpan = null;
		double outstandingAmntAsOnNPA = 0.0;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = packGetTCDetails.funcGetTermCreditDtls(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, claimRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			// Executing the Callable Statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getTermCreditDetails",
						"SP returns a 1 for funcGetTermCreditDtls SP. Error is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else {
				// Retrieving the ResultSet object
				rs = (ResultSet) callableStmt.getObject(3);

				// Reading the values from the ResultSet
				while (rs.next()) {
					cgpan = (String) rs.getString(1);
					outstandingAmntAsOnNPA = (double) rs.getDouble(2);

					// Populating the HashMap
					termCreditDtl = new HashMap();
					termCreditDtl.put(ClaimConstants.CLM_CGPAN, cgpan);
					termCreditDtl.put(ClaimConstants.CLM_OS_AMT_AS_ON_NPA_DATE,
							new Double(outstandingAmntAsOnNPA));

					// Adding the HashMap to the Vector
					termCreditDetails.addElement(termCreditDtl);
				}
				rs.close();
				rs = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getTermCreditDetails",
					"Exception in getTermCreditDetails method. Exception is :"
							+ sqlexception.getMessage());
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getTermCreditDetails", "Exited");
		return termCreditDetails;
	}

	public Vector getAllVoucherIds() throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getAllVoucherIds()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		Vector allVouchers = new Vector();

		int status = -1;
		String errorCode = null;

		int voucherId = 0;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetAllVoucherIds.funcGetAllVoucherIds(?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, Constants.CURSOR);
			callableStmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(3);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAllVoucherIds()",
						"SP returns a 1 in getting all claims filed.Error code is :"
								+ errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// Reading the resultset
				resultset = (ResultSet) callableStmt.getObject(2);
				while (resultset.next()) {
					voucherId = resultset.getInt(1);
					if (!allVouchers.contains(new Integer(voucherId))) {
						allVouchers.addElement(new Integer(voucherId));
					}
				}
				// closing the resultset
				resultset.close();
				resultset = null;
			}
			// Closing the callable statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getAllVoucherIds()",
					"Error retrieving all Claims Filed so far from the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getAllVoucherIds()", "Exited!");
		return allVouchers;
	}

	private void updateNPADetails(NPADetails details) throws DatabaseException {
		Log.log(Log.ERROR, "CPDAO", "updateNPADetails()", "Entered.");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		String borrowerId = details.getCgbid();
		// System.out.println("Borrower Id passed to UpdateNPADtls SP :" +
		// borrowerId);
		java.util.Date npaEffectiveDt = details.getNpaDate();
		// System.out.println("npaEffectiveDt :" + npaEffectiveDt);
		String cgtsiReportingFlag = details.getWhetherNPAReported();
		// System.out.println("cgtsiReportingFlag :" + cgtsiReportingFlag);
		String cgtsiReportingMode = details.getCgtsiReportingMode();
		java.util.Date cgtsiReportingDate = details.getReportingDate();
		String reference = details.getReference();
		double osAmnt = details.getOsAmtOnNPA();
		String reasonForNPA = details.getNpaReason();
		String mliRemarks = details.getEffortsTaken();
		String recoveryInitiated = details.getIsRecoveryInitiated();
		int noOfActions = details.getNoOfActions();
		java.util.Date actionCompletionDt = details.getEffortsConclusionDate();
		String mliRemarksOnFinPosition = details.getMliCommentOnFinPosition();
		String dtlsOnFinAssistance = details.getDetailsOfFinAssistance();
		String enhanceSupport = details.getCreditSupport();
		String bankFacilityDtl = details.getBankFacilityDetail();
		String willfulDefaulter = details.getWillfulDefaulter();
		// System.out.println("Willful Defauleter Flag :" + willfulDefaulter);
		String watchListFlag = details.getPlaceUnderWatchList();
		String monitoringDtls = null;
		String remarks = details.getRemarksOnNpa();
		java.util.Date recConcludingDt = details.getDtOfConclusionOfRecProc();
		String whetherWrittenOff = details.getWhetherWrittenOff();
		java.util.Date writtenOffDate = details.getDtOnWhichAccntWrittenOff();

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call funcUpdateNPADtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			if (npaEffectiveDt != null) {
				callableStmt.setDate(3,
						new java.sql.Date(npaEffectiveDt.getTime()));
			} else {
				callableStmt.setNull(3, java.sql.Types.DATE);
			}
			callableStmt.setString(4, cgtsiReportingFlag);
			if (cgtsiReportingMode == null) {
				callableStmt.setNull(5, java.sql.Types.VARCHAR);
			} else {
				callableStmt.setString(5, cgtsiReportingMode);
			}
			if (cgtsiReportingDate != null) {
				callableStmt.setDate(6,
						new java.sql.Date(cgtsiReportingDate.getTime()));
			} else {
				callableStmt.setNull(6, java.sql.Types.DATE);
			}
			callableStmt.setString(7, reference);
			callableStmt.setDouble(8, osAmnt);
			callableStmt.setString(9, reasonForNPA);
			callableStmt.setString(10, mliRemarks);
			callableStmt.setString(11, recoveryInitiated);
			callableStmt.setInt(12, noOfActions);
			if (actionCompletionDt != null) {
				callableStmt.setDate(13,
						new java.sql.Date(actionCompletionDt.getTime()));
			} else {
				callableStmt.setNull(13, java.sql.Types.DATE);
			}
			callableStmt.setString(14, mliRemarksOnFinPosition);
			callableStmt.setString(15, dtlsOnFinAssistance);
			callableStmt.setString(16, enhanceSupport);
			callableStmt.setString(17, bankFacilityDtl);
			callableStmt.setString(18, willfulDefaulter);
			callableStmt.setString(19, watchListFlag);
			callableStmt.setString(20, monitoringDtls);
			callableStmt.setString(21, remarks);
			if (recConcludingDt != null) {
				callableStmt.setDate(22,
						new java.sql.Date(recConcludingDt.getTime()));
			} else {
				callableStmt.setNull(22, java.sql.Types.DATE);
			}
			callableStmt.setString(23, whetherWrittenOff);
			if (writtenOffDate != null) {
				callableStmt.setDate(24,
						new java.sql.Date(writtenOffDate.getTime()));
			} else {
				callableStmt.setNull(24, java.sql.Types.DATE);
			}
			callableStmt.registerOutParameter(25, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(25);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "updateNPADetails()",
						"SP returns a 1 in updating NPA Details.Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			// Closing the Callable Statement
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			// sqlexception.printStackTrace();
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.ERROR, "CPDAO", "updateNPADetails()", "Exited.");
	}

	public HashMap getPrimarySecurityAndNetworthOfGuarantors(String borrowerId,
			String memberId) throws DatabaseException {
		Log.log(Log.ERROR, "CPDAO",
				"getPrimarySecurityAndNetworthOfGuarantors()", "Entered.");
		// System.out.println("Control in getPrimarySecurityAndNetworthOfGuarantors method.");
		Vector cgpans = getCGPANDetailsForBorrowerId(borrowerId, memberId);
		HashMap cgpanDtl = null;
		String cgpan = null;
		String appRefNumber = null;

		double totalNetWorth = 0.0;
		double totalValOfLand = 0.0;
		double totalValOfMachine = 0.0;
		double totalValOfBuilding = 0.0;
		double totalValOfOFMA = 0.0;
		double totalValOfCurrAssets = 0.0;
		double totalValOfOthers = 0.0;

		CallableStatement callableStmt = null;
		Connection conn = null;
		HashMap completeDtls = new HashMap();
		if (cgpans == null) {
			return null;
		}
		try {
			conn = DBConnection.getConnection();
			for (int j = 0; j < cgpans.size(); j++) {
				cgpanDtl = (HashMap) cgpans.elementAt(j);
				if (cgpanDtl == null) {
					continue;
				}
				// System.out.println("From CPDAO -> Printing HashMap :" +
				// cgpanDtl);
				cgpan = (String) cgpanDtl.get(ClaimConstants.CLM_CGPAN);
				if (cgpan == null) {
					continue;
				}
				appRefNumber = getAppRefNumber(cgpan);
				if (appRefNumber == null) {
					continue;
				}
				// System.out.println("1");
				callableStmt = conn
						.prepareCall("{?=call packGetPersonalGuarantee.funcGetPerGuarforAppRef(?,?,?)}");

				callableStmt.setString(2, appRefNumber); // Application Ref
															// Number

				callableStmt.registerOutParameter(1, Types.INTEGER);
				callableStmt.registerOutParameter(4, Types.VARCHAR);

				callableStmt.registerOutParameter(3, Constants.CURSOR);
				// System.out.println("2");
				callableStmt.executeQuery();
				// System.out.println("3");
				int status = callableStmt.getInt(1);

				Log.log(Log.DEBUG, "CPDAO",
						"getPrimarySecurityAndNetworthOfGuarantors", "Status :"
								+ status);

				if (status == Constants.FUNCTION_FAILURE) {
					// System.out.println("4");
					String error = callableStmt.getString(4);

					callableStmt.close();
					callableStmt = null;

					conn.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"getPrimarySecurityAndNetworthOfGuarantors",
							"Error Message:" + error);

					throw new DatabaseException(error);
				} else {
					// System.out.println("5");
					ResultSet guarantorsResults = (ResultSet) callableStmt
							.getObject(3);
					int i = 0;
					while (guarantorsResults.next()) {
						// System.out.println("6");
						if (i == 0) {
							totalNetWorth = totalNetWorth
									+ guarantorsResults.getDouble(2);
						}
						if (i == 1) {
							totalNetWorth = totalNetWorth
									+ guarantorsResults.getDouble(2);
						}
						if (i == 2) {
							totalNetWorth = totalNetWorth
									+ guarantorsResults.getDouble(2);
						}
						if (i == 3) {
							totalNetWorth = totalNetWorth
									+ guarantorsResults.getDouble(2);
						}
						i++;
					}
					// System.out.println("7");
					guarantorsResults.close();
					guarantorsResults = null;

					callableStmt.close();
					callableStmt = null;
				}
			}
			// System.out.println("8");
			completeDtls.put(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR,
					new Double(totalNetWorth));
			// System.out.println("1 -> Printing Complete Details :"
			// +completeDtls );
			for (int i = 0; i < cgpans.size(); i++) {
				cgpanDtl = (HashMap) cgpans.elementAt(i);
				if (cgpanDtl == null) {
					continue;
				}
				// System.out.println("From CPDAO -> Printing HashMap :" +
				// cgpanDtl);
				cgpan = (String) cgpanDtl.get(ClaimConstants.CLM_CGPAN);
				if (cgpan == null) {
					continue;
				}
				appRefNumber = getAppRefNumber(cgpan);
				if (appRefNumber == null) {
					continue;
				}
				// Retrieving the Primary Security Details
				callableStmt = conn
						.prepareCall("{?=call packGetPrimarySecurity.funcGetPriSecforAppRef(?,?,?)}");

				callableStmt.setString(2, appRefNumber); // Application
															// Reference Number

				callableStmt.registerOutParameter(1, Types.INTEGER);
				callableStmt.registerOutParameter(4, Types.VARCHAR);

				callableStmt.registerOutParameter(3, Constants.CURSOR);

				callableStmt.executeQuery();
				int status = callableStmt.getInt(1);

				if (status == Constants.FUNCTION_FAILURE) {

					String error = callableStmt.getString(4);

					callableStmt.close();
					callableStmt = null;

					conn.rollback();

					throw new DatabaseException(error);
				} else {
					ResultSet psResults = (ResultSet) callableStmt.getObject(3);
					while (psResults.next()) {
						if ((psResults.getString(1)).equals("Land")) {
							totalValOfLand = totalValOfLand
									+ psResults.getDouble(3);
						}
						if ((psResults.getString(1)).equals("Building")) {
							totalValOfBuilding = totalValOfBuilding
									+ psResults.getDouble(3);

						}
						if ((psResults.getString(1)).equals("Machinery")) {
							totalValOfMachine = totalValOfMachine
									+ psResults.getDouble(3);

						}
						if ((psResults.getString(1)).equals("Fixed Assets")) {
							totalValOfOFMA = totalValOfOFMA
									+ psResults.getDouble(3);

						}
						if ((psResults.getString(1)).equals("Current Assets")) {
							totalValOfCurrAssets = totalValOfCurrAssets
									+ psResults.getDouble(3);

						}
						if ((psResults.getString(1)).equals("Others")) {
							totalValOfOthers = totalValOfOthers
									+ psResults.getDouble(3);

						}

					}
					psResults.close();
					psResults = null;
					callableStmt.close();
					callableStmt = null;
				}
			}

		} catch (SQLException sqlex) {
			// sqlex.getCause();
			// sqlex.printStackTrace();
			Log.log(Log.ERROR, "CPDAO",
					"getPrimarySecurityAndNetworthOfGuarantors()", "Error :"
							+ sqlex.getMessage());
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}

		completeDtls.put(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND, new Double(
				totalValOfLand));
		completeDtls.put(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG, new Double(
				totalValOfBuilding));
		completeDtls.put(ClaimConstants.CLM_SAPGD_PARTICULAR_MC, new Double(
				totalValOfMachine));
		completeDtls.put(
				ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS,
				new Double(totalValOfOFMA));
		completeDtls.put(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS,
				new Double(totalValOfCurrAssets));
		completeDtls.put(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS,
				new Double(totalValOfOthers));

		// System.out.println("2- >Printing Complete Details :" +completeDtls );
		Log.log(Log.ERROR, "CPDAO",
				"getPrimarySecurityAndNetworthOfGuarantors()", "Exited");
		return completeDtls;
	}

	public HashMap getPrimarySecurityAndNetworthOfGuarantors(String npaId)
			throws DatabaseException {
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		HashMap sancmap = new HashMap();
		HashMap npamap = new HashMap();
		HashMap map = new HashMap();

		conn = DBConnection.getConnection();

		try {
			stmt = conn
					.prepareCall("{?=call packgetprimarysecurity.funcGetPriSecforAppRefasonNPA(?,?,?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.registerOutParameter(3, Constants.CURSOR);
			stmt.registerOutParameter(4, Types.VARCHAR);
			stmt.setString(2, npaId);
			stmt.executeQuery();

			int status = stmt.getInt(1);

			if (status == Constants.FUNCTION_FAILURE) {

				String error = stmt.getString(4);

				stmt.close();
				stmt = null;

				conn.rollback();

				throw new DatabaseException(error);
			} else {
				rs = (ResultSet) stmt.getObject(3);
				while (rs.next()) {
					// logic to populate map values
					String flag = (String) rs.getString(1);
					Double networth = (Double) rs.getDouble(2);
					String reason = (String) rs.getString(3);

					if ("SAN".equals(flag)) {
						// add to sanc map
						sancmap.put("networth", networth);
						sancmap.put("reasonReduction", reason);
					}
					if ("NPA".equals(flag)) {
						// add to npa map
						npamap.put("networth", networth);
						npamap.put("reasonReduction", reason);
					}
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			throw new DatabaseException();
		}

		try {
			conn = DBConnection.getConnection();
			stmt = conn
					.prepareCall("{?=call packgetprimarysecurity.funcGetPriSecPartforArnAsOnNPA(?,?,?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.registerOutParameter(3, Constants.CURSOR);
			stmt.registerOutParameter(4, Types.VARCHAR);
			stmt.setString(2, npaId);

			stmt.executeQuery();

			int status = stmt.getInt(1);
			if (status == Constants.FUNCTION_FAILURE) {

				String error = stmt.getString(4);

				stmt.close();
				stmt = null;

				conn.rollback();

				throw new DatabaseException(error);
			} else {
				rs = (ResultSet) stmt.getObject(3);

				while (rs.next()) {
					// logic to populate map values
					if ("SAN".equals((String) rs.getString(2))) {
						if ("LAND".equals((String) rs.getString(1))) {
							// add to sanc map
							sancmap.put("LAND", (Double) rs.getDouble(3));

						}
						if ("BUILDING".equals((String) rs.getString(1))) {
							// add to sanc map
							sancmap.put("BUILDING", (Double) rs.getDouble(3));

						}
						if ("MACHINE".equals((String) rs.getString(1))) {
							// add to sanc map
							sancmap.put("MACHINE", (Double) rs.getDouble(3));

						}
						if ("OTHER FIXED MOVABLE ASSETS".equals((String) rs
								.getString(1))) {
							// add to sanc map
							sancmap.put("OTHER FIXED MOVABLE ASSETS",
									(Double) rs.getDouble(3));

						}
						if ("CUR_ASSETS".equals((String) rs.getString(1))) {
							// add to sanc map
							sancmap.put("CURRENT ASSETS",
									(Double) rs.getDouble(3));

						}
						if ("OTHERS".equals((String) rs.getString(1))) {
							// add to sanc map
							sancmap.put("OTHERS", (Double) rs.getDouble(3));

						}
					}

					if ("NPA".equals((String) rs.getString(2))) {
						if ("LAND".equals((String) rs.getString(1))) {

							npamap.put("LAND", (Double) rs.getDouble(3));

						}
						if ("BUILDING".equals((String) rs.getString(1))) {

							npamap.put("BUILDING", (Double) rs.getDouble(3));

						}
						if ("MACHINE".equals((String) rs.getString(1))) {

							npamap.put("MACHINE", (Double) rs.getDouble(3));

						}
						if ("OTHER FIXED MOVABLE ASSETS".equals((String) rs
								.getString(1))) {

							npamap.put("OTHER FIXED MOVABLE ASSETS",
									(Double) rs.getDouble(3));

						}
						if ("CUR_ASSETS".equals((String) rs.getString(1))) {

							npamap.put("CURRENT ASSETS",
									(Double) rs.getDouble(3));

						}
						if ("OTHERS".equals((String) rs.getString(1))) {

							npamap.put("OTHERS", (Double) rs.getDouble(3));

						}
					}

				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			throw new DatabaseException();
		}
		map.put("SAN", sancmap);
		map.put("NPA", npamap);
		return map;
	}

	public String getAppRefNumber(String cgpan) throws DatabaseException {
		Log.log(Log.ERROR, "CPDAO", "getAppRefNumber()", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = null;
		int status = -1;
		String errorCode = null;
		String appRefNumber = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				Log.log(Log.ERROR, "CPDAO", "getAppRefNumber()",
						"Conection is null");
			}

			callableStmt = conn
					.prepareCall("{? = call funcgetapprefnoforcgpan(?,?,?,?)}");
			// callableStmt =
			// conn.prepareCall("{? = call funcgetapprefnoforcgpanappdao(?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			callableStmt.setNull(3, java.sql.Types.VARCHAR);
			callableStmt.setString(4, cgpan);
			callableStmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			// System.out.println("cgpan:"+cgpan);
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(5);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getAppRefNumber()",
						"SP returns a 1 in getting all claims filed.Error code is :"
								+ errorCode);
				callableStmt.close();
				// throw new DatabaseException(errorCode);
			}
			appRefNumber = callableStmt.getString(2);
			// System.out.println("appRefNumber:"+appRefNumber);
		} catch (SQLException sqlex) {
			Log.log(Log.ERROR, "CPDAO", "getAppRefNumber()",
					"Error :" + sqlex.getMessage());
			// sqlex.printStackTrace();
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.ERROR, "CPDAO", "getAppRefNumber()", "Exited");
		return appRefNumber;
	}

	public HashMap getRepaymentDetails(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getRepaymentDetails", "Entered");
		// Vector repaymentDetails = new Vector();
		CallableStatement callableStmt = null;
		Connection conn = DBConnection.getConnection();
		ResultSet rs = null;
		HashMap map = new HashMap();
		double totalAmntRepaid = 0.0;

		double repayAmnt = 0.0;
		String repayId = null;
		java.util.Date repayDt = null;

		int status = -1;
		String errorCode = null;

		try {
			callableStmt = conn
					.prepareCall("{? = call packGetRepaymentDtls.funcGetRepaymentDtl(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, cgpan);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getRepaymentDetails",
						"SP returns a 1. Code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			rs = (ResultSet) callableStmt.getObject(3);
			while (rs.next()) {
				repayId = (String) rs.getString(1);
				repayAmnt = (double) rs.getDouble(2);
				repayDt = (java.util.Date) rs.getDate(3);
				totalAmntRepaid = totalAmntRepaid + repayAmnt;
			}
			rs.close();
			rs = null;

			map.put(ClaimConstants.CLM_CGPAN, cgpan);
			map.put(ClaimConstants.TOTAL_AMNT_REPAID, new Double(
					totalAmntRepaid));
		} catch (SQLException sqlex) {
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getRepaymentDetails", "Exited");
		return map;
	}

	public HashMap getClaimLimitDtls(String designation)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimLimitDtls", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = DBConnection.getConnection();
		ResultSet rs = null;
		HashMap map = new HashMap();
		double maxClmApprvAmnt = 0.0;

		java.util.Date fromDate = null;
		java.util.Date toDate = null;

		int status = -1;
		String errorCode = null;

		try {
			callableStmt = conn
					.prepareCall("{? = call packGetClmLimitDtls.funcGetClmLimitDtls(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, designation);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimLimitDtls",
						"SP returns a 1. Code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			rs = (ResultSet) callableStmt.getObject(3);
			while (rs.next()) {
				maxClmApprvAmnt = (double) rs.getDouble(1);
				fromDate = rs.getDate(2);
				toDate = rs.getDate(3);
			}
			rs.close();
			rs = null;

			map.put(ClaimConstants.CLM_MAX_APPROVAL_AMOUNT, new Double(
					maxClmApprvAmnt));
			map.put(ClaimConstants.CLM_VALID_FROM_DT, fromDate);
			map.put(ClaimConstants.CLM_VALID_TO_DT, toDate);
		} catch (SQLException sqlex) {
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimLimitDtls",
				"Printing the HashMap :" + map);
		Log.log(Log.INFO, "CPDAO", "getClaimLimitDtls", "Exited");
		return map;
	}

	/*
	 * This method returns a Vector of HashMaps. Each HashMap will contain
	 * Personal Guarantee Details.
	 */

	public Vector getFirstClmPerGaurSecDtls(String clmRefNumber)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getFirstClmPerGaurSecDtls", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = DBConnection.getConnection();
		ResultSet rs = null;
		HashMap sanctionMap = new HashMap();
		HashMap npaMap = new HashMap();
		HashMap clmLodgementMap = new HashMap();
		Vector dtls = new Vector();
		double guarantorNetWorth = 0.0;

		String securityId = null;
		String particular = null;
		double value = 0.0;
		double amntThruSecurity = 0.0;
		String reasonForReduction = null;
		String particularFlag = null;

		int status = -1;
		String errorCode = null;

		try {
			callableStmt = conn
					.prepareCall("{? = call packGetClmSecurityDetails.funcGetClmSecurityDetails(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, clmRefNumber);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getFirstClmPerGaurSecDtls",
						"SP returns a 1. Code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			rs = (ResultSet) callableStmt.getObject(3);
			int count = 0;
			while (rs.next()) {
				securityId = (String) rs.getString(1);
				particularFlag = (String) rs.getString(2);
				guarantorNetWorth = (double) rs.getDouble(3);
				if (securityId == null) {
					continue;
				}
				particular = (String) rs.getString(4);
				value = (double) rs.getDouble(5);
				amntThruSecurity = (double) rs.getDouble(6);
				reasonForReduction = (String) rs.getString(7);
				if ((particularFlag != null)
						&& (particularFlag
								.equals(ClaimConstants.CLM_SAPGD_AS_ON_SANCTION_CODE))) {
					sanctionMap.put(ClaimConstants.CLM_SECURITY_ID, securityId);
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))) {
						sanctionMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_LAND,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))) {
						sanctionMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))) {
						sanctionMap.put(ClaimConstants.CLM_SAPGD_PARTICULAR_MC,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))) {
						sanctionMap
								.put(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS,
										new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))) {
						sanctionMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))) {
						sanctionMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS,
								new Double(value));
					}
					sanctionMap.put(
							ClaimConstants.CLM_AMOUNT_REALIZED_THRU_SEC,
							new Double(amntThruSecurity));
					sanctionMap.put(
							ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION,
							reasonForReduction);
					sanctionMap.put(
							ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR,
							new Double(guarantorNetWorth));
					sanctionMap.put(
							ClaimConstants.CLM_SECURITY_PARTICULAR_FLAG,
							ClaimConstants.CLM_SAPGD_AS_ON_SANCTION_CODE);
				}
				if ((particularFlag != null)
						&& (particularFlag
								.equals(ClaimConstants.CLM_SAPGD_AS_ON_NPA_CODE))) {
					npaMap.put(ClaimConstants.CLM_SECURITY_ID, securityId);
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))) {
						npaMap.put(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))) {
						npaMap.put(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))) {
						npaMap.put(ClaimConstants.CLM_SAPGD_PARTICULAR_MC,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))) {
						npaMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))) {
						npaMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))) {
						npaMap.put(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS,
								new Double(value));
					}
					npaMap.put(ClaimConstants.CLM_AMOUNT_REALIZED_THRU_SEC,
							new Double(amntThruSecurity));
					npaMap.put(ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION,
							reasonForReduction);
					npaMap.put(ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR,
							new Double(guarantorNetWorth));
					npaMap.put(ClaimConstants.CLM_SECURITY_PARTICULAR_FLAG,
							ClaimConstants.CLM_SAPGD_AS_ON_NPA_CODE);
				}
				if ((particularFlag != null)
						&& (particularFlag
								.equals(ClaimConstants.CLM_SAPGD_AS_ON_LODGE_OF_CLM))) {
					clmLodgementMap.put(ClaimConstants.CLM_SECURITY_ID,
							securityId);
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_LAND))) {
						clmLodgementMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_LAND,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG))) {
						clmLodgementMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_BLDG,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_MC))) {
						clmLodgementMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_MC,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS))) {
						clmLodgementMap
								.put(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHER_FIXED_MOV_ASSETS,
										new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS))) {
						clmLodgementMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_CUR_ASSETS,
								new Double(value));
					}
					if ((particular != null)
							&& (particular
									.equals(ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS))) {
						clmLodgementMap.put(
								ClaimConstants.CLM_SAPGD_PARTICULAR_OTHERS,
								new Double(value));
					}
					clmLodgementMap.put(
							ClaimConstants.CLM_AMOUNT_REALIZED_THRU_SEC,
							new Double(amntThruSecurity));
					clmLodgementMap.put(
							ClaimConstants.CLM_SAPGD_REASONS_FOR_REDUCTION,
							reasonForReduction);
					clmLodgementMap.put(
							ClaimConstants.CLM_SAPGD_NETWORTH_OF_GUARANTOR,
							new Double(guarantorNetWorth));
					clmLodgementMap.put(
							ClaimConstants.CLM_SECURITY_PARTICULAR_FLAG,
							ClaimConstants.CLM_SAPGD_AS_ON_LODGE_OF_CLM);
				}
				count++;
			}
			rs.close();
			rs = null;

			dtls.addElement(sanctionMap);
			dtls.addElement(npaMap);
			dtls.addElement(clmLodgementMap);
		} catch (SQLException sqlex) {
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimLimitDtls",
				"Size of the PerGaurDetails Vector :" + dtls.size());
		Log.log(Log.INFO, "CPDAO", "getClaimLimitDtls", "Exited");
		return dtls;
	}

	public Vector getCGPANSForBid(String bid) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getCGPANSForBid", "Entered");
		CallableStatement callableStmt = null;
		Connection conn = DBConnection.getConnection();
		ResultSet rs = null;
		Vector cgpans = new Vector();

		int status = -1;
		String errorCode = null;

		try {
			callableStmt = conn
					.prepareCall("{? = call packGetCGPANforBIDBName.funcGetCGPANforBID(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, bid);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getCGPANSForBid",
						"SP returns a 1. Code is :" + errorCode);
				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			rs = (ResultSet) callableStmt.getObject(3);
			String cgpan = null;
			while (rs.next()) {
				cgpan = (String) rs.getString(1);
				if (cgpan != null) {
					if (!cgpans.contains(cgpan)) {
						cgpans.addElement(cgpan);
					}
				}
			}
			rs.close();
			rs = null;

			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlex) {
			throw new DatabaseException(sqlex.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getCGPANSForBid", "Exited");
		return cgpans;
	}

	public void saveITPANDetail(String borrowerId, String itpan)
			throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "saveITPANDetail()", "Entered!");
		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		// String cgclan = null;
		// String cgcsa= null;
		String voucherid = null;
		String flag = null;

		try {
			conn = DBConnection.getConnection();

			callableStmt = conn
					.prepareCall("{? = call funcInsertITPANDtl(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.setString(3, itpan);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);

			// executing the callable statement
			callableStmt.execute();
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "saveITPANDetail()",
						"SP returns a 1 in saving ITPAN Details. Error code is :"
								+ errorCode);
				// Closing the Callable Statement
				callableStmt.close();
				try {
					conn.rollback();
				} catch (SQLException sqlex) {
					Log.log(Log.ERROR, "CPDAO", "saveITPANDetail()", "Error :"
							+ sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}
			callableStmt.close();
			callableStmt = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "saveITPANDetail()",
					"Error saving ITPAN Details into the database.");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "saveITPANDetail()", "Exited!");
	}

	/*
	 * This method uploads the recall notice and legal details attachments into
	 * the database. Added on 11/10/2004 by Veldurai.
	 */

	public void insertRecallAndLegalAttachments(
			ClaimApplication claimApplication, String claimRefNumber,
			boolean internetUser) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "insertRecallAndLegalAttachments()",
				"Entered");

		Connection connection = DBConnection.getConnection(false);

		try {
			Log.log(Log.DEBUG, "CPDAO", "insertRecallAndLegalAttachments()",
					"claimRefNumber " + claimRefNumber);
			String tableName = "CLAIM_FILES";

			if (internetUser) {
				tableName = "CLAIM_FILES_TEMP";
			}
			Statement statement = connection.createStatement();

			Log.log(Log.DEBUG, "CPDAO", "insertRecallAndLegalAttachments()",
					"Legal Detail and Recall Detail File Names are  "
							+ claimApplication.getLegalDetailsFileName() + ","
							+ claimApplication.getRecalNoticeFileName());

			if (claimApplication.getLegalDetailsFileName() != null
					&& claimApplication.getRecalNoticeFileName() != null) {
				Log.log(Log.DEBUG, "CPDAO",
						"insertRecallAndLegalAttachments()",
						"Both Attachments are available ");
				// both the attachments are available.

				statement
						.execute("INSERT INTO "
								+ tableName
								+ " (CLM_REF_NO,"
								+ "CFT_RECALL_NOTICE_FILE,CFT_RECALL_FILE_NAME,"
								+ "CFT_LEGAL_PROCEED_FILE,CFT_LEGAL_FILE_NAME) VALUES ('"
								+ claimRefNumber + "'," + "empty_blob(),'"
								+ claimApplication.getRecalNoticeFileName()
								+ "'" + ",empty_blob(),'"
								+ claimApplication.getLegalDetailsFileName()
								+ "')");

				statement = connection.createStatement();
				ResultSet resutls = statement
						.executeQuery("select CFT_RECALL_NOTICE_FILE,CFT_LEGAL_PROCEED_FILE from "
								+ tableName
								+ " where CLM_REF_NO= '"
								+ claimRefNumber + "'" + " for update ");

				while (resutls.next()) {
					oracle.sql.BLOB blob = ((OracleResultSet) resutls)
							.getBLOB(1);
					OutputStream outputStream = blob.getBinaryOutputStream();
					outputStream.write(claimApplication
							.getRecallNoticeFileData());
					outputStream.close();

					blob = ((OracleResultSet) resutls).getBLOB(2);
					outputStream = blob.getBinaryOutputStream();
					outputStream.write(claimApplication
							.getLegalDetailsFileData());
					outputStream.close();
				}

				resutls.close();
				statement.close();
			} else if (claimApplication.getLegalDetailsFileName() != null
					&& claimApplication.getRecalNoticeFileName() == null) {
				// Only Legal Detail attachment is available.
				Log.log(Log.DEBUG, "CPDAO",
						"insertRecallAndLegalAttachments()",
						"Only Legal Details attachments are available  ");

				statement
						.execute("INSERT INTO "
								+ tableName
								+ " (CLM_REF_NO,"
								+ "CFT_RECALL_NOTICE_FILE,CFT_RECALL_FILE_NAME,"
								+ ") VALUES ('" + claimRefNumber + "',"
								+ "empty_blob(),'"
								+ claimApplication.getRecalNoticeFileName()
								+ "'" + ")");

				statement = connection.createStatement();
				ResultSet resutls = statement
						.executeQuery("select CFT_LEGAL_PROCEED_FILE from "
								+ tableName + " where CLM_REF_NO= '"
								+ claimRefNumber + "'" + " for update ");

				while (resutls.next()) {
					oracle.sql.BLOB blob = ((OracleResultSet) resutls)
							.getBLOB(1);
					OutputStream outputStream = blob.getBinaryOutputStream();
					outputStream.write(claimApplication
							.getLegalDetailsFileData());
					outputStream.close();
				}

				resutls.close();
				statement.close();

			} else if (claimApplication.getLegalDetailsFileName() == null
					&& claimApplication.getRecalNoticeFileName() != null) {
				// Only Recall notice attachment is available.

				Log.log(Log.DEBUG, "CPDAO",
						"insertRecallAndLegalAttachments()",
						"Only Recall Details attachments are available  ");

				statement
						.execute("INSERT INTO "
								+ tableName
								+ " (CLM_REF_NO,"
								+ "CFT_LEGAL_PROCEED_FILE,CFT_LEGAL_FILE_NAME) VALUES ('"
								+ claimRefNumber + "'," + ",empty_blob(),'"
								+ claimApplication.getLegalDetailsFileName()
								+ "')");

				statement = connection.createStatement();
				ResultSet resutls = statement
						.executeQuery("select CFT_RECALL_NOTICE_FILE from "
								+ tableName + " where CLM_REF_NO= '"
								+ claimRefNumber + "'" + " for update ");

				while (resutls.next()) {
					oracle.sql.BLOB blob = ((OracleResultSet) resutls)
							.getBLOB(1);
					OutputStream outputStream = blob.getBinaryOutputStream();
					outputStream.write(claimApplication
							.getRecallNoticeFileData());
					outputStream.close();
				}

				resutls.close();
				statement.close();
			} else {
				// No Attachment is available.
				// We will not come here at all.
				Log.log(Log.DEBUG, "CPDAO",
						"insertRecallAndLegalAttachments()",
						"No Attachments are available! We should not come here at all...");
			}
			if (!internetUser) {
				// the following stored procedure moves the intra table value
				// into temp.
				// Since internet users directly inserting into temp, no need to
				// transfer the sps.
				CallableStatement callable = connection
						.prepareCall("{?=call funcInsClmfiles(?,?)}");

				callable.registerOutParameter(1, Types.INTEGER);
				callable.registerOutParameter(3, Types.VARCHAR);
				callable.setString(2, claimRefNumber);
				callable.execute();
				int errorCode = callable.getInt(1);
				String error = callable.getString(3);

				Log.log(Log.DEBUG, "CPDAO",
						"insertRecallAndLegalAttachments()",
						"errorCode and error are " + errorCode + "," + error);

				if (errorCode == Constants.FUNCTION_FAILURE) {
					connection.rollback();
					callable.close();
					callable = null;
					Log.log(Log.ERROR, "CPDAO",
							"insertRecallAndLegalAttachments()", "Error "
									+ error);

					throw new DatabaseException("Unable to Store Attachments ");
				}

				callable.close();
				callable = null;
			}

			connection.commit();

		} catch (SQLException e) {
			Log.log(Log.ERROR, "CPDAO", "insertRecallAndLegalAttachments()",
					"Error " + e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			Log.log(Log.ERROR, "CPDAO", "insertRecallAndLegalAttachments()",
					"Error " + e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "CPDAO", "insertRecallAndLegalAttachments()",
				"Exited");

	}

	/*
	 * This method fetches the Working Capital Tenure and Guarantee Start Date
	 * for the given CGPAN
	 */

	public HashMap getWorkingCapital(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "CPDAODAO", "getWorkingCapital", "Entered.");
		HashMap wcDetails = new HashMap();
		Connection connection = DBConnection.getConnection(false);
		int wcTenure = -1;
		java.util.Date wcGuarStartDt = null;
		try {

			// This function is called for a Working Capital / Composite / Both
			// Application

			CallableStatement wcObj = connection
					.prepareCall("{?=call funcGetWCTenureforCGPAN(?,?,?,?)}");

			wcObj.setString(2, cgpan); // CGPAN

			wcObj.registerOutParameter(1, Types.INTEGER);
			wcObj.registerOutParameter(5, Types.VARCHAR);
			wcObj.registerOutParameter(4, Types.INTEGER);
			wcObj.registerOutParameter(3, Types.DATE);

			wcObj.executeQuery();
			int wcObjValue = wcObj.getInt(1);
			String errorCode = wcObj.getString(5);

			if (wcObjValue == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getWorkingCapital()",
						"SP returns a 1 in fetching WC Details. Error code is :"
								+ errorCode);
				// Closing the Callable Statement
				wcObj.close();
				wcObj = null;
				throw new DatabaseException(errorCode);
			}
			wcTenure = wcObj.getInt(4);
			wcGuarStartDt = wcObj.getDate(3);
			wcDetails.put(ClaimConstants.WC_TENURE, new Integer(wcTenure));
			wcDetails.put(ClaimConstants.CLM_GUARANTEE_START_DT, wcGuarStartDt);
			wcObj.close();
			wcObj = null;
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getWorkingCapital()",
					"Error in fetching WC Details");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "CPDAO", "getWorkingCapital", "Exited");

		return wcDetails;
	}

	/**
	 * This methods returns all the cgpans filed for the given claim ref number
	 * in the database
	 * 
	 * @throws DatabaseException
	 */
	public ArrayList getAllCgpansForClmRefNum(String clmRefNum)
			throws DatabaseException {

		Log.log(Log.INFO, "CPDAO", "getAllCgpansForClmRefNum", "entered");
		Connection connection = DBConnection.getConnection();

		ArrayList cgpanList = new ArrayList();

		try {
			CallableStatement allCgpans = connection
					.prepareCall("{?=call packGetAllCgpansForClmRefNum.funcGetAllCgpansForClmRef(?,?,?)}");

			allCgpans.registerOutParameter(1, Types.INTEGER);
			allCgpans.setString(2, clmRefNum);
			allCgpans.registerOutParameter(3, Constants.CURSOR);
			allCgpans.registerOutParameter(4, Types.VARCHAR);

			allCgpans.execute();

			int functionReturnValue = allCgpans.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = allCgpans.getString(3);

				allCgpans.close();
				allCgpans = null;

				throw new DatabaseException(error);

			} else {
				ResultSet allCgpansResult = (ResultSet) allCgpans.getObject(3);

				while (allCgpansResult.next()) {

					String cgpan = allCgpansResult.getString(1);

					cgpanList.add(cgpan);
				}
				allCgpansResult.close();
				allCgpansResult = null;
				allCgpans.close();
				allCgpans = null;

			}

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "CPDAO", "getAllCgpansForClmRefNum",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "CPDAO", "getAllCgpansForClmRefNum",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "CPDAO", "getAllCgpansForClmRefNum", "Exited");
		return cgpanList;

	}

	/**
	 * This method returns an arraylist of periodic info object. The parameter
	 * passed to this object are id and type.
	 * 
	 * If type = 0, the id passed will be borrower id. All the disbursement
	 * details for applications belonging to this borrower will be fetched in
	 * this case.
	 * 
	 * If type = 1, the id passed will be CGPAN. The corresponding disbursement
	 * details for application for this CGPAN will be fetched.
	 * 
	 * @param id
	 * @param type
	 * @return Arraylist
	 * @roseuid 397BDFA403E6
	 */
	private ArrayList getDisbursementDetails(String id, int type)
			throws DatabaseException {

		Log.log(Log.INFO, "CPDAO", "getDisbursementDetails", "Entered");

		ArrayList periodicInfos = new ArrayList();

		Connection connection = DBConnection.getConnection();

		CallableStatement getDisbursementDetailStmt = null;
		// Disbursement disbursement = null;
		ResultSet resultSet = null;

		try {
			String exception = "";

			String functionName = null;

			if (type == GMConstants.TYPE_ZERO) {
				functionName = "{?=call packGetCPDtlsforDBR.funcGetCPDtlsForBid(?,?,?)}";
			}

			getDisbursementDetailStmt = connection.prepareCall(functionName);
			getDisbursementDetailStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getDisbursementDetailStmt.setString(2, id);
			getDisbursementDetailStmt.registerOutParameter(3, Constants.CURSOR);
			getDisbursementDetailStmt.registerOutParameter(4,
					java.sql.Types.VARCHAR);

			getDisbursementDetailStmt.executeQuery();

			exception = getDisbursementDetailStmt.getString(4);

			int error = getDisbursementDetailStmt.getInt(1);

			if (error == Constants.FUNCTION_FAILURE) {
				getDisbursementDetailStmt.close();
				getDisbursementDetailStmt = null;
				Log.log(Log.DEBUG, "CPDAO", "getDisbursementDetails",
						"Exception :" + exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}
			resultSet = (ResultSet) getDisbursementDetailStmt.getObject(3);

			PeriodicInfo periodicInfo = new PeriodicInfo();
			Disbursement disbursement = null;
			ArrayList listOfDisbursement = new ArrayList();
			boolean firstTime = true;

			while (resultSet.next()) {
				disbursement = new Disbursement();

				if (firstTime) {
					periodicInfo.setBorrowerId(resultSet.getString(1));
					periodicInfo.setBorrowerName(resultSet.getString(4));
					firstTime = false;
				}

				disbursement.setCgpan(resultSet.getString(2));
				// System.out.println("getDisbursementDetails:CGPAN : "+resultSet.getString(2));
				disbursement.setScheme(resultSet.getString(3));
				// System.out.println("getDisbursementDetails:Scheme : "+resultSet.getString(4));
				disbursement.setSanctionedAmount(resultSet.getDouble(5));
				// System.out.println("getDisbursementDetails:San Amt: "+resultSet.getString(5));

				listOfDisbursement.add(disbursement);
			}

			resultSet.close();
			resultSet = null;

			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt = null;

			String cgpan = null;

			int disbSize = listOfDisbursement.size();
			functionName = "{?=call packGetCPPIDBRDtlsCGPAN.funcCPDBRDetailsForCGPAN(?,?,?)}";
			getDisbursementDetailStmt = connection.prepareCall(functionName);
			for (int i = 0; i < disbSize; ++i) {
				ArrayList listOfDisbursementAmount = new ArrayList();
				disbursement = (Disbursement) listOfDisbursement.get(i);
				cgpan = disbursement.getCgpan();
				if (cgpan == null) {
					continue;
				}
				Log.log(Log.DEBUG, "CPDAO", "getDisbursementDetails", "Cgpan"
						+ cgpan);
				getDisbursementDetailStmt.registerOutParameter(1,
						java.sql.Types.INTEGER);
				getDisbursementDetailStmt.setString(2, cgpan);

				getDisbursementDetailStmt.registerOutParameter(3,
						Constants.CURSOR);
				getDisbursementDetailStmt.registerOutParameter(4,
						java.sql.Types.VARCHAR);

				getDisbursementDetailStmt.execute();

				exception = getDisbursementDetailStmt.getString(4);

				error = getDisbursementDetailStmt.getInt(1);
				if (error == Constants.FUNCTION_FAILURE) {
					getDisbursementDetailStmt.close();
					getDisbursementDetailStmt = null;
					Log.log(Log.ERROR, "CPDAO", "getDisbursementDetails",
							"Exception" + exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				resultSet = (ResultSet) getDisbursementDetailStmt.getObject(3);
				DisbursementAmount disbursementAmount = null;
				while (resultSet.next()) {
					disbursementAmount = new DisbursementAmount();

					disbursementAmount.setCgpan(cgpan);

					disbursementAmount
							.setDisbursementId(resultSet.getString(1));
					Log.log(Log.DEBUG, "CPDAO", "getDisbursementDetails",
							"disb Id" + disbursementAmount.getDisbursementId());

					disbursementAmount.setDisbursementAmount(resultSet
							.getDouble(2));
					Log.log(Log.DEBUG,
							"CPDAO",
							"getDisbursementDetails",
							"disb Amt"
									+ disbursementAmount
											.getDisbursementAmount());

					disbursementAmount.setDisbursementDate(DateHelper
							.sqlToUtilDate(resultSet.getDate(3)));
					Log.log(Log.DEBUG,
							"CPDAO",
							"getDisbursementDetails",
							"disb date"
									+ disbursementAmount.getDisbursementDate());
					disbursementAmount.setFinalDisbursement(resultSet
							.getString(4));
					Log.log(Log.DEBUG,
							"CPDAO",
							"getDisbursementDetails",
							"Fin disb "
									+ disbursementAmount.getFinalDisbursement());

					listOfDisbursementAmount.add(disbursementAmount);
				}

				disbursement.setDisbursementAmounts(listOfDisbursementAmount);
				resultSet.close();
				resultSet = null;

			}

			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt = null;

			periodicInfo.setDisbursementDetails(listOfDisbursement);
			periodicInfos.add(periodicInfo);

			connection.commit();

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

		Log.log(Log.INFO, "CPDAO", "get disbursementDetails", "Exited");
		return periodicInfos;
	}

	/**
	 * 
	 * @param claimRefNo
	 * @param q1
	 * @param q2
	 * @param q3
	 * @param q4
	 * @param q5
	 * @param q6
	 * @param q7
	 * @param q8
	 * @param q9
	 * @param q10
	 * @param q11
	 * @param q12
	 * @param q13
	 * @param q14
	 * @param userId
	 * @param systemDate
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int insertClaimTCProcessingDetails(String claimRefNo, String q1,
			String q2, String q3, String q4, String q5, String q6, String q7,
			String q8, String q9, String q10, String q11, String q12,
			String q13, String q14, String userId, java.util.Date systemDate,
			String ltrRefNo, String ltrDate) throws DatabaseException {
		Connection connection = null;
		int tempcount = 0;
		Date sqlltrDate;

		CallableStatement insertClaimProcessDetails = null;

		boolean status = false;

		sqlltrDate = null;
		Date endDt = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date dt = formatter.parse(ltrDate);
			sqlltrDate = new Date(dt.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// java.util.Date utilDate;
		// java.sql.Date sqlDate;

		try {
			connection = DBConnection.getConnection();
			insertClaimProcessDetails = connection
					.prepareCall("{?=call funcclmqueryinsert(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			insertClaimProcessDetails.registerOutParameter(1, Types.INTEGER);
			insertClaimProcessDetails.setString(2, claimRefNo);
			insertClaimProcessDetails.setString(3, q1);
			insertClaimProcessDetails.setString(4, q2);
			insertClaimProcessDetails.setString(5, q3);
			insertClaimProcessDetails.setString(6, q4);
			insertClaimProcessDetails.setString(7, q5);
			insertClaimProcessDetails.setString(8, q6);
			insertClaimProcessDetails.setString(9, q7);
			insertClaimProcessDetails.setString(10, q8);
			insertClaimProcessDetails.setString(11, q9);
			insertClaimProcessDetails.setString(12, q10);
			insertClaimProcessDetails.setString(13, q11);
			insertClaimProcessDetails.setString(14, q12);
			insertClaimProcessDetails.setString(15, q13);
			insertClaimProcessDetails.setString(16, q14);
			insertClaimProcessDetails.setString(17, userId);
			insertClaimProcessDetails.setDate(18,
					new java.sql.Date(systemDate.getTime()));
			insertClaimProcessDetails.setString(19, ltrRefNo);
			insertClaimProcessDetails.setDate(20, sqlltrDate);
			insertClaimProcessDetails.registerOutParameter(21, Types.VARCHAR);

			insertClaimProcessDetails.executeQuery();
			int returnValue = insertClaimProcessDetails.getInt(1);
			// System.out.println("Claim Ref No:"+claimRefNo+" Return Value:"+returnValue);
			if (returnValue == Constants.FUNCTION_FAILURE) {

				String error = insertClaimProcessDetails.getString(21);
				System.out.println("Error:" + error);
				status = false;
				tempcount = 0;
				insertClaimProcessDetails.close();
				insertClaimProcessDetails = null;

				Log.log(Log.ERROR, "CPDAO", "insertClaimTCProcessingDetails",
						error);

				connection.rollback();

				throw new DatabaseException(
						insertClaimProcessDetails.getString(4));

			} else if (returnValue == Constants.FUNCTION_SUCCESS) {
				status = true;
				tempcount = 1;
				insertClaimProcessDetails.close();
				insertClaimProcessDetails = null;
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
	 * @param claimRefNo
	 * @param cgpan
	 * @param userId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	/*
	 * 
	 * public void updateClaimApplicationStatus (String claimRefNo, String
	 * cgpan,String userId) throws DatabaseException {
	 * 
	 * 
	 * Connection connection = null ; int tempcount = 0; CallableStatement
	 * updateClaimStatusStmt = null ;
	 * 
	 * boolean status = false ;
	 * 
	 * java.util.Date utilDate; java.sql.Date sqlDate;
	 * 
	 * try { connection = DBConnection.getConnection() ; updateClaimStatusStmt =
	 * connection.prepareCall("{?=call Funcchangeclaimstatus(?,?,?,?}");
	 * 
	 * updateClaimStatusStmt.registerOutParameter(1,Types.INTEGER);
	 * if(!(claimRefNo==null||claimRefNo.equals(""))){
	 * updateClaimStatusStmt.setString(2,claimRefNo) ; } else {
	 * updateClaimStatusStmt.setNull(2,Types.VARCHAR); }
	 * 
	 * if(!(cgpan==null||cgpan.equals(""))){
	 * updateClaimStatusStmt.setString(3,cgpan) ; } else {
	 * updateClaimStatusStmt.setNull(3,Types.VARCHAR); }
	 * 
	 * updateClaimStatusStmt.setString(4,userId) ;
	 * System.out.println("UserId:"+userId);
	 * 
	 * updateClaimStatusStmt.registerOutParameter(5,Types.VARCHAR);
	 * 
	 * updateClaimStatusStmt.executeQuery(); int
	 * returnValue=updateClaimStatusStmt.getInt(1);
	 * System.out.println("returnValue:"+returnValue);
	 * if(returnValue==Constants.FUNCTION_FAILURE){
	 * 
	 * String error=updateClaimStatusStmt.getString(5);
	 * System.out.println("Error:"+error); status = false; // tempcount = 0;
	 * updateClaimStatusStmt.close(); updateClaimStatusStmt=null;
	 * 
	 * Log.log(Log.ERROR,"CPDAO","updateClaimStatus",error);
	 * 
	 * connection.rollback();
	 * 
	 * throw new DatabaseException(updateClaimStatusStmt.getString(5));
	 * 
	 * } else if(returnValue==Constants.FUNCTION_SUCCESS){ status= true;
	 * //tempcount = 1; updateClaimStatusStmt.close();
	 * updateClaimStatusStmt=null; } //
	 * System.out.println("Temp Count: "+tempcount); connection.commit(); }
	 * catch (Exception exception) { throw new
	 * DatabaseException(exception.getMessage()); } finally { // Free the
	 * connection here. DBConnection.freeConnection(connection); }
	 * 
	 * 
	 * 
	 * }
	 */

	/**
	 * 
	 * @param claimRefNo
	 * @param cgpan
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void updateClaimApplicationStatus(String claimRefNo, String cgpan,
			String userId) throws DatabaseException {

		Connection connection = null;
		CallableStatement stmt = null;

		try {
			connection = DBConnection.getConnection();

			stmt = connection
					.prepareCall("{?=call Funcchangeclaimstatus(?,?,?,?)}");
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			if (!(claimRefNo == null || claimRefNo.equals(""))) {
				stmt.setString(2, claimRefNo);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			if (!(cgpan == null || cgpan.equals(""))) {
				stmt.setString(3, cgpan);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}

			stmt.setString(4, userId);
			stmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			stmt.execute();

			int status = stmt.getInt(1);
			// System.out.println("status:"+status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(5);
				System.out.println("error:" + err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;

			Log.log(Log.INFO, "CPDAO", "updateClaimApplicationStatus",
					"funcCancAllocation executed successfully");
		} catch (SQLException exp) {
			// System.out.println("sql exception " +exp.getMessage());
			Log.log(Log.INFO, "CPDAO", "updateClaimApplicationStatus",
					"sql exception " + exp.getMessage());
			throw new DatabaseException(exp.getMessage());
		}
	}

	/**
	 * 
	 * checks whether given state is under NER or not
	 * 
	 * @param ssiStateName
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String checkBorrowerStateRegion(String ssiStateName)
			throws DatabaseException {
		Connection connection = null;

		CallableStatement checkBorrowerStateRegion = null;

		boolean status = false;
		String nerFlag = "N";

		try {
			connection = DBConnection.getConnection();
			checkBorrowerStateRegion = connection
					.prepareCall("{?=call funcCheckStateRegion(?,?,?)}");

			checkBorrowerStateRegion.registerOutParameter(1, Types.INTEGER);
			checkBorrowerStateRegion.setString(2, ssiStateName);
			checkBorrowerStateRegion.registerOutParameter(3, Types.VARCHAR);
			checkBorrowerStateRegion.registerOutParameter(4, Types.VARCHAR);

			checkBorrowerStateRegion.executeQuery();
			int returnValue = checkBorrowerStateRegion.getInt(1);

			if (returnValue == Constants.FUNCTION_FAILURE) {

				String error = checkBorrowerStateRegion.getString(4);
				System.out.println("Error:" + error);
				status = false;

				checkBorrowerStateRegion.close();
				checkBorrowerStateRegion = null;

				Log.log(Log.ERROR, "CPDAO", "checkBorrowerStateRegion", error);

				connection.rollback();

				throw new DatabaseException(
						checkBorrowerStateRegion.getString(4));

			} else if (returnValue == Constants.FUNCTION_SUCCESS) {
				status = true;
				nerFlag = checkBorrowerStateRegion.getString(3);
				checkBorrowerStateRegion.close();
				checkBorrowerStateRegion = null;
			}
			// System.out.println("North East Flag: "+nerFlag);
			connection.commit();
		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return nerFlag;

	}

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getClaimRefNo(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "CPDAO", "getClaimRefNo", "Entered");

		String claimRefNo = null;
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT UNIQUE CLM_REF_NO FROM CLAIM_APPLICATION_AMOUNT_TEMP WHERE CGPAN = ?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, cgpan);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				claimRefNo = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();

		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return claimRefNo;
	}

	/**
	 * This method returns an arraylist of periodic infos object. The parameter
	 * passed to this object are id and type.
	 * 
	 * If type = 0, the id passed will be borrower id. All the repayment details
	 * for applications belonging to this borrower will be fetched in this case.
	 * 
	 * If type = 1, the id passed will be CGPAN. The corresponding repayment
	 * details for application for this CGPAN will be fetched.
	 * 
	 * @param id
	 * @param type
	 * @return ArrayList
	 * @roseuid 397BFD3C0281
	 */
	private ArrayList getCPRepaymentDetails(String id, int type)
			throws DatabaseException {

		Log.log(Log.INFO, "CPDAO", "get Repayment Details", "Entered");

		ArrayList periodicInfos = new ArrayList();

		Connection connection = DBConnection.getConnection();
		CallableStatement getRepaymentDetailStmt = null;
		ResultSet resultSet = null;

		try {

			String exception = "";

			String functionName = "{?=call packGetDtlsforRepayment.funcGetDtlsforCGPAN(?,?,?)}";

			getRepaymentDetailStmt = connection.prepareCall(functionName);
			getRepaymentDetailStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getRepaymentDetailStmt.setString(2, id);
			getRepaymentDetailStmt.registerOutParameter(3, Constants.CURSOR);
			getRepaymentDetailStmt.registerOutParameter(4,
					java.sql.Types.VARCHAR);

			getRepaymentDetailStmt.executeQuery();

			exception = getRepaymentDetailStmt.getString(4);
			Log.log(Log.DEBUG, "CPDAO", "repayment detail", "exception "
					+ exception);
			int error = getRepaymentDetailStmt.getInt(1);
			Log.log(Log.DEBUG, "CPDAO", "repayment detail", "errorCode "
					+ error);
			if (error == Constants.FUNCTION_FAILURE) {
				getRepaymentDetailStmt.close();
				getRepaymentDetailStmt = null;
				connection.rollback();
				Log.log(Log.ERROR, "CPDAO", "getRepaymentdetail",
						"error in SP " + exception);
				throw new DatabaseException(exception);
			}
			Log.log(Log.DEBUG, "CPDAO", "getRepaymentdetail",
					"Before ResultSet assign");
			resultSet = (ResultSet) getRepaymentDetailStmt.getObject(3);
			Log.log(Log.DEBUG, "CPDAO", "getRepaymentdetail",
					"resultSet assigned");

			PeriodicInfo periodicInfo = new PeriodicInfo();
			Repayment repayment = null;

			ArrayList listOfRepayment = new ArrayList();

			boolean firstTime = true;
			String tcCgpan = null;
			int len = 0;
			String applType = null;

			while (resultSet.next()) {
				Log.log(Log.DEBUG, "CPDAO", "getRepaymentdetail",
						"Inside ResultSet");
				repayment = new Repayment();
				tcCgpan = resultSet.getString(2);
				len = tcCgpan.length();
				applType = tcCgpan.substring(len - 2, len - 1);
				if (applType.equalsIgnoreCase(GMConstants.CGPAN_TC)) {
					if (firstTime) {
						periodicInfo.setBorrowerId(resultSet.getString(1));
						Log.log(Log.DEBUG, "getRepaymentDetails for Borrower",
								"Borrower ID",
								" : " + periodicInfo.getBorrowerId());

						periodicInfo.setBorrowerName(resultSet.getString(3));
						Log.log(Log.DEBUG, "getRepaymentDetailsfor Borrower:",
								"Borrower Name",
								" : " + periodicInfo.getBorrowerName());
						firstTime = false;
					}

					repayment.setCgpan(tcCgpan);
					Log.log(Log.DEBUG, "getRepaymentDetailsfor Borrower:",
							"CGPAN ", ": " + repayment.getCgpan());
					repayment.setScheme(resultSet.getString(4));
					Log.log(Log.DEBUG, "getRepaymentDetailsfor Borrower:",
							"Scheme", " : " + repayment.getScheme());

					listOfRepayment.add(repayment);
				}
			}

			Log.log(Log.DEBUG, "getRepaymentDetails for Borrower:",
					"size of RepaymentObj", " : " + listOfRepayment.size());

			resultSet.close();
			resultSet = null;

			getRepaymentDetailStmt = null;

			functionName = "{?=call packGetRepaymentDtls.funcGetRepaymentDtl(?,?,?)}";
			getRepaymentDetailStmt = connection.prepareCall(functionName);
			// System.out.println("size of listOfRepayment= "+listOfRepayment.size());

			String cgpan = "";
			int size = listOfRepayment.size();
			for (int i = 0; i < size; ++i) {
				ArrayList listOfRepaymentAmount = new ArrayList();
				repayment = (Repayment) listOfRepayment.get(i);
				cgpan = repayment.getCgpan();
				Log.log(Log.DEBUG, "getRepaymentDetails for cgpan:", "cgpan",
						" : " + i + " " + cgpan);
				getRepaymentDetailStmt.registerOutParameter(1,
						java.sql.Types.INTEGER);
				getRepaymentDetailStmt.setString(2, cgpan);

				getRepaymentDetailStmt
						.registerOutParameter(3, Constants.CURSOR);
				getRepaymentDetailStmt.registerOutParameter(4,
						java.sql.Types.VARCHAR);

				getRepaymentDetailStmt.execute();

				exception = getRepaymentDetailStmt.getString(4);

				error = getRepaymentDetailStmt.getInt(1);
				if (error == Constants.FUNCTION_FAILURE) {
					getRepaymentDetailStmt.close();
					getRepaymentDetailStmt = null;
					Log.log(Log.ERROR, "getRepaymentDetails for cgpan:",
							"Exception ", exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				resultSet = (ResultSet) getRepaymentDetailStmt.getObject(3);
				RepaymentAmount repaymentAmount = null;
				while (resultSet.next()) {
					repaymentAmount = new RepaymentAmount();

					repaymentAmount.setCgpan(cgpan);
					Log.log(Log.DEBUG, "CPDAO", "RepaymentAmount", "Cgpan "
							+ cgpan);

					repaymentAmount.setRepayId(resultSet.getString(1));
					Log.log(Log.DEBUG, "CPDAO", "RepaymentAmount",
							"RepaymentId " + repaymentAmount.getRepayId());

					repaymentAmount.setRepaymentAmount(resultSet.getDouble(2));
					Log.log(Log.DEBUG, "rep Amt: ", "rpAmount", "--"
							+ repaymentAmount.getRepaymentAmount());

					repaymentAmount.setRepaymentDate(resultSet.getDate(3));
					Log.log(Log.DEBUG, "rep date:", "date", " "
							+ repaymentAmount.getRepaymentDate());

					listOfRepaymentAmount.add(repaymentAmount);
					Log.log(Log.DEBUG, "************", "***********",
							"****************");
				}
				repayment.setRepaymentAmounts(listOfRepaymentAmount);
				resultSet.close();
				resultSet = null;
			}
			periodicInfo.setRepaymentDetails(listOfRepayment);

			periodicInfos.add(periodicInfo);

			getRepaymentDetailStmt.close();
			getRepaymentDetailStmt = null;

			connection.commit();

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

		Log.log(Log.INFO, "CPDAO", "get Repayment Details", "Exited");
		return periodicInfos;
	}

	// added following method on 06-Apr-2011 for CGPAN wise Claim processing

	public String insertClaimProcessDetails1(String clmRefNum,
			String userRemarks, ArrayList mainArray,
			java.util.Date dateofReceipt, String microFlag,
			String falgforCasesafet, ClaimDetail clmdtl, String nerfalg,
			String womenOprator) throws DatabaseException {
		String methodName = "insertClaimProcessDetails1";

		// System.out.println("Inside the method insertClaimProcessDetails1...!##################################### ");
		Connection connection = null;
		CallableStatement insertClaimProcessDtls = null;
		Log.log(Log.INFO, "CPDAO", methodName, "Entered");
		int updateStatus = 0;
		boolean newConn = false;

		// ,nerfalg,womenOprator

		// jai code for CGPAN wise data

		String[] hidcgpan = (String[]) mainArray.get(0);
		String[] hidgaurIssue = (String[]) mainArray.get(1);
		String[] outstandingAsOnNPA = (String[]) mainArray.get(2);
		String[] recoverafterNPA = (String[]) mainArray.get(3);
		String[] netOutsandingAmt = (String[]) mainArray.get(4);
		String[] hidclaimbymliamt = (String[]) mainArray.get(5);
		String[] claimEligibleAmt = (String[]) mainArray.get(6);
		String[] firstInstallAmt = (String[]) mainArray.get(7);
		String[] dedecutByMliIfAny = (String[]) mainArray.get(8);
		String[] paybleAmt = (String[]) mainArray.get(9);

		double tcIssued1 = 0.0;
		double wcIssued1 = 0.0;
		double totalTCOSAmountAsOnNPA1 = 0.0;
		double totalWCOSAmountAsOnNPA1 = 0.0;
		double tcrecovery1 = 0.0;
		double wcrecovery1 = 0.0;
		double tcClaimEligibleAmt1 = 0.0;
		double wcClaimEligibleAmt1 = 0.0;

		// new Variables
		double totalIssuedAmt = 0.0;
		double tcClaimEligibleAmt = 0.0;
		double wcClaimEligibleAmt = 0.0;
		double tcFirstInstallment = 0.0;
		double wcFirstInstallment = 0.0;
		double totalTCFirstInstallment = 0.0;
		double totalWCFirstInstallment = 0.0;
		double totalDeductedByMli = 0.0;
		String payAmntNow = "";
		// retriveing the Total Inssue amount .

		for (int i = 0; i < hidgaurIssue.length; i++) {
			totalIssuedAmt = totalIssuedAmt
					+ Double.parseDouble(hidgaurIssue[i]);
		}
		// System.out.println("The Total issued Amount is :--->"+totalIssuedAmt);
		// / System.out.println("The Micro Falg Value is  :--->"+microFlag);

		// retriveing the Total Inssue amount .

		try { // try before main loop

			connection = DBConnection.getConnection();

			Statement str = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			connection.setAutoCommit(false);

			java.sql.Date dateReciipt = new Date(dateofReceipt.getTime());
			// String converttoDBform =DateHelper.stringToDBDate(dateofReceipt);
			// System.out.println("The SQL FORMAT DATE IS :--->"+dateReciipt);

			// new java.sql.Date(dateofReceipt.getTime() dateofReceipt

			// Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String todysDate = formatter.format(dateofReceipt);
			// System.out.println("Today : " + todysDate);
			String claimReciDate = DateHelper.stringToDBDate(todysDate);
			// System.out.println("The Current Date is :--->"+claimReciDate);

			String remQury = "Update claim_detail_temp@cginter set clm_remarks = clm_remarks ||'"
					+ userRemarks
					+ "',CLM_DT_OF_RECEIPT='"
					+ claimReciDate
					+ "' where clm_ref_no = '" + clmRefNum + "'";
			// str.executeQuery(Qury1);
			// int resletVal=str.executeUpdate(remQury);
			int remarkUpdat = str.executeUpdate(remQury);
			// str.execute(remQury);
			// System.out.println("The Result Value for remark upadate is :--->"+remarkUpdat);

			Log.log(Log.DEBUG, "CPDAO", methodName, "updateStatus,error "
					+ updateStatus + ",");

			// System.out.println("The length of Array is :-->"+hidcgpan.length);
			for (int i = 0; i < hidcgpan.length; i++) {
				String cgpan = hidcgpan[i];
				// System.out.println("The CGPAN is:---->"+cgpan);

				double guissu = Double.parseDouble(hidgaurIssue[i]);
				// System.out.println("The guissu is:---->"+guissu);

				double ountstand = Double.parseDouble(outstandingAsOnNPA[i]);
				// System.out.println("The ountstand is:---->"+ountstand);

				double recov = Double.parseDouble(recoverafterNPA[i]);
				// System.out.println("The recov is:---->"+recov);

				double netout = Double.parseDouble(netOutsandingAmt[i]);
				// System.out.println("The netout is:---->"+netout);

				double claimbymliamt = Double.parseDouble(hidclaimbymliamt[i]);
				// System.out.println("The claimByMLI is:---->"+claimbymliamt);

				double claimeligibleamt = Double
						.parseDouble(claimEligibleAmt[i]);
				// System.out.println("The ClaimEligible is:---->"+claimeligibleamt);

				double firstInstall = Double.parseDouble(firstInstallAmt[i]);
				// System.out.println("The firstInstall is:---->"+firstInstall);

				double dedecutByMli = Double.parseDouble(dedecutByMliIfAny[i]);
				// System.out.println("The dedecutByMli is:---->"+dedecutByMli);

				totalDeductedByMli = totalDeductedByMli + dedecutByMli;

				double payAmt = Double.parseDouble(paybleAmt[i]);
				// System.out.println("The payAmt is:---->"+payAmt);

				String loanType = cgpan.substring(cgpan.length() - 2,
						cgpan.length());
				// System.out.println("The Loan Type IS :--->"+loanType);
				// System.out.println("The New CGPAN IS :-------------------->"+cgpan);

				// /jai code for elegibel claim amount
				// code for term loan over
				cgpan = cgpan.trim();

				if (loanType.equals("TC") || loanType.equals("CC")) { // term
																		// loan
																		// type
																		// start
					if ((totalIssuedAmt <= 500000) && (microFlag.equals("Y"))) {
						// jai code
						if (falgforCasesafet.equals("Y")) {
							tcClaimEligibleAmt = Math.round((Math.min(guissu,
									ountstand) - (recov)) * 0.85);
						} else if (falgforCasesafet.equals("N")) {
							// jai code
							tcClaimEligibleAmt = Math.round((Math.min(guissu,
									ountstand) - (recov)) * 0.80);
						}
					}
					/*
					 * else
					 * if(((totalIssuedAmt)<=500000)&&(microFlag.equals("N"))) {
					 * // new code ,nerfalg,womenOprator if (
					 * falgforCasesafet.equals("Y") && (nerfalg.equals("Y") ||
					 * womenOprator.equals("F")) ){ tcClaimEligibleAmt
					 * =Math.round((Math.min(guissu,ountstand)-(recov))*0.80); }
					 * else{ tcClaimEligibleAmt
					 * =Math.round((Math.min(guissu,ountstand)-(recov))*0.75); }
					 * }
					 */

					else if (totalIssuedAmt <= 5000000
							&& (womenOprator.equals("F") || nerfalg.equals("Y"))) {

						tcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.80);

					} else if (totalIssuedAmt <= 500000 && (microFlag == "N")) {

						tcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.75);
					} else {
						tcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.75);
					}

					tcFirstInstallment = Math.round(tcClaimEligibleAmt * 0.75);
					totalTCFirstInstallment = totalTCFirstInstallment
							+ tcFirstInstallment;
					// System.out.println("First Installment is jagdish :--->"+tcFirstInstallment);

					// System.out.println("Inside inner resultSet For TC...!");
					// String newQury=
					// "Update claim_tc_detail_temp@cginter set ctd_tc_asf_deductable ='"+dedecutByMli+"',CTD_TC_CLM_ELIG_AMT ='"+tcClaimEligibleAmt+"',CTD_TC_FIRST_INST_PAY_AMT ='"+tcFirstInstallment+"',CTD_NPA_OUTSTANDING_AMT_REVISE ='"+ountstand+"',CTD_NPA_RECOVERED_REVISE ='"+recov+"' where  CGPAN='"+cgpan+"'";
					// System.out.println("The Quetrt tC=========>"+newQury);
					// int uapdValu=str.executeUpdate(newQury);
					// System.out.println("After Update to claim_tc_detail_temp@cginter value is :---->"+uapdValu);

					String Qury1 = "select count(*) from claim_tc_detail_temp@cginter where clm_ref_no ='"
							+ clmRefNum + "'";

					ResultSet rsQury1 = str.executeQuery(Qury1);
					if (rsQury1.next()) {
						try {
							// System.out.println("Inside inner resultSet For TC...!");

							String newQury = "update claim_tc_detail_temp set ctd_tc_asf_deductable ='"
									+ dedecutByMli
									+ "',CTD_TC_CLM_ELIG_AMT ='"
									+ tcClaimEligibleAmt
									+ "',CTD_TC_FIRST_INST_PAY_AMT ='"
									+ tcFirstInstallment
									+ "',CTD_NPA_OUTSTANDING_AMT_REVISE ='"
									+ ountstand
									+ "',CTD_NPA_RECOVERED_REVISE ='"
									+ recov
									+ "' where  clm_ref_no ='"
									+ clmRefNum
									+ "'  AND CGPAN='" + cgpan + "'";
							// String newQury=
							// "update claim_tc_detail_temp set ctd_tc_asf_deductable ="+dedecutByMli+",CTD_TC_CLM_ELIG_AMT ="+tcClaimEligibleAmt+",CTD_TC_FIRST_INST_PAY_AMT ="+tcFirstInstallment+",CTD_NPA_OUTSTANDING_AMT_REVISE ="+ountstand+",CTD_NPA_RECOVERED_REVISE ="+recov+"  where  clm_ref_no ='"+clmRefNum+"'  and cgpan='"+cgpan+"'";
							// System.out.println("The Quetrt tC=========>"+newQury);
							// int uapdValu=str.executeUpdate(newQury);
							int uapdValu = str.executeUpdate(newQury);
							// System.out.println("After Update to claim_tc_detail_temp@cginter value is :---->"+uapdValu);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} // term loan type over
				else { // working capital loan type start

					if (((totalIssuedAmt) <= 500000) && (microFlag.equals("Y"))) {
						// jai code
						if (falgforCasesafet.equals("Y")) {
							wcClaimEligibleAmt = Math.round((Math.min(guissu,
									ountstand) - (recov)) * 0.85);

						} else if (falgforCasesafet.equals("N")) {
							// jai code
							wcClaimEligibleAmt = Math.round((Math.min(guissu,
									ountstand) - (recov)) * 0.80);
						}
					}
					/*
					 * else
					 * if(((totalIssuedAmt)<=500000)&&(microFlag.equals("N"))) {
					 * //new code if ( falgforCasesafet.equals("Y") &&
					 * (nerfalg.equals("Y") || womenOprator.equals("F")) ){
					 * 
					 * wcClaimEligibleAmt
					 * =Math.round((Math.min(guissu,ountstand)-(recov))*0.80); }
					 * else{ wcClaimEligibleAmt
					 * =Math.round((Math.min(guissu,ountstand)-(recov))*0.75); }
					 * }
					 */

					else if (totalIssuedAmt <= 5000000
							&& (womenOprator == "F" || nerfalg == "Y")) {
						wcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.80);
					} else if (totalIssuedAmt <= 500000 && (microFlag == "N")) {
						wcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.75);
					} else {
						wcClaimEligibleAmt = Math.round((Math.min(guissu,
								ountstand) - (recov)) * 0.75);

					}
					wcFirstInstallment = Math.round(wcClaimEligibleAmt * 0.75);

					totalWCFirstInstallment = totalTCFirstInstallment
							+ wcFirstInstallment;
					// System.out.println("First Installment for Working Capital By Jagdish :--->:"+wcFirstInstallment);
					// System.out.println("wcClaimEligibleAmt By Jagdish :--->:"+wcClaimEligibleAmt);

					// String
					// wcUpdateQuery="update claim_wc_detail_temp@cginter set CWD_WC_ASF_DEDUCTABLE = '"+dedecutByMli+"',CWD_WC_CLM_ELIG_AMT = '"+wcClaimEligibleAmt+"',CWD_WC_FIRST_INST_PAY_AMT = '"+wcFirstInstallment+"',CWD_NPA_OUTSTANDING_AMT_REVISE = '"+ountstand+"',CWD_NPA_RECOVERED_REVISE = '"+recov+"' where  CGPAN='"+cgpan+"'"
					// ;
					// System.out.println("The Quetrt WC=========>"+wcUpdateQuery);
					// int uapdValuforWC=str.executeUpdate(wcUpdateQuery);
					// System.out.println("After Update to update claim_wc_detail_temp@cginter value is :---->"+uapdValuforWC);

					//
					String wcSelqury = "select count(*)  from claim_wc_detail_temp@cginter where clm_ref_no = '"
							+ clmRefNum + "'";
					ResultSet rswcSelqury = str.executeQuery(wcSelqury);
					if (rswcSelqury.next()) {
						String wcUpdateQuery = "update claim_wc_detail_temp set CWD_WC_ASF_DEDUCTABLE = '"
								+ dedecutByMli
								+ "',CWD_WC_CLM_ELIG_AMT = '"
								+ wcClaimEligibleAmt
								+ "',CWD_WC_FIRST_INST_PAY_AMT = '"
								+ wcFirstInstallment
								+ "',CWD_NPA_OUTSTANDING_AMT_REVISE = '"
								+ ountstand
								+ "',CWD_NPA_RECOVERED_REVISE = '"
								+ recov
								+ "' where clm_ref_no = '"
								+ clmRefNum
								+ "' AND CGPAN='" + cgpan + "'";
						// String
						// wcUpdateQuery="update claim_wc_detail_temp@cginter set CWD_WC_ASF_DEDUCTABLE = "+dedecutByMli+",CWD_WC_CLM_ELIG_AMT = "+wcClaimEligibleAmt+",CWD_WC_FIRST_INST_PAY_AMT = "+wcFirstInstallment+",CWD_NPA_OUTSTANDING_AMT_REVISE = "+ountstand+",CWD_NPA_RECOVERED_REVISE = "+recov+" where clm_ref_no = '"+clmRefNum+"' AND CGPAN='"+cgpan+"'"
						// ;
						// System.out.println("The Quetrt WC=========>"+wcUpdateQuery);
						int uapdValuforWC = str.executeUpdate(wcUpdateQuery);

						// System.out.println("After Update to update claim_wc_detail_temp@cginter value is :---->"+uapdValuforWC);
					}

				} // //working capital loan type over

				cgpan = "";
			} // for close

			// totalTCFirstInstallment+totalWCFirstInstallment-totalDeductedByMli
			connection.commit();

			payAmntNow = Double.toString(totalTCFirstInstallment
					+ totalWCFirstInstallment - totalDeductedByMli);
			clmdtl.setTotalAmtPayNow(payAmntNow);

			// payAmntNow =
			// Double.toString(tcFirstInstallment+wcFirstInstallment-tcServiceFee-wcServiceFee);
			// clmdtl.setTotalAmtPayNow(payAmntNow);

		} catch (Exception e) // try after main loop over
		{
			Log.log(Log.ERROR, "CPDAO", methodName, e.getMessage());
			Log.logException(e);
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());

			// e.printStackTrace();
		}

		// jai code for CGPAN wise data over

		finally {
			if (newConn) {
				DBConnection.freeConnection(connection);
			}
		}

		Log.log(Log.INFO, "CPDAO", methodName, "Exited");
		return payAmntNow;
	}

	/* added by upchar@path on 04-04-2013 */

	public ArrayList getPaymentDetailsForMemberID(String mliId,
			String startDate, String endDate) throws DatabaseException {

		// System.out.println("Entered in cpdao----getPaymentDetailsForMemberID----");
		// System.out.println("mli id:"+ mliId
		// +"----Startg date:"+startDate+"----end datye:"+endDate);
		Connection conn = null;
		CallableStatement callableStmt = null;
		java.sql.ResultSet rs = null;
		conn = DBConnection.getConnection();
		java.util.Date fromDate = null;
		java.util.Date toDate = null;
		java.util.ArrayList pendingCaseDetails = new java.util.ArrayList();
		String[] pendingCaseDetailsArray = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			fromDate = sdf.parse(startDate);
			toDate = sdf.parse(endDate);
		} catch (ParseException e) {
			// TODO
		}

		try {

			callableStmt = conn
					.prepareCall("{? = call packgetclmsetlist.funcgetclmsetlistformli(?,?,?,?,?)}");

			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER); // error
																			// code
			callableStmt.setDate(2, new java.sql.Date(fromDate.getTime()));
			callableStmt.setDate(3, new java.sql.Date(toDate.getTime()));
			callableStmt.setString(4, mliId);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR); // for
																			// exception

			callableStmt.execute();

			int error = callableStmt.getInt(1);
			String exception = callableStmt.getString(6);

			// System.out.println("function return value:"+error);
			if (error == Constants.FUNCTION_FAILURE) {

				callableStmt.close();
				callableStmt = null;

				throw new DatabaseException(exception);

			} else {
				rs = (ResultSet) callableStmt.getObject(5);

				while (rs.next()) {
					pendingCaseDetailsArray = new String[15];

					pendingCaseDetailsArray[0] = rs.getString(1);
					pendingCaseDetailsArray[1] = rs.getString(2);
					pendingCaseDetailsArray[2] = rs.getString(3);
					pendingCaseDetailsArray[3] = rs.getString(4);
					pendingCaseDetailsArray[4] = rs.getString(5);
					pendingCaseDetailsArray[5] = rs.getString(6);
					pendingCaseDetailsArray[6] = rs.getString(7);
					pendingCaseDetailsArray[7] = rs.getString(8);
					pendingCaseDetailsArray[8] = rs.getString(9);
					pendingCaseDetailsArray[9] = rs.getString(10);
					pendingCaseDetailsArray[10] = rs.getString(11);
					pendingCaseDetailsArray[11] = rs.getString(12);
					pendingCaseDetailsArray[12] = rs.getString(13);
					pendingCaseDetailsArray[13] = rs.getString(14);
					pendingCaseDetailsArray[14] = rs.getString(15);
					pendingCaseDetails.add(pendingCaseDetailsArray);
				}
				rs.close();
				rs = null;
				callableStmt.close();
				callableStmt = null;
			}

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "CPDAO", "getPaymentDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				conn.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "CPDAO", "getPaymentDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(conn);
		}

		return pendingCaseDetails;
	}

	public void updateDayWisePaymentDetails(List paymentDetailList,
			String[] paymentArray) throws DatabaseException {
		// System.out.println("enteredd in updateDayWisePaymentDetails-----");
		Connection conn = null;
		CallableStatement callableStmt = null;

		conn = DBConnection.getConnection();
		// java.util.Date paymentDate = null;
		// java.util.Date OutwardDate = null;

		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String UTRNO = paymentArray[0];
		String paymentDateStr = paymentArray[1];
		String accountNO = paymentArray[2];
		String outwardNO = paymentArray[3];
		String outwardDateStr = paymentArray[4];

		java.util.Date paymentDate = null;
		java.util.Date OutwardDate = null;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			paymentDate = formatter.parse(paymentDateStr);
			OutwardDate = formatter.parse(outwardDateStr);

			// ltrDt1 = formatter.parse(ltrDt);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String clmrefno = null;

		try {

			callableStmt = conn
					.prepareCall("{? = call Funcupdclmpymtdet(?,?,?,?,?,?,?)}");
			Iterator itr = paymentDetailList.iterator();

			while (itr.hasNext()) {

				clmrefno = (String) itr.next();
				callableStmt.registerOutParameter(1, java.sql.Types.INTEGER); // error
																				// code
				callableStmt.setString(2, clmrefno);
				callableStmt.setString(3, UTRNO);
				callableStmt.setDate(4,
						new java.sql.Date(paymentDate.getTime()));
				callableStmt.setString(5, accountNO);
				callableStmt.setString(6, outwardNO);
				callableStmt.setDate(7,
						new java.sql.Date(OutwardDate.getTime()));
				callableStmt.registerOutParameter(8, java.sql.Types.VARCHAR); // for
																				// exception

				callableStmt.execute();

				int error = callableStmt.getInt(1);
				String exception = callableStmt.getString(8);

				System.out.println("function return value:" + error);
				if (error == Constants.FUNCTION_FAILURE) {

					callableStmt.close();
					callableStmt = null;

					throw new DatabaseException(exception);
				}
				// }else{
				// callableStmt.close();
				// callableStmt=null;
				// }

			} // while-end
			callableStmt.close();
			callableStmt = null;

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "CPDAO", "updateDayWisePaymentDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				conn.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "CPDAO", "updateDayWisePaymentDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(conn);
		}

	}

	// added on 13-11-2013

	public Map getCgpanFlagsForBid(String borrowerId) throws DatabaseException {
		CallableStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map map = new HashMap();
		Vector tccgpans = new Vector();
		Vector wccgpans = new Vector();
		conn = DBConnection.getConnection(true);

		try {
			stmt = conn
					.prepareCall("{?=call packgetcgpdetailforclaim.funcGetCGPFlags(?,?,?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, borrowerId);
			stmt.registerOutParameter(3, Constants.CURSOR);
			stmt.registerOutParameter(4, Types.VARCHAR);
			stmt.execute();

			int errorCode = stmt.getInt(1);
			String exception = stmt.getString(4);
			if (errorCode == Constants.FUNCTION_FAILURE) {
				stmt.close();
				stmt = null;
				throw new DatabaseException(exception);
			} else {
				rs = (ResultSet) stmt.getObject(3);
				while (rs.next()) {
					Map m = new HashMap();
					String cgpan = (String) rs.getString(1);
					m.put("CGPAN", cgpan);
					m.put("MICROFLAG", rs.getString(2));
					m.put("SCHEME", rs.getString(3));
					m.put("GENDER", rs.getString(4));
					m.put("SANCFLAG", rs.getString(5));
					m.put("NERFLAG", rs.getString(6));
					m.put("LASTDSBRSMNTDT", rs.getDate(7));
					m.put("TCPRINREPAMT", rs.getDouble(8));
					m.put("TCINTREPAMT", rs.getDouble(9));
					m.put("TCPRINNPAOSAMT", rs.getDouble(10));
					m.put("WCPRINNPAOSAMT", rs.getDouble(11));
					m.put("TOTALNPAOSAMT", rs.getDouble(12));
					m.put("TOTALDISBAMNT", Double.valueOf(rs.getDouble(16)));
					if ("TC".equals(cgpan.substring(cgpan.length() - 2))) {
						tccgpans.add(m);
					} else {
						wccgpans.add(m);
					}
				}

				map.put("tccgpans", tccgpans);
				map.put("wccgpans", wccgpans);
			}

		} catch (SQLException e) {
			// TODO
		} finally {
			try {
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
				DBConnection.freeConnection(conn);
			} catch (SQLException e) {

			}
		}
		return map;
	}

	public Vector getClaimApprovalDetailsForNewCases(String loggedUsr,
			String bankName,String filetype) throws DatabaseException {
		
		//System.out.println("getClaimApprovalDetailsForNewCases:::::::::");
		
		Log.log(Log.INFO, "CPDAO", "getClaimApprovalDetailsForNewCases()",
				"Entered!");
		Vector claimDetails = new Vector();
		CallableStatement callableStmt = null;
		Connection conn = null;
		ResultSet resultset = null;
		ClaimDetail claimdetail = null;
		String flag = ClaimConstants.FIRST_INSTALLMENT;
		String cgclan = null;
		String bid = null;
		String memberId = null;
		String claimRefNumber = null;
		double claimApprovedAmnt = 0.0;
		double applicationApprovedAmnt = 0.0;
		double tcApprovedAmt = 0.0;
		double wcApprovedAmt = 0.0;
		double tcOutstanding = 0.0;
		double wcOutstanding = 0.0;
		double tcrecovery = 0.0;
		double wcrecovery = 0.0;
		double tcEligibleAmt = 0.0;
		double wcEligibleAmt = 0.0;
		double tcDeduction = 0.0;
		double wcDeduction = 0.0;
		double tcFirstInstallment = 0.0;
		double wcFirstInstallment = 0.0;
		java.util.Date clmApprvdDate = null;
		java.util.Date npaEffectiveDate = null;
		double outstandingAmntAsOnNPA = 0.0;
		double appliedClaimAmnt = 0.0;
		String clmStatus = null;
		String comments = null;
		String forwardedToUser = null;
		String unitName = null;
		int status = -1;
		String errorCode = null;
		String stateCode = null;
		String gstNo = null;
//added by koteswar for adding serivece tax
		double asfRefundableForTC = 0.0;
		double asfRefundableForWC = 0.0;
		String refundFlag = "N";
		
		/*int tcSerTaxDed = 0;
		int wcSerTaxDed = 0;
		
		int tcSwbhaCessDed = 0;
		int wcSwbhaCessDed = 0;*/
		
		//Diksha
		 double tcSerTaxDed = 0.0D;
	        double wcSerTaxDed = 0.0D;
	        double tcSwbhaCessDed = 0.0D;
	        double wcSwbhaCessDed = 0.0D;
	        double tckkCessDed = 0.0D;
	        double wckkCessDed = 0.0D;
	       //Diksha end
		//System.out.println("filetype=="+filetype);
		
		try {
			conn = DBConnection.getConnection();
			// System.out.println("before call packGetClmforAppprocess.funcGetClmAppforuser");
			callableStmt = conn
					.prepareCall("{? = call Packgetpackagepath1.funcGetClmApproval_list(?,?,?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, loggedUsr);
			callableStmt.setString(3, bankName);
			callableStmt.setString(4, filetype);
			callableStmt.registerOutParameter(5, Constants.CURSOR);
			callableStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStmt.execute();
			//System.out.println("Point 1==");
			// System.out.println("after call packGetClmforAppprocess.funcGetClmAppforuser");
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(6);
			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getClaimApprovalDetails()",
						"SP returns a 1 in retrieving Claim Processing Results.Error code is :"
								+ errorCode);
				callableStmt.close();
				//System.out.println("Point 2");
				throw new DatabaseException(errorCode);
			} else if (status == Constants.FUNCTION_SUCCESS) {
				// System.out.println("success call packGetClmforAppprocess.funcGetClmAppforuser");
				// extracting the resultset from the callable statement
				resultset = (ResultSet) callableStmt.getObject(5);
				//System.out.println("Point 3");
				while (resultset.next()) {
					//System.out.println("Point 4");
					memberId = resultset.getString(1);
					claimRefNumber = resultset.getString(2);
					unitName = resultset.getString(3);
					bid = resultset.getString(4);
					tcApprovedAmt = resultset.getDouble(5);
					wcApprovedAmt = resultset.getDouble(6);
					tcOutstanding = resultset.getDouble(7);
					wcOutstanding = resultset.getDouble(8);
					tcrecovery = resultset.getDouble(9);
					wcrecovery = resultset.getDouble(10);
					tcEligibleAmt = resultset.getDouble(11);
					wcEligibleAmt = resultset.getDouble(12);
					tcDeduction = resultset.getDouble(13);
					wcDeduction = resultset.getDouble(14);
					tcFirstInstallment = resultset.getDouble(15);
					wcFirstInstallment = resultset.getDouble(16);
					comments = resultset.getString(17);
					asfRefundableForTC = resultset.getDouble(18);
					asfRefundableForWC = resultset.getDouble(19);
					refundFlag = resultset.getString(20);
					clmStatus = resultset.getString(21);
					/*tcSerTaxDed=(int)(resultset.getDouble(22));
					wcSerTaxDed=(int)(resultset.getDouble(23));
					
					tcSwbhaCessDed=(int)(resultset.getDouble(24));
					wcSwbhaCessDed=(int)(resultset.getDouble(25));
*/
					//Diksha
					 tcSerTaxDed = resultset.getDouble(22);
                     wcSerTaxDed = resultset.getDouble(23);
                     tcSwbhaCessDed = resultset.getDouble(24);
                     wcSwbhaCessDed = resultset.getDouble(25);
                     tckkCessDed = resultset.getDouble(26);
                     wckkCessDed = resultset.getDouble(27);
					stateCode = resultset.getString(28);//rajuk
					//System.out.println("stateCode=="+stateCode);
					
					gstNo=resultset.getString(29);//rajuk
					//System.out.println("gstNo==="+gstNo); 
					String claimRetRemarks = resultset.getString(30);
                   // System.out.println((new StringBuilder("claimRetRemarks--")).append(claimRetRemarks).toString());
                    String claimRetdT = resultset.getString(31);
                    String appRemarks = resultset.getString(32);
                    //Diksha end
					if (flag.equals(ClaimConstants.FIRST_INSTALLMENT)) {
						System.out.println("Point 5");
						claimdetail = new ClaimDetail();
						claimdetail.setMliId(memberId);
						claimdetail.setClaimRefNum(claimRefNumber);
						claimdetail.setAppRemarks(appRemarks);
	                    claimdetail.setClaimRetRemarks(claimRetRemarks);
	                    claimdetail.setClaimRetdT(claimRetdT);
						claimdetail.setSsiUnitName(unitName);
						claimdetail.setBorrowerId(bid);
						claimdetail.setTcApprovedAmt(tcApprovedAmt);
						claimdetail.setWcApprovedAmt(wcApprovedAmt);
						claimdetail.setTcOutstanding(tcOutstanding);
						claimdetail.setWcOutstanding(wcOutstanding);
						claimdetail.setTcrecovery(tcrecovery);
						claimdetail.setWcrecovery(wcrecovery);
						claimdetail.setTcClaimEligibleAmt(tcEligibleAmt);
						claimdetail.setWcClaimEligibleAmt(wcEligibleAmt);
						claimdetail.setAsfDeductableforTC(tcDeduction);
						claimdetail.setAsfDeductableforWC(wcDeduction);
						claimdetail.setTcFirstInstallment(tcFirstInstallment);
						claimdetail.setWcFirstInstallment(wcFirstInstallment);
						claimdetail.setComments(comments);
						claimdetail.setAsfRefundableForTC(asfRefundableForTC);
						claimdetail.setAsfRefundableForWC(asfRefundableForWC);
						claimdetail.setRefundFlag(refundFlag);
						claimdetail.setClmStatus(clmStatus);
						
						claimdetail.setTcSerTaxDed(tcSerTaxDed);
						claimdetail.setWcSerTaxDed(wcSerTaxDed);
						
						
						claimdetail.setTcSwbhaCessDed(tcSwbhaCessDed);
						claimdetail.setWcSwbhaCessDed(wcSwbhaCessDed);
						
						//Diksha
						claimdetail.setTckkCessDed(tckkCessDed);
                        claimdetail.setWckkCessDed(wckkCessDed);
                        //Diksha end
						claimdetail.setStateCode(stateCode);//rajuk
						claimdetail.setGstNo(gstNo);//rajuk
						
						
					
					}
					
					if (claimdetail != null) {
						// adding the claimdetail object to the vector
						claimDetails.addElement(claimdetail);
					}
				}
				System.out.println("Point 6");
				resultset.close();
				resultset = null;
				// closing the callable statement
				System.out.println("Point 7");
				callableStmt.close();
				callableStmt = null;
				System.out.println("Point 8");

				if (claimDetails != null && claimDetails.size() != 0) {
					for (int i = 0; i < claimDetails.size(); i++) {
						ClaimDetail clm = (ClaimDetail) claimDetails.get(i);
						// System.out.println("going for:"+clm.getClaimRefNum());
						// System.out.println("before call packGetClmforAppprocess.funcCalcClaimElig");
						callableStmt = conn.prepareCall("{?=call packGetClmforAppprocess.funcCalcClaimElig(?,?,?,?)}");
						callableStmt.registerOutParameter(1, Types.INTEGER);
						callableStmt.setString(2, clm.getClaimRefNum());
						callableStmt.registerOutParameter(3, Types.VARCHAR);
						callableStmt.registerOutParameter(4, Constants.CURSOR);
						callableStmt.registerOutParameter(5, Types.VARCHAR);
						callableStmt.execute();
						// System.out.println("after call packGetClmforAppprocess.funcCalcClaimElig");
						status = callableStmt.getInt(1);
						errorCode = callableStmt.getString(5);
						if (status == Constants.FUNCTION_FAILURE) {
							Log.log(Log.ERROR, "CPDAO",
									"getClaimApprovalDetails()",
									"SP returns a 1 in retrieving Claim Processing Results.Error code is :"
											+ errorCode);
							callableStmt.close();
							throw new DatabaseException(errorCode);
						} else if (status == Constants.FUNCTION_SUCCESS) {
							// System.out.println("success call packGetClmforAppprocess.funcCalcClaimElig:");
							StringBuilder sb = new StringBuilder();
							String recommondation = (String) callableStmt
									.getString(3);
							// System.out.println("success---recommondation:"+recommondation);
							clm.setRecommendation(recommondation);
							ResultSet rs2 = (ResultSet) callableStmt
									.getObject(4);
							while (rs2.next()) {
								// System.out.println("status:"+rs2.getString(1)+"----cgpan:"+rs2.getString(2)+"---description:"+rs2.getString(3));
								sb.append(rs2.getString(1)); // status
								sb.append("&" + rs2.getString(2)); // cgpan
								sb.append("&" + rs2.getString(3)); // condition
								sb.append(".");
							}
							// System.out.println("String buffer---"+sb.toString());
							clm.setRecommendationData(sb.toString());
							rs2.close();
							rs2 = null;
							callableStmt.close();
							callableStmt = null;
						}
					}
				}
			}
		} catch (SQLException sqlexception) {
			Log.log(Log.ERROR, "CPDAO", "getClaimApprovalDetailsForNewCases()",
					"Error retrieving Details for Processing Claims from the database");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			DBConnection.freeConnection(conn);
		}
		Log.log(Log.INFO, "CPDAO", "getClaimApprovalDetailsForNewCases()",
				"Exited!");
		return claimDetails;
	}

	public Vector getCGPANDetailsForBidClaim(String borrowerId, String memberId)
			throws DatabaseException {
		Log.log(4, "CPDAO", "getCGPANDetailsForBorrowerId()", "Entered!");
		ResultSet rs = null;
		HashMap cgpandetails = null;
		Vector allcgpandetails = new Vector();
		ApplicationDAO appDAO = new ApplicationDAO();
		Application application = null;
		CallableStatement callableStmt = null;
		Connection conn = null;
		int status = -1;
		String errorCode = null;

		try {
			conn = DBConnection.getConnection();
			callableStmt = conn
					.prepareCall("{? = call packGetCGPANForBorrower.funcGetCGPANForBorrower(?,?,?)}");
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, borrowerId);
			callableStmt.registerOutParameter(3, Constants.CURSOR);
			callableStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			// System.out.println("before call packGetCGPANForBorrower.funcGetCGPANForBidClaim:"+
			// new java.util.Date());
			callableStmt.execute();
			// System.out.println("after call packGetCGPANForBorrower.funcGetCGPANForBidClaim:"+
			// new java.util.Date());
			status = callableStmt.getInt(1);
			errorCode = callableStmt.getString(4);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "CPDAO", "getCGPANDetailsForBorrowerId()",
						"SP returns a 1. Error code is :" + errorCode);

				callableStmt.close();
				throw new DatabaseException(errorCode);
			}
			if (status == 0) {
				rs = (ResultSet) callableStmt.getObject(3);
				while (rs.next()) {
					String cgpan = null;
					double approvedAmount = 0.0D;
					double enhancedApprovedAmount = 0.0D;
					double reapproveAmount = 0.0D;
					String appRefNum = null;
					String loantype = null;
					java.util.Date guarStartDt = null;
					String applicationStatus = null;

					cgpan = rs.getString(1);
					if (cgpan != null) {
						appRefNum = getAppRefNumber(cgpan);
						application = appDAO.getAppForAppRef(null, appRefNum);
						reapproveAmount = application.getReapprovedAmount();
						approvedAmount = rs.getDouble(2);
						enhancedApprovedAmount = rs.getDouble(3);
						loantype = rs.getString(4);
						guarStartDt = rs.getDate(5);
						applicationStatus = rs.getString(6);

						if (cgpan != null) {
							cgpandetails = new HashMap();
							cgpandetails.put("CGPAN", cgpan);
							if (reapproveAmount == 0.0D) {
								cgpandetails.put("ApprovedAmount", new Double(
										approvedAmount));
							}
							if (reapproveAmount > 0.0D) {
								cgpandetails.put("ApprovedAmount", new Double(
										reapproveAmount));
							}
							cgpandetails.put("EnhancedApprovedAmount",
									new Double(enhancedApprovedAmount));
							cgpandetails.put("LoanType", loantype);
							cgpandetails.put("GUARANTEESTARTDT", guarStartDt);
							cgpandetails.put("APPLICATION_STATUS",
									applicationStatus);
							allcgpandetails.add(cgpandetails);
						}
					}
				}

				rs.close();
				rs = null;

				callableStmt.close();
				callableStmt = null;
			}
		} catch (SQLException sqlexception) {
			Log.log(2, "CPDAO", "getCGPANDetailsForBorrowerId()",
					"Error retrieving CGPAN Details for the Borrower!");
			throw new DatabaseException(sqlexception.getMessage());
		} finally {
			// DBConnection.freeConnection(conn);
		}
		String clmRefNumber = null;
		Statement stmt = null;
		String query = "select clm_ref_no from claim_detail where mem_bnk_id||mem_zne_id||mem_brn_id='"
				+ memberId + "' and bid='" + borrowerId + "'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				clmRefNumber = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			rs = null;
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = null;
			DBConnection.freeConnection(conn);
		}

		/* DISBURSEMENT CHECKING */
		int type = 0;
		ArrayList disbursementDetails = null;
		ArrayList repaymentDetails = null;
		ArrayList disbDtls = null;
		ArrayList nestedDtls = null;
		PeriodicInfo pi = null;
		if (clmRefNumber == null) {
			disbursementDetails = getDisbursementDetails(borrowerId, type);
			if (disbursementDetails != null) {
				if (disbursementDetails.size() > 0) {
					Log.log(4, "CPDAO", "getCGPANDetailsForBorrowerId()",
							"Size of disbursementDetails Dtls :"
									+ disbursementDetails.size());
					for (int i = 0; i < disbursementDetails.size(); i++) {
						pi = (PeriodicInfo) disbursementDetails.get(i);
						if (pi != null) {
							disbDtls = pi.getDisbursementDetails();
							if (disbDtls != null) {
								Log.log(4,
										"CPDAO",
										"getCGPANDetailsForBorrowerId()",
										"Size of disbDtls Dtls :"
												+ disbDtls.size());
								if (disbDtls.size() > 0) {
									for (int j = 0; j < disbDtls.size(); j++) {
										Disbursement dsbrsmnt = (Disbursement) disbDtls
												.get(j);
										if (dsbrsmnt != null) {
											nestedDtls = dsbrsmnt
													.getDisbursementAmounts();
											if ((nestedDtls == null)
													|| (nestedDtls.size() != 0)) {
												String lastcgpan = null;
												java.util.Date lastDsbrsmntDt = null;
												java.util.Date presentDsbrsmntDt = null;
												Log.log(4,
														"CPDAO",
														"getCGPANDetailsForBorrowerId()",
														"Size of Nested Dtls :"
																+ nestedDtls
																		.size());
												for (int k = 0; k < nestedDtls
														.size(); k++) {
													DisbursementAmount dbamnt = (DisbursementAmount) nestedDtls
															.get(k);
													if (dbamnt != null) {
														String cgpan = dbamnt
																.getCgpan();
														Log.log(4,
																"CPDAO",
																"getCGPANDetailsForBorrowerId()",
																"cgpan :"
																		+ cgpan);

														presentDsbrsmntDt = dbamnt
																.getDisbursementDate();
														Log.log(4,
																"CPDAO",
																"getCGPANDetailsForBorrowerId()",
																"presentDsbrsmntDt :"
																		+ presentDsbrsmntDt);
														if (k == 0) {
															lastcgpan = cgpan;
															lastDsbrsmntDt = presentDsbrsmntDt;
														}
														if (k > 0) {
															if (cgpan
																	.equals(lastcgpan)) {
																if (presentDsbrsmntDt != null) {
																	if (presentDsbrsmntDt
																			.compareTo(lastDsbrsmntDt) > 0) {
																		lastDsbrsmntDt = presentDsbrsmntDt;
																	}
																	if (presentDsbrsmntDt
																			.compareTo(lastDsbrsmntDt) == 0) {
																		lastDsbrsmntDt = presentDsbrsmntDt;
																	}

																}

															} else {
																continue;
															}

														}

														HashMap hashmap = null;
														Log.log(4,
																"CPDAO",
																"getCGPANDetailsForBorrowerId()",
																"allcgpandetails size :"
																		+ allcgpandetails
																				.size());
														for (int m = 0; m < allcgpandetails
																.size(); m++) {
															hashmap = (HashMap) allcgpandetails
																	.elementAt(m);
															Log.log(4,
																	"CPDAO",
																	"getCGPANDetailsForBorrowerId()",
																	"priting hasmap in allcgpandetails vector :"
																			+ hashmap);
															if (hashmap != null) {
																if (hashmap
																		.containsKey("CGPAN")) {
																	String cgpn = (String) hashmap
																			.get("CGPAN");

																	if ((cgpn != null)
																			&& (!cgpn
																					.equals(""))) {
																		if (cgpn.equals(lastcgpan)) {
																			hashmap = (HashMap) allcgpandetails
																					.remove(m);
																			hashmap.put(
																					"LASTDSBRSMNTDT",
																					lastDsbrsmntDt);
																			allcgpandetails
																					.add(m,
																							hashmap);
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			disbDtls = null;
			nestedDtls = null;
		}
		/* CLAIM DETAIL CHECKING */
		if (clmRefNumber != null) {
			Vector tcDetails = null;
			ClaimDetail cd = getDetailsForClaimRefNumber(clmRefNumber);
			if (cd != null) {
				tcDetails = cd.getTcDetails();

				if (tcDetails != null) {
					for (int i = 0; i < tcDetails.size(); i++) {
						HashMap map = (HashMap) tcDetails.elementAt(i);
						if (map != null) {
							String pan = (String) map.get("CGPAN");
							java.util.Date dsbrsDate = (java.util.Date) map
									.get("LASTDSBRSMNTDT");
							HashMap hashmap = null;
							for (int m = 0; m < allcgpandetails.size(); m++) {
								hashmap = (HashMap) allcgpandetails
										.elementAt(m);
								Log.log(4, "CPDAO",
										"getCGPANDetailsForBorrowerId()",
										"priting hasmap in allcgpandetails vector :"
												+ hashmap);
								if (hashmap != null) {
									if (hashmap.containsKey("CGPAN")) {
										String cgpn = (String) hashmap
												.get("CGPAN");

										if ((cgpn != null)
												&& (!cgpn.equals(""))) {
											if (cgpn.equals(pan)) {
												hashmap = (HashMap) allcgpandetails
														.remove(m);
												hashmap.put("LASTDSBRSMNTDT",
														dsbrsDate);
												allcgpandetails.add(m, hashmap);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		type = 1;
		HashMap hashmap = null;
		ArrayList repayments = null;
		ArrayList repayAmounts = null;
		Repayment repayment = null;
		RepaymentAmount repayamntAmnt = null;
		for (int m = 0; m < allcgpandetails.size(); m++) {
			hashmap = (HashMap) allcgpandetails.elementAt(m);
			if (hashmap != null) {
				if (hashmap.containsKey("CGPAN")) {
					String cgpn = (String) hashmap.get("CGPAN");
					if ((cgpn != null) && (!cgpn.equals(""))) {
						repaymentDetails = getCPRepaymentDetails(cgpn, type);
						for (int i = 0; i < repaymentDetails.size(); i++) {
							pi = (PeriodicInfo) repaymentDetails.get(i);
							if (pi != null) {
								repayments = pi.getRepaymentDetails();
								if ((repayments == null)
										|| (repayments.size() != 0)) {
									double totalRepaidAmnt = 0.0D;
									for (int j = 0; j < repayments.size(); j++) {
										repayment = (Repayment) repayments
												.get(j);
										if (repayment != null) {
											repayAmounts = repayment
													.getRepaymentAmounts();
											if ((repayAmounts == null)
													|| (repayAmounts.size() != 0)) {
												String lastPan = null;

												for (int k = 0; k < repayAmounts
														.size(); k++) {
													repayamntAmnt = (RepaymentAmount) repayAmounts
															.get(k);
													String pan = repayamntAmnt
															.getCgpan();

													if (pan != null) {
														double repaidAmnt = repayamntAmnt
																.getRepaymentAmount();

														if (k == 0) {
															lastPan = pan;
															totalRepaidAmnt = repaidAmnt
																	+ totalRepaidAmnt;
														} else if (pan
																.equals(lastPan)) {
															lastPan = pan;
															totalRepaidAmnt = repaidAmnt
																	+ totalRepaidAmnt;
															continue;
														}

														hashmap = (HashMap) allcgpandetails
																.remove(m);
														hashmap.put(
																"AMNT_REPAID",
																new Double(
																		totalRepaidAmnt));
														allcgpandetails.add(m,
																hashmap);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		Log.log(4, "CPDAO", "getCGPANDetailsForBorrowerId()", "Exited!");
		for (int i = 0; i < allcgpandetails.size(); i++) {
			HashMap m = (HashMap) allcgpandetails.elementAt(i);
			if (m != null) {
				Log.log(4, "CPDAO", "getCGPANDetailsForBorrowerId()",
						"------> Printing the hashmap from CPDAO:" + m);
			}
		}

		return allcgpandetails;
	}

}

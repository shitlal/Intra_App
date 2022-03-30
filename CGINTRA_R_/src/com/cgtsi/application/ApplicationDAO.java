//Source file: C:\\CGTSI\\Vss\\com\\cgtsi\\application\\ApplicationDAO.java

package com.cgtsi.application;

import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.Message;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.User;
import com.cgtsi.claim.CPDAO;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Constants;
import com.cgtsi.common.Mailer;
//import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoUserFoundException;
import com.cgtsi.mcgs.MCGSProcessor;
import com.cgtsi.mcgs.MCGFDetails;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.CallableStatement;
//import java.sql.Statement;
import java.sql.Statement;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.ResultSet;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;
import com.cgtsi.util.DateConverter;
import com.cgtsi.common.Log;
import com.cgtsi.receiptspayments.DemandAdvice;
import com.cgtsi.receiptspayments.GuaranteeFee;
import com.cgtsi.receiptspayments.RpConstants;
import com.cgtsi.receiptspayments.RpDAO;
import com.cgtsi.receiptspayments.RpHelper;
import com.cgtsi.receiptspayments.RpProcessor;
import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.RegistrationDAO;
import com.cgtsi.risk.RiskManagementProcessor;
import com.cgtsi.risk.SubSchemeValues;

/**
 * This class encapsulates the data base operations.
 */
public class ApplicationDAO {
	// private Connection connection = null;

	/**
	 * @roseuid 39B875C80295
	 */
	public ApplicationDAO() {

	}

	/**
	 * Access method for the connection property.
	 * 
	 * @return the current value of the connection property
	 * 
	 *         public Connection getConnection()
	 * 
	 *         { Connection connection=DBConnection.getConnection(false); return
	 *         connection; }
	 */
	/**
	 * Sets the value of the connection property.
	 * 
	 * @param aConnection
	 *            the new value of the connection property
	 * 
	 *            public void setConnection(Connection aConnection) { Connection
	 *            connection = aConnection; }
	 * 
	 *            /** This method is used to submit a new application. New
	 *            application could be for TC,WC,Composite,Both, Additional Term
	 *            loan.
	 * @param apps
	 * @throws DatabaseException
	 * @roseuid 3972E72303E0
	 */

	public SSIDetails submitApp(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "Entered");

		SSIDetails ssiDetail = new SSIDetails();

		/*
		 * if (connection==null) { connection=DBConnection.getConnection(false);
		 * }
		 */
		//// System.out.println("in  Application16q13");

		String appLoanType = apps.getLoanType();

		try {

			// SP for SSI Details*****************

			String coveredValue = (apps.getBorrowerDetails())
					.getPreviouslyCovered();
			if (coveredValue.equals("Y")) { // if borrower covered then get SSI
											// ref No,update SSi Details

				String cgbid = ((apps.getBorrowerDetails()).getSsiDetails())
						.getCgbid();
				String cgpan = apps.getCgpan();
				if (cgbid != null && !cgbid.equals("")) {
					//// System.out.println("in  Application16q13aa");
					CallableStatement ssiRefNoForCgbid = connection
							.prepareCall("{?=call funcGetSSIRefNoforBID(?,?,?)}");
					ssiRefNoForCgbid.registerOutParameter(1, Types.INTEGER);
					ssiRefNoForCgbid.registerOutParameter(2, Types.INTEGER); // Get
																				// SSI
																				// RefNo
					ssiRefNoForCgbid.registerOutParameter(4, Types.VARCHAR);

					ssiRefNoForCgbid.setString(3, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCgbid());

					ssiRefNoForCgbid.executeQuery();
					int ssiRefNoForCgbidValue = ssiRefNoForCgbid.getInt(1);

					if (ssiRefNoForCgbidValue == Constants.FUNCTION_FAILURE) {

						String error = ssiRefNoForCgbid.getString(4);

						ssiRefNoForCgbid.close();
						ssiRefNoForCgbid = null;

						Log.log(Log.ERROR, "ApplicationDAO", "submitApp", error);

						connection.rollback();
						//// System.out.println("in  Application16q13ab");
						throw new DatabaseException(
								ssiRefNoForCgbid.getString(4));

					} else {
						((apps.getBorrowerDetails()).getSsiDetails())
								.setBorrowerRefNo(ssiRefNoForCgbid.getInt(2));

						ssiRefNoForCgbid.close();
						ssiRefNoForCgbid = null;

					}

					//// System.out.println("in  Application16q13ac");
				} else if (cgpan != null && !cgpan.equals("")) {

					//// System.out.println("in  Application16q14");
					CallableStatement ssiRefNoForCgpan = connection
							.prepareCall("{?=call funcGetSSIRefNoforCGPAN(?,?,?)}");
					ssiRefNoForCgpan.registerOutParameter(1, Types.INTEGER);
					ssiRefNoForCgpan.registerOutParameter(2, Types.INTEGER); // Get
																				// SSI
																				// RefNo
					ssiRefNoForCgpan.registerOutParameter(4, Types.VARCHAR);

					ssiRefNoForCgpan.setString(3, apps.getCgpan());

					ssiRefNoForCgpan.executeQuery();
					int ssiRefNoForCgpanValue = ssiRefNoForCgpan.getInt(1);

					if (ssiRefNoForCgpanValue == Constants.FUNCTION_FAILURE) {

						String error = ssiRefNoForCgpan.getString(4);
						ssiRefNoForCgpan.close();
						ssiRefNoForCgpan = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO", "submitApp", error);
						throw new DatabaseException(error);

					} else {
						((apps.getBorrowerDetails()).getSsiDetails())
								.setBorrowerRefNo(ssiRefNoForCgpan.getInt(2));

						ssiRefNoForCgpan.close();
						ssiRefNoForCgpan = null;

					}
				}

				/*
				 * retrieving the sub scheme for the borrower reference no and
				 * checking the borrower exposure
				 */
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Entering the loop for gettin the Sub Scheme");

				int approvedAmt;
				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				SubSchemeValues subSchemeValues = new SubSchemeValues();

				BorrowerDetails borrowerDetails = viewBorrowerDetails(((apps
						.getBorrowerDetails()).getSsiDetails())
						.getBorrowerRefNo());
				ssiDetail = borrowerDetails.getSsiDetails();
				Application tempApplication = new Application();
				tempApplication.setBorrowerDetails(borrowerDetails);
				tempApplication.setMliID(apps.getMliID());
				double balanceAppAmt = getBalanceApprovedAmt(tempApplication);
				//// System.out.println("balanceAppAmt:" + balanceAppAmt);
				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"tc crdit amount :"
									+ apps.getTermLoan().getCreditGuaranteed());
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("WC")) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"wc enhancecrdit amount :"
									+ apps.getWc().getEnhancedFundBased()
									+ apps.getWc().getEnhancedNonFundBased());
					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"wc crdit amount :"
									+ apps.getWc().getCreditFundBased()
									+ apps.getWc().getCreditNonFundBased());
					if (apps.getWc().getEnhancedFundBased() != 0
							|| apps.getWc().getEnhancedNonFundBased() != 0) {
						Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
								"entering enhanced "
										+ apps.getWc().getEnhancementDate());
						if (apps.getWc().getEnhancedFundBased()
								+ apps.getWc().getEnhancedNonFundBased() > balanceAppAmt) {
							throw new DatabaseException(
									"Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
											+ balanceAppAmt);

						}
					} else if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("BO")) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"both crdit amount :"
									+ apps.getTermLoan().getCreditGuaranteed()
									+ apps.getWc().getCreditFundBased()
									+ apps.getWc().getCreditNonFundBased());
					if (apps.getTermLoan().getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				}
				// apps.setBorrowerDetails(borrowerDetails);
				/*
				 * String
				 * subSchemeName=rpProcessor.getSubScheme(tempApplication);
				 * 
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","state" +
				 * apps.getBorrowerDetails().getSsiDetails().getState());
				 * Log.log
				 * (Log.INFO,"ApplicationDAO","submitApp","industry nature" +
				 * apps
				 * .getBorrowerDetails().getSsiDetails().getIndustryNature());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","Gender" +
				 * apps.getBorrowerDetails().getSsiDetails().getCpGender());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","mli ID" +
				 * apps.getMliID());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","Social Cat" +
				 * apps
				 * .getBorrowerDetails().getSsiDetails().getSocialCategory());
				 * 
				 * 
				 * if(!subSchemeName.equals("GLOBAL")) {
				 * subSchemeValues=rpProcessor
				 * .getSubSchemeValues(subSchemeName);
				 * 
				 * if(subSchemeValues!=null) {
				 * 
				 * double exposureAmount =
				 * subSchemeValues.getMaxBorrowerExposureAmount();
				 * 
				 * CallableStatement approvedAmount=connection.prepareCall(
				 * "{?=call funcGetApprovedAmt(?,?,?)}");
				 * 
				 * approvedAmount.registerOutParameter(1,Types.INTEGER);
				 * approvedAmount
				 * .setDouble(2,((apps.getBorrowerDetails()).getSsiDetails
				 * ()).getBorrowerRefNo());
				 * approvedAmount.registerOutParameter(3,Types.DOUBLE);
				 * approvedAmount.registerOutParameter(4,Types.VARCHAR);
				 * 
				 * approvedAmount.executeQuery();
				 * 
				 * int approvedAmountValue=approvedAmount.getInt(1);
				 * Log.log(Log.DEBUG,"ApplicationDAO","submitApp",
				 * "SSi Details Approved Amount result :" +
				 * approvedAmountValue);
				 * 
				 * if(approvedAmountValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = approvedAmount.getString(4);
				 * 
				 * approvedAmount.close(); approvedAmount=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.ERROR,"ApplicationDAO","submitApp",
				 * "SSI Detail Approved Amount Exception :" + error);
				 * 
				 * throw new DatabaseException(error); } else {
				 * approvedAmt=approvedAmount.getInt(3);
				 * 
				 * approvedAmount.close(); approvedAmount=null; }
				 * Log.log(Log.DEBUG
				 * ,"ApplicationDAO","submitApp","exposureAmount :" +
				 * exposureAmount);
				 * 
				 * if(approvedAmt >= exposureAmount) { throw new
				 * DatabaseException(
				 * "Borrower has crossed his exposure limit.Hence ineligible to submit a new application"
				 * ); }
				 * 
				 * 
				 * }
				 * 
				 * }
				 */
			} else { // if not covered,the generate SSI Ref no and insert the
						// details

				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Entering the loop for SSI Detail Insertion");

				double balanceAppAmt = getBalanceApprovedAmt(apps);

				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("WC")) {
					if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("BO")) {
					if (apps.getTermLoan().getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				}

				//// System.out.println("in  Application16q15");

				CallableStatement ssiDetails = connection
						.prepareCall("{?=call funcInsertSSIDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				ssiDetails.registerOutParameter(1, Types.INTEGER);
				ssiDetails.registerOutParameter(2, Types.INTEGER); // Generate
																	// SSI RefNo
				ssiDetails.registerOutParameter(32, Types.VARCHAR);

				ssiDetails.setString(3, coveredValue);
				//// System.out.println("3-Line number 329 ApplicationDAO:coveredValue"+coveredValue);

				ssiDetails.setString(4, apps.getBorrowerDetails()
						.getAssistedByBank());
				//// System.out.println("4-Line number 332 ApplicationDAO:coveredValue"+coveredValue);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"apps.getBorrowerDetails().getAssistedByBank()"
								+ apps.getBorrowerDetails().getAssistedByBank());

				ssiDetails.setNull(5, java.sql.Types.VARCHAR);

				String npaValue = (apps.getBorrowerDetails()).getNpa();
				ssiDetails.setString(6, npaValue);
				//// System.out.println("6-Line number 340 ApplicationDAO:npaValue"+npaValue);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp", "NPA :"
						+ npaValue);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getConstitution()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getConstitution()).equals(""))) {
					ssiDetails.setString(7, ((apps.getBorrowerDetails())
							.getSsiDetails()).getConstitution());
					//// System.out.println("7-Line number 340 ApplicationDAO:apps.getBorrowerDetails()).getSsiDetails()).getConstitution()"+apps.getBorrowerDetails().getSsiDetails().getConstitution());
				} else {
					ssiDetails.setString(7, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"Const :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getConstitution());

				ssiDetails.setString(8, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSsiType());
				//// System.out.println("8"+(apps.getBorrowerDetails().getSsiDetails().getSsiType()));
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"ssi Type :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiType());

				ssiDetails.setString(9, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSsiName().toUpperCase());
				//// System.out.println("9"+apps.getBorrowerDetails().getSsiDetails().getSsiName().toUpperCase());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"ssi name :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiName());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getRegNo()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getRegNo()).equals(""))) {
					ssiDetails.setString(10, ((apps.getBorrowerDetails())
							.getSsiDetails()).getRegNo());
					//// System.out.println("10"+apps.getBorrowerDetails().getSsiDetails().getRegNo());
				} else {

					ssiDetails.setString(10, null);
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"reg no :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getRegNo());

				ssiDetails.setNull(11, java.sql.Types.DATE);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSsiITPan()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSsiITPan()).equals(""))) {
					ssiDetails.setString(12, ((apps.getBorrowerDetails())
							.getSsiDetails()).getSsiITPan());
					//// System.out.println("12"+apps.getBorrowerDetails().getSsiDetails().getSsiITPan());

				} else {
					ssiDetails.setString(12, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"ssi ITPAN :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiITPan());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getActivityType()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getActivityType()).equals(""))) {
					ssiDetails.setString(13, ((apps.getBorrowerDetails())
							.getSsiDetails()).getActivityType());
					//// System.out.println("13"+((apps.getBorrowerDetails()).getSsiDetails()).getActivityType());
				} else {
					ssiDetails.setString(13, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"activity Type :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getActivityType());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getEmployeeNos()) == 0) {
					ssiDetails.setNull(14, java.sql.Types.INTEGER);
				} else {
					ssiDetails.setInt(14, ((apps.getBorrowerDetails())
							.getSsiDetails()).getEmployeeNos());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"employee nos :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getEmployeeNos());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getProjectedSalesTurnover()) == 0) {
					ssiDetails.setNull(15, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(15, ((apps.getBorrowerDetails())
							.getSsiDetails()).getProjectedSalesTurnover());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"sales :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getProjectedSalesTurnover());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getProjectedExports()) == 0) {
					ssiDetails.setNull(16, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(16, ((apps.getBorrowerDetails())
							.getSsiDetails()).getProjectedExports());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"exports :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getProjectedExports());

				ssiDetails.setString(17, ((apps.getBorrowerDetails())
						.getSsiDetails()).getAddress().toUpperCase());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"Address :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getAddress());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getCity()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCity()).equals(""))) {
					ssiDetails.setString(18, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCity().toUpperCase());

				} else {
					ssiDetails.setString(18, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"city :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCity());

				ssiDetails.setString(19, ((apps.getBorrowerDetails())
						.getSsiDetails()).getPincode());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"pincode :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getPincode());

				ssiDetails.setString(20, "Y"); // display defaulter

				if (((apps.getBorrowerDetails()).getSsiDetails()).getDistrict() != null
						&& !(((apps.getBorrowerDetails()).getSsiDetails())
								.getDistrict().equals(""))) {
					ssiDetails.setString(21, ((apps.getBorrowerDetails())
							.getSsiDetails()).getDistrict());
				} else {

					ssiDetails.setString(21, null);
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"district :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getDistrict());

				ssiDetails.setString(22, ((apps.getBorrowerDetails())
						.getSsiDetails()).getState());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"State :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getState());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getIndustryNature()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getIndustryNature()).equals(""))) {
					ssiDetails.setString(23, ((apps.getBorrowerDetails())
							.getSsiDetails()).getIndustryNature());

				} else {
					ssiDetails.setString(23, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"nature :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getIndustryNature());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getIndustrySector()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getIndustrySector()).equals(""))) {
					ssiDetails.setString(24, ((apps.getBorrowerDetails())
							.getSsiDetails()).getIndustrySector());

				} else {
					ssiDetails.setString(24, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"sector : "
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getIndustrySector());

				if (((apps.getBorrowerDetails()).getOsAmt()) == 0) {
					ssiDetails.setNull(25, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(25,
							(apps.getBorrowerDetails()).getOsAmt());
				}
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp", "os amt :"
						+ (apps.getBorrowerDetails()).getOsAmt());

				MCGFDetails mcgfDetails = apps.getMCGFDetails();
				if (mcgfDetails != null) {
					ssiDetails.setString(26, "Y");
				} else {

					ssiDetails.setString(26, "N");
				}

				ssiDetails.setString(27, createdBy);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp", "userId :"
						+ createdBy);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getEnterprise()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getEnterprise()).equals(""))) {
					ssiDetails.setString(28, ((apps.getBorrowerDetails())
							.getSsiDetails()).getEnterprise());

				} else {
					ssiDetails.setString(28, "N");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getUnitAssisted()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getUnitAssisted()).equals(""))) {
					ssiDetails.setString(29, ((apps.getBorrowerDetails())
							.getSsiDetails()).getUnitAssisted());

				} else {
					ssiDetails.setString(29, "N");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getConditionAccepted()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getConditionAccepted()).equals(""))) {
					ssiDetails.setString(30, ((apps.getBorrowerDetails())
							.getSsiDetails()).getConditionAccepted());

				} else {
					ssiDetails.setString(30, "Y");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getWomenOperated()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getWomenOperated()).equals(""))) {
					ssiDetails.setString(31, ((apps.getBorrowerDetails())
							.getSsiDetails()).getWomenOperated());

				} else {
					ssiDetails.setString(31, "N");
				}
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"SSI Details object :" + ssiDetails);

				ssiDetails.executeQuery();

				int ssiDetailsValue = ssiDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"SSi Details result :" + ssiDetailsValue);

				if (ssiDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = ssiDetails.getString(32);

					ssiDetails.close();
					ssiDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
							"SSI Detail Exception :" + error);

					throw new DatabaseException(error);
				} else {
					int ssiRefNo = ssiDetails.getInt(2);
					((apps.getBorrowerDetails()).getSsiDetails())
							.setBorrowerRefNo(ssiRefNo);

					ssiDetails.close();
					ssiDetails = null;
				}

				ssiDetail = null;
			}
		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		return ssiDetail;
	}

	/** ADDED BY SUKUMAR FOR RSF APPLICATION SUBMISSION **/

	/**
	 * 
	 * @param apps
	 * @param createdBy
	 * @param connection
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public SSIDetails submitRSFApp(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitRSFApp", "Entered");
		//// System.out.println("ApplicationDAO Line number 577"+"submitRSFApp"+"Entered");

		SSIDetails ssiDetail = new SSIDetails();

		String appLoanType = apps.getLoanType();
		//// System.out.println("Line number 581:"+appLoanType);

		try {

			String coveredValue = (apps.getBorrowerDetails())
					.getPreviouslyCovered();
			//// System.out.println("Reviously Covered Value:"+coveredValue);
			if (coveredValue.equals("Y")) { // if borrower covered then get SSI
											// ref No,update SSi Details

				String cgbid = ((apps.getBorrowerDetails()).getSsiDetails())
						.getCgbid();
				String cgpan = apps.getCgpan();
				if (cgbid != null && !cgbid.equals("")) {
					CallableStatement ssiRefNoForCgbid = connection
							.prepareCall("{?=call funcGetSSIRefNoforBID(?,?,?)}");
					ssiRefNoForCgbid.registerOutParameter(1, Types.INTEGER);
					ssiRefNoForCgbid.registerOutParameter(2, Types.INTEGER); // Get
																				// SSI
																				// RefNo
					ssiRefNoForCgbid.registerOutParameter(4, Types.VARCHAR);

					ssiRefNoForCgbid.setString(3, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCgbid());

					ssiRefNoForCgbid.executeQuery();
					int ssiRefNoForCgbidValue = ssiRefNoForCgbid.getInt(1);

					if (ssiRefNoForCgbidValue == Constants.FUNCTION_FAILURE) {

						String error = ssiRefNoForCgbid.getString(4);

						ssiRefNoForCgbid.close();
						ssiRefNoForCgbid = null;

						Log.log(Log.ERROR, "ApplicationDAO", "submitRSFApp",
								error);

						connection.rollback();

						throw new DatabaseException(
								ssiRefNoForCgbid.getString(4));

					} else {
						((apps.getBorrowerDetails()).getSsiDetails())
								.setBorrowerRefNo(ssiRefNoForCgbid.getInt(2));

						ssiRefNoForCgbid.close();
						ssiRefNoForCgbid = null;

					}

				} else if (cgpan != null && !cgpan.equals("")) {
					CallableStatement ssiRefNoForCgpan = connection
							.prepareCall("{?=call funcGetSSIRefNoforCGPAN(?,?,?)}");
					ssiRefNoForCgpan.registerOutParameter(1, Types.INTEGER);
					ssiRefNoForCgpan.registerOutParameter(2, Types.INTEGER); // Get
																				// SSI
																				// RefNo
					ssiRefNoForCgpan.registerOutParameter(4, Types.VARCHAR);

					ssiRefNoForCgpan.setString(3, apps.getCgpan());

					ssiRefNoForCgpan.executeQuery();
					int ssiRefNoForCgpanValue = ssiRefNoForCgpan.getInt(1);

					if (ssiRefNoForCgpanValue == Constants.FUNCTION_FAILURE) {

						String error = ssiRefNoForCgpan.getString(4);
						ssiRefNoForCgpan.close();
						ssiRefNoForCgpan = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO", "submitRSFApp",
								error);
						throw new DatabaseException(error);

					} else {
						((apps.getBorrowerDetails()).getSsiDetails())
								.setBorrowerRefNo(ssiRefNoForCgpan.getInt(2));

						ssiRefNoForCgpan.close();
						ssiRefNoForCgpan = null;

					}
				}

				/*
				 * retrieving the sub scheme for the borrower reference no and
				 * checking the borrower exposure
				 */
				Log.log(Log.INFO, "ApplicationDAO", "submitRSFApp",
						"Entering the loop for gettin the Sub Scheme");

				int approvedAmt;
				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				SubSchemeValues subSchemeValues = new SubSchemeValues();

				BorrowerDetails borrowerDetails = viewBorrowerDetails(((apps
						.getBorrowerDetails()).getSsiDetails())
						.getBorrowerRefNo());
				ssiDetail = borrowerDetails.getSsiDetails();
				Application tempApplication = new Application();
				tempApplication.setBorrowerDetails(borrowerDetails);
				tempApplication.setMliID(apps.getMliID());
				double balanceAppAmt = getBalanceRsfApprovedAmt(tempApplication);
				//// System.out.println("Line number 674 Balance Amount"+balanceAppAmt);
				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					//// System.out.println("Line number 677 ApplicationDAO submitRSFApp tc crdit amount :"+apps.getTermLoan().getCreditGuaranteed());
					Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
							"tc crdit amount :"
									+ apps.getTermLoan().getCreditGuaranteed());
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("WC")) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
							"wc enhancecrdit amount :"
									+ apps.getWc().getEnhancedFundBased()
									+ apps.getWc().getEnhancedNonFundBased());
					Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
							"wc crdit amount :"
									+ apps.getWc().getCreditFundBased()
									+ apps.getWc().getCreditNonFundBased());
					if (apps.getWc().getEnhancedFundBased() != 0
							|| apps.getWc().getEnhancedNonFundBased() != 0) {
						Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
								"entering enhanced "
										+ apps.getWc().getEnhancementDate());
						if (apps.getWc().getEnhancedFundBased()
								+ apps.getWc().getEnhancedNonFundBased() > balanceAppAmt) {
							throw new DatabaseException(
									"Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
											+ balanceAppAmt);

						}
					} else if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("BO")) {
					//// System.out.println("Aplication DAO Line number 705 Application Loan Type"+apps.getLoanType());
					//// System.out.println("balanceAppAmt"+balanceAppAmt);
					Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
							"both crdit amount :"
									+ apps.getTermLoan().getCreditGuaranteed()
									+ apps.getWc().getCreditFundBased()
									+ apps.getWc().getCreditNonFundBased());
					if (apps.getTermLoan().getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				}

				/*
				 * String
				 * subSchemeName=rpProcessor.getSubScheme(tempApplication);
				 * 
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","state" +
				 * apps.getBorrowerDetails().getSsiDetails().getState());
				 * Log.log
				 * (Log.INFO,"ApplicationDAO","submitApp","industry nature" +
				 * apps
				 * .getBorrowerDetails().getSsiDetails().getIndustryNature());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","Gender" +
				 * apps.getBorrowerDetails().getSsiDetails().getCpGender());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","mli ID" +
				 * apps.getMliID());
				 * Log.log(Log.INFO,"ApplicationDAO","submitApp","Social Cat" +
				 * apps
				 * .getBorrowerDetails().getSsiDetails().getSocialCategory());
				 * 
				 * 
				 * if(!subSchemeName.equals("GLOBAL")) {
				 * subSchemeValues=rpProcessor
				 * .getSubSchemeValues(subSchemeName);
				 * 
				 * if(subSchemeValues!=null) {
				 * 
				 * double exposureAmount =
				 * subSchemeValues.getMaxBorrowerExposureAmount();
				 * 
				 * CallableStatement approvedAmount=connection.prepareCall(
				 * "{?=call funcGetApprovedAmt(?,?,?)}");
				 * 
				 * approvedAmount.registerOutParameter(1,Types.INTEGER);
				 * approvedAmount
				 * .setDouble(2,((apps.getBorrowerDetails()).getSsiDetails
				 * ()).getBorrowerRefNo());
				 * approvedAmount.registerOutParameter(3,Types.DOUBLE);
				 * approvedAmount.registerOutParameter(4,Types.VARCHAR);
				 * 
				 * approvedAmount.executeQuery();
				 * 
				 * int approvedAmountValue=approvedAmount.getInt(1);
				 * Log.log(Log.DEBUG,"ApplicationDAO","submitApp",
				 * "SSi Details Approved Amount result :" +
				 * approvedAmountValue);
				 * 
				 * if(approvedAmountValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = approvedAmount.getString(4);
				 * 
				 * approvedAmount.close(); approvedAmount=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.ERROR,"ApplicationDAO","submitApp",
				 * "SSI Detail Approved Amount Exception :" + error);
				 * 
				 * throw new DatabaseException(error); } else {
				 * approvedAmt=approvedAmount.getInt(3);
				 * 
				 * approvedAmount.close(); approvedAmount=null; }
				 * Log.log(Log.DEBUG
				 * ,"ApplicationDAO","submitApp","exposureAmount :" +
				 * exposureAmount);
				 * 
				 * if(approvedAmt >= exposureAmount) { throw new
				 * DatabaseException(
				 * "Borrower has crossed his exposure limit.Hence ineligible to submit a new application"
				 * ); }
				 * 
				 * 
				 * }
				 * 
				 * }
				 */
			} else { // if not covered,the generate SSI Ref no and insert the
						// details

				Log.log(Log.INFO, "ApplicationDAO", "submitRSFApp",
						"Entering the loop for SSI Detail Insertion");

				double balanceAppAmt = getBalanceRsfApprovedAmt(apps);
				//// System.out.println("balanceAppAmt:"+balanceAppAmt);
				//// System.out.println("apps.getTermLoan().getCreditGuaranteed()"+apps.getTermLoan().getCreditGuaranteed());
				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("WC")) {
					if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				} else if (apps.getLoanType().equals("BO")) {
					if (apps.getTermLoan().getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt) {
						throw new DatabaseException(
								"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
										+ balanceAppAmt);
					}
				}

				CallableStatement ssiDetails = connection
						.prepareCall("{?=call funcInsertSSIDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				ssiDetails.registerOutParameter(1, Types.INTEGER);
				ssiDetails.registerOutParameter(2, Types.INTEGER); // Generate
																	// SSI RefNo
				ssiDetails.registerOutParameter(32, Types.VARCHAR);

				ssiDetails.setString(3, coveredValue);
				//// System.out.println("3-Line number 329 ApplicationDAO:coveredValue"+coveredValue);

				ssiDetails.setString(4, apps.getBorrowerDetails()
						.getAssistedByBank());
				//// System.out.println("4-Line number 332 ApplicationDAO:coveredValue"+coveredValue);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
						"apps.getBorrowerDetails().getAssistedByBank()"
								+ apps.getBorrowerDetails().getAssistedByBank());

				ssiDetails.setNull(5, java.sql.Types.VARCHAR);

				String npaValue = (apps.getBorrowerDetails()).getNpa();
				ssiDetails.setString(6, npaValue);
				//// System.out.println("6-Line number 340 ApplicationDAO:npaValue"+npaValue);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp", "NPA :"
						+ npaValue);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getConstitution()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getConstitution()).equals(""))) {
					ssiDetails.setString(7, ((apps.getBorrowerDetails())
							.getSsiDetails()).getConstitution());
					//// System.out.println("7-Line number 340 ApplicationDAO:apps.getBorrowerDetails()).getSsiDetails()).getConstitution()"+apps.getBorrowerDetails().getSsiDetails().getConstitution());
				} else {
					ssiDetails.setString(7, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"Const :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getConstitution());

				ssiDetails.setString(8, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSsiType());
				//// System.out.println("8"+(apps.getBorrowerDetails().getSsiDetails().getSsiType()));
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"ssi Type :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiType());

				ssiDetails.setString(9, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSsiName().toUpperCase());
				//// System.out.println("9"+apps.getBorrowerDetails().getSsiDetails().getSsiName().toUpperCase());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"ssi name :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiName());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getRegNo()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getRegNo()).equals(""))) {
					ssiDetails.setString(10, ((apps.getBorrowerDetails())
							.getSsiDetails()).getRegNo());
					//// System.out.println("10"+apps.getBorrowerDetails().getSsiDetails().getRegNo());
				} else {

					ssiDetails.setString(10, null);
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"reg no :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getRegNo());

				ssiDetails.setNull(11, java.sql.Types.DATE);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSsiITPan()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSsiITPan()).equals(""))) {
					ssiDetails.setString(12, ((apps.getBorrowerDetails())
							.getSsiDetails()).getSsiITPan());
					//// System.out.println("12"+apps.getBorrowerDetails().getSsiDetails().getSsiITPan());

				} else {
					ssiDetails.setString(12, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"ssi ITPAN :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getSsiITPan());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getActivityType()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getActivityType()).equals(""))) {
					ssiDetails.setString(13, ((apps.getBorrowerDetails())
							.getSsiDetails()).getActivityType());
					//// System.out.println("13"+((apps.getBorrowerDetails()).getSsiDetails()).getActivityType());
				} else {
					ssiDetails.setString(13, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"activity Type :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getActivityType());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getEmployeeNos()) == 0) {
					ssiDetails.setNull(14, java.sql.Types.INTEGER);
				} else {
					ssiDetails.setInt(14, ((apps.getBorrowerDetails())
							.getSsiDetails()).getEmployeeNos());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"employee nos :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getEmployeeNos());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getProjectedSalesTurnover()) == 0) {
					ssiDetails.setNull(15, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(15, ((apps.getBorrowerDetails())
							.getSsiDetails()).getProjectedSalesTurnover());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"sales :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getProjectedSalesTurnover());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getProjectedExports()) == 0) {
					ssiDetails.setNull(16, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(16, ((apps.getBorrowerDetails())
							.getSsiDetails()).getProjectedExports());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"exports :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getProjectedExports());

				ssiDetails.setString(17, ((apps.getBorrowerDetails())
						.getSsiDetails()).getAddress().toUpperCase());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"Address :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getAddress());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getCity()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCity()).equals(""))) {
					ssiDetails.setString(18, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCity().toUpperCase());

				} else {
					ssiDetails.setString(18, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"city :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCity());

				ssiDetails.setString(19, ((apps.getBorrowerDetails())
						.getSsiDetails()).getPincode());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"pincode :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getPincode());

				ssiDetails.setString(20, "Y"); // display defaulter

				if (((apps.getBorrowerDetails()).getSsiDetails()).getDistrict() != null
						&& !(((apps.getBorrowerDetails()).getSsiDetails())
								.getDistrict().equals(""))) {
					ssiDetails.setString(21, ((apps.getBorrowerDetails())
							.getSsiDetails()).getDistrict());
				} else {

					ssiDetails.setString(21, null);
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"district :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getDistrict());

				ssiDetails.setString(22, ((apps.getBorrowerDetails())
						.getSsiDetails()).getState());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"State :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getState());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getIndustryNature()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getIndustryNature()).equals(""))) {
					ssiDetails.setString(23, ((apps.getBorrowerDetails())
							.getSsiDetails()).getIndustryNature());

				} else {
					ssiDetails.setString(23, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"nature :"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getIndustryNature());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getIndustrySector()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getIndustrySector()).equals(""))) {
					ssiDetails.setString(24, ((apps.getBorrowerDetails())
							.getSsiDetails()).getIndustrySector());

				} else {
					ssiDetails.setString(24, null);
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitRSFApp",
						"sector : "
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getIndustrySector());

				if (((apps.getBorrowerDetails()).getOsAmt()) == 0) {
					ssiDetails.setNull(25, java.sql.Types.DOUBLE);
				} else {
					ssiDetails.setDouble(25,
							(apps.getBorrowerDetails()).getOsAmt());
				}
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp", "os amt :"
						+ (apps.getBorrowerDetails()).getOsAmt());

				MCGFDetails mcgfDetails = apps.getMCGFDetails();
				if (mcgfDetails != null) {
					ssiDetails.setString(26, "Y");
				} else {

					ssiDetails.setString(26, "N");
				}

				ssiDetails.setString(27, createdBy);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp", "userId :"
						+ createdBy);

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getEnterprise()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getEnterprise()).equals(""))) {
					ssiDetails.setString(28, ((apps.getBorrowerDetails())
							.getSsiDetails()).getEnterprise());

				} else {
					ssiDetails.setString(28, "N");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getUnitAssisted()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getUnitAssisted()).equals(""))) {
					ssiDetails.setString(29, ((apps.getBorrowerDetails())
							.getSsiDetails()).getUnitAssisted());

				} else {
					ssiDetails.setString(29, "N");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getConditionAccepted()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getConditionAccepted()).equals(""))) {
					ssiDetails.setString(30, ((apps.getBorrowerDetails())
							.getSsiDetails()).getConditionAccepted());

				} else {
					ssiDetails.setString(30, "Y");
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getWomenOperated()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getWomenOperated()).equals(""))) {
					ssiDetails.setString(31, ((apps.getBorrowerDetails())
							.getSsiDetails()).getWomenOperated());

				} else {
					ssiDetails.setString(31, "N");
				}
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
						"SSI Details object :" + ssiDetails);

				ssiDetails.executeQuery();

				int ssiDetailsValue = ssiDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitRSFApp",
						"SSi Details result :" + ssiDetailsValue);

				if (ssiDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = ssiDetails.getString(32);

					ssiDetails.close();
					ssiDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitRSFApp",
							"SSI Detail Exception :" + error);

					throw new DatabaseException(error);
				} else {
					int ssiRefNo = ssiDetails.getInt(2);
					((apps.getBorrowerDetails()).getSsiDetails())
							.setBorrowerRefNo(ssiRefNo);

					ssiDetails.close();
					ssiDetails = null;
				}

				ssiDetail = null;
			}
		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitRSFApp",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "submitRSFApp",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		return ssiDetail;
	}

	public SSIDetails submitRSF2App(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(4, "ApplicationDAO", "submitRSFApp", "Entered");
		//// System.out.println("ApplicationDAO Line number 577submitRSFAppEntered");
		SSIDetails ssiDetail = new SSIDetails();
		String appLoanType = apps.getLoanType();
		//// System.out.println((new
		// StringBuilder()).append("Line number applicationdao 1061 in ApplicationDAO").append(appLoanType).toString());
		try {
			String coveredValue = apps.getBorrowerDetails()
					.getPreviouslyCovered();
			if (coveredValue.equals("Y")) {
				String cgbid = apps.getBorrowerDetails().getSsiDetails()
						.getCgbid();
				String cgpan = apps.getCgpan();
				if (cgbid != null && !cgbid.equals("")) {
					CallableStatement ssiRefNoForCgbid = connection
							.prepareCall("{?=call funcGetSSIRefNoforBID(?,?,?)}");
					ssiRefNoForCgbid.registerOutParameter(1, 4);
					ssiRefNoForCgbid.registerOutParameter(2, 4);
					ssiRefNoForCgbid.registerOutParameter(4, 12);
					ssiRefNoForCgbid.setString(3, apps.getBorrowerDetails()
							.getSsiDetails().getCgbid());
					ssiRefNoForCgbid.executeQuery();
					int ssiRefNoForCgbidValue = ssiRefNoForCgbid.getInt(1);
					if (ssiRefNoForCgbidValue == 1) {
						String error = ssiRefNoForCgbid.getString(4);
						ssiRefNoForCgbid.close();
						ssiRefNoForCgbid = null;
						Log.log(2, "ApplicationDAO", "submitRSFApp", error);
						connection.rollback();
						throw new DatabaseException(
								ssiRefNoForCgbid.getString(4));
					}
					apps.getBorrowerDetails().getSsiDetails()
							.setBorrowerRefNo(ssiRefNoForCgbid.getInt(2));
					ssiRefNoForCgbid.close();
					ssiRefNoForCgbid = null;
				} else if (cgpan != null && !cgpan.equals("")) {
					CallableStatement ssiRefNoForCgpan = connection
							.prepareCall("{?=call funcGetSSIRefNoforCGPAN(?,?,?)}");
					ssiRefNoForCgpan.registerOutParameter(1, 4);
					ssiRefNoForCgpan.registerOutParameter(2, 4);
					ssiRefNoForCgpan.registerOutParameter(4, 12);
					ssiRefNoForCgpan.setString(3, apps.getCgpan());
					ssiRefNoForCgpan.executeQuery();
					int ssiRefNoForCgpanValue = ssiRefNoForCgpan.getInt(1);
					if (ssiRefNoForCgpanValue == 1) {
						String error = ssiRefNoForCgpan.getString(4);
						ssiRefNoForCgpan.close();
						ssiRefNoForCgpan = null;
						connection.rollback();
						Log.log(2, "ApplicationDAO", "submitRSFApp", error);
						throw new DatabaseException(error);
					}
					apps.getBorrowerDetails().getSsiDetails()
							.setBorrowerRefNo(ssiRefNoForCgpan.getInt(2));
					ssiRefNoForCgpan.close();
					ssiRefNoForCgpan = null;
				}
				Log.log(4, "ApplicationDAO", "submitRSFApp",
						"Entering the loop for gettin the Sub Scheme");
				RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
				SubSchemeValues subSchemeValues = new SubSchemeValues();
				BorrowerDetails borrowerDetails = viewBorrowerDetails(apps
						.getBorrowerDetails().getSsiDetails()
						.getBorrowerRefNo());
				ssiDetail = borrowerDetails.getSsiDetails();
				Application tempApplication = new Application();
				tempApplication.setBorrowerDetails(borrowerDetails);
				tempApplication.setMliID(apps.getMliID());
				double balanceAppAmt = getBalanceRsfApprovedAmt(tempApplication);
				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					Log.log(5,
							"ApplicationDAO",
							"submitRSFApp",
							(new StringBuilder())
									.append("tc crdit amount :")
									.append(apps.getTermLoan()
											.getCreditGuaranteed()).toString());
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt)
						throw new DatabaseException(
								(new StringBuilder())
										.append("Credit to be Guaranteed44 Amount should be within the eligible amount available for Guarantee :")
										.append(balanceAppAmt).toString());
				} else if (apps.getLoanType().equals("WC")) {
					Log.log(5,
							"ApplicationDAO",
							"submitRSFApp",
							(new StringBuilder())
									.append("wc enhancecrdit amount :")
									.append(apps.getWc().getEnhancedFundBased())
									.append(apps.getWc()
											.getEnhancedNonFundBased())
									.toString());
					Log.log(5,
							"ApplicationDAO",
							"submitRSFApp",
							(new StringBuilder())
									.append("wc crdit amount :")
									.append(apps.getWc().getCreditFundBased())
									.append(apps.getWc()
											.getCreditNonFundBased())
									.toString());
					if (apps.getWc().getEnhancedFundBased() != 0.0D
							|| apps.getWc().getEnhancedNonFundBased() != 0.0D) {
						Log.log(5,
								"ApplicationDAO",
								"submitRSFApp",
								(new StringBuilder())
										.append("entering enhanced ")
										.append(apps.getWc()
												.getEnhancementDate())
										.toString());
						if (apps.getWc().getEnhancedFundBased()
								+ apps.getWc().getEnhancedNonFundBased() > balanceAppAmt)
							throw new DatabaseException(
									(new StringBuilder())
											.append("Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :")
											.append(balanceAppAmt).toString());
					} else if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt)
						throw new DatabaseException(
								(new StringBuilder())
										.append("Credit to be Guaranteed 90 Amount should be within the eligible amount available for Guarantee :")
										.append(balanceAppAmt).toString());
				} else if (apps.getLoanType().equals("BO")) {
					Log.log(5,
							"ApplicationDAO",
							"submitRSFApp",
							(new StringBuilder())
									.append("both crdit amount :")
									.append(apps.getTermLoan()
											.getCreditGuaranteed())
									.append(apps.getWc().getCreditFundBased())
									.append(apps.getWc()
											.getCreditNonFundBased())
									.toString());
					if (apps.getTermLoan().getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt)
						throw new DatabaseException(
								(new StringBuilder())
										.append("Credit to be Guaranteed777 Amount should be within the eligible amount available for Guarantee :")
										.append(balanceAppAmt).toString());
				}
			} else {
				Log.log(4, "ApplicationDAO", "submitRSFApp",
						"Entering the loop for SSI Detail Insertion");
				double balanceAppAmt = getBalanceRsf2ApprovedAmt(apps);
				if (apps.getLoanType().equals("TC")
						|| apps.getLoanType().equals("CC")) {
					if (apps.getTermLoan().getCreditGuaranteed() > balanceAppAmt)
						throw new DatabaseException(
								(new StringBuilder())
										.append("Credit to be Guaranteed9991 Amount should be within the eligible amount available for Guarantee :")
										.append(balanceAppAmt).toString());
				} else if (apps.getLoanType().equals("WC")) {
					if (apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased() > balanceAppAmt)
						throw new DatabaseException(
								(new StringBuilder())
										.append("Credit to be Guaranteed5656 Amount should be within the eligible amount available for Guarantee :")
										.append(balanceAppAmt).toString());
				} else if (apps.getLoanType().equals("BO")
						&& apps.getTermLoan().getCreditGuaranteed()
								+ apps.getWc().getCreditFundBased()
								+ apps.getWc().getCreditNonFundBased() > balanceAppAmt)
					throw new DatabaseException(
							(new StringBuilder())
									.append("Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :")
									.append(balanceAppAmt).toString());
				CallableStatement ssiDetails = connection
						.prepareCall("{?=call funcInsertSSIDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				ssiDetails.registerOutParameter(1, 4);
				ssiDetails.registerOutParameter(2, 4);
				ssiDetails.registerOutParameter(32, 12);
				ssiDetails.setString(3, coveredValue);
				ssiDetails.setString(4, apps.getBorrowerDetails()
						.getAssistedByBank());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("apps.getBorrowerDetails().getAssistedByBank()")
								.append(apps.getBorrowerDetails()
										.getAssistedByBank()).toString());
				ssiDetails.setNull(5, 12);
				String npaValue = apps.getBorrowerDetails().getNpa();
				ssiDetails.setString(6, npaValue);
				Log.log(5, "ApplicationDAO", "submitRSFApp",
						(new StringBuilder()).append("NPA :").append(npaValue)
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails().getConstitution() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getConstitution().equals(""))
					ssiDetails.setString(7, apps.getBorrowerDetails()
							.getSsiDetails().getConstitution());
				else
					ssiDetails.setString(7, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("Const :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getConstitution())
								.toString());
				ssiDetails.setString(8, apps.getBorrowerDetails()
						.getSsiDetails().getSsiType());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("ssi Type :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getSsiType())
								.toString());
				ssiDetails.setString(9, apps.getBorrowerDetails()
						.getSsiDetails().getSsiName().toUpperCase());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("ssi name :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getSsiName())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails().getRegNo() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getRegNo().equals(""))
					ssiDetails.setString(10, apps.getBorrowerDetails()
							.getSsiDetails().getRegNo());
				else
					ssiDetails.setString(10, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("reg no :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getRegNo()).toString());
				ssiDetails.setNull(11, 91);
				if (apps.getBorrowerDetails().getSsiDetails().getSsiITPan() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getSsiITPan().equals(""))
					ssiDetails.setString(12, apps.getBorrowerDetails()
							.getSsiDetails().getSsiITPan());
				else
					ssiDetails.setString(12, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("ssi ITPAN :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getSsiITPan())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails().getActivityType() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getActivityType().equals(""))
					ssiDetails.setString(13, apps.getBorrowerDetails()
							.getSsiDetails().getActivityType());
				else
					ssiDetails.setString(13, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("activity Type :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getActivityType())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails().getEmployeeNos() == 0)
					ssiDetails.setNull(14, 4);
				else
					ssiDetails.setInt(14, apps.getBorrowerDetails()
							.getSsiDetails().getEmployeeNos());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("employee nos :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getEmployeeNos())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails()
						.getProjectedSalesTurnover() == 0.0D)
					ssiDetails.setNull(15, 8);
				else
					ssiDetails.setDouble(15, apps.getBorrowerDetails()
							.getSsiDetails().getProjectedSalesTurnover());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("sales :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails()
										.getProjectedSalesTurnover())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails()
						.getProjectedExports() == 0.0D)
					ssiDetails.setNull(16, 8);
				else
					ssiDetails.setDouble(16, apps.getBorrowerDetails()
							.getSsiDetails().getProjectedExports());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("exports :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getProjectedExports())
								.toString());
				ssiDetails.setString(17, apps.getBorrowerDetails()
						.getSsiDetails().getAddress().toUpperCase());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("Address :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getAddress())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails().getCity() != null
						&& !apps.getBorrowerDetails().getSsiDetails().getCity()
								.equals(""))
					ssiDetails.setString(18, apps.getBorrowerDetails()
							.getSsiDetails().getCity().toUpperCase());
				else
					ssiDetails.setString(18, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("city :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getCity()).toString());
				ssiDetails.setString(19, apps.getBorrowerDetails()
						.getSsiDetails().getPincode());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("pincode :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getPincode())
								.toString());
				ssiDetails.setString(20, "Y");
				if (apps.getBorrowerDetails().getSsiDetails().getDistrict() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getDistrict().equals(""))
					ssiDetails.setString(21, apps.getBorrowerDetails()
							.getSsiDetails().getDistrict());
				else
					ssiDetails.setString(21, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("district :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getDistrict())
								.toString());
				ssiDetails.setString(22, apps.getBorrowerDetails()
						.getSsiDetails().getState());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("State :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getState()).toString());
				if (apps.getBorrowerDetails().getSsiDetails()
						.getIndustryNature() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getIndustryNature().equals(""))
					ssiDetails.setString(23, apps.getBorrowerDetails()
							.getSsiDetails().getIndustryNature());
				else
					ssiDetails.setString(23, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("nature :")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getIndustryNature())
								.toString());
				if (apps.getBorrowerDetails().getSsiDetails()
						.getIndustrySector() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getIndustrySector().equals(""))
					ssiDetails.setString(24, apps.getBorrowerDetails()
							.getSsiDetails().getIndustrySector());
				else
					ssiDetails.setString(24, null);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder())
								.append("sector : ")
								.append(apps.getBorrowerDetails()
										.getSsiDetails().getIndustrySector())
								.toString());
				if (apps.getBorrowerDetails().getOsAmt() == 0.0D)
					ssiDetails.setNull(25, 8);
				else
					ssiDetails.setDouble(25, apps.getBorrowerDetails()
							.getOsAmt());
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder()).append("os amt :")
								.append(apps.getBorrowerDetails().getOsAmt())
								.toString());
				MCGFDetails mcgfDetails = apps.getMCGFDetails();
				if (mcgfDetails != null)
					ssiDetails.setString(26, "Y");
				else
					ssiDetails.setString(26, "N");
				ssiDetails.setString(27, createdBy);
				Log.log(5,
						"ApplicationDAO",
						"submitRSFApp",
						(new StringBuilder()).append("userId :")
								.append(createdBy).toString());
				if (apps.getBorrowerDetails().getSsiDetails().getEnterprise() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getEnterprise().equals(""))
					ssiDetails.setString(28, apps.getBorrowerDetails()
							.getSsiDetails().getEnterprise());
				else
					ssiDetails.setString(28, "N");
				if (apps.getBorrowerDetails().getSsiDetails().getUnitAssisted() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getUnitAssisted().equals(""))
					ssiDetails.setString(29, apps.getBorrowerDetails()
							.getSsiDetails().getUnitAssisted());
				else
					ssiDetails.setString(29, "N");
				if (apps.getBorrowerDetails().getSsiDetails()
						.getConditionAccepted() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getConditionAccepted().equals(""))
					ssiDetails.setString(30, apps.getBorrowerDetails()
							.getSsiDetails().getConditionAccepted());
				else
					ssiDetails.setString(30, "Y");
				if (apps.getBorrowerDetails().getSsiDetails()
						.getWomenOperated() != null
						&& !apps.getBorrowerDetails().getSsiDetails()
								.getWomenOperated().equals(""))
					ssiDetails.setString(31, apps.getBorrowerDetails()
							.getSsiDetails().getWomenOperated());
				else
					ssiDetails.setString(31, "N");
				Log.log(5, "ApplicationDAO", "submitRSFApp",
						(new StringBuilder()).append("SSI Details object :")
								.append(ssiDetails).toString());
				ssiDetails.executeQuery();
				int ssiDetailsValue = ssiDetails.getInt(1);
				Log.log(5, "ApplicationDAO", "submitRSFApp",
						(new StringBuilder()).append("SSi Details result :")
								.append(ssiDetailsValue).toString());
				if (ssiDetailsValue == 1) {
					String error = ssiDetails.getString(32);
					ssiDetails.close();
					ssiDetails = null;
					connection.rollback();
					Log.log(2,
							"ApplicationDAO",
							"submitRSFApp",
							(new StringBuilder())
									.append("SSI Detail Exception :")
									.append(error).toString());
					throw new DatabaseException(error);
				}
				int ssiRefNo = ssiDetails.getInt(2);
				apps.getBorrowerDetails().getSsiDetails()
						.setBorrowerRefNo(ssiRefNo);
				ssiDetails.close();
				ssiDetails = null;
				ssiDetail = null;
			}
		} catch (SQLException sqlException) {
			Log.log(2, "ApplicationDAO", "submitRSFApp",
					sqlException.getMessage());
			Log.logException(sqlException);
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(2, "ApplicationDAO", "submitRSFApp",
						ignore.getMessage());
			}
			throw new DatabaseException(sqlException.getMessage());
		}
		return ssiDetail;
	}

	/** END PART HERE **/

	/**
	 * This method submits the application details to the database
	 */
	public Application submitApplication(Application apps, String createdBy)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		String appLoanType = apps.getLoanType();
		//// System.out.println("Line number 1052 in ApplicationDAO"+appLoanType);
		MCGSProcessor mcgsProcessor = new MCGSProcessor();
		RiskManagementProcessor rpProcessor = new RiskManagementProcessor();

		SSIDetails ssiDetails = new SSIDetails();
		try {

			if (appLoanType.equals("TC") || (appLoanType.equals("CC"))
					|| (appLoanType.equals("WC"))) {

				if (!(apps.getScheme().equals("MCGS"))) {
					ssiDetails = submitApp(apps, createdBy, connection);
					submitPromotersDetails(apps, connection);

					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}

				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {

						ssiDetails = submitApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);
						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, Types.INTEGER);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());
						callable.setString(6, null);

						callable.registerOutParameter(7, Types.VARCHAR);

						callable.execute();

						int errorCode = callable.getInt(1);
						String error = callable.getString(7);

						if (errorCode == Constants.FUNCTION_FAILURE) {
							callable.close();
							callable = null;

							connection.rollback();

							Log.log(Log.ERROR, "MCGSDAO", "addSSIMembers",
									error);

							throw new DatabaseException(error);
						}

						callable.close();
						callable = null;

						if (ssiDetails != null) {
							apps.getBorrowerDetails().setSsiDetails(ssiDetails);

						}

					}

				}
				int temssiRef = ((apps.getBorrowerDetails()).getSsiDetails())
						.getBorrowerRefNo();
				//// System.out.println("temssiRef:"+temssiRef);
				String ssiRefNumber = Integer.toString(temssiRef);
				RpDAO rpDAO1 = new RpDAO();
				double prevTotalSancAmt = rpDAO1
						.getTotalSanctionedAmountNew(ssiRefNumber);
				ApplicationDAO appdao = new ApplicationDAO();

				double prevTotalHandloomSancAmt = appdao
						.getTotalSanctionedHandloomAmountNew(ssiRefNumber);

				double currentCreditAmount = 0;
				if (apps.getLoanType().equals("TC")) {
					currentCreditAmount = apps.getTermLoan()
							.getCreditGuaranteed();
				} else if (apps.getLoanType().equals("CC")) {
					currentCreditAmount = apps.getTermLoan()
							.getCreditGuaranteed()
							+ apps.getWc().getCreditFundBased()
							+ +apps.getWc().getCreditNonFundBased();
				} else if (apps.getLoanType().equals("WC")) {
					currentCreditAmount = apps.getWc().getCreditFundBased()
							+ apps.getWc().getCreditNonFundBased();
				}
				if (currentCreditAmount + prevTotalSancAmt > 10000000) {
					throw new DatabaseException(
							"Guarantee of Rs. "
									+ prevTotalSancAmt
									+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto"
									+ 10000000);
				}
				if ((currentCreditAmount + prevTotalHandloomSancAmt > 200000)
						&& (apps.getDcHandlooms().equals("Y"))) {
					throw new DatabaseException(
							"Guarantee of Rs. "
									+ prevTotalHandloomSancAmt
									+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto  Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
				}

				String applicationRefNo = submitApplicationDetails(apps,
						createdBy, connection);
				apps.setAppRefNo(applicationRefNo);

				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);

				WorkingCapital tempWc = apps.getWc();

				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();

				if ((appLoanType.equals("WC"))) {

					tempWc.setWcTenure(param.getWcTenorInYrs() * 12);
				}
				apps.setWc(tempWc);

				apps.setWc(tempWc);

				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getScheme().equals("MCGS")) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(applicationRefNo);
					apps.setMCGFDetails(mcgfDetails);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

			} else if (appLoanType.equals("BO")) {

				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Entering if it is a Both Application..");

				apps.setLoanType("TC");
				if (!(apps.getScheme().equals("MCGS"))) {

					ssiDetails = submitApp(apps, createdBy, connection);
					submitPromotersDetails(apps, connection);
					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {
						ssiDetails = submitApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);

						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, Types.INTEGER);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());

						callable.setString(6, null);

						callable.registerOutParameter(7, Types.VARCHAR);

						callable.execute();

						int errorCode = callable.getInt(1);
						String error = callable.getString(7);

						if (errorCode == Constants.FUNCTION_FAILURE) {
							callable.close();
							callable = null;

							connection.rollback();

							Log.log(Log.ERROR, "MCGSDAO", "addSSIMembers",
									error);

							throw new DatabaseException(error);
						}

						callable.close();
						callable = null;

						if (ssiDetails != null) {

							apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						}

					}
				}

				String tcAppRefNo = submitApplicationDetails(apps, createdBy,
						connection);
				apps.setWcAppRefNo(tcAppRefNo);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"First Reference Number :" + apps.getWcAppRefNo());
				apps.setAppRefNo(tcAppRefNo);
				// apps.getMCGFDetails().setApplicationReferenceNumber(tcAppRefNo);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"Application Reference Number :" + apps.getAppRefNo());

				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);
				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(tcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

				apps.setLoanType("WC");
				// setting the tc app ref no for the working capital application
				apps.setCgpanReference(tcAppRefNo);
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"TC App Ref No For Both Application:"
								+ apps.getCgpanReference());

				if (!(apps.getScheme().equals("MCGS"))) {

					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);

					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

				}

				String wcAppRefNo = submitApplicationDetails(apps, createdBy,
						connection);
				apps.setAppRefNo(wcAppRefNo);

				submitGuarantorSecurityDetails(apps, connection);
				// apps.getMCGFDetails().setApplicationReferenceNumber(wcAppRefNo);
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"Both Application Reference Number :"
								+ apps.getAppRefNo());
				submitTermCreditDetails(apps, createdBy, connection);

				WorkingCapital tempWc = apps.getWc();
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Term Loan Tenure :" + apps.getTermLoan().getTenure());
				tempWc.setWcTenure(apps.getTermLoan().getTenure());
				apps.setWc(tempWc);

				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(wcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

				// setting the wc app ref no for the term loan capital
				// application
				Log.log(Log.INFO, "ApplicationDAO", "submitApp", "tcapprefno :"
						+ tcAppRefNo);
				Log.log(Log.INFO, "ApplicationDAO", "submitApp", "wcapprefno :"
						+ wcAppRefNo);

				updateAppReference(tcAppRefNo, wcAppRefNo, connection);

				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Submitted a BO application");

			}
			connection.commit();

		} catch (SQLException e) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitApp", e.getMessage());
			Log.logException(e);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						ignore.getMessage());
			}

			throw new DatabaseException("Unable to submit Application");
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "Exited");

		return apps;

	}

	/**
	 * 
	 * @param apps
	 * @param createdBy
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */

	public double getTotalSanctionedHandloomAmountNew(String ssiRef)
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
					+ " from VIEW_APPL_HANDLOOMAMOUNT_MOD "
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

	public Application submitRSF2Application(Application apps, String createdBy)
			throws DatabaseException {
		Connection connection;
		String appLoanType;
		MCGSProcessor mcgsProcessor;
		RiskManagementProcessor rpProcessor;
		SSIDetails ssiDetails;
		connection = DBConnection.getConnection(false);
		appLoanType = apps.getLoanType();
		//System.out.println((new StringBuilder())
				//.append("Line number applicationdao 1911 in ApplicationDAO")
				//.append(appLoanType).toString());
		mcgsProcessor = new MCGSProcessor();
		rpProcessor = new RiskManagementProcessor();
		ssiDetails = new SSIDetails();
		try {
			if (appLoanType.equals("TC") || appLoanType.equals("CC")
					|| appLoanType.equals("WC")) {
				if (!apps.getScheme().equals("MCGS")) {
					ssiDetails = submitRSF2App(apps, createdBy, connection);
					submitPromotersDetails(apps, connection);
					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);
					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {
						ssiDetails = submitRSFApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);
						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, 4);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());
						callable.setString(6, null);
						callable.registerOutParameter(7, 12);
						callable.execute();
						int errorCode = callable.getInt(1);
						String error = callable.getString(7);
						if (errorCode == 1) {
							callable.close();
							callable = null;
							connection.rollback();
							Log.log(2, "MCGSDAO", "addSSIMembers", error);
							throw new DatabaseException(error);
						}
						callable.close();
						callable = null;
						if (ssiDetails != null)
							apps.getBorrowerDetails().setSsiDetails(ssiDetails);
					}
				}
				String applicationRefNo = submitRsf2ApplicationDetails(apps,
						createdBy, connection);
				apps.setAppRefNo(applicationRefNo);
				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);
				WorkingCapital tempWc = apps.getWc();
				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();
				if (appLoanType.equals("WC"))
					tempWc.setWcTenure(param.getWcTenorInYrs() * 12);
				apps.setWc(tempWc);
				apps.setWc(tempWc);
				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);
				if (apps.getScheme().equals("MCGS")) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(applicationRefNo);
					apps.setMCGFDetails(mcgfDetails);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}
			} else if (appLoanType.equals("BO")) {
				Log.log(4, "ApplicationDAO", "submitApp",
						"Entering if it is a Both Application..");
				apps.setLoanType("TC");
				if (!apps.getScheme().equals("MCGS")) {
					ssiDetails = submitRSFApp(apps, createdBy, connection);
					submitPromotersDetails(apps, connection);
					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);
					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {
						ssiDetails = submitApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);
						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, 4);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());
						callable.setString(6, null);
						callable.registerOutParameter(7, 12);
						callable.execute();
						int errorCode = callable.getInt(1);
						String error = callable.getString(7);
						if (errorCode == 1) {
							callable.close();
							callable = null;
							connection.rollback();
							Log.log(2, "MCGSDAO", "addSSIMembers", error);
							throw new DatabaseException(error);
						}
						callable.close();
						callable = null;
						if (ssiDetails != null)
							apps.getBorrowerDetails().setSsiDetails(ssiDetails);
					}
				}
				String tcAppRefNo = submitRsf2ApplicationDetails(apps,
						createdBy, connection);
				apps.setWcAppRefNo(tcAppRefNo);
				//// System.out.println((new
				// StringBuilder()).append("tcAppRefNo is   ").append(tcAppRefNo).toString());
				Log.log(5,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder())
								.append("First Reference Number :")
								.append(apps.getWcAppRefNo()).toString());
				apps.setAppRefNo(tcAppRefNo);
				Log.log(5,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder())
								.append("Application Reference Number :")
								.append(apps.getAppRefNo()).toString());
				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);
				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);
				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(tcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}
				apps.setLoanType("WC");
				apps.setCgpanReference(tcAppRefNo);
				Log.log(5,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder())
								.append("TC App Ref No For Both Application:")
								.append(apps.getCgpanReference()).toString());
				if (!apps.getScheme().equals("MCGS")) {
					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);
				}
				String wcAppRefNo = submitRsfApplicationDetails(apps,
						createdBy, connection);
				apps.setAppRefNo(wcAppRefNo);
				submitGuarantorSecurityDetails(apps, connection);
				Log.log(5,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder())
								.append("Both Application Reference Number :")
								.append(apps.getAppRefNo()).toString());
				submitTermCreditDetails(apps, createdBy, connection);
				WorkingCapital tempWc = apps.getWc();
				Log.log(4, "ApplicationDAO", "submitRSF2Application",
						(new StringBuilder()).append("Term Loan Tenure :")
								.append(apps.getTermLoan().getTenure())
								.toString());
				tempWc.setWcTenure(apps.getTermLoan().getTenure());
				apps.setWc(tempWc);
				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);
				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(wcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}
				Log.log(4,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder()).append("tcapprefno :")
								.append(tcAppRefNo).toString());
				Log.log(4,
						"ApplicationDAO",
						"submitRSF2Application",
						(new StringBuilder()).append("wcapprefno :")
								.append(wcAppRefNo).toString());
				updateAppReference(tcAppRefNo, wcAppRefNo, connection);
				Log.log(4, "ApplicationDAO", "submitRSF2Application",
						"Submitted a BO application");
			}
			connection.commit();
		} catch (SQLException e) {
			Log.log(2, "ApplicationDAO", "submitRSF2Application",
					e.getMessage());
			Log.logException(e);
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(2, "ApplicationDAO", "submitRSF2Application",
						ignore.getMessage());
			}
			throw new DatabaseException("Unable to submit Application");
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(4, "ApplicationDAO", "submitRSF2Application", "Exited");
		return apps;
	}

	public Application submitRSFApplication(Application apps, String createdBy)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		String appLoanType = apps.getLoanType();
		//// System.out.println("Line number 1351 in ApplicationDAO"+appLoanType);
		MCGSProcessor mcgsProcessor = new MCGSProcessor();
		RiskManagementProcessor rpProcessor = new RiskManagementProcessor();

		SSIDetails ssiDetails = new SSIDetails();
		try {

			if (appLoanType.equals("TC") || (appLoanType.equals("CC"))
					|| (appLoanType.equals("WC"))) {

				if (!(apps.getScheme().equals("MCGS"))) {
					ssiDetails = submitRSFApp(apps, createdBy, connection);
					submitPromotersDetails(apps, connection);

					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}

				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {
						ssiDetails = submitRSFApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);
						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, Types.INTEGER);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());
						callable.setString(6, null);

						callable.registerOutParameter(7, Types.VARCHAR);

						callable.execute();

						int errorCode = callable.getInt(1);
						String error = callable.getString(7);

						if (errorCode == Constants.FUNCTION_FAILURE) {
							callable.close();
							callable = null;

							connection.rollback();

							Log.log(Log.ERROR, "MCGSDAO", "addSSIMembers",
									error);

							throw new DatabaseException(error);
						}

						callable.close();
						callable = null;

						if (ssiDetails != null) {
							apps.getBorrowerDetails().setSsiDetails(ssiDetails);

						}

					}

				}
				String applicationRefNo = submitRsfApplicationDetails(apps,
						createdBy, connection);
				apps.setAppRefNo(applicationRefNo);

				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);

				WorkingCapital tempWc = apps.getWc();

				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();

				if ((appLoanType.equals("WC"))) {

					tempWc.setWcTenure(param.getWcTenorInYrs() * 12);
				}
				apps.setWc(tempWc);

				apps.setWc(tempWc);

				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getScheme().equals("MCGS")) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(applicationRefNo);
					apps.setMCGFDetails(mcgfDetails);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

			} else if (appLoanType.equals("BO")) {

				//// System.out.println("Application DAO Line number-1460 Application Type Both");
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Entering if it is a Both Application..");

				apps.setLoanType("TC");
				if (!(apps.getScheme().equals("MCGS"))) {

					ssiDetails = submitRSFApp(apps, createdBy, connection);
					// ssiDetails = submitApp(apps,createdBy,connection);
					submitPromotersDetails(apps, connection);
					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);
					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

					if (apps.getBorrowerDetails().getSsiDetails()
							.getBorrowerRefNo() == 0) {
						ssiDetails = submitApp(apps, createdBy, connection);
						submitPromotersDetails(apps, connection);

						CallableStatement callable = connection
								.prepareCall("{?= call funcInsertMCGFMemBorrower(?,?,?,?,?,?) }");
						callable.registerOutParameter(1, Types.INTEGER);
						callable.setString(2, apps.getBankId());
						callable.setString(3, apps.getZoneId());
						callable.setString(4, apps.getBranchId());
						callable.setInt(5, apps.getBorrowerDetails()
								.getSsiDetails().getBorrowerRefNo());

						callable.setString(6, null);

						callable.registerOutParameter(7, Types.VARCHAR);

						callable.execute();

						int errorCode = callable.getInt(1);
						String error = callable.getString(7);

						if (errorCode == Constants.FUNCTION_FAILURE) {
							callable.close();
							callable = null;

							connection.rollback();

							Log.log(Log.ERROR, "MCGSDAO", "addSSIMembers",
									error);

							throw new DatabaseException(error);
						}

						callable.close();
						callable = null;

						if (ssiDetails != null) {

							apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						}

					}
				}

				// String
				// tcAppRefNo=submitApplicationDetails(apps,createdBy,connection);
				String tcAppRefNo = submitRsfApplicationDetails(apps,
						createdBy, connection);
				apps.setWcAppRefNo(tcAppRefNo);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"First Reference Number :" + apps.getWcAppRefNo());
				apps.setAppRefNo(tcAppRefNo);
				// apps.getMCGFDetails().setApplicationReferenceNumber(tcAppRefNo);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
						"Application Reference Number :" + apps.getAppRefNo());

				submitGuarantorSecurityDetails(apps, connection);
				submitTermCreditDetails(apps, createdBy, connection);
				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(tcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

				apps.setLoanType("WC");
				// setting the tc app ref no for the working capital application
				apps.setCgpanReference(tcAppRefNo);
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"TC App Ref No For Both Application:"
								+ apps.getCgpanReference());

				if (!(apps.getScheme().equals("MCGS"))) {

					if (ssiDetails != null) {
						apps.getBorrowerDetails().setSsiDetails(ssiDetails);
						String subSchemeName = rpProcessor.getSubScheme(apps);
						apps.setSubSchemeName(subSchemeName);

					}
				} else if (apps.getScheme().equals("MCGS")) {
					String subSchemeName = rpProcessor.getSubScheme(apps);
					apps.setSubSchemeName(subSchemeName);

				}

				String wcAppRefNo = submitRsfApplicationDetails(apps,
						createdBy, connection);
				// String
				// wcAppRefNo=submitApplicationDetails(apps,createdBy,connection);
				apps.setAppRefNo(wcAppRefNo);

				submitGuarantorSecurityDetails(apps, connection);
				// apps.getMCGFDetails().setApplicationReferenceNumber(wcAppRefNo);
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitApp",
						"Both Application Reference Number :"
								+ apps.getAppRefNo());
				submitTermCreditDetails(apps, createdBy, connection);

				WorkingCapital tempWc = apps.getWc();
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Term Loan Tenure :" + apps.getTermLoan().getTenure());
				tempWc.setWcTenure(apps.getTermLoan().getTenure());
				apps.setWc(tempWc);

				submitWCDetails(apps, createdBy, connection);
				submitSecDetails(apps, connection);

				if (apps.getMCGFDetails() != null) {
					MCGFDetails mcgfDetails = apps.getMCGFDetails();
					mcgfDetails.setApplicationReferenceNumber(wcAppRefNo);
					mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
							connection);
				}

				// setting the wc app ref no for the term loan capital
				// application
				Log.log(Log.INFO, "ApplicationDAO", "submitApp", "tcapprefno :"
						+ tcAppRefNo);
				Log.log(Log.INFO, "ApplicationDAO", "submitApp", "wcapprefno :"
						+ wcAppRefNo);

				updateAppReference(tcAppRefNo, wcAppRefNo, connection);

				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"Submitted a BO application");

			}
			connection.commit();

		} catch (SQLException e) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitApp", e.getMessage());
			Log.logException(e);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						ignore.getMessage());
			}

			throw new DatabaseException("Unable to submit Application");
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "Exited");

		return apps;

	}

	/*
	 * This method updates the cgpan reference column with the application
	 * reference number
	 */

	public void updateAppReference(String appRefNo, String appReference,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateAppReference", "Entered");

		try {

			CallableStatement updateRef = connection
					.prepareCall("{?=call funcUpdateAppRef(?,?,?)}");

			updateRef.registerOutParameter(1, Types.INTEGER);
			updateRef.registerOutParameter(4, Types.VARCHAR);

			updateRef.setString(2, appRefNo); // Generate App Ref No
			updateRef.setString(3, appReference); // Generate App Ref No

			updateRef.executeQuery();
			int functionReturnValue = updateRef.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateAppReference",
					"update App Reference :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateRef.getString(4);

				updateRef.close();
				updateRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateAppReference",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			}
			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateAppReference",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateAppReference",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	/*
	 * This method submits the application Details to the DB
	 */

	public String submitApplicationDetails(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
				"Exited");

		String appLoanType = apps.getLoanType();
		Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
				"Entering Application Detail method...");

		try {

			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
			String subSchemeName = rpProcessor.getSubScheme(apps);
			apps.setSubSchemeName(subSchemeName);

			// SP for Application Details************
			CallableStatement applicationDetails = connection
					.prepareCall("{?=call funcInsertApplicationDtlNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			applicationDetails.registerOutParameter(1, Types.INTEGER);
			applicationDetails.registerOutParameter(2, Types.VARCHAR); // Generate
																		// App
																		// Ref
																		// No
			applicationDetails.registerOutParameter(36, Types.VARCHAR);

			applicationDetails.setInt(3, ((apps.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo()); // SSI RefNo //ssi
															// ref no
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitApplicationDetails",
					"SSI Ref No:"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getBorrowerRefNo());

			applicationDetails.setString(4, apps.getScheme()); // scheme name
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Scheme Name:" + apps.getScheme());

			applicationDetails.setString(5, apps.getBankId()); // bank id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Bank Id :" + apps.getBankId());

			applicationDetails.setString(6, apps.getZoneId()); // zone id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Zone id :" + apps.getZoneId());

			applicationDetails.setString(7, apps.getBranchId()); // branch id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Branch id :" + apps.getBranchId());

			applicationDetails.setString(8, apps.getMliBranchName()); // branch
																		// name
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Branch name :" + apps.getMliBranchName());

			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Branch Code :" + apps.getMliBranchCode());
			if ((apps.getMliBranchCode()) != null
					&& !((apps.getMliBranchCode()).equals(""))) {
				applicationDetails.setString(9, apps.getMliBranchCode()); // branch
																			// code
			} else {
				applicationDetails.setString(9, null);
			}

			applicationDetails.setString(10, apps.getMliRefNo()); // bank ref no
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Bank Ref No :" + apps.getMliRefNo());

			if (appLoanType.equals("CC")) {
				applicationDetails.setString(11, "Y"); // composite loan
			} else {
				applicationDetails.setString(11, "N"); // composite loan
			}
			applicationDetails.setString(12, createdBy); // user id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"user id :" + createdBy);

			applicationDetails.setString(13, apps.getLoanType()); // loan type
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Loan type :" + apps.getLoanType());

			String collateralSecurityValue = (apps.getProjectOutlayDetails())
					.getCollateralSecurityTaken();
			applicationDetails.setString(14, collateralSecurityValue); // collateral
																		// security
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Collateral Security: " + collateralSecurityValue);

			String thirdPartyValue = (apps.getProjectOutlayDetails())
					.getThirdPartyGuaranteeTaken();
			applicationDetails.setString(15, thirdPartyValue); // Third party
																// taken
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Third party taken : " + thirdPartyValue);

			if (((apps.getProjectOutlayDetails()).getSubsidyName()) != null
					&& !(((apps.getProjectOutlayDetails()).getSubsidyName())
							.equals(""))) {
				applicationDetails.setString(16,
						(apps.getProjectOutlayDetails()).getSubsidyName()); // subsidy
																			// scheme
																			// name
			} else {
				applicationDetails.setString(16, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitApplicationDetails",
					"SubsidyName :"
							+ (apps.getProjectOutlayDetails()).getSubsidyName());

			if (apps.getRehabilitation() == null) {
				applicationDetails.setString(17, "N"); // rehabilitation
			} else {

				String rehabilitationValue = (apps.getRehabilitation());
				applicationDetails.setString(17, rehabilitationValue); // collateral
																		// security

			}

			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Rehabilitation :" + apps.getRehabilitation());

			applicationDetails.setDouble(18,
					(apps.getProjectOutlayDetails()).getProjectOutlay()); // project
																			// outlay
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitApplicationDetails",
					"Project Oultay :"
							+ (apps.getProjectOutlayDetails())
									.getProjectOutlay());

			String cgpanVal = apps.getCgpanReference();
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Cgpan reference:" + cgpanVal);

			if ((cgpanVal != null) && !(cgpanVal.equals(""))) {
				applicationDetails.setString(19, cgpanVal); // cgpan reference
				Log.log(Log.DEBUG, "ApplicationDAO",
						"submitApplicationDetails", "cgpan :" + cgpanVal);

			} else {
				applicationDetails.setNull(19, java.sql.Types.VARCHAR); // cgpan
																		// reference

			}
			applicationDetails.setString(20, createdBy); // created by
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"User :" + createdBy);

			if ((apps.getRemarks()) != null
					&& !((apps.getRemarks()).equals(""))) {

				applicationDetails.setString(21, apps.getRemarks()); // remarks

			} else {
				applicationDetails.setString(21, null);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Remarks :" + apps.getRemarks());

			applicationDetails.setString(22, apps.getSubSchemeName());
			// applicationDetails.setString(22,"Y");
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Sub scheme Name:" + apps.getSubSchemeName());

			/*System.out.println("MSE TYPE IS "
					+ apps.getBorrowerDetails().getSsiDetails().getMSE());*/

			if ((apps.getBorrowerDetails().getSsiDetails().getMSE()) != null
					&& !(apps.getBorrowerDetails().getSsiDetails().getMSE())
							.equals("")) {
				applicationDetails.setString(23, apps.getBorrowerDetails()
						.getSsiDetails().getMSE());
			} else {
				applicationDetails.setString(23, "N");
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					" MSE:"
							+ apps.getBorrowerDetails().getSsiDetails()
									.getMSE());
			applicationDetails.setString(24, apps.getInternalRate());
			// added by sukumar@path for handleing dc handicrafts
			if ((apps.getHandiCrafts()) != null
					&& !((apps.getHandiCrafts()).equals(""))) {
				applicationDetails.setString(25, apps.getHandiCrafts());
			} else {
				applicationDetails.setString(25, "N");
			}
			if ((apps.getDcHandicrafts()) != null
					&& !((apps.getDcHandicrafts()).equals(""))) {
				applicationDetails.setString(26, apps.getDcHandicrafts());
			} else {
				applicationDetails.setString(26, "N");
			}
			// applicationDetails.setString(26,apps.getDcHandicrafts());
			applicationDetails.setString(27, apps.getIcardNo());
			if ((apps.getIcardIssueDate()) != null
					&& !((apps.getIcardIssueDate()).equals(""))) {
				applicationDetails.setDate(28, new java.sql.Date(apps
						.getIcardIssueDate().getTime())); // branch code
			} else {
				applicationDetails.setDate(28, null);
			}
			if ((apps.getJointFinance()) != null
					&& !((apps.getJointFinance()).equals(""))) {
				applicationDetails.setString(29, apps.getJointFinance());
			} else {
				applicationDetails.setString(29, "N");
			}
			if ((apps.getAppExpiryDate()) != null
					&& !((apps.getAppExpiryDate()).equals(""))) {
				applicationDetails.setDate(30, new java.sql.Date(apps
						.getAppExpiryDate().getTime())); // branch code
			} else {
				applicationDetails.setDate(30, null);
			}

			if ((apps.getJointcgpan()) != null
					&& !((apps.getJointcgpan()).equals(""))) {
				applicationDetails.setString(31, apps.getJointcgpan()); // branch
																		// code
			} else {
				applicationDetails.setDate(31, null);
			}

			if (apps.getActivityConfirm() == null) {
				applicationDetails.setString(32, "N"); // rehabilitation
			} else {

				String activityConfirm = (apps.getActivityConfirm());
				applicationDetails.setString(32, activityConfirm); // collateral
																	// security

			}
			if ((apps.getDcHandlooms()) != null
					&& !((apps.getDcHandlooms()).equals(""))) {
				applicationDetails.setString(33, apps.getDcHandlooms());
			} else {
				applicationDetails.setString(33, "N");
			}
			applicationDetails.setString(34, apps.getWeaverCreditScheme());
			applicationDetails.setString(35, apps.getHandloomchk());

			applicationDetails.executeQuery();
			int functionReturnValue = applicationDetails.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApplicationDetails",
					"Application Details :" + functionReturnValue);
			//// System.out.println("ApplicationDAO,submitApplicationDetails,functionReturnValue"+functionReturnValue);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = applicationDetails.getString(36);

				applicationDetails.close();
				applicationDetails = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						"Application Detail Exception" + error);
				throw new DatabaseException(error);

			} else {

				apps.setAppRefNo(applicationDetails.getString(2));

				applicationDetails.close();
				applicationDetails = null;

			}

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		String applicationRefNo = apps.getAppRefNo();

		Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
				"Application Reference No. :" + applicationRefNo);

		Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
				"Exited");

		return applicationRefNo;
	}

	/**
	 * 
	 * @param apps
	 * @param createdBy
	 * @param connection
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String submitRsfApplicationDetails(Application apps,
			String createdBy, Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitRsfApplicationDetails",
				"Exited");

		String appLoanType = apps.getLoanType();
		Log.log(Log.INFO, "ApplicationDAO", "submitRsfApplicationDetails",
				"Entering Application Detail method...");

		try {

			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
			String subSchemeName = rpProcessor.getSubScheme(apps);
			apps.setSubSchemeName(subSchemeName);

			// SP for Application Details************
			CallableStatement applicationDetails = connection
					.prepareCall("{?=call funcInsertApplicationDtlMod(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			applicationDetails.registerOutParameter(1, Types.INTEGER);
			applicationDetails.registerOutParameter(2, Types.VARCHAR); // Generate
																		// App
																		// Ref
																		// No
			applicationDetails.registerOutParameter(26, Types.VARCHAR);

			applicationDetails.setInt(3, ((apps.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo()); // SSI RefNo //ssi
															// ref no
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitRsfApplicationDetails",
					"SSI Ref No:"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getBorrowerRefNo());

			applicationDetails.setString(4, apps.getScheme()); // scheme name
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Scheme Name:" + apps.getScheme());

			applicationDetails.setString(5, apps.getBankId()); // bank id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Bank Id :" + apps.getBankId());

			applicationDetails.setString(6, apps.getZoneId()); // zone id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Zone id :" + apps.getZoneId());

			applicationDetails.setString(7, apps.getBranchId()); // branch id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Branch id :" + apps.getBranchId());

			applicationDetails.setString(8, apps.getMliBranchName()); // branch
																		// name
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Branch name :" + apps.getMliBranchName());

			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Branch Code :" + apps.getMliBranchCode());
			if ((apps.getMliBranchCode()) != null
					&& !((apps.getMliBranchCode()).equals(""))) {
				applicationDetails.setString(9, apps.getMliBranchCode()); // branch
																			// code
			} else {
				applicationDetails.setString(9, null);
			}

			applicationDetails.setString(10, apps.getMliRefNo()); // bank ref no
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Bank Ref No :" + apps.getMliRefNo());

			if (appLoanType.equals("CC")) {
				applicationDetails.setString(11, "Y"); // composite loan
			} else {
				applicationDetails.setString(11, "N"); // composite loan
			}
			applicationDetails.setString(12, createdBy); // user id
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"user id :" + createdBy);

			applicationDetails.setString(13, apps.getLoanType()); // loan type
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Loan type :" + apps.getLoanType());

			String collateralSecurityValue = (apps.getProjectOutlayDetails())
					.getCollateralSecurityTaken();
			applicationDetails.setString(14, collateralSecurityValue); // collateral
																		// security
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Collateral Security: " + collateralSecurityValue);

			String thirdPartyValue = (apps.getProjectOutlayDetails())
					.getThirdPartyGuaranteeTaken();
			applicationDetails.setString(15, thirdPartyValue); // Third party
																// taken
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Third party taken : " + thirdPartyValue);

			if (((apps.getProjectOutlayDetails()).getSubsidyName()) != null
					&& !(((apps.getProjectOutlayDetails()).getSubsidyName())
							.equals(""))) {
				applicationDetails.setString(16,
						(apps.getProjectOutlayDetails()).getSubsidyName()); // subsidy
																			// scheme
																			// name
			} else {
				applicationDetails.setString(16, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitRsfApplicationDetails",
					"SubsidyName :"
							+ (apps.getProjectOutlayDetails()).getSubsidyName());

			if (apps.getRehabilitation() == null) {
				applicationDetails.setString(17, "N"); // rehabilitation
			} else {

				String rehabilitationValue = (apps.getRehabilitation());
				applicationDetails.setString(17, rehabilitationValue); // collateral
																		// security

			}

			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Rehabilitation :" + apps.getRehabilitation());

			applicationDetails.setDouble(18,
					(apps.getProjectOutlayDetails()).getProjectOutlay()); // project
																			// outlay
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"submitRsfApplicationDetails",
					"Project Oultay :"
							+ (apps.getProjectOutlayDetails())
									.getProjectOutlay());

			String cgpanVal = apps.getCgpanReference();
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Cgpan reference:" + cgpanVal);

			if ((cgpanVal != null) && !(cgpanVal.equals(""))) {
				applicationDetails.setString(19, cgpanVal); // cgpan reference
				Log.log(Log.DEBUG, "ApplicationDAO",
						"submitRsfApplicationDetails", "cgpan :" + cgpanVal);

			} else {
				applicationDetails.setNull(19, java.sql.Types.VARCHAR); // cgpan
																		// reference

			}
			applicationDetails.setString(20, createdBy); // created by
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"User :" + createdBy);

			if ((apps.getRemarks()) != null
					&& !((apps.getRemarks()).equals(""))) {

				applicationDetails.setString(21, apps.getRemarks()); // remarks

			} else {
				applicationDetails.setString(21, null);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Remarks :" + apps.getRemarks());

			applicationDetails.setString(22, apps.getSubSchemeName());
			// applicationDetails.setString(22,"Y");
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Sub scheme Name:" + apps.getSubSchemeName());

			if ((apps.getBorrowerDetails().getSsiDetails().getMSE()) != null
					&& !(apps.getBorrowerDetails().getSsiDetails().getMSE())
							.equals("")) {
				applicationDetails.setString(23, apps.getBorrowerDetails()
						.getSsiDetails().getMSE());
			} else {
				applicationDetails.setString(23, "N");
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					" MSE:"
							+ apps.getBorrowerDetails().getSsiDetails()
									.getMSE());
			applicationDetails.setString(24, apps.getInternalRate());
			applicationDetails.setString(25, apps.getExternalRate());
			applicationDetails.executeQuery();
			int functionReturnValue = applicationDetails.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "submitRsfApplicationDetails",
					"Application Details :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = applicationDetails.getString(24);

				applicationDetails.close();
				applicationDetails = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						"Application Detail Exception" + error);
				throw new DatabaseException(error);

			} else {

				apps.setAppRefNo(applicationDetails.getString(2));

				applicationDetails.close();
				applicationDetails = null;

			}

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitRsfApplicationDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO",
						"submitRsfApplicationDetails", ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		String applicationRefNo = apps.getAppRefNo();

		Log.log(Log.INFO, "ApplicationDAO", "submitRsfApplicationDetails",
				"Application Reference No. :" + applicationRefNo);

		Log.log(Log.INFO, "ApplicationDAO", "submitRsfApplicationDetails",
				"Exited");

		return applicationRefNo;
	}

	public String submitRsf2ApplicationDetails(Application apps,
			String createdBy, Connection connection) throws DatabaseException {
		Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails", "Exited");
		String appLoanType = apps.getLoanType();
		Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails",
				"Entering Application Detail method...");
		try {
			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
			String subSchemeName = rpProcessor.getSubScheme(apps);
			apps.setSubSchemeName(subSchemeName);
			/*System.out.println((new StringBuilder())
					.append(" apps.getScheme()").append(apps.getScheme())
					.toString());*/
			CallableStatement applicationDetails = connection
					.prepareCall("{?=call funcInsertApplicationDtlMod(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			applicationDetails.registerOutParameter(1, 4);
			applicationDetails.registerOutParameter(2, 12);
			applicationDetails.registerOutParameter(26, 12);
			applicationDetails.setInt(3, apps.getBorrowerDetails()
					.getSsiDetails().getBorrowerRefNo());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder())
							.append("SSI Ref No:")
							.append(apps.getBorrowerDetails().getSsiDetails()
									.getBorrowerRefNo()).toString());
			applicationDetails.setString(4, apps.getScheme());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Scheme Name:")
							.append(apps.getScheme()).toString());
			applicationDetails.setString(5, apps.getBankId());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Bank Id :")
							.append(apps.getBankId()).toString());
			applicationDetails.setString(6, apps.getZoneId());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Zone id :")
							.append(apps.getZoneId()).toString());
			applicationDetails.setString(7, apps.getBranchId());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Branch id :")
							.append(apps.getBranchId()).toString());
			applicationDetails.setString(8, apps.getMliBranchName());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Branch name :")
							.append(apps.getMliBranchName()).toString());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Branch Code :")
							.append(apps.getMliBranchCode()).toString());
			if (apps.getMliBranchCode() != null
					&& !apps.getMliBranchCode().equals(""))
				applicationDetails.setString(9, apps.getMliBranchCode());
			else
				applicationDetails.setString(9, null);
			applicationDetails.setString(10, apps.getMliRefNo());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Bank Ref No :")
							.append(apps.getMliRefNo()).toString());
			if (appLoanType.equals("CC"))
				applicationDetails.setString(11, "Y");
			else
				applicationDetails.setString(11, "N");
			applicationDetails.setString(12, createdBy);
			Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
					(new StringBuilder()).append("user id :").append(createdBy)
							.toString());
			applicationDetails.setString(13, apps.getLoanType());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Loan type :")
							.append(apps.getLoanType()).toString());
			String collateralSecurityValue = apps.getProjectOutlayDetails()
					.getCollateralSecurityTaken();
			applicationDetails.setString(14, collateralSecurityValue);
			Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Collateral Security: ")
							.append(collateralSecurityValue).toString());
			String thirdPartyValue = apps.getProjectOutlayDetails()
					.getThirdPartyGuaranteeTaken();
			applicationDetails.setString(15, thirdPartyValue);
			Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Third party taken : ")
							.append(thirdPartyValue).toString());
			if (apps.getProjectOutlayDetails().getSubsidyName() != null
					&& !apps.getProjectOutlayDetails().getSubsidyName()
							.equals(""))
				applicationDetails.setString(16, apps.getProjectOutlayDetails()
						.getSubsidyName());
			else
				applicationDetails.setString(16, null);
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder())
							.append("SubsidyName :")
							.append(apps.getProjectOutlayDetails()
									.getSubsidyName()).toString());
			if (apps.getRehabilitation() == null) {
				applicationDetails.setString(17, "N");
			} else {
				String rehabilitationValue = apps.getRehabilitation();
				applicationDetails.setString(17, rehabilitationValue);
			}
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Rehabilitation :")
							.append(apps.getRehabilitation()).toString());
			applicationDetails.setDouble(18, apps.getProjectOutlayDetails()
					.getProjectOutlay());
			Log.log(5,
					"ApplicationDAO",
					"submitRsfApplicationDetails",
					(new StringBuilder())
							.append("Project Oultay :")
							.append(apps.getProjectOutlayDetails()
									.getProjectOutlay()).toString());
			String cgpanVal = apps.getCgpanReference();
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Cgpan reference:")
							.append(cgpanVal).toString());
			if (cgpanVal != null && !cgpanVal.equals("")) {
				applicationDetails.setString(19, cgpanVal);
				Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
						(new StringBuilder()).append("cgpan :")
								.append(cgpanVal).toString());
			} else {
				applicationDetails.setNull(19, 12);
			}
			applicationDetails.setString(20, createdBy);
			Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
					(new StringBuilder()).append("User :").append(createdBy)
							.toString());
			if (apps.getRemarks() != null && !apps.getRemarks().equals(""))
				applicationDetails.setString(21, apps.getRemarks());
			else
				applicationDetails.setString(21, null);
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Remarks :")
							.append(apps.getRemarks()).toString());
			applicationDetails.setString(22, apps.getSubSchemeName());
			Log.log(5,
					"ApplicationDAO",
					"submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Sub scheme Name:")
							.append(apps.getSubSchemeName()).toString());
			if (apps.getBorrowerDetails().getSsiDetails().getMSE() != null
					&& !apps.getBorrowerDetails().getSsiDetails().getMSE()
							.equals(""))
				applicationDetails.setString(23, apps.getBorrowerDetails()
						.getSsiDetails().getMSE());
			else
				applicationDetails.setString(23, "N");
			Log.log(5,
					"ApplicationDAO",
					"submitRsfApplicationDetails",
					(new StringBuilder())
							.append(" MSE:")
							.append(apps.getBorrowerDetails().getSsiDetails()
									.getMSE()).toString());
			applicationDetails.setString(24, apps.getInternalRate());
			applicationDetails.setString(25, apps.getExternalRate());
			applicationDetails.executeQuery();
			int functionReturnValue = applicationDetails.getInt(1);
			Log.log(5, "ApplicationDAO", "submitRsf2ApplicationDetails",
					(new StringBuilder()).append("Application Details :")
							.append(functionReturnValue).toString());
			if (functionReturnValue == 1) {
				String error = applicationDetails.getString(24);
				applicationDetails.close();
				applicationDetails = null;
				connection.rollback();
				Log.log(2,
						"ApplicationDAO",
						"submitRsf2ApplicationDetails",
						(new StringBuilder())
								.append("Application Detail Exception")
								.append(error).toString());
				throw new DatabaseException(error);
			}
			apps.setAppRefNo(applicationDetails.getString(2));
			applicationDetails.close();
			applicationDetails = null;
		} catch (SQLException sqlException) {
			Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails",
					sqlException.getMessage());
			Log.logException(sqlException);
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails",
						ignore.getMessage());
			}
			throw new DatabaseException(sqlException.getMessage());
		}
		String applicationRefNo = apps.getAppRefNo();
		Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails",
				(new StringBuilder()).append("Application Reference No. :")
						.append(applicationRefNo).toString());
		Log.log(4, "ApplicationDAO", "submitRsf2ApplicationDetails", "Exited");
		return applicationRefNo;
	}

	/* ----------------- */
	/*
	 * This method submits the promoter /guarantors and primary Security Details
	 * into the database
	 */

	public void submitPromotersDetails(Application apps, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails", "Entered");

		// SP for promoters Details
		try {

			String coveredValue = (apps.getBorrowerDetails())
					.getPreviouslyCovered();
			// String physicallyHandicapped =
			// (apps.getBorrowerDetails().getSsiDetails()).getPhysicallyHandicapped();
			//// System.out.println("physicallyHandicapped:"+physicallyHandicapped);
			if (coveredValue.equals("N")) { // if borrower covered then get SSI
											// ref No,update SSi Details

				CallableStatement promotersDetails = connection
						.prepareCall("{?=call funcInsertPromoterDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				promotersDetails.registerOutParameter(1, Types.INTEGER);
				promotersDetails.registerOutParameter(24, Types.VARCHAR);

				promotersDetails.setInt(2, ((apps.getBorrowerDetails())
						.getSsiDetails()).getBorrowerRefNo()); // SSI ref no
				//// System.out.println("2"+((apps.getBorrowerDetails()).getSsiDetails()).getBorrowerRefNo());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitPromotersDetails",
						"borrower ref no:"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getBorrowerRefNo());
				promotersDetails.setString(3, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpTitle()); // title
				//// System.out.println("3"+((apps.getBorrowerDetails()).getSsiDetails()).getCpTitle());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitPromotersDetails",
						"cp title:"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCpTitle());
				promotersDetails.setString(4, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpFirstName()); // first name
				//// System.out.println("4"+((apps.getBorrowerDetails()).getSsiDetails()).getCpFirstName());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitPromotersDetails",
						"cp forst name:"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCpFirstName());

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getCpMiddleName()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCpMiddleName()).equals(""))) {
					promotersDetails.setString(5, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCpMiddleName()); // middle name
					//// System.out.println("5"+((apps.getBorrowerDetails()).getSsiDetails()).getCpMiddleName());
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitPromotersDetails",
							"cp middle name:"
									+ ((apps.getBorrowerDetails())
											.getSsiDetails()).getCpMiddleName());
				} else {
					promotersDetails.setString(5, null);

				}
				//// System.out.println("5"+((apps.getBorrowerDetails()).getSsiDetails()).getCpMiddleName());
				promotersDetails.setString(6, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpLastName()); // last name
				//// System.out.println("6"+((apps.getBorrowerDetails()).getSsiDetails()).getCpLastName());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitPromotersDetails",
						"cp last name:"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCpLastName());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getCpITPAN()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCpITPAN()).equals(""))) {
					promotersDetails.setString(7, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCpITPAN()); // itpan
					//// System.out.println("7"+((apps.getBorrowerDetails()).getSsiDetails()).getCpITPAN());
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitPromotersDetails",
							"cp it pan:"
									+ ((apps.getBorrowerDetails())
											.getSsiDetails()).getCpITPAN());

				} else {
					promotersDetails.setString(7, null);

					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitPromotersDetails",
							"cp promoter first DOB :"
									+ (((apps.getBorrowerDetails())
											.getSsiDetails()).getCpITPAN()));
				}

				promotersDetails.setString(8, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpGender()); // gender
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitPromotersDetails",
						"cp gender:"
								+ ((apps.getBorrowerDetails()).getSsiDetails())
										.getCpGender());

				if ((((apps.getBorrowerDetails()).getSsiDetails()).getCpDOB()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCpDOB()).toString().equals(""))) {
					promotersDetails.setDate(
							9,
							new java.sql.Date((((apps.getBorrowerDetails())
									.getSsiDetails()).getCpDOB()).getTime())); // dob
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitPromotersDetails",
							"cp dob:"
									+ new java.sql.Date((((apps
											.getBorrowerDetails())
											.getSsiDetails()).getCpDOB())
											.getTime()));
				} else {
					promotersDetails.setDate(9, null);

				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getCpLegalID()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCpLegalID()).equals(""))) {
					promotersDetails.setString(10, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCpLegalID()); // legal ID

				} else {
					promotersDetails.setString(10, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getCpLegalIdValue()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getCpLegalIdValue()).equals(""))) {
					promotersDetails.setString(11, ((apps.getBorrowerDetails())
							.getSsiDetails()).getCpLegalIdValue()); // legal
																	// Value

				} else {
					promotersDetails.setString(11, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getFirstName()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getFirstName()).equals(""))) {
					promotersDetails.setString(12, ((apps.getBorrowerDetails())
							.getSsiDetails()).getFirstName()); // first Name

				} else {
					promotersDetails.setString(12, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getFirstDOB()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getFirstDOB()).toString().equals(""))) {
					promotersDetails
							.setDate(
									13,
									new java.sql.Date((((apps
											.getBorrowerDetails())
											.getSsiDetails()).getFirstDOB())
											.getTime()));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitPromotersDetails",
							"cp promoter first DOB :"
									+ new java.sql.Date((((apps
											.getBorrowerDetails())
											.getSsiDetails()).getFirstDOB())
											.getTime()));
				} else {

					promotersDetails.setDate(13, null);
					// first DOB
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getFirstItpan()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getFirstItpan()).equals(""))) {
					promotersDetails.setString(14, ((apps.getBorrowerDetails())
							.getSsiDetails()).getFirstItpan()); // first itpan

				} else {
					promotersDetails.setString(14, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSecondName()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSecondName()).equals(""))) {
					promotersDetails.setString(15, ((apps.getBorrowerDetails())
							.getSsiDetails()).getSecondName()); // second Name

				} else {
					promotersDetails.setString(15, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSecondDOB()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSecondDOB()).toString().equals(""))) {
					promotersDetails
							.setDate(
									16,
									new java.sql.Date((((apps
											.getBorrowerDetails())
											.getSsiDetails()).getSecondDOB())
											.getTime()));
				} else {

					promotersDetails.setDate(16, null);
					// second DOB
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSecondItpan()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSecondItpan()).equals(""))) {
					promotersDetails.setString(17, ((apps.getBorrowerDetails())
							.getSsiDetails()).getSecondItpan()); // second itpan

				} else {
					promotersDetails.setString(17, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getThirdName()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getThirdName()).equals(""))) {
					promotersDetails.setString(18, ((apps.getBorrowerDetails())
							.getSsiDetails()).getThirdName()); // third Name

				} else {
					promotersDetails.setString(18, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getThirdDOB()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getThirdDOB()).toString().equals(""))) {
					promotersDetails
							.setDate(
									19,
									new java.sql.Date((((apps
											.getBorrowerDetails())
											.getSsiDetails()).getThirdDOB())
											.getTime()));

				} else {
					// third DOB
					promotersDetails.setDate(19, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getThirdItpan()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getThirdItpan()).equals(""))) {
					promotersDetails.setString(20, ((apps.getBorrowerDetails())
							.getSsiDetails()).getThirdItpan()); // third itpan

				} else {
					promotersDetails.setString(20, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getSocialCategory()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getSocialCategory()).equals(""))) {
					promotersDetails.setString(21, ((apps.getBorrowerDetails())
							.getSsiDetails()).getSocialCategory()); // social
																	// category

				} else {
					promotersDetails.setString(21, null);
				}
				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getReligion()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getReligion()).equals(""))) {
					promotersDetails.setString(22, ((apps.getBorrowerDetails())
							.getSsiDetails()).getReligion()); // social category

				} else {
					promotersDetails.setString(22, null);
				}

				if ((((apps.getBorrowerDetails()).getSsiDetails())
						.getPhysicallyHandicapped()) != null
						&& !((((apps.getBorrowerDetails()).getSsiDetails())
								.getPhysicallyHandicapped()).equals(""))) {
					promotersDetails.setString(23, ((apps.getBorrowerDetails())
							.getSsiDetails()).getPhysicallyHandicapped()); // Physically
																			// Handicapped

				} else {
					promotersDetails.setString(23, null);
				}
				// String physicallyHandicapped =
				// (apps.getBorrowerDetails().getSsiDetails()).getPhysicallyHandicapped();
				//// System.out.println("physicallyHandicapped:"+(apps.getBorrowerDetails().getSsiDetails()).getPhysicallyHandicapped());
				promotersDetails.executeQuery();

				int promotersDetailsValue = promotersDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"promoters Details result" + promotersDetailsValue);

				if (promotersDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = promotersDetails.getString(24);

					promotersDetails.close();
					promotersDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Promoter Detail Exception" + error);
					throw new DatabaseException(error);

				}

				promotersDetails.close();
				promotersDetails = null;

			}
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	// SP for guarantors details
	public void submitGuarantorSecurityDetails(Application apps,
			Connection connection) throws DatabaseException {

		try {

			if (((apps.getProjectOutlayDetails()).getGuarantorsName1()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName1())
							.equals(""))) {

				CallableStatement guarantorsDetails1 = connection
						.prepareCall("{?=call funcInsertPersonalGuar(?,?,?,?)}");
				guarantorsDetails1.registerOutParameter(1, Types.INTEGER);
				guarantorsDetails1.registerOutParameter(5, Types.VARCHAR);

				guarantorsDetails1.setString(2, apps.getAppRefNo()); // app ref
																		// no

				guarantorsDetails1.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName1());

				guarantorsDetails1.setDouble(4,
						(apps.getProjectOutlayDetails())
								.getGuarantorsNetWorth1());

				guarantorsDetails1.executeQuery();

				int guarantorsDetailsValue1 = guarantorsDetails1.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Guarantors Details1 result :"
								+ guarantorsDetailsValue1);

				if (guarantorsDetailsValue1 == Constants.FUNCTION_FAILURE) {

					String error = guarantorsDetails1.getString(5);

					guarantorsDetails1.close();
					guarantorsDetails1 = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Guarantor Detail Exception" + error);
					throw new DatabaseException(error);
				}
				guarantorsDetails1.close();
				guarantorsDetails1 = null;

			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName2()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName2())
							.equals(""))) {
				CallableStatement guarantorsDetails2 = connection
						.prepareCall("{?=call funcInsertPersonalGuar(?,?,?,?)}");
				guarantorsDetails2.registerOutParameter(1, Types.INTEGER);
				guarantorsDetails2.registerOutParameter(5, Types.VARCHAR);

				guarantorsDetails2.setString(2, apps.getAppRefNo()); // app ref
																		// no

				guarantorsDetails2.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName2());

				guarantorsDetails2.setDouble(4,
						(apps.getProjectOutlayDetails())
								.getGuarantorsNetWorth2());

				guarantorsDetails2.executeQuery();

				int guarantorsDetailsValue2 = guarantorsDetails2.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Guarantors Details result :" + guarantorsDetailsValue2);

				if (guarantorsDetailsValue2 == Constants.FUNCTION_FAILURE) {

					String error = guarantorsDetails2.getString(5);

					guarantorsDetails2.close();
					guarantorsDetails2 = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Guarantor2 Detail Exception" + error);
					throw new DatabaseException(error);
				}

				guarantorsDetails2.close();
				guarantorsDetails2 = null;
			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName3()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName3())
							.equals(""))) {

				CallableStatement guarantorsDetails3 = connection
						.prepareCall("{?=call funcInsertPersonalGuar(?,?,?,?)}");
				guarantorsDetails3.registerOutParameter(1, Types.INTEGER);
				guarantorsDetails3.registerOutParameter(5, Types.VARCHAR);

				guarantorsDetails3.setString(2, apps.getAppRefNo()); // app ref
																		// no

				guarantorsDetails3.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName3());

				guarantorsDetails3.setDouble(4,
						(apps.getProjectOutlayDetails())
								.getGuarantorsNetWorth3());

				guarantorsDetails3.executeQuery();

				int guarantorsDetailsValue3 = guarantorsDetails3.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Guarantors Details3 result :"
								+ guarantorsDetailsValue3);

				if (guarantorsDetailsValue3 == Constants.FUNCTION_FAILURE) {

					String error = guarantorsDetails3.getString(5);

					guarantorsDetails3.close();
					guarantorsDetails3 = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Guarantor3 Detail Exception" + error);
					throw new DatabaseException(error);
				}
				guarantorsDetails3.close();
				guarantorsDetails3 = null;

			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName4()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName4())
							.equals(""))) {

				CallableStatement guarantorsDetails4 = connection
						.prepareCall("{?=call funcInsertPersonalGuar(?,?,?,?)}");
				guarantorsDetails4.registerOutParameter(1, Types.INTEGER);
				guarantorsDetails4.registerOutParameter(5, Types.VARCHAR);

				guarantorsDetails4.setString(2, apps.getAppRefNo()); // app ref
																		// no

				guarantorsDetails4.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName4());

				guarantorsDetails4.setDouble(4,
						(apps.getProjectOutlayDetails())
								.getGuarantorsNetWorth4());

				guarantorsDetails4.executeQuery();

				int guarantorsDetailsValue4 = guarantorsDetails4.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Guarantors Details4 result :"
								+ guarantorsDetailsValue4);

				if (guarantorsDetailsValue4 == Constants.FUNCTION_FAILURE) {

					String error = guarantorsDetails4.getString(5);

					guarantorsDetails4.close();
					guarantorsDetails4 = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Guarantor4 Detail Exception" + error);
					throw new DatabaseException(error);
				}
				guarantorsDetails4.close();
				guarantorsDetails4 = null;
			}

			// SP for primary Security Details-Land

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getLandParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails()).getLandParticulars())
							.equals(""))) {

				CallableStatement psLandDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psLandDetails.registerOutParameter(1, Types.INTEGER);
				psLandDetails.registerOutParameter(6, Types.VARCHAR);

				psLandDetails.setString(2, apps.getAppRefNo()); // app ref no
				psLandDetails.setString(3, "Land"); // PS Type

				psLandDetails.setString(4, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getLandParticulars())); // particulars

				psLandDetails.setDouble(5, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getLandValue()));

				psLandDetails.executeQuery();

				int psLandDetailsValue = psLandDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Land Details1 result :" + psLandDetailsValue);

				if (psLandDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psLandDetails.getString(5);

					psLandDetails.close();
					psLandDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails", "Land Detail Exception"
									+ error);
					throw new DatabaseException(error);
				}
				psLandDetails.close();
				psLandDetails = null;

			}

			// SP for primary Security Details-Building

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getBldgParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails()).getBldgParticulars())
							.equals(""))) {
				CallableStatement psBldgDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psBldgDetails.registerOutParameter(1, Types.INTEGER);
				psBldgDetails.registerOutParameter(6, Types.VARCHAR);

				psBldgDetails.setString(2, apps.getAppRefNo()); // app ref no
				psBldgDetails.setString(3, "Building"); // PS Type

				psBldgDetails.setString(4, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getBldgParticulars())); // particulars

				psBldgDetails.setDouble(5, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getBldgValue()));

				psBldgDetails.executeQuery();

				int psBldgDetailsValue = psBldgDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Building Details1 result :" + psBldgDetailsValue);

				if (psBldgDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psBldgDetails.getString(6);

					psBldgDetails.close();
					psBldgDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails", "Bldg Detail Exception"
									+ error);
					throw new DatabaseException(error);
				}
				psBldgDetails.close();
				psBldgDetails = null;

			}

			// SP for primary Security Details-Machine

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getMachineParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getMachineParticulars()).equals(""))) {
				CallableStatement psMachineDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psMachineDetails.registerOutParameter(1, Types.INTEGER);
				psMachineDetails.registerOutParameter(6, Types.VARCHAR);

				psMachineDetails.setString(2, apps.getAppRefNo()); // app ref no
				psMachineDetails.setString(3, "Machinery"); // PS Type

				psMachineDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getMachineParticulars())); // particulars

				psMachineDetails
						.setDouble(5,
								(((apps.getProjectOutlayDetails())
										.getPrimarySecurityDetails())
										.getMachineValue()));

				psMachineDetails.executeQuery();

				int psMachineDetailsValue = psMachineDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Machine Details result :" + psMachineDetailsValue);

				if (psMachineDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psMachineDetails.getString(6);

					psMachineDetails.close();
					psMachineDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Machine Detail Exception" + error);
					throw new DatabaseException(error);
				}
				psMachineDetails.close();
				psMachineDetails = null;

			}

			// Sp fpr primary Security Details-fixed/movable Assets

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getAssetsParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getAssetsParticulars()).equals(""))) {
				CallableStatement psAssetsDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psAssetsDetails.registerOutParameter(1, Types.INTEGER);
				psAssetsDetails.registerOutParameter(6, Types.VARCHAR);

				psAssetsDetails.setString(2, apps.getAppRefNo()); // app ref no
				psAssetsDetails.setString(3, "Fixed Assets"); // PS Type

				psAssetsDetails.setString(4, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getAssetsParticulars())); // particulars

				psAssetsDetails.setDouble(5, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getAssetsValue()));

				psAssetsDetails.executeQuery();

				int psAssetsDetailsValue = psAssetsDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Assets Details result :" + psAssetsDetailsValue);

				if (psAssetsDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psAssetsDetails.getString(6);

					psAssetsDetails.close();
					psAssetsDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails", "Assets Detail Exception"
									+ error);
					throw new DatabaseException(error);
				}
				psAssetsDetails.close();
				psAssetsDetails = null;

			}

			// SP for Primary Security Details-Current Assets

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getCurrentAssetsParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getCurrentAssetsParticulars()).equals(""))) {

				CallableStatement psCurrentAssetsDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psCurrentAssetsDetails.registerOutParameter(1, Types.INTEGER);
				psCurrentAssetsDetails.registerOutParameter(6, Types.VARCHAR);

				psCurrentAssetsDetails.setString(2, apps.getAppRefNo()); // app
																			// ref
																			// no
				psCurrentAssetsDetails.setString(3, "Current Assets"); // PS
																		// Type

				psCurrentAssetsDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getCurrentAssetsParticulars())); // particulars

				psCurrentAssetsDetails.setDouble(5,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getCurrentAssetsValue()));

				psCurrentAssetsDetails.executeQuery();

				int psCurrentAssetsDetailsValue = psCurrentAssetsDetails
						.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Current Assets Details result :"
								+ psCurrentAssetsDetailsValue);

				if (psCurrentAssetsDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psCurrentAssetsDetails.getString(6);

					psCurrentAssetsDetails.close();
					psCurrentAssetsDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails",
							"Current Assets Detail Exception" + error);
					throw new DatabaseException(error);
				}
				psCurrentAssetsDetails.close();
				psCurrentAssetsDetails = null;

			}

			// SP for Primary Security Details-Others

			if ((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getOthersParticulars()) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getOthersParticulars()).equals(""))) {

				CallableStatement psOthersDetails = connection
						.prepareCall("{?=call funcInsertPrimarySecurity(?,?,?,?,?)}");
				psOthersDetails.registerOutParameter(1, Types.INTEGER);
				psOthersDetails.registerOutParameter(6, Types.VARCHAR);

				psOthersDetails.setString(2, apps.getAppRefNo()); // app ref no
				psOthersDetails.setString(3, "Others"); // PS Type

				psOthersDetails.setString(4, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getOthersParticulars())); // particulars

				psOthersDetails.setDouble(5, (((apps.getProjectOutlayDetails())
						.getPrimarySecurityDetails()).getOthersValue()));

				psOthersDetails.executeQuery();

				int psOthersDetailsValue = psOthersDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"Others Details result :" + psOthersDetailsValue);

				if (psOthersDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = psOthersDetails.getString(6);

					psOthersDetails.close();
					psOthersDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitPromotersDetails", "Other Detail Exception"
									+ error);
					throw new DatabaseException(error);
				}
				psOthersDetails.close();
				psOthersDetails = null;

			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "submitPromotersDetails", "Exited");
	}

	/*
	 * This method submits the term Loan Details to the DB
	 */

	public void submitTermCreditDetails(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitTermCreditDetails",
				"Entered");
		String appLoanType = apps.getLoanType();

		try {

			if ((appLoanType.equals("TC")) || (appLoanType.equals("CC"))
					|| (appLoanType.equals("BO"))) {
				CallableStatement termLoanDetails = connection
						.prepareCall("{?=call funcInsertTermLoan(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				termLoanDetails.registerOutParameter(1, Types.INTEGER);
				termLoanDetails.registerOutParameter(19, Types.VARCHAR);

				termLoanDetails.setString(2, apps.getAppRefNo()); // app ref no
				Log.log(Log.DEBUG, "ApplicationDAO", "submitTermCreditDetails",
						"app ref no :" + apps.getAppRefNo());

				termLoanDetails.setDouble(3, (apps.getProjectOutlayDetails())
						.getTermCreditSanctioned());
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitTermCreditDetails",
						"trm sancitoned:"
								+ (apps.getProjectOutlayDetails())
										.getTermCreditSanctioned());

				if (((apps.getTermLoan()).getAmountSanctionedDate()) != null
						&& !(((apps.getTermLoan()).getAmountSanctionedDate())
								.toString().equals(""))) {
					termLoanDetails.setDate(
							4,
							new java.sql.Date(((apps.getTermLoan())
									.getAmountSanctionedDate()).getTime()));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"trm sancitoned Date:"
									+ new java.sql.Date(((apps.getTermLoan())
											.getAmountSanctionedDate())
											.getTime()));
				} else {
					termLoanDetails.setDate(4, null);

				}

				if (((apps.getProjectOutlayDetails())
						.getTcPromoterContribution()) == 0) {
					termLoanDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getTcPromoterContribution()); // promoter Cont
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"promoter cont:"
									+ (apps.getProjectOutlayDetails())
											.getTcPromoterContribution());
				}

				if (((apps.getProjectOutlayDetails()).getTcSubsidyOrEquity()) == 0) {
					termLoanDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getTcSubsidyOrEquity()); // subsidy
																					// or
																					// equity
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"subsidy:"
									+ (apps.getProjectOutlayDetails())
											.getTcSubsidyOrEquity());
				}

				if (((apps.getProjectOutlayDetails()).getTcOthers()) == 0) {
					termLoanDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getTcOthers()); // Others
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"others:"
									+ (apps.getProjectOutlayDetails())
											.getTcOthers());
				}

				if (((apps.getTermLoan()).getCreditGuaranteed()) == 0) {
					termLoanDetails.setNull(8, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(8,
							(apps.getTermLoan()).getCreditGuaranteed()); // credit
																			// to
																			// be
																			// guraranteed
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails",
							"CG:" + (apps.getTermLoan()).getCreditGuaranteed());
				}

				if (((apps.getTermLoan()).getTenure()) == 0) {
					termLoanDetails.setNull(9, java.sql.Types.INTEGER);
				} else {
					termLoanDetails.setInt(9, (apps.getTermLoan()).getTenure()); // Tenure
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails",
							"tenire:" + (apps.getTermLoan()).getTenure());
				}

				if (((apps.getTermLoan()).getInterestType()) != null
						&& !(((apps.getTermLoan()).getInterestType())
								.equals(""))) {
					termLoanDetails.setString(10,
							(apps.getTermLoan()).getInterestType()); // Interest
																		// Type-Fixed

				} else {
					termLoanDetails.setString(10, null);
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"interest type:"
									+ (apps.getTermLoan()).getInterestType());
				}

				if (((apps.getTermLoan()).getInterestRate()) == 0) {
					termLoanDetails.setNull(11, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(11,
							(apps.getTermLoan()).getInterestRate()); // Interest
																		// Rate
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"interest type:"
									+ (apps.getTermLoan()).getInterestRate());
				}

				if (((apps.getTermLoan()).getBenchMarkPLR()) == 0) {
					termLoanDetails.setNull(12, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails.setDouble(12,
							(apps.getTermLoan()).getBenchMarkPLR()); // Bench
																		// Mark
																		// PLR
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails",
							"B plr:" + (apps.getTermLoan()).getBenchMarkPLR());
				}

				if (((apps.getTermLoan()).getPlr()) == 0) {
					termLoanDetails.setNull(13, java.sql.Types.DOUBLE);
				} else {
					termLoanDetails
							.setDouble(13, (apps.getTermLoan()).getPlr()); // PLR
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails",
							"B plr:" + (apps.getTermLoan()).getPlr());
				}

				if (((apps.getTermLoan()).getRepaymentMoratorium()) == 0) {
					termLoanDetails.setNull(14, java.sql.Types.INTEGER);
				} else {
					termLoanDetails.setInt(14,
							(apps.getTermLoan()).getRepaymentMoratorium());// Moratorium
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"moratorium:"
									+ (apps.getTermLoan())
											.getRepaymentMoratorium());
				}

				if (((apps.getTermLoan()).getFirstInstallmentDueDate()) != null
						&& !(((apps.getTermLoan()).getFirstInstallmentDueDate())
								.toString().equals(""))) {
					termLoanDetails.setDate(
							15,
							new java.sql.Date(((apps.getTermLoan())
									.getFirstInstallmentDueDate()).getTime())); // First
																				// Install
																				// Date
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"install du date:"
									+ new java.sql.Date(((apps.getTermLoan())
											.getFirstInstallmentDueDate())
											.getTime()));
				} else {
					termLoanDetails.setDate(15, null);

				}

				if (((apps.getTermLoan()).getNoOfInstallments()) == 0) {
					termLoanDetails.setNull(16, java.sql.Types.INTEGER);
				} else {
					termLoanDetails.setInt(16,
							(apps.getTermLoan()).getNoOfInstallments());// No of
																		// Installments
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"installs:"
									+ (apps.getTermLoan())
											.getNoOfInstallments());
				}

				if (((apps.getTermLoan()).getPeriodicity()) != 1
						&& ((apps.getTermLoan()).getPeriodicity()) != 4
						&& ((apps.getTermLoan()).getPeriodicity()) != 5
						&& ((apps.getTermLoan()).getPeriodicity()) != 2
						&& ((apps.getTermLoan()).getPeriodicity()) != 3) {
					termLoanDetails.setNull(17, java.sql.Types.INTEGER);
				} else {
					termLoanDetails.setInt(17,
							(apps.getTermLoan()).getPeriodicity());// Periodicity
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"periodicity:"
									+ (apps.getTermLoan()).getPeriodicity());
				}

				if ((apps.getTermLoan()).getTypeOfPLR() != null
						&& !(((apps.getTermLoan()).getTypeOfPLR()).equals(""))) {
					termLoanDetails.setString(18,
							(apps.getTermLoan()).getTypeOfPLR()); // TYpe of PLR

				} else {

					termLoanDetails.setNull(18, java.sql.Types.VARCHAR);
				}

				termLoanDetails.executeQuery();
				int termLoanDetailsValue = termLoanDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitTermCreditDetails",
						"TC Details result :" + termLoanDetailsValue);

				if (termLoanDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = termLoanDetails.getString(19);

					termLoanDetails.close();
					termLoanDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitTermCreditDetails",
							"Term Credit Dtl Exception" + error);
					throw new DatabaseException(error);

				}
				termLoanDetails.close();
				termLoanDetails = null;

				// inserting the repayment details in the table for GM
				CallableStatement repaymentDtls = connection
						.prepareCall("{?=call funcInsTRMRepayment(?,?,?,?,?,?,?)}");

				repaymentDtls.registerOutParameter(1, Types.INTEGER);
				repaymentDtls.registerOutParameter(8, Types.VARCHAR);

				repaymentDtls.setString(2, apps.getAppRefNo());

				if (((apps.getTermLoan()).getRepaymentMoratorium()) == 0) {
					repaymentDtls.setNull(3, java.sql.Types.INTEGER);
				} else {

					repaymentDtls.setInt(3,
							(apps.getTermLoan()).getRepaymentMoratorium());// Moratorium
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"moratorium:"
									+ (apps.getTermLoan())
											.getRepaymentMoratorium());
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitTermCreditDetails",
						"apps.getTermLoan()).getFirstInstallmentDueDate()):"
								+ (apps.getTermLoan())
										.getFirstInstallmentDueDate());

				if (((apps.getTermLoan()).getFirstInstallmentDueDate()) != null
						&& !(((apps.getTermLoan()).getFirstInstallmentDueDate())
								.toString().equals(""))) {
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails1",
							"apps.getTermLoan()).getFirstInstallmentDueDate()):"
									+ (apps.getTermLoan())
											.getFirstInstallmentDueDate());

					repaymentDtls.setDate(
							4,
							new java.sql.Date(((apps.getTermLoan())
									.getFirstInstallmentDueDate()).getTime())); // First
																				// Install
																				// Date
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"install du date:"
									+ new java.sql.Date(((apps.getTermLoan())
											.getFirstInstallmentDueDate())
											.getTime()));
				} else {
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails2",
							"apps.getTermLoan()).getFirstInstallmentDueDate()):"
									+ (apps.getTermLoan())
											.getFirstInstallmentDueDate());

					repaymentDtls.setDate(4, null);

				}

				if (((apps.getTermLoan()).getPeriodicity()) != 1
						&& ((apps.getTermLoan()).getPeriodicity()) != 2
						&& ((apps.getTermLoan()).getPeriodicity()) != 3) {
					repaymentDtls.setNull(5, java.sql.Types.INTEGER);
				} else {

					repaymentDtls.setInt(5,
							(apps.getTermLoan()).getPeriodicity());// Periodicity
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"periodicity:"
									+ (apps.getTermLoan()).getPeriodicity());
				}

				if (((apps.getTermLoan()).getNoOfInstallments()) == 0) {
					repaymentDtls.setNull(6, java.sql.Types.INTEGER);
				} else {

					repaymentDtls.setInt(6,
							(apps.getTermLoan()).getNoOfInstallments());// No of
																		// Installments
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"installs:"
									+ (apps.getTermLoan())
											.getNoOfInstallments());
				}

				repaymentDtls.setString(7, createdBy);

				repaymentDtls.executeQuery();
				int repaymentDtlsValue = repaymentDtls.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitTermCreditDetails",
						"TC Details result :" + repaymentDtlsValue);

				if (repaymentDtlsValue == Constants.FUNCTION_FAILURE) {

					String error = repaymentDtls.getString(8);

					repaymentDtls.close();
					repaymentDtls = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitTermCreditDetails",
							"Repayment Dtl Exception" + error);
					throw new DatabaseException(error);

				}
				repaymentDtls.close();
				repaymentDtls = null;

				// if amount disbursed is entered then insert outstanding and
				// disbursement details
				if (((apps.getTermLoan()).getAmtDisbursed()) != 0) {
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"Amount disb"
									+ (apps.getTermLoan()).getAmtDisbursed());
					//// System.out.println("before calling funcInsertTCOutStand");
					// Insert Outstanding Details
					CallableStatement termLoanOutDetails = connection
							.prepareCall("{?=call funcInsertTCOutStand(?,?,?,?)}");
					//// System.out.println("Inside funcInsertTCOutStand:");
					termLoanOutDetails.registerOutParameter(1, Types.INTEGER);
					termLoanOutDetails.registerOutParameter(5, Types.VARCHAR);
					//// System.out.println("apps.getAppRefNo()"+apps.getAppRefNo());
					termLoanOutDetails.setString(2, apps.getAppRefNo());

					//// System.out.println("apps.getTermLoan()).getPplOS():"+apps.getTermLoan().getPplOS());
					//// System.out.println("test1:"+apps.getTermLoan().getPplOS());
					termLoanOutDetails.setDouble(3,
							(apps.getTermLoan()).getPplOS());
					//// System.out.println("(apps.getTermLoan()).getPplOsAsOnDate()).getTime())"+apps.getTermLoan().getPplOsAsOnDate().getTime());
					termLoanOutDetails.setDate(
							4,
							new java.sql.Date(((apps.getTermLoan())
									.getPplOsAsOnDate()).getTime()));// final
																		// Date

					termLoanOutDetails.executeQuery();
					int termLoanOutDetailsValue = termLoanOutDetails.getInt(1);
					//// System.out.println("termLoanOutDetailsValue:"+termLoanOutDetailsValue);
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails", "TCWC Details result :"
									+ termLoanOutDetailsValue);

					if (termLoanOutDetailsValue == Constants.FUNCTION_FAILURE) {

						String error = termLoanOutDetails.getString(5);
						//// System.out.println("error:"+error);
						termLoanOutDetails.close();
						termLoanOutDetails = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO",
								"submitTermCreditDetails",
								"Term Credit Dtl Exception" + error);
						throw new DatabaseException(error);
					}
					//// System.out.println("after calling funcInsertTCOutStand");
					termLoanOutDetails.close();
					termLoanOutDetails = null;

					// Inserting DBR Details

					CallableStatement termLoanDBRDetails = connection
							.prepareCall("{?=call funcInsertDBRDtl(?,?,?,?,?,?)}");

					termLoanDBRDetails.registerOutParameter(1, Types.INTEGER);
					termLoanDBRDetails.registerOutParameter(7, Types.VARCHAR);

					termLoanDBRDetails.setString(2, apps.getAppRefNo());
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails",
							"App ref no" + apps.getAppRefNo());

					termLoanDBRDetails.setDouble(3,
							(apps.getTermLoan()).getAmtDisbursed()); // DBR
																		// Amount
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"Amount disb"
									+ (apps.getTermLoan()).getAmtDisbursed());
					termLoanDBRDetails.setDate(
							4,
							new java.sql.Date(((apps.getTermLoan())
									.getFirstDisbursementDate()).getTime())); // First
																				// Date
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitTermCreditDetails",
							"First DBR Date"
									+ new java.sql.Date(((apps.getTermLoan())
											.getFirstDisbursementDate())
											.getTime()));
					if ((apps.getTermLoan()).getFinalDisbursementDate() != null
							&& !((apps.getTermLoan())
									.getFinalDisbursementDate().toString()
									.equals(""))) {
						termLoanDBRDetails
								.setDate(
										5,
										new java.sql.Date(((apps.getTermLoan())
												.getFinalDisbursementDate())
												.getTime())); // final Date
					} else {

						termLoanDBRDetails.setNull(5, java.sql.Types.DATE);
					}
					// Log.log(Log.DEBUG,"ApplicationDAO","submitTermCreditDetails","First DBR Date"
					// + new
					// java.sql.Date(((apps.getTermLoan()).getFinalDisbursementDate()).getTime()));
					termLoanDBRDetails.setString(6, createdBy); // User Id

					termLoanDBRDetails.execute();

					int termLoanDBRDetailsValue = termLoanDBRDetails.getInt(1);
					Log.log(Log.DEBUG, "ApplicationDAO",
							"submitTermCreditDetails", "TCWC Details result :"
									+ termLoanDBRDetailsValue);

					if (termLoanDBRDetailsValue == Constants.FUNCTION_FAILURE) {

						String error = termLoanDBRDetails.getString(7);

						termLoanDBRDetails.close();
						termLoanDBRDetails = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO",
								"submitTermCreditDetails",
								"Term Credit Dtl Exception" + error);
						throw new DatabaseException(error);
					}
					termLoanDBRDetails.close();
					termLoanDBRDetails = null;

				}

			} else if (appLoanType.equals("WC")) {

				/*
				 * double tcPromoterCont=(apps.getProjectOutlayDetails()).
				 * getTcPromoterContribution(); double
				 * tcSubsidy=(apps.getProjectOutlayDetails
				 * ()).getTcSubsidyOrEquity(); double
				 * tcOthers=(apps.getProjectOutlayDetails()).getTcOthers();
				 * double tcAmtSanctioned=(apps.getProjectOutlayDetails()).
				 * getTermCreditSanctioned();
				 */
				// If any of the above fields are not null,then the SP is called
				// if((tcPromoterCont!=0)||(tcSubsidy!=0)||(tcOthers!=0)||(tcAmtSanctioned!=0))
				// {
				CallableStatement termLoanWCDetails = connection
						.prepareCall("{?=call funcInsertTermLoan(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				termLoanWCDetails.registerOutParameter(1, Types.INTEGER);
				termLoanWCDetails.registerOutParameter(19, Types.VARCHAR);

				termLoanWCDetails.setString(2, apps.getAppRefNo()); // app ref
																	// no
				if ((apps.getProjectOutlayDetails()).getTermCreditSanctioned() != 0) {
					termLoanWCDetails.setDouble(3, (apps
							.getProjectOutlayDetails())
							.getTermCreditSanctioned());
				} else {

					termLoanWCDetails.setDouble(3, 0);
				}

				termLoanWCDetails.setNull(4, java.sql.Types.DATE); // Sanctioned
																	// Date

				if (((apps.getProjectOutlayDetails())
						.getTcPromoterContribution()) == 0) {
					termLoanWCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					termLoanWCDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getTcPromoterContribution()); // promoter Cont
				}

				if (((apps.getProjectOutlayDetails()).getTcSubsidyOrEquity()) == 0) {
					termLoanWCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					termLoanWCDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getTcSubsidyOrEquity()); // subsidy
																					// or
																					// equity
				}

				if (((apps.getProjectOutlayDetails()).getTcOthers()) == 0) {
					termLoanWCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					termLoanWCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getTcOthers()); // Others
				}

				termLoanWCDetails.setNull(8, java.sql.Types.DOUBLE); // credit
																		// to be
																		// guraranteed
				termLoanWCDetails.setNull(9, java.sql.Types.INTEGER); // Tenure
				termLoanWCDetails.setNull(10, java.sql.Types.VARCHAR); // Interest
																		// Type-Fixed

				termLoanWCDetails.setNull(11, java.sql.Types.DOUBLE); // Interest
																		// Rate
				termLoanWCDetails.setNull(12, java.sql.Types.DOUBLE); // Bench
																		// Mark
																		// PLR
				termLoanWCDetails.setNull(13, java.sql.Types.DOUBLE); // PLR
				termLoanWCDetails.setNull(14, java.sql.Types.INTEGER); // Moratorium
				termLoanWCDetails.setNull(15, java.sql.Types.DATE); // First
																	// Install
																	// Date
				termLoanWCDetails.setNull(16, java.sql.Types.INTEGER);// No of
																		// Installments
				termLoanWCDetails.setNull(17, java.sql.Types.INTEGER);// Periodicity
				termLoanWCDetails.setNull(18, java.sql.Types.VARCHAR);// PLR
																		// Type

				termLoanWCDetails.executeQuery();

				int termLoanWCDetailsValue = termLoanWCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitTermCreditDetails",
						"TCWC Details result :" + termLoanWCDetailsValue);

				if (termLoanWCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = termLoanWCDetails.getString(19);

					termLoanWCDetails.close();
					termLoanWCDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO",
							"submitTermCreditDetails",
							"Term Credit Dtl Exception" + error);
					throw new DatabaseException(error);
				}
				termLoanWCDetails.close();
				termLoanWCDetails = null;

				// }
			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitTermCreditDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitTermCreditDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "submitTermCreditDetails", "Exited");

	}

	/*
	 * This method submits the working capital Details to the DB
	 */
	public void submitWCDetails(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitWCDetails", "Entered");

		String appLoanType = apps.getLoanType();

		try {

			if ((appLoanType.equals("WC")) || (appLoanType.equals("CC"))
					|| (appLoanType.equals("BO"))) {

				// SP for Working Capital Loan
				CallableStatement wcDetails = connection
						.prepareCall("{?=call funcInsertWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				wcDetails.registerOutParameter(1, Types.INTEGER);
				wcDetails.registerOutParameter(18, Types.VARCHAR);

				wcDetails.setString(2, apps.getAppRefNo()); // app ref no
				if ((apps.getProjectOutlayDetails()).getWcFundBasedSanctioned() == 0) {
					wcDetails.setNull(3, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(3, (apps.getProjectOutlayDetails())
							.getWcFundBasedSanctioned()); // fund based
				}

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					wcDetails.setDouble(4, 0);
				} else {
					wcDetails.setDouble(4, (apps.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					wcDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(5, (apps.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					wcDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(6, (apps.getProjectOutlayDetails())
							.getWcSubsidyOrEquity()); // subsidy/equity support
				}

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					wcDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}

				wcDetails.setDouble(8,
						(apps.getWc()).getLimitFundBasedInterest()); // Interest
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"Interest :"
								+ (apps.getWc()).getLimitFundBasedInterest());

				if ((apps.getWc()).getLimitNonFundBasedCommission() == 0) {
					wcDetails.setDouble(9, 0);
				} else {

					wcDetails.setDouble(9,
							(apps.getWc()).getLimitNonFundBasedCommission()); // Commission
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"Commission :"
								+ (apps.getWc())
										.getLimitNonFundBasedCommission());

				if ((apps.getWc()).getLimitFundBasedSanctionedDate() != null
						&& !((apps.getWc()).getLimitFundBasedSanctionedDate()
								.toString().equals(""))) {
					wcDetails.setDate(
							10,
							new java.sql.Date(((apps.getWc())
									.getLimitFundBasedSanctionedDate())
									.getTime()));// Fund BAsed Sanctioned Date
				} else {

					wcDetails.setNull(10, java.sql.Types.DATE);
				}

				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"Fund Based Sanctioned Date :"
								+ (apps.getWc())
										.getLimitFundBasedSanctionedDate());

				if ((apps.getWc()).getLimitNonFundBasedSanctionedDate() != null
						&& !((apps.getWc())
								.getLimitNonFundBasedSanctionedDate()
								.toString().equals(""))) {
					wcDetails.setDate(
							11,
							new java.sql.Date(((apps.getWc())
									.getLimitNonFundBasedSanctionedDate())
									.getTime())); // Non Fund Based Sanctioned
													// Date
				} else {

					wcDetails.setNull(11, java.sql.Types.DATE);
				}

				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"Non Fund Based Sanctioned Date : "
								+ (apps.getWc())
										.getLimitNonFundBasedSanctionedDate());

				if (((apps.getWc()).getCreditFundBased()) == 0) {
					wcDetails.setNull(12, java.sql.Types.DOUBLE);
				} else {
					wcDetails
							.setDouble(12, (apps.getWc()).getCreditFundBased()); // Credit
																					// guranteed-Fund
																					// Based
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"Credit Fund Based :"
								+ (apps.getWc()).getCreditFundBased());

				if (((apps.getWc()).getCreditNonFundBased()) == 0) {
					wcDetails.setDouble(13, 0);
				} else {
					wcDetails.setDouble(13,
							(apps.getWc()).getCreditNonFundBased()); // Non Fund
																		// Based
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetails",
						"NonFundBased :"
								+ (apps.getWc()).getCreditNonFundBased());

				if (((apps.getWc()).getWcPlr()) == 0) {
					wcDetails.setNull(14, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(14, (apps.getWc()).getWcPlr()); // PLR
				}
				Log.log(Log.INFO, "ApplicationDAO", "submitWCDetails", "PLR :"
						+ (apps.getWc()).getWcPlr());

				if ((apps.getWc()).getWcTypeOfPLR() != null
						&& !(((apps.getWc()).getWcTypeOfPLR()).equals(""))) {
					wcDetails.setString(15, (apps.getWc()).getWcTypeOfPLR()); // PLR
																				// Type
				} else {

					wcDetails.setNull(15, java.sql.Types.VARCHAR); // PLR Type
				}

				if ((apps.getWc()).getWcInterestType() != null
						&& !(((apps.getWc()).getWcInterestType()).equals(""))) {
					wcDetails.setString(16, (apps.getWc()).getWcInterestType()); // PLR
																					// Type

				} else {

					wcDetails.setNull(16, java.sql.Types.VARCHAR);
				}

				if ((apps.getWc()).getWcTenure() != 0) {
					wcDetails.setInt(17, (apps.getWc()).getWcTenure()); // Tenure

				} else {

					wcDetails.setNull(17, java.sql.Types.INTEGER);
				}

				wcDetails.executeQuery();
				int wcDetailsValue = wcDetails.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetails",
						"WC Details result :" + wcDetailsValue);

				if (wcDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = wcDetails.getString(18);

					wcDetails.close();
					wcDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitWCDetails",
							"Working Credit Dtl Exception" + error);
					throw new DatabaseException(error);
				}
				wcDetails.close();
				wcDetails = null;

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetails",
						"Fund Based Principal :"
								+ (apps.getWc()).getOsFundBasedPpl());
				if (((apps.getWc()).getOsFundBasedPpl()) != 0) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetails",
							"Fund Based Principal is not zero");
					// Inserting Outstanding Details
					CallableStatement wcOutDetails = connection
							.prepareCall("{?=call funcInsertWCOutStand(?,?,?,?,?,?,?,?)}");
					wcOutDetails.registerOutParameter(1, Types.INTEGER);
					wcOutDetails.registerOutParameter(9, Types.VARCHAR);

					wcOutDetails.setString(2, apps.getAppRefNo()); // app ref no

					if (((apps.getWc()).getOsFundBasedPpl()) == 0) {
						wcOutDetails.setNull(3, java.sql.Types.DOUBLE);
					} else {
						wcOutDetails.setDouble(3,
								(apps.getWc()).getOsFundBasedPpl()); // Fund
																		// Based-Principal
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"Fund Based Principal :"
									+ (apps.getWc()).getOsFundBasedPpl());

					if ((apps.getWc()).getOsFundBasedInterestAmt() == 0) {
						wcOutDetails.setDouble(4, 0);
					} else {

						wcOutDetails.setDouble(4,
								(apps.getWc()).getOsFundBasedInterestAmt()); // Fund
																				// Based-Interest
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"Interest Fund Based :"
									+ (apps.getWc())
											.getOsFundBasedInterestAmt());

					if ((apps.getWc()).getOsFundBasedAsOnDate() != null
							&& !((apps.getWc()).getOsFundBasedAsOnDate()
									.toString().equals(""))) {
						wcOutDetails.setDate(
								5,
								new java.sql.Date(((apps.getWc())
										.getOsFundBasedAsOnDate()).getTime())); // Sanctioned
																				// Date
					} else {

						wcOutDetails.setDate(5, null);
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"Sanctioned Date :"
									+ (apps.getWc()).getOsFundBasedAsOnDate());

					if (((apps.getWc()).getOsNonFundBasedPpl()) == 0) {
						wcOutDetails.setDouble(6, 0);
					} else {
						wcOutDetails.setDouble(6,
								(apps.getWc()).getOsNonFundBasedPpl()); // Non
																		// Fund
																		// Based-Ppl
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"NonFundBased Principal :"
									+ (apps.getWc()).getOsNonFundBasedPpl());

					if ((apps.getWc()).getOsNonFundBasedCommissionAmt() == 0) {
						wcOutDetails.setDouble(7, 0);
					} else {

						wcOutDetails
								.setDouble(7, (apps.getWc())
										.getOsNonFundBasedCommissionAmt()); // Commission
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"NonFundBasedCommission :"
									+ (apps.getWc())
											.getOsNonFundBasedCommissionAmt());

					if ((apps.getWc()).getOsNonFundBasedAsOnDate() != null
							&& !((apps.getWc()).getOsNonFundBasedAsOnDate()
									.toString().equals(""))) {
						wcOutDetails
								.setDate(
										8,
										new java.sql.Date(((apps.getWc())
												.getOsNonFundBasedAsOnDate())
												.getTime()));// Non Fund Based
																// Sanctioned
																// Date
					} else {

						wcOutDetails.setDate(8, null);
					}

					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetails",
							"NonFundBased Sanctioned Date :"
									+ (apps.getWc())
											.getOsNonFundBasedAsOnDate());

					wcOutDetails.executeQuery();
					int wcOutDetailsValue = wcOutDetails.getInt(1);

					Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetails",
							"WC Details result :" + wcOutDetailsValue);

					if (wcOutDetailsValue == Constants.FUNCTION_FAILURE) {

						String error = wcOutDetails.getString(9);

						wcOutDetails.close();
						wcOutDetails = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO", "submitWCDetails",
								"Working Credit Dtl Exception" + error);
						throw new DatabaseException(error);
					}
					wcOutDetails.close();
					wcOutDetails = null;

				}
				/*
				 * added by sukumar@path for MLIs to feed disbursement details
				 * for working capital application 10/09/2009
				 */
				if ((appLoanType.equals("WC")) || (appLoanType.equals("BO"))) {
					CallableStatement wcDBRDetails = connection
							.prepareCall("{?=call funcInsertDBRDtl(?,?,?,?,?,?)}");

					wcDBRDetails.registerOutParameter(1, Types.INTEGER);
					wcDBRDetails.registerOutParameter(7, Types.VARCHAR);

					wcDBRDetails.setString(2, apps.getAppRefNo());
					wcDBRDetails.setDouble(3,
							(apps.getTermLoan()).getAmtDisbursed()); // DBR
																		// Amount

					// wcDBRDetails.setDate(4,new
					// java.sql.Date(((apps.getTermLoan()).getFirstDisbursementDate()).getTime()));
					// //First Date
					if ((apps.getTermLoan()).getFirstDisbursementDate() != null
							&& !((apps.getTermLoan())
									.getFirstDisbursementDate().toString()
									.equals(""))) {
						wcDBRDetails
								.setDate(
										4,
										new java.sql.Date(((apps.getTermLoan())
												.getFirstDisbursementDate())
												.getTime())); // First Date
					} else {

						wcDBRDetails.setNull(4, java.sql.Types.DATE);
					}

					if ((apps.getTermLoan()).getFinalDisbursementDate() != null
							&& !((apps.getTermLoan())
									.getFinalDisbursementDate().toString()
									.equals(""))) {
						wcDBRDetails
								.setDate(
										5,
										new java.sql.Date(((apps.getTermLoan())
												.getFinalDisbursementDate())
												.getTime())); // final Date
					} else {

						wcDBRDetails.setNull(5, java.sql.Types.DATE);
					}
					// Log.log(Log.DEBUG,"ApplicationDAO","submitTermCreditDetails","First DBR Date"
					// + new
					// java.sql.Date(((apps.getTermLoan()).getFinalDisbursementDate()).getTime()));
					wcDBRDetails.setString(6, createdBy); // User Id

					wcDBRDetails.execute();

					int wcDBRDetailsValue = wcDBRDetails.getInt(1);
					if (wcDBRDetailsValue == Constants.FUNCTION_FAILURE) {

						String error = wcDBRDetails.getString(7);

						wcDBRDetails.close();
						wcDBRDetails = null;
						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO",
								"submitTermCreditDetails",
								"Term Credit Dtl Exception" + error);
						throw new DatabaseException(error);
					}
					wcDBRDetails.close();
					wcDBRDetails = null;

					/* end part here */

				}
			} else if (appLoanType.equals("TC")) {

				/*
				 * double wcFBSanctioned=(apps.getProjectOutlayDetails()).
				 * getWcFundBasedSanctioned(); double
				 * wcNFBSanctioned=(apps.getProjectOutlayDetails
				 * ()).getWcNonFundBasedSanctioned(); double
				 * wcPromoterCont=(apps
				 * .getProjectOutlayDetails()).getWcPromoterContribution();
				 * double
				 * wcSubsidy=(apps.getProjectOutlayDetails()).getWcSubsidyOrEquity
				 * (); double
				 * wcOthers=(apps.getProjectOutlayDetails()).getWcOthers();
				 */
				// if((wcPromoterCont!=0)||(wcSubsidy!=0)||(wcOthers!=0)||(wcFBSanctioned!=0)||(wcNFBSanctioned!=0))
				// {
				CallableStatement wcTCDetails = connection
						.prepareCall("{?=call funcInsertWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				wcTCDetails.registerOutParameter(1, Types.INTEGER);
				wcTCDetails.registerOutParameter(18, Types.VARCHAR);

				wcTCDetails.setString(2, apps.getAppRefNo()); // app ref no
				if ((apps.getProjectOutlayDetails()).getWcFundBasedSanctioned() != 0) {
					wcTCDetails.setDouble(3, (apps.getProjectOutlayDetails())
							.getWcFundBasedSanctioned()); // fund based

				} else {

					wcTCDetails.setDouble(3, 0); // fund based
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetails",
						"Fund BAsed Sanctioned :"
								+ (apps.getProjectOutlayDetails())
										.getWcFundBasedSanctioned());

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					wcTCDetails.setDouble(4, 0);

				} else {

					wcTCDetails.setDouble(4, (apps.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetails",
						"Fund Non BAsed Sanctioned :"
								+ (apps.getProjectOutlayDetails())
										.getWcNonFundBasedSanctioned());

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					wcTCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(5, (apps.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					wcTCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(6, (apps.getProjectOutlayDetails())
							.getWcSubsidyOrEquity()); // subsidy/equity support
				}

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					wcTCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}

				wcTCDetails.setNull(8, java.sql.Types.DOUBLE);
				wcTCDetails.setDouble(9, 0);
				wcTCDetails.setNull(10, java.sql.Types.DATE);
				wcTCDetails.setNull(11, java.sql.Types.DATE);
				wcTCDetails.setNull(12, java.sql.Types.DOUBLE);
				wcTCDetails.setDouble(13, 0);
				wcTCDetails.setNull(14, java.sql.Types.DOUBLE);
				wcTCDetails.setNull(15, java.sql.Types.VARCHAR);
				wcTCDetails.setString(16, "T");
				wcTCDetails.setNull(17, java.sql.Types.INTEGER);

				wcTCDetails.execute();

				int wcTCDetailsValue = wcTCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetails",
						"TCWC Details result :" + wcTCDetailsValue);

				if (wcTCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = wcTCDetails.getString(18);

					wcTCDetails.close();
					wcTCDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitWCDetails",
							"WCTC Dtl Exception" + error);
					throw new DatabaseException(error);
				}
				wcTCDetails.close();
				wcTCDetails = null;

				// }
			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitWCDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitWCDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "submitWCDetails", "Exited");

	}

	/**
	 * added by sukumar@ path for submitting the renewal of working capital
	 * application on 23-09-2009
	 * 
	 * @param apps
	 * @param createdBy
	 * @param connection
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void submitWCDetailsNew(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitWCDetailsNew", "Entered");

		String appLoanType = apps.getLoanType();

		try {

			if ((appLoanType.equals("WC")) || (appLoanType.equals("CC"))
					|| (appLoanType.equals("BO"))) {

				// SP for Working Capital Loan
				CallableStatement wcDetails = connection
						.prepareCall("{?=call funcInsertWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				wcDetails.registerOutParameter(1, Types.INTEGER);
				wcDetails.registerOutParameter(18, Types.VARCHAR);

				wcDetails.setString(2, apps.getAppRefNo()); // app ref no
				if ((apps.getProjectOutlayDetails()).getWcFundBasedSanctioned() == 0) {
					wcDetails.setNull(3, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(3, (apps.getProjectOutlayDetails())
							.getWcFundBasedSanctioned()); // fund based
				}

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					wcDetails.setDouble(4, 0);
				} else {
					wcDetails.setDouble(4, (apps.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					wcDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(5, (apps.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					wcDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(6, (apps.getProjectOutlayDetails())
							.getWcSubsidyOrEquity()); // subsidy/equity support
				}

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					wcDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}

				wcDetails.setDouble(8,
						(apps.getWc()).getLimitFundBasedInterest()); // Interest
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Interest :"
								+ (apps.getWc()).getLimitFundBasedInterest());

				if ((apps.getWc()).getLimitNonFundBasedCommission() == 0) {
					wcDetails.setDouble(9, 0);
				} else {

					wcDetails.setDouble(9,
							(apps.getWc()).getLimitNonFundBasedCommission()); // Commission
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Commission :"
								+ (apps.getWc())
										.getLimitNonFundBasedCommission());

				if ((apps.getWc()).getLimitFundBasedSanctionedDate() != null
						&& !((apps.getWc()).getLimitFundBasedSanctionedDate()
								.toString().equals(""))) {
					wcDetails.setDate(
							10,
							new java.sql.Date(((apps.getWc())
									.getLimitFundBasedSanctionedDate())
									.getTime()));// Fund BAsed Sanctioned Date
				} else {

					wcDetails.setNull(10, java.sql.Types.DATE);
				}

				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Fund Based Sanctioned Date :"
								+ (apps.getWc())
										.getLimitFundBasedSanctionedDate());

				if ((apps.getWc()).getLimitNonFundBasedSanctionedDate() != null
						&& !((apps.getWc())
								.getLimitNonFundBasedSanctionedDate()
								.toString().equals(""))) {
					wcDetails.setDate(
							11,
							new java.sql.Date(((apps.getWc())
									.getLimitNonFundBasedSanctionedDate())
									.getTime())); // Non Fund Based Sanctioned
													// Date
				} else {

					wcDetails.setNull(11, java.sql.Types.DATE);
				}

				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Non Fund Based Sanctioned Date : "
								+ (apps.getWc())
										.getLimitNonFundBasedSanctionedDate());

				if (((apps.getWc()).getCreditFundBased()) == 0) {
					wcDetails.setNull(12, java.sql.Types.DOUBLE);
				} else {
					wcDetails
							.setDouble(12, (apps.getWc()).getCreditFundBased()); // Credit
																					// guranteed-Fund
																					// Based
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Credit Fund Based :"
								+ (apps.getWc()).getCreditFundBased());

				if (((apps.getWc()).getCreditNonFundBased()) == 0) {
					wcDetails.setDouble(13, 0);
				} else {
					wcDetails.setDouble(13,
							(apps.getWc()).getCreditNonFundBased()); // Non Fund
																		// Based
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"NonFundBased :"
								+ (apps.getWc()).getCreditNonFundBased());

				if (((apps.getWc()).getWcPlr()) == 0) {
					wcDetails.setNull(14, java.sql.Types.DOUBLE);
				} else {
					wcDetails.setDouble(14, (apps.getWc()).getWcPlr()); // PLR
				}
				Log.log(Log.INFO, "ApplicationDAO", "submitWCDetailsNew",
						"PLR :" + (apps.getWc()).getWcPlr());

				if ((apps.getWc()).getWcTypeOfPLR() != null
						&& !(((apps.getWc()).getWcTypeOfPLR()).equals(""))) {
					wcDetails.setString(15, (apps.getWc()).getWcTypeOfPLR()); // PLR
																				// Type
				} else {

					wcDetails.setNull(15, java.sql.Types.VARCHAR); // PLR Type
				}

				if ((apps.getWc()).getWcInterestType() != null
						&& !(((apps.getWc()).getWcInterestType()).equals(""))) {
					wcDetails.setString(16, (apps.getWc()).getWcInterestType()); // PLR
																					// Type

				} else {

					wcDetails.setNull(16, java.sql.Types.VARCHAR);
				}

				if ((apps.getWc()).getWcTenure() != 0) {
					wcDetails.setInt(17, (apps.getWc()).getWcTenure()); // Tenure

				} else {

					wcDetails.setNull(17, java.sql.Types.INTEGER);
				}

				wcDetails.executeQuery();
				int wcDetailsValue = wcDetails.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetailsNew",
						"WC Details result :" + wcDetailsValue);

				if (wcDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = wcDetails.getString(18);

					wcDetails.close();
					wcDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitWCDetailsNew",
							"Working Credit Dtl Exception" + error);
					throw new DatabaseException(error);
				}
				wcDetails.close();
				wcDetails = null;

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Fund Based Principal :"
								+ (apps.getWc()).getOsFundBasedPpl());
				if (((apps.getWc()).getOsFundBasedPpl()) != 0) {
					Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetailsNew",
							"Fund Based Principal is not zero");
					// Inserting Outstanding Details
					CallableStatement wcOutDetails = connection
							.prepareCall("{?=call funcInsertWCOutStand(?,?,?,?,?,?,?,?)}");
					wcOutDetails.registerOutParameter(1, Types.INTEGER);
					wcOutDetails.registerOutParameter(9, Types.VARCHAR);

					wcOutDetails.setString(2, apps.getAppRefNo()); // app ref no

					if (((apps.getWc()).getOsFundBasedPpl()) == 0) {
						wcOutDetails.setNull(3, java.sql.Types.DOUBLE);
					} else {
						wcOutDetails.setDouble(3,
								(apps.getWc()).getOsFundBasedPpl()); // Fund
																		// Based-Principal
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"Fund Based Principal :"
									+ (apps.getWc()).getOsFundBasedPpl());

					if ((apps.getWc()).getOsFundBasedInterestAmt() == 0) {
						wcOutDetails.setDouble(4, 0);
					} else {

						wcOutDetails.setDouble(4,
								(apps.getWc()).getOsFundBasedInterestAmt()); // Fund
																				// Based-Interest
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"Interest Fund Based :"
									+ (apps.getWc())
											.getOsFundBasedInterestAmt());

					if ((apps.getWc()).getOsFundBasedAsOnDate() != null
							&& !((apps.getWc()).getOsFundBasedAsOnDate()
									.toString().equals(""))) {
						wcOutDetails.setDate(
								5,
								new java.sql.Date(((apps.getWc())
										.getOsFundBasedAsOnDate()).getTime())); // Sanctioned
																				// Date
					} else {

						wcOutDetails.setDate(5, null);
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"Sanctioned Date :"
									+ (apps.getWc()).getOsFundBasedAsOnDate());

					if (((apps.getWc()).getOsNonFundBasedPpl()) == 0) {
						wcOutDetails.setDouble(6, 0);
					} else {
						wcOutDetails.setDouble(6,
								(apps.getWc()).getOsNonFundBasedPpl()); // Non
																		// Fund
																		// Based-Ppl
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"NonFundBased Principal :"
									+ (apps.getWc()).getOsNonFundBasedPpl());

					if ((apps.getWc()).getOsNonFundBasedCommissionAmt() == 0) {
						wcOutDetails.setDouble(7, 0);
					} else {

						wcOutDetails
								.setDouble(7, (apps.getWc())
										.getOsNonFundBasedCommissionAmt()); // Commission
					}
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"NonFundBasedCommission :"
									+ (apps.getWc())
											.getOsNonFundBasedCommissionAmt());

					if ((apps.getWc()).getOsNonFundBasedAsOnDate() != null
							&& !((apps.getWc()).getOsNonFundBasedAsOnDate()
									.toString().equals(""))) {
						wcOutDetails
								.setDate(
										8,
										new java.sql.Date(((apps.getWc())
												.getOsNonFundBasedAsOnDate())
												.getTime()));// Non Fund Based
																// Sanctioned
																// Date
					} else {

						wcOutDetails.setDate(8, null);
					}

					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"submitWCDetailsNew",
							"NonFundBased Sanctioned Date :"
									+ (apps.getWc())
											.getOsNonFundBasedAsOnDate());

					wcOutDetails.executeQuery();
					int wcOutDetailsValue = wcOutDetails.getInt(1);

					Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetailsNew",
							"WC Details result :" + wcOutDetailsValue);

					if (wcOutDetailsValue == Constants.FUNCTION_FAILURE) {

						String error = wcOutDetails.getString(9);

						wcOutDetails.close();
						wcOutDetails = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO",
								"submitWCDetailsNew",
								"Working Credit Dtl Exception" + error);
						throw new DatabaseException(error);
					}
					wcOutDetails.close();
					wcOutDetails = null;

				}
			} else if (appLoanType.equals("TC")) {

				/*
				 * double wcFBSanctioned=(apps.getProjectOutlayDetails()).
				 * getWcFundBasedSanctioned(); double
				 * wcNFBSanctioned=(apps.getProjectOutlayDetails
				 * ()).getWcNonFundBasedSanctioned(); double
				 * wcPromoterCont=(apps
				 * .getProjectOutlayDetails()).getWcPromoterContribution();
				 * double
				 * wcSubsidy=(apps.getProjectOutlayDetails()).getWcSubsidyOrEquity
				 * (); double
				 * wcOthers=(apps.getProjectOutlayDetails()).getWcOthers();
				 */
				// if((wcPromoterCont!=0)||(wcSubsidy!=0)||(wcOthers!=0)||(wcFBSanctioned!=0)||(wcNFBSanctioned!=0))
				// {
				CallableStatement wcTCDetails = connection
						.prepareCall("{?=call funcInsertWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				wcTCDetails.registerOutParameter(1, Types.INTEGER);
				wcTCDetails.registerOutParameter(18, Types.VARCHAR);

				wcTCDetails.setString(2, apps.getAppRefNo()); // app ref no
				if ((apps.getProjectOutlayDetails()).getWcFundBasedSanctioned() != 0) {
					wcTCDetails.setDouble(3, (apps.getProjectOutlayDetails())
							.getWcFundBasedSanctioned()); // fund based

				} else {

					wcTCDetails.setDouble(3, 0); // fund based
				}

				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Fund BAsed Sanctioned :"
								+ (apps.getProjectOutlayDetails())
										.getWcFundBasedSanctioned());

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					wcTCDetails.setDouble(4, 0);

				} else {

					wcTCDetails.setDouble(4, (apps.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"submitWCDetailsNew",
						"Fund Non BAsed Sanctioned :"
								+ (apps.getProjectOutlayDetails())
										.getWcNonFundBasedSanctioned());

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					wcTCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(5, (apps.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					wcTCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(6, (apps.getProjectOutlayDetails())
							.getWcSubsidyOrEquity()); // subsidy/equity support
				}

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					wcTCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					wcTCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}

				wcTCDetails.setNull(8, java.sql.Types.DOUBLE);
				wcTCDetails.setDouble(9, 0);
				wcTCDetails.setNull(10, java.sql.Types.DATE);
				wcTCDetails.setNull(11, java.sql.Types.DATE);
				wcTCDetails.setNull(12, java.sql.Types.DOUBLE);
				wcTCDetails.setDouble(13, 0);
				wcTCDetails.setNull(14, java.sql.Types.DOUBLE);
				wcTCDetails.setNull(15, java.sql.Types.VARCHAR);
				wcTCDetails.setString(16, "T");
				wcTCDetails.setNull(17, java.sql.Types.INTEGER);

				wcTCDetails.execute();

				int wcTCDetailsValue = wcTCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "submitWCDetailsNew",
						"TCWC Details result :" + wcTCDetailsValue);

				if (wcTCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = wcTCDetails.getString(18);

					wcTCDetails.close();
					wcTCDetails = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "submitWCDetailsNew",
							"WCTC Dtl Exception" + error);
					throw new DatabaseException(error);
				}
				wcTCDetails.close();
				wcTCDetails = null;

				// }
			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitWCDetailsNew",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitWCDetailsNew",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "submitWCDetailsNew", "Exited");

	}

	/* end part here for submission of renewal of working capital application */
	/**
	 * This method submits the Securitization Details to the Database
	 */
	public void submitSecDetails(Application app, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails", "Entered");

		try {
			CallableStatement secDetails = connection
					.prepareCall("{?=call funcInsertSecDetails(?,?,?,?,?,?,?,?,?,?)}");

			secDetails.registerOutParameter(1, Types.INTEGER);
			secDetails.registerOutParameter(11, Types.VARCHAR);

			secDetails.setString(2, app.getAppRefNo());

			if (app.getSecuritization().getSpreadOverPLR() == 0) {
				secDetails.setNull(3, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(3, app.getSecuritization()
						.getSpreadOverPLR());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Spread over PLR :"
								+ app.getSecuritization().getSpreadOverPLR());

			}

			if (app.getSecuritization().getPplRepaymentInEqual() == null
					|| (app.getSecuritization().getPplRepaymentInEqual())
							.equals("")) {
				secDetails.setNull(4, java.sql.Types.VARCHAR);
			} else {
				secDetails.setString(4, app.getSecuritization()
						.getPplRepaymentInEqual());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Repayment in equal inst :"
								+ app.getSecuritization()
										.getPplRepaymentInEqual());
			}

			if (app.getSecuritization().getTangibleNetWorth() == 0) {
				secDetails.setNull(5, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(5, app.getSecuritization()
						.getTangibleNetWorth());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"tangible worth:"
								+ app.getSecuritization().getTangibleNetWorth());
			}

			if (app.getSecuritization().getFixedACR() == 0) {
				secDetails.setNull(6, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(6, app.getSecuritization().getFixedACR());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"fixed ACR:" + app.getSecuritization().getFixedACR());
			}

			if (app.getSecuritization().getCurrentRatio() == 0) {
				secDetails.setNull(7, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(7, app.getSecuritization()
						.getCurrentRatio());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Current Ratio:"
								+ app.getSecuritization().getCurrentRatio());
			}

			if (app.getSecuritization().getMinimumDSCR() == 0) {
				secDetails.setNull(8, java.sql.Types.VARCHAR);
			} else {
				secDetails.setDouble(8, app.getSecuritization()
						.getMinimumDSCR());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Min dscr:" + app.getSecuritization().getMinimumDSCR());
			}

			if (app.getSecuritization().getAvgDSCR() == 0) {
				secDetails.setNull(9, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(9, app.getSecuritization().getAvgDSCR());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Avg dscr:" + app.getSecuritization().getAvgDSCR());
			}

			if (app.getSecuritization().getFinancialPartUnit() == 0) {
				secDetails.setNull(10, java.sql.Types.DOUBLE);
			} else {
				secDetails.setDouble(10, app.getSecuritization()
						.getFinancialPartUnit());
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						"Financial part of a unit:"
								+ app.getSecuritization()
										.getFinancialPartUnit());
			}

			secDetails.execute();
			int secDetailsValue = secDetails.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
					"secDetailsValue:" + secDetailsValue);

			if (secDetailsValue == Constants.FUNCTION_FAILURE) {

				String error = secDetails.getString(11);

				secDetails.close();
				secDetails = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "submitSecDetails", error);
				throw new DatabaseException(error);
			}
			secDetails.close();
			secDetails = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitSecDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	/**
	 * 
	 * @param cgpan
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void rejectApplication(String cgpan, String remarks, String userId)
			throws DatabaseException {

		Connection connection = null;
		CallableStatement stmt = null;
		//// System.out.println("rejectApplication entered");
		try {
			connection = DBConnection.getConnection();
			//// System.out.println("cgpan:"+cgpan);
			stmt = connection.prepareCall("{?=call FuncRejectAppl(?,?,?,?)}");
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);

			stmt.setString(2, cgpan);
			stmt.setString(3, remarks);
			stmt.setString(4, userId);
			stmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			stmt.execute();

			int status = stmt.getInt(1);
			//// System.out.println("status:"+status);
			if (status == Constants.FUNCTION_FAILURE) {
				String err = stmt.getString(5);
				//// System.out.println("error:"+err);
				stmt.close();
				stmt = null;
				throw new DatabaseException(err);
			}
			stmt.close();
			stmt = null;

			Log.log(Log.INFO, "ApplicationDAO", "rejectApplication",
					"Application Status Changed Successfully");
		} catch (SQLException exp) {
			//// System.out.println("sql exception " +exp.getMessage());
			Log.log(Log.INFO, "ApplicationDAO", "rejectApplication",
					"sql exception " + exp.getMessage());

			throw new DatabaseException(exp.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
	}

	/**
	 * Either of the argument can be null.but not both. If both are present the
	 * cgbid to the cgpan would be displayed else error would be thrown.
	 * 
	 * @param id
	 * @param type
	 *            - 0-CGBID 1-CGPAN
	 * 
	 * @return com.cgtsi.application.Borrower
	 * @throws DatabaseException
	 * @roseuid 3972E7240001
	 */
	public BorrowerDetails fetchBorrowerDtls(String cgbid, String cgpan)
			throws DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls", "Entered");

		Connection connection = DBConnection.getConnection(false);

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		//// System.out.println("CGPAN:"+cgpan);
		//// System.out.println("CGBID:"+cgbid);
		try {
			if (cgpan.equals("")) {
				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
						"Entered");

				CallableStatement ssiDtlForCgbid = connection.prepareCall
				// ("{?=call funcGetSSIDetailforBorId(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

						("{?=call funcGetSSIDetailforBorId(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
						"CGBID" + cgbid);

				ssiDtlForCgbid.setString(2, cgbid); // CGBID

				ssiDtlForCgbid.registerOutParameter(1, Types.INTEGER);
				ssiDtlForCgbid.registerOutParameter(33, Types.VARCHAR);

				ssiDtlForCgbid.registerOutParameter(3, Types.INTEGER); // SSI
																		// Ref
																		// No
				ssiDtlForCgbid.registerOutParameter(4, Types.VARCHAR); // Already
																		// Covered
																		// by
																		// CGTSI
				ssiDtlForCgbid.registerOutParameter(5, Types.VARCHAR); // Already
																		// assisted
																		// by
																		// bank
				ssiDtlForCgbid.registerOutParameter(6, Types.VARCHAR); // SSI
																		// Unique
																		// No
				ssiDtlForCgbid.registerOutParameter(7, Types.VARCHAR); // NPA
				ssiDtlForCgbid.registerOutParameter(8, Types.VARCHAR); // Constitution
				ssiDtlForCgbid.registerOutParameter(9, Types.VARCHAR); // SSI
																		// Type
				ssiDtlForCgbid.registerOutParameter(10, Types.VARCHAR); // unit
																		// Name
				ssiDtlForCgbid.registerOutParameter(11, Types.VARCHAR); // SSI
																		// reg
																		// no
				ssiDtlForCgbid.registerOutParameter(12, Types.DATE); // Commencement
																		// Date
				ssiDtlForCgbid.registerOutParameter(13, Types.VARCHAR); // SSI
																		// ITPAN
				ssiDtlForCgbid.registerOutParameter(14, Types.VARCHAR); // type
																		// of
																		// Activity
				ssiDtlForCgbid.registerOutParameter(15, Types.INTEGER); // No Of
																		// Employees
				ssiDtlForCgbid.registerOutParameter(16, Types.DOUBLE); // project
																		// Sales
				ssiDtlForCgbid.registerOutParameter(17, Types.DOUBLE); // Project
																		// Exports
				ssiDtlForCgbid.registerOutParameter(18, Types.VARCHAR); // SSI
																		// Address
				ssiDtlForCgbid.registerOutParameter(19, Types.VARCHAR); // SSi
																		// City
				ssiDtlForCgbid.registerOutParameter(20, Types.VARCHAR); // Pincode
				ssiDtlForCgbid.registerOutParameter(21, Types.VARCHAR); // Display
																		// Defaulter
				ssiDtlForCgbid.registerOutParameter(22, Types.VARCHAR); // SSI
																		// District
				ssiDtlForCgbid.registerOutParameter(23, Types.VARCHAR); // SSI
																		// State
				ssiDtlForCgbid.registerOutParameter(24, Types.VARCHAR); // Industry
																		// Nature
				ssiDtlForCgbid.registerOutParameter(25, Types.VARCHAR); // Indutry
																		// Sector
																		// name
				ssiDtlForCgbid.registerOutParameter(26, Types.VARCHAR); // Status
				ssiDtlForCgbid.registerOutParameter(27, Types.VARCHAR); // BID
				ssiDtlForCgbid.registerOutParameter(28, Types.DOUBLE); // Outstanding
																		// amount
				ssiDtlForCgbid.registerOutParameter(29, Types.VARCHAR); // MCGS
																		// Flag

				/*
				 * ssiDtlForCgbid.executeQuery(); int
				 * ssiDtlForCgbidValue=ssiDtlForCgbid.getInt(1);
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
				 * "SSI Details for CGBID :" + ssiDtlForCgbidValue);
				 * 
				 * if(ssiDtlForCgbidValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = ssiDtlForCgbid.getString(30); //
				 * System.out.println("Error:"+error); ssiDtlForCgbid.close();
				 * ssiDtlForCgbid=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
				 * "SSI Exception message:" + error);
				 * 
				 * throw new DatabaseException(error);
				 * 
				 * } else {
				 * ssiDetails.setBorrowerRefNo(ssiDtlForCgbid.getInt(3));
				 * borrowerDetails
				 * .setPreviouslyCovered(ssiDtlForCgbid.getString(4).trim());
				 * borrowerDetails
				 * .setAssistedByBank(ssiDtlForCgbid.getString(5).trim());
				 * borrowerDetails.setNpa(ssiDtlForCgbid.getString(7).trim());
				 * ssiDetails.setConstitution(ssiDtlForCgbid.getString(8));
				 * ssiDetails.setSsiType(ssiDtlForCgbid.getString(9).trim());
				 * ssiDetails.setSsiName(ssiDtlForCgbid.getString(10));
				 * ssiDetails.setRegNo(ssiDtlForCgbid.getString(11));
				 * ssiDetails.setSsiITPan(ssiDtlForCgbid.getString(13));
				 * ssiDetails.setActivityType(ssiDtlForCgbid.getString(14));
				 * ssiDetails.setEmployeeNos(ssiDtlForCgbid.getInt(15));
				 * ssiDetails
				 * .setProjectedSalesTurnover(ssiDtlForCgbid.getDouble(16));
				 * ssiDetails.setProjectedExports(ssiDtlForCgbid.getDouble(17));
				 * ssiDetails.setAddress(ssiDtlForCgbid.getString(18));
				 * ssiDetails.setCity(ssiDtlForCgbid.getString(19));
				 * ssiDetails.setPincode(ssiDtlForCgbid.getString(20).trim());
				 * ssiDetails.setDistrict(ssiDtlForCgbid.getString(22));
				 * ssiDetails.setState(ssiDtlForCgbid.getString(23));
				 * ssiDetails.setIndustryNature(ssiDtlForCgbid.getString(24));
				 * ssiDetails.setIndustrySector(ssiDtlForCgbid.getString(25));
				 * ssiDetails.setCgbid(ssiDtlForCgbid.getString(27));
				 * borrowerDetails.setOsAmt(ssiDtlForCgbid.getDouble(28));
				 */

				ssiDtlForCgbid.registerOutParameter(30, Types.VARCHAR); //
				ssiDtlForCgbid.registerOutParameter(31, Types.VARCHAR); //
				ssiDtlForCgbid.registerOutParameter(32, Types.VARCHAR); //
				// ssiDtlForCgbid.registerOutParameter(33,Types.VARCHAR);

				ssiDtlForCgbid.executeQuery();
				int ssiObjValue = ssiDtlForCgbid.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"SSI Details :" + ssiObjValue);

				if (ssiObjValue == Constants.FUNCTION_FAILURE) {

					String error = ssiDtlForCgbid.getString(33);

					// String error = ssiDtlForCgbid.getString(33);

					ssiDtlForCgbid.close();
					ssiDtlForCgbid = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ssiDetails.setBorrowerRefNo(ssiDtlForCgbid.getInt(3));
					borrowerDetails.setPreviouslyCovered(ssiDtlForCgbid
							.getString(4).trim());
					borrowerDetails.setAssistedByBank(ssiDtlForCgbid.getString(
							5).trim());
					borrowerDetails.setNpa(ssiDtlForCgbid.getString(7).trim());
					ssiDetails.setConstitution(ssiDtlForCgbid.getString(8));
					ssiDetails.setSsiType(ssiDtlForCgbid.getString(9).trim());
					ssiDetails.setSsiName(ssiDtlForCgbid.getString(10));
					ssiDetails.setRegNo(ssiDtlForCgbid.getString(11));
					ssiDetails.setSsiITPan(ssiDtlForCgbid.getString(13));
					ssiDetails.setActivityType(ssiDtlForCgbid.getString(14));
					ssiDetails.setEmployeeNos(ssiDtlForCgbid.getInt(15));
					ssiDetails.setProjectedSalesTurnover(ssiDtlForCgbid
							.getDouble(16));
					ssiDetails
							.setProjectedExports(ssiDtlForCgbid.getDouble(17));
					ssiDetails.setAddress(ssiDtlForCgbid.getString(18));
					ssiDetails.setCity(ssiDtlForCgbid.getString(19));
					ssiDetails.setPincode(ssiDtlForCgbid.getString(20).trim());
					ssiDetails.setDistrict(ssiDtlForCgbid.getString(22));
					ssiDetails.setState(ssiDtlForCgbid.getString(23));
					ssiDetails.setIndustryNature(ssiDtlForCgbid.getString(24));
					ssiDetails.setIndustrySector(ssiDtlForCgbid.getString(25));
					ssiDetails.setCgbid(ssiDtlForCgbid.getString(27));

					// kot

					ssiDetails.setEnterprise(ssiDtlForCgbid.getString(30));

					ssiDetails.setUnitAssisted(ssiDtlForCgbid.getString(31));

					ssiDetails.setWomenOperated(ssiDtlForCgbid.getString(32));

					/*
					 * ssiDetails.setEnterprise("N");
					 * 
					 * ssiDetails.setUnitAssisted("Y");
					 * 
					 * 
					 * ssiDetails.setWomenOperated("Y");
					 */
					borrowerDetails.setOsAmt(ssiDtlForCgbid.getDouble(28));

					ssiDtlForCgbid.close();
					ssiDtlForCgbid = null;

				}
			}
			// Fetching SSI details for CGPAN
			else if (cgbid.equals("")) {
				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
						"Entering the method in DAO to fetch dtls for cgpan..");

				CallableStatement ssiDtlForCgpan = connection.prepareCall
				// ("{?=call funcGetSSIDetailforCGPAN(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

						("{?=call funcGetSSIDetailforCGPAN(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				Log.log(Log.DEBUG, "ApplicationDAO", "fetchBorrowerDtls",
						"CGPAN" + cgpan);

				ssiDtlForCgpan.setString(2, null); // mem bank id
				ssiDtlForCgpan.setString(3, null); // zone id
				ssiDtlForCgpan.setString(4, null); // brnach id
				ssiDtlForCgpan.setString(5, cgpan); // CGPAN

				ssiDtlForCgpan.registerOutParameter(1, Types.INTEGER);
				ssiDtlForCgpan.registerOutParameter(35, Types.VARCHAR);

				ssiDtlForCgpan.registerOutParameter(6, Types.INTEGER); // SSI
																		// Ref
																		// No
				ssiDtlForCgpan.registerOutParameter(7, Types.VARCHAR); // Already
																		// Covered
																		// by
																		// CGTSI
				ssiDtlForCgpan.registerOutParameter(8, Types.VARCHAR); // Already
																		// assisted
																		// by
																		// bank
				ssiDtlForCgpan.registerOutParameter(9, Types.VARCHAR); // SSI
																		// Unique
																		// No
				ssiDtlForCgpan.registerOutParameter(10, Types.VARCHAR); // NPA
				ssiDtlForCgpan.registerOutParameter(11, Types.VARCHAR); // Constitution
				ssiDtlForCgpan.registerOutParameter(12, Types.VARCHAR); // SSI
																		// Type
				ssiDtlForCgpan.registerOutParameter(13, Types.VARCHAR); // unit
																		// Name
				ssiDtlForCgpan.registerOutParameter(14, Types.VARCHAR); // SSI
																		// reg
																		// no
				ssiDtlForCgpan.registerOutParameter(15, Types.DATE); // Commencement
																		// Date
				ssiDtlForCgpan.registerOutParameter(16, Types.VARCHAR); // SSI
																		// ITPAN
				ssiDtlForCgpan.registerOutParameter(17, Types.VARCHAR); // type
																		// of
																		// Activity
				ssiDtlForCgpan.registerOutParameter(18, Types.INTEGER); // No Of
																		// Employees
				ssiDtlForCgpan.registerOutParameter(19, Types.DOUBLE); // project
																		// Sales
				ssiDtlForCgpan.registerOutParameter(20, Types.DOUBLE); // Project
																		// Exports
				ssiDtlForCgpan.registerOutParameter(21, Types.VARCHAR); // SSI
																		// Address
				ssiDtlForCgpan.registerOutParameter(22, Types.VARCHAR); // SSi
																		// City
				ssiDtlForCgpan.registerOutParameter(23, Types.VARCHAR); // Pincode
				ssiDtlForCgpan.registerOutParameter(24, Types.VARCHAR); // Display
																		// Defaulter
				ssiDtlForCgpan.registerOutParameter(25, Types.VARCHAR); // SSI
																		// District
				ssiDtlForCgpan.registerOutParameter(26, Types.VARCHAR); // SSI
																		// State
				ssiDtlForCgpan.registerOutParameter(27, Types.VARCHAR); // Industry
																		// Nature
				ssiDtlForCgpan.registerOutParameter(28, Types.VARCHAR); // Indutry
																		// Sector
																		// name
				ssiDtlForCgpan.registerOutParameter(29, Types.VARCHAR); // Status
				ssiDtlForCgpan.registerOutParameter(30, Types.VARCHAR); // BID
				ssiDtlForCgpan.registerOutParameter(31, Types.DOUBLE); // Outstanding
																		// amount

				/*
				 * ssiDtlForCgpan.executeQuery(); int
				 * ssiDtlForCgpanValue=ssiDtlForCgpan.getInt(1);
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
				 * "SSI Details for CGPAN :" + ssiDtlForCgpanValue);
				 * 
				 * if(ssiDtlForCgpanValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = ssiDtlForCgpan.getString(32);
				 * 
				 * ssiDtlForCgpan.close(); ssiDtlForCgpan=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
				 * "SSI Exception message:" + error);
				 * 
				 * throw new DatabaseException(error); } else {
				 * 
				 * ssiDetails.setBorrowerRefNo(ssiDtlForCgpan.getInt(6));
				 * borrowerDetails
				 * .setPreviouslyCovered(ssiDtlForCgpan.getString(7).trim());
				 * borrowerDetails
				 * .setAssistedByBank(ssiDtlForCgpan.getString(8).trim());
				 * borrowerDetails.setNpa(ssiDtlForCgpan.getString(10).trim());
				 * ssiDetails.setConstitution(ssiDtlForCgpan.getString(11));
				 * ssiDetails.setSsiType(ssiDtlForCgpan.getString(12).trim());
				 * ssiDetails.setSsiName(ssiDtlForCgpan.getString(13));
				 * ssiDetails.setRegNo(ssiDtlForCgpan.getString(14));
				 * ssiDetails.setSsiITPan(ssiDtlForCgpan.getString(16));
				 * ssiDetails.setActivityType(ssiDtlForCgpan.getString(17));
				 * ssiDetails.setEmployeeNos(ssiDtlForCgpan.getInt(18));
				 * ssiDetails
				 * .setProjectedSalesTurnover(ssiDtlForCgpan.getDouble(19));
				 * ssiDetails.setProjectedExports(ssiDtlForCgpan.getDouble(20));
				 * ssiDetails.setAddress(ssiDtlForCgpan.getString(21));
				 * ssiDetails.setCity(ssiDtlForCgpan.getString(22));
				 * ssiDetails.setPincode(ssiDtlForCgpan.getString(23).trim());
				 * ssiDetails.setDistrict(ssiDtlForCgpan.getString(25));
				 * ssiDetails.setState(ssiDtlForCgpan.getString(26));
				 * ssiDetails.setIndustryNature(ssiDtlForCgpan.getString(27));
				 * ssiDetails.setIndustrySector(ssiDtlForCgpan.getString(28));
				 * ssiDetails.setCgbid(ssiDtlForCgpan.getString(30));
				 * borrowerDetails.setOsAmt(ssiDtlForCgpan.getDouble(31));
				 */

				ssiDtlForCgpan.registerOutParameter(32, Types.VARCHAR); //
				ssiDtlForCgpan.registerOutParameter(33, Types.VARCHAR); //
				ssiDtlForCgpan.registerOutParameter(34, Types.VARCHAR); //
				// ssiDtlForCgpan.registerOutParameter(35,Types.VARCHAR);

				ssiDtlForCgpan.executeQuery();
				int ssiObjValue = ssiDtlForCgpan.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"SSI Details :" + ssiObjValue);

				if (ssiObjValue == Constants.FUNCTION_FAILURE) {

					// String error = ssiObj.getString(30);

					String error = ssiDtlForCgpan.getString(35);

					ssiDtlForCgpan.close();
					ssiDtlForCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ssiDetails.setBorrowerRefNo(ssiDtlForCgpan.getInt(6));
					borrowerDetails.setPreviouslyCovered(ssiDtlForCgpan
							.getString(7).trim());
					borrowerDetails.setAssistedByBank(ssiDtlForCgpan.getString(
							8).trim());
					borrowerDetails.setNpa(ssiDtlForCgpan.getString(10).trim());
					ssiDetails.setConstitution(ssiDtlForCgpan.getString(11));
					ssiDetails.setSsiType(ssiDtlForCgpan.getString(12).trim());
					ssiDetails.setSsiName(ssiDtlForCgpan.getString(13));
					ssiDetails.setRegNo(ssiDtlForCgpan.getString(14));
					ssiDetails.setSsiITPan(ssiDtlForCgpan.getString(16));
					ssiDetails.setActivityType(ssiDtlForCgpan.getString(17));
					ssiDetails.setEmployeeNos(ssiDtlForCgpan.getInt(18));
					ssiDetails.setProjectedSalesTurnover(ssiDtlForCgpan
							.getDouble(19));
					ssiDetails
							.setProjectedExports(ssiDtlForCgpan.getDouble(20));
					ssiDetails.setAddress(ssiDtlForCgpan.getString(21));
					ssiDetails.setCity(ssiDtlForCgpan.getString(22));
					ssiDetails.setPincode(ssiDtlForCgpan.getString(23).trim());
					ssiDetails.setDistrict(ssiDtlForCgpan.getString(25));
					ssiDetails.setState(ssiDtlForCgpan.getString(26));
					ssiDetails.setIndustryNature(ssiDtlForCgpan.getString(27));
					ssiDetails.setIndustrySector(ssiDtlForCgpan.getString(28));
					ssiDetails.setCgbid(ssiDtlForCgpan.getString(30));
					borrowerDetails.setOsAmt(ssiDtlForCgpan.getDouble(31));

					// kot
					// ssiDetails.setAddress(ssiObj.getString(28));

					// ssiDetails.setMSE(ssiDtlForCgpan.getString(32));

					// ssiDetails.setMSE(ssiObj.getString(30));

					ssiDetails.setEnterprise(ssiDtlForCgpan.getString(32));

					ssiDetails.setUnitAssisted(ssiDtlForCgpan.getString(33));

					ssiDetails.setWomenOperated(ssiDtlForCgpan.getString(34));

					// ssiDetails.setEnterprise("N");

					// ssiDetails.setUnitAssisted("Y");

					// ssiDetails.setWomenOperated("Y");

					// borrowerDetails.setOsAmt(ssiDtlForCgpan.getDouble(31));

					ssiDtlForCgpan.close();
					ssiDtlForCgpan = null;

				}

			}

			// retrieving promoter details by passing ssi ref no
			int ssiRefNo = ssiDetails.getBorrowerRefNo();

			CallableStatement ssiDtlForSsiRef = connection
					.prepareCall("{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			ssiDtlForSsiRef.setInt(2, ssiRefNo); // AppRefNo

			ssiDtlForSsiRef.registerOutParameter(1, Types.INTEGER);
			ssiDtlForSsiRef.registerOutParameter(23, Types.VARCHAR);

			ssiDtlForSsiRef.registerOutParameter(3, Types.INTEGER); // SSI RefNo
			ssiDtlForSsiRef.registerOutParameter(4, Types.VARCHAR); // Chief
																	// title
			ssiDtlForSsiRef.registerOutParameter(5, Types.VARCHAR); // First
																	// name
			ssiDtlForSsiRef.registerOutParameter(6, Types.VARCHAR); // Middle
																	// Name
			ssiDtlForSsiRef.registerOutParameter(7, Types.VARCHAR); // Last name
			ssiDtlForSsiRef.registerOutParameter(8, Types.VARCHAR); // Chief
																	// ITPAN
			ssiDtlForSsiRef.registerOutParameter(9, Types.VARCHAR); // Gender
			ssiDtlForSsiRef.registerOutParameter(10, Types.DATE); // DOB
			ssiDtlForSsiRef.registerOutParameter(11, Types.VARCHAR); // Legal
																		// Type
			ssiDtlForSsiRef.registerOutParameter(12, Types.VARCHAR); // LegalID
			ssiDtlForSsiRef.registerOutParameter(13, Types.VARCHAR); // Promoter
																		// First
																		// Name
			ssiDtlForSsiRef.registerOutParameter(14, Types.DATE); // Promoter
																	// First DOB
			ssiDtlForSsiRef.registerOutParameter(15, Types.VARCHAR); // Promoter
																		// FirstITPAN
			ssiDtlForSsiRef.registerOutParameter(16, Types.VARCHAR); // Promoter
																		// Second
																		// Name
			ssiDtlForSsiRef.registerOutParameter(17, Types.DATE); // Promoter
																	// Second
																	// DOB
			ssiDtlForSsiRef.registerOutParameter(18, Types.VARCHAR); // Promoter
																		// Second
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(19, Types.VARCHAR); // Promoter
																		// Third
																		// Name
			ssiDtlForSsiRef.registerOutParameter(20, Types.DATE); // Promoter
																	// Third DOB
			ssiDtlForSsiRef.registerOutParameter(21, Types.VARCHAR); // Promoter
																		// Third
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(22, Types.VARCHAR);

			ssiDtlForSsiRef.executeQuery();
			int ssiDtlForSsiRefValue = ssiDtlForSsiRef.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Promoters Details :" + ssiDtlForSsiRefValue);

			if (ssiDtlForSsiRefValue == Constants.FUNCTION_FAILURE) {

				String error = ssiDtlForSsiRef.getString(23);

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Promoter Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ssiDetails.setCpTitle(ssiDtlForSsiRef.getString(4));
				ssiDetails.setCpFirstName(ssiDtlForSsiRef.getString(5));
				if (ssiDtlForSsiRef.getString(6) != null
						&& !ssiDtlForSsiRef.getString(6).equals("")) {
					ssiDetails.setCpMiddleName(ssiDtlForSsiRef.getString(6));
				} else {
					ssiDetails.setCpMiddleName("");
				}

				ssiDetails.setCpLastName(ssiDtlForSsiRef.getString(7));
				ssiDetails.setCpITPAN(ssiDtlForSsiRef.getString(8));
				ssiDetails.setCpGender(ssiDtlForSsiRef.getString(9).trim());
				ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(10)));
				ssiDetails.setCpLegalID(ssiDtlForSsiRef.getString(11));
				ssiDetails.setCpLegalIdValue(ssiDtlForSsiRef.getString(12));
				ssiDetails.setFirstName(ssiDtlForSsiRef.getString(13));
				ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(14)));
				ssiDetails.setFirstItpan(ssiDtlForSsiRef.getString(15));
				ssiDetails.setSecondName(ssiDtlForSsiRef.getString(16));
				ssiDetails.setSecondDOB(DateHelper
						.sqlToUtilDate(ssiDtlForSsiRef.getDate(17)));
				ssiDetails.setSecondItpan(ssiDtlForSsiRef.getString(18));
				ssiDetails.setThirdName(ssiDtlForSsiRef.getString(19));
				ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(20)));
				ssiDetails.setThirdItpan(ssiDtlForSsiRef.getString(21));
				ssiDetails.setSocialCategory(ssiDtlForSsiRef.getString(22));

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

			}
			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);

			Log.log(Log.DEBUG, "ApplicationDAO", "fetchBorrowerDtls",
					"Fetching BorrowerDetails over...");

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls", "Exited");

		return borrowerDetails;

	}

	/**
	 * 
	 * @param cgbid
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public BorrowerDetails fetchBorrowerDtlsNew(String cgbid, String cgpan,
			String loanType) throws DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew", "Entered");

		Connection connection = DBConnection.getConnection(false);

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
	//	System.out.println("CGPAN:" + cgpan);
	//	System.out.println("CGBID:" + cgbid);

		//// System.out.println("test is 1:");

		try {
			if (cgpan.equals("")) {

				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew",
						"Entered");

				CallableStatement ssiDtlForCgbid = connection.prepareCall
				// ("{?=call funcGetSSIDetailforBorIdNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

						("{?=call funcGetSSIDetailforBorIdNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew",
						"CGBID" + cgbid);

				ssiDtlForCgbid.setString(2, cgbid); // CGBID

				ssiDtlForCgbid.registerOutParameter(1, Types.INTEGER);
				ssiDtlForCgbid.registerOutParameter(34, Types.VARCHAR);
				ssiDtlForCgbid.setString(30, loanType);
				ssiDtlForCgbid.registerOutParameter(3, Types.INTEGER); // SSI
																		// Ref
																		// No
				ssiDtlForCgbid.registerOutParameter(4, Types.VARCHAR); // Already
																		// Covered
																		// by
																		// CGTSI
				ssiDtlForCgbid.registerOutParameter(5, Types.VARCHAR); // Already
																		// assisted
																		// by
																		// bank
				ssiDtlForCgbid.registerOutParameter(6, Types.VARCHAR); // SSI
																		// Unique
																		// No
				ssiDtlForCgbid.registerOutParameter(7, Types.VARCHAR); // NPA
				ssiDtlForCgbid.registerOutParameter(8, Types.VARCHAR); // Constitution
				ssiDtlForCgbid.registerOutParameter(9, Types.VARCHAR); // SSI
																		// Type
				ssiDtlForCgbid.registerOutParameter(10, Types.VARCHAR); // unit
																		// Name
				ssiDtlForCgbid.registerOutParameter(11, Types.VARCHAR); // SSI
																		// reg
																		// no
				ssiDtlForCgbid.registerOutParameter(12, Types.DATE); // Commencement
																		// Date
				ssiDtlForCgbid.registerOutParameter(13, Types.VARCHAR); // SSI
																		// ITPAN
				ssiDtlForCgbid.registerOutParameter(14, Types.VARCHAR); // type
																		// of
																		// Activity
				ssiDtlForCgbid.registerOutParameter(15, Types.INTEGER); // No Of
																		// Employees
				ssiDtlForCgbid.registerOutParameter(16, Types.DOUBLE); // project
																		// Sales
				ssiDtlForCgbid.registerOutParameter(17, Types.DOUBLE); // Project
																		// Exports
				ssiDtlForCgbid.registerOutParameter(18, Types.VARCHAR); // SSI
																		// Address
				ssiDtlForCgbid.registerOutParameter(19, Types.VARCHAR); // SSi
																		// City
				ssiDtlForCgbid.registerOutParameter(20, Types.VARCHAR); // Pincode
				ssiDtlForCgbid.registerOutParameter(21, Types.VARCHAR); // Display
																		// Defaulter
				ssiDtlForCgbid.registerOutParameter(22, Types.VARCHAR); // SSI
																		// District
				ssiDtlForCgbid.registerOutParameter(23, Types.VARCHAR); // SSI
																		// State
				ssiDtlForCgbid.registerOutParameter(24, Types.VARCHAR); // Industry
																		// Nature
				ssiDtlForCgbid.registerOutParameter(25, Types.VARCHAR); // Indutry
																		// Sector
																		// name
				ssiDtlForCgbid.registerOutParameter(26, Types.VARCHAR); // Status
				ssiDtlForCgbid.registerOutParameter(27, Types.VARCHAR); // BID
				ssiDtlForCgbid.registerOutParameter(28, Types.DOUBLE); // Outstanding
																		// amount
				ssiDtlForCgbid.registerOutParameter(29, Types.VARCHAR); // MCGS
																		// Flag

				/*
				 * ssiDtlForCgbid.executeQuery(); int
				 * ssiDtlForCgbidValue=ssiDtlForCgbid.getInt(1);
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtlsNew",
				 * "SSI Details for CGBID :" + ssiDtlForCgbidValue);
				 * 
				 * if(ssiDtlForCgbidValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = ssiDtlForCgbid.getString(31); //
				 System.out.println("Error:"+error); ssiDtlForCgbid.close();
				 * ssiDtlForCgbid=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtlsNew",
				 * "SSI Exception message:" + error);
				 * 
				 * throw new DatabaseException(error);
				 * 
				 * } else {
				 * ssiDetails.setBorrowerRefNo(ssiDtlForCgbid.getInt(3));
				 * borrowerDetails
				 * .setPreviouslyCovered(ssiDtlForCgbid.getString(4).trim());
				 * borrowerDetails
				 * .setAssistedByBank(ssiDtlForCgbid.getString(5).trim());
				 * borrowerDetails.setNpa(ssiDtlForCgbid.getString(7).trim());
				 * ssiDetails.setConstitution(ssiDtlForCgbid.getString(8));
				 * ssiDetails.setSsiType(ssiDtlForCgbid.getString(9).trim());
				 * ssiDetails.setSsiName(ssiDtlForCgbid.getString(10));
				 * ssiDetails.setRegNo(ssiDtlForCgbid.getString(11));
				 * ssiDetails.setSsiITPan(ssiDtlForCgbid.getString(13));
				 * ssiDetails.setActivityType(ssiDtlForCgbid.getString(14));
				 * ssiDetails.setEmployeeNos(ssiDtlForCgbid.getInt(15));
				 * ssiDetails
				 * .setProjectedSalesTurnover(ssiDtlForCgbid.getDouble(16));
				 * ssiDetails.setProjectedExports(ssiDtlForCgbid.getDouble(17));
				 * ssiDetails.setAddress(ssiDtlForCgbid.getString(18));
				 * ssiDetails.setCity(ssiDtlForCgbid.getString(19));
				 * ssiDetails.setPincode(ssiDtlForCgbid.getString(20).trim());
				 * ssiDetails.setDistrict(ssiDtlForCgbid.getString(22));
				 * ssiDetails.setState(ssiDtlForCgbid.getString(23));
				 * ssiDetails.setIndustryNature(ssiDtlForCgbid.getString(24));
				 * ssiDetails.setIndustrySector(ssiDtlForCgbid.getString(25));
				 * ssiDetails.setCgbid(ssiDtlForCgbid.getString(27));
				 * borrowerDetails.setOsAmt(ssiDtlForCgbid.getDouble(28));
				 */

				ssiDtlForCgbid.registerOutParameter(30, Types.VARCHAR); //
				ssiDtlForCgbid.registerOutParameter(31, Types.VARCHAR); //
				ssiDtlForCgbid.registerOutParameter(32, Types.VARCHAR); //
				ssiDtlForCgbid.registerOutParameter(33, Types.VARCHAR);

				ssiDtlForCgbid.executeQuery();
				int ssiObjValue = ssiDtlForCgbid.getInt(1);

				//// System.out.println("ssiObjValue is 1:" + ssiObjValue);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"SSI Details :" + ssiObjValue);

				if (ssiObjValue == Constants.FUNCTION_FAILURE) {

					// String error = ssiObj.getString(30);

					String error = ssiDtlForCgbid.getString(34);

					ssiDtlForCgbid.close();
					ssiDtlForCgbid = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ssiDetails.setBorrowerRefNo(ssiDtlForCgbid.getInt(3));
					borrowerDetails.setPreviouslyCovered(ssiDtlForCgbid
							.getString(4).trim());
					borrowerDetails.setAssistedByBank(ssiDtlForCgbid.getString(
							5).trim());
					borrowerDetails.setNpa(ssiDtlForCgbid.getString(7).trim());
					ssiDetails.setConstitution(ssiDtlForCgbid.getString(8));
					ssiDetails.setSsiType(ssiDtlForCgbid.getString(9).trim());
					ssiDetails.setSsiName(ssiDtlForCgbid.getString(10));
					ssiDetails.setRegNo(ssiDtlForCgbid.getString(11));
					ssiDetails.setSsiITPan(ssiDtlForCgbid.getString(13));
					ssiDetails.setActivityType(ssiDtlForCgbid.getString(14));
					ssiDetails.setEmployeeNos(ssiDtlForCgbid.getInt(15));
					ssiDetails.setProjectedSalesTurnover(ssiDtlForCgbid
							.getDouble(16));
					ssiDetails
							.setProjectedExports(ssiDtlForCgbid.getDouble(17));
					ssiDetails.setAddress(ssiDtlForCgbid.getString(18));
					ssiDetails.setCity(ssiDtlForCgbid.getString(19));
					ssiDetails.setPincode(ssiDtlForCgbid.getString(20).trim());
					ssiDetails.setDistrict(ssiDtlForCgbid.getString(22));
					ssiDetails.setState(ssiDtlForCgbid.getString(23));
					ssiDetails.setIndustryNature(ssiDtlForCgbid.getString(24));
					ssiDetails.setIndustrySector(ssiDtlForCgbid.getString(25));
					ssiDetails.setCgbid(ssiDtlForCgbid.getString(27));

					// kot

					ssiDetails.setEnterprise(ssiDtlForCgbid.getString(31));

					ssiDetails.setUnitAssisted(ssiDtlForCgbid.getString(32));

					ssiDetails.setWomenOperated(ssiDtlForCgbid.getString(33));

					// ssiDetails.setEnterprise("N");

					// ssiDetails.setUnitAssisted("Y");

					// ssiDetails.setWomenOperated("Y");

					borrowerDetails.setOsAmt(ssiDtlForCgbid.getDouble(28));

					ssiDtlForCgbid.close();
					ssiDtlForCgbid = null;

				}
			}
			// Fetching SSI details for CGPAN
			else if (cgbid.equals("")) {

				//// System.out.println("funcGetSSIDetailforCGPANNew22");

				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew",
						"Entering the method in DAO to fetch dtls for cgpan..");

				CallableStatement ssiDtlForCgpan = connection.prepareCall
				// ("{?=call funcGetSSIDetailforCGPANNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

						("{?=call funcGetSSIDetailforCGPANNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				Log.log(Log.DEBUG, "ApplicationDAO", "fetchBorrowerDtls",
						"CGPAN" + cgpan);

				ssiDtlForCgpan.setString(2, null); // mem bank id
				ssiDtlForCgpan.setString(3, null); // zone id
				ssiDtlForCgpan.setString(4, null); // brnach id
				ssiDtlForCgpan.setString(5, cgpan); // CGPAN

				ssiDtlForCgpan.registerOutParameter(1, Types.INTEGER);
				ssiDtlForCgpan.registerOutParameter(36, Types.VARCHAR);
				ssiDtlForCgpan.setString(32, loanType);
				ssiDtlForCgpan.registerOutParameter(6, Types.INTEGER); // SSI
																		// Ref
																		// No
				ssiDtlForCgpan.registerOutParameter(7, Types.VARCHAR); // Already
																		// Covered
																		// by
																		// CGTSI
				ssiDtlForCgpan.registerOutParameter(8, Types.VARCHAR); // Already
																		// assisted
																		// by
																		// bank
				ssiDtlForCgpan.registerOutParameter(9, Types.VARCHAR); // SSI
																		// Unique
																		// No
				ssiDtlForCgpan.registerOutParameter(10, Types.VARCHAR); // NPA
				ssiDtlForCgpan.registerOutParameter(11, Types.VARCHAR); // Constitution
				ssiDtlForCgpan.registerOutParameter(12, Types.VARCHAR); // SSI
																		// Type
				ssiDtlForCgpan.registerOutParameter(13, Types.VARCHAR); // unit
																		// Name
				ssiDtlForCgpan.registerOutParameter(14, Types.VARCHAR); // SSI
																		// reg
																		// no
				ssiDtlForCgpan.registerOutParameter(15, Types.DATE); // Commencement
																		// Date
				ssiDtlForCgpan.registerOutParameter(16, Types.VARCHAR); // SSI
																		// ITPAN
				ssiDtlForCgpan.registerOutParameter(17, Types.VARCHAR); // type
																		// of
																		// Activity
				ssiDtlForCgpan.registerOutParameter(18, Types.INTEGER); // No Of
																		// Employees
				ssiDtlForCgpan.registerOutParameter(19, Types.DOUBLE); // project
																		// Sales
				ssiDtlForCgpan.registerOutParameter(20, Types.DOUBLE); // Project
																		// Exports
				ssiDtlForCgpan.registerOutParameter(21, Types.VARCHAR); // SSI
																		// Address
				ssiDtlForCgpan.registerOutParameter(22, Types.VARCHAR); // SSi
																		// City
				ssiDtlForCgpan.registerOutParameter(23, Types.VARCHAR); // Pincode
				ssiDtlForCgpan.registerOutParameter(24, Types.VARCHAR); // Display
																		// Defaulter
				ssiDtlForCgpan.registerOutParameter(25, Types.VARCHAR); // SSI
																		// District
				ssiDtlForCgpan.registerOutParameter(26, Types.VARCHAR); // SSI
																		// State
				ssiDtlForCgpan.registerOutParameter(27, Types.VARCHAR); // Industry
																		// Nature
				ssiDtlForCgpan.registerOutParameter(28, Types.VARCHAR); // Indutry
																		// Sector
																		// name
				ssiDtlForCgpan.registerOutParameter(29, Types.VARCHAR); // Status
				ssiDtlForCgpan.registerOutParameter(30, Types.VARCHAR); // BID
				ssiDtlForCgpan.registerOutParameter(31, Types.DOUBLE); // Outstanding
																		// amount

				/*
				 * ssiDtlForCgpan.executeQuery(); int
				 * ssiDtlForCgpanValue=ssiDtlForCgpan.getInt(1);
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtlsNew",
				 * "SSI Details for CGPAN :" + ssiDtlForCgpanValue);
				 * 
				 * if(ssiDtlForCgpanValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = ssiDtlForCgpan.getString(33);
				 * 
				 * ssiDtlForCgpan.close(); ssiDtlForCgpan=null;
				 * 
				 * connection.rollback();
				 * 
				 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtlsNew",
				 * "SSI Exception message:" + error);
				 * 
				 * throw new DatabaseException(error); } else {
				 * 
				 * ssiDetails.setBorrowerRefNo(ssiDtlForCgpan.getInt(6));
				 * borrowerDetails
				 * .setPreviouslyCovered(ssiDtlForCgpan.getString(7).trim());
				 * borrowerDetails
				 * .setAssistedByBank(ssiDtlForCgpan.getString(8).trim());
				 * borrowerDetails.setNpa(ssiDtlForCgpan.getString(10).trim());
				 * ssiDetails.setConstitution(ssiDtlForCgpan.getString(11));
				 * ssiDetails.setSsiType(ssiDtlForCgpan.getString(12).trim());
				 * ssiDetails.setSsiName(ssiDtlForCgpan.getString(13));
				 * ssiDetails.setRegNo(ssiDtlForCgpan.getString(14));
				 * ssiDetails.setSsiITPan(ssiDtlForCgpan.getString(16));
				 * ssiDetails.setActivityType(ssiDtlForCgpan.getString(17));
				 * ssiDetails.setEmployeeNos(ssiDtlForCgpan.getInt(18));
				 * ssiDetails
				 * .setProjectedSalesTurnover(ssiDtlForCgpan.getDouble(19));
				 * ssiDetails.setProjectedExports(ssiDtlForCgpan.getDouble(20));
				 * ssiDetails.setAddress(ssiDtlForCgpan.getString(21));
				 * ssiDetails.setCity(ssiDtlForCgpan.getString(22));
				 * ssiDetails.setPincode(ssiDtlForCgpan.getString(23).trim());
				 * ssiDetails.setDistrict(ssiDtlForCgpan.getString(25));
				 * ssiDetails.setState(ssiDtlForCgpan.getString(26));
				 * ssiDetails.setIndustryNature(ssiDtlForCgpan.getString(27));
				 * ssiDetails.setIndustrySector(ssiDtlForCgpan.getString(28));
				 * ssiDetails.setCgbid(ssiDtlForCgpan.getString(30));
				 * borrowerDetails.setOsAmt(ssiDtlForCgpan.getDouble(31));
				 */

				ssiDtlForCgpan.registerOutParameter(32, Types.VARCHAR); //
				ssiDtlForCgpan.registerOutParameter(33, Types.VARCHAR); //
				ssiDtlForCgpan.registerOutParameter(34, Types.VARCHAR); //
				ssiDtlForCgpan.registerOutParameter(35, Types.VARCHAR);

				ssiDtlForCgpan.executeQuery();
				int ssiObjValue = ssiDtlForCgpan.getInt(1);

				//// System.out.println("funcGetSSIDetailforCGPANNew" +
				// ssiObjValue);

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"SSI Details :" + ssiObjValue);

				if (ssiObjValue == Constants.FUNCTION_FAILURE) {

					// String error = ssiObj.getString(30);

					String error = ssiDtlForCgpan.getString(36);

					ssiDtlForCgpan.close();
					ssiDtlForCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ssiDetails.setBorrowerRefNo(ssiDtlForCgpan.getInt(6));
					borrowerDetails.setPreviouslyCovered(ssiDtlForCgpan
							.getString(7).trim());
					borrowerDetails.setAssistedByBank(ssiDtlForCgpan.getString(
							8).trim());
					borrowerDetails.setNpa(ssiDtlForCgpan.getString(10).trim());
					ssiDetails.setConstitution(ssiDtlForCgpan.getString(11));
					ssiDetails.setSsiType(ssiDtlForCgpan.getString(12).trim());
					ssiDetails.setSsiName(ssiDtlForCgpan.getString(13));
					ssiDetails.setRegNo(ssiDtlForCgpan.getString(14));
					ssiDetails.setSsiITPan(ssiDtlForCgpan.getString(16));
					ssiDetails.setActivityType(ssiDtlForCgpan.getString(17));
					ssiDetails.setEmployeeNos(ssiDtlForCgpan.getInt(18));
					ssiDetails.setProjectedSalesTurnover(ssiDtlForCgpan
							.getDouble(19));
					ssiDetails
							.setProjectedExports(ssiDtlForCgpan.getDouble(20));
					ssiDetails.setAddress(ssiDtlForCgpan.getString(21));
					ssiDetails.setCity(ssiDtlForCgpan.getString(22));
					ssiDetails.setPincode(ssiDtlForCgpan.getString(23).trim());
					ssiDetails.setDistrict(ssiDtlForCgpan.getString(25));
					ssiDetails.setState(ssiDtlForCgpan.getString(26));
					ssiDetails.setIndustryNature(ssiDtlForCgpan.getString(27));
					ssiDetails.setIndustrySector(ssiDtlForCgpan.getString(28));
					ssiDetails.setCgbid(ssiDtlForCgpan.getString(30));
					borrowerDetails.setOsAmt(ssiDtlForCgpan.getDouble(31));

					// kot
					// ssiDetails.setEnterprise("N");

					// ssiDetails.setUnitAssisted("Y");

					// ssiDetails.setWomenOperated("Y");

					ssiDetails.setEnterprise(ssiDtlForCgpan.getString(33));

					ssiDetails.setUnitAssisted(ssiDtlForCgpan.getString(34));

					ssiDetails.setWomenOperated(ssiDtlForCgpan.getString(35));

					ssiDtlForCgpan.close();
					ssiDtlForCgpan = null;

				}

			}
			//// System.out.println("funcGetPromoterDtlforSSIRef");
			// retrieving promoter details by passing ssi ref no
			int ssiRefNo = ssiDetails.getBorrowerRefNo();

			//// System.out.println("ssiRefNo is ss" + ssiRefNo);

			CallableStatement ssiDtlForSsiRef = connection
					.prepareCall("{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			ssiDtlForSsiRef.setInt(2, ssiRefNo); // AppRefNo

			ssiDtlForSsiRef.registerOutParameter(1, Types.INTEGER);
			ssiDtlForSsiRef.registerOutParameter(23, Types.VARCHAR);

			ssiDtlForSsiRef.registerOutParameter(3, Types.INTEGER); // SSI RefNo
			ssiDtlForSsiRef.registerOutParameter(4, Types.VARCHAR); // Chief
																	// title
			ssiDtlForSsiRef.registerOutParameter(5, Types.VARCHAR); // First
																	// name
			ssiDtlForSsiRef.registerOutParameter(6, Types.VARCHAR); // Middle
																	// Name
			ssiDtlForSsiRef.registerOutParameter(7, Types.VARCHAR); // Last name
			ssiDtlForSsiRef.registerOutParameter(8, Types.VARCHAR); // Chief
																	// ITPAN
			ssiDtlForSsiRef.registerOutParameter(9, Types.VARCHAR); // Gender
			ssiDtlForSsiRef.registerOutParameter(10, Types.DATE); // DOB
			ssiDtlForSsiRef.registerOutParameter(11, Types.VARCHAR); // Legal
																		// Type
			ssiDtlForSsiRef.registerOutParameter(12, Types.VARCHAR); // LegalID
			ssiDtlForSsiRef.registerOutParameter(13, Types.VARCHAR); // Promoter
																		// First
																		// Name
			ssiDtlForSsiRef.registerOutParameter(14, Types.DATE); // Promoter
																	// First DOB
			ssiDtlForSsiRef.registerOutParameter(15, Types.VARCHAR); // Promoter
																		// FirstITPAN
			ssiDtlForSsiRef.registerOutParameter(16, Types.VARCHAR); // Promoter
																		// Second
																		// Name
			ssiDtlForSsiRef.registerOutParameter(17, Types.DATE); // Promoter
																	// Second
																	// DOB
			ssiDtlForSsiRef.registerOutParameter(18, Types.VARCHAR); // Promoter
																		// Second
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(19, Types.VARCHAR); // Promoter
																		// Third
																		// Name
			ssiDtlForSsiRef.registerOutParameter(20, Types.DATE); // Promoter
																	// Third DOB
			ssiDtlForSsiRef.registerOutParameter(21, Types.VARCHAR); // Promoter
																		// Third
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(22, Types.VARCHAR);

			ssiDtlForSsiRef.executeQuery();
			int ssiDtlForSsiRefValue = ssiDtlForSsiRef.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Promoters Details :" + ssiDtlForSsiRefValue);

			//// System.out.println("funcGetPromoterDtlforSSIRef3");

			if (ssiDtlForSsiRefValue == Constants.FUNCTION_FAILURE) {

				String error = ssiDtlForSsiRef.getString(23);

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Promoter Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ssiDetails.setCpTitle(ssiDtlForSsiRef.getString(4));
				ssiDetails.setCpFirstName(ssiDtlForSsiRef.getString(5));
				if (ssiDtlForSsiRef.getString(6) != null
						&& !ssiDtlForSsiRef.getString(6).equals("")) {
					ssiDetails.setCpMiddleName(ssiDtlForSsiRef.getString(6));
				} else {
					ssiDetails.setCpMiddleName("");
				}

				ssiDetails.setCpLastName(ssiDtlForSsiRef.getString(7));
				ssiDetails.setCpITPAN(ssiDtlForSsiRef.getString(8));
				ssiDetails.setCpGender(ssiDtlForSsiRef.getString(9).trim());
				// ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef.getDate(10)));
				ssiDetails.setCpLegalID(ssiDtlForSsiRef.getString(11));
				ssiDetails.setCpLegalIdValue(ssiDtlForSsiRef.getString(12));
				ssiDetails.setFirstName(ssiDtlForSsiRef.getString(13));
				// ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef.getDate(14)));
				ssiDetails.setFirstItpan(ssiDtlForSsiRef.getString(15));
				ssiDetails.setSecondName(ssiDtlForSsiRef.getString(16));
				// ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef.getDate(17)));
				ssiDetails.setSecondItpan(ssiDtlForSsiRef.getString(18));
				ssiDetails.setThirdName(ssiDtlForSsiRef.getString(19));
				// ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef.getDate(20)));
				ssiDetails.setThirdItpan(ssiDtlForSsiRef.getString(21));
				ssiDetails.setSocialCategory(ssiDtlForSsiRef.getString(22));

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

			}
			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);

			Log.log(Log.DEBUG, "ApplicationDAO", "fetchBorrowerDtlsNew",
					"Fetching BorrowerDetails over...");

			connection.commit();
			//// System.out.println("funcGetPromoterDtlforSSIRef1");
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtlsNew", "Exited");

		return borrowerDetails;

	}

	/**
	 * This method updates the decision taken on the probable duplicate
	 * applications by the CGTSI user.
	 * 
	 * @param appRefNo
	 * @param status
	 * @throws DatabaseException
	 * @roseuid 3972E7240004
	 */
	public void updateAppStatus(String appRefNo, int status)
			throws DatabaseException {

	}

	/**
	 * This method is used to store the working capital enhancement details.
	 * 
	 * @param app
	 * @return int
	 * @throws DatabaseException
	 * @roseuid 3972E7240007
	 */
	public void storeWcEnhancement(Application app, String createdBy)
			throws DatabaseException {
		String appRefNo = app.getAppRefNo();
		Log.log(Log.INFO, "ApplicationDAO", "storeWcEnhancement",
				"app ref no :" + appRefNo);
		Log.log(Log.INFO, "ApplicationDAO", "storeWcEnhancement", "Entered");

		RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
		//// System.out.println("in  Application16q");
		Connection connection = DBConnection.getConnection(false);

		try {

			//// System.out.println("in  Application16q1");
			String subSchemeName = rpProcessor.getSubScheme(app);
			app.setSubSchemeName(subSchemeName);
			//// System.out.println("in  Application16q2");
			submitApp(app, createdBy, connection);
			int temssiRef = ((app.getBorrowerDetails()).getSsiDetails())
					.getBorrowerRefNo();
			String ssiRefNumber = Integer.toString(temssiRef);
			//// System.out.println("Enhanced SSi Reference Line Number 4510: "+ssiRefNumber);
			RpDAO rpDAO1 = new RpDAO();
			double prevTotalSancAmt = rpDAO1
					.getTotalSanctionedAmountNew(ssiRefNumber);
			ApplicationDAO appdao = new ApplicationDAO();

			double prevTotalHandloomSancAmt = appdao
					.getTotalSanctionedHandloomAmountNew(ssiRefNumber);

			double currentCreditAmount = 0;
			if (app.getLoanType().equals("TC")) {
				currentCreditAmount = app.getTermLoan().getCreditGuaranteed();
			} else if (app.getLoanType().equals("CC")) {
				currentCreditAmount = app.getTermLoan().getCreditGuaranteed()
						+ app.getWc().getCreditFundBased()
						+ app.getWc().getCreditNonFundBased();
			} else if (app.getLoanType().equals("WC")) {
				currentCreditAmount = app.getWc().getEnhancedFundBased()
						+ app.getWc().getEnhancedNonFundBased();
			}

			if ((currentCreditAmount > 200000)
					&& (app.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto  Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			} else if ((currentCreditAmount + prevTotalHandloomSancAmt > 200000)
					&& (app.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Guarantee of Rs. "
								+ prevTotalHandloomSancAmt
								+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			} else if (currentCreditAmount + prevTotalSancAmt > 10000000) {
				throw new DatabaseException(
						"Guarantee of Rs. "
								+ prevTotalSancAmt
								+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto"
								+ 10000000);
			}
			//// System.out.println("in  Application16");
			updateApplicationDtl(app, createdBy, connection);
			updateGuarantorDtls(app, createdBy, connection);
			updateTermLoanDtl(app, createdBy, connection);
			updateWCDtl(app, createdBy, connection);
			updateSecDetails(app, createdBy, connection);

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			MCGFDetails mcgfDetails = app.getMCGFDetails();
			if (mcgfDetails != null) {
				mcgfDetails.setApplicationReferenceNumber(appRefNo);
				mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
						connection);
			}

			CallableStatement updateWEnhanceDtl = connection
					.prepareCall("{?=call packWCEnhancement.fucInsertWCEnhancement(?,?,?,?,?,?,?,?)}");
			updateWEnhanceDtl.registerOutParameter(1, Types.INTEGER);
			updateWEnhanceDtl.registerOutParameter(9, Types.VARCHAR);

			updateWEnhanceDtl.setString(2, appRefNo);
			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"app ref no :" + appRefNo);
			updateWEnhanceDtl
					.setDouble(3, (app.getWc()).getEnhancedFundBased());
			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"fund based :" + (app.getWc()).getEnhancedFundBased());

			if ((app.getWc()).getEnhancedNonFundBased() == 0) {
				updateWEnhanceDtl.setDouble(4, 0);

			} else {

				updateWEnhanceDtl.setDouble(4,
						(app.getWc()).getEnhancedNonFundBased());
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"storeWcEnhancement",
					"fund non based :"
							+ (app.getWc()).getEnhancedNonFundBased());
			updateWEnhanceDtl.setDate(
					5,
					new java.sql.Date(((app.getWc()).getEnhancementDate())
							.getTime()));
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"storeWcEnhancement",
					"date:"
							+ new java.sql.Date(((app.getWc())
									.getEnhancementDate()).getTime()));
			updateWEnhanceDtl.setDouble(6,
					(app.getWc()).getEnhancedFBInterest());
			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"interest:" + (app.getWc()).getEnhancedFBInterest());

			if ((app.getWc()).getEnhancedNFBComission() == 0) {
				updateWEnhanceDtl.setDouble(7, 0);

			} else {
				updateWEnhanceDtl.setDouble(7,
						(app.getWc()).getEnhancedNFBComission());
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"commission:" + (app.getWc()).getEnhancedNFBComission());

			updateWEnhanceDtl.setString(8, (app.getWc()).getWcInterestType());
			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"Interest Type:" + (app.getWc()).getWcInterestType());

			updateWEnhanceDtl.executeQuery();
			int updateWEnhanceDtlValue = updateWEnhanceDtl.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "storeWcEnhancement",
					"WC Details result :" + updateWEnhanceDtlValue);

			if (updateWEnhanceDtlValue == Constants.FUNCTION_FAILURE) {

				String error = updateWEnhanceDtl.getString(9);

				updateWEnhanceDtl.close();
				updateWEnhanceDtl = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			updateWEnhanceDtl.close();
			updateWEnhanceDtl = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "storeWcEnhancement",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "storeWcEnhancement",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

	}

	/**
	 * This method is used to store the working capital renewal details.
	 * 
	 * @param app
	 * @return int
	 * @throws DatabaseException
	 * @roseuid 3972E7240009
	 */
	public String storeWcRenewal(Application app, String createdBy)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		String appRefNo = "";

		try {

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();

			Log.log(Log.INFO, "ApplicationDAO", "storeWcRenewal", "Entered");

			Log.log(Log.INFO, "ApplicationDAO", "storeWcRenewal",
					"before submitting application details");
			String subSchemeName = rpProcessor.getSubScheme(app);
			app.setSubSchemeName(subSchemeName);

			double balanceAppAmt = getBalanceApprovedAmt(app);

			//// System.out.println("Balance Approved Amount-5001:"+balanceAppAmt);
			int ssiRefNo = ((app.getBorrowerDetails()).getSsiDetails())
					.getBorrowerRefNo();
			String ssiRefNumber = Integer.toString(ssiRefNo);
			RpDAO rpDAO1 = new RpDAO();
			double prevTotalSancAmt = rpDAO1
					.getTotalSanctionedAmountNew(ssiRefNumber);
			ApplicationDAO appdao = new ApplicationDAO();
			double prevTotalHandloomSancAmt = appdao
					.getTotalSanctionedHandloomAmountNew(ssiRefNumber);

			if ((prevTotalHandloomSancAmt > 200000)
					&& (app.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Guarantee of Rs. "
								+ prevTotalHandloomSancAmt
								+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			}

			if (app.getLoanType().equals("TC")
					|| app.getLoanType().equals("CC")) {
				if (app.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			} else if (app.getLoanType().equals("WC")) {
				balanceAppAmt = balanceAppAmt
						+ (app.getWc().getCreditFundBased() + app.getWc()
								.getCreditNonFundBased());
				if (app.getWc().getCreditFundBased()
						+ app.getWc().getCreditNonFundBased() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			} else if (app.getLoanType().equals("BO")) {
				if (app.getTermLoan().getCreditGuaranteed()
						+ app.getWc().getCreditFundBased()
						+ app.getWc().getCreditNonFundBased() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			}

			appRefNo = submitApplicationDetails(app, createdBy, connection);
			Log.log(Log.INFO, "ApplicationDAO", "storeWcRenewal",
					"after submitting application details");
			Log.log(Log.INFO, "ApplicationDAO", "storeWcRenewal",
					"renewal app ref no :" + appRefNo);
			app.setAppRefNo(appRefNo);
			submitGuarantorSecurityDetails(app, connection);
			submitTermCreditDetails(app, createdBy, connection);

			WorkingCapital tempWc = app.getWc();

			Administrator admin = new Administrator();
			ParameterMaster param = admin.getParameter();

			tempWc.setWcTenure(param.getWcTenorInYrs() * 12);

			app.setWc(tempWc);

			submitWCDetailsNew(app, createdBy, connection);
			submitSecDetails(app, connection);

			if (app.getMCGFDetails() != null) {
				MCGFDetails mcgfDetails = app.getMCGFDetails();
				mcgfDetails.setApplicationReferenceNumber(appRefNo);
				app.setMCGFDetails(mcgfDetails);
				mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
						connection);
			}

			connection.commit();
		} catch (SQLException exception) {
			Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
					exception.getMessage());
			Log.logException(exception);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "submitApplicationDetails",
						ignore.getMessage());
			}

			throw new DatabaseException(exception.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "storeWcRenewal", "Exited");

		return appRefNo;
	}

	/**
	 * This method is used to upload all application files. During upload
	 * elgiibility check is done. All the valid applications are updated into
	 * database.Invalid files are shown to the user.
	 * 
	 * @param apps
	 * @roseuid 3973DDD700CC
	 */
	public void uploadApplications(ArrayList apps) {

	}

	/**
	 * For the selected sub-scheme, this method fetches the Eligibility criteria
	 * maintained.
	 * 
	 * @param schemeID
	 * @return com.cgtsi.application.EligibilityCriteria
	 * @throws DatabaseException
	 * @roseuid 39797F4701BB
	 */
	public EligibilityCriteria getEligibilityCriteria(SubScheme subscheme)
			throws DatabaseException {
		return null;
	}

	/**
	 * This method to check whether an application's guarantee cover is live or
	 * not.
	 * 
	 * @param cgpan
	 * @return Boolean
	 * @roseuid 3979855202E2
	 */
	public boolean isAppLive(String cgpan) {
		return false;
	}

	// Added by ritesh path on 23Nov2006 to check duplicate based on mli
	/**
	 * This method is used to check applications for their duplicity for the
	 * selected duplicate criteria. This duplicate check is done for an
	 * application across members in a state/district based on mli.
	 * 
	 * @param mliID
	 * @param duplicateCriteria
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 397A732202D8
	 */
	public HashMap checkDuplicatePath(String bankName) throws DatabaseException {
		// System.out.println("ApplicationProcessor checkDuplicatePath Entered 4444444444444");
		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath", "Entered");
		Connection connection = DBConnection.getConnection(false);
		String bankId = "";
		String branchId = "";
		String zoneId = "";
		ArrayList tcApprovedAppList = null;
		ArrayList wcApprovedAppList = null;
		ArrayList tcPendingAppList = new ArrayList();
		ArrayList wcPendingAppList = new ArrayList();
		HashMap approvedPendingApplications = new HashMap();
		try {
			tcApprovedAppList = new ArrayList();
			wcApprovedAppList = new ArrayList();
			CallableStatement approvedApps = connection
					.prepareCall("{?=call packGetPackagePath.funcGetDuplicateAppPath(?,?,?)}");
			approvedApps.registerOutParameter(1, Types.INTEGER);
			approvedApps.registerOutParameter(2, Constants.CURSOR);
			approvedApps.registerOutParameter(3, Types.VARCHAR);
			approvedApps.setString(4, bankName);
			approvedApps.execute();
			int functionReturnValue = approvedApps.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {
				String error = approvedApps.getString(3);
				System.out.println("ApplicationDAO Line number4112"+error);
				approvedApps.close();
				approvedApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet approvedAppsResults = (ResultSet) approvedApps
						.getObject(2);
				Application aApplication = null;
				BorrowerDetails aBorrowerDetails = null;
				SSIDetails aSsiDetails = null;
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"Just Before ");
				int ritesh = 0;
				while (approvedAppsResults.next()) {
					// ritesh++;
					aApplication = new Application();
					aBorrowerDetails = new BorrowerDetails();
					aSsiDetails = new SSIDetails();
					aApplication.setCgpan(approvedAppsResults.getString(1));
					aApplication.setAppRefNo(approvedAppsResults.getString(2));
					//// System.out.println("Line number 4778 App Ref No:"+approvedAppsResults.getString(2));
					aSsiDetails.setCgbid(approvedAppsResults.getString(3));
					aSsiDetails.setSsiName(approvedAppsResults.getString(4));
					aSsiDetails.setRegNo(approvedAppsResults.getString(5));
					aSsiDetails
							.setCpFirstName(approvedAppsResults.getString(6));
					aSsiDetails.setCpMiddleName(approvedAppsResults
							.getString(7));
					aSsiDetails.setCpLastName(approvedAppsResults.getString(8));
					aSsiDetails.setAddress(approvedAppsResults.getString(9));
					aApplication.setBankId(approvedAppsResults.getString(10));
					aApplication.setZoneId("0000");
					aApplication.setBranchId("0000");
					aApplication.setLoanType(approvedAppsResults.getString(13));
					aSsiDetails.setCpITPAN(approvedAppsResults.getString(14));
					aSsiDetails.setState(approvedAppsResults.getString(15));
					aSsiDetails.setDistrict(approvedAppsResults.getString(16));
					aApplication.setMliRefNo(approvedAppsResults.getString(17));
					aBorrowerDetails.setSsiDetails(aSsiDetails);
					aApplication.setBorrowerDetails(aBorrowerDetails);
					aApplication.setExistSSI(approvedAppsResults.getString(18));
					if (approvedAppsResults.getString(13).equals("TC")
							|| approvedAppsResults.getString(13).equals("CC")) {
						tcApprovedAppList.add(aApplication);
					} else if (approvedAppsResults.getString(13).equals("WC")) {
						wcApprovedAppList.add(aApplication);
					}
				}
				//// System.out.println("value of ritesh  = "+ritesh);
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"After");
				approvedAppsResults.close();
				aApplication = null;
				aBorrowerDetails = null;
				aSsiDetails = null;
				approvedAppsResults = null;
				approvedApps.close();
				approvedApps = null;
			}

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicatePath",
					"After getting all approved applications");

			CallableStatement pendingApps = connection
					.prepareCall("{?=call packGetPackagePath.funcGetAllAppPath(?,?,?)}");
			pendingApps.registerOutParameter(1, Types.INTEGER);
			pendingApps.registerOutParameter(2, Constants.CURSOR);
			pendingApps.registerOutParameter(3, Types.VARCHAR);
			pendingApps.setString(4, bankName);
			pendingApps.execute();
			int functionReturnValues = pendingApps.getInt(1);
			if (functionReturnValues == Constants.FUNCTION_FAILURE) {
				String error = pendingApps.getString(3);
				pendingApps.close();
				pendingApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsResults = (ResultSet) pendingApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				while (pendingAppsResults.next()) {
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					if (pendingAppsResults.getString(1) != null
							&& !(pendingAppsResults.getString(1).equals(""))) {
						pApplication.setCgpan(pendingAppsResults.getString(1));
					}
					pApplication.setAppRefNo(pendingAppsResults.getString(2));
					pSsiDetails.setCgbid(pendingAppsResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsResults.getString(4));
					pSsiDetails.setRegNo(pendingAppsResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsResults.getString(6));
					pSsiDetails
							.setCpMiddleName(pendingAppsResults.getString(7));
					pSsiDetails.setCpLastName(pendingAppsResults.getString(8));
					pSsiDetails.setAddress(pendingAppsResults.getString(9));
					pApplication.setBankId(pendingAppsResults.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsResults.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsResults.getString(14));
					pSsiDetails.setState(pendingAppsResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsResults.getString(16));
					pApplication.setMliRefNo(pendingAppsResults.getString(17));
					pApplication.setPrevSSI(pendingAppsResults.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsResults.getString(13).equals("TC")
							|| pendingAppsResults.getString(13).equals("CC")) {
						tcPendingAppList.add(pApplication);
					} else if (pendingAppsResults.getString(13).equals("WC")) {
						wcPendingAppList.add(pApplication);
					}
				}
				pendingAppsResults.close();
				pendingAppsResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingApps.close();
				pendingApps = null;
			}
			// Log.log(Log.DEBUG,"ApplicationDAO","checkDuplicatePath","After getting all pending applications");
			/**
			 * This block returns the applications with status 'PE'
			 */
			CallableStatement pendingStatusApps = connection
					.prepareCall("{?=call packGetPackagePath.funcGetPendingStatusPath(?,?,?)}");

			pendingStatusApps.registerOutParameter(1, Types.INTEGER);
			pendingStatusApps.registerOutParameter(2, Constants.CURSOR);
			pendingStatusApps.registerOutParameter(3, Types.VARCHAR);
			pendingStatusApps.setString(4, bankName);
			pendingStatusApps.execute();
			int functionReturnVal = pendingStatusApps.getInt(1);
			if (functionReturnVal == Constants.FUNCTION_FAILURE) {
				String error = pendingStatusApps.getString(3);
				pendingStatusApps.close();
				pendingStatusApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsStatusResults = (ResultSet) pendingStatusApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				while (pendingAppsStatusResults.next()) {
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsStatusResults
							.getString(2));
					pSsiDetails.setCgbid(pendingAppsStatusResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsStatusResults
							.getString(4));
					pSsiDetails.setRegNo(pendingAppsStatusResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsStatusResults
							.getString(6));
					pSsiDetails.setCpMiddleName(pendingAppsStatusResults
							.getString(7));
					pSsiDetails.setCpLastName(pendingAppsStatusResults
							.getString(8));
					pSsiDetails.setAddress(pendingAppsStatusResults
							.getString(9));
					pApplication.setBankId(pendingAppsStatusResults
							.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsStatusResults
							.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsStatusResults
							.getString(14));
					pSsiDetails
							.setState(pendingAppsStatusResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsStatusResults
							.getString(16));
					pApplication.setMliRefNo(pendingAppsStatusResults
							.getString(17));
					pApplication.setPrevSSI(pendingAppsStatusResults
							.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsStatusResults.getString(13).equals("TC")
							|| pendingAppsStatusResults.getString(13).equals(
									"CC")) {
						tcPendingAppList.add(pApplication);
					} else if (pendingAppsStatusResults.getString(13).equals(
							"WC")) {
						wcPendingAppList.add(pApplication);
					}
				}
				pendingAppsStatusResults.close();
				pendingAppsStatusResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingStatusApps.close();
				pendingStatusApps = null;
			}
			// Log.log(Log.DEBUG,"ApplicationDAO","checkDuplicatePath","After getting all pending status");
			HashMap tcApprovedApplications = new HashMap();
			HashMap wcApprovedApplications = new HashMap();
			HashMap tcPendingApplications = new HashMap();
			HashMap wcPendingApplications = new HashMap();

			ArrayList tcApprovedApplicationsList = new ArrayList();
			ArrayList wcApprovedApplicationsList = new ArrayList();
			ArrayList tcPendingApplicationsList = new ArrayList();
			ArrayList wcPendingApplicationsList = new ArrayList();
			// Approved Application from the DB
			int tcApprovedListSize = tcApprovedAppList.size();
			int wcApprovedListSize = wcApprovedAppList.size();
			// Pending Application from the DB
			int tcPendingListSize = tcPendingAppList.size();
			int wcPendingListSize = wcPendingAppList.size();

			System.out.println("PATH  app dao returning tcApprovedAppList *****= "+tcApprovedAppList.size());
			System.out.println("PATH  returning wcApprovedAppList *****= "+wcApprovedAppList.size());
			System.out.println("PATH  returning tcPendingAppList *****= "+tcPendingAppList.size());
		  // System.out.println("PATH  returning wcPendingAppList *****= "+wcPendingAppList.size());
			/*
			 * This loop groups the term loan approved applications based on the
			 * MliID and adds them to a hashtable
			 */
			for (int i = 0; i < tcApprovedListSize; i++) {
				// System.out.println("ritesh in tcApprovedListSize loop===="+tcApprovedListSize);
				Application tcApprovedApplication = (Application) tcApprovedAppList
						.get(i);
				bankId = tcApprovedApplication.getBankId();
				zoneId = tcApprovedApplication.getZoneId();
				branchId = tcApprovedApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (tcApprovedApplications.containsKey(mliIdString)) {
					tcApprovedApplicationsList = (ArrayList) tcApprovedApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else tcApprovedListSize loop");
					tcApprovedApplicationsList = new ArrayList();
				}
				tcApprovedApplicationsList.add(tcApprovedApplication);
				tcApprovedApplications.put(mliIdString,
						tcApprovedApplicationsList);
			}
			/*
			 * This loop groups the working capital approved applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int j = 0; j < wcApprovedListSize; j++) {
				System.out.println("RAAAJJJJ in wcApprovedListSize loop==="+wcApprovedListSize);
				Application wcApprovedApplication = (Application) wcApprovedAppList
						.get(j);
				bankId = wcApprovedApplication.getBankId();
				zoneId = wcApprovedApplication.getZoneId();
				branchId = wcApprovedApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (wcApprovedApplications.containsKey(mliIdString)) {
					wcApprovedApplicationsList = (ArrayList) wcApprovedApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else wcApprovedListSize loop");
					wcApprovedApplicationsList = new ArrayList();
				}
				wcApprovedApplicationsList.add(wcApprovedApplication);
				wcApprovedApplications.put(mliIdString,
						wcApprovedApplicationsList);
			}
			/*
			 * This loop groups the term credit pending applications based on
			 * the MliID and adds them to a hashtable
			 */
			for (int k = 0; k < tcPendingListSize; k++) {
				//// System.out.println("ritesh in tcPendingListSize loop");
				Application tcPendingApplication = (Application) tcPendingAppList
						.get(k);

				bankId = tcPendingApplication.getBankId();
				zoneId = tcPendingApplication.getZoneId();
				branchId = tcPendingApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (tcPendingApplications.containsKey(mliIdString)) {
					tcPendingApplicationsList = (ArrayList) tcPendingApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else tcPendingListSize loop");
					tcPendingApplicationsList = new ArrayList();
				}
				tcPendingApplicationsList.add(tcPendingApplication);
				tcPendingApplications.put(mliIdString,
						tcPendingApplicationsList);
			}
			/*
			 * This loop groups the working capital pending applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int l = 0; l < wcPendingListSize; l++) {
				Application wcPendingApplication = (Application) wcPendingAppList
						.get(l);
				bankId = wcPendingApplication.getBankId();
				zoneId = wcPendingApplication.getZoneId();
				branchId = wcPendingApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (wcPendingApplications.containsKey(mliIdString)) {
					wcPendingApplicationsList = (ArrayList) wcPendingApplications
							.get(mliIdString);
				} else {
					wcPendingApplicationsList = new ArrayList();
				}
				wcPendingApplicationsList.add(wcPendingApplication);
				wcPendingApplications.put(mliIdString,
						wcPendingApplicationsList);
			}
			approvedPendingApplications.put("tcApproved",	tcApprovedApplications);
			approvedPendingApplications.put("wcApproved",wcApprovedApplications);
			approvedPendingApplications.put("tcPending", tcPendingApplications);
			approvedPendingApplications.put("wcPending", wcPendingApplications);
			
			
			System.out.println("tcApproved=="+tcApprovedApplications);
			System.out.println("wcApproved=="+wcApprovedApplications);
			System.out.println("tcPending=="+tcPendingApplications);
			System.out.println("wcPending=="+wcPendingApplications);
			//System.out.println("tcApprovedApplications====+wcApprovedApplications===+tcPendingApplications===+wcPendingApplications");

			tcApprovedApplications = null;
			wcApprovedApplications = null;
			tcPendingApplications = null;
			wcPendingApplications = null;
			tcApprovedApplicationsList = null;
			wcApprovedApplicationsList = null;
			tcPendingApplicationsList = null;
			wcPendingApplicationsList = null;
			bankId = null;
			zoneId = null;
			branchId = null;
			tcApprovedAppList = null;
			wcApprovedAppList = null;
			tcPendingAppList = null;
			wcPendingAppList = null;
			connection.commit();
		} catch (SQLException sqlException) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"Exception :" + ignore.getMessage());
			}
			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath", "Exited");
		return approvedPendingApplications;
	}

	public HashMap checkDuplicatePathnew(String bankName)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath", "Entered");
		Connection connection = DBConnection.getConnection(false);
		String bankId = "";
		String branchId = "";
		String zoneId = "";
		ArrayList tcApprovedAppList = null;
		ArrayList wcApprovedAppList = null;
		ArrayList tcPendingAppList = new ArrayList();
		ArrayList wcPendingAppList = new ArrayList();
		HashMap approvedPendingApplications = new HashMap();
		try {
			tcApprovedAppList = new ArrayList();
			wcApprovedAppList = new ArrayList();
			CallableStatement approvedApps = connection
					.prepareCall("{?=call packGetPackagePath050815.funcGetDuplicateAppPathTEST(?,?,?)}");
			approvedApps.registerOutParameter(1, Types.INTEGER);
			approvedApps.registerOutParameter(2, Constants.CURSOR);
			approvedApps.registerOutParameter(3, Types.VARCHAR);
			approvedApps.setString(4, bankName);
			approvedApps.execute();
			int functionReturnValue = approvedApps.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {
				String error = approvedApps.getString(3);
				//// System.out.println("ApplicationDAO Line number4112"+error);
				approvedApps.close();
				approvedApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet approvedAppsResults = (ResultSet) approvedApps
						.getObject(2);
				Application aApplication = null;
				BorrowerDetails aBorrowerDetails = null;
				SSIDetails aSsiDetails = null;
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"Just Before ");
				int ritesh = 0;
				while (approvedAppsResults.next()) {
					// ritesh++;
					aApplication = new Application();
					aBorrowerDetails = new BorrowerDetails();
					aSsiDetails = new SSIDetails();
					aApplication.setCgpan(approvedAppsResults.getString(1));
					aApplication.setAppRefNo(approvedAppsResults.getString(2));
					//// System.out.println("Line number 4778 App Ref No:"+approvedAppsResults.getString(2));
					aSsiDetails.setCgbid(approvedAppsResults.getString(3));
					aSsiDetails.setSsiName(approvedAppsResults.getString(4));
					aSsiDetails.setRegNo(approvedAppsResults.getString(5));
					aSsiDetails
							.setCpFirstName(approvedAppsResults.getString(6));
					aSsiDetails.setCpMiddleName(approvedAppsResults
							.getString(7));
					aSsiDetails.setCpLastName(approvedAppsResults.getString(8));
					aSsiDetails.setAddress(approvedAppsResults.getString(9));
					aApplication.setBankId(approvedAppsResults.getString(10));
					aApplication.setZoneId("0000");
					aApplication.setBranchId("0000");
					aApplication.setLoanType(approvedAppsResults.getString(13));
					aSsiDetails.setCpITPAN(approvedAppsResults.getString(14));
					aSsiDetails.setState(approvedAppsResults.getString(15));
					aSsiDetails.setDistrict(approvedAppsResults.getString(16));
					aApplication.setMliRefNo(approvedAppsResults.getString(17));
					aBorrowerDetails.setSsiDetails(aSsiDetails);
					aApplication.setBorrowerDetails(aBorrowerDetails);
					aApplication.setExistSSI(approvedAppsResults.getString(18));
					if (approvedAppsResults.getString(13).equals("TC")
							|| approvedAppsResults.getString(13).equals("CC")) {
						tcApprovedAppList.add(aApplication);
					} else if (approvedAppsResults.getString(13).equals("WC")) {
						wcApprovedAppList.add(aApplication);
					}
				}
				//// System.out.println("value of ritesh  = "+ritesh);
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"After");
				approvedAppsResults.close();
				aApplication = null;
				aBorrowerDetails = null;
				aSsiDetails = null;
				approvedAppsResults = null;
				approvedApps.close();
				approvedApps = null;
			}

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicatePath",
					"After getting all approved applications");

			CallableStatement pendingApps = connection
					.prepareCall("{?=call packGetPackagePath050815.funcGetAllAppPathTEST(?,?,?)}");
			pendingApps.registerOutParameter(1, Types.INTEGER);
			pendingApps.registerOutParameter(2, Constants.CURSOR);
			pendingApps.registerOutParameter(3, Types.VARCHAR);
			pendingApps.setString(4, bankName);
			pendingApps.execute();
			int functionReturnValues = pendingApps.getInt(1);
			if (functionReturnValues == Constants.FUNCTION_FAILURE) {
				String error = pendingApps.getString(3);
				pendingApps.close();
				pendingApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsResults = (ResultSet) pendingApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				while (pendingAppsResults.next()) {
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					if (pendingAppsResults.getString(1) != null
							&& !(pendingAppsResults.getString(1).equals(""))) {
						pApplication.setCgpan(pendingAppsResults.getString(1));
					}
					pApplication.setAppRefNo(pendingAppsResults.getString(2));
					pSsiDetails.setCgbid(pendingAppsResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsResults.getString(4));
					pSsiDetails.setRegNo(pendingAppsResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsResults.getString(6));
					pSsiDetails
							.setCpMiddleName(pendingAppsResults.getString(7));
					pSsiDetails.setCpLastName(pendingAppsResults.getString(8));
					pSsiDetails.setAddress(pendingAppsResults.getString(9));
					pApplication.setBankId(pendingAppsResults.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsResults.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsResults.getString(14));
					pSsiDetails.setState(pendingAppsResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsResults.getString(16));
					pApplication.setMliRefNo(pendingAppsResults.getString(17));
					pApplication.setPrevSSI(pendingAppsResults.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsResults.getString(13).equals("TC")
							|| pendingAppsResults.getString(13).equals("CC")) {
						tcPendingAppList.add(pApplication);
					} else if (pendingAppsResults.getString(13).equals("WC")) {
						wcPendingAppList.add(pApplication);
					}
				}
				pendingAppsResults.close();
				pendingAppsResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingApps.close();
				pendingApps = null;
			}
			// Log.log(Log.DEBUG,"ApplicationDAO","checkDuplicatePath","After getting all pending applications");
			/**
			 * This block returns the applications with status 'PE'
			 */
			CallableStatement pendingStatusApps = connection
					.prepareCall("{?=call packGetPackagePath.funcGetPendingStatusPath(?,?,?)}");

			pendingStatusApps.registerOutParameter(1, Types.INTEGER);
			pendingStatusApps.registerOutParameter(2, Constants.CURSOR);
			pendingStatusApps.registerOutParameter(3, Types.VARCHAR);
			pendingStatusApps.setString(4, bankName);
			pendingStatusApps.execute();
			int functionReturnVal = pendingStatusApps.getInt(1);
			if (functionReturnVal == Constants.FUNCTION_FAILURE) {
				String error = pendingStatusApps.getString(3);
				pendingStatusApps.close();
				pendingStatusApps = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsStatusResults = (ResultSet) pendingStatusApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				while (pendingAppsStatusResults.next()) {
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsStatusResults
							.getString(2));
					pSsiDetails.setCgbid(pendingAppsStatusResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsStatusResults
							.getString(4));
					pSsiDetails.setRegNo(pendingAppsStatusResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsStatusResults
							.getString(6));
					pSsiDetails.setCpMiddleName(pendingAppsStatusResults
							.getString(7));
					pSsiDetails.setCpLastName(pendingAppsStatusResults
							.getString(8));
					pSsiDetails.setAddress(pendingAppsStatusResults
							.getString(9));
					pApplication.setBankId(pendingAppsStatusResults
							.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsStatusResults
							.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsStatusResults
							.getString(14));
					pSsiDetails
							.setState(pendingAppsStatusResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsStatusResults
							.getString(16));
					pApplication.setMliRefNo(pendingAppsStatusResults
							.getString(17));
					pApplication.setPrevSSI(pendingAppsStatusResults
							.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsStatusResults.getString(13).equals("TC")
							|| pendingAppsStatusResults.getString(13).equals(
									"CC")) {
						tcPendingAppList.add(pApplication);
					} else if (pendingAppsStatusResults.getString(13).equals(
							"WC")) {
						wcPendingAppList.add(pApplication);
					}
				}
				pendingAppsStatusResults.close();
				pendingAppsStatusResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingStatusApps.close();
				pendingStatusApps = null;
			}
			// Log.log(Log.DEBUG,"ApplicationDAO","checkDuplicatePath","After getting all pending status");
			HashMap tcApprovedApplications = new HashMap();
			HashMap wcApprovedApplications = new HashMap();
			HashMap tcPendingApplications = new HashMap();
			HashMap wcPendingApplications = new HashMap();

			ArrayList tcApprovedApplicationsList = new ArrayList();
			ArrayList wcApprovedApplicationsList = new ArrayList();
			ArrayList tcPendingApplicationsList = new ArrayList();
			ArrayList wcPendingApplicationsList = new ArrayList();
			// Approved Application from the DB
			int tcApprovedListSize = tcApprovedAppList.size();
			int wcApprovedListSize = wcApprovedAppList.size();
			// Pending Application from the DB
			int tcPendingListSize = tcPendingAppList.size();
			int wcPendingListSize = wcPendingAppList.size();

			//// System.out.println("PATH  app dao returning tcApprovedAppList *****= "+tcApprovedAppList.size());
			//// System.out.println("PATH  returning wcApprovedAppList *****= "+wcApprovedAppList.size());
			//// System.out.println("PATH  returning tcPendingAppList *****= "+tcPendingAppList.size());
			//// System.out.println("PATH  returning wcPendingAppList *****= "+wcPendingAppList.size());
			/*
			 * This loop groups the term loan approved applications based on the
			 * MliID and adds them to a hashtable
			 */
			for (int i = 0; i < tcApprovedListSize; i++) {
				//// System.out.println("ritesh in tcApprovedListSize loop");
				Application tcApprovedApplication = (Application) tcApprovedAppList
						.get(i);
				bankId = tcApprovedApplication.getBankId();
				zoneId = tcApprovedApplication.getZoneId();
				branchId = tcApprovedApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (tcApprovedApplications.containsKey(mliIdString)) {
					tcApprovedApplicationsList = (ArrayList) tcApprovedApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else tcApprovedListSize loop");
					tcApprovedApplicationsList = new ArrayList();
				}
				tcApprovedApplicationsList.add(tcApprovedApplication);
				tcApprovedApplications.put(mliIdString,
						tcApprovedApplicationsList);
			}
			/*
			 * This loop groups the working capital approved applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int j = 0; j < wcApprovedListSize; j++) {
				//// System.out.println("ritesh in wcApprovedListSize loop");
				Application wcApprovedApplication = (Application) wcApprovedAppList
						.get(j);
				bankId = wcApprovedApplication.getBankId();
				zoneId = wcApprovedApplication.getZoneId();
				branchId = wcApprovedApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (wcApprovedApplications.containsKey(mliIdString)) {
					wcApprovedApplicationsList = (ArrayList) wcApprovedApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else wcApprovedListSize loop");
					wcApprovedApplicationsList = new ArrayList();
				}
				wcApprovedApplicationsList.add(wcApprovedApplication);
				wcApprovedApplications.put(mliIdString,
						wcApprovedApplicationsList);
			}
			/*
			 * This loop groups the term credit pending applications based on
			 * the MliID and adds them to a hashtable
			 */
			for (int k = 0; k < tcPendingListSize; k++) {
				//// System.out.println("ritesh in tcPendingListSize loop");
				Application tcPendingApplication = (Application) tcPendingAppList
						.get(k);

				bankId = tcPendingApplication.getBankId();
				zoneId = tcPendingApplication.getZoneId();
				branchId = tcPendingApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (tcPendingApplications.containsKey(mliIdString)) {
					tcPendingApplicationsList = (ArrayList) tcPendingApplications
							.get(mliIdString);
				} else {
					//// System.out.println("ritesh in else tcPendingListSize loop");
					tcPendingApplicationsList = new ArrayList();
				}
				tcPendingApplicationsList.add(tcPendingApplication);
				tcPendingApplications.put(mliIdString,
						tcPendingApplicationsList);
			}
			/*
			 * This loop groups the working capital pending applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int l = 0; l < wcPendingListSize; l++) {
				Application wcPendingApplication = (Application) wcPendingAppList
						.get(l);
				bankId = wcPendingApplication.getBankId();
				zoneId = wcPendingApplication.getZoneId();
				branchId = wcPendingApplication.getBranchId();
				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				if (wcPendingApplications.containsKey(mliIdString)) {
					wcPendingApplicationsList = (ArrayList) wcPendingApplications
							.get(mliIdString);
				} else {
					wcPendingApplicationsList = new ArrayList();
				}
				wcPendingApplicationsList.add(wcPendingApplication);
				wcPendingApplications.put(mliIdString,
						wcPendingApplicationsList);
			}
			approvedPendingApplications.put("tcApproved",
					tcApprovedApplications);
			approvedPendingApplications.put("wcApproved",
					wcApprovedApplications);
			approvedPendingApplications.put("tcPending", tcPendingApplications);
			approvedPendingApplications.put("wcPending", wcPendingApplications);

			tcApprovedApplications = null;
			wcApprovedApplications = null;
			tcPendingApplications = null;
			wcPendingApplications = null;
			tcApprovedApplicationsList = null;
			wcApprovedApplicationsList = null;
			tcPendingApplicationsList = null;
			wcPendingApplicationsList = null;
			bankId = null;
			zoneId = null;
			branchId = null;
			tcApprovedAppList = null;
			wcApprovedAppList = null;
			tcPendingAppList = null;
			wcPendingAppList = null;
			connection.commit();
		} catch (SQLException sqlException) {
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath",
						"Exception :" + ignore.getMessage());
			}
			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicatePath", "Exited");
		return approvedPendingApplications;
	}

	/**
	 * This method is used to check applications for their duplicity for the
	 * selected duplicate criteria. This duplicate check is done for an
	 * application across members in a state/district.
	 * 
	 * @param mliID
	 * @param duplicateCriteria
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 397A732202D8
	 */

	// bhu 1st
	public HashMap checkDuplicate() throws DatabaseException {
		// Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "Entered");

		Connection connection = DBConnection.getConnection(false);
		//// System.out.println("RITESH inside ApplicationDAO checkDuplicate");
		// Application application=new Application();
		// BorrowerDetails borrowerDetails=new BorrowerDetails();
		// SSIDetails ssiDetails=new SSIDetails();

		String bankId = "";
		String branchId = "";
		String zoneId = "";

		// ArrayList approvedPendingList=new ArrayList();
		ArrayList tcApprovedAppList = null;
		ArrayList wcApprovedAppList = null;

		ArrayList tcPendingAppList = new ArrayList();
		ArrayList wcPendingAppList = new ArrayList();

		HashMap approvedPendingApplications = new HashMap();

		int Tccount;
		int Wccount;

		try {
			/*
			 * Statement statement=connection.createStatement();
			 * 
			 * ResultSet results=statement.executeQuery(
			 * "SELECT        count(1), app.APP_LOAN_TYPE " +
			 * "FROM   application_detail app WHERE    app_status = '"
			 * +"AP"+"' "+ "AND app.APP_CREATED_MODIFIED_BY <> '"+"DEMOUSER"+
			 * "' group by app.APP_LOAN_TYPE");
			 */
			CallableStatement applicationCount = connection
					.prepareCall("{?=call packGetAppCount.funcGetAppCount(?,?)}");

			applicationCount.registerOutParameter(1, Types.INTEGER);
			applicationCount.registerOutParameter(3, Types.VARCHAR);

			applicationCount.registerOutParameter(2, Constants.CURSOR);

			applicationCount.executeQuery();
			int applicationCountValue = applicationCount.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
					"Application Count value :" + applicationCountValue);

			if (applicationCountValue == Constants.FUNCTION_FAILURE) {

				String error = applicationCount.getString(4);

				applicationCount.close();
				applicationCount = null;

				connection.rollback();

				// Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
				// "Application Count message:" + error);

				//// System.out.println("Error in Application DAO line number 4733"+error);

				throw new DatabaseException(error);
			} else {

				ResultSet results = (ResultSet) applicationCount.getObject(2);

				Tccount = 0;
				Wccount = 0;

				while (results.next()) {
					if (results.getString(2).equals("WC")) {
						Wccount += results.getInt(1);
					} else {
						Tccount += results.getInt(1);
					}

				}
				results.close();
				applicationCount.close();
				applicationCount = null;

				// Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
				// "Tccount,Wccount " + Tccount + ", " + Wccount);
			}

			// statement.close();

			tcApprovedAppList = new ArrayList(Tccount);
			wcApprovedAppList = new ArrayList(Wccount);
			CallableStatement approvedApps = connection
					.prepareCall("{?=call packGetApplications.funcGetApprovedApp(?,?)}");
			approvedApps.setFetchSize(6);
			approvedApps.registerOutParameter(1, Types.INTEGER);
			approvedApps.registerOutParameter(2, Constants.CURSOR);
			approvedApps.registerOutParameter(3, Types.VARCHAR);

			approvedApps.execute();

			int functionReturnValue = approvedApps.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = approvedApps.getString(3);

				approvedApps.close();
				approvedApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				// approvedApps.setFetchSize(Tccount+Wccount);
				ResultSet approvedAppsResults = (ResultSet) approvedApps
						.getObject(2);
				// approvedAppsResults.setFetchSize(Tccount+Wccount);
				//// System.out.println("PATH approvedAppsResults.getRow() = "+approvedAppsResults.getRow());
				Application aApplication = null;
				BorrowerDetails aBorrowerDetails = null;
				SSIDetails aSsiDetails = null;
				// Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
				// "Just Before ");
				int ritu = 0;
				// Start Changes by Path On 29Oct2006

				while (approvedAppsResults.next()) {

					ritu++;
					//// System.out.println("Ritu"+ritu);
					aApplication = new Application();
					aBorrowerDetails = new BorrowerDetails();
					aSsiDetails = new SSIDetails();

					aApplication.setCgpan(approvedAppsResults.getString(1));
					//// System.out.println("PATH approvedAppsResults.getString(1) "+approvedAppsResults.getString(1));
					aApplication.setAppRefNo(approvedAppsResults.getString(2));
					//// System.out.println("PATH approvedAppsResults.getString(2) "+approvedAppsResults.getString(2));
					aSsiDetails.setCgbid(approvedAppsResults.getString(3));
					//// System.out.println("PATH approvedAppsResults.getString(3) "+approvedAppsResults.getString(3));
					aSsiDetails.setSsiName(approvedAppsResults.getString(4));
					//// System.out.println("PATH approvedAppsResults.getString(4) "+approvedAppsResults.getString(4));
					aSsiDetails.setRegNo(approvedAppsResults.getString(5));
					//// System.out.println("PATH approvedAppsResults.getString(5) "+approvedAppsResults.getString(5));
					aSsiDetails
							.setCpFirstName(approvedAppsResults.getString(6));
					//// System.out.println("PATH approvedAppsResults.getString(6) "+approvedAppsResults.getString(6));
					aSsiDetails.setCpMiddleName(approvedAppsResults
							.getString(7));
					//// System.out.println("PATH approvedAppsResults.getString(7) "+approvedAppsResults.getString(7));
					aSsiDetails.setCpLastName(approvedAppsResults.getString(8));
					//// System.out.println("PATH approvedAppsResults.getString(8) "+approvedAppsResults.getString(8));
					aSsiDetails.setAddress(approvedAppsResults.getString(9));
					//// System.out.println("PATH approvedAppsResults.getString(9) "+approvedAppsResults.getString(9));
					aApplication.setBankId(approvedAppsResults.getString(10));
					//// System.out.println("PATH approvedAppsResults.getString(10) "+approvedAppsResults.getString(10));
					aApplication.setZoneId("0000");
					aApplication.setBranchId("0000");
					aApplication.setLoanType(approvedAppsResults.getString(13));
					//// System.out.println("PATH approvedAppsResults.getString(13) "+approvedAppsResults.getString(13));
					aSsiDetails.setCpITPAN(approvedAppsResults.getString(14));
					//// System.out.println("PATH approvedAppsResults.getString(14) "+approvedAppsResults.getString(14));
					aSsiDetails.setState(approvedAppsResults.getString(15));
					//// System.out.println("PATH approvedAppsResults.getString(15) "+approvedAppsResults.getString(15));
					aSsiDetails.setDistrict(approvedAppsResults.getString(16));
					//// System.out.println("PATH approvedAppsResults.getString(16) "+approvedAppsResults.getString(16));
					aApplication.setMliRefNo(approvedAppsResults.getString(17));
					aApplication.setPrevSSI(approvedAppsResults.getString(18));
					//// System.out.println("PATH approvedAppsResults.getString(17) "+approvedAppsResults.getString(17));
					//// System.out.println(" aApplication.setPrevSSI"+
					// aApplication.getPrevSSI());
					aBorrowerDetails.setSsiDetails(aSsiDetails);
					aApplication.setBorrowerDetails(aBorrowerDetails);

					// //////////////////////////////////

					//// System.out.println(approvedAppsResults.toString());
					// Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
					// "approvedAppsResults" + approvedAppsResults);
					if (approvedAppsResults.getString(13).equals("TC")
							|| approvedAppsResults.getString(13).equals("CC")) {
						tcApprovedAppList.add(aApplication);
						//// System.out.println("PATH tcApprovedAppList ");
					} else if (approvedAppsResults.getString(13).equals("WC")) {
						wcApprovedAppList.add(aApplication);
						//// System.out.println("PATH wcApprovedAppList ");
					}

				}
				//// System.out.println("PATH value of approved Apps ritu = "+ritu);
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "After");
				approvedAppsResults.close();
				aApplication = null;
				aBorrowerDetails = null;
				aSsiDetails = null;
				approvedAppsResults = null;
				approvedApps.close();
				approvedApps = null;

			}

			// Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
			// "After getting all approved applications");
			CallableStatement pendingApps = connection
					.prepareCall("{?=call packGetApplications.funcGetPendingApp(?,?)}");

			pendingApps.registerOutParameter(1, Types.INTEGER);
			pendingApps.registerOutParameter(2, Constants.CURSOR);
			pendingApps.registerOutParameter(3, Types.VARCHAR);

			pendingApps.execute();

			int functionReturnValues = pendingApps.getInt(1);
			if (functionReturnValues == Constants.FUNCTION_FAILURE) {

				String error = pendingApps.getString(3);

				pendingApps.close();
				pendingApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsResults = (ResultSet) pendingApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				int ritu1 = 0;
				while (pendingAppsResults.next()) {
					ritu1++;
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					if (pendingAppsResults.getString(1) != null
							&& !(pendingAppsResults.getString(1).equals(""))) {
						pApplication.setCgpan(pendingAppsResults.getString(1));
					}
					pApplication.setAppRefNo(pendingAppsResults.getString(2));
					pSsiDetails.setCgbid(pendingAppsResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsResults.getString(4));
					pSsiDetails.setRegNo(pendingAppsResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsResults.getString(6));
					pSsiDetails
							.setCpMiddleName(pendingAppsResults.getString(7));
					pSsiDetails.setCpLastName(pendingAppsResults.getString(8));
					pSsiDetails.setAddress(pendingAppsResults.getString(9));
					pApplication.setBankId(pendingAppsResults.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsResults.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsResults.getString(14));
					pSsiDetails.setState(pendingAppsResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsResults.getString(16));
					pApplication.setMliRefNo(pendingAppsResults.getString(17));
					pApplication.setExistSSI(pendingAppsResults.getString(18));

					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsResults.getString(13).equals("TC")
							|| pendingAppsResults.getString(13).equals("CC")) {
						tcPendingAppList.add(pApplication);
						//// System.out.println("PATH tcPendingAppList");
					} else if (pendingAppsResults.getString(13).equals("WC")) {
						wcPendingAppList.add(pApplication);
						//// System.out.println("PATH wcPendingAppList");
					}

				}
				//// System.out.println("PATH ritu1 pendigng apps = "+ritu1);
				pendingAppsResults.close();
				pendingAppsResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingApps.close();
				pendingApps = null;

			}

			// Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
			// "After getting all pending applications");
			/**
			 * This block returns the applications with status 'PE'
			 */

			CallableStatement pendingStatusApps = connection
					.prepareCall("{?=call packGetApplications.funcGetPendingAppStatus(?,?)}");

			pendingStatusApps.registerOutParameter(1, Types.INTEGER);
			pendingStatusApps.registerOutParameter(2, Constants.CURSOR);
			pendingStatusApps.registerOutParameter(3, Types.VARCHAR);

			pendingStatusApps.execute();

			int functionReturnVal = pendingStatusApps.getInt(1);
			if (functionReturnVal == Constants.FUNCTION_FAILURE) {

				String error = pendingStatusApps.getString(3);

				pendingStatusApps.close();
				pendingStatusApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsStatusResults = (ResultSet) pendingStatusApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				int ritu2 = 0;
				while (pendingAppsStatusResults.next()) {
					ritu2++;
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsStatusResults
							.getString(2));
					pSsiDetails.setCgbid(pendingAppsStatusResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsStatusResults
							.getString(4));
					pSsiDetails.setRegNo(pendingAppsStatusResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsStatusResults
							.getString(6));
					pSsiDetails.setCpMiddleName(pendingAppsStatusResults
							.getString(7));
					pSsiDetails.setCpLastName(pendingAppsStatusResults
							.getString(8));
					pSsiDetails.setAddress(pendingAppsStatusResults
							.getString(9));
					pApplication.setBankId(pendingAppsStatusResults
							.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsStatusResults
							.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsStatusResults
							.getString(14));
					pSsiDetails
							.setState(pendingAppsStatusResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsStatusResults
							.getString(16));
					pApplication.setMliRefNo(pendingAppsStatusResults
							.getString(17));
					// pApplication.setPrevSSI(pendingAppsStatusResults.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);

					if (pendingAppsStatusResults.getString(13).equals("TC")
							|| pendingAppsStatusResults.getString(13).equals(
									"CC")) {
						tcPendingAppList.add(pApplication);
						//// System.out.println("PATH tcPendingAppList");
					} else if (pendingAppsStatusResults.getString(13).equals(
							"WC")) {
						wcPendingAppList.add(pApplication);
						//// System.out.println("PATH wcPendingAppList");
					}

				}
				//// System.out.println("PATH ritu2 pendigng apps status = "+ritu2);
				pendingAppsStatusResults.close();
				pendingAppsStatusResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingStatusApps.close();
				pendingStatusApps = null;

			}

			// Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
			// "After getting all pending status");

			HashMap tcApprovedApplications = new HashMap();
			HashMap wcApprovedApplications = new HashMap();

			HashMap tcPendingApplications = new HashMap();
			HashMap wcPendingApplications = new HashMap();

			ArrayList tcApprovedApplicationsList = new ArrayList();
			ArrayList wcApprovedApplicationsList = new ArrayList();

			ArrayList tcPendingApplicationsList = new ArrayList();
			ArrayList wcPendingApplicationsList = new ArrayList();

			// Approved Application from the DB
			int tcApprovedListSize = tcApprovedAppList.size();
			int wcApprovedListSize = wcApprovedAppList.size();

			// Pending Application from the DB
			int tcPendingListSize = tcPendingAppList.size();
			int wcPendingListSize = wcPendingAppList.size();
			//// System.out.println("PATH tcApprovedListSize = "+tcApprovedListSize);
			//// System.out.println("PATH wcApprovedListSize = "+wcApprovedListSize);
			//// System.out.println("PATH tcPendingListSize = "+tcPendingListSize);
			//// System.out.println("PATH wcPendingListSize = "+wcPendingListSize);
			/*
			 * This loop groups the term loan approved applications based on the
			 * MliID and adds them to a hashtable
			 */
			for (int i = 0; i < tcApprovedListSize; i++) {

				Application tcApprovedApplication = (Application) tcApprovedAppList
						.get(i);
				// String cgpan=tcApprovedApplication.getCgpan();
				bankId = tcApprovedApplication.getBankId();
				zoneId = tcApprovedApplication.getZoneId();
				branchId = tcApprovedApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString tcApprovedListSize = "+mliIdString);
				if (tcApprovedApplications.containsKey(mliIdString)) {
					tcApprovedApplicationsList = (ArrayList) tcApprovedApplications
							.get(mliIdString);
				} else {
					tcApprovedApplicationsList = new ArrayList();
				}

				tcApprovedApplicationsList.add(tcApprovedApplication);
				tcApprovedApplications.put(mliIdString,
						tcApprovedApplicationsList);
			}
			/*
			 * This loop groups the working capital approved applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int j = 0; j < wcApprovedListSize; j++) {

				Application wcApprovedApplication = (Application) wcApprovedAppList
						.get(j);
				// String cgpan=wcApprovedApplication.getCgpan();
				bankId = wcApprovedApplication.getBankId();
				zoneId = wcApprovedApplication.getZoneId();
				branchId = wcApprovedApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString wcApprovedListSize = "+mliIdString);
				if (wcApprovedApplications.containsKey(mliIdString)) {
					wcApprovedApplicationsList = (ArrayList) wcApprovedApplications
							.get(mliIdString);
				} else {
					wcApprovedApplicationsList = new ArrayList();
				}

				wcApprovedApplicationsList.add(wcApprovedApplication);
				wcApprovedApplications.put(mliIdString,
						wcApprovedApplicationsList);
			}

			/*
			 * This loop groups the term credit pending applications based on
			 * the MliID and adds them to a hashtable
			 */

			for (int k = 0; k < tcPendingListSize; k++) {
				Application tcPendingApplication = (Application) tcPendingAppList
						.get(k);

				bankId = tcPendingApplication.getBankId();
				zoneId = tcPendingApplication.getZoneId();
				branchId = tcPendingApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString tcPendingListSize = "+mliIdString);
				if (tcPendingApplications.containsKey(mliIdString)) {
					tcPendingApplicationsList = (ArrayList) tcPendingApplications
							.get(mliIdString);
				} else {

					tcPendingApplicationsList = new ArrayList();
				}

				tcPendingApplicationsList.add(tcPendingApplication);
				tcPendingApplications.put(mliIdString,
						tcPendingApplicationsList);

			}

			/*
			 * This loop groups the working capital pending applications based
			 * on the MliID and adds them to a hashtable
			 */

			for (int l = 0; l < wcPendingListSize; l++) {
				Application wcPendingApplication = (Application) wcPendingAppList
						.get(l);

				bankId = wcPendingApplication.getBankId();
				zoneId = wcPendingApplication.getZoneId();
				branchId = wcPendingApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString wcPendingListSize = "+mliIdString);
				if (wcPendingApplications.containsKey(mliIdString)) {
					wcPendingApplicationsList = (ArrayList) wcPendingApplications
							.get(mliIdString);
				} else {

					wcPendingApplicationsList = new ArrayList();
				}

				wcPendingApplicationsList.add(wcPendingApplication);

				wcPendingApplications.put(mliIdString,
						wcPendingApplicationsList);

			}

			approvedPendingApplications.put("tcApproved",
					tcApprovedApplications);
			approvedPendingApplications.put("wcApproved",
					wcApprovedApplications);

			approvedPendingApplications.put("tcPending", tcPendingApplications);
			approvedPendingApplications.put("wcPending", wcPendingApplications);

			tcApprovedApplications = null;
			wcApprovedApplications = null;

			tcPendingApplications = null;
			wcPendingApplications = null;

			tcApprovedApplicationsList = null;
			wcApprovedApplicationsList = null;
			tcPendingApplicationsList = null;
			wcPendingApplicationsList = null;

			bankId = null;
			zoneId = null;
			branchId = null;
			tcApprovedAppList = null;
			wcApprovedAppList = null;
			tcPendingAppList = null;
			wcPendingAppList = null;

			connection.commit();

		} catch (SQLException sqlException) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				// Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
				// "Exception :" + ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "Exited");

		return approvedPendingApplications;

	}

	/**
	 * This method is used to fetch all the pending applications for eligibility
	 * check for the CGTSI users decision
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 397A75F2003E
	 */
	public Collection getPendingApps() throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		ArrayList pendingAppsList = new ArrayList();

		/*
		 * String bankId=""; String branchId=""; String zoneId="";
		 */
		try {

			CallableStatement pendingApps = connection
					.prepareCall("{?=call packGetApplications.funcGetPendingApp(?,?)}");

			pendingApps.registerOutParameter(1, Types.INTEGER);
			pendingApps.registerOutParameter(2, Constants.CURSOR);
			pendingApps.registerOutParameter(3, Types.VARCHAR);

			pendingApps.execute();

			int functionReturnValues = pendingApps.getInt(1);
			if (functionReturnValues == Constants.FUNCTION_FAILURE) {

				String error = pendingApps.getString(3);

				pendingApps.close();
				pendingApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsResults = (ResultSet) pendingApps
						.getObject(2);
				while (pendingAppsResults.next()) {
					Application pApplication = new Application();
					BorrowerDetails pBorrowerDetails = new BorrowerDetails();
					SSIDetails pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsResults.getString(2));
					pSsiDetails.setCgbid(pendingAppsResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsResults.getString(4));
					pSsiDetails.setRegNo(pendingAppsResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsResults.getString(6));
					pSsiDetails
							.setCpMiddleName(pendingAppsResults.getString(7));
					pSsiDetails.setCpLastName(pendingAppsResults.getString(8));
					pSsiDetails.setAddress(pendingAppsResults.getString(9));

					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					pendingAppsList.add(pApplication);

				}

				pendingAppsResults.close();
				pendingAppsResults = null;
				pendingApps.close();
				pendingApps = null;

			}

			/**
			 * This block returns the applications with status 'PE'
			 */

			CallableStatement pendingStatusApps = connection
					.prepareCall("{?=call packGetApplications.funcGetPendingAppStatus(?,?)}");

			pendingStatusApps.registerOutParameter(1, Types.INTEGER);
			pendingStatusApps.registerOutParameter(2, Constants.CURSOR);
			pendingStatusApps.registerOutParameter(3, Types.VARCHAR);

			pendingStatusApps.execute();

			int functionReturnVal = pendingStatusApps.getInt(1);
			if (functionReturnVal == Constants.FUNCTION_FAILURE) {

				String error = pendingStatusApps.getString(3);

				pendingStatusApps.close();
				pendingStatusApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsStatusResults = (ResultSet) pendingStatusApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				while (pendingAppsStatusResults.next()) {
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsStatusResults
							.getString(2));
					pSsiDetails.setCgbid(pendingAppsStatusResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsStatusResults
							.getString(4));
					pSsiDetails.setRegNo(pendingAppsStatusResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsStatusResults
							.getString(6));
					pSsiDetails.setCpMiddleName(pendingAppsStatusResults
							.getString(7));
					pSsiDetails.setCpLastName(pendingAppsStatusResults
							.getString(8));
					pSsiDetails.setAddress(pendingAppsStatusResults
							.getString(9));

					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					pendingAppsList.add(pApplication);

				}
				pendingAppsStatusResults.close();
				pendingAppsStatusResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingStatusApps.close();
				pendingStatusApps = null;

			}

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getPendingApps",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getPendingApps",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return pendingAppsList;

	}

	/**
	 * This method gets all the applications pending for eligibility check
	 * 
	 * @param apps
	 * @throws DatabaseException
	 */

	public EligibleApplication getAppsForEligibilityCheck(String appRefNo)
			throws DatabaseException {

		// Log.log(Log.INFO,"ApplicationDAO","getAppsForEligibilityCheck","Entered eligibility check :"
		// + appRefNo);
		Connection connection = DBConnection.getConnection(false);

		EligibleApplication eligibleApplication = new EligibleApplication();

		// Application application=new Application();

		try {

			CallableStatement ineligibleApps = connection
					.prepareCall("{?=call funcEvaluateEligibilyCriteria(?,?,?,?,?)}");

			ineligibleApps.registerOutParameter(1, Types.INTEGER);
			ineligibleApps.registerOutParameter(6, Types.VARCHAR);
			ineligibleApps.registerOutParameter(3, Types.VARCHAR);
			ineligibleApps.registerOutParameter(4, Types.VARCHAR);
			ineligibleApps.registerOutParameter(5, Types.VARCHAR);

			ineligibleApps.setString(2, appRefNo);

			ineligibleApps.execute();

			int functionReturnValues = ineligibleApps.getInt(1);

			if (functionReturnValues == Constants.FUNCTION_FAILURE) {

				String error = ineligibleApps.getString(3);

				ineligibleApps.close();
				ineligibleApps = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				if ((ineligibleApps.getString(3)) != null) {

					eligibleApplication.setPassedCondition((ineligibleApps
							.getString(3)).substring(1));
				} else {

					eligibleApplication.setPassedCondition("");
				}

				if ((ineligibleApps.getString(4)) != null) {
					eligibleApplication.setFailedCondition((ineligibleApps
							.getString(4)).substring(1));
				} else {

					eligibleApplication.setFailedCondition("");
				}

				if ((ineligibleApps.getString(5)) != null) {
					eligibleApplication
							.setMessage((ineligibleApps.getString(5))
									.substring(1));
				} else {

					eligibleApplication.setMessage("");
				}
				ineligibleApps.close();
				ineligibleApps = null;
			}

			connection.commit();
		} catch (SQLException sqlException) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO",
						"getAppsForEligibilityCheck",
						"Exception ;" + ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		// Log.log(Log.INFO,"ApplicationDAO","getAppsForEligibilityCheck","Exited"
		// );

		return eligibleApplication;

	}

	/**
	 * This method updates the status of the applications after the approval
	 * taken place. Applications could be rejected, held for want of more
	 * details, accepted (Approved).This method updates the status of the
	 * application in the database.
	 * 
	 * @param apps
	 * @throws DatabaseException
	 * @roseuid 397A8A7D03C5
	 */
	public void saveAuthorizedApps(ArrayList apps) throws DatabaseException {

	}

	/**
	 * This method is used to move the approved/rejected application from temp
	 * database to internet and intranet databases.
	 * 
	 * @param apps
	 * @return Boolean
	 * @throws DatabaseException
	 * @roseuid 397A9D31023A
	 */
	public boolean moveAppToIntranet(Application apps) throws DatabaseException {
		return true;
	}

	/**
	 * This method returns the application for a combination of MLI id, CGBID
	 * and CGPAN. If more than one parameter is available, then both the
	 * parameter would be combined in the search query to get the Application.
	 * If only MLI id or CGBID s present, list of CGPANs and Application
	 * Reference numbers would be given.
	 * 
	 * @param mliID
	 * @param cgbid
	 * @param cgpan
	 * @return com.cgtsi.application.Application
	 * @throws NoApplicationFoundException
	 * @throws DatabaseException
	 * @roseuid 398265930137
	 * 
	 *          public Application getApplication(String mliID, String cgbid,
	 *          String cgpan) throws NoApplicationFoundException,
	 *          DatabaseException { String appRefNo=null; Application
	 *          application
	 *          =getApplication(mliID,cgbid,cgpan,appRefNo,borrowerName);
	 * 
	 *          return application; }
	 */
	// ////////////////ADDED BY RITESH PATH ON 1DEC2006
	public Application getPartApplicationPath(String mliID, String cgpan,
			String appRefNo) throws NoApplicationFoundException,
			DatabaseException {

		// Log.log(Log.INFO,"ApplicationDAO","getPartApplicationPath","Entered. Memory : "+Runtime.getRuntime().freeMemory());
		//// System.out.println("PATH Inside ApplicationDAO  getPartApplication");
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		// TermLoan termLoan=new TermLoan();
		// WorkingCapital workingCapital = new WorkingCapital();

		Connection connection = DBConnection.getConnection(false);

		// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","connection in Application DAO in get Part Application"+connection);

		// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","After entering in the method");

		try {

			// if mli and app ref no are entered
			if ((mliID == null && !(appRefNo.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(appRefNo
							.equals("")))) {
				// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","when mliID and app ref No are entered");
				application = getAppForAppRef(mliID, appRefNo);

			}
			// Check here the value by Path 11Oct06
			// if mli and cgpan are entered
			else if ((mliID == null && !(cgpan.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(cgpan
							.equals("")))) {
				// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","when mliID and cgpan are entered");
				application = getAppForCgpan(mliID, cgpan);
				//// System.out.println("Comming out PATH from application=getAppForCgpan(mliID,cgpan) with application");
			}

			projectOutlayDetails = application.getProjectOutlayDetails();
			appRefNo = application.getAppRefNo();

			//// System.out.println("in  getPartApplicationPath");
			// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","values for application object set successfully...");
			//// System.out.println("PATH is here in appDAO with app_ref_no "+appRefNo);
			//// System.out.println("PATH is here in appDAO with region "+region);
			// SSI Details
			CallableStatement ssiObj = connection.prepareCall(
			// "{?=call funcGetSSIDetailforAppRefPath(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

					"{?=call funcGetSSIDetailforAppRefPath(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			ssiObj.setString(2, appRefNo); // AppRefNo

			ssiObj.registerOutParameter(1, Types.INTEGER);
			ssiObj.registerOutParameter(34, Types.VARCHAR);

			ssiObj.registerOutParameter(3, Types.INTEGER); // SSI Ref No
			ssiObj.registerOutParameter(4, Types.VARCHAR); // Already Covered by
															// CGTSI
			ssiObj.registerOutParameter(5, Types.VARCHAR); // Already assisted
															// by bank
			ssiObj.registerOutParameter(6, Types.VARCHAR); // SSI Unique No
			ssiObj.registerOutParameter(7, Types.VARCHAR); // NPA
			ssiObj.registerOutParameter(8, Types.VARCHAR); // Constitution
			ssiObj.registerOutParameter(9, Types.VARCHAR); // SSI Type
			ssiObj.registerOutParameter(10, Types.VARCHAR); // unit Name
			ssiObj.registerOutParameter(11, Types.VARCHAR); // SSI reg no
			ssiObj.registerOutParameter(12, Types.DATE); // Commencement Date
			ssiObj.registerOutParameter(13, Types.VARCHAR); // SSI ITPAN
			ssiObj.registerOutParameter(14, Types.VARCHAR); // type of Activity
			ssiObj.registerOutParameter(15, Types.INTEGER); // No Of Employees
			ssiObj.registerOutParameter(16, Types.DOUBLE); // project Sales
			ssiObj.registerOutParameter(17, Types.DOUBLE); // Project Exports
			ssiObj.registerOutParameter(18, Types.VARCHAR); // SSI Address
			ssiObj.registerOutParameter(19, Types.VARCHAR); // SSi City
			ssiObj.registerOutParameter(20, Types.VARCHAR); // Pincode
			ssiObj.registerOutParameter(21, Types.VARCHAR); // Display Defaulter
			ssiObj.registerOutParameter(22, Types.VARCHAR); // SSI District
			ssiObj.registerOutParameter(23, Types.VARCHAR); // SSI State
			ssiObj.registerOutParameter(24, Types.VARCHAR); // Industry Nature
			ssiObj.registerOutParameter(25, Types.VARCHAR); // Indutry Sector
															// name
			ssiObj.registerOutParameter(26, Types.VARCHAR); // Status
			ssiObj.registerOutParameter(27, Types.VARCHAR); // BID
			ssiObj.registerOutParameter(28, Types.DOUBLE); // Outstanding Amount
			ssiObj.registerOutParameter(29, Types.VARCHAR); // MCGS Flag
			// ADDED BY RITESH ON 30NOV2006 TO ADD SANCTION DATE ON APPROAL PAGE
			/*
			 * ssiObj.registerOutParameter(31,Types.VARCHAR); //Term loan
			 * sanction date ////END CODE HERE BY RITESH ssiObj.executeQuery();
			 * int ssiObjValue=ssiObj.getInt(1);
			 * 
			 * //Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath",
			 * "SSI Details :" + ssiObjValue);
			 * 
			 * if(ssiObjValue==Constants.FUNCTION_FAILURE){
			 * 
			 * String error = ssiObj.getString(30);
			 * 
			 * ssiObj.close(); ssiObj=null;
			 * 
			 * connection.rollback();
			 * 
			 * throw new DatabaseException("Application does not exist"); } else
			 * { ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
			 * borrowerDetails.setPreviouslyCovered(ssiObj.getString(4).trim());
			 * borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
			 * borrowerDetails.setNpa(ssiObj.getString(7).trim());
			 * ssiDetails.setConstitution(ssiObj.getString(8));
			 * ssiDetails.setSsiType(ssiObj.getString(9).trim());
			 * ssiDetails.setSsiName(ssiObj.getString(10));
			 * ssiDetails.setRegNo(ssiObj.getString(11));
			 * ssiDetails.setSsiITPan(ssiObj.getString(13));
			 * ssiDetails.setActivityType(ssiObj.getString(14));
			 * ssiDetails.setEmployeeNos(ssiObj.getInt(15));
			 * ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
			 * ssiDetails.setProjectedExports(ssiObj.getDouble(17));
			 * ssiDetails.setAddress(ssiObj.getString(18));
			 * ssiDetails.setCity(ssiObj.getString(19));
			 * ssiDetails.setPincode(ssiObj.getString(20).trim());
			 * ssiDetails.setDistrict(ssiObj.getString(22));
			 * ssiDetails.setState(ssiObj.getString(23));
			 * ssiDetails.setIndustryNature(ssiObj.getString(24));
			 * ssiDetails.setIndustrySector(ssiObj.getString(25));
			 * ssiDetails.setCgbid(ssiObj.getString(27)); // ADDED BY RITESH ON
			 * 30NOV2006 TO ADD SANCTION DATE ON APPROVAL PAGE
			 * ssiDetails.setSancDate_new(ssiObj.getString(30)); // END CODE
			 * HERE BY RITESH borrowerDetails.setOsAmt(ssiObj.getDouble(28));
			 */

			ssiObj.registerOutParameter(30, Types.VARCHAR); //
			ssiObj.registerOutParameter(31, Types.VARCHAR); //
			ssiObj.registerOutParameter(32, Types.VARCHAR); //
			ssiObj.registerOutParameter(33, Types.VARCHAR);

			ssiObj.executeQuery();
			int ssiObjValue = ssiObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
					"SSI Details :" + ssiObjValue);

			if (ssiObjValue == Constants.FUNCTION_FAILURE) {

				// String error = ssiObj.getString(30);

				String error = ssiObj.getString(34);

				ssiObj.close();
				ssiObj = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
				borrowerDetails
						.setPreviouslyCovered(ssiObj.getString(4).trim());
				borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
				borrowerDetails.setNpa(ssiObj.getString(7).trim());
				ssiDetails.setConstitution(ssiObj.getString(8));
				ssiDetails.setSsiType(ssiObj.getString(9).trim());
				ssiDetails.setSsiName(ssiObj.getString(10));
				ssiDetails.setRegNo(ssiObj.getString(11));
				ssiDetails.setSsiITPan(ssiObj.getString(13));
				ssiDetails.setActivityType(ssiObj.getString(14));
				ssiDetails.setEmployeeNos(ssiObj.getInt(15));
				ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
				ssiDetails.setProjectedExports(ssiObj.getDouble(17));
				ssiDetails.setAddress(ssiObj.getString(18));
				ssiDetails.setCity(ssiObj.getString(19));
				ssiDetails.setPincode(ssiObj.getString(20).trim());
				ssiDetails.setDistrict(ssiObj.getString(22));
				ssiDetails.setState(ssiObj.getString(23));
				ssiDetails.setIndustryNature(ssiObj.getString(24));
				ssiDetails.setIndustrySector(ssiObj.getString(25));
				ssiDetails.setCgbid(ssiObj.getString(27));

				// kot
				// ssiDetails.setEnterprise("N");

				// ssiDetails.setUnitAssisted("Y");

				// ssiDetails.setWomenOperated("Y");
				
				ssiDetails.setSancDate_new(ssiObj.getString(30));
				ssiDetails.setEnterprise(ssiObj.getString(31));

				ssiDetails.setUnitAssisted(ssiObj.getString(32));

				ssiDetails.setWomenOperated(ssiObj.getString(33));

				borrowerDetails.setOsAmt(ssiObj.getDouble(28));

				// Log.log(Log.DEBUG,"ApplicationDAO","getPartApplicationPath","OS Amount :"
				// + ssiObj.getDouble(28));
				borrowerDetails.setSsiDetails(ssiDetails);
				application.setBorrowerDetails(borrowerDetails);
				ssiObj.close();
				ssiObj = null;

			}
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();
			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);
			ssiDetails = null;
			borrowerDetails = null;

			// Log.log(Log.INFO,"ApplicationDAO","getPartApplicationPath","values of ssi object set successfully....");

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "getPartApplicationPath",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "getPartApplicationPath",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		// Log.log(Log.INFO,"ApplicationDAO","getPartApplicationPath","Exited. Memory : "+Runtime.getRuntime().freeMemory());

		return application;
	}

	// ////////////////END CODE BY RITESH PATH///////////////
	public Application getPartApplication(String mliID, String cgpan,
			String appRefNo) throws NoApplicationFoundException,
			DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "getPartApplication",
				"Entered. Memory : " + Runtime.getRuntime().freeMemory());
		//// System.out.println("PATH Inside ApplicationDAO  getPartApplication");
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		// TermLoan termLoan=new TermLoan();
		// WorkingCapital workingCapital = new WorkingCapital();

		Connection connection = DBConnection.getConnection(false);

		Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
				"connection in Application DAO in get Part Application"
						+ connection);

		Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
				"After entering in the method");

		try {

			// if mli and app ref no are entered
			if ((mliID == null && !(appRefNo.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(appRefNo
							.equals("")))) {
				Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
						"when mliID and app ref No are entered");
				//// System.out.println("ritesh before getAppForAppRef appRefNo= "+appRefNo);
				application = getAppForAppRef(mliID, appRefNo);
				//// System.out.println("ritesh after getAppForAppRefappRefNo= "+appRefNo);

			}
			// Check here the value by Path 11Oct06
			// if mli and cgpan are entered
			else if ((mliID == null && !(cgpan.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(cgpan
							.equals("")))) {
				Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
						"when mliID and cgpan are entered");
				application = getAppForCgpan(mliID, cgpan);
				//// System.out.println("Comming out PATH from application=getAppForCgpan(mliID,cgpan) with application");
			}

			projectOutlayDetails = application.getProjectOutlayDetails();
			appRefNo = application.getAppRefNo();

			//// System.out.println("in  getPartApplication");

			Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
					"values for application object set successfully...");

			// SSI Details
			CallableStatement ssiObj = connection.prepareCall(
			// "{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
					"{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			ssiObj.setString(2, appRefNo); // AppRefNo

			ssiObj.registerOutParameter(1, Types.INTEGER);
			ssiObj.registerOutParameter(33, Types.VARCHAR);

			ssiObj.registerOutParameter(3, Types.INTEGER); // SSI Ref No
			ssiObj.registerOutParameter(4, Types.VARCHAR); // Already Covered by
															// CGTSI
			ssiObj.registerOutParameter(5, Types.VARCHAR); // Already assisted
															// by bank
			ssiObj.registerOutParameter(6, Types.VARCHAR); // SSI Unique No
			ssiObj.registerOutParameter(7, Types.VARCHAR); // NPA
			ssiObj.registerOutParameter(8, Types.VARCHAR); // Constitution
			ssiObj.registerOutParameter(9, Types.VARCHAR); // SSI Type
			ssiObj.registerOutParameter(10, Types.VARCHAR); // unit Name
			ssiObj.registerOutParameter(11, Types.VARCHAR); // SSI reg no
			ssiObj.registerOutParameter(12, Types.DATE); // Commencement Date
			ssiObj.registerOutParameter(13, Types.VARCHAR); // SSI ITPAN
			ssiObj.registerOutParameter(14, Types.VARCHAR); // type of Activity
			ssiObj.registerOutParameter(15, Types.INTEGER); // No Of Employees
			ssiObj.registerOutParameter(16, Types.DOUBLE); // project Sales
			ssiObj.registerOutParameter(17, Types.DOUBLE); // Project Exports
			ssiObj.registerOutParameter(18, Types.VARCHAR); // SSI Address
			ssiObj.registerOutParameter(19, Types.VARCHAR); // SSi City
			ssiObj.registerOutParameter(20, Types.VARCHAR); // Pincode
			ssiObj.registerOutParameter(21, Types.VARCHAR); // Display Defaulter
			ssiObj.registerOutParameter(22, Types.VARCHAR); // SSI District
			ssiObj.registerOutParameter(23, Types.VARCHAR); // SSI State
			ssiObj.registerOutParameter(24, Types.VARCHAR); // Industry Nature
			ssiObj.registerOutParameter(25, Types.VARCHAR); // Indutry Sector
															// name
			ssiObj.registerOutParameter(26, Types.VARCHAR); // Status
			ssiObj.registerOutParameter(27, Types.VARCHAR); // BID
			ssiObj.registerOutParameter(28, Types.DOUBLE); // Outstanding Amount
			ssiObj.registerOutParameter(29, Types.VARCHAR); // MCGS Flag
			/*
			 * ssiObj.executeQuery(); int ssiObjValue=ssiObj.getInt(1);
			 * 
			 * Log.log(Log.DEBUG,"ApplicationDAO","getPartApplication",
			 * "SSI Details :" + ssiObjValue);
			 * 
			 * if(ssiObjValue==Constants.FUNCTION_FAILURE){
			 * 
			 * String error = ssiObj.getString(30);
			 * 
			 * ssiObj.close(); ssiObj=null;
			 * 
			 * connection.rollback();
			 * 
			 * throw new DatabaseException(error); } else {
			 * ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
			 * borrowerDetails.setPreviouslyCovered(ssiObj.getString(4).trim());
			 * borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
			 * borrowerDetails.setNpa(ssiObj.getString(7).trim());
			 * ssiDetails.setConstitution(ssiObj.getString(8));
			 * ssiDetails.setSsiType(ssiObj.getString(9).trim());
			 * ssiDetails.setSsiName(ssiObj.getString(10));
			 * ssiDetails.setRegNo(ssiObj.getString(11));
			 * ssiDetails.setSsiITPan(ssiObj.getString(13));
			 * ssiDetails.setActivityType(ssiObj.getString(14));
			 * ssiDetails.setEmployeeNos(ssiObj.getInt(15));
			 * ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
			 * ssiDetails.setProjectedExports(ssiObj.getDouble(17));
			 * ssiDetails.setAddress(ssiObj.getString(18));
			 * ssiDetails.setCity(ssiObj.getString(19));
			 * ssiDetails.setPincode(ssiObj.getString(20).trim());
			 * ssiDetails.setDistrict(ssiObj.getString(22));
			 * ssiDetails.setState(ssiObj.getString(23));
			 * ssiDetails.setIndustryNature(ssiObj.getString(24));
			 * ssiDetails.setIndustrySector(ssiObj.getString(25));
			 * ssiDetails.setCgbid(ssiObj.getString(27));
			 * borrowerDetails.setOsAmt(ssiObj.getDouble(28));
			 */

			ssiObj.registerOutParameter(30, Types.VARCHAR); //
			ssiObj.registerOutParameter(31, Types.VARCHAR); //
			ssiObj.registerOutParameter(32, Types.VARCHAR); //
			// ssiObj.registerOutParameter(33,Types.VARCHAR);

			ssiObj.executeQuery();
			int ssiObjValue = ssiObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
					"SSI Details :" + ssiObjValue);

			if (ssiObjValue == Constants.FUNCTION_FAILURE) {

				// String error = ssiObj.getString(30);

				String error = ssiObj.getString(33);

				ssiObj.close();
				ssiObj = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
				borrowerDetails
						.setPreviouslyCovered(ssiObj.getString(4).trim());
				borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
				borrowerDetails.setNpa(ssiObj.getString(7).trim());
				ssiDetails.setConstitution(ssiObj.getString(8));
				ssiDetails.setSsiType(ssiObj.getString(9).trim());
				ssiDetails.setSsiName(ssiObj.getString(10));
				ssiDetails.setRegNo(ssiObj.getString(11));
				ssiDetails.setSsiITPan(ssiObj.getString(13));
				ssiDetails.setActivityType(ssiObj.getString(14));
				ssiDetails.setEmployeeNos(ssiObj.getInt(15));
				ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
				ssiDetails.setProjectedExports(ssiObj.getDouble(17));
				ssiDetails.setAddress(ssiObj.getString(18));
				ssiDetails.setCity(ssiObj.getString(19));
				ssiDetails.setPincode(ssiObj.getString(20).trim());
				ssiDetails.setDistrict(ssiObj.getString(22));
				ssiDetails.setState(ssiObj.getString(23));
				ssiDetails.setIndustryNature(ssiObj.getString(24));
				ssiDetails.setIndustrySector(ssiObj.getString(25));
				ssiDetails.setCgbid(ssiObj.getString(27));

				// kot

				ssiDetails.setEnterprise(ssiObj.getString(30));

				ssiDetails.setUnitAssisted(ssiObj.getString(31));

				ssiDetails.setWomenOperated(ssiObj.getString(32));

				// ssiDetails.setEnterprise("N");

				// ssiDetails.setUnitAssisted("Y");

				// ssiDetails.setWomenOperated("Y");

				borrowerDetails.setOsAmt(ssiObj.getDouble(28));

				Log.log(Log.DEBUG, "ApplicationDAO", "getPartApplication",
						"OS Amount :" + ssiObj.getDouble(28));
				borrowerDetails.setSsiDetails(ssiDetails);
				application.setBorrowerDetails(borrowerDetails);
				ssiObj.close();
				ssiObj = null;

			}
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();
			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);
			ssiDetails = null;
			borrowerDetails = null;

			Log.log(Log.INFO, "ApplicationDAO", "getPartApplication",
					"values of ssi object set successfully....");

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "getPartApplication",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "getPartApplication",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getPartApplication",
				"Exited. Memory : " + Runtime.getRuntime().freeMemory());

		return application;
	}

	public TermLoan getTermLoan(String appRefNo, String applicationLoanType)
			throws NoApplicationFoundException, DatabaseException {
		// Log.log(Log.INFO,"ApplicationDAO","getTermLoan","Entered. Memory : "+Runtime.getRuntime().freeMemory());
		TermLoan termLoan = new TermLoan();
		Connection connection = DBConnection.getConnection(false);
		try {
			// Retrieving Loan type from the application
			// String applicationLoanType=application.getLoanType();

			// This function is called for a term Credit / Composite / Both
			// Application

			CallableStatement termLoanObj = connection
					.prepareCall("{?=call funcGetTermLoanforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			termLoanObj.setString(2, appRefNo); // AppRefNo

			termLoanObj.registerOutParameter(1, Types.INTEGER);
			termLoanObj.registerOutParameter(22, Types.VARCHAR);

			termLoanObj.registerOutParameter(3, Types.DOUBLE); // TRM Amount
																// Sanctioned
			termLoanObj.registerOutParameter(4, Types.DATE); // TRM Amt
																// sanctioned
																// Date
			termLoanObj.registerOutParameter(5, Types.DOUBLE); // TRM Promoter
																// Conribution
			termLoanObj.registerOutParameter(6, Types.DOUBLE); // Subsidy or
																// Equity
			termLoanObj.registerOutParameter(7, Types.DOUBLE); // Others

			termLoanObj.registerOutParameter(8, Types.DOUBLE); // Credit to be
																// guaranteed
			termLoanObj.registerOutParameter(9, Types.INTEGER); // Tenure
			termLoanObj.registerOutParameter(10, Types.VARCHAR); // Interest
																	// Type
			termLoanObj.registerOutParameter(11, Types.DOUBLE); // Interest Rate
			termLoanObj.registerOutParameter(12, Types.DOUBLE); // Bench Mark
																// PLR
			termLoanObj.registerOutParameter(13, Types.DOUBLE); // PLR
			termLoanObj.registerOutParameter(14, Types.INTEGER); // Moratorium
			termLoanObj.registerOutParameter(15, Types.DATE); // First
																// Installment
																// Date
			termLoanObj.registerOutParameter(16, Types.INTEGER); // No Of
																	// Installments
			termLoanObj.registerOutParameter(17, Types.INTEGER); // Repayment
																	// Periodicity
			termLoanObj.registerOutParameter(18, Types.VARCHAR); // PLR Type
			termLoanObj.registerOutParameter(19, Types.DOUBLE); // DBR Amount
			termLoanObj.registerOutParameter(20, Types.DATE); // DBR First Date
			termLoanObj.registerOutParameter(21, Types.DATE); // DBR Last Date

			termLoanObj.executeQuery();
			int termLoanObjValue = termLoanObj.getInt(1);

			// Log.log(Log.DEBUG,"ApplicationDAO","getTermLoan","Term Loan Details :"
			// + termLoanObjValue);

			if (termLoanObjValue != Constants.FUNCTION_SUCCESS
					&& termLoanObjValue != Constants.FUNCTION_NO_DATA) {

				String error = termLoanObj.getString(22);

				termLoanObj.close();
				termLoanObj = null;

				connection.rollback();

				// Log.log(Log.ERROR,"ApplicationDAO","getTermLoan","Term Loan Exception :"
				// + error);

				throw new DatabaseException(error);
			} else {
				// projectOutlayDetails.setTermCreditSanctioned(termLoanObj.getDouble(3));
				// //Amount Sanctioned
				java.sql.Date sanctionedDate = termLoanObj.getDate(4);
				//System.out.println("In getTermLoan sanctionedDate "
						//+ sanctionedDate);
				termLoan.setAmountSanctionedDate(DateHelper
						.sqlToUtilDate(sanctionedDate)); // sanctioned Date
				sanctionedDate = null;
				// projectOutlayDetails.setTcPromoterContribution(termLoanObj.getDouble(5));
				// projectOutlayDetails.setTcSubsidyOrEquity(termLoanObj.getDouble(6));
				// projectOutlayDetails.setTcOthers(termLoanObj.getDouble(7));
				if ((applicationLoanType.equals("TC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					termLoan.setCreditGuaranteed(termLoanObj.getDouble(8));
					termLoan.setTenure(termLoanObj.getInt(9));
					termLoan.setInterestType(termLoanObj.getString(10));
					termLoan.setInterestRate(termLoanObj.getDouble(11));
					termLoan.setBenchMarkPLR(termLoanObj.getDouble(12));
					termLoan.setPlr(termLoanObj.getDouble(13));
					termLoan.setRepaymentMoratorium(termLoanObj.getInt(14));
					termLoan.setFirstInstallmentDueDate(DateHelper
							.sqlToUtilDate(termLoanObj.getDate(15)));
					termLoan.setNoOfInstallments(termLoanObj.getInt(16));
					termLoan.setPeriodicity(termLoanObj.getInt(17));
					termLoan.setTypeOfPLR(termLoanObj.getString(18));
					termLoan.setAmtDisbursed(termLoanObj.getDouble(19));
					if (termLoanObj.getDate(20) != null
							&& termLoanObj.getDate(21) != null) {
						if (DateHelper.sqlToUtilDate(termLoanObj.getDate(20))
								.equals(DateHelper.sqlToUtilDate(termLoanObj
										.getDate(21)))) {
							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(21)));

						} else {

							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(21)));

						}
					}
					// Log.log(Log.INFO,"ApplicationDAO","getTermLoan","termloan set....");

				}
				termLoanObj.close();
				termLoanObj = null;
				Log.log(Log.DEBUG, "ApplicationDAO", "getTermLoan",
						"values of term loan object set successfully....");

			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "getTermLoan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "getTermLoan",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		// Log.log(Log.INFO,"ApplicationDAO","getTermLoan","Exited. Memory : "+Runtime.getRuntime().freeMemory());

		return termLoan;
	}

	public WorkingCapital getWorkingCapital(String appRefNo,
			String applicationLoanType) throws NoApplicationFoundException,
			DatabaseException {
		// Log.log(Log.INFO,"ApplicationDAO","getWorkingCapital","Entered. Memory : "+Runtime.getRuntime().freeMemory());
		WorkingCapital workingCapital = new WorkingCapital();
		//// System.out.println("appRefNo:"+appRefNo);
		Connection connection = DBConnection.getConnection(false);

		try {

			// This function is called for a Working Capital / Composite / Both
			// Application

			CallableStatement wcObj = connection
					.prepareCall("{?=call funcGetWorkingCapitalforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			wcObj.setString(2, appRefNo); // AppRefNo

			wcObj.registerOutParameter(1, Types.INTEGER);
			wcObj.registerOutParameter(17, Types.VARCHAR);
			wcObj.registerOutParameter(3, Types.DOUBLE); // Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(4, Types.DOUBLE); // Non Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(5, Types.DOUBLE); // Promoter
															// Contribution
			wcObj.registerOutParameter(6, Types.DOUBLE); // Subsidy/Equity
			wcObj.registerOutParameter(7, Types.DOUBLE); // Others
			wcObj.registerOutParameter(8, Types.DOUBLE); // Interest
			wcObj.registerOutParameter(9, Types.DOUBLE); // Commission
			wcObj.registerOutParameter(10, Types.DATE); // Fund Based Limit
														// Sanctioned date
			wcObj.registerOutParameter(11, Types.DATE); // NonFund Based Date
			wcObj.registerOutParameter(12, Types.DOUBLE); // Fund Based Credit
			wcObj.registerOutParameter(13, Types.DOUBLE); // Non Fund Based
															// Credit
			wcObj.registerOutParameter(14, Types.DOUBLE); // PLR
			wcObj.registerOutParameter(15, Types.VARCHAR); // PLR Type
			wcObj.registerOutParameter(16, Types.VARCHAR); // Interest Type

			wcObj.executeQuery();
			int wcObjValue = wcObj.getInt(1);

			// Log.log(Log.DEBUG,"ApplicationDAO","getWorkingCapital","TWorking Capital Loan Details :"
			// + wcObjValue);

			if (wcObjValue != Constants.FUNCTION_SUCCESS
					&& wcObjValue != Constants.FUNCTION_NO_DATA) {

				String error = wcObj.getString(17);

				wcObj.close();
				wcObj = null;

				connection.rollback();

				// Log.log(Log.DEBUG,"ApplicationDAO","getWorkingCapital","Working Capital Exception :"
				// + error);

				throw new DatabaseException(error);
			} else {
				if ((applicationLoanType.equals("WC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					workingCapital
							.setLimitFundBasedInterest(wcObj.getDouble(8));
					workingCapital.setLimitNonFundBasedCommission(wcObj
							.getDouble(9));
					workingCapital.setLimitFundBasedSanctionedDate(DateHelper
							.sqlToUtilDate(wcObj.getDate(10)));
					workingCapital
							.setLimitNonFundBasedSanctionedDate(DateHelper
									.sqlToUtilDate(wcObj.getDate(11)));
					workingCapital.setCreditFundBased(wcObj.getDouble(12));
					workingCapital.setCreditNonFundBased(wcObj.getDouble(13));
					workingCapital.setWcPlr(wcObj.getDouble(14));
					workingCapital.setWcTypeOfPLR(wcObj.getString(15));

					workingCapital
							.setWcInterestType(wcObj.getString(16).trim());

					// Log.log(Log.DEBUG,"ApplicationDAO","getWorkingCapital","Working Interest type:"
					// + workingCapital.getWcInterestType());
				}
				wcObj.close();
				wcObj = null;

			}
			// Log.log(Log.INFO,"ApplicationDAO","getWorkingCapital","values of working capital object set successfully....");

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getWorkingCapital",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getWorkingCapital",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		// Log.log(Log.INFO,"ApplicationDAO","getWorkingCapital","Exited. Memory : "+Runtime.getRuntime().freeMemory());

		return workingCapital;
	}

	/**
	 * This method returns the application for a combination of MLI id,
	 * CGBID,CGPAN and Application reference No. If more than one parameter is
	 * available, then both the parameter would be combined in the search query
	 * to get the Application. If only MLI id or CGBID s present, list of CGPANs
	 * and Application Reference numbers would be given.
	 * 
	 * @param mliID
	 * @param cgbid
	 * @param cgpan
	 * @param appRefNo
	 * @return com.cgtsi.application.Application
	 * @throws NoApplicationFoundException
	 * @throws DatabaseException
	 * @roseuid 398265930137
	 */
	public Application getApplication(String mliID, String cgpan,
			String appRefNo) throws NoApplicationFoundException,
			DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "getApplication",
				"Entered. Memory : " + Runtime.getRuntime().freeMemory());

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();

		Connection connection = DBConnection.getConnection(false);

		Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
				"After entering in the method");

		try {/*
			 * if (mliID!=null && !(mliID.equals(""))) { String
			 * bankId=mliID.substring(0,4); //getting the bank id
			 * 
			 * String zoneId=mliID.substring(4,8); //getting the zone id
			 * 
			 * String branchId=mliID.substring(8,12); //getting the branch id
			 * 
			 * }else {
			 * 
			 * mliID=null; }
			 */

			// if mli and app ref no are entered
			if ((mliID == null && !(appRefNo.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(appRefNo
							.equals("")))) {
				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"when mliID and app ref No are entered");
				application = getAppForAppRef(mliID, appRefNo);

			}
			// if mli and cgpan are entered
			else if ((mliID == null && !(cgpan.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(cgpan
							.equals("")))) {
				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"when mliID and cgpan are entered");
				application = getAppForCgpan(mliID, cgpan);
			}
			//// System.out.println("App Ref No:"+appRefNo);

			/*
			 * if app ref no are entered if
			 * ((cgpan.equals(""))&&(!(appRefNo.equals("")))) {
			 * application=getAppForAppRef(appRefNo);
			 * projectOutlayDetails=application.getProjectOutlayDetails(); }
			 * //if cgpan are entered else if
			 * ((!(cgpan.equals("")))&&(appRefNo.equals(""))) {
			 * application=getAppForCgpan(cgpan); }
			 */
			projectOutlayDetails = application.getProjectOutlayDetails();
			appRefNo = application.getAppRefNo();

			//// System.out.println("in  Application");
			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values for application object set successfully...");

			// SSI Details
			CallableStatement ssiObj = connection.prepareCall(
			// "{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
					"{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			ssiObj.setString(2, appRefNo); // AppRefNo

			ssiObj.registerOutParameter(1, Types.INTEGER);
			ssiObj.registerOutParameter(33, Types.VARCHAR);

			ssiObj.registerOutParameter(3, Types.INTEGER); // SSI Ref No
			ssiObj.registerOutParameter(4, Types.VARCHAR); // Already Covered by
															// CGTSI
			ssiObj.registerOutParameter(5, Types.VARCHAR); // Already assisted
															// by bank
			ssiObj.registerOutParameter(6, Types.VARCHAR); // SSI Unique No
			ssiObj.registerOutParameter(7, Types.VARCHAR); // NPA
			ssiObj.registerOutParameter(8, Types.VARCHAR); // Constitution
			ssiObj.registerOutParameter(9, Types.VARCHAR); // SSI Type
			ssiObj.registerOutParameter(10, Types.VARCHAR); // unit Name
			ssiObj.registerOutParameter(11, Types.VARCHAR); // SSI reg no
			ssiObj.registerOutParameter(12, Types.DATE); // Commencement Date
			ssiObj.registerOutParameter(13, Types.VARCHAR); // SSI ITPAN
			ssiObj.registerOutParameter(14, Types.VARCHAR); // type of Activity
			ssiObj.registerOutParameter(15, Types.INTEGER); // No Of Employees
			ssiObj.registerOutParameter(16, Types.DOUBLE); // project Sales
			ssiObj.registerOutParameter(17, Types.DOUBLE); // Project Exports
			ssiObj.registerOutParameter(18, Types.VARCHAR); // SSI Address
			ssiObj.registerOutParameter(19, Types.VARCHAR); // SSi City
			ssiObj.registerOutParameter(20, Types.VARCHAR); // Pincode
			ssiObj.registerOutParameter(21, Types.VARCHAR); // Display Defaulter
			ssiObj.registerOutParameter(22, Types.VARCHAR); // SSI District
			ssiObj.registerOutParameter(23, Types.VARCHAR); // SSI State
			ssiObj.registerOutParameter(24, Types.VARCHAR); // Industry Nature
			ssiObj.registerOutParameter(25, Types.VARCHAR); // Indutry Sector
															// name
			ssiObj.registerOutParameter(26, Types.VARCHAR); // Status
			ssiObj.registerOutParameter(27, Types.VARCHAR); // BID
			ssiObj.registerOutParameter(28, Types.DOUBLE); // Outstanding Amount
			ssiObj.registerOutParameter(29, Types.VARCHAR); // MCGS Flag

			// kot
			ssiObj.registerOutParameter(30, Types.VARCHAR); //
			ssiObj.registerOutParameter(31, Types.VARCHAR); //
			ssiObj.registerOutParameter(32, Types.VARCHAR); //
			// ssiObj.registerOutParameter(33,Types.VARCHAR);

			ssiObj.executeQuery();
			int ssiObjValue = ssiObj.getInt(1);

			//// System.out.println("in  Applicationtest " + ssiObjValue);

			Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
					"SSI Details :" + ssiObjValue);

			if (ssiObjValue == Constants.FUNCTION_FAILURE) {

				// String error = ssiObj.getString(30);

				String error = ssiObj.getString(33);

				ssiObj.close();
				ssiObj = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
				borrowerDetails
						.setPreviouslyCovered(ssiObj.getString(4).trim());
				borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
				borrowerDetails.setNpa(ssiObj.getString(7).trim());
				ssiDetails.setConstitution(ssiObj.getString(8));
				ssiDetails.setSsiType(ssiObj.getString(9).trim());
				ssiDetails.setSsiName(ssiObj.getString(10));
				ssiDetails.setRegNo(ssiObj.getString(11));
				ssiDetails.setSsiITPan(ssiObj.getString(13));
				ssiDetails.setActivityType(ssiObj.getString(14));
				ssiDetails.setEmployeeNos(ssiObj.getInt(15));
				ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
				ssiDetails.setProjectedExports(ssiObj.getDouble(17));
				ssiDetails.setAddress(ssiObj.getString(18));
				ssiDetails.setCity(ssiObj.getString(19));
				ssiDetails.setPincode(ssiObj.getString(20).trim());
				ssiDetails.setDistrict(ssiObj.getString(22));
				ssiDetails.setState(ssiObj.getString(23));
				ssiDetails.setIndustryNature(ssiObj.getString(24));
				ssiDetails.setIndustrySector(ssiObj.getString(25));
				ssiDetails.setCgbid(ssiObj.getString(27));

				// kot
				// ssiDetails.setAddress(ssiObj.getString(28));

				// ssiDetails.setMSE(ssiObj.getString(29));

				// ssiDetails.setMSE(ssiObj.getString(30));

				ssiDetails.setEnterprise(ssiObj.getString(30));

				ssiDetails.setUnitAssisted(ssiObj.getString(31));

				ssiDetails.setWomenOperated(ssiObj.getString(32));

				// ssiDetails.setEnterprise("N");

				// ssiDetails.setUnitAssisted("Y");

				// ssiDetails.setWomenOperated("Y");

				borrowerDetails.setOsAmt(ssiObj.getDouble(28));

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"OS Amount :" + ssiObj.getDouble(28));
				borrowerDetails.setSsiDetails(ssiDetails);
				application.setBorrowerDetails(borrowerDetails);
				ssiObj.close();
				ssiObj = null;

			}
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values of ssi object set successfully....");

			//// System.out.println("in  Application1");

			// Promoters Details
			CallableStatement promoterObj = connection
					.prepareCall("{?=call funcGetPromoterDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			promoterObj.setString(2, appRefNo); // AppRefNo

			promoterObj.registerOutParameter(1, Types.INTEGER);
			promoterObj.registerOutParameter(23, Types.VARCHAR);

			promoterObj.registerOutParameter(3, Types.INTEGER); // SSI RefNo
			promoterObj.registerOutParameter(4, Types.VARCHAR); // Chief title
			promoterObj.registerOutParameter(5, Types.VARCHAR); // First name
			promoterObj.registerOutParameter(6, Types.VARCHAR); // Middle Name
			promoterObj.registerOutParameter(7, Types.VARCHAR); // Last name
			promoterObj.registerOutParameter(8, Types.VARCHAR); // Chief ITPAN
			promoterObj.registerOutParameter(9, Types.VARCHAR); // Gender
			promoterObj.registerOutParameter(10, Types.DATE); // DOB
			promoterObj.registerOutParameter(11, Types.VARCHAR); // Legal Type
			promoterObj.registerOutParameter(12, Types.VARCHAR); // LegalID
			promoterObj.registerOutParameter(13, Types.VARCHAR); // Promoter
																	// First
																	// Name
			promoterObj.registerOutParameter(14, Types.DATE); // Promoter First
																// DOB
			promoterObj.registerOutParameter(15, Types.VARCHAR); // Promoter
																	// FirstITPAN
			promoterObj.registerOutParameter(16, Types.VARCHAR); // Promoter
																	// Second
																	// Name
			promoterObj.registerOutParameter(17, Types.DATE); // Promoter Second
																// DOB
			promoterObj.registerOutParameter(18, Types.VARCHAR); // Promoter
																	// Second
																	// ITPAN
			promoterObj.registerOutParameter(19, Types.VARCHAR); // Promoter
																	// Third
																	// Name
			promoterObj.registerOutParameter(20, Types.DATE); // Promoter Third
																// DOB
			promoterObj.registerOutParameter(21, Types.VARCHAR); // Promoter
																	// Third
																	// ITPAN
			promoterObj.registerOutParameter(22, Types.VARCHAR); // social
																	// Category

			promoterObj.executeQuery();
			int promoterObjValue = promoterObj.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Promoters Details :" + promoterObjValue);

			if (promoterObjValue == Constants.FUNCTION_FAILURE) {

				String error = promoterObj.getString(23);

				promoterObj.close();
				promoterObj = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"Promoter Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ssiDetails.setCpTitle(promoterObj.getString(4));
				ssiDetails.setCpFirstName(promoterObj.getString(5));
				if (promoterObj.getString(6) != null
						&& !promoterObj.getString(6).equals("")) {
					ssiDetails.setCpMiddleName(promoterObj.getString(6));
				} else {
					ssiDetails.setCpMiddleName("");
				}

				ssiDetails.setCpLastName(promoterObj.getString(7));
				ssiDetails.setCpITPAN(promoterObj.getString(8));
				ssiDetails.setCpGender(promoterObj.getString(9).trim());
				ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(10)));
				ssiDetails.setCpLegalID(promoterObj.getString(11));
				ssiDetails.setCpLegalIdValue(promoterObj.getString(12));
				ssiDetails.setFirstName(promoterObj.getString(13));
				ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(14)));
				ssiDetails.setFirstItpan(promoterObj.getString(15));
				ssiDetails.setSecondName(promoterObj.getString(16));
				ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(17)));
				ssiDetails.setSecondItpan(promoterObj.getString(18));
				ssiDetails.setThirdName(promoterObj.getString(19));
				ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(20)));
				ssiDetails.setThirdItpan(promoterObj.getString(21));
				ssiDetails.setSocialCategory(promoterObj.getString(22));

				(application.getBorrowerDetails()).setSsiDetails(ssiDetails);
				promoterObj.close();
				promoterObj = null;
			}

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values of promoter object set successfully....");
			//// System.out.println("in  Application2");
			// Guarantors Names
			CallableStatement guarantorObj = connection
					.prepareCall("{?=call packGetPersonalGuarantee.funcGetPerGuarforAppRef(?,?,?)}");

			guarantorObj.setString(2, appRefNo); // AppRefNo

			guarantorObj.registerOutParameter(1, Types.INTEGER);
			guarantorObj.registerOutParameter(4, Types.VARCHAR);

			guarantorObj.registerOutParameter(3, Constants.CURSOR);

			guarantorObj.executeQuery();
			int guarantorObjValue = guarantorObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Guarantors Details :" + guarantorObjValue);

			if (guarantorObjValue == Constants.FUNCTION_FAILURE) {

				String error = guarantorObj.getString(4);

				guarantorObj.close();
				guarantorObj = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"Gurantor Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ResultSet guarantorsResults = (ResultSet) guarantorObj
						.getObject(3);
				int i = 0;
				while (guarantorsResults.next()) {
					if (i == 0) {
						projectOutlayDetails
								.setGuarantorsName1(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth1(guarantorsResults
										.getDouble(2));
					}
					if (i == 1) {
						projectOutlayDetails
								.setGuarantorsName2(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth2(guarantorsResults
										.getDouble(2));
					}
					if (i == 2) {
						projectOutlayDetails
								.setGuarantorsName3(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth3(guarantorsResults
										.getDouble(2));
					}
					if (i == 3) {
						projectOutlayDetails
								.setGuarantorsName4(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth4(guarantorsResults
										.getDouble(2));
					}
					i++;

				}
				guarantorObj.close();
				guarantorObj = null;
				guarantorsResults.close();
				guarantorsResults = null;
			}

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values of project guarantor object set successfully....");

			//// System.out.println("in  Application3");
			// Primary Security Details
			CallableStatement psObj = connection
					.prepareCall("{?=call packGetPrimarySecurity.funcGetPriSecforAppRef(?,?,?)}");

			psObj.setString(2, appRefNo); // AppRefNo

			psObj.registerOutParameter(1, Types.INTEGER);
			psObj.registerOutParameter(4, Types.VARCHAR);

			psObj.registerOutParameter(3, Constants.CURSOR);

			psObj.executeQuery();
			int psObjValue = psObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Primary Security Details :" + psObjValue);

			if (psObjValue == Constants.FUNCTION_FAILURE) {

				String error = psObj.getString(4);

				psObj.close();
				psObj = null;

				connection.rollback();
				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"Primary Security Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ResultSet psResults = (ResultSet) psObj.getObject(3);
				while (psResults.next()) {
					if ((psResults.getString(1)).equals("Land")) {
						primarySecurityDetails.setLandParticulars(psResults
								.getString(2));
						primarySecurityDetails.setLandValue(psResults
								.getDouble(3));
					}
					if ((psResults.getString(1)).equals("Building")) {
						primarySecurityDetails.setBldgParticulars(psResults
								.getString(2));
						primarySecurityDetails.setBldgValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Machinery")) {
						primarySecurityDetails.setMachineParticulars(psResults
								.getString(2));
						primarySecurityDetails.setMachineValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Fixed Assets")) {
						primarySecurityDetails.setAssetsParticulars(psResults
								.getString(2));
						primarySecurityDetails.setAssetsValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Current Assets")) {
						primarySecurityDetails
								.setCurrentAssetsParticulars(psResults
										.getString(2));
						primarySecurityDetails.setCurrentAssetsValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Others")) {
						primarySecurityDetails.setOthersParticulars(psResults
								.getString(2));
						primarySecurityDetails.setOthersValue(psResults
								.getDouble(3));

					}

				}
				psResults.close();
				psResults = null;
				psObj.close();
				psObj = null;

			}

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values of ps object set successfully....");

			// Retrieving Loan type from the application
			String applicationLoanType = application.getLoanType();
			//// System.out.println("For Testing App Loan Type:"+applicationLoanType);
			//// System.out.println("For Testing Cgpan :"+cgpan);

			// This function is called for a term Credit / Composite / Both
			// Application
			// if
			// ((applicationLoanType.equals("TC"))||(applicationLoanType.equals("CC"))||(applicationLoanType.equals("BO")))
			// {

			//// System.out.println("in  Application4");
			CallableStatement termLoanObj = connection
					.prepareCall("{?=call funcGetTermLoanforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			termLoanObj.setString(2, appRefNo); // AppRefNo
			//// System.out.println("App Ref:"+appRefNo);

			termLoanObj.registerOutParameter(1, Types.INTEGER);
			termLoanObj.registerOutParameter(22, Types.VARCHAR);

			termLoanObj.registerOutParameter(3, Types.DOUBLE); // TRM Amount
																// Sanctioned
			termLoanObj.registerOutParameter(4, Types.DATE); // TRM Amt
																// sanctioned
																// Date
			termLoanObj.registerOutParameter(5, Types.DOUBLE); // TRM Promoter
																// Conribution
			termLoanObj.registerOutParameter(6, Types.DOUBLE); // Subsidy or
																// Equity
			termLoanObj.registerOutParameter(7, Types.DOUBLE); // Others

			termLoanObj.registerOutParameter(8, Types.DOUBLE); // Credit to be
																// guaranteed
			termLoanObj.registerOutParameter(9, Types.INTEGER); // Tenure
			termLoanObj.registerOutParameter(10, Types.VARCHAR); // Interest
																	// Type
			termLoanObj.registerOutParameter(11, Types.DOUBLE); // Interest Rate
			termLoanObj.registerOutParameter(12, Types.DOUBLE); // Bench Mark
																// PLR
			termLoanObj.registerOutParameter(13, Types.DOUBLE); // PLR
			termLoanObj.registerOutParameter(14, Types.INTEGER); // Moratorium
			termLoanObj.registerOutParameter(15, Types.DATE); // First
																// Installment
																// Date
			termLoanObj.registerOutParameter(16, Types.INTEGER); // No Of
																	// Installments
			termLoanObj.registerOutParameter(17, Types.INTEGER); // Repayment
																	// Periodicity
			termLoanObj.registerOutParameter(18, Types.VARCHAR); // PLR Type
			termLoanObj.registerOutParameter(19, Types.DOUBLE); // DBR Amount
			termLoanObj.registerOutParameter(20, Types.DATE); // DBR First Date
			termLoanObj.registerOutParameter(21, Types.DATE); // DBR Last Date

			termLoanObj.executeQuery();
			int termLoanObjValue = termLoanObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Term Loan Details :" + termLoanObjValue);

			if (termLoanObjValue != Constants.FUNCTION_SUCCESS
					&& termLoanObjValue != Constants.FUNCTION_NO_DATA) {

				String error = termLoanObj.getString(22);

				termLoanObj.close();
				termLoanObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Term Loan Exception :" + error);

				throw new DatabaseException(error);
			} else if (termLoanObjValue == Constants.FUNCTION_NO_DATA) {
				projectOutlayDetails.setTermCreditSanctioned(0); // Amount
																	// Sanctioned
				java.sql.Date sanctionedDate = termLoanObj.getDate(4);
				termLoan.setAmountSanctionedDate(null); // sanctioned Date
				sanctionedDate = null;
				projectOutlayDetails.setTcPromoterContribution(0);
				projectOutlayDetails.setTcSubsidyOrEquity(0);
				projectOutlayDetails.setTcOthers(0);
				if ((applicationLoanType.equals("TC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					termLoan.setCreditGuaranteed(0);
					termLoan.setTenure(0);
					termLoan.setInterestType("T");
					termLoan.setInterestRate(0);
					termLoan.setBenchMarkPLR(0);
					termLoan.setPlr(0);
					termLoan.setRepaymentMoratorium(0);
					termLoan.setFirstInstallmentDueDate(null);
					termLoan.setNoOfInstallments(0);
					termLoan.setPeriodicity(0);
					termLoan.setTypeOfPLR(null);
					termLoan.setAmtDisbursed(0);
					// if
					// (DateHelper.sqlToUtilDate(termLoanObj.getDate(20)).equals(DateHelper.sqlToUtilDate(termLoanObj.getDate(21))))
					// {
					termLoan.setFirstDisbursementDate(null);
					termLoan.setFinalDisbursementDate(null);

					// }else {

					// termLoan.setFirstDisbursementDate(DateHelper.sqlToUtilDate(termLoanObj.getDate(20)));
					// termLoan.setFinalDisbursementDate(DateHelper.sqlToUtilDate(termLoanObj.getDate(21)));

					// }

				}

				Log.log(Log.INFO, "ApplicationDAO", "getApplication",
						"termloan set....");

				// }
				termLoanObj.close();
				termLoanObj = null;

			} else {
				projectOutlayDetails.setTermCreditSanctioned(termLoanObj
						.getDouble(3)); // Amount Sanctioned
				java.sql.Date sanctionedDate = termLoanObj.getDate(4);
				termLoan.setAmountSanctionedDate(DateHelper
						.sqlToUtilDate(sanctionedDate)); // sanctioned Date
				sanctionedDate = null;
				projectOutlayDetails.setTcPromoterContribution(termLoanObj
						.getDouble(5));
				projectOutlayDetails.setTcSubsidyOrEquity(termLoanObj
						.getDouble(6));
				projectOutlayDetails.setTcOthers(termLoanObj.getDouble(7));
				if ((applicationLoanType.equals("TC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					termLoan.setCreditGuaranteed(termLoanObj.getDouble(8));
					termLoan.setTenure(termLoanObj.getInt(9));
					termLoan.setInterestType(termLoanObj.getString(10));
					termLoan.setInterestRate(termLoanObj.getDouble(11));
					termLoan.setBenchMarkPLR(termLoanObj.getDouble(12));
					termLoan.setPlr(termLoanObj.getDouble(13));
					termLoan.setRepaymentMoratorium(termLoanObj.getInt(14));
					termLoan.setFirstInstallmentDueDate(DateHelper
							.sqlToUtilDate(termLoanObj.getDate(15)));
					termLoan.setNoOfInstallments(termLoanObj.getInt(16));
					termLoan.setPeriodicity(termLoanObj.getInt(17));
					termLoan.setTypeOfPLR(termLoanObj.getString(18));
					termLoan.setAmtDisbursed(termLoanObj.getDouble(19));
					if (termLoanObj.getDate(20) != null) {
						if (DateHelper.sqlToUtilDate(termLoanObj.getDate(20))
								.equals(DateHelper.sqlToUtilDate(termLoanObj
										.getDate(21)))) {
							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(null);

						} else {

							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(21)));

						}

					}
					Log.log(Log.INFO, "ApplicationDAO", "getApplication",
							"termloan set....");

				}
				termLoanObj.close();
				termLoanObj = null;

			}
			// added '}' by sukumar@path for testing
			// }

			//// System.out.println("in  Application5");

			// retrieving the repayment details
			if ((applicationLoanType.equals("TC"))
					|| (applicationLoanType.equals("CC"))
					|| (applicationLoanType.equals("BO"))) {
				CallableStatement repaymentObj = connection
						.prepareCall("{?=call funcGetRepSchForAppRef(?,?,?,?,?,?)}");

				repaymentObj.setString(2, appRefNo);

				repaymentObj.registerOutParameter(1, Types.INTEGER);
				repaymentObj.registerOutParameter(7, Types.VARCHAR);

				repaymentObj.registerOutParameter(3, Types.INTEGER);
				repaymentObj.registerOutParameter(4, Types.DATE);
				repaymentObj.registerOutParameter(5, Types.INTEGER);
				repaymentObj.registerOutParameter(6, Types.INTEGER);

				repaymentObj.executeQuery();
				int repaymentObjValue = repaymentObj.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"Term Loan Details :" + repaymentObjValue);

				if (repaymentObjValue != Constants.FUNCTION_SUCCESS) {

					String error = repaymentObj.getString(7);

					repaymentObj.close();
					repaymentObj = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
							"Term Loan Exception :" + error);

					throw new DatabaseException(error);
				} else {
					termLoan.setRepaymentMoratorium(repaymentObj.getInt(3));
					termLoan.setFirstInstallmentDueDate(DateHelper
							.sqlToUtilDate(repaymentObj.getDate(4)));
					termLoan.setPeriodicity(repaymentObj.getInt(5));
					termLoan.setNoOfInstallments(repaymentObj.getInt(6));
					repaymentObj.close();
					repaymentObj = null;
				}
			}

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of term loan object set successfully....");

			// This function is called when the outstanding details ahve to be
			// retrieved
			if ((applicationLoanType.equals("TC"))
					|| (applicationLoanType.equals("CC"))
					|| (applicationLoanType.equals("BO"))) {
				CallableStatement tcOutObj = connection
						.prepareCall("{?=call funcGetTCOutStanding(?,?,?,?)}");

				tcOutObj.setString(2, appRefNo);

				tcOutObj.registerOutParameter(1, Types.INTEGER);
				tcOutObj.registerOutParameter(5, Types.VARCHAR);
				tcOutObj.registerOutParameter(3, Types.DOUBLE);
				tcOutObj.registerOutParameter(4, Types.DATE);

				tcOutObj.executeQuery();
				int tcOutObjValue = tcOutObj.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"TWorking Capital Loan Details :" + tcOutObjValue);

				if (tcOutObjValue == Constants.FUNCTION_FAILURE) {

					String error = tcOutObj.getString(5);

					tcOutObj.close();
					tcOutObj = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
							"Working Capital Exception :" + error);

					throw new DatabaseException(error);
				} else if (tcOutObjValue == Constants.FUNCTION_NO_DATA) {
					termLoan.setPplOS(0);

					termLoan.setPplOS(tcOutObj.getDouble(3));
					termLoan.setPplOsAsOnDate(DateHelper.sqlToUtilDate(tcOutObj
							.getDate(4)));

					tcOutObj.close();
					tcOutObj = null;
				} else {

					if (tcOutObj.getDouble(3) == -1) {
						termLoan.setPplOS(0);

					} else {

						termLoan.setPplOS(tcOutObj.getDouble(3));
						termLoan.setPplOsAsOnDate(DateHelper
								.sqlToUtilDate(tcOutObj.getDate(4)));

					}
					tcOutObj.close();
					tcOutObj = null;
				}
			}

			// This function is called for a Working Capital / Composite / Both
			// Application
			// if(cgpan.equals("")|| (cgpan==null))
			// {

			//// System.out.println("in  Application7");
			CallableStatement wcObj = connection
					.prepareCall("{?=call funcGetWorkingCapitalforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			wcObj.setString(2, appRefNo); // AppRefNo

			wcObj.registerOutParameter(1, Types.INTEGER);
			wcObj.registerOutParameter(17, Types.VARCHAR);

			wcObj.registerOutParameter(3, Types.DOUBLE); // Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(4, Types.DOUBLE); // Non Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(5, Types.DOUBLE); // Promoter
															// Contribution
			wcObj.registerOutParameter(6, Types.DOUBLE); // Subsidy/Equity
			wcObj.registerOutParameter(7, Types.DOUBLE); // Others
			wcObj.registerOutParameter(8, Types.DOUBLE); // Interest
			wcObj.registerOutParameter(9, Types.DOUBLE); // Commission
			wcObj.registerOutParameter(10, Types.DATE); // Fund Based Limit
														// Sanctioned date
			wcObj.registerOutParameter(11, Types.DATE); // NonFund Based Date
			wcObj.registerOutParameter(12, Types.DOUBLE); // Fund Based Credit
			wcObj.registerOutParameter(13, Types.DOUBLE); // Non Fund Based
															// Credit
			wcObj.registerOutParameter(14, Types.DOUBLE); // PLR
			wcObj.registerOutParameter(15, Types.VARCHAR); // PLR Type
			wcObj.registerOutParameter(16, Types.VARCHAR); // Interest Type

			wcObj.executeQuery();
			int wcObjValue = wcObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Working Capital Loan Details :" + wcObjValue);

			if (wcObjValue != Constants.FUNCTION_SUCCESS
					&& wcObjValue != Constants.FUNCTION_NO_DATA) {

				String error = wcObj.getString(17);

				wcObj.close();
				wcObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Working Capital Exception :" + error);

				throw new DatabaseException(error);
			} else if (wcObjValue == Constants.FUNCTION_NO_DATA) {
				projectOutlayDetails.setWcFundBasedSanctioned(0);
				projectOutlayDetails.setWcNonFundBasedSanctioned(0);
				projectOutlayDetails.setWcPromoterContribution(0);
				projectOutlayDetails.setWcSubsidyOrEquity(0);
				projectOutlayDetails.setWcOthers(0);
				if ((applicationLoanType.equals("WC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					workingCapital.setLimitFundBasedInterest(0);
					workingCapital.setLimitNonFundBasedCommission(0);
					workingCapital.setLimitFundBasedSanctionedDate(null);
					workingCapital.setLimitNonFundBasedSanctionedDate(null);
					workingCapital.setCreditFundBased(0);
					workingCapital.setCreditNonFundBased(0);
					workingCapital.setWcPlr(0);
					workingCapital.setWcTypeOfPLR(null);
					workingCapital.setWcInterestType("T");
					Log.log(Log.ERROR,
							"ApplicationDAO",
							"getApplication",
							"Working Interest type:"
									+ workingCapital.getWcInterestType());
				}

			} else {
				projectOutlayDetails.setWcFundBasedSanctioned(wcObj
						.getDouble(3));
				projectOutlayDetails.setWcNonFundBasedSanctioned(wcObj
						.getDouble(4));
				projectOutlayDetails.setWcPromoterContribution(wcObj
						.getDouble(5));
				projectOutlayDetails.setWcSubsidyOrEquity(wcObj.getDouble(6));
				projectOutlayDetails.setWcOthers(wcObj.getDouble(7));
				if ((applicationLoanType.equals("WC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					workingCapital
							.setLimitFundBasedInterest(wcObj.getDouble(8));
					workingCapital.setLimitNonFundBasedCommission(wcObj
							.getDouble(9));
					workingCapital.setLimitFundBasedSanctionedDate(DateHelper
							.sqlToUtilDate(wcObj.getDate(10)));
					workingCapital
							.setLimitNonFundBasedSanctionedDate(DateHelper
									.sqlToUtilDate(wcObj.getDate(11)));
					workingCapital.setCreditFundBased(wcObj.getDouble(12));
					workingCapital.setCreditNonFundBased(wcObj.getDouble(13));
					workingCapital.setWcPlr(wcObj.getDouble(14));
					workingCapital.setWcTypeOfPLR(wcObj.getString(15));
					workingCapital
							.setWcInterestType(wcObj.getString(16).trim());
					Log.log(Log.ERROR,
							"ApplicationDAO",
							"getApplication",
							"Working Interest type:"
									+ workingCapital.getWcInterestType());
				}
				wcObj.close();
				wcObj = null;

			}
			// }
			// ADDED '}' BY SUKUMAR@PATH FOR TESTING

			/*
			 * if
			 * ((applicationLoanType.equals("WC"))||(applicationLoanType.equals
			 * ("CC"))||(applicationLoanType.equals("BO"))) { CallableStatement
			 * wcOutObj=connection.prepareCall(
			 * "{?=call funcGetWCOutStanding(?,?,?,?,?,?,?,?)}");
			 * 
			 * wcOutObj.setString(2,appRefNo); //AppRefNo
			 * 
			 * wcOutObj.registerOutParameter(1,Types.INTEGER);
			 * wcOutObj.registerOutParameter(9,Types.VARCHAR);
			 * 
			 * wcOutObj.registerOutParameter(3,Types.DOUBLE); //OS Fund Based
			 * Principal wcOutObj.registerOutParameter(4,Types.DOUBLE); //OS
			 * Fund Based Interest wcOutObj.registerOutParameter(5,Types.DATE);
			 * //OS Fund Based Date
			 * wcOutObj.registerOutParameter(6,Types.DOUBLE); //OS Non Fund
			 * Based Principal wcOutObj.registerOutParameter(7,Types.DOUBLE);
			 * //OS Non Fund Based Interest
			 * wcOutObj.registerOutParameter(8,Types.DATE); //OS Non Fund Based
			 * Date
			 * 
			 * wcOutObj.executeQuery(); int wcOutObjValue=wcOutObj.getInt(1);
			 * 
			 * Log.log(Log.DEBUG,"ApplicationDAO","getApplication",
			 * "TWorking Capital Loan Details :" + wcOutObjValue);
			 * 
			 * if(wcOutObjValue==Constants.FUNCTION_FAILURE){
			 * 
			 * String error = wcOutObj.getString(9);
			 * 
			 * wcOutObj.close(); wcOutObj=null;
			 * 
			 * connection.rollback();
			 * 
			 * Log.log(Log.ERROR,"ApplicationDAO","getApplication",
			 * "Working Capital Exception :" + error);
			 * 
			 * throw new DatabaseException(error); }else
			 * if(wcOutObjValue==Constants.FUNCTION_NO_DATA){
			 * 
			 * workingCapital.setOsFundBasedPpl(0);
			 * workingCapital.setOsFundBasedInterestAmt(0);
			 * workingCapital.setOsFundBasedAsOnDate(null);
			 * workingCapital.setOsNonFundBasedPpl(0);
			 * workingCapital.setOsNonFundBasedCommissionAmt(0);
			 * workingCapital.setOsNonFundBasedAsOnDate(null); wcOutObj.close();
			 * wcOutObj = null;
			 * 
			 * } else {
			 * 
			 * workingCapital.setOsFundBasedPpl(wcOutObj.getDouble(3));
			 * workingCapital.setOsFundBasedInterestAmt(wcOutObj.getDouble(4));
			 * workingCapital
			 * .setOsFundBasedAsOnDate(DateHelper.sqlToUtilDate(wcOutObj
			 * .getDate(5)));
			 * workingCapital.setOsNonFundBasedPpl(wcOutObj.getDouble(6));
			 * workingCapital
			 * .setOsNonFundBasedCommissionAmt(wcOutObj.getDouble(7));
			 * workingCapital
			 * .setOsNonFundBasedAsOnDate(DateHelper.sqlToUtilDate(
			 * wcOutObj.getDate(8))); wcOutObj.close(); wcOutObj = null; }
			 * 
			 * }
			 */

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of working capital object set successfully....");

			// This function retrieves the Securitization Details
			// if(cgpan.equals("")|| (cgpan==null))
			// {

			//// System.out.println("in  Application8");
			CallableStatement getSecDtls = connection
					.prepareCall("{?=call funcGetSecDetails(?,?,?,?,?,?,?,?,?,?)}");

			getSecDtls.setString(2, appRefNo); // AppRefNo

			getSecDtls.registerOutParameter(1, Types.INTEGER);
			getSecDtls.registerOutParameter(11, Types.VARCHAR);

			getSecDtls.registerOutParameter(3, Types.DOUBLE); // spread over PLR
			getSecDtls.registerOutParameter(4, Types.VARCHAR); // repayment in
																// equal
																// installments
			getSecDtls.registerOutParameter(5, Types.DOUBLE); // tangible net
																// worth
			getSecDtls.registerOutParameter(6, Types.DOUBLE); // fixed asset
			getSecDtls.registerOutParameter(7, Types.DOUBLE); // current ratio
			getSecDtls.registerOutParameter(8, Types.DOUBLE); // min dscr
			getSecDtls.registerOutParameter(9, Types.DOUBLE); // avg dscr
			getSecDtls.registerOutParameter(10, Types.DOUBLE); // financial part
																// of a unit

			getSecDtls.executeQuery();

			int getSecDtlsValue = getSecDtls.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Securitization Details :" + getSecDtlsValue);

			if (getSecDtlsValue == Constants.FUNCTION_FAILURE) {

				String error = getSecDtls.getString(11);

				getSecDtls.close();
				getSecDtls = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Securitization Detail Exception :" + error);

				throw new DatabaseException(error);
			} else if (getSecDtlsValue == Constants.FUNCTION_NO_DATA) {
				securitization.setSpreadOverPLR(0);
				securitization.setPplRepaymentInEqual("Y");
				securitization.setTangibleNetWorth(0);
				securitization.setFixedACR(0);
				securitization.setCurrentRatio(0);
				securitization.setMinimumDSCR(0);
				securitization.setAvgDSCR(0);
				securitization.setFinancialPartUnit(0);
				getSecDtls.close();
				getSecDtls = null;

			} else {

				securitization.setSpreadOverPLR(getSecDtls.getDouble(3));
				securitization.setPplRepaymentInEqual(getSecDtls.getString(4));
				securitization.setTangibleNetWorth(getSecDtls.getDouble(5));
				securitization.setFixedACR(getSecDtls.getDouble(6));
				securitization.setCurrentRatio(getSecDtls.getDouble(7));
				securitization.setMinimumDSCR(getSecDtls.getDouble(8));
				securitization.setAvgDSCR(getSecDtls.getDouble(9));
				securitization.setFinancialPartUnit(getSecDtls.getDouble(10));
				getSecDtls.close();
				getSecDtls = null;

			}
			// }
			// ADDED ' } ' BY SUKUMAR@PATH FOR TESTING
			// bhu
			//// System.out.println("in  Application9");
			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			MCGFDetails mcgfDetails = mcgsProcessor.getMcgsDetails(appRefNo,
					connection);
			if (mcgfDetails != null) {
				application.setMCGFDetails(mcgfDetails);

			}
			mcgsProcessor = null;
			mcgfDetails = null;

			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);

			application.setTermLoan(termLoan);

			application.setWc(workingCapital);
			application.setSecuritization(securitization);

			projectOutlayDetails
					.setPrimarySecurityDetails(primarySecurityDetails);
			application.setProjectOutlayDetails(projectOutlayDetails);

			borrowerDetails = null;
			ssiDetails = null;
			projectOutlayDetails = null;
			primarySecurityDetails = null;
			termLoan = null;
			workingCapital = null;
			securitization = null;

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Exited from appDAO successfully....");

			connection.commit();

			//// System.out.println("in  Application19");
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getApplication",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getApplication",
				"Exited. Memory : " + Runtime.getRuntime().freeMemory());

		return application;
	}

	public Application getOldApplication(String mliID, String cgpan,
			String appRefNo) throws NoApplicationFoundException,
			DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "getApplication",
				"Entered. Memory : " + Runtime.getRuntime().freeMemory());

		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		PrimarySecurityDetails primarySecurityDetails = new PrimarySecurityDetails();
		TermLoan termLoan = new TermLoan();
		WorkingCapital workingCapital = new WorkingCapital();
		Securitization securitization = new Securitization();

		Connection connection = DBConnection.getConnection(false);

		Log.log(Log.INFO, "ApplicationDAO", "getApplication",
				"After entering in the method");

		try {/*
			 * if (mliID!=null && !(mliID.equals(""))) { String
			 * bankId=mliID.substring(0,4); //getting the bank id
			 * 
			 * String zoneId=mliID.substring(4,8); //getting the zone id
			 * 
			 * String branchId=mliID.substring(8,12); //getting the branch id
			 * 
			 * }else {
			 * 
			 * mliID=null; }
			 */

			if ((mliID == null && !(cgpan.equals("")))
					|| ((mliID != null && !(mliID.equals(""))) && !(cgpan
							.equals("")))) {
				Log.log(Log.INFO, "ApplicationDAO", "getApplication",
						"when mliID and cgpan are entered");
				application = getAppForCgpan(mliID, cgpan);
			}

			appRefNo = application.getAppRefNo();

			CallableStatement appForAppRef = connection.prepareCall
			// ("{?=call funcGetOldAppDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
					("{?=call funcGetOldAppDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			appForAppRef.setString(2, appRefNo);
			appForAppRef.setString(3, mliID);

			Log.log(Log.DEBUG, "ApplicationDAO", "getAppForAppRef",
					"App ref no from getAppForAppRef:" + appRefNo);

			appForAppRef.registerOutParameter(1, Types.INTEGER);
			// appForAppRef.registerOutParameter(29,Types.VARCHAR);
			appForAppRef.registerOutParameter(30, Types.VARCHAR);

			appForAppRef.registerOutParameter(4, Types.VARCHAR); // App Ref No
			appForAppRef.registerOutParameter(5, Types.INTEGER); // SSI Ref No
			appForAppRef.registerOutParameter(6, Types.VARCHAR); // CGPAN
			appForAppRef.registerOutParameter(7, Types.VARCHAR); // Scheme Name
			appForAppRef.registerOutParameter(8, Types.VARCHAR); // Bank ID
			appForAppRef.registerOutParameter(9, Types.VARCHAR); // Zone Id
			appForAppRef.registerOutParameter(10, Types.VARCHAR); // Branch Id
			appForAppRef.registerOutParameter(11, Types.VARCHAR); // MLI Branch
																	// name
			appForAppRef.registerOutParameter(12, Types.VARCHAR); // Branch Code
			appForAppRef.registerOutParameter(13, Types.VARCHAR); // App Bank
																	// Ref No
			appForAppRef.registerOutParameter(14, Types.VARCHAR); // Composite
																	// Loan
			appForAppRef.registerOutParameter(15, Types.VARCHAR); // User Id
			appForAppRef.registerOutParameter(16, Types.VARCHAR); // Loan Type
			appForAppRef.registerOutParameter(17, Types.VARCHAR); // Collateral
																	// Security
			appForAppRef.registerOutParameter(18, Types.VARCHAR); // Third Party
																	// Gurantee
			appForAppRef.registerOutParameter(19, Types.VARCHAR); // Subsidy
																	// Scheme
																	// Name
			appForAppRef.registerOutParameter(20, Types.VARCHAR); // Rehabilitation
			appForAppRef.registerOutParameter(21, Types.DOUBLE); // Project
																	// Outlay
			appForAppRef.registerOutParameter(22, Types.VARCHAR); // App CGPAN
																	// Ref No
			appForAppRef.registerOutParameter(23, Types.VARCHAR); // Remarks
			appForAppRef.registerOutParameter(24, Types.DATE); // Submitted Date
			appForAppRef.registerOutParameter(25, Types.VARCHAR); // Status
			appForAppRef.registerOutParameter(26, Types.VARCHAR); // Sub scheme
																	// name
			appForAppRef.registerOutParameter(27, Types.DOUBLE); // Approved
																	// Amount
			appForAppRef.registerOutParameter(28, Types.DOUBLE); // ReApproved
																	// Amount
			appForAppRef.registerOutParameter(29, Types.VARCHAR);
			Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
					"Before Executing");

			appForAppRef.execute();

			Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
					"After Executing");

			int appForAppRefValue = appForAppRef.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getAppForAppRef",
					"Application Details :" + appForAppRefValue);

			if (appForAppRefValue == Constants.FUNCTION_FAILURE) {

				String error = appForAppRef.getString(30);

				appForAppRef.close();
				appForAppRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getAppForAppRef",
						"Application Detail exception :" + error);

				throw new DatabaseException(error);
			} else {
				application.setAppRefNo(appForAppRef.getString(4)); // app ref
																	// no
				application.setScheme(appForAppRef.getString(7)); // Scheme Name
				mliID = appForAppRef.getString(8) + appForAppRef.getString(9)
						+ appForAppRef.getString(10);
				application.setMliID(mliID);
				application.setMliBranchName(appForAppRef.getString(11)); // Branch
																			// Name
				application.setMliBranchCode(appForAppRef.getString(12));// Branch
																			// Code
				application.setMliRefNo(appForAppRef.getString(13)); // MliRefNo
				application.setCompositeLoan(appForAppRef.getString(14).trim()); // CompositeLoan
				// user.setUserId(appForAppRef.getString(15)); //userId
				application.setLoanType(appForAppRef.getString(16).trim()); // loan
																			// type
				projectOutlayDetails.setCollateralSecurityTaken(appForAppRef
						.getString(17).trim()); // Collateral Security
				projectOutlayDetails.setThirdPartyGuaranteeTaken(appForAppRef
						.getString(18).trim()); // Guarantee Taken
				projectOutlayDetails.setSubsidyName(appForAppRef.getString(19)); // Subsidy
																					// Name
				application
						.setRehabilitation(appForAppRef.getString(20).trim()); // rehabilitation
				projectOutlayDetails.setProjectOutlay(appForAppRef
						.getDouble(21)); // project Outlay
				application.setCgpanReference(appForAppRef.getString(22)); // app
																			// Cgpan
																			// Ref
																			// No
				application.setRemarks(appForAppRef.getString(23)); // remarks
				application.setSubmittedDate(appForAppRef.getDate(24)); // status
				application.setStatus(appForAppRef.getString(25)); // status
				application.setSubSchemeName(appForAppRef.getString(26));
				application.setApprovedAmount(appForAppRef.getDouble(27));
				application.setReapprovedAmount(appForAppRef.getDouble(28));
				application.setMsE(appForAppRef.getString(29));

				application.setProjectOutlayDetails(projectOutlayDetails);

				appForAppRef.close();
				appForAppRef = null;
			}

			/*
			 * if app ref no are entered if
			 * ((cgpan.equals(""))&&(!(appRefNo.equals("")))) {
			 * application=getAppForAppRef(appRefNo);
			 * projectOutlayDetails=application.getProjectOutlayDetails(); }
			 * //if cgpan are entered else if
			 * ((!(cgpan.equals("")))&&(appRefNo.equals(""))) {
			 * application=getAppForCgpan(cgpan); }
			 */
			projectOutlayDetails = application.getProjectOutlayDetails();
			appRefNo = application.getAppRefNo();

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values for application object set successfully...");

			// SSI Details
			CallableStatement ssiObj = connection.prepareCall(
			// "{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
					"{?=call funcGetSSIDetailforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			ssiObj.setString(2, appRefNo); // AppRefNo

			ssiObj.registerOutParameter(1, Types.INTEGER);
			ssiObj.registerOutParameter(33, Types.VARCHAR);

			ssiObj.registerOutParameter(3, Types.INTEGER); // SSI Ref No
			ssiObj.registerOutParameter(4, Types.VARCHAR); // Already Covered by
															// CGTSI
			ssiObj.registerOutParameter(5, Types.VARCHAR); // Already assisted
															// by bank
			ssiObj.registerOutParameter(6, Types.VARCHAR); // SSI Unique No
			ssiObj.registerOutParameter(7, Types.VARCHAR); // NPA
			ssiObj.registerOutParameter(8, Types.VARCHAR); // Constitution
			ssiObj.registerOutParameter(9, Types.VARCHAR); // SSI Type
			ssiObj.registerOutParameter(10, Types.VARCHAR); // unit Name
			ssiObj.registerOutParameter(11, Types.VARCHAR); // SSI reg no
			ssiObj.registerOutParameter(12, Types.DATE); // Commencement Date
			ssiObj.registerOutParameter(13, Types.VARCHAR); // SSI ITPAN
			ssiObj.registerOutParameter(14, Types.VARCHAR); // type of Activity
			ssiObj.registerOutParameter(15, Types.INTEGER); // No Of Employees
			ssiObj.registerOutParameter(16, Types.DOUBLE); // project Sales
			ssiObj.registerOutParameter(17, Types.DOUBLE); // Project Exports
			ssiObj.registerOutParameter(18, Types.VARCHAR); // SSI Address
			ssiObj.registerOutParameter(19, Types.VARCHAR); // SSi City
			ssiObj.registerOutParameter(20, Types.VARCHAR); // Pincode
			ssiObj.registerOutParameter(21, Types.VARCHAR); // Display Defaulter
			ssiObj.registerOutParameter(22, Types.VARCHAR); // SSI District
			ssiObj.registerOutParameter(23, Types.VARCHAR); // SSI State
			ssiObj.registerOutParameter(24, Types.VARCHAR); // Industry Nature
			ssiObj.registerOutParameter(25, Types.VARCHAR); // Indutry Sector
															// name
			ssiObj.registerOutParameter(26, Types.VARCHAR); // Status
			ssiObj.registerOutParameter(27, Types.VARCHAR); // BID
			ssiObj.registerOutParameter(28, Types.DOUBLE); // Outstanding Amount
			ssiObj.registerOutParameter(29, Types.VARCHAR); // MCGS Flag
			/*
			 * ssiObj.executeQuery(); int ssiObjValue=ssiObj.getInt(1);
			 * 
			 * Log.log(Log.DEBUG,"ApplicationDAO","submitPromotersDetails",
			 * "SSI Details :" + ssiObjValue);
			 * 
			 * if(ssiObjValue==Constants.FUNCTION_FAILURE){
			 * 
			 * String error = ssiObj.getString(30);
			 * 
			 * ssiObj.close(); ssiObj=null;
			 * 
			 * connection.rollback();
			 * 
			 * throw new DatabaseException(error); } else {
			 * ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
			 * borrowerDetails.setPreviouslyCovered(ssiObj.getString(4).trim());
			 * borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
			 * borrowerDetails.setNpa(ssiObj.getString(7).trim());
			 * ssiDetails.setConstitution(ssiObj.getString(8));
			 * ssiDetails.setSsiType(ssiObj.getString(9).trim());
			 * ssiDetails.setSsiName(ssiObj.getString(10));
			 * ssiDetails.setRegNo(ssiObj.getString(11));
			 * ssiDetails.setSsiITPan(ssiObj.getString(13));
			 * ssiDetails.setActivityType(ssiObj.getString(14));
			 * ssiDetails.setEmployeeNos(ssiObj.getInt(15));
			 * ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
			 * ssiDetails.setProjectedExports(ssiObj.getDouble(17));
			 * ssiDetails.setAddress(ssiObj.getString(18));
			 * ssiDetails.setCity(ssiObj.getString(19));
			 * ssiDetails.setPincode(ssiObj.getString(20).trim());
			 * ssiDetails.setDistrict(ssiObj.getString(22));
			 * ssiDetails.setState(ssiObj.getString(23));
			 * ssiDetails.setIndustryNature(ssiObj.getString(24));
			 * ssiDetails.setIndustrySector(ssiObj.getString(25));
			 * ssiDetails.setCgbid(ssiObj.getString(27));
			 * 
			 * borrowerDetails.setOsAmt(ssiObj.getDouble(28));
			 */

			ssiObj.registerOutParameter(30, Types.VARCHAR); //
			ssiObj.registerOutParameter(31, Types.VARCHAR); //
			ssiObj.registerOutParameter(32, Types.VARCHAR); //
			// ssiObj.registerOutParameter(33,Types.VARCHAR);

			ssiObj.executeQuery();
			int ssiObjValue = ssiObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
					"SSI Details :" + ssiObjValue);

			if (ssiObjValue == Constants.FUNCTION_FAILURE) {

				// String error = ssiObj.getString(30);

				String error = ssiObj.getString(33);

				ssiObj.close();
				ssiObj = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ssiDetails.setBorrowerRefNo(ssiObj.getInt(3));
				borrowerDetails
						.setPreviouslyCovered(ssiObj.getString(4).trim());
				borrowerDetails.setAssistedByBank(ssiObj.getString(5).trim());
				borrowerDetails.setNpa(ssiObj.getString(7).trim());
				ssiDetails.setConstitution(ssiObj.getString(8));
				ssiDetails.setSsiType(ssiObj.getString(9).trim());
				ssiDetails.setSsiName(ssiObj.getString(10));
				ssiDetails.setRegNo(ssiObj.getString(11));
				ssiDetails.setSsiITPan(ssiObj.getString(13));
				ssiDetails.setActivityType(ssiObj.getString(14));
				ssiDetails.setEmployeeNos(ssiObj.getInt(15));
				ssiDetails.setProjectedSalesTurnover(ssiObj.getDouble(16));
				ssiDetails.setProjectedExports(ssiObj.getDouble(17));
				ssiDetails.setAddress(ssiObj.getString(18));
				ssiDetails.setCity(ssiObj.getString(19));
				ssiDetails.setPincode(ssiObj.getString(20).trim());
				ssiDetails.setDistrict(ssiObj.getString(22));
				ssiDetails.setState(ssiObj.getString(23));
				ssiDetails.setIndustryNature(ssiObj.getString(24));
				ssiDetails.setIndustrySector(ssiObj.getString(25));
				ssiDetails.setCgbid(ssiObj.getString(27));

				// kot
				// ssiDetails.setAddress(ssiObj.getString(28));

				// ssiDetails.setMSE(ssiObj.getString(29));

				// ssiDetails.setMSE(ssiObj.getString(30));

				ssiDetails.setEnterprise(ssiObj.getString(30));

				ssiDetails.setUnitAssisted(ssiObj.getString(31));

				ssiDetails.setWomenOperated(ssiObj.getString(32));

				// ssiDetails.setEnterprise("N");

				// ssiDetails.setUnitAssisted("Y");

				// ssiDetails.setWomenOperated("Y");

				borrowerDetails.setOsAmt(ssiObj.getDouble(28));

				Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
						"OS Amount :" + ssiObj.getDouble(28));
				borrowerDetails.setSsiDetails(ssiDetails);
				application.setBorrowerDetails(borrowerDetails);
				ssiObj.close();
				ssiObj = null;

			}
			ssiDetails = (application.getBorrowerDetails()).getSsiDetails();

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of ssi object set successfully....");

			// Promoters Details
			CallableStatement promoterObj = connection
					.prepareCall("{?=call funcGetPromoterDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			promoterObj.setString(2, appRefNo); // AppRefNo

			promoterObj.registerOutParameter(1, Types.INTEGER);
			promoterObj.registerOutParameter(23, Types.VARCHAR);

			promoterObj.registerOutParameter(3, Types.INTEGER); // SSI RefNo
			promoterObj.registerOutParameter(4, Types.VARCHAR); // Chief title
			promoterObj.registerOutParameter(5, Types.VARCHAR); // First name
			promoterObj.registerOutParameter(6, Types.VARCHAR); // Middle Name
			promoterObj.registerOutParameter(7, Types.VARCHAR); // Last name
			promoterObj.registerOutParameter(8, Types.VARCHAR); // Chief ITPAN
			promoterObj.registerOutParameter(9, Types.VARCHAR); // Gender
			promoterObj.registerOutParameter(10, Types.DATE); // DOB
			promoterObj.registerOutParameter(11, Types.VARCHAR); // Legal Type
			promoterObj.registerOutParameter(12, Types.VARCHAR); // LegalID
			promoterObj.registerOutParameter(13, Types.VARCHAR); // Promoter
																	// First
																	// Name
			promoterObj.registerOutParameter(14, Types.DATE); // Promoter First
																// DOB
			promoterObj.registerOutParameter(15, Types.VARCHAR); // Promoter
																	// FirstITPAN
			promoterObj.registerOutParameter(16, Types.VARCHAR); // Promoter
																	// Second
																	// Name
			promoterObj.registerOutParameter(17, Types.DATE); // Promoter Second
																// DOB
			promoterObj.registerOutParameter(18, Types.VARCHAR); // Promoter
																	// Second
																	// ITPAN
			promoterObj.registerOutParameter(19, Types.VARCHAR); // Promoter
																	// Third
																	// Name
			promoterObj.registerOutParameter(20, Types.DATE); // Promoter Third
																// DOB
			promoterObj.registerOutParameter(21, Types.VARCHAR); // Promoter
																	// Third
																	// ITPAN
			promoterObj.registerOutParameter(22, Types.VARCHAR); // social
																	// Category

			promoterObj.executeQuery();
			int promoterObjValue = promoterObj.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Promoters Details :" + promoterObjValue);

			if (promoterObjValue == Constants.FUNCTION_FAILURE) {

				String error = promoterObj.getString(23);

				promoterObj.close();
				promoterObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Promoter Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ssiDetails.setCpTitle(promoterObj.getString(4));
				ssiDetails.setCpFirstName(promoterObj.getString(5));
				if (promoterObj.getString(6) != null
						&& !promoterObj.getString(6).equals("")) {
					ssiDetails.setCpMiddleName(promoterObj.getString(6));
				} else {
					ssiDetails.setCpMiddleName("");
				}

				ssiDetails.setCpLastName(promoterObj.getString(7));
				ssiDetails.setCpITPAN(promoterObj.getString(8));
				ssiDetails.setCpGender(promoterObj.getString(9).trim());
				ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(10)));
				ssiDetails.setCpLegalID(promoterObj.getString(11));
				ssiDetails.setCpLegalIdValue(promoterObj.getString(12));
				ssiDetails.setFirstName(promoterObj.getString(13));
				ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(14)));
				ssiDetails.setFirstItpan(promoterObj.getString(15));
				ssiDetails.setSecondName(promoterObj.getString(16));
				ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(17)));
				ssiDetails.setSecondItpan(promoterObj.getString(18));
				ssiDetails.setThirdName(promoterObj.getString(19));
				ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(promoterObj
						.getDate(20)));
				ssiDetails.setThirdItpan(promoterObj.getString(21));
				ssiDetails.setSocialCategory(promoterObj.getString(22));

				(application.getBorrowerDetails()).setSsiDetails(ssiDetails);
				promoterObj.close();
				promoterObj = null;
			}

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of promoter object set successfully....");

			// Guarantors Names
			CallableStatement guarantorObj = connection
					.prepareCall("{?=call packGetOldPersonalGuarantee.funcGetOldPerGuarforAppRef(?,?,?)}");

			guarantorObj.setString(2, appRefNo); // AppRefNo

			guarantorObj.registerOutParameter(1, Types.INTEGER);
			guarantorObj.registerOutParameter(4, Types.VARCHAR);

			guarantorObj.registerOutParameter(3, Constants.CURSOR);

			guarantorObj.executeQuery();
			int guarantorObjValue = guarantorObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Guarantors Details :" + guarantorObjValue);

			if (guarantorObjValue == Constants.FUNCTION_FAILURE) {

				String error = guarantorObj.getString(4);

				guarantorObj.close();
				guarantorObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Gurantor Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ResultSet guarantorsResults = (ResultSet) guarantorObj
						.getObject(3);
				int i = 0;
				while (guarantorsResults.next()) {
					if (i == 0) {
						projectOutlayDetails
								.setGuarantorsName1(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth1(guarantorsResults
										.getDouble(2));
					}
					if (i == 1) {
						projectOutlayDetails
								.setGuarantorsName2(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth2(guarantorsResults
										.getDouble(2));
					}
					if (i == 2) {
						projectOutlayDetails
								.setGuarantorsName3(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth3(guarantorsResults
										.getDouble(2));
					}
					if (i == 3) {
						projectOutlayDetails
								.setGuarantorsName4(guarantorsResults
										.getString(1));
						projectOutlayDetails
								.setGuarantorsNetWorth4(guarantorsResults
										.getDouble(2));
					}
					i++;

				}
				guarantorObj.close();
				guarantorObj = null;
				guarantorsResults.close();
				guarantorsResults = null;
			}

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"values of project guarantor object set successfully....");

			// Primary Security Details
			CallableStatement psObj = connection
					.prepareCall("{?=call packGetOldPrimarySecurity.funcGetOldPriSecforAppRef(?,?,?)}");

			psObj.setString(2, appRefNo); // AppRefNo

			psObj.registerOutParameter(1, Types.INTEGER);
			psObj.registerOutParameter(4, Types.VARCHAR);

			psObj.registerOutParameter(3, Constants.CURSOR);

			psObj.executeQuery();
			int psObjValue = psObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Primary Security Details :" + psObjValue);

			if (psObjValue == Constants.FUNCTION_FAILURE) {

				String error = psObj.getString(4);

				psObj.close();
				psObj = null;

				connection.rollback();
				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Primary Security Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ResultSet psResults = (ResultSet) psObj.getObject(3);
				while (psResults.next()) {
					if ((psResults.getString(1)).equals("Land")) {
						primarySecurityDetails.setLandParticulars(psResults
								.getString(2));
						primarySecurityDetails.setLandValue(psResults
								.getDouble(3));
					}
					if ((psResults.getString(1)).equals("Building")) {
						primarySecurityDetails.setBldgParticulars(psResults
								.getString(2));
						primarySecurityDetails.setBldgValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Machinery")) {
						primarySecurityDetails.setMachineParticulars(psResults
								.getString(2));
						primarySecurityDetails.setMachineValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Fixed Assets")) {
						primarySecurityDetails.setAssetsParticulars(psResults
								.getString(2));
						primarySecurityDetails.setAssetsValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Current Assets")) {
						primarySecurityDetails
								.setCurrentAssetsParticulars(psResults
										.getString(2));
						primarySecurityDetails.setCurrentAssetsValue(psResults
								.getDouble(3));

					}
					if ((psResults.getString(1)).equals("Others")) {
						primarySecurityDetails.setOthersParticulars(psResults
								.getString(2));
						primarySecurityDetails.setOthersValue(psResults
								.getDouble(3));

					}

				}
				psResults.close();
				psResults = null;
				psObj.close();
				psObj = null;

			}

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of ps object set successfully....");

			// Retrieving Loan type from the application
			String applicationLoanType = application.getLoanType();

			// This function is called for a term Credit / Composite / Both
			// Application

			CallableStatement termLoanObj = connection
					.prepareCall("{?=call funcGetOldTermLoanforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			termLoanObj.setString(2, appRefNo); // AppRefNo

			termLoanObj.registerOutParameter(1, Types.INTEGER);
			termLoanObj.registerOutParameter(22, Types.VARCHAR);

			termLoanObj.registerOutParameter(3, Types.DOUBLE); // TRM Amount
																// Sanctioned
			termLoanObj.registerOutParameter(4, Types.DATE); // TRM Amt
																// sanctioned
																// Date
			termLoanObj.registerOutParameter(5, Types.DOUBLE); // TRM Promoter
																// Conribution
			termLoanObj.registerOutParameter(6, Types.DOUBLE); // Subsidy or
																// Equity
			termLoanObj.registerOutParameter(7, Types.DOUBLE); // Others

			termLoanObj.registerOutParameter(8, Types.DOUBLE); // Credit to be
																// guaranteed
			termLoanObj.registerOutParameter(9, Types.INTEGER); // Tenure
			termLoanObj.registerOutParameter(10, Types.VARCHAR); // Interest
																	// Type
			termLoanObj.registerOutParameter(11, Types.DOUBLE); // Interest Rate
			termLoanObj.registerOutParameter(12, Types.DOUBLE); // Bench Mark
																// PLR
			termLoanObj.registerOutParameter(13, Types.DOUBLE); // PLR
			termLoanObj.registerOutParameter(14, Types.INTEGER); // Moratorium
			termLoanObj.registerOutParameter(15, Types.DATE); // First
																// Installment
																// Date
			termLoanObj.registerOutParameter(16, Types.INTEGER); // No Of
																	// Installments
			termLoanObj.registerOutParameter(17, Types.INTEGER); // Repayment
																	// Periodicity
			termLoanObj.registerOutParameter(18, Types.VARCHAR); // PLR Type
			termLoanObj.registerOutParameter(19, Types.DOUBLE); // DBR Amount
			termLoanObj.registerOutParameter(20, Types.DATE); // DBR First Date
			termLoanObj.registerOutParameter(21, Types.DATE); // DBR Last Date

			termLoanObj.executeQuery();
			int termLoanObjValue = termLoanObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Term Loan Details :" + termLoanObjValue);

			if (termLoanObjValue != Constants.FUNCTION_SUCCESS
					&& termLoanObjValue != Constants.FUNCTION_NO_DATA) {

				String error = termLoanObj.getString(22);

				termLoanObj.close();
				termLoanObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Term Loan Exception :" + error);

				throw new DatabaseException(error);
			} else if (termLoanObjValue == Constants.FUNCTION_NO_DATA) {
				projectOutlayDetails.setTermCreditSanctioned(0); // Amount
																	// Sanctioned
				java.sql.Date sanctionedDate = termLoanObj.getDate(4);
				termLoan.setAmountSanctionedDate(null); // sanctioned Date
				sanctionedDate = null;
				projectOutlayDetails.setTcPromoterContribution(0);
				projectOutlayDetails.setTcSubsidyOrEquity(0);
				projectOutlayDetails.setTcOthers(0);
				if ((applicationLoanType.equals("TC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					termLoan.setCreditGuaranteed(0);
					termLoan.setTenure(0);
					termLoan.setInterestType("T");
					termLoan.setInterestRate(0);
					termLoan.setBenchMarkPLR(0);
					termLoan.setPlr(0);
					termLoan.setRepaymentMoratorium(0);
					termLoan.setFirstInstallmentDueDate(null);
					termLoan.setNoOfInstallments(0);
					termLoan.setPeriodicity(0);
					termLoan.setTypeOfPLR(null);
					termLoan.setAmtDisbursed(0);
					// if(termLoanObj.getDate(20)!=null)
					// {
					// if
					// (DateHelper.sqlToUtilDate(termLoanObj.getDate(20)).equals(DateHelper.sqlToUtilDate(termLoanObj.getDate(21))))
					// {
					termLoan.setFirstDisbursementDate(null);
					termLoan.setFinalDisbursementDate(null);

					// }else {

					// termLoan.setFirstDisbursementDate(DateHelper.sqlToUtilDate(termLoanObj.getDate(20)));
					// termLoan.setFinalDisbursementDate(DateHelper.sqlToUtilDate(termLoanObj.getDate(21)));

					// }

				}

				Log.log(Log.INFO, "ApplicationDAO", "getApplication",
						"termloan set....");

				// }
				termLoanObj.close();
				termLoanObj = null;

			} else {
				projectOutlayDetails.setTermCreditSanctioned(termLoanObj
						.getDouble(3)); // Amount Sanctioned
				java.sql.Date sanctionedDate = termLoanObj.getDate(4);
				termLoan.setAmountSanctionedDate(DateHelper
						.sqlToUtilDate(sanctionedDate)); // sanctioned Date
				sanctionedDate = null;
				projectOutlayDetails.setTcPromoterContribution(termLoanObj
						.getDouble(5));
				projectOutlayDetails.setTcSubsidyOrEquity(termLoanObj
						.getDouble(6));
				projectOutlayDetails.setTcOthers(termLoanObj.getDouble(7));
				if ((applicationLoanType.equals("TC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					termLoan.setCreditGuaranteed(termLoanObj.getDouble(8));
					termLoan.setTenure(termLoanObj.getInt(9));
					termLoan.setInterestType(termLoanObj.getString(10));
					termLoan.setInterestRate(termLoanObj.getDouble(11));
					termLoan.setBenchMarkPLR(termLoanObj.getDouble(12));
					termLoan.setPlr(termLoanObj.getDouble(13));
					termLoan.setRepaymentMoratorium(termLoanObj.getInt(14));
					termLoan.setFirstInstallmentDueDate(DateHelper
							.sqlToUtilDate(termLoanObj.getDate(15)));
					termLoan.setNoOfInstallments(termLoanObj.getInt(16));
					termLoan.setPeriodicity(termLoanObj.getInt(17));
					termLoan.setTypeOfPLR(termLoanObj.getString(18));
					termLoan.setAmtDisbursed(termLoanObj.getDouble(19));
					if (termLoanObj.getDate(20) != null) {
						if (DateHelper.sqlToUtilDate(termLoanObj.getDate(20))
								.equals(DateHelper.sqlToUtilDate(termLoanObj
										.getDate(21)))) {
							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(null);

						} else {

							termLoan.setFirstDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(20)));
							termLoan.setFinalDisbursementDate(DateHelper
									.sqlToUtilDate(termLoanObj.getDate(21)));

						}

					}

					Log.log(Log.INFO, "ApplicationDAO", "getApplication",
							"termloan set....");

				}
				termLoanObj.close();
				termLoanObj = null;

			}

			// retrieving the repayment details
			if ((applicationLoanType.equals("TC"))
					|| (applicationLoanType.equals("CC"))
					|| (applicationLoanType.equals("BO"))) {
				CallableStatement repaymentObj = connection
						.prepareCall("{?=call funcGetRepSchForAppRef(?,?,?,?,?,?)}");

				repaymentObj.setString(2, appRefNo);

				repaymentObj.registerOutParameter(1, Types.INTEGER);
				repaymentObj.registerOutParameter(7, Types.VARCHAR);

				repaymentObj.registerOutParameter(3, Types.INTEGER);
				repaymentObj.registerOutParameter(4, Types.DATE);
				repaymentObj.registerOutParameter(5, Types.INTEGER);
				repaymentObj.registerOutParameter(6, Types.INTEGER);

				repaymentObj.executeQuery();
				int repaymentObjValue = repaymentObj.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"Term Loan Details :" + repaymentObjValue);

				if (repaymentObjValue != Constants.FUNCTION_SUCCESS) {

					String error = repaymentObj.getString(7);

					repaymentObj.close();
					repaymentObj = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
							"Term Loan Exception :" + error);

					throw new DatabaseException(error);
				} else {
					termLoan.setRepaymentMoratorium(repaymentObj.getInt(3));
					termLoan.setFirstInstallmentDueDate(DateHelper
							.sqlToUtilDate(repaymentObj.getDate(4)));
					termLoan.setPeriodicity(repaymentObj.getInt(5));
					termLoan.setNoOfInstallments(repaymentObj.getInt(6));
					repaymentObj.close();
					repaymentObj = null;
				}
			}

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of term loan object set successfully....");

			// This function is called when the outstanding details ahve to be
			// retrieved
			if ((applicationLoanType.equals("TC"))
					|| (applicationLoanType.equals("CC"))
					|| (applicationLoanType.equals("BO"))) {
				CallableStatement tcOutObj = connection
						.prepareCall("{?=call funcGetTCOutStanding(?,?,?,?)}");

				tcOutObj.setString(2, appRefNo);

				tcOutObj.registerOutParameter(1, Types.INTEGER);
				tcOutObj.registerOutParameter(5, Types.VARCHAR);
				tcOutObj.registerOutParameter(3, Types.DOUBLE);
				tcOutObj.registerOutParameter(4, Types.DATE);

				tcOutObj.executeQuery();
				int tcOutObjValue = tcOutObj.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"TWorking Capital Loan Details :" + tcOutObjValue);

				if (tcOutObjValue == Constants.FUNCTION_FAILURE) {

					String error = tcOutObj.getString(5);

					tcOutObj.close();
					tcOutObj = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
							"Working Capital Exception :" + error);

					throw new DatabaseException(error);
				} else if (tcOutObjValue == Constants.FUNCTION_NO_DATA) {
					termLoan.setPplOS(0);

					termLoan.setPplOS(tcOutObj.getDouble(3));
					termLoan.setPplOsAsOnDate(DateHelper.sqlToUtilDate(tcOutObj
							.getDate(4)));

					tcOutObj.close();
					tcOutObj = null;
				} else {

					if (tcOutObj.getDouble(3) == -1) {
						termLoan.setPplOS(0);

					} else {

						termLoan.setPplOS(tcOutObj.getDouble(3));
						termLoan.setPplOsAsOnDate(DateHelper
								.sqlToUtilDate(tcOutObj.getDate(4)));

					}
					tcOutObj.close();
					tcOutObj = null;
				}
			}
			// This function is called for a Working Capital / Composite / Both
			// Application

			CallableStatement wcObj = connection
					.prepareCall("{?=call funcGetOldWCforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			wcObj.setString(2, appRefNo); // AppRefNo

			wcObj.registerOutParameter(1, Types.INTEGER);
			wcObj.registerOutParameter(17, Types.VARCHAR);

			wcObj.registerOutParameter(3, Types.DOUBLE); // Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(4, Types.DOUBLE); // Non Fund Based Limit
															// Sanctioned
			wcObj.registerOutParameter(5, Types.DOUBLE); // Promoter
															// Contribution
			wcObj.registerOutParameter(6, Types.DOUBLE); // Subsidy/Equity
			wcObj.registerOutParameter(7, Types.DOUBLE); // Others
			wcObj.registerOutParameter(8, Types.DOUBLE); // Interest
			wcObj.registerOutParameter(9, Types.DOUBLE); // Commission
			wcObj.registerOutParameter(10, Types.DATE); // Fund Based Limit
														// Sanctioned date
			wcObj.registerOutParameter(11, Types.DATE); // NonFund Based Date
			wcObj.registerOutParameter(12, Types.DOUBLE); // Fund Based Credit
			wcObj.registerOutParameter(13, Types.DOUBLE); // Non Fund Based
															// Credit
			wcObj.registerOutParameter(14, Types.DOUBLE); // PLR
			wcObj.registerOutParameter(15, Types.VARCHAR); // PLR Type
			wcObj.registerOutParameter(16, Types.VARCHAR); // Interest Type

			wcObj.executeQuery();
			int wcObjValue = wcObj.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"TWorking Capital Loan Details :" + wcObjValue);

			if (wcObjValue != Constants.FUNCTION_SUCCESS
					&& wcObjValue != Constants.FUNCTION_NO_DATA) {

				String error = wcObj.getString(17);

				wcObj.close();
				wcObj = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Working Capital Exception :" + error);

				throw new DatabaseException(error);
			} else if (wcObjValue == Constants.FUNCTION_NO_DATA) {
				projectOutlayDetails.setWcFundBasedSanctioned(0);
				projectOutlayDetails.setWcNonFundBasedSanctioned(0);
				projectOutlayDetails.setWcPromoterContribution(0);
				projectOutlayDetails.setWcSubsidyOrEquity(0);
				projectOutlayDetails.setWcOthers(0);
				if ((applicationLoanType.equals("WC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					workingCapital.setLimitFundBasedInterest(0);
					workingCapital.setLimitNonFundBasedCommission(0);
					workingCapital.setLimitFundBasedSanctionedDate(null);
					workingCapital.setLimitNonFundBasedSanctionedDate(null);
					workingCapital.setCreditFundBased(0);
					workingCapital.setCreditNonFundBased(0);
					workingCapital.setWcPlr(0);
					workingCapital.setWcTypeOfPLR(null);
					workingCapital.setWcInterestType("T");
					Log.log(Log.ERROR,
							"ApplicationDAO",
							"getApplication",
							"Working Interest type:"
									+ workingCapital.getWcInterestType());
				}

			} else {
				projectOutlayDetails.setWcFundBasedSanctioned(wcObj
						.getDouble(3));
				projectOutlayDetails.setWcNonFundBasedSanctioned(wcObj
						.getDouble(4));
				projectOutlayDetails.setWcPromoterContribution(wcObj
						.getDouble(5));
				projectOutlayDetails.setWcSubsidyOrEquity(wcObj.getDouble(6));
				projectOutlayDetails.setWcOthers(wcObj.getDouble(7));
				if ((applicationLoanType.equals("WC"))
						|| (applicationLoanType.equals("CC"))
						|| (applicationLoanType.equals("BO"))) {
					workingCapital
							.setLimitFundBasedInterest(wcObj.getDouble(8));
					workingCapital.setLimitNonFundBasedCommission(wcObj
							.getDouble(9));
					workingCapital.setLimitFundBasedSanctionedDate(DateHelper
							.sqlToUtilDate(wcObj.getDate(10)));
					workingCapital
							.setLimitNonFundBasedSanctionedDate(DateHelper
									.sqlToUtilDate(wcObj.getDate(11)));
					workingCapital.setCreditFundBased(wcObj.getDouble(12));
					workingCapital.setCreditNonFundBased(wcObj.getDouble(13));
					workingCapital.setWcPlr(wcObj.getDouble(14));
					workingCapital.setWcTypeOfPLR(wcObj.getString(15));
					workingCapital
							.setWcInterestType(wcObj.getString(16).trim());
					Log.log(Log.ERROR,
							"ApplicationDAO",
							"getApplication",
							"Working Interest type:"
									+ workingCapital.getWcInterestType());
				}
				wcObj.close();
				wcObj = null;

			}

			if ((applicationLoanType.equals("WC"))
					|| (applicationLoanType.equals("CC"))
					|| (applicationLoanType.equals("BO"))) {
				CallableStatement wcOutObj = connection
						.prepareCall("{?=call funcGetWCOutStanding(?,?,?,?,?,?,?,?)}");

				wcOutObj.setString(2, appRefNo); // AppRefNo

				wcOutObj.registerOutParameter(1, Types.INTEGER);
				wcOutObj.registerOutParameter(9, Types.VARCHAR);

				wcOutObj.registerOutParameter(3, Types.DOUBLE); // OS Fund Based
																// Principal
				wcOutObj.registerOutParameter(4, Types.DOUBLE); // OS Fund Based
																// Interest
				wcOutObj.registerOutParameter(5, Types.DATE); // OS Fund Based
																// Date
				wcOutObj.registerOutParameter(6, Types.DOUBLE); // OS Non Fund
																// Based
																// Principal
				wcOutObj.registerOutParameter(7, Types.DOUBLE); // OS Non Fund
																// Based
																// Interest
				wcOutObj.registerOutParameter(8, Types.DATE); // OS Non Fund
																// Based Date

				wcOutObj.executeQuery();
				int wcOutObjValue = wcOutObj.getInt(1);

				Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
						"TWorking Capital Loan Details :" + wcOutObjValue);

				if (wcOutObjValue == Constants.FUNCTION_FAILURE) {

					String error = wcOutObj.getString(9);

					wcOutObj.close();
					wcOutObj = null;

					connection.rollback();

					Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
							"Working Capital Exception :" + error);

					throw new DatabaseException(error);
				} else if (wcOutObjValue == Constants.FUNCTION_NO_DATA) {

					workingCapital.setOsFundBasedPpl(0);
					workingCapital.setOsFundBasedInterestAmt(0);
					workingCapital.setOsFundBasedAsOnDate(null);
					workingCapital.setOsNonFundBasedPpl(0);
					workingCapital.setOsNonFundBasedCommissionAmt(0);
					workingCapital.setOsNonFundBasedAsOnDate(null);
					wcOutObj.close();
					wcOutObj = null;

				} else {

					workingCapital.setOsFundBasedPpl(wcOutObj.getDouble(3));
					workingCapital.setOsFundBasedInterestAmt(wcOutObj
							.getDouble(4));
					workingCapital.setOsFundBasedAsOnDate(DateHelper
							.sqlToUtilDate(wcOutObj.getDate(5)));
					workingCapital.setOsNonFundBasedPpl(wcOutObj.getDouble(6));
					workingCapital.setOsNonFundBasedCommissionAmt(wcOutObj
							.getDouble(7));
					workingCapital.setOsNonFundBasedAsOnDate(DateHelper
							.sqlToUtilDate(wcOutObj.getDate(8)));
					wcOutObj.close();
					wcOutObj = null;
				}

			}
			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"values of working capital object set successfully....");

			// This function retrieves the Securitization Details
			CallableStatement getSecDtls = connection
					.prepareCall("{?=call funcGetOldSecDetails(?,?,?,?,?,?,?,?,?,?)}");

			getSecDtls.setString(2, appRefNo); // AppRefNo

			getSecDtls.registerOutParameter(1, Types.INTEGER);
			getSecDtls.registerOutParameter(11, Types.VARCHAR);

			getSecDtls.registerOutParameter(3, Types.DOUBLE); // spread over PLR
			getSecDtls.registerOutParameter(4, Types.VARCHAR); // repayment in
																// equal
																// installments
			getSecDtls.registerOutParameter(5, Types.DOUBLE); // tangible net
																// worth
			getSecDtls.registerOutParameter(6, Types.DOUBLE); // fixed asset
			getSecDtls.registerOutParameter(7, Types.DOUBLE); // current ratio
			getSecDtls.registerOutParameter(8, Types.DOUBLE); // min dscr
			getSecDtls.registerOutParameter(9, Types.DOUBLE); // avg dscr
			getSecDtls.registerOutParameter(10, Types.DOUBLE); // financial part
																// of a unit

			getSecDtls.executeQuery();

			int getSecDtlsValue = getSecDtls.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getApplication",
					"Securitization Details :" + getSecDtlsValue);

			if (getSecDtlsValue == Constants.FUNCTION_FAILURE) {

				String error = getSecDtls.getString(11);

				getSecDtls.close();
				getSecDtls = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Securitization Detail Exception :" + error);

				throw new DatabaseException(error);
			} else if (getSecDtlsValue == Constants.FUNCTION_NO_DATA) {
				securitization.setSpreadOverPLR(0);
				securitization.setPplRepaymentInEqual("Y");
				securitization.setTangibleNetWorth(0);
				securitization.setFixedACR(0);
				securitization.setCurrentRatio(0);
				securitization.setMinimumDSCR(0);
				securitization.setAvgDSCR(0);
				securitization.setFinancialPartUnit(0);
				getSecDtls.close();
				getSecDtls = null;

			} else {

				securitization.setSpreadOverPLR(getSecDtls.getDouble(3));
				securitization.setPplRepaymentInEqual(getSecDtls.getString(4));
				securitization.setTangibleNetWorth(getSecDtls.getDouble(5));
				securitization.setFixedACR(getSecDtls.getDouble(6));
				securitization.setCurrentRatio(getSecDtls.getDouble(7));
				securitization.setMinimumDSCR(getSecDtls.getDouble(8));
				securitization.setAvgDSCR(getSecDtls.getDouble(9));
				securitization.setFinancialPartUnit(getSecDtls.getDouble(10));
				getSecDtls.close();
				getSecDtls = null;

			}

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			MCGFDetails mcgfDetails = mcgsProcessor.getOldMcgsDetails(appRefNo,
					connection);
			if (mcgfDetails != null) {
				application.setMCGFDetails(mcgfDetails);

			}
			mcgsProcessor = null;
			mcgfDetails = null;

			borrowerDetails.setSsiDetails(ssiDetails);
			application.setBorrowerDetails(borrowerDetails);

			application.setTermLoan(termLoan);

			application.setWc(workingCapital);
			application.setSecuritization(securitization);

			projectOutlayDetails
					.setPrimarySecurityDetails(primarySecurityDetails);
			application.setProjectOutlayDetails(projectOutlayDetails);

			borrowerDetails = null;
			ssiDetails = null;
			projectOutlayDetails = null;
			primarySecurityDetails = null;
			termLoan = null;
			workingCapital = null;
			securitization = null;

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Exited from appDAO successfully....");

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getApplication",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getApplication",
				"Exited. Memory : " + Runtime.getRuntime().freeMemory());

		return application;
	}

	/**
	 * This method returns an ArrayList of CGPANs for the given id and type.
	 * 
	 * @param Id
	 *            - Id could be CGBID or member id.
	 * @param type
	 *            could be for Additional Term Loan / Renewal / Enhancement
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 399FD11800A4
	 */
	public ArrayList getCgpans(String mliId, String borrowerId, int type,
			String borrowerName) throws DatabaseException {
		ArrayList cgpansList = new ArrayList();
		Connection connection = DBConnection.getConnection(false);

		String bankId = "";
		String zoneId = "";
		String branchId = "";

		Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans", "mliID ;" + mliId);
		Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans", "borrowerId;"
				+ borrowerId);
		Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans", "type ;" + type);
		Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans", " borrower name;"
				+ borrowerName);

		if ((mliId != "") || (borrowerId != "")) {

			Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
					"Entering into the method for gettin cgpans....");

			try {

				CallableStatement cgpanList = connection
						.prepareCall("{?=call packGetCGPANEnhancement.funcGetCGPANEnhancement(?,?,?,?,?,?,?)}");
				cgpanList.registerOutParameter(1, Types.INTEGER);
				cgpanList.registerOutParameter(7, Constants.CURSOR); // retrieving
																		// the
																		// cgpan
																		// list
				cgpanList.registerOutParameter(8, Types.VARCHAR);

				if (type == 0) {
					cgpanList.setString(2, "TCE");
					Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
							"Setting TCE..");

				} else if (type == 1) {
					cgpanList.setString(2, "WCE");
					Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
							"Setting WCE..");

				} else if (type == 2) {
					cgpanList.setString(2, "WCR");
					Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
							"Setting WCR..");

				}

				bankId = mliId.substring(0, 4); // getting the bank id

				zoneId = mliId.substring(4, 8); // getting the zone id

				branchId = mliId.substring(8, 12); // getting the branch id

				if (bankId.equals("")) {
					cgpanList.setString(3, null);
				} else {
					cgpanList.setString(3, bankId);
				}

				if (zoneId.equals("")) {
					cgpanList.setString(4, null);
				} else {
					cgpanList.setString(4, zoneId);
				}

				if (branchId.equals("")) {
					cgpanList.setString(5, null);
				} else {
					cgpanList.setString(5, branchId);
				}

				if (borrowerId.equals("")) {
					cgpanList.setString(6, null);
				} else {
					cgpanList.setString(6, borrowerId);
				}

				cgpanList.execute();

				int functionReturnValue = cgpanList.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans",
						"Get Cgpans Result :" + functionReturnValue);

				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = cgpanList.getString(8);

					cgpanList.close();
					cgpanList = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					ResultSet cgpanResults = (ResultSet) cgpanList.getObject(7);

					while (cgpanResults.next()) {
						String cgPAN = cgpanResults.getString(1);

						Log.log(Log.DEBUG, "ApplicationDAO", "getCgpans",
								"CGPAN retrieved.." + cgPAN);

						cgpansList.add(cgPAN);
					}
					cgpanResults.close();
					cgpanResults = null;
					cgpanList.close();
					cgpanList = null;

				}

				connection.commit();

			} catch (SQLException sqlException) {

				Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
						sqlException.getMessage());
				Log.logException(sqlException);

				try {
					connection.rollback();
				} catch (SQLException ignore) {
					Log.log(Log.INFO, "ApplicationDAO", "getCgpans",
							ignore.getMessage());
				}

				throw new DatabaseException(sqlException.getMessage());

			} finally {
				DBConnection.freeConnection(connection);
			}

		}

		Log.log(Log.INFO, "ApplicationDAO", "getCgpans", "Exited");

		return cgpansList;
	}

	public void updateApplication(Application application, String createdBy)
			throws DatabaseException {
		MCGSProcessor mcgsProcessor = new MCGSProcessor();
		RiskManagementProcessor rpProcessor = new RiskManagementProcessor();

		Connection connection = DBConnection.getConnection(false);
		try {

			String subSchemeName = rpProcessor.getSubScheme(application);
			application.setSubSchemeName(subSchemeName);

			// double balanceAppAmt = getBalanceApprovedAmt(application);

			int ssiRefNo = ((application.getBorrowerDetails()).getSsiDetails())
					.getBorrowerRefNo();

			String ssiRefNumber = Integer.toString(ssiRefNo);
			RpDAO rpDAO1 = new RpDAO();
			double prevTotalSancAmt = rpDAO1
					.getTotalSanctionedAmountNew(ssiRefNumber);
			ApplicationDAO appdao = new ApplicationDAO();

			double prevTotalHandloomSancAmt = appdao
					.getTotalSanctionedHandloomAmountNew(ssiRefNumber);

			double currentCreditAmount = 0;
			if (application.getLoanType().equals("TC")) {
				currentCreditAmount = application.getTermLoan()
						.getCreditGuaranteed();
			} else if (application.getLoanType().equals("CC")) {
				currentCreditAmount = application.getTermLoan()
						.getCreditGuaranteed()
						+ application.getWc().getCreditFundBased()
						+ +application.getWc().getCreditNonFundBased();
			} else if (application.getLoanType().equals("WC")) {
				currentCreditAmount = application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased();
			}

			if ((currentCreditAmount > 200000)
					&& (application.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto  Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			} else if ((currentCreditAmount + prevTotalHandloomSancAmt > 200000)
					&& (application.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Guarantee of Rs. "
								+ prevTotalHandloomSancAmt
								+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto upto Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			}

			/*
			 * start comments by Path on 6th oct,06
			 * if(application.getLoanType().equals("TC")||
			 * application.getLoanType().equals("CC")) {
			 * if(application.getTermLoan().getCreditGuaranteed() >
			 * balanceAppAmt) { throw new DatabaseException(
			 * "Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
			 * + balanceAppAmt); } } else
			 * if(application.getLoanType().equals("WC")) {
			 * if(application.getWc().getCreditFundBased()+
			 * application.getWc().getCreditNonFundBased() > balanceAppAmt) {
			 * throw new DatabaseException(
			 * "Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
			 * + balanceAppAmt); } } else
			 * if(application.getLoanType().equals("BO")) {
			 * if(application.getTermLoan().getCreditGuaranteed() +
			 * application.getWc().getCreditFundBased()+
			 * application.getWc().getCreditNonFundBased() > balanceAppAmt) {
			 * throw new DatabaseException(
			 * "Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
			 * + balanceAppAmt); } }
			 * 
			 * end comments by Path on 6th oct,06
			 */

			updateApplicationDtl(application, createdBy, connection);
			//// System.out.println("test1");
			if (application.getStatus().equals("RE")) {
				updateSsiDtl(application, createdBy, connection);
			}
			//// System.out.println("test2");
			// updateOtherDtls(application,createdBy,connection);
			updateGuarantorDtls(application, createdBy, connection);
			//// System.out.println("test3");
			updateTermLoanDtl(application, createdBy, connection);
			updateWCDtl(application, createdBy, connection);
			updateSecDetails(application, createdBy, connection);
			MCGFDetails mcgfDetails = application.getMCGFDetails();
			if (mcgfDetails != null) {
				String appRefNo = application.getAppRefNo();
				mcgfDetails.setApplicationReferenceNumber(appRefNo);
				mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
						connection);
			}

			connection.commit();

		} catch (SQLException e) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitApp", e.getMessage());
			Log.logException(e);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						ignore.getMessage());
			}

			throw new DatabaseException("Unable to submit Application");
		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	/**
	 * This method is used to update the modified application details.
	 * 
	 * @param application
	 * @throws DatabaseException
	 * @roseuid 39A64BE00233
	 */
	public void updateApplicationDtl(Application application, String createdBy,
			Connection connection) throws DatabaseException {
		// MCGSProcessor mcgsProcessor=new MCGSProcessor();

		Log.log(Log.INFO, "ApplicationDAO", "updateApplicationDtl", "Entered");

		// java.util.Date sysDate=new java.util.Date();
		String appLoanType = application.getLoanType();
		//System.out.println("In AppDAO appLoanType:" + appLoanType);

		Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationDtl",
				"Application Loan type in DAO class :" + appLoanType);

		try {

			int approvedAmt;
			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();
			SubSchemeValues subSchemeValues = new SubSchemeValues();

			String subSchemeName = rpProcessor.getSubScheme(application);
			if (!subSchemeName.equals("GLOBAL")) {
				subSchemeValues = rpProcessor.getSubSchemeValues(subSchemeName);
				if (subSchemeValues == null) {
					double exposureAmount = subSchemeValues
							.getMaxBorrowerExposureAmount();

					CallableStatement approvedAmount = connection
							.prepareCall("{?=call funcGetApprovedAmt(?,?,?)}");

					approvedAmount.registerOutParameter(1, Types.INTEGER);
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"updateApplicationDtl",
							"SSi Ref No :"
									+ ((application.getBorrowerDetails())
											.getSsiDetails())
											.getBorrowerRefNo());
					approvedAmount
							.setDouble(2, ((application.getBorrowerDetails())
									.getSsiDetails()).getBorrowerRefNo());
					approvedAmount.registerOutParameter(3, Types.DOUBLE);
					approvedAmount.registerOutParameter(4, Types.VARCHAR);

					approvedAmount.executeQuery();

					int approvedAmountValue = approvedAmount.getInt(1);
					Log.log(Log.DEBUG, "ApplicationDAO",
							"updateApplicationDtl",
							"SSi Details Approved Amount result :"
									+ approvedAmountValue);

					if (approvedAmountValue == Constants.FUNCTION_FAILURE) {

						String error = approvedAmount.getString(4);

						approvedAmount.close();
						approvedAmount = null;

						connection.rollback();

						Log.log(Log.ERROR, "ApplicationDAO",
								"updateApplicationDtl",
								"SSI Detail Approved Amount Exception :"
										+ error);

						throw new DatabaseException(error);
					} else {
						approvedAmt = approvedAmount.getInt(3);

						approvedAmount.close();
						approvedAmount = null;
					}

					if (approvedAmt >= exposureAmount) {
						throw new DatabaseException(
								"Borrower has crossed his exposure limit.Hence ineligible to submit a new application");
					}

				}

			}

			Log.log(Log.INFO, "ApplicationDAO", "updateApplicationDtl",
					"Before updating application in the DAO class....");

			CallableStatement updateAppDtl = connection
					.prepareCall("{?=call funcUpdatetApplicationDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			Log.log(Log.INFO, "ApplicationDAO", "updateApplicationDtl",
					"After connecting");

			updateAppDtl.registerOutParameter(1, Types.INTEGER);
			updateAppDtl.registerOutParameter(31, Types.VARCHAR);

			updateAppDtl.setString(2, application.getAppRefNo());
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"app ref no :" + application.getAppRefNo());

			updateAppDtl.setInt(3, ((application.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo()); // SSI RefNo //ssi
															// ref no
			Log.log(Log.INFO,
					"ApplicationDAO",
					"updateApplication",
					"borr ref no :"
							+ ((application.getBorrowerDetails())
									.getSsiDetails()).getBorrowerRefNo());

			updateAppDtl.setString(4, application.getScheme()); // scheme name
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "scheme :"
					+ application.getScheme());
			updateAppDtl.setString(5, application.getBankId()); // bank id
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "bank id:"
					+ application.getBankId());

			updateAppDtl.setString(6, application.getZoneId()); // zone id
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "zone id:"
					+ application.getZoneId());

			updateAppDtl.setString(7, application.getBranchId()); // branch id
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"branch id:" + application.getBranchId());

			updateAppDtl.setString(8, application.getMliBranchName()); // branch
																		// name
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"branch name:" + application.getMliBranchName());

			if ((application.getMliBranchCode()) != null
					&& !((application.getMliBranchCode()).equals(""))) {
				updateAppDtl.setString(9, application.getMliBranchCode()); // branch
																			// code
			} else {
				updateAppDtl.setString(9, null);
			}
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"branch code:" + application.getMliBranchCode());

			updateAppDtl.setString(10, application.getMliRefNo()); // bank ref
																	// no
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"mli ref no:" + application.getMliRefNo());

			if (appLoanType.equals("CC")) {
				updateAppDtl.setString(11, "Y"); // composite loan
			} else {

				updateAppDtl.setString(11, "N"); // composite loan
			}

			updateAppDtl.setString(12, createdBy); // user id
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"created by:" + createdBy);

			updateAppDtl.setString(13, application.getLoanType()); // loan type
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"loan type:" + application.getLoanType());

			String collateralSecurityValue = (application
					.getProjectOutlayDetails()).getCollateralSecurityTaken();

			updateAppDtl.setString(14, collateralSecurityValue); // collateral
																	// security
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"collateralSecurityValue:" + collateralSecurityValue);

			String thirdPartyValue = (application.getProjectOutlayDetails())
					.getThirdPartyGuaranteeTaken();

			updateAppDtl.setString(15, thirdPartyValue); // Third party taken
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"thirdPartyValue:" + thirdPartyValue);

			if (((application.getProjectOutlayDetails()).getSubsidyName()) != null
					&& !(((application.getProjectOutlayDetails())
							.getSubsidyName()).equals(""))) {
				updateAppDtl.setString(16, (application
						.getProjectOutlayDetails()).getSubsidyName()); // subsidy
																		// scheme
																		// name

			} else {
				updateAppDtl.setString(16, null);

			}
			Log.log(Log.INFO,
					"ApplicationDAO",
					"updateApplication",
					"(application.getProjectOutlayDetails()).getSubsidyName():"
							+ (application.getProjectOutlayDetails())
									.getSubsidyName());

			String rehabilitationValue = (application.getRehabilitation());

			if (rehabilitationValue != null
					&& !(rehabilitationValue.equals(""))) {
				updateAppDtl.setString(17, rehabilitationValue); // collateral
																	// security
			} else {

				updateAppDtl.setString(17, "N"); // collateral security
			}
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"rehabilitationValue:" + rehabilitationValue);

			updateAppDtl.setDouble(18,
					(application.getProjectOutlayDetails()).getProjectOutlay()); // project
																					// outlay
			Log.log(Log.INFO,
					"ApplicationDAO",
					"updateApplication",
					"project outlay:"
							+ (application.getProjectOutlayDetails())
									.getProjectOutlay());

			String cgpanVal = application.getCgpanReference();

			if ((cgpanVal != null) && !cgpanVal.equals("")) {
				updateAppDtl.setString(19, cgpanVal); // cgpan reference

			} else {
				updateAppDtl.setNull(19, java.sql.Types.VARCHAR); // cgpan
																	// reference

			}
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"cgpanVal:" + cgpanVal);

			updateAppDtl.setString(20, createdBy); // created by
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					"createdBy:" + createdBy);

			if ((application.getRemarks() != null)
					&& !((application.getRemarks()).equals(""))) {
				updateAppDtl.setString(21, application.getRemarks()); // remarks
			} else {
				updateAppDtl.setString(21, null);
			}
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "remarks:"
					+ application.getRemarks());

			if (application.getWcEnhancement()) {
				updateAppDtl.setString(22, "EN");
			} else if (application.getStatus().equals("AP")
					|| application.getStatus().equals("AM")) {
				updateAppDtl.setString(22, "AM");

			} else if (application.getStatus().equals("HO")) {

				updateAppDtl.setString(22, "HO");
			} else if (application.getStatus().equals("EH")) {

				updateAppDtl.setString(22, "EH");
			} else if (application.getStatus().equals("EN")) {

				updateAppDtl.setString(22, "EN");
			}

			else {

				updateAppDtl.setString(22, "MO");
			}
			updateAppDtl.setString(23, application.getSubSchemeName());
			updateAppDtl.setString(24, application.getHandiCrafts());
			updateAppDtl.setString(25, application.getDcHandicrafts());
			updateAppDtl.setString(26, application.getIcardNo());

			if (!(application.getIcardIssueDate() == null)) {
				updateAppDtl.setDate(27, new java.sql.Date(application
						.getIcardIssueDate().getTime()));
			} else

			{
				updateAppDtl.setDate(27, null);
			}
			updateAppDtl.setString(28, application.getDcHandlooms());
			updateAppDtl.setString(29, application.getWeaverCreditScheme());
			updateAppDtl.setString(30, application.getHandloomchk());
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "status:"
					+ application.getStatus());

			updateAppDtl.executeQuery();
			int functionReturnValue = updateAppDtl.getInt(1);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateAppDtl.getString(31);

				updateAppDtl.close();
				updateAppDtl = null;
				connection.rollback();

				throw new DatabaseException(error);
			}
			updateAppDtl.close();
			updateAppDtl = null;

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateApplication",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateApplication", "Exited");
	}

	/**
	 * This method updates the SSI Details into the database
	 */

	public void updateSsiDtl(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl", "Entered");

		int ssiRefNo = ((apps.getBorrowerDetails()).getSsiDetails())
				.getBorrowerRefNo();

		// String
		// coveredValue=(apps.getBorrowerDetails()).getPreviouslyCovered();

		try {
			String cgbid = ((apps.getBorrowerDetails()).getSsiDetails())
					.getCgbid();
			//// System.out.println("cgbid:"+cgbid);
			String statusofApp = apps.getStatus();
			//// System.out.println("statusofApp: "+statusofApp);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl",
					"CGBID from borrowerDetails :" + cgbid);

			String cgpan = apps.getCgpan();
			//// System.out.println("ApplicationDAO-updateSsiDtl from borrowerDetails CGPAN:"+cgpan);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl",
					"CGPAN from borrowerDetails :" + cgpan);

			if ((cgpan != null) && !(cgpan.equals(""))) {

				Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl",
						"Entering the method if cgbid is not null");

				CallableStatement ssiRefNoForCgbid = connection
						.prepareCall("{?=call funcGetSSIRefNoforBID(?,?,?)}");
				ssiRefNoForCgbid.registerOutParameter(1, Types.INTEGER);
				ssiRefNoForCgbid.registerOutParameter(2, Types.INTEGER); // Get
																			// SSI
																			// RefNo
				ssiRefNoForCgbid.registerOutParameter(4, Types.VARCHAR);

				ssiRefNoForCgbid.setString(3, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCgbid());

				ssiRefNoForCgbid.executeQuery();
				int ssiRefNoForCgbidValue = ssiRefNoForCgbid.getInt(1);

				if (ssiRefNoForCgbidValue == Constants.FUNCTION_FAILURE) {

					String error = ssiRefNoForCgbid.getString(4);

					ssiRefNoForCgbid.close();
					ssiRefNoForCgbid = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					((apps.getBorrowerDetails()).getSsiDetails())
							.setBorrowerRefNo(ssiRefNoForCgbid.getInt(2));

					ssiRefNoForCgbid.close();
					ssiRefNoForCgbid = null;

				}

			} else if ((cgbid != null) && (!(cgbid.equals("")))
					&& (!(statusofApp.equals("RE")))) {

				Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl",
						"Entering teh method if cgpan is not null");

				CallableStatement ssiRefNoForCgpan = connection
						.prepareCall("{?=call funcGetSSIRefNoforCGPAN(?,?,?)}");
				ssiRefNoForCgpan.registerOutParameter(1, Types.INTEGER);
				ssiRefNoForCgpan.registerOutParameter(2, Types.INTEGER); // Get
																			// SSI
																			// RefNo
				ssiRefNoForCgpan.registerOutParameter(4, Types.VARCHAR);

				ssiRefNoForCgpan.setString(3, apps.getCgpan());

				ssiRefNoForCgpan.executeQuery();
				int ssiRefNoForCgpanValue = ssiRefNoForCgpan.getInt(1);

				if (ssiRefNoForCgpanValue == Constants.FUNCTION_FAILURE) {

					String error = ssiRefNoForCgpan.getString(4);

					ssiRefNoForCgpan.close();
					ssiRefNoForCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {
					((apps.getBorrowerDetails()).getSsiDetails())
							.setBorrowerRefNo(ssiRefNoForCgpan.getInt(2));

					ssiRefNoForCgpan.close();
					ssiRefNoForCgpan = null;

				}
			}

			Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl",
					"Entering teh method if both are is not null");

			CallableStatement updateSsiDtl = connection
					.prepareCall("{?=call funcUpdateSSIDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			updateSsiDtl.registerOutParameter(1, Types.INTEGER);
			updateSsiDtl.registerOutParameter(29, Types.VARCHAR);

			updateSsiDtl.setInt(2, ssiRefNo);
			//// System.out.println("SSi Ref No:"+ssiRefNo);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl", "SSI RefNo :"
					+ ssiRefNo);

			updateSsiDtl.setString(4, "N");

			String assistedValue = (apps.getBorrowerDetails())
					.getAssistedByBank();
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl",
					"bank Assistance :" + assistedValue);
			//// System.out.println("bank Assistance :" + assistedValue);
			updateSsiDtl.setString(3,
					(apps.getBorrowerDetails()).getPreviouslyCovered());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"covered value :"
							+ (apps.getBorrowerDetails())
									.getPreviouslyCovered());
			//// System.out.println("covered value :" +
			// (apps.getBorrowerDetails()).getPreviouslyCovered());
			updateSsiDtl.setNull(5, java.sql.Types.VARCHAR);

			String npaValue = (apps.getBorrowerDetails()).getNpa();
			//// System.out.println("NPA value:"+npaValue);
			updateSsiDtl.setString(6, npaValue);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl",
					"NPA" + (apps.getBorrowerDetails()).getNpa());

			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getConstitution()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getConstitution()).equals(""))) {
				updateSsiDtl.setString(7, ((apps.getBorrowerDetails())
						.getSsiDetails()).getConstitution());

			} else {
				updateSsiDtl.setString(7, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"const :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getConstitution());
			//// System.out.println("const :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getConstitution());
			updateSsiDtl.setString(8,
					((apps.getBorrowerDetails()).getSsiDetails()).getSsiType());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"ssi type :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getSsiType());
			//// System.out.println("ssi type :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getSsiType());
			updateSsiDtl.setString(9,
					((apps.getBorrowerDetails()).getSsiDetails()).getSsiName());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"ssi name : "
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getSsiName());
			//// System.out.println("ssi name : " +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getSsiName());
			updateSsiDtl.setString(10,
					((apps.getBorrowerDetails()).getSsiDetails()).getRegNo());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl", "reg no :"
					+ ((apps.getBorrowerDetails()).getSsiDetails()).getRegNo());
			//// System.out.println("reg no :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getRegNo());
			updateSsiDtl.setNull(11, java.sql.Types.DATE);

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getSsiITPan()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getSsiITPan()).equals(""))) {
				updateSsiDtl.setString(12, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSsiITPan());

			} else {
				updateSsiDtl.setString(12, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"ITPAN :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getSsiITPan());
			//// System.out.println("ITPAN :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getSsiITPan());
			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getActivityType()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getActivityType()).equals(""))) {
				updateSsiDtl.setString(13, ((apps.getBorrowerDetails())
						.getSsiDetails()).getActivityType());

			} else {
				updateSsiDtl.setString(13, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Activity Type :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getActivityType());
			//// System.out.println("Activity Type :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getActivityType());
			if ((((apps.getBorrowerDetails()).getSsiDetails()).getEmployeeNos()) == 0) {
				updateSsiDtl.setNull(14, java.sql.Types.INTEGER);
			} else {
				updateSsiDtl.setInt(14, ((apps.getBorrowerDetails())
						.getSsiDetails()).getEmployeeNos());
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"No of employees :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getEmployeeNos());
			//// System.out.println("No of employees :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getEmployeeNos());
			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getProjectedSalesTurnover()) == 0) {
				updateSsiDtl.setNull(15, java.sql.Types.DOUBLE);
			} else {
				updateSsiDtl.setDouble(15, ((apps.getBorrowerDetails())
						.getSsiDetails()).getProjectedSalesTurnover());
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Turnover :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getProjectedSalesTurnover());
			//// System.out.println("Turnover :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getProjectedSalesTurnover());
			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getProjectedExports()) == 0) {
				updateSsiDtl.setNull(16, java.sql.Types.DOUBLE);
			} else {
				updateSsiDtl.setDouble(16, ((apps.getBorrowerDetails())
						.getSsiDetails()).getProjectedExports());
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Exports :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getProjectedExports());
			//// System.out.println("Exports :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getProjectedExports());
			updateSsiDtl.setString(17,
					((apps.getBorrowerDetails()).getSsiDetails()).getAddress());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Address :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getAddress());
			//// System.out.println("Address :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getAddress());
			if ((((apps.getBorrowerDetails()).getSsiDetails()).getCity()) != null
					&& !(((apps.getBorrowerDetails()).getSsiDetails())
							.getCity().equals(""))) {
				updateSsiDtl
						.setString(18, ((apps.getBorrowerDetails())
								.getSsiDetails()).getCity());

			} else {
				updateSsiDtl.setString(18, null);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl", "City :"
					+ ((apps.getBorrowerDetails()).getSsiDetails()).getCity());
			//// System.out.println("City :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getCity());
			updateSsiDtl.setString(19,
					((apps.getBorrowerDetails()).getSsiDetails()).getPincode());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Pincode :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getPincode());
			//// System.out.println("Pincode :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getPincode());
			updateSsiDtl.setString(20, "Y"); // display defaulter
			updateSsiDtl
					.setString(21,
							((apps.getBorrowerDetails()).getSsiDetails())
									.getDistrict());
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"District :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getDistrict());
			//// System.out.println("District :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getDistrict());
			updateSsiDtl.setString(22,
					((apps.getBorrowerDetails()).getSsiDetails()).getState());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl", "State :"
					+ ((apps.getBorrowerDetails()).getSsiDetails()).getState());
			//// System.out.println("State :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getState());
			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getIndustryNature()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getIndustryNature()).equals(""))) {
				updateSsiDtl.setString(23, ((apps.getBorrowerDetails())
						.getSsiDetails()).getIndustryNature());

			} else {
				updateSsiDtl.setString(23, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Industry Name :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getIndustryNature());
			//// System.out.println("Industry Name :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getIndustryNature());
			updateSsiDtl.setString(24, "A");

			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getIndustrySector()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getIndustrySector()).equals(""))) {
				updateSsiDtl.setString(25, ((apps.getBorrowerDetails())
						.getSsiDetails()).getIndustrySector());

			} else {
				updateSsiDtl.setString(25, null);
			}
			Log.log(Log.DEBUG,
					"ApplicationDAO",
					"updateSsiDtl",
					"Industry Sector :"
							+ ((apps.getBorrowerDetails()).getSsiDetails())
									.getIndustrySector());
			//// System.out.println("Industry Sector :" +
			// ((apps.getBorrowerDetails()).getSsiDetails()).getIndustrySector());
			if (((apps.getBorrowerDetails()).getOsAmt()) == 0) {
				updateSsiDtl.setNull(26, java.sql.Types.DOUBLE);
			} else {
				updateSsiDtl.setDouble(26,
						(apps.getBorrowerDetails()).getOsAmt());
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl", "Os Amt :"
					+ (apps.getBorrowerDetails()).getOsAmt());
			//// System.out.println("Os Amt :" +
			// (apps.getBorrowerDetails()).getOsAmt());
			MCGFDetails mcgfDetails = apps.getMCGFDetails();
			if (mcgfDetails != null) {
				updateSsiDtl.setString(27, "Y");
			} else {

				updateSsiDtl.setString(27, "N");
			}

			updateSsiDtl.setString(28, createdBy);
			updateSsiDtl.executeQuery();
			int updateSsiDtlValue = updateSsiDtl.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSsiDtl",
					"Updating SSI Detail value" + updateSsiDtlValue);
			//// System.out.println("Updating SSI Detail value" +
			// updateSsiDtlValue);
			if (updateSsiDtlValue == Constants.FUNCTION_FAILURE) {

				String error = updateSsiDtl.getString(29);

				updateSsiDtl.close();
				updateSsiDtl = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateSsiDtl",
						"Update SSi Detail exception :" + error);

				throw new DatabaseException(error);
			}

			updateSsiDtl.close();
			updateSsiDtl = null;

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl",
					sqlException.getMessage());
			//// System.out.println("updateSsiDtl"+sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateSsiDtl", "Exited");
	}

	/*
	 * This method updates promoters / guarantors and primary Security details
	 */

	public void updateOtherDtls(Application apps, String userId,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateOtherDtls", "Entered");

		// SP for promoters Details
		try {

			Log.log(Log.INFO, "ApplicationDAO", "updateOtherDtls",
					"Entering Updating Other Details method");

			CallableStatement updatePromoterDtls = connection
					.prepareCall("{?=call funcUpdatePromoterDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			updatePromoterDtls.registerOutParameter(1, Types.INTEGER);
			updatePromoterDtls.registerOutParameter(23, Types.VARCHAR);

			updatePromoterDtls.setInt(2, ((apps.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo()); // SSI ref no
			updatePromoterDtls.setString(3,
					((apps.getBorrowerDetails()).getSsiDetails()).getCpTitle()); // title
			updatePromoterDtls.setString(4, ((apps.getBorrowerDetails())
					.getSsiDetails()).getCpFirstName()); // first name

			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getCpMiddleName()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getCpMiddleName()).equals(""))) {
				updatePromoterDtls.setString(5, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpMiddleName()); // middle name

			} else {
				updatePromoterDtls.setString(5, null);
			}

			updatePromoterDtls.setString(6, ((apps.getBorrowerDetails())
					.getSsiDetails()).getCpLastName()); // last name

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getCpITPAN()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getCpITPAN()).equals(""))) {
				updatePromoterDtls.setString(7, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpITPAN()); // itpan

			} else {
				updatePromoterDtls.setString(7, null);
			}

			updatePromoterDtls
					.setString(8, ((apps.getBorrowerDetails()).getSsiDetails())
							.getCpGender()); // gender

			if (!((((apps.getBorrowerDetails()).getSsiDetails()).getCpDOB())
					.toString().equals(""))
					&& (((apps.getBorrowerDetails()).getSsiDetails())
							.getCpDOB()) != null) {
				updatePromoterDtls.setDate(
						9,
						new java.sql.Date((((apps.getBorrowerDetails())
								.getSsiDetails()).getCpDOB()).getTime())); // dob
			} else {
				updatePromoterDtls.setDate(9, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getCpLegalID()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getCpLegalID()).equals(""))) {
				updatePromoterDtls.setString(10, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpLegalID()); // legal ID

			} else {
				updatePromoterDtls.setString(10, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getCpLegalIdValue()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getCpLegalIdValue()).equals(""))) {
				updatePromoterDtls.setString(11, ((apps.getBorrowerDetails())
						.getSsiDetails()).getCpLegalIdValue()); // legal Value

			} else {
				updatePromoterDtls.setString(11, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getFirstName()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getFirstName()).equals(""))) {
				updatePromoterDtls.setString(12, ((apps.getBorrowerDetails())
						.getSsiDetails()).getFirstName()); // first Name

			} else {
				updatePromoterDtls.setString(12, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getFirstDOB()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getFirstDOB()).toString().equals(""))) {
				updatePromoterDtls.setDate(
						13,
						new java.sql.Date((((apps.getBorrowerDetails())
								.getSsiDetails()).getFirstDOB()).getTime()));
			} else {
				updatePromoterDtls.setDate(13, null);

			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getFirstItpan()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getFirstItpan()).equals(""))) {
				updatePromoterDtls.setString(14, ((apps.getBorrowerDetails())
						.getSsiDetails()).getFirstItpan()); // first itpan

			} else {
				updatePromoterDtls.setString(14, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getSecondName()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getSecondName()).equals(""))) {
				updatePromoterDtls.setString(15, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSecondName()); // second Name

			} else {
				updatePromoterDtls.setString(15, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getSecondDOB()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getSecondDOB()).toString().equals(""))) {
				updatePromoterDtls.setDate(
						16,
						new java.sql.Date((((apps.getBorrowerDetails())
								.getSsiDetails()).getSecondDOB()).getTime())); // second
																				// DOB
			} else {
				updatePromoterDtls.setDate(16, null);

			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getSecondItpan()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getSecondItpan()).equals(""))) {
				updatePromoterDtls.setString(17, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSecondItpan()); // second itpan

			} else {
				updatePromoterDtls.setString(17, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getThirdName()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getThirdName()).equals(""))) {
				updatePromoterDtls.setString(18, ((apps.getBorrowerDetails())
						.getSsiDetails()).getThirdName()); // third Name

			} else {
				updatePromoterDtls.setString(18, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getThirdDOB()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getThirdDOB()).toString().equals(""))) {
				updatePromoterDtls.setDate(
						19,
						new java.sql.Date((((apps.getBorrowerDetails())
								.getSsiDetails()).getThirdDOB()).getTime()));
			} else {
				updatePromoterDtls.setDate(19, null);

				// third DOB
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails()).getThirdItpan()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getThirdItpan()).equals(""))) {
				updatePromoterDtls.setString(20, ((apps.getBorrowerDetails())
						.getSsiDetails()).getThirdItpan()); // third itpan

			} else {
				updatePromoterDtls.setString(20, null);
			}

			if ((((apps.getBorrowerDetails()).getSsiDetails())
					.getSocialCategory()) != null
					&& !((((apps.getBorrowerDetails()).getSsiDetails())
							.getSocialCategory()).equals(""))) {
				updatePromoterDtls.setString(21, ((apps.getBorrowerDetails())
						.getSsiDetails()).getSocialCategory()); // social
																// category

			} else {
				updatePromoterDtls.setString(21, null);
			}

			updatePromoterDtls.setString(22, userId);

			updatePromoterDtls.executeQuery();
			int updatePromoterDtlsValue = updatePromoterDtls.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
					"promoters Details result" + updatePromoterDtlsValue);

			if (updatePromoterDtlsValue == Constants.FUNCTION_FAILURE) {

				String error = updatePromoterDtls.getString(23);

				updatePromoterDtls.close();
				updatePromoterDtls = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updatePromoterDtls.close();
			updatePromoterDtls = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateOtherDtls",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	/*
	 * This method updates the guarantors Details into the DB
	 */

	public void updateGuarantorDtls(Application apps, String userId,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateGuarantorDtls", "Entered");

		try {

			// SP for guarantors details

			if (((apps.getProjectOutlayDetails()).getGuarantorsName1()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName1())
							.equals(""))) {
				CallableStatement updateGuarantorDtl1 = connection
						.prepareCall("{?=call funcUpdatePersonalGuar(?,?,?,?,?)}");
				updateGuarantorDtl1.registerOutParameter(1, Types.INTEGER);
				updateGuarantorDtl1.registerOutParameter(6, Types.VARCHAR);

				updateGuarantorDtl1.setString(2, apps.getAppRefNo()); // app ref
																		// no

				updateGuarantorDtl1.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName1());

				updateGuarantorDtl1.setDouble(4, (apps
						.getProjectOutlayDetails()).getGuarantorsNetWorth1());

				updateGuarantorDtl1.setString(5, userId);

				updateGuarantorDtl1.executeQuery();
				int updateGuarantorDtl1Value = updateGuarantorDtl1.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Guarantors Details1 result :"
								+ updateGuarantorDtl1Value);

				if (updateGuarantorDtl1Value == Constants.FUNCTION_FAILURE) {

					String error = updateGuarantorDtl1.getString(6);

					updateGuarantorDtl1.close();
					updateGuarantorDtl1 = null;

					connection.rollback();

					throw new DatabaseException(error);
				}

				updateGuarantorDtl1.close();
				updateGuarantorDtl1 = null;

			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName2()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName2())
							.equals(""))) {

				CallableStatement updateGuarantorDtl2 = connection
						.prepareCall("{?=call funcUpdatePersonalGuar(?,?,?,?,?)}");
				updateGuarantorDtl2.registerOutParameter(1, Types.INTEGER);
				updateGuarantorDtl2.registerOutParameter(6, Types.VARCHAR);

				updateGuarantorDtl2.setString(2, apps.getAppRefNo()); // app ref
																		// no

				updateGuarantorDtl2.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName2());

				updateGuarantorDtl2.setDouble(4, (apps
						.getProjectOutlayDetails()).getGuarantorsNetWorth2());

				updateGuarantorDtl2.setString(5, userId);

				updateGuarantorDtl2.executeQuery();
				int updateGuarantorDtl2Value = updateGuarantorDtl2.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Guarantors Details result :"
								+ updateGuarantorDtl2Value);

				if (updateGuarantorDtl2Value == Constants.FUNCTION_FAILURE) {

					String error = updateGuarantorDtl2.getString(6);

					updateGuarantorDtl2.close();
					updateGuarantorDtl2 = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateGuarantorDtl2.close();
				updateGuarantorDtl2 = null;

			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName3()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName3())
							.equals(""))) {

				CallableStatement updateGuarantorDtl3 = connection
						.prepareCall("{?=call funcUpdatePersonalGuar(?,?,?,?,?)}");
				updateGuarantorDtl3.registerOutParameter(1, Types.INTEGER);
				updateGuarantorDtl3.registerOutParameter(6, Types.VARCHAR);

				updateGuarantorDtl3.setString(2, apps.getAppRefNo()); // app ref
																		// no

				updateGuarantorDtl3.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName3());

				updateGuarantorDtl3.setDouble(4, (apps
						.getProjectOutlayDetails()).getGuarantorsNetWorth3());

				updateGuarantorDtl3.setString(5, userId);

				updateGuarantorDtl3.executeQuery();
				int updateGuarantorDtl3Value = updateGuarantorDtl3.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Guarantors Details3 result :"
								+ updateGuarantorDtl3Value);

				if (updateGuarantorDtl3Value == Constants.FUNCTION_FAILURE) {

					String error = updateGuarantorDtl3.getString(6);

					updateGuarantorDtl3.close();
					updateGuarantorDtl3 = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateGuarantorDtl3.close();
				updateGuarantorDtl3 = null;

			}

			if (((apps.getProjectOutlayDetails()).getGuarantorsName4()) != null
					&& !(((apps.getProjectOutlayDetails()).getGuarantorsName4())
							.equals(""))) {

				CallableStatement updateGuarantorDtl4 = connection
						.prepareCall("{?=call funcUpdatePersonalGuar(?,?,?,?,?)}");
				updateGuarantorDtl4.registerOutParameter(1, Types.INTEGER);
				updateGuarantorDtl4.registerOutParameter(6, Types.VARCHAR);

				updateGuarantorDtl4.setString(2, apps.getAppRefNo()); // app ref
																		// no

				updateGuarantorDtl4.setString(3,
						(apps.getProjectOutlayDetails()).getGuarantorsName4());

				updateGuarantorDtl4.setDouble(4, (apps
						.getProjectOutlayDetails()).getGuarantorsNetWorth4());

				updateGuarantorDtl4.setString(5, userId);

				updateGuarantorDtl4.executeQuery();
				int updateGuarantorDtl4Value = updateGuarantorDtl4.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Guarantors Details4 result :"
								+ updateGuarantorDtl4Value);

				if (updateGuarantorDtl4Value == Constants.FUNCTION_FAILURE) {

					String error = updateGuarantorDtl4.getString(6);

					updateGuarantorDtl4.close();
					updateGuarantorDtl4 = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateGuarantorDtl4.close();
				updateGuarantorDtl4 = null;

			}

			// SP for primary Security Details-Land

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getLandParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails()).getLandParticulars())
							.equals(""))) {

				CallableStatement updatePsLandDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsLandDetails.registerOutParameter(1, Types.INTEGER);
				updatePsLandDetails.registerOutParameter(7, Types.VARCHAR);

				updatePsLandDetails.setString(2, apps.getAppRefNo()); // app ref
																		// no
				updatePsLandDetails.setString(3, "Land"); // PS Type

				updatePsLandDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getLandParticulars())); // particulars

				updatePsLandDetails.setDouble(5,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails()).getLandValue()));

				updatePsLandDetails.setString(6, userId);

				updatePsLandDetails.executeQuery();
				int updatePsLandDetailsValue = updatePsLandDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Land Details1 result :" + updatePsLandDetailsValue);

				if (updatePsLandDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsLandDetails.getString(6);

					updatePsLandDetails.close();
					updatePsLandDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsLandDetails.close();
				updatePsLandDetails = null;

			}

			// SP for primary Security Details-Building

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getBldgParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails()).getBldgParticulars())
							.equals(""))) {

				CallableStatement updatePsBldgDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsBldgDetails.registerOutParameter(1, Types.INTEGER);
				updatePsBldgDetails.registerOutParameter(7, Types.VARCHAR);

				updatePsBldgDetails.setString(2, apps.getAppRefNo()); // app ref
																		// no
				updatePsBldgDetails.setString(3, "Building"); // PS Type

				updatePsBldgDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getBldgParticulars())); // particulars

				updatePsBldgDetails.setDouble(5,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails()).getBldgValue()));

				updatePsBldgDetails.setString(6, userId);

				updatePsBldgDetails.executeQuery();
				int updatePsBldgDetailsValue = updatePsBldgDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Building Details1 result :" + updatePsBldgDetailsValue);

				if (updatePsBldgDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsBldgDetails.getString(7);

					updatePsBldgDetails.close();
					updatePsBldgDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsBldgDetails.close();
				updatePsBldgDetails = null;

			}

			// SP for primary Security Details-Machine

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getMachineParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getMachineParticulars()).equals(""))) {

				CallableStatement updatePsMachineDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsMachineDetails.registerOutParameter(1, Types.INTEGER);
				updatePsMachineDetails.registerOutParameter(7, Types.VARCHAR);

				updatePsMachineDetails.setString(2, apps.getAppRefNo()); // app
																			// ref
																			// no
				updatePsMachineDetails.setString(3, "Machinery"); // PS Type

				updatePsMachineDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getMachineParticulars())); // particulars

				updatePsMachineDetails
						.setDouble(5,
								(((apps.getProjectOutlayDetails())
										.getPrimarySecurityDetails())
										.getMachineValue()));

				updatePsMachineDetails.setString(6, userId);

				updatePsMachineDetails.executeQuery();
				int updatePsMachineDetailsValue = updatePsMachineDetails
						.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Machine Details result :"
								+ updatePsMachineDetailsValue);

				if (updatePsMachineDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsMachineDetails.getString(7);

					updatePsMachineDetails.close();
					updatePsMachineDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsMachineDetails.close();
				updatePsMachineDetails = null;

			}

			// Sp fpr primary Security Details-fixed/movable Assets

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getAssetsParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getAssetsParticulars()).equals(""))) {

				CallableStatement updatePsAssetsDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsAssetsDetails.registerOutParameter(1, Types.INTEGER);
				updatePsAssetsDetails.registerOutParameter(7, Types.VARCHAR);

				updatePsAssetsDetails.setString(2, apps.getAppRefNo()); // app
																		// ref
																		// no
				updatePsAssetsDetails.setString(3, "Fixed Assets"); // PS Type

				updatePsAssetsDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getAssetsParticulars())); // particulars

				updatePsAssetsDetails
						.setDouble(5, (((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails()).getAssetsValue()));

				updatePsAssetsDetails.setString(6, userId);

				updatePsAssetsDetails.executeQuery();
				int updatePsAssetsDetailsValue = updatePsAssetsDetails
						.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Assets Details result :" + updatePsAssetsDetailsValue);

				if (updatePsAssetsDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsAssetsDetails.getString(7);

					updatePsAssetsDetails.close();
					updatePsAssetsDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsAssetsDetails.close();
				updatePsAssetsDetails = null;

			}

			// SP for Primary Security Details-Current Assets

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getCurrentAssetsParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getCurrentAssetsParticulars()).equals(""))) {

				CallableStatement updatePsCurrentAssetsDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsCurrentAssetsDetails.registerOutParameter(1,
						Types.INTEGER);
				updatePsCurrentAssetsDetails.registerOutParameter(7,
						Types.VARCHAR);

				updatePsCurrentAssetsDetails.setString(2, apps.getAppRefNo()); // app
																				// ref
																				// no
				updatePsCurrentAssetsDetails.setString(3, "Current Assets"); // PS
																				// Type

				updatePsCurrentAssetsDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getCurrentAssetsParticulars())); // particulars

				updatePsCurrentAssetsDetails.setDouble(5,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getCurrentAssetsValue()));

				updatePsCurrentAssetsDetails.setString(6, userId);

				updatePsCurrentAssetsDetails.executeQuery();
				int updatePsCurrentAssetsDetailsValue = updatePsCurrentAssetsDetails
						.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Current Assets Details result :"
								+ updatePsCurrentAssetsDetailsValue);

				if (updatePsCurrentAssetsDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsCurrentAssetsDetails.getString(7);

					updatePsCurrentAssetsDetails.close();
					updatePsCurrentAssetsDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsCurrentAssetsDetails.close();
				updatePsCurrentAssetsDetails = null;

			}

			// SP for Primary Security Details-Others

			if (((((apps.getProjectOutlayDetails()).getPrimarySecurityDetails())
					.getOthersParticulars())) != null
					&& !((((apps.getProjectOutlayDetails())
							.getPrimarySecurityDetails())
							.getOthersParticulars()).equals(""))) {

				CallableStatement updatePsOthersDetails = connection
						.prepareCall("{?=call funcUpdatePrimarySecurity(?,?,?,?,?,?)}");
				updatePsOthersDetails.registerOutParameter(1, Types.INTEGER);
				updatePsOthersDetails.registerOutParameter(7, Types.VARCHAR);

				updatePsOthersDetails.setString(2, apps.getAppRefNo()); // app
																		// ref
																		// no
				updatePsOthersDetails.setString(3, "Others"); // PS Type

				updatePsOthersDetails.setString(4,
						(((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails())
								.getOthersParticulars())); // particulars

				updatePsOthersDetails
						.setDouble(5, (((apps.getProjectOutlayDetails())
								.getPrimarySecurityDetails()).getOthersValue()));

				updatePsOthersDetails.setString(6, userId);

				updatePsOthersDetails.executeQuery();
				int updatePsOthersDetailsValue = updatePsOthersDetails
						.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateOtherDtls",
						"Others Details result :" + updatePsOthersDetailsValue);

				if (updatePsOthersDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updatePsOthersDetails.getString(7);

					updatePsOthersDetails.close();
					updatePsOthersDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updatePsOthersDetails.close();
				updatePsOthersDetails = null;

			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateOtherDtls",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	/*
	 * This method is used to update the term loan details
	 */
	public void updateTermLoanDtl(Application apps, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateTermLoanDtl", "Entered");

		String appLoanType = apps.getLoanType();

		try {

			if ((appLoanType.equals("TC")) || (appLoanType.equals("CC"))
					|| (appLoanType.equals("BO"))) {
				CallableStatement updateTcDetails = connection
						.prepareCall("{?=call funcUpdateTermLoan(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				updateTcDetails.registerOutParameter(1, Types.INTEGER);
				updateTcDetails.registerOutParameter(21, Types.VARCHAR);

				updateTcDetails.setString(2, apps.getAppRefNo()); // app ref no

				updateTcDetails.setDouble(3, (apps.getProjectOutlayDetails())
						.getTermCreditSanctioned());

				if (!(((apps.getTermLoan()).getAmountSanctionedDate())
						.toString().equals(""))
						&& ((apps.getTermLoan()).getAmountSanctionedDate()) != null) {
					updateTcDetails.setDate(
							4,
							new java.sql.Date(((apps.getTermLoan())
									.getAmountSanctionedDate()).getTime()));
				} else {
					updateTcDetails.setDate(4, null);

				}

				if (((apps.getProjectOutlayDetails())
						.getTcPromoterContribution()) == 0) {
					updateTcDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getTcPromoterContribution()); // promoter Cont
				}

				if (((apps.getProjectOutlayDetails()).getTcSubsidyOrEquity()) == 0) {
					updateTcDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getTcSubsidyOrEquity()); // subsidy
																					// or
																					// equity
				}

				if (((apps.getProjectOutlayDetails()).getTcOthers()) == 0) {
					updateTcDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getTcOthers()); // Others
				}

				if (((apps.getTermLoan()).getCreditGuaranteed()) == 0) {
					updateTcDetails.setNull(8, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(8,
							(apps.getTermLoan()).getCreditGuaranteed()); // credit
																			// to
																			// be
																			// guraranteed
				}

				if (((apps.getTermLoan()).getTenure()) == 0) {
					updateTcDetails.setNull(9, java.sql.Types.INTEGER);
				} else {
					updateTcDetails.setInt(9, (apps.getTermLoan()).getTenure()); // Tenure
				}

				if (((apps.getTermLoan()).getInterestType()) != null
						&& !(((apps.getTermLoan()).getInterestType())
								.equals(""))) {
					updateTcDetails.setString(10,
							(apps.getTermLoan()).getInterestType()); // Interest
																		// Type-Fixed

				} else {
					updateTcDetails.setString(10, null);
				}

				if (((apps.getTermLoan()).getInterestRate()) == 0) {
					updateTcDetails.setNull(11, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(11,
							(apps.getTermLoan()).getInterestRate()); // Interest
																		// Rate
				}

				if (((apps.getTermLoan()).getBenchMarkPLR()) == 0) {
					updateTcDetails.setNull(12, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails.setDouble(12,
							(apps.getTermLoan()).getBenchMarkPLR()); // Bench
																		// Mark
																		// PLR
				}

				if (((apps.getTermLoan()).getPlr()) == 0) {
					updateTcDetails.setNull(13, java.sql.Types.DOUBLE);
				} else {
					updateTcDetails
							.setDouble(13, (apps.getTermLoan()).getPlr()); // PLR
				}

				if (((apps.getTermLoan()).getRepaymentMoratorium()) == 0) {
					updateTcDetails.setNull(14, java.sql.Types.INTEGER);
				} else {
					updateTcDetails.setInt(14,
							(apps.getTermLoan()).getRepaymentMoratorium());// Moratorium
				}

				if (((apps.getTermLoan()).getFirstInstallmentDueDate()) != null
						&& !(((apps.getTermLoan()).getFirstInstallmentDueDate())
								.toString().equals(""))) {
					updateTcDetails.setDate(
							15,
							new java.sql.Date(((apps.getTermLoan())
									.getFirstInstallmentDueDate()).getTime()));// First
																				// Install
																				// Date
				} else {
					updateTcDetails.setNull(15, java.sql.Types.DATE);

				}

				if (((apps.getTermLoan()).getNoOfInstallments()) == 0) {
					updateTcDetails.setNull(16, java.sql.Types.INTEGER);
				} else {
					updateTcDetails.setInt(16,
							(apps.getTermLoan()).getNoOfInstallments());// No of
																		// Installments
				}

				if (((apps.getTermLoan()).getPeriodicity()) != 1
						|| ((apps.getTermLoan()).getPeriodicity()) != 2
						|| ((apps.getTermLoan()).getPeriodicity()) != 3) {
					updateTcDetails.setNull(17, java.sql.Types.INTEGER);
				} else {
					updateTcDetails.setInt(17,
							(apps.getTermLoan()).getPeriodicity());// Periodicity
				}

				if ((apps.getTermLoan()).getTypeOfPLR() != null
						&& !(((apps.getTermLoan()).getTypeOfPLR()).equals(""))) {
					updateTcDetails.setString(18,
							(apps.getTermLoan()).getTypeOfPLR()); // TYpe of PLR

				} else {

					updateTcDetails.setNull(18, java.sql.Types.VARCHAR);
				}

				updateTcDetails.setString(19, createdBy);
				updateTcDetails.setString(20, null);

				updateTcDetails.executeQuery();
				int updateTcDetailsValue = updateTcDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateTermLoanDtl",
						"TC Details result :" + updateTcDetailsValue);

				if (updateTcDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updateTcDetails.getString(21);

					updateTcDetails.close();
					updateTcDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateTcDetails.close();
				updateTcDetails = null;

				/*
				 * if (((apps.getTermLoan()).getPplOS())!=0 ||
				 * ((apps.getTermLoan()).getPplOsAsOnDate())!=null ||
				 * !((apps.getTermLoan()).getPplOsAsOnDate()).equals("")) {
				 * 
				 * CallableStatement updateTCOutDtls=connection.prepareCall(
				 * "{?=call funcUpdateTCOutStand(?,?,?,?,?)}");
				 * 
				 * updateTCOutDtls.registerOutParameter(1,Types.INTEGER);
				 * updateTCOutDtls.registerOutParameter(6,Types.VARCHAR);
				 * 
				 * updateTCOutDtls.setString(2,apps.getAppRefNo());
				 * updateTCOutDtls.setDouble(3,(apps.getTermLoan()).getPplOS());
				 * updateTCOutDtls.setDate(4,new
				 * java.sql.Date(((apps.getTermLoan
				 * ()).getPplOsAsOnDate()).getTime()));//final Date
				 * updateTCOutDtls.setString(5,createdBy);
				 * 
				 * updateTCOutDtls.executeQuery(); int
				 * updateTCOutDtlsValue=updateTCOutDtls.getInt(1);
				 * Log.log(Log.DEBUG,"ApplicationDAO","updateTermLoanDtl",
				 * "Update TCWC Details result :" + updateTCOutDtlsValue);
				 * 
				 * if(updateTCOutDtlsValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = updateTCOutDtls.getString(6);
				 * 
				 * updateTCOutDtls.close(); updateTCOutDtls=null;
				 * 
				 * connection.rollback();
				 * 
				 * throw new DatabaseException(error); } }
				 */} else if (appLoanType.equals("WC")) {
				double tcPromoterCont = (apps.getProjectOutlayDetails())
						.getTcPromoterContribution();
				double tcSubsidy = (apps.getProjectOutlayDetails())
						.getTcSubsidyOrEquity();
				double tcOthers = (apps.getProjectOutlayDetails())
						.getTcOthers();
				double tcAmtSanctioned = (apps.getProjectOutlayDetails())
						.getTermCreditSanctioned();

				// If any of the above fields are not null,then the SP is called
				// if((tcPromoterCont!=0)||(tcSubsidy!=0)||(tcOthers!=0)||(tcAmtSanctioned!=0))
				// {
				CallableStatement updateTCWCDetails = connection
						.prepareCall("{?=call funcUpdateTermLoan(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				updateTCWCDetails.registerOutParameter(1, Types.INTEGER);
				updateTCWCDetails.registerOutParameter(21, Types.VARCHAR);

				updateTCWCDetails.setString(2, apps.getAppRefNo()); // app ref
																	// no
				updateTCWCDetails.setDouble(3, (apps.getProjectOutlayDetails())
						.getTermCreditSanctioned());
				updateTCWCDetails.setNull(4, java.sql.Types.DATE); // Sanctioned
																	// Date

				if (((apps.getProjectOutlayDetails())
						.getTcPromoterContribution()) == 0) {
					updateTCWCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					updateTCWCDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getTcPromoterContribution()); // promoter Cont
				}

				if (((apps.getProjectOutlayDetails()).getTcSubsidyOrEquity()) == 0) {
					updateTCWCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					updateTCWCDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getTcSubsidyOrEquity()); // subsidy
																					// or
																					// equity
				}

				if (((apps.getProjectOutlayDetails()).getTcOthers()) == 0) {
					updateTCWCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					updateTCWCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getTcOthers()); // Others
				}

				updateTCWCDetails.setNull(8, java.sql.Types.DOUBLE); // credit
																		// to be
																		// guraranteed
				updateTCWCDetails.setNull(9, java.sql.Types.INTEGER); // Tenure
				updateTCWCDetails.setNull(10, java.sql.Types.VARCHAR); // Interest
																		// Type-Fixed
				updateTCWCDetails.setNull(11, java.sql.Types.DOUBLE); // Interest
																		// Rate
				updateTCWCDetails.setNull(12, java.sql.Types.DOUBLE); // Bench
																		// Mark
																		// PLR
				updateTCWCDetails.setNull(13, java.sql.Types.DOUBLE); // PLR
				updateTCWCDetails.setNull(14, java.sql.Types.INTEGER); // Moratorium
				updateTCWCDetails.setNull(15, java.sql.Types.DATE); // First
																	// Install
																	// Date
				updateTCWCDetails.setNull(16, java.sql.Types.INTEGER);// No of
																		// Installments
				updateTCWCDetails.setNull(17, java.sql.Types.INTEGER);// Periodicity
				updateTCWCDetails.setNull(18, java.sql.Types.VARCHAR);// PLR
																		// Type
				updateTCWCDetails.setString(19, createdBy); // User Id
				// updateTCWCDetails.setNull(21,java.sql.Types.DOUBLE); //DBR
				// Amount
				// updateTCWCDetails.setNull(22,java.sql.Types.DATE); //First
				// Date
				// updateTCWCDetails.setNull(23,java.sql.Types.DATE); //First
				// Date
				updateTCWCDetails.setNull(20, java.sql.Types.VARCHAR); // Remarks

				updateTCWCDetails.executeQuery();
				int updateTCWCDetailsValue = updateTCWCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateTermLoanDtl",
						"TCWC Details result :" + updateTCWCDetailsValue);

				if (updateTCWCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updateTCWCDetails.getString(21);

					updateTCWCDetails.close();
					updateTCWCDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateTCWCDetails.close();
				updateTCWCDetails = null;

				// }

			}
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateTermLoanDtl",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
		Log.log(Log.INFO, "ApplicationDAO", "updateTermLoanDtl", "Exited");

	}

	/*
	 * This method updates the working capital details into the database
	 */

	public void updateWCDtl(Application apps, String userId,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateWCDtl", "Entered");

		String appLoanType = apps.getLoanType();

		try {
			if ((appLoanType.equals("WC")) || (appLoanType.equals("CC"))
					|| (appLoanType.equals("BO"))) {

				// SP for Working Capital Loan
				CallableStatement updateWCDetails = connection
						.prepareCall("{?=call funcUpdatetWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				updateWCDetails.registerOutParameter(1, Types.INTEGER);
				updateWCDetails.registerOutParameter(19, Types.VARCHAR);

				updateWCDetails.setString(2, apps.getAppRefNo()); // app ref no
				updateWCDetails.setDouble(3, (apps.getProjectOutlayDetails())
						.getWcFundBasedSanctioned()); // fund based

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					updateWCDetails.setDouble(4, 0);
				} else {

					updateWCDetails.setDouble(4, (apps
							.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"updateWCDtl",
						"Non Fund BAsed Sanctioned :"
								+ (apps.getProjectOutlayDetails())
										.getWcNonFundBasedSanctioned());

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					updateWCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					updateWCDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"updateWCDtl",
						"promoter Cont :"
								+ (apps.getProjectOutlayDetails())
										.getWcPromoterContribution());

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					updateWCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					updateWCDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getWcSubsidyOrEquity()); // subsidy/equity
																					// support
				}
				Log.log(Log.INFO,
						"ApplicationDAO",
						"updateWCDtl",
						"Subsidy or equity :"
								+ (apps.getProjectOutlayDetails())
										.getWcSubsidyOrEquity());

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					updateWCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					updateWCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}
				Log.log(Log.INFO, "ApplicationDAO", "updateWCDtl", "Others:"
						+ (apps.getProjectOutlayDetails()).getWcOthers());

				updateWCDetails.setDouble(8,
						(apps.getWc()).getLimitFundBasedInterest()); // Interest

				if ((apps.getWc()).getLimitNonFundBasedCommission() == 0) {
					updateWCDetails.setDouble(9, 0);
				} else {

					updateWCDetails.setDouble(9,
							(apps.getWc()).getLimitNonFundBasedCommission()); // Commission
				}

				if (((apps.getWc()).getLimitFundBasedSanctionedDate()) != null
						&& !(((apps.getWc()).getLimitFundBasedSanctionedDate())
								.toString().equals(""))) {
					updateWCDetails.setDate(
							10,
							new java.sql.Date(((apps.getWc())
									.getLimitFundBasedSanctionedDate())
									.getTime())); // Fund BAsed Sanctioned Date
				} else {

					updateWCDetails.setDate(10, null);
				}
				if (((apps.getWc()).getLimitNonFundBasedSanctionedDate()) != null
						&& !(((apps.getWc())
								.getLimitNonFundBasedSanctionedDate())
								.toString().equals(""))) {
					updateWCDetails.setDate(
							11,
							new java.sql.Date(((apps.getWc())
									.getLimitNonFundBasedSanctionedDate())
									.getTime())); // Fund BAsed Sanctioned Date
				} else {

					updateWCDetails.setDate(11, null);
				}

				if (((apps.getWc()).getCreditFundBased()) == 0) {
					updateWCDetails.setNull(12, java.sql.Types.DOUBLE);
				} else {
					updateWCDetails.setDouble(12,
							(apps.getWc()).getCreditFundBased()); // Credit
																	// guranteed-Fund
																	// Based
				}

				if (((apps.getWc()).getCreditNonFundBased()) == 0) {

					updateWCDetails.setDouble(13, 0);
				} else {

					updateWCDetails.setDouble(13,
							(apps.getWc()).getCreditNonFundBased()); // Non Fund
																		// Based
				}

				if (((apps.getWc()).getWcPlr()) == 0) {
					updateWCDetails.setNull(14, java.sql.Types.DOUBLE);
				} else {
					updateWCDetails.setDouble(14, (apps.getWc()).getWcPlr()); // PLR
				}

				if ((apps.getWc()).getWcTypeOfPLR() != null
						&& !(((apps.getWc()).getWcTypeOfPLR()).equals(""))) {
					updateWCDetails.setString(15,
							(apps.getWc()).getWcTypeOfPLR()); // PLR Type
				} else {

					updateWCDetails.setNull(15, java.sql.Types.VARCHAR); // PLR
																			// Type
				}

				if ((apps.getWc()).getWcInterestType() != null
						&& !(((apps.getWc()).getWcInterestType()).equals(""))) {
					updateWCDetails.setString(16,
							(apps.getWc()).getWcInterestType()); // PLR Type

				} else {

					updateWCDetails.setNull(16, java.sql.Types.VARCHAR);
				}

				if ((apps.getRemarks()) == null
						|| (apps.getRemarks()).equals("")) {
					updateWCDetails.setNull(17, java.sql.Types.VARCHAR);

				} else {

					updateWCDetails.setString(17, null);
				}

				updateWCDetails.setString(18, userId);

				updateWCDetails.executeQuery();
				int updateWCDetailsValue = updateWCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateWCDtl",
						"WC Details result :" + updateWCDetailsValue);

				if (updateWCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updateWCDetails.getString(19);

					updateWCDetails.close();
					updateWCDetails = null;

					connection.rollback();

					throw new DatabaseException(error);
				}
				updateWCDetails.close();
				updateWCDetails = null;

				/*
				 * if (((apps.getWc()).getOsFundBasedPpl())!=0 ||
				 * ((apps.getWc()).getOsFundBasedInterestAmt())!=0 ||
				 * ((apps.getWc()).getOsFundBasedAsOnDate())!=null ||
				 * !(((apps.getWc()).getOsFundBasedAsOnDate()).equals("")) ||
				 * ((apps.getWc()).getOsNonFundBasedPpl())!=0 ||
				 * ((apps.getWc()).getOsNonFundBasedCommissionAmt())!=0 ||
				 * ((apps.getWc()).getOsNonFundBasedAsOnDate())==null ||
				 * !((apps.getWc()).getOsNonFundBasedAsOnDate()).equals("")) {
				 * 
				 * CallableStatement updateWcOutDetails=connection.prepareCall(
				 * "{?=call funcUpdateWCOutStand(?,?,?,?,?,?,?,?,?)}");
				 * updateWcOutDetails.registerOutParameter(1,Types.INTEGER);
				 * updateWcOutDetails.registerOutParameter(10,Types.VARCHAR);
				 * 
				 * updateWcOutDetails.setString(2,apps.getAppRefNo()); //app ref
				 * no
				 * 
				 * if (((apps.getWc()).getOsFundBasedPpl())==0) {
				 * updateWcOutDetails.setNull(3,java.sql.Types.DOUBLE); }else {
				 * 
				 * updateWcOutDetails.setDouble(3,(apps.getWc()).getOsFundBasedPpl
				 * ()); //Fund Based-Principal }
				 * 
				 * if ((apps.getWc()).getOsFundBasedInterestAmt()==0) {
				 * updateWcOutDetails.setDouble(4,0); }else {
				 * updateWcOutDetails.
				 * setDouble(4,(apps.getWc()).getOsFundBasedInterestAmt());
				 * //Fund Based-Interest }
				 * 
				 * if
				 * (!(((apps.getWc()).getOsFundBasedAsOnDate()).toString().equals
				 * ("")) && ((apps.getWc()).getOsFundBasedAsOnDate())!=null) {
				 * updateWcOutDetails.setDate(5,new
				 * java.sql.Date(((apps.getWc())
				 * .getOsFundBasedAsOnDate()).getTime()));//Sanctioned Date
				 * }else {
				 * 
				 * updateWcOutDetails.setDate(5,null); }
				 * 
				 * 
				 * if (((apps.getWc()).getOsNonFundBasedPpl())==0) {
				 * updateWcOutDetails.setDouble(6,0); }else {
				 * 
				 * updateWcOutDetails.setDouble(6,(apps.getWc()).
				 * getOsNonFundBasedPpl()); //Non Fund Based-Ppl }
				 * 
				 * if ((apps.getWc()).getOsNonFundBasedCommissionAmt()==0) {
				 * updateWcOutDetails.setDouble(7,0); }else {
				 * 
				 * updateWcOutDetails.setDouble(7,(apps.getWc()).
				 * getOsNonFundBasedCommissionAmt()); //Commission }
				 * 
				 * //if
				 * (!((apps.getWc()).getOsNonFundBasedAsOnDate().toString().
				 * equals("")) &&
				 * (apps.getWc()).getOsNonFundBasedAsOnDate()!=null) //{ //
				 * updateWcOutDetails.setDate(8,new
				 * java.sql.Date(((apps.getWc())
				 * .getOsNonFundBasedAsOnDate()).getTime()));//Non Fund Based
				 * Sanctioned Date //}else {
				 * 
				 * updateWcOutDetails.setDate(8,null);//Non Fund Based
				 * Sanctioned Date //}
				 * 
				 * updateWcOutDetails.setString(9,userId); //user Id
				 * 
				 * updateWcOutDetails.executeQuery();
				 * 
				 * int updateWcOutDetailsValue=updateWcOutDetails.getInt(1);
				 * Log.log(Log.DEBUG,"ApplicationDAO","updateWCDtl",
				 * "WC Details result :" + updateWcOutDetailsValue);
				 * 
				 * if(updateWcOutDetailsValue==Constants.FUNCTION_FAILURE){
				 * 
				 * String error = updateWcOutDetails.getString(10);
				 * 
				 * updateWcOutDetails.close(); updateWcOutDetails=null;
				 * 
				 * connection.rollback();
				 * 
				 * throw new DatabaseException(error); }
				 * 
				 * }
				 */} else if (appLoanType.equals("TC")) {

				double wcFBSanctioned = (apps.getProjectOutlayDetails())
						.getWcFundBasedSanctioned();
				double wcNFBSanctioned = (apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned();
				double wcPromoterCont = (apps.getProjectOutlayDetails())
						.getWcPromoterContribution();
				double wcSubsidy = (apps.getProjectOutlayDetails())
						.getWcSubsidyOrEquity();
				double wcOthers = (apps.getProjectOutlayDetails())
						.getWcOthers();

				// if((wcPromoterCont!=0)||(wcSubsidy!=0)||(wcOthers!=0)||(wcFBSanctioned!=0))
				// {
				CallableStatement updateWCTCDetails = connection
						.prepareCall("{?=call funcUpdatetWorkingCapital(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				updateWCTCDetails.registerOutParameter(1, Types.INTEGER);
				updateWCTCDetails.registerOutParameter(19, Types.VARCHAR);

				updateWCTCDetails.setString(2, apps.getAppRefNo()); // app ref
																	// no
				updateWCTCDetails.setDouble(3, (apps.getProjectOutlayDetails())
						.getWcFundBasedSanctioned()); // fund based
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"updateWCDtl",
						"wc fund based:"
								+ (apps.getProjectOutlayDetails())
										.getWcFundBasedSanctioned());

				if ((apps.getProjectOutlayDetails())
						.getWcNonFundBasedSanctioned() == 0) {
					updateWCTCDetails.setDouble(4, 0);
				} else {
					updateWCTCDetails.setDouble(4, (apps
							.getProjectOutlayDetails())
							.getWcNonFundBasedSanctioned()); // non fund based
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"updateWCDtl",
						"wc non fund based:"
								+ (apps.getProjectOutlayDetails())
										.getWcNonFundBasedSanctioned());

				if (((apps.getProjectOutlayDetails())
						.getWcPromoterContribution()) == 0) {
					updateWCTCDetails.setNull(5, java.sql.Types.DOUBLE);
				} else {
					updateWCTCDetails.setDouble(5, (apps
							.getProjectOutlayDetails())
							.getWcPromoterContribution());// promoter cont
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"updateWCDtl",
						"wc non fund based:"
								+ (apps.getProjectOutlayDetails())
										.getWcNonFundBasedSanctioned());

				if (((apps.getProjectOutlayDetails()).getWcSubsidyOrEquity()) == 0) {
					updateWCTCDetails.setNull(6, java.sql.Types.DOUBLE);
				} else {
					updateWCTCDetails.setDouble(6, (apps
							.getProjectOutlayDetails()).getWcSubsidyOrEquity()); // subsidy/equity
																					// support
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"updateWCDtl",
						"wc non fund based:"
								+ (apps.getProjectOutlayDetails())
										.getWcSubsidyOrEquity());

				if (((apps.getProjectOutlayDetails()).getWcOthers()) == 0) {
					updateWCTCDetails.setNull(7, java.sql.Types.DOUBLE);
				} else {
					updateWCTCDetails.setDouble(7,
							(apps.getProjectOutlayDetails()).getWcOthers()); // Others
				}
				Log.log(Log.DEBUG,
						"ApplicationDAO",
						"updateWCDtl",
						"wc non fund based:"
								+ (apps.getProjectOutlayDetails())
										.getWcOthers());

				updateWCTCDetails.setNull(8, java.sql.Types.DOUBLE); // Interest
				updateWCTCDetails.setDouble(9, 0); // Commission
				updateWCTCDetails.setNull(10, java.sql.Types.DATE); // Fund
																	// BAsed
																	// Sanctioned
																	// Date
				updateWCTCDetails.setNull(11, java.sql.Types.DATE); // Non Fund
																	// Based
																	// Sanctioned
																	// Date
				updateWCTCDetails.setNull(12, java.sql.Types.DOUBLE); // Credit
																		// guranteed-Fund
																		// Based
				updateWCTCDetails.setDouble(13, 0); // Non Fund Based
				updateWCTCDetails.setNull(14, java.sql.Types.DOUBLE); // PLR
				updateWCTCDetails.setNull(15, java.sql.Types.VARCHAR); // PLR
																		// type
				updateWCTCDetails.setString(16, "T"); // Interest type
				updateWCTCDetails.setNull(17, java.sql.Types.VARCHAR);
				updateWCTCDetails.setString(18, userId);

				updateWCTCDetails.executeQuery();
				int updateWCTCDetailsValue = updateWCTCDetails.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "updateWCDtl",
						"WC Details result :" + updateWCTCDetailsValue);

				if (updateWCTCDetailsValue == Constants.FUNCTION_FAILURE) {

					String error = updateWCTCDetails.getString(19);

					updateWCTCDetails.close();
					updateWCTCDetails = null;

					connection.rollback();

					Log.log(Log.DEBUG, "ApplicationDAO", "updateWCDtl",
							"WC Details exception :" + error);
					throw new DatabaseException(error);

				}
				updateWCTCDetails.close();
				updateWCTCDetails = null;

				// }

			}
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateWCDtl",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		Log.log(Log.INFO, "ApplicationDAO", "updateWCDtl", "Exited");
	}

	/**
	 * This method update the securitization details into the database
	 */
	public void updateSecDetails(Application app, String createdBy,
			Connection connection) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateSecDetails", "Entered");

		try {

			CallableStatement updateSecDtls = connection
					.prepareCall("{?=call funcUpdateSecDetails(?,?,?,?,?,?,?,?,?,?,?)}");

			updateSecDtls.registerOutParameter(1, Types.INTEGER);
			updateSecDtls.registerOutParameter(12, Types.VARCHAR);

			updateSecDtls.setString(2, app.getAppRefNo());

			if (app.getSecuritization().getSpreadOverPLR() == 0) {
				updateSecDtls.setNull(3, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls.setDouble(3, app.getSecuritization()
						.getSpreadOverPLR());
			}

			if (app.getSecuritization().getPplRepaymentInEqual() == null
					&& !((app.getSecuritization().getPplRepaymentInEqual())
							.equals(""))) {
				updateSecDtls.setNull(4, java.sql.Types.VARCHAR);
			} else {
				updateSecDtls.setString(4, app.getSecuritization()
						.getPplRepaymentInEqual());
			}

			if (app.getSecuritization().getTangibleNetWorth() == 0) {
				updateSecDtls.setNull(5, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls.setDouble(5, app.getSecuritization()
						.getTangibleNetWorth());
			}

			if (app.getSecuritization().getFixedACR() == 0) {
				updateSecDtls.setNull(6, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls.setDouble(6, app.getSecuritization()
						.getFixedACR());
			}

			if (app.getSecuritization().getCurrentRatio() == 0) {
				updateSecDtls.setNull(7, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls.setDouble(7, app.getSecuritization()
						.getCurrentRatio());
			}

			if (app.getSecuritization().getMinimumDSCR() == 0) {
				updateSecDtls.setNull(8, java.sql.Types.VARCHAR);
			} else {
				updateSecDtls.setDouble(8, app.getSecuritization()
						.getMinimumDSCR());
			}

			if (app.getSecuritization().getAvgDSCR() == 0) {
				updateSecDtls.setNull(9, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls
						.setDouble(9, app.getSecuritization().getAvgDSCR());
			}

			if (app.getSecuritization().getFinancialPartUnit() == 0) {
				updateSecDtls.setNull(10, java.sql.Types.DOUBLE);
			} else {
				updateSecDtls.setDouble(10, app.getSecuritization()
						.getFinancialPartUnit());
			}

			updateSecDtls.setString(11, createdBy);

			updateSecDtls.execute();
			int updateSecDtlsValue = updateSecDtls.getInt(1);

			if (updateSecDtlsValue == Constants.FUNCTION_FAILURE) {

				String error = updateSecDtls.getString(12);

				updateSecDtls.close();
				updateSecDtls = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateSecDetails", error);
				throw new DatabaseException(error);

			}
			updateSecDtls.close();
			updateSecDtls = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateSecDetails",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
	}

	/**
	 * This method is used to get all the applications pending for re-approval.
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39A64C610275
	 */
	public ArrayList getApplicationsForReapproval(String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getApplicationsForReapproval",
				"Entered");

		ArrayList tcReApprovedList = new ArrayList();
		ArrayList wcReApprovedList = new ArrayList();
		ArrayList clearAppsList = new ArrayList();

		Connection connection = DBConnection.getConnection(false);

		try {

			CallableStatement reApprovalList = connection
					.prepareCall("{?=call packGetAppForReApproval.funcGetAppForReApproval(?,?,?,?,?)}");
			reApprovalList.registerOutParameter(1, Types.INTEGER);
			reApprovalList.registerOutParameter(3, Constants.CURSOR);
			reApprovalList.registerOutParameter(4, Constants.CURSOR);
			reApprovalList.registerOutParameter(5, Types.INTEGER);
			reApprovalList.registerOutParameter(6, Types.VARCHAR);

			reApprovalList.setString(2, userId);

			reApprovalList.execute();
			int functionReturnValue = reApprovalList.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = reApprovalList.getString(6);

				reApprovalList.close();
				reApprovalList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcClearApplications = (ResultSet) reApprovalList
						.getObject(3);
				while (tcClearApplications.next()) {

					Application application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					SSIDetails ssiDetails = new SSIDetails();
					TermLoan termLoan = new TermLoan();
					WorkingCapital workingCapital = new WorkingCapital();
					borrowerDetails.setSsiDetails(ssiDetails);
					application.setBorrowerDetails(borrowerDetails);
					application.setTermLoan(termLoan);
					application.setWc(workingCapital);

					application.setAppRefNo(tcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"getApplicationsForReapproval", "App ref no1:"
									+ application.getAppRefNo());
					//// System.out.println("App Ref No is "
					// + application.getAppRefNo());
					application.setCgpan(tcClearApplications.getString(2));
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcClearApplications.getInt(3));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"getApplicationsForReapproval",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(tcClearApplications.getDate(4));
					if (tcClearApplications.getDouble(6) == 0) {
						application.setApprovedAmount(tcClearApplications
								.getDouble(5));
					} else {

						application.setApprovedAmount(tcClearApplications
								.getDouble(6));
					}
					Log.log(Log.INFO, "ApplicationDAO",
							"getApplicationsForReapproval", "re approve 5"
									+ tcClearApplications.getDouble(5));
					Log.log(Log.INFO, "ApplicationDAO",
							"getApplicationsForReapproval", "re approve 6"
									+ tcClearApplications.getDouble(6));
					Log.log(Log.INFO, "ApplicationDAO",
							"getApplicationsForReapproval", "re approve"
									+ application.getApprovedAmount());

					application.setStatus(tcClearApplications.getString(7));
					Log.log(Log.DEBUG, "ApplicationDAO",
							"getApplicationsForReapproval", "status"
									+ tcClearApplications.getString(6));

					tcReApprovedList.add(application);
				}
				tcClearApplications.close();
				tcClearApplications = null;

				ResultSet wcClearApplications = (ResultSet) reApprovalList
						.getObject(4);
				while (wcClearApplications.next()) {

					Application application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					SSIDetails ssiDetails = new SSIDetails();
					TermLoan termLoan = new TermLoan();
					WorkingCapital workingCapital = new WorkingCapital();
					borrowerDetails.setSsiDetails(ssiDetails);
					application.setBorrowerDetails(borrowerDetails);
					application.setTermLoan(termLoan);
					application.setWc(workingCapital);

					application.setAppRefNo(wcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApproval", "App ref no 2:"
									+ application.getAppRefNo());
					//// System.out.println("WC Appref no is "
					// + application.getAppRefNo());
					application.setCgpan(wcClearApplications.getString(2));
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcClearApplications.getInt(3));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(wcClearApplications.getDate(4));

					if (wcClearApplications.getDouble(6) == 0) {
						application.setApprovedAmount(wcClearApplications
								.getDouble(5));
					} else {

						application.setApprovedAmount(wcClearApplications
								.getDouble(6));
					}

					application.setStatus(wcClearApplications.getString(7));

					wcReApprovedList.add(application);
				}
				wcClearApplications.close();
				wcClearApplications = null;

				reApprovalList.close();
				reApprovalList = null;

			}

			clearAppsList.add(tcReApprovedList);
			clearAppsList.add(wcReApprovedList);

			connection.commit();

		} catch (SQLException sqlException) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());
		} catch (Exception exception) {

			throw new DatabaseException(exception.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "getApplicationsForReapproval",
				"Exited");

		return clearAppsList;
	}

	/**
	 * This method updates the decision taken on the probable duplicate
	 * applications by the CGTSI user.
	 * 
	 * @param applications
	 * @throws DatabaseException
	 * @roseuid 39A64D3A0227
	 */
	public void updateApplicationStatus(Application application,
			String createdBy) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateApplicationStatus",
				"Entered");
		//// System.out.println("ApplicationDAO"+"updateApplicationStatus"+"Entered");
		Connection connection = DBConnection.getNewConnection(false);

		java.util.Date utilDate = new java.util.Date();
		//System.out.println("<br>application DAO==15368===");
		try {
			connection.commit();

			CallableStatement updateAppStatus = connection
					.prepareCall("{?=call funcUpdateAppStatus(?,?,?,?,?,?,?,?)}");
		//	System.out.println("<br>application DAO==153748===");
			updateAppStatus.registerOutParameter(1, Types.INTEGER);
			updateAppStatus.registerOutParameter(9, Types.VARCHAR);

			updateAppStatus.setString(2, application.getAppRefNo());
			//// System.out.println(application.getAppRefNo());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Application app ref no :" + application.getAppRefNo());
			updateAppStatus.setString(3, application.getCgpan());
			//// System.out.println(application.getCgpan());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Application Status :" + application.getCgpan());
			updateAppStatus.setString(4, createdBy);
			//// System.out.println("createdBy:"+createdBy);
			if (application.getApprovedAmount() != 0) {
				updateAppStatus.setDouble(5, application.getApprovedAmount());
				//// System.out.println(application.getApprovedAmount());
			} else {

				updateAppStatus.setNull(5, java.sql.Types.DOUBLE);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Application Amount :" + application.getApprovedAmount());
			updateAppStatus.setDate(6, new java.sql.Date(utilDate.getTime()));
			//// System.out.println(utilDate.getTime());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Application Status :" + application.getStatus());
			updateAppStatus.setString(7, application.getStatus());
			//// System.out.println(application.getStatus());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Application Renmarks:" + application.getRemarks());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {

				updateAppStatus.setString(8, application.getRemarks());
				//// System.out.println(application.getRemarks());
			} else {

				updateAppStatus.setString(8, null);
			}

			try {
				updateAppStatus.execute();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			int functionReturnValue = updateAppStatus.getInt(1);
			//// System.out.println("functionReturnValue:"+functionReturnValue);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateApplicationStatus",
					"Get update App Status Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateAppStatus.getString(9);
				//// System.out.println("error:"+error);
				updateAppStatus.close();
				updateAppStatus = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updateAppStatus.close();
			updateAppStatus = null;

			// updating cgpan
			updateCgpan(application, connection);

			// generate cgbid
			if (!application.getAdditionalTC() && !application.getWcRenewal()) {
				int ssiRefNo = application.getBorrowerDetails().getSsiDetails()
						.getBorrowerRefNo();
				String cgbid = generateCgbid(ssiRefNo, connection);
				if (cgbid != null && !(cgbid.equals(""))) {
					application.getBorrowerDetails().getSsiDetails()
							.setCgbid(cgbid);

					// updation of cgbid
					updateCgbid(ssiRefNo, cgbid, connection);

				}

			}

			//

			if (application.getLoanType().equals("TC")) {
				updateWcTenure(application, connection);
			}

			connection.commit();
			connection.close();
			connection = null;

		} catch (Exception sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "updateApplicationStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (Exception ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			connection = null;
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateApplicationStatus", "Exited");

	}

	/**
	 * 
	 * @param application
	 * @param createdBy
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void updateRejectedApplicationStatus(Application application,
			String createdBy) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateRejectedApplicationStatus",
				"Entered");
		//// System.out.println("ApplicationDAO"+"updateRejectedApplicationStatus"+"Entered");
		Connection connection = DBConnection.getNewConnection(false);

		java.util.Date utilDate = new java.util.Date();

		try {
			connection.commit();

			CallableStatement updateAppStatus = connection
					.prepareCall("{?=call funcUpdateReAppStatusForRejApp(?,?,?,?,?,?,?,?)}");

			updateAppStatus.registerOutParameter(1, Types.INTEGER);
			updateAppStatus.registerOutParameter(9, Types.VARCHAR);

			updateAppStatus.setString(2, application.getAppRefNo());
			//// System.out.println(application.getAppRefNo());
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus",
					"Application app ref no :" + application.getAppRefNo());
			updateAppStatus.setString(3, application.getCgpan());
			//// System.out.println(application.getCgpan());
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus", "Application Status :"
							+ application.getCgpan());
			updateAppStatus.setString(4, createdBy);
			//// System.out.println("createdBy:"+createdBy);
			if (application.getApprovedAmount() != 0) {
				updateAppStatus.setDouble(5, application.getApprovedAmount());
				//// System.out.println(application.getApprovedAmount());
			} else {

				updateAppStatus.setNull(5, java.sql.Types.DOUBLE);
			}
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus", "Application Amount :"
							+ application.getApprovedAmount());
			updateAppStatus.setDate(6, new java.sql.Date(utilDate.getTime()));
			//// System.out.println(new java.sql.Date(utilDate.getTime()));
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus", "Application Status :"
							+ application.getStatus());
			updateAppStatus.setString(7, application.getStatus());
			//// System.out.println(application.getStatus());
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus", "Application Renmarks:"
							+ application.getRemarks());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {

				updateAppStatus.setString(8, application.getRemarks());
				//// System.out.println(application.getRemarks());
			} else {

				updateAppStatus.setString(8, null);
			}

			try {
				updateAppStatus.execute();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			int functionReturnValue = updateAppStatus.getInt(1);
			//// System.out.println("functionReturnValue:"+functionReturnValue);
			Log.log(Log.DEBUG, "ApplicationDAO",
					"updateRejectedApplicationStatus",
					"Get update App Status Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateAppStatus.getString(9);
				//// System.out.println("error:"+error);
				updateAppStatus.close();
				updateAppStatus = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updateAppStatus.close();
			updateAppStatus = null;

			// updating cgpan
			updateCgpan(application, connection);

			// generate cgbid
			if (!application.getAdditionalTC() && !application.getWcRenewal()) {
				int ssiRefNo = application.getBorrowerDetails().getSsiDetails()
						.getBorrowerRefNo();
				String cgbid = generateCgbid(ssiRefNo, connection);
				if (cgbid != null && !(cgbid.equals(""))) {
					application.getBorrowerDetails().getSsiDetails()
							.setCgbid(cgbid);

					// updation of cgbid
					updateCgbid(ssiRefNo, cgbid, connection);

				}

			}

			//

			if (application.getLoanType().equals("TC")) {
				updateWcTenure(application, connection);
			}

			connection.commit();
			connection.close();
			connection = null;

		} catch (Exception sqlException) {
			Log.log(Log.INFO, "ApplicationDAO",
					"updateRejectedApplicationStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (Exception ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			connection = null;
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateRejectedApplicationStatus",
				"Exited");

	}

	/**
	 * This method is used to view the application details by supplying either
	 * Borrower reference number or application reference number or CGPAN.
	 * 
	 * The type of ID is resolved by the accompaning type parameter.
	 * 
	 * 0-Borrower Ref. Number 1- Application reference number 2- CGPAN
	 * 
	 * @param id
	 * @param type
	 * @throws DatabaseException
	 * @roseuid 39A65190006A
	 * 
	 *          public void viewApplication(String id, int type) throws
	 *          DatabaseException {
	 * 
	 *          }
	 * 
	 *          /** This method is used to submit the additional term credit
	 *          application. This additional term credit details are saved into
	 *          database.
	 * @param application
	 * @throws DatabaseException
	 * @roseuid 39B0D3550234
	 */
	public String submitAddlTermCredit(Application application, String createdBy)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		String appRefNo = "";

		try {

			MCGSProcessor mcgsProcessor = new MCGSProcessor();
			RiskManagementProcessor rpProcessor = new RiskManagementProcessor();

			Log.log(Log.INFO, "ApplicationDAO", "submitAddlTermCredit",
					"Entered");
			int ssiRefNo = ((application.getBorrowerDetails()).getSsiDetails())
					.getBorrowerRefNo();
			((application.getBorrowerDetails()).getSsiDetails())
					.setBorrowerRefNo(ssiRefNo);
			// submitApp(application,createdBy,connection);
			String subSchemeName = rpProcessor.getSubScheme(application);
			application.setSubSchemeName(subSchemeName);

			double balanceAppAmt = getBalanceApprovedAmt(application);

			String ssiRefNumber = Integer.toString(ssiRefNo);
			RpDAO rpDAO1 = new RpDAO();
			double prevTotalSancAmt = rpDAO1
					.getTotalSanctionedAmountNew(ssiRefNumber);
			ApplicationDAO appdao = new ApplicationDAO();

			double prevTotalHandloomSancAmt = appdao
					.getTotalSanctionedHandloomAmountNew(ssiRefNumber);

			double currentCreditAmount = 0;
			if (application.getLoanType().equals("TC")) {
				currentCreditAmount = application.getTermLoan()
						.getCreditGuaranteed();
			} else if (application.getLoanType().equals("CC")) {
				currentCreditAmount = application.getTermLoan()
						.getCreditGuaranteed()
						+ application.getWc().getCreditFundBased()
						+ +application.getWc().getCreditNonFundBased();
			} else if (application.getLoanType().equals("WC")) {
				currentCreditAmount = application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased();
			}

			if ((currentCreditAmount > 200000)
					&& (application.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			} else if ((currentCreditAmount + prevTotalHandloomSancAmt > 200000)
					&& (application.getDcHandlooms().equals("Y"))) {
				throw new DatabaseException(
						"Guarantee of Rs. "
								+ prevTotalHandloomSancAmt
								+ " is already available for the Borrower. Total Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee upto  Rs. 200000 as per ceiling fixed by Office of DC Handlooms");
			}

			if (application.getLoanType().equals("TC")
					|| application.getLoanType().equals("CC")) {
				if (application.getTermLoan().getCreditGuaranteed() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			} else if (application.getLoanType().equals("WC")) {
				if (application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			} else if (application.getLoanType().equals("BO")) {
				if (application.getTermLoan().getCreditGuaranteed()
						+ application.getWc().getCreditFundBased()
						+ application.getWc().getCreditNonFundBased() > balanceAppAmt) {
					throw new DatabaseException(
							"Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee :"
									+ balanceAppAmt);
				}
			}

			appRefNo = submitApplicationDetails(application, createdBy,
					connection);
			Log.log(Log.INFO, "ApplicationDAO", "submitAddlTermCredit",
					"addtl term credit app ref no :" + appRefNo);
			application.setAppRefNo(appRefNo);
			submitGuarantorSecurityDetails(application, connection);
			submitTermCreditDetails(application, createdBy, connection);
			submitWCDetails(application, createdBy, connection);
			submitSecDetails(application, connection);

			if (application.getMCGFDetails() != null) {
				MCGFDetails mcgfDetails = application.getMCGFDetails();
				mcgfDetails.setApplicationReferenceNumber(appRefNo);
				application.setMCGFDetails(mcgfDetails);
				mcgsProcessor.updateMCGSDetails(mcgfDetails, createdBy,
						connection);
			}

			connection.commit();

		} catch (SQLException e) {

			Log.log(Log.ERROR, "ApplicationDAO", "submitApp", e.getMessage());
			Log.logException(e);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException("Unable to submit Application");
		}

		Log.log(Log.INFO, "ApplicationDAO", "submitAddlTermCredit", "Exited");

		return appRefNo;

	}

	/**
	 * This method returns an ArrayList of CGPANs for the given id and type.
	 * 
	 * @param id
	 *            - id could be CGBID or member id.
	 * @param type
	 *            - This int is used to identify the accompanying id.
	 * 
	 *            0-Member id 1-CGBID
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39B0DB64021D
	 */
	public ArrayList getAppRefNos(String mliId, String borrowerId,
			String borrowerName) throws DatabaseException,
			NoApplicationFoundException {
		Log.log(Log.INFO, "ApplicationDAO", "getAppRefNos", "Entered");

		Connection connection = DBConnection.getConnection();
		ArrayList cgpanAppRefNoList = null;
		String bankId = "";
		String zoneId = "";
		String branchId = "";

		// If mliId is entered then split the Id into bank,zone and branch ID

		bankId = mliId.substring(0, 4); // getting the bank id

		zoneId = mliId.substring(4, 8); // getting the zone id

		branchId = mliId.substring(8, 12); // getting the branch id

		/*
		 * Based on different combinations of parameters entered,the
		 * corresponding methods are called
		 */
		try {
			/*
			 * only BID is entered if
			 * (!(borrowerId.equals(""))&&(mliId.equals(""
			 * ))&&(borrowerName.equals(""))) {
			 * Log.log(Log.INFO,"ApplicationDAO"
			 * ,"getAppRefNos","Only BID entered...");
			 * 
			 * cgpanAppRefNoList=getDtlForBID(borrowerId); } //only borrower
			 * name is entered else if
			 * ((borrowerId.equals(""))&&(mliId.equals(""))&&
			 * !(borrowerName.equals(""))) {
			 * Log.log(Log.INFO,"ApplicationDAO","getAppRefNos"
			 * ,"Only borrowerName entered...");
			 * 
			 * cgpanAppRefNoList=getDtlForBName(borrowerName); } //only MLI id
			 * is entered else if
			 * ((borrowerId.equals(""))&&(!(mliId.equals("")))
			 * &&(borrowerName.equals(""))) {
			 * Log.log(Log.INFO,"ApplicationDAO","getAppRefNos"
			 * ,"Only MLIID entered...");
			 * 
			 * cgpanAppRefNoList=getDtlForMem(bankId,zoneId,branchId); }
			 */

			// if borrower name and mli id are entered
			if ((!(mliId.equals(""))) && (!(borrowerName.equals("")))) {

				cgpanAppRefNoList = getDtlForBorMem(borrowerName, bankId,
						zoneId, branchId);
			}
			// if BID and mli id are entered
			else if ((!(borrowerId.equals(""))) && !(mliId.equals(""))) {

				cgpanAppRefNoList = getDtlForBIDMem(borrowerId, bankId, zoneId,
						branchId);
			}

			connection.commit();
		} catch (DatabaseException dbException) {

			Log.log(Log.INFO, "ApplicationDAO", "getAppRefNos",
					dbException.getMessage());
			Log.logException(dbException);
			throw new DatabaseException(dbException.getMessage());

		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "getAppRefNos",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getAppRefNos", "Exited");

		return cgpanAppRefNoList;

	}

	// Added by ritesh path on 23Nov2006 to filter records based on mli
	/**
	 * This method returns an ArrayList of applications pending for approval
	 * based on mli.
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39B0FC18035C
	 */
	public ArrayList viewApplicationsForApprovalPath(String userId,
			String bankName) throws DatabaseException {
		//System.out.println("In viewApplicationsForApprovalPath ");
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApprovalPath",
				"Entered");
		//// System.out.println("PATH Inside viewApplicationsForApprovalPath **** userId = "+userId);
		ArrayList tcApprovedList = new ArrayList();
		ArrayList wcApprovedList = new ArrayList();
		ArrayList clearAppsList = new ArrayList();
		Connection connection = DBConnection.getConnection(false);
		try {
			CallableStatement approvalList = connection
					.prepareCall("{?=call packGetPackagePath1.funcGetAppForApprovalPath1(?,?,?,?,?,?)}");
			approvalList.registerOutParameter(1, Types.INTEGER);
			approvalList.registerOutParameter(3, Constants.CURSOR);
			approvalList.registerOutParameter(4, Constants.CURSOR);
			approvalList.registerOutParameter(5, Types.INTEGER);
			approvalList.registerOutParameter(6, Types.VARCHAR);

			approvalList.setString(2, userId);
			approvalList.setString(7, bankName);
			approvalList.execute();
			int functionReturnValue = approvalList.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {
				String error = approvalList.getString(6);
				approvalList.close();
				approvalList = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet tcClearApplications = (ResultSet) approvalList
						.getObject(3);
				Application application = null;
				while (tcClearApplications.next()) {
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);
					application.setAppRefNo(tcClearApplications.getString(1)); // app
																				// ref
																				// no
					//System.out.println(tcClearApplications.getDate(3)
							//+ "In viewApplicationsForApprovalPath AppRefNo "
							//+ tcClearApplications.getString(1));
					Log.log(Log.DEBUG, "ApplicationDAO", "n", "App ref no1:"
							+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApprovalPath",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(tcClearApplications.getDate(3));

					tcApprovedList.add(application);
					application = null;
				}
				tcClearApplications.close();
				tcClearApplications = null;
				ResultSet wcClearApplications = (ResultSet) approvalList
						.getObject(4);
				while (wcClearApplications.next()) {

					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);
					application.setAppRefNo(wcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApprovalPath", "App ref no 2:"
									+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApprovalPath",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(wcClearApplications.getDate(3));
					//System.out
							//.println(application.getAppRefNo()
									//+ "viewApplicationsForApprovalPath wcClearApplications.next "
									//+ wcClearApplications.getDate(3));
					wcApprovedList.add(application);
					application = null;
				}
				wcClearApplications.close();
				wcClearApplications = null;
			}
			int appCount = approvalList.getInt(5);
			approvalList.close();
			approvalList = null;
			/**
			 * This function gets all the applications with status 'PE' from the
			 * intranet tables
			 */
			CallableStatement pendingApprovalList = connection
					.prepareCall("{?=call packGetPackagePath.funcGetPendingAppForApproval1(?,?,?,?,?,?)}");
			pendingApprovalList.registerOutParameter(1, Types.INTEGER);
			pendingApprovalList.registerOutParameter(3, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(4, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(5, Types.INTEGER);
			pendingApprovalList.registerOutParameter(6, Types.VARCHAR);
			pendingApprovalList.setString(2, userId);
			pendingApprovalList.setString(7, bankName);
			pendingApprovalList.execute();
			int functionReturnValue1 = pendingApprovalList.getInt(1);
			if (functionReturnValue1 == Constants.FUNCTION_FAILURE) {
				String error = pendingApprovalList.getString(6);
				pendingApprovalList.close();
				pendingApprovalList = null;
				connection.rollback();
				throw new DatabaseException(error);
			} else {
				ResultSet tcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(3);
				Application application = null;
				while (tcPendingApplications.next()) {
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(tcPendingApplications.getString(1)); // app
																					// ref
																					// no
					// Log.log(Log.DEBUG,"ApplicationDAO","n","App ref no1:" +
					// application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcPendingApplications.getInt(2));
					// Log.log(Log.DEBUG,"ApplicationDAO","viewApplicationsForApprovalPath","SSI Ref No1:"
					// +
					// (application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo());
					application.setSubmittedDate(tcPendingApplications
							.getDate(3));
				//	System.out.println(tcPendingApplications.getDate(3)
						//	+ "In viewApplicationsForApprovalPath AppRefNo "
						//	+ tcPendingApplications.getString(1));
					tcApprovedList.add(application);
					application = null;
				}
				tcPendingApplications.close();
				tcPendingApplications = null;
				ResultSet wcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(4);
				while (wcPendingApplications.next()) {
					//System.out
							//.println("viewApplicationsForApprovalPath wcPendingApplications ");
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);
					application.setAppRefNo(wcPendingApplications.getString(1)); // app
																					// ref
																					// no
					// Log.log(Log.DEBUG,"ApplicationDAO","viewApplicationsForApprovalPath","App ref no 2:"
					// + application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcPendingApplications.getInt(2));
					// Log.log(Log.DEBUG,"ApplicationDAO","viewApplicationsForApprovalPath","SSI Ref No 2:"
					// +
					// (application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo());
					application.setSubmittedDate(wcPendingApplications
							.getDate(3));
					wcApprovedList.add(application);
					application = null;
				}
				wcPendingApplications.close();
				wcPendingApplications = null;
			}
			int pendingCount = pendingApprovalList.getInt(5);
			// Log.log(Log.INFO,"ApplicationDAO","viewApplicationsForApprovalPath","pendingCount"
			// + pendingCount);
			int applicationCount = appCount + pendingCount;
			Log.log(Log.INFO, "ApplicationDAO",
					"viewApplicationsForApprovalPath", "applicationCount"
							+ applicationCount);
			Integer intCount = new Integer(applicationCount);
			clearAppsList.add(tcApprovedList);
			clearAppsList.add(wcApprovedList);
			clearAppsList.add(intCount);
			tcApprovedList = null;
			wcApprovedList = null;
			connection.commit();
		} catch (SQLException sqlException) {
			Log.log(Log.ERROR, "ApplicationDAO",
					"viewApplicationsForApprovalPath",
					sqlException.getMessage());
			Log.logException(sqlException);
			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApprovalPath",
				"Exited");
		return clearAppsList;
	}

	/**
	 * This method returns an ArrayList of applications pending for approval.
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39B0FC18035C
	 */
	public ArrayList viewApplicationsForApproval(String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
				"Entered");
		ArrayList tcApprovedList = new ArrayList();
		ArrayList wcApprovedList = new ArrayList();
		ArrayList clearAppsList = new ArrayList();

		Connection connection = DBConnection.getConnection(false);
		try {
			CallableStatement approvalList = connection
					.prepareCall("{?=call packGetAppForApproval.funcGetAppForApproval(?,?,?,?,?)}");
			approvalList.registerOutParameter(1, Types.INTEGER);
			approvalList.registerOutParameter(3, Constants.CURSOR);
			approvalList.registerOutParameter(4, Constants.CURSOR);
			approvalList.registerOutParameter(5, Types.INTEGER);
			approvalList.registerOutParameter(6, Types.VARCHAR);

			approvalList.setString(2, userId);

			approvalList.execute();
			int functionReturnValue = approvalList.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = approvalList.getString(6);

				approvalList.close();
				approvalList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcClearApplications = (ResultSet) approvalList
						.getObject(3);
				Application application = null;
				int up = 0;
				int up2 = 0;
				while (tcClearApplications.next()) {
					up++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(tcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO", "n", "App ref no1:"
							+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(tcClearApplications.getDate(3));

					tcApprovedList.add(application);
					application = null;
					// if( up == 450){
					// break;
					// }
				}

				tcClearApplications.close();
				tcClearApplications = null;

				ResultSet wcClearApplications = (ResultSet) approvalList
						.getObject(4);
				// Application application= null;
				while (wcClearApplications.next()) {
					up2++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(wcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApproval", "App ref no 2:"
									+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(wcClearApplications.getDate(3));

					wcApprovedList.add(application);
					application = null;
					// if(up2 == 450){
					// break;
					// }
				}

				wcClearApplications.close();
				wcClearApplications = null;

			}
			int appCount = approvalList.getInt(5);

			approvalList.close();
			approvalList = null;

			/**
			 * This function gets all the applications with status 'PE' from the
			 * intranet tables
			 */
			CallableStatement pendingApprovalList = connection
					.prepareCall("{?=call packGetPendingAppForApproval.funcGetPendingAppForApproval(?,?,?,?,?)}");
			pendingApprovalList.registerOutParameter(1, Types.INTEGER);
			pendingApprovalList.registerOutParameter(3, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(4, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(5, Types.INTEGER);
			pendingApprovalList.registerOutParameter(6, Types.VARCHAR);

			pendingApprovalList.setString(2, userId);

			pendingApprovalList.execute();

			int functionReturnValue1 = pendingApprovalList.getInt(1);
			if (functionReturnValue1 == Constants.FUNCTION_FAILURE) {

				String error = pendingApprovalList.getString(6);

				pendingApprovalList.close();
				pendingApprovalList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(3);
				Application application = null;
				int up3 = 0;
				int up4 = 0;
				while (tcPendingApplications.next()) {
					up3++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(tcPendingApplications.getString(1)); // app
																					// ref
																					// no
					Log.log(Log.DEBUG, "ApplicationDAO", "n", "App ref no1:"
							+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcPendingApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application.setSubmittedDate(tcPendingApplications
							.getDate(3));

					tcApprovedList.add(application);
					application = null;
					// if(up3 == 450){
					// break;
					// }
				}

				tcPendingApplications.close();
				tcPendingApplications = null;

				ResultSet wcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(4);
				// Application application= null;
				while (wcPendingApplications.next()) {
					up4++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(wcPendingApplications.getString(1)); // app
																					// ref
																					// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApproval", "App ref no 2:"
									+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcPendingApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application.setSubmittedDate(wcPendingApplications
							.getDate(3));

					wcApprovedList.add(application);
					application = null;
					// if(up4 == 450){
					// break;
					// }
				}

				wcPendingApplications.close();
				wcPendingApplications = null;

			}

			int pendingCount = pendingApprovalList.getInt(5);
			Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
					"pendingCount" + pendingCount);

			int applicationCount = appCount + pendingCount;

			Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
					"applicationCount" + applicationCount);

			Integer intCount = new Integer(applicationCount);

			clearAppsList.add(tcApprovedList);
			clearAppsList.add(wcApprovedList);
			clearAppsList.add(intCount);
			tcApprovedList = null;
			wcApprovedList = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "viewApplicationsForApproval",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
				"Exited");

		return clearAppsList;
	}

	/**
	 * This method is used to get all the CGPANs while user requesting for
	 * working capital renewal.
	 * 
	 * @param id
	 * @param type
	 * @return ArrayList
	 * @roseuid 39B1663101EB
	 */
	public ArrayList getCgpansForRenewal(String id, String type) {
		return null;
	}

	/**
	 * This method is used to update the loan amount for a given CGPAN.(approved
	 * amount will be modified:This method is used in RP)
	 * 
	 * @param CGPAN
	 * @param ApprovedAmount
	 * @param ReapprovalComments
	 * @throws DatabaseException
	 * @roseuid 39B48DD200E2
	 */
	public void updateLoanAmount(String CGPAN, Double ApprovedAmount,
			String ReapprovalComments) throws DatabaseException {

	}

	/**
	 * This method is used to change the application status after the loan
	 * amount is changed. (This method is used in RP)
	 * 
	 * @param cgpan
	 * @param status
	 * @throws DatabaseException
	 * @roseuid 39B48DED0339
	 */
	public void changeApplicationStatus(String cgpan, String status)
			throws DatabaseException {

	}

	/**
	 * This method is used to fetch all live and approved applications.(This
	 * method is used in RP)
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39B4A11B0390
	 */
	public ArrayList getLiveApplications(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		String methodName = "getLiveApplications";

		//// System.out.println("getLiveApplications entered line number-10009");
		Connection connection = null;
		CallableStatement getliveApplicationStmt = null;
		ArrayList liveApplications = null;
		ResultSet resultSet = null;

		Application application = null;
		SSIDetails ssiDetails = null;
		BorrowerDetails borrowerDetails = null;

		int status = 0;
		Log.log(Log.INFO, "ApplicationDAO", methodName, "Entering");
		try {
			String exception = "";

			connection = DBConnection.getConnection(false);
			if (bankId == null) {
				getliveApplicationStmt = connection
						.prepareCall("{?=call packGetLiveApp.funcGetLiveApp(?,?)}");
			} else {
				/**
				 * New procedure had been created and called by
				 * sudeep.dhiman@pathinfotech.com on dec 22 this new procedure
				 * also retrive the ssi district, state, and promoter sex, and
				 * social category old procedure calling commented below
				 */
				// getliveApplicationStmt = connection.prepareCall(
				// "{?=call packGetMemberLiveApp.funcGetMemberLiveApp(?,?,?,?,?)}");

				/* new procedure */getliveApplicationStmt = connection
						.prepareCall("{?=call packGetMemberLiveApppath.funcGetMemberLiveApp(?,?,?,?,?)}");

				getliveApplicationStmt.setString(4, bankId);
				getliveApplicationStmt.setString(5, zoneId);
				getliveApplicationStmt.setString(6, branchId);
			}

			getliveApplicationStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getliveApplicationStmt.registerOutParameter(2, Constants.CURSOR);
			getliveApplicationStmt.registerOutParameter(3, Types.VARCHAR);

			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"Before executing Stored Procedure");
			getliveApplicationStmt.execute();
			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"After executing Stored Procedure");

			resultSet = (ResultSet) getliveApplicationStmt.getObject(2);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "ResultSet = "
					+ resultSet.toString());

			status = getliveApplicationStmt.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "Status = "
					+ status);
			if (status != 0) {
				String error = getliveApplicationStmt.getString(3);
				getliveApplicationStmt.close();
				getliveApplicationStmt = null;

				throw new DatabaseException(error);
			}

			int recordCount = 0;

			while (resultSet.next()) {
				if (recordCount == 0) {
					liveApplications = new ArrayList();
				}
				application = new Application();

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				application.setCgpan(resultSet.getString(1));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				application.setBankId(resultSet.getString(2));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				application.setZoneId(resultSet.getString(3));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				application.setBranchId(resultSet.getString(4));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Guarantee Fee="
								+ resultSet.getDouble(5));
				application.setGuaranteeAmount(resultSet.getDouble(5));

				ssiDetails = new SSIDetails();
				ssiDetails.setCgbid(resultSet.getString(6));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Approved Amount="
								+ resultSet.getDouble(7));
				application.setApprovedAmount(resultSet.getDouble(7));

				int schemeId = resultSet.getInt(8);
				//// System.out.println("Line number 11133 Scheme Id:"+schemeId);

				if (schemeId == 1) {
					application.setScheme("CGFSI");
				} else if (schemeId == 2) {
					application.setScheme("MCGS");
				} else if (schemeId == 3) {
					application.setScheme("RSF");
				}

				/**
				 * _________________________________________________________________________
				 * following lines of code added by
				 * sudeep.dhiman@pathinfotech.com on dec 22
				 */
				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : district=" + resultSet.getString(9));
				application.setDistrict(resultSet.getString(9));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : State=" + resultSet.getString(10));
				application.setState(resultSet.getString(10));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : SocialCategory ="
								+ resultSet.getString(11));
				application.setSocialCategory(resultSet.getString(11));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : sex=" + resultSet.getString(12));
				application.setSex(resultSet.getString(12));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : ssiRef=" + resultSet.getString(13));
				application.setSsiRef(resultSet.getString(13));

				/* _____________________________________________________________ */

				borrowerDetails = new BorrowerDetails();
				borrowerDetails.setSsiDetails(ssiDetails);

				application.setBorrowerDetails(borrowerDetails);

				liveApplications.add(application);
				++recordCount;
			}

			resultSet.close();
			resultSet = null;
			getliveApplicationStmt.close();
			getliveApplicationStmt = null;

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return liveApplications;
	}

	/* ----------------------- */

	/**
	 * added by sukumar on 20-08-2008 for getting expired cases
	 * 
	 * @param bankId
	 * @param zoneId
	 * @param branchId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public ArrayList getExpApplications(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		String methodName = "getExpApplications";

		//// System.out.println("getExpApplications entered line number-10160 in ApplicationDAO.java");
		Connection connection = null;
		CallableStatement getliveApplicationStmt = null;
		ArrayList liveApplications = null;
		ResultSet resultSet = null;

		Application application = null;
		SSIDetails ssiDetails = null;
		BorrowerDetails borrowerDetails = null;

		int status = 0;
		Log.log(Log.INFO, "ApplicationDAO", methodName, "Entering");
		try {
			String exception = "";

			connection = DBConnection.getConnection(false);
			if (bankId == null) {
				getliveApplicationStmt = connection
						.prepareCall("{?=call packGetExpApp.funcGetExpApp(?,?)}");
			} else {
				/**
				 * New procedure had been created and called by
				 * sudeep.dhiman@pathinfotech.com on dec 22 this new procedure
				 * also retrive the ssi district, state, and promoter sex, and
				 * social category old procedure calling commented below
				 */
				getliveApplicationStmt = connection
						.prepareCall("{?=call packGetMemberExpAppPath.funcGetMemberExpApp(?,?,?,?,?)}");

				getliveApplicationStmt.setString(4, bankId);
				getliveApplicationStmt.setString(5, zoneId);
				getliveApplicationStmt.setString(6, branchId);
			}

			getliveApplicationStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getliveApplicationStmt.registerOutParameter(2, Constants.CURSOR);
			getliveApplicationStmt.registerOutParameter(3, Types.VARCHAR);

			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"Before executing Stored Procedure");
			getliveApplicationStmt.execute();
			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"After executing Stored Procedure");

			resultSet = (ResultSet) getliveApplicationStmt.getObject(2);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "ResultSet = "
					+ resultSet.toString());

			status = getliveApplicationStmt.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "Status = "
					+ status);
			if (status != 0) {
				String error = getliveApplicationStmt.getString(3);
				getliveApplicationStmt.close();
				getliveApplicationStmt = null;

				throw new DatabaseException(error);
			}

			int recordCount = 0;

			while (resultSet.next()) {
				if (recordCount == 0) {
					liveApplications = new ArrayList();
				}
				application = new Application();

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				application.setCgpan(resultSet.getString(1));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				application.setBankId(resultSet.getString(2));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				application.setZoneId(resultSet.getString(3));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				application.setBranchId(resultSet.getString(4));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Guarantee Fee="
								+ resultSet.getDouble(5));
				application.setGuaranteeAmount(resultSet.getDouble(5));

				ssiDetails = new SSIDetails();
				ssiDetails.setCgbid(resultSet.getString(6));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Approved Amount="
								+ resultSet.getDouble(7));
				application.setApprovedAmount(resultSet.getDouble(7));

				int schemeId = resultSet.getInt(8);

				if (schemeId == 1) {
					application.setScheme("CGFSI");
				} else if (schemeId == 2) {
					application.setScheme("MCGS");
				}

				/**
				 * _________________________________________________________________________
				 * following lines of code added by
				 * sudeep.dhiman@pathinfotech.com on dec 22
				 */
				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : district=" + resultSet.getString(9));
				application.setDistrict(resultSet.getString(9));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : State=" + resultSet.getString(10));
				application.setState(resultSet.getString(10));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : SocialCategory ="
								+ resultSet.getString(11));
				application.setSocialCategory(resultSet.getString(11));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : sex=" + resultSet.getString(12));
				application.setSex(resultSet.getString(12));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : ssiRef=" + resultSet.getString(13));
				application.setSsiRef(resultSet.getString(13));

				/* _____________________________________________________________ */

				borrowerDetails = new BorrowerDetails();
				borrowerDetails.setSsiDetails(ssiDetails);

				application.setBorrowerDetails(borrowerDetails);

				liveApplications.add(application);
				++recordCount;
			}

			resultSet.close();
			resultSet = null;
			getliveApplicationStmt.close();
			getliveApplicationStmt = null;

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return liveApplications;
	}

	/* ------------------------- */
	/**
	 * This method is used to fetch all expired and closed applications.(This
	 * method is used in RP)
	 * 
	 * @return ArrayList
	 * @throws DatabaseException
	 * @roseuid 39B4A11B0390
	 */
	public ArrayList getExpiredCloseApplications(String bankId, String zoneId,
			String branchId) throws DatabaseException {
		String methodName = "getExpiredCloseApplications";
		Connection connection = null;
		CallableStatement getExpiredApplicationStmt = null;
		ArrayList expiredApplications = null;
		ResultSet resultSet = null;

		Application application = null;
		SSIDetails ssiDetails = null;
		BorrowerDetails borrowerDetails = null;

		int status = 0;
		Log.log(Log.INFO, "ApplicationDAO", methodName, "Entering");
		try {
			String exception = "";

			connection = DBConnection.getConnection(false);
			if (bankId == null) {
				getExpiredApplicationStmt = connection
						.prepareCall("{?=call packGetExpiredApp.funcGetExpiredApp(?,?)}");
			} else {
				getExpiredApplicationStmt = connection
						.prepareCall("{?=call packGetMemberExpApp.funcGetMemberExpApp(?,?,?,?,?)}");
				getExpiredApplicationStmt.setString(4, bankId);
				getExpiredApplicationStmt.setString(5, zoneId);
				getExpiredApplicationStmt.setString(6, branchId);
			}

			getExpiredApplicationStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			getExpiredApplicationStmt.registerOutParameter(2, Constants.CURSOR);
			getExpiredApplicationStmt.registerOutParameter(3, Types.VARCHAR);

			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"Before executing Stored Procedure");
			getExpiredApplicationStmt.execute();
			Log.log(Log.INFO, "ApplicationDAO", methodName,
					"After executing Stored Procedure");

			resultSet = (ResultSet) getExpiredApplicationStmt.getObject(2);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "ResultSet = "
					+ resultSet.toString());

			status = getExpiredApplicationStmt.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", methodName, "Status = "
					+ status);
			if (status != 0) {
				String error = getExpiredApplicationStmt.getString(3);
				getExpiredApplicationStmt.close();
				getExpiredApplicationStmt = null;

				throw new DatabaseException(error);
			}

			int recordCount = 0;

			while (resultSet.next()) {
				if (recordCount == 0) {
					expiredApplications = new ArrayList();
				}
				application = new Application();

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : CGPAN=" + resultSet.getString(1));
				application.setCgpan(resultSet.getString(1));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Bank Id=" + resultSet.getString(2));
				application.setBankId(resultSet.getString(2));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Zone Id=" + resultSet.getString(3));
				application.setZoneId(resultSet.getString(3));

				Log.log(Log.INFO, "ApplicationDAO", methodName,
						"Inside While : Branch Id=" + resultSet.getString(4));
				application.setBranchId(resultSet.getString(4));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Guarantee Fee="
								+ resultSet.getDouble(5));
				application.setGuaranteeAmount(resultSet.getDouble(5));

				ssiDetails = new SSIDetails();
				ssiDetails.setCgbid(resultSet.getString(6));

				Log.log(Log.INFO,
						"ApplicationDAO",
						methodName,
						"Inside While : Approved Amount="
								+ resultSet.getDouble(7));
				application.setApprovedAmount(resultSet.getDouble(7));

				application.setSubmittedDate(DateHelper.sqlToUtilDate(resultSet
						.getDate(8)));

				borrowerDetails = new BorrowerDetails();
				borrowerDetails.setSsiDetails(ssiDetails);

				application.setBorrowerDetails(borrowerDetails);

				expiredApplications.add(application);
				++recordCount;
			}

			resultSet.close();
			resultSet = null;
			getExpiredApplicationStmt.close();
			getExpiredApplicationStmt = null;

		} catch (Exception exception) {
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return expiredApplications;
	}

	/**
	 * This method is used get the outstanding amount for the given CGPAN.(This
	 * method is used in RP)
	 * 
	 * @param cgpan
	 * @return double
	 * @throws DatabaseException
	 * @roseuid 39B4A2550034
	 */
	public double getOutstandingAmount(String cgpan) throws DatabaseException {
		return 0;
	}

	/**
	 * This method used to get all the applcations pertaining to a ID. this id
	 * could be CGBID or MLI Id or CGPAN.
	 * 
	 * @param id
	 *            - Type of id. It could be CGPAN or CGBID or MLI ID. This type
	 *            is resolved by the accompanying parameter.
	 * @param type
	 *            - Type of id. This parameter is used to identify the type of
	 *            ID is passed.
	 * @return ArrayList
	 * @roseuid 39B9D28D0177
	 */
	public ArrayList getAllApplications(int flag, String mliId) {
		return null;
	}

	/**
	 * This method retrieves all the message titles and the message contents
	 * from the database
	 */
	public ArrayList getMessageTitleContent() throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getMessageTitleContent", "Entered");

		Connection connection = DBConnection.getConnection(false);
		ArrayList msgTitles = new ArrayList();
		ArrayList msgContents = new ArrayList();
		ArrayList msgTitleContent = new ArrayList();

		try {
			CallableStatement messageTitleList = connection
					.prepareCall("{?=call packgetInsUpdSpecialMsg.funcGetAllMsgTitles(?,?)}");

			messageTitleList.registerOutParameter(1, Types.INTEGER);
			messageTitleList.registerOutParameter(2, Constants.CURSOR);
			messageTitleList.registerOutParameter(3, Types.VARCHAR);

			messageTitleList.execute();

			int functionReturnValue = messageTitleList.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getMessageTitleContent",
					"Special Message Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = messageTitleList.getString(3);

				messageTitleList.close();
				messageTitleList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet messageTitles = (ResultSet) messageTitleList
						.getObject(2);
				while (messageTitles.next()) {

					String title = messageTitles.getString(1);
					Log.log(Log.DEBUG, "ApplicationDAO",
							"getMessageTitleContent",
							"Title from the result Set :" + title);

					String titleMessage = messageTitles.getString(2);
					Log.log(Log.DEBUG, "ApplicationDAO",
							"getMessageTitleContent",
							"Title Message from the result Set :"
									+ titleMessage);

					msgTitles.add(title);
					msgContents.add(titleMessage);
				}
				messageTitles.close();
				messageTitles = null;
				messageTitleList.close();
				messageTitleList = null;

			}

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getMessageTitleContent",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		msgTitleContent.add(msgTitles);
		msgTitleContent.add(msgContents);

		Log.log(Log.INFO, "ApplicationDAO", "getMessageTitleContent", "Exited");

		return msgTitleContent;
	}

	/**
	 * This method retrieves the message content for the title passed
	 */

	public SpecialMessage getMessageDesc(String msgTitle)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getMessageDesc", "Entered");

		Connection connection = DBConnection.getConnection(false);
		SpecialMessage specialMessage = new SpecialMessage();
		DateConverter dateConverter = new DateConverter();

		try {
			CallableStatement messageDescList = connection
					.prepareCall("{?=call packgetInsUpdSpecialMsg.funcGetMsgForTitle(?,?,?,?,?)}");
			messageDescList.registerOutParameter(1, Types.INTEGER);
			messageDescList.registerOutParameter(3, Types.VARCHAR);
			messageDescList.registerOutParameter(4, Types.DATE);
			messageDescList.registerOutParameter(5, Types.DATE);
			messageDescList.registerOutParameter(6, Types.VARCHAR);

			messageDescList.setString(2, msgTitle);

			messageDescList.execute();

			int functionReturnValue = messageDescList.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getMessageDesc",
					"Special Message Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = messageDescList.getString(6);

				messageDescList.close();
				messageDescList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				specialMessage.setMessageDesc(messageDescList.getString(3));
				specialMessage.setValidityFromDate(DateHelper
						.sqlToUtilDate(messageDescList.getDate(4)));
				specialMessage.setValidityToDate(DateHelper
						.sqlToUtilDate(messageDescList.getDate(5)));

				messageDescList.close();
				messageDescList = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getMessageDesc",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getMessageDesc", "Exited");

		return specialMessage;
	}

	/**
	 * This method adds the special message details into the database
	 */
	public void addSpecialMessage(SpecialMessage specialMessage)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "addSpecialMessage", "Entered");

		Connection connection = DBConnection.getConnection(false);

		try {
			CallableStatement insertSplMsg = connection
					.prepareCall("{?=call packgetInsUpdSpecialMsg.funcInsertSplMsg(?,?,?,?,?)}");

			insertSplMsg.registerOutParameter(1, Types.INTEGER);
			insertSplMsg.registerOutParameter(6, Types.VARCHAR);

			insertSplMsg.setString(2, specialMessage.getMsgTitle());
			insertSplMsg.setString(3, specialMessage.getMessageDesc());
			insertSplMsg.setDate(
					4,
					new java.sql.Date((specialMessage.getValidityFromDate())
							.getTime()));
			Log.log(Log.DEBUG, "ApplicationDAO", "addSpecialMessage",
					"to date :" + specialMessage.getValidityToDate());
			if (specialMessage.getValidityToDate() != null
					&& !specialMessage.getValidityToDate().toString()
							.equals("")) {
				insertSplMsg.setDate(
						5,
						new java.sql.Date((specialMessage.getValidityToDate())
								.getTime()));
			} else {
				insertSplMsg.setDate(5, null);
			}

			insertSplMsg.execute();

			int functionReturnValue = insertSplMsg.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "addSpecialMessage",
					"Insert Special Message Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = insertSplMsg.getString(6);

				insertSplMsg.close();
				insertSplMsg = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			insertSplMsg.close();
			insertSplMsg = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "addSpecialMessage",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "addSpecialMessage", "Exited");
	}

	/**
	 * This method updates the special message details into the database
	 */
	public void updateSpecialMessage(SpecialMessage specialMessage)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateSpecialMessage", "Entered");

		Connection connection = DBConnection.getConnection(false);

		try {
			CallableStatement updateSplMsg = connection
					.prepareCall("{?=call packgetInsUpdSpecialMsg.funcUpdateSplMsg(?,?,?,?,?)}");

			updateSplMsg.registerOutParameter(1, Types.INTEGER);
			updateSplMsg.registerOutParameter(6, Types.VARCHAR);

			updateSplMsg.setString(2, specialMessage.getMsgTitle());
			updateSplMsg.setString(3, specialMessage.getMessageDesc());
			updateSplMsg.setDate(
					4,
					new java.sql.Date((specialMessage.getValidityFromDate())
							.getTime()));
			if (specialMessage.getValidityToDate() != null
					&& !specialMessage.getValidityToDate().toString()
							.equals("")) {
				updateSplMsg.setDate(
						5,
						new java.sql.Date((specialMessage.getValidityToDate())
								.getTime()));
			} else {
				updateSplMsg.setDate(5, null);
			}

			updateSplMsg.execute();

			int functionReturnValue = updateSplMsg.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateSpecialMessage",
					"Update Special Message Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateSplMsg.getString(6);

				updateSplMsg.close();
				updateSplMsg = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			updateSplMsg.close();
			updateSplMsg = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateSpecialMessage",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "updateSpecialMessage", "Exited");
	}

	/**
	 * This method is used to generate cgpan for approved applications
	 */
	public String generateCgpan(Application application)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "generateCgpan", "Entered");

		String cgpan = "";
		Connection connection = DBConnection.getConnection(false);
		try {
			String loanType = application.getLoanType();

			Log.log(Log.INFO, "ApplicationDAO", "generateCgpan",
					"CGPAN Reference :" + application.getCgpanReference());

			if ((application.getCgpanReference() != null && !(application
					.getCgpanReference().equals("")))
					&& !(application.getCgpanReference().substring(0, 2)
							.equals("CG"))) {
				Log.log(Log.INFO, "ApplicationDAO", "generateCgpan",
						"cgpan reference not null");
				CallableStatement bothCgpan = connection
						.prepareCall("{?=call funcGenCGPANforBoth(?,?,?)}");

				bothCgpan.registerOutParameter(1, Types.INTEGER);
				bothCgpan.setString(2, application.getAppRefNo());
				bothCgpan.registerOutParameter(3, Types.VARCHAR); // cgpan
				bothCgpan.registerOutParameter(4, Types.VARCHAR);

				bothCgpan.execute();

				int functionReturnValue = bothCgpan.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "generateCgpan",
						"generate cgpan:" + functionReturnValue);

				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = bothCgpan.getString(4);

					bothCgpan.close();
					bothCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {

					cgpan = bothCgpan.getString(3);

					bothCgpan.close();
					bothCgpan = null;

				}

			} else if (loanType.equals("TC") || loanType.equals("CC")) {
				CallableStatement tcCgpan = connection
						.prepareCall("{?=call funcGenCGPANforTC(?,?)}");

				tcCgpan.registerOutParameter(1, Types.INTEGER);
				tcCgpan.registerOutParameter(2, Types.VARCHAR); // cgpan
				tcCgpan.registerOutParameter(3, Types.VARCHAR);

				tcCgpan.execute();

				int functionReturnValue = tcCgpan.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "generateCgpan",
						"generate cgpan:" + functionReturnValue);

				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = tcCgpan.getString(3);

					tcCgpan.close();
					tcCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {

					cgpan = tcCgpan.getString(2);

					tcCgpan.close();
					tcCgpan = null;

				}

			} else if (loanType.equals("WC")) {
				CallableStatement wcCgpan = connection
						.prepareCall("{?=call funcGenCGPANforWC(?,?)}");

				wcCgpan.registerOutParameter(1, Types.INTEGER);
				wcCgpan.registerOutParameter(2, Types.VARCHAR); // cgpan
				wcCgpan.registerOutParameter(3, Types.VARCHAR);

				wcCgpan.execute();

				int functionReturnValue = wcCgpan.getInt(1);
				Log.log(Log.DEBUG, "ApplicationDAO", "generateCgpan",
						"generate cgpan:" + functionReturnValue);

				if (functionReturnValue == Constants.FUNCTION_FAILURE) {

					String error = wcCgpan.getString(3);

					wcCgpan.close();
					wcCgpan = null;

					connection.rollback();

					throw new DatabaseException(error);
				} else {

					cgpan = wcCgpan.getString(2);

					wcCgpan.close();
					wcCgpan = null;

				}

			}

			connection.commit();
		}

		catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "generateCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "generateCgpan", "Exited");

		return cgpan;
	}

	/**
	 * This method updates cgpan into the DB
	 */

	public void updateCgpan(Application application, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateCgpan", "Entered");

		// Connection connection=DBConnection.getConnection(false);

		try {

			CallableStatement cgpanUpdate = connection
					.prepareCall("{?=call funcUpdateCGPAN(?,?,?)}");

			cgpanUpdate.registerOutParameter(1, Types.INTEGER);
			cgpanUpdate.registerOutParameter(4, Types.VARCHAR);

			cgpanUpdate.setString(2, application.getAppRefNo());
			cgpanUpdate.setString(3, application.getCgpan());

			cgpanUpdate.execute();

			int functionReturnValue = cgpanUpdate.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateCgpan", "update cgpan:"
					+ functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = cgpanUpdate.getString(4);

				cgpanUpdate.close();
				cgpanUpdate = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			cgpanUpdate.close();
			cgpanUpdate = null;

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
		Log.log(Log.INFO, "ApplicationDAO", "updateCgpan", "Exited");

	}

	/**
	 * This method updates the General status for unapproved applications
	 */

	public void updateGeneralStatus(Application application, String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateGeneralStatus", "Entered");
		Connection connection = DBConnection.getConnection(false);
		//System.out.println("<br>application DAO==17483===");
		try {
			CallableStatement generalUpdate = connection
					.prepareCall("{?=call funcUpdateGeneralStatus(?,?,?,?,?)}");
		//	System.out.println("<br>application DAO==17488===");
			generalUpdate.registerOutParameter(1, Types.INTEGER);
			generalUpdate.registerOutParameter(6, Types.VARCHAR);

			generalUpdate.setString(2, application.getAppRefNo());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateGeneralStatus",
					"app ref no:" + application.getAppRefNo());
			generalUpdate.setString(3, application.getStatus());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateGeneralStatus",
					"status:" + application.getStatus());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {
				generalUpdate.setString(4, application.getRemarks());
			} else {

				generalUpdate.setString(4, null);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "updateGeneralStatus",
					"remarks:" + application.getRemarks());

			generalUpdate.setString(5, userId);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateGeneralStatus",
					"user id:" + userId);

			generalUpdate.execute();

			int functionReturnValue = generalUpdate.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateGeneralStatus",
					"update general status:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = generalUpdate.getString(6);

				generalUpdate.close();
				generalUpdate = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			generalUpdate.close();
			generalUpdate = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateGeneralStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "updateGeneralStatus", "Exited");

	}

	/**
	 * This method updates the General status for unapproved applications
	 */

	public String generateCgbid(int ssiRefNo, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "generateCgbid", "Entered");
		// Connection connection=DBConnection.getConnection(false);

		String cgbid = "";

		try {
			CallableStatement cgbidValue = connection
					.prepareCall("{?=call funcGenerateBID(?,?,?)}");

			cgbidValue.registerOutParameter(1, Types.INTEGER);
			cgbidValue.registerOutParameter(3, Types.VARCHAR);
			cgbidValue.registerOutParameter(4, Types.VARCHAR);

			cgbidValue.setInt(2, ssiRefNo);

			cgbidValue.execute();

			int functionReturnValue = cgbidValue.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "generateCgbid",
					"generation of cgbid:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = cgbidValue.getString(4);

				cgbidValue.close();
				cgbidValue = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {

				cgbid = cgbidValue.getString(3);

				cgbidValue.close();
				cgbidValue = null;

				Log.log(Log.INFO, "ApplicationDAO", "cgbid value :", cgbid);

			}

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "generateCgbid",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
		Log.log(Log.INFO, "ApplicationDAO", "generateCgbid", "Exited");

		return cgbid;

	}

	/**
	 * This method updates the cgbid approved applications
	 */

	public void updateCgbid(int ssiRefNo, String cgbid, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateCgbid", "Entered");
		// Connection connection=DBConnection.getConnection(false);

		try {
			CallableStatement cgbidUpdate = connection
					.prepareCall("{?=call funcUpdateBID(?,?,?)}");

			cgbidUpdate.registerOutParameter(1, Types.INTEGER);
			cgbidUpdate.registerOutParameter(4, Types.VARCHAR);

			cgbidUpdate.setInt(2, ssiRefNo);
			cgbidUpdate.setString(3, cgbid);

			cgbidUpdate.execute();

			int functionReturnValue = cgbidUpdate.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateCgbid", "update cgbid:"
					+ functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = cgbidUpdate.getString(4);

				cgbidUpdate.close();
				cgbidUpdate = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			cgbidUpdate.close();
			cgbidUpdate = null;

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateCgbid",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		}
		Log.log(Log.INFO, "ApplicationDAO", "updateCgbid", "Exited");

	}

	/**
	 * This method is used to view the borrower details with ssi reference
	 * number as the input
	 */

	public BorrowerDetails viewBorrowerDetails(int ssiRefNo)
			throws DatabaseException {
		Application application = new Application();
		BorrowerDetails borrowerDetails = new BorrowerDetails();
		SSIDetails ssiDetails = new SSIDetails();

		Log.log(Log.INFO, "ApplicationDAO", "viewBorrowerDetails", "Entered");

		Connection connection = DBConnection.getConnection(false);
		//// System.out.println("in  Application77");

		try {
			CallableStatement ssiDtlForSsiRefNo = connection.prepareCall
			// ("{?=call funcGetSSIDetailforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

					("{?=call funcGetSSIDetailforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			Log.log(Log.INFO, "ApplicationDAO", "fetchBorrowerDtls",
					"SSI REF No" + ssiRefNo);

			ssiDtlForSsiRefNo.setInt(2, ssiRefNo); // CGBID

			ssiDtlForSsiRefNo.registerOutParameter(1, Types.INTEGER);
			ssiDtlForSsiRefNo.registerOutParameter(33, Types.VARCHAR);

			ssiDtlForSsiRefNo.registerOutParameter(3, Types.INTEGER); // SSI Ref
																		// No
			ssiDtlForSsiRefNo.registerOutParameter(4, Types.VARCHAR); // Already
																		// Covered
																		// by
																		// CGTSI
			ssiDtlForSsiRefNo.registerOutParameter(5, Types.VARCHAR); // Already
																		// assisted
																		// by
																		// bank
			ssiDtlForSsiRefNo.registerOutParameter(6, Types.VARCHAR); // SSI
																		// Unique
																		// No
			ssiDtlForSsiRefNo.registerOutParameter(7, Types.VARCHAR); // NPA
			ssiDtlForSsiRefNo.registerOutParameter(8, Types.VARCHAR); // Constitution
			ssiDtlForSsiRefNo.registerOutParameter(9, Types.VARCHAR); // SSI
																		// Type
			ssiDtlForSsiRefNo.registerOutParameter(10, Types.VARCHAR); // unit
																		// Name
			ssiDtlForSsiRefNo.registerOutParameter(11, Types.VARCHAR); // SSI
																		// reg
																		// no
			ssiDtlForSsiRefNo.registerOutParameter(12, Types.DATE); // Commencement
																	// Date
			ssiDtlForSsiRefNo.registerOutParameter(13, Types.VARCHAR); // SSI
																		// ITPAN
			ssiDtlForSsiRefNo.registerOutParameter(14, Types.VARCHAR); // type
																		// of
																		// Activity
			ssiDtlForSsiRefNo.registerOutParameter(15, Types.INTEGER); // No Of
																		// Employees
			ssiDtlForSsiRefNo.registerOutParameter(16, Types.DOUBLE); // project
																		// Sales
			ssiDtlForSsiRefNo.registerOutParameter(17, Types.DOUBLE); // Project
																		// Exports
			ssiDtlForSsiRefNo.registerOutParameter(18, Types.VARCHAR); // SSI
																		// Address
			ssiDtlForSsiRefNo.registerOutParameter(19, Types.VARCHAR); // SSi
																		// City
			ssiDtlForSsiRefNo.registerOutParameter(20, Types.VARCHAR); // Pincode
			ssiDtlForSsiRefNo.registerOutParameter(21, Types.VARCHAR); // Display
																		// Defaulter
			ssiDtlForSsiRefNo.registerOutParameter(22, Types.VARCHAR); // SSI
																		// District
			ssiDtlForSsiRefNo.registerOutParameter(23, Types.VARCHAR); // SSI
																		// State
			ssiDtlForSsiRefNo.registerOutParameter(24, Types.VARCHAR); // Industry
																		// Nature
			ssiDtlForSsiRefNo.registerOutParameter(25, Types.VARCHAR); // Indutry
																		// Sector
																		// name
			ssiDtlForSsiRefNo.registerOutParameter(26, Types.VARCHAR); // Status
			ssiDtlForSsiRefNo.registerOutParameter(27, Types.VARCHAR); // BID
			ssiDtlForSsiRefNo.registerOutParameter(28, Types.DOUBLE); // Outstanding
																		// amount
			ssiDtlForSsiRefNo.registerOutParameter(29, Types.VARCHAR); // MCGS
																		// Flag

			/*
			 * ssiDtlForSsiRefNo.executeQuery(); int
			 * ssiDtlForSsiRefNoValue=ssiDtlForSsiRefNo.getInt(1);
			 * 
			 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
			 * "SSI Details for CGBID :" + ssiDtlForSsiRefNoValue);
			 * 
			 * if(ssiDtlForSsiRefNoValue==Constants.FUNCTION_FAILURE){
			 * 
			 * String error = ssiDtlForSsiRefNo.getString(30);
			 * 
			 * ssiDtlForSsiRefNo.close(); ssiDtlForSsiRefNo=null;
			 * 
			 * Log.log(Log.DEBUG,"ApplicationDAO","fetchBorrowerDtls",
			 * "SSI Exception message:" + error);
			 * 
			 * throw new DatabaseException(error);
			 * 
			 * } else {
			 * ssiDetails.setBorrowerRefNo(ssiDtlForSsiRefNo.getInt(3));
			 * borrowerDetails
			 * .setPreviouslyCovered(ssiDtlForSsiRefNo.getString(4).trim());
			 * borrowerDetails
			 * .setAssistedByBank(ssiDtlForSsiRefNo.getString(5).trim());
			 * borrowerDetails.setNpa(ssiDtlForSsiRefNo.getString(7).trim());
			 * ssiDetails.setConstitution(ssiDtlForSsiRefNo.getString(8));
			 * ssiDetails.setSsiType(ssiDtlForSsiRefNo.getString(9).trim());
			 * ssiDetails.setSsiName(ssiDtlForSsiRefNo.getString(10));
			 * ssiDetails.setRegNo(ssiDtlForSsiRefNo.getString(11));
			 * ssiDetails.setSsiITPan(ssiDtlForSsiRefNo.getString(13));
			 * ssiDetails.setActivityType(ssiDtlForSsiRefNo.getString(14));
			 * ssiDetails.setEmployeeNos(ssiDtlForSsiRefNo.getInt(15));
			 * ssiDetails
			 * .setProjectedSalesTurnover(ssiDtlForSsiRefNo.getDouble(16));
			 * ssiDetails.setProjectedExports(ssiDtlForSsiRefNo.getDouble(17));
			 * ssiDetails.setAddress(ssiDtlForSsiRefNo.getString(18));
			 * ssiDetails.setCity(ssiDtlForSsiRefNo.getString(19));
			 * ssiDetails.setPincode(ssiDtlForSsiRefNo.getString(20).trim());
			 * ssiDetails.setDistrict(ssiDtlForSsiRefNo.getString(22));
			 * ssiDetails.setState(ssiDtlForSsiRefNo.getString(23));
			 * ssiDetails.setIndustryNature(ssiDtlForSsiRefNo.getString(24));
			 * ssiDetails.setIndustrySector(ssiDtlForSsiRefNo.getString(25));
			 * ssiDetails.setCgbid(ssiDtlForSsiRefNo.getString(27));
			 * borrowerDetails.setOsAmt(ssiDtlForSsiRefNo.getDouble(28));
			 */

			ssiDtlForSsiRefNo.registerOutParameter(30, Types.VARCHAR); //
			ssiDtlForSsiRefNo.registerOutParameter(31, Types.VARCHAR); //
			ssiDtlForSsiRefNo.registerOutParameter(32, Types.VARCHAR); //
			// ssiObj.registerOutParameter(33,Types.VARCHAR);

			ssiDtlForSsiRefNo.executeQuery();
			int ssiObjValue = ssiDtlForSsiRefNo.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "submitPromotersDetails",
					"SSI Details :" + ssiObjValue);

			if (ssiObjValue == Constants.FUNCTION_FAILURE) {

				// String error = ssiObj.getString(30);

				String error = ssiDtlForSsiRefNo.getString(33);

				ssiDtlForSsiRefNo.close();
				ssiDtlForSsiRefNo = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ssiDetails.setBorrowerRefNo(ssiDtlForSsiRefNo.getInt(3));
				borrowerDetails.setPreviouslyCovered(ssiDtlForSsiRefNo
						.getString(4).trim());
				borrowerDetails.setAssistedByBank(ssiDtlForSsiRefNo
						.getString(5).trim());
				borrowerDetails.setNpa(ssiDtlForSsiRefNo.getString(7).trim());
				ssiDetails.setConstitution(ssiDtlForSsiRefNo.getString(8));
				ssiDetails.setSsiType(ssiDtlForSsiRefNo.getString(9).trim());
				ssiDetails.setSsiName(ssiDtlForSsiRefNo.getString(10));
				ssiDetails.setRegNo(ssiDtlForSsiRefNo.getString(11));
				ssiDetails.setSsiITPan(ssiDtlForSsiRefNo.getString(13));
				ssiDetails.setActivityType(ssiDtlForSsiRefNo.getString(14));
				ssiDetails.setEmployeeNos(ssiDtlForSsiRefNo.getInt(15));
				ssiDetails.setProjectedSalesTurnover(ssiDtlForSsiRefNo
						.getDouble(16));
				ssiDetails.setProjectedExports(ssiDtlForSsiRefNo.getDouble(17));
				ssiDetails.setAddress(ssiDtlForSsiRefNo.getString(18));
				ssiDetails.setCity(ssiDtlForSsiRefNo.getString(19));
				ssiDetails.setPincode(ssiDtlForSsiRefNo.getString(20).trim());
				ssiDetails.setDistrict(ssiDtlForSsiRefNo.getString(22));
				ssiDetails.setState(ssiDtlForSsiRefNo.getString(23));
				ssiDetails.setIndustryNature(ssiDtlForSsiRefNo.getString(24));
				ssiDetails.setIndustrySector(ssiDtlForSsiRefNo.getString(25));
				ssiDetails.setCgbid(ssiDtlForSsiRefNo.getString(27));

				// kot
				// ssiDetails.setAddress(ssiObj.getString(28));

				// ssiDetails.setMSE(ssiDtlForSsiRefNo.getString(29));

				// ssiDetails.setMSE(ssiObj.getString(30));

				ssiDetails.setEnterprise(ssiDtlForSsiRefNo.getString(30));

				ssiDetails.setUnitAssisted(ssiDtlForSsiRefNo.getString(31));

				ssiDetails.setWomenOperated(ssiDtlForSsiRefNo.getString(32));

				/*
				 * ssiDetails.setEnterprise("N");
				 * 
				 * ssiDetails.setUnitAssisted("Y");
				 * 
				 * 
				 * ssiDetails.setWomenOperated("Y");
				 */
				borrowerDetails.setOsAmt(ssiDtlForSsiRefNo.getDouble(28));

				ssiDtlForSsiRefNo.close();
				ssiDtlForSsiRefNo = null;

			}
			//// System.out.println("in  Application88");
			// promoter details
			CallableStatement ssiDtlForSsiRef = connection
					.prepareCall("{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			ssiDtlForSsiRef.setInt(2, ssiRefNo); // AppRefNo

			ssiDtlForSsiRef.registerOutParameter(1, Types.INTEGER);
			ssiDtlForSsiRef.registerOutParameter(23, Types.VARCHAR);

			ssiDtlForSsiRef.registerOutParameter(3, Types.INTEGER); // SSI RefNo
			ssiDtlForSsiRef.registerOutParameter(4, Types.VARCHAR); // Chief
																	// title
			ssiDtlForSsiRef.registerOutParameter(5, Types.VARCHAR); // First
																	// name
			ssiDtlForSsiRef.registerOutParameter(6, Types.VARCHAR); // Middle
																	// Name
			ssiDtlForSsiRef.registerOutParameter(7, Types.VARCHAR); // Last name
			ssiDtlForSsiRef.registerOutParameter(8, Types.VARCHAR); // Chief
																	// ITPAN
			ssiDtlForSsiRef.registerOutParameter(9, Types.VARCHAR); // Gender
			ssiDtlForSsiRef.registerOutParameter(10, Types.DATE); // DOB
			ssiDtlForSsiRef.registerOutParameter(11, Types.VARCHAR); // Legal
																		// Type
			ssiDtlForSsiRef.registerOutParameter(12, Types.VARCHAR); // LegalID
			ssiDtlForSsiRef.registerOutParameter(13, Types.VARCHAR); // Promoter
																		// First
																		// Name
			ssiDtlForSsiRef.registerOutParameter(14, Types.DATE); // Promoter
																	// First DOB
			ssiDtlForSsiRef.registerOutParameter(15, Types.VARCHAR); // Promoter
																		// FirstITPAN
			ssiDtlForSsiRef.registerOutParameter(16, Types.VARCHAR); // Promoter
																		// Second
																		// Name
			ssiDtlForSsiRef.registerOutParameter(17, Types.DATE); // Promoter
																	// Second
																	// DOB
			ssiDtlForSsiRef.registerOutParameter(18, Types.VARCHAR); // Promoter
																		// Second
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(19, Types.VARCHAR); // Promoter
																		// Third
																		// Name
			ssiDtlForSsiRef.registerOutParameter(20, Types.DATE); // Promoter
																	// Third DOB
			ssiDtlForSsiRef.registerOutParameter(21, Types.VARCHAR); // Promoter
																		// Third
																		// ITPAN
			ssiDtlForSsiRef.registerOutParameter(22, Types.VARCHAR);

			ssiDtlForSsiRef.executeQuery();
			int ssiDtlForSsiRefValue = ssiDtlForSsiRef.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "getApplication",
					"Promoters Details :" + ssiDtlForSsiRefValue);

			if (ssiDtlForSsiRefValue == Constants.FUNCTION_FAILURE) {

				String error = ssiDtlForSsiRef.getString(23);

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getApplication",
						"Promoter Exception message:" + error);

				throw new DatabaseException(error);
			} else {
				ssiDetails.setCpTitle(ssiDtlForSsiRef.getString(4));
				ssiDetails.setCpFirstName(ssiDtlForSsiRef.getString(5));
				ssiDetails.setCpMiddleName(ssiDtlForSsiRef.getString(6));
				ssiDetails.setCpLastName(ssiDtlForSsiRef.getString(7));
				ssiDetails.setCpITPAN(ssiDtlForSsiRef.getString(8));
				ssiDetails.setCpGender(ssiDtlForSsiRef.getString(9).trim());
				ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(10)));
				ssiDetails.setCpLegalID(ssiDtlForSsiRef.getString(11));
				ssiDetails.setCpLegalIdValue(ssiDtlForSsiRef.getString(12));
				ssiDetails.setFirstName(ssiDtlForSsiRef.getString(13));
				ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(14)));
				ssiDetails.setFirstItpan(ssiDtlForSsiRef.getString(15));
				ssiDetails.setSecondName(ssiDtlForSsiRef.getString(16));
				ssiDetails.setSecondDOB(DateHelper
						.sqlToUtilDate(ssiDtlForSsiRef.getDate(17)));
				ssiDetails.setSecondItpan(ssiDtlForSsiRef.getString(18));
				ssiDetails.setThirdName(ssiDtlForSsiRef.getString(19));
				ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(ssiDtlForSsiRef
						.getDate(20)));
				ssiDetails.setThirdItpan(ssiDtlForSsiRef.getString(21));
				ssiDetails.setSocialCategory(ssiDtlForSsiRef.getString(22));

				ssiDtlForSsiRef.close();
				ssiDtlForSsiRef = null;

			}

			borrowerDetails.setSsiDetails(ssiDetails);

			application.setBorrowerDetails(borrowerDetails);
			(application.getBorrowerDetails()).setSsiDetails(ssiDetails);

			Log.log(Log.DEBUG, "ApplicationDAO", "fetchBorrowerDtls",
					"Fetching BorrowerDetails over...");

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateCgbid",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "updateCgbid", "Exited");

		return borrowerDetails;
	}

	/*
	 * This method returns the total corpus amount
	 */
	public double getCorpusAmount() throws DatabaseException {

		Log.log(Log.INFO, "ApplicationDAO", "getCorpusAmount", "Entered");

		Connection connection = DBConnection.getConnection(false);
		Application application = new Application();

		double corpusAmount = 0;

		try {
			CallableStatement calCorpusAmount = connection
					.prepareCall("{?=call funcGetCorpusAmt(?,?)}");

			calCorpusAmount.registerOutParameter(1, Types.INTEGER);
			calCorpusAmount.registerOutParameter(2, Types.DOUBLE);
			calCorpusAmount.registerOutParameter(3, Types.VARCHAR);

			calCorpusAmount.execute();

			int functionReturnValue = calCorpusAmount.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getCorpusAmount",
					"corpus Amount:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = calCorpusAmount.getString(3);

				calCorpusAmount.close();
				calCorpusAmount = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "getCorpusAmount",
						"corpus Amount exception:" + error);

				throw new DatabaseException(error);

			} else {

				corpusAmount = calCorpusAmount.getDouble(2);
				calCorpusAmount.close();
				calCorpusAmount = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCorpusAmount",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getCorpusAmount", "Exited");

		return corpusAmount;

	}

	/*
	 * This method checks whether the entered is within the securitization pool
	 */
	public void checkCgpanPool(String appRefNo) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "checkCgpanPool", "Entered");
		Connection connection = DBConnection.getConnection();

		Application application = new Application();

		try {
			CallableStatement checkCgpanModify = connection
					.prepareCall("{?=call funcCheckCGPANModify(?,?)}");

			checkCgpanModify.registerOutParameter(1, Types.INTEGER);
			checkCgpanModify.registerOutParameter(3, Types.VARCHAR);

			checkCgpanModify.setString(2, appRefNo);

			checkCgpanModify.execute();

			int functionReturnValue = checkCgpanModify.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "checkCgpanPool",
					"cgpan pool:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = checkCgpanModify.getString(3);

				checkCgpanModify.close();
				checkCgpanModify = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "checkCgpanPool",
						"checkCgpanPool exception:" + error);

				throw new DatabaseException(error);

			}
			checkCgpanModify.close();
			checkCgpanModify = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "checkCgpanPool",
					sqlException.getMessage());
			Log.logException(sqlException);

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "checkCgpanPool", "Exited");

	}

	/*
	 * This method updates the status of the reapproved Applications into the DB
	 */
	public void updateReapprovalStatus(Application application, String userId)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		java.util.Date sysDate = new java.util.Date();

		try {
			CallableStatement updateReapproveStatus = connection
					.prepareCall("{?=call funcUpdateReApprovalStatus(?,?,?,?,?,?,?)}");

			updateReapproveStatus.registerOutParameter(1, Types.INTEGER);
			updateReapproveStatus.registerOutParameter(8, Types.VARCHAR);

			updateReapproveStatus.setString(2, application.getCgpan());
			updateReapproveStatus.setString(3, userId);
			if (application.getReapprovedAmount() != 0) {
				updateReapproveStatus.setDouble(4,
						application.getReapprovedAmount());
			} else {

				updateReapproveStatus.setNull(4, java.sql.Types.DOUBLE);
			}
			updateReapproveStatus.setDate(5,
					new java.sql.Date(sysDate.getTime()));
			updateReapproveStatus.setString(6, application.getStatus());
			if (application.getReapprovalRemarks() != null
					&& !(application.getReapprovalRemarks().equals(""))) {

				updateReapproveStatus.setString(7,
						application.getReapprovalRemarks());
			} else {

				updateReapproveStatus.setString(7, null);
			}

			updateReapproveStatus.execute();

			int functionReturnValue = updateReapproveStatus.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "updateReapprovalStatus",
					"update reapprove status:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateReapproveStatus.getString(8);

				updateReapproveStatus.close();
				updateReapproveStatus = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "updateReapprovalStatus",
						"updateReapprovalStatus exception:" + error);

				throw new DatabaseException(error);

			}
			updateReapproveStatus.close();
			updateReapproveStatus = null;

			if (application.getCgpan().substring(11, 13).equals("TC")) {

				CPDAO cpDAO = new CPDAO();
				String appRefNumber = cpDAO.getAppRefNumber(application
						.getCgpan());
				application.setAppRefNo(appRefNumber);
				updateWcTenure(application, connection);
			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateReapprovalStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "updateReapprovalStatus", "Exited");

	}

	/**
	 * This method generates Cgpan for renewed WC Applications
	 * 
	 * @return String
	 * @throws DatabaseException
	 */
	public String generateRenewCgpan(String renewalCgpan)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		java.util.Date sysDate = new java.util.Date();

		String renewCgpan = "";

		try {
			CallableStatement cgpanRenew = connection
					.prepareCall("{?=call funcGenCGPANforWCRenewal(?,?,?)}");

			cgpanRenew.registerOutParameter(1, Types.INTEGER);
			cgpanRenew.registerOutParameter(4, Types.VARCHAR);

			cgpanRenew.registerOutParameter(3, Types.VARCHAR);

			cgpanRenew.setString(2, renewalCgpan);
			//// System.out.println("Input Cgpan:"+renewalCgpan);

			cgpanRenew.execute();

			int functionReturnValue = cgpanRenew.getInt(1);

			//// System.out.println("functionReturnValue:"+functionReturnValue);

			Log.log(Log.DEBUG, "ApplicationDAO", "generateRenewCgpan",
					"ugenerateRenewCgpan:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = cgpanRenew.getString(4);
				//// System.out.println("error:"+error);
				cgpanRenew.close();
				cgpanRenew = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "generateRenewCgpan",
						"generateRenewCgpan" + error);

				throw new DatabaseException(error);

			} else {

				renewCgpan = cgpanRenew.getString(3);

				cgpanRenew.close();
				cgpanRenew = null;

				Log.log(Log.DEBUG, "ApplicationDAO", "generateRenewCgpan",
						"generated cgpan" + renewCgpan);

			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "generateRenewCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "generateRenewCgpan", "Exited");

		return renewCgpan;
	}

	/**
	 * This method checks whether the entered Cgpan for renewal is the latest
	 * one or not
	 * 
	 * @param cgpan
	 * @throws DatabaseException
	 */
	public String checkRenewCgpan(String cgpan) throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		String stringVal = "";

		try {
			CallableStatement checkCgpan = connection
					.prepareCall("{?=call funcCheckRenewal(?,?,?)}");

			checkCgpan.registerOutParameter(1, Types.INTEGER);
			checkCgpan.registerOutParameter(4, Types.VARCHAR);
			checkCgpan.registerOutParameter(3, Types.VARCHAR);

			checkCgpan.setString(2, cgpan);

			checkCgpan.execute();

			int functionReturnValue = checkCgpan.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "checkCgpan",
					"checkrenewCgpan:" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = checkCgpan.getString(4);

				checkCgpan.close();
				checkCgpan = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "checkRenewCgpan",
						"checkCgpan" + error);

				throw new DatabaseException(error);

			} else {

				stringVal = checkCgpan.getString(3);
				checkCgpan.close();
				checkCgpan = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "checkRenewCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "checkRenewCgpan", "Exited");

		return stringVal;
	}

	/**
	 * This method returns all the SSi Reference Numbers for the member ID
	 * entered
	 * 
	 * @param memberId
	 * @return
	 * @throws DatabaseException
	 */
	public ArrayList getSsiRefNosForMcgf(String memberId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getSsiRefNosForMcgf", "Entered");

		Connection connection = DBConnection.getConnection(false);

		ArrayList ssiRefNosList = new ArrayList();
		ArrayList bidList = new ArrayList();
		ArrayList ssiRefNumberList = new ArrayList();

		try {

			CallableStatement ssiRefNoList = connection
					.prepareCall("{?=call packGetSsiRefNoForMcgf.funcGetSsiRefNoForMcgf(?,?,?,?)}");

			ssiRefNoList.registerOutParameter(1, Types.INTEGER);
			ssiRefNoList.registerOutParameter(3, Constants.CURSOR); // retrieving
																	// the ssi
																	// ref nos
																	// list
			ssiRefNoList.registerOutParameter(4, Constants.CURSOR);
			ssiRefNoList.registerOutParameter(5, Types.VARCHAR);
			ssiRefNoList.setString(2, memberId);

			ssiRefNoList.execute();

			int functionReturnValue = ssiRefNoList.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getSsiRefNosForMcgf",
					"Get Ssi ref nos Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = ssiRefNoList.getString(5);

				ssiRefNoList.close();
				ssiRefNoList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet ssiRefNoResults = (ResultSet) ssiRefNoList
						.getObject(3);
				ResultSet ssiRefNoTempResults = (ResultSet) ssiRefNoList
						.getObject(4);

				while (ssiRefNoResults.next()) {

					String ssiRefNumber = ssiRefNoResults.getString(1);
					String ssiName = ssiRefNoResults.getString(2);
					String bid = ssiRefNoResults.getString(3);

					String ssiDtl = "";

					if (bid != null && !(bid.equals(""))) {
						Log.log(Log.DEBUG, "ApplicationDAO",
								"getSsiRefNosForMcgf", "bid:" + bid);

						ssiDtl = bid + "" + "(" + ssiName + ")";
						Log.log(Log.DEBUG, "ApplicationDAO",
								"getSsiRefNosForMcgf", "ssi dtl:" + ssiDtl);
					} else {

						ssiDtl = ssiRefNumber + "" + "(" + ssiName + ")";
					}
					ssiRefNosList.add(ssiDtl);

				}

				while (ssiRefNoTempResults.next()) {

					String ssiRefNumber = ssiRefNoTempResults.getString(1);
					String ssiName = ssiRefNoTempResults.getString(2);
					String bid = ssiRefNoTempResults.getString(3);

					String ssiDtl = "";

					if (bid != null && !(bid.equals(""))) {
						Log.log(Log.DEBUG, "ApplicationDAO",
								"getSsiRefNosForMcgf", "bid:" + bid);

						ssiDtl = bid + "" + "(" + ssiName + ")";
						Log.log(Log.DEBUG, "ApplicationDAO",
								"getSsiRefNosForMcgf", "ssi dtl:" + ssiDtl);
					} else {

						ssiDtl = ssiRefNumber + "" + "(" + ssiName + ")";
					}
					ssiRefNosList.add(ssiDtl);

				}

				ssiRefNoResults.close();
				ssiRefNoResults = null;
				ssiRefNoList.close();
				ssiRefNoList = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getSsiRefNosForMcgf",
					sqlException.getMessage());
			Log.logException(sqlException);
			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getSsiRefNosForMcgf", "Exited");

		return ssiRefNosList;
	}

	/**
	 * This methods returns all the cgpans in the database
	 * 
	 * @throws DatabaseException
	 */

	public ArrayList getAllCgpans() throws DatabaseException {

		Log.log(Log.INFO, "ApplicationProcessor", "getAllCgpans", "entered");
		Connection connection = DBConnection.getConnection(false);

		ArrayList cgpanList = new ArrayList();

		try {
			CallableStatement allCgpans = connection
					.prepareCall("{?=call packGetAllCgpans.funcGetAllCgpans(?,?)}");

			allCgpans.registerOutParameter(1, Types.INTEGER);
			allCgpans.registerOutParameter(2, Constants.CURSOR);
			allCgpans.registerOutParameter(3, Types.VARCHAR);

			allCgpans.execute();

			int functionReturnValue = allCgpans.getInt(1);

			if (functionReturnValue == 1) {

				String error = allCgpans.getString(3);

				allCgpans.close();
				allCgpans = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {
				ResultSet allCgpansResult = (ResultSet) allCgpans.getObject(2);

				while (allCgpansResult.next()) {

					String cgpan = allCgpansResult.getString(1);

					cgpanList.add(cgpan);
				}
				allCgpansResult.close();
				allCgpansResult = null;
				allCgpans.close();
				allCgpans = null;

			}
			connection.commit();

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getAllCgpans",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAllCgpans",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationProcessor", "getAllCgpans", "Exited");
		return cgpanList;

	}

	/**
	 * This methods returns all the cgpans in the database
	 * 
	 * @throws DatabaseException
	 */

	public ArrayList getAllBids() throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		ArrayList bidList = new ArrayList();

		try {
			CallableStatement allBids = connection
					.prepareCall("{?=call packGetAllBids.funcGetAllBids(?,?)}");

			allBids.registerOutParameter(1, Types.INTEGER);
			allBids.registerOutParameter(2, Constants.CURSOR);
			allBids.registerOutParameter(3, Types.VARCHAR);

			allBids.execute();

			int functionReturnValue = allBids.getInt(1);

			if (functionReturnValue == 1) {

				String error = allBids.getString(3);
				allBids.close();
				allBids = null;

				connection.rollback();

				throw new DatabaseException(error);

			} else {
				ResultSet allBidsResult = (ResultSet) allBids.getObject(2);

				while (allBidsResult.next()) {

					String bid = allBidsResult.getString(1);

					bidList.add(bid);
				}
				allBidsResult.close();
				allBidsResult = null;
				allBids.close();
				allBids = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getAllBids",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAllBids",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return bidList;

	}

	/**
	 * This method updates the pending and rejected Status of the applications
	 * 
	 * @throws DatabaseException
	 */

	public void updatePendingRejectedStatus(Application application,
			String userId) throws DatabaseException {
		// Log.log(Log.INFO,"ApplicationDAO","updatePendingRejectedStatus","Entered");
		Connection connection = DBConnection.getConnection(false);

		try {
			CallableStatement pendingRejectUpdate = connection
					.prepareCall("{?=call funcUpdatePendingRejectStatus(?,?,?,?,?)}");

			pendingRejectUpdate.registerOutParameter(1, Types.INTEGER);
			pendingRejectUpdate.registerOutParameter(6, Types.VARCHAR);

			pendingRejectUpdate.setString(2, application.getAppRefNo());
			// Log.log(Log.INFO,"ApplicationDAO","updatePendingRejectedStatus","app ref no:"
			// + application.getAppRefNo());
			pendingRejectUpdate.setString(3, application.getStatus());
			// Log.log(Log.INFO,"ApplicationDAO","updatePendingRejectedStatus","status:"
			// + application.getStatus());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {
				pendingRejectUpdate.setString(4, application.getRemarks());
			} else {

				pendingRejectUpdate.setString(4, null);
			}
			// Log.log(Log.INFO,"ApplicationDAO","updatePendingRejectedStatus","remarks:"
			// + application.getRemarks());

			pendingRejectUpdate.setString(5, userId);
			// Log.log(Log.DEBUG,"ApplicationDAO","updatePendingRejectedStatus","user id:"
			// + userId);

			pendingRejectUpdate.execute();

			int functionReturnValue = pendingRejectUpdate.getInt(1);
			// Log.log(Log.DEBUG,"ApplicationDAO","updatePendingRejectedStatus","update general status:"
			// + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = pendingRejectUpdate.getString(6);

				pendingRejectUpdate.close();
				pendingRejectUpdate = null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			pendingRejectUpdate.close();
			pendingRejectUpdate = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updatePendingRejectedStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO",
						"updatePendingRejectedStatus", ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		// Log.log(Log.INFO,"ApplicationDAO","updatePendingRejectedStatus","Exited");

	}

	/**
	 * This method updates the status of the enhancement Applications
	 */
	public void updateEnhanceAppStatus(Application application, String userId)
			throws DatabaseException {

		Connection connection = DBConnection.getConnection(false);

		java.util.Date utilDate = new java.util.Date();

		try {
			connection.commit();

			CallableStatement updateEnhanceStatus = connection
					.prepareCall("{?=call funcUpdateEnhanceStatus(?,?,?,?,?,?,?)}");

			updateEnhanceStatus.registerOutParameter(1, Types.INTEGER);
			updateEnhanceStatus.registerOutParameter(8, Types.VARCHAR);

			updateEnhanceStatus.setString(2, application.getAppRefNo());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Application app ref no :" + application.getAppRefNo());

			updateEnhanceStatus.setString(3, userId);
			if (application.getApprovedAmount() != 0) {
				updateEnhanceStatus.setDouble(4,
						application.getApprovedAmount());

			} else {

				updateEnhanceStatus.setNull(4, java.sql.Types.DOUBLE);
			}
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Application Amount :" + application.getApprovedAmount());
			updateEnhanceStatus.setDate(5,
					new java.sql.Date(utilDate.getTime()));
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Application Status :" + application.getStatus());
			updateEnhanceStatus.setString(6, application.getStatus());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Application Renmarks:" + application.getRemarks());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {

				updateEnhanceStatus.setString(7, application.getRemarks());
			} else {

				updateEnhanceStatus.setString(7, null);
			}

			try {
				updateEnhanceStatus.execute();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			int functionReturnValue = updateEnhanceStatus.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Get update App Status Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateEnhanceStatus.getString(8);

				updateEnhanceStatus.close();
				updateEnhanceStatus = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updateEnhanceStatus.close();
			updateEnhanceStatus = null;

			connection.commit();
			connection.close();
			connection = null;
		} catch (Exception sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "updateEnhanceAppStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (Exception ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateEnhanceAppStatus",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			connection = null;
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateApplicationStatus", "Exited");

	}

	/**
	 * This method updates the rejected status of the enhancement Applications
	 */
	public void updateRejectStatus(Application application, String userId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateRejectStatus", "Entered");
		Connection connection = DBConnection.getConnection(false);

		try {

			connection.commit();

			CallableStatement updateRejectStatus = connection
					.prepareCall("{?=call funcUpdateRejectStatus(?,?,?,?,?)}");

			updateRejectStatus.registerOutParameter(1, Types.INTEGER);
			updateRejectStatus.registerOutParameter(6, Types.VARCHAR);

			Log.log(Log.DEBUG, "ApplicationDAO", "updateRejectStatus",
					"Application app ref no :" + application.getAppRefNo());
			updateRejectStatus.setString(2, application.getAppRefNo());

			Log.log(Log.DEBUG, "ApplicationDAO", "updateRejectStatus",
					"Application Status :" + application.getStatus());
			updateRejectStatus.setString(3, application.getStatus());
			Log.log(Log.DEBUG, "ApplicationDAO", "updateRejectStatus",
					"Application Renmarks:" + application.getRemarks());
			if (application.getRemarks() != null
					&& !(application.getRemarks().equals(""))) {

				updateRejectStatus.setString(4, application.getRemarks());
			} else {

				updateRejectStatus.setString(4, null);
			}
			updateRejectStatus.setString(5, userId);

			try {
				updateRejectStatus.execute();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			int functionReturnValue = updateRejectStatus.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateEnhanceAppStatus",
					"Get update App Status Result :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateRejectStatus.getString(6);

				updateRejectStatus.close();
				updateRejectStatus = null;

				connection.rollback();

				throw new DatabaseException(error);
			}

			updateRejectStatus.close();
			updateRejectStatus = null;

			connection.commit();
			connection.close();
			connection = null;
		} catch (Exception sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "updateEnhanceAppStatus",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (Exception ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateEnhanceAppStatus",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			connection = null;
		}
		Log.log(Log.INFO, "ApplicationDAO", "updateApplicationStatus", "Exited");

	}

	/********************* Methods called from getAppRefNos *************************/

	/**
	 * This method is called when Borrower name and mliId are entered
	 */
	public ArrayList getDtlForBorMem(String borrowerName, String bankId,
			String zoneId, String branchId) throws DatabaseException,
			NoApplicationFoundException {
		Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem", "Entered");
		Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
				"borrower name :" + borrowerName);
		Connection connection = DBConnection.getConnection(false);
		ArrayList appRefNos = new ArrayList();
		ArrayList cgpanList = new ArrayList();
		ArrayList cgpanAppRefNo = new ArrayList();
		try {
			CallableStatement dtlForBorMem = connection
					.prepareCall("{?=call packGetAppRefForModification.funcGetDtlforBorMem(?,?,?,?,?,?,?,?)}");

			dtlForBorMem.registerOutParameter(1, Types.INTEGER);
			dtlForBorMem.registerOutParameter(6, Constants.CURSOR);
			dtlForBorMem.registerOutParameter(7, Constants.CURSOR);
			dtlForBorMem.registerOutParameter(8, Constants.CURSOR);
			dtlForBorMem.registerOutParameter(9, Types.VARCHAR);

			dtlForBorMem.setString(2, borrowerName);
			dtlForBorMem.setString(3, bankId);
			dtlForBorMem.setString(4, zoneId);
			dtlForBorMem.setString(5, branchId);

			dtlForBorMem.execute();

			int functionReturnValue = dtlForBorMem.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
					"return value :" + functionReturnValue);

			if (functionReturnValue == 1) {

				String error = dtlForBorMem.getString(9);

				dtlForBorMem.close();
				dtlForBorMem = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet appRefNosResult = (ResultSet) dtlForBorMem
						.getObject(6);
				Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
						"appRefNo size:" + appRefNosResult.getFetchSize());
				ResultSet appRefNosTempResult = (ResultSet) dtlForBorMem
						.getObject(7);
				ResultSet appRefCgpanTempResult = (ResultSet) dtlForBorMem
						.getObject(8);

				while (appRefNosResult.next()) {
					String appRefNo = appRefNosResult.getString(1);
					Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
							"appRefNo:" + appRefNo);
					String cgpan = appRefNosResult.getString(2);

					Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
							"cgpan:" + cgpan);

					if (cgpan == null || cgpan.equals("")) {
						cgpan = "";
						if (!appRefNos.contains(appRefNo)) {
							appRefNos.add(appRefNo);
						}

					} else if (cgpan != null && !(cgpan.equals(""))) {

						try {

							Application application = getPartApplication(null,
									cgpan, "");
							if (!application.getStatus().equals("EX")
									&& !application.getStatus().equals("RE")) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}

							/*
							 * cgpanList.add(cgpan); appRefNos.add(appRefNo);
							 */
						} catch (DatabaseException databaseException) {
							if (!(databaseException.getMessage()
									.equals("Application does not exist."))) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}
						}
					}
				}
				while (appRefNosTempResult.next()) {
					String appRefNo = appRefNosTempResult.getString(1);
					String cgpan = appRefNosTempResult.getString(2);

					if (cgpan == null || cgpan.equals("")) {
						cgpan = "";
						if (!appRefNos.contains(appRefNo)) {
							appRefNos.add(appRefNo);
						}

					} else if (cgpan != null && !(cgpan.equals(""))) {

						try {

							Application application = getPartApplication(null,
									cgpan, "");

							if (!application.getStatus().equals("EX")
									&& !application.getStatus().equals("RE")) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}

							// cgpanList.add(cgpan);
							// appRefNos.add(appRefNo);

						} catch (DatabaseException databaseException) {
							if (!(databaseException.getMessage()
									.equals("Application does not exist."))) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}
						}
					}
				}

				while (appRefCgpanTempResult.next()) {
					String appRefNo = appRefCgpanTempResult.getString(1);
					String cgpan = appRefCgpanTempResult.getString(2);

					if (cgpan == null || cgpan.equals("")) {
						cgpan = "";
						if (!appRefNos.contains(appRefNo)) {
							appRefNos.add(appRefNo);
						}

					} else if (cgpan != null && !(cgpan.equals(""))) {

						try {

							Application application = getPartApplication(null,
									cgpan, "");
							if (!application.getStatus().equals("EX")
									&& !application.getStatus().equals("RE")) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}

							/*
							 * cgpanList.add(cgpan); appRefNos.add(appRefNo);
							 */
						} catch (DatabaseException databaseException) {
							if (!(databaseException.getMessage()
									.equals("Application does not exist."))) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
								}
								if (!appRefNos.contains(appRefNo)) {
									appRefNos.add(appRefNo);
								}

							}
						}
					}
				}

				appRefNosResult.close();
				appRefNosResult = null;
				appRefNosTempResult.close();
				appRefNosTempResult = null;
				appRefCgpanTempResult.close();
				appRefCgpanTempResult = null;

				dtlForBorMem.close();
				dtlForBorMem = null;

			}

			connection.commit();
		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		cgpanAppRefNo.add(appRefNos);
		cgpanAppRefNo.add(cgpanList);

		Log.log(Log.INFO, "ApplicationDAO", "getDtlForBorMem", "Exited");

		return cgpanAppRefNo;

	}

	/**
	 * This method is called when BID and MLI id are entered
	 */
	public ArrayList getDtlForBIDMem(String borrowerId, String bankId,
			String zoneId, String branchId) throws DatabaseException,
			NoApplicationFoundException {
		Connection connection = DBConnection.getConnection(false);
		ArrayList appRefNos = new ArrayList();
		ArrayList cgpanList = new ArrayList();
		ArrayList cgpanAppRefNo = new ArrayList();
		try {

			CallableStatement dtlForBIDMem = connection
					.prepareCall("{?=call packGetAppRefForModification.funcGetDtlforBIDMem(?,?,?,?,?,?,?,?)}");

			dtlForBIDMem.registerOutParameter(1, Types.INTEGER);
			dtlForBIDMem.registerOutParameter(6, Constants.CURSOR);
			dtlForBIDMem.registerOutParameter(7, Constants.CURSOR);
			dtlForBIDMem.registerOutParameter(8, Constants.CURSOR);
			dtlForBIDMem.registerOutParameter(9, Types.VARCHAR);

			dtlForBIDMem.setString(2, borrowerId);
			dtlForBIDMem.setString(3, bankId);
			dtlForBIDMem.setString(4, zoneId);
			dtlForBIDMem.setString(5, branchId);

			dtlForBIDMem.execute();

			int functionReturnValue = dtlForBIDMem.getInt(1);

			if (functionReturnValue == 1) {

				String error = dtlForBIDMem.getString(9);

				dtlForBIDMem.close();
				dtlForBIDMem = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet appRefNosResult = (ResultSet) dtlForBIDMem
						.getObject(6);
				ResultSet appRefNosTempResult = (ResultSet) dtlForBIDMem
						.getObject(7);
				ResultSet appRefCgpanTempResult = (ResultSet) dtlForBIDMem
						.getObject(8);

				while (appRefNosResult.next()) {
					String appRefNo = appRefNosResult.getString(1);

					String cgpan = appRefNosResult.getString(2);
					if (cgpan == null || cgpan.equals("")) {
						cgpan = "";
						appRefNos.add(appRefNo);
					}
					if (cgpan != null && !(cgpan.equals(""))) {
						try {

							Application application = getPartApplication(null,
									cgpan, "");

							if (!application.getStatus().equals("EX")) {
								cgpanList.add(cgpan);
								appRefNos.add(appRefNo);

							}

						} catch (DatabaseException databaseException) {
							if (!(databaseException.getMessage()
									.equals("Application does not exist."))) {
								cgpanList.add(cgpan);
								appRefNos.add(appRefNo);

							}
						}
					}
				}

				/*
				 * while(appRefNosTempResult.next()){ String
				 * appRefNo=appRefNosTempResult.getString(1);
				 * 
				 * 
				 * String cgpan=appRefNosTempResult.getString(2); if
				 * (cgpan==null || cgpan.equals("")) { cgpan="";
				 * appRefNos.add(appRefNo); } if(cgpan!=null &&
				 * !(cgpan.equals(""))) { try{
				 * 
				 * Application application=getPartApplication(null,cgpan,"");
				 * if(!application.getStatus().equals("EX")) {
				 * cgpanList.add(cgpan); appRefNos.add(appRefNo);
				 * 
				 * }
				 * 
				 * cgpanList.add(cgpan); appRefNos.add(appRefNo);
				 * 
				 * }catch (DatabaseException databaseException) {
				 * if(!(databaseException
				 * .getMessage().equals("Application does not exist."))) {
				 * cgpanList.add(cgpan); appRefNos.add(appRefNo);
				 * 
				 * } } }
				 * 
				 * }
				 */
				while (appRefCgpanTempResult.next()) {
					String appRefNo = appRefCgpanTempResult.getString(1);

					String cgpan = appRefCgpanTempResult.getString(2);
					if (cgpan == null || cgpan.equals("")) {
						cgpan = "";
						if (!appRefNos.contains(appRefNo)) {
							appRefNos.add(appRefNo);
						}

					}
					if (cgpan != null && !(cgpan.equals(""))) {
						try {

							Application application = getPartApplication(null,
									cgpan, "");

							if (!application.getStatus().equals("EX")) {
								if (!cgpanList.contains(cgpan)) {
									cgpanList.add(cgpan);
									appRefNos.add(appRefNo);

								}

							}

						} catch (DatabaseException databaseException) {
							if (!(databaseException.getMessage()
									.equals("Application does not exist."))) {
								cgpanList.add(cgpan);
								appRefNos.add(appRefNo);

							}
						}
					}
				}

				appRefNosResult.close();
				appRefNosResult = null;
				appRefCgpanTempResult.close();
				appRefCgpanTempResult = null;
				/*
				 * appRefNosTempResult.close(); appRefNosTempResult=null;
				 */
				dtlForBIDMem.close();
				dtlForBIDMem = null;

			}

			connection.commit();

		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getDtlForBIDMem",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		cgpanAppRefNo.add(appRefNos);
		cgpanAppRefNo.add(cgpanList);

		return cgpanAppRefNo;
	}

	/**
	 * This method is called when MLI id and app ref no are entered
	 */

	public Application getAppForMliRef(String bankId, String zoneId,
			String branchId, String appRefNo) throws DatabaseException {
		Connection connection = DBConnection.getConnection();

		Application application = new Application();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		User user = new User();

		try {
			CallableStatement appForMliRef = connection
					.prepareCall("{?=call funcGetAppDetailforMLIAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			appForMliRef.setString(2, appRefNo);
			appForMliRef.setString(3, bankId);
			appForMliRef.setString(4, zoneId);
			appForMliRef.setString(5, branchId);

			appForMliRef.registerOutParameter(1, Types.INTEGER);
			appForMliRef.registerOutParameter(28, Types.VARCHAR);

			appForMliRef.registerOutParameter(6, Types.VARCHAR); // App Ref No
			appForMliRef.registerOutParameter(7, Types.INTEGER); // SSI Ref No
			appForMliRef.registerOutParameter(8, Types.VARCHAR); // CGPAN
			appForMliRef.registerOutParameter(9, Types.VARCHAR); // Scheme Name
			appForMliRef.registerOutParameter(10, Types.VARCHAR); // Bank ID
			appForMliRef.registerOutParameter(11, Types.VARCHAR); // Zone Id
			appForMliRef.registerOutParameter(12, Types.VARCHAR); // Branch Id
			appForMliRef.registerOutParameter(13, Types.VARCHAR); // MLI Branch
																	// name
			appForMliRef.registerOutParameter(14, Types.VARCHAR); // Branch Code
			appForMliRef.registerOutParameter(15, Types.VARCHAR); // App Bank
																	// Ref No
			appForMliRef.registerOutParameter(16, Types.VARCHAR); // Composite
																	// Loan
			appForMliRef.registerOutParameter(17, Types.VARCHAR); // User Id
			appForMliRef.registerOutParameter(18, Types.VARCHAR); // Loan Type
			appForMliRef.registerOutParameter(19, Types.VARCHAR); // Collateral
																	// Security
			appForMliRef.registerOutParameter(20, Types.VARCHAR); // Third Party
																	// Gurantee
			appForMliRef.registerOutParameter(21, Types.VARCHAR); // Subsidy
																	// Scheme
																	// Name
			appForMliRef.registerOutParameter(22, Types.VARCHAR); // Rehabilitation
			appForMliRef.registerOutParameter(23, Types.DOUBLE); // Project
																	// Outlay
			appForMliRef.registerOutParameter(24, Types.VARCHAR); // App CGPAN
																	// Ref No
			appForMliRef.registerOutParameter(25, Types.VARCHAR); // Remarks
			appForMliRef.registerOutParameter(26, Types.DATE); // Submitted Date
			appForMliRef.registerOutParameter(27, Types.VARCHAR); // Status

			appForMliRef.execute();

			int appForMliRefValue = appForMliRef.getInt(1);

			if (appForMliRefValue == 1) {

				String error = appForMliRef.getString(28);

				appForMliRef.close();
				appForMliRef = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				application.setAppRefNo(appForMliRef.getString(6)); // app ref
																	// no

				application.setCgpan(appForMliRef.getString(7)); // CGPAN

				application.setScheme(appForMliRef.getString(9)); // Scheme Name

				application.setBankId(appForMliRef.getString(10).trim()); // Bank
																			// Id

				application.setZoneId(appForMliRef.getString(11).trim()); // Zone
																			// Id

				application.setBranchId(appForMliRef.getString(12).trim()); // Branch
																			// Id

				application.setMliBranchName(appForMliRef.getString(13)); // Branch
																			// Name

				application.setMliBranchCode(appForMliRef.getString(14));// Branch
																			// Code

				application.setMliRefNo(appForMliRef.getString(15)); // MliRefNo

				application.setCompositeLoan(appForMliRef.getString(16).trim()); // CompositeLoan

				user.setUserId(appForMliRef.getString(17)); // userId

				application.setLoanType(appForMliRef.getString(18).trim()); // loan
																			// type

				projectOutlayDetails.setCollateralSecurityTaken(appForMliRef
						.getString(19).trim()); // Collateral Security

				projectOutlayDetails.setThirdPartyGuaranteeTaken(appForMliRef
						.getString(20).trim()); // Guarantee Taken

				projectOutlayDetails.setSubsidyName(appForMliRef.getString(21)); // Subsidy
																					// Name

				application
						.setRehabilitation(appForMliRef.getString(22).trim()); // rehabilitation

				projectOutlayDetails.setProjectOutlay(appForMliRef
						.getDouble(23)); // project Outlay

				application.setRemarks(appForMliRef.getString(25)); // remarks

				application.setStatus(appForMliRef.getString(27).trim()); // status

				application.setProjectOutlayDetails(projectOutlayDetails);

				appForMliRef.close();
				appForMliRef = null;

			}

			connection.commit();
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getAppForMliRef",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAppForMliRef",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return application;

	}

	/**
	 * This method is called when app ref no is entered
	 */

	/*public Application getAppForAppRef(String mliId, String appRefNo)
			throws DatabaseException {
		// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Entered. Memory : "+Runtime.getRuntime().freeMemory());
		//// System.out.println("PATH in ApplicationDAO getAppForAppRef(String mliId,String appRefNo) mliID = "+mliId+" appRefNo = "+appRefNo);
		Connection connection = DBConnection.getConnection(false);

		// Log.log(Log.DEBUG,"ApplicationDAO","getAppForAppRef","connection in Application DAO in getAppForAppRef"+connection);
		//System.out.println("In getAppForAppRef mliId " + mliId + " appRefNo"
			//	+ appRefNo);
		Application application = new Application();
		ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
		// User user=new User();

		try {
			CallableStatement appForAppRef = connection.prepareCall
			// ("{?=call funcGetApplicationDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//used
			// for testdb
					("{?=call funcGetApplicationDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");// used
																																				// for
																																				// maindb

			appForAppRef.registerOutParameter(1, Types.INTEGER);
			appForAppRef.setString(2, appRefNo);
			appForAppRef.setString(3, mliId);
			appForAppRef.registerOutParameter(4, Types.VARCHAR); // App Ref No
			appForAppRef.registerOutParameter(5, Types.INTEGER); // SSI Ref No
			appForAppRef.registerOutParameter(6, Types.VARCHAR); // CGPAN
			appForAppRef.registerOutParameter(7, Types.VARCHAR); // Scheme Name
			appForAppRef.registerOutParameter(8, Types.VARCHAR); // Bank ID
			appForAppRef.registerOutParameter(9, Types.VARCHAR); // Zone Id
			appForAppRef.registerOutParameter(10, Types.VARCHAR); // Branch Id
			appForAppRef.registerOutParameter(11, Types.VARCHAR); // MLI Branch
																	// name
			appForAppRef.registerOutParameter(12, Types.VARCHAR); // Branch Code
			appForAppRef.registerOutParameter(13, Types.VARCHAR); // App Bank
																	// Ref No
			appForAppRef.registerOutParameter(14, Types.VARCHAR); // Composite
																	// Loan
			appForAppRef.registerOutParameter(15, Types.VARCHAR); // User Id
			appForAppRef.registerOutParameter(16, Types.VARCHAR); // Loan Type
			appForAppRef.registerOutParameter(17, Types.VARCHAR); // Collateral
																	// Security
			appForAppRef.registerOutParameter(18, Types.VARCHAR); // Third Party
																	// Gurantee
			appForAppRef.registerOutParameter(19, Types.VARCHAR); // Subsidy
																	// Scheme
																	// Name
			appForAppRef.registerOutParameter(20, Types.VARCHAR); // Rehabilitation
			appForAppRef.registerOutParameter(21, Types.DOUBLE); // Project
																	// Outlay
			appForAppRef.registerOutParameter(22, Types.VARCHAR); // App CGPAN
																	// Ref No
			appForAppRef.registerOutParameter(23, Types.VARCHAR); // Remarks
			appForAppRef.registerOutParameter(24, Types.DATE); // Submitted Date
			appForAppRef.registerOutParameter(25, Types.VARCHAR); // Status
			appForAppRef.registerOutParameter(26, Types.VARCHAR); // Sub scheme
																	// name
			appForAppRef.registerOutParameter(27, Types.DOUBLE); // Approved
																	// Amount
			appForAppRef.registerOutParameter(28, Types.DOUBLE); // ReApproved
																	// Amount
			appForAppRef.registerOutParameter(29, Types.VARCHAR); // ReApproved
																	// Amount
			// added by sukumar@path for displaying Handicraft details
			appForAppRef.registerOutParameter(30, Types.VARCHAR);// Handicraft
																	// Flag
			appForAppRef.registerOutParameter(31, Types.VARCHAR);// DC
																	// Handicraft
																	// Flag
			appForAppRef.registerOutParameter(32, Types.VARCHAR);
			appForAppRef.registerOutParameter(33, Types.DATE);// DC Handicraft
																// icardDate
			// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Before Executing");
			appForAppRef.registerOutParameter(34, Types.VARCHAR);// handloom
			appForAppRef.registerOutParameter(35, Types.VARCHAR);// weaver
			appForAppRef.registerOutParameter(36, Types.VARCHAR);// hanloomcheck

			appForAppRef.registerOutParameter(37, Types.VARCHAR);// mseType
			appForAppRef.registerOutParameter(38, Types.DATE);// sanction date
			appForAppRef.registerOutParameter(39, Types.DATE);
			appForAppRef.registerOutParameter(40, Types.VARCHAR);// errorcode
			// appForAppRef.registerOutParameter(38,Types.VARCHAR);//errorcode
			appForAppRef.execute();

			int appForAppRefValue = appForAppRef.getInt(1);

			if (appForAppRefValue == Constants.FUNCTION_FAILURE) {

				String error = appForAppRef.getString(40);
				// String error = appForAppRef.getString(38);
				appForAppRef.close();
				appForAppRef = null;

				connection.rollback();

				// Log.log(Log.ERROR,"ApplicationDAO","getAppForAppRef","Application Detail exception :"
				// + error);

				throw new DatabaseException(error);
			} else {
				application.setAppRefNo(appForAppRef.getString(4)); // app ref
																	// no
				application.setCgpan(appForAppRef.getString(6)); // app ref no
				application.setScheme(appForAppRef.getString(7)); // Scheme Name
				String mliID = appForAppRef.getString(8)
						+ appForAppRef.getString(9)
						+ appForAppRef.getString(10);
				application.setMliID(mliID);
				application.setMliBranchName(appForAppRef.getString(11)); // Branch
																			// Name
				application.setMliBranchCode(appForAppRef.getString(12));// Branch
																			// Code
				application.setMliRefNo(appForAppRef.getString(13)); // MliRefNo
				application.setCompositeLoan(appForAppRef.getString(14).trim()); // CompositeLoan
				// user.setUserId(appForAppRef.getString(15)); //userId
				application.setLoanType(appForAppRef.getString(16).trim()); // loan
																			// type
				projectOutlayDetails.setCollateralSecurityTaken(appForAppRef
						.getString(17).trim()); // Collateral Security
				projectOutlayDetails.setThirdPartyGuaranteeTaken(appForAppRef
						.getString(18).trim()); // Guarantee Taken
				projectOutlayDetails.setSubsidyName(appForAppRef.getString(19)); // Subsidy
																					// Name
				application
						.setRehabilitation(appForAppRef.getString(20).trim()); // rehabilitation
				projectOutlayDetails.setProjectOutlay(appForAppRef
						.getDouble(21)); // project Outlay
				application.setCgpanReference(appForAppRef.getString(22)); // app
																			// Cgpan
																			// Ref
																			// No
				application.setRemarks(appForAppRef.getString(23)); // remarks
				application.setSubmittedDate(appForAppRef.getDate(24)); // status
				application.setStatus(appForAppRef.getString(25)); // status
				application.setSubSchemeName(appForAppRef.getString(26));
				application.setApprovedAmount(appForAppRef.getDouble(27));
				application.setReapprovedAmount(appForAppRef.getDouble(28));
				application.setReapprovalRemarks(appForAppRef.getString(29));
				application.setHandiCrafts(appForAppRef.getString(30));
				application.setHandiCraftsStatus(appForAppRef.getString(30));
				application.setDcHandicraftsStatus(appForAppRef.getString(31));
				application.setDcHandicrafts(appForAppRef.getString(31));
				application.setIcardNo(appForAppRef.getString(32));
				application.setIcardIssueDate(appForAppRef.getDate(33));
				application.setProjectOutlayDetails(projectOutlayDetails);
				application.setDcHandlooms(appForAppRef.getString(34));
				application.setDcHandloomsStatus(appForAppRef.getString(34));
				application.setWeaverCreditScheme(appForAppRef.getString(35));
				application.setHandloomchk(appForAppRef.getString(36));

				application.setMsE(appForAppRef.getString(37)); // mse
				application.setSanctionedDate(appForAppRef.getDate(38));
				//System.out.println("getAppForAppRef "
					//	+ appForAppRef.getDate(38));

				appForAppRef.close();
				appForAppRef = null;
			}

			connection.commit();
		} catch (SQLException sqlException) {
			Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
					sqlException.getMessage());
			Log.logException(sqlException);
			sqlException.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		projectOutlayDetails = null;

		// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Exited. Memory : "+Runtime.getRuntime().freeMemory());

		return application;

	}*/
	
	public Application getAppForAppRef(String mliId, String appRefNo)
	throws DatabaseException {
// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Entered. Memory : "+Runtime.getRuntime().freeMemory());
//// System.out.println("PATH in ApplicationDAO getAppForAppRef(String mliId,String appRefNo) mliID = "+mliId+" appRefNo = "+appRefNo);
Connection connection = DBConnection.getConnection(false);

// Log.log(Log.DEBUG,"ApplicationDAO","getAppForAppRef","connection in Application DAO in getAppForAppRef"+connection);
//System.out.println("In getAppForAppRef mliId " + mliId + " appRefNo"
	//	+ appRefNo);
Application application = new Application();
ProjectOutlayDetails projectOutlayDetails = new ProjectOutlayDetails();
// User user=new User();

try {
	CallableStatement appForAppRef = connection.prepareCall
	// ("{?=call funcGetApplicationDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//used
	// for testdb
		//	("{?=call funcGetApplicationDtlforAppRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");// used
	// ("{?=call FUNCGETAPPLICATIONDTLFOR_R(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");    // 71 poutTAX_PBDIT
	 ("{?=call FUNCGETAPPLICATIONDTLFOR_R(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
	 		+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");    // 84 

	appForAppRef.registerOutParameter(1, Types.INTEGER);
	appForAppRef.setString(2, appRefNo);
	appForAppRef.setString(3, mliId);
	appForAppRef.registerOutParameter(4, Types.VARCHAR); // App Ref No
	appForAppRef.registerOutParameter(5, Types.INTEGER); // SSI Ref No
	appForAppRef.registerOutParameter(6, Types.VARCHAR); // CGPAN
	appForAppRef.registerOutParameter(7, Types.VARCHAR); // Scheme Name
	appForAppRef.registerOutParameter(8, Types.VARCHAR); // Bank ID
	appForAppRef.registerOutParameter(9, Types.VARCHAR); // Zone Id
	appForAppRef.registerOutParameter(10, Types.VARCHAR); // Branch Id
	appForAppRef.registerOutParameter(11, Types.VARCHAR); // MLI Branch
															// name
	appForAppRef.registerOutParameter(12, Types.VARCHAR); // Branch Code
	appForAppRef.registerOutParameter(13, Types.VARCHAR); // App Bank Ref No
	appForAppRef.registerOutParameter(14, Types.VARCHAR); // Composite Loan
	appForAppRef.registerOutParameter(15, Types.VARCHAR); // User Id
	appForAppRef.registerOutParameter(16, Types.VARCHAR); // Loan Type
	appForAppRef.registerOutParameter(17, Types.VARCHAR); // Collateral Security
	appForAppRef.registerOutParameter(18, Types.VARCHAR); // Third Party Gurantee
	appForAppRef.registerOutParameter(19, Types.VARCHAR); // Subsidy Scheme
															// Name
	appForAppRef.registerOutParameter(20, Types.VARCHAR); // Rehabilitation
	appForAppRef.registerOutParameter(21, Types.DOUBLE); // Project
															// Outlay
	appForAppRef.registerOutParameter(22, Types.VARCHAR); // App CGPAN
															// Ref No
	appForAppRef.registerOutParameter(23, Types.VARCHAR); // Remarks
	appForAppRef.registerOutParameter(24, Types.DATE); // Submitted Date
	appForAppRef.registerOutParameter(25, Types.VARCHAR); // Status
	appForAppRef.registerOutParameter(26, Types.VARCHAR); // Sub scheme
															// name
	appForAppRef.registerOutParameter(27, Types.DOUBLE); // Approved
															// Amount
	appForAppRef.registerOutParameter(28, Types.DOUBLE); // ReApproved
															// Amount
	appForAppRef.registerOutParameter(29, Types.VARCHAR); // ReApproved Amount
	// added by sukumar@path for displaying Handicraft details
	appForAppRef.registerOutParameter(30, Types.VARCHAR);// Handicraft Flag
	appForAppRef.registerOutParameter(31, Types.VARCHAR);// DC Handicraft Flag
	appForAppRef.registerOutParameter(32, Types.VARCHAR);
	appForAppRef.registerOutParameter(33, Types.DATE);// DC Handicraft icardDate
	// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Before Executing");
	appForAppRef.registerOutParameter(34, Types.VARCHAR);// handloom
	appForAppRef.registerOutParameter(35, Types.VARCHAR);// weaver
	appForAppRef.registerOutParameter(36, Types.VARCHAR);// hanloomcheck
	appForAppRef.registerOutParameter(37, Types.VARCHAR);// mseType
	//System.out.println("mseType ");
	appForAppRef.registerOutParameter(38, Types.DATE);// sanction date
	appForAppRef.registerOutParameter(39, Types.DATE);
	System.out.println("before error exe");
	appForAppRef.registerOutParameter(40, Types.VARCHAR);// errorcode
// added for 2C record			
	appForAppRef.registerOutParameter(41, Types.VARCHAR);//STATE_CODE
	appForAppRef.registerOutParameter(42, Types.VARCHAR);  // gst			
	appForAppRef.registerOutParameter(43, Types.VARCHAR);  //HYBRIDSECURITY_FLAG
	appForAppRef.registerOutParameter(44, Types.INTEGER);  //IMMOVCOLLATERATLSECURITY_AMT			
	appForAppRef.registerOutParameter(45, Types.VARCHAR);  //PRODIR_CRILC_CIBIL_RBI_FLAG	
	appForAppRef.registerOutParameter(46, Types.INTEGER);  //CREDITBUREAU_SCOR_KEYPROM
	appForAppRef.registerOutParameter(47, Types.INTEGER);  //CREDITBUREAU_SCOR_PROM2
	appForAppRef.registerOutParameter(48, Types.INTEGER);  //CREDITBUREAU_SCOR_PROM3
	appForAppRef.registerOutParameter(49, Types.INTEGER);  //CREDITBUREAU_SCOR_PROM4
	appForAppRef.registerOutParameter(50, Types.INTEGER);  //CREDITBUREAU_SCOR_PROM5	
	appForAppRef.registerOutParameter(51, Types.VARCHAR);  //CREDIT_BUREAU_NAME1	
	appForAppRef.registerOutParameter(52, Types.VARCHAR);   //CREDIT_BUREAU_NAME2
	appForAppRef.registerOutParameter(53, Types.VARCHAR);  //CREDITBUREAU_SCOR_PROM3
	appForAppRef.registerOutParameter(54, Types.VARCHAR);  //CREDIT_BUREAU_NAME4
	appForAppRef.registerOutParameter(55, Types.VARCHAR);  //CREDIT_BUREAU_NAME5
	appForAppRef.registerOutParameter(56, Types.INTEGER);  //CIBIL_MSME_RANK_FIRM
	appForAppRef.registerOutParameter(57, Types.INTEGER);  //EXPERIAN_COMMERCIAL_SCORE
	appForAppRef.registerOutParameter(58, Types.FLOAT);   //PROM_BORROWER_NETWORTH
	appForAppRef.registerOutParameter(59, Types.INTEGER);//PROM_CONTRIBUTION
	appForAppRef.registerOutParameter(60, Types.VARCHAR);   //PAST1YEAR_PROM_GRUPASSOC_NPA
	appForAppRef.registerOutParameter(61, Types.INTEGER);  //PROM_BUSINESS_EXP_YEAR
	appForAppRef.registerOutParameter(62, Types.FLOAT);   //SALES_REVENUE
	appForAppRef.registerOutParameter(63, Types.FLOAT);  //TAX_PBDIT			
	System.out.println("after error exe");		
    appForAppRef.registerOutParameter(64, Types.FLOAT);  //TAX_CURRENT_PROVISION_AMT
	appForAppRef.registerOutParameter(65, Types.FLOAT); //TOTAL_CURRENT_ASSETS
	appForAppRef.registerOutParameter(66, Types.FLOAT);  //TOTAL_CURRENT_LIABILITY
	appForAppRef.registerOutParameter(67, Types.FLOAT); //TOTAL_TERM_LIABILITY
	appForAppRef.registerOutParameter(68, Types.FLOAT);  //EQUITY_CAPITAL
	appForAppRef.registerOutParameter(69, Types.FLOAT); //PREFERENCE_CAPITAL
	appForAppRef.registerOutParameter(70, Types.FLOAT); //RESERVES_SURPLUS
	appForAppRef.registerOutParameter(71, Types.FLOAT);//REPAYMENTS_DUE_NYEAR_AMT 
	
	appForAppRef.registerOutParameter(72, Types.FLOAT);//INTEREST_PAYMENT
	appForAppRef.registerOutParameter(73, Types.FLOAT);    //OPERATING_INCOME
	appForAppRef.registerOutParameter(74, Types.FLOAT);  //PROFIT_AFT_TAX
	appForAppRef.registerOutParameter(75, Types.FLOAT);   //NETWORTH
	appForAppRef.registerOutParameter(76, Types.INTEGER);  //DEBIT_EQT_RATIO_UNIT			
    appForAppRef.registerOutParameter(77, Types.INTEGER);  //DEBIT_SRV_COV_RATIO_TL
	appForAppRef.registerOutParameter(78, Types.INTEGER); //CURRENT_RATIO_WC
	appForAppRef.registerOutParameter(79, Types.INTEGER);  //setDebitEqtRatio
	appForAppRef.registerOutParameter(80, Types.INTEGER); //setDebitSrvCoverageRatio
	appForAppRef.registerOutParameter(81, Types.INTEGER);  //setCurrentRatios
	appForAppRef.registerOutParameter(82, Types.INTEGER); //setCreditBureauChiefPromScor
	appForAppRef.registerOutParameter(83, Types.FLOAT); // setTotalAssets
	appForAppRef.registerOutParameter(84, Types.VARCHAR);   //setExistGreenFldUnitType
						
	// appForAppRef.registerOutParameter(43, Types.VARCHAR); //CHIEF_PROMOTER_MOBILE
   // appForAppRef.registerOutParameter(44, Types.VARCHAR);  //APP_BANK_APP_REF_NO
  // appForAppRef.registerOutParameter(47, Types.VARCHAR);  //LOAN_UNSECURED_PORTION_AMT
 // appForAppRef.registerOutParameter(48, Types.VARCHAR);  //LOAN_UNSECURED_EXCLUDCGT_AMT
// End 2C
	appForAppRef.execute();
	int appForAppRefValue = appForAppRef.getInt(1);
	if (appForAppRefValue == Constants.FUNCTION_FAILURE) {
		String error = appForAppRef.getString(40);
	    appForAppRef.close();
		appForAppRef = null;
		connection.rollback();
		System.out.println("error>>>>>>>>>>>>>"+error);
		throw new DatabaseException(error);
	} else {
		application.setAppRefNo(appForAppRef.getString(4)); // app ref
		application.setCgpan(appForAppRef.getString(6)); // app ref no
		application.setScheme(appForAppRef.getString(7)); // Scheme Name
		String mliID = appForAppRef.getString(8) + appForAppRef.getString(9) + appForAppRef.getString(10);
		application.setMliID(mliID);
		application.setMliBranchName(appForAppRef.getString(11)); // Branch Name
		application.setMliBranchCode(appForAppRef.getString(12));// Branch Code
		application.setMliRefNo(appForAppRef.getString(13)); // MliRefNo
		application.setCompositeLoan(appForAppRef.getString(14).trim()); // CompositeLoan
		// user.setUserId(appForAppRef.getString(15)); //userId
		application.setLoanType(appForAppRef.getString(16).trim()); // loan type
		projectOutlayDetails.setCollateralSecurityTaken(appForAppRef.getString(17).trim()); // Collateral Security
		projectOutlayDetails.setThirdPartyGuaranteeTaken(appForAppRef.getString(18).trim()); // Guarantee Taken
		projectOutlayDetails.setSubsidyName(appForAppRef.getString(19)); // Subsidy Name
		application.setRehabilitation(appForAppRef.getString(20).trim()); // rehabilitation
		projectOutlayDetails.setProjectOutlay(appForAppRef.getDouble(21)); // project Outlay
		application.setCgpanReference(appForAppRef.getString(22)); // appCgpan Ref
		application.setRemarks(appForAppRef.getString(23)); // remarks
		application.setSubmittedDate(appForAppRef.getDate(24)); // status
		application.setStatus(appForAppRef.getString(25)); // status
		application.setSubSchemeName(appForAppRef.getString(26));
		application.setApprovedAmount(appForAppRef.getDouble(27));
		application.setReapprovedAmount(appForAppRef.getDouble(28));
		application.setReapprovalRemarks(appForAppRef.getString(29));
		application.setHandiCrafts(appForAppRef.getString(30));
		application.setHandiCraftsStatus(appForAppRef.getString(30));
		application.setDcHandicraftsStatus(appForAppRef.getString(31));
		application.setDcHandicrafts(appForAppRef.getString(31));
		application.setIcardNo(appForAppRef.getString(32));
		application.setIcardIssueDate(appForAppRef.getDate(33));
		application.setProjectOutlayDetails(projectOutlayDetails);
		application.setDcHandlooms(appForAppRef.getString(34));
		application.setDcHandloomsStatus(appForAppRef.getString(34));
		application.setWeaverCreditScheme(appForAppRef.getString(35));
		application.setHandloomchk(appForAppRef.getString(36));
		application.setMsE(appForAppRef.getString(37)); // mse
		application.setSanctionedDate(appForAppRef.getDate(38));
		// Added by DKR for Financial Record Capturing in Applicaiton Display
		     application.setGstState(appForAppRef.getString(41));
		     application.setGstNo(appForAppRef.getString(42));
		    // System.out.println(" application.setHybridSecurity>>"+ application.getHybridSecurity());
		     application.setHybridSecurity(appForAppRef.getString(43));
		    // System.out.println(" application.getHybridSecurity()>>"+ application.getHybridSecurity());
		     application.setImmovCollateratlSecurityAmt(Double.valueOf(appForAppRef.getInt(44)));//immovColSecurityAmt
		    // System.out.println(" application. application.setImmovCollaterat()>>"+ application.getImmovCollateratlSecurityAmt());
		     application.setPromDirDefaltFlg(appForAppRef.getString(45)); 	
		     application.setCredBureKeyPromScor(appForAppRef.getInt(46)); 
		     application.setCredBurePromScor2(appForAppRef.getInt(47)); 	
		     application.setCredBurePromScor3(appForAppRef.getInt(48));	
		     application.setCredBurePromScor4(appForAppRef.getInt(49)); 
		     application.setCredBurePromScor5(appForAppRef.getInt(50)); 
		     application.setCredBureName1(appForAppRef.getString(51));      
		     application.setCredBureName2(appForAppRef.getString(52));      
		     application.setCredBureName3(appForAppRef.getString(53));		     
		     application.setCredBureName4(appForAppRef.getString(54));      
		     application.setCredBureName5(appForAppRef.getString(55)); 				     
		     application.setCibilFirmMsmeRank(appForAppRef.getInt(56)); 
		     application.setExpCommerScor(appForAppRef.getInt(57)); //expCommerScor     
		     application.setPromBorrNetWorth(appForAppRef.getFloat(58)); //promBorrNetWorth	 
		     application.setPromContribution(appForAppRef.getInt(59));	
		     application.setPromGAssoNPA1YrFlg(appForAppRef.getString(60)); 
		     application.setPromBussExpYr(appForAppRef.getInt(61));				     
		 	 application.setSalesRevenue(appForAppRef.getFloat(62));											
		 	 application.setTaxPBIT(appForAppRef.getFloat(63));					 	 				 	 
		 	 application.setTaxCurrentProvisionAmt(appForAppRef.getFloat(64));						
		 	 application.setTotCurrentAssets(appForAppRef.getFloat(65));									
		 	 application.setTotCurrentLiability(appForAppRef.getFloat(66));							
		 	 application.setTotTermLiability(appForAppRef.getFloat(67));									
		 	 application.setExuityCapital(appForAppRef.getFloat(68));										
		 	 application.setPreferenceCapital(appForAppRef.getFloat(69));								
		 	 application.setReservesSurplus(appForAppRef.getFloat(70));									
		 	 application.setRepaymentDueNyrAmt(appForAppRef.getFloat(71));
		 						
			    application.setInterestPayment(appForAppRef.getFloat(72));							
		 	    application.setOpratIncome(appForAppRef.getFloat(73));
				application.setProfAftTax(appForAppRef.getFloat(74)); 
				application.setNetworth(appForAppRef.getFloat(75));
				application.setDebitEqtRatioUnt(appForAppRef.getInt(76)); 
				application.setDebitSrvCoverageRatioTl(appForAppRef.getInt(77));
				application.setCurrentRatioWc(appForAppRef.getInt(78)); 
				application.setDebitEqtRatio(appForAppRef.getInt(79)); 
				application.setDebitSrvCoverageRatio(appForAppRef.getInt(80));
				application.setCurrentRatios(appForAppRef.getInt(81));
				application.setCreditBureauChiefPromScor(appForAppRef.getInt(82));
				application.setTotalAssets(appForAppRef.getFloat(83));	
				application.setExistGreenFldUnitType(appForAppRef.getString(84));
				
				application.setUnseqLoanportion(0.0d);
				application.setUnLoanPortionExcludCgtCovered(0.0d);
				
		// End financial record 
		appForAppRef.close();
		appForAppRef = null;
	}

	connection.commit();
} catch (SQLException sqlException) {
	Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
			sqlException.getMessage());
	Log.logException(sqlException);
	sqlException.printStackTrace();
	try {
		connection.rollback();
	} catch (SQLException ignore) {
		Log.log(Log.INFO, "ApplicationDAO", "getAppForAppRef",
				ignore.getMessage());
	}

	throw new DatabaseException(sqlException.getMessage());

} finally {
	DBConnection.freeConnection(connection);
}
projectOutlayDetails = null;

// Log.log(Log.INFO,"ApplicationDAO","getAppForAppRef","Exited. Memory : "+Runtime.getRuntime().freeMemory());

return application;

}



	/**
	 * This method is called when cgpan is entered
	 */

	public Application getAppForCgpan(String mliID, String cgpan)
			throws DatabaseException {
		// Log.log(Log.INFO,"ApplicationDAO","getAppForCgpan","Entered");
		//// System.out.println("PATH ApplicationDAO getAppForCgpan(String mliID,String cgpan) mliID = "+mliID+" cgpan = "+cgpan);
		Connection connection = DBConnection.getConnection(false);

		Application application = new Application();
		String appRefNo = "";

		try {
			Log.log(Log.DEBUG, "ApplicationDAO", "getAppForCgpan",
					"CGPAN After entering getAppforCgpan in DAO :" + cgpan);

			CallableStatement appForCgpan = connection
					.prepareCall("{?=call funcGetAppRefNoforCGPAN(?,?,?,?)}");

			appForCgpan.setString(3, mliID);
			appForCgpan.setString(4, cgpan);

			//// System.out.println("15139cgpan:"+cgpan+"-mliID:"+mliID);
			appForCgpan.registerOutParameter(1, Types.INTEGER);
			appForCgpan.registerOutParameter(5, Types.VARCHAR);

			appForCgpan.registerOutParameter(2, Types.VARCHAR); // App Ref No

			Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
					"Before Executing");

			appForCgpan.execute();

			Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
					"After Executing");

			int appForCgpanValue = appForCgpan.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "getAppForCgpan",
					"Application Details :" + appForCgpanValue);
			//// System.out.println("appForCgpanValue:"+appForCgpanValue);

			if (appForCgpanValue == Constants.FUNCTION_FAILURE) {

				String error = appForCgpan.getString(5);
				//// System.out.println("appForCgpanValue:"+appForCgpanValue+"error:"+error);
				appForCgpan.close();
				appForCgpan = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				//// System.out.println("15167 appForCgpan.getString(2):"+appForCgpan.getString(2));
				application.setAppRefNo(appForCgpan.getString(2)); // app ref no

				Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
						"Setting values for the application Object");
				//System.out.println("CHeck arn " + application.getAppRefNo());
				appForCgpan.close();
				appForCgpan = null;

			}
			appRefNo = application.getAppRefNo();

			application = getAppForAppRef(mliID, appRefNo);

			connection.commit();
		}

		catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan", "Exited");

		return application;

	}

	/**
	 * This method retrieves total approved amount for the scheme, subscheme
	 * passed
	 */
	public double getTotalApprovedAmt(Application application)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getTotalApprovedAmt", "Entered");

		double totalApprovedAmt;

		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement totalApprovedStmt = connection
					.prepareCall("{?=call funcGetTotalApprovedAmount(?,?,?,?)}");

			totalApprovedStmt.setString(2, application.getScheme());
			Log.log(Log.INFO, "ApplicationDAO", "getTotalApprovedAmt",
					"application.getScheme() :" + application.getScheme());

			totalApprovedStmt.setString(3, application.getSubSchemeName());
			Log.log(Log.INFO,
					"ApplicationDAO",
					"getTotalApprovedAmt",
					"application.getSubSchemeName() :"
							+ application.getSubSchemeName());

			totalApprovedStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			totalApprovedStmt.registerOutParameter(4, java.sql.Types.DOUBLE);
			totalApprovedStmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			totalApprovedStmt.execute();

			int totalApprovedStmtValue = totalApprovedStmt.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = totalApprovedStmt.getString(5);

				totalApprovedStmt.close();
				totalApprovedStmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				totalApprovedAmt = totalApprovedStmt.getDouble(4);

				Log.log(Log.INFO, "ApplicationDAO", "getTotalApprovedAmt",
						"totalApprovedAmt :" + totalApprovedAmt);
			}
			totalApprovedStmt.close();
			totalApprovedStmt = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getAppForCgpan",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return totalApprovedAmt;
	}

	/**
	 * This method returns the count of the claim application for the BID
	 * passed.
	 */
	public int getClaimCount(String bid) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getTotalApprovedAmt", "Entered");

		int claimCount = 0;

		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement totalClaimCount = connection
					.prepareCall("{?=call funcGetCountForClaim(?,?,?)}");

			totalClaimCount.setString(2, bid);
			Log.log(Log.INFO, "ApplicationDAO", "getClaimCount",
					"getClaimCount");

			totalClaimCount.registerOutParameter(1, java.sql.Types.INTEGER);
			totalClaimCount.registerOutParameter(3, java.sql.Types.INTEGER);
			totalClaimCount.registerOutParameter(4, java.sql.Types.VARCHAR);

			totalClaimCount.execute();

			int totalApprovedStmtValue = totalClaimCount.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = totalClaimCount.getString(4);

				totalClaimCount.close();
				totalClaimCount = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				claimCount = totalClaimCount.getInt(3);

				Log.log(Log.INFO, "ApplicationDAO", "getClaimCount",
						"getClaimCount :" + claimCount);
			}
			totalClaimCount.close();
			totalClaimCount = null;
		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getClaimCount",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getClaimCount",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return claimCount;
	}

	/**
	 * 
	 * @param appRefNo
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int getAppRefNoCount(String appRefNo) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getAppRefNoCount", "Entered");

		int appRefNumberCount = 0;
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT COUNT(APP_REF_NO) FROM APPLICATION_DETAIL WHERE APP_REF_NO=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, appRefNo);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				appRefNumberCount = rsSet.getInt(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("appRefNumberCount-"+appRefNumberCount);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return appRefNumberCount;
	}

	/**
	 * 
	 * added by sukuamr@path for retrieving ssi ref no for old cgpan and new
	 * application reference number
	 * 
	 * @param appRefNo
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getSSIRefNo(String appRefNo) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getSSIRefNo", "Entered");

		String ssiRef = "";
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT UNIQUE SSI_REFERENCE_NUMBER FROM APPLICATION_DETAIL WHERE (CGPAN=? OR APP_REF_NO=? ) ";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, appRefNo);
			pStmt.setString(2, appRefNo);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				ssiRef = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("ssiRef-"+ssiRef);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return ssiRef;
	}

	public String getSSIRefNoNew(String appRefNo) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getSSIRefNo", "Entered");

		String ssiRef = "";
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT UNIQUE SSI_REFERENCE_NUMBER FROM APPLICATION_DETAIL_TEMP WHERE (CGPAN=? OR APP_REF_NO=? ) union  "
					+ " SELECT UNIQUE SSI_REFERENCE_NUMBER FROM APPLICATION_DETAIL WHERE (CGPAN=? OR APP_REF_NO=? ) ";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, appRefNo);
			pStmt.setString(2, appRefNo);
			pStmt.setString(3, appRefNo);
			pStmt.setString(4, appRefNo);

			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				ssiRef = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("ssiRef-"+ssiRef);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return ssiRef;
	}

	/**
	 * 
	 * @param appRefNo
	 * @return
	 */
	public String getMemberIdforAppRef(String appRefNo)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getMemberIdforAppRef", "Entered");
		String memberId = "";
		Connection connection = DBConnection.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rsSet = null;
		try {
			String query = "SELECT MEM_BNK_ID||MEM_ZNE_ID||MEM_BRN_ID FROM APPLICATION_DETAIL WHERE APP_REF_NO=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, appRefNo);
			rsSet = pstmt.executeQuery();
			while (rsSet.next()) {
				memberId = rsSet.getString(1);
			}
			rsSet.close();
			pstmt.close();

		}

		catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "getMemberIdforAppRef", "Exited");
		return memberId;
	}

	/**
	 * Get the Branch Name for given cgpan
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getBranchName(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getBranchName", "Entered");

		String branchName = "";
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT UPPER(APP_MLI_BRANCH_NAME) FROM APPLICATION_DETAIL WHERE CGPAN=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, cgpan);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				branchName = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("appRefNumberCount-"+appRefNumberCount);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return branchName;
	}

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getAppRefNo(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getAppRefNo", "Entered");

		String appRefNo = "";
		PreparedStatement pStmt = null;
		ArrayList aList = new ArrayList();
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT APP_REF_NO FROM APPLICATION_DETAIL WHERE CGPAN=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, cgpan);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				appRefNo = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("appRefNumberCount-"+appRefNumberCount);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return appRefNo;
	}

	/**
	 * 
	 * @param memberId
	 * @param appRefNo
	 * @param cgpan
	 * @param branchName
	 * @param userId
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public void updateBranchNameForCgpan(String memberId, String appRefNo,
			String cgpan, String branchName, String userId)
			throws DatabaseException {
		String methodName = "submitClosureDetails";

		Log.log(Log.INFO, "ApplicationDAO", methodName, "Entered");
		String bankId = memberId.substring(0, 4);
		String zoneId = memberId.substring(4, 8);
		String branchId = memberId.substring(8, 12);
		//// System.out.println("MemberId:"+bankId.concat(zoneId).concat(branchId));
		Connection connection = DBConnection.getConnection();
		CallableStatement callable = null;
		int status = 0;
		try {

			callable = connection
					.prepareCall("{?=call funcInsBranchNameDet(?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1, Types.INTEGER);
			callable.setString(2, bankId);
			callable.setString(3, zoneId);
			callable.setString(4, branchId);
			callable.setString(5, appRefNo);
			//// System.out.println("AppRefNo:"+appRefNo);
			callable.setString(6, cgpan);
			//// System.out.println("CGPAN:"+cgpan);
			callable.setString(7, userId);
			//// System.out.println("User Id:"+userId);
			callable.setString(8, branchName);
			//// System.out.println("Branch Name:"+branchName);
			callable.registerOutParameter(9, java.sql.Types.VARCHAR);
			callable.executeQuery();
			status = callable.getInt(1);
			//// System.out.println("Error Code:"+status);
			String error = callable.getString(9);
			//// System.out.println("Error:"+error);
			Log.log(Log.DEBUG, "ApplicationDAO", methodName,
					"error code and error" + status + "," + error);

			if (status == Constants.FUNCTION_FAILURE) {
				Log.log(Log.ERROR, "ApplicationDAO", methodName, error);

				callable.close();
				callable = null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable = null;
			connection.commit();
		} catch (SQLException e) {
			Log.log(Log.ERROR, "ApplicationDAO", methodName, e.getMessage());

			Log.logException(e);

			throw new DatabaseException(
					"Unable to insert app_branchname_update details.");

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", methodName, "Exited");
	}

	/**
	 * 
	 * @param bankId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getExposureLimit(String bankId) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getExposureLimit", "Entered");

		double exposureLimit = 0;
		PreparedStatement pStmt = null;
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT NVL(CUMAMT,0)+NVL(UNPROCAMT,0) FROM VIEW_EXPOSURE_STATUS WHERE MEM_BNK_ID=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, bankId);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				exposureLimit = rsSet.getDouble(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("ExposureLimit for "+bankId+
			// "---"+exposureLimit);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return exposureLimit;
	}

	/**
	 * 
	 * @param bankId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getMaxExposureLimit(String bankId) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getMaxExposureLimit", "Entered");

		double maxExposureLimit = 0;
		PreparedStatement pStmt = null;
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT NVL(TOTAL_LIMIT,0) FROM EXPOSURE_LIMITS WHERE MEM_BNK_ID=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, bankId);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				maxExposureLimit = rsSet.getDouble(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("Maximum ExposureLimit for "+bankId+
			// "---"+maxExposureLimit);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return maxExposureLimit;
	}

	/**
	 * 
	 * @param cgpan
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public int getPaymentStatus(String cgpan) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getPaymentStatus", "Entered");

		int count = 0;
		PreparedStatement pStmt = null;
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT COUNT(CGPAN) FROM DAN_CGPAN_INFO WHERE CGPAN=? AND DCI_ALLOCATION_FLAG='N' "
					+ " AND DAN_ID LIKE 'CG%' AND (DCI_AMOUNT_RAISED-NVL(DCI_AMOUNT_CANCELLED,0))>0 "
					+ " HAVING COUNT(DAN_ID)=COUNT(CGPAN)";
			//// System.out.println("Query:"+query);
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, cgpan);

			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				count = rsSet.getInt(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("Guarantee / Enhanced dan amount is due for "+
			// cgpan + " and count is :"+count);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return count;
	}

	/**
	 * This method returns the classification of MLI
	 * 
	 * @param bankId
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public String getClassificationMLI(String bankId) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getClassificationMLI", "Entered");

		String mliType = null;
		PreparedStatement pStmt = null;
		ResultSet rsSet = null;
		Connection connection = DBConnection.getConnection();
		try {
			String query = "SELECT NVL(MLI_TYPE,NULL) FROM EXPOSURE_LIMITS WHERE MEM_BNK_ID=?";
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, bankId);
			rsSet = pStmt.executeQuery();
			while (rsSet.next()) {
				mliType = rsSet.getString(1);
			}
			rsSet.close();
			pStmt.close();
			//// System.out.println("classification of MLI- " + bankId + "---"
			// + mliType);
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return mliType;
	}

	/* end part here */

	/**
	 * This method returns the corpus contribution amount for the SSI Reference
	 * Number passed.
	 */
	public double getCorpusContAmt(int ssiRefNumber) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getCorpusContAmt", "Entered");

		double corpusContAmt;

		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement totalCorpusCont = connection
					.prepareCall("{?=call funcGetCorpusContamt(?,?,?)}");

			totalCorpusCont.setInt(2, ssiRefNumber);
			Log.log(Log.INFO, "ApplicationDAO", "getCorpusContAmt",
					"getClaimCount");

			totalCorpusCont.registerOutParameter(1, java.sql.Types.INTEGER);
			totalCorpusCont.registerOutParameter(3, java.sql.Types.DOUBLE);
			totalCorpusCont.registerOutParameter(4, java.sql.Types.VARCHAR);

			totalCorpusCont.execute();

			int totalApprovedStmtValue = totalCorpusCont.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = totalCorpusCont.getString(4);

				totalCorpusCont.close();
				totalCorpusCont = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				corpusContAmt = totalCorpusCont.getDouble(3);

				Log.log(Log.INFO, "ApplicationDAO", "getCorpusContAmt",
						"getClaimCount :" + corpusContAmt);
			}
			totalCorpusCont.close();
			totalCorpusCont = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCorpusContAmt",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getCorpusContAmt",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return corpusContAmt;
	}

	/**
	 * This method returns the amount for the participating bank passed.
	 */
	public double getPartBankAmount(String bnkId, String zoneId, String branchId)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getPartBankAmount", "Entered");

		double partBankAmount;

		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement partBankAmt = connection
					.prepareCall("{?=call funcGetPartBankAmount(?,?,?,?,?)}");

			partBankAmt.setString(2, bnkId);
			partBankAmt.setString(3, zoneId);
			partBankAmt.setString(4, branchId);

			partBankAmt.registerOutParameter(1, java.sql.Types.INTEGER);
			partBankAmt.registerOutParameter(5, java.sql.Types.DOUBLE);
			partBankAmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			partBankAmt.execute();

			int totalApprovedStmtValue = partBankAmt.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = partBankAmt.getString(6);

				partBankAmt.close();
				partBankAmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				partBankAmount = partBankAmt.getDouble(5);

				Log.log(Log.INFO, "ApplicationDAO", "getPartBankAmount",
						"getClaimCount :" + partBankAmount);
			}
			partBankAmt.close();
			partBankAmt = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getPartBankAmount",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getPartBankAmount",
						sqlException.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return partBankAmount;
	}

	/**
	 * This method returns the amount for the participating bank passed.
	 */
	public double getMcgsApprovedAmount(String bnkId, String zoneId,
			String branchId) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getMcgsApprovedAmount", "Entered");

		double partBankAmount;

		Connection connection = DBConnection.getConnection();

		try {
			CallableStatement partBankAmt = connection
					.prepareCall("{?=call funcGetMcgsApprovedAmt(?,?,?,?,?)}");

			partBankAmt.setString(2, bnkId);
			partBankAmt.setString(3, zoneId);
			partBankAmt.setString(4, branchId);

			partBankAmt.registerOutParameter(1, java.sql.Types.INTEGER);
			partBankAmt.registerOutParameter(5, java.sql.Types.DOUBLE);
			partBankAmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			partBankAmt.execute();

			int totalApprovedStmtValue = partBankAmt.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = partBankAmt.getString(6);

				partBankAmt.close();
				partBankAmt = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				partBankAmount = partBankAmt.getDouble(5);
				partBankAmt.close();
				partBankAmt = null;

				Log.log(Log.INFO, "ApplicationDAO", "getMcgsApprovedAmount",
						"getClaimCount :" + partBankAmount);
			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getMcgsApprovedAmount",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getMcgsApprovedAmount",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return partBankAmount;
	}

	/**
	 * This method returns the new Term Loan application Ref Nos
	 */
	public ArrayList getCountForTCConv() throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getCountForTCConv", "Entered");

		Connection connection = DBConnection.getConnection();
		ArrayList countTCApp = new ArrayList();

		try {
			CallableStatement countForTC = connection
					.prepareCall("{?=call packGetTCAppForConv.funcGetTCAppForConv(?,?)}");

			countForTC.registerOutParameter(1, java.sql.Types.INTEGER);
			countForTC.registerOutParameter(2, Constants.CURSOR);
			countForTC.registerOutParameter(3, java.sql.Types.VARCHAR);

			countForTC.execute();

			int totalApprovedStmtValue = countForTC.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = countForTC.getString(3);

				countForTC.close();
				countForTC = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcResults = (ResultSet) countForTC.getObject(2);
				while (tcResults.next()) {
					String appRefNo = (String) tcResults.getString(1);
					countTCApp.add(appRefNo);
				}

				tcResults.close();
				tcResults = null;
				countForTC.close();
				countForTC = null;
			}

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCountForTCConv",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getCountForTCConv",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return countTCApp;
	}

	/**
	 * This method returns the new Term Loan application Ref Nos
	 */
	public ArrayList getCountForWCConv() throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getCountForWCConv", "Entered");

		Connection connection = DBConnection.getConnection();
		ArrayList countWCApp = new ArrayList();

		try {
			CallableStatement countForWC = connection
					.prepareCall("{?=call packGetWCAppForConv.funcGetWCAppForConv(?,?)}");

			countForWC.registerOutParameter(1, java.sql.Types.INTEGER);
			countForWC.registerOutParameter(2, Constants.CURSOR);
			countForWC.registerOutParameter(3, java.sql.Types.VARCHAR);

			countForWC.execute();

			int totalApprovedStmtValue = countForWC.getInt(1);

			if (totalApprovedStmtValue == Constants.FUNCTION_FAILURE) {

				String error = countForWC.getString(3);

				countForWC.close();
				countForWC = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet wcResults = (ResultSet) countForWC.getObject(2);
				while (wcResults.next()) {
					String appRefNo = (String) wcResults.getString(1);
					countWCApp.add(appRefNo);
				}

				wcResults.close();
				wcResults = null;

			}
			countForWC.close();
			countForWC = null;

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCountForWCConv",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getCountForWCConv",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		return countWCApp;
	}

	/*
	 * This method updates the cgpan reference column with the application
	 * reference number
	 */

	public void updateTCConv(Application application, int appSSIRef)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateTCConv", "Entered");

		Connection connection = DBConnection.getConnection();

		try {

			CallableStatement updateRef = connection
					.prepareCall("{?=call funcUpdateTCConv(?,?,?,?,?,?)}");

			updateRef.registerOutParameter(1, Types.INTEGER);
			updateRef.registerOutParameter(7, Types.VARCHAR);

			updateRef.setString(2, application.getAppRefNo());
			updateRef.setString(3, application.getCgpan());
			updateRef.setInt(4, appSSIRef);
			updateRef.setInt(5, application.getBorrowerDetails()
					.getSsiDetails().getBorrowerRefNo());
			updateRef.setString(6, application.getSubSchemeName());

			updateRef.executeQuery();
			int functionReturnValue = updateRef.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateTCConv",
					"update App Reference :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateRef.getString(7);

				updateRef.close();
				updateRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateTCConv",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			}
			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateTCConv",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateTCConv",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	public void updateWCConv(Application application, int appSsiRef)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateWCConv", "Entered");

		Connection connection = DBConnection.getConnection();

		try {

			CallableStatement updateRef = connection
					.prepareCall("{?=call funcUpdateWCConv(?,?,?,?,?,?,?,?,?,?,?,?)}");

			updateRef.registerOutParameter(1, Types.INTEGER);
			updateRef.registerOutParameter(13, Types.VARCHAR);

			updateRef.setString(2, application.getAppRefNo());
			updateRef.setString(3, application.getCgpan());
			updateRef.setInt(4, appSsiRef);
			updateRef.setInt(5, application.getBorrowerDetails()
					.getSsiDetails().getBorrowerRefNo());
			updateRef.setString(6, application.getSubSchemeName());
			updateRef.setString(7, application.getStatus());
			updateRef.setDouble(8, application.getWc()
					.getFundBasedLimitSanctioned());
			updateRef.setDouble(9, application.getWc()
					.getNonFundBasedLimitSanctioned());
			updateRef.setDouble(10, application.getWc().getWcInterestRate());
			updateRef.setDouble(11, application.getWc()
					.getLimitNonFundBasedCommission());
			updateRef.setString(12, application.getWc().getWcInterestType());

			updateRef.executeQuery();
			int functionReturnValue = updateRef.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "updateWCConv",
					"update App Reference :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateRef.getString(13);

				updateRef.close();
				updateRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateWCConv",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			}
			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateWCConv",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateWCConv",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	public ArrayList getCountForDanGen(String appRefNo)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "getCountForDanGen", "Entered");

		Connection connection = DBConnection.getConnection();

		ArrayList countAmount = new ArrayList();

		try {

			CallableStatement countForDan = connection
					.prepareCall("{?=call funcGetDanGenValue(?,?,?,?,?)}");

			countForDan.registerOutParameter(1, Types.INTEGER);
			countForDan.registerOutParameter(3, Types.INTEGER);
			countForDan.registerOutParameter(4, Types.DOUBLE);
			countForDan.registerOutParameter(5, Types.VARCHAR);
			countForDan.registerOutParameter(6, Types.VARCHAR);

			countForDan.setString(2, appRefNo);

			countForDan.executeQuery();
			int functionReturnValue = countForDan.getInt(1);
			Log.log(Log.DEBUG, "ApplicationDAO", "getCountForDanGen",
					"update App Reference :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = countForDan.getString(6);

				countForDan.close();
				countForDan = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "getCountForDanGen",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			} else {

				countAmount.add(new Integer(countForDan.getInt(3)));
				countAmount.add(new Double(countForDan.getDouble(4)));
				countAmount.add(countForDan.getString(5));

			}
			countForDan.close();
			countForDan = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCountForDanGen",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getCountForDanGen",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		return countAmount;
	}

	public void generateDanForEnhance(Application application, User user)
			throws DatabaseException {
		Connection connection = DBConnection.getConnection(false);

		RpHelper rpHelper = new RpHelper();
		RegistrationDAO registrationDAO = new RegistrationDAO();
		Mailer mailer = new Mailer();
		DemandAdvice demandAdvice = new DemandAdvice();
		RpProcessor rpProcessor = new RpProcessor();
		RpDAO rpDAO = new RpDAO();
		Administrator administrator = new Administrator();
		 double tempvar=0.0;
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
		java.util.Date  fyenddate = null;
		Date danGeneratedDate = new Date();

		try {
			Calendar cal = Calendar.getInstance();
			Date sanctionDate = application.getSanctionedDate();// application_detail

			//// System.out.println("sanctionDate PATH :" + sanctionDate);
			if (sanctionDate != null) {
				cal.setTime(sanctionDate);
				int year = cal.get(Calendar.YEAR);
				if (year >= 2013) {
					demandAdvice.setDanType("GF");
				} else {
					demandAdvice.setDanType(RpConstants.DAN_TYPE_CGDAN);
				}
			} else {
				throw new DatabaseException("Problem to process application:"
						+ application.getAppRefNo()
						+ ":Sanction date is missing.");
			}

			//// System.out.println("demandAdvice dan type :" +
			// demandAdvice.getDanType());
			demandAdvice.setBankId(application.getMliID().substring(0, 4));
			demandAdvice.setZoneId(application.getMliID().substring(4, 8));
			demandAdvice.setBranchId(application.getMliID().substring(8, 12));
			application.setReapprovedAmount(0);

			// String cgdanNo =
			// rpHelper.generateCGDANId(application.getMliID().substring(0,4),
			// application.getMliID().substring(4,8),
			// application.getMliID().substring(8,12), connection) ;
			String cgdanNo = rpHelper.generateCGDANId(demandAdvice, connection);
			//// System.out.println("generated enhancement dan id:" + cgdanNo);
			Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
					"cgdanNo" + cgdanNo);

			demandAdvice.setDanNo(cgdanNo);
			demandAdvice.setDanGeneratedDate(danGeneratedDate);
			Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
					"cgpan" + application.getCgpan());
			//// System.out.println("generateDanForEnhance cgpan"+application.getCgpan());
			demandAdvice.setCgpan(application.getCgpan());

			Date dueDate = rpProcessor.getPANDueDate(demandAdvice, null);
			demandAdvice.setDanDueDate(dueDate);
			// Set expiry date for DAN
			demandAdvice.setUserId(user.getUserId());

		rpDAO.insertDANDetails(demandAdvice, connection);// inserting
																// demand_advice_info

			emailMessage += "DAN No : " + demandAdvice.getDanNo() + "\n";

			// Log.log(Log.INFO,"application DAO","generateDanForEnhance","monthDiff"+
			// monthDiff) ;

			double[] guaranteeAmount = rpProcessor.calculateGuaranteeFee(application);// based on card rate
														// calculate guarantee
														// fee base amount
			//// System.out.println("guaranteeAmount PATH :" + guaranteeAmount);
			Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
					"guaranteeAmount" + guaranteeAmount);
			Date guarStartDate = rpDAO.getGuarStartDate(application);
			Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
					"guarStartDate" + guarStartDate);
			//// System.out.println("guarStartDate PATH :" + guarStartDate);
			if (guarStartDate != null) {
				/*
				 * ParameterMaster param = administrator.getParameter(); int
				 * tenorYears = param.getWcTenorInYrs();
				 */
				ClaimsProcessor cpProcessor = new ClaimsProcessor();
				HashMap wcDetail = cpProcessor.getWorkingCapital(application
						.getCgpan());
				Integer applicationTenureObj = (Integer) wcDetail
						.get(ClaimConstants.WC_TENURE);
				
				System.out.println(applicationTenureObj);
				
				int tenorMonths = applicationTenureObj.intValue();
				Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
						"tenorMonths" + tenorMonths);
				
			//	System.out.println(tenorMonths);

				
				////koteswar start on 11082016
				
				try {
					
					Statement sanctiondAmountStatement = null;
					ResultSet sanctionedAmountResult = null;
					
					//Connection connection = DBConnection.getConnection(false);
					
					String query=" select least(max(enddt),max(exdt))  from (" +
" select  fystartdate(add_months(sysdate,24)) - 1 enddt, null exdt from dual" +
" union all" +" select null enddt ,APP_EXPIRY_DT from application_detail  where  app_ref_no='"+application.getAppRefNo()+"')  ";

				
					sanctiondAmountStatement = connection.prepareStatement(query);
				

					sanctionedAmountResult = sanctiondAmountStatement.executeQuery(query);
					while (sanctionedAmountResult.next()) {
						fyenddate = sanctionedAmountResult.getDate(1);

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
				
				System.out.println("System.out.println(fyenddate); is"+fyenddate);
				
				System.out.println(fyenddate); 
				
				Calendar calendar = Calendar.getInstance();
				
		
				
				calendar.setTime(fyenddate);
				
				Calendar calendar2 = Calendar.getInstance();
				
					
					calendar2.set(2012, 11, 31);
					
				
			
				java.util.Date cutofdate=calendar2.getTime();			
			
				
				Calendar calendar3 = Calendar.getInstance();			
				
				java.util.Date sancdt=application.getSanctionedDate();
				calendar3.setTime(sancdt);
						
				
				java.util.Date sancddt=calendar3.getTime();				
         
				
				Calendar sysdate = Calendar.getInstance();
				
				
			
				
				long monthDiff=(long) 0.0;
				
				
				
				if(sancddt.after(cutofdate))
				{

					
					sysdate.setTime(new java.util.Date());
					
					
					
					
					int daysdiff = (int) DateHelper.getDays(sysdate,
							calendar);
					
					 // System.out.println("final daysdiff if(sancddt.after(cutofdate))  is "+daysdiff);
					  
					 // System.out.println("final guaranteeAmount[4] if(sancddt.after(cutofdate))  is "+guaranteeAmount[4]);
					
					  tempvar = Math.abs(Math.round(guaranteeAmount[4] * daysdiff/365));
					  
					 // System.out.println("final tempvar if(sancddt.after(cutofdate)) is "+tempvar);
					  
				}
				else
				{
					
					Calendar calendar5 = Calendar.getInstance();
					
					
					calendar5.setTime(guarStartDate);
					
			
					calendar5.add(Calendar.MONTH, tenorMonths);
					
					
					Calendar sysdate5 = Calendar.getInstance();
					
					sysdate5.setTime(new java.util.Date());
					
					
					 monthDiff = DateHelper.getMonthDifference(sysdate5,
							 calendar5);
					 
						double monthDiffDouble = new Long(monthDiff).doubleValue() / 12;
						double tenorDouble = new Integer(tenorMonths).doubleValue() / 12;
					 guaranteeAmount[4] = guaranteeAmount[4] * (Math.ceil(monthDiffDouble )/ Math.ceil(tenorDouble));
					 guaranteeAmount[4] = Math.round(guaranteeAmount[4]);
					 tempvar=Math.abs(guaranteeAmount[4]);
					 
					// System.out.println("final guaranteeAmount[4] if(sancddt.BEFOR(cutofdate))  is "+guaranteeAmount[4]);
					 
					// System.out.println("final monthDiff if(sancddt.BEFOR(cutofdate))  is "+monthDiff);
					 
					 // System.out.println("final valies if(sancddt.BEFOR(cutofdate)) is "+tempvar);
					 
				}
				
				
			////koteswar end on 11082016
				
				

				Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
						"monthDiff" + monthDiff);

			
				//// System.out.println("monthDiffDouble PATH :" +
			
				Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
						"guaranteeAmount" + guaranteeAmount);

			}

			Log.log(Log.INFO, "application DAO", "generateDanForEnhance",
					"guaranteeAmount" + guaranteeAmount);

			demandAdvice.setAmountRaised(Math.round(tempvar));
			
			//demandAdvice.setAmountRaised(Math.round(guaranteeAmount[4]));
			
		   
             
             demandAdvice.setStandardRate(guaranteeAmount[0]);
             
               demandAdvice.setNpaRiskRate(guaranteeAmount[1]);
               
                 demandAdvice.setClaimRiskrate(guaranteeAmount[2]);
                 
                   demandAdvice.setFinalRate(guaranteeAmount[3]);
			
			
			//// System.out.println("inserting dan info");
			rpDAO.insertPANDetailsForDAN(demandAdvice, connection);// inserting
																	// dan_cgpan_info

			emailMessage += "CGPAN - " + demandAdvice.getCgpan()
					+ ", GuaranteeAmount - " + demandAdvice.getAmountRaised()
					+ "\n";

			// update the guarantee fee for the CGPAN.
			/*
			 * GuaranteeFee guaranteeFee=new GuaranteeFee();
			 * guaranteeFee.setGuaranteeAmount(demandAdvice.getAmountRaised());
			 * guaranteeFee.setCgpan(demandAdvice.getCgpan());
			 * rpDAO.updateGuaranteeFee(guaranteeFee,user.getUserId(),
			 * connection);
			 */
			try {
				users = administrator.getAllUsers(application.getMliID());
			} catch (NoUserFoundException exception) {
				Log.log(Log.WARNING, "application DAO",
						"generateDanForEnhance",
						"Exception getting user details for the MLI. Error="
								+ exception.getMessage());
			} catch (DatabaseException exception) {
				Log.log(Log.WARNING, "application DAO",
						"generateDanForEnhance",
						"Exception getting user details for the MLI. Error="
								+ exception.getMessage());
			}

			mailer = new Mailer();

			mliInfo = registrationDAO.getMemberDetails(application.getMliID()
					.substring(0, 4), application.getMliID().substring(4, 8),
					application.getMliID().substring(8, 12));

			mailPrivelege = mliInfo.getMail();
			emailPrivelege = mliInfo.getEmail();
			hardCopyPrivelege = mliInfo.getHardCopy();

			Log.log(Log.DEBUG, "application DAO", "generateDanForEnhance",
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

			}

			if (emailIds != null) {

				subject = "New Demand Advice(" + cgdanNo + ") generated";
				Message message = new Message(emailIds, null, null, subject,
						emailMessage);
				message.setFrom(fromEmail);
				// try
				// {
				// mailer.sendEmail(message);
				administrator.sendMail(message);
				/*
				 * }catch(MailerException mailerException) {
				 * Log.log(Log.WARNING,
				 * className,methodName,"Exception sending Mail. Error="
				 * +mailerException.getMessage()) ; }
				 */// administrator(message) ;

				// mailStatus = mailer.sendEmail(message) ;
			}
			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "getCountForDanGen",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "getCountForDanGen",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

		finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

	}

	public double getBalanceApprovedAmt(Application application)
			throws DatabaseException {
		RiskManagementProcessor rmProcessor = new RiskManagementProcessor();
		String subSchemeName = rmProcessor.getSubScheme(application);

		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "state"
				+ application.getBorrowerDetails().getSsiDetails().getState());
		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "industry nature"
				+ application.getBorrowerDetails().getSsiDetails()
						.getIndustryNature());
		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "Gender"
				+ application.getBorrowerDetails().getSsiDetails()
						.getCpGender());
		Log.log(Log.INFO, "ApplicationDAO", "submitApp",
				"mli ID" + application.getMliID());
		Log.log(Log.INFO, "ApplicationDAO", "submitApp", "Social Cat"
				+ application.getBorrowerDetails().getSsiDetails()
						.getSocialCategory());

		double approvedAmt = 0;
		double balanceAppAmt = 0;
		double exposureAmount = 0;

		Connection connection = DBConnection.getConnection();
		CallableStatement approvedAmount = null;

		try {
			approvedAmount = connection
					.prepareCall("{?=call funcGetApprovedAmt(?,?,?)}");

			approvedAmount.registerOutParameter(1, Types.INTEGER);
			approvedAmount.setDouble(2, ((application.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo());
			approvedAmount.registerOutParameter(3, Types.DOUBLE);
			approvedAmount.registerOutParameter(4, Types.VARCHAR);

			approvedAmount.executeQuery();

			int approvedAmountValue = approvedAmount.getInt(1);
			//// System.out.println("Line number 14909 approvedAmountValue"+approvedAmountValue);
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
					"SSi Details Approved Amount result :"
							+ approvedAmountValue);

			if (approvedAmountValue == Constants.FUNCTION_FAILURE) {

				String error = approvedAmount.getString(4);

				approvedAmount.close();
				approvedAmount = null;

				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						"SSI Detail Approved Amount Exception :" + error);

				throw new DatabaseException(error);
			} else {
				approvedAmt = approvedAmount.getInt(3);

				approvedAmount.close();
				approvedAmount = null;
			}
			//// System.out.println("subSchemeName"+subSchemeName);
			if (!subSchemeName.equals("GLOBAL")) {
				SubSchemeValues subSchemeValues = rmProcessor
						.getSubSchemeValues(subSchemeName);

				if (subSchemeValues != null) {

					exposureAmount = subSchemeValues
							.getMaxBorrowerExposureAmount();
					//// System.out.println("ApplicationDAO submitApp exposureAmount :"
					// + exposureAmount);

					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"exposureAmount :" + exposureAmount);

				}
			} else {
				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();
				exposureAmount = param.getMaxApprovedAmt();
				//// System.out.println("exposureAmount"+exposureAmount);
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"exposureAmount :" + exposureAmount);
				double rsfMaximumAmount = param.getMaxRsfApprovedAmt();
				//// System.out.println("Line number14950  ApplicationDAO.java-rsfMaximumAmount"+rsfMaximumAmount);

			}
			/* Start comments by path on 5th oct,06 */
			if (approvedAmt >= exposureAmount) {
				throw new DatabaseException(
						"Borrower has crossed his exposure limit.Hence ineligible to submit a new application");
			}
			// End comments by path */
			balanceAppAmt = exposureAmount - approvedAmt;

			Log.log(Log.INFO, "ApplicationDAO", "submitApp", "balanceAppAmt :"
					+ balanceAppAmt);

		} catch (SQLException se) {
			throw new DatabaseException(se.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return balanceAppAmt;
	}

	/* ADDED BY SUKUMAR FOR GETTING RSF MAXIMUM LIMIT */
	/**
	 * 
	 * @param application
	 * @return
	 * @throws com.cgtsi.common.DatabaseException
	 */
	public double getBalanceRsfApprovedAmt(Application application)
			throws DatabaseException {
		RiskManagementProcessor rmProcessor = new RiskManagementProcessor();
		String subSchemeName = rmProcessor.getSubScheme(application);

		Log.log(Log.INFO, "ApplicationDAO", "getBalanceRsfApprovedAmt", "state"
				+ application.getBorrowerDetails().getSsiDetails().getState());
		Log.log(Log.INFO, "ApplicationDAO", "getBalanceRsfApprovedAmt",
				"industry nature"
						+ application.getBorrowerDetails().getSsiDetails()
								.getIndustryNature());
		Log.log(Log.INFO, "ApplicationDAO", "getBalanceRsfApprovedAmt",
				"Gender"
						+ application.getBorrowerDetails().getSsiDetails()
								.getCpGender());
		Log.log(Log.INFO, "ApplicationDAO", "getBalanceRsfApprovedAmt",
				"mli ID" + application.getMliID());
		Log.log(Log.INFO, "ApplicationDAO", "getBalanceRsfApprovedAmt",
				"Social Cat"
						+ application.getBorrowerDetails().getSsiDetails()
								.getSocialCategory());

		double approvedAmt = 0;
		double balanceAppAmt = 0;
		double exposureAmount = 0;

		Connection connection = DBConnection.getConnection();
		CallableStatement approvedAmount = null;

		try {
			approvedAmount = connection
					.prepareCall("{?=call funcGetApprovedAmt(?,?,?)}");

			approvedAmount.registerOutParameter(1, Types.INTEGER);
			approvedAmount.setDouble(2, ((application.getBorrowerDetails())
					.getSsiDetails()).getBorrowerRefNo());
			//// System.out.println("ssi_reference_number::: "+((application.getBorrowerDetails()).getSsiDetails()).getBorrowerRefNo());
			approvedAmount.registerOutParameter(3, Types.DOUBLE);
			approvedAmount.registerOutParameter(4, Types.VARCHAR);

			approvedAmount.executeQuery();

			int approvedAmountValue = approvedAmount.getInt(1);
			//// System.out.println("Line number 14909 approvedAmountValue"+approvedAmountValue);
			Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
					"SSi Details Approved Amount result :"
							+ approvedAmountValue);

			if (approvedAmountValue == Constants.FUNCTION_FAILURE) {

				String error = approvedAmount.getString(4);

				approvedAmount.close();
				approvedAmount = null;

				Log.log(Log.ERROR, "ApplicationDAO", "submitApp",
						"SSI Detail Approved Amount Exception :" + error);

				throw new DatabaseException(error);
			} else {
				approvedAmt = approvedAmount.getInt(3);

				approvedAmount.close();
				approvedAmount = null;
			}
			//// System.out.println("subSchemeName"+subSchemeName);
			if (!subSchemeName.equals("GLOBAL")) {
				SubSchemeValues subSchemeValues = rmProcessor
						.getSubSchemeValues(subSchemeName);

				if (subSchemeValues != null) {

					exposureAmount = subSchemeValues
							.getMaxBorrowerExposureAmount();
					//// System.out.println("ApplicationDAO submitApp exposureAmount :"
					// + exposureAmount);

					Log.log(Log.DEBUG, "ApplicationDAO", "submitApp",
							"exposureAmount :" + exposureAmount);

				}
			} else {
				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();
				exposureAmount = param.getMaxRsfApprovedAmt();
				//// System.out.println("exposureAmount"+exposureAmount);
				Log.log(Log.INFO, "ApplicationDAO", "submitApp",
						"exposureAmount :" + exposureAmount);
				// double rsfMaximumAmount=param.getMaxRsfApprovedAmt();
				//// System.out.println("Line number14950  ApplicationDAO.java-rsfMaximumAmount"+exposureAmount);

			}
			/*
			 * Start comments by path on 5th oct,06 if(approvedAmt >=
			 * exposureAmount) { throw new DatabaseException(
			 * "Borrower has crossed his exposure limit.Hence ineligible to submit a new application"
			 * ); } End comments by path
			 */
			balanceAppAmt = exposureAmount - approvedAmt;
			//// System.out.println("Line number 14959 balanceAppAmt"+balanceAppAmt);
			Log.log(Log.INFO, "ApplicationDAO", "submitApp", "balanceAppAmt :"
					+ balanceAppAmt);

		} catch (SQLException se) {
			throw new DatabaseException(se.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}

		return balanceAppAmt;
	}

	public double getBalanceRsf2ApprovedAmt(Application application)
			throws DatabaseException {
		RiskManagementProcessor rmProcessor;
		String subSchemeName;
		double balanceAppAmt;
		double exposureAmount;
		Connection connection;
		rmProcessor = new RiskManagementProcessor();
		subSchemeName = rmProcessor.getSubScheme(application);
		Log.log(4,
				"ApplicationDAO",
				"getBalanceRsfApprovedAmt",
				(new StringBuilder())
						.append("state")
						.append(application.getBorrowerDetails()
								.getSsiDetails().getState()).toString());
		Log.log(4,
				"ApplicationDAO",
				"getBalanceRsfApprovedAmt",
				(new StringBuilder())
						.append("industry nature")
						.append(application.getBorrowerDetails()
								.getSsiDetails().getIndustryNature())
						.toString());
		Log.log(4,
				"ApplicationDAO",
				"getBalanceRsfApprovedAmt",
				(new StringBuilder())
						.append("Gender")
						.append(application.getBorrowerDetails()
								.getSsiDetails().getCpGender()).toString());
		Log.log(4,
				"ApplicationDAO",
				"getBalanceRsfApprovedAmt",
				(new StringBuilder()).append("mli ID")
						.append(application.getMliID()).toString());
		Log.log(4,
				"ApplicationDAO",
				"getBalanceRsfApprovedAmt",
				(new StringBuilder())
						.append("Social Cat")
						.append(application.getBorrowerDetails()
								.getSsiDetails().getSocialCategory())
						.toString());
		double approvedAmt = 0.0D;
		balanceAppAmt = 0.0D;
		exposureAmount = 0.0D;
		connection = DBConnection.getConnection();
		CallableStatement approvedAmount = null;
		try {
			approvedAmount = connection
					.prepareCall("{?=call funcGetApprovedAmt(?,?,?)}");
			approvedAmount.registerOutParameter(1, 4);
			approvedAmount.setDouble(2, application.getBorrowerDetails()
					.getSsiDetails().getBorrowerRefNo());
			approvedAmount.registerOutParameter(3, 8);
			approvedAmount.registerOutParameter(4, 12);
			approvedAmount.executeQuery();
			int approvedAmountValue = approvedAmount.getInt(1);
			Log.log(5,
					"ApplicationDAO",
					"submitApp",
					(new StringBuilder())
							.append("SSi Details Approved Amount result :")
							.append(approvedAmountValue).toString());
			if (approvedAmountValue == 1) {
				String error = approvedAmount.getString(4);
				approvedAmount.close();
				approvedAmount = null;
				Log.log(2, "ApplicationDAO", "submitApp", (new StringBuilder())
						.append("SSI Detail Approved Amount Exception :")
						.append(error).toString());
				throw new DatabaseException(error);
			}
			approvedAmt = approvedAmount.getInt(3);
			approvedAmount.close();
			approvedAmount = null;
			if (!subSchemeName.equals("GLOBAL")) {
				SubSchemeValues subSchemeValues = rmProcessor
						.getSubSchemeValues(subSchemeName);
				if (subSchemeValues != null) {
					exposureAmount = subSchemeValues
							.getMaxBorrowerExposureAmount();
					Log.log(5, "ApplicationDAO", "submitApp",
							(new StringBuilder()).append("exposureAmount :")
									.append(exposureAmount).toString());
				}
			} else {
				Administrator admin = new Administrator();
				ParameterMaster param = admin.getParameter();
				exposureAmount = param.getMaxRsf2ApprovedAmt();
				Log.log(4, "ApplicationDAO", "submitApp", (new StringBuilder())
						.append("exposureAmount :").append(exposureAmount)
						.toString());
			}
			balanceAppAmt = exposureAmount - approvedAmt;
			Log.log(4, "ApplicationDAO", "submitApp", (new StringBuilder())
					.append("balanceAppAmt :").append(balanceAppAmt).toString());
		} catch (SQLException se) {
			throw new DatabaseException(se.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return balanceAppAmt;
	}

	public void updateWcTenure(Application application, Connection connection)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateWcTenure", "Entered");

		try {

			CallableStatement updateTenure = connection
					.prepareCall("{?=call funcUpdateWCTenure(?,?)}");

			updateTenure.registerOutParameter(1, Types.INTEGER);
			updateTenure.registerOutParameter(3, Types.VARCHAR);

			updateTenure.setString(2, application.getAppRefNo());

			Log.log(Log.INFO, "ApplicationDAO", "updateWcTenure",
					"app ref no :" + application.getAppRefNo());
			/*
			 * updateTenure.setInt(3,application.getTermLoan().getTenure());
			 * 
			 * Log.log(Log.INFO,"ApplicationDAO","updateWcTenure","tenure :" +
			 * application.getTermLoan().getTenure());
			 */
			updateTenure.executeQuery();
			int functionReturnValue = updateTenure.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "updateWcTenure",
					"updateTenure :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateTenure.getString(3);

				updateTenure.close();
				updateTenure = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateWcTenure",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			}
			updateTenure.close();
			updateTenure = null;

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateWcTenure",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateWcTenure",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		}

	}

	public void updateAppCgpanReference(Application application)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference",
				"Entered");
		Connection connection = DBConnection.getConnection();
		try {

			CallableStatement updateCgpanRef = connection
					.prepareCall("{?=call funcUpdateAppCgpanRef(?,?)}");

			updateCgpanRef.registerOutParameter(1, Types.INTEGER);
			updateCgpanRef.registerOutParameter(3, Types.VARCHAR);

			updateCgpanRef.setString(2, application.getAppRefNo());

			Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference",
					"app ref no :" + application.getAppRefNo());

			updateCgpanRef.executeQuery();
			int functionReturnValue = updateCgpanRef.getInt(1);
			Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference",
					"updateTenure :" + functionReturnValue);

			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = updateCgpanRef.getString(3);

				updateCgpanRef.close();
				updateCgpanRef = null;

				connection.rollback();

				Log.log(Log.ERROR, "ApplicationDAO", "updateAppCgpanReference",
						"updateAppReference Exception" + error);
				throw new DatabaseException(error);

			}
			updateCgpanRef.close();
			updateCgpanRef = null;

			// connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference",
						ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			// Free the connection here.
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "updateAppCgpanReference", "Exited");

	}

	public HashMap checkDuplicateForMLIWise(String bank)
			throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "Entered");

		Connection connection = DBConnection.getConnection(false);
		String bankId = "";
		String branchId = "";
		String zoneId = "";
		ArrayList tcApprovedAppList = null;
		ArrayList wcApprovedAppList = null;

		ArrayList tcPendingAppList = new ArrayList();
		ArrayList wcPendingAppList = new ArrayList();

		HashMap approvedPendingApplications = new HashMap();
		int Tccount;
		int Wccount;
		try {
			//System.out.println("ADAO call packGetAppCount.funcGetAppCount start");
			CallableStatement applicationCount = connection
					.prepareCall("{?=call packGetAppCount.funcGetAppCount(?,?)}");

			applicationCount.registerOutParameter(1, Types.INTEGER);
			applicationCount.registerOutParameter(3, Types.VARCHAR);

			applicationCount.registerOutParameter(2, Constants.CURSOR);

			applicationCount.executeQuery();
			int applicationCountValue = applicationCount.getInt(1);

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
					"Application Count value :" + applicationCountValue);

			if (applicationCountValue == Constants.FUNCTION_FAILURE) {

				String error = applicationCount.getString(4);

				applicationCount.close();
				applicationCount = null;

				connection.rollback();

				Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
						"Application Count message:" + error);
				throw new DatabaseException(error);
			} else {

				ResultSet results = (ResultSet) applicationCount.getObject(2);

				Tccount = 0;
				Wccount = 0;

				while (results.next()) {
					if (results.getString(2).equals("WC")) {
						Wccount += results.getInt(1);
					} else {
						Tccount += results.getInt(1);
					}

				}
				results.close();
				applicationCount.close();
				applicationCount = null;

				System.out.println("ADAP Tccount : " + Tccount + "\t Wccount : " + Wccount);
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
						"Tccount,Wccount " + Tccount + ", " + Wccount);
			}
			//System.out.println("ADAO call packGetAppCount.funcGetAppCount end");

			tcApprovedAppList = new ArrayList(Tccount);
			wcApprovedAppList = new ArrayList(Wccount);
			//System.out.println("ADAO tcApprovedAppList : " + tcApprovedAppList.size() + "\t wcApprovedAppList : " + wcApprovedAppList.size());

		//	System.out.println("ADAO call packGetApplications050815.funcGetApprovedApp start");
			/*	CallableStatement approvedApps = connection
					.prepareCall("{?=call packGetApplications050815.funcGetApprovedApp(?,?,?)}");
			approvedApps.registerOutParameter(1, Types.INTEGER);
			approvedApps.registerOutParameter(2, Constants.CURSOR);
			approvedApps.registerOutParameter(3, Types.VARCHAR);
			// koteswar added
			approvedApps.setString(4, bank);

			approvedApps.execute();

			int functionReturnValue = approvedApps.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = approvedApps.getString(3);

				approvedApps.close();
				approvedApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet approvedAppsResults = (ResultSet) approvedApps.getObject(2);				
				Application aApplication = null;
				BorrowerDetails aBorrowerDetails = null;
				SSIDetails aSsiDetails = null;
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
						"Just Before ");
				int ritu = 0;
				// Start Changes by Path On 29Oct2006

				while (approvedAppsResults.next()) {

					ritu++;
					//// System.out.println("Ritu"+ritu);
					aApplication = new Application();
					aBorrowerDetails = new BorrowerDetails();
					aSsiDetails = new SSIDetails();

					aApplication.setCgpan(approvedAppsResults.getString(1));
					//// System.out.println("PATH approvedAppsResults.getString(1) "+approvedAppsResults.getString(1));
					aApplication.setAppRefNo(approvedAppsResults.getString(2));
					//// System.out.println("PATH approvedAppsResults.getString(2) "+approvedAppsResults.getString(2));
					aSsiDetails.setCgbid(approvedAppsResults.getString(3));
					//// System.out.println("PATH approvedAppsResults.getString(3) "+approvedAppsResults.getString(3));
					aSsiDetails.setSsiName(approvedAppsResults.getString(4));
					//// System.out.println("PATH approvedAppsResults.getString(4) "+approvedAppsResults.getString(4));
					aSsiDetails.setRegNo(approvedAppsResults.getString(5));
					//// System.out.println("PATH approvedAppsResults.getString(5) "+approvedAppsResults.getString(5));
					aSsiDetails
							.setCpFirstName(approvedAppsResults.getString(6));
					//// System.out.println("PATH approvedAppsResults.getString(6) "+approvedAppsResults.getString(6));
					aSsiDetails.setCpMiddleName(approvedAppsResults
							.getString(7));
					//// System.out.println("PATH approvedAppsResults.getString(7) "+approvedAppsResults.getString(7));
					aSsiDetails.setCpLastName(approvedAppsResults.getString(8));
					//// System.out.println("PATH approvedAppsResults.getString(8) "+approvedAppsResults.getString(8));
					aSsiDetails.setAddress(approvedAppsResults.getString(9));
					//// System.out.println("PATH approvedAppsResults.getString(9) "+approvedAppsResults.getString(9));
					aApplication.setBankId(approvedAppsResults.getString(10));
					//// System.out.println("PATH approvedAppsResults.getString(10) "+approvedAppsResults.getString(10));
					aApplication.setZoneId("0000");
					aApplication.setBranchId("0000");
					aApplication.setLoanType(approvedAppsResults.getString(13));
					//// System.out.println("PATH approvedAppsResults.getString(13) "+approvedAppsResults.getString(13));
					aSsiDetails.setCpITPAN(approvedAppsResults.getString(14));
					//// System.out.println("PATH approvedAppsResults.getString(14) "+approvedAppsResults.getString(14));
					aSsiDetails.setState(approvedAppsResults.getString(15));
					//// System.out.println("PATH approvedAppsResults.getString(15) "+approvedAppsResults.getString(15));
					aSsiDetails.setDistrict(approvedAppsResults.getString(16));
					//// System.out.println("PATH approvedAppsResults.getString(16) "+approvedAppsResults.getString(16));
					aApplication.setMliRefNo(approvedAppsResults.getString(17));
					aApplication.setPrevSSI(approvedAppsResults.getString(18));
					//// System.out.println("PATH approvedAppsResults.getString(17) "+approvedAppsResults.getString(17));
					//// System.out.println(" aApplication.setPrevSSI"+
					// aApplication.getPrevSSI());
					aBorrowerDetails.setSsiDetails(aSsiDetails);
					aApplication.setBorrowerDetails(aBorrowerDetails);

					// //////////////////////////////////

					//// System.out.println(approvedAppsResults.toString());
					Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
							"approvedAppsResults" + approvedAppsResults);
					if (approvedAppsResults.getString(13).equals("TC")
							|| approvedAppsResults.getString(13).equals("CC")) {
						tcApprovedAppList.add(aApplication);
						//// System.out.println("PATH tcApprovedAppList ");
					} else if (approvedAppsResults.getString(13).equals("WC")) {
						wcApprovedAppList.add(aApplication);
						//// System.out.println("PATH wcApprovedAppList ");
					}

				}
			//	System.out.println("ADAO call packGetApplications050815.funcGetApprovedApp end");
				//// System.out.println("PATH value of approved Apps ritu = "+ritu);
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "After");
				approvedAppsResults.close();
				aApplication = null;
				aBorrowerDetails = null;
				aSsiDetails = null;
				approvedAppsResults = null;
				approvedApps.close();
				approvedApps = null;

			}*/

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
					"After getting all approved applications");
			
			System.out.println("ADAO call packGetApplications050815.funcGetPendingApp start");
			CallableStatement pendingApps = connection
					.prepareCall("{?=call packGetApplications050815.funcGetPendingApp(?,?,?)}");

			pendingApps.registerOutParameter(1, Types.INTEGER);
			pendingApps.registerOutParameter(2, Constants.CURSOR);
			pendingApps.registerOutParameter(3, Types.VARCHAR);
			pendingApps.setString(4, bank);

			pendingApps.execute();

			int functionReturnValues = pendingApps.getInt(1);
			if (functionReturnValues == Constants.FUNCTION_FAILURE) {

				String error = pendingApps.getString(3);

				pendingApps.close();
				pendingApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsResults = (ResultSet) pendingApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				int ritu1 = 0;
				while (pendingAppsResults.next()) {
					ritu1++;
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					if (pendingAppsResults.getString(1) != null
							&& !(pendingAppsResults.getString(1).equals(""))) {
						pApplication.setCgpan(pendingAppsResults.getString(1));
					}
					pApplication.setAppRefNo(pendingAppsResults.getString(2));
					pSsiDetails.setCgbid(pendingAppsResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsResults.getString(4));
					pSsiDetails.setRegNo(pendingAppsResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsResults.getString(6));
					pSsiDetails
							.setCpMiddleName(pendingAppsResults.getString(7));
					pSsiDetails.setCpLastName(pendingAppsResults.getString(8));
					pSsiDetails.setAddress(pendingAppsResults.getString(9));
					pApplication.setBankId(pendingAppsResults.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsResults.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsResults.getString(14));
					pSsiDetails.setState(pendingAppsResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsResults.getString(16));
					pApplication.setMliRefNo(pendingAppsResults.getString(17));
					pApplication.setExistSSI(pendingAppsResults.getString(18));

					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);
					if (pendingAppsResults.getString(13).equals("TC")
							|| pendingAppsResults.getString(13).equals("CC")) {
						tcPendingAppList.add(pApplication);
						//// System.out.println("PATH tcPendingAppList");
					} else if (pendingAppsResults.getString(13).equals("WC")) {
						wcPendingAppList.add(pApplication);
						//// System.out.println("PATH wcPendingAppList");
					}

					//System.out.println("ADAO wcPendingAppList size = "+ wcPendingAppList.size());
					//System.out.println("ADAO tcPendingAppList size = "+ tcPendingAppList.size());

				}
				//System.out.println("ADAO call packGetApplications050815.funcGetPendingApp end");
								
				pendingAppsResults.close();
				pendingAppsResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingApps.close();
				pendingApps = null;

			}

			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
					"After getting all pending applications");
			/**
			 * This block returns the applications with status 'PE'
			 */

			System.out.println("ADAO call packGetApplications.funcGetPendingAppStatus start");
			CallableStatement pendingStatusApps = connection
					.prepareCall("{?=call packGetApplications.funcGetPendingAppStatus(?,?)}");

			pendingStatusApps.registerOutParameter(1, Types.INTEGER);
			pendingStatusApps.registerOutParameter(2, Constants.CURSOR);
			pendingStatusApps.registerOutParameter(3, Types.VARCHAR);

			pendingStatusApps.execute();

			int functionReturnVal = pendingStatusApps.getInt(1);
			if (functionReturnVal == Constants.FUNCTION_FAILURE) {

				String error = pendingStatusApps.getString(3);

				pendingStatusApps.close();
				pendingStatusApps = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {
				ResultSet pendingAppsStatusResults = (ResultSet) pendingStatusApps
						.getObject(2);
				Application pApplication = null;
				BorrowerDetails pBorrowerDetails = null;
				SSIDetails pSsiDetails = null;
				int ritu2 = 0;
				while (pendingAppsStatusResults.next()) {
					ritu2++;
					pApplication = new Application();
					pBorrowerDetails = new BorrowerDetails();
					pSsiDetails = new SSIDetails();
					pApplication.setAppRefNo(pendingAppsStatusResults
							.getString(2));
					pSsiDetails.setCgbid(pendingAppsStatusResults.getString(3));
					pSsiDetails.setSsiName(pendingAppsStatusResults
							.getString(4));
					pSsiDetails.setRegNo(pendingAppsStatusResults.getString(5));
					pSsiDetails.setCpFirstName(pendingAppsStatusResults
							.getString(6));
					pSsiDetails.setCpMiddleName(pendingAppsStatusResults
							.getString(7));
					pSsiDetails.setCpLastName(pendingAppsStatusResults
							.getString(8));
					pSsiDetails.setAddress(pendingAppsStatusResults
							.getString(9));
					pApplication.setBankId(pendingAppsStatusResults
							.getString(10));
					pApplication.setZoneId("0000");
					pApplication.setBranchId("0000");
					pApplication.setLoanType(pendingAppsStatusResults
							.getString(13));
					pSsiDetails.setCpITPAN(pendingAppsStatusResults
							.getString(14));
					pSsiDetails
							.setState(pendingAppsStatusResults.getString(15));
					pSsiDetails.setDistrict(pendingAppsStatusResults
							.getString(16));
					pApplication.setMliRefNo(pendingAppsStatusResults
							.getString(17));
					// pApplication.setPrevSSI(pendingAppsStatusResults.getString(18));
					pBorrowerDetails.setSsiDetails(pSsiDetails);
					pApplication.setBorrowerDetails(pBorrowerDetails);

					if (pendingAppsStatusResults.getString(13).equals("TC")
							|| pendingAppsStatusResults.getString(13).equals(
									"CC")) {
						tcPendingAppList.add(pApplication);
						//// System.out.println("PATH tcPendingAppList");
					} else if (pendingAppsStatusResults.getString(13).equals(
							"WC")) {
						wcPendingAppList.add(pApplication);
						//// System.out.println("PATH wcPendingAppList");
					}
					//System.out.println("ADAO tcPendingAppList : "+tcPendingAppList.size() +"\t wcPendingAppList : "+wcPendingAppList.size());

				}
				//System.out.println("ADAO call packGetApplications.funcGetPendingAppStatus end");
				
				//// System.out.println("PATH ritu2 pendigng apps status = "+ritu2);
				pendingAppsStatusResults.close();
				pendingAppsStatusResults = null;
				pApplication = null;
				pBorrowerDetails = null;
				pSsiDetails = null;
				pendingStatusApps.close();
				pendingStatusApps = null;

			}

			//// System.out.println("pe status end");
			Log.log(Log.DEBUG, "ApplicationDAO", "checkDuplicate",
					"After getting all pending status");

			HashMap tcApprovedApplications = new HashMap();
			HashMap wcApprovedApplications = new HashMap();

			HashMap tcPendingApplications = new HashMap();
			HashMap wcPendingApplications = new HashMap();

			ArrayList tcApprovedApplicationsList = new ArrayList();
			ArrayList wcApprovedApplicationsList = new ArrayList();

			ArrayList tcPendingApplicationsList = new ArrayList();
			ArrayList wcPendingApplicationsList = new ArrayList();

			// Approved Application from the DB
			int tcApprovedListSize = tcApprovedAppList.size();
			int wcApprovedListSize = wcApprovedAppList.size();
			//System.out.println("Approved Application from the DB");
			//System.out.println("ADAO tcApprovedListSize : "+tcApprovedListSize +"\t wcApprovedListSize"+wcApprovedListSize);
			
			// Pending Application from the DB
			int tcPendingListSize = tcPendingAppList.size();
			int wcPendingListSize = wcPendingAppList.size();
		//	System.out.println("Pending Application from the DB");
			//System.out.println("ADAO tcPendingListSize : "+tcPendingListSize +"\t wcPendingListSize : "+wcPendingListSize);
			/*
			 * This loop groups the term loan approved applications based on the
			 * MliID and adds them to a hashtable
			 */
			for (int i = 0; i < tcApprovedListSize; i++) {

				Application tcApprovedApplication = (Application) tcApprovedAppList
						.get(i);
				// String cgpan=tcApprovedApplication.getCgpan();
				bankId = tcApprovedApplication.getBankId();
				zoneId = tcApprovedApplication.getZoneId();
				branchId = tcApprovedApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;
				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString tcApprovedListSize = "+mliIdString);
				if (tcApprovedApplications.containsKey(mliIdString)) {
					tcApprovedApplicationsList = (ArrayList) tcApprovedApplications
							.get(mliIdString);
				} else {
					tcApprovedApplicationsList = new ArrayList();
				}

				tcApprovedApplicationsList.add(tcApprovedApplication);
				tcApprovedApplications.put(mliIdString,
						tcApprovedApplicationsList);
			}
			/*
			 * This loop groups the working capital approved applications based
			 * on the MliID and adds them to a hashtable
			 */
			for (int j = 0; j < wcApprovedListSize; j++) {

				Application wcApprovedApplication = (Application) wcApprovedAppList
						.get(j);
				// String cgpan=wcApprovedApplication.getCgpan();
				bankId = wcApprovedApplication.getBankId();
				zoneId = wcApprovedApplication.getZoneId();
				branchId = wcApprovedApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString wcApprovedListSize = "+mliIdString);
				if (wcApprovedApplications.containsKey(mliIdString)) {
					wcApprovedApplicationsList = (ArrayList) wcApprovedApplications
							.get(mliIdString);
				} else {
					wcApprovedApplicationsList = new ArrayList();
				}

				wcApprovedApplicationsList.add(wcApprovedApplication);
				wcApprovedApplications.put(mliIdString,
						wcApprovedApplicationsList);
			}

			/*
			 * This loop groups the term credit pending applications based on
			 * the MliID and adds them to a hashtable
			 */

			for (int k = 0; k < tcPendingListSize; k++) {
				Application tcPendingApplication = (Application) tcPendingAppList
						.get(k);

				bankId = tcPendingApplication.getBankId();
				zoneId = tcPendingApplication.getZoneId();
				branchId = tcPendingApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString tcPendingListSize = "+mliIdString);
				if (tcPendingApplications.containsKey(mliIdString)) {
					tcPendingApplicationsList = (ArrayList) tcPendingApplications
							.get(mliIdString);
				} else {

					tcPendingApplicationsList = new ArrayList();
				}

				tcPendingApplicationsList.add(tcPendingApplication);
				tcPendingApplications.put(mliIdString,
						tcPendingApplicationsList);

			}

			/*
			 * This loop groups the working capital pending applications based
			 * on the MliID and adds them to a hashtable
			 */

			for (int l = 0; l < wcPendingListSize; l++) {
				Application wcPendingApplication = (Application) wcPendingAppList
						.get(l);

				bankId = wcPendingApplication.getBankId();
				zoneId = wcPendingApplication.getZoneId();
				branchId = wcPendingApplication.getBranchId();

				String mliId = bankId + zoneId + branchId;

				String mliIdString = new String(mliId);
				//// System.out.println("PATH mliIdString wcPendingListSize = "+mliIdString);
				if (wcPendingApplications.containsKey(mliIdString)) {
					wcPendingApplicationsList = (ArrayList) wcPendingApplications
							.get(mliIdString);
				} else {

					wcPendingApplicationsList = new ArrayList();
				}

				wcPendingApplicationsList.add(wcPendingApplication);

				wcPendingApplications.put(mliIdString,
						wcPendingApplicationsList);

			}

			approvedPendingApplications.put("tcApproved",
					tcApprovedApplications);
			approvedPendingApplications.put("wcApproved",
					wcApprovedApplications);

			approvedPendingApplications.put("tcPending", tcPendingApplications);
			approvedPendingApplications.put("wcPending", wcPendingApplications);

			tcApprovedApplications = null;
			wcApprovedApplications = null;

			tcPendingApplications = null;
			wcPendingApplications = null;

			tcApprovedApplicationsList = null;
			wcApprovedApplicationsList = null;
			tcPendingApplicationsList = null;
			wcPendingApplicationsList = null;

			bankId = null;
			zoneId = null;
			branchId = null;
			tcApprovedAppList = null;
			wcApprovedAppList = null;
			tcPendingAppList = null;
			wcPendingAppList = null;

			connection.commit();

		} catch (SQLException sqlException) {

			try {
				connection.rollback();
			} catch (SQLException ignore) {
				Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate",
						"Exception :" + ignore.getMessage());
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO, "ApplicationDAO", "checkDuplicate", "Exited");

		return approvedPendingApplications;

	}

	public ArrayList viewApplicationsForApprovalForMLIWise(String userId,
			String bank) throws DatabaseException {
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
				"Entered");
		ArrayList tcApprovedList = new ArrayList();
		ArrayList wcApprovedList = new ArrayList();
		ArrayList clearAppsList = new ArrayList();

		Connection connection = DBConnection.getConnection(false);
		try {
			CallableStatement approvalList = connection
					.prepareCall("{?=call packGetAppForApproval050815.funcGetAppForApproval(?,?,?,?,?,?)}");
			approvalList.registerOutParameter(1, Types.INTEGER);
			approvalList.registerOutParameter(3, Constants.CURSOR);
			approvalList.registerOutParameter(4, Constants.CURSOR);
			approvalList.registerOutParameter(5, Types.INTEGER);
			approvalList.registerOutParameter(6, Types.VARCHAR);

			approvalList.setString(2, userId);
			approvalList.setString(7, bank);

			approvalList.execute();
			int functionReturnValue = approvalList.getInt(1);
			if (functionReturnValue == Constants.FUNCTION_FAILURE) {

				String error = approvalList.getString(6);

				approvalList.close();
				approvalList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcClearApplications = (ResultSet) approvalList
						.getObject(3);
				Application application = null;
				int up = 0;
				int up2 = 0;
				while (tcClearApplications.next()) {
					up++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(tcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO", "n", "App ref no1:"
							+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(tcClearApplications.getDate(3));

					tcApprovedList.add(application);
					application = null;
					// if( up == 450){
					// break;
					// }
				}

				tcClearApplications.close();
				tcClearApplications = null;

				ResultSet wcClearApplications = (ResultSet) approvalList
						.getObject(4);
				// Application application= null;
				while (wcClearApplications.next()) {
					up2++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(wcClearApplications.getString(1)); // app
																				// ref
																				// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApproval", "App ref no 2:"
									+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcClearApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application
							.setSubmittedDate(wcClearApplications.getDate(3));

					wcApprovedList.add(application);
					application = null;
					// if(up2 == 450){
					// break;
					// }
				}

				wcClearApplications.close();
				wcClearApplications = null;

			}
			int appCount = approvalList.getInt(5);

			approvalList.close();
			approvalList = null;

			/**
			 * This function gets all the applications with status 'PE' from the
			 * intranet tables
			 */
			CallableStatement pendingApprovalList = connection
					.prepareCall("{?=call packGetPendingAppForApproval.funcGetPendingAppForApproval(?,?,?,?,?)}");
			pendingApprovalList.registerOutParameter(1, Types.INTEGER);
			pendingApprovalList.registerOutParameter(3, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(4, Constants.CURSOR);
			pendingApprovalList.registerOutParameter(5, Types.INTEGER);
			pendingApprovalList.registerOutParameter(6, Types.VARCHAR);

			pendingApprovalList.setString(2, userId);

			pendingApprovalList.execute();

			int functionReturnValue1 = pendingApprovalList.getInt(1);
			if (functionReturnValue1 == Constants.FUNCTION_FAILURE) {

				String error = pendingApprovalList.getString(6);

				pendingApprovalList.close();
				pendingApprovalList = null;

				connection.rollback();

				throw new DatabaseException(error);
			} else {

				ResultSet tcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(3);
				Application application = null;
				int up3 = 0;
				int up4 = 0;
				while (tcPendingApplications.next()) {
					up3++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(tcPendingApplications.getString(1)); // app
																					// ref
																					// no
					Log.log(Log.DEBUG, "ApplicationDAO", "n", "App ref no1:"
							+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(tcPendingApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No1:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application.setSubmittedDate(tcPendingApplications
							.getDate(3));

					tcApprovedList.add(application);
					application = null;
					// if(up3 == 450){
					// break;
					// }
				}

				tcPendingApplications.close();
				tcPendingApplications = null;

				ResultSet wcPendingApplications = (ResultSet) pendingApprovalList
						.getObject(4);
				// Application application= null;
				while (wcPendingApplications.next()) {
					up4++;
					application = new Application();
					BorrowerDetails borrowerDetails = new BorrowerDetails();
					// SSIDetails ssiDetails = new SSIDetails();
					borrowerDetails.setSsiDetails(new SSIDetails());
					application.setBorrowerDetails(borrowerDetails);

					application.setAppRefNo(wcPendingApplications.getString(1)); // app
																					// ref
																					// no
					Log.log(Log.DEBUG, "ApplicationDAO",
							"viewApplicationsForApproval", "App ref no 2:"
									+ application.getAppRefNo());
					(application.getBorrowerDetails().getSsiDetails())
							.setBorrowerRefNo(wcPendingApplications.getInt(2));
					Log.log(Log.DEBUG,
							"ApplicationDAO",
							"viewApplicationsForApproval",
							"SSI Ref No 2:"
									+ (application.getBorrowerDetails()
											.getSsiDetails())
											.getBorrowerRefNo());
					application.setSubmittedDate(wcPendingApplications
							.getDate(3));

					wcApprovedList.add(application);
					application = null;
					// if(up4 == 450){
					// break;
					// }
				}

				wcPendingApplications.close();
				wcPendingApplications = null;

			}

			int pendingCount = pendingApprovalList.getInt(5);
			Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
					"pendingCount" + pendingCount);

			int applicationCount = appCount + pendingCount;

			Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
					"applicationCount" + applicationCount);

			Integer intCount = new Integer(applicationCount);

			clearAppsList.add(tcApprovedList);
			clearAppsList.add(wcApprovedList);
			clearAppsList.add(intCount);
			tcApprovedList = null;
			wcApprovedList = null;

			connection.commit();

		} catch (SQLException sqlException) {

			Log.log(Log.ERROR, "ApplicationDAO", "viewApplicationsForApproval",
					sqlException.getMessage());
			Log.logException(sqlException);

			try {
				connection.rollback();
			} catch (SQLException ignore) {
			}

			throw new DatabaseException(sqlException.getMessage());

		} finally {
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "ApplicationDAO", "viewApplicationsForApproval",
				"Exited");

		return clearAppsList;
	}

	public Application viewApplicationsDetails(String loan_type,
			String tempAppRefNo) throws DatabaseException {
		// System.out.println("11 11 11 11 11 11 11 11 11");

		Application tempApplication = new Application();
		Connection connection = DBConnection.getConnection();
		try {
			if ((loan_type.equals("TC")) || (loan_type.equals("WC"))) {
				if (loan_type.equals("TC")) {
					PreparedStatement pst = connection
							.prepareStatement("select t.TRM_PLR,t.TRM_INTEREST_RATE from term_loan_detail_temp t where t.app_ref_no=? ");

					pst.setString(1, tempAppRefNo);
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						Double plr = rs.getDouble(1);
						Double int_rate = rs.getDouble(2);
						tempApplication.setTRM_PLR(plr);
						tempApplication.setTRM_INTEREST_RATE(int_rate);
					}

				}
				if (loan_type.equals("WC")) {
					PreparedStatement pst1 = connection
							.prepareStatement("select w.WCP_PLR,w.WCP_INTEREST  from working_capital_detail_temp w where w.app_ref_no=?");
					pst1.setString(1, tempAppRefNo);
					ResultSet rs1 = pst1.executeQuery();
					while (rs1.next()) {
						Double plr_wc = rs1.getDouble(1);
						Double int_rate_wc = rs1.getDouble(2);
						tempApplication.setWCP_PLR(plr_wc);
						tempApplication.setWCP_INTEREST(int_rate_wc);
					}

				}
			} else {
				//System.out
					//	.println("Error  :   Loan type must be TC or WC......");
			}
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return tempApplication;
	}

	public DuplicateApplication viewApplicationsDetailsDupicate(
			String loan_type, String tempAppRefNo) throws DatabaseException {
		// System.out.println("13 13 13 13 13 13 13 13 13 13 13 13 13 13 ");
		DuplicateApplication tempApplication = new DuplicateApplication();
		Connection connection = DBConnection.getConnection();
		try {
			if ((loan_type.equals("TC")) || (loan_type.equals("WC"))) {
				if (loan_type.equals("TC")) {
					PreparedStatement pst = connection
							.prepareStatement("select t.TRM_PLR,t.TRM_INTEREST_RATE from term_loan_detail_temp t where t.app_ref_no=? ");

					pst.setString(1, tempAppRefNo);
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						Double plr = rs.getDouble(1);
						Double int_rate = rs.getDouble(2);
						tempApplication.setTRM_PLR(plr);
						tempApplication.setTRM_INTEREST_RATE(int_rate);
					}
				}
				if (loan_type.equals("WC")) {
					PreparedStatement pst1 = connection
							.prepareStatement("select w.WCP_PLR,w.WCP_INTEREST  from working_capital_detail_temp w where w.app_ref_no=?");
					pst1.setString(1, tempAppRefNo);
					ResultSet rs1 = pst1.executeQuery();
					while (rs1.next()) {
						Double plr_wc = rs1.getDouble(1);
						Double int_rate_wc = rs1.getDouble(2);
						tempApplication.setWCP_PLR(plr_wc);
						tempApplication.setWCP_INTEREST(int_rate_wc);
					}

				}
			} else {
				//System.out
						//.println("Error  :   Loan type must be TC or WC......");
			}
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return tempApplication;

	}
public HashMap<String, Double> getImmovableDeatils(String bankName) throws DatabaseException {
		
		HashMap<String, Double> immovAmtMap=new HashMap<String, Double>();
		Connection connection = DBConnection.getConnection();
		
		try {
			String strQury="SELECT  A.app_ref_no, A.IMMOVCOLLATERATLSECURITY_AMT  FROM    SSI_DETAIL   s, application_detail_temp@cginter A, working_capital_detail_temp@cginter   w" +
			"   WHERE   UPPER(A.app_status)  IN  ('EN')   AND APP_SANCTION_DT IS NOT NULL  and mem_bnk_id in (select mem_bnk_id from member_info where mem_bank_name= ?)" +
			"  AND     A.ssi_reference_number    =     s.ssi_reference_number  AND    LTRIM(RTRIM(UPPER(A.app_ref_no))) = LTRIM(RTRIM(UPPER(w.app_ref_no)))  AND    UPPER(A.app_loan_type)   IN  " +
			" ('WC')   AND LTRIM(RTRIM(UPPER(A.APP_CREATED_MODIFIED_BY))) NOT IN ('DEMOUSER')  AND A.scm_id <> 3  AND A.MEM_BNK_ID NOT IN   (SELECT MEM_BNK_ID FROM EXPOSURE_LIMITS WHERE MLI_TYPE = 'RRB')" +
			" AND NOT EXISTS (SELECT 1 FROM WORKING_CAP_ENHA_FULL_DTL_TEMP WCET,  application_detail a  WHERE  wcet.app_ref_no = a.app_ref_no  AND WCET.APP_REF_NO = W.APP_REF_NO AND" +
			" TRUNC (ENHANCED_DT) < '01-APR-2016'  AND a.mem_bnk_id IN  (SELECT DISTINCT MEM_BNK_ID FROM MEMBER_INFO  WHERE MEM_BANK_NAME IN  ('IDBI BANK LTD', 'THE FEDERAL BANK LTD','UNITED BANK OF INDIA')))";
			System.out.println(bankName+" bankName==============strQury:--------------"+strQury);
			if (!bankName.equals(null) ) {
					PreparedStatement pst = connection
							.prepareStatement(strQury);
					pst.setString(1, bankName);
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						immovAmtMap.put(rs.getString(1), rs.getDouble(2));
					}
				
			}
		} catch (Exception exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		} finally {
			DBConnection.freeConnection(connection);
		}
		return immovAmtMap;
	}

}

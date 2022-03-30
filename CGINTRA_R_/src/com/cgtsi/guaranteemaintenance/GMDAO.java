 //Source file: D:\\com\\cgtsi\\guaranteemaintenance\\GMDAO.java


/*************************************************************
   *
   * Name of the class: GMDAO
   * This class encapsulates all the database queries for this module.
   *
   * @author : Gowrishankar K.U
   * @version:
   * @since:
**************************************************************/



package com.cgtsi.guaranteemaintenance;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.validator.GenericValidator;

import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationDAO;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.NoApplicationFoundException;
import com.cgtsi.application.SSIDetails;
import com.cgtsi.claim.ClaimConstants;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.receiptspayments.DANSummary;
import com.cgtsi.receiptspayments.DemandAdvice;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;

/**
 * This class provides methods that interact with the database to either retrieve
 * or update information. These methods can be invoked by methods in any other
 * class.
 */
public class GMDAO {

   /**
    * @roseuid 39B9CCD90242
    */
   public GMDAO() {

   }




// Fix for Maximum Cursors Open
      public Disbursement getDisbursementDetailsForCgpan(String cgpan) 
						throws DatabaseException 
	{   
		Log.log(Log.INFO,"GMDAO","getDisbursementDetailsForCgpan","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement getDisbursementDetailStmt = null;
		String exception = null;
		ResultSet resultSet;
		Disbursement disbursement =null;
		ArrayList listOfDisbursementAmount = new ArrayList();
		
		try
		{
		
		getDisbursementDetailStmt = connection.prepareCall("{?=call packGetDtlsforDBR.funcGetDtlsForCGPAN(?,?,?)}");
		getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
		Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","CGPAN :"+cgpan);
		getDisbursementDetailStmt.setString(2,cgpan);
		getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
		getDisbursementDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

		getDisbursementDetailStmt.executeQuery() ;
		
		exception = getDisbursementDetailStmt.getString(4) ;
		Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","exception for"+cgpan+"-->"+exception);		

		int error=getDisbursementDetailStmt.getInt(1);

		if(error==Constants.FUNCTION_FAILURE)
		{
			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt=null;
			Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","Exception :"+exception);
			connection.rollback();
			throw new DatabaseException(exception);
		}
		resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;

		while (resultSet.next()) 
		{
			disbursement = new Disbursement(); 
			disbursement.setCgpan(resultSet.getString(2));
			disbursement.setScheme(resultSet.getString(3));
			disbursement.setSanctionedAmount(resultSet.getDouble(5));
			Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","disbursement amt added");
		}
		resultSet.close();
		resultSet=null;

		getDisbursementDetailStmt.close();
		getDisbursementDetailStmt = null;

		getDisbursementDetailStmt = connection.prepareCall("{?=call packGetPIDBRDtlsCGPAN.funcDBRDetailsForCGPAN(?,?,?)}"); 

		getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
		getDisbursementDetailStmt.setString(2,cgpan);
		getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
		getDisbursementDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

		getDisbursementDetailStmt.execute() ;

		exception = getDisbursementDetailStmt.getString(4) ;
		Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","exception for"+cgpan+"-->"+exception);		

		error=getDisbursementDetailStmt.getInt(1);
		
		if(error==Constants.FUNCTION_FAILURE)
		{
			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt=null;
			Log.log(Log.ERROR,"GMDAO","getDisbursementDetailsForCgpan","Exception" + exception);
			connection.rollback();
			throw new DatabaseException(exception);
		}
		
		resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;
		DisbursementAmount disbursementAmount = null;

		while (resultSet.next())
		{
			disbursementAmount = new DisbursementAmount();
			disbursementAmount.setCgpan(cgpan);
			disbursementAmount.setDisbursementId(resultSet.getString(1));
			disbursementAmount.setDisbursementAmount(resultSet.getDouble(2));
			disbursementAmount.setDisbursementDate(DateHelper.sqlToUtilDate(resultSet.getDate(3)));
			disbursementAmount.setFinalDisbursement(resultSet.getString(4));
			listOfDisbursementAmount.add(disbursementAmount);
		}
		
		if (listOfDisbursementAmount.size()==0)
		{
			//disbursement=null;
		}
		else
		{
			disbursement.setDisbursementAmounts(listOfDisbursementAmount);
		}

		Log.log(Log.DEBUG,"GMDAO","getDisbursementDetailsForCgpan","Disbursement Details added");		
		
		resultSet.close();
		resultSet=null;
		getDisbursementDetailStmt.close();
		getDisbursementDetailStmt=null;

		}
		catch(Exception e) 
		{
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getDisbursementDetailsForCgpan","Exited");
		
	return disbursement; 		
	}   
   
   
   public OutstandingDetail getOutstandingDetailsForCgpan(String cgpan)
   						throws DatabaseException 
  	{
		Log.log(Log.INFO,"GMDAO","getOutstandingDetailsForCgpan","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement getOutstandingDetailStmt = null;
		CallableStatement getOutDetailForCgpanStmt = null;
		OutstandingDetail outstandingDetail = null;
		OutstandingAmount  outAmount = null;		
		String exception = null;
		ResultSet resultSet;
		ResultSet cgpanResultSet;
		ArrayList outAmounts = new ArrayList();
		
		try
		{
			getOutstandingDetailStmt=connection.prepareCall("{?=call packGetOutstandingDtls.funcGetOutStandingforCGPAN(?,?,?)}");
			getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getOutstandingDetailStmt.setString(2, cgpan);
			getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
			getOutstandingDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

			getOutstandingDetailStmt.execute() ;

			exception = getOutstandingDetailStmt.getString(4) ;
			Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","exception for"+cgpan+"-->"+exception);			

			int error=getOutstandingDetailStmt.getInt(1);

			if(error == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Success");
			}

			if(error==Constants.FUNCTION_FAILURE)
			{
				getOutstandingDetailStmt.close();
				getOutstandingDetailStmt=null;
				Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForCgpan","Exception "+exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}

			resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;
			while (resultSet.next())
			{
				String cgpan1 = resultSet.getString(2);
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","cgpan from view : "+ cgpan1);
				if(cgpan1 != null) 

				{
					outstandingDetail = new OutstandingDetail();
					outstandingDetail.setCgpan(cgpan1);
					//System.out.println(cgpan1);
					outstandingDetail.setScheme(resultSet.getString(4));
					if (cgpan1.substring((cgpan1.length()-2), cgpan1.length()-1).equalsIgnoreCase(GMConstants.CGPAN_TC))
					{
						outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
					}
					else if (cgpan1.substring((cgpan1.length()-2), cgpan1.length()-1).equalsIgnoreCase(GMConstants.CGPAN_WC))
					{
						outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(5));
						outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(6));
					}
					
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Outstanding added for"+cgpan);
				} 
			}
			getOutstandingDetailStmt.close();
			getOutstandingDetailStmt = null;
			
			resultSet.close();
			resultSet = null;
			
		  
			int cgpanLength = cgpan.length();
			String panType = cgpan.substring((cgpanLength-2), cgpanLength-1);  
	
			if(panType.equalsIgnoreCase(GMConstants.CGPAN_TC))
			{
				getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
				getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getOutDetailForCgpanStmt.setString(2, cgpan);
				getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
				getOutDetailForCgpanStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;
	
				getOutDetailForCgpanStmt.execute() ;
				
				exception = getOutDetailForCgpanStmt.getString(4) ;
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","exception for"+cgpan+"-->"+exception);				
				
				error=getOutDetailForCgpanStmt.getInt(1);
	
				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Success");
				}
	
				if(error==Constants.FUNCTION_FAILURE)
				{
					getOutDetailForCgpanStmt.close();
					getOutDetailForCgpanStmt=null;
					Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForCgpan","Exception "+exception);
					connection.rollback();	
					throw new DatabaseException(exception);
				}
	
				// Reading the resultset
				cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;
				
				while(cgpanResultSet.next())
				{
					outAmount = new OutstandingAmount();
					outAmount.setCgpan(cgpan);
					outAmount.setTcoId(cgpanResultSet.getString(1));
					outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
					outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));
					outAmounts.add(outAmount);
				}
				
				if (outAmounts.size()==0)
				{
					//outstandingDetail=null;
				}
				else
				{
					outstandingDetail.setOutstandingAmounts(outAmounts);
				}

				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Outstanding Amounts added for"+cgpan);				
				cgpanResultSet.close();
				cgpanResultSet = null;
	
				getOutDetailForCgpanStmt.close();
				getOutDetailForCgpanStmt=null;
			}
			
			else 
			{ 
				getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
				getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getOutDetailForCgpanStmt.setString(2, cgpan);
				getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
				getOutDetailForCgpanStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;
	
				getOutDetailForCgpanStmt.execute() ;
	
				exception = getOutDetailForCgpanStmt.getString(4) ;
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","exception for"+cgpan+"-->"+exception);				
	
				error=getOutDetailForCgpanStmt.getInt(1);
	
				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Success");
				}
	
				if(error==Constants.FUNCTION_FAILURE)
				{
					getOutDetailForCgpanStmt.close();
					getOutDetailForCgpanStmt=null;
					Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForCgpan","Exception "+exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
	
				// Reading the resultset
				cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;
	
				while(cgpanResultSet.next())
				{
					outAmount = new OutstandingAmount();
					outAmount.setCgpan(cgpan);
					outAmount.setWcoId(cgpanResultSet.getString(1));
					outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
					outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
					outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
					outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
					outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
					outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));
	
					outAmounts.add(outAmount);
				}
				
				if (outAmounts.size()==0) 
				{
					//outstandingDetail=null; 
				}
				else
				{
					outstandingDetail.setOutstandingAmounts(outAmounts);
				}				 
				
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetailsForCgpan","Outstanding Amounts added for"+cgpan);
								
				cgpanResultSet.close();
				cgpanResultSet = null;
	
				getOutDetailForCgpanStmt.close(); 
				getOutDetailForCgpanStmt=null;
			} 
		}
		
		catch(Exception e) 
		{
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getOutstandingDetailsForCgpan","Exited");		
		return outstandingDetail;   
	}
	
	/**
   * 
   * @param bankId
   * @param zoneId
   * @param branchId
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
   public ArrayList getShiftCgpanForMember(String bankId, String zoneId, String branchId) throws DatabaseException
   {
		DemandAdvice demandAdvice = null ;
		Connection connection = null ;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus=0;
		String getDanDetailsErr = "" ;

		ArrayList danDetails=null;

		connection=DBConnection.getConnection(false);

		try {
			danDetails=new ArrayList();

			//connection=DBConnection.getConnection();
			//System.out.println("Connection established");
		//	getDanDetailsStmt=connection.prepareCall("{?= call packGetGFDanDetailsNew.funcGetGFDanDetailsNew(?,?,?,?,?,?)}");
			getDanDetailsStmt=connection.prepareCall("{?= call packGetGFDanDetailsModified.funcGetGFDanDetailsModified(?,?,?,?,?,?)}");
		
    
    	//getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			//System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);

			getDanDetailsStmt.setString(2, bankId);
			getDanDetailsStmt.setString(3, zoneId);
			getDanDetailsStmt.setString(4, branchId);
			getDanDetailsStmt.registerOutParameter(5, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(6, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus=getDanDetailsStmt.getInt(1);
			if (getSchemesStatus==0) {
				rsDanDetails=(ResultSet) getDanDetailsStmt.getObject(5);
				DANSummary danSummary = null;
				while (rsDanDetails.next())
				{
					danSummary = new DANSummary() ;
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
				rsPaidDetails = (ResultSet) getDanDetailsStmt.getObject(6);
				while (rsPaidDetails.next())
				{
					String tempDanId = rsPaidDetails.getString(1);
					double tempPaidAmt = rsPaidDetails.getDouble(2);
					for (int i=0;i<danDetails.size();i++)
					{
						DANSummary tempSummary = new DANSummary();
						tempSummary = (DANSummary) danDetails.get(i);
						if (tempSummary.getDanId().equals(tempDanId))
						{
							tempSummary.setAmountPaid(tempPaidAmt);
							danDetails.set(i, tempSummary);
							break;
						}
						tempSummary=null;
					}
				}

				rsDanDetails.close();
				rsDanDetails=null;
				rsPaidDetails.close();
				rsPaidDetails=null;
				getDanDetailsStmt.close();
				getDanDetailsStmt=null;

			}
			else {
				getDanDetailsErr=getDanDetailsStmt.getString(7);

				getDanDetailsStmt.close();
				getDanDetailsStmt=null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
		   }

		   connection.commit();

		}
		catch (Exception exception)
		{
			//System.out.println(exception.getMessage());

			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage());

		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
	return danDetails;
	}
  
  
  
  public void submitexceptionalNpaUpdaterequest(String memberId, String cgpan, String remarks, String userId, String ssiRefNo)
        throws DatabaseException
    {
        String methodName = "submitexceptionalNpaUpdaterequest";
        Log.log(4, "GMDAO", methodName, "Entered");
        String bankId = memberId.substring(0, 4);
        String zoneId = memberId.substring(4, 8);
        String branchId = memberId.substring(8, 12);
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call funcInsertNpaUpdationRequest(?,?,?,?,?,?,?,?)}");
            callable.registerOutParameter(1, 4);
            callable.setString(2, memberId.substring(0, 4));
            callable.setString(3, memberId.substring(4, 8));
            callable.setString(4, memberId.substring(8, 12));
            callable.setString(5, cgpan);
            callable.setString(6, ssiRefNo);
            callable.setString(7, remarks);
            callable.setString(8, userId);
            callable.registerOutParameter(9, 12);
            callable.execute();
            int errorCode = callable.getInt(1);
            String error = callable.getString(9);
            Log.log(4, "GMDAO", methodName, "error code and error" + errorCode + "," + error);
            if(errorCode == 1)
            {
                Log.log(2, "GMDAO", methodName, error);
                callable.close();
                callable = null;
                throw new DatabaseException(error);
            }
            callable.close();
            callable = null;
        }
        catch(SQLException e)
        {
            Log.log(2, "GMDAO", methodName, e.getMessage());
            Log.logException(e);
            throw new DatabaseException("Unable to insert NPA Update Request details for " + cgpan);
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "GMDAO", methodName, "Exited");
    }

  
  
  
  /**
   * 
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
   public ArrayList displayRequestedForClosureApproval() throws DatabaseException
   {
		DemandAdvice demandAdvice = null ;
		Connection connection = null ;
	//	ResultSet rsDanDetails = null;
	//	ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;

		int getSchemesStatus=0;
		String getDanDetailsErr = "" ;

		ArrayList danDetails=new ArrayList();
    ArrayList danDetails1=new ArrayList();
    ArrayList danDetails2=new ArrayList();

		connection=DBConnection.getConnection(false);

		try {
			danDetails=new ArrayList();

      getDanDetailsStmt=connection.prepareCall("{?= call packGetClosureDetailmod.funcGetClosureDetailmod(?,?,?)}");
		
    
    	//getDanDetailsStmt=connection.prepareCall("select MEM_ZNE_ID from DEMAND_ADVICE_INFO");
			//System.out.println("Prepare Call established");
			getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			getDanDetailsStmt.registerOutParameter(2, Constants.CURSOR);
      getDanDetailsStmt.registerOutParameter(3, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus=getDanDetailsStmt.getInt(1);
      if(getSchemesStatus==Constants.FUNCTION_FAILURE){

				String error = getDanDetailsStmt.getString(4);

				getDanDetailsStmt.close();
				getDanDetailsStmt=null;

				connection.rollback();

				throw new DatabaseException(error);
			}
			else {
      
		    ResultSet	rsDanDetails=(ResultSet) getDanDetailsStmt.getObject(2);
				
				while (rsDanDetails.next())
				{
				  DANSummary	danSummary = new DANSummary() ;
          danSummary.setMemberId(rsDanDetails.getString(1));
      //    System.out.println("MemberId:"+rsDanDetails.getString(1));
          danSummary.setCgpan(rsDanDetails.getString(2));
       //  System.out.println("CGPAN:"+rsDanDetails.getString(2));
          danSummary.setClosureDate(rsDanDetails.getDate(3));
      //   System.out.println("Closure Date:"+rsDanDetails.getDate(3));
          danSummary.setReason(rsDanDetails.getString(4));   
      //    System.out.println("Remarks:"+rsDanDetails.getString(4));
          danSummary.setAmountDue(rsDanDetails.getDouble(5));
      //    System.out.println("Current SFee:"+rsDanDetails.getDouble(5));
          danSummary.setAmountBengPaid(rsDanDetails.getDouble(6));
      //    System.out.println("Est Amt:"+rsDanDetails.getDouble(6));
  				danDetails1.add(danSummary);
         	//	danSummary = null;
				}
				rsDanDetails.close();
				rsDanDetails=null;
     //   System.out.println("Before exeeute Second cursor entered");
        ResultSet  rsPaidDetails=(ResultSet) getDanDetailsStmt.getObject(3);
		//		System.out.println("Second cursor entered");
				while (rsPaidDetails.next())
				{
				  DANSummary	danSummary1 = new DANSummary() ;
          danSummary1.setMemberId(rsPaidDetails.getString(1));
      //   System.out.println("MemberId:"+rsPaidDetails.getString(1));
          danSummary1.setCgpan(rsPaidDetails.getString(2));
      //   System.out.println("CGPAN:"+rsPaidDetails.getString(2));
          danSummary1.setClosureDate(rsPaidDetails.getDate(3));
      //    System.out.println("Closure Date:"+rsPaidDetails.getDate(3));
          danSummary1.setReason(rsPaidDetails.getString(4));          
          danSummary1.setAmountDue(rsPaidDetails.getDouble(5));
          danSummary1.setAmountBengPaid(rsPaidDetails.getDouble(6));
  				danDetails2.add(danSummary1);
         
				//	danSummary = null;
				}
        
     //   rsDanDetails.close();
		//		rsDanDetails=null;
      
				rsPaidDetails.close();
				rsPaidDetails=null;     	      
       
       getDanDetailsStmt.close();
			 getDanDetailsStmt=null;
       
			}
		   danDetails.add(0,danDetails1);
       danDetails.add(1,danDetails2);
		   connection.commit();

		}
		catch (Exception exception)
		{
		//	System.out.println(exception.getMessage());

			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage());

		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
	return danDetails;
	}
  
  
  /**
   * 
   * @param cgpan
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */  
  public DANSummary getRequestedForClosureApplication(String cgpan) throws DatabaseException
   {
		
		Connection connection = null ;
		ResultSet rsDanDetails = null;
		ResultSet rsPaidDetails = null;
		CallableStatement getDanDetailsStmt;
    DANSummary danSummary = null;
		int getSchemesStatus=0;
		String getDanDetailsErr = "" ;
	  connection=DBConnection.getConnection(false);

		try {
			
      getDanDetailsStmt=connection.prepareCall("{?= call packGetClosureDetail.funcGetCgpanClosureDetaiNew(?,?,?)}");

    	getDanDetailsStmt.registerOutParameter(1, java.sql.Types.INTEGER);
      getDanDetailsStmt.setString(2,cgpan);
			getDanDetailsStmt.registerOutParameter(3, Constants.CURSOR);
			getDanDetailsStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			getDanDetailsStmt.execute();

			getSchemesStatus=getDanDetailsStmt.getInt(1);
			if (getSchemesStatus==0) {
				rsDanDetails=(ResultSet) getDanDetailsStmt.getObject(3);
			
				while (rsDanDetails.next())
				{
					danSummary = new DANSummary() ;
          danSummary.setMemberId(rsDanDetails.getString(1));
       //  System.out.println("MemberId:"+rsDanDetails.getString(1));
          danSummary.setClosureDate(rsDanDetails.getDate(2));
        //  System.out.println("Closure Date:"+rsDanDetails.getDate(2));
          danSummary.setReason(rsDanDetails.getString(3));   
          danSummary.setAmountDue(rsDanDetails.getDouble(4));
          danSummary.setAmountBengPaid(rsDanDetails.getDouble(5));
          danSummary.setDanId(rsDanDetails.getString(6));
      		// danSummary = null;
				}				

				rsDanDetails.close();
				rsDanDetails=null;
			
      	getDanDetailsStmt.close();
				getDanDetailsStmt=null;

			}
			else {
				getDanDetailsErr=getDanDetailsStmt.getString(4);

				getDanDetailsStmt.close();
				getDanDetailsStmt=null;

				connection.rollback();

				throw new DatabaseException(getDanDetailsErr);
		   }

		   connection.commit();

		}
		catch (Exception exception)
		{
			//System.out.println(exception.getMessage());

			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage());

		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
	return danSummary;
	}
  
  /**
   * ADDED BY SUKUMAR@PATH FOR INSERTING DAN DETAILS FOR CLOSURE APPLICATIONS REQUESTED BY MLI
   * @param danNo
   * @param cgpan
   * @param danSummary
   * @param user
   * @param connection
   * @throws com.cgtsi.common.DatabaseException
   */
   public void insertDanDetailsForClosure(String danNo,String cgpan,
			DANSummary danSummary, User user, Connection connection) throws DatabaseException
	  {
		   String methodName = "insertDanDetailsForClosure" ;
		   //Connection connection = null ;
		   CallableStatement insertDanDetailsForClosureStmt = null ;
		   SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
  
  	   int updateStatus = 0 ;
		   //String paymentId = "";
		   String formatedDate = "" ;
		   String userId = "" ;

		   java.util.Date utilDate;
		   java.sql.Date sqlDate;
		   boolean newConn = false;

		   try {
				if (connection == null)
				{
					connection=DBConnection.getConnection();
					newConn=true;
				}
        insertDanDetailsForClosureStmt = connection.prepareCall(
						   "{?= call funcInsDanDetForClos(?,?,?,?,?,?,?,?,?,?,?)}");
				insertDanDetailsForClosureStmt.registerOutParameter(1, Types.INTEGER);
    
    		insertDanDetailsForClosureStmt.setString(2, danNo) ;
     //   System.out.println("DAN:-"+danNo);
				insertDanDetailsForClosureStmt.setString(3, cgpan) ;
     //  System.out.println("CGPAN:-"+cgpan);
				insertDanDetailsForClosureStmt.setDouble(4, danSummary.getAmountDue()) ;
     //   System.out.println("Current ASF :"+danSummary.getAmountDue());
				insertDanDetailsForClosureStmt.setDouble(5, danSummary.getAmountBeingPaid()) ;
    //    System.out.println("Est ASF:"+danSummary.getAmountBeingPaid());
				insertDanDetailsForClosureStmt.setString(6, danSummary.getReason()) ;
    //    System.out.println("Remarks:"+danSummary.getReason());
					utilDate = danSummary.getClosureDate();
				//formatedDate=dateFormat.format(utilDate);
				//sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate)) ;
// Modified by pradeep for new server on 16.07.2012
				 sqlDate=new java.sql.Date(utilDate.getTime());
        insertDanDetailsForClosureStmt.setDate(7, sqlDate) ;
     //   System.out.println("Closure Date:"+sqlDate);
        insertDanDetailsForClosureStmt.setString(8, danSummary.getMemberId().substring(0,4)) ;
        insertDanDetailsForClosureStmt.setString(9, danSummary.getMemberId().substring(4,8)) ;
        insertDanDetailsForClosureStmt.setString(10, danSummary.getMemberId().substring(8,12)) ;
    //    System.out.println("Member Id:"+danSummary.getMemberId().substring(0,4)+danSummary.getMemberId().substring(4,8)+danSummary.getMemberId().substring(8,12));
				userId = user.getUserId() ;
				insertDanDetailsForClosureStmt.setString(11, userId) ;
   //    System.out.println("User Id:"+ userId);
	      insertDanDetailsForClosureStmt.registerOutParameter(12, Types.VARCHAR);
				insertDanDetailsForClosureStmt.executeQuery();

				updateStatus=insertDanDetailsForClosureStmt.getInt(1);
   //     System.out.println("Return Value:"+updateStatus);
				String error=insertDanDetailsForClosureStmt.getString(12);
  //      System.out.println("Error:"+error);
				
				if(updateStatus==Constants.FUNCTION_FAILURE)
				{
					insertDanDetailsForClosureStmt.close();
					insertDanDetailsForClosureStmt=null;
          connection.rollback();
					Log.log(Log.ERROR,"GMDAO",methodName,error);

					throw new DatabaseException(error);
				}
				
			insertDanDetailsForClosureStmt.close();
			insertDanDetailsForClosureStmt=null;
      connection.commit();
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

			Log.log(Log.INFO,"GMDAO",methodName,"Exited");
	  }

 /**
   * 
   * @param cgpan
   * @param danSummary
   * @param user
   * @throws com.cgtsi.common.DatabaseException
   */
public void updateApplicationStatusForClosedCases(String cgpan,DANSummary danSummary,User user)throws DatabaseException
{
   
   	Log.log(Log.INFO,"GMDAO","updateApplicationStatusForClosedCases","Entered");
		Connection connection = DBConnection.getConnection();
	   	CallableStatement updateRepaymentStmt = null;
	   	int updateStatus=0;
   	//value set to return
         SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
         String formatedDate = "" ;
         String formatedDate1 = "";
         java.util.Date utilDate = new java.util.Date();
         java.sql.Date sqlDate ; 
         java.sql.Date sqlDate1 ; 
         String userId = user.getUserId(); ;

		try {
				/*Creates a CallableStatement object for calling database stored
				procedures*/

				updateRepaymentStmt = connection.prepareCall(
							"{?=call funcUpdateAppDetForClos(?,?,?,?,?)}");
  			updateRepaymentStmt.registerOutParameter(1,
													java.sql.Types.INTEGER);
				updateRepaymentStmt.setString(2,danSummary.getReason());
      // formatedDate=dateFormat.format(danSummary.getClosureDate());
    	// sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate)) ; Modified by pradeep for new server on 16.07.2012
    	 sqlDate=new java.sql.Date(danSummary.getClosureDate().getTime()) ;
        // System.out.println("2 danSummary.getClosureDate():"+sqlDate);
				updateRepaymentStmt.setDate(3,sqlDate);
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getRepaymentAmount());
				updateRepaymentStmt.setString(4,userId);
        updateRepaymentStmt.setString(5,cgpan);
     		updateRepaymentStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
    		updateRepaymentStmt.executeQuery();
    		updateStatus=Integer.parseInt(updateRepaymentStmt.getObject(1).toString());

				String error = updateRepaymentStmt.getString(6);

				if (updateStatus==Constants.FUNCTION_SUCCESS) {
			    updateRepaymentStmt.close();
			  	updateRepaymentStmt = null;
				  connection.commit();
		  		Log.log(Log.DEBUG,"GMDAO","updateApplicationStatusForClosedCases","success-SP");
				}
				else if (updateStatus==Constants.FUNCTION_FAILURE) {
					updateRepaymentStmt.close();
					updateRepaymentStmt = null;
					Log.log(Log.ERROR,"GMDAO","updateApplicationStatusForClosedCases","Error : "+error);
					connection.rollback();
					throw new DatabaseException(error);
				}
				
			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			 }			 
			 finally{
			 	DBConnection.freeConnection(connection);
			 }

		Log.log(Log.INFO,"GMDAO","updateApplicationStatusForClosedCases","Exited");
  
}
   

/*
  public void updateApplicationStatusForClosedCases(String cgpan,DANSummary danSummary,User user)throws DatabaseException
{

         PreparedStatement sanctiondAmountStatement = null;
        
         Connection connection           = DBConnection.getConnection();
         SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
         String formatedDate = "" ;
         String formatedDate1 = "";
         java.util.Date utilDate = new java.util.Date();
         java.sql.Date sqlDate ; 
         java.sql.Date sqlDate1 ; 
         String userId = "" ;
          try{
  
           String query = " UPDATE APPLICATION_DETAIL SET APP_STATUS='CL', "+
                        " APP_REMARKS=APP_REMARKS||'MLI CLOSURE REMARKS- '||?||' ACCOUNT HAS BEEN CLOSED ON '||?||'CLOSURE APPROVED ON'||?, "+
                         " APP_CLOSURE_DATE = ?,APP_CREATED_MODIFIED_BY=? WHERE CGPAN=? ";  
             //            System.out.println("Query:"+query);
               sanctiondAmountStatement = connection.prepareStatement(query);
               sanctiondAmountStatement.setString(1,danSummary.getReason());
           //    System.out.println("1 Reason:"+danSummary.getReason());
               	formatedDate=dateFormat.format(danSummary.getClosureDate());
                
				        sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate)) ;  
           //     System.out.println("2 danSummary.getClosureDate():"+sqlDate);
                sanctiondAmountStatement.setDate(2,sqlDate);
                utilDate = new Date() ;
                formatedDate1=dateFormat.format(utilDate);
                sqlDate1=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate1)) ; 
                sanctiondAmountStatement.setDate(3,sqlDate1);
           //     System.out.println("3 CLOSURE APPROVED ON "+ sqlDate1);
                sanctiondAmountStatement.setDate(4,sqlDate);
           //     System.out.println("4 APP_CLOSURE_DATE "+sqlDate);
                userId = user.getUserId();
                sanctiondAmountStatement.setString(5,userId);
          //      System.out.println("User Id:"+userId);
                sanctiondAmountStatement.setString(6,cgpan);
          //      System.out.println("CGPAN:"+cgpan);
                int i = sanctiondAmountStatement.executeUpdate(); 
           //     System.out.println("i:"+i);
               sanctiondAmountStatement.close();
               sanctiondAmountStatement = null;
                if(i==Constants.FUNCTION_FAILURE)
                {	
			              connection.rollback();	
			              throw new DatabaseException("UNABLE TO UPDATE THE APPLICATION DETAIL FOR "+cgpan);	
		            }
		        connection.commit();
          }catch(Exception exception)
            {
              Log.logException(exception);
        //      System.out.println(exception);
              throw new DatabaseException(exception.getMessage());
            }
            finally
            {
              DBConnection.freeConnection(connection);
            }
         
}


  */
  
/**
   * 
   * @param cgpan
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
  
	public Repayment getRepaymentDetailsForCgpan(String cgpan)  throws DatabaseException 
	 {
		 Log.log(Log.INFO,"GMDAO","getRepaymentDetailsForCgpan","Entered");
		 Connection connection = DBConnection.getConnection();
		 CallableStatement getRepaymentDetailStmt = null;
		 String exception = null;
		 ResultSet resultSet;
		 Repayment repayment =null;
		 ArrayList listOfRepaymentAmount = new ArrayList();
		 
		 	
		 try
		 {
			getRepaymentDetailStmt=connection.prepareCall("{?=call packGetDtlsforRepayment.funcGetDtlsforCGPAN(?,?,?)}");
			getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getRepaymentDetailStmt.setString(2,cgpan);
			getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
			getRepaymentDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;
	
			getRepaymentDetailStmt.executeQuery() ;
	
			exception = getRepaymentDetailStmt.getString(4) ;
			Log.log(Log.DEBUG,"GMDAO","getRepaymentDetailsForCgpan","exception for"+cgpan+"-->"+exception);
			
			int error=getRepaymentDetailStmt.getInt(1);
			//Log.log(Log.DEBUG,"GMDAO","getRepaymentDetailsForCgpan","errorCode "+error);
				
			if(error==Constants.FUNCTION_FAILURE)
			{
				getRepaymentDetailStmt.close();
				getRepaymentDetailStmt=null;
				connection.rollback();
				Log.log(Log.ERROR,"GMDAO","getRepaymentDetailsForCgpan","error in SP "+exception);
				throw new DatabaseException(exception);				
			}
	
			resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
	
			while (resultSet.next())
			{
				repayment=new Repayment();
				repayment.setCgpan(resultSet.getString(2));
				//Log.log(Log.DEBUG,"getRepaymentDetailsForCgpan","CGPAN ",": "+repayment.getCgpan());
				repayment.setScheme(resultSet.getString(4));
				//Log.log(Log.DEBUG,"getRepaymentDetailsForCgpan","Scheme"," : "+repayment.getScheme());
				Log.log(Log.DEBUG,"GMDAO","getRepaymentDetailsForCgpan","Repayment added for"+cgpan);
			}
	
			resultSet.close();
			resultSet=null;
			
			getRepaymentDetailStmt.close();
			getRepaymentDetailStmt = null;
	
			getRepaymentDetailStmt = connection.prepareCall("{?=call packGetRepaymentDtls.funcGetRepaymentDtl(?,?,?)}");
			getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getRepaymentDetailStmt.setString(2,cgpan);
			getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
			getRepaymentDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;
	
			getRepaymentDetailStmt.execute() ;
			
			exception = getRepaymentDetailStmt.getString(4) ;
			Log.log(Log.DEBUG,"GMDAO","getRepaymentDetailsForCgpan","exception for"+cgpan+"-->"+exception);			
			
			error=getRepaymentDetailStmt.getInt(1);
			
			if(error==Constants.FUNCTION_FAILURE)
			{
				getRepaymentDetailStmt.close();
				getRepaymentDetailStmt=null;
				Log.log(Log.ERROR,"getRepaymentDetailsForCgpan","Exception ",exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}
			
			resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
			RepaymentAmount repaymentAmount = null;
			while (resultSet.next())
			{
				repaymentAmount = new RepaymentAmount();
	
				repaymentAmount.setCgpan(cgpan);
				repaymentAmount.setRepayId(resultSet.getString(1));
				repaymentAmount.setRepaymentAmount(resultSet.getDouble(2));
				repaymentAmount.setRepaymentDate(resultSet.getDate(3));
				listOfRepaymentAmount.add(repaymentAmount);
				
			}
			if (listOfRepaymentAmount.size()==0)
			{
//				repayment=null; 
			}
			else
			{
				repayment.setRepaymentAmounts(listOfRepaymentAmount);
			}

			Log.log(Log.DEBUG,"GMDAO","getRepaymentDetailsForCgpan","Repayment Amounts added for"+cgpan);			
			
			resultSet.close();
			resultSet=null;
			  
			getRepaymentDetailStmt.close();
			getRepaymentDetailStmt=null;
 	    }
		catch(Exception e)                  
		{
			Log.logException(e);
			throw new DatabaseException(e.getMessage());
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getRepaymentDetailsForCgpan","Exited");		 
		return repayment;
	 } 

	 //Fix Completed

   /**
    * This method updates the Repayment details in the database. It takes an object
    * of type RepaymentAmount as an argument. This object is passed from the GMProcessor
    * class.
    * @param repaymentAmount
    * @param userId
    * @return Boolean
    * @roseuid 397AD89D0161
    */
   public boolean insertRepaymentDetails(RepaymentAmount repaymentAmount,
   					 	String userId)	throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMDAO","insertRepaymentDetails","Entered");
		Connection connection = DBConnection.getConnection();
	   	CallableStatement updateRepaymentStmt = null;
	   	int updateStatus=0;

       	//indicates whether the stored procedure was executed successfully
	   	boolean updateRepaymentStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		if(repaymentAmount !=null) {
			try {
				/*Creates a CallableStatement object for calling database stored
				procedures*/

				updateRepaymentStmt = connection.prepareCall(
							"{?=call funcInsertRepayDetailForCGPAN(?,?,?,?,?)}");


				updateRepaymentStmt.registerOutParameter(1,
													java.sql.Types.INTEGER);
				updateRepaymentStmt.setString(2,repaymentAmount.getCgpan());
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getCgpan());

				updateRepaymentStmt.setDouble(3,repaymentAmount.getRepaymentAmount());
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getRepaymentAmount());

				utilDate=repaymentAmount.getRepaymentDate();
				sqlDate = new java.sql.Date(utilDate.getTime());
/*				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
				updateRepaymentStmt.setDate(4,sqlDate);
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getRepaymentDate());

				updateRepaymentStmt.setString(5,userId);

				updateRepaymentStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

				updateRepaymentStmt.executeQuery();

				updateStatus=Integer.parseInt(
								updateRepaymentStmt.getObject(1).toString());

				String error = updateRepaymentStmt.getString(6);

				if (updateStatus==Constants.FUNCTION_SUCCESS) {
					updateRepaymentStatus =true;
					Log.log(Log.DEBUG,"GMDAO","insertRepaymentDetails","success-SP");
				}
				else if (updateStatus==Constants.FUNCTION_FAILURE) {
					updateRepaymentStatus =false;
					updateRepaymentStmt.close();
					updateRepaymentStmt = null;
					Log.log(Log.ERROR,"GMDAO","insertRepaymentDetails","Error : "+error);
					connection.rollback();
					throw new DatabaseException(error);
				}
				updateRepaymentStmt.close();
				updateRepaymentStmt = null;
				connection.commit();
				
			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			 }			 
			 finally{
			 	DBConnection.freeConnection(connection);
			 }
		}
		Log.log(Log.INFO,"GMDAO","insertRepaymentDetails","Exited");
		return updateRepaymentStatus;
   }


   public boolean updateRepaymentDetails(RepaymentAmount modifiedRepaymentAmount,
						String userId)	throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMDAO","updateRepaymentDetails","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement updateRepaymentStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateRepaymentStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		if(modifiedRepaymentAmount !=null)
		{
			try
			{
				/*Creates a CallableStatement object for calling database stored
				procedures*/

				updateRepaymentStmt = connection.prepareCall(
							"{?=call funcUpdRepayment(?,?,?,?,?)}");

				updateRepaymentStmt.registerOutParameter(1,
													java.sql.Types.INTEGER);

				updateRepaymentStmt.setString(2,modifiedRepaymentAmount.getRepayId());

				//updateRepaymentStmt.setString(3,modifiedRepaymentAmount.getCgpan());
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getCgpan());

				updateRepaymentStmt.setDouble(3,modifiedRepaymentAmount.getRepaymentAmount());
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getRepaymentAmount());


				utilDate=modifiedRepaymentAmount.getRepaymentDate();
				sqlDate = new java.sql.Date(utilDate.getTime());
/*				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
				updateRepaymentStmt.setDate(4,sqlDate);
				//System.out.println("Repay Dtl in dao " +repaymentAmount.getRepaymentDate());

				updateRepaymentStmt.setString(5,userId);
				updateRepaymentStmt.registerOutParameter(6, java.sql.Types.VARCHAR);

				updateRepaymentStmt.executeQuery();

				updateStatus=Integer.parseInt(
								updateRepaymentStmt.getObject(1).toString());

				String error = updateRepaymentStmt.getString(6);

				if (updateStatus==Constants.FUNCTION_SUCCESS) {
					updateRepaymentStatus =true;
					Log.log(Log.DEBUG,"GMDAO","updateRepaymentDetails","SP success");
				}
				else if (updateStatus==Constants.FUNCTION_FAILURE) {
					updateRepaymentStatus =false;
					updateRepaymentStmt.close();
					updateRepaymentStmt = null;
					Log.log(Log.ERROR,"GMDAO","updateRepaymentDetails","Error "+error);
					connection.rollback() ;
					throw new DatabaseException(error);
				}
				updateRepaymentStmt.close();
				updateRepaymentStmt = null;
				connection.commit() ;

			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}
				throw new DatabaseException(exception.getMessage());
			 }finally{
				DBConnection.freeConnection(connection);
			 }
		}
		Log.log(Log.INFO,"GMDAO","updateRepaymentDetails","Exited");
		return updateRepaymentStatus;
   }



/**
   * 
   * @param memberId
   * @param cgpan
   * @param startDate
   * @param closureRemarks
   * @param userId
   * @throws com.cgtsi.common.DatabaseException
   */

	public void submitClosureDetails(String memberId,String cgpan,java.sql.Date startDate,String closureRemarks,String userId) throws DatabaseException
	{
		String methodName = "submitClosureDetails" ;

		Log.log(Log.INFO, "GMDAO", methodName, "Entered") ;
    String bankId = memberId.substring(0,4);
    String zoneId = memberId.substring(4,8);
    String branchId = memberId.substring(8,12);

		Connection connection=DBConnection.getConnection();
		try
		{
			CallableStatement callable=connection.prepareCall("{?=call " +
				"funcClosureRequest(?,?,?,?,?,?,?,?)}");

			callable.registerOutParameter(1,Types.INTEGER);

			callable.setString(2,bankId);
			callable.setString(3,zoneId);
			callable.setString(4,branchId);
      callable.setString(5,cgpan);
			callable.setDate(6,startDate);
			callable.setString(7,closureRemarks);
			callable.setString(8,userId);
			callable.registerOutParameter(9,Types.VARCHAR);
			callable.execute();
			int errorCode=callable.getInt(1);

			String error=callable.getString(9);
   //   System.out.println("Error:"+error);

			Log.log(Log.DEBUG, "GMDAO", methodName, "error code and error"+errorCode+","+error) ;

			if(errorCode==Constants.FUNCTION_FAILURE)
			{
				Log.log(Log.ERROR, "GMDAO", methodName, error) ;

				callable.close();
				callable=null;
				throw new DatabaseException(error);
			}

			callable.close();
			callable=null;
		}
		catch(SQLException e)
		{
			Log.log(Log.ERROR, "GMDAO", methodName, e.getMessage()) ;

			Log.logException(e);

			throw new DatabaseException("Unable to insert app_closure_request details.");

		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO, "GMDAO", methodName, "Exited") ;
	}



   /**
    * This method updates the Disbursement Details entered by the user in the
    * database. It takes an object of type DisbursementAmount as an argument. This object
    * is passed from the GMProcessor class.
    * @param disbursementAmount
    *  @param userId
    * @return Boolean
    * @roseuid 397AD89D0163
    */
   public boolean updateDisbursement(DisbursementAmount disbursement, String userId)
   										throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMDAO","updateDisbursement","Entered");
		Connection connection = DBConnection.getConnection() ;
		CallableStatement updateDisbursementStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateDisbursementStatus = false;
		//value set to return
		String error = null;
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		if(disbursement!=null) {
			try	{

				updateDisbursementStmt= connection.prepareCall(
							"{?=call funcUpdateDBRDetail(?,?,?,?,?,?)}");

				updateDisbursementStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				updateDisbursementStmt.setString(2,disbursement.getDisbursementId());
				updateDisbursementStmt.setDouble(3,disbursement.getDisbursementAmount());

				utilDate = disbursement.getDisbursementDate() ;
				sqlDate = new java.sql.Date(utilDate.getTime());
/*				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
				updateDisbursementStmt.setDate(4,sqlDate);
				updateDisbursementStmt.setString(5,disbursement.getFinalDisbursement());

				updateDisbursementStmt.setString(6,userId);
				updateDisbursementStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				updateDisbursementStmt.executeQuery();
				updateStatus=updateDisbursementStmt.getInt(1);
				error = updateDisbursementStmt.getString(7);

				if (updateStatus==Constants.FUNCTION_SUCCESS) {
					updateDisbursementStatus =true;
					Log.log(Log.DEBUG,"GMDAO","updatedisbursement","SP Success");
				}
				else if (updateStatus==Constants.FUNCTION_FAILURE) {
					updateDisbursementStatus = false;
					updateDisbursementStmt.close();
					updateDisbursementStmt = null;
					Log.log(Log.ERROR,"GMDAO","updateDisbursement","Error "+ error);
					connection.rollback() ;
					throw new DatabaseException(error);
				}
				updateDisbursementStmt.close();
				updateDisbursementStmt = null;
				connection.commit() ;

			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			 }
			 finally {
			 	DBConnection.freeConnection(connection);
			 }
		}
		Log.log(Log.INFO,"GMDAO","updateDisbursement","Exited");
		return updateDisbursementStatus ;
   }


   public boolean insertDisbursement(DisbursementAmount disbursement, String userId)
										throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMDAO","insertDisbursement","Entered");
    System.out.println("GM DAO insertDisbursement Entered");
		Connection connection = DBConnection.getConnection() ;
		CallableStatement updateDisbursementStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateDisbursementStatus = false;
		//value set to return
		String error = null;
		java.util.Date utilDate;
		java.sql.Date sqlDate;

		if(disbursement!=null) {
			try	{

				updateDisbursementStmt= connection.prepareCall(
							"{?=call funcInsertDisbursementDtl(?,?,?,?,?,?)}");

				updateDisbursementStmt.registerOutParameter(1, java.sql.Types.INTEGER);
				updateDisbursementStmt.setString(2,disbursement.getCgpan());
        System.out.println("CGPAN:"+disbursement.getCgpan());
				updateDisbursementStmt.setDouble(3,disbursement.getDisbursementAmount());
        System.out.println("Disbursement Amount:"+disbursement.getDisbursementAmount());
				utilDate = disbursement.getDisbursementDate() ;
				sqlDate = new java.sql.Date(utilDate.getTime());
/*				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
				updateDisbursementStmt.setDate(4,sqlDate);
        System.out.println("Disbursement Date:"+sqlDate);
				updateDisbursementStmt.setString(5,disbursement.getFinalDisbursement());

				updateDisbursementStmt.setString(6,userId);
				updateDisbursementStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

				updateDisbursementStmt.executeQuery();
				updateStatus=updateDisbursementStmt.getInt(1);
    //    System.out.println("Result:"+updateStatus);
				error = updateDisbursementStmt.getString(7);
    //    System.out.println("Error:"+error);
				if (updateStatus==Constants.FUNCTION_SUCCESS) {
					updateDisbursementStatus =true;
					Log.log(Log.DEBUG,"GMDAO","insertdisbursement","SP Success");
				}
				else if (updateStatus==Constants.FUNCTION_FAILURE) {
					updateDisbursementStatus = false;
					updateDisbursementStmt.close();
					updateDisbursementStmt = null;
					Log.log(Log.ERROR,"GMDAO","updateDisbursement","Exception "+error);
					connection.rollback() ;
					throw new DatabaseException(error);
				}
				updateDisbursementStmt.close();
				updateDisbursementStmt = null;
				connection.commit() ;

			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			 }
			 finally {
				DBConnection.freeConnection(connection);
			 }
		}
		Log.log(Log.INFO,"GMDAO","updateDisbursement","Exited");
		return updateDisbursementStatus ;
   }




   /**
    * This method is to enter the NPA details of a borrower. An arraylist of objects
    * of type NPADetails, RecoveryAction and LegalSuitDetails will be passed as an
    * argument.
    * @param npaDetails
    * @param RecoveryProcedure
    * @param LegalSuitDetails
    * @return Boolean
    * @roseuid 397AD89D0165
    */
   public boolean insertNPADetails(NPADetails npaDetails,Vector tcVector,Vector wcVector,Map securityMap)throws DatabaseException
   {

		Log.log(Log.INFO,"GMDAO","insertNPADetails","Entered");
		Connection connection = DBConnection.getConnection(false);
		CallableStatement addNPADetailsStmt = null;
		int updateStatus=0;

		
		boolean addNPADetailsStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		String npaId = null;
             
		String npaexception = null;


		try
		{
			sqlDate = new java.sql.Date(0);
			utilDate = new java.util.Date();

			/*Creates a CallableStatement object for calling
			database stored procedures*/

			addNPADetailsStmt = connection.prepareCall(
						"{?=call funcInsertNPADtlNew(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			addNPADetailsStmt.registerOutParameter(1,java.sql.Types.INTEGER);
			addNPADetailsStmt.registerOutParameter(16,java.sql.Types.VARCHAR);

			addNPADetailsStmt.setString(2,npaDetails.getCgbid());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","2----------------"+npaDetails.getCgbid());

			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
			String formatedDate=null;
			utilDate = npaDetails.getNpaDate();
			if(utilDate!= null)
			{
				sqlDate = new java.sql.Date(utilDate.getTime());
				addNPADetailsStmt.setDate(3,sqlDate);
			}else
			{
				addNPADetailsStmt.setDate(3,null);
			}
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","3----------------"+sqlDate);

			addNPADetailsStmt.setString(4,npaDetails.getIsAsPerRBI());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","4----------------"+npaDetails.getIsAsPerRBI());
	
                        addNPADetailsStmt.setString(5,npaDetails.getNpaConfirm());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","5----------------");
      
			addNPADetailsStmt.setString(6,npaDetails.getNpaReason());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","6----------------"+npaDetails.getNpaReason());
 
			addNPADetailsStmt.setString(7,npaDetails.getEffortsTaken());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","7----------------"+npaDetails.getEffortsTaken());
 
			addNPADetailsStmt.setString(8,npaDetails.getIsAcctReconstructed());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","8----------------"+npaDetails.getIsAcctReconstructed());
 
   			addNPADetailsStmt.setString(9,npaDetails.getSubsidyFlag());
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","9----------------"+npaDetails.getSubsidyFlag());

                        addNPADetailsStmt.setString(10,npaDetails.getIsSubsidyRcvd());
                        Log.log(Log.DEBUG,"GMDAO","insertNPADetails","10----------------"+npaDetails.getIsSubsidyRcvd());
                        
                        addNPADetailsStmt.setString(11,npaDetails.getIsSubsidyAdjusted());
                        Log.log(Log.DEBUG,"GMDAO","insertNPADetails","11----------------"+npaDetails.getIsSubsidyAdjusted());

                        addNPADetailsStmt.setDouble(12,npaDetails.getSubsidyLastRcvdAmt());
                        Log.log(Log.DEBUG,"GMDAO","insertNPADetails","12----------------"+npaDetails.getSubsidyLastRcvdAmt());

			utilDate = npaDetails.getSubsidyLastRcvdDt();
			if(utilDate!=null && !utilDate.toString().equals(""))
			{
				sqlDate = new java.sql.Date(utilDate.getTime());
				addNPADetailsStmt.setDate(13,sqlDate);
			}else
			{
				addNPADetailsStmt.setNull(13,java.sql.Types.DATE);
			}
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","13----------------"+sqlDate);

                      utilDate = npaDetails.getLastInspectionDt();
 			if(utilDate!=null && !utilDate.toString().equals(""))
 			{
 				sqlDate = new java.sql.Date(utilDate.getTime());
 				addNPADetailsStmt.setDate(14,sqlDate);
 			}else
 			{
 				addNPADetailsStmt.setNull(14,java.sql.Types.DATE);
 			}
			
			addNPADetailsStmt.registerOutParameter(15, java.sql.Types.VARCHAR);
			
			addNPADetailsStmt.executeQuery();

			updateStatus=addNPADetailsStmt.getInt(1);
			npaexception = addNPADetailsStmt.getString(16);
 

			npaId = addNPADetailsStmt.getString(15);
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","22----------------"+npaId);
  
			if (updateStatus==Constants.FUNCTION_SUCCESS)
			{
				addNPADetailsStatus = true;
				Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

			}
			else if (updateStatus==Constants.FUNCTION_FAILURE)
			{
				connection.rollback();
				addNPADetailsStatus =false;
				addNPADetailsStmt.close();
				addNPADetailsStmt = null;
				Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
				throw new DatabaseException(npaexception);
			}

			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","npa id befor setng to npa obj"+npaId);
			npaDetails.setNpaId(npaId);
			Log.log(Log.DEBUG,"GMDAO","insertNPADetails","npa id sfter setng to npa obj"+npaDetails.getNpaId());
		//	addNPADetailsStmt.close();
		//	addNPADetailsStmt = null;

			
                        
                        //insert tcdetails
                        
                         
                        for(int i=0;i<tcVector.size();i++){
                            Map map = (java.util.Map)tcVector.get(i);
                            
                            addNPADetailsStmt = connection.prepareCall("{?=call funcInsertNPATLDetails(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                            addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                            addNPADetailsStmt.registerOutParameter(14,Types.VARCHAR);
                            
                            addNPADetailsStmt.setString(2,npaId);
                            addNPADetailsStmt.setString(3,(String)map.get("CGPAN"));
                            Date d = (Date)map.get("FIRST_DISB_DT");
                            addNPADetailsStmt.setDate(4,new java.sql.Date(d.getTime()));
                            d = (Date)map.get("LAST_DISB_DT");
                            addNPADetailsStmt.setDate(5,new java.sql.Date(d.getTime()));
                            d = (Date)map.get("FIRST_INST_DT");
                            addNPADetailsStmt.setDate(6,new java.sql.Date(d.getTime()));
                            addNPADetailsStmt.setDouble(7,(Double)map.get("PRINCIPAL_REPAY"));
                            addNPADetailsStmt.setDouble(8,(Double)map.get("INTEREST_REPAY"));
                            addNPADetailsStmt.setInt(9,(Integer)map.get("PRINCIPAL_MORATORIUM"));
                            addNPADetailsStmt.setInt(10,(Integer)map.get("INTEREST_MORATORIUM"));
                            addNPADetailsStmt.setDouble(11,(Double)map.get("TOTAL_DISB_AMT"));                           
                            addNPADetailsStmt.setDouble(12,(Double)map.get("PRINCIPAL_OS"));
                            addNPADetailsStmt.setDouble(13,(Double)map.get("INTEREST_OS"));
                            
                            addNPADetailsStmt.executeQuery();
                            updateStatus=addNPADetailsStmt.getInt(1);
                            npaexception = addNPADetailsStmt.getString(14);
                            
                            if (updateStatus==Constants.FUNCTION_SUCCESS)
                            {
                                    addNPADetailsStatus = true;
                                    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

                            }
                            else if (updateStatus==Constants.FUNCTION_FAILURE)
                            {
                                    connection.rollback();
                                    addNPADetailsStatus =false;
                                    addNPADetailsStmt.close();
                                    addNPADetailsStmt = null;
                                    Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                       
                                    throw new DatabaseException(npaexception);
                            }
                        }
                        
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
		    //insert wcdetails
                    
                          
                        for(int i=0;i<wcVector.size();i++){
                            Map map = (java.util.Map)wcVector.get(i);
                            
                            addNPADetailsStmt = connection.prepareCall("{?=call funcInsertNPAWCDetails(?,?,?,?,?)}");
                            addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                            addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                            
                            addNPADetailsStmt.setString(2,npaId);
                            addNPADetailsStmt.setString(3,(String)map.get("CGPAN"));
                            addNPADetailsStmt.setDouble(4,(Double)map.get("PRINCIPAL_OS"));
                            addNPADetailsStmt.setDouble(5,(Double)map.get("INTEREST_OS"));
                            
                            addNPADetailsStmt.executeQuery();
                            updateStatus=addNPADetailsStmt.getInt(1);
                            npaexception = addNPADetailsStmt.getString(6);
                            
                            if (updateStatus==Constants.FUNCTION_SUCCESS)
                            {
                                    addNPADetailsStatus = true;
                                    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

                            }
                            else if (updateStatus==Constants.FUNCTION_FAILURE)
                            {
                                    connection.rollback();
                                    addNPADetailsStatus =false;
                                    addNPADetailsStmt.close();
                                    addNPADetailsStmt = null;
                                    Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                            //    System.out.println("Exception "+npaexception);
                                    throw new DatabaseException(npaexception);
                            }
                        }
                        
		  //  addNPADetailsStmt.close();
		   // addNPADetailsStmt = null;
                    
                       //insert security detail
                       
                       Map securityAsOnSancDt = (Map)securityMap.get("securityAsOnSancDt");
                       Map securityAsOnNpaDt = (Map)securityMap.get("securityAsOnNpaDt");
		       Double networthAsOnSancDt = (Double)securityMap.get("networthAsOnSancDt");
		       Double networthAsOnNpaDt = (Double)securityMap.get("networthAsOnNpaDt");
		       String reasonForReductionAsOnNpaDt = (String)securityMap.get("reasonForReductionAsOnNpaDt");
                      
                    Vector securityIds = new Vector();
                       
                    Map securityAsOnSanc = new HashMap();
                        securityAsOnSanc.put("flag","SAN");
                        securityAsOnSanc.put("networth",networthAsOnSancDt);
                        securityAsOnSanc.put("reasonforreduction","NA");
                    
		    Map securityAsOnNpa = new HashMap();
		        securityAsOnNpa.put("flag","NPA");
		        securityAsOnNpa.put("networth",networthAsOnNpaDt);
		        securityAsOnNpa.put("reasonforreduction",reasonForReductionAsOnNpaDt);
                        
                   Vector vector = new Vector();
                   vector.add(securityAsOnSanc);
                   vector.add(securityAsOnNpa);
                    
                       
		   
                    
                    for(int i=0;i<vector.size();i++){
                        Map map = (Map)vector.get(i);
                        
                        addNPADetailsStmt = connection.prepareCall("{?=call funcInsertNPASecDetails(?,?,?,?,?,?)}") ;
                          
                        addNPADetailsStmt.registerOutParameter(1,Types.INTEGER); 
                        addNPADetailsStmt.registerOutParameter(6,Types.INTEGER);
                        addNPADetailsStmt.registerOutParameter(7,Types.VARCHAR);
                        addNPADetailsStmt.setString(2,npaId); 
                        addNPADetailsStmt.setString(3,(String)map.get("flag"));
                        
                        Double networth = (Double)map.get("networth");
                        
                        addNPADetailsStmt.setDouble(4,networth.doubleValue());
                        
                        addNPADetailsStmt.setString(5,(String)map.get("reasonforreduction"));
                        
                        addNPADetailsStmt.executeQuery();
                        updateStatus=addNPADetailsStmt.getInt(1);
                        npaexception = addNPADetailsStmt.getString(7);
                     
                        
                        if (updateStatus==Constants.FUNCTION_SUCCESS)
                        {
                                addNPADetailsStatus = true;
                                Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                              securityIds.add(addNPADetailsStmt.getInt(6));
                                
                        }
                        else if (updateStatus==Constants.FUNCTION_FAILURE)
                        {
                                connection.rollback();
                                addNPADetailsStatus =false;
                                addNPADetailsStmt.close();
                                addNPADetailsStmt = null;
                                Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                                throw new DatabaseException(npaexception);
                        }
                    }
                    
                    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
                    double landval = 0.0;
                    double bldgval = 0.0;
                    double macval = 0.0;
                    double movval = 0.0;
                    double currval = 0.0;
                    double othval = 0.0;
		    Integer id = (Integer)securityIds.get(0);
         //           Integer id = 123;
                    
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                   
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
        	    addNPADetailsStmt.setString(3,"LAND");
                    String landstr = (String)securityAsOnSancDt.get("LAND");
		    Double land = 0.0;
                    if(!GenericValidator.isBlankOrNull(landstr)){
                        land = Double.parseDouble(landstr);
                    }
		    
                    if(land != null || !("".equals(land))){
                        landval = land.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,landval);
		    addNPADetailsStmt.setString(5,"NA");
                    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                   
                   
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
		    
		    if(id != null || !("".equals(id))){
		        addNPADetailsStmt.setInt(2,id.intValue());
		    }
		    addNPADetailsStmt.setString(3,"BUILDING");
		    Double building = 0.0;
                    String buildingstr = (String)securityAsOnSancDt.get("BUILDING");
                    if(!GenericValidator.isBlankOrNull(buildingstr)){
                        building = Double.parseDouble(buildingstr);
                    }
                    
		    if(building != null || !("".equals(building))){
		        bldgval = building.doubleValue();
		    }
		    addNPADetailsStmt.setDouble(4,bldgval);
		    addNPADetailsStmt.setString(5,"NA");
		    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
                    
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
		    
		    if(id != null || !("".equals(id))){
		        addNPADetailsStmt.setInt(2,id.intValue());
		    }
		    addNPADetailsStmt.setString(3,"MACHINE");
		    Double machine = 0.0;
                    String machinestr = (String)securityAsOnSancDt.get("MACHINE");
		    if(!GenericValidator.isBlankOrNull(machinestr)){
		        machine = Double.parseDouble(machinestr);
		    }
                    
		    if(machine != null || !("".equals(machine))){
		        macval = machine.doubleValue();
		    }
		    addNPADetailsStmt.setDouble(4,macval);
		    addNPADetailsStmt.setString(5,"NA");
		    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
                    
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
		    
		    if(id != null || !("".equals(id))){
		        addNPADetailsStmt.setInt(2,id.intValue());
		    }
		    addNPADetailsStmt.setString(3,"OTHER FIXED MOVABLE ASSETS");
		    Double movableassets = 0.0;
                    String movableassetsstr = (String)securityAsOnSancDt.get("OTHER_FIXED_MOVABLE_ASSETS");
		    if(!GenericValidator.isBlankOrNull(movableassetsstr)){
		        movableassets = Double.parseDouble(movableassetsstr);
		    }
                    
		    if(movableassets != null || !("".equals(movableassets))){
		        movval = movableassets.doubleValue();
		    }
		    addNPADetailsStmt.setDouble(4,movval);
		    addNPADetailsStmt.setString(5,"NA");
		    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
                    
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
		    
		    if(id != null || !("".equals(id))){
		        addNPADetailsStmt.setInt(2,id.intValue());
		    }
		    addNPADetailsStmt.setString(3,"CUR_ASSETS");
		    Double currassets = 0.0;
                    String currassetsstr = (String)securityAsOnSancDt.get("CUR_ASSETS");
		    if(!GenericValidator.isBlankOrNull(currassetsstr)){
		        currassets = Double.parseDouble(currassetsstr);
		    }
                    
		    if(currassets != null || !("".equals(currassets))){
		        currval = currassets.doubleValue();
		    }
		    addNPADetailsStmt.setDouble(4,currval);
		    addNPADetailsStmt.setString(5,"NA");
		    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                    
                    
		    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
		    
		    if(id != null || !("".equals(id))){
		        addNPADetailsStmt.setInt(2,id.intValue());
		    }
		    addNPADetailsStmt.setString(3,"OTHERS");
		    Double others = 0.0;
                    String othersstr = (String)securityAsOnSancDt.get("OTHERS");
		    if(!GenericValidator.isBlankOrNull(othersstr)){
		        others = Double.parseDouble(othersstr);
		    }
                    
		    if(others != null || !("".equals(others))){
		        othval = others.doubleValue();
		    }
		    addNPADetailsStmt.setDouble(4,othval);
		    addNPADetailsStmt.setString(5,"NA");
		    
		    addNPADetailsStmt.executeQuery();
		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(6);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
		        
		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		    //    System.out.println("Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;
                   
                   
                   /*AS ON NPA DATE*/
                  id = (Integer)securityIds.get(1);
              //      id = 321;
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"LAND");
                     land = 0.0;
                     landstr = (String)securityAsOnNpaDt.get("LAND");
                     if(!GenericValidator.isBlankOrNull(landstr)){
                        land = Double.parseDouble(landstr);
                     }
                     
                    if(land != null || !("".equals(land))){
                        landval = land.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,landval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"BUILDING");
                     building = 0.0;
                     buildingstr = (String)securityAsOnNpaDt.get("BUILDING");
		    if(!GenericValidator.isBlankOrNull(buildingstr)){
		       building = Double.parseDouble(buildingstr);
		    }
                     
                    if(building != null || !("".equals(building))){
                        bldgval = building.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,bldgval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"MACHINE");
                     machine = 0.0;
                     machinestr = (String)securityAsOnNpaDt.get("MACHINE");
		    if(!GenericValidator.isBlankOrNull(machinestr)){
		       machine = Double.parseDouble(machinestr);
		    }
                     
                    if(machine != null || !("".equals(machine))){
                        macval = machine.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,macval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"OTHER FIXED MOVABLE ASSETS");
                     movableassets = 0.0;
                     movableassetsstr = (String)securityAsOnNpaDt.get("OTHER_FIXED_MOVABLE_ASSETS");
		    if(!GenericValidator.isBlankOrNull(movableassetsstr)){
		       movableassets = Double.parseDouble(movableassetsstr);
		    }
                     
                    if(movableassets != null || !("".equals(movableassets))){
                        movval = movableassets.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,movval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"CUR_ASSETS");
                     currassets = 0.0;
                     currassetsstr = (String)securityAsOnNpaDt.get("CUR_ASSETS");
		    if(!GenericValidator.isBlankOrNull(currassetsstr)){
		       currassets = Double.parseDouble(currassetsstr);
		    }
                     
                    if(currassets != null || !("".equals(currassets))){
                        currval = currassets.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,currval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    
                    
                    addNPADetailsStmt = connection.prepareCall("{?= call funcInsertNPASecParticular(?,?,?,?,?)}");
                    addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                    addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                    
                    if(id != null || !("".equals(id))){
                        addNPADetailsStmt.setInt(2,id.intValue());
                    }
                    addNPADetailsStmt.setString(3,"OTHERS");
                     others = 0.0;
                     othersstr = (String)securityAsOnNpaDt.get("OTHERS");
		    if(!GenericValidator.isBlankOrNull(othersstr)){
		       others = Double.parseDouble(othersstr);
		    }
                     
                    if(others != null || !("".equals(others))){
                        othval = others.doubleValue();
                    }
                    addNPADetailsStmt.setDouble(4,othval);
                    addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                    
                    addNPADetailsStmt.executeQuery();
                    updateStatus=addNPADetailsStmt.getInt(1);
                    npaexception = addNPADetailsStmt.getString(6);
                    
                    if (updateStatus==Constants.FUNCTION_SUCCESS)
                    {
                            addNPADetailsStatus = true;
                            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                        
                    }
                    else if (updateStatus==Constants.FUNCTION_FAILURE)
                    {
                            connection.rollback();
                            addNPADetailsStatus =false;
                            addNPADetailsStmt.close();
                            addNPADetailsStmt = null;
                            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    //    System.out.println("Exception "+npaexception);
                            throw new DatabaseException(npaexception);
                    }
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                   
                   
                   
                   
                    

			connection.commit();
		}catch (Exception exception)
		{
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(exception.getMessage());
		}
		finally
		{
				DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","insertNPADetails","Exited");

		return addNPADetailsStatus;
   	}
        
       
    public boolean updateNPADetails(NPADetails npaDetails,Vector tcVector,Vector wcVector,Map securityMap)throws DatabaseException
    {
        Log.log(Log.INFO,"GMDAO","updateNPADetails","Entered");
        Connection connection = DBConnection.getConnection(false);
        CallableStatement addNPADetailsStmt = null;
        int updateStatus=0;

        
        boolean addNPADetailsStatus = false;
        

        java.util.Date utilDate;
        java.sql.Date sqlDate;
        String npaId = null;
        String npaexception = null;

        try
        {
                sqlDate = new java.sql.Date(0);
                utilDate = new java.util.Date();

                /*Creates a CallableStatement object for calling
                database stored procedures*/

                addNPADetailsStmt = connection.prepareCall(
                                        "{?=call funcUpdateNPADtlMod(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            addNPADetailsStmt.registerOutParameter(1,java.sql.Types.INTEGER);
            addNPADetailsStmt.registerOutParameter(14,java.sql.Types.VARCHAR);

            addNPADetailsStmt.setString(2,npaDetails.getCgbid());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","2----------------"+npaDetails.getCgbid());

            SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
            String formatedDate=null;
            

            addNPADetailsStmt.setString(3,npaDetails.getIsAsPerRBI());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","3----------------"+npaDetails.getIsAsPerRBI());
            
            addNPADetailsStmt.setString(4,npaDetails.getNpaConfirm());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","4----------------");
           
            addNPADetailsStmt.setString(5,npaDetails.getEffortsTaken());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","5----------------"+npaDetails.getEffortsTaken());
            
            addNPADetailsStmt.setString(6,npaDetails.getIsAcctReconstructed());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","6----------------"+npaDetails.getIsAcctReconstructed());
            
            addNPADetailsStmt.setString(7,npaDetails.getSubsidyFlag());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","7----------------"+npaDetails.getSubsidyFlag());

            addNPADetailsStmt.setString(8,npaDetails.getIsSubsidyRcvd());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","8----------------"+npaDetails.getIsSubsidyRcvd());
            
            addNPADetailsStmt.setString(9,npaDetails.getIsSubsidyAdjusted());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","9----------------"+npaDetails.getIsSubsidyAdjusted());

            addNPADetailsStmt.setDouble(10,npaDetails.getSubsidyLastRcvdAmt());
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","10----------------"+npaDetails.getSubsidyLastRcvdAmt());

            utilDate = npaDetails.getSubsidyLastRcvdDt();
            if(utilDate!=null && !utilDate.toString().equals(""))
            {
                    sqlDate = new java.sql.Date(utilDate.getTime());
                    addNPADetailsStmt.setDate(11,sqlDate);
            }else
            {
                    addNPADetailsStmt.setNull(11,java.sql.Types.DATE);
            }
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","11----------------"+sqlDate);

            utilDate = npaDetails.getLastInspectionDt();
            if(utilDate!=null && !utilDate.toString().equals(""))
            {
                    sqlDate = new java.sql.Date(utilDate.getTime());
                    addNPADetailsStmt.setDate(12,sqlDate);
            }else
            {
                    addNPADetailsStmt.setNull(12,java.sql.Types.DATE);
            }
            
            addNPADetailsStmt.registerOutParameter(13, java.sql.Types.VARCHAR);
            
            addNPADetailsStmt.executeQuery();

            updateStatus=addNPADetailsStmt.getInt(1);
            npaexception = addNPADetailsStmt.getString(14);
            

            npaId = addNPADetailsStmt.getString(13);
        //npaId = "123";
            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","13----------------"+npaId);
            
            if (updateStatus==Constants.FUNCTION_SUCCESS)
            {
                    addNPADetailsStatus = true;
                    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

            }
            else if (updateStatus==Constants.FUNCTION_FAILURE)
            {
                    connection.rollback();
                    addNPADetailsStatus =false;
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    throw new DatabaseException(npaexception);
            }

               
            npaDetails.setNpaId(npaId);
            
            addNPADetailsStmt.close();
            addNPADetailsStmt = null;
           
            java.sql.Date disbdate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
           
           
        
            
            
            //insert tcdetails
            
             
            for(int i=0;i<tcVector.size();i++){
                Map map = (java.util.Map)tcVector.get(i);
                
                addNPADetailsStmt = connection.prepareCall("{?=call funcUpdateNPATLDtl(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                addNPADetailsStmt.registerOutParameter(14,Types.VARCHAR);
                
                addNPADetailsStmt.setString(2,npaId);
                addNPADetailsStmt.setString(3,(String)map.get("CGPAN"));
                Date d = (Date)map.get("FIRST_DISB_DT");
                addNPADetailsStmt.setDate(4,new java.sql.Date(d.getTime()));
                d = (Date)map.get("LAST_DISB_DT");
                addNPADetailsStmt.setDate(5,new java.sql.Date(d.getTime()));
                d = (Date)map.get("FIRST_INST_DT");
                addNPADetailsStmt.setDate(6,new java.sql.Date(d.getTime()));
                addNPADetailsStmt.setDouble(7,(Double)map.get("PRINCIPAL_REPAY"));
                addNPADetailsStmt.setDouble(8,(Double)map.get("INTEREST_REPAY"));
                addNPADetailsStmt.setInt(9,(Integer)map.get("PRINCIPAL_MORATORIUM"));
                addNPADetailsStmt.setInt(10,(Integer)map.get("INTEREST_MORATORIUM"));
                addNPADetailsStmt.setDouble(11,(Double)map.get("TOTAL_DISB_AMT"));                           
                addNPADetailsStmt.setDouble(12,(Double)map.get("PRINCIPAL_OS"));
                addNPADetailsStmt.setDouble(13,(Double)map.get("INTEREST_OS"));
                
                addNPADetailsStmt.executeQuery();
                updateStatus=addNPADetailsStmt.getInt(1);
                npaexception = addNPADetailsStmt.getString(14);
                
                if (updateStatus==Constants.FUNCTION_SUCCESS)
                {
                        addNPADetailsStatus = true;
                        Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

                }
                else if (updateStatus==Constants.FUNCTION_FAILURE)
                {
                        connection.rollback();
                        addNPADetailsStatus =false;
                        addNPADetailsStmt.close();
                        addNPADetailsStmt = null;
                        Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
            
                        throw new DatabaseException(npaexception);
                }
            }
            if(addNPADetailsStmt != null){
            addNPADetailsStmt.close();
            addNPADetailsStmt = null;
            }
            //insert wcdetails
            
              
            for(int i=0;i<wcVector.size();i++){
                Map map = (java.util.Map)wcVector.get(i);
                
                addNPADetailsStmt = connection.prepareCall("{?=call funcUpdateNPAWCDtl(?,?,?,?,?)}");
                addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                
                addNPADetailsStmt.setString(2,npaId);
                addNPADetailsStmt.setString(3,(String)map.get("CGPAN"));
                addNPADetailsStmt.setDouble(4,(Double)map.get("PRINCIPAL_OS"));
                addNPADetailsStmt.setDouble(5,(Double)map.get("INTEREST_OS"));
                
                addNPADetailsStmt.executeQuery();
                updateStatus=addNPADetailsStmt.getInt(1);
                npaexception = addNPADetailsStmt.getString(6);
                
                if (updateStatus==Constants.FUNCTION_SUCCESS)
                {
                        addNPADetailsStatus = true;
                        Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

                }
                else if (updateStatus==Constants.FUNCTION_FAILURE)
                {
                        connection.rollback();
                        addNPADetailsStatus =false;
                        addNPADetailsStmt.close();
                        addNPADetailsStmt = null;
                        Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                //    System.out.println("Exception "+npaexception);
                        throw new DatabaseException(npaexception);
                }
                
            }
            if(addNPADetailsStmt != null){
            addNPADetailsStmt.close();
            addNPADetailsStmt = null;
            }
            //addNPADetailsStmt.close();
            //addNPADetailsStmt = null;
            
            //insert security detail
            
            Map securityAsOnSancDt = (Map)securityMap.get("securityAsOnSancDt");
            Map securityAsOnNpaDt = (Map)securityMap.get("securityAsOnNpaDt");
            Double networthAsOnSancDt = (Double)securityMap.get("networthAsOnSancDt");
            Double networthAsOnNpaDt = (Double)securityMap.get("networthAsOnNpaDt");
            String reasonForReductionAsOnNpaDt = (String)securityMap.get("reasonForReductionAsOnNpaDt");
            
            Vector securityIds = new Vector();
            
            Map securityAsOnSanc = new HashMap();
            securityAsOnSanc.put("flag","SAN");
            securityAsOnSanc.put("networth",networthAsOnSancDt);
            securityAsOnSanc.put("reasonforreduction","NA");
            
            Map securityAsOnNpa = new HashMap();
            securityAsOnNpa.put("flag","NPA");
            securityAsOnNpa.put("networth",networthAsOnNpaDt);
            securityAsOnNpa.put("reasonforreduction",reasonForReductionAsOnNpaDt);
            
            Vector vector = new Vector();
            vector.add(securityAsOnSanc);
            vector.add(securityAsOnNpa);
            
            
            
            
            for(int i=0;i<vector.size();i++){
            Map map = (Map)vector.get(i);
            
            addNPADetailsStmt = connection.prepareCall("{?=call funcUpdateNPASecDet(?,?,?,?,?,?)}") ;
              
            addNPADetailsStmt.registerOutParameter(1,Types.INTEGER); 
            addNPADetailsStmt.registerOutParameter(6,Types.INTEGER);
            addNPADetailsStmt.registerOutParameter(7,Types.VARCHAR);
            addNPADetailsStmt.setString(2,npaId); 
            addNPADetailsStmt.setString(3,(String)map.get("flag"));
            
            Double networth = (Double)map.get("networth");
            
            addNPADetailsStmt.setDouble(4,networth.doubleValue());
            
            addNPADetailsStmt.setString(5,(String)map.get("reasonforreduction"));
            
            addNPADetailsStmt.executeQuery();
            updateStatus=addNPADetailsStmt.getInt(1);
            npaexception = addNPADetailsStmt.getString(7);
            
            
            if (updateStatus==Constants.FUNCTION_SUCCESS)
            {
                    addNPADetailsStatus = true;
                    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                  securityIds.add(addNPADetailsStmt.getInt(6));
                    
            }
            else if (updateStatus==Constants.FUNCTION_FAILURE)
            {
                    connection.rollback();
                    addNPADetailsStatus =false;
                    addNPADetailsStmt.close();
                    addNPADetailsStmt = null;
                    Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                    throw new DatabaseException(npaexception);
            }
            }
            if(addNPADetailsStmt != null){
            addNPADetailsStmt.close();
            addNPADetailsStmt = null;
            }
                     
                     
                     
                     
                     double landval = 0.0;
                     double bldgval = 0.0;
                     double macval = 0.0;
                     double movval = 0.0;
                     double currval = 0.0;
                     double othval = 0.0;
                     Integer id = (Integer)securityIds.get(0);
                     //           Integer id = 123;
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"LAND");
                     String landstr = (String)securityAsOnSancDt.get("LAND");
                     Double land = 0.0;
                     if(!GenericValidator.isBlankOrNull(landstr)){
                     land = Double.parseDouble(landstr);
                     }
                     
                     if(land != null || !("".equals(land))){
                     landval = land.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,landval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"BUILDING");
                     Double building = 0.0;
                     String buildingstr = (String)securityAsOnSancDt.get("BUILDING");
                     if(!GenericValidator.isBlankOrNull(buildingstr)){
                     building = Double.parseDouble(buildingstr);
                     }
                     
                     if(building != null || !("".equals(building))){
                     bldgval = building.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,bldgval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"MACHINE");
                     Double machine = 0.0;
                     String machinestr = (String)securityAsOnSancDt.get("MACHINE");
                     if(!GenericValidator.isBlankOrNull(machinestr)){
                     machine = Double.parseDouble(machinestr);
                     }
                     
                     if(machine != null || !("".equals(machine))){
                     macval = machine.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,macval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"OTHER FIXED MOVABLE ASSETS");
                     Double movableassets = 0.0;
                     String movableassetsstr = (String)securityAsOnSancDt.get("OTHER_FIXED_MOVABLE_ASSETS");
                     if(!GenericValidator.isBlankOrNull(movableassetsstr)){
                     movableassets = Double.parseDouble(movableassetsstr);
                     }
                     
                     if(movableassets != null || !("".equals(movableassets))){
                     movval = movableassets.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,movval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"CUR_ASSETS");
                     Double currassets = 0.0;
                     String currassetsstr = (String)securityAsOnSancDt.get("CUR_ASSETS");
                     if(!GenericValidator.isBlankOrNull(currassetsstr)){
                     currassets = Double.parseDouble(currassetsstr);
                     }
                     
                     if(currassets != null || !("".equals(currassets))){
                     currval = currassets.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,currval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"OTHERS");
                     Double others = 0.0;
                     String othersstr = (String)securityAsOnSancDt.get("OTHERS");
                     if(!GenericValidator.isBlankOrNull(othersstr)){
                     others = Double.parseDouble(othersstr);
                     }
                     
                     if(others != null || !("".equals(others))){
                     othval = others.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,othval);
                     addNPADetailsStmt.setString(5,"NA");
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     /*AS ON NPA DATE*/
                     id = (Integer)securityIds.get(1);
                     //      id = 321;
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"LAND");
                     land = 0.0;
                     landstr = (String)securityAsOnNpaDt.get("LAND");
                     if(!GenericValidator.isBlankOrNull(landstr)){
                     land = Double.parseDouble(landstr);
                     }
                     
                     if(land != null || !("".equals(land))){
                     landval = land.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,landval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"BUILDING");
                     building = 0.0;
                     buildingstr = (String)securityAsOnNpaDt.get("BUILDING");
                     if(!GenericValidator.isBlankOrNull(buildingstr)){
                     building = Double.parseDouble(buildingstr);
                     }
                     
                     if(building != null || !("".equals(building))){
                     bldgval = building.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,bldgval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"MACHINE");
                     machine = 0.0;
                     machinestr = (String)securityAsOnNpaDt.get("MACHINE");
                     if(!GenericValidator.isBlankOrNull(machinestr)){
                     machine = Double.parseDouble(machinestr);
                     }
                     
                     if(machine != null || !("".equals(machine))){
                     macval = machine.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,macval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"OTHER FIXED MOVABLE ASSETS");
                     movableassets = 0.0;
                     movableassetsstr = (String)securityAsOnNpaDt.get("OTHER_FIXED_MOVABLE_ASSETS");
                     if(!GenericValidator.isBlankOrNull(movableassetsstr)){
                     movableassets = Double.parseDouble(movableassetsstr);
                     }
                     
                     if(movableassets != null || !("".equals(movableassets))){
                     movval = movableassets.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,movval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"CUR_ASSETS");
                     currassets = 0.0;
                     currassetsstr = (String)securityAsOnNpaDt.get("CUR_ASSETS");
                     if(!GenericValidator.isBlankOrNull(currassetsstr)){
                     currassets = Double.parseDouble(currassetsstr);
                     }
                     
                     if(currassets != null || !("".equals(currassets))){
                     currval = currassets.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,currval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     addNPADetailsStmt = connection.prepareCall("{?= call funcUpdateNPASecParticular(?,?,?,?,?)}");
                     addNPADetailsStmt.registerOutParameter(1,Types.INTEGER);
                     addNPADetailsStmt.registerOutParameter(6,Types.VARCHAR);
                     
                     if(id != null || !("".equals(id))){
                     addNPADetailsStmt.setInt(2,id.intValue());
                     }
                     addNPADetailsStmt.setString(3,"OTHERS");
                     others = 0.0;
                     othersstr = (String)securityAsOnNpaDt.get("OTHERS");
                     if(!GenericValidator.isBlankOrNull(othersstr)){
                     others = Double.parseDouble(othersstr);
                     }
                     
                     if(others != null || !("".equals(others))){
                     othval = others.doubleValue();
                     }
                     addNPADetailsStmt.setDouble(4,othval);
                     addNPADetailsStmt.setString(5,reasonForReductionAsOnNpaDt);
                     
                     addNPADetailsStmt.executeQuery();
                     updateStatus=addNPADetailsStmt.getInt(1);
                     npaexception = addNPADetailsStmt.getString(6);
                     
                     if (updateStatus==Constants.FUNCTION_SUCCESS)
                     {
                         addNPADetailsStatus = true;
                         Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");
                     
                     }
                     else if (updateStatus==Constants.FUNCTION_FAILURE)
                     {
                         connection.rollback();
                         addNPADetailsStatus =false;
                         addNPADetailsStmt.close();
                         addNPADetailsStmt = null;
                         Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
                     //    System.out.println("Exception "+npaexception);
                         throw new DatabaseException(npaexception);
                     }
                     addNPADetailsStmt.close();
                     addNPADetailsStmt = null;
                     
                     
                     
                     
                   
                         
                  connection.commit();  
                 }catch (SQLException exception)
                 {
            try {
                connection.rollback();
            } catch (SQLException e) {
                // TODO
            }
            Log.logException(exception);
                         throw new DatabaseException(exception.getMessage());
                 }finally
                 {
                                 DBConnection.freeConnection(connection);
                 }
                 Log.log(Log.INFO,"GMDAO","updateNPADetails","Exited");
                 return addNPADetailsStatus;
    }
 
  


   public boolean updateNPADetailsOld(NPADetails npaDetails,
							ArrayList newRecoveryProcedures,
							ArrayList modifiedRecoveryProcedures,
							LegalSuitDetail legalSuitDetail)
							throws DatabaseException
   {
		Log.log(Log.INFO,"GMDAO","updateNPADetails","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement addNPADetailsStmt = null;
		int updateStatus=0;

		RecoveryProcedure newRecoveryProcedure = null ;
		RecoveryProcedure modifiedRecoveryProcedure = null ;
		//indicates whether the stored procedure was executed successfully
		boolean addNPADetailsStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;
		String npaId = null;
		String npaexception = null;

		try
		{
			sqlDate = new java.sql.Date(0);
			utilDate = new java.util.Date();

			/*Creates a CallableStatement object for calling
			database stored procedures*/

			addNPADetailsStmt = connection.prepareCall(
						"{?=call funcUpdateNPADtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		    addNPADetailsStmt.registerOutParameter(1,java.sql.Types.INTEGER);
		    addNPADetailsStmt.registerOutParameter(15,java.sql.Types.VARCHAR);

		    addNPADetailsStmt.setString(2,npaDetails.getCgbid());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","2----------------"+npaDetails.getCgbid());

		    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		    String formatedDate=null;
		    utilDate = npaDetails.getNpaDate();
		    if(utilDate!= null)
		    {
		            sqlDate = new java.sql.Date(utilDate.getTime());
		            addNPADetailsStmt.setDate(3,sqlDate);
		    }else
		    {
		            addNPADetailsStmt.setDate(3,null);
		    }
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","3----------------"+sqlDate);

		    addNPADetailsStmt.setString(4,npaDetails.getIsAsPerRBI());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","4----------------"+npaDetails.getIsAsPerRBI());
		    //              System.out.println("4----------------"+npaDetails.getWhetherNPAReported());
		    addNPADetailsStmt.setString(5,npaDetails.getNpaConfirm());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","5----------------");
		    
		    addNPADetailsStmt.setString(6,npaDetails.getNpaReason());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","6----------------"+npaDetails.getNpaReason());
		    
		    addNPADetailsStmt.setString(7,npaDetails.getEffortsTaken());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","7----------------"+npaDetails.getEffortsTaken());
		    
		    addNPADetailsStmt.setString(8,npaDetails.getIsAcctReconstructed());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","8----------------"+npaDetails.getIsAcctReconstructed());
		    
		    addNPADetailsStmt.setString(9,npaDetails.getSubsidyFlag());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","9----------------"+npaDetails.getSubsidyFlag());

		    addNPADetailsStmt.setString(10,npaDetails.getIsSubsidyRcvd());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","10----------------"+npaDetails.getIsSubsidyRcvd());
		    
		    addNPADetailsStmt.setString(11,npaDetails.getIsSubsidyAdjusted());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","11----------------"+npaDetails.getIsSubsidyAdjusted());

		    addNPADetailsStmt.setDouble(12,npaDetails.getSubsidyLastRcvdAmt());
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","12----------------"+npaDetails.getSubsidyLastRcvdAmt());

		    utilDate = npaDetails.getSubsidyLastRcvdDt();
		    if(utilDate!=null && !utilDate.toString().equals(""))
		    {
		            sqlDate = new java.sql.Date(utilDate.getTime());
		            addNPADetailsStmt.setDate(13,sqlDate);
		    }else
		    {
		            addNPADetailsStmt.setNull(13,java.sql.Types.DATE);
		    }
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","13----------------"+sqlDate);
		    
		    
		    addNPADetailsStmt.registerOutParameter(14, java.sql.Types.VARCHAR);
		    
		    addNPADetailsStmt.executeQuery();

		    updateStatus=addNPADetailsStmt.getInt(1);
		    npaexception = addNPADetailsStmt.getString(15);
		    

		    npaId = addNPADetailsStmt.getString(14);
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","22----------------"+npaId);
		    
		    if (updateStatus==Constants.FUNCTION_SUCCESS)
		    {
		            addNPADetailsStatus = true;
		            Log.log(Log.DEBUG,"GMDAO","insertNPADetails","insertnpadetails - SUCCESS");

		    }
		    else if (updateStatus==Constants.FUNCTION_FAILURE)
		    {
		            connection.rollback();
		            addNPADetailsStatus =false;
		            addNPADetailsStmt.close();
		            addNPADetailsStmt = null;
		            Log.log(Log.ERROR,"GMDAO","insert NPA Details","Exception "+npaexception);
		            throw new DatabaseException(npaexception);
		    }

		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","npa id befor setng to npa obj"+npaId);
		    npaDetails.setNpaId(npaId);
		    Log.log(Log.DEBUG,"GMDAO","insertNPADetails","npa id sfter setng to npa obj"+npaDetails.getNpaId());
		    addNPADetailsStmt.close();
		    addNPADetailsStmt = null;

                    
			
			

		}catch (SQLException exception)
		{
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}finally
		{
				DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","updateNPADetails","Exited");
		return addNPADetailsStatus;
   }
   
   

   public String insertNpaForUpload(NPADetails npaDetails) throws DatabaseException
   {

		   Log.log(Log.INFO,"GMDAO","insertNpaForUpload","Entered");
		   Connection connection = DBConnection.getConnection() ;
		   CallableStatement insertNpaStmt = null ;
		   ResultSet resultSet = null;
		   int updateStatus = 0;
		   java.util.Date utilDate = null;
		   java.sql.Date sqlDate = null;

		   boolean addNPADetailsStatus=false;
		   String npaId = null;

		   try
		   {
			sqlDate = new java.sql.Date(0);
			utilDate = new java.util.Date();

			 String npaexception = "" ;

			 sqlDate = new java.sql.Date(0);
			 utilDate = new java.util.Date();


			insertNpaStmt = connection.prepareCall(
						"{?=call funcInsertNPADtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			insertNpaStmt.registerOutParameter(1,
												java.sql.Types.INTEGER);
			insertNpaStmt.registerOutParameter(23,
												java.sql.Types.VARCHAR);

			insertNpaStmt.setString(2,npaDetails.getCgbid());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","2----------------"+npaDetails.getCgbid());

			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
			String formatedDate=null;
			utilDate = npaDetails.getNpaDate();
			if(utilDate!= null)
			{
				sqlDate = new java.sql.Date(utilDate.getTime());
				/*
				formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
				*/
				insertNpaStmt.setDate(3,sqlDate);
			}else
			{
				insertNpaStmt.setDate(3,null);
			}
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","3----------------"+sqlDate);

			insertNpaStmt.setString(4,npaDetails.getWhetherNPAReported());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","4----------------"+npaDetails.getWhetherNPAReported());
			insertNpaStmt.setString(5,null);//mode of reporting
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","5----------------");

			utilDate = npaDetails.getReportingDate();
			if(utilDate !=null)
			{
				sqlDate = new java.sql.Date(utilDate.getTime());
				/*
				formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
				*/
				insertNpaStmt.setDate(6,sqlDate);
			}else
			{
				insertNpaStmt.setDate(6,null);
			}
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","6----------------"+sqlDate);

			//d reference
			insertNpaStmt.setString(7,npaDetails.getReference());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","7----------------"+npaDetails.getReference());
			insertNpaStmt.setDouble(8,npaDetails.getOsAmtOnNPA());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","8----------------"+npaDetails.getOsAmtOnNPA());
			insertNpaStmt.setString(9,npaDetails.getNpaReason());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","9----------------"+npaDetails.getNpaReason());
			//d MLI Remarks
			insertNpaStmt.setString(10,npaDetails.getEffortsTaken());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","10----------------");
			insertNpaStmt.setString(11,npaDetails.getIsRecoveryInitiated());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","11----------------"+npaDetails.getIsRecoveryInitiated());

			insertNpaStmt.setInt(12,npaDetails.getNoOfActions());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","12----------------"+npaDetails.getNoOfActions());
			utilDate = npaDetails.getEffortsConclusionDate();
			if(utilDate!=null)
			{
				sqlDate = new java.sql.Date(utilDate.getTime());
				/*
				formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
				*/
				insertNpaStmt.setDate(13,sqlDate);
			}else
			{
				insertNpaStmt.setDate(13,null);
			}
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","13----------------"+sqlDate);

			insertNpaStmt.setString(15,npaDetails.getDetailsOfFinAssistance());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","14----------------"+npaDetails.getDetailsOfFinAssistance());
			insertNpaStmt.setString(14,npaDetails.getMliCommentOnFinPosition());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","15----------------"+npaDetails.getMliCommentOnFinPosition());

			insertNpaStmt.setString(16,npaDetails.getCreditSupport());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","16----------------"+npaDetails.getCreditSupport());

			insertNpaStmt.setString(17,npaDetails.getBankFacilityDetail());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","17----------------"+npaDetails.getBankFacilityDetail());

			insertNpaStmt.setString(18,npaDetails.getWillfulDefaulter());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","18----------------"+npaDetails.getWillfulDefaulter());

			insertNpaStmt.setString(19,npaDetails.getPlaceUnderWatchList());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","19----------------"+npaDetails.getPlaceUnderWatchList());

			insertNpaStmt.setString(20,null);//monitoring details
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","20----------------");

			insertNpaStmt.setString(21,npaDetails.getRemarksOnNpa());
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","21----------------"+npaDetails.getRemarksOnNpa());

			insertNpaStmt.registerOutParameter(22, java.sql.Types.VARCHAR);
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","22----------------");
			insertNpaStmt.executeQuery();

			updateStatus=insertNpaStmt.getInt(1);
			npaexception = insertNpaStmt.getString(23);

			npaId = insertNpaStmt.getString(22);
			Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","22----------------"+npaId);

			if (updateStatus==Constants.FUNCTION_SUCCESS) {
				addNPADetailsStatus = true;
				Log.log(Log.DEBUG,"GMDAO","insertNpaForUpload","insertNpaForUpload - SUCCESS");

			}
			else if (updateStatus==Constants.FUNCTION_FAILURE)
			{
				connection.rollback();
				addNPADetailsStatus =false;
				insertNpaStmt.close();
				insertNpaStmt = null;
				Log.log(Log.ERROR,"GMDAO","insertNPADetails ForUpload ","Exception "+npaexception);
				throw new DatabaseException(npaexception);
			}

			npaDetails.setNpaId(npaId);
			insertNpaStmt.close();
			insertNpaStmt = null;
			
			connection.commit() ;

	   }catch (Exception exception) {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage()) ;
	   }finally{
			 DBConnection.freeConnection(connection);
		}
	   Log.log(Log.INFO,"GMDAO","insertNPADetailsForUpload ","Exited");
	   return npaId;
  }

  public NPADetails updateNpaForUpload(NPADetails npaDetails) throws DatabaseException
  {

		  Log.log(Log.INFO,"GMDAO","insertNpaForUpload","Entered");
		  Connection connection = DBConnection.getConnection() ;
		  CallableStatement updateNPAForUploadStmt = null ;
		  ResultSet resultSet = null;
		  int updateStatus = 0;
		  java.util.Date utilDate = null;
		  java.sql.Date sqlDate = null;

		  boolean addNPADetailsStatus=false;
		  String npaId = null;
		  String npaexception = null;
	try
	{
		sqlDate = new java.sql.Date(0);
		utilDate = new java.util.Date();

		/*Creates a CallableStatement object for calling
		database stored procedures*/

		updateNPAForUploadStmt = connection.prepareCall(
					"{?=call funcUpdateNPADtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		updateNPAForUploadStmt.registerOutParameter(1,
										java.sql.Types.INTEGER);
		updateNPAForUploadStmt.registerOutParameter(25,
										java.sql.Types.VARCHAR);

		updateNPAForUploadStmt.setString(2,npaDetails.getCgbid());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","2 Borrrower Id : "+npaDetails.getCgbid());

		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

		utilDate = npaDetails.getNpaDate();
		if(utilDate != null)
		{
			sqlDate = new java.sql.Date(utilDate.getTime());
			/*
			String formatedDate=dateFormat.format(utilDate);
			sqDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
			*/
			updateNPAForUploadStmt.setDate(3,sqlDate);
		}else{
			updateNPAForUploadStmt.setDate(3,null);
		}

		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","3 Npa Date: "+sqlDate);

		updateNPAForUploadStmt.setString(4,npaDetails.getWhetherNPAReported());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","4 Whether npa reported  : "+npaDetails.getWhetherNPAReported());
		updateNPAForUploadStmt.setString(5,null);//mode of reporting
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","5 mode of reporting ");

		String formatedDate = null;

		utilDate = npaDetails.getReportingDate();
		if(utilDate != null)
		{
			sqlDate = new java.sql.Date(utilDate.getTime());
			/*
			formatedDate=dateFormat.format(utilDate);
			sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
			*/
			updateNPAForUploadStmt.setDate(6,sqlDate);
		}else{
			updateNPAForUploadStmt.setDate(6,null);
		}
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","6 ReportingDate : "+sqlDate);

		//d reference
		updateNPAForUploadStmt.setString(7,npaDetails.getReference());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","7 Reference : "+npaDetails.getReference());

		updateNPAForUploadStmt.setDouble(8,npaDetails.getOsAmtOnNPA());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","8 Os Amt on NPA : "+npaDetails.getOsAmtOnNPA());

		updateNPAForUploadStmt.setString(9,npaDetails.getNpaReason());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","9 Npa reason : "+npaDetails.getNpaReason());

		//d MLI Remarks
		updateNPAForUploadStmt.setString(10,null);
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","10 Remarks ");

		updateNPAForUploadStmt.setString(11,npaDetails.getIsRecoveryInitiated());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","11 IsRecoveryInitiated : "+npaDetails.getIsRecoveryInitiated());

		updateNPAForUploadStmt.setInt(12,npaDetails.getNoOfActions());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","12 NoOfActions : "+npaDetails.getNoOfActions());

		utilDate = npaDetails.getEffortsConclusionDate();
		if(utilDate != null)
		{
			sqlDate = new java.sql.Date(utilDate.getTime());
			/*
			formatedDate=dateFormat.format(utilDate);
			sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
			*/
			updateNPAForUploadStmt.setDate(13,sqlDate);
		}else{
			updateNPAForUploadStmt.setDate(13,null);
		}
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","13 EffortsConclusionDate : "+sqlDate);

		updateNPAForUploadStmt.setString(15,npaDetails.getDetailsOfFinAssistance());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","14 DetailsOfFinAssistance : "+npaDetails.getDetailsOfFinAssistance());

		updateNPAForUploadStmt.setString(14,npaDetails.getMliCommentOnFinPosition());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","15 MliCommentOnFinPosition : "+npaDetails.getMliCommentOnFinPosition());

		updateNPAForUploadStmt.setString(16,npaDetails.getCreditSupport());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","16 CreditSupport : "+npaDetails.getCreditSupport());

		updateNPAForUploadStmt.setString(17,npaDetails.getBankFacilityDetail());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","17 BankFacilityDetail : "+npaDetails.getBankFacilityDetail());

		updateNPAForUploadStmt.setString(18,npaDetails.getWillfulDefaulter());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","18 WillfulDefaulter : "+npaDetails.getWillfulDefaulter());

		updateNPAForUploadStmt.setString(19,npaDetails.getPlaceUnderWatchList());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","19 PlaceUnderWatchList : "+npaDetails.getPlaceUnderWatchList());

		updateNPAForUploadStmt.setString(20,null);//monitoring details
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","20 monitoring details = monitor");

		updateNPAForUploadStmt.setString(21,npaDetails.getRemarksOnNpa());
		Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","21 RemarksOnNpa : "+npaDetails.getRemarksOnNpa());

		updateNPAForUploadStmt.setNull(22,java.sql.Types.DATE);
		updateNPAForUploadStmt.setNull(23,java.sql.Types.VARCHAR);
		updateNPAForUploadStmt.setNull(24,java.sql.Types.DATE);

		updateNPAForUploadStmt.registerOutParameter(25, java.sql.Types.VARCHAR);

		updateNPAForUploadStmt.executeQuery();

		updateStatus=updateNPAForUploadStmt.getInt(1);
		npaexception = updateNPAForUploadStmt.getString(25);

		if (updateStatus==Constants.FUNCTION_SUCCESS) {
			addNPADetailsStatus = true;
			Log.log(Log.DEBUG,"GMDAO","updateNPAForUpload","SUCCESS SP ");
		}
		else if (updateStatus==Constants.FUNCTION_FAILURE)
		{
			connection.rollback();
			addNPADetailsStatus =false;
			updateNPAForUploadStmt.close();
			updateNPAForUploadStmt = null;
			Log.log(Log.ERROR,"GMDAO","updateNPAForUpload","Exception "+npaexception);
			throw new DatabaseException(npaexception);
		}

		updateNPAForUploadStmt.close();
		updateNPAForUploadStmt = null;
		
		connection.commit();
	  }catch (Exception exception) {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		  throw new DatabaseException(exception.getMessage()) ;
	  }finally{
			DBConnection.freeConnection(connection);
	   }
	  Log.log(Log.INFO,"GMDAO","updateNPAForUpload","Exited");
	  return npaDetails;
 }


	 public void insertRecAxnForUpload(RecoveryProcedure newRecoveryProcedure) throws DatabaseException
	 {

		   Log.log(Log.INFO,"GMDAO","insertRecAxnForUpload","Entered");
		   Connection connection = DBConnection.getConnection() ;
		   CallableStatement insertRecAxnForUploadStmt = null ;

		   java.util.Date utilDate = null;
		   java.sql.Date sqlDate =null;
		   int updateStatus = 0;
		   SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

		   try
		   {
			sqlDate = new java.sql.Date(0);
			utilDate = new java.util.Date();

			insertRecAxnForUploadStmt= connection.prepareCall(
									 "{?=call funcInsertRecoveryAxnDtl(?,?,?,?,?,?)}");
			insertRecAxnForUploadStmt.registerOutParameter(1,
										 java.sql.Types.INTEGER);
			insertRecAxnForUploadStmt.registerOutParameter(7,
										 java.sql.Types.VARCHAR);


			 Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insert Rec Action : Action Type "+newRecoveryProcedure.getActionType());
			insertRecAxnForUploadStmt.setString(2,newRecoveryProcedure.getActionType());

			insertRecAxnForUploadStmt.setString(3,newRecoveryProcedure.getNpaId());
			 Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insert Rec Action : npa Id  "+newRecoveryProcedure.getNpaId());

			insertRecAxnForUploadStmt.setString(4,newRecoveryProcedure.getActionDetails());
			 Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insert Rec Action : action details "+newRecoveryProcedure.getActionDetails());
			 utilDate = newRecoveryProcedure.getActionDate();

			 Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insert Rec Action : Date "+newRecoveryProcedure.getActionDate());
			sqlDate = new java.sql.Date(utilDate.getTime());
			/*
			 String  formatedDate=dateFormat.format(utilDate);			 
			 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
			 */
			insertRecAxnForUploadStmt.setDate(5,sqlDate);

			insertRecAxnForUploadStmt.setString(6,newRecoveryProcedure.getAttachmentName());
			 Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insert Rec Action : file "+newRecoveryProcedure.getAttachmentName());

			insertRecAxnForUploadStmt.executeQuery();

			 updateStatus=insertRecAxnForUploadStmt.getInt(1);
			 String recexception = insertRecAxnForUploadStmt.getString(7);

			 if (updateStatus==Constants.FUNCTION_SUCCESS) {
				  Log.log(Log.DEBUG,"GMDAO","insertRecAxnForUploadStmt","insertrecoveryaction-SUCCESS SP ");
			 }
			 else if (updateStatus==Constants.FUNCTION_FAILURE)
			 {
				connection.rollback();
				insertRecAxnForUploadStmt.close();
				insertRecAxnForUploadStmt= null;
				Log.log(Log.ERROR,"GMDAO","insertRecAxnForUploadStmt -Rec Procedure","Exception "+recexception);
				throw new DatabaseException(recexception);
			 }
			insertRecAxnForUploadStmt.close() ;
			insertRecAxnForUploadStmt = null;
			connection.commit();
	   }
	   catch (Exception exception) {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage()) ;
	   }finally{
			 DBConnection.freeConnection(connection);
		}
	   Log.log(Log.INFO,"GMDAO","insertRecAxnForUpload","Exited");

  }


  public void updateRecAxnForUpload(RecoveryProcedure modifiedRecoveryProcedure) throws DatabaseException
   {
	   Log.log(Log.INFO,"GMDAO","updateRecAxnForUpload","Entered");
	   Connection connection = DBConnection.getConnection() ;
	   CallableStatement uploadRecAxnForUploadStmt = null ;
	   ResultSet resultSet = null;
	   SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date utilDate = null;
		java.sql.Date sqlDate =null;
		int updateStatus = 0;
	   try
	   {
		sqlDate = new java.sql.Date(0);
		utilDate = new java.util.Date();

		 uploadRecAxnForUploadStmt = connection.prepareCall(
					 "{?=call funcUpdRecAXnDtl(?,?,?,?,?,?)}");
		 uploadRecAxnForUploadStmt.registerOutParameter(1,
										 java.sql.Types.INTEGER);
		 uploadRecAxnForUploadStmt.registerOutParameter(7,
										 java.sql.Types.VARCHAR);

		 uploadRecAxnForUploadStmt.setString(2,modifiedRecoveryProcedure.getRadId());
		 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload"," Rad Id  "+modifiedRecoveryProcedure.getRadId());

		 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload"," Action Type "+modifiedRecoveryProcedure.getActionType());
		 uploadRecAxnForUploadStmt.setString(3,modifiedRecoveryProcedure.getActionType());

		 uploadRecAxnForUploadStmt.setString(4,modifiedRecoveryProcedure.getActionDetails());
		 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload"," action details "+modifiedRecoveryProcedure.getActionDetails());

		 utilDate = modifiedRecoveryProcedure.getActionDate();
		 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload"," Date "+modifiedRecoveryProcedure.getActionDate());
		 if(utilDate != null)
		 {
			sqlDate = new java.sql.Date(utilDate.getTime());
			/*
			String formatedDate=dateFormat.format(utilDate);
			 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
			 */
			 uploadRecAxnForUploadStmt.setDate(5,sqlDate);
		 }else{
			 uploadRecAxnForUploadStmt.setDate(5,null);
		 }
		 uploadRecAxnForUploadStmt.setString(6,modifiedRecoveryProcedure.getAttachmentName());
		 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload"," file "+modifiedRecoveryProcedure.getAttachmentName());

		 uploadRecAxnForUploadStmt.executeQuery();

		 updateStatus=uploadRecAxnForUploadStmt.getInt(1);
		 String recexception = uploadRecAxnForUploadStmt.getString(7);

		 if (updateStatus==Constants.FUNCTION_SUCCESS) {
			 Log.log(Log.DEBUG,"GMDAO","updateRecAxnForUpload","updateRecoveryAction SP -SUCCESS");
		 }
		 else if (updateStatus==Constants.FUNCTION_FAILURE)
		 {
			connection.rollback();
			uploadRecAxnForUploadStmt.close();
			uploadRecAxnForUploadStmt = null;
			Log.log(Log.ERROR,"GMDAO","updateRecAxnForUpload ","Exception "+recexception);
			throw new DatabaseException(recexception);
		 }
		uploadRecAxnForUploadStmt.close();
		uploadRecAxnForUploadStmt = null;
		connection.commit() ;		 
		 
	   }catch (Exception exception) {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(exception.getMessage()) ;
	   }finally{
			 DBConnection.freeConnection(connection);
		}
	   Log.log(Log.INFO,"GMDAO","updateRecAxnForUpload","Exited");
  }



  public void updateLegalForUpload(LegalSuitDetail legalSuitDetail) throws DatabaseException
   {
	   Log.log(Log.INFO,"GMDAO","updateLegalForUpload","Entered");
	   Connection connection = DBConnection.getConnection() ;
	   CallableStatement updateLegalForUploadStmt = null ;

		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		 java.util.Date utilDate = null;
		 java.sql.Date sqlDate =null;
		 int updateStatus = 0;


	   try
	   {
		sqlDate = new java.sql.Date(0);
		utilDate = new java.util.Date();

		updateLegalForUploadStmt = connection.prepareCall(
					 "{?=call funcUpdateLegalDtl(?,?,?,?,?,?,?,?,?,?)}");
		updateLegalForUploadStmt.registerOutParameter(1,
										 java.sql.Types.INTEGER);
		updateLegalForUploadStmt.registerOutParameter(11,
										 java.sql.Types.VARCHAR);


		updateLegalForUploadStmt.setString(2,legalSuitDetail.getLegalSuiteNo());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Suit No : "+legalSuitDetail.getLegalSuiteNo());

		updateLegalForUploadStmt.setString(3,legalSuitDetail.getNpaId());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," NPA ID : "+legalSuitDetail.getNpaId());

		updateLegalForUploadStmt.setString(4,legalSuitDetail.getCourtName());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Court name : "+legalSuitDetail.getCourtName());

		updateLegalForUploadStmt.setString(5,legalSuitDetail.getForumName());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Forum name : "+legalSuitDetail.getForumName());

		updateLegalForUploadStmt.setString(6,legalSuitDetail.getLocation());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Loaction : "+legalSuitDetail.getLocation());

		 utilDate = legalSuitDetail.getDtOfFilingLegalSuit();
		sqlDate = new java.sql.Date(utilDate.getTime());
		/*
		 String formatedDate=dateFormat.format(utilDate);
		 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
		 */
		updateLegalForUploadStmt.setDate(7,sqlDate);
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Legald date : "+legalSuitDetail.getDtOfFilingLegalSuit());

		updateLegalForUploadStmt.setDouble(8,legalSuitDetail.getAmountClaimed());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," amt claimed : "+legalSuitDetail.getAmountClaimed());

		updateLegalForUploadStmt.setString(9,legalSuitDetail.getCurrentStatus());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," current status : "+legalSuitDetail.getCurrentStatus());

		updateLegalForUploadStmt.setString(10,legalSuitDetail.getRecoveryProceedingsConcluded());
		 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload"," Rec Proceedings Concluded: "+legalSuitDetail.getRecoveryProceedingsConcluded());

		updateLegalForUploadStmt.executeQuery();
		 updateStatus = updateLegalForUploadStmt.getInt(1);
		 String legalexception = updateLegalForUploadStmt.getString(11);

		 if (updateStatus==Constants.FUNCTION_SUCCESS) {
			 Log.log(Log.DEBUG,"GMDAO","updateLegalForUpload","updatelegaldetails SP-SUCCESS");
		 }
		 else if (updateStatus==Constants.FUNCTION_FAILURE)
		 {
			connection.rollback();
			updateLegalForUploadStmt.close();
			updateLegalForUploadStmt = null;
			Log.log(Log.ERROR,"GMDAO","update LegaldTL ","Exception "+legalexception);
			throw new DatabaseException(legalexception);
		 }
		updateLegalForUploadStmt.close();
		updateLegalForUploadStmt = null;

		connection.commit() ;

	   }catch (Exception exception) {
	   	
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage()) ;
	   }finally{
			 DBConnection.freeConnection(connection);
		}
	   Log.log(Log.INFO,"GMDAO","updateLegalForUpload","Exited");

  }



  public void insertLegalForUpload(LegalSuitDetail legalSuitDetail) throws DatabaseException
  {
	  Log.log(Log.INFO,"GMDAO","insertLegalForUpload","Entered");
	  Connection connection = DBConnection.getConnection() ;
	  CallableStatement insertLegalForUploadStmt = null ;
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		 java.util.Date utilDate = null;
		 java.sql.Date sqlDate =null;
		 int updateStatus = 0;


	   try
	   {
		sqlDate = new java.sql.Date(0);
		utilDate = new java.util.Date();

		insertLegalForUploadStmt = connection.prepareCall(
							"{?=call funcInsertLegalDtl(?,?,?,?,?,?,?,?,?,?)}");
		insertLegalForUploadStmt.registerOutParameter(1,
												java.sql.Types.INTEGER);
		insertLegalForUploadStmt.registerOutParameter(11,
												java.sql.Types.VARCHAR);


		insertLegalForUploadStmt.setString(2,legalSuitDetail.getLegalSuiteNo());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Suit No : "+legalSuitDetail.getLegalSuiteNo());

		insertLegalForUploadStmt.setString(3,legalSuitDetail.getNpaId());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," NPA ID : "+legalSuitDetail.getNpaId());

		insertLegalForUploadStmt.setString(4,legalSuitDetail.getCourtName());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Court name : "+legalSuitDetail.getCourtName());

		insertLegalForUploadStmt.setString(5,legalSuitDetail.getForumName());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Forum name : "+legalSuitDetail.getForumName());

		insertLegalForUploadStmt.setString(6,legalSuitDetail.getLocation());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Loaction : "+legalSuitDetail.getLocation());

		utilDate = legalSuitDetail.getDtOfFilingLegalSuit();

		sqlDate = new java.sql.Date(utilDate.getTime());
		/*
		String formatedDate=dateFormat.format(utilDate);
		sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
		*/
		insertLegalForUploadStmt.setDate(7,sqlDate);
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Legald date : "+legalSuitDetail.getDtOfFilingLegalSuit());

		insertLegalForUploadStmt.setDouble(8,legalSuitDetail.getAmountClaimed());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," amt claimed : "+legalSuitDetail.getAmountClaimed());

		insertLegalForUploadStmt.setString(9,legalSuitDetail.getCurrentStatus());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," current status : "+legalSuitDetail.getCurrentStatus());

		insertLegalForUploadStmt.setString(10,legalSuitDetail.getRecoveryProceedingsConcluded());
		Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload"," Rec Proceedings Concluded: "+legalSuitDetail.getRecoveryProceedingsConcluded());

		insertLegalForUploadStmt.executeQuery();
		updateStatus = insertLegalForUploadStmt.getInt(1);
		String legalexception = insertLegalForUploadStmt.getString(11);

		if (updateStatus==Constants.FUNCTION_SUCCESS) {
			Log.log(Log.DEBUG,"GMDAO","insertLegalForUpload","insertlegaldetails-SUCCESS");
		}
		else if (updateStatus==Constants.FUNCTION_FAILURE)
		{
			connection.rollback();
			insertLegalForUploadStmt.close();
			insertLegalForUploadStmt = null;
			Log.log(Log.ERROR,"GMDAO","insert NPA Details Legal dTL  ","Exception "+legalexception);
			throw new DatabaseException(legalexception);
		}
		insertLegalForUploadStmt.close();
		insertLegalForUploadStmt = null;
		connection.commit() ;		
	  }catch (Exception exception) {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

		  throw new DatabaseException(exception.getMessage()) ;
	  }finally{
			DBConnection.freeConnection(connection);
	   }
	  Log.log(Log.INFO,"GMDAO","insertLegalForUpload","Exited");

 }


   /**
    * This method will initiate the closure process of an application for the given
    * CGPAN and the reason for closure will also be updated.
    * @param strCgpan
    * @param strReason
    * @return Boolean
    * @roseuid 397AD89D0169
    */
   public boolean closure(String cgpan, String reason,String remarks, String userId)
   													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","Closure ","Entered");

		Log.log(Log.DEBUG,"GMDAO","Closure ","reason"+reason);
		Log.log(Log.DEBUG,"GMDAO","Closure ","cgpan"+cgpan);
		Log.log(Log.DEBUG,"GMDAO","Closure ","remarks"+remarks);
		Log.log(Log.DEBUG,"GMDAO","Closure ","userid"+userId);

		Connection connection = DBConnection.getConnection(false) ;
		boolean closureStatus = false;
		CallableStatement closureStmt = null ;


		try {

			String exception = "" ;

			closureStmt = connection.prepareCall(
				"{?=call funcCloseApplication (?,?,?,?,?)}");
			closureStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			closureStmt.setString(2,cgpan);
			closureStmt.setString(3,userId);
			closureStmt.setString(4,reason);
			closureStmt.setString(5,remarks);
			closureStmt.registerOutParameter(6,java.sql.Types.VARCHAR) ;

			closureStmt.executeQuery() ;

			exception = closureStmt.getString(6) ;

			int errorcode=closureStmt.getInt(1);

			if(errorcode==Constants.FUNCTION_SUCCESS) {
				closureStatus = true;
				Log.log(Log.DEBUG,"GMDAO","ClosureSP ","SUCCESS");
			}
			if(errorcode==Constants.FUNCTION_FAILURE)
			{
				connection.rollback();
				closureStmt.close();
				closureStmt=null;
				Log.log(Log.ERROR,"GMDAO","Closure","Exception "+exception);
				throw new DatabaseException(exception);
			}
			closureStmt.close();
			closureStmt=null;
			connection.commit() ;
		}catch (Exception exception) {
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}
			
			throw new DatabaseException(exception.getMessage()) ;
		}
		finally	{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","Closure ","Exited");
    	return closureStatus;
   }


   public TreeMap getMemberIdCgpansForClosure(String feeType)
													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","getMemberIdCgpansForClosure","Entered");

		Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","fee Type "+feeType);

		ResultSet resultSet = null;
		
		Connection connection = DBConnection.getConnection(false) ;
		CallableStatement memberIdCgpansClosureStmt = null ;
		
		String memberId = null;
		int noOfCgpans = 0;
		
		TreeMap memberIdCgpans = new TreeMap();
		
		try 
		{
			String exception = "" ;

			memberIdCgpansClosureStmt = connection.prepareCall(
				"{?=call packGetNotPaidNoOfCgpans.funcGetNotPaidNoOfCgpans(?,?,?)}");
				
			memberIdCgpansClosureStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
		
			memberIdCgpansClosureStmt.setString(2,feeType) ;
			
			memberIdCgpansClosureStmt.registerOutParameter(3,Constants.CURSOR) ;
			
			memberIdCgpansClosureStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

			memberIdCgpansClosureStmt.executeQuery() ;
			
			Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","Query Executed");
			
			exception = memberIdCgpansClosureStmt.getString(4) ;

			int errorcode=memberIdCgpansClosureStmt.getInt(1);

			if(errorcode==Constants.FUNCTION_SUCCESS) 
			{
				Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","SUCCESS");
			}
			
			if(errorcode==Constants.FUNCTION_FAILURE)
			{
				connection.rollback();
				memberIdCgpansClosureStmt.close();
				memberIdCgpansClosureStmt=null;
				Log.log(Log.ERROR,"GMDAO","getMemberIdCgpansForClosure",
													"Exception "+exception);
				throw new DatabaseException(exception);
			}
			
			resultSet = (ResultSet)memberIdCgpansClosureStmt.getObject(3) ;
			
			Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure",
												"before iterating Result Set");
			
			while(resultSet.next())
			{
				Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","Inside Result Set");
				
				noOfCgpans = resultSet.getInt(1);
				Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","noOfCgpans "+noOfCgpans );				
				
				memberId = resultSet.getString(2) + resultSet.getString(3) + resultSet.getString(4);
				Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","mem id"+memberId );
				
				memberIdCgpans.put(memberId,new Integer(noOfCgpans));				
			}
			Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure",
									"memberid-pans map size"+memberIdCgpans.size());

			resultSet.close();
			resultSet = null;
			memberIdCgpansClosureStmt.close();
			memberIdCgpansClosureStmt=null;
		   
			connection.commit() ;

	   }catch (Exception exception) 
	    {
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore)
			{
			}
		   	throw new DatabaseException(exception.getMessage()) ;
	   	}finally
	   	{
			 DBConnection.freeConnection(connection);
	   	}		
		Log.log(Log.INFO,"GMDAO","getMemberIdCgpansForClosure","Exited");
		
		return memberIdCgpans ;
   }



   public ArrayList getCgpansForClosure(String memberId, String feeType)
													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","getCgpansForClosure","Entered");

		Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure","fee Type "+feeType);
		Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure","memberId "+memberId);
		
		ResultSet resultSet = null;
		
		Connection connection = DBConnection.getConnection(false) ;
		CallableStatement cgpansClosureStmt = null ;		
				
		String bankId = memberId.substring(0,4);
		String zoneId = memberId.substring(4,8);
		String branchId = memberId.substring(8,12);
		
		ArrayList cgpans = new ArrayList();
		
		try 
		{
			String exception = "" ;

			cgpansClosureStmt = connection.prepareCall(
				"{?=call packGetNotPaidCgpans.funcGetNotPaidCgpans(?,?,?,?,?,?)}");
				
			cgpansClosureStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
		
			cgpansClosureStmt.setString(2,bankId) ;
			cgpansClosureStmt.setString(3,zoneId) ;
			cgpansClosureStmt.setString(4,branchId) ;
			cgpansClosureStmt.setString(5,feeType) ;
			
			cgpansClosureStmt.registerOutParameter(6,Constants.CURSOR) ;
			
			cgpansClosureStmt.registerOutParameter(7,java.sql.Types.VARCHAR) ;

			cgpansClosureStmt.executeQuery() ;
			
			Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure","Query Executed");
			
			exception = cgpansClosureStmt.getString(7) ;

			int errorcode=cgpansClosureStmt.getInt(1);

			if(errorcode==Constants.FUNCTION_SUCCESS) 
			{
				Log.log(Log.DEBUG,"GMDAO","getMemberIdCgpansForClosure","SUCCESS");
			}
			
			if(errorcode==Constants.FUNCTION_FAILURE)
			{
				connection.rollback();
				cgpansClosureStmt.close();
				cgpansClosureStmt=null;
				Log.log(Log.ERROR,"GMDAO","getCgpansForClosure",
													"Exception "+exception);
				throw new DatabaseException(exception);
			}
			
			resultSet = (ResultSet)cgpansClosureStmt.getObject(6) ;
			
			Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure",
										"before iterating Result Set");
				
			String cgpan = null;
				
			while(resultSet.next())
			{
				Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure","Inside Result Set");
				cgpan = resultSet.getString(1);
				Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure","Cgpan "+cgpan);
				cgpans.add(cgpan);				
			}
			Log.log(Log.DEBUG,"GMDAO","getCgpansForClosure",
									"cgpans list size"+cgpans.size());

			resultSet.close();
			resultSet = null;
			cgpansClosureStmt.close();
			cgpansClosureStmt=null;
		   
			connection.commit() ;

	   }catch (Exception exception) 
		{
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore)
			{
			}
			throw new DatabaseException(exception.getMessage()) ;
		}finally
		{
			 DBConnection.freeConnection(connection);
		}		
		Log.log(Log.INFO,"GMDAO","getCgpansForClosure","Exited");
		
		return cgpans ;
   }



   /**
    * This method updates the details of an existing borrower. It takes an object of
    * BorrowerDetails type and updates the details of the borrower in the database.
    * @param borrowerDetail
    * @param userId
    * @roseuid 397AD89D016C
    */
   public void updateBorrowerDetails(BorrowerDetails borrowerDetail, String userId)
   													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","updateBorrowerDetails","Entered");
		Connection connection = DBConnection.getConnection(false);

		CallableStatement updateBorrowerDetailsStmt = null;

		SSIDetails ssiDetails = new SSIDetails();

		java.sql.Date sqlDate = null;
		java.util.Date utilDate=new Date();

		if(borrowerDetail!=null)
		{
			try {
				/*Creates a CallableStatement object for calling database
				 * stored procedures*/

				updateBorrowerDetailsStmt= connection.prepareCall(
 			"{?=call funcUpdateSSIDetail(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				updateBorrowerDetailsStmt.registerOutParameter(1,Types.INTEGER);

				updateBorrowerDetailsStmt.registerOutParameter(30,Types.VARCHAR);

				updateBorrowerDetailsStmt.setInt(2,borrowerDetail.getSsiDetails().getBorrowerRefNo());		//Generate SSI RefNo

				Log.log(Log.DEBUG,"GMDAO","updateBorrowerDetails","Ref no"+borrowerDetail.getSsiDetails().getBorrowerRefNo());

				updateBorrowerDetailsStmt.setString(3,
											borrowerDetail.getPreviouslyCovered());
				updateBorrowerDetailsStmt.setString(4,
											borrowerDetail.getAssistedByBank());

				updateBorrowerDetailsStmt.setString(5,borrowerDetail.getAcNo());
				updateBorrowerDetailsStmt.setString(6,borrowerDetail.getNpa());
				updateBorrowerDetailsStmt.setString(7,borrowerDetail.getSsiDetails().getConstitution());
				updateBorrowerDetailsStmt.setString(8,borrowerDetail.getSsiDetails().getSsiType());
				updateBorrowerDetailsStmt.setString(9,borrowerDetail.getSsiDetails().getSsiName().toUpperCase());
     //   System.out.println("Modified SSI unit Name():"+borrowerDetail.getSsiDetails().getSsiName().toUpperCase());
				updateBorrowerDetailsStmt.setString(10,borrowerDetail.getSsiDetails().getRegNo());

			/*	utilDate = borrowerDetail.getSsiDetails().getCommencementDate();
				SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate=dateFormat.format(utilDate);
				sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());

			*/
				updateBorrowerDetailsStmt.setDate(11,null);
				updateBorrowerDetailsStmt.setString(12,borrowerDetail.getSsiDetails().getSsiITPan());

				updateBorrowerDetailsStmt.setString(13,borrowerDetail.getSsiDetails().getActivityType());
				updateBorrowerDetailsStmt.setInt(14,borrowerDetail.getSsiDetails().getEmployeeNos());
				updateBorrowerDetailsStmt.setDouble(15,borrowerDetail.getSsiDetails().getProjectedSalesTurnover());

				updateBorrowerDetailsStmt.setDouble(16,borrowerDetail.getSsiDetails().getProjectedExports());
				updateBorrowerDetailsStmt.setString(17,borrowerDetail.getSsiDetails().getAddress().toUpperCase());

				updateBorrowerDetailsStmt.setString(18,borrowerDetail.getSsiDetails().getCity().toUpperCase());
				updateBorrowerDetailsStmt.setString(19,borrowerDetail.getSsiDetails().getPincode());


				//Display defaulter is set to false.
				updateBorrowerDetailsStmt.setBoolean(20,false);

				updateBorrowerDetailsStmt.setString(21,borrowerDetail.getSsiDetails().getDistrict());

				updateBorrowerDetailsStmt.setString(22,borrowerDetail.getSsiDetails().getState());

				updateBorrowerDetailsStmt.setString(23,borrowerDetail.getSsiDetails().getIndustryNature());
//				Ssi status set to false..dd
				updateBorrowerDetailsStmt.setInt(24,0);

				updateBorrowerDetailsStmt.setString(25,borrowerDetail.getSsiDetails().getIndustrySector());

				updateBorrowerDetailsStmt.setDouble(26,borrowerDetail.getOsAmt());
				updateBorrowerDetailsStmt.setBoolean(27,false);//mcgs flag..

				updateBorrowerDetailsStmt.setString(28,userId);
        // System.out.println(borrowerDetail.getSsiDetails().getRemarks());
        updateBorrowerDetailsStmt.setString(29,borrowerDetail.getSsiDetails().getRemarks());
        
				updateBorrowerDetailsStmt.executeQuery();

				int updateBorrowerDetailsStmtValue = updateBorrowerDetailsStmt.getInt(1) ;
				String exception = updateBorrowerDetailsStmt.getString(30);
				if(updateBorrowerDetailsStmtValue==Constants.FUNCTION_FAILURE)
				{
					connection.rollback();
					updateBorrowerDetailsStmt.close();
					updateBorrowerDetailsStmt = null;
					Log.log(Log.ERROR,"GMDAO","updatePromoterDetails","Exception "+exception);
					throw new DatabaseException(exception);
				}
				updateBorrowerDetailsStmt.close();
				updateBorrowerDetailsStmt = null;

//****************************PromoterDetail*****************************
				updateBorrowerDetailsStmt= connection.prepareCall(
								"{?=call funcUpdatePromoterDtl(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				updateBorrowerDetailsStmt.registerOutParameter(1,Types.INTEGER);
				updateBorrowerDetailsStmt.registerOutParameter(23,Types.VARCHAR);
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","Ref no"+borrowerDetail.getSsiDetails().getBorrowerRefNo());

				updateBorrowerDetailsStmt.setInt(2,borrowerDetail.getSsiDetails().getBorrowerRefNo());
				updateBorrowerDetailsStmt.setString(3,borrowerDetail.getSsiDetails().getCpTitle());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","title"+borrowerDetail.getSsiDetails().getCpTitle());

				updateBorrowerDetailsStmt.setString(4,borrowerDetail.getSsiDetails().getCpFirstName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","Cp first name"+borrowerDetail.getSsiDetails().getCpFirstName());

				updateBorrowerDetailsStmt.setString(5,borrowerDetail.getSsiDetails().getCpMiddleName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","Cp middle name"+borrowerDetail.getSsiDetails().getCpMiddleName());
     //   System.out.println("Cp middle name"+borrowerDetail.getSsiDetails().getCpMiddleName());
				updateBorrowerDetailsStmt.setString(6,borrowerDetail.getSsiDetails().getCpLastName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","CP last name"+borrowerDetail.getSsiDetails().getCpLastName());

				updateBorrowerDetailsStmt.setString(7,borrowerDetail.ssiDetails.getCpITPAN());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","it pan"+borrowerDetail.ssiDetails.getCpITPAN());

				updateBorrowerDetailsStmt.setString(8,borrowerDetail.getSsiDetails().getCpGender());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","gender"+borrowerDetail.getSsiDetails().getCpGender());

				utilDate = borrowerDetail.getSsiDetails().getCpDOB();
				if(utilDate !=null && !(utilDate.toString().equals(""))){
//					System.out.println("getCpDOB: "+utilDate);
					sqlDate = new java.sql.Date(utilDate.getTime());
					//updateBorrowerDetailsStmt.setDate(9,java.sql.Date.valueOf(DateHelper.stringToSQLdate(
									//(borrowerDetail.getSsiDetails().getCpDOB()).toString())));			//dob
					updateBorrowerDetailsStmt.setDate(9,sqlDate);
				}else{
					updateBorrowerDetailsStmt.setDate(9,null);
				}
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","CpDOB"+ sqlDate);
				updateBorrowerDetailsStmt.setString(10,borrowerDetail.getSsiDetails().getCpLegalID());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","Cplegal Id"+ borrowerDetail.getSsiDetails().getCpLegalID());
				updateBorrowerDetailsStmt.setString(11,borrowerDetail.getSsiDetails().getCpLegalIdValue());

				updateBorrowerDetailsStmt.setString(12,borrowerDetail.getSsiDetails().getFirstName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","first name"+ borrowerDetail.getSsiDetails().getFirstName());
				updateBorrowerDetailsStmt.setString(14,borrowerDetail.getSsiDetails().getFirstItpan());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","first it pan"+ borrowerDetail.getSsiDetails().getFirstItpan());
				updateBorrowerDetailsStmt.setString(15,borrowerDetail.getSsiDetails().getSecondName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","2 name"+borrowerDetail.getSsiDetails().getSecondName());
				updateBorrowerDetailsStmt.setString(17,borrowerDetail.getSsiDetails().getSecondItpan());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","2 itpan"+borrowerDetail.getSsiDetails().getSecondItpan());
				updateBorrowerDetailsStmt.setString(18,borrowerDetail.getSsiDetails().getThirdName());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","3 name"+borrowerDetail.getSsiDetails().getThirdName());
				updateBorrowerDetailsStmt.setString(20,borrowerDetail.getSsiDetails().getThirdItpan());
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","3 it pan"+borrowerDetail.getSsiDetails().getThirdItpan());
				updateBorrowerDetailsStmt.setString(21,borrowerDetail.getSsiDetails().getSocialCategory());
				updateBorrowerDetailsStmt.setString(22,userId);

				utilDate = borrowerDetail.getSsiDetails().getFirstDOB();
				if(utilDate !=null && !(utilDate.toString().equals(""))){
//					System.out.println("getFirstDOB: "+utilDate);
					sqlDate = new java.sql.Date(utilDate.getTime());
					//updateBorrowerDetailsStmt.setDate(13,java.sql.Date.valueOf(DateHelper.stringToSQLdate(
					//					(borrowerDetail.getSsiDetails().getFirstDOB()).toString())));
					updateBorrowerDetailsStmt.setDate(13,sqlDate);
				}else{
					updateBorrowerDetailsStmt.setDate(13,null);
				}
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","1 DOB"+sqlDate);

				utilDate = borrowerDetail.getSsiDetails().getSecondDOB();
//				System.out.println("getSecondDOB: "+utilDate);
				if(utilDate !=null && !(utilDate.toString().equals(""))){
					sqlDate = new java.sql.Date(utilDate.getTime());
					//updateBorrowerDetailsStmt.setDate(16,java.sql.Date.valueOf(DateHelper.stringToSQLdate(
					//					(borrowerDetail.getSsiDetails().getSecondDOB()).toString())));
					updateBorrowerDetailsStmt.setDate(16,sqlDate);
					Log.log(Log.DEBUG,"GMDAO","update promoter Details","2 DOB"+sqlDate);
				}else{
					updateBorrowerDetailsStmt.setDate(16,null);
				}
				//System.out.println(sqlDate);
				utilDate = borrowerDetail.getSsiDetails().getThirdDOB();
				if(utilDate !=null && !(utilDate.toString().equals(""))){
//					System.out.println("getThirdDOB: "+utilDate);
					sqlDate = new java.sql.Date(utilDate.getTime());
					//updateBorrowerDetailsStmt.setDate(19,java.sql.Date.valueOf(DateHelper.stringToSQLdate(
					//			(borrowerDetail.getSsiDetails().getThirdDOB()).toString())));
					updateBorrowerDetailsStmt.setDate(19,sqlDate);
				}else{
					updateBorrowerDetailsStmt.setDate(19,null);
				}
				Log.log(Log.DEBUG,"GMDAO","update promoter Details","3 DOB"+sqlDate);

				updateBorrowerDetailsStmt.executeQuery();

				updateBorrowerDetailsStmtValue = updateBorrowerDetailsStmt.getInt(1) ;
				String error = updateBorrowerDetailsStmt.getString(23);
				if(updateBorrowerDetailsStmtValue==Constants.FUNCTION_FAILURE)
				{
					connection.rollback();
					updateBorrowerDetailsStmt.close();
					updateBorrowerDetailsStmt = null;
					Log.log(Log.ERROR,"GMDAO","updateBorrowerDetails","Update Borrower Detail exception :" + error);
					throw new DatabaseException(error);
				 }
				updateBorrowerDetailsStmt.close();
				updateBorrowerDetailsStmt = null;

				 connection.commit();

			}catch (SQLException exception)
			{
				Log.logException(exception);
				throw new DatabaseException(exception.getMessage());
			}finally
			{
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO,"GMDAO","updateBorrowerDetails","Exited");
   }

   /**
    * This method updates the database of which application or borrower has to be
    * shifted to another operating office.
    * @param srcId - This argument gives the ID of the operating office FROM which the
    * application, or borrower is to be shifted.
    * @param userId - This argument gives the information of the user
    *  @param cgpan - this argument gives the cgpan which has to be shifted
    * @param destId - This argument gives the ID of the operating office TO which the
    * application or the borrower has to be shifted.
    * @return Boolean
    * @roseuid 397AD89D0173
    */
   public boolean shiftCgpanBorrower(String srcId, String userId,
   				String cgpan, String destId)throws DatabaseException {
        //  System.out.println("GMDAO"+"shiftCgpanBorrower"+"Entered");
		Log.log(Log.INFO,"GMDAO","shiftCgpanBorrower","Entered");
       	Connection connection = DBConnection.getConnection();
		boolean shiftStatus = true;
       	String srcBankId = srcId.substring(0,4);
		String srcZoneId = srcId.substring(4,8);
		String srcBranchId = srcId.substring(8,12);
 //  System.out.println("Source MemberId:"+srcBankId+srcZoneId+srcBranchId);
		String destBankId = destId.substring(0,4);
		String destZoneId = destId.substring(4,8);
		String destBranchId = destId.substring(8,12);
  //  System.out.println("destination MemberId:"+destBankId+destZoneId+destBranchId);
       	try{
			CallableStatement shiftCgpanStmt = connection.prepareCall
				("{?=call funcShiftCGPANNEW(?,?,?,?,?)}");
/*CallableStatement shiftCgpanStmt = connection.prepareCall
				("{?=call funcShiftCGPAN(?,?,?,?,?)}"); */
			shiftCgpanStmt.registerOutParameter(1,Types.INTEGER);
			shiftCgpanStmt.registerOutParameter(6,Types.VARCHAR);

			shiftCgpanStmt.setString(2,cgpan);
			shiftCgpanStmt.setString(3,srcId);
			shiftCgpanStmt.setString(4,destId);
			shiftCgpanStmt.setString(5,userId);
			//shiftCgpanStmt.setString(6,destBankId);
//			shiftCgpanStmt.setString(7,destZoneId);
//			shiftCgpanStmt.setString(8,destBranchId);
//			shiftCgpanStmt.setString(9,userId);

			shiftStatus = shiftCgpanStmt.execute();

			int functionReturn = shiftCgpanStmt.getInt(1);
   //   System.out.println("functionReturn:"+functionReturn);
			String error=shiftCgpanStmt.getString(6);
   //   System.out.println("shift cgpan error:"+error);

			if(functionReturn == Constants.FUNCTION_SUCCESS)
			{
    //  System.out.println("GMDAO"+"ShiftCgpanBorrower"+"SP SUCCESS");
				Log.log(Log.DEBUG,"GMDAO","ShiftCgpanBorrower","SP SUCCESS");
			}
			if(functionReturn==Constants.FUNCTION_FAILURE)
			{
				shiftCgpanStmt.close();
				shiftCgpanStmt = null;
				connection.rollback() ;
   //     System.out.println("GMDAO"+"Shift cgpan"+"Exception "+error);
				Log.log(Log.ERROR,"GMDAO","Shift cgpan","Exception "+error);

				throw new DatabaseException(error);
			}
			shiftCgpanStmt.close();
			shiftCgpanStmt = null;
			connection.commit() ;
			
  		 }catch (Exception e)
  		 {
   //    System.out.println("GMDAO"+"Shift cgpan"+e.getMessage());
			Log.log(Log.ERROR,"GMDAO","Shift cgpan",e.getMessage());

			Log.logException(e);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}


			throw new DatabaseException();

		 }finally {

			DBConnection.freeConnection(connection);
	 	 }
   //   System.out.println("GMDAO"+"shiftCgpanBorrower"+"Successfully Exited");
		 Log.log(Log.INFO,"GMDAO","shiftCgpanBorrower","Exited");
    	return shiftStatus;
   	}

   /**
    * This method returns a BorrowerDetails object that contains the details of the
    * Borrower. The inputs passed to the method are memberId
    * and borrowerId cgpan borrowerName.
    *
    *
    * @param memberId
    * @param borrowerId
    * @param cgpan
    * @param borrowerName
    * @return com.cgtsi.application.BorrowerDetails
    * @roseuid 397BCC130248
    */

   public BorrowerDetails getBorrowerDetails(String memberId, String id,
							int type) throws
   											    DatabaseException {

		Log.log(Log.INFO,"GMDAO","getBorrowerDetails","Entered");
    	BorrowerDetails borrowerDetails = null;
		Log.log(Log.DEBUG,"GMDAO","getBorrowerDetails","mem"+memberId);
		Log.log(Log.DEBUG,"GMDAO","getBorrowerDetails","  id "+id);



    	// bid
    	if( type == GMConstants.TYPE_ZERO ) {
    			borrowerDetails = getBorrowerDetailsForBID(memberId,id);

		}//cgpan
		else if( type == GMConstants.TYPE_ONE ) {

			borrowerDetails = getBorrowerDetailsForCgpan(memberId,id);

		}//borrower Name
		else if( type == GMConstants.TYPE_TWO ) {

				borrowerDetails = getBorrowerDetailsForBorrower(memberId,id);
		}

		Log.log(Log.INFO,"GMDAO","getBorrowerDetails","Exited");
    	return borrowerDetails ;
   }

   /*
    * this metod gets the borrower details for Bid
    */
   	public BorrowerDetails getBorrowerDetailsForBID(String memberId,
   									 String borrowerId) throws DatabaseException{

		Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForBID","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement getBorrowerDetailsStmt;
		SSIDetails ssiDetails = new SSIDetails();
		BorrowerDetails borrowerDetails = new BorrowerDetails() ;

		int ssiRefNo;


		String bankId = memberId.substring(0,4);
		String zoneId = memberId.substring(4,8);
		String branchId = memberId.substring(8,12);
		int getStatus = 0;

		try {

			getBorrowerDetailsStmt = connection.prepareCall(
				"{?=call funcGetSSIDetailforBID(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			getBorrowerDetailsStmt.setString(2,bankId);
			getBorrowerDetailsStmt.setString(3,zoneId);
			getBorrowerDetailsStmt.setString(4,branchId);
			getBorrowerDetailsStmt.setString(5,borrowerId);
			getBorrowerDetailsStmt.registerOutParameter(1,
												java.sql.Types.INTEGER) ;
			getBorrowerDetailsStmt.registerOutParameter(32,
												Types.VARCHAR) ;


			getBorrowerDetailsStmt.registerOutParameter(6,Types.INTEGER) ; //SSI Ref No
			getBorrowerDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Already Covered by CGTSI
			getBorrowerDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Already assisted by bank
			getBorrowerDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//SSI Unique No
																			//NPA
			getBorrowerDetailsStmt.registerOutParameter(10,Types.VARCHAR);	//Constitution
			getBorrowerDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//SSI Type
			getBorrowerDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//unit Name
			getBorrowerDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//SSI reg no
			getBorrowerDetailsStmt.registerOutParameter(14,Types.VARCHAR);	//Commencement Date
			getBorrowerDetailsStmt.registerOutParameter(15,Types.DATE);		//SSI ITPAN
			getBorrowerDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//type of Activity
			getBorrowerDetailsStmt.registerOutParameter(17,Types.VARCHAR);	//No Of Employees
			getBorrowerDetailsStmt.registerOutParameter(18,Types.INTEGER);	//project Sales
			getBorrowerDetailsStmt.registerOutParameter(19,Types.DOUBLE);	//Project Exports
			getBorrowerDetailsStmt.registerOutParameter(20,Types.DOUBLE);	//SSI Address
			getBorrowerDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//SSi City
			getBorrowerDetailsStmt.registerOutParameter(22,Types.VARCHAR);		//Pincode
			getBorrowerDetailsStmt.registerOutParameter(23,Types.VARCHAR);	//Display Defaulter
			getBorrowerDetailsStmt.registerOutParameter(24,Types.VARCHAR);
			getBorrowerDetailsStmt.registerOutParameter(25,Types.VARCHAR);	//SSI District
			getBorrowerDetailsStmt.registerOutParameter(26,Types.VARCHAR);	//SSI State
			getBorrowerDetailsStmt.registerOutParameter(27,Types.VARCHAR);	//Industry Nature
			getBorrowerDetailsStmt.registerOutParameter(28,Types.VARCHAR);	//Indutry Sector name
			getBorrowerDetailsStmt.registerOutParameter(29,Types.VARCHAR);
			getBorrowerDetailsStmt.registerOutParameter(30,Types.VARCHAR);	//Status
			getBorrowerDetailsStmt.registerOutParameter(31,Types.DOUBLE);


			getBorrowerDetailsStmt.execute() ;
			getStatus = getBorrowerDetailsStmt.getInt(1);
			String exception = getBorrowerDetailsStmt.getString(32);

			if(getStatus  == Constants.FUNCTION_SUCCESS){
				Log.log(Log.DEBUG,"GMDAO","getBorroerDetails","Success-SP");
			}
			if(getStatus  == Constants.FUNCTION_FAILURE){
				getBorrowerDetailsStmt.close();
				getBorrowerDetailsStmt = null;
				Log.log(Log.ERROR,"GMDAO","GetBorrwoewerDtls","Exception" +exception);
				throw new DatabaseException(exception);
			}else {
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","Borrower Id : " +borrowerId);
				ssiDetails.setBorrowerRefNo(getBorrowerDetailsStmt.getInt(6));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","Borrower ref no :"+ssiDetails.getBorrowerRefNo());

				borrowerDetails.setPreviouslyCovered(getBorrowerDetailsStmt.getString(7).trim());
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","previously covered :"+borrowerDetails.getPreviouslyCovered());

				borrowerDetails.setAssistedByBank(getBorrowerDetailsStmt.getString(8).trim());
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","AssistedByBank:"+borrowerDetails.getAssistedByBank());

				borrowerDetails.setAcNo(getBorrowerDetailsStmt.getString(9));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setAcNo:"+borrowerDetails.getAcNo());

				borrowerDetails.setNpa(getBorrowerDetailsStmt.getString(10).trim());
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setNpa:"+borrowerDetails.getNpa());

				ssiDetails.setConstitution(getBorrowerDetailsStmt.getString(11));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setConstitution:"+ssiDetails.getConstitution());

				ssiDetails.setSsiType(getBorrowerDetailsStmt.getString(12).trim());
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setSsiType:"+ssiDetails.getSsiType());

				ssiDetails.setSsiName(getBorrowerDetailsStmt.getString(13));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setSsiName:"+ssiDetails.getSsiName());

				ssiDetails.setRegNo(getBorrowerDetailsStmt.getString(14));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setRegNo:"+ssiDetails.getRegNo());

				ssiDetails.setCommencementDate(DateHelper.sqlToUtilDate(getBorrowerDetailsStmt.getDate(15)));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setCommencementDate:"+ssiDetails.getCommencementDate());

				ssiDetails.setSsiITPan(getBorrowerDetailsStmt.getString(16));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setSsiITPan:"+ssiDetails.getSsiITPan());

				ssiDetails.setActivityType(getBorrowerDetailsStmt.getString(17));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setActivityType:"+ssiDetails.getActivityType());

				ssiDetails.setEmployeeNos(getBorrowerDetailsStmt.getInt(18));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setEmployeeNos:"+ssiDetails.getEmployeeNos());

				ssiDetails.setProjectedSalesTurnover(getBorrowerDetailsStmt.getDouble(19));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setProjectedSalesTurnover:"+ssiDetails.getProjectedSalesTurnover());

				ssiDetails.setProjectedExports(getBorrowerDetailsStmt.getDouble(20));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setProjectedSalesTurnover:"+ssiDetails.getProjectedExports());

				ssiDetails.setAddress(getBorrowerDetailsStmt.getString(21));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setAddress:"+ssiDetails.getAddress());

				ssiDetails.setCity(getBorrowerDetailsStmt.getString(22));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setCity:"+ssiDetails.getCity());

				ssiDetails.setPincode(getBorrowerDetailsStmt.getString(23).trim());
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setPincode:"+ssiDetails.getPincode());

				ssiDetails.setDistrict(getBorrowerDetailsStmt.getString(25));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setDistrict:"+ssiDetails.getDistrict());

				ssiDetails.setState(getBorrowerDetailsStmt.getString(26));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setState:"+ssiDetails.getState());

				ssiDetails.setIndustryNature(getBorrowerDetailsStmt.getString(27));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setIndustryNature:"+ssiDetails.getIndustryNature());

				ssiDetails.setIndustrySector(getBorrowerDetailsStmt.getString(28));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setIndustrySector:"+ssiDetails.getIndustrySector());

				ssiDetails.setCgbid(getBorrowerDetailsStmt.getString(30));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setCgbid:"+ssiDetails.getCgbid());

				borrowerDetails.setOsAmt(getBorrowerDetailsStmt.getDouble(31));
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","setOsAmt:"+borrowerDetails.getOsAmt());
				borrowerDetails.setSsiDetails(ssiDetails);
				ssiRefNo = (borrowerDetails.getSsiDetails()).getBorrowerRefNo();
				Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","Borrower ref no :"+ssiRefNo);
			}
			Log.log(Log.DEBUG,"GMDAO","GetBorrwoewerDtls","Borrower ref no outside:"+ssiRefNo);

			CallableStatement getPromoterDetailsStmt = connection.prepareCall(
			"{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");


			getPromoterDetailsStmt.registerOutParameter(1,Types.INTEGER);
			getPromoterDetailsStmt.registerOutParameter(23,Types.VARCHAR);

			getPromoterDetailsStmt.setInt(2,ssiRefNo);	//SSIRefNo
			getPromoterDetailsStmt.registerOutParameter(3,Types.INTEGER);	//SSI RefNo
			getPromoterDetailsStmt.registerOutParameter(4,Types.VARCHAR);	//Chief title
			getPromoterDetailsStmt.registerOutParameter(5,Types.VARCHAR);	//First name
			getPromoterDetailsStmt.registerOutParameter(6,Types.VARCHAR);	//Middle Name
			getPromoterDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Last name
			getPromoterDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Chief ITPAN
			getPromoterDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//Gender
			getPromoterDetailsStmt.registerOutParameter(10,Types.DATE);		//DOB
			getPromoterDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//Legal Type
			getPromoterDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//LegalID
			getPromoterDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//Promoter First Name
			getPromoterDetailsStmt.registerOutParameter(14,Types.DATE);		//Promoter First DOB
			getPromoterDetailsStmt.registerOutParameter(15,Types.VARCHAR);	//Promoter FirstITPAN
			getPromoterDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//Promoter Second Name
			getPromoterDetailsStmt.registerOutParameter(17,Types.DATE);		//Promoter Second DOB
			getPromoterDetailsStmt.registerOutParameter(18,Types.VARCHAR);	//Promoter Second ITPAN
			getPromoterDetailsStmt.registerOutParameter(19,Types.VARCHAR);	//Promoter Third Name
			getPromoterDetailsStmt.registerOutParameter(20,Types.DATE);		//Promoter Third DOB
			getPromoterDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//Promoter Third ITPAN
			getPromoterDetailsStmt.registerOutParameter(22,Types.VARCHAR);	//Social Category

			getPromoterDetailsStmt.executeQuery();
			int getPromoterDetailsStatus=getPromoterDetailsStmt.getInt(1);
			String prException = getPromoterDetailsStmt.getString(23);

			if(getPromoterDetailsStatus==Constants.FUNCTION_FAILURE)
			{
				getPromoterDetailsStmt.close();
				getPromoterDetailsStmt = null;
				Log.log(Log.ERROR,"GMDAO","GetpromotererDtls","Exception :" +prException);
				throw new DatabaseException(prException);
			 }	else
			 {
				 ssiDetails.setCpTitle(getPromoterDetailsStmt.getString(4));
				Log.log(Log.DEBUG,"GMDAO","GetpromotererDtls","CP Gender" + ssiDetails.getCpTitle());
				 ssiDetails.setCpFirstName(getPromoterDetailsStmt.getString(5));
				 ssiDetails.setCpMiddleName(getPromoterDetailsStmt.getString(6));
				 ssiDetails.setCpLastName(getPromoterDetailsStmt.getString(7));
				 ssiDetails.setCpITPAN(getPromoterDetailsStmt.getString(8));
				 ssiDetails.setCpGender(getPromoterDetailsStmt.getString(9).trim());
				Log.log(Log.DEBUG,"GMDAO","GetpromotererDtls","CP Gender" + ssiDetails.getCpGender());
				 ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(10)));
				 ssiDetails.setCpLegalID(getPromoterDetailsStmt.getString(11));
				Log.log(Log.DEBUG,"GMDAO","GetpromotererDtls","CP Gender" + ssiDetails.getCpLegalID());
				 ssiDetails.setCpLegalIdValue(getPromoterDetailsStmt.getString(12));
				 ssiDetails.setFirstName(getPromoterDetailsStmt.getString(13));
				 ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(14)));
				 ssiDetails.setFirstItpan(getPromoterDetailsStmt.getString(15));
				 ssiDetails.setSecondName(getPromoterDetailsStmt.getString(16));
				 ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(17)));
				 ssiDetails.setSecondItpan(getPromoterDetailsStmt.getString(18));
				 ssiDetails.setThirdName(getPromoterDetailsStmt.getString(19));
				 ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(20)));
				 ssiDetails.setThirdItpan(getPromoterDetailsStmt.getString(21));
				 ssiDetails.setSocialCategory(getPromoterDetailsStmt.getString(22));
				 borrowerDetails.setSsiDetails(ssiDetails);
			 }
			getBorrowerDetailsStmt.close();
			getBorrowerDetailsStmt = null;
			getPromoterDetailsStmt.close();
			getPromoterDetailsStmt = null;
			
			 
		}catch (SQLException exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}
		finally{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForBID","Exited");
	   	return borrowerDetails;
   	}

	/*
	 * this metod gets the borrower details for borrower name
	 */
	public BorrowerDetails getBorrowerDetailsForBorrower(String memberId,
										String borrowerName) throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForBorrowerName","Entered");

		Connection connection = DBConnection.getConnection();
		CallableStatement getBorrowerDetailsStmt = null ;

		SSIDetails ssiDetails = new SSIDetails();
		BorrowerDetails borrowerDetails = new BorrowerDetails() ;

		int ssiRefNo = 0;

		String bankId = memberId.substring(0,4);
		String zoneId = memberId.substring(4,8);
		String branchId = memberId.substring(8,12);
		int getStatus = 0;
		Log.log(Log.DEBUG,"GMDAO","getBorrowerDetailsForBorrowerName","name"+borrowerName);
		Log.log(Log.DEBUG,"GMDAO","getBorrowerDetailsForBorrowerName","member"+memberId);
		try {

			getBorrowerDetailsStmt = connection.prepareCall(
				"{?=call funcGetSSIDetailforBorr(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			getBorrowerDetailsStmt.setString(2,bankId);
			getBorrowerDetailsStmt.setString(3,zoneId);
			getBorrowerDetailsStmt.setString(4,branchId);
			getBorrowerDetailsStmt.setString(5,borrowerName);

			getBorrowerDetailsStmt.registerOutParameter(1,
												java.sql.Types.INTEGER) ;
			getBorrowerDetailsStmt.registerOutParameter(32,
												Types.VARCHAR) ;


			getBorrowerDetailsStmt.registerOutParameter(6,Types.INTEGER) ; //SSI Ref No
			getBorrowerDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Already Covered by CGTSI
			getBorrowerDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Already assisted by bank
			getBorrowerDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//SSI Unique No
																			//NPA
			getBorrowerDetailsStmt.registerOutParameter(10,Types.VARCHAR);	//Constitution
			getBorrowerDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//SSI Type
			getBorrowerDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//unit Name
			getBorrowerDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//SSI reg no
			getBorrowerDetailsStmt.registerOutParameter(14,Types.VARCHAR);	//Commencement Date
			getBorrowerDetailsStmt.registerOutParameter(15,Types.DATE);		//SSI ITPAN
			getBorrowerDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//type of Activity
			getBorrowerDetailsStmt.registerOutParameter(17,Types.VARCHAR);	//No Of Employees
			getBorrowerDetailsStmt.registerOutParameter(18,Types.INTEGER);	//project Sales
			getBorrowerDetailsStmt.registerOutParameter(19,Types.DOUBLE);	//Project Exports
			getBorrowerDetailsStmt.registerOutParameter(20,Types.DOUBLE);	//SSI Address
			getBorrowerDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//SSi City
			getBorrowerDetailsStmt.registerOutParameter(22,Types.VARCHAR);		//Pincode
			getBorrowerDetailsStmt.registerOutParameter(23,Types.VARCHAR);	//Display Defaulter
			getBorrowerDetailsStmt.registerOutParameter(24,Types.VARCHAR);	//SSI District
			getBorrowerDetailsStmt.registerOutParameter(25,Types.VARCHAR);	//SSI State
			getBorrowerDetailsStmt.registerOutParameter(26,Types.VARCHAR);	//Industry Nature
			getBorrowerDetailsStmt.registerOutParameter(27,Types.VARCHAR);	//Indutry Sector name
			getBorrowerDetailsStmt.registerOutParameter(28,Types.VARCHAR);	//Status
			getBorrowerDetailsStmt.registerOutParameter(29,Types.VARCHAR);
			getBorrowerDetailsStmt.registerOutParameter(30,Types.VARCHAR);	//BID
			getBorrowerDetailsStmt.registerOutParameter(31,Types.DOUBLE);
			getBorrowerDetailsStmt.execute() ;
			getStatus = getBorrowerDetailsStmt.getInt(1);

			String exception = getBorrowerDetailsStmt.getString(32);

			if(getStatus  == Constants.FUNCTION_SUCCESS){
				Log.log(Log.DEBUG,"borrowerDetails","get ","success dao for BorrowerName SP");
			}
			if(getStatus  == Constants.FUNCTION_FAILURE){
				Log.log(Log.ERROR,"GMDAO","GetBorrwoewerDtls","Exception " + exception);
				getBorrowerDetailsStmt.close();
				getBorrowerDetailsStmt = null;
				throw new DatabaseException(exception);

			}else {
				ssiDetails.setBorrowerRefNo(getBorrowerDetailsStmt.getInt(6));
				borrowerDetails.setPreviouslyCovered(getBorrowerDetailsStmt.getString(7).trim());
				borrowerDetails.setAssistedByBank(getBorrowerDetailsStmt.getString(8).trim());
				borrowerDetails.setAcNo(getBorrowerDetailsStmt.getString(9));
				borrowerDetails.setNpa(getBorrowerDetailsStmt.getString(10).trim());
				ssiDetails.setConstitution(getBorrowerDetailsStmt.getString(11));
				ssiDetails.setSsiType(getBorrowerDetailsStmt.getString(12).trim());
				ssiDetails.setSsiName(getBorrowerDetailsStmt.getString(13));
				ssiDetails.setRegNo(getBorrowerDetailsStmt.getString(14));
				ssiDetails.setCommencementDate(DateHelper.sqlToUtilDate(getBorrowerDetailsStmt.getDate(15)));
				ssiDetails.setSsiITPan(getBorrowerDetailsStmt.getString(16));
				ssiDetails.setActivityType(getBorrowerDetailsStmt.getString(17));
				ssiDetails.setEmployeeNos(getBorrowerDetailsStmt.getInt(18));
				ssiDetails.setProjectedSalesTurnover(getBorrowerDetailsStmt.getDouble(19));
				ssiDetails.setProjectedExports(getBorrowerDetailsStmt.getDouble(20));
				ssiDetails.setAddress(getBorrowerDetailsStmt.getString(21));
				ssiDetails.setCity(getBorrowerDetailsStmt.getString(22));
				ssiDetails.setPincode(getBorrowerDetailsStmt.getString(23).trim());
				ssiDetails.setDistrict(getBorrowerDetailsStmt.getString(25));
				ssiDetails.setState(getBorrowerDetailsStmt.getString(26));
				ssiDetails.setIndustryNature(getBorrowerDetailsStmt.getString(27));
				ssiDetails.setIndustrySector(getBorrowerDetailsStmt.getString(28));
				ssiDetails.setCgbid(getBorrowerDetailsStmt.getString(30));
			//	System.out.println("setCgbid:"+ssiDetails.getCgbid());

				borrowerDetails.setOsAmt(getBorrowerDetailsStmt.getDouble(31));
			//System.out.println("setOsAmt:"+borrowerDetails.getOsAmt());
				borrowerDetails.setSsiDetails(ssiDetails);
				ssiRefNo = (borrowerDetails.getSsiDetails()).getBorrowerRefNo();
				//System.out.println("Borrower ref no :"+ssiRefNo);

			}


			CallableStatement getPromoterDetailsStmt = connection.prepareCall(
			"{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			Log.log(Log.DEBUG,"GMDAO","getPRDetailsForBname","ssiref"+ssiRefNo);
			getPromoterDetailsStmt.setInt(2,ssiRefNo);	//SSIRefNo

			getPromoterDetailsStmt.registerOutParameter(1,Types.INTEGER);
			getPromoterDetailsStmt.registerOutParameter(23,Types.VARCHAR);

			getPromoterDetailsStmt.registerOutParameter(3,Types.INTEGER);	//SSI RefNo
			getPromoterDetailsStmt.registerOutParameter(4,Types.VARCHAR);	//Chief title
			getPromoterDetailsStmt.registerOutParameter(5,Types.VARCHAR);	//First name
			getPromoterDetailsStmt.registerOutParameter(6,Types.VARCHAR);	//Middle Name
			getPromoterDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Last name
			getPromoterDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Chief ITPAN
			getPromoterDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//Gender
			getPromoterDetailsStmt.registerOutParameter(10,Types.DATE);		//DOB
			getPromoterDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//Legal Type
			getPromoterDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//LegalID
			getPromoterDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//Promoter First Name
			getPromoterDetailsStmt.registerOutParameter(14,Types.DATE);		//Promoter First DOB
			getPromoterDetailsStmt.registerOutParameter(15,Types.VARCHAR);	//Promoter FirstITPAN
			getPromoterDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//Promoter Second Name
			getPromoterDetailsStmt.registerOutParameter(17,Types.DATE);		//Promoter Second DOB
			getPromoterDetailsStmt.registerOutParameter(18,Types.VARCHAR);	//Promoter Second ITPAN
			getPromoterDetailsStmt.registerOutParameter(19,Types.VARCHAR);	//Promoter Third Name
			getPromoterDetailsStmt.registerOutParameter(20,Types.DATE);		//Promoter Third DOB
			getPromoterDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//Promoter Third ITPAN
			getPromoterDetailsStmt.registerOutParameter(22,Types.VARCHAR);	//Social Category

			getPromoterDetailsStmt.executeQuery();
			String prException = getPromoterDetailsStmt.getString(23);
			int getPromoterDetailsStatus=getPromoterDetailsStmt.getInt(1);

			if(getPromoterDetailsStatus==Constants.FUNCTION_FAILURE)
			{
				Log.log(Log.ERROR,"GMDAO","GetpromotererDtls","Exception :" + prException );
				getPromoterDetailsStmt.close();
				getPromoterDetailsStmt = null;
				throw new DatabaseException(prException);
			 }	else {
				 ssiDetails.setCpTitle(getPromoterDetailsStmt.getString(4));
				 ssiDetails.setCpFirstName(getPromoterDetailsStmt.getString(5));
				 ssiDetails.setCpMiddleName(getPromoterDetailsStmt.getString(6));
				 ssiDetails.setCpLastName(getPromoterDetailsStmt.getString(7));
				 ssiDetails.setCpITPAN(getPromoterDetailsStmt.getString(8));
				 ssiDetails.setCpGender(getPromoterDetailsStmt.getString(9).trim());
				 ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(10)));
				 ssiDetails.setCpLegalID(getPromoterDetailsStmt.getString(11));
				 ssiDetails.setCpLegalIdValue(getPromoterDetailsStmt.getString(12));
				 ssiDetails.setFirstName(getPromoterDetailsStmt.getString(13));
				 ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(14)));
				 ssiDetails.setFirstItpan(getPromoterDetailsStmt.getString(15));
				 ssiDetails.setSecondName(getPromoterDetailsStmt.getString(16));
				 ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(17)));
				 ssiDetails.setSecondItpan(getPromoterDetailsStmt.getString(18));
				 ssiDetails.setThirdName(getPromoterDetailsStmt.getString(19));
				 ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(20)));
				 ssiDetails.setThirdItpan(getPromoterDetailsStmt.getString(21));
				 ssiDetails.setSocialCategory(getPromoterDetailsStmt.getString(22));

				borrowerDetails.setSsiDetails(ssiDetails);

			 }
			getBorrowerDetailsStmt.close();
			getBorrowerDetailsStmt = null;
			 
			getPromoterDetailsStmt.close();
			getPromoterDetailsStmt = null;

		} catch (SQLException exception) {
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage());
		}finally{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForBorrowerName","Exited");
		return borrowerDetails;
	}

/*
 * this metod gets the borrower details for cgpan
 */
	public BorrowerDetails getBorrowerDetailsForCgpan(String memberId,
								String cgpan)throws DatabaseException {

	Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForCGPAN","Entered");
	Connection connection = DBConnection.getConnection();
	CallableStatement getBorrowerDetailsStmt = null ;

	SSIDetails ssiDetails = new SSIDetails();
	BorrowerDetails borrowerDetails = new BorrowerDetails() ;

	int ssiRefNo = 0;

	String bankId = memberId.substring(0,4);
	String zoneId = memberId.substring(4,8);
	String branchId = memberId.substring(8,12);
	int getStatus = 0;
	Log.log(Log.DEBUG,"GMDAO","getBorrowerDetailsForCGPAN","CGPAN--"+cgpan);
	Log.log(Log.DEBUG,"GMDAO","getBorrowerDetailsForCGPAN","member --"+memberId);
	try {

		getBorrowerDetailsStmt = connection.prepareCall(
			"{?=call funcGetSSIDetailforCGPAN(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                getBorrowerDetailsStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
		getBorrowerDetailsStmt.setString(2,bankId);
		getBorrowerDetailsStmt.setString(3,zoneId);
		getBorrowerDetailsStmt.setString(4,branchId);
		getBorrowerDetailsStmt.setString(5,cgpan);

		getBorrowerDetailsStmt.registerOutParameter(6,Types.INTEGER) ; //SSI Ref No
		getBorrowerDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Already Covered by CGTSI
		getBorrowerDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Already assisted by bank
		getBorrowerDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//SSI Unique No
																		//NPA
		getBorrowerDetailsStmt.registerOutParameter(10,Types.VARCHAR);	//Constitution
		getBorrowerDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//SSI Type
		getBorrowerDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//unit Name
		getBorrowerDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//SSI reg no
		getBorrowerDetailsStmt.registerOutParameter(14,Types.VARCHAR);	//Commencement Date
		getBorrowerDetailsStmt.registerOutParameter(15,Types.DATE);		//SSI ITPAN
		getBorrowerDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//type of Activity
		getBorrowerDetailsStmt.registerOutParameter(17,Types.VARCHAR);	//No Of Employees
		getBorrowerDetailsStmt.registerOutParameter(18,Types.INTEGER);	//project Sales
		getBorrowerDetailsStmt.registerOutParameter(19,Types.DOUBLE);	//Project Exports
		getBorrowerDetailsStmt.registerOutParameter(20,Types.DOUBLE);	//SSI Address
		getBorrowerDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//SSi City
		getBorrowerDetailsStmt.registerOutParameter(22,Types.VARCHAR);		//Pincode
		getBorrowerDetailsStmt.registerOutParameter(23,Types.VARCHAR);	//Display Defaulter
		getBorrowerDetailsStmt.registerOutParameter(24,Types.VARCHAR);	//SSI District
		getBorrowerDetailsStmt.registerOutParameter(25,Types.VARCHAR);	//SSI State
		getBorrowerDetailsStmt.registerOutParameter(26,Types.VARCHAR);	//Industry Nature
		getBorrowerDetailsStmt.registerOutParameter(27,Types.VARCHAR);	//Indutry Sector name
		getBorrowerDetailsStmt.registerOutParameter(28,Types.VARCHAR);	//Status
		getBorrowerDetailsStmt.registerOutParameter(29,Types.VARCHAR);
		getBorrowerDetailsStmt.registerOutParameter(30,Types.VARCHAR);	//BID
		getBorrowerDetailsStmt.registerOutParameter(31,Types.DOUBLE);	//os amt
          //      getBorrowerDetailsStmt.registerOutParameter(32,Types.VARCHAR) ;
                getBorrowerDetailsStmt.registerOutParameter(35,Types.VARCHAR) ;
                
                        getBorrowerDetailsStmt.registerOutParameter(32, Types.VARCHAR);
	                getBorrowerDetailsStmt.registerOutParameter(33, Types.VARCHAR);
	                getBorrowerDetailsStmt.registerOutParameter(34, Types.VARCHAR);
		getBorrowerDetailsStmt.execute() ;
		getStatus = getBorrowerDetailsStmt.getInt(1);

		//String exception = 	getBorrowerDetailsStmt.getString(32);
		 String exception =      getBorrowerDetailsStmt.getString(35);
		if(getStatus  == Constants.FUNCTION_SUCCESS){
			Log.log(Log.DEBUG,"getborrowerDetails","success dao for CGPAN SP","--");
		}
		if(getStatus  == Constants.FUNCTION_FAILURE){
			Log.log(Log.ERROR,"GMDAO","getborrowerDetails","Exception " + exception);
			getBorrowerDetailsStmt.close();
			getBorrowerDetailsStmt = null;
			throw new DatabaseException(exception);
		}else {
			ssiDetails.setBorrowerRefNo(getBorrowerDetailsStmt.getInt(6));
			borrowerDetails.setPreviouslyCovered(getBorrowerDetailsStmt.getString(7).trim());
			borrowerDetails.setAssistedByBank(getBorrowerDetailsStmt.getString(8).trim());
			borrowerDetails.setAcNo(getBorrowerDetailsStmt.getString(9));
			borrowerDetails.setNpa(getBorrowerDetailsStmt.getString(10).trim());
			ssiDetails.setConstitution(getBorrowerDetailsStmt.getString(11));
			ssiDetails.setSsiType(getBorrowerDetailsStmt.getString(12).trim());
			ssiDetails.setSsiName(getBorrowerDetailsStmt.getString(13));
			ssiDetails.setRegNo(getBorrowerDetailsStmt.getString(14));
			ssiDetails.setCommencementDate(DateHelper.sqlToUtilDate(getBorrowerDetailsStmt.getDate(15)));
			ssiDetails.setSsiITPan(getBorrowerDetailsStmt.getString(16));
			ssiDetails.setActivityType(getBorrowerDetailsStmt.getString(17));
			ssiDetails.setEmployeeNos(getBorrowerDetailsStmt.getInt(18));
			ssiDetails.setProjectedSalesTurnover(getBorrowerDetailsStmt.getDouble(19));
			ssiDetails.setProjectedExports(getBorrowerDetailsStmt.getDouble(20));
			ssiDetails.setAddress(getBorrowerDetailsStmt.getString(21));
			ssiDetails.setCity(getBorrowerDetailsStmt.getString(22));
			ssiDetails.setPincode(getBorrowerDetailsStmt.getString(23).trim());
			ssiDetails.setDistrict(getBorrowerDetailsStmt.getString(25));
			ssiDetails.setState(getBorrowerDetailsStmt.getString(26));
			ssiDetails.setIndustryNature(getBorrowerDetailsStmt.getString(27));
			ssiDetails.setIndustrySector(getBorrowerDetailsStmt.getString(28));
			ssiDetails.setCgbid(getBorrowerDetailsStmt.getString(30));
			borrowerDetails.setOsAmt(getBorrowerDetailsStmt.getDouble(31));
                        
                                ssiDetails.setEnterprise(getBorrowerDetailsStmt.getString(32));
		                ssiDetails.setUnitAssisted(getBorrowerDetailsStmt.getString(33));
		                ssiDetails.setWomenOperated(getBorrowerDetailsStmt.getString(34));
                        
			borrowerDetails.setSsiDetails(ssiDetails);
			ssiRefNo = (borrowerDetails.getSsiDetails()).getBorrowerRefNo();
		}


		CallableStatement getPromoterDetailsStmt = connection.prepareCall(
		"{?=call funcGetPromoterDtlforSSIRef(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

		getPromoterDetailsStmt.registerOutParameter(1,Types.INTEGER);
		getPromoterDetailsStmt.registerOutParameter(23,Types.VARCHAR);

		getPromoterDetailsStmt.setInt(2,ssiRefNo);	//SSIRefNo
		getPromoterDetailsStmt.registerOutParameter(3,Types.INTEGER);	//SSI RefNo
		getPromoterDetailsStmt.registerOutParameter(4,Types.VARCHAR);	//Chief title
		getPromoterDetailsStmt.registerOutParameter(5,Types.VARCHAR);	//First name
		getPromoterDetailsStmt.registerOutParameter(6,Types.VARCHAR);	//Middle Name
		getPromoterDetailsStmt.registerOutParameter(7,Types.VARCHAR);	//Last name
		getPromoterDetailsStmt.registerOutParameter(8,Types.VARCHAR);	//Chief ITPAN
		getPromoterDetailsStmt.registerOutParameter(9,Types.VARCHAR);	//Gender
		getPromoterDetailsStmt.registerOutParameter(10,Types.DATE);		//DOB
		getPromoterDetailsStmt.registerOutParameter(11,Types.VARCHAR);	//Legal Type
		getPromoterDetailsStmt.registerOutParameter(12,Types.VARCHAR);	//LegalID
		getPromoterDetailsStmt.registerOutParameter(13,Types.VARCHAR);	//Promoter First Name
		getPromoterDetailsStmt.registerOutParameter(14,Types.DATE);		//Promoter First DOB
		getPromoterDetailsStmt.registerOutParameter(15,Types.VARCHAR);	//Promoter FirstITPAN
		getPromoterDetailsStmt.registerOutParameter(16,Types.VARCHAR);	//Promoter Second Name
		getPromoterDetailsStmt.registerOutParameter(17,Types.DATE);		//Promoter Second DOB
		getPromoterDetailsStmt.registerOutParameter(18,Types.VARCHAR);	//Promoter Second ITPAN
		getPromoterDetailsStmt.registerOutParameter(19,Types.VARCHAR);	//Promoter Third Name
		getPromoterDetailsStmt.registerOutParameter(20,Types.DATE);		//Promoter Third DOB
		getPromoterDetailsStmt.registerOutParameter(21,Types.VARCHAR);	//Promoter Third ITPAN
		getPromoterDetailsStmt.registerOutParameter(22,Types.VARCHAR);	//Social Categorys

		getPromoterDetailsStmt.executeQuery();

		String prException = getPromoterDetailsStmt.getString(23);
		int getPromoterDetailsStatus=getPromoterDetailsStmt.getInt(1);

		 if(getPromoterDetailsStatus==Constants.FUNCTION_FAILURE )
		 {
			Log.log(Log.ERROR,"GMDAO","GetpromotererDtls","Exception " + prException);
			getPromoterDetailsStmt.close();
			getPromoterDetailsStmt = null;
			throw new DatabaseException(prException);
		 }else
		 {
			 ssiDetails.setCpTitle(getPromoterDetailsStmt.getString(4));
			 ssiDetails.setCpFirstName(getPromoterDetailsStmt.getString(5));
			 ssiDetails.setCpMiddleName(getPromoterDetailsStmt.getString(6));
			 ssiDetails.setCpLastName(getPromoterDetailsStmt.getString(7));
			 ssiDetails.setCpITPAN(getPromoterDetailsStmt.getString(8));
			 ssiDetails.setCpGender(getPromoterDetailsStmt.getString(9).trim());
			 ssiDetails.setCpDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(10)));
			 ssiDetails.setCpLegalID(getPromoterDetailsStmt.getString(11));
			 ssiDetails.setCpLegalIdValue(getPromoterDetailsStmt.getString(12));
			 ssiDetails.setFirstName(getPromoterDetailsStmt.getString(13));
			 ssiDetails.setFirstDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(14)));
			 ssiDetails.setFirstItpan(getPromoterDetailsStmt.getString(15));
			 ssiDetails.setSecondName(getPromoterDetailsStmt.getString(16));
			 ssiDetails.setSecondDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(17)));
			 ssiDetails.setSecondItpan(getPromoterDetailsStmt.getString(18));
			 ssiDetails.setThirdName(getPromoterDetailsStmt.getString(19));
			 ssiDetails.setThirdDOB(DateHelper.sqlToUtilDate(getPromoterDetailsStmt.getDate(20)));
			 ssiDetails.setThirdItpan(getPromoterDetailsStmt.getString(21));
			 ssiDetails.setSocialCategory(getPromoterDetailsStmt.getString(22));
			 borrowerDetails.setSsiDetails(ssiDetails);
		 }

		getBorrowerDetailsStmt.close();
		getBorrowerDetailsStmt = null;
		
		getPromoterDetailsStmt.close();
		getPromoterDetailsStmt = null;

	} catch (SQLException exception) {
		throw new DatabaseException(exception.getMessage());
	}finally{
		DBConnection.freeConnection(connection);
	}

	Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForCGPAN","Exited");
	return borrowerDetails;


}



   /**
    * This method returns an arraylist of OutstandingDetail object. The parameter
    * passed to this object are id and type.
    *
    * If type = 0, the id passed will be borrower id. All the applications belonging
    * to this borrower will be fetched in this case.
    *
    * If type = 1, the id passed will be CGPAN. The corresponding application for
    * this CGPAN will be fetched.
    * @param id
    * @param type
    * @return ArrayList
    * @roseuid 397BCEB00132
    */
   public ArrayList getOutstandingDetails(String id, int type)
   													 throws DatabaseException,
   													  SQLException
   	{
		Log.log(Log.INFO,"GMDAO","getOutstandingDetails","Entered");

		OutstandingDetail outstandingDetail = null ;

		PeriodicInfo periodicInfo=new PeriodicInfo();

		ArrayList periodicInfos = new ArrayList();

		ArrayList listOfOutstandingDetail = new ArrayList();

		Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","Id passed is "+id);

		Connection connection = DBConnection.getConnection() ;

		CallableStatement getOutstandingDetailStmt = null ;

		ResultSet resultSet = null;

		ResultSet cgpanResultSet = null;

		CallableStatement getOutDetailForCgpanStmt = null ;

		String panType = null;
		int cgpanLength = 0;


		String cgpan = null;
		String appType = null;
		int len = 0;
		int size = 0;

		try
		{
			String exception = "" ;

			String functionName=null;

			ArrayList outDtls = new ArrayList();

			if(type==GMConstants.TYPE_ZERO )
			{
				functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforBID(?,?,?)}"	;
				getOutstandingDetailStmt = connection.prepareCall(functionName);
				getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getOutstandingDetailStmt.setString(2, id);
				getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				getOutstandingDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getOutstandingDetailStmt.execute() ;

				exception = getOutstandingDetailStmt.getString(4) ;
				int error=getOutstandingDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","GET OutstandingForBID","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getOutstandingDetailStmt.close();
					getOutstandingDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForBID","Exception "+exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				// Reading the resultset
				resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				boolean firstTime=true;

				String cgpan1 = null;

				while (resultSet.next())
				{
					if(firstTime)
					{
						periodicInfo.setBorrowerId(resultSet.getString(1));
						periodicInfo.setBorrowerName(resultSet.getString(4));
						firstTime=false;
					}
					cgpan1 = resultSet.getString(2);
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","cgpan from view : "+ cgpan1);
					if(cgpan1 != null)
					{
						outstandingDetail = new OutstandingDetail();
						outstandingDetail.setCgpan(cgpan1);
						outstandingDetail.setScheme(resultSet.getString(3));
						outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(6));
						outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(7));
						listOfOutstandingDetail.add(outstandingDetail);
					}
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set View");
			    }
				resultSet.close();
				resultSet = null;

				getOutstandingDetailStmt.close();
				getOutstandingDetailStmt = null;

				size = listOfOutstandingDetail.size();
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","size of out dtls : "+ size);

				for(int i = 0; i<size; ++i)
				{
					OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
					cgpan = outDtl.getCgpan() ;
					Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","inside for loop cgpan : "+ cgpan);

					len = cgpan.length();

					appType = cgpan.substring(len-2,len-1);

					ArrayList outAmounts = new ArrayList();

					if(appType.equalsIgnoreCase(GMConstants.CGPAN_TC))
					{
						getOutDetailForCgpanStmt = connection.prepareCall("{?= call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						getOutDetailForCgpanStmt.setString(2, cgpan);
						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						}

						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							connection.rollback();
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();
							outAmount.setCgpan(cgpan);
							outAmount.setTcoId(cgpanResultSet.getString(1));
							outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));

							outAmounts.add(outAmount);
							Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set for Cgpan TC");
						}

						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;
					}
					else if(appType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
							appType.equalsIgnoreCase(GMConstants.CGPAN_RENEWAL))
					{
						getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","Cgpan WC ");
						getOutDetailForCgpanStmt.setString(2, cgpan);

						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GET WC Outstanding","Success");
						}

						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GET WC Outstanding","Exception"+exception);
							connection.rollback() ;
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();
							outAmount.setCgpan(cgpan);

							outAmount.setWcoId(cgpanResultSet.getString(1));
							outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));
							outAmounts.add(outAmount);
							Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set for Cgpan WC");
						}
						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;
					}
					outDtl.setOutstandingAmounts(outAmounts);
					outDtls.add(outDtl);
 				}
				periodicInfo.setOutstandingDetails(outDtls);
				Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","OutDtls size "+outDtls.size());
				periodicInfos.add(periodicInfo);
			}
			else if(type==GMConstants.TYPE_ONE)
			{
				functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforCGPAN(?,?,?)}";

	    		getOutstandingDetailStmt = connection.prepareCall(functionName);

				getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getOutstandingDetailStmt.setString(2, id);
				getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				getOutstandingDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getOutstandingDetailStmt.execute() ;

				exception = getOutstandingDetailStmt.getString(4) ;

				int error=getOutstandingDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","getOutstandinForCGPAN","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getOutstandingDetailStmt.close();
					getOutstandingDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForCGPAN","Exception "+exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				// Reading the resultset
				resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				cgpanLength = id.length();

				panType = id.substring((cgpanLength-2), cgpanLength-1);

				if(panType.equalsIgnoreCase(GMConstants.CGPAN_TC))
				{
					while(resultSet.next())
					{
						outstandingDetail = new OutstandingDetail();

						periodicInfo.setBorrowerId(resultSet.getString(1));
						outstandingDetail.setCgpan(resultSet.getString(2));
						periodicInfo.setBorrowerName(resultSet.getString(3));
						outstandingDetail.setScheme(resultSet.getString(4));
						outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						listOfOutstandingDetail.add(outstandingDetail);
					}
					resultSet.close();
					resultSet = null;

					getOutstandingDetailStmt.close();
					getOutstandingDetailStmt = null;

					size = listOfOutstandingDetail.size();

					for(int i = 0; i<size; ++i)
					{
						OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
						cgpan = outDtl.getCgpan() ;

						len = cgpan.length();

						appType = cgpan.substring(len-2,len-1);

						ArrayList outAmounts = new ArrayList();

						getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						getOutDetailForCgpanStmt.setString(2, cgpan);
						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						}

						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							connection.rollback();	
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();

							outAmount.setCgpan(cgpan);

							outAmount.setTcoId(cgpanResultSet.getString(1));
							outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));
							outAmounts.add(outAmount);
						}
						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;

						outDtl.setOutstandingAmounts(outAmounts);
						outDtls.add(outDtl);
					}
				}
				else if(panType.equalsIgnoreCase(GMConstants.CGPAN_WC))
				{
					while(resultSet.next())
					{
						outstandingDetail = new OutstandingDetail();

						periodicInfo.setBorrowerId(resultSet.getString(1));
						outstandingDetail.setCgpan(resultSet.getString(2));
						periodicInfo.setBorrowerName(resultSet.getString(3));
						outstandingDetail.setScheme(resultSet.getString(4));
						outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(5));
						outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(6));

						listOfOutstandingDetail.add(outstandingDetail);
					}
					resultSet.close();
					resultSet = null;

					getOutstandingDetailStmt.close();
					getOutstandingDetailStmt = null;

					size = listOfOutstandingDetail.size();

					for(int i = 0; i<size; ++i)
					{
						OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
						cgpan = outDtl.getCgpan() ;

						len = cgpan.length();

						appType = cgpan.substring(len-2,len-1);

						ArrayList outAmounts = new ArrayList();

						getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						getOutDetailForCgpanStmt.setString(2, cgpan);
						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GETWcOutstanding","Success");
						}

						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GETWcOutstanding","Exception "+exception);
							connection.rollback();
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

					    OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();
							outAmount.setCgpan(cgpan);
							outAmount.setWcoId(cgpanResultSet.getString(1));
							outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));

							outAmounts.add(outAmount);
						}

						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;

						outDtl.setOutstandingAmounts(outAmounts);
						outDtls.add(outDtl);
					}
				}
				periodicInfo.setOutstandingDetails(outDtls);
				periodicInfos.add(periodicInfo);
			}
			else if(type==GMConstants.TYPE_TWO)
			{
				functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforBorr(?,?,?)}"	;
				getOutstandingDetailStmt = connection.prepareCall(functionName);
				getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

				getOutstandingDetailStmt.setString(2, id);
				getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				getOutstandingDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getOutstandingDetailStmt.execute() ;

				exception = getOutstandingDetailStmt.getString(4) ;

				int error=getOutstandingDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","GET OTS For Borr Name SP","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getOutstandingDetailStmt.close();
					getOutstandingDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForBorrower","Exception "+exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				// Reading the resultset
				resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				boolean firstTime=true;

				String cgpan1 = null;

				while (resultSet.next())
				{
					outstandingDetail = new OutstandingDetail();

					if(firstTime)
					{
						periodicInfo.setBorrowerId(resultSet.getString(1));
						periodicInfo.setBorrowerName(resultSet.getString(4));
						firstTime=false;
					}
					cgpan1 = resultSet.getString(2);
					if(cgpan1 != null)
					{
						outstandingDetail.setCgpan(cgpan1);
						outstandingDetail.setScheme(resultSet.getString(3));
						outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(6));
						outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(7));

						listOfOutstandingDetail.add(outstandingDetail);
					}
			    }
				resultSet.close();
				resultSet = null;

				getOutstandingDetailStmt.close();
				getOutstandingDetailStmt = null;

				size = listOfOutstandingDetail.size();

				for(int i = 0; i<size; ++i)
				{
					OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
					cgpan = outDtl.getCgpan() ;

					len = cgpan.length();

					appType = cgpan.substring(len-2,len-1);

					ArrayList outAmounts = new ArrayList();

					if(appType.equalsIgnoreCase(GMConstants.CGPAN_TC))
					{
						getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						getOutDetailForCgpanStmt.setString(2, cgpan);
						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						}

						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							connection.rollback();
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;
						OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();
							outAmount.setCgpan(cgpan);
							outAmount.setTcoId(cgpanResultSet.getString(1));
							outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));
							outAmounts.add(outAmount);
						}
						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;
					}
					else if(appType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
							appType.equalsIgnoreCase(GMConstants.CGPAN_RENEWAL))
					{
						getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
						getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						getOutDetailForCgpanStmt.setString(2, cgpan);
						getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						getOutDetailForCgpanStmt.registerOutParameter(4,
															java.sql.Types.VARCHAR) ;

						getOutDetailForCgpanStmt.execute() ;

						exception = getOutDetailForCgpanStmt.getString(4) ;

						error=getOutDetailForCgpanStmt.getInt(1);

						if(error == Constants.FUNCTION_SUCCESS)
						{
							Log.log(Log.DEBUG,"GMDAO","GET WC Outstanding","Success");
						}
						if(error==Constants.FUNCTION_FAILURE)
						{
							getOutDetailForCgpanStmt.close();
							getOutDetailForCgpanStmt=null;
							Log.log(Log.ERROR,"GMDAO","GET WC Outstanding","Exception "+exception);
							connection.rollback();
							throw new DatabaseException(exception);
						}

						// Reading the resultset
						cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						OutstandingAmount outAmount = null;

						while(cgpanResultSet.next())
						{
							outAmount = new OutstandingAmount();

							outAmount.setCgpan(cgpan);

							outAmount.setWcoId(cgpanResultSet.getString(1));
							outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));
							outAmounts.add(outAmount);
						}
						cgpanResultSet.close();
						cgpanResultSet = null;

						getOutDetailForCgpanStmt.close();
						getOutDetailForCgpanStmt=null;
					}
					outDtl.setOutstandingAmounts(outAmounts);
					outDtls.add(outDtl);
				}
				periodicInfo.setOutstandingDetails(outDtls);
				periodicInfos.add(periodicInfo);
			}
			connection.commit();
		}catch (SQLException exception)
		{
			Log.logException(exception);
			throw new DatabaseException(exception.getMessage()) ;
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","getOutstandingDetails","Exited");
		return  periodicInfos;
   }


   /**
    * This method is used to update the outstanding details for the given CGPAN in
    * the OutstandingDetail object passed as a parameter.
    * @param osDtl
    * @return Boolean
    * @roseuid 397BD2D200F7
    */
   public boolean insertTcOutstandingDetails(OutstandingAmount outstandingAmount)
   									 throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMDAO","insertTcOutstandingDetails","Entered:");
		Connection connection = DBConnection.getConnection();
		CallableStatement insertOutstandingDetailsStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateOutstandingStatus = false;

		java.util.Date utilDate = null;
		java.sql.Date sqlDate = null;

		String exception = null;

		String cgpan = outstandingAmount.getCgpan() ;
		int cgpanLen = cgpan.length() ;
		String cgpanType = cgpan.substring(cgpanLen-2, cgpanLen-1);
		Log.log(Log.DEBUG,"GMDAO","insertTcOutstandingDetails","cgpan :"+cgpan);

		try	
		{
			if (outstandingAmount!=null) 
			{
				if (cgpanType.equalsIgnoreCase(GMConstants.CGPAN_TC)) 
				{
					sqlDate = new java.sql.Date(0);
					utilDate = new java.util.Date();

					/*Creates a CallableStatement object for calling database
					 * stored procedures*/

					insertOutstandingDetailsStmt = connection.prepareCall(
							"{?=call funcInsertTCOutStanding(?,?,?,?)}");

					insertOutstandingDetailsStmt .registerOutParameter(1,
													 java.sql.Types.INTEGER);
					insertOutstandingDetailsStmt .setString(2,
												outstandingAmount.getCgpan());

					insertOutstandingDetailsStmt .setDouble(3,
							outstandingAmount.getTcPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMDAO","insertTcOutstandingDetails","Tc Amt :"+
						outstandingAmount.getTcPrincipalOutstandingAmount());

					utilDate = outstandingAmount.getTcOutstandingAsOnDate() ;
					sqlDate = new java.sql.Date(utilDate.getTime());
					/*
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					String formatedDate=dateFormat.format(utilDate);					
					sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
					*/

					Log.log(Log.DEBUG,"GMDAO","insertTcOutstandingDetails","Tc Date :"+sqlDate);
					insertOutstandingDetailsStmt .setDate(4,sqlDate);

					insertOutstandingDetailsStmt .registerOutParameter(5,
														java.sql.Types.VARCHAR);

					insertOutstandingDetailsStmt .executeQuery();

					updateStatus=insertOutstandingDetailsStmt .getInt(1);

					exception = insertOutstandingDetailsStmt .getString(5);

					if (updateStatus==Constants.FUNCTION_SUCCESS) 
					{
						updateOutstandingStatus = true;
						Log.log(Log.DEBUG,"GMDAO","insertOutstandingDetailsTC","Success");
					}
					else if (updateStatus==Constants.FUNCTION_FAILURE) 
					{
						updateOutstandingStatus  = false;
						insertOutstandingDetailsStmt.close();
						insertOutstandingDetailsStmt  = null;
						Log.log(Log.ERROR,"GMDAO","insertOutstandingDetailsTC","Error "+exception);
						connection.rollback() ;
						throw new DatabaseException(exception);
					}
					insertOutstandingDetailsStmt.close();
					insertOutstandingDetailsStmt  = null;
					connection.commit() ;

				}
			}//end of condt outstanding detail!=null
		}catch (Exception sqlexception) {
			Log.logException(sqlexception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(sqlexception.getMessage());
		}finally {
			DBConnection.freeConnection(connection);
	   	}
		Log.log(Log.INFO,"GMDAO","insertTcOutstandingDetails","Exited");
   		return updateOutstandingStatus;
 	}


	public boolean insertWcOutstandingDetails(OutstandingAmount outstandingAmount)
									  throws DatabaseException, SQLException {
		 Log.log(Log.INFO,"GMDAO","insertWcOutstandingDetails","Entered:");
		 Connection connection = DBConnection.getConnection();
		 CallableStatement updateOutstandingDetailsStmt = null;
		 int updateStatus=0;

		 //indicates whether the stored procedure was executed successfully
		 boolean updateOutstandingStatus = false;

		 java.util.Date utilDate = null;
		 java.sql.Date sqlDate = null;

		 String exception = null;

		 String cgpan = outstandingAmount.getCgpan() ;
		 int cgpanLen = cgpan.length() ;
		 String cgpanType = cgpan.substring(cgpanLen-2, cgpanLen-1);

		 Log.log(Log.DEBUG,"GMDAO","insertWcOutstandingDetails","cgpan :"+cgpan);

		 try {
			 if (outstandingAmount!=null) 
			 {
				 if( cgpanType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
				 	cgpanType.equalsIgnoreCase(GMConstants.CGPAN_RENEWAL) ) 
				 {

						 updateOutstandingDetailsStmt= connection.prepareCall(
								 "{?=call funcInsertWCOutStanding(?,?,?,?,?,?,?,?)}");

						 updateOutstandingDetailsStmt.registerOutParameter(1,
													  java.sql.Types.INTEGER);
						 updateOutstandingDetailsStmt.setString(2,
									 outstandingAmount.getCgpan());

				 		updateOutstandingDetailsStmt.setDouble(3,
							outstandingAmount.getWcFBPrincipalOutstandingAmount());
						Log.log(Log.DEBUG,"GMDAO","insertWcOutstandingDetails","Wc FB Pr Amt :"+
							outstandingAmount.getWcFBPrincipalOutstandingAmount());

						updateOutstandingDetailsStmt.setDouble(4,
							outstandingAmount.getWcFBInterestOutstandingAmount());


						 utilDate = outstandingAmount.getWcFBOutstandingAsOnDate();
						 if(utilDate!=null && !utilDate.toString().equals(""))
						 {
							sqlDate = new java.sql.Date (utilDate.getTime ());
							updateOutstandingDetailsStmt.setDate(5,sqlDate);
						 }
						 else{
						 	
							updateOutstandingDetailsStmt.setNull(5,java.sql.Types.DATE);
						 }
						 
/*						 SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
						 String formatedDate=dateFormat.format(utilDate);
						 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
						 Log.log(Log.DEBUG,"GMDAO","insertWcOutstandingDetails","wc Fb date :"+sqlDate);
						 

						 updateOutstandingDetailsStmt.setDouble(6,
					outstandingAmount.getWcNFBPrincipalOutstandingAmount());

						 updateOutstandingDetailsStmt.setDouble(7,
					outstandingAmount.getWcNFBInterestOutstandingAmount());


						 utilDate = outstandingAmount.getWcNFBOutstandingAsOnDate();

						if(utilDate!=null && !utilDate.toString().equals(""))
						{
							sqlDate = new java.sql.Date (utilDate.getTime());
							
							updateOutstandingDetailsStmt.setDate(8,sqlDate);
						}
						else{
							updateOutstandingDetailsStmt.setNull(8,java.sql.Types.DATE);
						}
						 
/*						 SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
						 String formatedDate1 = dateFormat.format(utilDate);
						 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));
*/
						 Log.log(Log.DEBUG,"GMDAO","insertWcOutstandingDetails","wc NFb date :"+sqlDate); 
						 


						 updateOutstandingDetailsStmt.registerOutParameter(9,
															 java.sql.Types.VARCHAR);

						 updateOutstandingDetailsStmt.executeQuery();
						 updateStatus=Integer.parseInt(
								 updateOutstandingDetailsStmt.getObject(1).toString());

						exception = updateOutstandingDetailsStmt.getString(9);

						 if (updateStatus==Constants.FUNCTION_SUCCESS) {
							 updateOutstandingStatus = true;
							 Log.log(Log.DEBUG,"GMDAO","insertWcOutstandingDetailsWC","Success");
						 }
						 else if (updateStatus==Constants.FUNCTION_FAILURE ) {
							 updateOutstandingStatus  = false;
							 updateOutstandingDetailsStmt.close();
							 updateOutstandingDetailsStmt = null;
							 Log.log(Log.ERROR,"GMDAO","insertWcOutstandingDetailsWC","Exception :"+exception);
							 connection.rollback() ;
							 throw new DatabaseException(exception);
						 }
						updateOutstandingDetailsStmt.close();
						updateOutstandingDetailsStmt = null;
						connection.commit() ;

					 }
			 }//end of condt outstanding detail!=null
		 }catch (Exception sqlexception) {
			 Log.logException(sqlexception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			 throw new DatabaseException(sqlexception.getMessage());
		 }finally {
			 DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","insertWcOutstandingDetails","Exited");
		 return updateOutstandingStatus;
	 }


	public boolean updateTcOutstandingDetails(OutstandingAmount outstandingAmount)
									  throws DatabaseException, SQLException {
		 Log.log(Log.INFO,"GMDAO","updateTcOutstandingDetails","Entered:");
		 Connection connection = DBConnection.getConnection();
		 CallableStatement updateOutstandingDetailsStmt = null;
		 int updateStatus=0;

		 //indicates whether the stored procedure was executed successfully
		 boolean updateOutstandingStatus = false;

		 java.util.Date utilDate = null;
		 java.sql.Date sqlDate = null;

		 String exception = null;

		 String cgpan = outstandingAmount.getCgpan() ;
		 int cgpanLen = cgpan.length() ;
		 String cgpanType = cgpan.substring(cgpanLen-2, cgpanLen-1);
		 Log.log(Log.DEBUG,"GMDAO","updateTcOutstandingDetails","cgpan :"+cgpan);

		 try
		 {
			 if (outstandingAmount!=null)
			 {
				 if (cgpanType.equalsIgnoreCase(GMConstants.CGPAN_TC))
				 {
					 sqlDate = new java.sql.Date(0);
					 utilDate = new java.util.Date();

					 /*Creates a CallableStatement object for calling database
					  * stored procedures*/
					 updateOutstandingDetailsStmt= connection.prepareCall(
							 "{?=call funcUpdTCOutStanding(?,?,?,?)}");

					 updateOutstandingDetailsStmt.registerOutParameter(1,
													  java.sql.Types.INTEGER);
					 updateOutstandingDetailsStmt.setString(2,
												 outstandingAmount.getTcoId());
					Log.log(Log.DEBUG,"GMDAO","updateTcOutstandingDetails","Tc ID :"+
							outstandingAmount.getTcoId());
			 		updateOutstandingDetailsStmt.setDouble(3,
			 			outstandingAmount.getTcPrincipalOutstandingAmount());

					Log.log(Log.DEBUG,"GMDAO","updateTcOutstandingDetails","Tc Amt :"+
					outstandingAmount.getTcPrincipalOutstandingAmount());

					utilDate = outstandingAmount.getTcOutstandingAsOnDate() ;
					sqlDate = new java.sql.Date(utilDate.getTime());
					/*
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					String formatedDate=dateFormat.format(utilDate);
					sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
					*/
					Log.log(Log.DEBUG,"GMDAO","updateTcOutstandingDetails","tc date :"+sqlDate);
					updateOutstandingDetailsStmt.setDate(4,sqlDate);


					updateOutstandingDetailsStmt.registerOutParameter(5,
														 java.sql.Types.VARCHAR);

					updateOutstandingDetailsStmt.executeQuery();

					updateStatus=updateOutstandingDetailsStmt.getInt(1);
					exception = updateOutstandingDetailsStmt.getString(5);

					if (updateStatus==Constants.FUNCTION_SUCCESS) {
						updateOutstandingStatus = true;
						Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetailsFor TC","Success");
					 }
					 else if (updateStatus==Constants.FUNCTION_FAILURE) {
						 updateOutstandingStatus  = false;
						 updateOutstandingDetailsStmt.close();
						 updateOutstandingDetailsStmt = null;
						 Log.log(Log.ERROR,"GMDAO","updateOutstandingDetailsFor TC","Exception "+exception);
						 connection.rollback() ;
						 throw new DatabaseException(exception);
					 }
					updateOutstandingDetailsStmt.close();
					updateOutstandingDetailsStmt = null;
					connection.commit() ;
				 }
			}//end of condt outstanding detail!=null
		 }catch (Exception sqlexception) {
			 Log.logException(sqlexception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			 throw new DatabaseException(sqlexception.getMessage());
		 }finally {
			 DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","updateTcOutstandingDetails","Exited");
		 return updateOutstandingStatus;
	 }



	public boolean updateWcOutstandingDetails(OutstandingAmount outstandingAmount)
									  throws DatabaseException, SQLException {
		 Log.log(Log.INFO,"GMDAO","updateWcOutstandingDetails","Entered:");
		 Connection connection = DBConnection.getConnection();
		 CallableStatement updateOutstandingDetailsStmt = null;
		 int updateStatus=0;

		 //indicates whether the stored procedure was executed successfully
		 boolean updateOutstandingStatus = false;

		 java.util.Date utilDate = null;
		 java.sql.Date sqlDate = null;

		 String cgpan = outstandingAmount.getCgpan() ;
		 int cgpanLen = cgpan.length() ;
		 String cgpanType = cgpan.substring(cgpanLen-2, cgpanLen-1);
		 Log.log(Log.DEBUG,"GMDAO","updateWcOutstandingDetails","cgpan :"+cgpan);

		 try
		 {
			 if (outstandingAmount!=null) 
			 {
				 if(cgpanType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
					cgpanType.equalsIgnoreCase(GMConstants.CGPAN_WC)) 
				 {

					 updateOutstandingDetailsStmt= connection.prepareCall(
							 "{?=call funcUpdWCOutStanding(?,?,?,?,?,?,?,?)}");

					 updateOutstandingDetailsStmt.registerOutParameter(1,
												  java.sql.Types.INTEGER);
					 updateOutstandingDetailsStmt.setString(2,
								 	outstandingAmount.getWcoId());

					Log.log(Log.DEBUG,"GMDAO","updateWcOutstandingDetails","Wc ID *:"+
						outstandingAmount.getWcoId());
			 		updateOutstandingDetailsStmt.setDouble(3,
						outstandingAmount.getWcFBPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMDAO","updateWcOutstandingDetails","Wc FB Pr Amt *:"+
							outstandingAmount.getWcFBPrincipalOutstandingAmount());

					 updateOutstandingDetailsStmt.setDouble(4,
						outstandingAmount.getWcFBInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMDAO","updateWcOutstandingDetails","Wc FB Int Amt *:"+
						outstandingAmount.getWcFBInterestOutstandingAmount());

					 utilDate = outstandingAmount.getWcFBOutstandingAsOnDate();
					 if (utilDate!=null)
					 {
						sqlDate = new java.sql.Date (utilDate.getTime ());
						Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date *:"+sqlDate);
						updateOutstandingDetailsStmt.setDate(5,sqlDate);
					 }
					 else
					 {
						Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date *: null");
						updateOutstandingDetailsStmt.setNull(5,java.sql.Types.DATE);
					 }

/*					 SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					 String formatedDate=dateFormat.format(utilDate);
					 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
					 Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date *:"+sqlDate);
					 updateOutstandingDetailsStmt.setDate(5,sqlDate);

					Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date* :"+outstandingAmount.getWcNFBPrincipalOutstandingAmount());
					 updateOutstandingDetailsStmt.setDouble(6,
						outstandingAmount.getWcNFBPrincipalOutstandingAmount());

					Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date *:"+outstandingAmount.getWcNFBInterestOutstandingAmount());
					 updateOutstandingDetailsStmt.setDouble(7,
						outstandingAmount.getWcNFBInterestOutstandingAmount());

					Log.log(Log.DEBUG,"GMDAO","updateOutstandingDetails","wc Fb date* :"+outstandingAmount.getWcNFBOutstandingAsOnDate());
					 utilDate = outstandingAmount.getWcNFBOutstandingAsOnDate();
					 if (utilDate!=null)
					 {
						sqlDate = new java.sql.Date (utilDate.getTime());
						Log.log(Log.DEBUG,"GMDAO--","out--","wc NFb date *:"+sqlDate); 
						updateOutstandingDetailsStmt.setDate(8,sqlDate);
					 }
					 else
					 {
						Log.log(Log.DEBUG,"GMDAO--","out--","wc NFb date *:null"); 
						updateOutstandingDetailsStmt.setNull(8,java.sql.Types.DATE);
					 }
					 /*String formatedDate1 = dateFormat.format(utilDate);
					 sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));
*/



					 updateOutstandingDetailsStmt.registerOutParameter(9,
														 java.sql.Types.VARCHAR);

					 updateOutstandingDetailsStmt.executeQuery();

					 updateStatus=Integer.parseInt(
							 updateOutstandingDetailsStmt.getObject(1).toString());

					String exception = updateOutstandingDetailsStmt.getString(9);
					 if (updateStatus==Constants.FUNCTION_SUCCESS) {
						 updateOutstandingStatus = true;
						 Log.log(Log.DEBUG,"GMDAO","updateWcOutstandingDetails","Success");
					 }
					 else if (updateStatus==Constants.FUNCTION_FAILURE) {
						 updateOutstandingStatus  = false;
						 updateOutstandingDetailsStmt.close();
						 updateOutstandingDetailsStmt = null;
						 Log.log(Log.ERROR,"GMDAO","updateWcOutstandingDetails","Exception "+exception);
						 connection.rollback();
						 throw new DatabaseException(exception);
					 }
					updateOutstandingDetailsStmt.close();
					updateOutstandingDetailsStmt = null;
					connection.commit() ;
				 }

			}//end of condt outstanding detail!=null
		 }catch (Exception sqlexception) {
			 Log.logException(sqlexception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			 throw new DatabaseException(sqlexception.getMessage());
		 }finally {
			 DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","updateWcOutstandingDetails","Exited");
		 return updateOutstandingStatus;
	 }

   /**
    * This method returns an arraylist of periodic info object. The parameter passed
    * to this object are id and type.
    *
    * If type = 0, the id passed will be borrower id. All the disbursement details
    * for applications belonging to this borrower will be fetched in this case.
    *
    * If type = 1, the id passed will be CGPAN. The corresponding disbursement
    * details for application for this CGPAN will be fetched.
    * @param id
    * @param type
    * @return Arraylist
    * @roseuid 397BDFA403E6
    */
   public ArrayList getDisbursementDetails(String id, int type)
   										 			throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","getDisbursementDetails","Entered");
//   		ArrayList listOfDisbursement = new ArrayList();
//		ArrayList listOfDisbursementAmount = new ArrayList();


		ArrayList periodicInfos = new ArrayList();

		Connection connection = DBConnection.getConnection() ;

		CallableStatement getDisbursementDetailStmt = null ;
		//Disbursement disbursement = null;
		ResultSet resultSet = null;


		try {
			String exception = "" ;

		 	String functionName=null;

		 	if(type==GMConstants.TYPE_ZERO)
		 	{
				functionName="{?=call packGetDtlsforDBR.funcGetDtlsForBid(?,?,?)}"	;
		 	}
		 	else if (type==GMConstants.TYPE_ONE)
		 	{
				functionName="{?=call packGetDtlsforDBR.funcGetDtlsForCGPAN(?,?,?)}";
		 	}else if (type==GMConstants.TYPE_TWO)
			{
				functionName="{?=call packGetDtlsforDBR.funcGetDtlsForBorrower(?,?,?)}";
			}


			getDisbursementDetailStmt = connection.prepareCall(functionName);
			getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getDisbursementDetailStmt.setString(2,id);
			getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
			getDisbursementDetailStmt.registerOutParameter(4,
												java.sql.Types.VARCHAR) ;

			getDisbursementDetailStmt.executeQuery() ;

			exception = getDisbursementDetailStmt.getString(4) ;

			int error=getDisbursementDetailStmt.getInt(1);

			if(error==Constants.FUNCTION_FAILURE)
			{
				getDisbursementDetailStmt.close();
				getDisbursementDetailStmt=null;
				Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Exception :"+exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}
			resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;

			PeriodicInfo periodicInfo=new PeriodicInfo();
			Disbursement disbursement = null;
			ArrayList listOfDisbursement = new ArrayList();
			boolean firstTime=true;

			while (resultSet.next())
			{
				disbursement = new Disbursement();

				if(firstTime)
				{
					periodicInfo.setBorrowerId(resultSet.getString(1));
					periodicInfo.setBorrowerName(resultSet.getString(4));
					firstTime=false;
				}

				disbursement.setCgpan(resultSet.getString(2));
			//	System.out.println("getDisbursementDetails:CGPAN : "+resultSet.getString(2));
				disbursement.setScheme(resultSet.getString(3));
			//	System.out.println("getDisbursementDetails:Scheme : "+resultSet.getString(4));
				disbursement.setSanctionedAmount(resultSet.getDouble(5));
				//System.out.println("getDisbursementDetails:San Amt: "+resultSet.getString(5));

				listOfDisbursement.add(disbursement);
 			}

			resultSet.close();
			resultSet=null;

			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt = null;


			String cgpan = null;

			int disbSize = listOfDisbursement.size();
			functionName="{?=call packGetPIDBRDtlsCGPAN.funcDBRDetailsForCGPAN(?,?,?)}"	;
			getDisbursementDetailStmt = connection.prepareCall(functionName);
			for(int i=0; i<disbSize; ++i) {
				ArrayList listOfDisbursementAmount = new ArrayList();
				disbursement = (Disbursement)listOfDisbursement.get(i);
				cgpan=disbursement.getCgpan();
				if(cgpan == null)
				{
					continue;
				}
				Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Cgpan"+cgpan);
				getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getDisbursementDetailStmt.setString(2,cgpan);

				getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
				getDisbursementDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

				getDisbursementDetailStmt.execute() ;

				exception = getDisbursementDetailStmt.getString(4) ;

				error=getDisbursementDetailStmt.getInt(1);
				if(error==Constants.FUNCTION_FAILURE)
				{
					getDisbursementDetailStmt.close();
					getDisbursementDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getDisbursementDetails","Exception" + exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;
				DisbursementAmount disbursementAmount = null;

				while (resultSet.next())
				{
					disbursementAmount = new DisbursementAmount();

					disbursementAmount.setCgpan(cgpan);

					disbursementAmount.setDisbursementId(resultSet.getString(1));
					Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb Id" +
						disbursementAmount.getDisbursementId());

					disbursementAmount.setDisbursementAmount(resultSet.getDouble(2));
					Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb Amt" +
					disbursementAmount.getDisbursementAmount());

					disbursementAmount.setDisbursementDate(DateHelper.sqlToUtilDate(resultSet.getDate(3)));
					Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb date" +
						disbursementAmount.getDisbursementDate());
					disbursementAmount.setFinalDisbursement(resultSet.getString(4));
					Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Fin disb " +
						disbursementAmount.getFinalDisbursement());

					listOfDisbursementAmount.add(disbursementAmount);
				}

				disbursement.setDisbursementAmounts(listOfDisbursementAmount);
				resultSet.close();
				resultSet=null;

			}

			getDisbursementDetailStmt.close();
			getDisbursementDetailStmt=null;

			periodicInfo.setDisbursementDetails(listOfDisbursement);
			periodicInfos.add(periodicInfo);
			
			connection.commit() ;
			
		}catch (Exception exception) {
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(exception.getMessage()) ;
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","get disbursementDetails","Exited");
		return  periodicInfos;
	  }




   /**
    * This method returns an arraylist of periodic infos object. The parameter passed to
    * this object are id and type.
    *
    * If type = 0, the id passed will be borrower id. All the repayment details for
    * applications belonging to this borrower will be fetched in this case.
    *
    * If type = 1, the id passed will be CGPAN. The corresponding repayment details
    * for application for this CGPAN will be fetched.
    * @param id
    * @param type
    * @return ArrayList
    * @roseuid 397BFD3C0281
    */
   public ArrayList getRepaymentDetails(String id, int type)
   													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","get Repayment Details","Entered");

		ArrayList periodicInfos = new ArrayList();

		Connection connection = DBConnection.getConnection() ;
		CallableStatement getRepaymentDetailStmt = null ;
		ResultSet resultSet = null;

		try {

			String exception = "" ;
			String functionName=null;

			if(type==GMConstants.TYPE_ZERO)
			{
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBID(?,?,?)}"	;
			}
			else if(type==GMConstants.TYPE_ONE)
			{
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforCGPAN(?,?,?)}";
			}else if(type==GMConstants.TYPE_TWO){
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBorrower(?,?,?)}";
			}

			getRepaymentDetailStmt = connection.prepareCall(functionName);
			getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getRepaymentDetailStmt.setString(2,id);
			getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
			getRepaymentDetailStmt.registerOutParameter(4,
												java.sql.Types.VARCHAR) ;

			getRepaymentDetailStmt.executeQuery() ;

			exception = getRepaymentDetailStmt.getString(4) ;
			Log.log(Log.DEBUG,"GMDAO","repayment detail","exception "+exception);
			int error=getRepaymentDetailStmt.getInt(1);
			Log.log(Log.DEBUG,"GMDAO","repayment detail","errorCode "+error);	
			if(error==Constants.FUNCTION_FAILURE)
			{
				getRepaymentDetailStmt.close();
				getRepaymentDetailStmt=null;
				connection.rollback();
				Log.log(Log.ERROR,"GMDAO","getRepaymentdetail","error in SP "+exception);
				throw new DatabaseException(exception);				
			}
			Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","Before ResultSet assign");
			resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
			Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","resultSet assigned");
			
			PeriodicInfo periodicInfo=new PeriodicInfo();
			Repayment repayment = null;

			ArrayList listOfRepayment = new ArrayList();

			boolean firstTime=true;
			String tcCgpan = null;
			int len = 0;
			String applType = null;
			
			while (resultSet.next())
			{
				Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","Inside ResultSet");
				repayment = new Repayment();
				tcCgpan = resultSet.getString(2);
				len = tcCgpan.length() ;
				applType = tcCgpan.substring(len-2,len-1);
				if(applType.equalsIgnoreCase(GMConstants.CGPAN_TC))
				{
					if(firstTime)
					{
						periodicInfo.setBorrowerId(resultSet.getString(1));
						Log.log(Log.DEBUG,"getRepaymentDetails for Borrower","Borrower ID"," : "+periodicInfo.getBorrowerId());

						periodicInfo.setBorrowerName(resultSet.getString(3));
						Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","Borrower Name"," : "+periodicInfo.getBorrowerName());
						firstTime=false;
					}

					repayment.setCgpan(tcCgpan);
					Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","CGPAN ",": "+repayment.getCgpan());
					repayment.setScheme(resultSet.getString(4));
					Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","Scheme"," : "+repayment.getScheme());

					listOfRepayment.add(repayment);
				}
			}

			Log.log(Log.DEBUG,"getRepaymentDetails for Borrower:","size of RepaymentObj"," : "+listOfRepayment.size());

			resultSet.close();
			resultSet=null;

			getRepaymentDetailStmt = null;

			functionName="{?=call packGetRepaymentDtls.funcGetRepaymentDtl(?,?,?)}"	;
			getRepaymentDetailStmt = connection.prepareCall(functionName);
			//System.out.println("size of listOfRepayment= "+listOfRepayment.size());

			String cgpan="";
			int size = listOfRepayment.size();
			for(int i=0;i<size;++i) {
				ArrayList listOfRepaymentAmount = new ArrayList();
				repayment = (Repayment)listOfRepayment.get(i);
				cgpan=repayment.getCgpan();
				Log.log(Log.DEBUG,"getRepaymentDetails for cgpan:","cgpan"," : "+i+" "+cgpan);
				getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getRepaymentDetailStmt.setString(2,cgpan);

				getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
				getRepaymentDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

				getRepaymentDetailStmt.execute() ;

				exception = getRepaymentDetailStmt.getString(4) ;

				error=getRepaymentDetailStmt.getInt(1);
				if(error==Constants.FUNCTION_FAILURE)
				{
					getRepaymentDetailStmt.close();
					getRepaymentDetailStmt=null;
					Log.log(Log.ERROR,"getRepaymentDetails for cgpan:","Exception ",exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}
				resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
				RepaymentAmount repaymentAmount = null;
				while (resultSet.next())
				{
					repaymentAmount = new RepaymentAmount();

					repaymentAmount.setCgpan(cgpan);
					Log.log(Log.DEBUG,"GMDAO","RepaymentAmount","Cgpan "+cgpan);

					repaymentAmount.setRepayId(resultSet.getString(1));
					Log.log(Log.DEBUG,"GMDAO","RepaymentAmount","RepaymentId "+repaymentAmount.getRepayId());

					repaymentAmount.setRepaymentAmount(resultSet.getDouble(2));
					Log.log(Log.DEBUG,"rep Amt: ","rpAmount","--"+repaymentAmount.getRepaymentAmount());

					repaymentAmount.setRepaymentDate(resultSet.getDate(3));
					Log.log(Log.DEBUG,"rep date:","date"," "+repaymentAmount.getRepaymentDate());

					listOfRepaymentAmount.add(repaymentAmount);
					Log.log(Log.DEBUG,"************","***********","****************");
				}
				repayment.setRepaymentAmounts(listOfRepaymentAmount);
				resultSet.close();
				resultSet=null;
			}
			periodicInfo.setRepaymentDetails(listOfRepayment);

			periodicInfos.add(periodicInfo);

			getRepaymentDetailStmt.close();
			getRepaymentDetailStmt=null;
			
			connection.commit();

		}catch (Exception exception) {
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(exception.getMessage()) ;
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","get Repayment Details","Exited");
		return  periodicInfos;
	}



   /**
    * This method returns an arraylist of RepaymentSchedule object. The parameter
    * passed to this object are id and type.
    *
    * If type = 0, the id passed will be borrower id. All the application details
    * belonging to this borrower will be fetched in this case.
    *
    * If type = 1, the id passed will be CGPAN. The corresponding application details
    * for this CGPAN will be fetched.
    * @param Id
    * @param Type
    * @return ArrayList
    * @roseuid 397BFFEF02C0
    */
 	public ArrayList getRepaymentSchedule(String id, int type)
   													throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","get Repayment Scedule","Entered");
		ArrayList listOfRepaymentSchedule = new ArrayList();

		RepaymentSchedule repaymentSchedule = null;

		Connection connection = DBConnection.getConnection() ;

		CallableStatement getRepaymentScheduleStmt = null ;
		ResultSet resultSet = null;
		String cgpan1 = null;

		ArrayList repaySchedules = new ArrayList();

		try
		{
			String exception = "" ;
			String functionName=null;

			if(type==GMConstants.TYPE_ZERO)
			{
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBID(?,?,?)}"	;
			}
			else if(type == GMConstants.TYPE_ONE)
			{
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforCGPAN(?,?,?)}";
			}else if(type == GMConstants.TYPE_TWO){
				functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBorrower(?,?,?)}";
			}

			getRepaymentScheduleStmt = connection.prepareCall(functionName);
			getRepaymentScheduleStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

			getRepaymentScheduleStmt.setString(2,id);
			getRepaymentScheduleStmt.registerOutParameter(3,Constants.CURSOR);
			getRepaymentScheduleStmt.registerOutParameter(4,
												java.sql.Types.VARCHAR) ;

			getRepaymentScheduleStmt.executeQuery() ;

			exception = getRepaymentScheduleStmt.getString(4) ;
	//		System.out.println("Exception : " + exception);
			int error=getRepaymentScheduleStmt.getInt(1);

			if(error==Constants.FUNCTION_FAILURE)
			{
				getRepaymentScheduleStmt.close();
				getRepaymentScheduleStmt=null;
				Log.log(Log.ERROR,"GMDAO","getRepaymentSchedule","Exception "+exception);
				connection.rollback() ;
				throw new DatabaseException(exception);
			}
			resultSet = (ResultSet)getRepaymentScheduleStmt.getObject(3) ;

			while (resultSet.next())
			{
				cgpan1 = resultSet.getString(2);
				if(cgpan1 != null)
				{
					repaymentSchedule = new RepaymentSchedule();
					repaymentSchedule.setBorrowerId(resultSet.getString(1)) ;
					repaymentSchedule.setBorrowerName(resultSet.getString(3)) ;
					repaymentSchedule.setCgpan(cgpan1) ;
					repaymentSchedule.setScheme(resultSet.getString(4)) ;
					listOfRepaymentSchedule.add(repaymentSchedule);
				}
			}

			Log.log(Log.DEBUG,"GMDAO","getRepaymentSchedule","size of the ;list"+listOfRepaymentSchedule.size());

			resultSet.close();
			resultSet=null;

			getRepaymentScheduleStmt.close();
			getRepaymentScheduleStmt = null;


			int size = listOfRepaymentSchedule.size();
			RepaymentSchedule repaySchedule = null;
			RepaymentSchedule rpSchedule = null;
			String cgpan = null;
			boolean firstTime = true;
			for(int i = 0; i < size; ++i )
			{
				repaySchedule = (RepaymentSchedule)listOfRepaymentSchedule.get(i);

				cgpan = repaySchedule.getCgpan() ;

				Log.log(Log.DEBUG,"GMDAO","getRepaymentSchedule","Cgpan == "+ cgpan);
				if(cgpan!=null)
				{

					getRepaymentScheduleStmt = connection.prepareCall(
								"{?=call funcGetRepayScheduleForCGPAN(?,?,?,?,?,?)}");

					getRepaymentScheduleStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
					getRepaymentScheduleStmt.registerOutParameter(7,
													java.sql.Types.VARCHAR) ;

					getRepaymentScheduleStmt.setString(2, cgpan);
					getRepaymentScheduleStmt.registerOutParameter(3, Types.INTEGER);
					getRepaymentScheduleStmt.registerOutParameter(4, Types.DATE);
					getRepaymentScheduleStmt.registerOutParameter(5, Types.INTEGER);
					getRepaymentScheduleStmt.registerOutParameter(6, Types.INTEGER);

					getRepaymentScheduleStmt.execute();
					int errorCode=getRepaymentScheduleStmt.getInt(1);

					String repayError=getRepaymentScheduleStmt.getString(7);

					Log.log(Log.DEBUG,"GMDAO","getRepaymentSchedule","errorCode, error "+errorCode+","+repayError);

					if(errorCode==Constants.FUNCTION_SUCCESS)
					{
						Log.log(Log.DEBUG,"GMDAO","getRepaymentSchedule","SP is SUCCESS for cgpan");
					}
					if(errorCode==Constants.FUNCTION_FAILURE)
					{
						getRepaymentScheduleStmt.close();
						getRepaymentScheduleStmt=null;
						Log.log(Log.ERROR,"GMDAO","getRepaymentSchedule","Exception "+repayError);
						connection.rollback() ;
						//throw new DatabaseException(repayError);
					}else
					{
						rpSchedule = new RepaymentSchedule();

						if(firstTime)
						{
							rpSchedule.setBorrowerId(repaySchedule.getBorrowerId());
							rpSchedule.setBorrowerName(repaySchedule.getBorrowerName());
							firstTime  =false;
						}
						rpSchedule.setCgpan(repaySchedule.getCgpan());
						rpSchedule.setScheme(repaySchedule.getScheme());
						rpSchedule.setMoratorium(getRepaymentScheduleStmt.getInt(3));
						rpSchedule.setFirstInstallmentDueDate(
									getRepaymentScheduleStmt.getDate(4));
						rpSchedule.setPeriodicity((getRepaymentScheduleStmt.getString(5)) );
						rpSchedule.setNoOfInstallment(getRepaymentScheduleStmt.getInt(6));
						repaySchedules.add(rpSchedule);
					}

				}
			}
			
			connection.commit() ;
		} catch (Exception e) {
			Log.log(Log.ERROR,"GMDAO","getRepaymentSchedule",e.getMessage());
			Log.logException(e);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException("Unable to get RepaySchedule Details");
		} finally {
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","getRepaymentSchedule","Exited");

		return repaySchedules;
   }



   /**
    * This method updates the RepaymentSchedule details in the database. It takes an
    * object of type RepaymentSchedule as an argument. This object is passed from the
    * GMProcessor class.
    * @param repaySchedule
    *  @param user id
    * @return Boolean
    * @roseuid 397C00CF0150
    */
   public boolean updateRepaymentSchedule(RepaymentSchedule repaymentSchedule,
    									String userId)throws DatabaseException
   {

		Log.log(Log.INFO,"GMDAO","update Repayment Scedule","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement updateRepaymentScheduleStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateRepaymentScheduleStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;

			if(repaymentSchedule!=null) {
				try	{

					/*Creates a CallableStatement object for calling
					 * database stored procedures*/

					updateRepaymentScheduleStmt= connection.prepareCall(
					"{?=call funcUpdateRepaymentSchedule (?,?,?,?,?,?,?)}");

					updateRepaymentScheduleStmt.registerOutParameter(1,
														java.sql.Types.INTEGER);
					updateRepaymentScheduleStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

					updateRepaymentScheduleStmt.setString(2,
												repaymentSchedule.getCgpan());

					updateRepaymentScheduleStmt.setDouble(3,
											repaymentSchedule.getMoratorium());


					utilDate=repaymentSchedule.getFirstInstallmentDueDate();
					sqlDate = new java.sql.Date(utilDate.getTime());
/*					utilDate=repaymentSchedule.getFirstInstallmentDueDate();
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					String formatedDate=dateFormat.format(utilDate);
					sqlDate=java.sql.Date.valueOf(DateHelper.stringToSQLdate(formatedDate));// utilDate.toString());
*/
					updateRepaymentScheduleStmt.setDate(4,sqlDate);
					updateRepaymentScheduleStmt.setString(5,
											repaymentSchedule.getPeriodicity());

					updateRepaymentScheduleStmt.setDouble(6,
										repaymentSchedule.getNoOfInstallment());

					updateRepaymentScheduleStmt.setString(7,userId);

					updateRepaymentScheduleStmt.executeQuery();
					updateStatus=Integer.parseInt(
							updateRepaymentScheduleStmt.getObject(1).toString());
					String exception = updateRepaymentScheduleStmt.getString(8);

					if (updateStatus==Constants.FUNCTION_SUCCESS )
					{
						Log.log(Log.DEBUG,"updateRepaymentSchedule","SP ","SUCCESS");
						updateRepaymentScheduleStatus = true;
					}
					else if (updateStatus==Constants.FUNCTION_FAILURE)
					{
						updateRepaymentScheduleStmt.close();
						updateRepaymentScheduleStmt = null;
						Log.log(Log.ERROR,"updateRepaymentSchedule","Exception ",exception);
						updateRepaymentScheduleStatus = false;
						connection.rollback() ;
						throw new DatabaseException(exception);
					}
					updateRepaymentScheduleStmt.close();
					updateRepaymentScheduleStmt = null;
					connection.commit();

				}catch (Exception exception)
				{
					Log.logException(exception);
					try
					{
						connection.rollback();
					}
					catch (SQLException ignore){}

					throw new DatabaseException(exception.getMessage());
				}finally
				{
					DBConnection.freeConnection(connection);
				}
			}
			Log.log(Log.INFO,"GMDAO","update Repayment Scedule","Exited");
			return updateRepaymentScheduleStatus;
  	 }

   /*
    * this method gets the closure details for the id;
    * if type == 1 then Bid
    * if type == 2 then cgpan
    * if type == 0 then member
    */

   public HashMap getClosureDetails(String id,int type,String memberId) throws DatabaseException {


	Log.log(Log.INFO,"GMDAO","get Closure Details","Entered");
	Connection connection = DBConnection.getConnection();
	Log.log(Log.DEBUG,"GMDAO","get Closure Details","ID : "+id);

   		HashMap closureDetails =  new HashMap();
   		HashMap borrowerInfos = new HashMap();

   		ClosureDetail closureDetail = null;
   		BorrowerInfo borrowerInfo = null;
   		CgpanInfo cgpanInfo = null;
   		//String memberId = "";
   		String borrowerId = "";
   		String borrowerName = "";


   		CallableStatement getClosureDetailStmt = null;
   		ResultSet closureResultSet = null;
   		ResultSet tempClosureResultSet = null;
   		
		try {
			String exception = "" ;

			String functionName=null;

			if(type==GMConstants.TYPE_ZERO)
			{
				functionName="{?=call packGetDtlsforClosure.funcGetClosureforBID(?,?,?,?,?,?)}";
				getClosureDetailStmt = connection.prepareCall(functionName);
				getClosureDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				Log.log(Log.DEBUG,"GMDAO","get Closure Details","in closure dao -- the Id "+id);
				getClosureDetailStmt.setString(2, id);
				getClosureDetailStmt.setString(3, memberId.substring(0,4));
				getClosureDetailStmt.setString(4, memberId.substring(4,8));
				getClosureDetailStmt.setString(5, memberId.substring(8,12));
				getClosureDetailStmt.registerOutParameter(6,Constants.CURSOR) ;
				getClosureDetailStmt.registerOutParameter(7,
													java.sql.Types.VARCHAR) ;

				getClosureDetailStmt.execute() ;

				exception = getClosureDetailStmt.getString(7) ;
				int error=getClosureDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS){
					Log.log(Log.DEBUG,"GMDAO","get Closure Details For Bid","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getClosureDetailStmt.close();
					getClosureDetailStmt=null;
					Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForBid","Exception "+exception);
					connection.rollback() ;
					throw new DatabaseException(exception);
				}

				closureResultSet = (ResultSet)getClosureDetailStmt.getObject(6) ;

			}else if (type == GMConstants.TYPE_ONE )
			{
				functionName="{?=call packGetDtlsforClosure.funcGetClosureforCGPAN(?,?,?)}";
				getClosureDetailStmt = connection.prepareCall(functionName);
				getClosureDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				Log.log(Log.DEBUG,"GMDAO","get Closure Details","in closure dao -- the Id "+id);
				getClosureDetailStmt.setString(2, id);
				getClosureDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				getClosureDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getClosureDetailStmt.execute() ;

				exception = getClosureDetailStmt.getString(4) ;
				int error=getClosureDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","get Closure Details For Cgpan","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getClosureDetailStmt.close();
					getClosureDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getClosureDetailsForCgpan","Exception "+exception);
					connection.rollback();
					throw new DatabaseException(exception);
				}

				closureResultSet = (ResultSet)getClosureDetailStmt.getObject(3) ;

			}else if (type == GMConstants.TYPE_TWO )
			{
				Log.log(Log.DEBUG,"GMDAO","get Closure Details","in closure dao -- the Id "+id+"type "+type);
				functionName="{?=call packGetDtlsforClosure.funcGetClosureforBor(?,?,?,?,?,?)}";
				getClosureDetailStmt = connection.prepareCall(functionName);

				getClosureDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

				getClosureDetailStmt.setString(2, id);
				getClosureDetailStmt.setString(3, memberId.substring(0,4));
				getClosureDetailStmt.setString(4, memberId.substring(4,8));
				getClosureDetailStmt.setString(5, memberId.substring(8,12));
				getClosureDetailStmt.registerOutParameter(6,Constants.CURSOR) ;
				getClosureDetailStmt.registerOutParameter(7,
													java.sql.Types.VARCHAR) ;

				getClosureDetailStmt.execute() ;

				exception = getClosureDetailStmt.getString(7) ;

				int error=getClosureDetailStmt.getInt(1);

				if(error == Constants.FUNCTION_SUCCESS)
				{
					Log.log(Log.DEBUG,"GMDAO","get Closure Details For Borrower","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getClosureDetailStmt.close();
					getClosureDetailStmt=null;
					Log.log(Log.ERROR,"GMDAO","getClosureDetailsForBorrower","Exception "+exception);
					connection.rollback() ;
					throw new DatabaseException(exception);
				}

				closureResultSet = (ResultSet)getClosureDetailStmt.getObject(6) ;
				Log.log(Log.DEBUG,"GMDAO","get Closure Details For Borrower","resultset--rows"+closureResultSet.getFetchSize());

			}
			int i = 0;

			ArrayList cgpanInfos = new ArrayList();
			closureDetail = new ClosureDetail();
			while (closureResultSet.next())
			{
				borrowerInfo = new BorrowerInfo();
				cgpanInfo = new CgpanInfo();
//				cgpanInfos = new ArrayList();

				closureDetail.setMemberId(closureResultSet.getString(1));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure:member ID : "+closureDetail.getMemberId());
				memberId = closureDetail.getMemberId() ;

				borrowerInfo.setBorrowerId(closureResultSet.getString(2));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure:borr ID : "+borrowerInfo.getBorrowerId());
				borrowerId = borrowerInfo.getBorrowerId();

				borrowerInfo.setBorrowerName(closureResultSet.getString(4));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure :BName: "+borrowerInfo.getBorrowerName());
				borrowerName = borrowerInfo.getBorrowerName() ;

				cgpanInfo.setCgpan(closureResultSet.getString(3));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure :cgpan: "+cgpanInfo.getCgpan());

				cgpanInfo.setScheme(closureResultSet.getString(5));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure :Scheme : "+cgpanInfo.getScheme());

				cgpanInfo.setSanctionedAmount(closureResultSet.getDouble(6));
				Log.log(Log.DEBUG,"GMDAO","get Closure Details"," closure :Amt : "+cgpanInfo.getSanctionedAmount());

				//cgpanInfos.add(cgpanInfo);
				//borrowerInfo.setCgpanInfos(cgpanInfos);

/*				if(closureDetails.containsValue(memberId)) 
				{
					borrowerInfos = new HashMap();
					if (borrowerInfos.containsValue(borrowerId))
					{
						borrowerInfos.get(borrowerId);
						cgpanInfos = borrowerInfo.getCgpanInfos();
					}
					else
					{
						borrowerInfos.put(borrowerId, borrowerInfo);
		                borrowerInfos.put(borrowerName, borrowerInfo);
					}
				}else
				 {
					borrowerInfo.setCgpanInfos(cgpanInfos);
					borrowerInfos.put(borrowerId, borrowerInfo);
					closureDetails.put(memberId,borrowerInfos);
				}
*/
				if(closureDetails.containsKey(memberId))
				{
					borrowerInfos = (HashMap)closureDetails.get(memberId);
					if(borrowerInfos.containsKey(borrowerId))
					{
						borrowerInfo = (BorrowerInfo)borrowerInfos.get(borrowerId);
						cgpanInfos = borrowerInfo.getCgpanInfos();
						cgpanInfos.add(cgpanInfo);  
					
					}
					else{
						
						cgpanInfos = new ArrayList();
						cgpanInfos.add(cgpanInfo);						
					}					
					borrowerInfo.setCgpanInfos(cgpanInfos);
					borrowerInfos.put(borrowerId,borrowerInfo);						
				}
				else{
					
					borrowerInfos = new HashMap();
					cgpanInfos = new ArrayList();
					cgpanInfos.add(cgpanInfo);
				
					borrowerInfo.setCgpanInfos(cgpanInfos);
					borrowerInfos.put(borrowerId,borrowerInfo);						
					
				}
				closureDetails.put(memberId,borrowerInfos);
				++i;
		    }// end of while
			closureResultSet.close();
			closureResultSet = null;

			getClosureDetailStmt.close();
			getClosureDetailStmt = null;

			connection.commit() ;
			
		}catch (Exception exception) {
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){}

			throw new DatabaseException(exception.getMessage()) ;
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}
		Log.log(Log.INFO,"GMDAO","get Closure Details","Exited");
   		return closureDetails;
   }



   public HashMap getClosureDetailsForFeeNotPaid(String cgpan) throws DatabaseException {

		Log.log(Log.INFO,"GMDAO","getClosureDetailsForFeeNotPaid","Entered");
		
		Connection connection = DBConnection.getConnection();
		Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","cgpan : "+cgpan);

		HashMap closureDetails =  new HashMap();
		HashMap borrowerInfos = new HashMap();

		ClosureDetail closureDetail = new ClosureDetail();
		
		BorrowerInfo borrowerInfo = new BorrowerInfo();
				
		String memberId = null;
		String borrowerId = null;
		String borrowerName = null;
		double sancAmt = 0;
		String scheme = null;
				
		CallableStatement getClosureDetailStmt = null;
		ResultSet closureResultSet = null;
				
		try 
		{
			String exception = "" ;
	
			String functionName=null;
			
			functionName="{?=call packGetDtlsforClosure.funcGetClosureforCGPAN(?,?,?)}";
			getClosureDetailStmt = connection.prepareCall(functionName);
			getClosureDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
	
			getClosureDetailStmt.setString(2, cgpan);
			getClosureDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
			getClosureDetailStmt.registerOutParameter(4,
												java.sql.Types.VARCHAR) ;
	
			getClosureDetailStmt.execute() ;
	
			exception = getClosureDetailStmt.getString(4) ;
			int error=getClosureDetailStmt.getInt(1);
	
			if(error == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","Success");
			}
	
			if(error==Constants.FUNCTION_FAILURE)
			{
				getClosureDetailStmt.close();
				getClosureDetailStmt=null;
				Log.log(Log.ERROR,"GMDAO","getClosureDetailsForFeeNotPaid","Exception "+exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}
	
			closureResultSet = (ResultSet)getClosureDetailStmt.getObject(3) ;
			
			ArrayList cgpanInfos = new ArrayList();
			 
			while(closureResultSet.next())
			{
				CgpanInfo cgpanInfo = new CgpanInfo();
				
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","cgpan "+closureResultSet.getString(3));
				memberId = closureResultSet.getString(1);
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","memId"+memberId);
				borrowerId = closureResultSet.getString(2);
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","bId "+borrowerId);
				borrowerName = closureResultSet.getString(4);
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","bname "+borrowerName);
				scheme = closureResultSet.getString(5);
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","scheme "+scheme);
				sancAmt = closureResultSet.getDouble(6);
				Log.log(Log.DEBUG,"GMDAO","getClosureDetailsForFeeNotPaid","sancAmt "+sancAmt);
				
				closureDetail.setMemberId(memberId);
				
				borrowerInfo.setBorrowerId(borrowerId);
				borrowerInfo.setBorrowerName(borrowerName);
				
				cgpanInfo.setCgpan(cgpan);
				cgpanInfo.setScheme(scheme);
				cgpanInfo.setSanctionedAmount(sancAmt);
				
				cgpanInfos.add(cgpanInfo);
				
				borrowerInfo.setCgpanInfos(cgpanInfos);
				
				borrowerInfos.put(borrowerId,borrowerInfo);
				
				closureDetails.put(memberId,borrowerInfos);				
			}
			closureResultSet.close();
			closureResultSet=null;
			getClosureDetailStmt.close();
			getClosureDetailStmt=null;
			
	
		}catch (Exception exception) {
			Log.logException(exception);
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore)
			{
			}
			throw new DatabaseException(exception.getMessage()) ;
		}
		finally
		{
			DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","getClosureDetailsForFeeNotPaid","Exited");
		return closureDetails;
 
   }
   public ArrayList getAllReasonsForClosure() throws DatabaseException {

		   Log.log(Log.INFO,"GMDAO","getClosureReasons","Entered");

		   Connection connection = DBConnection.getConnection() ;

		   CallableStatement getClosureDetailStmt = null ;
		   ResultSet closureResultSet = null;
		   ArrayList closureReasons = null;

		   try {
		   String exception = "" ;

			getClosureDetailStmt = connection.prepareCall(
					"{?=call packGetClosureReasons.funcGetClosureReasons(?,?)}");
			getClosureDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getClosureDetailStmt.registerOutParameter(2,Constants.CURSOR) ;
			getClosureDetailStmt.registerOutParameter(3,java.sql.Types.VARCHAR) ;

			getClosureDetailStmt.execute() ;

			String reasonException = getClosureDetailStmt.getString(3) ;
			int reasonError=getClosureDetailStmt.getInt(1);

			if(reasonError == Constants.FUNCTION_SUCCESS){
				Log.log(Log.DEBUG,"GMDAO","get Closure Reasons","Success");
			}

			if(reasonError==Constants.FUNCTION_FAILURE)
			{
				getClosureDetailStmt.close();
				getClosureDetailStmt=null;
				Log.log(Log.ERROR,"GMDAO", "getClosureReasons","Exception "+reasonException);
				connection.rollback() ;
				throw new DatabaseException(reasonException);
			}

			closureResultSet = (ResultSet)getClosureDetailStmt.getObject(2) ;
			String reason = null;
			closureReasons = new ArrayList();
			while(closureResultSet.next()){
				reason = closureResultSet.getString(1);
				if(reason != null)
				{
					Log.log(Log.DEBUG,"GMDAO", "getClosureReasons","inside While");
					closureReasons.add(reason);
				}
			}

			closureResultSet.close();
			closureResultSet = null;

			getClosureDetailStmt.close();
			getClosureDetailStmt = null;

			connection.commit() ;
	   }catch (Exception exception) {
		   throw new DatabaseException(exception.getMessage()) ;
	   }finally{
	   		DBConnection.freeConnection(connection);
	   }
	   Log.log(Log.INFO,"GMDAO","getClosureReasons","Exited");
	   return closureReasons;
  }




   /**
    * This method returns NPADEtals object. The parameter passed to
    * this method is borrowerid
    *
    * @return com.cgtsi.guaranteemaintenance.NPADetails
    *
    */
    public NPADetails getNPADetails(String borrowerId)
      throws DatabaseException
    {
      Log.log(4, "GMDAO", "getNPADetails", "Entered");

      Connection connection = DBConnection.getConnection();
      CallableStatement getNPADetailStmt = null;
      ResultSet resultSet = null;

      NPADetails npaDetail = null;
      try
      {
        Log.log(5, "GMDAO", "get NPA Details For Bid", "test");

        getNPADetailStmt = connection.prepareCall("{?=call packGetNPADetail.funcGetNPADetail(?,?,?)}");

        getNPADetailStmt.registerOutParameter(1, 4);
        getNPADetailStmt.setString(2, borrowerId);

        getNPADetailStmt.registerOutParameter(3, -10);

        getNPADetailStmt.registerOutParameter(4, 12);

        getNPADetailStmt.execute();

        Log.log(5, "GMDAO", "get NPA Details For Bid", "test");

        resultSet = (ResultSet)getNPADetailStmt.getObject(3);
        int error = getNPADetailStmt.getInt(1);
        String exception = getNPADetailStmt.getString(4);

        if (error == 0) {
          Log.log(5, "GMDAO", "get NPA Details For Bid", "Success");
        }

        if (error == 1)
        {
          getNPADetailStmt.close();
          getNPADetailStmt = null;
          Log.log(2, "GMDAO", "get NPA Details For Bid", "Exception " + exception);
          connection.rollback();
          throw new DatabaseException(exception);
        }

        LegalSuitDetail legalSuitDetail = null;
        boolean isNpaAvailable = false;
        while (resultSet.next())
        {
          Log.log(5, "GMDAO", "getNPADetails", "inside result set isNpaAvailable = true");
          isNpaAvailable = true;
          npaDetail = new NPADetails();
          legalSuitDetail = new LegalSuitDetail();

          npaDetail.setNpaId(resultSet.getString(1));
          npaDetail.setCgbid(resultSet.getString(2));
          npaDetail.setNpaDate(resultSet.getDate(3));
          npaDetail.setWhetherNPAReported(resultSet.getString(4));
          npaDetail.setReportingDate(resultSet.getDate(6));
          npaDetail.setReference(resultSet.getString(7));
          npaDetail.setOsAmtOnNPA(resultSet.getDouble(8));
          npaDetail.setNpaReason(resultSet.getString(9));
          npaDetail.setEffortsTaken(resultSet.getString(10));
          Log.log(5, "GMDAO", "getNPADetails", "Efforts " + npaDetail.getEffortsTaken());

          npaDetail.setIsRecoveryInitiated(resultSet.getString(11));
          npaDetail.setNoOfActions(resultSet.getInt(12));
          npaDetail.setEffortsConclusionDate(resultSet.getDate(13));
          npaDetail.setMliCommentOnFinPosition(resultSet.getString(15));
          npaDetail.setDetailsOfFinAssistance(resultSet.getString(16));
          npaDetail.setCreditSupport(resultSet.getString(17));
          npaDetail.setBankFacilityDetail(resultSet.getString(18));
          npaDetail.setWillfulDefaulter(resultSet.getString(19));
          npaDetail.setPlaceUnderWatchList(resultSet.getString(20));
          npaDetail.setRemarksOnNpa(resultSet.getString(24));
          Log.log(5, "GMDAO", "getNPADetails", "Remarks" + npaDetail.getRemarksOnNpa());

          legalSuitDetail.setLegalSuiteNo(resultSet.getString(25));
          legalSuitDetail.setCourtName(resultSet.getString(26));
          legalSuitDetail.setForumName(resultSet.getString(27));
          legalSuitDetail.setLocation(resultSet.getString(28));
          legalSuitDetail.setDtOfFilingLegalSuit(resultSet.getDate(29));
          legalSuitDetail.setAmountClaimed(resultSet.getDouble(30));
          legalSuitDetail.setCurrentStatus(resultSet.getString(31));
          legalSuitDetail.setRecoveryProceedingsConcluded(resultSet.getString(32));

          npaDetail.setLegalSuitDetail(legalSuitDetail);
          
            //added on 08-11-2013   
               npaDetail.setIsAsPerRBI(resultSet.getString(33));
               npaDetail.setNpaConfirm(resultSet.getString(34));
               npaDetail.setEffortsTaken(resultSet.getString(35));
               npaDetail.setIsAcctReconstructed(resultSet.getString(36));
               npaDetail.setSubsidyFlag(resultSet.getString(37));
               npaDetail.setIsSubsidyRcvd(resultSet.getString(38));
               npaDetail.setIsSubsidyAdjusted(resultSet.getString(39));
               npaDetail.setSubsidyLastRcvdAmt(resultSet.getDouble(40));
               npaDetail.setSubsidyLastRcvdDt(resultSet.getDate(41));
               npaDetail.setLastInspectionDt(resultSet.getDate(42));
               npaDetail.setNpaCreatedDate(resultSet.getDate(43));
        }
        getNPADetailStmt.close();
        getNPADetailStmt = null;

        resultSet.close();
        resultSet = null;

        if ((npaDetail != null) && (npaDetail.getIsRecoveryInitiated().equals("Y")))
        {
          Log.log(5, "GMDAO", "getNPADetails", "isNpaAvailable = true getRecoveryAxn");

          getNPADetailStmt = connection.prepareCall("{?=call packGetRecoveryAxn.funcGetRecoveryAxn(?,?,?)}");

          getNPADetailStmt.registerOutParameter(1, 4);

          getNPADetailStmt.setString(2, npaDetail.getNpaId());

          getNPADetailStmt.registerOutParameter(3, -10);
          getNPADetailStmt.registerOutParameter(4, 12);

          getNPADetailStmt.execute();

          resultSet = (ResultSet)getNPADetailStmt.getObject(3);

          int recerror = getNPADetailStmt.getInt(1);
          String recexception = getNPADetailStmt.getString(4);

          if (recerror == 0)
          {
            Log.log(5, "GMDAO", "get RecoveryActionDetails For Bid", "Success");
          }

          if (recerror == 1)
          {
            getNPADetailStmt.close();
            getNPADetailStmt = null;
            Log.log(2, "GMDAO", "get RecoveryAction Details For Bid", "Exception " + recexception);
            connection.rollback();
            throw new DatabaseException(exception);
          }

          ArrayList recoProcs = new ArrayList();

          while (resultSet.next())
          {
            Log.log(5, "GMDAO", "get NPA Details..getRecoveryActionDetailsForBid", "Inside Result Set");
            RecoveryProcedure recoProc = new RecoveryProcedure();
            Log.log(5, "GMDAO", "Test", "new R");
            recoProc.setActionType(resultSet.getString(1));
            Log.log(5, "GMDAO", "Test", "1");
            recoProc.setRadId(resultSet.getString(2));
            Log.log(5, "GMDAO", "Test", "2");
            recoProc.setActionDetails(resultSet.getString(3));
            Log.log(5, "GMDAO", "Test", "3");
            recoProc.setActionDate(resultSet.getDate(4));
            Log.log(5, "GMDAO", "Test", "4");
            recoProc.setAttachmentName(resultSet.getString(5));
            Log.log(5, "GMDAO", "Test", "5");
            recoProcs.add(recoProc);
            Log.log(5, "GMDAO", "Test", "one while");
          }
          Log.log(5, "GMDAO", "getRecoveryActionDetailsForBid", "Size is " + recoProcs.size());
          resultSet.close();
          resultSet = null;

          getNPADetailStmt.close();
          getNPADetailStmt = null;
          connection.commit();
          npaDetail.setRecoveryProcedure(recoProcs);
        }
      }
      catch (Exception exception) {
        try {
          connection.rollback();
        }
        catch (SQLException localSQLException) {
        }
        throw new DatabaseException(exception.getMessage());
      } finally {
        DBConnection.freeConnection(connection);
      }
      Log.log(4, "GMDAO", "getNPADetails", "Exited");

      return npaDetail;
    }
   
    public NPADetails getNPADetailsOld(String borrowerId) throws DatabaseException{

                 Log.log(Log.INFO,"GMDAO","getNPADetails","Entered");

                 Connection connection = DBConnection.getConnection() ;
                 CallableStatement getNPADetailStmt = null ;
                 ResultSet resultSet = null;

                 NPADetails npaDetail = null;

                 try {
                         Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

                         getNPADetailStmt = connection.prepareCall(
                         "{?=call packGetNPADetail.funcGetNPADetail(?,?,?)}");
                         getNPADetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
                         getNPADetailStmt.setString(2, borrowerId) ;

                         getNPADetailStmt.registerOutParameter(3,
                                                                                                 Constants.CURSOR) ;
                         getNPADetailStmt.registerOutParameter(4,
                                                                                         java.sql.Types.VARCHAR) ;

                         getNPADetailStmt.execute() ;
                         
                         Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

                         resultSet = (ResultSet)getNPADetailStmt.getObject(3) ;
                         int error = getNPADetailStmt.getInt(1) ;
                         String exception =  getNPADetailStmt.getString(4) ;

                         if(error == Constants.FUNCTION_SUCCESS){
                                 Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","Success");
                         }

                         if(error==Constants.FUNCTION_FAILURE)
                         {
                                 getNPADetailStmt.close();
                                 getNPADetailStmt=null;
                                 Log.log(Log.ERROR,"GMDAO","get NPA Details For Bid","Exception "+exception);
                                 connection.rollback();
                                 throw new DatabaseException(exception);
                         }

                         LegalSuitDetail legalSuitDetail = null;
                         boolean isNpaAvailable = false;
                         while (resultSet.next())
                         {
                                 Log.log(Log.DEBUG,"GMDAO","getNPADetails","inside result set isNpaAvailable = true");
                                 isNpaAvailable = true;
                                 npaDetail = new NPADetails();
                                 legalSuitDetail = new LegalSuitDetail();

                                 npaDetail.setNpaId(resultSet.getString(1)) ;
                                 npaDetail.setCgbid(resultSet.getString(2)) ;
                                 npaDetail.setNpaDate(resultSet.getDate(3)) ;
                                 npaDetail.setWhetherNPAReported(resultSet.getString(4)) ;
                                 npaDetail.setReportingDate(resultSet.getDate(6)) ;
                                 npaDetail.setReference(resultSet.getString(7)) ;
                                 npaDetail.setOsAmtOnNPA(resultSet.getDouble(8)) ;
                                 npaDetail.setNpaReason(resultSet.getString(9));
                                 npaDetail.setEffortsTaken(resultSet.getString(10));
                                 Log.log(Log.DEBUG,"GMDAO","getNPADetails","Efforts "+npaDetail.getEffortsTaken());

                                 npaDetail.setIsRecoveryInitiated(resultSet.getString(11));
                                 npaDetail.setNoOfActions(resultSet.getInt(12));
                                 npaDetail.setEffortsConclusionDate(resultSet.getDate(13));
                                 npaDetail.setMliCommentOnFinPosition(resultSet.getString(15)) ;
                                 npaDetail.setDetailsOfFinAssistance(resultSet.getString(16)) ;
                                 npaDetail.setCreditSupport(resultSet.getString(17)) ;
                                 npaDetail.setBankFacilityDetail(resultSet.getString(18)) ;
                                 npaDetail.setWillfulDefaulter(resultSet.getString(19));
                                 npaDetail.setPlaceUnderWatchList(resultSet.getString(20));
                                 npaDetail.setRemarksOnNpa(resultSet.getString(24));
                                 Log.log(Log.DEBUG,"GMDAO","getNPADetails","Remarks"+npaDetail.getRemarksOnNpa());

                                 legalSuitDetail.setLegalSuiteNo(resultSet.getString(25));
                                 legalSuitDetail.setCourtName(resultSet.getString(26));
                                 legalSuitDetail.setForumName(resultSet.getString(27));
                                 legalSuitDetail.setLocation(resultSet.getString(28));
                                 legalSuitDetail.setDtOfFilingLegalSuit(resultSet.getDate(29));
                                 legalSuitDetail.setAmountClaimed(resultSet.getDouble(30));
                                 legalSuitDetail.setCurrentStatus(resultSet.getString(31));
                                 legalSuitDetail.setRecoveryProceedingsConcluded(resultSet.getString(32));

                                 npaDetail.setLegalSuitDetail(legalSuitDetail);
                         }
                         getNPADetailStmt.close();
                         getNPADetailStmt=null;

                         resultSet.close();
                         resultSet = null;

                         if(npaDetail!=null && npaDetail.getIsRecoveryInitiated().equals("Y"))
                         {
                                 Log.log(Log.DEBUG,"GMDAO","getNPADetails","isNpaAvailable = true getRecoveryAxn");

                                 getNPADetailStmt = connection.prepareCall(
                                                 "{?=call packGetRecoveryAxn.funcGetRecoveryAxn(?,?,?)}");

                                 getNPADetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

                                 getNPADetailStmt.setString(2, npaDetail.getNpaId()) ;

                                 getNPADetailStmt.registerOutParameter(3,Constants.CURSOR) ;
                                 getNPADetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

                                 getNPADetailStmt.execute() ;

                                 resultSet = (ResultSet)getNPADetailStmt.getObject(3) ;

                                 int recerror = getNPADetailStmt.getInt(1) ;
                                 String recexception =  getNPADetailStmt.getString(4) ;

                                 if(recerror == Constants.FUNCTION_SUCCESS)
                                 {
                                         Log.log(Log.DEBUG,"GMDAO","get RecoveryActionDetails For Bid","Success");
                                 }

                                 if(recerror==Constants.FUNCTION_FAILURE)
                                 {
                                         getNPADetailStmt.close();
                                         getNPADetailStmt=null;
                                         Log.log(Log.ERROR,"GMDAO","get RecoveryAction Details For Bid","Exception "+recexception);
                                         connection.rollback() ;
                                         throw new DatabaseException(exception);
                                 }

                                 ArrayList recoProcs = new ArrayList();

                                 while (resultSet.next())
                                 {
                                         Log.log(Log.DEBUG,"GMDAO","get NPA Details..getRecoveryActionDetailsForBid","Inside Result Set");
                                         RecoveryProcedure recoProc = new RecoveryProcedure();
                                         Log.log(Log.DEBUG,"GMDAO","Test","new R");
                                         recoProc.setActionType(resultSet.getString(1));
                                         Log.log(Log.DEBUG,"GMDAO","Test","1");
                                         recoProc.setRadId(resultSet.getString(2));
                                         Log.log(Log.DEBUG,"GMDAO","Test","2");
                                         recoProc.setActionDetails(resultSet.getString(3));
                                         Log.log(Log.DEBUG,"GMDAO","Test","3");
                                         recoProc.setActionDate(resultSet.getDate(4));
                                         Log.log(Log.DEBUG,"GMDAO","Test","4");
                                         recoProc.setAttachmentName(resultSet.getString(5));
                                         Log.log(Log.DEBUG,"GMDAO","Test","5");
                                         recoProcs.add(recoProc);
                                         Log.log(Log.DEBUG,"GMDAO","Test","one while");
                                 }
                                 Log.log(Log.DEBUG,"GMDAO","getRecoveryActionDetailsForBid","Size is "+recoProcs.size());
                                 resultSet.close();
                                 resultSet = null;

                                 getNPADetailStmt.close();
                                 getNPADetailStmt=null;
                                 connection.commit() ;
                                 npaDetail.setRecoveryProcedure(recoProcs);
                         }
                 }catch (Exception exception) {
                         try
                         {
                                 connection.rollback();
                         }
                         catch (SQLException ignore){}

                         throw new DatabaseException(exception.getMessage()) ;
                 }finally{
                         DBConnection.freeConnection(connection);
                 }
                 Log.log(Log.INFO,"GMDAO","getNPADetails","Exited");

                 return npaDetail;

    }
   
    public NPADetails getNPADetailsNew(String borrowerId) throws DatabaseException{

                 Log.log(Log.INFO,"GMDAO","getNPADetails","Entered");

                 Connection connection = DBConnection.getConnection() ;
                 CallableStatement getNPADetailStmt = null ;
                 ResultSet resultSet = null;

                 NPADetails npaDetail = null;

                 try {
                         Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

                         getNPADetailStmt = connection.prepareCall(
                         "{?=call packGetNPADetail.funcGetNPADetail(?,?,?)}");
                         getNPADetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
                         getNPADetailStmt.setString(2, borrowerId) ;

                         getNPADetailStmt.registerOutParameter(3,
                                                                                                 Constants.CURSOR) ;
                         getNPADetailStmt.registerOutParameter(4,
                                                                                         java.sql.Types.VARCHAR) ;

                         getNPADetailStmt.execute() ;
                         
                         Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

                         resultSet = (ResultSet)getNPADetailStmt.getObject(3) ;
                         int error = getNPADetailStmt.getInt(1) ;
                         String exception =  getNPADetailStmt.getString(4) ;

                         if(error == Constants.FUNCTION_SUCCESS){
                                 Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","Success");
                         }

                         if(error==Constants.FUNCTION_FAILURE)
                         {
                                 getNPADetailStmt.close();
                                 getNPADetailStmt=null;
                                 Log.log(Log.ERROR,"GMDAO","get NPA Details For Bid","Exception "+exception);
                                 connection.rollback();
                                 throw new DatabaseException(exception);
                         }

                         LegalSuitDetail legalSuitDetail = null;
                         boolean isNpaAvailable = false;
                         while (resultSet.next())
                         {
                                 Log.log(Log.DEBUG,"GMDAO","getNPADetails","inside result set isNpaAvailable = true");
                                 isNpaAvailable = true;
                                 npaDetail = new NPADetails();
                                 legalSuitDetail = new LegalSuitDetail();

                                 npaDetail.setNpaId(resultSet.getString(1)) ;//-------------
                                 npaDetail.setCgbid(resultSet.getString(2)) ;//-----------
                                 npaDetail.setNpaDate(resultSet.getDate(3)) ;//-----------------
                                 npaDetail.setWhetherNPAReported(resultSet.getString(4)) ;//---------------
                                 npaDetail.setReportingDate(resultSet.getDate(6)) ;//-------------------
                                 npaDetail.setReference(resultSet.getString(7)) ;
                                 npaDetail.setOsAmtOnNPA(resultSet.getDouble(8)) ;//-----------------------
                                 npaDetail.setNpaReason(resultSet.getString(9));//---------------------
                                 npaDetail.setEffortsTaken(resultSet.getString(10));//-------------------
                                 

                                 npaDetail.setIsRecoveryInitiated(resultSet.getString(11));//------------------
                                 npaDetail.setNoOfActions(resultSet.getInt(12));
                                 npaDetail.setEffortsConclusionDate(resultSet.getDate(13));
                                 npaDetail.setMliCommentOnFinPosition(resultSet.getString(15)) ;
                                 npaDetail.setDetailsOfFinAssistance(resultSet.getString(16)) ;
                                 npaDetail.setCreditSupport(resultSet.getString(17)) ;
                                 npaDetail.setBankFacilityDetail(resultSet.getString(18)) ;
                                 npaDetail.setWillfulDefaulter(resultSet.getString(19));
                                 npaDetail.setPlaceUnderWatchList(resultSet.getString(20));
                                 npaDetail.setRemarksOnNpa(resultSet.getString(24));
                                
                         }
                         getNPADetailStmt.close();
                         getNPADetailStmt=null;

                         resultSet.close();
                         resultSet = null;
                           

                        
                 }catch (Exception exception) {
                         try
                         {
                                 connection.rollback();
                         }
                         catch (SQLException ignore){}

                         throw new DatabaseException(exception.getMessage()) ;
                 }finally{
                         DBConnection.freeConnection(connection);
                 }
                 Log.log(Log.INFO,"GMDAO","getNPADetails","Exited");

                 return npaDetail;

    }
    
   

   /**
    * This method is to update the recovery actions taken for a borrower. An object
    * of type Recovery is passed as an argument to this method.
    * @param recovery
    * @return boolean
    * @roseuid 3F54ABFD01F4
    */
   public boolean updateRecoveryDetails(Recovery recovery)
   													throws DatabaseException {
		Log.log(Log.INFO,"GMDAO","update Recovery Details","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement updateRecoveryDetailsStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateRecoveryStatus = false;
		//value set to return

		java.util.Date utilDate;
		java.sql.Date sqlDate;

		if(recovery!=null) {
			try	{
				sqlDate = new java.sql.Date(0);
				utilDate = new java.util.Date();

				/*Creates a CallableStatement object for calling
				 * database stored procedures*/

				updateRecoveryDetailsStmt= connection.prepareCall(
						"{?=call funcInsertRecoveryDtl (?,?,?,?,?,?,?,?,?)}");
				updateRecoveryDetailsStmt.registerOutParameter(1,Types.INTEGER);
				updateRecoveryDetailsStmt.registerOutParameter(10,Types.VARCHAR);


				updateRecoveryDetailsStmt.setString(2,recovery.getCgbid());
				Log.log(Log.INFO,"GMDAO","update Recovery Details","recovery.getCgbid() :" + recovery.getCgbid());
				//System.out.println("CGBID from the screen :" + recovery.getCgbid());
				//System.out.println("recovery amount :" + recovery.getAmountRecovered());
				updateRecoveryDetailsStmt.setDouble(3,
												recovery.getAmountRecovered());
				Log.log(Log.INFO,"GMDAO","update Recovery Details","recovery.getAmountRecovered() :" + recovery.getAmountRecovered());
				//System.out.println("recovery date :" + recovery.getDateOfRecovery());
				utilDate = recovery.getDateOfRecovery();
//				System.out.println("utilDate :"+utilDate);
				if(utilDate!=null ){
//					updateRecoveryDetailsStmt.setDate(4,
//				            java.sql.Date.valueOf(DateHelper.stringToSQLdate(
//				                    utilDate.toString())));
					updateRecoveryDetailsStmt.setDate(4,
							new java.sql.Date(utilDate.getTime()));
				}else {
					updateRecoveryDetailsStmt.setDate(4,null);
				}
				//System.out.println("Legal Charges :" + recovery.getLegalCharges());
				updateRecoveryDetailsStmt.setDouble(5,
												recovery.getLegalCharges() );
				//System.out.println("recovery by ots :" + recovery.getIsRecoveryByOTS());
				updateRecoveryDetailsStmt.setString(6,
												recovery.getIsRecoveryByOTS());
				Log.log(Log.INFO,"GMDAO","update Recovery Details","recovery.getIsRecoveryByOTS() :" + recovery.getIsRecoveryByOTS());
				//System.out.println("sale of asset :" + recovery.getIsRecoveryBySaleOfAsset());
				updateRecoveryDetailsStmt.setString(7,
												recovery.getIsRecoveryBySaleOfAsset());
				//System.out.println("details of asset sold :" + recovery.getDetailsOfAssetSold());
				updateRecoveryDetailsStmt.setString(8,
												recovery.getDetailsOfAssetSold());

				updateRecoveryDetailsStmt.setString(9,
														recovery.getRemarks());

				updateRecoveryDetailsStmt.executeQuery();

				String exception = updateRecoveryDetailsStmt.getString(10);
			    updateStatus = updateRecoveryDetailsStmt.getInt(1);

				if(updateStatus == Constants.FUNCTION_SUCCESS ){
					Log.log(Log.DEBUG,"GMDAO","updateRecoveryDetails","SUCCESS SP ");
				}

			    if(updateStatus == Constants.FUNCTION_FAILURE )
			    {
					updateRecoveryDetailsStmt.close();
					updateRecoveryDetailsStmt = null;
			    	Log.log(Log.ERROR,"GMDAO","updateRecoveryDetails","Exception "+exception);
			    	connection.rollback() ;
			    	throw new DatabaseException(exception);
			    }
				updateRecoveryDetailsStmt.close();
				updateRecoveryDetailsStmt = null;
				connection.commit() ;
				
			}catch (Exception exception) {
			
				Log.logException(exception);
			
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			}finally{
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO,"GMDAO","update Recovery Details","Exited");
		return updateRecoveryStatus ;
   }



   public boolean modifyRecoveryDetails(Recovery recovery)
													throws DatabaseException {
		Log.log(Log.INFO,"GMDAO","modifyRecoveryDetails","Entered");
		Connection connection = DBConnection.getConnection();
		CallableStatement updateRecoveryDetailsStmt = null;
		int updateStatus=0;

		//indicates whether the stored procedure was executed successfully
		boolean updateRecoveryStatus = false;
		//value set to return

		//java.util.Date utilDate;
		//java.sql.Date sqlDate;

		if(recovery!=null) 
		{
			try	
			{
			//	sqlDate = new java.sql.Date(0);
			//	utilDate = new java.util.Date();

				/*Creates a CallableStatement object for calling
				 * database stored procedures*/

				updateRecoveryDetailsStmt= connection.prepareCall(
						"{?=call funcUpdRecDtl (?,?,?,?,?,?,?,?,?)}");
				updateRecoveryDetailsStmt.registerOutParameter(1,Types.INTEGER);
				updateRecoveryDetailsStmt.registerOutParameter(10,Types.VARCHAR);


				updateRecoveryDetailsStmt.setString(2,recovery.getRecoveryNo());
				//System.out.println("CGBID from the screen :" + recovery.getCgbid());
				//System.out.println("recovery amount :" + recovery.getAmountRecovered());
				updateRecoveryDetailsStmt.setDouble(3,
												recovery.getAmountRecovered());
				//System.out.println("recovery date :" + recovery.getDateOfRecovery());
				Date recDate = recovery.getDateOfRecovery()	;
				if(recDate!=null){
					updateRecoveryDetailsStmt.setDate(4,
							new java.sql.Date(recDate.getTime()));
				}else{
					updateRecoveryDetailsStmt.setDate(4,null);
				}
				//System.out.println("Legal Charges :" + recovery.getLegalCharges());
				updateRecoveryDetailsStmt.setDouble(5,
												recovery.getLegalCharges() );
				//System.out.println("recovery by ots :" + recovery.getIsRecoveryByOTS());
				updateRecoveryDetailsStmt.setString(6,
												recovery.getIsRecoveryByOTS());
				//System.out.println("sale of asset :" + recovery.getIsRecoveryBySaleOfAsset());
				updateRecoveryDetailsStmt.setString(7,
												recovery.getIsRecoveryBySaleOfAsset());
				//System.out.println("details of asset sold :" + recovery.getDetailsOfAssetSold());
				updateRecoveryDetailsStmt.setString(8,
												recovery.getDetailsOfAssetSold());

				updateRecoveryDetailsStmt.setString(9,
														recovery.getRemarks());

				updateRecoveryDetailsStmt.executeQuery();

				String exception = updateRecoveryDetailsStmt.getString(10);
				updateStatus = updateRecoveryDetailsStmt.getInt(1);

				if(updateStatus == Constants.FUNCTION_FAILURE )
				{
					updateRecoveryDetailsStmt.close();
					updateRecoveryDetailsStmt = null;
					Log.log(Log.ERROR,"GMDAO","modifyRecoveryDetails","Exception "+exception);
					connection.rollback() ;
					throw new DatabaseException(exception);
				}
				if(updateStatus == Constants.FUNCTION_SUCCESS ){
					Log.log(Log.DEBUG,"GMDAO","modifyRecoveryDetails","SUCCESS SP");
				}
				updateRecoveryDetailsStmt.close();
				updateRecoveryDetailsStmt = null;
				connection.rollback() ;

			}catch (Exception exception) {
				Log.logException(exception);
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage());
			}finally{
				DBConnection.freeConnection(connection);
			}
		}
		Log.log(Log.INFO,"GMDAO","modifyRecoveryDetails","Exited");
		return updateRecoveryStatus ;
   }

   /**
    * This method returns an arraylist of Recovery object. The parameter passed to
    * this object are Bid .
    * @param Id
    * @return com.cgtsi.guaranteemaintenance.Recovery
    * @roseuid 3F54D8D2009C
    */
    public ArrayList getRecoveryDetails(String borrowerId) throws
   												DatabaseException {

			Connection connection = DBConnection.getConnection() ;
			CallableStatement getRecoveryDetailStmt = null ;
			ResultSet resultSet = null;
			Recovery recoveryDetail = null;
			ArrayList recoveryDetails = null;
			try {
				String exception = "" ;

				getRecoveryDetailStmt = connection.prepareCall(
							"{?=call packGetRecoveryDtls.funcGetREcoveryDtls(?,?,?)}");
				getRecoveryDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getRecoveryDetailStmt.setString(2, borrowerId);
				getRecoveryDetailStmt.registerOutParameter(3,
									Constants.CURSOR) ;
				getRecoveryDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getRecoveryDetailStmt.execute() ;
				int error=getRecoveryDetailStmt.getInt(1);
				exception = getRecoveryDetailStmt.getString(4);

				if(error == Constants.FUNCTION_SUCCESS){
					Log.log(Log.DEBUG,"GMDAO","get Recovery Details For Bid","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getRecoveryDetailStmt.close();
					getRecoveryDetailStmt=null;
					Log.log(Log.DEBUG,"GMDAO","getRecoveryDetailsForBid","Exception "+exception);
					connection.rollback() ;
					throw new DatabaseException(exception);
				}

				resultSet = (ResultSet)getRecoveryDetailStmt.getObject(3) ;

				recoveryDetails = new ArrayList();
				while (resultSet.next())
				{
					recoveryDetail = new Recovery();
					recoveryDetail.setCgbid(borrowerId) ;
					recoveryDetail.setRecoveryNo(resultSet.getString(1)) ;
					recoveryDetail.setAmountRecovered(resultSet.getDouble(2)) ;
					java.sql.Date sqldate =  resultSet.getDate(3);
//					System.out.println("sql date form dao"+sqldate);
					recoveryDetail.setDateOfRecovery(sqldate) ;
					recoveryDetail.setLegalCharges(resultSet.getDouble(4)) ;
					recoveryDetail.setIsRecoveryByOTS(resultSet.getString(5)) ;
					recoveryDetail.setIsRecoveryBySaleOfAsset(resultSet.getString(6)) ;
					recoveryDetail.setDetailsOfAssetSold(resultSet.getString(7)) ;
					recoveryDetail.setRemarks(resultSet.getString(8)) ;
					recoveryDetails.add(recoveryDetail);
				}
				resultSet.close();
				resultSet = null;
				getRecoveryDetailStmt.close();
				getRecoveryDetailStmt=null;
				
				connection.commit() ;

			}catch (Exception exception) {
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage()) ;
			}
			finally
			{
				DBConnection.freeConnection(connection);
			}
			return recoveryDetails;
   	}



   /**
    * This method fetches borrower id for the borrower name .
    * @return String
    */
   public String  getBorrowerIdForBorrowerName(String borrowerName) throws DatabaseException {
		Log.log(Log.INFO,"GMDAO","getBorrowerIdForBorrowerName","Entered");
		Connection connection = DBConnection.getConnection();
	   	CallableStatement getBorrowerIdForBorrowerNameStmt = null;
		String borrowerId = null;

	   	try {
			getBorrowerIdForBorrowerNameStmt = connection.prepareCall("{? = call funcGetBIDforSSIName(?,?,?)}");

			getBorrowerIdForBorrowerNameStmt.registerOutParameter(1,Types.INTEGER);
			getBorrowerIdForBorrowerNameStmt.setString(2,borrowerName);
			getBorrowerIdForBorrowerNameStmt.registerOutParameter(3,java.sql.Types.VARCHAR);
			getBorrowerIdForBorrowerNameStmt.registerOutParameter(4,java.sql.Types.VARCHAR);

			getBorrowerIdForBorrowerNameStmt.execute();
			int updateStatus = getBorrowerIdForBorrowerNameStmt.getInt(1);

			String error = 	getBorrowerIdForBorrowerNameStmt.getString(4);

			if(updateStatus == Constants.FUNCTION_FAILURE ){
				getBorrowerIdForBorrowerNameStmt.close();
				getBorrowerIdForBorrowerNameStmt = null;
				Log.log(Log.ERROR,"GMDAO","getBorrowerIdForBorrowerName","Exception "+error);
				connection.rollback() ;
				throw new DatabaseException(error);
			}
			borrowerId = getBorrowerIdForBorrowerNameStmt.getString(3);

			getBorrowerIdForBorrowerNameStmt.close();
			getBorrowerIdForBorrowerNameStmt = null;
			connection.commit() ;
	   }
	   catch(Exception exception){
		Log.log(Log.ERROR,"GMDAO","getBorrowerIdForBorrowerName",exception.getMessage());
		Log.logException(exception);
		try
		{
			connection.rollback();
		}
		catch (SQLException ignore){}

		throw new DatabaseException(exception.getMessage());

	   }finally{
		DBConnection.freeConnection(connection);
	   }
		Log.log(Log.INFO,"GMDAO","getBorrowerIdForBorrowerName","Exited");
	   return borrowerId ;
   }

   /**
    * This method retrieves all the borrower ids for the given member id.
    * @param MemberId
    * @return Array
    * @roseuid 39B239660222
    */
   public ArrayList getBorrowerIds(String memberId) throws DatabaseException {

	Log.log(Log.INFO,"GMDAO","get Borrower IDs","Entered");
  // System.out.println("GMDAO"+"get Borrower IDs"+"Entered");
	   Connection connection = DBConnection.getConnection();
	   CallableStatement getBorrowerIdsStmt = null;

	   ResultSet resultSetBorrowerIds = null;
	   ArrayList borrowerIds = new ArrayList();

	   String bankId = memberId.substring(0,4);
	   String zoneId = memberId.substring(4,8);
	   String branchId = memberId.substring(8,12);
	   String borrowerId = "";

	   try {
   		  getBorrowerIdsStmt = connection.prepareCall("{? = call packGetAllBorrowerIdsForMemId.funcGetBorrowerIds(?,?,?,?,?)}");

	      getBorrowerIdsStmt.registerOutParameter(1,Types.INTEGER);
		  getBorrowerIdsStmt.registerOutParameter(5,Constants.CURSOR);
		  getBorrowerIdsStmt.registerOutParameter(6,java.sql.Types.VARCHAR);

		  getBorrowerIdsStmt.setString(2,bankId);
		  getBorrowerIdsStmt.setString(3,zoneId);
		  getBorrowerIdsStmt.setString(4,branchId);

		  getBorrowerIdsStmt.execute();
		  int updateStatus = getBorrowerIdsStmt.getInt(1);
		  String exception = getBorrowerIdsStmt.getString(6);

		  if(updateStatus == Constants.FUNCTION_FAILURE ){
		  	getBorrowerIdsStmt.close();
			getBorrowerIdsStmt = null;
			Log.log(Log.ERROR,"GMDAO","getBorrowerIds","Exception "+exception);
    //  System.out.println("GMDAO"+"getBorrowerIds"+"Exception "+exception);
			connection.rollback() ;
			throw new DatabaseException(exception);
		  }

		  resultSetBorrowerIds = (ResultSet)getBorrowerIdsStmt.getObject(5);

		  while(resultSetBorrowerIds.next()){
				borrowerId = resultSetBorrowerIds.getString(1);
				if(borrowerId!=null){
					borrowerIds.add(borrowerId);
				}
		  }
      //System.out.println("borrowerIds:"+borrowerIds);
		  resultSetBorrowerIds.close();
		  resultSetBorrowerIds = null;
     	  getBorrowerIdsStmt.close();
		  getBorrowerIdsStmt = null;
		  connection.commit() ;
	   }
	   catch(Exception exception){
  //   System.out.println("GMDAO"+"getBorrowerIds"+exception.getMessage());
		Log.log(Log.ERROR,"GMDAO","getBorrowerIds",exception.getMessage());
		Log.logException(exception);
		try
		{
			connection.rollback();
		}
		catch (SQLException ignore){}

		throw new DatabaseException(exception.getMessage());

	   }finally{
	   	DBConnection.freeConnection(connection);
	   }
  //   System.out.println("GMDAO"+"get Borrower IDs"+"Exited");
		Log.log(Log.INFO,"GMDAO","get Borrower IDs","Exited");

	   return borrowerIds ;
   }

/* ------------------- */
/**
   * 
   * @param cgpan
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
public String getallocationStatusforCgpan(String cgpan) throws DatabaseException {

            Log.log(Log.INFO,"GMDAO","getallocationStatusforCgpan","Entered");
 // System.out.println("GMDAO"+"getallocationStatusforCgpan"+"Entered");
	    Connection connection = DBConnection.getConnection();
            CallableStatement callableStmt = null;
		 Connection conn = null;
	   String allocationStatus = null;
            int status = -1;
		 String errorCode = null;
    
     try
		 {
			 conn = DBConnection.getConnection();
			 callableStmt = conn.prepareCall("{? = call funcGetAllStatusforCGPAN(?,?,?)}");
			 callableStmt.registerOutParameter(1,java.sql.Types.INTEGER);
			 callableStmt.setString(2,cgpan);
			 callableStmt.registerOutParameter(3,java.sql.Types.VARCHAR);
			 callableStmt.registerOutParameter(4,java.sql.Types.VARCHAR);

			 callableStmt.execute();
			 status =  callableStmt.getInt(1);
			 errorCode = callableStmt.getString(4);
    //  System.out.println("errorCode:"+errorCode);
			 if(status == Constants.FUNCTION_FAILURE)
			 {
				 Log.log(Log.ERROR,"GMDAO","getallocationStatusforCgpan()","SP returns a 1. Error code is :" + errorCode);
				 callableStmt.close();
				 throw new DatabaseException(errorCode);
			 }
			 else if(status == Constants.FUNCTION_SUCCESS)
			 {
				allocationStatus = callableStmt.getString(3);
    //     System.out.println("allocationStatus:"+allocationStatus);
				 callableStmt.close();
			 }
		 }
		 catch(SQLException sqlexception)
		 {
			   Log.log(Log.ERROR,"GMDAO","getallocationStatusforCgpan()","Error retrieving MemberID for the CGPAN!");
			   throw new DatabaseException(sqlexception.getMessage());
		 }
		 finally{
				DBConnection.freeConnection(conn);
		 }

	  
  //   System.out.println("GMDAO"+"getallocationStatusforCgpan"+"Exited");
		Log.log(Log.INFO,"GMDAO","getallocationStatusforCgpan","Exited");

	   return allocationStatus ;
   }
/**
   * 
   * @param cgpan
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
 public String getMemIdforCgpan(String cgpan) throws DatabaseException {

	Log.log(Log.INFO,"GMDAO","getMemIdforCgpan","Entered");
 // System.out.println("GMDAO"+"getMemIdforCgpan"+"Entered");
	   Connection connection = DBConnection.getConnection();
     CallableStatement callableStmt = null;
		 Connection conn = null;
	   String memIdforCgpan = null;
     int status = -1;
		 String errorCode = null;
    
     try
		 {
			 conn = DBConnection.getConnection();
			 callableStmt = conn.prepareCall("{? = call funcGetMLIIDforCGPAN(?,?,?)}");
			 callableStmt.registerOutParameter(1,java.sql.Types.INTEGER);
			 callableStmt.setString(2,cgpan);
			 callableStmt.registerOutParameter(3,java.sql.Types.VARCHAR);
			 callableStmt.registerOutParameter(4,java.sql.Types.VARCHAR);

			 callableStmt.execute();
			 status =  callableStmt.getInt(1);
			 errorCode = callableStmt.getString(4);
 //     System.out.println("errorCode:"+errorCode);
			 if(status == Constants.FUNCTION_FAILURE)
			 {
				 Log.log(Log.ERROR,"GMDAO","getMemIdforCgpan()","SP returns a 1. Error code is :" + errorCode);
				 callableStmt.close();
				 throw new DatabaseException(errorCode);
			 }
			 else if(status == Constants.FUNCTION_SUCCESS)
			 {
				memIdforCgpan = callableStmt.getString(3);
    //     System.out.println("memIdforCgpan:"+memIdforCgpan);
				 callableStmt.close();
			 }
		 }
		 catch(SQLException sqlexception)
		 {
			   Log.log(Log.ERROR,"GMDAO","getMemIdforCgpan()","Error retrieving MemberID for the CGPAN!");
			   throw new DatabaseException(sqlexception.getMessage());
		 }
		 finally{
				DBConnection.freeConnection(conn);
		 }

	  
  //   System.out.println("GMDAO"+"getMemIdforCgpan"+"Exited");
		Log.log(Log.INFO,"GMDAO","getMemIdforCgpan","Exited");

	   return memIdforCgpan ;
   }

/* --------------------- */
   public ArrayList getAllActions() throws
											   DatabaseException {

		   Log.log(Log.INFO,"GMDAO","getAllActions","Entered");
		   Connection connection = DBConnection.getConnection() ;
		   CallableStatement getAllActionsStmt = null ;
		   ResultSet resultSet = null;
		   ArrayList actions = null;
		   try {
			   String exception = "" ;

			getAllActionsStmt  = connection.prepareCall(
						   "{?=call packGetAllActions.funcGetAllActions(?,?)}");
			getAllActionsStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getAllActionsStmt.registerOutParameter(2,Constants.CURSOR) ;
			getAllActionsStmt.registerOutParameter(3,java.sql.Types.VARCHAR) ;

			getAllActionsStmt.execute() ;

			int error = getAllActionsStmt.getInt(1);
			exception = getAllActionsStmt.getString(3);

			if(error == Constants.FUNCTION_SUCCESS){
			   Log.log(Log.DEBUG,"GMDAO","funcGetAllActions","Success");
			}

			if(error==Constants.FUNCTION_FAILURE)
			{
				getAllActionsStmt.close();
				getAllActionsStmt=null;
				Log.log(Log.ERROR,"GMDAO","getAllActions","Exception "+exception);
				connection.rollback();
				throw new DatabaseException(exception);
			}

		    resultSet = (ResultSet)getAllActionsStmt.getObject(2) ;

			actions = new ArrayList();
			String action = null;
			while(resultSet.next())
			{
				action = resultSet.getString(1);
				if(action != null)
				{
					actions.add(action);
				}
			}
			resultSet.close();
			resultSet = null;
			getAllActionsStmt.close();
			getAllActionsStmt=null;
			connection.commit() ;

	   }catch (Exception exception) {
		try
		{
			connection.rollback();
		}
		catch (SQLException ignore){}

		   throw new DatabaseException(exception.getMessage()) ;
	   }finally{
		   DBConnection.freeConnection(connection);
   		}
	   Log.log(Log.INFO,"GMDAO","getAllActions","Exited");
	   return actions;
  }

  public CgpanDetail getCgpanDetails(String cgpan) throws DatabaseException {

		  Log.log(Log.INFO,"GMDAO","getCgpanDetails","Entered");
		  Connection connection = DBConnection.getConnection() ;
		  CallableStatement getCgpanDetailsStmt = null ;
		  ResultSet resultSet = null;
		  CgpanDetail cgpanDetail = null;
		  try {
			String exception = "" ;

			getCgpanDetailsStmt = connection.prepareCall(
						  "{?=call packGetCGPANDtl.funcGetCGPANDtl(?,?,?)}");
			getCgpanDetailsStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			getCgpanDetailsStmt.setString(2,cgpan) ;
			getCgpanDetailsStmt.registerOutParameter(3,Constants.CURSOR) ;
			getCgpanDetailsStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

			getCgpanDetailsStmt.execute() ;

		   int error = getCgpanDetailsStmt.getInt(1);
		   exception = getCgpanDetailsStmt.getString(4);

		   if(error == Constants.FUNCTION_SUCCESS){
			  Log.log(Log.DEBUG,"GMDAO","funcGetCGPANDtl","Success");
		   }

		   if(error==Constants.FUNCTION_FAILURE)
		   {
			   getCgpanDetailsStmt.close();
			   getCgpanDetailsStmt=null;
			   Log.log(Log.ERROR,"GMDAO","getCgpanDetails","Exception "+exception);
			   connection.rollback() ;
			   throw new DatabaseException(exception);
		   }

		   resultSet = (ResultSet)getCgpanDetailsStmt.getObject(3) ;

		   String prFirstName = null;
		   String prSecondName = null;
		   String prThirdName = null;

		   cgpanDetail = new CgpanDetail();
		   while(resultSet.next())
		   {
		        cgpanDetail.setBorrowerId(resultSet.getString(1))	;

				cgpanDetail.setBorrowerName(resultSet.getString(2))	;

				prFirstName =  resultSet.getString(3);
				prSecondName = resultSet.getString(4) ;
				prThirdName = resultSet.getString(5);
				if(prSecondName==null || prSecondName.equals(""))
				{
					cgpanDetail.setChiefPromoterName(prFirstName+" " +prThirdName);
				}
				else{
					
					cgpanDetail.setChiefPromoterName(prFirstName+ " " + prSecondName+" " +prThirdName);
				}
				

				cgpanDetail.setCity(resultSet.getString(6));

				cgpanDetail.setWcAmountSanctioned(resultSet.getDouble(7));

				cgpanDetail.setAmountApproved(resultSet.getDouble(8));

				cgpanDetail.setGuaranteeIssueDate(resultSet.getDate(9));

				cgpanDetail.setTcAmountSanctioned(resultSet.getDouble(10));
		   }

		   resultSet.close();
		   resultSet = null;
		   getCgpanDetailsStmt.close();
		   getCgpanDetailsStmt=null;
		   
		   connection.commit() ;

	  }catch (Exception exception) {
		try
		{
			connection.rollback();
		}
		catch (SQLException ignore){}

		  throw new DatabaseException(exception.getMessage()) ;
	  }finally{
			DBConnection.freeConnection(connection);
	   }
	  Log.log(Log.INFO,"GMDAO","getCGpanDetails","Exited");
	  return cgpanDetail;
 }
 
 
 
 /*
  * The vector is a collection of HashMaps. Each hashmap contains details
  * about each CGPAN as key-value pairs. This method will encapsulate all the
  * cgpans for a borrower. The hashmap also contains approved amount and
  * enhanced approved amount. Effectively, for each cgpan, we can get the
  * approved amount and enhanced approved amount.
  */
 public Vector getCGPANDetailsForBId(String borrowerId) throws DatabaseException
 {
   Log.log(Log.INFO,"GMDAO","getCGPANDetailsForBId","Entered!");
		ResultSet rs = null;
		HashMap cgpandetails = null;
		Vector allcgpandetails = new Vector();

		CallableStatement callableStmt = null;
		Connection conn = null;

		int status = -1;
		String errorCode = null;

		try
		{
			 conn = DBConnection.getConnection();
			 callableStmt = conn.prepareCall("{? = call packGetCGPANForBorrower.funcGetCGPANForBorrower(?,?,?)}");
			 callableStmt.registerOutParameter(1,java.sql.Types.INTEGER);
			 callableStmt.setString(2,borrowerId);
			 callableStmt.registerOutParameter(3,Constants.CURSOR);
			 callableStmt.registerOutParameter(4,java.sql.Types.VARCHAR);

			 callableStmt.execute();
			 status =  callableStmt.getInt(1);
			 errorCode = callableStmt.getString(4);

			 if(status == Constants.FUNCTION_FAILURE)
			 {
				  Log.log(Log.ERROR,"GMDAO","getCGPANDetailsForBId","SP returns a 1. Error code is :" + errorCode);

				  // closing the callable statement
				  callableStmt.close();
				  throw new DatabaseException(errorCode);
			 }
			 else if(status == Constants.FUNCTION_SUCCESS)
			 {
				 // Extracting the resultset from the callable statement
				 rs = (ResultSet)callableStmt.getObject(3);
				while(rs.next())
				{
				  String cgpan = null;
				  double approvedAmount = 0.0;
				  double  enhancedApprovedAmount = 0.0;
				  String loantype = null;

				  // reading from the resultset
				  cgpan = rs.getString(1);
				  approvedAmount = rs.getDouble(2);
				  enhancedApprovedAmount = rs.getDouble(3);
				  loantype = rs.getString(4);

					 // Populating the hashmap. Each hashmap will have info about
					 // one cgpan.
					 if(cgpan != null)
					 {
						 cgpandetails = new HashMap();
						 cgpandetails.put(ClaimConstants.CLM_CGPAN,cgpan);
						 cgpandetails.put(ClaimConstants.CGPAN_APPRVD_AMNT,new Double(approvedAmount));
						 cgpandetails.put(ClaimConstants.CGPAN_ENHANCED_APPRVD_AMNT, new Double( enhancedApprovedAmount));
						 cgpandetails.put(ClaimConstants.CGPAN_LOAN_TYPE, loantype);
						 allcgpandetails.add(cgpandetails);
					 }
				}

				// closing the resultset
				rs.close();
				// closing the callable statement
				callableStmt.close();
			}
	 }
	 catch(SQLException sqlexception)
	 {
		 Log.log(Log.ERROR,"GMDAO","getCGPANDetailsForBId","Error retrieving CGPAN Details for the Borrower!");
		 throw new DatabaseException(sqlexception.getMessage());
	 }
	 finally{
		 DBConnection.freeConnection(conn);
	 }
	 Log.log(Log.INFO,"GMDAO","getCGPANDetailsForBId","Exited!");
	 return allcgpandetails;
 }


/**
 * This method returns the outstanding details for the given list of cgpans.
 * It returns an Arraylist which contains 2 elements.
 *		The first element is an ArrayList of OutstandingDetail objects with the old values.
 *		The next element is an ArrayList of OutstandingDetail objects with the modified values.
 */
 public ArrayList getOutstandingsForApproval(ArrayList cgpanList) throws DatabaseException
 {
	  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","Entered");

	  Connection connection=DBConnection.getConnection();
//	  PeriodicInfo oldPeriodicInfo = new PeriodicInfo();
//	  PeriodicInfo newPeriodicInfo = new PeriodicInfo();
	  OutstandingDetail oldOsDetail = new OutstandingDetail();
	  OutstandingDetail newOsDetail = new OutstandingDetail();
	  OutstandingAmount oldOsAmt = new OutstandingAmount();
	  OutstandingAmount newOsAmt = new OutstandingAmount();
	  ArrayList oldOsAmts = new ArrayList();
	  ArrayList newOsAmts = new ArrayList();
	  ArrayList oldOsDtls = new ArrayList();
	  ArrayList newOsDtls = new ArrayList();
//	  ArrayList oldInfos = new ArrayList();
//	  ArrayList newInfos = new ArrayList();
	  ArrayList returnDetails = new ArrayList();
	  CallableStatement stmt;
	  ResultSet oldOsSet;
	  ResultSet newOsSet;
	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	  try
	  {
		  int size = cgpanList.size();
		  for (int i=0;i<size;i++)
		  {
//			  oldPeriodicInfo = new PeriodicInfo();
//			  newPeriodicInfo = new PeriodincInfo();
			  oldOsDetail = new OutstandingDetail();
			  newOsDetail = new OutstandingDetail();

			  oldOsAmts=new ArrayList();
			  newOsAmts=new ArrayList();

			  String cgpan = (String) cgpanList.get(i);
			  oldOsDetail.setCgpan(cgpan);
			  newOsDetail.setCgpan(cgpan);
			  String type = cgpan.substring(cgpan.length()-2, cgpan.length()-1);
			  //String date = "";			  
			  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting os detials for " + cgpan);
			  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","cgpan type " + type);

			  stmt=connection.prepareCall("{?=call packGetAllModifiedOutstandings.funcGetAllModifiedOutstandings(?,?,?,?)}");

			  stmt.setString(2, cgpan);
			  stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			  stmt.registerOutParameter(3, Constants.CURSOR);
			  stmt.registerOutParameter(4, Constants.CURSOR);
			  stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

			  stmt.execute();
			  
			  int status=stmt.getInt(1);
			  if (status==0)
			  {
				  newOsSet = (ResultSet) stmt.getObject(3);
				  oldOsSet = (ResultSet) stmt.getObject(4);
				  while (newOsSet.next())
				  {
					  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new os details ");
					  newOsAmt = new OutstandingAmount();
					  newOsAmt.setCgpan(cgpan);
					  if (type.equalsIgnoreCase("T"))
					  {
						  newOsAmt.setTcoId(newOsSet.getString(1));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new tco id " + newOsAmt.getTcoId());
						  newOsAmt.setTcPrincipalOutstandingAmount(newOsSet.getDouble(4));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new tco amount " + newOsAmt.getTcPrincipalOutstandingAmount());
						  //date = newOsSet.getDate(5);
						  newOsAmt.setTcOutstandingAsOnDate(DateHelper.sqlToUtilDate(newOsSet.getDate(5)));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new tco date " + newOsAmt.getTcOutstandingAsOnDate());
					  }
					  else if (type.equalsIgnoreCase("W") || type.equalsIgnoreCase("R"))
					  {
						  newOsAmt.setWcoId(newOsSet.getString(1));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco date " + newOsAmt.getWcoId());
						  newOsAmt.setWcFBPrincipalOutstandingAmount(newOsSet.getDouble(6));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco principal amount " + newOsAmt.getWcFBPrincipalOutstandingAmount());
						  newOsAmt.setWcFBInterestOutstandingAmount(newOsSet.getDouble(7));
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco interest amount " + newOsAmt.getWcFBInterestOutstandingAmount());
						  //date = newOsSet.getDate(8);
						Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco date " + newOsSet.getDate(8));
						  newOsAmt.setWcFBOutstandingAsOnDate(DateHelper.sqlToUtilDate(newOsSet.getDate(8)));						  
						  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco date " + newOsAmt.getWcFBOutstandingAsOnDate());
						  
							newOsAmt.setWcNFBPrincipalOutstandingAmount(newOsSet.getDouble(9));
							Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco principal amount " + newOsAmt.getWcNFBPrincipalOutstandingAmount());
							newOsAmt.setWcNFBInterestOutstandingAmount(newOsSet.getDouble(10));
							Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco interest amount " + newOsAmt.getWcNFBInterestOutstandingAmount());
							//date = newOsSet.getString(11);
							newOsAmt.setWcNFBOutstandingAsOnDate(DateHelper.sqlToUtilDate(newOsSet.getDate(11)));
							Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting new wco date " + newOsAmt.getWcFBOutstandingAsOnDate());
						  
					  }
					  newOsAmts.add(newOsAmt);
					  newOsAmt=null;
				  }
				  int newSize=newOsAmts.size();

				  while (oldOsSet.next())
				  {
					  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old os details ");
					  oldOsAmt = new OutstandingAmount();
					  oldOsAmt.setCgpan(cgpan);
					  String tempId = oldOsSet.getString(1);
					  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","old id " + tempId);
					  for (int j=0;j<newSize;j++)
					  {
						  OutstandingAmount tempOsAmt = new OutstandingAmount();
						  tempOsAmt = (OutstandingAmount) newOsAmts.get(j);

						  if (type.equalsIgnoreCase("T"))
						  {
							  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","new tco id " + tempOsAmt.getTcoId());


							  if (tempOsAmt.getTcoId() != null && tempOsAmt.getTcoId().equals(tempId))
							  {
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting tc old for new");
								  oldOsAmt.setTcoId(tempOsAmt.getTcoId());
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old tco id " + oldOsAmt.getTcoId());
								  oldOsAmt.setTcPrincipalOutstandingAmount(oldOsSet.getDouble(4));
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old tco amount " + oldOsAmt.getTcPrincipalOutstandingAmount());
								 // date = oldOsSet.getString(5);
								  oldOsAmt.setTcOutstandingAsOnDate(DateHelper.sqlToUtilDate(oldOsSet.getDate(5)));
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old tco date " + oldOsAmt.getTcOutstandingAsOnDate());
								  oldOsAmts.add(oldOsAmt);
								  break;
							  }
						  }
						  else if (type.equalsIgnoreCase("W") || type.equalsIgnoreCase("R"))
						  {
							  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","new wco id " + tempOsAmt.getWcoId());
							  if (tempOsAmt.getWcoId() != null && tempOsAmt.getWcoId().equals(tempId))
							  {
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting wc old for new");
								  oldOsAmt.setWcoId(tempOsAmt.getWcoId());
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco id " + oldOsAmt.getWcoId());
								  oldOsAmt.setWcFBPrincipalOutstandingAmount(oldOsSet.getDouble(6));
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco principal amount " + oldOsAmt.getWcFBPrincipalOutstandingAmount());
								  oldOsAmt.setWcFBInterestOutstandingAmount(oldOsSet.getDouble(7));
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco interest amount " + oldOsSet.getDate(8));
								  //date = oldOsSet.getString(8);
								  oldOsAmt.setWcFBOutstandingAsOnDate(DateHelper.sqlToUtilDate(oldOsSet.getDate(8)));
								  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco date " + oldOsAmt.getWcFBOutstandingAsOnDate());
								  
								oldOsAmt.setWcNFBPrincipalOutstandingAmount(oldOsSet.getDouble(9));
								Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco principal amount " + oldOsAmt.getWcNFBPrincipalOutstandingAmount());
								oldOsAmt.setWcNFBInterestOutstandingAmount(oldOsSet.getDouble(10));
								Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco interest amount " + oldOsAmt.getWcNFBInterestOutstandingAmount());
								//date = oldOsSet.getString(11);
								oldOsAmt.setWcNFBOutstandingAsOnDate(DateHelper.sqlToUtilDate(oldOsSet.getDate(11)));
								Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","getting old wco date " + oldOsAmt.getWcNFBOutstandingAsOnDate());
								  
								  oldOsAmts.add(oldOsAmt);
								  break;
							  }
						  }
						  tempOsAmt=null;
					  }
					  oldOsAmt=null;
				  }
				  newOsSet.close();
				  newOsSet=null;
				  oldOsSet.close();
				  oldOsSet=null;

				  stmt.close();
				  stmt=null;

				  oldOsDetail.setOutstandingAmounts(oldOsAmts);
				  newOsDetail.setOutstandingAmounts(newOsAmts);

				  oldOsDtls.add(oldOsDetail);
				  newOsDtls.add(newOsDetail);
			  }
			  else
			  {
				  String err = stmt.getString(5);
				  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","error getting os detials for " + cgpan);
				  stmt.close();
				  stmt=null;
				  throw new DatabaseException(err);
			  }
			  oldOsAmts=null;
			  newOsAmts=null;
			  oldOsDetail=null;
			  newOsDetail=null;
		  }
		  returnDetails.add(oldOsDtls);
		  returnDetails.add(newOsDtls);
	  }
	  catch (SQLException sqlException)
	  {
		Log.log(Log.ERROR,"RIDAO","getOutstandingsForApproval",sqlException.getMessage());
		Log.logException(sqlException);
		throw new DatabaseException(sqlException.getMessage());
	  }
	  finally
	  {
			DBConnection.freeConnection(connection);
	  }

	  Log.log(Log.INFO,"GMDAO","getOutstandingsForApproval","Exited");

	  return returnDetails;
 }



/**
 * This method returns the disbursement details for the given list of cgpans.
 * It returns an Arraylist which contains 2 elements.
 *		The first element is an ArrayList of DisbursementDetail objects with the old values.
 *		The next element is an ArrayList of DisbursementDetail objects with the modified values.
 */
 public ArrayList getDisbursementsForApproval(ArrayList cgpanList) throws DatabaseException
 {
	  Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","Entered");

	  Connection connection=DBConnection.getConnection();
//	  PeriodicInfo oldPeriodicInfo = new PeriodicInfo();
//	  PeriodicInfo newPeriodicInfo = new PeriodicInfo();
	  Disbursement oldDisDetail = new Disbursement();
	  Disbursement newDisDetail = new Disbursement();
	  DisbursementAmount oldDisAmt = new DisbursementAmount();
	  DisbursementAmount newDisAmt = new DisbursementAmount();
	  ArrayList oldDisAmts = new ArrayList();
	  ArrayList newDisAmts = new ArrayList();
	  ArrayList oldDisDtls = new ArrayList();
	  ArrayList newDisDtls = new ArrayList();
//	  ArrayList oldInfos = new ArrayList();
//	  ArrayList newInfos = new ArrayList();
	  ArrayList returnDetails = new ArrayList();
	  CallableStatement stmt;
	  ResultSet oldDisSet;
	  ResultSet newDisSet;
	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

	  try
	  {
	  		int size = cgpanList.size();
			for (int i=0;i<size;i++)
			{
//				oldPeriodicInfo = new PeriodicInfo();
//				newPeriodicInfo = new PeriodincInfo();
				oldDisDetail = new Disbursement();
				newDisDetail = new Disbursement();

				oldDisAmts=new ArrayList();
				newDisAmts=new ArrayList();

				String cgpan = (String) cgpanList.get(i);
				oldDisDetail.setCgpan(cgpan);
				newDisDetail.setCgpan(cgpan);
				String type = cgpan.substring(cgpan.length()-2, cgpan.length()-1);
				String date = "";
				Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","getting dis detials for " + cgpan);

				stmt=connection.prepareCall("{?=call packGetAllModifiedDis.funcGetAllModifiedDis(?,?,?,?)}");

				stmt.setString(2, cgpan);
				stmt.registerOutParameter(1, java.sql.Types.INTEGER);
				stmt.registerOutParameter(3, Constants.CURSOR);
				stmt.registerOutParameter(4, Constants.CURSOR);
				stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				stmt.execute();
				int status=stmt.getInt(1);
				if (status==0)
				{
					newDisSet = (ResultSet) stmt.getObject(3);
					oldDisSet = (ResultSet) stmt.getObject(4);
					while (newDisSet.next())
					{
						newDisAmt = new DisbursementAmount();
						newDisAmt.setCgpan(cgpan);
						newDisAmt.setDisbursementId(newDisSet.getString(1));
						Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","new dis id " + newDisAmt.getDisbursementId());
						newDisAmt.setDisbursementAmount(newDisSet.getDouble(4));
						Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","new dis amount " + newDisAmt.getDisbursementAmount());
						newDisAmt.setDisbursementDate(newDisSet.getDate(5));
						Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","new dis date " + newDisAmt.getDisbursementDate());
						newDisAmt.setFinalDisbursement(newDisSet.getString(6));
						Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","new dis final flag " + newDisAmt.getFinalDisbursement());

						newDisAmts.add(newDisAmt);
						newDisAmt=null;
					}
					newDisSet.close();
					newDisSet=null;
					int newSize=newDisAmts.size();

					while (oldDisSet.next())
					{
						oldDisAmt = new DisbursementAmount();
						oldDisAmt.setCgpan(cgpan);
						String tempId = oldDisSet.getString(1);
						Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","getting new id for old " + tempId);
						for (int j=0;j<newSize;j++)
						{
							DisbursementAmount tempDis = new DisbursementAmount();
							tempDis = (DisbursementAmount) newDisAmts.get(j);

							if (tempDis.getDisbursementId().equals(tempId))
							{
								Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","new found");
								oldDisAmt.setDisbursementId(tempId);
								Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","old dis id " + oldDisAmt.getDisbursementId());
								oldDisAmt.setDisbursementAmount(oldDisSet.getDouble(4));
								Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","old dis amount " + oldDisAmt.getDisbursementAmount());
								oldDisAmt.setDisbursementDate(oldDisSet.getDate(5));
								Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","old dis date " + oldDisAmt.getDisbursementDate());
								oldDisAmt.setFinalDisbursement(oldDisSet.getString(6));
								Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","old dis final flag " + oldDisAmt.getFinalDisbursement());

								oldDisAmts.add(oldDisAmt);
								break;
							}
							tempDis=null;
						}
						oldDisAmt=null;
					}
					oldDisSet.close();
					oldDisSet=null;

					stmt.close();
					stmt=null;

					oldDisDetail.setDisbursementAmounts(oldDisAmts);
					newDisDetail.setDisbursementAmounts(newDisAmts);

					oldDisDtls.add(oldDisDetail);
					newDisDtls.add(newDisDetail);
				}
				else
				{
					String err = stmt.getString(5);
					Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","error getting dis detials for " + cgpan);
					stmt.close();
					stmt=null;
					throw new DatabaseException(err);
				}
				oldDisAmts=null;
				newDisAmts=null;
				oldDisDetail=null;
				newDisDetail=null;
			}
			returnDetails.add(oldDisDtls);
			returnDetails.add(newDisDtls);
	  }
	  catch (SQLException sqlException)
	  {
			Log.log(Log.ERROR,"RIDAO","getDisbursementsForApproval",sqlException.getMessage());
			Log.logException(sqlException);
			throw new DatabaseException(sqlException.getMessage());
	  }
	  finally
	  {
			DBConnection.freeConnection(connection);
	  }

	  Log.log(Log.INFO,"GMDAO","getDisbursementsForApproval","Exited");

	  return returnDetails;
 }


/**
 * This method returns the repayment details for the given list of cgpans.
 * It returns an Arraylist which contains 2 elements.
 *		The first element is an ArrayList of Repayment objects with the old values.
 *		The next element is an ArrayList of Repayment objects with the modified values.
 */
 public ArrayList getRepaymentsForApproval(ArrayList cgpanList) throws DatabaseException
 {
	  Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","Entered");

	  Connection connection=DBConnection.getConnection();
//	  PeriodicInfo oldPeriodicInfo = new PeriodicInfo();
//	  PeriodicInfo newPeriodicInfo = new PeriodicInfo();
	  Repayment oldRepayDetail = new Repayment();
	  Repayment newRepayDetail = new Repayment();
	  RepaymentAmount oldRepayAmt = new RepaymentAmount();
	  RepaymentAmount newRepayAmt = new RepaymentAmount();
	  ArrayList oldRepayAmts = new ArrayList();
	  ArrayList newRepayAmts = new ArrayList();
	  ArrayList oldRepayDtls = new ArrayList();
	  ArrayList newRepayDtls = new ArrayList();
//	  ArrayList oldInfos = new ArrayList();
//	  ArrayList newInfos = new ArrayList();
	  ArrayList returnDetails = new ArrayList();
	  CallableStatement stmt;
	  ResultSet oldRepaySet;
	  ResultSet newRepaySet;
	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

	  try
	  {
	  		int size = cgpanList.size();
			for (int i=0;i<size;i++)
		    {
//				oldPeriodicInfo = new PeriodicInfo();
//				newPeriodicInfo = new PeriodincInfo();
				oldRepayDetail = new Repayment();
				newRepayDetail = new Repayment();

				oldRepayAmts=new ArrayList();
				newRepayAmts=new ArrayList();

				String cgpan = (String) cgpanList.get(i);
				oldRepayDetail.setCgpan(cgpan);
				newRepayDetail.setCgpan(cgpan);
				String type = cgpan.substring(cgpan.length()-2, cgpan.length()-1);
				String date = "";
				Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","getting repay detials for " + cgpan);
				stmt=connection.prepareCall("{?=call packGetAllModifiedRepayments.funcGetAllModifiedRepayments(?,?,?,?)}");

				stmt.setString(2, cgpan);
				stmt.registerOutParameter(1, java.sql.Types.INTEGER);
				stmt.registerOutParameter(3, Constants.CURSOR);
				stmt.registerOutParameter(4, Constants.CURSOR);
				stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				stmt.execute();
				int status=stmt.getInt(1);
				if (status==0)
				{
					newRepaySet = (ResultSet) stmt.getObject(3);
					oldRepaySet = (ResultSet) stmt.getObject(4);

					while (newRepaySet.next())
					{
						newRepayAmt = new RepaymentAmount();
						newRepayAmt.setCgpan(cgpan);
						newRepayAmt.setRepayId(newRepaySet.getString(1));
						Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","new repay id " + newRepayAmt.getRepayId());
						newRepayAmt.setRepaymentAmount(newRepaySet.getDouble(3));
						Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","new repay amount " + newRepayAmt.getRepaymentAmount());
						newRepayAmt.setRepaymentDate(newRepaySet.getDate(4));
						Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","new repay date " + newRepayAmt.getRepaymentDate());

						newRepayAmts.add(newRepayAmt);
						newRepayAmt=null;
					}
					newRepaySet.close();
					newRepaySet=null;
					int newSize = newRepayAmts.size();

					while (oldRepaySet.next())
					{
						oldRepayAmt = new RepaymentAmount();
						oldRepayAmt.setCgpan(cgpan);
						String tempId = oldRepaySet.getString(1);
						Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","old repay id " + tempId);

						for (int j=0;j<newSize;j++)
						{
							RepaymentAmount tempAmt = new RepaymentAmount();
							tempAmt = (RepaymentAmount) newRepayAmts.get(j);

							if (tempAmt.getRepayId().equals(tempId))
							{
								Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","new found for old");
								oldRepayAmt.setRepayId(tempId);
								oldRepayAmt.setRepaymentAmount(oldRepaySet.getDouble(3));
								Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","old repay amount " + oldRepayAmt.getRepaymentAmount());
								oldRepayAmt.setRepaymentDate(oldRepaySet.getDate(4));
								Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","old repay date " + oldRepayAmt.getRepaymentDate());

								oldRepayAmts.add(oldRepayAmt);
								break;
							}
							tempAmt=null;
						}
						oldRepayAmt=null;
					}
					oldRepaySet.close();
					oldRepaySet=null;

					stmt.close();
					stmt=null;

					oldRepayDetail.setRepaymentAmounts(oldRepayAmts);
					newRepayDetail.setRepaymentAmounts(newRepayAmts);

					oldRepayDtls.add(oldRepayDetail);
					newRepayDtls.add(newRepayDetail);
				}
				else
				{
					String err = stmt.getString(5);
					Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","error getting repay detials for " + cgpan);
					stmt.close();
					stmt=null;
					throw new DatabaseException(err);
				}
				oldRepayAmts=null;
				newRepayAmts=null;

				oldRepayDetail=null;
				newRepayDetail=null;
		    }
			returnDetails.add(oldRepayDtls);
			returnDetails.add(newRepayDtls);
	  }
	  catch (SQLException sqlException)
	  {
			Log.log(Log.ERROR,"RIDAO","getRepaymentsForApproval",sqlException.getMessage());
			Log.logException(sqlException);
			throw new DatabaseException(sqlException.getMessage());
	  }
	  finally
	  {
			DBConnection.freeConnection(connection);
	  }

	  Log.log(Log.INFO,"GMDAO","getRepaymentsForApproval","Exited");

	  return returnDetails;
 }

/**
 * This method returns the npa details for the given borrower id.
 * It returns an Arraylist which contains 2 elements.
 *		The first element is a NPA object with the old values.
 *		The next element is a NPA object with the new values.
 */
 public ArrayList getNpaDetailsForApproval(String borrowerId ) throws DatabaseException
 {
	  Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","Entered");

	  ArrayList returnDetails = new ArrayList();
	  ArrayList oldRecActions = new ArrayList();
	  ArrayList newRecActions = new ArrayList();

	  NPADetails oldNpaDetail = new NPADetails();
	  NPADetails newNpaDetail = new NPADetails();
	  LegalSuitDetail oldSuit = new LegalSuitDetail();
	  LegalSuitDetail newSuit = new LegalSuitDetail();
	  RecoveryProcedure oldRecProc = new RecoveryProcedure();
	  RecoveryProcedure newRecProc = new RecoveryProcedure();
	  Connection connection=DBConnection.getConnection();
	  CallableStatement stmt;
	  ResultSet oldSet;
	  ResultSet newSet;
	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	  try
	  {
		stmt=connection.prepareCall("{?=call packGetAllModifiedNPA.funcGetAllModifiedNPA(?,?,?,?)}");

		Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","getting npa for " + borrowerId);
		stmt.setString(2, borrowerId);
		stmt.registerOutParameter(1, java.sql.Types.INTEGER);
		stmt.registerOutParameter(3, Constants.CURSOR);
		stmt.registerOutParameter(4, Constants.CURSOR);
		stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

		stmt.execute();
		int status=stmt.getInt(1);
		if (status==Constants.FUNCTION_SUCCESS)
		{
			newSet = (ResultSet) stmt.getObject(3);
			oldSet = (ResultSet) stmt.getObject(4);

			while (newSet.next())
			{
				newNpaDetail = new NPADetails();
				newNpaDetail.setNpaId(newSet.getString(1));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa id " + newNpaDetail.getNpaId());
				newNpaDetail.setCgbid(newSet.getString(2));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa cgbid " + newNpaDetail.getCgbid());
				newNpaDetail.setNpaDate(newSet.getDate(3));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa date " + newNpaDetail.getNpaDate());
				newNpaDetail.setWhetherNPAReported(newSet.getString(4));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa reported " + newNpaDetail.getWhetherNPAReported());
				newNpaDetail.setReportingDate(newSet.getDate(6));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa reporting date " + newNpaDetail.getReportingDate());
				newNpaDetail.setReference(newSet.getString(7));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa reference " + newNpaDetail.getReference());
				newNpaDetail.setOsAmtOnNPA(newSet.getDouble(8));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa os amt " + newNpaDetail.getOsAmtOnNPA());
				newNpaDetail.setNpaReason(newSet.getString(9));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa reason " + newNpaDetail.getNpaReason());
				newNpaDetail.setRemarksOnNpa(newSet.getString(10));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa remarks " + newNpaDetail.getRemarksOnNpa());
				newNpaDetail.setIsRecoveryInitiated(newSet.getString(11));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa rec initiated " + newNpaDetail.getIsRecoveryInitiated());
				newNpaDetail.setNoOfActions(newSet.getInt(12));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa no of actions " + newNpaDetail.getNoOfActions());
				newNpaDetail.setEffortsConclusionDate(newSet.getDate(13));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa eff conc date " + newNpaDetail.getEffortsConclusionDate());
				newNpaDetail.setMliCommentOnFinPosition(newSet.getString(15)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa comm on fin position " + newNpaDetail.getMliCommentOnFinPosition());
				newNpaDetail.setDetailsOfFinAssistance(newSet.getString(16)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa fin assistance " + newNpaDetail.getDetailsOfFinAssistance());
				newNpaDetail.setCreditSupport(newSet.getString(17)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa credit support " + newNpaDetail.getCreditSupport());
				newNpaDetail.setBankFacilityDetail(newSet.getString(18)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa bank facility " + newNpaDetail.getBankFacilityDetail());
				newNpaDetail.setWillfulDefaulter(newSet.getString(19));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa willfull " + newNpaDetail.getWillfulDefaulter());
				newNpaDetail.setPlaceUnderWatchList(newSet.getString(20));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa watchlist " + newNpaDetail.getPlaceUnderWatchList());
				newNpaDetail.setRemarksOnNpa(newSet.getString(24));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new npa remarks " + newNpaDetail.getRemarksOnNpa());
			}

			while (oldSet.next())
			{
				oldNpaDetail = new NPADetails();
				oldNpaDetail.setNpaId(oldSet.getString(1));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa id " + oldNpaDetail.getNpaId());
				oldNpaDetail.setCgbid(oldSet.getString(2));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa cgbid " + oldNpaDetail.getCgbid());
				oldNpaDetail.setNpaDate(oldSet.getDate(3));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa date " + oldNpaDetail.getNpaDate());
				oldNpaDetail.setWhetherNPAReported(oldSet.getString(4));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa reported " + oldNpaDetail.getWhetherNPAReported());
				oldNpaDetail.setReportingDate(oldSet.getDate(6));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa reporting date " + oldNpaDetail.getReportingDate());
				oldNpaDetail.setReference(oldSet.getString(7));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa reference " + oldNpaDetail.getReference());
				oldNpaDetail.setOsAmtOnNPA(oldSet.getDouble(8));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa os amt " + oldNpaDetail.getOsAmtOnNPA());
				oldNpaDetail.setNpaReason(oldSet.getString(9));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa reason " + oldNpaDetail.getNpaReason());
				oldNpaDetail.setRemarksOnNpa(oldSet.getString(10));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa remarks " + oldNpaDetail.getRemarksOnNpa());
				oldNpaDetail.setIsRecoveryInitiated(oldSet.getString(11));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa rec initiated " + oldNpaDetail.getIsRecoveryInitiated());
				oldNpaDetail.setNoOfActions(oldSet.getInt(12));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa no of actions " + oldNpaDetail.getNoOfActions());
				oldNpaDetail.setEffortsConclusionDate(oldSet.getDate(13));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa eff conc date " + oldNpaDetail.getEffortsConclusionDate());
				oldNpaDetail.setMliCommentOnFinPosition(oldSet.getString(15)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa comm on fin position " + oldNpaDetail.getMliCommentOnFinPosition());
				oldNpaDetail.setDetailsOfFinAssistance(oldSet.getString(16)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa fin assistance " + oldNpaDetail.getDetailsOfFinAssistance());
				oldNpaDetail.setCreditSupport(oldSet.getString(17)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa credit support " + oldNpaDetail.getCreditSupport());
				oldNpaDetail.setBankFacilityDetail(oldSet.getString(18)) ;
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa bank facility " + oldNpaDetail.getBankFacilityDetail());
				oldNpaDetail.setWillfulDefaulter(oldSet.getString(19));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa willfull " + oldNpaDetail.getWillfulDefaulter());
				oldNpaDetail.setPlaceUnderWatchList(oldSet.getString(20));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa watchlist " + oldNpaDetail.getPlaceUnderWatchList());
				oldNpaDetail.setRemarksOnNpa(oldSet.getString(24));
				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old npa remarks " + oldNpaDetail.getRemarksOnNpa());
			}
			oldSet.close();
			oldSet=null;
			newSet.close();
			newSet=null;
			stmt.close();
			stmt=null;

			String npaId = oldNpaDetail.getNpaId();
			if (npaId != null && !npaId.equals(""))
			{
				stmt = connection.prepareCall("{?=call packGetAllModifiedRecActions.funcGetAllModifiedRecActions(?,?,?,?)}");

				Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","getting rec actions for npa " + npaId);
				stmt.setString(2, npaId);
				stmt.registerOutParameter(1, java.sql.Types.INTEGER);
				stmt.registerOutParameter(3, Constants.CURSOR);
				stmt.registerOutParameter(4, Constants.CURSOR);
				stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				stmt.execute();
				status=stmt.getInt(1);
				if (status==Constants.FUNCTION_SUCCESS)
				{
					oldSet = (ResultSet) stmt.getObject(4);
					newSet = (ResultSet) stmt.getObject(3);
					while (newSet.next())
					{
						newRecProc = new RecoveryProcedure();
						newRecProc.setRadId(newSet.getString(1));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc id " + newRecProc.getRadId());
						newRecProc.setActionType(newSet.getString(2));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc action type " + newRecProc.getActionType());
						newRecProc.setNpaId(newSet.getString(3));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc npa id " + newRecProc.getNpaId());
						newRecProc.setActionDetails(newSet.getString(4));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc action details " + newRecProc.getActionDetails());
						newRecProc.setActionDate(newSet.getDate(5));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc action date " + newRecProc.getActionDate());
						newRecProc.setAttachmentName(newSet.getString(6));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new recproc attachment " + newRecProc.getAttachmentName());

						newRecActions.add(newRecProc);
						newRecProc=null;
					}
					newNpaDetail.setRecoveryProcedure(newRecActions);

					while (oldSet.next())
					{
						oldRecProc = new RecoveryProcedure();
						oldRecProc.setRadId(oldSet.getString(1));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc id " + oldRecProc.getRadId());
						for (int i=0;i<newRecActions.size();i++)
						{
							RecoveryProcedure tempRec = (RecoveryProcedure) newRecActions.get(i);
							if (oldRecProc.getRadId().equals(tempRec.getRadId()))
							{
								oldRecProc.setActionType(oldSet.getString(2));
								Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc action type " + oldRecProc.getActionType());
								oldRecProc.setNpaId(oldSet.getString(3));
								Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc npa id " + oldRecProc.getNpaId());
								oldRecProc.setActionDetails(oldSet.getString(4));
								Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc action details " + oldRecProc.getActionDetails());
								oldRecProc.setActionDate(oldSet.getDate(5));
								Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc action date " + oldRecProc.getActionDate());
								oldRecProc.setAttachmentName(oldSet.getString(6));
								Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old recproc attachment " + oldRecProc.getAttachmentName());

								oldRecActions.add(oldRecProc);
								break;
							}
						}
						oldRecProc=null;
					}
					oldNpaDetail.setRecoveryProcedure(oldRecActions);

					oldSet.close();
					oldSet=null;
					newSet.close();
					newSet=null;
					stmt.close();
					stmt=null;
				}
				else
				{
					String err = stmt.getString(5);
					Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","error getting recovery action detials for " + npaId);
					stmt.close();
					stmt=null;
					throw new DatabaseException(err);
				}

				stmt = connection.prepareCall("{?=call packGetAllModifiedLegalSuit.funcGetAllModifiedLegalSuit(?,?,?,?)}");
				stmt.setString(2, npaId);
				stmt.registerOutParameter(1, java.sql.Types.INTEGER);
				stmt.registerOutParameter(3, Constants.CURSOR);
				stmt.registerOutParameter(4, Constants.CURSOR);
				stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				stmt.execute();
				status=stmt.getInt(1);
				if (status==Constants.FUNCTION_SUCCESS)
				{
					oldSet = (ResultSet) stmt.getObject(4);
					newSet = (ResultSet) stmt.getObject(3);
					while (newSet.next())
					{
						newSuit = new LegalSuitDetail();
						newSuit.setLegalSuiteNo(newSet.getString(1));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit suite no " + newSuit.getLegalSuiteNo());
						newSuit.setNpaId(newSet.getString(2));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit npa id " + newSuit.getNpaId());
						newSuit.setCourtName(newSet.getString(3));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit court name " + newSuit.getCourtName());
						newSuit.setForumName(newSet.getString(4));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit forum name " + newSuit.getForumName());
						newSuit.setLocation(newSet.getString(5));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit location " + newSuit.getLocation());
						newSuit.setDtOfFilingLegalSuit(newSet.getDate(6));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit filing date " + newSuit.getDtOfFilingLegalSuit());
						newSuit.setAmountClaimed(newSet.getDouble(7));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit amount claimed " + newSuit.getAmountClaimed());
						newSuit.setCurrentStatus(newSet.getString(8));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit current status " + newSuit.getCurrentStatus());
						newSuit.setRecoveryProceedingsConcluded(newSet.getString(9));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","new legalsuit concuded " + newSuit.getRecoveryProceedingsConcluded());
					}
					newNpaDetail.setLegalSuitDetail(newSuit);
					newSuit=null;
					while (oldSet.next())
					{
						oldSuit = new LegalSuitDetail();
						oldSuit.setLegalSuiteNo(oldSet.getString(1));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit suite no " + oldSuit.getLegalSuiteNo());
						oldSuit.setNpaId(oldSet.getString(2));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit npa id " + oldSuit.getNpaId());
						oldSuit.setCourtName(oldSet.getString(3));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit court name " + oldSuit.getCourtName());
						oldSuit.setForumName(oldSet.getString(4));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit forum name " + oldSuit.getForumName());
						oldSuit.setLocation(oldSet.getString(5));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit location " + oldSuit.getLocation());
						oldSuit.setDtOfFilingLegalSuit(oldSet.getDate(6));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit filing date " + oldSuit.getDtOfFilingLegalSuit());
						oldSuit.setAmountClaimed(oldSet.getDouble(7));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit amount claimed " + oldSuit.getAmountClaimed());
						oldSuit.setCurrentStatus(oldSet.getString(8));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit current status " + oldSuit.getCurrentStatus());
						oldSuit.setRecoveryProceedingsConcluded(oldSet.getString(9));
						Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","old legalsuit concuded " + oldSuit.getRecoveryProceedingsConcluded());
					}
					oldNpaDetail.setLegalSuitDetail(oldSuit);
					oldSuit=null;

					oldSet.close();
					oldSet=null;
					newSet.close();
					newSet=null;
					stmt.close();
					stmt=null;

					returnDetails.add(oldNpaDetail);
					returnDetails.add(newNpaDetail);
				}
				else
				{
					String err = stmt.getString(5);
					Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","error getting legal suit detials for " + npaId);
					stmt.close();
					stmt=null;
					throw new DatabaseException(err);
				}
			}
		}
		else
		{
			String err = stmt.getString(5);
			Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","error getting npa detials for " + borrowerId);
			stmt.close();
			stmt=null;
			throw new DatabaseException(err);
		}
	  }
	 catch (SQLException sException)
	 {
		 throw new DatabaseException(sException.getMessage());
	 }
	 finally
	 {
		DBConnection.freeConnection(connection);
	 }

	  Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","Exited");
	  return returnDetails;
 }


 /**
  * This method returns the borrower details for the given ssi ref no.
  * It returns an Arraylist which contains 2 elements.
  *		The first element is a BorrowerDetail object with the old values.
  *		The next element is a BorrowerDetail object with the new values.
  */
  public ArrayList getBorrowerDetailsForApproval(int ssiRefNo) throws DatabaseException
  {
	Log.log(Log.INFO,"GMDAO","getNpaDetailsForApproval","Entered");

	ArrayList returnDetails = new ArrayList();

	BorrowerDetails oldBorrowerDtl = new BorrowerDetails();
	BorrowerDetails newBorrowerDtl = new BorrowerDetails();

	SSIDetails oldSSIDtl = new SSIDetails();
	SSIDetails newSSIDtl = new SSIDetails();

	Connection connection=DBConnection.getConnection();
	CallableStatement stmt;
	ResultSet oldBorrowerSet;
	ResultSet newBorrowerSet;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	try
	{
	  stmt=connection.prepareCall("{?=call packGetModifiedBorrowerDtl.funcGetModifiedBorrowerDtl(?,?,?,?)}");

	  Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","getting borrower details for " + ssiRefNo);
	  stmt.setInt(2, ssiRefNo);
	  stmt.registerOutParameter(1, java.sql.Types.INTEGER);
	  stmt.registerOutParameter(3, Constants.CURSOR);
	  stmt.registerOutParameter(4, Constants.CURSOR);
	  stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

	  stmt.execute();
	  int status=stmt.getInt(1);
	  if (status==Constants.FUNCTION_SUCCESS)
	  {
		  newBorrowerSet = (ResultSet) stmt.getObject(3);
		  oldBorrowerSet = (ResultSet) stmt.getObject(4);

		  while (newBorrowerSet.next())
		  {
			  newBorrowerDtl = new BorrowerDetails();
			  newSSIDtl = new SSIDetails();

			  newSSIDtl.setBorrowerRefNo(newBorrowerSet.getInt(1));
			  newSSIDtl.setCgbid(newBorrowerSet.getString(2));
			  newSSIDtl.setSsiName(newBorrowerSet.getString(4));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new name " + newSSIDtl.getSsiName());
			  newSSIDtl.setAddress(newBorrowerSet.getString(5));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new address " + newSSIDtl.getAddress());
			  newSSIDtl.setCity(newBorrowerSet.getString(6));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new city " + newSSIDtl.getCity());
			  newSSIDtl.setState(newBorrowerSet.getString(7));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new State " + newSSIDtl.getState());
			  newSSIDtl.setDistrict(newBorrowerSet.getString(8));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new Dist " + newSSIDtl.getDistrict());
			  newSSIDtl.setPincode(newBorrowerSet.getString(9));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new pin code " + newSSIDtl.getPincode());
			  newSSIDtl.setSsiType(newBorrowerSet.getString(10));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new ssi type " + newSSIDtl.getSsiType());
			  newSSIDtl.setIndustryNature(newBorrowerSet.getString(11));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new ind nature " + newSSIDtl.getIndustryNature());
			  newSSIDtl.setIndustrySector(newBorrowerSet.getString(12));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new ind sector " + newSSIDtl.getIndustrySector());
			  newSSIDtl.setConstitution(newBorrowerSet.getString(13));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new constitution " + newSSIDtl.getConstitution());
			  newSSIDtl.setRegNo(newBorrowerSet.getString(14));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new reg no " + newSSIDtl.getRegNo());
			  newSSIDtl.setEmployeeNos(newBorrowerSet.getInt(15));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new emp no " + newSSIDtl.getEmployeeNos());
			  newSSIDtl.setProjectedSalesTurnover(newBorrowerSet.getDouble(16));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new turn over " + newSSIDtl.getProjectedSalesTurnover());
			  newSSIDtl.setProjectedExports(newBorrowerSet.getDouble(17));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new exports " + newSSIDtl.getProjectedExports());
			  newSSIDtl.setSsiITPan(newBorrowerSet.getString(18));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new ssi itpan " + newSSIDtl.getSsiITPan());
		 	  newSSIDtl.setCommencementDate(newBorrowerSet.getDate(19));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new commencement date " + newSSIDtl.getCommencementDate());
			  newSSIDtl.setActivityType(newBorrowerSet.getString(20));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new act type " + newSSIDtl.getActivityType());
			  newBorrowerDtl.setNpa(newBorrowerSet.getString(21));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new npa " + newBorrowerDtl.getNpa());
			  newBorrowerDtl.setAssistedByBank(newBorrowerSet.getString(22));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new assisted " + newBorrowerDtl.getAssistedByBank());
			  newBorrowerDtl.setPreviouslyCovered(newBorrowerSet.getString(23));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new covered " + newBorrowerDtl.getPreviouslyCovered());
			  newSSIDtl.setDisplayDefaultersList(newBorrowerSet.getString(24));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new defaulter " + newSSIDtl.getDisplayDefaultersList());
			  newBorrowerDtl.setOsAmt(newBorrowerSet.getDouble(25));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new os amt " + newBorrowerDtl.getOsAmt());
			  newSSIDtl.setCpTitle(newBorrowerSet.getString(26));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp title " + newSSIDtl.getCpTitle());
			  newSSIDtl.setCpFirstName(newBorrowerSet.getString(27));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp first name " + newSSIDtl.getCpFirstName());
			  newSSIDtl.setCpMiddleName(newBorrowerSet.getString(28));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp middle name " + newSSIDtl.getCpMiddleName());
			  newSSIDtl.setCpLastName(newBorrowerSet.getString(29));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp last name " + newSSIDtl.getCpLastName());
			  newSSIDtl.setCpITPAN(newBorrowerSet.getString(30));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp itpan " + newSSIDtl.getCpITPAN());
			  newSSIDtl.setCpGender(newBorrowerSet.getString(31));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp gender " + newSSIDtl.getCpGender());
			  newSSIDtl.setCpDOB(newBorrowerSet.getDate(32));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp dob " + newSSIDtl.getCpDOB());
			  newSSIDtl.setCpLegalID(newBorrowerSet.getString(33));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new cp legal id " + newSSIDtl.getCpLegalID());
			  newSSIDtl.setCpLegalIdValue(newBorrowerSet.getString(34));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new legal id value " + newSSIDtl.getCpLegalIdValue());
			  newSSIDtl.setFirstName(newBorrowerSet.getString(35));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other first name " + newSSIDtl.getFirstName());
			  newSSIDtl.setFirstDOB(newBorrowerSet.getDate(36));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other first dob " + newSSIDtl.getFirstDOB());
			  newSSIDtl.setFirstItpan(newBorrowerSet.getString(37));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other first itpan " + newSSIDtl.getFirstItpan());
			  newSSIDtl.setSecondName(newBorrowerSet.getString(38));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other second name " + newSSIDtl.getSecondName());
			  newSSIDtl.setSecondDOB(newBorrowerSet.getDate(39));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new otehr second dob " + newSSIDtl.getSecondDOB());
			  newSSIDtl.setSecondItpan(newBorrowerSet.getString(40));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other second itpan " + newSSIDtl.getSecondItpan());
			  newSSIDtl.setThirdName(newBorrowerSet.getString(41));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other third name " + newSSIDtl.getThirdName());
			  newSSIDtl.setThirdDOB(newBorrowerSet.getDate(42));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other third dob " + newSSIDtl.getThirdDOB());
			  newSSIDtl.setThirdItpan(newBorrowerSet.getString(43));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new other third itpan " + newSSIDtl.getThirdItpan());
			  newSSIDtl.setSocialCategory(newBorrowerSet.getString(44));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","new social category " + newSSIDtl.getSocialCategory());

			  newBorrowerDtl.setSsiDetails(newSSIDtl);
		  }

		while (oldBorrowerSet.next())
		{
			oldBorrowerDtl = new BorrowerDetails();
			oldSSIDtl = new SSIDetails();

			oldSSIDtl.setBorrowerRefNo(oldBorrowerSet.getInt(1));
			oldSSIDtl.setCgbid(oldBorrowerSet.getString(2));
			oldSSIDtl.setSsiName(oldBorrowerSet.getString(4));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old name " + oldSSIDtl.getSsiName());
			oldSSIDtl.setAddress(oldBorrowerSet.getString(5));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old address " + oldSSIDtl.getAddress());
			oldSSIDtl.setCity(oldBorrowerSet.getString(6));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old city " + oldSSIDtl.getCity());
			oldSSIDtl.setState(oldBorrowerSet.getString(7));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old state " + oldSSIDtl.getState());
			oldSSIDtl.setDistrict(oldBorrowerSet.getString(8));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old dist " + oldSSIDtl.getDistrict());
			oldSSIDtl.setPincode(oldBorrowerSet.getString(9));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old pincode " + oldSSIDtl.getPincode());
			oldSSIDtl.setSsiType(oldBorrowerSet.getString(10));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old ssi type " + oldSSIDtl.getSsiType());
			oldSSIDtl.setIndustryNature(oldBorrowerSet.getString(11));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old ind nature " + oldSSIDtl.getIndustryNature());
			oldSSIDtl.setIndustrySector(oldBorrowerSet.getString(12));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old ind secotr " + oldSSIDtl.getIndustrySector());
			oldSSIDtl.setConstitution(oldBorrowerSet.getString(13));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old const " + oldSSIDtl.getConstitution());
			oldSSIDtl.setRegNo(oldBorrowerSet.getString(14));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old reg no " + oldSSIDtl.getRegNo());
			oldSSIDtl.setEmployeeNos(oldBorrowerSet.getInt(15));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old emp no " + oldSSIDtl.getEmployeeNos());
			oldSSIDtl.setProjectedSalesTurnover(oldBorrowerSet.getDouble(16));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old turnover " + oldSSIDtl.getProjectedSalesTurnover());
			oldSSIDtl.setProjectedExports(oldBorrowerSet.getDouble(17));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old exports " + oldSSIDtl.getProjectedExports());
			oldSSIDtl.setSsiITPan(oldBorrowerSet.getString(18));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old ssi itpan " + oldSSIDtl.getSsiITPan());
			oldSSIDtl.setCommencementDate(oldBorrowerSet.getDate(19));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old commencement date " + oldSSIDtl.getCommencementDate());
			oldSSIDtl.setActivityType(oldBorrowerSet.getString(20));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old" + oldSSIDtl.getActivityType());
			oldBorrowerDtl.setNpa(oldBorrowerSet.getString(21));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old npa " + oldBorrowerDtl.getNpa());
			oldBorrowerDtl.setAssistedByBank(oldBorrowerSet.getString(22));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old assisted " + oldBorrowerDtl.getAssistedByBank());
			oldBorrowerDtl.setPreviouslyCovered(oldBorrowerSet.getString(23));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old covered " + oldBorrowerDtl.getPreviouslyCovered());
			oldSSIDtl.setDisplayDefaultersList(oldBorrowerSet.getString(24));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old defaulter " + oldSSIDtl.getDisplayDefaultersList());
			oldBorrowerDtl.setOsAmt(oldBorrowerSet.getDouble(25));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old os amt " + oldBorrowerDtl.getOsAmt());
			oldSSIDtl.setCpTitle(oldBorrowerSet.getString(26));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp title " + oldSSIDtl.getCpTitle());
			oldSSIDtl.setCpFirstName(oldBorrowerSet.getString(27));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp first name " + oldSSIDtl.getCpFirstName());
			oldSSIDtl.setCpMiddleName(oldBorrowerSet.getString(28));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp middle name " + oldSSIDtl.getCpMiddleName());
			oldSSIDtl.setCpLastName(oldBorrowerSet.getString(29));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp last name " + oldSSIDtl.getCpLastName());
			oldSSIDtl.setCpITPAN(oldBorrowerSet.getString(30));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp itpan " + oldSSIDtl.getCpITPAN());
			oldSSIDtl.setCpGender(oldBorrowerSet.getString(31));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp gender " + oldSSIDtl.getCpGender());
			oldSSIDtl.setCpDOB(oldBorrowerSet.getDate(32));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp dob " + oldSSIDtl.getCpDOB());
			oldSSIDtl.setCpLegalID(oldBorrowerSet.getString(33));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp legal id " + oldSSIDtl.getCpLegalID());
			oldSSIDtl.setCpLegalIdValue(oldBorrowerSet.getString(34));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old cp legal id vale " + oldSSIDtl.getCpLegalIdValue());
			oldSSIDtl.setFirstName(oldBorrowerSet.getString(35));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other first name " + oldSSIDtl.getFirstName());
			oldSSIDtl.setFirstDOB(oldBorrowerSet.getDate(36));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other first dob " + oldSSIDtl.getFirstDOB());
			oldSSIDtl.setFirstItpan(oldBorrowerSet.getString(37));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other first itpan " + oldSSIDtl.getFirstItpan());
			oldSSIDtl.setSecondName(oldBorrowerSet.getString(38));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other second name " + oldSSIDtl.getSecondName());
			oldSSIDtl.setSecondDOB(oldBorrowerSet.getDate(39));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old otehr second dob " + oldSSIDtl.getSecondDOB());
			oldSSIDtl.setSecondItpan(oldBorrowerSet.getString(40));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other second itpan " + oldSSIDtl.getSecondItpan());
			oldSSIDtl.setThirdName(oldBorrowerSet.getString(41));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other thrid name " + oldSSIDtl.getThirdName());
			oldSSIDtl.setThirdDOB(oldBorrowerSet.getDate(42));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other third dob " + oldSSIDtl.getThirdDOB());
			oldSSIDtl.setThirdItpan(oldBorrowerSet.getString(43));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old other third itpan " + oldSSIDtl.getThirdItpan());
			oldSSIDtl.setSocialCategory(oldBorrowerSet.getString(44));
			Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","old social category " + oldSSIDtl.getSocialCategory());

			oldBorrowerDtl.setSsiDetails(oldSSIDtl);
		}
		returnDetails.add(oldBorrowerDtl);
		returnDetails.add(newBorrowerDtl);

		newBorrowerSet.close();
		oldBorrowerSet.close();
		newBorrowerSet=null;
		oldBorrowerSet=null;
		stmt.close();
		stmt=null;
	  }
	  else
	  {
		  String err = stmt.getString(5);
		  Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","error getting borrower detials for " + ssiRefNo);
		  stmt.close();
		  stmt=null;
		  throw new DatabaseException(err);
	  }

   }
   catch (SQLException sException)
   {
	   throw new DatabaseException(sException.getMessage());
   }
   finally
   {
	  DBConnection.freeConnection(connection);
   }

   Log.log(Log.INFO,"GMDAO","getBorrowerDetailsForApproval","Exited");
   return returnDetails;

  }


	/**
	  * This method approves the periodic info for the given member id an borrower id.
	  * If the borrower id is not given, then the list of borrower ids for the given member is retirved
	  *		and for each borrower id, the periodic info details are got and approved.
	  */
	 public void approvePeriodicInfo(String memberId, String borrowerId) throws DatabaseException,NoApplicationFoundException
	 {
		 Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 1","Entered");

		 Connection connection = DBConnection.getConnection(false);

		 ArrayList borrowerIds = getBorrowerIds(memberId);
		 try
		 {
			 if (borrowerId != null && ! borrowerId.trim().equals(""))
			 {
				 approvePeriodicInfo(memberId, borrowerId, connection);
			 }
			 else
			 {
				 int size = borrowerIds.size();
				 for (int i=0;i<size;i++)
				 {
					 approvePeriodicInfo(memberId, (String) borrowerIds.get(i), connection);
				 }
			 }
			 connection.commit();
		 }
		 catch (DatabaseException exception)
		 {
			 try
			 {
			 	connection.rollback();
			 }
			 catch (SQLException sException)
			 {
				 throw new DatabaseException(sException.getMessage());
			 }

			 throw exception;
		 }
		 catch (SQLException sException)
		 {
			 throw new DatabaseException(sException.getMessage());
		 }
		 finally
		 {
			DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 1","Exited");
	 }


 /**
  * This method approves the periodic info for the given borrower id.
  */
 public void approvePeriodicInfo(String memberId, String borrowerId,
 			Connection connection)	throws DatabaseException,NoApplicationFoundException
 {
	Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Entered");

	CallableStatement stmt;
	ArrayList cgpans = new ArrayList();
	ApplicationProcessor apProcessor = new ApplicationProcessor();

	try
	{
		String bnkId = memberId.substring(0,4);
		String zneId = memberId.substring(4,8);
		String brnId = memberId.substring(8,12);
		ArrayList retList = apProcessor.getDtlForBIDMem(borrowerId, bnkId, zneId, brnId);
		apProcessor=null;
		cgpans = (ArrayList) retList.get(1);

		int size = cgpans.size();
		int status = 0;
		for (int i=0;i<size;i++)
		{
			String inCgpan = (String) cgpans.get(i);
			
			/***************Updating Disbursement Details**************************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating Dis for " + inCgpan);
			stmt = connection.prepareCall("{?=call funcDEDBRDetail(?,?)}");
			stmt.setString(2, inCgpan);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update Dis Successfull for " + inCgpan);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error Updating Dis for " + inCgpan);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;
		/********************Updating Outstanding Details******************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating OS for " + inCgpan);
			stmt = connection.prepareCall("{?=call funcDEUpdOutstanding(?,?)}");
			stmt.setString(2, inCgpan);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update OS Successfull for " + inCgpan);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error Updating OS for " + inCgpan);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;



			/******************Updating Repayment Details*******************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating Repayment for " + inCgpan);
			stmt = connection.prepareCall("{?=call funcDERepaymentDtl(?,?)}");
			stmt.setString(2, inCgpan);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update Repayment Successfull for " + inCgpan);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error Updating Repayment for " + inCgpan);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;

			/******************Updating Repayment Schedule Details*******************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating Repayment Schedule for " + inCgpan);
			stmt = connection.prepareCall("{?=call funcDERepSchedule(?,?)}");
			stmt.setString(2, inCgpan);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update Repayment Schedule Successfull for " + inCgpan);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error Updating Repayment Schedule for " + inCgpan);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;			
		}

		/**************Updating NPA Details*************************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating NPA for " + borrowerId);
			stmt = connection.prepareCall("{?=call funcDENPADetail(?,?)}");
			stmt.setString(2, borrowerId);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update NPA Successfull for " + borrowerId);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error NPA Repayment for " + borrowerId);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;

			/*************Updating Recovery Details*********************/

			Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Updating Recovery for " + borrowerId);
			stmt = connection.prepareCall("{?=call funcDERecoveryDtl(?,?)}");
			stmt.setString(2, borrowerId);
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			stmt.execute();
			status = stmt.getInt(1);
			if (status == Constants.FUNCTION_SUCCESS)
			{
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Update Recovery Successfull for " + borrowerId);
			}
			else if (status == Constants.FUNCTION_FAILURE)
			{
				String error = stmt.getString(3);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Error Recovery Repayment for " + borrowerId);
				Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","error message from sp " + error);
				throw new DatabaseException(error);
			}
			stmt.close();
			stmt=null;
	}
	catch (SQLException exception)
	{
		Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","exception " + exception.getMessage());
		throw new DatabaseException(exception.getMessage());
	}
	Log.log(Log.INFO,"GMDAO","approvePeriodicInfo 2","Exited");
 }

 /**
  * This method approves the modified borrower details for the given borrower id.
  */
 public void approveBorrowerDetails(int ssiRefNo) throws DatabaseException
 {
	Log.log(Log.INFO,"GMDAO","approveBorrowerDetails","Entered");
	Connection connection = null;
	CallableStatement stmt;

	try
	{
		connection = DBConnection.getConnection();
		stmt = connection.prepareCall("{?=call funcDEBorrowerDetail(?,?)}");

		stmt.registerOutParameter(1, java.sql.Types.INTEGER);
		stmt.setInt(2, ssiRefNo);
		stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
		stmt.execute();

		int status = stmt.getInt(1);
		if (status == Constants.FUNCTION_FAILURE)
		{
			String err = stmt.getString(3);
			Log.log(Log.INFO,"GMDAO","approveBorrowerDetails","error from funcdeborrowerdetail " + err);
			stmt.close();
			stmt=null;
			throw new DatabaseException(err);
		}
		stmt.close();
		stmt=null;
	}
	catch (SQLException exception)
	{
		Log.log(Log.INFO,"GMDAO","approveBorrowerDetails","exception " + exception.getMessage());
		throw new DatabaseException(exception.getMessage());
	}
	finally
	{
		DBConnection.freeConnection(connection);
	}
	Log.log(Log.INFO,"GMDAO","approveBorrowerDetails","Exited");
 }


 public ArrayList getCgpanMapping(String memberId) throws DatabaseException
  {
	 Log.log(Log.INFO,"GMDAO","getCgpanMapping","Entered");
	 Connection connection = null;
	 CallableStatement stmt;

	 ArrayList cgpanMapping = new ArrayList();
	 ArrayList mapping = new ArrayList();

	 ResultSet cgpans;

	 try
	 {
		 connection = DBConnection.getConnection();
		 Log.log(Log.INFO,"GMDAO","getCgpanMapping","Connection got");
		 stmt = connection.prepareCall("{?=call packGetAllCgpansForMember.funcGetAllCgpansForMember(?,?,?,?,?)}");

		 stmt.registerOutParameter(1, java.sql.Types.INTEGER);
		 String bnkId = memberId.substring(0,4);
		 String zneId = memberId.substring(4,8);
		 String brnId = memberId.substring(8,12);
		 stmt.setString(2, bnkId);
		 stmt.setString(3, zneId);
		 stmt.setString(4, brnId);
		 stmt.registerOutParameter(5, Constants.CURSOR);
		 stmt.registerOutParameter(6, java.sql.Types.VARCHAR);
		 stmt.execute();

		 int status = stmt.getInt(1);
		 if (status == Constants.FUNCTION_FAILURE)
		 {
			 String err = stmt.getString(6);
			 Log.log(Log.INFO,"GMDAO","getCgpanMapping","error from funcGetAllCgpansForMember " + err);
			 stmt.close();
			 stmt=null;
			 throw new DatabaseException(err);
		 }
		 cgpans=(ResultSet) stmt.getObject(5);
		 while (cgpans.next())
		 {
			 mapping=new ArrayList();
			 String newCgpan = cgpans.getString(1);
			 String oldCgpan = cgpans.getString(2);
			 mapping.add(oldCgpan);
			 mapping.add(newCgpan);
			 cgpanMapping.add(mapping);
			 mapping=null;
		 }
		 cgpans.close();
		 cgpans=null;
		 stmt.close();
		 stmt=null;
	 }
	 catch (SQLException exception)
	 {
		 Log.log(Log.INFO,"GMDAO","getCgpanMapping","exception " + exception.getMessage());
		 throw new DatabaseException(exception.getMessage());
	 }
	 finally
	 {
		 DBConnection.freeConnection(connection);
	 }
	 Log.log(Log.INFO,"GMDAO","getCgpanMapping","Exited");
	 return cgpanMapping;
  }

  /**
   * This method returns the recovery details for the given borrower id.
   * It returns an Arraylist which contains 2 elements.
   *		The first element is an ArrayList of Recovery objects with the old values.
   *		The next element is an ArrayList of Recovery objects with the modified values.
   */
   public ArrayList getRecoveryForApproval(String borrowerId) throws DatabaseException
   {
		Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","Entered");

		Connection connection=DBConnection.getConnection();
//		PeriodicInfo oldPeriodicInfo = new PeriodicInfo();
//		PeriodicInfo newPeriodicInfo = new PeriodicInfo();
		Recovery oldRecovery = new Recovery();
		Recovery newRecovery = new Recovery();
		ArrayList oldRecDetails = new ArrayList();
		ArrayList newRecDetails = new ArrayList();
//		ArrayList oldInfos = new ArrayList();
//		ArrayList newInfos = new ArrayList();
		ArrayList returnDetails = new ArrayList();
		CallableStatement stmt;
		ResultSet oldRecSet;
		ResultSet newRecSet;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

		try
		{
		  stmt=connection.prepareCall("{?=call packGetAllModifiedRecovery.funcGetAllModifiedRecovery(?,?,?,?)}");

		  Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","getting recovery details for " + borrowerId);
		  stmt.setString(2, borrowerId);
		  stmt.registerOutParameter(1, java.sql.Types.INTEGER);
		  stmt.registerOutParameter(3, Constants.CURSOR);
		  stmt.registerOutParameter(4, Constants.CURSOR);
		  stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

		  stmt.execute();
		  int status=stmt.getInt(1);
		  if (status==Constants.FUNCTION_SUCCESS)
		  {
			  newRecSet = (ResultSet) stmt.getObject(3);
			  oldRecSet = (ResultSet) stmt.getObject(4);

			  while (newRecSet.next())
			  {
				  newRecovery = new Recovery();
				  newRecovery.setCgbid(borrowerId);
				  newRecovery.setRecoveryNo(newRecSet.getString(1));
				  newRecovery.setAmountRecovered(newRecSet.getDouble(3));
				  newRecovery.setDateOfRecovery(newRecSet.getDate(4));
				  newRecovery.setLegalCharges(newRecSet.getDouble(5));
				  newRecovery.setIsRecoveryByOTS(newRecSet.getString(6));
				  newRecovery.setIsRecoveryBySaleOfAsset(newRecSet.getString(7));
				  newRecovery.setDetailsOfAssetSold(newRecSet.getString(8));
				  newRecovery.setRemarks(newRecSet.getString(9));

				  newRecDetails.add(newRecovery);
				  newRecovery=null;
			  }
			  newRecSet.close();
			  newRecSet=null;
			  int newSize=newRecDetails.size();

			  while (oldRecSet.next())
			  {
				  oldRecovery = new Recovery();
				  oldRecovery.setCgbid(borrowerId);
				  String tempId = oldRecSet.getString(1);
				  Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","old recovery id " + tempId);
				  for (int i=0;i<newSize;i++)
				  {
					  Recovery temp = new Recovery();
					  temp = (Recovery) newRecDetails.get(i);
					  if (temp.getRecoveryNo().equals(tempId))
					  {
						  Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","found match " + temp.getRecoveryNo());
						  oldRecovery.setRecoveryNo(tempId);
						  oldRecovery.setAmountRecovered(oldRecSet.getDouble(3));
						  oldRecovery.setDateOfRecovery(oldRecSet.getDate(4));
						  oldRecovery.setLegalCharges(oldRecSet.getDouble(5));
						  oldRecovery.setIsRecoveryByOTS(oldRecSet.getString(6));
						  oldRecovery.setIsRecoveryBySaleOfAsset(oldRecSet.getString(7));
						  oldRecovery.setDetailsOfAssetSold(oldRecSet.getString(8));
						  oldRecovery.setRemarks(oldRecSet.getString(9));

						  oldRecDetails.add(oldRecovery);
						  break;
					  }
					  temp=null;
				  }
				  oldRecovery=null;
			  }
			  oldRecSet.close();
			  oldRecSet=null;

			  stmt.close();
			  stmt=null;

			  returnDetails.add(oldRecDetails);
			  returnDetails.add(newRecDetails);
		  }
		  else
		  {
			  String err = stmt.getString(5);
			  Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","error getting reecovery detials for " + borrowerId);
			  stmt.close();
			  stmt=null;
			  throw new DatabaseException(err);
		  }
		}
		catch (SQLException sqlException)
		{
			  Log.log(Log.ERROR,"RIDAO","getRecoveryForApproval",sqlException.getMessage());
			  Log.logException(sqlException);
			  throw new DatabaseException(sqlException.getMessage());
		}
		finally
		{
			  DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","getRecoveryForApproval","Exited");

		return returnDetails;
   }

   /**
	* This method returns the repayment schedule details for the given list of cgpans.
	* It returns an Arraylist which contains 2 elements.
	*		The first element is a RepaymentSchedule object with the old values.
	*		The next element is a RepaymentSchedule object with the modified values.
	*/
   public ArrayList getRepayScheduleForApproval(ArrayList cgpanList) throws DatabaseException
   {
		Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","Entered");

		Connection connection=DBConnection.getConnection();
		RepaymentSchedule oldRepaySch = new RepaymentSchedule();
		RepaymentSchedule newRepaySch = new RepaymentSchedule();
		ArrayList oldRepaySchDtls = new ArrayList();
		ArrayList newRepaySchDtls = new ArrayList();
		ArrayList returnDetails = new ArrayList();
		CallableStatement stmt;
		ResultSet oldRepaySchSet;
		ResultSet newRepaySchSet;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

		try
		{
			  int size = cgpanList.size();
		   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","size " + size);
			  for (int i=0;i<size;i++)
			  {
			   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","i " + i);
				  String cgpan = (String) cgpanList.get(i);
//				  oldRepaySch.setCgpan(cgpan);
//				  newRepaySch.setCgpan(cgpan);
				  String type = cgpan.substring(cgpan.length()-2, cgpan.length()-1);
				  String date = "";
				  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","getting dis detials for " + cgpan);

				  stmt=connection.prepareCall("{?=call packGetAllModifiedRepaySch.funcGetAllModifiedRepaySch(?,?,?,?)}");

				  stmt.setString(2, cgpan);
				  stmt.registerOutParameter(1, java.sql.Types.INTEGER);
				  stmt.registerOutParameter(3, Constants.CURSOR);
				  stmt.registerOutParameter(4, Constants.CURSOR);
				  stmt.registerOutParameter(5, java.sql.Types.VARCHAR);

				  stmt.execute();
				  int status=stmt.getInt(1);
				  if (status==0)
				  {
				   newRepaySchSet = (ResultSet) stmt.getObject(3);
				   oldRepaySchSet = (ResultSet) stmt.getObject(4);
					  while (newRepaySchSet.next())
					  {
						  newRepaySch = new RepaymentSchedule();
						  newRepaySch.setCgpan(cgpan);
						  newRepaySch.setMoratorium(newRepaySchSet.getInt(3));
						  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","new moratorium " + newRepaySch.getMoratorium());
						  newRepaySch.setFirstInstallmentDueDate(newRepaySchSet.getDate(4));
						  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","new first installment due date " + newRepaySch.getFirstInstallmentDueDate());
						  newRepaySch.setPeriodicity(newRepaySchSet.getString(5));
						  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","new periodicity " + newRepaySch.getPeriodicity());
						  newRepaySch.setNoOfInstallment(newRepaySchSet.getInt(6));
						  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","new no of installments " + newRepaySch.getNoOfInstallment());
						   
						  newRepaySchDtls.add(newRepaySch);
						  newRepaySch=null;
					  }
				   newRepaySchSet.close();
				   newRepaySchSet=null;

				   int newSize=newRepaySchDtls.size();

					  while (oldRepaySchSet.next())
					  {
						  oldRepaySch = new RepaymentSchedule();
						   						   
						   String tempCgpan = oldRepaySchSet.getString(2);
						   for (int j=0;j<newSize;j++)
						   {
							   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","entering " + j);
							   RepaymentSchedule temp = new RepaymentSchedule();
							   temp = (RepaymentSchedule) newRepaySchDtls.get(j);
							   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","searching old for new cgpan " + temp.getCgpan());
							   if (temp.getCgpan().equals(tempCgpan))
							   {
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","founc match ");
								   oldRepaySch.setCgpan(tempCgpan);
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","old cgpan " + oldRepaySch.getCgpan());
								   oldRepaySch.setMoratorium(oldRepaySchSet.getInt(3));
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","old moratorium " + oldRepaySch.getMoratorium());
								   oldRepaySch.setFirstInstallmentDueDate(oldRepaySchSet.getDate(4));
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","old first installment due date " + oldRepaySch.getFirstInstallmentDueDate());
								   oldRepaySch.setPeriodicity(oldRepaySchSet.getString(5));
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","old periodicity " + oldRepaySch.getPeriodicity());
								   oldRepaySch.setNoOfInstallment(oldRepaySchSet.getInt(6));
								   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","old no of installments " + oldRepaySch.getNoOfInstallment());
								    
								   oldRepaySchDtls.add(oldRepaySch);
							   }
							   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","setting null ");
							   temp=null;
							   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after setting null ");
						  }
					  }
				   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","b4 rs close ");
				   oldRepaySchSet.close();
				   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after rs close ");
				   oldRepaySchSet=null;
				   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after rs null ");

					  stmt.close();
				   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after stmt close ");
					  stmt=null;
				   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after stmt null "); 
				  }
				  else
				  {
					  String err = stmt.getString(5);
					  Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","error getting dis detials for " + cgpan);
					  stmt.close();
					  stmt=null;
					  throw new DatabaseException(err);
				  }
			  }
		   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","b4 adding old ");
			  returnDetails.add(oldRepaySchDtls);
		   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after adding old ");
			  returnDetails.add(newRepaySchDtls);
		   Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","after adding new ");
		}
		catch (SQLException sqlException)
		{
			  Log.log(Log.ERROR,"RIDAO","getRepayScheduleForApproval",sqlException.getMessage());
			  Log.logException(sqlException);
			  throw new DatabaseException(sqlException.getMessage());
		}
		finally
		{
			  DBConnection.freeConnection(connection);
		}

		Log.log(Log.INFO,"GMDAO","getRepayScheduleForApproval","Exited");

		return returnDetails;
   }


   /**
    * This method retrieves all the CGPANs for the given borrower  id.
    * @param BorrowerId
    * @return Array
    * @roseuid 39B23A7601F4
    */
 /*  public Array getCGPANs(String BorrowerId) {
    return null;
   }

   /**
    * This method retrieves all the member ids belonging to the same operating office
    * and returns an array of string.
    * @param MemberId
    * @return Array
    * @roseuid 39B23D00004E
    */
 /*  public Array getOtherMemberIds(String MemberId) {
    return null;
   }*/

/*private Connection getDbConnection() throws DatabaseException
	   {
		  String driver="oracle.jdbc.driver.OracleDriver";
		  String sourceURL="jdbc:oracle:thin:@cprbbfs479:1521:satyam";
		  Connection connection;

		  try
		  {
			  Class.forName(driver);
			  connection=DriverManager.getConnection(sourceURL,"cgtsiadmin","cgtsiadmin");
		  }
		  catch (Exception exception)
		  {
				 throw new DatabaseException(exception.getMessage());
		  }

		  return connection;
	   }

public static void main(String args[]) throws DatabaseException{
	  GMDAO gmDao = new GMDAO();

	  ArrayList ar = gmDao.getDisbursementDetails("CG200300001TC",1);
	  PeriodicInfo pInfo = (PeriodicInfo)ar.get(0);
	  ArrayList d = pInfo.getDisbursementDetails() ;
	  System.out.println("Disbursement size : "+d.size());
	  for(int i=0;i<d.size();++i) {
		Disbursement d1 = (Disbursement)d.get(i);
		ArrayList da = d1.getDisbursementAmounts() ;
		System.out.println("Disbursement Amount size : "+da.size());
		for(int j=0;j<da.size();++j) {
			DisbursementAmount dam = (DisbursementAmount)da.get(j);
			System.out.println("CGPAN : " + dam.getCgpan());
			System.out.println("Amount : " + dam.getDisbursementAmount());
			System.out.println("Date : " + dam.getDisbursementDate());
			System.out.println("Final Disbursement : " + dam.getFinalDisbursement());
		}
	  }
	} */

	/**
	 * This method fetches borrower id for the borrower name .
	 * @return String
	 */
	public ArrayList  getBorrowerIdForBorrowerName(String borrowerName,String memberId) throws DatabaseException {
		 Log.log(Log.INFO,"GMDAO","getBorrowerIdForBorrowerName","Entered");
		 Connection connection = DBConnection.getConnection();
		 CallableStatement getBorrowerIdForBorrowerNameStmt = null;
		 ArrayList bids = new ArrayList();
		 
		String bankId = memberId.substring(0,4);		 
		String zoneId = memberId.substring(4,8);
		String branchId = memberId.substring(8,12);

		 try {
			 getBorrowerIdForBorrowerNameStmt = connection.prepareCall("{? = call packGetBIDsforSSIName.funcGetBIDsForSSIName(?,?,?,?,?,?)}");

			 getBorrowerIdForBorrowerNameStmt.registerOutParameter(1,Types.INTEGER);
			 getBorrowerIdForBorrowerNameStmt.setString(2,borrowerName);
			 getBorrowerIdForBorrowerNameStmt.setString(3,bankId);
			getBorrowerIdForBorrowerNameStmt.setString(4,zoneId);
			getBorrowerIdForBorrowerNameStmt.setString(5,branchId);
			 getBorrowerIdForBorrowerNameStmt.registerOutParameter(6,Constants.CURSOR);
			 getBorrowerIdForBorrowerNameStmt.registerOutParameter(7,java.sql.Types.VARCHAR);

			 getBorrowerIdForBorrowerNameStmt.execute();
			 int updateStatus = getBorrowerIdForBorrowerNameStmt.getInt(1);

			 String error = 	getBorrowerIdForBorrowerNameStmt.getString(7);

			 if(updateStatus == Constants.FUNCTION_FAILURE ){
				 getBorrowerIdForBorrowerNameStmt.close();
				 getBorrowerIdForBorrowerNameStmt = null;
				 Log.log(Log.ERROR,"GMDAO","getBorrowerIdForBorrowerName","Exception "+error);
				 connection.rollback() ;
				 throw new DatabaseException(error);
			 }
			 else{
			 	
			 	ResultSet borrowerIds = (ResultSet)	getBorrowerIdForBorrowerNameStmt.getObject(6);
			 	while(borrowerIds.next())
			 	{
			 		String bid = borrowerIds.getString(1);
			 		String unitName = borrowerIds.getString(2);
			 		String bidUnitName = bid + "(" + unitName + ")";  
			 		
			 		if(bid!=null && !bid.equals(""))
			 		{
						bids.add(bidUnitName);
			 		}
			 	}
			 	 
				borrowerIds.close();
				borrowerIds=null;
			 }

			 getBorrowerIdForBorrowerNameStmt.close();
			 getBorrowerIdForBorrowerNameStmt = null;
			 connection.commit() ;
		}
		catch(Exception exception){
		 Log.log(Log.ERROR,"GMDAO","getBorrowerIdForBorrowerName",exception.getMessage());
		 Log.logException(exception);
		 try
		 {
			 connection.rollback();
		 }
		 catch (SQLException ignore){}

		 throw new DatabaseException(exception.getMessage());

		}finally{
		 DBConnection.freeConnection(connection);
		}
		 Log.log(Log.INFO,"GMDAO","getBorrowerIdForBorrowerName","Exited");
		return bids ;
	}
	
	
	public ArrayList getRepaymentDetailsForReport(String id, int type)
													 throws DatabaseException {

		 Log.log(Log.INFO,"GMDAO","get Repayment Details","Entered");

		 ArrayList periodicInfos = new ArrayList();

		 Connection connection = DBConnection.getConnection() ;
		 CallableStatement getRepaymentDetailStmt = null ;
		 ResultSet resultSet = null;

		 try {

			 String exception = "" ;
			 String functionName=null;

			 if(type==GMConstants.TYPE_ZERO)
			 {
				 functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBID(?,?,?)}"	;
			 }
			 else if(type==GMConstants.TYPE_ONE)
			 {
				 functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforCGPAN(?,?,?)}";
			 }else if(type==GMConstants.TYPE_TWO){
				 functionName="{?=call packGetDtlsforRepayment.funcGetDtlsforBorrower(?,?,?)}";
			 }

			 getRepaymentDetailStmt = connection.prepareCall(functionName);
			 getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			 getRepaymentDetailStmt.setString(2,id);
			 getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
			 getRepaymentDetailStmt.registerOutParameter(4,
												 java.sql.Types.VARCHAR) ;

			 getRepaymentDetailStmt.executeQuery() ;

			 exception = getRepaymentDetailStmt.getString(4) ;
			 Log.log(Log.DEBUG,"GMDAO","repayment detail","exception "+exception);
			 int error=getRepaymentDetailStmt.getInt(1);
			 Log.log(Log.DEBUG,"GMDAO","repayment detail","errorCode "+error);	
			 if(error==Constants.FUNCTION_FAILURE)
			 {
				 getRepaymentDetailStmt.close();
				 getRepaymentDetailStmt=null;
				 connection.rollback();
				 Log.log(Log.ERROR,"GMDAO","getRepaymentdetail","error in SP "+exception);
				 throw new DatabaseException(exception);				
			 }
			 Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","Before ResultSet assign");
			 resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
			 Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","resultSet assigned");
			
			 PeriodicInfo periodicInfo=new PeriodicInfo();
			 Repayment repayment = null;

			 ArrayList listOfRepayment = new ArrayList();

			 boolean firstTime=true;
			 String tcCgpan = null;
			 int len = 0;
			 String applType = null;
			
			 while (resultSet.next())
			 {
				 Log.log(Log.DEBUG,"GMDAO","getRepaymentdetail","Inside ResultSet");
				 repayment = new Repayment();
				 tcCgpan = resultSet.getString(2);
				 len = tcCgpan.length() ;
				 applType = tcCgpan.substring(len-2,len-1);
				 if(applType.equalsIgnoreCase(GMConstants.CGPAN_TC))
				 {
					 if(firstTime)
					 {
						 periodicInfo.setBorrowerId(resultSet.getString(1));
						 Log.log(Log.DEBUG,"getRepaymentDetails for Borrower","Borrower ID"," : "+periodicInfo.getBorrowerId());

						 periodicInfo.setBorrowerName(resultSet.getString(3));
						 Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","Borrower Name"," : "+periodicInfo.getBorrowerName());
						 firstTime=false;
					 }

					 repayment.setCgpan(tcCgpan);
					 Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","CGPAN ",": "+repayment.getCgpan());
					 repayment.setScheme(resultSet.getString(4));
					 Log.log(Log.DEBUG,"getRepaymentDetailsfor Borrower:","Scheme"," : "+repayment.getScheme());

					 listOfRepayment.add(repayment);
				 }
			 }

			 Log.log(Log.DEBUG,"getRepaymentDetails for Borrower:","size of RepaymentObj"," : "+listOfRepayment.size());

			 resultSet.close();
			 resultSet=null;

			 getRepaymentDetailStmt = null;

			 functionName="{?=call packGetRepaymentDtlsForReport.funcGetRepaymentDtlForReport(?,?,?)}"	;
			 getRepaymentDetailStmt = connection.prepareCall(functionName);
			 //System.out.println("size of listOfRepayment= "+listOfRepayment.size());

			 String cgpan="";
			 int size = listOfRepayment.size();
			 for(int i=0;i<size;++i) {
				 ArrayList listOfRepaymentAmount = new ArrayList();
				 repayment = (Repayment)listOfRepayment.get(i);
				 cgpan=repayment.getCgpan();
				 Log.log(Log.DEBUG,"getRepaymentDetails for cgpan:","cgpan"," : "+i+" "+cgpan);
				 getRepaymentDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				 getRepaymentDetailStmt.setString(2,cgpan);

				 getRepaymentDetailStmt.registerOutParameter(3,Constants.CURSOR);
				 getRepaymentDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

				 getRepaymentDetailStmt.execute() ;

				 exception = getRepaymentDetailStmt.getString(4) ;

				 error=getRepaymentDetailStmt.getInt(1);
				 if(error==Constants.FUNCTION_FAILURE)
				 {
					 getRepaymentDetailStmt.close();
					 getRepaymentDetailStmt=null;
					 Log.log(Log.ERROR,"getRepaymentDetails for cgpan:","Exception ",exception);
					 connection.rollback();
					 throw new DatabaseException(exception);
				 }
				 resultSet = (ResultSet)getRepaymentDetailStmt.getObject(3) ;
				 RepaymentAmount repaymentAmount = null;
				 while (resultSet.next())
				 {
					 repaymentAmount = new RepaymentAmount();

					 repaymentAmount.setCgpan(cgpan);
					 Log.log(Log.DEBUG,"GMDAO","RepaymentAmount","Cgpan "+cgpan);

					 repaymentAmount.setRepayId(resultSet.getString(1));
					 Log.log(Log.DEBUG,"GMDAO","RepaymentAmount","RepaymentId "+repaymentAmount.getRepayId());

					 repaymentAmount.setRepaymentAmount(resultSet.getDouble(2));
					 Log.log(Log.DEBUG,"rep Amt: ","rpAmount","--"+repaymentAmount.getRepaymentAmount());

					 repaymentAmount.setRepaymentDate(resultSet.getDate(3));
					 Log.log(Log.DEBUG,"rep date:","date"," "+repaymentAmount.getRepaymentDate());

					 listOfRepaymentAmount.add(repaymentAmount);
					 Log.log(Log.DEBUG,"************","***********","****************");
				 }
				 repayment.setRepaymentAmounts(listOfRepaymentAmount);
				 resultSet.close();
				 resultSet=null;
			 }
			 periodicInfo.setRepaymentDetails(listOfRepayment);

			 periodicInfos.add(periodicInfo);

			 getRepaymentDetailStmt.close();
			 getRepaymentDetailStmt=null;
			
			 connection.commit();

		 }catch (Exception exception) {
			 Log.logException(exception);
			 try
			 {
				 connection.rollback();
			 }
			 catch (SQLException ignore){}

			 throw new DatabaseException(exception.getMessage()) ;
		 }
		 finally
		 {
			 DBConnection.freeConnection(connection);
		 }

		 Log.log(Log.INFO,"GMDAO","get Repayment Details","Exited");
		 return  periodicInfos;
	 }



	public ArrayList getDisbursementDetailsForReport(String id, int type)
													 throws DatabaseException {

		 Log.log(Log.INFO,"GMDAO","getDisbursementDetails","Entered");
//			 ArrayList listOfDisbursement = new ArrayList();
//		 ArrayList listOfDisbursementAmount = new ArrayList();


		 ArrayList periodicInfos = new ArrayList();

		 Connection connection = DBConnection.getConnection() ;

		 CallableStatement getDisbursementDetailStmt = null ;
		 //Disbursement disbursement = null;
		 ResultSet resultSet = null;


		 try {
			 String exception = "" ;

			 String functionName=null;

			 if(type==GMConstants.TYPE_ZERO)
			 {
				 functionName="{?=call packGetDtlsforDBR.funcGetDtlsForBid(?,?,?)}"	;
			 }
			 else if (type==GMConstants.TYPE_ONE)
			 {
				 functionName="{?=call packGetDtlsforDBR.funcGetDtlsForCGPAN(?,?,?)}";
			 }else if (type==GMConstants.TYPE_TWO)
			 {
				 functionName="{?=call packGetDtlsforDBR.funcGetDtlsForBorrower(?,?,?)}";
			 }


			 getDisbursementDetailStmt = connection.prepareCall(functionName);
			 getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			 getDisbursementDetailStmt.setString(2,id);
			 getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
			 getDisbursementDetailStmt.registerOutParameter(4,
												 java.sql.Types.VARCHAR) ;

			 getDisbursementDetailStmt.executeQuery() ;

			 exception = getDisbursementDetailStmt.getString(4) ;

			 int error=getDisbursementDetailStmt.getInt(1);

			 if(error==Constants.FUNCTION_FAILURE)
			 {
				 getDisbursementDetailStmt.close();
				 getDisbursementDetailStmt=null;
				 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Exception :"+exception);
				 connection.rollback();
				 throw new DatabaseException(exception);
			 }
			 resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;

			 PeriodicInfo periodicInfo=new PeriodicInfo();
			 Disbursement disbursement = null;
			 ArrayList listOfDisbursement = new ArrayList();
			 boolean firstTime=true;

			 while (resultSet.next())
			 {
				 disbursement = new Disbursement();

				 if(firstTime)
				 {
					 periodicInfo.setBorrowerId(resultSet.getString(1));
					 periodicInfo.setBorrowerName(resultSet.getString(4));
					 firstTime=false;
				 }

				 disbursement.setCgpan(resultSet.getString(2));
			 //	System.out.println("getDisbursementDetails:CGPAN : "+resultSet.getString(2));
				 disbursement.setScheme(resultSet.getString(3));
			 //	System.out.println("getDisbursementDetails:Scheme : "+resultSet.getString(4));
				 disbursement.setSanctionedAmount(resultSet.getDouble(5));
				 //System.out.println("getDisbursementDetails:San Amt: "+resultSet.getString(5));

				 listOfDisbursement.add(disbursement);
			 }

			 resultSet.close();
			 resultSet=null;

			 getDisbursementDetailStmt.close();
			 getDisbursementDetailStmt = null;


			 String cgpan = null;

			 int disbSize = listOfDisbursement.size();
			 functionName="{?=call packGetPIDBRDtlsCGPANForReport.funcDBRDetailsForCGPANReport(?,?,?)}"	;
			 getDisbursementDetailStmt = connection.prepareCall(functionName);
			 for(int i=0; i<disbSize; ++i) {
				 ArrayList listOfDisbursementAmount = new ArrayList();
				 disbursement = (Disbursement)listOfDisbursement.get(i);
				 cgpan=disbursement.getCgpan();
				 if(cgpan == null)
				 {
					 continue;
				 }
				 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Cgpan"+cgpan);
				 getDisbursementDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				 getDisbursementDetailStmt.setString(2,cgpan);

				 getDisbursementDetailStmt.registerOutParameter(3,Constants.CURSOR);
				 getDisbursementDetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

				 getDisbursementDetailStmt.execute() ;

				 exception = getDisbursementDetailStmt.getString(4) ;

				 error=getDisbursementDetailStmt.getInt(1);
				 if(error==Constants.FUNCTION_FAILURE)
				 {
					 getDisbursementDetailStmt.close();
					 getDisbursementDetailStmt=null;
					 Log.log(Log.ERROR,"GMDAO","getDisbursementDetails","Exception" + exception);
					 connection.rollback();
					 throw new DatabaseException(exception);
				 }
				 resultSet = (ResultSet)getDisbursementDetailStmt.getObject(3) ;
				 DisbursementAmount disbursementAmount = null;

				 while (resultSet.next())
				 {
					 disbursementAmount = new DisbursementAmount();

					 disbursementAmount.setCgpan(cgpan);

					 disbursementAmount.setDisbursementId(resultSet.getString(1));
					 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb Id" +
						 disbursementAmount.getDisbursementId());

					 disbursementAmount.setDisbursementAmount(resultSet.getDouble(2));
					 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb Amt" +
					 disbursementAmount.getDisbursementAmount());

					 disbursementAmount.setDisbursementDate(DateHelper.sqlToUtilDate(resultSet.getDate(3)));
					 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","disb date" +
						 disbursementAmount.getDisbursementDate());
					 disbursementAmount.setFinalDisbursement(resultSet.getString(4));
					 Log.log(Log.DEBUG,"GMDAO","getDisbursementDetails","Fin disb " +
						 disbursementAmount.getFinalDisbursement());

					 listOfDisbursementAmount.add(disbursementAmount);
				 }

				 disbursement.setDisbursementAmounts(listOfDisbursementAmount);
				 resultSet.close();
				 resultSet=null;

			 }

			 getDisbursementDetailStmt.close();
			 getDisbursementDetailStmt=null;

			 periodicInfo.setDisbursementDetails(listOfDisbursement);
			 periodicInfos.add(periodicInfo);
			
			 connection.commit() ;
			
		 }catch (Exception exception) {
			 Log.logException(exception);
			 try
			 {
				 connection.rollback();
			 }
			 catch (SQLException ignore){}

			 throw new DatabaseException(exception.getMessage()) ;
		 }
		 finally
		 {
			 DBConnection.freeConnection(connection);
		 }

		 Log.log(Log.INFO,"GMDAO","get disbursementDetails","Exited");
		 return  periodicInfos;
	   }


	public NPADetails getNPADetailsForReport(String borrowerId) throws DatabaseException{

		 Log.log(Log.INFO,"GMDAO","getNPADetails","Entered");

		 Connection connection = DBConnection.getConnection() ;
		 CallableStatement getNPADetailStmt = null ;
		 ResultSet resultSet = null;

		 NPADetails npaDetail = null;

		 try {
			 Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

			 getNPADetailStmt = connection.prepareCall(
			 "{?=call packGetNPADetailForReport.funcGetNPADetailForReport(?,?,?)}");
			 getNPADetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
			 getNPADetailStmt.setString(2, borrowerId) ;

			 getNPADetailStmt.registerOutParameter(3,
												 Constants.CURSOR) ;
			 getNPADetailStmt.registerOutParameter(4,
											 java.sql.Types.VARCHAR) ;

			 getNPADetailStmt.execute() ;
			
			 Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","test");

			 resultSet = (ResultSet)getNPADetailStmt.getObject(3) ;
			 int error = getNPADetailStmt.getInt(1) ;
			 String exception =  getNPADetailStmt.getString(4) ;

			 if(error == Constants.FUNCTION_SUCCESS){
				 Log.log(Log.DEBUG,"GMDAO","get NPA Details For Bid","Success");
			 }

			 if(error==Constants.FUNCTION_FAILURE)
			 {
				 getNPADetailStmt.close();
				 getNPADetailStmt=null;
				 Log.log(Log.ERROR,"GMDAO","get NPA Details For Bid","Exception "+exception);
				 connection.rollback();
				 throw new DatabaseException(exception);
			 }

			 LegalSuitDetail legalSuitDetail = null;
			 boolean isNpaAvailable = false;
			 while (resultSet.next())
			 {
				 Log.log(Log.DEBUG,"GMDAO","getNPADetails","inside result set isNpaAvailable = true");
				 isNpaAvailable = true;
				 npaDetail = new NPADetails();
				 legalSuitDetail = new LegalSuitDetail();

				 npaDetail.setNpaId(resultSet.getString(1)) ;
				 npaDetail.setCgbid(resultSet.getString(2)) ;
				 npaDetail.setNpaDate(resultSet.getDate(3)) ;
				 npaDetail.setWhetherNPAReported(resultSet.getString(4)) ;
				 npaDetail.setReportingDate(resultSet.getDate(6)) ;
				 npaDetail.setReference(resultSet.getString(7)) ;
				 npaDetail.setOsAmtOnNPA(resultSet.getDouble(8)) ;
				 npaDetail.setNpaReason(resultSet.getString(9));
				 npaDetail.setEffortsTaken(resultSet.getString(10));
				 Log.log(Log.DEBUG,"GMDAO","getNPADetails","Efforts "+npaDetail.getEffortsTaken());

				 npaDetail.setIsRecoveryInitiated(resultSet.getString(11));
				 npaDetail.setNoOfActions(resultSet.getInt(12));
				 npaDetail.setEffortsConclusionDate(resultSet.getDate(13));
				 npaDetail.setMliCommentOnFinPosition(resultSet.getString(15)) ;
				 npaDetail.setDetailsOfFinAssistance(resultSet.getString(16)) ;
				 npaDetail.setCreditSupport(resultSet.getString(17)) ;
				 npaDetail.setBankFacilityDetail(resultSet.getString(18)) ;
				 npaDetail.setWillfulDefaulter(resultSet.getString(19));
				 npaDetail.setPlaceUnderWatchList(resultSet.getString(20));
				 npaDetail.setRemarksOnNpa(resultSet.getString(24));
				 Log.log(Log.DEBUG,"GMDAO","getNPADetails","Remarks"+npaDetail.getRemarksOnNpa());

				 legalSuitDetail.setLegalSuiteNo(resultSet.getString(25));
				 legalSuitDetail.setCourtName(resultSet.getString(26));
				 legalSuitDetail.setForumName(resultSet.getString(27));
				 legalSuitDetail.setLocation(resultSet.getString(28));
				 legalSuitDetail.setDtOfFilingLegalSuit(resultSet.getDate(29));
				 legalSuitDetail.setAmountClaimed(resultSet.getDouble(30));
				 legalSuitDetail.setCurrentStatus(resultSet.getString(31));
				 legalSuitDetail.setRecoveryProceedingsConcluded(resultSet.getString(32));

				 npaDetail.setLegalSuitDetail(legalSuitDetail);
			 }
			 getNPADetailStmt.close();
			 getNPADetailStmt=null;

			 resultSet.close();
			 resultSet = null;

			 if(isNpaAvailable)
			 {
				 Log.log(Log.DEBUG,"GMDAO","getNPADetails","isNpaAvailable = true getRecoveryAxn");

				 getNPADetailStmt = connection.prepareCall(
						 "{?=call packGetRecoveryAxnForReport.funcGetRecoveryAxnForReport(?,?,?)}");

				 getNPADetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

				 getNPADetailStmt.setString(2, npaDetail.getNpaId()) ;

				 getNPADetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				 getNPADetailStmt.registerOutParameter(4,java.sql.Types.VARCHAR) ;

				 getNPADetailStmt.execute() ;

				 resultSet = (ResultSet)getNPADetailStmt.getObject(3) ;

				 int recerror = getNPADetailStmt.getInt(1) ;
				 String recexception =  getNPADetailStmt.getString(4) ;

				 if(recerror == Constants.FUNCTION_SUCCESS)
				 {
					 Log.log(Log.DEBUG,"GMDAO","get RecoveryActionDetails For Bid","Success");
				 }

				 if(recerror==Constants.FUNCTION_FAILURE)
				 {
					 getNPADetailStmt.close();
					 getNPADetailStmt=null;
					 Log.log(Log.ERROR,"GMDAO","get RecoveryAction Details For Bid","Exception "+recexception);
					 connection.rollback() ;
					 throw new DatabaseException(exception);
				 }

				 ArrayList recoProcs = new ArrayList();

				 while (resultSet.next())
				 {
					 Log.log(Log.DEBUG,"GMDAO","get NPA Details..getRecoveryActionDetailsForBid","Inside Result Set");
					 RecoveryProcedure recoProc = new RecoveryProcedure();
					 Log.log(Log.DEBUG,"GMDAO","Test","new R");
					 recoProc.setActionType(resultSet.getString(1));
					 Log.log(Log.DEBUG,"GMDAO","Test","1");
					 recoProc.setRadId(resultSet.getString(2));
					 Log.log(Log.DEBUG,"GMDAO","Test","2");
					 recoProc.setActionDetails(resultSet.getString(3));
					 Log.log(Log.DEBUG,"GMDAO","Test","3");
					 recoProc.setActionDate(resultSet.getDate(4));
					 Log.log(Log.DEBUG,"GMDAO","Test","4");
					 recoProc.setAttachmentName(resultSet.getString(5));
					 Log.log(Log.DEBUG,"GMDAO","Test","5");
					 recoProcs.add(recoProc);
					 Log.log(Log.DEBUG,"GMDAO","Test","one while");
				 }
				 Log.log(Log.DEBUG,"GMDAO","getRecoveryActionDetailsForBid","Size is "+recoProcs.size());
				 resultSet.close();
				 resultSet = null;

				 getNPADetailStmt.close();
				 getNPADetailStmt=null;
				 connection.commit() ;
				 npaDetail.setRecoveryProcedure(recoProcs);
			 }
		 }catch (Exception exception) {
			 try
			 {
				 connection.rollback();
			 }
			 catch (SQLException ignore){}

			 throw new DatabaseException(exception.getMessage()) ;
		 }finally{
			 DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","getNPADetails","Exited");

		 return npaDetail;

	}

	public ArrayList getRecoveryDetailsForReport(String borrowerId) throws
												DatabaseException {

			Connection connection = DBConnection.getConnection() ;
			CallableStatement getRecoveryDetailStmt = null ;
			ResultSet resultSet = null;
			Recovery recoveryDetail = null;
			ArrayList recoveryDetails = null;
			try {
				String exception = "" ;

				getRecoveryDetailStmt = connection.prepareCall(
							"{?=call packGetRecoveryDtlsForReport.funcGetREcoveryDtlsForReport(?,?,?)}");
				getRecoveryDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				getRecoveryDetailStmt.setString(2, borrowerId);
				getRecoveryDetailStmt.registerOutParameter(3,
									Constants.CURSOR) ;
				getRecoveryDetailStmt.registerOutParameter(4,
													java.sql.Types.VARCHAR) ;

				getRecoveryDetailStmt.execute() ;
				int error=getRecoveryDetailStmt.getInt(1);
				exception = getRecoveryDetailStmt.getString(4);

				if(error == Constants.FUNCTION_SUCCESS){
					Log.log(Log.DEBUG,"GMDAO","get Recovery Details For Bid","Success");
				}

				if(error==Constants.FUNCTION_FAILURE)
				{
					getRecoveryDetailStmt.close();
					getRecoveryDetailStmt=null;
					Log.log(Log.DEBUG,"GMDAO","getRecoveryDetailsForBid","Exception "+exception);
					connection.rollback() ;
					throw new DatabaseException(exception);
				}

				resultSet = (ResultSet)getRecoveryDetailStmt.getObject(3) ;

				recoveryDetails = new ArrayList();
				while (resultSet.next())
				{
					recoveryDetail = new Recovery();
					recoveryDetail.setCgbid(borrowerId) ;
					recoveryDetail.setRecoveryNo(resultSet.getString(1)) ;
					recoveryDetail.setAmountRecovered(resultSet.getDouble(2)) ;
					java.sql.Date sqldate =  resultSet.getDate(3);
//					System.out.println("sql date form dao"+sqldate);
					recoveryDetail.setDateOfRecovery(sqldate) ;
					recoveryDetail.setLegalCharges(resultSet.getDouble(4)) ;
					recoveryDetail.setIsRecoveryByOTS(resultSet.getString(5)) ;
					recoveryDetail.setIsRecoveryBySaleOfAsset(resultSet.getString(6)) ;
					recoveryDetail.setDetailsOfAssetSold(resultSet.getString(7)) ;
					recoveryDetail.setRemarks(resultSet.getString(8)) ;
					recoveryDetails.add(recoveryDetail);
				}
				resultSet.close();
				resultSet = null;
				getRecoveryDetailStmt.close();
				getRecoveryDetailStmt=null;
				
				connection.commit() ;

			}catch (Exception exception) {
				try
				{
					connection.rollback();
				}
				catch (SQLException ignore){}

				throw new DatabaseException(exception.getMessage()) ;
			}
			finally
			{
				DBConnection.freeConnection(connection);
			}
			return recoveryDetails;
	}
	


	public ArrayList getOutstandingDetailsForReport(String id, int type)
													  throws DatabaseException,
													   SQLException
	 {
		 Log.log(Log.INFO,"GMDAO","getOutstandingDetails","Entered");

		 OutstandingDetail outstandingDetail = null ;

		 PeriodicInfo periodicInfo=new PeriodicInfo();

		 ArrayList periodicInfos = new ArrayList();

		 ArrayList listOfOutstandingDetail = new ArrayList();

		 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","Id passed is "+id);

		 Connection connection = DBConnection.getConnection() ;

		 CallableStatement getOutstandingDetailStmt = null ;

		 ResultSet resultSet = null;

		 ResultSet cgpanResultSet = null;

		 CallableStatement getOutDetailForCgpanStmt = null ;

		 String panType = null;
		 int cgpanLength = 0;


		 String cgpan = null;
		 String appType = null;
		 int len = 0;
		 int size = 0;

		 try
		 {
			 String exception = "" ;

			 String functionName=null;

			 ArrayList outDtls = new ArrayList();

			 if(type==GMConstants.TYPE_ZERO )
			 {
				 functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforBID(?,?,?)}"	;
				 getOutstandingDetailStmt = connection.prepareCall(functionName);
				 getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				 getOutstandingDetailStmt.setString(2, id);
				 getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				 getOutstandingDetailStmt.registerOutParameter(4,
													 java.sql.Types.VARCHAR) ;

				 getOutstandingDetailStmt.execute() ;

				 exception = getOutstandingDetailStmt.getString(4) ;
				 int error=getOutstandingDetailStmt.getInt(1);

				 if(error == Constants.FUNCTION_SUCCESS)
				 {
					 Log.log(Log.DEBUG,"GMDAO","GET OutstandingForBID","Success");
				 }

				 if(error==Constants.FUNCTION_FAILURE)
				 {
					 getOutstandingDetailStmt.close();
					 getOutstandingDetailStmt=null;
					 Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForBID","Exception "+exception);
					 connection.rollback();
					 throw new DatabaseException(exception);
				 }
				 // Reading the resultset
				 resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				 boolean firstTime=true;

				 String cgpan1 = null;

				 while (resultSet.next())
				 {
					 if(firstTime)
					 {
						 periodicInfo.setBorrowerId(resultSet.getString(1));
						 periodicInfo.setBorrowerName(resultSet.getString(4));
						 firstTime=false;
					 }
					 cgpan1 = resultSet.getString(2);
					 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","cgpan from view : "+ cgpan1);
					 if(cgpan1 != null)
					 {
						 outstandingDetail = new OutstandingDetail();
						 outstandingDetail.setCgpan(cgpan1);
						 outstandingDetail.setScheme(resultSet.getString(3));
						 outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						 outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(6));
						 outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(7));
						 listOfOutstandingDetail.add(outstandingDetail);
					 }
					 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set View");
				 }
				 resultSet.close();
				 resultSet = null;

				 getOutstandingDetailStmt.close();
				 getOutstandingDetailStmt = null;

				 size = listOfOutstandingDetail.size();
				 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","size of out dtls : "+ size);

				 for(int i = 0; i<size; ++i)
				 {
					 OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
					 cgpan = outDtl.getCgpan() ;
					 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","inside for loop cgpan : "+ cgpan);

					 len = cgpan.length();

					 appType = cgpan.substring(len-2,len-1);

					 ArrayList outAmounts = new ArrayList();

					 if(appType.equalsIgnoreCase(GMConstants.CGPAN_TC))
					 {
						 getOutDetailForCgpanStmt = connection.prepareCall("{?= call packGetTCOutStandingForReport.funcTCOutStandingForReport(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 getOutDetailForCgpanStmt.setString(2, cgpan);
						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						 }

						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							 connection.rollback();
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();
							 outAmount.setCgpan(cgpan);
							 outAmount.setTcoId(cgpanResultSet.getString(1));
							 outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));

							 outAmounts.add(outAmount);
							 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set for Cgpan TC");
						 }

						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;
					 }
					 else if(appType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
							 appType.equalsIgnoreCase(GMConstants.CGPAN_RENEWAL))
					 {
						 getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStandingForReport.funcWCOutStandingForReport(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","Cgpan WC ");
						 getOutDetailForCgpanStmt.setString(2, cgpan);

						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GET WC Outstanding","Success");
						 }

						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GET WC Outstanding","Exception"+exception);
							 connection.rollback() ;
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();
							 outAmount.setCgpan(cgpan);

							 outAmount.setWcoId(cgpanResultSet.getString(1));
							 outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							 outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							 outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							 outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							 outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));
							 outAmounts.add(outAmount);
							 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","end of one Result Set for Cgpan WC");
						 }
						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;
					 }
					 outDtl.setOutstandingAmounts(outAmounts);
					 outDtls.add(outDtl);
				 }
				 periodicInfo.setOutstandingDetails(outDtls);
				 Log.log(Log.DEBUG,"GMDAO","getOutstandingDetails","OutDtls size "+outDtls.size());
				 periodicInfos.add(periodicInfo);
			 }
			 else if(type==GMConstants.TYPE_ONE)
			 {
				 functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforCGPAN(?,?,?)}";

				 getOutstandingDetailStmt = connection.prepareCall(functionName);

				 getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
				 getOutstandingDetailStmt.setString(2, id);
				 getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				 getOutstandingDetailStmt.registerOutParameter(4,
													 java.sql.Types.VARCHAR) ;

				 getOutstandingDetailStmt.execute() ;

				 exception = getOutstandingDetailStmt.getString(4) ;

				 int error=getOutstandingDetailStmt.getInt(1);

				 if(error == Constants.FUNCTION_SUCCESS)
				 {
					 Log.log(Log.DEBUG,"GMDAO","getOutstandinForCGPAN","Success");
				 }

				 if(error==Constants.FUNCTION_FAILURE)
				 {
					 getOutstandingDetailStmt.close();
					 getOutstandingDetailStmt=null;
					 Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForCGPAN","Exception "+exception);
					 connection.rollback();
					 throw new DatabaseException(exception);
				 }
				 // Reading the resultset
				 resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				 cgpanLength = id.length();

				 panType = id.substring((cgpanLength-2), cgpanLength-1);

				 if(panType.equalsIgnoreCase(GMConstants.CGPAN_TC))
				 {
					 while(resultSet.next())
					 {
						 outstandingDetail = new OutstandingDetail();

						 periodicInfo.setBorrowerId(resultSet.getString(1));
						 outstandingDetail.setCgpan(resultSet.getString(2));
						 periodicInfo.setBorrowerName(resultSet.getString(3));
						 outstandingDetail.setScheme(resultSet.getString(4));
						 outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						 listOfOutstandingDetail.add(outstandingDetail);
					 }
					 resultSet.close();
					 resultSet = null;

					 getOutstandingDetailStmt.close();
					 getOutstandingDetailStmt = null;

					 size = listOfOutstandingDetail.size();

					 for(int i = 0; i<size; ++i)
					 {
						 OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
						 cgpan = outDtl.getCgpan() ;

						 len = cgpan.length();

						 appType = cgpan.substring(len-2,len-1);

						 ArrayList outAmounts = new ArrayList();

						 getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 getOutDetailForCgpanStmt.setString(2, cgpan);
						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						 }

						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							 connection.rollback();	
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();

							 outAmount.setCgpan(cgpan);

							 outAmount.setTcoId(cgpanResultSet.getString(1));
							 outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));
							 outAmounts.add(outAmount);
						 }
						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;

						 outDtl.setOutstandingAmounts(outAmounts);
						 outDtls.add(outDtl);
					 }
				 }
				 else if(panType.equalsIgnoreCase(GMConstants.CGPAN_WC))
				 {
					 while(resultSet.next())
					 {
						 outstandingDetail = new OutstandingDetail();

						 periodicInfo.setBorrowerId(resultSet.getString(1));
						 outstandingDetail.setCgpan(resultSet.getString(2));
						 periodicInfo.setBorrowerName(resultSet.getString(3));
						 outstandingDetail.setScheme(resultSet.getString(4));
						 outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(5));
						 outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(6));

						 listOfOutstandingDetail.add(outstandingDetail);
					 }
					 resultSet.close();
					 resultSet = null;

					 getOutstandingDetailStmt.close();
					 getOutstandingDetailStmt = null;

					 size = listOfOutstandingDetail.size();

					 for(int i = 0; i<size; ++i)
					 {
						 OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
						 cgpan = outDtl.getCgpan() ;

						 len = cgpan.length();

						 appType = cgpan.substring(len-2,len-1);

						 ArrayList outAmounts = new ArrayList();

						 getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 getOutDetailForCgpanStmt.setString(2, cgpan);
						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GETWcOutstanding","Success");
						 }

						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GETWcOutstanding","Exception "+exception);
							 connection.rollback();
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();
							 outAmount.setCgpan(cgpan);
							 outAmount.setWcoId(cgpanResultSet.getString(1));
							 outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							 outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							 outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							 outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							 outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));

							 outAmounts.add(outAmount);
						 }

						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;

						 outDtl.setOutstandingAmounts(outAmounts);
						 outDtls.add(outDtl);
					 }
				 }
				 periodicInfo.setOutstandingDetails(outDtls);
				 periodicInfos.add(periodicInfo);
			 }
			 else if(type==GMConstants.TYPE_TWO)
			 {
				 functionName="{?=call packGetOutstandingDtls.funcGetOutStandingforBorr(?,?,?)}"	;
				 getOutstandingDetailStmt = connection.prepareCall(functionName);
				 getOutstandingDetailStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;

				 getOutstandingDetailStmt.setString(2, id);
				 getOutstandingDetailStmt.registerOutParameter(3,Constants.CURSOR) ;
				 getOutstandingDetailStmt.registerOutParameter(4,
													 java.sql.Types.VARCHAR) ;

				 getOutstandingDetailStmt.execute() ;

				 exception = getOutstandingDetailStmt.getString(4) ;

				 int error=getOutstandingDetailStmt.getInt(1);

				 if(error == Constants.FUNCTION_SUCCESS)
				 {
					 Log.log(Log.DEBUG,"GMDAO","GET OTS For Borr Name SP","Success");
				 }

				 if(error==Constants.FUNCTION_FAILURE)
				 {
					 getOutstandingDetailStmt.close();
					 getOutstandingDetailStmt=null;
					 Log.log(Log.ERROR,"GMDAO","getOutstandingDetailsForBorrower","Exception "+exception);
					 connection.rollback();
					 throw new DatabaseException(exception);
				 }
				 // Reading the resultset
				 resultSet = (ResultSet)getOutstandingDetailStmt.getObject(3) ;

				 boolean firstTime=true;

				 String cgpan1 = null;

				 while (resultSet.next())
				 {
					 outstandingDetail = new OutstandingDetail();

					 if(firstTime)
					 {
						 periodicInfo.setBorrowerId(resultSet.getString(1));
						 periodicInfo.setBorrowerName(resultSet.getString(4));
						 firstTime=false;
					 }
					 cgpan1 = resultSet.getString(2);
					 if(cgpan1 != null)
					 {
						 outstandingDetail.setCgpan(cgpan1);
						 outstandingDetail.setScheme(resultSet.getString(3));
						 outstandingDetail.setTcSanctionedAmount(resultSet.getDouble(5));
						 outstandingDetail.setWcFBSanctionedAmount(resultSet.getDouble(6));
						 outstandingDetail.setWcNFBSanctionedAmount(resultSet.getDouble(7));

						 listOfOutstandingDetail.add(outstandingDetail);
					 }
				 }
				 resultSet.close();
				 resultSet = null;

				 getOutstandingDetailStmt.close();
				 getOutstandingDetailStmt = null;

				 size = listOfOutstandingDetail.size();

				 for(int i = 0; i<size; ++i)
				 {
					 OutstandingDetail outDtl = (OutstandingDetail)listOfOutstandingDetail.get(i);
					 cgpan = outDtl.getCgpan() ;

					 len = cgpan.length();

					 appType = cgpan.substring(len-2,len-1);

					 ArrayList outAmounts = new ArrayList();

					 if(appType.equalsIgnoreCase(GMConstants.CGPAN_TC))
					 {
						 getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetTCOutStanding.funcTCOutStanding(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 getOutDetailForCgpanStmt.setString(2, cgpan);
						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GETTcOutstanding","Success");
						 }

						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GETTcOutstanding","Exception "+exception);
							 connection.rollback();
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;
						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();
							 outAmount.setCgpan(cgpan);
							 outAmount.setTcoId(cgpanResultSet.getString(1));
							 outAmount.setTcPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setTcOutstandingAsOnDate(cgpanResultSet.getDate(3));
							 outAmounts.add(outAmount);
						 }
						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;
					 }
					 else if(appType.equalsIgnoreCase(GMConstants.CGPAN_WC) ||
							 appType.equalsIgnoreCase(GMConstants.CGPAN_RENEWAL))
					 {
						 getOutDetailForCgpanStmt = connection.prepareCall("{?=call packGetWCOutStanding.funcWCOutStanding(?,?,?)}");
						 getOutDetailForCgpanStmt.registerOutParameter(1,java.sql.Types.INTEGER) ;
						 getOutDetailForCgpanStmt.setString(2, cgpan);
						 getOutDetailForCgpanStmt.registerOutParameter(3,Constants.CURSOR) ;
						 getOutDetailForCgpanStmt.registerOutParameter(4,
															 java.sql.Types.VARCHAR) ;

						 getOutDetailForCgpanStmt.execute() ;

						 exception = getOutDetailForCgpanStmt.getString(4) ;

						 error=getOutDetailForCgpanStmt.getInt(1);

						 if(error == Constants.FUNCTION_SUCCESS)
						 {
							 Log.log(Log.DEBUG,"GMDAO","GET WC Outstanding","Success");
						 }
						 if(error==Constants.FUNCTION_FAILURE)
						 {
							 getOutDetailForCgpanStmt.close();
							 getOutDetailForCgpanStmt=null;
							 Log.log(Log.ERROR,"GMDAO","GET WC Outstanding","Exception "+exception);
							 connection.rollback();
							 throw new DatabaseException(exception);
						 }

						 // Reading the resultset
						 cgpanResultSet = (ResultSet)getOutDetailForCgpanStmt.getObject(3) ;

						 OutstandingAmount outAmount = null;

						 while(cgpanResultSet.next())
						 {
							 outAmount = new OutstandingAmount();

							 outAmount.setCgpan(cgpan);

							 outAmount.setWcoId(cgpanResultSet.getString(1));
							 outAmount.setWcFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(2));
							 outAmount.setWcFBInterestOutstandingAmount(cgpanResultSet.getDouble(3));
							 outAmount.setWcFBOutstandingAsOnDate(cgpanResultSet.getDate(4));
							 outAmount.setWcNFBPrincipalOutstandingAmount(cgpanResultSet.getDouble(5));
							 outAmount.setWcNFBInterestOutstandingAmount(cgpanResultSet.getDouble(6));
							 outAmount.setWcNFBOutstandingAsOnDate(cgpanResultSet.getDate(7));
							 outAmounts.add(outAmount);
						 }
						 cgpanResultSet.close();
						 cgpanResultSet = null;

						 getOutDetailForCgpanStmt.close();
						 getOutDetailForCgpanStmt=null;
					 }
					 outDtl.setOutstandingAmounts(outAmounts);
					 outDtls.add(outDtl);
				 }
				 periodicInfo.setOutstandingDetails(outDtls);
				 periodicInfos.add(periodicInfo);
			 }
			 connection.commit();
		 }catch (SQLException exception)
		 {
			 Log.logException(exception);
			 throw new DatabaseException(exception.getMessage()) ;
		 }
		 finally
		 {
			 DBConnection.freeConnection(connection);
		 }
		 Log.log(Log.INFO,"GMDAO","getOutstandingDetails","Exited");
		 return  periodicInfos;
	}



	/**
	 * This method the memberIds and borrower Ids for which borrower details have been modified
	 * @author SS14485
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */	   
	   
	public TreeMap getBidsForApproval()throws DatabaseException
	{
	 Log.log(Log.INFO,"GMDAO","getBidsForApproval","Entered");
		 ArrayList borrowerIds = new ArrayList();
		 TreeMap bidsList = new TreeMap();
	   		
	 Connection connection = DBConnection.getConnection(false) ;
		
		 try
		 {			
	
			 CallableStatement memberIdsList = connection.prepareCall(
				 "{?=call packGetBidsForModification.funcGetBidsForModification(?,?)}");
		
		
		
			 memberIdsList.registerOutParameter(1, java.sql.Types.INTEGER) ;		
			 memberIdsList.registerOutParameter(2, Constants.CURSOR) ;
			 memberIdsList.registerOutParameter(3, java.sql.Types.VARCHAR) ;
				
			 memberIdsList.execute();
			 int memberIdValue=memberIdsList.getInt(1);

			 Log.log(Log.DEBUG,"GMDAO","getBidsForApproval","memberIdValue :" + memberIdValue);

			  if(memberIdValue==Constants.FUNCTION_FAILURE){

				  String error = memberIdsList.getString(3);

				 memberIdsList.close();
				 memberIdsList=null;
							
				 connection.rollback();

				 Log.log(Log.DEBUG,"GMDAO","getBidsForApproval","error:" + error);

				 throw new DatabaseException(error);
			  }	else {						

				 ResultSet memberIdResult = (ResultSet)memberIdsList.getObject(2);
				 ArrayList borrowersList = new ArrayList();
				 while(memberIdResult.next())
				 {
					String memberId = memberIdResult.getString(1);
				 	if(bidsList.containsKey(memberId))
				 	{
						borrowersList= (ArrayList)bidsList.get(memberId);
				 	}
				 	else{
				 		
						borrowersList=new ArrayList();
				 	}
					borrowersList.add(memberIdResult.getString(2));
					 
					bidsList.put(memberId,borrowersList);
				 }
			 }
		 }
	 catch (SQLException exception)
	 {
		 Log.log(Log.ERROR,"GMDAO","getBidsForApproval",exception.getMessage());
		 Log.logException(exception);

		 try
		 {
			 connection.rollback();
		 }
		 catch (SQLException ignore){}


		 throw new DatabaseException("Unable to get Member and Borrower Ids.");
	 }
	 finally
	 {
		 DBConnection.freeConnection(connection);
	 }

	 Log.log(Log.INFO,"RpDAO","getMemberIdsForExcess","Exited");
		 return bidsList;
	}
	   
     
/**
   * 
   * @param bid
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
public int getExceptionBIDCount(String bid)throws DatabaseException
	{
		Log.log(Log.INFO,"GMDAO","getTotalApprovedAmt","Entered");
		
		int bidCount =0;
		
		Connection connection=DBConnection.getConnection();
		
		try{
			CallableStatement bidCountStmt=connection.prepareCall
									("{?=call Funccheckexceptionalbid(?,?,?)}");
									
			bidCountStmt.setString(2,bid);		
			Log.log(Log.INFO,"GMDAO","getExceptionBIDCount","getClaimCount");
			
			bidCountStmt.registerOutParameter(1,java.sql.Types.INTEGER);						
			bidCountStmt.registerOutParameter(3,java.sql.Types.INTEGER);
			bidCountStmt.registerOutParameter(4,java.sql.Types.VARCHAR);
			
			bidCountStmt.execute();
			
			int totalApprovedStmtValue=bidCountStmt.getInt(1);
			
			if(totalApprovedStmtValue==Constants.FUNCTION_FAILURE){

				String error = bidCountStmt.getString(4);

				bidCountStmt.close();
				bidCountStmt=null;
					
			   connection.rollback();

			   throw new DatabaseException(error);
			}	else {
				
				bidCount = bidCountStmt.getInt(3);
				
				Log.log(Log.INFO,"GMDAO","getExceptionBIDCount","getClaimCount :" + bidCount);
			}		
			bidCountStmt.close();
			bidCountStmt=null;
		}
		catch (SQLException sqlException)
		{

			Log.log(Log.INFO,"GMDAO","getExceptionBIDCount",sqlException.getMessage());
			Log.logException(sqlException);
	
			try
			{
				connection.rollback();
			}
			catch (SQLException ignore){
				Log.log(Log.INFO,"GMDAO","getExceptionBIDCount",ignore.getMessage());
			}
	
			throw new DatabaseException(sqlException.getMessage());

		}finally{
			DBConnection.freeConnection(connection);
		}

		return bidCount;
	}  
     
     


	/**
	 * This method the memberIds and borrower Ids for which periodic info details have been modified
	 * @author SS14485
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */	   
	   
	public TreeMap getBidsForPerInfoApproval()throws DatabaseException
	{
	 Log.log(Log.INFO,"GMDAO","getBidsForPerInfoApproval","Entered");
		 ArrayList borrowerIds = new ArrayList();
		 TreeMap bidsList = new TreeMap();
	   		
	 Connection connection = DBConnection.getConnection(false) ;
		
		 try
		 {			
	
			 CallableStatement memberIdsList = connection.prepareCall(
				 "{?=call packGetBidsForNPARecovery.funcGetBidsForNPARecovery(?,?)}");
		
		
		
			 memberIdsList.registerOutParameter(1, java.sql.Types.INTEGER) ;		
			 memberIdsList.registerOutParameter(2, Constants.CURSOR) ;
			 memberIdsList.registerOutParameter(3, java.sql.Types.VARCHAR) ;
				
			 memberIdsList.execute();
			 int memberIdValue=memberIdsList.getInt(1);

			 Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","memberIdValue :" + memberIdValue);

			  if(memberIdValue==Constants.FUNCTION_FAILURE){

				  String error = memberIdsList.getString(3);

				 memberIdsList.close();
				 memberIdsList=null;
							
				 connection.rollback();

				 Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","error:" + error);

				 throw new DatabaseException(error);
			  }	else {						

				 ResultSet memberIdResult = (ResultSet)memberIdsList.getObject(2);
				 ArrayList borrowersList = new ArrayList();
				 while(memberIdResult.next())
				 {
					String memberId = memberIdResult.getString(1);
					Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","memberId:" + memberId);
					if(bidsList.containsKey(memberId))
					{
						borrowersList= (ArrayList)bidsList.get(memberId);
					}
					else{
				 		
						borrowersList=new ArrayList();
					}
					borrowersList.add(memberIdResult.getString(2));
					Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","memberIdResult.getString(2):" + memberIdResult.getString(2));
					 
					bidsList.put(memberId,borrowersList);
				 }
			 }
			 
			CallableStatement borrowerIdsList = connection.prepareCall(
				"{?=call packGetBidsForOutDisRep.funcGetBidsForOutDisRep(?,?)}");
		
		
		
			borrowerIdsList.registerOutParameter(1, java.sql.Types.INTEGER) ;		
			borrowerIdsList.registerOutParameter(2, Constants.CURSOR) ;
			borrowerIdsList.registerOutParameter(3, java.sql.Types.VARCHAR) ;
				
			borrowerIdsList.execute();
			int borrowerIdValue=borrowerIdsList.getInt(1);

			Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","memberIdValue :" + borrowerIdValue);

			 if(borrowerIdValue==Constants.FUNCTION_FAILURE){

				 String error = borrowerIdsList.getString(3);

				borrowerIdsList.close();
				borrowerIdsList=null;
							
				connection.rollback();

				Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","error:" + error);

				throw new DatabaseException(error);
			 }	else {						

				ResultSet borrowerIdResult = (ResultSet)borrowerIdsList.getObject(2);
				ArrayList borrowersList = new ArrayList();
				while(borrowerIdResult.next())
				{
				   String memberId = borrowerIdResult.getString(1);
				   Log.log(Log.DEBUG,"GMDAO","getBidsForPerInfoApproval","memberId 1:" + memberId);
				   if(bidsList.containsKey(memberId))
				   {				   	
					   borrowersList= (ArrayList)bidsList.get(memberId);
				   }
				   else{
				 		
					   borrowersList=new ArrayList();
				   }			
				   if(!borrowersList.contains(borrowerIdResult.getString(2)))
				   {
					borrowersList.add(borrowerIdResult.getString(2));	   
				   }
					 
				   bidsList.put(memberId,borrowersList);
				}
			}
			 
		 }
	 catch (SQLException exception)
	 {
		 Log.log(Log.ERROR,"GMDAO","getBidsForPerInfoApproval",exception.getMessage());
		 Log.logException(exception);

		 try
		 {
			 connection.rollback();
		 }
		 catch (SQLException ignore){}


		 throw new DatabaseException("Unable to get Member and Borrower Ids.");
	 }
	 finally
	 {
		 DBConnection.freeConnection(connection);
	 }

	 Log.log(Log.INFO,"RpDAO","getBidsForPerInfoApproval","Exited");
		 return bidsList;
	}
        
        
        
        
        //added by upchar@path on 08-05-2013
        
         public HashMap getPrimarySecurityAndNetworthOfGuarantorsAsOnSanc(String borrowerId, String memberId) throws DatabaseException
         {
                      Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Entered.");
                      // System.out.println("Control in getPrimarySecurityAndNetworthOfGuarantors method.");
                      com.cgtsi.claim.CPDAO dao = new  com.cgtsi.claim.CPDAO();
                      
                      GMDAO dao2 = new GMDAO();
                      Vector cgpans = dao2.getCGPANDetailsNPA(borrowerId, memberId);
                      
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
                      if(cgpans == null)
                      {
                              return null;
                      }
                      try
                      {
                              conn = DBConnection.getConnection();
                      for(int j=0; j<cgpans.size(); j++)
                      {
                              cgpanDtl = (HashMap)cgpans.elementAt(j);
                              if(cgpanDtl == null)
                              {
                                      continue;
                              }
                              // System.out.println("From CPDAO -> Printing HashMap :" + cgpanDtl);
                              cgpan = (String)cgpanDtl.get(ClaimConstants.CLM_CGPAN);
                              if(cgpan == null)
                              {
                                      continue;
                              }
                              appRefNumber = dao.getAppRefNumber (cgpan);
                              if(appRefNumber == null)
                              {
                                      continue;
                              }
                              // System.out.println("1");
                              callableStmt=conn.prepareCall(
                                              "{?=call packGetPersonalGuarantee.funcGetPerGuarforAppRef(?,?,?)}");

                              callableStmt.setString(2,appRefNumber); //Application Ref Number

                              callableStmt.registerOutParameter(1,Types.INTEGER);
                              callableStmt.registerOutParameter(4,Types.VARCHAR);

                              callableStmt.registerOutParameter(3,Constants.CURSOR);
                              // System.out.println("2");
                              callableStmt.executeQuery();
                              // System.out.println("3");
                              int status=callableStmt.getInt(1);

                              Log.log(Log.DEBUG,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors","Status :" + status);

                               if(status==Constants.FUNCTION_FAILURE){
                               // System.out.println("4");
                                       String error = callableStmt.getString(4);

                                       callableStmt.close();
                                      callableStmt=null;

                                      conn.rollback();

                                      Log.log(Log.ERROR,"ApplicationDAO","getPrimarySecurityAndNetworthOfGuarantors","Error Message:" + error);

                                      throw new DatabaseException(error);
                               }      else {
                               // System.out.println("5");
                                              ResultSet guarantorsResults=(ResultSet)callableStmt.getObject(3);
                                              int i=0;
                                              while(guarantorsResults.next())
                                              {
                                              // System.out.println("6");
                                                              if (i==0)
                                                              {
                                                                      totalNetWorth = totalNetWorth + guarantorsResults.getDouble(2);
                                                              }
                                                              if (i==1)
                                                              {
                                                                      totalNetWorth = totalNetWorth + guarantorsResults.getDouble(2);
                                                              }
                                                              if (i==2)
                                                              {
                                                                      totalNetWorth = totalNetWorth + guarantorsResults.getDouble(2);
                                                              }
                                                              if (i==3)
                                                              {
                                                                      totalNetWorth = totalNetWorth + guarantorsResults.getDouble(2);
                                                              }
                                                              i++;
                                              }
                                              // System.out.println("7");
                                              guarantorsResults.close();
                                              guarantorsResults=null;

                                              callableStmt.close();
                                              callableStmt=null;
                                      }
                              }
                              // System.out.println("8");
                              completeDtls.put("networth", new Double(totalNetWorth));
                              // System.out.println("1 -> Printing Complete Details :" +completeDtls );
                              for(int i=0; i<cgpans.size(); i++)
                              {
                                      cgpanDtl = (HashMap)cgpans.elementAt(i);
                                      if(cgpanDtl == null)
                                      {
                                              continue;
                                      }
                                      // System.out.println("From CPDAO -> Printing HashMap :" + cgpanDtl);
                                      cgpan = (String)cgpanDtl.get(ClaimConstants.CLM_CGPAN);
                                      if(cgpan == null)
                                      {
                                              continue;
                                      }
                                      appRefNumber = dao.getAppRefNumber(cgpan);
                                      if(appRefNumber == null)
                                      {
                                              continue;
                                      }
                                      // Retrieving the Primary Security Details
                                      callableStmt=conn.prepareCall(
                                                      "{?=call packGetPrimarySecurity.funcGetPriSecforAppRef(?,?,?)}");

                                      callableStmt.setString(2,appRefNumber); //Application Reference Number

                                      callableStmt.registerOutParameter(1,Types.INTEGER);
                                      callableStmt.registerOutParameter(4,Types.VARCHAR);

                                      callableStmt.registerOutParameter(3,Constants.CURSOR);

                                      callableStmt.executeQuery();
                                      int status=callableStmt.getInt(1);

                                       if(status==Constants.FUNCTION_FAILURE){

                                               String error = callableStmt.getString(4);

                                               callableStmt.close();
                                              callableStmt=null;

                                              conn.rollback();

                                              throw new DatabaseException(error);
                                       }      else {
                                                      ResultSet psResults=(ResultSet)callableStmt.getObject(3);
                                                              while(psResults.next())
                                                              {
                                                                      if ((psResults.getString(1)).equals("Land"))
                                                                      {
                                                                              totalValOfLand = totalValOfLand + psResults.getDouble(3);
                                                                      }if ((psResults.getString(1)).equals("Building"))
                                                                      {
                                                                              totalValOfBuilding = totalValOfBuilding + psResults.getDouble(3);

                                                                      }if ((psResults.getString(1)).equals("Machinery"))
                                                                      {
                                                                              totalValOfMachine = totalValOfMachine + psResults.getDouble(3);

                                                                      }if ((psResults.getString(1)).equals("Fixed Assets"))
                                                                      {
                                                                              totalValOfOFMA = totalValOfOFMA + psResults.getDouble(3);

                                                                      }if ((psResults.getString(1)).equals("Current Assets"))
                                                                      {
                                                                              totalValOfCurrAssets = totalValOfCurrAssets + psResults.getDouble(3);

                                                                      }if ((psResults.getString(1)).equals("Others"))
                                                                      {
                                                                              totalValOfOthers = totalValOfOthers + psResults.getDouble(3);

                                                                      }


                                                              }
                                                              psResults.close();
                                                              psResults=null;
                                                              callableStmt.close();
                                                              callableStmt=null;
                                              }
                                      }

                              }
                              catch(SQLException sqlex)
                              {
                                      // sqlex.getCause();
                                      // sqlex.printStackTrace();
                                      Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Error :" + sqlex.getMessage());
                                      throw new DatabaseException(sqlex.getMessage());
                              }
                              finally{
                                      DBConnection.freeConnection(conn);
                              }

                              completeDtls.put("land",new Double(totalValOfLand));
                              completeDtls.put("building",new Double(totalValOfBuilding));
                              completeDtls.put("machine",new Double(totalValOfMachine));
                              completeDtls.put("fixed_mov_asset",new Double(totalValOfOFMA));
                              completeDtls.put("current_asset", new Double(totalValOfCurrAssets));
                              completeDtls.put("others", new Double(totalValOfOthers));

                              // System.out.println("2- >Printing Complete Details :" +completeDtls );
                              Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Exited");
                              return completeDtls;
         }
         
         
         /* added by upchar@path*/
          public Vector getCGPANDetailsNPA(String borrowerId, String memberId) throws DatabaseException
          {
           //   System.out.println("----GMDAO----getCGPANDetailsNPA-------");
              Log.log(Log.INFO,"CPDAO","getCGPANDetailsForBorrowerId()","Entered!");
              HashMap cgpandetails = null;
              Vector allcgpandetails = new Vector();
              ApplicationDAO appDAO = new ApplicationDAO();
              Application application = null;
              String query = null;
              
          //              String query="SELECT  A.CGPAN,A.app_status,A.app_loan_type, A.app_guar_start_date_time,a.app_sanction_dt,A.app_approved_amount,NVL(t.trm_interest_rate,w.wcp_interest) RATE "
          //                              +" FROM  APPLICATION_DETAIL A, SSI_DETAIL s, term_loan_detail t, working_capital_detail w "
          //                              +" WHERE   LTRIM(RTRIM(UPPER(s.bid))) ='" + borrowerId
          //                              +"' AND  LTRIM(RTRIM(UPPER(A.app_status))) IN('AP') "//NOT IN ('CL','XE','LC','RE')"
          //                              +" AND  LTRIM(RTRIM(UPPER(A.app_created_modified_by))) <> LTRIM(RTRIM(UPPER('demouser')))"
          //                              +" AND  LTRIM(RTRIM(UPPER(s.ssi_created_modified_by))) <> LTRIM(RTRIM(UPPER('demouser')))"
          //                              +" AND  s.ssi_reference_number = A.ssi_reference_number and a.app_ref_no=t.app_ref_no and a.app_ref_no=w.app_ref_no";
                              
                              
                              query = "SELECT A.CGPAN,APP_LOAN_TYPE,APP_GUAR_START_DATE_TIME,APP_SANCTION_DT,APP_APPROVED_AMOUNT,TRM_INTEREST_RATE RATE\n" + 
                              "FROM APPLICATION_DETAIL A,SSI_DETAIL S,TERM_LOAN_DETAIL T\n" + 
                              "WHERE A.SSI_rEFERENCE_NUMBER = S.SSI_rEFERENCE_NUMBER\n" + 
                              "AND A.APP_rEF_NO = T.APP_REF_NO\n" + 
                              "AND APP_LOAN_TYPE IN ('TC','CC')\n" + 
                              "AND BID = '" + borrowerId +
                              "' \n" + 
                              "AND  LTRIM(RTRIM(UPPER(A.app_status))) NOT IN ('CL','XE','LC','RE','EX')\n" + 
                              "UNION ALL\n" + 
                              "SELECT A.CGPAN,APP_LOAN_TYPE,APP_GUAR_START_DATE_TIME,APP_SANCTION_DT,APP_APPROVED_AMOUNT,WCP_INTEREST RATE\n" + 
                              "FROM APPLICATION_DETAIL A,SSI_DETAIL S,WORKING_CAPITAL_DETAIL W\n" + 
                              "WHERE A.SSI_rEFERENCE_NUMBER = S.SSI_rEFERENCE_NUMBER\n" + 
                              "AND A.APP_rEF_NO = W.APP_REF_NO\n" + 
                              "AND APP_LOAN_TYPE IN ('WC')\n" + 
                              "AND BID = '" + borrowerId +
                              "' \n" + 
                              "AND  LTRIM(RTRIM(UPPER(A.app_status))) NOT IN ('CL','XE','LC','RE','EX')";

              
              Connection conn = null;
              Statement stmt = null;
              ResultSet rs = null;
              
             
              Map cgpanMap = null;
              
              
              int status = -1;
              String errorCode = null;
          //     NPADetails npaDetails = null;
            
              try
              {
                  
              
                   conn = DBConnection.getConnection();
                   stmt = conn.createStatement();
                   rs = stmt.executeQuery(query);
                   
                   while(rs.next()){
                  
                   cgpanMap = new HashMap();
                   String cgpan = rs.getString("cgpan");
                   
                  
                   
                   Date guarDate = rs.getDate("app_guar_start_date_time");
                   Date sancDate = rs.getDate("app_sanction_dt");
                   String loanType = rs.getString("app_loan_type");
          //          String cgpanstatus = rs.getString("app_status");
                   double approvedAmount = rs.getDouble("app_approved_amount");
                   double rate = rs.getDouble("rate");
                   
                   
                   cgpanMap.put("CGPAN",cgpan);
                   cgpanMap.put("GUARANTEE_START_DT",guarDate);
                   cgpanMap.put("SANCTION_DT",sancDate);
                   cgpanMap.put("CGPAN_LOAN_TYPE",loanType); 
          //            cgpanMap.put("APPLICATION_STATUS",cgpanstatus);
                   cgpanMap.put("APPROVED_AMOUNT",new Double(approvedAmount));
                   cgpanMap.put("RATE",rate);
                   
                   allcgpandetails.add(cgpanMap);
                      
                   }
                   
              }
              catch(SQLException sqlexception)
              {
                   Log.log(Log.ERROR,"CPDAO","getCGPANDetailsForBorrowerId()","Error retrieving CGPAN Details for the Borrower!");
                   throw new DatabaseException(sqlexception.getMessage());
              }
              finally{
                   DBConnection.freeConnection(conn);
              }
          //       System.out.println("----exited GMDAO---getCGPANDetailsNPA-----");
           
              return allcgpandetails;
          
          
          }
          
    
        
          
    public HashMap getPrimarySecurity(String borrowerId, String memberId) throws DatabaseException
    {
                 Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Entered.");
                 // System.out.println("Control in getPrimarySecurityAndNetworthOfGuarantors method.");
                // com.cgtsi.claim.CPDAO dao = new  com.cgtsi.claim.CPDAO();
              GMDAO dao = new GMDAO();
                 
            //    Vector cgpans = dao.getCGPANDetailsForBorrowerId(borrowerId,memberId);
             Vector cgpans = dao.getCGPANDetailsNPA(borrowerId,memberId);
                 
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
                 Vector completeSecurity = new Vector();
                 
                 if(cgpans == null || cgpans.size()==0)
                 {
                         return null;
                 }
                 try
                 {
                         conn = DBConnection.getConnection();

                        
                         for(int i=0; i<cgpans.size(); i++)
                         {
                                 cgpanDtl = (HashMap)cgpans.elementAt(i);
                                 if(cgpanDtl == null)
                                 {
                                         continue;
                                 }
                                 // System.out.println("From CPDAO -> Printing HashMap :" + cgpanDtl);
                                 cgpan = (String)cgpanDtl.get("CGPAN");
                                 if(cgpan == null)
                                 {
                                         continue;
                                 }
                                 appRefNumber = dao.getAppRefNumberForCGPAN(cgpan);
                                 if(appRefNumber == null)
                                 {
                                         continue;
                                 }
                                 // Retrieving the Primary Security Details
                                 callableStmt=conn.prepareCall(
                                                 "{?=call packGetPrimarySecurity.funcGetPriSecforAppRef(?,?,?)}");

                                 callableStmt.setString(2,appRefNumber); //Application Reference Number

                                 callableStmt.registerOutParameter(1,Types.INTEGER);
                                 callableStmt.registerOutParameter(4,Types.VARCHAR);

                                 callableStmt.registerOutParameter(3,Constants.CURSOR);

                                 callableStmt.executeQuery();
                                 int status=callableStmt.getInt(1);

                                  if(status==Constants.FUNCTION_FAILURE){

                                          String error = callableStmt.getString(4);

                                          callableStmt.close();
                                         callableStmt=null;

                                         conn.rollback();

                                         throw new DatabaseException(error);
                                  }      else {
                                                 ResultSet psResults=(ResultSet)callableStmt.getObject(3);
                                                         while(psResults.next())
                                                         {
                                                                 if ((psResults.getString(1)).equals("Land"))
                                                                 {
                                                                         totalValOfLand = totalValOfLand + psResults.getDouble(3);
                                                                 }if ((psResults.getString(1)).equals("Building"))
                                                                 {
                                                                         totalValOfBuilding = totalValOfBuilding + psResults.getDouble(3);

                                                                 }if ((psResults.getString(1)).equals("Machinery"))
                                                                 {
                                                                         totalValOfMachine = totalValOfMachine + psResults.getDouble(3);

                                                                 }if ((psResults.getString(1)).equals("Fixed Assets"))
                                                                 {
                                                                         totalValOfOFMA = totalValOfOFMA + psResults.getDouble(3);

                                                                 }if ((psResults.getString(1)).equals("Current Assets"))
                                                                 {
                                                                         totalValOfCurrAssets = totalValOfCurrAssets + psResults.getDouble(3);

                                                                 }if ((psResults.getString(1)).equals("Others"))
                                                                 {
                                                                         totalValOfOthers = totalValOfOthers + psResults.getDouble(3);

                                                                 }

                                                             
                                                             
                                                     //        completeSecurity.add(completeDtls);
                                                         }
                                                         psResults.close();
                                                         psResults=null;
                                                         callableStmt.close();
                                                         callableStmt=null;
                                         }
                                 }

                         }
                         catch(SQLException sqlex)
                         {
                                 // sqlex.getCause();
                                 // sqlex.printStackTrace();
                                 Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Error :" + sqlex.getMessage());
                                 throw new DatabaseException(sqlex.getMessage());
                         }
                         finally{
                             completeDtls.put("PARTICULAR_LAND",new Double(totalValOfLand));
                             completeDtls.put("PARTICULAR_BLDG",new Double(totalValOfBuilding));
                             completeDtls.put("PARTICULAR_MC",new Double(totalValOfMachine));
                             completeDtls.put("PARTICULAR_OTHER_FIXED_MOV_ASSETS",new Double(totalValOfOFMA));
                             completeDtls.put("PARTICULAR_CUR_ASSETS", new Double(totalValOfCurrAssets));
                             completeDtls.put("PARTICULAR_OTHERS", new Double(totalValOfOthers));
                             
                                 DBConnection.freeConnection(conn);
                         }

                         

                         // System.out.println("2- >Printing Complete Details :" +completeDtls );
                         Log.log(Log.ERROR,"CPDAO","getPrimarySecurityAndNetworthOfGuarantors()","Exited");
                         return completeDtls;
    }
    
    public String getAppRefNumberForCGPAN(String cgpan)
        throws DatabaseException
    {
        Connection conn;
        String appRefNumber;
        Log.log(2, "CPDAO", "getAppRefNumber()", "Entered");
        CallableStatement callableStmt = null;
        conn = null;
        int status = -1;
        String errorCode = null;
        appRefNumber = null;
        try
        {
            conn = DBConnection.getConnection();
            if(conn == null)
                Log.log(2, "CPDAO", "getAppRefNumber()", "Conection is null");
             callableStmt = conn.prepareCall("{? = call funcgetapprefnoforcgpan(?,?,?,?)}");
            callableStmt.registerOutParameter(1, Types.INTEGER);
            callableStmt.registerOutParameter(2, Types.VARCHAR);
            callableStmt.setNull(3, Types.VARCHAR);
            callableStmt.setString(4, cgpan);
            callableStmt.registerOutParameter(5, Types.VARCHAR);
            System.out.println((new StringBuilder()).append("cgpan:").append(cgpan).toString());
            callableStmt.execute();
            
             status = callableStmt.getInt(1);
             errorCode = callableStmt.getString(5);
            if(status == 1)
            {
                Log.log(2, "CPDAO", "getAppRefNumber()", (new StringBuilder()).append("SP returns a 1 in getting all claims filed.Error code is :").append(errorCode).toString());
                callableStmt.close();
            }
            appRefNumber = callableStmt.getString(2);
            System.out.println((new StringBuilder()).append("appRefNumber:").append(appRefNumber).toString());
        }
        catch(SQLException sqlex)
        {
            Log.log(2, "CPDAO", "getAppRefNumber()", (new StringBuilder()).append("Error :").append(sqlex.getMessage()).toString());
            throw new DatabaseException(sqlex.getMessage());
        }
   
    finally{
        DBConnection.freeConnection(conn);
    }
        Log.log(2, "CPDAO", "getAppRefNumber()", "Exited");
        return appRefNumber;
    }

    public Vector getCgpanDetailsAsOnNpa(String npaId)throws DatabaseException{
      
           Connection conn = null;
           Statement stmt = null;
           ResultSet rs = null;
           String query = null;
           Vector allCgpans = new Vector();
           Map tcmap = null;
           Map wcmap = null;
           
           conn = DBConnection.getConnection();      

           try {
               stmt = conn.createStatement();
               
               /*logic to get npa tc details*/
               query = "select cgpan,ntd_first_disbursement_dt,ntd_last_disbursement_dt,ntd_first_instalment_dt" +
               ",ntd_principal_repay_amt,ntd_interest_repay_amt,ntd_principal_moratorium,ntd_interest_moratorium" +
               ",ntd_total_disb_amt,ntd_npa_principal_os_amt,ntd_npa_interest_os_amt from npa_tc_detail_temp where npa_id='" + npaId +
               "'" ;
               
               System.out.println("query==="+query);
               
               rs = stmt.executeQuery(query);
               
               while(rs.next()){
                   tcmap = new HashMap();
                   tcmap.put("CGPAN",rs.getString(1));
                   tcmap.put("FIRSTDISBDT",rs.getDate(2));
                   tcmap.put("LASTDISBDT",rs.getDate(3));
                   tcmap.put("FIRSTINSTDT",rs.getDate(4));
                   tcmap.put("PRINCIPALREPAY",rs.getDouble(5));
                   tcmap.put("INTERESTREPAY",rs.getDouble(6));
                   tcmap.put("PRINCIPALMORATORIUM",rs.getInt(7));
                   tcmap.put("INTERESTMORATORIUM",rs.getInt(8));
                   tcmap.put("TOTALDISBAMT",rs.getDouble(9));
                   tcmap.put("PRINCIPALOS",rs.getDouble(10));
                   tcmap.put("INTERESTOS",rs.getDouble(11));
                   allCgpans.add(tcmap);
               }
               
              
               /*logic to get npa wc details*/
                query = "select cgpan,nwd_npa_principal_os_amt,nwd_npa_interest_os_amt from npa_wc_detail_temp where npa_id='" + npaId +
                "'";
                System.out.println("query==="+query);
               rs = stmt.executeQuery(query);
               
               while(rs.next()){
                   wcmap = new HashMap();
                   wcmap.put("CGPAN",rs.getString(1));
                   wcmap.put("PRINCIPALOS",rs.getDouble(2));
                   wcmap.put("INTERESTOS",rs.getDouble(3));
                   allCgpans.add(wcmap);
               }
               
              
               
           } catch (SQLException e) {
           throw new DatabaseException();
           }finally{
               try{
               rs.close();
               }catch(SQLException e){
               
               }
               rs = null;
               try{
               stmt.close();
               }catch(SQLException e){
               
               }
               stmt = null;
               if(conn != null){
                 
                   DBConnection.freeConnection(conn);
                   
                   conn = null;
               }
           }
           return allCgpans;
       }
    
    

	public void saveRevivalRequest(int totalEntry, String cgpan,
			String[] danfys, String[] baseAmts, String[] remarks, String userId)
			throws DatabaseException, Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DBConnection.getConnection(false);
			stmt = conn.createStatement();
			for (int i = 0; i < totalEntry; i++) {
				String danfy = danfys[i];
				double baseAmt = Double.parseDouble(baseAmts[i]);
				String remark = remarks[i];

				if ("".equals(danfy) || "".equals(baseAmt) || "".equals(remark))
					continue;
				System.out.println(cgpan + "----" + danfy + "----" + baseAmt
						+ "----" + remark);
				String sql = " INSERT INTO REVIVAL_REQUEST VALUES('" + cgpan
						+ "','" + danfy + "','" + baseAmt + "','" + remark
						+ "','" + userId + "',sysdate)";

				stmt.executeUpdate(sql);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				DBConnection.freeConnection(conn);
			}
		}
	}

	public ArrayList showRevivalRequestForCGPAN(String cgpan)
			throws DatabaseException, Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList revivals = new ArrayList();
		try {
			conn = DBConnection.getConnection();
			stmt = conn.createStatement();
			String sql = " SELECT CGPAN, DANFY, BASEAMT, REMARKS FROM REVIVAL_REQUEST WHERE CGPAN='"
					+ cgpan + "' ORDER BY CGPAN ";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] str = new String[4];
				str[0] = rs.getString(1);
				str[1] = rs.getString(2);
				str[2] = String.valueOf(rs.getDouble(3));
				str[3] = rs.getString(4);
				revivals.add(str);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			DBConnection.freeConnection(conn);
		}
		return revivals;
	}

	public ArrayList showRevivalRequests() throws DatabaseException, Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList revivals = new ArrayList();
		try {
			conn = DBConnection.getConnection();
			stmt = conn.createStatement();
			String sql = " SELECT CGPAN, DANFY, BASEAMT, REMARKS, CREATED_BY FROM REVIVAL_REQUEST ORDER BY CGPAN ";

			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String cgpan = rs.getString(1);
				String danfy = rs.getString(2);
				double amt = rs.getDouble(3);
				String remark = rs.getString(4);
				String createdBy = rs.getString(5);

				String[] revival = new String[5];
				revival[0] = cgpan;
				revival[1] = danfy;
				revival[2] = String.valueOf(amt);
				revival[3] = remark;
				revival[4] = createdBy;
				revivals.add(revival);
			}

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			DBConnection.freeConnection(conn);
		}

		return revivals;
	}

	public void approveRevivalRequests(String[] cgpans, String[] danfy,
			String[] amts, String[] decisions, String[] remarks) throws DatabaseException,
			Exception {
		Connection conn = null;
		CallableStatement cstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "";
		Map cgmap = null;
		ArrayList cgs = new ArrayList();
		try {
			conn = DBConnection.getConnection(false);
			stmt = conn.createStatement();
			String[] app_status = new String[cgpans.length];
			String[] dan_type = new String[cgpans.length];

			for (int i = 0; i < cgpans.length; i++) {
				String decision = decisions[i];
				if ("APPROVE".equals(decision)) {
					sql = " SELECT APP_SANCTION_DT, APP_EXPIRY_DT FROM APPLICATION_DETAIL WHERE CGPAN='"
							+ cgpans[i] + "' ";
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						Date sancDt = rs.getDate(1);
						String strDt = sdf.format(sancDt);
						Calendar cal = Calendar.getInstance();
						cal.setTime(sancDt);
						int year = cal.get(Calendar.YEAR);
						if (year >= 2013) {
							dan_type[i] = "AF";
						} else {
							dan_type[i] = "SF";
						}
						
						Date currentDate = Calendar.getInstance().getTime();
						if (currentDate.compareTo(rs.getDate(2)) > 0) {
							app_status[i] = "EX";
						} else {
							app_status[i] = "AP";
						}						
					}
					cgmap = new HashMap();
					cgmap.put("CGPAN", cgpans[i]);
					cgmap.put("APP_STATUS", app_status[i]);
					cgmap.put("APP_REMARKS", remarks[i]);
					if(!cgs.contains(cgmap)){
						cgs.add(cgmap);
					}
				}
			}
			if (rs != null)
				rs.close();
			
			for (int i = 0; i < cgs.size(); i++) {
				HashMap map = (HashMap)cgs.get(i);
				String cg = (String) map.get("CGPAN");
				String status = (String) map.get("APP_STATUS");				
					sql = " UPDATE APPLICATION_DETAIL SET APP_STATUS='"
							+ status + "',APP_REMARKS=APP_REMARKS||'. Account revived "+remarks[i]+"' WHERE CGPAN='" + cg
							+ "' ";
					stmt.executeUpdate(sql);
					sql = " UPDATE APPLICATION_DETAIL@repuser SET APP_STATUS='"
						+ status + "',APP_REMARKS=APP_REMARKS||'. Account revived "+remarks[i]+"'  WHERE CGPAN='" + cg
						+ "' ";
					stmt.executeUpdate(sql);
					sql = " DELETE FROM REVIVAL_REQUEST WHERE CGPAN='"+cg+"'";
					stmt.executeUpdate(sql);
					
			}
			if (stmt != null)
				stmt.close();
			
			
			cstmt = conn.prepareCall("{? = call funcinsadhocdan(?,?,?,?,?,?)}");
			for (int i = 0; i < cgpans.length; i++) {
				String decision = decisions[i];
				if ("APPROVE".equals(decision)) {
					cstmt.registerOutParameter(1, Types.INTEGER);
					cstmt.setString(2, cgpans[i]);
					cstmt.setString(3, dan_type[i]);
					cstmt.setDouble(4, Double.parseDouble(amts[i]));
					cstmt.setString(
							5,
							(danfy[i].replace("FY", dan_type[i]) + " generated as case revived."));
					cstmt.setString(6, app_status[i]);
					cstmt.registerOutParameter(7, Types.VARCHAR);
					cstmt.execute();
				}
			}
			
			//stmt = conn.createStatement();
			
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw new DatabaseException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cstmt != null) {
				cstmt.close();
			}
			DBConnection.freeConnection(conn);
		}
	}

  
}

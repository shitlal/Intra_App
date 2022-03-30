//Source file: D:\\com\\cgtsi\\guaranteemaintenance\\GMProcessor.java

/*************************************************************
   * 
   * Name of the class: GMProcessor  
   * This class is to handle data and operations of the Guarantee 
   * Maintenace module. 
   * It acts as a medium to transfer data the user enters to the database.
   * @author : Gowrishankar K.U
   * @version:  
   * @since:  
**************************************************************/
package com.cgtsi.guaranteemaintenance;

 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.Message;
import com.cgtsi.admin.User;
import com.cgtsi.application.Application;
import com.cgtsi.application.ApplicationProcessor;
import com.cgtsi.application.BorrowerDetails;
import com.cgtsi.application.NoApplicationFoundException;

import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.Mailer;
import com.cgtsi.common.MailerException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.common.NoUserFoundException;

import java.util.Map;

public class GMProcessor {
   
   /**
    * 
    * constructor 
    * @roseuid 39B9CCD903B9
    */
   public GMProcessor() {
    
   }



      public OutstandingDetail getOutstandingDetailsForCgpan(String cgpan) throws  DatabaseException
    {
		Log.log(Log.INFO,"GMProcessor","getOutstandingDetailsForCgpan","Entered");
		OutstandingDetail outstandingDetail = null;
		GMDAO gmDAO = new GMDAO() ;
		outstandingDetail = gmDAO.getOutstandingDetailsForCgpan(cgpan); 
		Log.log(Log.INFO,"GMProcessor","getOutstandingDetailsForCgpan","Exited");
		return outstandingDetail;	
      
   }   
    
   public Disbursement getDisbursementDetailsForCgpan(String cgpan) throws  DatabaseException
	{
		Log.log(Log.INFO,"GMProcessor","getDisbursementDetailsForCgpan","Entered");
		Disbursement disbursement = null;
		GMDAO gmDAO = new GMDAO() ;
		disbursement = gmDAO.getDisbursementDetailsForCgpan(cgpan); 
		Log.log(Log.INFO,"GMProcessor","getDisbursementDetailsForCgpan","Exited");
		return disbursement;	
      
   } 
   
   /**
   * 
   * @param bankId
   * @param zoneId
   * @param branchId
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
   public ArrayList displayShiftingCgpans(String bankId, String zoneId, String branchId) throws DatabaseException
   {
 //  System.out.println("displayShiftingCgpans Entered");
    	GMDAO gmDAO = new GMDAO();
		ArrayList danDetails ;
    	danDetails = gmDAO.getShiftCgpanForMember(bankId, zoneId, branchId) ;
   //   System.out.println("displayShiftingCgpans exited");
    	return danDetails ;
   }
   
   /**
   * 
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
    public ArrayList displayRequestedForClosureApproval() throws DatabaseException
   {
   // System.out.println("displayRequestedForClosureApproval Entered");
    GMDAO gmDAO = new GMDAO();
		ArrayList danDetails ;
    danDetails = gmDAO.displayRequestedForClosureApproval() ;
 //   System.out.println("displayRequestedForClosureApproval exited");
    return danDetails ;
   }
   
   public Repayment getRepaymentDetailsForCgpan(String cgpan) throws  DatabaseException
	{
		Log.log(Log.INFO,"GMProcessor","getRepaymentDetailsForCgpan","Entered");
		Repayment repayment = null;
		GMDAO gmDAO = new GMDAO() ;
		repayment = gmDAO.getRepaymentDetailsForCgpan(cgpan); 
		Log.log(Log.INFO,"GMProcessor","getRepaymentDetailsForCgpan","Exited");
		return repayment;	
      
   }   
   
   /**
    * This method is to save the repayment details. An arraylist of object type 
    * Repayment is passed as an argument to this method. For each element of the 
    * arraylist, the Repayment object is passed to the UpdateRepaymentDetails of 
    * GMDAO which saves the details in the database.
    * @param repaymentDetails
    * @return Boolean
    * @roseuid 397AD89D00C0
    */
  public void insertRepaymentDetails(ArrayList newRepaymentAmounts, String userId) 
   										throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMProcessor","insertRepaymentDetails","Entered");	
		GMDAO gmDAO = new GMDAO();
		int sizeOfrepaymentAmounts= 0 ; 
		
		if(newRepaymentAmounts== null) {
			return ;    
		}
		sizeOfrepaymentAmounts = newRepaymentAmounts.size() ;
		//inserts in to the database for each repaymnt dtl object.
				
		for(int i = 0; i < sizeOfrepaymentAmounts; i++)	{
			gmDAO.insertRepaymentDetails((RepaymentAmount)newRepaymentAmounts.get(i), userId);	
		} 

		Log.log(Log.INFO,"GMProcessor","insertRepaymentDetails","Exited");
   }
   

   public void updateRepaymentDetails(ArrayList modifiedRepaymentAmounts, String userId) 
										 throws DatabaseException, SQLException {
		 Log.log(Log.INFO,"GMProcessor","upddateRepaymentDetails","Entered");	
		 GMDAO gmDAO = new GMDAO();
		 int sizeOfrepaymentAmounts= 0 ; 
		
		 if(modifiedRepaymentAmounts== null) {
			 return ;
		 }
		 sizeOfrepaymentAmounts = modifiedRepaymentAmounts.size() ;
		 //updates in to the database for each repaymnt dtl object.
				
		 for(int i = 0; i < sizeOfrepaymentAmounts; i++)	{
			 gmDAO.updateRepaymentDetails((RepaymentAmount)modifiedRepaymentAmounts.get(i), userId);	
		 } 

		 Log.log(Log.INFO,"GMProcessor","upddateRepaymentDetails","Exited");
	}


   /**
    * This method is to save the disbursement details. An arraylist of object type 
    * Disbursement is passed as an argument to this method. For each element of the 
    * arraylist, the Disbursement object is passed to the UpdateDisbursementDetails 
    * of GMDAO which saves the details in the database.
    * @param disbursements
    * @return void
    * @roseuid 397AD89D00CC
    */
   public void updateDisbursement(ArrayList disbursements, String userId) 
   									 throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMProcessor","updateDisbursement","Entered");
		GMDAO gmDAO = new GMDAO();
		int sizeOfdisbursements = 0;
		
		if(disbursements == null) {
			return ;
		}
		sizeOfdisbursements = disbursements.size();
		
		for(int i = 0; i< sizeOfdisbursements; i++) {
			gmDAO.updateDisbursement((DisbursementAmount)disbursements.get(i), userId);
			Log.log(Log.DEBUG,"GMProcessor","updateDisbursement","i=>"+i);
		} 
		Log.log(Log.INFO,"GMProcessor","updateDisbursement","Exited");
   }
   

   public void insertDisbursement(ArrayList disbursements, String userId) 
									 throws DatabaseException, SQLException {
		Log.log(Log.INFO,"GMProcessor","insertDisbursement","Entered");
		GMDAO gmDAO = new GMDAO();
		int sizeOfdisbursements = 0;
		
		if(disbursements == null) {
			return ;
		}
		sizeOfdisbursements = disbursements.size();
		
		for(int i = 0; i< sizeOfdisbursements; i++) {
			gmDAO.insertDisbursement((DisbursementAmount)disbursements.get(i), userId);
			Log.log(Log.DEBUG,"GMProcessor","insertDisbursement","i=>"+i);
		} 
		Log.log(Log.INFO,"GMProcessor","insertDisbursement","Exited");
   }


   /**
    * This method is to enter the NPA details of a borrower. An arraylist of objects 
    * of type NPADetails, RecoveryAction and LegalSuitDetails will be passed as an 
    * argument. This object is passed to the addNPADetails of the GMDAO which stores 
    * the details in the database.
    * @param npaDetails
    * @param RecoveryAction - This attribute contains an arraylist of 
    * RecoveryProcedure object.
    * @param LegalSuitDetails - This attribute contains an array of LegalSuitDetails 
    * object.
    * @return Boolean
    * @roseuid 397AD89D00CE
    */
   public void insertNPADetails(NPADetails npaDetails,Vector tcVector,Vector wcVector,Map securityMap)throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","addNPADETAILS","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.insertNPADetails(npaDetails, tcVector,wcVector,securityMap);
		Log.log(Log.INFO,"GMProcessor","addNPADETAILS","Exited");	
   }
   
   
    public void updateNPADetails(NPADetails npaDetails,Vector tcVector,Vector wcVector,Map securityMap)throws DatabaseException  {
                 Log.log(Log.INFO,"GMProcessor","updateNPADetails","Entered");
                 GMDAO  gmDAO = new GMDAO();
                 
                 gmDAO.updateNPADetails(npaDetails,tcVector,wcVector,securityMap);
                 Log.log(Log.INFO,"GMProcessor","updateNPADetails","Exited");    
    }
   
   public void updateNPADetailsOld(NPADetails npaDetails, 
								ArrayList newRecoveryProcedures,
								ArrayList modifiedrecoveryProcedures,  
								LegalSuitDetail legalSuitDetail)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","updateNPADetails","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.updateNPADetailsOld(npaDetails, newRecoveryProcedures,
						modifiedrecoveryProcedures,legalSuitDetail);
		Log.log(Log.INFO,"GMProcessor","updateNPADetails","Exited");	
   }



   public void insertNPAForUpload(NPADetails npaDetails)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","insertNPAForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.insertNpaForUpload(npaDetails);
		Log.log(Log.INFO,"GMProcessor","insertNPAForUpload","Exited");	
   }
   
   public void updateNPAForUpload(NPADetails npaDetails)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","updateNPAForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.updateNpaForUpload(npaDetails);
		Log.log(Log.INFO,"GMProcessor","updateNPAForUpload","Exited");	
   }

   

   public void insertRecAxnForUpload(RecoveryProcedure newRecoveryProcedure)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","insertRecAxnForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.insertRecAxnForUpload(newRecoveryProcedure);
		Log.log(Log.INFO,"GMProcessor","insertRecAxnForUpload","Exited");	
   }

   public void updateRecAxnForUpload(RecoveryProcedure modifiedRecoveryProcedure)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","updateRecAxnForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.updateRecAxnForUpload(modifiedRecoveryProcedure);
		Log.log(Log.INFO,"GMProcessor","updateRecAxnForUpload","Exited");	
   }
   
   
   public void updateLegalForUpload(LegalSuitDetail legalSuitDetail)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","updateLegalForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.updateLegalForUpload(legalSuitDetail);
		Log.log(Log.INFO,"GMProcessor","updateLegalForUpload","Exited");	
   }
   
   public void insertLegalForUpload(LegalSuitDetail legalSuitDetail)
								throws DatabaseException  {
		Log.log(Log.INFO,"GMProcessor","insertLegalForUpload","Entered");
		GMDAO  gmDAO = new GMDAO();
		
		gmDAO.insertLegalForUpload(legalSuitDetail);
		Log.log(Log.INFO,"GMProcessor","insertLegalForUpload","Exited");	
   }



   /**
    * This method is involed when the user selects to close an application due to 
    * some reason. It takes the CGPAN and reason for closure as arguments. This 
    * detail is sent to closure of GMDAO which updates the database of the changes.
    * @param cgpan
    * @param reason
    * @param user
    * @return Boolean
    * @roseuid 397AD89D00D0
    */
   public boolean closure(String cgpan,String reason,String remarks, String userId) 
   										throws NoUserFoundException, DatabaseException,
											NoApplicationFoundException {
		Log.log(Log.INFO,"GMProcessor","Closure ","Entered");
    	GMDAO gmDAO = new GMDAO();

		Log.log(Log.DEBUG,"GMProcessor","Closure ","reason"+reason);
		Log.log(Log.DEBUG,"GMProcessor","Closure ","cgpan"+cgpan);
		Log.log(Log.DEBUG,"GMProcessor","Closure ","remarks"+remarks);
		Log.log(Log.DEBUG,"GMProcessor","Closure ","userid"+userId);

		boolean closureStatus = false;
		//closure process done for cgpan with reason.		
		if(cgpan!=null && userId != null){
			closureStatus = gmDAO.closure(cgpan,reason,remarks, userId);
		}
/*		if(closureStatus)
		{
			sendMailForClosure(cgpan);
		}					 
*/		
		Log.log(Log.INFO,"GMProcessor","Closure ","Exited");
		return closureStatus;
   }
   
   public void sendMailForClosure(String cgpan,String userId,String fromId,String reason) throws DatabaseException,
   										NoApplicationFoundException,NoUserFoundException,
   										MailerException
   {
		Log.log(Log.INFO,"GMProcessor","sendMailForClosure ","Entered");
		Administrator admin = new Administrator();
		Log.log(Log.DEBUG,"GMProcessor","sendMailForClosure ","cgpan "+cgpan);
		Log.log(Log.DEBUG,"GMProcessor","sendMailForClosure ","userId "+userId);
		Log.log(Log.DEBUG,"GMProcessor","sendMailForClosure ","fromId"+fromId);
//    System.out.println("PATH Inside GMProcessor  sendMailForClosure cgpan = "+cgpan);
		String subject="Closure Of Application";
		String messageBody="The application "+cgpan + " is closed beacause " + reason;
		 
		ArrayList emailToAddresses = new ArrayList();
		ArrayList mailToAddresses = new ArrayList();
	
		ApplicationProcessor apProcessor = new ApplicationProcessor();  
		Application apps = apProcessor.getPartApplication(null,cgpan,"");
 //   System.out.println("Comming out PATH with apps in GMProcessor from apProcessor.getPartApplication()");
		String mliIdForCgpan = apps.getMliID();
		
		String userIdForCgpan = apps.getUserId(); 
		
		ArrayList activeUsers = admin.getAllUsers(mliIdForCgpan);
		
		String activeEmailId = null;
		String activeUserMailId = null;
		for(int i =0; i < activeUsers.size(); ++i)
		{
			User activeUserId = (User)activeUsers.get(i);
			//System.out.println("activeUserId  : "+activeUserId);
			activeEmailId = activeUserId.getEmailId();
			//System.out.println("activeEmailId : "+activeEmailId);
			activeUserMailId = activeUserId.getUserId();  
			  
			emailToAddresses.add(activeEmailId);
			mailToAddresses.add(activeUserMailId);			
		}
											 
		//Get the user object and then the email ID for the Admin user.
	//	User adminUser = null;
			
	//	adminUser = admin.getUserInfo(AdminConstants.ADMIN_USER_ID);
			
		
	//	String adminEmailId=adminUser.getEmailId();
	//	Log.log(Log.DEBUG,"GMProcessor","Closure","adminEmailId "+adminEmailId);		
		
		//String fromAddress = userId;
							 
	//	emailToAddresses.add(adminEmailId);//Email To addresses
		
		Message emailMessage=new Message(emailToAddresses,null,null,subject,messageBody);
		Message mailMessage=new Message(mailToAddresses,null,null,subject,messageBody);
		
		emailMessage.setFrom(fromId);
		mailMessage.setFrom(userId);
		
		Mailer mailer=new Mailer();	
		
		admin.sendMail(mailMessage);
 //   System.out.println("PATH after admin.sendMail(mailMessage)");
		mailer.sendEmail(emailMessage);
 //   System.out.println("PATH after mailer.sendEmail(emailMessage)");
    
    Log.log(Log.INFO,"GMProcessor","sendMailForClosure ","Exited");
   }
  
  
   public TreeMap viewMemberIdCgpansForClosure(String feeType) throws DatabaseException {
		
		Log.log(Log.INFO,"GMProcessor","viewMemberIdCgpansForClosure","Entered");
		GMDAO gmDAO = new GMDAO();
		TreeMap memberIdCgpans = null;
		
		memberIdCgpans = gmDAO.getMemberIdCgpansForClosure(feeType);	 
		 
		Log.log(Log.INFO,"GMProcessor","viewMemberIdCgpansForClosure","Exited");
		
		return memberIdCgpans ;
   }


   public ArrayList viewCgpansForClosure(String memberId, String feeType) 
   												throws DatabaseException {
		
		Log.log(Log.INFO,"GMProcessor","viewCgpansForClosure","Entered");
		
		GMDAO gmDAO = new GMDAO();
		
		ArrayList cgpans = null;
		
		cgpans = gmDAO.getCgpansForClosure(memberId, feeType);	 
		 
		Log.log(Log.INFO,"GMProcessor","viewCgpansForClosure","Exited");
		
		return cgpans ;
   }



   /**
    * This method updates the details of an existing borrower. It takes an object of 
    * Borrower type and passes this object to the GMDAO which updates the details of 
    * the borrower in the database.
    * @param borrower
    * @return Boolean
    * @roseuid 397AD89D00DF
    */
   public void updateBorrowerDetails(BorrowerDetails borrowerDetail, String userId)
   										 throws DatabaseException {
		
		Log.log(Log.INFO,"GMProcessor","updateBorrowerDetails","Entered");
		GMDAO gmDAO = new GMDAO();
		
		if(borrowerDetail == null) {
			return ;
		}
		gmDAO.updateBorrowerDetails(borrowerDetail, userId);	 
		 
		Log.log(Log.INFO,"GMProcessor","updateborrowerDetails","Exited");
   }
   
   /**
    * This method is to enter the recovery actions taken for a borrower. An object of 
    * type Recovery is passed as an argument to this method. This object is passed to 
    * the enterRecoveryDetails of GMDAO which stores the details in the database.
    * @param recoveryDetails
    * @return Boolean
    * @roseuid 397AD89D00E1
    */
   public void addRecoveryDetails(Recovery recoveryDetails) 
   												throws DatabaseException {
    	
		Log.log(Log.INFO,"GMProcessor","addRecoverDetails","Enetred");
    	GMDAO gmDAO = new GMDAO();
    	
    	if(recoveryDetails == null) {
    		return ;
    	}
    	gmDAO.updateRecoveryDetails(recoveryDetails);
		Log.log(Log.INFO,"GMProcessor","addRecoveryDetails","Exited");
    	return ;
   }
   

   public void modifyRecoveryDetails(Recovery recoveryDetails) 
												throws DatabaseException {
    	
		Log.log(Log.INFO,"GMProcessor","modifyRecoveryDetails","Enetred");
		GMDAO gmDAO = new GMDAO();
    	
		if(recoveryDetails == null) {
			return ;
		}
		gmDAO.modifyRecoveryDetails(recoveryDetails);
		Log.log(Log.INFO,"GMProcessor","modifyRecoveryDetails","Exited");
		return ;
   }
   


   /**
    * This method is used to retrieve the application details of an existing borrower 
    * with the given Borrower Id. This method is invoked when the user chooses to 
    * update any of the following and enters the Borrower Id to get the details:
    * 1. Borrower details
    * 2. Outstanding details
    * 3. Disbursement details
    * 4. Repayment details
    * 5. Repayment schedule
    * 6. Recovery details
    * 7. NPA details
    * 8. Periodic Info
    * 9. Closure details
    * In the display, only the details corresponding to the option selected will be 
    * available for the user to edit.
    * @param strParameter - This argument gives the information of whether the user 
    * gives the Member Id or Borrower Id or CGPAN as input.
    * @param strValue - This argument gives the value of the parameter.
    * @return com.cgtsi.application.Application
    * @roseuid 397AD89D00E3
    */
  /* public Application getApplicationDetails(String strParameter,
   											String strValue){
    return null;
   }*/
   
   /**
    * This method is used to generate a file that contains the periodic info that has 
    * to be entered by the MLIs. This file is sent to the MLI who will enter the 
    * necessary details and upload the Periodic Info through Thin Client. The file 
    * generated is sent to floppy or stored in hard disk as the user desires.
    * @param strParameter - This argument tells whether the user enters a Member Id 
    * or Borrower Id or CGPAN to get the application details.
    * @param strValue - This argument gives the value of the parameter.
    * @return Boolean
    * @roseuid 397AD89D00E6
    */
/*   public boolean exportPeriodicInfo(String id, int value)
   									 		   throws DatabaseException,
   									 		   SQLException {
    	gmDAO = new GMDAO();
	    
	    ArrayList outstandingDetails = null ;
		ArrayList disbursementDetails = null ;
		ArrayList repaymentDetails = null ;
		NPADetails npaDetails = null ;
		Recovery recoveryDetails = null ;
		ArrayList periodicInfoDetails = null ;
		
		
		if(id == null) {
			return false ;
		}
		
		outstandingDetails  = gmDAO.getOutstandingDetails(id, value);
    	
		disbursementDetails = gmDAO.getDisbursementDetails(id, value);
	    
		repaymentDetails = gmDAO.getRepaymentDetails(id, value);
		
		npaDetails = gmDAO.getNPADetails(id, value);
		
		recoveryDetails = gmDAO.getRecoveryDetails(id, value);
		
		if(outstandingDetails != null) {
			periodicInfoDetails.add(outstandingDetails);
		}
		if(disbursementDetails != null) {
			periodicInfoDetails.add(disbursementDetails);
		}
		if(repaymentDetails != null) {
			periodicInfoDetails.add(repaymentDetails);
		}
		if(npaDetails != null) {
			periodicInfoDetails.add(npaDetails);
		}
		if(recoveryDetails != null){
			periodicInfoDetails.add(recoveryDetails);
		}
		
		File file = storeDetailsInFile(periodicInfoDetails);
		
		return true;
   }*/
   
   /**
    * This method is used to update the outstanding details of all the CGPANs passed 
    * as an ArrayList of OutstandingDetail object.
    * @param osDtl - This parameter contains an array list of OutstandingDetail 
    * object entered in the screen by the user
    * @return Boolean
    * @roseuid 397AD89D00E9
    */
   public ArrayList updateTcOutstanding(ArrayList outstandingAmounts) 
   											throws DatabaseException,
   											           SQLException {
		Log.log(Log.INFO,"GMProcessor","updateOutstanding","Entered");   											 
   		GMDAO gmDAO = new GMDAO();
		OutstandingAmount outstandingAmount = null ;
   		int sizeOfOutstandingAmounts = 0 ;
   		
   	//	String cgpan = "" ;
  // 		ArrayList cgpans = new ArrayList() ;
   		
		if(outstandingAmounts == null) { 
			return null ;
		}
		sizeOfOutstandingAmounts = outstandingAmounts.size();
		for(int i = 0; i < sizeOfOutstandingAmounts; i++) {
			outstandingAmount = (OutstandingAmount)outstandingAmounts.get(i);
	
/*			if(tcOutstandingAmount == 0) {
				cgpan = outstandingAmount.getCgpan() ;
				cgpans.add(cgpan) ;				 
			}
*/			
			gmDAO.updateTcOutstandingDetails(outstandingAmount) ;			
		} 
		Log.log(Log.INFO,"GMProcessor","updateoutstanding","Exited");
		//return cgpans ;
		return null;
   }


   public ArrayList updateWcOutstanding(ArrayList outstandingAmounts) 
											throws DatabaseException,
													   SQLException {
		Log.log(Log.INFO,"GMProcessor","updateWcOutstanding","Entered");   											 
		GMDAO gmDAO = new GMDAO();
		OutstandingAmount outstandingAmount = null ;
		int sizeOfOutstandingAmounts = 0 ;
   		
	//	String cgpan = "" ;
  // 		ArrayList cgpans = new ArrayList() ;
   		
		if(outstandingAmounts == null) { 
			return null ;
		}
		sizeOfOutstandingAmounts = outstandingAmounts.size();
		for(int i = 0; i < sizeOfOutstandingAmounts; i++) {
			outstandingAmount = (OutstandingAmount)outstandingAmounts.get(i);
	
/*			if(tcOutstandingAmount == 0) {
				cgpan = outstandingAmount.getCgpan() ;
				cgpans.add(cgpan) ;				 
			}
*/			
			gmDAO.updateWcOutstandingDetails(outstandingAmount) ;			
		} 
		Log.log(Log.INFO,"GMProcessor","updateWcOutstanding","Exited");
		//return cgpans ;
		return null;
   }




   public ArrayList insertTcOutstanding(ArrayList outstandingAmounts) 
											throws DatabaseException,
													   SQLException {
		Log.log(Log.INFO,"GMProcessor","insertTcOutstanding","Entered");   											 
		GMDAO gmDAO = new GMDAO();
		OutstandingAmount outstandingAmount = null ;
		int sizeOfOutstandingAmounts = 0 ;
   		
		if(outstandingAmounts == null) { 
			return null ;
		}
		sizeOfOutstandingAmounts = outstandingAmounts.size();
		for(int i = 0; i < sizeOfOutstandingAmounts; i++) {
			outstandingAmount = (OutstandingAmount)outstandingAmounts.get(i);
			gmDAO.insertTcOutstandingDetails(outstandingAmount) ;			
		} 
		Log.log(Log.INFO,"GMProcessor","insertTcOutstanding","Exited");
		//return cgpans ;
		return null;
   }



   public ArrayList insertWcOutstanding(ArrayList outstandingAmounts) 
											throws DatabaseException,
													   SQLException {
		Log.log(Log.INFO,"GMProcessor","insertWcOutstanding","Entered");   											 
		GMDAO gmDAO = new GMDAO();
		OutstandingAmount outstandingAmount = null ;
		int sizeOfOutstandingAmounts = 0 ;
   		
		if(outstandingAmounts == null) { 
			return null ;
		}
		sizeOfOutstandingAmounts = outstandingAmounts.size();
		for(int i = 0; i < sizeOfOutstandingAmounts; i++) {
			outstandingAmount = (OutstandingAmount)outstandingAmounts.get(i);
			gmDAO.insertWcOutstandingDetails(outstandingAmount) ;			
		} 
		Log.log(Log.INFO,"GMProcessor","insertWcOutstanding","Exited");
		//return cgpans ;
		return null;
   }


   
   /**
    * This method is invoked when the user wants to shift an application or borrower 
    * to another operating office. It takes 3 arguments and passes the arguments to 
    * the shiftCgpanBorrower method of GMDAO.
    * 
    * @param id - This argument gives the value of the parameter. If the user wants 
    * to shift an application, then this argument will contain the Cgpan of the 
    * application. If the user wants to shift a borrower, then this argument will 
    * contain the value of the Cgbid.
    * 
    * @param type - This argument gives the information of whether the user wants to 
    * shift an application or a borrower to another operating office.
    * 0-CGBID
    * 1-CGPAN
    * 
    * @param destID - This argument gives the ID of the operating office to which the 
    * application or the borrower has to be shifted.
    * @return Boolean
    * @roseuid 397AD89D00EB
    */
  public boolean shiftCgpanBorrower(String srcId, String userId, String cgpan, String destId) 
   													throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","ShiftCgpanBorrower","Entered");
//    System.out.println("GMProcessor"+"ShiftCgpanBorrower"+"Entered");
       GMDAO gmDAO = new GMDAO() ;
       
       if(cgpan == null || destId.equals("")) {
       	  return false ;
       }
      
	   gmDAO.shiftCgpanBorrower(srcId, userId, cgpan, destId) ;
//     System.out.println("GMProcessor"+"ShiftCgpanBorrower"+"Exited");
	   Log.log(Log.INFO,"GMProcessor","ShiftCgpanBorrower","Exited");            
       return true;
   }
   
   /**
    * This method returns a Borrower object that contains the details of the 
    * Borrower. The inputs passed to the method are id and type.
    * 
    * If type is 0, id will represent member id.
    * 
    * If type is 1, id will represent borrower id
    * 
    * If type is 2, id will represent CGPAN. The borrower of the specific CGPAN will 
    * be fetched and displayed in this case.
    * 
    * @param id - It could be CGBID or CGPAN
    * @param type - indicates which id is part of parameter.
    * 0-CGBID
    * 1-CGPAN
    * @return com.cgtsi.application.Borrower
    * @roseuid 397BCBA20368
    */
   public BorrowerDetails viewBorrowerDetails(String memberId, String id, 
   									int type)throws DatabaseException, 
   									       	 NoDataException {
		Log.log(Log.INFO,"GMProcessor","viewBorrowerDetails","Entered");   									       					
    	GMDAO gmDAO = new GMDAO();
    	
		//To get the borrower details for the given id.
    	BorrowerDetails borrowerDetails = null;
    	
		
    	borrowerDetails = gmDAO.getBorrowerDetails(memberId, id, type);

		if(borrowerDetails == null) {
			throw new NoDataException("BorrowerDetails are not found");
		}
    	
		Log.log(Log.INFO,"GMProcessor","viewBorrowerDetails","Exited");  	
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
    * 
    * @param id - id could be anything. it could be CGBID, CGPAN, or MLI ID.
    * the type of id would be resolved by type parameter.
    * 0-CGBID
    * 1-CGPAN
    * @param type
    * @return ArrayList 
    * @roseuid 397BCE420094
    */
   public ArrayList viewOutstandingDetails(String id, int type) 
   										  throws DatabaseException,
   										  		SQLException,
   										      	NoUserFoundException {
		Log.log(Log.INFO,"GMProcessor","viewOutstandingDetails","Entered");   										      		
    	
    	GMDAO gmDAO = new GMDAO();
    	ArrayList periodicInfos = null ;
    	
    	
		periodicInfos  = gmDAO.getOutstandingDetails(id, type);

    	if(periodicInfos  == null){
    		throw new NoUserFoundException("User details not found") ;
    	}
    	
/*		for(int i=0; i < periodicInfos.size();++i)
		{
		   PeriodicInfo pr =(PeriodicInfo)periodicInfos.get(i);
		   ArrayList outDtls = pr.getOutstandingDetails();
		   Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","Size of out dtls : "+ outDtls.size());
		   for(int j=0; j<outDtls.size(); ++j)
		   {
		   		OutstandingDetail outDtl = (OutstandingDetail)outDtls.get(j);
		   		ArrayList outAmts = outDtl.getOutstandingAmounts();
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail Cgpan : "+ outDtl.getCgpan());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail Scheme : "+ outDtl.getScheme());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  tc Sanc Amout : "+ outDtl.getTcSanctionedAmount());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  Wc FB Sanc Amt : "+ outDtl.getWcFBSanctionedAmount());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  Wc NFB Sanc Amt : "+ outDtl.getWcNFBSanctionedAmount());
				
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","outAmts.size() : "+ outAmts.size());
			
		   		for(int k = 0; k<outAmts.size(); ++k)
			   	{	
			   	   	OutstandingAmount outAmt = (OutstandingAmount)outAmts.get(k); 
			   	   	Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Cgpan : "+ outAmt.getCgpan());
				
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmounttco Id  : "+ outAmt.getTcoId());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount WCO ID  : "+ outAmt.getWcoId());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc Pr Amt : "+ outAmt.getTcPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc Int Amt  : "+ outAmt.getTcInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc As on Dt : "+ outAmt.getTcOutstandingAsOnDate());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmountWc FB Int  Amt : "+ outAmt.getWcFBInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc FB Pr  Amt : "+ outAmt.getWcFBPrincipalOutstandingAmount ());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmountWc NFB Int  Amt : "+ outAmt.getWcNFBInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount  Wc NFB Pr  Amt : "+ outAmt.getWcNFBPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc NFB  Date : "+ outAmt.getWcFBOutstandingAsOnDate());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc FB  Date : "+ outAmt.getWcNFBOutstandingAsOnDate());
			   	}
			}
		}
*/
		Log.log(Log.INFO,"GMProcessor","viewOutstandingDetails","Exited");
    	return periodicInfos ;
   }
   
   /**
    * This method returns an arraylist of Disbursement object. The parameter passed 
    * to this object are id and type. 
    * 
    * If type = 0, the id passed will be borrower id. All the disbursement details 
    * for applications belonging to this borrower will be fetched in this case.
    * 
    * If type = 1, the id passed will be CGPAN. The corresponding disbursement 
    * details for application for this CGPAN will be fetched.
    * @param id - Id could be CGBID,CGPAN or MLI ID
    * Type of the id is resolved by the parameter 'type'
    * @param type
    * @return ArrayList
    * @roseuid 397BDED9037F
    */
   public ArrayList viewDisbursementDetails(String id, int type) 
   											throws DatabaseException
   											{
    	
		Log.log(Log.INFO,"GMProcessor","viewDisbursementDetails","Entered");
    	GMDAO gmDAO = new GMDAO(); 
		ArrayList periodicInfoList = new ArrayList() ;

		periodicInfoList = gmDAO.getDisbursementDetails(id, type);
		
/*		for(int i=0;i<periodicInfoList.size();++i)
		{
			PeriodicInfo pi = (PeriodicInfo)periodicInfoList.get(i);
			System.out.println("bid "+pi.getBorrowerId());
			System.out.println("name "+pi.getBorrowerName());
			System.out.println("disb size"+pi.getDisbursementDetails().size());
			ArrayList disbs = pi.getDisbursementDetails() ;
			for(int j=0;j<disbs.size();++j)
			{
				Disbursement d =(Disbursement)disbs.get(j);
				System.out.println("pan "+d.getCgpan());
				System.out.println("sanc amt "+d.getSanctionedAmount());
				System.out.println("D amt size "+d.getDisbursementAmounts().size());				 
				for(int k=0;k<disbs.size();++k)
				{
					DisbursementAmount da =(DisbursementAmount)disbs.get(k);
					System.out.println("pan "+da.getCgpan());
					System.out.println("d amt "+da.getDisbursementAmount());
					System.out.println("D date "+da.getDisbursementDate());				 
				}				
			}
			
		}
		*/
    	//if(periodicInfoList == null){
    		//System.out.println("list is null");
    	//	throw new NoDisbursementDetailsException("No disbursement details") ;
    	//}
		Log.log(Log.INFO,"GMProcessor","viewDisbursementDetails","Exited");
    	return periodicInfoList ;
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
    * @roseuid 397BE5850173
    */
   public ArrayList viewRepaymentSchedule(String id, int type)
   												 throws DatabaseException,
   												 NoRepaymentScheduleException {
		Log.log(Log.INFO,"GMProcessor","viewRepaymentSchedule","Entered");   												 	
		GMDAO gmDAO = new GMDAO(); 
		ArrayList repaymentSchedules ;

		Log.log(Log.DEBUG,"GMAction","inside processor --","Entered");
		repaymentSchedules = gmDAO.getRepaymentSchedule(id, type);
		
		if(repaymentSchedules == null ) {
			throw new NoRepaymentScheduleException("No repayment details found") ; 
		}
		
		Log.log(Log.INFO,"GMProcessor","viewRepaymentSchedule","Exited");
		return repaymentSchedules ;	

   }
   
   /**
    * This method returns an arraylist of Repayment object. The parameter passed to 
    * this object are id and type. 
    * 
    * If type = 0, the id passed will be borrower id. All the disbursement details 
    * for applications belonging to this borrower will be fetched in this case.
    * 
    * If type = 1, the id passed will be CGPAN. The corresponding disbursement 
    * details for application for this CGPAN will be fetched.
    * @param id - Id could be CGBID/CGPAN/MLI ID.
    * Identified by accompanying parameter 'type'
    * 0-CGBID
    * 1-CGPAN
    * @param type
    * @return ArrayList
    * @roseuid 397BFCB801F5
    */
   public ArrayList viewRepaymentDetails(String id, int type) 
   												throws DatabaseException
   												{
		Log.log(Log.INFO,"GMProcessor","viewRepaymentDetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		ArrayList periodicInfos = null ;
		
		periodicInfos = gmDAO.getRepaymentDetails(id, type);
		
		Log.log(Log.INFO,"GMProcessor","viewRepaymentDetails","Exited");
		return periodicInfos;
   }
   
   /**
    * This method is to save the repayment details. An arraylist of object type 
    * RepaymentSchedule is passed as an argument to this method. For each element of 
    * the arraylist, the RepaymentSchedule object is passed to the 
    * UpdateRepaymentSchedule of GMDAO which saves the details in the database.
    * @param repaySchedule
    * @return boolean
    * @roseuid 397C008A0093
    */
   public void updateRepaymentSchedule(ArrayList repaymentSchedules, String userId) 
   													throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","updateRepaymentSchedule","Entered");
		GMDAO gmDAO = new GMDAO();
		int sizeOfrepaymentSchedules = 0 ;
		
		if(repaymentSchedules == null) {
			return ;
		}
		
		sizeOfrepaymentSchedules = repaymentSchedules.size(); 
		for(int i = 0; i < sizeOfrepaymentSchedules; i++) { 
			gmDAO.updateRepaymentSchedule((RepaymentSchedule)
													 repaymentSchedules.get(i), userId);
			Log.log(Log.DEBUG,"GMProcessor","updateRepaymentSchedule","i=>"+i);
		} 
		Log.log(Log.INFO,"GMProcessor","updateRepaymentSchedule","Exited");
   }
   
   
   public HashMap viewClosureDetails(String id,int type,String memberId) throws DatabaseException {
   		
		Log.log(Log.INFO,"GMProcessor","viewClosureDetails","Entered");
		
   		GMDAO gmDao = new GMDAO();
   		HashMap closureDetails = null;
   		
		Log.log(Log.DEBUG,"GMProcessor","viewClosureDetails","ID : "+id);
		
   		closureDetails = gmDao.getClosureDetails(id, type,memberId);
   		
		Log.log(Log.DEBUG,"GMProcessor","viewClosureDetails","size of closure" +
											" details arr :"+closureDetails.size());
		
		Log.log(Log.INFO,"GMProcessor","viewClosureDetails","Exited");
		
   	    return closureDetails;		 
   	    
   }

public HashMap getClosureDetailsForFeeNotPaid(String cgpan) throws DatabaseException {
   		
	 Log.log(Log.INFO,"GMProcessor","getClosureDetailsForFeeNotPaid","Entered");
		
	 GMDAO gmDao = new GMDAO();
	 HashMap closureDetails = null;
   		
	 Log.log(Log.DEBUG,"GMProcessor","getClosureDetailsForFeeNotPaid","cgpan :"+cgpan);
		
	 closureDetails = gmDao.getClosureDetailsForFeeNotPaid(cgpan);
   		
	 Log.log(Log.DEBUG,"GMProcessor","getClosureDetailsForFeeNotPaid","size of closure" +
										 " details arr :"+closureDetails.size());
		
	 Log.log(Log.INFO,"GMProcessor","getClosureDetailsForFeeNotPaid","Exited");
		
	 return closureDetails;		 
   	    
}


   
   public ArrayList getRecoveryDetails(String borrowerId) 
									throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","getRecoveryDetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		ArrayList recoveryDetails = null ;
		
/*		if (borrowerId.equals("")) {
			return null ;
		}
*/		
		recoveryDetails = gmDAO.getRecoveryDetails(borrowerId);
		
		Log.log(Log.INFO,"GMProcessor","getRecoveryDetails","Exited");
		return recoveryDetails ;
   }

   public NPADetails getNPADetails(String borrowerId) throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","getNPADetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		NPADetails npaDetails = null ;
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Borrower Id : "+borrowerId);
		
	/*	if (borrowerId.equals("")) {
			//System.out.println("borrowerId null");
			return null ;
		}
		*/
		
		npaDetails = gmDAO.getNPADetails(borrowerId);
		if(npaDetails==null) {
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","npaDetails==null");
		}
/*		if(npaDetails!=null) {
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Reco Procs size : "+npaDetails.getRecoveryProcedure().size());
		
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","no Of Actions"+npaDetails.getNoOfActions());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","BID "+npaDetails.getCgbid());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA date"+npaDetails.getNpaDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","OS amt"+npaDetails.getOsAmtOnNPA());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA reported"+npaDetails.getWhetherNPAReported());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Rep Date "+npaDetails.getReportingDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA reason "+npaDetails.getNpaReason());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","willful defaulter "+npaDetails.getWillfulDefaulter());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","efforts taken "+npaDetails.getEffortsTaken());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","is reco initiated "+npaDetails.getIsRecoveryInitiated());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","effortsd conlcusion Date"+npaDetails.getEffortsConclusionDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","mli comment on Fin "+npaDetails.getMliCommentOnFinPosition());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","DTLs of Fin Pos"+npaDetails.getDetailsOfFinAssistance());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Credit support"+npaDetails.getCreditSupport());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Bank Facility "+npaDetails.getBankFacilityDetail());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Watch List"+npaDetails.getPlaceUnderWatchList());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Remarks On npa "+npaDetails.getRemarksOnNpa());
		
		LegalSuitDetail legalSuitDetail = npaDetails.getLegalSuitDetail();
		if(legalSuitDetail != null){
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getCourtName"+legalSuitDetail.getCourtName());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getLegalSuiteNo "+legalSuitDetail.getLegalSuiteNo());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getDtOfFilingLegalSuit "+legalSuitDetail.getDtOfFilingLegalSuit());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getForumName "+legalSuitDetail.getForumName());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getLocation "+legalSuitDetail.getLocation());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getAmountClaimed "+legalSuitDetail.getAmountClaimed());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getCurrentStatus"+legalSuitDetail.getCurrentStatus());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getRecoveryProceedingsConcluded "+legalSuitDetail.getRecoveryProceedingsConcluded());
		} 
 		ArrayList recoveryProcedures = npaDetails.getRecoveryProcedure(); 
 		RecoveryProcedure reco = null;
 		for(int i=0; i<recoveryProcedures.size(); ++i){ 
 			reco = (RecoveryProcedure)recoveryProcedures.get(i);
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionDate"+reco.getActionDate());
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionType"+reco.getActionType() );
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getAttachmentName"+reco.getAttachmentName() );
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getNpaId"+reco.getNpaId());
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionDetails"+reco.getActionDetails());
 		} 
	}*/
				
		Log.log(Log.INFO,"GMProcessor","getNPADetails","Exited");
		return npaDetails ;
   }
   
   
   public ArrayList getAllActions() throws DatabaseException
   {
		Log.log(Log.INFO,"GMProcessor","getAllActions","Entered");
	   	GMDAO gmDAO = new GMDAO();
	   	ArrayList actions = null;
	   	
	   	actions = gmDAO.getAllActions();
	   	
		Log.log(Log.INFO,"GMProcessor","getAllActions","Exited");
	   	return actions;
   }



   public ArrayList getAllReasonsForClosure() throws DatabaseException
   {
		Log.log(Log.INFO,"GMProcessor","getAllReasonsForClosure","Entered");
		GMDAO gmDAO = new GMDAO();
		ArrayList closureReasons = null;
	   	
		closureReasons = gmDAO.getAllReasonsForClosure();
	   	
		Log.log(Log.INFO,"GMProcessor","getAllReasonsForClosure","Exited");
		return closureReasons;
   }



   public CgpanDetail getCgpanDetails(String cgpan) throws DatabaseException
   {
		Log.log(Log.INFO,"GMProcessor","getCgpanDetails","Entered");
		GMDAO gmDAO = new GMDAO();
		CgpanDetail cgpanDtl = null;	   	
		cgpanDtl = gmDAO.getCgpanDetails(cgpan);
	   	
		Log.log(Log.INFO,"GMProcessor","getCgpanDetails","Exited");
		return cgpanDtl;
   }

   
   /**
    * This method will store all the Periodic info details in a file and returns the 
    * file object.
    * @param Collection
    * @return File
    * @roseuid 39B21D65009C
    */
/*   public File storeDetailsInFile(Collection Collection) {
    	
    return null;
   }
   
   /**
    * This method fetches all the member ids and returns an array of string.
    * @return Array
    * @roseuid 39B2383B01E4
    */
   public String getBorrowerIdForBorrowerName(String borrowerName)
   				 throws DatabaseException, NoDataException {
   	   GMDAO gmDAO = new GMDAO() ;
   	   String borrowerId = null ;
   	   
	   borrowerId =  gmDAO.getBorrowerIdForBorrowerName(borrowerName) ;
   	   if(borrowerId == null){
   	       throw new NoDataException("No borrower id found for the borrower Name") ;
   	   }
   	   return borrowerId ;   	   
   }
   
   /**
    * This method retrieves all the borrower ids for the given member id.
    * @param MemberId
    * @return Array
    * @roseuid 39B2394400AB
    */
   public  ArrayList getBorrowerIds(String memberId) throws DatabaseException
   												 {
	   Log.log(Log.INFO,"GMProcessor","getBorrowerIds","Entered");
   //  System.out.println("GMProcessor"+"getBorrowerIds"+"Entered");
       GMDAO gmDAO = new GMDAO() ;
       ArrayList borrowerIds = null ;       
       
       borrowerIds =  gmDAO.getBorrowerIds(memberId) ;
   //    System.out.println("GMProcessor"+"getBorrowerIds"+"Exited");
	   Log.log(Log.INFO,"GMProcessor","getBorrowerIds","Exited");
       return borrowerIds;
   }
   /**
   * 
   * @param cgpanToShift
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
     public  String getMemIdforCgpan(String cgpanToShift) throws DatabaseException
   												 {
	   Log.log(Log.INFO,"GMProcessor","getMemIdforCgpan","Entered");
   //  System.out.println("GMProcessor"+"getMemIdforCgpan"+"Entered");
       GMDAO gmDAO = new GMDAO() ;
       String memIdforCgpan = null ;       
       
       memIdforCgpan =  gmDAO.getMemIdforCgpan(cgpanToShift) ;
   //    System.out.println("GMProcessor"+"getMemIdforCgpan"+"Exited");
	   Log.log(Log.INFO,"GMProcessor","getMemIdforCgpan","Exited");
       return memIdforCgpan;
   }
   /**
   * 
   * @param cgpanToShift
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   */
    public  String getallocationStatusforCgpan(String cgpanToShift) throws DatabaseException
   												 {
	   Log.log(Log.INFO,"GMProcessor","getallocationStatusforCgpan","Entered");
  //   System.out.println("GMProcessor"+"getallocationStatusforCgpan"+"Entered");
       GMDAO gmDAO = new GMDAO() ;
       String memIdforCgpan = null ;       
       
       memIdforCgpan =  gmDAO.getallocationStatusforCgpan(cgpanToShift) ;
    //   System.out.println("GMProcessor"+"getallocationStatusforCgpan"+"Exited");
	   Log.log(Log.INFO,"GMProcessor","getallocationStatusforCgpan","Exited");
       return memIdforCgpan;
   }
   /**
    * This method retrieves  the CGPANs for the given borrower  id.
    * @param BorrowerId
    * @return Array
    * @roseuid 39B23A10036B
    */
   public Vector getCGPANs(String borrowerId) throws DatabaseException,
   												NoDataException {

		Log.log(Log.INFO,"GMProcessor","getCGPANs","Entered");
      	GMDAO gmDAO = new GMDAO() ;
		Vector cgpans = gmDAO.getCGPANDetailsForBId(borrowerId); 
	  	Log.log(Log.INFO,"GMProcessor","getCGPANs","Exited");
     	return cgpans;	
      
   }
   
   /**
    * This method retrieves all the member ids belonging to the same operating office 
    * and returns an array of string.
    * @param MemberId
    * @return Array
    * @roseuid 39B23CE10271
    */
/*   public Array getOtherMemberIds(String memberId) 
   										throws DatabaseException,
												NoDataException {       
       gmDAO = new GMDAO() ;
       Array otherMemberIds = null ;        
       
	   if (memberId.equals("")) {
		  return null ;
	   }
       otherMemberIds =  gmDAO.getOtherMemberIds(memberId) ;
       if(otherMemberIds == null) {
           throw new NoDataException ("to member id not found") ;
       }
       return otherMemberIds ;
   }*/
/*    public static void main(String args[]) throws Exception, DatabaseException {
    	GMProcessor gmProcessor = new GMProcessor();
     	
    	java.util.Hashtable closureDtls = gmProcessor.viewClosureDetails("000100000000",2);  
		String memberId = "";
		String bId = "";
		String bname="";
		String cgpan=""; 
		String scheme = "";
		double amt = 0;
		
		java.util.Set memberSet = closureDtls.keySet();
		java.util.Iterator memberIterator = memberSet.iterator();
		Hashtable binfos = new Hashtable();
		ArrayList cgpanInfos = new ArrayList();
		System.out.println("***************************************");
		while(memberIterator.hasNext()){
			 memberId = (String)memberIterator.next();
			 System.out.println("member Id "+ memberId);
			 
			 binfos = (Hashtable)closureDtls.get(memberId);
			 java.util.Set bSet = binfos.keySet() ;
			 java.util.Iterator bIterator = bSet.iterator() ;
			 while(bIterator.hasNext()) {
			 	bId = (String)bIterator.next(); 
				System.out.println("borrId "+ bId);
				
				BorrowerInfo borrInfo = (BorrowerInfo)binfos.get(bId);
				bname = borrInfo.getBorrowerName() ;
				System.out.println("bname"+ bname);
				System.out.println("------------------");
				cgpanInfos = borrInfo.getCgpanInfos(); 
				for(int i = 0;i< cgpanInfos.size(); ++i){
					CgpanInfo cg = (CgpanInfo)cgpanInfos.get(i);
					cgpan = cg.getCgpan() ;
					scheme = cg.getScheme() ;
					amt = cg.getSanctionedAmount() ;
					System.out.println("====cgpans ===");
					System.out.println("cg "+ cgpan);
					
					System.out.println("scheme "+ scheme);
					System.out.println("amount "+ amt);
				}
				
			 }
			System.out.println("End of one ht");
   		}
   		
    }*/

 	public ArrayList uploadGmApplication(Hashtable gmApp,String userId,String bankId,
				  String zneId, String brnId) throws DatabaseException, SQLException, NoUserFoundException, NoApplicationFoundException
 {
	 Log.log(Log.INFO,"GMProcessor","uploadGmApplication","Entered");
    	
	 GMDAO gmDAO= new GMDAO();
	 Set borrowerIdSet  = gmApp.keySet();
	 Iterator borrowerIdIterator = borrowerIdSet.iterator();
    	
	 String borrowerIdKey = null;
	 ArrayList outstandingAmounts = null;
	 String memberId = bankId+zneId+brnId;
	 //System.out.println("memberId:"+memberId);
	 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","memberId:"+memberId);
	 ArrayList borrowerIds = gmDAO.getBorrowerIds(memberId);
	 //System.out.println("borrowerIds:"+borrowerIds);
	 ArrayList errorMessages = new ArrayList();
	 ArrayList periodicInfoDetails = new ArrayList();

	Administrator administrator = new Administrator();
	User user = administrator.getUserInfo(userId);
	String fromId=user.getEmailId();
	
	ArrayList cgpansToBeClosed=new ArrayList();	 
		
    	
	 while(borrowerIdIterator.hasNext())
	 {
	 	cgpansToBeClosed=new ArrayList();
		 borrowerIdKey = (String) borrowerIdIterator.next();
		 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","borrowerIdKey:"+borrowerIdKey);
		 //System.out.println("Borrowerid:"+borrowerIdKey);
		 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","OSDetails-"+"borrower Id:"+borrowerIdKey);
		 if (borrowerIds.contains(borrowerIdKey))
		 {
			 //System.out.println("Borrowerid:"+borrowerIdKey);
			 PeriodicInfo periodicInfo = (PeriodicInfo)gmApp.get(borrowerIdKey);
			if(periodicInfo.getIsVerified())         
			{
				
				//NPA Details

				NPADetails  npaDetailsTC = periodicInfo.getNpaDetails();
				double amountClaimedTC =0; 
				String courtNameTC = null;
				String currentStatusTC =null ;
				Date dtOfFilingLegalSuitTC = null;
				String forumNameTC = null;
				String legalSuiteNoTC = null;
				String locationTC = null;
				String npaIdTC1 = null;
				String recoveryProceedingsConcludedTC = null;						

				if (npaDetailsTC!=null)
				{
					String npaIdTC = npaDetailsTC.getNpaId();
					String cgbidTC = npaDetailsTC.getCgbid();
					String bankFacilityDetailTC = npaDetailsTC.getBankFacilityDetail();
					String creditSupportTC = npaDetailsTC.getCreditSupport();
					String detailsOfFinAssistanceTC = npaDetailsTC.getDetailsOfFinAssistance();
					Date effortsConclusionDateTC = npaDetailsTC.getEffortsConclusionDate();
					String effortsTakenTC = npaDetailsTC.getEffortsTaken();
					String isRecoveryInitiatedTC = npaDetailsTC.getIsRecoveryInitiated();
					String mliCommentOnFinPositionTC = npaDetailsTC.getMliCommentOnFinPosition();
					int noOfActionsTC = npaDetailsTC.getNoOfActions();
					Date npaDateTC = npaDetailsTC.getNpaDate();
					String npaReasonTC = npaDetailsTC.getNpaReason();
					double osAmtOnNPATC = npaDetailsTC.getOsAmtOnNPA();
					String placeUnderWatchListTC = npaDetailsTC.getPlaceUnderWatchList();
					String referenceTC = npaDetailsTC.getReference();
					Date reportingDateTC = npaDetailsTC.getReportingDate();
					String remarksOnNpaTC = npaDetailsTC.getRemarksOnNpa();
					String whetherNPAReportedTC = npaDetailsTC.getWhetherNPAReported();
					String willfulDefaulterTC = npaDetailsTC.getWillfulDefaulter();
				 
					LegalSuitDetail legalSuitDetailTC = npaDetailsTC.getLegalSuitDetail();
					if((legalSuitDetailTC !=  null) && (legalSuitDetailTC.getCourtName() != null))
					{
						amountClaimedTC = legalSuitDetailTC.getAmountClaimed(); 
						courtNameTC = legalSuitDetailTC.getCourtName();
						currentStatusTC = legalSuitDetailTC.getCurrentStatus();
						dtOfFilingLegalSuitTC = legalSuitDetailTC.getDtOfFilingLegalSuit();
						forumNameTC = legalSuitDetailTC.getForumName();
						legalSuiteNoTC = legalSuitDetailTC.getLegalSuiteNo();
						locationTC = legalSuitDetailTC.getLocation();
						npaIdTC1 = legalSuitDetailTC.getNpaId();
						recoveryProceedingsConcludedTC = legalSuitDetailTC.getRecoveryProceedingsConcluded();						
					}

		 		 			
					NPADetails  npaDetailsGM = gmDAO.getNPADetails(borrowerIdKey);
					
				   if(npaDetailsGM!=null)
				   {
					   ////System.out.println("---------------------");
					   //System.out.println("npaDetailsGM not null");
					   String npaIdGM = npaDetailsGM.getNpaId();
					   //System.out.println("npaIdGM:"+npaIdGM);			
					   if(npaIdTC!=null)
					   {
						//System.out.println("npaIdTC not null");
						if(npaIdTC.equals(npaIdGM))
						{			
						  // System.out.println("npaIdTC == npaIdGM, hence update");
							//update
							NPADetails npaDetailsUpdate = new NPADetails();
							npaDetailsUpdate.setBankFacilityDetail(bankFacilityDetailTC);
							npaDetailsUpdate.setCgbid(borrowerIdKey);
							//System.out.println("borrowerIdKey:"+borrowerIdKey);
							npaDetailsUpdate.setCreditSupport(creditSupportTC);
							npaDetailsUpdate.setDetailsOfFinAssistance(detailsOfFinAssistanceTC);
							npaDetailsUpdate.setEffortsConclusionDate(effortsConclusionDateTC);
							npaDetailsUpdate.setEffortsTaken(effortsTakenTC);
							npaDetailsUpdate.setIsRecoveryInitiated(isRecoveryInitiatedTC);
							npaDetailsUpdate.setMliCommentOnFinPosition(mliCommentOnFinPositionTC);
							npaDetailsUpdate.setNoOfActions(noOfActionsTC);
							npaDetailsUpdate.setNpaDate(npaDateTC);
							npaDetailsUpdate.setNpaId(npaIdTC);
							npaDetailsUpdate.setNpaReason(npaReasonTC);
							npaDetailsUpdate.setOsAmtOnNPA(osAmtOnNPATC);
							npaDetailsUpdate.setPlaceUnderWatchList(placeUnderWatchListTC);
							npaDetailsUpdate.setReference(referenceTC);
							npaDetailsUpdate.setRemarksOnNpa(remarksOnNpaTC);
							npaDetailsUpdate.setReportingDate(reportingDateTC);
							npaDetailsUpdate.setWhetherNPAReported(whetherNPAReportedTC);
							npaDetailsUpdate.setWillfulDefaulter(willfulDefaulterTC);
							gmDAO.updateNpaForUpload(npaDetailsUpdate);
							//System.out.println("npa Details Updated");
						 
						   if (isRecoveryInitiatedTC.equalsIgnoreCase("Y"))
						   {
								LegalSuitDetail legalSuitDetailUpdate = new LegalSuitDetail();
								if((legalSuitDetailTC !=  null) && (legalSuitDetailTC.getCourtName() != null))
								{
									 legalSuitDetailUpdate.setAmountClaimed(amountClaimedTC);
									 legalSuitDetailUpdate.setCourtName(courtNameTC);
									 legalSuitDetailUpdate.setCurrentStatus(currentStatusTC);
									 legalSuitDetailUpdate.setDtOfFilingLegalSuit(dtOfFilingLegalSuitTC);
									 legalSuitDetailUpdate.setForumName(forumNameTC);
									 legalSuitDetailUpdate.setLegalSuiteNo(legalSuiteNoTC);
									 legalSuitDetailUpdate.setLocation(locationTC);
									 legalSuitDetailUpdate.setNpaId(npaIdTC);
									 //System.out.println("npaIdTC:"+npaIdTC);
									 legalSuitDetailUpdate.setRecoveryProceedingsConcluded(recoveryProceedingsConcludedTC);
									 //System.out.println("BE4 updation");
									 gmDAO.updateLegalForUpload(legalSuitDetailUpdate);
									 //System.out.println("legal Details Updated"); 	 					   	
								}
								ArrayList recoveryProcedureTC = npaDetailsTC.getRecoveryProcedure();
								int recoveryProcedureTCSize = recoveryProcedureTC.size();
								for(int z=0; z<recoveryProcedureTCSize ; z++)
								{
									RecoveryProcedure recoveryProcedureObjTC = (RecoveryProcedure) recoveryProcedureTC.get(z);
									RecoveryProcedure recoveryProcedureObjFinalTC = new RecoveryProcedure();
									Date actionDate = recoveryProcedureObjTC.getActionDate();
									String actionDetails = recoveryProcedureObjTC.getActionDetails();
									String actionType = recoveryProcedureObjTC.getActionType();
									String attachmentName = recoveryProcedureObjTC.getAttachmentName();
									String npaIdTC2 = recoveryProcedureObjTC.getNpaId();
									String radId = recoveryProcedureObjTC.getRadId();
									recoveryProcedureObjFinalTC.setActionDate(recoveryProcedureObjTC.getActionDate());
									recoveryProcedureObjFinalTC.setActionDetails(recoveryProcedureObjTC.getActionDetails());
									recoveryProcedureObjFinalTC.setActionType(recoveryProcedureObjTC.getActionType());
									recoveryProcedureObjFinalTC.setAttachmentName(recoveryProcedureObjTC.getAttachmentName());
									recoveryProcedureObjFinalTC.setNpaId(recoveryProcedureObjTC.getNpaId());
								
									if(radId==null || radId.equals(""))
									{
									
										gmDAO.insertRecAxnForUpload(recoveryProcedureObjFinalTC);
										//System.out.println("recovery Procedure Updated");
									
									}else{
										recoveryProcedureObjFinalTC.setRadId(radId);
									
										gmDAO.updateRecAxnForUpload(recoveryProcedureObjFinalTC);
										//System.out.println("recovery Procedure Updated");					
									}					
								}
						   }
						}
					}
					
				   else if(npaIdTC==null)
				   {//insert
					   //System.out.println("--------------------");
					   //System.out.println("npaIdTC==null, hence insert");
					   NPADetails npaDetailsInsert = new NPADetails();
					   npaDetailsInsert.setBankFacilityDetail(bankFacilityDetailTC);
					   npaDetailsInsert.setCgbid(borrowerIdKey);
					   npaDetailsInsert.setCreditSupport(creditSupportTC);
					   npaDetailsInsert.setDetailsOfFinAssistance(detailsOfFinAssistanceTC);
					   npaDetailsInsert.setEffortsConclusionDate(effortsConclusionDateTC);
					   npaDetailsInsert.setEffortsTaken(effortsTakenTC);
					   npaDetailsInsert.setIsRecoveryInitiated(isRecoveryInitiatedTC);
					   npaDetailsInsert.setMliCommentOnFinPosition(mliCommentOnFinPositionTC);
					   npaDetailsInsert.setNoOfActions(noOfActionsTC);
					   npaDetailsInsert.setNpaDate(npaDateTC);
						//System.out.println("npaDateTC:"+npaDateTC);
					   npaDetailsInsert.setNpaReason(npaReasonTC);
					   npaDetailsInsert.setOsAmtOnNPA(osAmtOnNPATC);
					   npaDetailsInsert.setPlaceUnderWatchList(placeUnderWatchListTC);
					   npaDetailsInsert.setReference(referenceTC);
					   npaDetailsInsert.setRemarksOnNpa(remarksOnNpaTC);
					   npaDetailsInsert.setReportingDate(reportingDateTC);
					   npaDetailsInsert.setWhetherNPAReported(whetherNPAReportedTC);
					   npaDetailsInsert.setWillfulDefaulter(willfulDefaulterTC);
					   String npaId = gmDAO.insertNpaForUpload(npaDetailsInsert);
					   //System.out.println("Npa Details inserted:"+npaId);
					 
					  if (isRecoveryInitiatedTC.equalsIgnoreCase("Y"))
					  {
							LegalSuitDetail legalSuitDetailInsert = new LegalSuitDetail();
							legalSuitDetailInsert.setAmountClaimed(amountClaimedTC);
							legalSuitDetailInsert.setCourtName(courtNameTC);
							legalSuitDetailInsert.setCurrentStatus(currentStatusTC);
							legalSuitDetailInsert.setDtOfFilingLegalSuit(dtOfFilingLegalSuitTC);
							legalSuitDetailInsert.setForumName(forumNameTC);
							legalSuitDetailInsert.setLegalSuiteNo(legalSuiteNoTC);
							legalSuitDetailInsert.setLocation(locationTC);
							legalSuitDetailInsert.setNpaId(npaId);
							//System.out.println("npaId:"+npaId);
							legalSuitDetailInsert.setRecoveryProceedingsConcluded(recoveryProceedingsConcludedTC);
							gmDAO.insertLegalForUpload(legalSuitDetailInsert);
							//System.out.println("Legal Details inserted");
						
							ArrayList recoveryProcedureTC = npaDetailsTC.getRecoveryProcedure();
							int recoveryProcedureTCSize = recoveryProcedureTC.size();
							for(int z=0; z<recoveryProcedureTCSize ; z++)
							{
								RecoveryProcedure recoveryProcedureObjTC = (RecoveryProcedure) recoveryProcedureTC.get(z);
								RecoveryProcedure recoveryProcedureObjFinalTC = new RecoveryProcedure();
								Date actionDate = recoveryProcedureObjTC.getActionDate();
								String actionDetails = recoveryProcedureObjTC.getActionDetails();
								String actionType = recoveryProcedureObjTC.getActionType();
								String attachmentName = recoveryProcedureObjTC.getAttachmentName();
				 //			   String radId = recoveryProcedureObjTC.getRadId();
			
								recoveryProcedureObjFinalTC.setActionDate(recoveryProcedureObjTC.getActionDate());
								recoveryProcedureObjFinalTC.setActionDetails(recoveryProcedureObjTC.getActionDetails());
								recoveryProcedureObjFinalTC.setActionType(recoveryProcedureObjTC.getActionType());
								recoveryProcedureObjFinalTC.setAttachmentName(recoveryProcedureObjTC.getAttachmentName());
								recoveryProcedureObjFinalTC.setNpaId(npaId);			
			
								gmDAO.insertRecAxnForUpload(recoveryProcedureObjFinalTC);
								 //System.out.println("Recovery procedure inserted");	
							}
					  }
				   }
			   }
			
			   else
			   {
				   //System.out.println("---------------------");
				   //System.out.println("Insert as repayIdGM is null");
				   NPADetails npaDetailsInsert = new NPADetails();
				   npaDetailsInsert.setBankFacilityDetail(bankFacilityDetailTC);
				   npaDetailsInsert.setCgbid(borrowerIdKey);
				   //System.out.println("borrowerIdKey:"+borrowerIdKey);
				   npaDetailsInsert.setCreditSupport(creditSupportTC);
				   npaDetailsInsert.setDetailsOfFinAssistance(detailsOfFinAssistanceTC);
				   npaDetailsInsert.setEffortsConclusionDate(effortsConclusionDateTC);
				   npaDetailsInsert.setEffortsTaken(effortsTakenTC);
				   npaDetailsInsert.setIsRecoveryInitiated(isRecoveryInitiatedTC);
				   npaDetailsInsert.setMliCommentOnFinPosition(mliCommentOnFinPositionTC);
				   npaDetailsInsert.setNoOfActions(noOfActionsTC);
				   npaDetailsInsert.setNpaDate(npaDateTC);
				   npaDetailsInsert.setNpaReason(npaReasonTC);
				   npaDetailsInsert.setOsAmtOnNPA(osAmtOnNPATC);
				   npaDetailsInsert.setPlaceUnderWatchList(placeUnderWatchListTC);
				   npaDetailsInsert.setReference(referenceTC);
				   npaDetailsInsert.setRemarksOnNpa(remarksOnNpaTC);
				   npaDetailsInsert.setReportingDate(reportingDateTC);
				   npaDetailsInsert.setWhetherNPAReported(whetherNPAReportedTC);
				   npaDetailsInsert.setWillfulDefaulter(willfulDefaulterTC);
				   String npaId = gmDAO.insertNpaForUpload(npaDetailsInsert);
				   //System.out.println("npa details inserted:"+npaId);
		 
				  if (isRecoveryInitiatedTC.equalsIgnoreCase("Y"))
				  {
					LegalSuitDetail legalSuitDetailInsert = new LegalSuitDetail();
					legalSuitDetailInsert.setAmountClaimed(amountClaimedTC);
					legalSuitDetailInsert.setCourtName(courtNameTC);
					legalSuitDetailInsert.setCurrentStatus(currentStatusTC);
					legalSuitDetailInsert.setDtOfFilingLegalSuit(dtOfFilingLegalSuitTC);
					legalSuitDetailInsert.setForumName(forumNameTC);
					legalSuitDetailInsert.setLegalSuiteNo(legalSuiteNoTC);
					legalSuitDetailInsert.setLocation(locationTC);
					legalSuitDetailInsert.setNpaId(npaId);
					legalSuitDetailInsert.setRecoveryProceedingsConcluded(recoveryProceedingsConcludedTC);
					gmDAO.insertLegalForUpload(legalSuitDetailInsert);
					//System.out.println("legal details inserted");
		
					ArrayList recoveryProcedureTC = npaDetailsTC.getRecoveryProcedure();
					int recoveryProcedureTCSize = recoveryProcedureTC.size();
					//System.out.println("rec action size " + recoveryProcedureTCSize);
					for(int z=0; z<recoveryProcedureTCSize ; z++)
					{
						RecoveryProcedure recoveryProcedureObjTC = (RecoveryProcedure) recoveryProcedureTC.get(z);
						RecoveryProcedure recoveryProcedureObjFinalTC = new RecoveryProcedure();
						Date actionDate = recoveryProcedureObjTC.getActionDate();
						String actionDetails = recoveryProcedureObjTC.getActionDetails();
						String actionType = recoveryProcedureObjTC.getActionType();
						String attachmentName = recoveryProcedureObjTC.getAttachmentName();
		 //			   String radId = recoveryProcedureObjTC.getRadId();

						recoveryProcedureObjFinalTC.setActionDate(recoveryProcedureObjTC.getActionDate());
						recoveryProcedureObjFinalTC.setActionDetails(recoveryProcedureObjTC.getActionDetails());
						recoveryProcedureObjFinalTC.setActionType(recoveryProcedureObjTC.getActionType());
						recoveryProcedureObjFinalTC.setAttachmentName(recoveryProcedureObjTC.getAttachmentName());
						recoveryProcedureObjFinalTC.setNpaId(npaId);
//							recoveryProcedureObjFinalTC.setRadId(radId); 

						gmDAO.insertRecAxnForUpload(recoveryProcedureObjFinalTC);
					   // System.out.println("Recovery procedure inserted");	
					}
				  }
			   }
			}			 
	 			
		 //Disbursement Details
		 //System.out.println("--------Disbursement Details-----------");
		 ArrayList disDtlsThinClient = periodicInfo.getDisbursementDetails();
		 //System.out.println("disDtlsThinClient:"+disDtlsThinClient);
		 int disDtlsThinClientSize = disDtlsThinClient.size();
		 //System.out.println("disDtlsThinClient:"+disDtlsThinClient);
		 ArrayList disbDtlsGM = gmDAO.getDisbursementDetails(borrowerIdKey,GMConstants.TYPE_ZERO);//from DB
		 //System.out.println("disbDtlsGM:"+disbDtlsGM); 
		 if (disDtlsThinClient!=null)
		 {
			 int disDtlsSize = disDtlsThinClient.size();
			 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","disDtlsTCSize:"+disDtlsSize);			 
			 for(int i=0; i < disDtlsSize ; i++)  
			 {
			 	
				 //get Disbursement object from Disbursement ArrayList for ThinClient
				 Disbursement disDetailThinClient= (Disbursement) disDtlsThinClient.get(i);
				 //String BidThinClient = periodicInfo.getBorrowerId();
				 String cgpanThinClient = disDetailThinClient.getCgpan();
				 //System.out.println("cgpanThinClient:"+cgpanThinClient);
				 String schemeThinClient = disDetailThinClient.getScheme();
				 //System.out.println("schemeThinClient:"+schemeThinClient);
				 ArrayList disDtlsArrayThinClient = disDetailThinClient.getDisbursementAmounts();
				 if(disDtlsArrayThinClient != null)
				 {
	//				   System.out.println("disDtlsArrayThinClient:"+disDtlsArrayThinClient);
					   int disDtlsArrayThinClientSize = disDtlsArrayThinClient.size();
					   //System.out.println("disDtlsArrayThinClientSize:"+disDtlsArrayThinClientSize);
					   for(int x=0; x < disDtlsArrayThinClientSize ;x++)  
					   {
						   ////System.out.println("******************");
						   DisbursementAmount dAmountTC = (DisbursementAmount) disDtlsArrayThinClient.get(x);
						   String disIdThinClient = dAmountTC.getDisbursementId();
						   //System.out.println("disIdThinClient:"+disIdThinClient);
						   double dAmountThinClient = dAmountTC.getDisbursementAmount();
						   //System.out.println("dAmountThinClient:"+dAmountThinClient);
						   Date disDateThinClient = dAmountTC.getDisbursementDate();
							//System.out.println("disDateThinClient:"+disDateThinClient);
						   //String disDateThinClient1 = disDateThinClient.toString();
						   String dfinalThinClient = dAmountTC.getFinalDisbursement();
						   //System.out.println("dfinalThinClient:"+dfinalThinClient);
						   //System.out.println("******************");
	
						   Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","DisDtls-"+"cgpanTC:"+cgpanThinClient);	
	
						  //System.out.println("******************"); 
						   PeriodicInfo periodicInfoGM2 = (PeriodicInfo) disbDtlsGM.get(0);
						   Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","periodicInfoGM2:"+periodicInfoGM2);
						   //System.out.println("periodicInfoGM2:"+periodicInfoGM2);
						   ArrayList disDtlsGM = (ArrayList) periodicInfoGM2.getDisbursementDetails();
						   //System.out.println("disDtlsGM:"+disDtlsGM);
						   int disDtlsGMSize = disDtlsGM.size();
						   Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","disDtlsGMSize:"+disDtlsGMSize);					 
						   //System.out.println("disDtlsGMSize:"+disDtlsGMSize);
		 
						   if(disDtlsGMSize != 0)
						   { 
							  //System.out.println("******************");
							  Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication"," disbdtls in DB not null ");						 
							  for(int k=0; k < disDtlsGMSize ; k++)
							   {
								   //get Disbursement object from Disbursement ArrayList for GM
								   Disbursement disDetailGM = (Disbursement) disDtlsGM.get(k);
								   String cgpanGM = disDetailGM.getCgpan();	
								   //System.out.println("cgpanGM:"+cgpanGM);
								   String schemeGM = disDetailGM.getScheme();
								   //System.out.println("schemeGM:"+schemeGM);	
								   ArrayList disDtlsArrayGM = disDetailGM.getDisbursementAmounts();
								   //System.out.println("disDtlsArrayGM:"+disDtlsArrayGM);
												   int disDtlsArrayGMSize  = disDtlsArrayGM.size();
	 
												   if(cgpanGM.equals(cgpanThinClient))
												   {
													  if(disDtlsArrayGM != null)
													  {
														  //System.out.println("disDtlsArrayGM is not null");
										  int p = disDtlsArrayGM.size();
										  if(p == 0)
										  {//insert
	
											  //System.out.println("Insert as  size == 0");
											  DisbursementAmount dAmountInsert = new  DisbursementAmount();
											  dAmountInsert.setCgpan(cgpanThinClient);
											  dAmountInsert.setDisbursementAmount(dAmountThinClient);
											  dAmountInsert.setDisbursementDate(disDateThinClient);
											  dAmountInsert.setFinalDisbursement(dfinalThinClient);
											  gmDAO.insertDisbursement(dAmountInsert,userId);	
											  //System.out.println("Disbursement Details inserted");													 		
										  }
										  else
										  {
											  if(disIdThinClient == null)
											  {
												  //insert
												  //System.out.println("Insert as  disIdThinClient == null");
												  DisbursementAmount dAmountInsert = new DisbursementAmount();
												  dAmountInsert.setCgpan(cgpanThinClient);
												  dAmountInsert.setDisbursementAmount(dAmountThinClient);
												  dAmountInsert.setDisbursementDate(disDateThinClient);
												  dAmountInsert.setFinalDisbursement(dfinalThinClient);
												  gmDAO.insertDisbursement(dAmountInsert,userId);
												  //System.out.println("Disbursement Details inserted");										
											  }
											  else
											  {
												  //update for every iteration
												  for(int q=0;q<p;q++)
												  {//get details
													  DisbursementAmount dAmountGMDao = (DisbursementAmount) disDtlsArrayGM.get(q);
													  String disIdGM = dAmountGMDao.getDisbursementId();
													  //System.out.println("disIdGM:"+disIdGM);
													  double dAmountGM = dAmountGMDao.getDisbursementAmount();
													  //System.out.println("dAmountGM:"+dAmountGM);
													  Date disDateGM = dAmountGMDao.getDisbursementDate();
													  //System.out.println("disDateGM:"+disDateGM);
													  String dfinalGM = dAmountGMDao.getFinalDisbursement();
													  //System.out.println("dfinalGM:"+dfinalGM);
													  //System.out.println("------------------");										
													  if(disIdGM.equals(disIdThinClient))
													  {
														  //update
														  //System.out.println("Update as  disIdThinClient == disIdGM");
														  DisbursementAmount dAmountUpdate = new DisbursementAmount();
														  dAmountUpdate.setCgpan(cgpanThinClient);
														  dAmountUpdate.setDisbursementAmount(dAmountThinClient);
														  dAmountUpdate.setDisbursementDate(disDateThinClient);
														  dAmountUpdate.setDisbursementId(disIdThinClient);
														  dAmountUpdate.setFinalDisbursement(dfinalThinClient);
														  gmDAO.updateDisbursement(dAmountUpdate,userId);
														  //System.out.println("Disbursement Details updated");											
													  }
												  }
											  }
										  }
									  }
									  else
									  {	
										  //insert
										  //System.out.println("disDtlsArrayGM is null, hence insert");
										  DisbursementAmount dAmountInsert = new DisbursementAmount();
										  dAmountInsert.setCgpan(cgpanThinClient);
										  dAmountInsert.setDisbursementAmount(dAmountThinClient);
										  dAmountInsert.setDisbursementDate(disDateThinClient);
										  dAmountInsert.setFinalDisbursement(dfinalThinClient);
										  gmDAO.insertDisbursement(dAmountInsert,userId);
										  //System.out.println("Disbursement Details inserted");	
									  }
								   }
							   }	
						   }
						   else
						   {
							  Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","Disbursement dtls not in DB, hence Insert");					 	
							  if((dAmountThinClient != 0) && (disDateThinClient != null))
							  {
								  DisbursementAmount dAmountInsert = new  DisbursementAmount();
								  dAmountInsert.setCgpan(cgpanThinClient);
								  dAmountInsert.setDisbursementAmount(dAmountThinClient);
								  dAmountInsert.setDisbursementDate(disDateThinClient);
								  dAmountInsert.setFinalDisbursement(dfinalThinClient);
								  gmDAO.insertDisbursement(dAmountInsert,userId); 	  
								  //System.out.println("Disbursement Details inserted");  				
							  }
						  }
					  }
				 }
		   	}
		}
		
		 //Repayment Details
		 ArrayList repaymentDtlsTC = periodicInfo.getRepaymentDetails(); 
		 ArrayList repayDtlsGM = gmDAO.getRepaymentDetails(borrowerIdKey,GMConstants.TYPE_ZERO);//from DB
		 
		 if (repaymentDtlsTC!=null)
		 {
			 int repaymentDtlsTCSize = repaymentDtlsTC.size();
			 //System.out.println("repaymentDtlsTCSize:"+repaymentDtlsTCSize);
			 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","RepayDtls-"+"repaymentDtlsTCSize:"+repaymentDtlsTCSize);
			 for(int k=0; k<repaymentDtlsTCSize; k++)
			 {
				 //get repayment object from repayment ArrayList for ThinClient
				 Repayment repaymentDetailThinClient= (Repayment) repaymentDtlsTC.get(k);
				 //System.out.println("repaymentDetailThinClient:"+repaymentDetailThinClient);
				 String cgpanTC = repaymentDetailThinClient.getCgpan();
				 //System.out.println("cgpanTC:"+cgpanTC);
				 String schemeTC = repaymentDetailThinClient.getScheme(); 
				 //System.out.println("schemeTC:"+schemeTC);
				 ArrayList repayAmountsTC = repaymentDetailThinClient.getRepaymentAmounts();
				 if (repayAmountsTC!=null)
				 {
					int repaymentAmountsSize = repayAmountsTC.size();
			 
					for(int l=0; l<repaymentAmountsSize; l++)
					{
						RepaymentAmount repayAmountThinClient =  (RepaymentAmount) repayAmountsTC.get(l);
						String repayIdTC = repayAmountThinClient.getRepayId();
						//System.out.println("repayIdTC:"+repayIdTC);
						double repayAmtTC = repayAmountThinClient.getRepaymentAmount();
						//System.out.println("repayAmtTC:"+repayAmtTC);
						Date repayDateTC = repayAmountThinClient.getRepaymentDate();
						//System.out.println("repayDateTC:"+repayDateTC);
				
						//System.out.println("-----------------------");
						PeriodicInfo periodicInfoGM2 = (PeriodicInfo) repayDtlsGM.get(0);
						//System.out.println("periodicInfoGM2:"+periodicInfoGM2);
						ArrayList repaymentDtlsGM = (ArrayList) periodicInfoGM2.getRepaymentDetails();
						//System.out.println("repaymentDtlsGM:"+repaymentDtlsGM);
						int repaymentDtlsGMSize = repaymentDtlsGM.size();
						//System.out.println("repaymentDtlsGMSize:"+repaymentDtlsGMSize);
					 
						if(repaymentDtlsGMSize != 0) 
						{
						   for(int m=0; m<repaymentDtlsGMSize; m++)
						   {
							  //System.out.println("-----------------------");
							   Repayment repayDetailGM = (Repayment) repaymentDtlsGM.get(m);
							   String cgpanGM = repayDetailGM.getCgpan();	
							   //System.out.println("cgpanGM:"+cgpanGM);
							   String schemeGM = repayDetailGM.getScheme();
							   //System.out.println("schemeGM:"+schemeGM);
							   ArrayList repayAmountsGM = repayDetailGM.getRepaymentAmounts();
							   //System.out.println("repayAmountsGM:"+repayAmountsGM);
					 
					 
							   if(cgpanGM.equals(cgpanTC))
							   {
								  if(repayAmountsGM != null)
								  {
									  int x = repayAmountsGM.size();
									  if(x == 0)
									  {//insert
										  //System.out.println("-----------------------");
										  //System.out.println("no repayAmounts in DB,hence insert");
										  RepaymentAmount repayAmountInsert = new RepaymentAmount();
										  repayAmountInsert.setCgpan(cgpanTC);
										  repayAmountInsert.setRepaymentAmount(repayAmtTC);
										  repayAmountInsert.setRepaymentDate(repayDateTC);
										  gmDAO.insertRepaymentDetails(repayAmountInsert,userId);									
					 		
									  }
									  else
									  {
										  if(repayIdTC == null)
										  {
											  //insert
											  //System.out.println("-----------------------");
											  //System.out.println("repayIdTC is null,hence insert");
											  RepaymentAmount repayAmountInsert = new RepaymentAmount();
											  repayAmountInsert.setCgpan(cgpanTC);
											  repayAmountInsert.setRepaymentAmount(repayAmtTC);
											  repayAmountInsert.setRepaymentDate(repayDateTC);
											  gmDAO.insertRepaymentDetails(repayAmountInsert,userId);									
									
										  }
										  else
										  {
									
											  for(int y=0;y<x;y++)
											  {//update
												  //System.out.println("-----------------------");
												  //System.out.println("repayId is present in DB,hence update");
												  RepaymentAmount RepayAmountGM = (RepaymentAmount) repayAmountsGM.get(y);
												  String repayIdGM = RepayAmountGM.getRepayId();
												  ////System.out.println("repayIdGM:"+repayIdGM);
												  double repayAmtGM = RepayAmountGM.getRepaymentAmount();
												  //System.out.println("repayAmtGM:"+repayAmtGM);
												  Date repayDateGM = RepayAmountGM.getRepaymentDate();
												  //System.out.println("repayDateGM:"+repayDateGM);
										
										
												  if(repayIdGM.equals(repayIdTC))
												  {
													  //update
													  //System.out.println("repayIdGM == repayIdTC,hence update");
													  RepaymentAmount repayAmountUpdate = new RepaymentAmount();
													  repayAmountUpdate.setRepayId(repayIdTC);
													  repayAmountUpdate.setCgpan(cgpanTC);
													  repayAmountUpdate.setRepaymentAmount(repayAmtTC);
													  repayAmountUpdate.setRepaymentDate(repayDateTC);
													  gmDAO.updateRepaymentDetails(repayAmountUpdate,userId);	
												  }
											  }
										  }
									  }
								  }
								  else 
								  {
									  //insert new repayment amount object
									  //System.out.println("-----------------------");
									  //System.out.println("repayAmountsGM == null, hence insert");
									  RepaymentAmount repayAmountInsert = new RepaymentAmount();
									  repayAmountInsert.setCgpan(cgpanTC);
									  repayAmountInsert.setRepaymentAmount(repayAmtTC);
									  repayAmountInsert.setRepaymentDate(repayDateTC);
									  gmDAO.insertRepaymentDetails(repayAmountInsert,userId);						 		
								  }
							   }
						   }						 	
						}
						else
						{
							   if((repayAmtTC != 0) || (repayDateTC != null))
							   {
								   //System.out.println("no repayAmounts in DB,hence insert");
								   RepaymentAmount repayAmountInsert = new RepaymentAmount();
								   repayAmountInsert.setCgpan(cgpanTC);
								   repayAmountInsert.setRepaymentAmount(repayAmtTC);
								   repayAmountInsert.setRepaymentDate(repayDateTC);
								   gmDAO.insertRepaymentDetails(repayAmountInsert,userId);
								   //System.out.println("repayment Details inserted");			
							   } 
						   }
					   } 
				    }
				} 
			}
			 
			 //Recovery Details 
			 ArrayList  RecoveryDetailsTC = periodicInfo.getRecoveryDetails();
			 int RecoveryDetailsTCSize = RecoveryDetailsTC.size();
			 //System.out.println("RecoveryDetailsTCSize:"+RecoveryDetailsTCSize);
		  
			  if((RecoveryDetailsTC != null) || (RecoveryDetailsTCSize != 0))
			  {
					ArrayList recoveryDtlsGM = gmDAO.getRecoveryDetails(borrowerIdKey);//from DB
					int RecoveryDetailsGMSize = recoveryDtlsGM.size();
					//System.out.println("RecoveryDetailsGMSize:"+RecoveryDetailsGMSize);			 			 
					for(int p=0;p<RecoveryDetailsTCSize; p++)
					{
					   Recovery recoveryObjTC = (Recovery) RecoveryDetailsTC.get(p);
					   if (recoveryObjTC!=null && recoveryObjTC.getAmountRecovered()!=0.0)
					   {
							double amountRecovered= recoveryObjTC.getAmountRecovered();
							String cgbid = borrowerIdKey;
							//System.out.println("cgbid:"+cgbid);
							String cgpan = recoveryObjTC.getCgpan();
							Date dateOfRecovery = recoveryObjTC.getDateOfRecovery();
							//System.out.println("dateOfRecovery:"+dateOfRecovery);
							String detailsOfAssetSold = recoveryObjTC.getDetailsOfAssetSold();
							String isRecoveryByOTS = recoveryObjTC.getIsRecoveryByOTS();
							String isRecoveryBySaleOfAsset = recoveryObjTC.getIsRecoveryBySaleOfAsset();
							double legalCharges = recoveryObjTC.getLegalCharges();
							String recoveryNo = recoveryObjTC.getRecoveryNo();
							String remarks = recoveryObjTC.getRemarks();
					
								
							if(recoveryNo == null || recoveryNo.equals(""))
							{	//insert
								//System.out.println("-------------------------");
								//System.out.println("recoveryNo is null in TC,hence new & so insert");
								Recovery recoveryInsert = new Recovery(); 
								recoveryInsert.setCgbid(cgbid);
								recoveryInsert.setAmountRecovered(amountRecovered);
								recoveryInsert.setDateOfRecovery(dateOfRecovery);
								//System.out.println("insert--->dateOfRecovery:"+dateOfRecovery);
								recoveryInsert.setDetailsOfAssetSold(detailsOfAssetSold);
								recoveryInsert.setIsRecoveryByOTS(isRecoveryByOTS);
								recoveryInsert.setIsRecoveryBySaleOfAsset(isRecoveryBySaleOfAsset);
								recoveryInsert.setLegalCharges(legalCharges);
								recoveryInsert.setRecoveryNo(recoveryNo);
								recoveryInsert.setRemarks(remarks);
								gmDAO.updateRecoveryDetails(recoveryInsert);
								//System.out.println(" recovery details inserted");
							}
					
							else
							{
								for(int q=0;q<RecoveryDetailsGMSize; q++)
								{
								   // System.out.println("-------------------------");
								   // System.out.println("q:"+q);
									//System.out.println("recoveryNo is not null in TC,hence update");
									Recovery recoveryGM = (Recovery) recoveryDtlsGM.get(q); 
									String recoveryNoGM = recoveryGM.getRecoveryNo();
									//System.out.println("recoveryNoGM:"+recoveryNoGM);
							
									if(recoveryNoGM.equals(recoveryNo))
									{
									   // System.out.println(" recoveryNoGM == recoveryNo");
										Recovery recoveryUpdate = new Recovery(); 
										recoveryUpdate.setCgbid(cgbid);
										recoveryUpdate.setAmountRecovered(amountRecovered);
										recoveryUpdate.setDateOfRecovery(dateOfRecovery);
										//System.out.println("update--->dateOfRecovery:"+dateOfRecovery);
										recoveryUpdate.setDetailsOfAssetSold(detailsOfAssetSold);
										recoveryUpdate.setIsRecoveryByOTS(isRecoveryByOTS);
										recoveryUpdate.setIsRecoveryBySaleOfAsset(isRecoveryBySaleOfAsset);
										recoveryUpdate.setLegalCharges(legalCharges);
										recoveryUpdate.setRecoveryNo(recoveryNo); 
										recoveryUpdate.setRemarks(remarks);
										gmDAO.modifyRecoveryDetails(recoveryUpdate);
										//System.out.println(" recovery details modified");	
									}
								}
							}
						}
					}// end of Recovery Details		  	  	
			 	}
			 	
				//Outstanding Details 
				//System.out.println("-----------Outstanding details----------");
				ArrayList osDtlsThinClient = periodicInfo.getOutstandingDetails();
				//System.out.println("osDtlsThinClient:"+osDtlsThinClient);
				ArrayList prDtlsGM = gmDAO.getOutstandingDetails(borrowerIdKey,GMConstants.TYPE_ZERO);//from DB
				//System.out.println("prDtlsGM:"+prDtlsGM);			 			 
				if (osDtlsThinClient!=null)
				{ 
					int osDtlsSize = osDtlsThinClient.size();
					for(int a=0; a<osDtlsSize; a++)
					{
						OutstandingDetail osDetailThinClient= (OutstandingDetail) osDtlsThinClient.get(a);
						//System.out.println("--------TC----------");
						String cgpanThinClient = osDetailThinClient.getCgpan();	
						//System.out.println("cgpanThinClient:"+cgpanThinClient);
						String schemeThinClient = osDetailThinClient.getScheme();	
						//System.out.println("schemeThinClient:"+schemeThinClient);
						ArrayList osAmountsThinClient = osDetailThinClient.getOutstandingAmounts();
						if(osAmountsThinClient != null)
						{
							//System.out.println("osAmountsThinClient:"+osAmountsThinClient);
							int osAmountsThinClientSize = osAmountsThinClient.size();
							//System.out.println("osAmountsThinClientSize:"+osAmountsThinClientSize);
							for(int b=0; b<osAmountsThinClientSize; b++)
							{
								OutstandingAmount osAmountThinClient = (OutstandingAmount) osAmountsThinClient.get(b);
								//System.out.println("--------TCArray----------");
								String tcIdThinClient = osAmountThinClient.getTcoId();
								//System.out.println("tcIdThinClient:"+tcIdThinClient);
								double tcPrincipalThinClient = osAmountThinClient.getTcPrincipalOutstandingAmount();
								//System.out.println("tcPrincipalThinClient:"+tcPrincipalThinClient);
								Date tcDateThinClient = osAmountThinClient.getTcOutstandingAsOnDate();
								//System.out.println("tcDateThinClient:"+tcDateThinClient);
								String wcIdThinClient = osAmountThinClient.getWcoId();
								//System.out.println("wcIdThinClient:"+wcIdThinClient);
								double wcPrincipalThinClient = osAmountThinClient.getWcFBPrincipalOutstandingAmount();
								//System.out.println("wcPrincipalThinClient:"+wcPrincipalThinClient);
								double wcInterestThinClient = osAmountThinClient.getWcFBInterestOutstandingAmount();
								//System.out.println("wcInterestThinClient:"+wcInterestThinClient);
								Date wcDateThinClient = osAmountThinClient.getWcFBOutstandingAsOnDate();
								
								double wcNFBPrincipalThinClient = osAmountThinClient.getWcNFBPrincipalOutstandingAmount();
								Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"wcInterestThinClient*:"+wcNFBPrincipalThinClient);
								double wcNFBInterestThinClient = osAmountThinClient.getWcNFBInterestOutstandingAmount();
								Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"wcInterestThinClient*:"+wcNFBInterestThinClient);
								Date wcNFBDateThinClient = osAmountThinClient.getWcNFBOutstandingAsOnDate();
								Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"wcInterestThinClient*:"+wcNFBDateThinClient);
	
								PeriodicInfo periodicInfoGM1 = (PeriodicInfo) prDtlsGM.get(0);
								ArrayList osDtlsGM = (ArrayList) periodicInfoGM1.getOutstandingDetails();
								int osDtlsGMSize = osDtlsGM.size();
								//System.out.println("osDtlsGMSize:"+osDtlsGMSize);
								if(osDtlsGMSize != 0)
								{ 
									Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 1");
									int countCgpan = 1;
									for(int c=0; c<osDtlsGMSize; c++)
									{
										Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 2");
										//get outstandingdetail object from outstandingDetail ArrayList for GM
										OutstandingDetail osDetailGM = (OutstandingDetail) osDtlsGM.get(c);
										//System.out.println("--------GM----------");
										String cgpanGM = osDetailGM.getCgpan();	
										//System.out.println("cgpanGM:"+cgpanGM);
										String schemeGM = osDetailGM.getScheme();	
										//System.out.println("schemeGM:"+schemeGM);
										ArrayList osAmountsGM = osDetailGM.getOutstandingAmounts();
							 
										if(cgpanGM.equals(cgpanThinClient))
										{
											Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 3");
										   //System.out.println("cgpanGM == cgpanThinClient");
										   int cgpanLength = cgpanThinClient.length();
										   int type = cgpanLength - 2;
										   int type1 = cgpanLength - 1;
										   String cgpanType = cgpanThinClient.substring(type,type1);
								
										   //TC
										   if(cgpanType.equalsIgnoreCase("t"))
										   {
											Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 4");
											   //System.out.println("--------TC---------");
											   if(osAmountsGM != null)
											   {
												Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 5");
												   //System.out.println("osAmountsGM != null");
										
												   int x = osAmountsGM.size();
												   if(x == 0)
												   {
													Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 6");
													   //insert
													   //System.out.println("--------InsertTc as osAmountsGM == 0---------");
													   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
													   osAmountThinClientInsert.setCgpan(cgpanThinClient);
													   //System.out.println("cgpanThinClient:"+cgpanThinClient);
													   osAmountThinClientInsert.setTcoId(tcIdThinClient);
													   //System.out.println("tcIdThinClient:"+tcIdThinClient);
													   osAmountThinClientInsert.setTcPrincipalOutstandingAmount(tcPrincipalThinClient);
													   //System.out.println("tcPrincipalThinClient:"+tcPrincipalThinClient);
													   osAmountThinClientInsert.setTcOutstandingAsOnDate(tcDateThinClient);
													   //System.out.println("tcDateThinClient:"+tcDateThinClient);
													   gmDAO.insertTcOutstandingDetails(osAmountThinClientInsert);	
													   //System.out.println("********Inserted*********");
														if (tcPrincipalThinClient==0)
														{
															cgpansToBeClosed.add(cgpanThinClient);
														}
												   }
									
												   else
												   {
													   if(tcIdThinClient == null)
													   {
														Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 7");
														   //insert
													   
														   //System.out.println("--------InsertTc tcIdThinClient == null---------");
														   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
														   osAmountThinClientInsert.setCgpan(cgpanThinClient);
														   //System.out.println("cgpanThinClient:"+cgpanThinClient);
														   osAmountThinClientInsert.setTcoId(tcIdThinClient);
														   //System.out.println("tcIdThinClient:"+tcIdThinClient);
														   osAmountThinClientInsert.setTcPrincipalOutstandingAmount(tcPrincipalThinClient);
														   //System.out.println("tcPrincipalThinClient:"+tcPrincipalThinClient);
														   osAmountThinClientInsert.setTcOutstandingAsOnDate(tcDateThinClient);
														   //System.out.println("tcDateThinClient:"+tcDateThinClient);
														   gmDAO.insertTcOutstandingDetails(osAmountThinClientInsert);
														   //System.out.println("********Inserted*********");		
														if (tcPrincipalThinClient==0)
														{
															cgpansToBeClosed.add(cgpanThinClient);
														}
													   }
													   else
													   {
														Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 8");
														   for(int y=0;y<x;y++)
														   {//get details
															Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 9");
															   OutstandingAmount OsAmountGM= (OutstandingAmount) osAmountsGM.get(y);
															   //System.out.println("--------GMArray----------");
															   String tcIdGM = OsAmountGM.getTcoId();
															   //System.out.println("tcIdGM:"+tcIdGM);
															   double tcPrincipalGM = OsAmountGM.getTcPrincipalOutstandingAmount();
															   //System.out.println("tcPrincipalGM:"+tcPrincipalGM);
															   Date tcDateGM = OsAmountGM.getTcOutstandingAsOnDate();
															   //System.out.println("tcDateGM:"+tcDateGM);
															   String wcIdGM = OsAmountGM.getWcoId();
															   //System.out.println("wcIdGM:"+wcIdGM);
															   double wcPrincipalGM = OsAmountGM.getWcFBPrincipalOutstandingAmount();
															   //System.out.println("wcPrincipalGM:"+wcPrincipalGM);
															   double wcInterestGM = OsAmountGM.getWcFBInterestOutstandingAmount();
															   //System.out.println("wcInterestGM:"+wcInterestGM);
															   Date wcDateGM = OsAmountGM.getWcFBOutstandingAsOnDate();												
													
															   if(tcIdGM.equals(tcIdThinClient))
															   {//update
																   if((tcPrincipalThinClient != tcPrincipalGM) ||(tcDateThinClient.compareTo(tcDateGM) != 0))			
																   {
																	   //update
																	   //System.out.println("----------UpdateTc tcIdGM == tcIdThinClient-------");
																	   Log.log(Log.INFO,"GMProcessor","uploadGmApplication","TC - Updated");
																	   OutstandingAmount osAmountThinClientUpdate = new OutstandingAmount();
																	   osAmountThinClientUpdate.setCgpan(cgpanThinClient);
																	   //System.out.println("cgpanThinClient:"+cgpanThinClient);
																	   osAmountThinClientUpdate.setTcoId(tcIdThinClient);
																	   //System.out.println("tcIdThinClient:"+tcIdThinClient);
																	   osAmountThinClientUpdate.setTcPrincipalOutstandingAmount(tcPrincipalThinClient);
																	   //System.out.println("tcPrincipalThinClient:"+tcPrincipalThinClient);
																	   osAmountThinClientUpdate.setTcOutstandingAsOnDate(tcDateThinClient);
																	   //System.out.println("tcDateThinClient:"+tcDateThinClient);
																	   gmDAO.updateTcOutstandingDetails(osAmountThinClientUpdate);
																	   //System.out.println("********Updated*********");
																	if (tcPrincipalThinClient==0)
																	{
																		cgpansToBeClosed.add(cgpanThinClient);
																	}
																   }
															   }
														   }
													   }
												   }
											   }
											   else
											   {
												   //insert directly
												   //System.out.println("--------InsertTc as OsAMounts is null---------");
												   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
												   osAmountThinClientInsert.setCgpan(cgpanThinClient);
												   //System.out.println("cgpanThinClient:"+cgpanThinClient);
												   osAmountThinClientInsert.setTcoId(tcIdThinClient);
												   //System.out.println("tcIdThinClient:"+tcIdThinClient);
												   osAmountThinClientInsert.setTcPrincipalOutstandingAmount(tcPrincipalThinClient);
												   //System.out.println("tcPrincipalThinClient:"+tcPrincipalThinClient);
												   osAmountThinClientInsert.setTcOutstandingAsOnDate(tcDateThinClient);
												   //System.out.println("tcDateThinClient:"+tcDateThinClient);
												   gmDAO.insertTcOutstandingDetails(osAmountThinClientInsert);
												   //System.out.println("********Inserted*********");
												if (tcPrincipalThinClient==0)
												{
													cgpansToBeClosed.add(cgpanThinClient);
												}
											   }
										   }
										   //WC
										   else
										   {
											Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 10");
											   if(osAmountsGM != null)
											   {
												   int x = osAmountsGM.size();
												   if(x == 0)
												   {
													Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 11");
													   //insert
													   //System.out.println("--------InsertWc osAmountsGM == 0---------");
													   Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Inserted");
													   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
													   osAmountThinClientInsert.setCgpan(cgpanThinClient);
													   //System.out.println("cgpanThinClient:"+cgpanThinClient);
													   osAmountThinClientInsert.setWcoId(wcIdThinClient);
													   //System.out.println("wcIdThinClient:"+wcIdThinClient);
													   osAmountThinClientInsert.setWcFBOutstandingAsOnDate(wcDateThinClient);
													   //System.out.println("wcDateThinClient:"+wcDateThinClient);
													   osAmountThinClientInsert.setWcFBPrincipalOutstandingAmount(wcPrincipalThinClient);
													   //System.out.println("wcPrincipalThinClient:"+wcPrincipalThinClient);
													   osAmountThinClientInsert.setWcFBInterestOutstandingAmount(wcInterestThinClient);
														osAmountThinClientInsert.setWcNFBOutstandingAsOnDate(wcNFBDateThinClient);
														osAmountThinClientInsert.setWcNFBPrincipalOutstandingAmount(wcNFBPrincipalThinClient);
														osAmountThinClientInsert.setWcNFBInterestOutstandingAmount(wcNFBInterestThinClient);													   
													   //System.out.println("wcInterestThinClient:"+wcInterestThinClient);
													   gmDAO.insertWcOutstandingDetails(osAmountThinClientInsert);	
													   //System.out.println("********Inserted*********");									
												   }
									
												   else
												   {
													   if(wcIdThinClient == null)
													   {
														Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 12");
														   //insert	
														   //System.out.println("--------InsertWc wcIdThinClient is null---------");
														   Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Inserted");
														   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
														   osAmountThinClientInsert.setCgpan(cgpanThinClient);
														   //System.out.println("cgpanThinClient:"+cgpanThinClient);
														   osAmountThinClientInsert.setWcoId(wcIdThinClient);
														   //System.out.println("wcIdThinClient:"+wcIdThinClient);
														   osAmountThinClientInsert.setWcFBOutstandingAsOnDate(wcDateThinClient);
														   //System.out.println("wcDateThinClient:"+wcDateThinClient);
														   osAmountThinClientInsert.setWcFBPrincipalOutstandingAmount(wcPrincipalThinClient);
														   //System.out.println("wcPrincipalThinClient:"+wcPrincipalThinClient);
														   osAmountThinClientInsert.setWcFBInterestOutstandingAmount(wcInterestThinClient);
														   //System.out.println("wcInterestThinClient:"+wcInterestThinClient);
														osAmountThinClientInsert.setWcNFBOutstandingAsOnDate(wcNFBDateThinClient);
														osAmountThinClientInsert.setWcNFBPrincipalOutstandingAmount(wcNFBPrincipalThinClient);
														osAmountThinClientInsert.setWcNFBInterestOutstandingAmount(wcNFBInterestThinClient);
														   gmDAO.insertWcOutstandingDetails(osAmountThinClientInsert);
														   //System.out.println("********Inserted*********");											
													   }
													   else
													   {
														Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 13");
														   for(int y=0;y<x;y++)
														   {//get details
															Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 14");
															OutstandingAmount OsAmountGM= (OutstandingAmount) osAmountsGM.get(y);
															   //System.out.println("--------GMArray----------");
															   String tcIdGM = OsAmountGM.getTcoId();
															   //System.out.println("tcIdGM:"+tcIdGM);
															   double tcPrincipalGM = OsAmountGM.getTcPrincipalOutstandingAmount();
															   //System.out.println("tcPrincipalGM:"+tcPrincipalGM);
															   Date tcDateGM = OsAmountGM.getTcOutstandingAsOnDate();
															   //System.out.println("tcDateGM:"+tcDateGM);
															   String wcIdGM = OsAmountGM.getWcoId();
															   //System.out.println("wcIdGM:"+wcIdGM);
															   double wcPrincipalGM = OsAmountGM.getWcFBPrincipalOutstandingAmount();
															   //System.out.println("wcPrincipalGM:"+wcPrincipalGM);
															   double wcInterestGM = OsAmountGM.getWcFBInterestOutstandingAmount();
															   //System.out.println("wcInterestGM:"+wcInterestGM);
															   Date wcDateGM = OsAmountGM.getWcFBOutstandingAsOnDate();
															double wcNFBPrincipalGM = OsAmountGM.getWcNFBPrincipalOutstandingAmount();
															//System.out.println("wcPrincipalGM:"+wcPrincipalGM);
															double wcNFBInterestGM = OsAmountGM.getWcNFBInterestOutstandingAmount();
															//System.out.println("wcInterestGM:"+wcInterestGM);
															Date wcNFBDateGM = OsAmountGM.getWcNFBOutstandingAsOnDate();
															   if(wcIdGM.equals(wcIdThinClient))
															   {//update
																Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 15");
																   if((wcPrincipalThinClient != wcPrincipalGM) || (wcInterestThinClient != wcInterestGM) || (wcDateThinClient.compareTo(wcDateGM) !=0))			
																   {
																	Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 16");
																	   //update  here	
																	   //System.out.println("----------UpdateWc wcIdGM == wcIdThinClient-------");
																	   Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Updated");
																	   OutstandingAmount osAmountThinClientUpdate = new OutstandingAmount();
																	   osAmountThinClientUpdate.setCgpan(cgpanThinClient);
																	   //System.out.println("cgpanThinClient:"+cgpanThinClient);
																	   osAmountThinClientUpdate.setWcoId(wcIdThinClient);
																	   //System.out.println("wcIdThinClient:"+wcIdThinClient);
																	   osAmountThinClientUpdate.setWcFBOutstandingAsOnDate(wcDateThinClient);
																	   //System.out.println("wcDateThinClient:"+wcDateThinClient);
																	   osAmountThinClientUpdate.setWcFBPrincipalOutstandingAmount(wcPrincipalThinClient);
																	   //System.out.println("wcPrincipalThinClient:"+wcPrincipalThinClient);
																	   osAmountThinClientUpdate.setWcFBInterestOutstandingAmount(wcInterestThinClient);
																	   //System.out.println("wcInterestThinClient:"+wcInterestThinClient);
																	Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Updated" + wcNFBDateThinClient);
																	osAmountThinClientUpdate.setWcNFBOutstandingAsOnDate(wcNFBDateThinClient);
																	Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Updated"+ wcNFBPrincipalThinClient);
																	osAmountThinClientUpdate.setWcNFBPrincipalOutstandingAmount(wcNFBPrincipalThinClient);
																	Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Updated"+wcNFBInterestThinClient);
																	osAmountThinClientUpdate.setWcNFBInterestOutstandingAmount(wcNFBInterestThinClient);
																	   gmDAO.updateWcOutstandingDetails(osAmountThinClientUpdate);
																	   //System.out.println("********Updated*********");			
																   }													
															   }
														   }
													   }
												   }
											   }
											   else
											   {
												Log.log(Log.INFO,"GMProcessor","uploadGmApplication","here 17");
												   //insert directly
												   //System.out.println("--------InsertWc as osAmounts is null---------");
												   Log.log(Log.INFO,"GMProcessor","uploadGmApplication","OSDetails-"+"WC Inserted");
												   OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
												   osAmountThinClientInsert.setCgpan(cgpanThinClient);
												   //System.out.println("cgpanThinClient:"+cgpanThinClient);
												   osAmountThinClientInsert.setWcoId(wcIdThinClient);
												   //System.out.println("wcIdThinClient:"+wcIdThinClient);
												   osAmountThinClientInsert.setWcFBOutstandingAsOnDate(wcDateThinClient);
												   //System.out.println("wcDateThinClient:"+wcDateThinClient);
												   osAmountThinClientInsert.setWcFBPrincipalOutstandingAmount(wcPrincipalThinClient);
												   //System.out.println("wcPrincipalThinClient:"+wcPrincipalThinClient);
												   osAmountThinClientInsert.setWcFBInterestOutstandingAmount(wcInterestThinClient);
												   //System.out.println("wcInterestThinClient:"+wcInterestThinClient);
												osAmountThinClientInsert.setWcNFBOutstandingAsOnDate(wcNFBDateThinClient);
												osAmountThinClientInsert.setWcNFBPrincipalOutstandingAmount(wcNFBPrincipalThinClient);
												osAmountThinClientInsert.setWcNFBInterestOutstandingAmount(wcNFBInterestThinClient);
												   gmDAO.insertWcOutstandingDetails(osAmountThinClientInsert);		
												   //System.out.println("********Inserted*********");							
											   }
										   }
										}
									}							
								}
								else
								{	//insert here
									
										int cgpanLength = cgpanThinClient.length();
										int type = cgpanLength - 2;
										int type1 = cgpanLength - 1;
										String cgpanType = cgpanThinClient.substring(type,type1);
									
										if(cgpanType.equalsIgnoreCase("t")) 
										{
											if((tcPrincipalThinClient!= 0) || (tcDateThinClient != null))
											{
												OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
												osAmountThinClientInsert.setCgpan(cgpanThinClient);
												osAmountThinClientInsert.setTcPrincipalOutstandingAmount(tcPrincipalThinClient);
												osAmountThinClientInsert.setTcOutstandingAsOnDate(tcDateThinClient);
												gmDAO.insertTcOutstandingDetails(osAmountThinClientInsert);	 
												//System.out.println("********TC Inserted*********");
											}
										}			
										else
										{
											if((wcPrincipalThinClient!= 0) || (wcDateThinClient != null))
											{
												OutstandingAmount osAmountThinClientInsert = new OutstandingAmount();
												osAmountThinClientInsert.setCgpan(cgpanThinClient);
												osAmountThinClientInsert.setWcFBOutstandingAsOnDate(wcDateThinClient);
												osAmountThinClientInsert.setWcFBPrincipalOutstandingAmount(wcPrincipalThinClient);
												osAmountThinClientInsert.setWcFBInterestOutstandingAmount(wcInterestThinClient);
												osAmountThinClientInsert.setWcNFBOutstandingAsOnDate(wcNFBDateThinClient);
												osAmountThinClientInsert.setWcNFBPrincipalOutstandingAmount(wcNFBPrincipalThinClient);
												osAmountThinClientInsert.setWcNFBInterestOutstandingAmount(wcNFBInterestThinClient);
												gmDAO.insertWcOutstandingDetails(osAmountThinClientInsert);	
												//System.out.println("********WC Inserted*********");
											} 
										}
									}
								}						
							} 
						}
					}
			 	
			 	for (int ctr=0;ctr<cgpansToBeClosed.size();ctr++)
			 	{
			 		String cgpan=(String)cgpansToBeClosed.get(ctr);
					try
					{
						String reason ="Tenore Expired";
						sendMailForClosure(cgpan,userId,fromId,reason);
					}
					catch(MailerException e)
					{
						String errorMessage=cgpan+ " has been closed since Outstanding Amount was ZERO. But Sending E-mails for is failed.";
						errorMessages.add(errorMessage);
					}
					catch(DatabaseException e)
					{
						String errorMessage=cgpan+ " has been closed since Outstanding Amount was ZERO. But Sending E-mails for is failed.";
						errorMessages.add(errorMessage);
					}
					 closure(cgpan, "Tenor Expired", "Automatic Closure while Upload", userId);									
			 	}
			 	cgpansToBeClosed=null;

				String successMessage = "Periodic Information Details for the Borrower :" + borrowerIdKey + "has been uploaded successfully";
				errorMessages.add(successMessage);  				
			 }
			 else
			 {
				String errorDetails = "This Periodic Info Detail has not been verified." +
					" Hence cannot upload the applicaton";
				errorMessages.add(errorDetails); 
			 }
		  }
				
		  else
		  {
			 String errorBorrower = borrowerIdKey+" does not exist in the database";
			 Log.log(Log.DEBUG,"GMProcessor","uploadGmApplication","Error  "+errorBorrower);
			 //System.out.println("errorBorrower:"+errorBorrower); 
			 errorMessages.add(errorBorrower);  
		  }
	  }
	  Log.log(Log.INFO,"GMProcessor","uploadGmApplication","Exited");
	  return errorMessages;
   }


	public BorrowerDetails getBorrowerDetailsForBID(String memberId,String borrowerId)throws DatabaseException
	{
		GMDAO gmDAO = new GMDAO() ;
		BorrowerDetails borrowerDetails=gmDAO.getBorrowerDetailsForBID(memberId,borrowerId);		
		
		return borrowerDetails;
	}

	public ArrayList getOutstandingsForBid(String borrowerId, String memberId)throws DatabaseException, NoApplicationFoundException
	{
		GMDAO gmDAO = new GMDAO();
		ArrayList cgpanList=new ArrayList();
		ArrayList returnOsDetails=new ArrayList();
		ApplicationProcessor apProcessor=new ApplicationProcessor();

		String bnkId=memberId.substring(0,4);
		String zneId=memberId.substring(4,8);
		String brnId=memberId.substring(8,12);

		ArrayList retList = apProcessor.getDtlForBIDMem(borrowerId, bnkId, zneId, brnId);
		cgpanList = (ArrayList) retList.get(1);

		returnOsDetails = gmDAO.getOutstandingsForApproval(cgpanList);

		return returnOsDetails;
	}

	public ArrayList getDisbursementsForBid(String borrowerId, String memberId)throws DatabaseException, NoApplicationFoundException
	{
		GMDAO gmDAO = new GMDAO();
		ArrayList cgpanList=new ArrayList();
		ArrayList returnDisbursements=new ArrayList();
		ApplicationProcessor apProcessor=new ApplicationProcessor();

		String bnkId=memberId.substring(0,4);
		String zneId=memberId.substring(4,8);
		String brnId=memberId.substring(8,12);

		ArrayList retList = apProcessor.getDtlForBIDMem(borrowerId, bnkId, zneId, brnId);
		cgpanList = (ArrayList) retList.get(1);

		returnDisbursements = gmDAO.getDisbursementsForApproval(cgpanList);

		return returnDisbursements;
	}

	public ArrayList getRepaymentsForBid(String borrowerId, String memberId)throws DatabaseException, NoApplicationFoundException
	{
		GMDAO gmDAO = new GMDAO();
		ArrayList cgpanList=new ArrayList();
		ArrayList returnRepayments=new ArrayList();
		ApplicationProcessor apProcessor=new ApplicationProcessor();

		String bnkId=memberId.substring(0,4);
		String zneId=memberId.substring(4,8);
		String brnId=memberId.substring(8,12);

		ArrayList retList = apProcessor.getDtlForBIDMem(borrowerId, bnkId, zneId, brnId);
		cgpanList = (ArrayList) retList.get(1);

		returnRepayments = gmDAO.getRepaymentsForApproval(cgpanList);

		return returnRepayments;
	}

	/**
	  * This method approves the periodic info for the given member id an borrower id.
	  * If the borrower id is not given, then the list of borrower ids for the given member is retirved
	  *		and for each borrower id, the periodic info details are got and approved.
	  */
	 public void approvePeriodicInfo(String memberId, String borrowerId) throws DatabaseException,NoApplicationFoundException 
	 {
		 Log.log(Log.INFO,"GMProcessor","approvePeriodicInfo","Entered");

		 GMDAO gmDAO = new GMDAO();
		 gmDAO.approvePeriodicInfo(memberId, borrowerId);
		 Log.log(Log.INFO,"GMProcessor","approvePeriodicInfo","Exited");
	 }

	/**
	 * This method returns the npa details for the given borrower id.
	 * It returns an Arraylist which contains 2 elements.
	 *		The first element is a NPA object with the old values.
	 *		The next element is a NPA object with the new values.
	 */
	 public ArrayList getNpaDetailsForApproval(String borrowerId) throws DatabaseException
	 {
		 Log.log(Log.INFO,"GMProcessor","getNpaDetailsForApproval","Entered");

		 GMDAO gmDAO = new GMDAO();
		 ArrayList returnNpaDetails = gmDAO.getNpaDetailsForApproval(borrowerId);
		 Log.log(Log.INFO,"GMProcessor","getNpaDetailsForApproval","Exited");
		 return returnNpaDetails;
	 }
	 
	 /**
	  * This method returns the borrower details for the given ssi ref no.
	  * It returns an Arraylist which contains 2 elements.
	  *		The first element is a BorrowerDetails object with the old values.
	  *		The next element is a BorrowerDetails object with the new values.
	  */
	  public ArrayList getBorrowerDetailsForApproval(int ssiRefNo) throws DatabaseException
	  {
		  Log.log(Log.INFO,"GMProcessor","getBorrowerDetailsForApproval","Entered");

		  GMDAO gmDAO = new GMDAO();
		  ArrayList returnBorrowerDetails = gmDAO.getBorrowerDetailsForApproval(ssiRefNo);
		  Log.log(Log.INFO,"GMProcessor","getBorrowerDetailsForApproval","Exited");
		  return returnBorrowerDetails;
	  }	 

	  /**
	   * This method approves the modified borrower details for the given borrower id.
	   */
	  public void approveBorrowerDetails(int ssiRefNo) throws DatabaseException
	  {
		Log.log(Log.INFO,"GMProcessor","approveBorrowerDetails","Entered");

		GMDAO gmDAO = new GMDAO();
		gmDAO.approveBorrowerDetails(ssiRefNo);
		Log.log(Log.INFO,"GMProcessor","approveBorrowerDetails","Exited");
	  }

	  public ArrayList getCgpanMapping(String memberId) throws DatabaseException
	  {
		Log.log(Log.INFO,"GMProcessor","approveBorrowerDetails","Entered");

		GMDAO gmDAO = new GMDAO();
		
		Log.log(Log.INFO,"GMProcessor","approveBorrowerDetails","Exited");
		return gmDAO.getCgpanMapping(memberId);
	  }

	  /**
	   * This method returns the recovery details for the given borrower id.
	   * It returns an Arraylist which contains 2 elements.
	   *		The first element is an arraylist of recovery objects with the old values.
	   *		The next element is an arraylist of recovery objects with the new values.
	   */
	   public ArrayList getRecoveryForApproval(String borrowerId) throws DatabaseException
	   {
		   Log.log(Log.INFO,"GMProcessor","getRecoveryForApproval","Entered");

		   GMDAO gmDAO = new GMDAO();
		   ArrayList returnRecDetails = gmDAO.getRecoveryForApproval(borrowerId);
		   Log.log(Log.INFO,"GMProcessor","getRecoveryForApproval","Exited");
		   return returnRecDetails;
	   }

	   /**
		* This method returns the repayment schedule details for the given list of cgpans.
		* It returns an Arraylist which contains 2 elements.
		*		The first element is a RepaymentSchedule object with the old values.
		*		The next element is a RepaymentSchedule object with the modified values.
		*/
		public ArrayList getRepayScheduleForApproval(String borrowerId, String memberId) throws DatabaseException, NoApplicationFoundException
		{
			Log.log(Log.INFO,"GMProcessor","getRepayScheduleForApproval","Entered");

			GMDAO gmDAO = new GMDAO();
			ArrayList cgpanList=new ArrayList();
			ArrayList returnRepaySchDetails=new ArrayList();
			ApplicationProcessor apProcessor=new ApplicationProcessor();

			String bnkId=memberId.substring(0,4);
			String zneId=memberId.substring(4,8);
			String brnId=memberId.substring(8,12);

			ArrayList retList = apProcessor.getDtlForBIDMem(borrowerId, bnkId, zneId, brnId);
			cgpanList = (ArrayList) retList.get(1);

			returnRepaySchDetails = gmDAO.getRepayScheduleForApproval(cgpanList);

			Log.log(Log.INFO,"GMProcessor","getRepayScheduleForApproval","Exited");
			return returnRepaySchDetails;			
		}

  /**
	* This method fetches all the member ids and returns an array of string.
	* @return Array
	* @roseuid 39B2383B01E4
	*/
   public ArrayList getBorrowerIdForBorrowerName(String borrowerName,String memberId)
				 throws DatabaseException, NoDataException {
	   GMDAO gmDAO = new GMDAO() ;
	   ArrayList borrowerIds = null ;
   
	   borrowerIds =  gmDAO.getBorrowerIdForBorrowerName(borrowerName,memberId) ;
	   if(borrowerIds == null){
		   throw new NoDataException("No borrower id found for the borrower Name") ;
	   }
	   return borrowerIds ;   	   
   }

   public ArrayList viewRepaymentDetailsForReport(String id, int type) 
												throws DatabaseException
												{
		Log.log(Log.INFO,"GMProcessor","viewRepaymentDetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		ArrayList periodicInfos = null ;
		
		periodicInfos = gmDAO.getRepaymentDetailsForReport(id, type);
		
		Log.log(Log.INFO,"GMProcessor","viewRepaymentDetails","Exited");
		return periodicInfos;
   }
   
   public ArrayList viewDisbursementDetailsForReport(String id, int type) 
											throws DatabaseException
											{
    	
		Log.log(Log.INFO,"GMProcessor","viewDisbursementDetails","Entered");
		GMDAO gmDAO = new GMDAO(); 
		ArrayList periodicInfoList = new ArrayList() ;

		periodicInfoList = gmDAO.getDisbursementDetailsForReport(id, type);
		
/*		for(int i=0;i<periodicInfoList.size();++i)
		{
			PeriodicInfo pi = (PeriodicInfo)periodicInfoList.get(i);
			System.out.println("bid "+pi.getBorrowerId());
			System.out.println("name "+pi.getBorrowerName());
			System.out.println("disb size"+pi.getDisbursementDetails().size());
			ArrayList disbs = pi.getDisbursementDetails() ;
			for(int j=0;j<disbs.size();++j)
			{
				Disbursement d =(Disbursement)disbs.get(j);
				System.out.println("pan "+d.getCgpan());
				System.out.println("sanc amt "+d.getSanctionedAmount());
				System.out.println("D amt size "+d.getDisbursementAmounts().size());				 
				for(int k=0;k<disbs.size();++k)
				{
					DisbursementAmount da =(DisbursementAmount)disbs.get(k);
					System.out.println("pan "+da.getCgpan());
					System.out.println("d amt "+da.getDisbursementAmount());
					System.out.println("D date "+da.getDisbursementDate());				 
				}				
			}
			
		}
		*/
		//if(periodicInfoList == null){
			//System.out.println("list is null");
		//	throw new NoDisbursementDetailsException("No disbursement details") ;
		//}
		Log.log(Log.INFO,"GMProcessor","viewDisbursementDetails","Exited");
		return periodicInfoList ;
   }
   
   public NPADetails getNPADetailsForReport(String borrowerId) 
									throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","getNPADetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		NPADetails npaDetails = null ;
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Borrower Id : "+borrowerId);
		
	/*	if (borrowerId.equals("")) {
			//System.out.println("borrowerId null");
			return null ;
		}
		*/
		
		npaDetails = gmDAO.getNPADetailsForReport(borrowerId);
		if(npaDetails==null) {
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","npaDetails==null");
		}
/*		if(npaDetails!=null) {
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Reco Procs size : "+npaDetails.getRecoveryProcedure().size());
		
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","no Of Actions"+npaDetails.getNoOfActions());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","BID "+npaDetails.getCgbid());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA date"+npaDetails.getNpaDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","OS amt"+npaDetails.getOsAmtOnNPA());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA reported"+npaDetails.getWhetherNPAReported());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Rep Date "+npaDetails.getReportingDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","NPA reason "+npaDetails.getNpaReason());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","willful defaulter "+npaDetails.getWillfulDefaulter());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","efforts taken "+npaDetails.getEffortsTaken());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","is reco initiated "+npaDetails.getIsRecoveryInitiated());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","effortsd conlcusion Date"+npaDetails.getEffortsConclusionDate());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","mli comment on Fin "+npaDetails.getMliCommentOnFinPosition());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","DTLs of Fin Pos"+npaDetails.getDetailsOfFinAssistance());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Credit support"+npaDetails.getCreditSupport());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Bank Facility "+npaDetails.getBankFacilityDetail());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Watch List"+npaDetails.getPlaceUnderWatchList());
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","Remarks On npa "+npaDetails.getRemarksOnNpa());
		
		LegalSuitDetail legalSuitDetail = npaDetails.getLegalSuitDetail();
		if(legalSuitDetail != null){
		
		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getCourtName"+legalSuitDetail.getCourtName());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getLegalSuiteNo "+legalSuitDetail.getLegalSuiteNo());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getDtOfFilingLegalSuit "+legalSuitDetail.getDtOfFilingLegalSuit());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getForumName "+legalSuitDetail.getForumName());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getLocation "+legalSuitDetail.getLocation());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getAmountClaimed "+legalSuitDetail.getAmountClaimed());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getCurrentStatus"+legalSuitDetail.getCurrentStatus());

		Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getRecoveryProceedingsConcluded "+legalSuitDetail.getRecoveryProceedingsConcluded());
		} 
		ArrayList recoveryProcedures = npaDetails.getRecoveryProcedure(); 
		RecoveryProcedure reco = null;
		for(int i=0; i<recoveryProcedures.size(); ++i){ 
			reco = (RecoveryProcedure)recoveryProcedures.get(i);
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionDate"+reco.getActionDate());
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionType"+reco.getActionType() );
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getAttachmentName"+reco.getAttachmentName() );
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getNpaId"+reco.getNpaId());
			Log.log(Log.DEBUG,"GMProcessor","getNPADetails","getActionDetails"+reco.getActionDetails());
		} 
	}*/
				
		Log.log(Log.INFO,"GMProcessor","getNPADetails","Exited");
		return npaDetails ;
   }


   public ArrayList getRecoveryDetailsForReport(String borrowerId) 
									throws DatabaseException {
		Log.log(Log.INFO,"GMProcessor","getRecoveryDetails","Entered");
		
		GMDAO gmDAO = new GMDAO(); 
		ArrayList recoveryDetails = null ;
		
/*		if (borrowerId.equals("")) {
			return null ;
		}
*/		
		recoveryDetails = gmDAO.getRecoveryDetailsForReport(borrowerId);
		
		Log.log(Log.INFO,"GMProcessor","getRecoveryDetails","Exited");
		return recoveryDetails ;
   }
   
   public ArrayList viewOutstandingDetailsForReport(String id, int type) 
										  throws DatabaseException,
												SQLException,
												NoUserFoundException {
		Log.log(Log.INFO,"GMProcessor","viewOutstandingDetails","Entered");   										      		
    	
		GMDAO gmDAO = new GMDAO();
		ArrayList periodicInfos = null ;
    	
    	
		periodicInfos  = gmDAO.getOutstandingDetailsForReport(id, type);

		if(periodicInfos  == null){
			throw new NoUserFoundException("User details not found") ;
		}
    	
/*		for(int i=0; i < periodicInfos.size();++i)
		{
		   PeriodicInfo pr =(PeriodicInfo)periodicInfos.get(i);
		   ArrayList outDtls = pr.getOutstandingDetails();
		   Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","Size of out dtls : "+ outDtls.size());
		   for(int j=0; j<outDtls.size(); ++j)
		   {
				OutstandingDetail outDtl = (OutstandingDetail)outDtls.get(j);
				ArrayList outAmts = outDtl.getOutstandingAmounts();
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail Cgpan : "+ outDtl.getCgpan());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail Scheme : "+ outDtl.getScheme());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  tc Sanc Amout : "+ outDtl.getTcSanctionedAmount());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  Wc FB Sanc Amt : "+ outDtl.getWcFBSanctionedAmount());
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingDetail  Wc NFB Sanc Amt : "+ outDtl.getWcNFBSanctionedAmount());
				
				Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","outAmts.size() : "+ outAmts.size());
			
				for(int k = 0; k<outAmts.size(); ++k)
				{	
					OutstandingAmount outAmt = (OutstandingAmount)outAmts.get(k); 
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Cgpan : "+ outAmt.getCgpan());
				
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmounttco Id  : "+ outAmt.getTcoId());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount WCO ID  : "+ outAmt.getWcoId());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc Pr Amt : "+ outAmt.getTcPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc Int Amt  : "+ outAmt.getTcInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount tc As on Dt : "+ outAmt.getTcOutstandingAsOnDate());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmountWc FB Int  Amt : "+ outAmt.getWcFBInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc FB Pr  Amt : "+ outAmt.getWcFBPrincipalOutstandingAmount ());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmountWc NFB Int  Amt : "+ outAmt.getWcNFBInterestOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount  Wc NFB Pr  Amt : "+ outAmt.getWcNFBPrincipalOutstandingAmount());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc NFB  Date : "+ outAmt.getWcFBOutstandingAsOnDate());
					Log.log(Log.DEBUG,"GMProcessor","viewOutstandingDetails","OutstandingAmount Wc FB  Date : "+ outAmt.getWcNFBOutstandingAsOnDate());
				}
			}
		}
*/
		Log.log(Log.INFO,"GMProcessor","viewOutstandingDetails","Exited");
		return periodicInfos ;
   }
   
	public TreeMap getBidsForApproval() throws DatabaseException,
											 SQLException,
											 NoUserFoundException {
											 	
			Log.log(Log.INFO,"GMProcessor","getBidsForApproval","Entered");   										      		

			GMDAO gmDAO = new GMDAO();
			TreeMap bidsList = gmDAO.getBidsForApproval();
			
			Log.log(Log.INFO,"GMProcessor","getBidsForApproval","Exited");
			return bidsList;
						 	
	 }

	public TreeMap getBidsForPerInfoApproval() throws DatabaseException,
										 SQLException,
										 NoUserFoundException {
											 	
		Log.log(Log.INFO,"GMProcessor","getBidsForApproval","Entered");   										      		

		GMDAO gmDAO = new GMDAO();
		TreeMap bidsList = gmDAO.getBidsForPerInfoApproval();
			
		Log.log(Log.INFO,"GMProcessor","getBidsForApproval","Exited");
		return bidsList;
						 	
 }



}

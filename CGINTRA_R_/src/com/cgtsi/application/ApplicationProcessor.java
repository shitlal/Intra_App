//Source file: C:\\CGTSI\\Vss\\com\\cgtsi\\application\\ApplicationProcessor.java

package com.cgtsi.application;

import com.cgtsi.claim.ClaimsProcessor;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Constants;
import com.cgtsi.common.MessageException;
//import com.cgtsi.common.NoDataException;

//import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.ParameterMaster;
import com.cgtsi.admin.User;
import com.cgtsi.common.Log;
import com.cgtsi.mcgs.MCGFDetails;
import com.cgtsi.mcgs.MCGSProcessor;

import com.cgtsi.registration.MLIInfo;
import com.cgtsi.registration.Registration;
import com.cgtsi.risk.GlobalLimits;
import com.cgtsi.risk.RiskManagementProcessor;
import com.cgtsi.risk.SubSchemeValues;
import com.cgtsi.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;

//import com.cgtsi.util.DBConnection;


/**
 * Application is the controller class for all the application related activities.
 * Applications can be TC, WC, Composite etc. WC can be Renewed or Enhanced.
 * This handles all the request from application submission to application
 * approval, rre-approval.
 */
public class ApplicationProcessor
{
		ApplicationDAO appDAO;
		////System.out.println("<br>Application DAO===48===");
   /**
	* @roseuid 39B875C70348
	*/
   public ApplicationProcessor()
   {
		appDAO=new ApplicationDAO();
   }

   /**
	* This method is used to submit a new application. New application could be for
	* TC,WC,Composite,Both, Additional Term loan.
	* @param application
	* @roseuid 3972E7230187
	*/
   public Application submitNewApplication(Application application,String createdBy) throws DatabaseException,
															InvalidApplicationException,MessageException
   {   //System.out.println("<br>Application processing===65===");
	   Log.log(Log.INFO,"ApplicationProcessor","submitNewApplication","Entered");
			    application=appDAO.submitApplication(application,createdBy);

			   Log.log(Log.INFO,"ApplicationProcessor","submitNewApplication","Exited");
			   return application;
   }
   /**
   * 
   * @param application
   * @param createdBy
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   * @throws com.cgtsi.application.InvalidApplicationException
   * @throws com.cgtsi.common.MessageException
   */
  public Application submitNewRSFApplication(Application application,String createdBy) throws DatabaseException,
															InvalidApplicationException,MessageException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","submitNewRSFApplication","Entered");
			   application=appDAO.submitRSFApplication(application,createdBy);
//gdfs
			   Log.log(Log.INFO,"ApplicationProcessor","submitNewRSFApplication","Exited");
			   return application;
   }
   
   
    public Application submitNewRSF2Application(Application application, String createdBy)
          throws DatabaseException, InvalidApplicationException, MessageException
      {
        //  //System.out.println("applicationProcessor:  line 95");
          Log.log(4, "ApplicationProcessor", "submitNewRSF2Application", "Entered");
    //      application = appDAO.submitRSF2Application(application, createdBy);
          Log.log(4, "ApplicationProcessor", "submitNewRSF2Application", "Exited");
          return application;
      }
   /**

	* @param id - It could be CGBID or CGPAN.
	* if type is 0-CGBID
	* 1-CGPAN
	* @param type
	* @return com.cgtsi.application.BorrowerDetails
	* @roseuid 3972E723018A
	*/
   public BorrowerDetails fetchBorrowerDetails(String cgbid,String cgpan) throws DatabaseException,
														NoApplicationFoundException
   {
		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetails","Entered");

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetails","CGBID to fetch SSI Details :" + cgbid);

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetails","CGPAN to fetch SSI Details :" + cgpan);

		BorrowerDetails borrowerDetails=appDAO.fetchBorrowerDtls(cgbid,cgpan);
		if(borrowerDetails==null){
			throw new NoApplicationFoundException("No Application Found");
		}

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetails","Exited");

		return borrowerDetails;
   }
   /**
   * 
   * @param cgbid
   * @param cgpan
   * @param loanType
   * @return 
   * @throws com.cgtsi.common.DatabaseException
   * @throws com.cgtsi.application.NoApplicationFoundException
   */
    public BorrowerDetails fetchBorrowerDetailsNew(String cgbid,String cgpan,String loanType) throws DatabaseException,
														NoApplicationFoundException
   {
		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetailsNew","Entered");

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetailsNew","CGBID to fetch SSI Details :" + cgbid);

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetailsNew","CGPAN to fetch SSI Details :" + cgpan);

		BorrowerDetails borrowerDetails=appDAO.fetchBorrowerDtlsNew(cgbid,cgpan,loanType);
		if(borrowerDetails==null){
			throw new NoApplicationFoundException("No Application Found");
		}

		Log.log(Log.INFO,"ApplicationProcessor","fetchBorrowerDetailsNew","Exited");

		return borrowerDetails;
   }
  /**
   * 
   * @param paymentId
   * @param userId
   * @throws com.cgtsi.common.DatabaseException
   */
   public void rejectApplication(String cgpan,String remarks,String userId)
													throws DatabaseException
	   {
     Log.log(Log.INFO,"ApplicationProcessor","rejectApplication","Entered");
        
		 ApplicationDAO appDAO=new ApplicationDAO();
 //   //System.out.println("Test Here");
    
     appDAO.rejectApplication(cgpan,remarks,userId);
    Log.log(Log.INFO,"ApplicationProcessor","rejectApplication","Exited");
   
   }

// Added by Ritesh path on 23Nov2006
public ArrayList checkDuplicatePath(String mliFlag, String bankName)throws DatabaseException
{
  Log.log(Log.INFO,"ApplicationProcessor","checkDuplicatePath","Entered");
  //System.out.println("ApplicationProcessor checkDuplicatePath Entered 3333333333333333");
  ArrayList duplicateCriteriaObj=new ArrayList();
  ArrayList tcDuplicateCriteriaObj=new ArrayList();
  ArrayList wcDuplicateCriteriaObj=new ArrayList();
  ApplicationDAO appDAO=new ApplicationDAO();
  
  HashMap approvedPendingApplications=appDAO.checkDuplicatePath(bankName);
  //System.out.println("ApplicationProcessor checkDuplicatePath Returned===2"+approvedPendingApplications);
  //System.out.println("ApplicationProcessor checkDuplicatePath Entered 5555555555555555555555");
  HashMap tcApprovedApplications=(HashMap)approvedPendingApplications.get("tcApproved");
  HashMap wcApprovedApplications=(HashMap)approvedPendingApplications.get("wcApproved");		
  HashMap tcPendingApplications=(HashMap)approvedPendingApplications.get("tcPending");
  HashMap wcPendingApplications=(HashMap)approvedPendingApplications.get("wcPending");		
  Set tcApprovedAppsKeys=tcApprovedApplications.keySet();
  Set wcApprovedAppsKeys=wcApprovedApplications.keySet();
  Set tcPendingAppsKeys=tcPendingApplications.keySet();
  Set wcPendingAppsKeys=wcPendingApplications.keySet();
  Iterator tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
  Iterator wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
  Iterator tcPendingAppsIterator=tcPendingAppsKeys.iterator();
  Iterator wcPendingAppsIterator=wcPendingAppsKeys.iterator();
  
  if (mliFlag.equals(Constants.WITHIN_MLIS))
  {
    Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering if flag is W");
    String mliID = null;
    ArrayList approvedApps = null;
    ArrayList pendingApps = null;
    while (tcPendingAppsIterator.hasNext())
    {
      ArrayList tcDuplicateCriteriaObjTemp = new ArrayList();
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering pending interator..");
      mliID=(String)tcPendingAppsIterator.next();
      approvedApps=(ArrayList)tcApprovedApplications.get(mliID);
    
      if (approvedApps==null)
      {					
        approvedApps=new ArrayList();
        pendingApps=(ArrayList)tcPendingApplications.get(mliID);
        tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
        //System.out.println("ApplicationProcessor checkDuplicatePath Entered777777777777777");
      }
      else
      {
        pendingApps=(ArrayList)tcPendingApplications.get(mliID);
        tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
        //System.out.println("ApplicationProcessor checkDuplicatePath Entered888888888888888888");
      }
      
      for(int i=0; i<tcDuplicateCriteriaObjTemp.size(); i++)
      {
        DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjTemp.get(i);
        tcDuplicateCriteriaObj.add(tcDupApplication);
      }
      mliID = null;
      approvedApps = null;
      pendingApps = null;
    }
    while (wcPendingAppsIterator.hasNext())
    {
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering wc pending interator..");
      ArrayList wcDuplicateCriteriaObjTemp = new ArrayList();
      mliID=(String)wcPendingAppsIterator.next();
      approvedApps=(ArrayList)wcApprovedApplications.get(mliID);
      if (approvedApps==null)
      {					
        approvedApps=new ArrayList();
        pendingApps=(ArrayList)wcPendingApplications.get(mliID);
        wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
        //System.out.println("ApplicationProcessor checkDuplicatePath Entered99999999999999999");
      }
      else
      {
        pendingApps=(ArrayList)wcPendingApplications.get(mliID);
        wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
        //System.out.println("ApplicationProcessor checkDuplicatePath Entered101010101010101010101010");
      }
      for(int j=0; j<wcDuplicateCriteriaObjTemp.size(); j++)
      {
        DuplicateApplication wcDupApplication = (DuplicateApplication)wcDuplicateCriteriaObjTemp.get(j);
        //System.out.println("ApplicationProcessor checkDuplicatePath Entered111111111111111111111111111111111111111111111111");
        wcDuplicateCriteriaObj.add(wcDupApplication);
      }
      mliID = null;
      approvedApps = null;
      pendingApps = null;
    }
    duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
    duplicateCriteriaObj.add(wcDuplicateCriteriaObj);
  }
  else if (mliFlag.equals(Constants.ACROSS_MLIS))
  {
    Log.log(Log.INFO,"ApplicationProcessor","checkDuplicatePath","Entering if flag is A");
    //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is A");
    ArrayList tcDuplicateCriteriaObjtemp = new ArrayList();
    ArrayList pendingApps = new ArrayList();
    while (tcPendingAppsIterator.hasNext())
    {
      String key = (String)tcPendingAppsIterator.next();
      ArrayList tempPendingApps=(ArrayList)tcPendingApplications.get(key);
      for(int i=0; i<tempPendingApps.size(); i++)
      {
        Application tempPending = (Application)tempPendingApps.get(i);
        pendingApps.add(tempPending);
      }
    }
    ArrayList approvedApps=new ArrayList();
    tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
    if(tcApprovedApplications.isEmpty())
    {						
      approvedApps=new ArrayList();
    }
    else
    {
      approvedApps = new ArrayList();
      
      while (tcApprovedAppsIterator.hasNext())
      {			
        String key = (String)tcApprovedAppsIterator.next();			
        ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
        for(int j=0; j<approvedAppsTemp.size();j++)
        {
          Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
          approvedApps.add(tcApplicationTemp);
        }						
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
      }
    }
    tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
    //System.out.println("ritesh is here tcDuplicateCriteriaObjtemp.size() ="+tcDuplicateCriteriaObjtemp.size());
    for(int m=0; m< tcDuplicateCriteriaObjtemp.size(); m++)
    {
      DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjtemp.get(m);
      tcDuplicateCriteriaObj.add(tcDupApplication);
    }
    while (wcPendingAppsIterator.hasNext())
    {
      ArrayList wcDuplicateCriteriaObjtemp = new ArrayList();
      String key = (String)wcPendingAppsIterator.next();
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","key 1:" + key);
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering wc pending interator..");
      pendingApps=(ArrayList)wcPendingApplications.get(key);
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","pendingApps size :" + pendingApps.size());
      wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
      if(wcApprovedApplications.isEmpty())
      {
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","approved empty..");
        approvedApps=new ArrayList();
        wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      else
      {
        approvedApps = new ArrayList();
      
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","approved not empty..");					
        while (wcApprovedAppsIterator.hasNext())
        {						
          key= (String)wcApprovedAppsIterator.next();
          Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","key :" + key);
          Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","wcApprovedAppsIterator..");
          ArrayList approvedAppsTemp=(ArrayList)wcApprovedApplications.get(key);
          for(int i=0; i<approvedAppsTemp.size(); i++)
          {
            Application wcApplicationtemp = (Application)approvedAppsTemp.get(i);;							
            approvedApps.add(wcApplicationtemp);													
          }
        }
        wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      //System.out.println("ritesh is here tcDuplicateCriteriaObjtemp.size() ="+tcDuplicateCriteriaObjtemp.size());
      for(int k=0; k< wcDuplicateCriteriaObjtemp.size(); k++)
      {
        DuplicateApplication dupApplication = (DuplicateApplication)wcDuplicateCriteriaObjtemp.get(k);
        wcDuplicateCriteriaObj.add(dupApplication);									
      }
    
    }
    duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
    duplicateCriteriaObj.add(wcDuplicateCriteriaObj);			
  }
  appDAO=null;
  approvedPendingApplications=null;
  tcApprovedApplications=null;
  wcApprovedApplications=null;
  tcPendingApplications=null;
  wcPendingApplications=null;
  tcApprovedAppsKeys=null;
  wcApprovedAppsKeys=null;
  tcPendingAppsKeys=null;
  wcPendingAppsKeys=null;
  tcApprovedAppsIterator=null;
  wcApprovedAppsIterator=null;
  tcPendingAppsIterator	= null;
  wcPendingAppsIterator	= null;
  Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Exited");
  return duplicateCriteriaObj;
}



public ArrayList checkDuplicatePathnew(String mliFlag, String bankName)throws DatabaseException
{
  Log.log(Log.INFO,"ApplicationProcessor","checkDuplicatePath","Entered");
//  //System.out.println("ApplicationProcessor checkDuplicatePath Entered");
  ArrayList duplicateCriteriaObj=new ArrayList();
  ArrayList tcDuplicateCriteriaObj=new ArrayList();
  ArrayList wcDuplicateCriteriaObj=new ArrayList();
  ApplicationDAO appDAO=new ApplicationDAO();
  
  HashMap approvedPendingApplications=appDAO.checkDuplicatePathnew(bankName);
  
  HashMap tcApprovedApplications=(HashMap)approvedPendingApplications.get("tcApproved");
  HashMap wcApprovedApplications=(HashMap)approvedPendingApplications.get("wcApproved");		
  HashMap tcPendingApplications=(HashMap)approvedPendingApplications.get("tcPending");
  HashMap wcPendingApplications=(HashMap)approvedPendingApplications.get("wcPending");		
  Set tcApprovedAppsKeys=tcApprovedApplications.keySet();
  Set wcApprovedAppsKeys=wcApprovedApplications.keySet();
  Set tcPendingAppsKeys=tcPendingApplications.keySet();
  Set wcPendingAppsKeys=wcPendingApplications.keySet();
  Iterator tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
  Iterator wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
  Iterator tcPendingAppsIterator=tcPendingAppsKeys.iterator();
  Iterator wcPendingAppsIterator=wcPendingAppsKeys.iterator();
  
  if (mliFlag.equals(Constants.WITHIN_MLIS))
  {
    Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering if flag is W");
    String mliID = null;
    ArrayList approvedApps = null;
    ArrayList pendingApps = null;
    while (tcPendingAppsIterator.hasNext())
    {
      ArrayList tcDuplicateCriteriaObjTemp = new ArrayList();
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering pending interator..");
      mliID=(String)tcPendingAppsIterator.next();
      approvedApps=(ArrayList)tcApprovedApplications.get(mliID);
    
      if (approvedApps==null)
      {					
        approvedApps=new ArrayList();
        pendingApps=(ArrayList)tcPendingApplications.get(mliID);
        tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      else
      {
        pendingApps=(ArrayList)tcPendingApplications.get(mliID);
        tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      
      for(int i=0; i<tcDuplicateCriteriaObjTemp.size(); i++)
      {
        DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjTemp.get(i);
        tcDuplicateCriteriaObj.add(tcDupApplication);
      }
      mliID = null;
      approvedApps = null;
      pendingApps = null;
    }
    while (wcPendingAppsIterator.hasNext())
    {
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering wc pending interator..");
      ArrayList wcDuplicateCriteriaObjTemp = new ArrayList();
      mliID=(String)wcPendingAppsIterator.next();
      approvedApps=(ArrayList)wcApprovedApplications.get(mliID);
      if (approvedApps==null)
      {					
        approvedApps=new ArrayList();
        pendingApps=(ArrayList)wcPendingApplications.get(mliID);
        wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      else
      {
        pendingApps=(ArrayList)wcPendingApplications.get(mliID);
        wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      for(int j=0; j<wcDuplicateCriteriaObjTemp.size(); j++)
      {
        DuplicateApplication wcDupApplication = (DuplicateApplication)wcDuplicateCriteriaObjTemp.get(j);
        wcDuplicateCriteriaObj.add(wcDupApplication);
      }
      mliID = null;
      approvedApps = null;
      pendingApps = null;
    }
    duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
    duplicateCriteriaObj.add(wcDuplicateCriteriaObj);
  }
  else if (mliFlag.equals(Constants.ACROSS_MLIS))
  {
    Log.log(Log.INFO,"ApplicationProcessor","checkDuplicatePath","Entering if flag is A");
  //  //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is A");
    ArrayList tcDuplicateCriteriaObjtemp = new ArrayList();
    ArrayList pendingApps = new ArrayList();
    while (tcPendingAppsIterator.hasNext())
    {
      String key = (String)tcPendingAppsIterator.next();
      ArrayList tempPendingApps=(ArrayList)tcPendingApplications.get(key);
      for(int i=0; i<tempPendingApps.size(); i++)
      {
        Application tempPending = (Application)tempPendingApps.get(i);
        pendingApps.add(tempPending);
      }
    }
    ArrayList approvedApps=new ArrayList();
    tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
    if(tcApprovedApplications.isEmpty())
    {						
      approvedApps=new ArrayList();
    }
    else
    {
      approvedApps = new ArrayList();
      
      while (tcApprovedAppsIterator.hasNext())
      {			
        String key = (String)tcApprovedAppsIterator.next();			
        ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
        for(int j=0; j<approvedAppsTemp.size();j++)
        {
          Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
          approvedApps.add(tcApplicationTemp);
        }						
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
      }
    }
    tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
 //   //System.out.println("ritesh is here tcDuplicateCriteriaObjtemp.size() ="+tcDuplicateCriteriaObjtemp.size());
    for(int m=0; m< tcDuplicateCriteriaObjtemp.size(); m++)
    {
      DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjtemp.get(m);
      tcDuplicateCriteriaObj.add(tcDupApplication);
    }
    while (wcPendingAppsIterator.hasNext())
    {
      ArrayList wcDuplicateCriteriaObjtemp = new ArrayList();
      String key = (String)wcPendingAppsIterator.next();
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","key 1:" + key);
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","Entering wc pending interator..");
      pendingApps=(ArrayList)wcPendingApplications.get(key);
      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","pendingApps size :" + pendingApps.size());
      wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
      if(wcApprovedApplications.isEmpty())
      {
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","approved empty..");
        approvedApps=new ArrayList();
        wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
      else
      {
        approvedApps = new ArrayList();
      
        Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","approved not empty..");					
        while (wcApprovedAppsIterator.hasNext())
        {						
          key= (String)wcApprovedAppsIterator.next();
          Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","key :" + key);
          Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicatePath","wcApprovedAppsIterator..");
          ArrayList approvedAppsTemp=(ArrayList)wcApprovedApplications.get(key);
          for(int i=0; i<approvedAppsTemp.size(); i++)
          {
            Application wcApplicationtemp = (Application)approvedAppsTemp.get(i);;							
            approvedApps.add(wcApplicationtemp);													
          }
        }
        wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
      }
 //     //System.out.println("ritesh is here tcDuplicateCriteriaObjtemp.size() ="+tcDuplicateCriteriaObjtemp.size());
      for(int k=0; k< wcDuplicateCriteriaObjtemp.size(); k++)
      {
        DuplicateApplication dupApplication = (DuplicateApplication)wcDuplicateCriteriaObjtemp.get(k);
        wcDuplicateCriteriaObj.add(dupApplication);									
      }
    
    }
    duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
    duplicateCriteriaObj.add(wcDuplicateCriteriaObj);			
  }
  appDAO=null;
  approvedPendingApplications=null;
  tcApprovedApplications=null;
  wcApprovedApplications=null;
  tcPendingApplications=null;
  wcPendingApplications=null;
  tcApprovedAppsKeys=null;
  wcApprovedAppsKeys=null;
  tcPendingAppsKeys=null;
  wcPendingAppsKeys=null;
  tcApprovedAppsIterator=null;
  wcApprovedAppsIterator=null;
  tcPendingAppsIterator	= null;
  wcPendingAppsIterator	= null;
  Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Exited");
  return duplicateCriteriaObj;
}


 public ArrayList checkDuplicate(String mliFlag)throws DatabaseException
	{
		// Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Entered");
    ////System.out.println("PATH inside  ApplicationProcessor checkDuplicate value mliFlag = "+mliFlag);
		ArrayList duplicateCriteriaObj=new ArrayList();
	   ArrayList tcDuplicateCriteriaObj=new ArrayList();
	   ArrayList wcDuplicateCriteriaObj=new ArrayList();

	   ApplicationDAO appDAO=new ApplicationDAO();
	   HashMap approvedPendingApplications=appDAO.checkDuplicate();

/*		HashMap approvedApplications=(HashMap)approvedPendingApplications.get("approved");
		HashMap pendingApplications=(HashMap)approvedPendingApplications.get("pending");

		Set approvedAppsKeys=approvedApplications.keySet();
		Set pendingAppsKeys=pendingApplications.keySet();

		Iterator approvedAppsIterator=approvedAppsKeys.iterator();
		Iterator pendingAppsIterator=pendingAppsKeys.iterator();
*/
		HashMap tcApprovedApplications=(HashMap)approvedPendingApplications.get("tcApproved");
		HashMap wcApprovedApplications=(HashMap)approvedPendingApplications.get("wcApproved");		
		
		HashMap tcPendingApplications=(HashMap)approvedPendingApplications.get("tcPending");
		HashMap wcPendingApplications=(HashMap)approvedPendingApplications.get("wcPending");		

		Set tcApprovedAppsKeys=tcApprovedApplications.keySet();
		Set wcApprovedAppsKeys=wcApprovedApplications.keySet();
		
		Set tcPendingAppsKeys=tcPendingApplications.keySet();
		Set wcPendingAppsKeys=wcPendingApplications.keySet();

		Iterator tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
		Iterator wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
		
		Iterator tcPendingAppsIterator=tcPendingAppsKeys.iterator();
		Iterator wcPendingAppsIterator=wcPendingAppsKeys.iterator();

		if (mliFlag.equals(Constants.WITHIN_MLIS))
		{
			//Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering if flag is W");
   //   //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is W");
			String mliID = null;
			ArrayList approvedApps = null;
			ArrayList pendingApps = null;
			
			while (tcPendingAppsIterator.hasNext())
			{
				ArrayList tcDuplicateCriteriaObjTemp = new ArrayList();
				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering pending interator..");

				mliID=(String)tcPendingAppsIterator.next();
				approvedApps=(ArrayList)tcApprovedApplications.get(mliID);

				if (approvedApps==null)
				{					
					approvedApps=new ArrayList();
					pendingApps=(ArrayList)tcPendingApplications.get(mliID);
					tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);

				}else{
					
					pendingApps=(ArrayList)tcPendingApplications.get(mliID);
					tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
				}
				
				for(int i=0; i<tcDuplicateCriteriaObjTemp.size(); i++)
				{
					DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjTemp.get(i);
					tcDuplicateCriteriaObj.add(tcDupApplication);
				}


				mliID = null;
				approvedApps = null;
				pendingApps = null;

			}
			
			while (wcPendingAppsIterator.hasNext())
			{
				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering wc pending interator..");

				ArrayList wcDuplicateCriteriaObjTemp = new ArrayList();
				
				mliID=(String)wcPendingAppsIterator.next();
				approvedApps=(ArrayList)wcApprovedApplications.get(mliID);

				if (approvedApps==null)
				{					
					approvedApps=new ArrayList();
					pendingApps=(ArrayList)wcPendingApplications.get(mliID);
					wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
					
					//return 	duplicateCriteriaObj;	
					
					//break;				
				}else{
					
					pendingApps=(ArrayList)wcPendingApplications.get(mliID);
					wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);

				}
				
				for(int j=0; j<wcDuplicateCriteriaObjTemp.size(); j++)
				{
					DuplicateApplication wcDupApplication = (DuplicateApplication)wcDuplicateCriteriaObjTemp.get(j);
					wcDuplicateCriteriaObj.add(wcDupApplication);
				}


				mliID = null;
				approvedApps = null;
				pendingApps = null;

			}
			
			duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
			duplicateCriteriaObj.add(wcDuplicateCriteriaObj);
			
		}else if (mliFlag.equals(Constants.ACROSS_MLIS))
		{
			Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Entering if flag is A");
  //    //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is A");
			ArrayList tcDuplicateCriteriaObjtemp = new ArrayList();
			ArrayList pendingApps = new ArrayList();
			while (tcPendingAppsIterator.hasNext())
			{
				
				
				String key = (String)tcPendingAppsIterator.next();
				ArrayList tempPendingApps=(ArrayList)tcPendingApplications.get(key);
/*				tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
				if(tcApprovedApplications.isEmpty())
				{						
					ArrayList approvedApps=new ArrayList();
					tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
										
				}
				else{
					ArrayList approvedApps = new ArrayList();
					
					while (tcApprovedAppsIterator.hasNext())
					{			
						key = (String)tcApprovedAppsIterator.next();			
						ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
						for(int j=0; j<approvedAppsTemp.size();j++)
						{
							Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
							approvedApps.add(tcApplicationTemp);
							
						}						
						Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
					}
					tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
*/
					for(int i=0; i<tempPendingApps.size(); i++)
					{
						Application tempPending = (Application)tempPendingApps.get(i);
						pendingApps.add(tempPending);
					}
				}
				ArrayList approvedApps=new ArrayList();
					tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
					if(tcApprovedApplications.isEmpty())
					{						
						approvedApps=new ArrayList();
						//tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);							
					}
					else{
						approvedApps = new ArrayList();
		
						while (tcApprovedAppsIterator.hasNext())
						{			
							String key = (String)tcApprovedAppsIterator.next();			
							ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
							for(int j=0; j<approvedAppsTemp.size();j++)
							{
								Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
								approvedApps.add(tcApplicationTemp);
				
							}						
							Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
						}
					}
				tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
				
				
				
				for(int m=0; m< tcDuplicateCriteriaObjtemp.size(); m++)
				{
					DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjtemp.get(m);
					tcDuplicateCriteriaObj.add(tcDupApplication);
				}

			//}	
			
			while (wcPendingAppsIterator.hasNext())
			{
				ArrayList wcDuplicateCriteriaObjtemp = new ArrayList();
				String key = (String)wcPendingAppsIterator.next();
				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","key 1:" + key);
				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering wc pending interator..");
				pendingApps=(ArrayList)wcPendingApplications.get(key);
				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","pendingApps size :" + pendingApps.size());
				wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
				if(wcApprovedApplications.isEmpty())
				{
					Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","approved empty..");
					
					approvedApps=new ArrayList();
					wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
					
										
				}
				else{
					approvedApps = new ArrayList();
					
					Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","approved not empty..");					
					while (wcApprovedAppsIterator.hasNext())
					{						
						key= (String)wcApprovedAppsIterator.next();
						Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","key :" + key);
						Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","wcApprovedAppsIterator..");
						ArrayList approvedAppsTemp=(ArrayList)wcApprovedApplications.get(key);
						for(int i=0; i<approvedAppsTemp.size(); i++)
						{
							Application wcApplicationtemp = (Application)approvedAppsTemp.get(i);;							
							approvedApps.add(wcApplicationtemp);													
						}
						
					}
					wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
				}
				
				for(int k=0; k< wcDuplicateCriteriaObjtemp.size(); k++)
				{
					DuplicateApplication dupApplication = (DuplicateApplication)wcDuplicateCriteriaObjtemp.get(k);
					wcDuplicateCriteriaObj.add(dupApplication);									
				}

			}
			
			duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
			duplicateCriteriaObj.add(wcDuplicateCriteriaObj);			
				
		}


	   appDAO=null;
	   approvedPendingApplications=null;

		tcApprovedApplications=null;
		wcApprovedApplications=null;
		
		tcPendingApplications=null;
		wcPendingApplications=null;

		tcApprovedAppsKeys=null;
		wcApprovedAppsKeys=null;
		
		tcPendingAppsKeys=null;
		wcPendingAppsKeys=null;

		tcApprovedAppsIterator=null;
		wcApprovedAppsIterator=null;
		
		tcPendingAppsIterator	= null;
		wcPendingAppsIterator	= null;
		
		Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Exited");
		
		return duplicateCriteriaObj;
	}

	/**
	* This method checks the duplicity of all the parameters and returns duplicate Criteria object
	*/

      private ArrayList checkDuplicateApplications(ArrayList approvedApps,ArrayList pendingApps) throws DatabaseException
			{
    	  //System.out.println("ApplicationProcessor checkDuplicatePath Entered 66666666666666666");
			   Log.log(Log.INFO, "ApplicationProcessor" , "checkDuplicateApplications", "Entered " );
			   DuplicateApplication duplicateApplication=new DuplicateApplication();
			   DuplicateCriteria duplicateCriteria=new DuplicateCriteria();
			   DuplicateCondition duplicateCondition=new DuplicateCondition();
			   ArrayList duplicateApp=new ArrayList();
   			 int approvedAppSize=approvedApps.size();
				 int pendingAppSize=pendingApps.size();
				 ArrayList duplicateConditionList=null;
				 boolean duplicateApps=false;
         
         ApplicationDAO applicationDAO=new ApplicationDAO();
         
				    
				  //  //System.out.println("***********************************3**********************************");
				for (int i=0;i<pendingAppSize ;i++ )
				{
                                
					     duplicateApps=false;
                             //   //System.out.println(" boolean duplicateApps=false;");
                                
				  duplicateApplication=new DuplicateApplication();
				  duplicateCriteria=new DuplicateCriteria();
				  //duplicateCondition=new DuplicateCondition();
				  duplicateConditionList=new ArrayList();
				  duplicateApplication.setDuplicateCondition(duplicateConditionList);
				  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
				  Application pendingApp=(Application)pendingApps.get(i);
        //  //System.out.println(pendingApp.getAppRefNo());
				  if(Log.isDebugEnabled())
				  {
					  Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "before checking with Approved Applications" );
					  Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending " + i + " app ref no " + pendingApp.getAppRefNo());
				  }
				  /*
					* This loop checks the pending application against the approved
					* applications for duplicity.
					*/
          ////System.out.println("approvedAppSize "+approvedAppSize);
				  for (int j=0;j< approvedAppSize;j++ )
				  {
                               
					  ////System.out.println("approvedAppSize "+approvedAppSize);
            duplicateApps=false;
						boolean regNo = false;
            boolean unitName = false;
            boolean firstName = false;
            boolean middleName = false;
            boolean lastName = false;
            boolean address = false;
            //boolean bankRefNo = false;
            boolean itpan = false;
            boolean loanType1 = false;
					  //duplicateCondition=new DuplicateCondition();
            Application approvedApp=(Application)approvedApps.get(j);
            if(Log.isDebugEnabled())
            {
              Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "approved " + j + " app ref no " + approvedApp.getAppRefNo());
            }
            String aState = ((approvedApp.getBorrowerDetails()).getSsiDetails()).getState();
            String pState = ((pendingApp.getBorrowerDetails()).getSsiDetails()).getState();
						String aDistrict = ((approvedApp.getBorrowerDetails()).getSsiDetails()).getDistrict();
            String pDistrict = ((pendingApp.getBorrowerDetails()).getSsiDetails()).getDistrict();
            String aLoanType = approvedApp.getLoanType();
            String pLoanType = pendingApp.getLoanType();
            String aSSiRef = approvedApp.getExistSSI();
            String pSSiRef = pendingApp.getPrevSSI();
           //start bhu
            if(aState.equalsIgnoreCase(pState) && aDistrict.equalsIgnoreCase(pDistrict))
            {
              String aSsiRegNo=((approvedApp.getBorrowerDetails()).getSsiDetails()).getRegNo();
              String pSsiRegNo=((pendingApp.getBorrowerDetails()).getSsiDetails()).getRegNo();
              if(((aSsiRegNo!=null && !aSsiRegNo.equals("")) && (pSsiRegNo!=null && !pSsiRegNo.equals(""))) && (!aSsiRegNo.equals("0") && !pSsiRegNo.equals("0")))
              {
                if (aSsiRegNo.equalsIgnoreCase(pSsiRegNo))
                {
                  duplicateConditionList=duplicateApplication.getDuplicateCondition();
                  duplicateCondition=new DuplicateCondition();
                  duplicateCondition.setPrevLoanType(aLoanType);
                  duplicateCondition.setexistLoanType(pLoanType);
                  duplicateCondition.setExistingValue(aSsiRegNo);
                  duplicateCondition.setNewValue(pSsiRegNo);
                  duplicateCondition.setPrevSSi(pSSiRef);
                  duplicateCondition.setExistSSi(aSSiRef);
                  duplicateCondition.setConditionName("SSI Reg No");
                  duplicateConditionList.add(duplicateCondition);
                  duplicateApplication.setDuplicateCondition(duplicateConditionList);
                  duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                  duplicateCriteria.setSsiRegNo(true);
                  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                  //duplicateApps=true;
                  regNo = true;
                }
                else
                {
                  regNo = false;
                }
              }
              else
              {
								regNo = true;
              }
              String aSsiUnitName=((approvedApp.getBorrowerDetails()).getSsiDetails()).getSsiName();
              String pSsiUnitName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getSsiName();
              String approvedSsiUnitName=aSsiUnitName.replaceAll(" ","");
              String pendingSsiUnitName=pSsiUnitName.replaceAll(" ","");
              if (approvedSsiUnitName.equalsIgnoreCase(pendingSsiUnitName))
              {
                duplicateConditionList=duplicateApplication.getDuplicateCondition();
							  duplicateCondition=new DuplicateCondition();
                duplicateCondition.setPrevLoanType(aLoanType);
                duplicateCondition.setexistLoanType(pLoanType);
                duplicateCondition.setExistingValue(aSsiUnitName);
                duplicateCondition.setNewValue(pSsiUnitName);
                  duplicateCondition.setPrevSSi(pSSiRef);
                  duplicateCondition.setExistSSi(aSSiRef);
                 
                duplicateCondition.setConditionName("Unit Name");
                duplicateConditionList.add(duplicateCondition);
                duplicateApplication.setDuplicateCondition(duplicateConditionList);
                duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                duplicateCriteria.setUnitName(true);
                duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                //duplicateApps=true;
                unitName = true;
              }
              if(Log.isDebugEnabled())
              {
                Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved First Name :" + ((approvedApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName());
              }
              String aFirstName=((approvedApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName();
  						String pFirstName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName();
              if(Log.isDebugEnabled())
              {					  
                Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","PEnding First Name :" + pFirstName);
              }
              String approvedFirstName=aFirstName.replaceAll(" ","");
              String pendingFirstName=pFirstName.replaceAll(" ","");
              if (approvedFirstName.equalsIgnoreCase(pendingFirstName))
              {
                  duplicateConditionList=duplicateApplication.getDuplicateCondition();
                  duplicateCondition=new DuplicateCondition();
                  duplicateCondition.setConditionName("Promoter First Name");
                  duplicateCondition.setExistingValue(aFirstName);
                  duplicateCondition.setNewValue(pFirstName);
                  duplicateCondition.setPrevSSi(pSSiRef);
                  duplicateCondition.setExistSSi(aSSiRef);
                 
                  duplicateConditionList.add(duplicateCondition);
                  duplicateApplication.setDuplicateCondition(duplicateConditionList);
                  duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                  duplicateCriteria.setPFirstName(true);
                  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                  //duplicateApps=true;
                  firstName = true;
                }
                else
                {
                  firstName = false;
                }
                String aMiddleName=((approvedApp.getBorrowerDetails()).getSsiDetails()).getCpMiddleName();
                if(Log.isDebugEnabled())
                {
                  Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Middle Name :" + aMiddleName);
                }
                String pMiddleName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpMiddleName();
    						if(Log.isDebugEnabled())
        				{
                  Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Middle name :" + aMiddleName + " for app ref no" + approvedApp.getCgpan());						
                  Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Pending Middle name:" + pMiddleName + " for cgpan" + pendingApp.getAppRefNo());
                }
                if ((aMiddleName!=null && !(aMiddleName.equals(""))) && (pMiddleName!=null && !(pMiddleName.equals(""))))
                {
                  String approvedMiddleName=aMiddleName.replaceAll(" ","");
                  String pendingMiddleName=pMiddleName.replaceAll(" ","");
                  if (approvedMiddleName.equalsIgnoreCase(pendingMiddleName))
                  {
                    if(Log.isDebugEnabled())
                    {
                      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Middle name :" + approvedMiddleName + " for app ref no" + approvedApp.getCgpan());						
                      Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Pending Middle name:" + pendingMiddleName + " for cgpan" + pendingApp.getAppRefNo());
                    }
                    duplicateConditionList=duplicateApplication.getDuplicateCondition();
                    duplicateCondition=new DuplicateCondition();
                    duplicateCondition.setConditionName("Promoter Middle Name");
                    duplicateCondition.setExistingValue(aMiddleName);
                    duplicateCondition.setNewValue(pMiddleName);
                    duplicateCondition.setPrevSSi(pSSiRef);
                    duplicateCondition.setExistSSi(aSSiRef);
                 
                    duplicateConditionList.add(duplicateCondition);
                    duplicateApplication.setDuplicateCondition(duplicateConditionList);
                    duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                    duplicateCriteria.setPMiddleName(true);
                    duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                    //duplicateApps=true;
                    middleName = true;
                  }
                  else
                  {
                  	middleName = false;
                  }
                }
                else
                {
                	middleName = true;
                }
                String aLastName=((approvedApp.getBorrowerDetails()).getSsiDetails()).getCpLastName();
    
                String pLastName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpLastName();
                String approvedLastName=aLastName.replaceAll(" ","");
                String pendingLastName=pLastName.replaceAll(" ","");
                if (approvedLastName.equalsIgnoreCase(pendingLastName))
                {
                  if(Log.isDebugEnabled())
                  {
                    Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Last name :" + approvedLastName + "for app ref no" + approvedApp.getCgpan());						
                    Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Pending last name:" + pendingLastName + "for cgpan" + pendingApp.getAppRefNo());
                  }
                  duplicateConditionList=duplicateApplication.getDuplicateCondition();
                  duplicateCondition=new DuplicateCondition();
    
                  duplicateCondition.setConditionName("Promoter Last Name");
                  duplicateCondition.setExistingValue(aLastName);
                  duplicateCondition.setNewValue(pLastName);
                    duplicateCondition.setPrevSSi(pSSiRef);
                    duplicateCondition.setExistSSi(aSSiRef);
                 
                  duplicateConditionList.add(duplicateCondition);
                  duplicateApplication.setDuplicateCondition(duplicateConditionList);
                  duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                  duplicateCriteria.setPLastName(true);
                  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                  //duplicateApps=true;
                  lastName = true;
                }
                else
                {
                  lastName = false;
                }
                String aAddress=((approvedApp.getBorrowerDetails()).getSsiDetails()).getAddress();
                if(Log.isDebugEnabled())
                {
                  Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Address :" + aAddress);
                }
                String pAddress=((pendingApp.getBorrowerDetails()).getSsiDetails()).getAddress();
                if(Log.isDebugEnabled())
                {					  
                 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved Address :" + pAddress);
                }
                String approvedAddress=aAddress.replaceAll(" ","");
                String pendingAddress=pAddress.replaceAll(" ","");
                if (approvedAddress.equalsIgnoreCase(pendingAddress))
                {
                   duplicateConditionList=duplicateApplication.getDuplicateCondition();
                   duplicateCondition=new DuplicateCondition();
                   duplicateCondition.setConditionName("SSI Address");
                   duplicateCondition.setExistingValue(aAddress);
                   duplicateCondition.setNewValue(pAddress);
                     duplicateCondition.setPrevSSi(pSSiRef);
                     duplicateCondition.setExistSSi(aSSiRef);
                 
                   duplicateConditionList.add(duplicateCondition);
                   duplicateApplication.setDuplicateCondition(duplicateConditionList);
                   duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                   duplicateCriteria.setAddress(true);
                   duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                   //duplicateApps=true;
                   address = true;
                }
                else
                {
                  address = false;
                }
                String aItpan=((approvedApp.getBorrowerDetails()).getSsiDetails()).getCpITPAN();
                String pItpan=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpITPAN();
                if((aItpan!=null && !(aItpan.equals(""))) && (pItpan!=null && !(pItpan.equals(""))))
                {
                   char array1[] = aItpan.toCharArray();
                   char array2[] = pItpan.toCharArray();
                   if(array1.length==10 && array2.length==10 && (Character.isLetter(array1[0]) && Character.isLetter(array1[1]) && Character.isLetter(array1[2]) && Character.isLetter(array1[3]) && Character.isLetter(array1[4]))
                    && (Character.isLetter(array2[0]) && Character.isLetter(array2[1]) && Character.isLetter(array2[2]) && Character.isLetter(array2[3]) && Character.isLetter(array2[4])) 
                    && (Character.isDigit(array1[5]) && Character.isDigit(array1[6]) && Character.isDigit(array1[7]) && Character.isDigit(array1[8])) 
                    && (Character.isDigit(array2[5]) && Character.isDigit(array2[6]) && Character.isDigit(array2[7]) && Character.isDigit(array2[8]))
                  &&	(Character.isLetter(array1[9]))
                  && (Character.isLetter(array2[9])))
                   {
                    if(aItpan.equalsIgnoreCase(pItpan))
                    {
                    
                    
                      duplicateConditionList=duplicateApplication.getDuplicateCondition();
                      duplicateCondition=new DuplicateCondition();
                      duplicateCondition.setConditionName("ITPAN Of Chief Promoter");
                      duplicateCondition.setExistingValue(aItpan);
                      duplicateCondition.setNewValue(pItpan);
                        duplicateCondition.setPrevSSi(pSSiRef);
                        duplicateCondition.setExistSSi(aSSiRef);
                 
                      duplicateConditionList.add(duplicateCondition);
                      duplicateApplication.setDuplicateCondition(duplicateConditionList);
                      duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                      duplicateCriteria.setItPAN(true);
                      duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                      //duplicateApps=true;
                      itpan = true;
                    }	
                    else
                    {
                      itpan = false;						 	
                    }
                   }
                   else
                   {
                   if(aItpan.equalsIgnoreCase(pItpan))
                    {
                      duplicateConditionList=duplicateApplication.getDuplicateCondition();
                      duplicateCondition=new DuplicateCondition();
                      duplicateCondition.setConditionName("ITPAN Of Chief Promoter");
                      duplicateCondition.setExistingValue(aItpan);
                      duplicateCondition.setNewValue(pItpan);
                        duplicateCondition.setPrevSSi(pSSiRef);
                        duplicateCondition.setExistSSi(aSSiRef);
                 
                      duplicateConditionList.add(duplicateCondition);
                      duplicateApplication.setDuplicateCondition(duplicateConditionList);
                      duplicateCriteria=duplicateApplication.getDuplicateCriteria();
                      duplicateCriteria.setItPAN(true);
                      duplicateApplication.setDuplicateCriteria(duplicateCriteria);
                      //duplicateApps=true;
                      itpan = true;
                    }	
                  //  itpan = true;
                   }
                }
                else
                {
                  itpan = true;
                }
                
/*						String aBankRefNo=approvedApp.getMliRefNo();						
						String pBankRefNo=pendingApp.getMliRefNo();

						String approvedBankRefNo=aBankRefNo.replaceAll(" ","");
						String pendingBankRefNo=pBankRefNo.replaceAll(" ","");
						
						if (approvedBankRefNo.equalsIgnoreCase(pendingBankRefNo))
						{

						   duplicateConditionList=duplicateApplication.getDuplicateCondition();
							  duplicateCondition=new DuplicateCondition();

							  duplicateCondition.setConditionName("Application Bank Reference No.");
							  duplicateCondition.setExistingValue(aBankRefNo);
							  duplicateCondition.setNewValue(pBankRefNo);

							  duplicateConditionList.add(duplicateCondition);
							  duplicateApplication.setDuplicateCondition(duplicateConditionList);

							duplicateCriteria=duplicateApplication.getDuplicateCriteria();
							  duplicateCriteria.setAddress(true);
							 duplicateApplication.setDuplicateCriteria(duplicateCriteria);

							//duplicateApps=true;
							bankRefNo = true;

						}
						else{
							
							bankRefNo = false;
						}
						*/
					  }
         //   if(unitName && firstName && lastName && address) bhuend
               int temp =0;
               if(regNo) temp +=1;
               if(itpan) temp +=1;
               //if(unitName) temp1+=1;
               if(firstName) temp+=1;
           //    if(middleName) temp+=1;
               if(lastName) temp+=1;
               //if(address) temp1+=1;
               if(unitName && address) 
               {
                   temp+=3;
               }
               else 
               {
                  if(unitName) temp+=2;
                  if(address) temp+=2;
               }
      //   //System.out.println("temp value:"+temp);
  					//if(regNo && itpan && unitName && firstName && middleName && lastName && address)
            if(temp >= 4)
            {
              Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending all matching : "+pendingApp.getAppRefNo());
              duplicateApps = true;
            //  //System.out.println("Duplicate Application true and Temp value :"+temp);
              //  //System.out.println("Duplicate Application true and pendingApp.getAppRefNo() value :"+pendingApp.getAppRefNo());
           //   break;
            }
            else
            {
              Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending all not matching : "+pendingApp.getAppRefNo());
              
              
           //   //System.out.println("pendingApp.getAppRefNo()...not duplicate..........");
  						duplicateApps = false;
						//break;
    	}
					  if (duplicateApps)
					  {
                                          
                                       //   //System.out.println("**********duplicateApps***************");
  						duplicateApplication.setOldCgpan(approvedApp.getCgpan());
          //    //System.out.println("Old Cgpan:"+approvedApp.getCgpan());
    					duplicateApplication.setNewAppRefNo(pendingApp.getAppRefNo());
           //   //System.out.println("New App ref No:"+pendingApp.getAppRefNo());
              duplicateApplication.setPrevLoanType(approvedApp.getLoanType());
              
              String prevSSi = applicationDAO.getSSIRefNo(approvedApp.getCgpan());
              String existSSi = applicationDAO.getSSIRefNo(pendingApp.getAppRefNo());
              
              
              
              duplicateApplication.setPrevSSi(prevSSi);
              duplicateApplication.setExistSSi(existSSi);
            //  //System.out.println("approvedApp.getLoanType():"+pendingApp.getPrevSSI());
          //  //System.out.println("pendingApp.getLoanType():"+approvedApp.getExistSSI());
             
              duplicateApplication.setExistLoanType(pendingApp.getLoanType());
      				duplicateApplication.setBorrowerId(((approvedApp.getBorrowerDetails()).getSsiDetails()).getCgbid());
  						duplicateApp.add(duplicateApplication);						
    					Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending " + i + " duplicate with approved " + j + " cgpan " +approvedApp.getCgpan());
        			Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "duplicateAppList size" +duplicateConditionList.size());						
          		break;
					  }
            else
            {
  						duplicateConditionList.clear();		
    				}
					}
					//Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "after checking with Approved Applications" );
					/*
					* This loop checks the pending application against the remaining pending
					* applications for duplicity
					*/
					if (!duplicateApps)
					{
                                        
						 //   //System.out.println("**********duplicateApps*******not duplicate********");
						duplicateApplication=new DuplicateApplication();
						duplicateCriteria=new DuplicateCriteria();
						//duplicateCondition=new DuplicateCondition();
						duplicateConditionList=new ArrayList();
						duplicateApplication.setDuplicateCondition(duplicateConditionList);
						duplicateApplication.setDuplicateCriteria(duplicateCriteria);
						for (int k=0;k<pendingAppSize;k++)
						{
							boolean regNo = false;
							boolean unitName = false;
							boolean firstName = false;
							boolean middleName = false;
							boolean lastName = false;
							boolean address = false;
							//boolean bankRefNo = false;
							boolean itpan = false;
          //    //System.out.println("K value:"+k+" I value:"+i);
							if(k!=i)
							{
								//duplicateCondition=new DuplicateCondition();
								Application remPendingApp=(Application)pendingApps.get(k);
								if(Log.isDebugEnabled())
								{								
									Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending " + i + " checking with pending " + k + " app ref no " +remPendingApp.getAppRefNo());
								}
								String rState = ((remPendingApp.getBorrowerDetails()).getSsiDetails()).getState();
 						    String pState = ((pendingApp.getBorrowerDetails()).getSsiDetails()).getState();
							  String rDistrict =  ((remPendingApp.getBorrowerDetails()).getSsiDetails()).getDistrict();
							  String pDistrict = ((pendingApp.getBorrowerDetails()).getSsiDetails()).getDistrict();
								if(rState.equalsIgnoreCase(pState) && rDistrict.equalsIgnoreCase(pDistrict))
								{
									if(Log.isDebugEnabled())
									{								
										Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "state and district are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
									}
									String rSsiRegNo=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getRegNo();
 							    String pSsiRegNo=((pendingApp.getBorrowerDetails()).getSsiDetails()).getRegNo();
								   if(((rSsiRegNo!=null && !rSsiRegNo.equals("")) && (pSsiRegNo!=null && !pSsiRegNo.equals(""))) && (!rSsiRegNo.equals("0") && !pSsiRegNo.equals("0")))
								   {
									   if (rSsiRegNo.equalsIgnoreCase(pSsiRegNo))
									   {
											if(Log.isDebugEnabled())
											{
												Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "ssi reg no are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
											}								
										   	duplicateConditionList=duplicateApplication.getDuplicateCondition();
											 duplicateCondition=new DuplicateCondition();
											 duplicateCondition.setExistingValue(rSsiRegNo);
											 duplicateCondition.setNewValue(pSsiRegNo);
											 duplicateCondition.setConditionName("SSI Reg No");
											 duplicateConditionList.add(duplicateCondition);
											 duplicateApplication.setDuplicateCondition(duplicateConditionList);
											 duplicateCriteria=duplicateApplication.getDuplicateCriteria();
											 duplicateCriteria.setSsiRegNo(true);
											 duplicateApplication.setDuplicateCriteria(duplicateCriteria);
											 //duplicateApps=true;
											 regNo = true;
									   }
									   else{
										regNo = false;
									   }
								   }
								   else{
									regNo = true;
								   }
									 String rSsiUnitName=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getSsiName();
									 String pSsiUnitName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getSsiName();
									 String remPendingSsiUnitName=rSsiUnitName.replaceAll(" ","");
									 String pendingSsiUnitName=pSsiUnitName.replaceAll(" ","");
									 if (remPendingSsiUnitName.equalsIgnoreCase(pendingSsiUnitName))
									 {
										if(Log.isDebugEnabled())
										{
											Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "ssi unti name are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
										}
										duplicateConditionList=duplicateApplication.getDuplicateCondition();
									  duplicateCondition=new DuplicateCondition();
										duplicateCondition.setExistingValue(rSsiUnitName);
										 duplicateCondition.setNewValue(pSsiUnitName);
										 duplicateCondition.setConditionName("Unit Name");
										 duplicateConditionList.add(duplicateCondition);
										 duplicateApplication.setDuplicateCondition(duplicateConditionList);
										  duplicateCriteria=duplicateApplication.getDuplicateCriteria();
										 duplicateCriteria.setUnitName(true);
										  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
										 //duplicateApps=true;
										 unitName = true;
									 }
									 else{
										unitName = false;
									 }
								   if(Log.isDebugEnabled())
								   {
									 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Rem Pending First Name :" + ((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName());
								   }
									 String rFirstName=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName();
									 String pFirstName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpFirstName();
								   if(Log.isDebugEnabled())
								   {
									 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","PEnding First Name :" + pFirstName);
								   }
									 String remPendingFirstName=rFirstName.replaceAll(" ","");
									 String pendingFirstName=pFirstName.replaceAll(" ","");
									 if (remPendingFirstName.equalsIgnoreCase(pendingFirstName))
									 {
										if(Log.isDebugEnabled())
										{
											Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "promoter name are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
										}
										 duplicateConditionList=duplicateApplication.getDuplicateCondition();
										   duplicateCondition=new DuplicateCondition();
										   duplicateCondition.setConditionName("Promoter First Name");
										   duplicateCondition.setExistingValue(rFirstName);
										   duplicateCondition.setNewValue(pFirstName);
										   duplicateConditionList.add(duplicateCondition);
										   duplicateApplication.setDuplicateCondition(duplicateConditionList);
										  duplicateCriteria=duplicateApplication.getDuplicateCriteria();
										 duplicateCriteria.setPFirstName(true);
										  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
										 //duplicateApps=true;
										 firstName = true;
									 }
									 else{
									 	
										firstName = false;
									 }
									 String rMiddleName=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCpMiddleName();
								   if(Log.isDebugEnabled())
								   {
									 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Rem Pending Middle Name :" + rMiddleName);
								   }
									 String pMiddleName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpMiddleName();
								 if (rMiddleName!=null && pMiddleName!=null)
									 {
										 String remPendingMiddleName=rMiddleName.replaceAll(" ","");
										 String pendingMiddleName=pMiddleName.replaceAll(" ","");
										 if (remPendingMiddleName.equalsIgnoreCase(pendingMiddleName))
										 {
											if(Log.isDebugEnabled())
											{
												Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "promoter middle name are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
											}
											duplicateConditionList=duplicateApplication.getDuplicateCondition();
											   duplicateCondition=new DuplicateCondition();
											   duplicateCondition.setConditionName("Promoter Middle Name");
											   duplicateCondition.setExistingValue(rMiddleName);
											   duplicateCondition.setNewValue(pMiddleName);
											   duplicateConditionList.add(duplicateCondition);
											   duplicateApplication.setDuplicateCondition(duplicateConditionList);
												duplicateCriteria=duplicateApplication.getDuplicateCriteria();
											   duplicateCriteria.setPMiddleName(true);
											  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
											 //duplicateApps=true;
											 middleName = true;
										 }
										 else{
											middleName = false;
										 }
									 }
									 else{
										middleName = true;
									 }
									 String rLastName=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCpLastName();
									 String pLastName=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpLastName();
									 String remPendingLastName=rLastName.replaceAll(" ","");
									 String pendingLastName=pLastName.replaceAll(" ","");
									 if (remPendingLastName.equalsIgnoreCase(pendingLastName))
									 {
										if(Log.isDebugEnabled())
										{
											Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "promoter last name are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
										}
										duplicateConditionList=duplicateApplication.getDuplicateCondition();
										   duplicateCondition=new DuplicateCondition();
										   duplicateCondition.setConditionName("Promoter Last Name");
										   duplicateCondition.setExistingValue(rLastName);
										   duplicateCondition.setNewValue(pLastName);
										   duplicateConditionList.add(duplicateCondition);
										   duplicateApplication.setDuplicateCondition(duplicateConditionList);
										 duplicateCriteria=duplicateApplication.getDuplicateCriteria();
										   duplicateCriteria.setPLastName(true);
										  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
										 //duplicateApps=true;
										 lastName = true;
									 }
									 else{
										lastName = false;
									 }
									 String rAddress=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getAddress();
								   if(Log.isDebugEnabled())
								   {
									 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Rem Pending  Address :" + rAddress);
								   }
									 String pAddress=((pendingApp.getBorrowerDetails()).getSsiDetails()).getAddress();
								   if(Log.isDebugEnabled())
								   {
									 Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Pending Address :" + pAddress);
								   }
									 String remPendingAddress=rAddress.replaceAll(" ","");
									 String pendingAddress=pAddress.replaceAll(" ","");
									 if (remPendingAddress.equalsIgnoreCase(pendingAddress))
									 {
										if(Log.isDebugEnabled())
										{
											Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "promoter address are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
										}
										duplicateConditionList=duplicateApplication.getDuplicateCondition();
										duplicateCondition=new DuplicateCondition();
										   duplicateCondition.setConditionName("SSI Address");
										   duplicateCondition.setExistingValue(rAddress);
										   duplicateCondition.setNewValue(pAddress);
										   duplicateConditionList.add(duplicateCondition);
										   duplicateApplication.setDuplicateCondition(duplicateConditionList);
										 duplicateCriteria=duplicateApplication.getDuplicateCriteria();
										   duplicateCriteria.setAddress(true);
										  duplicateApplication.setDuplicateCriteria(duplicateCriteria);
										 //duplicateApps=true;
										 address = true;
									 }
									 else{
										address = false;
									 }
									String aItpan=((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCpITPAN();
									String pItpan=((pendingApp.getBorrowerDetails()).getSsiDetails()).getCpITPAN();
									
                  
                  if((aItpan!=null && !(aItpan.equals(""))) && (pItpan!=null && !(pItpan.equals(""))))
									{
                   
										if(Log.isDebugEnabled())
										{
											Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","Approved itpan :" + aItpan);						
											Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicateApplications","remaining Pending itpan :" + pItpan);
										}
										 char array1[] = aItpan.toCharArray();
										 char array2[] = pItpan.toCharArray();
						 
										 if(array1.length==10 && array2.length==10 && (Character.isLetter(array1[0]) && Character.isLetter(array1[1]) && Character.isLetter(array1[2]) && Character.isLetter(array1[3]) && Character.isLetter(array1[4]))
										  && (Character.isLetter(array2[0]) && Character.isLetter(array2[1]) && Character.isLetter(array2[2]) && Character.isLetter(array2[3]) && Character.isLetter(array2[4])) 
										  && (Character.isDigit(array1[5]) && Character.isDigit(array1[6]) && Character.isDigit(array1[7]) && Character.isDigit(array1[8])) 
										  && (Character.isDigit(array2[5]) && Character.isDigit(array2[6]) && Character.isDigit(array2[7]) && Character.isDigit(array2[8]))
										&&	(Character.isLetter(array1[9]))
										&& (Character.isLetter(array2[9])))
										 {
											if(Log.isDebugEnabled())
											{
												Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "valid format are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
											}
											
											if(aItpan.equalsIgnoreCase(pItpan))
											{
												   duplicateConditionList=duplicateApplication.getDuplicateCondition();
												   duplicateCondition=new DuplicateCondition();
												   duplicateCondition.setConditionName("ITPAN Of Chief Promoter");
												   duplicateCondition.setExistingValue(aItpan);
												   duplicateCondition.setNewValue(pItpan);
												   duplicateConditionList.add(duplicateCondition);
												   duplicateApplication.setDuplicateCondition(duplicateConditionList);
												   duplicateCriteria=duplicateApplication.getDuplicateCriteria();
												   duplicateCriteria.setItPAN(true);
												   duplicateApplication.setDuplicateCriteria(duplicateCriteria);
												itpan = true;
											}	
											else{
												itpan = false;						 	
											}
										 }
										 else{
											if(Log.isDebugEnabled())
											{
												Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", " not valid format are same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
											}
                      if(aItpan.equalsIgnoreCase(pItpan))
											{
												duplicateConditionList=duplicateApplication.getDuplicateCondition();
												   duplicateCondition=new DuplicateCondition();
												   duplicateCondition.setConditionName("ITPAN Of Chief Promoter");
												   duplicateCondition.setExistingValue(aItpan);
												   duplicateCondition.setNewValue(pItpan);
												   duplicateConditionList.add(duplicateCondition);
												   duplicateApplication.setDuplicateCondition(duplicateConditionList);
												   duplicateCriteria=duplicateApplication.getDuplicateCriteria();
												   duplicateCriteria.setItPAN(true);
												   duplicateApplication.setDuplicateCriteria(duplicateCriteria);
											   	 itpan = true;
											}	
									//		itpan = true;
										 }
									}
									else{
							
										itpan = true;
									}
/*								String aBankRefNo=remPendingApp.getMliRefNo();						
									String pBankRefNo=pendingApp.getMliRefNo();

									String approvedBankRefNo=aBankRefNo.replaceAll(" ","");
									String pendingBankRefNo=pBankRefNo.replaceAll(" ","");
						
									if (approvedBankRefNo.equalsIgnoreCase(pendingBankRefNo))
									{

									   duplicateConditionList=duplicateApplication.getDuplicateCondition();
										  duplicateCondition=new DuplicateCondition();

										  duplicateCondition.setConditionName("Application Bank Reference No.");
										  duplicateCondition.setExistingValue(aBankRefNo);
										  duplicateCondition.setNewValue(pBankRefNo);

										  duplicateConditionList.add(duplicateCondition);
										  duplicateApplication.setDuplicateCondition(duplicateConditionList);

										duplicateCriteria=duplicateApplication.getDuplicateCriteria();
										  duplicateCriteria.setAddress(true);
										 duplicateApplication.setDuplicateCriteria(duplicateCriteria);

										//duplicateApps=true;
										bankRefNo = true;

									}
									else{
										
										bankRefNo = false;
									}
									*/
								}
								Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "state and district are not same for :" + remPendingApp.getAppRefNo() + "and " + pendingApp.getAppRefNo());
							 int temp1 =0;
               if(regNo) temp1 +=1;
               if(itpan) temp1 +=1;
               //if(unitName) temp1+=1;
               if(firstName) temp1+=1;
             //  if(middleName) temp1+=1;
               if(lastName) temp1+=1;
               //if(address) temp1+=1;
               if(unitName && address) 
               {
                   temp1+=3;
               }
               else 
               {
                  if(unitName) temp1+=2;
                  if(address) temp1+=2;
               }
               
							//	if(regNo && itpan && unitName && firstName && middleName && lastName && address)
							//	{
                if(temp1 >= 4)
                {
									Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending  checking with pending  app ref no " +remPendingApp.getAppRefNo());
									duplicateApps = true;
								}
								else{
									Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending  checking with pending  app ref no not all matching" +remPendingApp.getAppRefNo());
									duplicateApps = false;
									//break;
								}
                
          //       //System.out.println("App Ref N0: "+pendingApp.getAppRefNo() +" temp value:" +temp1 );
						
                
                 if (duplicateApps)
								  {
									//duplicateApplication.setOldCgpan(approvedApp.getCgpan());
									duplicateApplication.setOldCgpan(remPendingApp.getAppRefNo());									
									duplicateApplication.setNewAppRefNo(pendingApp.getAppRefNo());	
                  duplicateApplication.setPrevLoanType(remPendingApp.getLoanType());
         //         //System.out.println("remPendingApp.getLoanType():"+remPendingApp.getLoanType());
         //         //System.out.println("pendingApp.getLoanType():"+pendingApp.getLoanType());
                  duplicateApplication.setExistLoanType(pendingApp.getLoanType());
      		 				//duplicateApplication.setBorrowerId(((remPendingApp.getBorrowerDetails()).getSsiDetails()).getCgbid());
									Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "pending " + i + " duplicate with pending " + k + " appref no " + remPendingApp.getAppRefNo());
									duplicateApp.add(duplicateApplication);
                //   pendingApps.remove(i); -- added by sukumar@path for test purpose
               //   continue;
               break;
            
      		  }
								else{
									duplicateConditionList.clear();		
								}  
           		}//end of for loop
            
						}
						}//end of if loop
						
					//Log.log(Log.DEBUG, "ApplicationProcessor" , "checkDuplicateApplications", "after checking within pending applications" );
					}
					
					Log.log(Log.INFO, "ApplicationProcessor" , "checkDuplicateApplications", "Exited " );
					
					return duplicateApp;
				}


   /**
	* This method is used to submit the working capital enhancement application.
	* @param app
	* @roseuid 3972E7230193
	*/
   public void submitWcEnhancement(Application app,String createdBy) throws InvalidApplicationException,
															DatabaseException,MessageException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","submitWcEnhancement","Entered");

		appDAO.storeWcEnhancement(app,createdBy);
		
		app=null;
		createdBy=null;

		Log.log(Log.INFO,"ApplicationProcessor","submitWcEnhancement","Exited");

   }

   /**
	* This method is used to submit the working capital renewal details.
	* @param app
	* @return int
	* @roseuid 3972E7230196
	*/
   public String submitWcRenewal(Application app,String createdBy) throws InvalidApplicationException,
														DatabaseException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","submitWcRenewal","Entered");

	String appRefNo=appDAO.storeWcRenewal(app,createdBy);
	
	app=null;
	createdBy=null;

	Log.log(Log.INFO,"ApplicationProcessor","submitWcRenewal","Exited");

	return appRefNo;
   }


   /**
	* This method is used to  all application files. During upload elgiibility
	* check is done. All the valid applications are updated into database.Invalid
	* files are shown to the user.
	* @param applications
	* @return ArrayList
	* @roseuid 3973DCE2010F
	*/
   public ArrayList uploadApplication(ArrayList applications,String userId,String bankId,String zoneId,String branchId) throws InvalidApplicationException,
																				DatabaseException,NoApplicationFoundException
   {
   	
	Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Entered");
	ArrayList errorMessages=new ArrayList();
		if((applications!=null)&&(applications.size()!=0)){
			
			Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Application not null");
	   		
			boolean applicationValidVal=false;
			boolean errorMessageVal=false;
			int count=0;
			ApplicationDAO applicationDAO=new ApplicationDAO();
			String errorMessage="";
			
	   		
			int applicationSize=applications.size();
			for(int i=0;i<applicationSize;i++){
				
				Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Each Application Loop");
				Application application=(Application)applications.get(i);
								
				errorMessageVal=false;				   			
			/**
			 * Validations for each application object
			 */
				if(application.getIsVerified())
				{
					SSIDetails ssiDetails=application.getBorrowerDetails().getSsiDetails();
					BorrowerDetails borrowerDetails=application.getBorrowerDetails();
					borrowerDetails.setSsiDetails(ssiDetails);
					MCGFDetails mcgfDetails=application.getMCGFDetails();
					application.setBorrowerDetails(borrowerDetails);
					application.setMCGFDetails(mcgfDetails);
					//borrower covered validation
					Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Borrower Previously Covered :" + application.getBorrowerDetails().getPreviouslyCovered());
					
								
					if(application.getBorrowerDetails().getPreviouslyCovered().equals("Y"))
					{
						//checking for CGPAN
						if(application.getCgpanReference()!=null && !(application.getCgpanReference().equals("")))
						{
							String cgbid="";
							String cgpan=application.getCgpanReference().toUpperCase();
							
	//						if(application.getAdditionalTC() || application.getWcEnhancement() || application.getWcRenewal())
	//						{
							//checking if the cgpan exists in the database
							
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","cgpan:" + cgpan);
								ArrayList cgpans=appDAO.getAllCgpans();
								if(!(cgpans.contains(cgpan)))
								{
									errorMessageVal = true;
									if (application.getMliRefNo()!=null && !application.getMliRefNo().equals(""))
									{
										errorMessage="The CGPAN : " + cgpan + " is an invalid CGPAN " +  "(" + "Bank Reference No. :" + application.getMliRefNo() + ")";
									}
									else{
										errorMessage="The CGPAN : " + cgpan + " is an invalid CGPAN ";
									}
									
									errorMessages.add(errorMessage);							
								}
								else{
									
									ClaimsProcessor claimProcessor = new ClaimsProcessor();
								
									if(cgpan!=null && !(cgpan.equals("")))
									{
										cgbid = claimProcessor.getBorowwerForCGPAN(cgpan);		
								
										int claimCount = appDAO.getClaimCount(cgbid);
										if(claimCount>0)
										{
											errorMessageVal = true;
											errorMessage = "Application cannot be filed by this borrower " +  cgbid + " since Claim Application has been submitted";
											errorMessages.add(errorMessage);
										}
								
									}
									
								}
	
								try{
								//checking if the cgpan exists for the MLI ID passed
									
									appDAO.getAppForCgpan(bankId + zoneId + branchId,cgpan);
								}catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="The CGPAN :" + cgpan + " does not belong the Member " + bankId + zoneId + branchId;
									errorMessages.add(errorMessage);								
								}
								
	//						}
	//						else {
								
	//						}
							if(application.getAdditionalTC())
							{
								Application tcApplication = new Application();
								try{
									
									tcApplication=appDAO.getAppForCgpan(null,cgpan);
								
									if(!(tcApplication.getLoanType().equals("TC")))
									{
										errorMessageVal = true;
										errorMessage="The CGPAN : " + cgpan + " cannot be applied for Additional Term Loan " + "(" + "Bank Reference No. :" + tcApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									}							
									if(tcApplication.getLoanType().equals("TC") && (!tcApplication.getStatus().equals("EX")&& !tcApplication.getStatus().equals("AP")))
									{
										errorMessageVal = true;
										errorMessage="The CGPAN : " + cgpan + " is not expired / approved "  + "(" + "Bank Reference No. :" + tcApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									
									}				
									
								}
								catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="The CGPAN :" + cgpan + " is not valid for Additional Term Loan " ;
									errorMessages.add(errorMessage);								
								}								
							}						
							
							if(application.getWcRenewal())
							{
								Application wcRenewApplication = new Application();
								try{
									
									wcRenewApplication=appDAO.getAppForCgpan(null,application.getCgpanReference());
									if(!(wcRenewApplication.getLoanType().equals("WC")))
									{
										errorMessageVal = true;
										errorMessage="The CGPAN : " + cgpan + " cannot be applied for Renewal of Cover "  + "(" + "Bank Reference No. :" + wcRenewApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									}
									
									String renewcgpan=appDAO.checkRenewCgpan(application.getCgpanReference());
									if(renewcgpan.equals("0"))
									{
										if(!wcRenewApplication.getStatus().equals("EX"))
										{
											
											errorMessageVal = true;
											errorMessage="The CGPAN : " + cgpan + " is not expired " + "(" + "Bank Reference No. :" + wcRenewApplication.getMliRefNo() + ")";
											errorMessages.add(errorMessage);								
											
										}
									}
									else{
											
										Application tempWcRenewApplication=appDAO.getAppForCgpan(null,renewcgpan);
										if(!tempWcRenewApplication.getStatus().equals("EX"))
										{
											errorMessageVal = true;
											errorMessage="The CGPAN : " + renewcgpan + " is not expired " + "(" + "Bank Reference No. :" + tempWcRenewApplication.getMliRefNo() + ")";
											errorMessages.add(errorMessage);								
												
										}
											
										tempWcRenewApplication=null;
											
									}
										
									if(!renewcgpan.equals("0") && renewcgpan.substring(11,13).equals("R9"))
									{
										errorMessageVal = true;
										errorMessage="This CGPAN :" + cgpan + " cannot be renewed further "  + "(" + "Bank Reference No. :" + wcRenewApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									
									
									}
									
								}
								catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="The CGPAN :" + cgpan + " is not valid for Working Capital Renewal ";
									errorMessages.add(errorMessage);								
								}								
								
								
	/*							if (!(application.getCgpanReference().equals("")))
								{
									String renewCgpan=checkRenewCgpan(application.getCgpanReference());
									if(renewCgpan.equals("0"))
									{
										continue;
									}else if(!(application.getCgpanReference().equals(renewCgpan)))
									{
										errorMessage="The CGPAN : " + application.getCgpanReference() + " is not valid CGPAN for renewal";
										errorMessages.add(errorMessage);								
										
										
									}
								}
	*/
							
								wcRenewApplication=null;	
							}						
	
							if(application.getWcEnhancement())
							{
								Application wcEnhanceApplication = new Application();
								try{
									
									wcEnhanceApplication=appDAO.getAppForCgpan(null,cgpan);
									if(!(wcEnhanceApplication.getLoanType().equals("WC")))
									{
										errorMessageVal = true;
										errorMessage="The CGPAN : " + cgpan + " cannot be applied for WC Enhancement " + "(" + "Bank Reference No. :" + wcEnhanceApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									}
									if(wcEnhanceApplication.getLoanType().equals("WC") && !(wcEnhanceApplication.getStatus().equals("AP")))
									{
										errorMessageVal = true;
										errorMessage="The CGPAN : " + cgpan + " is not an Approved Application "+ "(" + "Bank Reference No. :" + wcEnhanceApplication.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									
									}
									
								}
								catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="The CGPAN :" + cgpan + " is not valid for Working Capital Enhancement ";
									errorMessages.add(errorMessage);								
								}				
								
								Application enhancementApp = new Application();				
								
								try{
									
									enhancementApp = appDAO.getApplication(null,application.getCgpanReference(),"");
									Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","application Wc:" +application.getWc().getEnhancedFundBased());							
									Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","wcEnhanceApplication Wc:" + enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned());
									if(application.getWc().getEnhancedFundBased() < enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned())
									{
										errorMessageVal = true;
										errorMessage="For Enhancement of CGPAN : " + cgpan + " The Enhanced Fund Based Amount should be greater than the current fund based sanctioned Amount "+ "(" + "Bank Reference No. :" + enhancementApp.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
	
									}
									if(application.getWc().getEnhancedNonFundBased() < enhancementApp.getProjectOutlayDetails().getWcNonFundBasedSanctioned())
									{
										errorMessageVal = true;
										errorMessage="For Enhancement of CGPAN : " + cgpan + " the Enhanced Non Fund Based amount should be greater than the current Non fund based sanctioned Amount "+ "(" + "Bank Reference No. :" + enhancementApp.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
	
									}
									if(application.getWc().getEnhancedFundBased() + application.getWc().getEnhancedNonFundBased() < 
									enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned() + enhancementApp.getProjectOutlayDetails().getWcNonFundBasedSanctioned())
									{
										errorMessageVal = true;
										errorMessage="For Enhancement of CGPAN : " + cgpan + " the total enhanced amount should be greater than the current sanctioned Amount "+ "(" + "Bank Reference No. :" + enhancementApp.getMliRefNo() + ")";
										errorMessages.add(errorMessage);								
									
									}									
								}
								catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="The CGPAN :" + cgpan + " is not valid for Working Capital Enhancement " + "(" + "Bank Reference No. :" + enhancementApp.getMliRefNo() + ")";
									errorMessages.add(errorMessage);								
								}								
								
								enhancementApp=null;
								wcEnhanceApplication=null;
								
							}
								
								
	/*							try{
									
									borrowerDetails=applicationDAO.fetchBorrowerDtls(cgbid,cgpan);
									application.setBorrowerDetails(borrowerDetails);
									Log.log(Log.DEBUG, "ApplicationProcessor", "uploadApplication", "covered here " + application.getBorrowerDetails().getPreviouslyCovered());
									
									if(borrowerDetails!=null)
									{
										int ssiRefNo=borrowerDetails.getSsiDetails().getBorrowerRefNo();
										application.getBorrowerDetails().getSsiDetails().setBorrowerRefNo(ssiRefNo);									
										
									}
									
								}catch(DatabaseException databaseException)
								{
									errorMessageVal = true;
									errorMessage="For Application with CGPAN Reference :" + application.getCgpanReference() + " , borrower Details are not available";
									//errorMessage="For the CGPAN :" + cgpan + "borrower details does not exist";
									errorMessages.add(errorMessage);								
								}
	*/							
								
	/*							if(borrowerDetails==null || borrowerDetails.equals(""))
								{
									applicationValidVal=true;		//invalid
									
									errorMessages.add(errorMessage);
								} 					
	*/											
							
							
						}
						//checking for CGBID
						else if(application.getBorrowerDetails().getSsiDetails().getCgbid()!=null && !(application.getBorrowerDetails().getSsiDetails().getCgbid().equals("")))
						{
							String cgpan="";
							String cgbid=application.getBorrowerDetails().getSsiDetails().getCgbid();
							
							//checkin if it's a valid borrower ID
							ArrayList bidList=appDAO.getAllBids();
							if(!(bidList.contains(cgbid)))
							{
								errorMessageVal = true;
								errorMessage="The CGBID :" + cgbid + " does not exist";
								errorMessages.add(errorMessage);								
								
							}
							
							
							
							//checking if the borrower belongs to the member logged in
							ClaimsProcessor cpProcessor =  new ClaimsProcessor();						
							ArrayList borrowerIds=cpProcessor.getAllBorrowerIDs(bankId+zoneId+branchId);
							if (!(borrowerIds.contains(cgbid)))
							{
								errorMessageVal = true;
								errorMessage="The CGBID :" + cgbid + " does not belong to the Member " + bankId+zoneId+branchId;
								errorMessages.add(errorMessage);								
							
							}
							else{
								
								if(cgbid!=null && !(cgbid.equals("")))
								{
									int claimCount = appDAO.getClaimCount(cgbid);
									if(claimCount>0)
									{
										errorMessageVal = true;
										errorMessage = "Application cannot be filed by this borrower " + cgbid + "since Claim Application has been submitted";
										errorMessages.add(errorMessage);
									}
	
								}
								
							}
							
	/*						try{
									
								borrowerDetails=applicationDAO.fetchBorrowerDtls(cgbid,cgpan);
								application.setBorrowerDetails(borrowerDetails);
								if(borrowerDetails!=null)
								{
									int ssiRefNo=borrowerDetails.getSsiDetails().getBorrowerRefNo();
									application.getBorrowerDetails().getSsiDetails().setBorrowerRefNo(ssiRefNo);
									
								}
							}catch(DatabaseException databaseException)
							{
								errorMessageVal = true;
								errorMessage="For Application with CGBID :" + cgbid + " , borrower Details are not available";
								//errorMessage="For the CGPAN :" + cgpan + "borrower details does not exist";
								errorMessages.add(errorMessage);								
							}
			*/				
							
						}
		   				
					}
					
					
					//checkin for mcgf User
					MLIInfo mliInfo=new MLIInfo();
					Registration registration=new Registration();
					Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","bank Id:" + bankId);
					Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","zone Id:" + zoneId);
					Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","branchId:" + branchId);
					mliInfo=registration.getMemberDetails(bankId,zoneId,branchId);
					String mcgfFlag=mliInfo.getSupportMCGF();
					Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","MCGF Flag:" + mcgfFlag);			
					//////System.out.println("mcgf details:" + application.getMCGFDetails());	
					//////System.out.println("participating bank :" + application.getMCGFDetails().getParticipatingBank());
					//Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","MCGF Participating Bank:" + application.getMCGFDetails().getParticipatingBank());
					
					if(mcgfFlag.equals("Y")&& application.getMCGFDetails()==null)
					{
						errorMessageVal = true;
						errorMessage="For the MCGF User" + bankId+zoneId+branchId +" ,MCGF Details are not available.Hence this application cannot be submitted";	
						errorMessages.add(errorMessage);
						
					}
					
					if(application.getMCGFDetails()!=null)
					{
						if((application.getMCGFDetails().getParticipatingBank()!=null && !(application.getMCGFDetails().getParticipatingBank().equals(""))) && mcgfFlag.equals("N"))
						{
							errorMessageVal = true;
							errorMessage="For an Non - MCGF User ,MCGF Details should not be available.Hence this application cannot be submitted";	
							errorMessages.add(errorMessage);
						
						}
						
						if(mcgfFlag.equals("Y") && !(application.getMCGFDetails().getMcgfId().equals(bankId+zoneId+branchId)))				
						{
							errorMessageVal = true;
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","MCGF ID:" + application.getMCGFDetails().getMcgfId());
							errorMessage="For an MCGF User ,MCGF ID should be equal to the logged In user ";
							errorMessages.add(errorMessage);					
							
						}
					
						MCGSProcessor mcgsProcessor=new MCGSProcessor();
						if(application.getMCGFDetails().getMcgfId().equals(bankId+zoneId+branchId))
						{
							ArrayList participatingBanks=mcgsProcessor.getAllParticipatingBanks(bankId+zoneId+branchId);
							if(participatingBanks==null || participatingBanks.size()==0)
							{
								errorMessageVal = true;
								errorMessage="For the MCGF ID " + bankId+zoneId+branchId+ " no participating banks are available.Hence application cannot be submitted";
								errorMessages.add(errorMessage);
											
							}
						}
					}
					
					if(!errorMessageVal)
					{	
						application.setBankId(bankId);		
						application.setZoneId(zoneId);
						application.setBranchId(branchId);					
						
						if(application.getAdditionalTC())
						{						
							borrowerDetails=applicationDAO.fetchBorrowerDtls("",application.getCgpanReference());
	//						application.setBorrowerDetails(borrowerDetails);
							Log.log(Log.DEBUG, "ApplicationProcessor", "uploadApplication", "covered here " + application.getBorrowerDetails().getPreviouslyCovered());
									
							if(borrowerDetails!=null)
							{
								int ssiRefNo=borrowerDetails.getSsiDetails().getBorrowerRefNo();
								SSIDetails ssiDtl = new SSIDetails();
								ssiDtl.setBorrowerRefNo(ssiRefNo);
								BorrowerDetails borrowerDtl = new BorrowerDetails();
								borrowerDtl.setSsiDetails(ssiDtl);
								application.setBorrowerDetails(borrowerDtl);									
										
							}
							application.setMliID(application.getBankId()+application.getZoneId()+application.getBranchId());
							
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Additional Term Loan");						
							appDAO.submitAddlTermCredit(application,userId);
							
							errorMessage="Application for CGPAN " + application.getCgpanReference() + " has been submitted successfully for Additional Term Loan";
							errorMessages.add(errorMessage);						
							
							
						}else if(application.getWcEnhancement())
						{
							
							Application enhanceApplication=appDAO.getAppForCgpan(null,application.getCgpanReference());
							Application enhancementApp = appDAO.getApplication(null,enhanceApplication.getCgpan(),"");						
							String status = enhanceApplication.getStatus();
							application.setStatus(status);
							application.setAppRefNo(enhanceApplication.getAppRefNo());
							application.setMliBranchName(enhanceApplication.getMliBranchName());
							application.setMliRefNo(enhanceApplication.getMliRefNo());
							WorkingCapital enhanceWc=application.getWc();
							enhanceWc.setFundBasedLimitSanctioned(enhanceWc.getEnhancedFundBased());
							enhanceWc.setNonFundBasedLimitSanctioned(enhanceWc.getEnhancedNonFundBased());
							enhanceWc.setLimitFundBasedInterest(enhanceWc.getEnhancedFBInterest());
							enhanceWc.setLimitNonFundBasedCommission(enhanceWc.getEnhancedNFBComission());
							enhanceWc.setCreditFundBased(enhanceWc.getEnhancedFundBased());
							enhanceWc.setCreditNonFundBased(enhanceWc.getEnhancedNonFundBased());
							
							if(enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned()==0 && enhanceWc.getEnhancedFundBased()!=0)
							{
								enhanceWc.setLimitFundBasedSanctionedDate(enhanceWc.getEnhancementDate());	
							}
							else if(enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned()!=0 && enhanceWc.getEnhancedFundBased()!=0)
							{
								enhanceWc.setLimitFundBasedSanctionedDate(enhanceWc.getEnhancementDate());
							}
							if(enhancementApp.getProjectOutlayDetails().getWcNonFundBasedSanctioned()==0 && enhanceWc.getEnhancedNonFundBased()!=0)
							{
								enhanceWc.setLimitNonFundBasedSanctionedDate(enhanceWc.getEnhancementDate());
							}
							else if(enhancementApp.getProjectOutlayDetails().getWcNonFundBasedSanctioned()!=0 && enhanceWc.getEnhancedNonFundBased()!=0)
							{
								enhanceWc.setLimitNonFundBasedSanctionedDate(enhanceWc.getEnhancementDate());
							}
							
							enhanceWc.setEnhancedFundBased(enhanceWc.getEnhancedFundBased() - enhancementApp.getProjectOutlayDetails().getWcFundBasedSanctioned());
							enhanceWc.setEnhancedNonFundBased(enhanceWc.getEnhancedNonFundBased() - enhancementApp.getProjectOutlayDetails().getWcNonFundBasedSanctioned());
							
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","enhanced fund based :" + enhanceWc.getEnhancedNonFundBased());
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","credit to be guarantee :" + enhanceWc.getCreditNonFundBased());
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","enhanced fund based :" + enhanceWc.getEnhancedFundBased());
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","enhanced fund based :" + enhanceWc.getCreditFundBased());
							
							application.setWc(enhanceWc);
							
							//Application enhanceApp=appDAO.getPartApplication(null,application.getCgpanReference(),"");
							
							borrowerDetails=applicationDAO.fetchBorrowerDtls("",application.getCgpanReference());
							int ssiRefNo=borrowerDetails.getSsiDetails().getBorrowerRefNo();
							SSIDetails ssiDtl = new SSIDetails();
							ssiDtl.setBorrowerRefNo(ssiRefNo);
							BorrowerDetails borrowerDtl = new BorrowerDetails();
							borrowerDtl.setSsiDetails(ssiDtl);
							borrowerDtl.setPreviouslyCovered("Y");
							application.setBorrowerDetails(borrowerDtl);									
							
							application.setMliID(application.getBankId()+application.getZoneId()+application.getBranchId());
							
							try{
								
								appDAO.storeWcEnhancement(application,userId);
							}							
							catch(DatabaseException exception)
							{
								if(exception.getMessage().equalsIgnoreCase("Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee"))
								{
									
									errorMessage="Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee";
									errorMessages.add(errorMessage);
									
								}
								if(exception.getMessage().equalsIgnoreCase("Borrower has crossed his exposure limit.Hence ineligible to submit a new application"))
								{
									errorMessage = "Borrower has crossed his exposure limit.Hence ineligible to submit a new application";
									errorMessages.add(errorMessage);
								}
							}
							
							
							errorMessage="Application for CGPAN " + application.getCgpanReference() + " has been submitted successfully for Working Capital Enhancement";						
							errorMessages.add(errorMessage);
							
						}else if(application.getWcRenewal())
						{
							borrowerDetails=applicationDAO.fetchBorrowerDtls("",application.getCgpanReference());
	//						application.setBorrowerDetails(borrowerDetails);
							Log.log(Log.DEBUG, "ApplicationProcessor", "uploadApplication", "cgpan reference" + application.getCgpanReference());
							Log.log(Log.DEBUG, "ApplicationProcessor", "uploadApplication", "covered here " + application.getBorrowerDetails().getPreviouslyCovered());
									
							if(borrowerDetails!=null)
							{
								int ssiRefNo=borrowerDetails.getSsiDetails().getBorrowerRefNo();
								SSIDetails ssiDtl = new SSIDetails();
								ssiDtl.setBorrowerRefNo(ssiRefNo);
								BorrowerDetails borrowerDtl = new BorrowerDetails();
								borrowerDtl.setSsiDetails(ssiDtl);
								application.setBorrowerDetails(borrowerDtl);									
										
							}
							
							WorkingCapital tempWc = application.getWc();
							tempWc.setFundBasedLimitSanctioned(tempWc.getRenewalFundBased());
							tempWc.setNonFundBasedLimitSanctioned(tempWc.getRenewalNonFundBased());
							tempWc.setLimitFundBasedInterest(tempWc.getRenewalFBInterest());
							tempWc.setLimitNonFundBasedCommission(tempWc.getRenewalNFBComission());
							tempWc.setLimitFundBasedSanctionedDate(tempWc.getRenewalDate());	
							
							tempWc.setCreditFundBased(tempWc.getRenewalFundBased());
							tempWc.setCreditNonFundBased(tempWc.getRenewalNonFundBased());
							
							if(tempWc.getRenewalFundBased()!=0 && tempWc.getRenewalNonFundBased()==0)
							{
								tempWc.setLimitFundBasedSanctionedDate(tempWc.getRenewalDate());	
							}
							else if(tempWc.getRenewalFundBased()==0 && tempWc.getRenewalNonFundBased()!=0)
							{
								tempWc.setLimitNonFundBasedSanctionedDate(tempWc.getRenewalDate());
							}
							else if(tempWc.getRenewalFundBased()!=0 && tempWc.getRenewalNonFundBased()!=0)
							{
								tempWc.setLimitNonFundBasedSanctionedDate(tempWc.getRenewalDate());
								tempWc.setLimitNonFundBasedSanctionedDate(tempWc.getRenewalDate());
							}

							application.setWc(tempWc);
							
							String renewcgpan=appDAO.checkRenewCgpan(application.getCgpanReference());
							Log.log(Log.DEBUG, "ApplicationProcessor", "uploadApplication", "renewcgpan" + renewcgpan);
							
							if(renewcgpan.equals("0"))
							{
								//cgpan=cgpan;
								application.setCgpanReference(application.getCgpanReference());
							}else{
							
								application.setCgpanReference(renewcgpan);				
							
							}
	        //    //System.out.println("Renewal Cgpan:"+renewcgpan);
							application.setMliID(application.getBankId()+application.getZoneId()+application.getBranchId());
							try{
								
								appDAO.storeWcRenewal(application,userId);
							}
							
							catch(DatabaseException exception)
							{
								if(exception.getMessage().equalsIgnoreCase("Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee"))
								{
									
									errorMessage="Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee";
									errorMessages.add(errorMessage);
									
								}
								if(exception.getMessage().equalsIgnoreCase("Borrower has crossed his exposure limit.Hence ineligible to submit a new application"))
								{
									errorMessage = "Borrower has crossed his exposure limit.Hence ineligible to submit a new application";
									errorMessages.add(errorMessage);
								}
							}
							
							
							errorMessage="Application for CGPAN " + application.getCgpanReference() + " has been submitted successfully for Working Capital Renewal";						
							errorMessages.add(errorMessage);						
							
						}else{
							
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","New Application");
							Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","borrower vcovered value" +application.getBorrowerDetails().getPreviouslyCovered());
							if(application.getBorrowerDetails().getPreviouslyCovered().equals("Y"))
							{
								String cgpanValue = application.getCgpanReference();
          //      //System.out.println("cgpanValue:"+cgpanValue);
								
								if(cgpanValue!=null && !(cgpanValue.equals("")))
								{								
									application.setCgpan(cgpanValue);
								}
							}
							application.setMliID(application.getBankId()+application.getZoneId()+application.getBranchId());
							try{
								
								appDAO.submitApplication(application,userId);
							}
							catch(DatabaseException exception)
							{
								if(exception.getMessage().equalsIgnoreCase("Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee"))
								{
									
									errorMessage="Credit to be Guaranteed Amount should be within the eligible amount available for Guarantee";
									errorMessages.add(errorMessage);
									
								}
								if(exception.getMessage().equalsIgnoreCase("Borrower has crossed his exposure limit.Hence ineligible to submit a new application"))
								{
									errorMessage = "Borrower has crossed his exposure limit.Hence ineligible to submit a new application";
									errorMessages.add(errorMessage);
								}
							}
							
							
							count ++;						
						}
						
						
					}
					//String incorrectFile="No. of Applications with Errors :" + count;
					
				}
				else{
					
					errorMessage = "This application has not been verified by the NO.Hence cannot be uploaded ";						
					errorMessages.add(errorMessage);			
				}
				
			}

			if(count!=0)
			{				
				errorMessage=count + " new Application(s) have been successfully submitted";						
				errorMessages.add(errorMessage);						
			
			}

	   		
		}
		else{
			String errorMessage="There are no applications for Upload";
			errorMessages.add(errorMessage);
		}
	Log.log(Log.INFO,"ApplicationProcessor","uploadApplication","Exited");
		return errorMessages;
   }
   /**
	* This method is used to check whether the supplied application is valid or not.
	* @param application
	* @return boolean
	* @throws InvalidApplicationException
	* @roseuid 3973DD210070
	*
   public boolean checkValidity(Application application) throws InvalidApplicationException
   {
	return false;
   }

   /**
	* This method is used to check whether the approved amount  is less than or equal
	* to the set limit in the risk management module. If no limits are available,
	* this method returns true.
	* @param application
	* @param globalLimit
	* @param userLimit
	* @return boolean
	* @roseuid 397A7D4500F8
	*/
   /*public boolean checkLimit(Application application, GloablLimits globalLimit, double userLimit)
   {
	return true;
   }*/

   /**
	* This method is used to get the cap amount maintained for the selected
	* sub-scheme.
	* @param subScheme
	* @return double
	* @roseuid 397A9F3001E4
	*
   public double getSubSchemeDetails(SubScheme subScheme) throws DatabaseException
   {
		if(subScheme!=null){
			 //double cap=appDAO.getSubSchemeDetails(subScheme);
			 //return cap;
		}

		return 0.0;
   }

   /**
	* This method returns the application for a combination of MLI id, CGBID and
	* CGPAN. If more than one parameter is available, then both the parameter would
	* be combined in the search query to get the Application. If only MLI id or CGBID
	* s present, list of CGPANs and Application Reference numbers would be given.
	* @param mliID
	* @param cgbid
	* @param cgpan
	* @return com.cgtsi.application.Application
	* @throws NoApplicationFoundException
	* @roseuid 3982653C014F
	*
   public Application getApplication(String mliID, String cgbid, String cgpan) throws NoApplicationFoundException,DatabaseException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","getApplication","Entered");

		String appRefNo=null;
		Application application=getApplication(cgpan,appRefNo);

		Log.log(Log.INFO,"ApplicationProcessor","getApplication","Exited");

		return application;
   }


   /**
	  * This method returns the application for a combination of MLI id, CGBID,CGPAN
	  * and application reference no. If more than one parameter is available, then both the parameter would
	  * be combined in the search query to get the Application. If only MLI id or CGBID
	  * s present, list of CGPANs and Application Reference numbers would be given.
	  * @param mliID
	  * @param cgbid
	  * @param cgpan
	  * @param appRefNo
	  * @return com.cgtsi.application.Application
	  * @throws NoApplicationFoundException
	  * @roseuid 3982653C014F
	  */
	 public Application getApplication(String mliID,String cgpan,String appRefNo) throws NoApplicationFoundException,DatabaseException
	 {
		 Log.log(Log.INFO,"ApplicationProcessor","getApplication","Entered");

		Application application=null;

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of CGPAN:" + cgpan);

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of App Ref No:" + appRefNo);

		//if any of the following conditions is satisfied,the application form is returned
		if((!(cgpan.equals("")))||
			(!(appRefNo.equals("")))){
				application=appDAO.getApplication(mliID,cgpan,appRefNo);

			if(application==null){
				throw new NoApplicationFoundException("No Application Found");
			}

		}/*else if((!(mliID.equals("")))||(!(cgbid.equals("")))||(!(borrowerName.equals("")))){
			application=null;

		}	*/

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of Application :" + application);

		Log.log(Log.INFO,"ApplicationProcessor","getApplication","Exited");

		return application;
	 }

/**
	 * This method is used to fetch the Date Submitted and Borrower Reference Number
	 * and Loan details of the Application.
	 * @param mliID
	 * @param cgbid
	 * @param cgpan
	 * @param appRefNo
	 * @return com.cgtsi.application.Application
	 * @throws NoApplicationFoundException
	 */

	 public Application getPartApplication(String mliID,String cgpan,String appRefNo) throws NoApplicationFoundException,DatabaseException
		 {
			 Log.log(Log.INFO,"ApplicationProcessor","getPartApplication","Entered");

			Application application=null;
////System.out.println("PATH Inside ApplicationProcessor  getPartApplication");
			Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of CGPAN:" + cgpan);

			Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of App Ref No:" + appRefNo);

				//if any of the following conditions is satisfied,the application form is returned
			if((!(cgpan.equals("")))||
				(!(appRefNo.equals("")))){
					application=appDAO.getPartApplication(mliID,cgpan,appRefNo);
          ////System.out.println("Comming out PATH with application in AppProcessor from appDAO.getPartApplication(mliID,cgpan,appRefNo)");
				if(application==null){
					throw new NoApplicationFoundException("No Application Found");
				}

			}/*else if((!(mliID.equals("")))||(!(cgbid.equals("")))||(!(borrowerName.equals("")))){
				application=null;

			}	*/



			Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of Application :" + application);

			Log.log(Log.INFO,"ApplicationProcessor","getPartApplication","Exited");

			return application;
	 }

/////////////ADDED BY RITESH PATH ON 1DEC2006


	 public Application getPartApplicationPath(String mliID,String cgpan,String appRefNo) throws NoApplicationFoundException,DatabaseException
		 {
			 //Log.log(Log.INFO,"ApplicationProcessor","getPartApplication","Entered");

			Application application=null;
   //   //System.out.println("PATH Inside ApplicationProcessor  getPartApplication");
			Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of CGPAN:" + cgpan);

			Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of App Ref No:" + appRefNo);

				//if any of the following conditions is satisfied,the application form is returned
			if((!(cgpan.equals("")))||
				(!(appRefNo.equals("")))){
					application=appDAO.getPartApplicationPath(mliID,cgpan,appRefNo);
          ////System.out.println("Comming out PATH with application in AppProcessor from appDAO.getPartApplication(mliID,cgpan,appRefNo)");
				if(application==null){
					throw new NoApplicationFoundException("No Application Found");
				}

			}
			//Log.log(Log.DEBUG,"ApplicationProcessor","getPartApplication","Value of Application :" + application);

			//Log.log(Log.INFO,"ApplicationProcessor","getPartApplication","Exited");

			return application;
	 }

//////////END CODE BY RITESH PATH/////////////
	/**
	  * This method is used to fetch all the pending applications for eligibility check
	  * for the CGTSI users decision.
	  * @return ArrayList
	  * @roseuid 3982868C0351
	  */
	 public ArrayList getPendingApps() throws DatabaseException,NoApplicationFoundException
	 {
		  Log.log(Log.INFO,"ApplicationProcessor","getPendingApps","Entered");

		  ArrayList eligibilityApps=(ArrayList)appDAO.getPendingApps();

			  Log.log(Log.INFO,"ApplicationProcessor","getPendingApps","Exited");

			  return eligibilityApps;
	 }


   /**
	* This method is used to fetch all the applications after doing eligibility check
	* for the CGTSI users decision.
	* @return ArrayList
	* @roseuid 3982868C0351
	*/
   public EligibleApplication getAppsForEligibilityCheck(String appRefNo) throws DatabaseException,NoApplicationFoundException
   {
		//Log.log(Log.INFO,"ApplicationProcessor","getAppsForEligibilityCheck","Entered");

		EligibleApplication eligibleApplication=appDAO.getAppsForEligibilityCheck(appRefNo);

		//Log.log(Log.INFO,"ApplicationProcessor","getAppsForEligibilityCheck","Exited");

			return eligibleApplication;
   }




   /**
	* This method updates the decision taken on the probable duplicate applications
	* by the CGTSI user.
	* @param applications
	* @roseuid 3984FF0B02A3
	*/
	public void updateApplicationsStatus(Application application,String createdBy) throws DatabaseException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","updateApplicationsStatus","Entered");

		appDAO.updateApplicationStatus(application,createdBy);

		Log.log(Log.INFO,"ApplicationProcessor","updateApplicationsStatus","Exited");

   }

/**
   * 
   * @param application
   * @param createdBy
   * @throws com.cgtsi.common.DatabaseException
   */
	public void updateRejectedApplicationsStatus(Application application,String createdBy) throws DatabaseException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","updateRejectedApplicationsStatus","Entered");

		appDAO.updateRejectedApplicationStatus(application,createdBy);

		Log.log(Log.INFO,"ApplicationProcessor","updateRejectedApplicationsStatus","Exited");

   }

   /**
	* This method is used to update the modified application details.
	* @param application
	* @roseuid 39A64BBE013A
	*/
   public void updateApplication(Application application,String createdBy) throws DatabaseException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","updateApplication","Entered");
		if(application!=null){

			Log.log(Log.INFO,"ApplicationProcessor","updateApplication","Before updating in the processor class....");

			appDAO.updateApplication(application,createdBy);
			Log.log(Log.INFO,"ApplicationProcessor","updateApplication","After updating in the processor class....");

			Log.log(Log.INFO,"ApplicationProcessor","updateApplication","Exited");
			
			application=null;

		}else{
			return;
		}
   }

   /**
	* This method is used to get all the applications pending for re-approval.
	* @return ArrayList
	* @roseuid 39A64C3B02CA
	*/
   public ArrayList getApplicationsForReapproval(String userId) throws DatabaseException,NoApplicationFoundException
   {
	   Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","Entered");
	   
	   ArrayList ineligibleReapproveApps = new ArrayList();
		ArrayList eligibleReapproveApps = new ArrayList();		
		ArrayList reapprovalAppsList = new ArrayList();		
		ArrayList appRefNos = new ArrayList();
		
		double reapprovedAmt;
		
		Application application = null;
		
		String appRefNo;

		ArrayList reapprovalApplications=appDAO.getApplicationsForReapproval(userId);
		ArrayList tcReapproveApps = (ArrayList)reapprovalApplications.get(0);
		ArrayList wcReapproveApps = (ArrayList)reapprovalApplications.get(1);
				
		int	tcSize = tcReapproveApps.size();	
		int wcSize = wcReapproveApps.size();
                
               
		/**
		 * This loop adds all the application reference nos of the term credit applications
		 */
		for(int j=0 ; j<tcSize; j++)
		{
			Application tcApplication = (Application)tcReapproveApps.get(j);	
                        
		    //System.out.println("tcApplication.getCgpan()..."+tcApplication.getCgpan());
			Application tempApplication = appDAO.getApplication(null,tcApplication.getCgpan(),"");
			reapprovedAmt = calApprovedAmount(tempApplication);			
			tcApplication.setReapprovedAmount(reapprovedAmt);	
		//	String tcAppRefNo = tcApplication.getAppRefNo();
		Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","tc status:" + tcApplication.getStatus());
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","tc App Ref No :" + tcApplication);
			TermLoan tempTermLoan = tcApplication.getTermLoan();
			if(tempApplication.getLoanType().equals("CC"))
			{			
				tempTermLoan.setCreditGuaranteed(tempApplication.getTermLoan().getCreditGuaranteed() + 
												tempApplication.getWc().getCreditFundBased() + 
												tempApplication.getWc().getCreditNonFundBased());
			}
			else{
				tempTermLoan.setCreditGuaranteed(tempApplication.getTermLoan().getCreditGuaranteed());	
			}
			
			tcApplication.setTermLoan(tempTermLoan);
			tcApplication.setReapprovalRemarks(tempApplication.getReapprovalRemarks());
			appRefNos.add(tcApplication);
			
			tempApplication = null;
		}
		
		/**
		 * This loop adds all the application reference nos of the working capital applications
		 */
		for(int k=0 ; k<wcSize; k++)
		{
			Application wcApplication = (Application)wcReapproveApps.get(k);
			Application tempApplication = appDAO.getApplication(null,wcApplication.getCgpan(),"");
			reapprovedAmt = calApprovedAmount(tempApplication);
			wcApplication.setReapprovedAmount(reapprovedAmt);
			
			TermLoan tempTc = wcApplication.getTermLoan();
			tempTc.setCreditGuaranteed(tempApplication.getWc().getCreditFundBased() + tempApplication.getWc().getCreditNonFundBased());
			wcApplication.setReapprovalRemarks(tempApplication.getReapprovalRemarks());
			wcApplication.setTermLoan(tempTc);			
			
			//String wcAppRefNo = wcApplication.getAppRefNo();
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","wc status:" + wcApplication.getStatus());
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","wc App Ref No :" + wcApplication);
			appRefNos.add(wcApplication);
		}

		//EligibleApplication eligibleApplication = new EligibleApplication();
		
		int appRefNosList= appRefNos.size();
		
		for(int i=0; i<appRefNosList; i++)
		{
			Application reapproveApp = (Application)appRefNos.get(i);
			String reapproveAppRefNo = reapproveApp.getAppRefNo();			
			EligibleApplication eligibleApplication = appDAO.getAppsForEligibilityCheck(reapproveAppRefNo);
			
			if(!(eligibleApplication.getFailedCondition().equals("")))
			{
				eligibleApplication.setAppRefNo(reapproveAppRefNo);
				eligibleApplication.setBorrowerRefNo(reapproveApp.getBorrowerDetails().getSsiDetails().getBorrowerRefNo());
				eligibleApplication.setCgpan(reapproveApp.getCgpan());
				eligibleApplication.setSubmissiondate(reapproveApp.getSubmittedDate().toString());
				eligibleApplication.setEligibleApprovedAmount(reapproveApp.getApprovedAmount());
				eligibleApplication.setEligibleCreditAmount(reapproveApp.getReapprovedAmount());
				eligibleApplication.setEligibleCreditGuaranteed(reapproveApp.getTermLoan().getCreditGuaranteed());
				eligibleApplication.setStatus(reapproveApp.getStatus());
				eligibleApplication.setEligibleRemarks(reapproveApp.getReapprovalRemarks());				
				ineligibleReapproveApps.add(eligibleApplication);
				
			}else{		
				
				eligibleReapproveApps.add(reapproveApp);
				
			}			
		}
		reapprovalAppsList.add(ineligibleReapproveApps);
		reapprovalAppsList.add(eligibleReapproveApps);			

		Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForReapproval","Exited");

		return reapprovalAppsList;

   }


   /**
	* This method is used to view the application details by supplying either
	* Borrower reference number or application reference number or CGPAN.
	*
	* The type of ID is resolved by the accompaning type parameter.
	*
	* 0-Borrower Ref. Number
	* 1- Application reference number
	* 2- CGPAN
	* @param id
	* @param type
	* @roseuid 39A64D7B0144
	*
   public void viewApplication(String id, int type) throws DatabaseException
   {

   }

   /**
	* This method is used to submit the additional term credit application.
	* @param application
	* @roseuid 39B0D32A00FC
	*/
   public String submitAddlTermCredit(Application application,String createdBy) throws DatabaseException,InvalidApplicationException
   {
		Log.log(Log.INFO,"ApplicationProcessor","submitAddlTermCredit","Entered");

		String appRefNo=appDAO.submitAddlTermCredit(application,createdBy);

		Log.log(Log.INFO,"ApplicationProcessor","submitAddlTermCredit","Exited");

		return appRefNo;

   }

   /**
	* This method returns an ArrayList of CGPANs for the given id and type.
	* @param id and type
	* @return ArrayList
	* @roseuid 39B0ECCC02EC
	*/
   public ArrayList getCgpans(String mliId,String borrowerId,int type,String borrowerName) throws DatabaseException,
												NoApplicationFoundException
   {
		Log.log(Log.INFO,"ApplicationProcessor","getCgpans","Entered");

		Log.log(Log.DEBUG,"ApplicationProcessor","getCgpans","Value of MLI ID" + mliId);

		ArrayList cgpansList=null;
			cgpansList=appDAO.getCgpans(mliId,borrowerId,type,borrowerName);

			Log.log(Log.INFO,"ApplicationProcessor","getCgpans","Exited");

		return cgpansList;
   }

   /**
   * This method returns an ArrayList of Application reference numbers for the given
   * id and type.
   * id can be memberID or CGBID
   * @param id and type
   * @return ArrayList
   * @roseuid 39B0ECCC02EC
   */
   public ArrayList getAppRefNos(String mliId,String borrowerId,String borrowerName) throws DatabaseException,
												NoApplicationFoundException
   {
	 Log.log(Log.INFO,"ApplicationProcessor","getAppRefNos","Entered");

	ArrayList cgpanAppRefNoList=appDAO.getAppRefNos(mliId,borrowerId,borrowerName);

	Log.log(Log.INFO,"ApplicationProcessor","getAppRefNos","AppRefNosList:" + cgpanAppRefNoList);

	   return cgpanAppRefNoList;
   }

 // Added by Ritesh on 23Nov2006 to filter records based on mli
  /**
	* This method returns an ArrayList of applications pending for approval based on the mli.
	* @return ArrayList
	* @throws NoApplicationFoundException
	* @roseuid 39B0FB0C01BC
	*/
   public ArrayList getApplicationsForApprovalPath(String userId, String bankName) throws DatabaseException,NoApplicationFoundException
	 {
    ArrayList applicationsList=new ArrayList();
    //System.out.println("getApplicationsForApprovalPath Entered555555555555555555555");
   Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApprovalPath","Entered. Memory : "+ Runtime.getRuntime().freeMemory());

    HashMap duplicateApplications=new HashMap();
    HashMap ineligibleApplications=new HashMap();

    Application application=new Application();
    EligibleApplication eligibleApplication=new EligibleApplication();

		ArrayList eligibleAppsList=new ArrayList();

		ArrayList eligibleNonDupApps =new ArrayList();
    //added by sukumar@path for eligible non duplicate RSF applications
    ArrayList eligibleNonDupRsfApps =new ArrayList();
    
		ArrayList eligibleDupApps =new ArrayList();
		ArrayList ineligibleNonDupApps =new ArrayList();
		ArrayList ineligibleApps =new ArrayList();
		ArrayList dupApps=new ArrayList();
		ArrayList ineligibleDupApps=new ArrayList();
		String mliId=null;
		String cgpan="";
		/*
		 * Performs duplicate Check for all the applications across MLIs
		 */
		String mliFlag=Constants.ACROSS_MLIS;

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList duplicateApp = new ArrayList();
		
    ArrayList duplicateApps=checkDuplicatePath(mliFlag,bankName);
    //System.out.println("getApplicationsForApprovalPath Entered666666666666666666");
     //System.out.println("duplicateApps====2961"+duplicateApps);
		ArrayList tcDuplicateApp =(ArrayList)duplicateApps.get(0); 
		ArrayList wcDuplicateApp =(ArrayList)duplicateApps.get(1);		
		for(int i=0;i<tcDuplicateApp.size();i++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)tcDuplicateApp.get(i);
			duplicateApp.add(duplicateApplication);
		}
		for(int j=0;j<wcDuplicateApp.size();j++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)wcDuplicateApp.get(j);
			duplicateApp.add(duplicateApplication);
		}		
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());
		ArrayList dupAppRefNoList=new ArrayList();
		int duplicateAppSize=duplicateApp.size();
 //   //System.out.println("duplicateAppSize"+duplicateAppSize);
		/*
		 * This loop retrieves all the duplicate app reference nos.
		 */
                 
                 
                 //System.out.println("***********************************1**********************************");
		DuplicateApplication dupApplication = null;
		for (int a=0;a<duplicateAppSize;a++)
		{
			dupApplication=(DuplicateApplication)duplicateApp.get(a);
			String dupRefNo=dupApplication.getNewAppRefNo();
  //  //System.out.println("in ApplicationProcessor dupRefNo = "+dupRefNo);
			duplicateApplications.put(dupRefNo,dupApplication);
			dupAppRefNoList.add(dupRefNo);
			dupRefNo = null;
		}
		dupAppRefNoList.trimToSize();
		dupApplication = null;
    Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","duplicate app ref nos size :" + dupAppRefNoList.size());
		/*
		 * Performs eligbility Check for all the pending applications
		 */
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

    ArrayList pendingAppList=appDAO.viewApplicationsForApprovalPath(userId,bankName);
    //System.out.println("getApplicationsForApprovalPath Entered 77777777777777777777777");
    //System.out.println("viewApplicationsForApprovalPath called from here");

 //   //System.out.println("PATH after calling pendingAppList=appDAO.viewApplicationsForApprovalPath(userId) **** userId = "+userId);
    Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Pending size:" + pendingAppList.size());
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList tcPendingAppList=(ArrayList)pendingAppList.get(0);
		ArrayList wcPendingAppList=(ArrayList)pendingAppList.get(1);

		int tcPendingAppListSize=tcPendingAppList.size();
 //   //System.out.println("tcPendingAppListSize:"+tcPendingAppListSize);
    Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","tc Pending size:" + tcPendingAppListSize);
		int wcPendingAppListSize=wcPendingAppList.size();
  //  //System.out.println("wcPendingAppListSize:"+wcPendingAppListSize);
  Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","wc Pending size:" + wcPendingAppListSize);
		//retrievin the count of applications beyond user's limit
		Integer intCount=(Integer)pendingAppList.get(2);
   		
		pendingAppList.clear();
		pendingAppList = null;
       
    //   //System.out.println("***********************************2**********************************");
		ArrayList appRefNoList=new ArrayList();
		Application tcApplication = null;
		String tcAppRefNo = null;
		for (int x=0;x<tcPendingAppListSize;x++)
		{
			
			//Application tcApplication=new Application();
			tcApplication=(Application)tcPendingAppList.get(x);
			tcAppRefNo=tcApplication.getAppRefNo();	
			//System.out.println("IN getApplicationsForApprovalPath tcAppRefNo"+tcAppRefNo);
			appRefNoList.add(tcAppRefNo);

		}
		tcPendingAppList.clear();
		tcPendingAppList = null;
		tcApplication = null;
		tcAppRefNo = null;
		Application wcApplication = null;
		String wcAppRefNo = null;
		for (int y=0;y<wcPendingAppListSize;y++)
		{
			wcApplication=new Application();
			wcApplication=(Application)wcPendingAppList.get(y);
			wcAppRefNo=wcApplication.getAppRefNo();			
			appRefNoList.add(wcAppRefNo);

		}
		appRefNoList.trimToSize();
		wcPendingAppList.clear();
		wcPendingAppList = null;
		wcApplication = null;
		wcAppRefNo = null;
		int appRefNoListSize=appRefNoList.size();
 // //System.out.println("getApplicationsForApprovalPath appRefNoListSize = "+appRefNoListSize);
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","pending app ref nos size :" + appRefNoListSize);
		for (int i=0;i<appRefNoListSize; i++)
		{
		//	String mliId=null;
		//	String cgpan="";

			String appRefNo=(String)appRefNoList.get(i);
     // //System.out.println("Line number 2632-AppRefNo"+appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin eligiblity check :" + appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			eligibleApplication=getAppsForEligibilityCheck(appRefNo);

			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			//java.util.Date submittedDate = null;
  //    //System.out.println("ritesh is here with condition "+eligibleApplication.getFailedCondition());
			if (!(eligibleApplication.getFailedCondition().equals("")))
				{					
					eligibleApplication.setAppRefNo(appRefNo);
					eligibleAppsList.add(eligibleApplication);
				//	submittedDate = null;
					application = null;
          ////System.out.println("ritesh is coming here also");
				}
        appRefNo = null;
        eligibleApplication = null;
		//	cgpan = null;

		}
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","eligible apps size :" + eligibleAppsList.size());
		ArrayList eligibleAppRefNoList=new ArrayList();

		int eligibleAppsListSize=eligibleAppsList.size();
		/*
		 * This loop retreives all the eligible application reference nos.
		 */
		EligibleApplication eligibleApp = null;
		for (int b=0;b<eligibleAppsListSize;b++)
		{
			//EligibleApplication eligibleApp=new EligibleApplication();
			eligibleApp=(EligibleApplication)eligibleAppsList.get(b);
			String eligibleRefNo=eligibleApp.getAppRefNo();
			ineligibleApplications.put(eligibleRefNo,eligibleApp);
			eligibleAppRefNoList.add(eligibleRefNo);
			eligibleApp = null;
			eligibleRefNo = null;
		}
		eligibleAppRefNoList.trimToSize();

		/*
		 * Checks if the app ref no. of each of the pending application belongs
		 * to the eligible applications list or the duplicate applications list
		 */

		 //int dupAppRefNoListSize=dupAppRefNoList.size();
		// int eligibleAppRefNoListSize=eligibleAppRefNoList.size();

		BorrowerDetails borrowerDetails=new BorrowerDetails();
		SSIDetails ssiDetails=new SSIDetails();
		borrowerDetails.setSsiDetails(ssiDetails);
		String strLoanType = null;
    String handicraftFlag = null;
    String handicraftReimb = null;
       String handloomFlag = null;
    
		TermLoan termLoan = null;
		WorkingCapital workingCapital = null;
		for (int i=0;i<appRefNoListSize; i++)
		{
			double creditAmount=0;
			String appRefNo=(String)appRefNoList.get(i);

						application=new Application();

						application.setBorrowerDetails(borrowerDetails);


						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());

						application=getPartApplicationPath(mliId,cgpan,appRefNo);
						//System.out.println("getPartApplicationPath called from here");
            /* added by sukumar@path for getting the zone Name */
            String memberId = application.getMliID();
            String bankId = memberId.substring(0,4);
            String zoneId = memberId.substring(4,8);
            String branchId = memberId.substring(8,12);
            Registration registration = new Registration();
       	    MLIInfo mliInfo=registration.getMemberDetails(bankId,zoneId,branchId);
          //  //System.out.println("Bank Name:"+mliInfo.getBankName());
        //    //System.out.println("Zone Name:"+mliInfo.getZoneName());
            application.setZoneName(mliInfo.getZoneName());
            /* end part here */
            //application=getPartApplication(mliId,cgpan,appRefNo);
						strLoanType = application.getLoanType();
            handicraftFlag = application.getHandiCrafts();
            handicraftReimb = application.getDcHandicrafts();
		    handloomFlag = application.getHandloomchk();
						termLoan = appDAO.getTermLoan(appRefNo,strLoanType);
						application.setTermLoan(termLoan);
						termLoan = null;
                                                
                                                
                                              //  //System.out.println("appRefNo**************wc******"+appRefNo);
		 //   //System.out.println("strLoanType**************strLoanType******"+strLoanType);
						workingCapital = appDAO.getWorkingCapital(appRefNo, strLoanType);
   						
						application.setWc(workingCapital);
						workingCapital = null;
		//    //System.out.println("***************33333333333*******************************");
						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());


						Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Before checkin:" + appRefNo);

			double dblApprovedAmt = 0;
			//Eligible Non-Duplicate Applications
			if (!(dupAppRefNoList.contains(appRefNo)) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
      //  //System.out.println("Line number 2723 Application Processor Sukumar");
      //  //System.out.println(">>>>>>>>>>Coming here in Eligible Non-Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Non Duplicate Ref No :" +appRefNo);
				Application clearApplication=new Application();
				BorrowerDetails clearBorrowerDetails=new BorrowerDetails();
				//SSIDetails clearSsiDetails=new SSIDetails();
				clearBorrowerDetails.setSsiDetails(new SSIDetails());
				clearApplication.setBorrowerDetails(clearBorrowerDetails);
				clearBorrowerDetails=null;


				java.util.Date submittedDate=application.getSubmittedDate();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","submitted Date:" + submittedDate);
				int ssiRefNo=(application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","ssi ref no:" + ssiRefNo);
        
				clearApplication.setAppRefNo(appRefNo);     
     //  //System.out.println("AppRefNo:"+appRefNo);
				clearApplication.setSubmittedDate(submittedDate);
       //System.out.println("submittedDate"+submittedDate);
				clearApplication.setTermLoan(application.getTermLoan());
    //    //System.out.println("application.getTermLoan()"+application.getTermLoan());
				clearApplication.setWc(application.getWc());
     //   //System.out.println("application.getWc()"+application.getWc());
				clearApplication.setLoanType(strLoanType);
   //    //System.out.println("strLoanType"+strLoanType);
        clearApplication.setHandiCrafts(handicraftFlag);
        clearApplication.setDcHandicrafts(handicraftReimb);
        
			    clearApplication.setHandloomchk(handloomFlag);
        
				dblApprovedAmt = calApprovedAmount(application);
   // dblApprovedAmt =   application.getTermLoan().getCreditGuaranteed();
 //      //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				clearApplication.setApprovedAmount(dblApprovedAmt); 
				clearApplication.setStatus(application.getStatus());
  //      //System.out.println("application.getStatus()"+application.getStatus());
				clearApplication.setRemarks(application.getRemarks());
        clearApplication.setZoneName(application.getZoneName());
    //    //System.out.println("application.getRemarks()"+application.getRemarks());
    //    //System.out.println("Application.getSchemeId"+application.getScheme());
        ///////new code by ritesh on 30Nov2006
        //clearApplication.setMliID(application.getMliID());
        (clearApplication.getBorrowerDetails().getSsiDetails()).setConstitution(application.getBorrowerDetails().getSsiDetails().getConstitution());
        (clearApplication.getBorrowerDetails().getSsiDetails()).setSsiName(application.getBorrowerDetails().getSsiDetails().getSsiName());
        (clearApplication.getBorrowerDetails().getSsiDetails()).setActivityType(application.getBorrowerDetails().getSsiDetails().getActivityType());
        (clearApplication.getBorrowerDetails().getSsiDetails()).setSancDate_new(application.getBorrowerDetails().getSsiDetails().getSancDate_new());        
        (clearApplication.getBorrowerDetails().getSsiDetails()).setIndustryNature(application.getBorrowerDetails().getSsiDetails().getIndustryNature());        
     
        ///////End code code by ritesh
       // //System.out.println("Industry Nature:"+application.getBorrowerDetails().getSsiDetails().getIndustryNature());
				(clearApplication.getBorrowerDetails().getSsiDetails()).setBorrowerRefNo(ssiRefNo);
        if(!(application.getScheme()).equals("RSF"))
          {
				    eligibleNonDupApps.add(clearApplication);
          }else
             {
              eligibleNonDupRsfApps.add(clearApplication);
          //    //System.out.println("eligibleNonDupRsfApps"+appRefNo);
             } 
				submittedDate = null;

				clearApplication = null;
			}
			//Probable Eligible Duplicate Application
			else if (dupAppRefNoList.contains(appRefNo) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
     //   //System.out.println(">>>>>>>>>>Coming here in Eligible Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}
        String industrynature=application.getBorrowerDetails().getSsiDetails().getIndustryNature();
        //sukumar
        String activityType = application.getBorrowerDetails().getSsiDetails().getActivityType();
      //  //System.out.println("activityType:"+activityType);
      // String prevSSI = application.getPrevSSI();
     //  String existSSI = application.getExistSSI();
        ////System.out.println("industrynature:"+industrynature);
        
				DuplicateApplication eligibleDupApp=new DuplicateApplication();
    //    //System.out.println("Line number 2792 Eligible Duplicate Applications");
        
				eligibleDupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
     //   //System.out.println("appRefNo"+appRefNo);
				eligibleDupApp.setDupCreditAmount(creditAmount);
    //    //System.out.println("creditAmount"+creditAmount);
				dblApprovedAmt = calApprovedAmount(application);
        
				eligibleDupApp.setDupApprovedAmount(dblApprovedAmt);
     //   //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				eligibleDupApp.setStatus(application.getStatus());
        eligibleDupApp.setNature(industrynature);
        eligibleDupApp.setZoneName(application.getZoneName());
        eligibleDupApp.setLoanType(strLoanType);
        eligibleDupApp.setHandicraftFlag(handicraftFlag);
        eligibleDupApp.setDcHandicraftReimb(handicraftReimb);
       
     //  //System.out.println("Old Cgpan:"+eligibleDupApp.getOldCgpan());
    //   //System.out.println("New App Ref No:"+eligibleDupApp.getNewAppRefNo());
        String prevSSIRef = appDAO.getSSIRefNoNew(eligibleDupApp.getOldCgpan());
        //System.out.println("getApplicationsForApprovalPath Entered 88888888888888888888");
        String existSSIRef = appDAO.getSSIRefNoNew(eligibleDupApp.getNewAppRefNo());
        //System.out.println("getApplicationsForApprovalPath Entered 99999999999999999999");
        eligibleDupApp.setPrevSSi(prevSSIRef);
        eligibleDupApp.setExistSSi(existSSIRef);
     //   //System.out.println("application.getStatus()"+application.getStatus());
				eligibleDupApp.setDuplicateRemarks(application.getRemarks());
        eligibleDupApp.setActivityType(activityType);
    //    //System.out.println("Activity Type for App Ref No:"+appRefNo+" is: "+activityType);
     //   //System.out.println("application.getRemarks()"+application.getRemarks());
     //   //System.out.println("application.getScheme()"+application.getScheme());
     
				eligibleDupApps.add(eligibleDupApp);

				eligibleDupApp = null;

			}
			//Ineligible Non Duplicate applications
			else if (!(dupAppRefNoList.contains(appRefNo)) && eligibleAppRefNoList.contains(appRefNo))
			{
     //   //System.out.println(">>>>>>>>>>Coming here in Ineligible Non Duplicate applications Line number 2815");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Non Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}

				EligibleApplication inEligibleApplication=new EligibleApplication();
     //   //System.out.println("Line number 2833 In Eligible Applications");
				inEligibleApplication=(EligibleApplication)ineligibleApplications.get(appRefNo);
     //   //System.out.println(" In Eligible Application appRefNo"+appRefNo);
				inEligibleApplication.setEligibleCreditAmount(creditAmount);
     //   //System.out.println("creditAmount"+creditAmount);
				inEligibleApplication.setSubmissiondate(application.getSubmittedDate().toString());
				dblApprovedAmt = calApprovedAmount(application);
				inEligibleApplication.setEligibleApprovedAmount(dblApprovedAmt);
    //    //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				inEligibleApplication.setStatus(application.getStatus());
     //   //System.out.println("application.getStatus()"+application.getStatus());
				inEligibleApplication.setEligibleRemarks(application.getRemarks());
     //   //System.out.println("application.getRemarks()"+application.getRemarks());
     //   //System.out.println("application.getScheme()"+application.getScheme());
				ineligibleNonDupApps.add(inEligibleApplication);				
				inEligibleApplication = null;
			}
			//Ineligible Duplicate Applications
			else if (dupAppRefNoList.contains(appRefNo) && eligibleAppRefNoList.contains(appRefNo))
			{
     //   //System.out.println(">>>>>>>>>>Coming here in Ineligible Duplicate Applications Line number 2853");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount;					
				}
				DuplicateApplication dupApp=new DuplicateApplication();
				dupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				dupApp.setDupCreditAmount(creditAmount);
				dupApp.setStatus(application.getStatus());
				dupApp.setDuplicateRemarks(application.getRemarks());
     //   //System.out.println("In Eligible Duplicate Applications");
				EligibleApplication inEligibleApp=new EligibleApplication();
				inEligibleApp=(EligibleApplication)ineligibleApplications.get(appRefNo);
     //   //System.out.println("In Eligible Duplicate Aplication RefNumber"+appRefNo);
				dblApprovedAmt = calApprovedAmount(application);
				dupApp.setDupApprovedAmount(dblApprovedAmt);
    //    //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				inEligibleApp.setSubmissiondate(application.getSubmittedDate().toString());
				inEligibleApp.setStatus(application.getStatus());
    //    //System.out.println("application.getStatus()"+application.getStatus());
				inEligibleApp.setEligibleRemarks(application.getRemarks());
     //   //System.out.println("application.getRemarks()"+application.getRemarks());
				
				dupApps.add(dupApp);
				ineligibleApps.add(inEligibleApp);
				dupApp = null;
				inEligibleApp = null;
			}
			strLoanType = null;
		appRefNo = null;
		application  = null;
		}
		ssiDetails=null;
		borrowerDetails = null;
		eligibleAppRefNoList = null;
		ineligibleDupApps.add(dupApps);
		ineligibleDupApps.add(ineligibleApps);

		applicationsList.add(eligibleNonDupApps);
		applicationsList.add(eligibleDupApps);
		applicationsList.add(ineligibleNonDupApps);
		applicationsList.add(ineligibleDupApps);
		applicationsList.add(intCount);
    applicationsList.add(eligibleNonDupRsfApps);
		cgpan=null;
		intCount = null;
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Exited. Memory : "+ Runtime.getRuntime().freeMemory());
   //////////
/*
   duplicateApplications.clear();
			   ineligibleApplications.clear();

		   eligibleAppsList.clear();

		   eligibleNonDupApps.clear();
		   eligibleDupApps.clear();
		   ineligibleNonDupApps.clear();
		   ineligibleApps.clear();
		   dupApps.clear();
		   ineligibleDupApps.clear();
*/
			duplicateApplications=null;
			ineligibleApplications=null;

			application=null;
		eligibleApplication=null;

		eligibleAppsList=null;

		eligibleNonDupApps =null;
		eligibleDupApps =null;
		ineligibleNonDupApps =null;
		ineligibleApps =null;
		dupApps=null;
		ineligibleDupApps=null;

   ////////

	   return applicationsList;
   }

 //koteswar start
   public ArrayList getApplicationsForApprovalPathnew(String userId, String bankName) throws DatabaseException,NoApplicationFoundException
	 {
  ArrayList applicationsList=new ArrayList();
 Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApprovalPath","Entered. Memory : "+ Runtime.getRuntime().freeMemory());

  HashMap duplicateApplications=new HashMap();
  HashMap ineligibleApplications=new HashMap();

  Application application=new Application();
  EligibleApplication eligibleApplication=new EligibleApplication();

		ArrayList eligibleAppsList=new ArrayList();

		ArrayList eligibleNonDupApps =new ArrayList();
  //added by sukumar@path for eligible non duplicate RSF applications
  ArrayList eligibleNonDupRsfApps =new ArrayList();
  
		ArrayList eligibleDupApps =new ArrayList();
		ArrayList ineligibleNonDupApps =new ArrayList();
		ArrayList ineligibleApps =new ArrayList();
		ArrayList dupApps=new ArrayList();
		ArrayList ineligibleDupApps=new ArrayList();
		String mliId=null;
		String cgpan="";
		/*
		 * Performs duplicate Check for all the applications across MLIs
		 */
		String mliFlag=Constants.ACROSS_MLIS;

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList duplicateApp = new ArrayList();
		
  ArrayList duplicateApps=checkDuplicatePathnew(mliFlag,bankName);
  
		ArrayList tcDuplicateApp =(ArrayList)duplicateApps.get(0); 
		ArrayList wcDuplicateApp =(ArrayList)duplicateApps.get(1);		
		for(int i=0;i<tcDuplicateApp.size();i++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)tcDuplicateApp.get(i);
			duplicateApp.add(duplicateApplication);
		}
		for(int j=0;j<wcDuplicateApp.size();j++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)wcDuplicateApp.get(j);
			duplicateApp.add(duplicateApplication);
		}		
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());
		ArrayList dupAppRefNoList=new ArrayList();
		int duplicateAppSize=duplicateApp.size();
//   //System.out.println("duplicateAppSize"+duplicateAppSize);
		/*
		 * This loop retrieves all the duplicate app reference nos.
		 */
               
               
            //   //System.out.println("***********************************1**********************************");
		DuplicateApplication dupApplication = null;
		for (int a=0;a<duplicateAppSize;a++)
		{
			dupApplication=(DuplicateApplication)duplicateApp.get(a);
			String dupRefNo=dupApplication.getNewAppRefNo();
//  //System.out.println("in ApplicationProcessor dupRefNo = "+dupRefNo);
			duplicateApplications.put(dupRefNo,dupApplication);
			dupAppRefNoList.add(dupRefNo);
			dupRefNo = null;
		}
		dupAppRefNoList.trimToSize();
		dupApplication = null;
  Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","duplicate app ref nos size :" + dupAppRefNoList.size());
		/*
		 * Performs eligbility Check for all the pending applications
		 */
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

  ArrayList pendingAppList=appDAO.viewApplicationsForApprovalPath(userId,bankName);

//   //System.out.println("PATH after calling pendingAppList=appDAO.viewApplicationsForApprovalPath(userId) **** userId = "+userId);
  Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Pending size:" + pendingAppList.size());
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList tcPendingAppList=(ArrayList)pendingAppList.get(0);
		ArrayList wcPendingAppList=(ArrayList)pendingAppList.get(1);

		int tcPendingAppListSize=tcPendingAppList.size();
//   //System.out.println("tcPendingAppListSize:"+tcPendingAppListSize);
  Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","tc Pending size:" + tcPendingAppListSize);
		int wcPendingAppListSize=wcPendingAppList.size();
//  //System.out.println("wcPendingAppListSize:"+wcPendingAppListSize);
Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","wc Pending size:" + wcPendingAppListSize);
		//retrievin the count of applications beyond user's limit
		Integer intCount=(Integer)pendingAppList.get(2);
 		
		pendingAppList.clear();
		pendingAppList = null;
     
    // //System.out.println("***********************************2**********************************");
		ArrayList appRefNoList=new ArrayList();
		Application tcApplication = null;
		String tcAppRefNo = null;
		for (int x=0;x<tcPendingAppListSize;x++)
		{
			
			//Application tcApplication=new Application();
			tcApplication=(Application)tcPendingAppList.get(x);
			tcAppRefNo=tcApplication.getAppRefNo();			
			appRefNoList.add(tcAppRefNo);

		}
		tcPendingAppList.clear();
		tcPendingAppList = null;
		tcApplication = null;
		tcAppRefNo = null;
		Application wcApplication = null;
		String wcAppRefNo = null;
		for (int y=0;y<wcPendingAppListSize;y++)
		{
			wcApplication=new Application();
			wcApplication=(Application)wcPendingAppList.get(y);
			wcAppRefNo=wcApplication.getAppRefNo();			
			appRefNoList.add(wcAppRefNo);

		}
		appRefNoList.trimToSize();
		wcPendingAppList.clear();
		wcPendingAppList = null;
		wcApplication = null;
		wcAppRefNo = null;
		int appRefNoListSize=appRefNoList.size();
// //System.out.println("getApplicationsForApprovalPath appRefNoListSize = "+appRefNoListSize);
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","pending app ref nos size :" + appRefNoListSize);
		for (int i=0;i<appRefNoListSize; i++)
		{
		//	String mliId=null;
		//	String cgpan="";

			String appRefNo=(String)appRefNoList.get(i);
   // //System.out.println("Line number 2632-AppRefNo"+appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin eligiblity check :" + appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			eligibleApplication=getAppsForEligibilityCheck(appRefNo);

			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			//java.util.Date submittedDate = null;
//    //System.out.println("ritesh is here with condition "+eligibleApplication.getFailedCondition());
			if (!(eligibleApplication.getFailedCondition().equals("")))
				{					
					eligibleApplication.setAppRefNo(appRefNo);
					eligibleAppsList.add(eligibleApplication);
				//	submittedDate = null;
					application = null;
        ////System.out.println("ritesh is coming here also");
				}
      appRefNo = null;
      eligibleApplication = null;
		//	cgpan = null;

		}
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","eligible apps size :" + eligibleAppsList.size());
		ArrayList eligibleAppRefNoList=new ArrayList();

		int eligibleAppsListSize=eligibleAppsList.size();
		/*
		 * This loop retreives all the eligible application reference nos.
		 */
		EligibleApplication eligibleApp = null;
		for (int b=0;b<eligibleAppsListSize;b++)
		{
			//EligibleApplication eligibleApp=new EligibleApplication();
			eligibleApp=(EligibleApplication)eligibleAppsList.get(b);
			String eligibleRefNo=eligibleApp.getAppRefNo();
			ineligibleApplications.put(eligibleRefNo,eligibleApp);
			eligibleAppRefNoList.add(eligibleRefNo);
			eligibleApp = null;
			eligibleRefNo = null;
		}
		eligibleAppRefNoList.trimToSize();

		/*
		 * Checks if the app ref no. of each of the pending application belongs
		 * to the eligible applications list or the duplicate applications list
		 */

		 //int dupAppRefNoListSize=dupAppRefNoList.size();
		// int eligibleAppRefNoListSize=eligibleAppRefNoList.size();

		BorrowerDetails borrowerDetails=new BorrowerDetails();
		SSIDetails ssiDetails=new SSIDetails();
		borrowerDetails.setSsiDetails(ssiDetails);
		String strLoanType = null;
  String handicraftFlag = null;
  String handicraftReimb = null;
     String handloomFlag = null;
  
		TermLoan termLoan = null;
		WorkingCapital workingCapital = null;
		for (int i=0;i<appRefNoListSize; i++)
		{
			double creditAmount=0;
			String appRefNo=(String)appRefNoList.get(i);

						application=new Application();

						application.setBorrowerDetails(borrowerDetails);


						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());

						application=getPartApplicationPath(mliId,cgpan,appRefNo);
          /* added by sukumar@path for getting the zone Name */
          String memberId = application.getMliID();
          String bankId = memberId.substring(0,4);
          String zoneId = memberId.substring(4,8);
          String branchId = memberId.substring(8,12);
          Registration registration = new Registration();
     	    MLIInfo mliInfo=registration.getMemberDetails(bankId,zoneId,branchId);
        //  //System.out.println("Bank Name:"+mliInfo.getBankName());
      //    //System.out.println("Zone Name:"+mliInfo.getZoneName());
          application.setZoneName(mliInfo.getZoneName());
          /* end part here */
          //application=getPartApplication(mliId,cgpan,appRefNo);
						strLoanType = application.getLoanType();
          handicraftFlag = application.getHandiCrafts();
          handicraftReimb = application.getDcHandicrafts();
		    handloomFlag = application.getHandloomchk();
						termLoan = appDAO.getTermLoan(appRefNo,strLoanType);
						application.setTermLoan(termLoan);
						termLoan = null;
                                              
                                              
                                            //  //System.out.println("appRefNo**************wc******"+appRefNo);
		  //  //System.out.println("strLoanType**************strLoanType******"+strLoanType);
						workingCapital = appDAO.getWorkingCapital(appRefNo, strLoanType);
 						
						application.setWc(workingCapital);
						workingCapital = null;
		   // //System.out.println("***************33333333333*******************************");
						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());


						Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Before checkin:" + appRefNo);

			double dblApprovedAmt = 0;
			//Eligible Non-Duplicate Applications
			if (!(dupAppRefNoList.contains(appRefNo)) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
    //  //System.out.println("Line number 2723 Application Processor Sukumar");
    //  //System.out.println(">>>>>>>>>>Coming here in Eligible Non-Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Non Duplicate Ref No :" +appRefNo);
				Application clearApplication=new Application();
				BorrowerDetails clearBorrowerDetails=new BorrowerDetails();
				//SSIDetails clearSsiDetails=new SSIDetails();
				clearBorrowerDetails.setSsiDetails(new SSIDetails());
				clearApplication.setBorrowerDetails(clearBorrowerDetails);
				clearBorrowerDetails=null;


				java.util.Date submittedDate=application.getSubmittedDate();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","submitted Date:" + submittedDate);
				int ssiRefNo=(application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","ssi ref no:" + ssiRefNo);
      
				clearApplication.setAppRefNo(appRefNo);     
   //  //System.out.println("AppRefNo:"+appRefNo);
				clearApplication.setSubmittedDate(submittedDate);
//     //System.out.println("submittedDate"+submittedDate);
				clearApplication.setTermLoan(application.getTermLoan());
  //    //System.out.println("application.getTermLoan()"+application.getTermLoan());
				clearApplication.setWc(application.getWc());
   //   //System.out.println("application.getWc()"+application.getWc());
				clearApplication.setLoanType(strLoanType);
 //    //System.out.println("strLoanType"+strLoanType);
      clearApplication.setHandiCrafts(handicraftFlag);
      clearApplication.setDcHandicrafts(handicraftReimb);
      
			    clearApplication.setHandloomchk(handloomFlag);
      
				dblApprovedAmt = calApprovedAmount(application);
 // dblApprovedAmt =   application.getTermLoan().getCreditGuaranteed();
//      //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				clearApplication.setApprovedAmount(dblApprovedAmt); 
				clearApplication.setStatus(application.getStatus());
//      //System.out.println("application.getStatus()"+application.getStatus());
				clearApplication.setRemarks(application.getRemarks());
      clearApplication.setZoneName(application.getZoneName());
  //    //System.out.println("application.getRemarks()"+application.getRemarks());
  //    //System.out.println("Application.getSchemeId"+application.getScheme());
      ///////new code by ritesh on 30Nov2006
      //clearApplication.setMliID(application.getMliID());
      (clearApplication.getBorrowerDetails().getSsiDetails()).setConstitution(application.getBorrowerDetails().getSsiDetails().getConstitution());
      (clearApplication.getBorrowerDetails().getSsiDetails()).setSsiName(application.getBorrowerDetails().getSsiDetails().getSsiName());
      (clearApplication.getBorrowerDetails().getSsiDetails()).setActivityType(application.getBorrowerDetails().getSsiDetails().getActivityType());
      (clearApplication.getBorrowerDetails().getSsiDetails()).setSancDate_new(application.getBorrowerDetails().getSsiDetails().getSancDate_new());        
      (clearApplication.getBorrowerDetails().getSsiDetails()).setIndustryNature(application.getBorrowerDetails().getSsiDetails().getIndustryNature());        
   
      ///////End code code by ritesh
     // //System.out.println("Industry Nature:"+application.getBorrowerDetails().getSsiDetails().getIndustryNature());
				(clearApplication.getBorrowerDetails().getSsiDetails()).setBorrowerRefNo(ssiRefNo);
      if(!(application.getScheme()).equals("RSF"))
        {
				    eligibleNonDupApps.add(clearApplication);
        }else
           {
            eligibleNonDupRsfApps.add(clearApplication);
        //    //System.out.println("eligibleNonDupRsfApps"+appRefNo);
           } 
				submittedDate = null;

				clearApplication = null;
			}
			//Probable Eligible Duplicate Application
			else if (dupAppRefNoList.contains(appRefNo) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
   //   //System.out.println(">>>>>>>>>>Coming here in Eligible Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}
      String industrynature=application.getBorrowerDetails().getSsiDetails().getIndustryNature();
      //sukumar
      String activityType = application.getBorrowerDetails().getSsiDetails().getActivityType();
    //  //System.out.println("activityType:"+activityType);
    // String prevSSI = application.getPrevSSI();
   //  String existSSI = application.getExistSSI();
      ////System.out.println("industrynature:"+industrynature);
      
				DuplicateApplication eligibleDupApp=new DuplicateApplication();
  //    //System.out.println("Line number 2792 Eligible Duplicate Applications");
      
				eligibleDupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
   //   //System.out.println("appRefNo"+appRefNo);
				eligibleDupApp.setDupCreditAmount(creditAmount);
  //    //System.out.println("creditAmount"+creditAmount);
				dblApprovedAmt = calApprovedAmount(application);
      
				eligibleDupApp.setDupApprovedAmount(dblApprovedAmt);
   //   //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				eligibleDupApp.setStatus(application.getStatus());
      eligibleDupApp.setNature(industrynature);
      eligibleDupApp.setZoneName(application.getZoneName());
      eligibleDupApp.setLoanType(strLoanType);
      eligibleDupApp.setHandicraftFlag(handicraftFlag);
      eligibleDupApp.setDcHandicraftReimb(handicraftReimb);
     
   //  //System.out.println("Old Cgpan:"+eligibleDupApp.getOldCgpan());
  //   //System.out.println("New App Ref No:"+eligibleDupApp.getNewAppRefNo());
      String prevSSIRef = appDAO.getSSIRefNoNew(eligibleDupApp.getOldCgpan());
      String existSSIRef = appDAO.getSSIRefNoNew(eligibleDupApp.getNewAppRefNo());
  
      eligibleDupApp.setPrevSSi(prevSSIRef);
      eligibleDupApp.setExistSSi(existSSIRef);
   //   //System.out.println("application.getStatus()"+application.getStatus());
				eligibleDupApp.setDuplicateRemarks(application.getRemarks());
      eligibleDupApp.setActivityType(activityType);
  //    //System.out.println("Activity Type for App Ref No:"+appRefNo+" is: "+activityType);
   //   //System.out.println("application.getRemarks()"+application.getRemarks());
   //   //System.out.println("application.getScheme()"+application.getScheme());
   
				eligibleDupApps.add(eligibleDupApp);

				eligibleDupApp = null;

			}
			//Ineligible Non Duplicate applications
			else if (!(dupAppRefNoList.contains(appRefNo)) && eligibleAppRefNoList.contains(appRefNo))
			{
   //   //System.out.println(">>>>>>>>>>Coming here in Ineligible Non Duplicate applications Line number 2815");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Non Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}

				EligibleApplication inEligibleApplication=new EligibleApplication();
   //   //System.out.println("Line number 2833 In Eligible Applications");
				inEligibleApplication=(EligibleApplication)ineligibleApplications.get(appRefNo);
   //   //System.out.println(" In Eligible Application appRefNo"+appRefNo);
				inEligibleApplication.setEligibleCreditAmount(creditAmount);
   //   //System.out.println("creditAmount"+creditAmount);
				inEligibleApplication.setSubmissiondate(application.getSubmittedDate().toString());
				dblApprovedAmt = calApprovedAmount(application);
				inEligibleApplication.setEligibleApprovedAmount(dblApprovedAmt);
  //    //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				inEligibleApplication.setStatus(application.getStatus());
   //   //System.out.println("application.getStatus()"+application.getStatus());
				inEligibleApplication.setEligibleRemarks(application.getRemarks());
   //   //System.out.println("application.getRemarks()"+application.getRemarks());
   //   //System.out.println("application.getScheme()"+application.getScheme());
				ineligibleNonDupApps.add(inEligibleApplication);				
				inEligibleApplication = null;
			}
			//Ineligible Duplicate Applications
			else if (dupAppRefNoList.contains(appRefNo) && eligibleAppRefNoList.contains(appRefNo))
			{
   //   //System.out.println(">>>>>>>>>>Coming here in Ineligible Duplicate Applications Line number 2853");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount;					
				}
				DuplicateApplication dupApp=new DuplicateApplication();
				dupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				dupApp.setDupCreditAmount(creditAmount);
				dupApp.setStatus(application.getStatus());
				dupApp.setDuplicateRemarks(application.getRemarks());
   //   //System.out.println("In Eligible Duplicate Applications");
				EligibleApplication inEligibleApp=new EligibleApplication();
				inEligibleApp=(EligibleApplication)ineligibleApplications.get(appRefNo);
   //   //System.out.println("In Eligible Duplicate Aplication RefNumber"+appRefNo);
				dblApprovedAmt = calApprovedAmount(application);
				dupApp.setDupApprovedAmount(dblApprovedAmt);
  //    //System.out.println("dblApprovedAmt"+dblApprovedAmt);
				inEligibleApp.setSubmissiondate(application.getSubmittedDate().toString());
				inEligibleApp.setStatus(application.getStatus());
  //    //System.out.println("application.getStatus()"+application.getStatus());
				inEligibleApp.setEligibleRemarks(application.getRemarks());
   //   //System.out.println("application.getRemarks()"+application.getRemarks());
				
				dupApps.add(dupApp);
				ineligibleApps.add(inEligibleApp);
				dupApp = null;
				inEligibleApp = null;
			}
			strLoanType = null;
		appRefNo = null;
		application  = null;
		}
		ssiDetails=null;
		borrowerDetails = null;
		eligibleAppRefNoList = null;
		ineligibleDupApps.add(dupApps);
		ineligibleDupApps.add(ineligibleApps);

		applicationsList.add(eligibleNonDupApps);
		applicationsList.add(eligibleDupApps);
		applicationsList.add(ineligibleNonDupApps);
		applicationsList.add(ineligibleDupApps);
		applicationsList.add(intCount);
  applicationsList.add(eligibleNonDupRsfApps);
		cgpan=null;
		intCount = null;
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Exited. Memory : "+ Runtime.getRuntime().freeMemory());
 //////////
/*
 duplicateApplications.clear();
			   ineligibleApplications.clear();

		   eligibleAppsList.clear();

		   eligibleNonDupApps.clear();
		   eligibleDupApps.clear();
		   ineligibleNonDupApps.clear();
		   ineligibleApps.clear();
		   dupApps.clear();
		   ineligibleDupApps.clear();
*/
			duplicateApplications=null;
			ineligibleApplications=null;

			application=null;
		eligibleApplication=null;

		eligibleAppsList=null;

		eligibleNonDupApps =null;
		eligibleDupApps =null;
		ineligibleNonDupApps =null;
		ineligibleApps =null;
		dupApps=null;
		ineligibleDupApps=null;

 ////////

	   return applicationsList;
 }

   
   //koteswar end
   
   
   
   
   
   /**
	* This method returns an ArrayList of applications pending for approval.
	* @return ArrayList
	* @throws NoApplicationFoundException
	* @roseuid 39B0FB0C01BC
	*/
   
   //bhu 1st
   public ArrayList getApplicationsForApproval(String userId) throws DatabaseException,NoApplicationFoundException
	 {
                ArrayList applicationsList=new ArrayList();
              //  //System.out.println("PATH getApplicationsForApproval userId ***** = "+userId);
              //  Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Entered. Memory : "+ Runtime.getRuntime().freeMemory());
            
                HashMap duplicateApplications=new HashMap();
                HashMap ineligibleApplications=new HashMap();
            
                Application application=new Application();
                EligibleApplication eligibleApplication=new EligibleApplication();

		ArrayList eligibleAppsList=new ArrayList();

		ArrayList eligibleNonDupApps =new ArrayList();
		ArrayList eligibleDupApps =new ArrayList();
		ArrayList ineligibleNonDupApps =new ArrayList();
		ArrayList ineligibleApps =new ArrayList();
		ArrayList dupApps=new ArrayList();
		ArrayList ineligibleDupApps=new ArrayList();

		String mliId=null;
		String cgpan="";

		/*
		 * Performs duplicate Check for all the applications across MLIs
		 */
		String mliFlag=Constants.ACROSS_MLIS;

		//Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList duplicateApp = new ArrayList();
 //   //System.out.println("PATH going to check duplicate apps before mliFlag ***** =  "+mliFlag);
	/* commed by sukumar for test the application on 09-08-2008 */
  	ArrayList duplicateApps=checkDuplicate(mliFlag);
  //  //System.out.println("PATH returning duplicateApps after checkDuplicate(mliFlag) ");
    
		ArrayList tcDuplicateApp =(ArrayList)duplicateApps.get(0); 
		ArrayList wcDuplicateApp =(ArrayList)duplicateApps.get(1);		
  // //System.out.println("ORG   returning tcDuplicateApp *****= "+tcDuplicateApp.size());		
 //  //System.out.println("ORG   returning wcDuplicateApp *****= "+wcDuplicateApp.size());		
		for(int i=0;i<tcDuplicateApp.size();i++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)tcDuplicateApp.get(i);
			duplicateApp.add(duplicateApplication);
		}
		for(int j=0;j<wcDuplicateApp.size();j++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)wcDuplicateApp.get(j);
			duplicateApp.add(duplicateApplication);
		}		

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());
   /* --comment part end here on 09-08-2008   */

		ArrayList dupAppRefNoList=new ArrayList();
		int duplicateAppSize=duplicateApp.size();
////System.out.println("PATH duplicateApp.size after ading WC and TC application is ***** = "+duplicateApp.size());		    

		/*
		 * This loop retrieves all the duplicate app reference nos.
		 */
		DuplicateApplication dupApplication = null;
		for (int a=0;a<duplicateAppSize;a++)
		{

			dupApplication=(DuplicateApplication)duplicateApp.get(a);
			String dupRefNo=dupApplication.getNewAppRefNo();
			duplicateApplications.put(dupRefNo,dupApplication);
			dupAppRefNoList.add(dupRefNo);
     //System.out.println("PATH dupRefNo = ***** "+dupRefNo);      
			dupRefNo = null;
		}
		dupAppRefNoList.trimToSize();
		
		dupApplication = null;
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","duplicate app ref nos size :" + dupAppRefNoList.size());
 // //System.out.println("PATH duplicate app ref nos size :***** = " + dupAppRefNoList.size());

		/*
		 * Performs eligbility Check for all the pending applications
		 */

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());
////System.out.println("PATH before calling pendingAppList=appDAO.viewApplicationsForApproval(userId) **** userId = "+userId);
		ArrayList pendingAppList=appDAO.viewApplicationsForApproval(userId);
 //   //System.out.println("pendingAppList.size()"+pendingAppList.size());
// //System.out.println("PATH after calling pendingAppList=appDAO.viewApplicationsForApproval(userId) **** userId = "+userId);
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Pending size:" + pendingAppList.size());

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList tcPendingAppList=(ArrayList)pendingAppList.get(0);
		ArrayList wcPendingAppList=(ArrayList)pendingAppList.get(1);
		int tcPendingAppListSize=tcPendingAppList.size();
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","tc Pending size:" + tcPendingAppListSize);
		int wcPendingAppListSize=wcPendingAppList.size();
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","wc Pending size:" + wcPendingAppListSize);

		//retrievin the count of applications beyond user's limit
		Integer intCount=(Integer)pendingAppList.get(2);
   		
		pendingAppList.clear();
		pendingAppList = null;

		ArrayList appRefNoList=new ArrayList();
		Application tcApplication = null;
		String tcAppRefNo = null;
		for (int x=0;x<tcPendingAppListSize;x++)
		{
			
			//Application tcApplication=new Application();
			tcApplication=(Application)tcPendingAppList.get(x);
			tcAppRefNo=tcApplication.getAppRefNo();			
			appRefNoList.add(tcAppRefNo);

		}
		tcPendingAppList.clear();
		tcPendingAppList = null;
		tcApplication = null;
		tcAppRefNo = null;
		Application wcApplication = null;
		String wcAppRefNo = null;
		for (int y=0;y<wcPendingAppListSize;y++)
		{
			wcApplication=new Application();
			wcApplication=(Application)wcPendingAppList.get(y);
			wcAppRefNo=wcApplication.getAppRefNo();			
			appRefNoList.add(wcAppRefNo);

		}
		appRefNoList.trimToSize();
		wcPendingAppList.clear();
		wcPendingAppList = null;
		wcApplication = null;
		wcAppRefNo = null;
		int appRefNoListSize=appRefNoList.size();
  //  //System.out.println("getApplicationsForApproval appRefNoListSize = "+appRefNoListSize);
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","pending app ref nos size :" + appRefNoListSize);
		for (int i=0;i<appRefNoListSize; i++)
		{
		//	String mliId=null;
		//	String cgpan="";

			String appRefNo=(String)appRefNoList.get(i);
     // //System.out.println("appRefNo-test:"+appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin eligiblity check :" + appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			eligibleApplication=getAppsForEligibilityCheck(appRefNo);

			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			//java.util.Date submittedDate = null;
			if (!(eligibleApplication.getFailedCondition().equals("")))
				{					
					/*application=new Application();
					Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin getApplication() :" + Runtime.getRuntime().freeMemory() + " in FOR loop i = "+i);
					application=getPartApplication(mliId,cgpan,appRefNo);
					Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin getApplication() :" + Runtime.getRuntime().freeMemory());
					java.util.Date submittedDate=application.getSubmittedDate();
					//String stringDate=submittedDate.toString();
					eligibleApplication.setSubmissiondate(submittedDate.toString());	 
					 */

					eligibleApplication.setAppRefNo(appRefNo);
   					

					eligibleAppsList.add(eligibleApplication);
				//	submittedDate = null;
					application = null;

				}
			appRefNo = null;
			eligibleApplication = null;
		//	cgpan = null;

		}
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","eligible apps size :" + eligibleAppsList.size());
		ArrayList eligibleAppRefNoList=new ArrayList();

		int eligibleAppsListSize=eligibleAppsList.size();
		/*
		 * This loop retreives all the eligible application reference nos.
		 */
		EligibleApplication eligibleApp = null;
		for (int b=0;b<eligibleAppsListSize;b++)
		{
			//EligibleApplication eligibleApp=new EligibleApplication();
			eligibleApp=(EligibleApplication)eligibleAppsList.get(b);
			String eligibleRefNo=eligibleApp.getAppRefNo();
			ineligibleApplications.put(eligibleRefNo,eligibleApp);
			eligibleAppRefNoList.add(eligibleRefNo);
			eligibleApp = null;
			eligibleRefNo = null;
		}
		eligibleAppRefNoList.trimToSize();

		/*
		 * Checks if the app ref no. of each of the pending application belongs
		 * to the eligible applications list or the duplicate applications list
		 */

		 //int dupAppRefNoListSize=dupAppRefNoList.size();
		// int eligibleAppRefNoListSize=eligibleAppRefNoList.size();

		BorrowerDetails borrowerDetails=new BorrowerDetails();
		SSIDetails ssiDetails=new SSIDetails();
		borrowerDetails.setSsiDetails(ssiDetails);
		String strLoanType = null;
		TermLoan termLoan = null;
    String memId = null;
		WorkingCapital workingCapital = null;
		for (int i=0;i<appRefNoListSize; i++)
		{
			double creditAmount=0;
			String appRefNo=(String)appRefNoList.get(i);

						application=new Application();
          Application     applicationTemp = new Application();

						application.setBorrowerDetails(borrowerDetails);


						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());

						application=getPartApplication(mliId,cgpan,appRefNo);
           	strLoanType = application.getLoanType();
            memId = appDAO.getMemberIdforAppRef(appRefNo);
						termLoan = appDAO.getTermLoan(appRefNo,strLoanType);
						application.setTermLoan(termLoan);
						termLoan = null;
						workingCapital = appDAO.getWorkingCapital(appRefNo, strLoanType);
   						
						application.setWc(workingCapital);
						workingCapital = null;

						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());


						Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Before checkin:" + appRefNo);

			double dblApprovedAmt = 0;
			//Eligible Non-Duplicate Applications
			if (!(dupAppRefNoList.contains(appRefNo)) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
      //  //System.out.println(" ORG >>>>>>>>>>Coming here in Eligible Non-Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Non Duplicate Ref No :" +appRefNo);
				Application clearApplication=new Application();
				BorrowerDetails clearBorrowerDetails=new BorrowerDetails();
				//SSIDetails clearSsiDetails=new SSIDetails();
				clearBorrowerDetails.setSsiDetails(new SSIDetails());
				clearApplication.setBorrowerDetails(clearBorrowerDetails);
				clearBorrowerDetails=null;


				java.util.Date submittedDate=application.getSubmittedDate();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","submitted Date:" + submittedDate);
				int ssiRefNo=(application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","ssi ref no:" + ssiRefNo);
        clearApplication.setMliID(memId);
				clearApplication.setAppRefNo(appRefNo);
				clearApplication.setSubmittedDate(submittedDate);
				clearApplication.setTermLoan(application.getTermLoan());
				clearApplication.setWc(application.getWc());
				clearApplication.setLoanType(strLoanType);
				dblApprovedAmt = calApprovedAmount(application);
				clearApplication.setApprovedAmount(dblApprovedAmt);
				clearApplication.setStatus(application.getStatus());
				clearApplication.setRemarks(application.getRemarks());
      //  //System.out.println("Application RefNO:"+appRefNo);
        java.util.Date sanctionedDate = null;
        if(strLoanType.equals("TC"))
				{
       sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
        }else if(strLoanType.equals("WC")){
        if(application.getWc().getLimitFundBasedSanctionedDate()!=null){
          sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
        } else {
          sanctionedDate = application.getWc().getLimitNonFundBasedSanctionedDate();
        }        
        }else if (strLoanType.equals("CC")){
        sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
        }
       clearApplication.setSanctionedDate(sanctionedDate); 
      // clearApplication.setActivity(application.getBorrowerDetails().getSsiDetails().getActivityType());
    //  //System.out.println("Sanctioned Date:"+sanctionedDate);  
		//	//System.out.println("Activity:"+application.getBorrowerDetails().getSsiDetails().getActivityType());	
    //  //System.out.println("SSI Name:"+application.getBorrowerDetails().getSsiDetails().getSsiName()); 
        (clearApplication.getBorrowerDetails().getSsiDetails()).setBorrowerRefNo(ssiRefNo);
        (clearApplication.getBorrowerDetails().getSsiDetails()).setActivityType(application.getBorrowerDetails().getSsiDetails().getActivityType());
            
           
       
				eligibleNonDupApps.add(clearApplication);
				submittedDate = null;

				clearApplication = null;
			}
			//Probable Eligible Duplicate Application
			else if (dupAppRefNoList.contains(appRefNo) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
     //   //System.out.println(" ORG >>>>>>>>>>Coming here in Eligible Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
        java.util.Date sanctionedDate = null;
         
			
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
          sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
          
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				// sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
        if(application.getWc().getLimitFundBasedSanctionedDate()!=null){
          sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
        } else {
          sanctionedDate = application.getWc().getLimitNonFundBasedSanctionedDate();
        }
        }else if (strLoanType.equals("CC")){
					
         
          
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					 sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
				}
        
				DuplicateApplication eligibleDupApp=new DuplicateApplication();
				eligibleDupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				eligibleDupApp.setDupCreditAmount(creditAmount);
				dblApprovedAmt = calApprovedAmount(application);
				eligibleDupApp.setDupApprovedAmount(dblApprovedAmt);
				eligibleDupApp.setStatus(application.getStatus());
      //  String prevSSI = application.getPrevSSI();
       //   String existSSI = application.getExistSSI();
       // eligibleDupApp.setPrevSSi(prevSSI);
      //  eligibleDupApp.setExistSSi(existSSI);
     //  //System.out.println("App Reference NUmber:"+appRefNo);
     //   //System.out.println("EDuplicate sanctionedDate:"+sanctionedDate);
        eligibleDupApp.setSanctionedDate(sanctionedDate);
       	eligibleDupApp.setDuplicateRemarks(application.getRemarks());
				eligibleDupApps.add(eligibleDupApp);

				eligibleDupApp = null;

			}
			//Ineligible Non Duplicate applications
			else if (!(dupAppRefNoList.contains(appRefNo)) && eligibleAppRefNoList.contains(appRefNo))
			{
     //   //System.out.println(" ORG >>>>>>>>>>Coming here in Ineligible Non Duplicate applications");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Non Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}

				EligibleApplication inEligibleApplication=new EligibleApplication();
				inEligibleApplication=(EligibleApplication)ineligibleApplications.get(appRefNo);
				inEligibleApplication.setEligibleCreditAmount(creditAmount);
				inEligibleApplication.setSubmissiondate(application.getSubmittedDate().toString());
				dblApprovedAmt = calApprovedAmount(application);
				inEligibleApplication.setEligibleApprovedAmount(dblApprovedAmt);
				inEligibleApplication.setStatus(application.getStatus());
				inEligibleApplication.setEligibleRemarks(application.getRemarks());
				ineligibleNonDupApps.add(inEligibleApplication);				
				inEligibleApplication = null;
			}
			//Ineligible Duplicate Applications
			else if (dupAppRefNoList.contains(appRefNo) && eligibleAppRefNoList.contains(appRefNo))
			{
    //    //System.out.println(" ORG >>>>>>>>>>Coming here in Ineligible Duplicate Applications");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount;					
				}
				DuplicateApplication dupApp=new DuplicateApplication();
				dupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				dupApp.setDupCreditAmount(creditAmount);
				dupApp.setStatus(application.getStatus());
				dupApp.setDuplicateRemarks(application.getRemarks());

				EligibleApplication inEligibleApp=new EligibleApplication();
				inEligibleApp=(EligibleApplication)ineligibleApplications.get(appRefNo);
				dblApprovedAmt = calApprovedAmount(application);
				dupApp.setDupApprovedAmount(dblApprovedAmt);
				inEligibleApp.setSubmissiondate(application.getSubmittedDate().toString());
				inEligibleApp.setStatus(application.getStatus());
				inEligibleApp.setEligibleRemarks(application.getRemarks());
				
				dupApps.add(dupApp);
				ineligibleApps.add(inEligibleApp);
				dupApp = null;
				inEligibleApp = null;
			}
			strLoanType = null;
		appRefNo = null;
		application  = null;
		}
		ssiDetails=null;
		borrowerDetails = null;
		eligibleAppRefNoList = null;
		ineligibleDupApps.add(dupApps);
		ineligibleDupApps.add(ineligibleApps);

		applicationsList.add(eligibleNonDupApps);
		applicationsList.add(eligibleDupApps);
		applicationsList.add(ineligibleNonDupApps);
		applicationsList.add(ineligibleDupApps);
		applicationsList.add(intCount);
    		cgpan=null;
		intCount = null;
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Exited. Memory : "+ Runtime.getRuntime().freeMemory());
   //////////
/*
   duplicateApplications.clear();
			   ineligibleApplications.clear();

		   eligibleAppsList.clear();

		   eligibleNonDupApps.clear();
		   eligibleDupApps.clear();
		   ineligibleNonDupApps.clear();
		   ineligibleApps.clear();
		   dupApps.clear();
		   ineligibleDupApps.clear();
*/
			duplicateApplications=null;
			ineligibleApplications=null;

			application=null;
		eligibleApplication=null;

		eligibleAppsList=null;

		eligibleNonDupApps =null;
		eligibleDupApps =null;
		ineligibleNonDupApps =null;
		ineligibleApps =null;
		dupApps=null;
		ineligibleDupApps=null;

   ////////

	   return applicationsList;
   }


   /**
	* This method used to get all the applcations pertaining to a ID. this id could
	* be CGBID or MLI Id or CGPAN.
	* @param id - Type of id. It could be CGPAN or CGBID or MLI ID.
	* This type is resolved by the accompanying parameter.
	* @param type - Type of id. This parameter is used to identify the type of ID is
	* passed.
	* @return ArrayList
	* @roseuid 39B9D1E0009C
	*
   public ArrayList getAllApplications(String id, int type)
   {
	return null;
   }*/

   /**
	* This method retrieves all the message titles and contents from the database
	*/
	public ArrayList getMessageTitleContent() throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","getMessageTitleContent","Entered");

		ArrayList messageTitles=appDAO.getMessageTitleContent();

		Log.log(Log.INFO,"ApplicationProcessor","getMessageTitleContent","Exited");

		return messageTitles;
	}

	/**
	* This method retrieves the message content for the title passed
	*/

	public SpecialMessage getMessageDesc(String title)throws DatabaseException
	{

		Log.log(Log.INFO,"ApplicationProcessor","getMessageDesc","Entered");

		SpecialMessage specialMessage=appDAO.getMessageDesc(title);
		
		title=null;

		Log.log(Log.INFO,"ApplicationProcessor","getMessageDesc","Exited");
		return specialMessage;
	}

	/**
	* This method adds the special message in to the database
	*/

	public void addSpecialMessage(SpecialMessage specialMessage)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","addSpecialMessage","Entered");

		appDAO.addSpecialMessage(specialMessage);
		
		specialMessage=null;

		Log.log(Log.INFO,"ApplicationProcessor","addSpecialMessage","Exited");
	}

	/**
	* This method updates the special message in to the database
	*/

	public void updateSpecialMessage(SpecialMessage specialMessage)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","updateSpecialMessage","Entered");

		appDAO.updateSpecialMessage(specialMessage);
		
		specialMessage=null;

		Log.log(Log.INFO,"ApplicationProcessor","updateSpecialMessage","Exited");
	}

	/**
	* This method is used to generate CGPAN for approved applications
	*/

	public String generateCgpan(Application application)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","generateCgpan","Entered");

		String cgpan=appDAO.generateCgpan(application);
//		//System.out.println("CGPAN:"+cgpan);
		application=null;

		Log.log(Log.INFO,"ApplicationProcessor","generateCgpan","Exited");

		return cgpan;

	}

	/**
	* This method is used to update cgpan into the DB
	*

	public void updateCgpan(Application application)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","updateCgpan","Entered");

		appDAO.updateCgpan(application);

		Log.log(Log.INFO,"ApplicationProcessor","updateCgpan","Exited");
	}

	/**
	* This method updates the general status into the DB
	*/

	public void updateGeneralStatus(Application application,String userId)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","updateGeneralStatus","Entered");

		appDAO.updateGeneralStatus(application,userId);
		
		application=null;
		userId=null;

		Log.log(Log.INFO,"ApplicationProcessor","updateGeneralStatus","Exited");

	}

	/**
	* This method generates cgbid for an approved application
	*

	public String generateCgbid(int ssiRefNo)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","updateGeneralStatus","Entered");

		String cgbid=appDAO.generateCgbid(ssiRefNo);

		Log.log(Log.INFO,"ApplicationProcessor","updateGeneralStatus","Exited");

		return cgbid;

	}

	/**
	* This method updates cgbid for an approved application
	*

	public void updateCgbid(int ssiRefNo,String cgbid)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","updateCgbid","Entered");

		appDAO.updateCgbid(ssiRefNo,cgbid);
		
		cgbid=null; 		

		Log.log(Log.INFO,"ApplicationProcessor","updateCgbid","Exited");


	}

	/**
	* This method views the borrower details with ssi reference number as the input parameter
	*/

	public BorrowerDetails viewBorrowerDetails(int ssiRefNo)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","viewBorrowerDetails","Entered");

		BorrowerDetails borrowerDetails=appDAO.viewBorrowerDetails(ssiRefNo);

		Log.log(Log.INFO,"ApplicationProcessor","viewBorrowerDetails","Exited");

		return borrowerDetails;

	}

/*
 * This method retrieves the total corpus amount
 * @author SS14485
 */

 public double getCorpusAmount()throws DatabaseException
 {
		Log.log(Log.INFO,"ApplicationProcessor","updateCgbid","Entered");

		double corpusAmount=appDAO.getCorpusAmount();

		Log.log(Log.INFO,"ApplicationProcessor","updateCgbid","Exited");

		return corpusAmount;
 }

 /*
  * This method checks whether the cgpan passed is within the securitization pool
  * @author SS14485
 */

 public void checkCgpanPool(String appRefNo)throws DatabaseException
 {
	Log.log(Log.INFO,"ApplicationProcessor","checkCgpanPool","Entered");

	appDAO.checkCgpanPool(appRefNo);
	
	appRefNo=null;

	Log.log(Log.INFO,"ApplicationProcessor","checkCgpanPool","Exited");

 }

 public Application getAppForCgpan(String mliId,String cgpan)throws DatabaseException
 {  
	Log.log(Log.INFO,"ApplicationProcessor","checkCgpanPool","Entered");

	Application application=appDAO.getAppForCgpan(mliId,cgpan);
  ////System.out.println("cgpan is "+cgpan);
		Log.log(Log.INFO,"ApplicationProcessor","checkCgpanPool","Exited");

	return application;

 }

 /*
  * This method updates the status of the reapproved Applications
  * @author SS14485
  *
  * To change the template for this generated type comment go to
  * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
  */

  public void updateReapprovalStatus(Application application,String userId)throws DatabaseException
  {
		Log.log(Log.INFO,"ApplicationProcessor","updateReapprovalStatus","Entered");

		appDAO.updateReapprovalStatus(application,userId);
		
		application=null;
		userId=null;

			Log.log(Log.INFO,"ApplicationProcessor","updateReapprovalStatus","Exited");
  }

/**
 * This method updates the cgpan for WC Renewed Applications
 * @author SS14485
 *
 */

	public String generateRenewCgpan(String renewalCgpan)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","generateRenewCgpan","Entered");

		String renewCgpan=appDAO.generateRenewCgpan(renewalCgpan);

		Log.log(Log.INFO,"ApplicationProcessor","generateRenewCgpan","Exited");

		return renewCgpan;

	}


	/**
	 * This method checks whether the cgpan entered for renewal is the latest cgpan or not
	 * @param cgpan
	 * @throws DatabaseException
	 */

	public String checkRenewCgpan(String cgpan)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","checkRenewCgpan","Entered");

		String stringVal=appDAO.checkRenewCgpan(cgpan);

		Log.log(Log.INFO,"ApplicationProcessor","checkRenewCgpan","Exited");
		
		return stringVal;


	}


	/*
	 * This method calculates the approved amount for the approved Amount
	 * @author SS14485
	 */
		 private double calApprovedAmount(Application application)
		 {
			TermLoan termLoan=new TermLoan();
			WorkingCapital workingCapital=new WorkingCapital();
			SubSchemeValues subSchemeValues = new SubSchemeValues(); 
			
			RiskManagementProcessor rpProcessor=new RiskManagementProcessor();

			//ApplicationProcessor appProcessor=new ApplicationProcessor();
			String mliId=null;
			String cgpan="";


			double approvedAmount=0;
			double calApprovedAmt = 0;

			try
			{


				//application=appProcessor.getApplication(mliId,cgpan,appRefNo);
				String loanType=application.getLoanType();
				String subScheme=application.getSubSchemeName();
				double maxApprovedAmt=0;
				double maxGCoverAmt=0;
        double maxRsfApprovedAmt=0;
				if (!(subScheme.equals(Constants.GLOBAL_SUB_SCHEME)))
				{					

					//credit to be guaranteed
					double creditAmount=0;

					if (loanType.equals(ApplicationConstants.TC_APPLICATION))
					{
						creditAmount=application.getTermLoan().getCreditGuaranteed();
					}
					else if (loanType.equals(ApplicationConstants.WC_APPLICATION))
					{
						double fundBasedCredit=application.getWc().getCreditFundBased();
						double nonFundBasedCredit=application.getWc().getCreditNonFundBased();
						creditAmount=fundBasedCredit + nonFundBasedCredit;
					}else if (loanType.equals(ApplicationConstants.CC_APPLICATION))
					{
						double tcCreditAmount=application.getTermLoan().getCreditGuaranteed();

						double fundBasedCredit=application.getWc().getCreditFundBased();
						double nonFundBasedCredit=application.getWc().getCreditNonFundBased();
						double wcCreditAmount=fundBasedCredit + nonFundBasedCredit;

						creditAmount= tcCreditAmount + wcCreditAmount;
					}
					

					subSchemeValues = rpProcessor.getSubSchemeValues(subScheme);
					if(subSchemeValues!=null)
					{
					
					//Maximum Guarantee Cov	er Exposure in  %
/*						double maxGCover=subSchemeValues.getMaxGCoverExposure();//in %
	
						double corpusAmount=getCorpusAmount();
	
						//Maximum Guarantee Cover Exposure Amount=(Maximum Guarantee Cover Exposure)
					//											Corpus Amount
						if(corpusAmount!=0)
						{
							maxGCoverAmt=(maxGCover / 100) * corpusAmount;
						}
			*/
						double maxBorrowerExposure=subSchemeValues.getMaxBorrowerExposureAmount();
	     //     //System.out.println("Line number 3707 maxBorrowerExposure"+maxBorrowerExposure);
						double maxExposureAmt;
	
						maxExposureAmt=maxBorrowerExposure;
	
/*						Administrator admin=new Administrator();
						ParameterMaster paramMaster=admin.getParameter();
						maxApprovedAmt=paramMaster.getMaxApprovedAmt();
						Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","max approved amount:" + maxApprovedAmt);
						
	
						if (maxApprovedAmt == -1)
						{
							maxCreditAmount = creditAmount;
						}
						else if (creditAmount < maxApprovedAmt)
						{
							maxCreditAmount=creditAmount;
						}
						else {
	
							maxCreditAmount=maxApprovedAmt;
						}
	*/
						
						if (maxExposureAmt < creditAmount)
						{
							approvedAmount=maxExposureAmt;
						}else{
	
							approvedAmount=creditAmount;
						}
						
						
/*						admin = null;
						paramMaster = null;
*/						
					}
					else{
						
						approvedAmount = creditAmount;						
					}
					
					Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","approvedAmount :" + approvedAmount + "for app ref" + application.getAppRefNo());
					
					/**
					 * calculation of the approved amount based on global limit
					 */
					
					GlobalLimits globalLimits = rpProcessor.getGlobalLimits(application.getScheme(),application.getSubSchemeName());
					
					double globalAmount = globalLimits.getUpperLimit();
					
					Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","globalAmount:" + globalAmount);
					
						/**
						 * Retrieve the approved amount depending on the scheme and subscheme names
						 */		
						
					double totalApprovedAmt = getTotalApprovedAmt(application);
					
					Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","globalAmount:" + totalApprovedAmt);
					
					double diffAmount;
					
					if (globalAmount !=0)
					{
						if(totalApprovedAmt > globalAmount || totalApprovedAmt == globalAmount)
						{
							calApprovedAmt = approvedAmount;
						}
						else if (globalAmount > totalApprovedAmt)
						{
							diffAmount = globalAmount - totalApprovedAmt;
							
							Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","diffAmount:" + diffAmount);
							
							if(diffAmount > approvedAmount)
							{
								calApprovedAmt = approvedAmount;
							}
							else if(diffAmount < approvedAmount)
							{
								calApprovedAmt = approvedAmount - diffAmount;
							}
						}
						
					}
					else{
						
						calApprovedAmt = approvedAmount;						
					}
					
					double mcgsAmount = 0;
					double diffMcgsAmount;
					if(application.getScheme().equals(ApplicationConstants.MCGS_SCHEME))
					{
						//get the amount for the participating bank limit
						double partBankAmount = getPartBankAmount(application.getMliID().substring(0,4),application.getMliID().substring(4,8),application.getMliID().substring(8,12));
						double mcgsApprovedAmt = getMcgsApprovedAmount(application.getMliID().substring(0,4),application.getMliID().substring(4,8),application.getMliID().substring(8,12));
						Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","partBankAmount:" + partBankAmount);
						Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","mcgsApprovedAmt:" + mcgsApprovedAmt);
						if(partBankAmount!=0 && mcgsApprovedAmt!=0)
						{
							if(mcgsApprovedAmt > partBankAmount || mcgsApprovedAmt==partBankAmount)
							{
								mcgsAmount = calApprovedAmt;
							}
							else if(partBankAmount > mcgsApprovedAmt)
							{
								diffMcgsAmount = partBankAmount - mcgsApprovedAmt;
								Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","diffMcgsAmount:" + diffMcgsAmount);
								if(calApprovedAmt > diffMcgsAmount)
								{
									mcgsAmount = calApprovedAmt - diffMcgsAmount;
								}
								else if (calApprovedAmt < diffMcgsAmount)
								{
									mcgsAmount = calApprovedAmt;
								}
							}
							
							calApprovedAmt = mcgsAmount;
							
						}
					}
					Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","calApprovedAmt:" + calApprovedAmt);
											
					

				}else if (subScheme.equals(Constants.GLOBAL_SUB_SCHEME))
				{
      //   //System.out.println("loanType"+loanType);
					double creditAmount=0;
					//double maxCreditAmount=0;
					if (loanType.equals(ApplicationConstants.TC_APPLICATION))
					{
						creditAmount=application.getTermLoan().getCreditGuaranteed();
					}
					else if (loanType.equals(ApplicationConstants.WC_APPLICATION))
					{
						double fundBasedCredit=application.getWc().getCreditFundBased();
        //    //System.out.println("fundBasedCredit"+fundBasedCredit);
						double nonFundBasedCredit=application.getWc().getCreditNonFundBased();
        //    //System.out.println("nonFundBasedCredit"+nonFundBasedCredit);
						creditAmount=fundBasedCredit + nonFundBasedCredit;

					}else if (loanType.equals(ApplicationConstants.CC_APPLICATION))
					{
						double tcCreditAmount=application.getTermLoan().getCreditGuaranteed();

						double fundBasedCredit=application.getWc().getCreditFundBased();
						double nonFundBasedCredit=application.getWc().getCreditNonFundBased();
						double wcCreditAmount=fundBasedCredit + nonFundBasedCredit;

						creditAmount= tcCreditAmount + wcCreditAmount;
					}
					Administrator admin=new Administrator();
					ParameterMaster paramMaster=admin.getParameter();
					maxApprovedAmt=paramMaster.getMaxApprovedAmt();
          maxRsfApprovedAmt=paramMaster.getMaxRsfApprovedAmt();
			//		//System.out.println("Line number 3895 maxApprovedAmt:"+maxApprovedAmt);
      //    //System.out.println("Line number 3896 maxRsfApprovedAmt:"+maxRsfApprovedAmt);
         if(application.getScheme().equals("RSF") && creditAmount<=maxRsfApprovedAmt){
             calApprovedAmt=creditAmount;   
       //   //System.out.println("Line number3902- calApprovedAmt"+calApprovedAmt);
          }
				 else	if (maxApprovedAmt==-1.0)
					{
						approvedAmount = creditAmount;
					}
					else if(creditAmount>maxApprovedAmt)
					{
						calApprovedAmt = maxApprovedAmt;
					}
					else{
						
						calApprovedAmt=creditAmount;
					}
			
					double mcgsAmount = 0;
					double diffMcgsAmount;
					if(application.getScheme().equals(ApplicationConstants.MCGS_SCHEME))
					{
						//get the amount for the participating bank limit
						double partBankAmount = getPartBankAmount(application.getMliID().substring(0,4),application.getMliID().substring(4,8),application.getMliID().substring(8,12));
						double mcgsApprovedAmt = getMcgsApprovedAmount(application.getMliID().substring(0,4),application.getMliID().substring(4,8),application.getMliID().substring(8,12));
						Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","partBankAmount:" + partBankAmount);
						Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","mcgsApprovedAmt:" + mcgsApprovedAmt);
						if(partBankAmount!=0 && mcgsApprovedAmt!=0)
						{
							if(mcgsApprovedAmt > partBankAmount || mcgsApprovedAmt==partBankAmount)
							{
								mcgsAmount = calApprovedAmt;
							}
							else if(partBankAmount > mcgsApprovedAmt)
							{
								diffMcgsAmount = partBankAmount - mcgsApprovedAmt;
								Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","diffMcgsAmount:" + diffMcgsAmount);
								if(calApprovedAmt > diffMcgsAmount)
								{
									mcgsAmount = calApprovedAmt - diffMcgsAmount;
								}
								else if (calApprovedAmt < diffMcgsAmount)
								{
									mcgsAmount = calApprovedAmt;
								}
							}
							
							calApprovedAmt = mcgsAmount;
							
						}
					}
					Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","calApprovedAmt:" + calApprovedAmt);

				}
				loanType = null;
				subScheme = null;
				Log.log(Log.INFO,"ApplicationProcessor","calApprovedAmount","calApprovedAmt 1:" + calApprovedAmt);

			}catch (Exception e)
			{
				Log.log(Log.ERROR,"ApplicationProcessor","calApprovedAmount",e.getMessage());
				Log.logException(e);

			}

			termLoan=null;
			workingCapital=null;
			rpProcessor=null;


			return calApprovedAmt;

		 }
		
/**
 * This method retrieves teh SSI Reference Numbers for the MLId Id passed
 */	

	public ArrayList getSsiRefNosForMcgf(String memberId)throws DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","getSsiRefNosForMcgf","Entered");
		
		ArrayList ssiRefNosList=appDAO.getSsiRefNosForMcgf(memberId);
		
		Log.log(Log.INFO,"ApplicationProcess1or","getSsiRefNosForMcgf","Exited");
		
		return ssiRefNosList;
	}

	/**
	* This method is called when BID and MLI id are entered
	*/
	public ArrayList getDtlForBIDMem(String borrowerId,String bankId,String zoneId,String branchId)
														throws DatabaseException,NoApplicationFoundException
	{
		Log.log(Log.INFO,"ApplicationProcessor","getDtlForBIDMem","Entered");

		ArrayList cgpanAppRefNos = appDAO.getDtlForBIDMem(borrowerId, bankId, zoneId, branchId);

		Log.log(Log.INFO,"ApplicationProcessor","getDtlForBIDMem","Exited");
		return cgpanAppRefNos;
	}
	
	/**
	 * This method updates the pending and rejected status of the applications
	 * @author SS14485
	 */
	
	public void updatePendingRejectedStatus(Application application,String userId)throws DatabaseException
	{
		//Log.log(Log.INFO,"ApplicationProcessor","updatePendingRejectedStatus","Entered");
		
		appDAO.updatePendingRejectedStatus(application,userId);
		
		//Log.log(Log.INFO,"ApplicationProcessor","updatePendingRejectedStatus","Exited");
		
	}
	
	public Application getOldApplication(String mliID,String cgpan,String appRefNo) throws NoApplicationFoundException,DatabaseException
	{
		Log.log(Log.INFO,"ApplicationProcessor","getApplication","Entered");

	   Application application=null;

	   Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of CGPAN:" + cgpan);

	   Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of App Ref No:" + appRefNo);

	   //if any of the following conditions is satisfied,the application form is returned
	   if((!(cgpan.equals("")))||
		   (!(appRefNo.equals("")))){
			   application=appDAO.getOldApplication(mliID,cgpan,appRefNo);

		   if(application==null){
			   throw new NoApplicationFoundException("No Application Found");
		   }

	   }/*else if((!(mliID.equals("")))||(!(cgbid.equals("")))||(!(borrowerName.equals("")))){
		   application=null;

	   }	*/

	   Log.log(Log.DEBUG,"ApplicationProcessor","getApplication","Value of Application :" + application);

	   Log.log(Log.INFO,"ApplicationProcessor","getApplication","Exited");

	   return application;
	}

	/**
	 * This method updates the  status of the enhancement applications 
	 * @author SS14485
	 */		
	
	public void updateEnhanceAppStatus(Application application,String userId)throws DatabaseException
	{
		appDAO.updateEnhanceAppStatus(application,userId);
              
	}
	
	/**
	 * This method updates the  rejected status of the enhancement applications 
	 * @author SS14485
	 */		
	
	public void updateRejectStatus(Application application,String userId)throws DatabaseException
	{
		appDAO.updateRejectStatus(application,userId);
	}
	
	public double getTotalApprovedAmt(Application application)throws DatabaseException
	{
		double totalApprovedAmt = appDAO.getTotalApprovedAmt(application);
		
		return totalApprovedAmt;
	}
	
	public int getClaimCount(String bid)throws DatabaseException
	{
		int claimCount = appDAO.getClaimCount(bid);
		
		return claimCount;
	}

	public double getCorpusContAmt(int ssiRefNumber)throws DatabaseException
	{
		double corpusContAmt = appDAO.getCorpusContAmt(ssiRefNumber);
		
		return corpusContAmt;
	}

	public double getPartBankAmount(String bnkId,String zoneId,String branchId)throws DatabaseException
	{
		double partBankAmount = appDAO.getPartBankAmount(bnkId,zoneId,branchId);
		
		return partBankAmount;
	}
	
	public double getMcgsApprovedAmount(String bnkId,String zoneId,String branchId)throws DatabaseException
	{
		double mcgsApprovedAmt = appDAO.getMcgsApprovedAmount(bnkId,zoneId,branchId);
		
		return mcgsApprovedAmt;
	}
	
	public ArrayList getCountForTCConv()throws DatabaseException
	{
		ArrayList countTCApp = appDAO.getCountForTCConv();
		
		return countTCApp;
	}

	public ArrayList getCountForWCConv()throws DatabaseException
	{
		ArrayList countWCApp = appDAO.getCountForWCConv();
		
		return countWCApp;
	}

	public void updateTCConv(Application application,int appSSIRef)throws DatabaseException
	{
		appDAO.updateTCConv(application,appSSIRef);		
		
	}

	public void updateWCConv(Application application,int appSSIRef)throws DatabaseException
	{
		appDAO.updateWCConv(application,appSSIRef);		
		
	}

	public ArrayList getCountForDanGen(String appRefNo)throws DatabaseException
	{
		ArrayList countAmount = appDAO.getCountForDanGen(appRefNo);
		
		return countAmount;		
		
	}
  
	
	public void generateDanForEnhance(Application application,User user)throws DatabaseException
	{
		appDAO.generateDanForEnhance(application,user);
        ////System.out.println("exited from application processor after generateing dan foir enhance");
		
	}
	
	public double getBalanceApprovedAmt(Application application) throws DatabaseException
	{
		return appDAO.getBalanceApprovedAmt(application);
	}

	public void updateAppCgpanReference(Application application) throws DatabaseException
	{
		appDAO.updateAppCgpanReference(application);
	}
	
	 public ArrayList getApplicationsForApprovalForMLIWise(String userId,String bank) throws DatabaseException,NoApplicationFoundException
	 {
  ArrayList applicationsList=new ArrayList();
//  //System.out.println("PATH getApplicationsForApproval userId ***** = "+userId);
  Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Entered. Memory : "+ Runtime.getRuntime().freeMemory());

  HashMap duplicateApplications=new HashMap();
  HashMap ineligibleApplications=new HashMap();

  Application application=new Application();
  EligibleApplication eligibleApplication=new EligibleApplication();

		ArrayList eligibleAppsList=new ArrayList();

		ArrayList eligibleNonDupApps =new ArrayList();
		ArrayList eligibleDupApps =new ArrayList();
		ArrayList ineligibleNonDupApps =new ArrayList();
		ArrayList ineligibleApps =new ArrayList();
		ArrayList dupApps=new ArrayList();
		ArrayList ineligibleDupApps=new ArrayList();

		String mliId=null;
		String cgpan="";

		/*
		 * Performs duplicate Check for all the applications across MLIs
		 */
		String mliFlag=Constants.ACROSS_MLIS;

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList duplicateApp = new ArrayList();

		//System.out.println("AP checkDuplicateForMLIWise start1");
		ArrayList duplicateApps=checkDuplicateForMLIWise(mliFlag,bank);
		//System.out.println("AP checkDuplicateForMLIWise end1");
		
		ArrayList tcDuplicateApp =(ArrayList)duplicateApps.get(0); 
		ArrayList wcDuplicateApp =(ArrayList)duplicateApps.get(1);		
				
		for(int i=0;i<tcDuplicateApp.size();i++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)tcDuplicateApp.get(i);
			duplicateApp.add(duplicateApplication);
		}
		for(int j=0;j<wcDuplicateApp.size();j++)
		{
			DuplicateApplication duplicateApplication = (DuplicateApplication)wcDuplicateApp.get(j);
			duplicateApp.add(duplicateApplication);
		}		

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get duplicate Applications Memory Size :" + Runtime.getRuntime().freeMemory());
 /* --comment part end here on 09-08-2008   */

		ArrayList dupAppRefNoList=new ArrayList();
		int duplicateAppSize=duplicateApp.size();
////System.out.println("PATH duplicateApp.size after ading WC and TC application is ***** = "+duplicateApp.size());		    

		/*
		 * This loop retrieves all the duplicate app reference nos.
		 */
		DuplicateApplication dupApplication = null;
		for (int a=0;a<duplicateAppSize;a++)
		{

			dupApplication=(DuplicateApplication)duplicateApp.get(a);
			String dupRefNo=dupApplication.getNewAppRefNo();
			duplicateApplications.put(dupRefNo,dupApplication);
			dupAppRefNoList.add(dupRefNo);
  // //System.out.println("PATH dupRefNo = ***** "+dupRefNo);      
			dupRefNo = null;
		}
		dupAppRefNoList.trimToSize();
		
		dupApplication = null;
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","duplicate app ref nos size :" + dupAppRefNoList.size());
// //System.out.println("PATH duplicate app ref nos size :***** = " + dupAppRefNoList.size());

		/*
		 * Performs eligbility Check for all the pending applications
		 */

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());
////System.out.println("PATH before calling pendingAppList=appDAO.viewApplicationsForApproval(userId) **** userId = "+userId);
		ArrayList pendingAppList=appDAO.viewApplicationsForApprovalForMLIWise(userId,bank);
       System.out.println("pendingAppList.size()"+pendingAppList.size());
////System.out.println("PATH after calling pendingAppList=appDAO.viewApplicationsForApproval(userId) **** userId = "+userId);
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Pending size:" + pendingAppList.size());

		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get view applications for approval Memory Size :" + Runtime.getRuntime().freeMemory());

		ArrayList tcPendingAppList=(ArrayList)pendingAppList.get(0);
		ArrayList wcPendingAppList=(ArrayList)pendingAppList.get(1);
		int tcPendingAppListSize=tcPendingAppList.size();
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","tc Pending size:" + tcPendingAppListSize);
		int wcPendingAppListSize=wcPendingAppList.size();
	Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","wc Pending size:" + wcPendingAppListSize);

		//retrievin the count of applications beyond user's limit
		Integer intCount=(Integer)pendingAppList.get(2);
 		
		pendingAppList.clear();
		pendingAppList = null;

		ArrayList appRefNoList=new ArrayList();
		Application tcApplication = null;
		String tcAppRefNo = null;
		for (int x=0;x<tcPendingAppListSize;x++)
		{
			
			//Application tcApplication=new Application();
			tcApplication=(Application)tcPendingAppList.get(x);
			tcAppRefNo=tcApplication.getAppRefNo();			
			appRefNoList.add(tcAppRefNo);

		}
		tcPendingAppList.clear();
		tcPendingAppList = null;
		tcApplication = null;
		tcAppRefNo = null;
		Application wcApplication = null;
		String wcAppRefNo = null;
		for (int y=0;y<wcPendingAppListSize;y++)
		{
			wcApplication=new Application();
			wcApplication=(Application)wcPendingAppList.get(y);
			wcAppRefNo=wcApplication.getAppRefNo();			
			appRefNoList.add(wcAppRefNo);

		}
		appRefNoList.trimToSize();
		wcPendingAppList.clear();
		wcPendingAppList = null;
		wcApplication = null;
		wcAppRefNo = null;
		int appRefNoListSize=appRefNoList.size();
//  //System.out.println("getApplicationsForApproval appRefNoListSize = "+appRefNoListSize);
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","pending app ref nos size :" + appRefNoListSize);
		for (int i=0;i<appRefNoListSize; i++)
		{
		//	String mliId=null;
		//	String cgpan="";

			String appRefNo=(String)appRefNoList.get(i);
   // //System.out.println("appRefNo-test:"+appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin eligiblity check :" + appRefNo);
			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			eligibleApplication=getAppsForEligibilityCheck(appRefNo);

			Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get eligiblity Check Memory Size :" + Runtime.getRuntime().freeMemory());

			//java.util.Date submittedDate = null;
			if (!(eligibleApplication.getFailedCondition().equals("")))
				{					
					/*application=new Application();
					Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin getApplication() :" + Runtime.getRuntime().freeMemory() + " in FOR loop i = "+i);
					application=getPartApplication(mliId,cgpan,appRefNo);
					Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin getApplication() :" + Runtime.getRuntime().freeMemory());
					java.util.Date submittedDate=application.getSubmittedDate();
					//String stringDate=submittedDate.toString();
					eligibleApplication.setSubmissiondate(submittedDate.toString());	 
					 */

					eligibleApplication.setAppRefNo(appRefNo);
 					

					eligibleAppsList.add(eligibleApplication);
				//	submittedDate = null;
					application = null;

				}
			appRefNo = null;
			eligibleApplication = null;
		//	cgpan = null;

		}
		Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","eligible apps size :" + eligibleAppsList.size());
		ArrayList eligibleAppRefNoList=new ArrayList();

		int eligibleAppsListSize=eligibleAppsList.size();
		/*
		 * This loop retreives all the eligible application reference nos.
		 */
		EligibleApplication eligibleApp = null;
		for (int b=0;b<eligibleAppsListSize;b++)
		{
			//EligibleApplication eligibleApp=new EligibleApplication();
			eligibleApp=(EligibleApplication)eligibleAppsList.get(b);
			String eligibleRefNo=eligibleApp.getAppRefNo();
			ineligibleApplications.put(eligibleRefNo,eligibleApp);
			eligibleAppRefNoList.add(eligibleRefNo);
			eligibleApp = null;
			eligibleRefNo = null;
		}
		eligibleAppRefNoList.trimToSize();

		/*
		 * Checks if the app ref no. of each of the pending application belongs
		 * to the eligible applications list or the duplicate applications list
		 */

		 //int dupAppRefNoListSize=dupAppRefNoList.size();
		// int eligibleAppRefNoListSize=eligibleAppRefNoList.size();

		BorrowerDetails borrowerDetails=new BorrowerDetails();
		SSIDetails ssiDetails=new SSIDetails();
		borrowerDetails.setSsiDetails(ssiDetails);
		String strLoanType = null;
		TermLoan termLoan = null;
  String memId = null;
		WorkingCapital workingCapital = null;
		for (int i=0;i<appRefNoListSize; i++)
		{
			double creditAmount=0;
			String appRefNo=(String)appRefNoList.get(i);

						application=new Application();
        Application     applicationTemp = new Application();

						application.setBorrowerDetails(borrowerDetails);


						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","Before callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());

						application=getPartApplication(mliId,cgpan,appRefNo);
         	strLoanType = application.getLoanType();
          memId = appDAO.getMemberIdforAppRef(appRefNo);
						termLoan = appDAO.getTermLoan(appRefNo,strLoanType);
						application.setTermLoan(termLoan);
						termLoan = null;
						workingCapital = appDAO.getWorkingCapital(appRefNo, strLoanType);
 						
						application.setWc(workingCapital);
						workingCapital = null;

						Log.log(Log.DEBUG,"ApplicationProcessor","getApplicationsForApproval","After callin to get Application Memory = :" + Runtime.getRuntime().freeMemory());


						Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Before checkin:" + appRefNo);

			double dblApprovedAmt = 0;
			//Eligible Non-Duplicate Applications
			if (!(dupAppRefNoList.contains(appRefNo)) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
    //  //System.out.println(" ORG >>>>>>>>>>Coming here in Eligible Non-Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Non Duplicate Ref No :" +appRefNo);
				Application clearApplication=new Application();
				BorrowerDetails clearBorrowerDetails=new BorrowerDetails();
				//SSIDetails clearSsiDetails=new SSIDetails();
				clearBorrowerDetails.setSsiDetails(new SSIDetails());
				clearApplication.setBorrowerDetails(clearBorrowerDetails);
				clearBorrowerDetails=null;


				java.util.Date submittedDate=application.getSubmittedDate();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","submitted Date:" + submittedDate);
				int ssiRefNo=(application.getBorrowerDetails().getSsiDetails()).getBorrowerRefNo();
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","ssi ref no:" + ssiRefNo);
      clearApplication.setMliID(memId);
				clearApplication.setAppRefNo(appRefNo);
				clearApplication.setSubmittedDate(submittedDate);
				clearApplication.setTermLoan(application.getTermLoan());
				clearApplication.setWc(application.getWc());
				clearApplication.setLoanType(strLoanType);
				dblApprovedAmt = calApprovedAmount(application);
				clearApplication.setApprovedAmount(dblApprovedAmt);
				clearApplication.setStatus(application.getStatus());
				clearApplication.setRemarks(application.getRemarks());
    //  //System.out.println("Application RefNO:"+appRefNo);
      java.util.Date sanctionedDate = null;
      if(strLoanType.equals("TC"))
				{
     sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
      }else if(strLoanType.equals("WC")){
      if(application.getWc().getLimitFundBasedSanctionedDate()!=null){
        sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
      } else {
        sanctionedDate = application.getWc().getLimitNonFundBasedSanctionedDate();
      }        
      }else if (strLoanType.equals("CC")){
      sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
      }
     clearApplication.setSanctionedDate(sanctionedDate); 
    // clearApplication.setActivity(application.getBorrowerDetails().getSsiDetails().getActivityType());
  //  //System.out.println("Sanctioned Date:"+sanctionedDate);  
		//	//System.out.println("Activity:"+application.getBorrowerDetails().getSsiDetails().getActivityType());	
  //  //System.out.println("SSI Name:"+application.getBorrowerDetails().getSsiDetails().getSsiName()); 
      (clearApplication.getBorrowerDetails().getSsiDetails()).setBorrowerRefNo(ssiRefNo);
      (clearApplication.getBorrowerDetails().getSsiDetails()).setActivityType(application.getBorrowerDetails().getSsiDetails().getActivityType());
          
         
     
				eligibleNonDupApps.add(clearApplication);
				submittedDate = null;

				clearApplication = null;
			}
			//Probable Eligible Duplicate Application
			else if (dupAppRefNoList.contains(appRefNo) && (!(eligibleAppRefNoList.contains(appRefNo))))
			{
   //   //System.out.println(" ORG >>>>>>>>>>Coming here in Eligible Duplicate Application");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","Eligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
      java.util.Date sanctionedDate = null;
       
			
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
        sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
        
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				// sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
      if(application.getWc().getLimitFundBasedSanctionedDate()!=null){
        sanctionedDate = application.getWc().getLimitFundBasedSanctionedDate();
      } else {
        sanctionedDate = application.getWc().getLimitNonFundBasedSanctionedDate();
      }
      }else if (strLoanType.equals("CC")){
					
       
        
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					 sanctionedDate = application.getTermLoan().getAmountSanctionedDate();
				}
      
				DuplicateApplication eligibleDupApp=new DuplicateApplication();
				eligibleDupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				eligibleDupApp.setDupCreditAmount(creditAmount);
				dblApprovedAmt = calApprovedAmount(application);
				eligibleDupApp.setDupApprovedAmount(dblApprovedAmt);
				eligibleDupApp.setStatus(application.getStatus());
    //  String prevSSI = application.getPrevSSI();
     //   String existSSI = application.getExistSSI();
     // eligibleDupApp.setPrevSSi(prevSSI);
    //  eligibleDupApp.setExistSSi(existSSI);
   //  //System.out.println("App Reference NUmber:"+appRefNo);
   //   //System.out.println("EDuplicate sanctionedDate:"+sanctionedDate);
      eligibleDupApp.setSanctionedDate(sanctionedDate);
     	eligibleDupApp.setDuplicateRemarks(application.getRemarks());
				eligibleDupApps.add(eligibleDupApp);

				eligibleDupApp = null;

			}
			//Ineligible Non Duplicate applications
			else if (!(dupAppRefNoList.contains(appRefNo)) && eligibleAppRefNoList.contains(appRefNo))
			{
   //   //System.out.println(" ORG >>>>>>>>>>Coming here in Ineligible Non Duplicate applications");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Non Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased() + application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount; 
					
				}

				EligibleApplication inEligibleApplication=new EligibleApplication();
				inEligibleApplication=(EligibleApplication)ineligibleApplications.get(appRefNo);
				inEligibleApplication.setEligibleCreditAmount(creditAmount);
				inEligibleApplication.setSubmissiondate(application.getSubmittedDate().toString());
				dblApprovedAmt = calApprovedAmount(application);
				inEligibleApplication.setEligibleApprovedAmount(dblApprovedAmt);
				inEligibleApplication.setStatus(application.getStatus());
				inEligibleApplication.setEligibleRemarks(application.getRemarks());
				ineligibleNonDupApps.add(inEligibleApplication);				
				inEligibleApplication = null;
			}
			//Ineligible Duplicate Applications
			else if (dupAppRefNoList.contains(appRefNo) && eligibleAppRefNoList.contains(appRefNo))
			{
  //    //System.out.println(" ORG >>>>>>>>>>Coming here in Ineligible Duplicate Applications");
				Log.log(Log.DEBUG,"ApplicationProcessingAction","showAppsForApproval","InEligible Duplicate Ref No :" +appRefNo);
				creditAmount=0;
				if(strLoanType.equals("TC"))
				{
					creditAmount=application.getTermLoan().getCreditGuaranteed();
				}else if(strLoanType.equals("WC"))
				{
					creditAmount=application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
				}else if (strLoanType.equals("CC")){
					
					double tcCreditAmount = application.getTermLoan().getCreditGuaranteed();
					double wcCreditAmount = application.getWc().getCreditFundBased()+ application.getWc().getCreditNonFundBased();
					creditAmount = tcCreditAmount + wcCreditAmount;					
				}
				DuplicateApplication dupApp=new DuplicateApplication();
				dupApp=(DuplicateApplication)duplicateApplications.get(appRefNo);
				dupApp.setDupCreditAmount(creditAmount);
				dupApp.setStatus(application.getStatus());
				dupApp.setDuplicateRemarks(application.getRemarks());

				EligibleApplication inEligibleApp=new EligibleApplication();
				inEligibleApp=(EligibleApplication)ineligibleApplications.get(appRefNo);
				dblApprovedAmt = calApprovedAmount(application);
				dupApp.setDupApprovedAmount(dblApprovedAmt);
				inEligibleApp.setSubmissiondate(application.getSubmittedDate().toString());
				inEligibleApp.setStatus(application.getStatus());
				inEligibleApp.setEligibleRemarks(application.getRemarks());
				
				dupApps.add(dupApp);
				ineligibleApps.add(inEligibleApp);
				dupApp = null;
				inEligibleApp = null;
			}
			strLoanType = null;
		appRefNo = null;
		application  = null;
		}
		ssiDetails=null;
		borrowerDetails = null;
		eligibleAppRefNoList = null;
		ineligibleDupApps.add(dupApps);
		ineligibleDupApps.add(ineligibleApps);

		applicationsList.add(eligibleNonDupApps);
		applicationsList.add(eligibleDupApps);
		applicationsList.add(ineligibleNonDupApps);
		applicationsList.add(ineligibleDupApps);
		applicationsList.add(intCount);
  		cgpan=null;
		intCount = null;
			Log.log(Log.INFO,"ApplicationProcessor","getApplicationsForApproval","Exited. Memory : "+ Runtime.getRuntime().freeMemory());
 //////////
/*
 duplicateApplications.clear();
			   ineligibleApplications.clear();

		   eligibleAppsList.clear();

		   eligibleNonDupApps.clear();
		   eligibleDupApps.clear();
		   ineligibleNonDupApps.clear();
		   ineligibleApps.clear();
		   dupApps.clear();
		   ineligibleDupApps.clear();
*/
			duplicateApplications=null;
			ineligibleApplications=null;

			application=null;
		eligibleApplication=null;

		eligibleAppsList=null;

		eligibleNonDupApps =null;
		eligibleDupApps =null;
		ineligibleNonDupApps =null;
		ineligibleApps =null;
		dupApps=null;
		ineligibleDupApps=null;

 ////////

	   return applicationsList;
 }

	 
	 public ArrayList checkDuplicateForMLIWise(String mliFlag,String bank)throws DatabaseException
	 {
	 	 Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Entered");

	 	ArrayList duplicateCriteriaObj=new ArrayList();
	    ArrayList tcDuplicateCriteriaObj=new ArrayList();
	    ArrayList wcDuplicateCriteriaObj=new ArrayList();

	    ApplicationDAO appDAO=new ApplicationDAO();
	    //System.out.println("AP appDAO.checkDuplicateForMLIWise(bank) start");
	    HashMap approvedPendingApplications=appDAO.checkDuplicateForMLIWise(bank);
	    //System.out.println("AP appDAO.checkDuplicateForMLIWise(bank) end");
	    
	 	HashMap tcApprovedApplications=(HashMap)approvedPendingApplications.get("tcApproved");
	 	HashMap wcApprovedApplications=(HashMap)approvedPendingApplications.get("wcApproved");		
	 	
	 	HashMap tcPendingApplications=(HashMap)approvedPendingApplications.get("tcPending");
	 	HashMap wcPendingApplications=(HashMap)approvedPendingApplications.get("wcPending");		

	 	Set tcApprovedAppsKeys=tcApprovedApplications.keySet();
	 	Set wcApprovedAppsKeys=wcApprovedApplications.keySet();
	 	
	 	Set tcPendingAppsKeys=tcPendingApplications.keySet();
	 	Set wcPendingAppsKeys=wcPendingApplications.keySet();

	 	Iterator tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
	 	Iterator wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
	 	
	 	Iterator tcPendingAppsIterator=tcPendingAppsKeys.iterator();
	 	Iterator wcPendingAppsIterator=wcPendingAppsKeys.iterator();

	 	if (mliFlag.equals(Constants.WITHIN_MLIS))
	 	{
	 		Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering if flag is W");
	 //   //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is W");
	 		String mliID = null;
	 		ArrayList approvedApps = null;
	 		ArrayList pendingApps = null;
	 		
	 		while (tcPendingAppsIterator.hasNext())
	 		{
	 			ArrayList tcDuplicateCriteriaObjTemp = new ArrayList();
	 			Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering pending interator..");

	 			mliID=(String)tcPendingAppsIterator.next();
	 			approvedApps=(ArrayList)tcApprovedApplications.get(mliID);

	 			if (approvedApps==null)
	 			{					
	 				approvedApps=new ArrayList();
	 				pendingApps=(ArrayList)tcPendingApplications.get(mliID);
	 				tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);

	 			}else{
	 				
	 				pendingApps=(ArrayList)tcPendingApplications.get(mliID);
	 				tcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
	 			}
	 			
	 			for(int i=0; i<tcDuplicateCriteriaObjTemp.size(); i++)
	 			{
	 				DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjTemp.get(i);
	 				tcDuplicateCriteriaObj.add(tcDupApplication);
	 			}


	 			mliID = null;
	 			approvedApps = null;
	 			pendingApps = null;

	 		}
	 		
	 		while (wcPendingAppsIterator.hasNext())
	 		{
	 			Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering wc pending interator..");

	 			ArrayList wcDuplicateCriteriaObjTemp = new ArrayList();
	 			
	 			mliID=(String)wcPendingAppsIterator.next();
	 			approvedApps=(ArrayList)wcApprovedApplications.get(mliID);

	 			if (approvedApps==null)
	 			{					
	 				approvedApps=new ArrayList();
	 				pendingApps=(ArrayList)wcPendingApplications.get(mliID);
	 				wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);
	 				
	 				//return 	duplicateCriteriaObj;	
	 				
	 				//break;				
	 			}else{
	 				
	 				pendingApps=(ArrayList)wcPendingApplications.get(mliID);
	 				wcDuplicateCriteriaObjTemp=checkDuplicateApplications(approvedApps,pendingApps);

	 			}
	 			
	 			for(int j=0; j<wcDuplicateCriteriaObjTemp.size(); j++)
	 			{
	 				DuplicateApplication wcDupApplication = (DuplicateApplication)wcDuplicateCriteriaObjTemp.get(j);
	 				wcDuplicateCriteriaObj.add(wcDupApplication);
	 			}


	 			mliID = null;
	 			approvedApps = null;
	 			pendingApps = null;

	 		}
	 		
	 		duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
	 		duplicateCriteriaObj.add(wcDuplicateCriteriaObj);
	 		
	 	}else if (mliFlag.equals(Constants.ACROSS_MLIS))
	 	{
	 		Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Entering if flag is A");
//	     //System.out.println("PATH ApplicationProcessor checkDuplicate is Entering if flag is A");
	 		ArrayList tcDuplicateCriteriaObjtemp = new ArrayList();
	 		ArrayList pendingApps = new ArrayList();
	 		while (tcPendingAppsIterator.hasNext())
	 		{
	 			
	 			
	 			String key = (String)tcPendingAppsIterator.next();
	 			ArrayList tempPendingApps=(ArrayList)tcPendingApplications.get(key);
	 /*				tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
	 			if(tcApprovedApplications.isEmpty())
	 			{						
	 				ArrayList approvedApps=new ArrayList();
	 				tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
	 									
	 			}
	 			else{
	 				ArrayList approvedApps = new ArrayList();
	 				
	 				while (tcApprovedAppsIterator.hasNext())
	 				{			
	 					key = (String)tcApprovedAppsIterator.next();			
	 					ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
	 					for(int j=0; j<approvedAppsTemp.size();j++)
	 					{
	 						Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
	 						approvedApps.add(tcApplicationTemp);
	 						
	 					}						
	 					Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
	 				}
	 				tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
	 */
	 				for(int i=0; i<tempPendingApps.size(); i++)
	 				{
	 					Application tempPending = (Application)tempPendingApps.get(i);
	 					pendingApps.add(tempPending);
	 				}
	 			}
	 			ArrayList approvedApps=new ArrayList();
	 				tcApprovedAppsIterator=tcApprovedAppsKeys.iterator();
	 				if(tcApprovedApplications.isEmpty())
	 				{						
	 					approvedApps=new ArrayList();
	 					//tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);							
	 				}
	 				else{
	 					approvedApps = new ArrayList();
	 	
	 					while (tcApprovedAppsIterator.hasNext())
	 					{			
	 						String key = (String)tcApprovedAppsIterator.next();			
	 						ArrayList approvedAppsTemp=(ArrayList)tcApprovedApplications.get(key);
	 						for(int j=0; j<approvedAppsTemp.size();j++)
	 						{
	 							Application tcApplicationTemp=(Application)approvedAppsTemp.get(j);
	 							approvedApps.add(tcApplicationTemp);
	 			
	 						}						
	 						Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Size :" + tcDuplicateCriteriaObj.size());
	 					}
	 				}
	 			tcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
	 			
	 			
	 			
	 			for(int m=0; m< tcDuplicateCriteriaObjtemp.size(); m++)
	 			{
	 				DuplicateApplication tcDupApplication = (DuplicateApplication)tcDuplicateCriteriaObjtemp.get(m);
	 				tcDuplicateCriteriaObj.add(tcDupApplication);
	 			}

	 		//}	
	 		
	 		while (wcPendingAppsIterator.hasNext())
	 		{
	 			ArrayList wcDuplicateCriteriaObjtemp = new ArrayList();
	 			String key = (String)wcPendingAppsIterator.next();
	 			Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","key 1:" + key);
	 			Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","Entering wc pending interator..");
	 			pendingApps=(ArrayList)wcPendingApplications.get(key);
	 			Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","pendingApps size :" + pendingApps.size());
	 			wcApprovedAppsIterator=wcApprovedAppsKeys.iterator();
	 			if(wcApprovedApplications.isEmpty())
	 			{
	 				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","approved empty..");
	 				
	 				approvedApps=new ArrayList();
	 				wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
	 				
	 									
	 			}
	 			else{
	 				approvedApps = new ArrayList();
	 				
	 				Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","approved not empty..");					
	 				while (wcApprovedAppsIterator.hasNext())
	 				{						
	 					key= (String)wcApprovedAppsIterator.next();
	 					Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","key :" + key);
	 					Log.log(Log.DEBUG,"ApplicationProcessor","checkDuplicate","wcApprovedAppsIterator..");
	 					ArrayList approvedAppsTemp=(ArrayList)wcApprovedApplications.get(key);
	 					for(int i=0; i<approvedAppsTemp.size(); i++)
	 					{
	 						Application wcApplicationtemp = (Application)approvedAppsTemp.get(i);;							
	 						approvedApps.add(wcApplicationtemp);													
	 					}
	 					
	 				}
	 				wcDuplicateCriteriaObjtemp=checkDuplicateApplications(approvedApps,pendingApps);
	 			}
	 			
	 			for(int k=0; k< wcDuplicateCriteriaObjtemp.size(); k++)
	 			{
	 				DuplicateApplication dupApplication = (DuplicateApplication)wcDuplicateCriteriaObjtemp.get(k);
	 				wcDuplicateCriteriaObj.add(dupApplication);									
	 			}

	 		}
	 		
	 		duplicateCriteriaObj.add(tcDuplicateCriteriaObj);
	 		duplicateCriteriaObj.add(wcDuplicateCriteriaObj);			
	 			
	 	}


	    appDAO=null;
	    approvedPendingApplications=null;

	 	tcApprovedApplications=null;
	 	wcApprovedApplications=null;
	 	
	 	tcPendingApplications=null;
	 	wcPendingApplications=null;

	 	tcApprovedAppsKeys=null;
	 	wcApprovedAppsKeys=null;
	 	
	 	tcPendingAppsKeys=null;
	 	wcPendingAppsKeys=null;

	 	tcApprovedAppsIterator=null;
	 	wcApprovedAppsIterator=null;
	 	
	 	tcPendingAppsIterator	= null;
	 	wcPendingAppsIterator	= null;
	 	
	 	Log.log(Log.INFO,"ApplicationProcessor","checkDuplicate","Exited");
	 	
	 	return duplicateCriteriaObj;
	 }

	 public Application getApplicationsForNew(String loan_type, String tempAppRefNo) throws DatabaseException,NoApplicationFoundException
	 {
		 //System.out.println("getApplicationsForApprovalPath Entered 1010101010101010101010101010101010101010");
		ApplicationDAO applicationDAO = new ApplicationDAO();
		Application Applicationnew=applicationDAO.viewApplicationsDetails(loan_type,tempAppRefNo);
		 return Applicationnew;
	 }
	 public DuplicateApplication getApplicationsForNew1(String loan_type, String tempAppRefNo) throws DatabaseException,NoApplicationFoundException
	 {
		 //System.out.println("12 12 12 12 12 1212 12 1212 12 1212 12 1212 12 1212 12 1212 12 12");
		ApplicationDAO applicationDAO = new ApplicationDAO();
		DuplicateApplication DupApplicationnew=applicationDAO.viewApplicationsDetailsDupicate(loan_type,tempAppRefNo);
		 return DupApplicationnew;
	 }
}

